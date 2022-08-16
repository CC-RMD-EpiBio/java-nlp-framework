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
 * CCDAPanelSectionHeaderAnnotator looks for panel names
 * and creates panel contentHeadings for them.
 * 
 * While this should be done via a lookup into Loinc to
 * get the panel name, there is not enough coverage within Loinc
 * to reliably do this. 
 * 
 * Instead, panel names are looked up by pattern - Panels
 * observed all have "Final report" followed by a date.
 * In addition, a fair number of these have "Panel" in the name.
 * 
 * Of those that do not, a large chunk of false positives (those
 * that have Final Report on the line, are procedures that are
 * CT or MRI scans.
 *   
 *  
 *   
 *
 * @author  Guy Divita 
 * @created Jan 08 2018
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.Sections;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.model.Date;

import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.PanelContentHeading;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;




public class CCDAPanelSectionHeaderAnnotator extends JCasAnnotator_ImplBase {
 
 
  public static final String annotatorName = CCDAPanelSectionHeaderAnnotator.class.getSimpleName();

  
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    
    this.performanceMeter.startCounter();
    
    try {
    
      List<Annotation> terms = UIMAUtil.getAnnotations(pJCas, LexicalElement.typeIndexID );
      
      if ( terms != null && !terms.isEmpty()) {
        UIMAUtil.sortByOffset(terms);
        
        // -----------------------------
        // look for terms that match the type ccdaSectionHeader that start on a line
        // -----------------------------
        for ( Annotation term: terms ) {
         
           if ( hasResultSectionEvidence( (LexicalElement) term)) {
             Annotation line = VUIMAUtil.getLine( pJCas, term);
             if ( endsWithDate(pJCas, term, line ) )
                 if ( !hasProcedureEvidence( pJCas, line ))
                   createPanelSectionHeading( pJCas, term, line);
          }
        }
        
      }
     
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue in CCDASectionHeader Annotator " + e.toString());
    }
    
    this.performanceMeter.stopCounter();     
    
  } // End Method process() ------------------
  
  // =================================================
  /**
   * endsWithDate returns true if the line ends with a date or date and time
   *   - a quicker comprimise - look for a date after the Final report tag.
   * 
   * @param pJCas
   * @param line
   * @return boolean
  */
  // =================================================
  private final boolean endsWithDate(JCas pJCas, Annotation pTerm, Annotation pLine) {
    boolean returnVal = false;
    
    Type DateType = UIMAUtil.getLabelType(pJCas, "gov.nih.cc.rmd.framework.model.Date");
    List<Annotation> things = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, DateType, Date.typeIndexID, pTerm.getEnd(), pLine.getEnd(), true);
    
    if ( things != null && !things.isEmpty()) {
      returnVal = true;
     
      
      
    }
    
    return returnVal;
  } // end Method endsWithDate () -------------------

  // =================================================
  /**
   * hasResultSectinEvidence returns true if the term's semantic type is
   *  resultSectionEvidence
   * 
   * @param pJCas
   * @return boolean
  */
  // =================================================
  private final boolean hasResultSectionEvidence( LexicalElement pTerm ) { 
      
    boolean returnVal = false;
    String termTypes = pTerm.getSemanticTypes();
  
    
    if ( termTypes != null && termTypes.contains("resultSectionEvidence")) 
        returnVal = true;   
      
      return returnVal;
    
  } // end Method hasResultSectinEvidence() ---------------------
  
  

  // =================================================
  /**
   * hasProcedureEvidence returns true if the term's semantic type is
   *  resultSectionEvidence
   * 
   * @param pJCas
   * @param pLine
   * @return boolean
  */
  // =================================================
  private final boolean hasProcedureEvidence( JCas pJCas, Annotation pLine  ) { 
      
    boolean returnVal = false;
    List<Annotation> termsInLine = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  LexicalElement.typeIndexID,  pLine.getBegin(), pLine.getEnd());
    
    if ( termsInLine != null  && !termsInLine.isEmpty() )
      for ( Annotation term : termsInLine ) {
        String termTypes = ((LexicalElement) term).getSemanticTypes();
        if ( termTypes != null && termTypes.contains("proceduresSectionEvidence")) {
          returnVal = true;
          break;
        }
      }
  
    return returnVal;
    
  } // end Method hasProcedureSectionEvidence() ---------------------

  
  // =================================================
  /**
   * createSectionHeading creates a section heading
   *  (ContentHeading) if one does not already exist
   * 
   * @param pJCas
   * @param pSectionEvidence
   * @param pLine
  */
  // =================================================
    private  final void createPanelSectionHeading(JCas pJCas, Annotation pSectionEvidence, Annotation pLine) {
    
      
        if ( pLine.getBegin() >= pSectionEvidence.getBegin() -2)
          return;
      
        PanelContentHeading aContentHeading = new PanelContentHeading( pJCas);
        aContentHeading.setBegin(pLine.getBegin());
        aContentHeading.setEnd( pSectionEvidence.getBegin() -2 );
       
        aContentHeading.setId( "CCDAPanelSectionNameAnnotator_" + this.annotationCounter);
        
        ArrayList<String> otherFeatures = new ArrayList<String>();
        String sectionType = "not ccda Section";
        sectionType = ((LexicalElement)pSectionEvidence).getSemanticTypes();
        otherFeatures.add( sectionType );  // <----- this puts "resultSectionEvidence" on the sectionZone sectionType 
        
        String possibleAnnotations = whatKindOfThingsInThisSection( (LexicalElement) pSectionEvidence  );
        otherFeatures.add( possibleAnnotations);
        
        StringArray otherFeaturez = UIMAUtil.list2StringArray(pJCas, otherFeatures);
        aContentHeading.setOtherFeatures( otherFeaturez);
      
        
        aContentHeading.addToIndexes();
        aContentHeading.setSectionName( "Results:Panel:" + aContentHeading.getCoveredText() );
        
  } // end Method createSectionHeading() -------------



