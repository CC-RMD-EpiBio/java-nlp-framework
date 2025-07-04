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
/**
 * 
 */
package gov.nih.cc.rmd.nlp.framework.annotator;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * @author Guy
 *
 */
public class AnnotationContainer {

  private JCas jcas;
  private Annotation annotation;

  // ==========================================
  /**
   * AnnotationContainer [Summary]
   *
   * @param pJCas
   * @param pAnnotation
   */
  // ==========================================
  public AnnotationContainer(JCas pJCas, Annotation pAnnotation) {
   this.jcas = pJCas;
   this.annotation = pAnnotation;
  } // end Constructor ==========================================

  // ==========================================
  /**
   * getJcas 
   *
   * @return jcas
   */
  // ==========================================
  public final JCas getJcas() {
    return this.jcas;
  }

  // ==========================================
  /**
   * setJcas 
   *
   * @param jcas the jcas to set
   */
  // ==========================================
  public final void setJcas(JCas jcas) {
    this.jcas = jcas;
  }

  // ==========================================
  /**
   * getAnnotation 
   *
   * @return annotation
   */
  // ==========================================
  public final Annotation getAnnotation() {
    return this.annotation;
  }

  // ==========================================
  /**
   * setAnnotation 
   *
   * @param annotation the annotation to set
   */
  // ==========================================
  public final void setAnnotation(Annotation annotation) {
    this.annotation = annotation;
  }
  

} // end Class AnnotationContainer() ---------
