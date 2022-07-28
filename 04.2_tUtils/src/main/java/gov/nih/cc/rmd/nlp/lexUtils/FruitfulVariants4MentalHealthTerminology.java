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
 * FruitfulVariants, given a string, will retrieve the 
 * all the variants from that string. 
 * 
 *   It will weed out those variants that are deemed
 *   non-productive.  In particular, anything downstream
 *   from an acronym.
 *   
 *
 * @author  Guy Divita 
 * @created Feb 21, 2013
 * 
 * ------------------------------------------------------------
 *
 *
 *
 */ 
// ================================================
package gov.nih.cc.rmd.nlp.lexUtils;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import gov.nih.nlm.nls.lvg.Api.LvgCmdApi;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;




public class FruitfulVariants4MentalHealthTerminology extends FruitfulVariants {
  
 

//-----------------------------------------
 /**
  * Constructor
  * @param pLogger
  * @throws Exception 
  *
  */
 // -----------------------------------------
 public FruitfulVariants4MentalHealthTerminology() throws Exception {
   
  super();
    
   
   
 } // end Constructor() ---------------------

  

  // -----------------------------------------
  /**
   * main 
   *   reads in the input file, pulls the --inputField=X field
   *   (0 offset, first field == 0 ), creates the variant
   *   and appends the variant and variant info at the end of the row
   *   
   *   
   * @param args
   */
  // -----------------------------------------
  public static void main(String[] pArgs) {
    
    
    
    String[] args = setArgs( pArgs);
    String inputFile = U.getOption(args,  "--inputFile=", "/some/dir/mentalHealthFile.csv" ); 
    String dateStamp = U.getOption(args,  "--dateStamp=",  "v1.0");
    String outputFile = U.getOption(args,  "--outputFile=", inputFile + "_variants_" + dateStamp + ".csv" );
    int inputField = Integer.parseInt(U.getOption(args,  "--inputField=", "2"));
    BufferedReader        in = null;
    PrintWriter          out = null;
    String              term = null;
    List<LvgOutputStructure>     allOutputTerms = null;
    List<LvgOutputStructure>     outputTerms1 = null;
    List<LvgOutputStructure>     outputTerms2 = null;
    List<LvgOutputStructure>     outputTerms3 = null;
    List<LvgOutputStructure>     outputTerms4 = null;
    FruitfulVariants4MentalHealthTerminology fruitfulTermsInstance = null;
   
   
  
    System.err.println("Starting lookup");
    // -----------------------
    //  output 
    try {
      in = new BufferedReader( new FileReader(inputFile));
    } catch ( Exception e) { System.err.println("Not to read "  + inputFile ); return; }
  
    // -----------------------
    //  output 
    try {
      out = new PrintWriter( outputFile );
    } catch ( Exception e) { System.err.println("Not able to write to the file  "  + outputFile );  
      try { in.close(); } catch (IOException e1) { } 
      return; 
    }
  
    out.print( LRAGR_FORMAT_HEADER);
    // ------------------------
    // Instantiate the lexUtils
    try {
     
      fruitfulTermsInstance = new FruitfulVariants4MentalHealthTerminology();
      
    
    // take standard input in, run it through
    // the fruitfulTerm method, display the results
  
      String row = null;
      int ctr = 0;
      while ( (row = in.readLine()) != null ) {
        try {
          
          
          if ( row != null && !row.startsWith("#") && ctr > 0 ) {
            String cols[] = U.split(row);
            term = cols[2];
            
          term = fruitfulTermsInstance.filterOutNecAndNos( term );
          term = fruitfulTermsInstance.filterOutParentheticalExpressions( term);  
            
          outputTerms1 = fruitfulTermsInstance.fruitfulVariantsV(term );
          outputTerms2 = fruitfulTermsInstance.permuteTwoWordTerms( term );
          outputTerms3 = fruitfulTermsInstance.ofTwoWordTerms( term );
          outputTerms4 = fruitfulTermsInstance.invertOfTerms (term );
            
          allOutputTerms = new ArrayList<LvgOutputStructure>();
          if ( outputTerms1 != null ) allOutputTerms.addAll( outputTerms1);
          if ( outputTerms2 != null ) allOutputTerms.addAll( outputTerms2);
          if ( outputTerms3 != null ) allOutputTerms.addAll( outputTerms3);
          if ( outputTerms4 != null ) allOutputTerms.addAll( outputTerms4);
          
          }
        } catch (Exception e2) { 
          e2.printStackTrace();
          GLog.println(GLog.ERROR_LEVEL, System.class, "main", 
             "Issue with term: " + term + "\n" + e2.getMessage() );
        }
        if ( allOutputTerms != null ) {
          
           allOutputTerms = fruitfulTermsInstance.filterVariants( allOutputTerms );
           allOutputTerms = fruitfulTermsInstance.uniqVariants( allOutputTerms );
          
           
           for ( LvgOutputStructure  variantRow: allOutputTerms ) 
             out.print( fruitfulTermsInstance.lvgOutputStructureToLRAGR( row, variantRow ));
        }  // end if there are output terms  
       
        ctr++;
      } // end loop through input terms
      out.close();
      
      System.err.print("The output went to " + outputFile + "\n" );
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Not able to read from standard input " + e.getMessage() );
      System.exit(-1);
    } // end loop through the standard input;
    
    // Clean up
    if ( fruitfulTermsInstance != null )
      fruitfulTermsInstance.cleanup();
    try {
      in.close();
    }
    catch (IOException e) {e.printStackTrace(); }
  
    
  } // end Method Main() ---------------------




// =================================================
  /**
   * format 
   * 0    1           2          3              4            5           6             
   * ID|Category|ConceptName|Sources|SourceIdentifiers|OntologySeed|OntologyId
   *   0       1     2      3    4 5  6  7   8      9   10
   * input|variant|<noun>|<base>|v|1|128|1|History|cost|3|
   * 
   * TermID|variant|pos|infl|conceptName|ontologySeed|category|Sources|SourceIdentifiers|OntologyId|History|cost|
   * 
   * @param pRow
   * @param pVariant
   * @return String
  */
  // =================================================
  private static String format(String pRow, LvgOutputStructure pVariant) {
   
    StringBuffer buff = new StringBuffer();
    String[] rowCols = U.split(pRow);
    
   
 try {  
    buff.append(  rowCols[0]);              buff.append("|");
    buff.append(  pVariant.getVariant());   buff.append("|");
    
    
    if ( pVariant.getVariant().contains("-No Output-"))
     return null;
    
    else {
      buff.append(   pVariant.getPos());      buff.append("|");
      buff.append(   pVariant.getInfl());     buff.append("|");
      buff.append(   rowCols[2]);             buff.append("|");
      buff.append(   rowCols[5]);             buff.append("|");
      buff.append(   rowCols[1]);             buff.append("|");
      buff.append(   rowCols[3]);             buff.append("|");
      buff.append(   rowCols[4]);             buff.append("|");
      buff.append(   pVariant.getHistory());  buff.append("|");
      buff.append(   pVariant.getCost());    
    
    buff.append("\n");
    }
 } catch ( Exception e) {
   e.printStackTrace();
   System.err.println(e.toString());

 }
    
    return buff.toString() ;
  } // end Method format() -------------------------




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

