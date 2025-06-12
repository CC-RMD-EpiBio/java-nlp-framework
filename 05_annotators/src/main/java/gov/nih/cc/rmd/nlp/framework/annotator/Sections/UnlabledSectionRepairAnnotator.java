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
 * UnlabledSectionRepairAnnotator finds unlabeled sections
 * and combines each into the closest prior named section
 *   
 *
 * @author  Guy Divita 
 * @created Jan 08 2018
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.Sections;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.PageFooter;
import gov.nih.cc.rmd.framework.PageHeader;
import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class UnlabledSectionRepairAnnotator extends JCasAnnotator_ImplBase {
 
 
  public static final String annotatorName = UnlabledSectionRepairAnnotator.class.getSimpleName();
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   * From the bottom up, we are looking for the first content heading that does not have
   * "date", "by", "page", "report" or "Patient" in the name  that is doesn't have  shapes (location, phone, person
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    
    this.performanceMeter.startCounter();
    
    try { 
      
      List<Annotation> sectionZones = UIMAUtil.getAnnotations(pJCas, SectionZone.typeIndexID, false );     
      
      if ( sectionZones!= null && !sectionZones.isEmpty()) {
        
        UIMAUtil.uniqueAnnotations(sectionZones);
        UIMAUtil.sortByOffset(sectionZones);
        
        for ( Annotation section : sectionZones ) { 
          String sectionName = ((SectionZone) section).getSectionName();
          if ( sectionName == null || sectionName.toLowerCase().contains("unlabeled section")  ) 
            if ( !inHeader( pJCas, section) && !inFooter(pJCas, section ))
              VUIMAUtil.repairUnknownSectionZones(pJCas, section);
          
          
        }
      }
    
    } catch ( Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", "Issue in Unlabeled Section Repair Annotator " + e.toString());
      
      
    }
    
    this.performanceMeter.stopCounter();     
    
  } // End Method process() ------------------
  
 

 // =================================================
  /**
   * inHeader returns true if section is within a page header
   * 
   * @param pJCas
   * @param section
   * @return boolean
  */
  // =================================================
 private final boolean inHeader(JCas pJCas, Annotation section) {
  boolean returnVal = false;
    
    if ( section != null ) {
      List<Annotation> header = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, PageHeader.typeIndexID,  section.getBegin(), section.getEnd());
      
      if ( header != null && !header.isEmpty())
        returnVal = true;
    }
    return returnVal;
  }



  // =================================================
  /**
   * inFooter returns true if this section is within a footer
   * 
   * @param pJCas
   * @param section
   * @return boolean
  */
  // =================================================
  private boolean inFooter(JCas pJCas, Annotation section) {
    boolean returnVal = false;
    
    if ( section != null ) {
      List<Annotation> footer = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, PageFooter.typeIndexID,  section.getBegin(), section.getEnd());
      
      if ( footer != null && !footer.isEmpty())
        returnVal = true;
    }
    return returnVal;
  }



//----------------------------------
 /**
  * initialize
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
     
   } catch (Exception e ) {
     e.printStackTrace();
     String msg = "Issue initializing " + annotatorName + " " + e.toString() ;
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", msg);
     throw new ResourceInitializationException();
   }
   
 } // end Method initialize() --------
 
//----------------------------------
/**
 * initialize loads in the resources needed
 * 
 * @param pArgs
 * 
 **/
// ----------------------------------
public void initialize(String[] pArgs) throws ResourceInitializationException {
  
 
  try {
    
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
   
    
  } catch (Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", "Issue initializing the " + annotatorName + " " + e.toString() );
    throw new ResourceInitializationException();
  }
  
} // end Method Initialize() -----------


 // ---------------------------
 // Global Variables
 // ---------------------------
 private ProfilePerformanceMeter              performanceMeter = null;
 
} // end Class UnlabeledSectionRepairAnnotator() ---------------
