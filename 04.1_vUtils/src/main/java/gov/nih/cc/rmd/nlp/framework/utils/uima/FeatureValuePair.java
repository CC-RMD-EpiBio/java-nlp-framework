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
// -------------------------------------------------------
/**
 * FeatureValuePair.java
 *
 * @author Guy Divita
 *  
 * 
 */
// -------------------------------------------------------
package gov.nih.cc.rmd.nlp.framework.utils.uima;

public class FeatureValuePair {

  
  // -------------------------------------------------------
  /**
   * Constructor
   *
   */
  // -------------------------------------------------------
  public FeatureValuePair() {
    
  }
  // -------------------------------------------------------
  /**
   * Constructor
   *
   * @param featureName
   * @param featureValue
   */
  // -------------------------------------------------------
  public FeatureValuePair(String featureName, String featureValue) {
   
    this.featureName = featureName;
    this.featureValue = featureValue;
  }
  // -------------------------------------------------------
  /**
   * getFeatureName
   *
   * @return featureName
   */
  // -------------------------------------------------------
  public String getFeatureName() {
    return featureName;
  }
  // ----------------------------------
  /**
   * set Description
   *
   * @param featureName the featureName to set
   */
  // ----------------------------------
  public void setFeatureName(String featureName) {
    this.featureName = featureName;
  }
  // -------------------------------------------------------
  /**
   * getFeatureValue
   *
   * @return featureValue
   */
  // -------------------------------------------------------
  public String getFeatureValue() {
    return featureValue;
  }
  // ----------------------------------
  /**
   * set Description
   *
   * @param featureValue the featureValue to set
   */
  // ----------------------------------
  public void setFeatureValue(String featureValue) {
    this.featureValue = featureValue;
  }
  
  

  // --------------------------
  // Class Variables
  // --------------------------
  private String featureName = null;
  private String featureValue = null;
} // end Class FeatureValuePair ----------------------------
