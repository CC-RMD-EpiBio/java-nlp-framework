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
package gov.nih.cc.rmd.nlp.lexUtils;
// =================================================
/**
 * mrconsostyFromMrhier.java  Summary [TBD]
 *
 * @author     Guy Divita
 * @created    Jul 23, 2019
 * 
*/
// =================================================


import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

/**
 * @author divitag2
 *
 */
public class MRCONSOSTYFromMrhier {

  // =================================================
  /**
   * main
   * 
   * @param pArgs
  */
  // =================================================
  public static void main(String[] pArgs) {
    
    try {
      
      String[] args = setArgs(pArgs );
      // Read in arguments
      
      String     inputDir = U.getOption(pArgs,  "--inputDir=", "./2019AA/META");
      String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_terminology_" );
      String  mrconsoFile = U.getOption( pArgs, "--mrconso=", inputDir + "/mrconso.rrf");
      String   mrhierFile = U.getOption( pArgs, "--mrhier=",  inputDir + "/mrhier.rrf");
      String    mrrelFile = U.getOption( pArgs, "--mrrel=",   inputDir + "/mrrel.rrf");
      String    mrstyFile = U.getOption( pArgs, "--mrsty=",   inputDir + "/mrsty.rrf");
      
      
      // read in mrconso
      MRCONSO mrconso = new MRCONSO( args );
    
      // read in mrsty 
      
      // read in mrhier
      
      //     Repeat until done
      // +---------------------
      // | find a seed term
      // |
      // | walk through data store to find aui/cui decendents in mrhier
      // |   
      // | walk through list of cuis to get mrconso
      // |
      // | talk through list of cuis to get mrsty
      // |
      // |merge mrconso and mrsty rows for list of cuis
      // +-------------------
      
    } catch ( Exception e ) {
      e.printStackTrace();
      System.err.println("Issue with creating mrconsosty from mrhier " + e.toString());
    }

  } // end Method main() -----------------------------
  
//------------------------------------------
 /**
  * setArgs
  * 
  * 
  * @return
  */
 // ------------------------------------------
 public static String[] setArgs(String pArgs[]) {

   // -------------------------------------
   // dateStamp
   String dateStamp = U.getDateStampSimple();

   // -------------------------------------
   // Input and Output

   String     inputDir = U.getOption(pArgs,  "--inputDir=", "./2019AA/META");
   String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_terminology_" + dateStamp);
   String      mrconso = U.getOption( pArgs, "--mrconso=", inputDir + "/mrconso.rrf");
   String       mrhier = U.getOption( pArgs, "--mrhier=",  inputDir + "/mrhier.rrf");
   String        mrrel = U.getOption( pArgs, "--mrrel=",   inputDir + "/mrrel.rrf");
   String        mrsty = U.getOption( pArgs, "--mrsty=",   inputDir + "/mrsty.rrf");
   
  
   String       logDir = U.getOption(pArgs, "--logDir=",   outputDir + "/logs" ); 
   String   printToLog = U.getOption(pArgs, "--printToLog=", "true");

 
   String args[] = {
       
       "--inputDir=" + inputDir,
       "--outputDir=" + outputDir,
     
       "--mrconso="    + mrconso,
       "--mrhier="     + mrhier,
       "--mrrel="      + mrrel,
       "--mrsty="      + mrsty,
       
       "--logDir=" + logDir,
       "--printToLog=" + printToLog,
      
      
   };

   
      
   if ( Use.usageAndExitIfHelp("MrconsoFromMrhier",   pArgs, args  ))
     System.exit(0);
    

   return args;

 }  // End Method setArgs() -----------------------



 // ------------------------------------------------
 // Global Variables
 // ------------------------------------------------

} // end Class mrconsostyFrom MrHier() ---------------
