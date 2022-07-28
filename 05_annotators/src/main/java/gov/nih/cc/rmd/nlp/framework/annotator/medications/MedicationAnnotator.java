/*******************************************************************************
 * ---------------------------------------------------------------------------
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                        Epidemiology and Biostatistics Branch 
 *                                         2019
 *    
 *  This work is licensed under the Creative Commons Attribution 4.0 International License. 
 *  
 *  This license allows you to use, share and  adapt for any purpose, provided:
 *     Provide attribution to the creators of this work within your work.
 *     Indicate if changes were made to this work.
 *     No claim to merchantability, implied warranty, or liability can be made.
 *     
 *   When attributing this code, please make reference to
 *    [citation/url here] .  
 *    
 *     In the absence of a specific paper or url listed above, reference http://clinicalcenter.nih.gov/rmd/eb/nlp
 *  
 *  To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 ******************************************************************************/
// =================================================
/**
 * MedicationAnnotator will mark pharmacologic substances
 * and organic chemical mentions
 * 
 * An open question - otc, medical devices? wound care products like gauze, 
 * 
 * The list of semantic types that are medications are:
 *
 * orch  T109  Organic Chemical
 * strd  T110  Steroid
 * opco  T115  Organophosphorus Compound
 * aapp  T116  Amino Acid, Peptide, or Protein
 * phsu  T121  Pharmacologic Substance
 * bacs  T123  Biologically Active Substance
 * enzy  T126  Enzyme
 * antb  T195  Antibiotic
 * clnd  T200  Clinical Drug
 * 
 * 
 * 
 *
 * @author Guy Divita
 * @created April 9 2018
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.medications;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.Medication;

import gov.va.chir.model.ClinicalStatement;
import gov.va.chir.model.CodedEntry;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

public class MedicationAnnotator extends JCasAnnotator_ImplBase {

  public static final String annotatorName = MedicationAnnotator.class.getSimpleName();

  // -----------------------------------------
  /**
   * process
   * relies on having concepts already marked via a concept mapping tool
   * like sophia, metaMap or cTAKES
   * 
   * picks out those that have medication semantic types
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
      this.performanceMeter.startCounter();
      List<Annotation> allConcepts = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.ClinicalStatement.typeIndexID);

      if (allConcepts != null) processClinicalStatements(pJCas, allConcepts);

      this.performanceMeter.stopCounter();

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue in Medical Annotator " + e.toString());
    }

  } // end Method process() ----------------

  // =======================================================
  /**
   * processClinicalStatements
   * 
   * @param pJCas
   * @param pClinicalStatements
   */
  // =======================================================
  private final void processClinicalStatements(JCas pJCas, List<Annotation> pClinicalStatements) {

    if (pClinicalStatements != null) 
      for (Annotation aConcept : pClinicalStatements)
        processClinicalStatement(pJCas, aConcept);

  } // End Method processConcepts() ======================

  // =================================================
  /**
   * processClinicalStatement
   * 
   * @param pJCas
   * @param pClinicalStatement
   */
  // =================================================
  private final void processClinicalStatement(JCas pJCas, Annotation pClinicalStatement) {

    StringArray semanticTypes = ((ClinicalStatement) pClinicalStatement).getSemanticTypez();
    String sectionName = ((ClinicalStatement) pClinicalStatement).getSectionName();
    
    if (semanticTypes != null && semanticTypes.size() > 0 ) 
      if ( !sectionShouldNotContainMedications( sectionName ))
        if (isMedication( semanticTypes ) ) 
          if ( isFromRXNorm( ( ClinicalStatement) pClinicalStatement ) ||
              isNotAmbiguous( pClinicalStatement ) )
            createMedication(pJCas, (ClinicalStatement) pClinicalStatement);
      
  } // end Method processClinicalStatement() ---------

  // =================================================
  /**
   * sectionShouldNotContainMedications returns true
   * if this section name should not contain medications
   * 
   * An example would be - address section,
   * 
   * 
   * @param pSectionName
   * @return boolean
  */
  // =================================================
  private boolean sectionShouldNotContainMedications(String pSectionName) {
    
    boolean returnVal = false;
    
    if ( pSectionName != null && !pSectionName.isEmpty())
      returnVal = this.nonMedicationSectionz.contains( pSectionName.toLowerCase());
    
    return returnVal;
  } // end sectionShouldNotContainMedications() ------

  // =================================================
  /**
   * isNotAmbiguous - is there only one interpretation of this clinical statement?
   * 
   * @param pClinicalStatement
   * @return boolean
  */
  // =================================================
  private final boolean isNotAmbiguous(Annotation pClinicalStatement) {
    boolean returnVal = false;
    
    FSArray codedEntries;
    if (pClinicalStatement != null ) {
      codedEntries = ((ClinicalStatement)pClinicalStatement).getCodedEntries();
      if (codedEntries != null && codedEntries.size() == 1)
        returnVal = true;
      else {
        returnVal = true;
        for ( int i = 0; i < codedEntries.size(); i++  ) {
          CodedEntry codedEntry = (CodedEntry) codedEntries.get(i);
          if ( !isMedication( codedEntry.getSemanticType()) ) {
            returnVal = false; 
            break;
          }
        }
      }
    }
    
    return returnVal;
  } // end Method isNotAmbiguous() -----------------

  // =================================================
  /**
   * isFromRXNorm  there are too many concepts with medication
   * semantic types - limit decision to if the medication
   * is in rxNORM 
   * 
   * @param pClinicalStatement
   * @return boolean 
  */
  // =================================================
   private final boolean isFromRXNorm(ClinicalStatement pClinicalStatement) {
     boolean returnVal = false;
     
     FSArray concepts = pClinicalStatement.getCodedEntries();
     
     if ( concepts != null && concepts.size() > 0 ) 
       for (int  i = 0; i < concepts.size();  i++) {
         CodedEntry codedEntry = (CodedEntry) concepts.get(i);
         if (  isFromRXNorm( codedEntry ) ) {
           returnVal = true;
           break;
         }
       }
      
     return returnVal;
  } // end Method isFromRxNorm() --------------------

