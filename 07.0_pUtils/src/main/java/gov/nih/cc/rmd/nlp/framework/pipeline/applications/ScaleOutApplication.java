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
/*
 *
 */
/**
 * ScaleOutApplication wraps up x number of threaded servers around a pipeline
 *
 * @author  guy
 * @created Sep 3, 2014
 *
   
 */

package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.util.Progress;

import gov.nih.cc.rmd.nlp.framework.pipeline.AbstractPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.ScaleOutAnnotatorPerformanceStats;
import gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * The Class ScaleOutApplication.
 */
public class ScaleOutApplication {

  /** The Constant DEFAULT_RECYCLE_AT. */
  private static final int DEFAULT_RECYCLE_AT = 1000000000;

  /** The meter. */
  private PerformanceMeter meter = null;

  /** The initialization meter. */
  private PerformanceMeter initializationMeter = null;

  /** The applications. */
  private ArrayList<FrameworkBaselineApplicationWithStaticWriters> applications = null;

  /** The number of servers. */
  private int numberOfServers;

  /** The output dir. */
  private String outputDir;

  /** The metric. */
  private int metric;

  /** The log dir. */
  private String logDir;

  /** The Single reader. */
  private static gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader SingleReader;

  /** The recycle at. */
  private int recycleAt = DEFAULT_RECYCLE_AT;

  /** The args. */
  // private ArrayList<Integer> writers = null;
  private String[] args = null;

  /** The pipeline class. */
  private Class<?> pipelineClass;

  /** The pipeline args. */
  private String[] pipelineArgs;

  /** The number to process. */
  private long numberToProcess = Integer.MAX_VALUE;

  // =======================================================
  /**
   * Constructor ScaleOutApplication .
   *
   * @param numberOfServers the number of servers
   * @param outputDir the output dir
   * @param metric the metric
   * @throws Exception the exception
   */
  // =======================================================
  public ScaleOutApplication(int numberOfServers, String outputDir, int metric) throws Exception {

    initialize(numberOfServers, outputDir, metric, DEFAULT_RECYCLE_AT, null);
  } // end Constructor() ---------------------------------

  // =======================================================
  /**
   * Constructor ScaleOutApplication .
   *
   * @param pNumberOfApplications the number of applications
   * @param pOutputDir the output dir
   * @throws Exception the exception
   */
  // =======================================================
  public ScaleOutApplication(int pNumberOfApplications, String pOutputDir) throws Exception {

    this.initialize(pNumberOfApplications, pOutputDir, 100, DEFAULT_RECYCLE_AT, null);
  } // end constructor

  // =======================================================
  /**
   * Constructor ScaleOutApplication .
   *
   * @param pNumberOfApplications the number of applications
   * @param pOutputDir the output dir
   * @param pMetric the metric
   * @param pRcycleAt the rcycle at
   * @param pNumberToProcess the number to process
   * @throws Exception the exception
   */
  // =======================================================
  public ScaleOutApplication(int pNumberOfApplications, String pOutputDir, int pMetric,
      int pRcycleAt, int pNumberToProcess) throws Exception {

    this.recycleAt = pRcycleAt;
    this.numberToProcess = pNumberToProcess;
    this.initialize(pNumberOfApplications, pOutputDir, pMetric, this.recycleAt, null);
  } // end constructor

  // =======================================================
  /**
   * Constructor ScaleOutApplication .
   *
   * @param pNumberOfApplications the number of applications
   * @param pOutputDir the output dir
   * @param pMetric the metric
   * @param pRcycleAt the rcycle at
   * @param pNumberToProcess the number to process
   * @param pMeter the meter
   * @throws Exception the exception
   */
  // =======================================================
  public ScaleOutApplication(int pNumberOfApplications, String pOutputDir, int pMetric, int pRcycleAt, int pNumberToProcess, PerformanceMeter pMeter) throws Exception {

    this.recycleAt = pRcycleAt;
    this.numberToProcess = pNumberToProcess;
    this.initialize(pNumberOfApplications, pOutputDir, pMetric, this.recycleAt, pMeter);
  } // end constructor

