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
