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
 * BiGram is a container holding a bi-Gram
 *
 * @author  Divita
 * @created Sep 19, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils.nGrams;

import gov.nih.cc.rmd.nlp.framework.utils.U;


public class BiGram {

  // =======================================================
  /**
   * Constructor BiGram creates a biGram from the ngram index
   * line which looks like
   *
   * @param line
   */
  // =======================================================
  public BiGram(String line) {
    
    String[] cols = U.split(line);
    String key = cols[0].trim().toLowerCase();
   String uniGrams[] = U.split(key, " ");
   if ( uniGrams.length == 1) { 
     String t = uniGrams[0];
     uniGrams = new String[2];
     this.leftGram = t;
     rightGram = " ";
     
   } else {
     this.leftGram = uniGrams[0];
     this.rightGram = uniGrams[1];
   }
     
   this.frequency = cols[1];
  }
  // -----------------------
  // Global Variables
  // -----------------------
  public String leftGram = null;
  public String rightGram = null;
  public String frequency = null;
  // =======================================================
  /**
   * getLeftGram 
   * 
   * @return String
   */
  // =======================================================
  public String getLeftGram() {
   return this.leftGram;
  }  // End Method getLeftGram() ======================
  
  
  // =======================================================
  /**
   * getRightGram 
   * 
   * @return String
   */
  // =======================================================
  public String getRightGram() {
   return this.rightGram;
  } // End Method getRightGram() ======================


  // =======================================================
  /**
   * getFrequency 
   * 
   * @return
   */
  // =======================================================
  public String getFrequency() {
   return this.frequency;
  } // End Method getFrequency() ======================
  
  
  
} // end Class BiGram
