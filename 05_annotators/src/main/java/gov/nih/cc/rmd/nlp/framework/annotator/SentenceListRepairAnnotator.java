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
 * SentenceListRepair makes sure that sentences that go beyond list elements get truncated 
 * at the end of the list element.
 * 
 * Also, that sentences that contain section names should be split and re-made to not include 
 * the section name.
 * @author Guy Divita
 * @created Dec 31, 2019
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.model.SectionName;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Delimiter;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.ListDelimiter;
import gov.va.chir.model.ListElement;
import gov.va.chir.model.QuotedUtterance;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.Utterance;
import gov.va.chir.model.WordToken;


public class SentenceListRepairAnnotator extends JCasAnnotator_ImplBase {

  @Override
  // -----------------------------------------
  /**
   * 
   * 
   * process makes sure that sentences that go beyond list elements get truncated 
   * at the end of the list element.
   * 
   * Also, for lists that have just one element - these are not
   * lists - so make them back to a sentence.
   * 
   * This method also merges sentences within quoted expressions when
   * the quoted expression is not the same as the sentences. 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
      this.performanceMeter.startCounter();

      removeOneElementLists( pJCas);
      
      removeListsTheSameDelimiter( pJCas);
      
      sentenceListRepairProcess( pJCas);
      
      repairQuotedUtterances(pJCas);
      
      removeSectionNamesFromSentences( pJCas);

      repairSentencesThatEndWithANumber( pJCas );

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
      // throw new AnalysisEngineProcessException();
    }

    this.performanceMeter.stopCounter();
   
  } // End Method process() =

 
 // =================================================
  /**
   * repairSentencesThatEndWithANumber.  Sentences that
   * end with a number are missing that trailing number.
   * 
   * Put these back together again
   * @param pJCas
  */
  // =================================================
 private void repairSentencesThatEndWithANumber(JCas pJCas) {
    
   
   List<Annotation> numbers = UIMAUtil.getAnnotations( pJCas, gov.nih.cc.rmd.framework.model.Number.typeIndexID );
   
   if ( numbers != null && !numbers.isEmpty())
     for ( Annotation number : numbers ) {
       
       String buff = number.getCoveredText();
      
       List<Annotation> sentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Sentence.typeIndexID, number.getBegin(), number.getEnd(), true );
    
       if ( sentences == null || sentences.isEmpty() ) {
         sentences =  UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Sentence.typeIndexID, number.getBegin() - 5, number.getBegin() );
       
         if ( sentences != null && !sentences.isEmpty() ) {
           Annotation aSentence = sentences.get(0);
           List<Annotation> numberTokens = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, WordToken.typeIndexID, number.getBegin(), number.getEnd() );
         
           if ( numberTokens != null && !numberTokens.isEmpty()) {
             Annotation overlappingWordToken = numberTokens.get(0);
             if ( ((WordToken)overlappingWordToken).getSentenceBreak() )
               aSentence.setEnd(  number.getEnd());
           }
         }
       }
     }
    
  } // end Method repairSentencesThatEndWithANumber() ----


// =================================================
  /**
   * removeSectionNamesFromSentences 
   * turns sentences that have embedded section names
   * in them into sentence|section Name|sentence
   * 
   * @param pJCas
  */
  // =================================================
  private void removeSectionNamesFromSentences(JCas pJCas) {
  
    List<Annotation> sectionNames = UIMAUtil.getAnnotations(pJCas, SectionName.typeIndexID );
    
    if ( sectionNames != null && !sectionNames.isEmpty()) 
      for ( Annotation sectionName : sectionNames )
        removeSectionNamesFromSentences( pJCas, sectionName);
    
    
  } // end Method removeSectionNamesFromSentences() --


