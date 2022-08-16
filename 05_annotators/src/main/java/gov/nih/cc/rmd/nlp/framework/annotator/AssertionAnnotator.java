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
 * AssertionAnnotator marks assertion status, conditional, and experiencer
 * for concepts, based on conTEXT . Assumes that the AssertionEvidenceAnnotator has
 * already been called in the pipeline.
 *
 *
 * @author Guy Divita
 * @created Sept 23 2015
 *
 **          *
 *          *
 *          *
 *          *
 *          *
 *          *
 *          *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.AssertionEvidence;
import gov.va.vinci.model.Concept;

public class AssertionAnnotator extends JCasAnnotator_ImplBase {

  public static final String EvidenceLRAGRFiles =

            "resources/vinciNLPFramework/assertion/assertedEvidence.lragr" + ":"
          + "resources/vinciNLPFramework/assertion/attributionEvidence.lragr"    +  ":" 
          + "resources/vinciNLPFramework/assertion/conditionalEvidence.lragr" + ":"
          + "resources/vinciNLPFramework/assertion/otherEvidence.lragr" + ":"
          + "resources/vinciNLPFramework/assertion/historicalEvidence.lragr" + ":"
          + "resources/vinciNLPFramework/assertion/negationEvidence.lragr" + ":"
          + "resources/vinciNLPFramework/assertion/familyHistoryEvidence.lragr" + ":"
          + "resources/vinciNLPFramework/assertion/subjectIsEvidence.lragr";

  public static final String labels             = "AssertedEvidence:ConditionalEvidence:HistoricalEvidence:NegationEvidence:SubjectIsPatientEvidence:SubjectIsOtherEvidence:FamilyHistoryEvidence:AttributionIsPatientEvidence";

  // =======================================================
  /**
   * process
   * 
   * @param pJCas
   * @throws AnalysisEngineProcessException
   *
   */
  // =======================================================
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
      this.performanceMeter.startCounter();

      try {
        // --------------------------------------------------------------
        // create Negation, Assertion, Historical, Subject Annotations
        List<Annotation> allEvidences = UIMAUtil.getAnnotations(pJCas, AssertionEvidence.typeIndexID, true);

        if (allEvidences != null && !allEvidences.isEmpty())
          processEvidence(pJCas, allEvidences);

      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue with the assertion annotator " + e.toString());
        this.performanceMeter.startCounter();
        return;
        // throw new AnalysisEngineProcessException();
      }

      // ----------------------------------------------------------------
      // Process slot Value components - look in the dependent Content part
      // for evidence - propagate negative evidence
      // to status of dependent content
      // Propagate dependent content to content annotations
      //
      // Make sure + and x are added to assertion lexicon
      processSlotValueAssertions(pJCas);
      
      // --------------------------------------
      // Annotations that are not around evidence are being left null for 
      // the assertion status.
      // Loop through focus annotations to make sure they get
      // a status if the assertion status is null
      processAllNullAssertionAnnotations( pJCas);

