// =================================================
/**
 * MentionSlot holds relationship names between a class mention and a complexSlotMentionValue.
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



public class MentionSlot {

  // -----------------------------------------
  /**
   * Constructor:
   * @param pName
   */
  // -----------------------------------------
  public MentionSlot(String pName) {
    this.id = pName;
  }
  
  
  // -----------------------------------------
  /** 
   * getId retrieves mentionId (relationship name)
   *  
   * @return the mentionId
   */
  // -----------------------------------------
  public String getId() {
    return this.id;
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
    return "<MentionSlot id=" + U.quote(id)  + "/>";
  }


  // -----------------------------------------
  /** 
   * setId sets the value of mentionId
   *  
   * @param mentionId the mentionId to set
   */
  // -----------------------------------------
  public void setId(String mentionId) {
    this.id = mentionId;
  }

  // -----------------------------------------
  // GlobalVariables
  // -----------------------------------------
  private String id = null;  // holds relationship names!!!

}
