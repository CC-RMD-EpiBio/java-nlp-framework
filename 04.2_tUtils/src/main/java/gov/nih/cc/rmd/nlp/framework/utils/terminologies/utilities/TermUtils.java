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
 * TermUtils includes tokenizing and normalizing utilities used throughout the framework.
 * 
 * tokenize takes text and tokenizes it into space bounded tokens.  This method
 * follows the same guidelines as the gov.nih.cc.rmd.framework.Word class,
 * but only makes tokens of type string, not Tokens from the chir model. This class
 * throws out whitespace. This method is used to make keys for indexes.
 * 
 * normalize takes a multi-word term, reverses the tokens within it, replaces
 * punctuation with spaces to create a reverseKey.
 * 
 *
 *
 * @author  Guy Divita 
 * @created Feb 21, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.acronym.Acronyms;

public class TermUtils {

   
  // -----------------------------------------
  /**
   * Constructor for tokenizeSimple.  This
   * constructor requires the URL of the knownAcronym
   * resource.  This should be in resources/Tokenizer/knownAcronyms.txt
   * and should be references in parameter of a config as "knownAcronymsURL".
   * 
   * @param pKnownAcronymsPath
   * 
   */
   // ---------------------------------------
   public TermUtils( String pKnownAcronymsPath ) throws Exception {
     
     initialize(pKnownAcronymsPath );
     
   } // end Constructor() --------------------


   // -----------------------------------------
   /**
    * Constructor for tokenizeSimple.  This
    * constructor assumes that the file 
    *  resources/Tokenizer/knownAcronyms.txt exists
   
    */
    // ---------------------------------------
    public TermUtils( ) throws Exception {
      
      String knownAcronymsPath = "resources/vinciNLPFramework/tokenizer/knownAcronyms.txt";
      
      initialize(knownAcronymsPath );
      
    } // end Constructor() --------------------
   
