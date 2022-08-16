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
 * PotentialSymptomAnnotator creates Symptoms and OrganSystem out of 
 *   terms that have categories that are symptom types.
 *
 *       
 * @author  Guy Divita 
 * @created March 9, 2014
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;


import java.io.BufferedReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.vinci.model.OrganSystem;
import gov.va.vinci.model.Symptom_Potential;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class PotentialSymptomAnnotator extends JCasAnnotator_ImplBase {
 

//-----------------------------------------
 /**
  * process 
  * 
  * 
  */
 // -----------------------------------------
 public void process(JCas pJCas) throws AnalysisEngineProcessException {
  
   try {
     this.performanceMeter.startCounter();
     
     // ------------------------
     // Mark terms as not seen
     // ------------------------
     markTermsAsNotSeen(pJCas);
     
     // Traverse thru sentences, looking for symptoms
      java.util.List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID);
      
      if ( sentences != null && !sentences.isEmpty()) {
        for (  Annotation aSentence: sentences ) {
          findSymptoms(pJCas, aSentence);
         
        }
      } // end loop thru sentences
     
      
      // ------------------------
      // Look at slot values to retrieve symptom terms 
      //   look for triggers in the slots and evidence in the values
      java.util.List<Annotation> slotValues = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID);
      
      if ( slotValues != null ) {
        for ( Annotation aSlotValue : slotValues ) {
          findSymptomSlotValue(pJCas, aSlotValue);
          
        }
      } // end loop thru slot values
     
      List<Annotation> potentialSymptoms = UIMAUtil.getAnnotations(pJCas, Symptom_Potential.typeIndexID);
      UIMAUtil.uniqueAnnotations( potentialSymptoms);
     
      this.performanceMeter.stopCounter();
   } catch (Exception e) {
     e.printStackTrace();
     System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
    
   }
 } // end Method process() ----------------
  

//-----------------------------------------
/**
* markTermsAsNotSeen initializes all the terms to notseen.
* 
* @param pJCas
* 
*/
//-----------------------------------------
private void markTermsAsNotSeen(JCas pJCas) {
	try {
		java.util.List<Annotation> terms = UIMAUtil.getAnnotations(pJCas, LexicalElement.typeIndexID);
		if ( terms != null ) 
			for ( Annotation term: terms ) 
				((LexicalElement)term).setMarked(false);
			
		
	} catch (Exception e) {
		e.printStackTrace();
		System.err.println("Issue with marking the terms as not seen " + e.toString());
	}
	
} // end Method markTermsAsNotSeen() -----




//-----------------------------------------
/**
* hasTermsBeenSeen returns true if the terms have been seen
* 
* @param pTerms
* @return boolean
*/
//-----------------------------------------
private boolean hasTermsBeenSeen(List<Annotation> pTerms) {
	
	boolean returnVal = false;
	if ( pTerms != null && !pTerms.isEmpty() )
		for ( Annotation term: pTerms) {
			if ( hasTermBeenSeen( term) ){
				returnVal = true;
				break;
			}
		}
	return returnVal;

} // end Method hasTermsBeenSeen() -----


//-----------------------------------------
/**
* hasTermBeenSeen returns true if the term have been seen
* 
* @param pTerm
* @return boolean
*/
//-----------------------------------------
private boolean hasTermBeenSeen(Annotation pTerm) {
	
	boolean returnVal = false;
	
	returnVal = ((LexicalElement)pTerm).getMarked();

    return returnVal;

} // end Method hasTermsBeenSeen() -----


//-----------------------------------------
/**
* markTermsASeen marks the terms as seen
* 
* @param pTerms
* 
*/
//-----------------------------------------
private void markTermsAsSeen(List<Annotation> pTerms) {
	
	if ( pTerms != null && !pTerms.isEmpty() )
		for ( Annotation term: pTerms)
			markTermAsSeen( term);

} // end Method markTermsAsSeen() -----


//-----------------------------------------
/**
* markTermAsSeen marks the term as seen
* 
* @param pTerm
* 
*/
//-----------------------------------------
private void markTermAsSeen(Annotation pTerm) {
	
	((LexicalElement)pTerm).setMarked(true);

} // end Method markTermAsSeen() -----


//-----------------------------------------
/**
 * findSymptoms
 * 
 * @param pJCas
 * @param pSentence
* @throws AnalysisEngineProcessException 
 * 
 */
