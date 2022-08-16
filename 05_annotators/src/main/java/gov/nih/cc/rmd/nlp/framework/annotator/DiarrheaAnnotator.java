// =================================================
/**
 * DiarrheaAnnotator finds diarrhea mentions
 * 
 * #   Diarrhea = DiarrheaDirectMention 
 * #
 * #   DiarrheaEvidence =              
 * #              [ DiarrheaAnatomy + 
 * #              [ DiarrheaFrequency               -+ 
 * #                DiarrheaSeverity |               |   One or more of these
 * #                DiarrheaColor |                  |
 * #                DiarrheaConsistancy |            |
 * #                DiarrheaSensation |              |
 * #                DiarrheaPathologicalModifier  |  |
 * #                DiarrheaSymptoms ]              -+
 * #   DiarrheaEvidence =              
 * #              [ NormalDefication + 
 * #              [ DiarrheaFrequency               -+ 
 * #                DiarrheaSeverity |               |   One or more of these
 * #                DiarrheaColor |                  |
 * #                DiarrheaConsistancy |            |
 * #                DiarrheaSensation |              |
 * #                DiarrheaPathologicalModifiers |  |
 * #                DiarrheaSymptoms ]              -+
 * #   DiarrheaEvidence =              
 * #                DiarrheaLabTest
 

 * @author  Guy Divita 
 * @created July 12, 2016
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.Utterance;
import gov.va.vinci.model.Concept;
import gov.va.vinci.model.ConditionalEvidence;
import gov.va.vinci.model.DiarrheaAnatomy;
import gov.va.vinci.model.DiarrheaCessation;
import gov.va.vinci.model.DiarrheaColor;
import gov.va.vinci.model.DiarrheaConsistancy;
import gov.va.vinci.model.DiarrheaDirectMention;
import gov.va.vinci.model.DiarrheaEvidence;
import gov.va.vinci.model.DiarrheaFrequency;
import gov.va.vinci.model.DiarrheaLabTest;
import gov.va.vinci.model.DiarrheaMention;
import gov.va.vinci.model.DiarrheaPathologicalModifier;
import gov.va.vinci.model.DiarrheaPertinantNegative;
import gov.va.vinci.model.DiarrheaSensation;
import gov.va.vinci.model.DiarrheaSeverity;
import gov.va.vinci.model.DiarrheaSymptoms;
import gov.va.vinci.model.Duration;
import gov.va.vinci.model.FamilyHistoryEvidence;
import gov.va.vinci.model.Fever;
import gov.va.vinci.model.HistoricalEvidence;
import gov.va.vinci.model.NegationEvidence;
import gov.va.vinci.model.NormalDefication;
import gov.va.vinci.model.SubjectIsOtherEvidence;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class DiarrheaAnnotator extends JCasAnnotator_ImplBase {
 
 
  
  // -----------------------------------------
  /**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    this.performanceMeter.startCounter();
    
    createEvidence( pJCas);
  
    processEvidence( pJCas);
   
    this.performanceMeter.stopCounter();

  } // end Method process() ----------------
   

// ==========================================
  /**
   * processEvidence 
   *
   * @param pJCas
   */
  // ==========================================
  private void processEvidence(JCas pJCas) {
   
    // Iterate thru the utterances
    List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Utterance.typeIndexID );
    
    if ( sentences != null && !sentences.isEmpty()) {
      for ( Annotation sentence: sentences )
        processSentence(pJCas, sentence);
    }
    
  }  // end Method processEvidence() ===========
  
  

//==========================================
 /**
  * processSection  will process the evidence one sentence at a time from the given section.
  *
  * @param pJCas
  * @param pSection
  */
 // ==========================================
 protected void processSection(JCas pJCas, Annotation pSection ) {
  
   // Iterate thru the utterances
   List<Annotation> sentences = UIMAUtil.getAnnotationsBySpan(pJCas, Utterance.typeIndexID, pSection.getBegin(), pSection.getEnd() );
   
   if ( sentences != null && !sentences.isEmpty()) {
     for ( Annotation sentence: sentences )
       processSentence(pJCas, sentence);
   }
   
 }  // end Method processSnippet() ===========
 


// ==========================================
/**
 * processDiarrheaEvidence (This assumes that the fever annotator has already run)
 *
 * @param pJCas
 * @param pSentence
 */
