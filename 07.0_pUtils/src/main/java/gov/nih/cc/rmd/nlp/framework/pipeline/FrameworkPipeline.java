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
/**
 * FrameworkPipeline holds the components of making a pipeline
 * (devoid of the underlying implementation)
 *
 * @author  Guy Divita
 * @created Jul 17, 2014
 *
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline;

import java.util.ArrayList;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.pipeline.UimaContextParameter;

/**
 * The Class FrameworkPipeline.
 */
public final class FrameworkPipeline {

  /** The Constant FIT. */
  public static final String FIT = "FIT";

  /** The Constant FLAP. */
  public static final String FLAP = "FLAP";

  /** The pipeline implementation. */
  private String pipelineImplementation = FIT; // FIT|FLAP
                                               // fit for uimaFit
                                               // implementation,
                                               // FLAP for Leo implementation

  /** The args. */
  private String[] args; // Global arguments

  /** The pipeline components. */
  private ArrayList<PipelineComponent> pipelineComponents;

  /** The type descriptor classpath. */
  private String typeDescriptorClasspath = "gov.va.vinci.Model";

  // =======================================================
  /**
   * Constructor FrameworkPipeline .
   *
   * @param pArgs Global arguments that would have scope over multiple pipeline
   *          components such as scale-out options, or inputs and outputs ...
   */
  // =======================================================
  public FrameworkPipeline(String[] pArgs) {

    this.args = pArgs;
    this.pipelineComponents = new ArrayList<PipelineComponent>();

    // ----------------------------
    // Set the pipeline implementation (it's a static setting within
    // FrameworkPipeline)
    // ----------------------------
    String _pipelineImplementation =
        U.getOption(pArgs, "--frameworkPipelineImplementation=", FrameworkPipeline.FIT);
    this.pipelineImplementation = _pipelineImplementation;

  } // end Constructor( pArgs) -----------------------------

  // =======================================================
  /**
   * getPipelineImplementation sets the underlying implementation to use the
   * uimaFIT way of creating a pipeline or the FLAP way of creating a pipeline.
   * 
   * @return pImplementation FIT|FLAP
   *
   *
   */
  // =======================================================
  public final String getPipelineIplementation() {
    return pipelineImplementation;
  } // end Method getPipelineImplementation() -------------

  // =======================================================
  /**
   * setImplementation sets the underlying implementation to use the uimaFIT way
   * of creating a pipeline or the FLAP way of creating a pipeline.
   * 
   * @param pImplementation FIT|FLAP
   */
  // =======================================================
  public final void setPipelineImplementation(String pImplementation) {
    this.pipelineImplementation = pImplementation;
  } // end Method setPipelineImplementation

  // =======================================================
  /**
   * getArgs returns the global arguments .
   *
   * @return String[]
   */
  // =======================================================
  public final String[] getArgs() {
    return args;
  } // end Method getArgs() --------------------------------

  // =======================================================
  /**
   * settArgs sets global arguments .
   *
   * @param pArgs the args
   */
  // =======================================================
  public final void setArgs(String[] pArgs) {
    this.args = pArgs;
  } // end Method setArgs() --------------------------------

  // =======================================================
  /**
   * getPipelineComponents retrieves the list of pipeline components.
   *
   * @return List<PipelineComponent>
   */
  // =======================================================
  public final List<PipelineComponent> getPipelineComponents() {
    return pipelineComponents;
  } // end Method getPipelineComponents() ------------------

  // =======================================================
  /**
   * add adds a pipeline annotator with its parameters to the pipeline.
   *
   * @param annotatorClassName the annotator class name
   */
  // =======================================================
  public final void add(String annotatorClassName) {

    PipelineComponent pipelineComponent = new PipelineComponent(annotatorClassName);

    this.pipelineComponents.add(pipelineComponent);

  } // End Method add() -----------------------------------

  // =======================================================
  /**
   * add adds a pipeline annotator with its parameters to the pipeline.
   *
   * @param annotatorClassName the annotator class name
   * @param parameters variable number of parameter strings in key=value pairs
   *          to be passed to the annotator
   */
  // =======================================================
  public final void add(String annotatorClassName, UimaContextParameter... parameters) {

    PipelineComponent pipelineComponent = new PipelineComponent(annotatorClassName, parameters);

    this.pipelineComponents.add(pipelineComponent);

  } // End Method add() -----------------------------------

  // =======================================================
  /**
   * add adds a pipeline annotator with its parameters to the pipeline.
   *
   * @param pNumberOfInstances the number of instances
   * @param annotatorClassName the annotator class name
   * @param parameters variable number of parameter strings in key=value pairs
   *          to be passed to the annotator
   */
  // =======================================================
  public final void add(int pNumberOfInstances, String annotatorClassName,
    UimaContextParameter... parameters) {

    PipelineComponent pipelineComponent = new PipelineComponent(annotatorClassName, parameters);
    pipelineComponent.setNumberOfInstances(pNumberOfInstances);

    this.pipelineComponents.add(pipelineComponent);

  } // End Method add() -----------------------------------

  // =================================================
  /**
   * add 
   * 
   * @param annotatorClassName
   * @param pParameters
   * @param pResources
  */
  // =================================================
 public final void add(String annotatorClassName, UimaContextParameter[] pParameters, UimaResource[] pResources) {
   
   PipelineComponent pipelineComponent = new PipelineComponent(annotatorClassName, pParameters, pResources);
   
   this.pipelineComponents.add(pipelineComponent);
    
  }

