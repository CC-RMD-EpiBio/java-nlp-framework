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
