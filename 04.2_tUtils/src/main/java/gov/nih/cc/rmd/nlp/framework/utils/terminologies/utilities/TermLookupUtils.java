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
 * TermLookupUtils holds the pointer to the currently used
 * tokenizer, the currently used normalizer ....
 *
 * @author     Guy Divita
 * @created    May 18, 2018
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities;

import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.U;

public final class TermLookupUtils {

  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws Exception 
   * 
  **/
  // =================================================
  public TermLookupUtils() throws Exception {
    
    this.tokenize = new TermUtils();
  }

  

  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws Exception 
   * 
  **/
  // =================================================
  public TermLookupUtils(String[] pArgs) throws Exception {
    
    String knownAcronymsPath = U.getOption( pArgs,  "--knownAcronyms=", "resources/com/ciitizen/framework/tokenizer/knownAcronyms.txt");
    this.tokenize = new TermUtils( knownAcronymsPath );
    
  }


  // -------------------------------------------------------
  /**
   * createLexRecord creates a lexRecord and computes the key and number of
   * tokens for this term.
   * 
   * @param pRow
   * @return LexRecord
   * @throws Exception
   */
  // -------------------------------------------------------
  public LexRecord createLexRecord(String pRow) throws Exception {
    LexRecord tmpRecord = null;
    try {
      tmpRecord = LexRecord.parseLRAGR(pRow);
      String key = normalize(tmpRecord.getTerm());
      int numberOfTokens = countTokens(key);

      tmpRecord.setKey(key);
      tmpRecord.setNumberOfTokens(numberOfTokens);

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue creating a record from the row " + pRow + "\n" + e.toString();
      System.err.println(msg);
      throw new Exception (msg);
    }
    return tmpRecord;
  } // end Method indexx () ---------------
  
//-----------------------------------------
 /**
  * normalize will create a key out of the term The key is in reverse order,
  * lowercased, and punctuation is replaced by spaces.
  * 
  * @param pTerm
  * @return
  */
 // -----------------------------------------
 private String normalize(String pTerm) throws Exception {
   StringBuffer key = new StringBuffer();

   List<String> tokens = tokenize.tokenize(pTerm);

   for (int tokenCtr = tokens.size() - 1; tokenCtr >= 0; tokenCtr--) {

     if (!U.isOnlyPunctuation(tokens.get(tokenCtr))) {
       String t = tokens.get(tokenCtr);
       key.append(t);
       if (tokenCtr > -1)
         key.append(" ");
     } else {
       // key.append(" ");
     }
   }

   return key.toString().trim().toLowerCase();
 } // end Method normalize() ----------------------
 
 
  // =================================================
  /**
   * tokenize 
   * 
   * @param pKey
   * @return List<String>
   * @throws Exception 
  */
  // =================================================
  public List<String> tokenize(String pKey) throws Exception {
   return this.tokenize.tokenize( pKey );
  } // end Method tokenize() -------------------------

//-----------------------------------------
 /**
  * countTokens returns how many tokens there are in this string
  * 
  * @param pKey
  * @return
  */
 // -----------------------------------------
 private int countTokens(String pKey) {
   int numberOfTokens = 0;
   if (pKey != null) {
     List<String> tokens;
     try {
       tokens = this.tokenize.tokenize(pKey);
       numberOfTokens = tokens.size();
     } catch (Exception e) {
         e.printStackTrace();
        String msg = "Issue with counting tokens of a term " + e.getMessage() ;
        System.err.println( msg );
     }
   }
   return numberOfTokens;

 } // end Method countTokens() -----------

  
  // -------------------------
  // Global Variables
  // -------------------------
  private TermUtils tokenize = null;

} // end Class TermLookupUtils () -----------------
