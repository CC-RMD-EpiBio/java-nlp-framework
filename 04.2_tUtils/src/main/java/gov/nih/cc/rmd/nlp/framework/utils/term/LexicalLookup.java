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
 * LexicalLookup will return a list of terms given a list of (string) tokens.
 * This class includes both a lookup mechanism, and a pass through get() method
 * to the underlying indexes to retrieve terms given a key.
 * 
 * This class includes lookups in both the master database, and the local
 * lists.
 * 
 * The underlying indexes are created from the LoadLexicalLookup class (for
 * the master db), and the LexicalLookupLocal() class (for the local terminology).
 *
 *
 * @author  Guy Divita 
 * @created Feb 21, 2011
 * @revised May 8, 2011 (to include local terminology)
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils.term;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermUtils;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.LexRecord;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;

public class LexicalLookup {

  private PorterStemmer porterStemmer;
  private final String  acronymsFile = "resources/vinciNLPFramework/tokenizer/knownAcronyms.txt";

  // -----------------------------------------
  /**
   * Constructor: LexicalLookup
   *
   * @param jdbcConnectionString
   * @param jdbcDriver
   * @param localTerminologyFiles
   * @param knownAcronymsFile
   * 
   */
  // -----------------------------------------
  public LexicalLookup(String[] pLocalTerminologyFiles, String pKnownAcronymsFile)  throws Exception {

    this.tokenize = new TermUtils(pKnownAcronymsFile);

    if (pLocalTerminologyFiles != null)
      this.termLookupLocal = new TermLookupLocal(pLocalTerminologyFiles, this.tokenize);
  } // end Constructor()---------------------------

  // -----------------------------------------
  /**
   * Constructor
   * @throws Exception
   * 
   */
  // -----------------------------------------
  public LexicalLookup() throws Exception {

    
   
    this.tokenize = new TermUtils(acronymsFile);
    
    String[] localTerminologyFiles = {"resources/vinciNLPFramework/term/2020AA/SPECIALIST_00.LRAGR",
                                      "resources/vinciNLPFramework/term/2020AA/SPECIALIST_01.LRAGR"};

    this.termLookupLocal = new TermLookupLocal(localTerminologyFiles, this.tokenize);
   
  } // End Constructor() -----------------------------
 

  // =======================================================
  /**
   * Constructor LexicalLookup
   *
   * @param localLexiconz
   * @throws Exception
   * @deprecated - need to update default path to specialist files
   */
  // =======================================================
  public LexicalLookup(String[] localLexiconz) throws Exception {

    
    this.tokenize = new TermUtils(acronymsFile);

    try {
      if (localLexiconz != null)
        this.termLookupLocal = new TermLookupLocal(localLexiconz, this.tokenize);

      else {
        String[] localTerminologyFiles = {"resources/vinciNLPFramework/term/2020AA/SPECIALIST_00.LRAGR",
                                          "resources/vinciNLPFramework/term/2020AA/SPECIALIST_01.LRAGR"};
        
        this.termLookupLocal = new TermLookupLocal(localTerminologyFiles, this.tokenize);
      }

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue reading in the local lexicons " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, msg);
      throw new Exception(msg);
    }
  }

  // =======================================================
  /**
   * Constructor LexicalLookup
   *
   * @param localTerminologyFiles
   * @param knownAcronymsFile
   * @param pLocalResources
   * @param pTermRows - the number of rows in the terminology files
   * @throws Exception
   */
  // =======================================================
  public LexicalLookup(String[] pLocalTerminologyFiles, String pKnownAcronymsFile, String pLocalResources, String pTermRows) throws Exception {

    this.tokenize = new TermUtils(pKnownAcronymsFile);

    this.porterStemmer = new PorterStemmer();

    if (pLocalTerminologyFiles != null)
      this.termLookupLocal =
          new TermLookupLocal(pLocalTerminologyFiles, pLocalResources, this.tokenize, pTermRows);
  } // end constructor LexicalLookup()

  

