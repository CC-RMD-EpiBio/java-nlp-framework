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
/*
 *
 */
/**
 * PipelineComponent is a container to hold pipeline component information
 * devoid of implementation - 
 *    the name of the class, the parameters that could/should be passed to it
 *
 * @author  Guy Divita
 * @created Jul 17, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline;

import java.util.ArrayList;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;

import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * The Class PipelineComponent.
 */
public class PipelineComponent {

  /** The parameters. */
  private String[] parameters = null;

  /** The uima context parameters. */
  private UimaContextParameter[] uimaContextParameters = null;
  
 /** The uima External Resource */
  private UimaResource[] uimaResources = null;

  /** The annotator class name. */
  private String annotatorClassName = null;

  /** The is remote. */
  private boolean isRemote = false;

  /** The end point name. */
  private String endPointName = null;

  /** The broker URL. */
  private String brokerURL = null;

  /** The service name. */
  private String serviceName = null;

  /** The number of instances. */
  private int numberOfInstances = 1;
  
  /** The aeConfigFile */
  private String aeConfigFile = null;

  // =======================================================
  /**
   * Constructor PipelineComponent .
   *
   * @param pAnnotatorClassName the annotator class name
   * @param pParameters the parameters
   */
  // =======================================================
  public PipelineComponent(String pAnnotatorClassName, String[] pParameters) {
    this.annotatorClassName = pAnnotatorClassName;
    this.parameters = pParameters;

  } // end Constructor()

  // =======================================================
  /**
   * Constructor PipelineComponent .
   *
   * @param pAnnotatorClassName the annotator class name
   * @param pUimaContextParameters the uima context parameters
   */
  // =======================================================
  public PipelineComponent(String pAnnotatorClassName, UimaContextParameter[] pUimaContextParameters) {

    this.annotatorClassName = pAnnotatorClassName;
    this.uimaContextParameters = pUimaContextParameters;
  } // end Constructor()

  // =======================================================
  /**
   * Constructor PipelineComponent .
   *
   * @param pAnnotatorClassName the annotator class name
   * @param pUimaContextParameters the uima context parameters
   */
  // =======================================================
  public PipelineComponent(String pAnnotatorClassName, UimaContextParameter pUimaContextParameters) {

    this.annotatorClassName = pAnnotatorClassName;
    this.uimaContextParameters = new UimaContextParameter[1];
    this.uimaContextParameters[0] = pUimaContextParameters;
  } // end Constructor()

  // =======================================================
  /**
   * Constructor PipelineComponent .
   *
   * @param pAnnotatorClassName the annotator class name
   * @param pUimaContextParameters the uima context parameters
   * @param pServiceName the service name
   * @param pNumberOfInstances the number of instances
   */
  // =======================================================
  public PipelineComponent(String pAnnotatorClassName, UimaContextParameter pUimaContextParameters,
      String pServiceName, int pNumberOfInstances) {

    this.annotatorClassName = pAnnotatorClassName;
    this.uimaContextParameters = new UimaContextParameter[1];
    this.uimaContextParameters[0] = pUimaContextParameters;
    this.serviceName = pServiceName;
    this.numberOfInstances = pNumberOfInstances;
  } // end Constructor()

  // =======================================================
  /**
   * Constructor PipelineComponent .
   *
   * @param pAnnotatorClassName the annotator class name
   */
  // =======================================================
  public PipelineComponent(String pAnnotatorClassName) {
    this.annotatorClassName = pAnnotatorClassName;
    this.uimaContextParameters = null;
  } // end Constructor

  // =================================================
  /**
   * Constructor
   *
   * @param pAnnotatorClassName
   * @param pParameters
   * @param pResources
   * 
  **/
  // =================================================
  public PipelineComponent(String pAnnotatorClassName, UimaContextParameter[] pParameters, UimaResource[] pResources) {
    this.annotatorClassName = pAnnotatorClassName;
    this.uimaContextParameters = pParameters;
    this.uimaResources = pResources;
  }

  // =================================================
  /**
   * Constructor
   *
   * @param pAnnotatorClassName
   * @param pAEDescriptorConfigFileName  Hey, if it's already been created, pass it in.
   *                                      This makes framework work even more with straight up uima.
   * 
  **/
  // =================================================
  public PipelineComponent(String pAnnotatorClassName, String pAEDescriptorConfigFileName) {
    this.annotatorClassName = pAnnotatorClassName;
    this.aeConfigFile = pAEDescriptorConfigFileName;
  }

