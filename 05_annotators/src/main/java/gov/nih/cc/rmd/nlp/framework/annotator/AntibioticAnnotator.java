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
