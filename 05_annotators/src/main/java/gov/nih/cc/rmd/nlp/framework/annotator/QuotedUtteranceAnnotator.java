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
