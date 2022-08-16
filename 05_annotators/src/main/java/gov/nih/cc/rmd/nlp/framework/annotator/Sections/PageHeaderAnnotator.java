// =================================================
/**
 * Ciitizen sections identifies the section(zones)
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
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.PageHeader;
import gov.nih.cc.rmd.framework.PageHeaderEvidence;
import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.framework.model.Shape;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.LexicalElement;



public class PageHeaderAnnotator extends JCasAnnotator_ImplBase {
 
 
  public static final String annotatorName = PageHeaderAnnotator.class.getSimpleName();
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   * We are looking for the first content heading that does not have
   * "date" or "Patient" in the name  that is doesn't have  shapes (location, phone, person
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    
    this.performanceMeter.startCounter();
    
    int pageNumber = VUIMAUtil.getPageNumber(pJCas);
    
    
    if ( ! processKnownHeaderSections( pJCas ))
      processUnknownSections( pJCas );
    
    
    this.performanceMeter.stopCounter();     
    
  } // End Method process() ------------------
  
 

// =================================================
  /**
   * processKnownHeaderSections 
   * 
   * @param pJCas
   * @return boolean 
  */
  // =================================================
  private boolean processKnownHeaderSections(JCas pJCas) {
  
    boolean returnVal = false;
    
    List<Annotation> sectionZones = UIMAUtil.getAnnotations(pJCas, SectionZone.typeIndexID, false );     
    
    if ( sectionZones!= null && !sectionZones.isEmpty()) {
   
      boolean found = false;
      Annotation previousSection = null;
      Annotation headerSection = null;
      int twentyPercentOfDoc = (int) (sectionZones.size() * .40 );
      Annotation section = null;
      for ( int i = 0; i < twentyPercentOfDoc; i++ ) { 
         section = sectionZones.get(i);
         String sectionName = ((SectionZone) section).getSectionName();
     
        if ( isPageHeaderSection( section)  ) {
          found = true;
          headerSection = section;
       
        // stop if you've hit a ccda section name
        } else if ( isContentSection( pJCas, section ) ) {
            if (containsHeaderWords( pJCas, previousSection ) ) {
              found = true;
              headerSection = previousSection;
            }
            break;
          
        }
        previousSection = section;
      }
     
      if ( found )
        createHeaderThrough( pJCas, headerSection);
    }
    
    return returnVal;
  } // end Method processKnownHeaderSections() 
  



// =================================================
  /**
   * processUnknownSections 
   * 
   * @param pJCas
  */
  // =================================================
  private void processUnknownSections(JCas pJCas) {
    try {
      
      List<Annotation> sectionZones = UIMAUtil.getAnnotations(pJCas, SectionZone.typeIndexID, false );      
  if ( sectionZones!= null && !sectionZones.isEmpty()) {
    
    UIMAUtil.sortByOffset(sectionZones);
    UIMAUtil.uniqueAnnotations(sectionZones);
   
    boolean notFirstPage = isFirstPage(pJCas );
    
    for ( int i = 0; i < sectionZones.size(); i++ ) {
      Annotation sectionZone = sectionZones.get(i);
      Annotation nextSectionZone = null;
      Annotation nextNextSectionZone = null;
      String sectionText = null;
      String nextSectionText = null;
      if ( i < sectionZones.size() -1) {
        nextSectionZone = sectionZones.get(i + 1);
        sectionText = sectionZone.getCoveredText();
      }
      if ( i < sectionZones.size() -2) {
        nextNextSectionZone = sectionZones.get(i + 2);
        
      }
      
      // -------------------------------
      // If we are not on the first page, and you've gone
      // through some sections without finding pageHeader 
      // evidence, there's not going to be a page header
      // on this page,  give up.
      if ( notFirstPage && i > 2 ) 
        break;
      
      
      if ( ( isContentSection(pJCas, sectionZone ))  ||
           (  
             !isPageHeaderSection(  sectionZone)  &&  
             !isFilledWithShapes(pJCas, sectionZone )   &&
             !containsHeaderWords( pJCas, sectionZone)  &&
             !containsHeaderWords( pJCas, nextNextSectionZone) )
           
           
         )  {
          if ( notFirstPage && i == 0 || !notFirstPage )
            createHeader( pJCas, sectionZone );
            break;
          }
      } // end loop 
    }
   
} catch (Exception e) {
  e.printStackTrace();
  GLog.println(GLog.ERROR_LEVEL,"Issue in CiitizenSection Annotator " + e.toString());
}
    // end Method processUnknownSections() 
  }