// ==========================================
@SuppressWarnings("unchecked")
private void processSentence(JCas pJCas,  Annotation pSentence) {
  
  List<Annotation> directMentions = null;
  List<Annotation> frequencies = null;
  List<Annotation> severities = null;
  List<Annotation> colors = null;
  List<Annotation> sensations = null;
  List<Annotation> consistancies = null;
  List<Annotation> modifiers = null;
  List<Annotation> symptoms = null;
  List<Annotation> fever = null;
  List<Annotation> labTests = null;
  List<Annotation> negatives = null;
  List<Annotation> anatomies = null;
  List<Annotation> defications = null;
  List<Annotation> durations = null;
  List<Annotation> ceased = null;
  List<Annotation> negativeEvidence = null;
  List<Annotation> diarrheaCeased = null;
  List<Annotation> negativeConditional = null;
  List<Annotation> conditionalEvidence = null;
  List<Annotation> historicalEvidence = null;
  List<Annotation> otherNegativeEvidence = null;
  List<Annotation> subjectEvidence = null;
  List<Annotation> familyHistoryEvidence = null;
  
  
  try {
    directMentions = UIMAUtil.getAnnotationsBySpan(pJCas,DiarrheaDirectMention.typeIndexID,        pSentence.getBegin(), pSentence.getEnd() );
    frequencies    = UIMAUtil.getAnnotationsBySpan(pJCas,DiarrheaFrequency.typeIndexID,            pSentence.getBegin(), pSentence.getEnd() );
    severities     = UIMAUtil.getAnnotationsBySpan(pJCas,DiarrheaSeverity.typeIndexID,             pSentence.getBegin(), pSentence.getEnd() );
    colors         = UIMAUtil.getAnnotationsBySpan(pJCas,DiarrheaColor.typeIndexID,                pSentence.getBegin(), pSentence.getEnd() );
    consistancies  = UIMAUtil.getAnnotationsBySpan(pJCas,DiarrheaConsistancy.typeIndexID,          pSentence.getBegin(), pSentence.getEnd() );
    sensations     = UIMAUtil.getAnnotationsBySpan(pJCas,DiarrheaSensation.typeIndexID,            pSentence.getBegin(), pSentence.getEnd() );
    modifiers      = UIMAUtil.getAnnotationsBySpan(pJCas,DiarrheaPathologicalModifier.typeIndexID, pSentence.getBegin(), pSentence.getEnd() );
    symptoms       = UIMAUtil.getAnnotationsBySpan(pJCas,DiarrheaSymptoms.typeIndexID,             pSentence.getBegin(), pSentence.getEnd() );
    labTests       = UIMAUtil.getAnnotationsBySpan(pJCas,DiarrheaLabTest.typeIndexID,              pSentence.getBegin(), pSentence.getEnd() );
    negatives      = UIMAUtil.getAnnotationsBySpan(pJCas,DiarrheaPertinantNegative.typeIndexID,    pSentence.getBegin(), pSentence.getEnd() );
    anatomies      = UIMAUtil.getAnnotationsBySpan(pJCas,DiarrheaAnatomy.typeIndexID,              pSentence.getBegin(), pSentence.getEnd() );
    defications    = UIMAUtil.getAnnotationsBySpan(pJCas,NormalDefication.typeIndexID,             pSentence.getBegin(), pSentence.getEnd() );
    fever          = UIMAUtil.getAnnotationsBySpan(pJCas,Fever.typeIndexID,                        pSentence.getBegin(), pSentence.getEnd() );
    durations      = UIMAUtil.getAnnotationsBySpan(pJCas,Duration.typeIndexID,                     pSentence.getBegin(), pSentence.getEnd() );
    ceased         = UIMAUtil.getAnnotationsBySpan(pJCas,DiarrheaCessation.typeIndexID,            pSentence.getBegin(), pSentence.getEnd() );
    negativeEvidence = UIMAUtil.getAnnotationsBySpan(pJCas,DiarrheaPertinantNegative.typeIndexID,  pSentence.getBegin(), pSentence.getEnd() );
    
    conditionalEvidence = UIMAUtil.getAnnotationsBySpan(pJCas,ConditionalEvidence.typeIndexID,  pSentence.getBegin(), pSentence.getEnd() );
    historicalEvidence = UIMAUtil.getAnnotationsBySpan(pJCas,HistoricalEvidence.typeIndexID,  pSentence.getBegin(), pSentence.getEnd() );
    otherNegativeEvidence = UIMAUtil.getAnnotationsBySpan(pJCas,NegationEvidence.typeIndexID,  pSentence.getBegin(), pSentence.getEnd() );
    subjectEvidence = UIMAUtil.getAnnotationsBySpan(pJCas,SubjectIsOtherEvidence.typeIndexID,  pSentence.getBegin(), pSentence.getEnd() );
    familyHistoryEvidence = UIMAUtil.getAnnotationsBySpan(pJCas,FamilyHistoryEvidence.typeIndexID,  pSentence.getBegin(), pSentence.getEnd() );
      
    boolean counterEvidence = false;
    if ( negativeEvidence   != null && !negativeEvidence.isEmpty()   ||
         conditionalEvidence != null && !conditionalEvidence.isEmpty() ||
         historicalEvidence != null && !historicalEvidence.isEmpty() ||
         otherNegativeEvidence != null && !otherNegativeEvidence.isEmpty() ||
        		 subjectEvidence != null && !subjectEvidence.isEmpty() ||
        		 familyHistoryEvidence != null && !familyHistoryEvidence.isEmpty() ||
                 diarrheaCeased != null && !diarrheaCeased.isEmpty() )
    	counterEvidence = true;
    		
    
    if      ( directMentions != null && !directMentions.isEmpty()  && !counterEvidence ) createDiarrheaMention( pJCas,directMentions , severities, frequencies, durations);
    
    else if      (( defications != null && !defications.isEmpty() )  && !counterEvidence &&
                                     ( // frequencies   != null |
                                 //    durations     != null |
                                 //    modifiers     != null |
                                 //    sensations    != null |
                                       consistancies != null |
                                 //    colors        != null |
                                       severities    != null ) )  createDiarrheaMention(pJCas, defications, frequencies, durations, modifiers, sensations, consistancies, colors, severities );
        
    else if (( anatomies != null && anatomies.isEmpty()) && !counterEvidence &&
                                   (   frequencies   != null |
                                       modifiers     != null |
                                       sensations    != null |
                                       consistancies != null |
                                //     colors        != null |
                                       severities    != null ) )  createDiarrheaEvidence(pJCas, anatomies, frequencies, durations, modifiers, sensations, consistancies, colors, severities  );

    else if ( labTests != null && !labTests.isEmpty() )           createDiarrheaEvidence(pJCas, labTests, null, null, null, null, null, null, null  );
    else if ( symptoms != null && !symptoms.isEmpty() )           createDiarrheaEvidence(pJCas, symptoms, frequencies, durations, modifiers, sensations, consistancies, colors, severities  );
    else if ( fever    != null && !fever.isEmpty() )              createDiarrheaEvidence(pJCas, fever,    frequencies, durations, modifiers, sensations, consistancies, colors, severities  );
    else {
      // Remove evidences that lead to nothing
      removeEvidence ( frequencies, severities, colors, consistancies, sensations, modifiers, symptoms, labTests, negatives, anatomies, defications, fever, durations);
    }
    
    
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println("Issue processing the sentence " + e.toString());
    
  }
    
} // end Method processDiarrheaEvidence() =====



  // =======================
