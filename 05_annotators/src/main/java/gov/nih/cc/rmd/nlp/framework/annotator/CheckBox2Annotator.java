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
 * CheckBox annotator looks for patterns that are of the form
 *   [ x ] smoking
 *   (-) pain
 *   (+) depression
 *   
 *   Finding the brackets is necessary
 *   Finding that there is one character or no characters between the 
 *   brackets is needed to not confuse (03/02/2015) parenthical expressions
 *   from being marked as checkboxes.
 *   
 * @author  Guy Divita 
 * @created August 25, 2014
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.Line;
import gov.va.chir.model.SlotValue;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class CheckBox2Annotator extends JCasAnnotator_ImplBase {
  
  @Override
  // -----------------------------------------
  /**
   * process finds check boxes from lines of documents
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
    this.performanceMeter.startCounter();
    
    // Loop thru lines
    List<Annotation> lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID);
    
    if ( lines != null ) {
    
      for ( Annotation line : lines ) {
        
        if ( line != null  ) {
         
          // ------------------------
          // look for begin brackets
          String row = line.getCoveredText();
      
       
          String question = null;
          String answer   = null;
          
          // ----------------------------
          // Look for empty check box  ______Question ____ Question    Question______ Question ____  
          
          answer = findBlankAnswer(row);
          if ( answer != null ) {
            question = findQuestion( row , answer);
            
          } else {
          // ---------------------------
          // Look for filled in check box  _Y_Question _N_ Question   Question_N_   Question _Y_
          //   System.err.println(row);
          answer = findAnswer( row);
          if ( answer != null )
            question = findQuestion( row, answer);
          }
          if ( question != null )
            createSlotValue(pJCas, line, question, answer);
          
          
                
        } // end if the line isn't null
        
        } //end loop thru lines
      
    } //end if there are  lines
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
     // throw new AnalysisEngineProcessException();
    }
  } // end Method process
      
      
      
 
  
  // =======================================================
  /**
   * findQuestion returns the part of the line that is not 
   * the question
   *      answer  Question
   *      Question answer
   * @param pLine
   * @param pAnswer
   * @return String
   */
  // =======================================================
  private String findQuestion(String pLine, String pAnswer) {
    String question = null;
    
    int beginPattern = pLine.indexOf(pAnswer);
    int endPattern   = beginPattern + pAnswer.length();
    
    String leftSide = null;
    String rightSide = null;
    
    if ( beginPattern > 0 )
      leftSide  = pLine.substring(0, beginPattern);
   
    if ( endPattern < pLine.length())
      rightSide = pLine.substring(endPattern);
    
    if ( leftSide != null )  leftSide = leftSide.trim();
    if ( rightSide != null )  rightSide = rightSide.trim();
    
    if ( leftSide != null  )
      if ( rightSide != null )
        if ( leftSide.length() > rightSide.length())  
          question = leftSide;
        else
          question = rightSide;
      else
        question = leftSide;
    else
      question = rightSide;
    
    return question;
  } // End Method findQuestion() ======================
  





  // =======================================================
  /**
   * findBlankAnswer looks for the pattern of multiple
   * under bars (but not the whole line) 
   * 
   * @param line
   * @return String
   */
  // =======================================================
  private String findBlankAnswer(String pLine) {
    String blankAnswer = null;
    
    
    
    Matcher matcher = blankAnswerpattern.matcher( pLine );
    
    if ( matcher.groupCount() == 1 ) {
      blankAnswer = matcher.group();

      // --------------------
      // Just check to see that the ___ is not the whole line
      if ( blankAnswer.length() == pLine.trim().length() )
        blankAnswer = null;
    }
    
    
    return blankAnswer;
  } // End Method findBlankAnswer() ======================
  

  
  // =======================================================
  /**
   * findAnswer looks for the pattern of multiple
   * under bars __Y__
   * 
   * @param line
   * @return String
   */
  // =======================================================
  private String findAnswer(String pLine) {
    String answer = null;
    
   
    Matcher matcher = answerPattern.matcher( pLine );
    
    if ( matcher.groupCount() == 1 ) 
      answer = matcher.group();
    
    return answer;
  } // End Method findAnswer() ======================
  







  // =======================================================
  /**
   * isDependentContentACheckKindOfValue returns true if the
   * content is
   *   one single character
   *   a yes/no value
   *   a true/false value
   *   empty
   *   
   * 
   * @param contentHeaderString
   * @return boolean
   */
  // =======================================================
  private boolean isDependentContentACheckKindOfValue(String checkBoxValue) {
    boolean returnVal = false;
    
    if      ( checkBoxValue == null )
      returnVal = true;
    else if ( checkBoxValue.trim().length() == 0  )
      returnVal = true;
    else if ( checkBoxValue.trim().length() == 1 )
      returnVal = true;
    else if ( checkBoxValue.toLowerCase().equals("yes")) 
      returnVal = true;
    else if ( checkBoxValue.toLowerCase().equals("no"))
      returnVal = true;
    else if ( checkBoxValue.toLowerCase().equals("yes/no"))
      returnVal = true;
    else if ( checkBoxValue.toLowerCase().equals("true"))
      returnVal = true;
    else if ( checkBoxValue.toLowerCase().equals("false"))
      returnVal = true;
    else if ( checkBoxValue.toLowerCase().equals("true/false"))
      returnVal = true;
    
      return returnVal;
  } // End Method isDependentContentACheckKindOfValue() ======================
  





  // =======================================================
  /**
   * matchingBracket returns a ] to a begin bracket of [
   * 
   * @param bracketOpen
   * @return char
   */
  // =======================================================
  private char matchingBracket(char bracketOpen) {
   
    char returnVal = ']';
    
    if      ( bracketOpen == '[')   returnVal = ']';
    else if ( bracketOpen == '(')   returnVal = ')';
    else if ( bracketOpen == '_')   returnVal = '_';
    
    return returnVal;
  } // End Method matchingBracket() ======================
  





  // =======================================================
  /**
   * findBracket finds the first instance of a [ or ( or _
   * 
   * @param row
   * @return char
   */
  // =======================================================
  private char findBracket(String row) {
    
    char returnVal = '_';
    if ( row.indexOf('[') > -1 )
      returnVal = '[';
    else if ( row.indexOf('(') > -1)
      returnVal = '(';
    else if ( row.indexOf( '_') > -1)
      returnVal = '_';
      
    
    return returnVal;
  } // End Method findBracket() ======================
  





  // =======================================================
  /**
   * createSlotValue creates a slotvalue, contentHeader, and dependent content annotations
   * 
   * @param pJCas
   * @param pLine
   * @param pQuestion
   * @param pAnswer
   * @return SlotValue
   */
  // =======================================================
  private SlotValue createSlotValue(JCas pJCas, Annotation pLine, String pQuestion, String pAnswer ) {
 
    
    int contentHeaderBegin = 0;
    int contentHeaderEnd = 0;
    
    String row = pLine.getCoveredText();
    contentHeaderBegin = row.indexOf(pQuestion) + pLine.getBegin();
    contentHeaderEnd = contentHeaderBegin + pQuestion.length();
    
    
    // -------------------
    // Create the contentHeader 
     ContentHeading contentHeading = createContentHeading( pJCas, contentHeaderBegin, contentHeaderEnd);
    
    int contentBegin = 0;
    int contentEnd = 0;
    contentBegin = row.indexOf(pAnswer);
    contentEnd = contentBegin + pAnswer.length();
    // -------------------
    // create the dependentContent
     DependentContent dependentContent = createDependentContent(pJCas, contentBegin, contentEnd);
    
     
     int slotBegin = contentHeaderBegin;
     int slotEnd   = contentEnd;
     if ( slotBegin > contentEnd) { 
       slotBegin = contentBegin;
       slotEnd   = contentHeaderEnd;
     }
     
     
    // -------------------
    // create the slotValue
     SlotValue slotValue = new SlotValue(pJCas);
     slotValue.setBegin(slotBegin);
     slotValue.setEnd( slotEnd);
     slotValue.setId("CheckBox2SlotValue");
     slotValue.setDependentContent(dependentContent);
     slotValue.setContentString(contentHeading.getCoveredText());
     slotValue.setContentString(dependentContent.getCoveredText());
     slotValue.setHeading(contentHeading);
     slotValue.addToIndexes();
     contentHeading.setParent(slotValue);
     dependentContent.setParent(slotValue);
     
     return slotValue;
    
  } // End Method createSlotValue() ======================
  






  // =======================================================
  /**
   * createDependentContent 
   * 
   * @param pJCas
   * @param contentBegin
   * @param contentEnd
   * @return DependentContent
   */
  // =======================================================
  private DependentContent createDependentContent(JCas pJCas, int contentBegin, int contentEnd) {
    DependentContent dependentContent = new DependentContent( pJCas);
    dependentContent.setBegin(contentBegin);
    dependentContent.setEnd( contentEnd);
    dependentContent.setId("CheckBox2DependentContent");
    dependentContent.addToIndexes();
    
    return dependentContent;
    // End Method createDependentContent() ======================
  }





  // =======================================================
  /**
   * createContentHeading creates a contentHeading
   * 
   * @param pJCas
   * @param contentHeaderBegin
   * @param contentHeaderEnd
   * @return ContentHeading
   */
  // =======================================================
  private ContentHeading createContentHeading(JCas pJCas, int contentHeaderBegin, int contentHeaderEnd) {
    
    ContentHeading contentHeading = new ContentHeading( pJCas);
    contentHeading.setBegin(contentHeaderBegin);
    contentHeading.setEnd( contentHeaderEnd);
    contentHeading.setId("CheckBox2ContentHeading");
    contentHeading.addToIndexes();
    
    return contentHeading;
  } // End Method createContentHeading() ======================
  



  
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
     
      this.performanceMeter = new ProfilePerformanceMeter( args, this.getClass().getSimpleName() );
      
      this.blankAnswerpattern =   Pattern.compile("_{2,}");
      this.answerPattern =  Pattern.compile("_+\\w+_+");

      
    } catch (Exception e ) {
      e.printStackTrace();
      
      System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
    

    
    
    initialize();
  } // end Method initialize() ---------

  //----------------------------------
    /**
     * initialize loads in the resources needed for slotValues. Currently, this involves
     * a list of known slot names that might show up.
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize() throws ResourceInitializationException {
      
    this.slotValueAnnotator = new SlotValueAnnotator();
    
  
  } // end Method initialize() --------------
  

 
  // -----------------------------------------
  // class Variables
  // -----------------------------------------
    SlotValueAnnotator slotValueAnnotator = null;
  
    private ProfilePerformanceMeter performanceMeter = null;
    private Pattern blankAnswerpattern =  null; 
    private Pattern answerPattern =  null; 


  
} // end Class SlotValue ---------------------
