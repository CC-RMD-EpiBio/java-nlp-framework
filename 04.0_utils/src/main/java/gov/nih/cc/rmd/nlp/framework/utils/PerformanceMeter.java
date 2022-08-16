/*
 *
 */
/**
 * PerformanceMeter.java Summary
 *  Class Detail
 *
 *
 * @author  Guy Divita 
 * @created Feb 29, 2012
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

//import org.apache.log4j.Logger;

import com.sun.management.OperatingSystemMXBean;
import com.sun.management.UnixOperatingSystemMXBean;

/**
 * The Class PerformanceMeter.
 */

public class PerformanceMeter {

  /** The total load. */
  private int totalLoad = 0;

  /** The cpu load ctr. */
  private int cpuLoadCtr = 1;

  // -----------------------------------------
  /**
   * Constructor: PerformanceMeter - This constructor sends all messages to the
   * bit bucket.
   */
  // -----------------------------------------
  public PerformanceMeter() {
    NullOutputStream nullOutputStream = new NullOutputStream();
    this.out = GLog.getErr();
   //  this.out = new PrintWriter(nullOutputStream);
    rt = Runtime.getRuntime();
    // this.begin(); <---- always called explicitly
  }

  // -----------------------------------------
  /**
   * Constructor: PerformanceMeter.
   *
   *  will set where the performance meter sends its output to
   *  if there is none already set.ou
   * @param pStream the stream
   */
  // -----------------------------------------
  public PerformanceMeter(PrintStream pStream) {
    
    if ( this.out == null )
      this.out = new PrintWriter(pStream);
    rt = Runtime.getRuntime();
    this.begin();
  }

  /**
   * Returns the out.
   *
   * @return PrintWriter the out
   */
  public PrintWriter getOut() {
    return out;
  }

  /**
   * Sets the out.
   *
   * @param out the out to set
   */
  public void setOut(PrintWriter out) {
    this.out = out;
  }

  // -----------------------------------------
  /**
   * Constructor using a printWriter rather than a printStream.
   *
   * @param errLog the err log
   */
  // -----------------------------------------
  public PerformanceMeter(PrintWriter errLog) {
    rt = Runtime.getRuntime();
    this.out = errLog;
    this.begin();

  } // End Constructor() -----------------------------

  // =======================================================
  /**
   * Constructor PerformanceMeter creates a performance meter and opens a log
   * file to report findings to.
   *
   * @param pLogFileName the log file name
   */
  // =======================================================
  public PerformanceMeter(String pLogFileName) {
    try {
      String dir = U.getDirFromFileName(pLogFileName);
      try {
        U.mkDir(dir);
      } catch (Exception e) {
        e.printStackTrace();
        String msg = "Issue with making the dir for the performance log file " + e.getMessage();
        GLog.println(GLog.ERROR_LEVEL, msg);
        throw new RuntimeException(msg);
      }

      this.out = new PrintWriter(pLogFileName);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      String msg = "Issue with setting up the performance meter log file " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, msg);
      throw new RuntimeException(msg);
    }

