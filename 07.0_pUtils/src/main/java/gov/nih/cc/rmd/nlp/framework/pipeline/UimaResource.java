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
 * UimaResource is a container that should mirror the UIMAResource Parameters
 * that would be coming from the analysis engine configuration file for a given
 * annotator
 *
 * @author  Guy Divita
 * @created Dec 22, 2019
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.Resource;

/**
 * @author guy
 *
 */
public class UimaResource extends org.apache.uima.resource.Resource_ImplBase {

  // =======================================================
  /**
   * Constructor UimaResource 
   *
   * @param resourceName
   * @param resourceUrl
   * @param resourceURLSuffix
   * @param implementation
   * @param pIsManditory
   */
  // =======================================================
  public UimaResource(String resourceName, Object resourceUrl, Object resourceURLSuffix, String implementation, boolean pIsManditory) {
   
    this.name = resourceName;
    this.url = resourceUrl;
    this.setUrlSuffix(resourceURLSuffix);
    this.implementation = implementation;
    this.isManditory = pIsManditory;
    
  }
  
 

  // ------------------------
  // Container Urls
  // ------------------------
  private String           name = null;
  private Object          url = null;
  private Object          urlSuffix = null;
  private boolean   isManditory = false;
  private String implementation = "";
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
   * geturl 
   * 
   * @return the Url
   */
  // =======================================================
  public final Object getUrl() {
    return this.url;
  }
  // =======================================================
  /**
   * seturl 
   * 
   * @param url the url to set
   */
  // =======================================================
  public final void setUrl(Object Url) {
    this.url = Url;
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
   * getimplementation 
   * 
   * @return the implementation
   */
  // =======================================================
  public final String implementation() {
    return implementation;
  }
  // =======================================================
  /**
   * setimplementation 
   * 
   * @param implementation the implementation to set
   */
  // =======================================================
  public final void setMultivalue(String implementation) {
    this.implementation = implementation;
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
  /**
   * @return the urlSuffix
   */
  public Object getUrlSuffix() {
    return urlSuffix;
  }
  /**
   * @param urlSuffix the urlSuffix to set
   */
  public void setUrlSuffix(Object urlSuffix) {
    this.urlSuffix = urlSuffix;
  }
  // =================================================
  /**
   * getResourceImplClass since this is an optional param
   * this could return null;
   * 
   * The question is if this does a classForname of 
   * and doesn't bind the resource - should an exception be
   * thrown? 
   * 
   * @return
   * @throws ClassNotFoundException 
  */
  // =================================================
  public Object getResourceImplClass() throws ClassNotFoundException {
    
    Class<?> returnVal = null;
    try {
      
      if ( this.implementation != null )
        returnVal =  Class.forName(implementation);
    
    } catch (Exception e ) {
      e.printStackTrace();
      System.err.println("Could not find/bind to " + this.implementation );
    }
    return returnVal;
  } // end Method getResourceImplClass() --------------
  
} // end Class UimaResource() --------------------
