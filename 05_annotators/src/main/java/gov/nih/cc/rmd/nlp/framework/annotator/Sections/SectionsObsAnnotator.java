// =================================================
/**
 *  sections identifies the section(zones)
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

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.NestedSection;
import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.nih.cc.rmd.nlp.lexUtils.U;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Delimiter;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.Line;
import gov.va.chir.model.SlotValue;




public class SectionsObsAnnotator extends JCasAnnotator_ImplBase {
 
 
  public static final String annotatorName = SectionsObsAnnotator.class.getSimpleName();

  
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
    
      findSectionsAndSubSections(pJCas);
      
      // Iterate thru subsections to find slot values
      findSlotValues(pJCas);
      
    
      // Set the sectionNames
      findSectionNames(pJCas);
      
      // put the nested sections together within each section
      organizeSections( pJCas);
      
      
      
      // set contentHeaders/sectionNames as a separate annotation
      createSectionHeadings( pJCas);
      // Alter Section offset to not  include section name
      
      
      
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue in CiitizenSection Annotator " + e.toString());
    }
    
    this.performanceMeter.stopCounter();     
    
  } // End Method process() ------------------
  
  // =================================================
  /**
   * createSectionHeadings creates separate annotations
   * that are the section headings
   * 
   * @param pJCas
   * @throws Exception 
  */
  // =================================================
  private final void createSectionHeadings(JCas pJCas) throws Exception  {
    
    try {
  List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, SectionZone.typeIndexID, false);
      
      if ( sections != null && !sections.isEmpty()) 
        for ( Annotation section: sections )
          createSectionHeading(pJCas, section);
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue in createSectionHeadings " + e.toString());
      throw e;
    }
    
  } // end Method createSectionHeadings() ------------

  // =================================================
  /**
   * createSectionHeading creates a section heading
   *  (ContentHeading) if one does not already exist
   * 
   * @param pJCas
   * @param pSection
  */
  // =================================================
    private  final void createSectionHeading(JCas pJCas, Annotation pSection) {
    
      try {
        ContentHeading aContentHeading = null;
        List<Annotation>contentHeadings = UIMAUtil.getAnnotationsBySpan(pJCas, ContentHeading.typeIndexID, pSection.getBegin(), pSection.getEnd());
        
        
        if ( contentHeadings != null && !contentHeadings.isEmpty()  && 
            contentHeadings.get(0).getBegin() >= pSection.getBegin() &&
            contentHeadings.get(0).getEnd() <= pSection.getEnd() ) {
          
          aContentHeading = (ContentHeading) contentHeadings.get(0);
          aContentHeading.setId(aContentHeading.getId() + "_Ciitizen_SectionHeading");
          if ( aContentHeading.getParent() == null ||
              !aContentHeading.getParent().equals( pSection ) ) {
              aContentHeading.setParent(  pSection );
              String sectionName = aContentHeading.getSectionName();
              if ( sectionName == null) {
                sectionName = aContentHeading.getCoveredText().trim();
              }
              ((SectionZone)pSection).setSectionName( normalize(sectionName));
             
          }
            
        } else {
        
         String sectionHeadingString = ((SectionZone) pSection).getSectionName() ;
         
         if ( sectionHeadingString != null ) {
         
           aContentHeading = new ContentHeading( pJCas);
           aContentHeading.setBegin(pSection.getBegin());
           aContentHeading.setEnd(pSection.getBegin() + sectionHeadingString.length());
           aContentHeading.setSectionName( normalize(sectionHeadingString));
           aContentHeading.setId( "CiitizenSectionsAnnotator_createSectionHeading_" + this.annotationCounter);
           aContentHeading.setParent( pSection );
           aContentHeading.addToIndexes();
           ((SectionZone)pSection).setSectionName( normalize(sectionHeadingString));
         }
         else {
           GLog.println(GLog.TRACE_LEVEL,"try harder to label this section with a section Name  " );
           tryHarderToLabelSection(pJCas, (SectionZone) pSection );
         
           
         }
        }
        
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL,"Issue in createSectionHeading " + e.toString());
        throw e;
      }  
  } // end Method createSectionHeading() -------------

  // =================================================
  /**
   * tryHarderToLabelSection [TBD] summary
   * 
   * @param pSection
  */
  // =================================================
  private void tryHarderToLabelSection(JCas pJCas, SectionZone pSection) {
    
    String coveredText = pSection.getCoveredText();
    String firstLine = getFirstLine( coveredText);
    String sectionName = "Unlabeled Section";
    // if the first line has all caps, or Initial caps or has a colon in it
    if ( U.allUpperCase( firstLine ) || 
         U.isCapsCamelCase(  firstLine ) ||
         firstLine.contains(":") ){
      sectionName = firstLine;
      
      createContentHeading( pJCas, pSection, firstLine);
      
    }
   
    ((SectionZone)pSection).setSectionName( normalize(sectionName) );
  }  // end Method tryHarderToLabelSection() ----------
  

  // =================================================
  /**
   * createContentHeading 
   * 
   * @param pJCas
   * @param pSection
   * @param firstLine
  */
  // =================================================
  private void createContentHeading(JCas pJCas, SectionZone pSection, String pFirstLine) {
   
    ContentHeading statement = new ContentHeading( pJCas);
    statement.setBegin( pSection.getBegin());
    statement.setEnd( pSection.getBegin() + pFirstLine.length() + 1);
    statement.setId( "CiitizenSectionsAnnotator_TryHarder_" + this.annotationCounter++);
    statement.addToIndexes();
    
  } // end Method createContentHeading() 
  

  // =================================================
  /**
   * getFirstLine retrieves the first line of the text,
   * devoid of the \n
   * 
   * @param coveredText
   * @return String
  */
  // =================================================
  private final String getFirstLine(String pText) {
    
    String returnVal = pText;
    String rows[] = U.split(pText, "\n");
    if ( rows != null && rows.length > 0 )
      returnVal = rows[0];
    return returnVal;
    
  }  // end Method getFirstLine() -------------------- 
  
  

  // =================================================
  /**
   * normalize strips leading and trailing punctuation from the section name ,
   * 
   * 
   * @param pName
   * @return String
  */
  // =================================================
  private final String normalize(String pName) {
    
   String returnVal = U.stripLeadingAndTrailingPunctuation( pName);
    
    return returnVal;
  }  // end Method getFirstLine() -------------------- 

  // =================================================
  /**
   * organizeSections creates an array of nested sections
   * of all sub-sections of the top level sectionZones
   * 
   * @param pJCas
   * @throws Exception
  */
  // =================================================
  private final void organizeSections(JCas pJCas) throws Exception  {
    
    try {
      
      List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, SectionZone.typeIndexID, false);
      
      if ( sections != null && !sections.isEmpty()) 
        for ( Annotation section: sections ) 
          organizeSection(pJCas, section);
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, "issue with organizing the sections " + e.toString());
      throw e;
    }
    
  }  // end Method organizeSections() ----------------

  // =================================================
  /**
   * organizeSection organizes and flattens out subsections
   * of the section
   * 
   * @param pJCas
   * @param pSection
  */
  // =================================================
  private void organizeSection(JCas pJCas, Annotation pSection) throws Exception {
    
    try {
      FSArray nestedSectionz = ((SectionZone) pSection).getNestedSection();
      if ( nestedSectionz != null && nestedSectionz.size() > 0 ) {
     
        List<Annotation>flattenedSections = new ArrayList<Annotation>();
        for  (int i = 0; i < nestedSectionz.size(); i++ ) {
          Annotation aNestedSection = (Annotation) nestedSectionz.get(i);
          if (  aNestedSection != null ) {
            if ( aNestedSection.getClass().getName().contains("Section")) {
              FSArray moreNestedSectionz = ((SectionZone)aNestedSection).getNestedSection() ;
              if ( moreNestedSectionz!= null && moreNestedSectionz.size() > 0 ) {
                for  (int k = 0; k < moreNestedSectionz.size(); k++ ) {
                  Annotation moreNestedSection = (Annotation) moreNestedSectionz.get(k);
                  flattenedSections.add( moreNestedSection );
                }
              }  else {
                flattenedSections.add( aNestedSection );
              }
            } else {
              flattenedSections.add( aNestedSection );
            }
          } // end if there is a sub-sub section(s)
        } // end loop thru all the subsections
      
      
        if ( flattenedSections != null && !flattenedSections.isEmpty()) {
          nestedSectionz.removeFromIndexes();  //<---- not sure if I need to do this
          nestedSectionz = new FSArray(pJCas, flattenedSections.size());
          for ( int i = 0; i < flattenedSections.size(); i++)
            nestedSectionz.set(i, flattenedSections.get(i));
          ((SectionZone) pSection).setNestedSection( nestedSectionz);
          
        }
    
      } // end if there are subsections
      
        
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, "issue with organizing the section " + e.toString());
      throw e;
    }
    
    
  } // end Method organizeSection() ------------------

  // =================================================
  /**
   * findSectionNames iterates thru the sections
   *   for each section, it makes the section name
   *   the part that is at the beginning that is
   *   not covered by the first nested section
   * 
   * @param pJCas
  */
  // =================================================
  private final void findSectionNames(JCas pJCas) {
    
    List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, SectionZone.typeIndexID, false);
    
    if ( sections != null && !sections.isEmpty()) {
      for ( Annotation section: sections )
        findSectionName(pJCas, section );
    }
    
  } // end Method findSectionNames() -----------------
  
  // =================================================
  /**
   * findSectionName makes the section name
   *   the part that is at the beginning that is
   *   not covered by the first nested section
   * 
   * @param pJCas
   * @param pSection
  */
  // =================================================
  private final void findSectionName(JCas pJCas, Annotation pSection) {
    
  
    String sectionName = null;
    String docText = pJCas.getDocumentText();
    if ( pSection != null ) {
      FSArray nestedSectionz = ((SectionZone)pSection).getNestedSection() ;
      if ( nestedSectionz != null ) {
        Annotation firstSection = (Annotation) nestedSectionz.get(0);
        if ( firstSection.getBegin() > pSection.getBegin() ) {
          sectionName = docText.substring(pSection.getBegin(), firstSection.getBegin()-1);
        }
        
      }
      ((SectionZone) pSection).setSectionName( sectionName);
    }
    
    
  } // end Method findSectionNames() -----------------
  

  // =================================================
  /**
   * findSlotValues iterates thru subSections
   * 
   * @param pJCas
  */
  // =================================================
  private final void findSlotValues(JCas pJCas) {
  
    try {
    
      List<Annotation> subSections = UIMAUtil.getAnnotations(pJCas, NestedSection.typeIndexID, false );
      
      
      if ( subSections != null && !subSections.isEmpty() ) {
        
        for ( Annotation subSection : subSections ) {
          
          List<Annotation> lines = UIMAUtil.getAnnotationsBySpan( pJCas, Line.type, subSection.getBegin(), subSection.getEnd() );
          
          if (lines != null && !lines.isEmpty() ) {
            
            ArrayList<Annotation> nestedSections = new ArrayList<Annotation>();
            for ( Annotation line: lines ) {
              String buff = line.getCoveredText();
              int delimiter = looksLikeSlotValue( buff) ;
              if ( delimiter > -1 ) { //<---------------------  looks like a slotValue
                Annotation aSlotValue = getExistingSlotValue(  pJCas, line ) ;
                
                if ( aSlotValue == null ) 
                  aSlotValue = createSlotValue(pJCas, buff, line.getBegin(), line.getEnd(), delimiter, subSection );
                if ( aSlotValue != null ) {
                  ((SlotValue) aSlotValue).setParent(subSection);
                  nestedSections.add( aSlotValue );
                }
              }
            }
            
            if ( nestedSections != null && !nestedSections.isEmpty()) {
              FSArray nestedSectionz = new FSArray(pJCas, nestedSections.size());
              for ( int i = 0; i < nestedSections.size();  i++) 
                nestedSectionz.set(i,  nestedSections.get(i));
            
              ((NestedSection)subSection).setNestedSection(nestedSectionz);
            }
            
          }
          
        }
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue in CiitizenSection Annotator finding slot values " + e.toString());
      throw e;
    }
    } // end Method findSlotValues

  // =================================================
  /**
   * getExistingSlotValue finds an already made SlotValue
   * if it exists
   * 
   * @param pJCas
   * @param pLine
   * @return Annotation (null if not found)
  */
  // =================================================
  private final Annotation getExistingSlotValue(JCas pJCas, Annotation pLine) {
    Annotation returnVal = null;
    
    List<Annotation> slotValues = UIMAUtil.getAnnotationsBySpan(pJCas, SlotValue.typeIndexID, pLine.getBegin(), pLine.getEnd() );
    
    if ( slotValues != null && !slotValues.isEmpty()) 
       returnVal = slotValues.get(0);
    
    return returnVal;
  } // end Method getExistingSlotValue() -----

  // -----------------------------------------
  /**
   * findSectionsAndSubSections
   * 
   * @param pJcas
   * 
   * 
   */
  // -----------------------------------------
  public final void findSectionsAndSubSections(JCas pJCas) throws AnalysisEngineProcessException {
   
    
    
    try {
    
      int currentIndentation = 0;
      int lastIndentation = 0;
      int sectionIndentation = 0;
      int nestedSectionIndentation = 0;
      boolean firstLine = true;
      boolean sectionBreak = false;
      boolean nestedSectionBreak = false;
      StringBuffer sectionBuff = null;
      StringBuffer nestedSectionBuff = null;
      int sectionBegin = 0;
      int sectionEnd = 0;
      int nestedSectionBegin = 0;
      int nestedSectionEnd = 0;
      ArrayList<NestedSection> nestedSections = null;
      int sectionCounter = 0; 
      boolean isCapitalized = false;
      Annotation nextLine = null;
      boolean nextLineLooksLikeAContentHeading = false;
      boolean partOfExistingSectionZone = false;
      
      
      List<Annotation> lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID );
        
      if ( lines != null && !lines.isEmpty()) {
        for ( int i = 0; i < lines.size(); i++ ) {
          Annotation line = lines.get(i);
          String  lineBuff = line.getCoveredText();
          
          SectionZone existingSectionZone = getExistingSectionZone( pJCas, line );
          if ( existingSectionZone != null   ) {
            if ( !isEmpty(sectionBuff)  )                     {        
              createSection(pJCas,  sectionBuff, nestedSections,  sectionBegin, sectionIndentation);  
              sectionCounter++; 
              sectionBuff = new StringBuffer();
              sectionIndentation = existingSectionZone.getIndentation();  
              sectionBegin = existingSectionZone.getEnd() + 1;
            }
          } else {
            
          
          if ( i < lines.size() -1 )
            nextLine = lines.get(i+1);
           
          isCapitalized = doesNextLineLookLikeAContentHeading(pJCas, line );
          currentIndentation = ((Line) line).getIndentation();
          nestedSectionBreak = isNestedSectionBreak( lineBuff, currentIndentation, lastIndentation );  // blank line or hr
          nextLineLooksLikeAContentHeading = doesNextLineLookLikeAContentHeading(pJCas, nextLine);
          sectionBreak = isSectionBreak(firstLine, nestedSectionBreak, currentIndentation, nestedSectionIndentation, sectionIndentation, lastIndentation, isCapitalized , nextLineLooksLikeAContentHeading);
        
          if ( currentIndentation > lastIndentation ) nestedSectionIndentation = currentIndentation;
          
          boolean aBreak = false;
          
          if (  sectionBreak && !isEmpty(sectionBuff)  )                     {        
            createSection(pJCas,       sectionBuff, nestedSections,    sectionBegin,              sectionIndentation); aBreak = true; sectionCounter++; }
          else if ( currentIndentation != -1 && currentIndentation < sectionIndentation && !isEmpty(sectionBuff)  )                     {        
            createSection(pJCas,       sectionBuff, nestedSections,    sectionBegin,              sectionIndentation); aBreak = true; sectionCounter++;  }
        //  else if ( !firstLine && currentIndentation == 0 && !isEmpty(sectionBuff) ) {
         //   createSection(pJCas,       sectionBuff, nestedSections,    sectionBegin,              sectionIndentation); aBreak = true; sectionCounter++; }
    
        
          if (  nestedSectionBreak && !isEmpty(nestedSectionBuff) )                { 
            if ( nestedSections == null ) nestedSections = new ArrayList<NestedSection>();
            nestedSections.add( createNestedSection(pJCas, nestedSectionBuff.toString(),              nestedSectionBegin,  nestedSectionIndentation)); aBreak = true; }
          else if ( currentIndentation <  nestedSectionIndentation && !isEmpty(nestedSectionBuff) )              { 
            if ( nestedSections == null ) nestedSections = new ArrayList<NestedSection>();
            nestedSections.add(createNestedSection(pJCas, nestedSectionBuff.toString(),              nestedSectionBegin,  nestedSectionIndentation)); aBreak = true; }
         
          
          if ( sectionBreak ) {
            
            sectionBuff = new StringBuffer();
            sectionIndentation = currentIndentation;
            sectionBegin = line.getBegin();
           
          } // else {
          
            if ( sectionBuff == null ) {
             sectionBuff = new StringBuffer();
             sectionIndentation = currentIndentation;
              sectionBegin = line.getBegin();
            }
            sectionBuff.append( lineBuff + "\n");
            
          //}
          if (aBreak  ) {
            nestedSectionBuff = null;
            if ( currentIndentation < nestedSectionIndentation && currentIndentation > 0 ) {
              nestedSectionBuff = new StringBuffer();
              nestedSectionBegin = line.getBegin();
              nestedSectionBuff.append( lineBuff + "\n");
            }
          } else {
            if ( (currentIndentation == nestedSectionIndentation && currentIndentation != 0 )||  currentIndentation > sectionIndentation ) {
              if (nestedSectionBuff == null ) {
                nestedSectionBuff = new StringBuffer();
                nestedSectionBegin = line.getBegin();
              }
              
             nestedSectionBuff.append( lineBuff + "\n");
            }
          }
          
            
           
        
          lastIndentation =currentIndentation;
          firstLine = false;
        
          } // end if not part of an existing meeting
          
        } // end loop thru lines
    
        // take care of the last open section or nestedSection 
        if ( nestedSectionBuff != null && !isEmpty(nestedSectionBuff))         createNestedSection(pJCas, nestedSectionBuff.toString(),               nestedSectionBegin,  nestedSectionIndentation);
        if (       sectionBuff != null && !isEmpty(     sectionBuff ))               createSection(pJCas,       sectionBuff,  nestedSections,    sectionBegin,  sectionIndentation);
      
      } // end if there are lines
    
   
     
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue in CiitizenSection Annotator " + e.toString());
      throw  e;
    }
   
  } // End Method process() -----------------
    

  
 // =================================================
  /**
   * getExistingSectionZone returns an existing section zone
   * if it exists.  One might exist if a prior annotator
   * created it like a page header, or table annotator
   * 
   * @param pJCas
   * @param pLine
   * @return SectionZone
  */
  // =================================================
  private final SectionZone getExistingSectionZone(JCas pJCas, Annotation pLine) {
    
    SectionZone returnVal = null;
    
    List<Annotation> sectionZones = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  SectionZone.typeIndexID,  pLine.getBegin(), pLine.getEnd());
    
    if ( sectionZones != null && !sectionZones.isEmpty()) {
      // there should only be one
      returnVal = (SectionZone )sectionZones.get(0);
    }
    
    return  returnVal;
  } // end Method getExistingSectionZone() ---------