// -----------------------------------------
private void findSymptoms(JCas pJCas, Annotation pSentence) throws AnalysisEngineProcessException {
 
 
 try {
 // iterate thru the terms of the sentence looking
 
  List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd());
 
  
 
  if ( terms != null && !terms.isEmpty() ) {
    // Annotation         trigger = findTrigger( terms, "SignOrSymptomTrigger" );
    List<Annotation> evidences = findEvidence( pJCas, terms, "SignOrSymptom" ); 
    evidences = cullNotASymptom(evidences);
    List<Annotation>  negativeEvidences = findEvidence(pJCas, terms, "NegativeSymptomEvidence");
  
    
    if ( evidences != null && !evidences.isEmpty() && negativeEvidences == null ) {
      String sectionName = ((LexicalElement)evidences.get(0)).getSectionName();
      if ( notInSection ( sectionName ))
    	  if ( evidencesAsserted( pJCas, evidences ))
    		  createSymptom( pJCas, evidences);
    }

  } // end if the terms are not null
  
  
 } catch( Exception e) {
   e.printStackTrace();
   System.err.println("Issue annotating bowel prep " + e.toString());
   throw new AnalysisEngineProcessException();
 }
 
} // end Method findbowelPrep

//-----------------------------------------
/**
* evidencesAsserted returns true if the evidences are all asserted 
* 
* (GD edit 2021-01) (and non-contextual)
* 
* @param pJCas
* @param pSentence
* @throws AnalysisEngineProcessException 
* 
*/
//-----------------------------------------
private boolean evidencesAsserted(JCas pJCas, List<Annotation> evidences) {
	boolean returnVal = true;
	if ( evidences != null )
		for ( Annotation evidence: evidences ) {
			returnVal = evidenceAsserted( pJCas, evidence ) ;
			if ( !returnVal )
				break;
			}
		
	return returnVal;
}

//-----------------------------------------
/**
* evidencesAsserted returns true if the evidences are all asserted 
* (GD edit 2021-01 ) (and non-contextual, and relates to the patient
* 
* Ask the question whether or not historical should be in this list - I think it should be
* 
* @param pJCas
* @param pSentence
* @throws AnalysisEngineProcessException 
* 
*/
//-----------------------------------------
private boolean evidenceAsserted(JCas pJCas, Annotation evidence) {

	boolean returnVal = true;
	String assertionStatus = null;

	assertionStatus = VUIMAUtil.getAssertionStatus(pJCas, evidence);
	if ( assertionStatus == null || !assertionStatus.contains("Asserted")) {
		return false;
	 
	}
	
	if ( VUIMAUtil.getConditional_Status( evidence ) )
	  return false;
	
	if ( !VUIMAUtil.getSubject_Status( evidence ))
	    return false;
	
	return returnVal;
	
} // end Method evidenceAsserted() --------


//-----------------------------------------
/**
* CullNotASymptom
*   Because the symptom list is machine generated and some odd symptoms
*   are acronyms of regular words that show up much more often - 
*   rather than edit the symtpom list, we apply a filter to filter out
*   terms that got tagged with "NotSymptom" - i.e., we take these 
*   terms off the symptom evidence list
*   
* @param pJCas
* @param pSlotValue
* @throws AnalysisEngineProcessException 
* 
*/
//-----------------------------------------
private List<Annotation> cullNotASymptom(List<Annotation> evidences) {
	
	ArrayList<Annotation> returnVal = null;
	if ( evidences != null ) {
		returnVal = new ArrayList<Annotation>(evidences.size());
		for ( Annotation evidence: evidences ) {
			if ( findTrigger( evidence, "NotSymptom") == null )
				returnVal.add( evidence );
		}
	}
	return returnVal;
} // end Method cullNotASymptom() --------


