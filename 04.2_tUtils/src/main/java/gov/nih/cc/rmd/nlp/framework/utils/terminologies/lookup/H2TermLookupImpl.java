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
 * H2TermLookupImpl is an alternative to the in-memory
 * lookup that framework currently uses.  It is to 
 * be used in those places where memory is the scarce resource.
 * 
 * This is the h2 implementation - we could'a also
 * done hqldb, mysql, and prostgress implementations
 * Hsqldb is said to be a wee bit faster than h2.
 * 
 * There had been issues with versionitus dependency
 * conflicts with hsql between lvg and ctakes.  
 * 
 * Now that is coming back to me, h2 is how we got
 * around that mess of the dependency conflicts.
 * 
 * This class relies on a two processes
 * 
 *   an indexing process to create files for loading
 *   we can embed the h2 files into a jar for distribution
 *   
 *   a process that does the reading from the the file
 *   to do the queries
 * 
 *
 * @author     Guy Divita
 * @created    Jun 24, 2021
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies.lookup;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.LexRecord;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.OrderByLenthComparator;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.TermLookupUtils;

/**
 * @author Guy
 *
 */
public class H2TermLookupImpl extends TermLookupG {

  // =================================================
  /**
   * Constructor
   *
   * 
  **/
  // =================================================
  public H2TermLookupImpl() throws Exception {
    super();
  }

  // -----------------------------------------
  /**
   * initialize is used for loading and indexing the terminology tables.
   *
   * @param pTerminologyFiles the terminology files
   * @throws Exception the exception
   */
  // -----------------------------------------
  @Override
  public void init(String[] pArgs) throws Exception {
   super.init( pArgs);

  } // end Method init() ---------------------

  // -----------------------------------------
  /**
   * initialize is used for loading and indexing the terminology tables.
   *
   * @param pTerminologyFiles the terminology files
   * @param pNumberOfRecords the number of records
   * @throws Exception the exception
   */
  // -----------------------------------------
  @Override
  public void init(String[] pTerminologyFiles, int pNumberOfRecords) throws Exception {
    if (!alreadyLoaded) {

      this.termLookupUtils = new TermLookupUtils();
      termIndexSingleWord  = new H2TableIndexAndLookup( "termIndexSingleWord" );
      termIndexTwoWord     = new H2TableIndexAndLookup( "termIndexTwoWord" );
      termIndexThreeWord   = new H2TableIndexAndLookup( "termIndexThreeWord" );
      termIndexRestWord    = new H2TableIndexAndLookup( "termIndexRestWord" );

      this.orderByLengthComparator = new OrderByLenthComparator();

      this.indexx(pTerminologyFiles);
      alreadyLoaded = true;
    }

  } // end Method init() --------------------

  /* (non-Javadoc)
   * @see gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermLookupInterface#init(java.lang.String)
   */
  @Override
  public void init(String pUMLSVersion)  {
    // TODO Auto-generated method stub

  }

 //-----------------------------------------
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

 //-----------------------------------------
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

