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
 * AssertionEvidenceAnnotator creates negative, asserted, historical, conditional, and subject/other evidence annotations
 * based on terms found from the assertion dictionaries.  The terms in the dictionary are based on conTEXT.
 * 
 * These pieces of evidence (annotations) are used downstream with an assertion annotator that sets 
 * the assertion attributes of entities based on the proximity to the evidence found within a sentence window.
 *
 *
 * @author  Guy Divita 
 * @created October 6, 2015
 *
 **   *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.ClinicalStatusEvidence;
import gov.nih.cc.rmd.framework.model.Date;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.QuotedUtterance;
import gov.va.chir.model.SlotValue;
import gov.va.vinci.model.AssertedEvidence;
import gov.va.vinci.model.AssertionEvidence;
import gov.va.vinci.model.AttributionIsPatientEvidence;
import gov.va.vinci.model.ConditionalEvidence;
import gov.va.vinci.model.FamilyHistoryEvidence;
import gov.va.vinci.model.HistoricalEvidence;
import gov.va.vinci.model.NegationEvidence;
import gov.va.vinci.model.NoEvidence;
import gov.va.vinci.model.ScopeBreakEvidence;
import gov.va.vinci.model.SubjectIsOtherEvidence;
import gov.va.vinci.model.SubjectIsPatientEvidence;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class AssertionEvidenceAnnotator extends JCasAnnotator_ImplBase {
 
  

  public static final String EvidenceLRAGRFiles = 
      
      "resources/vinciNLPFramework/assertion/assertedEvidence.lragr"    +  "|" +
       "resources/vinciNLPFramework/assertion/attributionEvidence.lragr"    +  "|" +
      "resources/vinciNLPFramework/assertion/conditionalEvidence.lragr" +  "|" +
      "resources/vinciNLPFramework/assertion/historicalEvidence.lragr"  +  "|" +
      "resources/vinciNLPFramework/assertion/negationEvidence.lragr"    +  "|" +
      "resources/vinciNLPFramework/assertion/subjectIsEvidence.lragr"   +  "|" +
      "resources/vinciNLPFramework/assertion/familyHistoryEvidence.lragr"   +  "|" +
      "resources/vinciNLPFramework/assertion/otherEvidence.lragr"   +  "|" +
      "resources/vinciNLPFramework/assertion/falsePositiveEvidence.lragr"  ;
      
  public static final String labels = "AssertedEvidence|ConditionalEvidence|HistoricalEvidence|NegationEvidence|SubjectIsPatientEvidence|SubjectIsOtherEvidence|FamilyHistoryEvidence";
      
// =======================================================
 /**
  * process
  * 
  * @param pJCas
  * @throws AnalysisEngineProcessException
  *
  */
 // =======================================================
   public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
     
     try {
     
     this.performanceMeter.startCounter();
    
       // --------------------------------------------------------------
       // create Negation, Assertion, Historical, Subject Annotations
       createAssertionEvidenceAnnotations( pJCas);  
       createHistoricalEvidenceFromDateEvidence ( pJCas);
       createAttributionToPatientFromQuotedEvidence( pJCas);
       
       // -----------------------------------------
       // Create assertion evidence from numbers within answers of questions
       createEvidenceInSlotValueAnswers( pJCas);
     
   
     
   
     } catch (Exception e) {
       e.printStackTrace();
       System.err.println("Issue with the assertion annotator " + e.toString());
     //  throw new AnalysisEngineProcessException();
     }
     this.performanceMeter.stopCounter();
     
   } // end Method process() ----------------
   
   // =======================================================
   /**
    * createEvidenceInSlotValueAnswers  - look in the dependent Content part
    *                                     of slot values, make positive or negative
    *                                     evidence from numberic only values
   
    * @param pJCas
    */
   // =======================================================
   private final void createEvidenceInSlotValueAnswers(JCas pJCas) {
    
     List<Annotation> slotValues = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID);
     
     if ( slotValues != null && !slotValues.isEmpty()) {
      for ( Annotation slotValue : slotValues ) {
        DependentContent    answer = ((SlotValue) slotValue).getDependentContent();
        
       if ( answer != null ) { 
         makeNumbersEvidence( pJCas, answer);
       }
      }
     }
     
   }  // End Method createEvidenceInSlotValueAnswers() ======================


   
   
  // =======================================================
  /**
   * createAssertionEvidenceAnnotations creates annotations for negationEvidence,
   * assertionEvidence, HistoricalEvidence, SubjectEvidence
   * 
   * @param pJCas
   */
  // =======================================================
  private final void createAssertionEvidenceAnnotations(JCas pJCas) {
    
    List<Annotation>            terms = UIMAUtil.getAnnotations(pJCas, LexicalElement.typeIndexID);
    
    if ( terms != null ) {
    for ( Annotation term: terms ) {
      String category = ((LexicalElement)term).getSemanticTypes();
      
      
      if ( category != null && category.contains("Evidence")) {
        
        if ( category.contains("ClinicalStatusEvidence"))                                     createEvidence( pJCas, term, new ClinicalStatusEvidence(pJCas));
           
        if      ( category.contains("NoEvidence"))                                            createEvidence( pJCas, term, new NoEvidence(pJCas));
        else if ( category.contains("ScopeBreakEvidence"))                                    createEvidence( pJCas, term, new ScopeBreakEvidence(pJCas));
        else if ( category.contains("ConditionalEvidence"))                                   createEvidence( pJCas, term, new ConditionalEvidence(pJCas));
        else if ( category.contains("NegationEvidence"))                                      createEvidence( pJCas, term, new NegationEvidence(pJCas));
        else if ( category.contains("AssertedEvidence"))                                      createEvidence( pJCas, term, new AssertedEvidence(pJCas));
        else if ( category.contains("FamilyHistoryEvidence"))                                 createEvidence( pJCas, term, new FamilyHistoryEvidence(pJCas));
        else if ( category.contains("HistoricalEvidence") && this.historyIsAsserted )         createEvidence( pJCas, term, new HistoricalEvidence(pJCas));
        else if ( category.contains("SubjectIsPatientEvidence"))                              createEvidence( pJCas, term, new SubjectIsPatientEvidence(pJCas));
        else if ( category.contains("SubjectIsOtherEvidence"))                                createEvidence( pJCas, term, new SubjectIsOtherEvidence(pJCas));
        else if ( category.contains("AttributionPatientEvidence"))                            createEvidence( pJCas, term, new AttributionIsPatientEvidence(pJCas));
        else if ( category.contains("OtherEvidence"))                                         createEvidence( pJCas, term, new ConditionalEvidence(pJCas));
       
        else if ( !category.contains("ClinicalStatusEvidence")) 
       ;//   System.err.println("Evidence found but not captured " + category + "|" + term.getCoveredText());
      } // end if this is an evidence term
    } // end loop through terms
    } // end if there are terms for this record 
   
  } // end Method createkAssertionEvidenceAnnotations() ====


  
  // =======================================================
  /**
   * createHistoricalEvidenceFromDateEvidence creates annotations for HistoricalEvidence 
   * from found dates in scope
   * 
   * @param pJCas
   */
  // =======================================================
  private final void createHistoricalEvidenceFromDateEvidence(JCas pJCas) {
    
    List<Annotation>            dates = UIMAUtil.getAnnotations(pJCas, Date.typeIndexID);
    
    if ( dates != null && !dates.isEmpty() ) 
      for ( Annotation term: dates ) 
         if ( this.historyIsAsserted )       
           createEvidence( pJCas, term, new HistoricalEvidence(pJCas));
     
    
   
   
  } // end Method createkAssertionEvidenceAnnotations() ====



  // =======================================================
  /**
   * createAttributionToPatientFromQuotedEvidence creates annotations for attributedToThePatient 
   * from found from quoted phrases
   * 
   * @param pJCas
   */
  // =======================================================
  private final void createAttributionToPatientFromQuotedEvidence(JCas pJCas) {
    
    List<Annotation>            quotedUtterances = UIMAUtil.getAnnotations(pJCas, QuotedUtterance.typeIndexID);
    
    if ( quotedUtterances != null && !quotedUtterances.isEmpty() ) 
      for ( Annotation quote: quotedUtterances ) 
          createEvidence( pJCas, quote, new AttributionIsPatientEvidence(pJCas));
     
    
   
  } // end Method createkAssertionEvidenceAnnotations() ====


  
  

  // =======================================================
