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
 * GLog.java is my attempt at logging that won't interfere
 * with other loggers.
 *
 * @author  divita
 * @created Jan 13, 2016
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils;

import java.io.File;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * The Class GLog.
 *
 * @author Divita
 */
public final class GLog {

  /** Class fields. */
  public static final int ALL_LEVEL = 0;

  /** The Constant TRACE_LEVEL. */
  public static final int TRACE_LEVEL = 1;

  /** The Constant DEBUG_LEVEL. */
  public static final int DEBUG_LEVEL = 2; // print only when debug level is on

  /** The Constant STD___LEVEL. */
  public static final int STD___LEVEL = 3; // print always

  /** The Constant INFO_LEVEL. */
  public static final int INFO_LEVEL = 4; //

  /** The Constant WARNING_LEVEL. */
  public static final int WARNING_LEVEL = 5;

  /** The Constant ERROR_LEVEL. */
  public static final int ERROR_LEVEL = 6; // print when there is an error

  /** The Constant OFF_LEVEL. */
  public static final int OFF_LEVEL = 7;

  /** The debug level. */
  private static boolean debug_level = false;

  /** The error level. */
  private static boolean error_level = true;

  /** The std level. */
  private static boolean std_level = true;

  /** The info level. */
  private static boolean info_level = true;

  /** The warning level. */
  private static boolean warning_level = true;

  /** The trace level. */
  private static boolean trace_level = false;

  /** The all level. */
  private static boolean all_level = false;

  /** The off level. */
  private static boolean off_level = false;

  /** The log to console too. */
  @SuppressWarnings("unused")
  private static boolean printToConsole = true;

  // -------------------------------
  // Class variables
  /** The log dir. */
  // -------------------------------
  private static String _logDir = null;

 

 

  /** The log 4 J conversion pattern. */
  private static String log4JConversionPattern = "%d %-4r [thread %t] %-5p %c%m%n";

  /** The null print writer. */
  private static PrintWriter logWriter = null;

  /** The print to log. */
  private static boolean printToLog = false;
  private static HashMap<String, boolean[]> ClassLoggingHash = null;

  // =================================================
  /**
   * setLogWriter
   *
   * @param pWriter
   * 
  **/
  // =================================================
  public static final PrintWriter setLogWriter (PrintWriter pWriter) {
    logWriter  = pWriter;
    return logWriter;
  } // end Method setLogWriter() --------------------
  
  // =================================================
  /**
   * getLogWriter
   *
   * @return pWriter
   * 
  **/
  // =================================================
  public static final PrintWriter getWriter () {
    return logWriter;
  } // end Method getLogWriter() --------------------

