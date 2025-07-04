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
 * FeverAnnotator 
 *
 *  Turns mentions of fever to asserted clinical statements that a fever is present.
 *  
 *
 * @author  Guy Divita 
 * @created Jan 07, 2014
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.vinci.model.Fever;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.vinci.vitals.Temperature;


public class FeverAnnotator extends  JCasAnnotator_ImplBase {
 
  

  // -----------------------------------------
  /**
   * processTerms 
   * 
   * @param pJCas
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
   processTerms( pJCas);
   
   processSlotValues( pJCas);
   
   processTemperatureMentionsInProse( pJCas);
    
  } // end Method process 


  
  
  // -----------------------------------------
  /**
   * processTerms 
   * 
   * @param pJCas
   * 
   */
  // -----------------------------------------
  public void processTerms(JCas pJCas) throws AnalysisEngineProcessException {
  
  
    
    
    List<Annotation> terms = UIMAUtil.getAnnotations(pJCas, LexicalElement.typeIndexID );
    
    if ( terms != null && !terms.isEmpty() ) {
    
      for ( Annotation term : terms ) {
        
        String termClasses = ((LexicalElement)term).getSemanticTypes();
        
        if (termClasses != null && termClasses.length() > 0 ) {
          String[] termClassez = U.split( termClasses, ":");
          
          if ( termClassez != null && termClassez.length > 0 ) {
           for ( String termClass : termClassez ) {
              if       ( termClass.contains("Fever_Mentions")) {
                if ( !isInContentHeading(pJCas, term ) )
                createFeverMention( pJCas, term);
                break;
              } else if ( termClass.contains("Temperature")) {
                createTemperatureMention( pJCas, term);
                break;
              }
            }
          }
        }
      }
    }
      
      
    
  } // end Method processTerms 




  // =======================================================
  /**
   * processSlotValues looks for temperature mentions in
   * slots looks into the values in the value part, and determines
   * if this value indicates a fever mention
   * 
   * @param pJCas
   */
  // =======================================================
  public void processSlotValues(JCas pJCas) {
  
    List<Annotation> slotsAndValues = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID);
    
