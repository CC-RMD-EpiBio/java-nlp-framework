/*******************************************************************************
 * ---------------------------------------------------------------------------
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                        Epidemiology and Biostatistics Branch 
 *                                         2019
 *    
 *  This work is licensed under the Creative Commons Attribution 4.0 International License. 
 *  
 *  This license allows you to use, share and  adapt for any purpose, provided:
 *     Provide attribution to the creators of this work within your work.
 *     Indicate if changes were made to this work.
 *     No claim to merchantability, implied warranty, or liability can be made.
 *     
 *   When attributing this code, please make reference to
 *    [citation/url here] .  
 *    
 *     In the absence of a specific paper or url listed above, reference http://clinicalcenter.nih.gov/rmd/eb/nlp
 *  
 *  To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 ******************************************************************************/
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
