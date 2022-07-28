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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.nih.nlm.nls.lvg.Api.LvgCmdApi;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;




public class FruitfulVariants {
  
  // -----------------------------------------
  /**
   * Constructor
   * @param pLogger
   * @throws Exception 
   * @deprecated
   */
  // -----------------------------------------
  public FruitfulVariants(Object pLogger) throws Exception {
    
    // Open up a database connection or load in this data
    // from a hash
   
   // this.logger = pLogger;
    this.fruitfulVariantsCmdApi = new LvgCmdApi("-f:0:S:v -f:n -m -n -SC -SI -CR:o");
    this.termLookupCmdApi =  new LvgCmdApi("-f:Ln -m -SC -SI -CR:o");
    this.stripNecAndNosCmdApi =  new LvgCmdApi("-f:0 -m -SC -SI -CR:o");
     
    
    
  } // end Constructor() ---------------------

//-----------------------------------------
 /**
  * Constructor
  * @param pLogger
  * @throws Exception 
  *
  */
 // -----------------------------------------
 public FruitfulVariants() throws Exception {
   
   // Open up a database connection or load in this data
   // from a hash
  
  // this.logger = null;
   this.fruitfulVariantsCmdApi = new LvgCmdApi("-f:v -f:n -m -SC -SI -CR:o");  // I took out the -n to make -no output rows 
   this.termLookupCmdApi =  new LvgCmdApi("-f:Ln -m -SC -SI -CR:o");
   this.stripNecAndNosCmdApi =  new LvgCmdApi("-f:0 -m -SC -SI -CR:o");
   
   
 } // end Constructor() ---------------------


//=================================================
/**
 * lvgOutputStructureToLRAGR  takes lvgOutputStructure'd instances and turns them into lragr rows
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
public final  String lvgOutputStructureToLRAGR(String pRow, LvgOutputStructure pVariant) {
 
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
    if ( rowCols.length > 1 ) buff.append(   rowCols[2]);             buff.append("|");
    if ( rowCols.length > 5 ) buff.append(   rowCols[5]);             buff.append("|");
    if ( rowCols.length > 1 ) buff.append(   rowCols[1]);             buff.append("|");
    if ( rowCols.length > 3 ) 
      buff.append(   rowCols[3]);             buff.append("|");
    if ( rowCols.length > 4 ) buff.append(   rowCols[4]);             buff.append("|");
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


 
  // -----------------------------------------
  /**
   * fruitfulVariants  Given a string, will retrieve the 
   * variants generated, and the lexical information
   * along with it - including the variant history from 
   * input to term, and the variant's part of speech/inflection
   * 
   * @param pTerm
   * @return List<String>
   * @throws Exception 
   */
  // -----------------------------------------
  public final String[] fruitfulVariants(String pTerm) throws Exception {
  
    String variants[] = null;
    try {
     
      ArrayList<String> allVariants = new ArrayList<String>();
    
      
 
      // ------------------------------------
      // Retrieve inflections of the synonyms
      // ------------------------------------
      String variantz = this.fruitfulVariantsCmdApi.MutateToString(pTerm);
    
      variants = variantz.split("\n");
      for (int z = 0; z < variants.length; z++) {
        String buff = variants[z];
        String buff2 = buff.substring(0,buff.length()-1);
        allVariants.add(buff2);
      }
      variants = allVariants.toArray(new String[allVariants.size()]);
      
      // -----------------------------------
      // Uniq them 
      // -----------------------------------
     // finalVariants = uniq( allVariantz);      

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "fruitfulSynonyms", 
      "Issue here with fruitfulVariants \n" + e.getMessage() );
      
    }
    
  
    return variants;
  } // end Method fruitfulSynonyms() ------------

  
