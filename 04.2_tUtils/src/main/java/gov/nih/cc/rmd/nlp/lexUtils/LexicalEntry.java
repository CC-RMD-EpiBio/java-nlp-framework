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

/**
 * @author Guy
 *
 */
public class LexicalEntry {

  private String termId;
  private String term;
  private String pos;
  private String inflection;
  private String conceptName;
  private String category;
  private String otherInfo;

  // ==========================================
  /**
   * LexicalEntry [Summary]
   *
   */
  // ==========================================
  public LexicalEntry() {
    // TODO Auto-generated constructor stub
    // end Constructor ==========================================
  }

  // ==========================================
  /**
   * LexicalEntry [Summary]
   *
   * @param pTermId
   * @param pTerm
   * @param pPOS
   * @param pInflection
   * @param pConceptName
   * @param pCategory
   * @param pOtherInfo
   */
  // ==========================================
  public LexicalEntry(String pTermId, String pTerm, String pPOS, String pInflection, String pConceptName,
      String pCategory, String pOtherInfo) {
    
      this.termId = pTermId;
      this.term = pTerm;
      this.pos = pPOS;
      this.inflection = pInflection;
      this.conceptName = pConceptName;
      this.category = pCategory;
      this.otherInfo = pOtherInfo;
    
  } // end Constructor ==========================================

  // ==========================================
  /**
   * toLRAGRString writes out the entry in the lragr format
   *
   * @return String   termId|term|pos|inflection|conceptName|category|otherInfo .... 
   */
  // ==========================================
  public String toLRAGRString() {
   
    StringBuffer buff = new StringBuffer();
    
    buff.append( this.termId );       buff.append("|");
    buff.append( this.term);          buff.append("|");
    buff.append( this.pos);           buff.append("|");
    buff.append( this.inflection);    buff.append("|");
    buff.append( this.conceptName);   buff.append("|");
    buff.append( this.category);      buff.append("|");
    buff.append( this.otherInfo);     buff.append("|");
    
   return buff.toString();  
  } // end Method toLRAGRString() ========================================
  
  

} // end LexicalEntry