// =================================================
/**
 * isFirstPage returns true if the document header
 * has evidence that it is the first page of a
 * document
 * 
 * @param pJCas
 * @return boolean
*/
// =================================================
  private final boolean isFirstPage(JCas pJCas) {
  
    boolean returnVal = false;
    int pageNumber = -1;
    try {
      pageNumber = VUIMAUtil.getPageNumber(pJCas);
    } catch (Exception e) {
      e.printStackTrace();
      
    }
   if ( pageNumber > 1)
     returnVal = true;
    
  return returnVal;
} // end Method isFirstPage() ----------------------



// =================================================
  /**
   * isContentSection returns true if this section is
   * one that should not be in a header - like Allergies
   * 

   * @param pSectionZone
   * @return boolean 
  */
  // =================================================
  private boolean isContentSection(JCas pJCas, Annotation pSectionZone) {
    
    boolean returnVal = false;
    if ( pSectionZone != null ) {
      String sectionName = ((SectionZone) pSectionZone).getSectionName();
      if ( sectionName != null )  {
      
        
        Annotation contentHeading = getContentHeading( pJCas, pSectionZone);
        if ( contentHeading != null ) {
          String contentHeadingType = getContentHeadingType ( (ContentHeading) contentHeading);
          if ( contentHeadingType != null && (contentHeadingType.equals("ccdaSectionName") || contentHeadingType.equals("geneSectionName")))
            returnVal = true;
        } else {
      
          sectionName = sectionName.toLowerCase();
          if ( this.contentSectionNames.contains( sectionName ))
            returnVal = true;
        }
      }
    }
    return returnVal;
  } // end Method isContentSection()  -------------
  

// =================================================
  /**
   * getContentHeadingType  returns ccdaSectionName or null 
   * 
   * @param pContentHeading
   * @return String
  */
  // =================================================
  private final String getContentHeadingType(ContentHeading pContentHeading) {
    String returnVal = null;
    
    StringArray otherFeaturez;
    if ( pContentHeading != null  ) {
      otherFeaturez = pContentHeading.getOtherFeatures();
      String[] otherFeatures;
      if ( otherFeaturez != null ) {
        otherFeatures = UIMAUtil.stringArrayToArrayOfString( otherFeaturez);
        if ( otherFeatures != null && otherFeatures.length > 0 )
          returnVal = otherFeatures[0];
      }
    }
    return returnVal;
  } // end Method getContentHeadingType() ------------



// =================================================
/**
 * getContentHeading 
 * 
 * @param pJCas
 * @param pSectionZone
 * @return Annotation
*/
// =================================================
private final Annotation getContentHeading(JCas pJCas, Annotation pSectionZone) {
 Annotation contentHeading = null;
 
 List<Annotation> contentHeadings = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, ContentHeading.typeIndexID, pSectionZone.getBegin(), pSectionZone.getEnd() );

 if ( contentHeadings != null && !contentHeadings.isEmpty())
   contentHeading = contentHeadings.get(0);
 
 return contentHeading;
} // end Method getContentHeading() ---------------



//=================================================
 /**
  * isPageHeaderSection returns true if this section is
  * one that should be in a header - like date of service: xxxx
  * 
  * @param pSectionZone
  * @return boolean 
 */
 // =================================================
 private boolean isPageHeaderSection( Annotation pSectionZone) {
   
   boolean returnVal = false;
   if ( pSectionZone != null ) {
     String sectionName = ((SectionZone) pSectionZone).getSectionName() ;
     if ( sectionName != null ) {
       sectionName = sectionName.toLowerCase();
       if ( this.pageHeaderSectionNames.contains( sectionName ))
       returnVal = true;
     }
   }
   return returnVal;
 } // end Method isContentSection()  -------------
 

  

