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
 * SimpleConceptAnnotator converts clinicalStatements
 * to SimpleConcepts, with a string that contains the
 * sorted set of cui's for simple span and cui comparison.
 *
 * @author  Guy Divita 
 * @created March 10, 2013
 * @modified April 23, 2013  - added conceptNames, made a one-to-one relationship
 *                             between cui and concept name fields.  No longer sorted
 *                             by cui's. 
 *
 * ------------------------------------------------------------
 *
 *
 *
 * -------------------------------------------------------------
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ClinicalStatement;
import gov.va.chir.model.CodedEntry;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.Concept;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class SimpleConceptAnnotator extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  /**
   * process retrieves lines of the document, labels those that are questions
   * as QuestionAndAnswer elements.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
  
    this.performanceMeter.startCounter();
   
    HashMap<String,Concept> cui_conceptHash = new HashMap<String, Concept>();
    // ------------------------------------
    // retrieve the clinical statements
    //
    List<Annotation> clinicalStatements = UIMAUtil.getAnnotations(pJCas, ClinicalStatement.type);
    List<Annotation>       codedEntries = UIMAUtil.getAnnotations( pJCas, CodedEntry.type);
    
    if ( clinicalStatements != null)
      for (Annotation clinicalStatement : clinicalStatements)
    
        createSimpleConcept( pJCas, clinicalStatement );
        
    else if ( codedEntries != null )
      for ( Annotation codedEntry: codedEntries ) 
        createSimpleConceptFromCodedEntry ( pJCas, codedEntry, cui_conceptHash);
      
    
    
    this.performanceMeter.stopCounter();
  
  } // end Method process() ----------------
   
  
//-----------------------------------------
 /**
  * createQuestionAnnotation
  * 
  * @param pJCas
  * @param pClinicalStatement
  * 
  * @return Evidence
  */
 // -----------------------------------------
 private void createSimpleConcept( JCas pJCas,  Annotation pClinicalStatement  )  {
  
  Concept statement = new Concept( pJCas);
  HashMap<String,String>cui_conceptHash = new HashMap<String, String>();
  
   statement.setBegin(        pClinicalStatement.getBegin());
   statement.setEnd(          pClinicalStatement.getEnd());
  
   // assemble the cuis from the clinical statement
   FSArray codedEntriez = ((ClinicalStatement)pClinicalStatement).getCodedEntries();
   if ( codedEntriez != null ) {
       @SuppressWarnings("unchecked")
      List<Annotation> codedEntries = UIMAUtil.fSArray2List(pJCas, codedEntriez);
       
       for ( Annotation codedEntry: codedEntries ) {
         String                cui = ((CodedEntry) codedEntry).getCodeCode();
         String        conceptName = ((CodedEntry) codedEntry).getDisplayName();
         StringArray semanticTypez = ((CodedEntry) codedEntry).getSemanticType();
         String semanticTypes      = clean( semanticTypez);
           
         cui_conceptHash.put( cui + "|" + conceptName, semanticTypes);
         
       } // end loop through the list of coded entries
       
       
       
       Set<String>               keys = cui_conceptHash.keySet();
       StringBuffer          cuiArray = new StringBuffer();
       StringBuffer  conceptNameArray = new StringBuffer();
       StringBuffer semanticTypeArray = new StringBuffer();
     
       for ( String key: keys ) {
          String[] cui_concept = U.split( key);
          cuiArray.append        (cui_concept[0] + ":");
          conceptNameArray.append(cui_concept[1] + ":");
          semanticTypeArray.append(cui_conceptHash.get(key) + ":");
       }          
       
       // -------------------------------------
       // remove the trailing pipes
       String         cuis = cuiArray.substring        (0, cuiArray.length()         -1);
       String conceptNames = conceptNameArray.substring(0, conceptNameArray.length() -1 );
       String semanticTypes = semanticTypeArray.substring(0, semanticTypeArray.length() -1);
  
       
       statement.setCuis( cuis);
       statement.setConceptNames( conceptNames);
       statement.setCategories(semanticTypes );
       String assertionStatus = ((ClinicalStatement)pClinicalStatement).getNegation_Status();
       statement.setAssertionStatus(  assertionStatus);
       
       
       statement.setConditionalStatus(  ((ClinicalStatement) pClinicalStatement).getConditional() );
       statement.setAssertionStatus (   ((ClinicalStatement)pClinicalStatement).getNegation_Status());
       statement.setHistoricalStatus(   ((ClinicalStatement)pClinicalStatement).getHistorical() );
       statement.setSubjectStatus(      ((ClinicalStatement)pClinicalStatement).getSubject() );
       
       
       String sectionName = VUIMAUtil.deriveSectionName( pClinicalStatement);
       
       statement.setSectionName( sectionName );
       //statement.setSection(v);
       statement.setId( this.getClass().getSimpleName() + "_" + this.annotation_Ctr++);
       
       
   
       
       
       
   } // end if there are codedEntriez
  
   statement.addToIndexes(pJCas);

 } // end Method createEvidence() ---
  
  

  // =======================================================
