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
 * FamilyMemersApplication 
 *
 *  Converts concepts with the semantic type t099 (FamilyMember) to the annotations 
 *  that is FamilyMember.  
 * @author  Guy Divita 
 * @created April 28, 2013
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.Diagnosis;

import gov.va.chir.model.CodedEntry;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class DiagnosisAnnotator extends JCasAnnotator_ImplBase {
 
  
 
  
  // -----------------------------------------
  /**
   * process 
   * 
   * @param pJCas
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    this.performanceMeter.startCounter();
   
    HashSet<String> snippetSet = new HashSet<String>();
    // retrieve coded entries if they exist
    List<Annotation> codedEntries = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.CodedEntry.typeIndexID );
    
    if ( codedEntries != null && codedEntries.size() > 0) {
      
      // ------------------------------
      // Retrieve just those codedEntries that are T099 (Family Member)
      
      for ( Annotation codedEntry: codedEntries ) {
        StringArray semanticTypez = ((CodedEntry) codedEntry).getSemanticType();
        if ( semanticTypez != null) {
          String semanticTypes = UIMAUtil.stringArrayToString(semanticTypez);
          
          // ---------------------------------------
          // Diagnosis Codes:  from paper:
          // 
          // "cgab|T019|Congenital Abnormality",
          // "acab|T020|Acquired Abnormality",
          // "inpo|T037|Injury or Poisoning",
          // "dsyn|T047|Disease or Syndrome",
          // "mobd|T048|Mental or Behavioral Dysfunction",
          // "comd|T049|Cell or Molecular Dysfunction",
          // "anab|T190|Anatomical Abnormality",
          // "neop|T191|Neoplastic Process",
         
          
         for ( String diagnosis : diagnosisTypes ) {
           if ( semanticTypes.contains(diagnosis )) {
             String key = codedEntry.getBegin() + "|" + codedEntry.getEnd();
            
             if ( !snippetSet.contains(key) ) {
               snippetSet.add( key);
               createDiagnosis(pJCas, codedEntry);
              
            } // end of if this is a unique diagnosis
            continue;
           } // end if found a diagnosis
            
         } // end loop through diagnosis types
          
        } // end if there are semantic types
      } // end of loop through coded entries
      
      
    } // we've got codedEntries
   
    this.performanceMeter.stopCounter();
    
  } // end Method process 


 
// ------------------------------------------
  /**
   * createDiagnosis
   *
   *
   * @param pJCas
   * @param pAnnotation
   */
  // ------------------------------------------
  private void createDiagnosis(JCas pJCas, Annotation pAnnotation) {
    
    Diagnosis diagnosis = new Diagnosis( pJCas);
    
    diagnosis.setBegin( pAnnotation.getBegin());
    diagnosis.setEnd(   pAnnotation.getEnd());
    diagnosis.addToIndexes();
    
    
  }  // End Method createFamilyMember() -----------------------
  


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
    * initialize loads in the resources needed for phrase chunking. 
    * 
    * @param aContext
    * 
    **/
   // ----------------------------------
   public void initialize(UimaContext aContext) throws ResourceInitializationException {
     
    String[] args = null;
     try {
       args                 = (String[]) aContext.getConfigParameterValue("args");  
       this.outputDir      =  U.getOption(args, "--outputDir=", "./");
       this.performanceMeter = new PerformanceMeter( this.outputDir + "/logs/profile_" + this.getClass().getSimpleName() + ".log"  );
      
    
      
     } catch (Exception e ) {
       
     }
     
   
     
   } // end initialize()

   // ---------------------------
   // Global Variables
   // ---------------------------
   
 
   private int annotationCounter = 0;  
   PerformanceMeter              performanceMeter = null;
   private String                       outputDir = null;

  
  private static final String diagnosisTypes[] = 
                                  { "cgab","T019","Congenital Abnormality",
                                    "acab","T020","Acquired Abnormality",
                                    "inpo","T037","Injury or Poisoning",
                                    "dsyn","T047","Disease or Syndrome",
                                    "mobd","T048","Mental or Behavioral Dysfunction",
                                    "comd","T049","Cell or Molecular Dysfunction",
                                    "anab","T190","Anatomical Abnormality" ,
                                    "neop","T191","Neoplastic Process" };

  /* consider these
  
  "acty|T052|Activity",
  "bhvr|T053|Behavior",
  "biof|T038|Biologic Function",
  "dora|T056|Daily or Recreational Activity",
  "fndg|T033|Finding",
  "menp|T041|Mental Process",
 
 
  "npop|T070|Natural Phenomenon or Process",
  "ocac|T057|Occupational Activity",
  "patf|T046|Pathologic Function",
  "phpr|T067|Phenomenon or Process",
  "phsf|T039|Physiologic Function",
  
  
  
  "socb|T054|Social Behavior",
  "sosy|T184|Sign or Symptom",
  "bhvr|T053|Behavior",
  
  */
  
  
} // end Class DiagnosisAnnotator() ---------------
