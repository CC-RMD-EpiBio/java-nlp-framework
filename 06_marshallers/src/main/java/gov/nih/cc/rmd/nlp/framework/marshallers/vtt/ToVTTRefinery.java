/*******************************************************************************
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                       Epidemiology and Biostatistics Branch 
 *                                            2019 - 2022
 *   ---------------------------------------------------------------------------
 *   Copyright Notice:
 *   This software was developed and funded by the National Institutes of Health
 *   Clinical Center (NIHCC), part of the National Institutes of Health (NIH),
 *   and agency of the United States Department of Health and Human Services,
 *   which is making the software available to the public for any commercial
 *   or non-commercial purpose under the following open-source BSD license.
 *  
 *   Government Usage Rights Notice:
 *   The U.S. Government retains unlimited, royalty-free usage rights to this 
 *   software, but not ownership, as provided by Federal law. Redistribution 
 *   and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *      1. Redistributions of source code must retain the above copyright
 *         and government usage rights notice, this list of conditions and the 
 *         following disclaimer.
 *  
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *        
 *      3. Neither the names of the National Institutes of Health Clinical
 *         Center, the National Institutes of Health, the U.S. Department of
 *         Health and Human Services, nor the names of any of the software
 *         developers may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
 *   
 *      4. The U.S. Government retains an unlimited, royalty-free right to
 *         use, distribute or modify the software.
 *   
 *      5. Please acknowledge NIH CC as the source of this software by including
 *         the phrase: "Courtesy of the U.S. National Institutes of Health Clinical Center"
 *          or 
 *                     "Source: U.S. National Institutes of Health Clinical Center."
 *  
 *     THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS
 *     IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *     TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *     PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 *     OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *     EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *     PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *     NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *     When attributing this code, please make reference to:
 *        Divita G, Carter ME, Tran LT, Redd D, Zeng QT, Duvall S, Samore MH, Gundlapalli AV. 
 *        v3NLP Framework: tools to build applications for extracting concepts from clinical text. 
 *        eGEMs. 2016;4(3). 
 *      
 *     In the absence of a specific paper or url listed above, reference https://github.com/CC-RMD-EpiBio/java-nlp-framework
 *   
 *     To view a copy of this license, visit https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD
 *******************************************************************************/
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