/**
 * removeEvidence 
 *
 * @param pThingsToRemove (variable number of them)
 */
@SuppressWarnings({ "static-method", "unchecked" })
private void removeEvidence(List<Annotation>... pThingsToRemove  ) {
  
  if ( pThingsToRemove != null )
    for (List<Annotation> thingToRemove: pThingsToRemove)
      if ( thingToRemove != null && !thingToRemove.isEmpty() )
        for ( Annotation aThingToRemove: thingToRemove) {
          aThingToRemove.removeFromIndexes();
        }
  
  
  } // End Method removeEvidence =======



  // ==========================================
/**
 * createDiarrheaMention 
 *
 * @param pJCas
 * @param directMentions
 * @param pDurations 
 * @param pFrequencies 
 * @param pSeverities 
 */
// ==========================================
private void createDiarrheaMention(JCas pJCas, List<Annotation> pDirectMentions, List<Annotation> pSeverities, List<Annotation> pFrequencies, List<Annotation> pDurations) {
  
  try {
    
      DiarrheaMention statement = new DiarrheaMention( pJCas);
      
      int span[] = getMaxSpans( pDirectMentions, pSeverities, pFrequencies, pSeverities);
      
      statement.setBegin( span[0]);
      statement.setEnd(   span[1]);
      statement.setId("DiarrheaAnnotator_" + this.counter++);
      statement.addToIndexes();
      
    
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println("Issue creating the diarrheaMention " + e.toString());
  }
  
}  // end Method createDiarrheaMention() =====




  // ==========================================
  /**
   * getMaxSpans 
   *
   * @param pListOfListOfAnnotations 
   * @return int[]
   */
  // =========================================
  @SuppressWarnings("unchecked")
  private int[] getMaxSpans(Object... pListOfListOfAnnotations ) {
  
    int[] span = new int[2];
    int maxBegin = 999999;
    int maxEnd   = 0;
    
    if ( pListOfListOfAnnotations != null ) {
    	
    	if ( pListOfListOfAnnotations.length == 1) {
    	
    		span[0] = ((Annotation)((List)pListOfListOfAnnotations[0]).get(0)).getBegin();
    		span[1] = ((Annotation)((List)pListOfListOfAnnotations[0]).get(0)).getEnd();
    	} else {
    
    		for ( Object listOfAnnotations : pListOfListOfAnnotations ) {
      if ( listOfAnnotations != null ) {
        if ( !((List<Annotation>) listOfAnnotations).isEmpty() ) {
          span = getMaxSpansAux( (List<Annotation>)listOfAnnotations );
          if ( span[0] <= maxBegin)
            maxBegin = span[0];
          if ( span[1] < maxBegin)
            maxBegin = span[1];
          
          if ( span[1] > maxEnd)
            maxEnd   = span[1];
          if ( span[0] > maxEnd)
            maxEnd = span[0];
        }
      }
    }
    span[0] = maxBegin;
    span[1] = maxEnd;
    	}
    }
    
    if ( span[0] == 9999 )  
      throw new RuntimeException();
    return span;
  } // end Method getMaxSpans() ===============
  

  // ==========================================
  /**
   * getMaxSpans 
   *
   * @param pAnnotations
   * @return int[]
   */
  // =========================================
  private int[] getMaxSpansAux(List<Annotation> pAnnotations ) {
  
    int[] span = new int[2];
    int maxBegin = 9999999;
    int maxEnd   = 0;
    if ( pAnnotations != null ) {
      for ( Annotation annotation : pAnnotations ) {
        if ( maxBegin > annotation.getBegin() ) maxBegin = annotation.getBegin();
        if ( maxEnd   < annotation.getEnd()   ) maxEnd   = annotation.getEnd();
        
      }
    }
    span[0] = maxBegin;
    span[1] = maxEnd;
    return span;
  } // end Method getMaxSpansAux() ===============
  


