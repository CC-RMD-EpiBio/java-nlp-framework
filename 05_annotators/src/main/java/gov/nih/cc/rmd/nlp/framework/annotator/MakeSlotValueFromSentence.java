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
 * MakeSlotValueFromSentence creates a slot value within a sentence
 * that has xxx = yyy in it or xxx equals yyy
 * only the term before is used as the slot and the term after is the value.
 * 
 * 
 *
 * @author  Guy Divita 
 * @created June 23, 2016
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Delimiter;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.Token;
import gov.va.chir.model.VAnnotation;
import gov.va.chir.model.WordToken;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class MakeSlotValueFromSentence extends JCasAnnotator_ImplBase {
  


  private int annotationCounter;



  @Override
  // -----------------------------------------
  /**
   * process finds slotValues in the whole document
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
    this.performanceMeter.startCounter();
    List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID);
    

    if ( sentences != null && !sentences.isEmpty() ) {
      for ( Annotation sentence : sentences )
        process( pJCas, (Sentence) sentence  );
    }
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
     // throw new AnalysisEngineProcessException();
    }
  } // end Method process
      
  //----------------------------------
  /**
   * process
   * 
   *  (Small flaw in this, - this will only pick up the first 
   * @param pJCas
   * @param pSlotAndValue
   * @param pIteration   ( don't do more than 1 iterations
   * 
   **/
  // ----------------------------------  
  private void process(JCas pJCas, Sentence pSentence ) {
  
   String sentence =  pSentence.getCoveredText();
   System.err.println(sentence);
   List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd());
  
   LexicalElement termBefore = null;
   LexicalElement term = null;
   LexicalElement termAfter = null;
   if ( terms != null && !terms.isEmpty()) {
    int i = 1;
    while (  i < terms.size()-1 ) {
      termBefore = (LexicalElement) terms.get( i -1 );
            term = (LexicalElement) terms.get(i);
       termAfter = (LexicalElement) terms.get(i + 1);
      
      String aTerm = term.getCoveredText();
  
      if ( aTerm != null && !aTerm.isEmpty() && 
          (aTerm.toLowerCase().equals("=")     ||
           aTerm.toLowerCase().equals("equals") ||
           aTerm.toLowerCase().equals("equal") ) ) {
    
          createSlotValue(pJCas, termBefore, term, termAfter );
          
      }
      i++;
    } // end Loop thru terms
   } // end if terms are empty
   
  } // End Method process() ======================
  



// =======================
  /**
   * createSlotValue 
   *
   * @param pJCas
   * @param termBefore
   * @param term
   * @param termAfter
   */
  // =======================
  private void createSlotValue(JCas pJCas, LexicalElement pSlot, LexicalElement pDelimiter, LexicalElement pValue) {
  
    int      begin =  pSlot.getBegin();
      int    slotEnd = pSlot.getEnd();
      int valueBegin = -1;
      int valueEnd = slotEnd;
      if ( pValue != null ) {
        valueBegin = pValue.getBegin();
        valueEnd =  pValue.getEnd();
      }
      int z = valueEnd - valueBegin;
      
      if (!( z > 0 && z < 3000 )) return;
      
      SlotValue     slotValue = new SlotValue(pJCas);
     
     // VUIMAUtil.setProvenance( pJCas, slotValue, this.getClass().getName() );
      slotValue.setBegin(begin);
      slotValue.setEnd( valueEnd );
      slotValue.setDisplayString( slotValue.getCoveredText() );
      slotValue.setId( "SlotValue_FromSentence_" + this.annotationCounter++);
      
      // --------------------------
      // Content Heading  (here the content heading should not make phrases to process)
      
      // --------------------------
      // If a content heading already exists, attach this to the slot value
      //      also, delete a section that is the slot + value 
     
      ContentHeading contentHeader = getExistingContentHeader( pJCas, pSlot);
      if ( contentHeader == null )
         contentHeader = createContentHeading( pJCas, pSlot );
      else {
        // delete any section that covers just the slot + value
   //     SlotValueAnnotator.deleteSection( pJCas, slotValue);
      }
      
      contentHeader.setParent(slotValue);
      String contentHeaderString = contentHeader.getCoveredText();
      
      if ( contentHeaderString == null || contentHeaderString.trim().length() == 0 ) {
        System.err.println("Something is wrong here empty content heading of a slotValue " );
       // throw new RuntimeException();
      }
      slotValue.setContentHeaderString(contentHeaderString);
      slotValue.setHeading(contentHeader);
      slotValue.setProcessContent( true);
      
      
     
      // ------------------------
      // Delimiter
      
      if ( pDelimiter != null ) {
        Delimiter delimiter = createDelimiter( pJCas, pDelimiter, slotValue );
        slotValue.setDelimiter(delimiter.getCoveredText());
      }
        
      // ------------------------
      // Dependent content
      if ( pValue != null ) {
       
        DependentContent contentValue = createDependentContent(pJCas, pValue, slotValue );
        contentValue.setParent( slotValue);
        
        slotValue.setContentString( contentValue.getCoveredText());
        slotValue.setDependentContent( contentValue);
       
      }
     
     // if ( !this.assertInAssertionAnnotator )  ---- now done in assertion annotator properly
      //  assertQuestionAnswer( pJCas, slotValue);
      
      slotValue.addToIndexes();
      
      // System.err.println("Made a slot value");
   
    
    
    
    
  } // End Method createSlotValue =======
  

