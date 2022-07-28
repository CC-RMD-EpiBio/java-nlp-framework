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
