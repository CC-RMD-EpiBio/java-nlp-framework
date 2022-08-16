// =================================================
/**
 * ComplexSlotMentionValue.java Summary
 *  Class Detail
 *
 *
 * @author  Guy Divita 
 * @created Aug 2, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.va.vinci.knowtator;

import gov.nih.cc.rmd.nlp.framework.utils.U;



public class ComplexSlotMentionValue {
  
  
  // -----------------------------------------
  /**
   * Constructor: ComplexSlotMentionValue
   *
   * @param pValue
   */
  // -----------------------------------------
  public ComplexSlotMentionValue(String pValue) {
   this.value = pValue;
  }
  
//-----------------------------------------
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
    buff.append("<ComplexSlotMentionValue ");
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
