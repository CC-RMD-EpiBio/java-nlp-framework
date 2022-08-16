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
