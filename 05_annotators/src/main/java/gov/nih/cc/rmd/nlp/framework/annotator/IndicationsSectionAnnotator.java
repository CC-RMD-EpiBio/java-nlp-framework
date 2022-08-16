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
 * DiarrheaAnnotator finds diarrhea mentions
 * 
 * #   Diarrhea = DiarrheaDirectMention 
 * #
 * #   DiarrheaEvidence =              
 * #              [ DiarrheaAnatomy + 
 * #              [ DiarrheaFrequency               -+ 
 * #                DiarrheaSeverity |               |   One or more of these
 * #                DiarrheaColor |                  |
 * #                DiarrheaConsistancy |            |
 * #                DiarrheaSensation |              |
 * #                DiarrheaPathologicalModifier  |  |
 * #                DiarrheaSymptoms ]              -+
 * #   DiarrheaEvidence =              
 * #              [ NormalDefication + 
 * #              [ DiarrheaFrequency               -+ 
 * #                DiarrheaSeverity |               |   One or more of these
 * #                DiarrheaColor |                  |
 * #                DiarrheaConsistancy |            |
 * #                DiarrheaSensation |              |
 * #                DiarrheaPathologicalModifiers |  |
 * #                DiarrheaSymptoms ]              -+
 * #   DiarrheaEvidence =              
 * #                DiarrheaLabTest
 

 * @author  Guy Divita 
 * @created July 12, 2016
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

import gov.va.chir.model.ContentHeading;
//import gov.va.chir.model.IndicationsSection;
import gov.va.chir.model.Line;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;




public class IndicationsSectionAnnotator extends JCasAnnotator_ImplBase {
 
 
  
  // -----------------------------------------
  /**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    this.performanceMeter.startCounter();
   
    try {
    int indicationSectionStart = -1;
    int indicationSectionEnd = Integer.MAX_VALUE;
    Annotation indicationLine = null;
    Annotation lastLine = null;
    boolean delimetersFound = false;
    
    // get the lines of the document
    List<Annotation>lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID);
    
    if ( lines != null && !lines.isEmpty()) {
    // iterate thru the lines of the document 
    for ( Annotation line: lines ) {
      // look for a line that starts with "Indications"
      String lineText = line.getCoveredText();
      if ( lineText != null && lineText.length() > 0  ) {
        
       String cleanLine = lineText.trim();
      
       // If you've found an indications header read until you find a line that indicates a new section header
       if ( indicationSectionStart > -1 ) {
          
         if      ( looksLikeEndOfSection( line, lastLine ))  {indicationSectionEnd = line.getEnd();     break; }
         else if ( looksLikeNewSection( line, delimetersFound ))              {indicationSectionEnd = lastLine.getEnd(); break; }
         
       } else {
        
        
        if (cleanLine.startsWith("Indications: ")   || 
            cleanLine.startsWith("Indication: ")    ||
            cleanLine.startsWith("Indications - ")  ||
            cleanLine.startsWith("Indication - ")   ||
            cleanLine.startsWith("Indications ")    ||
            cleanLine.startsWith("Indication ")     ||
            cleanLine.equals("Indications:")        ||
            cleanLine.equals("Indication:")         ||
            cleanLine.equals("Indication"))        {
          indicationSectionStart = line.getBegin(); 
          indicationLine = line;
          }
        
       } // end if a begin section found
      } // end if
      lastLine = line;
    } // end loop thru lines
    } // end if there are lines 
    
    if ( indicationSectionStart > 0 && indicationSectionEnd < Integer.MAX_VALUE ) {
     
      ContentHeading contentHeading = createContentHeading( pJCas, indicationLine);
    //  IndicationsSection indicationSection = createIndicationsSection( pJCas, indicationSectionEnd, contentHeading );
      if ( findDelimiter( indicationLine.getCoveredText()) > 0)
        delimetersFound = true;
      
      // Mark Lines with process me
      markSectionLines(pJCas, contentHeading);
      //markSectionLines(pJCas, indicationSection );
    }
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with finding the section " + e.toString());
      
    }
    this.performanceMeter.stopCounter();

  } // =======================
  
  
  // =======================
  /**
   * markSectionLines marks the lines with processMe
   *
   * @param pJCas
   * @param pSection
   */
  // =======================
  private void markSectionLines(JCas pJCas, Annotation pSection) {
   
    List<Annotation> allLines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID);
    
    
    if ( allLines != null && !allLines.isEmpty()) {
      for (Annotation aLine: allLines )  
        ((Line) aLine).setProcessMe(false);
    
    
      // ------------------------------
      // get lines that cover this section
      List<Annotation> goodLines = UIMAUtil.getAnnotationsBySpan(pJCas, Line.typeIndexID, pSection.getBegin(), pSection.getEnd());
      if ( goodLines != null && !goodLines.isEmpty()) {
        for (Annotation aLine: goodLines )  
          ((Line) aLine).setProcessMe(true);
      }
        
    
    }
    
    
    } // End Method markSectionLines =======
  
  // =======================
  /**
   * createContentHeading [Summary]
   *
   * @param pJCas
   * @param pIndicationLine
   * @return
   */
  // =======================
  private ContentHeading createContentHeading(JCas pJCas, Annotation pIndicationLine) {
   
    ContentHeading contentHeading = null;
    contentHeading = new ContentHeading(pJCas);
    contentHeading.setBegin( pIndicationLine.getBegin());
    int endb = pIndicationLine.getEnd()-1;
    String buff = pIndicationLine.getCoveredText();
    int delimiter1 = buff.indexOf(":");
    int delimiter2 = buff.indexOf(" ");
    if ( delimiter1 > 0) endb = delimiter1;
    else if ( delimiter2 >  0 ) endb = delimiter2 -1;
   
    contentHeading.setId("IndicationsAnnotatorContentHeading_" + this.counter++);
    
    contentHeading.setEnd( contentHeading.getBegin() + endb);
    contentHeading.addToIndexes();
 
    return contentHeading;
  } // End Method createContentHeading =======
  
  /**
   * createIndicationsSection [Summary]
   *
   * @param pJCas
   * @param pSectionBegin
   * @param pSectionEnd
   * @param pContentHeading
   */
  // =======================
