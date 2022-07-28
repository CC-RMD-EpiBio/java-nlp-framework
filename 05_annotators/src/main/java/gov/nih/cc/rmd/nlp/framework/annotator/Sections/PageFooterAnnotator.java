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
 * PageFooter identifies the non clinical text part of the
 * bottom of each page
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

import gov.nih.cc.rmd.framework.PageFooter;
import gov.nih.cc.rmd.framework.PageFooterEvidence;
import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.framework.model.Shape;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.LexicalElement;



public class PageFooterAnnotator extends JCasAnnotator_ImplBase {
 
 
  public static final String annotatorName = PageFooterAnnotator.class.getSimpleName();
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
    
    
    if ( ! processKnownFooterSections( pJCas ))
      processUnknownSections( pJCas );
    
    
    this.performanceMeter.stopCounter();     
    
  } // End Method process() ------------------
  
 

// =================================================
  /**
   * processKnownFooterSections 
   * 
   * @param pJCas
   * @return boolean 
  */
  // =================================================
  private boolean processKnownFooterSections(JCas pJCas) {
  
    boolean returnVal = false;
    
    List<Annotation> sectionZones = UIMAUtil.getAnnotations(pJCas, SectionZone.typeIndexID, false );     
    
    if ( sectionZones!= null && !sectionZones.isEmpty()) {
   
      boolean found = false;
      Annotation footerSection = null;
      int twentyPercentOfDoc = (int) (sectionZones.size() * .50 );
      Annotation section = null;
      for ( int i = sectionZones.size() -1; i > sectionZones.size() - twentyPercentOfDoc; i-- ) { 
         section = sectionZones.get(i);
         String sectionName = ((SectionZone) section).getSectionName();
     
        if ( isPageFooterSection( section)  ) {
          found = true;
          footerSection = section;
          returnVal = true;
        }
      }
     
      if ( found )
        createFooterThrough( pJCas, footerSection);
    }
    
    return returnVal;
  } // end Method processKnownfooterSections() 
  



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
	  int twentyPercentOfDoc = (int) (sectionZones.size() * .50 );
	  if ( twentyPercentOfDoc == 0)
	    twentyPercentOfDoc = sectionZones.size();
    UIMAUtil.sortByOffset(sectionZones);
    UIMAUtil.uniqueAnnotations(sectionZones);
    boolean someKindOfPageFooterSeen = false;
    for ( int i = sectionZones.size() -1; i >= twentyPercentOfDoc; i-- ) {
      Annotation sectionZone = sectionZones.get(i);
      Annotation sectionBelow = null;
      if ( i < sectionZones.size() -1 )
        sectionBelow = sectionZones.get(i + 1);
      String sectionText = sectionZone.getCoveredText();
      
      if (  ( isContentSection( pJCas, sectionZone ) &&  (i == sectionZones.size() -1 ) ) ){
         break;
        
      } else if  (  !isPageFooterSection(  sectionZone)  &&  
             !isFilledWithShapes(pJCas, sectionZone )   &&
             !containsFooterWords( pJCas, sectionZone)  ) {
        
        
           if ( someKindOfPageFooterSeen && sectionBelow != null )
    		  createFooterThrough( pJCas, sectionBelow );     //   $15376.59
            break;
          } else {
        	  someKindOfPageFooterSeen = true;
          }
      
      } // end loop 
    }
   
} catch (Exception e) {
  e.printStackTrace();
  GLog.println(GLog.ERROR_LEVEL,"Issue in CiitizenSection Annotator " + e.toString());
}
    // end Method processUnknownSections() 
  }


//=================================================
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


//=================================================
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



//=================================================
/**
* getContentHeading 
* 
* @param pJCas
* @param pSectionZone
* @return Annotation
*/
//=================================================
private final Annotation getContentHeading(JCas pJCas, Annotation pSectionZone) {
Annotation contentHeading = null;

List<Annotation> contentHeadings = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, ContentHeading.typeIndexID, pSectionZone.getBegin(), pSectionZone.getEnd() );

if ( contentHeadings != null && !contentHeadings.isEmpty())
contentHeading = contentHeadings.get(0);

return contentHeading;
} // end Method getContentHeading() ---------------

 

//=================================================
 /**
  * isPageFooterSection returns true if this section is
  * one that should be in a footer - like date of service: xxxx
  * 
  * @param pSectionZone
  * @return boolean 
 */
 // =================================================
 private boolean isPageFooterSection( Annotation pSectionZone) {
   
   boolean returnVal = false;
   if ( pSectionZone != null ) {
    
     String sectionTypes = ((SectionZone) pSectionZone).getSectionTypes() ;
     
     // String sectionName = ((SectionZone) pSectionZone).getSectionName().toLowerCase();
     // if ( this.PageFooterSectionNames.contains( sectionName ))
    
     if ( sectionTypes != null && sectionTypes.contains("pageFooterSectionName"))
       returnVal = true;
   }
   return returnVal;
 } // end Method isContentSection()  -------------
 

  

