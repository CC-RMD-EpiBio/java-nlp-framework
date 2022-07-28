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
 * TempToken.java  Summary [TBD]
 *
 * @author     Guy Divita
 * @created    Jun 15, 2018
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.annotator.loretta;

import org.apache.uima.jcas.tcas.Annotation;

/**
 * @author guy
 *
 */
public class TempToken {

  private int endOffset;
  private int beginOffset;
  private String coveredText;
  private Annotation realToken = null;

  // =================================================
  /**
   * Constructor
   *
   * @param pText
   * @param pBeginOffset
   * @param pEndOffset
   * 
  **/
  // =================================================
  public TempToken(String pText, int pBeginOffset, int pEndOffset) {
   
    this.coveredText = pText;
    this.beginOffset = pBeginOffset;
    this.endOffset = pEndOffset;
  } // end Constructor

  /**
   * @return the endOffset
   */
  public final int getEndOffset() {
    return endOffset;
  }

  /**
   * @param endOffset the endOffset to set
   */
  public final void setEndOffset(int endOffset) {
    this.endOffset = endOffset;
  }

  /**
   * @return the beginOffset
   */
  public final int getBeginOffset() {
    return beginOffset;
  }

  /**
   * @param beginOffset the beginOffset to set
   */
  public final void setBeginOffset(int beginOffset) {
    this.beginOffset = beginOffset;
  }

  /**
   * @return the coveredText
   */
  public final String getCoveredText() {
    return coveredText;
  }

  /**
   * @param coveredText the coveredText to set
   */
  public final void setCoveredText(String coveredText) {
    this.coveredText = coveredText;
  }

  /**
   * 
   * @param pToken
  */
  public final void setRealToken(Annotation pToken) {
    this.realToken = pToken ;
  }
  
  /**
   * 
   * @param pToken
  */
  public final Annotation getRealToken() {
    return this.realToken;
  }

}