    rt = Runtime.getRuntime();
    this.begin();
  }

  // -----------------------------------------
  /**
   * begin.
   */
  // -----------------------------------------
  public void begin() {
    begin("");

  }

  // -----------------------------------------
  /**
   * begin.
   *
   * @param pMessage the message
   */
  // -----------------------------------------
  public void begin(String pMessage) {
    
    this.start_time = System.currentTimeMillis(); 
    String d = new Date( this.start_time).toString();
   // String t = this.formatTime( this.start_time);
   
   long memoryUsage = getMemoryUsage();
   String msg = null;
   
   msg = "========================================================\n" + 
         "= Start: " + d  + "|InitialMemory|" + memoryUsage + "|=\n" + 
         "========================================================\n";
   System.out.flush();
   System.err.flush();
   
   
   this.out.print(msg );
   GLog.println(GLog.DEBUG_LEVEL, msg);
   this.out.flush();
//   if ( this.logger != null)
 //    this.logger.info(msg);
   
  } // end Method begin() --------------------

  // -----------------------------------------
  /**
   * mark.
   *
   * @param pMessage the message
   * @return String
   */
  // -----------------------------------------
  public String mark(String pMessage) {

    long currentCount = System.currentTimeMillis();
    long current = currentCount - this.start_time;

    String c = formatTime(current);

    long memoryUsage = getMemoryUsage();
    int systemLoad = getSystemCPULoad();
    int filesOpen = getFilesOpen();

    this.tellMsg = "===============================\n" + "=|ElapsedTime=" + c + "|Memory Used="
        + memoryUsage + "|SystemCPULoad=" + systemLoad + "|" + "|filesOpen=" + filesOpen + "|"
        + pMessage + "\n" + "===============================\n";

    this.out.print(this.tellMsg);
    this.out.flush();
    GLog.println(GLog.DEBUG_LEVEL, tellMsg);
//    GLog.println(GLog.STD___LEVEL, tellMsg);
    // if ( this.logger != null)
    // this.logger.info(this.tellMsg);

    return this.tellMsg;
  } // end Method mark() ---------------------

  // ==========================================
  /**
   * getFilesOpen attempts to retrieve the number of open file handles
   * 
   * (This only works when run on linux platform).
   *
   * @return int
   */
  // ==========================================
  private int getFilesOpen() {

    int returnVal = 0;
    OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    if (os instanceof UnixOperatingSystemMXBean) {
      returnVal = (int) ((UnixOperatingSystemMXBean) os).getOpenFileDescriptorCount();
    }

    return returnVal;
  } // end Method getFilesOpen() ========================================

  // =======================================================
  /**
   * getSystemLoad retrieves the system load.
   *
   * @return String
   */
  // =======================================================
  public String getSystemCPULoadObs() {
    String load = "N/A";
    OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    double doubleLoad = osBean.getSystemCpuLoad();
    load = String.valueOf(Math.round(doubleLoad * 100));
    return load;
  } // End Method getSystemLoad() ======================

  // =======================================================
  /**
   * getSystemLoad retrieves the system load.
   *
   * @return int
   */
  // =======================================================
  public int getSystemCPULoad() {
    int load = 0;
    OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    double doubleLoad = osBean.getSystemCpuLoad(); // < sys admins would like us
                                                   // to use system load average
                                                   // rather than cpu load

    // double doubleLoad = osBean.getSystemLoadAverage(); // this gives
    // nonsensical numbers.
    load = (int) Math.round(doubleLoad * 100);
    return load;
  } // End Method getSystemLoad() ======================

  // =======================================================
  /**
   * processedAnother [Summary here].
   */
  // =======================================================
  public void processedAnother() {

    DecimalFormat df = new DecimalFormat("#.##");
    if (this.numberProcessed % this.metric == 0) {

      long endCount = System.currentTimeMillis();
      long ec = endCount - this.start_time;

      double avgPerFile = ec / this.numberProcessed + 0.0000001;
      int systemCPULoad = getSystemCPULoad();
      String avgPerFile2 = df.format(avgPerFile);
      String msg = "Files processed = " + this.numberProcessed + "|Avg time per file = |"
          + avgPerFile2 + "|SystemCPULoad = " + systemCPULoad;

      this.mark(msg);
    }
    this.numberProcessed++;

  } // End Method processedAnother() ======================

  // =======================================================
  /**
   * avg.
   *
   * @param pNumProcessed the num processed
   * @return String
   */
  // =======================================================
  public String avg(int pNumProcessed) {

    return avg(pNumProcessed, 0);

  } // End Method avg() ======================

  // =======================================================
  /**
   * avg.
   *
   * @param pNumProcessed the num processed
   * @param pNumberOfThreads the number of threads
   * @return String
   */
  // =======================================================
  public String avg(int pNumProcessed, int pNumberOfThreads) {

    long endCount = System.currentTimeMillis();
    long totalTimeInMillisecs = endCount - this.start_time;
    double avgPerFileInMilliSec = totalTimeInMillisecs / (pNumProcessed + 0.000001);
    int systemCPULoad = getSystemCPULoad();
    this.totalLoad = this.totalLoad + systemCPULoad;
    double avgLoad = this.totalLoad / this.cpuLoadCtr++;
    String msg = "Files processed = " + pNumProcessed + "|Avg time per file = |"
        + avgPerFileInMilliSec + "|SystemCPULoad = " + systemCPULoad + "|Avg Load =" + avgLoad
        + "|Number of threads=" + pNumberOfThreads;

    this.mark(msg);

    return msg;

  } // End Method avg() ======================
  
