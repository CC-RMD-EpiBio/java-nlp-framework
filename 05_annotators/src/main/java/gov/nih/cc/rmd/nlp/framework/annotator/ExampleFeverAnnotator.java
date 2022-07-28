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
 * ExampleFeverAnnotator labels direct fever mentions
 *
 *
 * @author  Guy Divita 
 * @created Dec 10, 2014
 *
 **   *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;


import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.vinci.model.Fever;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;



public class ExampleFeverAnnotator extends JCasAnnotator_ImplBase {
 
  public static final String annotatorName = ExampleFeverAnnotator.class.getSimpleName();
  
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

    String docText = pJCas.getDocumentText().toLowerCase();
    if ( docText != null) {
     
      int  lastSeen = -1;
      int lastMatch = lastSeen;
      while ((lastSeen = docText.indexOf("fever", lastSeen)) > -1 && lastSeen != lastMatch ) {
        createExampleAnnotation( pJCas, lastSeen, lastSeen + 5);  // <----- Magic happens here
        lastMatch = lastSeen;
        
      }
      
     
    } // end if there is any text in the document
  
    this.performanceMeter.stopCounter();
    
  } // end Method process() ----------------
   
  
//-----------------------------------------
 /**
  * createExampleAnnotation creates lineAnnotatons
  * 
  * See the type.decriptor project gov.va.chir.Model.xml for the 
  * the definition of what a Line Type is.
  * 
  * @param pJCas
  * @param beginSpan
  * @param endSpan
  * 
  */
 // -----------------------------------------
 private void createExampleAnnotation( JCas pJCas,  int beginSpan, int endSpan )  {
  
  Fever statement = new Fever( pJCas);
   
   statement.setBegin(                    beginSpan);
   statement.setEnd(                        endSpan);
   
   statement.addToIndexes(pJCas);   // <------------- very important to do this

 } // end Method createExampleAnnotation() ---
  
  
  //----------------------------------
  /**
   * destroy
   * 
   **/
  // ----------------------------------
  public void destroy() {
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
  }

 
//-----------------------------------------
/**
 * initialize loads in the resources.
 *   Put pipeline parameters in the args parameter
 *   to be retrieved here.
 *
 * @param aContext
 *
 */
// -----------------------------------------
 public void initialize(UimaContext aContext)  throws ResourceInitializationException {
  
   String args[] = null;
   
   try {
     args                  = (String[]) aContext.getConfigParameterValue("args");
     
     
     // ------------------------------------------------------------
     // Example parameter passed from the pipeline to this annotator.
     //    The args string array contains --key=value elements. The 
     //    U.getOption() method traverses thru the args to find the
     //    --key=  and parse the element to retrieve the value for 
     //    this key.
     // -------------------------------------------------------------
     @SuppressWarnings("unused")
    String exampleLabel = U.getOption(args,  "--exampleLabel=", "fever");

    this.outputDir      = U.getOption(args, "--outputDir=", "./");
    this.performanceMeter = new PerformanceMeter( this.outputDir + "/logs/profile_" + this.getClass().getSimpleName() + ".log"  );
     
     
    
   } catch (Exception e) {
     e.printStackTrace();
     System.err.println("Issue with getting the passed in arguments " + e.toString());
     throw new ResourceInitializationException();
   
   }

 } // end Method initialize() -----------

   // ---------------------------
   // Global Variables
   // ---------------------------
   PerformanceMeter              performanceMeter = null;
   private String                       outputDir = null;


} // end Class ExampleFeverAnnotator() ---------------
