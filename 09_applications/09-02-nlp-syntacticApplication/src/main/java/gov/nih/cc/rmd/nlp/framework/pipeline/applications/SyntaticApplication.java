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
 * SyntaticApplication kicks off the Syntatic pipeline on a corpus
 * 
 * @author Divita
 *         Feb 14, 2013
 * 
 *         ------------------------------------------------------------
 * 
 * 
 * 
 *         -------------------------------------------------------------
 */

package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import gov.nih.cc.rmd.nlp.framework.annotator.AssertionAnnotator;
import gov.nih.cc.rmd.nlp.framework.pipeline.FrameworkPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.SyntaticPipeline;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;

import org.apache.uima.analysis_engine.AnalysisEngine;


public class SyntaticApplication {

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

      PerformanceMeter meter = new PerformanceMeter(System.err);
      meter.begin("Starting the application");
      

      // --------------------
      // Read in any command line arguments, and add to them needed ones
      // The precedence is command line args come first, followed by those
      // set by the setArgs method, followed by the defaults set here.
      // --------------------
      String args[] = setArgs(pArgs);

      GLog.setLogDir( U.getOption(args,  "--logDir=", "./logs" ));
   
      // -------------------
      // Create a BaselineFrameworkApplication instance
      // -------------------
      FrameworkBaselineApplication application = new FrameworkBaselineApplication();

      // -------------------
      // Add a performance meter to the application (This is optional)
      application.addPerformanceMeter(meter);

      // -------------------
      // Create a pipeline, retrieve an analysis engine
      // that uses the pipeline, attach it to the application
      // -------------------
      SyntaticPipeline        linePipeline = new SyntaticPipeline( args );
      AnalysisEngine                ae = linePipeline.getAnalysisEngine();
      application.setAnalsyisEngine(ae);

      // -------------------
      // Create a reader <on vinci, this should be the multi-record reader>
      // -------------------
      // application.createReader( FrameworkBaselineApplication.MULTI_RECORD_FILE_READER, args);
      application.createReader(FrameworkBaselineApplication.TEXT_READER, args);

      // ------------------
      // Create a writers to write out the processed cas's (write out xmi, vtt files, stat file, and concordance file)
      // ------------------
      // application.addWriter(FrameworkBaselineApplication.VTT_WRITER, args);
      application.addWriter(FrameworkBaselineApplication.STATS_WRITER, args);
      application.addWriter(FrameworkBaselineApplication.XMI_WRITER, args);
     // application.addWriter(FrameworkBaselineApplication.COMMON_MODEL_WRITER, args);

      meter.mark("Finished Initialization");

      // ------------------
      // gather and process the cas's
      // -----------------

      try {
        application.process();
      } catch (Exception e) {
        e.getStackTrace();
        String msg = "Issue processing the files " + e.toString();
        System.err.println(msg);
      }

      meter.stop();

      // ----------------------
      System.err.println(" DOHN");

    } catch (Exception e2) {
      e2.printStackTrace();
      String msg = "Issue with the application " + e2.toString();
      System.err.println(msg);
    }
    System.exit(0);

  } // End Method main() -----------------------------------

  // ------------------------------------------
  /**
   * setArgs
   * 
   * 
   * @return
   */
  // ------------------------------------------
  public static String[] setArgs(String pArgs[]) {

    // -------------------------------------
    // dateStamp
    String dateStamp = U.getDateStampSimple();

    // -------------------------------------
    // Input and Output

    String    inputDir  = U.getOption(pArgs, "--inputDir=", "/data/input/");
    String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_Syntatic_" + dateStamp);
    String       logDir = U.getOption(pArgs, "--logDir=",   outputDir + "/logs" ); 
    String   printToLog = U.getOption(pArgs, "--printToLog=", "true");
    String  outputTypes = U.getOption(pArgs, "--outputTypes=" ,"List:ListElement:Number:Line:WordToken:SectionZone:ContentHeading:DependentContent:SlotValue:CheckBox:Delimiter:Question:Section:LexicalElement:Sentence:POS:Phrase:Shape:UnitOfMeasure:Table:Row:Column" );
    String profilePerformanceLogging = U.getOption(pArgs,  "--profilePerformanceLogging=", "false");
    
    String localTerminologyFiles = U.getOption(pArgs, "--localTerminologyFiles=", SyntaticPipeline.SyntaticTerminologyFiles ); 
    
    String setMetaDataHeader = U.getOption(pArgs,  "--setMetaDataHeader=", "false");
    
  
    String args[] = {
        
        "--inputDir=" + inputDir,
        "--outputDir=" + outputDir,
        "--logDir=" + logDir,
        "--printToLog=" + printToLog,
        "--profilePerformanceLogging=" + profilePerformanceLogging,
        "--outputTypes=" + outputTypes,
        "--localTerminologyFiles=" + localTerminologyFiles,
        "--setMetaDataHeader=" + setMetaDataHeader
    
        
        
       
    };

       
     

    return args;

  }  // End Method setArgs() -----------------------
  

  // ------------------------------------------------
  // Global Variables
  // ------------------------------------------------

} // End SyntaticApplication Class -------------------------------
