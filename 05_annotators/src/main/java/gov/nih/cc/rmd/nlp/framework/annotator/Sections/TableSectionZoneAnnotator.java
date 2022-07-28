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
 * TableSectionZoneAnnotator marks contignous lines with tabs in them
 * as a separate section, as a table section
 *   
 *
 * @author  Guy Divita 
 * @created Jan 08 2018
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.Sections;

import java.util.ArrayList;
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
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Line;



public class TableSectionZoneAnnotator extends JCasAnnotator_ImplBase {
 
 
  public static final String annotatorName = TableSectionZoneAnnotator.class.getSimpleName();
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
    
    List<Annotation> lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID );
    
    if ( lines != null && !lines.isEmpty()) {
      UIMAUtil.sortByOffset(lines);
      
      ArrayList<Annotation> sectionBufferOfLines = null;
      for (int i = 0; i < lines.size(); i++ ) {
        Annotation aLine = lines.get(i);
        
        String lineText = aLine.getCoveredText();
        
        if ( lineText == null || lineText.trim().length() == 0  ) {
          if ( sectionBufferOfLines != null && !sectionBufferOfLines.isEmpty() ) 
            createTableSectionZone(pJCas, sectionBufferOfLines );
          sectionBufferOfLines = null;
        } else if ( lineText.contains( "\t") ) {
          if ( sectionBufferOfLines == null )
            sectionBufferOfLines = new ArrayList<Annotation>();
          sectionBufferOfLines.add( aLine );
        } else if ( sectionBufferOfLines != null && !sectionBufferOfLines.isEmpty() ) {
          createTableSectionZone( pJCas, sectionBufferOfLines );
          sectionBufferOfLines = null;
        }
        
      }
    }
    
   
    
    this.performanceMeter.stopCounter();     
    
  } // End Method process() ------------------
  
 

// =================================================
  /**
   * createTableSectionZone 
   * 
   * @param pJCas
   * @param pLines
   *  
  */
  // =================================================
  private final void createTableSectionZone(JCas pJCas, List<Annotation> pLines ) {
  
  
 

    SectionZone statement = new SectionZone(pJCas);
    
    int offsetBegin = pLines.get(0).getBegin();
    int offsetEnd   = pLines.get(pLines.size()-1).getEnd();
    statement.setBegin( offsetBegin  );
    statement.setEnd( offsetEnd);
    statement.setSectionName("Table");
    // statement.setSectionTypes(v);
    statement.setId( "TableAnnotator_" + this.annotationCounter++);
    statement.addToIndexes();
   
  
 } // end Method createTableSectionZone()  -------
  
  
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

 private int annotationCounter = 0;
 private ProfilePerformanceMeter              performanceMeter = null;

} // end Class TableSectionZoneAnnotator() ---------------
