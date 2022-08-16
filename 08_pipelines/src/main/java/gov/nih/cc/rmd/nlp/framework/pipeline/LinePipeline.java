package gov.nih.cc.rmd.nlp.framework.pipeline;



import gov.nih.cc.rmd.nlp.framework.annotator.FilterWriter;
import gov.nih.cc.rmd.nlp.framework.annotator.LineAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.noOp.NoOpAnnotator;
import gov.nih.cc.rmd.nlp.framework.pipeline.AbstractPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.FrameworkPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.UimaContextParameter;

// =================================================
/**
 * LinePipeline annotates lines of documents
 *        
 * @author  Guy Divita 
 * @created July 30, 2018
 *
 */


  public class LinePipeline extends AbstractPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public LinePipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public LinePipeline()  throws Exception {
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
		pipeline.setTypeDescriptorClassPath("gov.nih.cc.rmd.Model");  
	
		  
	      
	     UimaContextParameter argsParameter = new UimaContextParameter("args",  pArgs, "String",  true, true);

	     pipeline.add(  LineAnnotator.class.getCanonicalName(), argsParameter ) ;
	     pipeline.add(  FilterWriter.class.getCanonicalName(), argsParameter ) ;
     
    
      return pipeline;
    
    }  // End Method createPipeline() ======================
    
    

} // end Class DateAndTimePipeline
