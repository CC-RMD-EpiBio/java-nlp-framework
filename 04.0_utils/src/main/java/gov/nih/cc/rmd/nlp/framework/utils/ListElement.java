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
// ------------------------------------------------------------
/**
 * ListElement is a component of a StructuredList.  A list Element
 * contains a listDelimiter and a listContent field. 
 *
 *
 * @author Divita
 * May 31, 2013
 * 
 * ------------------------------------------------------------
 *
 *
 *
 * -------------------------------------------------------------
 */
// -------------------------------------------------------------
package gov.nih.cc.rmd.nlp.framework.utils;

import java.util.ArrayList;
import java.util.List;


public class ListElement {

  public void setListContent(String listContent) {
    this.listContent = listContent;
  }  

  public String getListContent() {
  return listContent;
  }
  

  public void setListDelimiter(String listDelimiter) {
    this.listDelimiter = listDelimiter;  
  }

  public String getListDelimiter() {
    return listDelimiter;
  }

//------------------------------------------
  /**
   * getTokens
   *  retrieves the tokens of this listElement
   *
   *
   * @return List
   */
  // ------------------------------------------
  public List<?> getTokens() {
      
    return this.tokens;
    
  }  // End Method getTokens() -----------------------
  
  
  // ------------------------------------------
  /**
   * addToken
   *   adds the individual tokens that make up 
   *   the content of this listElement.
   *
   *
   * @param token
   */
  // ------------------------------------------
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void addToken(Object token) {
    
    if ( tokens == null) {
      tokens = new ArrayList();
    }
    ((ArrayList) tokens).add(token);
   
    
  }  // End Method addToken() -----------------------
  


  // -----------------------------------
  // Class Variables
  // -----------------------------------
  private String listDelimiter = null;
  private String   listContent = null;
  private   List<?>     tokens = null;
 
  
 }  // End ListElement Class -------------------------------
