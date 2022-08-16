// =================================================
/**
 * FitlerOutPointers removes pointers from the cas's annotations.
 * This is useful just before writing out files to other
 * formats where the pointers to other annotations
 * cause problems.
 * 
 *        
 * @author  Guy Divita 
 * @created 2014.04.24
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;


import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class FilterOutPointersAnnotator extends JCasAnnotator_ImplBase {
 

  // -----------------------------------------
  /**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
  
    try {
	  // ----------------------------------
	  // Filter out pointers from the annotations
	  UIMAUtil.removeArrayElements(pJCas);
	  
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
      throw new AnalysisEngineProcessException();
    }
  } // end Method process() ----------------

  
  



  //----------------------------------
  /**
   * initialize loads in the resources needed for phrase chunking. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
 
    initialize();
    
  } // end Method initalize() -----------------------------------------
  

  //----------------------------------
  /**
   * initialize initalizes the list of outputTypes
   * 
   **/
  // ----------------------------------
  public void initialize() throws ResourceInitializationException {

  } // end Method initialize() -------
  


//---------------------------------------
//Global Variables
//---------------------------------------


  
} // end Class FilterWriter() ---------------
