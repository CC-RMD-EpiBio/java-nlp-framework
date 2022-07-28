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
/*
 *
 */
/**
 * CuiTermStore is a wrapper around methods to query 
 * and retrieve from the cuiTerm table.
 *
 *
 * @author  Guy Divita 
 * @created Sep 13, 2011
 *
 * [Utah/Salt Lake City Preamble Here]
 */
// ================================================
package gov.nih.cc.rmd.nlp.lexUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The Class CuiTermStore.
 */
public class CuiTermStore {

  // ----------------------------------------------
  /**
   * Constructor: CuiTermStore.
   *
   * @throws Exception the exception
   */
  // ----------------------------------------------
  public CuiTermStore() throws Exception {

    this.db = new DbUtils(this.dbConfigProperties);

  } // end Constructor() --------------------------

  // ----------------------------------------------
  /**
   * get returns a String array of terms for a given cui.
   *
   * @param pCui the cui
   * @return String (Term)
   * @throws SQLException the SQL exception
   */
  // ----------------------------------------------
  public String[] get(String pCui) throws SQLException {

    String[] returnValue = null;
    ArrayList<String> buff = null;
    // ---------------------------------------------------------
    // Set the prepared statement if it has not already been set
    // ---------------------------------------------------------
    if (this.getPreparedStatement == null) {

      String query = "SELECT term FROM cuiTerm WHERE cui= ?";
      Connection connection = this.db.getConnection();
      this.getPreparedStatement = connection.prepareStatement(query);
    }

    this.getPreparedStatement.setString(1, pCui);
    ResultSet results = this.getPreparedStatement.executeQuery();

    if (results != null) {
      buff = new ArrayList<String>();

      while (results.next()) {
        buff.add(results.getString(1));
      } // end loop through result set

      returnValue = buff.toArray(new String[buff.size()]);
    }
    return returnValue;
  } // end Method get() -----------------------------

  // ----------------------------------------------
  /**
   * getCuis returns a String array of cuis for a given a term.
   *
   * @param pTerm the term
   * @return String[] (of cui's)
   * @throws SQLException the SQL exception
   */
  // ----------------------------------------------
  public String[] getCuis(String pTerm) throws SQLException {

    String[] returnValue = null;
    ArrayList<String> buff = null;
    // ---------------------------------------------------------
    // Set the prepared statement if it has not already been set
    // ---------------------------------------------------------
    if (this.getPreparedStatement == null) {

      String query = "SELECT cui FROM cuiTerm WHERE term= ?";
      Connection connection = this.db.getConnection();
      this.getCuiPreparedStatement = connection.prepareStatement(query);
    }

    this.getCuiPreparedStatement.setString(1, pTerm);
    ResultSet results = this.getCuiPreparedStatement.executeQuery();

    if (results != null) {
      buff = new ArrayList<String>();

      while (results.next()) {
        buff.add(results.getString(1));
      } // end loop through result set

      returnValue = buff.toArray(new String[buff.size()]);
    }
    return returnValue;
  } // end Method getCuis() -----------------------------

  // ----------------------------------------------
  /**
   * cleanup drops the database connection.
   */
  // ----------------------------------------------
  public void cleanup() {
    try {
      this.db.finalize();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Could not drop the db connection to the cuiTermStore table.");
    }

  } // end Method cleanup() ---------------------

  /** The db. */
  private DbUtils db = null;

  /** The get prepared statement. */
  private PreparedStatement getPreparedStatement = null;

  /** The get cui prepared statement. */
  private PreparedStatement getCuiPreparedStatement = null;

  /** The db config properties. */
  public String dbConfigProperties = "resources/lvg2016/dbConfig.properties";

} // end Class CuiTermStore() ---------------------
