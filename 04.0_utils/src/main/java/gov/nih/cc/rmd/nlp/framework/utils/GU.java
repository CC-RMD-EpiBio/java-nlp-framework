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
