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
 * SectionRepairAnnotator finds content headings that don't have sections already made from them.
 * Sections are made from contiguous runs between section headings.
 *
 * Section repair cull's overlapping sections to favor the repaired one if it exists
 * @author Guy Divita
 * @created March 31, 2017
 *
 *          *
 *          *
 *          *
 *          *
 *          *
 *          *
 *          *
 *          *
 * 
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Delimiter;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.Section;
import gov.va.chir.model.SectionHeadingDelimiter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class SectionRepairAnnotator extends JCasAnnotator_ImplBase {

  @Override
  // -----------------------------------------
  /**
   * process finds slotValues in the whole document
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
      this.performanceMeter.startCounter();

      List<Annotation> headings = UIMAUtil.getAnnotations(pJCas, ContentHeading.typeIndexID, false);

      if (headings != null && !headings.isEmpty()) {

        for (int i = 0; i < headings.size() ; i++) {

          Annotation heading = headings.get(i);
          if ( heading.getBegin() >= heading.getEnd()) {
            System.err.println("bad header, where did it come from ? "  + ((ContentHeading)heading).getId());
          }
          String contentHeadingString = heading.getCoveredText();
          
          int nextHeadingBegin = -1;
          
          if ( i < headings.size() -1 )
             nextHeadingBegin = headings.get(i + 1).getBegin();
          else 
            nextHeadingBegin = pJCas.getDocumentText().length() ;
          
          if ( heading.getBegin() == nextHeadingBegin ) continue;

          // is there a section that is between the heading and the next heading?
          List<Annotation> sections = UIMAUtil.getAnnotationsBySpan(pJCas, Section.typeIndexID, heading.getEnd(),nextHeadingBegin);
          if (sections != null && !sections.isEmpty()) {
            Annotation aSection = sections.get(0);
            String sectionClassName = aSection.getClass().getName();
            if ( sectionClassName.contains("List")) {
              createASectionOutOfThis( pJCas, heading, nextHeadingBegin );
            } else {
              ;; // this is covered, do nothing
            }
          } else {

            // --------------------------------------
            // Check to see that there are no slot values in this area either
            List<Annotation> values = UIMAUtil.getAnnotationsBySpan(pJCas, DependentContent.typeIndexID, heading.getEnd(),
                nextHeadingBegin);
            if (values != null && !values.isEmpty()) {
             ;;
            } else {
              
              createASectionOutOfThis( pJCas, heading, nextHeadingBegin );
             

            } // end create a section

          } // if there are already sections
        } // end loop thru the headings
        
       
        
      } // end if there are headings
      
      // -----------------------------
      // cull overlapping sections
      cullSections(pJCas);

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
      // throw new AnalysisEngineProcessException();
    }

    this.performanceMeter.stopCounter();
   
  } // End Method process() =

 
  

  // =======================
  /**
   * createASectionOutOfThis 
   * @param pJCas 
   *
   * @param heading
   * @param nextHeadingBegin
   */
  // =======================
  private void createASectionOutOfThis(JCas pJCas, Annotation heading, int nextHeadingBegin) {
    // create a section that starts after the first content heading and ends before the next content heading.
    // Find the delimiter in the line that has the heading
    int sectionStart = heading.getEnd() + 1;
    List<Annotation> delimiters = UIMAUtil.getAnnotationsBySpan(pJCas, SectionHeadingDelimiter.typeIndexID, heading.getEnd(), nextHeadingBegin);
    if (delimiters != null && !delimiters.isEmpty()) {
      Annotation delimiter = delimiters.get(0);
      if ( delimiter.getEnd() + 1 < nextHeadingBegin -1 )
        sectionStart = delimiter.getEnd() + 1;
    }
    
    // ---------- why 2?  because I don't want to caputure the \n at the end either.
    if ( sectionStart < (nextHeadingBegin ))
      createSection(pJCas, sectionStart, (nextHeadingBegin ), (ContentHeading) heading);
    } // End Method createASectionOutOfThis =======
  

  // =======================
  /**
   * createSection 
   *
   * @param pJCas
   * @param pStart
   * @param pEnd
   * @param pHeading
   */
  // =======================
  private void createSection(JCas pJCas, int pStart, int pEnd, ContentHeading pHeading) {
   
      if ( pStart >= pEnd ) {
        System.err.println("Something wrong here " );
        return; //  throw new RuntimeException();
      }
      if (pHeading == null ) { System.err.println("no heading?"); throw new RuntimeException(); }
     String contentHeadingString = pHeading.getCoveredText();
     Section statement = new Section( pJCas);
     statement.setBegin( pStart);
     statement.setEnd( pEnd);
     statement.setId("SectionRepair_xx_" + this.sectionRepairCtr++);
     statement.setProcessMe(true);
     statement.setMarked(false);
     statement.setContentHeaderString( contentHeadingString);
     statement.setSectionHeading(pHeading);
     statement.setSectionName( contentHeadingString);
     statement.addToIndexes();
     
    
    
    } // End Method createSection =======
  
  // =======================
  /**
   * cullSections culls overlapping sections
   * favoring those created by repair over obsSecAn (the offsets are wonky on those)
   *
   * @param pJCas
   */
  // =======================
  private void cullSections(JCas pJCas) {
    
    List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, Section.typeIndexID );
    
    if ( sections != null && !sections.isEmpty()) {
      
      HashMap<String, SectionInfo> sectionNames = new HashMap<String, SectionInfo>();
      for ( Annotation section: sections ) {
        
        String sectionName = ((Section)section).getSectionName();
        if ( sectionName != null ) {

          // ------------------
          // The "title" section is an artifact of me putting metadata at the top of the output
          if ( sectionName.equals("Title")) {
            section.removeFromIndexes();
            continue;
          }
          SectionInfo similarSections = sectionNames.get( sectionName);
          if ( similarSections == null ) {
            SectionInfo sectionInfo = new SectionInfo( section);
            sectionNames.put(sectionName, sectionInfo);
          } else {
            similarSections.add( section);
          }
        } 
      }
      
    
    Set<String> keys = sectionNames.keySet();
    for ( String key : keys ) {
      SectionInfo similarSections = sectionNames.get(key);
      
      List<Annotation> duplicateSections = similarSections.getSections();
      if ( duplicateSections != null && duplicateSections.size() > 1 ) {
        // See if sections are overlapping
        Annotation lastSection = null;
        for ( Annotation aSection: duplicateSections ) {
          if ( lastSection != null ) 
            if ( isOverlapping( aSection, lastSection) ) {
              lastSection.removeFromIndexes();
            }
          lastSection = aSection;
          
          
        } // loop thru duplicate sections
      }
      
    }
    
    
    }
    
    
    
    } // End Method cullSections =======
  
  
  // =======================
  /**
   * isOverlapping does one section overlap the other
   *
   * @param aSection
   * @param bSection
   * @return
   */
  // =======================
  private boolean isOverlapping(Annotation aSection, Annotation bSection) {
    boolean returnVal = false;
    
    // -------aaaaaaaaaaaaaaaaaa----------------------------------
    // ---------bbbbbbbbbbbbbbbbbbb-------------------------------
    // ----bbbbbbbbbbbbbbbbb--------------------------------------
    // ----------bbbbbbbbbbb--------------------------------------
    
    if (( aSection.getBegin() <= bSection.getBegin() &&
        bSection.getBegin()<= aSection.getEnd() )   ||
       (  aSection.getBegin() > bSection.getBegin()  && 
           aSection.getBegin() <= bSection.getEnd() ) )
      returnVal = true;
    
    return returnVal;
    } // End Method isOverlapping =======
  




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

      initialize( args);
      
    } catch (Exception e) {
      System.err.println("Issue - initializing " + this.getClass().getName());
      throw new ResourceInitializationException();
    }
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
  private ProfilePerformanceMeter performanceMeter = null;
  private int sectionRepairCtr = 0;


} // end Class SlotValueRepairAnnotator ---------------------
