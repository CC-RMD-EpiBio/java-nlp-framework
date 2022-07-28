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
// =================================================
/**
 * SectionPipeline defines the section pipeline

 *     
 *     
 * @author  Guy Divita 
 * @created May 24, 2013
 *
 * 

 */
package gov.nih.cc.rmd.nlp.framework.pipeline;

import gov.nih.cc.rmd.nlp.framework.annotator.CheckBoxAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.LineAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.ObsecAnSectionAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.QuestionAnswerAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SectionRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SentenceAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueRepairAnnotator2;
import gov.nih.cc.rmd.nlp.framework.annotator.SlotValueWithinListAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.TokenAnnotator;


  public class Section2Pipeline extends AbstractPipeline  {

   
	// =======================================================
	    /**
	     * Constructor SectionPipeline 
	     *
	     * @param pArgs
	     */
	    // =======================================================
	    public Section2Pipeline() {
	      super();
	      
	    }

	  
   
    // =======================================================
    /**
     * Constructor SectionPipeline 
     *
     * @param pArgs
     */
    // =======================================================
    public Section2Pipeline(String[] pArgs) {
      super(pArgs);
      
    }


    // =======================================================
    /**
     * createPipeline defines the pipeline
     * 
     * @param pArgs
     * @return FrameworkPipeline
     */
    // =======================================================
    public FrameworkPipeline createPipeline(String[] pArgs) {

      FrameworkPipeline pipeline = new FrameworkPipeline(pArgs);
      // -----------------------------
      // Add pipeline components to the pipeline
      // -----------------------------
      UimaContextParameter  argsParameter = new UimaContextParameter("args",      pArgs,    "String",  true, true);


      //pipeline.add(           DocumentHeaderFromLeoCSI.class.getCanonicalName(), argsParameter);
         pipeline.add(  2,    ObsecAnSectionAnnotator.class.getCanonicalName(), argsParameter);
         pipeline.add(  1,             TokenAnnotator.class.getCanonicalName(), argsParameter); 
       //  pipeline.add(   TokenizeOnlySectionsAnnotator.class.getCanonicalName(), argsParameter );   //< --- only as good as the sectionizer
                                                                                                    //      commented out because the
                                                                                                    //      sectionizer misses sections that
                                                                                                    //      get picked up in sectionRepair
          pipeline.add(  1,              LineAnnotator.class.getCanonicalName(), argsParameter);        
          pipeline.add(  1,          CheckBoxAnnotator.class.getCanonicalName(), argsParameter);
          pipeline.add(  1,         SlotValueAnnotator.class.getCanonicalName(), argsParameter);   //<---- now delays assertion until assertionAnnotator
        
          pipeline.add(  1,          SentenceAnnotator.class.getCanonicalName(),  argsParameter);
          pipeline.add(  1,   SlotValueRepairAnnotator.class.getCanonicalName() , argsParameter);
          pipeline.add(        QuestionAnswerAnnotator.class.getCanonicalName() , argsParameter);
      
          pipeline.add(      SlotValueRepairAnnotator2.class.getCanonicalName(), argsParameter);
          pipeline.add(   SlotValueWithinListAnnotator.class.getCanonicalName(), argsParameter);
          pipeline.add(         SectionRepairAnnotator.class.getCanonicalName(), argsParameter);
        //  pipeline.add(                  SectionFilter.class.getCanonicalName(), argsParameter);
        //  pipeline.add(      CoreCharacteristicsWriter.class.getCanonicalName(), argsParameter);
        //  pipeline.add(                   FilterWriter.class.getCanonicalName(), argsParameter);
      
      
      
      return pipeline;

    } // End Method createPipeline() ======================



   
   
} // end Class SophiaNegexServer 
