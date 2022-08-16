/*******************************************************************************
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                       Epidemiology and Biostatistics Branch 
 *                                            2019 - 2022
 *   ---------------------------------------------------------------------------
 *   Copyright Notice:
 *   This software was developed and funded by the National Institutes of Health
 *   Clinical Center (NIHCC), part of the National Institutes of Health (NIH),
 *   and agency of the United States Department of Health and Human Services,
 *   which is making the software available to the public for any commercial
 *   or non-commercial purpose under the following open-source BSD license.
 *  
 *   Government Usage Rights Notice:
 *   The U.S. Government retains unlimited, royalty-free usage rights to this 
 *   software, but not ownership, as provided by Federal law. Redistribution 
 *   and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *      1. Redistributions of source code must retain the above copyright
 *         and government usage rights notice, this list of conditions and the 
 *         following disclaimer.
 *  
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *        
 *      3. Neither the names of the National Institutes of Health Clinical
 *         Center, the National Institutes of Health, the U.S. Department of
 *         Health and Human Services, nor the names of any of the software
 *         developers may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
 *   
 *      4. The U.S. Government retains an unlimited, royalty-free right to
 *         use, distribute or modify the software.
 *   
 *      5. Please acknowledge NIH CC as the source of this software by including
 *         the phrase: "Courtesy of the U.S. National Institutes of Health Clinical Center"
 *          or 
 *                     "Source: U.S. National Institutes of Health Clinical Center."
 *  
 *     THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS
 *     IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *     TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *     PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 *     OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *     EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *     PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *     NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *     When attributing this code, please make reference to:
 *        Divita G, Carter ME, Tran LT, Redd D, Zeng QT, Duvall S, Samore MH, Gundlapalli AV. 
 *        v3NLP Framework: tools to build applications for extracting concepts from clinical text. 
 *        eGEMs. 2016;4(3). 
 *      
 *     In the absence of a specific paper or url listed above, reference https://github.com/CC-RMD-EpiBio/java-nlp-framework
 *   
 *     To view a copy of this license, visit https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD
 *******************************************************************************/
/*
 *
 */
/**
 * BaselineApplication is an abstract class that contains
 * those methods common to pipeline applications embedded within
 * gui's.  The gui's call the pipelines in separate threads,
 * thus requiring a run() method.  The guis also need methods
 * to track progress, set readers and writers ....  
 * 
 * Classes that extend from this just need to implement
 * the init() method that instantiates a pipeline to
 * pass it along to this class.
 * 
 * @author Divita
 * Aug 26, 2015
 * 
 * ------------------------------------------------------------
 *
 *
 *
 * -------------------------------------------------------------
 */
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import java.io.PrintWriter;

import org.apache.uima.analysis_engine.AnalysisEngine;

import gov.nih.cc.rmd.nlp.framework.pipeline.AbstractPipeline;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * The Class BaselineApplication.
 */
public abstract class BaselineApplication implements Runnable {

  // ------------------------------------------
  /**
   * constructor This must be used in conjunction with the setArgs() and
   * init(Pipeline) .
   *
   * @throws Exception the exception
   */
  // ------------------------------------------
  public BaselineApplication() throws Exception {
    // n/a
  }

  // =======================================================
  /**
   * getName retrieves the tool name.
   *
   * @return the name
   */
  // =======================================================
  public String getName() {
    return this.name;
  } // End Method getName() ======================

  // ------------------------------------------
  /**
   * setArgs.
   *
   * @param pArgs the args
   * @throws Exception the exception
   */
  // ------------------------------------------
  public void setArgs(String[] pArgs) throws Exception {
    this.args = pArgs;
    this.application.setArgs(pArgs);
  }

  // ------------------------------------------
  /**
   * init Implement this It should look like the following.
   *
   * @param pArgs the args
   * @return FrameworkBaselineApplicaition
   * @throws Exception the exception
   */
  // ------------------------------------------
  public abstract FrameworkBaselineApplication init(String[] pArgs) throws Exception;

  /*
   * 
   * try {
   * 
   * 
   * // -------------------- // Read in the arguments needed for this
   * application // -------------------- this.args =
   * MRSAConceptApplication.setArgs(pArgs);
   * 
   * // ------------------- // Create a pipeline // -------------------
   * MRSAConceptPipeline mrsaPipeline = new MRSAConceptPipeline( this.args );
   * 
   * this.application = this.init(pArgs, mrsaPipeline);
   * 
   * 
   * } catch ( Exception e) { e.printStackTrace();
   * System.err.println("Issue initializing app " + e.toString()); throw e;
   * 
   * }
   * 
   * 
   * return this.application;
   * 
   */

  // ------------------------------------------
  /**
   * init.
   *
   * @param pPipeline the pipeline
   * @return FrameworkBaselineApplicaition
   * @throws Exception the exception
   */
  // ------------------------------------------
  @SuppressWarnings("resource")
  public FrameworkBaselineApplication init(AbstractPipeline pPipeline) throws Exception {

    try {

      String outputDir = U.getOption(this.args, "--outputDir=", "./");
      this.name = U.getOption(this.args, "--tool=", "someTool");
      String logDir = U.getOption(this.args, "--logDir=", outputDir + "/logs");
      U.mkDir(logDir);
      String logFile = logDir + "/" + this.getClass().getSimpleName() + ".log";
      PrintWriter logOut = new PrintWriter(logFile);
      this.meter = new PerformanceMeter(logOut);
      this.meter.begin("Starting the application");

      // -------------------
      // Create a BaselineFrameworkApplication instance
      // -------------------
      this.application = new FrameworkBaselineApplication(this.args);

      // -------------------
      // Add a performance meter to the application (This is optional)
      this.application.addPerformanceMeter(this.meter);

      // -------------------
      // Create a pipeline, retrieve an analysis engine
      // that uses the pipeline, attach it to the application
      // -------------------
      AnalysisEngine ae = pPipeline.getAnalysisEngine();
      application.setAnalsyisEngine(ae);

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue initializing app " + e.toString());
      throw e;

    }

    this.initialized = true;

    return this.application;
  } // End Method init() ======================

