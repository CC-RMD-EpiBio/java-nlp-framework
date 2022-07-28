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
 * SlotValueRepairAnnotator finds slot values that have slot values embedded in them.
 *
 * @author  Guy Divita 
 * @created June 23, 2016
 *
 * *  
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
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Delimiter;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.Token;
import gov.va.chir.model.VAnnotation;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class SlotValueRepairAnnotator extends JCasAnnotator_ImplBase {
  
  private int annotationCounter = 0;



  @Override
  // -----------------------------------------
  /**
   * process finds slotValues in the whole document
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
    this.performanceMeter.startCounter();
    List<Annotation> slotsAndValues = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID);
    

    if ( slotsAndValues != null && !slotsAndValues.isEmpty() ) {
      for ( Annotation slotAndValue : slotsAndValues )
        process( pJCas, (SlotValue) slotAndValue, 0 );
    }
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue with " + this.getClass().getName() + " " + e.toString());
     // throw new AnalysisEngineProcessException();
    }
  } // end Method process
      
  //----------------------------------
  /**
   * process
   * 
   * @param pJCas
   * @param pSlotAndValue
   * @param pIteration   ( don't do more than 1 iterations
   * 
   **/
  // ----------------------------------  
  private void process(JCas pJCas, SlotValue pSlotAndValue, int pIteration) {
  
    if (pIteration == 2)  return;
    if ( pSlotAndValue != null ) {
      //ContentHeading contentHeading = pSlotAndValue.getHeading();
      
      pSlotAndValue = fixSlot( pJCas, pSlotAndValue);
      
      DependentContent        value = pSlotAndValue.getDependentContent();
       
      if ( value != null ) {
       
        value = removeSentencesFromSlotValue(pJCas, value);
        
        String coveredText = value.getCoveredText();
        
        if ( coveredText == null  || coveredText.trim().length() == 0) {
        
          Annotation firstSentence = getFirstSentenceFromSlotValue(pJCas, pSlotAndValue);
          if (firstSentence != null)
           coveredText = firstSentence.getCoveredText();
        }
        if ( coveredText != null ) 
        {
          
          // remove sentences from slot value before doing on
          
         
          
          int delimiter = coveredText.indexOf( ':');
          int spaces = coveredText.indexOf( ' '); // nospaces - this is a time or date
          int slashes = coveredText.indexOf('/'); // slashes - likely a date
          
          boolean notADelimiter = colonInNumber( coveredText);
          
          if (( !notADelimiter )
          && ( delimiter > 1  && slashes == -1 && spaces > -1   ))  {
              delimiter = delimiter + value.getBegin();
            // -----------------------------------
            // Find the last term on the left of the delimiter
            // Get all terms to bhe right of the delimiter
            // create new slotValue from heading and value terms
            // Change the value span to just cover up to the headingTerm.
            List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas,LexicalElement.typeIndexID, value.getBegin(), value.getEnd());
            
            Annotation      headingTerm = getTermOnLeftOfDelimiter(terms, delimiter);
            if ( headingTerm != null && headingTerm.getCoveredText() != null && headingTerm.getCoveredText().trim().length() > 0 ) {
              List<Annotation> valueTerms = getTermsOnRightOfDelimiter( terms, delimiter );
              if ( valueTerms != null && valueTerms.size() > 0 ) {
                Annotation    delimiterToken = getDelimiterToken( pJCas, delimiter);
                SlotValue newSlotValue = createSlotValue(pJCas, headingTerm, valueTerms, delimiterToken );
                value.setId("re-repairedContent_" + value.getId());
                if ( value.getBegin() > headingTerm.getBegin() -1 ) {
                  value.setEnd( value.getBegin());
                } else {
                  value.setEnd( headingTerm.getBegin() -1 );
                }
                if ( newSlotValue != null )
                  try {
                    GLog.println(GLog.ERROR_LEVEL,newSlotValue.getCoveredText());
                    process( pJCas, newSlotValue, pIteration +1);
                  } catch (Exception e) {
                    e.printStackTrace();
                    GLog.println(GLog.ERROR_LEVEL, newSlotValue.getCoveredText());
                    GLog.println(GLog.ERROR_LEVEL,"Issue with repair : " + e.toString());
                    System.exit(-1);
                  }
              }
            }
          }
        } 
  
      }
    }
  } // End Method process() ======================
  
  // ==========================================
  /**
   * colonInNumber
   *
   * @param pBuff
   * @return boolean
   */
  // ==========================================
  private boolean colonInNumber ( String pBuff ) { 
    boolean returnVal = false;
 
    int i = -1;
    int last = 0;
    while ( (i = pBuff.indexOf( ':', last )) > -1) {  
      if ( i > 0  && U.isNumber( pBuff.charAt(i-1)) && i+1 < pBuff.length() && U.isNumber( pBuff.charAt(i+1)) ) {
        returnVal = true;
        break;
      }
      last = i+1;
    }
        
    return returnVal;
  }

  // ==========================================
  /**
   * fixSlot splits apart slots that have periods in them.
   * It keeps the part to the right of the period.  It assumes
   * that there is already a sentence made of the sentence
   * to the left.
   *
   * @param pJCas
   * @param pSlotAndValue
   * @return DependentContent
   */
  // ==========================================
  private SlotValue fixSlot(JCas pJCas, SlotValue pSlotValue) {
  
    SlotValue slotValue = pSlotValue;
   
    
    ContentHeading slot = slotValue.getHeading();
    if ( slot != null) {
    String buff = slot.getCoveredText();
    int pi = buff.indexOf(". ");
    if ( pi > 1 ) {
      //String newCoveredText = buff.substring(pi + 1);
      slot.setBegin( slot.getBegin() + pi + 1);
      pSlotValue.setBegin( slot.getBegin() );
      
    }
    }
    
    
    return slotValue;
    
  } // End Method fixSlot() ======================
  

  // ==========================================
  /**
   * removeSentencesFromSlotValue 
   *
   * @param pJCas
   * @param value
   * @return DependentContent
   */
  // ==========================================
  private DependentContent removeSentencesFromSlotValue(JCas pJCas, DependentContent pValue) {
   DependentContent value = pValue;
   
   List<Annotation> sentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Sentence.typeIndexID, pValue.getBegin(), pValue.getEnd()); 
   
   if ( sentences != null && !sentences.isEmpty()) 
     for ( int i = sentences.size() -1; i >=0 ; i-- ) {
       Annotation sentence = sentences.get(i);
       // remove this span from value;
       if ( sentence.getCoveredText().endsWith(".") ) {
         int sb = sentence.getBegin() ;
         int vb = value.getBegin();
         int ve = value.getEnd();
         if ( sb > vb && sb < ve  && sb > 0)
           sb = sb -1;
         else 
           sb = vb;
         value.setEnd( sb );
         }
     }
   
    return value;
    // end Method removeSentencesFromSlotValue() 
  }