      this.performanceMeter.stopCounter();

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with the assertion annotator " + e.toString());
      // throw new AnalysisEngineProcessException();
    }
  } // end Method process() ----------------

  // =================================================
  /**
   * processAllNullAssertionAnnotations Annotations that are not 
   * around evidence are being left null for the assertion status.
   * Loop through focus annotations to make sure they get
   * a status if the assertion status is null
   * 
   * @param pJCas
  */
  // =================================================
  private void processAllNullAssertionAnnotations(JCas pJCas) {
   
    List<Annotation> allConceptAnnotations = UIMAUtil.getAnnotations(pJCas, Concept.typeIndexID, true );
    
    List<Annotation> allVAnnotations = UIMAUtil.getAnnotations(pJCas, VAnnotation.typeIndexID, true );
    
    if ( allConceptAnnotations != null && !allConceptAnnotations.isEmpty() )
      for ( Annotation anAnnotation : allConceptAnnotations )
        if ( ((Concept) anAnnotation).getAssertionStatus() == null )
          ((Concept) anAnnotation).setAssertionStatus("Asserted");
    
    
    if ( allVAnnotations != null && !allVAnnotations.isEmpty() )
      for ( Annotation anAnnotation : allVAnnotations )
        if ( ((VAnnotation) anAnnotation).getNegation_Status() == null )
          ((VAnnotation) anAnnotation).setNegation_Status("Asserted");
    
  } // end Method processAllNullAssertionAnnotations() -----
  

  // =======================================================
  /**
   * processSlotValueAssertions - look in the dependent Content part
   * for evidence.
   * Consider null dependentContent evidence
   * to be negative evidence.
   * 
   * If DependentContent has concepts in it, but
   * no assertion evidence, assert it.
   * 
   * Propagate dependent content to content annotations
   * 
   * Make sure + and x are added to assertion lexicon
   * 
   * @param pJCas
   */
  // =======================================================
  private void processSlotValueAssertions(JCas pJCas) {

    List<Annotation> slotValues = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID);

    if (slotValues != null && !slotValues.isEmpty()) {
      for (Annotation slotValue : slotValues) {

        ContentHeading question = ((SlotValue) slotValue).getHeading();
        DependentContent answer = ((SlotValue) slotValue).getDependentContent();

        String assertionStatus = "Negated";
        boolean conditionalStatus = false;
        String subjectStatus = "Patient";

        if (answer != null && answer.getEnd() > answer.getBegin()) {
          try {
            answer.getCoveredText();
          } catch (Exception e) {
            e.printStackTrace();

            System.err.println("issue with an empty answer " + e.toString());
          }
          if (!answerIsEmpty(answer.getCoveredText())) {
            if ((answerHasEvidence(pJCas, answer))
                || (answer.getNegation_Status() != null && answer.getNegation_Status().equals("Asserted"))) {
              assertionStatus = answer.getNegation_Status();
              conditionalStatus = answer.getConditional();
              subjectStatus = answer.getSubject();
            } else { // there is no negative evidence to assert it
              assertionStatus = "Asserted";
              conditionalStatus = false;
              subjectStatus = "Patient";
            }
          } else { // answer is empty
            assertionStatus = "Negated";
            conditionalStatus = false;
            subjectStatus = "Patient";
          }
        } else { // answer is null - assume Negated
          assertionStatus = "Negated";
          conditionalStatus = false;
          subjectStatus = "Patient";
        }

        markAnnotations(pJCas, slotValue, assertionStatus);
        if (question != null) {
          question.setNegation_Status(assertionStatus);
          question.setConditional(conditionalStatus);
          question.setSubject(subjectStatus);
        }
        if (answer != null) {
          answer.setNegation_Status(assertionStatus);
          answer.setConditional(conditionalStatus);
          answer.setSubject(subjectStatus);
        }
      }
    }

  } // End Method processSlotValueAssertions() ======================

  // =======================================================
  /**
   * markAnnotations marks terms and concepts that fall under the span of this slotValue with the assertion properties
   * 
   * @param pJCas
   * @param slotValue
   * @param assertionStatus
   *
   */
  // =======================================================
  private void markAnnotations(JCas pJCas, Annotation slotValue, String assertionStatus) {

    List<Annotation> annotations = UIMAUtil.getAnnotationsBySpan(pJCas, slotValue.getBegin(), slotValue.getEnd());

    if (annotations != null && !annotations.isEmpty()) {
      for (Annotation annotation : annotations) {
        if (!annotation.getClass().getSimpleName().contains("Evidence")) {
          try {
            ((Concept) annotation).setAssertionStatus(assertionStatus);

          } catch (Exception e) {
            try {
              ((VAnnotation) annotation).setNegation_Status(assertionStatus);

            } catch (Exception e2) {
            }
            ;
          }
        }
      }
    }

  } // End Method markAnnotations() ======================

  // =======================================================
  /**
   * answerHasEvidence returns true if the answer has any assertion
   * evidence
   * 
   * @param pJCas
   * @param pAnswer
   * @return boolean
   */
  // =======================================================
  private boolean answerHasEvidence(JCas pJCas, DependentContent pAnswer) {
    boolean returnVal = false;

    Type assertionEvidenceType = pJCas.getTypeSystem().getType("gov.va.vinci.model.AssertionEvidence");
    List<Annotation> evidences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, assertionEvidenceType, AssertionEvidence.typeIndexID,
        pAnswer.getBegin(), pAnswer.getEnd(), true);

    if (evidences != null) returnVal = true;

    return returnVal;

  } // End Method answerHasEvidence() ========================

  // =======================================================
  /**
   * answerIsEmpty [Summary here]
   * 
   * @param coveredText
   * @return
   */
  // =======================================================
  private boolean answerIsEmpty(String coveredText) {
    boolean returnVal = false;

    String buff = coveredText;
    if (buff != null) {
      buff = buff.replace('_', ' ');
      if (buff.trim().length() == 0) returnVal = true;
      else returnVal = false;
    } else {
      returnVal = true;
    }

    return returnVal;
    // End Method answerIsEmpty() ======================
  }

  // =======================================================
  /**
   * processEvidences
   * 
   * @param pJCas
   * @param pAllEvidences
   *
   */
  // =======================================================
  private void processEvidence(JCas pJCas, List<Annotation> pAllEvidences) {

  //  List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Utterance.typeIndexID, true);
    
    // limit the scope of what is seen to phrases for negation/assertion
    List<Annotation> largePhrases = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID, false);
    
    
    if ( largePhrases != null && !largePhrases.isEmpty()) {
      largePhrases = UIMAUtil.uniqueAnnotations(largePhrases);
    }
    
    // ------------------------------
    // find a trigger point
    // Make an annotation out of it

    Annotation lastTerminSeen = null;
    for (Annotation someEvidence : pAllEvidences) {
      
     

      String triggerTypes = ((AssertionEvidence) someEvidence).getTriggerType();
      if (triggerTypes == null) {
        System.err.println("issue with empty trigger type for " + someEvidence.getCoveredText());
        // System.exit(1);
      } else {
        if (triggerTypes.trim().endsWith("|")) 
          triggerTypes = triggerTypes.substring(0, triggerTypes.lastIndexOf("|"));

        triggerTypes = triggerTypes.replace("null", "");
        triggerTypes = triggerTypes.replace(":", "|");
        triggerTypes = triggerTypes.replace("||", "|");
        if (triggerTypes.startsWith("|")) triggerTypes = triggerTypes.substring(1);

        String[] triggerTypez = U.split(triggerTypes);
        // There could be more than one trigger type TBA
        // ----------------------------
        // switch on trigger type

        for (String triggerType : triggerTypez) {

          if (triggerType.contains("post")) {
            markAnnotationsBefore(pJCas, someEvidence, lastTerminSeen, largePhrases);
          } else if (triggerType.contains("pre")) {
            markAnnotationsAfter(pJCas, someEvidence, largePhrases);
          } else if (triggerType.contains("termin")) {
            lastTerminSeen = someEvidence;
          }
          // else if (triggerTypes.contains("pseudo")) { ; };

          markDependentContent(pJCas, someEvidence, largePhrases);
        }
      }
    } // end loop thru annotations

  } // End Method processNegation() ======================

  // =======================================================
  /**
   * markDependentContent [Summary here]
   * 
   * @param pJCas
   * @param someEvidence
   * @param pSentences
   */
  // =======================================================
  private void markDependentContent(JCas pJCas, Annotation someEvidence, List<Annotation> pSentences) {

    // -----------------------------------
    // find the sentence for this evidence
    List<Annotation> someSentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pSentences, someEvidence.getBegin(),
        someEvidence.getEnd());

    if (someSentences != null) {
      for (Annotation aSentence : someSentences) {

        if (aSentence.getClass().getSimpleName().contains("DependentContent")) {

          if (someEvidence.getClass().getSimpleName().contains("NegationEvidence"))
            ((DependentContent) aSentence).setNegation_Status("Negated");
          else if (someEvidence.getClass().getSimpleName().contains("AssertedEvidence"))
            ((DependentContent) aSentence).setNegation_Status("Asserted");

          if (someEvidence.getClass().getSimpleName().contains("ConditionalEvidence"))
            ((DependentContent) aSentence).setConditional(true);
          if (someEvidence.getClass().getSimpleName().contains("HistoricalEvidence")) {
            try {
              ((DependentContent) aSentence).setHistorical(true);
            } catch (Exception e) {
              System.err.println("Can't set historical to a dependentContent ");
            }
          }

          if (someEvidence.getClass().getSimpleName().contains("SubjectIsPatientEvidence"))
            ((DependentContent) aSentence).setSubject("Patient");
          else if (someEvidence.getClass().getSimpleName().contains("SubjectIsOtherEvidence"))
            ((DependentContent) aSentence).setSubject("Other");
          else if (someEvidence.getClass().getSimpleName().contains("FamilyHistoryEvidence"))
            ((DependentContent) aSentence).setSubject("Other");

          
          
        }

      } // end loop thru sentences

    } // end if there is a found sentence
  } // End Method markDependentContent() ======================

  // =======================================================
  /**
   * markAnnotationsBefore marks all annotations from the beginning
   * of the sentence or from the last termin seen as this kind of evidence.
   * 
   * @param pJCas
   * @param someEvidence
   * @param lastTerminSeen
   * @param pSentences
   */
  // =======================================================
  private void markAnnotationsBefore(JCas pJCas, Annotation someEvidence, Annotation lastTerminSeen,
      List<Annotation> pSentences) {

    // -----------------------------------
    // find the sentence for this evidence
    List<Annotation> someSentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pSentences, someEvidence.getBegin(),
        someEvidence.getEnd());

    if (someSentences != null) {
      for (Annotation aSentence : someSentences) {
        int beginMarker = aSentence.getBegin();
        if (lastTerminSeen != null && lastTerminSeen.getBegin() > beginMarker) beginMarker = lastTerminSeen.getBegin();
        markAnnotations(pJCas, someEvidence, beginMarker, someEvidence.getBegin());
      } // end loop thru sentences

    } // end if there is a found sentence
  } // end Method markAnnotationsBefore() ==================

  // =======================================================
  /**
   * markAnnotationsAfter marks all annotations from the trigger
   * of the sentence to the end of the sentence or the first hit termin
   * 
   * @param pJCas
   * @param someEvidence
   * @param lastTerminSeen
   * @param pSentences
   */
  // =======================================================
  private void markAnnotationsAfter(JCas pJCas, Annotation someEvidence, List<Annotation> pSentences) {

    // -----------------------------------
    // find the sentence for this evidence
  //  List<Annotation> someSentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pSentences, someEvidence.getBegin(), someEvidence.getEnd());
    List<Annotation> someSentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Sentence.typeIndexID, someEvidence.getBegin(), someEvidence.getEnd());
        
    // --------------------
    // For the purposes of negation scope, if the sentence ends with a colon, the
    // negation/assertion trigger on the left side of the colon asserts/denies concepts
    // on the right side, if this is a sentence

    someSentences = addASentenceIfSentenceEndsWithColon(pJCas, pSentences, someSentences);

    String evidenceType = getEvidenceType(someEvidence);
    if (someSentences != null) {
      for (Annotation aSentence : someSentences) {

        int beginMarker = someEvidence.getEnd() + 1;
        int endMarker = aSentence.getEnd();
        Annotation terminal = getNextTerminal(pJCas, evidenceType, beginMarker, endMarker);
        if (terminal != null) endMarker = terminal.getBegin();

        markAnnotations(pJCas, someEvidence, beginMarker, endMarker);
      } // end loop thru sentences

    } // end if there is a found sentence
  } // end Method markAnnotationsBefore() ==================

  private List<Annotation> addASentenceIfSentenceEndsWithColon(JCas pJCas, List<Annotation> pSentences,
      List<Annotation> someSentences) {
    ArrayList<Annotation> moreSentences = new ArrayList<Annotation>();

    moreSentences.addAll(someSentences);

    if (someSentences != null && !someSentences.isEmpty()) {
      Annotation sentence = someSentences.get(0);

      String buff = sentence.getCoveredText().toLowerCase();

      if (buff != null && buff.trim().endsWith(":")) if (buff.contains("these") || buff.contains("following")) {
        List<Annotation> nextSentence = UIMAUtil.fuzzyFindAnnotationsBySpan(pSentences, sentence.getEnd() + 1,
            sentence.getEnd() + 2);

        if (nextSentence != null && !nextSentence.isEmpty()) moreSentences.addAll(nextSentence);
      }

    }

    return moreSentences;
  } // End Method addASentenceIfSentenceEndsWithColon() ======================

  // =======================================================
  /**
   * getNextTerminal [Summary here]
   * 
   * @param pJCas
   * @param pEvidenceType
   * @param beginMarker
   * @param endMarker
   * @return
   */
  // =======================================================
  private Annotation getNextTerminal(JCas pJCas, String pEvidenceType, int beginMarker, int endMarker) {

    Annotation returnVal = null;
    // -----------------------------------
    // find the sentence for this evidence
    List<Annotation> remainingEvidence = UIMAUtil.getAnnotationsBySpan(pJCas, AssertionEvidence.typeIndexID, beginMarker,
        endMarker);

    if (remainingEvidence != null) for (Annotation someEvidence : remainingEvidence) {
      if (((AssertionEvidence) someEvidence).getTriggerType().equals("termin"))
        if (getEvidenceType(someEvidence).equals(pEvidenceType)) {
        returnVal = someEvidence;
        break;
        }
    } // end loop thru the remaining evidence

    return returnVal;
  } // End Method getNextTerminal() ======================

  // =======================================================
  /**
   * getEvidenceType returns the evidence type
   * 
   * @param pSomeEvidence
   * @return String
   */
  // =======================================================
  private String getEvidenceType(Annotation pSomeEvidence) {
    String returnVal = null;
    if (pSomeEvidence != null) returnVal = pSomeEvidence.getClass().getSimpleName();
    return returnVal;
  } // End Method getEvidenceType() ======================

  // =======================================================
  /**
   * markAnnotations marks the annotations between the begin and end with
   * attributes based on the evidence.
   * 
   * @param pJCas
   * @param someEvidence
   * @param pBegin
   * @param pEnd
   */
  // =======================================================
  private void markAnnotations(JCas pJCas, Annotation someEvidence, int pBegin, int pEnd) {

    // -----------------------
    // Get all the annotations for this sentence
    List<Annotation> someAnnotations = UIMAUtil.getAnnotationsBySpan(pJCas, pBegin, pEnd);
    if (someAnnotations != null && !someAnnotations.isEmpty()) 
      for (Annotation anAnnotation : someAnnotations) {
        markAnnotation(anAnnotation, someEvidence);
    } // end loop thru the annotations of

  } // End Method markAnnotationsBefore() ======================

  // =======================================================
  /**
   * markAnnotation updates the assertion attributes from the evidence found
   * 
   * N.B. At this point we could create relationships between the evidence
   * and the concept
   * 
   * N.B.B. there are two attribute types from different kinds
   * of annotations. Rather than guess, this tries them
   * all. If one fails, it tries the others.
   * 
   * @param anAnnotation
   * @param someEvidence
   */
  // =======================================================
  private void markAnnotation(Annotation anAnnotation, Annotation someEvidence) {

    String annotationType = anAnnotation.getClass().getSimpleName();
    if (annotationType.contains("Line") || annotationType.contains("NegationEvidence")
        || annotationType.contains("AssertedEvidence") || annotationType.contains("CEMHeader")
        || annotationType.contains("Section")

    ) return;
    String evidenceKind = someEvidence.getClass().getSimpleName();
    
    markExperiencerAttribute(anAnnotation, "Patient", null);  //<------ this should be the default

    switch (evidenceKind) {
      case "NegationEvidence":
        markNegationAttribute(anAnnotation, "Negated", someEvidence);
        break;
      case "NoEvidence":
        markNegationAttribute(anAnnotation, "Negated", someEvidence);
        break;
      case "AssertionEvidence":
        markNegationAttribute(anAnnotation, "Asserted", someEvidence);
        break;
      case "ConditionalEvidence":
        markConditionalAttribute(anAnnotation, someEvidence);
        break;
      case "HistoricalEvidence":
        markHistoricalAttribute(anAnnotation, someEvidence);
        break;
      case "SubjectIsPatientEvidence":
        markExperiencerAttribute(anAnnotation, "Patient", someEvidence);
        break;
      case "SubjectIsOtherEvidence":
        markExperiencerAttribute(anAnnotation, "Other", someEvidence);
        break;
      case "FamilyHistoryEvidence":
        markExperiencerAttribute(anAnnotation, "Other", someEvidence);
        break;
      case "AttributionIsPatientEvidence":
        markAttributionAttribute(anAnnotation,  someEvidence);
        break;
        
      default:
        markNegationAttribute(anAnnotation, "Asserted", someEvidence);
        break;
    } // end switch

    // End Method markAnnotation() ======================

  }

  // =======================================================
  /**
   * markAttribute marks this attribute
   * 
   * @param anAnnotation
   * @param someEvidence
   */
  // =======================================================
  private void markNegationAttribute(Annotation anAnnotation, String pAttribute, Annotation someEvidence) {

    try {
    
      String oldAssertionStatus = ((Concept) anAnnotation).getAssertionStatus();
      if (oldAssertionStatus != null && oldAssertionStatus.contains("Negated")) {
        ;
      } else {
        ((Concept) anAnnotation).setAssertionStatus(pAttribute);
      }

    } catch (Exception e) {
      try {
        String oldAssertionStatus = ((VAnnotation) anAnnotation).getNegation_Status();
        if (oldAssertionStatus != null && oldAssertionStatus.contains("Negated")) {
          ;
        } else {
          ((VAnnotation) anAnnotation).setNegation_Status(pAttribute);
          ((VAnnotation) anAnnotation).setAssertionPredicate(someEvidence);
        }
      } catch (Exception e2) {
        // these are things we don't care about
        // e2.printStackTrace();
        // System.err.println("really?");
      }
      ;
    }
  } // End Method markNegationAttribute() ======================

  // =======================================================
  /**
   * markConditionalAttribute marks this attribute
   * 
   * @param anAnnotation
   * @param someEvidence
   */
  // =======================================================
  private void markConditionalAttribute(Annotation anAnnotation, Annotation someEvidence) {

    try {
      ((Concept) anAnnotation).setConditionalStatus(true);

    } catch (Exception e) {
      try {
        ((VAnnotation) anAnnotation).setConditional(true);
        ((VAnnotation) anAnnotation).setAssertionPredicate(someEvidence);
      } catch (Exception e2) {
      }
      ;
    }
  } // End Method markConditionalAttribute() ======================

  // =======================================================
  /**
   * markHistoricalAttribute marks this attribute
   * 
   * @param anAnnotation
   * @param someEvidence
   */
  // =======================================================
  private void markHistoricalAttribute(Annotation anAnnotation, Annotation someEvidence) {

    try {
      ((Concept) anAnnotation).setHistoricalStatus(true );

    } catch (Exception e) {
      try {
       //  ((VAnnotation) anAnnotation).setNegation_Status("Historical");
        ((VAnnotation) anAnnotation).setHistorical(true );
        ((VAnnotation) anAnnotation).setAssertionPredicate(someEvidence);
      } catch (Exception e2) {
      }
      ;
    }
  } // End Method markHistoricalAttribute() ======================

  // =======================================================
  /**
   * markExperiencerAttribute marks this attribute
   * 
   * @param anAnnotation
   * @param pAttribute
   *          (Patient|Other)
   * @param someEvidence
   */
  // =======================================================
  private void markExperiencerAttribute(Annotation anAnnotation, String pAttribute, Annotation someEvidence) {

    try {
      ((Concept) anAnnotation).setSubjectStatus(pAttribute);
    } catch (Exception e) {
      try {
        ((VAnnotation) anAnnotation).setSubject(pAttribute);
        ((VAnnotation) anAnnotation).setAssertionPredicate(someEvidence);
      } catch (Exception e2) {
      }
      ;
    }
  } // End Method markExperiencerAttribute() ======================
  
