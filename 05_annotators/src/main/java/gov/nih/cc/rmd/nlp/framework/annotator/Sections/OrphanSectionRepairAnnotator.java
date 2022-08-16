// =================================================
/**
 * Orphan Section Repair finds content headings
 * and sections that have the same span.  When
 * they exist, demote the section to an unnamed
 * section, include the section name into the
 * unnamed section, and get rid of the content
 * header.
 *   
 *
 * @author  Guy Divita 
 * @created Jan 08 2018
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.Sections;

import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.ContentHeading;



public class OrphanSectionRepairAnnotator extends JCasAnnotator_ImplBase {
 
 
  public static final String annotatorName = OrphanSectionRepairAnnotator.class.getSimpleName();
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
          Annotation contentHeading = getContentHeadingForSection(pJCas, section );
          if ( contentHeading != null &&  sameSpan( section, contentHeading )) {
            String sectionName = ((SectionZone)section).getSectionName();
            if ( sectionName == null || !this.contentSectionNames.contains( sectionName.toLowerCase() )) {
              contentHeading.removeFromIndexes();
              ((SectionZone)section).setSectionName("Unlabeled Section");
            }
            
          }
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
   * getContentHeadingForSection returns the content heading
   * associated with this section
   * 
   * @param pJCas
   * @param section
   * @return Annotation
  */
  // =================================================
 private final Annotation getContentHeadingForSection(JCas pJCas, Annotation section) {
  Annotation returnVal = null;
    
    if ( section != null ) {
      List<Annotation> contentHeadings = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, ContentHeading.typeIndexID,  section.getBegin(), section.getEnd());
      
      if ( contentHeadings != null && !contentHeadings.isEmpty()) {
       returnVal = contentHeadings.get(0); 
      }
    }
    return returnVal;
  } // end Method getContentHeading() ---------------



  // =================================================
  /**
   * sameSpan returns true if the two annotations have the same span
   *
   * @param pAnnotation1
   * @param pAnnotation2
   * @return boolean
  */
  // =================================================
  private boolean sameSpan(Annotation pAnnotation1,  Annotation pAnnotation2) {
    boolean returnVal = false;
    
    if ( pAnnotation1 != null && pAnnotation2 != null) 
      if ( pAnnotation1.getBegin() == pAnnotation2.getBegin() &&
           pAnnotation1.getEnd()   == pAnnotation2.getEnd() )
        returnVal = true;
 
    return returnVal;
  } // end Method sameSpan() -------



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
   
    String contentSections = U.getOption(pArgs, "--contentSectionNames=", "resources/com/ciitizen/framework/sections/contentSectionNames.txt" );
   
    loadContentSections( contentSections );
    
  } catch (Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", "Issue initializing the " + annotatorName + " " + e.toString() );
    throw new ResourceInitializationException();
  }
  
} // end Method Initialize() -----------


// =================================================
/**
* loadContentSections 
* 
* @param pContentSectionsFile
* @throws ResourceInitializationException 
*/
//=================================================
private void loadContentSections(String pContentSectionsFile ) throws ResourceInitializationException {
 try {
 
  String[] rows = U.readClassPathResourceIntoStringArray(pContentSectionsFile);
 
  if ( rows != null && rows.length > 0 ) {
    this.contentSectionNames = new HashSet<String>(rows.length );
    for ( String row : rows ) {
      if ( row != null && !row.startsWith( "#")) {
        String cols[] = U.split(row);
        this.contentSectionNames.add( cols[0].trim().toLowerCase());
      }
    }
    
  }
  
 } catch (Exception e ) {
   e.printStackTrace();
   GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadContentSections", "Issue reading in the file   " + pContentSectionsFile + " " + e.toString() );
   throw new ResourceInitializationException();
 }
 

}// end Method loadContentSections() 

 // ---------------------------
 // Global Variables
 // ---------------------------
 private ProfilePerformanceMeter              performanceMeter = null;
 private HashSet<String> contentSectionNames = null;
 
} // end Class OrphanSectionRepairAnnotator() ---------------
