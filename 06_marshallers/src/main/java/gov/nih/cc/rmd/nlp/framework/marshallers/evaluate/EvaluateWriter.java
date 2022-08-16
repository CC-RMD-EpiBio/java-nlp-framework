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
//=================================================
/**
 * EvaluateWriter is a wrapper around the evaluateAnnotator. 
 *
 * @author  Guy Divita 

 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.evaluate;


import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.annotator.EvaluateAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.EvaluateWithAttributeAnnotator;
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;



public class EvaluateWriter extends  AbstractWriter {

  
//-----------------------------------------
 /** 
  * constructor
  * @throws ResourceInitializationException 
  */
 // ---------------------------------------
  public EvaluateWriter( ) throws ResourceInitializationException {
   /* 
    String args[] = null;
    super.initialize( args );
    initializeAux( args);
    */
   
    
  } // end Constructor() -------------------

//-----------------------------------------
 /** 
  * constructor
  * 
  * @param args
 * @throws ResourceInitializationException 
  */
 // ---------------------------------------
  public EvaluateWriter( String[] pArgs) throws ResourceInitializationException {
    
    super.initialize( pArgs );
    initializeAux( pArgs);
   
    
  } // end Constructor() -------------------

  //----------------------------------
  /**
   * initialize loads in the resources 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
    String args[] = null;
    try {
      args                 = (String[]) aContext.getConfigParameterValue("args");  

    } catch (Exception e ) {
      GLog.println(GLog.ERROR_LEVEL,"Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
    

    initialize( args );
 
  } // end initialize() ----------------------------
  
  
// =================================================
/**
 * initializeAux 
 * @param pArgs
 * @throws ResourceInitializationException
*/
// =================================================
   protected void initializeAux(String[] pArgs) throws ResourceInitializationException {
      
     initialize( pArgs);
  
} // End Method initializeAux() -------------------
   
   
   
// =================================================
/**
 * initialize
 * @param pArgs
 * @throws ResourceInitializationException
*/
// =================================================
   public void initialize(String[] pArgs) throws ResourceInitializationException {
      
      try {
        this.evaluator = new gov.nih.cc.rmd.nlp.framework.annotator.EvaluateWithAttributeAnnotator(  pArgs);
      
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, "Issue initializing Evalation Writer " + e.toString());
        throw new ResourceInitializationException();
      }
  
} // end Method initialize() -----------------------

/* (non-Javadoc)
 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
 */
@Override
public void process(JCas pJCas) throws AnalysisEngineProcessException {
 this.evaluator.process( pJCas);
 }


// =================================================
/**
 * report runs the report at the end
 * 
 * @return String
*/
// =================================================
public String report() {
  return this.report();
  
}

/* (non-Javadoc)
 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#destroy()
 */
@Override
public void destroy()  {
 this.evaluator.destroy( );
   // String returnVal = report();
 }



// ---------------------
// Global Variables
// ---------------------
private gov.nih.cc.rmd.nlp.framework.annotator.EvaluateWithAttributeAnnotator evaluator = null;


} // end Class EvaluateWriter
