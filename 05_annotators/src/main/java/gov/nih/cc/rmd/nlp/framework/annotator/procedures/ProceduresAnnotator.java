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
 * ProceduresAnnotator labels proceduress from text.
 * 
 * This annotator converts clinical statements that 
 * have a semantic type from the following 
 * Therapeutic or Preventive Procedure = T061 = topp
 *
 * This is built directly upon UIMA's implementation of
 * an annotator - JCasAnnotator_ImplBase.  
 * 
 * This class creates annotations that get defined 
 * from a uima type descriptor found in the
 * 06_type.descriptor/src/main/resources/com/ciitizen/framework/ProceduresModel.xml
 *
 * This class refers to annotations that got defined
 * in the framework-type.descriptors project/repo
 *
 * @author  Guy Divita 
 * @created Jun 1, 2018
 *
 * 
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.procedures;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.Procedure;

import gov.va.chir.model.ClinicalStatement;
import gov.va.chir.model.CodedEntry;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class ProceduresAnnotator extends JCasAnnotator_ImplBase  {
 

  // -----------------------------------------
  /**
   * process retrieves lines of the document, labels those that are questions
   * as QuestionAndAnswer elements.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
    this.performanceMeter.startCounter();
    
    // iterate through the clinical statements
    
    List<Annotation> clinicalStatements = UIMAUtil.getAnnotations(pJCas, ClinicalStatement.typeIndexID );
    
    if ( clinicalStatements != null && !clinicalStatements.isEmpty() ) 
      for ( Annotation clinicalStatement : clinicalStatements ) {
          List<CodedEntry> procedureConcepts = isThisAProcedure( pJCas, (ClinicalStatement) clinicalStatement) ;
          if ( procedureConcepts != null && !procedureConcepts.isEmpty())
            createProceduresAnnotation( pJCas, (ClinicalStatement) clinicalStatement, procedureConcepts );
      }
   
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  } // end Method process() ----------------
   
  


 // =================================================
   /**
    * isThisAProcedure sees if this clinical statement
    * contains a procedure semantic type
    * 
    * @param pJCas
    * @param pClinicalStatement
    * @return List<CodedEntry>  of codedEntry (UMLS Concepts)
   */
   // =================================================
  private final List<CodedEntry> isThisAProcedure( JCas pJCas, ClinicalStatement pClinicalStatement) {
     
    ArrayList<CodedEntry> procedureConcepts = null;
     FSArray codedEntriez =  pClinicalStatement.getCodedEntries();
     if ( codedEntriez != null && codedEntriez.size() > 0 ) {
   
       @SuppressWarnings("unchecked")
      List<Annotation> codedEntries = UIMAUtil.fSArray2List(pJCas, codedEntriez);
     
       if ( codedEntries != null && ! codedEntries.isEmpty()) {
        for ( Annotation codedEntry : codedEntries ) {
           if ( isThisAProcedure( pJCas, (CodedEntry) codedEntry )) {
            if ( procedureConcepts == null) procedureConcepts = new ArrayList<CodedEntry>();
              procedureConcepts.add( (CodedEntry) codedEntry);
           }
        }
       }
     }
    return procedureConcepts ;
   } // end Method isThisAProcedure() -----------------

     

  // =================================================
    /**
     * isThisAProcedure sees if this clinical statement
     * contains a procedure semantic type
     * 
     * @param pJCas
     * @param pCodedEntry
     * @return boolean
    */
    // =================================================
   private final boolean isThisAProcedure( JCas pJCas,  CodedEntry pCodedEntry) {
      
     boolean returnVal = false;
     
     StringArray semanticTypez = pCodedEntry.getSemanticType();
     String[] semanticTypes = null;
    if ( semanticTypez != null && semanticTypez.size() > 0) {
       semanticTypes = UIMAUtil.stringArrayToArrayOfString(semanticTypez);

       if ( semanticTypes != null && semanticTypes.length > 0 ) {
         for ( String semanticType: semanticTypes)
           if ( semanticType != null && 
           semanticType.equals("topp") || semanticType.equals("T061")) {
             returnVal = true;
             break;
           }
       }
    }
      
     
     return returnVal;
    } // end Method isThisAProcedure() -----------------

//-----------------------------------------
 /**
  * createProceduresAnnotation  creates an annotation for an instance of a procedures found.
  * 
  * @param pJCas
  * @param pClinicalStatement
  * @param pCodedEntries
  * 
  * @return ProceduresLabel1
  */
 // -----------------------------------------
 private final void createProceduresAnnotation( JCas pJCas,  ClinicalStatement pClinicalStatement, List<CodedEntry> pCodedEntries)  {
  
   Procedure statement = new Procedure( pJCas);
  
  /*  these are the things that ctakes keeps track of
  statement.getBodyLaterality()
  statement.getBodyLocation()
  statement.getBodySide()
  statement.getDuration()
  statement.getEndTime()
  statement.getProcedureDevice()
  */
  
  statement.setBegin(                    pClinicalStatement.getBegin());
  statement.setEnd(                      pClinicalStatement.getEnd());
 
  
  statement.setAssertionStatus( ((ClinicalStatement) pClinicalStatement).getNegation_Status());
  statement.setHistoricalStatus(((ClinicalStatement) pClinicalStatement).getHistorical());
  statement.setConditionalStatus(((ClinicalStatement) pClinicalStatement).getConditional());
  statement.setSubjectStatus(((ClinicalStatement) pClinicalStatement).getSubject());
//((ClinicalStatement) pClinicalStatement).getHypothetical();
  
  String conceptNames = getCuisFromCodedEntries( pCodedEntries);
  String cuis = getCuisFromCodedEntries( pCodedEntries);
  
  statement.setConceptNames(conceptNames);
  statement.setCuis(cuis);
  statement.setCategories("topp");
  statement.setSectionName( pClinicalStatement.getSectionName());
  
  // statement.setHypothetical();
  
  statement.setId("ProceduresAnnotator_" + this.annotationCtr++);
  statement.addToIndexes(pJCas);
 

 } //end Method createProceduresAnnotation() ---

 
 // =================================================
    /**
     * getCuisFromCodedEntries 
     * 
     * @param pCodedEntries
     * @return String (colon delimited cuis )
    */
    // =================================================
 private final String getCuisFromCodedEntries(List<CodedEntry> pCodedEntries) {
      
   String returnVal = null;
   ArrayList<String> buff = new ArrayList<String>();
   
   if ( pCodedEntries != null && !pCodedEntries.isEmpty()) {
     for ( CodedEntry codedEntry : pCodedEntries )
       buff.add( codedEntry.getCodeCode());
   
     returnVal = U.list2String(buff,  ':');
   }
   return returnVal;
 } // end Method getCuisFromCodedEntries() ----------

//=================================================
 /**
  * getConceptNamesFromCodedEntries 
  * 
  * @param pCodedEntries
  * @return String (colon delimited conceptNames )
 */
 // =================================================
private final String getConceptNamesFromCodedEntries(List<CodedEntry> pCodedEntries) {
   
String returnVal = null;
ArrayList<String> buff = new ArrayList<String>();

if ( pCodedEntries != null && !pCodedEntries.isEmpty()) {
  for ( CodedEntry codedEntry : pCodedEntries )
    buff.add( codedEntry.getDisplayName());

  returnVal = U.list2String(buff,  ':');
}
return returnVal;
} // end Method getCuisFromCodedEntries() ----------





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
   *  initialize    This is the standard uima way to pass parameters to an annotator.
   *                It is cumbersome.  It requires creating a config file with params
   *                in it, making it difficult to dynamically pass in parameters. 
   *                
   *                This method merges the uima way and keeping the ability to dynamically
   *                pass parameters into the class via - putting all parameters in a string
   *                array called "args" with each row containing a --key=value format.
   *                This way, arguments could be directly passed from command line,
   *                or read from a config file, or dynamically added to that string
   *                passed in. 
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   * 
   * @param aContext
   * @throws ResourceInitializationException
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
     
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  

        initialize(args);
        
      } catch (Exception e ) {
        String msg = "Issue in initializing class " + this.getClass().getName() + " " + e.toString() ;
        GLog.println(GLog.ERROR_LEVEL, msg);     // <------ use your own logging here
        throw new ResourceInitializationException();
      }
      
  
  } // end Method initialize() -------
  
  //----------------------------------
  /**
   * initialize initializes the class.  Parameters are passed in via a String
   *                array  with each row containing a --key=value format.
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    this.proceduresParameter = U.getOption(pArgs,  "--termplateParam=", "aDefaultValue"); 
    
      
  } // end Method initialize() -------
  
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  private String  proceduresParameter = null;
  
} // end Class ProceduresAnnotator() ---------------
