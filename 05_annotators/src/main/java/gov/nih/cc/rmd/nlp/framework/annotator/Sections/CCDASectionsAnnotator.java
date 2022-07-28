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
 * CCDA sections identifies the section(zones)
 * that we are interested in:
 * 
 *   Demographics (gender/age/address/email/phone/Ethnicity/Race)
 *   Chief Complaint/Active Problems
 *   Social History (Smoking Status/Tobacco Use/Alchohol Use/Canabnoid Use)
 *   Allergies
 *   Medications (active/current/Ordered/Expired)
 *   Family History
 *   Vitals
 *   Labs (Results)
 *   Assessment/Plan
 *   Instructions
 *   Diagnosis
 *   Providers  <---- useful for patient
 *   
 *   Riff off of the sectionizer that we've got - look for
 *   section headings for these 
 *   
 *     find one - zone til the next indication we are out of
 *     that section - 
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
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.PageFooter;
import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.model.SectionName;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Delimiter;
import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.Line;
import gov.va.chir.model.SlotValue;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class CCDASectionsAnnotator extends JCasAnnotator_ImplBase {
 
 
  public static final String annotatorName = CCDASectionsAnnotator.class.getSimpleName();

  
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
    
      findSections(pJCas);
      
    
      
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue in CCDA-Section Annotator " + e.toString());
    }
    
    this.performanceMeter.stopCounter();     
    
  } // End Method process() ------------------
  
  
  // -----------------------------------------
  /**
   * findSections finds contiguious sections based on
   * ccda section names 
   * 
   * 
   * @param pJcas
   * 
   * 
   */
  // -----------------------------------------
  public final void findSections(JCas pJCas) throws AnalysisEngineProcessException {

    StringBuffer sectionBuff = null;

    Annotation previousCCDAContentHeading = null;
    List<Annotation> lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID);
    int sectionBegin = 0;
    int sectionEnd = 0;

    String sectionName= null;
    if (lines != null && !lines.isEmpty()) {
      for (int i = 0; i < lines.size(); i++) {
        Annotation line = lines.get(i);
        String lineBuff = line.getCoveredText();
        Annotation ccdaContentHeading = containsCCDASectionName(pJCas, line);
       
        if ( ccdaContentHeading != null )
           sectionName = ccdaContentHeading.getCoveredText();
            
      
        if (ccdaContentHeading != null  ) {
          if (!isEmpty(sectionBuff))
            createSection(pJCas, sectionBuff, sectionBegin, sectionEnd, previousCCDAContentHeading);
          sectionBuff = new StringBuffer();
          sectionBegin = line.getBegin();
         
          previousCCDAContentHeading = ccdaContentHeading;
        }

        if (sectionBuff == null)
          sectionBuff = new StringBuffer();
        sectionBuff.append(lineBuff + "\n");
        sectionEnd = line.getEnd();
      } // end loop thru lines

      // take care of the last open section or nestedSection
      if (sectionBuff != null && !isEmpty(sectionBuff))
        createSection(pJCas, sectionBuff, sectionBegin, sectionEnd, previousCCDAContentHeading);

    } // end if there are lines

  } // End Method process() -----------------
  
 // =================================================
  /**
   * isPartOfPageFooter returns true if this line is
   * part of a page footer
   * 
   * @param pJCas
   * @param line
   * @return
  */
  // =================================================
  private final boolean isPartOfPageFooter(JCas pJCas, Annotation line) {
    boolean returnVal = false;
    
    List< Annotation> pageFooter = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  PageFooter.typeIndexID, line.getBegin(), line.getEnd() );
   
    if ( pageFooter != null && !pageFooter.isEmpty())
      returnVal = true;
    
    return returnVal;
  } // end Method isPartOfPageFooter() --------------


  // =================================================
  /**
   * containsCCDASectionName returns an existing contentHeading
   * if it exists.  If it's not part of a slot value
   * 
   * @param pJCas
   * @param pLine
   * @return Annotation
  */
  // =================================================
  private final Annotation containsCCDASectionName(JCas pJCas, Annotation pLine) {
    
    Annotation returnVal = null;
    
    List<Annotation> contentHeadings = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  ContentHeading.typeIndexID,  pLine.getBegin(), pLine.getEnd());
    
    if ( contentHeadings != null && !contentHeadings.isEmpty()) {
      // there should only be one
      
      if ( !isInSlotValue( pJCas, contentHeadings.get(0)) )
        returnVal = contentHeadings.get(0);
    }
    
    return  returnVal;
  } // end Method containsCCDASectionName() ---------


// =================================================
  /**
   * isInSlotValue checks to see if this content heading
   * is the content heading from a slot value
   * 
   * @param annotation
   * @return boolean
  */
  // =================================================
  private final boolean isInSlotValue(JCas pJCas, Annotation pContentHeading) {
    boolean returnVal = false;
    
    List<Annotation> slotValue = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, SlotValue.typeIndexID, pContentHeading.getBegin(), pContentHeading.getEnd());
    
    if ( slotValue != null && !slotValue.isEmpty())
      returnVal = true;
    
    return returnVal;
  } // end Method isInSlotValue() ------------


