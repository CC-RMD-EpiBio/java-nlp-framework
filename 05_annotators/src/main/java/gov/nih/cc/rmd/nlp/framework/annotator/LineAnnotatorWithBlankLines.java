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
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.acronym.Acronyms;



public class LineAnnotatorWithBlankLines extends JCasAnnotator_ImplBase  {
 

  // -----------------------------------------
  /**
   * process retrieves lines of the document, labels those that are questions
   * as QuestionAndAnswer elements.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    
    this.performanceMeter.startCounter();
    try {
   
      String docText = pJCas.getDocumentText();
      int offset = 0;
    
      if ( docText != null && !docText.isEmpty()) {
    
        String[] rows = U.split(docText, "\n");
      
        for ( int i = 0; i < rows.length; i++ ) {
          String row = rows[i];
          int lineLength = row.length();
          int indententation = getIndentation( row);
          boolean capitalization = getCapitalization( row );
        
          createLine( pJCas, offset, offset + lineLength, indententation, capitalization );
        
          offset = offset + row.length() + 1 ;   // +1 for newline that got stripped by split  
  
        } // end loop thru rows
      } // end if no text
         
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
      //   throw new AnalysisEngineProcessException();
    }
    
    this.performanceMeter.stopCounter();
    
  } // end Method process() ----------------
   
  
//-----------------------------------------
 /**
  * createQuestionAnnotation
  * 
  * @param pJCas
  * @param pBeginSpan
  * @param pEndSpan
  * @param pIndentation
  * @param pCapitalization
  * 
  */
 // -----------------------------------------
 private void createLine( JCas pJCas,  int beginSpan, int endSpan, int pIndentation, boolean pCapitalization )  {
  
  Line statement = new Line( pJCas);
   
  statement.setBegin(                    beginSpan);
  statement.setEnd(                      endSpan);
  statement.setId("LineAnnotatorWithBlankLines_" + this.lineCtr++);
  statement.setIndentation(  pIndentation  );
  statement.setCapitalized(  pCapitalization);
  statement.setNegation_Status("Asserted");

  statement.addToIndexes(pJCas);
  

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
      
    
    initialize( args);
      
  } // end Method initialize() -------
  

  //----------------------------------
  /**
   * initialize 
   *
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String pArgs[]) throws ResourceInitializationException {

    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    
  } // end Method initialize() -------
   
  

  
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int lineCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  private String  outputDir = null;
  
} // end Class LineAnnotator() ---------------
