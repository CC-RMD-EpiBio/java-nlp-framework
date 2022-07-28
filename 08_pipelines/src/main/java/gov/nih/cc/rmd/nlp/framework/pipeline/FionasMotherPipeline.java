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
package gov.nih.cc.rmd.nlp.framework.pipeline;



import gov.nih.cc.rmd.nlp.framework.annotator.Sections.CCDAPanelSectionHeaderAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.CCDASectionHeaderAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.CCDASectionsAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.PageFooterAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.PageHeaderAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.SentenceSectionRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.SentenceTabRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.TableSectionZoneAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.AbsoluteDateAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.AbsoluteTimeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.DateAndTimeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.DateByLookupAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.DurationAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.EventAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.RelativeDateAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.RelativeTimeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.SetAnnotator;


import gov.nih.cc.rmd.nlp.framework.annotator.labs.LabPanelAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.medications.DoseFormAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.medications.MedicationDatesAnnotator;

import gov.nih.cc.rmd.nlp.framework.annotator.procedures.ProcedureAttributesAnnotator;


import gov.nih.cc.rmd.nlp.framework.annotator.AssertionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.AssertionEvidenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.CheckBoxAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.CityStateAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.FilterWriter;
import gov.nih.cc.rmd.nlp.framework.annotator.LineAnnotatorWithBlankLines;
import gov.nih.cc.rmd.nlp.framework.annotator.ObsecAnSectionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.PersonAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.ProblemAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.QuestionAnswerAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SectionRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SentenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.ShapeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator2;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueWithinListAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TermAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TokenAnnotator;

import gov.nih.cc.rmd.nlp.framework.pipeline.AbstractPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.FrameworkPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.UimaContextParameter;

