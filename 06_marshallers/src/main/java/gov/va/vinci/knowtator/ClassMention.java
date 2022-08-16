// =================================================
/**
 * ClassMention is a container class to hold a classMention
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


import java.util.ArrayList;


public class ClassMention {

  // -----------------------------------------
  /**
   * Constructor: ClassMention
   *
   * @param annotationIdString
   * @param label
   */
  // -----------------------------------------
  public ClassMention(String pReferenceId, String label) {
    this.id = pReferenceId;
    this.mentionClass = new MentionClass( label);
  }

  // ----------------------------------------------
  /**
   * Constructor: ClassMention
   *
   * @param pClassMentionId
   * @param pMentionClass
   * @param pHasSlotMentions
   */
  // ----------------------------------------------
  public ClassMention(String pClassMentionId, MentionClass pMentionClass, ArrayList<HasSlotMention> pHasSlotMentions) {
   this.id = pClassMentionId;
   this.mentionClass = pMentionClass;
   this.hasSlotMentions = pHasSlotMentions;
    
    
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
   * getmentionClass retrieves mentionClass
   *  
   * @return the mentionClass
   */
  // -----------------------------------------
  public MentionClass getMentionClass() {
    return mentionClass;
  }
  // -----------------------------------------
  /** 
   * setmentionClass sets the value of mentionClass
   *  
   * @param mentionClass the mentionClass to set
   */
  // -----------------------------------------
  public void setMentionClass(MentionClass mentionClass) {
    this.mentionClass = mentionClass;
  }
  // -----------------------------------------
  /** 
   * gethasSlotMentions retrieves hasSlotMentions
   *  
   * @return the hasSlotMentions
   */
  // -----------------------------------------
  public ArrayList<HasSlotMention> getHasSlotMentions() {
    return this.hasSlotMentions;
  }
  // -----------------------------------------
  /** 
   * addslotMention adds a slotMention to the list of slotMentions.
   *  
   * @param pSlotMentionId the slotMentionId to add
   */
  // -----------------------------------------
  public void addHasSlotMention(String pSlotMentionId){
    if ( this.hasSlotMentions == null )
        this.hasSlotMentions = new ArrayList<HasSlotMention>();
    
    this.hasSlotMentions.add( new HasSlotMention(pSlotMentionId));
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
    return "<ClassMention id=" + U.quote(id)  + ">" + U.NL + 
             mentionClass + U.NL + 
             U.toString( hasSlotMentions ) + U.NL + 
             "</ClassMention>" ;
             
  }

  // -----------------------------------------
  // Global Variables
  // -----------------------------------------
  private String id = null;
  private MentionClass mentionClass = null;
  private ArrayList<HasSlotMention> hasSlotMentions = null;
  
  

}
