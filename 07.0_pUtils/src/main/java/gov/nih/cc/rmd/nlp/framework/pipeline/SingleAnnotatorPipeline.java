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
 * Single annotator pipeline creates a pipeline around a single
 * annotator.  The annotator name gets passed in to be created.
 *
 * @author  Guy Divita
 * @created Sept 5, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline;

import org.apache.uima.analysis_engine.AnalysisEngine;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;

/**
 * The Class SingleAnnotatorPipeline.
 */
public class SingleAnnotatorPipeline extends AbstractPipeline {

  // =======================================================
  /**
   * Constructor LinePipeline .
   *
   * @param pArgs the args
   */
  // =======================================================
  public SingleAnnotatorPipeline(String[] pArgs) {

    this.args = pArgs;
  } // end Constructor -------------------------------------

  // =======================================================
  /**
   * Constructor LinePipeline .
   */
  // =======================================================
  public SingleAnnotatorPipeline() {
    super();
  } // end Constructor -------------------------------------

  // =======================================================
  /**
   * setArgs .
   *
   * @param pArgs the args
   */
  // =======================================================
  @Override
  public void setArgs(String[] pArgs) {
    this.args = pArgs;
  } // end Method setArgs ---------------------------------

  // =======================================================
  /**
   * createPipeline defines the pipeline components used. This method should be
   * fleshed out in classes that extend the BasePipeline class.
   *
   * @param pAnnotatorClassName the annotator class name
   * @param pArgs the args
   * @return FrameworkPipeline
   */
  // =======================================================
  public FrameworkPipeline createPipeline(String pAnnotatorClassName, String[] pArgs) {

    FrameworkPipeline pipeline = null;
    try {
      pipeline = new FrameworkPipeline(pArgs);

      UimaContextParameter argsParameter =
          new UimaContextParameter("args", pArgs, "String", true, false);

      // -----------------------------
      // Add pipeline components to the pipeline
      // -----------------------------
      try {
        pipeline.add(pAnnotatorClassName, argsParameter);
        GLog.println(GLog.STD___LEVEL, this.getClass(), "createPipeline",
            "Added the " + pAnnotatorClassName + " to the single annotator applicaiton ");
      } catch (Exception e) {
        e.printStackTrace();
        String msg = "Issue defining and adding " + pAnnotatorClassName
            + " annotator to the single Annotator Application " + e.toString();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createPipeline", msg);
        throw new RuntimeException(msg);
      }

    } catch (Exception e2) {
      e2.printStackTrace();
      String msg =
          "Issue creating a pipeline within the single annotator application " + e2.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createPipeline", msg);
      throw new RuntimeException(msg);
    }
    return pipeline;

  } // End Method createPipeline() ======================

  /* see superclass */
  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.va.vinci.nlp.framework.pipeline.AbstractPipeline#createPipeline(java.
   * lang.String[])
   * 
   * @
   */
  @Override
  public FrameworkPipeline createPipeline(String[] pArgs) {

    return null;
    // End Method createPipeline() ======================
  }

  // =======================================================
  /**
   * getAnalysisEngine returns an analysis engine for this pipeline.
   *
   * @param pAnnotatorClassName the annotator class name
   * @return AnalysisEngine
   * @throws Exception the exception
   */
  // =======================================================
  public AnalysisEngine getAnalysisEngine(String pAnnotatorClassName) throws Exception {

    AnalysisEngine ae = null;

    try {

      // -----------------------------
      // Create a pipeline
      // -----------------------------

      FrameworkPipeline pipeline = createPipeline(pAnnotatorClassName, this.args);

     
      // ----------------------------
      // Create an aggregate engine from this pipeline
      // ----------------------------
      UIMAFitPipelineImplementation x = new UIMAFitPipelineImplementation();
      ae = x.createAggregateEngine(pipeline);

    } catch (Exception e) {
      e.getStackTrace();
      String msg = "Issue with creating the engine " + e.getMessage();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getAnalysisEngine", msg);
      throw new Exception(msg);
    }
    return ae;

  } // End Method getAnalysisEngine() ======================

  

} // end Class BasePipeline
