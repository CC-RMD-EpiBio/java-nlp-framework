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