// =================================================
  /**
   * doesNextLineLookLikeAContentHeading returns true if
   *   this line (the next line) is a contentHeading, or if
   *   it is short (<60 chars), and [all caps or camel case initial cap], 
   *   and has no trailing period (indication of a sentence)
   *   
   *   or is a blank line itself (two blank lines make a section break)
   * 
   * @param pJCas
   * @param nextLine
   * @return boolean
  */
  // =================================================
  private final boolean doesNextLineLookLikeAContentHeading(JCas pJCas, Annotation pLine) {
    boolean returnVal = false;
    
    if ( pLine == null )
      return false;
    
    try {
      String buff = pLine.getCoveredText();
      
      if ( buff.trim().length() == 0 )
        return false;
      
      List<Annotation> contentHeadings = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  ContentHeading.typeIndexID,  pLine.getBegin(), pLine.getEnd());
      if ( contentHeadings != null && !contentHeadings.isEmpty())
        return true;
      
      else
        returnVal = doesNextLineLookLikeAContentHeading(  buff);
      
      
    } catch ( Exception e) {
       
    }
    
    
    return returnVal;
  } // end Method doesNextLineLookLikeAContentHeading() ---
  
//=================================================
 /**
  * doesNextLineLookLikeAContentHeading returns true if
  *   this line (the next line) is a contentHeading, or if
  *   it is short (<60 chars), and [all caps or camel case initial cap], 
  *   and has no trailing period (indication of a sentence)
  *   
  *   or is a blank line itself (two blank lines make a section break)
  * 
  * @param pJCas
  * @param nextLine
  * @return boolean
 */
 // =================================================
  private final boolean doesNextLineLookLikeAContentHeading(String pLine) {
    
    boolean returnVal = false;
    
    if ( pLine != null && pLine.trim().length() > 0  ) {
      if ( pLine.trim().endsWith(".") ) 
          returnVal = false;
      else if (pLine.trim().length() > 59 )
        returnVal = false;
      else if ( U.allUpperCase( pLine ))
        returnVal = true;
      else if ( U.isCapsCamelCase(pLine))
        returnVal = true;
      
    } else
      returnVal = true;
    
    
    return returnVal;
    
    
  
  } // end Method doesNextLineLookLikeAContentHeading() 
 
  