//==========================================
 /**
  * removeSentencesFromSlotValue 
  *
  * @param pJCas
  * @param value
  * @return Sentence
  */
 // ==========================================
 private Annotation getFirstSentenceFromSlotValue(JCas pJCas, Annotation pSlotValue) {
  
   Annotation value = null;
   DependentContent v = null;
  
  List<Annotation> sentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Sentence.typeIndexID, pSlotValue.getBegin(), pSlotValue.getEnd()); 
  
  if ( sentences != null && !sentences.isEmpty()) {
    if ( sentences.size() > 1) value = sentences.get(1);
    else                       value = sentences.get(0);
    v = ((SlotValue) pSlotValue).getDependentContent();
    v.setBegin( value.getBegin());
    v.setEnd( value.getEnd());
    v.setId("SlotValueRepair");
    pSlotValue.setEnd( v.getEnd() );
   
   

  }
    
  return (Annotation) v;
   // end Method removeSentencesFromSlotValue() 
 }
  
  //----------------------------------
  /**
   * getTermOnLeftOfDelimiter
   * 
   * @param pTerms
   * @param pDelimiter
   * 
   **/
  // ----------------------------------  
private Annotation getTermOnLeftOfDelimiter(List<Annotation> pTerms, int pDelimiter) {
  Annotation returnVal = null;
  if ( pTerms != null ) {
    Annotation previousTerm = null;
    for ( Annotation term: pTerms ) {
      
      if ( term.getBegin() >= pDelimiter ) {
        returnVal = previousTerm;
        break;
      } else {
        previousTerm = term;
      }
    }
  }
  return returnVal;
}  // End Method getTermOnLeftOfDelimiter() ======================
  