  // ----------------------------------------------
  /**
   * lookup retrieves an array terms for a given sentence string.
   * 
   * @param pSentenceString
   * @return String[] of term
   * @throws Exception
   * 
   */
  // ----------------------------------------------
  public String[] lookupSimple(String pSentence) throws Exception {

    String[] returnVal = null;
    ArrayList<String> terms = new ArrayList<String>();
    List<List<LexRecord>> termLists = this.lookup(pSentence);

    if (termLists != null) {
      for (List<LexRecord> termList : termLists) {
        if (termList != null && !termList.isEmpty()) {
          LexRecord lexRecord = termList.get(0);
          String term = lexRecord.getTerm();
          terms.add(term);

        }
      }
    } else {
      terms.add(pSentence);
    }
    if (terms != null && !terms.isEmpty())
      returnVal = terms.toArray(new String[terms.size()]);

    return returnVal;
  } // end Method lookupSimple() ---------------------

  // ==========================================
  /**
   * lookupUninflected returns the uninflected terms from this query
   * 
   * Note: this might produce duplicates if there are duplicates in the sentence
   *
   * @param pQuery
   * @return String[]
   */
  // ==========================================
  public String[] lookupUninflected(String pSentence) throws Exception {

    String[] returnVal = null;
    ArrayList<String> terms = new ArrayList<String>();
    List<List<LexRecord>> termLists = this.lookup(pSentence);

    if (termLists != null) {
      for (List<LexRecord> termList : termLists) {
        if (termList != null && !termList.isEmpty()) {
          LexRecord lexRecord = termList.get(0);

          String uninflectedTerm = lexRecord.getCitation();
          String term = lexRecord.getTerm();

          if (uninflectedTerm != null)
            term = uninflectedTerm;
          else
            term = this.porterStemmer.stem(term);

          terms.add(term);

        }
      }
    } else {
      terms.add(pSentence);
    }

    if (terms != null && !terms.isEmpty())
      returnVal = terms.toArray(new String[terms.size()]);

    return returnVal;

  } // end Method lookupUninflected() ========================================

  // ----------------------------------------------
  /**
   * lookup retrieves terms for a given sentence string. This api returns a list
   * of a list of LexRecords. Each (inner) LexRecord List corresponds to a
   * spelling of a term.
   * 
   * There may be multiple lexRecords for a given term, if that term has
   * multiple grammatical senses to it, say a noun and a verb.
   * 
   * @param pSentenceString
   * @return List<List<LexRecord>> (of Terms)
   * @throws Exception
   * 
   */
  // ----------------------------------------------
  public List<List<LexRecord>> lookup(String pSentence) throws Exception {

    List<String> sentenceTokens = this.tokenize.tokenize(pSentence);
    List<List<LexRecord>> termLists = this.lookup(sentenceTokens);

    return termLists;
  } // end Method lookup() ---------------------

  // -----------------------------------------
  /**
   * lookup retrieves the terms from a sentence
   * 
   * @param pSentenceTokens
   * @return List<List<LexRecord>> There will be a list of <LexRecord> for each
   *         term but in most cases, it will only have one LexRecord in it.
   * @throws Exception
   */
  // -----------------------------------------
  public List<List<LexRecord>> lookup(List<String> pSentenceTokens) throws Exception {
    ArrayList<List<LexRecord>> returnValue = new ArrayList<List<LexRecord>>();

    boolean found = false;
    // Loop through the tokens of the sentence backwards
    for (int iCtr = pSentenceTokens.size() - 1; iCtr >= 0; iCtr--) {
      String lastWord = pSentenceTokens.get(iCtr);

      String key = normalize(lastWord);
      found = false;
      List<LexRecord> lexItems = this.getAux(1, key);
      if (lexItems != null) {

        LexRecord aRecord = lexItems.get(0);
        int lookAhead = aRecord.getLongestTermWithThisEnding();

        List<String> lookAheadKeys = generateLookAheadKeys(pSentenceTokens, iCtr, lookAhead);
        if ((lookAheadKeys == null) || (lookAheadKeys.size() < 1)) {

          found = true;
          returnValue.add(lexItems);

        } else {
          for (int k = 0; k < lookAheadKeys.size(); k++) {
            int numberOfTokens = countTokens(lookAheadKeys.get(k));

            List<LexRecord> terms = getAux(numberOfTokens, lookAheadKeys.get(k));

            // If a term was found, move the token in focus back
            if (terms != null) {

              returnValue.add(terms);
              iCtr = (iCtr - numberOfTokens) + 1;
              found = true;
              break;

            } // end if lexical elements were found

          } // end loop through potential terms
        } // end if there are look ahead keys

      }
      // --------------------------------
      // Create a single token non-lexical record for this
      // non-found token
      // --------------------------------
      if (!found) {
        ArrayList<LexRecord> one = new ArrayList<LexRecord>(1);
        one.add(new LexRecord(lastWord));
        returnValue.add(one);
      }

    } // end loop through the tokens of the sentence

    // ------------------------------------------
    // returnVale is in reverse order, reverse it
    // ------------------------------------------
    Collections.reverse(returnValue);

    return returnValue;
  } // end Method lookup() -------------------

