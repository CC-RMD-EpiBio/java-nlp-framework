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