//-----------------------------------------
/**
* findBowelPrepSlotValue
* 
* @param pJCas
* @param pSlotValue
* @throws AnalysisEngineProcessException 
* 
*/
//-----------------------------------------
private void findSymptomSlotValue(JCas pJCas, Annotation pSlotValue) throws AnalysisEngineProcessException {


try {
//Retrieve the slot and the value

  ContentHeading     slot = ((SlotValue)pSlotValue).getHeading();
   DependentContent value = ((SlotValue)pSlotValue).getDependentContent();
   List<Annotation> valueTerms = null;
   String valueString = null;
   
   // --------------------
   // Retrieve terms from the slot
    List<Annotation> slotTerms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, slot.getBegin(), slot.getEnd());
    Annotation         trigger = findTrigger( slotTerms, "SignOrSymptom" );
    if ( value != null ) {		
      valueTerms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, value.getBegin(), value.getEnd());
      valueString = value.getCoveredText();
    }
    boolean notFound = true;
    
   
    if ( trigger != null  && value != null ) {
   // --------------------
   // Retrieve terms from the value
      
      if (( valueTerms != null && !valueTerms.isEmpty()) || 
    	  (valueString != null && (U.isNumber( valueString) || valueString.contains("+") ) ) ) {
        
        List<Annotation>         negativeEvidences = findEvidence(pJCas, valueTerms, "NegativeSymptomEvidence");
      
        if (  negativeEvidences == null ) {
        	  if ( evidenceAsserted( pJCas, trigger ))
          createSymptom( pJCas, pSlotValue, (LexicalElement)trigger );
          notFound = false;
        }
      }
    }

    // ----------------------------------------------------------
    // if nothing was created above, check the value for evidence
    if ( notFound ) {
    	  List<Annotation> evidences = findEvidence( pJCas, valueTerms, "SignOrSymptom" );
    	  evidences = cullNotASymptom( evidences);
    	  if ( evidences != null && !evidences.isEmpty()  )
    		  createSymptom(pJCas,  evidences );
    }
    
    
    
    
 } catch( Exception e) {
   e.printStackTrace();
   System.err.println("Issue annotating bowel prep " + e.toString());
   throw new AnalysisEngineProcessException();
 }

} // end Method findbowelPrepSlotValue()
    
//-----------------------------------------
/**
* findTrigger
* 
* @param pTerm
* @param pCategory
* @return Annotation
*/
//-----------------------------------------
private Annotation findTrigger(Annotation pTerm, String pCategory)  {
    
 Annotation trigger = null;
  
 String semanticTypes = ((LexicalElement)pTerm).getSemanticTypes();
 if ( semanticTypes != null && semanticTypes.length() > 3) {
   if ( containsSemanticType(semanticTypes, pCategory) )
     trigger = pTerm;
 }
 
 return trigger;
   
} // end Method findBowelPrepTrigger

//-----------------------------------------
/**
* findTrigger returns the first found trigger
* 
* @param pTerms
* @param pCategory
* @return Annotation  (null if not found)
* 
*/
//-----------------------------------------
private Annotation findTrigger(List<Annotation> pTerms, String pCategory)  {
  
 Annotation trigger = null;
 if ( pTerms != null ) {
   for ( Annotation term : pTerms ) {
     trigger = findTrigger(term, pCategory );
     if ( trigger != null ) 
       break;
   } // end loop thru terms
 }
 return trigger;
 
} // end Method findBowelPrepTriggers

//-----------------------------------------
/**
* findEvidence
* 
* @param pTerm
* @param pCategories 
* @return Annotation (null if not found) 
* 
*/
//-----------------------------------------
private Annotation findEvidence(JCas pJCas, Annotation pTerm, String pCategory)  {
  
Annotation evidence = null;
String termString = pTerm.getCoveredText().trim();
String semanticTypes = ((LexicalElement)pTerm).getSemanticTypes();
if ( semanticTypes != null && semanticTypes.length() > 3) {
 if ( containsSemanticType( semanticTypes, pCategory))
 
   evidence = pTerm;
} else if ( pCategory.equals("Number")) {
   termString = pTerm.getCoveredText().trim();
  if ( U.isNumber( termString ))
    evidence = pTerm;
}

if ( evidence != null ) {
  String assertionStatus = VUIMAUtil.getAssertionStatus(pJCas, evidence );
  boolean conditional = VUIMAUtil.getConditional_Status( evidence );
  String subject = VUIMAUtil.getSubject( evidence);
 
  if ( assertionStatus.contains("Asserted") && !conditional && subject.equals("Patient") )
     ;
 else
   evidence = null;
}   
return evidence;
 
} // end Method findEvidence()


//-----------------------------------------
/**
* findEvidences returns all the found evidences
* 
* @param pTerms
* @param pCategory
* @return List<Annotation>  (null if not found)
* 
*/
//-----------------------------------------
private List<Annotation> findEvidence(JCas pJCas, List<Annotation> pTerms, String pCategory)  {

Annotation evidence = null;
ArrayList<Annotation> evidences = null;
if ( pTerms != null ) {
for ( Annotation term : pTerms ) {
  evidence = findEvidence(pJCas, term, pCategory );
  if ( evidence != null ) {
    if ( evidences == null ) evidences = new ArrayList<Annotation>();
    evidences.add( evidence);
  }
    
} // end loop thru terms
}
return evidences;

} // end Method findEvidences()

