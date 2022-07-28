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
 * TermLookupLocal is a class that retrieves terms from
 * a local lexicon.  The local lexicon should be found
 * in the resources/localTerminology directory.
 *
 *
 * @author  Guy Divita 
 * @created Feb 21, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils.term;

import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermLookupFactory;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermLookupInterface;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermUtils;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.LexRecord;

import gov.nih.cc.rmd.nlp.framework.utils.U;

public class TermLookupLocal {

  // -----------------------------------------
  /**
   * Constructor This constructor is used for classes that extend this class and
   * do the heavy lifting with overwriting methods.
   * 
   */
  // -----------------------------------------
  public TermLookupLocal() throws Exception {

  } // end Constructor() ----------------------

  // -----------------------------------------
  /**
   * Constructor This constructor is used for loading and indexing the local
   * terminology tables.
   * 
   * @param pLocalTerminologyFile
   * @param pTokenize (An instantiation of a tokenizer)
   * 
   */
  // -----------------------------------------
  public TermLookupLocal(String pLocalTerminologyFile, TermUtils pTokenize) throws Exception {

    String[] localTerminologyFiles = new String[1];
    localTerminologyFiles[0] = pLocalTerminologyFile;
    this.init(localTerminologyFiles, 1000);

  } // end Constructor() ----------------------
  
  

  // -----------------------------------------
  /**
   * Constructor This constructor is used for loading and indexing the local
   * terminology tables.
   * 
   * @param pLocalTerminologyFile
   * @param pAcronymsFile
   * 
   */
  // -----------------------------------------
  public TermLookupLocal(String pLocalTerminologyFile, String pAcronymsFile) throws Exception {

    String[] localTerminologyFiles = new String[1];
    localTerminologyFiles[0] = pLocalTerminologyFile;
    this.init(localTerminologyFiles, 1000);

  } // end Constructor() ----------------------

  // -----------------------------------------
  /**
   * Constructor This constructor is used for loading and indexing the master
   * terminology tables.
   * 
   * @param pLocalTerminologyFile
   * @param pAcronymsFile
   * @param pNoRecords ( a reasonable guess of how many records are being added)
   * 
   */
  // -----------------------------------------
  public TermLookupLocal(String pLocalTerminologyFile, String pAcronymsFile, int pNoRecords)
      throws Exception {

    String[] localTerminologyFiles = new String[1];
    localTerminologyFiles[0] = pLocalTerminologyFile;
    this.init(localTerminologyFiles, pNoRecords);

  } // end Constructor() ----------------------

  // -----------------------------------------
  /**
   * Constructor
   * 
   * @param pLocalTerminologyFiles
   * @param pTokenize
   * @throws Exception
   */
  // -----------------------------------------
  public TermLookupLocal(String[] pLocalTerminologyFiles, TermUtils pTokenize) throws Exception {

    this.init(pLocalTerminologyFiles, 1000);
  } // end Cconstructor() --------------------

  // =======================================================
  /**
   * Constructor TermLookupLocal
   *
   * @param pLocalTerminologyFiles
   * @param pLocalResources
   * @param pTokenize
   * @throws Exception
   */
  // =======================================================
  public TermLookupLocal(String[] pLocalTerminologyFiles, String pLocalResources, TermUtils pTokenize, String pTermRows) throws Exception {

    int termRows = Integer.parseInt(pTermRows);
    this.init(pLocalTerminologyFiles, termRows);
  }

  // =================================================
  /**
   * index
   * 
   * @param pLRAGRTerm
   */
  // =================================================
  public void index(String pLRAGRTerm) {
    
    this.termLookup.addTerm(pLRAGRTerm);
  }

  // -----------------------------------------
  /**
   * initialize is used for loading and indexing the terminology tables.
   * 
   * @param pLocalTerminologyFile
   * @param pAcronymsFile
   * 
   */
  // -----------------------------------------
  void init(String[] pTerminologyFiles, int pNoRecords) throws Exception {

    this.tokenize = new TermUtils();

    this.termLookup = TermLookupFactory.getTermLookup(pTerminologyFiles, pNoRecords);

  } // end Constructor() ----------------------

