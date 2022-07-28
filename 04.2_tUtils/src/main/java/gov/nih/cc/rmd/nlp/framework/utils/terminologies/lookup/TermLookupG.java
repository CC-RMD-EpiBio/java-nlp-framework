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
/**
 * TermLookupG implements the TermLookupInterface
 * with the sophia created Indexes configured for MRCONSOSTY records
 * 
 * that is
 *    each row of an lragr file will be a separate record - so there will be duplicates
 *    that differ only by source 
 *    The other version concatnates rows into a single row with all possible sources, cui's, and semantic types in one row.
 *
 *
 * @author  Guy Divita 
 * @created May 16, 2018
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies.lookup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermLookupInterface;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.LexRecord;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.OrderByLenthComparator;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.TermLookupUtils;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.term.TermLookupLocal;

/**
 * Reference implementation of {@link TermLookupLocal}. Includes hibernate tags
 * for MEME database.
 */
public class TermLookupG implements TermLookupInterface {

  /** The Constant LRAGR_FIELD_ID. */
  public static final int LRAGR_FIELD_ID = 0;

  /** The Constant LRAGR_FIELD_KEY. */
  public static final int LRAGR_FIELD_KEY = 1;

  /** The Constant LRAGR_FIELD_POS. */
  public static final int LRAGR_FIELD_POS = 2;

  /** The Constant LRAGR_FIELD_INFL. */
  public static final int LRAGR_FIELD_INFL = 3;

  /** The Constant LRAGR_FIELD_BASEFORM. */
  public static final int LRAGR_FIELD_BASEFORM = 4;

  /** The Constant LRAGR_FIELD_CITATION_FORM. */
  public static final int LRAGR_FIELD_CITATION_FORM = 5;

  /** The Constant LRAGR_FIELD_CATEGORIES. */
  public static final int LRAGR_FIELD_CATEGORIES = 6;

  /** The Constant LRAGR_FIELD_SAB. */
  public static final int LRAGR_FIELD_SAB = 7;

  /** The Constant LRAGR_FIELD_SOURCE_ID. */
  public static final int LRAGR_FIELD_SOURCE_ID = 8;

  /** The Constant LRAGR_FIELD_FLOW_HISTORY. */
  public static final int LRAGR_FIELD_FLOW_HISTORY = 9;

  /** The Constant LRAGR_FIELD_DISTANCE. */
  public static final int LRAGR_FIELD_DISTANCE = 10;

  /** The Constant LRAGR_FIELD_TTY. */
  public static final int LRAGR_FIELD_TTY = 11;

  /** The Constant LRAGR_FIELD_STT. */
  public static final int LRAGR_FIELD_STT = 12;

  /** The Constant LRAGR_FIELD_REVERSE_KEY. */
  public static final int LRAGR_FIELD_REVERSE_KEY = 13;

  /** The Constant LRAGR_FIELD_NUMBER_THAT_ENDS_WITH_KEY. */
  public static final int LRAGR_FIELD_NUMBER_THAT_ENDS_WITH_KEY = 14;

  /** The Constant LRAGR_FIELD_CUI. */
  public static final int LRAGR_FIELD_CUI = LRAGR_FIELD_ID;

  // -----------------------------------------
  /**
   * constructor Make sure you call init( with args or termFiles ) if you use
   * this constructor.
   *
   * @throws Exception the exception
   */
  // -----------------------------------------
  public TermLookupG() throws Exception {

  } // end constructor () --------------------

  // -----------------------------------------
  /**
   * constructor.
   *
   * @param pArgs the args
   * @param pNumberOfRecords the number of records
   * @throws Exception the exception
   */
  // -----------------------------------------
  public TermLookupG(String[] pArgs, int pNumberOfRecords) throws Exception {

    try {
      this.init(pArgs, pNumberOfRecords);
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue initializing the termLookupSophiaImpl " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, msg);
      throw e;
    }

  } // end constructor () --------------------

  // -----------------------------------------
  /**
   * initialize is used for loading and indexing the terminology tables.
   *
   * @param pTerminologyFiles the terminology files
   * @throws Exception the exception
   */
  // -----------------------------------------
  @Override
  public void init(String[] pTerminologyFiles) throws Exception {

    init(pTerminologyFiles, 1000);
  }

  // -----------------------------------------
  /**
   * initialize is used for loading and indexing the terminology tables.
   *
   * @param pTerminologyFiles the terminology files
   * @param pNumberOfRecords the number of records
   * @throws Exception the exception
   */
  // -----------------------------------------
  public void init(String[] pTerminologyFiles, int pNumberOfRecords) throws Exception {

    if (!alreadyLoaded) {

      this.termLookupUtils = new TermLookupUtils();
      termIndexSingleWord = new ConcurrentHashMap<String, List<LexRecord>>(pNumberOfRecords);
      termIndexTwoWord = new ConcurrentHashMap<String, List<LexRecord>>((int) (pNumberOfRecords * .20));
      termIndexThreeWord = new ConcurrentHashMap<String, List<LexRecord>>((int) (pNumberOfRecords * .20));
      termIndexRestWord = new ConcurrentHashMap<String, List<LexRecord>>((int) (pNumberOfRecords * .20));

      this.orderByLengthComparator = new OrderByLenthComparator();

      this.indexx(pTerminologyFiles);
      alreadyLoaded = true;

    }

  } // end Method init() --------------------