// =================================================
  /**
   * looksLikeSlotValue is an ad-hoc way to recognize 
   *               a slot value -
   *               begins with a Capitalized token
   *               contains a delimiter
   *               contains a number of white spaces between heading and value
   * @param line
   * @return int   -1 if not , the location of the delimiter if true
  */
  // =================================================
  private final int  looksLikeSlotValue(String  pLine) {
   
    int returnVal = -1;
    
    if ( pLine == null || pLine.isEmpty() || pLine.trim().length() == 0 ) return -1;
    char firstChar = pLine.trim().charAt(0);
    
    if ( Character.isLetter(firstChar ) && ( !Character.isLowerCase(firstChar ))) {
      
      returnVal = pLine.indexOf(':') ;
      if ( returnVal <= 0)
        returnVal = -1;
        
     
    }
    
    return returnVal;
  } // end Method looksLikeSlotValue() -------------


// =================================================
  /**
   * processObs
   * 
   * @param pJCas
  */
  // =================================================
  private void processObs(JCas pJCas) {
    
    // Iterate through the content headings
    // start new sections from one to the next
    // ignore content headings that are also the beginning of slot values
    // ignore content headings that are questions
     
    List<Annotation> contentHeadings = UIMAUtil.getAnnotations(pJCas, ContentHeading.typeIndexID , false);  
    
    UIMAUtil.uniqueAnnotations(contentHeadings);
    UIMAUtil.sortByOffset(contentHeadings); 
    
    if ( contentHeadings != null && !contentHeadings.isEmpty() ) {
      
      Annotation lastHeading = null;
      for ( int i = 0; i < contentHeadings.size(); i++ ) {
      
        Annotation heading = contentHeadings.get(i);
      
        if ( lastHeading != null )
          createSectionZoneObs( pJCas, (ContentHeading) lastHeading , lastHeading.getBegin(), heading.getBegin() -1);
        lastHeading = heading;
      } // end loop thru content headings
    }
    
    
  }

