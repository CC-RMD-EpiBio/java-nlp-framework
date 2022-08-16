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
 * FruitfulSynonyms given a cui, will retrieve the 
 * sui level strings (synonyms).  It will return the
 * (noun) inflectional variants of each of these
 * synonyms.  It will retrieve the derivational
 * variants of each synonym. 
 * 
 *
 *
 * @author  Guy Divita 
 * @created September 11, 2011
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



public class FruitfulSynonyms {
  
  // -----------------------------------------
  /**
   * Constructor
   * @throws Exception 
   *
   */
  // -----------------------------------------
  public FruitfulSynonyms() throws Exception {
    
    // Open up a database connection or load in this data
    // from a hash
   
    try {
      this.cuiTermStore = new CuiTermStore();
    } catch (Exception e) {
      System.err.println("Issue with opening up the cuiTermStore - this may be ok if you are not trying to get synonyms from the umls");
    }
  
    this.inflectionLvgCmdApi = new LvgCmdApi("-f:ici~128+16777215 -CR:o -F:2");
    this.derivationLvgCmdApi = new LvgCmdApi("-f:d -CR:o -F:2");   
    this.inflection2LvgCmdApi = new LvgCmdApi("-f:ici~128+16777215 -SC -SI -CR:o");
    this.vegetableLvgCmdApi = new LvgCmdApi("-f:ici~128+16777215:Ln -f:s:Ln -f:A:Ln -f:a:Ln -SC -SI -CR:o");
    
    
    
   
    
  } // end Constructor() ---------------------

  
//-----------------------------------------
  /**
   * fruitfulSynonyms  Given a term, will retrieve the 
 * sui level strings (synonyms).  It will return the
 * (noun) inflectional variants of each of these
 * synonyms. It will return the derivations of the synonyms
 * of each.
   * 
   * @param pCui
   * @return List<String>
   * @throws Exception 
   */
  // -----------------------------------------
  public  String[] fruitfulSynonymsFromTerm(String pTerm) throws Exception {
    
    String[] finalVariants = null;
    String[] cuis = this.cuiTermStore.getCuis( pTerm );
    ArrayList<String> allVariants = new ArrayList<String>();
    
    if ( cuis != null ) {
      for ( String cui : cuis ) {
        String variants[] = fruitfulSynonyms( cui);
        if ( variants != null ) {
          for ( String variant : variants )
            allVariants.add( variant);
        }       
      } // end loop through cuis
      
      finalVariants = uniq( allVariants);
    } // end if there were any cuis
    
    return finalVariants;
  } // end Method getFruitfulSynonymsFromTerm()----
  
  // -----------------------------------------
  /**
   * fruitfulSynonyms  Given a cui, will retrieve the 
 * sui level strings (synonyms).  It will return the
 * (noun) inflectional variants of each of these
 * synonyms. It will return the derivations of the synonyms
 * of each.
   * 
   * @param pCui
   * @return List<String>
   * @throws Exception 
   */
  // -----------------------------------------
  public  String[] fruitfulSynonyms(String pCui) throws Exception {
    
    String[] finalVariants = null;
    String[] synonyms = null;
   

    // Retrieve the strings for this cui
    synonyms = this.cuiTermStore.get(pCui);
    
   
    finalVariants = generateVegetableVariants( synonyms );
    
   
  
    return finalVariants;
  } // end Method fruitfulSynonyms() ------------

   // ==========================================
  /**
   * generateVegetableVariants returns inflections for nouns, acronyms, 
   * acronym expansions, spelling variants,  
   *
   * @param pTerms
   * @return String[]  [TBD format of output here, please]
   * @throws Exception 
   */
  // ==========================================
  public String[] generateVegetableVariants(String[] pTerms) throws Exception  {
    
    
    String[] finalVariants = null;
    
    ArrayList<String> allVariantz = new ArrayList<String>();
    try {
      if (pTerms != null) {
        for (int i = 0; i < pTerms.length; i++) {
          
          generateVegitable( pTerms[i] , allVariantz );
   
        } // end loop through each output term
      } // end if there are any output terms

       
      // -----------------------------------
      // Uniq them 
      // -----------------------------------
      finalVariants = uniq( allVariantz);      

    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("LvgAPI issue: " + e.toString());
    }
   
    return finalVariants;
  } // end Method generateInflectionsAndDerivations() ===
  


  // ==========================================
  /**
   * generateInflectionsAndDerivations [Summary]
   *
   * @param pTerm
   * @param allVariants
   * @param allDerivations
   * @throws Exception 
   */
  // ==========================================
  public void generateVegitable(String pTerm,ArrayList<String> allVariants   )  throws Exception  {
  
    
      // ------------------------------------
      // Retrieve inflections of the synonyms
      // ------------------------------------
      String variantz = this.vegetableLvgCmdApi.MutateToString( pTerm );
      if ( variantz != null && variantz.trim().length() > 0 ) {
    
        String[] variants = variantz.split("\n");
        for (int z = 0; z < variants.length; z++) {
          allVariants.add(variants[z].trim());
        }
      }
   
   
  } // end Method generateInflectionsAndDerivations() ==
  

 

    
  // -----------------------------------------
  /**
   * cleanup closes the open files, sockets, database connections that lvg would have opened.
   * 
   */
  // -----------------------------------------
  public void cleanup() { 
      this.cuiTermStore.cleanup();
         
  } // end Method cleanup() ------------------

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
    String               cui = null;
    String[]     outputTerms = null;
    FruitfulSynonyms fruitfulTermsInstance = null;
  
    System.err.println("Starting lookup");
    // -----------------------
    // grab the standard input
    try {
      in = new BufferedReader( new InputStreamReader(System.in));
    } catch ( Exception e) { System.err.println("Not able to grab standard input!"); return;}// System.exit(-1); }
  
    // ------------------------
    // Instantiate the lexUtils
    try {
     fruitfulTermsInstance = new FruitfulSynonyms();
    
    // take standard input in, run it through
    // the fruitfulTerm method, display the results
  
      System.err.print("Input cui: ");
      while ( (cui = in.readLine()) != null ) {
        try {
          outputTerms = fruitfulTermsInstance.fruitfulSynonymsFromTerm(cui );
        } catch (Exception e2) { e2.printStackTrace(); System.err.println("Issue with term: " + cui); }
         if ( outputTerms != null ){
           int ctr = 0;
           for ( String i: outputTerms ) {
             System.out.println( ctr + "|" + i);
             ctr++;
           } // end loop through output terms
         }
         
         System.err.print("\n---> Input term: ");
      } // end loop through input terms
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Not able to read from standard input");
      System.exit(-1);
    } // end loop through the standard input;
    
    // Clean up
    if (fruitfulTermsInstance != null  )
      fruitfulTermsInstance.cleanup();
    try {
      in.close();
    }
    catch (IOException e) {e.printStackTrace(); }
  
    
  } // end Method Main() ---------------------


  // -----------------------------------------
  // Class Variables
  // -----------------------------------------
  private LvgCmdApi inflectionLvgCmdApi  = null; 
  private LvgCmdApi inflection2LvgCmdApi  = null; 
  private LvgCmdApi vegetableLvgCmdApi = null;
  private LvgCmdApi derivationLvgCmdApi  = null; 
  private CuiTermStore      cuiTermStore = null;
  
} // end Class lexUtils() --------------------
