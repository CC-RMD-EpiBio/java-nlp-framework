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
