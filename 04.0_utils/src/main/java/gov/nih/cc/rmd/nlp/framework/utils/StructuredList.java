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
