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
 * PhraseAnnotator identifies phrases within sentences. This phrase chunker 
 * uses the phrase chunker embedded within hitex.  That phrase chunker 
 * was an opennlp trained maxent module developed for ctakes.  This chunker
 * uses the same rules that hitex included as an add-on.
 *
 *
 * @author  Guy Divita 
 * @created Jun 22, 2011
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.MinimalPhrase;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.Snippet;
import gov.va.chir.model.Token;
import gov.va.chir.model.Utterance;
import gov.va.chir.model.WordToken;
import gov.nih.cc.rmd.nlp.framework.utils.DTaggerUtilities;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;

import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import opennlpL.tools.chunker.Chunker;
import opennlpL.tools.lang.english.TreebankChunker;


public class PhraseAnnotator extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  /**
   * process uses the openNLP chunker to label tokens with phrase tags. 
   * The phrase tags are used to create "chunk" phrases.  (I call these
   * vinci phrases).  "Chunk" phrases are then reviewed and some are
   * (optionally) combined to make longer phrases. The reviewed phrases
   * are the final (chir) phrases.  
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
                
    this.performanceMeter.startCounter();

    try {
    AnnotationIndex<Annotation> sentenceIndex = pJCas.getAnnotationIndex(Utterance.type);
    List<Annotation> sentences = UIMAUtil.annotationIndex2List(sentenceIndex);
    List<gov.va.vinci.model.Phrase> nonFinalPhrases = null;
    List<gov.va.chir.model.Phrase> finalPhrases = null;
    HashSet<String> keys = new HashSet<String>();
    
    if (sentences != null) {
      // --------------------------------
      // Iterate through the utterances
      // --------------------------------
      for (Annotation sentence : sentences) {

        if (processThisUtterance(sentence)) {

          ArrayList<String> wordList = new ArrayList<String>();
          ArrayList<String> tagList = new ArrayList<String>();
          List<Annotation> tokens = getWordsAndTags(pJCas, (Utterance) sentence, wordList, tagList);

          String[] words = wordList.toArray(new String[wordList.size()]);
          String[] tags = tagList.toArray(new String[tagList.size()]);

          String[] tokenPhraseTags = this.chunker.chunk(words, tags);

          nonFinalPhrases = tokenPhraseTagsIntoNonFinalPhrases(pJCas, (Utterance) sentence, tokenPhraseTags, tokens);

          if ((nonFinalPhrases != null) && (nonFinalPhrases.size() > 0)) {
            finalPhrases = combineOfPhrases(pJCas, nonFinalPhrases, keys);

            createMinimalPhrases(pJCas, finalPhrases);
          }
          // --------------------------------------
          // remove the non-final phrases
         
          if (( nonFinalPhrases != null ) && ( nonFinalPhrases.size() > 0))
            for (gov.va.vinci.model.Phrase phrase : nonFinalPhrases)
              phrase.removeFromIndexes();

          
          // --------------------------------------
          // Attach the phrases, if any, on the sentence
          if ( finalPhrases != null ) {
           // FSArray finalPhrasez = UIMAUtil.list2FsArray(pJCas, finalPhrases);
            //  ((Sentence)sentence).setPhrases(finalPhrasez);
          }
          
          
        } // end if this is a prossessable utterance

      } // end loop through each sentence

       List<Annotation> vPhrases = UIMAUtil.getAnnotations(pJCas, gov.va.vinci.model.Phrase.type);
       if ( vPhrases != null ) for ( Annotation vPhrase : vPhrases)vPhrase.removeFromIndexes(pJCas);

      // -------------------------------
      // attach all terms to each phrase -- some phrases are not getting their
      // terms attached
      
      List<Annotation> fPhrases = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.Phrase.type);
      if ( fPhrases != null ) {
        for (Annotation fPhrase : fPhrases)
          attachTermsToPhraseOld(pJCas, fPhrase);
      } // end if there are phrases 
      

  } // end if there are any sentences
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue in phraseAnnotator " + e.toString());
    }

  this.performanceMeter.stopCounter();

  }  //end Method process() --------------------
  
     
  private void attachTermsToPhraseOld(JCas pJCas, Annotation fPhrase) {
  
    String kind = ((gov.va.chir.model.Phrase)fPhrase).getPhraseKind();
    if (( kind != null ) && (kind.equals( "final")) ) {
    // FSArray les = ((gov.va.chir.model.Phrase) fPhrase).getLexicalElements();
      
      // List<Token> tokens = UIMAUtil.fSArray2List(pJCas, ((gov.va.chir.model.Phrase) fPhrase).getPhraseTokens());
      List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas,Token.typeIndexID, fPhrase.getBegin(), fPhrase.getEnd());
      List<LexicalElement> terms = new ArrayList<LexicalElement>();
       
      if ( tokens != null ) {
        for ( Annotation token : tokens ) {
          if ( token != null ) {
            LexicalElement term = (LexicalElement) ((Token)token).getParent();
            if ( term != null ) {
              
              if ( !terms.contains(term))
                terms.add ( term);
            }
          }
         // FSArray termz = UIMAUtil.list2FsArray(pJCas, terms);
      //    ((gov.va.chir.model.Phrase) fPhrase).setLexicalElements( termz) ;
        }   
      }
      
    } else   if (( kind != null ) && (kind.equals( "minimalPhrase")) ) {
    
    } else {
     // rogue phrase
      // System.err.println("Rogue phrase deleting " + ((gov.va.chir.model.Phrase)fPhrase).getId() + "|" +  fPhrase.getCoveredText());
      fPhrase.removeFromIndexes();
    }
      
  
} // end Method attachTermsToPhrase() ------

// -----------------------------------------
  /**
   * tokenPhraseTagsIntoPhrases creates phrases from the "chunk" labels the open-nlp chunker
   * created for the tokens of the phrase.
   * 
   * This works a little differently than in ctakes.  The tokens fed into the chunker
   * didn't include sentence ending punctuation because it's not considered part of the sentence word tokens.
   * Soooo, the chunker tags the last word of the sentence with an "O" which is usually
   * reserved for the period or last token of the sentence.  
   * We are going to consider those tokens marked with "O" to be valid phrase tokens
   * 
   * @param sentence 
   * @param pJCas 
   * @param tokenPhraseTags
   * @param tokens
   * @return List<gov.va.vinci.model.Phrase>  (the phrases are being deposited into this list)
   */
  // -----------------------------------------
  private List<gov.va.vinci.model.Phrase> tokenPhraseTagsIntoNonFinalPhrases(JCas                            pJCas, 
                                                                             Utterance                       sentence, 
                                                                             String[]                        tokenPhraseTags, 
                                                                             List<Annotation>                tokens ){
                                           
    ArrayList<gov.va.vinci.model.Phrase> nonFinalPhrases = new ArrayList<gov.va.vinci.model.Phrase>();
    Token aToken = null;
    Token priorToken = null;
    String finalPhraseTag = null;
    ArrayList<Token> chunkTokens = new ArrayList<Token>();
    gov.va.vinci.model.Phrase nonFinalPhrase = null;
    
    // -----------------------------------------------
    // Iterate through the tokens/token phrase tags
    // -----------------------------------------------
    for (int i = 0; i < tokenPhraseTags.length; i++) {
      
      aToken = (Token)tokens.get(i);
      aToken.setPhraseTag(tokenPhraseTags[i] );
      finalPhraseTag = getFinalPhaseTag( tokenPhraseTags, i, priorToken);
      
      if (tokenPhraseTags[i].startsWith("B-") ) { 
        if ( (middleOfATerm(aToken))) {
          chunkTokens.add(aToken);
        } else {
          // -----------------------------------------
          // There are cases where the chunker is breaking a phrase
          // in the middle of a term.  Detect this, and don't break
          // if this is in the middle of a term.
          // -----------------------------------------
          nonFinalPhrase = createNonFinalPhrase(pJCas, sentence, chunkTokens, finalPhraseTag);
          if ( nonFinalPhrase != null )
            nonFinalPhrases.add( nonFinalPhrase );
              
          chunkTokens = new ArrayList<Token>();
          chunkTokens.add( aToken);
        }
      } else if ( tokenPhraseTags[i].startsWith("O")) {
        // -----------------------------------------
        // There are cases where the chunker marks things like preps and stand alone verbs
        // as "O". Make a separate chunk for these and mark them w/ the pos tag, not the phrase tag
        // -----------------------------------------
       
        nonFinalPhrase = createNonFinalPhrase(pJCas, sentence, chunkTokens, finalPhraseTag);
        if ( nonFinalPhrase != null )
          nonFinalPhrases.add( nonFinalPhrase );
        
        chunkTokens = new ArrayList<Token>();
        if (  !aToken.getPunctuationOnly() )
          chunkTokens.add( aToken);
        
        if ( i >= tokenPhraseTags.length -2 )
          chunkTokens.add( aToken);
      } else if ( tokenPhraseTags[i].startsWith("I-")) {
        chunkTokens.add( aToken);
      } 
      
      // ------------------------------------------
      // criteria to create a chunk
      //   1. A B- was hit
      //   2. A O- was hit
      //   3. The end of the sentence was hit
      // ------------------------------------------
          priorToken = aToken;
    } // end loop through the tokens that have been marked with chunk tags
    // create a last phrase for the last tokens in the phrase
    
    finalPhraseTag = getFinalPhaseTag( tokenPhraseTags, tokenPhraseTags.length, priorToken);
    nonFinalPhrase = createNonFinalPhrase( pJCas, sentence, chunkTokens, finalPhraseTag);
    if ( nonFinalPhrase != null )
      nonFinalPhrases.add(  nonFinalPhrase );
    
    return (nonFinalPhrases) ;
    
  } // end Method tokenPhraseTagsIntoVinciPhrases() -

  // -----------------------------------------
  /**
   * createMinimalPhrases creates sub-phrases for each phrase that is devoid
   * of determiners and some modifiers.  This pairs down each phrase to those
   * tokens that are likely to be the tokens of entry terms in controlled 
   * medical vocabularies. Minimal Phrases should be attached to their parent phrases.
   * 
   * @param finalPhrases
   */
  // -----------------------------------------
  private void createMinimalPhrases(JCas pJCas, List<gov.va.chir.model.Phrase> finalPhrases) {
    
    
    for ( gov.va.chir.model.Phrase phrase: finalPhrases ) {
      MinimalPhrase minPhrase = createMinimalPhrase ( pJCas, phrase);
     
      
    } // end loop through final phrases
    
  } // end Method createSimplePhrases() ------

  // -----------------------------------------
  /**
   * createMinimalPhrase creates a sub-phrase for the  phrase. It is devoid
   * of determiners and some modifiers.  This pairs down the phrase to those
   * tokens that are likely to be the tokens of entry terms in controlled 
   * medical vocabularies.  For NP's this is jj's and NN's
   * For verb phrases this is rb's and v*'s
   *  
   * 
   * @param pJCas
   * @param pPhrase
   */
  // -----------------------------------------
  private MinimalPhrase createMinimalPhrase(JCas pJCas, gov.va.chir.model.Phrase pPhrase) {
    
     MinimalPhrase minimalPhrase = null;
   
     
    // ---------------------------------------
    // Extract the tokens of the phrase
    // ---------------------------------------
    // FSArray tokenz = pPhrase.getPhraseTokens();  
     List<Annotation> tokenz = null;
     if (pPhrase == null ) return null;
     tokenz = UIMAUtil.getAnnotationsBySpan(pJCas, WordToken.typeIndexID, pPhrase.getBegin(), pPhrase.getEnd());
    
       ArrayList<Token> minimalPhraseTokens = new ArrayList<Token>(tokenz.size());
    
    Token aToken = null;
    String pos = null;
    String word = null;
    String phraseType = pPhrase.getPhraseType();
    if ( tokenz != null )
    for ( int i = 0; i < tokenz.size(); i++ ) {
      aToken = (Token) tokenz.get(i);
      word = aToken.getCoveredText().toLowerCase();
      if ( aToken.getPartOfSpeech() == null )
        continue;
      pos = aToken.getPartOfSpeech().getPos();
      
      if ( phraseType.equals("NP")   || 
           phraseType.equals("ADJP") || 
           phraseType.equals("PRT")  || 
           phraseType.equals("ADVP") || 
        //   phraseType.equals("SBAR") ||
           phraseType.equals("UCP")  ) {
        
          if ( pos.startsWith("NN") || 
               pos.startsWith("JJ") || 
               pos.startsWith("IN") || 
               pos.equals("SYM")    || 
               pos.startsWith("RB") || 
               pos.startsWith("VB") || 
               pos.startsWith("FW") ) {
          minimalPhraseTokens.add( aToken);
         
        }
        // end dealing with NP tokens
      } else if ( phraseType.equals("VP") ) {
        if  (( pos.startsWith("V") || !pos.startsWith("RB")) && (!DTaggerUtilities.isAuxOrModal( word) ) )
          minimalPhraseTokens.add( aToken);
      } // end of Verb phrases
    } // end loop through tokens of the phrase
    
    if (minimalPhraseTokens.size() > 0) {

     // List<LexicalElement> minimalTerms = getPhraseTerms(minimalPhraseTokens);
    //  FSArray minimalTermz = UIMAUtil.list2FsArray(pJCas, minimalTerms);

    //  FSArray minimalPhraseTokenz = UIMAUtil.list2FsArray(pJCas, minimalPhraseTokens);
      minimalPhrase = new MinimalPhrase(pJCas);
      // VUIMAUtil.setProvenance( pJCas, minimalPhrase, this.getClass().getName() );
      minimalPhrase.setBegin(((Token) minimalPhraseTokens.get(0)).getBegin());
      minimalPhrase.setEnd(((Token) minimalPhraseTokens.get(minimalPhraseTokens.size() - 1)).getEnd());
      minimalPhrase.setDisplayString(U.normalize(minimalPhrase.getCoveredText()));
      minimalPhrase.setParent(pPhrase);
      minimalPhrase.setId("MinimalPhrase_" + this.minimalPhraseCounter++);
      // minimalPhrase.setPhraseTokens(minimalPhraseTokenz);
      // minimalPhrase.setUtteranceTokens(minimalPhraseTokenz);
      // minimalPhrase.setLexicalElements(minimalTermz);
      // minimalPhrase.setUtteranceLexicalElements(minimalTermz);
      minimalPhrase.setPhraseType(pPhrase.getPhraseType());
      minimalPhrase.setPhraseKind("minimalPhrase");
      // minimalPhrase.setProvenance(pPhrase.getProvenance());

      minimalPhrase.addToIndexes(pJCas);
    } else {
      // System.err.println("skpping phrase " + pPhrase.getCoveredText());
    }

   
    
    return ( minimalPhrase);
  } // end Method createMinimalPhrase() ------

  // -----------------------------------------
  /**
   * combinePhrases combines runs of NP's into one NP, and combines
   * "of" and "with" prepositions with the surrounding phrases.
   * 
   * 
   * @param nonFinalPhrases
   */
  // -----------------------------------------
  private List<gov.va.chir.model.Phrase> combineOfPhrases(JCas pJCas, List<gov.va.vinci.model.Phrase> nonFinalPhrases, HashSet<String> pKeys) {
    
    ArrayList<gov.va.chir.model.Phrase> finalPhrases = new ArrayList<gov.va.chir.model.Phrase>();
    String[]                                tag = new String[ nonFinalPhrases.size()];
    String                                 word = null;
    
    
    // -----------------------------------------------------------------------------
    // Create an array of phrase tags. (This makes looking at phrase tag patterns easier)
    // -----------------------------------------------------------------------------
    for ( int i = 0; i < nonFinalPhrases.size(); i++ ) {
      gov.va.vinci.model.Phrase pr =  nonFinalPhrases.get(i);
      if ( pr != null) {
        tag[i] = pr.getPhraseType();
        if ( tag[i] == null ) tag[i] = "null";
      }
      else 
        tag[i] = "null";
    } // end loop to fill tag
    if ( nonFinalPhrases.size() < 3 ) {
      for ( int k = 0; k < nonFinalPhrases.size(); k++)
        finalPhrases.add( createFinalPhrase( pJCas, nonFinalPhrases, k, pKeys));
     
    } else {
      word = nonFinalPhrases.get(1).getCoveredText();
      if (word == null)
        word = "";
    
    // -----------------------------------------------------------------------------
    // Look at the first three phrases to see if they do not need to be combined
    // -----------------------------------------------------------------------------
    if (!(( tag[0].equals("NP"))  && ( tag[1].equals("PP") ) && (word.equalsIgnoreCase("of")) && ( tag[2].equals("NP")))) {
      for ( int i = 0; (i < 2 && i < nonFinalPhrases.size()); i++ ) {
        finalPhrases.add( createFinalPhrase( pJCas, nonFinalPhrases, i, pKeys));
      } 
    } // end processing the first two phrases;
    
    // -----------------------------------------------------------------------------
    // Loop from the 3'rd phrase on to combine prep phrases and create final phrases
    // -----------------------------------------------------------------------------
    for ( int i = 2; i < nonFinalPhrases.size(); i++ ) {
      word = nonFinalPhrases.get(i-1).getCoveredText();
      if ( (this.combinePhrases) && ( tag[i-2].equals("NP"))  && ( tag[i-1].equals("PP") ) && (word.equalsIgnoreCase("of")) && ( tag[i].equals("NP"))) {
        finalPhrases.add( combineFinalPhrases( pJCas, nonFinalPhrases, i));
      } else {
        finalPhrases.add( createFinalPhrase( pJCas, nonFinalPhrases, i, pKeys)) ;
      }    
    } // end loop through nonFinal phrases
    } // end if there are more than 2 phrases to combine
    
    return ( finalPhrases);
  } // end Method combinePhrases() -------------

    // -----------------------------------------
  /**
   * combineFinalPhrases combines the phrases [i-2][i-1][i] into one.
   * 
   * @param pJCas
   * @param nonFinalPhrases
   * @param i
   * @return
   */
  // -----------------------------------------
  private gov.va.chir.model.Phrase combineFinalPhrases(JCas pJCas, List<gov.va.vinci.model.Phrase>nonFinalPhrases, int pCurrentPosition) {
    
    gov.va.chir.model.Phrase finalPhrase = new gov.va.chir.model.Phrase( pJCas );
    finalPhrase.setPhraseKind("final");
    
    gov.va.vinci.model.Phrase phrases[] = new gov.va.vinci.model.Phrase[3];
    
    phrases[0] = nonFinalPhrases.get(pCurrentPosition-2);
    phrases[1] = nonFinalPhrases.get(pCurrentPosition-1);
    phrases[2] = nonFinalPhrases.get(pCurrentPosition);
    
    ArrayList<Token> allTokens = new ArrayList<Token>();
    ArrayList<LexicalElement> allTerms = new ArrayList<LexicalElement>();
    for ( int i = 0; i < 3; i ++) {
      
      List<Annotation> tokenz = UIMAUtil.getAnnotationsBySpan(pJCas, Token.typeIndexID, phrases[i].getBegin(), phrases[i].getEnd());
      List<Annotation>  termz = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, phrases[i].getBegin(),  phrases[i].getEnd() ); 
      
      for ( int j = 0; j < tokenz.size(); j++) allTokens.add( (Token) tokenz.get(j)); 
     
      if ( termz != null && !termz.isEmpty())
        for ( int j = 0; j < termz.size();  j++) 
          if ( termz.get(j) != null)
            allTerms.add( (LexicalElement) termz.get(j));
      
    }
  // FSArray allTokenz = UIMAUtil.list2FsArray(pJCas, allTokens);
  // FSArray allTermz  = UIMAUtil.list2FsArray(pJCas, allTerms);
    
    // VUIMAUtil.setProvenance( pJCas, finalPhrase, this.getClass().getName() );
    finalPhrase.setBegin( phrases[0].getBegin());
    finalPhrase.setEnd  (phrases[2].getEnd());
    finalPhrase.setDisplayString( U.normalize( finalPhrase.getCoveredText()) );
    finalPhrase.setId( "Phrase_" + this.finalPhraseCounter++);
    //finalPhrase.setLexicalElements(allTermz );
    
   // finalPhrase.setParent(phrases[0].getParent());  // <------- holy cow
    //finalPhrase.setPhraseTokens(allTokenz);
    finalPhrase.setPhraseType("NP"); 
   //  finalPhrase.setProvenance(phrases[0].getProvenance());
    //finalPhrase.setUtteranceLexicalElements( allTermz);
    //finalPhrase.setUtteranceTokens( allTokenz);
    
    finalPhrase.addToIndexes(pJCas);
    
    
    return finalPhrase;
  } // end Method combineFinalPhrases() ------

  // -----------------------------------------
  /**
   * createFinalPhrase creates a final phrase, given a non-final phrase
   * 
   * @param pJCas
   * @param nonFinalPhrases
   * @param i
   * @return
   */
  // -----------------------------------------
  private gov.va.chir.model.Phrase createFinalPhrase(JCas pJCas,  List<gov.va.vinci.model.Phrase> nonFinalPhrases, int i, HashSet<String> pKeys) {
    
    gov.va.chir.model.Phrase finalPhrase = null;
   
 
   
    gov.va.vinci.model.Phrase nonFinalPhrase = nonFinalPhrases.get(i);
    
    String key = U.pad(nonFinalPhrase.getBegin()) + "|" + U.pad(nonFinalPhrase.getEnd()) ;
    if ( pKeys.contains( key))
      return null;
    pKeys.add(key);
    finalPhrase = new gov.va.chir.model.Phrase( pJCas);
  //  FSArray termz = nonFinalPhrase.getLexicalElements();
 //   List<Annotation> terms = UIMAUtil.fSArray2List(pJCas, termz);
    
    finalPhrase.setPhraseKind("final");
    // VUIMAUtil.setProvenance( pJCas, finalPhrase, this.getClass().getName() );
    finalPhrase.setBegin( nonFinalPhrase.getBegin());
    finalPhrase.setEnd  ( nonFinalPhrase.getEnd());
    
    finalPhrase.setDisplayString( nonFinalPhrase.getDisplayString());
    finalPhrase.setId( "Phrase_" +  this.finalPhraseCounter++);
    
  //  finalPhrase.setLexicalElements( termz );
    finalPhrase.setMarked(nonFinalPhrase.getMarked());
    
    finalPhrase.setOtherFeatures(nonFinalPhrase.getOtherFeatures());
    
    // finalPhrase.setParent(nonFinalPhrase.getParent());  // <-------------------------------- holy cow!
    
   // finalPhrase.setPhraseTokens(nonFinalPhrase.getPhraseTokens());
    finalPhrase.setPhraseType(nonFinalPhrase.getPhraseType()); 
   // finalPhrase.setProvenance(nonFinalPhrase.getProvenance());
  //  finalPhrase.setUtteranceLexicalElements(nonFinalPhrase.getUtteranceLexicalElements());
  //  finalPhrase.setUtteranceTokens(nonFinalPhrase.getUtteranceTokens());
   
     finalPhrase.addToIndexes(pJCas);
 
    return finalPhrase;
  } // end Method createFinalPhrase() -----------

    // -----------------------------------------
    /**
     * getFinalPhraseTag creates a string that is the phrase tag.
     * The tokenPhraseTags that the chunker deposits look like B-NP, I-NP ...
     * This method strips the B- and I- from the chunker tag.
     * 
     * It might be weird to pass in the prior token's label, but
     * this makes sense: At the current state of processing, the
     * phrase label to be put on the phrase to be created is from
     * the prior tokens.  For example, when a B-NP is hit, this is
     * the trigger to create a phrase from the prior tokens seen, and
     * the prior tokens's chunk label.
     * 
     * There are exceptions - if we are at the last token, and the
     * last token isn't an "O" - take the tag from that last token.
     * 
     * @param tokenPhraseTags
     * @param pCurrentPosition
     * @param priorToken
     * @return
     */
    // -----------------------------------------
    private String getFinalPhaseTag(String[] tokenPhraseTags, int pCurrentPosition, Token priorToken) {
      
      String finalPhraseTag = "null";
      if (( pCurrentPosition == tokenPhraseTags.length ) && ( tokenPhraseTags.length > 1 )){
        
        if (( tokenPhraseTags[tokenPhraseTags.length -1].startsWith("O" )) &&
            ( tokenPhraseTags[tokenPhraseTags.length -2].length() >= 2)) {
         finalPhraseTag = tokenPhraseTags[tokenPhraseTags.length -2].substring(2);
        } else if ( tokenPhraseTags[tokenPhraseTags.length -1].length() > 1 )
          finalPhraseTag = tokenPhraseTags[tokenPhraseTags.length -1].substring(2);

      } else if (pCurrentPosition > 0 ) {
        if ( tokenPhraseTags[pCurrentPosition-1].length() > 2) {
          finalPhraseTag    = tokenPhraseTags[pCurrentPosition -1].substring(2);
        } else
          finalPhraseTag =  priorToken.getPartOfSpeech().getPos();
      }
      return finalPhraseTag;
    } // end Method getFinalPhraseTag() -----

    // -----------------------------------------
  /**
   * processThisUtterance determines if the utterance 
   * is one that should be processed.  SlotFillter, contentHeadings, 
   * phrases that have come from prior processing, snippets are all kinds
   * of utterances that have been pre-determined to be phrases and need
   * no further chunking.
   * 
   * @param sentence
   * @return boolean
   */
  // -----------------------------------------
  private boolean processThisUtterance(Annotation sentence) {
   boolean returnValue = false;
    
    if (( ((Utterance)sentence).getTypeIndexID() != SlotValue.type )  && 
    ( (((Utterance)sentence).getUtteranceTokens() != null ) &&
        ( ((Utterance)sentence).getUtteranceTokens().size() > 0 )) && 
        ( ((Utterance)sentence).getTypeIndexID() != gov.va.vinci.model.Phrase.type )     && 
        ( ((Utterance)sentence).getTypeIndexID() != gov.va.chir.model.Phrase.type ) && 
        ( ((Utterance)sentence).getTypeIndexID() != Snippet.type )  && 
        ( ((Utterance)sentence).getTypeIndexID() != ContentHeading.type ) )
        returnValue = true;
          
    return returnValue;
   
  } // end Method processThisUtterance() ----


  // -----------------------------------------
  /**
   * fixTag makes a pseudo Phrase tag for pos tags on tokens
   * that the chunker missed.
   *   pp -> PP
   *   v* -> VP
   *   conj ->CONJP
   *   ,    -> 0
   *   (|)  ->
   *   SYM  -> NP
   *   
   * @param pos
   * @return String
   */
  // -----------------------------------------
  private String fixTag(String pos) {
    String chunkerTag = "O";
    
    if      ( pos.contains("PP") ) chunkerTag = "PP";
    else if ( pos.contains( "CC")) chunkerTag = "CONJP";
    else if ( pos.startsWith("V")) chunkerTag = "VP";
    else if ( pos.contains("IN"))  chunkerTag = "PP";
    else if ( pos.contains(","))   chunkerTag = "O";
    else if ( pos.contains(")" ))  chunkerTag = "O";
    else if ( pos.contains("("))   chunkerTag = "O";
    else if ( pos.contains("SYM")) chunkerTag = "NP";
    
    else System.err.println(" not seen this yet, tell guy: " + pos);
    
    return chunkerTag;
  }  // end Method fixTag
  
  // -----------------------------------------
  /**
   * middleOfATerm - detect if this token is not the
   * first word of a multi-word term.
   * 
   * @param aToken
   * @return
   */
  // -----------------------------------------
  private boolean middleOfATerm(Token pToken) {
    boolean returnValue = false;
    
    LexicalElement term = (LexicalElement) pToken.getParent();
    if ( term != null ) {
      FSArray tokenz = term.getTokens();
      for ( int i = 0; i < tokenz.size(); i++ ) {
        Token aToken = (Token)tokenz.get(i);
        if ( pToken.getId().equals(aToken.getId())) {
          if ( i > 0)
            returnValue = true;
          break;
        }
      }
    }
    return returnValue;
  } // end Method middleOfATerm() ------------
  /**
   * createVinciPhrase creates the phrase, attaches the tokens and terms to it.
   * 
   * @param pJCas
   * @param pTokens
   * @param pTag
   * @return
   */
  // -----------------------------------------
  private gov.va.vinci.model.Phrase createNonFinalPhrase(JCas pJCas, Utterance pSentence, List<Token> pTokens, String pTag ) {
    
    gov.va.vinci.model.Phrase phrase = null; 
    if (( pTokens != null ) && ( pTokens.size() > 0)) {
    
      phrase = new gov.va.vinci.model.Phrase(pJCas);
    
      int begin = pTokens.get(0).getBegin();
      int   end = pTokens.get(pTokens.size() -1).getEnd();
    
      phrase.setBegin(begin  );
      phrase.setEnd( end  );
      phrase.setPhraseType(pTag);
      phrase.setDisplayString( U.normalize( phrase.getCoveredText()));
      phrase.setId( "Vinci_Phrase_" + this.nonFinalPhraseCounter++);
      phrase.setParent( pSentence );
      // phrase.setProvenance(this.provenance);  <----- this is supposed to be a StringArray
      
     // FSArray tokenz = UIMAUtil.list2FsArray(pJCas, pTokens);
     //phrase.setPhraseTokens(tokenz);
      
      // List<LexicalElement> phraseTerms = getPhraseTerms( pTokens);
     //  FSArray termz = UIMAUtil.list2FsArray( pJCas, phraseTerms);
      // phrase.setLexicalElements( termz);
     // phrase.setUtteranceLexicalElements(termz); <----- duplicated

      phrase.addToIndexes(pJCas);
    
    }
    return phrase;
  } // end Method createVinciPhrase() ----

  // -----------------------------------------
  /**
   * getPhraseTerms retrieves a list of lexicalElements that span
   * the phrase by getting the terms associated with the tokens that
   * are part of the phrase.
   * 
   * @param pTokens (of the phrase)
   * @return List<LexicalElement>
   */
  // -----------------------------------------
  private List<LexicalElement> getPhraseTerms(List<Token> pTokens) {
    
    ArrayList<LexicalElement> terms = new ArrayList<LexicalElement>();
    LexicalElement priorTerm = null;
    for ( Token token: pTokens ) {
      LexicalElement term = (LexicalElement) token.getParent();
      
      if (( priorTerm != null ) && ( !term.getId().equals( priorTerm.getId() ) ) )
        terms.add( term);
     
      priorTerm = term;
      
    } //end loop through tokens of the phrase;
    
    return terms;
  } // end Method getPhraseTerms() ----------

  // end Method process() ----------------

  // -----------------------------------------
  /**
   * getWordsAndTags returns a String[] with words in it
   * and a String[] with parts of speech in it.
   * 
   * The pos's are in penn treebank format.
   * 
   * @param pJCas
   * @param sentence
   * @param tokens
   * @param tokenStrings
   * @param posStrings
   * @return List<Annotation> of token
   */
  // -----------------------------------------
  @SuppressWarnings("unchecked")
  private List<Annotation> getWordsAndTags(JCas pJCas, Utterance sentence, List<String> tokenStrings, List<String> posStrings) {
    
    ArrayList<Annotation> returnVal = null;
    // FSArray tokenz = ((Utterance)sentence).getUtteranceTokens();
    List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas, Token.typeIndexID, sentence.getBegin(), sentence.getEnd());// UIMAUtil.fSArray2List(pJCas, tokenz);
    if ( tokens == null )
      return( returnVal);
    returnVal = new ArrayList<Annotation>( tokens.size());
    
   
    if  (( tokens != null)  && ( tokens.size() > 0 )) {
      for ( Annotation token: tokens) {
        if ( token != null ) {
          if ( ((Token)token).getPartOfSpeech() != null && (token.getCoveredText().trim().length() > 0)) {
            tokenStrings.add(token.getCoveredText());  // should this be normalized?
            posStrings.add( ((Token)token).getPartOfSpeech().getPos() );
            returnVal.add( token);
          }
        } else {
         //System.err.println( "empty token for sentence |" + sentence.getCoveredText() + "|");
        }
      } // end loop through tokens
    } else {
      //System.err.println("Empty tokens for sentence |" + sentence.getDisplayString() + "|");
    }
    
    return ( returnVal);
  } // end Method getTokenAndPos() ---------
  