    if ( slotsAndValues != null && !slotsAndValues.isEmpty() ) {
      for ( Annotation slotValue : slotsAndValues ) {
        processSlotValue( pJCas, (SlotValue) slotValue);
      }
    }
    
    
  } // End Method processSlotValues() ======================




  // =======================================================
  /**
   * processSlotValue looks for temperature mentions in
   * the slot and looks into value part to determine
   * if this value indicates a fever mention
   * 
   * The side effect of this is that the temperature mention
   * will gain a numeric value.
   * 
   * @param pJCas
   * @param pSlotValue
   */
  // =======================================================
  public void processSlotValue(JCas pJCas, SlotValue pSlotValue) {
  
    ContentHeading slot = pSlotValue.getHeading();
    DependentContent aValue = pSlotValue.getDependentContent();
    List<Annotation> temperatureMentions = UIMAUtil.getAnnotationsBySpan(pJCas, Temperature.typeIndexID, slot.getBegin(), slot.getEnd());
    
    if ( temperatureMentions!= null && !temperatureMentions.isEmpty() ) {
      if ( aValue != null  ) {
        String buff = aValue.getCoveredText();
        if ( buff != null && U.containsNumber( buff ) ) {
          double aNumericValue = U.getNumericValue(  buff );
          ((Temperature)temperatureMentions.get(0)).setDoubleValue(aNumericValue);
          
          if ( isFeverValue( aNumericValue)) {
            createFeverMention(pJCas, pSlotValue);
          } else {
            Temperature largerTempMention = createTemperatureMention(pJCas, pSlotValue);
            largerTempMention.setDoubleValue( aNumericValue);
            ((Temperature)temperatureMentions.get(0)).removeFromIndexes();
                        
          }
        } else {
          // ----------------
          // if the value is "high"
          if ( buff.toLowerCase().contains("high") ||
               buff.toLowerCase().contains("hot")  ||
               buff.toLowerCase().contains("clammy")  ||
               buff.toLowerCase().contains("fever") ) {
            
            createFeverMention(pJCas, pSlotValue);
            ((Temperature)temperatureMentions.get(0)).removeFromIndexes();
          }
            
        }
      }
    }
  
  } // End Method processSlotValue() ======================




  // =======================================================
  /**
   * processTemperatureMentionsInProse finds temperature mentions in sentences,
   * looks for the nearest numeric value in the sentence from the mention, assumes that
   * it's the value of the temperature and figures out if this is a fever mention
   * 
   * @param pJCas
   */
  // =======================================================
  private void processTemperatureMentionsInProse(JCas pJCas) {
  
    List<Annotation> temperatureMention = UIMAUtil.getAnnotations(pJCas, Temperature.typeIndexID);
    
    if ( temperatureMention != null && !temperatureMention.isEmpty()) {
      for ( Annotation tempMention : temperatureMention ) {
        prcoessTemperatureMentionInProse( pJCas, tempMention);
      }
    }
    
  } // End Method processTemperatureMentionsInProse() ======================
  




  // =======================================================
  /**
   * prcoessTemperatureMentionInProse looks for the nearest numeric 
   *     value in the sentence from the mention, assumes that
   *     it's the value of the temperature and figures out if this is a fever mention
   * 
   * @param pJCas
   * @param pTemperatureMention
   */
  // =======================================================
  private void prcoessTemperatureMentionInProse(JCas pJCas, Annotation pTemperatureMention) {
    
    List<Annotation> sentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Sentence.typeIndexID, pTemperatureMention.getBegin(), pTemperatureMention.getEnd());

    if ( sentences!= null && !sentences.isEmpty()) {
      Annotation aSentence = sentences.get(0);
      
      // --------------------------------------------------------------
      // Iterate thru the terms of the sentence to find a numeric value from the trigger temperature 
      // mention to the right and if not found from the trigger temperature mention to the left
      Annotation triggerTerm = ((Temperature)pTemperatureMention).getParent();
      
      List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, aSentence.getBegin(), aSentence.getEnd());
      
      if ( terms != null && !terms.isEmpty()) {
        int triggerTermLocation = 0;
        for ( int i = 0; i < terms.size(); i++ ) {
          if ( terms.get(i) == triggerTerm ) {
            triggerTermLocation = i; 
            break;
          }
        }
        boolean found = false;
        // Traverse til end of the sentence
        for ( int i = triggerTermLocation + 1; i < terms.size(); i++ ) {
           String possibleValue = terms.get(i).getCoveredText();
           if ( U.containsNumber(possibleValue )) {
             double value = U.getNumericValue(possibleValue);
            if ( isFeverValue( value )) { 
                createFeverMention(pJCas, triggerTerm.getBegin(), terms.get(i).getEnd() );
                pTemperatureMention.removeFromIndexes();
                found = true;
                break;
            } else {
              createTemperatureMention(pJCas, triggerTerm.getBegin(), terms.get(i).getEnd(), value);
              found = true;
              break;
            }
            
          } else if ( possibleValue.toLowerCase().contains("hot") || 
                      possibleValue.toLowerCase().contains("warm") ||
                      possibleValue.toLowerCase().contains("high") ||
                      possibleValue.toLowerCase().contains("clammy") ) {
            createFeverMention(pJCas, triggerTerm.getBegin(), terms.get(i).getEnd() );
            pTemperatureMention.removeFromIndexes();
            found = true;
            break;
          } else if ( possibleValue.toLowerCase().contains("cool") ||
                      possibleValue.toLowerCase().contains("cold") ) {
            createTemperatureMention(pJCas, triggerTerm.getBegin(), terms.get(i).getEnd(), 0.0);
            found = true;
            break;
            
          }
        } // end loop to the right
        if ( !found ) {
          for ( int i = triggerTermLocation; i >= 0;  i-- ) {
            String possibleValue = terms.get(i).getCoveredText();
            if ( U.containsNumber(possibleValue )) {
              double value = U.getNumericValue(possibleValue);
             if ( isFeverValue( value )) { 
                 createFeverMention(pJCas, terms.get(i).getEnd() , triggerTerm.getBegin());
                 pTemperatureMention.removeFromIndexes();
                 found = true;
                 break;
             } else {
               createTemperatureMention(pJCas,  terms.get(i).getEnd(), triggerTerm.getBegin(), value);
               found = true;
               break;
             }
             
           }
         } // end loop to the left
        }
      }
    }
    
  } // End Method prcoessTemperatureMentionInProse() ======================
  




  // =======================================================
  /**
   * isFeverValue returns true if the numeric value is > feverThreshold
   * 
   * @param aNumericValue
   * @return boolean
   */
  // =======================================================
  private boolean isFeverValue(double aNumericValue) {
    
    boolean returnVal = false;
    // Determine if the range is in c of f 
    if ( aNumericValue > 90 && aNumericValue < 107 ) {
      if ( aNumericValue > this.feverThreshold_f) 
        returnVal = true;
    } else if ( aNumericValue > 35 && aNumericValue < 40 )
      if ( aNumericValue > this.feverThreshold_c )
        returnVal = true;
    
    return returnVal;
    // End Method isFeverValue() ======================
  }




  // ------------------------------------------
  /**
   * createFeverMention creates a feverMention annotation
   *
   *
   * @param pJCas
   * @param pAnnotation
   */
  // ------------------------------------------
  private void createFeverMention(JCas pJCas, Annotation pAnnotation) {
    
    Fever feverMention = new Fever( pJCas);
    
    feverMention.setBegin( pAnnotation.getBegin());
    feverMention.setEnd(   pAnnotation.getEnd());
    feverMention.addToIndexes();
    
    
  }  // End Method createFeverMention() -----------------------
  

  // ------------------------------------------
  /**
   * createFeverMention creates a feverMention annotation
   *
   *
   * @param pJCas
   * @param pAnnotation
   */
  // ------------------------------------------
  private void createFeverMention(JCas pJCas, int pBegin, int pEnd ) {
    
    Fever feverMention = new Fever( pJCas);
    
    feverMention.setBegin( pBegin);
    feverMention.setEnd(   pEnd);
    feverMention.addToIndexes();
    
    
  }  // End Method createFeverMention() -----------------------
  

 


  // ------------------------------------------
  /**
   * createTemperatureMention creates a temperature mention
   *   This will be a triger to find a value in a downstream process.
   *
   * @param pJCas
   * @param pAnnotation
   */
  // ------------------------------------------
  private Temperature createTemperatureMention(JCas pJCas, Annotation pAnnotation) {
    
    Temperature temp = new Temperature( pJCas);
    
    temp.setBegin( pAnnotation.getBegin());
    temp.setEnd(   pAnnotation.getEnd());
    temp.setParent( pAnnotation);
    
    temp.addToIndexes();
    
    return temp;
    
  }  // End Method createTemperatureMention() -----------------------
  


