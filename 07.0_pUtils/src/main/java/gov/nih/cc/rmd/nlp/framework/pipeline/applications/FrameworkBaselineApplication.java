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
 * BaseFrameworkApplication.java [Summary here]
 *
 * @author  guy
 * @created Oct 10, 2013
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import org.apache.uima.analysis_engine.AnalysisEngine;

import gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * The Class FrameworkBaselineApplication.
 */
public class FrameworkBaselineApplication extends FrameworkBaselineProcessor implements Runnable {

  // =======================================================
  /**
   * Constructor FrameworkBaselineApplication .
   */
  // =======================================================
  public FrameworkBaselineApplication() {

  }

  // =======================================================
  /**
   * Constructor FrameworkBaselineApplication .
   *
   * @param pArgs the args
   */
  // =======================================================
  public FrameworkBaselineApplication(String[] pArgs) {

    this.args = pArgs;
    this.inputDir = U.getOption(pArgs, "--inputDir=", "./");
    this.outputDir = U.getOption(pArgs, "--outputDir=", "./output/");
    this.logDir = U.getOption(pArgs, "--logDir=", this.outputDir + "./logs");

  }

  // =======================================================
  /**
   * Constructor FrameworkBaselineApplication .
   *
   * @param pMeter the meter
   */
  // =======================================================
  public FrameworkBaselineApplication(PerformanceMeter pMeter) {

    this.totalMeter = pMeter;
  }

  // =======================================================
  /**
   * Constructor FrameworkBaselineApplication .
   *
   * @param pAnalysisEngine the analysis engine
   */
  // =======================================================
  public FrameworkBaselineApplication(AnalysisEngine pAnalysisEngine) {

    this.analysisEngine = pAnalysisEngine;

  } // end Constructor() -------------------------------------

  // =======================================================
  /**
   * Constructor FrameworkBaselineApplication .
   *
   * @param pAnalysisEngine the analysis engine
   * @param pMeter the meter
   */
  // =======================================================
  public FrameworkBaselineApplication(AnalysisEngine pAnalysisEngine, PerformanceMeter pMeter) {

    this.analysisEngine = pAnalysisEngine;
    this.totalMeter = pMeter;

  } // end Constructor() -------------------------------------

  /* see superclass */
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    try {
      this.process(this.numberToProcess);
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "run", "What? " + e.toString());
    }
    // System.err.println( this.serverName + ": this thread should die now, it's
    // properly been processed");

  } // End Method run() ======================

  // -----------------------------------------
  // Private Global variables
  /** The singleton reader. */
  // ----------------------------------------
  protected Reader singletonReader = null;

  /** The input dir. */
  protected String inputDir = null;

 

  

} // End Class FrameworkBaselineApplication
