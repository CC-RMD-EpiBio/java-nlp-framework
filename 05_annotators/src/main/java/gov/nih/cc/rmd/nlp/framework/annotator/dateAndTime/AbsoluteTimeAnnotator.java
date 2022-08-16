// =================================================
/**
 * DateAndTimeAnnotator identifies date and time parts
 *
 * @author  Guy Divita 
 * @created Jan 08 2018
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime;

import java.util.ArrayList;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.vinci.model.Concept;
import gov.va.vinci.model.temporal.AbsoluteTime;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class AbsoluteTimeAnnotator extends DateAndTimeAnnotator {
 
 
  //-----------------------------------------
  /**
   * createDate
   * 
   * @param pJCas
   * @param pConcept
   */
  // -----------------------------------------
  @Override
   Annotation createDate( JCas pJCas, Annotation pConcept ) {
    
    
    AbsoluteTime statement = new AbsoluteTime(pJCas);
    statement.setBegin(                    pConcept.getBegin());
    statement.setEnd(                       pConcept.getEnd());
    statement.setNegation_Status( "Asserted");
    statement.setId( "AbsoluteTimeAnnotator_" + this.annotationCounter++);
    statement.setSemanticTypes("Time" );
    statement.addToIndexes(pJCas);
    
    ArrayList<String> patterns = new ArrayList<String>(1);
    patterns.add( ((Concept) pConcept).getOtherInfo() );
    
    StringArray otherFeatures = UIMAUtil.list2StringArray(pJCas, patterns );
    statement.setOtherFeatures( otherFeatures);
    
    return statement;
  } // end Method createDate()  -------------
   
     
  
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   * 
   */
  // -----------------------------------------
  @Override
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    super.process( pJCas);
  }
  
//----------------------------------
/**
 * initialize loads in the resources needed
 * 
 * @param pArgs
 * 
 **/
// ----------------------------------
  @Override
public void initialize(String[] pArgs) throws ResourceInitializationException {
  
 
  try {
    
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    String   _manualPositiveRegExPatterns =  "resources/vinciNLPFramework/dateAndTime/manualAbsoluteTimeExpressions.regex";
    
    
    this.manualPositiveRegExPatterns      = loadRegularExpressions( _manualPositiveRegExPatterns);
    

    
    
  } catch (Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, "Issue initializing the dateAndTimeAnnotator " + e.toString() );
    throw new ResourceInitializationException();
  }
  

  
} // end Method initialize()






 // ---------------------------
 // Global Variables
 // ---------------------------

 private int annotationCounter = 0;

} // end Class ExampleAnnotator() ---------------
