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
null
