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
 * Gene Observations Annotator will find gene observations 
 * 
 * each gene observation will have the following attributes
 * 
 *  the gene               (terminology lookup)
 *  the gene Variant,      (regex/Enumerations)
 *  the gene variant value percent  (% value)
 *  medication(s) of interest ( terminology lookup)
 *  
 *  These correspond to columns in gene report tables
 *
 *  The format for the tables to extract from include
 *  
 *   For molpath-foundation-one Theraputic Implications table with the following column headings
 *   
 *   Genomic Alterations Detected | FDA Approved Therapies | FDA Approved Therapies | Potential Clinical Trials
 *   
 *   PIK3CA  <-- gene               none                     Everolimus                Yes
 *   H1047R  <-- gene variant                                Temsirolimus         
 *   
 *   
 *   In guardant360 kinds of reports
 *   
 *   Summary of Alterations & associated Treatment Options
 *   
 *   Alteration __   %cfDNA      cfDNA Amplification   FDA Approved in Indication  Available for Use in Other Indications  Clinical Drug Trials
 *  
 *   EGFR       L858R  29.5         _                          Afatinib                    None                             Trials Available
 *              AMP                 ++                         None                    Afatnib
 *                                                                                     Cetuximab
 *  
 *  
 *   Variants of unknown significance
 *   CDk1                  PALB2         TNFAIP3
 *   amplification         T397S         T647P
 *  
 *  
 *  in the mayo clinic 
 *     gene: xxxx
 *     DNA change: 
 *     Amino Acid Change: 
 *     Classification  mutation
 *     
 *     Variant of uncertain cignificance
 *  
 *  
 * @author  Guy Divita 
 * @created Jun 1, 2018
 *
 * 
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.geneObservations;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.GeneVariantObservation;
import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.framework.TableRow;
import gov.nih.cc.rmd.framework.TestMethod;
import gov.nih.cc.rmd.framework.TestName;
import gov.nih.cc.rmd.framework.TestValue;
import gov.nih.cc.rmd.nlp.framework.annotator.loretta.SectionMetaInfo;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Group;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Sentence;



public class GeneVariantObservationAndMethodAnnotator extends JCasAnnotator_ImplBase  {
 

  private SectionMetaInfo sectionInfo;
  // -----------------------------------------
  /**
   * process retrieves lines of the document, labels those that are questions
   * as QuestionAndAnswer elements.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
    this.performanceMeter.startCounter();
    
   
    // Iterate through the geneObservation sections
    
    List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, SectionZone.typeIndexID );
    
    if (sections != null && !sections.isEmpty()) {
      
      for ( Annotation section : sections) {
        String sectionType = ((SectionZone) section ).getSectionTypes();
        
        if ( sectionType != null && sectionType.toLowerCase().contains("genesectionname")) {
          processGeneSection( pJCas, section );
          findGeneValues ( pJCas, section);
          
        }
      }
    } else {
      gov.va.chir.model.DocumentHeader documentHeader = VUIMAUtil.getDocumentHeader(pJCas);
      String sectionName = documentHeader.getSectionType();
      if ( this.sectionInfo.isValidSectionFor(sectionName, "GeneObservation")) {
     
        Annotation docAnnotation = createDocAnnotation(pJCas );
        if ( docAnnotation != null  ) {
          processGeneSection( pJCas, docAnnotation );
          findGeneValues ( pJCas, docAnnotation);
          docAnnotation.removeFromIndexes();
        }
    }
    }
    
   
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  } // end Method process() ----------------
   
  
  // =================================================
  /**
   * createDocAnnotation creates an annotation that has the span
   * of the whole document
   * 
   * @param pJCas
   * @return Annotation
  */
  // =================================================
 private final Annotation createDocAnnotation(JCas pJCas) {
    
    Group statement = null;
    
    String docText = pJCas.getDocumentText();
    
    if ( docText == null || docText.length() == 0)
      return null;
   
    statement = new Group(pJCas);
    
    statement.setBegin(0);
    statement.setEnd( docText.length()  );
    statement.addToIndexes();
   
    return statement;
  } // end Method createDocAnnotation() -------------


  // =================================================
  /**
   * findGeneValues looks for elements that look like 2 + 10 %
   * in tab'd gene rows 
   * @param pJCas
   * @param pSection
  */
  // =================================================
  private void findGeneValues(JCas pJCas, Annotation pSection) {
   
    List<Annotation> tableRows = UIMAUtil.getAnnotationsBySpan(pJCas,  TableRow.typeIndexID,  pSection.getBegin(), pSection.getEnd()) ;
    
    if ( tableRows != null && !tableRows.isEmpty()) 
      for ( Annotation tableRow : tableRows ) 
        findGeneValuesInRow( pJCas, tableRow);
        
  } // end Method findGeneValues() -------------------
    
  // =================================================
  /**
   * findGeneValuesInRow
   * 
   * @param pJCas
   * @param pTableRow
  */
  // =================================================
private void findGeneValuesInRow(JCas pJCas, Annotation pTableRow) {
    
  List<Annotation> sentences = UIMAUtil.getAnnotationsBySpan(pJCas,  Sentence.typeIndexID,  pTableRow.getBegin(), pTableRow.getEnd()) ;
  
  if ( sentences != null && !sentences.isEmpty()) 
    for ( Annotation sentence : sentences ) 
      findGeneValuesInSentence( pJCas, sentence);
      
} // end Method findGeneValuesInRow() ----------------
  
// =================================================
/**
 * findGeneValuesInSentence
 * 
 * @param pJCas
 * @param pSentence
*/
// =================================================
private void findGeneValuesInSentence(JCas pJCas, Annotation pSentence) {
  
  
  //System.err.println("Looking at " + pSentence.getCoveredText());
  Matcher m = this.geneValuePattern.matcher(pSentence.getCoveredText());
  
  if ( m.matches())
    createTestValue(pJCas, pSentence);
    
} // end Method findGeneValuesInRow() ----------------

  


