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
 * OffsetDesendingComparator compares uima annotations by offset in reverse order
 *   with the largest end offset first 
 *
 *
 * @author  Guy Divita 
 * @created Feb 19, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils.uima;

import java.util.Comparator;

import org.apache.uima.jcas.tcas.Annotation;


public class OffsetDesendingComparator implements Comparator<Annotation>{

  public OffsetDesendingComparator() {
    super();
  }
  
  
  // -----------------------------------------
  /** 
   * compare compares objects by begin offset
   *
   * @param pO1
   * @param pO2
   * @return int 
   */
  // -----------------------------------------
  @Override
  public int compare(Annotation pO1,  Annotation pO2) {
    
    int returnValue = 0;
    
    if ( pO1 == null && pO2 == null )
      returnValue = 0;
    else if ( pO1 == null ) 
      returnValue = -1;
    else if ( pO2 == null )
      returnValue = 1;
    else {
      int b1 = ((Annotation) pO1).getEnd();
      int b2 = ((Annotation) pO2).getEnd();
      returnValue = b2 - b1;
      if ( b2 > b1) returnValue = 1;
      else if ( b2 < b1 ) returnValue = -1;
      else returnValue = 0;
      
      
      
    }
    return returnValue;
  }

  

} // end Class OffsetDesendingComparator() ----------------------
