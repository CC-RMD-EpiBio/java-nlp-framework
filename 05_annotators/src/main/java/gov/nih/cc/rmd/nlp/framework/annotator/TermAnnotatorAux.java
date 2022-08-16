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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;



import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.PartOfSpeech;
import gov.va.chir.model.Token;
import gov.va.chir.model.Utterance;
import gov.va.chir.model.VAnnotation;
import gov.va.chir.model.WordToken;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.term.LexicalLookup;
import gov.nih.cc.rmd.nlp.framework.utils.term.PorterStemmer;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.LexRecord;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.TermUtils;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class TermAnnotatorAux implements Runnable {
    
   
  


  // -----------------------------------------
  /**
   * run 
   */
  // -----------------------------------------
  @Override
  public void run() {
    
     int ctr = 0;
     while ( this.runForever == true  ) {
     
       if ( !this.utteranceStack.isEmpty()) {
         ctr = 0;
         processUtterances (  this.utteranceStack);
       
       }// end if the utteranceStack has utterances on it
       
       
       if ( !this.termStack.isEmpty()) {
         repairTerms ( this.termStack );
         
       }  // end if the termStack has terms on it
       
       System.err.println("work done - need to wait or die ");
       
       // ----------------------------
       // If there is no more work to do, put the instance
       // back on the availableThreadStack, and notify 
       // and wait, wait, wait
     
       if (this.utteranceStack.isEmpty() && this.termStack.isEmpty()) {
       
         
         System.err.println("Both queues are empty ");
         System.err.println("Add thread back to available queue ");
         System.err.println("need to wait until a queue is filled ");
         this.availableThreadStack.add( this );
         System.err.println("idle thread, adding it back to the available stack ");
         //synchronized(  this.availableThreadStack ) { this.availableThreadStack.notifyAll();} 
         try {
           System.err.println("waiting until a new sentence is put on the stack " + ctr++);
           //synchronized (this.utteranceStack) { this.utteranceStack.wait(); }
           Thread.sleep(3000);
        
           } catch (Exception e) { e.printStackTrace(); System.err.println("Issue with notify " + e.toString());} 
        }
       
      
     }  // end runForever  - the destroy will change the runForever to false
    
    
  } // end Method run() ========================================
  

  // ==========================================
  /**
   * processUtterances 
   *
   * @param pUtteranceStack
   */
  // ==========================================
  private void processUtterances(LinkedList<AnnotationContainer> pUtteranceStack) {
    
    AnnotationContainer utteranceContainer = null;
    
    while ( pUtteranceStack.size() > 0 )   {
      try {
        utteranceContainer = pUtteranceStack.remove();
        this.termTokenizeSentence( utteranceContainer.getJcas(), (Utterance) utteranceContainer.getAnnotation());
        ((Utterance) utteranceContainer.getAnnotation()).setMarked(true);
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue with finding terms in this sentence " + e.toString());
      }
    }
      
    } // end Method processUtterances() ========================================
  
//==========================================
 /**
  * repairTerms
  *
  * @param pTermStack
  */
 // ==========================================
 private void repairTerms(LinkedList<AnnotationContainer> pTermStack) {
   
   AnnotationContainer termContainer = null;
   
   while ( pTermStack.size() > 0 )   {
     try {
       termContainer = pTermStack.remove();
       this.repairHyphenatedTerm( termContainer.getJcas(), (LexicalElement) termContainer.getAnnotation());
       ((LexicalElement) termContainer.getAnnotation()).setMarked(true);
     } catch (Exception e) {
       e.printStackTrace();
       System.err.println("Issue with repariing hyphenated terms in this sentence " + e.toString());
     }
   }
     
   } // end Method repairTerms() ========================================
 
  
  

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
  
      if ( tokens != null ) {
        terms =  termTokenizeSentence( pJCas, pSentence, tokens);
      } // end if there are any tokens in this utterance
      
      return terms;
    } // end Method termTokenizeSentence() -----

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

      }
      
      
      
      if (( terms != null ) && ( terms.size() > 0)) {
        FSArray termz = UIMAUtil.list2FsArray(pJCas, terms);
        pSentence.setUtteranceLexicalElements(termz);
      }
      
      return terms;
    } // end Method termTokenizeSentence() -----
    
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
     void repairHyphenatedTerm(JCas pJCas, Annotation term) throws Exception {
     
      List<Annotation>  reTokens = this.tokenAnnotator.tokenize(pJCas, term.getCoveredText(), term.getBegin(), true);
      reTokens = removeWhiteSpaceTokens( reTokens);
    
      ArrayList<LexicalElement> moreTerms = new ArrayList<LexicalElement>();
      termTokenizeSentenceAux( pJCas, reTokens, moreTerms, true);

    
    } // End Method repairHyphenatedTerm() ======================
    

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
    for (int iCtr = pSentenceTokens.size() - 1; iCtr >= 0; iCtr--) {
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
    
      String lastWord = aToken.getCoveredText();
      
      String key = normalize(lastWord);
      found = false;
         
      List<LexRecord> lexItems = this.lexicalLookup.get(1, key);
      
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
        if ( !pReTokenized && containsBreakChars( aToken.getCoveredText()) ) { // STOPPED HERE
         returnVal = true;
         break;
         
        } else {
        
         if ( !this.localTermsOnly ) {
            ArrayList<LexRecord> one = new ArrayList<LexRecord>(1);
            one.add(new LexRecord(lastWord));
            coveredLexItems.add(one);
            coveredTokens.add(pSentenceTokens.get(iCtr));
            LexicalElement term = createTerm(pJCas, coveredTokens, one);
            terms.add( term);
          } // end if we only want to capture what's in the lexicon
       
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
      
   
    } // end Method lookup() -------------------




    

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
         
          if ( zTokens != null && !zTokens.isEmpty()) {
            for ( Annotation zToken : zTokens) {
              pCoveredTokens.add(zToken);
            }
          }
          if ( !this.localTermsOnly || !isOrphan( terms )) {
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
      
     List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas, WordToken.typeIndexID, pTerm.getBegin(), pTerm.getEnd());
     if ( tokens != null )
       for ( Annotation token : tokens)
         ((VAnnotation) token).setParent(pTerm);
    
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
        String keyToken = keyTokens[                   currentKeyPosition].toLowerCase();
        
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
          System.err.println("key has a pipe in it! " + key2);
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
        
        if ( !U.isOnlyPunctuation( tokens.get( tokenCtr)) ) {
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
      int  zEnd = pTokens.get(pTokens.size() -1).getEnd();
     
      // ---------------------
      // Check to see if this span has not already been consumed by another term
      if ( (aTerm = coveredByOtherTerm( pJCas, begin, zEnd)) == null) {
    
        aTerm = new LexicalElement( pJCas);
      
      aTerm.setBegin(begin);
      aTerm.setEnd( zEnd );
      synchronized (this ) {
        aTerm.setId( "LexicalElement_" + annotationCounter++);
      }
      aTerm.setProcessMe(true);
      
      aTerm.addToIndexes();   // 1
      
     
      
      FSArray tokens = UIMAUtil.list2FsArray( pJCas, pTokens);
      aTerm.setTokens( tokens);
      
      int euiCtr = 0;
      
      List<LexRecord> finalCandidates = filterCandidatesByCase( pLexItems, aTerm.getCoveredText() );
      

      for (Iterator<LexRecord> i = finalCandidates.iterator(); i.hasNext();) {
        LexRecord record = i.next();

        List<String> euis = record.getEuis();
        List<String> poss = record.getPoss();
        String extraStuff = record.getExtraStuff();
       
        if (euis != null) {
          StringArray euiArray = new StringArray(pJCas, euis.size());
          for (Iterator<String> j = euis.iterator(); j.hasNext();)
            euiArray.set(euiCtr++, j.next());
          aTerm.setEuis(euiArray);
        }

        if (poss != null) {
          int numberOfPoss = poss.size();
          int posCtr = 0;
          FSArray posFeatureArray = new FSArray(pJCas, numberOfPoss);
          for (Iterator<String> j = poss.iterator(); j.hasNext();) {
            String aPos = j.next();
            PartOfSpeech pos = new PartOfSpeech(pJCas);
            pos.setPos(aPos);
            pos.setTypeSystem("PennTreebank");
            posFeatureArray.set(posCtr++, pos);
          }
          aTerm.setPotentialPartsOfSpeech(posFeatureArray);
          aTerm.setSubTerms(record.getSubTermsString());
          aTerm.setCitationForm(record.getCitation());
          aTerm.setSemanticTypes(record.getSemanticType());
        
          if (extraStuff != null)
            addExtraStuffToOtherFeatures(pJCas, aTerm, extraStuff);

        } // end if the poss are not empty

      } // end loop through lexItems
      // aTerm.setLexMatches( termsArray);

      // ------------------------------------------------
      // create a stemmedKey here
      // ------------------------------------------------
      createStemmedKey( aTerm );
      
      }
      return aTerm;
    } // end Method createTerm() ---------------
    
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
      
      StringBuffer buff = new StringBuffer();
      if ( otherFeatures != null && otherFeatures.size() > 0 ) {
        for ( int i = 0; i < otherFeatures.size(); i++ ) buff.append( otherFeatures.get(i) + "|");
      }
      
      if ( extraStuff != null )
        buff.append(extraStuff);
      
      if ( buff.length() > 0 ) {
        StringArray otherFeaturez = new StringArray(pJCas, 1);
        otherFeaturez.set(0, buff.toString());
        aTerm.setOtherFeatures(otherFeaturez);
      }
    }  // End Method addExtraStuffToOtherFeatures() ======================
    

    // =======================================================
    /**
     * coveredByOtherTerm [Summary here]
     * 
     * @param pJCas
     * @param pStart
     * @param pEnd
     * @return LexicalElement
     */
    // =======================================================
    private LexicalElement coveredByOtherTerm(JCas pJCas, int pStart, int pEnd) {
      
    LexicalElement term = null;
    
    List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pStart, pEnd);
      
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

   
 
    
    // ==========================================
    /**
     * initialize 
     *
     * @param localTerminologyFilez
     * @param pLocalResources
     * @param pAvailableThreads  this is a pointer to the thread pool stack 
     *                           this instance should add itself back on the
     *                           available stack when the utteranceStack is empty
     *                           
     * @throws  ResourceInitializationException 
     */
    // ==========================================
    public void initialize(String pLocalTerminologyFilez, String pLocalResources, Stack<TermAnnotatorAux> pAvailableThreads, String pArgs[]) throws ResourceInitializationException  {
      
      this.availableThreadStack = pAvailableThreads ;
      this.runForever = true;
      
      this.initialize(pLocalTerminologyFilez, pLocalResources, pArgs);
      
    }  // end Method initialize() ========================================


    //----------------------------------
    /**
     * initialize loads in the resources needed for term tokenization. Currently, 
     * this involves NLM's LRAGR table.   
     * 
     * @param localTerminologyFilez
     * @param pLocalResources
     * 
     * @throws  ResourceInitializationException 
     **/
    // ----------------------------------
    public void initialize(String localTerminologyFilez, String pLocalResources , String pArgs[]) throws ResourceInitializationException {
        
      String[] localTerminologyFiles = null;
      
      if ( localTerminologyFilez != null && localTerminologyFilez.length() > 0 )
        localTerminologyFiles = U.split(localTerminologyFilez);
     
      String knownAcronymsFile = null;
   
 
      knownAcronymsFile = "resources/com/ciitizen/framework/tokenizer/knownAcronyms.txt";
      try {
        this.tokenize = new TermUtils(knownAcronymsFile);
      }
      catch (Exception e2) {
        e2.printStackTrace();
        String msg = "Issue: TermTokenizer : couldnt create the termUtils |" + knownAcronymsFile + "|" + e2.toString() ;
        System.err.println(msg);
        throw new ResourceInitializationException();
      }
      
      
 
    try {
     
   
      if ( localTerminologyFiles == null ) {
        localTerminologyFiles = new String[1]; 
        localTerminologyFiles[0] = "resources/com/ciitizen/framework/term/SPECIALIST.LRAGR"; 
      }
      
      if ( lexiconsIncludeSPECIALISTLexicon( localTerminologyFiles) )
        this.localTermsOnly = false;
      
      String termRows = U.getOption(pArgs, "--termRows=", "1000" ); 
      this.lexicalLookup = new LexicalLookup( localTerminologyFiles, knownAcronymsFile, pLocalResources, termRows);
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue: TermTokenizer : Couldnt create the lexical lookup " + e.getMessage();
      System.err.println(msg);
      
      throw new ResourceInitializationException();
      }
      
      try {
        this.tokenAnnotator = new TokenAnnotator();
        this.tokenAnnotator.initialize( pArgs);
        
        
        this.porterStemmer = new gov.nih.cc.rmd.nlp.framework.utils.term.PorterStemmer();
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue: TermTokenizer : Couldnt create the secondary tokenizer " + e.getMessage() ;
      System.err.println(msg);
      throw new ResourceInitializationException();
      
    }

      this.utteranceStack = new  LinkedList<AnnotationContainer>();
      this.termStack      = new LinkedList<AnnotationContainer>();
      
    } // end Method initialize() -------
  
    
  // ----------------------------------
  /**
   * destroy cleans up after the last invocation.
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void destroy() {
    
    this.runForever = false;
  } // end Method destroy() ------------
 


//---------------------------------------
//Global Variables
//---------------------------------------

 
    // -------------------------------------------
    // Class Variables
    // -------------------------------------------
    private static int                 annotationCounter = 0;    // new Term Counter.
    private LexicalLookup                  lexicalLookup = null;
    private TermUtils                           tokenize = null;
    private TokenAnnotator                tokenAnnotator = null;
    private boolean                       localTermsOnly = true;
    ProfilePerformanceMeter             performanceMeter = null;
    private PorterStemmer                  porterStemmer = null;
    public  LinkedList<AnnotationContainer> utteranceStack = null;
    public  LinkedList<AnnotationContainer>    termStack = null;
    private Stack<TermAnnotatorAux> availableThreadStack = null;
    private boolean                           runForever = true;
     
    
    
} // end Class TermAnnotator() -----------
