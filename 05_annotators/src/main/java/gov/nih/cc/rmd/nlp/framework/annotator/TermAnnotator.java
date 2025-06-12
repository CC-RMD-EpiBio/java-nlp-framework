// =================================================
/**
 * TermAnnotator identifies single and multi-word terms within text. 
 * This is a dictionary based lookup that relies upon the SPECIALIST Lexicon
 * for the source of the lexemes.  This lookup mechanism is an evolution
 * of the lookup mechanism within NLM's TextTools (and subsequently the
 * MMTx projects).  
 * 
 * The lookup algorithm matches terms within a sentence from left to
 * right, on grounds that more terms have their head word on the right
 * of the string than the left (in English).
 *
 *   Parameters:
 *       localTerminologyFiles   String[] a list of resource relative or fullpath to LRAGR files containing local lexica. 
 *    
 *
 * @author  Guy Divita 
 * @created Mar 1, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;



import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Line;
import gov.va.chir.model.PartOfSpeech;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.Token;
import gov.va.chir.model.Utterance;
import gov.va.chir.model.VAnnotation;
import gov.va.chir.model.WordToken;
import gov.va.vinci.model.temporal.AbsoluteDate;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;

import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.term.LexicalLookup;
import gov.nih.cc.rmd.nlp.framework.utils.term.PorterStemmer;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.LexRecord;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.TermUtils;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class TermAnnotator extends JCasAnnotator_ImplBase {
    
    @SuppressWarnings("unchecked")
    @Override
    // -----------------------------------------
    /**
     * process takes sentences and returns terms attached to that sentence.
     * 
     * @param aJCas
     */
    // -----------------------------------------
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
    
    	
      try {
      this.performanceMeter.startCounter();
      String msg = " Start " + this.getClass().getSimpleName();
      GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", msg);
      
     
      
      if ( this.processMe ) {
      
    // Utterances include sentence slotValue, content headings, dependent content ....
    // if you use utterance, the same span could get termized multiple times because it
    // is within the bounds of a slotValue, contentHeading and sentence all at the same time.
   
    List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Utterance.typeIndexID, true);

    if (sentences != null) {

      // ------------------------------------------------------
      // Walk through the utterances, looking for those that
      // are contentHeaders, and not slotValue's.
      // ------------------------------------------------------
      for (Annotation aSentence : sentences ) {
       
        {
            try {
         
              // Check to see if the utterance has not already been covered.  I.e., sentence that had an embedded list element in it
              List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, aSentence.getBegin(), aSentence.getEnd(),false);

             
              if ( terms == null || terms.isEmpty() )
                termTokenizeSentence(pJCas, (Utterance) aSentence);
              
            
           
            } catch (Exception e) {
              e.printStackTrace();
              GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process","Issue with term tokenizing " + e.toString());
              this.performanceMeter.stopCounter();
              return;
           
            } // throw a uima exception here.
          } //end if this is a sentence to be processed 

      } // end loop through the sentences of the document
    } // end if there are any sentences
  
   
    // ---------------------------------------------
    // Process the content headings of those slot:values and questions that are asserted
    // ---------------------------------------------
    
    List<Annotation> contentHeadings = UIMAUtil.getAnnotations(pJCas, ContentHeading.typeIndexID);
    if ( contentHeadings != null ) {
    for ( Annotation contentHeading : contentHeadings) {
      try {
        
        
        List<?> contentHeadingTerms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, contentHeading.getBegin(), contentHeading.getEnd());
        
        if ( contentHeadingTerms == null || contentHeadingTerms.size() == 0 )
          contentHeadingTerms = termTokenizeSentence(pJCas, (ContentHeading) contentHeading );
        
            if ( ((VAnnotation)contentHeading).getNegation_Status() == null  ||         
                 ((VAnnotation)contentHeading).getNegation_Status().contains("sserted") ) {
            
             ;; 
            } else {
            
              // ----------------------------------------
              // We have a negated content heading - negate any terms within it
              if ( contentHeadingTerms != null && contentHeadingTerms.size() > 0 )
                negateTerms( (List<Annotation>) contentHeadingTerms, ((ContentHeading) contentHeading).getNegation_Status());
              
            }
     
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process","Issue with term tokenizing " + e.toString());
        this.performanceMeter.stopCounter();
        return;
        
      }
    }
    }
    
    // ---------------------------------------------
    // Process the dependent content of  slot:values 
    // ---------------------------------------------
    
    List<Annotation> dependentContents = UIMAUtil.getAnnotations(pJCas, DependentContent.typeIndexID);
    if ( dependentContents != null ) {
    for ( Annotation dependentContent : dependentContents) {
      try {
        List<Annotation> depententContentTerms  = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, dependentContent.getBegin(), dependentContent.getEnd());
        
        if ( depententContentTerms == null || depententContentTerms.size() == 0 ) {
          depententContentTerms = (List<Annotation>) termTokenizeSentence(pJCas,  (Utterance) dependentContent );
        }
        
        if ( ((VAnnotation)dependentContent).getNegation_Status() == null  ||         
            ((VAnnotation)dependentContent).getNegation_Status().contains("sserted") ) {
          ;;
        } else {
          if ( depententContentTerms != null && depententContentTerms.size() > 0 ) 
            negateTerms( depententContentTerms, ((DependentContent) dependentContent).getNegation_Status());
        }
      
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process","Issue with term tokenizing " + e.toString());
        this.performanceMeter.stopCounter();
        return;
      }
    }
    }
    
    // ----------------------------------
    // Final repair - look for terms that contain hyphens, that are not from any lexicon
    //   break them into their parts and add terms for each constituent.
    repairHyphenatedTerms(pJCas);
    
    
    
    // ----------------------------------
    
      }
      msg = " End " + this.getClass().getSimpleName();
      GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", msg);
     
    this.performanceMeter.stopCounter();
    
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process","Issue with term tokenizing " + e.toString());
        this.performanceMeter.stopCounter();
        return;
      }
    
    } // end Method process() ------------------

    // =======================================================
    /**
     * repairHyphenatedTerms  look for terms that contain hyphens, that are not from any lexicon
     *                        break them into their parts and add terms for each constituent.
     * 
     * @param pJCas
     *
     */
    // =======================================================
    private void repairHyphenatedTerms(JCas pJCas)  {
    
      List<Annotation> terms = UIMAUtil.getAnnotations(pJCas, LexicalElement.typeIndexID);
      
      if ( terms != null )
        for ( Annotation term : terms ) {
          if (term.getCoveredText().contains("-") ) {
            if ( ((LexicalElement)term).getEuis() == null ||  ((LexicalElement)term).getEuis().get(0).contains("unknown") ||  ((LexicalElement)term).getSemanticTypes() == null ) {
              try {
                repairHyphenatedTerm( pJCas, term);
              } catch (Exception e) {
                e.printStackTrace();
                GLog.println(GLog.ERROR_LEVEL, this.getClass(), "repairHyphonatedTerms", "Issue with retokenizing around hyphens " + e.getMessage());
                
              }
            }
            
          } // end if there is a hyphen in this term
        } // end loop thru terms
      
    } // End Method repairHyphenatedTerms() ======================
    

    // =======================================================
    /**
     * repairHyphenatedTerm break this term into the parts
     * around the hyphen
     * 
     * @param pJCas
     * @param term
     * @throws Exception 
     */
    // =======================================================
    private void repairHyphenatedTerm(JCas pJCas, Annotation term) throws Exception {
     
      List<Annotation> oldTokens = UIMAUtil.getAnnotationsBySpan(pJCas,  WordToken.typeIndexID, term.getBegin(), term.getEnd());
      
     
      List<Annotation>  reTokens = this.tokenAnnotator.tokenize(pJCas, term.getCoveredText(), term.getBegin(), true);
      reTokens = removeWhiteSpaceTokens( reTokens);
     
      if ( oldTokens != null && !oldTokens.isEmpty() && reTokens != null && !reTokens.isEmpty() ) {
    
        for ( Annotation token: oldTokens) 
          token.removeFromIndexes();
     
      }
      ArrayList<LexicalElement> moreTerms = new ArrayList<LexicalElement>();
      termTokenizeSentenceAux( pJCas, reTokens, moreTerms, true);
     
    } // End Method repairHyphenatedTerm() ======================
    

    // =======================================================
    /**
     * negateTerms negates terms found in content headings
     * 
     * @param pTermsToNegate
     * @param pNegationStatus
     */
    // =======================================================
    private void negateTerms(List<Annotation> pTermsToNegate, String pNegationStatus) {
      
      if ( pTermsToNegate != null ) 
        for ( Annotation term : pTermsToNegate ) {
          
          ((LexicalElement) term).setNegation_Status( pNegationStatus);
          ((LexicalElement) term).setSubject("Patient");
          ((LexicalElement) term).setConditional(false);
        }
          
      
    } // End Method negateTerms() ======================
    

    // -----------------------------------------
    /**
     * termTokenizeSentence creates terms from the
     * tokens in this sentence.
     * 
     * @param pJCas
     * @param pSentence
     * @return 
     * @throws Exception 
     */
    // -----------------------------------------
    public List<?> termTokenizeSentence(JCas pJCas, Utterance pSentence) throws Exception {
      
      List<?> terms = null;
      List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas, WordToken.typeIndexID, pSentence.getBegin(), pSentence.getEnd());
  
      // GLog.println(GLog.DEBUG_LEVEL,this.getClass(), "termTokenizeSentence",  "looking at sentence |" + U.extremeNormalize( pSentence.getCoveredText()));
      if ( tokens != null ) {
  
        // Some sentences are too big to process so process by line instead
        
        if ( tokens.size() > this.sentenceSizeThreshold ) {
          GLog.println(GLog.DEBUG_LEVEL,this.getClass(), "termTokenizeSentence",  "sentence tooooo big turning into line sentences|" + this.sentenceSizeThreshold + "|" + tokens.size()); 
          terms = termTokenizeSentenceByLine( pJCas, pSentence, tokens);
        } else {
          terms =  termTokenizeSentence( pJCas, pSentence, tokens);
        }
      } // end if there are any tokens in this utterance
      
      return terms;
    } // end Method termTokenizeSentence() -----

    // =================================================
    /**
     * termTokenizeSentenceByLine break this sentence into lines
     * and process each line
     * 
     * @param pJCas
     * @param pSentence
     * @param tokens
     * @return terms
     * @throws Exception 
    */
    // =================================================
    private List<LexicalElement> termTokenizeSentenceByLine(JCas pJCas, Utterance pSentence, List<Annotation> tokens) throws Exception {
      ArrayList<LexicalElement> returnVal = null;
      
      // ignore the tokens passed in
      List<Annotation> lines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Line.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), false );
      
      if ( lines != null && !lines.isEmpty()) {
        returnVal = new ArrayList<LexicalElement>();
        for ( Annotation aLine: lines ) {
          List<Annotation>  someTokens = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, WordToken.typeIndexID, aLine.getBegin(), aLine.getEnd(), false );
          Utterance aLineSentence = convertLineToSentence( pJCas, aLine, someTokens);
          GLog.println(GLog.DEBUG_LEVEL,this.getClass(), "termTokenizeSentenceByLine",  "looking at sentence |" + U.extremeNormalize( aLineSentence.getCoveredText()));
          
          boolean reTokenize = termTokenizeSentenceAux(pJCas, someTokens, returnVal, false );
          if (( returnVal != null ) && ( returnVal.size() > 0)) {
            FSArray termz = UIMAUtil.list2FsArray(pJCas, returnVal);
            aLineSentence.setUtteranceLexicalElements(termz);
          }
           
        }
      }
      
      return returnVal;
      
    } // end Method termTokenizeSentenceByLine() --------

    // =================================================
    /**
     * convertLineToSentence
     * 
     * @param pJCas
     * @param pLine
     * @param pTokens
     * @return Utternace
    */
    // =================================================
    private Utterance convertLineToSentence(JCas pJCas, Annotation pLine, List<Annotation> pTokens) {
      
      Sentence returnVal =  new Sentence ( pJCas);
      returnVal.setBegin(pLine.getBegin());
      returnVal.setEnd(pLine.getEnd());
      returnVal.setId("Term:convertLineToSentence_" + this.annotationCounter++);
      returnVal.addToIndexes();
      FSArray someTokens = UIMAUtil.list2FsArray(pJCas, pTokens);
      returnVal.setUtteranceTokens( someTokens);
      
      return returnVal;
    } // end Method convertLineToSentence() ----

    // -----------------------------------------
    /**
     * lookup retrieves the terms from a sentence
     * 
     * @param pJCas
     * @param pSentenceTokens
     * @return 
     * 
     * @throws Exception 
     */
    // -----------------------------------------
    public List<LexicalElement> termTokenizeSentence(JCas pJCas, Utterance pSentence, List<Annotation> pTokens) throws Exception {
     
      ArrayList<LexicalElement> terms = new ArrayList<LexicalElement>(); 
     
    
      boolean reTokenize = termTokenizeSentenceAux(pJCas, pTokens, terms, false );
     
      // -------------------------
      // if the tokens contained token splitting chars that were not part of known terms
      //  re-tokenize breaking on these, and do the term lookup again
   
     
      if ( reTokenize  ) {
        boolean breakOnBreakChars = true;
      
  
        List<Annotation> oldTokens = pTokens;
        FSArray oldTokenz = pSentence.getUtteranceTokens();
        if ( oldTokenz != null ) {
          for ( Annotation t : oldTokens) {
           
          }
          
          oldTokenz.removeFromIndexes(pJCas);
          UIMAUtil.removeAnnotations(pJCas, oldTokens);
        }
        List<Annotation>  reTokens = this.tokenAnnotator.tokenize(pJCas, pSentence.getCoveredText(), pSentence.getBegin(), breakOnBreakChars);
        reTokens = removeWhiteSpaceTokens( reTokens);
      
       
        // ------------------
        // Remove the old tokens and terms and re-attach the new tokens to the sentence
       
        FSArray reTokenz = UIMAUtil.list2FsArray(pJCas, reTokens);
        pSentence.setUtteranceTokens(reTokenz);
        UIMAUtil.removeAnnotations(pJCas, terms);
        terms = new ArrayList<LexicalElement>(terms.size());
        termTokenizeSentenceAux( pJCas, reTokens, terms, true);

      } // end of re-tokenizing needed
      
      
      
      if (( terms != null ) && ( terms.size() > 0)) {
        FSArray termz = UIMAUtil.list2FsArray(pJCas, terms);
        pSentence.setUtteranceLexicalElements(termz);
      }
      
      return terms;
    } // end Method termTokenizeSentence() -----
    
 

    // ------------------------------------------
    /**
     * removeWhiteSpaceTokens
     *
     *
     * @param pTokens
     * @return
     */
    // ------------------------------------------
    public List<Annotation> removeWhiteSpaceTokens(List<Annotation> pTokens) {
    
      ArrayList<Annotation> buff = null;
      if ( pTokens != null && pTokens.size() > 0) {
        buff = new ArrayList<Annotation>( pTokens.size());
        for ( Annotation token : pTokens ) {
        	if ( token != null ) {
        	if ( token.getCoveredText().trim().length() == 0) {
            token.removeFromIndexes();
          } else if ( U.isOnlyPunctuation(token.getCoveredText())){
            token.removeFromIndexes();
          } else {
            buff.add( token);
          }            
        	}
        }
        
      }
      return buff;
    
    }  // End Method removeWhiteSpaceTokens() -----------------------
    

    // -----------------------------------------
    /**
     * lookup retrieves the terms from a sentence
     * 
     * @param pJCas
     * @param pSentenceTokens
     * 
     * @throws Exception 
     */
    // -----------------------------------------
    public boolean termTokenizeSentenceAux(JCas pJCas,  List<Annotation> pSentenceTokens, ArrayList<LexicalElement> terms, boolean pReTokenized) throws Exception {
     
      boolean returnVal = false;
      
      if ( pSentenceTokens == null || pSentenceTokens.size() == 0 )
    	  return false;
      
      ArrayList<Annotation> coveredTokens = new ArrayList<Annotation>();
      ArrayList<List<LexRecord>> coveredLexItems = new ArrayList<List<LexRecord>>();
    
      
      boolean found = false;
      // Loop through the tokens of the sentence backwords
      int iCtr = pSentenceTokens.size() - 1;
    for (; iCtr >= 0; iCtr--) {
      Token    aToken = (Token) pSentenceTokens.get(iCtr);
      if ( aToken == null ) {
       continue;
        
      }
      
      // -----------------------------------
      // skip over tokens that have already been absorbed into a term
      VAnnotation parent = (VAnnotation) aToken.getParent();
      if ( parent!= null && parent.getClass().getSimpleName().contains("LexicalElement") ) {
        continue;
      }
    
      // look up the key directly 
      String lastWord = aToken.getCoveredText();
     
      
      String key = lastWord;
      found = false;
        
      List<LexRecord> lexItems = this.lexicalLookup.get(1, key);
      
      if ( lexItems == null ) {
       
        key = normalize(lastWord);
        found = false;
          
         lexItems = this.lexicalLookup.get(1, key);
        
      }      
     
      if ( lexItems == null) {
        
        String newKey = key;
        if ( key.contains("-"))
          newKey = U.removeHyphen(key);
        
        if ( !newKey.equals(key))
          lexItems =  this.lexicalLookup.get(1, newKey);
        
      }
      
      
      if (lexItems != null) {
        LexRecord aRecord = lexItems.get(0);
        int lookAhead = aRecord.getLongestTermWithThisEnding();
        found = lookupWithLookAhead(pJCas, terms, lexItems, coveredTokens, coveredLexItems, lookAhead, pSentenceTokens, iCtr, key);
      }
      // --------------------------------
      // Create a single token non-lexical record for this
      // non-found token
      // --------------------------------
      if (!found) {
        
        // -----------------------------------
        // re-tokenize and look for the pieces around +/-\: (only do this once)
        // -----------------------------------
        if ( !pReTokenized && containsBreakChars( aToken.getCoveredText()) && !isPeriodOnlyBreakChars( aToken.getCoveredText()) ) { 
         returnVal = true;
         break;
         
        } else {
        
         if ( ! ( U.isOnlyPunctuation(lastWord) && lastWord.length() == 1 )) {
            ArrayList<LexRecord> one = new ArrayList<LexRecord>(1);
            one.add(new LexRecord(lastWord));
            coveredLexItems.add(one);
            coveredTokens.add(pSentenceTokens.get(iCtr));
            LexicalElement term = createTerm(pJCas, coveredTokens, one);
            terms.add( term);
         }
       
        }
      }  else {
         iCtr = (iCtr -coveredTokens.size() +1 ) ;
        
      }
      coveredTokens = new ArrayList<Annotation>();
      
    } // end loop through the tokens of the sentence
      
      // ------------------------------------------
      // returnVale is in reverse order, reverse it
      // ------------------------------------------
      Collections.reverse(coveredLexItems);    
   
      
      
     return returnVal;
      
   
    } // end Method termTokenizeSentenceAux() -------------------




    

    // =================================================
    /**
     * isPeriodOnlyBreakChars returns true if the breakChars 
     * are only periods
     * 
     * @param pTokenString
     * @return boolean
    */
    // =================================================
   private final boolean isPeriodOnlyBreakChars(String pTokenString) {
     
     boolean returnVal = true;
     
     if ( pTokenString != null && pTokenString.length() > 0) {
       
       if        ( U.isOnlyPunctuation(pTokenString) ) 
         returnVal = false;
       
       else {
         
         char[] buff = pTokenString.toCharArray();
         for ( int i = 0; i < buff.length; i++ )  {
           if ( U.isSentenceBreakChar( buff[i]) && buff[i] != '.') {
             returnVal = false; 
             break;
           }
         }
       
       }
     }
  
     return returnVal;
    } // end Method isPeriodOnlyBreakChars() --------

    // =======================================================
    /**
     * getTokenAnnotator returns the tokenizer used to tokenize terms
     * 
     * @return TokenAnnotator
     */
    // =======================================================
    public TokenAnnotator getTokenAnnotator() {
     
      return this.tokenAnnotator;
    }  // End Method getTokenAnnotator() ======================
    

    // ------------------------------------------
    /**
     * containsBreakChars returns true if the token
     * contains a breakchar, but isn't itself a breakchar
     *
     *
     * @param coveredText
     * @return
     */
    // ------------------------------------------
    private boolean containsBreakChars(String pTokenString) {
      boolean returnVal = false;
    
      if ( pTokenString != null && pTokenString.length() > 0) {
        
        if        ( U.isOnlyPunctuation(pTokenString) ) 
          returnVal = false;
        
        else if ( U.containsTokenSplittingChar(pTokenString )) 
          if ( U.containsLetters( pTokenString ))
            returnVal = true;
      }
      return returnVal;  
    }  // End Method containsBreakChars() -----------------------
    

    // -----------------------------------------
    /**
     * lookupWithLookAhead
     * @param pTerms
     * @param pCoveredTokens
     * @param pCoveredLexItems
     * @param pLookAhead
     * @param pSentenceTokens
     * @param pICtr
     * @param pLookAhead2
     * @return
     * @throws Exception 
     */
    // -----------------------------------------
  private boolean lookupWithLookAhead(
                                        JCas pJCas,
                                        ArrayList<LexicalElement> pTerms, List<LexRecord> lexItems,
                                        ArrayList<Annotation> pCoveredTokens,
                                        ArrayList<List<LexRecord>> coveredLexItems,
                                        int lookAhead,
                                        List<Annotation> pSentenceTokens,
                                        int iCtr,
                                        String key) throws Exception {
    
    Token aToken = (Token) pSentenceTokens.get(iCtr);
    LexicalElement aTerm  = null;
    boolean found = false;
    List<String> lookAheadKeys = generateLookAheadKeys(pSentenceTokens, iCtr, lookAhead);

    if ((lookAheadKeys == null) || (lookAheadKeys.size() < 1)) {
      found = true;
     
      pCoveredTokens.add(aToken);
      coveredLexItems.add(lexItems);
      aTerm = createTerm(pJCas, pCoveredTokens, lexItems);
      pointTokensToTerm(pJCas, aTerm);
      pTerms.add( aTerm);
      pCoveredTokens = new ArrayList<Annotation>();
      
    } else {
      for (int k = 0; k < lookAheadKeys.size(); k++) {
      String fields[] = U.split(lookAheadKeys.get(k));
        String lookAheadKey = fields[0];
        int numberOfTokens = Integer.valueOf(fields[1]);
        List<LexRecord> terms = lookup(lookAheadKey, numberOfTokens);
        
        // --------------------------------------------------
        // If a term was found, move the token in focus back
        if (terms != null) {
          found = true;
          coveredLexItems.add(terms);
         List<Annotation>zTokens = mapKeyToCoveredTokens( lookAheadKey, pSentenceTokens, iCtr);
        
         if ( zTokens != null ) {
            for ( Annotation zToken : zTokens) {
              pCoveredTokens.add(zToken);
            }
          }
          if ( !isOrphan( terms )) {
           
            aTerm = createTerm(pJCas, pCoveredTokens, terms);
            pTerms.add(aTerm);
            // -------------------------------
            // make the tokens of this new term point to 
            // this new term
            pointTokensToTerm(pJCas, aTerm);
            
//            pCoveredTokens = new ArrayList<Annotation>();  <----  not sure why this was here!!
            break;
          }
        } // end if lexical elements were found
        
      } // end loop through potential terms
    } // end if there are look ahead keys
    return found;
  } // end Method lookupWithLookAhead() -----

  

    // =======================================================
    /**
     * pointTokensToTerm points each of the token's parent to this term
     * 
     * @param pJCas
     * @param pTerm
     */
    // =======================================================
    private void pointTokensToTerm(JCas pJCas, LexicalElement pTerm) {
      
      FSArray tokenz = pTerm.getTokens();
      if (tokenz != null) {
        @SuppressWarnings("unchecked")
        List<Annotation>tokens = UIMAUtil.fSArray2List(pJCas, tokenz);
        if ( tokens != null )
          for ( Annotation token : tokens)
          ((VAnnotation) token).setParent(pTerm);
      } // end if there are any tokenz
    }  // End Method pointTokensToTerm() ======================
    

    // =======================================================
    /**
     * returns the tokens from the sentence that cover this key
     *
     * @param pJCas
     * @param pKey
     * @param pSentenceTokens
     * @param pStartingPosition
     * @return Tok
     */
    // =======================================================
    private List<Annotation> mapKeyToCoveredTokens( String pKey, List<Annotation>pSentenceTokens, int pStartingPosition) {
    
      List<Annotation> coveredTokens = new ArrayList<Annotation>();
      String keyTokens[] = U.split(pKey, " ");
      int currentPosition = pStartingPosition;
      int currentKeyPosition = 0;
      
      boolean done = false;
      while ( currentPosition >= 0  && !done ) {
        
        String tokenFromSentence = pSentenceTokens.get(currentPosition).getCoveredText().toLowerCase();
        // GD 2018-08-21
        // pipes were replaced with ; in generateLookAhead - that needs to be done here to match pipes
        tokenFromSentence =   tokenFromSentence.replaceAll("\\|", ";");
        String keyToken = keyTokens[ currentKeyPosition].toLowerCase();
        
        if ( tokenFromSentence.contains( keyToken )) {
          coveredTokens.add( pSentenceTokens.get(currentPosition));
          currentKeyPosition++;
          currentPosition--;
        } else if ( U.isOnlyPunctuation(tokenFromSentence )) {
          currentPosition--;
        }
        if ( currentKeyPosition == keyTokens.length ) {
          done = true;
          pStartingPosition = currentPosition;
          ArrayList<Annotation> reversedCoveredTokens = new ArrayList<Annotation>(coveredTokens.size());
          for ( int i = coveredTokens.size() -1; i>=0; i-- ) 
            reversedCoveredTokens.add( coveredTokens.get(i));
          coveredTokens = reversedCoveredTokens;
        }
      }

      if ( coveredTokens.size() == 0 )
        coveredTokens = null;
      
      return coveredTokens;
    }  // End Method getNumberOfRealTokensFromTerms() ======================
    

    // =======================================================
    /**
     * isOrphan checks to see if the terms are not ophran terms
     * 
     * @param terms
     * @return boolean
     */
    // =======================================================
    private boolean isOrphan(List<LexRecord> terms) {
      
      boolean returnVal = false;
      
      if ( terms != null ) {
        for ( LexRecord aTerm: terms ) {
          if ( aTerm.getOrphanTerm() ) {
            returnVal = true;
            break;
          }
        }    
      }
      return returnVal;
    }  // End Method isOrphan() ======================
    

    // -----------------------------------------
    /**
     * lookup retrieves a list of terms given a key.
     * Multiple records could be returned for a key based
     * on case and part of speech.
     * 
     * @param pKey
     * @param pNumberOfTokens
     * @return
     */
    // -----------------------------------------
    private List<LexRecord> lookup(String pKey, int pNumberOfTokens) throws Exception {
      List<LexRecord> returnValue = null;
      
   
      
      returnValue = lexicalLookup.get( pNumberOfTokens, pKey);
   
      return returnValue;
    } // end Method lookup


    // -----------------------------------------
    /**
     * generateLookAheadKeys returns a list of strings that cover a token
     * in a sentence and the n tokens before it.
     *    For look ahead = 3
     *                              \|/
     *          sentence-> a b c d e f g
     * 
     *   will return -------------\|/
     *                         c d e f          f e d c
     *                           d e f          f e d
     *                             e f          f e
     *                               f          f
     *                                            /|\
     *  That's a white lie.  it will return   -----+
     * 
     * @param pSentenceTokens
     * @param pI
     * @param pLookAhead
     * @return List<String>
     */
    // -----------------------------------------
    private List<String> generateLookAheadKeys(List<Annotation> pSentenceTokens, int pTokenInFocus, int pLookAhead) {
     
      ArrayList<String> buff = new ArrayList<String>();
      
    
      for ( int lookAhead = pLookAhead; lookAhead > 0; lookAhead--) {
        
        StringBuffer key = new StringBuffer();
        key.append( pSentenceTokens.get( pTokenInFocus).getCoveredText() );
        key.append(" ");
        int i = 1;
        int tokenCtr = i;
        while ( tokenCtr < lookAhead  && pTokenInFocus - i >= 0 ) {

          
          Token theToken = (Token) pSentenceTokens.get( pTokenInFocus -i);
          
          if ( theToken.getPunctuationOnly() && theToken.getCoveredText().length() == 1) {
            
          } else {
            tokenCtr++;
            key.append( theToken.getCoveredText());
            key.append(" ");
          }
          i++;
        } 
        String key2 = key.toString().replaceAll("\\|", ";").trim();
        if (key2.contains("|")) {
          GLog.println(GLog.ERROR_LEVEL, this.getClass(), "generateLookAheadString","key has a pipe in it! " + key2);
          System.exit(0);
        }
        buff.add( key2 + "|" + tokenCtr);
      }
      
      return buff;
    } //end Method generateLookAheadString() -


   



    // -----------------------------------------
    /**
     * normalize will create a key out of the term
     * The key is in reverse order, lowercased, and punctuation is
     * replaced by spaces. 
     * @param pTerm
     * @return
     */
    // -----------------------------------------
    private String normalize(String pTerm) throws Exception {
      StringBuffer key = new StringBuffer();
      
      List<String> tokens = tokenize.tokenize( pTerm);
      
      for ( int tokenCtr = tokens.size() -1; tokenCtr >= 0 ; tokenCtr--) {
        
        if ( !U.isOnlyPunctuation( tokens.get( tokenCtr)) ) { // ----> didn't work || tokens.get(tokenCtr).contentEquals(",") ) {
          char[] buff = tokens.get(tokenCtr).toCharArray();
          for (int i = 0; i < buff.length; i++) if (U.isPunctuation(buff[i])) buff[i] = ' ';
          String buff2 = new String(buff).trim();
          key.append( buff2 );
          if ( tokenCtr > 0 )
            key.append(" ");
        } else {
          key.append(" ");
        }
      }
      
      return key.toString().trim();
    } // end Method key() ----------------------
    
    
    // -----------------------------------------
    /**
     * createTerm creates a Term.  The term will include a key that 
     * is the uninflected, lowercased, term or if not from the
     * dictionary, normalized by these rules
     *    if all numbers, the numbers
     *    if all punctuation, null
     *    if a combination of punctuation and numbers the same form
     *    if a contains letters then it gets porter stemmed, lowercased and trimmed. 
     * 
     * @param pJCas
     * @param pTokens
     * @param pLexItems
     * 
     * @return LexicalElement
     */
    // -----------------------------------------
    private LexicalElement createTerm(JCas pJCas, ArrayList<Annotation> pTokens, List<LexRecord> pLexItems) {

      LexicalElement aTerm = null;

      int begin = pTokens.get(0).getBegin();
      int zEnd = pTokens.get(pTokens.size() - 1).getEnd();

      // ---------------------
      // Check to see if this span has not already been consumed by another term
      aTerm = coveredByOtherTerm(pJCas, begin, zEnd);
      ;;
      if ((aTerm == null) || (  aTerm.getClass().getName().contains("Number"))) {

       
          aTerm = new LexicalElement(pJCas);

          aTerm.setBegin(begin);
          aTerm.setEnd(zEnd);
          // aTerm.setDisplayString( aTerm.getCoveredText() );
          aTerm.setId("TermAnnotator_" + this.annotationCounter++);
          aTerm.setProcessMe(true);

          aTerm.addToIndexes(); // 1

          FSArray tokens = UIMAUtil.list2FsArray(pJCas, pTokens);
          aTerm.setTokens(tokens);

          String p = null;
          try {
            p = aTerm.getCoveredText();
          } catch (Exception e) {
            e.printStackTrace();
            GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createTerm",
                "issue with this term in the termAnnotator - the offets are messed up because the tokens are not in order |"
                    + begin + "|" + zEnd);
            GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createTerm",
                "------------------------------------------------------");
            System.err.print(pJCas.getDocumentText());
            GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createTerm",
                "-------------------------------------------------------");

          }
          List<LexRecord> finalCandidates = filterCandidatesByCase(pLexItems, p);

          updateTermWithLexRecords(pJCas, aTerm, finalCandidates);

          // ------------------------------------------------
          // create a stemmedKey here
          // ------------------------------------------------
          createStemmedKey(aTerm);
          String sectionName = VUIMAUtil.deriveSectionName(aTerm);
          aTerm.setSectionName(sectionName);

        
      }
      return aTerm;
    } // end Method createTerm() ---------------
    
    // =================================================
    /**
     * updateTermWithLexRecords accumulates the euis, pos, semanticTypes, citationForms, extraStuff
     * from each LexRecord and stuffs it onto the term.
     * 
     * Note:  Not dealing with passing on the subterm info here.
     * 
     * @param pJCas
     * @param aTerm
     * @param finalCandidate
    */
    // =================================================
   private void updateTermWithLexRecords(JCas pJCas, LexicalElement aTerm, List<LexRecord> finalCandidates) {
      
     HashSet<String>                          euisHash = new HashSet<String>();
     HashSet<String>                          possHash = new HashSet<String>();
     HashSet<String>    extraStuffAndOtherFeaturesHash = new HashSet<String>();
     HashSet<String>                 semanticTypesHash = new HashSet<String>();
     String                               citationForm = null;
  // HashSet<String>                      subTermsHash = new HashSet<String>();
     
    
     for ( LexRecord record :  finalCandidates ) {
       
     
       List<String> euis = record.getEuis();
       StringBuffer euiz = new StringBuffer();
       if ( euis != null )  
         for ( String eui: euis) {
           euisHash.add( eui);
           euiz.append( "|" + eui );
         }
      
       List<String> poss = record.getPoss();
       if ( poss != null ) 
         for ( String pos : poss ) 
           possHash.add( pos );
       
       
       StringArray otherFeaturez = aTerm.getOtherFeatures();
       if ( otherFeaturez != null && otherFeaturez.size() > 0 )
         for ( int ofsa = 0; ofsa < otherFeaturez.size(); ofsa++ )
          extraStuffAndOtherFeaturesHash.add(  otherFeaturez.get(ofsa));
       
       String extraStuff = record.getExtraStuff();  
       // GD - I'm adding the cui to extra stuff here to keep all the info together
       if ( extraStuff != null ) 
         extraStuffAndOtherFeaturesHash.add(  extraStuff +  euiz);
      
       semanticTypesHash.add ( record.getSemanticType() );
       
       if ( citationForm == null )
         citationForm = record.getCitation() ;
       
       
     } // end loop through the lexRecords -----------------------
       
     // -----------------------------------------
       // Here's where you set the aTerm values (after you've accumulated them from 
       // the candidates!
    
     // -------------------------
     // euis
     List<String> euiList = new ArrayList<String>( euisHash.size());
     for ( String eui : euisHash)  euiList.add( eui );
     StringArray euiz = UIMAUtil.list2StringArray(pJCas, euiList);
     aTerm.setEuis(euiz);
     
     // ------------------------
     // pos 
     FSArray posFeatureArray = new FSArray(pJCas, possHash.size());
     List<String> posList = new ArrayList<String> ( possHash.size());
     int posCtr = 0;
     for ( String aPos : possHash ) {
       PartOfSpeech pos = new PartOfSpeech(pJCas);
       pos.setBegin( aTerm.getBegin());
       pos.setEnd( aTerm.getEnd());
       pos.setPos(aPos);
       pos.setTypeSystem("PennTreebank");
       posFeatureArray.set(posCtr++, pos);
     }
     aTerm.setPotentialPartsOfSpeech(posFeatureArray);
     
     // ------------------------------
     // citation  choose the first - as good as any
     aTerm.setCitationForm(citationForm);
    
     // ---------------------------
     // semanticType
     StringBuffer buff = new StringBuffer();
     for ( String semanticType : semanticTypesHash )
       buff.append( semanticType + ":");
     String semanticTypes = buff.toString().substring(0, buff.length() -1);
     aTerm.setSemanticTypes(semanticTypes );
     
    
     
     aTerm.setSubject("Patient"); // <---- the default, will be changed by the assertion annotator
     
     // --------------------------
     // extra stuff
     if ( extraStuffAndOtherFeaturesHash.size() > 0  ) {
       StringArray otherFeaturez = new StringArray( pJCas, extraStuffAndOtherFeaturesHash.size() );
       int i = 0;
       for ( String extraStuff : extraStuffAndOtherFeaturesHash )
         otherFeaturez.set( i++, extraStuff);
       aTerm.setOtherFeatures( otherFeaturez );
     }
     
      
    } // end updateTermWithLexRecords() ----------------

    // =================================================
    /**
     * addEuisToTerm adds any new euis to the list of
     * euis' on the term - Note: the term may not have any euis on it 
     * or the new euis might be empty.  Note 
     * 
     * @param pJCas
     * @param aTerm
     * @param euis
    */
    // =================================================
   private void addEuisToTerm(JCas pJCas, LexicalElement aTerm, List<String> euis) {
     
     StringArray euiz = aTerm.getEuis();
     int euizSize = 0;
     
     List<String> newEuis = new ArrayList<String>(euis.size());
     
     if ( euiz != null && euiz.size() > 0 && euis!= null && euis.size() > 0  ) {
       euizSize = euiz.size();
       for ( int i = 0; i < euiz.size(); i++ ) 
         for ( int k = 0; k < euis.size(); k++ ) 
           if ( !euiz.get(i).equals(euis.get(k)) )
             newEuis.add( euis.get(k));
     }
    
     if (( euiz == null || euiz.size() == 0 ) && euis != null && euis.size() > 0 ) 
       for ( int k = 0; k < euis.size(); k++ ) 
         newEuis.add( euis.get(k));
     
     if ( newEuis != null && newEuis.size() > 0 ) {
       int newSize = euizSize + newEuis.size();
       StringArray updatedEuis = new StringArray(pJCas, newSize);
      int i = 0;
       for ( ; i < euizSize; i++ ) 
         updatedEuis.set(i, euiz.get(i) );
       int k = 0;
       for (  ; i < newSize; i++ ) 
         updatedEuis.set(i, newEuis.get(k++) );
      
       aTerm.setEuis(updatedEuis);
       
     }
     
     
    } // end Method addEuisToTerm() -------------

    // ==========================================
    /**
     * createStemmedKey  includes a key that 
     * is the uninflected, lowercased, term or if not from the
     * dictionary, normalized by these rules
     *    if all numbers, the numbers
     *    if all punctuation, null
     *    if a combination of punctuation and numbers the same form
     *    if a contains letters then it gets porter stemmed, lowercased and trimmed. 
     * 
     *  This tool uses the openNLP version of the porter stemmer
     *  
     * @param pCitationForm
     * @return String
     */
    // ========================================================
  public String createStemmedKey(LexicalElement pTerm) {

    String normed = pTerm.getCitationForm();

    if (normed == null) {
      String key = pTerm.getCoveredText();
      if (key != null && key.length() > 0) {

        // null out punctuation
        if (U.isOnlyPunctuation(key)) {
          normed = null;
        } else if (U.isNumber(key))
          normed = key;
        else if (!U.containsLetters(key)) {
          normed = key;

        } else {
          key = key.toLowerCase().trim();
          normed = this.porterStemmer.stem(key);
        } // end if no citation form but not null
      }
    }

    if (normed != null)
      normed = normed.toLowerCase().trim();
    
    pTerm.setStemmedKey( normed );

    return normed;
  } // end Method createStemmedKey() =========================
    

    // =======================================================
    /**
     * addExtraStuffToOtherFeatures [Summary here]
     * 
     * @param aTerm
     * @param extraStuff
     */
    // =======================================================
    private void addExtraStuffToOtherFeatures(JCas pJCas, LexicalElement aTerm, String extraStuff) {
      
      StringArray otherFeatures = aTerm.getOtherFeatures();
     
      int numberOfFeatures = -1;
      boolean found = false;
      
      if ( otherFeatures != null && otherFeatures.size() > 0  && extraStuff != null && extraStuff.trim().length() > 0 ) {
        for ( int i = 0; i < otherFeatures.size(); i++ ) {
          String aFeature = otherFeatures.get(i);
         
          if ( aFeature.contains( extraStuff.trim())) {
            found = true;
            break;
          }
        }
  
        if ( !found ) {
          numberOfFeatures = otherFeatures.size() + 1;
          
          StringArray otherFeaturesBetter = new StringArray(pJCas,  numberOfFeatures);
          for ( int i = 0; i < numberOfFeatures -1; i++ ) 
            otherFeaturesBetter.set(i, otherFeatures.get(i));
          otherFeaturesBetter.set( numberOfFeatures -1, extraStuff);
          aTerm.setOtherFeatures(otherFeaturesBetter);
          
        }
      } else if ( extraStuff != null && extraStuff.trim().length() > 0 ) { // end if there are features
        StringArray otherFeaturesBetter = new StringArray(pJCas, 1);
        otherFeaturesBetter.set(0,  extraStuff);
        aTerm.setOtherFeatures(otherFeaturesBetter);
      
      }
      
    }  // End Method addExtraStuffToOtherFeatures() ======================
    

    
    // =======================================================
    /**
     * coveredByOtherTerm returns the covered term
     * 
     * @param pJCas
     * @param pStart
     * @param pEnd
     * @return LexicalElement  (null if not)
     */
    // =======================================================
    private LexicalElement coveredByOtherTerm(JCas pJCas, int pStart, int pEnd) {
      
    LexicalElement term = null;
    
    List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pStart, pEnd, false);
      
    
    if ( terms != null && terms.size() > 0 )
      term = (LexicalElement)  terms.get(0);
      
     return term;
    } // End Method coveredByOtherTerm() ======================
    

    // -----------------------------------------
    /**
     * filterCandidatesByCase returns a smaller list of lexItems
     * that match exactly the covered text, unless there are none,
     * in which case, nothing gets filtered out.
     * 
     * @param pLexItems
     * @param pCoveredText
     * @return
     */
    // -----------------------------------------
    private List<LexRecord> filterCandidatesByCase(List<LexRecord> pLexItems, String pCoveredText) {
      
      ArrayList<LexRecord> returnValue = new ArrayList<LexRecord>(pLexItems.size());
      for ( Iterator<LexRecord> i = pLexItems.iterator(); i.hasNext();) {
        LexRecord record = i.next();
        
        if ( record.getTerm().equals( pCoveredText)) 
          returnValue.add( record);
        
      } // end loop through lexItems
      
      if ( returnValue.size() == 0 ) {
        returnValue = (ArrayList<LexRecord>) pLexItems;
      }
      
      return returnValue;
    } // end Method filterCandidatesByCase() ---

   
   

   


    // =======================================================
    /**
     * lexiconsIncludeSPECIALISTLexicon returns true if one
     * of the terminology files is the SPECIALIST Lexicon
     * 
     * @param pTerminologyFiles
     * @return boolean
     */
    // =======================================================
    private boolean lexiconsIncludeSPECIALISTLexicon(String[] pTerminologyFiles) {
      boolean returnVal = false;
      
      if ( pTerminologyFiles != null ) 
        for ( String terminologyFileName : pTerminologyFiles )
          if ( terminologyFileName.toLowerCase().contains("specialist")) {
            returnVal = true;
            break;
          }
      return returnVal;
      // End Method lexiconsIncludeSPECIALISTLexicon() ======================
    }

    //----------------------------------
    /**
     * initialize loads in the resources needed for term tokenization. Currently, 
     * this involves NLM's LRAGR table.   
     * 
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
     
     // if ( aContext != null)
      //  super.initialize(aContext);
      
      String[] localTerminologyFiles = null;
      String   localTerminologyFilez = null;
      String   localResources = null;
      String[] args = null;
    
      
      try {
       args                  = (String[]) aContext.getConfigParameterValue("args"); 
       
       initialize( args);
       
       
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize","Issue with getting the local terminology files " + e.toString());
        throw new ResourceInitializationException();
 
      }
    
    
     
    } // end Method initialize() ---------

    
 
    
    //----------------------------------
    /**
     * initialize loads in the resources needed for term tokenization. Currently, 
     * this involves NLM's LRAGR table.   
     * 
     * @param pArgs
     * 
     **/
    // ----------------------------------
    public void initialize(String[] pArgs) throws ResourceInitializationException {
        
      String localTerminologyFilez = null;
      String localResources = null;
      String termRows = "1000";
    
      this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
      
      this.sentenceSizeThreshold = Integer.parseInt(U.getOption( pArgs,  "--sentenceSizeThreshold=", "100"));
      
      if ( pArgs != null && pArgs.length > 0 ) {
        localTerminologyFilez = U.getOption(pArgs, "--localTerminologyFiles=", "");
        localResources = U.getOption(pArgs, "--localResources", "./");
        termRows = U.getOption( pArgs,  "--termRows=", "1000");
        
           
      }
         
      initialize( localTerminologyFilez, localResources , termRows, pArgs);
      
    } // end Method Initialize();
    
    //----------------------------------
    /**
     * initialize loads in the resources needed for term tokenization. Currently, 
     * this involves NLM's LRAGR table.   
     * 
     * @param localTerminologyFilez
     * @param pLocalResources
     * 
     **/
    // ----------------------------------
    public void initialize(String localTerminologyFilez, String pLocalResources, String pTermRows, String[] pArgs ) throws ResourceInitializationException {
        
      String[] localTerminologyFiles = null;
      
      if ( localTerminologyFilez != null && localTerminologyFilez.length() > 0 ) {
        if ( localTerminologyFilez.contains(":") )
          localTerminologyFiles = U.split(localTerminologyFilez,":");
        else
          localTerminologyFiles = U.split(localTerminologyFilez);
      }
     
      String knownAcronymsFile = null;
   
 
      knownAcronymsFile = "resources/vinciNLPFramework/tokenizer/knownAcronyms.txt";
      try {
        this.tokenize = new TermUtils(knownAcronymsFile);
      }
      catch (Exception e2) {
        e2.printStackTrace();
        String msg = "Issue: TermTokenizer : couldnt create the termUtils |" + knownAcronymsFile + "|" + e2.toString() ;
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize",msg);
        throw new ResourceInitializationException();
      }
      
      
 
    try {
     
   
      if ( localTerminologyFiles == null ) {
        GLog.println(GLog.WARNING_LEVEL, this.getClass(), "initialize", "No terminology file set, are you sure?" );
        this.processMe = false;
      } else {
     
         this.lexicalLookup = new LexicalLookup( localTerminologyFiles, knownAcronymsFile, pLocalResources, pTermRows);      }
   
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue: TermTokenizer : Couldnt create the lexical lookup " + e.getMessage();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize",msg);
      
      throw new ResourceInitializationException();
      }
      
      try {
        this.tokenAnnotator = new TokenAnnotator();
        this.tokenAnnotator.initialize(pArgs);
        
        
        this.porterStemmer = new gov.nih.cc.rmd.nlp.framework.utils.term.PorterStemmer();
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue: TermTokenizer : Couldnt create the secondary tokenizer " + e.getMessage() ;
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize",msg);
      throw new ResourceInitializationException();
      
    }
    
    } // end Method initialize() -------
      
  // ==========================================
    /**
     * addQueryTerms turns query terms into lragr rows
     *   and adds them to the lexicon
     *
     * @param pLexicalLookup
     * @param pQueryTerms  (colon delmited terms)
     */
    // ==========================================
    private void addQueryTerms(LexicalLookup pLexicalLookup, String pQueryTerms) {
     
      String terms[] = U.split( pQueryTerms, ":" );
      String termCategory = "SearchTerm";
      for ( String term: terms ) {
        String normalizedTerm = term.toLowerCase().trim();
        String lragrRow = "ST" + U.zeroPad(localTermId++, 4)  + "|" +  // id
                          term.toLowerCase().trim()           + "|" +  // key
                          "noun|count(thr_sing)"              + "|" +  // category|inflection
                          normalizedTerm                      + "|" +  // uninflected form
                          normalizedTerm                      + "|" +  // citation form
                          termCategory ;
        this.lexicalLookup.addTerm( lragrRow );
        
      }
      
    } // end Method addQueryTerms() =============
    

  // ----------------------------------
  /**
   * destroy cleans up after the last invocation.
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void destroy() {
    
    try {
      if ( this.lexicalLookup != null )
       this.lexicalLookup.finalize();
    }
    catch (Throwable e) {
      e.printStackTrace();
      getContext().getLogger().log(Level.SEVERE, "Tokenization: known acronyms are not specified.");
    } // end catch()
    
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
    
  } // end Method destroy() ------------
 


//---------------------------------------
//Global Variables
//---------------------------------------

 
    // -------------------------------------------
    // Class Variables
    // -------------------------------------------
    private int         annotationCounter = 0;    // new Term Counter.
    private int             localTermId = 0;
    private LexicalLookup   lexicalLookup = null;
    private String          queryTerms = null;
    private TermUtils       tokenize = null;
    private TokenAnnotator tokenAnnotator = null;
  //  private boolean localTermsOnly = true;
    private ProfilePerformanceMeter       performanceMeter = null;
    private PorterStemmer            porterStemmer = null;
    private boolean processMe = true;
    private int            sentenceSizeThreshold = 100000;
    
    
} // end Class TermAnnotator() -----------
