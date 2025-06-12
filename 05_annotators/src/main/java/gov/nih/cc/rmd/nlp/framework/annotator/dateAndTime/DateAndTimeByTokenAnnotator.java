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

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.model.Date;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.WordToken;
import gov.va.vinci.model.temporal.AbsoluteDate;



public class DateAndTimeByTokenAnnotator extends JCasAnnotator_ImplBase  {
 
  public static final String annotatorName = DateAndTimeByTokenAnnotator.class.getSimpleName();

  
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * DatesByToken Look for word tokens with numbers in them
   * see if they look like dates.  These are meant to catch
   * dates that are missed by regular expression
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    this.performanceMeter.startCounter();
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " Start " + this.getClass().getSimpleName());
    
    try {
         
      
      List<Annotation> numbers = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.framework.model.PotentialNumber.typeIndexID );
      
      if ( numbers != null && !numbers.isEmpty() ) {
        
        for ( Annotation aNumber : numbers ) {
          String buff = aNumber.getCoveredText();
          List<Annotation> aWordTokenz = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, WordToken.typeIndexID, aNumber.getBegin(), aNumber.getEnd() );
          if ( aWordTokenz != null && !aWordTokenz.isEmpty() ) {
            WordToken aWordToken = (WordToken) aWordTokenz.get(0);
            if ( !aWordToken.getMarked() &&
               //  !containsDate ( pJCas, aWordToken ) &&
                 isADate( pJCas, aWordToken ) ) {
              
                  createDate( pJCas, aWordToken );
                  aWordToken.setMarked(true);
              
            }
         }
        }
        
        
      }
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue in DateAndTime Annotator " + e.toString());
    }
    
  
    
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " End " + this.getClass().getSimpleName());
    
    this.performanceMeter.stopCounter();     
    
  } // end Method process() ----------------
   
  
 

  // =================================================
  /**
   * containsDate 
   * 
   * @param pJCas
   * @param aWordToken
   * @return boolean
  */
  // =================================================
  private final boolean containsDate(JCas pJCas, WordToken pWordToken) {
    
    boolean returnVal = false;
    
    List<Annotation> dates = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Date.typeIndexID,  pWordToken.getBegin(), pWordToken.getEnd() );
    if ( dates != null && !dates.isEmpty())
      returnVal = true;
    
    return returnVal;
  } // end Method containsDate() ---------------------


  // =================================================
  /**
   * isADate 
   * 
   * @param pJCas
   * @param aWordToken
   * @return boolean
  */
  // =================================================
 private final boolean isADate(JCas pJCas, WordToken aWordToken) {
    
   boolean returnVal = false;
   
   String buff = aWordToken.getCoveredText();
   
   // strip off non number letters at the end of the buff
   StringBuffer newBuff =  new StringBuffer();
   for ( int i = 0; i < buff.length(); i++ ) {
     if ( Character.isDigit(buff.charAt(i)) ||buff.charAt(i)== '/' )
       newBuff.append(buff.charAt(i));
   }
   buff = newBuff.toString();
   
 
   String all_in = "(((1[0-2]|0?[1-9]|[01]|[12][0-9]|0?[1-9])\\/(?:[0-9]{2})?[0-9]{2})|)";
  
   String months =  "January|February|March|April|May|June|July|August|September|October|November|December|Jan|Feb|Mar|Apr|Jun|Jul|Aug|Sept|Oct|Nov|Dec";                                 

   String days =  "Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday|Mon|Tue|Tues|Wed|Thur|Thurs|Fri|Sat|Sun";                                 

   
   if ( buff.matches(all_in)) {
     returnVal = true;
   }
   
   else if ( buff.matches(months)) {
     returnVal = true;
   }
   else if ( buff.matches(days)) {
     returnVal = true;
   }
   
   
   return returnVal;
   
  } // end Method isDate() ---------------------------


  

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
       Annotation date = createDate( pJCas, concept);
       concept.removeFromIndexes(pJCas);
      
     }
   }
     
  } // end Method transformConceptsToDateConcepts() ----


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
   
   
   AbsoluteDate statement = new AbsoluteDate(pJCas);
   statement.setBegin(                    pConcept.getBegin());
   statement.setEnd(                       pConcept.getEnd());
   statement.setPii(true);
	 statement.setNegation_Status("Asserted");
   statement.setId( "DateAndTimeByTokenAnnotator_" + this.annotationCtr++);
   statement.setSemanticTypes("DateAndTime:Date:PII" );
  
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
  
   
  } catch (Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, "Issue initializing the dateAndTimeAnnotator " + e.toString() );
    throw new ResourceInitializationException();
  }
  
} // end Method initialize()


 // ---------------------------
 // Global Variables
 // ---------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  

} // end Class DateAndTimeAnnotator() ---------------
