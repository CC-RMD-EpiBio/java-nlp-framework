// =================================================
/**
 * ProblemAnnotator 
 *
 *  Converts concepts with the semantic types that would indicate a problem
 *  into problem annotations 
 *  
 *   // Problem Codes:  from paper:
  * 
  * "cgab|T019|Congenital Abnormality",
  * "acab|T020|Acquired Abnormality",
  * "inpo|T037|Injury or Poisoning",
  * "dsyn|T047|Disease or Syndrome",
  * "mobd|T048|Mental or Behavioral Dysfunction",
  * "comd|T049|Cell or Molecular Dysfunction",
  * "anab|T190|Anatomical Abnormality",
  * "neop|T191|Neoplastic Process",
 *
 * @author  Guy Divita 
 * @created Jan 06, 2014
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;

import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.SectionZone;

import gov.va.chir.model.CodedEntry;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.Problem;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class ProblemAnnotator extends  JCasAnnotator_ImplBase {
 
  
 //-----------------------------------------
 /**
  * process finds all problem mentions,
  *    weeds out findings that are normal (but keeps findings that are always abnormal)
  *   
  * 
  * @param pJCas
  * 
  */
 // -----------------------------------------
 public void process (JCas pJCas) throws AnalysisEngineProcessException {
  
   
   // Loop through sections likely to have problems  -- ignore other sections
   
   if ( this.ignoreSections ) {
     
   
    processSectionProblems( pJCas, null );
     
   } else {
     List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, SectionZone.typeIndexID);
   
   
     if ( sections != null && !sections.isEmpty() )
       for (Annotation aSection : sections ) 
         if ( processSection( aSection ) ) 
           processSectionProblems( pJCas, aSection );
         
   }
  
 } // end method process() -------------------------- 
 


// =================================================
/**
 * processSection determines if this section will have
 * problems in it.
 * 
 * @param pSection
 * @return boolean
*/
// =================================================

private boolean processSection(Annotation pSection) {
  
  boolean returnVal = false;
  
  String sectionName = ((SectionZone)pSection).getSectionName(); 
  if (sectionName != null ) {
    sectionName = sectionName.toLowerCase();
    returnVal = this.problemSectionList.contains( sectionName);
  }
  
  return returnVal;
 
} // end Method process() -------------------
  // -----------------------------------------


 

 
 // =================================================
  /**
   * processProblems  will find all problem mentions
   *    If the seeNotProblems is true all problems 
   *    are returned.  If not, only asserted problems
   *    are returned
   * 
   * @param pJCas
   */
//=================================================
  private final List<Annotation> processSectionProblems (JCas pJCas, Annotation pSection) throws AnalysisEngineProcessException {
   
    ArrayList<Annotation> problems = null;
    List<Annotation> codedEntries = null;
    try {

      HashMap<String, Problem> problemAnnotationHash = new HashMap<String, Problem>();

     
      if ( pSection == null )
        codedEntries = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.CodedEntry.typeIndexID );
        
      else
        codedEntries = UIMAUtil.getAnnotationsBySpan(pJCas, gov.va.chir.model.CodedEntry.typeIndexID, pSection.getBegin(), pSection.getEnd());

      processCodedEntries( pJCas, problemAnnotationHash, codedEntries);
      
      Collection<Problem> problemz = problemAnnotationHash.values();
      if (!problemz.isEmpty()) {
        problems = new ArrayList<Annotation>();
        problems.addAll(problemz);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue in problem :" + e.toString());
      throw new AnalysisEngineProcessException();
    }
    return problems;
  } // End Method processProblems() ------------------
  
  
  // =================================================
/**
 * processCodedEntries 
 * 
 * @param pJCas
 * @param problemAnnotationHash
 * @param codedEntries
*/
// =================================================
 private final void processCodedEntries(JCas pJCas, HashMap<String, Problem> problemAnnotationHash, List<Annotation> codedEntries) {
 
   if (codedEntries != null && codedEntries.size() > 0)
    // ------------------------------
    // Retrieve just those codedEntries that are T099 (Family Member)
    for (Annotation codedEntry : codedEntries)
      processCodedEntry(pJCas,  codedEntry, problemAnnotationHash);
  
} // End processCodedEntries() ------------------------

  
  // =================================================
  /**
   * processCodedEntry
   * 
   * @param codedEntry
   * @param problemAnnotationHash
   */
  // =================================================
  private final void processCodedEntry(JCas pJCas, Annotation codedEntry, HashMap<String, Problem> problemAnnotationHash) {

    String negationStatus = ((CodedEntry) codedEntry).getNegation_Status();

    if (this.seeNotProblems || negationStatus == null || negationStatus.toLowerCase().equals("asserted")) {
      StringArray semanticTypez = ((CodedEntry) codedEntry).getSemanticType();
      if (semanticTypez != null) {
        String semanticTypes = UIMAUtil.stringArrayToString(semanticTypez);
        for (String problem : problemTypes) {
          if (semanticTypes.contains(problem) && !notAProblem(codedEntry)) {
            String key = codedEntry.getBegin() + "|" + codedEntry.getEnd();

            Problem aProblem = (Problem) problemAnnotationHash.get(key);
            if (aProblem == null) {
              problemAnnotationHash.put(key, createProblem(pJCas, codedEntry));

            } else { // end of if this is a unique problem
              addCuisToProblem(aProblem, (CodedEntry) codedEntry);
            }
            continue;
          } // end if found a problem
        } // end loop through problem types
      } // end if there are semantic types
    } // end if the concept was not negated
  } // end Method process () -----------------------------

 
