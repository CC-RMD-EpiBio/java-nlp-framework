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
// =======================================================
/**
 * UimaContextParameter is a container that should mirror the UIMAContext Parameters
 * that would be coming from the analysis engine configuration file for a given
 * annotator
 *
 * @author  Guy Divita
 * @created Jul 22, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline;

import org.apache.uima.UimaContext;

/**
 * @author guy
 *
 */
public class UimaContextParameter {

  // =======================================================
  /**
   * Constructor UimaContextParameter 
   *
   * @param parameterName
   * @param parameterValue
   * @param pIsManditory
   */
  // =======================================================
  public UimaContextParameter(String parameterName, Object parameterValue, boolean pIsManditory) {
   
    this.name = parameterName;
    this.value = parameterValue;
    this.isManditory = pIsManditory;
    this.type = "String";
  }
  
  // =======================================================
  /**
   * Constructor UimaContextParameter 
   *
   * @param name
   * @param value
   * @param type 
   * @param isManditory
   * @param isMultiValued
   */
  // =======================================================
  public UimaContextParameter(String name, Object value, String type,  boolean isManditory, boolean isMultiValued) {
    super();
    this.name = name;
    this.value = value;
    this.isManditory = isManditory;
    this.isMultiValued = isMultiValued;
    this.type = type;
  }

  // ------------------------
  // Container values
  // ------------------------
  private String           name = null;
  private Object          value = null;
  private boolean   isManditory = false;
  private boolean isMultiValued = false;
  private String           type = "String";
  // =======================================================
  /**
   * getname 
   * 
   * @return the name
   */
  // =======================================================
  public final String getName() {
    return name;
  }
  // =======================================================
  /**
   * setname 
   * 
   * @param name the name to set
   */
  // =======================================================
  public final void setName(String name) {
    this.name = name;
  }
  // =======================================================
  /**
   * getvalue 
   * 
   * @return the value
   */
  // =======================================================
  public final Object getValue() {
    return value;
  }
  // =======================================================
  /**
   * setvalue 
   * 
   * @param value the value to set
   */
  // =======================================================
  public final void setValue(Object value) {
    this.value = value;
  }
  // =======================================================
  /**
   * getisManditory 
   * 
   * @return the isManditory
   */
  // =======================================================
  public final boolean isManditory() {
    return isManditory;
  }
  // =======================================================
  /**
   * setisManditory 
   * 
   * @param isManditory the isManditory to set
   */
  // =======================================================
  public final void setManditory(boolean isManditory) {
    this.isManditory = isManditory;
  }
  // =======================================================
  /**
   * getisMultiValued 
   * 
   * @return the isMultiValued
   */
  // =======================================================
  public final boolean isMultiValued() {
    return isMultiValued;
  }
  // =======================================================
  /**
   * setisMultiValued 
   * 
   * @param isMultiValued the isMultiValued to set
   */
  // =======================================================
  public final void setMultiValued(boolean isMultiValued) {
    this.isMultiValued = isMultiValued;
  }
  // =======================================================
  /**
   * gettype 
   * 
   * @return the type
   */
  // =======================================================
  public final String getType() {
    return type;
  }
  // =======================================================
  /**
   * settype 
   * 
   * @param type the type to set
   */
  // =======================================================
  public final void setType(String type) {
    this.type = type;
  }

  // =======================================================
  /**
   * getUimaContext returns a UIMAConcext from this set
   * of parameters
   * 
   * @return UIMAContext
   */
  // =======================================================
  public UimaContext getUimaContext() {
    // TODO Auto-generated method stub
    return null;
    // End Method getUIMAContext() ======================
  }
  
}
