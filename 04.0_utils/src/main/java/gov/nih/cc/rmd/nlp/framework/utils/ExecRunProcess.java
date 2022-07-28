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
// =================================================
/**
 * ExecRunProcess runs a process on the command line
 * where input is pushed to the command's stdIn
 * 
 * where the command's std out/err is funneled
 * to this processes out and error
 * 
 * This class is intended to be extended for specific wrappers 
 * 
 * Elements to this class:
 * 
 *  1. Create a directory with all the jars,libs,dlls, exec's that the
 *  external process will need to run as if it was running from the command line
 *
 *  2. Zip/Jar this directory up.  It would be helpful to have all those
 *  files off a root directory.  Reason: these will be copied on the fly to
 *  a temp directory at runtime, where that temp directory might include other
 *  items.  Having everything off a tmp/root/someDependeny.dll  will make
 *  it easier to clean up afterwards programmatically, or manually.   
 *  
 *  3. If you zipped it up, rename it to jar.  
 *  
 *  4. Create a maven dependency for this jar:
 *     a. Put this jar into your project's src/main/resources directory
 *     b. Add to your pom the magic to have this jar get added into the classpath 
 *     c. Add this jar as a dependency to your application
 *     
 *  5. Create a new class extended from this class
 *
 *  6. In that new class, overwrite the setLibsAndExecs( ) method and put into it something like
 *      { super.setLibsAndExecs(  dummyAnnotatorDlls ); }
 *   
 *  7.  In your init method of your wrapper (here called DummyPythonWrapper), call the following: 
 *  
 *     DummyPythonWrapper exec_ = new DummyPythonWrapper( );
 *     exec_.setRelativeRootLibDir("dummyannotator");
 *     exec_.setWorkingDir( );          
 *     exec_.setLibsAndExecs( );          // <----- Pass in the list of libs and execs to copy
 *     String command = exec_.getWorkingDir() + "/dummyannotator.exe";   //<----- no need to call cmd dispite the documentation 
 *     String args[] = {};
 *     exec_.setCommand(command, args );  // <----- Set the command and its arguments
 *     exec_.forkOffProcess();            // <----- Once everything is defined, fork off the process 
 *
 *  8. In the process method of your wrapper call
 *     String output = exec_.processOnce( inputString );
 *     
 *  9. In the destroy method of your wrapper, call
 *     exec_.close();     <------ closes out the forked processes
 *     and optionally
 *     exec_.cleanup();   <----- removes the temp files and dir
 *
 * @author     Guy Divita
 * @created    Sep 4, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * @author divitag2
 *
 */
public class ExecRunProcess {

 
  public static final String END_OF_INPUT              = "|_EndOfInput_|";     // <--- tells the process to stop listening and process
  public static final String END_OF_PROCESS_PROCESSING = "|_EndOfOutput_|";    // <--- tells this process that has finished sending the output
  public static final String END_OF_ALL_INPUT          = "|_EndOfAllInput_|";  // <--- tells the process to die after
  // =================================================
  /**
   * Constructor
   * This constructor assumes you have a working directory that is
   * already populated with where the command is. 
   * 
   * @param pCommand 
   * @param pCommandLineArguments
   * @param pWorkingDirectory 
   * @throws Exception 
   * 
  **/
  // =================================================
  public ExecRunProcess(String pCommand, String[] pCommandLineArguments, String pWorkingDirectory) throws Exception {
    
    init ( pCommand, pCommandLineArguments, pWorkingDirectory );

  } // end Constructor() ----------------------------

  
  // =================================================
  /**
   * Constructor  This constructor will copy the libs and exec into the workingDir (the first time), and set
   *              the process up to run. 
   *
   * @param pCommand
   * @param pCommandLineArgs
   * @param pWorkingDir
   * @param pRelativeLibRootDir
   * @param pDllsToLoad  list of paths to dlls, libs, and execs that should be on the classpath that are needed
   *                    to run the command.  These should be coming from a jar that wrapped up everything
   *                    needed.  That jar should have a root directory.  
   *                    
   * @throws Exception 
   * 
  **/
  // =================================================
  public ExecRunProcess(String pCommand, String[] pCommandLineArgs, String workingDir, String pRelativeLibRootDir, String[] pDllsAndLibsToLoad) throws Exception {
    
    try {
    
      setRelativeRootLibDir("dummyannotator");
      setWorkingDir( );            // <----- Set the working directory the default way - with the $relativeRootLibDir as part of it
      setLibsAndExecs( pDllsAndLibsToLoad );     // <----- Pass in the list of libs and execs to copy
      setCommand(pCommand, pCommandLineArgs );  // <----- Set the command and its arguments
      forkOffProcess();            // <----- Once everything is defined, fork off the process 
      
  
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println( "issue loading the dlls " + e.toString());
      throw new Exception ();
     }
   
  } // end Constructor() -----------------------------