//  private IndicationsSection createIndicationsSection(JCas pJCas, int pSectionEnd, ContentHeading pContentHeading) {
//   
//    IndicationsSection section = new IndicationsSection(pJCas);
//    section.setBegin(pContentHeading.getEnd() + 1);
//    section.setEnd( pSectionEnd);
//    section.setId("IndicationSectionAnnotator_" + this.counter++);
//    section.setDisplayString( pContentHeading.getCoveredText());
//    section.setContentHeaderString("Indications");
//    section.setSectionName("Indications");
//    
//    section.setSectionHeading(pContentHeading);
//   
//    section.addToIndexes();
//   
//    
//    
//    return section;
//    } // End Method createIndicationsSection =======
  
  // =======================
  /**
   * looksLikeNewSection Do we see one of the section patterns
   *   delimiter in line +2
   *   No text after delimiter +2
   *   Line starts with Capital Letter +2
   *   Line is less than 40 chars +1
   *   Line includes two periods       -2;
   *   Line includes one period at the end of the line -1
   *   line only has one word (no spaces) +1
   *   line only has two words (one space) +1
   *   Line only includes uppercase letters +1
   *   line has words that are lower case (other than "the, of, and, or, at, from, to") -2  TBD
   *   
   *
   * @param line
   * @return boolean
   */
  // =======================
  private boolean looksLikeNewSection(Annotation pLine, boolean pPriorDelimitersFound) {
   
    boolean returnVal = false;
   
   int score = 0;
   
   if ( pLine == null ) return returnVal;
   String buff = pLine.getCoveredText();
   
   int delimeter = findDelimiter (buff );
   
   if ( delimeter > -1 ) { 
     score+=2;
     String toTheRight = buff.substring(delimeter);
     if ( toTheRight == null || toTheRight.trim().length() == 0 ) score+=2;
   }
   if ( U.isInitialCap( buff.trim() ) ) {
     score+=1;
     if ( pPriorDelimitersFound && delimeter < 1 ) score-=1;
   }
   
   if ( U.allUpperCase(buff) ) score+=2;
   
   if ( buff.length() < 40 ) 
     score+=1; 
   else {
     if ( delimeter > -1) {
       score+=1;
     } else {
       score-=4;
     }
   }
    
   char lastChar = buff.toCharArray()[buff.length()-1];
   if ( lastChar == '.') score-=1;
   
   if ( U.hasMultiplePeriods(buff)) score -=2; 
   
   if ( score > 2) returnVal = true;
   
    return returnVal;
  } // End Method looksLikeNewSection =======
  
//=======================
 /**
  * looksLikeEndOfSection Do we see one of the section patterns
  *   
  *   only punctuation
  *   two or more blank lines
  *   
  *
  * @param pLine
  * @param pLastLine
  * @return boolean
  */
 // =======================
 private boolean looksLikeEndOfSection(Annotation pLine, Annotation pLastLine) {
  
   boolean returnVal = false;
   
  if ( pLine == null  || pLine.getCoveredText().length() == 0 || pLine.getCoveredText().trim().length() == 0 ) {
    if ( pLastLine == null ||  pLastLine.getCoveredText() == null|| pLastLine.getCoveredText().length() == 0 )
      returnVal = true;
  } else {  // pLine is not null
   
    String buff = pLine.getCoveredText().trim();
    if ( U.isOnlyPunctuation(buff) ) 
        returnVal = true;
  } 
  
   return returnVal;
 } // End Method looksLikeEndOfSection =======
 


  // =======================
  /**
   * findDelimiter
   *
   * @param buff
   * @return int (-1 if no delimeter
   */
  // =======================
  private int findDelimiter(String buff) {
    int delimeter = -1;
    delimeter = buff.indexOf(":");
    if ( delimeter == -1 ) 
      delimeter = buff.indexOf("- ");
    
    return delimeter ;
    } // End Method findDelimiter =======
 
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
    

    this.outputDir      = U.getOption(args, "--outputDir=", "./");
    this.performanceMeter = new ProfilePerformanceMeter(args, this.getClass().getCanonicalName()    );
    
   
    
    
  } // end Method initialize() -------
  

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  ProfilePerformanceMeter              performanceMeter = null;
  private String                       outputDir = null;
  private int                   counter = 0;
  
  
  
} // end Class MetaMapClient() ---------------
