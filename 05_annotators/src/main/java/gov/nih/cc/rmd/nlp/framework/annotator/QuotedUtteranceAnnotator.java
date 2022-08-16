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
 * QuotedUtterancesAnnotator creates a QuotedUtterance for text that is encapsulated within quotes. 
 * This allows one to find sources of the expression - like the patient said "xxxx xxxx" 
 * 
 * Of note, a quoted utterance could include more than a phrase worth.  It could be
 * a paragraph. 
 * 
 * @author Guy Divita
 * @created Dec 31, 2019
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.DoubleQuote;
import gov.va.chir.model.QuotedUtterance;


public class QuotedUtteranceAnnotator extends JCasAnnotator_ImplBase {

  @Override
  // -----------------------------------------
  /**
   * process finds quoted utterances within text.  If there are sectionZones,
   * it will process a section at a time, if not
   * it will process the whole document as a section.
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
      this.performanceMeter.startCounter();

      
      List<Annotation> sections = UIMAUtil.getAnnotations( pJCas, SectionZone.typeIndexID, true );
      
      if ( sections == null  || sections.isEmpty())
        process ( pJCas, (Annotation) null) ;
        
      if ( sections != null  && !sections.isEmpty())
        for ( Annotation section : sections)
          process( pJCas, section);
   
    this.performanceMeter.stopCounter();
    

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
      // throw new AnalysisEngineProcessException();
    }
  } // End Method process() --------------------------

 
 // =================================================
  /**
   * process looks for quotedUtterances within the window of a section.
   * There's got to be an even number of quotes to do this
   * 
   * @param pJCas
   * @param pSection
  */
  // =================================================
  private void process(JCas pJCas, Annotation pSection) {
    
 
    String docText = null;
    int sectionBegin = 0;
    int sectionEnd = -1;
    
    if ( pSection != null ) {
     docText = pSection.getCoveredText();
     sectionBegin = pSection.getBegin();
     sectionEnd = pSection.getEnd();
    } else {
      docText = pJCas.getDocumentText();
      sectionBegin = 0;
      sectionEnd = docText.length();
    }
    
    
    // Look for quotes - 
    int lastSeen =  -1;
    int seen = 0;
    int quotesSeen = 0;
  
    while (seen > -1 ) {
     seen = docText.indexOf(DOUBLE_QUOTE, lastSeen+1) ;
     
     if ( seen > -1) {
       // create a quote annotation
       createQuotes(pJCas, seen + sectionBegin, seen + sectionEnd +1);
       quotesSeen++;
     }
     lastSeen = seen;
    }
    
    if ( quotesSeen > 0 && quotesSeen % 2 == 0 ) {
      int begin_ = sectionBegin;
      int end_ = sectionEnd;
      List<Annotation> quotes = UIMAUtil.getAnnotationsBySpan(pJCas, DoubleQuote.typeIndexID,  begin_, end_);
      
      if ( quotes != null && !quotes.isEmpty() ) 
        for ( int i = 0; i < quotes.size(); i=i+2) {
          createQuotedUtterance( pJCas, quotes.get(i).getBegin(), quotes.get(i+1).getEnd());
        }
      
      
    } else {
      if ( quotesSeen > 0 )
        GLog.println(GLog.DEBUG_LEVEL, QuotedUtteranceAnnotator.class, "process", "something is not right here" );
    }
      


  }


// =================================================
  /**
   * createQuotes
   * 
   * @param pJCas
   * @param pBegin
   * @param pEnd
  */
  // =================================================
  private Annotation createQuotes(JCas pJCas, int pBegin, int pEnd) {
   
    DoubleQuote statement = new DoubleQuote( pJCas);
    statement.setBegin( pBegin);
    statement.setEnd ( pEnd );
    statement.addToIndexes();
    
    return statement;
    
    
  } // end Method createQuotedUtterance() ----------


// =================================================
  /**
   * createQuotedUtterance
   * 
   * @param pJCas
   * @param pBegin
   * @param pEnd
   * @return Annotation
  */
  // =================================================
  private Annotation createQuotedUtterance(JCas pJCas, int pBegin, int pEnd) {
    
    QuotedUtterance statement = new QuotedUtterance( pJCas);
    statement.setBegin( pBegin);
    statement.setEnd ( pEnd );
    statement.addToIndexes();
    
    return statement;
    
    
  } // end Method createQuoteAnnotation() ----------





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
  private static final char DOUBLE_QUOTE = '"';
  private ProfilePerformanceMeter performanceMeter = null;
  

} // end Class SlotValueRepairAnnotator ---------------------