  // =================================================
  /**
   * processGeneSection 
   * 
   * @param pSection
  */
  // =================================================
  private final void processGeneSection(JCas pJCas, Annotation pSection) {
   
  
   List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas,  LexicalElement.typeIndexID, pSection.getBegin(), pSection.getEnd() );
   
   if ( terms != null ) {
     for ( Annotation term  : terms ) {
       String termType = ((LexicalElement) term).getSemanticTypes() ;
       if ( termType != null ) {
         if (      termType.contains("GeneSequencingMethod" ))  createGeneSequencingMethod(   pJCas, (LexicalElement) term );
         else if ( termType.contains("GeneVariantObservation"))
           createGeneVariantObservation( pJCas, (LexicalElement) term ); 
         else if ( termType.contains("TestName" ))              createTestName(               pJCas, (LexicalElement) term );
           
       }
       
     }
   }
   
   
   
    
  } // end Method findAll() ------------------------


// =================================================
/**
 * createGeneSequencingMethod 
 * 
 * @param pJCas
 * @param pTerm
*/
// =================================================
private final void createGeneSequencingMethod(JCas pJCas, LexicalElement pTerm) {
  
    TestMethod statement = new TestMethod( pJCas);
    statement.setBegin(  pTerm.getBegin());
    statement.setEnd(   pTerm.getEnd());
    statement.setId( "GeneObservationsAnnotator_" + this.annotationCtr);
    statement.setCategories( pTerm.getSemanticTypes() );
    try { statement.setCuis( UIMAUtil.stringArrayToString(pTerm.getEuis())); } catch (Exception e) {};
    statement.addToIndexes();
 
} // end Method createGeneSequencingMethod() -------


//=================================================
/**
* createGeneVariantObservation 
* 
* @param pJCas
* @param pTerm
*/
//=================================================
private final void createGeneVariantObservation(JCas pJCas, LexicalElement pTerm) {

  GeneVariantObservation statement = new GeneVariantObservation( pJCas);
  statement.setBegin(  pTerm.getBegin());
  statement.setEnd(   pTerm.getEnd());
  statement.setId( "GeneVariantObservationAndMethodAnnotator_" + this.annotationCtr);
  statement.setCategories( pTerm.getSemanticTypes() );
  try { statement.setCuis( UIMAUtil.stringArrayToString(pTerm.getEuis())); } catch (Exception e) {};
  statement.addToIndexes();

} // end Method createGeneVariantObservation() ----


//=================================================
/**
* createTestName 
* 
* @param pJCas
* @param pTerm
*/
//=================================================
private final void createTestName(JCas pJCas, LexicalElement pTerm) {

  TestName statement = new TestName( pJCas);
  statement.setBegin(  pTerm.getBegin());
  statement.setEnd(   pTerm.getEnd());
  statement.setId( "GeneObservationsAnnotator_" + this.annotationCtr);
  statement.setCategories( pTerm.getSemanticTypes() );
  try { statement.setCuis( UIMAUtil.stringArrayToString(pTerm.getEuis())); } catch (Exception e) {};
  statement.addToIndexes();

} // end Method createTestName() ------------------


//=================================================
/**
* createTestValue 
* 
* @param pJCas
* @param pValue
*/
//=================================================
private final void createTestValue(JCas pJCas, Annotation pValue) {

TestValue statement = new TestValue( pJCas);
statement.setBegin(  pValue.getBegin());
statement.setEnd(   pValue.getEnd());
statement.setId( "GeneObservationsAnnotator_" + this.annotationCtr);


statement.addToIndexes();

} // end Method createTestName() ------------------


//----------------------------------
/**
 * destroy
* 
 **/
// ----------------------------------
public void destroy() {
  this.performanceMeter.writeProfile( this.getClass().getSimpleName());
}


  //----------------------------------
  /**
   *  initialize    This is the standard uima way to pass parameters to an annotator.
   *                It is cumbersome.  It requires creating a config file with params
   *                in it, making it difficult to dynamically pass in parameters. 
   *                
   *                This method merges the uima way and keeping the ability to dynamically
   *                pass parameters into the class via - putting all parameters in a string
   *                array called "args" with each row containing a --key=value format.
   *                This way, arguments could be directly passed from command line,
   *                or read from a config file, or dynamically added to that string
   *                passed in. 
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   * 
   * @param aContext
   * @throws ResourceInitializationException
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
     
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  

        initialize(args);
        
      } catch (Exception e ) {
        String msg = "Issue in initializing class " + this.getClass().getName() + " " + e.toString() ;
        GLog.println(GLog.ERROR_LEVEL, msg);     // <------ use your own logging here
        throw new ResourceInitializationException();
      }
      
  
  } // end Method initialize() -------
  
  //----------------------------------
  /**
   * initialize initializes the class.  Parameters are passed in via a String
   *                array  with each row containing a --key=value format.
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    this.proceduresParameter = U.getOption(pArgs,  "--termplateParam=", "aDefaultValue"); 
    
    try {
    // ------------------------
    // read in the section/type resource
    this.sectionInfo = new SectionMetaInfo( );
      
    } catch (Exception e) {
      e.printStackTrace();
      throw new ResourceInitializationException();
    }
  } // end Method initialize() -------
  
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  private String  proceduresParameter = null;
  Pattern geneValuePattern = Pattern.compile("\\d{1,2}\\s{0,2}\\+\\s{0,2}\\d{1,3}\\s{0,2}\\%");//. represents single character.
  
  
} // end Class ProceduresAnnotator() ---------------