  // =======================================================
  /**
   * initialize [Summary here].
   *
   * @param pNumberOfServers the number of servers
   * @param pOutputDir the output dir
   * @param pMetric the metric
   * @param pRecycleAt the recycle at
   * @param pMeter the meter
   * @throws Exception the exception
   */
  // =======================================================
  @SuppressWarnings("resource")
  private void initialize(int pNumberOfServers, String pOutputDir, int pMetric, int pRecycleAt, PerformanceMeter pMeter) throws Exception {

    this.numberOfServers = pNumberOfServers;
    this.outputDir = pOutputDir;
    this.metric = pMetric;
    this.logDir = this.outputDir + "/logs";
    this.meter = pMeter;
    String initializationLog = this.outputDir + "/logs/initialization.log";
    U.mkDir(this.logDir);

    if (this.meter == null) {
      String mainLogFile = this.outputDir + "/logs/mainLog.log";
      PrintWriter mainLog = new PrintWriter(mainLogFile);
      this.meter = new PerformanceMeter(mainLog);
    }

    this.initializationMeter = new PerformanceMeter(initializationLog);

    this.initializationMeter.begin("initializing the Application ");

    String msg1 = "Starting the application with " + numberOfServers;
    System.err.println(msg1);
    this.initializationMeter.mark(msg1);

    // -------------------
    // Create a BaselineFrameworkApplication instance
    // -------------------
    this.applications =  new ArrayList<FrameworkBaselineApplicationWithStaticWriters>(numberOfServers);

    // ------------------
    // Set up the application pipelines
    // -----------------
    for (int serverNumber = 0; serverNumber < numberOfServers; serverNumber++) {

      initialize(serverNumber, pRecycleAt);

    }

    // End Method initialize() ======================
  }

  // =======================================================
  /**
   * initialize .
   *
   * @param serverNumber the server number
   * @param pRecycleAt the recycle at
   * @return FrameworkBaselineApplication
   * @throws FileNotFoundException the file not found exception
   */
  // =======================================================
  @SuppressWarnings("resource")
  private FrameworkBaselineApplicationWithStaticWriters initialize(int serverNumber, int pRecycleAt) throws FileNotFoundException {

    // ===================================================
    // This needs to be in it's own thread for each
    String serverName = "Application____" + serverNumber;
    FrameworkBaselineApplicationWithStaticWriters anApplication = new FrameworkBaselineApplicationWithStaticWriters();
    this.applications.add(anApplication);

    // -------------------
    // Add a performance meter to the application
    String serverLogFileName = outputDir + "/logs/" + "ServerLog_" + serverNumber + ".log";
    PrintWriter serverLogWriter = new PrintWriter(serverLogFileName);
    PerformanceMeter serverLog = new PerformanceMeter(serverLogWriter);
    serverLog.setMetric(metric);
    anApplication.setServerName(serverName);
    anApplication.addPerformanceMeter(serverLog);
    anApplication.setRecycleAt(pRecycleAt);
    anApplication.setNumberToProcess(this.numberToProcess);
    System.err.println("set the application name to " + anApplication.getServerName());

    return anApplication;
  } // End Method initialize() ======================

  // =======================================================
  /**
   * createReader creates and sets up a singleton reader. This reader is
   * synchronized across the n servers.
   *
   * @param pReaderType ( taken from the FrameworkBaselineApplication.xxxxREADER
   *          enumerations)
   * @param pArgs the args
   * @return CollectionReader
   */
  // =======================================================
  public CollectionReader createReader(int pReaderType, String[] pArgs) {

    SingleReader = (Reader) FrameworkReaders.createSingletonReader(pReaderType, pArgs);

    for (int serverNumber = 0; serverNumber < this.numberOfServers; serverNumber++)
      applications.get(serverNumber).attachReader(SingleReader);

    return SingleReader;
  } // End Method createReader() ======================

