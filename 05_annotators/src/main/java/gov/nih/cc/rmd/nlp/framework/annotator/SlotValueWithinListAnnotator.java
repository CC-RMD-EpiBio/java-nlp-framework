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
 * SlotValueWithinListAnnotator captures slot values within list elements.
 *
 * @author  Guy Divita 
 * @created Feb 17, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ListElement;
import gov.va.chir.model.Token;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class SlotValueWithinListAnnotator extends JCasAnnotator_ImplBase {
  
  @Override
  // -----------------------------------------
  /**
   * process retrieves the listElements and processes each 
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    
    try {
    this.performanceMeter.startCounter();
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " Start " + this.getClass().getSimpleName());
    
    // Retrieve list Elements:
    
    List<Annotation> listElements = UIMAUtil.getAnnotations(pJCas, ListElement.typeIndexID);
    
    if ( listElements != null && listElements.size() > 0 ) {
      
      for ( Annotation listElement: listElements ) {
        
        process( pJCas, listElement);
      }
      
    }
    
  
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
    // throw new AnalysisEngineProcessException();
    }
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " End " + this.getClass().getSimpleName());
    this.performanceMeter.stopCounter();

  } // end Method process() ------------------
  

  // -----------------------------------------
  /**
   * process processes a listElement 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  private void process(JCas pJCas, Annotation pListElement) throws AnalysisEngineProcessException {
  
  
     FSArray elementTokenz = ((ListElement)pListElement).getTokens();
    @SuppressWarnings("unchecked")
    List<Annotation>elementTokenzz = UIMAUtil.fSArray2List(pJCas, elementTokenz);
    List<Token> elementTokens = SlotValueAnnotator.convertAnnotationsToTokens( elementTokenzz);
     
     if ( elementTokens != null && !elementTokens.isEmpty() ) {
      
         int delimiterIndex = this.slotValueAnnotator.findDelimiter(pJCas,  elementTokens );
         
         // make the slot tokens
         if ( delimiterIndex > -1) {
           List<Annotation>slotTokens = new ArrayList<Annotation>(delimiterIndex);
           List<Annotation>delimiterToken = new ArrayList<Annotation>(1);
           List<Annotation>valueTokens = new ArrayList<Annotation>( elementTokens.size() - delimiterIndex );
           
           // make the slot tokens
           for ( int i = 0; i < delimiterIndex; i++ ) slotTokens.add( elementTokens.get(i));
           // make the delimiter token
           delimiterToken.add(  elementTokens.get(delimiterIndex));
           // make the value tokens
           for ( int i = delimiterIndex + 1; i< elementTokens.size(); i++ ) valueTokens.add( elementTokens.get(i));
           
           this.slotValueAnnotator.createSlotValue( pJCas, slotTokens, valueTokens, delimiterToken ); 
         }
     }
     
     
  } // end Method process() ------------------
  
 

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
   
  } // end Method initialize() --------------
  
  
  //----------------------------------
  /**
   * initialize 
   * 
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
   
    
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
   
    this.slotValueAnnotator = new SlotValueAnnotator();
    this.slotValueAnnotator.initialize();
  
  }// end Method initialize() ---------------
  


 
// ---------------------------------------
// Global Variables
// ---------------------------------------
  private SlotValueAnnotator         slotValueAnnotator = null;
  ProfilePerformanceMeter              performanceMeter = null;
 
  
  
} // end Class SlotValue ---------------------
