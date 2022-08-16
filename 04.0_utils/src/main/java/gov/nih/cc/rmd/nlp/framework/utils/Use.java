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
 * Use prints out usage pages
 *
 * @author  Guy Divita 
 * @created Jul 6, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Use {


  // ================================================|Public Method Header|====
/**
 * Method usage prints out the usage page
 *
 * @param pFileName     The path to a valid usage page.
 *
 *
*/
// ================================================|Public Method Header|====
public static void usage( String pFileName)
  {
    if ( pFileName != null ) {

      try {
  BufferedReader      fStream = null;
  fStream = new BufferedReader(new FileReader(pFileName ));

  try {
          String line = null;

    while ((line = fStream.readLine()) != null )  {
      System.out.println( line );
    }
    fStream.close();
    fStream = null;
  } catch ( Exception e ) {
    System.err.println("Not able to read the man page " + e.toString());
    e.printStackTrace();
  }
      }catch (Exception e2 ) {
  System.err.println("Not able to open the man page " + e2.toString());
  e2.printStackTrace();
      }
    }

   } // ***End public static void usage( String pFileName)


// ================================================|Public Method Header|====
/**
 * Method usage prints out the usage page
 *
 * @param pInputStream  The input stream of a help file 
 *
 *
*/
// ================================================|Public Method Header|====
public static void usage( InputStream pInputStream )
  {

    if ( pInputStream != null ) {

      try {
  BufferedReader fStream = new BufferedReader(new InputStreamReader( pInputStream ));

  try {
          String line = null;

    while ((line = fStream.readLine()) != null )  {
      System.out.println( line );
    }
    fStream.close();
    fStream = null;
  } catch ( Exception e ) {
    System.err.println("Not able to read the man page " + e.toString());
    e.printStackTrace();
  }
      }catch (Exception e2 ) {
  System.err.println("Not able to open the man page " + e2.toString());
  e2.printStackTrace();
      }
    }


   } // ***End public static void usage( String pFileName)


// ================================================|Public Method Header|====
/**
* Method usage prints out the usage page
*
* @param pClassName
* @param pArgs
* @param pUsageArgs
* @param pHelpMsg
* @return boolean (if --help was in the pArgs 
*
*
*/
//================================================|Public Method Header|====
public static boolean usageAndExitIfHelp( String pClassName, String[] pArgs, String[] pUsageArgs, String pHelpMsg )
 {

  boolean returnVal = false;
  String help = U.getOption(pArgs,  "--help", "");
  if ( help.equals("true")) {
    
    System.out.print( pHelpMsg );
    System.out.print ( "\n\n");
    System.out.print("Usage: java -jar " + pClassName + ".jar  with the following options: \n\n");
    
    for ( String arg: pUsageArgs)
      System.out.print( "     " + arg + '\n' );
      
    
    returnVal = true;
    System.exit(0 );
   
  }

  return returnVal;


  } // ***End public static void usageAndExit( String pFileName)


// =================================================
/**
 * usageAndExitIfHelp detects if --help was an argument, and prints 
 * the options
 *  
 *   if a pClassNameUsage.txt file has been created and placed in the [pClassName project]/src/main/resources
 *   directory, it will be printed out.
 * 
 * @param pClassName
 * @param pArgs
 * @param args
 * 
 * @return boolean 
*/
// =================================================
public static boolean usageAndExitIfHelp(String pClassName, String[] pArgs, String[] args ) {
 
  String helpMsg = getHelp( pClassName);
  return usageAndExitIfHelp( pClassName, pArgs, args, helpMsg );
  
} // end Method usageAndExitIfHelp() --------------


// =================================================
/**
 * getHelp returns a help message
 * 
 * @param pClassname  (no namespace please)
 * @return String
*/
// =================================================
public static String getHelp( String pClassName) {
 
  String returnVal;
  try {
    returnVal = U.readClassPathResource( pClassName + "Usage.txt");
  } catch (Exception e) {
    e.printStackTrace();
    returnVal = "Usage page has not been put into the src/main/resources/" + pClassName + "Usage.txt not created yet";
   
  }
  return returnVal;
}


// =================================================
/**
 * version checks to see if one of the pArgs arg is --version
 * 
 * If so, it will print out on stdout the version from the args list,
 * and return true
 * 
 * @param pArgs
 * @param args
 * @return boolean
*/
// =================================================
  public static boolean version(String[] pArgs, String[] args) {
  
     boolean returnVal = false;
     
     if ( pArgs != null && pArgs.length > 0 ) 
       for ( String arg : pArgs )
         if ( arg.toLowerCase().startsWith("--version") || arg.equals("--v")) {
           returnVal = true;
           String version = U.getOption(args,  "--version=", "unknown");
           System.out.print("version=" + version + "\n");
           break;
         }
     
     
  return returnVal ;
} // end Method version () ------------------------




} // End of the Class Use