// =================================================
/**
 * removeSectionNamesFromSentences 
 * turns sentences that have embedded section names
 * in them into sentence|section Name|sentence
 * 
 * @param pJCas
 * @param pSectionName
*/
// =================================================
private void removeSectionNamesFromSentences(JCas pJCas, Annotation pSectionName) {
  
  List<Annotation> sentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Sentence.typeIndexID, pSectionName.getBegin(), pSectionName.getEnd() );

  if ( sentences != null && !sentences.isEmpty()) {
    Annotation aSentence = sentences.get(0);
   
    if ( aSentence.getBegin() >= pSectionName.getBegin() && aSentence.getEnd() <= pSectionName.getEnd() )
      return;
      
    if ( aSentence.getBegin() < pSectionName.getBegin() )
      createSentence( pJCas, (Sentence) aSentence, aSentence.getBegin(), pSectionName.getBegin()-1); 
    
    
    createSentence( pJCas, (Sentence) aSentence, pSectionName.getBegin(), pSectionName.getEnd());
    
    
    if ( aSentence.getEnd() > pSectionName.getEnd() )
      createSentence( pJCas, (Sentence) aSentence, pSectionName.getEnd() +1, aSentence.getEnd());
    
    
    
    aSentence.removeFromIndexes();
    
    
    
  }
  
} // end Method removeSectionNamesFromSentence() ----


// =================================================
  /**
   * repairQuotedUtterances merges sentences within quoted expressions when
   * the quoted expression is not the same as the sentences. 
   * 
   * @param pJCas
  */
  // =================================================
  private void repairQuotedUtterances(JCas pJCas) {
   
    List<Annotation> quotedUtterances = UIMAUtil.getAnnotations(pJCas, QuotedUtterance.typeIndexID, true);
    
    if ( quotedUtterances != null && !quotedUtterances.isEmpty()) {
      for ( Annotation quotedUtterance : quotedUtterances )
        repairQuotedUtterance( pJCas, quotedUtterance);
    }
    
  } // end Method repairQuotedUtterances() ---------


// =================================================
/**
 * repairQuotedUtterance 
 * 
 * @param pJCas
 * @param pQuotedUtterance
*/
// =================================================
private final void repairQuotedUtterance(JCas pJCas, Annotation pQuotedUtterance) {
  
  List<Annotation> sentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Sentence.typeIndexID,  pQuotedUtterance.getBegin(), pQuotedUtterance.getEnd());

  if ( sentences != null && sentences.size() > 1 ) {
      mergeIntoOneSentence( pJCas, sentences ) ;
      for ( Annotation sentence : sentences )
        sentence.removeFromIndexes();
  }
  
} // end Method repairQuotedUtterance() ------------


// =================================================
  /**
   * sentenceListRepairProcess makes sure that sentences that go beyond 
   * list elements get truncated at the end of the list element.
   * 
   * 
   * @param pJCas
  */
  // =================================================
  private final void sentenceListRepairProcess(JCas pJCas) {
    List<Annotation> listElements = UIMAUtil.getAnnotations(pJCas, ListElement.typeIndexID, false);

    if (listElements != null && !listElements.isEmpty()) {

      for (int i = 0; i < listElements.size() ; i++) {
        
        processListElement( pJCas, listElements.get(i));
    
      } // end loop thru the list elements
   } // end if there are headings
    
  }


  // =================================================
  /**
   * removeOneElementLists 
   * 
   * @param pJCas
   */
  // ================================================
  private final void removeOneElementLists(JCas pJCas) {

	  
      List<Annotation> listz = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.List.typeIndexID );
  
      if (listz != null && !listz.isEmpty()) 
    	  for ( Annotation aList : listz  ) 
    	     if ( aList != null &&  ((gov.va.chir.model.List) aList).getListElements() != null  && ((gov.va.chir.model.List) aList).getListElements().size() == 1 )
    				removeListAndElement( pJCas, (gov.va.chir.model.List) aList);
    		
    	  
	
} // end Method removeOneElementLists() -------------


  // =================================================
  /**
   * removeListsTheSameDelimiter 
   * 
   * @param pJCas
   */
  // ================================================
  private final void removeListsTheSameDelimiter(JCas pJCas) {

    
      List<Annotation> listz = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.List.typeIndexID );
  
      if (listz != null && !listz.isEmpty()) 
        for ( Annotation aList : listz  ) 
           if ( aList != null &&  ((gov.va.chir.model.List) aList).getListElements() != null )
               removeListWithTheSameDelimiter(  pJCas, aList );
           
        
  
} // end Method removeOneElementLists() -------------

  
  // =================================================
  /**
   * removeListWithTheSameDelimiter if all the list elements are 10.
   * This is not a list, but a bunch of sentences that end with 10.
   * 
   * @param pJCas
   * @param pList
  */
  // =================================================
 private final void removeListWithTheSameDelimiter(JCas pJCas, Annotation pList) {
   
   boolean returnVal = false;
   HashSet<String> delimiterHash = new HashSet<String>();
   FSArray listElements = (( gov.va.chir.model.List) pList ).getListElements();
   for ( int i = 0; i < listElements.size(); i++ ) {
      gov.va.chir.model.ListElement listElement = (gov.va.chir.model.ListElement) listElements.get(i);
      Delimiter delimiter = listElement.getDelimiter();
      String delimiterKey = delimiter.getCoveredText();
      
      if ( delimiterHash.contains( delimiterKey)) {
       returnVal = true;
       break;
      } else {
        delimiterHash.add(delimiterKey);
      }
   }
   
   if ( returnVal ) 
     for ( int i = 0; i < listElements.size(); i++ ) {
       gov.va.chir.model.ListElement listElement = (gov.va.chir.model.ListElement) listElements.get(i);
       removeListElement(pJCas,  listElement);
     }
     pList.removeFromIndexes();
       
  } // end Method removeListWithTheSameDelimiter() ---


  // =================================================
  /**
   * removeListAndElemens appends the delimiter to the sentence before where the
   * delimiter was and removes the list elements.
   * 
   * @param pJCas
   * @param aList
   */
  // ================================================
