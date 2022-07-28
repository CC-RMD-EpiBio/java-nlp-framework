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
 * An application annotating lines in documents
 * 
 * 
 * @author Divita
 * Feb 14, 2013
 * 
 * ------------------------------------------------------------
 *
 *
 *
 * -------------------------------------------------------------
 */


package gov.cc.rmd.nlp.framework.pipeline.applications;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.uima.analysis_engine.AnalysisEngine;

import gov.nih.cc.rmd.nlp.framework.pipeline.NoOpPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.applications.ApplicationAPI;



// -----------------------------------
/**
 * Class Types
 *
 */
// ------------------------------------
public class NoOpAPI extends ApplicationAPI {



  // =======================================================
  /**
   * Constructor NoOp 
   *
   * @param pArgs
   */
  // =======================================================
  public NoOpAPI(String[] pArgs) throws Exception {

    
    try {
      // --------------------
      // Read in any command line arguments, and add to them needed ones
      // The precedence is command line args come first, followed by those
      // set by the setArgs method, followed by the defaults set here.
      // --------------------

        
      String args[]  = NoOpApplication.setArgs(pArgs );
      // -------------------
      // Create an engine with a pipeline, attach it to the application
      // -------------------
      NoOpPipeline linePipeline = new NoOpPipeline(args );
    
      AnalysisEngine ae = linePipeline.getAnalysisEngine( );
      super.initializeApplication( pArgs, ae);
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue initializing NoOp " + e.toString();
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
    
      String[] args = NoOpApplication.setArgs( pArgs);
      NoOpAPI anApplication = new NoOpAPI( args);

    
      // ------------------
      // gather and process the cas's
      // -----------------
   

   
     
      
      BufferedReader in = new BufferedReader( new InputStreamReader( System.in ));
      String row = null;
      System.err.print("input : ");
      while ( (row = in.readLine() )!= null && row.trim().length() > 0  ) {
         String results = anApplication.process(  row + "\n" );  // <---------------- all the work is done here
         
         
        System.out.println();
        System.out.println(results );
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
    
 

  // End NoOpAPI Class -------------------------------
}
