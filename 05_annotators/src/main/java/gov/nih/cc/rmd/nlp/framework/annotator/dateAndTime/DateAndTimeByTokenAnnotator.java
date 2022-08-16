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
 * DateAndTimeAnnotator identifies date and time parts
 *
 * @author  Guy Divita 
 * @created Jan 08 2018
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.model.Date;
import gov.nih.cc.rmd.framework.model.Number;
import gov.nih.cc.rmd.nlp.framework.annotator.RedCatAnnotator;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Line;
import gov.va.chir.model.Token;
import gov.va.chir.model.WordToken;
import gov.va.vinci.model.Concept;



public class DateAndTimeByTokenAnnotator extends JCasAnnotator_ImplBase  {
 
  public static final String annotatorName = DateAndTimeByTokenAnnotator.class.getSimpleName();

  
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * DatesByToken Look for word tokens with numbers in them
   * see if they look like dates.  These are meant to catch
   * dates that are missed by regular expression
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    this.performanceMeter.startCounter();
    try {
         
      
      List<Annotation> numbers = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.framework.model.PotentialNumber.typeIndexID );
      
      if ( numbers != null && !numbers.isEmpty() ) {
        
        for ( Annotation aNumber : numbers ) {
          String buff = aNumber.getCoveredText();
          List<Annotation> aWordTokenz = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, WordToken.typeIndexID, aNumber.getBegin(), aNumber.getEnd() );
          if ( aWordTokenz != null && !aWordTokenz.isEmpty() ) {
            WordToken aWordToken = (WordToken) aWordTokenz.get(0);
            if ( !aWordToken.getMarked() &&
               //  !containsDate ( pJCas, aWordToken ) &&
                 isADate( pJCas, aWordToken ) ) {
              
              createDate( pJCas, aWordToken );
              aWordToken.setMarked(true);
              
            }
         }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue in DateAndTime Annotator " + e.toString());
    }
    
 
    
    this.performanceMeter.stopCounter();     
    
  } // end Method process() ----------------
   
  
 

  // =================================================
  /**
   * containsDate 
   * 
   * @param pJCas
   * @param aWordToken
   * @return boolean
  */
  // =================================================
  private final boolean containsDate(JCas pJCas, WordToken pWordToken) {
    
    boolean returnVal = false;
    
    List<Annotation> dates = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Date.typeIndexID,  pWordToken.getBegin(), pWordToken.getEnd() );
    if ( dates != null && !dates.isEmpty())
      returnVal = true;
    
    return returnVal;
  } // end Method containsDate() ---------------------


  // =================================================
  /**
   * isADate 
   * 
   * @param pJCas
   * @param aWordToken
   * @return boolean
  */
  // =================================================
 private final boolean isADate(JCas pJCas, WordToken aWordToken) {
    
   boolean returnVal = false;
   
   String buff = aWordToken.getCoveredText();
   
  // String month = "(3[01]|[12][0-9]|0[1-9])";
   String month = "[0-1][1-9]" ;
   String delimiter = "\\/";
   String day = "[0|1|2|3][0-9]";
   String partYear = "[0-9][0-9]";
   String year = "(\\b[1|2][0-9])" ;
   String time = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$";
   
   
   
   if ( buff.matches( "^" +  year ) ||
        buff.matches( "^" +  month + delimiter + year ) ||
        buff.matches( "^" +  month + delimiter + partYear ) ||
        buff.matches( "^" +  month + delimiter + day + delimiter + year ) ||
        buff.matches( "^" +  month + delimiter + day + delimiter + year  +  time ) ||
        buff.matches( "^" +   time ) 
       )
     return true;
   
   
   return returnVal;
   
  } // end Method isDate() ---------------------------


  

  // =================================================
  /**
   * transformConceptsToDateConcepts 
   * 
   * @param pJCas
   * @param pConcepts
  */
  // =================================================
 private void transformConceptsToDateConcepts(JCas pJCas, List<Annotation> pConcepts) {
   
   if (pConcepts != null && !pConcepts.isEmpty()) {
     for ( Annotation concept: pConcepts ) {
       Annotation date = createDate( pJCas, concept);
       concept.removeFromIndexes(pJCas);
      
     }
   }
     
  } // end Method transformConceptsToDateConcepts() ----


 //-----------------------------------------
 /**
  * createDate
  * 
  * @param pJCas
  * @param pConcept
 * @return 
  */
 // -----------------------------------------
 Annotation createDate( JCas pJCas, Annotation pConcept ) {
   
   
   Date statement = new Date(pJCas);
   statement.setBegin(                    pConcept.getBegin());
   statement.setEnd(                       pConcept.getEnd());
	 statement.setNegation_Status("Asserted");
   statement.setId( "DateAndTimeByTokenAnnotator_" + this.annotationCtr++);
   statement.setSemanticTypes("DateAndTime" );
  
  
	 statement.addToIndexes();       
	 
	 return statement;
 } // end Method createDate()  -------------
  
 

 /**
  * destroy
  * 
  **/
 public void destroy() {
    if ( this.performanceMeter != null )
      this.performanceMeter.writeProfile( this.getClass().getSimpleName());
 }

 

//=======================================================
/**
* initialize initializes the annotator.  
* @param pContext
*  @throws ResourceInitializationException 
*
*/
//======================================================  
 public void initialize(UimaContext pContext)  throws ResourceInitializationException {
  
     String args[] = null;
    
     try {
       args                 = (String[]) pContext.getConfigParameterValue("args");  
       initialize( args );
       
     } catch (Exception e ) {
       GLog.println( GLog.ERROR_LEVEL,"Issue in DateAndTimeAnnotator " + e.toString());
       throw new ResourceInitializationException();
     }
   
 } // end Method initialize() ------------

 
//----------------------------------
/**
 * initialize loads in the resources needed
 * 
 * @param pArgs
 * 
 **/
// ----------------------------------
public void initialize(String[] pArgs) throws ResourceInitializationException {
  
 
  try {
    
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
  
   
  } catch (Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, "Issue initializing the dateAndTimeAnnotator " + e.toString() );
    throw new ResourceInitializationException();
  }
  
} // end Method initialize()


 // ---------------------------
 // Global Variables
 // ---------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  

} // end Class DateAndTimeAnnotator() ---------------