// =================================================
/**
 * FionasMother Pipeline is the evolution of Sophia applied to the
 * ciitizen's environment initially.  This is the document based pipeline
 * that does lookup based on section type to specific terminologies and
 * sub trees or other query filters.  I.e.,  medication sections get
 * lookups only into rx norm. 
 * 
 * FionasMother used to be fiona, but two significant changes were made
 * to the "branded" fiona pipeline - first, sectionizing has been taken
 * out of the pipeline (that's because we are processing by section rather
 * than document) and because of that, each document is now a section
 * and a section name now has to be passed in as metadata.
 *        
 * @author  Guy Divita 
 * @created Aug 9, 2018
 *
 */


  public class FionasMotherPipeline extends AbstractPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public FionasMotherPipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public FionasMotherPipeline()  throws Exception {
     super( );
      
    } // End Constructor() -----------------------------
    
    
	// =======================================================
	/**
	 * createPipeline defines the pipeline.
	 *
	 * @param pArgs the args
	 * @return FrameworkPipeline
	 */
	// =======================================================
	@Override
  public FrameworkPipeline createPipeline(String[] pArgs) {

		FrameworkPipeline pipeline = new FrameworkPipeline(pArgs);
		pipeline.setTypeDescriptorClassPath("com.ciitizen.framework.Model");  //<--- in the type.descriptor/desc/com/ciitizen/framework folder
	
		  
	      
	      UimaContextParameter argsParameter = new UimaContextParameter("args",  pArgs, "String",  true, true);
  
	      pipeline.add(  LineAnnotatorWithBlankLines.class.getCanonicalName(), argsParameter ) ;
	      pipeline.add(               ShapeAnnotator.class.getCanonicalName(), argsParameter );
	      pipeline.add(               TokenAnnotator.class.getCanonicalName(), argsParameter );
	      pipeline.add(            SentenceAnnotator.class.getCanonicalName(), argsParameter );
	      pipeline.add(   SentenceTabRepairAnnotator.class.getCanonicalName(), argsParameter );
	      pipeline.add(                TermAnnotator.class.getCanonicalName(), argsParameter );
	  
	      pipeline.add(        AbsoluteDateAnnotator.class.getCanonicalName(), argsParameter);  // has to be done before CCDAPanelSectionHeader
	      pipeline.add(         DateAndTimeAnnotator.class.getCanonicalName(), argsParameter);
	      
	      pipeline.add(     CCDASectionHeaderAnnotator.class.getCanonicalName(), argsParameter);
	      pipeline.add(CCDAPanelSectionHeaderAnnotator.class.getCanonicalName(), argsParameter);
	      pipeline.add(          CCDASectionsAnnotator.class.getCanonicalName(), argsParameter);
	      pipeline.add( SentenceSectionRepairAnnotator.class.getCanonicalName(), argsParameter);
	      
	      
	      pipeline.add(   AssertionEvidenceAnnotator.class.getCanonicalName(), argsParameter);
	      pipeline.add(           CityStateAnnotator.class.getCanonicalName(), argsParameter);
	      pipeline.add(              PersonAnnotator.class.getCanonicalName(), argsParameter);
	  
	      pipeline.add(        AbsoluteTimeAnnotator.class.getCanonicalName(), argsParameter);
	      pipeline.add(            DurationAnnotator.class.getCanonicalName(), argsParameter);
	      pipeline.add(               EventAnnotator.class.getCanonicalName(), argsParameter);
	      pipeline.add(        RelativeDateAnnotator.class.getCanonicalName(), argsParameter);
	      pipeline.add(        RelativeTimeAnnotator.class.getCanonicalName(), argsParameter);
	      pipeline.add(                 SetAnnotator.class.getCanonicalName(), argsParameter);
	      pipeline.add(        DateByLookupAnnotator.class.getCanonicalName(), argsParameter);
     
	      
	      // The page header/footer are not in fiona - they are in document metadata pipeline 
	      // - needed here because this pipeline does it all on full docs
	      
	      pipeline.add(          PageHeaderAnnotator.class.getCanonicalName(), argsParameter);
	      pipeline.add(          PageFooterAnnotator.class.getCanonicalName(), argsParameter);
	      
	      
	      // pipeline.add(DiagnosisInGeneReportPageHeaderAnnotator.class.getCanonicalName(), argsParameter );
        //  pipeline.add(SpecimenSiteInGeneReportPageHeaderAnnotator.class.getCanonicalName(), argsParameter );
        //  pipeline.add(SpecimenCollectionDateInGeneReportPageHeaderAnnotator.class.getCanonicalName(), argsParameter );
         
          pipeline.add(              LabPanelAnnotator.class.getCanonicalName(), argsParameter);
        //  pipeline.add(      GeneNameLookupAnnotator.class.getCanonicalName(), argsParameter );
        //  pipeline.add(   MedicationsLookupAnnotator.class.getCanonicalName(), argsParameter );
	      pipeline.add(            DoseFormAnnotator.class.getCanonicalName(), argsParameter );
	      pipeline.add(     MedicationDatesAnnotator.class.getCanonicalName(), argsParameter );
	    
	   
        pipeline.add(               ProblemAnnotator.class.getCanonicalName(), argsParameter );
	   //   pipeline.add(            CancerStageLookupAnnotator.class.getCanonicalName(), argsParameter );
	  
	   //   pipeline.add(            DiagnosisLookupAnnotator.class.getCanonicalName(), argsParameter );
	      
	    
	     // pipeline.add( GeneVariantObservationAndMethodAnnotator.class.getCanonicalName(), argsParameter );
	     // pipeline.add(    GeneObservationsAnnotator.class.getCanonicalName(), argsParameter );    
          
	    //  pipeline.add(            ProceduresLookupAnnotator.class.getCanonicalName(), argsParameter );
	      pipeline.add(         ProcedureAttributesAnnotator.class.getCanonicalName(), argsParameter );
	     // pipeline.add(                VitalsAnnotator.class.getCanonicalName(), argsParameter);
	     // pipeline.add(       VitalSignLookupAnnotator.class.getCanonicalName(), argsParameter);
	      
      //  pipeline.add(            ConditionsLookupAnnotator.class.getCanonicalName(), argsParameter );
	      pipeline.add(           AssertionAnnotator.class.getCanonicalName(), argsParameter);
	     // pipeline.add(       ReferenceDateAnnotator.class.getCanonicalName(), argsParameter);
	     // pipeline.add(           EventDateAnnotator.class.getCanonicalName(), argsParameter );
	     
	  //    pipeline.add(                 FilterWriter.class.getCanonicalName(), argsParameter ) ; 
	
      return pipeline;
    
    }  // End Method createPipeline() ======================
    
    
    // ------------------------
    // Private Global Variables
    // ------------------------
  

} // end Class DateAndTimePipeline
