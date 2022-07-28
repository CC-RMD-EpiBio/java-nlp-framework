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



    
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.AbsoluteDateAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.AbsoluteTimeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.DateAndTimeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.DateByLookupAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.DurationAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.EventAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.RelativeDateAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.RelativeTimeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime.SetAnnotator;

import gov.nih.cc.rmd.nlp.framework.annotator.AssertionEvidenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.CheckBoxAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.CityStateAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.FilterWriter;
import gov.nih.cc.rmd.nlp.framework.annotator.LineAnnotatorWithBlankLines;
import gov.nih.cc.rmd.nlp.framework.annotator.ObsecAnSectionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.PersonAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.QuestionAnswerAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SectionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SectionRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SentenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.ShapeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator2;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueWithinListAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TermAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TokenAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.CCDASectionsAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.PageFooterAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.Sections.PageHeaderAnnotator;
import gov.nih.cc.rmd.nlp.framework.pipeline.AbstractPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.FrameworkPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.UimaContextParameter;

// =================================================
/**
 * ciitizenSections Pipeline finds umls mapped mentions in text
 *        
 * @author  Guy Divita 
 * @created Jan 10, 2018
 *
 */


  public class MinimalPipeline extends AbstractPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public MinimalPipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public MinimalPipeline()  throws Exception {
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
		pipeline.setTypeDescriptorClassPath("com.ciitizen.framework.Model");  //<--- in the type.descriptor/desc/com/ciitizen/framework folder
	
		  
	      
	      UimaContextParameter argsParameter = new UimaContextParameter("args",  pArgs, "String",  true, true);


          pipeline.add(      ObsecAnSectionAnnotator.class.getCanonicalName(), argsParameter );
          pipeline.add(               ShapeAnnotator.class.getCanonicalName(), argsParameter );
          
          pipeline.add(               TokenAnnotator.class.getCanonicalName(), argsParameter );
        
          pipeline.add(  LineAnnotatorWithBlankLines.class.getCanonicalName(), argsParameter ) ;
          pipeline.add(            CheckBoxAnnotator.class.getCanonicalName(), argsParameter );
          
         
          pipeline.add(           SlotValueAnnotator.class.getCanonicalName(), argsParameter );
          pipeline.add(            SentenceAnnotator.class.getCanonicalName(), argsParameter );
          
          pipeline.add(                TermAnnotator.class.getCanonicalName(), argsParameter );
          
         
          pipeline.add(   AssertionEvidenceAnnotator.class.getCanonicalName(), argsParameter);
      
          pipeline.add(      QuestionAnswerAnnotator.class.getCanonicalName(), argsParameter );
          pipeline.add( SlotValueWithinListAnnotator.class.getCanonicalName(), argsParameter);
          pipeline.add(     SlotValueRepairAnnotator.class.getCanonicalName() ,argsParameter);
         
          pipeline.add(       SectionRepairAnnotator.class.getCanonicalName() ,argsParameter);
           
          
          pipeline.add(     SlotValueRepairAnnotator2.class.getCanonicalName() ,argsParameter);
         
          pipeline.add(     CCDASectionsAnnotator.class.getCanonicalName(), argsParameter ); 
        

          pipeline.add(        AbsoluteDateAnnotator.class.getCanonicalName(), argsParameter );
         
          pipeline.add(        AbsoluteTimeAnnotator.class.getCanonicalName(), argsParameter );
          
          pipeline.add(        CityStateAnnotator.class.getCanonicalName(), argsParameter );
          pipeline.add(        PersonAnnotator.class.getCanonicalName(), argsParameter );
          
          pipeline.add(            DurationAnnotator.class.getCanonicalName(), argsParameter );
          pipeline.add(               EventAnnotator.class.getCanonicalName(), argsParameter );
          pipeline.add(        RelativeDateAnnotator.class.getCanonicalName(), argsParameter );
          pipeline.add(        RelativeTimeAnnotator.class.getCanonicalName(), argsParameter );
          pipeline.add(                 SetAnnotator.class.getCanonicalName(), argsParameter );
          pipeline.add(         DateAndTimeAnnotator.class.getCanonicalName(), argsParameter );
          pipeline.add(        DateByLookupAnnotator.class.getCanonicalName(), argsParameter );
      
          pipeline.add(        PageHeaderAnnotator.class.getCanonicalName(), argsParameter );
          pipeline.add(        PageFooterAnnotator.class.getCanonicalName(), argsParameter );
          
          
          pipeline.add(                 FilterWriter.class.getCanonicalName(), argsParameter) ; 
        
    
    
      return pipeline;
    
    }  // End Method createPipeline() ======================
    
    
    // ------------------------
    // Private Global Variables
    // ------------------------
  

} // end Class DateAndTimePipeline

