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
 * H2TableIndexAndLookup.java  Summary [TBD]
 *
 * @author     Guy Divita
 * @created    Jun 25, 2021
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies.lookup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.sql.SQLException;
import java.sql.Statement;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.LexRecord;

/**
 * @author Guy
 *
 */
public class H2TableIndexAndLookup {
  
  
 

  
  // =================================================
  /**
   * Constructor
   *
   * @param pTableName
   * @throws SQLException 
   * 
  **/
  // =================================================
  public H2TableIndexAndLookup(String pArgs[] ) throws SQLException {
     
    initialize( pArgs );
  } // end Constructor() ------------------------------


  // =================================================
  /**
   * Constructor
   *
   * @param pTableName
   * @throws SQLException 
   * 
  **/
  // =================================================
  public H2TableIndexAndLookup(String pTableName) throws SQLException {
    String[] args = null;
    this.tableName = pTableName;
    initialize( args );
  } // end Constructor() ------------------------------

  // =================================================
  /**
   * initialize 
   *      The args on pArgs should be
   *      inputDir=
   *      databaseName=
   *      year=
   *      extension=
   *       
   * @param pArgs
   * @throws SQLException 
  */
  // =================================================
  public final void initialize(String[] pArgs) throws SQLException {
    
    
   
      this.h2Instance = new H2JDBCUtils( pArgs );
     
      
      // set the connection
      this.connection = this.h2Instance.getConnection();
      
     
    
  } // end Method initialize() -----------------------

  // =================================================
  /**
   * prepareStatements creates the prepared statements 
   * for later use
   * @param pTableName
   * @throws SQLException 
   * 
   * 
  */
  // =================================================
 public final void prepareStatements( String pTableName) throws SQLException {

    // create any prepared statements for later use;
    String insertStatement  = createInsertSQLStatement ( pTableName );
    String selectStatement = createSelectStatement( pTableName);
    String selectKeysStatement = createSelectKeysStatement( pTableName);
    this.insertPreparedStatement = this.connection.prepareStatement(insertStatement) ;
    this.queryPreparedStatement = this.connection.prepareStatement(selectStatement );
    this.queryKeysPreparedStatement = this.connection.prepareStatement(selectKeysStatement);
    
    
    
  } // end Method prepareStatements() ----------------


  // =================================================
  /**
   * createDatabase 
   * 
   * @param aFileName
   * @deprecated   in h2, the name of the file is the database.
   *               if you try to open a file that does not exist
   *               h2 will create it - creating the database.
  */
  // =================================================
  public final synchronized void createDatabase(String aFileName) {
   
    
  } // end Method creteDatabase() --------------------


  // =================================================
    /**
     * createTable 
     * 
    */
    // =================================================
   public final void createTable( String pTableName) {
  
     
     String createTableSQL = createTableSQLStatement( pTableName );
     GLog.println( GLog.INFO_LEVEL, this.getClass(), "createTable", createTableSQL);
     // Step 1: Establishing a Connection
     try {
       // Connection connection = this.h2Instance.getConnection();
         // Step 2:Create a statement using connection object
       Statement statement = this.connection.createStatement();
  
         // Step 3: Execute the query or update query
       boolean status = statement.execute(createTableSQL);
       System.err.println("status = " + status );
  
     } catch (SQLException e) {
         // print SQL exception information
         this.h2Instance.printSQLException(e);
     }
  } // end Method createTable() ------------------------


    // =================================================
    /**
     * getConnection 
     * 
     * @return Connection
     * @throws SQLException 
    */
    // =================================================
   public final Connection getConnection() throws SQLException {
      return this.h2Instance.getConnection();
    } // end Method getConnection() ---------------------


  // =================================================
  /**
   * get returns a list of LexRecord - one for each 
   * lragr row retrieved from the db
   * 
   * @param pKey
   * @return
   * @throws SQLException 
  */
  // =================================================
  public final List<LexRecord> get(String pKey) throws SQLException {
   
    List<String> rows = getTableRowsForKey( pKey );
    
    List<LexRecord> returnVal = new ArrayList<LexRecord>(); 
    if ( rows != null && !rows.isEmpty())
      for ( String row: rows ) {
       LexRecord buff = LexRecord.parseLRAGR(row);
       returnVal.add( buff );
      }
    return returnVal;
  } // end Method get() ------------------------------

