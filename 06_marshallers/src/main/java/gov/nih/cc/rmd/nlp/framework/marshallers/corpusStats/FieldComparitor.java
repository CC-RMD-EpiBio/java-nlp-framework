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
// =======================================================
/**
 * FieldComparitor.java [Summary here]
 *
 * @author  guy
 * @created Mar 18, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.corpusStats;

import gov.nih.cc.rmd.nlp.framework.utils.U;

import java.util.Comparator;

/**
 * @author guy
 *
 */

public class FieldComparitor implements Comparator<String> {

  private int fieldToSortOn = 0;

  // =======================================================
  /**
   * Constructor FieldComparitor  sorts on this field
   *
   * @param pFieldToSortOn
   */
  // =======================================================
  public FieldComparitor(int pFieldToSortOn) {
    this.fieldToSortOn = pFieldToSortOn;
  }

  /* (non-Javadoc)
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */

  public int compare(String o1, String o2) {
    
    int returnValue = 0;
    String cols1[] = null;
    String cols2[] = null;
    int focusValue1 = 0;
    int focusValue2 = 0;
    
    if ( o1 != null && ((String)o1).length() > 0) { 
     cols1 = U.split((String)o1, CorpusStatsWriter.CSV_DELIMETER);
     String focusString1 = cols1[this.fieldToSortOn];
     focusValue1 = Integer.valueOf(focusString1.trim());
    } else
      focusValue1 = -1;
    
    if ( o2 != null && ((String)o2).length() > 0 ) {
       cols2 = U.split((String)o2, CorpusStatsWriter.CSV_DELIMETER);
       String focusString2 = cols2[this.fieldToSortOn];
     focusValue2 = Integer.valueOf(focusString2.trim());
    } else 
      focusValue2 = -1;
    
    
   
    
    returnValue = focusValue2 - focusValue1;
    
    
    return returnValue;
  } // End Method compare() ======================

 
  

}
