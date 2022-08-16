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
 * LabsPanelAnnotator decomposes lab panels to its constituent parts
 *   test name 
 *   value
 *   range
 *   eventDate
 *   interpretation
 *   comment
 *   status 
 *  
 *
 *
 * @author  Guy Divita 
 * @created Aug 23, 2018
 *
 * 
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.labs;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.ClinicalStatusEvidence;
import gov.nih.cc.rmd.framework.LabObservation;
import gov.nih.cc.rmd.framework.Observation;
import gov.nih.cc.rmd.framework.ObservationEvidence;
import gov.nih.cc.rmd.framework.ObservationsPanel;
import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.framework.TestComment;
import gov.nih.cc.rmd.framework.TestName;
import gov.nih.cc.rmd.framework.TestRange;
import gov.nih.cc.rmd.framework.TestValue;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Line;
import gov.va.vinci.model.temporal.AbsoluteDate;

public class LabPanelAnnotator extends JCasAnnotator_ImplBase {

  // -----------------------------------------
  /**
   * process retrieves lines of the document, labels those that are questions as
   * QuestionAndAnswer elements.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {

      this.performanceMeter.startCounter();
      
      // retrieve section zones that are marked with resultSectionEvidence -
      // these can
      // be ccda Panel or Procedure zones

      List<Annotation> resultsSectionSections = getResultSectionSections(pJCas);

      if (resultsSectionSections != null && !resultsSectionSections.isEmpty())
        for (Annotation section : resultsSectionSections)
          processResultsSection(pJCas, section);

      this.performanceMeter.stopCounter();
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process",
          "Issue with " + this.getClass().getName() + " " + e.toString());
      // throw new AnalysisEngineProcessException();
    }
   

  } // end Method process() ----------------

  // =================================================
  /**
     * processResultsSection breaks this section into 
     * observations
     * 
     * sets the section's event/statement date
     * 
     * @param pJCas
     * @param pSection
    */
    // =================================================
    private final void processResultsSection(JCas pJCas, Annotation pSection) {
     
      // Find the statement/event date from the first lines
      Annotation eventDate = null;
      Annotation observation = null;
      Annotation clinicalStatus = null;
      String line = null;
      String panelName = ((SectionZone)pSection).getSectionName();
      List<Annotation> lines = UIMAUtil.getAnnotationsBySpan( pJCas, Line.typeIndexID, pSection.getBegin(), pSection.getEnd() );
      
      
      if ( lines != null && !lines.isEmpty()) {
      
        List<Annotation> observations = new ArrayList<Annotation> ();
        int i = 0;
        for ( i = 0; i < 2; i++ ) {
          line = lines.get(i).getCoveredText();
          clinicalStatus = getClinicalStatus( pJCas, lines.get(i));
          if ( (eventDate = getEventDate( pJCas, lines.get(i)) ) != null )
           break;
        }
       
        for ( ;i < lines.size(); i++ ) {
          line = lines.get(i).getCoveredText();
          if ( ( observation = processObservation( pJCas, lines.get(i), eventDate ) )!= null )
              observations.add( observation );
        }
        
        if ( observations != null && !observations.isEmpty()) {
           ObservationsPanel panel = createObservationsPanel( pJCas, pSection, panelName, observations, eventDate, clinicalStatus );
           for ( Annotation observation_ : observations )
             ((Observation) observation_).setParent( panel.getPanelName());
        }
      }
      
    } // end Method processResultsSection() ---------

  

  // =================================================
  /**
   * createObservationsPanel 
   * 
   * @param pJCas
   * @param pSection
   * @param pPanelName
   * @param pObservations
   * @param pEventDate
   * @param pClinicalStatus
   * @return ObservationsPanel
  */
  // =================================================
 private ObservationsPanel createObservationsPanel(JCas pJCas, Annotation pSection, String pPanelName, List<Annotation> pObservations, Annotation pEventDate, Annotation pClinicalStatus) {
   
   ObservationsPanel statement = new ObservationsPanel( pJCas);
   
   String eventDate = null;
   String clinicalStatus = null;
   if ( pEventDate     != null ) eventDate = pEventDate.getCoveredText();
   if ( pClinicalStatus!= null)  clinicalStatus = pClinicalStatus.getCoveredText();
   
   statement.setBegin(  pSection.getBegin() );
   statement.setEnd(    pSection.getEnd());
   statement.setId( "LabPanelAnnotator_" + this.annotationCounter++);
   statement.setAssertionStatus("asserted");
   statement.setPanelName( pPanelName);
   statement.setObservations(  UIMAUtil.list2FsArray(pJCas, pObservations));
   statement.setEventDate(  eventDate );
   statement.setStatementDate( eventDate);
   statement.setClinicalStatus( clinicalStatus );
   statement.setComments( null); // [TBD]
   
   
   statement.addToIndexes();
   return statement;
   
  } // end Method createObservationsPanel () ---------

