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
 * SectionPattern annotator looks for patterns that are of the form 
 *   SCREEN FOR xxxx
 *   XXX Screening
 *   TEST FOR
 *   XXXX Survey
 *   
 * @author  Guy Divita 
 * @created August 25, 2014
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Line;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class SectionByPatternAnnotator extends JCasAnnotator_ImplBase {
  
  @Override
  // -----------------------------------------
  /**
   * process finds check boxes from lines of documents
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
    String[] leftSidePatterns = { "screen for", "screen", "survey" };
    String[] rightSidePatterns = { "screen", "screening", "test" , "assessment", "history", "hist", "heent", "exam", "plan", "***", "care", "scale"};
    
    // Loop thru lines
    List<Annotation> lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID);
    
    if ( lines != null ) {
    
      for ( Annotation line : lines ) {
        
        if ( line != null  ) {
         
       
          
          String lineString = line.getCoveredText();
          String row = lineString.trim();
          
          int offset = line.getBegin();
          if ( row != null && row.length() > 0 ) {
            
            int delimeter = lineString.indexOf(':');
            if ( delimeter < 1) delimeter = lineString.length();
            
            // -------------------------
            // leftside  patterns
            
            for ( String pattern : leftSidePatterns ) {
              if ( row.toLowerCase().startsWith(pattern) ) {
                createContentHeading(pJCas,   offset, delimeter + offset );
              }
            } // end loop thru left pattern
            
            // --------------------------
            // right side patterns  xxxx survey:
            
            for ( String pattern: rightSidePatterns) {
              String delimitedRow = lineString.substring(0, delimeter ).toLowerCase();
              if ( delimitedRow.endsWith(pattern)) {
                createContentHeading( pJCas,  offset, delimeter + offset );
              }
              
            } //end loop thru right side patterns
            
            
            
            
          } //  end if row is not null
        } // end if there is text in the line
      } // end loop thru line 
    } // end if the line annotation is not empty    
          
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
    //  throw new AnalysisEngineProcessException();
    }  
    
  } // end Method process
      
      
      
  // =======================================================
  /**
   * createContentHeading creates a contentHeading
   * 
   * @param pJCas
   * @param contentHeaderBegin
   * @param contentHeaderEnd
   * @return ContentHeading
   */
  // =======================================================
  private ContentHeading createContentHeading(JCas pJCas, int contentHeaderBegin, int contentHeaderEnd) {
    
    ContentHeading contentHeading = new ContentHeading( pJCas);
    contentHeading.setBegin(contentHeaderBegin);
    contentHeading.setEnd( contentHeaderEnd);
    contentHeading.addToIndexes();
    
    return contentHeading;
  } // End Method createContentHeading() ======================
  





  //----------------------------------
  /**
   * initialize loads in the resources needed for slotValues. Currently, this involves
   * a list of known slot names that might show up.
   *
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
    initialize();
  } // end Method initialize() ---------

  //----------------------------------
    /**
     * initialize loads in the resources needed for slotValues. Currently, this involves
     * a list of known slot names that might show up.
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize() throws ResourceInitializationException {
      
    
  
  } // end Method initialize() --------------
  

 
  // -----------------------------------------
  // class Variables
  // -----------------------------------------

//---------------------------------------
//Global Variables
//---------------------------------------

  
} // end Class SlotValue ---------------------