  // =======================================================
  /**
   * createReader creates and sets up a singleton reader. This reader is
   * synchronized across the n servers.
   * 
   * The default reader is the text reader.
   *
   * @param pArgs relies upon the arguement --inputFormat= with the values
   *          coming from FrameworkBaselineApplicaiton.TEXT_READER_ ....
   * 
   * @return CollectionReader
   */
  // =======================================================
  public CollectionReader createReader(String[] pArgs) {

    String inputFormatType = U.getOption(pArgs, "--inputFormat=", FrameworkReaders.TEXT_READER_);

    SingleReader = (Reader) FrameworkReaders.createSingletonReader(inputFormatType, pArgs);

    for (int serverNumber = 0; serverNumber < this.numberOfServers; serverNumber++)
      applications.get(serverNumber).attachReader(SingleReader);

    return SingleReader;
  } // End Method createReader() ======================

  // =======================================================
  /**
   * addWriter adds a writer to each of the servers. A new writer gets created
   * for each server.
   *
   * @param pArgs the args
   */
  // =======================================================
  public void addWriters(String[] pArgs) {

    this.args = pArgs;

    for (int serverNumber = 0; serverNumber < this.numberOfServers; serverNumber++) {

      // ------------------
      // Create a writers to write out the processed cas's (write out xmi, vtt
      // files, stat file, and concordance file)
      // ------------------
      applications.get(serverNumber).addWriters(pArgs);
      applications.get(serverNumber).addStaticWriters(pArgs);

    } // end loop thru applications

  } // End Method addWriter() ======================

  // ==========================================
  /**
   * addStaticWriter .
   *
   * @param pWriterType the writer type
   * @param pArgs the args
   */
  // ==========================================
  public void addStaticWriter(int pWriterType, String[] pArgs) {

    this.args = pArgs;

    for (int serverNumber = 0; serverNumber < this.numberOfServers; serverNumber++) {

      applications.get(serverNumber).addStaticWriter(pWriterType, pArgs);

    } // end loop thru applications

  } // end Method addStaticWriter() ========================

  // =======================================================
  /**
   * setOutputDir sets the output directory. This enables the performance meter
   * files to be put into ./logs and the ./stats and ./eval dirs to be created
   * if need be
   *
   * @param pOutputDir the output dir
   */
  // =======================================================
  public void setOutputDir(String pOutputDir) {
    this.outputDir = pOutputDir;
  } // End Method setOutputDir() ======================

  // =======================================================
  /**
   * setMetric sets the metric for how many processed before the performance
   * meter tells.
   *
   * @param pMetric the metric
   */
  // =======================================================
  public void setMetric(int pMetric) {
    this.metric = pMetric;
  } // End Method setMetric() ======================

  // =======================================================
  /**
   * setPipelines sets the pipelines.
   *
   * @param pipelineClass the pipeline class
   * @param pArgs the args
   * @throws Exception the exception
   */
  // =======================================================
  public void setPipelines(Class<?> pipelineClass, String[] pArgs) throws Exception {

    this.pipelineClass = pipelineClass;
    this.pipelineArgs = pArgs;

    for (int serverNumber = 0; serverNumber < this.numberOfServers; serverNumber++) {
      FrameworkBaselineApplication anApplication = this.applications.get(serverNumber);
      setPipeline(pipelineClass, pArgs, anApplication);

    } // end loop thru applications

  } // End Method setPipelines() ======================

  // =======================================================
  /**
   * setPipeline .
   *
   * @param pPipelineClass the pipeline class
   * @param pArgs the args
   * @param pApplication the application
   * @throws Exception the exception
   */
  // =======================================================
  private void setPipeline(Class<?> pPipelineClass, String[] pArgs,
    FrameworkBaselineApplication pApplication) throws Exception {
    // -------------------
    // Create an engine with a pipeline, attach it to the application
    // -------------------
    Object aPipeline = pPipelineClass.newInstance();
    ((AbstractPipeline) aPipeline).setArgs(pArgs);

    AnalysisEngine ae = ((AbstractPipeline) aPipeline).getAnalysisEngine();
    pApplication.setAnalsyisEngine(ae);
    System.err.println("Set the pipeline for " + pipelineClass.getName());
  } // End Method setPipeline() ======================