//=======================================================
 /**
  * markAttributionAttribute marks this attribute as true - meaning
  * that the patient is who is attributed to saying this mention
  * 
  * @param anAnnotation
  * 
  * @param someEvidence
  */
 // =======================================================
 private void markAttributionAttribute(Annotation anAnnotation,  Annotation someEvidence) {

   try {
     ((Concept) anAnnotation).setAttributedToPatient(true);
   } catch (Exception e) {
     try {
       ((VAnnotation) anAnnotation).setAttributedToPatient( true);
    
     } catch (Exception e2) {
     }
     ;
   }
 } // End Method markExperiencerAttribute() ======================


  // =======================================================
  /**
   * getDependentContentAssertionStatusObs returns either "Asserted" or "Negated"
   * if the dependent Content is null, Negated is returned.
   * This method comes from the SlotValue process. Use the newer version. This
   * is here for historical comparison of code.
   * 
   * @deprecated
   * @param pDependentContent
   * @return String
   */
  // =======================================================
  public static String getDependentContentAssertionStatus(DependentContent dependentContent) {

    String returnValue = "Negated";
    dependentContent.setProcessMe(false);
    dependentContent.setNegation_Status("Negated");
    String content = dependentContent.getCoveredText();

    if (content != null) {

      char contentArray[] = content.toLowerCase().toCharArray();
      int xd = content.toLowerCase().indexOf("x"); // <---- too simplistic catching anxiety - it's looking for [x] or (x) or _x_
      if (xd > 0 && contentArray[xd - 1] >= 'a' && contentArray[xd - 1] <= 'z') xd = -1;
      if (xd >= 0 && xd < content.length() - 1 && contentArray[xd + 1] >= 'a' && contentArray[xd + 1] <= 'z') xd = -1;

      int pd = content.toLowerCase().indexOf("+");
      if (xd < 0 && pd > -1) xd = pd;

      // ---------------------------------------
      // is x'd yes question or yes/no question
      //
      if (content.toLowerCase().contains("yes") || content.toLowerCase().contains("no")
          || content.toLowerCase().matches("\\by\\b") || content.toLowerCase().matches("\\bn\\b")) {
        // -------------------------
        // if Box'ed or X'd answer

        int yesd = content.toLowerCase().indexOf("yes");
        int nod = content.toLowerCase().indexOf("no");
        int yd = content.toLowerCase().indexOf("y");
        int nd = content.toLowerCase().indexOf("n");

        // -----------------------------------
        // Looking for x close to yes or no
        if (xd > -1 && content.toLowerCase().indexOf("explain") < 0) {
          if ((Math.abs(yesd - xd) < Math.abs(nod - xd)) || (Math.abs(yd - xd) < Math.abs(nd - xd))) {
            dependentContent.setProcessMe(true);
            dependentContent.setNegation_Status("Asserted");
          }

        } else if ((content.toLowerCase().contains("yes") && !content.toLowerCase().contains("no"))
            && !(content.toLowerCase().contains("none")) && !(content.toLowerCase().contains("denies"))
            && !(content.toLowerCase().contains("denied"))
            && (content.toLowerCase().contains("y") && !content.toLowerCase().contains("n"))) {
          // (!content.toLowerCase().contains("no") && !content.toLowerCase().contains("n"))) {

          dependentContent.setProcessMe(true);
          dependentContent.setNegation_Status("Asserted");
        }
      } else if (xd > 0) { // ---------------------------------------
        // checked content

        dependentContent.setProcessMe(true);
        dependentContent.setNegation_Status("Asserted");

      } else if (contentContainsNonZeroNumber(content)) {

        dependentContent.setProcessMe(true);
        dependentContent.setNegation_Status("Asserted");

      } else if (contentContainsShortAnswer(content)) {

        dependentContent.setProcessMe(true);
        dependentContent.setNegation_Status("Asserted");

      } else if (xd < 0 && content != null) { // not checked && content.length() > 20 ) {

        // dependentContent.setProcessMe(true);
        // dependentContent.setNegation_Status("Asserted");

      }

      returnValue = dependentContent.getNegation_Status();

    }
    return returnValue;
  } // End Method getDependentContentAssertionStatus() ======================

  // =======================================================
  /**
   * contentContainsNonZeroNumber assume that the content is asserted if
   * there is a non-zero number in the content
   * 
   * @param content
   * @return
   */
  // =======================================================
  private static boolean contentContainsNonZeroNumber(String pContent) {

    boolean returnVal = false;

    if (pContent != null && pContent.length() > 0) {
      char[] contentArray = pContent.toCharArray();

      for (int i = 0; i < contentArray.length; i++) {
        if (U.isNumber(contentArray[i]) && contentArray[i] > '0') {
          returnVal = true;
          break;

        }
      }
    }

    return returnVal;
  } // End Method contentContainsNonZeroNumber() ======================

  // =======================================================
  /**
   * contentContainsShortAnswer assume that the content is asserted if
   * it's tokens that are characters that were not caught above
   * for negated terms.
   * 
   * @param content
   * @return
   */
  // =======================================================
  private static boolean contentContainsShortAnswer(String pContent) {

    boolean returnVal = false;

    if (pContent != null && pContent.length() > 0) {
      char[] contentArray = pContent.toCharArray();

      for (int i = 0; i < contentArray.length; i++) {
        if (contentArray[i] >= 'A' && contentArray[i] <= 'z') {
          returnVal = true;

        }
      }
    }

    return returnVal;
  } // End Method contentContainsNonZeroNumber() ======================

  // ----------------------------------
  /**
   * destroy
   * 
   **/
  // ----------------------------------
  public void destroy() {
    this.performanceMeter.writeProfile(this.getClass().getSimpleName());
  } // end Method destroy() ------------------

  // -----------------------------------------
  /**
   * initialize loads in the resources.
   * Put pipeline parameters in the args parameter
   * to be retrieved here.
   * 
   * Pass in on your args, an arg with the value: "--outputDir=/some/path/to/put/outputFiles"
   *
   * This class is tooled up to have performance statisitcs get put into $outputDir/logs/Class.log
   *
   * @param aContext
   *
   */
  // -----------------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {

    String args[] = null;

    try {
      args = (String[]) aContext.getConfigParameterValue("args");

      initialize(args);

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with getting the passed in arguments " + e.toString());
      throw new ResourceInitializationException();

    }

  } // end Method initialize() -----------

  // -----------------------------------------
  /**
   * initialize loads in the resources.
   * Put pipeline parameters in the args parameter
   * to be retrieved here.
   * 
   * Pass in on your args, an arg with the value: "--outputDir=/some/path/to/put/outputFiles"
   *
   * This class is tooled up to have performance statisitcs get put into $outputDir/logs/Class.log
   *
   * @param pArgs
   *
   */
  // -----------------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {

    try {

      this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getSimpleName());

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with getting the passed in arguments " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, msg);
      throw new ResourceInitializationException();

    }

  } // end Method initialize() -----------

  // ---------------------------
  // Global Variables
  // ---------------------------
  ProfilePerformanceMeter performanceMeter = null;

} // end Class ExampleAnnotator() ---------------
