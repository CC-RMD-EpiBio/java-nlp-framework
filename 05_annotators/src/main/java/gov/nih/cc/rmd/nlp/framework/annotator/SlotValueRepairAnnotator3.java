// =================================================
/**
 * SlotValueRepairAnnotator3 
 *   creates dependent content of slots that have a slotValue end value that
 *   is much longer than the combined slot delimiter and value.
 *   
 *   transforms slot values where the value has multiple sentences in it - into
 *   a separate sectionzone with content Heading. - The slotValue object gets removed.
 * @author  Guy Divita 
 * @created May 3, 2019
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Delimiter;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.SlotValueDelimiter;


public class SlotValueRepairAnnotator3 extends JCasAnnotator_ImplBase {
  


  private int annotationCounter = 0;



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
    List<Annotation> slotsAndValues = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID);
    

    if ( slotsAndValues != null && !slotsAndValues.isEmpty() ) {
      for ( Annotation slotAndValue : slotsAndValues ) {
        process( pJCas, (SlotValue) slotAndValue  );
        findSections( pJCas, (SlotValue) slotAndValue);
      }
    }
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
     // throw new AnalysisEngineProcessException();
    }
  } // end Method process
      
  //----------------------------------
  /**
   * process
   * 
   * @param pJCas
   * @param pSlotAndValue
   *
   * 
   **/
  // ----------------------------------  
  private void process(JCas pJCas, SlotValue pSlotAndValue ) {
  
   Delimiter delimiter = null;
   ContentHeading slot = pSlotAndValue.getHeading();
   DependentContent value = pSlotAndValue.getDependentContent();
   String valueValue = null;
   
   if ( value != null )
     valueValue = value.getCoveredText();
   
   if ( valueValue != null && valueValue.trim().length() == 0)  valueValue = null;
   
   List<Annotation>   delimiters = UIMAUtil.getAnnotationsBySpan(pJCas,SlotValueDelimiter.typeIndexID, pSlotAndValue.getBegin(), pSlotAndValue.getEnd() );
   if ( delimiters != null && !delimiters.isEmpty())  
     delimiter = (Delimiter) delimiters.get(0);
   else
     return;
   
   if (( value == null || valueValue == null) &&  pSlotAndValue.getEnd() > delimiter.getEnd())
     createDependentContent( pJCas, pSlotAndValue, delimiter.getEnd() + 1, pSlotAndValue.getEnd());
   
   
  } // End Method process() ==========================
   
   
  // =================================================
  /**
   * createDependentContent 
   * 
   * @param pJCas
   * @param pSlotAndValue
   * @param pBegin
   * @param pEnd
  */
  // =================================================
  private void createDependentContent(JCas pJCas, SlotValue pSlotAndValue, int pBegin, int pEnd) {
    
    DependentContent  content = new DependentContent(pJCas);
   
    content.setBegin( pBegin);
    content.setEnd  ( pEnd );
    content.setId("SlotValueRepair3_" + this.annotationCounter++);
    content.addToIndexes();
    content.setParent( pSlotAndValue);
    pSlotAndValue.setDependentContent( content);
    
  
  } // End Method createDependentContent() ========
  



// =================================================
  /**
   * findSections examines the dependent content - if
   * the dependent content has multiple sentences, make a sectionZone from
   * the combination of the contentHeading and the dependent content.
   * remove the slot value.
   * 
   * 
   * @param pJCas
   * @param pSlotAndValue
  */
  // =================================================
 private void findSections(JCas pJCas, SlotValue pSlotAndValue) {
    
   ContentHeading contentHeading = pSlotAndValue.getHeading();
   DependentContent dependentContent = pSlotAndValue.getDependentContent();
   
   if ( dependentContent != null  ) {
   
   List<Annotation> sentences = UIMAUtil.getAnnotationsBySpan(pJCas,  Sentence.typeIndexID, dependentContent.getBegin(), dependentContent.getEnd() );
   
   if ( sentences != null && sentences.size() > 1) {
     
     // create SectionZone
     createSectionZone( pJCas, contentHeading, pSlotAndValue.getBegin(), pSlotAndValue.getEnd() );
     // remove slotAndValue
     pSlotAndValue.removeFromIndexes();
     
   }
     
   } // end Method findSections() --------------------
   
  } // =================================================
  /**
   * createSectionZone
   * 
   * @param pJCas
   * @param pSectionBegin
   * @param pSectionEnd
  */
  // =================================================
 void createSectionZone(JCas pJCas, Annotation pContentHeading,  int pSectionBegin, int pSectionEnd) {
   
   String sectionTypes = "CCDA_Section";
   String kindsOfAnnotations = "none";
   SectionZone statement = new SectionZone(pJCas);
   statement.setBegin(  pSectionBegin);
   statement.setEnd(    pSectionEnd  );
   statement.setId( this.getClass().getName() + "_" + this.annotationCounter++);
   if ( pContentHeading != null ) {
     statement.setSectionName(  pContentHeading.getCoveredText());
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
   
   
   statement.setAnnotationTypes( kindsOfAnnotations);
   statement.setSectionTypes( sectionTypes);
   statement.addToIndexes();
   
  
    
  }

  // end Method FindSection() -----------------

//----------------------------------
 /**
  * destroy
 * 
  **/
 // ----------------------------------
 public void destroy() {
   this.performanceMeter.writeProfile( this.getClass().getSimpleName());
 }
 
 
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
   
  
   String[] args = null;
   try {
     args                 = (String[]) aContext.getConfigParameterValue("args");  
    
   } catch (Exception e ) {
    
   }
   initialize( args);
  
 } // end Method initialize() ---------

 //----------------------------------
 /**
  * initialize 
  *
  * @param pArgs
  * 
  **/
 // ----------------------------------
 public void initialize(String pArgs[]) throws ResourceInitializationException {
   
   this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
   

} // end Method initialize() --------------
    
 
  // -----------------------------------------
  // class Variables
  // -----------------------------------------

//---------------------------------------
//Global Variables
//---------------------------------------
  private ProfilePerformanceMeter  performanceMeter = null;
  
  
} // end Class SlotValueRepairAnnotator ---------------------
