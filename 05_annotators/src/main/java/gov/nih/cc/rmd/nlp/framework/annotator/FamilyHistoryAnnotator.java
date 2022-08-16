// =================================================
/** 
 * FamilyHistoryAnnotator identifies the status of concepts that are 
 * not related to the patient.
 * 
 * [TBD]
 *
 * @author  Guy Divita 
 * @created Jun 22, 2011
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;


public class FamilyHistoryAnnotator extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  /**
   * process retrieves metadata from other sources and adds information
   * to the documentHeader
   * 
   * As a side affect, if the sourceDocument isn't filled out, this method
   * will fill it out so that other projects that rely upon it can pick
   * up the filename from it.
   * 
   * 
   * 03/10/2012
   * If there are no phrases, take the terms from each sentence. 
   * All we really need are the terms.
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    
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
 
  
  
} // end Class FamilyHistoryAnnotator() ---------------