  // -----------------------------------------
  /**
   * tokenize returns a List of non whitespace tokens given a string.  
   * This uses a finite state automata through
   * each character of the text, accumulating tokens as token barriers
   * are hit.  
   * 
   * Token barriers include space tokens. 
   * 
   * There is a small amount of repair done to mark punctuation that
   * is surrounded by whitespace, and punctuation that is at the end
   * of a non white-space token.
   * 
   * @param pText
   * @return List<String>
   * 
   */
  // -----------------------------------------
  public List<String> tokenize(String pText) throws Exception {
    
   
    ArrayList<String> tokens = new ArrayList<String>(10);

    int currentChar = 0;
    StringBuffer buff = new StringBuffer();
    StringBuffer whiteSpaceBuff = new StringBuffer();
    int punctBeginChar = 0;
    
  
  
   
    String restOfString = null;
    StringBuffer punctBuff = new StringBuffer();
    
    
    char[] charArray = pText.toCharArray();
    // -------------------------------------
    // Loop through characters of the string
    // -------------------------------------
    for (int i = 0; i < charArray.length; i++) {
      
      char xxx = charArray[i];
      // ---------------
      // Rest of the string (needed for right look-ahead)
      if ( i+1 < charArray.length)
        restOfString = pText.substring(i+1);
      else
        restOfString = null;
        
      // -----------------------------
      // Character hit
      // -----------------------------
      if (!Character.isWhitespace(charArray[i])) {
        if ((whiteSpaceBuff != null) && (whiteSpaceBuff.length() > 0)) {
          whiteSpaceBuff = new StringBuffer();
        }
        
       // old code buff.append(charArray[i]);
        
        // --------------------------------------------
        // if the token before was a number and the current token is 
        // indicative of time, a fraction, an equation, listChar, or a reference  ie.  {3 or (3  
        if          ( i > 1                    && U.isNumber(charArray[i-1])   &&  canBeTimeFractionEquationListMarkerOrReference( charArray[i]) ) {
          
          buff.append(charArray[i]);
         
        // --------------------------------------------
        // if the token after is a number and the current token is
        // indicative of time, polarity, an equation, listMarker, or a reference
        } else if ( i + 1 < charArray.length &&  U.isNumber(charArray[i+1])   && canBeTimePolarityFractionEquationListMarkerOrReference( charArray[i]) ) {
         
          buff.append(charArray[i]);
        
        // ----------------------------------------------
        // if the token before and after are numbers
        // and the character is indicative of a decimal or formatted number
        } else if (charArray[i] == '.'  && i > 1   &&  i + 1 < charArray.length && U.isNumber(charArray[i+1]) ||    //   1.00  part of a number  
                   charArray[i] == ','  && i > 1   &&  i + 1 < charArray.length && U.isNumber(charArray[i+1]) ) {  //   1,000 part of a number (could need more rules)                      
       
          buff.append(charArray[i]);
          
          
        // -----------------------------------
        // if you have a letter list marker <space> a)) 
        } else if ( charArray[i] == ')' && i -2 >= 0 && i+1 < charArray.length && U.isWhiteSpace(charArray[i-2]) && U.isWhiteSpace( charArray[i+1])   )  {
         
          buff.append(charArray[i]);

          
        // -------------------------------------
        // normal case = at a char [A-z]
        } else if ( ( charArray[i] >= 'A' && charArray[i] <= 'Z' ) || (charArray[i] >= 'a' && charArray[i] <=  'z' ) ) {
          
      
         
          buff.append(charArray[i]);
          
        } else if ( charArray[i] == ')'   && i > 0  && charArray[i-1] == 's'  ) {
          buff.append(charArray[i]);
            
        } else if ( charArray[i]!= ':'   &&  
                //  charArray[i]!= '/'   &&  
                    charArray[i]!= '\\'  &&  
                    charArray[i]!= '\''  && // <----- should have broken up Parkenson's but is not
                    charArray[i]!= '"'   && // <----- should have broken up air quotes
                    charArray[i]!= '`'   && 
                    charArray[i]!='('    && 
              //    charArray[i]!=')'    &&  //  <--------when commented out--- picks up list markers but mis-tokenizes (s) as s)  
                    charArray[i]!= '['   &&
                    charArray[i]!= ']'   && 
                    charArray[i]!='{'    && 
                    charArray[i]!= '}'   &&
                    charArray[i]!= '*'   &&
                    charArray[i]!= ','   && 
                    charArray[i]!= '-'   &&
                    charArray[i]!= '>'   ) { 
                //    charArray[i]!= '.'   &&      
                 //       (  !isBreakChar(charArray[i]))) {
               //    !pBreakOnBreakChars || !isBreakChar(charArray[i])) {
       
         
          buff.append(charArray[i]);
          
        
        } else {
          
          dealWithPunctuationTokens( punctBuff, 
              punctBeginChar, 
              currentChar, 
              charArray,
              i,
              buff, 
              tokens,
              restOfString
             
              
              );
          
          buff = new StringBuffer();
        

        }
        
        
        
        
        
        
      // -----------------------------
      // Whitespace hit
      // -----------------------------
      } else { 

       
        whiteSpaceBuff.append(charArray[i]);

        // make a token from the accumulated token chars
        if ((buff != null) && (buff.length() > 0)) {
          String tokenAnnotations[] = addTokens(buff.toString(), restOfString);

          for (int it = 0; it < tokenAnnotations.length; it++){
            tokens.add(tokenAnnotations[it]);
           
          }
          buff = new StringBuffer();
        }

      }

      currentChar++;
    } // end loop through characters of the line

    // end if there is anything left
    if ((buff != null) && (buff.length() > 0)) {
      tokens.add( buff.toString());
    }
    if ((whiteSpaceBuff != null) && (whiteSpaceBuff.length() > 0)) {
      tokens.add(  whiteSpaceBuff.toString());
    }
        
    
    
    return (tokens);
    
  } // end Method tokenize() --------------------------
  
  