  // ------------------------------------------
  /**
   * run.
   */
  // ------------------------------------------
  @Override
  public void run() {

    try {
      application.process();

    } catch (Exception e) {
      e.getStackTrace();
      String msg = "Issue processing the files " + e.toString();
      System.err.println(msg);
    }

  } // End Method run() ======================

  // ------------------------------------------
  /**
   * run configures the readers and writers and runs.
   *
   * @param pArgs the args
   */
  // ------------------------------------------
  public void run(String[] pArgs) {
    try {

      this.args = pArgs;
      this.setReaderAndWriters(pArgs);

      this.run();

      // --------------------------
      // The pipeline gets destroyed at the end of a run
      // requiring one to re-initialize the application with
      // a new instance of the pipeline
      //
      // This should be done in the background in the thread
      // that kicked off the run. Maybe as part of the run?

    } catch (Exception e) {
      e.getStackTrace();
      String msg = "Issue processing the files " + e.toString();
      System.err.println(msg);
    }

  } // End Method run() ======================

  // ------------------------------------------
  /**
   * createReader.
   *
   * @param pReaderType the reader type
   * @param pArgs the args
   * @throws Exception the exception
   */
  // ------------------------------------------
  public void createReader(int pReaderType, String[] pArgs) throws Exception {

    this.application.createReader(pArgs);

  } // End Method createReader() -----------

  // =======================================================
  /**
   * setReaderAndWriters .
   *
   * @param pArgs the reader and writers
   */
  // =======================================================
  public void setReaderAndWriters(String[] pArgs) {

    this.application.createReader(pArgs);
    this.application.addWriters(pArgs);

  } // End Method setReaderAndWriter() ======================

  // ------------------------------------------
  /**
   * addWriter.
   *
   * @param pWriterType the writer type
   * @param pArgs the args
   * @throws Exception the exception
   */
  // ------------------------------------------
  public void addWriter(int pWriterType, String[] pArgs) throws Exception {

    this.application.addWriters(pArgs);

  } // End Method addWriter() -----------

  // ------------------------------------------
  /**
   * getArgs.
   *
   * @return the args
   * @returns String[]
   */
  // --------------------------------------------
  public String[] getArgs() {
    return this.application.getArgs();
  } // End Method getArgs() ------------------

  // =======================================================
  /**
   * getProgress .
   *
   * @return double
   */
  // =======================================================
  public double getProgress() {
    return this.application.getProgress();
  } // End Method getProgress() ======================

  // ------------------------------------------
  /**
   * finalize.
   */
  // ------------------------------------------
  @Override
  public void finalize() {
    this.meter.stop();
    System.err.println(" DOHN");

  } // end Method finalize() -----------------

  // ------------------------------------------
  /**
   * main Overide this.
   *
   * @param pArgs the command line arguments
   */
  // ------------------------------------------
  public static void main(String[] pArgs) {

    /*
     * try {
     * 
     * BaselineApplication app = new BaselineApplication(pArgs);
     * 
     * 
     * 
     * // ------------------- // Create a reader <on vinci, this should be the
     * multi-record reader> // -------------------
     * app.createReader(FrameworkBaselineApplication.TEXT_READER, args);
     * 
     * // ------------------ // Create a writers to write out the processed
     * cas's (write out xmi, vtt files, stat file, and concordance file) //
     * ------------------ app.addWriter(FrameworkBaselineApplication.VTT_WRITER,
     * args); app.addWriter(FrameworkBaselineApplication.STATS_WRITER, args);
     * app.addWriter(FrameworkBaselineApplication.XMI_WRITER, args);
     * app.addWriter(FrameworkBaselineApplication.BIOC_WRITER, args);
     * 
     * // ------------------ // gather and process the cas's //
     * ----------------- app.run();
     * 
     * // ---------------------- // Finalize app.finalize();
     * 
     * } catch (Exception e2) { e2.printStackTrace(); String msg =
     * "Issue with the application " + e2.toString(); System.err.println(msg); }
     * System.exit(0);
     */
  } // End Method main() -----------------------------------

  // =========================================
  // Global variables

  /** The meter. */
  protected PerformanceMeter meter = null;

  /** The name. */
  private String name;

  /** The application. */
  protected FrameworkBaselineApplication application = null;

  /** The initialized. */
  protected boolean initialized = false;

  /** The args. */
  protected String[] args;

  // =======================================================
  /**
   * initialized [Summary here].
   *
   * @return true, if successful
   */
  // =======================================================
  public boolean initialized() {
    return this.initialized;
  } // End Method initialized() ======================

  // =======================================================
  /**
   * cancel cancels the thread that is running - which should translate to the
   * uima process() method.
   * 
   */
  // =======================================================
  public void cancel() {
    application.destroyGentle("Cancel called");
  } // End Method cancel() ======================

}
