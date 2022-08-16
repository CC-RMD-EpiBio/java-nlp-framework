// ------------------------------------------------------------
/**
 * StructuredList is a container that includes listElements. List Elements
 * contain a listDelimiter along with listContent.
 * 
 * The last seen listDelimiter is kept to determine if a new list element
 * belongs to this list or should be part of another list.
 *
 *
 * @author guy
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

// -----------------------------------
/**
 * Class Types
 *
 */
// ------------------------------------
public class StructuredList {

  

  // -----------------------------------------
  /**
   * Constructor 
   * 
   */
  // -----------------------------------------
  public StructuredList() {
    this.listElements = new ArrayList<ListElement>();
  }

  // ---------------------------------------------
  /**
   * setListElements
   *
   * @param anElement the listElements to set
   */
  // ---------------------------------------------
  public void addListElement(ListElement anElement) {
    
    if ( this.listElements != null ) 
      listElements.add( anElement);
   
    this.setLastSeenListDelimiter(anElement.getListDelimiter());
    
 
  }  // End Method setListElements() ---------------------------
  
  // ---------------------------------------------
  /**
   * setListElements
   *
   * @param listElements the listElements to set
   */
  // ---------------------------------------------
  public void setListElements(List<ListElement> listElements) {
    this.listElements = listElements;
    
    
  }  // End Method setListElements() ---------------------------
  

  // ---------------------------------------------
  /**
   * getListElements
   *
   * @return the listElements
   */
  // ---------------------------------------------
  public List<ListElement> getListElements() {
    return listElements;
    
  }  // End Method getListElements() ---------------------------
  
    
  // ---------------------------------------------
  /**
   * setLastSeenListDelimiter  (this is done internally within the addListElement method)
   *
   * @param lastSeenListDelimiter 
   */
  // ---------------------------------------------
  public void setLastSeenListDelimiter(String lastSeenListDelimiter) {
    this.lastSeenListDelimiter = lastSeenListDelimiter;
    
    
  } // End Method setLastSeenListDelimiter() ---------------------------

  // ---------------------------------------------
  /**
   * getLastSeenListDelimiter
   *
   * @return the lastSeenListDelimiter
   */
  // ---------------------------------------------
  public String getLastSeenListDelimiter() {
    return lastSeenListDelimiter;
    
    // End Method getLastSeenListDelimiter() ---------------------------
  }


  // ------------------------------------------
  /**
   * existingListOrNewList determines if the next element coming in
   * belongs to an existing structured list, or the start of another
   * list.
   *
   *
   * @param pStructuredList
   * @param pDelimiter
   * @return StructuredList
   */
  // ------------------------------------------
  public static StructuredList existingListOrNewList(StructuredList pStructuredList, String pDelimiter) {
    
    StructuredList returnVal = null;
    
    if ( pStructuredList == null ) {
      returnVal = new StructuredList();
      // System.err.println("Creating a new structured list");
    } else{
      String openListLastSeenDelimiter =  pStructuredList.getLastSeenListDelimiter();
      
      if ( nextInSequence( pDelimiter, openListLastSeenDelimiter ) ) {
        returnVal = pStructuredList;
        // System.err.println("using the same list ");
      } else { 
        returnVal = new StructuredList();
        // System.err.println("Creating a new structured list because it doesn't look like the previous one");
      }
      
    }
    return returnVal;
    
  }  // End Method existingListOrNewList() -----------------------
  


  // ---------------------------------------------
  /**
   * setInitialIndentation
   *
   * @param initialIndentation the initialIndentation to set
   */
  // ---------------------------------------------
  public void setInitialIndentation(int initialIndentation) {
    this.initialIndentation = initialIndentation;
    
    
    // End Method setInitialIndentation() ---------------------------
  }

  // ---------------------------------------------
  /**
   * getInitialIndentation
   *
   * @return the initialIndentation
   */
  // ---------------------------------------------
  public int getInitialIndentation() {
    return initialIndentation;
    
    // End Method getInitialIndentation() ---------------------------
  }




  // ------------------------------------------
  /**
   * nextInSequence  determines if the currentDelimiter, say "c."  follows the last
   * seen delimiter say "b." 
   *   The surrounding punctuation of the delimiter is removed, and if anything is left,
   *   the use of the ascii values for the last character of the delimiter is used
   *   to determine if  one follows the other directly if the delimiter is a character
   *   
   *   Bullet type delimiters such as * or - or _ or ~ and null delimiters will
   *   return true if the last seen delimiter was also a bullet/null type delimiter.  
   *
   * @param pCurrentDelimiter
   * @param pLastSeenDelimiter
   * @return
   */
  // ------------------------------------------
  private static boolean nextInSequence(String pCurrentDelimiter, String pLastSeenDelimiter ) {
  
    boolean returnVal = false;
    
    // ---------------------------
    // if the lastSeenDelimiter is empty
    if ( pLastSeenDelimiter == null || pLastSeenDelimiter.trim().length() == 0 ) {
      if ( pCurrentDelimiter == null || pCurrentDelimiter.trim().length() == 0 ) {
        returnVal = true;
      } else {
        returnVal = false;
      }
    } else if ( pCurrentDelimiter == null || pCurrentDelimiter.trim().length() == 0 ) {
      returnVal = false;
      
    } else {
      // ---------------------------
      // both the delimiters have some value
    
      String lastSeenDelimiter = U.stripPunctuation( pLastSeenDelimiter );
      String  currentDelimiter = U.stripPunctuation( pCurrentDelimiter) ;
      
     
    
      if ( lastSeenDelimiter == null || currentDelimiter == null )
        returnVal = true;
      else {
        if ( U.isNumber(lastSeenDelimiter) && U.isNumber( currentDelimiter )) {
          if ( Integer.valueOf( lastSeenDelimiter ) + 1 == Integer.valueOf( currentDelimiter))
            returnVal = true;
        } else {
          if ( lastSeenDelimiter != null ) {
            char lastSeenChar = lastSeenDelimiter.charAt(lastSeenDelimiter.length() -1);
            char currentChar = currentDelimiter.charAt(currentDelimiter.length() -1);
            if ( lastSeenChar + 1 == currentChar)
              returnVal = true;
          }
          
        }
      }
    
    
    
    }
    
    
    return returnVal;
    
  }  // End Method nextInSequence() -----------------------




  //--------------------------
  // Class Variables
  // --------------------------
  private List<ListElement> listElements = null;
  private String   lastSeenListDelimiter = null;
  private int         initialIndentation = 0;

  
} // End StructuredList Class -------------------------------
null
