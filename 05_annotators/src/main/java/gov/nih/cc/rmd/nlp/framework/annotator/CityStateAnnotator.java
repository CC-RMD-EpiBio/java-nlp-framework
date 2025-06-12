// =================================================
/**
 * CityState annotator turns lexical elements that
 * are marked with the category city or state
 * into Location annotations
 * 
 * this relies on a city and state lexicon in ...
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
import gov.nih.cc.rmd.framework.model.Location;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Utterance;



public class CityStateAnnotator extends JCasAnnotator_ImplBase {
    
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
      
      List<Annotation> someLocations = new ArrayList<Annotation>();
      List<Annotation> terms = UIMAUtil.getAnnotations(pJCas, LexicalElement.typeIndexID, true);

      if (terms != null && !terms.isEmpty()) {

        // ------------------------------------------------------
        // Walk through the utterances, looking for those that
        // are contentHeaders, and not slotValue's.
        // ------------------------------------------------------
        for (Annotation aTerm : terms )  {
        
          String sectionName = getSectionName(pJCas, aTerm);
          String semanticTypes = ((LexicalElement) aTerm).getSemanticTypes();
         
          if ( isALocation(semanticTypes )) 
            someLocations.add( createLocation(pJCas, (LexicalElement) aTerm, sectionName));
        }
      } // end if there are any terms
      
      // weed out weak locations 
      //    only one address location in a phrase
      //    and lowercase
      weedOutWeakLocations( pJCas, someLocations);
      
  
     } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL,"Issue with term tokenizing " + e.toString());
     }
     
     
      this.performanceMeter.stopCounter();
    
    } // end Method process() ------------------

    
   

    // =================================================
    /**
     * weedOutWeakLocations prunes out locations that are likely false positives
     * 
     * @param pJCas
     * @param pSomeLocations
    */
    // =================================================
    private void weedOutWeakLocations(JCas pJCas, List<Annotation> pSomeLocations) {
      
      if ( pSomeLocations != null && !pSomeLocations.isEmpty())
        for ( Annotation aLocation : pSomeLocations) {
          
          if ( isOnlyLocationInSentence( pJCas, aLocation) )
            if ( !isInitialCapitalized( aLocation ))
              aLocation.removeFromIndexes(pJCas);
        }
      
    } // end Method weedOutWeakLocations() -------------




    // =================================================
    /**
     * isOnlyLocationInSentence returns true if this is the only
     * location in the surrounding phrase
     * 
     * @param pJCas
     * @param pLocation
     * @return
    */
    // =================================================
    private boolean isOnlyLocationInSentence(JCas pJCas, Annotation pLocation) {
     
      boolean returnVal = false;
      
      List<Annotation> someSentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Utterance.typeIndexID, pLocation.getBegin(), pLocation.getEnd(), true );
      if ( someSentences != null )
        for ( Annotation aSentence : someSentences)
          if ( isOnlyLocationInSentence( pJCas, pLocation, aSentence) ) {
            returnVal = true;
            break;
          }
      
      return returnVal;
      
    } // end Method isOnlyLocationInSentence() ---------




    // =================================================
    /**
     * isOnlyLocationInSentence returns true if there is only one
     * location annotation in this sentence
     * 
     * @param pJCas
     * @param pLocation
     * @param aSentence
     * @return boolean
    */
    // =================================================
    private final boolean isOnlyLocationInSentence(JCas pJCas, Annotation pLocation, Annotation pSentence) {
      boolean returnVal = true;
      
      List<Annotation> someLocations = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Location.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), true );
     
      if ( someLocations != null && !someLocations.isEmpty() )
        if ( someLocations.size() > 1)
          returnVal = true;
      
      return returnVal;
        
    } // end Method isOnlyLocationInSentence() ----------




    // =================================================
    /**
     * isInitialCapitalized returns true if this 
     * 
     * @param pLocation
     * @return boolean
    */
    // =================================================
    private boolean isInitialCapitalized(Annotation pLocation) {
      boolean returnVal = false;
      
      String mention = pLocation.getCoveredText();
      if ( U.isInitialCap(mention))
        returnVal = true;
      
      return returnVal;
    } // end Method isInitialCapitalized() --------------------




    // =================================================
    /**
     * isALocation
     * 
     * @param pSemanticTypes
     * @return
    */
    // =================================================
    private boolean isALocation(String pSemanticTypes) {
      
      boolean returnVal = false;
      if ( pSemanticTypes != null && !pSemanticTypes.isEmpty() )
        if ( pSemanticTypes.contains("city") ||
             pSemanticTypes.contains("state") ||
             pSemanticTypes.contains("Country") )
          returnVal = true;
      
      return returnVal;
    }  // end Method isALocation() --------------------
    



   
    
    // =================================================
    /**
     * createLocation 
     * 
     * @param pJCas
     * @param pTerm
     * @param codedEntries
     * @param pSectionZone
     * @return Annotation
    */
    // =================================================
    private final Annotation createLocation(JCas pJCas, LexicalElement pTerm, String pSectionName) {
   
      Location statement = new Location(pJCas);
      
      statement.setBegin( pTerm.getBegin());
      statement.setEnd(   pTerm.getEnd());
      statement.setId("CityStateAnnotator_" + annotationCounter );
      statement.setSemanticTypes( pTerm.getSemanticTypes());
      StringArray codez = pTerm.getEuis();
      String codes = UIMAUtil.stringArrayToString(codez);
      codes = codes.replace("|", ",");
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
   
    
} // end Class TermToConceptAnnotator() -----------

