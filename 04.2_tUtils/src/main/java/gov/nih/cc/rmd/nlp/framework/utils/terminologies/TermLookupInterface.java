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
 * TermLookupInterface is an interface that connects to the appropriate
 * lookup back end to pull data from. 
 *
 *
 * @author  Guy Divita 
 * @created May 16, 2018
 * @modified Feb 21, 2019
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies;


import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.LexRecord;

public interface TermLookupInterface {
  

  // -----------------------------------------
  /**
   * initialize is used for loading and indexing the terminology tables.
   * 
   * @param pArgs
   * 
   */
  // -----------------------------------------
   void init(String[] pArgs ) throws Exception ; 
  
   
   // -----------------------------------------
   /**
    * initialize is used for loading and indexing the terminology tables.
    * 
    * @param pTerminologies
    * @param pNumberOfRecords
    * 
    */
   // -----------------------------------------
    void init(String[] pTerminologies , int pNumberOfRecords) throws Exception ; 
   
  // =================================================
  /**
   * init 
   * 
   * @param pUMLSVersion
   * @throws Exception 
   * @deprecated - moving away from umls version
  */
  // =================================================
  void init(String pUMLSVersion) throws Exception;


  // -----------------------------------------
  /**
   * get retrieves a list of terms given a key. Multiple records could be
   * returned for a key based on case and part of speech.
   * 
   * Note: The key is a string composed of the tokens of the term in reverse
   * order. Also, punctuation has been replaced by spaces.
   * 
   * @param pKey
   * @param pNumberOfTokens
   * @return List<LexRecord>
   * @throws Exception
   */
  // -----------------------------------------
  public List<LexRecord> get(String pKey) throws Exception ;

 
  // -----------------------------------------
  /**
   * get retrieves a list of terms given a key. Multiple records could be
   * returned for a key based on case and part of speech.
   * 
   * @param pKey
   * @param pNumberOfTokens
   * @return List<LexRecord>
   */
  // -----------------------------------------
 public List<LexRecord> get(String pKey, int pNumberOfTokens) ; 
  
 
 // ==========================================
 /**
  * addTerm adds term to the lexicon
  *
  * @param pLRAGRTerm (row in lragr format)
  * 
  */
 // ==========================================
 public void addTerm( String pLRAGRTerm) ;


// =================================================
/**
 * getSimple
 * 
 * @param pTerminologies   |SNOMEDCT_US|RXNORM|REL|NET|NCI|LNC|UCUM|SPECIALIST| ....
 * @param key
 * @return String[]  return rows from the service
 * @throws Exception 
*/
// =================================================
 public String[] getSimple(String[] pTerminologies, String key) throws Exception;

 
// =================================================
 /**
  * getSimple  assumes a default terminology like UMLS or SNOMEDCT_US
  * 
  * @param key
  * @return String[]  return rows from the service
  * @throws Exception 
  */
 // =================================================
 public String[] getSimple(String key) throws Exception;

//=================================================
/**
* getNotSimple  assumes a default terminology like UMLS or SNOMEDCT_US
* 
*
* @param tree       The tree the term comes from - i.e. in snomed the 
*                   (parenthetical term) that follows the term like aspirin (substance)
*                   
*                   or semantic type from the UMLS 
*                   
*                   
* @param key
* @return String[]  return rows from the service
* @throws Exception 
*/
// =================================================
public String[] getNotSimple(String tree, String key) throws Exception;




//=================================================
/**
* getNotSimple  assumes a default terminology like UMLS or SNOMEDCT_US
* 
*
* @param pSabs   The terminologies to search from (colon separated sabs)
* @param tree       The tree the term comes from - i.e. in snomed the 
*                   (parenthetical term) that follows the term like aspirin (substance)
*                   
*                   or semantic type from the UMLS 
*                   
*                   
* @param key
* @return String[]  return rows from the service
* @throws Exception 
*/
//=================================================
public String[] getNotSimple(String pSabs, String tree, String key) throws Exception;


//=================================================
/**
* getNotThatSimple  
* 
* @param sab        a source abbreviations 
* @param tree       The tree the term comes from - i.e. in snomed the 
*                   (parenthetical term) that follows the term like aspirin (substance)
*                   
*                   or semantic type from the UMLS 
*                   
*                   
* @param key
* @return List<LexRecord>  return rows from the service
* @throws Exception 
*/
//=================================================
public List<LexRecord> getNotThatSimple(String sab, String tree, String key) throws Exception;

//=================================================
/**
* getNotThatSimple  
* 
* @param tree       The tree the term comes from - i.e. in snomed the 
*                   (parenthetical term) that follows the term like aspirin (substance)
*                   
*                   or semantic type from the UMLS 
*                   
*                   
* @param key
* @return List<LexRecord>  return rows from the service
* @throws Exception 
*/
//=================================================
public List<LexRecord> getNotThatSimple( String tree, String key) throws Exception;



// =================================================
/**
 * init 
 * 
 * @param pArgs
 * @param pTerminologyFiles
 * @throws Exception 
*/
// =================================================
public void init(String[] pArgs, String[] pTerminologyFiles) throws Exception;




  
} // end Class LexicalLookup() ---------------

