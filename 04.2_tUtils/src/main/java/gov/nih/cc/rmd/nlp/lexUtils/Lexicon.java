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
