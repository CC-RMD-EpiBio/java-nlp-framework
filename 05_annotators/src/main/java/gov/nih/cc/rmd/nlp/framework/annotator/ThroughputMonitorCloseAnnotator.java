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