@SuppressWarnings("unchecked")
private final void removeListAndElement(JCas pJCas, gov.va.chir.model.List aList) {
 
  Annotation delimiter = getListDelimiter(pJCas, aList );
  

   // combine the bounds of this listElement with any overlapping sentence
   List<Annotation> sentenceBeforeDelimiter = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Sentence.typeIndexID,  delimiter.getBegin() -4, delimiter.getBegin() );
   if ( sentenceBeforeDelimiter != null && !sentenceBeforeDelimiter.isEmpty())
     sentenceBeforeDelimiter.get(0).setEnd( delimiter.getEnd());
     
    
   FSArray listElementz = aList.getListElements();
   if ( listElementz != null ) {
     List<Annotation >elements = UIMAUtil.fSArray2List(pJCas, listElementz);
     if ( elements != null && elements.size() > 0 )
       for (Annotation element : elements ) {
         Delimiter delimiter2 = ((ListElement) element ).getDelimiter();
         if ( delimiter2 != null )
           delimiter2.removeFromIndexes();
         element.removeFromIndexes();
       }
   }
   aList.removeFromIndexes();
   

 
 
} // end Method removeListAndElement() -------------


// =================================================
/**
 * removeListElement appends the delimiter to the sentence before where the
 * delimiter was and removes the list element
 * 
 * @param pJCas
 * @param pListElement
 */
// ================================================
private final void removeListElement(JCas pJCas, gov.va.chir.model.ListElement pListElement) {

Annotation delimiter = pListElement.getDelimiter();

if ( delimiter != null) {

 // combine the bounds of this listElement with any overlapping sentence
 List<Annotation> sentenceBeforeDelimiter = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Sentence.typeIndexID,  delimiter.getBegin() -4, delimiter.getBegin() );
 if ( sentenceBeforeDelimiter != null && !sentenceBeforeDelimiter.isEmpty())
   sentenceBeforeDelimiter.get(0).setEnd( delimiter.getEnd());
   
 delimiter.removeFromIndexes();
 pListElement.removeFromIndexes();
 
}



} // end Method removeListAndElement() -------------

  // =================================================
  /**
   * getListDelimiter retrieves the first list delimiter 
   * @param pJCas
   * @param aList
   * @return Annotation
  */
  // =================================================
  private final Annotation getListDelimiter(JCas pJCas, gov.va.chir.model.List aList) {
    
    Annotation returnVal = null;
    FSArray listElementz = aList.getListElements();
    if ( listElementz != null ) {
      @SuppressWarnings("unchecked")
      List<Annotation >elements = UIMAUtil.fSArray2List(pJCas, listElementz);
      if ( elements != null && elements.size() > 0 )
        for (Annotation element : elements ) {
          Delimiter delimiter = ((ListElement) element ).getDelimiter();
          if ( delimiter != null )
            returnVal = delimiter;
       }
    }
    return returnVal;
  } // end Method 


  // =================================================
   /**
    * removeListAndElemens 
    * 
    * @param pJCas
    * @param aList
    */
   // ================================================
