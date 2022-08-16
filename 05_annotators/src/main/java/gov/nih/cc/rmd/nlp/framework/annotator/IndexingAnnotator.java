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
 * IndexAnnotator creates indexingAnnotations to be
 * used for the full text indexing writer which
 * will add these to the tables for full text indexing
 * 
 *        
 * @author  Guy Divita 
 * @created March 20, 2015
 *
 * *  
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
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.DependentContent;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Section;
import gov.va.chir.model.SlotValue;
import gov.va.vinci.model.Key;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class IndexingAnnotator extends JCasAnnotator_ImplBase {
  
 
  private HashSet<String> stopWordHash;

  // -----------------------------------------
  /**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
    this.performanceMeter.startCounter();
    
   
      List<Annotation>titleTerms = getTitleTerms(pJCas);
      
      if ( titleTerms != null && !titleTerms.isEmpty())
      processTerms(pJCas, titleTerms, true);
      
      
    
      List<Annotation> documentTerms = UIMAUtil.getAnnotations(pJCas, LexicalElement.typeIndexID);
      processTerms(pJCas, documentTerms, false);
      
     
      
      // set the section names for each of the keys
      if ( documentTerms != null && !documentTerms.isEmpty())
        setSectionNames(pJCas);
      
    
   
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   // throw new AnalysisEngineProcessException();
  }
    
    this.performanceMeter.stopCounter();
    
    
  } // end Method process() ----------------
  
  

  // ==========================================
  /**
   * getTitleTerms retreives the document title terms from
   * the document
   *
   * @param pJCas
   * @return
   */
  // ==========================================
  private List<Annotation> getTitleTerms(JCas pJCas) {
  
    List<Annotation> titleTerms = null;
    
    List<Annotation> slotValues = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID);
    
    if ( slotValues != null && !slotValues.isEmpty()) {
      
      for ( Annotation slotValue: slotValues ) {
        String contentHeading = ((SlotValue) slotValue).getContentHeaderString();
        if ( contentHeading.equals("Title")) {
          DependentContent value = ((SlotValue) slotValue).getDependentContent();
          if ( value != null ) {
            titleTerms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, value.getBegin(), value.getEnd());
          }
          break;  // we are done -we found it.
        }
        
      }
    }
    
    return titleTerms;
  } // end Method getTitleTerms() ========================================
  



  // ==========================================
  /**
   * processTerms 
   *
   * @param pJCas
   * @param pTerms 
   * @param pInTitle
   */
  // ==========================================
  private void processTerms(JCas pJCas, List<Annotation> pTerms, boolean pInTitle) {
   
    if ( pTerms != null ) {
      
      for ( Annotation term : pTerms ) {
        String key = ((LexicalElement) term).getStemmedKey();
        if (key != null && !isStopWord(key)) {
          
          if ( !((LexicalElement) term).getMarked() ) {
            createKeyAnnotation(pJCas, ((LexicalElement)term), pInTitle);
            ((LexicalElement) term).setMarked(true) ; 
          } // end if this term has been processed 
          
        } // end if this term is a stop word
       // term.removeFromIndexes();  
        
      } // end Loop thru annotations
      
    } // end if there are any terms to process 
    // end Method processTerms() ========================================
  }



  // ==========================================
  /**
   * setSectionNames sets the section names for
   * all the keys that appear in non-empty named sections
   *
   * @param pJCas
   */
  // ==========================================
  private void setSectionNames(JCas pJCas) {
   
    List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, Section.typeIndexID);
    
    if ( sections != null && !sections.isEmpty()) {
      for ( Annotation section: sections ) {
        String sectionName = ((Section)section).getSectionName();
        if ( sectionName != null ) {
          List<Annotation> keys = UIMAUtil.getAnnotationsBySpan(pJCas, Key.typeIndexID, section.getBegin(), section.getEnd());
     
            if ( keys != null && !keys.isEmpty()) {
              for ( Annotation key: keys ) {
                ((Key)key).setSectionName(sectionName);
              }
            }
        
        } //end if the section name is not null
      } // end loop thru sections
    } // end if there are any sections
  }  // end Method setSectionNames() ========================================
  



  // -----------------------------------------
  /**
   * createKeyAnnotation 
   * @param pJCas
   * @param pKey
   * @param pInTitle
   * @param pAssertionStatus
   *  
   */
  // -----------------------------------------
  private void createKeyAnnotation(JCas pJCas, LexicalElement pKey, boolean pInTitle   ) { 
    
    Key key = new Key(pJCas);
    key.setKey( pKey.getStemmedKey());
    key.setBegin(pKey.getBegin());
    key.setEnd( pKey.getEnd());
    String assertionStatus = pKey.getNegation_Status() ;
    String subjectStatus   = pKey.getSubject();
    if ( assertionStatus == null ) assertionStatus = "Asserted";
    if (   subjectStatus == null ) subjectStatus   = "Patient";
    
    key.setAssertionStatus(assertionStatus );
    key.setConditionalStatus(pKey.getConditional());
   // key.setHistoricalStatus(pKey.getHistorical()));
    key.setSubjectStatus(subjectStatus);
    key.setInTitle(pInTitle);
    
  
    key.addToIndexes();
    
   
  } // end createKeyAnnotation() ---------------------


  // -----------------------------------------
  /**
   * isStopWord 
   * 
   * @param pkKey
   * @return boolean
   */
  // -----------------------------------------
  private boolean isStopWord(String pKey) {

    boolean returnVal = false;
    if (this.stopWordHash.contains(pKey ))
      returnVal = true;
   
    return returnVal;
  } // end Method isStopWord() ---------


  // -----------------------------------------
  /**
   * loadStopWords 
   * 
   * @param pStopWordListName
   * @return HashSet<String>
   * @throws ResourceInitializationException 
   */
  // -----------------------------------------
  public HashSet<String> loadStopWords(String pFileName) throws ResourceInitializationException {
    
   try {
     
     this.stopWordHash = new HashSet<String>();
     
     String[] rows = U.readClassPathResourceIntoStringArray(pFileName);
     
     for ( String row: rows ) {
       if ( !row.startsWith("#"))
         this.stopWordHash.add( row.toLowerCase());
     }
   } catch (Exception e) {
     e.printStackTrace();
     System.err.println("Issue loading the stop word list " + e.toString());
     throw new ResourceInitializationException();
   }
   
   return this.stopWordHash;
  }  // end Method loadStopWords() ----------

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
   * initialize loads in the resources needed for phrase chunking. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
  
      String[] args = (String[]) aContext.getConfigParameterValue("args");
      
      String stopWordListName = U.getOption(args, "--stopWordListName=", "resources/com/ciitizen/framework/indexing/stopWordList.txt");
      
      loadStopWords(stopWordListName);
      
      this.performanceMeter = new ProfilePerformanceMeter( args, this.getClass().getSimpleName() );
      
     
    
  } // end Method initialize() -------
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  private ProfilePerformanceMeter performanceMeter = null;
 
  
} // end Class IndexingAnnotator() ---------------
