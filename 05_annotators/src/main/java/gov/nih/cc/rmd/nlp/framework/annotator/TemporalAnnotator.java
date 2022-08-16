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
 * TemporalAnnotator annotates the following temporal entities
 *       
 *   AbsoluteTime    +
 *   AbsoluteDate    |   Point in time
 *   RelateTime      |
 *   RelativeDate    +
 *   
 *   Duration        + both an expression and atomic element

 *   Event           +     
 *   Signal          |   atomic elements that make up temporal expressions
 *                   |  
 *   Set             +
 *       
 *
 *
 * @author  Guy Divita 
 * @created Feb 5, 2015
 *
 **   *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;


import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.temporal.AbsoluteDate;
import gov.va.vinci.model.temporal.AbsoluteTime;
import gov.va.vinci.model.temporal.Duration;
import gov.va.vinci.model.temporal.OtherTemporalEntity;
import gov.va.vinci.model.temporal.RelativeDate;
import gov.va.vinci.model.temporal.RelativeTime;
import gov.va.vinci.model.temporal.Set;
import gov.va.vinci.model.temporal.Signal;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class TemporalAnnotator extends JCasAnnotator_ImplBase {
 
  public static final String annotatorName = TemporalAnnotator.class.getSimpleName();
  
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
    
 
    String docText = pJCas.getDocumentText();
    if ( docText != null) {
      
      List<Annotation> tokens = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.Token.typeIndexID );
      for (Annotation token: tokens )  ((VAnnotation)token).setMarked(false);
      
      processTemporalEntity( pJCas, docText, AbsoluteDate.class, this.absoluteDatePattern, tokens);
      processTemporalEntity( pJCas, docText, RelativeDate.class, this.relativeDatePattern, tokens);
      
      processTemporalEntity( pJCas, docText, AbsoluteTime.class, this.absoluteTimePattern, tokens);
      processTemporalEntity( pJCas, docText, RelativeTime.class, this.relativeTimePattern, tokens);
      
      processTemporalEntity( pJCas, docText, Duration.class, this.durationPattern, tokens);
    
      processTemporalEntity( pJCas, docText, Signal.class, this.signalPattern, tokens);
        
      processTemporalEntity( pJCas, docText, Set.class, this.setPattern, tokens);
      
      processTemporalEntity( pJCas, docText, OtherTemporalEntity.class, this.otherPattern, tokens);
       
    
     
    } // end if there is text to be processed
    
  } // end Method process() ----------------
   
  
  
// =======================================================
  /**
   * processTemporalEntity marks annotations from matches
   * fromt the class passed in
   * 
   * @param pJCas
   * @param pDocText
   * @param pTemporalClass
   * @param pTemporalPattern 
   * @param pTokens
   */
  // =======================================================
  private void processTemporalEntity(JCas pJCas, String pDocText, Class<?> pTemporalClass, Pattern pTemporalPattern, List<Annotation> pTokens) {
   
    Matcher matcher = pTemporalPattern.matcher(pDocText);
    List<Annotation>  tokens = null;
  
    // ---------------------
    // Loop thru the matches
    while(matcher.find()) {
      // ---------------------------------------
      // Mark the tokens that cover the pattern
      tokens  = UIMAUtil.getAnnotationsBySpan(pJCas, gov.va.chir.model.Token.typeIndexID , matcher.start(), matcher.end());
      for (Annotation token: tokens )  ((VAnnotation)token).setMarked(true);
    } // end loop through matches
    
    // --------------------
    // Create annotations for the contiguous marked tokens of this type
    createAnnotations(pJCas, pTemporalClass, tokens);
  
  } // End Method processTemporalEntity() ======================
  



  // =======================================================
  /**
   * keepLongestSpanningAnnotations consolidates overlapping
   * annotations
   *   This runs thru the marked tokens, and creates a pTemporalClass instance
   *   from contiguous spans of marked tokens.
   *   
   *   A side effect is that the tokens are marked false at the end 
   *   of this process.
   * 
   * @param pJCas
   * @param pTemporalClass
   * @param pTokens 
   */
  // =======================================================
  private void createAnnotations(JCas pJCas, Class<?> pTemporalClass, List<Annotation> pTokens) {
   
    ArrayList<Annotation> buff = new ArrayList<Annotation>();
    for (Annotation token : pTokens ) {
      if ( ((VAnnotation)token).getMarked()) {
        buff.add( token);
      } else {
        if ( buff != null && buff.size() > 0 ) 
          createAnnotation(pJCas, pTemporalClass, buff );
        buff = new ArrayList<Annotation>();
      }
      ((VAnnotation)token).setMarked(false);
    } // end loop thru the tokens
    
    // do the last set of tokens
    if ( buff != null && buff.size() > 0 )
      createAnnotation(pJCas, pTemporalClass, buff);
  }  // End Method PatterLoadREDRegularExpressions() ======================
  
  

 // =======================================================
   /**
    * createAnnotation 
    * 
    * @param pJCas
    * @param pTemporalClass
    * @param buff
    */
   // =======================================================
   private void createAnnotation(JCas pJCas, Class<?> pTemporalClass, List<Annotation> pBuff) {
    
     int begin = pBuff.get(0).getBegin();
     int   end = pBuff.get( pBuff.size() -1 ).getEnd();
     
     createAnnotation( pJCas, pTemporalClass, begin, end);
     
   } // End Method createAnnotation() ======================
   

  
  
  //-----------------------------------------
  /**
  * createAnnotation creates a clinicalStatement of from the phrase passed in.
  * The phrase is set as the parent of each Evidence
  * 
  * @param pJCas
  * @param uimaLabelClass
  * @param pBegin
  * @param pEnd
  */
  // -----------------------------------------
  private void createAnnotation( JCas pJCas, Class<?> uimaLabelClass, int pBegin, int pEnd) {
  
   
    try {
      Constructor<?> c = uimaLabelClass.getConstructor(new Class[] { JCas.class });
    
      VAnnotation statement = (VAnnotation) c.newInstance(pJCas);
      statement.setBegin(                    pBegin);
      statement.setEnd(                      pEnd);
     
      statement.setId( "TemporalAnnotator_" + this.annotationCounter++);
      statement.addToIndexes(pJCas);
  
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Something went wrong here " + e.toString() );
     
    }
     

  } // End Method keepLongestSpanningAnnotations() ======================
  





