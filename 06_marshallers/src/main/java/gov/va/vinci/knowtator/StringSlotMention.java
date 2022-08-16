// =================================================
/**
 * StringSlotMention is a container class to hold string slot mentions
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



public class StringSlotMention {

  // -----------------------------------------
  /**
   * Constructor
   *
   * @param pReferenceId;
   * @param pName
   * @param pValue
   */
  // -----------------------------------------
  public StringSlotMention(String pReferenceId, String pName, String pValue) {
    this.id = pReferenceId;
    this.mentionSlot = new MentionSlot( pName);
    this.stringSlotMentionValue = new StringSlotMentionValue( pValue);
  } // end Constructor() --------------------
  
  // ----------------------------------------------
  /**
   * Constructor: StringSlotMention
   *
   * @param pReferenceId
   * @param pMmentionSlot
   * @param pStringSlotMentionValue
   */
  // ----------------------------------------------
  public StringSlotMention(String pReferenceId, MentionSlot pMentionSlot, StringSlotMentionValue pStringSlotMentionValue) {
    this.id = pReferenceId;
    this.mentionSlot = pMentionSlot;
    this.stringSlotMentionValue = pStringSlotMentionValue;
    
  } // end Constructor() ---------------------

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
  // -----------------------------------------
  /** 
   * getmentionSlot retrieves mentionSlot
   *  
   * @return the mentionSlot
   */
  // -----------------------------------------
  public MentionSlot getMentionSlot() {
    return mentionSlot;
  }
  // -----------------------------------------
  /** 
   * setmentionSlot sets the value of mentionSlot
   *  
   * @param mentionSlot the mentionSlot to set
   */
  // -----------------------------------------
  public void setMentionSlot(MentionSlot mentionSlot) {
    this.mentionSlot = mentionSlot;
  }
  // -----------------------------------------
  /** 
   * getstringSlotMentionValue retrieves stringSlotMentionValue
   *  
   * @return the stringSlotMentionValue
   */
  // -----------------------------------------
  public StringSlotMentionValue getStringSlotMentionValue() {
    return stringSlotMentionValue;
  }
  // -----------------------------------------
  /** 
   * setstringSlotMentionValue sets the value of stringSlotMentionValue
   *  
   * @param stringSlotMentionValue the stringSlotMentionValue to set
   */
  // -----------------------------------------
  public void setStringSlotMentionValue(
      StringSlotMentionValue stringSlotMentionValue) {
    this.stringSlotMentionValue = stringSlotMentionValue;
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
    return "<StringSlotMention id=" + U.quote(id) + ">" + U.NL +  
            mentionSlot + U.NL + 
            stringSlotMentionValue + 
            "</StringSlotMention>"  ;
  }

  // ------------------------------------------
  // Global Variables
  // ------------------------------------------
  private String               id = null;
  private MentionSlot mentionSlot = null;
  private StringSlotMentionValue stringSlotMentionValue = null;
 

}
