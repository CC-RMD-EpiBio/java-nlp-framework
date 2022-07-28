/*******************************************************************************
 * ---------------------------------------------------------------------------
 * NIH Clinical Center
 * Department of Rehabilitation
 * Epidemiology and Biostatistics Branch
 * 2019
 * 
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * 
 * This license allows you to use, share and adapt for any purpose, provided:
 * Provide attribution to the creators of this work within your work.
 * Indicate if changes were made to this work.
 * No claim to merchantability, implied warranty, or liability can be made.
 * 
 * When attributing this code, please make reference to
 * [citation/url here] .
 * 
 * In the absence of a specific paper or url listed above, reference http://clinicalcenter.nih.gov/rmd/eb/nlp
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 ******************************************************************************/
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