//-----------------------------------------
 /**
  * createSectionZone
  * 
  * @param pJCas
  * @param pText
  * @param pSubSections
  * @param pSectionBegin
  * @param pSectionEnd
  * @param pIndentation
  */
 // -----------------------------------------
 private SectionZone createSection( JCas              pJCas, 
                                    StringBuffer      pText, 
                                    List<NestedSection>  pNestedSections,
                                    int               pSectionBegin, 
                                    int               pIndentation ) {
   
   
    SectionZone statement = new SectionZone(pJCas);
    statement.setBegin(  pSectionBegin);
    statement.setEnd(    pSectionBegin + pText.length() -1 );
    statement.setId( "CiitizenSectionAnnotator_" + this.annotationCounter++);
    statement.setIndentation(pIndentation);
    statement.addToIndexes();
    
    if ( pNestedSections != null && !pNestedSections.isEmpty()) {
      FSArray subSectionz = new FSArray(pJCas, pNestedSections.size() );
     
      int numberOfNestedSections = pNestedSections.size();
      for ( int i = 0; i < numberOfNestedSections; i++ ) {
        NestedSection aNestedSection = pNestedSections.get(i);
        aNestedSection.setParentSection(statement);
        
      
        subSectionz.set(i, aNestedSection);
       }
       pNestedSections.clear();
     //  subSectionz.addToIndexes();
       statement.setNestedSection(  subSectionz);
    }
   return statement;
 } // end Method createSubSection()  -------
  
  
  
