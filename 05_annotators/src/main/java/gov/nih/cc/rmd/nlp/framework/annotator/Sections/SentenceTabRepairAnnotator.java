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