//----------------------------------
/**
 * getTermOnRightOfDelimiter
 * 
 * @param pTerms
 * @param pDelimiter
 * 
 **/
// ---------------------------------- 
private List<Annotation> getTermsOnRightOfDelimiter(List<Annotation> pTerms, int pDelimiter) {
  ArrayList<Annotation> returnVal = null;
  if ( pTerms != null ) {
    
    for ( Annotation term: pTerms ) {
      
      if ( term.getBegin() <= pDelimiter ) {
        ;;// skip this term
      } else if ( term.getBegin() > pDelimiter ) {
        if ( returnVal == null ) returnVal = new ArrayList<Annotation>();
        returnVal.add( term );
      }
        
      } 
    }
  return returnVal;
} // End Method getTermsOnRightOfDelimiter() ======================
  

//----------------------------------
/**
* getDelimiterToken
* 
* @param pJCas
* @param pDelimiter
* 
**/
//---------------------------------- 
private Annotation getDelimiterToken(JCas pJCas, int pDelimiter) {
Annotation returnVal = null;

  List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas, Token.typeIndexID, pDelimiter, pDelimiter + 1);
  
  if ( tokens != null && !tokens.isEmpty() )
    returnVal = tokens.get(0);
  

return returnVal;
} // End Method getDelimiterTerm() ======================

  
// -----------------------------------------
/**
 * createSlotValue creates a slotValue annotation
 * from the slot and the value.  
 * 
 * @param pJCas
 * @param pSlotTokens
 * @param pValueTokens
 * @param pDelimiter
 * @param pProcess
 * @return 
 * 
 */
// -----------------------------------------
private SlotValue createSlotValue(JCas pJCas, 
                             Annotation pSlotTerm,
                             List<Annotation> pValueTerms,
                             Annotation pDelimiter
                             ) {
  
  SlotValue slotValue = null;
  int      begin = pSlotTerm.getBegin();
  int    slotEnd = pSlotTerm.getEnd();
  int valueBegin = -1;
  int valueEnd = slotEnd;
  if ( pValueTerms != null && pValueTerms.size() > 0) {
    valueBegin =  pValueTerms.get(0).getBegin();
    valueEnd   =  pValueTerms.get(pValueTerms.size()-1).getEnd();
  }
  int z = valueEnd - valueBegin;
  
  if ( z > 0 && z < 3000 ) {
         slotValue = new SlotValue(pJCas);
 
    if ( valueEnd >= begin) { 
      GLog.println(GLog.DEBUG_LEVEL,"Issue here with slot value ");
      return null;
    }
    slotValue.setBegin(begin);
    slotValue.setEnd( valueEnd );
    slotValue.setDisplayString( slotValue.getCoveredText() );
    slotValue.setId( "repairedSlotValue_" + this.annotationCounter++);
  
    // ---------------
    // Content Heading  
    ContentHeading contentHeading = createContentHeading( pJCas, pSlotTerm);
    slotValue.setHeading(contentHeading);   
    slotValue.setProcessContent( false);    

    // ------------------------
    // Delimiter
    if ( pDelimiter != null ) {
      Delimiter delimiter = createDelimiter( pJCas, pDelimiter, slotValue);
      slotValue.setDelimiter(delimiter.getCoveredText());
    }
    
   DependentContent contentValue = createDependentContent(pJCas, pValueTerms );
   contentValue.setParent( slotValue);
    
   slotValue.setContentString( contentValue.getCoveredText());
   slotValue.setDependentContent( contentValue);
   
   slotValue.addToIndexes();
  
 }    
  return slotValue;
} // end Method createSlotValue() ----------