  // =================================================
  /**
   * processObservation looks for a test, value, range
   * 
   * This method will return null if there are no tabs in the line
   *  
   * look for the following in the following columns
   * first column should be a test name
   * second column should have a number value or a short string like a color or value like clear
   * second column could have a comment in it
   * third column could be blank or have a range in it
   *  
   * 
   * @param pJCas
   * @param pLine
   * @param pEventDate
   * @return Observation  
  */
  // =================================================
  private LabObservation processObservation(JCas pJCas, Annotation pLine, Annotation pEventDate) {
   
    LabObservation observation = null;
    String lineText = pLine.getCoveredText();
    int commentField = -1;
    String testName = null;
    String testValue = null;
    String testComment = null;
    String testRange = null;
    int testValueOffset = -1;
    int testCommentOffset = -1;
    int testRangeOffset = -1;
    if ( lineText == null || lineText.trim().length() == 0 || !lineText.contains("\t" ))  return null;
    
    String cols[] = U.split( lineText, "\t") ;
    if ( cols.length > 1) {
       testName = cols[0].trim();
       if ( cols[1] != null ) {
         testValueOffset = pLine.getBegin() + cols[0].length() + 1;
         if ((commentField = cols[1].toLowerCase().indexOf("comment")) > -1 ) {
          
          testValue = cols[1].substring(0,  commentField );
          testComment = cols[1].substring(commentField );
          testCommentOffset = testValueOffset + commentField ;
        
        } else {
          testValue = cols[1].trim();
        }
       }
    } // end if cols > 1
    
    if ( cols.length > 2)  {  // has a range field
      if ( cols[2] != null ) {
        testRange = cols[2].trim();
        testRangeOffset = pLine.getBegin() + cols[0].length() + cols[1].length() + 2;
      }
    }
    
    String eventDate = null;
    if ( pEventDate != null )
      eventDate = pEventDate.getCoveredText();
    
    observation = new LabObservation(pJCas );
    observation.setBegin( pLine.getBegin());
    observation.setEnd( pLine.getEnd());
    observation.setTestName(  testName);
    observation.setTestValue( testValue);
    observation.setResultRange( testRange);
    observation.setComment( testComment);
    observation.setEventDate(  eventDate );
   // observation.setInterpretation(v);
    observation.addToIndexes();
    
    
    // create testName
    // create testValue
    // create testRange
    // create testComment
    // create testInterpretation
    createObservationEvidence( pJCas, TestName.class, pLine.getBegin(), pLine.getBegin() + testName.length(), observation);
    createObservationEvidence( pJCas, TestValue.class, testValueOffset, testValueOffset + testValue.length(),  observation);
    if ( testRange != null )
      createObservationEvidence( pJCas, TestRange.class, testRangeOffset, testRangeOffset + testRange.length(), observation);
    if ( testComment != null )
      createObservationEvidence( pJCas, TestComment.class, testCommentOffset, testCommentOffset + testComment.length(), observation);
   // if ( eventDate != null )
   //   createObservationEvidence( pJCas, TestDate.class, )
    
    
    return observation;
  } // end Method processObservation() ---------------

