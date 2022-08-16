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
 * RelationUtilities contain utilities like how to get the inverse of a relation or, given an inverse, how to get the active form.
 *
 * @author     Guy Divita
 * @created    Feb 17, 2022
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.lexUtils;

import java.util.HashMap;
import java.util.HashSet;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

/**
 * @author Guy
 *
 */
public class RelationUtilities {

  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws Exception 
   * 
  **/
  // =================================================
  public RelationUtilities(String[] pArgs) throws Exception {
    
    this.initialize( pArgs );
    
  } // end Constructor() -----------------------------
  
//=================================================
 /**
  * isRelationAllowed returns true if this relation is on the list of approved relations
  * 
  * @param pRelation
  * @return boolean
 */
 // =================================================
 public boolean isRelationAllowed(String pRelation) {
   
   boolean returnVal = false;
   
   if ( pRelation != null && pRelation.trim().length() > 0 )
     returnVal = this.allowedRelationsHash.contains(pRelation); 
       
   return returnVal;
 } // end Method isRelationAllowed() ----------------

 // =================================================
 /**
  * getInverseRelation  returns the inverse, if the base is given, and returns the base if the inverse is given. 
  * 
  * @param pRelation
  * @return String
 */
 // =================================================
 public String getInverseRelation(String pRelation) {
 
   String returnVal = null;
   
   if ( pRelation != null && pRelation.trim().length() > 0 ) {
      returnVal = this.inverseHash.get(pRelation.trim());
   
     if ( returnVal == null  )
       returnVal = this.relationsWithInverseHash.get(pRelation.trim());
     
   }
   return returnVal;
   
 } // end Method getInverseRelation() ---------------- 
 
 
 
//=================================================
/**
* isInverseRelation returns true if this is an inverse
* of a relation
* 
* @param pRelation
* @return String
*/
// =================================================
public boolean isInverseRelation (String pRelation) {

 boolean returnVal = false;
 
 if ( pRelation != null && pRelation.trim().length() > 0 )
    if ( this.inverseHash.get(pRelation.trim()) != null )
      returnVal = true;
    
 return returnVal;
 
} // end Method isInverseRelation() ---------------- 





  // =================================================
  /**
   * initialize 
   * 
   * @param pArgs
   * @throws Exception 
  */
  // =================================================
  public void initialize(String[] pArgs) throws Exception {
    
    String     relationsWithInverses = U.getOption(pArgs,  "--relationsWithInverses=", "./resources/relationsWithInverses.csv");
    String     inverseRelations      = U.getOption(pArgs,  "--inverseRelations=", "./resources/inverseRelations.csv");
    String     allowedRelations      = U.getOption(pArgs, "--allowedRelations=", "./resources/allowedRelations.txt");
  
    loadRelationsWithInverses( relationsWithInverses);
    loadInverseRelations ( inverseRelations);
    loadAllowedRelations ( allowedRelations );
     
    
    
  } // end Method initialize() -----------------------

  // =================================================
  /**
   * loadInverseRelations 
   * 
   * @param pInverseRelationsFileName
   * @throws Exception 
  */
  // =================================================
  private void loadInverseRelations(String pInverseRelationsFileName) throws Exception {
   
    try {
      String[] rows = U.readFileIntoStringArray(pInverseRelationsFileName);
      this.inverseHash = new HashMap<String,String>(300);

      String baseForm = null;
      String inverseForm = null;
      if ( rows!= null && rows.length > 0 ) 
        for ( String row: rows ) {
          if ( row != null && row.trim().length() > 0 && !row.startsWith("#") ) {
            String cols[] = U.split(row);
            inverseForm = cols[0].trim();
            try {
             baseForm = cols[1].trim();
            } catch (Exception e) {
              e.printStackTrace();
            }
            this.inverseHash.put( inverseForm, baseForm);
          }
        }
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = GLog.println(GLog.ERROR_LEVEL,this.getClass(), "loadInverseRelations", "Issue loading the file " + pInverseRelationsFileName + " : " + e.toString() );
      throw new Exception (msg);
    }
   
  } // end Method loadInverseRelations() -------------

