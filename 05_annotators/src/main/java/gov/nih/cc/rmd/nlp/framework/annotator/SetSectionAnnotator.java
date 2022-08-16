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
 *    SetSectionAnnotator sets the sections on the clinical 
 *    statements and coded entries
 * 
 *
 * @author  Guy Divita 
 * @created Feb 21, 2018

 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.SectionZone;

import gov.va.chir.model.ClinicalStatement;
import gov.va.chir.model.CodedEntry;
import gov.va.chir.model.VAnnotation;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class SetSectionAnnotator extends JCasAnnotator_ImplBase {
    
    @Override
    // -----------------------------------------
    /**
     * process takes sentences and returns terms attached to that sentence.
     * 
     * @param aJCas
     */
    // -----------------------------------------
    public final void process(JCas pJCas) throws AnalysisEngineProcessException {

      this.performanceMeter.startCounter();
     
      try {
      
      List<Annotation> sections = UIMAUtil.getAnnotations(pJCas,  SectionZone.typeIndexID, true);
      
      if ( sections != null && !sections.isEmpty()) 
        
        for ( Annotation section : sections ) 
          setSectionsForClinicalStatements( pJCas, section);
        
        
      

     
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, "Issue setting the section for the clinical statements " + e.toString());
        throw new RuntimeException(e.toString());
        // throw a uima exception here.
      }
    
    
    this.performanceMeter.stopCounter();
   
    } // end Method process() ------------------


 
    // =================================================
    /**
     * setSectionsForClinicalStatements finds the clinical statements
     * within this section and set's the parent section for each
     * 
     * @param pJCas
     * @param section
    */
    // =================================================
   private final void setSectionsForClinicalStatements(JCas pJCas, Annotation pSection) {
      
     List<Annotation> clinicalStatements = UIMAUtil.getAnnotationsBySpan(pJCas, ClinicalStatement.typeIndexID, pSection.getBegin(), pSection.getEnd() );
      
     if ( clinicalStatements != null && !clinicalStatements.isEmpty())
       for ( Annotation clinicalStatement: clinicalStatements ) {
         setSectionForClinicalStatement( clinicalStatement, pSection);
       }
    } // end Method setSectionsForClinicalStatements() ---



    // =================================================
    /**
     * setSectionForClinicalStatement sets the parent section for
     * the clinical statement and any codedEntry within it as well
     * 
     * @param pClinicalStatement
     * @param pSection
    */
    // =================================================
    private final void setSectionForClinicalStatement(Annotation pClinicalStatement, Annotation pSection) {
     
      
      String sectionName = ((SectionZone) pSection).getSectionName();
      ((ClinicalStatement)  pClinicalStatement).setSection ( (VAnnotation) pSection );
      ((ClinicalStatement)  pClinicalStatement).setSectionName( sectionName );
      
      
    FSArray codedEntries = ((ClinicalStatement) pClinicalStatement).getCodedEntries();
    
    if (codedEntries != null && codedEntries.size() > 0 ) {
      for ( int i =  0; i < codedEntries.size(); i++ ) {
        CodedEntry codedEntry = (CodedEntry) codedEntries.get(i);
        codedEntry.setSection( (VAnnotation) pSection);
        codedEntry.setSectionName( sectionName );
      }
    }
    } // end Method setSectionForClinicalStatement() ---



    // ----------------------------------
    /**
     * destroy cleans up after the last invocation.
     * 
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void destroy() {
    
        this.performanceMeter.writeProfile( this.getClass().getSimpleName());
      
    } // end Method destroy() --------------



    //----------------------------------
    /**
     * initialize loads in the resources needed for term tokenization. Currently, 
     * this involves NLM's LRAGR table.   
     * 
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
     
      String[]          args = null;
       try {
         args                  = (String[]) aContext.getConfigParameterValue("args");  
        
         initialize( args);
    
      
       } catch (Exception e) {
   		e.printStackTrace();
   		GLog.println("Something went wrong with initializing sophia " + e.toString());
   		throw new ResourceInitializationException();
   	}
    
    
      
    } // end Method initialize() -------

    
    //----------------------------------
    /**
     * initialize loads in the resources needed for term tokenization. Currently, 
     * this involves NLM's LRAGR table.   
     * 
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(String[] pArgs) throws ResourceInitializationException {
    
    	
      this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
   
     
    } // end Method initialize() -------

    
  

    
 
    // -------------------------------------------
    // Class Variables
    // -------------------------------------------
  
   ProfilePerformanceMeter              performanceMeter = null;
    
    private int clinicalStatmentCounter = 0;
    
    
} // end Class SetSectionAnnotator() -----------
