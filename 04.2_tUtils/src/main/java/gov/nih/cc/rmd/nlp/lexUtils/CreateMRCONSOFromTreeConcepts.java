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
 * CreateMRCONSOFromTreeConcepts
 * 
 * reads in a file containing high level concept cuis (--ontologyCuis=./mentalFunctionOntologyConcepts.txt)
 * translates the cuis to aui's, then finds the dependent concept mrconso rows.
 * 
 * From there, if we wanted to make an lragr, use the LRAGRFromMRCONSOSTY program.
 * 
 * Wait, that needs sty part - [TBD]
 * 
 *
 * @author Guy Divita
 * @created Aug 9, 2019
 * 
 */
// =================================================
package gov.nih.cc.rmd.nlp.lexUtils;

import java.io.PrintWriter;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

/**
 * @author Guy
 *
 */
public class CreateMRCONSOFromTreeConcepts {

  // =================================================
  /**
   * main
   * 
   * @param pArgs
   */
  // =================================================
  public static void main(String[] pArgs) {

    PrintWriter out = null;
    try {

      String[] args = setArgs(pArgs);
      // Read in arguments

      String inputDir = U.getOption(pArgs, "--inputDir=", "./2019AA/META");
      String outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_terminology_");
      String mrhierFile = U.getOption(pArgs, "--mrhier=", inputDir + "/mrhier.rrf");
      String totalNumberOfmrhierRows = U.getOption(pArgs, "--totalNumberOfmrhierRows=", "10");
      String conceptsFile = U.getOption(pArgs, "--conceptsFile=", "./mentalHealthOntologyConcepts.txt");

      out = new PrintWriter(outputDir + "/" + "DecendentMRCONSO.RRF");

      PerformanceMeter meter = new PerformanceMeter();

      // CreateMRCONSOFromTreeConcepts

      getRowsFromMrhier(args, conceptsFile, out);

      getRowsFromMrRel(args, conceptsFile, out);

      out.close();
      System.err.println("Dohn");

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with creating mrconso from tree concepts " + e.toString());
    }

  } // End Method main() ----------------------------

  // =================================================
  /**
   * getRowsFromMrhier
   * 
   * @param args
   * @param conceptsFile
   * @param out
   * @throws Exception
   */
  // =================================================
  private static void getRowsFromMrhier(String[] args, String conceptsFile, PrintWriter out) throws Exception {

    MRHIER mrhier = new MRHIER(args);

    String[] conceptRows = U.readFileIntoStringArray(conceptsFile);

    if (conceptRows != null && conceptRows.length > 0) {
      for (String conceptRow : conceptRows) {
        if (conceptRow == null || conceptRow.trim().length() < 1 || conceptRow.startsWith("#")) continue;

        String[] cols = U.split(conceptRow);

        if (!cols[0].trim().startsWith("C")) {
          System.err.println("Issue with this line : " + conceptRow);
          continue;
        }

        try {
          List<String> decendentMRCONSORows = mrhier.getDecendentRowsForCui(cols[0].trim());

          if (decendentMRCONSORows != null && !decendentMRCONSORows.isEmpty()) for (String aRow : decendentMRCONSORows)
            out.print(aRow + "\n");

        } catch (Exception e) {
          e.printStackTrace();
          System.err.println("issue with row " + conceptRow);

        }
      }
    }
    mrhier.close();

  } // end Method getRowsFromMrhier() ------

  // =================================================
  /**
   * getRowsFromMrRel
   * 
   * @param args
   * @param conceptsFile
   * @param out
   * @throws Exception
   */
  // =================================================
  private static void getRowsFromMrRel(String[] args, String conceptsFile, PrintWriter out) throws Exception {
  
    MRREL mrrel = new MRREL(args);

    // read the concepts file

    String[] conceptRows = U.readFileIntoStringArray(conceptsFile);

    if (conceptRows != null && conceptRows.length > 0) {
      for (String conceptRow : conceptRows) {
        if (conceptRow == null || conceptRow.trim().length() < 1 || conceptRow.startsWith("#")) continue;

        String[] cols = U.split(conceptRow);

        if (!cols[0].trim().startsWith("C")) {
          System.err.println("Issue with this line : " + conceptRow);
          continue;
        }

        try {
          List<String> decendentMRCONSORows = mrrel.getDecendentRowsForCui(cols[0].trim());

          if (decendentMRCONSORows != null && !decendentMRCONSORows.isEmpty()) for (String aRow : decendentMRCONSORows)
            out.print(aRow + "\n");

        } catch (Exception e) {
          e.printStackTrace();
          System.err.println("issue with row " + conceptRow);

        }
      }
    }
    
    mrrel.close();

  } // end Method getRowsFromMrrel() ------

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
    // dateStamp
    String dateStamp = U.getDateStampSimple();

    // -------------------------------------
    // Input and Output

    String inputDir = U.getOption(pArgs, "--inputDir=", "./2020AA-Custom1/META");
    String outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_terminology_" + dateStamp);
    String mrhier = U.getOption(pArgs, "--mrhier=", inputDir + "/mrhier.RRF");
    String totalNumberOfMRHIERRows = U.getOption(pArgs, "--totalNumberOfMRHIERRows=", "17406195");
    String totalNumberOfMrconsoRows = U.getOption(pArgs, "--totalNumberOfMrconsoRows=", "8946369");
    String conceptsFile = U.getOption(pArgs, "--conceptsFile=", "./mentalHealthOntologyConcepts.txt");

    String args[] = {

        "--inputDir=" + inputDir, "--outputDir=" + outputDir, "--mrhier=" + mrhier,
        "--totalNumberOfMrconsoRows=" + totalNumberOfMrconsoRows, "--totalNumberOfMRHIERRows=" + totalNumberOfMRHIERRows,
        "--conceptsFile=" + conceptsFile };

    if (Use.usageAndExitIfHelp("CreateMRCONSOFromTreeConcepts", pArgs, args)) System.exit(0);

    return args;

  } // End Method setArgs() -----------------------

}