//-----------------------------------------
 /**
  * createSectionZone
  * 
  * @param pJCas
  * @param pConcept
  */
 // -----------------------------------------
 private void createSectionZoneObs( JCas pJCas, ContentHeading pHeading, int pBegin, int pEnd ) {
   
   
    SectionZone statement = new SectionZone(pJCas);
   statement.setBegin(                  pBegin);
   statement.setEnd(                    pEnd);
 	 statement.setId( "CiitizenSectionAnnotator_" + this.annotationCounter++);
   statement.setSectionTypes( pHeading.getSectionName());
   statement.setSectionName( ((ContentHeading)pHeading).getSectionName());
   
	 statement.addToIndexes();       
 } // end Method createDate()  -------------
  
	  
 
//-----------------------------------------
/**
 * createSubSection
 * 
 * @param pJCas
 * @param pBuff
 *
 * @param pBegin
 * @param pEnd
 * @param pIndentation
 */
// -----------------------------------------
private NestedSection createNestedSection( JCas pJCas, String pBuff, int pBegin, int pIndentation ) {
  
  
   NestedSection statement = new NestedSection(pJCas);
   statement.setBegin(                  pBegin);
   statement.setEnd(                    pBegin + pBuff.length());
   statement.setId( "CiitizenSectionAnnotator_SubSection_" + this.annotationCounter++);
   statement.setIndentation(pIndentation);
  
  
   
   statement.addToIndexes();
   
   return statement;
} // end Method createSubSection()  -------------
 