  // =================================================
  /**
   * setLogDir sets the log dir. This method will create the path to the logdir
   * if it does not yet exist.  If the log writer is already open then no problem, 
   * more will go to that log
   *
   *
   * @param pLogDir (full path to the log dir)
   * @return PrintWriter 
   * @throws Exception the exception
   */
  // =================================================
  public final static PrintWriter setLogDir(String pLogDir) throws Exception {

    _logDir = pLogDir;
    String fullFileName = null;

    try {
      if (printToLog) {
        if ( logWriter == null ) {
           U.mkDir(_logDir);
           fullFileName = getFileName();
           logWriter = new PrintWriter( fullFileName);
        }
      } else {
      
        NullOutputStream nullOutputStream = new NullOutputStream();
        logWriter = new PrintWriter (nullOutputStream );
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with setting the log dir and opening the log file " + fullFileName + " " + e.toString());
    }
    return logWriter;

  } 

  // =================================================
  /**
   * println writes out the message to the log file and to std-err. There is a
   * flush that happens to ensure the logfile gets this message.
   *
   * @param pMsg the msg
   */
  // =================================================
  public final static void println(String pMsg) {

    println(STD___LEVEL, pMsg);

  } // End Method println() ======================

  // =================================================
  /**
   * println prints the message depending on what the print category is, and if
   * the category_status flag has been set.
   *
   * @param pPrintCategory the print category
   * @param pMsg the msg
   */
  // =================================================
  public final static String println(int pPrintCategory, String pMsg) {

    String returnVal = pMsg;
    switch (pPrintCategory) {
      case DEBUG_LEVEL:
      case TRACE_LEVEL:
        returnVal = debug_println(pMsg);
        break;
      case ERROR_LEVEL:
        returnVal = error_println(pMsg);
        break;
      case STD___LEVEL:
      default:
        returnVal = std_println(pMsg);
        break;

    }
    
    return returnVal;
  } // end Method println() ---------------------------

  // =================================================
  /**
   * println prints the message depending on what the print category is, and if
   * the category_status flag has been set.
   *
   * @param pPrintCategory the print category
   * @param pCallingClass the calling class
   * @param pCallingMethod the calling method
   * @param pMsg the msg
   * @return String
   */
  // =================================================
  public final static String println(int pPrintCategory, Class<?> pCallingClass, String pCallingMethod, String pMsg) {

    
    String returnVal = pMsg;
    boolean levels[] = null;
    if ( ClassLoggingHash != null )
      levels = ClassLoggingHash.get(pCallingClass.getName() );
    
    if ( levels != null ) {
      if ( levels[OFF_LEVEL])
        ;
      else if (levels[ALL_LEVEL])
        std_println(pCallingClass, pCallingMethod, pMsg);
      else if (levels[ DEBUG_LEVEL] || levels[TRACE_LEVEL] )
        debug_println(pCallingClass, pCallingMethod, pMsg);
      else if (levels[ERROR_LEVEL] )
        error_println(pCallingClass, pCallingMethod, pMsg);
      else if (levels[WARNING_LEVEL] )
        warn_println(pCallingClass, pCallingMethod, pMsg);
     else if (levels[STD___LEVEL] || levels[INFO_LEVEL] )
       std_println(pCallingClass, pCallingMethod, pMsg);
       
    } else 
    
      
    switch (pPrintCategory) {
      
      case OFF_LEVEL: 
        break;
      case ALL_LEVEL:
        returnVal = std_println(pCallingClass, pCallingMethod, pMsg);
        break;
      case DEBUG_LEVEL:
      case TRACE_LEVEL:
        returnVal = debug_println(pCallingClass, pCallingMethod, pMsg);
        break;
      case ERROR_LEVEL:
        returnVal = error_println(pCallingClass, pCallingMethod, pMsg);
        break;
      case WARNING_LEVEL:
        returnVal = warn_println(pCallingClass, pCallingMethod, pMsg);
        break;
      
      case STD___LEVEL:
      case INFO_LEVEL:
      default:
        returnVal = std_println(pCallingClass, pCallingMethod, pMsg);
        break;

    }
    
    return returnVal;
  } // end Method println() ---------------------------

  // =================================================
  /**
   * debug_println prints the calling method plus the message.
   *
   * @param pCallingClass the calling class
   * @param pCallingMethod the calling method
   * @param pMsg the msg
   * @return String
   */
  // =================================================
  public final static String debug_println(Class<?> pCallingClass, String pCallingMethod, String pMsg) {

    String returnVal = pMsg;
    String className = "           ";
    String msg = pMsg;

    if (msg != null) {
      String[] rows = U.split(msg, "\n");
      for (String row : rows) {
        if ( pCallingClass != null )
          className = pCallingClass.getName();
          
        returnVal = printToLog(className + "." + pCallingMethod + "--> " + row);
      }
    }
    
    return returnVal;

  } // end Method debug_println() ---------------------

  // =================================================
  /**
   * printToLog is a last resort method to get !#$!$ print statements to a log
   * without having a process co-opt the logger and silence statements that are
   * being looked for for debugging purposes.
   * 
   * This method is triggered on by --printToLog=true passed in with the setLog
   *
   * @param pLogMsg the log msg
   * @return String
   */
  // =================================================
  public final static String printToLog(String pLogMsg) {

    
    StringBuffer msg = new StringBuffer();
    msg.append( pLogMsg);
    msg.append(  "\n");
    String buff = msg.toString();
    if ( printToConsole ) {
      System.err.print( buff );
      System.err.flush();
    }
    
    
    if (printToLog) {
      checkNullPrintWriter();
      logWriter.print(buff);
      logWriter.flush();
      
     
    }
    
    return buff;
  } // end Method printToLog() ----------------------

  // =================================================
  /**
   * checkNullPrintWriter checks to see if the logWriter is null. If so,
   * it checks to see if the logfile exists, if it does, it changes the log file
   * name to .appended It opens a new printWriter with the fileName.
   */
  // =================================================
  private final static void checkNullPrintWriter() {

    if (logWriter == null) {
      String fileName = getFileName();
      File aFile = new File(fileName);
      if (aFile.exists()) {
        fileName = fileName + ".appended";
        try {
          logWriter = new PrintWriter(fileName);
        } catch (Exception e) {
          e.printStackTrace();
          System.err.println("Could not open an appended log file " + fileName);
        }
      }
    }

  } // end Method setNullPrintWriter() ---------------

  // =================================================
  /**
   * debug_println prints the calling method plus the message.
   *
   * @param pMsg the msg
   * @return String
   */
  // =================================================
  public final static String debug_println(String pMsg) {

    String returnVal = pMsg;
    Throwable t = new Throwable();
    StackTraceElement[] elements = t.getStackTrace();
    int i = 1;
    if (elements.length > 3)
      i = 3;

    // String calleeMethod = elements[0].getMethodName();
    String callerMethodName = elements[i].getMethodName();
   //  Class<?> callerClass = elements[i].getClass();

    returnVal = debug_println(null, callerMethodName, pMsg);
    
    return returnVal;
    
  } // end Method println() ---------------------------

  // =================================================
  /**
   * error_println prints the calling method plus the message.
   *
   * @param pCallingClass the calling class
   * @param pCallingMethod the calling method
   * @param pMsg the msg
   * @return String
   */
  // =================================================
  public final static String error_println(Class<?> pCallingClass, String pCallingMethod,
    String pMsg) {

    // attachAppender( pCallingClass);
    String returnVal = pMsg;
    String msg = pMsg;
    StringBuffer buff = new StringBuffer();
    if (msg != null) {
      String[] rows = U.split(msg, "\n");
      for (String row : rows) {
         buff.append(printToLog(pCallingClass + "." + pCallingMethod + "--> " + row) + "\n" );
      }
      returnVal = buff.toString();

    }
    
    return returnVal;
  } // end Method error_println() ---------------------
    // =================================================

  /**
   * error_println prints the calling method plus the message.
   *
   * @param pMsg the msg
   * @return String
   */
  // =================================================
  public final static String error_println(String pMsg) {

    String returnVal = pMsg;
    Throwable t = new Throwable();
    StackTraceElement[] elements = t.getStackTrace();
    int i = 1;
    if (elements.length > 2)
      i = 2;

    // String calleeMethod = elements[0].getMethodName();
    String callerMethodName = elements[i].getMethodName();
    Class<?> callerClass = elements[i
                                    ].getClass();

    returnVal = error_println(callerClass, callerMethodName, pMsg);
    
    return returnVal;

  } // end Method error_println() ---------------------------

  // =================================================
    /**
     * throwExceptionWithMsg prints a msg to the log and throws a new exception with the message.
     * 
     * @param pException
     * @param pClass
     * @param pMethodName
     * @param pMsg
     * @throws Exception
    */
    // =================================================
    public static void throwExceptionWithMsg(Exception e, Class<?> pClass, String pMethodName, String pMsg) throws Exception {
     e.printStackTrace();
      String msg = GLog.println(GLog.ERROR_LEVEL,pClass, pMethodName, pMsg) ;
      throw new Exception(msg);
    } // end Method throwExceptionWithMsg() -----------

  // =================================================
  /**
   * warn_println prints the calling method plus the message.
   *
   * @param pCallingClass the calling class
   * @param pCallingMethod the calling method
   * @param pMsg the msg
   * @return String
   */
  // =================================================
  public final static String warn_println(Class<?> pCallingClass, String pCallingMethod,
    String pMsg) {

    String returnVal = pMsg;
    // attachAppender( pCallingClass);
    String msg = pMsg;

    StringBuffer buff = new StringBuffer();
    if (msg != null) {
      String[] rows = U.split(msg, "\n");
      for (String row : rows) {
       buff.append( printToLog(pCallingClass + "." + pCallingMethod + "--> " + row) + "\n");
      }
      returnVal = buff.toString();
    }
    
    return returnVal;

  } // end Method error_println() ---------------------
    // =================================================

  /**
   * warn_println prints the calling method plus the message.
   *
   * @param pMsg the msg
   * @return String
   */
  // =================================================
  public final static String warn_println(String pMsg) {

    String returnVal = pMsg;
    Throwable t = new Throwable();
    StackTraceElement[] elements = t.getStackTrace();
    int i = 1;
    if (elements.length > 2)
      i = 2;

    // String calleeMethod = elements[0].getMethodName();
    String callerMethodName = elements[i].getMethodName();
    Class<?> callerClass = elements[i].getClass();

    returnVal = warn_println(callerClass, callerMethodName, pMsg);
    
    return returnVal;

  } // end Method error_println() ---------------------------

  // =================================================
  /**
   * std_println prints the calling method plus the message.
   *
   * @param pCallingClass the calling class
   * @param pCallerMethodName the caller method name
   * @param pMsg the msg
   * @return String
   */
  // =================================================
  public final static String std_println(Class<?> pCallingClass, String pCallerMethodName, String pMsg) {

    String returnVal = pMsg;
    String className = ("           ");
    if (std_level) {
      // attachAppender( pCallingClass);
      String msg = pMsg;

      if (msg != null) {
        
        if ( pCallingClass != null )
          className = pCallingClass.getName();
        String[] rows = U.split(msg, "\n");
        StringBuffer buff = new StringBuffer();
        for (String row : rows) {
          buff.append( printToLog(className + "." + pCallerMethodName + "--> " + row) + "\n" );
        }
        returnVal = buff.toString();
      }
    }
    return returnVal;
  } // end Method std_println() ---------------------------

  // =================================================
  /**
   * std_println prints the calling method plus the message.
   *
   * @param pMsg the msg
   * @return String
   */
  // =================================================
  public final static String std_println(String pMsg) {

    String returnVal = pMsg;
    if (std_level) {
      // attachAppender( GLog.class);

      String msg = pMsg;
      if (msg != null) {
        String[] rows = U.split(msg, "\n");
        StringBuffer buff = new StringBuffer();
        for (String row : rows)
          buff.append( printToLog("           ." + "--> " + row) + "\n" );
        returnVal = buff.toString();  
      }
    }
    
    return returnVal;

  } // end Method error_println() ---------------------------

  // =================================================
  /**
   * getFileName calculates what the filename should be (outputDir+ log +
   * date+thread)
   * 
   * This also creates an appender that can be retrieved based on the filename.
   *
   * @return the file name
   */
  // =================================================
  public final static String getFileName() {
    String threadName = Thread.currentThread().getName();
    String fileName = _logDir + "/log_" + threadName;

    return fileName + ".log";
  } // End Method getFileName() ======================

  // =================================================
  /**
   * getErr returns the the file handle to the log file. if the log file does
   * not yet exist, a print writer around system.err is returned 
   * if printToConsole is not turned off.
   * 
   * 
   * @return PrintWriter The filehandle to the log file
   * 
   */
  // =================================================
  public final static PrintWriter getErr() {

    if ( logWriter == null && printToConsole ) {
      System.err.println(" getting err ");
      if ( logWriter == null ) 
        logWriter = new PrintWriter ( System.err );
    }
    return logWriter;
  } // End Method getErr() ======================

  

  // =================================================
  /**
   * close will close the appenders and log files.
   */
  // =================================================
  public final static void close() {

    if (logWriter != null)
      logWriter.close();
    logWriter = null;
    
   throw new RuntimeException( "this should have been only called at the end ! where is it coming from");

  } // end Method close() ------------------------

  // =================================================
  /**
   * finalize (calls close() ).
   */
  // =================================================
  @Override
  protected void finalize() {
    close();

  } // end Method finalize() -----------------------

  // =================================================
  /**
   * isDebug_level() returns boolean (whether or not debug is turned on or not).
   *
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  // =================================================
  public static final boolean isDebug_level() {
    return debug_level;
  }

  // =================================================
  /**
   * isError_level() returns boolean (whether or not error is turned on or not).
   *
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  // =================================================
  public static final boolean isError_level() {
    return error_level;
  }

  // =================================================
  /**
   * setDebug_Level turns on or off the debug level.
   *
   * @param pDebug_level the debug level
   */
  // =================================================
  public static final void setDebug_level(boolean pDebug_level) {
    GLog.debug_level = pDebug_level;
  }

  // =================================================
  /**
   * setError_Level turns on or off the error level.
   *
   * @param pError_level the error level
   */
  // =================================================
  public static final void setError_level(boolean pError_level) {
    GLog.error_level = pError_level;

  }

  // =================================================
  /**
   * getLoggingLevels returns an array of levels that have been turned on for
   * this class.
   *
   * @param pClass the class
   * @return String[]
   */
  // =================================================
  public final static boolean[] getLoggingLevels(Class<?> pClass) {

    boolean[] returnVal = null;
    
    
    if ( ClassLoggingHash != null ) 
      if ( pClass != null )
        returnVal  = ClassLoggingHash.get(pClass.getName() );
      
      // ------------------------------
      // The properties file should have 
      
      // This program looks for a 
      // the full path to the class = ALL|TRACE|DEBUG|STD|INFO|WARNING|ERROR|OFF
      // example :   gov.nih.cc.rmd.nlp.framework.utils.GLog = TRACE|DEBUG
      
      
   /*
    returnVal[1] = "All_____LEVEL=" + String.valueOf(GLog.all_level);
    returnVal[2] = "TRACE___LEVEL=" + String.valueOf(GLog.trace_level);
    returnVal[3] = "DEBUG___LEVEL=" + String.valueOf(GLog.debug_level);
    returnVal[4] = "STD_____LEVEL=" + String.valueOf(GLog.std_level);

    returnVal[5] = "INFO____LEVEL=" + String.valueOf(GLog.info_level);
    returnVal[6] = "WARNING_LEVEL=" + String.valueOf(GLog.warning_level);
    returnVal[7] = "ERROR___LEVEL=" + String.valueOf(GLog.error_level);
    returnVal[8] = "OFF_____LEVEL=" + String.valueOf(GLog.off_level);
    */

    return returnVal;

  } // end Method getLoggingLevels() -------

  // =================================================
  /**
   * initialize sets the logDir, debug and error levels from the command line
   * arguments.
   * 
   * This is looking for arguments --logDir=/some/path/to/logs
   * --debug_level=true|false or --debug_level (true) false by default
   * --error_level=true|false or --error_level (true) true by default
   * --warning_level= --trace_level= --info_level= --std_level=
   * --locToConsoleToo= --printToLog=true|false if true, will print to a .g.log
   * file even if the appenders don't work --log4JConversionPattern=
   *
   * @param pArgs the args
   * @throws Exception the exception
   */
  // =================================================
  public static void set(String[] pArgs) throws Exception {
   
    String outputDir = U.getOption(pArgs, "--outputDir=",  "/glog_Output_" );
    String _logDir = U.getOption(pArgs, "--logDir=", outputDir + "/logs");
    String loggingPropertiesFile = U.getOption(pArgs, "--loggingPropertiesFile=",U.getDirFromFileName(_logDir ) + "/../config/frameworkLogging.properties" );

    GLog.debug_level = Boolean.valueOf(U.getOption(pArgs, "--debug_level=", "false"));
    GLog.error_level = Boolean.valueOf(U.getOption(pArgs, "--error_level=", "true"));
    GLog.info_level = Boolean.valueOf(U.getOption(pArgs, "--info_level=", "true"));
    GLog.std_level = Boolean.valueOf(U.getOption(pArgs, "--std_level=", "true"));
    GLog.warning_level = Boolean.valueOf(U.getOption(pArgs, "--warning_level=", "true"));
    GLog.off_level = Boolean.valueOf(U.getOption(pArgs, "--off_level=", "false"));
    GLog.all_level = Boolean.valueOf(U.getOption(pArgs, "--all_level=", "false"));
    GLog.trace_level = Boolean.valueOf(U.getOption(pArgs, "--trace_level=", "false"));
    GLog.printToConsole = Boolean.valueOf(U.getOption(pArgs, "--printToConsole=", "false"));
    GLog.printToLog = Boolean.valueOf(U.getOption(pArgs, "--printToLog=", "false"));
   
    setLogDir(_logDir);
   
    // read in the properties file
    setClassLevelProperties( loggingPropertiesFile);
    
    // -------set the log levels
   

    GLog.println(GLog.STD___LEVEL, GLog.class, "setLevels", "" );

  } // end Method setLogFile() ------------------
  
    
    
    // =================================================
    /**
     * getFrameworkPropertiesFile looks for a physical file
     * via --configFile=  or $outputDir/config/frameworkLogging.properties
     * 
     * @return
    */
    // =================================================
   private static Properties getFrameworkPropertiesFile(String pFileName ) {
      
     Properties returnVal = null;
     
     try {
     File configFile = new File( pFileName );
     
     if ( configFile.exists())
     
      returnVal = U.readProperties( configFile.getAbsolutePath() );
     else 
       returnVal = U.readProperties( "config/frameworkLogging.properties");
     } catch (Exception e) {
       e.printStackTrace();
       System.err.println("Issue trying to read in the config file " + pFileName + ": " + e.toString());
     
       throw new RuntimeException();
       
     }
     
      return returnVal;
    }



  // end Method setLevels() -----------------------
  
  // =================================================
  /**
   * setClassLevelProperties
   * 
   * @param pLoggingPropertiesFile
  */
  // =================================================
   private static void setClassLevelProperties(String pLoggingPropertiesFile) {
   
     ClassLoggingHash = new HashMap<String, boolean[]>();
     if ( pLoggingPropertiesFile != null ) {
       
       File aFile = new File (pLoggingPropertiesFile);
       
       if ( aFile.exists() && aFile.canRead()) {
         Properties properties = getFrameworkPropertiesFile( pLoggingPropertiesFile);
     
         if ( properties != null ) {
           Enumeration<?> propertyList = properties.elements() ;
       
           while ( propertyList.hasMoreElements() ) {
             String className = (String) propertyList.nextElement();
             String values = (String) properties.get( className);
             
             String vals[] = U.split( values) ;
             boolean buff[] = new boolean[8];
             buff[0] = buff[1] = buff[2] = buff[3] = buff[4] = buff[5] = buff[6] = buff[7];
             //  ALL|TRACE|DEBUG|STD|INFO|WARNING|ERROR|OFF
             for ( String val : vals ) {
               switch ( val ) {
                 case "ALL" : buff[ALL_LEVEL] = true; break;
                 case "TRACE" : buff[TRACE_LEVEL] = true; break;
                 case "DEBUG" : buff[DEBUG_LEVEL] = true; break;
                 case "STD"   : buff[STD___LEVEL] = true; break;
                 case "INFO"  : buff[INFO_LEVEL] = true; break;
                 case "WARNING": buff[WARNING_LEVEL] = true; break;
                 case "ERROR" : buff[ERROR_LEVEL] = true; break;
                 case "OFF"   : buff[OFF_LEVEL] = true; break;
              }
             }
             ClassLoggingHash.put( className, buff );
           
           } // end loop through list
         } // end if there are properties
       } // end if there is a file and it exists
     } // end if a filename was passed in
     
  } // end Method setClassLevelProperties() ----------



 

  // =================================================
  /**
   * main.
   *
   * @param pArgs the command line arguments
   */
  // =================================================
  public static void main(String[] pArgs) {

    try {

      String[] args = setArgs(pArgs);

      // --------------------
      GLog.set(args); // requires --logDir=, --debug_level=true|false
      PerformanceMeter meter = new PerformanceMeter();
      meter.setMetric(1);

      meter.begin("Starting the application");

      GLog.println("an information messge ");
      GLog.println(GLog.ERROR_LEVEL, "an error message");
      GLog.println(GLog.ERROR_LEVEL, GLog.class, "main", "anoother way to give an error msgs ");
      GLog.println(GLog.DEBUG_LEVEL, "a debug message");
      GLog.println(GLog.STD___LEVEL, GLog.class, "main", "anoother way to give an std msgs ");
      GLog.println(GLog.INFO_LEVEL, GLog.class, "main", "anoother way to give an info msgs ");
      GLog.println(GLog.WARNING_LEVEL, GLog.class, "main", "anoother way to give an warning msgs ");


      meter.stop("end ");

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Something went wrong here " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, GLog.class, "main", msg);
    }

  } // end method Main() -----------------------------

  // ------------------------------------------
  /**
   * setArgs sets args for logging.
   *
   * @param pArgs the args
   * @return String[]
   */
  // ------------------------------------------
  public static String[] setArgs(String pArgs[]) {

    String className = U.getClassName(MethodHandles.lookup().lookupClass().toString());
    String fullClassName = U.getClassNameFull(MethodHandles.lookup().lookupClass().toString());
    String dateStamp = U.getDateStampSimple();

    // -------------------------------------
    // Input and Output

           
    String outputDir = U.getOption(pArgs, "--outputDir=",  "/glog_Output_" + dateStamp);
    String logDir = U.getOption(pArgs, "--logDir=", outputDir + "/logs");
    String loggingPropertiesFile = U.getOption(pArgs, "--loggingPropertiesFile=",U.getDirFromFileName(logDir ) + "/../config/frameworkLogging.properties" );
    String printToConsole = U.getOption(pArgs, "--printToConsole=", "true");
    String printToLog = U.getOption(pArgs, "--printToLog=", "false");

    
    
    String args[] = {
       
        "--outputDir=" + outputDir,
        "--logDir=" + logDir,
        "--loggingPropertiesFile=",  loggingPropertiesFile,
        "--debug_level=" + debug_level,
        "--error_level=" + error_level,
        "--warning_level=" + warning_level,
        "--trace_level=" + trace_level,
        "--info_level=" + info_level, 
        "--std_level=" + std_level,
        "--printToConsole=" + printToConsole,
        "--printToLog=" + printToLog

        
    };

    String description = " \n\n\n       " + className + " tests the glog3 == log4J capabilities \n";

    if (Use.usageAndExitIfHelp(fullClassName, pArgs, args)) {
      System.out.print(description);
      System.exit(0);
    }

    return args;

  } // End Method setArgs() -----------------------

} // end Class GLog
