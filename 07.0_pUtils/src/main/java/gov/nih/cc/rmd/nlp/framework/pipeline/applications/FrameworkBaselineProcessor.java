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
 * BaseFrameworkProcessor
 *
 * @author  divita
 * @created Sept 3, 2016
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;



import gov.nih.cc.rmd.nlp.framework.pipeline.ScaleOutAnnotatorPerformanceStats;
import gov.va.chir.model.DocumentHeader;
// import gov.va.vinci.cm.Document;

import gov.nih.cc.rmd.nlp.framework.marshallers.evaluate.EvaluateWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.frameworkObject.FrameworkObjectWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.html.HTMLWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader;
import gov.nih.cc.rmd.nlp.framework.marshallers.string.FromStringReader;
import gov.nih.cc.rmd.nlp.framework.marshallers.uima.UimaWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.vtt.ToVTT;
import gov.nih.nlm.nls.vtt.model.VttDocument;    // <---- needed to get vtt dependency explicit 
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;
import gov.nih.cc.rmd.nlp.framework.marshallers.xmi.ToXMI;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;

/**
 * Framework Baseline Processor.
 */
public class FrameworkBaselineProcessor extends FrameworkBaselineWriters {

  // =======================================================
  /**
   * destroy calls the destroy on the analysis engine and calls the destroy for
   * each of the writers.
   */
  // =======================================================
  public void destroy() {

    destroyWriters();
    

    destroyAnnotators();
   

    // ---------------------
    // Write out the last performance meter
    if (this.totalMeter != null) {
      this.totalMeter.stop("Final Meter Performance");
      // this.totalMeter.close();
      // this.totalMeter = null;
    }

    // ------------------
    // Make sure everyone knows you are not dead
    this.alive = false;
    ScaleOutAnnotatorPerformanceStats.analyze(this.logDir);

    // -----------------------
    // Cleanup
    Runtime.getRuntime().gc();

  } // End Method destroy() ==============================

  // =================================================
  /**
   * destroyWriters 
   * 
  */
  // =================================================
   public final void destroyWriters() {
    if (this.writers != null) {
      for (Writer writer : this.writers) {
        try {
          if ( writer != null )
            writer.destroy();
        } catch (Exception e) {
          e.printStackTrace();
          GLog.println(GLog.ERROR_LEVEL, this.getClass(), "destroy",
              "In destroy, one of the writer destroys failed");
        }
        writer = null;
      }
      this.writers = null;
    }
  } // end Method destroyWriters() -----------------

