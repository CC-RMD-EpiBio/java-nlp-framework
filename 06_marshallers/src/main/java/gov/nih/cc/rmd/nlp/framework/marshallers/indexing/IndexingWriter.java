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
 * IndexingWriter writes out instances of key annotations
 * into the tables needed for full text indexing.
 *  
 *  These are going into an h2 database called fullTextIndexes
 *  
 *  The tables include 
 *     keyDocId = key|docId
 *     KEYTERMFREQs = key|termFrequency|numberOfDocs
 *     
 *        
 * @author  Guy Divita 
 * @created Doc 1, 2015
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.indexing;


import gov.va.chir.model.DocumentHeader;
import gov.va.vinci.model.Key;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;


public class IndexingWriter extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter  {
 
  // =======================================================
  /**
   * Constructor IndexingWriter 
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public IndexingWriter(String[] pArgs) throws ResourceInitializationException {
    initialize( pArgs);
  }

  // -----------------------------------------
  /**
   * process 
   *   writes out the instances to a file, and accumulates
   *   stats for each file until the proces is done.
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    this.performanceMeter.startCounter();
    this.inProcess = true;
    try {
     
    List<Annotation> keys = UIMAUtil.getAnnotations(pJCas, Key.typeIndexID);
    long documentId = Long.parseLong(  getNumber( VUIMAUtil.getDocumentId(pJCas)));
    HashMap<String, Boolean> keyHash = new HashMap<String, Boolean>(1000000);
   
    if ( keys != null ) {
      for ( Annotation key: keys ) {
      
        String    assertionStatus = ((Key)key).getAssertionStatus();
        String      subjectStatus = ((Key)key).getSubjectStatus();
        boolean conditionalStatus = ((Key)key).getConditionalStatus();
      
        if ( !assertionStatus.equals( "Asserted") )  continue;
        if ( !subjectStatus.equals("Patient"))       continue;
        if ( conditionalStatus )                     continue;
      
        String aKey = ((Key)key).getKey() ;
        if ( aKey.length() > MAX_KEY_SIZE )  aKey = aKey.substring(0,MAX_KEY_SIZE);
        synchronized(this) {// ---------------------------
          this.inProcess = true;
          int[] freq = STATIC_KEYHASH.get(aKey);
          if ( freq == null ) {
            freq = new int[2];
            freq[KEYTERMFREQ] = 0;
            freq[KEYDOCFREQ] = 0;
          }
          
          freq[KEYTERMFREQ]++;
          STATIC_KEYHASH.put(aKey, freq);   //termFreq freq
          
          if ( STATIC_KEYHASH.size() > sizeOfKeyHash ){
            sizeOfKeyHash = STATIC_KEYHASH.size();
         //   System.err.print("The keyhash is growing " + sizeOfKeyHash + " just added " + aKey + " with " + this.serverName + "|" + this.inProcess + "\n");
          }
          
          boolean keyInTitle = ((Key)key).getInTitle(); 
          Boolean inTitle = keyHash.get(aKey);
          if ( inTitle == null ) {
            inTitle = new Boolean(keyInTitle);
            keyHash.put(aKey, inTitle);
          }
          if ( keyInTitle ) { 
            inTitle = true;
            keyHash.put(aKey, inTitle);
          }
          
          
          
        } // end synchronized block ------------------
      } // end loop thru the keys of the document
    
    // iterate thru the unique keys of this record to index key|docId  and the docFreq
    
      try {
        
        Set<String> keySet = keyHash.keySet();
        for ( String aKey: keySet ) {
          boolean inTitle = keyHash.get(aKey);
          addToTables(aKey, documentId, inTitle);
        }
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue in process IndexingWriter " + e.toString());
      }

    }
    } catch (Exception e) {
 //     e.printStackTrace();
 //     System.err.println("Issue in process x IndexingWriter - some of these can be ignored. Not throwing an exception here " + e.toString());
    }
    this.performanceMeter.stopCounter();
    this.inProcess = false;
   
    
  } // end Method process() ----------------

  // ==========================================
  /**
   * getNumber saves just the number part of a string
   *
   * @param pString
   * @return String
   */
  // ==========================================
  private String getNumber(String pString) {
    
    StringBuffer buff = new StringBuffer();
    
    char[] inBuff = pString.toCharArray();
    
    for (int i = 0; i < inBuff.length; i++) 
      if ( U.isNumber( inBuff[i]))
          buff.append(inBuff[i]);
    
    
    return buff.toString();
    // end Method getNumber() ========================================
  }