  // ==========================================
  /**
   * dealWithPunctuationTokens 
   *
   */
  // ==========================================
  private void dealWithPunctuationTokens( StringBuffer punctBuff, 
                           int pPunctBeginChar, 
                           int currentChar, 
                           char[] charArray,
                           int i,
                           StringBuffer buff, 
                           ArrayList<String> tokens,
                           String restOfString
                           ) {
    
    
    // -------------------------------------
    // create a buffer for punctuation chars
    // -------------------------------------
    punctBuff.append( charArray[i]);

    // make a token from the accumulated token chars
    if ((buff != null) && (buff.length() > 0)) {
      String tokenAnnotations[] = addTokens( buff.toString(),restOfString);

      for (int it = 0; it < tokenAnnotations.length; it++) {
        tokens.add(tokenAnnotations[it]);
       
      }
      buff = new StringBuffer();
    }
    
 // make a token from the accumulated punct chars
    if ((punctBuff != null) && (punctBuff.length() > 0)) {
      String tokenAnnotations[] = addTokens( punctBuff.toString(),restOfString);

      for (int it = 0; it < tokenAnnotations.length; it++) {
        tokens.add(tokenAnnotations[it]);
       
      }
      punctBuff = new StringBuffer();
    }
    
    /*
    
    
    // -------------------------------------
    // create a buffer for punctuation chars
    // -------------------------------------
    int punctBeginChar = pPunctBeginChar;
    if ( punctBuff.length() == 0) 
      punctBeginChar = currentChar;
    punctBuff.append( charArray[i]);

    // make a token from the accumulated token chars
    if ((buff != null) && (buff.length() > 0)) {
      Token tokenAnnotations[] = addTokens(pJCas, beginChar, buff.toString(), lastToken);

      for (int it = 0; it < tokenAnnotations.length; it++) {
        tokens.add(tokenAnnotations[it]);
        lastToken = tokenAnnotations[it];
      }
      buff.setLength(0);
    }
    
 // make a token from the accumulated punct chars
    if ((punctBuff != null) && (punctBuff.length() > 0)) {
      Token tokenAnnotations[] = addTokens(pJCas, punctBeginChar, punctBuff.toString(), lastToken);

      for (int it = 0; it < tokenAnnotations.length; it++) {
        tokens.add(tokenAnnotations[it]);
        lastToken = tokenAnnotations[it];
      }
      punctBuff.setLength(0);
    }
    
    */
  } // end Method dealWithPunctuationTokens() ========================================
  
  
  
//-----------------------------------------
 /**
  * isBreakChar returns true if a word separating character has been hit.
  * 
  * These include slashes and backslashes, Plus
  * 
  * @param c
  * @return
  */
 // -----------------------------------------
 public static boolean isBreakChar(char c) {

   boolean returnVal = U.containsTokenSplittingChar( c);
   
   return returnVal;
 }

// ----------------------------------------------
  /**
   * sentenceTokenize returns an array of String, one for each sentence [TBD]
   * 
   * @param docString
   * @return String[]
   */
  // ----------------------------------------------
  public List<String> sentenceTokenize(String docString) {
   ArrayList<String> sentences = null;
  
  
   return sentences;
  } // end Method sentenceTokenize

  

  // -----------------------------------------
  /**
   * normalize will create a key out of the term
   * The key is in reverse order, lowercased, and punctuation is
   * replaced by spaces. 
   * @param pTerm
   * @return
   */
  // -----------------------------------------
  public String normalize(String pTerm) throws Exception {
    StringBuffer key = new StringBuffer();
    
    List<String> tokens = tokenize( pTerm);
    
    for ( int tokenCtr = tokens.size() -1; tokenCtr >= 0 ; tokenCtr--) {
      
      if ( !U.isOnlyPunctuation( tokens.get( tokenCtr)) ) {
        key.append( tokens.get(tokenCtr) );
        if ( tokenCtr > 0 )
          key.append(" ");
      } else {
        key.append(" ");
      }
    }
    
    return key.toString().trim().toLowerCase();
  } // end Method key() ----------------------
  
  

