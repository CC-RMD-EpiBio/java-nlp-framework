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
