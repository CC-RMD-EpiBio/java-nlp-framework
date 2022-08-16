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
 * Section View annotator creates sectionZone's that are
 * devoid of the section header so that html and vtt can view it
 * without overlaps.
 *     
 *
 * @author  Guy Divita 
 * @created July 23 2018
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.framework.marshallers.json;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.SectionZone;

import gov.va.chir.model.ContentHeading;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

/**
 * Section view annotator.
 */
public class SectionViewAnnotator extends JCasAnnotator_ImplBase {

  /** The Constant annotatorName. */
  public static final String annotatorName = SectionViewAnnotator.class.getSimpleName();

  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase. This version labels lines of
   * the document.
   * 
   * We are looking for the first content heading that does not have "date" or
   * "Patient" in the name that is doesn't have shapes (location, phone, person
   *
   * @param pJCas the j cas
   * @throws AnalysisEngineProcessException the analysis engine process
   *           exception
   */
  // -----------------------------------------
  @Override
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    this.performanceMeter.startCounter();

    List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, SectionZone.typeIndexID);

    if (sections != null && !sections.isEmpty())
      for (Annotation section : sections)
        createSectionView(pJCas, section);

    this.performanceMeter.stopCounter();

  } // End Method process() ------------------

  // =================================================
  /**
   * createSectionView .
   *
   * @param pJCas the j cas
   * @param pSectionZone the section zone
   */
  // =================================================
  private final void createSectionView(JCas pJCas, Annotation pSectionZone) {

    String sectionHeading = ((SectionZone) pSectionZone).getSectionName();

    if (sectionHeading != null && !sectionHeading.toLowerCase().contains("unknown")
        && !sectionHeading.toLowerCase().contains("unlabeled")) {
      List<Annotation> contentHeadings =
          UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, ContentHeading.typeIndexID,
              pSectionZone.getBegin(), pSectionZone.getBegin() + sectionHeading.length() - 1);

      if (contentHeadings != null && !contentHeadings.isEmpty()) {
        Annotation contentHeading = contentHeadings.get(0);

        pSectionZone.setBegin(contentHeading.getEnd() + 1);

      }

    }

  } // end Method createSectionView()

  /**
   * destroy.
   */
  @Override
  public void destroy() {
    if (this.performanceMeter != null)
      this.performanceMeter.writeProfile(this.getClass().getSimpleName());
  } // end Method destroy() ----------

  // ----------------------------------
  /**
   * initialize.
   *
   * @param aContext the a context
   * @throws ResourceInitializationException the resource initialization
   *           exception
   */
  // ----------------------------------
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {

    String[] args = null;
    try {
      args = (String[]) aContext.getConfigParameterValue("args");

      initialize(args);

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue initializing " + annotatorName + " " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", msg);
      throw new ResourceInitializationException();
    }

  } // end Method initialize() --------

  // ----------------------------------
  /**
   * initialize loads in the resources needed.
   *
   * @param pArgs the args
   * @throws ResourceInitializationException the resource initialization
   *           exception
   */
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {

    try {

      this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getSimpleName());

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize",
          "Issue initializing the " + annotatorName + " " + e.toString());
      throw new ResourceInitializationException();
    }

  } // end Method Initialize() -----------

  // ---------------------------
  // Global Variables
  /** The performance meter. */
  // ---------------------------
  private ProfilePerformanceMeter performanceMeter = null;

} // end Class SectionViewAnnotator() ---------------