private boolean containsSemanticType(String pSemanticTypes, String pCategory) {

  boolean returnVal = false;
  String cols[] = U.split(pSemanticTypes, ":" );
  String[] categories = U.split(pCategory, ":" );
  
  for ( String col: cols ) {
    if ( col != null ) {
     for ( String category: categories ) {
        if ( col.equals( category)){
          returnVal = true;
          break;
        }
      }
      if ( returnVal ) break;  
    }
  }
  
  return returnVal;
 
} // End Method containsSemanticType() ======================

 
  

  // =======================================================
  /**
   * createOrganSystem creates the symptom categorization (organSystem)
   * annotation for each category this term has.
   * 
   * @param pJCas
   * @param pTerm
   */
  // =======================================================
  private void createOrganSystem(JCas pJCas, LexicalElement pTerm) {
   
    String categories = pTerm.getSemanticTypes();
    if ( categories != null ) {
      String[] categoriez = U.splitBetter(categories, ':');
      for ( String category : categoriez ) {
        if ( symtpomCategoryz.contains( category.trim() )) {
          createOrganSystem( pJCas, pTerm, category.trim());
        }
        
      } // end loop through the categories
      
    } // end if there are categories
    
    
    
  }  // End Method createOrganSystem() ======================
  


  // =======================================================
  /**
   * createOrganSystem creates an annotation of the type Category
   * 
   * @param pJCas
   * @param pTerm
   * @param pCategory
   * 
   */
  // =======================================================
  private void createOrganSystem(JCas pJCas, LexicalElement pTerm, String pCategory) {
  
    StringArray cuiz = pTerm.getEuis();
    String cuis = null;
    if ( cuiz != null) {
      cuis = UIMAUtil.stringArrayToString( cuiz);
      cuis = cuis.replace('|', ':');      
    }
    try {
      if ( pCategory.equals("Sign or Symptom")) pCategory = "GeneralSymptom";
      Class<?> organSystemClass = Class.forName("gov.va.vinci.model." + pCategory);
  
      Constructor<?> c = organSystemClass.getConstructor(new Class[] { JCas.class });
      OrganSystem statement = (OrganSystem) c.newInstance(pJCas);

      
      statement.setBegin(                    pTerm.getBegin());
      statement.setEnd(                      pTerm.getEnd());
     
      ((OrganSystem)statement).setAssertionStatus(pTerm.getNegation_Status());
      ((OrganSystem)statement).setCategories(pCategory);
      ((OrganSystem)statement).setConceptNames( pTerm.getCitationForm());
      ((OrganSystem)statement).setConditionalStatus( pTerm.getConditional());
      ((OrganSystem)statement).setCuis(cuis);
      ((OrganSystem)statement).setSubjectStatus(pTerm.getSubject());
      
      statement.addToIndexes();
    
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue trying to make an organ system annotation " + e.toString() ;
      System.err.println(msg);
      
    }
  
  } // End Method createOrganSystem() ======================
  





//=======================================================
 /**
  * getCuizFromTerm returns a string of cuis, delimited 
  * with a : 
  * 
  * @param pTerm
  * @return String
  */
 // =======================================================
 private String getCuizFromTerm(LexicalElement pTerm) {
 
   StringArray euiz = null; 
   String euis = null;
   if ( pTerm != null )
     if ( (euiz = pTerm.getEuis()) != null ) {
       euis = UIMAUtil.stringArrayToString(euiz);
   
       if ( euiz != null )
         euis = euis.replace('|',  ':');
     }

   return euis;
   
 } // End Method getCuisFromTerm() ======================

  // ------------------------------------------
  /**
   * notInSection
   *    filter out any sections that look like medication lists
   *    med
   *
   * @param sectionName
   * @return true if the concept is not in a section
   *                 where symptoms should not be found
   */
  // ------------------------------------------
  private boolean notInSection(String sectionName) {
  
    
    boolean returnValue = true;
    
    if ( sectionName != null ) 
      if ( this.badSections.contains(sectionName.trim()) ) 
        returnValue = false;
      else if ( sectionName.toLowerCase().contains("med") ||
    		    sectionName.toLowerCase().contains("plan") ||
    		    sectionName.toLowerCase().contains("alerg") ||
    		    sectionName.toLowerCase().contains("signed")  ||
    		    sectionName.toLowerCase().contains("labs") )
    	      
    	  returnValue = false;
    	  
    return returnValue;
        
  }  // End Method notInSection() -----------------------

  
  

