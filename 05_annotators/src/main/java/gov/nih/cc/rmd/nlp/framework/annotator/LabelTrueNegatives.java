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
 * LabelTrueNegatives creates true negative annotations from 
 * named labels that are not asserted. 
 * 
 *
 * 
 * @author Guy Divita
 * @created June 8, 2016
 * 
 *  
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

import gov.va.chir.model.TrueNegative;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class LabelTrueNegatives extends JCasAnnotator_ImplBase {
  
  @Override
  // -----------------------------------------
  /**
   * process takes in a cas that's filled with annotations
   * of one label, and transforms them to a cas with 
   * other labels.
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
    
    // ---------------------------------------------
    // Iterate through each of the (original) labels

    try {

    List<Annotation> annotations = UIMAUtil.getAnnotations(pJCas);
    
    for (Annotation anAnnotation : annotations) {

      String annotationLabel = anAnnotation.getClass().getSimpleName();
      
      
      for ( String label: this.origLabels) {
        if ( annotationLabel.equals( label )) {
        
            //System.err.println("Looking at |" + anAnnotation.getBegin() + "|" + anAnnotation.getEnd() + "|" + anAnnotation.getCoveredText() + "|"  + anAnnotation.getType().getName() + "|" );
           
            String assertionStatus = VUIMAUtil.getAssertionStatus(pJCas, anAnnotation);
            
            if ( assertionStatus == null || !assertionStatus.equals("Asserted")) {
              // Consider this a true negative
              createTrueNegativeAnnotation(pJCas, anAnnotation );
              anAnnotation.removeFromIndexes(pJCas);
            }
        }            
           
      } 
    } // end if there are any annotations
    
  } catch (Exception e) {
        e.printStackTrace();
        String msg = "Issue with relabel " + e.toString();
        System.err.println(msg);
        
        throw new RuntimeException(msg);
      }
    
    
  } // end Method process() --------------------------
  
// -----------------------------------------
  /**
   * createTrueNegativeAnnotation c
   * 
   * @param pJCas
   * @param anAnnotation
   *
   */
  // -----------------------------------------
  private void createTrueNegativeAnnotation(JCas pJCas, Annotation anAnnotation ) {

    // ----------------------------------------------
    // create a UIMA annotation from this label
    // ----------------------------------------------
    try {
              
      TrueNegative statement = new TrueNegative(pJCas);
     
      // --------------------------------------------------
      // Add span and slot attributes to the uimaAnnotation
      // --------------------------------------------------
  
    
      statement.setBegin(anAnnotation.getBegin());
      statement.setEnd(anAnnotation.getEnd());
      statement.addToIndexes(pJCas);
    } catch (Exception e) {
      e.getStackTrace();
      System.err.println("issue creating a true negative " + e.toString());
      
    }
      
      
    
  } // end Method createAnnotation

//----------------------------------
  /**
   * initialize pulls in the pairs of label|relabel to work with
   *
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
   
    String args[] = null;
    try {
      args = (String[]) aContext.getConfigParameterValue("args");

      initialize( args);
     
    } catch (Exception e) {
     
      e.printStackTrace();
      String msg = "Issue getting a parameter " + e.toString();
      System.err.println(msg);
      throw new ResourceInitializationException();
    }
  
  } // end Method initialize() --------------

  
  // ==========================================
  /**
   * initialize extracts two parameters from 
   * the string[] args:  
   *   --potentialLabels=aaa:bbb:ccc:ddd
   *   
   *   colon delimited labels to turn into true negatives when instances of these
   *   are found that are not asserted.
   *                     
   *
   * @param pArgs
   */
  // ==========================================
  public void initialize(String[] pArgs) {
    
     String potentialLabels = U.getOption(pArgs,  "--potentialLabels=","Concept:ClinicalStatement");
    
    
     this.origLabels = U.split(potentialLabels, ":" );
 
   
  } // end Method initialize() ================
  



  // ---------------------------------------------
  // Class Variables
  // ---------------------------------------------
  private String[]                    origLabels = null;

 
} // end Class RelabelAnnotator --------------------------