// =================================================
  /**
   * containsHeaderWords returns true if words like
   *   page, report, statement, printed, final, phone ... are in it
   * 
   * @param pJCas
   * @param pSectionZone
   * @return boolean
  */
  // =================================================
  private final boolean containsHeaderWords(JCas pJCas, Annotation pSectionZone) {
   
    boolean returnVal = false;
    int pageHeaderTerms = 0;
    if ( pSectionZone != null ) {
    
    List<Annotation> terms = UIMAUtil.getAnnotationsBySpan( pJCas, LexicalElement.typeIndexID, pSectionZone.getBegin(), pSectionZone.getEnd() );
    
    if ( terms != null && !terms.isEmpty()) {
      for ( Annotation term : terms ) {
        
        String termType = ((LexicalElement)term).getSemanticTypes();
        
        if ( termType != null && termType.contains("PageHeaderEvidence")) {
          createPageHeaderEvidence( pJCas, term);
          pageHeaderTerms++;
        }         
      }
    }
    
    if ( pageHeaderTerms > 1)
      returnVal = true;
    }
    
    return returnVal;
  } // end Method containsHeaderWords() 
  



// =================================================
/**
 * createPageHeaderEvidence
 * 
 * @param pJCas
 * @param pTerm
 * @return PageHeaderTerm
*/
// =================================================
private  final PageHeaderEvidence createPageHeaderEvidence(JCas pJCas, Annotation pTerm) {
  
  PageHeaderEvidence statement = new PageHeaderEvidence ( pJCas);
  statement.setBegin( pTerm.getBegin());
  statement.setEnd( pTerm.getEnd());
  statement.setId( "PageHeaderAnnotator_" + annotationCounter++);
  statement.addToIndexes();
  
  return statement;
  
}



// =================================================
  /**
   * isFilledWithShapes returns true if more than one shape is seen
   * within the section zone
   * 
   * @param pSectionZone
   * @return boolean
  */
  // =================================================
  private final boolean isFilledWithShapes(JCas pJCas, Annotation pSectionZone) {
    boolean returnVal = false;
    
    List<Annotation> shapes = UIMAUtil.getAnnotationsBySpan(pJCas, Shape.typeIndexID, pSectionZone.getBegin(), pSectionZone.getEnd(), true );
    int shapeCtr = 0;
    if ( shapes != null && !shapes.isEmpty()) {
      for ( Annotation shape : shapes ) {
       
        if ( // !shape.getType().getName().contains("Date") &&
             !shape.getType().getName().contains("UnitOfMeasure")) 
          shapeCtr++;
      }
      if ( shapeCtr > 0 )
        returnVal = true;
    }
    return returnVal;
  }  // end Method isFilledWithShapes() 



  // =================================================
  /**
   * createHeader
   * 
   * @param pJCas
   * @param pSectionZone
  */
  // =================================================
  private void createHeader(JCas pJCas, Annotation  pSectionZone) {
 
   
    PageHeader statement = new PageHeader(pJCas);
    statement.setBegin(  0);
    statement.setEnd(   pSectionZone.getBegin()-1 );
    
    if ( statement.getEnd() > 1 ) {
    statement.setSectionName("PageHeader");
    // statement.setSectionTypes(v);
    statement.setId( "HeaderFooterSectionsAnnotator_" + this.annotationCounter++);
    statement.addToIndexes();
    }
  
 } // end Method createHeader()  -------
  

  // =================================================
  /**
   * createHeaderThrough
   * 
   * @param pJCas
   * @param pSectionZone
  */
  // =================================================
  private void createHeaderThrough(JCas pJCas, Annotation  pSectionZone) {
 
   
    PageHeader statement = new PageHeader(pJCas);
    statement.setBegin(  0);
    statement.setEnd(   pSectionZone.getEnd());
    statement.setSectionName("PageHeader");
    // statement.setSectionTypes(v);
    statement.setId( "HeaderFooterSectionsAnnotator_" + this.annotationCounter++);
    statement.addToIndexes();
  
 } // end Method createHeader()  -------
  
  
 



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
    String pageHeaderWordList = U.getOption(pArgs, "--pageHeaderWords=", "resources/com/ciitizen/framework/sections/pageHeaderWords.txt" );
    String contentSections = U.getOption(pArgs, "--contentSectionNames=", "resources/com/ciitizen/framework/sections/ccdaSectionHeaders.lragr" );
    String pageHeaderSections = U.getOption(pArgs, "--pageHeaderSectionNames=", "resources/com/ciitizen/framework/sections/pageHeaderSectionNames.txt" );
     
    
    loadHeaderWords(pageHeaderWordList );
    loadContentSections( contentSections );
    loadPageHeadertSections( pageHeaderSections );
   
    
  } catch (Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", "Issue initializing the " + annotatorName + " " + e.toString() );
    throw new ResourceInitializationException();
  }
  
} // end Method Initialize() -----------

