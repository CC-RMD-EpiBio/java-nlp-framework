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
package gov.nih.cc.rmd.nlp.framework.pipeline;

import gov.nih.cc.rmd.nlp.framework.annotator.Sections.CCDASectionHeaderAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.CCDASectionsAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.PageFooterAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.PageHeaderAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.SentenceSectionRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.SentenceTabRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.AbsoluteDateAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.AbsoluteTimeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.DateAndTimeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.DateByLookupAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.DurationAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.EventAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.RelativeDateAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.RelativeTimeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.SetAnnotator;

import gov.nih.cc.rmd.nlp.framework.annotator.CityStateAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.FilterWriter;
import gov.nih.cc.rmd.nlp.framework.annotator.LineAnnotatorWithBlankLines;
import gov.nih.cc.rmd.nlp.framework.annotator.PersonAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SentenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TermAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TokenAnnotator;

import gov.nih.cc.rmd.nlp.framework.annotator.ShapeAnnotator;
import gov.nih.cc.rmd.nlp.framework.pipeline.AbstractPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.FrameworkPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.UimaContextParameter;

// =================================================
/**
 * DocumentMetaDataPipeline runs data through pipeline components to get
 * document meta data like referenceDate, document type[TBD] ...
 * 
 * @author Guy Divita
 * @created July 10, 2018
 *
 */
public class DocumentMetaDataPipeline extends AbstractPipeline {

  // =======================================================
  /**
   * Constructor
   *
   * @param pArgs
   */
  // =======================================================
  public DocumentMetaDataPipeline(String[] pArgs) {
    super(pArgs);

  }

  // -----------------------------------------
  /**
   * Constructor
   * 
   * @throws Exception
   */
  // -----------------------------------------
  public DocumentMetaDataPipeline() throws Exception {
    super();

  } // End Constructor() -----------------------------

  // =======================================================
  /**
   * createPipeline defines the pipeline
   * 
   * @param pArgs
   * @return FrameworkPipeline
   */
  // =======================================================
  @Override
  public FrameworkPipeline createPipeline(String[] pArgs) {

    FrameworkPipeline pipeline = new FrameworkPipeline(pArgs);
    pipeline.setTypeDescriptorClassPath("com.ciitizen.framework.Model"); // <---
                                                                         // in
                                                                         // the
                                                                         // type.descriptor/desc/com/ciitizen/framework
                                                                         // folder

    UimaContextParameter argsParameter =
        new UimaContextParameter("args", pArgs, "String", true, true);

   
    pipeline.add(LineAnnotatorWithBlankLines.class.getCanonicalName(), argsParameter);
//    pipeline.add(ShapesAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(TokenAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(SentenceAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(SentenceTabRepairAnnotator.class.getCanonicalName(), argsParameter );
    pipeline.add(TermAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(CCDASectionHeaderAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(CCDASectionsAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(SentenceSectionRepairAnnotator.class.getCanonicalName(), argsParameter);
    
    pipeline.add(CityStateAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(PersonAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(AbsoluteDateAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(AbsoluteTimeAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(DurationAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(EventAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(RelativeDateAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(RelativeTimeAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(SetAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(DateAndTimeAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(DateByLookupAnnotator.class.getCanonicalName(), argsParameter);

    pipeline.add(PageHeaderAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(PageFooterAnnotator.class.getCanonicalName(), argsParameter);
   //  pipeline.add(SpecimenCollectionDateInGeneReportPageHeaderAnnotator.class.getCanonicalName(), argsParameter );
    

   // pipeline.add(ReferenceDateAnnotator.class.getCanonicalName(), argsParameter);

   // pipeline.add(EventDateAnnotator.class.getCanonicalName(), argsParameter);
    pipeline.add(FilterWriter.class.getCanonicalName(), argsParameter);

    return pipeline;

  } // End Method createPipeline() ======================

  // ------------------------
  // Private Global Variables
  // ------------------------

} // end Class DocumentMetaDataPipeline
