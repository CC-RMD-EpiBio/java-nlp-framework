// =======================================================
/**
 * CorpusStatsSummary
 *    After the instance table is created, run this program to create the summary stat tables and reports
 *
 * @author  guy
 * @created Oct 31, 2015
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.corpusStats;

import gov.nih.cc.rmd.nlp.framework.utils.U;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * @author guy
 *
 */
public class CorpusStatsSummary {


  private static Connection static_connection;



  // ------------------------------------------
  /**
   * writeSummaryTable writes out a table with
   *   Number of documents
   *   Number of Concept Instances
   *   Number of Classes
   *   Number of DocumentTypes
   * @throws RuntimeException 
   *
   *
   */
  // ------------------------------------------
  private static void writeSummaryTable2() throws Exception {
    
    String InsertQuery = "INSERT INTO SummaryTable2 ("
        + "totalDocuments, " 
        + "totalPatients,"
        + "totalLabel, "
        + "totalTIUStandardTitle " 
        + ") values" + "(?,?,?,?)";
    
    double _numberOfDocuments     = queryOneIntResult( "SELECT COUNT(DISTINCT documentSID)      from CORPUS_INSTANCES", DOUBLE);
    double _numberOfPatients      = queryOneIntResult( "SELECT COUNT(DISTINCT patientSID)       from CORPUS_INSTANCES", DOUBLE);
    double _numberOfClasses       = queryOneIntResult( "SELECT COUNT(DISTINCT category)         from CORPUS_INSTANCES", INT);
    double _numberOfDocumentTypes = queryOneIntResult( "SELECT COUNT(DISTINCT TIUStandardTitle) from CORPUS_INSTANCES", INT);
    
    try {
    
     PreparedStatement insertPreparedStatement = static_connection.prepareStatement(InsertQuery);
    
     insertPreparedStatement.setDouble(1, _numberOfDocuments);
     insertPreparedStatement.setDouble(2, _numberOfPatients);
     insertPreparedStatement.setInt(3, ((int)_numberOfClasses));
     insertPreparedStatement.setInt(4, ((int)_numberOfDocumentTypes));
     
     insertPreparedStatement.execute();
     
   
    } catch (Exception e) {
      e.printStackTrace();
      String msg1 = "Error: Issue with creating the statsTable2 " + e.getMessage(); 
      System.err.println(msg1  );
     
    }
   
  } // End Method writeSummaryTable2() -----------------------
  
//------------------------------------------
 /**
  * writeSummaryTable writes out a table with
  *   Number of documents
  *   Number of Concept Instances
  *   Number of Classes
  *   Number of DocumentTypes
  * @throws RuntimeException 
  *
  *
  */
 // ------------------------------------------
 private static void writeSummaryTable3() throws Exception {
   


  Statement tableThreeQueryByDocuments = static_connection.createStatement();
  
  ResultSet tableThreeByDocumentsResultSet = tableThreeQueryByDocuments.executeQuery("SELECT Label, count(*) AS FREQUENCY " + 
                                                                "FROM CORPUS_INSTANCES " + 
                                                                "GROUP BY Label,TIUDocumentSID " +
                                                                "ORDER BY FREQUENCY DESC");
  
  ResultSet tableThreeByPatientsResultSet = tableThreeQueryByDocuments.executeQuery("SELECT Label, count(*) AS FREQUENCY " + 
      "FROM CORPUS_INSTANCES " + 
      "GROUP BY Label,patientSID " +
      "ORDER BY FREQUENCY DESC");
  
  ResultSet tableThreeByInstancesResultSet = tableThreeQueryByDocuments.executeQuery("SELECT Label, count(*) AS FREQUENCY " + 
      "FROM CORPUS_INSTANCES " + 
      "GROUP BY Label " +
      "ORDER BY FREQUENCY DESC");
  
  HashMap<String,TableThreeRowContainer> tList = new HashMap<String, TableThreeRowContainer>();
  if ( tableThreeByDocumentsResultSet != null ) {
    while ( tableThreeByDocumentsResultSet.next() ) {
      TableThreeRowContainer t = new TableThreeRowContainer();
      String          label = tableThreeByDocumentsResultSet.getString(1);
      double totalDocuments = tableThreeByDocumentsResultSet.getDouble(2);
      t.label = label;
      t.totalDocuments = totalDocuments;
      tList.put( label, t);
    } // end loop thru result set
  } // end if the result set is null
  
  if ( tableThreeByPatientsResultSet != null ) {
    while ( tableThreeByPatientsResultSet.next() ) {
     
      String          label = tableThreeByPatientsResultSet.getString(1);
      double  totalPatients = tableThreeByPatientsResultSet.getDouble(2);
      TableThreeRowContainer t = tList.get( label);
      t.totalPatients = totalPatients;
   } // end loop thru result set
  } // end if the result set is null
  
  if ( tableThreeByInstancesResultSet != null ) {
    while ( tableThreeByInstancesResultSet.next() ) {
     
      String          label = tableThreeByInstancesResultSet.getString(1);
      double totalInstances = tableThreeByInstancesResultSet.getDouble(2);
      TableThreeRowContainer t = tList.get( label);
      t.totalInstances = totalInstances;
   } // end loop thru result set
  } // end if the result set is null
 
   
   String InsertQuery = "INSERT INTO SummaryTable3 ("
       + "Label, " 
       + "TotalInstatnces,"
       + "TotalDocuments, "
       + "TotalPatients " 
       + ") values" + "(?,?,?,?)";
   
    Set<String> keys = tList.keySet();
   for ( String key : keys ) {
     TableThreeRowContainer t = tList.get(key);
  
     try {
   
       PreparedStatement insertPreparedStatement = static_connection.prepareStatement(InsertQuery);
   
       insertPreparedStatement.setString(1, t.label);
       insertPreparedStatement.setDouble(2, t.totalInstances);
       insertPreparedStatement.setDouble(3, t.totalDocuments);
       insertPreparedStatement.setDouble(4, t.totalPatients);
       insertPreparedStatement.execute();

     } catch (Exception e) {
       e.printStackTrace();
       String msg1 = "Error: Issue wit inserting into table 3 " + e.getMessage(); 
       System.err.println(msg1  );
    
     }
  
   } // end loop thru keys of the rows of summary table 3
 } // End Method writeSummaryTable2() -----------------------

