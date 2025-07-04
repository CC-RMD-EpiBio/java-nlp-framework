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
 * SectionInfoContainer.java  Summary [TBD]
 *
 * @author     Guy Divita
 * @created    Jul 10, 2018
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.annotator.loretta;

/**
 * @author guy
 *
 */
public final class SectionInfoContainer {

  // =================================================
  /**
   * Constructor
   *
   * @param pSectionName
   * @param pNormalizedSectionName
   * @param pAnnotationLabel
   * 
  **/
  // =================================================
  public SectionInfoContainer(String pSectionName, String pNormalizedSectionName, String pAnnotationLabel) {
    
    this.sectionName = pSectionName;
    this.normalizedSectionName = pNormalizedSectionName;
    this.annotationLabel = pAnnotationLabel;
    
  } // end Constructor () ----------------------------

  
  /**
   * @return the sectionName
   */
  public final String getSectionName() {
    return sectionName;
  }
  /**
   * @param sectionName the sectionName to set
   */
  public final void setSectionName(String sectionName) {
    this.sectionName = sectionName;
  }
  /**
   * @return the normalizedSectionName
   */
  public final String getNormalizedSectionName() {
    return normalizedSectionName;
  }
  /**
   * @param normalizedSectionName the normalizedSectionName to set
   */
  public final void setNormalizedSectionName(String normalizedSectionName) {
    this.normalizedSectionName = normalizedSectionName;
  }
  /**
   * @return the annotationLabel
   */
  public final String getAnnotationLabel() {
    return annotationLabel;
  }
  /**
   * @param annotationLabel the annotationLabel to set
   */
  public final void setAnnotationLabel(String annotationLabel) {
    this.annotationLabel = annotationLabel;
  }
  
  
//-------------------------
 // Class Variables
 // -------------------------
 String sectionName = null;
 String normalizedSectionName = null;
 String annotationLabel = null;
  
} // end Class SectionInfoContainer() ---------------
