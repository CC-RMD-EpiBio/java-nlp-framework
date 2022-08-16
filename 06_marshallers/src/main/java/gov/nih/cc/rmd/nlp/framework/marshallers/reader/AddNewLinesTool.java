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
// =================================================
/**
 * AddNewLinesTool adds newlines where soap note
 * sections are.
 * 
 * This class relies upon the ccdaSection lexicon
 *
 * @author     Guy Divita
 * @created    Apr 24, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.reader;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * @author divitag2
 *
 */
public class AddNewLinesTool {

  // =================================================
  /**
   * Constructor
   * @param pArgs
   * @throws Exception 
   * 
  **/
  // =================================================
  public AddNewLinesTool(String[] pArgs) throws Exception {
   
    initialize( pArgs );
  } // end Constructor() ----------------------------

  // =================================================
  /**
   * initialize
   * 
   * @param pArgs
   * @throws Exception 
  */
  // =================================================
   private void initialize(String[] pArgs) throws Exception {
   
     this.sectionNames = new HashMap<String,String>();
    readSectionNames( );
     
  } // end Method initialize() ----------------------
  
  
  // =================================================
  /**
   * readSectionNames reads in SectionNames from the
   * "resources/vinciNLPFramework/sections/ccdaSectionHeaders.lragr" 
   * @throws Exception 
   * 
  */
  // =================================================
  private void readSectionNames() throws Exception {
   
    try {
      String[] rows = U.readClassPathResourceIntoStringArray( "resources/vinciNLPFramework/sections/ccdaSectionHeaders.lragr" );
      
      
      for ( String row: rows ) {
        if ( row != null && row.trim().length() > 0 && !row.startsWith("#")) {
          String[] cols = U.split(row);
          try {
          String sectionName = cols[1];
          sectionName = sectionName.toLowerCase();
          this.sectionNames.put(sectionName, row);
          } catch (Exception e) {
            System.err.println(" " + row);
          }
        }
      }
      
      
    } catch (Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "readSectionNames", "Issue reading in resources/vinciNLPFramework/sections/ccdaSectionHeaders.lragr " + e.toString() );
     throw e;
    }
    
  } // end Method readSectionNames() ----------------


  // ----------------------
  // Global Variables
  // ----------------------
   HashMap<String, String> sectionNames = null;


  // =================================================
  /**
   * addNewLines 
   * 
   * @param pDocumentText
   * @return
  */
  // =================================================
   public String addNewLines(String pDocumentText) {
     String returnVal = null;
     
     String documentText = pDocumentText.toLowerCase();
     char[]   chars = documentText.toCharArray();
     char[]   chars2 = pDocumentText.toCharArray();
     Set<String> keys = this.sectionNames.keySet();
     
     int fromIndex = 0;
     for ( String key : keys ) {
     
      
     
       while (( fromIndex = documentText.toLowerCase().indexOf(key, fromIndex +1))  > 0 ) {
         // look closer at the document text
         //   is the first non-space character a \n, or punctuation or is it a lowercase letter
         //   if it's a lowercase letter, replace the closest space character to the fromIndex 
         //   with a newline
         if ( (chars2.length < fromIndex + key.length() ) && chars2[ fromIndex + key.length() ] == ':')
           if ( isCharsToLeftANewLineOrLowerCaseLetter( chars, fromIndex ) )
             replaceCharToLeftWithNewLine( chars2,  fromIndex);
         
         if (( fromIndex -2 >= 0 ) && 
             chars2[fromIndex -2] == ' ' && 
             chars2[fromIndex -1] == '-' &&  
             Character.isUpperCase(chars2[fromIndex]) )
           replaceCharToLeftWithNewLine( chars2,  fromIndex-1);
         
       }
     }
     
     returnVal = new String( chars2);
     
    return returnVal;
  } // end Method addNewLines() --------------------
  
  

  // =================================================
  /**
   * replaceCharToLeftWithNewLine
   * 
   * @param pText
   * @param fromIndex
  */
  // =================================================
  private void replaceCharToLeftWithNewLine(char[] pText, int fromIndex) {
    
    for ( int i = fromIndex; i> 0; i--) {
      if ( pText[i] == ' ' ) {
        pText[i] = '\n';
        break;
      }
    }
  } // end Method replaceCharToLeftWithNewLine() -----
  
  

  // =================================================
  /**
   * isCharsToLeftANewLineOrLowerCaseLetter 
   * 
   * The intent of this method is to look for clues to see
   * if the stuff to the left is the middle part of a sentence.
   * If the stuff to the left ends with a comma - it's part of a sentence
   * don't split.
   * If the stuff to the left ends in a period - it's safe to add a new section
   * if there is a word to the left that is 
   * 
   * @param pText
   * @param fromIndex
   * @return boolean
  */
  // =================================================
    private final boolean isCharsToLeftANewLineOrLowerCaseLetter(char[] pText, int fromIndex) {
    boolean returnVal = false;
    
    for ( int i = fromIndex-1; i> 0; i--) {
      // System.err.println("-->" + pText[i]);
      if ( pText[i] == '\n' ) 
        return ( false);
      else if ( pText[i] == ' ') {
       if ( i < fromIndex -1)
         return true;
      }
      else if ( pText[i] == '.') 
        if ( i < fromIndex-1 ) 
          return( true); 
        else 
          return ( false);
      else if ( Character.isLowerCase( pText[i])) 
        return (true);
      else if ( U.isNumber( pText[i])) 
        return (true);
      else if (  pText[i] == ',') 
        return (false);
      else if ( i < fromIndex -1 ) 
          return (true);
        
     
    }
    
    
    return returnVal;
  } // end Method isCharsToLeftANewLineOrLowerCaseletter

  // =================================================
  /**
   * addBTISNewLines
   *    BTRIS files could come in a format which have no
   *    newlines.
   *    
   *    If there are no newlines in the text, convert three 
   *    consecutive spaces followed by a Cap letter to
   *    two spaces followed by a newline.
   *    
   * @param documentText
   * @return String
  */
  // =================================================
  public final String addBTISNewLines(String pDocumentText) {
   
    String buff = pDocumentText ;

    int numberOfLines = U.getNewlineOffsets(buff).length;
    
    if ( numberOfLines == 0 )  {
      buff = findAndReplaceMultiSpacePattern( buff );
      buff = buff.replaceAll(BTRIS_SPACE_REGEX, BTRIS_NEWLINE_PATTERN );  
      buff = buff.replaceAll(BTRIS_BULLET_REGEX, BTRIS_BULLET_PATTERN) ;
   
      buff = addNewLines( buff );
      
    }
    
   
    
    return buff;
  } // end Method addBTISNewLines() ------------------


  // =================================================
  /**
   * findAndReplaceMultiSpacePattern finds instances
   * of lastName=1823 ] Section: Xxxxxx
   *                  /|\
   *                 put a new line here
   * @param pBuff
   * @return String  with a newline replacing the last space
  */
  // =================================================
  private final String findAndReplaceMultiSpacePattern(String pBuff) {
    String returnVal = pBuff;
    char[] buff2 = pBuff.toCharArray();
  
    
    for ( int ctr = 0; ctr <  buff2.length; ctr++ ) {
     
      if ( buff2[ctr] == ']' && buff2[ctr+1] == ' ' && Character.isUpperCase(buff2[ctr+2] ) ) {
       
        if ( ctr + 22  < buff2.length ) {
          int colonCtr = pBuff.substring( ctr+2, ctr+22 ).indexOf( ":");
          if ( colonCtr > 0) {
            buff2[ctr+1] = '\n';
          }
          
        }
      }
    }
   
  
    
    returnVal = new String( buff2);
    
    return returnVal;
  } // end Method findAndReplaceMultiSpacePattern() ---

  // =================================================
  /**
   * stripBTRISDeidentifyMarkers 
   * 
   * @param documentText
   * @return
  */
  // =================================================
  public String stripBTRISDeidentifyMarkers(String documentText) {
   
    String returnVal = documentText;
 
    
   returnVal = stripBTRISDeidentifyMarkers( returnVal, BTRIS_REDACTED_LAST_NAME );
   returnVal = stripBTRISDeidentifyMarkers( returnVal, BTRIS_REDACTED_FIRST_NAME );
   returnVal = stripBTRISDeidentifyMarkers( returnVal, BTRIS_REDACTED_MIDDLE_NAME );
   returnVal = stripBTRISDeidentifyMarkers( returnVal, BTRIS_REDACTED_ORGANIZATION );
   returnVal = stripBTRISDeidentifyMarkers( returnVal, BTRIS_REDACTED_ID );
    
   return returnVal;
  }

  // =================================================
  /**
   * stripBTRISDeidentifyMarkers 
   *    transform mentions like  [LAST_NAME i=459]  to
   *    white space
   * 
   * @param pDocumentText
   * @param pRedactedPiece
   * @return String 
  */
  // =================================================
   public final String stripBTRISDeidentifyMarkers(String pDocumentText, String pRedactedPiece) {

     String buff = pDocumentText;
     
     int lastPtr = -1;
     int deidPtr = -1;
     
     while ( (deidPtr = buff.indexOf( pRedactedPiece, lastPtr) ) > 0 ) {
       
       String before = buff.substring(0, deidPtr);
       int bracket = buff.indexOf("]", deidPtr );
       String current = whiteSpaces( bracket - deidPtr + 1 );
       String after = buff.substring(bracket + 1);
       
       buff = before + current + after;
       
       lastPtr = bracket+1;
     }
    
     return buff;
  } // end Method stripBTRISDeidentifyMarkers() ------


