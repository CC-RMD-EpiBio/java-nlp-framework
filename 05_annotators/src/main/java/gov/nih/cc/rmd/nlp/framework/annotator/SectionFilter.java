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
 * SectionFilter filters out all annotations that are not within the
 * bounds of these sections  .  The filter will keep annotations that
 * are DocumentHeader, CSI, or Top.  If the list of sections passed in is
 * empty, no filtering happens.
 *
 *
 * @author  Guy Divita 
 * @created April 6, 2017
 *
 * *  
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

import gov.va.chir.model.Section;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.Concept;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

public class SectionFilter extends JCasAnnotator_ImplBase {

  @Override
  // -----------------------------------------
  /**
   * process takes tokens and whitespace tokens as input and returns
   * sentences
   *
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    this.performanceMeter.startCounter();

    try {
     
      if ( this.filter ) {
      
      List<Annotation> allAnnotations = UIMAUtil.getAnnotations(pJCas);
      markAnnotations( allAnnotations, false);
    
    if ( this.sectionNames != null && !this.sectionNames.isEmpty() ) {
      
      List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, Section.typeIndexID, true );
      
     
      if ( sections != null && !sections.isEmpty()) {
        // -----------------------------
        // make this list a kill list containing only those sections that need to be deleted
        for ( Annotation section: sections ) {
          String aSectionName = ((Section)section).getSectionName();
          if ( aSectionName != null ) {
            if ( this.sectionNames.contains(aSectionName.toLowerCase() )) { 
             markAllAnnotationsInSectionToKeep(pJCas, section);
             System.out.println("Keeping section :" + aSectionName);
            }
          }
          
        } // loop thru the sections
      
      } // end if there are sections 
  
      
    List<Annotation> allVAnnotations = UIMAUtil.getAnnotations( pJCas, VAnnotation.typeIndexID, true);
    
    removeUnmarkedAnnotations( pJCas, allVAnnotations);
    
    List<Annotation> allConcepts = UIMAUtil.getAnnotations(pJCas, Concept.typeIndexID, true);
    removeUnmarkedAnnotations( pJCas, allConcepts);
    
    } // end if any sections need to be removed 

      } // end if there is a filter to be had
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
      // throw new AnalysisEngineProcessException();
    }

    this.performanceMeter.stopCounter();
   
    

  } // end Method process() ------------------

//=======================
 /**
  * markAllAnnotationsInSectionToKeep marks all annotations within this sections bounds
  *
  * @param pJCas
  * @param section
  */
 // =======================
 private void markAllAnnotationsInSectionToKeep(JCas pJCas, Annotation section) {
	 
	 if ( section != null ) {
		 markAnnotation( section, true);
		 List<Annotation> allAnnotations = UIMAUtil.getAnnotationsBySpan(pJCas, section.getBegin(), section.getEnd());
	 
		 if ( allAnnotations != null && !allAnnotations.isEmpty()) 
			 for ( Annotation annotation: allAnnotations ) 
				 
				 markAnnotation( annotation, true);
		 
	 }
 
 } // end Method markAllAnnotationsInSectionToKeep() ------------------

 

  // =======================
  /**
   * removeUnmarkedAnnotations removes all annotations within this list
   *
   * @param pJCas
   * @param pAnnotationsToRemove
   */
  // =======================
  private void removeUnmarkedAnnotations(JCas pJCas, List<Annotation> pAnnotationsToRemove) {
  
   
    if ( pAnnotationsToRemove != null && !pAnnotationsToRemove.isEmpty() ) {
      for ( Annotation annotation : pAnnotationsToRemove ) {
    	  boolean keep = false;
    	  try {
    		    
				keep = ((VAnnotation) annotation).getMarked();
			 } catch (Exception e) {
				 try { 
					 keep = ((Concept) annotation).getMarked();
				 } catch (Exception e1) {
					 // not one of mine - ignore
				 }
			 }
    	  if ( !keep ) {
    		  try {
    			//String sectionName =  ((Section) annotation).getSectionName();
    			//if ( sectionName != null) System.out.println("Section to remove " + sectionName);
    		  } catch (Exception e) {}
    		  annotation.removeFromIndexes();
    	  }
      }
    }
   
    
   } // End Method removeAllAnnotionsBetween =======
  
  
  // =======================
  /**
   * markAnnotation marks those annotations that derive from VAnnotation or Concept
   *
   * @param pJCas
   * @param pAnnotations
   * @param pValue
   */
  // =======================
  private void markAnnotations ( List<Annotation> pAnnotations, boolean pValue) {
			 
	  if ( pAnnotations != null && !pAnnotations.isEmpty())
		  for ( Annotation annotation: pAnnotations)
			  markAnnotation( annotation, pValue);
			  
  } // End Method markAnnotations =======
  
  

  // =======================
  /**
   * markAnnotation marks those annotations that derive from VAnnotation or Concept
   *
   * @param pJCas
   * @param pAnnotation
   * @param pValue
   */
  // =======================
  private void markAnnotation ( Annotation pAnnotation, boolean pValue) {
	  
	  try {
			 ((VAnnotation) pAnnotation).setMarked(pValue);
		 } catch (Exception e) {
			 try { 
				 ((Concept) pAnnotation).setMarked(pValue);
			 } catch (Exception e1) {
				 // not one of mine - ignore
			 }
		 }
	  
  } // End Method markAnnotation() =======
  


  // ----------------------------------
  /**
   * destroy
   * 
   **/
  // ----------------------------------
  public void destroy() {
    this.performanceMeter.writeProfile(this.getClass().getSimpleName());
  }

  // ----------------------------------
  /**
   * initialize loads in the resources needed for slotValues. Currently, this involves
   * a list of known slot names that might show up.
   *
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {

    String[] args = null;
    try {
      args = (String[]) aContext.getConfigParameterValue("args");

     
      
    } catch (Exception e) {
     
    }
    
    initialize( args);
  } // end Method initialize() ---------------
  

  // ----------------------------------
  /**
   * initialize loads in the resources needed for slotValues. Currently, this involves
   * a list of known slot names that might show up.
   *
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(String pArgs[] ) throws ResourceInitializationException {

   
    try {
      this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getSimpleName());
      
      String sectionNamesString = U.getOption(pArgs,  "--sectionNames=", "");

      if ( sectionNamesString != null && sectionNamesString.length() > 0) {
       String[] sectionNamez = U.split(sectionNamesString, ":");
       this.sectionNames = new HashSet<String>(sectionNamez.length);
       for ( String name : sectionNamez ) 
         if ( name.trim().length() > 0 )  {
           this.sectionNames.add( name.toLowerCase());
           this.filter = true;
           System.out.println("Section name to hash :" + name.toLowerCase());
         }
         
     
      
      annotationsToKeep = new HashSet<String>();
    //  annotationsToKeep.add( "DocumentHeader");
    //  annotationsToKeep.add( "CSI");
    //  annotationsToKeep.add( "Document");
      annotationsToKeep.add( "Top");
      }
      
    } catch (Exception e) {
      System.err.println("Issue - initializing " + this.getClass().getName());
      throw new ResourceInitializationException();
    }

   
  } // end Method initialize() ---------------


  // -----------------------------------------
  // class Variables
  // -----------------------------------------

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  private boolean filter = false;
  private ProfilePerformanceMeter performanceMeter = null;
  private HashSet<String> sectionNames = null;
  private HashSet<String> annotationsToKeep = null;
 

} // end Class SectionTokenizerSimple() -----------
