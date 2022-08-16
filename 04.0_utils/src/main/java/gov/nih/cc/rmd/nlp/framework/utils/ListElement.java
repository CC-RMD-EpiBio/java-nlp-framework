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
null