// ==========================================
/**
 * createDiarrheaEvidence 
 *
 * @param pJCas
 * @param pTrigger
 * @param frequencies
 * @param pDurations,
 * @param modifiers
 * @param sensations
 * @param consistancies
 * @param colors
 * @param severities
 
 */
// ==========================================
private void createDiarrheaEvidence(JCas pJCas, 
                                    List<Annotation> pTrigger, 
                                    List<Annotation> pFrequencies,
                                    List<Annotation> pDurations,
                                    List<Annotation> pModifiers, 
                                    List<Annotation> pSensations, 
                                    List<Annotation> pConsistancies, 
                                    List<Annotation> pColors,
                                    List<Annotation> pSeverities) {
  
 
    
  int span[] = null;
   try {
     
     DiarrheaEvidence statement = new DiarrheaEvidence( pJCas);
     
     try {
     span = getMaxSpans( pTrigger, pFrequencies, pDurations, pModifiers, pSensations, pConsistancies, pColors, pSeverities);
     
     } catch (Exception e) {
       e.printStackTrace();
       
     }
     statement.setBegin( span[0]);
     statement.setEnd(   span[1]);
     statement.setId("DiarrheaAnnotator_" + this.counter++);
     statement.addToIndexes();
     
   
 } catch (Exception e) {
   e.printStackTrace();
   System.err.println("Issue creating the diarrheaMention " + e.toString());
 }
 
  
  
  // end Method createDiarrheaEvidence() ========================================
}


