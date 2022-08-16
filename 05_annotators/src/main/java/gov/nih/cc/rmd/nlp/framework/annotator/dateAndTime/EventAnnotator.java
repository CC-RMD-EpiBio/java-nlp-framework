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

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.vinci.model.Concept;
import gov.va.vinci.model.temporal.Event;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;



public class EventAnnotator extends DateAndTimeAnnotator {
 
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
    
    
    Event statement = new Event(pJCas);
    statement.setBegin(                    pConcept.getBegin());
    statement.setEnd(                       pConcept.getEnd());
    statement.setAssertionStatus( "Asserted");
    statement.setId( "EventAnnotator_" + this.annotationCounter++);
  
    String category = "Date"; 
   
    try {
      category =  ((Concept)pConcept).getCategories();
      
    } catch (Exception e) { }; // not a concept coming in.
    
    statement.setOtherInfo(category);
    
    statement.setCategories("Date" );
    statement.addToIndexes(pJCas);
    
    return statement;
  } // end Method createDate()  -------------
   
     
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
    String   _manualPositiveRegExPatterns =  "resources/com/ciitizen/framework/dateAndTime/eventRegularExpressions.txt";
    
    
    this.manualPositiveRegExPatterns      = loadRegularExpressions( _manualPositiveRegExPatterns);
    
    
    
  } catch (Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, "Issue initializing the EventAnnotator " + e.toString() );
    throw new ResourceInitializationException();
  }
} // end Method initialize() --
  

 // ---------------------------
 // Global Variables
 // ---------------------------
 private int annotationCounter = 0;

} // end Class ExampleAnnotator() ---------------