// =================================================
  /**
   * whatKindOfThingsInThisSection retreives the possible annotation types 
   * that can be found in this section type.  If this is empty, the default is none
   * 
   * It gets this info from the ccdaSectinHeaders.lragr table where the last field
   * contains the list of potential annotation types for this kind of section.  This
   * gets stuffed into the otherFeatures attribute during lexical lookup.
   * 
   * @param pTerm
   * @return String
  */
  // =================================================
    private final String whatKindOfThingsInThisSection(LexicalElement pTerm) {
    String returnVal = "none";
    
    StringArray otherFeaturez = pTerm.getOtherFeatures();
    
    if ( otherFeaturez != null && otherFeaturez.size() > 0 ) {
      returnVal = UIMAUtil.stringArrayToString(otherFeaturez);   
    }
    
    return returnVal;
  } // end whatKindOfThingsInThisSection() ----------

/**
  * destroy
  * 
  **/
 public void destroy() {
    if ( this.performanceMeter != null )
      this.performanceMeter.writeProfile( this.getClass().getSimpleName());
 } // end Method destroy() ----------

 
 //----------------------------------
 /**
  * initialize
  * 
  * @param aContext
  * 
  **/
 // ----------------------------------
 public void initialize(UimaContext aContext) throws ResourceInitializationException {
   
   String[] args = null;
   try {
     args                 = (String[]) aContext.getConfigParameterValue("args");  
     
     initialize( args );
     
   } catch (Exception e ) {
     e.printStackTrace();
     String msg = "Issue initializing " + annotatorName + " " + e.toString() ;
     GLog.println(GLog.ERROR_LEVEL, msg);
     throw new ResourceInitializationException();
   }
   
 } // end Method initialize() --------
 
//----------------------------------
/**
 * initialize loads in the resources needed
 * 
 * @param pArgs
 * 
 **/
// ----------------------------------
public void initialize(String[] pArgs) throws ResourceInitializationException {
  
 
  try {
    
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
   
    
  } catch (Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, "Issue initializing the " + annotatorName + " " + e.toString() );
    throw new ResourceInitializationException();
  }
  

  
} // end Method initialize()






 // ---------------------------
 // Global Variables
 // ---------------------------

 private int annotationCounter = 0;
 private ProfilePerformanceMeter              performanceMeter = null;

} // end Class ExampleAnnotator() ---------------