  // =======================================================
  /**
   * run processes all the input records.
   *
   * @throws Exception the exception
   */
  // =======================================================
  public void run() throws Exception {
    run(1);
  } // End Method run() ======================

  // =======================================================
  /**
   * runApplication kicks off a single application .
   *
   * @param pServerNumber the server number
   */
  // =======================================================
  private void runApplication(int pServerNumber) {

    // --------------------------------
    // Kick off a server (never dies)
    try {

      Thread t = new Thread(this.applications.get(pServerNumber));
      System.err.println(" kicking off thread " + pServerNumber + " "
          + this.applications.get(pServerNumber).getServerName());
      t.start();
      // applications[serverNumber].process(); // <------ the non-treaded way of
      // doing this

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with kicking off the server " + e.getMessage();
      System.err.println(msg);
      meter.stop(msg);
      return;

    }

  } // end Method runApplication() ---------------------------

  // =======================================================
  /**
   * runApplication kicks off a single application .
   *
   * @param pApplication the application
   */
  // =======================================================
  private void runApplication(FrameworkBaselineApplication pApplication) {

    // --------------------------------
    // Kick off a server (never dies)
    try {

      String serverName = pApplication.getServerName();
      Thread t = new Thread(pApplication);
      System.err.println(" kicking off thread " + serverName);
      t.start();
      // applications[serverNumber].process(); // <------ the non-treaded way of
      // doing this

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with kicking off the server " + e.getMessage();
      System.err.println(msg);
      meter.stop(msg);
      return;

    }

  } // end Method runApplication() ---------------------------

  // =======================================================
  /**
   * run until numberOfProcesses are processed.
   *
   * @param pNumberToProcess the number to process
   * @throws Exception the exception
   */
  // =======================================================
  public void run(int pNumberToProcess) throws Exception {

    run(pNumberToProcess, 1, 1, 100, 100000, 60);

  } // End Method run() ======================

