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