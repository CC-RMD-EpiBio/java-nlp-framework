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
 * TokenSlash Repair Annotator aims to fix B/P which gets
 * tokenized as three tokens, then unretrievable by lexical lookup
 * 
 * Reconnect tokens around slashes back into one token.
 * 
 * @author Guy Divita
 * @created April 28, 2022
 * 
 *  
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import com.ibm.icu.impl.PatternTokenizer;

import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Line;
import gov.va.chir.model.Token;
import gov.va.chir.model.WordToken;



public class TokenSlashRepairAnnotator extends TokenAnnotator {

  

  // =======================================================
   /**
    * process
    * 
    * @param pJCas
    *
    */
   // ======================================================  
	@Override
	public void process(JCas pJCas) throws AnalysisEngineProcessException {

	  super.performanceMeter.startCounter();
	  
	  tokenSlashRepair(pJCas);
	  
	  tokenAcronymRepair( pJCas);
	
		super.performanceMeter.stopCounter();
	}



  // =================================================
  /**
   * tokenAcronymRepair looks for tokens that are
   * made up of letter dot letter dot 
   * 
   * to make these one token
   * 
   * re-tokenize via space delimiters within a line
   * 
   * @param pJCas
  */
  // =================================================
  private final void tokenAcronymRepair(JCas pJCas) {
   
    List<Annotation> lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID); 
    if ( lines != null && !lines.isEmpty() )
      for ( Annotation line : lines )
        processTokenAcronymRepair( pJCas, line);
      
   
    
  } // end Method tokenAcronymRepair() ---------------



  // =================================================
  /**
   * processTokenAcronymRepair 
   * re-tokenize via space delimiters within a line
   * 
   * @param pJCas
   * @param pLine
  */
  // =================================================
  private final void processTokenAcronymRepair(JCas pJCas, Annotation pLine) {
    
    String lineText = pLine.getCoveredText();
    
    if ( lineText.contains("p.o."))
        System.err.println(" P.o.");
    
    if ( lineText != null && lineText.trim().length() > 0)
      processTokenAcronymRepair(pJCas, lineText,  pLine) ;
    
  } // end Method processTokenAcronymRepair() --------
    
  // =================================================
  /**
   * processTokenAcronymRepair 
   * 
   * @param pJCas
   * @param pLineText
   * @param pLine
  */
  // =================================================
  private final  void processTokenAcronymRepair(JCas pJCas, String pLineText, Annotation pLine) {
    
    String lineText = pLineText.replace("  ",  " ");
    String tokens[] = U.split(lineText, " ");
    // what happens when multiple spaces are seen?
    // what offsets get messed up because of the trim()?
    // Cycle through the (word) tokens to find tokens that contain punctuation in them
    
    for ( String token : tokens ) 
      if ( token.contains(".")  )
        if ( U.containsLetters(token) )
          if ( hasTrailing( token, ":" ))
            fixToken(pJCas, token, pLine, pLineText, ":", true);
          else if ( hasTrailing( token, ";" ))
            fixToken(pJCas, token, pLine, pLineText, ";", true);
          else if ( hasTrailing( token, "." ) && hasMoreThanOne(token, '.'))
            fixToken(pJCas, token, pLine, pLineText, null, false);
    
    
  } // end Method  processTokenAcronymRepair() -------
  
  
  // =================================================
  /**
   * hasMoreThanOne
   * 
   * @param pToken
   * @param pPattern
   * @return boolean
  */
  // =================================================
  private final boolean hasMoreThanOne(String pToken, char pPattern) {
    boolean returnVal = false;
    
    if ( pToken != null && pToken.trim().length() > 0 ) {
    
      int count = 0;
     
      for (int i = 0; i < pToken.length(); i++) {
        if (pToken.charAt(i) == pPattern) {
          count++;
          if ( count > 1)
            break;
        }
      }
      
      if ( count > 1 )
        returnVal = true;
    }  
    
    return returnVal;
    
  } // end Method hasMoreThanOne() -------------------


  
  // =================================================
  /**
   * hasTrailing 
   * 
   * @param pToken
   * @param pPattern
   * @return boolean
  */
  // =================================================
  private final boolean hasTrailing(String pToken, String pPattern) {
    boolean returnVal = false;
    
    if ( pToken.trim().endsWith( pPattern))
      returnVal = true;
    
    return returnVal;
    
  } // end Method hasTrailing() ---------------------



  // =================================================
  /**
   * fixToken so, we have a token like P.O. that we want to
   * find in the original text, remove the tokens that
   * overlap it, and replace it with this token
   * 
   * @param pJCas
   * @param pToken
   * @param pLine
   * @param pTrailing
   * @param pSentenceBreak
  */
  // =================================================
  private final void fixToken(JCas pJCas, String pToken, Annotation pLine, String pLineString, String pTrailing, boolean pSentenceBreak) {
    
    int beginOffset = pLineString.indexOf( pToken) + pLine.getBegin();
    int endOffset =   beginOffset + pToken.length();
    
    List<Annotation> badTokens = UIMAUtil.fuzzyFindAnnotationsBySpan( pJCas, Token.typeIndexID, beginOffset, endOffset, true);
    
    
    if ( badTokens != null && !badTokens.isEmpty()) {
      @SuppressWarnings("unused")
      Annotation            aToken = createToken( pJCas, pLine, pToken, pTrailing, pSentenceBreak );
      
      Annotation trailingLastToken = null;
      if ( pTrailing != null && pTrailing.trim().length() > 0 )
          trailingLastToken = createTrailingToken( pJCas, pLine, pToken, pTrailing, pSentenceBreak );
      
      for ( Annotation badToken : badTokens )
        badToken.removeFromIndexes(pJCas);
    }
      
    
  } // =================================================
  /**
   * createToken 
   * 
   * @param pJCas
   * @param badTokens
   * @param pTrailing
   * @param pSentenceBreak
   * @return Annotation
  */
  // =================================================
  private Annotation createToken(JCas pJCas, Annotation pLine, String pToken, String pTrailing, boolean pSentenceBreak) {
    
    WordToken statement = null;
    
    String lineText = pLine.getCoveredText();
    
    int lineOffset = lineText.indexOf( pToken);
    
   
   if ( lineOffset > -1 ) {
      int endOffset = pLine.getBegin() + lineOffset  + pToken.length() ;
      
      statement = new WordToken( pJCas);
      statement.setBegin( pLine.getBegin() + lineOffset );
      statement.setEnd( endOffset);
      statement.setId( "CreateToken_" + this.annotationCounter);
      statement.setSentenceBreak( pSentenceBreak);         
      statement.setAllCapitalization(false);
      statement.setContainsPunctuation(true);
      statement.setDisplayString( pTrailing);
      statement.setInitialCapitalization(false);
      statement.setPunctuationOnly(true);
      statement.setWhiteSpaceFollows(true);
      statement.addToIndexes();
      
      
      String c = statement.getCoveredText() ;
      
      
   }
     return statement;
    
  } // end Method createToken() ----------------------



  // =================================================
  /**
   * createTrailingToken creates a token from the trailing token.
   * This is a sentence ending punctuation like .:;? 
   * 
   * The tricky part is if it's a period, whether to make it
   * a token, or because this is an acronym, that it should
   * stay with the prior token.  
   * 
   * @param pJCas
   * @param pLine
   * @param pToken
   * @param pTrailing
   * @param pSentenceBreak 
   * @return Annotation
  */
  // =================================================
  private final Annotation createTrailingToken(JCas pJCas, Annotation pLine, String pToken, String pTrailing, boolean pSentenceBreak) {
   
   String lineText = pLine.getCoveredText();
   
   int lineOffset = lineText.indexOf( pToken);
   
   Annotation trailingToken = null;
  if ( lineOffset > -1 ) {
     int endOffset = pLine.getBegin() + lineOffset  + pToken.length();
     trailingToken = createTrailingToken( pJCas, endOffset -1, endOffset, pTrailing , pSentenceBreak);
      
   }
     
   return trailingToken;
    
  }
  
  // =================================================
  /**
   * createTrailingToken 
   * 
   * @param pJCas
   * @param pBeginOffset
   * @param pEndOffset
   * @param pTrailing
   * @param pSentenceBreak
   * @return Annotation
  */
  // =================================================
  private final Annotation createTrailingToken(JCas pJCas, int pBeginOffset, int pEndOffset, String pTrailing, boolean pSentenceBreak) {
    
    WordToken statement = new WordToken(pJCas);
    
    statement.setBegin( pBeginOffset);
    statement.setEnd( pEndOffset);
    statement.setId( "_CreateTrailingToken_" + this.annotationCounter);
    statement.setSentenceBreak( pSentenceBreak);
    statement.setAllCapitalization(false);
    statement.setContainsPunctuation(true);
    statement.setDisplayString( pTrailing);
    statement.setInitialCapitalization(false);
    statement.setPunctuationOnly(true);
    statement.setWhiteSpaceFollows(true);
    statement.addToIndexes();
    
    return statement;
    
  } // end Method createTrailingToken() ------------



  // end createTrailingToken() ---------------------



  // end Method fixToken



  // end Method processTokenAcronymRepair() ---------



  // end Method processTokenAcronymRepair



  // =================================================
  /**
   * tokenSlashRepair
   * 
   * @param pJCas
  */
  // =================================================
  
  private void tokenSlashRepair(JCas pJCas) {
    WordToken tokenBefore = null;
    WordToken aToken = null;
    WordToken tokenAfter = null;
    WordToken newToken = null;
  
   
    try {
        List<Annotation> tokens  = UIMAUtil.getAnnotations(pJCas, WordToken.typeIndexID );
        
        if ( tokens != null && !tokens.isEmpty()) {
          
          for ( int i = 0; i < tokens.size(); i++ ) {
            aToken = (WordToken) tokens.get(i);
            
            String chars = aToken.getCoveredText();
            
            if ( chars.contentEquals("/") ) {
              if ( i > 1) {
                tokenBefore = (WordToken)tokens.get(i-1);
               
               if ( i < tokens.size() -1) {
                 tokenAfter = (WordToken) tokens.get(i+1);
                 
                 if ( !tokenBefore.getContainsPunctuation() && !tokenAfter.getContainsPunctuation() )
                   newToken = replaceTokens(pJCas, tokenBefore, aToken, tokenAfter);
                 
               }
             
            }
            }
          }
          
       
     
      } // end if there is a valid snippet
        
        
    
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with finding patterns from regular expressions : " + e.toString() ;
      System.err.println(msg);
    }
  }



  // =================================================
  /**
   * replaceTokens creates a new token from the span of the three tokens
   * @param pJCas 
   * 
   * @param tokenBefore
   * @param aToken
   * @param tokenAfter
   * @return WordToken
  */
  // =================================================
    private final WordToken replaceTokens(JCas pJCas, WordToken tokenBefore, WordToken aToken, WordToken tokenAfter) {
    
      WordToken statement = new WordToken( pJCas);
      statement.setBegin( tokenBefore.getBegin());
      statement.setEnd( tokenAfter.getEnd());
      
      statement.addToIndexes(pJCas );
      statement.setContainsPunctuation( true);
      statement.setDisplayString( statement.getCoveredText());
      statement.setId( "_ReplaceTokens" + this.annotationCounter++);
      
      removeFromIndexes( pJCas, tokenBefore );
      removeFromIndexes( pJCas, aToken);
      removeFromIndexes( pJCas, tokenAfter );
      
    return statement;
  } // end Method replaceTokens() --------------------



    // =================================================
  /**
   * removeFromIndexes 
   * 
   * @param pJCas
   * @param pToken
  */
  // =================================================
  private final void removeFromIndexes(JCas pJCas, WordToken pToken) {
    
    List<Annotation> duplicateTokens = UIMAUtil.getAnnotationsBySpan(pJCas, WordToken.typeIndexID, pToken.getBegin(), pToken.getEnd() );
        
    if ( duplicateTokens != null && !duplicateTokens.isEmpty()) {
     
      if ( duplicateTokens.size() > 1)
        System.err.println("more than one token ");
      for ( Annotation aDuplicate : duplicateTokens) 
         aDuplicate.removeFromIndexes();
    }    
  } // end Method removeFromIndexes() --------------



    // ------------------------------
    // Global Variables
    // ------------------------------
    private int annotationCounter = 0;
  
  
} // end Class Tokenizer --------------------------
