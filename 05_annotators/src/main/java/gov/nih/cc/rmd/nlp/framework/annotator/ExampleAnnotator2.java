// =================================================
/**
 * ExampleAnnotator labels lines from text.
 *
 *
 * @author  Guy Divita 
 * @created Jun 22, 2011
 *
 **   *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.Line;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;



public class ExampleAnnotator2 extends JCasAnnotator_ImplBase {
 
  public static final String annotatorName = ExampleAnnotator.class.getSimpleName();
  
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
    
    
    this.performanceMeter.startCounter();
    String docText = pJCas.getDocumentText();
    if ( docText != null) {
      int newExampleProjectOffsetList[] = U.getNewlineOffsets(docText);
      if ( newExampleProjectOffsetList != null ) {
        int previousEndSpan = 0;
        int beginSpan = 0;
        int i = 0;
        
        // -----------------------------------
        // Loop through the newline offsets 
        for ( ; i < newExampleProjectOffsetList.length; i++ ) {
          beginSpan   = previousEndSpan;
          int endSpan = newExampleProjectOffsetList[i];
       
          createExampleAnnotation( pJCas, beginSpan, endSpan);  // <----- Magic happens here
          
          previousEndSpan = endSpan + 1;
        } // end loop thru newlines of the document
        
        // -----------------------------------------------------------
        // take care of the last line (which might not have a newline)
        if ( previousEndSpan < docText.length()) {
          beginSpan = previousEndSpan;
          int endSpan = docText.length();
          
          createExampleAnnotation( pJCas, beginSpan, endSpan);  
          
        }

        // ----------------------------------------
        // And the really last line of the document (this might be redundant)
        if ( i > 0 &&  newExampleProjectOffsetList[i-1] < docText.length())
          createExampleAnnotation( pJCas, beginSpan, docText.length());
      
      } // end if there are newlines in the document
    } // end if there is any text in the document
  
    this.performanceMeter.stopCounter();
    
  } // end Method process() ----------------
   
  
  //----------------------------------
  /**
   * destroy
   * 
   **/
  // ----------------------------------
  public void destroy() {
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());

  }

  
//-----------------------------------------
 /**
  * initialize loads in the resources.
  *   Put pipeline parameters in the args parameter
  *   to be retrieved here.
  * 
  *      Pass in on your args, an arg with the value: "--outputDir=/some/path/to/put/outputFiles"
  *
  *   This class is tooled up to have performance statisitcs get put into $outputDir/logs/Class.log  
  *
  * @param aContext
  *
  */
 // -----------------------------------------
  public void initialize(UimaContext aContext)  throws ResourceInitializationException {
   
    String args[] = null;
    
    try {
      args                  = (String[]) aContext.getConfigParameterValue("args");
      
      initialize( args);
   
      
     
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with getting the passed in arguments " + e.toString());
      throw new ResourceInitializationException();
    
    }
 
  } // end Method initialize() -----------
  

//-----------------------------------------
 /**
  * initialize 
  * 
  * @param pArgs
  *
  */
 // -----------------------------------------
  public void initialize(String pArgs[] )  throws ResourceInitializationException {
    
    this.outputDir        = U.getOption(pArgs, "--outputDir=", "./");
    
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    
    // ------------------------------------------------------------
    // Example parameter passed from the pipeline to this annotator.
    //    The args string array contains --key=value elements. The 
    //    U.getOption() method traverses thru the args to find the
    //    --key=  and parse the element to retrieve the value for 
    //    this key.
    // -------------------------------------------------------------
    String exampleLabel = U.getOption(pArgs,  "--exampleLabel=", "fever");
    
  } // end Method initialize() ------------
   
//-----------------------------------------
 /**
  * createExampleAnnotation creates lineAnnotatons
  * 
  * See the type.decriptor project gov.va.chir.Model.xml for the 
  * the definition of what a Line Type is.
  * 
  * @param pJCas
  * @param beginSpan
  * @param endSpan
  * 
  */
 // -----------------------------------------
 private void createExampleAnnotation( JCas pJCas,  int beginSpan, int endSpan )  {
  
  Line statement = new Line( pJCas);
   
   
   VUIMAUtil.setProvenance( pJCas, statement, this.getClass().getName() );
   statement.setBegin(                    beginSpan);
   statement.setEnd(                        endSpan);
   
   statement.addToIndexes(pJCas);   // <------------- very important to do this

 } // end Method createEvidence() ---
  
  

   // ---------------------------
   // Global Variables
   // ---------------------------
 ProfilePerformanceMeter              performanceMeter = null;
   private String                       outputDir = null;


  
  
} // end Class ExampleAnnotator() ---------------
