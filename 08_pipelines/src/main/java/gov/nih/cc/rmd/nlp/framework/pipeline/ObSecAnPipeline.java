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
 * ObSecAnPipeline defines the section pipeline

 *     
 *     
 * @author  Guy Divita 
 * @created May 24, 2013
 *
 * 

 */
package gov.nih.cc.rmd.nlp.framework.pipeline;

import gov.nih.cc.rmd.nlp.framework.annotator.ObsecAnSectionAnnotator;


  public class ObSecAnPipeline extends AbstractPipeline  {

   
  
   
    // =======================================================
    /**
     * Constructor SectionPipeline 
     *
     * @param pArgs
     */
    // =======================================================
    public ObSecAnPipeline(String[] pArgs) {
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

      UimaContextParameter   argsParameter = new UimaContextParameter("args",  args, "String",  true, true);

      pipeline.add(               ObsecAnSectionAnnotator.class.getCanonicalName(), argsParameter  );
           
      return pipeline;

    } // End Method createPipeline() ======================



   
   
} // end Class SophiaNegexServer 