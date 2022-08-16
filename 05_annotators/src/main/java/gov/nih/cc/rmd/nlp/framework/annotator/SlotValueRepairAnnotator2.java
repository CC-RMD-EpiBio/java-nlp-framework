// =================================================
/**
 * SlotValueRepairAnnotator2 filters out slot:values where the slot has a period 
 * in them.  The part before the period in the line belongs to a sentence, not
 * the slot value.
 *
 * @author  Guy Divita 
 * @created June 23, 2016
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.WordToken;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class SlotValueRepairAnnotator2 extends JCasAnnotator_ImplBase {
  


  @Override
  // -----------------------------------------
  /**
   * process finds slotValues in the whole document
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
    this.performanceMeter.startCounter();
    List<Annotation> slotsAndValues = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID);
    

    if ( slotsAndValues != null && !slotsAndValues.isEmpty() ) {
      for ( Annotation slotAndValue : slotsAndValues )
        process( pJCas, (SlotValue) slotAndValue  );
    }
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
     // throw new AnalysisEngineProcessException();
    }
  } // end Method process
      
  //----------------------------------
  /**
   * process
   * 
   * @param pJCas
   * @param pSlotAndValue
   * @param pIteration   ( don't do more than 1 iterations
   * 
   **/
  // ----------------------------------  
  private void process(JCas pJCas, SlotValue pSlotAndValue ) {
  
    
   ContentHeading slot = pSlotAndValue.getHeading();
   
   if ( slot != null ) {
   int endSentence = 0;
   
   List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas, WordToken.typeIndexID, slot.getBegin(), slot.getEnd());
   if ( tokens != null && !tokens.isEmpty())
   for ( Annotation token : tokens ) {
     String tokenString = token.getCoveredText();
     if ( ((WordToken)token).getSentenceBreak() && (tokenString.equals('.' )|| tokenString.equals(';') || tokenString.equals('-') )) {
       endSentence = token.getEnd();
       break;
     }
   }
     
  if ( endSentence != 0 ) {
    
    // fix the offsets for the slot value, the contentHeading
    if ( endSentence+1 < pSlotAndValue.getEnd() ) {
      pSlotAndValue.setBegin( endSentence+1);
      slot.setBegin( endSentence + 1);
    } else {
      // I should no longer get here - bug fixed.
     // System.err.println("Something went ary with " + slot.getCoveredText());
     // throw new RuntimeException();
    }
    
  }
   }
   
  } // End Method process() ======================
  



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
  * initialize loads in the resources needed for slotValues. Currently, this involves
  * a list of known slot names that might show up.
  *
  * @param aContext
  * 
  **/
 // ----------------------------------
 public void initialize(UimaContext aContext) throws ResourceInitializationException {
   
  
   String[] args = null;
   try {
     args                 = (String[]) aContext.getConfigParameterValue("args");  
     
   } catch (Exception e ) {
    
   }
   
   initialize( args);
   
 } // end Method initialize() ---------

 //----------------------------------
 /**
  * initialize 
  *
  * @param pArgs
  * 
  **/
 // ----------------------------------
 public void initialize(String pArgs[]) throws ResourceInitializationException {
   
   this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
   

} // end Method initialize() --------------
    
 
  // -----------------------------------------
  // class Variables
  // -----------------------------------------

//---------------------------------------
//Global Variables
//---------------------------------------
  private ProfilePerformanceMeter  performanceMeter = null;
  
  
} // end Class SlotValueRepairAnnotator ---------------------
