// =================================================
/**
 * Mention.java Summary
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


public class Mention {

  // ----------------------------------------------
  /** 
   * toString
   *
   * @return
   */
  // ----------------------------------------------
  @Override
  public String toString() {
    return "<Mention id=" + id + "/>";
  }

  // -----------------------------------------
  /**
   * Constructor
   *
   * @param pMentionId
   */
  // -----------------------------------------
  public Mention(String pMentionId) {
    this.id = pMentionId;
  }
  
  // -----------------------------------------
  /** 
   * getmentionId retrieves mentionId
   *  
   * @return the mentionId
   */
  // -----------------------------------------
  public String getMentionId() {
    return this.id;
  }

  // -----------------------------------------
  /** 
   * setmentionId sets the value of mentionId
   *  
   * @param mentionId the mentionId to set
   */
  // -----------------------------------------
  public void setMentionId(String mentionId) {
    this.id = mentionId;
  }

  // -----------------------------------------
  // GlobalVariables
  // -----------------------------------------
  private String id = null;

}
