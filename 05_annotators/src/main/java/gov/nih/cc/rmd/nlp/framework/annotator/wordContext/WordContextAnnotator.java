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
 * ExampleProjectAnnotator labels lines from text.
 *
 *
 * @author  Guy Divita 
 * @created Jun 22, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.wordContext;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.VAnnotation;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class WordContextAnnotator extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  /**
   * process adds a contextLeft and contexRight attributes to each label of focus found.
   * The context is window (char) sized.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    String docText = pJCas.getDocumentText();
    int docTextLength = docText.length();
    String contextLeft = null;
    String contextRight = null;
    List<Annotation>  foci = null;
    
    try {  
      foci = UIMAUtil.getAnnotations(pJCas, this.label);
    } catch ( Exception e) {
      e.printStackTrace();
      System.err.println( "Something went wrong in the WordContextAnnotator " + e.getMessage()) ;
      throw new AnalysisEngineProcessException();
    }
    
    if ( foci != null )
      for ( Annotation focus : foci ) {
        
        // ------------------------
        // Get the x number of characters to the left of the focus
        int beginLeft = focus.getBegin() - this.window ;
        if ( beginLeft < 0)
          beginLeft = 0;
        contextLeft = docText.substring(beginLeft, focus.getBegin());
        
        // ------------------------
        // get the x number of characters to the right of the focus
        
        int endRight = focus.getEnd() +1  + this.window;
        if ( endRight > docTextLength ) 
          endRight = docTextLength;
        if ( focus.getEnd() + 1 < docTextLength )
           contextRight = docText.substring(focus.getEnd()+1, endRight);
        // -------------------------
        // add context attributes to the annotations
        ((VAnnotation) focus).setContextLeft( contextLeft);
        ((VAnnotation) focus).setContextRight( contextRight);
      
      } // end loop through focus of foci
   
  
  } // end Method process() ----------------
   
  


  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   *   We need a window and a label
   * @param aContext
   *   
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
    if (aContext != null)
      super.initialize(aContext);
  
    String windowString = null;
    try {
      if ( aContext != null ) {
        windowString = (String) aContext.getConfigParameterValue("contextWindow"); // <---- check to see if I can do this
      this.window = Integer.parseInt(windowString);
      this.label =  (String)  aContext.getConfigParameterValue("contextLabel");
      }
    } catch( Exception e) {
      e.printStackTrace();
      System.err.println("Issue initializing the wordContext annotator " + e.toString());
      throw new ResourceInitializationException(e);
    }
      
  } // end Method initialize() -------
  
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
 
  private String   label = "VAnnotation";
  private int     window = 10;
  
  
  
  
} // end Class ExampleProjectAnnotator() ---------------
