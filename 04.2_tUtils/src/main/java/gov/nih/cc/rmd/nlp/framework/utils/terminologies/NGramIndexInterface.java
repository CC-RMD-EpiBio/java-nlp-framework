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
 * UMLSGramIndex creates an index of the UMLS 
 * to be absorbed by another program 
 * 
 * This class does not include a tokenizer. Instead,
 * it requires one to pass in a List<String> of the
 * tokens for each file processed.  
 * 
 * Make sure you use the same tokenizer on the indexing
 * side and the retrieval or query side. 
 *  
 * This class requires the following sequence of methods
 * to index a corpus:
 * 
 * -----------------------------------------------
 * { 
 *   initialize( ... ) with needed parameters
 * 
 *   for each record/file
 *     process( List<String>tokens, String documentId )
 *   
 *   finish()
 * 
 * } 
 * -----------------------------------------------
 *
 * There are two kinds of files produced:
 *   nGrams_X.unsorted.freq  : One for each kind of gram.  This file includes the gram and its
 *                             corpus frequency.  This number is useful for tf/idf calculations.
 *                             
 *                             The file format of these files are
 *                               +----+-----------+
 *                                gram|corpus Freq|
 *                               +----+-----------+
 *                             
 *                             
 *   nGrams_X.txt            : One for each kind of gram.  This file includes the gram, the reference
 *                             to where it came from (an id made up from the document name, patient
 *                             id, or something akin to that), and the frequency of this gram within 
 *                             this document.  The file is not sorted.  It will be more efficient
 *                             to use unix sort to sort this file than to have it done within java.
 *                             
 *                             The file format of these files are
 *                               +----+--------+----------
 *                                gram|doc Freq|identifier
 *                               +----+--------+----------
 *                                
 *                             Use the unix sort command sort -t "|" +0 -1 +1nr -2 +2 -3 nGrams_X.txt 
 *                             
 
 *   More Notes:
 *            
 * Version 2: X number of files are read in, the (interim) gram files are
 *            written to a directory.
 *            When the last file is read in,
 *            the finish method reads all the interim
 *            files summarizes them and writes out the
 *            gram files.
 * 
 * TTD - 
 *     - break on sentential boundaries - No need for ngrams that 
 *       cross these boundaries, right?
 *       
 *  Discussion - should the ngrams be all lowercased - yes
 *             - should the stopwords be lowercased - yes
 *             
 *  The stop word list should contain the usual suspects and
 *  also be guided by the top ngrams.  Anything that brings back
 *  over a quarter of the corpus in a search is not likely to be useful.
 *  The usual suspects include punctuation, and closed class categories
 *   (det, pron, conj, aux, modal, compl)
 *   
 *  The multi token ngrams can be culled to those that
 *  include nouns and verbs.  Any that don't have nouns
 *  and/or verbs are likley to be junk. i.e. "has a" "in the"
 *  This would require a pos lookup of some kind.  Something
 *  not hooked in here yet. Maybe a function word hash check
 *  would cover most of the junk, and let adj/adv's pass thru.
 *  That wouldn't require a pos check. 
 *  
 *   The constructors were modified to make a call to the UMLSLicenseValidation.validateUMLSLicense()
 *   to check for the existence of a umlsValidation.dat file in the resources/com/ciitizen/framework/UMLSResources
 *   directory.  If this file is not present and is not valid, an exception is thrown. 
 *   The umlsValidation.dat file is created by running the UMLSLicenseValidation program and providing
 *   a valid UMLS Terminology Services (UTS) username/password to be validated.
 * 
 * 
 * @author Guy Divita
 * @created Oct 20, 2011
 * 
 *  
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies;




public interface NGramIndexInterface {


  
  // =================================================
  /**
   * init.  The subsequent methods are looking for the arguments:
   *                      --sophiaVersion=
   *                      --terminologyServiceURL=  http://localHost:8080
   * 
   * 
   * @param pArgs  
  */
  // =================================================
   public void init(String[] pArgs) throws Exception ;
  
 
  
  // -----------------------------------------
  /**
   *load reads in the file into the gram index
   * 
   * @param gramFileName should be the relative path to the gramFileName based on what would be in the classpath
   * @throws Exception 
   */
  // -----------------------------------------
  public void load(String pGramFileName) throws Exception ;
//-----------------------------------------
  /**
   *loadConceptNameTable reads in the file into the gram index
   * 
   * @param gramFileName2
   * @throws Exception 
   */
  // -----------------------------------------
  public void loadConceptNameTable(String pConceptNameFile) throws Exception ; 
 
  // -----------------------------------------
  /**
   * get retrieves the info from the key
   * 
   * @param aQuery
   * @return String
   * @throws Exception 
   */
  // -----------------------------------------
  public String get(String pQuery) throws Exception ;
  
//-----------------------------------------
  /**
   * getConceptName retrieves the ConceptName from the cui|conceptName hash
   * 
   * @param aCui
   * @return String
   * @throws Exception 
   */
  // -----------------------------------------
  public String getConceptName(String pCui) throws Exception ;

 

  //-----------------------------------------
  /**
   * get retrieves the info from the key
   * 
   * @param pReverseKey
   * @return String
   * @throws Exception 
   */
  // -----------------------------------------
  public String get(int pNumGrams, String pReverseKey) throws Exception ;



  // =================================================
  /**
   * getCodeSystem returns the code system that was used
   * for the current lookups
   * 
   * @return String    something like UMLS  or SNOMED or IMO
  */
  // =================================================
  public String getCodeSystem();



  // =================================================
  /**
   * getCodeSystemVersion  getCodeSystemVersion returns the code system version that was used
     * for the current lookups
     * 
   * @return String    something like '2011-AA-Level0+9'  
  */
  // =================================================
  public String getCodeSystemVersion();
    
 
  
 
  // ---------------------------------------------
  // Class Variables
  // ---------------------------------------------
  

 
} // end Class NGramIndexInterface --------------------
