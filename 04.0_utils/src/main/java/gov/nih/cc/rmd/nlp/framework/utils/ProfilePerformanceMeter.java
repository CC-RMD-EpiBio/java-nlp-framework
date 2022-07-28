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
// =================================================
/**
 * PerformanceMeter.java Summary
 *  Class Detail
 *
 *
 * @author  Guy Divita 
 * @created Feb 29, 2012
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils;

import java.io.File;
import java.io.PrintWriter;



public class ProfilePerformanceMeter extends PerformanceMeter {

 

  // -----------------------------------------
  /**
   * Constructor: PerformanceMeter - This constructor
   * sends all messages to the bit bucket
   *
   * @param pArgs  an array list 
   * 
   */
  // -----------------------------------------
  public ProfilePerformanceMeter( String pArgs[], String pClassName )  {
	  super();
	    try {
	    String logDir         = U.getOption(pArgs, "--logDir=", "./");
	    this.profilePerformanceLogging = Boolean.parseBoolean(U.getOption(pArgs,  "--profilePerformanceLogging=", "false"));
	    boolean printToLog = Boolean.valueOf(U.getOption(pArgs, "--printToLog=", "false"));
	    if (!printToLog ) this.profilePerformanceLogging = false;   // <---- if we are not printing logs, don't profile
	    
	    PrintWriter o = null;
	    if ( profilePerformanceLogging ) {
	    String profileDir     = logDir + "/profiles";
	    String dateStamp      = U.getDateStampSimple();
	    this.className        = pClassName;
	    File p = new File( profileDir);
	    if ( !p.exists())
	      U.mkDir(profileDir);
	   
	    this.logName = profileDir + "/" + pClassName + "_" + dateStamp  + ".log";
	    o = new PrintWriter( logName);
	    } else {
	      NullOutputStream nullOutputStream = new NullOutputStream();
	     o = new PrintWriter( nullOutputStream ) ;
	     
	    }
	    this.setOut(o);
	   
	  } catch (Exception e) {
	    e.printStackTrace();
	    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "constructor", "Issue opening the profile meter for " + pClassName + " " + e.toString());
	  
	    
	  }
	  }
   

@Override
  public void stopCounter(String pMsg) {
	super.stopCounter();
	try {
	  if (  this.profilePerformanceLogging ) {
		PrintWriter o = null;
		o = this.getOut();
		if ( o == null ) {
			o = new PrintWriter( this.logName);
			this.setOut(o);
			
		} 
	  // super.getOut().print(pMsg);
		this.writeProfile( this.className);
	  }
	} catch (Exception e) {
		e.printStackTrace();
		GLog.println(GLog.ERROR_LEVEL, this.getClass(), "stopCounter", "Issue writing the profile time file ");
	}
	
	// Open and write out the current number
	
	
   
  } // End Method stopCounter() ======================
  
  
  // =================================================
/**
 * stopCounter 
 * 
*/
// =================================================
@Override
public void stopCounter() {
  this.stopCounter("");
  
} // End Method stopCounter() ----------------------


  // ------------------
   // Class variables
   // -----------------
   private String logName = null;
   private boolean profilePerformanceLogging = false;
   private String className = null;
  
  
  
} // end Class ProfilePerformanceMeter
