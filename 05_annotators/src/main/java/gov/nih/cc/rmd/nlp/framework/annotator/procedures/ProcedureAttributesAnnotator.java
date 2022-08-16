// =================================================
/**
 * ProceduresAttributes assigns event date and diagnosis
 * to the procedure if either were in the window
 * where the procedure was found
 *
 * @author  Guy Divita 
 * @created Jun 1, 2018
 *
 * 
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.procedures;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.Diagnosis;
import gov.nih.cc.rmd.framework.Procedure;

import gov.va.vinci.model.temporal.AbsoluteDate;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class ProcedureAttributesAnnotator extends JCasAnnotator_ImplBase  {
 

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
    
    // iterate through the clinical statements
    
    List<Annotation> procedures = UIMAUtil.getAnnotations(pJCas, Procedure.typeIndexID );
    
    if ( procedures != null && !procedures.isEmpty() ) 
      for ( Annotation procedure : procedures ) {
         processProcedure( pJCas, procedure);
      }
   
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  } // end Method process() ----------------
   
  


 // =================================================
   /**
    * processProcedure looks for a date and a diagnosis
    * within the lines around the procedure
    * 
    * @param pJCas
    * @param pProcedure
    *
   */
   // =================================================
  private final void processProcedure( JCas pJCas, Annotation pProcedure) {
  
    // get three lines (including the line the procedure is on ) as the window
   
     List<Annotation> lines = VUIMAUtil.getLines(pJCas, pProcedure, 3);
     
     if ( lines != null && !lines.isEmpty()) {
       int beginOffset = pProcedure.getEnd();
       int endOffset = lines.get(lines.size() -1).getEnd();
       
       
       List<Annotation> diagnoses = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Diagnosis.typeIndexID, beginOffset, endOffset);
       if ( diagnoses != null && !diagnoses.isEmpty()) {
         StringBuffer diagnosisBuff = new StringBuffer();
         for ( Annotation diagnosis : diagnoses ) {
      
           String diagnosisString = ((Diagnosis) diagnosis).getOtherInfo();  
           if ( diagnosisString != null ) {
             // C0003962|Ascites|<noun>|<all>|Ascites|Ascites|fndg:dsrd|SNOMEDCT_US|389026000|n|0|P|PF|20|27|0.0|true|null:
             String cols[] = U.split( diagnosisString);
             diagnosisString = diagnosis.getCoveredText() + ":" + cols[8] + ":" + cols[7];
             diagnosisBuff.append( diagnosisString + "|");
           }
         }
         String diagnsisString = diagnosisBuff.toString().substring(0, diagnosisBuff.length() -1);    
             ((Procedure) pProcedure).setDiagnosis( diagnsisString);
         }
         
       
       List<Annotation> dates = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, AbsoluteDate.typeIndexID, beginOffset, endOffset);
       if ( dates != null && !dates.isEmpty()) 
          ((Procedure) pProcedure).setEventDate(  dates.get(0).getCoveredText());
       
       
    }
  
    
   } // end Method processProcedure() -----------------

  
 

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
   *  initialize    This is the standard uima way to pass parameters to an annotator.
   *                It is cumbersome.  It requires creating a config file with params
   *                in it, making it difficult to dynamically pass in parameters. 
   *                
   *                This method merges the uima way and keeping the ability to dynamically
   *                pass parameters into the class via - putting all parameters in a string
   *                array called "args" with each row containing a --key=value format.
   *                This way, arguments could be directly passed from command line,
   *                or read from a config file, or dynamically added to that string
   *                passed in. 
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   * 
   * @param aContext
   * @throws ResourceInitializationException
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
     
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  

        initialize(args);
        
      } catch (Exception e ) {
        String msg = "Issue in initializing class " + this.getClass().getName() + " " + e.toString() ;
        GLog.println(GLog.ERROR_LEVEL, msg);     // <------ use your own logging here
        throw new ResourceInitializationException();
      }
      
  
  } // end Method initialize() -------
  
  //----------------------------------
  /**
   * initialize initializes the class.  Parameters are passed in via a String
   *                array  with each row containing a --key=value format.
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
      
  } // end Method initialize() -------
  
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  
} // end Class ProceduresAnnotator() ---------------
