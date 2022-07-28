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
 * Sentence tab repair iterates through sentences with tabs in them
 * makes them table rows, and breaks the sentences into separate sentences. Tabs are equivalent to .
 *   
 *
 * @author  Guy Divita 
 * @created Aug 15, 2018
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.Sections;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.framework.TableRow;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Line;
import gov.va.chir.model.Sentence;



public class SentenceTabRepairAnnotator extends JCasAnnotator_ImplBase {
 
 
  public static final String annotatorName = SentenceTabRepairAnnotator.class.getSimpleName();
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    
    this.performanceMeter.startCounter();
   
    List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID);
    
    if ( sentences != null && !sentences.isEmpty() ) 
      
      for ( Annotation sentence : sentences )
        processSentence( pJCas, sentence );
    
   // There will be some embedded sentences within the tableRows now
   // that will have newlines in them, but not tabs.  
  
    List<Annotation> tableRows = UIMAUtil.getAnnotations(pJCas, TableRow.typeIndexID);
     if ( tableRows!= null && !tableRows.isEmpty())
       for ( Annotation tableRow : tableRows )
         processTableRow( pJCas, tableRow ); 
    
    this.performanceMeter.stopCounter();     
    
  } // End Method process() ------------------
  
 

// =================================================
  /**
   * processTableRow finds the sentences in the table row
   * if any have newlines, breaks the sentence on the newline
   * 
   * @param pJCas
   * @param pTableRow
  */
  // =================================================
  private final void processTableRow(JCas pJCas, Annotation pTableRow) {
   
    List<Annotation> sentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Sentence.typeIndexID,  pTableRow.getBegin(), pTableRow.getEnd(), false);
    
    if ( sentences != null && !sentences.isEmpty())
      for ( Annotation sentence : sentences ) {
        String text = sentence.getCoveredText();
        if ( text != null && text.contains("\n")) {
          makeTabSentences( pJCas, (Sentence)sentence);
          sentence.removeFromIndexes();
        }
      }
      
  } // end processTableRow() --------------------------



// =================================================
  /**
   * processSentence looks for sentences with tabs in them
   * 
   * @param pJCas
   * @param pSentence
  */
  // =================================================
 private void processSentence(JCas pJCas, Annotation pSentence) {
   
   if ( pSentence != null ) {
     try {
     String buff = pSentence.getCoveredText();
     if ( buff != null && buff.trim().length() > 0 && buff.contains("\t") ) {
       makeTableRow( pJCas, pSentence );
       makeTabSentences( pJCas, (Sentence) pSentence);
       pSentence.removeFromIndexes();
     }
     } catch ( Exception e) {
       e.printStackTrace();
       System.err.println("Issue with a bad sentence :" + e.toString());
     }
   }
  } // end Method processSentence() ------------------



  // =================================================
/**
 * makeTableRow makes a tabRow from this sentence
 *   This sentence might span more than one line
 * 
 * @param pSentence
*/
// =================================================
private final void makeTableRow(JCas pJCas, Annotation pSentence) {
 
    List<Annotation> lines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Line.typeIndexID,  pSentence.getBegin(), pSentence.getEnd());
    
    if ( lines != null && !lines.isEmpty())
      for ( Annotation line: lines ) {
        TableRow statement = new TableRow( pJCas);
        statement.setBegin( line.getBegin());
        statement.setEnd(   line.getEnd() );
        statement.setId( "SentenceTabRepair_" + this.annotationCounter++);
        statement.addToIndexes();
      }
}



  // =================================================
/**
 * makeTabSentences make small sentences from the text, break on tab
 * break on \n
 * 
 * @param pSentence
*/
// =================================================
 private final void makeTabSentences(JCas pJCas, Sentence pSentence) {
  
   String buff = pSentence.getCoveredText();
   int offset = pSentence.getBegin();
   
   char[] cBuff = buff.toCharArray();
   StringBuffer sentenceBuff = new StringBuffer();
   for ( int i = 0; i < cBuff.length; i++ ) {
     if ( cBuff[i] == '\t'  || cBuff[i] == '\n') {
       if ( sentenceBuff != null && sentenceBuff.length() > 0 )
         createSentence ( pJCas, sentenceBuff, offset, pSentence);
       sentenceBuff = new StringBuffer();
       offset = i + pSentence.getBegin() + 1;
     } else {
       sentenceBuff.append( cBuff[i]);
     }
   } // end loop
   
   if ( sentenceBuff != null && sentenceBuff.length() > 0 )
     createSentence ( pJCas, sentenceBuff, offset, pSentence);
   
  
  
}



  // =================================================
  /**
   * createSentence creates a sentence from the sentence buff and puts it at the offset
   * 
   * @param pJCas
   * @param pSentenceBuff
   * @param pOffset
   * @param pSentence
  */
  // =================================================
 private Sentence createSentence(JCas pJCas, StringBuffer pSentenceBuff, int pOffset, Sentence pSentence) {
    
   Sentence statement = new Sentence( pJCas);
   statement.setBegin( pOffset );
   statement.setEnd( pOffset + pSentenceBuff.length() );
   statement.setId("SentenceTabRepairAnnotator_" + this.annotationCounter++ );
   statement.setInProse(false);
   statement.setMarked(false );
   statement.setProcessMe( true);
   statement.addToIndexes();
   
   
   return statement;
    
  } // end Method createSentence() ------------------



  // =================================================
  /**
   * createTableSectionZone 
   * 
   * @param pJCas
   * @param pLines
   *  
  */
  // =================================================
  private final void createTableSectionZone(JCas pJCas, List<Annotation> pLines ) {
  
  
 

    SectionZone statement = new SectionZone(pJCas);
    
    int offsetBegin = pLines.get(0).getBegin();
    int offsetEnd   = pLines.get(pLines.size()-1).getEnd();
    statement.setBegin( offsetBegin  );
    statement.setEnd( offsetEnd);
    statement.setSectionName("Table");
    // statement.setSectionTypes(v);
    statement.setId( "TableAnnotator_" + this.annotationCounter++);
    statement.addToIndexes();
   
  
 } // end Method createTableSectionZone()  -------
  
  
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