//------------------------------------------
/**
* createDependantContent
*
*
* @param pJCas
* @param pValueTerms
*/
// ------------------------------------------
private DependentContent createDependentContent(JCas pJCas, List<Annotation> pValueTerms) {
 
 DependentContent  content = new DependentContent(pJCas);
 
 int begin =  pValueTerms.get(0).getBegin();
 int theEnd = pValueTerms.get(pValueTerms.size() -1 ).getEnd();
 
 if ( begin >= theEnd) {
   GLog.println(GLog.ERROR_LEVEL,"Something wrong here making a delimiter 2" );
   throw new RuntimeException();
 }
 
 content.setBegin( begin);
 content.setEnd  ( theEnd );
 content.setId("RepairedSlotValueContent_" + this.annotationCounter);
 if ( content.getBegin() >= content.getEnd() ) {
   GLog.println(GLog.ERROR_LEVEL,"what? ");
   throw new RuntimeException();
 }
 content.addToIndexes();
 
 return content;
}  // End Method createDependantContent() -----------------------


// ------------------------------------------
/**
* createContentHeading
*
*
* @param pJCas
* @param pHeadingTerm
*/
// ------------------------------------------
private  ContentHeading createContentHeading(JCas pJCas, Annotation pHeadingTerm) {
 
 ContentHeading contentHeader = new ContentHeading(pJCas);
 if ( pHeadingTerm.getBegin() >= pHeadingTerm.getEnd() ) {
   GLog.println(GLog.ERROR_LEVEL,"Issue here making a new content heading 4");
   throw new RuntimeException();
 }
 contentHeader.setBegin( pHeadingTerm.getBegin());
 contentHeader.setEnd( pHeadingTerm.getEnd() );

 contentHeader.setId( "RepairdSlotValueContentHeader_" + this.annotationCounter);
 contentHeader.setProcessMe(false);
 contentHeader.addToIndexes();
 
 return contentHeader;
 
 // End Method createContentHeading() -----------------------
}

// ------------------------------------------
/**
* createDelimiter
*
*
* @param pJCas
* @param pDelimiter
*/
// ------------------------------------------
private Delimiter createDelimiter(JCas pJCas, Annotation pDelimiter, VAnnotation pParent) {

 Delimiter delimiter = new Delimiter( pJCas);
 
 if ( pDelimiter.getBegin() >=  pDelimiter.getEnd() ) {
   GLog.println(GLog.ERROR_LEVEL,"Issue here making a new delimiter 5 ");
   throw new RuntimeException();
 }
 delimiter.setBegin(pDelimiter.getBegin());
 delimiter.setEnd(pDelimiter.getEnd());
 
 delimiter.setId( "RepairedSlotValueDelimiter_" + this.annotationCounter);
 // VUIMAUtil.setProvenance( pJCas, delimiter, this.getClass().getName() );
 delimiter.setParent(pParent );
 delimiter.setProcessMe(false);
 delimiter.addToIndexes();
 
 return delimiter;
 
}  // End Method createDelimiter() -----------------------







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
  * initialize loads in the resources needed for slotValues. Currently, this involves
  * a list of known slot names that might show up.
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
   initialize( args);
  
 } // end Method initialize() ---------

 //----------------------------------
 /**
  * initialize 
  *
  * @param pArgs
  * 
  **/
 // ----------------------------------
 public void initialize(String pArgs[]) throws ResourceInitializationException {
   
   this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
   

} // end Method initialize() --------------
 
  // -----------------------------------------
  // class Variables
  // -----------------------------------------

//---------------------------------------
//Global Variables
//---------------------------------------
  private ProfilePerformanceMeter  performanceMeter = null;
  
  
} // end Class SlotValueRepairAnnotator ---------------------