  String inputFile = U.getOption(pArgs,  "--inputFile=", "/some/dir/mentalHealthFile.csv" ); 
  String outputFile = U.getOption(pArgs,  "--outputFile=", inputFile + "_variants_" + dateStamp + ".csv" );
  String inputField =  U.getOption(pArgs,  "--inputField=", "2");
  
  
  String   printToLog = U.getOption(pArgs, "--printToLog=", "true");
  String   printToConsole = U.getOption(pArgs, "--printToConsole=", "true");
  String profilePerformanceLogging = U.getOption(pArgs,  "--profilePerformanceLogging=", "false");
 
  String args[] = {
      
      "--inputFile=" + inputFile,
      "--outputFile=" + outputFile,
      "--dateStamp=" + dateStamp,
      "--inputField=" + inputField,
      
     
      "--printToLog=" + printToLog,
      "--profilePerformanceLogging=" + profilePerformanceLogging,
      "--printToConsole=" + printToConsole,
     
     
  };

   // need a help option here 
  // This method assumes that there is a resources/BodyFunctionApplication.txt
  if ( Use.usageAndExitIfHelp( "FruitfulVariants4MentalHealthTerminology", pArgs, args ) )
      Runtime.getRuntime().exit(0);
                        

  return args;

}  // End Method setArgs() -----------------------

  // private Logger logger;
  // -----------------------------------------
  // Class Variables
  // -----------------------------------------
  private LvgCmdApi fruitfulVariantsCmdApi  = null; 
 
  public static final String  LRAGR_FORMAT_HEADER =  
  "#  Mental Functioning Terminology Format \n" + 
  "#TermID|variant|pos|infl|conceptName|ontologySeed|category|Sources|SourceIdentifiers|OntologyId|History|cost\n" +
  "#\n";
  
  
} // end Class lexUtils() --------------------
