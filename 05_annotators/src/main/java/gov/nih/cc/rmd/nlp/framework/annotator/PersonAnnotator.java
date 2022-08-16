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
 * Person annotator turns lexical elements that
 * are marked with the category PersonPrefix, Person, PersonSuffix
 * into Person annotations
 * 
 *    Combining person prefix, person, person suffix into one term 
 * 
 * this relies on a personPieces lexicon in ...
 *
 * @author  Guy Divita 
 * @created May 24, 2018
 *
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.framework.model.Person;
import gov.nih.cc.rmd.framework.model.PersonNamePrefix;
import gov.nih.cc.rmd.framework.model.PersonNameSuffix;
import gov.nih.cc.rmd.framework.model.Shape;


import gov.va.chir.model.LexicalElement;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class PersonAnnotator extends JCasAnnotator_ImplBase {
    
    @Override
    // -----------------------------------------
    /**
     * process takes sentences and returns terms attached to that sentence.
     * 
     * @param aJCas
     */
    // -----------------------------------------
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
    
      try {
      this.performanceMeter.startCounter();
      
      
      List<Annotation >personPieces = findPersonComponents( pJCas);
      
      combinePersonPieces( pJCas, personPieces);
     
      
     } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL,"Issue with term tokenizing " + e.toString());
     }
      
      this.performanceMeter.stopCounter();
    
    } // end Method process() ------------------

    
   

    // =================================================
    /**
     * combinePersonPieces will combine personPrefix, unknown|personName ,
     * unknown personSuffix,  personPrefix, unknown|personName, personSuffix into
     * a person annotation  within an utterance window
     * 
     * @param pJCas
     * @param personPieces
    */
    // =================================================
    private final void combinePersonPieces(JCas pJCas, List<Annotation> personPieces) {
     
      if ( personPieces != null && !personPieces.isEmpty()) {
        for ( Annotation personPiece : personPieces ) {
           String pieceKind = ((Shape )personPiece).getSemanticTypes();
           
           if ( pieceKind.contains("personPrefix")) {
             LexicalElement termAfterPersonPiece = getTermAfterPersonPiece( pJCas, personPiece);
             if ( termAfterPersonPiece != null ) { 
               String euis = "unknown";
               StringArray euiz = termAfterPersonPiece.getEuis();
               if ( euiz != null ) euis = UIMAUtil.stringArrayToString( euiz);
               
               if ( (
                      euis.contains("unknown") &&   
                   (U.isInitialCap(termAfterPersonPiece.getCoveredText() )))
                   ||
                   (termAfterPersonPiece.getSemanticTypes() != null && termAfterPersonPiece.getSemanticTypes().contains("personName") ) 
                   )
                
               {
                 combineAnnotations( pJCas, personPiece, termAfterPersonPiece);
             }
             }
           } else if (  pieceKind.contains("personSuffix") ) {
             LexicalElement termBeforePersonPiece = getTermBeforePersonPiece( pJCas, personPiece);
             
             if ( termBeforePersonPiece != null && 
                 ( (termBeforePersonPiece.getSemanticTypes() != null && termBeforePersonPiece.getSemanticTypes().contains("personName") ||
                  (termBeforePersonPiece.getEuis() != null && termBeforePersonPiece.getEuis().equals("unknown") ) ) &&
                  (U.isInitialCap(termBeforePersonPiece.getCoveredText() ) ))) {
                  combineAnnotations( pJCas, termBeforePersonPiece, personPiece);
             }
           }
        }
      }
    } // end Method combinePersonPieces()  -------------
    




    // =================================================
    /**
     * getTermBeforePersonPiece 
     * 
     * @param personPiece
     * @return LexicalElement
    */
    // =================================================
    private final LexicalElement getTermBeforePersonPiece(JCas pJCas, Annotation pTerm) {
     
      LexicalElement returnVal = null;
      
      List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  LexicalElement.typeIndexID, pTerm.getBegin()-30, pTerm.getBegin() - 2 );
      
      if ( terms != null && !terms.isEmpty()) {
        // retrieve the last of this list
        returnVal = (LexicalElement) terms.get( terms.size() -1);
        
      }
      return returnVal ;
    }  // end Method getTermBeforePersonPiece() --------
    
 // =================================================
    /**
     * getTermAfterPersonPiece 
     * 
     * @param personPiece
     * @return LexicalElement
    */
    // =================================================
    private final LexicalElement getTermAfterPersonPiece(JCas pJCas, Annotation pTerm) {
     
      LexicalElement returnVal = null;
      
      List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  LexicalElement.typeIndexID, pTerm.getEnd()+1, pTerm.getEnd()+30 );
      
      if ( terms != null && !terms.isEmpty()) {
        // retrieve the last of this list
        returnVal = (LexicalElement) terms.get(0);
        
      }
      return returnVal ;
    }  // end Method getTermAfterPersonPiece() --------
    



    // =================================================
    /**
     * combineAnnotations creates a person annotation
     * from these two annotation pieces
     * 
     * @param pJCas
     * @param pTerm1
     * @param pTerm2
    */
    // =================================================
    private final void combineAnnotations(JCas pJCas, Annotation pTerm1, Annotation pTerm2) {
     
      Person statement = new Person( pJCas);
      statement.setBegin( pTerm1.getBegin());
      statement.setEnd(   pTerm2.getEnd());
      statement.setId("PersonAnnotator_" + annotationCounter );
      statement.setSemanticTypes( "person");
      statement.addToIndexes();
     
      
    } // end Method combineAnnotations()  --------------
    




    // =================================================
    /**
     * findPersonPieces
     * 
     * @param pJCas
     * @return List<Annotation
    */
    // =================================================
    private final List<Annotation> findPersonComponents(JCas pJCas) {
     
      List<Annotation> returnVal = null;
      List<Annotation> personPieces = new ArrayList<Annotation>();
      
      List<Annotation> terms = UIMAUtil.getAnnotations(pJCas, LexicalElement.typeIndexID, true);

      
      if (terms != null && !terms.isEmpty()) {

      
        
        // ------------------------------------------------------
        // Walk through the utterances, looking for those that
        // are contentHeaders, and not slotValue's.
        // ------------------------------------------------------
        for (Annotation aTerm : terms )  {
          String sectionName = getSectionName(pJCas, aTerm);
          String semanticTypes = ((LexicalElement) aTerm).getSemanticTypes();
          if ( isPerson(semanticTypes ))
            personPieces.add( createPerson(pJCas, (LexicalElement) aTerm, semanticTypes, sectionName));
        }
      } // end if there are any terms
      
      if ( personPieces != null && !personPieces.isEmpty())
        returnVal = personPieces;
  
      return returnVal;
    } // end Method findPersonComponents() 
    




    // =================================================
    /**
     * isPerson
     * 
     * @param pSemanticTypes
     * @return boolean
    */
    // =================================================
    private final boolean isPerson(String pSemanticTypes) {
      
      boolean returnVal = false;
      if ( pSemanticTypes != null && !pSemanticTypes.isEmpty()  && !pSemanticTypes.equals("null")) {
        
        if ( pSemanticTypes.contains("PersonName") ||
             pSemanticTypes.contains("personPrefix") ||
             pSemanticTypes.contains("personSuffix") )
          returnVal = true;
      }
      return returnVal;
    }  // end Method isPerson() --------------------
    



   
    
    // =================================================
    /**
     * createPerson 
     * 
     * @param pJCas
     * @param pTerm
     * @param codedEntries
     * @param pSectionZone
    */
    // =================================================
    private final Annotation createPerson(JCas pJCas, LexicalElement pTerm, String pClass, String pSectionName) {
   
      Shape statement = null;
      
      
      if      (pClass.contains("PersonName"))   statement = new Person( pJCas); 
      else if (pClass.contains("personPrefix")) statement = new PersonNamePrefix( pJCas); 
      else if (pClass.contains("personSuffix")) statement = new PersonNameSuffix( pJCas); 
      
      
      
      
      statement.setBegin( pTerm.getBegin());
      statement.setEnd(   pTerm.getEnd());
      statement.setId("PersonAnnotator_" + annotationCounter );
      statement.setSemanticTypes( pTerm.getSemanticTypes());
      statement.setEuis( pTerm.getEuis() );
      statement.setOtherFeatures( pTerm.getOtherFeatures() );
     
   
      statement.addToIndexes();
      
      return statement;
      
    } // end Method createClinicalStatement() ----------



     
     // =================================================
     /**
      * getSectionZone 
      * @param pJCas
      * @param pTerm
      * @return String
     */
     // =================================================
     private final String getSectionName(JCas pJCas, Annotation pTerm) {
      
       SectionZone sectionZone = null;
       String returnVal = "";
       
       List<Annotation> sections = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  SectionZone.typeIndexID,  pTerm.getBegin(), pTerm.getEnd() );
       
       
       if (sections != null && !sections.isEmpty()) {
         sectionZone = (SectionZone) sections.get(0);
         returnVal = sectionZone.getSectionName();
       }
       
       return returnVal;
     } // end Method getSectionZone() ---
    
     
    
     
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
      
    } // end Method destroy() ------------




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
     
      String args[] = null;
      try {
        args                  = (String[]) aContext.getConfigParameterValue("args"); 
        
        initialize( args );
        
      } catch ( Exception e) {
        e.printStackTrace();
        String msg = "Issue with initializing TermAnnotator " + e.toString();
        GLog.println(GLog.ERROR_LEVEL, msg);
        throw new ResourceInitializationException();
      }
      
      
     
      
      
    } // end Method initialize() ---------

    
 
    
    //----------------------------------
    /**
     * initialize loads in the resources needed for term tokenization. Currently, 
     * this involves NLM's LRAGR table.   
     * 
     * @param pArgs
     * 
     **/
    // ----------------------------------
    public void initialize(String[] pArgs) throws ResourceInitializationException {
     
      try {
        this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
       
       
      } catch (Exception e) {
        e.printStackTrace();
       //  GLog.println(GLog.ERROR_LEVEL,"Issue with getting the local terminology files " + e.toString());
        throw new ResourceInitializationException();
 
      }
    
    
      
    } // end Method Initialize();
    
    
 
    // -------------------------------------------
    // Class Variables
    // -------------------------------------------
    private int                           annotationCounter  = 0;    // new Term Counter.
    private ProfilePerformanceMeter       performanceMeter   = null;
    
 // ---------------------------------------
    // The needed LRAGR file paths 
    public final static String persons_Lexica =   "resources/vinciNLPFramework/person/femaleFirstNames.lragr" +  ":"
                                                + "resources/vinciNLPFramework/person/maleFirstNames.lragr"   +  ":"
                                                + "resources/vinciNLPFramework/person/personNamePieces.lragr"  ;
    
    
   
    
} // end Class TermToConceptAnnotator() -----------
