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
