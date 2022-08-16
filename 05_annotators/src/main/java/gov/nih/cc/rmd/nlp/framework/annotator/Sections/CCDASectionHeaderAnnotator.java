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
 * CCDASectionHeader sections identifies the section(zones)
 * that we are interested in:
 * 
 *   Demographics (gender/age/address/email/phone/Ethnicity/Race)
 *   Chief Complaint/Active Problems
 *   Social History (Smoking Status/Tobacco Use/Alchohol Use/Canabnoid Use)
 *   Allergies
 *   Medications (active/current/Ordered/Expired)
 *   Family History
 *   Vitals
 *   Labs (Results)
 *   Assessment/Plan
 *   Instructions
 *   Diagnosis
 *   Providers  <---- useful for patient
 *   
 *   Riff off of the sectionizer that we've got - look for
 *   section headings for these 
 *   
 *     find one - zone til the next indication we are out of
 *     that section - 
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
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Line;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;




public class CCDASectionHeaderAnnotator extends JCasAnnotator_ImplBase {
 
 
  public static final String annotatorName = CCDASectionHeaderAnnotator.class.getSimpleName();

  
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
         String buff = term.getCoveredText();
         
         
           if ( isCCDASectionName( (LexicalElement) term)) 
            if (startsTheLine( pJCas, term ) )
              if ( !contentNameExists( pJCas, term ))
                createSectionHeading( pJCas, term);
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
   * contentNameExists returns true if this span is 
   * already a content name
   * 
   * @param pJCas
   * @param pTerm
   * @return boolean
  */
  // =================================================
  private final boolean contentNameExists(JCas pJCas, Annotation pTerm) {
    boolean returnVal = false;
    
    List<Annotation> contentHeaders = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  ContentHeading.typeIndexID,  pTerm.getBegin(), pTerm.getEnd());
    
    if ( contentHeaders != null && !contentHeaders.isEmpty())
      returnVal = true;
    return returnVal;
  } // end Method contentNameExists() ----------------

  // =================================================
  /**
   * isCCDASectionName 
   * 
   * @param pJCas
   * @return boolean
  */
  // =================================================
  public static final boolean isCCDASectionName( LexicalElement pTerm ) { 
      
    boolean returnVal = false;
    String termTypes = pTerm.getSemanticTypes();
    
    if ( termTypes != null )
      if ( termTypes.contains("ccdaSectionName") || termTypes.contains("geneSectionName") || termTypes.contains("pageFooterSectionName"))
          returnVal = true;
      
      return returnVal;
    
  } // end Method isCCDASectionName() ---------------------
  
  

  // =================================================
  /**
   * startsTheLine returns true if this term is at the beginning of a line 
   * 
   * @param pJCas the cas
   * @param pTerm the term
   * @return boolean
  */
  // =================================================
  private final boolean startsTheLine( JCas pJCas, Annotation pTerm ) { 
      
    boolean returnVal = false;
   
    List<Annotation> lines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Line.typeIndexID,  pTerm.getBegin(), pTerm.getEnd());
    
    if ( lines != null && !lines.isEmpty()) {
      Annotation line = lines.get(0);
      if ( line != null ) {
        String lineBuff = line.getCoveredText();
        if ( lineBuff != null ) {
          lineBuff = lineBuff.trim();
          String termBuff = pTerm.getCoveredText();
          if (termBuff.length() > lineBuff.length())
            termBuff = termBuff.substring(0,  lineBuff.length() -1);
          
          if ( termBuff != null && termBuff.length() > 0)
            if ( lineBuff.startsWith( termBuff)) 
              returnVal = true;
        }
      }
    }
   
    return returnVal;
    
  } // end Method startsTheLine() ---------------------

  // =================================================
  /**
   * createSectionHeading creates a section heading
   *  (ContentHeading) if one does not already exist
   * 
   * @param pJCas
   * @param pSection
  */
  // =================================================
    private  final void createSectionHeading(JCas pJCas, Annotation pTerm) {
    
        ContentHeading aContentHeading = null;
        aContentHeading = new ContentHeading( pJCas);
        aContentHeading.setBegin(pTerm.getBegin());
        aContentHeading.setEnd(pTerm.getEnd());
        aContentHeading.setSectionName( pTerm.getCoveredText());
        aContentHeading.setId( this.getClass().getCanonicalName() + "_" + this.annotationCounter);
        
        ArrayList<String> otherFeatures = new ArrayList<String>();
        String sectionType = "not ccda Section";
        if ( isCCDASectionName ( (LexicalElement ) pTerm ) )
            sectionType = ((LexicalElement)pTerm).getSemanticTypes();
        otherFeatures.add( sectionType );
        
        String possibleAnnotations = whatKindOfThingsInThisSection( (LexicalElement) pTerm  );
        otherFeatures.add( possibleAnnotations);
        
        StringArray otherFeaturez = UIMAUtil.list2StringArray(pJCas, otherFeatures);
        aContentHeading.setOtherFeatures( otherFeaturez);
      
        
        aContentHeading.addToIndexes();
   
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
    
     
   } catch (Exception e ) {
    
   }
   initialize( args );
   
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
