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
