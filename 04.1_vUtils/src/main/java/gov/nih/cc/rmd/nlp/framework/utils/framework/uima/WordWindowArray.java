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
// -------------------------------------------------------
/**
 * WordWindowArray.java
 *
 * @author Guy Divita
 * @created 12:44:56 PM
 *  
 * 
 */
// -------------------------------------------------------
package gov.nih.cc.rmd.nlp.framework.utils.framework.uima;

import gov.va.chir.model.Token;

/**
 * @author guy
 *
 */
public class WordWindowArray {

  public Token[] windowBefore;
  public Token[] windowAfter;
  // -------------------------------------------------------
  /**
   * getWindowBefore
   *
   * @return windowBefore
   */
  // -------------------------------------------------------
  public Token[] getWindowBefore() {
    return windowBefore;
  }
  // ----------------------------------
  /**
   * set Description
   *
   * @param windowBefore the windowBefore to set
   */
  // ----------------------------------
  public void setWindowBefore(Token[] windowBefore) {
    this.windowBefore = windowBefore;
  }
  // -------------------------------------------------------
  /**
   * getWindowAfter
   *
   * @return windowAfter
   */
  // -------------------------------------------------------
  public Token[] getWindowAfter() {
    return windowAfter;
  }
  // ----------------------------------
  /**
   * set Description
   *
   * @param windowAfter the windowAfter to set
   */
  // ----------------------------------
  public void setWindowAfter(Token[] windowAfter) {
    this.windowAfter = windowAfter;
  }

}
