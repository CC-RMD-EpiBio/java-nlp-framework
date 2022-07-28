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
// =======================================================
/**
 * TermLookup Looks up terms from within the SPECIALIST Lexicon and
 * any local terminologies that are plugged in.
 *
 * @author  divita
 * @created Apr 17, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils.term;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.LexRecord;

import gov.nih.cc.rmd.nlp.framework.utils.DirTools;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * @author divita
 *
 */
public final class TermLookup {

  // =======================================================
  /**
   * main 
   * 
   * @param args
   */
  // =======================================================
  public static void main(String[] pArgs) {
   
    
    try {
      // -------------------------------
      // Get command line arguments to set input/output ... 
      String[] args = setArgs( pArgs);
    
      String            help = U.getOption(pArgs, "--help",  (String)null); 
      if ( help == null)
                        help = U.getOption(pArgs, "-h", (String)null);
      
      if ( help != null) {
        usage();
        return;
      }
      
      String       inputFile = U.getOption(pArgs, "--inputFile=",  "STDIN");
      String      outputFile = U.getOption(pArgs, "--outputFile=", "STDOUT");
      String        inputDir = U.getOption(pArgs, "--inputDir=",  (String) null);
      String       outputDir = U.getOption(pArgs, "--outputDir=", "./");
      
      // ------------------------------
      // get local lexica
      String   localLexicons = U.getOption(pArgs, "--localLexicons=" ,  "");
      String   localLexiconz[] = getLocalLexica( args); U.split(localLexicons, ":");
      
     
      LexicalLookup termLookup = new LexicalLookup(localLexiconz);
      
      // ------------------------------
      // where will the input be coming from? files or directories
      
      if ( inputDir != null && inputDir.length() > 0  )
        processInputDir( termLookup, inputDir, outputDir );
      else if ( inputFile != null && !inputFile.equals("STDIN"))
        processInputFile( termLookup, inputFile, outputFile);
      else 
        processSTDIN( termLookup, outputFile);
        
    
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with term lookup " + e.toString();
      System.err.println(msg);
    }
    
    
  }  // End Method main() ======================
  

  // =======================================================
  /**
   * usage 
   * 
   */
  // =======================================================
  public  static void usage() {
    
    String buff = 
    "TermLookup is a command line utility that takes text as input and returns \n" +
    "rows of terms found from the text. Each row is an instance of a term that \n" +
    "was found.  The row includes a uniq identifier from the lexicon, a semantic \n" +
    "type (if there is one) and any extra information the lexicon might have for \n" +
    "this term. \n\n" +
    
    "TermLookup has the following command line options : \n\n" +
   
   
    "          Argument    |   Default Value  | Notes                                 \n" +
    "        --------------+------------------+------                                 \n" +
    "         --inputFile= | STDIN            | or a computable path and filename     \n" +
    "        --outputFile= | STDOUT           | or a computable path and filename     \n" +
    "          --inputDir= |                  | if set, will read files from this dir \n" +
    "         --outputDir= |                  | if inputDir is set, outputDir will be ./ or what is set here \n" +
    "     --localLexicons= |                  | colon separated list of .LRAGR files   \n\n" +
    
    " Current limitations of the command line version: \n " + 
    "    No Sentence parsing is done for this version ";
    
    System.err.println(buff);
    
    
  } // End Method usage() ======================
  


  // ------------------------------------------
    /**
     * setArgs  These are arguments that can be set on the command line
     *          to pass in the following arguments:
     *          Argument    |   Default Value  | Notes
     *        --------------+------------------+------
     *         --inputFile= | STDIN            | or a computable path and filename
     *        --outputFile= | STDOUT           | or a computable path and filename
     *          --inputDir= |                  | if set, will read files from this dir
     *         --outputDir= |                  | if inputDir is set, outputDir will be ./ or what is set here
     *     --localLexicons= |                  | colon separated list of .LRAGR files 
     *
     * @return String[]
     */
    // ------------------------------------------
  public static String[] setArgs(String pArgs[]) {
      
   
      String       inputFile = U.getOption(pArgs, "--inputFile=",  "STDIN");
      String      outputFile = U.getOption(pArgs, "--outputFile=", "STDOUT");
      String        inputDir = U.getOption(pArgs, "--inputDir=",  "");
      String       outputDir = U.getOption(pArgs, "--outputDir=", "./");
      String   localLexicons = U.getOption(pArgs, "--localLexicons=" ,  "");
      
     
      
      
   
    
      String args[] = {  "--inputFile=" + inputFile,
                         "--outputFile=" + outputFile, 
                         
                         "--inputDir=" + inputDir,
                         "--outputDir=" + outputDir,
                         
                         "--localLexicons=" + localLexicons
              
                      }; 
      
      return args;
      
      
      // End Method setArgs() -----------------------
    }


  // =======================================================
  /**
   * processSTDIN processes input from the standard input.
   * The output goes to the passed in outputfile.  By default,
   * this is STDOUT.
   * 
   * processSTDIN will process the input differently than
   * processing an input file in that \n's will indicate
   * the end of a sentence.
   * 
   * @param termLookup
   * @param pOutputFile
   */
  // =======================================================
  private static void processSTDIN(LexicalLookup termLookup, String pOutputFile) throws Exception {
    
    BufferedReader in = null;
    PrintWriter   out = openOutput( pOutputFile);
    
  
    
    try {
      System.err.print("Input term -> ");
      in = new BufferedReader(new InputStreamReader(System.in));
      String line = null;
      while ( (line = in.readLine()) != null  ) {
        
     
        List<List<LexRecord>> termLists = termLookup.lookup(line);
        
        reportTerms( out, termLists);
        
        System.out.flush();
        System.err.flush();
        System.err.print("Input term ->");
      } // end loop through input
    
      out.close();
    } catch (Exception e ) {
      e.printStackTrace();
      String msg = "Issue with reading from standard input " + e.toString();
      System.err.println (msg);
    }
    
  } // =======================================================