//==========================================
/**
* createDiarrheaEvidence 
*
* @param pJCas
* @param pTrigger
* @param frequencies
* @param pDurations,
* @param modifiers
* @param sensations
* @param consistancies
* @param colors
* @param severities

*/
//==========================================
private void createDiarrheaMention(JCas pJCas, 
                                 List<Annotation> pTrigger, 
                                 List<Annotation> pFrequencies,
                                 List<Annotation> pDurations,
                                 List<Annotation> pModifiers, 
                                 List<Annotation> pSensations, 
                                 List<Annotation> pConsistancies, 
                                 List<Annotation> pColors,
                                 List<Annotation> pSeverities) {


 
int span[] = null;
try {
  
  DiarrheaMention statement = new DiarrheaMention( pJCas);
  
  try {
  span = getMaxSpans( pTrigger, pFrequencies, pDurations, pModifiers, pSensations, pConsistancies, pColors, pSeverities);
  
  } catch (Exception e) {
    e.printStackTrace();
    
  }
  statement.setBegin( span[0]);
  statement.setEnd(   span[1]);
  statement.setId("DiarrheaAnnotator_" + this.counter++);
  statement.addToIndexes();
  

} catch (Exception e) {
e.printStackTrace();
System.err.println("Issue creating the diarrheaMention " + e.toString());
}



// end Method createDiarrheaEvidence() ========================================
}




  // ==========================================
  /**
   * createEvidence 
   *
   * @param pJCas
   */
  // ==========================================
  protected void createEvidence(JCas pJCas) {
	  
	  
    List<Annotation> terms = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.LexicalElement.type);
    String semanticTypes = null;

    if (terms != null) {
      // -----------------------------------------------------
      // Loop through each term
      // ----------------------------------------------------
      for (Annotation term : terms ) {
        if ((semanticTypes = ((LexicalElement) term).getSemanticTypes()) != null) {
         
          String semanticType = validSemanticTypes( semanticTypes);
          if (semanticType != null  ) {
          
            
              // Create an annotation as long as the term is not in
              // a content Heading that is not part of question/answer slot/value structure
              if ( !inSectionHeading(pJCas, term )) {
                try {
                  createAnnotation(pJCas, (LexicalElement) term, semanticType );
                } catch (Exception e) {
                  e.printStackTrace();
                }
              } // end if this is not in a section heading 
          } // end if this term has the right semantic type
        } // end if this term even has a semantic type
      } // end loop thru terms
    } // end if there are any terms
  }  // end Method createEvidence() ======================
  


