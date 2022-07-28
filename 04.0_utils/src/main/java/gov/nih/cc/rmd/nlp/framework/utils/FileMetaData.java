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
 * FileMetaData is a container to hold file meta data
 * including a referenceDate, the doc type, the doc name
 * ...
 *
 * @author     Guy Divita
 * @created    Mar 15, 2018
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils;

public class FileMetaData {

  private String documentDate = null;
  /**
   * @return the documentDate
   */
  public final String getDocumentDate() {
    return documentDate;
  }
  /**
   * @param documentDate the documentDate to set
   */
  public final void setDocumentDate(String documentDate) {
    this.documentDate = documentDate;
  }
  /**
   * @return the documentType
   */
  public final String getDocumentType() {
    return documentType;
  }
  /**
   * @param documentType the documentType to set
   */
  public final void setDocumentType(String documentType) {
    this.documentType = documentType;
  }
  /**
   * @return the documentName
   */
  public final String getDocumentName() {
    return documentName;
  }
  /**
   * @param documentName the documentName to set
   */
  public final void setDocumentName(String documentName) {
    this.documentName = documentName;
  }
  /**
   * @return the patientId
   */
  public final String getPatientId() {
    return patientId;
  }
  /**
   * @param patientId the patientId to set
   */
  public final void setPatientId(String patientId) {
    this.patientId = patientId;
  }
  public final int getPageNumber() {
    return pageNumber;
  }
  public final void setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber;
  }
  private String documentType = null;
  private String documentName = null;
  private String patientId = null;
  private int pageNumber = -1;
 
} // end Class FileMetaData ------------------------