  // =======================================================
  /**
   * getParameters .
   *
   * @return String[] of {"key1=value1", "key2=value2" ...}
   */
  // =======================================================
  public final String[] getParameters() {
    return parameters;
  } // end Method getParameters() --------------------------

  // =======================================================
  /**
   * getParameters .
   *
   * @return String[] of {"key1=value1", "key2=value2" ...}
   */
  // =======================================================
  public UimaContextParameter[] getUimaConextParameters() {
    return this.uimaContextParameters;

  } // end Method getUimaContextParameters() --------------------------

  // =======================================================
  /**
   * getParameterVector.
   *
   * @return String[] of {"key1","value1","key2","value2" ...}
   */
  // =======================================================
  public final String[] getParameterVector() {

    String[] returnVal = null;
    ArrayList<String> buff = new ArrayList<String>();

    if (this.parameters != null && this.parameters.length > 0) {
      for (String parameter : this.parameters) {
        String[] keyValuePair = U.split(parameter, "=");
        if (keyValuePair[0] != null)
          buff.add(keyValuePair[0]);
        if (keyValuePair[1] != null)
          buff.add(keyValuePair[1]);
        else
          buff.add("null");
      } // end loop through parameters

      returnVal = buff.toArray(new String[buff.size()]);
    }

    return returnVal;
  } // end Method getParameters() --------------------------

  // =======================================================
  /**
   * setParameters.
   *
   * @param pParameters the parameters
   */
  // =======================================================
  public final void setParameters(String[] pParameters) {
    this.parameters = pParameters;
  } // end Method setParameters) --------------------------

  // =======================================================
  /**
   * setUimaConextParameters.
   *
   * @param pParameters the parameters
   */
  // =======================================================
  public final void setParameters(UimaContextParameter[] pParameters) {
    this.uimaContextParameters = pParameters;
  } // end Method setUimaConextParameters) --------------------------

  // =======================================================
  /**
   * getAnnotatorClassName .
   *
   * @return String
   */
  // =======================================================
  public final String getAnnotatorClassName() {
    return annotatorClassName;
  } // end Method getAnnotatorClassName() -------------------

  // =======================================================
  /**
   * setAnnotatorClassName .
   *
   * @param pAnnotatorClassName the annotator class name
   */
  // =======================================================
  public final void setAnnotatorClassName(String pAnnotatorClassName) {
    this.annotatorClassName = pAnnotatorClassName;
  } // end Method setAnnotatorClassName() ------------------

  // =======================================================
  /**
   * getAnnotatorClass returns the class for the classname.
   * 
   * Note that in order for this to work, you will have to make sure that the
   * dependency for this class is declared. -
   * 
   * @return Class<?>
   */
  // =======================================================
  @SuppressWarnings("unchecked")
  public final Class<JCasAnnotator_ImplBase> getAnnotatorClass() {

    Class<JCasAnnotator_ImplBase> componentClass = null;
    try {
      componentClass = (Class<JCasAnnotator_ImplBase>) Class.forName(this.getAnnotatorClassName());
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue trying to get the class for " + this.getAnnotatorClassName()
          + ". Perhaps this class has not been declared as a dependency? " + e.toString();
      System.err.println(msg);
    }
    return componentClass;
  } // End Method getAnnotatorClass() ======================

  // =======================================================
  /**
   * getParametersForFit returns the parameters in parameter pairs where the
   * even parameters are the names and the odd parameters are the values.
   *
   * @return the parameters for fit
   */
  // =======================================================
  public final Object[] getParametersForFit() {

    Object[] fitParameters = null;

    if (this.uimaContextParameters != null && this.uimaContextParameters.length > 0) {
      fitParameters = new Object[this.uimaContextParameters.length * 2];
      int i = 0;
      for (UimaContextParameter parameter : this.uimaContextParameters) {
        fitParameters[i] = parameter.getName();
        i++;
        fitParameters[i] = parameter.getValue();
        i++;
      }
    }

    return fitParameters;
  } // End Method getParametersForFit() ======================

