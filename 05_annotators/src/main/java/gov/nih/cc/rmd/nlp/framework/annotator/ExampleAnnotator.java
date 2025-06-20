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
 * ExampleAnnotator labels lines from text.
 *
 *
 * @author  Guy Divita 
 * @created Jun 22, 2011
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

import gov.va.chir.model.Line;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;



public class ExampleAnnotator extends JCasAnnotator_ImplBase {
 
  public static final String annotatorName = ExampleAnnotator.class.getSimpleName();
  
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
    
    
    String docText = pJCas.getDocumentText();
    if ( docText != null) {
      int newExampleProjectOffsetList[] = U.getNewlineOffsets(docText);
      if ( newExampleProjectOffsetList != null ) {
        int previousEndSpan = 0;
        int beginSpan = 0;
        int i = 0;
        
        // -----------------------------------
        // Loop through the newline offsets 
        for ( ; i < newExampleProjectOffsetList.length; i++ ) {
          beginSpan   = previousEndSpan;
          int endSpan = newExampleProjectOffsetList[i];
       
          createExampleAnnotation( pJCas, beginSpan, endSpan);  // <----- Magic happens here
          
          previousEndSpan = endSpan + 1;
        } // end loop thru newlines of the document
        
        // -----------------------------------------------------------
        // take care of the last line (which might not have a newline)
        if ( previousEndSpan < docText.length()) {
          beginSpan = previousEndSpan;
          int endSpan = docText.length();
          
          createExampleAnnotation( pJCas, beginSpan, endSpan);  
          
        }

        // ----------------------------------------
        // And the really last line of the document (this might be redundant)
        if ( i > 0 &&  newExampleProjectOffsetList[i-1] < docText.length())
          createExampleAnnotation( pJCas, beginSpan, docText.length());
      
      } // end if there are newlines in the document
    } // end if there is any text in the document
  
    
  } // end Method process() ----------------
   
  
  
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
      String exampleLabel = U.getOption(args,  "--exampleLabel=", "fever");
      
      
     
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with getting the passed in arguments " + e.toString());
      throw new ResourceInitializationException();
    
    }
 
  } // end Method initialize() -----------
  
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
  
  Line statement = new Line( pJCas);
   
   
   VUIMAUtil.setProvenance( pJCas, statement, this.getClass().getName() );
   statement.setBegin(                    beginSpan);
   statement.setEnd(                        endSpan);
   
   statement.addToIndexes(pJCas);   // <------------- very important to do this

 } // end Method createEvidence() ---
  
  


  
  
} // end Class ExampleAnnotator() ---------------