 // =======================================================
 /**
  * getNumberOfDocumentTypes 
  * 
  * @param pQuery
  * @param intOrDouble INT|DOUBLE
  * @return double
  */
 // =======================================================
 private static double queryOneIntResult(String pQuery, int intOrDouble ) {
   double returnVal = 0; 
   String query = "SELECT COUNT(DISTINCT TIUStandardTitle) from CORPUS_INSTANCES";
   try {
     Statement statement = static_connection.createStatement();
     ResultSet rs = statement.executeQuery(pQuery);
     
     if ( rs != null && rs.first()) { 
       
       switch ( intOrDouble ) {
         case INT:    returnVal = rs.getInt(1); break;
         case DOUBLE: returnVal = rs.getDouble(1); break;
       }
     }
            
   } catch (SQLException e) {
     e.printStackTrace();
     System.err.println( "Issue with statement " + query + " " + e.toString());
   }
    
   return returnVal;
 } // End Method queryOneIntResult() ======================



 //----------------------------------
 /**
  * initialize loads in the resources needed for phrase chunking. 
  * 
  * @param aContext
  * 
  **/
 // ----------------------------------
 public void initialize( String[] pArgs) throws ResourceInitializationException {
 
  
   String      outputDir = U.getOption(pArgs, "--outputDir=", "./");
  
   this.outputStatsDir       = outputDir + "/stats/";
   System.err.println(" OutputStats Dir = " + this.outputStatsDir);
  
   initializeDatabase(this.outputStatsDir);
   createSummaryTable2( static_connection );
   createSummaryTable3( static_connection );
 
  
  
 } // end Method initialize() -------
 

//=======================================================
/**
 * initializeDatabase 
 * 
 * @return
 */
// =======================================================
private void initializeDatabase(String pOutStatsDir ) {
  try {

    String    databaseName = pOutStatsDir + "/corpusInstances";
   
    static_connection = connect2H2_Database(databaseName );
  
  } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with the database stuff " + e.toString());
    }

  
} // End Method initializeDatabase() ======================


//=======================================================
/**
* createSummaryTable2 
* 
* @param pConnection 
*/
//=======================================================
private static void createSummaryTable2( Connection pConnection) {

try {



Statement stat = pConnection.createStatement();

stat.execute("create table SummaryTable2  (" +             
 "totalDocuments         BIGINT, "    +
 "totalPatients         BIGINT, "    +
 "totalLabel            int, "       + 
 "totalTIUStandardTitle int      )");



} catch (Exception e) {
  e.printStackTrace();
  System.err.println( "Issue creating the h2 datatabase");
}

}  // End Method createH2_DatabaseTable() ======================



//=======================================================
/**
* createSummaryTable3     classes   | total Instances     |   total Documents | total Patients
* 
* @param pConnection 
*/
//=======================================================
private static void createSummaryTable3( Connection pConnection) {

try {



Statement stat = pConnection.createStatement();

stat.execute("create table SummaryTable3  (" +             
"Label                 varchar(20), " +
"TotalInstances        BIGINT, "      +
"TotalDocuments        BIGINT, "      + 
"TotalPatients         BIGINT   )");



} catch (Exception e) {
e.printStackTrace();
System.err.println( "Issue creating the h2 datatabase");
}

}  // End Method createSummarytable3() ======================


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
  conn = DriverManager.getConnection("jdbc:h2:" + pH2DatabaseName );
 
  
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println( "Issue creating the h2 datatabase");
  }
  
  return conn;
}  // End Method createH2_DatabaseTable() ======================





 private String outputStatsDir;
 
 
 private static final int INT = 1;
 private static final int DOUBLE = 2;
 

  
} // end Class CorpusStatsSummary() ------
