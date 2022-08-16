// =================================================
/**
 * Writer is an interface for all marshaller writers.
 *
 *
 * @author  Guy Divita 
 * @created Sept 20, 2013
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.writer;


import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;




public abstract class AbstractWriter extends JCasAnnotator_ImplBase implements Writer {


  // protected List<UimaContextParameter> uimaContextParameters = null;
  // protected AnalysisEngine analysisEngine = null;
  // protected List<UimaContextParameter>uimaParameters;
   
  // -----------------------------------------
  /** 
   * process 
   *
   * @param pJCas 
   * @exception AnalysisEngineProcessException 
   */
  // -----------------------------------------
   // public void process(JCas pJCas) throws AnalysisEngineProcessException 
    
 
  // -----------------------------------------
  /* 
   * getParamters 
   *
   * @return UimaContextParameter[]
   * [TBD]   too new - creates a circularity - pulling
   *                   back until there's a usecase
   *                   to justify re-arranging packages
   *                   to stop the circularity
   *
    -----------------------------------------
 public List<UimaContextParameter> getParameters() {
    
    return this.uimaContextParameters;
  }
   
  
  // -----------------------------------------
  /** 
   * SetParamters 
   *
   * @param pUimaContextParameter[]
   *
   *
  // -----------------------------------------
  public void setParameters ( List<UimaContextParameter> pParameters )  {
    this.uimaContextParameters = pParameters;
  }
  */  
  
  // -----------------------------------------
  /** 
   * destroy 
   *
   */
  // -----------------------------------------
 // @Override
 // public void destroy() {
  //
  //  
  //} // end Method destroy() 

  
  //----------------------------------
  /**
   * initialize 
   *
   * @param aContext  [description here]
   * @exception ResourceInitializationException  [description here]
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
  }
  
  
//----------------------------------
  /**
   * initialize 
   *
   * @param pArgs 
   * @exception ResourceInitializationException  [description here]
   * 
   **/
  // ----------------------------------
  public void initialize(String pArgs[]) throws ResourceInitializationException {
    
  }
  
  
  
    
  

} // end Class Writer() ----