  // -----------------------------------------
  /**
   * get retrieves lexical records given the number of tokens to look for, and a
   * key
   * 
   * @param pNumberOfTokens
   * @param pKey
   * @return List<LexRecord>
   */
  // -----------------------------------------
  public List<LexRecord> get(int pNumberOfTokens, String pKey) throws Exception {

    List<LexRecord> returnValue = getAux(pNumberOfTokens, pKey);

    // If the first lookup didn't work, lowercase the key and try again
    if ((returnValue == null) || (returnValue.size() < 1)) {
      returnValue = getAux(pNumberOfTokens, pKey.toLowerCase());
    }
    return returnValue;

  } // end Method get() ------------------------

  // -----------------------------------------
  /**
   * getAcronymExpansionsDB will retrieve expansions given an eui.
   * 
   * @param pAcronymEui
   * @return List<String>
   */
  // -----------------------------------------
  public List<String> getAcronymExpansionsDB(String pAcronymEui) throws Exception {

    List<String> returnValue = null;

    return returnValue;
  } // end Method getAcronymExpansions() -----

  // -----------------------------------------
  /**
   * getAcronymExpansionEUIS will retrieve expansions given an eui.
   * 
   * @param pEui
   * @return List<String> of euis
   */
  // -----------------------------------------
  public List<String> getAcronymExpansionEuisDB(String pEui) throws Exception {

    ArrayList<String> returnValue = null;
    /*
     * // --------------------------------------------------------- // Set the
     * prepared statement if it has not already been set //
     * --------------------------------------------------------- if
     * (this.getAcronymExpansionEuisPreparedStatement == null) {
     * 
     * String query = "SELECT expansionEUI FROM acronyms WHERE acronymEui= ?";
     * Connection connection = this.db.getConnection();
     * this.getAcronymExpansionEuisPreparedStatement =
     * connection.prepareStatement(query); }
     * 
     * this.getAcronymExpansionEuisPreparedStatement.setString(1, pEui);
     * 
     * ResultSet results =
     * this.getAcronymExpansionEuisPreparedStatement.executeQuery();
     * 
     * if (results != null) { returnValue = new ArrayList<String>();
     * 
     * while (results.next()) { returnValue.add(results.getString(1)); } // end
     * loop through result set } // end if there is a result set
     * 
     * if (returnValue.size() < 1) returnValue = null;
     */
    return returnValue;
  } // end Method getAcronymExpansionEUIs() -----

  // -----------------------------------------
  /**
   * getAcronymOf returns the set of acronyms for this eui
   * 
   * @param pEui
   * @return
   * @throws SQLException
   */
  // -----------------------------------------
  public List<String> getAcronymOfDB(String pEui) throws SQLException {

    ArrayList<String> returnValue = null;

    /*
     * ArrayList<String> acronymEuis = null; //
     * --------------------------------------------------------- // Set the
     * prepared statement if it has not already been set //
     * --------------------------------------------------------- if
     * (this.getAcronymsOfPreparedStatement == null) { // INSERT INTO acronyms (
     * acronymEui, expansionEUI, expansion String query =
     * "SELECT acronym,acronymEUI FROM acronymExpansions WHERE expansionEui= ?";
     * Connection connection = this.db.getConnection();
     * this.getAcronymsOfPreparedStatement = connection.prepareStatement(query);
     * }
     * 
     * this.getAcronymsOfPreparedStatement.setString(1, pEui);
     * 
     * ResultSet results = this.getAcronymsOfPreparedStatement.executeQuery();
     * 
     * if (results != null) { returnValue = new ArrayList<String>(); acronymEuis
     * = new ArrayList<String>();
     * 
     * while (results.next()) { returnValue.add(results.getString(1));
     * acronymEuis.add(results.getString(2)); } // end loop through result set }
     * // end if there is a result set
     * 
     * if (returnValue.size() < 1) returnValue = null;
     */
    return returnValue;
  } // end Method getAcronymOf() -------------

