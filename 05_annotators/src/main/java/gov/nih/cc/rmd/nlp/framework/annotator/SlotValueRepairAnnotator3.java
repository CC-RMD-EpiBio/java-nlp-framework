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
