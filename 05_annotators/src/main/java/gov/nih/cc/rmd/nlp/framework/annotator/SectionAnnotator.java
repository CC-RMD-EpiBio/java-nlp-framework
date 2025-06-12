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
 * SectionAnnotator identifies sections within a medical document
 *
 *
 * @author  Guy Divita 
 * @created Mar 1, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Line;
import gov.va.chir.model.Section;
import gov.va.chir.model.SlotValue;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

public class SectionAnnotator extends JCasAnnotator_ImplBase {

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


    List<Annotation> sectionLines = new ArrayList<Annotation>();
    List<Annotation> lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID);
    List<Annotation> contentHeadings = null;
    List<Annotation> previousContentHeadings = null;
    SlotValue firstSlotValue = null;
    Annotation contentHeading = null;
    
    if ( lines != null ) {
    	for ( Annotation line: lines ) {
    		
    		// -------------------------------
    		// if a content heading is in this line, and a slot value is not,
    		//    end a previous section,
    		//    start a section
    		contentHeadings = UIMAUtil.getAnnotationsBySpan(pJCas, ContentHeading.typeIndexID, line.getBegin(), line.getEnd());
    		
    		if ( contentHeadings != null && contentHeadings.size() > 0  ) {
    			List<Annotation> slotValues = UIMAUtil.getAnnotationsBySpan(pJCas, SlotValue.typeIndexID, line.getBegin(), line.getEnd());
    			
    			if ( slotValues == null || slotValues.size() == 0 ) {
    				
    				// -----------------
    				// close out and create a section if sectionLines are not empty
    				if ( sectionLines != null && sectionLines.size() > 0  && contentHeading != null  ) { 
    					// System.err.println("The last line seen = " + line.getCoveredText());
    				  createSection(pJCas, contentHeading, sectionLines);
    				}
    				// ----------------
    				// create a new section lines list
    				sectionLines = new ArrayList<Annotation>();
    			
    				contentHeading = contentHeadings.get(0);
    			 // System.err.println(" contentHeading = " + contentHeading.getCoveredText());
    			} else { // end if there isn't a slot value in this line
    			
             firstSlotValue = (SlotValue) slotValues.get(0);
          }
    		} // end if there is a content heading hit	
    		
    		sectionLines.add(line);
    		
    	} // end loop thru the lines
    	
    	if ( sectionLines != null && sectionLines.size() > 0 && contentHeading != null ) 
    	  createSection( pJCas, contentHeading, sectionLines);
    } // end if there are any lines

    
    

  } // end Method process() ------------------

 



  // =======================================================
  /**
   * createSection 
   * 
   * @param pJCas
   * @param contentHeadings
   * @param sectionLines
   */
  // =======================================================
  private void createSection(JCas pJCas, Annotation contentHeading, List<Annotation> sectionLines) {
   
    
    int sectionBegin = sectionLines.get(0).getBegin();
    int sectionEnd   = sectionLines.get(sectionLines.size() -1 ).getEnd();
    
    Section section = new Section(pJCas);
    section.setBegin( sectionBegin);
    section.setEnd( sectionEnd);
    
    if ( contentHeading != null) {
      String sectionName = contentHeading.getCoveredText();
      section.setSectionHeading((ContentHeading) contentHeading);
      section.setContentHeaderString( sectionName);
    } else {
      throw new RuntimeException("Shouldnt be here");
    }
   
    section.setId("Section_" + this.annotationCounter++);
    section.setProcessMe(true);
    
  
    
    
    section.addToIndexes();
    
    
    
  } // End Method createSection() ======================
  


  // ----------------------------------
  /**
   * initialize loads in the resources needed for sectionTokenization.
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext)  throws ResourceInitializationException {

   
  
  } // end Method initialize() --------------


 
// ---------------------------------------
// Global Variables
// ---------------------------------------
 private int annotationCounter = 0; // new Section Counter.

} // end Class SectionTokenizerSimple() -----------