  // =================================================
  /**
   * unique 
   * 
   * @param termLists
   * @return List<List<LexRecord>>
  */
  // =================================================
   public final LexRecord[] unique(List<List<LexRecord>> pTermLists) {
     LexRecord[] returnVal = null;
    
    HashMap<String,LexRecord> buff = new HashMap<String,LexRecord>( pTermLists.size());
    if ( pTermLists != null ) {
      for (List<LexRecord> termList : pTermLists ) {
        if ( termList != null ) {
          for ( LexRecord term : termList ) {
            String key = term.getCitation() + "|" + term.getEuis() + "|" + term.getSABs() + "|" + term.getSemanticType() ;
            LexRecord lexRec = buff.get( key);
            if ( lexRec == null )
              buff.put( key,  term );
          }
        }
      }
      
      Collection<LexRecord> returnVal2;
      if ( buff != null && !buff.isEmpty()) {
        returnVal2 = buff.values();
        returnVal = returnVal2.toArray(new LexRecord[returnVal2.size()]);
      }
    }
    
    return returnVal ;
  } // end Method unque() ---------------------------

  // -----------------------------------------
  /**
   * finalize cleans everything up
   */
  // -----------------------------------------
  public void finalize() throws Throwable, Exception {

    super.finalize();

  } // end Method finalize() -----------------

