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
 * SemanticWindowAnnotator combines multiple sentence fragments into an utterance
 * to allow for scoping that are smaller than a section but bigger than sentences.
 * 
 * List elements that are separated by newlines are showing up as sentences; 
 * 
 * Combine sentences that have a colon in the first sentence, until you hit a period.
 * into an utterance
 * 
 * Combine sentences that are separated by colons and semi-colons.
 *   
 *
 * @author  Guy Divita 
 * @created Aug 15, 2018
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.Sections;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.framework.TableRow;
import gov.nih.cc.rmd.model.SectionName;
import gov.nih.cc.rmd.model.SemanticWindow;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Line;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.WordToken;



public class SemanticWindowAnnotator extends JCasAnnotator_ImplBase {
 
 
  public static final String annotatorName = SemanticWindowAnnotator.class.getSimpleName();
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    
    this.performanceMeter.startCounter();
    int documentLength = pJCas.getDocumentText().length();
   
    List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID);
    
    if ( sentences != null && !sentences.isEmpty() ) {
      
      List<Annotation> utterance = new ArrayList<Annotation>();
      
      Annotation lastSentence = null;
      for ( Annotation sentence : sentences ) {
        
        
        // sectionNames are semantic window breaking entities
        if ( containsSectionName( pJCas, sentence )) {
          if ( utterance != null && !utterance.isEmpty()) 
            makeUtterance( pJCas, utterance);
          utterance = new ArrayList<Annotation>();
        } else if ( !contagious( pJCas, (Sentence) lastSentence, (Sentence) sentence)) {
          if ( utterance != null && !utterance.isEmpty()) 
            makeUtterance( pJCas, utterance);
          utterance = new ArrayList<Annotation>();
        } else {
          
        Annotation nextWordToken = getNextWordToken( pJCas, sentence, documentLength);
        // what does the sentence end with
        if ( nextWordToken != null ) {
          String nextWordTokenChar = nextWordToken.getCoveredText().trim();
          
          if ( containsSectionName( pJCas, nextWordToken)) {
            if ( utterance != null && !utterance.isEmpty()) 
              makeUtterance( pJCas, utterance);
            utterance = new ArrayList<Annotation>();
          } else {
       
            switch ( nextWordTokenChar ) {
              case ":" : utterance.add( sentence); break;
              case ";" : utterance.add( sentence); break;
              case "." : 
                if ( utterance != null && !utterance.isEmpty())
                  makeUtterance( pJCas, utterance ); 
                utterance = new ArrayList<Annotation>(); break;
              default  : utterance.add( sentence); break;  
            } // end switch
          } // end if the sentence is a sectionName
        } // end if the next token is in a section name
        
        } // end if there is a next token
        lastSentence = sentence;
     
      } // end loop through sentences
      if ( utterance != null && !utterance.isEmpty())
        makeUtterance( pJCas, utterance );
      
    } // end if there are sentences in this document 
    
    
        
    this.performanceMeter.stopCounter();     
    
  }  // end Method process() -------------------------
    
  // =================================================
  /**
   * Contagious returns true if there is no gap between
   * the last sentence tokens and the current sentence
   * 
   * @param pJCas
   * @param lastSentence
   * @param currentSentence
   * @return boolean
  */
  // =================================================
  private boolean contagious(JCas pJCas, Sentence pLastSentence, Sentence pCurrentSentence) {
    
    boolean returnVal = false;
    
    if ( pLastSentence != null && pCurrentSentence != null ) {
    List<Annotation> lastTokens = UIMAUtil.getAnnotationsBySpan( pJCas, WordToken.typeIndexID, pLastSentence.getBegin(), pLastSentence.getEnd());
    List<Annotation> currentTokens = UIMAUtil.getAnnotationsBySpan( pJCas, WordToken.typeIndexID, pCurrentSentence.getBegin(), pCurrentSentence.getEnd());
    
    if ( lastTokens != null && !lastTokens.isEmpty() && currentTokens != null && !currentTokens.isEmpty()) {
      Annotation lastTokenLastToken = lastTokens.get(lastTokens.size() -1);
      Annotation firstTokenCurrentTokens = currentTokens.get(0);
      
      String buff_1 = ((WordToken)lastTokenLastToken).getId();
      String buff_2 = ((WordToken)firstTokenCurrentTokens).getId();
      
      char b1 = buff_1.charAt( buff_1.length() -1);
      char b2 = buff_2.charAt( buff_2.length() -1);
      
      if (( b2 - b1 ) < 3 )
        returnVal = true;
    }
    }
    return returnVal;
  } // end Method contagious() -----------------------

  // =================================================
  /**
   * containsSectionName returns true if there is a section name overlapping
   * this sentence.
   * 
   * @param pJCas
   * @param pSentence
   * @return boolean
  */
  // =================================================
  private final boolean containsSectionName(JCas pJCas, Annotation pSentence) {
    boolean returnVal = false;
    
    List<Annotation> sectionNames = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  SectionName.typeIndexID, pSentence.getBegin(), pSentence.getEnd() );
   
    if ( sectionNames != null && !sectionNames.isEmpty())
      returnVal = true;
      
    return returnVal;
  } // end Method containsSectionName() -------------

  // =================================================
  /**
   * makeUtterance creates an over-arching utterance across these
   * sentences
   * 
   * @param pJCas
   * @param pUtterance
  */
  // =================================================
