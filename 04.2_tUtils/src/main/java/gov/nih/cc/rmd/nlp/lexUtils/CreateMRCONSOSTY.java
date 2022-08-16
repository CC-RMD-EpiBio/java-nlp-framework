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
