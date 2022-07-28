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
 * Token is a quick dirty tokenizer.  This uses heuristics that are found
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
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.Line;
import gov.va.chir.model.Token;
import gov.va.chir.model.WhitespaceToken;
import gov.va.chir.model.WordToken;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.acronym.Acronyms;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.TermUtils;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class TokenAnnotator extends JCasAnnotator_ImplBase {

  @Override
  // -----------------------------------------
  /**
   * process takes a cas of un-annotated text and breaks it into
   * gov.va.chir.tokens.  This uses a finite state automata through
   * each character of the text, accumulating tokens as token barriers
   * are hit.  
   * 
   * Token barriers include space tokens.  
   * 
   * There is a small amount of repair done to mark punctuation that
   * is surrounded by whitespace, and punctuation that is at the end
   * of a non white-space token.
   * 
   * 
   * 
   * @param pJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
    this.performanceMeter.startCounter();
    boolean breakOnBreakChars = false;

    String docText = pJCas.getDocumentText();
    
    
     tokenize(pJCas, docText, 0, breakOnBreakChars);

    // ------------------------
    // specific to framework - 
    // mark the metadata delimiters as sentence delimiters
    // ------------------------
     markMetaDataDelimiters( pJCas );
     
     // ----------------------
     // re-mark lines that start with - as list delimiters
     // ----------------------
     markHyphenListDelimiters( pJCas);
     
     List<Annotation> tokens = UIMAUtil.getAnnotations(pJCas, Token.typeIndexID, true );
     if ( tokens != null && !tokens.isEmpty())
       ;
     
    this.performanceMeter.stopCounter();
    
    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue with " + this.getClass().getName() + " " + e.toString());
    //  throw new AnalysisEngineProcessException();
    
    }

  } // end Method process() --------------------------

 
  // ------------------------------------------
  /**
   * tokenize processes strings into tokens, and breaking
   * on breakchars in addition to white space based on
   * if the breakOnBreakChars is true or not.
   *
   *
   * @param pJCas
   * @param breakOnBreakChars
   */
  // ------------------------------------------
  public List<Annotation> tokenize(JCas pJCas, String pDocText, int pBeginChar,  boolean pBreakOnBreakChars) {
    this.docTokenNumber = -1;

    // get document text from JCas

    String docText = pDocText;
    ArrayList<Annotation> tokens = new ArrayList<Annotation>(10);
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
        // Character hit (or a non leading underbar hit)
        // -----------------------------
        if (!Character.isWhitespace(charArray[i]) ) { 
           //  || (i > 0 && charArray[i] == '_' && ( charArray[i-1] == '_' && !Character.isWhitespace(charArray[i-1]))))  {
          
          // -----------------------------------------------------
          // make a whitespace token of the accumulated whitespace
          if ((whiteSpaceBuff != null) && (whiteSpaceBuff.length() > 0)) {
            WhitespaceToken tokenAnnotation = (WhitespaceToken) addToken(pJCas, whiteSpaceBeginChar, whiteSpaceBuff.toString());
            tokens.add(tokenAnnotation);
            whiteSpaceBuff = new StringBuffer();
          }

          // --------------------------------------------
          // if the token before was a number and the current token is 
          // indicative of time, a fraction, an equation, listChar, or a reference  ie.  {3 or (3  
          if          ( i > 1                    && U.isNumber(charArray[i-1])   &&  TermUtils.canBeTimeFractionEquationListMarkerOrReference( charArray[i]) ) {
            if (buff.length() == 0)
              beginChar = currentChar;
            buff.append(charArray[i]);
           
          // --------------------------------------------
          // if the token after is a number and the current token is
          // indicative of time, polarity, an equation, listMarker, or a reference
          } else if ( i + 1 < charArray.length &&  U.isNumber(charArray[i+1])   && TermUtils.canBeTimePolarityFractionEquationListMarkerOrReference( charArray[i]) ) {
            if (buff.length() == 0)
              beginChar = currentChar;
            buff.append(charArray[i]);
          
          // ----------------------------------------------
          // if the token before and after are numbers
          // and the character is indicative of a decimal or formatted number
          } else if (charArray[i] == '.'  && i > 1   &&  i + 1 < charArray.length && U.isNumber(charArray[i+1]) ||    //   1.00  part of a number  
                     charArray[i] == ','  && i > 1   &&  i + 1 < charArray.length && U.isNumber(charArray[i+1]) ) {  //   1,000 part of a number (could need more rules)                      
            if (buff.length() == 0)
              beginChar = currentChar;
            buff.append(charArray[i]);
            
            
          // -----------------------------------
          // if you have a letter list marker <space> a)) 
          } else if ( charArray[i] == ')' && i -2 >= 0 && i+1 < charArray.length && U.isWhiteSpace(charArray[i-2]) && U.isWhiteSpace( charArray[i+1])   )  {
            if (buff.length() == 0)
              beginChar = currentChar;
            buff.append(charArray[i]);
  
            
          // -------------------------------------
          // normal case = at a char [A-z]
          } else if ( ( charArray[i] >= 'A' && charArray[i] <= 'Z' ) || (charArray[i] >= 'a' && charArray[i] <=  'z' ) ) {
            
        
            if (buff.length() == 0)
              beginChar = currentChar;
            buff.append(charArray[i]);
         
          
          
          // --------------------------------------
          // Handle leading underbars
          } else if ( charArray[i] == '_' && ( 
              ( ( i == 0 ) ||
                ( i > 0    && Character.isWhitespace(i -1) || charArray[i-1]== '_') ) ) ) {
          
            dealWithPunctuationTokens( punctBuff, 
                punctBeginChar, 
                currentChar, 
                charArray,
                i,
                buff, 
                tokens,
                lastToken,
                pJCas,
                beginChar);
                
                
            
          } else if ( charArray[i]!= ':'   &&  
                      charArray[i]!= '/'   &&  
                      charArray[i]!= '\\'  &&  
                      charArray[i]!= '\''  && // <----- should have broken up Parkenson's but is not
                      charArray[i]!= '"'   && // <----- should have broken up air quotes
                      charArray[i]!= '`'   && 
                      charArray[i]!='('    && 
                   // charArray[i]!=')'    &&   <--------when commented out--- picks up list markers but mis-tokenizes (s) as s)  
                      charArray[i]!= '['   &&
                      charArray[i]!= ']'   && 
                      charArray[i]!='{'    && 
                      charArray[i]!= '}'   &&
                      charArray[i]!= '*'   &&
                //    charArray[i]!= '.'   &&       
                      charArray[i]!= ','   &&
                      charArray[i]!= '-'   &&
                      charArray[i]!= '>'   &&
                //    charArray[i]!= '_'  &&  ()   // --- would always break on _
                      
                      ( !pBreakOnBreakChars || pBreakOnBreakChars  && !isBreakChar(charArray[i]))) {
         
                       if (buff.length() == 0)
                         beginChar = currentChar;
                      
                       buff.append(charArray[i]);
            
          
          } else {
            
           
            dealWithPunctuationTokens( punctBuff, 
                 punctBeginChar, 
                 currentChar, 
                 charArray,
                 i,
                 buff, 
                 tokens,
                 lastToken,
                 pJCas,
                 beginChar);
           
          

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
            Token tokenAnnotations[] = addTokens(pJCas, beginChar, buff.toString(), lastToken);

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
        tokens.add(addToken(pJCas, beginChar, buff.toString()));
      }
      if ((whiteSpaceBuff != null) && (whiteSpaceBuff.length() > 0)) {
        tokens.add(addToken(pJCas, whiteSpaceBeginChar,
            whiteSpaceBuff.toString()));
      }
      // if (( punctBuff != null ) && ( punctBuff.length() > 0 )) {
      //   tokens.add(addToken( pJCas, punctBeginChar, punctBuff.toString()));
      // }

      
      repairNumbersThatEndInPeriods( pJCas);
      
      
    } // end if there is any doc text
    return tokens;
 
  } // End Method tokenize() ======================
    
  

  
  

  // ==========================================
  /**
   * dealWithPunctuationTokens 
   *
   */
  // ==========================================
  private void dealWithPunctuationTokens( StringBuilder punctBuff, 
                           int pPunctBeginChar, 
                           int currentChar, 
                           char[] charArray,
                           int i,
                           StringBuilder buff, 
                           ArrayList<Annotation> tokens,
                           Token lastToken,
                           JCas pJCas,
                           int beginChar) {
    
    
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

    boolean returnVal = U.containsTokenSplittingChar( c);
    
    return returnVal;
  }

  
// ==========================================
  /**
   * repairNumbersThatEndInPeriods breaks token patterns
   * that [equals] [Number][.][space]  to have the period
   * be a separate sentence ending token.  
   *
   * @param pJCas
   */
  // ==========================================
  public void repairNumbersThatEndInPeriods(JCas pJCas) {
    
    
    List<Annotation> annotations = UIMAUtil.getAnnotations(pJCas, Token.typeIndexID );
    if ( annotations != null && !annotations.isEmpty()) {
    for ( int i = 2; i < annotations.size()-2; i++ ) {
      Annotation tokenBefore = annotations.get(i -2);
      Annotation tokenBefore1 = annotations.get(i -1);
      Annotation currentToken = annotations.get(i);
      Annotation tokenAfter = annotations.get(i+1);
      
      String tb = tokenBefore.getCoveredText().toLowerCase();
      String ct = currentToken.getCoveredText();
      
      if ( tb.equals("=") || tb.equals("equals" ) || tb.equals(">") || tb.equals("<") || tb.equals("than"))
        if (ct.endsWith(".")) {
          if ( tokenAfter.getClass().getName().contains("WhitespaceToken") && 
               tokenBefore1.getClass().getName().contains("WhitespaceToken")) {
            // ---------------------------
            // Remove the current token
            // create two tokens - the non period part
            // A separate token for the period
            String nonPeriodPart = ct.substring(0, ct.length()-1);
            if ( nonPeriodPart != null && nonPeriodPart.length() > 0 ) {
            	addToken( pJCas, currentToken.getBegin(), nonPeriodPart, false);
            	addToken( pJCas, currentToken.getEnd()-1, ".", true);
            	((WordToken) tokenBefore).setListDelimiter(false);
            	((WordToken) tokenBefore).setSentenceBreak(false);
            	currentToken.removeFromIndexes();
            }
          }
        }
    }
    }
   
  } // end Method repairNumbersThatEndInPeriods() 
  

//----------------------------------
 /**
  * destroy
 * 
  **/
 // ----------------------------------
 public void destroy() {
   this.performanceMeter.writeProfile( this.getClass().getSimpleName());
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
  public void initialize(UimaContext aContext) throws ResourceInitializationException {

   
      
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  
       initialize( args);
        
      } catch (Exception e ) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL,"Issue with token annotator " + e.toString());
        throw new ResourceInitializationException();
      }
      

      
  } // end Method initialize() -------

  //----------------------------------
  /**
   * initialize loads in the resources needed for tokenization. Currently, this involves
   * a list of acronyms that might show up at the end of a sentence.
   *
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String pArgs[]) throws ResourceInitializationException {

    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    String knownAcronymsFile = "resources/vinciNLPFramework/tokenizer/knownAcronyms.txt";

    try {
      this.acronyms = new Acronyms( knownAcronymsFile);
    } catch ( Exception e) {
      e.printStackTrace();
      String msg = "Issue with tokenizer: Could not open the known acronyms file. x " + e.toString();
      
      GLog.println(GLog.ERROR_LEVEL,msg);
      throw new ResourceInitializationException();
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
  private Token[] addTokens(JCas pCAS, int pBeginOffset, String pValue, Token lastToken ) {

    Token[] tokens = null;
   
    String buff = pCAS.getDocumentText();
    if ((pValue != null) && (pValue.length() > 0)) {
      char firstChar = pValue.charAt(0);
      char lastChar = pValue.charAt(pValue.length() - 1);
      
      int peek = pBeginOffset + pValue.length() + 10;
      if (peek > buff.length() ) peek = buff.length(); 

      String charsToTheRight = buff.substring(pBeginOffset + pValue.length(), peek);
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
     
        tokens[0] = addToken(pCAS, pBeginOffset, pValue, sentenceBreakMarker);

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
        tokens[0] = addToken(pCAS, pBeginOffset, firstString);
        tokens[1] = addToken(pCAS, pBeginOffset + 1, middleString);
        tokens[2] = addToken(pCAS, pBeginOffset + pValue.length()-1, lastString);


        // -----------------------------------------------
        // more than one trailing punctuation 
        // -----------------------------------------------
      } else if (( pValue.length() > 1) && U.isPunctuation(secondToLastChar) && U.isPunctuation(lastChar) ) {

        tokens = addLeadingAndTrailingPuctuationTokens(pCAS, pBeginOffset, pValue);


        // -----------------------------------------------
        // LIST Markers:
        // small numbers with a trailing period (numbered lists)  1. 1) 
        // -----------------------------------------------
      } else if ( (( rightTrimmedString.matches("\\d+") )         && ( rightTrimmedString.length() < 3)  && ((lastChar == '.') || (lastChar == '*') || (lastChar == ')' ) )) || 
                  (( rightTrimmedString.matches("[A-Z]"))         && ( rightTrimmedString.length() < 3)  && ((lastChar == '.') || (lastChar == '*') || (lastChar == ')' ) )) ||
                  (( rightTrimmedString.equals("A"))              && ( rightTrimmedString.length() < 3)  && ((lastChar == ')') )) ||
                  (( rightTrimmedString.matches("[a-z]"))         && ( rightTrimmedString.length() < 3)  && ((lastChar == '.') || (lastChar == '*') || (lastChar == ')' ) )) ){
          
        tokens = new Token[1];
        boolean sentenceBreak = true;

        tokens[0] = addToken(pCAS, pBeginOffset, pValue,sentenceBreak); 
        tokens[0].setListDelimiter(true);

        // --------------------------------------------
        // Seeing 1. xxxx means that the token before 1. is also a sentence break.
        // GD: 2019-12-31 -- not when a list is within a sentence - 
        if ( lastToken != null && !U.isWord( lastToken.getCoveredText()) ){
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
        //System.err.println(rightTrimmedString + "|5.1");
        tokens[0] = addToken(pCAS, pBeginOffset, rightTrimmedString);

        // ----------------------------------------------
        // In a future iteration, there should be a configurable
        // option to use colons and semi-colons as sentence breaks
        // ----------------------------------------------
        boolean sentenceBreakMarker = U.isSentenceBreakChar( lastChar);

        tokens[1] = addToken(pCAS, pBeginOffset + pValue.length() - 1, lastString, sentenceBreakMarker);
        // -----------------------------------------------
        // Leading punctuation (except $ + - $ #)
        // -----------------------------------------------
      } else if (U.isPunctuation(firstChar) && ("+-$#".indexOf(firstChar) == -1)) {
        // System.err.println( pValue + "|6");
        tokens = new Token[2];
        tokens[0] = addToken(pCAS, pBeginOffset, firstString);
        tokens[1] = addToken(pCAS, pBeginOffset + 1, leftTrimmedString);
        // -----------------------------------------------
        // Just a normal token
        // -----------------------------------------------
      } else {
        // System.err.println( pValue + "|7");
        tokens = new Token[1];
        tokens[0] = addToken(pCAS, pBeginOffset, pValue);
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
   * @param pCAS
   * @param pBeginOffset
   * @param pValue
   * @return
   */
  // -----------------------------------------
  private Token[] addLeadingAndTrailingPuctuationTokens(JCas pCAS, int pBeginOffset, String pValue) {

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
        endTokenStack.push(addToken(pCAS, pBeginOffset + z, zString.toString(),sentenceBreak));
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
          beginTokenStack.add(addToken(pCAS, pBeginOffset + a, aString.toString()));
        } else {
          break;
        }
      } // end loop through front end of the stringstring


      String middle = pValue.substring(a, z + 1);


      int numberOfTokens = beginTokenStack.size() + endTokenStack.size() + 1;
      tokens = new Token[numberOfTokens];

      for (int bi = 0; finalI < beginTokenStack.size(); bi++)
        tokens[finalI++] = beginTokenStack.get(bi);
      tokens[finalI++] = addToken(pCAS, pBeginOffset + a, middle);
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
   * @param pCAS
   * @param pBeginOffset
   * @param pValue
   * 
   * @return Token
   */
  // -----------------------------------------
  private Token addToken(JCas pCAS, int pBeginOffset, String pValue) {
    return addToken( pCAS, pBeginOffset, pValue, false);
  } // end Method addToken() -----------------

  // -----------------------------------------
  /**
   * addToken creates a TokenAnnoation from the begin char position,
   * the string and the CAS. This method sets the token specific features.
   * This method might return subclasses of token including whitespace, and punctuation
   * This method takes an additional parameter to indicate the token is a potential
   * sentence boundary marker.  
   *  
   * @param pCAS
   * @param pBeginOffset
   * @param pValue
   * @param pSentenceBreakMarker
   * @return Token
   */
  // -----------------------------------------
  private Token addToken(JCas pCAS, int pBeginOffset, String pValue, boolean pSentenceBreakMarker) {

    Token tokenAnnotation = null;
    if ( pValue == null || pValue.length() == 0 ) return null;
    
    if (Character.isWhitespace(pValue.charAt(0))) {
      tokenAnnotation = new WhitespaceToken(pCAS);
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
        if ( buff[i] == '\n')  { //    ---- was counting \r\n as empty lines -->  || (buff[i] == '\r') ) {
          ctr++;
          if (ctr > 1)
            break;
        }
      } // end loop through whitespace chars;
      
     if ( ctr > 1 ) {          
        ((WhitespaceToken)tokenAnnotation).setEmptyLine(true);
        tokenAnnotation.setSentenceBreak(true);
      }
    } else {

      tokenAnnotation = new WordToken(pCAS);
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
    tokenAnnotation.setWhiteSpaceFollows(UIMAUtil.doesWhiteSpaceFollow(pCAS, pBeginOffset, pValue));
    tokenAnnotation.setInitialCapitalization(U.isInitialCap(pValue));

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
    tokenAnnotation.addToIndexes();
    
   
    return tokenAnnotation;

  } // end method addToken() ----------------------


  // End Method tokenize() -----------------------
  
 

  

  // ---------------------------------------------
  // Class Variables
  // ---------------------------------------------


  // =================================================
  /**
   * markMetaDataDelimiters marks "===============================================MetaData=Not=Part=of=Record"
   *                        as punctuation only, sentenceBreaking
   *                        
   * 
  */
  // =================================================
  private void markMetaDataDelimiters(JCas pJCas ) {
    
    String metaDataDelimiter = "==============================================MetaData=Not=Part=of=Record";
    List<Annotation> tokens = UIMAUtil.getAnnotations(pJCas, WordToken.typeIndexID );
   
    if ( tokens != null && !tokens.isEmpty()) {
      for ( int i = 0; i < tokens.size(); i++ ) {
        WordToken aToken = (WordToken) tokens.get(i);
        String tokenText = aToken.getCoveredText();
       
        if ( metaDataDelimiter.equals( tokenText ) ) {
           aToken.setPunctuationOnly(true);
           aToken.setSentenceBreak(true);
        
          // and Mark the dog toto too
           WordToken nextToken = (WordToken) tokens.get(i+1);
           if ( nextToken.getCoveredText().equals("=")) 
             nextToken.setSentenceBreak(true);
           
             
        
        }
        
        // just look at the to 10 lines of the document
        if ( i > 300 )  break;  
      } // end loop
    }
    
  } // end markMetaDataDelimiters() ----------------

  
  // =================================================
  /**
   * markHyphenListDelimiters mark hyphens that start lines
   * as list delimiters
   * 
   * If there are are more than one starting hyphen, it's not
   * a list marker  This should get rid of --------
   * 
   * if a line starts with
   *  -1  it's likely a number not a list
   *  -2  
   * @param pJCas
  */
  // =================================================
  private void markHyphenListDelimiters(JCas pJCas) {
   
    
    List<Annotation> lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID );
    
    if ( lines != null && !lines.isEmpty()) {
      for ( Annotation aLine: lines ) {
        String lineText = aLine.getCoveredText();
        if ( lineText != null && lineText.trim().length() > 0 ) 
          if ( lineText.trim().startsWith( "-")   && 
              !lineText.trim().startsWith("--")) {
            
            if ( lineStartsWithNegativeNumber( lineText.trim() ))
              continue;
            
            List<Annotation> someHyphens = UIMAUtil.getAnnotationsBySpan(pJCas, WordToken.typeIndexID, aLine.getBegin(), aLine.getEnd() );
            
            
            if ( someHyphens != null && !someHyphens.isEmpty() ) {
              for ( Annotation aToken : someHyphens ) {
                if ( aToken.getCoveredText().equals("-")) {
                  ((WordToken) aToken).setListDelimiter(true);
                  ((WordToken) aToken).setSentenceBreak(true);
                  break;
                }
              }
            }
          }
      }
    }
    
  } // end Method markHyphenListDelimiters() ----



  // =================================================
  /**
   * lineStartsWithNegativeNumber returns true if the
   * line starts with a negative number
   *   -1 
   *   
   *  This method will only be called when the line 
   *  already starts with a hyphen.  So look to the
   *  next character to see if it's a number
   * 
   * @param trim
   * @return boolean
  */
  // =================================================
  private final boolean lineStartsWithNegativeNumber(String pLine) {
    boolean returnVal = false;
    
    if ( pLine.length() > 1 ) 
      if ( U.isNumber(pLine.charAt(1) ))
          returnVal = true;
    
    return returnVal;
  } // end Method lineStartsWithNegativeNumber() ----



  private Acronyms                      acronyms = null; // acronyms utilities
  private long                 annotationCounter = 0;
  private int                     docTokenNumber = 0;
  public ProfilePerformanceMeter      performanceMeter = null;
  


} // end Class Tokenizer --------------------------
