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



import gov.nih.cc.rmd.nlp.framework.annotator.AssertionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.AssertionEvidenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.CheckBoxAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.CityStateAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.FilterWriter;
import gov.nih.cc.rmd.nlp.framework.annotator.LineAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.LineAnnotatorWithBlankLines;
import gov.nih.cc.rmd.nlp.framework.annotator.POSAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.PersonAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.PhraseAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.ProblemAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.QuestionAnswerAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SentenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.ShapeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator2;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueWithinListAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TermAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TokenAnnotator;
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
import gov.nih.cc.rmd.nlp.framework.annotator.noOp.NoOpAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.procedures.ProcedureAttributesAnnotator;
import gov.nih.cc.rmd.nlp.framework.pipeline.AbstractPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.FrameworkPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.UimaContextParameter;
import gov.nih.cc.rmd.nlp.framework.utils.U;

// =================================================
/**
 * SyntaticPipeline tokenizes documents into sections, tokens, terms, phrases and sentences.
 *        
 * @author  Guy Divita 
 * @created July 30, 2018
 *
 */


  public class SyntaticPipeline extends AbstractPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public SyntaticPipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public SyntaticPipeline()  throws Exception {
     super( );
      
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
	    UimaContextParameter argsParameter = new UimaContextParameter("args",  pArgs, "String",  true, true);
      pipeline.setTypeDescriptorClassPath("gov.nih.cc.rmd.framework.Model"); 
	    
	     pipeline.add(   LineAnnotatorWithBlankLines.class.getCanonicalName(), argsParameter ) ;
	     pipeline.add(                ShapeAnnotator.class.getCanonicalName(), argsParameter );
	     pipeline.add(                TokenAnnotator.class.getCanonicalName(), argsParameter );
	     
	     pipeline.add(             CheckBoxAnnotator.class.getCanonicalName(), argsParameter ) ;
	     pipeline.add(            SlotValueAnnotator.class.getCanonicalName(), argsParameter );   
	     
	     pipeline.add(             SentenceAnnotator.class.getCanonicalName(), argsParameter );
	   
	 
	     pipeline.add(                 TermAnnotator.class.getCanonicalName(), argsParameter );
	     pipeline.add(    AssertionEvidenceAnnotator.class.getCanonicalName(), argsParameter);
	     pipeline.add(       QuestionAnswerAnnotator.class.getCanonicalName(), argsParameter );
	   
	      
	     pipeline.add(  SlotValueWithinListAnnotator.class.getCanonicalName(), argsParameter);
	     pipeline.add(      SlotValueRepairAnnotator.class.getCanonicalName(), argsParameter );
	     pipeline.add(     SlotValueRepairAnnotator2.class.getCanonicalName(), argsParameter );
	     
	         
	     pipeline.add(     CCDASectionHeaderAnnotator.class.getCanonicalName(), argsParameter);
	     pipeline.add(CCDAPanelSectionHeaderAnnotator.class.getCanonicalName(), argsParameter);
	     pipeline.add(          CCDASectionsAnnotator.class.getCanonicalName(), argsParameter);
	     pipeline.add( SentenceSectionRepairAnnotator.class.getCanonicalName(), argsParameter);
	     pipeline.add(     SentenceTabRepairAnnotator.class.getCanonicalName(), argsParameter);    
	     pipeline.add(      TableSectionZoneAnnotator.class.getCanonicalName(), argsParameter);   
	     pipeline.add(                   POSAnnotator.class.getCanonicalName(), argsParameter);
	     pipeline.add(                PhraseAnnotator.class.getCanonicalName(), argsParameter);
	     
	     pipeline.add(             AssertionAnnotator.class.getCanonicalName(), argsParameter);
	  
	  //  pipeline.add(           CityStateAnnotator.class.getCanonicalName(), argsParameter);
	  //  pipeline.add(              PersonAnnotator.class.getCanonicalName(), argsParameter);
	     
	  //   pipeline.add(          PageHeaderAnnotator.class.getCanonicalName(), argsParameter);
	  //  pipeline.add(          PageFooterAnnotator.class.getCanonicalName(), argsParameter);
	   
	     pipeline.add(                 FilterWriter.class.getCanonicalName(), argsParameter ) ; 
	   
    
      return pipeline;
    
    }  // End Method createPipeline() ======================
    
	
	// Global Variables -------------------
	
	
	 public static String SyntaticTerminologyFiles =  
       "resources/vinciNLPFramework/term/2020AA/SPECIALIST_00.LRAGR" + ":" 
     + "resources/vinciNLPFramework/term/2020AA/SPECIALIST_01.LRAGR" + ":" 
     + "resources/vinciNLPFramework/dateAndTime/dateAndTime.lragr" + ":"
     + "resources/vinciNLPFramework/medications/localMedications.lragr" + ":"
     + "resources/vinciNLPFramework/medications/doseForm.lragr" + ":"
     + "resources/vinciNLPFramework/ucum/ucum.lragr" + ":" 
     + "resources/vinciNLPFramework/assertion/clinicalStatusEvidence.lragr" + ":"
     + "resources/vinciNLPFramework/locations/us_cities.lragr" + ":"
     + "resources/vinciNLPFramework/locations/us_states.lragr" + ":"
     + "resources/vinciNLPFramework/person/personNamePieces.lragr" + ":"
     + "resources/vinciNLPFramework/sections/ccdaSectionHeaders.lragr" + ":"
     + "resources/vinciNLPFramework/sections/pageHeaderPageFooterEvidence.lragr" + ":"
     + "resources/vinciNLPFramework/geneObservations/geneObservations.lragr" + ":"
   
     + AssertionAnnotator.EvidenceLRAGRFiles; // <---
                                               // needed


   public static String SyntaticMinimalTerminologyFiles =  
    //   "resources/vinciNLPFramework/term/2011/SPECIALIST_PART1.LRAGR" + ":" 
    // + "resources/vinciNLPFramework/term/2011/SPECIALIST_PART2.LRAGR" + ":"
     "resources/vinciNLPFramework/dateAndTime/dateAndTime.lragr" + ":"  
   + "resources/vinciNLPFramework/ucum/ucum.lragr" + ":" 
   + "resources/vinciNLPFramework/sections/ccdaSectionHeaders.lragr" + ":" 
   + "resources/vinciNLPFramework/sections/pageHeaderPageFooterEvidence.lragr" + ":" 
   + AssertionAnnotator.EvidenceLRAGRFiles; // <---
                                               // needed

    

} // end Class DateAndTimePipeline

