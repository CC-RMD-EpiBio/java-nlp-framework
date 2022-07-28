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
 * DbUtils is a wrapper around database connection stuff
 *
 *
 * @author  Guy Divita 
 * @created Mar 5, 2011
 *
 * [Utah/Salt Lake City Preamble Here]
 */
// ================================================
package gov.nih.cc.rmd.nlp.lexUtils;


import java.io.BufferedReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DbUtils {

  // -----------------------------------------
  /**
   * Constructor
   * initialize creates the db connections based
   * on the parameters from the the config file.
   * 
   * The path to the config file (dbConfig.properties)
   * in the form of a url needs to be passed in here.
   * 
   * @param pDbConfigFile
   * 
   * @throws Exception
   */
  // -----------------------------------------
  public DbUtils(URL pDbConfigFile) throws Exception {
    
    // read the config file
    readConfig( pDbConfigFile);
    
    // load the driver
    loadDriver( this.driverName); 
    
    // create a connection
    connect();
    
  } // end Constructor  ---------------------------

  // -----------------------------------------
  /**
   * DbUtils Constructor
   *
   * @param pDbConfigProperties
   * @throws Exception 
   */
  // -----------------------------------------
  public DbUtils(String pDbConfigProperties) throws Exception {
   
    // read the config file
    readConfig( pDbConfigProperties );
    
    // load the driver
    loadDriver( this.driverName); 
    
    // create a connection
    connect();
    
  } // end Constructor() ---------------------

  // -----------------------------------------
  /**
   * finalize closes down the connection and cleans up
   * if this is ever called.
   * 
   */
  // -----------------------------------------
   public void finalize() throws Exception {
   
     this.closeConnection();
     this.connection = null;
    
  } // end Method finalize() -----------------
 
  // -----------------------------------------
  /**
   *closeConnection shuts down the connection
   * 
   */
  // -----------------------------------------
  public void closeConnection() throws Exception {
   
    try {
      this.execute( "SHUTDOWN");
    this.connection.close();
    } catch ( SQLException e) {
      throw new Exception("Could not close the connection " + getMsg( e) );
    }
  } // end Method closeConnection() ----------

  // -----------------------------------------
  /**
   * execute executes sql commands that don't
   * return a result set
   * 
   * @param pCommand
   */
  // -----------------------------------------
  public void execute(String pCommand) throws Exception  {
    
    
    try {
      Statement statement = this.connection.createStatement();
      statement.execute( pCommand);
      statement.close();
    } catch ( SQLException e ) {
      
      throw new Exception ("Couldn't execute the command " + pCommand + " " + getMsg(e));
    }
    
  } // end Method execute() ------------------

  // -----------------------------------------
  /** 
   * getconnection retrieves connection
   *  
   * @return the connection
   */
  // -----------------------------------------
  public Connection getConnection() {
    return this.connection;
  }

  // -----------------------------------------
  /** 
   * getconnectionString retrieves connectionString
   *  
   * @return the connectionString
   */
  // -----------------------------------------
  public String getConnectionString() {
    return this.connectionString;
  }

  // -----------------------------------------
  /** 
   * setconnectionString sets the value of connectionString
   *  
   * @param pConnectionString the connectionString to set
   */
  // -----------------------------------------
  public void setConnectionString(String pConnectionString) {
    this.connectionString = pConnectionString;
  }

  // -----------------------------------------
  /** 
   * setconnection sets the value of connection
   *  
   * @param pConnection the connection to set
   */
  // -----------------------------------------
  public void setConnection(Connection pConnection) {
    this.connection = pConnection;
  }

  // -----------------------------------------
  /**
   * connect opens a connection to the database.
   * Don't forget to close it when finished.
   * 
   */
  // -----------------------------------------
  public void connect() throws Exception {
    try {
      this.connection = DriverManager.getConnection(this.connectionString, this.user, this.password);
      this.connection.setAutoCommit(true);
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new Exception("Not able to connection to the database " + e.toString() );
    }
    
  } // end Method Connect() ------------------

  // -----------------------------------------
  /**
   * loadDriver loads the driver dynamically by
   * picking up the name from the config file, then
   * looking for the class by that name.  The class
   * will have to be put in a jar or class that's on
   * the class path.  This will enable one to swap in
   * hyperSql, or mysql, or slqLite, by just adding
   * the driver class to the class path, and adding
   * the name to the config file.
   * 
   * @param pDriverName
   * @throws Exception
   */
  // -----------------------------------------
  private void loadDriver(String pDriverName) throws Exception {
    try {
      Class.forName(pDriverName).newInstance();
    }
    catch (Exception e) {
      throw new Exception ("Not able to load the driver, check the driver name, or that the classpath includes a db driver "+ e.toString());
    }
    
  } // end Method loadDriver() ---------------
  
  
  // -----------------------------------------
  /**
   * getMsg returns a more detailed message for
   * a sql exception.
   * 
   * @param pE
   * @return
   */
  // -----------------------------------------
  private String getMsg(SQLException pE) {
    StringBuffer msg = new StringBuffer();
    msg.append("SQL Exception  = " + pE.getMessage()   + "\n");
    msg.append("SQL State      = " + pE.getSQLState()  + "\n");
    msg.append("Vendor Message = " + pE.getErrorCode() + "\n");
    return msg.toString();
  } // end Method getMsg() -------------------

  
  // -----------------------------------------
  /**
   * readConfig reads in the config file resources/db/dbConfig.properties
   * This method is looking for the following variables:
   *   dbDriverName=
   *   dbUser=
   *   dbPassword=
   *   dbConnectionString=
   * 
   * @param pDbConfigFile
   */
  // -----------------------------------------
  private void readConfig(String pDbConfigFile) throws Exception {
    
    BufferedReader in = U.getClassPathResource( pDbConfigFile);
    this.properties = new Properties();
    this.properties.load(in);
    readConfig();
       
  } // end Method readConfig() ----------------
  
  // -----------------------------------------
  /**
   * readConfig reads in the config file resources/db/dbConfig.properties
   * This method is looking for the following variables:
   *   dbDriverName=
   *   dbUser=
   *   dbPassword=
   *   dbConnectionString=
   * 
   * @param pDbConfigFile
   */
  // -----------------------------------------
  private void readConfig(URL pDbConfigFile) throws Exception {
   
    if (pDbConfigFile == null){
      throw new Exception("No dbConfig file specified. It was supposed to be file:///resources/db/dbConfig.properties");
    }  
   try {
     this.properties = new Properties();
     this.properties.load( pDbConfigFile.openStream());
     
     readConfig();
     
   } catch ( Exception e0 ) {
     e0.printStackTrace();
     throw new Exception ("Not able to open the database properties file " + e0.toString());
   }
  } // end Method readConfig() ----------------
  
   // -----------------------------------------
   /**
    * readConfig reads in the config file resources/db/dbConfig.properties
    * This method is looking for the following variables:
    *   dbDriverName=
    *   dbUser=
    *   dbPassword=
    *   dbConnectionString=
    * 
    * @param pDbConfigFile
    */
   // -----------------------------------------
   private void readConfig() throws Exception {

     try {
     this.driverName       = this.properties.getProperty("dbDriverName");
     this.user             = this.properties.getProperty("dbUser");
     this.password         = this.properties.getProperty("dbPassword");
     String prefix         = this.properties.getProperty("dbConnectionStringPrefix");
     String path           = this.properties.getProperty("dbConnectionStringPath");
     String suffix         = this.properties.getProperty("dbConnectionStringSuffix");
     
     if ( !path.startsWith("/"))
       path= U.getClassPathToResources() + "/" + path;
    
     this.connectionString = prefix + path + suffix;
     System.err.println("Connection string = " + this.connectionString);
     
     } catch (Exception e1 ) {
       throw new Exception ("Not able to find a database parameter " + e1.toString());
     }

  } // end Method readConfig() ---------------


  // =================================================
  /**
   * main
   */
  // ================================================
  public static void main(String[] args) {
    URL dbConfigFile = null;
    
    try {
      DbUtils db = new DbUtils( dbConfigFile);
    
      db.finalize();
    
    
    } catch ( Exception e) {
      e.printStackTrace();
      System.err.println("Something went wrong " + e.toString());
    }
    
  
    
  } // end Method main() --------------------------

  // =====================================
  // Class Variables
  // =====================================
  private String       driverName = null;
  private Properties   properties = null;
  private Connection   connection = null;
  private String connectionString = null;
  private String             user = null;
  private String         password = null;
  
} // end Class DbUtils() --------------------------
