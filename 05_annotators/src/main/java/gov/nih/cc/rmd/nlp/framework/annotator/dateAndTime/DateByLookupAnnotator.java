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
 * It recognizes dates by java pattern - not regex pattern - tooooo much time 
 * trying to hone regex's
 *
 * @author  Guy Divita 
 * @created Jan 08 2018
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.dateAndTime;

import java.awt.desktop.AboutEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.model.Date;
import gov.nih.cc.rmd.framework.model.Shape;
import gov.nih.cc.rmd.framework.model.PotentialNumber;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.nih.cc.rmd.nlp.lexUtils.U;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.WordToken;
import gov.va.vinci.model.Concept;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.temporal.AbsoluteDate;



public class DateByLookupAnnotator extends JCasAnnotator_ImplBase  {
 
  public static final String annotatorName = DateByLookupAnnotator.class.getSimpleName();

  
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    this.performanceMeter.startCounter();
    try {
      
    
      datesByLookup( pJCas);
      
      absoluteDateLookup( pJCas);
      
    //  cleanUpDates( pJCas);
      
    //  mergeDatesAndTimes( pJCas);
   
	    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue in DateAndTime Annotator " + e.toString());
    }
    
    this.performanceMeter.stopCounter();     
    
  } // end Method process() ----------------
   
  

  // =================================================
  /**
   * cleanUpDates finds dates, and the numbers or potential numbers within them
   * and removes the numbers and potential numbers
   * This removes dates within dates
   * 
   * @param pJCas
  */
  // =================================================
    private final void cleanUpDates(JCas pJCas) {
    
      
      List<Annotation> dates = UIMAUtil.getAnnotations(pJCas, Date.typeIndexID);
      
      if ( dates != null && !dates.isEmpty()) {
        
        dates = UIMAUtil.uniqueAnnotations(dates);
        for ( Annotation aDate: dates ) {
         
          
          int dateSize = aDate.getEnd() - aDate.getBegin();
          List<Annotation> numbers = UIMAUtil.getAnnotationsBySpan(pJCas, gov.nih.cc.rmd.framework.model.Number.typeIndexID , aDate.getBegin(), aDate.getEnd(), false );
          if ( numbers != null && !numbers.isEmpty() )
            for ( Annotation number: numbers )
              number.removeFromIndexes();
          
          List<Annotation> potentialNumbers = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, gov.nih.cc.rmd.framework.model.PotentialNumber.typeIndexID , aDate.getBegin() , aDate.getEnd() , false );
          if ( potentialNumbers != null && !potentialNumbers.isEmpty() ) 
            for ( Annotation number: potentialNumbers ) 
             number.removeFromIndexes();
            
          
          List<Annotation> datePieces = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, gov.nih.cc.rmd.framework.model.Date.typeIndexID , aDate.getBegin(), aDate.getEnd(), false );
          if ( datePieces != null && !datePieces.isEmpty() )
            for ( Annotation datePiece: datePieces ) {
              int datePieceSize = datePiece.getEnd() - datePiece.getBegin();
             if ( datePieceSize < dateSize )
               datePiece.removeFromIndexes();
            }
        }
      }
      
      
  } // end Method cleanUpDates() ----------------------



  // =================================================
  /**
   * mergeDatesAndTimes will create Date from sequences of date number date
   * 
   * @param pJCas
  */
  // =================================================
  private final void mergeDatesAndTimes(JCas pJCas) {
  
    List<Annotation> datesAndNumbers_ = UIMAUtil.getAnnotations(pJCas, Date.typeIndexID);
    List<Annotation> numbers = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.framework.model.Number.typeIndexID);
    List<Annotation> potentialNumbers = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.framework.model.PotentialNumber.typeIndexID);
    
    VAnnotation token1 = null;
    VAnnotation token2 = null;
    VAnnotation token3 = null;
    if ( datesAndNumbers_!= null && potentialNumbers!= null) {
        ArrayList<Annotation> datesAndNumbers = new ArrayList<Annotation>();
        datesAndNumbers.addAll( datesAndNumbers_);
        datesAndNumbers.addAll( potentialNumbers);
        UIMAUtil.sortByOffset(datesAndNumbers);
        for ( int i = 0; i < datesAndNumbers.size() -1; i++ ) {
           token1 = (VAnnotation)datesAndNumbers.get(i); 
           token2 = (VAnnotation) datesAndNumbers.get(i + 1);
          
          if ( i < datesAndNumbers.size() -2)
            token3 = (VAnnotation) datesAndNumbers.get(i + 2);
          
        
          
        //  if  ( nextToEachOther( token1, token2) && nextToEachOther( token2, token3) && !token1.getMarked() && token1.getClass().getName().contains("Date") && !token2.getMarked() && token2.getClass().getName().contains("Number") && token3!= null && token3.getClass().getName().contains("Date") ) 
        //    createDate( pJCas, token1, token2, token3);
          
       //   else
            if      (nextToEachOther( token1, token2) &&  !token1.getMarked() && token1.getClass().getName().contains("Date") && !token2.getMarked() && token2.getClass().getName().contains("Date"))
            createDate( pJCas, token1, token2);
          
          else if      (nextToEachOther( token1, token2) && !token1.getMarked() && token1.getClass().getName().contains("Date") && !token2.getMarked() && token2.getClass().getName().contains("Number"))
            createDate( pJCas, token1, token2);
         
          
        } // end loop
        
        for ( int i = 0; i < datesAndNumbers.size(); i++ ) 
          if ( ((VAnnotation) datesAndNumbers.get(i)).getMarked() )
             datesAndNumbers.get(i).removeFromIndexes() ;
        
    }
    
  } // end Method mergeDatesAndTimes() ----------------



  // =================================================
  /**
   * nextToEachOther returns true if the these tokens 
   * are within 2 chars of each other 
   * 
   * @param token1
   * @param token2
   * @return boolean
  */
  // =================================================
  private final boolean nextToEachOther(Annotation pToken1, Annotation pToken2) {
    boolean returnVal = false;
    
    if ( pToken1 != null && pToken2 != null && pToken2.getBegin() -2 <= pToken1.getEnd() )
      returnVal = true;
    
    
    return returnVal;
  } // end Method nextToEachOther() -----------------



  // =================================================
  /**
   * createDate creates one Date from the combined terms
   *  it removes any underlying terms, and marks the Shapes for
   *  later removal
   * 
   * @param pJCas
   * @param token1
   * @param token2
   * @param token3
   * @return Date
  */
  // =================================================
  private final Date createDate(JCas pJCas, VAnnotation token1, VAnnotation token2, VAnnotation token3) {
    
    Date statement = new Date ( pJCas);
    statement.setBegin( token1.getBegin());
    statement.setEnd( token3.getEnd() );
    statement.setId(   "DateByLookupAnnotator"  + ":createDate_1_" + this.annotationCounter);
    statement.addToIndexes();
    
    token1.setMarked(true);
    token2.setMarked(true);
    token3.setMarked(true);
    
    List<Annotation> termsToRemove = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, statement.getBegin(), statement.getEnd(), false );
    
    if ( termsToRemove!= null && !termsToRemove.isEmpty())
      for ( Annotation term : termsToRemove)
        term.removeFromIndexes();
    
    return statement;
    
    
  } // end Method createDate() ----------------------


  // =================================================
  /**
   * createDate creates one Date from the combined terms
   *  it removes any underlying terms, and marks the Shapes for
   *  later removal
   * 
   * @param pJCas
   * @param token1
   * @param token2
   * @return Date
  */
  // =================================================
  private final Date createDate(JCas pJCas, VAnnotation token1, VAnnotation token2 ) {
    
    Date statement = new Date ( pJCas);
    statement.setBegin( token1.getBegin());
    statement.setEnd( token2.getEnd() );
    statement.setId( "DateByLookupAnnotator" + ":createDate_2_" + this.annotationCounter);
    statement.addToIndexes();
    
    token1.setMarked(true);
    token2.setMarked(true);
   
    
    List<Annotation> termsToRemove = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, statement.getBegin(), statement.getEnd(), false );
    
    if ( termsToRemove!= null && !termsToRemove.isEmpty())
      for ( Annotation term : termsToRemove)
        term.removeFromIndexes();
    
    return statement;
    
    
  } // end Method createDate() ----------------------



  // =================================================
  /**
   * absoluteDateLookup 
   * 
   * @param pJCas
  */
  // =================================================
  private final void absoluteDateLookup(JCas pJCas) {
    
    
    List<Annotation> tokens = UIMAUtil.getAnnotations(pJCas, WordToken.typeIndexID );
    
    if ( tokens != null && !tokens.isEmpty()) {
     
      for ( Annotation token : tokens )
        simpleDateLookup( pJCas, token );
        
    }
    
  } // end Method absoluteDateLookup() ---------------



  // =================================================
  /**
   * absoluteDateLookup 
   * 
   *         01/07/2010
   *          1/7/2010
   *          1/07/2011
   *         12/1/2011
   *         
   *         2021-05-05   added 
   *         
   *         
   * @param pJCas
   * @param pToken
  */
  // =================================================
 private final void simpleDateLookup(JCas pJCas, Annotation pToken) {
    
   String buff = pToken.getCoveredText();
   
   boolean yearSeen = false;
   boolean couldBeMonth = false;
   boolean couldBeDay = false;
   String[] cols = null;
   
   try {
     if ( buff.length() > 5 )
       if ( !U.containsLetters(buff))
     if ( !U.isOnlyPunctuation( buff ) ) {
       String punct = getFirstPunctuation( buff);
       cols = U.split(buff, punct);
       if ( cols != null ) {
         int ctr = 0;
         for ( String col : cols ) {
           
           if ( col == null || col.trim().length() == 0 ) 
             return;
           if ( !U.containsPunctuation( col ) ) {
             double num = Double.valueOf(col );
             if ( num > 1900  && num < 2040 ) {
               yearSeen = true;
               ctr++;
             }
             else if ( num > 12 && num < 31 ) {
               couldBeDay = true;
               ctr++;
             } 
             else if ( num <= 12 ) {
               couldBeDay = true;
               couldBeMonth = true;
               ctr++;
             }
           }
         }
         
         if ( couldBeMonth && (couldBeDay || yearSeen ) && ctr >= 2)
            createDate( pJCas, pToken);
       }
     }
   } catch ( Exception e) {
     e.printStackTrace();
     System.err.println("issue finding a simple date " + buff + " " + e.toString());
   }
   
  } // end Method absoluteDateLookup() --------------



  // =================================================
  /**
   * getFirstPunctuation
   * 
   * @param pBuff
   * @return String
  */
  // =================================================
 private String getFirstPunctuation(String pBuff) {
    String returnVal = "";
    
    if ( pBuff != null ) {
      char[] buff = pBuff.toCharArray();
      
      for ( char chr : buff ) 
        if ( U.isPunctuation(chr )) {
          returnVal = Character.toString(chr);         
          break;
        }
      
    }
    return returnVal;
  } // end Method getFirstPunctuation() --------------



  // =================================================
  /**
   * DatesByLookup summary
   * 
   * @param pJCas
  */
  // =================================================
   private final void datesByLookup(JCas pJCas) {
    
    List<Annotation> allTerms = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.LexicalElement.typeIndexID, false);
    

    if (allTerms != null && !allTerms.isEmpty()) 
      processTerms( pJCas, allTerms );
     
     
  } // end method datesByLookup() ------------------





 // =======================================================
 /**
  * processTerms.  If the terms are cauti terms, creates cauti Concepts
  * from them.
  * 
  * @param pJCas
  * @param terms
  */
 // =======================================================
 private void processTerms(JCas pJCas, List<Annotation> terms) {
   
   
   if (terms != null) {
     
     
     
     // -----------------------------------------------------
     // Loop through each term
     // -----------------------------------------------------
     for (Annotation term : terms ) {
       try {
         
         // if ( the term has the type date, make it a date
         String types = ((LexicalElement)term).getSemanticTypes();
         String coveredText = term.getCoveredText();
       
         if ( types != null && types.contains("Date"))
           createDate( pJCas, term );
       } catch (Exception e) {
         e.printStackTrace();
       }
       
     } // end loop through terms
   } // end if there are any terms
   
 }  // End Method processTerms() ======================
 

 //-----------------------------------------
 /**
  * createDate
  * 
  * @param pJCas
  * @param pConcept
  */
 // -----------------------------------------
  void createDate( JCas pJCas, Annotation pConcept ) {
   
    
    boolean makeDate = false;
    
    List<Annotation> dates = null;
    dates = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Date.typeIndexID, pConcept.getBegin(), pConcept.getEnd() );
  
    if ( dates != null && !dates.isEmpty())
      makeDate = datesCoverConceptRange( pConcept, dates ) ;
    else
      makeDate = true;
    
    if ( makeDate ) {
 
    
      Date statement = new Date(pJCas);
      statement.setBegin(                    pConcept.getBegin());
      statement.setEnd(                       pConcept.getEnd());
      statement.setId( "DateByLookupAnnotator_createDate_3_" + this.annotationCounter++);
      statement.setSemanticTypes("Date" );
      statement.addToIndexes();  
      
    }
  
    if ( makeDate && dates != null && !dates.isEmpty()) 
      for (Annotation date: dates)
        date.removeFromIndexes(pJCas);
    
 } // end Method createDate()  -------------
  
 

 // =================================================