  // =================================================
  /**
   * createObservationEvidence creates an ObservationEvidence tied back to the panel
   * 
   * @param pJCas
   * @param pClassName
   * @param pBeginOffset
   * @param pEndoffset
   * @param pParentPanel
  */
  // =================================================
  private final void createObservationEvidence(JCas pJCas, Class<?> pClassName, int pBeginOffset, int pEndOffset, Annotation pParentPanel ) {
    
    try {
      Constructor<?> c = pClassName.getConstructor(new Class[] {
          JCas.class
      });
      Object statement = c.newInstance(pJCas);

      ((ObservationEvidence) statement).setBegin(pBeginOffset);
      ((ObservationEvidence) statement).setEnd(pEndOffset);
      ((ObservationEvidence) statement).setId("LabPanelAnnotator_" + annotationCounter++);
      ((ObservationEvidence) statement).setParentPanel( pParentPanel);
      ((ObservationEvidence) statement).addToIndexes(pJCas);

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println( GLog.ERROR_LEVEL, this.getClass(), "createObservationEvidence", "Something went wrong here " + e.toString());
    }
 
 
   
    
  } // end Method createObservationEvidence() -------

  // =================================================
  /**
   * getClinicalStatus looks for a status like "preliminary|final|registered|amended
   * 
   * @param pJCas
   * @param pLine
   * @return Annotation
  */
  // =================================================
  private Annotation getClinicalStatus(JCas pJCas, Annotation pLine) {
   List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pLine.getBegin(), pLine.getEnd());
    Annotation returnVal = null;
    if ( terms != null && !terms.isEmpty()) {
      for ( Annotation term : terms ) {
        String semanticTypes = ((LexicalElement) term).getSemanticTypes();
        if ( semanticTypes != null && semanticTypes.contains("ClinicalStatusEvidence")) { 
          returnVal = createClinicalStatus( pJCas, term );
          break;
        }
      }
   }
   return returnVal;
  } // end Method getClinicalStatus() ----------------

  // =================================================
  /**
   * createClinicalStatus 
   * 
   * @param pJCas
   * @param pTerm
   * @return ClinicalStatusEvidence
  */
  // =================================================
   private final Annotation createClinicalStatus(JCas pJCas, Annotation pTerm) {
     ClinicalStatusEvidence statement = new ClinicalStatusEvidence(pJCas );
     statement.setBegin( pTerm.getBegin());
     statement.setEnd(  pTerm.getEnd() );
     statement.addToIndexes();
  return statement;
  } // end Method createClinicalStatus() -----------

  // =================================================
  /**
   * getEventDate retrieves the event date tagged at the top of the panel. This
   * looks for a date on the line that has the header or if not found the next
   * line
   * 
   * @param pJCas
   * @param pLine
   * @return Annotation
   */
  // =================================================
  private final Annotation getEventDate(JCas pJCas, Annotation pLine) {

    Annotation returnVal = null;
    List<Annotation> eventDates = UIMAUtil.getAnnotationsBySpan(pJCas, AbsoluteDate.typeIndexID,
        pLine.getBegin(), pLine.getEnd());

    if (eventDates != null && !eventDates.isEmpty())
      returnVal = eventDates.get(0);
    return returnVal;
  } // end Method getEventDate() -------------------

  // =================================================
  /**
   * getResultSectionSections
   * 
   * @param pJCas
   * @return List<Annotation>
   */
  // =================================================
  private final List<Annotation> getResultSectionSections(JCas pJCas) {

    List<Annotation> resultsSections = null;
    List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, SectionZone.typeIndexID);

    if (sections != null && !sections.isEmpty()) {
      resultsSections = new ArrayList<Annotation>();
      for (Annotation section : sections) {
        String aType = ((SectionZone) section).getSectionTypes();
        if (aType != null && aType.contains("resultSectionEvidence"))
          resultsSections.add(section);
      }
    }
    return resultsSections;
  } // end Method getResultSectionSections() --------
  
  

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
      String msg = "Issue initializing " + this.getClass().getSimpleName() + " " + e.toString() ;
      GLog.println(GLog.ERROR_LEVEL, msg);
      throw new ResourceInitializationException();
    }
    
  } // end Method initialize() --------

  // ----------------------------------
  /**
   * initialize initializes the class. Parameters are passed in via a String
   * array with each row containing a --key=value format.
   * 
   * It is important to adhere to the posix style "--" prefix and include a
   * "=someValue" to fill in the value to the key.
   * @param pArgs
   * @throws ResourceInitializationException
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {

    try {

      this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getSimpleName());

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue initizlizng labPanel Annotator " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", msg);
      throw new ResourceInitializationException();
    }
  } // end Method initialize() -------

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCounter = 0; // new Term Counter.

  private ProfilePerformanceMeter performanceMeter = null;

} // end Class LabPanelAnnotator() ---------------
