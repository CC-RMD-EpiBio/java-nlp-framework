// =================================================
/**
 * HasSlotMention is a container class to hold <hasSlotmention id= xxx/>
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



public class HasSlotMention {

  // -----------------------------------------
  /**
   * Constructor: HasSlotMention.java Summary
   * [Detail Here]
   *
   * @param pSlotMentionId
   */
  // -----------------------------------------
  public HasSlotMention(String pSlotMentionId) {
    this.id = pSlotMentionId;
  }

  // -----------------------------------------
  /** 
   * getid retrieves id
   *  
   * @return the id
   */
  // -----------------------------------------
  public String getId() {
    return id;
  }

  // -----------------------------------------
  /** 
   * setid sets the value of id
   *  
   * @param id the id to set
   */
  // -----------------------------------------
  public void setId(String id) {
    this.id = id;
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
    return "<HasSlotMention id=" + U.quote(id)  + "/>";
  }

  // --------------------------------------------
  // GlobalVariables
  // --------------------------------------------
  private String id = null;
  
}
