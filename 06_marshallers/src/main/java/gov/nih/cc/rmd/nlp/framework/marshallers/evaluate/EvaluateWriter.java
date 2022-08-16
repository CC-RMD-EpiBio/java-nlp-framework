//=================================================
/**
 * EvaluateWriter is a wrapper around the evaluateAnnotator. 
 *
 * @author  Guy Divita 

 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.evaluate;


import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.annotator.EvaluateAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.EvaluateWithAttributeAnnotator;
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;



public class EvaluateWriter extends  AbstractWriter {

  
//-----------------------------------------
 /** 
  * constructor
  * @throws ResourceInitializationException 
  */
 // ---------------------------------------
  public EvaluateWriter( ) throws ResourceInitializationException {
   /* 
    String args[] = null;
    super.initialize( args );
    initializeAux( args);
    */
   
    
  } // end Constructor() -------------------

//-----------------------------------------
 /** 
  * constructor
  * 
  * @param args
 * @throws ResourceInitializationException 
  */
 // ---------------------------------------
  public EvaluateWriter( String[] pArgs) throws ResourceInitializationException {
    
    super.initialize( pArgs );
    initializeAux( pArgs);
   
    
  } // end Constructor() -------------------

  //----------------------------------
  /**
   * initialize loads in the resources 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
    String args[] = null;
    try {
      args                 = (String[]) aContext.getConfigParameterValue("args");  

    } catch (Exception e ) {
      GLog.println(GLog.ERROR_LEVEL,"Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
    

    initialize( args );
 
  } // end initialize() ----------------------------
  
  
// =================================================
/**
 * initializeAux 
 * @param pArgs
 * @throws ResourceInitializationException
*/
// =================================================
   protected void initializeAux(String[] pArgs) throws ResourceInitializationException {
      
     initialize( pArgs);
  
} // End Method initializeAux() -------------------
   
   
   
// =================================================
/**
 * initialize
 * @param pArgs
 * @throws ResourceInitializationException
*/
// =================================================
   public void initialize(String[] pArgs) throws ResourceInitializationException {
      
      try {
        this.evaluator = new gov.nih.cc.rmd.nlp.framework.annotator.EvaluateWithAttributeAnnotator(  pArgs);
      
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, "Issue initializing Evalation Writer " + e.toString());
        throw new ResourceInitializationException();
      }
  
} // end Method initialize() -----------------------

/* (non-Javadoc)
 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
 */
@Override
public void process(JCas pJCas) throws AnalysisEngineProcessException {
 this.evaluator.process( pJCas);
 }


// =================================================
/**
 * report runs the report at the end
 * 
 * @return String
*/
// =================================================
public String report() {
  return this.report();
  
}

/* (non-Javadoc)
 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#destroy()
 */
@Override
public void destroy()  {
 this.evaluator.destroy( );
   // String returnVal = report();
 }



// ---------------------
// Global Variables
// ---------------------
private gov.nih.cc.rmd.nlp.framework.annotator.EvaluateWithAttributeAnnotator evaluator = null;


} // end Class EvaluateWriter
