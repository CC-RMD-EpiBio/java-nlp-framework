// =================================================
/**
 * IndicationsAnnotator
 * 
 * Lexical lookup everything in the Indications Section
 * Create Indications for the terms (except follow-up, for)
 

 * @author  Guy Divita 
 * @created March 22, 2017
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

import gov.va.chir.model.ContentHeading;
//import gov.va.chir.model.IndicationsSection;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.MinimalPhrase;
import gov.va.chir.model.PartOfSpeech;
import gov.va.chir.model.Section;
import gov.va.chir.model.VAnnotation;
//import gov.va.vinci.model.Indication;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;




public class IndicationsAnnotator extends JCasAnnotator_ImplBase {
 
 
  
  // -----------------------------------------
  /**
   * process
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    this.performanceMeter.startCounter();

    setMarked(pJCas, false);
    
    try {

      List<Annotation> sections = null;//UIMAUtil.getAnnotations(pJCas, IndicationsSection.typeIndexID);

      if (sections != null && !sections.isEmpty()) {
        // iterate thru the indication sections of the document
        for (Annotation section : sections) {
          markIndicationSectionHeadings(pJCas, section);
          processIndicationsSectionPhrase(pJCas, section);
          markAllAnnotationsWithinSection(pJCas, section);
        } // end loop thru sections
      } // end if there are sections

      
      filterOutUnNeededTerms( pJCas);
      
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with finding the section " + e.toString());

    }
    this.performanceMeter.stopCounter();

  } // =======================
  
  

  // =======================
  /**
   * markAllAnnotationsWithinSection [Summary]
   *
   * @param pJCas
   * @param pSection
   */
  // =======================
  private void markAllAnnotationsWithinSection(JCas pJCas, Annotation pSection) {
   
    List<Annotation> annotations = UIMAUtil.getAnnotationsBySpan(pJCas, pSection.getBegin(), pSection.getEnd() );
    
    if ( annotations != null && !annotations.isEmpty()) {
      for ( Annotation annotation : annotations ) {
        try {
          ((VAnnotation)annotation).setMarked(true);
        } catch (Exception e) {
          
        }
      }
    }
    
    } // End Method markAllAnnotationsWithinSection =======
  



  // =======================
  /**
   * markIndicationSectionHeadings [Summary]
   *
   * @param pJCas
   * @param section
   */
  // =======================
  private void markIndicationSectionHeadings(JCas pJCas, Annotation pSection) {
   
    ContentHeading heading = ((Section)pSection).getSectionHeading();
    if ( heading != null )
      heading.setMarked(true);
    
    } // End Method markIndicationSectionHeadings =======
  



  // =======================
  /**
   * filterOutUnNeededTerms [Summary]
   *
   * @param pJCas
   */
  // =======================
  private void filterOutUnNeededTerms(JCas pJCas) {
  List<Annotation> allTerms = UIMAUtil.getAnnotations(pJCas, VAnnotation.typeIndexID, true);
    
    if ( allTerms != null && !allTerms.isEmpty()) {
      for ( Annotation term: allTerms ) {
        
        if ( !((VAnnotation) term).getMarked() )
          term.removeFromIndexes();
      }
    } 
    } // End Method filterOutUnNeededTerms =======
  



  // =======================
  /**
   * setMarked sets the marked to pValue
   *
   * @param pJCas
   * @param b
   */
  // =======================
  private void setMarked(JCas pJCas, boolean pValue) {
    List<Annotation> allTerms = UIMAUtil.getAnnotations(pJCas, VAnnotation.typeIndexID, true);
    
    if ( allTerms != null && !allTerms.isEmpty()) {
      for ( Annotation term: allTerms ) {
        ((VAnnotation) term).setMarked( pValue);
      }
   }
    
  
    
   } // End Method setMarked =======
  



  // =======================
  /**
   * processIndicationsSection 
   *
   * @param pJCas
   * @param pSection
   * 
   */
  // =======================
  private void  processIndicationsSection(JCas pJCas, Annotation pSection) {
   
   
    List<Annotation> sectionTerms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSection.getBegin(), pSection.getEnd());
 
    if ( sectionTerms != null && !sectionTerms.isEmpty() ){
      
      for ( Annotation term : sectionTerms ) {
        
       String semanticTypes = ((LexicalElement)term ).getSemanticTypes();
       String termString = term.getCoveredText();
           if ( !this.badWords.contains( termString  ) ||
            
               semanticTypes == null || !semanticTypes.contains("NotIndication")             
           ) {
         
//          createIndication(pJCas, (LexicalElement) term);
//          ((LexicalElement)term).setMarked( true );
//          ((IndicationsSection)pSection).setMarked(true);
        }
      } // end loop thru terms of the section
    } // end if there are any terms 
    } // End Method processIndicationsSection =======
  

  // =======================
  /**
   * processIndicationsSection 
   *
   * @param pJCas
   * @param pSection
   * 
   */
  // =======================
  private void  processIndicationsSectionPhrase(JCas pJCas, Annotation pSection) {
   
   
    List<Annotation> sectionPrhases = UIMAUtil.getAnnotationsBySpan(pJCas, MinimalPhrase.typeIndexID, pSection.getBegin(), pSection.getEnd());
 
    if ( sectionPrhases != null && !sectionPrhases.isEmpty() ){
      
      for ( Annotation term : sectionPrhases ) {
        
      // String semanticTypes = ((LexicalElement)term ).getSemanticTypes();
       String termString = term.getCoveredText();
       //if ( !this.badWords.contains( termString  ) ){
      
//          createIndication(pJCas,  term);
//          ((MinimalPhrase)term).setMarked( true );
//          ((IndicationsSection)pSection).setMarked(true);
       // }
      } // end loop thru terms of the section
    } // end if there are any terms 
    } // End Method processIndicationsSection =======
  
  
  
  

  // =======================
  /**
   * getPOS 
   *
   * @param term
   * @return String
   */
  // =======================
  private String getPOS(LexicalElement term) {
   
    String returnVal = "NN";
    
    PartOfSpeech poss = term.getPartOfSpeech();
    if ( poss != null )
      returnVal = poss.getPos();
    
    
    return returnVal;
    
    
    } // End Method getPOS =======




  // =======================
  /**
   * processIndicationSection looks for terms in this section
   * and turns them into "Indication"
   *
   * @param pJCas
   * @param pTerm
   */
  // =======================