// =================================================
/**
 * loadHeaderWords 
 * 
 * @param pPageHeaderWordList
 * @throws ResourceInitializationException 
*/
// =================================================
private final void loadHeaderWords(String pPageHeaderWordList) throws ResourceInitializationException {
  try {
  
   String[] rows = U.readClassPathResourceIntoStringArray(pPageHeaderWordList);
  
   if ( rows != null && rows.length > 0 ) {
     this.headerWords = new ArrayList<String> (rows.length);
  
     for ( String row : rows ) {
       if ( row != null && !row.startsWith("#"))
         this.headerWords.add(row.toLowerCase().trim());
     }
   }
     
   
  } catch (Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadHeaderWords",  "Issue reading in the file   " + pPageHeaderWordList + " " + e.toString() );
    throw new ResourceInitializationException();
  }
  
  
} // end Method loadHeaderWords() 



 // =================================================
/**
 * loadContentSections 
 * 
 * @param pContentSectionsFile
 * @throws ResourceInitializationException 
*/
// =================================================
private void loadContentSections(String pContentSectionsFile ) throws ResourceInitializationException {
  try {
    
    String[] rows = U.readClassPathResourceIntoStringArray(pContentSectionsFile );
  
    if ( rows != null && rows.length > 0 ) {
      this.contentSectionNames = new HashSet<String>(rows.length );
      for ( String row : rows ) {
        if ( row != null && !row.startsWith( "#") && row.trim().length() > 0) {
          String cols[] = U.split(row);
          this.contentSectionNames.add( cols[1].trim().toLowerCase());
        }
         
     }
     
   }
   
  } catch (Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadContentSections", "Issue reading in the file   " + pContentSectionsFile + " " + e.toString() );
    throw new ResourceInitializationException();
  }
  
 
}// end Method loadContentSections() 


// =================================================
/**
* loadPageHeaderSections 
* 
* @param pContentSectionsFile
* @throws ResourceInitializationException 
*/
//=================================================
private void loadPageHeadertSections(String pPageHeaderSectionsFile ) throws ResourceInitializationException {
 try {
 
  String[] rows = U.readClassPathResourceIntoStringArray(pPageHeaderSectionsFile);
 
  if ( rows != null && rows.length > 0 ) {
    this.pageHeaderSectionNames = new HashSet<String>(rows.length );
    for ( String row : rows ) {
      if ( row != null && !row.startsWith( "#"))
        this.pageHeaderSectionNames.add( row.trim());
    }
    
  }
  
 } catch (Exception e ) {
   e.printStackTrace();
   GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadPageHeaderSections", "Issue reading in the file   " + pPageHeaderSectionsFile + " " + e.toString() );
   throw new ResourceInitializationException();
 }
 

}// end Method loadPageHeaderSections() 








 // ---------------------------
 // Global Variables
 // ---------------------------

 private int annotationCounter = 0;
 private ProfilePerformanceMeter              performanceMeter = null;
 private Pattern pagePatterns;
private List<String> headerWords = null;
private HashSet<String> contentSectionNames = null;
private HashSet<String> pageHeaderSectionNames = null;

} // end Class ExampleAnnotator() ---------------