  // =================================================
  /**
   * put 
   * 
   * @param pKey
   * @param pRecords
   * @throws Exception
  */
  // =================================================
  public final void put(String pKey, List<LexRecord> pRecords) throws Exception {
   
    String[] rows = null;
    if ( pRecords != null && pRecords.isEmpty() )
      rows = LexRecord.lexItemsToStringArray(pRecords);
    
    if ( rows != null && rows.length > 0 )
      for ( String row: rows )
        insertRecord( pKey, row );
      
  } // end Method put() ------------------------------

  // =================================================
  /**
   * keys returns a list of keys
   * 
   * @return 
   * @throws SQLException 
  */
  // =================================================
   public final Enumeration<String> keys() throws SQLException {
     
     
     List<String> buff = new ArrayList<String>();
     try {
       ResultSet rs = this.queryKeysPreparedStatement.executeQuery();
       while (rs.next()) { 
          String row = rs.getString("value");
          if ( row != null )
            buff.add( row);
       }
       
       } catch (SQLException e) {
         this.h2Instance.printSQLException(e);
         throw e;
       }
       
     Enumeration<String> returnVal = Collections.enumeration(buff);
      return returnVal;
     
    
  } // end Method keys() -----------------------------

  // =================================================
    /**
     * commit 
     * @throws SQLException 
     * 
    */
    // =================================================
   public final void commit() throws SQLException {
      this.h2Instance.getConnection().commit();
      
    } // end Method commit() ---------------------------


  // =================================================
  /**
   * close 
   * @throws Exception 
   * 
  */
  // =================================================
  public final void close() throws Exception {
    this.h2Instance.close();
    
  } // end Method Close() ----------------------------


  // =================================================
    /**
     * createIndexOnOneWordTable
     * @param pTableName
     * 
    */
    // =================================================
  public final void createIndexOnTable( String pTableName) {
    
    
    try {
      // Connection connection = this.h2Instance.getConnection();
        // Step 2:Create a statement using connection object
      Statement statement = this.connection.createStatement();
      String indexOnTableSQL = createIndexOnTableSQLStatement( pTableName); 
      
      boolean status = statement.execute(indexOnTableSQL);
      System.err.println("status = " + status );
    } catch (SQLException e) {
      // print SQL exception information
      this.h2Instance.printSQLException(e);
  }
    
      
    } // end Method createIndexOnOneWordTable() -----


  // =================================================
  /**
   * getTableRowsForKey 
   * 
   * @param string
   * @return
   * @throws SQLException 
  */
  // =================================================
 public final List<String> getTableRowsForKey(String pQueryKey) throws SQLException {
   
   List<String> returnVal = new ArrayList<String>();
   try {
     // Connection connection = this.h2Instance.getConnection();
     
     // Step 2:Create a statement using connection object
    //  PreparedStatement preparedStatement = this.connection.prepareStatement(QUERY_SophiaOneWordIndexTable_SQL);
     this.queryPreparedStatement.setString(1, pQueryKey);
     System.out.println(this.queryPreparedStatement);
     // Step 3: Execute the query or update query
     ResultSet rs = this.queryPreparedStatement.executeQuery();

     // Step 4: Process the ResultSet object.
     while (rs.next()) { 
        String row = rs.getString("value");
        if ( row != null )
          returnVal.add( row);
     }
     
     } catch (SQLException e) {
       this.h2Instance.printSQLException(e);
       throw e;
     }
     
    return returnVal;
  } // end Method getTableRowsForKey() ---------------


  // =================================================
  /**
   * insertRecord 
   * @param pKey
   * @param pValue
  */
  // =================================================
  public final void insertRecord(String pKey, String pValue) {
  
    
    
    try {
        this.insertPreparedStatement.setString(1, pKey);
        this.insertPreparedStatement.setString(2, pValue);
     
  
        GLog.println( GLog.INFO_LEVEL, this.getClass(), "insertRecord", "Sending the statement " + this.insertPreparedStatement.toString());
      
        int status = this.insertPreparedStatement.executeUpdate();
        
       //  this.commit();
       
       System.err.println("status =" + status );
    } catch (SQLException e) {
  
        // print SQL exception information
        this.h2Instance.printSQLException(e);
    }
  
    // Step 4: try-with-resource statement will auto close the connection.
  
    
  } // end Method insertRecord() ---------------------