// =================================================
  /**
   * isFromRXNorm  there are too many concepts with medication
   * semantic types - limit decision to if the medication
   * is in rxNORM 
   * 
   * @param pCodedEntry
   * @return boolean 
  */
  // =================================================
   private final boolean isFromRXNorm(CodedEntry pCodedEntry) {
     boolean returnVal = false;
     
     
     StringArray otherFeaturez = pCodedEntry.getOtherFeatures();
     String otherFeatures = null;
     String sab = null;
    if ( otherFeaturez != null && otherFeaturez.size() > 0 ) {
      otherFeatures = UIMAUtil.stringArrayToString(otherFeaturez);
      String[] otherFeatureVals = U.split(otherFeatures);
      try {
        sab = otherFeatureVals[6];
      } catch (Exception e) { 
     
        sab = otherFeatureVals[0];  // the ciitizen.terminology location 
      }
      if (sab.equals( "RXNORM"))
        returnVal = true;
      
    }
      
    return returnVal ;
  } // end Method isFromRxNorm() -----------------------------

   
   
  // =======================================================
  /**
   * isMedication looks at the semantic type to see if this
   * is a medication semantic type
   * 
   * @param pSemanticTypes
   * @return booleanvha
   */
  // =======================================================
  private final boolean isMedication( StringArray pSemanticTypes ) {
    boolean returnVal = false;
    
    for (int i = 0; i < pSemanticTypes.size(); i++ ) {
      String aSemanticType = pSemanticTypes.get(i);
      if ( this.medicationSemanticTypez.contains( aSemanticType)) {
        returnVal = true;
        break;
      }
    }
    
    return returnVal;

  } // End Method isPartOfSectionHeading() ======================

  // -----------------------------------------
  /**
   * createMedication creates a medication annotation
   * 
   * @param pJCas
   * @param pTerm
   * 
   * @return Evidence
   * 
   */
  // -----------------------------------------
  private final void createMedication(JCas pJCas, ClinicalStatement pTerm ) {

  
    Medication statement = new Medication( pJCas);
    statement.setBegin(pTerm.getBegin());
    statement.setEnd(pTerm.getEnd());
    statement.setId( "Medication_" + this.annotationCounter++);
   
    statement.setAssertionStatus(pTerm.getNegation_Status());
    statement.setConditionalStatus( pTerm.getConditional());
    statement.setSubjectStatus( pTerm.getSubject());
    statement.setHistoricalStatus( pTerm.getHistorical());
    statement.setSectionName( pTerm.getSectionName());
    statement.setSection( pTerm.getSection());
  
    
    //statement.setParent( pTerm);
   
    
    statement.setInProse( pTerm.getInProse() );
    
    List<CodedEntry> medicationConcepts = getRelevantCodedEntries( pTerm );
    String                         cuis = getCuis(                 medicationConcepts);
    String                 conceptNames = getConceptNames(         medicationConcepts);
    String                   categories = getCategories(           medicationConcepts);
    statement.setCuis( cuis );
    statement.setConceptNames( conceptNames);
    statement.setCategories( categories) ;
    
    // statement.setOtherInfo( otherInfo);
    // otherInfo
    // statement.setCodeSystem( pTerm.getCodeSystem());
    // statement.setCodeSystemName( pTerm.getCodeSystemName());
    // statement.setGroup( pTerm.getSemanticGroup());
    // add these to otherInfo
   
    
    statement.addToIndexes();

      

  } // end Method createMedication() ---

  

  // =================================================
  /**
   * getRelevantCodedEntries returns the codedEntries that
   * have the semantic types for medications
   * 
   * @param pClinicalStatement
   * @return List<CodedEntry>
  */
  // =================================================
  private final List<CodedEntry> getRelevantCodedEntries(ClinicalStatement pClinicalStatement) {
   
   List<CodedEntry> returnVal = null;
   
   FSArray codedEntriez = pClinicalStatement.getCodedEntries();
   if ( codedEntriez != null && codedEntriez.size() > 0 ) {
     for ( int i = 0; i < codedEntriez.size(); i++ ) {
       CodedEntry aCodedEntry = (CodedEntry) codedEntriez.get(i);
       StringArray semanticTypez = aCodedEntry.getSemanticType();
       if ( semanticTypez != null && semanticTypez.size() > 0) {
         if ( isMedication( semanticTypez) ) {
           if ( returnVal == null )returnVal = new ArrayList<CodedEntry>();
           returnVal.add( aCodedEntry);
         }
       }
     }   
   }
 
   return returnVal;
  } // end Method getRelevantCodedEntries() --------
  