//=================================================
/**
 * containsFooterWords returns true if words like
 *   page, report, statement, printed, final, phone ... are in it
 * 
 * @param pJCas
 * @param pSectionZone
 * @return boolean
*/
// =================================================
private final boolean containsFooterWords(JCas pJCas, Annotation pSectionZone) {
 
  boolean returnVal = false;
  int pageHeaderTerms = 0;
  if ( pSectionZone != null ) {
  
  List<Annotation> terms = UIMAUtil.getAnnotationsBySpan( pJCas, LexicalElement.typeIndexID, pSectionZone.getBegin(), pSectionZone.getEnd() );
  
  if ( terms != null && !terms.isEmpty()) {
    for ( Annotation term : terms ) {
      
      String termType = ((LexicalElement)term).getSemanticTypes();
      
      if ( termType != null && termType.contains("PageFooterEvidence")) {
        createPageFooterEvidence( pJCas, term);
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
   * createFooterThrough
   * 
   * @param pJCas
   * @param pSectionZone
  */
  // =================================================
  private void createFooterThrough(JCas pJCas, Annotation  pSectionZone) {
 
	int endOfDocument = 0;
	String docText = pJCas.getDocumentText();
	if ( docText != null && docText.trim().length() > 0)
		endOfDocument = docText.length();
	  
    PageFooter statement = new PageFooter(pJCas);
    statement.setBegin(pSectionZone.getBegin()  );
    statement.setEnd(  endOfDocument);
    statement.setSectionName("PageFooter");
    // statement.setSectionTypes(v);
    statement.setId( "PageFooterAnnotator_" + this.annotationCounter++);
    statement.addToIndexes();
  
 } // end Method createFooter()  -------
  

  // =================================================
  /**
   * createFooterThrough
   * 
   * @param pJCas
   * @param pSectionZone
  */
  // =================================================
  private void createFooter(JCas pJCas, Annotation  pSectionZone) {
 
   
      
    PageFooter statement = new PageFooter(pJCas);
    statement.setBegin(pSectionZone.getBegin()  );
    statement.setEnd(  pSectionZone.getEnd());
    statement.setSectionName("PageFooter");
    // statement.setSectionTypes(v);
    statement.setId( "PageFooterAnnotator_" + this.annotationCounter++);
    statement.addToIndexes();
  
 } // end Method createFooter()  -------
  
  


//=================================================
/**
* createPageFooterEvidence
* 
* @param pJCas
* @param pTerm
* @return PageHeaderTerm
*/
//=================================================
private  final PageFooterEvidence createPageFooterEvidence(JCas pJCas, Annotation pTerm) {
 
 PageFooterEvidence statement = new PageFooterEvidence ( pJCas);
 statement.setBegin( pTerm.getBegin());
 statement.setEnd( pTerm.getEnd());
 statement.setId( "PageFooterAnnotator_" + annotationCounter++);
 statement.addToIndexes();
 
 return statement;
 
}
 



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
    String PageFooterWordList = U.getOption(pArgs, "--pageFooterWords=", "resources/com/ciitizen/framework/sections/pageFooterWords.txt" );
    String contentSections = U.getOption(pArgs, "--contentSectionNames=", "resources/com/ciitizen/framework/sections/ccdaSectionHeaders.lragr" );
   //  String PageFooterSections = U.getOption(pArgs, "--pageFooterSectionNames=", "resources/com/ciitizen/framework/sections/pageFooterSectionNames.txt" );
     
    
    loadFooterWords(PageFooterWordList );
    loadContentSections( contentSections );
   //  loadPageFooterSections( PageFooterSections );
   
    
  } catch (Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", "Issue initializing the " + annotatorName + " " + e.toString() );
    throw new ResourceInitializationException();
  }
  
} // end Method Initialize() -----------

// =================================================
/**
 * loadFooterWords 
 * 
 * @param pPageFooterWordList
 * @throws ResourceInitializationException 
*/
// =================================================
private final void loadFooterWords(String pPageFooterWordList) throws ResourceInitializationException {
  try {
  
   String[] rows = U.readClassPathResourceIntoStringArray(pPageFooterWordList);
  
   if ( rows != null && rows.length > 0 ) {
     this.footerWords = new ArrayList<String> (rows.length);
  
     for ( String row : rows ) {
       if ( row != null && !row.startsWith("#"))
         this.footerWords.add(row.toLowerCase().trim());
     }
   }
     
   
  } catch (Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadFooterWords",  "Issue reading in the file   " + pPageFooterWordList + " " + e.toString() );
    throw new ResourceInitializationException();
  }
  
  
} // end Method loadFooterWords() 



//=================================================
/**
* loadContentSections 
* 
* @param pContentSectionsFile
* @throws ResourceInitializationException 
*/
//=================================================
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
* loadPageFooterSections 
* 
* @param pContentSectionsFile
* @throws ResourceInitializationException 
*/
//=================================================
private void loadPageFooterSections(String pPageFooterSectionsFile ) throws ResourceInitializationException {
 try {
 
  String[] rows = U.readClassPathResourceIntoStringArray(pPageFooterSectionsFile);
 
  if ( rows != null && rows.length > 0 ) {
    this.PageFooterSectionNames = new HashSet<String>(rows.length );
    for ( String row : rows ) {
      if ( row != null && !row.startsWith( "#"))
        this.PageFooterSectionNames.add( row.trim());
    }
    
  }
  
 } catch (Exception e ) {
   e.printStackTrace();
   GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadPageFooterSections", "Issue reading in the file   " + pPageFooterSectionsFile + " " + e.toString() );
   throw new ResourceInitializationException();
 }
 

}// end Method loadPageFooterSections() 








 // ---------------------------
 // Global Variables
 // ---------------------------

 private int annotationCounter = 0;
 private ProfilePerformanceMeter              performanceMeter = null;
 private Pattern pagePatterns;
private List<String> footerWords = null;
private HashSet<String> contentSectionNames = null;
private HashSet<String> PageFooterSectionNames = null;

} // end Class ExampleAnnotator() ---------------