  // =================================================
  /**
   * Constructor
   *
   *  If you use this constructor, you will need to call
   *  the following methods:
   *  
   *      setRelativeRootLibDir()
   *      setWorkingDir()
   *      setCommand()
   *      forkOffProcess()
   *  
  **/
  // =================================================
  public ExecRunProcess() {
   
  }


  // =================================================
  /**
   * setRelativeRootLibDir sets the name of the root part of the 
   * relative path found in the jar that includes the libs
   * and execs to be copied over. 
   * 
   * @param pAbstractLibRootDir
  */
  // =================================================
  public final void setRelativeRootLibDir(String pAbstractLibRootDir) {
    this.relativeRootLibDir = pAbstractLibRootDir;
  } // end Method setrelativeRootLibDir() ------------


  // =================================================
  /**
   * setWorkingDir sets the working directory.  It
   * uses java's default mechanism to define it.
   * 
   * If you use this mechanism, set the relativeRootLibDir first
   * so that the tempDir includes the relativeRootLibDir.
   * If you don't set the root dir, a unique id is
   * created. 
   * 
   * In windows, this will be under /Users/you/AppData/Local/Temp/[$relativeRootLibDir]
   * 
   * @param pWorkingDir
   * @return String
   * @throws Exception 
  */
  // =================================================
  public final String setWorkingDir() throws Exception {
  
    String workingDir = System.getProperty("java.io.tmpdir") + "/" + this.relativeRootLibDir;
    this.workingDir = workingDir.replace('\\', '/' );
    
  
    setWorkingDir( workingDir);
    
    return this.workingDir;
  
  } // end Method setWorkingDir() -------------------


  // =================================================
  /**
   * setWorkingDir sets the working directory.  If you 
   * set this yourself, make sure the process will have 
   * the ability to write and read from it.
   * 
   * if this directory does not yet exist, it creates it.
   * 
   * @param pWorkingDir
   * @throws Exception 
  */
  // =================================================
  public final void setWorkingDir(String pWorkingDir) throws Exception {
  
  File aDir = new File( pWorkingDir);
  
  if ( !aDir.exists() || !aDir.canRead() || aDir.list().length < 1 ) 
    U.mkDir(pWorkingDir );
  
   if ( aDir.isDirectory() && aDir.canRead() && aDir.canWrite())
     this.workingDirectory = pWorkingDir;
   else 
     throw new Exception( "Cannot use " + pWorkingDir + " as a temp directory ");
    
  } // end Method setWorkingDir() -------------------


//=================================================
 /**
  * getWorkingDir
  * @return String 
 */
 // =================================================
 public final String getWorkingDir()  {
   
   return this.workingDirectory;
 } // end Method getWorkingDir() -------------------
 
  
  // =================================================
  /**
   * setCommand sets the command to be run and its arguments
   * 
   * @param pCommand
   * @param pArgs   This is posix style - anything that 
   *                that is white space delimited should
   *                be a separate element.
   *                It is ok if this is null or empty.
  */
   // =================================================
  public final void setCommand(String pCommand, String[] pArgs) {
    
    this.command = pCommand;
    this.commandLineArguments = pArgs;
    
  } // end Method setCommand() -----------------------


  // =================================================
  /**
   * setLibsAndExecs sets the list of libararies, dlls,
   * and exec files to be copied over to the temp dir
   * 
   * Make sure the elements include the relative root 
   * directory as part of the path
   * 
   * 
   * @param pLibsAndExecs
   * @throws Exception 
  */
  // =================================================
  public final void setLibsAndExecs(String[] pLibsAndExecs) throws Exception {
    this.libsAndExecs = pLibsAndExecs;
    
    copyDllsLibsAndExes( );
    
  } // end Method setLibsAndExecs() -------------------