  // =======================================================
  /**
   * run - start with 1 thread. Let it run for a while. Test to see if the
   * machine load is less than some threshold. If not, add an additional thread.
   * Report the number of threads employed, and the amount of memory used.
   *
   * @param pNumberToProcess the number to process
   * @param pInitialNumberOfServers the initial number of servers
   * @param pMaxNumberOfServers the max number of servers
   * @param pMaxSystemLoad the max system load
   * @param recycleAt the recycle at
   * @param pSeconds (how many seconds to wait before waking up and reporting
   *          and seeing when to recycle
   * @throws Exception the exception
   */
  // =======================================================
  public void run(long pNumberToProcess, int pInitialNumberOfServers, int pMaxNumberOfServers, int pMaxSystemLoad, int recycleAt, int pSeconds) throws Exception {

    try {
      // ---------------------
      // Kick off the initial number of applications
      int serverNumber = 0;
      int numberOfActiveServers = 0;
      int killedServers = 0;
      this.numberToProcess = pNumberToProcess;
      int numberOfServers = this.applications.size();
      for (serverNumber = 0; serverNumber < numberOfServers; serverNumber++) {
        runApplication(serverNumber);
        System.err.println("running  application " + serverNumber);
        numberOfActiveServers++;
      }
      this.initializationMeter.stop("Finished-sh initialization ");
      this.meter.begin();

      // ----------------------------
      // Gotta wait until some trigger here
      // ----------------------------
      boolean done = false;
      Progress[] progress = SingleReader.getProgress();
      long totalFiles = progress[0].getTotal();
      if (pNumberToProcess > 1)
        totalFiles = pNumberToProcess;

      System.err.println("===============================================");
      System.err.println("========= Going to process " + totalFiles + " ==============");
      System.err.println("================================================");

      while (!done) {

        progress = SingleReader.getProgress();

        long completed = progress[0].getCompleted();
       //  System.err.println("  here's what's been completed " + completed);

        boolean doneness = testForDoneness(applications, numberOfServers, completed, totalFiles);

        if (doneness && SingleReader.allProcessed() ) {

          for (int k = 0; k < numberOfServers; k++) {

            String msg = "Doneness passed. About to kill server " + k + "\n";
            meter.mark(msg);
            System.err.print(msg);
            applications.get(k).destroyGentle(msg);
            System.err.println("Killing service " + applications.get(killedServers).getServerName());
           
          }
          done = true;

        } else {

          numberOfActiveServers = getNumberOfActiveServers(applications);
          int cpuLoad = meter.getSystemCPULoad();
          long memoryUsed = meter.getMemoryUsage();
          String time = meter.avgAux((int)completed);
          String msg = "The number of active servers = " + numberOfActiveServers + "|time=" + time + "|memoryUsed="
              + memoryUsed + "|load = " + cpuLoad + "| maxSystemLoad =" + pMaxSystemLoad
              + " |max number of servers = " + pMaxNumberOfServers + " |the number processed = "
              + completed;
          System.err.println(msg);

          if (completed % meter.getMetric() == 0)
            meter.avg((int) completed, numberOfActiveServers);

          // -----------------------------
          // test the load on the machine
          // If the load is below the threshold
          // kick off another server

          if ((cpuLoad < pMaxSystemLoad) && (numberOfActiveServers < pMaxNumberOfServers)) {
            serverNumber++;
            spinUpNewApplication(serverNumber, cpuLoad, true);

          } else if (numberOfActiveServers > pMaxNumberOfServers) {
            // --------------------------
            // The nice factor: if this is over a threashold kill off a thread
            msg = "Killing a server because the load is over " + pMaxSystemLoad
                + " and the number of active servers is greater than " + pMaxNumberOfServers + "\n";
            System.err.print(msg);
            meter.mark(msg);
            applications.get(killedServers).destroyGentle(msg);
            System.err.println("Killing service " + applications.get(killedServers).getServerName());
            killedServers++;
            numberOfActiveServers--;

          }

         Thread.sleep(pSeconds * 100);
          
        } // end if

      } // end !done;

      String msg = "Think I'm done here\n";
      // System.err.print(msg);
      meter.mark(msg);
      progress = SingleReader.getProgress();
      long total = progress[0].getTotal();
      
      // ------------------------
      // Need to let all the existing threads die here before proceeding
      // ------------------------
     while (!SingleReader.allProcessed()) {
        int z = 0;
        System.err.println("Sleeeping until last surviving thread is done " + z++);
        Thread.sleep(pSeconds * 100);
     }

      // ------------------------
      // Fail safe - retrieve the residual records not processed
      if (!SingleReader.allProcessed()) {
        msg = "The reader says there is more to be proccessed\n";
        System.err.print(msg);
        meter.mark(msg);

        int lastRecordsToProcess[] = SingleReader.whatIsNotProcessed();

        if (lastRecordsToProcess != null && lastRecordsToProcess.length > 0) {

          msg = "There are " + lastRecordsToProcess.length + " to be processed\n";
          System.err.print(msg);
          meter.mark(msg);
          // -----------------------------
          // Spin up one final thread to
          // process this residual
          serverNumber++;
          FrameworkBaselineApplication newApplication = spinUpNewApplication(serverNumber, meter.getSystemCPULoad(), false);
          newApplication.process(lastRecordsToProcess);
        }
        msg = "Now all should be done " + SingleReader.allProcessed() + "\n";
        System.err.print(msg);
        meter.mark(msg);
      }

      msg = " have all the records been processed ? " + SingleReader.allProcessed() + "\n"
          + " Finished killing all threads about to go into master thread's destroy \n";
      System.err.print(msg);
      meter.mark(msg);
      this.destroy();

      msg = "Finished = processed " + total + "\n";
      System.err.print(msg);
      meter.stop(msg);

      msg = meter.avgAux((int) total) + "\n";
      System.err.print( msg );
      meter.stop(msg);

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with the scale-up server " + e.toString();
      System.err.println(msg);
      throw new Exception(msg);
    }
  } // End Method run() ======================