  // ------------------------------------------
  /**
   * setArgs
   * 
   * 
   * @return
   */
  // ------------------------------------------
  public final static String[] setArgs(String pArgs[]) {

    // -------------------------------------
    String    inputDir  = U.getOption(pArgs, "--inputDir=", "/00_legacy/04.2_tUtils/src/resources/sophiaH2Indexes");
    String         year = U.getOption(pArgs,  "--year=", "2020");
    String    tableName = "OneWordIndex";
    String databaseName = U.getOption(pArgs, "--databaseName=", "Sophia_" + tableName + "_"+ year);
    String       logDir = U.getOption(pArgs, "--logDir=",     "./logs" ); 
    String   printToLog = U.getOption(pArgs, "--printToLog=", "true");
    String   printToConsole = U.getOption(pArgs, "--printToConsole=", "true");
    String profilePerformanceLogging = U.getOption(pArgs,  "--profilePerformanceLogging=", "false");
    
    String    extension = U.getOption(pArgs, "--extension=", ".h2" );
    String      jdbcURL = U.getOption(pArgs,  "--jdbcURL=", "jdbc:h2:file:" + inputDir + "/" + databaseName + "_"  + extension ); 
    String jdbcUsername = U.getOption(pArgs, "--jdbcUserName=",  "sa");
    String jdbcPassword = U.getOption(pArgs,  "--jdbcPassword=", "sa");
    String useH2JarFile = U.getOption(pArgs,  "--useH2JarFile=", "true"  );
  
    String args[] = {
        
        "--inputDir=" + inputDir,
        "--databaseName=" + databaseName,
        "--logDir=" + logDir,
        "--printToLog=" + printToLog,
        "--profilePerformanceLogging=" + profilePerformanceLogging,
        "--printToConsole=" + printToConsole,
        
        "--jdbcURL=" + jdbcURL,
        "--extension=" + extension,
        "--jdbcUserName=" + jdbcUsername,
        "--jdbcPassword=" + jdbcPassword,
        "--year=" + year, 
        "--useH2JarFile=" + useH2JarFile
      
       
    };

     // need a help option here 
    // This method assumes that there is a resources/CS_01_CovidSymptomsTrainingFromVTTApplication.txt
    Use.usageAndExitIfHelp( "H2TableIndexAndLookup", pArgs, args ) ;
  
    return args;

  }  // End Method setArgs() -----------------------

  


 
  
// =================================================
  /**
   * createTableSQLStatement 
   * 
   * @param pTableName
   * @return String
  */
  // =================================================
  private final String createTableSQLStatement(String pTableName) {
       String returnVal =  
        "create table if not exists " + pTableName + " (\n" + 
                           "      key   varchar(50)  ,\n" +
                           "      value varchar(400)\n" +  
                           "  );";
       
       
      
       return returnVal;
  } // end Method createTableSQLStatement() -------






// =================================================
/**
 * createIndexOnTableSQLStatement 
 * 
 * @param pTableName
 * @return String
*/
// =================================================
private final String createIndexOnTableSQLStatement(String pTableName) {

  String returnVal =  "CREATE INDEX keyIndex ON " + pTableName + " (key);";

return returnVal;

} // end Method createIndexOnTableSQLStatement() -----------


//=================================================
/**
* createInsertSQLStatement 
* 
* @param pTableName
* @return String
*/
//=================================================
private final String createInsertSQLStatement(String pTableName) {


    String returnVal = "INSERT INTO "  + pTableName + 
      "  (key, value) VALUES " +
      " (?, ?);";
  
return returnVal;

} // end Method createInsertSQLStatement() -----------



//=================================================
/**
* createSelectStatement 
* 
* @param pTableName
* @return String
*/
//=================================================
private final String createSelectStatement(String pTableName) {

  String returnVal = "select value from  " + pTableName + " where key =?";
 
return returnVal;

} // end Method createSelectStatement() -----------

//=================================================
/**
* createSelectKeysStatement 
* 
* @param pTableName
* @return String
*/
//=================================================
private final String createSelectKeysStatement(String pTableName) {
  String returnVal = "select key from " + pTableName ;

return returnVal;

} // end Method createSelectStatement() -----------




//---------------------------
 // Global Variables
 // ---------------------------
 
