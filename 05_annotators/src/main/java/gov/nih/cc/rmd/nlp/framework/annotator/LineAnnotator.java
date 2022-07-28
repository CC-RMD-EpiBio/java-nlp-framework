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
 * LineAnnotator labels lines from text.
 *
 *
 * @author  Guy Divita 
 * @created Jun 22, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.Line;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;



public class LineAnnotator extends JCasAnnotator_ImplBase  {
 

  // -----------------------------------------
  /**
   * process retrieves lines of the document, labels those that are questions
   * as QuestionAndAnswer elements.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
    this.performanceMeter.startCounter();
    String docText = pJCas.getDocumentText();
   
    if ( docText != null) {
      int newLineOffsetList[] = U.getNewlineOffsets(docText);
      if ( newLineOffsetList != null ) {
        int previousEndSpan = 0;
        int beginSpan = 0;
        int i = 0;
        
        // -----------------------------------------------
        // Loop through the newlines 
        for ( ; i < newLineOffsetList.length; i++ ) {
          beginSpan   = previousEndSpan;
          int endSpan = newLineOffsetList[i];
          createLine( pJCas, beginSpan, endSpan);
          
          previousEndSpan = endSpan + 1;
        } // end loop thru newlines of the document
        
        // -------------------------------------------------
        // take care of the last line (which might not have a newline)
        if ( previousEndSpan < docText.length()) {
          beginSpan = previousEndSpan;
          int endSpan = docText.length();
          createLine( pJCas, beginSpan, endSpan);
        }

        if ( i > 0 &&  newLineOffsetList[i-1] < docText.length())
          createLine( pJCas, beginSpan, docText.length());
      
        
      }
    } // end loop thru line
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  } // end Method process() ----------------
   
  
//-----------------------------------------
 /**
  * createQuestionAnnotation
  * 
  * @param pJCas
  * @param pPhrase
  * 
  * @return Evidence
  */
 // -----------------------------------------
 private void createLine( JCas pJCas,  int beginSpan, int endSpan )  {
  
  Line statement = new Line( pJCas);
   
  statement.setBegin(                    beginSpan);
  statement.setEnd(                      endSpan);
  statement.setId("LineAnnotator_" + this.lineCtr++);
   

  statement.addToIndexes(pJCas);
  
  String text = statement.getCoveredText();
  int indentation = getIndentation( text);
  boolean capitalization = getCapitalization( text);
  
  statement.setIndentation(  indentation  );
  statement.setCapitalized( capitalization);

 } // end Method createEvidence() ---
  
// =================================================
/**
 * getIndentation returns the number of characters 
 * starting from the left before the first non whitespace
 * character
 * 
 * This will return -1 for blank lines;
 * @param pText
 * @return int
*/
// =================================================
private final int getIndentation(String pText) {
  int returnVal = 0;
  
  char[] buff = pText.toCharArray();
  int i = 0;
  for ( ; i < buff.length; i++ ) {

    if ( buff[i] >= '!') {
      returnVal = i;
      break;
    }
  }
  if ( i >= buff.length -1 )
    returnVal = -1;
  
  return returnVal;
} // end Method getIndentation() ------------------


// =================================================
/**
 * getCapitalization  returns true if the first character
 * has been capitalized.
 * 
 * This will also return true if the first character is
 * a number or a *
 * 
 * @param pText
 * @return boolean
*/
// =================================================
private boolean getCapitalization(String pText) {
  
  boolean returnVal = false;
  
  if ( pText != null && !pText.isEmpty() && pText.trim().length() > 0 ) {
    String buff = pText.trim();
    if ( buff != null ) {
      char c = buff.charAt(0) ;
  
      if      ( c == '*' )            returnVal = true;
      else if ( Character.isDigit(c)) returnVal = true;
      else if ( Character.isLetter(c) && !Character.isLowerCase(c)) returnVal = true; 
    }
  }
  
  return returnVal;
} // End Method getCaptitalization() -------------


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
      

      this.performanceMeter = new ProfilePerformanceMeter( args, this.getClass().getSimpleName() );
      
    
    initialize();
      
  } // end Method initialize() -------
  
  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * 
   **/
  // ----------------------------------
  public void initialize() throws ResourceInitializationException {
       
   
      
  } // end Method initialize() -------
  
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int lineCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  private String  outputDir = null;
  
} // end Class LineAnnotator() ---------------
