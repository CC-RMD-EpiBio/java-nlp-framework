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
 * MedicationDates annotator will look for
 * start, stop, expired, dates related to medications 
 * 
 * @author Guy Divita
 * @created Aug 9 2018
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.medications;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.ClinicalStatusEvidence;
import gov.nih.cc.rmd.DoseForm;
import gov.nih.cc.rmd.Medication;

import gov.va.chir.model.ClinicalStatement;
import gov.va.chir.model.CodedEntry;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Line;
import gov.va.vinci.model.temporal.AbsoluteDate;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

public class MedicationDatesAnnotator extends JCasAnnotator_ImplBase {

  public static final String annotatorName = MedicationDatesAnnotator.class.getSimpleName();

  // -----------------------------------------
  /**
   * process
   * 
   *   find medications and look to the right
   *   within the window for a dose form.
   *   
   *   The dose form may be within the medication 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
      this.performanceMeter.startCounter();
      List<Annotation> medications = UIMAUtil.getAnnotations(pJCas,Medication.typeIndexID);

      if (medications != null && !medications.isEmpty())
        for (Annotation medication : medications ) {
          findDates(pJCas, (Medication) medication);
          
        }

      this.performanceMeter.stopCounter();

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue in Medical Annotator " + e.toString());
    }

  } // end Method process() ----------------

  // =======================================================
  /**
   * findDates looks for dates around medications (to the right of 
   * 
   * @param pJCas
   * @param pMedication
   * 
   */
  // =======================================================
  private final void findDates(JCas pJCas, Medication pMedication) {

    if (pMedication != null ) {
      
      // find the line that the dose form is in
      List<Annotation> lines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Line.typeIndexID, pMedication.getBegin(), pMedication.getEnd());
      
      if ( lines != null && !lines.isEmpty()) 
        for ( Annotation line: lines )
          findDatesAux(pJCas, line, pMedication);
    }
     
  } // End Method findDoseForm() =====================

  // =================================================
  /**
   * findDoseFormAux looks for dose form terms in this line
   * 
   * @param pJCas
   * @param pLine
   */
  // =================================================
  private final void findDatesAux(JCas pJCas, Annotation pLine, Medication pMedication) {

    List<Annotation> dates = UIMAUtil.getAnnotationsBySpan(pJCas, AbsoluteDate.typeIndexID, pLine.getBegin(), pLine.getEnd());
    
    if ( dates != null && !dates.isEmpty())  {
      UIMAUtil.sortByOffset(dates);
      dates = UIMAUtil.uniqueAnnotations(dates);
    
      switch ( dates.size() ) {
        case 1:  classifyKindOfDate( pJCas, dates.get(0), pMedication, pLine);  break;
        case 2:  classifyKindOfDateAux( pJCas, dates.get(0), dates.get(1), pMedication, pLine ); break; 
      }
   
    }
  } // end Method findDatesAux() ---------------------
    
  // =================================================
  /**
   * classifyKindOfDate determines from the words around 
   * this date what kind of date it is
   * @param pJCas
   * @param aDate
   * @param pMedication
   * @param pLine
  */
  // =================================================
    private final void classifyKindOfDate(JCas pJCas, Annotation aDate, Medication pMedication, Annotation pLine) {
    
      
      // medication 10 mg take by mouth  started (xx/xx/xx)  stopped (xx/xx/xx)   <--- not handling here
      // medication 10 mg take by mouth   (xx/xx/xx) started  (xx/xx/xx) stopped  <--- not handling here
      // medication 10 mg take by mouth xx/xx/xx active
      // medication 10 mg take by mouth active - xx/xx/xx
      
      
      Annotation clinicalStatusEvidence = null;
      List<Annotation>clinicalStatuz = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  ClinicalStatusEvidence.typeIndexID, pMedication.getEnd(), pLine.getEnd() );

      if ( clinicalStatuz != null && !clinicalStatuz.isEmpty() )  {
        
        if ( clinicalStatuz.size() == 1 ) {
          clinicalStatusEvidence = clinicalStatuz.get(0);
          classifyKindOfDate( clinicalStatusEvidence, aDate, pMedication);
        } else {
          // I'm assuming the second clinical status is a clinical status not related to the date
          classifyKindOfDate( clinicalStatuz.get(0), aDate,  pMedication);
          clinicalStatusEvidence = clinicalStatuz.get( clinicalStatuz.size() -1) ;
        } 
    
      // set the clincial status
      pMedication.setStatus( clinicalStatusEvidence.getCoveredText());
      }
  } 
    
 // =================================================
    /**
     * classifyKindOfDate determines from the words around 
     * this date what kind of date it is
     * @param pJCas
     * @param firstDate
     * @param secondDate
     * @param pMedication
     * @param pLine
    */
    // =================================================
      private final void classifyKindOfDateAux(JCas pJCas, Annotation firstDate, Annotation secondDate, Medication pMedication, Annotation pLine) {
      
        
        // medication 10 mg take by mouth  started (xx/xx/xx)  stopped (xx/xx/xx)   <--- not handling here
       
        
       
        List<Annotation>clinicalStatuz = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  ClinicalStatusEvidence.typeIndexID, pMedication.getEnd(), pLine.getEnd() );

        if ( clinicalStatuz != null && !clinicalStatuz.isEmpty() )  {
          
          if ( clinicalStatuz.size() == 2 ) {
          
            classifyKindOfDate( clinicalStatuz.get(0), firstDate,  pMedication);
            classifyKindOfDate( clinicalStatuz.get(1), secondDate, pMedication);
          }
          else {
            // I'm assuming the second date is a statement date
            classifyKindOfDate( clinicalStatuz.get(0), firstDate,  pMedication);
          }
          
          // set the status
        pMedication.setStatus( clinicalStatuz.get(clinicalStatuz.size() -1).getCoveredText());
        }
        
    } 
        
    
    
  // =================================================
  /**
   * classifyKindOfDate 
   * 
   * @param clinicalStatus
   * @param aDate
   * @param pMedication
  */
  // =================================================
  private final void classifyKindOfDate(Annotation clinicalStatus, Annotation aDate, Medication pMedication ) {
   
    
    String aDateString = aDate.getCoveredText(); 
    
    String clinicalStatusWord  = clinicalStatus.getCoveredText();
    if ( clinicalStatusWord != null && clinicalStatusWord.trim().length() > 0 ) {
  
   switch ( clinicalStatusWord.toLowerCase().trim() ) {
     case "begin":
     case "start":
     case "started" :  pMedication.setStartedDate(  aDateString);  break;
     case "end":
     case "stopped":
     case "discontinue":
     case "discontinued":
     case "completed":
     case "stop":     pMedication.setStoppedDate( aDateString); break;
     case "ordered": 
     case "order": 
     case "prescribed":
     case "prescribe": pMedication.setPrescribedDate( aDateString); break;
     case "dispense" :
     case "dispensed": 
     case "filled" :
     case "fill":      pMedication.setFilledDate( aDateString); break;
     case "active":    pMedication.setStatementDate( aDateString); break;
    
     
   }
    
    }
  } // end Method classifyKindOfDate() ---------------

  // end Method findDoseFormAux() --------------------

  
 
  
 
  // ------------------------------------------------
  /**
   * destroy
   * 
   **/
  // ------------------------------------------------
  public void destroy() {
    try {
      this.performanceMeter.writeProfile(this.getClass().getSimpleName());
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, "Issue destroying Medication Annotator " + e.toString());

    }
  } // end Method Destroy() ---

  // ----------------------------------
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
      args = (String[]) aContext.getConfigParameterValue("args");

      initialize(args);

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, "Issue initializing AnnotomicalParts Annotator " + e.toString());
      throw new ResourceInitializationException();
    }

  } // end Method initialize() ----

  // ----------------------------------
  /**
   * initialize
   * 
   * @param aArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String pArgs[]) throws ResourceInitializationException {

    try {
      this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getSimpleName());
      String nonMedicationSectionsFile = U.getOption(pArgs,  "--NonMedicationSections=", "resources/com/ciitizen/framework/medications/nonMedicationSections.txt");
      
      
      loadMedicationSemanticTypez();
      loadNonMedicationSectionz( nonMedicationSectionsFile);
      
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, "Issue initializing Medication sections Annotator " + e.toString());
      throw new ResourceInitializationException();
    }

  } // end Method initialize() -----------------------

  // =================================================
  /**
   * loadMedicationSemanticTypez
   * 
  */
  // =================================================
   private void loadMedicationSemanticTypez() {
    
     this.medicationSemanticTypez = new HashSet<String>();
     for ( String aType : this.medicationSemanticTypes )
       this.medicationSemanticTypez.add( aType);
    
     
  } // End Method load MedicationSemanticTypez() ------

   