private final void removeListAndElementObs(JCas pJCas, gov.va.chir.model.List aList) {
	
	FSArray elementz = aList.getListElements();
	@SuppressWarnings("unchecked")
	List<Annotation> elements = UIMAUtil.fSArray2List(pJCas, elementz);
	for ( Annotation element: elements ) {
		Annotation delimiter = ((ListElement) element).getDelimiter();
		
		// combine the bounds of this listElement with any overlapping sentence
		List<Annotation> sentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Sentence.typeIndexID,  element.getBegin(), element.getEnd() );
		if ( sentences != null && !sentences.isEmpty())
			mergeIntoOneSentence( pJCas, sentences, element );
		element.removeFromIndexes();
		delimiter.removeFromIndexes();
		//for ( Annotation sentence : sentences )
		//	sentence.removeFromIndexes();
		
	
	}
	aList.removeFromIndexes();
	
	
} // end Method removeListAndElement() -------------


// =================================================
/**
 * mergeIntoOneSentence merges any sentences and List Element
 * that are a part of a one element list.
 * a list element
 * 
 * @param pJCas
 * @param pSentences 
 * @param pListElement
*/
// =================================================
private final Annotation mergeIntoOneSentence(JCas pJCas, List<Annotation> pSentences, Annotation pListElement) {
	
	Annotation returnVal = null;
	int _begin = pListElement.getBegin();
	int _end   = pListElement.getEnd();
	for ( Annotation sentence : pSentences ) {
		_begin = Math.min(sentence.getBegin(), _begin);
		_end   = Math.max( sentence.getEnd(), _end );
	}
	returnVal = createSentence( pJCas, (Sentence) pSentences.get(0), _begin, _end );	
	
	
	
	return returnVal;
} // end Method mergeIntoOneSentence() --------------

//=================================================
/**
* mergeIntoOneSentence merges any sentences and List Element
* that are a part of a one element list.
* a list element
* 
* @param pJCas
* @param pSentences 
*/
//=================================================
private final Annotation mergeIntoOneSentence(JCas pJCas, List<Annotation> pSentences ) {

  Annotation returnVal = null;
  int _begin = 99999999;
  int _end   = 0;

  for ( Annotation sentence : pSentences ) {
  _begin = Math.min(sentence.getBegin(), _begin);
  _end   = Math.max( sentence.getEnd(), _end );
  }


  returnVal = createSentence( pJCas, (Sentence) pSentences.get(0), _begin, _end );  



return returnVal;
} // end Method mergeIntoOneSentence() --------------

//=================================================
/**
* createSentence creates a sentence
* a list element
* 
* @param pJCas
* @param pListElement
*/
// =================================================
private final Annotation createSentence(JCas pJCas, Sentence pOldSentence, int pBegin, int pEnd ) {
	
  
  if ( pBegin > pEnd ) {
    System.err.println( "Something wrong here ");
    throw new RuntimeException( "Somthing wrong here with this sentence");
    
  }
	Sentence statement = new Sentence( pJCas);
	statement.setBegin( pBegin);
	statement.setEnd( pEnd);
	statement.setId("SentenceListRepair_from_" + ((Sentence) pOldSentence).getId());
	statement.setConditional(pOldSentence.getConditional() );
	statement.setEventDate(pOldSentence.getEventDate() );
	statement.setGeneric(pOldSentence.getGeneric());
	statement.setHistorical(pOldSentence.getHistorical( ));
	statement.setInProse(pOldSentence.getInProse( ));
	statement.setMarked(pOldSentence.getMarked( ));
	statement.setNegation_Status(pOldSentence.getNegation_Status( ));
	statement.setOtherFeatures(pOldSentence.getOtherFeatures( ));
	statement.setParent(pOldSentence.getParent( ));
	statement.setPhrases(pOldSentence.getPhrases( ));
	statement.setProcessMe(pOldSentence.getProcessMe( ));
	statement.setReferenceDate(pOldSentence.getReferenceDate( ));
	statement.setSection(pOldSentence.getSection( ));
	statement.setSectionName(pOldSentence.getSectionName( ));
	statement.setSentenceNumber(pOldSentence.getSentenceNumber( ));
	statement.setStatementDate(pOldSentence.getStatementDate( ));
	statement.setSubject(pOldSentence.getSubject( ));
	statement.setUncertainty(pOldSentence.getUncertainty( ));
	statement.setUtteranceLexicalElements(pOldSentence.getUtteranceLexicalElements( ));
	statement.setUtteranceTokens(pOldSentence.getUtteranceTokens( ));
	statement.addToIndexes();
	
	return statement;
	
} // end Method createSentence() -------------------

