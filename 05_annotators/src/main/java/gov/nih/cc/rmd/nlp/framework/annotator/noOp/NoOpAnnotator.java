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
 * NoOpAnnotator is an empty annotator
 *
 *
 * @author  Guy Divita 
 * @created September 27, 2013
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.noOp;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;


public class NoOpAnnotator extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  /**
   * process retrieves lines of the document, labels those that are questions
   * as QuestionAndAnswer elements.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
   // String inputFileName = Util.getInputFileName(pJCas);
   // System.err.println( "-----noOp--------- input fileName = " +  inputFileName ); 
   

  
  } // end Method process() ----------------
   
  
  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
   
      
  } // end Method initialize() -------
  
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------

  
  
  
  
} // end Class LineAnnotator() ---------------
