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
 * OnTheFlyAnnotator labels terms from text.
 *
 *
 * @author  Guy Divita 
 * @created July 26, 2016
 *
 **   *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.LexicalElement;
import gov.va.vinci.model.SearchTerm;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class OnTheFlyAnnotator extends JCasAnnotator_ImplBase {
 
  
//=======================================================
/**
* process
* 
* @param pJCas
* @throws AnalysisEngineProcessException
*
*/
// =======================================================
public void process(JCas pJCas) throws AnalysisEngineProcessException {

  this.performanceMeter.startCounter();
  
  String id = VUIMAUtil.getDocumentId(pJCas);
  
  try {

    // --------------------------------------------------------------
    // create Negation, Assertion, Historical, Subject Annotations
    List<Annotation> searchTerms = UIMAUtil.getAnnotations(pJCas, LexicalElement.typeIndexID );  

    if ( searchTerms != null && !searchTerms.isEmpty())
      for ( Annotation term : searchTerms ) {
       
        String categories = ((LexicalElement) term).getSemanticTypes();
        if ( categories.contains("SearchTerm")) {
          createSearchTerm( pJCas, term );
        
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with the assertion annotator " + e.toString());
    
    }
    this.performanceMeter.startCounter();
   
  }
   /**
    * createConceptAnnotation creates a Concept
    * See the type.decriptor project gov.va.chir.Model.xml for the 
    * the definition of what a Concept Type is.
    * 
    * @param pJCas
    * @param pSearchTerm
    * 
    * 
    */
   private void createSearchTerm( JCas pJCas,  Annotation pSearchTerm )  {
    
     SearchTerm term = new SearchTerm( pJCas);
     term.setBegin(  pSearchTerm.getBegin());
     term.setEnd(    pSearchTerm.getEnd());
     term.setCategories("SearchTerm");
     term.setId("OnTheFly_" + this.termId++);
     term.addToIndexes(pJCas);   // <------------- very important to do this
     
     System.out.println(" found a term " + term.getCoveredText());
     
   } // end Method createEvidence() --------------------

   

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
  *      Pass in on your args, an arg with the value: "--outputDir=/some/path/to/put/outputFiles"
  *
  *   This class is tooled up to have performance statisitcs get put into $outputDir/logs/Class.log  
  *
  * @param aContext
  *
  */
 // -----------------------------------------
  public void initialize(UimaContext aContext)  throws ResourceInitializationException {
   
    String args[] = null;
    
    try {
      args                  = (String[]) aContext.getConfigParameterValue("args");
      this.outputDir        = U.getOption(args, "--outputDir=", "./");
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
   private int                             termId = 0;


  
  
} // end Class onTheFlyAnnotator() ---------------
