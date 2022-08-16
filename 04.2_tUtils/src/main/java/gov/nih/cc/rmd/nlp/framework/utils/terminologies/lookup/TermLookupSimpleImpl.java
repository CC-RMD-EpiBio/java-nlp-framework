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
 * TermLookupSophiaImpl implements the TermLookupInterface
 * with the sophia created Indexes
 *
 *    This method relies on the lragr field format for it's contents
 *    For specific purposes, additional content is appended as fields after the lragr
 *    fields.
 *    
 *    Lragr fields:
 *    
 *    id|fully inflected key|pos|inflection|baseForm|citationForm|category
 *    
 *    for umls concept rows the fields are
 *    
 *    
 *     # cui|key|pos|infl|base|citation|tuis|sab|sourceID|history|distance|tty|stt|reverseKey|numberEndsWith
 *
 *    Rows that start with # are comments and are are read in
 *    
 *    Keys should be in lowercase UNLESS the case matters and entries should
 *    then be in the case that an entry should match to.
 *    
 *    That is, if there is ambigious forms like aids and AIDS  there should be two entries
 *    - one for the exact case "AIDS" and one for aids.  
 *
 *
 * @author  Guy Divita 
 * @created May 16, 2018
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies.lookup;


import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermLookupInterface;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.LexRecord;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.OrderByLenthComparator;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.TermLookupUtils;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;

public class TermLookupSimpleImpl implements TermLookupInterface {
  
  
  //     # cui|key|pos|infl|base|citation|tuis|sab|sourceID|history|distance|tty|stt|reverseKey|numberEndsWith
      

 public static final int LRAGR_FIELD_ID                        =  0;
 public static final int LRAGR_FIELD_KEY                       =  1;
 public static final int LRAGR_FIELD_POS                       =  2;
 public static final int LRAGR_FIELD_INFL                      =  3;
 public static final int LRAGR_FIELD_BASEFORM                  =  4;
 public static final int LRAGR_FIELD_CITATION_FORM             =  5;
 public static final int LRAGR_FIELD_CATEGORIES                =  6;
 public static final int LRAGR_FIELD_SAB                       =  7;
 public static final int LRAGR_FIELD_SOURCE_ID                 =  8;
 public static final int LRAGR_FIELD_FLOW_HISTORY              =  9;
 public static final int LRAGR_FIELD_DISTANCE                  = 10;
 public static final int LRAGR_FIELD_TTY                       = 11;
 public static final int LRAGR_FIELD_STT                       = 12;
 public static final int LRAGR_FIELD_REVERSE_KEY               = 13;
 public static final int LRAGR_FIELD_NUMBER_THAT_ENDS_WITH_KEY = 14;

 public static final int LRAGR_FIELD_CUI                       = LRAGR_FIELD_ID;

 

  // -----------------------------------------
  /**
   * constructor   Make sure you call init( with args or termFiles ) if you use this constructor.
   *
   * @throws Exception 
   */
  // -----------------------------------------
  public TermLookupSimpleImpl( ) throws Exception {
 
   
    
  } // end constructor () --------------------
  

  // -----------------------------------------
  /**
   * constructor
   * 
   * @param pArgs
   * @throws Exception 
   */
  // -----------------------------------------
  public TermLookupSimpleImpl( String[] pArgs, int pNumberOfRecords ) throws Exception {
 
    try {
      this.init( pArgs );
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue initializing the termLookupSophiaImpl " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, msg);
      throw e;
    }
    
  } // end constructor () --------------------
  


  /* (non-Javadoc)
   * @see gov.nih.cc.rmd.framework.utils.terminologies.TermLookupInterface#init(java.lang.String)
   */
  @Override
  public void init(String pUMLSVersion) {
    // TODO Auto-generated method stub
    
  }


  // -----------------------------------------
  /**
   * initialize is used for loading and indexing the terminology tables.
   * 
   * @param pTerminologyFiles
   * 
   */
  // -----------------------------------------
   public void init(String[] pTerminologyFiles ) throws Exception {
     
    
     init( pTerminologyFiles, 1000);
   }

