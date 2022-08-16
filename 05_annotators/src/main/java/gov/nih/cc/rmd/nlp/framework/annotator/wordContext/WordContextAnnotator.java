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
