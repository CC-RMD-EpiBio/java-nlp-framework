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
import org.junit.experimental.categories.Categories;

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
      GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " Start " + this.getClass().getSimpleName());
      
      
      
      List<Annotation >personPieces = findPersonComponents( pJCas);
      
      combinePersonPieces( pJCas, personPieces);
     
      
     } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL,"Issue with term tokenizing " + e.toString());
     }
      
      GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " End " + this.getClass().getSimpleName());
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
      if ( hasPii (pTerm1) ||  hasPii( pTerm2) )
        statement.setPii(true); 
    
      statement.setSemanticTypes( "person");
      statement.addToIndexes();
     
      
    } // end Method combineAnnotations()  --------------
    




    // =================================================
    /**
     * hasPii returns true if this is a shape and getPII is true
     * or lexical element with a semantic type of phi or pii
     * 
     * @param pMention
     * @return boolean
    */
    // =================================================
    private boolean hasPii(Annotation pMention) {
      boolean returnVal = false;
      
      String className = pMention.getClass().getCanonicalName();
      if( className.contains("LexicalElement")) {
        String semanticTypeS = ((LexicalElement)pMention).getSemanticTypes();
        if ( semanticTypeS != null && !semanticTypeS.isEmpty()) {
           String semanticTypes = semanticTypeS.toLowerCase();
           if ( semanticTypes.contains("pii") || semanticTypes.contains("phi"))
             returnVal = true;
        }
      } else if ( className.contains("Shape")) {
        if ( ((Shape) pMention).getPii() || ((Shape) pMention).getPhi() )
          returnVal = true;
      }
        
      return returnVal;
    } // end Method hasPii() -------------------




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
             pSemanticTypes.contains("personSuffix") ||
             pSemanticTypes.equals("person") ||   //  there are other semantic types that could also 
             pSemanticTypes.contains("person:") ||    // get caught here Person Factors, Personal Background
             pSemanticTypes.contains(":person|") 
             )
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
      
      
      if      (pClass.contains("PersonName"))  {  statement = new Person( pJCas);            }
      else if (pClass.contains("personPrefix")) { statement = new PersonNamePrefix( pJCas);  }
      else if (pClass.contains("personSuffix")) { statement = new PersonNameSuffix( pJCas);  } 
      else if (pClass.contains("person"))       statement = new Person( pJCas); 
      
      
      String categories = ((LexicalElement)pTerm).getSemanticTypes();
      if ( categories != null && categories.trim().length() > 0 ) {
        if ( categories.toLowerCase().contains("pii") ) statement.setPii(true);
        if ( categories.toLowerCase().contains("phi") ) statement.setPhi(true);
      }
      
      
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

