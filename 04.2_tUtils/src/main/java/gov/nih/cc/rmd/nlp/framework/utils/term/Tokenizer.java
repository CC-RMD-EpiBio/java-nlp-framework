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
 * Token Annotator Interface Implementation of the VINCI NLP tokenizer.
 * This is a quick dirty tokenizer.  This uses heuristics that are found
 * in the SPECIALIST textTools tokenizer, the acronym list from that tokenizer
 * and acronyms from the hitex tokenizer.  
 * The tokenizer chunks strings by whitespace, sentence trailing 
 * punctuation, surrounding punctuation.  It keeps intact hyphened words,
 * possessives (both 's and items ending with ').  The tokenizer keeps leading
 * punctuation intact for tokens that start with $ or & or @. 
 * This tokenizer uses the gov.chir.model tagset. 
 * 
 * 
 * @author Guy Divita
 * @created Jan 14, 2011
 * 
 *  
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils.term;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.acronym.Acronyms;



public class Tokenizer  {
  
  
// =======================================================
  /**
   * Constructor Tokenizer 
   * @throws Exception 
   *
   */
  // =======================================================
  public Tokenizer() throws Exception {
    initialize();
  }


//------------------------------------------
 /**
  * tokenize processes strings into tokens, and breaking
  * on breakchars 
  *
  *
  * @param pDocText
  * @param pBeginChar
  * @return List<Token> (including whitespace tokens)
  * 
  */
 // ------------------------------------------
 public List<Token> tokenize( String pDocText ) {
   
  return tokenize( pDocText, 0, true);
   
 } // end Method tokenize() ------------------
  
 
//------------------------------------------
/**
 * wordTokenize processes strings into tokens, and breaking
 * on breakchars.
 *
 *
 * @param pDocText
 * @param pBeginChar
 * @return List<String> of wordTokens
 * 
 */
// ------------------------------------------
public List<String> wordTokenize( String pDocText ) {
  
 List<Token> tokens = tokenize( pDocText, 0, true);
 ArrayList<String>finalTokens = null;

 
 if ( tokens != null && tokens.size() > 0) {
   finalTokens = new ArrayList<String>(tokens.size());
   for ( int i = 0; i < tokens.size(); i++ ) {
     if( tokens.get(i).getDisplayString().trim().length() > 0)
       finalTokens.add( tokens.get(i).getDisplayString());
   }
  
 }
 
 
 return finalTokens;
  
} 
 
  
//------------------------------------------
 /**
  * tokenize processes strings into tokens, and breaking
  * on breakchars in addition to white space based on
  * if the breakOnBreakChars is true or not.
  *
  *
  * @param pDocText
  * @param pBeginChar
  * @param breakOnBreakChars
  */
 // ------------------------------------------
 public List<Token> tokenize( String pDocText, int pBeginChar,  boolean pBreakOnBreakChars) {
   this.docTokenNumber = -1;

   // get document text from JCas
   
   String docText = pDocText;
   ArrayList<Token> tokens = new ArrayList<Token>(10);
   int beginChar = pBeginChar;
   int whiteSpaceBeginChar = pBeginChar;
   int punctBeginChar = pBeginChar;
   int currentChar = pBeginChar;
   StringBuilder buff = new StringBuilder();
   StringBuffer whiteSpaceBuff = new StringBuffer();
   StringBuilder punctBuff = new StringBuilder();
   

   if (docText != null) {

     char[] charArray = docText.toCharArray();
     Token lastToken = null;
     // -------------------------------------
     // Loop through characters of the string
     // -------------------------------------
     for (int i = 0; i < charArray.length; i++) {

       // -----------------------------
       // Character hit
       // -----------------------------
      if (!Character.isWhitespace(charArray[i])) {
                
         // make a whitespace token of the accumulated whitespace
         if ((whiteSpaceBuff != null) && (whiteSpaceBuff.length() > 0)) {
           WhitespaceToken tokenAnnotation = (WhitespaceToken) addToken(pDocText, whiteSpaceBeginChar, whiteSpaceBuff.toString());
           tokens.add(tokenAnnotation);
           whiteSpaceBuff = new StringBuffer();
         }
         
         else if ( charArray[i] == '_' && ( 
             ( ( i == 0 ) ||
               ( i > 0    && Character.isWhitespace(i -1) || charArray[i-1]== '_') ) ) ) {
         
           dealWithPunctuationTokens(
               punctBuff, 
                punctBeginChar,
                currentChar,
                i,
                charArray,
                pDocText,
                beginChar,
                buff,
                lastToken,
               tokens);
          
           
         
         } else if (  !pBreakOnBreakChars || !isBreakChar(charArray[i])) {
           if (buff.length() == 0)
             beginChar = currentChar;
           buff.append(charArray[i]);
         } else {
           
           dealWithPunctuationTokens(
                   punctBuff, 
                    punctBeginChar,
                    currentChar,
                    i,
                    charArray,
                    pDocText,
                    beginChar,
                    buff,
                    lastToken,
                   tokens);
          
           
         }
         
         
         // -----------------------------
         // Whitespace hit
         // -----------------------------
       } else {
         if (whiteSpaceBuff.length() == 0) {
           whiteSpaceBeginChar = currentChar;
         }
         whiteSpaceBuff.append(charArray[i]);

         // make a token from the accumulated token chars
         if ((buff != null) && (buff.length() > 0)) {
           Token tokenAnnotations[] = addTokens(pDocText, beginChar, buff.toString(), lastToken);

           for (int it = 0; it < tokenAnnotations.length; it++){
             tokens.add(tokenAnnotations[it]);
             lastToken = tokenAnnotations[it];
           }
           buff.setLength(0);
         }

       }

       currentChar++;
     } // end loop through characters of the line

     // end if there is anything left
     if ((buff != null) && (buff.length() > 0)) {
       tokens.add(addToken(pDocText, beginChar, buff.toString()));
     }
     if ((whiteSpaceBuff != null) && (whiteSpaceBuff.length() > 0)) {
       tokens.add(addToken(pDocText, whiteSpaceBeginChar,
           whiteSpaceBuff.toString()));
     }
     // if (( punctBuff != null ) && ( punctBuff.length() > 0 )) {
     //   tokens.add(addToken( pJCas, punctBeginChar, punctBuff.toString()));
     // }
     
   } // end if there is any doc text
   return tokens;
 }  // End Method tokenize() -----------------------
  

// ==========================================
/**
 * dealWithPunctuationTokens 
 *
 */
// ==========================================
private void dealWithPunctuationTokens( StringBuilder punctBuff, 
                         int pPunctBeginChar,
                         int currentChar,
                         int i,
                         char[] charArray,
                         String pDocText,
                         int beginChar,
                         StringBuilder buff,
                         Token lastToken,
                         List<Token> tokens) {
  // -------------------------------------
  // create a buffer for punctuation chars
  // -------------------------------------
    int punctBeginChar = pPunctBeginChar;
    if ( punctBuff.length() == 0) 
      punctBeginChar = currentChar;
    punctBuff.append( charArray[i]);
  
    // make a token from the accumulated token chars
    if ((buff != null) && (buff.length() > 0)) {
      Token tokenAnnotations[] = addTokens(pDocText, punctBeginChar, buff.toString(), lastToken);

      for (int it = 0; it < tokenAnnotations.length; it++) {
        
        tokens.add(tokenAnnotations[it]);
        lastToken = tokenAnnotations[it];
      }
      buff.setLength(0);
    }
    
 
 // make a token from the accumulated punct chars
    if ((punctBuff != null) && (punctBuff.length() > 0)) {
      Token tokenAnnotations[] = addTokens(pDocText, punctBeginChar, punctBuff.toString(), lastToken);

      for (int it = 0; it < tokenAnnotations.length; it++) {
        tokens.add(tokenAnnotations[it]);
        lastToken = tokenAnnotations[it];
      }
 
      punctBuff.setLength(0);
    }    
    
} // end Method dealWithPunctuationTokens() ========================================



// -----------------------------------------
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
    