// =================================================
/**
 * whiteSpaces returns x number of spaces
 * 
 * @param x
 * @return String
*/
// =================================================
public static final String whiteSpaces(int x) {
  
  StringBuffer buff = new StringBuffer();
  
  for ( int i = 0; i < x; i++ )
      buff.append(' ');
  
  return buff.toString();
} // end Method whiteSpaces() -----------------------

  
  private final static String BTRIS_4SPACE_REGEX = "\\s{4,}[A-Z]{1,}[a-z]{1,}:";
  private final static String BTRIS_SPACE_REGEX = "   ";
  private final static String BTRIS_NEWLINE_PATTERN = "  \n";
  private final static String BTRIS_BULLET_REGEX = " o ";
 
  private final static String BTRIS_DASH_REGEX = " -";
  private final static String BTRIS_DASH_PATTERN = "\n-";
  private final static String BTRIS_BULLET_PATTERN = "\no ";
  private static Pattern btris4SpaceRegexPattern = Pattern.compile(BTRIS_4SPACE_REGEX);
  
  public final static String BTRIS_REDACTED_LAST_NAME = "[LAST_NAME i=";
  public final static String BTRIS_REDACTED_FIRST_NAME = "[FIRST_NAME i=";
  public final static String BTRIS_REDACTED_MIDDLE_NAME = "[MIDDLE_NAME i=";
  public final static String BTRIS_REDACTED_ORGANIZATION = "[ORGANIZATION i=";
  public final static String BTRIS_REDACTED_ID = "[ID i=";
  
  } // end Class AddNewLinesTool () ------------------
