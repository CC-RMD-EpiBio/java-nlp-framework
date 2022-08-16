// =================================================
/**
 * Antibiotic Annotator identifies antibiotic mentions
 * 
 * @author  Guy Divita 
 * @created Oct 31, 2017
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
import gov.va.vinci.model.Concept;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class AntibioticAnnotator extends JCasAnnotator_ImplBase {
 
 
  
  // -----------------------------------------
  /**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    this.performanceMeter.startCounter();
    
    try {
    	createEvidence( pJCas);
    } catch ( Exception e) {
    	e.printStackTrace();
    	System.err.println("Issue with antibiotic mention annotator " + e.getMessage());
    }
    
    
    this.performanceMeter.stopCounter();

  } // end Method process() ----------------
   
  
  // ==========================================
  /**
   * createEvidence 
   *
   * @param pJCas
   */
  // ==========================================
  private void createEvidence(JCas pJCas) {
	  
	
	
	
    List<Annotation> terms = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.LexicalElement.type);
    String semanticTypes = null;

    if (terms != null) {
      // -----------------------------------------------------
      // Loop through each term
      // ----------------------------------------------------
      for (Annotation term : terms ) {
    	  
		semanticTypes = ( (LexicalElement) term).getSemanticTypes();
        if ( semanticTypes != null && semanticTypes.trim().length()> 3 && 
        		!semanticTypes.trim().equals(":" ) &&
        		!semanticTypes.trim().equals("::" ) ) {
         
          String semanticType = validSemanticTypes( semanticTypes);
          if (semanticType != null  ) {
          
            
              // Create an annotation as long as the term is not in
              // a content Heading that is not part of question/answer slot/value structure
             // if ( !inSectionHeading(pJCas, term )) {
                try {
                  createAnnotation(pJCas, (LexicalElement) term, semanticType );
                } catch (Exception e) {
                  e.printStackTrace();
                  System.err.println("issue in creating an annotation " + e.toString());
                }
             // } // end if this is not in a section heading 
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
       statement.setId("AntibioticAnnotator" + this.counter++);
   
       
       String assertionStatus = pTerm.getNegation_Status();
       if ( assertionStatus == null ) assertionStatus = "Asserted";
         
       statement.setAssertionStatus(  assertionStatus );
       statement.setSubjectStatus(  pTerm.getSubject());
       statement.setConditionalStatus( pTerm.getConditional());
       statement.setHistoricalStatus( pTerm.getHistorical());
       
     
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
    * getAssertionStatus returns |Asserted|Negated|Conditional|SubjectIsOther|Historical|Hypothetical 
    *     
    * 
    * @param pTerm
    * @return String   returns |Asserted|Negated|Conditional|SubjectIsOther|Historical|Hypothetical 
    *
    */
   // ======================================================	
  private String getAssertionStatus(LexicalElement pTerm) {
    
    String returnVal = "Asserted";
    
     try {
    	
      String negation = pTerm.getNegation_Status();
      boolean conditional = pTerm.getConditional();
      String subject = pTerm.getSubject();
       if ( negation != null && negation.contains("Negated"))
         returnVal = "Negated";
       
       if ( subject != null && !subject.equals("Patient"))
         returnVal = "Other";
       
     
    
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method getAssertionStatus " + e.getMessage() );
        throw e;
      }
     
     return returnVal;
    } // End Method getAssertionStatus ============
    
  


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

			this.validSemanticTypes = new HashSet<String>(7);

			for (String semanticType : SemanticTypes)
				this.validSemanticTypes.add(semanticType);

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
  private HashSet<String>             validSemanticTypes = null;
  public static final String               SemanticTypez = "Antibiotic";
  public static final String[]             SemanticTypes = U.split(SemanticTypez,":");
  private int                                    counter = 0;
  
  
  
} // end Class MetaMapClient() ---------------