  // =================================================
  /**
   * setLibsAndExecs sets the list of libararies, dlls,
   * and exec files to be copied over to the temp dir
   * 
   * Make sure the elements include the relative root 
   * directory as part of the path
   * 
   *  This version is meant to be overwritten with
   *  your specific elements;
   * 
   * @param pLibsAndExecs
   * @throws Exception 
  */
  // =================================================
  public void setLibsAndExecs( ) throws Exception {
   
    
  } // end Method setLibsAndExecs() -------------------
  
  
  // =================================================
  /**
   * forkOffProcess forks off the process
   * 
   * @throws Exception 
  */
  // =================================================
 public final void forkOffProcess() throws Exception {


   if ( this.command != null)
     if ( this.workingDirectory != null ) 
       init( this.command,  this.commandLineArguments, this.workingDirectory) ;
     else
       throw new Exception ("working directory is not yet set.");
   else
     throw new Exception( "The command is not yet set.");

    
  } // end Method forkOffProcess() -------------------

//=================================================
/**
 * process runs the command, and returns with anything
 * that came from std or stderr.
 * 
 * @return String
 * @throws IOException 
*/
// =================================================
 public final String process() throws IOException {
  
   StringBuffer returnVal = new StringBuffer();
    
   try {
   
    String line = null;
      
    while (( line = bufferedProcessOutputWriter.readLine()) != null )  {
      returnVal.append(line);
      returnVal.append(U.NL);
      
    }
    
   
  } catch (IOException e) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, ExecRunProcess.class, "process",
        "Problem sending this input to the command "  +   e.toString());
    throw e;
   
  }
    
  return returnVal.toString();
} // end Method process() -------------------------
 
 // =================================================
 /**
  * processOnce sends the inputString to the process 
  * and returns what returns from the processe's std output
  * 
  * This method is expecting that once this has been
  * called, the input has been sent to the external process,
  * the external process will die after sending back
  * its output. 
  *
  *  This method relies on having some simplistic handshaking
  *  communication between the exeternal process and
  *  this process.  Specificaly, this method "listens" 
  *  and continues to read from the external process's output
  *  until it sees the END_OF_PROCESS_PROCESSING marker seen.
  *  
  *  If the external process does not send this when it finishes
  *  processing, this method will continue to wait for-ever.
  *  
  *  Likewise, the external process will need a signal
  *  from this process to stop listening for input and
  *  start processing.  Therefore, this method will
  *  send an END_OF_INPUT message at the end of each
  *  input sent in.
  *  
  * 
  * @param pInputString
  * @return String
  * @throws IOException 
 */
 // =================================================
  public final String processOnce(String pInputString) throws IOException {
    
    String returnVal = process( pInputString, true);
    
    return returnVal;
  } // end Method processOnce() ----------------------

  
  // =================================================
  /**
   * process sends the inputString to the process 
   * and returns what returns from the processe's std output
   * 
   * This method relies on having some simplistic handshaking
  *  communication between the exeternal process and
  *  this process.  Specificaly, this method "listens" 
  *  and continues to read from the external process's output
  *  until it sees the END_OF_PROCESS_PROCESSING marker seen.
  *  
  *  If the external process does not send this when it finishes
  *  processing, this method will continue to wait for-ever.
  *  
  *  Likewise, the external process will need a signal
  *  from this process to stop listening for input and
  *  start processing.  Therefore, this method will
  *  send an END_OF_INPUT message at the end of each
  *  input sent in.
   * 
   * @param pInputString
   * @param pProcessOnce
   * @return String
   * @throws IOException 
  */
  // =================================================
  public final String process(String pInputString) throws IOException {
    
   String returnVal = process( pInputString, false);
    
   return returnVal;
    
  } // end Method process() --------------------------
  
  
  // =================================================
  /**
   * process sends the inputString to the process 
   * and returns what returns from the processe's std output
   * 
   * [TBD] re-initializes if it runs off the rails with an exception
   * 
   * @param pInputString
   * @param pProcessOnce
   * @return String
   * @throws IOException 
  */
  // =================================================
   public final String process(String pInputString, boolean pProcessOnce) throws IOException {
    
     StringBuffer returnVal = new StringBuffer();
      
     try {
       if ( bufferedProcessInputReader == null ) {
         return null;
       }
       
      bufferedProcessInputReader.write( pInputString  );
      bufferedProcessInputReader.newLine();
      bufferedProcessInputReader.write(END_OF_INPUT);
      bufferedProcessInputReader.newLine();
      bufferedProcessInputReader.flush();
      
      if ( pProcessOnce )
        bufferedProcessInputReader.close();  // <--------------- remove this when we get the handshanking right
      
      String line = null;
      boolean done = false;
      while (!done ) {
        
        // [TBD] Put a timeout catch here so this doen't always hang if it goes off the rails
        
        while (( line = bufferedProcessOutputWriter.readLine()) != null )  {
       
      
       
        returnVal.append(line);
        returnVal.append(U.NL);
        
        if ( line.endsWith( END_OF_PROCESS_PROCESSING )) {
          done = true;
          break;
        }
        
      }
      
      }
    } catch (IOException e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, ExecRunProcess.class, "process",
          "Problem sending this input to the command "  + pInputString + " " +  e.toString());
      throw e;
     
    }
      
    return returnVal.toString();
  } // end Method process() -------------------------

   // =================================================
   /**
    * process sends the inputString to the process 
    * and returns what returns from the processe's std output
    * 
    * [TBD] re-initializes if it runs off the rails with an exception
    * 
    * @param pInputFileName
    * @param pProcessOnce
    * @return String
    * @throws IOException 
   */
   // =================================================
    public final String processFile(String pInputFileName) throws IOException {
     
      StringBuffer returnVal = new StringBuffer();
       
      try {
        if ( bufferedProcessInputReader == null ) {
          return null;
        }
        
        String inputFileName = pInputFileName;
        inputFileName = inputFileName.replace('\\', '/');
        inputFileName = inputFileName.replace("//","/" );
        
       bufferedProcessInputReader.write( inputFileName  );
       bufferedProcessInputReader.newLine();
       bufferedProcessInputReader.flush();
       
      
       String output = null;
       
       while ( (output = bufferedProcessOutputWriter.readLine()) != null &&
                     ( !output.contains("bio_path") && !output.contains("xml_path") 
                     ) 
              ) 
         System.err.println( output);
       
      
         returnVal.append(output);      
       
      
     
  
         
     
     } catch (IOException e) {
       e.printStackTrace();
       GLog.println(GLog.ERROR_LEVEL, ExecRunProcess.class, "process",
           "Problem sending this input to the command "  + pInputFileName + " " +  e.toString());
       throw e;
      
     }
       
     return returnVal.toString();
   } // end Method process() -------------------------


  // =================================================
  /**
   * readFromStdOut reads a line from the process's stdout before
   * it's ready for input
   * @param pReadyText  
   * 
   * @return String
   * @throws IOException 
  */
  // =================================================
    public final String readFromStdOut(String pReadyText ) throws IOException {
    
      
      String output = null;
      while ( true ) {
        output = bufferedProcessOutputWriter.readLine();
        if ( output != null  && ((!output.startsWith( pReadyText )))) 
        System.err.println( output);
        else 
          break;
      }
      
    return output;
  } // end Method readFromStdOut() ---------------------


    // =================================================
    /**
     * close closes the open processes.  
     * 
    */
    // =================================================
   public final void close() {
     
   try {
     if ( bufferedProcessInputReader != null ) try {
       bufferedProcessInputReader.write(END_OF_ALL_INPUT);
       bufferedProcessInputReader.flush();
       bufferedProcessInputReader.close();
       bufferedProcessInputReader = null;
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Issue closing the process ? " + e.toString());
    }
      
     
     if ( bufferedProcessOutputWriter != null) {
       bufferedProcessOutputWriter.close();
       bufferedProcessOutputWriter = null;
     }
    
     if ( process != null) 
       process.destroy();
     
       
   } catch (Exception e ) {
     e.printStackTrace();
     System.err.println("Issue closing the process ? " + e.toString());
   }
    } // end Method close();

   // =================================================
   /**
    * close closes the open processes.  
    * 
   */
   // =================================================
  @Override
   protected  void finalize() throws Throwable {
      close();   
 
   } // end Method close();
   
  // =================================================
   /**
    * cleanup removes the temp dir that was created
    * 
    * This method will not throw an exception if it
    * cannot remove the files/directory, but will print
    * a msg. 
   */
   // =================================================
  public final void cleanup() {
    
    File aDir = null;
    try {
      
      if ( this.workingDirectory != null ) {
         aDir = new File( this.workingDirectory);
         
         U.deleteDirectory(  aDir);
        
      }
    } catch (Exception e) {
      System.err.println( "Issue trying to clean up " + e.toString());
    }
    
  } // end Method cleanup() --------------------------
    
   
  // =================================================
  /**
   * init sets the working directory, forks off the process 
   * with the command and its args.  It will kick off a cmd 
   * if windows, or sh if unix.
   * 
   * This method expects utf8 character to be sent to the process
   * and utf8 chars read back in.
   * 
   * @param pCommand
   * @param pCommandLineArguments
   * @param pCorkingDirectory
   * @throws Exception 
  */
  // =================================================
  private final void init(String pCommand, String[] pCommandLineArguments, String pWorkingDirectory) throws Exception {

    File workingDirectory = null;
    ProcessBuilder processBuilder = null;

    if ( !U.isWindows() )
      this.FS = LINUX_FS;
    
    try {
      List<String> commandLineTokens = tokenizeCommandLine(pCommand, pCommandLineArguments);

      if (pWorkingDirectory != null) workingDirectory = new File(pWorkingDirectory);

      processBuilder = new ProcessBuilder(commandLineTokens);
      processBuilder.directory(workingDirectory);
      processBuilder.redirectErrorStream(true);
      process = processBuilder.start();
      OutputStream processInputStream = this.process.getOutputStream();
      InputStream processOutputStream = this.process.getInputStream();
      bufferedProcessInputReader  = new BufferedWriter(new OutputStreamWriter(processInputStream , StandardCharsets.UTF_8)) ;
      bufferedProcessOutputWriter = new BufferedReader(new InputStreamReader(processOutputStream));

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, ExecRunProcess.class, "constructor",
          "Problem with setting up the Exec interface from the command " + pCommand + " " + e.toString());
      throw e;
    }

  } // end method init() -----------------------------


  // =================================================
  /**
   * getDLLsFromJar retrieves a list of the contents of a jar
   * This is useful to use when the list of files within a
   * jar is not possible to enumerate - i.e., over 100 files.
   * 
   * @param pPath    should be something like spacyTokenizer/
   * @return List<String>
   * @throws Exception 
  */
  // =================================================
  public final String[] getDLLsFromJar(String pPath) throws Exception {
    
    String[] returnVal = null;
    List<String> dllList = new ArrayList<String>();
    String anPath = null;
    try {
   
      String javaClassPath = System.getProperty("java.class.path" );
      if ( javaClassPath != null ) {
        String classPaths[] = U.split( javaClassPath, ";" );  //< ----- OS specific
        for ( String aClassPath : classPaths ) {
          if ( aClassPath.toLowerCase().contains( pPath.toLowerCase() )) {
            anPath = aClassPath ;
            break;
          }
        }
      }
        
      JarFile jarFile = new JarFile( anPath);

      final Enumeration<JarEntry> entries = jarFile.entries();
      while (entries.hasMoreElements()) {
          JarEntry entry = entries.nextElement();
          String aFileName = entry.getName();
          if ( !aFileName.endsWith("/"))
          dllList.add( aFileName);
      }
      jarFile.close();
     
    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getDLLsFromJar", "Issue getting the name of the files from the jar " + e.toString() );
      throw e;
    }
    
    if ( dllList != null && !dllList.isEmpty())
      returnVal = dllList.toArray( new String[ dllList.size()] );
    return returnVal;
  } // end Method getDLLsFromJar() -------------------
  
  
