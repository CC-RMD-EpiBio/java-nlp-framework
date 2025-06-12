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