//-----------------------------------------
/**
* createSubSection
* 
* @param pJCas
* @param pBuff
*
* @param pBegin
* @param pEnd
* @param pDelimiter
* @param subSection
*/
//-----------------------------------------
private final Annotation createSlotValue( JCas pJCas, String pBuff, int pBegin, int pEnd,  int pDelimiter, Annotation subSection  ) {


 SlotValue slotValue = new SlotValue(pJCas);
 slotValue.setBegin(                  pBegin);
 slotValue.setEnd(                    pBegin + pBuff.length());
 slotValue.setId( "CiitizenSectionAnnotator_slotValue_" + this.annotationCounter++);
 slotValue.setParent( subSection);
 slotValue.addToIndexes();
 
 ContentHeading heading = new ContentHeading(pJCas );
 heading.setBegin( pBegin);
 heading.setEnd( pBegin + pDelimiter);
 heading.setId("CiitizenSectionAnnotator_slotValue_" + this.annotationCounter++);
 heading.setParent(slotValue);
 heading.addToIndexes();
 slotValue.setHeading( heading);
 
 DependentContent value = new DependentContent(pJCas );
 value.setBegin( pBegin + pDelimiter + 1);
 value.setEnd( pEnd);
 value.setId("CiitizenSectionAnnotator_slotValue_" + this.annotationCounter++);
 value.setParent(slotValue);
 value.addToIndexes();
 slotValue.setDependentContent(value);
 
  Delimiter delimiter = new Delimiter(pJCas);
  delimiter.setBegin( pBegin + pDelimiter  );
  delimiter.setEnd(  pBegin + pDelimiter  );
  delimiter.addToIndexes();
  slotValue.setDelimiter( delimiter.getCoveredText());

 return slotValue;
} // end Method createSlotValue()  ------------------

 



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