/**
 * createSimpleConceptFromCodedEntry [Summary here]
 * 
 * @param pJCas
 * @param codedEntry
 * @param cui_conceptHash 
 */
// =======================================================
private void createSimpleConceptFromCodedEntry(JCas pJCas, Annotation codedEntry, HashMap<String, Concept> conceptHash) {
  
  
  String key = codedEntry.getBegin() + "|" + codedEntry.getEnd();
  String cui = ((CodedEntry) codedEntry).getCodeCode();
  String conceptName  = ((CodedEntry) codedEntry).getDisplayName();
  StringArray semanticTypz = ((CodedEntry) codedEntry).getSemanticType(); 
  String semanticType = UIMAUtil.stringArrayToString(semanticTypz); 
  
  Concept statement = conceptHash.get(key);
  
  
  if ( statement == null ) {
    statement = new Concept( pJCas);
    statement.setBegin(        codedEntry.getBegin());
    statement.setEnd(          codedEntry.getEnd());
    statement.setId(U.getUniqId());
    statement.addToIndexes();
    //statement.setAnnotator( this.getClass().getName());
    statement.setAssertionStatus( ((CodedEntry)codedEntry).getNegation_Status());
    statement.setConditionalStatus( ((CodedEntry)codedEntry).getConditional());
    statement.setHistoricalStatus( ((CodedEntry)codedEntry).getHistorical());
    statement.setSubjectStatus( ((CodedEntry)codedEntry).getSubject());
    statement.setSectionName( ((CodedEntry)codedEntry).getSectionName());
   
    
    conceptHash.put(key, statement);
  }
  
  String          cuis = addNodeToList( statement.getCuis(),         cui          );
  String  conceptNames = addNodeToList( statement.getConceptNames(), conceptName  );
  String semanticTypes = addNodeToList( statement.getCategories(),   semanticType ); 
  
  statement.setCuis( cuis);
  statement.setConceptNames( conceptNames);
  statement.setCategories(semanticTypes );


  
 
  
  

} // End Method createSimpleConceptFromCodedEntry() ======================



// =======================================================
/**
 * addNodeToList 
 * 
 * @param cuis
 * @return 
 */
// =======================================================
private String addNodeToList(String pipedString, String value) {
  
  String returnVal = null;
  
 if ( pipedString == null ) 
  returnVal = value;
 else {
    String vals[] = U.split(pipedString);
    
    boolean found = false;
    for ( String val: vals) {
      if ( val.equals( value )) { 
        found = true;
        break;
      }
    }
    if ( !found )
      returnVal = pipedString + ":" + value;
 }
 
 return returnVal;
}  // End Method addNodeToList() ======================



// ------------------------------------------
/**
 * clean returns a String from the string array
 *  of pipe delimited elements
 *
 *
 * @param pSr
 * @return
 */
// ------------------------------------------
private String clean(StringArray pArray) {
 
  String returnValue = null;
  
  if ( pArray != null )
    returnValue = UIMAUtil.stringArrayToString(pArray);
  
  return returnValue;
  
  
}  // End Method clean() -----------------------


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
       
      
     } catch (Exception e) {
		  e.printStackTrace();
		  GLog.println("Something went wrong initializing the simpleConcept " + e.toString());
		  throw new ResourceInitializationException();
	  }
     
   
      
  } // end Method initialize() -------
  
//----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {

	  try {
		   this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
	  } catch (Exception e) {
		  e.printStackTrace();
		  GLog.println("Something went wrong initializing the simpleConcept " + e.toString());
		  throw new ResourceInitializationException();
	  }
      
  } // end Method initialize() -------
  
 
// ---------------------------------------
// Global Variables
// ---------------------------------------
   PerformanceMeter              performanceMeter = null;
   private static int              annotation_Ctr = 0;

  
  
} // end Class SimpleConceptAnnotator() ---------------
