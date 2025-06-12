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