// =================================================
/**
 * isSectionBreak returns true if a blank line
 * or horizontal rule has been hit
 * @param pNestedSectionBreak 
 * 
 * @param pCurrentIndentation
 * @param pNestedIndentation
 * @param pSectionIndentation
 * @param pLastIndentation
 * @param nextLineLooksLikeAContentHeading 
 * 
 * @return boolean
*/
// =================================================
private final boolean isSectionBreak( boolean pFirstLine, boolean pNestedSectionBreak, int pCurrentIndentation, int pNestedIndentation, 
    int pSectionIndentation, int pLastIndentation, boolean pIsCapitalized, boolean nextLineLooksLikeAContentHeading ) {
 boolean returnVal = false  ;
  
 if ( pFirstLine ) return false;
 
 if ( pNestedSectionBreak ) {
   if ( pNestedIndentation == 0 && nextLineLooksLikeAContentHeading ) 
     returnVal = true;
   else if ( pCurrentIndentation == pNestedIndentation )
     returnVal = false;
   else if (pCurrentIndentation < pNestedIndentation && pCurrentIndentation >= pSectionIndentation )
     returnVal = false;
   else if (pCurrentIndentation > -1 &&  pCurrentIndentation < pNestedIndentation && pCurrentIndentation < pSectionIndentation )
     returnVal = true;
 
 } else if ( pCurrentIndentation == 0 && pLastIndentation == -1 && pSectionIndentation == pCurrentIndentation && pIsCapitalized ) {
   returnVal = true;
  
 } else if ( pCurrentIndentation == 0 && pLastIndentation == -1 && pSectionIndentation == -1 && pIsCapitalized )
   returnVal = true;
 
 
  return returnVal;
} // end Method isSectionBreak() ---------------------

