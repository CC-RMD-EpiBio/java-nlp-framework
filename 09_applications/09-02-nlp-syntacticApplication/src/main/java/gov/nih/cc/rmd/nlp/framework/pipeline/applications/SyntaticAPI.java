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
// ------------------------------------------------------------
/**
 * An api annotating syntatic elements in documents
 * The input is a string that is at the document level
 * The output is pipe delimited string that is in VTT
 * style  
 * 
 * @author Divita
 * Feb 14, 2013
 * 
 */
// -------------------------------------------------------------
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.uima.analysis_engine.AnalysisEngine;

import gov.nih.cc.rmd.nlp.framework.marshallers.frameworkObject.FrameworkObject;
import gov.nih.cc.rmd.nlp.framework.pipeline.SyntaticPipeline;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;


// -----------------------------------
/**
 * Class Types
 *
 */
// ------------------------------------
public class SyntaticAPI extends ApplicationAPI {

  
//=======================================================
 /**
  * Constructor SyntaticAPI 
  *
  */
  // ====================================================
 public SyntaticAPI() throws Exception {
 
   String dummyArgs[] = null;
  
   init( dummyArgs);
   
  } // end Constructor() ----------------------------

 //=======================================================
 /**
  * Constructor SyntaticAPI 
  * 
  * @param pArgs 
  *
  */
 // =====================================================
     public SyntaticAPI( String[] pArgs) throws Exception {
       
       init( pArgs);
       
      } // end Constructor() ----------------------------

 
  // =======================================================
  /**
   * init 
   *
   * @param pArgs
   */
  // =======================================================
  public void init(String[] pArgs) throws Exception {

    
    try {
      // --------------------
      // Read in any command line arguments, and add to them needed ones
      // The precedence is command line args come first, followed by those
      // set by the setArgs method, followed by the defaults set here.
      // --------------------

        
      String args[]  = SyntaticApplication.setArgs(pArgs );
      
      GLog.setLogDir( U.getOption(args,  "--logDir=", "./logs" ));
      
      // -------------------
      // Create an engine with a pipeline, attach it to the application
      // -------------------
      SyntaticPipeline syntaticPipeline = new SyntaticPipeline(args );
    
      AnalysisEngine ae = syntaticPipeline.getAnalysisEngine( );
      super.initializeApplication( pArgs, ae);
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue initializing Line " + e.toString();
      System.err.println( msg);
      throw new Exception( msg);
      
    }
      
  } // end Method initialize() -----------------------------
  
  
  
  // ------------------------------------------
  /**
   * main
   *    See the setArgs method to see what specific command line 
   *    arguments should be passed in here.
   *
   * @param pArgs
   */
  // ------------------------------------------
  public static void main(String[] pArgs) {
    

    try {
    
      String[] args = SyntaticApplication.setArgs( pArgs);
      SyntaticAPI anApplication = new SyntaticAPI( args);

    
      // ------------------
      // gather and process the cas's
      // -----------------
   

   
     
      
      BufferedReader in = new BufferedReader( new InputStreamReader( System.in ));
      String row = null;
      System.err.print("input : ");
      while ( (row = in.readLine() )!= null && row.trim().length() > 0  ) {
         FrameworkObject results = anApplication.processToFrameworkObject(  row + "\n" );  // <---------------- all the work is done here
         
         
        System.out.println();
        System.out.println(results.getWordTokens().toString() );
        System.err.println("--------------------");
        System.err.print("input : ");
      }
    
      System.err.println("Finished");
    
    } catch (Exception e) {
      e.getStackTrace();
      String msg = "Issue processing the files " + e.toString();
      System.err.println(msg);
    }
    
  } // end Method main() --------------------------------------
    
 

  // End SyntaticAPI Class -------------------------------
}
