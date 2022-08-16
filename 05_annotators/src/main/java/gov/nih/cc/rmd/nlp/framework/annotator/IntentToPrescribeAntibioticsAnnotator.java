// =================================================
/**
 * IntentToPrescribeAntibioticAnnotator identifies antibiotic mentions that are intents
 * 
 * @author  Guy Divita 
 * @created Oct 31, 2017
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Sentence;
import gov.va.vinci.model.Antibiotic;
import gov.va.vinci.model.AssertedEvidence;
import gov.va.vinci.model.ConditionalEvidence;
import gov.va.vinci.model.False;
import gov.va.vinci.model.FamilyHistoryEvidence;
import gov.va.vinci.model.HistoricalEvidence;
import gov.va.vinci.model.IntentToPrescribeAntibiotics;
import gov.va.vinci.model.NegationEvidence;
import gov.va.vinci.model.SubjectIsOtherEvidence;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class IntentToPrescribeAntibioticsAnnotator extends JCasAnnotator_ImplBase {
 
 
  
  // -----------------------------------------
  /**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    this.performanceMeter.startCounter();
    
    try {
      
      // Loop thru sections
        // loop thru utterances
          
      
      List<Annotation> antibiotics = UIMAUtil.getAnnotations(pJCas, Antibiotic.typeIndexID );
      
      if ( antibiotics != null && !antibiotics.isEmpty()) {
     
        for ( Annotation antibiotic : antibiotics ) {
          List<Annotation> sentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,Sentence.typeIndexID, antibiotic.getBegin(), antibiotic.getEnd() );
          
          if ( sentences != null && !sentences.isEmpty() ) {
            for ( Annotation sentence: sentences ) {
              boolean negativeEvidence = processSentenceNegativeEvidence( pJCas, sentence, antibiotic);
              if ( !negativeEvidence )
                processSentencePosiveEvidence ( pJCas, sentence, antibiotic);
            }
            
          } else {
            // antibiotic mention is not in a sentence
            ArrayList<Annotation> antibiotics_mentionList = new ArrayList<Annotation>(1);
            antibiotics_mentionList.add( antibiotic);
            createIntentToPrescribeAntibiotics(pJCas, antibiotics_mentionList);
          }
        
        } // end loop thru antibiotic mentions       
        
      }
      
     
    } catch ( Exception e) {
    	e.printStackTrace();
    	System.err.println("Issue with antibiotic mention annotator " + e.getMessage());
    }
    
    
    this.performanceMeter.stopCounter();

  } // end Method process() ----------------
   
//=======================================================
  /**
   * processSentence looks for negative evidence to make
   * a true negative
   * 
   * @param pJCas
   * @param pSentence 
   * @param pAntibiotic
   *
   */
  // ======================================================  
 private boolean processSentencePosiveEvidence(JCas pJCas, Annotation pSentence, Annotation pAntibiotic) {
   
   boolean negativeFound = false;
    try {
   
      List<Annotation>positiveEvidences = UIMAUtil.fuzzyFindAnnotationsBySpan( pJCas, AssertedEvidence.typeIndexID, pSentence.getBegin(), pSentence.getEnd());
    
      if ( positiveEvidences == null  ) {
        positiveEvidences = new ArrayList<Annotation>(1);
      }
       
       
         positiveEvidences.add( pAntibiotic);
         UIMAUtil.sortByOffset(positiveEvidences);
         createIntentToPrescribeAntibiotics( pJCas, positiveEvidences);
         negativeFound = true;
      
      
      
     } catch ( Exception e ) {
       e.printStackTrace();
       System.err.println("Issue within method processSentence " + e.getMessage() );
       throw e;
     }
    
     return negativeFound;
   } // End Method processSentence ============
   
 
  
  
  // =======================================================
   /**
    * processSentence looks for negative evidence to make
    * a true negative
    * 
    * @param pJCas
    * @param pSentence 
    * @param pAntibiotic
    *
    */
   // ======================================================	
  private boolean processSentenceNegativeEvidence(JCas pJCas, Annotation pSentence, Annotation pAntibiotic) {
    
    boolean negativeFound = false;
     try {
    
       List<Annotation>negationEvidences = UIMAUtil.fuzzyFindAnnotationsBySpan( pJCas, NegationEvidence.typeIndexID, pSentence.getBegin(), pSentence.getEnd());
       List<Annotation>conditionalEvidences = UIMAUtil.fuzzyFindAnnotationsBySpan( pJCas, ConditionalEvidence.typeIndexID, pSentence.getBegin(), pSentence.getEnd());
       List<Annotation>subjectEvidences = UIMAUtil.fuzzyFindAnnotationsBySpan( pJCas, SubjectIsOtherEvidence.typeIndexID, pSentence.getBegin(), pSentence.getEnd());
       List<Annotation>historicalEvidences = UIMAUtil.fuzzyFindAnnotationsBySpan( pJCas, HistoricalEvidence.typeIndexID, pSentence.getBegin(), pSentence.getEnd());
       List<Annotation>familyHistoryEvidences = UIMAUtil.fuzzyFindAnnotationsBySpan( pJCas, FamilyHistoryEvidence.typeIndexID, pSentence.getBegin(), pSentence.getEnd());
       
       
       List<Annotation>combinedEvidences = combineEvidences( negationEvidences, conditionalEvidences, subjectEvidences, historicalEvidences, familyHistoryEvidences);
       
       if ( combinedEvidences != null && !combinedEvidences.isEmpty() ) {
          combinedEvidences.add( pAntibiotic);
          UIMAUtil.sortByOffset(combinedEvidences);
          createTrueNegative( pJCas, combinedEvidences);
          negativeFound = true;
       }
       
       
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method processSentence " + e.getMessage() );
        throw e;
      }
     
      return negativeFound;
    } // End Method processSentence ============
    
  


  // =======================================================
   /**
    * createTrueNegative creates an annotation around the whole thing
    * 
    * @param pJCas
    * @param pCombinedEvidences
    * @param pAntibiotic void
    *
    */
   // ======================================================	
  private void createTrueNegative(JCas pJCas, List<Annotation> pCombinedEvidences ) {
    
    
     try {
    
       False tn = new False( pJCas);
       
       Annotation first = pCombinedEvidences.get(0);
       Annotation last = pCombinedEvidences.get( pCombinedEvidences.size() -1);
       
       tn.setBegin( first.getBegin());
       tn.setEnd( last.getEnd());
       tn.setAssertionStatus("Negated");
       
       tn.setAnnotationId("IntentToPrescribe_" + counter);
       tn.addToIndexes();
       
       
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method createTrueNegative " + e.getMessage() );
        throw e;
      }
    } // End Method createTrueNegative ============
    

  // =======================================================
   /**
    * createTrueNegative creates an annotation around the whole thing
    * 
    * @param pJCas
    * @param pCombinedEvidences
    * @param pAntibiotic void
    *
    */
   // ======================================================  
  private void createIntentToPrescribeAntibiotics(JCas pJCas, List<Annotation> pCombinedEvidences ) {
    
    
     try {
    
       IntentToPrescribeAntibiotics tn = new IntentToPrescribeAntibiotics( pJCas);
       
       Annotation first = pCombinedEvidences.get(0);
       Annotation last = pCombinedEvidences.get( pCombinedEvidences.size() -1);
       
       tn.setBegin( first.getBegin());
       tn.setEnd( last.getEnd());
       tn.setAssertionStatus("Asserted");
       tn.setInProse(false);
       tn.setId("IntentToPrescribe_Antibiotics_" + counter);
       tn.addToIndexes();
       
       
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method createTrueNegative " + e.getMessage() );
        throw e;
      }
    } // End Method createTrueNegative ============
    
  



  // =======================================================
   /**
    * combineEvidences 
    * 
    * @param negationEvidences
    * @param conditionalEvidences
    * @param subjectEvidences
    * @param historicalEvidences
   * @param familyHistoryEvidences 
    * @return List<Annotation>
    *
    */
   // ======================================================	
  private List<Annotation> combineEvidences(List<Annotation> negationEvidences, List<Annotation> conditionalEvidences,
      List<Annotation> subjectEvidences, List<Annotation> historicalEvidences, List<Annotation> familyHistoryEvidences) {
  
     List<Annotation> combinedEvidences = new ArrayList<Annotation>();
     
     try {
    	
       if ( negationEvidences    != null ) for (Annotation annotation : negationEvidences )     combinedEvidences.add( annotation );
       if ( conditionalEvidences != null ) for (Annotation annotation : conditionalEvidences )  combinedEvidences.add( annotation );
       if ( subjectEvidences     != null ) for (Annotation annotation : subjectEvidences )      combinedEvidences.add( annotation );
       if ( historicalEvidences  != null ) for (Annotation annotation : historicalEvidences )   combinedEvidences.add( annotation );
       if ( familyHistoryEvidences  != null ) for (Annotation annotation : familyHistoryEvidences )   combinedEvidences.add( annotation );
        
       if ( combinedEvidences != null && !combinedEvidences.isEmpty())
         UIMAUtil.sortByOffset( combinedEvidences);
       else 
         combinedEvidences = null;
         
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method combineEvidences " + e.getMessage() );
        throw e;
      }
     return combinedEvidences ;
  } // End Method combineEvidences ============
    
  



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
  






//----------------------------------
/**
 * destroy
* 
 **/
// ----------------------------------
public void destroy() {
  this.performanceMeter.writeProfile( this.getClass().getSimpleName());
}

	// ----------------------------------
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
			args = (String[]) aContext.getConfigParameterValue("args");

			initialize(args);

		} catch (Exception e) {
			System.err.println(
					"Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
			throw new ResourceInitializationException();
		}

	} // end Method Initialize() --------

	// ----------------------------------
	/**
	 * initialize loads in the resources.
	 * 
	 * @param pArgs
	 * 
	 **/
	// ----------------------------------
	public void initialize(String[] pArgs) throws ResourceInitializationException {

		try {

			this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getSimpleName());

			

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Issue initalizing antibiotic annotator " + e.toString());
			throw new ResourceInitializationException();
		}

	} // end Method initialize() -------

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  private ProfilePerformanceMeter       performanceMeter = null;
 
  
  private int                                    counter = 0;
  
  
  
} // end Class MetaMapClient() ---------------