//-----------------------------------------
 /**
  * fruitfulVariantsV  Given a string, will retrieve the 
  * variants generated, and the lexical information
  * along with it - including the variant history from 
  * input to term, and the variant's part of speech/inflection
  * 
  * This version uses the "V" version of Generate Variants - which is
  * a lookup rather than a generation of the terms 
  *
  * Only the first of unique variants is kept.  Hopefully, it's the shortest distance one.
  *
  * @param pTerm
  * @return List<String>
  * @throws Exception 
  */
 // -----------------------------------------
 public final List<LvgOutputStructure> fruitfulVariantsV(String pTerm) throws Exception {
 
   ArrayList<LvgOutputStructure> variants = null;
   HashSet<String> uniqVariants = new HashSet<String>( );
   
   try {
    
     variants = new ArrayList<LvgOutputStructure>();
   
     

     // ------------------------------------
     // Retrieve inflections of the synonyms
     // ------------------------------------
     String variantz = this.fruitfulVariantsCmdApi.MutateToString(pTerm);
   
     String[] rows = U.split(variantz,"\n");
     for (  String row: rows ) {
       if ( row != null && row.trim().length() > 0 ) {
         String cols[] = U.split(row);
         String key = cols[1];
         if ( !uniqVariants.contains( key )) {
           variants.add ( GLvgUtils.parseLvgOutput( row ));
           uniqVariants.add( key);
         }
       }
     }
     
  
   } catch (Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "fruitfulSynonyms", 
     "Issue here with fruitfulVariants \n" + e.getMessage() );
     
   }
   
 
   return variants;
 } // end Method fruitfulSynonyms() ------------
//-----------------------------------------
 /**
  * termLookup returns lragr info from known terms
  * Will return null for terms not found in the lexicon
  * 
  * @param pTerm
  * @return List<String>
  * @throws Exception 
  */
 // -----------------------------------------
 public final String[] termLookup(String pTerm) throws Exception {
 
   String variants[] = null;
   try {
     // String[] finalVariants = null;
     // String[] synonyms = null;
     ArrayList<String> allVariants = new ArrayList<String>();
   
     

     // ------------------------------------
     // Retrieve inflections of the synonyms
     // ------------------------------------
     String variantz = this.termLookupCmdApi.MutateToString(pTerm);
   
     if ( variantz != null && variantz.trim().length() > 0 ) {
     variants = variantz.split("\n");
     for (int z = 0; z < variants.length; z++) {
       String buff = variants[z];
       String buff2 = buff.substring(0,buff.length()-1);
       allVariants.add(buff2);
     }
     variants = allVariants.toArray(new String[allVariants.size()]);
     
     // -----------------------------------
     // Uniq them 
     // -----------------------------------
    // finalVariants = uniq( allVariantz);      
     }
     
   } catch (Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "termLookup", "Issue here with termLookup \n" + e.getMessage() );
     
   }
   
 
   return variants;
 } // end Method termLookup() ------------
 
  

//=================================================
 /**
  * permuteTwoWordTerms - if the term has two words, this will
  * permute the variants of them otherwise null is returned.
  * 
  * @param pTerm
  * @return  List<LvgOutputStructure> 
 * @throws Exception 
 */
 // =================================================
   public final List<LvgOutputStructure>  permuteTwoWordTerms( String pTerm ) throws Exception {
     
     List<LvgOutputStructure> returnVal = null;
   
     String words[] = U.split(pTerm.toLowerCase(), " " );
   
     if ( words.length == 2) {
   
       String termA = words[0];
       String termB = words[1];
       returnVal = permuteTwoWordTerms( termA, termB);
     }
     
     return returnVal;
   } //end Method permuteWordTerms() -------------------



   // =================================================
  /**
   * permuteTwoWordTerms 
   * 
   *  For a term that consists of a b
   *  with variants for a1 a2 a3  and b1 b2 b3
   *  return a1 b         a1 b1   a3 b1
   *         a2 b         a1 b2   a3 b2
   *         a3 b         a1 b3   a3 b3  
   *         a  b1        a2 b1   
   *         a  b2        a2 b2
   *         a  b3        a2 b3
   *    
   * @param pTermA
   * @param pTermB
   * @return String[]
   * @throws Exception 
  */
  // =================================================
    public final List<LvgOutputStructure> permuteTwoWordTerms(String pTermA, String pTermB) throws Exception {
   
     
      List<LvgOutputStructure> buff = new ArrayList<LvgOutputStructure>();
   
      String[] variantsA = this.fruitfulVariants( pTermA );
      String[] variantsB = this.fruitfulVariants( pTermB );
      
      if ( variantsA != null && variantsA.length > 0 )
        if ( variantsB != null && variantsB.length > 0 )
          for ( String variantA : variantsA )
            for ( String variantB : variantsB )
              buff.add( createTermFromParts( variantA, variantB) );
      
      
      
    return buff;
    }  // end Method permuteTwoWordTerms() ------------

  // =================================================
  /**
   * createTermFromParts 
   * 
   * @param variantA
   * @param variantB
   * @return String in the format 
  */
  // =================================================