  // -----------------------------------------
  /** 
   * addToTables
   * the tables
   * 
   * @param pKey          This assumes it's already been truncated to the MAX_KEY_SIZE
   * @param pDocumentId
   * @param pInTitle        this key was found in the title of the document
   * @throws SQLException 
   *
   */
  // -----------------------------------------
  private void addToTables(String pKey,  long pDocumentId, boolean pInTitle) throws SQLException {
   
    try {
    static_insertIntoKeyDocIdPreparedStatement.setLong  (1, pDocumentId);
    static_insertIntoKeyDocIdPreparedStatement.setString(2, pKey);
    static_insertIntoKeyDocIdPreparedStatement.setBoolean(3,pInTitle);
   
    static_insertIntoKeyDocIdPreparedStatement.execute();
   
    updateKEYTERMFREQsTable( pKey );
    } catch (Exception e) {}
    
   
  } // End Method addToTables() ======================
  

  // -----------------------------------------
  /** 
   * updateKeyDocFreqTable
   * 
   * @param pKey This assumes it's already been truncated to the MAX_KEY_SIZE
   * @throws SQLException 
   * 
   *
   */
  // -----------------------------------------
  private synchronized void updateKEYTERMFREQsTable(String pKey) throws SQLException {
    
  
    
    int[] freqs = STATIC_KEYHASH.get( pKey);
    if ( freqs == null ) {         // ------------------------
      freqs = new int[2];          // This should never happen
      freqs[KEYDOCFREQ] = 0;       //
      freqs[KEYTERMFREQ] = 0;          // ------------------------
    }
    freqs[KEYDOCFREQ]++;
   
  } // End Method updateKeyDocFreqTable() ======================
  


  // -----------------------------------------
  /** 
   * destroy closes the open instance freq table
   *         and writes out a summary table
   * 
   *
   */
  // -----------------------------------------
  @Override
  public synchronized void destroy() {
     
   if ( am_I_TheLastInstance() && !destroyed ) { 
      destroyLast();
    } 
  
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
  
  } // end Method Destroy() -----------------

 


  // ==========================================
  /**
   * destroyLast is called when its the last destroy
   * called in a multi thread env.  This method
   * pushes the keys to the database. 
   *
   */
  // ==========================================
  private synchronized void destroyLast() {
    destroyed = true;
    try {
      System.err.print("==============================================\n");
      System.err.print(" Destroy last on " + this.serverName + " writing the hash to the database now\n");
      System.err.print(" The number of keys are " + STATIC_KEYHASH.size()  + "\n");
      System.err.print("==============================================\n" );
      
      
      while ( this.inProcess ) {  // <--- needed to allow this instance of processing to finish the last record before proceeding
        System.err.println("not dead yet have more processing to do for a record , please wait ");
        Thread.sleep(10);
      }
      System.err.println("No body should be in the process for this application " + this.inProcess);
      
     // Thread.sleep(6000 * instanceCount); //<----- insurance policy to make sure every other instance is really finished 
      
      Set<String> keys = STATIC_KEYHASH.keySet();
      int ctr = 0;
      for ( String key: keys ) {
        int freqs[] = STATIC_KEYHASH.get( key);   // freq|docFreq
       //  System.err.println("|" + key + "|" + freqs[KEYTERMFREQ] + "|" + freqs[KEYDOCFREQ]);
         static_insertIntoKEYTERMFREQPreparedStatement.setString(1, key);
        static_insertIntoKEYTERMFREQPreparedStatement.setLong(2,  freqs[KEYTERMFREQ] );     // term freq
        static_insertIntoKEYTERMFREQPreparedStatement.setLong(3,  freqs[KEYDOCFREQ] );  // doc freq
        static_insertIntoKEYTERMFREQPreparedStatement.execute();
        ctr++;
      } // end Loop thru keys 
    
      System.err.print("==============================================\n");
      System.err.print(" Cycled thru " + ctr + " keys \n");
      System.err.print("==============================================\n" );
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with inserting into hash :" + e.toString());
     
    }
    
        
  } // end Method destroyLast() ========================================
  

  // ==========================================
  /**
   * am_I_TheLastInstance returns true if I am the last alive instance of this class
   *
   * @return boolean
   */
  // ==========================================
  private synchronized boolean am_I_TheLastInstance() {
  
    boolean returnFlag = false;
    
  //  System.err.println("Removing instance " + this.serverName);
   if ( !staticInstanceStack.remove( this.serverName) )
    System.err.println("just tried to remove an instance that isn't on the list ");
    
   if ( staticInstanceStack.isEmpty())
     returnFlag = true;
    
    return returnFlag;
  } // end Method am_I_TheLastInstance() ========================================
  

