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