  /* see superclass */
  @Override
  public void init(String[] pArgs, String[] pTerminologyFiles) throws Exception {

    init(pTerminologyFiles, 1000);

  }

  /* see superclass */
  @Override
  public void init(String pUMLSVersion) {
    // TODO Auto-generated method stub
  
  }

  // -----------------------------------------
  /**
   * get retrieves a list of terms given a key. Multiple records could be
   * returned for a key based on case and part of speech.
   * 
   * Note: The key is a string composed of the tokens of the term in reverse
   * order. Also, punctuation has been replaced by spaces.
   *
   * @param pKey the key
   * @return List<LexRecord>
   * @throws Exception the exception
   */
  // -----------------------------------------
  @Override
  public List<LexRecord> get(String pKey) throws Exception {

    List<LexRecord> returnValue = null;

    
    returnValue = termIndexSingleWord.get(pKey);

    if (returnValue == null)
      returnValue = termIndexTwoWord.get(pKey);

    if (returnValue == null)
      returnValue = termIndexThreeWord.get(pKey);

    if (returnValue == null)
      returnValue = termIndexRestWord.get(pKey);

    return returnValue;
  } // end Method get() ---------------------

  // -----------------------------------------
  /**
   * getSimple retrieves a list of terms given a key. Multiple records could be
   * returned for a key based on case and part of speech.
   *
   * @param pTerminologies the terminologies
   * @param pKey the key
   * @return String[]
   * @throws Exception the exception
   */
  // -----------------------------------------
  @Override
  public String[] getSimple(String[] pTerminologies, String pKey) throws Exception {

    String[] returnValue = null;
    List<LexRecord> lexItems = null;

    lexItems = get(pKey);
    
    returnValue = LexRecord.lexItemsToStringArray( lexItems );

    if (returnValue != null && returnValue.length > 0 && pTerminologies != null
        && pTerminologies.length > 0)
      returnValue = filterToSabs(pTerminologies, returnValue);

    return returnValue;
  } // end Method get() ---------------------

 
  // =================================================
  /**
   * filterToSabs filters the rows to rows that only come from the source(s)
   * (sab=source abbreviation) passed in.
   *
   * @param pSabs the sabs
   * @param pRows the rows
   * @return String[]
   */
  // =================================================
  public final String[] filterToSabs(String[] pSabs, String[] pRows) {

    String[] returnVal = null;
    ArrayList<String> buff = null;
    String sab = null;
    if (pRows != null && pRows.length > 0) {
      buff = new ArrayList<String>(pRows.length);

      for (String row : pRows) {

        String cols[] = U.split(row);

        if (cols != null && cols.length > LRAGR_FIELD_SAB)
          sab = cols[LRAGR_FIELD_SAB];

        if (sab != null && pSabs != null && pSabs.length > 0)
          for (String aSab : pSabs)
            if (sab.equals(aSab)) {
              buff.add(row);
              break;
            }
      }

    }
    if (buff != null && !buff.isEmpty())
      returnVal = buff.toArray(new String[buff.size()]);

    return returnVal;
  }

