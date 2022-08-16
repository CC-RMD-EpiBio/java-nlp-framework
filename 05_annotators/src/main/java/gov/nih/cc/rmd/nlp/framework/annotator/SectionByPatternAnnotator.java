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