/**
 * datesCoverConceptRange checks to see if the pConcept span
 * covers the dates in the set of date tokens
 * 
 * @param pConcept
 * @param pDates
 * @return boolean
*/
// =================================================
  private final boolean datesCoverConceptRange(Annotation pConcept, List<Annotation> pDates) {
  
    boolean returnVal = true;
    
    
    if ( pDates != null && !pDates.isEmpty())  {
      int[]  maxSpans = UIMAUtil.getMaxSpans(pDates);
      
      if ( pConcept.getBegin() <= maxSpans[0] && pConcept.getEnd() >= maxSpans[1] )
        returnVal = true;
    } 
    
    
    return returnVal;
  } // end Method datesCoverConceptRange() ---------



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
* initialize initializes the annotator.  The context should include
* a String[] variable with "args" as the key
*   On the set of Strings, strings with the following 
*                            conceptName=MatchedConcept
*            strictPositiveRegExPatterns=/some/path/to/StrictPositiveRegexPatterns.regex
*        lessStrictPositiveRegExPatterns=/some/path/to/LessStrictPositiveRegexPatterns.regex
*       leastStrictPositiveRegExPatterns=/some/path/to/LeastStrictPositiveRegexPatterns.regex
*            strictNegativeRegExPatterns=/some/path/to/StrictNegativeRegexPatterns.regex
*        lessStrictNegativeRegExPatterns=/some/path/to/LessStrictNegativeRegexPatterns.regex
*       leastStrictNegativeRegExPatterns=/some/path/to/LeastStrictNegativeRegexPatterns.regex
* 
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
       GLog.println( GLog.ERROR_LEVEL, " Issue with DateByLookupAnnotator " + e.toString());
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
 protected ProfilePerformanceMeter       performanceMeter = null;
 private int annotationCounter = 0;

} // end Class DateByLookupAnnotator() ---------------