// =======================================================
  /**
   * inSectionHeading returns true if this is in a section
   * heading (contentHeading) that is not in a slotValue
   * 
   * @param pJCas
   * @param pTerm
   * @return boolean
   */
  // =======================================================
  private boolean inSectionHeading(JCas pJCas, Annotation pTerm) {
    boolean returnVal = false;
    
    List<Annotation> sectionHeadings = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, ContentHeading.typeIndexID, pTerm.getBegin(),  pTerm.getEnd());
      
    if ( sectionHeadings != null && !sectionHeadings.isEmpty()) {
      for ( Annotation sectionHeading : sectionHeadings ) {
        String pedigree = ((ContentHeading)sectionHeading).getId();
        if ( pedigree.contains("ContentHeading_"))              returnVal = true;
        else if ( pedigree.contains("ObsecAn_ContentHeading"))  returnVal = true;
        
      } // loop thru all the overlapping section headings (there shold only be one)
    } // if there are any section headings to consider
    
    
    return returnVal;
  }  // End Method inSectionHeading() ======================
  


  //-----------------------------------------
   /**
    * createAnnotation 
    * 
    * @param pJCas
    * @param uimaLabelClass
    * @param pTerm
   * @param pSemanticType 
    * 
    * @return Evidence
  
    */
   // -----------------------------------------
   private void createAnnotation( JCas pJCas, LexicalElement pTerm , String pSemanticType ) {
   
     try {
      
       Class<?>   uimaLabelClass = Class.forName("gov.va.vinci.model." + pSemanticType);
       Constructor<?>          c = uimaLabelClass.getConstructor(new Class[] { JCas.class });
       
       Concept statement = (Concept) c.newInstance(pJCas);
      
     
       statement.setBegin(                    pTerm.getBegin());
       statement.setEnd(                      pTerm.getEnd());
       statement.setId("DiarrheaAnnotator" + this.counter++);
   
     
     
       String codeCode = pTerm.getEuis(0);
       String conceptName = pTerm.getCitationForm();
   
       statement.setCuis(codeCode);
       
       statement.setCategories(pSemanticType);
       statement.setConceptNames(conceptName);
       
       // ----------------------------------------
       // Set the assertion attribution if you can
       setAssertionAttribution(pJCas, statement, pTerm);
       
       // ----------------------------------
       // Set the section name 
       String sectionName = VUIMAUtil.getSectionName(pJCas, pTerm);
       statement.setSectionName(sectionName);
       
       // -------------------------
       // Determine if this is in prose or not
       statement.setInProse( VUIMAUtil.isInProse(pJCas,pTerm));
       
       statement.addToIndexes(pJCas);
  
     } catch (Exception e) {
       e.printStackTrace();
       System.err.println("Something went wrong here " + e.toString() );
     }
     
   } // end Method createAnnotation() ---


  // =======================================================
  /**
   * setAssertionAttribution will check to see if this concept
   * falls within a slot value, and if so, if this slot value
   * has a value, and if so, what the value is.  If the value
   * has a negative polarity to it, negate this concept.
   * 
   * @param pJCas
   * @param pStatement
   * @param pTerm 
   */
  // =======================================================
  private void setAssertionAttribution(JCas pJCas, Concept pStatement, LexicalElement pTerm) {
  
    
    if ( pTerm.getNegation_Status() != null) {
      pStatement.setAssertionStatus(pTerm.getNegation_Status());
      pStatement.setConditionalStatus( pTerm.getConditional());
      pStatement.setSubjectStatus( pTerm.getSubject());
      pStatement.setSectionName( pTerm.getSubject() );
      
    
    } else {
      List<Annotation> slotValues = UIMAUtil.getEnclosingAnnotation(pJCas, pStatement, SlotValue.typeIndexID);
    
      if ( slotValues != null && !slotValues.isEmpty()) {
        for ( Annotation aSlotValue : slotValues ) {
        
        DependentContent dependentContent = ((SlotValue) aSlotValue).getDependentContent();
        if ( dependentContent != null ) {
          pStatement.setConditionalStatus( dependentContent.getConditional());
          pStatement.setSubjectStatus(dependentContent.getSubject());
          String negationStatus = dependentContent.getNegation_Status();
          if (negationStatus != null && negationStatus.equals("Negated") ) {
            pStatement.setAssertionStatus( "Negated");
            
          } else {
            pStatement.setAssertionStatus("Asserted");
          }
        }
      }
    }
    }
    if (pStatement.getConditionalStatus() ) pStatement.setAssertionStatus("Negated");
    
  } // End Method setAssertionAttribution() ======================
  


  // =======================================================
  /**
   * validSemanticTypes returns the first valid semantic type 
   * from the list of semantic types 
   * 
   * 
   * @param pSemanticTypes
   * @return String
   */
  // =======================================================
  private String validSemanticTypes(String pSemanticTypes) {
   
    String returnVal = null;
    
    if ( pSemanticTypes != null && pSemanticTypes.trim().length() > 0 ) {
      String cols[] = U.split( pSemanticTypes, ":");
      
      if ( cols != null )
        for ( String pty : cols ) {
          if ( pty != null && pty.trim().length() > 0 )
            if ( this.validSemanticTypes.contains(pty)) {
              returnVal = pty;
              break;
            }
        }
      
    }
    
   return returnVal;
  } // End Method validSemanticTypes() ======================
  


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
 * initialize loads in the resources. 
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
	        System.err.println("Issue initializing the Diarrhea Annotator " + e.toString());
	        throw new ResourceInitializationException();
	      }
 
} // end Method initialize() --------------------

  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs ) throws ResourceInitializationException {
       
	  try {
	  	  this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
     	  this.validSemanticTypes = new HashSet<String>(7);
   
          for ( String semanticType : SemanticTypes ) 
			  this.validSemanticTypes.add( semanticType );
    
	  } catch (Exception e ) {
		  System.err.println("Issue initializing the Diarrhea Annotator " + e.toString());
		  throw new ResourceInitializationException();
	  }
	      
    
  } // end Method initialize() -------
  

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------

  protected ProfilePerformanceMeter       performanceMeter = null;

  private HashSet<String> validSemanticTypes = null;
  public static final String SemanticTypez = 
      "DiarrheaAnatomy:DiarrheaFrequency:DiarrheaSeverity:" + 
      "DiarrheaColor:DiarrheaConsistancy|DiarrheaSensation:DiarrheaPathologicalModifier:DiarrheaFrequency:DiarrheaSymptom:" +
      "NormalDefication:DiarrheaLabTest:DiarrheaDirectMention:DiarrheaMention:" + 
      "DiarrheaEvidence:DiarrheaCessation:DiarrheaPertinantNegative:";
  
  public static final String DiarrheaMentions =   "DiarrheaDirectMention:DiarrheaMention:Diarrhea:NegativeConditional";
  public static final String[] SemanticTypes = U.split(SemanticTypez,":");
  private int                   counter = 0;
  
  
  
} // end Class MetaMapClient() ---------------
