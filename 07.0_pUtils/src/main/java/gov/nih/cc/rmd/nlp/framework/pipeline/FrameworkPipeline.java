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
