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
