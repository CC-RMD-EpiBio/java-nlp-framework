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