private LvgOutputStructure createTermFromParts(String pVariantA, String pVariantB) {
  
  LvgOutputStructure returnVal = null;
   
 
    try {
    LvgOutputStructure variantA = GLvgUtils.parseLvgOutput( pVariantA );
    LvgOutputStructure variantB = GLvgUtils.parseLvgOutput( pVariantB );
    
    
    if ( variantA != null && variantB != null ) {
    
     String inputTerm =   variantA.getInputTerm() + " " + variantB.getInputTerm();
     String proposedVariant =   variantA.getVariant() + " " + variantB.getVariant();
     
     String[] validTermz = this.termLookup( proposedVariant );
     
     if ( validTermz != null && validTermz.length > 0 ) {
    
       returnVal = new LvgOutputStructure();
       returnVal.setInputTerm( inputTerm );
       returnVal.setVariant(   proposedVariant );
       returnVal.setPos( variantA.getPos() + " " + variantB.getPos());
       returnVal.setInfl( variantA.getInfl() + " " + variantB.getInfl());
       returnVal.setHistory( variantA.getHistory() + " " + variantB.getHistory());
       int costA = Integer.parseInt(variantA.getCost());
       int costB = Integer.parseInt(variantB.getCost());
       int cost = costA + costB;
       String costz =  String.valueOf( cost );
       returnVal.setCost(  costz );
     }   
    }
  } catch (Exception e ) {
    e.printStackTrace();
    System.err.println("Issue with convrsion " + e.toString());
  }
    return returnVal;
    
  } // end Method createTermFromParts() ------


//=================================================
/**
* ofTwoWordTerms creates variants of to word terms
* that are a b ->  b of a
*              ->  b of the a
*          [    -> b of his a
*              ->  b of her a
*              ->  b of their a ]
* 
* @param pTerm
* @return List<LvgOutputStructure> 
*/
// =================================================
public final List<LvgOutputStructure> ofTwoWordTerms(String pTerm) {


  List<LvgOutputStructure> returnVal = new ArrayList<LvgOutputStructure>(3);
  
  String words[] = U.split(pTerm.toLowerCase(), " " );

  if ( words.length == 2) {

  
    String termA = words[0];
    String termB = words[1];
    
    if ( termA.endsWith( ",") ) return null;  // <--- this should be univerted elsewhere
    
    String ofTerm1 = termB + " of " + termA;
    String ofTerm2 = termB + " of the " + termA;
    String ofTerm3 = termB + " of a " + termA;
    String ofTerm4 = termB + " of his " + termA;
    String ofTerm5 = termB + " of her " + termA;
    
    
    
    returnVal.add (new LvgOutputStructure( pTerm, ofTerm1, "<noun>", "<base>", "Of", "6" ));
    returnVal.add (new LvgOutputStructure( pTerm, ofTerm2, "<noun>", "<base>", "Of", "6" ));
    returnVal.add (new LvgOutputStructure( pTerm, ofTerm3, "<noun>", "<base>", "Of", "6" ));
    returnVal.add (new LvgOutputStructure( pTerm, ofTerm4, "<noun>", "<base>", "Of", "6" ));
    returnVal.add (new LvgOutputStructure( pTerm, ofTerm5, "<noun>", "<base>", "Of", "6" ));
  }
    

 return returnVal;
} // end Method ofTwoWordTerms() -----------------



//=================================================
/**
* invertOfTerms transforms a b of c to c a b
* except if there is a "with" in the statement. That messess up the inversion
* 
* @param pTerm
* @return List<LvgOutputStructure> 
*/
// =================================================
 public final List<LvgOutputStructure>  invertOfTerms(String pTerm) {
 
   List<LvgOutputStructure> returnVal = new ArrayList<LvgOutputStructure>(3);
   
   if ( pTerm != null ) {
     int pivot = pTerm.toLowerCase().indexOf( " of ");
     if ( pivot != -1 ) {
     
        if (!pTerm.toLowerCase().contains(" with ") ) {
       
          String part1 = pTerm.substring(pivot + 4);
          String part2 = pTerm.substring(0, pivot );
          String compactTerm = part1 + " " + part2;
        
       
          returnVal.add (new LvgOutputStructure( pTerm, compactTerm, "<noun>", "<base>", "IOf", "6" ));
        }
        
     }
       
     } // has of in it 
     
     
   
   return returnVal;
} // end Method invertOfTerms() --------------

 

