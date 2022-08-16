/*******************************************************************************
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                       Epidemiology and Biostatistics Branch 
 *                                            2019 - 2022
 *   ---------------------------------------------------------------------------
 *   Copyright Notice:
 *   This software was developed and funded by the National Institutes of Health
 *   Clinical Center (NIHCC), part of the National Institutes of Health (NIH),
 *   and agency of the United States Department of Health and Human Services,
 *   which is making the software available to the public for any commercial
 *   or non-commercial purpose under the following open-source BSD license.
 *  
 *   Government Usage Rights Notice:
 *   The U.S. Government retains unlimited, royalty-free usage rights to this 
 *   software, but not ownership, as provided by Federal law. Redistribution 
 *   and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *      1. Redistributions of source code must retain the above copyright
 *         and government usage rights notice, this list of conditions and the 
 *         following disclaimer.
 *  
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *        
 *      3. Neither the names of the National Institutes of Health Clinical
 *         Center, the National Institutes of Health, the U.S. Department of
 *         Health and Human Services, nor the names of any of the software
 *         developers may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
 *   
 *      4. The U.S. Government retains an unlimited, royalty-free right to
 *         use, distribute or modify the software.
 *   
 *      5. Please acknowledge NIH CC as the source of this software by including
 *         the phrase: "Courtesy of the U.S. National Institutes of Health Clinical Center"
 *          or 
 *                     "Source: U.S. National Institutes of Health Clinical Center."
 *  
 *     THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS
 *     IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *     TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *     PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 *     OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *     EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *     PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *     NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *     When attributing this code, please make reference to:
 *        Divita G, Carter ME, Tran LT, Redd D, Zeng QT, Duvall S, Samore MH, Gundlapalli AV. 
 *        v3NLP Framework: tools to build applications for extracting concepts from clinical text. 
 *        eGEMs. 2016;4(3). 
 *      
 *     In the absence of a specific paper or url listed above, reference https://github.com/CC-RMD-EpiBio/java-nlp-framework
 *   
 *     To view a copy of this license, visit https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD
 *******************************************************************************/
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
