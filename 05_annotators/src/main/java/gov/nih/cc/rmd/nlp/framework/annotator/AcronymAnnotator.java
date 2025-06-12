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
 * AcronymAnnotator marks potential acronyms
 * and acronym expansions.  Acronyms and their expansion annotations
 * are created, as well as the lexicalElement being updated with acronym
 * and expansion features.
 * 
 *
 *
 * @author  Guy Divita 
 * @created Mar 5, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.Acronym;
import gov.va.chir.model.AcronymExpansion;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.PartOfSpeech;
import gov.nih.cc.rmd.nlp.framework.utils.acronym.Acronyms;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;


public class AcronymAnnotator extends JCasAnnotator_ImplBase {
    
    private Acronyms acronymLookup;
    public void process(JCas pJCas) throws AnalysisEngineProcessException {

    AnnotationIndex<Annotation> termIndex = pJCas
        .getAnnotationIndex(LexicalElement.type);

    if (termIndex != null) {

      // ------------------------------------------------------
      // Walk through the utterances, looking for those that
      // are contentHeaders, and not slotValue's.
      // ------------------------------------------------------
      for (Iterator<Annotation> i = termIndex.iterator(); i.hasNext();) {
        LexicalElement aTerm = (LexicalElement) i.next();

        try {
          discoverAcronym(pJCas, aTerm);
        } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException(e.toString());
          // throw a uima exception here.
        }

      } // end loop through the lexicalElements of the document
    } // end if there are any terms
  
    } // end Method process() ------------------
  
  
  // -----------------------------------------
    /**
     * discoverAcronym will note if the term is a potential acronym, and the
     * expansions.
     * 
     * @param pJCas
     * @param pTerm
     */
    // -----------------------------------------
    private void discoverAcronym(JCas pJCas, LexicalElement pTerm) throws Exception {
    
    StringArray euis = null;
    ArrayList<String> expansionsList = new ArrayList<String>();
    ArrayList<String> expansionEuisList = new ArrayList<String>();
    ArrayList<String> knownAcronymList = new ArrayList<String>();
    StringArray acronymExpansionArray = null;
    StringArray acronymExpansionEuisArray = null;
    StringArray acronymArray = null;
    List<String> acronymExpansions = null;
    List<String> acronymExpansionEuis = null;
    List<String> knownAcronyms = null;
    
    if (pTerm != null) {
      euis = pTerm.getEuis();
      
      if (euis != null) {
        for (int i = 0; i < euis.size(); i++) { // =========================
          String eui = euis.get(i);
          if ( ( acronymExpansions = this.acronymLookup.getAcronymExpansions(eui)) != null ) 
            for (int iE = 0; iE < acronymExpansions.size(); iE++)
              expansionsList.add(acronymExpansions.get(iE));
            acronymExpansionEuis = this.acronymLookup.getAcronymExpansionEuis(eui);
            if ( acronymExpansionEuis != null )
              for (int iE = 0; iE < acronymExpansionEuis.size(); iE++)
                expansionEuisList.add(acronymExpansionEuis.get(iE));

           if (( knownAcronyms = this.acronymLookup.getAcronymOf(eui)) != null )            
            if ( knownAcronyms != null )
              for (int iE = 0; iE < knownAcronyms.size(); iE++)
                knownAcronymList.add(knownAcronyms.get(iE)); 
                
        } // end loop through euis =========================================
        
        // Add the potential acronym expansions the lexicalElement
        if ((expansionsList != null) && (expansionsList.size() > 0)) {
          int expansionCtr = 0;
          int expansionEuiCtr = 0;
          acronymExpansionArray = new StringArray(pJCas, expansionsList.size());
          for (Iterator<String> j = expansionsList.iterator(); j.hasNext();)
            acronymExpansionArray.set(expansionCtr++, j.next());
          acronymExpansionEuisArray = new StringArray(pJCas, expansionEuisList.size());
          for (Iterator<String> k = expansionEuisList.iterator(); k.hasNext();)
            acronymExpansionEuisArray.set(expansionEuiCtr++, k.next());
          pTerm.setAcronymExpansions(acronymExpansionArray);
          pTerm.setAcronymExpansionEuis( acronymExpansionEuisArray);
        } // end if there are any expansions to be added to the term
        
        // Add potential acronyms of found expansions to the term
        
        if ((knownAcronymList != null) && (knownAcronymList.size() > 0)) {
          int acronymCtr = 0;
          acronymArray = new StringArray(pJCas, knownAcronymList.size());
          
          for (Iterator<String> j = knownAcronymList.iterator(); j.hasNext();)
            acronymArray.set(acronymCtr++, j.next());
          pTerm.setAcronyms(acronymArray);
          pTerm.setIsAcronymExpansion(true);
          AcronymExpansion anAcronymE = new AcronymExpansion(pJCas);
          int begin = pTerm.getBegin();
          int zEnd = pTerm.getEnd();
          VUIMAUtil.setProvenance(pJCas, anAcronymE, this.getClass().getName()); 
          anAcronymE.setBegin(begin);
          anAcronymE.setEnd(zEnd);
          anAcronymE.setDisplayString(pTerm.getCoveredText());
          anAcronymE.setAcronyms(acronymArray);
          anAcronymE.setId("AcronymExpansion_" + this.annotationCounter++);
          
          anAcronymE.addToIndexes();
          
          
        } // end if there are any known acronyms of the expansion term in hand
        
        // ------------------------------------------------------
        // Figure out if a potential acronym is really an acronym
        // ------------------------------------------------------
        if (acronymCriteria(pTerm, expansionsList)) {
          pTerm.setIsAcronym(true);
          Acronym anAcronym = new Acronym(pJCas);
          int begin = pTerm.getBegin();
          int zEnd = pTerm.getEnd();
	        VUIMAUtil.setProvenance(pJCas, anAcronym, this.getClass().getName()); 
          anAcronym.setBegin(begin);
          anAcronym.setEnd(zEnd);
          anAcronym.setDisplayString(pTerm.getCoveredText());
          anAcronym.setAcronymExpansions(acronymExpansionArray);
          anAcronym.setId("Acronym_" + this.annotationCounter++);
          pTerm.setIsAcronym(true);
          anAcronym.addToIndexes();
          
        } // end if this term meets the acronym criteria
      } // end if there are eui's for this term
    } // end if the term is null;
       
    } // end Method discoverAcronym() ---------
    
    
  // -----------------------------------------
  /**
   * acronymCriteria returns true under the following conditions
   *    if this is an unambiguous acronym
   *    if it is an ambiguous acronym and the term is in all upper case
   *    if it is not At,He,NAME
   *    if it is an ambiguous acronym and the term is in lower case, and cannot be
   *    a prep|conj|pron|det|
   *        (This will weed out a, as, or, to, when used as function words)
   * 
   * @param pTerm
   * @param pAcronymExpansions
   * @return
   */
  // -----------------------------------------
  private boolean acronymCriteria(LexicalElement pTerm, List<String> pAcronymExpansions) {
    
    boolean  returnValue = false;
    String          buff = pTerm.getCoveredText();
    FSArray     posArray = pTerm.getPotentialPartsOfSpeech();
    PartOfSpeech aPos = null;
    String           pos = null;
    
    if (( pAcronymExpansions == null ) ||(pAcronymExpansions.size() < 1 )) 
      returnValue = false;
    else if ( buff.equals("At") || buff.equals("He") || buff.equals("NAME") || buff.equals("or") || 
              buff.equals("AND") || buff.equals("BUT") || buff.equals("IN") )
      returnValue = false;
    else if ( pAcronymExpansions.size() == 1)
      returnValue = true;
    // else if ( !buff.equals( buff.toLowerCase() ) )
    //  returnValue = true;
    else if ( posArray == null ) 
      returnValue = true;
    else for ( int i = 0; i < posArray.size(); i++ ) {
     aPos = (PartOfSpeech) posArray.get(i);
     pos = aPos.getPos();
     returnValue = true;
     if ( pos.matches("CC|DT|EX|IN|MD|PP|RP|TO|W") ) {
       returnValue = false;
       break;
     } // end if the pos is a noun,adj, or adv
     
    } // end loop through potential parts of speech
  
    return returnValue;
  } // end Method acronymCriteria() ---------


  // ----------------------------------
  /**
   * initialize loads in the resources needed for marking acronyms. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
  
    if (aContext != null)
        super.initialize(aContext);
    
    initialize();
   
  } // end Method initialize() -------
      
//----------------------------------
 /**
  * initialize loads in the resources needed for marking acronyms. 
  * 
  * 
  **/
 // ----------------------------------
 public void initialize() throws ResourceInitializationException {
   
   try {
     this.acronymLookup = new Acronyms();
   } catch (Exception e) {
     e.printStackTrace();
     String msg = "Issue loading the acronym lookup "+ e.toString();
     System.err.println(msg);
     throw new ResourceInitializationException();
   }
  
 } // end Method initialize() -------
     

  
  // -----------------------------------------
  // Class Variables
  // -----------------------------------------
  private  int  annotationCounter = 0;
  
  
} // end Class AcronymAnnoatorSimple() -----