// =================================================
  /**
   * loadNonMedicationSectionz loads non medication sections
   * into a hash - with a lowercase key - 
   * 
   * @param pNonMedicationSectionsFile
   * @throws Exception 
   * 
  */
  // =================================================
   private final void loadNonMedicationSectionz(String pNonMedicationSectionsFile ) throws Exception {
    
     this.nonMedicationSectionz = new HashSet<String>();
     
     String[] sectionNames;
    try {
      sectionNames = U.readClassPathResourceIntoStringArray(pNonMedicationSectionsFile);
   
      for ( String aSection : sectionNames ) {
        if ( aSection  != null && !aSection.trim().isEmpty() && !aSection.startsWith("#"))
          this.nonMedicationSectionz.add( aSection.toLowerCase());
      }
    
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with loading Non Medication sections from " + pNonMedicationSectionsFile  + " " + e.getMessage(); 
      GLog.println(GLog.ERROR_LEVEL, msg);
      throw new Exception (msg);
    }
     
     for ( String aSection : sectionNames )
       this.nonMedicationSectionz.add( aSection );
    
     
  } // End Method load MedicationSemanticTypez() ------

  // ---------------------------
  // Global Variables
  // ---------------------------

  private int             annotationCounter = 0;
  private String[]         medicationSemanticTypes = { "orch", "strd", "opco", "aapp", "phsu", "bacs", "enzy", "antb", "clnd" };
  private HashSet<String>  medicationSemanticTypez = null; 
  private HashSet<String>  nonMedicationSectionz = null;
  ProfilePerformanceMeter performanceMeter  = null;
 

} // end Class ExampleAnnotator() ---------------
