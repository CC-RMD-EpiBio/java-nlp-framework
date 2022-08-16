// =================================================
/**
 * CreationDate.java Summary
 *  Class Detail
 *
 *
 * @author  Guy Divita 
 * @created Jun 14, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.va.vinci.knowtator;


public class CreationDate {

  // -----------------------------------------
  /**
   * Constructor: 
   *
   * @param pCreationDate
   */
  // -----------------------------------------
  public CreationDate(String pCreationDate) {
    this.mDate = pCreationDate;
  }

  // -----------------------------------------
  /**
   * getDateString()
   * 
   * @return
   */
  // -----------------------------------------
  public String getDateString() {
    
    return this.mDate;
  }
  
  // -----------------------------------------
  /**
   * setDate
   * 
   * @param pDateString
   */
  // -----------------------------------------
  public void setDate( String pDateString) {
    
    this.mDate = pDateString;
  }
  
  // -----------------------------------------
  // GlobalVariables
  // -----------------------------------------
  private String mDate = null;

}
