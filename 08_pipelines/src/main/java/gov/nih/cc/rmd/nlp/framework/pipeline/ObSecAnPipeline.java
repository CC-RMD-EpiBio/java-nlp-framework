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