/**
 * makeNumbersEvidence changes answer's assertion status
 * if the answer only contains non-negative numbers
 * 
 * @param pJCas
 * @param pAnswer
 */
// =======================================================
private final void makeNumbersEvidence(JCas pJCas, DependentContent pAnswer) {

  String assertionValue = "Negated";
  if ( pAnswer != null ) {
    String answer = null;
    try {
      answer = pAnswer.getCoveredText();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Why empty covered text ?" + e.toString());
      
    }
      
    if ( answer != null && !answer.isEmpty()) {
      
      if ( U.isNumber( answer )) {
         double val = Double.valueOf( answer );
         if ( val > 0.0 ) {
           assertionValue = "Asserted";
         } else {
           assertionValue = "Negated";
         }
         createEvidence(pJCas, pAnswer,  assertionValue);
      }
    }
  }
    
} // End Method makeNumbersEvidence() ======================

  // =======================================================
/**
 * createEvidence
 * 
 * @param pJCas
 * @param pAnswer
 * @param assertionValue
 */
// =======================================================
private final void createEvidence(JCas pJCas, DependentContent pAnswer, String assertionValue) {
   
  AssertionEvidence evidence = null;
  if ( assertionValue.contains("Asserted"))
    evidence = new AssertedEvidence(pJCas);
  else 
    evidence = new NegationEvidence(pJCas);
  
  evidence.setBegin(pAnswer.getBegin());
  evidence.setEnd(pAnswer.getEnd());
  evidence.setTriggerType("pre:post");
  evidence.addToIndexes();
   
} // End Method createEvidence() ======================


  // =======================================================
  /**
   * createEvidence creates annotations for the given class
   * around the span for term passed in.
   * 
   * @param pJCas
   * @param pTerm
   * @param pEvidence 
   * @return Annotation
   *
   */
  // =======================================================
  private final Annotation createEvidence(JCas pJCas, Annotation pTerm, AssertionEvidence pEvidence) {
  
    pEvidence.setBegin( pTerm.getBegin());
    pEvidence.setEnd( pTerm.getEnd());
    String triggerType = null;
    
    if ( pTerm.getClass().getName().contains("QuotedUtterance"))
      triggerType = "pre|post|pseudo";
    else if ( pTerm.getClass().getName().contains("Date"))
      triggerType = "pre|post";
    else {
      try {
        StringArray otherFeatures = ((LexicalElement)pTerm).getOtherFeatures();
        if ( otherFeatures != null && otherFeatures.size()  > 0 )
          triggerType = otherFeatures.get(0);
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue with this record " + ((LexicalElement)pTerm).toString() );
        System.exit(-1);
      }
    }
      
    if ( triggerType != null ) {
        pEvidence.setTriggerType(triggerType);
    } else {
        System.err.println("evidence issue: no triggure type for " + pTerm.getCoveredText());
        System.err.println("eui = " + ((LexicalElement)pTerm).getEuis() );
        Exception e = new Exception();
        e.printStackTrace();
        System.exit(1);
    }
    pEvidence.addToIndexes(pJCas);
    
    return pEvidence;
  
  } // end Method createEvidence() =========================
  
  
