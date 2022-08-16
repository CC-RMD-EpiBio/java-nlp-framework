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
// ------------------------------------------------------------
/**
 * An api annotating syntatic elements in documents
 * The input is a string that is at the document level
 * The output is pipe delimited string that is in VTT
 * style  
 * 
 * @author Divita
 * Feb 14, 2013
 * 
 */
// -------------------------------------------------------------
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.uima.analysis_engine.AnalysisEngine;

import gov.nih.cc.rmd.nlp.framework.marshallers.frameworkObject.FrameworkObject;
import gov.nih.cc.rmd.nlp.framework.pipeline.SyntaticPipeline;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;


// -----------------------------------
/**
 * Class Types
 *
 */
// ------------------------------------
public class SyntaticAPI extends ApplicationAPI {

  
//=======================================================
 /**
  * Constructor SyntaticAPI 
  *
  */
  // ====================================================
 public SyntaticAPI() throws Exception {
 
   String dummyArgs[] = null;
  
   init( dummyArgs);
   
  } // end Constructor() ----------------------------

 //=======================================================
 /**
  * Constructor SyntaticAPI 
  * 
  * @param pArgs 
  *
  */
 // =====================================================
     public SyntaticAPI( String[] pArgs) throws Exception {
       
       init( pArgs);
       
      } // end Constructor() ----------------------------

 
  // =======================================================
  /**
   * init 
   *
   * @param pArgs
   */
  // =======================================================
  public void init(String[] pArgs) throws Exception {

    
    try {
      // --------------------
      // Read in any command line arguments, and add to them needed ones
      // The precedence is command line args come first, followed by those
      // set by the setArgs method, followed by the defaults set here.
      // --------------------

        
      String args[]  = SyntaticApplication.setArgs(pArgs );
      
      GLog.setLogDir( U.getOption(args,  "--logDir=", "./logs" ));
      
      // -------------------
      // Create an engine with a pipeline, attach it to the application
      // -------------------
      SyntaticPipeline syntaticPipeline = new SyntaticPipeline(args );
    
      AnalysisEngine ae = syntaticPipeline.getAnalysisEngine( );
      super.initializeApplication( pArgs, ae);
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue initializing Line " + e.toString();
      System.err.println( msg);
      throw new Exception( msg);
      
    }
      
  } // end Method initialize() -----------------------------
  
  
  
  // ------------------------------------------
  /**
   * main
   *    See the setArgs method to see what specific command line 
   *    arguments should be passed in here.
   *
   * @param pArgs
   */
  // ------------------------------------------
  public static void main(String[] pArgs) {
    

    try {
    
      String[] args = SyntaticApplication.setArgs( pArgs);
      SyntaticAPI anApplication = new SyntaticAPI( args);

    
      // ------------------
      // gather and process the cas's
      // -----------------
   

   
     
      
      BufferedReader in = new BufferedReader( new InputStreamReader( System.in ));
      String row = null;
      System.err.print("input : ");
      while ( (row = in.readLine() )!= null && row.trim().length() > 0  ) {
         FrameworkObject results = anApplication.processToFrameworkObject(  row + "\n" );  // <---------------- all the work is done here
         
         
        System.out.println();
        System.out.println(results.getWordTokens().toString() );
        System.err.println("--------------------");
        System.err.print("input : ");
      }
    
      System.err.println("Finished");
    
    } catch (Exception e) {
      e.getStackTrace();
      String msg = "Issue processing the files " + e.toString();
      System.err.println(msg);
    }
    
  } // end Method main() --------------------------------------
    
 

  // End SyntaticAPI Class -------------------------------
}
