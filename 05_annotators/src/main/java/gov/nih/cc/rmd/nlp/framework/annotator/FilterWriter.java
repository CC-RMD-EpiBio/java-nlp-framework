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
 * FilterWriter filters to only those labels that are passed in
 * when the class is instantiated, or re-initialized.
 * 
 * 
 *        
 * @author  Guy Divita 
 * @created September 7, 2013
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;


import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.VAnnotation;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class FilterWriter extends JCasAnnotator_ImplBase {
 
  
  
  private Boolean filterOutNegation;
  // -----------------------------------------
  /**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
    this.performanceMeter.startCounter();
	  if ( this.labelHash != null && this.labelHash.size() > 0 ) {

	   
	    
		  List<Annotation>annotations = UIMAUtil.getAnnotations(pJCas );
		  
		  if ( annotations != null ) {
			  
			  for ( Annotation annotation : annotations ) {
				
				  String label = U.getNameWithoutNameSpace( annotation.getType().getName());
				  // System.err.println(" label = " + label );
				  boolean isAsserted = isAnnotationAsserted(annotation);
				  
				  //System.err.println("-----------------comparing -------------> " + label );
				  if ( this.mustKeepHash.contains( label)  || this.labelHash.contains( label )) {
				    if ( this.filterOutNegation && !isAsserted ) {
				      annotation.removeFromIndexes(pJCas);
				      annotation.removeFromIndexes(pJCas);
				      
				      //System.err.println("Removing " + label + " " + annotation.getCoveredText());
				    } else {
				     // System.err.println("Keepingx= " + label );
				    }
				  } else {
				   //  System.err.println("removing " + label + " " + annotation.getCoveredText());
				    annotation.removeFromIndexes(pJCas);
				    annotation.removeFromIndexes(pJCas);
				  }
				 
				  
			  } // end loop through annotations
			  
		  } // end of if there are any annotations
		  
	  } // end if there are any filters  
	
	  // ----------------------------------
	  // Filter out String and FSArrays from the annotations
	 // UIMAUtil.removeArrayElements(pJCas);
	
	 
	  
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
     //  throw new AnalysisEngineProcessException();
    }
    
    this.performanceMeter.stopCounter();
  } // end Method process() ----------------

  
  

  // =======================================================
  /**
   * isAnnotationAsserted [Summary here]
   * 
   * @param annotation
   * @return boolean
   */
  // =======================================================
  private boolean isAnnotationAsserted(Annotation pAnnotation) {
    boolean returnValue = false;
   
    String negationStatus = null;
    try {
      negationStatus = ((VAnnotation) pAnnotation).getNegation_Status();
    } catch (Exception e ) {
      try {
        negationStatus = ((gov.va.vinci.model.Concept) pAnnotation).getAssertionStatus();
      } catch (Exception e2) {}
    }
      
      
    if (negationStatus == null )
      returnValue = true;
    else if (negationStatus.toLowerCase().equals("asserted"))
      returnValue = true;
    else
      returnValue = false;
    
   
    return returnValue;
  }  // End Method isAnnotationAsserted() ======================
  



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
    
  
    String             args[] = null;
    
   
    try {
    	args              = (String[]) aContext.getConfigParameterValue("args");
    	 
   
    initialize( args);
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issuew with the filter " + e.toString());
      throw new ResourceInitializationException();
    }
    
  } // end Method initalize() -----------------------------------------
  


  //----------------------------------
  /**
   * initialize initalizes the list of outputTypes
   * 
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String [] pArgs ) throws ResourceInitializationException {
    

    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
   
    String outputTypes[]  = null;
    if (  pArgs != null && pArgs.length > 0 ) {
      
      String outputTypez = U.getOption(pArgs, "--outputTypes=", "");
     if ( outputTypez != null && outputTypez.length() > 0 ) {
      if ( outputTypez.contains(":")) outputTypez = outputTypez.replaceAll(":", "|");
       outputTypes = U.split(outputTypez);
     }
     filterOutNegation = Boolean.valueOf(U.getOption(pArgs, "--filterOutNegations=",  "false"));
   }
   
    initialize( outputTypes, filterOutNegation);
    
  
  } // end Method initialize() -------
  

  
  
  //----------------------------------
  /**
   * initialize initalizes the list of outputTypes
   * 
   **/
  // ----------------------------------
  public void initialize(String [] pOutputTypes, boolean pFilterOutNegation) throws ResourceInitializationException {
    
    
    this.filterOutNegation = pFilterOutNegation;
    
	  if ( pOutputTypes != null && pOutputTypes.length > 0 ) {
	  
		  this.labelHash= new HashSet<String>( pOutputTypes.length );
		  this.mustKeepHash = new HashSet<String>( mustKeepList.length);
   
		  for ( String outputType : pOutputTypes) {
		    try {
		      if ( outputType != null ) {
		      String label = U.getNameWithoutNameSpace( outputType);
		      this.labelHash.add( label);
		    
		      }
		      } catch (Exception e) {
		      e.printStackTrace();
		     // System.err.println("Issue with filer label " + outputType + " " + e.toString());
		    }
		    }
		  
		  for ( String mustKeep : mustKeepList ) {
			  this.mustKeepHash.add( mustKeep);
			 
		  }
		  
	  }
    
  } // end Method initialize() -------
  

  
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private HashSet<String> labelHash = null;
   private HashSet<String> mustKeepHash = null;
   private String[] mustKeepList = { "DocumentHeader", "SourceDocumentInformation", "CSI" };
   ProfilePerformanceMeter              performanceMeter = null;
  

   ;
  
} // end Class FilterWriter() ---------------
