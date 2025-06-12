// =================================================
/**
 * PosFixes01Annotator fixes the obvious issues not fixed by
 * the mayo clinic's pos tagger.
 *
 *
 * @author  GD  
 * @created 02/13/2023
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.pos;

import static org.junit.Assert.assertArrayEquals;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.PartOfSpeech;
import gov.va.chir.model.Sentence;


public class PosFixes01Annotator extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  /**
   * process retrieves lines of the document, labels those that are questions
   * as QuestionAndAnswer elements.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
      this.performanceMeter.startCounter();

     
     // Loop through utterances  (that's the span we won't cross) 
      
      
      
      List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID, true );
      
      if ( sentences != null && !sentences.isEmpty())
        for ( Annotation sentence : sentences )
          processSentence ( pJCas, sentence );
        
       
      this.performanceMeter.stopCounter();
      
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
     //   throw new AnalysisEngineProcessException();
      }
  } // end Method process() -------------------------------------------------------
    

//=================================================
 /**
  * processSentence looks for mental functioning annotations
  * in this sentence (This might be too big a chunk, we'll see)
  * 
  * look for verb evidence 
  * 
  * @param pJCas
  * @param pSentence
 */
 // =================================================
 private final void processSentence(JCas pJCas, Annotation pSentence) {
  
   List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas,  LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd(), true );
      if (terms != null && !terms.isEmpty())
        for (Annotation term : terms) {
          if ( isVerb(pJCas, term)) {
            System.err.println(" make decision " + term.toString());
            // change pos in term and pos annotation
          }
        }
 
  } // end Method processSentence() --------------
   

//=================================================
/**
* isVerb returns true if the pos tagger tagged this as a verb or as a noun
* The (cTakes) tagger isn't working as well as it should.  
* Adding some heruistics that  
* 
*    if the word before is "to" - it's a verb [1]
*    if the word ends with an ing - it's maybe a verb
* [1]  The penn treebank punts with "to" making it a separate 
*      ambiguous tag - and while some to [verb] combinations
*      are flagged, some [verbs], I guess were not in the ctakes training
*      set so they got tagged as nn rather than nouns.
* @param pJCas
* @param activity
* @return boolean
*/
//=================================================
public static boolean isVerb(JCas pJCas, Annotation pActivity) {
 
 boolean returnVal = false;
  List<Annotation> posList = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, PartOfSpeech.typeIndexID, pActivity.getBegin(), pActivity.getEnd() );
  List<Annotation> wordBefore = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pActivity.getBegin() -2, pActivity.getBegin() -1);
  
  String cc = "";
  String bb =  pActivity.getCoveredText().trim(); 
  String tokens[] = U.split( bb, " " );
 
    
  
  
  String tag = "";
  if (posList != null &&! posList.isEmpty()) {
    for (  Annotation pos_ : posList ) {
     tag = ((PartOfSpeech) pos_).getPos();
     if ( tag.startsWith("V") || tag.startsWith("R")) {
       returnVal = true;
       break;
     }
    }
  }
  if ( !returnVal )
    if ( wordBefore != null && !wordBefore.isEmpty() )
      if ( wordBefore.get(0).getCoveredText() != null && wordBefore.get(0).getCoveredText().toLowerCase().equals("to")) {
       
        if ( canBeAVerb( pJCas, pActivity))
          returnVal = true;
      }
  if ( !returnVal )
   if ( bb.endsWith("ing") || bb.endsWith("ed") || bb.endsWith("ought") )
     returnVal = true;
  
  if ( !returnVal )
    if ( tokens != null  && tokens.length > 1 && canBeAVerb( pJCas, pActivity))
      returnVal = true;
    
 return returnVal;
} // end Method isVerb() -----------------------------

  
  
  // =================================================
/**
 * canBeAVerb returns true if one of the lexicalElements indicates this can be a verb
 * 
 * @param pJCas
 * @param pTerm
 * @return boolean
*/
// =================================================
  public static final boolean canBeAVerb(JCas pJCas, Annotation pTerm) {
    boolean returnVal = false;
    
       List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pTerm.getBegin(), pTerm.getEnd(), false);;
    if ( terms != null && !terms.isEmpty() )
    	for ( Annotation aTerm : terms ) {
    		FSArray potentalPartsOfSpeechez = ((LexicalElement) aTerm).getPotentialPartsOfSpeech();
    
    		String termName = pTerm.getCoveredText();
   
    		if ( potentalPartsOfSpeechez != null && potentalPartsOfSpeechez.size() > 0 ) {
    			@SuppressWarnings("unchecked")
    			List<Annotation> potentalPartsOfSpeech = UIMAUtil.fSArray2List(pJCas, potentalPartsOfSpeechez);
    
    			for ( Annotation pos : potentalPartsOfSpeech ) {
    				String aPos = ((PartOfSpeech) pos).getPos() ;
    				if ( aPos != null && aPos.toLowerCase().startsWith("v" ))
    						returnVal = true;
    						break;
    						
    			}
    		}
    	}
    return returnVal;
} // end Method canBeAVerb() ----------------------


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

      initialize(args);
      
    } catch (Exception e ) {
      String msg = "Issue in initializing class " + this.getClass().getName() + " " + e.toString() ;
      GLog.println(GLog.ERROR_LEVEL, msg);     // <------ use your own logging here
      throw new ResourceInitializationException();
    }
      
  } // end Method initialize() -------
  
//----------------------------------
  /**
   * initialize initializes the class.  Parameters are passed in via a String
   *                array  with each row containing a --key=value format.
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
   
      
  } // end Method initialize() -------
  
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   ProfilePerformanceMeter performanceMeter = null;
  
  
  
  
} // end Class LineAnnotator() ---------------
