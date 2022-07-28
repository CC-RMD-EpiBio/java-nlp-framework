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
import gov.va.chir.model.VAnnotation;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class CheckBoxAnnotator extends JCasAnnotator_ImplBase {
  
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
         if (!this.useProcessMe || ( this.useProcessMe && ((VAnnotation)line).getProcessMe() )) {
          // ------------------------
          // look for begin brackets
          String row = line.getCoveredText();
      
          int offset = line.getBegin();
          if ( row != null && row.length() > 0 ) {
            char bracketOpen = findBracket(row);
            int beginBracket = row.indexOf(bracketOpen);
            
            if ( beginBracket > -1) {
              char bracketClose = matchingBracket(bracketOpen);
              int endBracket = row.lastIndexOf( bracketClose);
              if ( endBracket > -1 && endBracket > beginBracket )  {  // got a [  ] 
                // ---------------------------
                // peek to the left
                // peek to the right
                // peek inside the brackets
                
                String  leftContext = row.substring(0, beginBracket).trim();
                String rightContext = null; if ( endBracket + 1 < row.length() ) rightContext = row.substring(endBracket + 1);
                String      content = null; if ( beginBracket + 1 < endBracket )      content = row.substring(beginBracket + 1, endBracket );
                
                // -------------------
                // if both the left and right contexts are not empty, this is not likely a slot value
                // Make the insides of the bracket the content
                //
                // if the left context is empty but the right context is not, 
                //         make the right context contentHeader for a slot 
                // if the right context is empty but the left context is not, 
                //         make the left context contentHeader for a slot
                //
                // -------------------
                String contentHeaderString = null;
                if ( leftContext != null && leftContext.trim().length() > 0 ) 
                  contentHeaderString = leftContext.trim();
                
                if ( rightContext != null && rightContext.trim().length() > 0  && contentHeaderString == null )
                  contentHeaderString = rightContext.trim();
                
                if (isDependentContentACheckKindOfValue(content)) {
                
                  int contentHeaderBegin = -1;
                  int contentHeaderEnd = -1;
                  int contentBegin = -1;
                  int contentEnd = -1;
                  if (contentHeaderString != null) {
                    contentHeaderBegin = row.indexOf(contentHeaderString);
                    contentHeaderEnd = contentHeaderBegin + contentHeaderString.length();
                    SlotValue slotValue = null;
                    if (content != null && content.length() > 0) {
                      contentBegin = beginBracket + 1;
                      contentEnd = contentBegin + content.length() ;
                      slotValue = createSlotValue(pJCas, contentHeaderBegin + offset, contentHeaderEnd + offset, contentBegin
                          + offset, contentEnd + offset, beginBracket + offset, endBracket + offset);
                    } else { // end if the content is empty or not
                      slotValue = createSlotValue2(pJCas, contentHeaderBegin + offset, contentHeaderEnd + offset, beginBracket
                          + offset, endBracket + offset);
                    } // end if there is no content but a slot value

                    // ---------------------
                    // Calculate the assertion status --- no longer done here - done in assertion module properly
                    // this.slotValueAnnotator.assertQuestionAnswer(pJCas, slotValue);
                  }    
                } // end if there is a content header 
                
                
              } //end if we seen the pattern [ ] 
                
              
            }
      
          } // end if the row isn't empty
         } // end process me 
        } // end if the line isn't null 
        
        } //end loop thru lines
      
    } //end if there are  lines
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
     //  throw new AnalysisEngineProcessException();
    }
    
  } // end Method process
      
      
      
 
  
  // =======================================================
  /**
   * isDependentContentACheckKindOfValue returns true if the
   * content is
   *   one single character (not a number)
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
    else if ( checkBoxValue.trim().length() == 1  &&  !U.isNumber(checkBoxValue)  ) 
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
    else if ( row.indexOf('(') > -1 && !row.toLowerCase().contains("(s)") )
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
   * @param contentHeaderBegin
   * @param contentHeaderEnd
   * @param contentBegin
   * @param contentEnd
   * @return SlotValue
   */
  // =======================================================
  private SlotValue createSlotValue(JCas pJCas, int contentHeaderBegin, int contentHeaderEnd, int contentBegin, int contentEnd, int beginBracket, int endBracket) {
   
    // -------------------
    // Create the contentHeader 
     ContentHeading contentHeading = createContentHeading( pJCas, contentHeaderBegin, contentHeaderEnd);
    
    // -------------------
    // create the dependentContent
     DependentContent dependentContent = createDependentContent(pJCas, contentBegin, contentEnd);
    
     
     int slotBegin = contentHeaderBegin;
     int slotEnd   = endBracket;
     if ( slotBegin > contentEnd) { 
       slotBegin = beginBracket;
       slotEnd   = contentHeaderEnd;
     }
     
     // -------------------------
     // The brackets are on the right side
     if ( slotBegin > slotEnd ){
       int tmp = slotBegin;
       slotBegin = slotEnd;
       slotEnd = tmp;
     }
     
    // -------------------
    // create the slotValue
     SlotValue slotValue = new SlotValue(pJCas);
     slotValue.setBegin(slotBegin);
     slotValue.setEnd( slotEnd);
     slotValue.setId("CheckBoxSlotValue");
     slotValue.setDependentContent(dependentContent);
     slotValue.setContentHeaderString(contentHeading.getCoveredText());
     slotValue.setContentString(dependentContent.getCoveredText());
     slotValue.setHeading(contentHeading);
  
     slotValue.addToIndexes();
     contentHeading.setParent(slotValue);
     dependentContent.setParent(slotValue);
     
     return slotValue;
    
  } // End Method createSlotValue() ======================
  



  
  // =======================================================
  /**
   * createSlotValue creates a slotvalue, contentHeader, and dependent content annotations
   * 
   * @param pJCas
   * @param contentHeaderBegin
   * @param contentHeaderEnd
   * @param contentBegin
   * @param contentEnd
   * @return SlotValue
   */
  // =======================================================
  private SlotValue createSlotValue2(JCas pJCas, int contentHeaderBegin, int contentHeaderEnd,  int beginBracket, int endBracket) {
   
    // -------------------
    // Create the contentHeader 
     ContentHeading contentHeading = createContentHeading( pJCas, contentHeaderBegin, contentHeaderEnd);
    
  
     
     int slotBegin = contentHeaderBegin;
     int slotEnd   = endBracket;
     if ( slotBegin > endBracket) { 
       slotBegin = beginBracket;
       slotEnd   = contentHeaderEnd;
     }
     
    // -------------------
    // create the slotValue
     SlotValue slotValue = new SlotValue(pJCas);
     slotValue.setBegin(slotBegin);
     slotValue.setEnd( slotEnd);
     slotValue.setHeading(contentHeading);
     slotValue.setContentHeaderString( contentHeading.getCoveredText());
     slotValue.setId("CheckBoxSlotValue2");
     slotValue.addToIndexes();
     contentHeading.setParent(slotValue);
    
     
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
    dependentContent.setId("CheckBoxDependentContent");
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
    contentHeading.setId("CheckBoxContentHeading");
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
      
      initialize( args );   
      
    } catch (Exception e ) {
      System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
    

    
    
  
  } // end Method initialize() ---------

  //----------------------------------
    /**
     * initialize loads in the resources needed for slotValues. Currently, this involves
     * a list of known slot names that might show up.
     *
     * @param pArgs
     * 
     **/
    // ----------------------------------
    public void initialize(String pArgs[]) throws ResourceInitializationException {
      
      this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
      
      this.useProcessMe = Boolean.parseBoolean(U.getOption(pArgs,  "--useProcessMe=", "false"));
    
  
  } // end Method initialize() --------------
  

 
  // -----------------------------------------
  // class Variables
  // -----------------------------------------
    SlotValueAnnotator slotValueAnnotator = null;
    private ProfilePerformanceMeter performanceMeter = null;
    private boolean useProcessMe = false;


  
} // end Class SlotValue ---------------------