// =================================================
  /**
   * processListElement truncates sentences that fall within
   * a list element
   * 
   * @param pJCas
   * @param pListElement
  */
  // =================================================
  private final void processListElement(JCas pJCas, Annotation pListElement) {
    
    
    List<Annotation> sentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Sentence.typeIndexID, pListElement.getBegin(), pListElement.getEnd());
    
    if ( sentences != null && !sentences.isEmpty() ) {
      
      // look at the last of these
      Annotation lastSentence = sentences.get(   sentences.size() - 1);
      
      if ( lastSentence.getEnd() > pListElement.getEnd() ) {
        lastSentence.setEnd(  pListElement.getEnd() );
        
        // remove the terms and tokens that are beyond the end
        removeTokens( pJCas, ((Sentence)lastSentence), pListElement.getEnd() );
        removeTerms( pJCas, ((Sentence)lastSentence), pListElement.getEnd() );
      }
      
    }
     
  } // end Method processListElement()--------------




  // =================================================
  /**
   * removeTokens removes the tokens that go beyond the list element
   * 
   * @param pSentence
   * @param pEnd
  */
  // =================================================
  private final void removeTokens(JCas pJCas, Sentence pSentence, int pEnd) {
   
  
    FSArray tokenz = pSentence.getUtteranceTokens();
    List<WordToken> culledTokens = new ArrayList<WordToken>();
  
    if ( tokenz != null && tokenz.size() > 0 ) {
      for ( int i = 0; i < tokenz.size(); i++) {
        if ( ((Annotation)tokenz.get(i)).getEnd() <= pEnd )
          culledTokens.add( (WordToken) tokenz.get(i)) ;
      }
      FSArray culledTokenz = UIMAUtil.list2FsArray(pJCas, culledTokens);
      pSentence.setUtteranceTokens( culledTokenz);
    }
    
  } // end Method removeTokens() ---------------------




  // =================================================
  /**
   * removeTerms removes the terms that go beyond the list element
   * 
   * @param pSentence
   * @param pEnd
  */
  // =================================================
 private final void removeTerms(JCas pJCas, Sentence pSentence, int pEnd) {
    
    FSArray termz = pSentence.getUtteranceLexicalElements();
    List<LexicalElement> culledTerms = new ArrayList<LexicalElement>();
    
    if ( termz != null && termz.size() > 0 ) {
      for ( int i = 0; i < termz.size(); i++) {
        if ( ((Annotation)termz.get(i)).getEnd() <= pEnd )
          culledTerms.add( (LexicalElement) termz.get(i)) ;
      }
      FSArray culledTermz = UIMAUtil.list2FsArray(pJCas, culledTerms);
      pSentence.setUtteranceLexicalElements( culledTermz);
    
    }
  } // end Method removeTerms() ---------------------




  // ----------------------------------
  /**
   * destroy
   * 
   **/
  // ----------------------------------
  public void destroy() {
    this.performanceMeter.writeProfile(this.getClass().getSimpleName());
  }

  // ----------------------------------
  /**
   * initialize loads in the resources needed for slotValues. Currently, this involves
   * a list of known slot names that might show up.
   *
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {

    String[] args = null;
    try {
      args = (String[]) aContext.getConfigParameterValue("args");

      initialize( args);
      
    } catch (Exception e) {
      System.err.println("Issue - initializing " + this.getClass().getName());
      throw new ResourceInitializationException();
    }
  } // end Method initialize() ---------------
  

  // ----------------------------------
  /**
   * initialize loads in the resources needed for slotValues. Currently, this involves
   * a list of known slot names that might show up.
   *
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(String pArgs[] ) throws ResourceInitializationException {

   
    try {
      this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getSimpleName());

      
    } catch (Exception e) {
      System.err.println("Issue - initializing " + this.getClass().getName());
      throw new ResourceInitializationException();
    }

   
  } // end Method initialize() ---------------


  // -----------------------------------------
  // class Variables
  // -----------------------------------------

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  private ProfilePerformanceMeter performanceMeter = null;
  

} // end Class SlotValueRepairAnnotator ---------------------