//-----------------------------------------
/**
* createSymptom
* 
* @param pJCas
* @param pBowelPrepQualityEvidences
* @throws Exception 
* 
*/
// -----------------------------------------
private void createSymptom(JCas             pJCas, 
                           List<Annotation> pSymptomEvidences) throws Exception {
  
  
	
	for ( Annotation evidence: pSymptomEvidences ) {
		
		String assertionStatus = ((LexicalElement) evidence).getNegation_Status();
		if ( assertionStatus == null ) assertionStatus = "Asserted";
		if ( ((LexicalElement) evidence).getMarked()) continue;
		if ( !assertionStatus.equals("Asserted")	) continue;
		
		String cuis = getCuizFromTerm( (LexicalElement) evidence );
  
  
  String conceptName = ((LexicalElement) evidence).getCitationForm();
  String sectionName = ((LexicalElement) evidence).getSectionName();
  

  
  Symptom_Potential statement = new Symptom_Potential(pJCas);
  statement.setSectionName(sectionName);
    
  
  statement.setBegin(((LexicalElement) evidence).getBegin());
  statement.setEnd(((LexicalElement) evidence).getEnd());
  
  statement.setAssertionStatus( assertionStatus );
 
  statement.setCuis(cuis);
  statement.setConceptNames(conceptName);
  String lastCategory = ((LexicalElement) evidence).getSemanticTypes();
  statement.setCategories( lastCategory);
 
  statement.setSubjectStatus( ((LexicalElement) evidence).getSubject());
  statement.setConditionalStatus( ((LexicalElement) evidence).getConditional());
  statement.setId("Potential_Symptom_1:" + U.getUniqId());
  statement.setInProse( VUIMAUtil.isInProse(pJCas,((LexicalElement) evidence)));

  statement.addToIndexes();
  
	} //end Loop thru evidence

  // createOrganSystem( pJCas, lastTerm);
  
 
} // end Method createSymptom() ----



//-----------------------------------------
/**
* createSymptom
* 
* @param pJCas
* @param pSlotValue
* @param pSymptomEvidence
* @param pBowelPrepQualityEvidences
* @throws Exception 
* 
*/
//-----------------------------------------
private void createSymptom(JCas             pJCas, 
                           Annotation       pSlotValue, 
                           Annotation       pSymptomEvidence) {

	if ( hasTermBeenSeen( pSymptomEvidence ) )
		  return;
else 
	  markTermAsSeen( pSymptomEvidence );
	
	String assertionStatus = ((LexicalElement) pSymptomEvidence).getNegation_Status() ;
	if (assertionStatus == null ) assertionStatus = "Asserted";	
	if ( !assertionStatus.equals("Asserted")	) return;
		
  
  Symptom_Potential statement = new Symptom_Potential(pJCas);
  statement.setSectionName( ((LexicalElement) pSymptomEvidence).getSectionName());
   
  
  String cuis = getCuizFromTerm( (LexicalElement) pSymptomEvidence  );
  
  
  String conceptName = ((LexicalElement)pSymptomEvidence).getCitationForm();
 

  statement.setBegin(pSlotValue.getBegin());
  statement.setEnd(pSlotValue.getEnd());
  statement.setCuis(cuis);
  statement.setConceptNames(conceptName);
  String lastCategory = ((LexicalElement)pSymptomEvidence).getSemanticTypes();
  statement.setCategories( lastCategory);
 
  statement.setAssertionStatus( assertionStatus);
  statement.setSubjectStatus( ((LexicalElement)pSymptomEvidence).getSubject());
  statement.setConditionalStatus( ((LexicalElement)pSymptomEvidence).getConditional());
  statement.setId("Potential_Symptom_2:" + U.getUniqId());
  statement.setInProse( false);

  statement.addToIndexes();

  // createOrganSystem(pJCas, (LexicalElement) pSymptomEvidence);
  
  System.err.println("Made a symptomf of " + statement.getCoveredText());
} // End Method createBostonBowelPrepScoreEvidenceAnnotation() ==




