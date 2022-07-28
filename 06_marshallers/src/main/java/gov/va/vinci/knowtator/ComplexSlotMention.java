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
 * ComplexSlotMention.java Summary
 *  Class Detail
 *
 *
 * @author  Guy Divita 
 * @created Aug 1, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.va.vinci.knowtator;

import gov.nih.cc.rmd.nlp.framework.utils.U;



public class ComplexSlotMention {
  

  // ----------------------------------------------
  /**
   * Constructor: ComplexSlotMention
   *
   * @param id
   * @param mentionSlot
   * @param complexSlotValue
   */
  // ----------------------------------------------
  public ComplexSlotMention(String id, MentionSlot mentionSlot, ComplexSlotMentionValue complexSlotValue) {
    super();
    this.id = id;
    this.mentionSlot = mentionSlot;
    this.complexSlotValue = complexSlotValue;
  }
  // ----------------------------------------------
  /**
   * Constructor: ComplexSlotMention
   *
   */
  // ----------------------------------------------
  public ComplexSlotMention() {
    super();
  }
  // ----------------------------------------------
  /** 
   * getId retrieves id
   *  
   * @return the id
   */
  // ----------------------------------------------
  public String getId() {
    return id;
  }
  // ----------------------------------------------
  /** 
   * setId sets the value of id
   *  
   * @param id the id to set
   *
   */
  // ----------------------------------------------
  public void setId(String id) {
    this.id = id;
  }
  // ----------------------------------------------
  /** 
   * getMentionSlot retrieves mentionSlot
   *  
   * @return the mentionSlot
   */
  // ----------------------------------------------
  public MentionSlot getMentionSlot() {
    return mentionSlot;
  }
  // ----------------------------------------------
  /** 
   * setMentionSlot sets the value of mentionSlot
   *  
   * @param mentionSlot the mentionSlot to set
   *
   */
  // ----------------------------------------------
  public void setMentionSlot(MentionSlot mentionSlot) {
    this.mentionSlot = mentionSlot;
  }
  // ----------------------------------------------
  /** 
   * getComplexSlotValue retrieves complexSlotValue
   *  
   * @return the complexSlotValue
   */
  // ----------------------------------------------
  public Object getComplexSlotValue() {
    return complexSlotValue;
  }
  // ----------------------------------------------
  /** 
   * setComplexSlotValue sets the value of complexSlotValue
   *  
   * @param complexSlotValue the complexSlotValue to set
   *
   */
  // ----------------------------------------------
  public void setComplexSlotValue(ComplexSlotMentionValue complexSlotValue) {
    this.complexSlotValue = complexSlotValue;
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
    return "<ComplexSlotMention id=" + U.quote(id)  + ">" + U.NL + 
    mentionSlot + U.NL +
    complexSlotValue + U.NL + 
    "</ComplexSlotMention>" ;
  }


  // ----------------------------------------
  // Class Variables
  // ----------------------------------------
  private String               id = null;
  private MentionSlot mentionSlot = null;
  private ComplexSlotMentionValue complexSlotValue = null;

} // end Class ComplexSlotMention( ---------