  // =======================================================
  /**
   * spinUpNewApplication.
   *
   * @param serverNumber the server number
   * @param pCPULoad the CPU load
   * @param pRun the run
   * @return the framework baseline application with static writers
   * @throws Exception the exception
   */
  // =======================================================
  private FrameworkBaselineApplicationWithStaticWriters spinUpNewApplication(int serverNumber, int pCPULoad, boolean pRun) throws Exception {
   
    FrameworkBaselineApplicationWithStaticWriters newApplication = initialize(serverNumber, this.recycleAt);
    setPipeline(this.pipelineClass, this.pipelineArgs, newApplication);
    newApplication.attachReader(SingleReader);

    newApplication.addWriters(this.args);
    newApplication.addStaticWriters(this.args);

    if (pRun)
      runApplication(newApplication);
    meter.mark("Added an additional server - server number " + serverNumber
        + " because the load is " + pCPULoad);

    return newApplication;
  } // End Method spinUpNewApplication() ======================

  // =======================================================
  /**
   * getNumberOfActiveServers returns the number of active servers.
   *
   * @param applications2 the applications 2
   * @return int
   */
  // =======================================================
  private synchronized int getNumberOfActiveServers(
    ArrayList<FrameworkBaselineApplicationWithStaticWriters> applications2) {

    int numberOfActiveServers = 0;

    if (applications2 != null)
      for (int i = 0; i < applications2.size(); i++) {
        if (applications2.get(i).isAlive())
          numberOfActiveServers++;
      }

    return numberOfActiveServers;
  } // End Method getNumberOfActiveServers() ======================

  // =======================================================
  /**
   * testForDoneness returns true if the number completed is >= total Files, or
   * all the applications are dead.
   *
   * @param applications2 the applications 2
   * @param pNumberOfServers the number of servers
   * @param completed the completed
   * @param totalFiles the total files
   * @return boolean true if all the completed >= totalFiles OR all threads are
   *         done or killed
   */
  // =======================================================
  private synchronized boolean testForDoneness(
                                        ArrayList<FrameworkBaselineApplicationWithStaticWriters> applications2, 
                                        int pNumberOfServers,
                                        long completed, 
                                        long totalFiles) {

    boolean doneness = true;

    if (completed  < totalFiles )
      doneness = false;
    /*
     * boolean t = true; for ( int i = 0; i < pNumberOfServers; i++ ) { if (
     * pApplications.get(i).isAlive() ) { t = false; break; } } //
     * ---------------------- // If all the threads are dead, but you've not
     * finished all the the records send back that you are done anyway. if (
     * doneness == false && t == true) doneness = true;
     */
    return doneness;
  } // End Method testForDoneness() ======================

  // =======================================================
  /**
   * getMainPerformanceMeter.
   *
   * @return the main performance meter
   */
  // =======================================================
  public PerformanceMeter getMainPerformanceMeter() {
    return this.meter;

  } // End Method getMainPerformanceMeter() ======================

  // =======================================================
  /**
   * destroy does the things at the end of a scaled-out application .
   */
  // =======================================================
  public void destroy() {

    try {
      FrameworkBaselineApplicationWithStaticWriters.staticDestroy();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with static destroy " + e.toString());
    }
    ScaleOutAnnotatorPerformanceStats.analyze(this.logDir);

  } // End Method destroy() ======================

} // end Class ScaleOutApplicaiton ------------------------
