// =================================================
/**
 * MentionClass is a container that holds the label for a classMention
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

public class MentionClass {

  // -----------------------------------------
  /**
   * Constructor
   *
   * @param pLabel
   */
  // -----------------------------------------
  public MentionClass(String pLabel) {
    this.id = pLabel;
  }
  
 

  // ----------------------------------------------
  /**
   * Constructor: MentionClass
   *
   * @param pMentionClassId
   * @param pRelationship
   */
  // ----------------------------------------------
  public MentionClass(String pMentionClassId, String pRelationship) {
    this.id = pMentionClassId;
    
    if ( pRelationship != null && (pRelationship.trim().length() > 0 ) )
    this.relationship = pRelationship;
    
    
    
  } // end Constructor() ----------------------



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
   * setRelationship sets the value of relationship
   *  
   * @param relationship the relationship to set
   *
   */
  // ----------------------------------------------
  public void setRelationship(String relationship) {
    this.relationship = relationship;
  }



  // ----------------------------------------------
  /** 
   * getRelationship retrieves relationship
   *  
   * @return the relationship
   */
  // ----------------------------------------------
  public String getRelationship() {
    return relationship;
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
    
    buff.append("<MentionClass id=");
    buff.append( U.quote(id) + "> ");
    if ( relationship != null )
      buff.append(relationship );
    buff.append("/>");
    return buff.toString();
  }

  // -----------------------------------------
  // Global Variables
  // -----------------------------------------
  private String id = null;  // the annotation label 
  private String relationship = null; 
  
}