    // -----------------------------------------
   /**
    * initialize is used for loading and indexing the terminology tables.
    * 
    * @param pArgs
    * @param pTerminologyFiles
    * 
    */
   // -----------------------------------------
    public void init(String[] pArgs,  String[] pTerminologyFiles) throws Exception {
      
   
      this.init(pTerminologyFiles, 1000);
   
   
    } // end Method init() --------------------

     
   
   
     // -----------------------------------------
     /**
      * initialize is used for loading and indexing the terminology tables.
      * 
      * @param pTerminologyFiles
      * @param pNumberOfRecords
      * 
      */
     // -----------------------------------------
      public void init(String[] pTerminologyFiles, int pNumberOfRecords) throws Exception {
        
      if ( !alreadyLoaded ) {
       
       this.termLookupUtils = new TermLookupUtils( );
       termIndex = new Hashtable<String, String[]>( pNumberOfRecords);
      
       this.orderByLengthComparator = new OrderByLenthComparator();
     
       this.indexx(pTerminologyFiles);
       alreadyLoaded = true;
       
     }
     
   } // end Method init() --------------------
  
   
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
   * @return List<LexRecord>
   * @throws Exception
   */
  // -----------------------------------------
  @Override
  public List<LexRecord> get(String pKey) throws Exception {
   
    List<LexRecord> returnValue = null;
   
    String[] rows = getSimple( pKey);
   
    if ( rows != null && rows.length > 0 ) {
      returnValue = new ArrayList<LexRecord>( rows.length);
      for ( String row: rows ) {
        returnValue.add( this.termLookupUtils.createLexRecord(row)) ;
      }
    }
    
    return returnValue;
  } // end Method get() ---------------------

  
  //-----------------------------------------
 /**
  * getSimple retrieves a list of terms given a key. Multiple records could be
  * returned for a key based on case and part of speech.
  * 
  * 
  * @param pKey
  * @param pNumberOfTokens
  * @return String[]  
  * @throws Exception
  */
 // -----------------------------------------
 @Override
 public String[] getSimple(String pKey) throws Exception {
  
   String[] returnValue = null;
  
    returnValue = termIndex.get(pKey);
   
   return returnValue;
 } // end Method get() ---------------------



