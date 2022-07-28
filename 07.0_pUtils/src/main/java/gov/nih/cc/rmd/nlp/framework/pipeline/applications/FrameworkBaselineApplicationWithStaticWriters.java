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
 * BaseFrameworkApplicationWithStaticWriters extends BaseFrameworkApplication 
 * to have static writers
 *
 * @author  guy
 * @created Sept 2, 2016
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.uima.analysis_engine.AnalysisEngine;

import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * The Class FrameworkBaselineApplicationWithStaticWriters.
 */
public class FrameworkBaselineApplicationWithStaticWriters extends FrameworkBaselineApplication {

  // =======================================================
  /**
   * Constructor FrameworkBaselineApplication .
   */
  // =======================================================
  public FrameworkBaselineApplicationWithStaticWriters() {
    super();
  }

  // =======================================================
  /**
   * Constructor FrameworkBaselineApplication .
   *
   * @param pArgs the args
   */
  // =======================================================
  public FrameworkBaselineApplicationWithStaticWriters(String[] pArgs) {
    super(pArgs);
  }

  // =======================================================
  /**
   * Constructor FrameworkBaselineApplication .
   *
   * @param pMeter the meter
   */
  // =======================================================
  public FrameworkBaselineApplicationWithStaticWriters(PerformanceMeter pMeter) {

    super(pMeter);
  }

  // =======================================================
  /**
   * Constructor FrameworkBaselineApplication .
   *
   * @param pAnalysisEngine the analysis engine
   */
  // =======================================================
  public FrameworkBaselineApplicationWithStaticWriters(AnalysisEngine pAnalysisEngine) {

    super(pAnalysisEngine);

  } // end Constructor() -------------------------------------

  // =======================================================
  /**
   * Constructor FrameworkBaselineApplication .
   *
   * @param pAnalysisEngine the analysis engine
   * @param pMeter the meter
   */
  // =======================================================
  public FrameworkBaselineApplicationWithStaticWriters(AnalysisEngine pAnalysisEngine,
      PerformanceMeter pMeter) {

    super(pAnalysisEngine, pMeter);

  } // end Constructor() -------------------------------------

  // =======================================================
  /**
   * addWriters.
   *
   * @param pArgs assumes that there is an --outputFormat= option that contains
   *          one or more of the values
   */
  // =======================================================
  public void addStaticWriters(String[] pArgs) {

    String outputFormat = U.getOption(pArgs, "--staticOutputFormat=", ""); // FrameworkBaselineWriters.STATS_WRITER_);

    String writerTypes[] = U.split(outputFormat, ":");

    if (writerTypes != null && writerTypes.length > 0 && writerTypes[0].length() > 0)
      for (String writerType : writerTypes) {
        this.addStaticWriter(writerType, pArgs);
      }

  } // End Method addWriters() ======================

  // =======================================================
  /**
   * createWriter creates the writers that will transform the processed cas's to
   * output files.
   *
   * @param pTypeOfWriter (see the enumeration of XXX_WRITERs
   * @param pArgs the args
   */
  // =======================================================
  public void addStaticWriter(String pTypeOfWriter, String[] pArgs) {

    int writerCode = FrameworkBaselineWriters.getTypeOfWriterCode(pTypeOfWriter);

    addStaticWriter(writerCode, pArgs);

  } // end Method addWriter() --------------------------

  // =======================================================
  /**
   * createWriter creates the writer that will transform the processed cas's to
   * output files.
   *
   * @param pTypeOfWriter (see the enumeration of XXX_WRITERs
   * @param pArgs the args
   */
  // =======================================================
  public void addStaticWriter(int pTypeOfWriter, String[] pArgs) {

    try {

      Writer writer = staticWriters.get(pTypeOfWriter);

      if (writer == null) {
        writer = createWriter(pTypeOfWriter, pArgs, 0);
        staticWriters.put(pTypeOfWriter, writer);
        staticWriterInstances.add(writer); // <------ needed to know what
                                           // writers to destroy per application

      }

      if (super.writers == null)
        super.writers = new HashSet<Writer>();

      super.writers.add(writer);

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "addWriter",
          "Issue with adding this writer " + e.toString());
    }

  } // end Method addWriter() --------------------------

  // =======================================================
  /**
   * destroy destroys the annotators and non static writers.
   */
  // =======================================================
  @Override
  public void destroy() {

    
    super.destroyAnnotators();
    
    if (this.writers != null) {
      for (Writer writer : this.writers) {
        if (writer != null)
          if (!staticWriterInstances.contains(writer)) {
            writer.destroy();
            writer = null;
          }
      }
    }
  } // end Method destroy() -------------------------------

  // =======================================================
  /**
   * destroy calls the static destroys (called once from .
   */
  // =======================================================
  public static void staticDestroy() {

    if (staticWriterInstances != null) {

      for (Writer writer : staticWriterInstances) {
        try {
          if (writer != null) {
            writer.destroy();
            writer = null;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      staticWriterInstances.clear();
    }

  } // End Method staticDestroy() ========================

  // =======================================================
  /**
   * getStaticWriters.
   *
   * @return List<Writer>
   */
  // =======================================================
  public HashMap<Integer, Writer> getStaticWriters() {
    return staticWriters;

  } // End Method getWriters() ===========================

  // -----------------------------------------
  // Private Global variables
  /** The static writers. */
  // ----------------------------------------
  private static HashMap<Integer, Writer> staticWriters = new HashMap<Integer, Writer>();

  /** The static writer instances. */
  private static HashSet<Writer> staticWriterInstances = new HashSet<Writer>();

} // End Class FrameworkBaselineApplication