    return  U.containsTokenSplittingChar( c);

  }

//----------------------------------
  /**
   * initialize loads in the resources needed for tokenization. Currently, this involves
   * a list of acronyms that might show up at the end of a sentence.
   *
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize() throws Exception {
    
     
    String knownAcronymsFile = "resources/vinciNLPFramework/tokenizer/knownAcronyms.txt";
          
    try {
      this.acronyms = new Acronyms( knownAcronymsFile);
    } catch ( Exception e) {
      GLog.throwExceptionWithMsg( e, this.getClass(), "initialize", "Issue with tokenizer: Could not open the known acronyms file." + e.toString()) ;
    }
  
  } // end Method initialize() --------------



  

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
   * @param pCAS
   * @param pBeginOffset
   * @param pValue
   * @return Token[]
   */
  // -----------------------------------------
  private Token[] addTokens(String pDocText, int pBeginOffset, String pValue, Token lastToken ) {
 
    Token[] tokens = null;
   
    
    if ((pValue != null) && (pValue.length() > 0)) {
      char firstChar = pValue.charAt(0);
      char lastChar = pValue.charAt(pValue.length() - 1);
      int peek = pBeginOffset + pValue.length() + 10;
      if (peek > pDocText.length() ) 
        peek = pDocText.length() -1; 
      String charsToTheRight = null;
      try {
        if ( pBeginOffset + pValue.length() < pDocText.length())
          charsToTheRight = pDocText.substring(pBeginOffset + pValue.length(), peek);
      } catch ( Exception e) {
        e.printStackTrace();
        
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
        tokens = new Token[1];
        boolean sentenceBreakMarker =  U.isSentenceBreakChar(lastChar);
        tokens[0] = addToken( pDocText, pBeginOffset, pValue, sentenceBreakMarker);
        
      // -----------------------------------------------
      // symmetric surrounding punctuation <> {} "" '' []
      // -----------------------------------------------
      } else if ( (pValue.length() > 2) && 
           (U.isPunctuation(firstChar)) && 
           (!U.isOnlyPunctuation(middleString)) && 
           (U.isPunctuation(lastChar)) &&
           (lastChar != '.') && 
           (firstChar != '.') ) {
        
        tokens = new Token[3];
        tokens[0] = addToken( pDocText, pBeginOffset, firstString);
        tokens[1] = addToken( pDocText, pBeginOffset + 1, middleString);
        tokens[2] = addToken( pDocText, pBeginOffset + pValue.length()-1, lastString);
       
      
        // -----------------------------------------------
        // more than one trailing punctuation 
        // -----------------------------------------------
      } else if (( pValue.length() > 1) && U.isPunctuation(secondToLastChar) && U.isPunctuation(lastChar) ) {
        
        tokens = addLeadingAndTrailingPuctuationTokens(pDocText, pBeginOffset, pValue);
        
          
      // -----------------------------------------------
      // LIST Markers:
      // small numbers with a trailing period (numbered lists)  1. 1) 
      // -----------------------------------------------
      } else if ( (( rightTrimmedString.matches("\\d+") )         && ( rightTrimmedString.length() < 3)  && ((lastChar == '.') || (lastChar == ')' ) )) || 
                  (( rightTrimmedString.matches("[A-Z]|[a-z]")) && ( rightTrimmedString.length() < 3)  && ((lastChar == '.') || (lastChar == ')' ) )) ){
        tokens = new Token[1];
        boolean sentenceBreak = true;
       
        tokens[0] = addToken( pDocText, pBeginOffset, pValue,sentenceBreak); 
        tokens[0].setListDelimiter(true);
        
        // --------------------------------------------
        // Seeing 1. xxxx means that the token before 1. is also a sentence break.
        if ( lastToken != null) {
          lastToken.setSentenceBreak(true);
        } 
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
                  
                  (lastChar != '-') && 
                  (lastChar != '\'') && (!this.acronyms.isAcronym(pValue, charsToTheRight))) {
        
        tokens = new Token[2];
        tokens[0] = addToken(pDocText, pBeginOffset, rightTrimmedString);
        
        // ----------------------------------------------
        // In a future iteration, there should be a configurable
        // option to use colons and semi-colons as sentence breaks
        // ----------------------------------------------
        boolean sentenceBreakMarker = U.isSentenceBreakChar( lastChar);
       
        tokens[1] = addToken(pDocText, pBeginOffset + pValue.length() - 1, lastString, sentenceBreakMarker);
        // -----------------------------------------------
        // Leading punctuation (except $ + - $ #)
        // -----------------------------------------------
      } else if (U.isPunctuation(firstChar) && ("+-$#".indexOf(firstChar) == -1)) {
        // System.err.println( pValue + "|6");
        tokens = new Token[2];
        tokens[0] = addToken(pDocText, pBeginOffset, firstString);
        tokens[1] = addToken(pDocText, pBeginOffset + 1, leftTrimmedString);
        // -----------------------------------------------
        // Just a normal token
        // -----------------------------------------------
      } else {
        // System.err.println( pValue + "|7");
        tokens = new Token[1];
        tokens[0] = addToken( pDocText, pBeginOffset, pValue);
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
 * @param pDocText
 * @param pBeginOffset
 * @param pValue
 * @return
 */
// -----------------------------------------
private Token[] addLeadingAndTrailingPuctuationTokens( String pDocText, int pBeginOffset, String pValue) {
  
    boolean sentenceBreak = false;
    Token tokens[] = null;
    Stack<Token> endTokenStack = new Stack<Token>();
    int z = 0;
    for (z = pValue.length() - 1; z >= 0; z--) {
      char zChar = pValue.charAt(z);
      if (U.isPunctuation(zChar)) {
        StringBuffer zString = new StringBuffer();
        zString.append(zChar);
        sentenceBreak = false;
        if (( z == pValue.length() - 1) && (U.isSentenceBreakChar( zChar ))) 
          sentenceBreak = true;
        endTokenStack.push(addToken( pDocText, pBeginOffset + z, zString.toString(),sentenceBreak));
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
      ArrayList<Token> beginTokenStack = new ArrayList<Token>();
      int a = 0;
      for (a = 0; a < pValue.length(); a++) {
        char aChar = pValue.charAt(a);
        if (U.isPunctuation(aChar)) {
          StringBuffer aString = new StringBuffer();
          aString.append(aChar);
          beginTokenStack.add(addToken(pDocText, pBeginOffset + a, aString.toString()));
        } else {
          break;
        }
      } // end loop through front end of the stringstring
      
      
      String middle = pValue.substring(a, z + 1);
     
      
      int numberOfTokens = beginTokenStack.size() + endTokenStack.size() + 1;
      tokens = new Token[numberOfTokens];
   
      for (int bi = 0; finalI < beginTokenStack.size(); bi++)
        tokens[finalI++] = beginTokenStack.get(bi);
      tokens[finalI++] = addToken(pDocText, pBeginOffset + a, middle);
    } else {
      tokens = new Token[endTokenStack.size()];
    }
    while (!endTokenStack.empty())
      tokens[finalI++] = endTokenStack.pop();
  
  return tokens;
} // end Method addLeadingAndTrailingPuctuationTokens() ----

  //-----------------------------------------
  /**
   * addToken creates a TokenAnnoation from the begin char position,
   * the string and the CAS. This method sets the token specific features.
   * This method might return subclasses of token including whitespace, and punctuation
   * 
   * @param pDocText
   * @param pBeginOffset
   * @param pValue
   * 
   * @return Token
   */
  // -----------------------------------------
  private Token addToken( String pDocText, int pBeginOffset, String pValue) {
    return addToken( pDocText, pBeginOffset, pValue, false);
  } // end Method addToken() -----------------

  // -----------------------------------------
  /**
   * addToken creates a TokenAnnoation from the begin char position,
   * the string and the CAS. This method sets the token specific features.
   * This method might return subclasses of token including whitespace, and punctuation
   * This method takes an additional parameter to indicate the token is a potential
   * sentence boundary marker.  
   *  
   * @param pDocText
   * @param pBeginOffset
   * @param pValue
   * @param pSentenceBreakMarker
   * @return Token
   */
  // -----------------------------------------
  private Token addToken( String pDocText, int pBeginOffset, String pValue, boolean pSentenceBreakMarker) {
    
    Token tokenAnnotation = null;
    if (Character.isWhitespace(pValue.charAt(0))) {
      tokenAnnotation = (Token) new WhitespaceToken();
      tokenAnnotation.setTokenType("WhiteSpace");
      if (( pValue.contains("\r")) || (pValue.contains("\n"))) {
        ((WhitespaceToken)tokenAnnotation).setNewLine(true);
        tokenAnnotation.setTokenType("NewLine");
        this.docTokenNumber++;
      }
    
      // ----------------------------
      // check for blank lines
      char buff[] = pValue.toCharArray();
      int ctr = 0;
      for ( int i = 0; i < buff.length; i++) {
        if (( buff[i] == '\n') || (buff[i] == '\r') ) {
          ctr++;
          if (ctr > 1)
            break;
        }
      } // end loop through whitespace chars;
      if ( ctr > 1) {            
        ((WhitespaceToken)tokenAnnotation).setEmptyLine(true);
        tokenAnnotation.setSentenceBreak(true);
      }
    } else {
     
     tokenAnnotation = new Token();
     this.docTokenNumber++;
    }
     
    // VUIMAUtil.setProvenance( pCAS, tokenAnnotation, this.getClass().getName() );
    tokenAnnotation.setId("Token_" + this.annotationCounter++);
    tokenAnnotation.setBegin(pBeginOffset);
    tokenAnnotation.setEnd(pBeginOffset + pValue.length());
    tokenAnnotation.setDisplayString(pValue);
    tokenAnnotation.setProcessMe(true);
    tokenAnnotation.setTokenNumber(this.docTokenNumber);
    
    // features specific to token annotations
    tokenAnnotation.setAllCapitalization(U.isAllCaps(pValue));
    tokenAnnotation.setWhiteSpaceFollows(doesWhiteSpaceFollow(pDocText, pBeginOffset, pValue));
    tokenAnnotation.setAllCapitalization(U.isInitialCap(pValue));
    
    tokenAnnotation.setPunctuationOnly( U.isOnlyPunctuation( pValue));
    tokenAnnotation.setContainsPunctuation(U.containsPunctuation( pValue));
    tokenAnnotation.setContainsSymbol( U.containsSymbol(pValue));
    
    tokenAnnotation.setSentenceBreak( pSentenceBreakMarker);
    
    if ( tokenAnnotation.getContainsSymbol() )
      tokenAnnotation.setTokenType("Symbol");
    if ( tokenAnnotation.getPunctuationOnly() )
      tokenAnnotation.setTokenType("Punctuation");
    if( U.isNumber(pValue ))
      tokenAnnotation.setTokenType("Number");
   
    
    return tokenAnnotation;
    
  } // end method addToken() ----------------------
  
  
  // ----------------------------------
  /**
   * doesWhiteSpaceFollow returns true if the character after this annotation
   * is whitespace. This method will return false if there is no trailing
   * character.
   * 
   * @param pCAS
   * @param pBeginOffset
   * @param pValue
   * @return boolean true if the character after this annotation is whitespace.
   * 
   */
  // ----------------------------------
  private boolean doesWhiteSpaceFollow(String pDocText, int pBeginOffset, String pValue) {
    boolean returnValue = false;
    String docText = pDocText; // is this efficient? Is this a
                                             // pointer or is space
                                             // being allocated to the docText?
    int nextChar = pBeginOffset + pValue.length();
    if (nextChar < docText.length()) {
      char c = docText.charAt(pBeginOffset + pValue.length());
      returnValue = Character.isWhitespace(c);
    }
    return returnValue;
  } // end method doesWhiteSpaceFollow() -
   
 
 
  // ---------------------------------------------
  // Class Variables
  // ---------------------------------------------
  

  private Acronyms                      acronyms = null; // acronyms utilities
  private long                 annotationCounter = 0;
  private int                     docTokenNumber = 0;
  
  
} // end Class Tokenizer --------------------------
