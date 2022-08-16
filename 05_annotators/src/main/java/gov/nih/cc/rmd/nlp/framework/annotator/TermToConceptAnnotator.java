// =================================================
/**
 * TermToConceptAnnotator converts Terms to concepts where
 * the terms have cuis.
 * 
 *   Each lexRecord will become a separate codedConcept
 *   Each term will become a clinicalStatement
 *
 * @author  Guy Divita 
 * @created May 24, 2018
 *
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.SectionZone;

import gov.va.chir.model.ClinicalStatement;
import gov.va.chir.model.CodedEntry;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.MinimalPhrase;
import gov.va.chir.model.Token;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class TermToConceptAnnotator extends JCasAnnotator_ImplBase {
    
   
    @SuppressWarnings("unchecked")
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
      
     
      List<Annotation> terms = UIMAUtil.getAnnotations(pJCas, LexicalElement.typeIndexID, true);

      if (terms != null && !terms.isEmpty()) {

        // ------------------------------------------------------
        // Walk through the utterances, looking for those that
        // are contentHeaders, and not slotValue's.
        // ------------------------------------------------------
        for (Annotation aTerm : terms )  {
          String sectionName = getSectionName(pJCas, aTerm);
          processTerm(pJCas, (LexicalElement) aTerm, sectionName);
        }
      } // end if there are any terms
  
     } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL,"Issue with term tokenizing " + e.toString());
     }
      
      this.performanceMeter.stopCounter();
    
    } // end Method process() ------------------

    
   

    // -----------------------------------------
    /**
     * processTerm
     * 
     * @param pJCas
     * @param pTerm
     * @throws Exception 
     */
    // -----------------------------------------
    private final  void processTerm (JCas pJCas, LexicalElement pTerm, String pSectionName ) throws Exception {
      
      StringArray lexMatchez = pTerm.getLexMatches();
      
      if ( lexMatchez != null && lexMatchez.size() > 0 ) {
        String[] lexMatches = UIMAUtil.stringArrayToArrayOfString(lexMatchez );
        Annotation aCodedEntry = null;
        ArrayList<Annotation> codedEntries = new ArrayList<Annotation>(lexMatchez.size());
        if ( lexMatches != null && lexMatches.length > 0  )
          for ( String lexMatch : lexMatches ) {
            aCodedEntry = createCodedEntry( pJCas, pTerm, lexMatch, pSectionName );
            codedEntries.add( aCodedEntry);
          }
      
        createClinicalStatement( pJCas, pTerm, codedEntries, pSectionName);
      }
    
    } // end Method processTerm() --------------

   
    
    // =================================================
    /**
     * createClinicalStatement 
     * 
     * @param pJCas
     * @param pTerm
     * @param codedEntries
     * @param pSectionZone
    */
    // =================================================
    private final void createClinicalStatement(JCas pJCas, LexicalElement pTerm, List<Annotation> pCodedEntries, String pSectionName) {
   
      ClinicalStatement statement = new ClinicalStatement(pJCas);
      
      statement.setBegin( pTerm.getBegin());
      statement.setEnd(   pTerm.getEnd());
      statement.setId("TermToConceptAnnotator_clincalStatment_" + annotationCounter );
      statement.setDisplayString( U.display(statement.getCoveredText()));
      
      // -----------------------------------
      // Copy over the coded entry semantic types
      // -----------------------------------
      HashSet<String> semanticTypeHash = new HashSet<String>();
      for ( Annotation codedEntry : pCodedEntries ) {
        String semanticTypes[] = getSemanticTypes( (CodedEntry) codedEntry);
       
     
      
        for ( String semanticType : semanticTypes ) semanticTypeHash.add( semanticType );
        VUIMAUtil.updateConceptInfo(statement, (CodedEntry) codedEntry);
      }
      String[] semanticTypes = semanticTypeHash.toArray(new String[ semanticTypeHash.size()]);
      if ( semanticTypes != null ) { 
        StringArray semanticTypez = UIMAUtil.string2StringArray(pJCas, semanticTypes);
        statement.setSemanticTypez(semanticTypez);
       
        // statement.setSemanticGroups();
        
        
      }
      // -----------------------------------------
      // setMinimal Phrase and set Parent should be pointing to the same thing
      // Find the closest minimal phrase or make one up
      // --------------------------------
      MinimalPhrase minimalPhrase = getMinimalPhrase( pJCas, pTerm);
      
      statement.setParent( minimalPhrase );
      statement.setSectionName( pSectionName);
      
      statement.setProcessMe(true);
     
      if ( pCodedEntries != null ) {
        FSArray codedEntriez = UIMAUtil.list2FsArray(pJCas, pCodedEntries );
        statement.setCodedEntries(codedEntriez);
      }
      
      statement.addToIndexes();
      
    } // end Method createClinicalStatement() ----------



    // =================================================
    /**
     * createCodedEntry 
     * 
     * @param pJCas
     * @param pTerm
     * @param pLexMatch
     * @param pSectionName
     * @return CodedEntry
    */
    // =================================================
     private final Annotation createCodedEntry(JCas pJCas, Annotation pTerm, String pLexMatch, String pSectionName ) {
      
       CodedEntry statement = new CodedEntry(pJCas);
       
       statement.setBegin( pTerm.getBegin());
       statement.setEnd(   pTerm.getEnd());
       statement.setId("TermToConceptAnnotator_codedEntry_" + annotationCounter );
       statement.setSectionName(pSectionName);
       
       
       String cols[] = U.split( pLexMatch );
       //   0        1     2      3     4    5     6    7    8    9 10|11|12|13|4
       // C1519530|title|<noun>|<all>|Title|Title|T078|NCI|C19067|n|0 |S |PF|title|1
       
       statement.setCodeCode( cols[0]);
       statement.setDisplayName( cols[4]);
       if ( cols.length > 7)
         statement.setCodeSystem(cols[7]);
       //statement.setCodeSystemName(v);
       String semanticTypeString = cols[6];
       if ( semanticTypeString != null && !semanticTypeString.isEmpty() ) {
         String[] semanticTypes = U.split(semanticTypeString, ":");
         StringArray semanticTypez = UIMAUtil.string2StringArray(pJCas, semanticTypes);
         statement.setSemanticType(semanticTypez);
       } 
       statement.setSectionName(pSectionName);
       //statement.setSemanticGroup(v);
      
       String[] otherFeatures = new String[6];
       otherFeatures[0] = cols[7];  // sab
       otherFeatures[1] = cols[8];  // source id 
       otherFeatures[2] = cols[9];  // variant history
       otherFeatures[3] = cols[10]; // variant distance
       otherFeatures[4] = cols[11]; // stt
       otherFeatures[5] = cols[12]; // tty
       
       StringArray otherFeaturez = UIMAUtil.string2StringArray(pJCas, otherFeatures);
      statement.setOtherFeatures( otherFeaturez);
       
       statement.addToIndexes();
       
       return statement;
      
    }  // end Method createLexicalElement() ------------
 
     
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
    
     
     // ------------------------------------------
     /**
      * createClinicalStatement creates the clinicalStatement associated with
      * this span.  It will find any Phrase that encompass this span and point
      * the parent to it.
      *
      *
      * @param pJCas
      * @param codedEntry
      * @param pTokens
      * @param ClinicalStatement
      */
     // ------------------------------------------
     private final ClinicalStatement createClinicalStatement(JCas pJCas, CodedEntry codedEntry, String pSpanKey,  ArrayList<Token> pTokens, HashMap<String, Object>pClinicalStatementHash, HashMap<String, Object>pMinimalPhraseHash, String pSectionName) throws Exception {

     
       ClinicalStatement clinicalStatement = new ClinicalStatement(pJCas);
      
       
       
      
       
       return clinicalStatement;
    
       
       
       
     }  // End Method createClinicalStatement() -----------------------
     

     // ------------------------------------------
     /**
      * getSemanticTypes
      *
      *
      * @param codedEntry
      * @return
      */
     // ------------------------------------------
     private final String[] getSemanticTypes(CodedEntry codedEntry) {

       String[] returnValue = null;
       StringArray stys = codedEntry.getSemanticType();
       if (stys != null ) {
         returnValue = UIMAUtil.stringArrayToArrayOfString(stys);
       }
       
       return returnValue;
       // End Method getSemanticTypes() -----------------------
     }

     // ------------------------------------------
     /**
      * getMinimalPhrase returns the minimal phrase that covers the clinical Statement
      * If none are found, a Phrase with a minimal Phrase are created
      *
      * @param pJCas
      * @param pTerm
      * @return MinimalPhrase
      */
     // ------------------------------------------
     private final MinimalPhrase getMinimalPhrase(JCas pJCas, Annotation pTerm)  {
       
       MinimalPhrase returnVal = null;
       
       List<Annotation> phrases = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, MinimalPhrase.typeIndexID,  pTerm.getBegin(), pTerm.getEnd() );
         
       if ( phrases != null && !phrases.isEmpty())
         returnVal = (MinimalPhrase) phrases.get(0); 
         
       return returnVal;
     }  // End Method getMinimalPhrase() -----------------------
     
     
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
   
    
} // end Class TermToConceptAnnotator() -----------
