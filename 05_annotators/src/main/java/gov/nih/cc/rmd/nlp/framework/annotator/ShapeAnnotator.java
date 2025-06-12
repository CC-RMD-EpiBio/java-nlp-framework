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
 * ShapeTokenizer is an annotator that creates (shape) terms from 
 * the strings and tokens of sentences.  A shape is a term that is recognized
 * by a means other than dictionary lookup - usually via regular expression
 * lookup.  Examples of shapes includes dates, times, telephone numbers,
 * identifiers.  

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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.model.Identifier;
import gov.nih.cc.rmd.framework.model.Location;
import gov.nih.cc.rmd.framework.model.PhoneNumber;
import gov.nih.cc.rmd.framework.model.Shape;
import gov.nih.cc.rmd.framework.model.URL;
import gov.nih.cc.rmd.framework.model.UnitOfMeasure;

import gov.va.chir.model.Token;
import gov.va.chir.model.Utterance;
import gov.nih.cc.rmd.nlp.framework.utils.DatePattern;
import gov.nih.cc.rmd.nlp.framework.utils.PatternMatch;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.UnitOfMeasurePattern;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class ShapeAnnotator extends JCasAnnotator_ImplBase {
  
  @Override
  // -----------------------------------------
  /**
   * process iterates through the sentences of the document, looking
   * for shapes. Each shape found will be turned into a term annotation.
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
    
    AnnotationIndex<Annotation> sentenceIndex = pJCas
        .getAnnotationIndex(Utterance.type);

    if (sentenceIndex != null) {

      // ------------------------------------------------------
      // Walk through the utterances, looking for those that
      // are contentHeaders, and not slotValue's.
      // ------------------------------------------------------
      for (Iterator<Annotation> i = sentenceIndex.iterator(); i.hasNext();) {
        Utterance aSentence = (Utterance) i.next();

        try {
          shapeTokenizeSentence(pJCas, aSentence);
        } catch (Exception e) {
          e.printStackTrace();
          throw new AnalysisEngineProcessException(e.toString(), null);
          // throw a uima exception here.
        }

      } // end loop through the sentences of the document

    } // end if there are any sentences
   
    
  } // end Method process() --------------------------
  
// -----------------------------------------
  /**
   * shapeTokenizeSentence finds date patterns from the string and
   * tokens of a sentence
   * 
   * @param pJCas
   * @param pSentence
 * @throws Exception 
   */
  // -----------------------------------------
  private void shapeTokenizeSentence(JCas pJCas, Utterance pSentence) throws Exception {
    
    String sentenceString = pSentence.getCoveredText();
    
    if (( sentenceString != null ) && ( sentenceString.trim().length() > 0 )) {
    int sentenceBeginOffset = pSentence.getBegin();
    ArrayList<Annotation>terms = new ArrayList<Annotation>();
    
    // get the tokens of the sentence
    @SuppressWarnings("unchecked")
	List<Annotation> tokens = UIMAUtil.fSArray2List(pJCas, pSentence.getUtteranceTokens());
    
    if (( tokens != null ) && ( tokens.size() > 0 )) {
    
      // Look for date patterns in this sentence
      Collection<PatternMatch> dateMatches = null;
      Collection<PatternMatch> unitOfMeasureMatches = null;
      try {
        dateMatches =  DatePattern.getMatches(sentenceString);
        unitOfMeasureMatches = UnitOfMeasurePattern.getMatches(sentenceString);
        
      } catch ( Exception e) {
    	  e.printStackTrace();
    	  throw new Exception (e);
      }
      // look for the matching tokens
      
      for ( PatternMatch match : dateMatches) {
        List<Token>matchingTokens = findTokens( sentenceBeginOffset, tokens, match);
        if ( matchingTokens != null) {
           terms.add( createTerm(pJCas, matchingTokens, "nn", "gov.nih.cc.rmd.framework", "Date" ));
        } // end if matching tokens were found.
      } // end loop through each match
      
      for ( PatternMatch match : unitOfMeasureMatches) {
        List<Token>matchingTokens = findTokens( sentenceBeginOffset, tokens, match);
        if ( matchingTokens != null) {
           terms.add( createTerm(pJCas, matchingTokens, "nn", "gov.nih.cc.rmd.framework", "UnitOfMeasure" ));
        } // end if matching tokens were found.
      } // end loop through each match
      
    } // end of if there were tokens for the utterance 
    
    // Assign the terms to the sentence
    if ( terms != null ) {
      FSArray termArray = UIMAUtil.list2FsArray(pJCas, terms);
      pSentence.setUtteranceLexicalElements(termArray);
    }
    } // end if there is any text within the sentence to work with
    
  } // end Method shapeTokenizeSentence

  
  // -----------------------------------------
/**
 * findTokens retrieves those tokens that cover the span of the 
 * match.
 * 
 * @param pSentenceBeginOffset
 * @param tokens
 * @param pMatch
 * @return
 */
// -----------------------------------------
private List<Token> findTokens(int pSentenceBeginOffset, List<Annotation> tokens, PatternMatch pMatch) {
  ArrayList<Token> matchingTokens = new ArrayList<Token>();
  
  for ( Annotation aToken : tokens) {
	if ( aToken != null ) {  
    int begin = aToken.getBegin() - pSentenceBeginOffset;
    int end   = aToken.getEnd()   - pSentenceBeginOffset;
    if (begin >= pMatch.getStart() && end <= pMatch.getEnd() ){
      matchingTokens.add( (Token) aToken);
    } else if ( aToken.getBegin() > pMatch.getStart() )
      break;    
	}
  } // end loop through tokens of the sentence.
  
  if ( matchingTokens.size() == 0) matchingTokens = null;
  
  return matchingTokens;
} // end Method findTokens() -----------------

  // -----------------------------------------
  /**
   * createTerm creates a Term.
   * 
   * @param pJCas
   * @param pTokens
   * @param pPOS (in the Penntreebank format.)
   * @param pProvenance
   * @param pTermType
   * 
   * @return LexicalElement
   */
  // -----------------------------------------
  private Shape createTerm(JCas pJCas, 
                                    List<Token> pTokens, 
                                    String pPOS,
                                    String pProvenance,
                                    String pTermType) {
    
    Shape aTerm = null;
    if ( pTermType == null )
      aTerm = new Shape( pJCas);

    else if ( pTermType.equals("Time"))
      aTerm = new Shape( pJCas);
    else if ( pTermType.equals("Email"))
      aTerm = new gov.nih.cc.rmd.framework.model.Email( pJCas);
    else if ( pTermType.equals("Identifier"))
      aTerm = new Identifier( pJCas);
    else if ( pTermType.equals("Location"))
      aTerm = new Location( pJCas);
    /*
    else if ( pTermType.equals("Organization"))
      aTerm = new Organization( pJCas);
    else if ( pTermType.equals("PersonalName"))
      aTerm = new PersonalName( pJCas);
      */
    else if ( pTermType.equals("PhoneNumber"))
      aTerm = new PhoneNumber( pJCas);
    else if ( pTermType.equals("UnitOfMeasure"))
      aTerm = new UnitOfMeasure( pJCas);
    else if ( pTermType.equals("URL"))
      aTerm = new URL( pJCas);
    else
     aTerm = new Shape( pJCas);
    
    int begin = pTokens.get(0).getBegin();
    int  zEnd = pTokens.get(pTokens.size() -1).getEnd();
    
    aTerm.setBegin(begin);
    aTerm.setEnd( zEnd );
   
    aTerm.setId( "ShapeAnnotator_"+ this.annotationCounter++);
    
    
    aTerm.addToIndexes(); 
    
   
    
    return aTerm;
  } // end Method createTerm() ---------------
  

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
   * initialize loads in the resources. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
     
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  

      } catch (Exception e ) {
        System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
        throw new ResourceInitializationException();
      }
      

      this.performanceMeter = new ProfilePerformanceMeter( args, this.getClass().getSimpleName() );
      
    
    initialize( args);
      
  } // end Method initialize() -------
  

  //----------------------------------
  /**
   * initialize 
   *
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String pArgs[]) throws ResourceInitializationException {

    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    
  } // end Method initialize() -------
   
  

  
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int lineCtr = 0;
  private ProfilePerformanceMeter performanceMeter = null;
  private long                   annotationCounter = 0;

  
  
} // end Class ShapeAnnotator --------------------------