  // =================================================
  /**
   * destroyAnnotators 
   * 
  */
  // =================================================
  public final void destroyAnnotators() {
    // --------------------
    // Call the annotator destroys
    if (this.analysisEngine != null) {
      try {
        this.analysisEngine.destroy(); // <--- does this call each writer's
                                       // destroy?
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "destroy",
            "The analysis Engine threw a screw " + e.toString());
      }
      // this.analysisEngine = null;

    }
    
  } // End Method destroyAnnotators() -----------------

  /**
   * Destroy gentle.
   *
   * @return the string
   */
  // =================================================
  /**
   * destroyAndReport
   * 
   * @return String
   */
  // =================================================
  public String destroyAndReport() {

    String returnVal = null;
    if (this.writers != null) {
      for (Writer writer : this.writers) {
        try {

          if (writer.getClass().getSimpleName().startsWith("Evaluate")) {
            returnVal = ((EvaluateWriter) writer).report();
          } else {
            writer.destroy();
          }
        } catch (Exception e) {
          e.printStackTrace();
          GLog.println(GLog.ERROR_LEVEL, this.getClass(), "destroy",
              "In destroy, one of the writer destroys failed");
        }
        writer = null;
      }
      this.writers = null;
    }

    // --------------------
    // Call the annotator destroys
    if (this.analysisEngine != null) {
      try {
        this.analysisEngine.destroy(); // <--- does this call each writer's
                                       // destroy?
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "destroy",
            "The analysis Engine threw a screw " + e.toString());
      }
      // this.analysisEngine = null;

    }

    // ---------------------
    // Write out the last performance meter
    if (this.totalMeter != null) {
      this.totalMeter.stop("Final Meter Performance");
      // this.totalMeter.close();
      // this.totalMeter = null;
    }

    // ------------------
    // Make sure everyone knows you are not dead
    this.alive = false;
    ScaleOutAnnotatorPerformanceStats.analyze(this.logDir);

    // -----------------------
    // Cleanup
    Runtime.getRuntime().gc();

    return returnVal;

  } // end Method destroyAndReport() ----------------------

  /**
   * Destroy gentle.
   *
   * @param pWhy the why
   */
  public synchronized void destroyGentle(String pWhy) {

    this.alive = false;
    String msg = "Gentle eath called upon for " + this.serverName + "\n";

    if (!this.destroyCalled) {
      this.destroyCalled = true;
      try {
        // GLog.println(GLog.ERROR_LEVEL,this.getClass(), aMethod,Calling the
        // application destroy outside the process ");
        System.err.println(" Really killing this service " + this.getServerName());
        this.destroy();
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "destroyGentle",
            "Issue with calling destroy outside run " + e.toString());
      }
    } else {
      // System.err.println("we think this has been destroyed once, don't do it again for " + this.getServerName());
    }

    if (this.totalMeter != null)
      this.totalMeter.mark(msg);
    // GLog.println(GLog.ERROR_LEVEL,msg);

  } // End Method destroyGentle() ======================

  // =======================================================
  /**
   * setEngine.
   * 
   * @param pAe the analsyis engine
   */
  // =======================================================
  public void setAnalsyisEngine(AnalysisEngine pAe) {
    this.analysisEngine = pAe;
    GLog.println(GLog.STD___LEVEL, this.getClass(), "setAnalysisEngine", "added pipeline");

  } // End Method setEngine() =============================

  // =======================================================
  /**
   * getEngine.
   * 
   * @return AnalysisEngine
   */
  // =======================================================
  public AnalysisEngine getAnalysisEngine() {
    return this.analysisEngine;
  } // End Method getEngine() =============================

  // =======================================================
  /**
   * addPerformanceMeter adds a performance Meter to keep track of time and
   * memory.
   * 
   * @param pMeter the meter
   */
  // =======================================================
  public void addPerformanceMeter(PerformanceMeter pMeter) {
    this.totalMeter = pMeter;
    // End Method addPerformanceMeter() ======================
  }

  // =======================================================
  /**
   * process loops through the input files, processes the files and writes the
   * processed files out via the writers.
   * 
   * @param pNumberToProcess2 the number to process 2
   */
  // =======================================================
  public void process(long pNumberToProcess2) {

    // ------------------
    // gather and process the cas's
    // -----------------
    if (this.totalMeter == null)
      this.totalMeter = FrameworkBaselineProcessor.createPerformanceMeter(this.args);

    this.totalMeter.begin("After initialization, starting meter");

    long numberToProcess2 = pNumberToProcess2 == -1 ? Long.MAX_VALUE : pNumberToProcess2;

    int metric = Integer.parseInt(U.getOption(this.args, "--metric=", "1000"));
    this.totalMeter.setMetric(metric);
    try {
      this.alive = true;

      // ---------------------------------
      // Set writer parameters -
      // ---------------------------------
      // setWriterParameters();

      String msg = "======================xxxx============================\n" + "The server name = "
          + this.serverName + " gonna process " + numberToProcess2 + "\n"
          + "======================xxxx============================\n";
      GLog.println(GLog.STD___LEVEL, this.getClass(), "process", msg);

      JCas jCas = null;
      while (this.isAlive()) {

        try {
          jCas = getNext(this.singletonReader, filesProcessed, numberToProcess2, this.totalMeter);

        } catch (Exception e) {
          e.printStackTrace();
          GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process",
              "Issue here 0 : " + e.toString());
          jCas = null;
          continue;
        }
        // ------------------
        // Process each cas
        // ------------------
        try {
          if (jCas != null && jCas.getDocumentText() != null && jCas.getDocumentText().length() > 0
              && !jCas.getDocumentText().equals(Reader.NO_TEXT))
            this.analysisEngine.process(jCas);
          else {
            // GLog.println(this.serverName + " Should be here at some point ");
            continue;
          }

        } catch (Exception e4) {
          e4.printStackTrace();
          msg = this.serverName + ": Issue with one of the annotators " + e4.toString();
          GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
          totalMeter.mark(msg);

          try {
            msg = this.serverName + ": |========= Record Begin =======================|\n"
                + jCas.getDocumentText() + "\n|================== Record End ================|\n";

            totalMeter.mark(msg);
            GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
          } catch (Exception e6) {
            jCas = null;
          }

          try {
            msg =
                this.serverName + ": The offending document id = " + VUIMAUtil.getDocumentId(jCas);

            totalMeter.mark(msg);
            GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
            jCas = null;
          } catch (Exception e5) {
            msg = this.serverName
                + ": Issue with such a bad document that it doen't have a documentID ";

            totalMeter.mark(msg);
            GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
            jCas = null;
          }

          jCas = null;

        } // end dealing with exception e4 --------------

        if (jCas != null) {
          try {
            // -----------------
            // Write out the cas with the writers
            // -----------------
            if (this.writers != null) {

              try {

                this.write(jCas);
              } catch (Exception e) {
                e.printStackTrace();
                GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process",
                    "aMethod,One of the writers failed " + e.toString());
              }

            }

            this.filesProcessed++;

          } catch (Exception e1) {
            e1.printStackTrace();
            msg = this.serverName + ": Issue with writing the file " + e1.getMessage() + "\n";
            GLog.println(GLog.ERROR_LEVEL, msg);
            totalMeter.mark(msg);

          }

          // Universally set this record as processed
          try {
            long recordId = VUIMAUtil.getRecordId(jCas);
            this.singletonReader.setProcessed(recordId); // <---- synchronized
          } catch (Exception e3) {
            e3.printStackTrace();
            GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process",
                "Issue 3 here " + e3.toString());
          }
        }

        try {

          if (totalMeter != null)
            totalMeter.processedAnother();
        } catch (Exception e4) {
          e4.printStackTrace();
          GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process",
              "Issue 4 here " + e4.toString());
        }

        // -----------------------------------
        // Check to see if you need to recycle this
        try {
          if (this.filesProcessed > this.recycleAt) {
            this.alive = false;
            msg = this.serverName + ": Recycle threshold met - recycling this thread "
                + this.filesProcessed + "|" + this.recycleAt + "\n";
            totalMeter.mark(msg);

            GLog.println(GLog.STD___LEVEL, this.getClass(), "process", msg);
            break;
          }

        } catch (Exception e5) {
          e5.printStackTrace();
          GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process",
              "Issue 5 here " + e5.toString());
        }

      } // end loop through each of the files ---------------

      msg = this.serverName + ": Finished: Final input Files processed: " + this.filesProcessed
          + "\n";
      this.totalMeter.mark(msg);

      if (this.totalMeter != null)
        this.totalMeter.mark(msg); // an external hand could destroy the
                                   // applicaition
      GLog.println(GLog.STD___LEVEL, this.getClass(), "process", msg);
      // -------------------
      // Call the writer's destroy() method to clean things up
      this.alive = false;

      try {
        if (!this.destroyCalled)
          this.destroy();
        this.destroyCalled = true;
      } catch (Exception e5) {
        e5.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process",
            "Issue with calling the destroy " + e5.toString());
      }

    } catch (Exception e4) {
      e4.printStackTrace();
      String msg =
          this.serverName + ": Issue with looping through the input docuements " + e4.getMessage();
      if (this.totalMeter != null)
        this.totalMeter.mark(msg);
      this.totalMeter.mark(msg);
      GLog.println(GLog.STD___LEVEL, this.getClass(), "process", msg);

    }
  } // End Method process() ======================

  // =================================================
  /**
   * setWriterParameters
   * 
   *
   * // ================================================= private void
   * setWriterParameters() {
   * 
   * if ( this.writers != null && !this.writers.isEmpty() ) for ( Writer aWriter
   * : this.writers ) try { setWriterParameters( aWriter); } catch ( Exception
   * e) { e.printStackTrace(); GLog.println(GLog.ERROR_LEVEL, "Issue with
   * setting the configuration parameters " + e.toString()); } } // End Method
   * setWriterParameters() --------------
   *
   * @param pSingletonReader the singleton reader
   * @param pFilesProcessed the files processed
   * @param numberToProcess2 the number to process 2
   * @param pProcessMeter the process meter
   * @return the next
   */

  // =================================================
  /**
   * setWriterParameters Standard UIMA writers pick up the parameters from the
   * UIMAcontext (off the analysisEngine) But ... framework does not have a
   * mechanism to set parameters for the writers and does not have a mechanism
   * for passing parameters that had been set into framework writers.
   * 
   * To get around this, this method adds parameters to the umiaContext so that
   * the writers can pull specific parameters to use.
   * @throws Exception
   * 
   *           [TBD] too new - cause of a circularity pulled for now
   * 
   * 
   *           // ================================================= final void
   *           setWriterParameters( Writer pWriter) throws Exception {
   * 
   *           List<UimaContextParameter> parameters = pWriter.getParameters();
   * 
   *           if (parameters != null && !parameters.isEmpty()) { for
   *           (UimaContextParameter parameter : parameters) { try {
   *           this.analysisEngine.setConfigParameterValue( parameter.getName(),
   *           parameter.getValue());
   * 
   *           } catch (Exception e) { e.printStackTrace(); String msg = "Issue
   *           with adding paramter to the analysis engine for a writer within
   *           the framework process " + e.toString(); throw new Exception (
   *           msg); } } // end loop through parameters } // end if there are
   *           any parameters
   * 
   * 
   *           } // end Method setWriterParameters() -------------------
   */

  // =======================================================
  /**
   * getNext
   * 
   * @param pSingletonReader
   * @param pFilesProcessed
   * @param numberToProcess2 NOTE: this now has to be > 0 and has to be set!
   * @return JCas
   */
  // =======================================================
  private synchronized JCas getNext(Reader pSingletonReader, int pFilesProcessed,
    long numberToProcess2, PerformanceMeter pProcessMeter) {

    JCas jCas = null;

    try {
      if (this.isAlive() && pSingletonReader.hasNext()) {
        if (pFilesProcessed < numberToProcess2) {

          jCas = this.analysisEngine.newJCas();

          try {
            if (jCas != null) {
              singletonReader.getNext(jCas.getCas());
              if (jCas.getDocumentText() == null) {
                @SuppressWarnings("unused")
                String msg = this.serverName + ":  ==== empty document text jcas from a reader !\n";
                // pProcessMeter.mark( msg);
                // System.out.print(msg);

              }
            }
          } catch (Exception e) {
            e.printStackTrace();
            String msg = this.serverName + ": Serious and fatal Issue reading the next record "
                + e.toString() + "\n";
            GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getNext", msg);
            pProcessMeter.mark(msg);
            throw new Exception(msg);
          }
        } else {
          this.alive = false;
          String msg = this.serverName + ": There are no more records to process. Processed "
              + pFilesProcessed + " of " + numberToProcess2 + "\n";
          GLog.println(GLog.STD___LEVEL, this.getClass(), "getNext", msg);
          pProcessMeter.mark(msg);
        }
      } else {
        this.alive = false;
        String msg = this.serverName
            + ": Has Next came back FALSE. There are no more records to process. Processed "
            + pFilesProcessed + " of " + numberToProcess2 + "\n";
        GLog.println(GLog.STD___LEVEL, this.getClass(), "getNext", msg);
        pProcessMeter.mark(msg);
      }
    } catch (Exception e) {
      e.printStackTrace();
      String msg = this.serverName + ": Issue reading the next record " + e.toString() + "\n";
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getNext", msg);
      pProcessMeter.mark(msg);
      // throw new Exception (msg);
    }
    return jCas;
  } // End Method getNext() ======================

  // =======================================================
  /**
   * process processes X number of records.
   */
  // =======================================================
  public void process() {

    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process",
        "------generic process ---------------------\n");
    this.process(Integer.MAX_VALUE);
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process",
        "-----finished generic process ------------\n");
  } // End Method process() ======================

 
  // =======================================================
  /**
   * process takes a string as input, processes it, and sends the output out as
   * a string of X.
   * 
   * @param pCommonModelDocument the common model document
   * @return String
   */
  // =======================================================
  // public gov.va.vinci.cm.Document processOBSS(Document pCommonModelDocument) {

    /* 
    Document returnDoc = null;
    // -----------------
    // convert the common model document to a cas
    // -----------------
    if (this.commonModelReader == null)
      this.commonModelReader =
          new gov.va.vinci.nlp.framework.marshallers.commonModel.util.FromCommonModel();

    JCas jCas = null;
    try {
      jCas = this.analysisEngine.newJCas();

      this.commonModelReader.fromCommonModel(pCommonModelDocument, jCas);

      // ------------------
      // Process each cas
      // ------------------
      try {

        if (jCas != null && jCas.getDocumentText() != null && jCas.getDocumentText().length() > 0
            && !jCas.getDocumentText().equals(Reader.NO_TEXT))
          this.analysisEngine.process(jCas);
        this.filesProcessed++;

        // ----------------
        // Convert back to a new common model
        if (this.commonModelWriter == null)
          this.commonModelWriter =
              new gov.va.vinci.nlp.framework.marshallers.commonModel.util.ToCommonModel();
        returnDoc = this.commonModelWriter.convert(jCas);

      } catch (Exception e1) {
        e1.printStackTrace();
        String msg = "Issue with processing the file " + e1.getMessage();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
      }
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue converting the common model into a cas " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);

    }

    return returnDoc;

  } // end Method process() ------------------------------
*/

  // =======================================================
  /**
   * process processes the records pointed to by these record ids.
   * 
   * @param pRecordIds the record ids
   */
  // =======================================================
  public void process(int[] pRecordIds) {

    // ------------------
    // gather and process the cas's
    // -----------------

    this.alive = true;
    JCas jCas = null;

    for (int i = 0; i < pRecordIds.length; i++) {

      try {
        jCas = this.analysisEngine.newJCas();

        this.singletonReader.get(jCas.getCas(), pRecordIds[i]);
      } catch (Exception e2) {
        e2.printStackTrace();
        String msg = "Issue reading record " + pRecordIds[i] + " " + e2.toString();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
        continue;
      }
      // ------------------
      // Process each cas
      // ------------------
      try {
        if (jCas != null && jCas.getDocumentText() != null && jCas.getDocumentText().length() > 0
            && !jCas.getDocumentText().equals(Reader.NO_TEXT))
          this.analysisEngine.process(jCas);
        this.filesProcessed++;

      } catch (Exception e4) {
        e4.printStackTrace();
        String msg = "Issue with one of the annotators " + e4.toString();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
      }

      try {
        // -----------------
        // Write out the cas with the writers
        // -----------------
        if (this.writers != null)
          this.write(jCas);

      } catch (Exception e1) {
        e1.printStackTrace();
        String msg = "Issue with writing the file " + e1.getMessage();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
      }

    } // end loop through the record Ids

    if (!this.destroyCalled)
      try {
        this.destroyCalled = true;
        this.destroy();
      } catch (Exception e5) {
        e5.printStackTrace();
        String msg = "Issue with writing the file " + e5.getMessage();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
      }

  } // End Method process() ======================

  // =======================================================
  /**
   * process processes a jCas.
   * 
   * @param pJCas the j cas
   * @return the j cas
   */
  // =======================================================
  public JCas process(JCas pJCas) {

    try {

      this.analysisEngine.process(pJCas);

    } catch (Exception e4) {
      e4.printStackTrace();
      String msg = "Issue with process jcas " + e4.toString();
      GLog.println(GLog.ERROR_LEVEL, msg);
    }

    return pJCas;

  } // End Method process() ======================

  
  // =======================================================
  /**
   * process takes a string as input, processes it, and sends the output out as
   * a string of X.
   * 
   * @param pInputText the input text
   * @return String
   */
  // =======================================================
  // public gov.va.vinci.cm.Document processToCommonModelObss(String pInputText) {
