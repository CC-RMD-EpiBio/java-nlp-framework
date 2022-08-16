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
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;




public interface Writer {


 
  // -----------------------------------------
  /** 
   * process 
   *
   * @param pJCas 
   * @exception AnalysisEngineProcessException 
   */
  // -----------------------------------------
   public void process(JCas pJCas) throws AnalysisEngineProcessException ;
    
   
  // -----------------------------------------
  /** 
   * getParamters 
   *
   * @return UimaContextParameter[]
   *
   */
   // -----------------------------------------
   // public List<UimaContextParameter> getParameters(); 
  
  
  // -----------------------------------------
  /** 
   * SetParamters 
   *
   * @param List<pUimaContextParameter>
   *
   */
  // -----------------------------------------
  // public void setParameters ( List<UimaContextParameter> pParameters ) ; 
    
  
    
  

  
  // -----------------------------------------
  /** 
   * destroy 
   *
   */
  // -----------------------------------------
  public void destroy();
  
  
  //----------------------------------
  /**
   * initialize 
   *
   * @param aContext  [description here]
   * @exception ResourceInitializationException  [description here]
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException; 
  
  
//----------------------------------
  /**
   * initialize 
   *
   * @param pArgs 
   * @exception ResourceInitializationException  [description here]
   * 
   **/
  // ----------------------------------
  public void initialize(String pArgs[]) throws ResourceInitializationException ;
    
  
  
    
  

} // end Class Writer() ----
