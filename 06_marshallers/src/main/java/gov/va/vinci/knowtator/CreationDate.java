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