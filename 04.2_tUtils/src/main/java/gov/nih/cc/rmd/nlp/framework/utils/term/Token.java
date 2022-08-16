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
