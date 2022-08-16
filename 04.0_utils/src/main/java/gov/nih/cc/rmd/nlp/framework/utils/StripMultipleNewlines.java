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
// =================================================
/**
 * StripMultipleNewlines removes \n\n and replaces it with \n
 * 
 * assuming the input is coming from stdin  unless --inputFile= is on the command line
 * assuming the output is going to stdout unless --outputFile= is on the command line
 *
 * @author     Guy Divita
 * @created    Jul 12, 2021
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * @author Guy
 *
 */
public final class StripMultipleNewlines {

  // =================================================
  /**
   * main
   * 
   * @param pArgs
  */
  // =================================================
public static void main(String[] pArgs) {
   
try {
  
  String[]            args = setArgs( pArgs);
  String    inputFileName  = U.getOption(args, "--inputFile=", "stdIn");
  String    outputFileName = U.getOption(args, "--outputFile=", "sdtOut" );
  String inputBuffer = null;
  String outputBuffer = null;
  PrintWriter out = null;
  
  if ( inputFileName.toLowerCase().equals("stdin")) {
    StringBuffer buff = new StringBuffer();
    BufferedReader in =  null;
    String line = null;
    in =  new BufferedReader(new InputStreamReader( System.in));
    while ((  line = in.readLine() ) != null ) {
      buff.append( line);
      buff.append( "\n");
    }
    inputBuffer = buff.toString().trim();
  } else {
    inputBuffer = U.readFile( inputFileName );
  }
  inputBuffer = trimLines( inputBuffer);
  inputBuffer = inputBuffer.replaceAll("\r", "");
  
  outputBuffer = inputBuffer.replace("\n\n",  "");
  
  if ( outputFileName.toLowerCase().contentEquals("stdout")) 
    out = new PrintWriter( System.out);
  else 
    out = new PrintWriter( outputFileName);

  out.print( outputBuffer );
  
  out.close();
    
  GLog.println("output went to " + outputFileName);
  
  
} catch (Exception e) {
  e.printStackTrace();
  GLog.error_println("Issue with strip newline " + e.toString());
} // end try/catch
 
 GLog.println("Dohn");

} // end Method Main() -------------------------------

// =================================================
  /**
   * trimLines  right trims the lines
   * 
   * @param pInputBuffer
   * @return String
  */
  // =================================================
 private final static String trimLines(String pInputBuffer) {
    
    String returnVal = null;
    String[] rows = U.split(pInputBuffer, "\n");
    StringBuffer trimmedLines = new StringBuffer();
    for ( int i = 0; i < rows.length; i++ ) {
     trimmedLines.append( rows[i].replaceAll("\\s+$", "") );
     trimmedLines.append("\n");
    }
    returnVal = trimmedLines.toString().trim();
      
    return returnVal;
  } // end Method trimLines() ---------------

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
  // Input and Output

  String    inputFile  = U.getOption(pArgs, "--inputFile=", "stdIn");
  
  String    outputFile = "stdOut";
  
  if ( inputFile != "sdtIn")
    outputFile = U.getOption(pArgs, "--outputFile=", U.stripExtensionFromInputFile( inputFile ) + "_stripped" + U.getFileExtension ( inputFile ));
 
  
  String args[] = {
      
      "--inputFile=" + inputFile,
      "--outputFile=" + outputFile
      
  };

   // need a help option here 
  // This method assumes that there is a resources/CS_00_CovidSymptomsApplication.txt
  Use.usageAndExitIfHelp( "StripMultipleNewlines", pArgs, args ) ;
  
  
                          

  return args;

}  // End Method setArgs() -----------------------


} // end Class StripMultipleNewLines() ---------------