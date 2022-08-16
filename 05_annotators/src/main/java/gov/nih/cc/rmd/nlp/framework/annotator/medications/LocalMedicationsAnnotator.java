// =================================================
/**
 * LocalMedications
 *
 * @author Guy Divita
 * @created April 10, 2018
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.medications;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ClinicalStatement;
import gov.va.chir.model.CodedEntry;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Section;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

public class LocalMedicationsAnnotator extends JCasAnnotator_ImplBase {

  public static final String annotatorName = LocalMedicationsAnnotator.class.getSimpleName();

  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.
   * This version labels lines of the document.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
      this.performanceMeter.startCounter();
      List<Annotation> allTerms = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.LexicalElement.typeIndexID);

      if (allTerms != null) processTerms(pJCas, allTerms);

      this.performanceMeter.stopCounter();

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue in AnatomicalParts Annotator " + e.toString());
    }

  } // end Method process() ----------------

  // =======================================================
  /**
   * processTerms. If the terms are cauti terms, creates cauti Concepts
   * from them.
   * 
   * @param pJCas
   * @param terms
   */
  // =======================================================
  private void processTerms(JCas pJCas, List<Annotation> terms) {

    String semanticTypes = null;
    if (terms != null) {

      // ---------------------------
      // Retrieve section and contentHeading annotations
      // ---------------------------
      List<Annotation> sectionAndContentHeadingAnnotations = VUIMAUtil.getSectionAndContentHeadingAnnotations(pJCas);

      // -----------------------------------------------------
      // Loop through each term
      // -----------------------------------------------------
      for (Annotation term : terms) {

        if ((semanticTypes = ((LexicalElement) term).getSemanticTypes()) != null) {
          if (semanticTypes.contains("Medication")) {
            // ------------------------------------------------------
            // if the lexical element is not part of a contentHeading of a section
            // create an anatomicalPart for it
            // ------------------------------------------------------
            if (!isPartOfSectionHeading(pJCas, sectionAndContentHeadingAnnotations, term)) {
              CodedEntry codedEntry = createCodedEntry(pJCas, (LexicalElement) term);
              addToClinicalStatement( pJCas, codedEntry );
             
            } // end if this is not in a section heading
          } // end if this term even has a valid semantic type
        } // end if there are semantic types
      } // end loop through terms
    } // end if there are any terms

  } // End Method processTerms() ======================

  // =================================================
  /**
   * addToClinicalStatement codedEntries need to be attached
   * to clinical statements, or they will be missed.
   * 
   * @param pJCas
   * @param pCodedEntry
  */
  // =================================================
  private final void addToClinicalStatement(JCas pJCas, CodedEntry pCodedEntry) {
   
    List<Annotation> clinicalStatements = UIMAUtil.getAnnotationsBySpan(pJCas, ClinicalStatement.typeIndexID, pCodedEntry.getBegin(), pCodedEntry.getEnd() );
    
    if ( clinicalStatements != null && !clinicalStatements.isEmpty()) {
      for ( Annotation statement : clinicalStatements ) {
        FSArray codedEntries = ((ClinicalStatement)statement).getCodedEntries();
        FSArray moreCodedEntries = new FSArray(pJCas,codedEntries.size()+1);
        for (int  i = 0; i < codedEntries.size(); i++ ) 
          moreCodedEntries.set(i, codedEntries.get(i));
        moreCodedEntries.set(codedEntries.size(),  pCodedEntry);
      ((ClinicalStatement)statement).setCodedEntries( moreCodedEntries);
      codedEntries.removeFromIndexes();
      }
    } else {
      // create a clinical statement for this codedEntry  [TBD]
    }
      
  } // End Method addToClinicalStatement() -----------------

  // =======================================================
  /**
   * isPartOfSectionHeading looks for content Heading and
   * section to see if this term is within the content heading
   * of a known section.
   * 
   * @param pJCas
   * @param pTerm
   * @return boolean
   */
  // =======================================================
  private boolean isPartOfSectionHeading(JCas pJCas, List<Annotation> pSectionAnnotations, Annotation pTerm) {
    boolean returnVal = false;

    boolean contentHeadingSeen = false;
    boolean sectionSeen = false;
    List<Annotation> overlappingAnnotations = UIMAUtil.fuzzyFindAnnotationsBySpan(pSectionAnnotations, pTerm.getBegin(),
        pTerm.getEnd());

    if (overlappingAnnotations != null && overlappingAnnotations.size() > 1) {

      for (Annotation anAnnotation : overlappingAnnotations) {
        try {
          int typeId = anAnnotation.getTypeIndexID();
          String whereAmI = anAnnotation.getCoveredText();
          if (whereAmI != null && whereAmI.length() > 20) whereAmI = whereAmI.substring(0, 20);

          if (typeId == ContentHeading.typeIndexID) contentHeadingSeen = true;
          if (typeId == Section.typeIndexID) sectionSeen = true;

          // System.err.println("where am i = " + whereAmI + "|" + typeId + "|" + ContentHeading.typeIndexID + "|" +
          // Section.typeIndexID + "|" + anAnnotation.getClass().getName());
          if (contentHeadingSeen && sectionSeen) break;
        } catch (Exception e) {

        }

      } // end loop thru the overlappingAnnotations

      if (contentHeadingSeen && sectionSeen) returnVal = true;
    } // end if there are overlapping annotations

    return returnVal;

  } // End Method isPartOfSectionHeading() ======================

  // -----------------------------------------
  /**
   * createCodedEntry makes a coded entry that
   * will be picked up with the medicationAnnotator
   * downstream.
   * 
   * @param pJCas
   * @param uimaLabelClass
   * @param pTerm
   * 
   * @return Evidence
   * 
   */
  // -----------------------------------------
  private CodedEntry createCodedEntry(JCas pJCas, LexicalElement pTerm) {

    CodedEntry statement = new CodedEntry(pJCas);

    statement.setBegin(pTerm.getBegin());
    statement.setEnd(pTerm.getEnd());
    statement.setId("localMedicationAnnotator_" + this.annotationCounter++);
    String codeCode = pTerm.getEuis(0);
    String conceptName = pTerm.getCitationForm();
    String semanticType = pTerm.getSemanticTypes();
    String sab = "local";
    StringArray extraStuffs = pTerm.getOtherFeatures();
    String extraStuff = null;
    if (extraStuffs != null && extraStuffs.size() > 0) 
      extraStuff = extraStuffs.get(0);

    if (extraStuff != null) {
      String[] cols = U.split(extraStuff);
      sab = cols[0];
      semanticType = cols[1];
    }

    String otherFeatures[] = new String[1];
    otherFeatures[0] = "||||||" + sab + "||||||||||||";
    StringArray otherFeaturez = UIMAUtil.string2StringArray(pJCas, otherFeatures);
    statement.setOtherFeatures(otherFeaturez);
    statement.setCodeCode(codeCode);
    statement.setCodeSystemName("LocalMedicationsLexicon");
    StringArray semanticTypez = new StringArray(pJCas, 1);
    semanticTypez.set(0, semanticType);
    statement.setSemanticType(semanticTypez);
    statement.setDisplayName(conceptName);
    statement.setNegation_Status(pTerm.getNegation_Status());
    statement.setSectionName(  pTerm.getSectionName());
    statement.setSection( pTerm.getSection());
    

    statement.setParent(pTerm);
    statement.addToIndexes(pJCas);
    
    return statement;

  } // end Method createCodedEntry() ---

  // ----------------------------------
  /**
   * destroy
   * 
   **/
  // ----------------------------------
  public void destroy() {
    try {
      this.performanceMeter.writeProfile(this.getClass().getSimpleName());
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, "Issue destroying AnnotomicalParts Annotator " + e.toString());

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

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, "Issue initializing LocalMedications Annotator " + e.toString());
      throw new ResourceInitializationException();
    }

  } // end initialize()

  // ---------------------------
  // Global Variables
  // ---------------------------

  private int             annotationCounter = 0;
  ProfilePerformanceMeter performanceMeter  = null;

} // end Class ExampleAnnotator() ---------------