  // -----------------------------------------
  /**
   * get retrieves a list of terms given a key. Multiple records could be
   * returned for a key based on case and part of speech.
   * 
   * Note: The key is a string composed of the tokens of the term in reverse
   * order. Also, punctuation has been replaced by spaces.
   * 
   * @param pKey
   * @param pNumberOfTokens
   * @return
   * @throws Exception
   */
  // -----------------------------------------
  public List<LexRecord> get(String pKey) throws Exception {
    List<LexRecord> returnValue = null;

    returnValue = this.termLookup.get(pKey);

    return returnValue;
  } // end Method get

  // -----------------------------------------
  /**
   * get retrieves lexical records given the number of tokens to look for, and a
   * key.
   * 
   * Note: The key is a string composed of the tokens of the term in reverse
   * order. Also, punctuation has been replaced by spaces.
   * 
   * 
   * @param pNumberOfTokens
   * @param pKey
   * @return List<LexRecord>
   */
  // -----------------------------------------
  public List<LexRecord> get(int pNumberOfTokens, String pKey) throws Exception {

    List<LexRecord> returnValue = get(pKey, pNumberOfTokens);

    // the returnValue might just contain oprhan terms - look up again to see
    // if there are any non-ophan term candidates lowercased.
    // If the first lookup didn't work, lowercase the key and try again

    if ((returnValue == null) || (returnValue.size() < 1) || onlyOprhanTerm(returnValue)) {
      returnValue = get(pKey.toLowerCase(), pNumberOfTokens);

      if (returnValue == null || returnValue.size() < 1) {
        returnValue = get(stripLeadingPunct(pKey.toLowerCase()), pNumberOfTokens);
      }
    }
    return returnValue;

  } // end Method get() ------------------------

  // =======================================================
  /**
   * onlyOprhanTerm returns true if all the terms are orphans
   * 
   * @param pTerms
   * @return boolean
   */
  // =======================================================
  private boolean onlyOprhanTerm(List<LexRecord> pTerms) {

    boolean returnVal = true;

    if (pTerms != null) {
      for (LexRecord aTerm : pTerms) {
        if (!aTerm.getOrphanTerm()) {
          returnVal = false;
          break;
        }
      }
    }
    return returnVal;
  } // End Method onlyOprhanTerm() ======================

  // =======================================================
  /**
   * stripLeadingPunct This is a method that will have to take each token,
   * examine the leading and trailing punctuation, and strip leading and
   * trailing punctuation from each token
   * 
   * @param lowerCase
   * @param pNumberOfTokens
   * @return
   */
  // =======================================================
  public String stripLeadingPunct(String pKey) {

    String returnVal = pKey;
    String buff = pKey;
    StringBuffer newBuff = new StringBuffer();
    StringBuffer token = new StringBuffer();

    if (buff.length() > 2) {
      char[] cbuff = buff.toCharArray();

      for (int i = 0; i < cbuff.length; i++) {
        char c = cbuff[i];

        if (U.isWhiteSpace(c)) {
          if (token != null && token.length() > 0) {
            String stoken = stripLeadingPunctAux(token.toString());

            newBuff.append(stoken);
            token = new StringBuffer();
          }
          newBuff.append(cbuff[i]);
        } else {
          token.append(cbuff[i]);
        }

      }
      if (token != null && token.length() > 0) {

        String stoken = stripLeadingPunctAux(token.toString());
        newBuff.append(stoken);
      }

      if (newBuff != null)
        returnVal = newBuff.toString().trim();

    }

    return returnVal;
  } // end Method stripLeadingPunct() ------------------------