//=======================================================
 /**
  * avgAux returns the full time spent and the avg for the num processed
  * in the format "total time (ms)=xxxxxx|Avg time (ms) per file=yyyyy"
  *
  * @param pNumProcessed the num processed
  * @return String
  */
 // =======================================================
 public String avgAux(int pNumProcessed ) {

   long endCount = System.currentTimeMillis();
   long totalTimeInMillisecs = endCount - this.start_time;
   double avgPerFileInMilliSec = totalTimeInMillisecs / (pNumProcessed + 0.000001);
  
   String msg = "Total time (ms)=" + totalTimeInMillisecs + "|Avg time (ms) per file=" + avgPerFileInMilliSec ;

   this.mark(msg);

   return msg;

 } // End Method avg() ======================

  // =======================================================
  /**
   * avg.
   *
   * @return String
   */
  // =======================================================
  public String avg() {

    String returnVal = avg(this.numberProcessed);

    return returnVal;
  } // End Method avg() ======================

  // =======================================================
  /**
   * getMemoryUsage returns the memory usage in kbytes.
   *
   * @return long
   */
  // =======================================================
  public long getMemoryUsage() {
    long memoryUsage = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
    return memoryUsage;
  } // End Method getMemoryUsage() ======================

  // =======================================================
  /**
   * getTotalTimeInHours returns how long this meter has been alive in hours.
   *
   * @return int
   */
  // =======================================================
  public int getTotalTimeInHours() {
    int hours = 0;
    long endCount = System.currentTimeMillis();
    long ec = endCount - this.start_time;
    hours = (int) TimeUnit.MILLISECONDS.toHours(ec);

    return hours;
  } // End Method getTotalTimeInHours() ======================

  // -----------------------------------------
  /**
   * stop.
   */
  // -----------------------------------------
  public void stop() {
    stop("Finished ");

  }

  // -----------------------------------------
  /**
   * stop.
   *
   * @param pMsg the msg
   */
  // -----------------------------------------
  public void stop(String pMsg) {
    long endCount = System.currentTimeMillis();
    long ec = endCount - this.start_time;
    String e = formatTime(ec);
    long memoryUsage = getMemoryUsage();
    double avgPerFile = ec / this.numberProcessed;
    int systemCPULoad = getSystemCPULoad();

    String avgPerFileString = formatTime((int) (Math.round(avgPerFile)));

    this.tellMsg = "=============================================================================\n"
        + "= " + pMsg + "\n" + "= ElapsedTime " + e + "\n" + "= Memory Used                      = "
        + memoryUsage + "\n" + "= Files processed                  = " + this.numberProcessed + "\n"
        + "= Avg Time per file                = " + avgPerFileString + "\n"
        + "= SystemCPULoad                    = " + systemCPULoad + "\n"
        + "============================================================================\n";
    if (this.out != null) {
      this.out.print(this.tellMsg);
      this.out.flush();
      GLog.println(GLog.DEBUG_LEVEL, this.tellMsg);
    }

  }

  // ------------------------------------------
  /**
   * tell.
   *
   * @return String
   */
  // ------------------------------------------
  public String tell() {

    return this.tellMsg;

    // End Method tell() -----------------------
  }

  // ------------------------------------------
  /**
   * getNumberProcessed.
   *
   * @return int
   */
  // ------------------------------------------
  public int getNumberProcessed() {

    return this.numberProcessed;

  } // End Method getNumberProcessed() --------

  // ------------------------------------------
  /**
   * setMetric sets the interval between tells.
   *
   * @param pMetric the metric
   */
  // ------------------------------------------
  public void setMetric(int pMetric) {

    this.metric = pMetric;

  } // End Method setMetric() -----------------------

  // ------------------------------------------
  /**
   * getMetric gets the interval between tells.
   *
   * @return int
   */
  // ------------------------------------------
  public int getMetric() {

    return this.metric;

  } // End Method getMetric() -----------------------

  // -----------------------------------------
  /**
   * close.
   */
  // -----------------------------------------
  public void close() {
    if (this.out != null) {
      this.out.close();
      this.out = null;
    }

  } // end Method close() ------------------

  // -----------------------------------------
  /**
   * finally.
   */
  // -----------------------------------------
  @Override
  public void finalize() {
    close();

  }

  // -----------------------------------------
  /**
   * formatTime.
   *
   * @param time the time
   * @return String
   */
  // -----------------------------------------
  public static String formatTime(long time) {

    // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd
    // HH:mm:ss");

    String t = String.format("TotalMilliseconds: %d = %d min, %d sec", time,
        TimeUnit.MILLISECONDS.toMinutes(time), TimeUnit.MILLISECONDS.toSeconds(time)
            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    return t;
  }

  /** The tell msg. */
  private String tellMsg = null;

  // ------------------
  // Class variables
  /** The out. */
  // -----------------
  private PrintWriter out = null;

  /** The start time. */
  // private Logger logger;
  private long start_time = 0;

  /** The rt. */
  private Runtime rt = null;

  /** The number processed. */
  private int numberProcessed = 1;

  /** The metric. */
  private int metric = 10;

  /** The begin counter time. */
  long beginCounterTime = 0;

  /** The current amount of time. */
  long currentAmountOfTime = 0;

  /**
   * Write profile.
   *
   * @param pMessage the message
   */
  public synchronized void writeProfile(String pMessage) {
    if (this.out != null) {
      String msg = pMessage + "|" + this.currentAmountOfTime + "\n";
      this.out.print(msg);

      // GLog.println(GLog.STD___LEVEL, msg);
      this.out.close();
    }
    this.out = null;
  } // End Method write() ======================

  /**
   * Start counter.
   */
  public void startCounter() {
    this.beginCounterTime = System.currentTimeMillis();

  } // End Method startCounter() ======================

  /**
   * Stop counter.
   *
   * @param pMsg the msg
   */
  public void stopCounter(String pMsg) {
    if (pMsg != null && this.out != null) {
      this.out.print(pMsg);
      this.out.flush();

    }
    this.stopCounter();

  } // End Method stopCounter() ======================

  /**
   * Stop counter.
   */
  public void stopCounter() {
    this.currentAmountOfTime += System.currentTimeMillis() - this.beginCounterTime;

  } // End Method stopCounter() ======================

} // end Class PerformanceMeter
