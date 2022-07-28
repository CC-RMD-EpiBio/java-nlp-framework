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
 * StringSlotMentionValue.java Summary
 *  Class Detail
 *
 *
 * @author  Guy Divita 
 * @created Jun 13, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.va.vinci.knowtator;

import gov.nih.cc.rmd.nlp.framework.utils.U;



public class StringSlotMentionValue {

  // -----------------------------------------
  /**
   * Constructor: StringSlotMentionValue.java Summary
   * [Detail Here]
   *
   * @param pValue
   */
  // -----------------------------------------
  public StringSlotMentionValue(String pValue) {
   this.value = pValue;
  }

  // -----------------------------------------
  /** 
   * getvalue retrieves value
   *  
   * @return the value
   */
  // -----------------------------------------
  public String getValue() {
    return value;
  }

  // -----------------------------------------
  /** 
   * setvalue sets the value of value
   *  
   * @param value the value to set
   */
  // -----------------------------------------
  public void setValue(String value) {
    this.value = value;
  }

  // ----------------------------------------------
  /** 
   * toString
   *
   * @return
   */
  // ----------------------------------------------
  @Override
  public String toString() {
    StringBuffer buff = new StringBuffer();
    buff.append("<StringSlotMentionValue ");
    if ( value != null )
      buff.append("value=" + U.quote(value) );
    buff.append( "/>" );
    return buff.toString();
  }

  // -----------------------------------------
  // GlobalVariables
  // -----------------------------------------
  private String value = null;
  
}