//-----------------------------------------
 /**
  * createSectionZone
  * 
  * @param pJCas
  * @param pText
  * @param pSectionBegin
  * @param pSectionEnd
  * @param pContentHeading
  * @return SectionZone
  */
 // -----------------------------------------
 private SectionZone createSection( JCas              pJCas, 
                                    StringBuffer      pText,
                                    int               pSectionBegin,
                                    int               pSectionEnd,
                                    Annotation        pContentHeading) {
   
   
    String sectionTypes = "CCDA_Section";
    String kindsOfAnnotations = "none";
    SectionZone statement = new SectionZone(pJCas);
    statement.setBegin(  pSectionBegin);
    statement.setEnd(    pSectionEnd  );
    statement.setId( "CCDASectionAnnotator_" + this.annotationCounter++);
    if ( pContentHeading != null ) {
      String sectionName = pContentHeading.getCoveredText();
      statement.setSectionName(   sectionName );
      StringArray otherFeaturez = ((ContentHeading) pContentHeading).getOtherFeatures();
   
      if (otherFeaturez != null && otherFeaturez.size(    ) > 1 ) {
        sectionTypes = otherFeaturez.get(0);
        kindsOfAnnotations = otherFeaturez.get(1);
      }
    } else {
      // This document is actually a section passed in as a document
      // get the section name from the document header, it's been passed in 
      // at the api level and placed there.
      DocumentHeader documentHeader = VUIMAUtil.getDocumentHeader(pJCas);
      if ( documentHeader != null)  {
        String sectionName = documentHeader.getSectionType();
        if ( sectionName != null )
          statement.setSectionName( sectionName );
      }
    }
    if ( pContentHeading != null ) {
      ((ContentHeading)pContentHeading).setParent( statement);
      createSectionName(pJCas, pContentHeading);
    }
    
   
    statement.setAnnotationTypes( kindsOfAnnotations);
    statement.setSectionTypes( sectionTypes);
    statement.addToIndexes();
    
   
 
   
   return statement;
 } // end Method createSection()  -------
  
  
 // =================================================
/**
 * createSectionName creates a SectionName annotation
 * from the contentHeading.  Removes the ContentHeading
 * if the content heading is not from a slot value structure
 * 
 * @param pJCas
 * @param pContentHeading
*/
// =================================================
private final void createSectionName(JCas pJCas, Annotation pContentHeading) {
  
  
  SectionName statement = new SectionName( pJCas);
  
  statement.setBegin( pContentHeading.getBegin());
  
  int endOffset = pContentHeading.getEnd();
  if ( this.keepEndPunctuation )
    endOffset = findNextPunctuationDelimiter( pJCas, pContentHeading );
  
  statement.setEnd(  endOffset);
  statement.setId(annotatorName + "_SectionName_" + this.annotationCounter++ );
  
  statement.setConditional(((ContentHeading)pContentHeading).getConditional() );
  
  statement.setEventDate(((ContentHeading)pContentHeading).getEventDate() );
  statement.setMarked(((ContentHeading)pContentHeading).getMarked() ) ;
 
  
  
  statement.setNegation_Status( ((ContentHeading)pContentHeading).getNegation_Status() );
  statement.setOtherFeatures(((ContentHeading)pContentHeading).getOtherFeatures() );
  statement.setParent(((ContentHeading)pContentHeading).getParent() );
  statement.setProcessMe(((ContentHeading)pContentHeading).getProcessMe() );
  statement.setReferenceDate(((ContentHeading)pContentHeading).getReferenceDate() );
  statement.setSection(((ContentHeading)pContentHeading).getSection() );
  statement.setSectionName(((ContentHeading)pContentHeading).getSectionName() );
  statement.setStatementDate(((ContentHeading)pContentHeading).getStatementDate() );
  statement.addToIndexes();
  
  pContentHeading.removeFromIndexes();
  
}


// =================================================
/**
 * findNextPunctuationDelimiter returns the offset after
 * the next delimiter from the contentHeading if there is one
 * Otherwise, it returns the ending offset of the content heading
 * 
 * @param pJCas
 * @param pContentHeading
 * @return int
*/
// =================================================
private int findNextPunctuationDelimiter(JCas pJCas, Annotation pContentHeading) {
  int returnVal = pContentHeading.getEnd();
  
  List<Annotation> delimiters = UIMAUtil.getAnnotationsBySpan(pJCas, Delimiter.typeIndexID, pContentHeading.getEnd() , pContentHeading.getEnd() + 5, true );
  if ( delimiters != null && !delimiters.isEmpty())
    returnVal = delimiters.get(0).getEnd();
  
  return returnVal;
}


// =================================================
/**
 * isEmpty checks if this string buffer is null or 
 * is empty or is only white space.
 * 
 * @param pBuff
 * @return boolean
*/
// =================================================
private final boolean isEmpty(StringBuffer pBuff) {
  
  boolean returnVal = true;
  
  if ( pBuff!= null ) 
    returnVal = isBlankLine( pBuff.toString() );
    
  
  return returnVal;
} // end Method isEmpty() -------------------------



//=================================================
/**
* isBlankLine returns true if this has no chars, or
* if all the chars are whitespace
* 
* @param pBuff
* @return boolean
*/
//=================================================
private final boolean isBlankLine(String pBuff) {
boolean returnVal = true;

if ( pBuff!= null ) {
 if (!pBuff.isEmpty() ) {
   if ( pBuff.trim().length() > 0 )
     returnVal = false;
 }
}
return returnVal;
} // end Method isBlankLine() ---------------------

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
    
    this.keepEndPunctuation =Boolean.parseBoolean(U.getOption(pArgs,"--keepSentenceEndingPunctuation=", "true"));
    
   
    
  } catch (Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, "Issue initializing the " + annotatorName + " " + e.toString() );
    throw new ResourceInitializationException();
  }
  

  
} // end Method initialize()






 // ---------------------------
 // Global Variables
 // ---------------------------

 private int annotationCounter = 0;
 private ProfilePerformanceMeter              performanceMeter = null;
 private boolean keepEndPunctuation = false;

} // end Class ExampleAnnotator() ---------------