// =======================
/**
 * getTokens 
 *
 * @param pJCas
 * @param pAnnotation
 * @return List<Annotation>
 */
// =======================
  private static List<Token> getTokens(JCas pJCas, LexicalElement pAnnotation) {
  
    List<Token> returnTokens = null;
    List<Annotation> tokens =  UIMAUtil.getAnnotationsBySpan(pJCas, WordToken.typeIndexID, pAnnotation.getBegin(), pAnnotation.getEnd() );
    
    if ( tokens != null) {
     returnTokens = new ArrayList<Token>( tokens.size());
     for ( Annotation token: tokens )
       returnTokens.add( (Token) token );
    }
    return returnTokens;
  } // End Method getTokens =======




  // =======================================================
  /**
   * getExistingContentHeader retrieves a content Heading that 
   * covers this set of pSlots
   * 
   * @param pJCas
   * @param pSlotTokens
   * @return ContentHeading
   */
  // =======================================================
  private  ContentHeading getExistingContentHeader(JCas pJCas, Annotation pSlot) {
   
    ContentHeading contentHeading = null;
    int start = pSlot.getBegin();
    int   end = pSlot.getEnd(); 
    List<Annotation> contentHeadings = UIMAUtil.getAnnotationsBySpan(pJCas, ContentHeading.typeIndexID, start, end);
    
    if ( contentHeadings != null && contentHeadings.size() > 0 ) {
      contentHeading = (ContentHeading) contentHeadings.get(0);
    }
    return contentHeading;
  } // End Method getExistingContentHeader() ======================
  


  // ------------------------------------------
  /**
   * createContentHeading
   *
   *
   * @param pJCas
   * @param pSlotTokens
   */
  // ------------------------------------------
  private  ContentHeading createContentHeading(JCas pJCas, Annotation pSlot ) {
    
    ContentHeading contentHeader = new ContentHeading(pJCas);
    // VUIMAUtil.setProvenance( pJCas, contentHeader, this.getClass().getName() );
    contentHeader.setBegin( pSlot.getBegin());
    contentHeader.setEnd( pSlot.getEnd() );
  
    contentHeader.setId( "ContentHeader_" + this.annotationCounter++);
    contentHeader.setProcessMe(false);
    contentHeader.addToIndexes();
    
    return contentHeader;
    
    // End Method createContentHeading() -----------------------
  }



  // ------------------------------------------
  /**
   * createDelimiter
   *
   *
   * @param pJCas
   * @param pDelimiter
   */
  // ------------------------------------------
  private Delimiter createDelimiter(JCas pJCas, Annotation pDelimiter, VAnnotation pParent ) {
   
    Delimiter delimiter = new Delimiter( pJCas);
    delimiter.setBegin(pDelimiter.getBegin());
    delimiter.setEnd(pDelimiter.getEnd());
    
    delimiter.setId( "delimiter_" + this.annotationCounter++);
    // VUIMAUtil.setProvenance( pJCas, delimiter, this.getClass().getName() );
    delimiter.setParent(pParent );
    delimiter.setProcessMe(false);
    delimiter.addToIndexes();
    
    return delimiter;
    
  }  // End Method createDelimiter() -----------------------
  
  // ------------------------------------------
  /**
   * createDependantContent
   *
   *
   * @param pJCas
   * @param pValueTokens
   * @param slotValue.getEnd() 
   */
  // ------------------------------------------
  private  DependentContent createDependentContent(JCas pJCas, Annotation pValue, SlotValue slotValue ) {
    
    DependentContent  content = new DependentContent(pJCas);
   
    content.setBegin( pValue.getBegin());
    content.setEnd  ( pValue.getEnd() );
  
    content.setId("SlotValueFixed2Content_" + this.annotationCounter++);
    content.addToIndexes();
    
    return content;
  }  // End Method createDependantContent() -----------------------
  


  
  
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
      args                 = (String[]) aContext.getConfigParameterValue("args");  
 
    } catch (Exception e ) {
      System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
    
    this.performanceMeter = new ProfilePerformanceMeter( args, this.getClass().getSimpleName() );
    
    
    
  } // end Method initialize() ---------

    
 
  // -----------------------------------------
  // class Variables
  // -----------------------------------------

//---------------------------------------
//Global Variables
//---------------------------------------
  private ProfilePerformanceMeter  performanceMeter = null;
  
  
} // end Class SlotValueRepairAnnotator ---------------------
