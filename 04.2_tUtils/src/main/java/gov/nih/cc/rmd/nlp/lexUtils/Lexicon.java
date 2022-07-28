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
/**
 * 
 */
package gov.nih.cc.rmd.nlp.lexUtils;

import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * @author Guy
 *
 */
public class Lexicon {

 
  // ==========================================
  /**
   * Lexicon 
   * 
   * @param pFilePath
   * @throws Exception
   *
   */
  // ==========================================
  public Lexicon( String pFilePath) throws Exception{
    this.lragrRows = new ArrayList<LexicalEntry>();
    this.filePath = pFilePath;
     
    this.fruitfulVariants = new FruitfulSynonyms();
 
  
  } // end Constructor ========================
  

  // ==========================================
  /**
   * addTermInflectionsAndDerivationsVariants
   *
   * @param pTermId
   * @param pTerm
   * @param pPOS
   * @param pInflection
   * @param pConceptName
   * @param pCategory
   * @param pOtherInfo
   * @throws Exception 
   */
  // ==========================================
  public void addTermVariants(String pTermId, String pTerm, String pPOS, String pInflection, String pConceptName, String pCategory, String pOtherInfo) throws Exception {

    LexicalEntry entry = new LexicalEntry( pTermId, pTerm, pPOS, pInflection, pConceptName, pCategory, pOtherInfo);
    this.lragrRows.add( entry);
    
    String[] terms = new String[1];
    terms[0] = pTerm;
    String[] variants = this.fruitfulVariants.generateVegetableVariants(terms);
    
    if ( variants != null && variants.length > 0 ) {
      for ( String variant: variants ) {
        System.err.println(variant);
        String[] cols = U.split(variant);
        System.err.println(variant);
        String term = cols[0];
        String pos = cols[1];
        String inflection = cols[2];
        
        LexicalEntry variantEntry = new LexicalEntry( pTermId, term, pos, inflection, pConceptName, pCategory, pOtherInfo);
        this.lragrRows.add( variantEntry);
                
      } // end loop thru variants
    } // end if there are variants
    
  } // end Method addTermAndSinglePluralVariants() ==
  

  // ==========================================
  /**
   * save 
   *
   */
  // ==========================================
  public void save() throws Exception {
   
    try {
      PrintWriter out = new PrintWriter( this.filePath);
    
      // loop thru the lragr rows and push them to the file
      for ( LexicalEntry entry: this.lragrRows ) {
        out.print(entry.toLRAGRString());
        out.print("\n");
      }
    
      out.close();
      
    } catch (Exception e ) {
      e.printStackTrace();
      System.err.println("Issue with writing the " + this.filePath + " " + e.toString());
      throw e;
    }
  } // end Method save() ======================


  // ==========================================
  /**
   * addTermInflectionsAndDerivationsVariants assumes the input term is a noun and in singular form
   *
   * @param pTermId
   * @param pTerm
   * @param pConceptName
   * @param pCategory
   * @param pOtherInfo
   * @throws Exception 
   */
  // ==========================================
  public void addTermInflectionsAndDerivationsVariants(String pTermId, 
                                                       String pTerm, 
                                                       String pConceptName, 
                                                       String pCategory,
                                                       String pOtherInfo) throws Exception {
  
    
   addTermVariants( pTermId,  pTerm, "noun", "base+singular", pConceptName, pCategory,pOtherInfo);
   
    
  }// end Method addTermAndSinglePluralVariants() 
  

  // ============================
  // Global Variables
  // ============================
  private ArrayList<LexicalEntry> lragrRows = null;
  private String filePath = null;
  private FruitfulSynonyms fruitfulVariants = null;


} // end Class Lexicon