  // -----------------------------------------
  /**
   * getSimple retrieves a list of terms given a key. Multiple records could be
   * returned for a key based on case and part of speech.
   *
   * @param pKey the key
   * @return String[]
   * @throws Exception the exception
   */
  // -----------------------------------------
  @Override
  public String[] getSimple(String pKey) throws Exception {

    String[] returnValue = getSimple(null, pKey);

    return returnValue;
  } // end Method get() ---------------------

//-----------------------------------------
 /**
  * getNotSimple retrieves a list of terms given a key. Multiple records could be
  * returned for a key based on case and part of speech. This method
  * includes a sub query - a semantic type or tree to further
  * filter to
  *
  * @param pTree  - the semantic type or tree to filter to
  * @param pKey the key
  * @return String[]
  * @throws Exception the exception
  */
 // -----------------------------------------
 @Override
  public String[] getNotSimple(String pTree, String pKey) throws Exception {
   
   List<LexRecord>  candidates =  get( pKey);
   List<LexRecord>  returnVals = null;
   String[]         returnRows = null;
   
   if ( candidates != null )
   
     if ( pTree == null )
       returnVals = candidates;
     else {
       returnVals= new ArrayList<LexRecord>( candidates.size() );
       for (LexRecord row: candidates ) {
        
         if ( row.getSemanticType().contains( pTree ) 
               || row.getTree().contains( pTree ) )
             
           returnVals.add(  row );
       }   
     }
   
     
   returnRows = LexRecord.lexItemsToStringArray( returnVals);
   
   return returnRows;
  } // end Method getNotSimple() -------------
 
//-----------------------------------------
/**
* getNotSimple retrieves a list of terms given a key. Multiple records could be
* returned for a key based on case and part of speech. This method
* includes a sub query - a semantic type or tree to further
* filter to
*
* @param pSabs  (colon separated list of sabs )
* @param pTree  - the semantic type or tree to filter to
* @param pKey the key
* @return String[]
* @throws Exception the exception
*/
//-----------------------------------------
@Override
public String[] getNotSimple(String pSabs, String pTree, String pKey) throws Exception {

  String[] returnRows2 = null;
  String[] returnRows = getNotSimple( pTree, pKey);
     
  if (returnRows != null  && returnRows.length > 0 ) {
    
    String sabs[] = U.split( pSabs, ":");
    if ( sabs != null && sabs.length > 0 ) {
      returnRows2 = filterToSabs( sabs, returnRows);
    }
    else
      returnRows2 = returnRows;
    
  }

return returnRows2;
} // end Method getNotSimple() -------------


//-----------------------------------------
/**
* getNotSimple retrieves a list of terms given a key. Multiple records could be
* returned for a key based on case and part of speech. This method
* includes a sub query - a semantic type or tree to further
* filter to
*
* @param pTree  - the semantic type or tree to filter to
* @param pKey the key
* @return List<LexRecord>
* @throws Exception the exception
*/
//-----------------------------------------
@Override
public List<LexRecord> getNotThatSimple( String tree, String key) throws Exception {
// TODO Auto-generated method stub
return null;
}

//-----------------------------------------
/**
* getNotSimple retrieves a list of terms given a key. Multiple records could be
* returned for a key based on case and part of speech. This method
* includes a sub query - a semantic type or tree to further
* filter to
*
* @param pSabs  (colon separated list of sabs )
* @param pTree  - the semantic type or tree to filter to
* @param pKey the key
* @return List<LexRecord>
* @throws Exception the exception
*/
//-----------------------------------------
  @Override
public List<LexRecord> getNotThatSimple(String sab, String tree, String key) throws Exception {
  // TODO Auto-generated method stub
  return null;
}
  
  

  // -----------------------------------------
  /**
   * get retrieves a list of terms given a key. Multiple records could be
   * returned for a key based on case and part of speech.
   *
   * @param pKey the key
   * @param pNumberOfTokens the number of tokens
   * @return List<LexRecord>
   */
  // -----------------------------------------
  @Override
  public List<LexRecord> get(String pKey, int pNumberOfTokens) {

    List<LexRecord> returnValue = null;

    switch (pNumberOfTokens) {
      case 1:
        returnValue = termIndexSingleWord.get(pKey.toLowerCase());
        if ( returnValue == null && !U.isAllLowerCase(pKey) )
          returnValue = termIndexSingleWord.get(pKey);
        break;
      case 2:
        returnValue = termIndexTwoWord.get(pKey.toLowerCase());
        if ( returnValue == null && !U.isAllLowerCase(pKey) )
          returnValue = termIndexTwoWord.get(pKey);

        break;
      case 3:
        returnValue = termIndexThreeWord.get(pKey.toLowerCase());
        if ( returnValue == null && !U.isAllLowerCase(pKey) )
          returnValue = termIndexThreeWord.get(pKey);
        
        break;
      default: {
        returnValue = termIndexRestWord.get(pKey.toLowerCase());
        if ( returnValue == null && !U.isAllLowerCase(pKey) )
          returnValue = termIndexRestWord.get(pKey);
        break;
      }
    } // end switch

    return returnValue;

  } // end Method get() -----------------------

  // -------------------------------------------------------
  /**
   * lookupTerm puts together the tokens and looks them up.
   *
   * @param tokens the tokens
   * @return LexRecord
   * @throws Exception the exception
   */
  // -------------------------------------------------------
  List<LexRecord> lookupTerm(String[] tokens) throws Exception {

    List<LexRecord> lexRecords = null;
    StringBuffer buff = new StringBuffer();
    if (tokens != null) {
      for (int i = 0; i < tokens.length; i++)
        buff.append(tokens[i] + " ");
      String key = buff.toString().trim();

      lexRecords = this.get(key, tokens.length);

    }
    // if ( lexRecords != null)
    // System.err.println(" found a match");

    return lexRecords;
  } // end Method lookupTerm() -----------------------

