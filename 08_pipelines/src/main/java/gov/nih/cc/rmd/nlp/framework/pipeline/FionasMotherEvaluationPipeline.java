package gov.nih.cc.rmd.nlp.framework.pipeline;




import gov.nih.cc.rmd.nlp.framework.annotator.EvaluateWithAttributeAnnotator;
import gov.nih.cc.rmd.nlp.framework.annotator.RelabelAnnotator;
import gov.nih.cc.rmd.nlp.framework.pipeline.FrameworkPipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.UimaContextParameter;
import gov.nih.cc.rmd.nlp.framework.utils.U;

// =================================================
/**
 * EvaluationPipeline extends the pipeline by
 * adding a relabel annotator at the beginning and
 * at the end of the pipeline - to transform any annoations
 * from a reference standard into gold labels, and any 
 * labels from the pipeline that are being evaluated
 * into copper.
 * 
 * This method assumes the following args are being  passed
 * in
 *      --beforeLabelPairs=Date:Gold|True:Gold|NotDate:False
 *      --afterLabelPairs=Date:Copper|NotDate:False
 *
 *        
 * @author  Guy Divita 
 * @created January 16, 2018
 *
 * 
 */

public class FionasMotherEvaluationPipeline extends FionasMotherPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public FionasMotherEvaluationPipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public FionasMotherEvaluationPipeline()  throws Exception {
     super( );
      
    } // End Constructor() -----------------------------
    
    
	// =======================================================
	/**
	 * createPipeline overrides the createPipeline transforms
	 * any before label pairs to gold and false's, runs the
	 * super createPipeline, then transforms any after label
	 * pairs to copper and false's. 
	 * 
	 * @param pArgs
	 * @return FrameworkPipeline
	 */
	// =======================================================
   @Override
	public FrameworkPipeline createPipeline(String[] pArgs) {

	
    FrameworkPipeline pipeline = super.createPipeline(pArgs);
     
		String beforeLabelPairs = U.getOption(pArgs,  "--beforeLabelPairs=","Date:Gold|True:Gold");
		String  afterLabelPairs = U.getOption(pArgs,   "--afterLabelPairs=","Date:Copper");
		
		String beforeArgs[] = {"--labelPairs=" + beforeLabelPairs, "--remove=true"};
		String  afterArgs[] = {"--labelPairs=" + afterLabelPairs, "--remove=false"};
	
		UimaContextParameter argsParameter = new UimaContextParameter("args", pArgs, "String", true, true);
		UimaContextParameter beforeArgsParameter = new UimaContextParameter("args", beforeArgs, "String", true, true);
		UimaContextParameter  afterArgsParameter = new UimaContextParameter("args",  afterArgs, "String", true, true);

		 pipeline.addAtBeginning(              RelabelAnnotator.class.getCanonicalName(), beforeArgsParameter );
	     pipeline.addAtEnd      (              RelabelAnnotator.class.getCanonicalName(), afterArgsParameter  );
	     pipeline.addAtEnd      (EvaluateWithAttributeAnnotator.class.getCanonicalName(), argsParameter  );  // <--- can only be done 
	                                                                                                 //      here if not in
	                                                                                                 //      scale-out mode
	                                                                                                 //      Use here when you want
	                                                                                                 //      to see xmi output
	  
      return pipeline;
    
    }  // End Method createPipeline() ======================
    
    

} // end Class FionasMotherEvaluationPipeline
