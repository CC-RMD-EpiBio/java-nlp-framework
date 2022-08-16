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
 * TestVTTSnippitUtilities reads in a vtt snippet file and writes it out again
 *
 *
 * @author  Guy Divita 
 * @created Sep 19, 2017
 *
 **  *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.snippet;

import java.io.File;

import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

public class TestVTTSnippitUtilities {

  // =============================================
  /**
   * main 
   * 
   * @param pArgs
   *
   */
  // =============================================
  public static void main(String[] pArgs) {
   
    try {

      String[]    args = setArgs(pArgs);
      String  inputDir = U.getOption(args,  "--inputDir=",  "");
      String outputDir = U.getOption(args,  "--outputDir=", "");
      
      // ----------------------------
      // Make the output directory
      U.mkDir(outputDir );
      // ----------------------------
      // read in .vtt files from the inputDir=
      
      File aDir = new File( inputDir);
      if ( aDir.exists() && aDir.canRead()) {
        File[] someFiles = aDir.listFiles();
        if ( someFiles != null ) {
          for ( File aFile: someFiles ) {
            String fileName = aFile.getAbsolutePath();
            if ( fileName.endsWith(".vtt")) {
              System.err.println("Reading in file " + fileName);
              SnippetsContainer vttSnippetsContainer = VTTSnippetUtils.readVTTSnippetFile(aFile);
              
              RedSnippetsContainer redSnippetsContainer = new RedSnippetsContainer( vttSnippetsContainer );
              
           
              
              String outputFileName = outputDir + "/" + aFile.getName() + ".snippets.vtt";
              System.err.println("Writing out file " + outputFileName );
               // vttSnippetsContainer.write( outputFileName );
             
            //  redSnippetsContainer.convertSnippetsToYesNo();
              redSnippetsContainer.write( outputFileName );
              
             
              
              System.err.println("+");
              
            }
          } // loop thru the files of the dir
        }  // there are files in the inputDir
        
      } // end if inputDir exists
      
      System.err.println("Dohn");
      // ----------------------------
      // for each vtt file, parse thru it make a new one from the 
      // containers
      
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue within method main " + e.getMessage());
     
    }
  } // End Method main ============

  // ------------------------------------------
  /**
   * setArgs takes command line args, which override
   *         default args set here
   * 
   * 
   * @return String
   */
  // ------------------------------------------
  public static String[] setArgs(String pArgs[]) {

  
    // -------------------------------------
    // dateStamp
    String dateStamp = U.getDateStampSimple();

    // -------------------------------------
    // Input and Output

    String     drive = U.getOption(pArgs,  "--drive=", "d:");
    String  inputDir = U.getOption(pArgs,  "--inputDir=",  drive + "/data/vhaslcdivitg/data/ORD_Samore_201606098D/diarrhea2017-09-14_13_05_18/snippets");
    String outputDir = U.getOption(pArgs,  "--outputDir=", drive + "/data/vhaslcdivitg/data/ORD_Samore_201606098D/diarrhea" + dateStamp + "/redSnippets");
    
    String    logDir = outputDir + "/logs"; 
    String args[] = {
        "--inputDir=" + inputDir,
        "--outputDir=" + outputDir,
        "--logDir="    + logDir
    };
    
    String description = 
        " \n\n\n TestVTTSnippetUtilities reads in and writes out vtt snippet files\n" +
        " in the v3NLPFramework format.\n\n"   ;
            

    if ( Use.usageAndExitIfHelp(TestVTTSnippitUtilities.class.getCanonicalName(), pArgs, args) ) {
      System.out.print( description);
      System.exit(0);
    }
     
     

    return args;

  }  // End Method setArgs() -----------------------
  

} // End Class TestVTTSnippitUtilities ============