/*
    
    gov.va.vinci.cm.Document document = null;

    // ------------------
    // Put this into a file
    //
    try {
      String inputFileName = createInputFile(pInputText);

      JCas jCas = null;
      try {
        jCas = this.analysisEngine.newJCas();

        try {

          boolean seen = false;
          while (!seen && singletonReader.hasNext()) {

            // this.meter.mark("Getting the cas ");
            singletonReader.getNext(jCas.getCas()); // I'm assuming that the
                                                    // next file is what was
                                                    // just put into the dir

            String aFileName = VUIMAUtil.getDocumentId(jCas);
            if (aFileName.contains(inputFileName))
              seen = true;
          }
          // this.meter.mark("Got the cas ");

          // ------------------
          // Process each cas
          // ------------------
          try {
            if (jCas != null && jCas.getDocumentText() != null
                && jCas.getDocumentText().length() > 0
                && !jCas.getDocumentText().equals(Reader.NO_TEXT))
              this.analysisEngine.process(jCas);
            this.filesProcessed++;

            // -----------------
            // convert the cas to a common model document
            // -----------------
            if (this.commonModelWriter == null)
              this.commonModelWriter =
                  new gov.va.vinci.nlp.framework.marshallers.commonModel.util.ToCommonModel();

            document = this.commonModelWriter.convert(jCas);

          } catch (Exception e1) {
            e1.printStackTrace();
            String msg = "Issue with processing the file " + e1.getMessage();
            GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processToCommonModel", msg);
          }
        } catch (Exception e2) {
          e2.printStackTrace();
          String msg = "Issue processing " + e2.toString();
          GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processToCommonModel", msg);
        }

      } catch (Exception e3) {
        e3.printStackTrace();
        String msg = "Issue processing " + e3.toString();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processToCommonModel", msg);
      }

    } catch (Exception e4) {
      e4.printStackTrace();
      String msg = "Issue processing " + e4.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processToCommonModel", msg);
    }

    return document;
   
   // End Method processToCommonModel() ======================
*/
  
  // =======================================================
  /**
   * processTo processes the document and converts it to the passed in format.
   * 
   * @param pFormat |commonModel|vtt|xmi
   * @param pInputText the input text
   * @return String
   */
  // =======================================================
  public String processTo(String pFormat, String pInputText) {

    String returnVal = null;

    JCas jCas = null;
    try {
      jCas = this.analysisEngine.newJCas();

      try {

        jCas.setDocumentText(pInputText);
        createDocumentHeaderAnnotation(jCas, pInputText);

        // ------------------
        // Process each cas
        // ------------------
        try {

          if (jCas != null && jCas.getDocumentText() != null && jCas.getDocumentText().length() > 0
              && !jCas.getDocumentText().equals(Reader.NO_TEXT))
            this.analysisEngine.process(jCas);
          this.filesProcessed++;

          switch (pFormat) {

            /*
            case COMMON_MODEL_XML_STRING:
              if (this.commonModelWriter == null)
                this.commonModelWriter =
                    new gov.va.vinci.nlp.framework.marshallers.commonModel.util.ToCommonModel();

              returnVal = this.commonModelWriter.processToXML(jCas);

              break;
              */
            case XMI_XML_STRING:
              if (this.xmiWriter == null)
                this.xmiWriter = new ToXMI();
              returnVal = this.xmiWriter.processToXML(jCas);
              break;

           
        
            case VTT_STRING_OBJECT:
            case STRING_OBJECT:
              if ( this.vttWriter == null)
                this.vttWriter = new ToVTT(this.args);
              returnVal = this.vttWriter.processToString(jCas)  ;
              
              break;
              
            case VTT_PIPE_STRING:
              if ( this.vttWriter == null)
                this.vttWriter = new ToVTT(this.args);
              returnVal = this.vttWriter.processToPipedString(jCas);
              
              break;
              
            default:
              break;
          }

        } catch (Exception e1) {
          e1.printStackTrace();
          String msg = "Issue with processing the file " + e1.getMessage();
          GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processTo", msg);
        }
      } catch (Exception e2) {
        e2.printStackTrace();
        String msg = "Issue processing " + e2.toString();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processTo", msg);
      }

    } catch (Exception e3) {
      e3.printStackTrace();
      String msg = "Issue processing " + e3.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processTo", msg);
    }

    return returnVal;

  } // End Method processTo() ======================

  
  

  // =================================================
  /**
   * processToHTML takes the text, and document metadata and processes it. It
   * returns
   *
   * @param pInputText the input text
   * @param pDocumentMetaData the document meta data
   * @return JsonDocumentAnnotations
   */
  // =================================================
  public String processToHTML(String pInputText ) {

    String returnVal = null;

    returnVal = (String) processAPI(HTML_OBJECT, pInputText );

    return returnVal;

  } // end Method processToJson() --------------------

  // =======================================================
  /**
   * processAPI processes the document text and converts it to the passed in
   * format object.
   * 
   * This converts the cas to other representations such as a bioC document,
   * common model document to be further processed outside the confines of UIMA.
   * 
   * @param pFormat COMMON_MODEL_OBJECT|VTT_OBJECT
   * @param pInputText the input text
   * @return Object an instance of one of the above document types
   */
  // =======================================================
  public Object processAPI(String pFormat, String pInputText) {

    return processAPI(pFormat, pInputText, null);

  } // end Method processAPI() -------------------------------
  
  
  // =======================================================
  /**
   * processAPI processes the document text and converts it to the passed in
   * format object.
   * 
   * This converts the cas to other representations such as a bioC document,
   * common model document to be further processed outside the confines of UIMA.
   * 
   * @param pFormat COMMON_MODEL_OBJECT|VTT_OBJECT
   * @param pInputText the text
   * @param pSectionType - process as if the input is of this section type
   * @param pDocumentMetaData the metadata
   * @return Object an instance of one of the above document types
   */
  // =======================================================
  public Object processAPI(String pFormat, String pInputText, String pSectionType ) {
    
    return processAPI( pFormat, pInputText, pSectionType, null);
    
  } // end Method processAPI() -----------------------------

  // =======================================================
  /**
   * processAPI processes the document text and converts it to the passed in
   * format object.
   * 
   * This converts the cas to other representations such as a bioC document,
   * common model document to be further processed outside the confines of UIMA.
   * 
   * @param pFormat COMMON_MODEL_OBJECT|VTT_OBJECT
   * @param pInputText the text
   * @param pSectionType - process as if the input is of this section type
   * @param pDocumentMetaData the metadata
   * @return Object an instance of one of the above document types
   */
  // =======================================================
  public Object processAPI(String pFormat, String pInputText, String pSectionType, String pOutputTypes ) {

    Object returnVal = null;

    JCas jCas = null;
    try {
      jCas = this.analysisEngine.newJCas();

      try {

        jCas.setDocumentText(pInputText);
        createDocumentHeaderAnnotation(jCas, pInputText , pSectionType );

        // ------------------
        // Process each cas
        // ------------------
        try {

          if (jCas != null && jCas.getDocumentText() != null && jCas.getDocumentText().length() > 0
              && !jCas.getDocumentText().equals(Reader.NO_TEXT))
            this.analysisEngine.process(jCas);
          this.filesProcessed++;

          switch (pFormat) {

            /*
            case COMMON_MODEL_OBJECT:
              if (this.commonModelWriter == null)
                this.commonModelWriter =
                    new gov.va.vinci.nlp.framework.marshallers.commonModel.util.ToCommonModel();

              returnVal = this.commonModelWriter.convert(jCas);

              break;
              */
            case VTT_OBJECT:
              if (this.vttWriter == null) 
                this.vttWriter = new ToVTT(this.outputDir, ToVTT.DEFAULT_OUTPUT_TYPES);
              returnVal = this.vttWriter.processToVttDoc(jCas);
              break;

            case STRING_OBJECT:
            case VTT_STRING_OBJECT:
              if (this.vttWriter == null)
                this.vttWriter = new ToVTT(this.outputDir, ToVTT.DEFAULT_OUTPUT_TYPES);
              returnVal = this.vttWriter.processToString(jCas);
              break;

              
            case FRAMEWORK_OBJECT:
              if (this.frameworkObjectWriter == null)
                this.frameworkObjectWriter = new FrameworkObjectWriter();
              returnVal = this.frameworkObjectWriter.processToFrameworkObject(jCas, pOutputTypes);
              break;  
          
            case HTML_OBJECT:
              if (this.htmlWriter == null) {
                this.htmlWriter = new HTMLWriter(this.args);
              }
              returnVal = this.htmlWriter.processApi(jCas);
              break;

              
            case UIMA_OBJECT:
              if (this.uimaWriter == null) {
                this.uimaWriter = new UimaWriter(this.args);
              }
              returnVal = this.uimaWriter.processToCas(jCas);
              break;

           

            case EVALUATE_OBJECT:
              if (this.evaluateWriter == null) {
                this.evaluateWriter = new EvaluateWriter(this.args);
              }
              this.evaluateWriter.process(jCas);

              break;
            default:
              break;
          }

        } catch (Exception e1) {
          e1.printStackTrace();
          String msg = "Issue with processing the file " + e1.getMessage();
          GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processAPI", msg);
        }
      } catch (Exception e2) {
        e2.printStackTrace();
        String msg = "Issue processing " + e2.toString();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processAPI", msg);
      }

    } catch (Exception e3) {
      e3.printStackTrace();
      String msg = "Issue processing " + e3.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processAPI", msg);
    }

    return returnVal;

  } // End Method processAPI() ======================

  
  // =================================================
  /**
   * createDocumentHeaderAnnotation .
   * 
   * @param pJCas the j cas
   * @param pInputText the input text
 
   * @param pDocumentMetaData the document meta data
   */
  // =================================================
  // private void createDocumentHeaderAnnotation(JCas pJCas, String pInputText,
  // DocumentMetadata pDocumentMetaData) {
  //
  // createDocumentHeaderAnnotation( pJCas, pInputText, null,
  // pDocumentMetaData);
  //
  // }
  
  
  // =================================================
  /**
   * createDocumentHeaderAnnotation .
   * 
   * @param pJCas the j cas
   * @param pInputText the input text
   * @param pSectionType  process the document as if it is all from this section type
   * @param pDocumentMetaData the document meta data
   */
  // =================================================
  private void createDocumentHeaderAnnotation(JCas pJCas, String pInputText, String pSectionType ) {

    String documentType = "fromAPI";
    String documentName = "CiitizenAPI_" + docIdCounter++;
    String documentId = documentName;
    String documentReferenceDate = new Date(0).toString();
    String documentEventDate = null;
    String documentTitle = "generic";
    String otherMetaDataFieldNames = "pageNumber|numberOfPages";
    String otherMetaData = "1|1";

   
      int pageNumber = 1;
      int numberOfPages = 1;
    //  Map<String, String> attributes = pDocumentMetaData.getAttributes();
    // if ( attributes != null && !attributes.isEmpty())
      otherMetaData = pageNumber + "|" + numberOfPages;
    
        
    

    DocumentHeader documentHeader = new DocumentHeader(pJCas);

    documentHeader.setDocumentId(documentName);
    documentHeader.setDocumentId(documentId);
    documentHeader.setDocumentName(documentName);
    documentHeader.setBegin(0);
    documentHeader.setEnd(pInputText.length());
    documentHeader.setDocumentType(documentType);
    documentHeader.setDocumentTitle(documentTitle);
    documentHeader.setReferenceDate(documentReferenceDate);
    documentHeader.setSectionType( pSectionType);
    documentHeader.setOtherMetaDataFieldNames( otherMetaDataFieldNames);
    documentHeader.setOtherMetaData( otherMetaData );
    documentHeader.setEventDate( documentEventDate);

    documentHeader.addToIndexes(pJCas);

  } // end Method createDocumentHeaderAnnotation() -----

  // ------------------------------------------
  /**
   * createDocumentHeaderAnnotation.
   *
   * @param pJCas the j cas
   * @param pText the text
   */
  // ------------------------------------------
  private void createDocumentHeaderAnnotation(JCas pJCas, String pText) {

    DocumentHeader documentHeader = new DocumentHeader(pJCas);

    String documentName = "X_" + docIdCounter++;
    documentHeader.setDocumentId(documentName);
    documentHeader.setDocumentName(documentName);
    documentHeader.setBegin(0);
    documentHeader.setEnd(pText.length());
    documentHeader.setDocumentType("fromWeb");
    documentHeader.setDocumentTitle("generic");

    documentHeader.addToIndexes(pJCas);

  } // End Method createDocumentHeaderAnnotation() -----------------------
  // =======================================================

  /**
   * createInputFile .
   * 
   * @param pInputText the input text
   * @return the string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  // =======================================================
  private String createInputFile(String pInputText) throws IOException {

    String fileName = null;
    try {

      if (StaticInputDir == null)
        StaticInputDir = File.createTempFile("xx", ".tmp").getParent();

      String uuID = String.valueOf(UUID.randomUUID());
      fileName = "v3NLPInputFile" + uuID;
      String fullFileName = StaticInputDir + "/" + fileName + ".string";
      try (final PrintWriter out = new PrintWriter(fullFileName);) {
        out.print(pInputText);
        out.print('\n');
      }
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue making a temp file for the string " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createInputFile", msg);

    }
    return fileName;

  } // End Method createInputFile() ======================

  // =======================================================
  /**
   * createPerformanceMeter sets up a performance meter.
   * 
   * @param args the args
   * @return PerformanceMeter
   */
  // =======================================================
  @SuppressWarnings("resource")
  public static PerformanceMeter createPerformanceMeter(String[] args) {

    PerformanceMeter meter = null;
    try {
      // --------------------------
      // Set up a performance meter
      // --------------------------
      String outputDir = U.getOption(args, "--outputDir", "./out");
      outputDir = outputDir + "/logs";
      U.mkDir(outputDir);
      String performanceLogFile = outputDir + "/" + "performanceLog.txt";
      GLog.println(GLog.ERROR_LEVEL, FrameworkBaselineProcessor.class, "createPerformanceMeter",
          "------------------------------");
      GLog.println(GLog.ERROR_LEVEL, FrameworkBaselineProcessor.class, "createPerformanceMeter",
          "Opening up the performance log " + performanceLogFile);
      GLog.println(GLog.ERROR_LEVEL, FrameworkBaselineProcessor.class, "createPerformanceMeter",
          "------------------------------");
      PrintWriter performanceLog = new PrintWriter(performanceLogFile);

      performanceLog.println("===============================");
      performanceLog.flush();
      meter = new PerformanceMeter(performanceLog);

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue opening up the performance meter " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, FrameworkBaselineProcessor.class, "createPerformanceMeter",
          msg);
      meter = new PerformanceMeter();
    }

    return meter;

  } // End Method createPerformanceMeter() ======================

  // =======================================================
  /**
   * isTooOld indicate if this application has grown too old or has grown to
   * have too much memory. This should be overwritten for each specific
   * application
   * 
   * @return boolean
   */
  // =======================================================
  public boolean isTooOld() {
    boolean returnVal = false;

    int memoryUsed = (int) this.totalMeter.getMemoryUsage();
    int hoursAlive = this.totalMeter.getTotalTimeInHours();

    if (memoryUsed > memoryUsedThreshold || hoursAlive > hoursAliveThreshold)
      returnVal = true;

    return returnVal;
  } // End Method isTooOld() ======================

  /**
   * Returns the memory used threshold.
   *
   * @return the memoryUsedThreshold
   */
  public int getMemoryUsedThreshold() {
    return memoryUsedThreshold;
  }

  // =======================================================
  /**
   * getInputStringList returns the inputStringList.
   * 
   * @return List
   */
  // =======================================================
  public List<String> getInputStringList() {
    return this.inputStrings;
  } // End Method getInputStringList() ======================

  // =======================================================
  /**
   * isAlive returns true before the end of processing is done.
   * 
   * @return boolean
   */
  // =======================================================
  public boolean isAlive() {
    return this.alive;
  } // End Method isAlive() ======================

  // =======================================================
  /**
   * processed returns the number of files processed.
   * 
   * @return int
   */
  // =======================================================
  public int processed() {
    return this.filesProcessed;
  } // End Method processed() ======================

  // =======================================================
  /**
   * getProgress returns the percent processed 0.0 -> 1.0
   * 
   * @return double
   */
  // =======================================================
  public double getProgress() {
    double progress = 0.0;
    if (this.singletonReader != null) {
      Progress p[] = this.singletonReader.getProgress();
      progress = p[0].getCompleted() / (p[0].getTotal() + 0.0000001); // <----
                                                                      // so we
                                                                      // don't
                                                                      // cause
                                                                      // an
                                                                      // exception
    }

    return progress;
  } // End Method getProgress() ======================

  /**
   * Sets the memory used threshold.
   *
   * @param memoryUsedThreshold the memoryUsedThreshold to set
   */
  public void setMemoryUsedThreshold(int memoryUsedThreshold) {
    this.memoryUsedThreshold = memoryUsedThreshold;
  }

  /**
   * Returns the hours alive threshold.
   *
   * @return the hoursAliveThreshold
   */
  public int getHoursAliveThreshold() {
    return hoursAliveThreshold;
  }

  /**
   * setHoursAliveTheshold.
   * 
   * @param hoursAliveThreshold the hoursAliveThreshold to set
   */
  public void setHoursAliveThreshold(int hoursAliveThreshold) {
    this.hoursAliveThreshold = hoursAliveThreshold;
  }

  /**
   * setPerformanceMeter.
   * 
   * @param pPerformanceMeter the performance meter
   */
  public void setPerformanceMeter(PerformanceMeter pPerformanceMeter) {
    this.totalMeter = pPerformanceMeter;
  }

  // =======================================================
  /**
   * setName sets the application name == the server number.
   * 
   * @param serverNumber the name
   */
  // =======================================================
  public void setName(int serverNumber) {

    this.setServerName("server_" + String.valueOf(serverNumber));
  } // End Method setName() ======================

  /**
   * Returns the server name.
   *
   * @return the serverName
   */
  public String getServerName() {
    return serverName;
  }

  /**
   * Sets the server name.
   *
   * @param serverName the serverName to set
   */
  public void setServerName(String serverName) {
    this.serverName = serverName;
  } // End Method setServerName() ======================

  // =======================================================
  /**
   * setRecycleAt sets the number of files this application can process before
   * being recycled.
   * 
   * @param pRecycleAt the recycle at
   */
  // =======================================================
  public void setRecycleAt(int pRecycleAt) {
    this.recycleAt = pRecycleAt;
  } // End Method setRecycleAt() ======================

  // ==========================================
  /**
   * setNumberToProcess.
   *
   * @param pNumberToProcess the number to process
   */
  // ==========================================
  public void setNumberToProcess(long pNumberToProcess) {
    this.numberToProcess = pNumberToProcess;
  } // end Method setNumberToProcess() ========================================

  // =======================================================
  /**
   * getArgs.
   * 
   * @return String[]
   */
  // =======================================================
  public String[] getArgs() {
    return this.args;
  } // End Method getArgs() ======================

  // ==========================================
  /**
   * setArgs .
   *
   * @param pArgs the args
   */
  // ==========================================
  public void setArgs(String[] pArgs) {
    this.args = pArgs;
  } // end Method setArgs() ========================================

  // -----------------------------------------
  // Private Global variables
  // ----------------------------------------
  /** The number to process. */
  // protected AnalysisEngine analysisEngine = null;
  protected long numberToProcess = Integer.MAX_VALUE;

  /** The total meter. */
  protected PerformanceMeter totalMeter = null;

  /** The memory used threshold. */
  private int memoryUsedThreshold = 1000000000;

  /** The hours alive threshold. */
  private int hoursAliveThreshold = 8;

  /** The files processed. */
  private int filesProcessed = 0;

  /** The destroy called. */
  private boolean destroyCalled = false;

  /** The recycle at. */
  private long recycleAt = 1000000000;

  /** The input strings. */
  private ArrayList<String> inputStrings = new ArrayList<String>();

  /** The output strings. */
  @SuppressWarnings("unused")
  private ArrayList<String> outputStrings;

  /** The common model writer. */
  // private gov.va.vinci.nlp.framework.marshallers.commonModel.util.ToCommonModel commonModelWriter;

  /** The alive. */
  private boolean alive = true;

  /** The common model reader. */
  // private FromCommonModel commonModelReader = null;
  
  /** the string reader */
  private FromStringReader stringReader = null;

  /** The xmi writer. */
  private ToXMI xmiWriter = null;

  /** The vtt writer. */
  private ToVTT vttWriter = null;

  

  /** The html writer. */
  private HTMLWriter htmlWriter = null;

  /** The evaluate writer. */
  private EvaluateWriter evaluateWriter = null;
  
  /** The uima writer. */
  private gov.nih.cc.rmd.nlp.framework.marshallers.uima.UimaWriter uimaWriter = null;
  
  /** The framework object writer. */
  private FrameworkObjectWriter frameworkObjectWriter = null;

  /** The doc id counter. */
  private int docIdCounter = 0;

} // End Class FrameworkBaselineApplication
