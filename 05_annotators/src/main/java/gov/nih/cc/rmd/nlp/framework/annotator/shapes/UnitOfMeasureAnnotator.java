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
 * TermShapeAnnotator discovers Shape's from a combination
 * of other shapes and other looked up terms
 * 
 * numberRange
 * units of measure combined with numbers
 * 
 * This is a fourth iteration of this functionality
 * 
 * I'm limiting the scope to finding patterns within
 * a line.
 * 
 * 
 *
 *
 * @author Guy Divita
 * @created April 19, 2019
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.shapes;

import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.model.NumberRange;
import gov.nih.cc.rmd.framework.model.Shape;
import gov.nih.cc.rmd.framework.model.UnitOfMeasure;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.CodedEntry;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Line;
import gov.va.chir.model.WordToken;

public class UnitOfMeasureAnnotator extends JCasAnnotator_ImplBase {

  // -----------------------------------------
  /**
   * process retrieves lines of the document
   * looks for patterns with the line
   * 
   * Sometimes a line will include columns in tables
   * Keep the window to look in within a column.
   * Columns are delimited by tabs.
   * 
   * The tension here is that the more I make separate
   * patterns, the slower this annotator becomes.
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
      this.performanceMeter.startCounter();

      List<Annotation> terms = UIMAUtil.getAnnotations(pJCas,LexicalElement.typeIndexID );
      
      if ( terms != null && terms.size() > 0) {
        
        // ------------------------------
        // Retrieve just those codedEntries that are T099 (Family Member)
        
        for ( Annotation term: terms ) {
         
          String semanticTypes = ((LexicalElement) term ).getSemanticTypes() ;
          
            if (semanticTypes != null &&  semanticTypes.contains("UnitOfMeasure")) {
               createUnitOfMeasure(pJCas, term);
            
               // look to the left one token to see if it's a number
               // if so, make the number + the unit of measure into a lexical element that is a unit of measure
               if ( !U.isNumber(term.getCoveredText().trim())) {
                 Annotation wordToLeft = getWordToLeft( pJCas, term );
              
                 if ( wordToLeft != null &&  U.isNumber( wordToLeft.getCoveredText() ) )
                   createUnitOfMeasureTerm( pJCas, wordToLeft, term );
              
                 else {
              
                   // if there is no number to the left, look to the right for a number 
                   Annotation wordToRight = getWordToRight( pJCas, term );
                   if ( wordToRight != null &&  U.isNumber( wordToRight.getCoveredText() ))
                     createUnitOfMeasureTerm( pJCas, wordToRight, term );
                
             
                 }
               }
            }
                
        }
      } 
    

      
      
      
      
      
    } catch (

    Exception e) {
      e.printStackTrace();
      String msg = "Issue with one of the shapes " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
    }
    this.performanceMeter.stopCounter();
    
  } // end Method process() ----------------

  

  // =================================================
  /**
   * getWordToLeft returns the WordToken to the left of this annotation
   * 
   * @param pJCas
   * @param pTerm
   * @return Annotation 
  */
  // =================================================
  public static Annotation getWordToLeft(JCas pJCas, Annotation pTerm) {
    
    Annotation returnVal = null;
    
    int position1 = pTerm.getBegin() -2;
    int position2 = position1 + 1;
    List<Annotation> tokens = null;
    
    while ( tokens == null || tokens.isEmpty() && position1 >= 0) {
      tokens = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, WordToken.typeIndexID,  position1, position2, false);
      position1--;
      position2 = position1 + 1;
    }
    if ( tokens != null && !tokens.isEmpty())
      returnVal = tokens.get(0);
      
    
    return returnVal;
  } // end getWordToLeft() ---------------------------


  // =================================================
  /**
   * getWordToRight returns the WordToken to the right of this annotation
   * 
   * @param pJCas
   * @param pTerm
   * @return Annotation 
  */
  // =================================================
  public static Annotation getWordToRight(JCas pJCas, Annotation pTerm) {
    
    Annotation returnVal = null;
    
    int position1 = pTerm.getEnd() +1;
    int position2 = position1 + 1;
    List<Annotation> tokens = null;
    int lastWord = pJCas.getDocumentText().length();
    
    while ( tokens == null || tokens.isEmpty() && position1 < lastWord) {
      tokens = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, WordToken.typeIndexID,  position1, position2, false);
      position1++;
      position2 = position1 + 1;
    }
    if ( tokens != null && !tokens.isEmpty())
      returnVal = tokens.get(0);
      
    
    return returnVal;
  } // end getWordToRight() ---------------------------

  
  

  // -----------------------------------------
  /**
   * createUnitOfMeasure 
   *
   * @param pJCas
   * @param pTerm
   *     
   */
  // -----------------------------------------
  private final void createUnitOfMeasure(JCas pJCas, Annotation pTerm) {

    try {
      UnitOfMeasure statement = new UnitOfMeasure( pJCas);

     statement.setBegin(pTerm.getBegin());
     statement.setEnd(pTerm.getEnd());
     statement.setId("UnitOfMeasure_" + annotationCounter++);
     statement.addToIndexes(pJCas);

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createUnitOfMeasure", "Something went wrong here " + e.toString());
    }

  } // end Method createUnitOfMeasure() ---
  
  
  // -----------------------------------------
  /**
   * createUnitOfMeasure 
   *
   * @param pJCas
   * @param pTerm
   *     
   */
  // -----------------------------------------
  private final void createUnitOfMeasureTerm(JCas pJCas, Annotation pWord1, Annotation pWord2) {

    try {
      LexicalElement statement = new LexicalElement( pJCas);
      
     int beginWord = Math.min( pWord1.getBegin(), pWord2.getBegin() );
     int endWord = Math.max( pWord1.getEnd(),  pWord2.getEnd() );

     statement.setBegin( beginWord );
     statement.setEnd(endWord);
     statement.setId("UnitOfMeasureTerm_" + annotationCounter++);
     
     statement.setSemanticTypes("unitOfMeasure");
     
     statement.addToIndexes(pJCas);
     
     
     
     

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createUnitOfMeasure", "Something went wrong here " + e.toString());
    }

  } // end Method createUnitOfMeasure() ---

  // ----------------------------------
  /**
   * destroy.
   */
  // ----------------------------------
  @Override
  public void destroy() {
    this.performanceMeter.writeProfile(this.getClass().getSimpleName());
  }

  // ----------------------------------
  /**
   * initialize loads in the resources.
   * 
   * @param pContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext pContext) throws ResourceInitializationException {

    String args[] = null;

    try {

      args = (String[]) pContext.getConfigParameterValue("args");

    } catch (Exception e) {

    }
    initialize(args);

  } // end Method initialize() -------

  // ----------------------------------
  /**
   * initialize loads in the resources.
   * 
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {

    this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getSimpleName());

  } // end Method initialize() -------

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------

  protected int                   annotationCounter = 0;   // new Term Counter.
  private ProfilePerformanceMeter performanceMeter  = null;

} // end Class UnitOfMeasureAnnotator() ---------------