//=================================================
/**
 * getCuis 
 * 
 * @param medicationConcepts
 * @return String  (colon separated cuis )
*/
// =================================================
private final String getCuis(List<CodedEntry> medicationConcepts) {
 String returnVal = null;

 if ( medicationConcepts != null && !medicationConcepts.isEmpty()) {
   ArrayList<String>  cuis = new  ArrayList<String>(medicationConcepts.size());
   for ( CodedEntry aConcept : medicationConcepts )
     cuis.add( aConcept.getCodeCode() );
 
   returnVal = U.list2String(cuis, ':');
 }
 return returnVal;
} // end Method getCuis() --------------------------

  
//=================================================
 /**
  * getCategories returns semantic type abbr's
  * 
  * @param medicationConcepts
  * @return String (colon separated semantic type abbrs
 */
 // =================================================
private final String getCategories(List<CodedEntry> medicationConcepts) {
   String returnVal = null;
   HashSet<String> uniqueSemanticTypes = new HashSet<String>(medicationConcepts.size() + 2);
   
   if ( medicationConcepts != null && !medicationConcepts.isEmpty()) {
     ArrayList<String>  cuis = new  ArrayList<String>(medicationConcepts.size());
     for ( CodedEntry aConcept : medicationConcepts ) {
       StringArray semanticTypez = aConcept.getSemanticType() ;
       if ( semanticTypez != null) {
         String[] semanticTypes = UIMAUtil.stringArrayToArrayOfString(semanticTypez);
         for ( String semanticType: semanticTypes)
           uniqueSemanticTypes.add( semanticType);
           
       }
     }
     String[] semanticTypeArray = new String[uniqueSemanticTypes.size()]  ;
     uniqueSemanticTypes.toArray(semanticTypeArray);
     returnVal = U.stringArray2String(semanticTypeArray, ":"); 
   }
   
   return returnVal;
    
 } // end Method getCategories() --------------------

 // =================================================
 /**
  * getConceptNames 
  * 
  * @param medicationConcepts
  * @return String (colon separated list)
 */
 // =================================================
