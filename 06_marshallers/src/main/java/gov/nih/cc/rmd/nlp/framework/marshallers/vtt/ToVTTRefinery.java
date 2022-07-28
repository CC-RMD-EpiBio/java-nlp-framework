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
 * ToDatabase is a CAS Consumer that transforms
 * the vinciNLPFramework encoded CAS into a database through
 * jdbc calls to pre-existing database tables that conform to
 * the database schema common to framework and chartReader.
 * 
 * Make sure you have the path to your jdbc driver in the classpath.
 * 
 *   The following parameters are found in the toDatabase.xml configuration file:
 *   
 *      jdbcDriver        |
 *      databaseUserName  |  
 *      databaseName      |
 *      databasePassword  |
 *      jdbcConnectString | Note: this is devoid of the databaseName, userName,databasePassword, which are taken from
 *                                the other parameters
 *
 *
 *   Can filter to/out annotation labels via
 *      includeLabels      parameter set in the toDatabase.xml 
 *                         a list of full namespace labels (one per line)
 *      excludedLabels     a list of full namespace labels (one per line)
 *    
 *    This should be an either/or not both
 *
 * @author  Guy Divita 
 * @created June 26, 2012
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.vtt;



import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.utils.U;



  public class ToVTTRefinery extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter  {


    private List<ToVTT> vttWriters = null;
    // =======================================================
    /**
     * Constructor ToVTT creates a vtt writer. 
     *   
     * @param pVttDir,
     * @param pOutputTypes
     * @throws ResourceInitializationException 
     *
     */
    // =======================================================
    public ToVTTRefinery(String pVttDir, String[] pOutputTypes) throws ResourceInitializationException {
      

     
      this.initialize(pVttDir, pOutputTypes );
      
      
    } // end Constructor()  ---------------------


    // =================================================
    /**
     * Constructor
     *
     * @param uimaContext
     * @param args
     * @throws ResourceInitializationException 
     * 
     * [TTD]   this method is too new - can't pass the analysis engine yet
    **
    // =================================================
    public ToVTTRefinery(AnalysisEngine pAnalysisEngine, String[] args) throws ResourceInitializationException {
      
      this.analysisEngine = pAnalysisEngine;
      
      initialize( args);
      UimaContext aContext = this.analysisEngine.getUimaContext();
      
      initialize( aContext  );
      
    } */
    
 // =======================================================
    /**
     * Constructor ToVTT creates a vtt writer. 
     *   
     * @param pArgs
     * 
     * @throws ResourceInitializationException 
     *
     */
    // =======================================================
    public ToVTTRefinery(String[] pArgs ) throws ResourceInitializationException {
      
      
      this.initialize(pArgs );
      
      
    } // end Constructor()  ---------------------

    




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
      
      for (ToVTT vttWriter: this.vttWriters) 
        vttWriter.process(pJCas);
      
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
      
      
      String args[] = null;
      args                  = (String[]) aContext.getConfigParameterValue("args");
      if ( args != null && args.length > 0 )
        initialize ( args);
      else {
        super.initialize(aContext);
      }
      
    } // end Method initialize() --------------


    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param pArgs
     * 
     **/
    // ----------------------------------
    public void initialize(String pArgs[]) throws ResourceInitializationException {
      
      String   outputDir =  U.getOption(pArgs,  "--outputDir=",   "/someDir" ) + "/vtt"; 
      String outputTypez =  U.getOption(pArgs,  "--outputTypes=", "Concept:Snippet");
      
      String[] outputTypes = U.split(outputTypez, ":");
      initialize( outputDir, outputTypes  );

    } // end Method initialize() --------------



    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param pOutputDir
     * @param pLabels
     * 
     **/
    // ----------------------------------
    public void initialize(String pOutputDir, String[] pOutputTypes) throws ResourceInitializationException {
      
     
      
      this.vttWriters = new ArrayList<ToVTT>();
      for ( String outputType: pOutputTypes ) {
        String vttDir = pOutputDir + "/vtt/" + outputType;
        try {U.mkDir( vttDir); } catch (Exception e) {};
        String[] outputTypes = new String[1]; 
        outputTypes[0] = outputType;
        
        vttWriters.add( new ToVTT( vttDir, outputTypes ));
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
    private int counter = 0;
   
    public static Color[][]  colors = null;
    public static String[] colorNames = null;
   
  
   

} // end Class toCommonModel