  //----------------------------------
  /**
   * initialize 
   * 
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
  
    
    String          outputDir = U.getOption(pArgs, "--outputDir=", "./");
    String vlmineDatabaseName = U.getOption(pArgs, "--vlmineDatabasename=", "vlmineFullTextIndexes");
    this.serverName           = U.getOption(pArgs, "--servername=", "defaultServer");
    
    boolean addToDatabase = Boolean.valueOf( U.getOption(pArgs,  "--addToDatabase=", "false"));
    
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
   
    initialize(  outputDir, vlmineDatabaseName, addToDatabase, this.serverName   );
    
   
  } // end Method initialize() -------
  
  
  
  //----------------------------------
  /**
   * initialize 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
  
    String[]    args = (String[])aContext.getConfigParameterValue("args");
    
    initialize(args);
    
   
  } // end Method initialize() -------
  
  
  // ------------------------------------------
  /**
   * initialize called from another mechanism besides a classic writer, like within the knowtator listener.
   * 
   * @param pOutputDir
   */
  // ------------------------------------------
  public void initialize( String pOutputDir, String pDatabaseName, boolean pAddToDatabase, String pServerName )  throws ResourceInitializationException {
 
    this.outputDir = pOutputDir;
    this.serverName = pServerName;
    try {
      U.mkDir(pOutputDir);
    } catch (Exception e1) {
      e1.getStackTrace();
      String msg = "Issue with making the stats dir \n" + e1.getMessage() ;
      System.err.println( msg );
      throw new ResourceInitializationException ();
    }
   
    if ( static_connection == null ) {
      static_connection = initializeDatabase(this.outputDir, pDatabaseName, pAddToDatabase);
    }
  
    if ( STATIC_KEYHASH == null  )
     STATIC_KEYHASH = Collections.synchronizedMap(new HashMap<String, int[]>(100000000));  // key|freq|docFreq
    
   staticInstanceStack.add( pServerName );
   instanceCount++;
    
  }  // End Method initialize() -----------------------
  
  


// =======================================================
  /**
   * initializeDatabase 
   * 
   * @return
   */
  // =======================================================
  private static Connection initializeDatabase(String pOutputDir, String pDatabaseName, boolean pAddToDatabase ) throws ResourceInitializationException{
    try {

      String    databaseName = pOutputDir + "/" + pDatabaseName;
     
      static_connection = connect2H2_Database(databaseName );

      if ( !pAddToDatabase ) {
        createKeyDocIdTable( static_connection );
        createKEYTERMFREQsTable(  static_connection);
         }
      
      // create the prepared statements
      
      static_insertIntoKeyDocIdPreparedStatement = createInsertIntoKeyDocIdPreparedStatement(static_connection);
      static_insertIntoKEYTERMFREQPreparedStatement  = createInsertIntoKEYTERMFREQsPreparedStatement(static_connection);
    
      
    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue with the database stuff " + e.toString());
      }
  
    return static_connection;
  } // End Method initializeDatabase() ======================
  

//=======================================================
 /**
  * close closes the H2 database
  * 
  */
 // =======================================================
 public static void close( ) {
   try {

   if ( static_connection != null && static_connection.isClosed() ) 
     static_connection.close();
   
    static_connection = null;
   
   } catch (Exception e) {
       e.printStackTrace();
       System.err.println("Issue with closing the database " + e.toString());
     }
 
   
 } // End Method close() ================================

//=======================================================
 /**
  * createKeyDocIdTable
  * 
  * @param pConnection 
  */
 // =======================================================
 private static void createKeyDocIdTable( Connection pConnection) throws ResourceInitializationException{
 
   try {
  
   Statement stat = pConnection.createStatement();
   
   stat.execute("create table keyDocId  (" +             
    "documentSID           varchar(20),  " +
    "key                   varchar(" + MAX_KEY_SIZE + "), " +
    "inTitle               boolean )");  
  
  
   stat.execute("create INDEX keyDocID_key ON keyDocId (key)");
   stat.execute("CREATE UNIQUE INDEX keyDocId_2 ON keyDocId(documentSID, KEY, inTitle)");
   stat.close();
   
          
   } catch (Exception e) {
     e.printStackTrace();
     System.err.println( "Issue creating table keyDocId " + e.toString());
     throw new ResourceInitializationException();
   }
  
 }  // End Method createKeyDocIdTable() ======================
 

//=======================================================
/**
 * createKEYTERMFREQsTable
 * 
 * @param pConnection
 * 
 */
// =======================================================
private static void createKEYTERMFREQsTable( Connection pConnection) throws ResourceInitializationException{

  try {
 
  Statement stat = pConnection.createStatement();
  
  stat.execute("create table KEYTERMFREQs  (" +             
      "key                   varchar(" + MAX_KEY_SIZE + ")," + 
      "termFreq              long,  " +
      "docFreq               long  " +
      ") ");  
 
 
  // stat.execute("create INDEX KEYTERMFREQs_key ON KEYTERMFREQs (key)");
   stat.close();
         
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println( "Issue creating the h2 datatabase table KEYTERMFREQs " + e.toString());
    throw new ResourceInitializationException();
  }
 
}  // End Method createKEYTERMFREQsTable() ======================





