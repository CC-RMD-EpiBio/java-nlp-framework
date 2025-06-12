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
 * Sentence Section Repair alters sentences, to take
 * out content headings from parts of sentences
 * so the two are mutually exclusive.  
 *
 * @author  Guy Divita 
 * @created Aug 15, 2018
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.Sections;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Sentence;



public class SentenceSectionRepairAnnotator extends JCasAnnotator_ImplBase {
 
 
  public static final String annotatorName = SentenceSectionRepairAnnotator.class.getSimpleName();
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    
    this.performanceMeter.startCounter();
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " Start " + this.getClass().getSimpleName());
    
   
    List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID);
    
    if ( sentences != null && !sentences.isEmpty() ) 
      
      for ( Annotation sentence : sentences )
        processSentence( pJCas, sentence );
    
   
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " End " + this.getClass().getSimpleName());
    this.performanceMeter.stopCounter();     
    
  } // End Method process() ------------------
  
 

// =================================================
  /**
   * processSentence looks for sentences with tabs in them
   * 
   * @param pJCas
   * @param pSentence
  */
  // =================================================
 private void processSentence(JCas pJCas, Annotation pSentence) {
   
 
  if ( pSentence != null ) {
     List<Annotation> contentHeadings = UIMAUtil.getAnnotationsBySpan(pJCas, ContentHeading.typeIndexID, pSentence.getBegin(), pSentence.getEnd() );
    
     if ( contentHeadings != null && !contentHeadings.isEmpty())
       repairSentence( pJCas, pSentence, contentHeadings.get(0));
     
   }
  } // end Method processSentence() ------------------



  // =================================================
/**
 * repairSentence removes the content heading part of the sentence 
 * from the sentence - it's covered in the contentHeading. 
 * 
 * @param pJCas
 * @param pSentence
 * @param pContentHeading
*/
// =================================================
private final void repairSentence(JCas pJCas, Annotation pSentence, Annotation pContentHeading) {
 
  if ( pSentence.getBegin() == pContentHeading.getBegin()) 
    if ( pSentence.getEnd() == pContentHeading.getEnd())
      pSentence.removeFromIndexes();  //< ---- are the same, take the sentence out
    else if ( pSentence.getEnd() > pContentHeading.getEnd()) { 
      pSentence.setBegin( pContentHeading.getEnd() + 1);
      ((Sentence) pSentence).setProcessMe( true);
      ((Sentence) pSentence).setDisplayString(  U.normalize(pSentence.getCoveredText()));
    } 
 
} // end Method repairSentence() ------------------



  
/**
  * destroy
  * 
  **/
 public void destroy() {
    if ( this.performanceMeter != null )
      this.performanceMeter.writeProfile( this.getClass().getSimpleName());
 } // end Method destroy() ----------

 
 //----------------------------------
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
     args                 = (String[]) aContext.getConfigParameterValue("args");  
     
     initialize( args );
     
   } catch (Exception e ) {
     e.printStackTrace();
     String msg = "Issue initializing " + annotatorName + " " + e.toString() ;
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", msg);
     throw new ResourceInitializationException();
   }
   
 } // end Method initialize() --------
 
//----------------------------------
/**
 * initialize loads in the resources needed
 * 
 * @param pArgs
 * 
 **/
// ----------------------------------
public void initialize(String[] pArgs) throws ResourceInitializationException {
  
 
  try {
    
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
   
    
  } catch (Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", "Issue initializing the " + annotatorName + " " + e.toString() );
    throw new ResourceInitializationException();
  }
  
} // end Method Initialize() -----------


 // ---------------------------
 // Global Variables
 // ---------------------------

 private int annotationCounter = 0;
 private ProfilePerformanceMeter              performanceMeter = null;

} // end Class TableSectionZoneAnnotator() ---------------