  // -----------------------------------------
  /**
   * main
   * 
   * Usage: LexicalLookup [--inputFileName] [--outputFileName]
   * 
   * LexicalLookup returns a set of terms given a document. Matches are found
   * from three places: the SPECIALIST Lexicon, local terminology, and shape
   * patterns. This program assumes each sentence is bounded by a new line. It
   * does not yet have a sentence tokenizer built in.
   * 
   * Options: --inputFileName=/path/to/inputFileName
   * --outputFileName=/path/to/outputFileName
   * 
   * 
   * The local terminology is not pre-indexed, but read into memory upon
   * startup. This allows for quick integration of new terminology. The local
   * terminology file is read from the
   * classes/resources/localTerminology/localTerminology.LRAGR file. For more
   * information about the local terminology see [ Local Terminology ]
   * 
   * 
   */
  // -----------------------------------------
  public static void main(String[] args) {

    try {

      Object params[] = new Object[2];

      String sentence = null;
      // -------------------------------------------------
      // Retrieve options
      // -------------------------------------------------
      getOptions(args, params);
      BufferedReader in = (BufferedReader) params[0];
      PrintWriter out = (PrintWriter) params[1];
      // -------------------------------------------------
      // Initialize the resources
      // -------------------------------------------------

      // LexicalLookup ind = new LexicalLookup(jdbcConnectString, jdbcDriver,
      // localTerminologyFile, acronymsFile);
      LexicalLookup ind = new LexicalLookup();

      while ((sentence = in.readLine()) != null) {

        // List<String> sentenceTokens = tokenizer.tokenize(sentence);
        // List<List<LexRecord>> termLists = ind.lookup(sentenceTokens);

        List<List<LexRecord>> termLists = ind.lookup(sentence);
        for (int k = 0; k < termLists.size(); k++) {
          List<LexRecord> aTermSet = termLists.get(k);
          for (int m = 0; m < aTermSet.size(); m++) {
            out.println(aTermSet.get(m).toString());
            out.flush();
          } // end loop through term Groups
        } // end loop through the terms of the sentence
      } // end loop through the sentence tokens

      in.close();
      ind.finalize();
      out.close();

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, "Something went wrong " + e.toString());
    } catch (Throwable e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, "Something went wrong " + e.toString());
    }

  } // end Method lookup() ------------------------

  // ----------------------------------------------
  /**
   * getOptions
   * 
   * @param args
   * @param in
   * @param out
   * @throws FileNotFoundException
   * @throws UnsupportedEncodingException
   */
  // ----------------------------------------------
  private static void getOptions(String[] args, Object[] params)
    throws UnsupportedEncodingException, FileNotFoundException {
    BufferedReader in;
    PrintWriter out;
    String inputFileName = null;
    String outputFileName = null;
    if (args != null) {
      gov.nih.cc.rmd.nlp.framework.utils.NameValue[] arg = U.getOpts(args);

      for (int i = 0; i < args.length; i++) {
        String name = arg[i].getName();
        String value = arg[i].getValue();
        if (name.startsWith("inputFileName")) {
          inputFileName = value;
        } else if (name.startsWith("outputFileName")) {
          outputFileName = value;
        }
      } // end loop through the arguments
    } // end if there are any arguments

    if (inputFileName != null)
      in = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName), "UTF-8"));
    else
      in = new BufferedReader(new InputStreamReader(System.in));

    if (outputFileName != null)
      out = new PrintWriter(outputFileName);
    else
      out = new PrintWriter(System.out);

    params[0] = in;
    params[1] = out;

  } // end Method getOptions() ---------------

  // -----------------------------------------
  /**
   * getAux retrieves a list of LexicalRecords from the local terminology for a
   * given string and the number of tokens
   * 
   * Note: The key is a string composed of the reversed ordered tokens of a term
   * or tokens of a sentence.
   * 
   * @param pNumberOfTokens
   * @param pKey
   * @return List<LexRecord>
   * @throws Exception
   */
  // -----------------------------------------
  private List<LexRecord> getAux(int pNumberOfTokens, String pKey) throws Exception {
    List<LexRecord> returnValue = null;

    if (this.termLookupLocal != null)
      returnValue = this.termLookupLocal.get(pNumberOfTokens, pKey);

    if (returnValue != null && returnValue.size() < 1)
      returnValue = null;

    return returnValue;
  } // end Method getAuxLocal() --------------

  // -----------------------------------------
  /**
   * generateLookAheadKeys returns a list of strings that cover a token in a
   * sentence and the n tokens before it. For look ahead = 3 \|/ sentence-> a b
   * c d e f g
   * 
   * will return -------------\|/ c d e f f e d c d e f f e d e f f e f f /|\
   * That's a white lie. it will return -----+
   * 
   * @param pSentenceTokens
   * @param pI
   * @param pLookAhead
   * @return List<String>
   */
  // -----------------------------------------
  private List<String> generateLookAheadKeys(List<String> pSentenceTokens, int pTokenInFocus,
    int pLookAhead) {

    ArrayList<String> buff = new ArrayList<String>();

    for (int lookAhead = pLookAhead; lookAhead > 0; lookAhead--) {

      StringBuffer key = new StringBuffer();
      key.append(pSentenceTokens.get(pTokenInFocus));
      key.append(" ");
      for (int i = 1; ((i < lookAhead) && (pTokenInFocus - i >= 0)); i++) {
        key.append(pSentenceTokens.get(pTokenInFocus - i));
        key.append(" ");
      }
      buff.add(key.toString().trim());
    }

    return buff;
  } // end Method generateLookAheadString() -

  // -----------------------------------------
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
      String[] tokens = pKey.split(" ");
      numberOfTokens = tokens.length;

    }
    return numberOfTokens;

  } // end Method numberOfTokens() -----------

  // -----------------------------------------
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
        key.append(tokens.get(tokenCtr).toLowerCase());
        if (tokenCtr > 0)
          key.append(" ");
      } else {
        key.append(" ");
      }
    }

    return key.toString().trim();
  } // end Method key() ----------------------

  // ==========================================
  /**
   * addTerm adds term to the lexicon
   *
   * @param pLRAGRTerm (row in lragr format)
   * 
   */
  // ==========================================
  public void addTerm(String pLRAGRTerm) {
    try {
      this.termLookupLocal.index(pLRAGRTerm);
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, "Bad term " + pLRAGRTerm);

    }

  } // end Method addTerm() ==================

  // ==========================================
  /**
   * addTerms adds terms to the lexicon
   *
   * @param pLRAGRTerms (rows in lragr format)
   * 
   */
  // ==========================================
  public void addTerms(List<String> pLRAGRTerms) {

    if (pLRAGRTerms != null && !pLRAGRTerms.isEmpty())
      for (String termRow : pLRAGRTerms) {
        try {
          addTerm(termRow);
        } catch (Exception e) {
          e.printStackTrace();
          GLog.println(GLog.ERROR_LEVEL, "Bad term " + termRow);

        }

      }
  } // end Method addTerms() ==================

  // LRAGR columns
  public final static int EUI = 0;

  public final static int TERM = 1;

  public final static int CATEGORY = 2;

  public final static int INFLECTION = 3;

  public final static int UNINFLECTION = 4;

  public final static int CITATION_FORM = 5;

  private TermUtils tokenize = null;

  private TermLookupLocal termLookupLocal = null;

} // end Class LexicalLookup() ---------------
