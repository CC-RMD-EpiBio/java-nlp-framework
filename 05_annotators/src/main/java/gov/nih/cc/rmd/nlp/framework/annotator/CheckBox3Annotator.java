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
 * CheckBox3 turns lines that have both positive and negative asserted values into slotValues with checkbox3 as the id
 *   
 * @author  Guy Divita 
 * @created August 25, 2014
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.Line;
import gov.va.chir.model.SlotValue;
import gov.va.vinci.model.AssertedEvidence;
import gov.va.vinci.model.NegationEvidence;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class CheckBox3Annotator extends JCasAnnotator_ImplBase {
  
  @Override
  // -----------------------------------------
  /**
   * process finds check boxes from lines of documents
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
    this.performanceMeter.startCounter();
    
    // Loop thru lines
    List<Annotation> lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID);
    
    if ( lines != null ) {
    
      for ( Annotation line : lines ) {
        
        if ( line != null  ) {
         
      //    List<Annotation> positiveAnnotations = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, AssertedEvidence.typeIndexID, line.getBegin(), line.getEnd(), true );
         //  List<Annotation> negativeAnnotations = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, NegationEvidence.typeIndexID, line.getBegin(), line.getEnd(), true );
         
          String lineTxt = line.getCoveredText();
          if ( lineTxt.contains("[") && lineTxt.contains("]")   )
             createCheckBox( pJCas, line );
                
        } // end if the line isn't null
        
        } //end loop thru lines
      
    } //end if there are  lines
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
     // throw new AnalysisEngineProcessException();
    }
  } // end Method process
      
      
  // =======================================================
  /**
   * createSlotValue a checkbox line 
   * 
   * @param pJCas
   * @param pLine
   * @param pQuestion
   * @param pAnswer
   * @return SlotValue
   */
  // =======================================================
  private SlotValue createCheckBox(JCas pJCas, Annotation pLine ) {
 
    // -------------------
    // create the slotValue
     SlotValue slotValue = new SlotValue(pJCas);
     slotValue.setBegin(pLine.getBegin());
     slotValue.setEnd( pLine.getEnd());
     slotValue.setId("CheckBox3");
    
     slotValue.addToIndexes();
    
     
     return slotValue;
    
  } // End Method createSlotValue() ======================
  



  
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
     
      this.performanceMeter = new ProfilePerformanceMeter( args, this.getClass().getSimpleName() );
      
      this.blankAnswerpattern =   Pattern.compile("_{2,}");
      this.answerPattern =  Pattern.compile("_+\\w+_+");

      
    } catch (Exception e ) {
      e.printStackTrace();
      
      System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
    

    
    
    initialize();
  } // end Method initialize() ---------

  //----------------------------------
    /**
     * initialize loads in the resources needed for slotValues. Currently, this involves
     * a list of known slot names that might show up.
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize() throws ResourceInitializationException {
      
    this.slotValueAnnotator = new SlotValueAnnotator();
    
  
  } // end Method initialize() --------------
  

 
  // -----------------------------------------
  // class Variables
  // -----------------------------------------
    SlotValueAnnotator slotValueAnnotator = null;
  
    private ProfilePerformanceMeter performanceMeter = null;
    private Pattern blankAnswerpattern =  null; 
    private Pattern answerPattern =  null; 


  
} // end Class SlotValue ---------------------
