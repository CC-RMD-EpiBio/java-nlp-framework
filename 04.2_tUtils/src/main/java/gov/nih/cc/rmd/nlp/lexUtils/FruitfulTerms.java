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
 * FruitfulTerms is a method to generate all kinds of
 * variants from a term.  
 * 
 * LVG has a fruitful variants method (-f:G) that
 * produces fruitful variants (acronyms, expansions, 
 * derivations, synonyms, inflections, spelling variants
 * and the like, but it only does it for single tokens.
 * This class contains a method that generates the
 * variants for each word, then permutes the variants
 * for each token.  Since the intension is to discover
 * new terminology, the output is NOT filtered to see
 * if the new combination is an existing term.  It is
 * intended that this will be wildly over-generative, but
 * is to be constrained by seeing of any of the newly
 * created variants are sequences of tokens in an existing
 * corpus.
 *
 * @author  Guy Divita 
 * @created Apr 21, 2011
 *
 * [Utah/Salt Lake City Preamble Here]
 */
// ================================================
package gov.nih.cc.rmd.nlp.lexUtils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import gov.nih.nlm.nls.lvg.Api.LvgCmdApi;


public class FruitfulTerms {
  
  // -----------------------------------------
  /**
   * Constructor
   * @throws Exception 
   *
   */
  // -----------------------------------------
  public FruitfulTerms() throws Exception {
    
    this.mLvgCmdApi = new LvgCmdApi("-f:G -m -F:2");
    //this.permute = new Permute();
   
  } // end Constructor() ---------------------

  // -----------------------------------------
  /**
   * main 
   * Usage: LexUtils
   *    <input term on stdin>
   * 
   *   The program takes a term, creates variants
   *   for each token of the term, and permutes them
   *   to come up with new term candidates.
   *   
   * @param args
   */
  // -----------------------------------------
  public static void main(String[] args) {
    
    BufferedReader        in = null;
    String         inputTerm = null;
    String[]     outputTerms = null;
    FruitfulTerms fruitfulTermsInstance = null;
   
    // -----------------------
    // grab the standard input
    try {
      in = new BufferedReader( new InputStreamReader(System.in));
    } catch ( Exception e) { System.err.println("Not able to grab standard input!"); return; } // System.exit(-1); }

    // ------------------------
    // Instantiate the lexUtils
    try {
     fruitfulTermsInstance = new FruitfulTerms();
    
    // take standard input in, run it through
    // the fruitfulTerm method, display the results
  
      System.err.print("Input term: ");
      while ( (inputTerm = in.readLine()) != null ) {
        try {
          outputTerms = fruitfulTermsInstance.fruitfulTerms(inputTerm );
        } catch (Exception e2) { e2.printStackTrace(); System.err.println("Issue with term: " + inputTerm); }
         if ( outputTerms != null ){
           int ctr = 0;
           for ( String i: outputTerms ) {
             System.out.println( ctr + "|" + i);
             ctr++;
           } // end loop through output terms
         }
         System.err.print("\nInput term: ");
      } // end loop through input terms
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Not able to read from standard input");
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

  // -----------------------------------------
  /**
   * cleanup closes the open files, sockets, database connections that lvg would have opened.
   * 
   */
  // -----------------------------------------
  public void cleanup() {
    
    
  } // end Method cleanup() ------------------

  // -----------------------------------------
  /**
   * fruitfulTerms is a method to generate all kinds of
 * variants from a term.  
 * 
 * LVG has a fruitful variants method (-f:G) that
 * produces fruitful variants (acronyms, expansions, 
 * derivations, synonyms, inflections, spelling variants
 * and the like, but it only does it for single tokens.
 * This class contains a method that generates the
 * variants for each word, then permutes the variants
 * for each token.  Since the intension is to discover
 * new terminology, the output is NOT filtered to see
 * if the new combination is an existing term.  It is
 * intended that this will be wildly over-generative, but
 * is to be constrained by seeing of any of the newly
 * created variants are sequences of tokens in an existing
 * corpus.
   * @param pInputTerm
   * @return List<String>
   * @throws Exception 
   */
  // -----------------------------------------

  private String[]  fruitfulTerms(String pInputTerm) throws Exception {
    List<String> outputTerms = null;
    
    // ------------------------------------
    // UMLS tokenize the input. Replace punct with spaces, break on spaces
    String tokens[] = pInputTerm.split("\\W");
    @SuppressWarnings("unchecked")
    ArrayList<String>[] cols = new ArrayList[tokens.length];
    
    for ( int i = 0; i < tokens.length; i++ ) {
      cols[i] = new ArrayList<String>();
      try {
        
        String buff = this.mLvgCmdApi.MutateToString(tokens[i]);
        String[] variants = buff.split("\n");
        for ( int z = 0; z < variants.length; z++ ) {
          cols[i].add( variants[z].trim());
        }
      } catch (Exception e) {
        e.printStackTrace();
        throw new Exception( "LvgAPI issue: " + e.toString() );
      }
   
    } // end loop through tokens
    
    // permute the output
    outputTerms = Permute.permute( cols );
    
    // remove duplicates
    String[] uniqueOutputTerms = uniq( outputTerms);
        
    return uniqueOutputTerms;
  } // end Method fruitfulTerms() ------------
  
  // -----------------------------------------
  /**
   * uniq
   * 
   * @param pOutputTerms
   * @return
   */
  // -----------------------------------------
  private String[] uniq(List<String> pTerms) {
    
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
  // Class Variables
  // -----------------------------------------
  LvgCmdApi mLvgCmdApi  = null; 
  
} // end Class lexUtils() --------------------
