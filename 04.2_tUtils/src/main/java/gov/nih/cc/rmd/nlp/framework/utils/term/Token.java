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
// =======================================================
/**
 * Token.java [Summary here]
 *
 * @author  guy
 * @created Nov 14, 2013
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils.term;

/**
 * @author guy
 *
 */
public class Token {

  private boolean whiteSpaceFollows = false;
  private boolean punctuationOnly = false;
  private boolean containsSymbol = false;
  private boolean containsPunctuation = false;
   private boolean processMe = false;
  private String displayString = null;
  private int end = 0;
  private int begin = 0;
  private String id = null;
  /**
   * @return the displayString
   */
  public String getDisplayString() {
    return displayString;
  }


  /**
   * @return the allCapitalization
   */
  public boolean isAllCapitalization() {
    return allCapitalization;
  }


  /**
   * @return the tokenNumber
   */
  public int getTokenNumber() {
    return tokenNumber;
  }


  private boolean sentenceBreak = false;
  private String tokenType = "WordToken";
  private boolean listDelimiter = false;
  private boolean allCapitalization;
  private int tokenNumber;
  
  
  /**
   * @return the whiteSpaceFollows
   */
  public boolean isWhiteSpaceFollows() {
    return whiteSpaceFollows;
  }


  /**
   * @return the containsPunctuation
   */
  public boolean isContainsPunctuation() {
    return containsPunctuation;
  }


  /**
   * @return the processMe
   */
  public boolean isProcessMe() {
    return processMe;
  }


  /**
   * @return the end
   */
  public int getEnd() {
    return end;
  }


  /**
   * @return the begin
   */
  public int getBegin() {
    return begin;
  }


  /**
   * @return the id
   */
  public String getId() {
    return id;
  }


  /**
   * @return the sentenceBreak
   */
  public boolean isSentenceBreak() {
    return sentenceBreak;
  }


  /**
   * @return the tokenType
   */
  public String getTokenType() {
    return tokenType;
  }


  /**
   * @return the listDelimiter
   */
  public boolean isListDelimiter() {
    return listDelimiter;
  }



  // =======================================================
  /**
   * setListDelimiter [Summary here]
   * 
   * @param pListDelimiter
   */
  // =======================================================
  public void setListDelimiter(boolean pListDelimiter) {
    this.listDelimiter = pListDelimiter;
  }  // End Method setListDelimiter() ======================
  

  // =======================================================
  /**
   * setTokenType [Summary here]
   * 
   * @param pTokenType   whiteSpaceToken|wordToken
   */
  // =======================================================
  public void setTokenType(String pTokenType) {
    this.tokenType = pTokenType;
  }  // End Method setTokenType() ======================
  

  // =======================================================
  /**
   * setSentenceBreak [Summary here]
   * 
   * @param pSentenceBreak;
   */
  // =======================================================
  public void setSentenceBreak(boolean pSentenceBreak) {
    this.sentenceBreak = pSentenceBreak;
  }  // End Method setSentenceBreak() ======================
  

  // =======================================================
  /**
   * setId [Summary here]
   * 
   * @param pId
   */
  // =======================================================
  public void setId(String pId) {
    this.id = pId;
  } // End Method setId() ======================
  

  // =======================================================
  /**
   * setBegin [Summary here]
   * 
   * @param pBeginOffset
   */
  // =======================================================
  public void setBegin(int pBeginOffset) {
   this.begin = pBeginOffset;
  } // End Method setBegin() ======================
  

  // =======================================================
  /**
   * setEnd [Summary here]
   * 
   * @param pEndOffset
   */
  // =======================================================
  public void setEnd(int pEndOffset) {
    this.end = pEndOffset;
  }  // End Method setEnd() ======================
  

  // =======================================================
  /**
   * setDisplayString [Summary here]
   * 
   * @param pValue
   */
  // =======================================================
  public void setDisplayString(String pValue) {
    this.displayString = pValue;
  } // End Method setDisplayString() ======================
  

  // =======================================================
  /**
   * setProcessMe [Summary here]
   * 
   * @param pBrocessMe
   */
  // =======================================================
  public void setProcessMe(boolean pProcessMe) {
    this.processMe = pProcessMe;
  }  // End Method setProcessMe() ======================
  

  // =======================================================
  /**
   * setTokenNumber [Summary here]
   * 
   * @param pTokenNumber
   */
  // =======================================================
  public void setTokenNumber(int pTokenNumber) {
     this.tokenNumber = pTokenNumber;
  }  // End Method setTokenNumber() ======================
  

  // =======================================================
  /**
   * setAllCapitalization [Summary here]
   * 
   * @param pAllCapitalization
   */
  // =======================================================
  public void setAllCapitalization(boolean pAllCapitalization) {
    this.allCapitalization = pAllCapitalization;
  }  // End Method setAllCapitalization() ======================
  

  // =======================================================
  /**
   * setPunctuationOnly [Summary here]
   * 
   * @param pOnlyPunctuation
   */
  // =======================================================
  public void setPunctuationOnly(boolean pOnlyPunctuation) {
    this.punctuationOnly = pOnlyPunctuation;
    
  }  // End Method setPunctuationOnly() ======================
  

  // =======================================================
  /**
   * setContainsPunctuation [Summary here]
   * 
   * @param pContainsPunctuation
   */
  // =======================================================
  public void setContainsPunctuation(boolean pContainsPunctuation) {
     this.containsPunctuation = pContainsPunctuation;
  }  // End Method setContainsPunctuation() ======================
  

  // =======================================================
  /**
   * setContainsSymbol [Summary here]
   * 
   * @param pContainsSymbol
   */
  // =======================================================
  public void setContainsSymbol(boolean pContainsSymbol) {
    this.containsSymbol = pContainsSymbol;
  }  // End Method setContainsSymbol() ======================
  

  // =======================================================
  /**
   * getContainsSymbol [Summary here]
   * 
   * @return
   */
  // =======================================================
  public boolean getContainsSymbol() {
    return this.containsSymbol;
  }  // End Method getContainsSymbol() ======================
  

  // =======================================================
  /**
   * getPunctuationOnly [Summary here]
   * 
   * @return
   */
  // =======================================================
  public boolean getPunctuationOnly() {
    return this.punctuationOnly;
  }  // End Method getPunctuationOnly() ======================
  

  // =======================================================
  /**
   * setWhiteSpaceFollows [Summary here]
   * 
   * @param pWoesWhiteSpaceFollow
   */
  // =======================================================
  public void setWhiteSpaceFollows(boolean pDoesWhiteSpaceFollow) {
    this.whiteSpaceFollows = pDoesWhiteSpaceFollow;
  }  // End Method setWhiteSpaceFollows() ======================
  

}