  // =======================================================
  /**
   * processInputFile sentence tokenizes then term tokenizes each input file. Terms are
   * put to the output file.
   * 
   * @param pTermLookup
   * @param pInputFile
   * @param pOutputFile
   * @throws Exception 
   */
  // =======================================================
  private static void processInputFile(LexicalLookup pTermLookup, String pInputFile, String pOutputFile) throws Exception {
  
    try {
      String inputBuff = U.readClassPathResource(pInputFile);
      
      PrintWriter out = openOutput( pOutputFile);
  
      // --------------------------------
      // Sentence tokenize the input buff
      //    not do'able yet
      //  
      // --------------------------------
      List<List<LexRecord>> terms = pTermLookup.lookup(inputBuff);
      
      reportTerms( out, terms);
      out.close();
      
      
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue reading the input file " + pInputFile + " :" + e.toString();
      System.err.println(msg);
      throw new Exception (msg);
    }
      
  } // End Method processInputFile() ======================


  // =======================================================
  /**
   * processInputDir processes the contents of the files from 
   * a given directory.  Output files are put into the outputDir as
   * inputFileName."term" files.
   * 
   * @param pTermLookup
   * @param pInputDir
   * @param pOutputDir
   * @throws Exception 
   */
  // =======================================================
  private static void processInputDir(LexicalLookup pTermLookup, String pInputDir, String pOutputDir) throws Exception {
    
    DirTools dirTools = null;
    
    try {
      checkViabilityOfOutputDir( pOutputDir );
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with writability to the outputDir " + e.toString();
      System.err.println(msg);
    }
    
    try {
      dirTools = new DirTools(pInputDir);
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with reading from the directory " + pInputDir + " :" + e.toString();
      System.err.println(msg);
      throw new Exception(msg );
    }
    
    while ( dirTools.hasNext() ) {
      String         inputFile = dirTools.getNext();
      String inputFileNameOnly = U.getNameWithoutNameSpace(inputFile);
      String   fileName = U.getFileNamePrefix(inputFileNameOnly) + ".term";
      
      processInputFile ( pTermLookup, inputFile, pOutputDir + "/" + fileName  );
      
    } // end loop through the files in the input dir
  
  
  } // End Method processInputDir() ======================
  


  // =======================================================
  /**
   * reportTerms reports each mrconsosty row kind of info one per line.
   * There may be duplicates because the term might have multiple parts of speech like noun|singular noun|plural
   * 
   * @param pOut
   * @param pTermLists
   * 
   */
  // =======================================================
  private static void reportTerms(PrintWriter pOut, List<List<LexRecord>> pTermLists) {
    if ( pTermLists != null ) {
      for (List<LexRecord> termList : pTermLists ) {
        if ( termList != null ) {
          for ( LexRecord term : termList ) {
            String outputRow = term.toPipedString() + "\n";
            pOut.print( outputRow);
            pOut.flush();
           
          }
        }
      }
    } else {
      pOut.print("No output\n");
    }
    // End Method reportTerms() ======================
  }


  // =======================================================
  /**
   * checkViabilityOfOutputDir checks to see if the outputDir
   * exists, if it does not, it creates it.  Checks to see if
   * the outputDir can be written to.  Thows an exception if
   * it cannot write
   * 
   * @param pOutputDir
   * @throws Exception 
   */
  // =======================================================
  private static void checkViabilityOfOutputDir(String pOutputDir) throws Exception {
    
   
    File aDir = new File( pOutputDir );
    if ( !aDir.exists())
      
      try {
        U.mkDir(pOutputDir);
      } catch (Exception e) {
        e.printStackTrace();
        String msg = "Issue with making the outputDir " + e.toString();
        System.err.println(msg);
        throw new Exception (msg);
      }
    
      if (!aDir.canWrite()) {
        String msg = "Issue with being able to write to the outputDir ";
        System.err.println(msg);
        throw new Exception (msg);
      }
    
  }  // End Method checkViabilityOfOutputDir() ======================
  


  /**
   * openOutput returns a printWriter
   * 
   * @param pOutputFile
   * @return
   * @throws Exception 
   */
  // =======================================================
  private static PrintWriter openOutput(String pOutputFile) throws Exception {
 
    PrintWriter out = null;
    try {
      
      if ( pOutputFile != null && !pOutputFile.equals("STDOUT") && !pOutputFile.equals("STDERR")) {
       out = new PrintWriter( pOutputFile);
       
      } else {
        out = new PrintWriter( System.out);
      }
                
      } catch (Exception e) {
      e.printStackTrace();
      String msg ="Issue opening up the output file " + pOutputFile + ": " + e.toString();
      System.err.println(msg);
      throw new Exception( msg);
      }
    return out;
    // End Method openOutput() ======================
  }


  // End Method processSTDIN() ======================
  


  // =======================================================
  /**
   * getLocalLexica returns an array of local lexicon file names
   * 
   * @param pArgs  with one of the args being --localLexicons= xx1.LRAGR:xx2.LRAGR 
   * @return
   */
  // =======================================================
  public static final String[] getLocalLexica(String[] pArgs) {
    
    String[] localLexiconz = null;
    if ( pArgs != null ) {
      String     localLexicons = U.getOption(pArgs, "--localLexicons=" ,  "");
      if ( localLexicons != null && localLexicons.length() > 0 ) {
          localLexiconz = U.split(localLexicons, ":");
      }
    }
    
    return localLexiconz;
    // End Method getLocalLexica() ======================
  }
  

  
  
} // end Class TermLookup -----------------------
