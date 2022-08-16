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
