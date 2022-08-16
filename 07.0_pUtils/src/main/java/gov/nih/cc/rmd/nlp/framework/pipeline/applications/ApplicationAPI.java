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
 * ApplicationAPI implements the FrameworkBaselineProcessorAPI
 * This is useful for wrapping services around pipelines
 *
 * @author     Guy Divita
 * @created    Feb 22, 2018
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;

import gov.nih.cc.rmd.nlp.framework.marshallers.frameworkObject.FrameworkObject;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;

/**
 * Abstractly represents an application api.
 */
public  class ApplicationAPI implements FrameworkBaselineProcessorAPI {

  // =================================================
  /**
   * initializeApplication 
   *
   * @param pArgs Commandline {key=value,key=value} options
   * @param pAE Spun up UIMA Analysis Engine to pass in
   * @throws Exception the exception
   */
  // =================================================
  @Override
  public void initializeApplication(String[] pArgs, AnalysisEngine pAE) throws Exception {

    try {

      this.args = pArgs;
      // -------------------
      // Create a BaselineFrameworkApplication instance
      // -------------------
      this.application = new FrameworkBaselineApplication(pArgs);

      // -------------------
      // Create an engine with a pipeline, attach it to the application
      // -------------------
      this.application.setAnalsyisEngine(pAE);

      // -------------------
      // Create a reader -- not needed because the process methods are called directly
      // -------------------
      // this.application.createReader(FrameworkReaders.STRING_READER, pArgs);

     
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initializeApplication",
          "Issue initializing the pipeline api " + e.toString());
      throw new Exception();

    }

  } // end Method initializeApplication() ---------


  // =================================================
  /**
   * processToHTML .
   *
   * @param pInputText the input text
   * @param pDocumentMetaData the documentMetaData
   * @return JsonDocumentAnnotations
   */
  // =================================================
  @Override
  public String processToHTML(String pInputText ) {

    String returnVal = null;
    try {
      returnVal = this.application.processToHTML(pInputText );
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processToJson",
          "Issue processing |" + pInputText + "|\n to json " + e.toString());
    }
    return returnVal;

  } // end Method processToHTML() -----------------

  // =================================================
  /**
   * processToEvaluate .
   *
   * @param pInputText the input text
   * @param pDocumentMetaData the documentMetaData
   * 
   */
  // =================================================
  public void processToEvaluate(String pInputText ) {

    try {
      this.application.processAPI(FrameworkBaselineWriters.EVALUATE_OBJECT, pInputText  );
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processToEvaluate",
          "Issue processing |" + pInputText + "|\n to evaluate " + e.toString());
    }

  } // end Method processToEvaluate() -----------------

  // =================================================
  /**
   * processToVTT processes the file out to a vtt formatted string.
   *
   * @param pInputText the input text
   * @return String
   */
  // =================================================
  public String processToVTT(String pInputText ) {
    String returnVal = null;
    try {
      returnVal = (String) this.application.processAPI(FrameworkBaselineWriters.VTT_STRING_OBJECT,
          pInputText );
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processToVTT",
          "Issue processing |" + pInputText + "|\n to evaluate " + e.toString());
    }
    return returnVal;
  }

  
 // =================================================
  /**
   * processToCas processes the string to a cas
   * 
   * @param pInputText
   * @return JCas
  */
  // =================================================
  public JCas processToCas(String pInputText) {
  
    
    JCas returnVal = null;
    try {
      returnVal = (JCas) this.application.processAPI(FrameworkBaselineWriters.UIMA_OBJECT, pInputText );
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processToVTT",
          "Issue processing |" + pInputText + "|\n to evaluate " + e.toString());
    }
    return returnVal;
  } // end Method processToCas() -----------------------

  
  
//=================================================
 /**
  * processToFrameworkObject processes the string to objects that 
  * have arrays, not lists in them (python's jni interface can't handle lists)
  * 
  * @param pInputText
  * @return FrameworkObject
 */
 // =================================================
 public FrameworkObject processToFrameworkObject(String pInputText ) {
 
  
   return processToFrameworkObject( pInputText, null);
   
 } // end Method processToCas() -----------------------
  
  
//=================================================
 /**
  * processToFrameworkObject processes the string to objects that 
  * have arrays, not lists in them (python's jni interface can't handle lists)
  * 
  * @param pInputText
  * @param pOutputTypes
  * @return  FrameworkObject
 */
 // =================================================
 public FrameworkObject processToFrameworkObject(String pInputText, String pOutputTypes) {
 
   
   FrameworkObject returnVal = null;
   try {
     returnVal = (FrameworkObject) this.application.processAPI(FrameworkBaselineWriters.FRAMEWORK_OBJECT, pInputText, null, pOutputTypes );
   } catch (Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processToVTT",
         "Issue processing |" + pInputText + "|\n to evaluate " + e.toString());
   }
   return returnVal;
 } // end Method processToCas() -----------------------


//=======================================================
 /**
  * process takes inputText and returns text that is formatted
  * via the vtt piped format - just the annotations
  * 
  * @param inputText
  * @exeption Exception
  * @return String  (vtt style output) 
  */
 // =======================================================
 public String process(String pInputText) throws Exception {
   
   String returnValue = null;
   try {
    
     returnValue = this.application.processTo( FrameworkBaselineApplication.VTT_PIPE_STRING, pInputText );
   } catch (Exception e) {
     e.printStackTrace();
     String msg = "Issue processing the input text " + e.toString();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processToPipedVTT", msg );
     throw new Exception (msg);
   }
   return returnValue;
 }  // End Method processTo() ======================
 
//=======================================================
/**
 * processToPipedVTT takes inputText and returns text that is formatted
 * via the vtt piped format - just the annotations
 * 
 * @param inputText
 * @exeption Exception
 * @return String  (vtt style output) 
 */
// =======================================================
  /* (non-Javadoc)
 * @see gov.nih.cc.rmd.nlp.framework.pipeline.applications.FrameworkBaselineProcessorAPI#processToPipedVTT(java.lang.String)
 */
 @Override
public String processToPipedVTT(String pInputText) {
   String returnValue = null;
   try {
    
     returnValue = this.application.processTo( FrameworkBaselineApplication.VTT_PIPE_STRING, pInputText );
   } catch (Exception e) {
     e.printStackTrace();
     String msg = "Issue processing the input text " + e.toString();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processToPipedVTT", msg );
   }
   return returnValue;
}


  // =================================================
  /**
   * attachPerformanceMeter connects the performance meter to this api.
   *
   * @param pMeter the meter
   */
  // =================================================
  public void attachPerformanceMeter(PerformanceMeter pMeter) {

    this.application.setPerformanceMeter(pMeter);

  } // end Method addPerformanceMeter() -------------

  // =================================================
  /**
   * getArgs [TBD] summary.
   *
   * @return the args
   */
  // =================================================

  public String[] getArgs() {
    return args;
  }

  // =================================================
  /**
   * destroy.
   */
  // =================================================
  public void destroy() {

    this.application.destroy();
  } // end Method destroy() -------------------------

  // =================================================
  /**
   * destroyAndReport calls the destory and creates a report
   * 
   * @return String
   */
  // =================================================
  public String destroyAndReport() {

    String returnVal = this.application.destroyAndReport();

    return returnVal;
  } // end Method destroy() -------------------------

  // ------------------------------------------------
  // Global Variables
  // ------------------------------------------------

  /** The application. */
  private FrameworkBaselineApplication application = null;

  /** The args. */
  private String[] args = null;

} // end Class ApplicationAPI() -------------------