  // -----------------------------------------
  /**
   * calculateSubTerms looks up the subterms of this term. Note that this is not
   * just the longest span from the left or right of the term. There can be
   * inner terms as well. For example, for a term with tokens a b c d, there can
   * be b c d c d d a b c a b b c <------- b <---
   *
   * @param pRow the row
   * @throws Exception the exception
   */
  // -----------------------------------------
  private void calculateSubTerms(String pRow) throws Exception {

    LexRecord tmpRecord = this.termLookupUtils.createLexRecord(pRow);
    String key = tmpRecord.getKey();

    String[] tokens = null;
    Hashtable<String, LexRecord> subTerms = null;

    List<String> buff = this.termLookupUtils.tokenize(key);
    if (buff != null) {
      ArrayList<String> buff2 = new ArrayList<String>(buff.size());
      for (int i = 0; i < buff.size(); i++) {
        if (buff.get(i) != null && buff.get(i).length() > 0 && !buff.get(i).equals(" ")) {

          buff2.add(buff.get(i));
        }
      }
      tokens = buff2.toArray(new String[buff2.size()]);
    }

    if (tokens != null && tokens.length > 1 && buff != null) {

      List<LexRecord> foundRecords = this.lookupTerm(buff.toArray(new String[buff.size()]));

      if (foundRecords == null)
        throw new Exception("Couldnt find record for |" + key + "|" + pRow);
      subTerms = calculateSubTermsAux(tokens);
      @SuppressWarnings("unchecked")
      List<LexRecord> subTermzz = (List<LexRecord>) U.HashToList(subTerms);
      LexRecord[] subTermz = orderByTokenLength(subTermzz);

      if (subTerms != null) {
        for (LexRecord lexRec : foundRecords) {
          lexRec.setSubTerms(subTermz);
        }
      }

    }

  } // end Method calculateSubTerms() --------------

  // -----------------------------------------
  /**
   * calculateLookAhead will add the number of lookaheads one has to do to make
   * sure this gets found, and marks orphan words when found.
   * 
   * This method has the side effect that orphan terms and head terms are added
   * to the single word index that they can get easily retrieved during the
   * lexical lookup process to be able to calculate the look a-head mechanism.
   * 
   * This method also computes the subterms associated with this term.
   *
   * @param pRow the row
   * @throws Exception the exception
   */
  // -----------------------------------------
  protected void calculateLookAhead(String pRow) throws Exception {

    LexRecord tmpRecord = termLookupUtils.createLexRecord(pRow);
    String key = tmpRecord.getKey();
    int numberOfTokens = tmpRecord.getNumberOfTokens();

    List<String> tokens = this.termLookupUtils.tokenize(key);
    String lastToken = tokens.get(0);

    List<LexRecord> singleRecords = null;
    // Set the lookahead number for each ending
    if (numberOfTokens > 0) {
      singleRecords = termIndexSingleWord.get(lastToken);
      if (singleRecords != null) {
        for (int i = 0; i < singleRecords.size(); i++) {
          LexRecord aRecord = singleRecords.get(i);
          if (numberOfTokens > aRecord.getLongestTermWithThisEnding())
            aRecord.setLongestTermWithThisEnding(numberOfTokens);

        }
      } else {
        // make a orphan lexical term record
        // System.err.println("orphan word|" + lastToken);
        ArrayList<LexRecord> records = new ArrayList<LexRecord>();
        LexRecord record = new LexRecord(lastToken, lastToken, lastToken, tmpRecord.getPos(),
            "orphan", true, numberOfTokens);
        record.setLongestTermWithThisEnding(numberOfTokens);
        records.add(record);
        termIndexSingleWord.put(lastToken, records);
      }
    }
  } // end Method calculateLookAhead() --------------

  // -----------------------------------------
  /**
   * calculateLookAhead goes through the multi-word term keys and creates the
   * max-look-ahead freq attached to the first token* of those keys
   * 
   * If that token is not itself an entry, one is made, creating an "orphan"
   * key.
   * 
   * *The first token (reading right to left rather than left to right)
   *
   * @throws Exception the exception
   */
  // --------------------------------------
  protected void calculateLookAhead() throws Exception {

    // ----------------
    // Loop through the multi-word records again
    
    
   
   
   
    

    Enumeration<String> keys = termIndexRestWord.keys();
    ThreadedCaculateLookAheadAux calculateLookaheadRest =  new ThreadedCaculateLookAheadAux( keys , this);
    Thread calculateRestKeys  = new Thread(calculateLookaheadRest );
    calculateRestKeys.start() ;

    keys = termIndexThreeWord.keys();
    ThreadedCaculateLookAheadAux calculateLookaheadThreeWord =  new ThreadedCaculateLookAheadAux( keys , this);
    Thread calculateThreeWordtKeys  = new Thread(calculateLookaheadThreeWord );
    calculateThreeWordtKeys.start() ;
 
    keys = termIndexTwoWord.keys();
    ThreadedCaculateLookAheadAux calculateLookaheadTwoWord =  new ThreadedCaculateLookAheadAux( keys , this);
    Thread calculateTwoWordtKeys  = new Thread(calculateLookaheadTwoWord );
    calculateTwoWordtKeys.start() ;
   
    // ------------------------
    // join these up
    calculateRestKeys.join() ;
    calculateThreeWordtKeys.join();
    calculateTwoWordtKeys.join();
    
    System.err.println( "finished calculating the lookahead ");
    Runtime.getRuntime().gc();
    
    

  } // end Method createLexRecord() ------------------------