  // ==========================================================
  /**
   * setRemote sets the flag to indicate if this annotator is a remote service
   * or not.
   *
   * @param pIsRemote the remote
   */
  // ==========================================================
  public final void setRemote(boolean pIsRemote) {
    this.isRemote = pIsRemote;
  } // End Method setRemote =======

  // =========================================================
  /**
   * isRemoteAnnotator returns true if this is a remote annotator.
   *
   * @return boolean
   */
  // =========================================================
  public final boolean isRemoteAnnotator() {

    return this.isRemote;
  } // End Method isRemoteAnnotator ==========================

  // =========================================================
  /**
   * setBrokerURL sets broker url for a remote service.
   *
   * @param pBrokerURL the broker URL
   */
  // =========================================================
  public final void setBrokerURL(String pBrokerURL) {
    this.brokerURL = pBrokerURL;
  } // End Method setBrokerURL ===============================

  // =========================================================
  /**
   * getBrokerURL gets broker url for a remote service.
   *
   * @return String
   */
  // =========================================================
  public final String getBrokerURL() {
    return this.brokerURL;
  } // End Method getBrokerURL ===============================

  // =========================================================
  /**
   * setEndPointName sets the endpoint name for a remote service.
   *
   * @param pEndPointName the end point name
   */
  // =========================================================
  public final void setEndPointName(String pEndPointName) {
    this.endPointName = pEndPointName;
  } // End Method setEndPointName ============================

  // =========================================================
  /**
   * getEndPointName gets the endpoint name for a remote service.
   *
   * @return String
   */
  // =========================================================
  public final String getEndPointName() {
    return this.endPointName;
  } // End Method getEndPointName ============================

  // =========================================================
  /**
   * setNumberOfInstances sets the number of scaled-out instances of this
   * annotator .
   *
   * @param pNumberOfInstances the number of instances
   */
  // =========================================================
  public final void setNumberOfInstances(int pNumberOfInstances) {
    this.numberOfInstances = pNumberOfInstances;
  } // End Method setNumberOfInstances =======================

  // =========================================================
  /**
   * getNumberOfInstances gets the number of instances that this annotator
   * should be scaled-out to.
   *
   * @return int
   */
  // =========================================================
  public final int getNumberOfInstances() {
    return this.numberOfInstances;
  } // End Method getEndPointName ============================

  /**
   * Returns the service name.
   *
   * @return the serviceName
   */
  public final String getServiceName() {
    return serviceName;
  }

  /**
   * Sets the service name.
   *
   * @param serviceName the serviceName to set
   */
  public final void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

 

  /**
   * @return the uimaResources
   */
  public final UimaResource[] getUimaResources() {
    return uimaResources;
  }

  /**
   * @param uimaResources the uimaResources to set
   */
  public final void setUimaResources(UimaResource[] uimaResources) {
    this.uimaResources = uimaResources;
  }

  // =================================================
  /**
   * getAEConfigFile 
   * 
   * @return String
  */
  // =================================================
   public final String getAEConfigFile() {
    return this.aeConfigFile;
  }

   // =================================================
   /**
    * getAEConfigName returns the config file (with a path), but without the .xml extension.
    * 
    * @return String
   */
   // =================================================
    public final String getAEConfigName() {
      String returnVal = null;
      if ( this.aeConfigFile != null ) {
        // String path  =   U.getDirFromFileName(this.aeConfigFile); <--- //   -->   this munges periods into slashes -don't use
        // String name  = U.getFileNamePrefix(this.aeConfigFile);
        returnVal = this.aeConfigFile;
        returnVal = returnVal.substring(0,  returnVal.indexOf(".xml"));
       
      }
     return returnVal;
   }
   
    
 // =================================================
    /**
     * getAEConfigName returns the config file name (with extension)
     * 
     * @return String
    */
    // =================================================
     public final String getAEConfigFileName() {
       return this.aeConfigFile;
    
    }
    
  // =================================================
  /**
   * setAEConfigFile
   * @param aeConfigFile the aeConfigFile to set
   */
  // =================================================
  public final void setAEConfigFile(String pAeConfigFile) {
    this.aeConfigFile = pAeConfigFile;
  }

} // end Class PipelineComponent --------------------------
