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



import gov.nih.cc.rmd.nlp.framework.annotator.FilterWriter;
import gov.nih.cc.rmd.nlp.framework.annotator.noOp.NoOpAnnotator;
import gov.nih.cc.rmd.nlp.framework.pipeline.AbstractPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.FrameworkPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.UimaContextParameter;
import gov.nih.cc.rmd.nlp.framework.utils.U;

// =================================================
/**
 * NoOp pipeline does nothing
 *        
 * @author  Guy Divita 
 * @created July 30, 2018
 *
 */


  public class NoOpPipeline extends AbstractPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public NoOpPipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public NoOpPipeline()  throws Exception {
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
		
		 String typeDescriptorClassPath = U.getOption(pArgs,"--typeDescriptor=", "gov.nih.cc.rmd.framework.Model");
		 pipeline.setTypeDescriptorClassPath( typeDescriptorClassPath );  //<--- in the type.descriptor/desc/com/ciitizen/framework folder
	
		  
	      
	     UimaContextParameter argsParameter = new UimaContextParameter("args",  pArgs, "String",  true, true);

         pipeline.add(  NoOpAnnotator.class.getCanonicalName(), argsParameter ) ;
         pipeline.add(  FilterWriter.class.getCanonicalName(), argsParameter ) ;
     
    
      return pipeline;
    
    }  // End Method createPipeline() ======================
    
    
    // ------------------------
    // Private Global Variables
    // ------------------------
  

} // end Class DateAndTimePipeline