  // =======================================================
  /**
   * calculateLookAhead 
   *
   * @param keys the keys
   * @throws Exception the exception
   */
  // =======================================================
  void calculateLookAhead(Enumeration<String> keys) throws Exception {
    while (keys.hasMoreElements()) {

      String key = keys.nextElement();
      List<LexRecord> lexRecords = termIndexRestWord.get(key);

      if (lexRecords == null)
        lexRecords = termIndexThreeWord.get(key);

      if (lexRecords == null)
        lexRecords = termIndexTwoWord.get(key);

      if (lexRecords == null)
        lexRecords = termIndexSingleWord.get(key);

      if (lexRecords != null)
        for (LexRecord record : lexRecords) {

          calculateLookAhead(record);
        } // loop thru records

    } // end loop through keys for the index
    // End Method calculateLookAhead() ======================
  }

  // -----------------------------------------
  /**
   * calculateLookAheadAndOrphanWords will add the number of lookaheads one has
   * to do to make sure this gets found, and marks orphan words when found.
   * 
   * This method has the side effect that orphan terms and head terms are added
   * to the single word index that they can get easily retrieved during the
   * lexical lookup process to be able to calculate the look a-head mechanism.
   * 
   * This method also computes the subterms associated with this term.
   *
   * @param pRecord the record
   * @throws Exception the exception
   */
  // -----------------------------------------
  protected void calculateLookAhead(LexRecord pRecord) throws Exception {

    LexRecord tmpRecord = pRecord;
    String key = tmpRecord.getKey();
    int numberOfTokens = tmpRecord.getNumberOfTokens();

    List<String> tokens = this.termLookupUtils.tokenize(key);
    String lastToken = tokens.get(0);

    List<LexRecord> singleRecords = null;
    // Set the lookahead number for each ending
    if (numberOfTokens > 1) {
      singleRecords = termIndexSingleWord.get(lastToken);
      if (singleRecords != null) {
        for (int i = 0; i < singleRecords.size(); i++) {
          LexRecord aRecord = singleRecords.get(i);
          if (numberOfTokens > aRecord.getLongestTermWithThisEnding())
            aRecord.setLongestTermWithThisEnding(numberOfTokens);

        }
      } else {
        // make a orphan lexical term record
        // System.err.println("orphan word|" + lastToken);
        ArrayList<LexRecord> records = new ArrayList<LexRecord>(10);
        LexRecord record = new LexRecord(lastToken, lastToken, lastToken, tmpRecord.getPos(),
            "orphan", true, numberOfTokens);
        record.setLongestTermWithThisEnding(numberOfTokens);
        records.add(record);
        syncronizedTermIndexSingleWordPut( lastToken, records);
      }
    }
  } // end Method calculateLookAhead() --------------

  // -----------------------------------------
  /**
   * calculateSubTerms looks up the subterms of this term. Note that this is not
   * just the longest span from the left or right of the term. There can be
   * inner terms as well. For example, for a term with tokens a b c d, there can
   * be b c d c d d a b c a b b c <------- b <---
   * 
   * Note: The tokens being passed in are from keys that are the reverse order,
   * punctuation replaced.
   *
   * @param tokens the tokens
   * @return the hashtable
   * @throws Exception the exception
   */
  // -----------------------------------------
  private Hashtable<String, LexRecord> calculateSubTermsAux(String[] tokens) throws Exception {

    Hashtable<String, LexRecord> subTerms = new Hashtable<String, LexRecord>(5);

    // ---------------------------------
    // Look up single tokens
    // ---------------------------------
    List<LexRecord> singleTerms = permuteSingleTerms(tokens);
    addSubTerms(subTerms, singleTerms);

    // --------------------------------
    // permute subterms from the right
    // --------------------------------
    List<LexRecord> rightSubTerms = permuteFromRight(tokens);
    addSubTerms(subTerms, rightSubTerms);

    // --------------------------------
    // permute subterms from the left
    // --------------------------------
    List<LexRecord> leftSubTerms = permuteFromLeft(tokens);
    addSubTerms(subTerms, leftSubTerms);

    // --------------------------------
    // permute inner terms
    // --------------------------------
    if (tokens.length > 3) {
      String subTokens[] = new String[tokens.length - 2];
      for (int i = 1; i < tokens.length - 1; i++)
        subTokens[i - 1] = tokens[i];
      Hashtable<String, LexRecord> innerTerms = calculateSubTermsAux(subTokens);
      addSubTerms(subTerms, innerTerms);
    }
    return subTerms;

  } // end Method calculateSubTerms() ----------------------

  // -------------------------------------------------------
  /**
   * permuteSingleTerms.
   *
   * @param pTokens the tokens
   * @return List<LexRecord>
   * @throws Exception the exception
   */
  // -------------------------------------------------------
  private List<LexRecord> permuteSingleTerms(String[] pTokens) throws Exception {

    Hashtable<String, LexRecord> subTermz = new Hashtable<String, LexRecord>();

    String tokens[] = new String[1];
    for (int i = 0; i < pTokens.length; i++) {
      tokens[0] = pTokens[i];
      List<LexRecord> recs = lookupTerm(tokens);
      addSubTerms(subTermz, recs);

    } // end loop through tokens

    @SuppressWarnings("unchecked")
    List<LexRecord> subTerms = (List<LexRecord>) U.HashToList(subTermz);
    return subTerms;
  } // end Method permuteSingleTerms() ----------------------