 // =================================================
/**
 * main 
 * @param pArgs
*/
// =================================================
public static void main(String[] pArgs) {

  H2TableIndexAndLookup h2Instance = null;
  
  try {
  String[] args = setArgs( pArgs);
  String    inputDir  = U.getOption(args, "--inputDir=", "/00_legacy/04.2_tUtils/src/resources/sophiaH2Indexes");
  String         year = U.getOption(args,  "--year=", "2020");
  String    extension = U.getOption(args, "--extension=", ".h2" );
  String    tableName = "OneWord";
  String databaseName = U.getOption(args, "--databaseName=", "Sophia_" + tableName + "_"+ year ); // no extension
  boolean useH2JarFile = Boolean.parseBoolean( U.getOption(pArgs,  "--useH2JarFile=", "true" ) );
  
  String aFileName = inputDir + "/" + databaseName + extension ;
 
  
  if ( !useH2JarFile ) {
    h2Instance = new H2TableIndexAndLookup( args );
    h2Instance.createDatabase( aFileName );
  
 
    h2Instance.createTable( tableName);
    h2Instance.createIndexOnTable( tableName);
  
    h2Instance.prepareStatements( tableName );
  
    h2Instance.insertRecord("sleep", sleepLRAGRRow);
    h2Instance.insertRecord("gait", gaitLRAGRRow);
    h2Instance.insertRecord("depression", depression1);
    h2Instance.insertRecord("depression", depression2);
    h2Instance.insertRecord("depression", depression3);
    h2Instance.commit();
  
   
  List<String> result = h2Instance.getTableRowsForKey( "depression");
  h2Instance.close();
  h2Instance = null;
  
  }

  h2Instance = new H2TableIndexAndLookup( args );
  h2Instance.prepareStatements(  tableName);
  
  List<String> result2 = h2Instance.getTableRowsForKey( "sleep"); // let's hope we get the same results
  if ( result2 != null && !result2.isEmpty())
    for ( String row : result2)
      System.err.println( row );
  
  List<String> result3 = h2Instance.getTableRowsForKey( "gait"); // let's hope we get the same results
  if ( result3 != null && !result3.isEmpty())
    for ( String row : result3)
      System.err.println( row );
  
  if ( h2Instance.getConnection() != null )
    h2Instance.close();
  else
    System.err.println("This should not be closed at this point!");
  
  
  // -------------------------------
  //  _done_  put the db into a jar file 
  //  _done_  create a dependency for it
  //  _done_  Access via reading a resource
  //  _done_  and have it be opened read only 
  //  ______  build the sophia tables [TBD]
  //  ______  incorporate this into the existing code base [TBD]
  //  ______  benchmark performance against the memory version [TBD] 
  // -------------------------------
  
  


  
  } catch (Exception e) {
    e.printStackTrace();
    GLog.error_println("Issue with H2TableIndexAnd Lookup main " + e.toString());
  }
  
  
} // end Method main() ----------------------------




private H2JDBCUtils h2Instance = null;
 
 private Connection connection = null;
 private String tableName = "someTableName";



 
 private PreparedStatement insertPreparedStatement = null;
 private PreparedStatement queryPreparedStatement = null;
 private PreparedStatement queryKeysPreparedStatement = null;
 


 
 private static final String sleepLRAGRRow = "C0037313|sleep|verb|pres(fst_sing,fst_plur,thr_plur,second)|sleep|sleep|Organism Function|SNOMEDCT_US|258158006|null|0|Y|biological function:observable entity:qualifier value|0|0|0.0|true|E0056246";
 private static final String gaitLRAGRRow = "C0016928|gait|noun|count(thr_sing)|gait|gait|Finding|SNOMEDCT_US|271705001|null|0|N|function:observable entity|0|0|0.0|true|E0029163";
 
 private static final String depression1 =  "C4084909|depression|noun|uncount(thr_sing)|depression|depression|Intellectual Product|NCI|PROMIS|null|0|Y|null|0|0|0.0|true|E0021862";
 private static final String depression2 =  "C0011570|depression|noun|uncount(thr_sing)|depression|depression|Mental or Behavioral Dysfunction|SNOMEDCT_US|41006004|S|1|Y|disorder:finding|0|0|0.0|true|U080951";
 private static final String depression3 =  "C0460137|depression|noun|count(thr_sing)|depression|depression|Functional Concept|SNOMEDCT_US|255339005|null|0|Y|qualifier value|0|0|0.0|true|E0021862";
 
} // end class H2TableIndexAndLookup() --------