//=================================================
 /**
  * getExecFromPath retrieves the all in one exec file
  * from the class path - for when the encompassing jar holds just
  * the .exe
  * 
  * @param pExecPath
  * @return List<String>
  * @throws Exception 
 */
 // =================================================
 public final String[] getExecFromPath(String pExecPath) throws Exception {
   
   String[] returnVal = null;
   List<String> dllList = new ArrayList<String>();
   String classPaths[] = null;
   try {
  
     String javaClassPath = System.getProperty("java.class.path" );
     if ( javaClassPath != null ) {
       classPaths = U.split( javaClassPath, this.FS );  //< ----- OS specific
       for ( String aClassPath : classPaths ) {
         if ( aClassPath.toLowerCase().contains( pExecPath.toLowerCase() )) {
           dllList.add( aClassPath ) ;
           
         }
       }
     }
       
    
   
   } catch (Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getDLLsFromJar", "Issue getting the name of the files from the jar " + e.toString() );
     throw e;
   }
   
   if ( dllList != null && !dllList.isEmpty()) {
     List<String> newDllList = null;
     for ( int i = 0; i < dllList.size(); i++ ) { // String aClassPath : dllList ) {
       
       if ( dllList.get(i).endsWith(".jar")) {
         newDllList = extractFromJar( dllList.get(i) );
         dllList.set(i, "");
         
       }
     }
     if ( !newDllList.isEmpty()) {
       trimDllList( dllList );
       dllList.addAll( newDllList);
     }
   }
   
   
   
   if ( dllList != null && !dllList.isEmpty())
     returnVal = dllList.toArray( new String[ dllList.size()] );
   return returnVal;
 } // end Method getDLLsFromJar() -------------------

  // =================================================
