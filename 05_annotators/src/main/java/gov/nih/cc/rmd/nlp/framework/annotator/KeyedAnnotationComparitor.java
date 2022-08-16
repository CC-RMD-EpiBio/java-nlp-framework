// ------------------------------------------------------------
/**
 * KeyedAnnotationComparitor Summary 
 *
 *
 * @author guy
 * Apr 8, 2013
 * 
 * ------------------------------------------------------------
 *
 *
 *
 * -------------------------------------------------------------
 */
// -------------------------------------------------------------
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.Comparator;

import org.apache.uima.jcas.tcas.Annotation;


// -----------------------------------
/**
 * Class Types
 *
 */
// ------------------------------------
public class KeyedAnnotationComparitor implements Comparator {

  /* (non-Javadoc)
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(Object o1, Object o2) {
  
    int returnValue = 0;
    int begin1 = -1;
    int begin2 = -1;
    
    if ( o1 != null)
      begin1 = ((Annotation) o1).getBegin();
   
    if ( o2 != null)
      begin2 = ((Annotation) o2).getBegin();
    
    returnValue = begin2 - begin1; 
    
    if ( returnValue > 0)
        returnValue = 1;
    
    return returnValue;
    
    
  }  // End Method compare() -----------------------

 
  

  // End KeyedAnnotationComparitor Class -------------------------------
}
