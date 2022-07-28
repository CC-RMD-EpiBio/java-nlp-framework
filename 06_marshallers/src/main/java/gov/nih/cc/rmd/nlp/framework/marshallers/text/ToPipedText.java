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
//=================================================
/**
 * This writer put's pipes at boundary markers 
 *
 * @author  Guy Divita 
 * @created June 26, 2012
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.text;


import gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;



public class ToPipedText extends AbstractWriter {





  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   * 
  **/
  // =================================================
  public ToPipedText(String[] pArgs) throws ResourceInitializationException {
    initialize( pArgs);
  } // end Constructor() ----------------------





    // -----------------------------------------
    /** 
     * process iterates through all annotations, filters out
     * those that should be filtered out, then pushes them
     * into a database store.
     * 
     * Each document should include a DocumentAnnotation annotation and a documentHeader annotation
     * for re-animation purposes.
     *
     * @param pAJCas
     * @throws AnalysisEngineProcessException
     */
    // -----------------------------------------
    @Override
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
      
	 
     
    	// -----------------------------------
      // Set the text
      String docText = pJCas.getDocumentText();
      String pipedDocText = docText;
      
      StringBuffer docTextBuff = new StringBuffer();
      docTextBuff.append( docText);
      
    
      // ------------------------------------
      // get all the segmentations 
      // ------------------------------------
      List<Annotation> segments = null;
      try {
        segments = UIMAUtil.getAnnotations( pJCas, this.segmentType );
        
        if ( segments != null && !segments.isEmpty()) {
          
          // unique and reverse sort
          segments = UIMAUtil.uniqueAnnotationList(segments);
          UIMAUtil.sortByOffsetDescending(segments);
          
          // Insert the last break in the document
          docTextBuff.insert( segments.get( segments.size() -1).getEnd(), SEGMENT_DELIMITER);
          
          
          // insert breaks going backward 
          for ( int j = 0; j < segments.size(); j++) {
            
            Annotation currentSegment = segments.get(j);
            int segmentBreakOffset = currentSegment.getBegin();
            docTextBuff.insert( segmentBreakOffset, SEGMENT_DELIMITER);
            
          }
          
          pipedDocText = docTextBuff.toString();
          
        }
      
      } catch ( Exception e3 ) {
        e3.printStackTrace();
        GLog.error_println("Issue with segments " + e3.toString());
      }
      // ----------------------------------
      // print out the text followed by pipe delimited annotations, index style
      // ----------------------------------
   
      String inputFileName = VUIMAUtil.getDocumentId(pJCas);
      inputFileName = U.getFileNamePrefix(inputFileName);
      String outputFileName = inputFileName + ".txt";
      File outputFile = new File ( this.outputDir + "/" + outputFileName);
      PrintWriter out = null;
      try {
        out = new PrintWriter (outputFile.getAbsolutePath() );
        out.print( pipedDocText);
        out.close();
      } catch (Exception e) {
        e.getStackTrace();
        String msg = "Issue writing out file " + outputFile.getAbsolutePath() + "\n" + e.getMessage() + "\n" ;
        System.err.println(msg);
      }
        
        
      System.err.println("Finished processing file" );
    } // end Method process





	//----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
      
      String pOutputDir = null;
    
      
      initialize( pOutputDir  );
    
    
      
    } // end Method initialize() --------------


  //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param pArgs
     * 
     **/
    // ----------------------------------
    public void initialize( String[] pArgs ) throws ResourceInitializationException {
      
      String      outputDir = U.getOption(pArgs, "--outputDir=", "./someDir/");
      
      this.segmentType = U.getOption( pArgs,  "--segmentType=", "Segment");
      
      
      initialize( outputDir  );
    
    
      
    } // end Method initialize() --------------


    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(String pOutputDir ) throws ResourceInitializationException {
      
     
      this.outputDir = pOutputDir + "/pipedText";
 
      try {
        U.mkDir( this.outputDir );
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue creating the output dir " + this.outputDir + " " + e.toString());
        throw new ResourceInitializationException();
      }
      
      
    } // end Method initialize() --------------


    


    // -----------------------------------------
    /** 
     * destroy closes the database
     *
     */
    // -----------------------------------------
    @Override
    public void destroy() {
    
    } // end Method destroy() 


    // ----------------------------------------
    // Global variables
    // ----------------------------------------
  
    private String segmentType = "Sentence";
    private String outputDir;
    private char SEGMENT_DELIMITER = '|';
  
  

} // end Class toCommonModel