/**
 * trimDllList removes empty entries in the list
 * 
 * @param dllList
*/
// =================================================
 private void trimDllList(List<String> dllList) {
 
   if ( dllList != null && !dllList.isEmpty())
     for ( int i = 0; i < dllList.size(); i++ )
       if ( dllList.get(i) == null || dllList.get(i).trim().length() == 0 )
         dllList.remove(i);
     
} // end Method trimDllList() ----------------------


// =================================================
/**
 * extractFromJar returns the contents of the jar file in a list
 * 
 * @param pJar
 * @return List<String>
 * @throws IOException 
*/
// =================================================
private final List<String> extractFromJar(String pJar) throws IOException {
  List<String> returnVal = new ArrayList<String>();
  
  JarFile jarFile = new JarFile( pJar);

  final Enumeration<JarEntry> entries = jarFile.entries();
  while (entries.hasMoreElements()) {
      JarEntry entry = entries.nextElement();
      String aFileName = entry.getName();
      if ( !aFileName.endsWith("/"))
      returnVal.add( aFileName);
  }
  jarFile.close();
  
  
  return returnVal;
} // end extractFromJar() ----------------------------


  // =================================================
  /**
   * tokenizeCommandLine 
   * 
   * @param pCommand
   * @param pCommandLineArguments
   * @return List<String>
   * @throws Exception 
  */
  // =================================================
   private final List<String> tokenizeCommandLine(String pCommand, String[] pCommandLineArguments) throws Exception {
     List< String> commandLineTokens = new ArrayList<String>();
  
     try {
      
        // Switch based on linux vs windows
       if (U.isWindows()) {
         commandLineTokens.add("cmd.exe"); // run in a command line shell
         commandLineTokens.add("/c"); // run the command and don't die vs /C then die
       } else {
         commandLineTokens.add("sh");
         commandLineTokens.add("-c");
       }
       commandLineTokens.add(pCommand);

       if (pCommandLineArguments != null && pCommandLineArguments.length > 0) 
         for (String arg : pCommandLineArguments)
           if (arg != null && arg.trim().length() > 0) 
             commandLineTokens.add(arg);
     } catch (Exception e) {
       e.printStackTrace();
       System.err.println("Issue trying to tokenize the command line and its arguments " + e.toString());
       throw e;
     }

    return commandLineTokens;
  } // end Method tokenizeCommandLine() --------------


  // =================================================
  /**
   * copyDllsLibsAndExes copies over the set of libs and execs
   * that were set via the setLibsAndExecs step.
   * 
   * This method will check to see if those files are already
   * in the working directory.  If so, it won't re-copy them over.
   * 
   * This means that updates to these external programs won't
   * be copied over.  If there are updates to these external
   * libs and programs, you'll need to manually remove
   * the temp dir to insure the latest version gets 
   * copied over. 
   * 
   * @throws Exception 
  */
  // =================================================
  private final void copyDllsLibsAndExes() throws Exception {
    
    if ( this.libsAndExecs == null || this.libsAndExecs.length < 1)
      throw new Exception( "No libs or exes were found. Please set these via the setLibsAndExec() method ");
    else {
     
      boolean loadIt = true;
      File aDir = new File ( this.workingDirectory );
      
      if ( aDir.exists() ) 
          if  ( aDir.canRead() && aDir.listFiles().length > 0 )  {
            // ------------------------------------------------------
            // Trading one bug for another - I cannot just count the number
            // entries in the top level directory - it wont match the number
            // of entries because of subdir counts.
            // So, I'll just have assume that if the dir is there, it's been
            // copied over in it's entirety. 
           //  if ( aDir.list().length >= this.libsAndExecs.length ) {
           //   System.err.println("Those files are already there, not re-copying them");
              loadIt = false;
            }
          
      
      if ( loadIt )
      
        for ( String dll : this.libsAndExecs ) {
        	try {
        		copyDllsLibsAndExes(  dll );
        	} catch (Exception e) {
        		e.printStackTrace();
        		System.err.println("Issue copying " + dll + " " + e.toString());
        		throw e;
        	}
        }
      }    
    
    
  } // end Method copyDllsLibsAndExes() --------------------------

  
 
