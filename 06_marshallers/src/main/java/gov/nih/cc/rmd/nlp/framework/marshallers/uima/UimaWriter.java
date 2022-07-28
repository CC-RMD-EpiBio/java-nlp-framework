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
 * ToUima is a CAS Consumer that transforms
 * that passes a cas through as an object
 * 
 * @author  Guy Divita 
 * @created March 4 2019
 *
 *  
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.uima;


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



public class UimaWriter extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter  {



    // =======================================================
    /**
     * Constructor ToXMI 
     *   (used for streaming xmi applications, no fileName or output dir needed)
     */
    // =======================================================
    public UimaWriter() {
    
    } // end Constructor ------------------------

    

    // ==========================================
    /**
     * ToXMI constructor with "--outputDir=" with path that an ./xmi dir will be added
     *
     * @param pArgs
     * @throws ResourceInitializationException 
     */
    // ==========================================
    public UimaWriter(String[] pArgs) throws ResourceInitializationException {
     
       initialize( pArgs);
    }  // end Constructor ========================
    
   



    // =======================================================
    /**
     * processToCas
     * 
     * @param jCas
     * @return String
     */
    // =======================================================
    public Object processToCas(JCas pJCas) {
      
      Object returnVal = null;
      try {
          returnVal = pJCas;
      } catch (Exception e) {
         System.err.println("Ussue with passing the cas thru  :" + e.toString());
      
      }
      return returnVal;
    } // End Method processToCas() ======================
    
    
   
    /* (non-Javadoc)
     * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
     */
    @Override
    public void process(JCas arg0) throws AnalysisEngineProcessException {
      // TODO Auto-generated method stub
    }

    // -----------------------------------------
    /** 
     * destroy 
     *
     */
    // -----------------------------------------
    @Override
    public void destroy() {
      try {
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
      
     
      try {
        
      
        
      } catch (Exception e) {
        e.getStackTrace();
        GLog.println( GLog.ERROR_LEVEL, this.getClass(), "initialize", "Issue here " + e.toString());
        throw new ResourceInitializationException();
      }
      
    
    } // end Method initialize() --------------


    // ----------------------------------------
    // Global variables
    // ----------------------------------------
    private ProfilePerformanceMeter  performanceMeter = null;
      
    
    
   
} // end Class ToXMI