private final String getConceptNames(List<CodedEntry> medicationConcepts) {
  String returnVal = null;

  if ( medicationConcepts != null && !medicationConcepts.isEmpty()) {
    ArrayList<String>  conceptNames = new  ArrayList<String>(medicationConcepts.size());
    for ( CodedEntry aConcept : medicationConcepts )
      conceptNames.add( aConcept.getDisplayName() );
  
    returnVal = U.list2String(conceptNames, ':');
  }
  return returnVal;
 } // end Method  getConceptNames() -----------------

 
  // ------------------------------------------------
  /**
   * destroy
   * 
   **/
  // ------------------------------------------------
  public void destroy() {
    try {
      this.performanceMeter.writeProfile(this.getClass().getSimpleName());
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, "Issue destroying Medication Annotator " + e.toString());

    }
  } // end Method Destroy() ---

  // ----------------------------------
  /**
   * initialize
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {

    String[] args = null;
    try {
      args = (String[]) aContext.getConfigParameterValue("args");

      initialize(args);

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, "Issue initializing AnnotomicalParts Annotator " + e.toString());
      throw new ResourceInitializationException();
    }

  } // end Method initialize() ----

  // ----------------------------------
  /**
   * initialize
   * 
   * @param aArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String pArgs[]) throws ResourceInitializationException {

    try {
      this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getSimpleName());
      String nonMedicationSectionsFile = U.getOption(pArgs,  "--NonMedicationSections=", "resources/com/ciitizen/framework/medications/nonMedicationSections.txt");
      
      
      loadMedicationSemanticTypez();
      loadNonMedicationSectionz( nonMedicationSectionsFile);
      
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, "Issue initializing Medication sections Annotator " + e.toString());
      throw new ResourceInitializationException();
    }

  } // end Method initialize() -----------------------

  // =================================================
  /**
   * loadMedicationSemanticTypez
   * 
  */
  // =================================================
   private void loadMedicationSemanticTypez() {
    
     this.medicationSemanticTypez = new HashSet<String>();
     for ( String aType : this.medicationSemanticTypes )
       this.medicationSemanticTypez.add( aType);
    
     
  } // End Method load MedicationSemanticTypez() ------

   
// =================================================
  /**
   * loadNonMedicationSectionz loads non medication sections
   * into a hash - with a lowercase key - 
   * 
   * @param pNonMedicationSectionsFile
   * @throws Exception 
   * 
  */
  // =================================================
   private final void loadNonMedicationSectionz(String pNonMedicationSectionsFile ) throws Exception {
    
     this.nonMedicationSectionz = new HashSet<String>();
     
     String[] sectionNames;
    try {
      sectionNames = U.readClassPathResourceIntoStringArray(pNonMedicationSectionsFile);
   
      for ( String aSection : sectionNames ) {
        if ( aSection  != null && !aSection.trim().isEmpty() && !aSection.startsWith("#"))
          this.nonMedicationSectionz.add( aSection.toLowerCase());
      }
    
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with loading Non Medication sections from " + pNonMedicationSectionsFile  + " " + e.getMessage(); 
      GLog.println(GLog.ERROR_LEVEL, msg);
      throw new Exception (msg);
    }
     
     for ( String aSection : sectionNames )
       this.nonMedicationSectionz.add( aSection );
    
     
  } // End Method load MedicationSemanticTypez() ------

  // ---------------------------
  // Global Variables
  // ---------------------------

  private int             annotationCounter = 0;
  private String[]         medicationSemanticTypes = { "orch", "strd", "opco", "aapp", "phsu", "bacs", "enzy", "antb", "clnd" };
  private HashSet<String>  medicationSemanticTypez = null; 
  private HashSet<String>  nonMedicationSectionz = null;
  ProfilePerformanceMeter performanceMeter  = null;
 

} // end Class ExampleAnnotator() ---------------
