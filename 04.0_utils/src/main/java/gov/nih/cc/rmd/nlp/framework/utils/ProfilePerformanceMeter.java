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
