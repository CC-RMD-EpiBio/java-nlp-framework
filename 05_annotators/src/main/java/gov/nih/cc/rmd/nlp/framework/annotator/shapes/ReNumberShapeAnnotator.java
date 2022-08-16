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
 * ReNumberShapeAnnotator converts PotentialNumbers back into Numbers
 * provided that they do not overlap with lexicalElements that were
 * from a dictionary
 * 
 *  
 *
 * @author  Guy Divita 
 * @created May 6, 2021
 
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

import gov.nih.cc.rmd.framework.model.Number;
import gov.nih.cc.rmd.framework.model.PotentialNumber;
import gov.nih.cc.rmd.framework.model.Shape;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.LexicalElement;



public class ReNumberShapeAnnotator extends JCasAnnotator_ImplBase {
 
  
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

      List<Annotation> potentialNumbers = UIMAUtil.getAnnotations( pJCas, PotentialNumber.typeIndexID); 
   
      if ( potentialNumbers != null && !potentialNumbers.isEmpty()) 
      
        for ( Annotation potentialNumber: potentialNumbers ) {
          
          if ( !overlapsWithExistingTerm( pJCas, potentialNumber.getBegin(), potentialNumber.getEnd()) ) {
              Number aNumber = createNumber( pJCas, potentialNumber);
             
          } 
          // potentialNumber.removeFromIndexes();
         
        }
        
     
     
  } catch (
    
      Exception e) {
        e.printStackTrace();
        String msg = "Issue with reNumber " + e.toString();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
      }
        
    
  
  } // end Method process() ----------------
   
  
  

  // =================================================
  /**
   * createNumber transforms a potentialNumber into a number
   * 
   * @param pJCas
   * @param potentialNumber
   * @return Number
  */
  // =================================================
  private final Number createNumber(JCas pJCas, Annotation pPotentialNumber) {
   
    
      Number statement = new Number( pJCas);

      statement.setBegin(pPotentialNumber.getBegin());
      statement.setEnd(pPotentialNumber.getEnd());
      statement.setId( this.getClass().getCanonicalName() + ":createNumber_" + annotationCounter++);
      statement.addToIndexes(pJCas);

      return statement;
    
  } // end Mehtod createNumber() ---------------------




  // =================================================
/**
 * overlapsWithExistingTerm returns true if
 * this span overlaps with another lexical element
 * 
 * @param pJCas
 * @param pBegin
 * @param pBen
 * @return boolean
*/
// =================================================
private final  boolean overlapsWithExistingTerm(JCas pJCas, int pBegin, int pEnd) {
  
  boolean returnVal = false;
  
  List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  LexicalElement.typeIndexID, pBegin,  pEnd);
  
  if ( terms != null && !terms.isEmpty()) 
    for ( Annotation term : terms ) {
      String buff = term.getCoveredText();
     
      StringArray euis = ((LexicalElement)term).getEuis();
      String semanticTypes = ((LexicalElement)term).getSemanticTypes();
      if (( euis == null || euis.size() == 0 || euis.get(0).contains("unknown")) && (semanticTypes == null )) {
         term.removeFromIndexes();
         break;
      } else {
        returnVal = true;
        break;
      }
    }
    
  return returnVal;
} // end Method notOverlappingWithExistingTerm() ---------




// ----------------------------------
/**
* destroy.
*/
// ----------------------------------
@Override
public void destroy() {
  this.performanceMeter.writeProfile(this.getClass().getSimpleName());
}

  //----------------------------------
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
    
      args                 = (String[]) pContext.getConfigParameterValue("args");  
      initialize( args );
      
    } catch (Exception e ) {
      GLog.println( GLog.ERROR_LEVEL,"Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
    
    
  } // end Method initialize() -------
  
  
  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
   
    
  } // end Method initialize() -------
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
 
  protected int annotationCounter = 0; // new Term Counter.
  private ProfilePerformanceMeter performanceMeter = null;
  
  
  
} // end Class RegExShapeAnnotator() ---------------
