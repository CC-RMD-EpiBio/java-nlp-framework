// =======================================================
/**
 * RunCommand encapsulate forking off a process so that
 * it can be run in a separate thread
 *
 * @author  guy
 * @created Aug 14, 2015
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class RunCommand implements Runnable {

  private String command;
  private String[] environmentVariables;
  private Process process;

  // =======================================================
  /**
   * Constructor RunCommand 
   *
   * @param pCommand
   */
  // =======================================================
  public RunCommand(String pCommand) {
     this.command = pCommand;
  }

  // =======================================================
  /**
   * Constructor RunCommand 
   *
   * @param pCommand
   * @param pEnvironmentVariables
   */
  // =======================================================
  public RunCommand(String pCommand, String[] pEnvironmentVariables) {
    this.command = pCommand;
    this.environmentVariables = pEnvironmentVariables;
  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
 
  public void run() {
  
    try {
     // this.process = U.runP(this.command, this.environmentVariables );
      U.run(this.command, this.environmentVariables);
     
  
    } catch (Exception e ) {  
      e.printStackTrace();
      System.err.println("Issue kicking off the service");
     // throw e;
    }
    
  }// End Method run() ======================
  
  public String readOutput( )  {
    
    String returnVal = null;
    StringBuffer buff = null;
    StringBuffer errorBuff = null;
    BufferedReader outputStream = null;
    try {
      
      if ( this.process != null ) {
        outputStream = new BufferedReader(new InputStreamReader( this.process.getInputStream()));
        
        if ( outputStream != null ) {

          buff      = new StringBuffer();
      
          String line = null; 
          while ((line = outputStream.readLine()) != null) {
            buff.append(line);
            buff.append(U.NL);
          }
          outputStream.close();
        } // end if there is an outputStream
      
     
      errorBuff = new StringBuffer();
      outputStream = new BufferedReader(new InputStreamReader( this.process.getErrorStream()));

      if ( outputStream != null ) {
        String line = null;
        while ((line = outputStream.readLine()) != null) {
          errorBuff.append(line);
          errorBuff.append(U.NL);
          buff.append(line);
          buff.append(U.NL);
          }
        outputStream.close();
      } // end if there is an error output stream
      } // end if there is a process 
      
      if ( buff != null )
        returnVal = buff.toString();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("xxx " + e.getMessage());
    }
    
 
    
    return returnVal;
  } // End Method readOutput() -------------
  
  
 public String readOutput2( )  {
    
    String returnVal = null;
    StringBuffer buff = null;
   
    BufferedReader outputStream = null;
    try {
      
      if ( this.process != null ) {
        outputStream = new BufferedReader(new InputStreamReader( this.process.getInputStream()));
        
        if ( outputStream != null ) {

          buff      = new StringBuffer();
      
          String line = null; 
          while ((line = outputStream.readLine()) != null) {
            buff.append(line);
            buff.append(U.NL);
          }
          outputStream.close();
        } // end if there is an outputStream
      
     
     
      } // end if there is a process 
      
      if ( buff != null )
        returnVal = buff.toString();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("xxx " + e.getMessage());
    }
    
 
    
    return returnVal;
  } // End Method readOutput() -------------
    
 public String readStdError( )  {
   
   String returnVal = null;
   
   StringBuffer errorBuff = null;
   BufferedReader outputStream = null;
   try {
     
   
     errorBuff = new StringBuffer();
     outputStream = new BufferedReader(new InputStreamReader( this.process.getErrorStream()));

     if ( outputStream != null ) {
       String line = null;
       while ((line = outputStream.readLine()) != null) {
         errorBuff.append(line);
         errorBuff.append(U.NL);
        
         }
       outputStream.close();
     } // end if there is an error output stream
    
     
     if ( errorBuff != null )
       returnVal = errorBuff.toString();
   } catch (Exception e) {
     e.printStackTrace();
     System.err.println("xxx " + e.getMessage());
   }
   

   
   return returnVal;
 } // End Method readOutput() -------------
    
 
 
  // =======================================================
  /**
   * kill kills the process that was forked off by the run command
   * 
   */
  // =======================================================
  public void kill() {
   if ( this.process != null ) {
     
     this.process.destroyForcibly();
    
   }
   this.process = null;
   try { this.finalize(); } catch (Throwable e) {  }
   
  } // End Method kill() ======================

  // =======================================================
  /**
   * isUp 
   * 
   * @return boolean
   */
  // =======================================================
  public boolean isUp() {
   
    boolean returnVal = false;
    
    if ( this.process != null && this.process.isAlive() )
      returnVal = true;
    
    return returnVal;
  } // End Method isUp() ======================
  
  
  

} // end Class RunCommand
