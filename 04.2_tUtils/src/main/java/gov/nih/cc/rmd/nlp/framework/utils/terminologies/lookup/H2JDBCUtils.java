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
 * H2JDBCUtils
 *
 * @author     Guy Divita
 * @created    Jun 25, 2021
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies.lookup;


import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;


public class H2JDBCUtils {
  
  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws SQLException 
   * 
  **/
  // =================================================
  public H2JDBCUtils(String[] pArgs) throws SQLException {
    this.initialize(pArgs);
    
  } // end Constructor() ----------------------------

  public  synchronized Connection getConnection() throws SQLException {
    if ( connection == null )
        initialize();
  
    return connection;
} // end Method getConnection() --------------------

  // =================================================
  /**
   * close closes the jdbc connection
   * @throws Exception 
   * 
  */
  // =================================================
  public  synchronized void close() throws Exception {
    
    if ( connection != null ) 
    {
      try {
        connection.close();
        System.err.println("closed the connection");
        this.connection = null;
      } catch (SQLException e) {
        e.printStackTrace();
        String msg = GLog.error_println("H2JDBCUtils:close: Issue closing the connection " + e.toString());
        throw new Exception (msg);
      } 
    }
    else
    {
      GLog.println("H2JDBCUtils:close: closing an already closed connection. ");
    }
    
  } // end Method close() ----------------------------

  // =================================================
  /**
   * initialize sets up the connection or creates the connection
   * 
   * @param pArgs
   * @throws SQLException 
  */
  // =================================================
  public  synchronized void initialize(String[] pArgs) throws SQLException {
    
    
    jdbcURL      = U.getOption(pArgs, "--jdbcURL=", "jdbc:h2:file:" ); 
    jdbcUsername = U.getOption(pArgs, "--jdbcUserName=",  "sa");
    jdbcPassword = U.getOption(pArgs, "--jdbcPassword=", "sa");
  
        inputDir  = U.getOption(pArgs, "--inputDir=", "/00_legacy/04.2_tUtils/src/resources/sophiaH2Indexes");
             year = U.getOption(pArgs,  "--year=", "2020");
        extension = U.getOption(pArgs, "--extension=", ".h2" );
     databaseName = U.getOption(pArgs, "--databaseName=", "Sophia_" + "someTableName" + "_"+ year ); // no extension
     boolean useH2JarFile = Boolean.parseBoolean( U.getOption(pArgs,  "--useH2JarFile=", "true" ) );
     
     
     if (  useH2JarFile ) {
       String prefix = "resources/vinciNLPFramework/sophia";
       String dbName = prefix + "/" + databaseName + extension + ".mv.db" ;
       URL resource = this.getClass().getClassLoader().getResource( dbName );
       jdbcURL = "jdbc:h2:" + resource;
       jdbcURL = jdbcURL.substring(0, jdbcURL.length() - 6);
       jdbcURL = jdbcURL.replace(":jar:file:" , ":zip:");
       System.err.println("The jdbcURL = " + jdbcURL);
       
     } else {
    
        aFileName = inputDir + "/" + databaseName + extension ;    
        
        if ( jdbcURL.contentEquals("jdbc:h2:file:" ) )
            jdbcURL = jdbcURL + aFileName;
     }
    initialize();
   
  } // end Method initialize() ---------------------
  
  // =================================================
  /**
   * initialize sets up the connection or creates the connection
   * 
   * @param pArgs
   * @throws SQLException 
  */
  // =================================================
  public synchronized void initialize() throws SQLException {
    
  try {
    
    this.connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    System.err.println("connection opened ");
   // String schema = this.connection.getSchema();
   // System.err.println("Schema = " + schema);
  
  } catch (SQLException e) {
       e.printStackTrace(); 
       GLog.error_println("Issue trying to create a jdbc connection " + e.toString());
       throw e;
    }
   
  } // end Method initialize() ---------------------

// =================================================
  /**
   * setConnection sets connection
   *
   *  @param pConnection the connection to set
  */
  // =================================================
  public  synchronized final void setConnection(Connection pConnection) {
    this.connection = pConnection;
    
    System.err.println(" set the connection");
  }

  // =================================================
  /**
   * getJdbcURL get getJdbcURL
   * 
   * @return String
  */
  // =================================================
  public  final String getJdbcURL() {
    return jdbcURL;
  }

  // =================================================
  /**
   * setJdbcURL sets jdbcURL
   *
   *  @param jdbcURL the jdbcURL to set
  */
  // =================================================
  public  final void setJdbcURL(String jdbcURL) {
    this.jdbcURL = jdbcURL;
  }