private final void makeUtterance(JCas pJCas, List<Annotation>pUtterance) {
    
   SemanticWindow statement = new SemanticWindow( pJCas );
   
   int _begin = pUtterance.get(0).getBegin();
   int _end   = pUtterance.get( pUtterance.size() -1 ).getEnd();
   
   statement.setBegin( _begin);
   statement.setEnd( _end);
   statement.setId ( "SemanticWindowAnnotator_" + this.annotationCounter++);
   statement.addToIndexes();
    
  } // end Method 



  // End Method process() ------------------
  
 

// =================================================
  /**
   * getNextWordToken 
   * 
   * @param pJCas
   * @param sentence
   * @return Annotation (WordToken)
  */
  // =================================================
 private Annotation getNextWordToken(JCas pJCas, Annotation pAnnotation, int pDocumentLength) {
   
   
   int _begin = pAnnotation.getEnd() ;
   int _end = _begin + 3;
   Annotation nextWordToken = null;
   
   
   if ( _end > pDocumentLength )
     _end = pDocumentLength -1;
   List<Annotation>nextTokens = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, WordToken.typeIndexID, _begin, _end );
    if ( nextTokens != null && !nextTokens.isEmpty())
      nextWordToken = nextTokens.get(0);
    return nextWordToken;
    
  } // end Method getNextWordToken() ----------------



  
/**
  * destroy
  * 
  **/
 public void destroy() {
    if ( this.performanceMeter != null )
      this.performanceMeter.writeProfile( this.getClass().getSimpleName());
 } // end Method destroy() ----------

 
 //----------------------------------
 /**
  * initialize
  * 
  * @param aContext
  * 
  **/
 // ----------------------------------
 public void initialize(UimaContext aContext) throws ResourceInitializationException {
   
   String[] args = null;
   try {
     args                 = (String[]) aContext.getConfigParameterValue("args");  
     
     initialize( args );
     
   } catch (Exception e ) {
     e.printStackTrace();
     String msg = "Issue initializing " + annotatorName + " " + e.toString() ;
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", msg);
     throw new ResourceInitializationException();
   }
   
 } // end Method initialize() --------
 
//----------------------------------
/**
 * initialize loads in the resources needed
 * 
 * @param pArgs
 * 
 **/
// ----------------------------------
public void initialize(String[] pArgs) throws ResourceInitializationException {
  
 
  try {
    
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
   
    
  } catch (Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", "Issue initializing the " + annotatorName + " " + e.toString() );
    throw new ResourceInitializationException();
  }
  
} // end Method Initialize() -----------


 // ---------------------------
 // Global Variables
 // ---------------------------

 private int annotationCounter = 0;
 private ProfilePerformanceMeter              performanceMeter = null;

} // end Class TableSectionZoneAnnotator() ---------------
