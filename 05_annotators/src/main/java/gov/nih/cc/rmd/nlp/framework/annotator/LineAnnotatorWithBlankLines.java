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
