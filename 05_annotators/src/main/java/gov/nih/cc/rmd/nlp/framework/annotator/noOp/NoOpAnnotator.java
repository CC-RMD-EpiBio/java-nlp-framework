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
