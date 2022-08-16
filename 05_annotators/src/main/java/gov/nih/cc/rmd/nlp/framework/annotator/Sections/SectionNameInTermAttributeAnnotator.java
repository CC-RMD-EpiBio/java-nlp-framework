// =================================================
/**
 * This annotator stuffs sectionNames into LexicalElements
 *   
 *
 * @author  Guy Divita 
 * @created Aug 18 2030
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
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.PageFooter;
import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.model.SectionName;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Delimiter;
import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Line;
import gov.va.chir.model.SlotValue;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class SectionNameInTermAttributeAnnotator extends JCasAnnotator_ImplBase {
 
 
  public static final String annotatorName = SectionNameInTermAttributeAnnotator.class.getSimpleName();

  
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    
    this.performanceMeter.startCounter();
    
    try {
    
     List<Annotation> sectionZones = UIMAUtil.getAnnotations(pJCas, SectionZone.typeIndexID );
     
     if ( sectionZones != null && !sectionZones.isEmpty()) 
       for ( Annotation sectionZone : sectionZones )
         processSectionZone( pJCas, sectionZone );
       
     
    
      
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue in CCDA-Section Annotator " + e.toString());
    }
    
    this.performanceMeter.stopCounter();     
    
  } // End Method process() ------------------
  
  
  // =================================================
  /**
   * processSectionZone goes through the terms in the
   * sectionzone and adds the sectionName to each term
   * 
   * @param pJCas
   * @param pSectionZone
  */
  // =================================================
  private final void processSectionZone(JCas pJCas, Annotation pSectionZone) {
    
    if ( pSectionZone != null ) {
      
      String sectionName = ((SectionZone) pSectionZone ).getSectionName() ;
      
      if ( sectionName  != null ) {
        List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSectionZone.getBegin(), pSectionZone.getEnd(), true );
    
        if ( terms != null && !terms.isEmpty() ) 
          for ( Annotation term : terms ) {
            ((LexicalElement) term).setSectionName( sectionName );
          }
        }
    }
      
    
    
  } // end Method processSectionZone() -------





/**
  * destroy
  * 
  **/
 public void destroy() {
    if ( this.performanceMeter != null )
      this.performanceMeter.writeProfile( this.getClass().getSimpleName());
 } // end Method destroy() ----------

 
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
     
    
     
   } catch (Exception e ) {
     
   }
   initialize( args );
   
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
    GLog.println(GLog.ERROR_LEVEL, "Issue initializing the " + annotatorName + " " + e.toString() );
    throw new ResourceInitializationException();
  }
  

  
} // end Method initialize()






 // ---------------------------
 // Global Variables
 // ---------------------------

 
 private ProfilePerformanceMeter              performanceMeter = null;
 
} // end Class ExampleAnnotator() ---------------
