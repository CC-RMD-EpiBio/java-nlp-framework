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
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.model.Date;
import gov.nih.cc.rmd.framework.model.Number;
import gov.nih.cc.rmd.nlp.framework.annotator.RedCatAnnotator;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Line;
import gov.va.chir.model.Token;
import gov.va.chir.model.WordToken;
import gov.va.vinci.model.Concept;



public class DateAndTimeAnnotator extends RedCatAnnotator {
 
  public static final String annotatorName = DateAndTimeAnnotator.class.getSimpleName();

  
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    this.performanceMeter.startCounter();
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " Start " + this.getClass().getSimpleName());
    
    try {
         
      datesByRegularExpression(pJCas);
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue in DateAndTime Annotator " + e.toString());
    }
    
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " End " + this.getClass().getSimpleName());
    this.performanceMeter.stopCounter();     
    
  } // end Method process() ----------------
   
  
  
  // =================================================
  /**
   * DatesByRegularExpression runs regular expression across
   * the text of the document
   * 
   * @param pJCas
   * @throws Exception 
  */
  // =================================================
   final void datesByRegularExpression(JCas pJCas) throws Exception {
    
    try {
      
      
    List<Annotation> lineAnnotations = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID );
    
    if ( lineAnnotations != null && !lineAnnotations.isEmpty() ) 
      for ( Annotation line : lineAnnotations) 
        process( pJCas, line );
      
    
    
    // ---------------------------
    // Transform concepts to Dates
    List<Annotation> conceptAnnotations = UIMAUtil.getAnnotations(pJCas, Concept.typeIndexID, false );
    
    if ( conceptAnnotations != null && !conceptAnnotations.isEmpty())
       transformConceptsToDateConcepts ( pJCas, conceptAnnotations );
    
   
   
    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, "issue with processing the date regular expressions " + e.toString() );
      throw e;
    }
    
  } // end Method DatesByRegularExpression() --------



  // =================================================
  /**
   * transformConceptsToDateConcepts 
   * 
   * @param pJCas
   * @param pConcepts
  */
  // =================================================
 private void transformConceptsToDateConcepts(JCas pJCas, List<Annotation> pConcepts) {
   
   if (pConcepts != null && !pConcepts.isEmpty()) {
     for ( Annotation concept: pConcepts ) {
      // if ( !badYear( concept ) ) {
         Annotation date = createDate( pJCas, concept);
      // }
       
       concept.removeFromIndexes(pJCas);
      
     }
   }
     
  } // end Method transformConceptsToDateConcepts() ----


 // =================================================
  /**
   * badYear checks if the year is within the range of 19xx or 20xx
   * if it is, returns false (it's a bad year)
   * 
   * @param pConcept
   * @return boolean
  */
  // =================================================
 private boolean badYear(Annotation pConcept) {
   boolean returnVal = true;
   
   String dateString = pConcept.getCoveredText();
   try {
     int year = getYear( dateString );
    
     if ( ( year >= 1800 && year <= 2071 ))
       returnVal = false;
   } catch (Exception e) {}
   
   return returnVal;
  } // end Method badYear() -------------------



// =================================================
/**
 * getYear returns the year part of the string
 * 
 * @param pDateString
 * @return int
*/
// =================================================
 private int getYear(String pDateString) {
 
   int returnVal = 0;
   String yearString = "";
   
   try {
   // assuming the date is the last part of the string
   if ( pDateString != null && (pDateString.contains("/") || pDateString.contains(".")) && !U.containsLetters(pDateString)) {
     pDateString = pDateString.replace('.', '/');
      yearString = pDateString.substring( pDateString.lastIndexOf("/")+1);
     if ( yearString.length() == 2)
       yearString = "20" + yearString;
     returnVal = Integer.valueOf(yearString);
   } else if ( pDateString != null && !U.containsLetters(pDateString)) 
     returnVal = Integer.valueOf(pDateString);
   else if ( pDateString != null && U.containsLetters(pDateString) && pDateString.lastIndexOf(',') > 0) {
     int lastIndxOfComma = pDateString.lastIndexOf(',');
      yearString = pDateString.substring( lastIndxOfComma + 1).trim();
     returnVal = Integer.valueOf(yearString);
   }
   } catch (Exception e) { 
     // e.printStackTrace();
     GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "getYear ", "Not able to get year from |" + pDateString + "|" + yearString);
     }
   
  return returnVal;
} // end Method getYear() -----------------



//-----------------------------------------
 /**
  * createDate
  * 
  * @param pJCas
  * @param pConcept
 * @return 
  */
 // -----------------------------------------
 Annotation createDate( JCas pJCas, Annotation pConcept ) {
   
   
   Date statement = new Date(pJCas);
   statement.setBegin(                    pConcept.getBegin());
   statement.setEnd(                       pConcept.getEnd());
	 statement.setNegation_Status("Asserted");
   statement.setId( "DateAndTimeAnnotator_" + this.annotationCounter++);
   statement.setSemanticTypes("DateAndTime" );
  
   ArrayList<String> patterns = new ArrayList<String>(1);
   patterns.add( ((Concept) pConcept).getOtherInfo() );
   
   StringArray otherFeatures = UIMAUtil.list2StringArray(pJCas, patterns );
   
   statement.setOtherFeatures( otherFeatures );   
	 statement.addToIndexes();       
	 
	 return statement;
 } // end Method createDate()  -------------
  
 

 /**
  * destroy
  * 
  **/
 public void destroy() {
    if ( this.performanceMeter != null )
      this.performanceMeter.writeProfile( this.getClass().getSimpleName());
 }

 

//=======================================================
/**
* initialize initializes the annotator.  
* @param pContext
*  @throws ResourceInitializationException 
*
*/
//======================================================  
 public void initialize(UimaContext pContext)  throws ResourceInitializationException {
  
     String args[] = null;
    
     try {
       args                 = (String[]) pContext.getConfigParameterValue("args");  
       initialize( args );
       
     } catch (Exception e ) {
       GLog.println( GLog.ERROR_LEVEL,"Issue in DateAndTimeAnnotator " + e.toString());
       throw new ResourceInitializationException();
     }
   
 } // end Method initialize() ------------

 
//----------------------------------
/**
 * initialize loads in the resources needed
 * 
 * @param pArgs
 * 
 **/
// ----------------------------------
public void initialize(String[] pArgs) throws ResourceInitializationException {
  
 
  try {
    
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    String   _manualPositiveRegExPatterns = "resources/vinciNLPFramework/dateAndTime/manualPositiveRegexPatterns.v3.regex";
  
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

} // end Class DateAndTimeAnnotator() ---------------