  // =================================================
  /**
   * getH2FileName get getH2FileName
   * 
   * @return String
  */
  // =================================================
  public  final String getH2FileName() {
    return h2FileName;
  }

  // =================================================
  /**
   * setH2FileName sets h2FileName
   *
   *  @param h2FileName the h2FileName to set
  */
  // =================================================
  public  final void setH2FileName(String h2FileName) {
    this.h2FileName = h2FileName;
  }

  // =================================================
  /**
   * getYear get getYear
   * 
   * @return String
  */
  // =================================================
  public  final String getYear() {
    return year;
  }

  // =================================================
  /**
   * setYear sets year
   *
   *  @param pYear the year to set
  */
  // =================================================
 public  final void setYear(String pYear) {
    this.year = pYear;
  }

  // =================================================
  /**
   * getExtension get getExtension
   * 
   * @return String
  */
  // =================================================
  public  final String getExtension() {
    return this.extension;
  }

  // =================================================
  /**
   * setExtension sets extension
   *
   *  @param pExtension the extension to set
  */
  // =================================================
  public  final void setExtension(String pExtension) {
    this.extension = pExtension;
  }

  // =================================================
  /**
   * getDatabaseName get getDatabaseName
   * 
   * @return String
  */
  // =================================================
  public  final String getDatabaseName() {
    return databaseName;
  }

  // =================================================
  /**
   * setDatabaseName sets databaseName
   *
   *  @param pDatabaseName the databaseName to set
  */
  // =================================================
  public  final void setDatabaseName(String pDatabaseName) {
    this.databaseName = pDatabaseName;
  }

  // =================================================
  /**
   * getJdbcUsername get getJdbcUsername
   * 
   * @return String
  */
  // =================================================
  public  final String getJdbcUsername() {
    return jdbcUsername;
  }

  // =================================================
  /**
   * setJdbcUsername sets jdbcUsername
   *
   *  @param pJdbcUsername the jdbcUsername to set
  */
  // =================================================
   public  final void setJdbcUsername(String pJdbcUsername) {
    this.jdbcUsername = pJdbcUsername;
  }

  // =================================================
  /**
   * getJdbcPassword get getJdbcPassword
   * 
   * @return String
  */
  // =================================================
  public  final String getJdbcPassword() {
    return jdbcPassword;
  }

  // =================================================
  /**
   * setJdbcPassword sets jdbcPassword
   *
   *  @param pJdbcPassword the jdbcPassword to set
  */
  // =================================================
  
  public  final void setJdbcPassword(String pJdbcPassword) {
    this.jdbcPassword = pJdbcPassword;
  }

// =================================================
  /**
   * getInputDir get getInputDir
   * 
   * @return String
  */
  // =================================================
  public final String getInputDir() {
    return inputDir;
  }

  // =================================================
  /**
   * setInputDir sets inputDir
   *
   *  @param inputDir the inputDir to set
  */
  // =================================================
  
  public final void setInputDir(String pInputDir) {
    this.inputDir = pInputDir;
  }

  // =================================================
  /**
   * getaFileName get getaFileName
   * 
   * @return String
  */
  // =================================================
  public final String getaFileName() {
    return aFileName;
  }

  // =================================================
  /**
   * setaFileName sets aFileName
   *
   *  @param aFileName the aFileName to set
  */
  // =================================================
  public final void setaFileName(String pFileName) {
    this.aFileName = pFileName;
  }

  // =================================================
  /**
   * printSQLException pretty prints an h2 sql error 
   *
   *  @param aFileName the aFileName to set
  */
  // =================================================
public final void printSQLException(SQLException ex) {
    for (Throwable e: ex) {
        if (e instanceof SQLException) {
            e.printStackTrace(System.err);
            System.err.println("SQLState: " + ((SQLException) e).getSQLState());
            System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
            System.err.println("Message: " + e.getMessage());
            Throwable t = ex.getCause();
            while (t != null) {
                System.out.println("Cause: " + t);
                t = t.getCause();
            }
        }
    }
} // end Method printSQLException() ----------------

// -----------------------
// Global Variables
// -----------------------
private  String jdbcURL = "jdbc:h2:file:";
private  String h2FileName = "sophia";
private  String year = "2020";
private  String extension = ".h2";
private  String databaseName = h2FileName + "_" + year + extension;

private  String jdbcUsername = "sa";
private  String jdbcPassword = "";
private  Connection connection = null;

String    inputDir  = null;
String    aFileName = null;

} // end Class H2JDBCUtils() -----------
