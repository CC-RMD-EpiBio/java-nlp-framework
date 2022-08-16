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
// =================================================
/**
 * Throughput annotator monitor is an annotator that records
 * how fast records are passing through this annotator.  The
 * annotator records to a static separate method that captures
 * all instances of this annotator across a process (if multi-threaded)
 * 
 * The monitor creates/overwrites a file every $XXXX instances
 * with throughput statistics in it. 
 * 
 * @author  Guy Divita 
 * @created Dec 11, 2015
 *
 **   *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;


import java.io.File;
import java.io.PrintWriter;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;



public class ThroughputMonitorCloseAnnotator extends JCasAnnotator_ImplBase {
 
  public static final String annotatorName = ThroughputMonitorCloseAnnotator.class.getSimpleName();

  
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   try {
    synchronized(this) {
      performanceMeter.processedAnother();
      
      if ( counter % metric == 0) {
        report();
      }
      counter++;
      
    }
	 
    
   } catch (Exception e) {
     e.printStackTrace();
     System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
     throw new AnalysisEngineProcessException();
   }
    
  } // end Method process() ----------------
   
   

  // =======================================================
  /**
   * report writes out a file
   * 
   */
  // =======================================================
  private void report() {
    
    try {
      PrintWriter out = new PrintWriter( performanceMeterName );
    
      out.print( performanceMeter.tell());
      out.close();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with writing out the file " + performanceMeterName + " " + e.toString());
    }
  }  // End Method report() ======================
  





        //----------------------------------
	      /**
	       * destroy
	      * 
	       **/
	      // ----------------------------------
	      public void destroy() {
	        this.performanceMeter.writeProfile( this.getClass().getSimpleName());
	      }

	       
//----------------------------------
 /**
  * initialize loads in the resources needed for phrase chunking. 
  * 
  * @param aContext
  * 
  **/
 // ----------------------------------
 public void initialize(UimaContext aContext) throws ResourceInitializationException {
   
  String[] args = null;
   try {
    
	   args                 = (String[]) aContext.getConfigParameterValue("args");  
       
       initialize( args );
      
   } catch (Exception e) {
	  e.printStackTrace();
     System.err.println("Issue creating hte ThroughputMonitorAnnotator " + e.toString());
     throw new ResourceInitializationException();
   }
  
 } // end initialize()


//----------------------------------
/**
* initialize loads in the resources needed for phrase chunking. 
* 
* @param pArgs
* 
**/
// ----------------------------------
public void initialize(String pArgs[]) throws ResourceInitializationException {
	 
	String dateStamp = U.getDateStampSimple();
    String logDir               = U.getOption(pArgs, "--logDir=", "./logs");
    this.performanceMeterName   = logDir + "/throughput_" + dateStamp + ".log" ;
    this.metric                 = Integer.parseInt(U.getOption(pArgs,  "--metric=", "10"));
   
    
    File logDir_ = new File ( logDir );
    if ( !logDir_.exists()  ) { 
    	try {
    		U.mkDir(logDir );
    	} catch (Exception e) {
    		e.printStackTrace();
    		GLog.println("Issue making the log dir for throughtput Monitor "+ e.toString());
    		throw new ResourceInitializationException();
    	}
    }
    counter = 0;
    this.performanceMeter     = new PerformanceMeter( );
    
    
} // end Method initialize() 
 // ---------------------------
 // Global Variables
 // ---------------------------
 private String performanceMeterName = "/some/path/to/outputDir/logs/someServiceName_2015_12_11.log";
 private  PerformanceMeter  performanceMeter = null;
 private  long counter = 0;
 private  int metric = 10;
  
} // end Class ExampleAnnotator() ---------------