//  private Indication createIndication(JCas pJCas, Annotation pTerm) {
//   
//    Indication concept = new Indication(pJCas);
//    concept.setBegin( pTerm.getBegin());
//    concept.setEnd(   pTerm.getEnd() );
//    
//  //  String cuis = getCuis( pTerm );
//   // String semanticTypes = pTerm.getSemanticTypes();
//    
//    String assertionStatus = VUIMAUtil.getAssertionStatus(pJCas, pTerm);
//    concept.setAssertionStatus( assertionStatus );
//   // concept.setConditionalStatus( pTerm.getConditional());
//    //concept.setHistoricalStatus( pTerm.getHistorical());
//   // concept.setSubjectStatus( pTerm.getSubject());
//   
//    
//    //concept.setCuis( cuis );
//    concept.setCategories( "Indication" ); // :" + semanticTypes);
//    concept.setSectionName("Indications");
//    concept.setId("IndicationAnnotator_" + this.counter++);
//    concept.addToIndexes();
//   
//    
//    return concept;
//    } // End Method createIndicationsSection =======
  
// =======================
  /**
   * getCuis 
   *
   * @param pTerm
   * @return String colon delimited cui's
   */
  // =======================
  private String getCuis(LexicalElement pTerm) {
    
    StringArray cuiz = pTerm.getEuis();
    String[] cuizz = cuiz.toArray();
    StringBuffer cuizzz = new StringBuffer();
    for ( String cui: cuizz)
      cuizzz.append(cui + ":");
    
    String buff = cuizzz.toString();
    int p = buff.lastIndexOf(":");
    String returnVal = buff.substring(0, p);
    
    return returnVal;
    } // End Method getCuis =======
  



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
      
      initialize (args );

    } catch (Exception e ) {
      System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
    
  } // end Method Initialize() ---------
    

    //----------------------------------
    /**
     * initialize loads in the resources. 
     * 
     * @param pArgs
     * 
     **/
    // ----------------------------------
    public void initialize( String[] pArgs) throws ResourceInitializationException {
     
   
    this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getCanonicalName()    );
    
    this.badWords = new HashSet();
    this.badWords.add("of");
    this.badWords.add("the");
    this.badWords.add("up");
    this.badWords.add("follow");
   
  } // end Method initialize() -------
  

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  ProfilePerformanceMeter              performanceMeter = null;
  private int                   counter = 0;
  private HashSet<String> badWords = null;
  
  
  
} // end Class MetaMapClient() ---------------