  // =================================================
  /**
   * add 
   * 
   * @param annotatorClassName
   * @param aeDescriptorConfigFileName
  */
  // =================================================
  public final void add(String annotatorClassName, String aeDescriptorConfigFileName ) {
    
    PipelineComponent pipelineComponent = new PipelineComponent(annotatorClassName, aeDescriptorConfigFileName );
    
    this.pipelineComponents.add(pipelineComponent);
    
  }

  // =======================================================
  /**
   * addAtBeginningg adds a pipeline annotator with its parameters to the
   * pipeline. This is useful as a mechanism to re-use an already defined
   * pipeline but add some pre-pipeline annotators to it.
   *
   * @param annotatorClassName the annotator class name
   * @param parameters variable number of parameter strings in key=value pairs
   *          to be passed to the annotator
   */
  // =======================================================
  public final void addAtBeginning(String annotatorClassName, UimaContextParameter... parameters) {

    PipelineComponent pipelineComponent = new PipelineComponent(annotatorClassName, parameters);

    this.pipelineComponents.add(0, pipelineComponent);

  } // End Method addAtBeginning() --------------------------

  // =======================================================
  /**
   * addAtBeginning adds a pipeline annotator with its parameters to the
   * beginning of the pipeline. This is useful as a mechanism to re-use an
   * already defined pipeline but add some pre-pipeline annotators to it.
   *
   * @param pNumberOfInstances the number of instances
   * @param annotatorClassName the annotator class name
   * @param parameters variable number of parameter strings in key=value pairs
   *          to be passed to the annotator
   */
  // =======================================================
  public final void addAtBeginning(int pNumberOfInstances, String annotatorClassName,
    UimaContextParameter... parameters) {

    PipelineComponent pipelineComponent = new PipelineComponent(annotatorClassName, parameters);
    pipelineComponent.setNumberOfInstances(pNumberOfInstances);

    this.pipelineComponents.add(0, pipelineComponent);

  } // End Method addAtBeginning() -----------------------------------

  // =======================================================
  /**
   * addAtEnd adds a pipeline annotator with its parameters to the pipeline.
   * This is useful as a mechanism to re-use an already defined pipeline but add
   * some post-pipeline annotators to it.
   * 
   * (This is a convenience method that is the same as the add method)
   *
   * @param annotatorClassName the annotator class name
   * @param parameters variable number of parameter strings in key=value pairs
   *          to be passed to the annotator
   */
  // =======================================================
  public final void addAtEnd(String annotatorClassName, UimaContextParameter... parameters) {

    PipelineComponent pipelineComponent = new PipelineComponent(annotatorClassName, parameters);

    this.pipelineComponents.add(pipelineComponent);

  } // End Method addAtBeginning() --------------------------

  // =======================================================
  /**
   * addAtEnd adds a pipeline annotator with its parameters to the end of the
   * pipeline. This is useful as a mechanism to re-use an already defined
   * pipeline but add some post-pipeline annotators to it.
   * 
   * (This is a convenience method that is the same as the add method)
   *
   * @param pNumberOfInstances the number of instances
   * @param annotatorClassName the annotator class name
   * @param parameters variable number of parameter strings in key=value pairs
   *          to be passed to the annotator
   */
  // =======================================================
  public final void addAtEnd(int pNumberOfInstances, String annotatorClassName, UimaContextParameter... parameters) {

    PipelineComponent pipelineComponent = new PipelineComponent(annotatorClassName, parameters);
    pipelineComponent.setNumberOfInstances(pNumberOfInstances);

    this.pipelineComponents.add(pipelineComponent);

  } // End Method addAtEnd() -----------------------------------

  // =======================
  /**
   * addRemoteAnnotator [Summary].
   *
   * @param pAnnotatorName the annotator name
   * @param pBrokerURL the broker URL
   * @param pServiceName the service name
   * @param pEndPointName the end point name
   * @param pArgs the args
   */
  // =======================
  public final void addRemoteAnnotator(String pAnnotatorName, String pBrokerURL, String pServiceName,
    String pEndPointName, String[] pArgs) {
    PipelineComponent pipelineComponent = new PipelineComponent(pAnnotatorName);
    pipelineComponent.setBrokerURL(pBrokerURL);
    pipelineComponent.setServiceName(pServiceName);
    pipelineComponent.setEndPointName(pEndPointName);
    pipelineComponent.setRemote(true);

    // ---------------------
    // Do I need to test it here and make it fail if it's not up?
    //

    this.pipelineComponents.add(pipelineComponent);

  }

  // =======================================================
  /**
   * getTypeDescriptorClasspath gets the type descriptor path
   * 
   * gets it in the form of "gov.va.vinci.Model" That is period delimiters, no
   * .xml extension
   * 
   * 
   * @return pTypeDescriptorClasspath
   *
   */
  // =======================================================
  public String getTypeDescriptorClasspath() {
    return this.typeDescriptorClasspath;

  } // End Method getTypeDescriptorClassPath() =============

  // =======================================================
  /**
   * setTypeDescriptorPath sets the type descriptor path
   * 
   * sets it in the form of "gov.va.vinci.Model" That is period delimiters, no
   * .xml extension
   *
   * @param pTypeDescriptorClasspath the type descriptor class path
   */
  // =======================================================
  public final void setTypeDescriptorClassPath(String pTypeDescriptorClasspath) {
    this.typeDescriptorClasspath = pTypeDescriptorClasspath;

  } // End Method setTypeDescriptorClassPath() =============

} // end Class FrameworkPipeline() ------------------------