// =======================================================
  /**
   * addCuisToProblem 
   * 
   * @param pProblem
   * @param pCodedEntry
   */
  // =======================================================
  private void addCuisToProblem(Problem pProblem, CodedEntry pCodedEntry) {
   
    String cuis       = pProblem.getCuis();
    String names      = pProblem.getConceptNames();
    String categories = pProblem.getCategories();
    
    String newCui          = getCuiFromCodedEntry         ( pCodedEntry);
    String newName         = getConceptNamesFromCodedEntry( pCodedEntry);
    String newCategories   = getCategoriesFromCodedEntry  ( pCodedEntry);
    
    if ( uniqString( newCui, cuis ))
        pProblem.setCuis( cuis + "|" + newCui );
    
    if ( uniqString ( newName, names ))
      pProblem.setConceptNames(names + "|" + newName );
    
    String[] categoriez = U.split(newCategories);
    if ( categoriez != null ) {
      
      String categoryList = categories;
      for ( String category : categoriez ) { 
    
        if ( uniqString( category, categoryList ) )
          categoryList = categoryList + "|" + category;
      }
      pProblem.setCategories(categoryList);
    }
    
    
  } // End Method addCuisToProblem() ======================
  



  // =======================================================
  /**
   * uniqString 
   * 
   * @param pNewString
   * @param pList
   * @return boolean
   */
  // =======================================================
  private boolean uniqString(String pNewString, String pList) {
   boolean returnVal = true;
   
   if ( pList != null && pNewString != null &&  pList.contains( pNewString) )
     returnVal = false;
       
   return returnVal;
  }  // End Method uniqString() ======================
  



  // =======================================================
  /**
   * notAProblem looks up the problem in a table of known not probems.
   * 
   * @param codedEntry
   * @return boolean
   */
  // =======================================================
  private boolean notAProblem(Annotation codedEntry) {
   
    boolean returnVal = false;
    
    String surfaceForm = codedEntry.getCoveredText().toLowerCase().trim();
    
    if ( this.notAProblemList.contains( surfaceForm )) 
      returnVal = true;
    
    return returnVal;
    
  }  // End Method notAProblem() ======================
  



// ------------------------------------------
  /**
   * createDiagnosis
   *
   *
   * @param pJCas
   * @param pAnnotation
   * @return Problem
   */
  // ------------------------------------------
  private Problem createProblem(JCas pJCas, Annotation pAnnotation) {
    
    Problem problem = new Problem( pJCas);
    
    problem.setBegin( pAnnotation.getBegin());
    problem.setEnd(   pAnnotation.getEnd());
    
    
    problem.setAssertionStatus( ((VAnnotation)pAnnotation).getNegation_Status() );
    problem.setConditionalStatus(     ((VAnnotation)pAnnotation).getConditional()  );
    
    problem.setSubjectStatus(         ((VAnnotation)pAnnotation).getSubject());
    problem.setCategories(      getCategoriesFromCodedEntry(  (CodedEntry)  pAnnotation) );
    problem.setConceptNames(    getConceptNamesFromCodedEntry((CodedEntry)  pAnnotation) );
    problem.setCuis(            getCuiFromCodedEntry(         (CodedEntry)  pAnnotation) );
    problem.setSectionName(     ((CodedEntry) pAnnotation).getSectionName());
    problem.setOtherInfo(       getOtherFeaturesFromCodedEntry((CodedEntry) pAnnotation) );
   
    
   
    
    problem.addToIndexes();
    
    return problem;
    
  }  // End Method createFamilyMember() -----------------------
  



// =======================================================
  /**
   * getOtherFeaturesFromCodedEntry 
   * 
   * @param pAnnotation
   * @return String
   */
  // =======================================================
  private String getOtherFeaturesFromCodedEntry(CodedEntry pAnnotation) {
    
    String otherFeatures = null;
    
    StringArray otherFeaturez = pAnnotation.getOtherFeatures();
    if ( otherFeaturez != null) {
       otherFeatures = UIMAUtil.stringArrayToString(otherFeaturez);
      
    }
     
    return otherFeatures;
  }  // End Method getOtherFeaturesFromCodedEntry() ======================
  



  // =======================================================
  /**
   * getCuiFromCodedEntry 
   * 
   * @param pAnnotation
   * @return String
   */
  // =======================================================
  private String getCuiFromCodedEntry(CodedEntry pAnnotation) {
    String cui = null;
    
    cui = pAnnotation.getCodeCode();
    
    return cui;
  } // End Method getCuiFromCodedEntry() ======================
  



  // =======================================================
  /**
   * getConceptNamesFromCodedEntry 
   * 
   * @param pAnnotation
   * @return String
   */
  // =======================================================
  private String getConceptNamesFromCodedEntry(CodedEntry pAnnotation) {
    String conceptName = pAnnotation.getDisplayName();
    return conceptName;
  } // End Method getConceptNamesFromCodedEntry() ======================
  