//=================================================
/**
 * copyDllsLibsAndExes  the path (from the jar) of where the dll is.
 * 
 * This assumes that you've jar'd up the dll's, libs and exe's and put that jar
 * on the classpath.
 * 
 * This method will skip the copy if the directory  exists 
 * and is populated.  This assumes that the jar that was sipped up has a root
 * directory. 
 * 
 * @param pDllToLoad
 * @throws Exception 
*/
// =================================================
  private final static void copyDllsLibsAndExes(String pName ) throws Exception {
    
	 
   try {
    ClassLoader cl = U.class.getClassLoader();
    InputStream in = cl.getResourceAsStream(pName);
    
    if ( in == null )
      throw new Exception( "The lib " + pName + " was not found in the classpath " );
    
   byte[] buffer = new byte[1024];
   int read = -1;
   String tmpDir = System.getProperty("java.io.tmpdir");
   String tmpDLLName = tmpDir + "/" + pName;
   
   // ------------------------
   // if the files are in subdirectories of the
   // the jar, make the subdirs
   // -----------------------
   String tmpDLLSubDir_ = U.getDirFromFileName(tmpDLLName);
   File tmpDLLSubDir = new File( tmpDLLSubDir_);
   if ( !tmpDLLSubDir.exists() )
	   U.mkDir(tmpDLLSubDir_);
   
   File temp = new File( tmpDLLName); 
   FileOutputStream fos = new FileOutputStream(temp);

   while((read = in.read(buffer)) != -1) {
     fos.write(buffer, 0, read);
   }
   fos.close();
   in.close();
   
   // Set the executable bit on the file
   temp.setExecutable(true);
   
   } catch (Exception e) {
     e.printStackTrace();
     System.err.println("Issue loading the dll " + e.toString());
     throw e;
   }
}
  
