//=================================================
/**
 * toCAS puts the processed cas on an exposed hashtable<documentId,jCAS> for other
 * processes to pick off.
 *
 * @author  Guy Divita 
 * @created Sept 16, 2014
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.cas;


import gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;

import java.util.Hashtable;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;



public class ToCAS extends AbstractWriter {



  // =======================================================
/**
 * Constructor ToCAS 
 *
 * @param pOutputHash
 */
// =======================================================
public ToCAS( String pArgs[]) {
  this.initialize(pArgs);
} // end Constructor() -----------------------------------
  


    // ==========================================
  /**
   * initialize 
   *
   * @param pArgs
   */
  // ==========================================
  public void initialize(String pArgs[]) {
  
    
  } // end Method initialize() ==============================
  



    // =======================================================
  /**
   * Constructor ToCAS 
   *
   * @param pOutputHash
   */
  // =======================================================
  public ToCAS(Hashtable<String, JCas> pOutputHash) {
    this.outputHash = pOutputHash;
  } // end Constructor() -----------------------------------


    // -----------------------------------------
    /** 
     * process iterates through all annotations, filters out
     * those that should be filtered out, then pushes them
     * into a database store.
     * 
     * Each document should include a DocumentAnnotation annotation and a documentHeader annotation
     * for re-animation purposes.
     *
     * @param pAJCas
     * @throws AnalysisEngineProcessException
     */
    // -----------------------------------------
    @Override
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
      
   try {
       String documentId = VUIMAUtil.getDocumentId(pJCas);
      // System.err.println("Putting " + documentId + " on the output queue");
       this.outputHash.put(documentId, pJCas);
      } catch (Exception e) {
        e.getStackTrace();
        String msg = "Issue putting the jcas on the outputHash " + e.toString() ;
        System.err.println(msg);
        throw new AnalysisEngineProcessException();
      }
    } // end Method process


    


    // -----------------------------------------
    /** 
     * destroy closes the database
     *
     */
    // -----------------------------------------
    @Override
    public void destroy() {
    
    } // end Method destroy() 


    // ----------------------------------------
    // Global variables
    // ----------------------------------------
  
    private Hashtable<String, JCas> outputHash = null;


} // end Class toCommonModel
