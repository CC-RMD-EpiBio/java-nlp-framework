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
 * GU GUI Utilities
 *
 * @author  guy
 * @created Feb 12, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils;

import javax.swing.JOptionPane;

/**
 * @author guy
 *
 */
public class GU {

  // =======================================================
  /**
   * prompt pops up a box with a message, and returns the value
   * from the user's typed in response
   * 
   * @param pMessage
   * @return String
   */
  // =======================================================
  public static String prompt(String pMessage) {
   
    String returnValue = JOptionPane.showInputDialog(pMessage);
    
    return returnValue;
    
  } // End Method prompt() ======================
  

}