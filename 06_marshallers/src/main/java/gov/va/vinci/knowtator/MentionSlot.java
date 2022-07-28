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