  // =======================================================
  /**
   * stripLeadingPunctAux This is a method that will have to take each token,
   * examine the leading and trailing punctuation, and strip leading and
   * trailing punctuation from each token
   * 
   * @param lowerCase
   * @param pNumberOfTokens
   * @return
   */
  // =======================================================
  String stripLeadingPunctAux(String pKey) {
    String buff = pKey;
    String newbuff = pKey;
    if (buff.length() > 2) {

      char[] cbuff = buff.toCharArray();
      char firstChar = cbuff[0];
      int lastCharIdx = cbuff.length - 1;
      char lastChar = cbuff[lastCharIdx];

      if (U.isPunctuation(firstChar))
        if (U.isPunctuation(lastChar))
          newbuff = buff.substring(1, cbuff.length - 1);
        else
          newbuff = buff.substring(1);
      else if (U.isPunctuation(lastChar))
        newbuff = buff.substring(0, lastCharIdx - 1);
    }
    return newbuff;
    // End Method stripLeadingPunct() ======================
  }

  // -----------------------------------------
  /**
   * get retrieves a list of terms given a key. Multiple records could be
   * returned for a key based on case and part of speech.
   * 
   * @param pKey
   * @param pNumberOfTokens
   * @return
   */
  // -----------------------------------------
  protected List<LexRecord> get(String pKey, int pNumberOfTokens) {
    List<LexRecord> returnValue = null;

    returnValue = this.termLookup.get(pKey, pNumberOfTokens);

    return returnValue;
  } // end Method lookup

  // -------------------------------------------------------
  /**
   * lookupTerm puts together the tokens and looks them up
   * 
   * @param tokens
   * @return LexRecord
   * @throws Exception
   */
  // -------------------------------------------------------
  List<LexRecord> lookupTerm(String[] tokens) throws Exception {

    List<LexRecord> lexRecords = null;
    StringBuffer buff = new StringBuffer();
    if (tokens != null) {
      for (int i = 0; i < tokens.length; i++)
        buff.append(tokens[i] + " ");
      String key = buff.toString().trim();

      lexRecords = this.get(tokens.length, key);
    }
    // if ( lexRecords != null)
    // System.err.println(" found a match");

    return lexRecords;
  } // end Method lookupTerm() -----------------------

  // -----------------------------------------
  /**
   * normalize will create a key out of the term The key is in reverse order,
   * lowercased, and punctuation is replaced by spaces.
   * 
   * @param pTerm
   * @return
   */
  // -----------------------------------------
  public String normalize(String pTerm) throws Exception {

    StringBuffer key = new StringBuffer();

    List<String> tokens = tokenize.tokenize(pTerm);

    for (int tokenCtr = tokens.size() - 1; tokenCtr >= 0; tokenCtr--) {

      if (!U.isOnlyPunctuation(tokens.get(tokenCtr))) {
        String t = tokens.get(tokenCtr);
        key.append(t);
        if (tokenCtr > 0)
          key.append(" ");
      } else {
        // key.append(" ");
      }
    }

    return key.toString().trim();
  } // end Method normalize() ----------------------

  // -----------------------------------------
  /**
   * countTokens returns how many tokens there are in this string
   * 
   * @param pKey
   * @return
   */
  // -----------------------------------------
  protected int countTokens(String pKey) {
    int numberOfTokens = 0;
    if (pKey != null) {
      List<String> tokens;
      try {
        tokens = this.tokenize.tokenize(pKey);
        numberOfTokens = tokens.size();
      } catch (Exception e) {
        e.printStackTrace();
        String msg = "Issue with counting tokens of a term " + e.getMessage();
        System.err.println(msg);
      }
    }
    return numberOfTokens;

  } // end Method countTokens() -----------

  // LRAGR columns
  public final static int EUI = 0;

  public final static int TERM = 1;

  public final static int CATEGORY = 2;

  public final static int INFLECTION = 3;

  public final static int UNINFLECTION = 4;

  public final static int CITATION_FORM = 5;

  private TermLookupInterface termLookup = null;

  protected TermUtils tokenize = null;

} // end Class LexicalLookup() ---------------
