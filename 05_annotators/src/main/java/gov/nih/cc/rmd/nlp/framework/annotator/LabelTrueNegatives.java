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