//-----------------------------------------
/**
* makeTempLexicalElement
* 
* @param pJCas
* @param pListOfTerms
* 
* @return LexicalElement
*/
//-----------------------------------------
private LexicalElement makeTempLexicalElement(JCas pJCas, List<Annotation> pListOfTerms) {

LexicalElement returnVal = null;

if ( pListOfTerms != null && pListOfTerms.size() == 1)
  returnVal = (LexicalElement) pListOfTerms.get(0); 
else if ( pListOfTerms != null && pListOfTerms.size() > 1){
  Annotation first = pListOfTerms.get(0);
  Annotation last  = pListOfTerms.get( pListOfTerms.size() -1);

  returnVal = new LexicalElement(pJCas);
  returnVal.setBegin( first.getBegin());
  returnVal.setEnd(   last.getEnd());
  returnVal.setSemanticTypes( ((LexicalElement)last).getSemanticTypes() );
}


return returnVal;
}// End Method makeTempLexicalElement() ======================





// =======================================================
  /**
   * getFirstCategory retrieves the list of categories from
   * a : delimited list
   * 
   * @param semanticTypes
   * @return String
   */
  // =======================================================
  private String getFirstCategory(String semanticTypes) {
  
    String returnVal = null;
    if ( semanticTypes != null && semanticTypes.length() > 0) {
      
      String vals[] = U.split(semanticTypes, ":");
      if ( vals != null )
        for ( int i = 0; i < vals.length; i++ ) {
          if ( vals[i] != null && vals[i].trim().length() > 0 ) {
            returnVal = vals[i];
            break;
          }
        }
    }
    return returnVal;
      
  } // End Method getFirstCategory() ======================
  



//------------------------------------------
/**
 * initializeBadSections loads a file of sections that
 * symptoms should not appear in.
 * 
 * @param pLocalResources
 * 
 */
// ------------------------------------------
private void initializeBadSections(  )  {
  
  this.badSections = new HashSet<String>();
  BufferedReader in = null;

  // --------------------------
  // Look up local badSymptoms file first 

   String fileName = "resources/vinciNLPFramework/symptoms/badSections.txt";
  
  try {
     in = U.getClassPathResource(fileName );
      } catch (Exception e2) {
        e2.printStackTrace();
        System.err.println("Could not find a bad sections file at " + fileName +  " | because " + e2.toString());
      }
  
    
 
  String row = null;
  try {
    if ( in == null ) return;
    while (( row = in.readLine()) != null )   {
      if ( row!= null &&  row.trim().length() > 1 && !row.trim().startsWith("#")) {
        this.badSections.add(row.trim());
      } // end if
    } // end loop 
    in.close(); 
  
  } catch (Exception e2 ) {
    e2.printStackTrace();
    String msg = "issue reading the bad sections file " + e2.toString();
    System.err.println(msg);
  }
  
  
} // end Method initializeBadSymptoms() ----


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
   * initialize 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
 	  String args[] = null;
   	  
    	try {
        args                  = (String[]) aContext.getConfigParameterValue("args"); 
        
        initialize( args);
        
    	} catch (Exception e) {
    	  e.printStackTrace();
        System.err.println("Issue with getting the args for the symptom annotator " + e.toString());
        throw new ResourceInitializationException();
    	}
  } // end Method initialize() ---------
        
  //----------------------------------
  /**
   * initialize
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize( String[] pArgs) throws ResourceInitializationException {
    
  
    try {
        this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
        
        this.initializeBadSections(  );
        
        
        symtpomCategoryz = new HashSet<String> ();
        for ( String category : symptomCategories )  symtpomCategoryz.add( category);   
        
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with getting the args for the symptom annotator " + e.toString());
      throw new ResourceInitializationException();
  
    }
    
      
  } // end Method initialize() -------
  

 
// ---------------------------------------
// Global Variables
// ---------------------------------------
  private HashSet<String>          badSections = null;
  private ProfilePerformanceMeter  performanceMeter = null;
 
  
  protected static final String[] symptomCategories = {     // These correspond to the categories in the AreSymptoms.LRAGR and localSymptom.LRAGR tables
  "Sign or Symptom",
  "Cardiovascular",
  "Digestive",
  "Endocrine",
  "GeneralSymptom",
  "Genitourinary",
  "Immune",
  "Integumentary",
  "Lymphatic",
  "MentalHealth",
  "Musculoskeletal",
  "Nervous",
  "Reproductive",
  "Respiratory",
  "Urinary"
  };
  protected static HashSet<String> symtpomCategoryz  = null;


  
} // end Class MetaMapClient() ---------------
