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
package gov.nih.cc.rmd.nlp.framework.marshallers.xmi;


import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;



public class ToXMI extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter  {



    // =======================================================
    /**
     * Constructor ToXMI 
     *   (used for streaming xmi applications, no fileName or output dir needed)
     */
    // =======================================================
    public ToXMI() {
    
    }

    // =======================================================
    /**
     * Constructor ToXMI will create the output directory if it does not
     * exist
     *
     * @param pXmiOutputDir
     * @throws Exception 
     * 
     */
    // =======================================================
    public ToXMI(String pXmiOutputDir) throws Exception {

        this.outputDir = pXmiOutputDir;
        this.extension = ".xmi";

        U.mkDir(this.outputDir);
        
        GLog.println("xmi Output going to " + this.outputDir);
        
    } // end Constructor() ---------------------

    // ==========================================
    /**
     * ToXMI constructor with "--outputDir=" with path that an ./xmi dir will be added
     *
     * @param pArgs
     * @throws ResourceInitializationException 
     */
    // ==========================================
    public ToXMI(String[] pArgs) throws ResourceInitializationException {
     
       initialize( pArgs);
    }  // end Constructor ========================
    
    // -----------------------------------------
    /**
     * writeXMIFile writes out the xmi file for a given jcas 
     * 
     * @param outputFileName
     * @param pJCas
     */
    // -----------------------------------------
    public void write(String outputFileName, JCas pJCas)  {
    
      FileOutputStream xmiOut = null;
      try {
          File xmiOutFile = new File( outputFileName);
          xmiOut = new FileOutputStream(xmiOutFile);
          CAS aCas = pJCas.getCas();
          XmiCasSerializer.serialize(aCas, xmiOut);
          xmiOut.close();
         
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println("Ussue with writing the xmi version of | " + outputFileName +  " :" + e.toString());
         throw new RuntimeException();
      } finally {
          try {
            if ( xmiOut != null )
              xmiOut.close();
          } catch (Exception e) {
    
          }
      }
      
    } // end Method writeXMIFile() -------------------------



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
      
      this.performanceMeter.startCounter();
      String outputFileName = renderFileName( pJCas, this.outputDir );
      
      try {
        write( outputFileName, pJCas);
      } catch ( Exception e) {
        String msg = "Issue writing xmi file " + outputFileName + " \n" + e.getMessage() + "\n" + U.getStackTrace(e);
        System.err.println(msg);
      }
      this.performanceMeter.stopCounter();
    } // end Method process




    // =======================================================
    /**
     * processToXML 
     * 
     * @param jCas
     * @return String
     */
    // =======================================================
    public String processToXML(JCas pJCas) {
      
      ByteArrayOutputStream xmiOut = null;
      String returnVal = null;
      try {
         xmiOut = new ByteArrayOutputStream();
        
          CAS aCas = pJCas.getCas();
          XmiCasSerializer.serialize(aCas, xmiOut);
          returnVal = xmiOut.toString("UTF-8");
          xmiOut.close();
      } catch (Exception e) {
         System.err.println("Ussue with writing the xmi version :" + e.toString());
      } finally {
          try {
            if ( xmiOut != null )
              xmiOut.close();
          } catch (Exception e) {
    
          }
      }
      return returnVal;
    } // End Method processToXML() ======================
    




    // =======================================================
    /**
     * renderFileName figures out the input file Name, creates a path
     * to the appropriate output directory, with the appropriate extension on it.
     * 
     * @param pJCas
     * @param pOutputDir
     * @return
     */
    // =======================================================
    private String renderFileName(JCas pJCas, Object pOutputDir) {
     
      String outputFileName = null;
      
      // ----- retrieve the fileName from the documentHeader 
      String fullInputFileName = VUIMAUtil.getDocumentId(pJCas);
      
      // ----- just get the fileName
      if ( fullInputFileName == null ) fullInputFileName = "InputFile" + this.counter++;
      String name = U.getOnlyFileName(fullInputFileName);
      String nameNoExtension = U.getFileNamePrefix(name);
      
      outputFileName = this.outputDir + "/" +  nameNoExtension + this.extension;
      
      return outputFileName;
      
    }  // End Method renderFileName() ======================
    


    // -----------------------------------------
    /** 
     * destroy 
     *
     */
    // -----------------------------------------
    @Override
    public void destroy() {
      try {
        
        GLog.println("xmi Output going to " + this.outputDir);
        this.performanceMeter.writeProfile( this.getClass().getSimpleName());
        
        
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, "Issue destroying XMI Writer " + e.toString());
        
      }
    } // end Method destroy() ----------

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
      try {
        args                  = (String[]) aContext.getConfigParameterValue("args");
          
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue with initializing xmi writer " + e.toString());
        throw new ResourceInitializationException();
      }
      
      initialize( args );
     
    } // end Method initialize() --------------


    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
     *
     * @param pArgs  
     * 
     *    make sure an --outputDir=  argument is on it - an /xmi directory will be created
     * 
     **/
    // ----------------------------------
    public void initialize(String pArgs[]) throws ResourceInitializationException {
      
      String pOutputDir = U.getOption(pArgs,  "--outputDir=", "/someOutputDir") + "/xmi";
      
      this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
      
      
      initialize( pOutputDir );
  
     } // end Method initialize() --------------

    
    
    
    
    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize( String pOutputDir ) throws ResourceInitializationException {
      
      this.outputDir = pOutputDir;
      try {
        
        U.mkDir(pOutputDir);
        
        GLog.println("xmi Output going to " + this.outputDir);
        
      } catch (Exception e) {
        e.getStackTrace();
        String msg = "Issue creating the xmi outputDir " + e.getMessage();
        System.err.println(msg);
        throw new ResourceInitializationException();
      }
      
    
    } // end Method initialize() --------------


    // ----------------------------------------
    // Global variables
    // ----------------------------------------
    private String outputDir = null;
    private String extension = ".xmi";
    private int counter = 0;
    private ProfilePerformanceMeter  performanceMeter = null;
    
   
} // end Class ToXMI
