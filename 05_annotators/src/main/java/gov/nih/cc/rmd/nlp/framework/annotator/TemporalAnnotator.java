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
