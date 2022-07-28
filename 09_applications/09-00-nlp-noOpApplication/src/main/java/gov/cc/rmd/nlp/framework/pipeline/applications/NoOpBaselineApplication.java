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
package gov.cc.rmd.nlp.framework.pipeline.applications;
// ------------------------------------------------------------
/**
 * NoOpApplication calls a linePipeline in such a way that 
 * it can be used within gui's.
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




import gov.nih.cc.rmd.nlp.framework.pipeline.NoOpPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.applications.BaselineApplication;
import gov.nih.cc.rmd.nlp.framework.pipeline.applications.FrameworkBaselineApplication;


// -----------------------------------
/**
 * Class Types
 *
 */
// ------------------------------------
public class NoOpBaselineApplication extends BaselineApplication {



  public NoOpBaselineApplication() throws Exception {
    super();
   
  }




  //------------------------------------------
  /**
  * init
  *
  * @param pInputDir
  * @throws Exception
  */
  //------------------------------------------
  @Override
  public FrameworkBaselineApplication init(String[] pArgs) throws Exception {
    
    try {
  
     
     // --------------------
      // Read in the arguments needed for this application
      // --------------------
       this.setArgs(pArgs);
  
      // -------------------
      // Create a pipeline
      // -------------------
      NoOpPipeline        linePipeline = new NoOpPipeline( this.args );
   
      this.application = this.init( linePipeline);
      
  
    } catch ( Exception e) {
      e.printStackTrace();
      System.err.println("Issue initializing app " + e.toString());
      throw e;
      
    }
    
   
    return this.application;
  } // End Method init() ======================
  
  

// ------------------------------------------
/**
 * main
 * Options:
 * 
 * 
 * @param pArgs
 */
// ------------------------------------------
public static void main(String[] pArgs) {

  try {

    String[] args = NoOpApplication.setArgs(pArgs);
    
    NoOpBaselineApplication app = new NoOpBaselineApplication();
    app.init(args);
    
  // -------------------
    // Create a reader <on vinci, this should be the multi-record reader>
    // -------------------
    app.createReader(FrameworkBaselineApplication.TEXT_READER, pArgs);

    // ------------------
    // Create a writers to write out the processed cas's (write out xmi, vtt files, stat file, and concordance file)
    // ------------------
    app.addWriter(FrameworkBaselineApplication.VTT_WRITER, args);
    app.addWriter(FrameworkBaselineApplication.STATS_WRITER, args);
    app.addWriter(FrameworkBaselineApplication.XMI_WRITER, args);
    app.addWriter(FrameworkBaselineApplication.BIOC_WRITER, args);
  
    // ------------------
    // gather and process the cas's
    // -----------------
    app.run();
  
    // ----------------------
    // Finalize 
    app.finalize();

  } catch (Exception e2) {
    e2.printStackTrace();
    String msg = "Issue with the application " + e2.toString();
    System.err.println(msg);
  }
  System.exit(0);

} // End Method main() -----------------------------------



} // End NoOpBaselineApplication Class -------------------------------