  // -----------------------------------------
  /**
   * normalizeNotReverse will create a key out of the term
   * The key is in regular (left-to-right) order, lowercased, and punctuation is
   * replaced by spaces. 
   * @param pTerm
   * @return
   */
  // -----------------------------------------
  public String normalizeNotReverse(String pTerm) throws Exception {
    StringBuffer key = new StringBuffer();
    
    List<String> tokens = tokenize( pTerm);
    
    
    for ( int tokenCtr = 0;  tokenCtr < tokens.size();   tokenCtr++) {
      
      //if ( !U.isOnlyPunctuation( tokens.get( tokenCtr)) ) {
      
        key.append( tokens.get(tokenCtr) );
        key.append(" ");
     // } else {
      //  key.append(" ");
      //}
    }
    
    
    return key.toString().trim();
  } // end Method key() ----------------------
  
  //----------------------------------
  /**
   * initialize loads in the resources needed for tokenization. Currently, this involves
   * a list of acronyms that might show up at the end of a sentence.
   *
   * @param pKnownAcronymsPath
   * 
   **/
  // ----------------------------------
  public void initialize(String pKnownAcronymsPath ) throws Exception {
    this.acronyms = new Acronyms( pKnownAcronymsPath);

  
  } // end Method initialize() --------------

// -----------------------------------------
  /**
   * main
   * 
   * @param args
   */
  // -----------------------------------------
  public static void main(String[] args) {
    
    String fileName = null;
    try {
      fileName = "./resources/vinciNLPFramework/tokenizer/knownAcronyms.txt";
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    try {
      TermUtils tokenizer = new TermUtils( fileName );
      
      String pText = getDocument("/data/inputAndOutput/input/hardTokens.txt");
      System.err.println( pText);
      
      List<String> tokens = tokenizer.tokenize(pText);
      
      if (tokens != null)
        for ( Iterator<String> i = tokens.iterator(); i.hasNext(); ) {
          GLog.println(GLog.STD___LEVEL,  "|" + i.next() + "|");
        } // end loop though tokens 
      
    }
    catch (Exception e) {
      System.err.println("could create the tokenizer " + e.toString());
      e.printStackTrace();
    }
  }

  //-----------------------------------------
  /**
   * addTokens returns TokenAnnoations from the begin char position,
   * the string and the CAS. This method sets token specific features.
   * This method might return subclasses of tokens including whitespace, and punctuation
   * 
   * This method tokenizes into multiple tokens
   *       symmetric surrounding  punctuation, 
   *       trailing punctuation (Except when the trailing punctuation is part of an acronym)
   *                             Or if the trailing punctuation is a %
   *       leading punctuation (except for $ +, -, @, # )
   * This method keeps as one token list headers such as 1. 2. 3.
   * 
   * @param pValue
   * @return Token[]
   */
  // -----------------------------------------
  private String[] addTokensOld( String pValue) {
 
    
    String[] tokens = null;
    
    if ((pValue != null) && (pValue.length() > 0)) {
      char firstChar = pValue.charAt(0);
      char lastChar = pValue.charAt(pValue.length() - 1);
      char secondToLastChar = 'X';
      String firstString = pValue.substring(0, 1);
      String middleString = firstString;
      String lastString   = firstString;
      String rightTrimmedString = firstString;
      String leftTrimmedString = firstString;
      if ( pValue.length() > 1) secondToLastChar = pValue.charAt( pValue.length() -2);
      
      if ( pValue.length() > 1) {
        middleString = pValue.substring(1, pValue.length() - 1);
        lastString = pValue.substring(pValue.length() - 1, pValue.length());
        rightTrimmedString = pValue.substring(0, pValue.length() - 1);
        leftTrimmedString = pValue.substring(1);
      }
      
      // ------------------------------------------------
      // all punctuation tokens. Stand alone sentence break markers
      // will trigger the sentence break flag to be set.
      // -----------------------------------------------
      if ( U.isOnlyPunctuation( pValue)) {
        tokens = new String[1];
        // boolean sentenceBreakMarker =  u.isSentenceBreakChar(lastChar);
        tokens[0] = pValue ;
        
      // -----------------------------------------------
      // symmetric surrounding punctuation <> {} "" '' []
      // -----------------------------------------------
      } else if ( (pValue.length() > 2) && 
           (U.isPunctuation(firstChar)) && 
           (!U.isOnlyPunctuation(middleString)) && 
           (U.isPunctuation(lastChar)) &&
           (lastChar != '.') && 
           (firstChar != '.') ) {
        
        tokens = new String[3];
        tokens[0] = firstString;
        tokens[1] = middleString;
        tokens[2] = lastString ;
      
        // -----------------------------------------------
        // more than one trailing punctuation 
        // -----------------------------------------------
      } else if (( pValue.length() > 1) && U.isPunctuation(secondToLastChar) && U.isPunctuation(lastChar) ) {
        
        tokens = addLeadingAndTrailingPuctuationTokens( pValue);
        
          
      // -----------------------------------------------
      // small numbers with a trailing period (numbered lists)
      // -----------------------------------------------
      } else if (( rightTrimmedString.matches("\\d+") ) && ( rightTrimmedString.length() < 3)  && (lastChar == '.') ) { 
        tokens = new String[1];
        tokens[0] =  pValue;
        
      // -----------------------------------------------
      // trailing punctuation but is not an acronym
      //     These are also being marked as possible sentence boundaries.
      //     Contractions and possessives are kept as part of the token
      // -----------------------------------------------
      } else if ((pValue.length() > 1) && (U.isPunctuation(lastChar)) && 
                    (lastChar != '%') && 
                    (lastChar != '+') && 
                    (lastChar != '-') && 
                    (lastChar != '\'') && (!this.acronyms.isAcronym(pValue, ""))) {       
        tokens = new String[2];
        tokens[0] =  rightTrimmedString;
        
        // ----------------------------------------------
        // In a future iteration, there should be a configurable
        // option to use colons and semi-colons as sentence breaks
        // ----------------------------------------------
        boolean sentenceBreakMarker = U.isSentenceBreakChar( lastChar);
        if ( !sentenceBreakMarker )
          tokens[1] = lastString;
        // -----------------------------------------------
        // Leading punctuation (except $ + - $ #)
        // -----------------------------------------------
      } else if (U.isPunctuation(firstChar) && ("+-$#".indexOf(firstChar) == -1)) {
        
        tokens = new String[2];
        tokens[0] = firstString;
        tokens[1] = leftTrimmedString;
        // -----------------------------------------------
        // Just a normal token
        // -----------------------------------------------
      } else {
        
        tokens = new String[1];
        tokens[0] =  pValue;
      }
    } // end if any valid string passed in
    return tokens;
  } // end Method addTokens() ---------------
    
  //-----------------------------------------
  /**
   * addTokens returns TokenAnnoations from the begin char position,
   * the string and the CAS. This method sets token specific features.
   * This method might return subclasses of tokens including whitespace, and punctuation
   * 
   * This method tokenizes into multiple tokens
   *       symmetric surrounding  punctuation, 
   *       trailing punctuation (Except when the trailing punctuation is part of an acronym)
   *                             Or if the trailing punctuation is a %
   *       leading punctuation (except for $ +, -, @, # )
   * This method keeps as one token list headers such as 1. 2. 3.
   * 
   
   * @param pValue
   * @return String[]
   */
  // -----------------------------------------
    private String[] addTokens( String pValue, String pRest) {
      
    
    String[] tokens = null;

   
    if ((pValue != null) && (pValue.length() > 0)) {
      char firstChar = pValue.charAt(0);
      char lastChar = pValue.charAt(pValue.length() - 1);
      String charsToTheRight = null;
      
      int peek =  10;
      if (pRest == null ) { 
        peek = 0;
      } else if (peek > pRest.length() ) {
        peek = pRest.length(); 
        charsToTheRight = pRest.substring(0, peek);
      }

  
      char secondToLastChar = 'X';
      String firstString = pValue.substring(0, 1);
      String middleString = firstString;
      String lastString   = firstString;
      String rightTrimmedString = firstString;
      String leftTrimmedString = firstString;
      if ( pValue.length() > 1) secondToLastChar = pValue.charAt( pValue.length() -2);

      if ( pValue.length() > 1) {
        middleString = pValue.substring(1, pValue.length() - 1);
        lastString = pValue.substring(pValue.length() - 1, pValue.length());
        rightTrimmedString = pValue.substring(0, pValue.length() - 1);
        leftTrimmedString = pValue.substring(1);
      }

      if  (this.acronyms == null) throw new RuntimeException("null acronyms?");
      // ------------------------------------------------
      // all punctuation tokens. Stand alone sentence break markers
      // will trigger the sentence break flag to be set.
      // -----------------------------------------------
      if ( U.isOnlyPunctuation( pValue)) {
        tokens = new String[1];
       // boolean sentenceBreakMarker =  U.isSentenceBreakChar(lastChar);
        tokens[0] =  pValue;

        // -----------------------------------------------
        // symmetric surrounding punctuation <> {} "" '' []
        // -----------------------------------------------
      } else if ( (pValue.length() > 2) && 
          (U.isPunctuation(firstChar)) && 
          (!U.isOnlyPunctuation(middleString)) && 
          (U.isPunctuation(lastChar)) &&
          (lastChar != '.') && 
          (firstChar != '.') ) {

        tokens = new String[3];
        tokens[0] = firstString;
        tokens[1] = middleString;
        tokens[2] = lastString;


        // -----------------------------------------------
        // more than one trailing punctuation 
        // -----------------------------------------------
      } else if (( pValue.length() > 1) && U.isPunctuation(secondToLastChar) && U.isPunctuation(lastChar) ) {

        tokens = addLeadingAndTrailingPuctuationTokens( pValue);


        // -----------------------------------------------
        // LIST Markers:
        // small numbers with a trailing period (numbered lists)  1. 1) 
        // -----------------------------------------------
      } else if ( (( rightTrimmedString.matches("\\d+") )         && ( rightTrimmedString.length() < 3)  && ((lastChar == '.') || (lastChar == '*') || (lastChar == ')' ) )) || 
                  (( rightTrimmedString.matches("[A-Z]"))         && ( rightTrimmedString.length() < 3)  && ((lastChar == '.') || (lastChar == '*') || (lastChar == ')' ) )) ||
                  (( rightTrimmedString.equals("A"))              && ( rightTrimmedString.length() < 3)  && ((lastChar == ')') )) ||
                  (( rightTrimmedString.matches("[a-z]"))         && ( rightTrimmedString.length() < 3)  && ((lastChar == '.') || (lastChar == '*') || (lastChar == ')' ) )) ){
          
        tokens = new String[1];
       

        tokens[0] = pValue;

       
        // -----------------------------------------------
        // trailing punctuation but is not an acronym
        //     These are also being marked as possible sentence boundaries.
        //     Contractions and possessives are kept as part of the token
        //     The isAcronym method needs more info to make the decision -
        //     if the character to the right start with a capital
        //     or if the next chars start with a new line.

        // -----------------------------------------------
      } else if ((pValue.length() > 1) && (U.isPunctuation(lastChar)) && 
          (lastChar != '%') && 
          (lastChar != '+') && 
          (lastChar != ')') &&
          (lastChar != '-') && 
          (lastChar != '\'') && (!this.acronyms.isAcronym(pValue, charsToTheRight))) {

        tokens = new String[2];
        tokens[0] =  rightTrimmedString;

        // ----------------------------------------------
        // In a future iteration, there should be a configurable
        // option to use colons and semi-colons as sentence breaks
        // ----------------------------------------------
       // boolean sentenceBreakMarker = U.isSentenceBreakChar( lastChar);

        tokens[1] =  lastString;
        // -----------------------------------------------
        // Leading punctuation (except $ + - $ #)
        // -----------------------------------------------
      } else if (U.isPunctuation(firstChar) && ("+-$#".indexOf(firstChar) == -1)) {
        // System.err.println( pValue + "|6");
        tokens = new String[2];
        tokens[0] = firstString;
        tokens[1] = leftTrimmedString;
        // -----------------------------------------------
        // Just a normal token
        // -----------------------------------------------
      } else {
        // System.err.println( pValue + "|7");
        tokens = new String[1];
        tokens[0] =  pValue;
      }
    } // end if any valid string passed in
    return tokens;
  } // end Method addTokens() ---------------

  
  
  
  // -----------------------------------------
/**
 * addLeadingAndTrailingPunctionTokens returns the tokens for a string
 * that includes multiple trailing punctuation and possible leading punctuation.
 * 
 * This method accounts for multiple punctuation at the beginning of the string as well.
 *
 * @param pValue
 * @return
 */
// -----------------------------------------
private String[] addLeadingAndTrailingPuctuationTokens(String pValue) {
  
    boolean sentenceBreak = false;
    String tokens[] = null;
    Stack<String> endTokenStack = new Stack<String>();
    int z = 0;
    for (z = pValue.length() - 1; z >= 0; z--) {
      char zChar = pValue.charAt(z);
      if (U.isPunctuation(zChar)) {
        StringBuffer zString = new StringBuffer();
        zString.append(zChar);
        sentenceBreak = false;
        if (( z == pValue.length() - 1) && (U.isSentenceBreakChar( zChar ))) 
          sentenceBreak = true;
        if ( !sentenceBreak )
        endTokenStack.push( zString.toString());
      } else {
        break;
      }
    } // end loop through tail end string
    
   
    
    // -------------------------------------------------
    // Handle punctuation at the beginning of the string,
    // if the whole string is not punctuation
    // -------------------------------------------------
    int finalI = 0;
    if (z > 0) {
      ArrayList<String> beginTokenStack = new ArrayList<String>();
      int a = 0;
      for (a = 0; a < pValue.length(); a++) {
        char aChar = pValue.charAt(a);
        if (U.isPunctuation(aChar)) {
          StringBuffer aString = new StringBuffer();
          aString.append(aChar);
          beginTokenStack.add( aString.toString());
        } else {
          break;
        }
      } // end loop through front end of the stringstring
      
      
      String middle = pValue.substring(a, z + 1);
     
      
      int numberOfTokens = beginTokenStack.size() + endTokenStack.size() + 1;
      tokens = new String[numberOfTokens];
   
      for (int bi = 0; finalI < beginTokenStack.size(); bi++)
        tokens[finalI++] = beginTokenStack.get(bi);
      tokens[finalI++] = middle;
    } else {
      tokens = new String[endTokenStack.size()];
    }
    while (!endTokenStack.empty())
      tokens[finalI++] = endTokenStack.pop();
  
  return tokens;
} // end Method addLeadingAndTrailingPuctuationTokens() ----

 

// =======================================================
/**
 * canBeTimePolarityFractionEquationListMarkerOrReference returns true
 * if the char is one of the following 
 * 
 *    c == ':'   ||    //   :00  end of time
 *    c == '/'   ||    //   /1    fractions or list header
 *    c == '\\'  ||    //   \2 <--- not sure but would want this to stick together
 *    c == '-'   ||    //   -0  negative
 *    c == '+'   ||    //   +1  positive
 *    c == '('   ||    //   (0  beginning of an equation 
 *    c == ')'   ||    //   )3  list marker or end of an equation
 *    c == '['   ||    ///  [3  begin a reference
 *    c == ']'   ||    //   ]3  ?? 
 *    c == '{'   ||    //   {3  begin a reference
 *    c == '}'   ||    //   }1  ?? 
 *    c == '*'    )    //   1*  part of an equation or it's a list marker
 *   
 * @param c
 * @return boolean
 */
// =======================================================
public static boolean canBeTimePolarityFractionEquationListMarkerOrReference(char c) {
  
  boolean returnVal = false;
  
  if (c == ':'   ||    //   :00  end of time
      c == '/'   ||    //   /1    fractions or list header
      c == '\\'  ||    //   \2 <--- not sure but would want this to stick together
      c == '-'   ||    //   -0  negative
      c == '+'   ||    //   +1  positive
      c == '('   ||    //   (0  beginning of an equation 
      c == ')'   ||    //   1)  list marker or end of an equation
      c == '['   ||    //   1[   naaa
      c == ']'   ||    //   1]  end of a reference 
      c == '{'   ||    //   1{  naaa  shouldn't happen
      c == '}'   ||    //   1}  end of a references 
      c == '*'    )    //   1*  part of an equation or it's a list marker
    
    returnVal = true;
    
  return returnVal;
} // End Method canBeTimePolarityFractionEquationListMarkerOrReference() ======================


// =======================================================
/**
 * canBeTimeFractionEquationListMarkerrOrReference returns true if c is one
 * of these characters
 * 
 *   c == ':'   ||    //   11:   beginning of time 
 *   c == '/'   ||    //   fractions
 *   c == '\\'  ||    //   1\2 <--- not sure but would want this to stick together
 *   c == '-'   ||    //   1-  begin of an equation 
 *   c == '+'   ||    //   10+ begin of an equation 10+1  | positive 10+
 *   c == '('   ||    //   1(  ?? 
 *   c == ')'   ||    //   1)  end of an equation, or it's a list marker
 *   c == '['   ||    //   1[ ??
 *   c == ']'   ||    //   0]  end of a reference
 *   c == '{'   ||    //   3{  ?? 
 *   c == '}'   ||    //   9}  end of a reference {1} or it's a list marker
 *   c == '*'         //   9*  part of an equation or list marker
 * 
 * @param c
 * @return boolean
 */
// =======================================================
public static boolean canBeTimeFractionEquationListMarkerOrReference( char c) {
  
  boolean returnVal = false;
  
 if (c == ':'   ||    //   11:   beginning of time 
     c == '/'   ||    //   fractions
     c == '\\'  ||    //   1\2 <--- not sure but would want this to stick together
     c == '-'   ||    //   1-  begin of an equation 
     c == '+'   ||    //   10+ begin of an equation 10+1  | positive 10+
  // c == '('   ||    //   0(    naaa 
     c == ')'   ||    //   1)  end of an equation, or it's a list marker
  // c == '['   ||    //   0[    naaa
     c == ']'   ||    //   0]  end of a reference
  // c == '{'   ||    //   0{    naaa
     c == '}'   ||    //   9}  end of a reference {1} or it's a list marker
     c == '*'  )     //   9*  part of an equation or list marker
     returnVal = true;
   
  return returnVal;
} // End Method canBeTimeFractionEquationListCharOrReference() ======================



  // -----------------------------------------
  /**
   * getDocument returns the text from the filename passed in
   * 
   * @param pFileName
   * @return String
   */
  // -----------------------------------------
  private static String getDocument(String pFileName ) {
   
    StringBuffer buff = new StringBuffer();
    BufferedReader in = null;
    String       line = null;
    try {
     in = new BufferedReader(new FileReader(pFileName));
  
     while ((line = in.readLine()) != null) 
       buff.append(line + "\n");
      in.close();
    } catch ( Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Something wrong: " + e.toString());
    }
      
    return buff.toString();
  } // end getDocument() ----------------------

  //---------------------------------------------
  // Class Variables
  // ---------------------------------------------
  private Acronyms                      acronyms = null; // acronyms utilities

  

  
} // end Class TermUtils() ------------------