  // -------------------------------------------------------
  /**
   * addSubTerms.
   *
   * @param subTerms the sub terms
   * @param innerTerms the inner terms
   */
  // -------------------------------------------------------
  private void addSubTerms(Hashtable<String, LexRecord> subTerms,
    Hashtable<String, LexRecord> innerTerms) {

    Enumeration<String> e = innerTerms.keys();
    while (e.hasMoreElements()) {
      String key = e.nextElement();
      subTerms.put(key, innerTerms.get(key));
    }

  } // end Method addSubTerms() -----------------------------

  // -------------------------------------------------------
  /**
   * addSubTerms.
   *
   * @param subTerms the sub terms
   * @param moreSubTerms the more sub terms
   */
  // -------------------------------------------------------
  private void addSubTerms(Hashtable<String, LexRecord> subTerms, List<LexRecord> moreSubTerms) {

    if (moreSubTerms != null)
      for (LexRecord aLexRec : moreSubTerms) {

        subTerms.put(aLexRec.getKey(), aLexRec);
      }

  } // end addSubTerms() -----------------------------------

  // -------------------------------------------------------
  /**
   * permuteFromRight
   * 
   * Note: The tokens being passed in are from keys that are the reverse order,
   * punctuation replaced.
   *
   * @param tokens the tokens
   * @return List<LexRecord>
   * @throws Exception the exception
   */
  // -------------------------------------------------------
  List<LexRecord> permuteFromRight(String[] tokens) throws Exception {

    Hashtable<String, LexRecord> subTermz = new Hashtable<String, LexRecord>();

    List<LexRecord> recs = lookupTerm(tokens);
    addSubTerms(subTermz, recs);

    // the recursive part
    if (tokens.length > 1) {
      String stemmedTokens[] = new String[tokens.length - 1];
      for (int i = 0; i < tokens.length - 1; i++) {
        stemmedTokens[i] = tokens[i];
      } // end loop
      List<LexRecord> stemmedTerms = permuteFromRight(stemmedTokens);
      addSubTerms(subTermz, stemmedTerms);

    }

    @SuppressWarnings("unchecked")
    List<LexRecord> subTerms = (List<LexRecord>) U.HashToList(subTermz);
    return subTerms;
  } // end Method permuteFromLeft() ------------------------

  // -------------------------------------------------------
  /**
   * permuteFromLeft
   * 
   * Note: The tokens being passed in are from keys that are the reverse order,
   * punctuation replaced.
   *
   * @param tokens the tokens
   * @return List<LexRecord>
   * @throws Exception the exception
   */
  // -------------------------------------------------------
  private List<LexRecord> permuteFromLeft(String[] tokens) throws Exception {

    Hashtable<String, LexRecord> subTermz = new Hashtable<String, LexRecord>();

    List<LexRecord> recs = lookupTerm(tokens);
    addSubTerms(subTermz, recs);

    // the recursive part
    if (tokens.length > 0) {
      String stemmedTokens[] = new String[tokens.length - 1];
      for (int i = tokens.length - 1; i > 0; i--) {
        stemmedTokens[i - 1] = tokens[i];
      } // end loop
      List<LexRecord> stemmedTerms = permuteFromLeft(stemmedTokens);
      addSubTerms(subTermz, stemmedTerms);

    }
    @SuppressWarnings("unchecked")
    List<LexRecord> subTerms = (List<LexRecord>) U.HashToList(subTermz);

    return subTerms;
  } // end Method permuteFromLeft() ------------------------

