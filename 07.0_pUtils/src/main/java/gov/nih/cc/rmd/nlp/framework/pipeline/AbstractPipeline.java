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
/*
 *
 */
/**
 * BasePipeline defines the two methods needed for each pipeline
 *
 * @author  Guy Divita
 * @created Jul 21, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline;

import org.apache.uima.analysis_engine.AnalysisEngine;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;

/**
 * The Class AbstractPipeline.
 */
public abstract class AbstractPipeline implements Runnable {

  // =======================================================
  /**
   * Constructor LinePipeline .
   *
   * @param pArgs the args
   */
  // =======================================================
  public AbstractPipeline(String[] pArgs) {

    this.args = pArgs;
  } // end Constructor -------------------------------------

  // =======================================================
  /**
   * Constructor LinePipeline .
   */
  // =======================================================
  public AbstractPipeline() {

  } // end Constructor -------------------------------------

  // =======================================================
  /**
   * setArgs .
   *
   * @param pArgs the args
   */
  // =======================================================
  public void setArgs(String[] pArgs) {
    this.args = pArgs;
  } // end Method setArgs ---------------------------------

  // =======================================================
  /**
   * createPipeline defines the pipeline components used. This method should be
   * fleshed out in classes that extend the BasePipeline class.
   *
   * @param pArgs the args
   * @return FrameworkPipeline
   */
  // =======================================================
  public abstract FrameworkPipeline createPipeline(String[] pArgs);

  // End Method createPipeline() ======================

  // =======================================================
  /**
   * getAnalysisEngine returns an analysis engine for this pipeline.
   *
   * @return AnalysisEngine
   * @throws Exception the exception
   */
  // =======================================================
  public AnalysisEngine getAnalysisEngine() throws Exception {

    AnalysisEngine ae = null;

    try {

      // -----------------------------
      // Create a pipeline
      // -----------------------------

      FrameworkPipeline pipeline = createPipeline(this.args);

      // ----------------------------
      // Create an aggregate engine from this pipeline
      // ----------------------------
      UIMAFitPipelineImplementation x = new UIMAFitPipelineImplementation();
      ae = x.createAggregateEngine(pipeline);

    } catch (Exception e) {
      e.getStackTrace();
      String msg = "Issue with creating the engine " + e.getMessage();
      GLog.println(GLog.STD___LEVEL, this.getClass(), "getAnalysisEngine", msg);
      throw new Exception(msg);
    }
    return ae;

  } // End Method getAnalysisEngine() ======================

  // =======================================================
  /**
   * run.
   */
  // =======================================================
  @Override
  public void run() {
    // n/a
  } // End Method run() ======================

  // ------------------------
  // Private Global Variables
  /** The args. */
  // ------------------------
  String[] args = null;

} // end Class BasePipeline
