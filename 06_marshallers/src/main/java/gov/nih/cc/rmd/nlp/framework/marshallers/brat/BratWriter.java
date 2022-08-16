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
 * CSVWriter writes out cas's into brat formatted .txt and .ann files.
 *
 * @author  Guy Divita 
 * @created May 3, 2018
 *
 *  
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.brat;

import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

/**
 * The Class CSVWriter.
 */
public class BratWriter implements Writer {

  // =======================================================
  /**
   * Constructor ToBrat creates a Brat writer.
   * 
   *
   * @param pArgs the args
   * @throws ResourceInitializationException the resource initialization
   *           exception
   */
  // =======================================================
  public BratWriter(String[] pArgs) throws ResourceInitializationException {

    this.initialize(pArgs);

  } // end Constructor() ---------------------

  // -----------------------------------------
  /**
   * 
   * process iterates through all annotations, filters out those that should be
   * filtered out, then pushes them into a database store.
   * 
   * Each document should include a DocumentAnnotation annotation and a
   * documentHeader annotation for re-animation purposes.
   *
   * @param pJCas the j cas
   * @throws AnalysisEngineProcessException the analysis engine process
   *           exception
   */
  // -----------------------------------------
  @Override
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
      this.performanceMeter.startCounter();

      String fileName = VUIMAUtil.getDocumentId(pJCas);
      // brat doesn't like spaces in names
      fileName = fileName.replace(" ", "_");
      // take off the extension
      fileName = U.getFileNamePrefix(fileName);
      // write the text out to a file

      BratAnnotationsContainer container = new BratAnnotationsContainer(fileName);
      container.setText(pJCas.getDocumentText());

      // Itterate though the annotations that are of interest
      List<Annotation> annotations = UIMAUtil.getAnnotations(pJCas);
      if (annotations != null) {
        for (Annotation annotation : annotations) {

          String label = getLabel(annotation);
          if (isAnOutputLabel(label)) {
            container.addEntity(annotation);

            if (!container.getLabels().contains(label)) {
              container.getLabels().add(label);
              bratAnnotationConfFile.addLabel(label);

            }

          }
        }
        container.write(this.outputDir);
        BratAnnotation.resetCounter(); // <------ this could be dangerous

      }

    } catch (Exception e) {
      // n/a
    }
    this.performanceMeter.stopCounter();

  } // End Method process() --------------------------

  // =================================================
  /**
   * getLabel returns the label sans the namespace.
   *
   * @param pAnnotation the annotation
   * @return String
   */
  // =================================================
  private final String getLabel(Annotation pAnnotation) {

    String returnVal = U.getNameWithoutNameSpace(pAnnotation.getClass().getName());

    return returnVal;
  } // end Method getLabel() -------------------------

  // =================================================
  /**
   * isAnOutputLabel .
   *
   * @param pLabel the label
   * @return boolean
   */
  // =================================================
  private final boolean isAnOutputLabel(String pLabel) {

    boolean returnVal = false;
    returnVal = this.outputTypez.contains(pLabel);

    return returnVal;
  } // end Method isAnOutputLabel() ------------------

  // end Method process

  // =================================================
  /**
   * getFileName divines the file name from the documentHeader's documentId.
   * What gets returned is devoid of a namespace. If the documentId is a
   * filename, the extension is stripped off in this method.
   *
   * @param pJCas the j cas
   * @return String
   */
  // =================================================
  public static String getFileName_(JCas pJCas) {
    String fileName = null;
    fileName = VUIMAUtil.getDocumentId(pJCas);
    if (fileName.endsWith(".txt") || fileName.endsWith(".csv") || fileName.endsWith(".rpt")
        || fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".xlsx")) {

      int lastPeriod = fileName.lastIndexOf('.');
      fileName = fileName.substring(0, lastPeriod);
    }

    return fileName;
  } // end Method getFilename() ------------------

  // -----------------------------------------
  /**
   * 
   * destroy.
   */
  // -----------------------------------------
  @Override
  public void destroy() {

    try {
      this.bratAnnotationConfFile.write();
    } catch (Exception e) {
      // n/a
    }
    this.performanceMeter.writeProfile(this.getClass().getSimpleName());

  } // end Method process

  // ----------------------------------
  /**
   * initialize picks up the parameters needed to write a cas out to the
   * database
   * 
   * Within framework, this doesn't get called. It's here for compatability
   * sake.
   *
   * @param aContext the a context
   * @throws ResourceInitializationException the resource initialization
   *           exception
   */
  // ----------------------------------
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {

    try {
      String args[] = null;
      args = (String[]) aContext.getConfigParameterValue("args");
      initialize(args);

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with getting the uima context " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, msg);
      throw new ResourceInitializationException();
    }

  } // end Method initialize() --------------

  // ----------------------------------
  /**
   * initialize picks up the parameters needed to write a cas out to the brat
   * format brat files go in the $outputDir/brat/ directory
   * 
   * Looking for the following arguments --outputDir= --outputTypes=
   * --filterOut=.
   *
   * @param pArgs the args
   * @throws ResourceInitializationException the resource initialization
   *           exception
   */
  // ----------------------------------
  @Override
  public void initialize(String pArgs[]) throws ResourceInitializationException {

    try {
      this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getSimpleName());

      this.outputDir = U.getOption(pArgs, "--outputDir=", "/someDir") + "/brat";
      String _outputTypes = U.getOption(pArgs, "--outputTypes=", "Concept:Snippet");
      // String filteredOutTypez = U.getOption(pArgs, "--filterOut=",
      // "DocumentHeader:Token:Delimiter:SlotValue:Sentence:Phrase:DependentContent:ContentHeading:Utterance");

      String[] outputTypes = U.split(_outputTypes, ":");
      this.outputTypez = new HashSet<String>(outputTypes.length);
      for (String label : outputTypes)
        this.outputTypez.add(label); // < ---- might need normalization no name
                                     // space/ lowercase?

      GLog.println(GLog.STD___LEVEL, "Output is going to " + outputDir);
      U.mkDir(outputDir);

      this.bratAnnotationConfFile = new BratAnnotationConf(this.outputDir);

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue initializing the brat writer " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, msg);
      throw new ResourceInitializationException();
    }

  } // end Method initialize() --------------

  // ----------------------------------------
  // Global Variables
  /** The output dir. */
  // ----------------------------------------
  private String outputDir = null;

  /** The performance meter. */
  private ProfilePerformanceMeter performanceMeter = null;

  /** The brat annotation conf file. */
  private BratAnnotationConf bratAnnotationConfFile = null;

  /** The output typez. */
  private HashSet<String> outputTypez = null;

} // end Class toBrat() -----------------------
