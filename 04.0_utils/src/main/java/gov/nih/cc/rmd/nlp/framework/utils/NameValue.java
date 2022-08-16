// =================================================
/**
 * NameValue is a container class to hold name value pairs
 *   
 *
 * @author  Guy Divita 
 * @created Jul 6, 2011
 *
 *
 *
 *
 *
 * 
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils;


public class NameValue {

  
 
  
  // ----------------------------------------------
  /**
   * Constructor: NameValue
   *
   * @param name
   * @param value
   */
  // ----------------------------------------------
  public NameValue(String name, String value) {
    super();
    this.name = name;
    this.value = value;
  }

  // ----------------------------------------------
  /** 
   * toString
   *
   * @return String
   */
  // ----------------------------------------------
  @Override
  public String toString() {
    return "NameValue [name=" + name + ", value=" + value + "]";
  }
  // ----------------------------------------------
  /** 
   * getName retrieves name
   *  
   * @return String the name
   */
  // ----------------------------------------------
  public String getName() {
    return name;
  }
  // ----------------------------------------------
  /** 
   * getValue retrieves value
   *  
   * @return String the value
   */
  // ----------------------------------------------
  public String getValue() {
    return value;
  }
  // ----------------------------------------------
  /** 
   * setName sets the value of name
   *  
   * @param name the name to set
   *
   */
  // ----------------------------------------------
  public void setName(String name) {
    this.name = name;
  }

  // ----------------------------------------------
  /** 
   * setValue sets the value of value
   *  
   * @param value the value to set
   *
   */
  // ----------------------------------------------
  public void setValue(String value) {
    this.value = value;
  }

  // --------------------------------
  // Class Variables
  // --------------------------------
  private  String name = null;
  private String value = null;
  
} // end Class NameValue() -----------------------