//=================================================
/**
* isNestedSectionBreak returns true if a blank line
* or horizontal rule has been hit
* 
* @param pBuff
* @return boolean
*/
//=================================================
private final boolean isNestedSectionBreak(String pBuff, int pCurrentIndent, int pLastIndent) {
boolean returnVal = true;


if ( !isBlankLine(pBuff) ) 
if (   pBuff.startsWith("======================" ) ||
       pBuff.startsWith("----------------------" ) ||
       pBuff.startsWith("______________________" ) ||
       pBuff.startsWith("++++++++++++++++++++++" ) ||
       pBuff.startsWith("=-=-=-=-=-=-=-=-=-=-=-" ) ||
       pBuff.startsWith("-=-=-=-=-=-=-=-=-=-=-=" ) ||
       pBuff.startsWith("**********************" ) ||
       pBuff.startsWith("+---------------------" )  )
    returnVal = true;
else if (pCurrentIndent < pLastIndent )
  returnVal = true;
else 
  returnVal = false;

return returnVal;
} // end Method isNestedSectionBreak() ---------------------


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
     
     initialize( args );
     
   } catch (Exception e ) {
     e.printStackTrace();
     String msg = "Issue initializing " + annotatorName + " " + e.toString() ;
     GLog.println(GLog.ERROR_LEVEL, msg);
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
    GLog.println(GLog.ERROR_LEVEL, "Issue initializing the " + annotatorName + " " + e.toString() );
    throw new ResourceInitializationException();
  }
  

  
} // end Method initialize()






 // ---------------------------
 // Global Variables
 // ---------------------------

 private int annotationCounter = 0;
 private ProfilePerformanceMeter              performanceMeter = null;

} // end Class ExampleAnnotator() ---------------
