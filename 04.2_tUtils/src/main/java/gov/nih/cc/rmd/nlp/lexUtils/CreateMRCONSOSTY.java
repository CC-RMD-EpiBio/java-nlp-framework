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
 * MRCONSO reads in and indexes the mrconso table
 * This resource is indexed on cui and aui.
 *
 * @author     Guy Divita
 * @created    Jul 24, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.lexUtils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

/**
 * @author divitag2
 *
 */
public class CreateMRCONSOSTY {

  


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
      
      String     inputDir = U.getOption(args,  "--inputDir=", "./2019AA/META");
      String    outputDir = U.getOption(args, "--outputDir=", inputDir + "_terminology_" );
      String   outputFile = U.getOption(args,  "--outputFile=", outputDir + "/" + "MRCONSOSTY.RRF" );
    
      U.mkDir( outputDir);
      PrintWriter out = new PrintWriter( outputFile);
      PerformanceMeter meter = new PerformanceMeter();
    
       // read in mrconso
       MRCONSO mrconso = new MRCONSO( args);
       
       // read in mrsty 
       MRSTY mrsty = new MRSTY( args);
      
       // loop through the cuis of mrconso
       
       for ( int i = 0; i < mrconso.getSize(); i++) {
          char[] row_ = mrconso.getRow(i);
          if ( row_ == null || row_.length == 0 ) continue;
          String mrconsoRow = new String ( row_);
          
          String aCui = MRCONSO.getCui(mrconsoRow);
          
          List<String> mrstyRows = mrsty.getRowsFromCui(aCui);
          
          if ( mrstyRows != null && !mrstyRows.isEmpty())
            for ( String mrstyRow : mrstyRows ) {
             String buff = MRCONSOSTY.merge(mrconsoRow, mrstyRow) + "\n" ;
             out.print(buff);
             out.flush();
            }
          else {
            // there is no semantic type info for this concept (i.e. home made concepts )
            String buff = MRCONSOSTY.merge( mrconsoRow, MRSTY.getTemplateRowForCui( aCui) );
            out.print(buff + "\n");
            out.flush();
          }
         
       }
         
      out.close();  
      
      
      meter.stop();
      
      System.err.println("Dohn");
      
    } catch ( Exception e ) {
      e.printStackTrace();
      System.err.println("Issue with creating mrconsosty from mrhier " + e.toString());
    }


  } // end Method main() ----------------------------

  
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

   String     inputDir = U.getOption(pArgs,  "--inputDir=", "C:/work/softwareRepos/framework-legacy/00_legacy/01_sophia_resources/src/main/resources/resources/vinciNLPFramework/sophia/2020.07.01/Custom/META");
   String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_terminology_" + dateStamp);
   String      mrconso = U.getOption( pArgs, "--mrconso=", inputDir + "/MRCONSO.RRF");
   String totalNumberOfMrconsoRows = U.getOption(pArgs,"--totalNumberOfMrconsoRows=", "8946369");
   String      MRSTY = U.getOption( pArgs, "--MRSTY=", inputDir + "/MRSTY.RRF");
   String totalNumberOfMRSTYRows = U.getOption(pArgs,"--totalNumberOfMRSTYRows=", "5545407");
   String   outputFile = U.getOption(pArgs,  "--outputFile=", outputDir + "/" + "MRCONSOSTY.RRF" );
   
   
  
   
  
   String args[] = {
       
       "--inputDir=" + inputDir,
       "--outputDir=" + outputDir,
       "--outputFile=" + outputFile,
       "--mrconso="    + mrconso,
       "--MRSTY=" + MRSTY,
       "--totalNumberOfMrconsoRows=" + totalNumberOfMrconsoRows,
       "--totalNumberOfMRSTYRows=" + totalNumberOfMRSTYRows
      
   };

   
      
   if ( Use.usageAndExitIfHelp("MRCONSO",   pArgs, args  ))
     System.exit(0);
    

   return args;

 }  // End Method setArgs() -----------------------

  // ------------------------------------------------
  // Class Variables
  // ------------------------------------------------
  
}