// =======================================================
    /**
     * createTemperatureMention [Summary here]
     * 
     * @param pJCas
     * @param begin
     * @param end
     */
    // =======================================================
    private void createTemperatureMention(JCas pJCas, int begin, int end, double pValue) {
      Temperature temp = new Temperature( pJCas);
      
      temp.setBegin( begin);
      temp.setEnd(   end);
      temp.setDoubleValue(pValue);
  
      
      temp.addToIndexes();
    } // End Method createTemperatureMention() ======================




  // =======================================================
  /**
   * isInContentHeading returns true if this term is in a 
   * content heading (not in a slot value)
   * 
   * @param pJCas
   * @param term
   * @return
   */
  // =======================================================
  private boolean isInContentHeading(JCas pJCas, Annotation pTerm) {
  
    boolean returnVal = false;
    
    List<Annotation> headings = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, ContentHeading.typeIndexID, pTerm.getBegin(), pTerm.getEnd());
  
    List<Annotation> slotValues = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, SlotValue.typeIndexID, pTerm.getBegin(), pTerm.getEnd());
  
    
    if ( headings != null && !headings.isEmpty())
      if ( slotValues == null ||slotValues.isEmpty())
        returnVal = true;
  
    return returnVal;
    
  } // End Method isInContentHeading() ======================



public static final double feverThreshold_f = 101.4;  // >=100.4   another interpretation 
public static final double feverThreshold_c = 38.555;  // >=38.2


  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
 
   
  } // end Method initialize() -------

//----------------------------------
/**
 * initialize reads in the wsd evidence file
 * 
 * @param pArgs
 *    called from the pipe method rather than
 * 
 * 
 **/
// ----------------------------------
public void initialize(String[] pArgs)   throws ResourceInitializationException {

 
  try {
    
   
    
    
  } catch (Exception e) {
    e.printStackTrace();
    String msg = "Issue with loading in the wsd file " + e.toString();
    System.err.println( msg );
    throw new ResourceInitializationException ();
  }
 
   
} // end Method initialize() -------
  

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected String AnnotatorClassName = "ProblemAnnotator";



 
  // --------------------------------------------
  // Global variables
  // --------------------------------------------
  
  
  
  
  
  
} // end Class MetaMapClient() ---------------