// =======================================================
/**
 * getCategoriesFromCodedEntry 
 * 
 * @param pAnnotation
 * @return String
 */
// =======================================================
private String getCategoriesFromCodedEntry(CodedEntry pAnnotation) {
  
  String semanticTypes = null;
  
  StringArray semanticTypez = pAnnotation.getSemanticType();
  if (semanticTypez != null)
    semanticTypes = UIMAUtil.stringArrayToString(semanticTypez);
  
  return semanticTypes;
  // End Method getCategoriesFromCodedEntry() ======================
}


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
       
     String[] args = (String[]) aContext.getConfigParameterValue("args");
     initialize ( args);
      
  } // end Method initialize() -------

//----------------------------------
/**
 * initialize reads in the wsd evidence file
 * 
 * @param pArgs
 *    called from the pipe method rather than
 * 
 * 
 **/
// ----------------------------------
public void initialize(String pArgs[] )   throws ResourceInitializationException {
 

  try {
  this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
  this.ignoreSections = Boolean.valueOf(U.getOption(pArgs,  "--ignoreSections=", "false"));
  this.seeNotProblems = Boolean.valueOf(U.getOption(pArgs,  "--seeNotProblems=", "false"));

  
  loadNotAProblemList();
  loadProblemSectionList();
  
  } catch (Exception e) {
    e.printStackTrace();
    String msg = "Issue with loading the notAProblemList" + e.getMessage() ;
    GLog.println(GLog.ERROR_LEVEL, msg );
    throw new ResourceInitializationException ();
  }
 
   
} // end Method initialize() -------------------------------
  

  // =======================================================
/**
 * loadNotAProblemList loads a list of known things that
 * are not problems. 
 * 
 * @throws Exception 
 * 
 */
// =======================================================
private void loadNotAProblemList() throws Exception {

    
  
  this.notAProblemList = new HashSet<String>();
  
    String notAProblemList = "resources/com/ciitizen/framework/problems/notAProblem.txt";
    String[] buff = U.readClassPathResourceIntoStringArray(notAProblemList);
    
    if (buff != null ) {
      for ( String row: buff) {
        if ( row!= null && row.trim().length() > 0 && !row.startsWith("#")) {
        
          this.notAProblemList.add(row.toLowerCase().trim());
        }
        
      }
        
    }

  } // End Method loadNotAProblemList() ======================

// =======================================================
/**
* loadProblemSectionList loads those sections to look into
* to find problems.  Like chief complaint,  allergies, 
* 
* @throws Exception 
* 
*/
//=======================================================
private void loadProblemSectionList() throws Exception {

   
 this.problemSectionList = new HashSet<String>();
 
   String problemSectionFile = "resources/com/ciitizen/framework/problems/problemSections.txt";
   String[] buff = U.readClassPathResourceIntoStringArray(problemSectionFile);
   
   if (buff != null ) {
     for ( String row: buff) {
       if ( row!= null && row.trim().length() > 0 && !row.startsWith("#")) {
       
         this.problemSectionList.add(row.toLowerCase().trim());
       }
       
     }
       
   }

 } // End Method loadNotAProblemList() ======================



  // --------------------------------------------
  // Global variables
  // --------------------------------------------
  ProfilePerformanceMeter    performanceMeter = null;
  private HashSet<String>     notAProblemList = null;
  private HashSet<String>  problemSectionList = null;
  private boolean              ignoreSections = false;
  private boolean              seeNotProblems = false;
 
  private static final String problemTypes[] = 
                                  { "cgab","T019","Congenital Abnormality",  // <---- *
                                    "acab","T020","Acquired Abnormality",  // <---- *
                                    "inpo","T037","Injury or Poisoning",   // <--- *
                                    "dsyn","T047","Disease or Syndrome",  // <--- *
                                    "mobd","T048","Mental or Behavioral Dysfunction", // <--- *
                                    "comd","T049","Cell or Molecular Dysfunction", // <---- *
                                    "anab","T190","Anatomical Abnormality" , //< ---- *
                                    "neop","T191","Neoplastic Process", // <-----*
                                    "patf","T046","Pathologic Function",  
                                    "sosy","T184","Sign or Symptom",
                                    "fndg","T033","Finding"
                                         
                                  };  


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
  
  
} // end Class MetaMapClient() ---------------
