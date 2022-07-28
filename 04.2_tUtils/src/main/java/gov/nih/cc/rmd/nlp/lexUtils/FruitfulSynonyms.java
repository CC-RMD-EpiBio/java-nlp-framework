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