  try {
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
  
  } catch (Exception e) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "get", "Issue retrieving value for key " + pKey + "  :" + e.toString());
  }

  return returnValue;

} // end Method get() -----------------------

  /* (non-Javadoc)
   * @see gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermLookupInterface#addTerm(java.lang.String)
   */
  @Override
  public void addTerm(String pLRAGRTerm) {
    super.addTerm( pLRAGRTerm);

  } // end Method addTerm() ----------------

  /* (non-Javadoc)
   * @see gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermLookupInterface#getSimple(java.lang.String[], java.lang.String)
   */
  @Override
  public String[] getSimple(String[] pTerminologies, String key) throws Exception {
   return getSimple( pTerminologies, key );
  } // end Method getSimple() -------------

  /* (non-Javadoc)
   * @see gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermLookupInterface#getSimple(java.lang.String)
   */
  @Override
  public String[] getSimple(String key) throws Exception {
   return super.getSimple( key);
  } // end Method getSimple() -----------

  /* (non-Javadoc)
   * @see gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermLookupInterface#getNotSimple(java.lang.String, java.lang.String)
   */
  @Override
  public String[] getNotSimple(String tree, String key) throws Exception {
   return super.getNotSimple(tree, key);
  }  // end Method getNotSimple() --------

  /* (non-Javadoc)
   * @see gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermLookupInterface#getNotSimple(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public String[] getNotSimple(String pSabs, String tree, String key) throws Exception {
   return getNotSimple( pSabs, tree, key );
  } // end Method getNotSimple() --------

  /* (non-Javadoc)
   * @see gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermLookupInterface#getNotThatSimple(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public List<LexRecord> getNotThatSimple(String sab, String tree, String key) throws Exception {
     return getNotThatSimple( sab, tree, key);
  } // end Method notThatSimple() -------

  /* (non-Javadoc)
   * @see gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermLookupInterface#getNotThatSimple(java.lang.String, java.lang.String)
   */
  @Override
  public List<LexRecord> getNotThatSimple(String tree, String key) throws Exception {
    return getNotThatSimple(  tree, key);
   } // end Method notThatSimple() -------
  

  
  /* (non-Javadoc)
   * @see gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermLookupInterface#init(java.lang.String[], java.lang.String[])
   */
  @Override
  public void init(String[] pArgs, String[] pTerminologyFiles) throws Exception {
    init(  pTerminologyFiles, 1000);

  }

  
  // -----------------------------------------
  /**
   * index opens the URLs up, reads all the files into one buffer, then indexes
   * them.
   *
   * @param pLexicalListFiles the lexical list files
   * @throws Exception the exception
   */
  // -----------------------------------------
  @Override
  protected void indexx(String[] pLexicalListFiles) throws Exception {
  
 // StringBuilder buff = new StringBuilder();
    
    PerformanceMeter m = new PerformanceMeter();
    m.begin(" Starting the hash part of the index ");
    ConcurrentHashMap<String,Thread> activeThreads  = new ConcurrentHashMap<String,Thread>( pLexicalListFiles.length );
    try {
     if (pLexicalListFiles != null) {

        for (String fileName : pLexicalListFiles) {
          
          H2TermLookupAux h2TermIndexAux =  new H2TermLookupAux( fileName , this);
           Thread termIndexThread  = new Thread(h2TermIndexAux );
           activeThreads.put(fileName, termIndexThread);
           termIndexThread.start() ;
          //
          // calls h2TermIndexAux.index( fileName) inside a new thread
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

  
//-----------------------------------------
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
 
//=================================================
/**
 * syncronizedTermIndexSingleWordPut 
 * 
 * @param key
 * @param records
*/
// =================================================
 @Override
 protected synchronized void syncronizedTermIndexSingleWordPut(String key, List<LexRecord> records) {
 
   try {
     termIndexSingleWord.put(key, records);
   } catch (Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "syncronizedTermIndexSingleWordPut", "Issue with putting this term in " + key + " :" + e.toString());
     
   }
}  // end Method syncronizedTermIndexSingleWordPut() --------- 
 
// =================================================
/**
 * syncronizedTermIndexTwoWordPut 
 * 
 * @param key
 * @param records
*/
// =================================================
 @Override
 protected synchronized void syncronizedTermIndexTwoWordPut(String key, List<LexRecord> records) {
  try {
    termIndexTwoWord.put(key, records);
  } catch (Exception e) {
   e.printStackTrace();
   GLog.println(GLog.ERROR_LEVEL, this.getClass(), "syncronizedTermIndexTwoWordPut", "Issue with putting this term in " + key + " :" + e.toString());
   
  }
  
}  // end Method syncronizedTermIndexTwoWordPut() --------- 
 
// =================================================
/**
 * syncronizedTermIndexThreeWordPut 
 * 
 * @param key
 * @param records
*/
// =================================================
 @Override
 protected synchronized void syncronizedTermIndexThreeWordPut(String key, List<LexRecord> records) {
 
   try {
   termIndexThreeWord.put(key, records);
   } catch (Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "syncronizedTermIndexThreeWordPut", "Issue with putting this term in " + key + " :" + e.toString());
     
   }
  
}  // end Method syncronizedTermIndexThreeWordPut() --------- 
 
 
 // =================================================
 /**
  * syncronizedTermIndexSingleWordPut 
  * 
  * @param key
  * @param records
 */
 // =================================================
 @Override
  protected synchronized void syncronizedtermIndexRestWordPut(String key, List<LexRecord> records) {
   
   try {
     termIndexRestWord.put(key, records);
   } catch (Exception e) {
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "syncronizedtermIndexRestWordPut", "Issue with putting this term in " + key + " :" + e.toString());
     
   }
 }  // end Method syncronizedtermIndexRestWordPut() --------- 
  
 
 
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
   
   

 } // end Method calculateLookAhead() ------------------------
 
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
 
//-----------------------------------------
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
 @Override
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
  
  // =================================================
  /**
   * main 
   * 
   * @param args
  */
  // =================================================
  public static void main(String[] args) {
    
    try {
      
      // index the lragr files
      
      
      
      // save the h2 db
      
      // load the h2 db
      
      // make some queries from the h2 db
      
      
    } catch ( Exception e) {
      e.printStackTrace();
      GLog.error_println("Issue with H2 term lookup test " + e.toString());
    }

  } // end Method main() ------------------------------
  
  
  // ----------------------------
  // Global Variables
  // ---------------------------
  H2TableIndexAndLookup termIndexSingleWord  = null;
  H2TableIndexAndLookup termIndexTwoWord     = null;
  H2TableIndexAndLookup termIndexThreeWord   = null;
  H2TableIndexAndLookup termIndexRestWord    = null;

} // end Class H2TermLookupImpl() ---------------------
