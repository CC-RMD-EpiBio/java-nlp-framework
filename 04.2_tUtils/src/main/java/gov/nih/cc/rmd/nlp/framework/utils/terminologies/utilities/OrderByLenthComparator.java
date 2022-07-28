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
 * OrderByLenthComparator compares LexRecords by the number of tokens each has
 *
 * @author Guy Divita
 * @created Mar 5, 2011
 *
*/
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities;


import java.util.Comparator;

  public class OrderByLenthComparator implements Comparator<LexRecord>{ 


    @Override
    public int compare(LexRecord arg0, LexRecord arg1) {
      
      int noOfATokens = 0;
      int noOfBTokens = 0;
      int   returnVal = 0;
      
      if ( arg0 != null ) {
        String aTokens[] = arg0.getKey().split(" ");
        noOfATokens = aTokens.length;
      }
      if ( arg1 != null) {
        String bTokens[] = arg1.getKey().split(" ");
        noOfBTokens = bTokens.length;
      }
      returnVal = noOfBTokens - noOfATokens;
           
      return returnVal;
  
    } // end Method compare() ------------------------
} // end Class OrderByLengthComparator ---------------
