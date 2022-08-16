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