//=================================================
/**
 * setExecutablePermissionsForCommand (only applies to linux platforms)
 * sets the x for user to run the executable command
 *                   
 * @param pCommand
 * @throws Exception 
*/
// =================================================
   public final void setExecutablePermissionsForCommand(String pCommand) throws Exception {
	
	   try {

		   File command = new File ( pCommand );
		   
		   if (command.exists()) {
			   boolean bval = command.setExecutable(true);
			   if ( !bval ) 
				   throw new Exception("Something went wrong with setting the executable permisisons for command " + pCommand );
		   } else {
			   throw new Exception ("The command " + pCommand + " does not exist, not setting the execute bit on it.");
		   }
	   } catch (Exception e) {
		   e.printStackTrace();
		   System.err.println("Something went wrong with setting the executable permisisons for command " + pCommand + " " + e.toString());
		   throw e;
	   }
			   
  } // end Method setExecutablePermissionsForCommand


// ---------------------------------
  // Class variables
  // ---------------------------------
  private Process process = null;
  private BufferedWriter bufferedProcessInputReader = null;
  private BufferedReader bufferedProcessOutputWriter = null;

  private String workingDir = null;
  private String command = null;
  private String[] commandLineArguments = {};
  private String relativeRootLibDir = UUID.randomUUID().toString();
  private String workingDirectory = null;
  protected String[] libsAndExecs = null;
  protected String WINDOWS_FS = ";";
  protected String LINUX_FS = ":";
  protected String FS = WINDOWS_FS;
 
  
} // end Class ExecRunProcess() -----------------------
