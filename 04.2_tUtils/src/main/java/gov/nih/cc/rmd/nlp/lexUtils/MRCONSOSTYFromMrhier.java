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