 // =======================================================
 /**
  * createInsertIntoKeyDocIdPreparedStatement
  * 
  * @param  pConnection
  * @return PreparedStatement
  * @throws SQLException 
  */
 // =======================================================
 private static PreparedStatement createInsertIntoKeyDocIdPreparedStatement(Connection pConnection ) throws SQLException {
   
   String InsertQuery = "INSERT INTO keyDocId ("
       + "documentSID, "             // 1
       + "key, "                     // 2
       + "inTitle "                  // 3
       + ") values" + "(?,?,?)";
   
   
    PreparedStatement insertPreparedStatement = pConnection.prepareStatement(InsertQuery);
   
   return insertPreparedStatement;
 
 } // End Method createH2InsertPreparedStatement() ======================
 

//=======================================================
/**
* createInsertIntoKeyDocIdPreparedStatement
* 
* @param pConnection
* @return PreparedStatement
* @throws SQLException 
*/
// =======================================================
private static PreparedStatement createInsertIntoKEYTERMFREQsPreparedStatement(Connection pConnection ) throws SQLException {
 
 
  String updateOrInsertStatement = "INSERT INTO KEYTERMFREQs (KEY, TERMFREQ, DOCFREQ ) VALUES( ?, ?, ? )";
  
  PreparedStatement insertPreparedStatement = pConnection.prepareStatement(updateOrInsertStatement);
 
 return insertPreparedStatement;

} // End Method createInsertIntoKEYTERMFREQsPreparedStatement() ======================





  // =======================================================
  /**
   * createH2_DatabaseTable 
   * 
   * @param pOutputDatabaseName
   * @param pTableName
   * @param maxRows 
   * @return Connection
   * 
   */
  // =======================================================
  private static Connection connect2H2_Database(String pH2DatabaseName) {
  
    Connection conn = null;
    try {
      @SuppressWarnings("unused")
      org.h2.Driver t = null;  // <----- here to make sure this is a complile error if not found.
    Class.forName("org.h2.Driver");
    conn = DriverManager.getConnection("jdbc:h2:" + pH2DatabaseName   );
    Logger tmp_logger = java.util.logging.Logger.getLogger("h2database");
    tmp_logger.setLevel(Level.OFF);
    
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println( "Issue creating the h2 datatabase");
    }
    
    return conn;
  }  // End Method createH2_DatabaseTable() ======================
  


  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  public String serverName = "defaultServer";
  private boolean inProcess = false;
  public static final char CSV_CHAR_DELIMITER = ',';
  public static final String CSV_DELIMETER = String.valueOf(CSV_CHAR_DELIMITER);  // could be a pipe
   private static final String NOT_CSV_DELIMETER = "<COMMA>";   
  
   public static final int MAX_KEY_SIZE = 100;
  private static int numberOfDocuments = 0;  // <--- needed to assign document ids if they dont exist
   private String outputDir = null;
   private static Connection static_connection = null;
   private static PreparedStatement static_insertIntoKeyDocIdPreparedStatement;
  
   private static PreparedStatement static_insertIntoKEYTERMFREQPreparedStatement = null;
   private static int sizeOfKeyHash = 100;
  
   private static Map<String,int[]> STATIC_KEYHASH = null; 
   private static int  instanceCount = 0;
   private static boolean destroyed = false;
  
   private static  HashSet<String> staticInstanceStack = new HashSet<String>();
   private static final int KEYTERMFREQ = 0;
   private static final int KEYDOCFREQ  = 1; 
   private ProfilePerformanceMeter              performanceMeter = null;
   

 
} // end Class MetaMapClient() ---------------