  // ---------------------------------------------
  // Indexing Methods
  // ---------------------------------------------
  // -----------------------------------------
  /**
   * indexx opens the URL up, reads each line and indexes it.
   *
   * @param pLexicalListFile the lexical list file
   * @throws Exception the exception
   * @deprecated
   */
  // -----------------------------------------
  @SuppressWarnings("unused")
  private void indexx(String pLexicalListFile) throws Exception {

    String line = null;
    try {
      // System.err.println("name of file = " + pLexicalListFile );
      try (final BufferedReader in = U.getClassPathResource(pLexicalListFile);) {

        while ((line = in.readLine()) != null)
          if ((!line.startsWith("#")) && (line.trim().length() > 0)) {
            index(line);
          }
        in.close();
      }
      // -------------------------------------
      // close the file, re-open it and figure out
      // how many many look-aheads are needed, and if the
      // term contains orphan words
      // -------------------------------------

      try (final BufferedReader in2 = U.getClassPathResource(pLexicalListFile);) {

        while ((line = in2.readLine()) != null)
          if (!line.startsWith("#")) {
            calculateLookAhead(line);
            calculateSubTerms(line);
          }

        in2.close();
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Something wrong: " + e.toString());
    }

  } // end Method indexx() --------------------

  // -----------------------------------------
  /**
   * index opens the URLs up, reads all the files into one buffer, then indexes
   * them.
   *
   * @param pLexicalListFiles the lexical list files
   * @throws Exception the exception
   */
  // -----------------------------------------
  @SuppressWarnings("deprecation")
  protected void indexx(String[] pLexicalListFiles) throws Exception {

    // StringBuilder buff = new StringBuilder();
    
    PerformanceMeter m = new PerformanceMeter();
    m.begin(" Starting the hash part of the index ");
    ConcurrentHashMap<String,Thread> activeThreads  = new ConcurrentHashMap<String,Thread>( pLexicalListFiles.length );
    try {
     if (pLexicalListFiles != null) {

        for (String fileName : pLexicalListFiles) {
          
          TermLookupGAux termLookupGAux =  new TermLookupGAux( fileName , this);
           Thread termIndexThread  = new Thread(termLookupGAux );
           activeThreads.put(fileName, termIndexThread);
           termIndexThread.start() ;
          //
          // calls termLookupGAux.index( fileName) inside a new thread
          //
          }

        } // end loop thru the local files

       // ----------------------------
       // need a way to know that all the threads are done here
         
         for ( String fileName : pLexicalListFiles ) {
           Thread aThread = activeThreads.get( fileName);
          
           if ( aThread != null )
             aThread.join();
      
           activeThreads.remove( fileName);
         }
         
        // 
        // String lexiconContent = buff.toString();

        m.stop(" End the hash part of the index ");
        // ----------------------------
        // Calculate the max lookup now
        m = new PerformanceMeter();
        m.begin("Starting the process of calculating the look-ahead ");
        calculateLookAhead();
        m.stop("Finished the process of calculating the look-ahead ");
      
    
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue went wrong with indexing the local terminology " + e.toString();
      System.err.println(msg);
      throw new RuntimeException(msg);
    }

  } // end Method indexx() --------------------


// =================================================
  /**
   * setTermIndexFileDir  sets the directory where the 
   * hash tables will reside for quicker read
   * @throws Exception 
   * @parm pDir
   * 
  */
  // =================================================
   public void setTermIndexFileLocation(String pDir) throws Exception {
    
     File aDir = new File ( pDir);
     FileOutputStream fos = null;
     if ( aDir.exists() && aDir.canWrite() ) {
       this.termIndexSingleWordFile = pDir + "/termIndexSingleWordHash.hsh.gz";
       this.termIndexTwoWordFile = pDir + "/termIndexTwoWordHash.hsh.gz";
       this.termIndexThreeWordFile = pDir + "/termIndexThreeWordHash.hsh.gz";
       this.termIndexRestWordFile =  pDir + "/termIndexRestWordHash.hsh.gz";
     } else {
       throw new Exception ( "Not able to set the TermIndexFileLocation because the dir " + pDir + " does not exist or can be written to " );
     }
     
   
    
  } // end Method saveHashes() ----------------------


  // -----------------------------------------
  /**
   * index indexes this lragr entry.
   *
   * @param pRow the row
   * @throws Exception the exception
   */
  // -----------------------------------------
  public void index(String pRow) throws Exception {

    LexRecord tmpRecord = this.termLookupUtils.createLexRecord(pRow);
    String key = tmpRecord.getKey();
    List<String> tokens = this.termLookupUtils.tokenize(key);
    int numberOfTokens = tokens.size();
    
  
    List<LexRecord> records = null;

    switch (numberOfTokens) {
      case 0:
        break;
      case 1:
        
        records = termIndexSingleWord.get(key.toLowerCase());
        if ( records == null  && !U.isAllLowerCase( key ) )
          records = termIndexSingleWord.get(key);
        
        break;
      case 2:
        
        records = termIndexTwoWord.get(key.toLowerCase());
        if ( records == null  && !U.isAllLowerCase( key ) )
          records = termIndexTwoWord.get(key );
        break;
      case 3:
        records = termIndexThreeWord.get(key.toLowerCase());
        if ( records == null  && !U.isAllLowerCase( key ) )
          records = termIndexThreeWord.get(key);
        break;
      default:
        records = termIndexRestWord.get(key.toLowerCase());
        if ( records == null  && !U.isAllLowerCase( key ) )
          records = termIndexRestWord.get(key);
        break;

    } // end switch

    if (records == null) {
      records = new ArrayList<LexRecord>();

      switch (numberOfTokens) {
        case 0:
          break;
        case 1:
          syncronizedTermIndexSingleWordPut( key, records);
          break;
        case 2:
          syncronizedTermIndexTwoWordPut( key, records);
         
          break;
        case 3:
          syncronizedTermIndexThreeWordPut( key, records);
        
          break;
        default:
          syncronizedtermIndexRestWordPut( key, records);
          break;
      }
    }
    records.add(tmpRecord);
   
   
  } // end Method index() -------------------

  // =================================================
  /**
   * syncronizedTermIndexSingleWordPut 
   * 
   * @param key
   * @param records
   * @throws Exception 
  */
  // =================================================
   protected synchronized void syncronizedTermIndexSingleWordPut(String key, List<LexRecord> records) throws Exception {
    termIndexSingleWord.put(key, records);
    
  }  // end Method syncronizedTermIndexSingleWordPut() --------- 
   
  // =================================================
  /**
   * syncronizedTermIndexTwoWordPut 
   * 
   * @param key
   * @param records
  */
  // =================================================
   protected synchronized void syncronizedTermIndexTwoWordPut(String key, List<LexRecord> records) {
    termIndexTwoWord.put(key, records);
    
  }  // end Method syncronizedTermIndexTwoWordPut() --------- 
   
  // =================================================
  /**
   * syncronizedTermIndexThreeWordPut 
   * 
   * @param key
   * @param records
  */
  // =================================================
   protected synchronized void syncronizedTermIndexThreeWordPut(String key, List<LexRecord> records) {
    termIndexThreeWord.put(key, records);
   
    
  }  // end Method syncronizedTermIndexThreeWordPut() --------- 
   
   
   // =================================================
   /**
    * syncronizedTermIndexSingleWordPut 
    * 
    * @param key
    * @param records
   * @throws Exception 
   */
   // =================================================
    protected synchronized void syncronizedtermIndexRestWordPut(String key, List<LexRecord> records) throws Exception {
     termIndexRestWord.put(key, records);
     
   }  // end Method syncronizedtermIndexRestWordPut() --------- 
   
   
  // -----------------------------------------
  /**
   * iterator returns an iterator that passes through each of the hashtables.
   *
   * @return Iterator<lexRecord>>
   */
  // -----------------------------------------
  public Iterator<LexRecord> iterator() {

    ArrayList<LexRecord> recs = new ArrayList<LexRecord>(1000);

    for (Enumeration<List<LexRecord>> eX = termIndexSingleWord.elements(); eX.hasMoreElements();) {
      List<LexRecord> records = eX.nextElement();
      for (Iterator<LexRecord> i = records.iterator(); i.hasNext();)
        recs.add(i.next());
    } // end loop through single word list

    for (Enumeration<List<LexRecord>> eX = termIndexTwoWord.elements(); eX.hasMoreElements();) {
      List<LexRecord> records = eX.nextElement();
      for (Iterator<LexRecord> i = records.iterator(); i.hasNext();)
        recs.add(i.next());
    } // end loop through single word list

    for (Enumeration<List<LexRecord>> eX = termIndexThreeWord.elements(); eX.hasMoreElements();) {
      List<LexRecord> records = eX.nextElement();
      for (Iterator<LexRecord> i = records.iterator(); i.hasNext();)
        recs.add(i.next());
    } // end loop through single word list

    for (Enumeration<List<LexRecord>> eX = termIndexRestWord.elements(); eX.hasMoreElements();) {
      List<LexRecord> records = eX.nextElement();
      for (Iterator<LexRecord> i = records.iterator(); i.hasNext();)
        recs.add(i.next());
    } // end loop through single word list

    return recs.iterator();
  } // end Method iterator() ----------------------

  // -------------------------------------------------------
  /**
   * orderByTokenLength returns a list orderd by the number of tokens (most to
   * least).
   *
   * @param subTermzz the sub termzz
   * @return LexRecord[]
   */
  // -------------------------------------------------------
  private LexRecord[] orderByTokenLength(List<LexRecord> subTermzz) {

    LexRecord[] termsArray = subTermzz.toArray(new LexRecord[subTermzz.size()]);
    Arrays.sort(termsArray, this.orderByLengthComparator);

    return termsArray;
  } // end Method orderByTokenLength() --------

  // ==========================================
  /**
   * addTerm adds term to the lexicon.
   *
   * @param pLRAGRTerm (row in lragr format)
   * @see gov.nih.cc.rmd.framework.utils.terminologies.TermLookupInterface#addTerm(java.lang.String)
   */
  // ==========================================
  @Override
  public void addTerm(String pLRAGRTerm) {
    try {
      this.index(pLRAGRTerm);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Bad term " + pLRAGRTerm);

    }

  } // end Method addTerm() ==================

  // ---------------------------------------
  // Global Variables
  /** The term index single word. */
  // ---------------------------------------
  private static ConcurrentHashMap<String, List<LexRecord>> termIndexSingleWord = null;
  private static ConcurrentHashMap<String, List<LexRecord>> termIndexTwoWord = null;
  private static ConcurrentHashMap<String, List<LexRecord>> termIndexThreeWord = null;
  private static ConcurrentHashMap<String, List<LexRecord>> termIndexRestWord = null;
  protected static boolean alreadyLoaded = false;
  protected TermLookupUtils termLookupUtils = null;
  protected OrderByLenthComparator orderByLengthComparator = null;
  protected String termHashDir = null;
  protected String termIndexSingleWordFile = null;
  protected String termIndexTwoWordFile = null;
  protected String termIndexThreeWordFile = null;
  protected String termIndexRestWordFile = null;

} // end Class TermLookupLocalCoreSophiaImpl() -------
