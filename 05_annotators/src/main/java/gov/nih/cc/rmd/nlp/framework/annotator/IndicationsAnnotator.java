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