 //-----------------------------------------
/**
 * getSimple retrieves a list of terms given a key. Multiple records could be
 * returned for a key based on case and part of speech.
 * 
 * 
 * @param pSabs          limit to these terminologies
 * @param pKey
 * @return String[]  
 * @throws Exception
 */
// -----------------------------------------
@Override
public String[] getSimple(String[] pSabs, String pKey) throws Exception {
 
  List<LexRecord> returnList = null;
  String[]        returnVal = null;
 
   returnList = get(pKey);
   
   if ( returnList != null && !returnList.isEmpty() )
     returnList = filterToSab( pSabs, returnList);
  
   if ( returnList != null && !returnList.isEmpty() ) 
    returnVal = LexRecord.lexItemsToStringArray( returnList);
   
   
   
  return returnVal;
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
 
   String returnVal[] = null;
   List<LexRecord> returnList = getNotThatSimple( pTree, pKey);
   returnVal = LexRecord.lexItemsToStringArray( returnList);
   
 return returnVal;
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

   String[] returnVal = null;
   List<LexRecord >returnList = getNotThatSimple(pSabs, pTree, pKey);
   returnVal = LexRecord.lexItemsToStringArray( returnList);
      
    
return returnVal;
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
public List<LexRecord> getNotThatSimple( String pTree, String pKey) throws Exception {

    List<LexRecord> returnVals = null;
    try {
      List<LexRecord> candidates = get(pKey);
      if (candidates != null) if (pTree == null) returnVals = candidates;
      else {
        returnVals = new ArrayList<LexRecord>(candidates.size());
        for (LexRecord row : candidates) {
          if (row.getSemanticType().contains(pTree) || row.getTree().contains(pTree))
            returnVals.add(row);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getNotThatSimple", "Issue in getNotThatSimple : " + e.toString());
      throw e;
    }

    return returnVals;
  } // end Method getNotThatSimple() --------

//-----------------------------------------
/**
* getNotThatSimple retrieves a list of terms given a key. Multiple records could be
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
public List<LexRecord> getNotThatSimple(String pSabs, String pTree, String pKey) throws Exception {

 List<LexRecord >returnVals2 = null;
 List<LexRecord> returnVals = getNotThatSimple( pTree, pKey);
    
 if (returnVals != null  && !returnVals.isEmpty()) {
   
   String sabs[] = U.split( pSabs, ":");
   if ( sabs != null && sabs.length > 0 ) {
     returnVals2 = filterToSab( sabs, returnVals);
   }
   else
     returnVals2 = returnVals;
   
 }

return returnVals2;
} // end Method getNotSimple() -------------
 
 

  // =================================================
/**
 * filterToSab filters the rows to rows that only come
 * from the source (sab=source abbreviation) passed in
 * 
 * @param pSab
 * @param pRows
 * @return List<LexRecord>
*/
// =================================================
  private final List<LexRecord> filterToSab(String[] pSabs, List<LexRecord> pRows) {
  
    List<LexRecord> returnVal = null;
    HashSet<LexRecord> buff = new HashSet<LexRecord>( pRows.size());
    for ( LexRecord row : pRows ) {
      String sabs_ = row.getSABs();
      if ( sabs_ != null ) {
        String[] sabs = U.split(sabs_); 
        for ( String aSab : pSabs ) 
          for ( String bSab : sabs )
            if ( bSab.equals( aSab  ) ) 
              buff.add( row);
      }
    }
         
    if ( buff != null && !buff.isEmpty()) {
      returnVal = new ArrayList<LexRecord>( buff.size() );
      for ( LexRecord row : buff )
         returnVal.add( row ); 
    }
    
    return returnVal ;
  } // end Method filterToSab() --------------
  
  
  // -----------------------------------------
  /**
   * get retrieves a list of terms given a key. Multiple records could be
   * returned for a key based on case and part of speech.
   * 
   * @param pKey
   * @param pNumberOfTokens
   * @return List<LexRecord>
   */
  // -----------------------------------------
  @Override
 public List<LexRecord> get(String pKey, int pNumberOfTokens) {
   
   List<LexRecord> returnValue = null;
  try {
    returnValue = get( pKey );
  } catch (Exception e) {
    e.printStackTrace();
    String msg = "Issue with get with key "  + pKey + " " + e.getMessage(); 
    GLog.println(GLog.ERROR_LEVEL, msg );
  }

   return returnValue;
   
 } // end Method get() -----------------------
  
 
 

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

     lexRecords = this.get(key, tokens.length );
  
       
   }
   // if ( lexRecords != null)
   // System.err.println(" found a match");

   return lexRecords;
 } // end Method lookupTerm() -----------------------

 

 
//-----------------------------------------
/**
 * calculateSubTerms looks up the subterms of this term. Note that this is not
 * just the longest span from the left or right of the term. There can be
 * inner terms as well. For example, for a term with tokens a b c d, there can
 * be b c d c d d a b c a b b c <------- b <---
 * 
 * @param pKey
 * @return
 * @throws Exception
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

  if (tokens != null && tokens.length > 1 && buff != null ) {

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
 
 

// -------------------------------------------------------
/**
 * orderByTokenLength returns a list orderd by the number of tokens (most to
 * least)
 * 
 * @param subTermzz
 * @return LexRecord[]
 */
// -------------------------------------------------------
private LexRecord[] orderByTokenLength(List<LexRecord> subTermzz) {

  LexRecord[] termsArray = subTermzz.toArray(new LexRecord[subTermzz.size()]);
  Arrays.sort(termsArray, this.orderByLengthComparator);

  return termsArray;
} // end Method orderByTokenLength() --------

 


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
 * @param pKey
 * @return
 * @throws Exception
 */
// -----------------------------------------
private Hashtable<String, LexRecord> calculateSubTermsAux(String[] tokens)
    throws Exception {

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
 * permuteSingleTerms
 * 
 * @param tokens
 * @return List<LexRecord>
 * @throws Exception
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
 * addSubTerms
 * 
 * @param subTerms
 * @param innerTerms
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

//-------------------------------------------------------
/**
* addSubTerms
* 
* @param subTerms
* @param moreSubTerms
*/
// -------------------------------------------------------
private void addSubTerms(Hashtable<String, LexRecord> subTerms,
   List<LexRecord> moreSubTerms) {

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
 * @param tokens
 * @return List<LexRecord>
 * @throws Exception
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
 * @param tokens
 * @return List<LexRecord>
 * @throws Exception
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
  * index opens the URLs up, reads all the files into one buffer, then indexes
  * them.
  * 
  * @param pLexicalListFiles
  */
 // -----------------------------------------
 private void indexx(String[] pLexicalListFiles) throws Exception {

  
   try {
   if (pLexicalListFiles != null) {
     
     PerformanceMeter meter = new PerformanceMeter();
     meter.begin();

     for (String fileName : pLexicalListFiles) {
       BufferedReader in = null;
       try {
         GLog.println(GLog.STD___LEVEL,"Reading in simple terminology file |" + fileName + "|");
         
         meter.mark("Loading " + fileName);
         
         try {
           in = U.getInputStreamFromFileOrResource ( fileName );
         } catch ( Exception e2) {
          
          
             e2.printStackTrace();
             String msg = "Issue trying to open up file " + fileName + " " + e2.toString();
             throw new Exception(msg);
           }
         
       
         
         String line = null;
         int ctr = 0;
         int cctr = 1;
         while ( (line = in.readLine()) != null ) {
           
           if ( line != null && !line.startsWith("#") && line.length() > 4) {
             
               index(line);
               if ( ctr % 100000 == 0) { 
                 if ( cctr == 80 ) {
                   System.err.print('\n');
                   cctr = 1;
                 }
                 System.err.print("*");
                 // meter.mark("Loaded " + ctr  + " rows from "  + fileName );
               }
               ctr++;
         
           }
         } // end loop through the lines
         in.close();
         String msg = "Finished loading " + ctr + " rows from " + fileName ; 
         meter.mark(msg);
        
       } catch (Exception e) {
         e.printStackTrace();
         GLog.println(GLog.ERROR_LEVEL,"Issue with reading in file " + fileName + " " + e.toString());
         throw e;
       }
     } // end loop thru the local files

     meter.stop("Finished loading files ");
     
   }
   
   }  catch (Exception e) {
     e.printStackTrace();
     String msg = "Issue went wrong with indexing the local terminology " + e.toString();
     GLog.println(GLog.ERROR_LEVEL,msg);
     throw new RuntimeException(msg);
   }

 } // end Method indexx() --------------------

 // -----------------------------------------
 /**
  * index indexes this lragr entry
  * 
  * @param pRow
  * @throws Exception
  */
 // -----------------------------------------
 public void index(String pRow) throws Exception {

 
   
   String cols[] = U.split( pRow);
   
   String key = cols[1];
   
   String[] rows = termIndex.get(key);
   String[] newRows = null;
   if ( rows != null ) {
     newRows = new String[rows.length + 1 ];
     for ( int i = 0; i < rows.length; i++ ) newRows[i] = rows[i];
     newRows[newRows.length -1] = pRow;
    
   } else {
     newRows = new String[1];
     newRows[0] = pRow;
   }
   termIndex.put(key,  newRows);
 
 } // end Method index() -------------------

 
 
 /* (non-Javadoc)
 * @see gov.nih.cc.rmd.framework.utils.terminologies.TermLookupInterface#addTerm(java.lang.String)
 */
@Override
public void addTerm(String pLRAGRTerm) {
  // TODO Auto-generated method stub
  
}



// ---------------------------------------
 // Global Variables
 // ---------------------------------------
 private static Hashtable<String, String[]> termIndex = null;

 private static boolean alreadyLoaded = false;
 
 private TermLookupUtils termLookupUtils = null;
 private OrderByLenthComparator orderByLengthComparator = null;

 
 
} // end Class TermLookupLocalCoreSophiaImpl() -------