//----------------------------------
  /**
   * initialize loads in the resources needed for phrase chunking. 
   * 
   * @param aContext
   * @throws ResourceInitializationException
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
    String[] args = null;
     try {
       args                 = (String[]) aContext.getConfigParameterValue("args");  
       
       initialize( args);
       
      
      
     } catch (Exception e ) {
       
     }
     
   
    
  } // End Method initialize() ------
  
  
  //----------------------------------
  /**
   * initialize loads in the resources needed for phrase chunking. 
   * 
   * @throws ResourceInitializationException
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
    
    try {
      
      this.combinePhrases = Boolean.parseBoolean(U.getOption(pArgs,  "--combinePhrases=", "true"));
 
      this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getName()  );
     
   
      String openNLPModelFileName =  "resources/vinciNLPFramework/chunker_opennlp/EnglishChunk.bin.gz";
      File   aFile = new File ( openNLPModelFileName);
      this.openNLPModelFile = getFileFromResource(openNLPModelFileName ) ;
      InputStream t = this.getClass().getClassLoader().getResourceAsStream( openNLPModelFileName);
     
     
      this.chunker = new TreebankChunker(aFile, t, true);
     
    } catch( Exception e) {
      String msg = "Could not create the openNLP POS tagger." + e.toString() + "\n" + U.getStackTrace(e);
      System.err.println(msg);
      throw new ResourceInitializationException(e);
    }
      
  } // end Method initialize() -------
  



  // =======================================================
  /**
   * destroy 
   * 
   */
  // =======================================================
  public void destroy() {
    if ( this.openNLPModelFile != null && this.openNLPModelFile.canRead() )
      this.openNLPModelFile.deleteOnExit();

    this.performanceMeter.writeProfile( this.getClass().getSimpleName());

  }// End Method destroy1() ======================
  


  // =======================================================
  /**
   * getFileFromResource
   * 
   * @param openNLPModelFileName
   * @return
   * @throws Exception 
   */
  // =======================================================
  private File getFileFromResource(String openNLPModelFileName) throws Exception {
    InputStream t1 = this.getClass().getClassLoader().getResourceAsStream(openNLPModelFileName);
    
    File f = null;
    try {
      f = File.createTempFile("tmpPhraseModel", ".mod");
    } catch (IOException e) {
      e.printStackTrace();
      String msg = "Issue with creating a temp file " + e.getMessage() ;
      System.err.println( msg );
      t1.close();
      throw new Exception (msg);
    }
    InputStream inputStream= t1;
    OutputStream out=new FileOutputStream(f);
    byte buf[]=new byte[1024];
    int len;
    while((len=inputStream.read(buf))>0)
      out.write(buf,0,len);
    out.close();
    inputStream.close();
    
    return f;
    // End Method getFileFromResource() ======================
  }




  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  private Chunker chunker = null;
  public static final String OPENNLP_CHUNKER_MODEL_FILE_PARAM = "ChunkerModel";
  private int nonFinalPhraseCounter = 0;
  private int finalPhraseCounter = 0;
  private File openNLPModelFile;
  private int minimalPhraseCounter = 0;
  private ProfilePerformanceMeter              performanceMeter = null;
  private String                       outputDir = null;
  private boolean combinePhrases = true;
 
  
  
} // end Class Phrase() -------
