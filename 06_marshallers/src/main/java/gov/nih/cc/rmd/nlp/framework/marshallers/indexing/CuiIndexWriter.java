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


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.vinci.model.Concept;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class CuiIndexWriter extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter  {
 
  // =======================================================
  /**
   * Constructor IndexingWriter 
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public CuiIndexWriter(String[] pArgs) throws ResourceInitializationException {
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
   
    try {
     
      List<Annotation> keys = UIMAUtil.getAnnotations(pJCas, Concept.typeIndexID);
      String     documentId = VUIMAUtil.getDocumentId(pJCas);
   
      if ( keys != null ) {
        for ( Annotation key: keys ) {
      
          String    assertionStatus = ((Concept)key).getAssertionStatus();
          String      subjectStatus = ((Concept)key).getSubjectStatus();
          boolean conditionalStatus = ((Concept)key).getConditionalStatus();
          String stati = assertionStatus + ":" + subjectStatus + ":" + conditionalStatus ;
          String cuiz = ((Concept)key).getCuis();
          if (cuiz == null ) continue;
          String cuis[] = U.split(cuiz,"|");
      
          for ( String cui: cuis )
            addToIndex( cui, documentId, key.getBegin(), key .getEnd(), stati);
        } // end loop thru the keys of the document
      } //end if there are concepts
    } catch (Exception e) {
 //     e.printStackTrace();
 //     System.err.println("Issue in process x IndexingWriter - some of these can be ignored. Not throwing an exception here " + e.toString());
    }
    this.performanceMeter.stopCounter();
   
  } // end Method process() ----------------

  

  // -----------------------------------------
  /** 
   * addToTables
   * the tables
   * 
   * @param pCui           
   * @param documentId
   * @param pBegin
   * @param pEnd
   * @param pStatus       
   * @throws SQLException 
   *
   */
  // -----------------------------------------
  private synchronized void addToIndex(String pCui,  String pDocumentId, int pBegin, int pEnd, String pStatus) throws SQLException {
   
    try {
      cuiIndexPreparedStatement.setString  (1, pCui);
      
      if ( U.isNumber(pDocumentId )) {
        long documentId = Long.parseLong(pDocumentId);
        cuiIndexPreparedStatement.setLong    (2, documentId);
      } else 
        cuiIndexPreparedStatement.setString   (2, pDocumentId);
    
      cuiIndexPreparedStatement.setInt     (3, pBegin);
      cuiIndexPreparedStatement.setInt     (4, pEnd);
      cuiIndexPreparedStatement.setString  (5, pStatus);
      
      cuiIndexPreparedStatement.execute();
   
     
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue adding to table " + e.toString());
      throw e;
    }
    
   
  } // End Method addToTables() ======================
  


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
     
    close();
  
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
  
  } // end Method Destroy() -----------------

 

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
  


  //----------------------------------
  /**
   * initialize 
   * 
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
  
   
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
            this.outputDir = U.getOption(pArgs, "--outputDir=", "./");
         String typeOfDocId = U.getOption(pArgs,  "--typeOfDocId=", "Long"); 
    
   
    try {
      U.mkDir(outputDir);
      U.mkDir(outputDir + "/db");
    } catch (Exception e1) {
      e1.getStackTrace();
      String msg = "Issue with making the output dir \n" + e1.getMessage() ;
      System.err.println( msg );
      throw new ResourceInitializationException ();
    }
    
    
    static_connection = initializeDatabase(this.outputDir, "cuiIndex", typeOfDocId );
   
  } // end Method initialize() -------
  
  
  
  
  


// =======================================================
  /**
   * initializeDatabase 
   * 
   * @return
   */
  // =======================================================
  private  Connection initializeDatabase(String pOutputDir, String pDatabaseName, String typeOfDocId  ) throws ResourceInitializationException{
    try {

      String    databaseName = pOutputDir + "/db/" + pDatabaseName;
     
      static_connection = connect2H2_Database(databaseName );
      createCuiIndexTable( static_connection, typeOfDocId );
      
      
      cuiIndexPreparedStatement = createCuiIndexPreparedStatement(static_connection);
    
      
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
 public  void close( ) {
   try {

   if ( static_connection != null && !static_connection.isClosed() ) 
     static_connection.close();
   
    static_connection = null;
   
   } catch (Exception e) {
       e.printStackTrace();
       System.err.println("Issue with closing the database " + e.toString());
     }
 
   
 } // End Method close() ================================

//=======================================================
 /**
  * createCuiIndexPreparedStatement
  * 
  * @param pConnection 
  */
 // =======================================================
 private void createCuiIndexTable( Connection pConnection, String pTypeOfDocId) throws ResourceInitializationException{
 
   try {
  
   Statement stat = pConnection.createStatement();
   
   stat.execute("create table cuiIndex  (" +             
       "cui                   varchar(8),   " +
       "documentSID           " + pTypeOfDocId + ",         " +
       "begin                 integer,      " +
       "end                   integer,      " +
       "status                varchar(30)   )") ;
     
  
  
   stat.execute("create INDEX cuiIndexIndex ON cuiIndex (cui)");
   stat.execute("CREATE INDEX docIdIndex ON cuiIndex (documentSID)");
   stat.close();
   
          
   } catch (Exception e) {
     e.printStackTrace();
     System.err.println( "Issue creating table cuiIndex " + e.toString());
     throw new ResourceInitializationException();
   }
  
 }  // End Method createKeyDocIdTable() ======================
 
//=======================================================
/**
* createCuiIndexPreparedStatement
* 
* @param pConnection
* @return PreparedStatement
* @throws SQLException 
*/
// =======================================================
private  PreparedStatement createCuiIndexPreparedStatement(Connection pConnection ) throws SQLException {
 
 
  String updateOrInsertStatement = "INSERT INTO cuiIndex (cui, documentSID, begin, end, status ) VALUES( ?, ?, ?, ?, ? )";
  
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
  private  Connection connect2H2_Database(String pH2DatabaseName) {
  
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
  
   private String outputDir = null;
   private  Connection static_connection = null;
   private  PreparedStatement cuiIndexPreparedStatement = null;
  
 
   private ProfilePerformanceMeter              performanceMeter = null;
   

 
} // end Class MetaMapClient() ---------------