//-----------------------------------------
 /**
  * initialize loads in the resources.
  *   Put pipeline parameters in the args parameter
  *   to be retrieved here.
  *
  * @param aContext
  *
  */
 // -----------------------------------------
  public void initialize(UimaContext aContext)  throws ResourceInitializationException {
   
    String args[] = null;
    
    try {
      args                  = (String[]) aContext.getConfigParameterValue("args");
      
    
      @SuppressWarnings("deprecation")
      String resources = U.getClassPathToResources() + "/com/ciitizen/framework";
      String absoluteDateFile = resources + "/dateTime/absoluteDateRegularExpressions.txt";
      String absoluteTimeFile = resources + "/dateTime/absoluteTimeRegularExpressions.txt";
      String relativeDateFile = resources + "/dateTime/relativeDateRegularExpressions.txt";
      String relativeTimeFile = resources + "/dateTime/relativeTimeRegularExpressions.txt";
      String durationFile     = resources + "/dateTime/durationRegularExpressions.txt";
      String signalFile       = resources + "/dateTime/signalRegularExpression.txt";
      String setFile          = resources + "/dateTime/setRegularExpression.txt";
      String otherTemporalFile= resources + "/dateTime/otherTemporalExpression.txt";
      
      
      
      this.absoluteDatePattern = PatternLoadREDRegularExpressions(absoluteDateFile);
      this.absoluteTimePattern = PatternLoadREDRegularExpressions(absoluteTimeFile);
      this.relativeDatePattern = PatternLoadREDRegularExpressions(relativeDateFile);
      this.relativeTimePattern = PatternLoadREDRegularExpressions(relativeTimeFile);
      this.durationPattern     = PatternLoadREDRegularExpressions(durationFile);
      this.signalPattern       = PatternLoadREDRegularExpressions(signalFile);
      this.setPattern          = PatternLoadREDRegularExpressions(setFile);
      this.otherPattern        = PatternLoadREDRegularExpressions(otherTemporalFile);
      
      
     
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with getting the passed in arguments " + e.toString());
      throw new ResourceInitializationException();
    
    }
 
  } // end Method initialize() -----------
  
// =======================================================
/**
 * PatternLoadREDRegularExpressions reads in the list of regular expressions
 * and turns them into one compiled pattern
 * 
 * @param pRegularExpressionPatternFile  containing regular expressions
 * @return Pattern a compiled pattern
 * @throws Exception 
 */
// =======================================================
private Pattern PatternLoadREDRegularExpressions(String pRegularExpressionPatternFile) throws Exception {
 
  String regularExpressionString = U.readFile( pRegularExpressionPatternFile );
  
  Pattern pattern = Pattern.compile( regularExpressionString );
  return pattern;
  
}// End Method PatterLoadREDRegularExpressions() ======================





// -------------------------
 // Global Variables
 private Pattern absoluteDatePattern = null;
 private Pattern absoluteTimePattern = null; 
 private Pattern relativeDatePattern = null; 
 private Pattern relativeTimePattern = null; 
 private Pattern durationPattern = null;
 private Pattern signalPattern = null;
 private Pattern setPattern = null;
 private Pattern eventPattern = null;
 private Pattern otherPattern = null;
 private int annotationCounter = 0;
 

  
  
} // end Class ExampleAnnotator() ---------------