//=================================================
/**
 * filterOutNecAndNos 
 * 
 * @param term
 * @return String
 * @throws Exception 
*/
// =================================================
public final String filterOutNecAndNos(String pTerm) throws Exception {
  
  LvgOutputStructure m = null;
  String returnVal = pTerm;
  try {
    String output = stripNecAndNosCmdApi.MutateToString(pTerm);
    
    String rows[] = U.split(output, "\n");
    for ( String row : rows ) {
      m = GLvgUtils.parseLvgOutput(row );
      if ( m != null) {
        returnVal = m.getVariant();
        break;
      } 
    }
    
    
    
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println("Issue with stripping nec and nos on term " + pTerm + " " + e.toString());
   throw new Exception ();
  }
  return returnVal;
} // end Method filteroutNecAndNos() -------------


// =================================================
/**
 * filterOutParentheticalExpressions strips phrases like
 * (finding) or [Mesh] out
 * 
 * @param pTerm
 * 
 * @return String
 */
// =================================================
public final String filterOutParentheticalExpressions(String pTerm) {

  String returnVal = pTerm;
  returnVal = filterOutParentheticalExpressions(returnVal, '(', ')');
  returnVal = filterOutParentheticalExpressions(returnVal, '[', ']');
  returnVal = filterOutParentheticalExpressions(returnVal, '{', '}');
  returnVal = filterOutParentheticalExpressions(returnVal, '<', '>');

  return returnVal;
} // end Method filterOutParentheticalExpressions() ----

// =================================================
/**
 * filterOutParentheticalExpressions strips phrases like
 * (finding) or [Mesh] out
 * 
 * @param pTerm
 * @param pOpenBracket
 * @param pCloseBracket
 * @return String
 */
// =================================================
public final String filterOutParentheticalExpressions(String pTerm, char pOpenBracket, char pCloseBracket) {

  String returnVal = pTerm;
try {
  String buff = pTerm;
  int firstParen = buff.indexOf(pOpenBracket);
  int lastParen = buff.lastIndexOf(pCloseBracket);

  if (firstParen >= 0 && lastParen <= buff.length()) {

    String beforePart = null;
    String afterPart = null;
    if ( firstParen > 0)
     beforePart = buff.substring(0, firstParen - 1);
    
    if ( lastParen < buff.length())
     afterPart = buff.substring(lastParen + 1);
    
    returnVal = beforePart + " " + afterPart;
    returnVal = returnVal.trim();
  }

} catch (Exception e) {
  e.printStackTrace();
  
}
  return returnVal;
} // end Method filterOutParentheticalExpressions() ----

  // -----------------------------------------
  /**
   * cleanup closes the open files, sockets, database connections that lvg would have opened.
   * 
   */
  // -----------------------------------------
  public final void cleanup() { 
     
         
  } // end Method cleanup() ------------------
  

//=================================================
 /**
  * filterVariants filter single letter output variants (all of these are useless acronyms and abbreviations)
  * And filters those single letter terms that have been made plural or possessive.  s  's
  * 
  * @param pOutputTerms
  * @return String[]  of outputRows
 */
 // =================================================
public final  List<LvgOutputStructure> filterVariants(List<LvgOutputStructure> pOutputTerms) {
  
  List<LvgOutputStructure>  returnVal = null;
  
  
  if ( pOutputTerms  != null  && !pOutputTerms.isEmpty()) {
    returnVal = new ArrayList<LvgOutputStructure>( pOutputTerms.size() );
    
  
    for ( LvgOutputStructure row : pOutputTerms ) {
      if ( row != null ) {
      String variant = row.getVariant();
      if ( variant != null )
        if ( (variant.length() == 2 && variant.endsWith("s")))
          ;
        else if (variant.length() == 3 && variant.endsWith("'s"))
          ;
        else  if ( variant.length() > 1 )
          returnVal.add( row);
      }
    }
  }
  
   return returnVal;
 } // end Method filterVariants() -------------------



 // =================================================
 /**
  * uniqVariants returns a unique'd set of term variants,
  * ideally with the shortest variant path
  * 
  * @param outputRows
  * @return String[] 
 */
 // =================================================
