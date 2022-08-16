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