  // =================================================
  /**
   * loadRelationsWithInverses
   * 
   * @param pRelationsWithInverses
   * @throws Exception 
  */
  // =================================================
  private void loadRelationsWithInverses(String pRelationsWithInverses) throws Exception {
    try {
      String[] rows = U.readFileIntoStringArray(pRelationsWithInverses);
      this.relationsWithInverseHash = new HashMap<String,String>(300);

      if ( rows!= null && rows.length > 0 ) 
        for ( String row: rows ) {
          if ( row != null && row.trim().length() > 0 && !row.startsWith("#") ) {
            String cols[] = U.split(row);
            String baseForm = cols[0].trim();
            String inverseForm = cols[1].trim();
            this.relationsWithInverseHash.put( baseForm, inverseForm);
          }
        }
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = GLog.println(GLog.ERROR_LEVEL,this.getClass(), "loadInverseRelations", "Issue loading the file " + pRelationsWithInverses + " : " + e.toString() );
      throw new Exception (msg);
    }
  } // end Method loadRelationsWithInverses() --------

  // =================================================
  /**
   * loadAllowedRelations
   * 
   * @param pAllowedRelations
   * @throws Exception 
  */
  // =================================================
  private void loadAllowedRelations(String pAllowedRelations) throws Exception {
    try {
      String[] rows = U.readFileIntoStringArray(pAllowedRelations);
      this.allowedRelationsHash = new HashSet<String>(00);

      if ( rows!= null && rows.length > 0 ) 
        for ( String row: rows ) {
          if ( row != null && row.trim().length() > 0 && !row.startsWith("#") ) {
           String baseForm = row.trim();
         this.allowedRelationsHash.add( baseForm );
          }
        }
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = GLog.println(GLog.ERROR_LEVEL,this.getClass(), "loadInverseRelations", "Issue loading the file " + pAllowedRelations + " : " + e.toString() );
      throw new Exception (msg);
    }
  } // end Method loadAllowedRelations() --------
  
  // =================================================
  /**
   * main
   * 
   * @param pArgs
  */
  // =================================================
  public static void main(String[] pArgs) {
    
    String args[] = setArgs( pArgs );
    
    try {
      RelationUtilities relUtil = new RelationUtilities( args);

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println("Issue with the RelationUtilities " + e.toString());
    }
      
  } // end Method Main() -----------------------------

  // =================================================
  /**
   * setArgs [TBD] summary
   * 
   * @param pArgs
   * @return
  */
  // =================================================
  private static String[] setArgs(String[] pArgs) {


    String     inputDir = U.getOption(pArgs,  "--inputDir=", "./2020AA/META");
    String     relationsWithInverses = U.getOption(pArgs,  "--relationsWithInverses=", "./resources/relationsWithInverses.csv");
    String     inverseRelations      = U.getOption(pArgs,  "--inverseRelations=", "./resources/inverseRelations.csv");
    String     allowedRelations      = U.getOption(pArgs, "--allowedRelations=", "./resources/allowedRelations.txt");
    String     version               = "2022.02.17.0";
     
    
    String args[] = {
        
        "--inputDir=" + inputDir,
        "--relationsWithInverses=" + relationsWithInverses,
        "--inverseRelations="      + inverseRelations,
        "--allowedRelations="      + allowedRelations,
        "--version="               + version
        
    };

    if ( Use.version(pArgs, args ) || Use.usageAndExitIfHelp( "RelationUtilities", pArgs, args ) )
      Runtime.getRuntime().exit(0);
   
    return args;

  } // end Method setArgs() ---------------------
  
  // --------------------------------------------
  // Global Variables
  // --------------------------------------------
  private HashMap<String, String> inverseHash = null;
  private HashMap<String, String> relationsWithInverseHash = null;
  private HashSet<String > allowedRelationsHash = null;
  
  
  
} // end Class RelationUtils.class() -----------------