//----------------------------------
 /**
  * destroy
 * 
  **/
 // ----------------------------------
 public void destroy() {
   this.performanceMeter.writeProfile( this.getClass().getSimpleName());
 }
 
 
//-----------------------------------------
 /**
  * initialize loads in the resources.
  *   Put pipeline parameters in the args parameter
  *   to be retrieved here.
  * 
  *      Pass in on your args, an arg with the value: "--outputDir=/some/path/to/put/outputFiles"
  *
  *   This class is tooled up to have performance statisitcs get put into $outputDir/logs/Class.log  
  *
  * @param aContext
  *
  */
 // -----------------------------------------
  public void initialize(UimaContext aContext)  throws ResourceInitializationException {
   
    String args[] = null;
    
    try {
      args                  = (String[]) aContext.getConfigParameterValue("args");
     
     
     
    } catch (Exception e) {
    
    
    }
    initialize( args );
 
  } // end Method initialize() -----------
  
  
//-----------------------------------------
 /**
  * initialize loads in the resources.
  *   Put pipeline parameters in the args parameter
  *   to be retrieved here.
  * 
  *      Pass in on your args, an arg with the value: "--outputDir=/some/path/to/put/outputFiles"
  *
  *   This class is tooled up to have performance statisitcs get put into $outputDir/logs/Class.log  
  *
  * @param pArgs
  *
  */
 // -----------------------------------------
  public void initialize(String[] pArgs)  throws ResourceInitializationException {
   
    try {
    
      this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
      this.historyIsAsserted = Boolean.valueOf(U.getOption(pArgs, "--historyIsAsserted=", "true"));
    
     
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with getting the passed in arguments " + e.toString());
      throw new ResourceInitializationException();
    
    }
 
  } // end Method initialize() -----------
  
   // ---------------------------
   // Global Variables
   // ---------------------------
   ProfilePerformanceMeter       performanceMeter = null;
   private boolean              historyIsAsserted = false;


  
  
} // end Class ExampleAnnotator() ---------------
