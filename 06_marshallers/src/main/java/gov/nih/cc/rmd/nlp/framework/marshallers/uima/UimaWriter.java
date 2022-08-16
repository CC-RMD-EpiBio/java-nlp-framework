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
