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
 * UTest tests some things
 *
 * @author  Guy Divita 
 * @created Jul 6, 2013
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class UTest {


  // ================================================|Public Method Header|====
/**
 * Method main
 *
 * @param pArgs     The path to a valid usage page.
 *
 *
*/
// ================================================|Public Method Header|====
public static void main( String[] pArgs)
  {
  
  String fileName = "messedUpfile.txt";
  String fileName2 = "fixedFile.txt";
  // -------------------------------
  // Create a file that has end of file characters in the middle of the file
  createMessedUpFile( fileName);
  
  String buff = readFile( fileName);
  
 // String buff2 = U.stripEOF(buff);
  
  System.out.println(" ------------------------------");
  //ystem.out.println( buff2);  

  writeFixedFile( buff, fileName2);
  
   } // ***End public static void usage( String pFileName)


// =======================================================
/**
 * writeFixedFile [Summary here]
 * 
 * @param buff2
 * @param fileName2
 */
// =======================================================
private static void writeFixedFile(String buff2, String pFileName) {
  try {
    PrintWriter out = new PrintWriter( pFileName);
    
    out.print( buff2);
    out.close();
  } catch (Exception e) {}
  
} // End Method writeFixedFile() ======================



// =======================================================
/**
 * readFile [Summary here]
 * 
 * @param fileName
 */
// =======================================================
private static String readFile(String fileName) {
  
  String buff = null;
  try {
    buff = U.readFile( "messedUpFile");

  System.out.println( buff);
  

  
  
  } catch (Exception e) {
    System.err.println("Issue with reading in the file \n" + e.toString() + "\n" + U.getStackTrace(e));
  }

  return buff;
}  // End Method readFile() ======================



// =======================================================
/**
 * createMessedUpFile creates a text file with the end of file char
 * in the middle
 * 
 * @param pFileName
 */
// =======================================================
private static void createMessedUpFile(String pFileName) {

  try {
    PrintWriter out = new PrintWriter( pFileName);
    
    int i = 0;
    for ( ; i < 10; i++ ) {
      out.print("XXX " + i + " XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n");
    }
    
    // ----------------
    // inject the eof char here
    char eof = (char)26;
    out.print( eof );
    out.print('\n');
    for ( ; i < 20; i++ ) {
      out.print("XXX " + i + " XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n");
      System.err.println( " here  " + i);
    }
    
    out.close();
    
    
  } catch (FileNotFoundException e) {
    
    String msg = "Issue with injecting an eof intot the file \n" + e.getMessage() + "\n" + U.getStackTrace(e);
    System.err.println( msg);
  }
  

}  // End Method createMessedUpFile() ======================






} // End of the Class Use