public final  List<LvgOutputStructure> uniqVariants(List<LvgOutputStructure> outputRows) {
   List<LvgOutputStructure> returnVal = null;
   List<String> buff = null;
   
   HashMap<String,LvgOutputStructure> variantHash = new HashMap<String, LvgOutputStructure>( outputRows.size());
   
   if ( outputRows  != null ) {
     buff = new ArrayList<String>( outputRows.size() );
     
   
     for ( LvgOutputStructure row : outputRows ) {
     
       String variant = row.getVariant();
       
       LvgOutputStructure firstRow = variantHash.get( variant); 
       
       if ( firstRow == null )
         variantHash.put( variant, row ); 
     
     }
     
     Set<String> keys = variantHash.keySet();
     returnVal = new ArrayList<LvgOutputStructure>( variantHash.size() );
     for ( String key: keys ) {
       returnVal.add( variantHash.get( key ) ) ; 
     
  
     }
   }
    return returnVal;
 } // end Method UniqVariants() -----------



  // -----------------------------------------
  /**
   * uniq
   * 
   * @param pOutputTerms
   * @return
   */
  // -----------------------------------------
  public final String[] uniq(List<String> pTerms) {
    
    String[] returnVal = null;
    if ( pTerms != null ) {
    HashMap<String, int[]> map = new HashMap<String,int[]>(pTerms.size()*2);
    int [] dummy = new int[1];
    for ( String entry : pTerms )
      map.put(entry, dummy);
    
    returnVal = new String[ map.size()];
    int i = 0;
    for ( String entry: map.keySet() )
      returnVal[i++] = entry;
        
    Arrays.sort(returnVal);
    }
    return returnVal;
    
  } // end Method uniq () --------------------


  // -----------------------------------------
  /**
   * main 
   * Usage: FruitfulSynonyms
   *    <input term on stdin>
   * 
   *   The program takes a cui, and returns
   *   the set of synonyms.
   *   
   * @param args
   */
  // -----------------------------------------
  public static void main(String[] args) {
    
    BufferedReader        in = null;
    String              term = null;
    String[]     outputTerms = null;
    FruitfulVariants fruitfulTermsInstance = null;
    
    // ---------------------------------
    // Set up a log
    // ---------------------------------
 //  Logger logger = new FrameworkLogging();
    
  //  String logFileName = logger.getName();  
  //  System.err.println("The Log is going to " + logFileName);
   
  
    System.err.println("Starting lookup");
    // -----------------------
    // grab the standard input
    try {
      in = new BufferedReader( new InputStreamReader(System.in));
    } catch ( Exception e) { System.err.println("Not able to grab standard input!"); return; } // System.exit(-1); }
  
    // ------------------------
    // Instantiate the lexUtils
    try {
     
      fruitfulTermsInstance = new FruitfulVariants();
      
    
    // take standard input in, run it through
    // the fruitfulTerm method, display the results
  
      System.out.print("Input term: ");
      while ( (term = in.readLine()) != null ) {
        try {
          outputTerms = fruitfulTermsInstance.fruitfulVariants(term );
        } catch (Exception e2) { 
          GLog.println(GLog.ERROR_LEVEL, System.class, "main", 
             "Issue with term: " + term + "\n" + e2.getMessage() );
        }
         if ( outputTerms != null ){
           int ctr = 0;
           for ( String i: outputTerms ) {
             System.out.println( ctr + "|" + i + "<--|");
             ctr++;
           } // end loop through output terms
         }
         
         System.out.print("\n---> Input term: ");
      } // end loop through input terms
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



  // private Logger logger;
  // -----------------------------------------
  // Class Variables
  // -----------------------------------------
  private LvgCmdApi fruitfulVariantsCmdApi  = null; 
  private LvgCmdApi termLookupCmdApi  = null; 
  private LvgCmdApi stripNecAndNosCmdApi = null;
 
  
} // end Class lexUtils() --------------------
