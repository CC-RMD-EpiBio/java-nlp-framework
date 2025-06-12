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
 * RedCatAnnotator uses the regular expressions created by the RedCat tool
 * to find and extract patterns from documents.  Both positive and negative
 * patterns are found and there is a back-off mechanism built in to find patterns
 * if no strict patterns are found.
 * 
 * The patterns will be labeled with the --ConceptName=XXXX passed in.  They will be 
 * made into Concept and Concept with Negative assertions.
 * 
 * TBD - hook in Leo's dynamic annotation scheme to convert Concept into conceptName
 *
 * @author  Guy Divita 
 * @created Sept 29, 2017
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.Group;
import gov.va.chir.model.Line;
import gov.va.chir.model.TrueNegative;
import gov.va.vinci.model.Concept;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

/**
 * Evaluates a set of regular expression rules against documents and adds
 * annotations defined in those rules.
 * 
 * @author divita
 */
public class RedCatAnnotator extends JCasAnnotator_ImplBase {



  // =======================================================
   /**
    * process
    * 
    * @param pJCas
    *
    */
   // ======================================================  
	@Override
	public void process(JCas pJCas) throws AnalysisEngineProcessException {

	  this.performanceMeter.startCounter();

	  try {
	    
	    // Need to pass to the process method an annotation that covers the span of the
	    // whole document.  In the past, DocumentAnnotation came automatically with UIMA
	    // that was an annotation that had the correct span.  DocumentAnnotation is 
	    // causing issues downstream.  So, I'm making a Group annotation temporarily
	    // to do the same thing.
	    
	    String docText = pJCas.getDocumentText();
	    
	    if ( docText != null && docText.length() > 0 ) {
	    
	      Annotation documentAnnotation = createFocus(pJCas, 0, pJCas.getDocumentText().length());
	    
	      process( pJCas, documentAnnotation );
	    
	      documentAnnotation.removeFromIndexes();
	    }
	
	  
	    
	  } catch (Exception e) {
		  e.printStackTrace();
		  String msg = "Issue with finding patterns from regular expressions : " + e.toString() ;
		  GLog.println( GLog.ERROR_LEVEL,msg);
		}
		
		this.performanceMeter.stopCounter();
	} // end Method process() ==============================
 
// =======================================================
 /**
  * process processes each snippet
  * 
  * @param pJCas
  * @param pSnippet 
 * @throws Exception 
  *
  */
 // ======================================================  
protected void process(JCas pJCas, Annotation pSnippet) throws Exception {
 
  
   List<Annotation> positiveConcepts = new ArrayList<Annotation>();
   List<Annotation> negativeConcepts = new ArrayList<Annotation>();
   try {
  
	   String buff = pSnippet.getCoveredText().toLowerCase();
     // GLog.println( GLog.DEBUG_LEVEL,"---------------------------------------snippet begin----");
     // GLog.println( GLog.DEBUG_LEVEL, buff);
     // GLog.println( GLog.DEBUG_LEVEL,"---------------------------------------snippet end------");
     
    
      
     boolean patternFound = true; 
     if ( !matchPatterns( pJCas, pSnippet,  true, this.strictPositiveRegExPatterns, "strictPositive", positiveConcepts ) )
    	if (!matchPatterns( pJCas, pSnippet,  true, this.lessStrictPositiveRegExPatterns, "lessStrictPositive", positiveConcepts) )
    	  if (!matchPatterns(pJCas, pSnippet,  true, this.leastStrictPositiveRegExPatterns, "leastStrictPositive", positiveConcepts) )
    		  matchPatterns(pJCas, pSnippet,  true, this.manualPositiveRegExPatterns, "manualPositive", positiveConcepts) ;
    	  else 
    	    patternFound = false;
     
    	 
     // don't make negative instances if there are no positive patterns found
     // only look for the negative pattern in the same line?
     if ( patternFound && !positiveConcepts.isEmpty() ) {
     
       // find the line that the postiveConcepts are in and only look for the negative patterns in that line?
       for ( Annotation aPositiveInstance : positiveConcepts ) {
         
         Annotation line = getLineOfInstance(pJCas, aPositiveInstance );
         matchPatterns( pJCas, line,  false, this.manualNegativeRegExPatterns, "manualNegative", negativeConcepts); 
         matchPatterns( pJCas, line,  false, this.strictNegativeRegExPatterns, "strictNegative", negativeConcepts) ;
         matchPatterns( pJCas, line,  false, this.lessStrictNegativeRegExPatterns, "lessStrictNegative", negativeConcepts) ;
         matchPatterns( pJCas, line,  false, this.leastStrictNegativeRegExPatterns, "leastStrictNegative", negativeConcepts) ;
       }  // loop through positive concepts found
     } 
        
   
     
     // ------------------------------------------------
     // weed out concepts that overlap trueNegatives 
     weedOutNegativeConcepts(pJCas, positiveConcepts, negativeConcepts);
     
     // -----------------------------------------------
     // weed out overlapping concepts from multiple hits
    if (this.weedOutOverlappingConcepts) 
        weedOutOverlappingConceptsPoor( pJCas, pSnippet, positiveConcepts );
     
    
     
    } catch ( Exception e ) {
      e.printStackTrace();
      GLog.println( GLog.ERROR_LEVEL,"Issue within method process " + e.getMessage() );
      throw e;
    }
  } // End Method process ============
  

  // =================================================
/**
 * getLineOfInstance returns the line annotation that
 * contains the annotation in question
 * 
 * @param pJCas
 * @param pAnnotation
 * @return Annotation
*/
// =================================================
private Annotation getLineOfInstance(JCas pJCas, Annotation pAnnotation) {
  
  Annotation returnVal = null;
  
  List<Annotation> lines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Line.typeIndexID, pAnnotation.getBegin(), pAnnotation.getEnd());
  
  if ( lines != null && !lines.isEmpty())
   returnVal = lines.get(0);
  
  
  return returnVal;
} // end Method getLineOfInstance() ------------------------

  // =======================================================
 /**
  * weedOutOverlappingConcepts returns the first largest of the overlapping
  * concepts .  Use this version of the method when
  * there is no window around the text passed in.
  * 
  * If you have a snippet where you are expecting only one
  * mention within that snippet, use the other version. It performs better
  * under those circumstances. 
  * 
  * @param pJCas
  * @param pSnippet
  * @param pOverlappingConcepts
  *
  */
 // ======================================================	
private void weedOutOverlappingConceptsPoor(JCas pJCas, Annotation pSnippet, List<Annotation> pOverlappingConcepts) {
  
   try {
  
     if ( pOverlappingConcepts == null ) return;
     
     if ( pOverlappingConcepts.size() > 1 ) {
       int beginOffset = 0;
       int endOffset = 0;
       boolean overlapping = false;
       
       beginOffset = pOverlappingConcepts.get(0).getBegin();
       endOffset = pOverlappingConcepts.get(0).getEnd();
       int max = pOverlappingConcepts.get(0).getCoveredText().length();
       int biggest = 0;
       
       for ( int i = 1; i < pOverlappingConcepts.size(); i++ ) {
         Annotation aConcept = pOverlappingConcepts.get(i);
       
         if ( endOffset > aConcept.getBegin() && aConcept.getBegin() >= beginOffset ) {
           ((Concept) aConcept).setMarked(true);
           endOffset = aConcept.getEnd();
           overlapping = true;
           
           if ( aConcept.getCoveredText().length() > max ) {
             max = aConcept.getCoveredText().length() ;
             biggest = i;
           }
           
         }
      } // end loop thru the overlapping concepts
      
       if ( overlapping ) {
         for ( int i = 0; i < pOverlappingConcepts.size(); i++ ) {
           Annotation aConcept = pOverlappingConcepts.get(i);
           if ( !((Concept) aConcept).getMarked())
             aConcept.removeFromIndexes();
           else if ( i != biggest )
               aConcept.removeFromIndexes();
         } // end loop thru overlapping concepts
       } else {
         for ( int i = 0; i < pOverlappingConcepts.size(); i++ ) {
           Annotation aConcept = pOverlappingConcepts.get(i);
           if ( i != biggest )
               aConcept.removeFromIndexes();
         } // end loop thru overlapping concepts
       }
     } // end if there are multple concepts 
     
    } catch ( Exception e ) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,this.getClass(), "weedOutOverlappingConcepts", "Issue within method weedOutOverlappingConcepts " + e.getMessage() );
      throw e;
    }
  } // End Method weedOutOverlappingConcepts ============
  

  // =======================================================
   /**
    * matchPatterns finds patterns and creates annotations around them
    * 
    * @param pJCas
    * @param pSnippet
    * @param pPositive   true if positive false if not
    * @param pRegExPatterns
    * @param pPatternFile
    * @param pReturnedConceptsFound  <--- pass in an instantiated list, passes back the concepts created around the matches.
    * @return boolean   true if one or more patterns were found
   * @throws Exception 
    *
    */
   // ======================================================  
  protected boolean matchPatterns(JCas pJCas, Annotation pSnippet, boolean pPositive, Pattern[] pRegExPatterns, String pPatternFile, List<Annotation> pReturnedConceptsFound) throws Exception {
   
    boolean returnVal = false;
     try {
   
       if ( pRegExPatterns != null ) {
         
        for ( int i = 0; i < pRegExPatterns.length; i++ ) {
        	
        	if ( matchPatterns( pJCas, pSnippet, pPositive, pRegExPatterns[i], i, pPatternFile , pReturnedConceptsFound)){
              returnVal = true;
             
              if ( pPositive)
            	  break;
        	}
          
        } // end loop through patterns
       } // end if there are any regex patterns
     } catch (Exception e) {
       e.printStackTrace();
     }
     return returnVal;
  } // end Method matchPatterns() =============================
   
 
  
// =======================================================
/**
* matchPatterns finds patterns and creates annotations around them
* 
* @param pJCas
* @param pSnippet
* @param pPositive   true if positive false if not
* @param pRegExPattern
 * @param pPatternId 
* @return boolean   true if one or more patterns were found
* @throws Exception 
*
*/
// ======================================================  
protected boolean matchPatterns(JCas pJCas, Annotation pSnippet, boolean pPositive, Pattern pRegExPattern, int pPatternId, String pPatternFile, List<Annotation>pReturnedConceptsFound) throws Exception {

 boolean returnVal = false;
 try {

   
   if (pRegExPattern != null) {
    
     // GLog.println(GLog.DEBUG_LEVEL, "------- regex pattern ---->>>>>");
     // GLog.println(GLog.DEBUG_LEVEL, pRegExPattern.toString() ) ;
     // GLog.println(GLog.DEBUG_LEVEL, "------- end regex pattern <<<<<");
     
     String snippetText = pSnippet.getCoveredText().toLowerCase();
     snippetText = snippetText.replace('\n',  ' ');
     snippetText = snippetText.replace('\r', ' ');
     int currentOffset = pSnippet.getBegin();
     Matcher matcher = pRegExPattern.matcher(snippetText);
     // GLog.println(GLog.DEBUG_LEVEL, "------- Looking at |" + snippetText + "|");

    
     while (matcher.find()) {
   
       
       String matchedText = snippetText.substring(matcher.start(), matcher.end()) ;
       
       
       if ( matchedText == null || matchedText.trim().length() == 0 ) {
        // GLog.println( GLog.DEBUG_LEVEL,"               this pattern " + pRegExPattern.toString()  + "matched too little " );
       }
        // GLog.println( GLog.DEBUG_LEVEL,"                  =====> in text this is |" + matchedText + "|");
       
       returnVal = true;

      
       
       String focusTerm = null;
 
       // capture the focus term in the pattern 
       
       int numberOfGroups = matcher.groupCount();
       int focusOffsetBegin = -1;
       for (int i = 0; i < numberOfGroups; i++) {
           
         if ( numberOfGroups == 1 )
           focusTerm = matcher.group();
         
         else if ( i < numberOfGroups -1  )
           focusTerm = matcher.group(i + 1);
         else
           focusTerm = matcher.group();
        
         if ( focusTerm == null ) {
           // System.err.println("something went wrong, " + matcher.group() + " i = " + i );
           focusTerm = matcher.group();
         }
         
         focusOffsetBegin = matcher.group().indexOf( focusTerm) + matcher.start() + currentOffset ;
         
         
         int focusOffsetEnd   = focusOffsetBegin + focusTerm.length();
         createFocus(pJCas, focusOffsetBegin, focusOffsetEnd );
       } // end capture the focusTerm 
        
      
       
       String info = pPatternFile + ":" + pPatternId + ":"  + pRegExPattern.toString() + ":";
       String assertionValue = "Asserted";
       
       Annotation aConcept = null;
       if (!pPositive) { 
         assertionValue = "Negated";
       
         aConcept = createTrueNegative(pJCas, matcher.start() + currentOffset, matcher.end() + currentOffset, assertionValue, info, focusTerm);
       } else {
         
        
          
           aConcept = createConcept(pJCas, matcher.start() + currentOffset, matcher.end() + currentOffset, assertionValue, info, focusTerm);
       }
       /*
       GLog.println( GLog.DEBUG_LEVEL,"              Match found :" + matcher.group());
       GLog.println( GLog.DEBUG_LEVEL,"              Positive: "    + pPositive);
       GLog.println( GLog.DEBUG_LEVEL,"              Start index: " + matcher.start());
       GLog.println( GLog.DEBUG_LEVEL,"              End index: " + matcher.end() + " ");
       GLog.println( GLog.DEBUG_LEVEL,"              matching pattern |" + matcher.pattern() + "|");
       GLog.println( GLog.DEBUG_LEVEL,"              patternID    : " + pPatternId);
       GLog.println( GLog.DEBUG_LEVEL,"              pattern file : " + pPatternFile);
       */
       
       if (aConcept != null) 
         pReturnedConceptsFound.add( aConcept);

       if ( pPositive )
    	   break;
     } // end loop thru matches
   } // end if there are any regular expressions

 } catch (Exception e) {
   e.printStackTrace();
   String msg = "Issue within method matchPatterns " + e.getMessage();
   throw new Exception(msg);
 }

 return returnVal;
} // End Method matchPatterns ========================

  
  
	// ----------------------------------------
	/**
	 * createConcept creates a concept given capture group details from a
	 * regular expression match.

   * @param pJCas
	 * @param pBegin
	 * @param pEnd
	 * @param pAsserionStatus  (Asserted|Negated)
	 * @param pMatchedExpression 
	 * @param pFocusTerm 
   * @return Concept
	 */
	// -----------------------------------------
	protected final Concept createConcept(JCas pJCas,  int pBegin,  int pEnd, String pAssertionStatus,  String pMatchedExpression, String pFocusTerm) {
	
		   DocumentHeader documentHeader = VUIMAUtil.getDocumentHeader(pJCas);
		    String otherMetaData = documentHeader.getOtherMetaData();
		
		Concept concept = new Concept(pJCas);
		concept.setBegin(pBegin);
		concept.setEnd(pEnd);
		concept.setCuis( "0000");
		concept.setAssertionStatus(pAssertionStatus);
		concept.setCategories( pMatchedExpression );
		
		concept.setId("regEx_0000" );
	   concept.setConceptNames( this.conceptName );
	  
	  concept.setOtherInfo(pFocusTerm + ":" + otherMetaData + ":" + "MatchedExpression:" + pMatchedExpression);
	//  System.err.println("pattern -> |" + pMatchedExpression + "|");
	  //System.err.println("found   -> |" + concept.getCoveredText() + "|" );
	concept.addToIndexes();

		return concept;
	} // end Method createConcept() ----------
	
	 // ----------------------------------------
  /**
   * createFocus creates an annotation to capture the group within the regular expression pattern.
   *  FYI - I used the Group label - because of lack of time to add
   *  to the type descriptor a Focus label.  This really should be a Focus label. 

   * @param pJCas
   * @param pBegin
   * @param pEnd
     */
  // -----------------------------------------
  protected final Annotation createFocus(JCas pJCas,  int pBegin,  int pEnd ) {
  
    Group concept = new Group(pJCas);
    concept.setBegin(pBegin);
    concept.setEnd(pEnd);
   
    
    concept.setId("redCatAnnotator_0000" );
    
   
    concept.addToIndexes();

    return concept;
   
  } // end Method createFocus() ----------
	
//----------------------------------------
 /**
  * createTrueNegative creates a true negative concept given the capture group details from a
  * regular expression match.

  * @param pJCas
  * @param pBegin
  * @param pEnd
  * @param pAsserionStatus  (Asserted|Negated)
  * @param pMatchedExpression 
 * @param pFocusTerm 
  * @return Concept
  */
 // -----------------------------------------
 protected final Annotation createTrueNegative(JCas pJCas,  int pBegin,  int pEnd, String pAssertionStatus,  String pMatchedExpression, String pFocusTerm) {
  TrueNegative concept = new TrueNegative( pJCas);
   concept.setBegin(pBegin);
   concept.setEnd(pEnd);
   concept.setNegation_Status("Negated");
   concept.setId("regEx_0000" );
   String otherFeatures[] = new String[1];
   
   
   DocumentHeader documentHeader = VUIMAUtil.getDocumentHeader(pJCas);
   String otherMetaData = documentHeader.getOtherMetaData();

   otherFeatures[0] = pFocusTerm + ":" + otherMetaData + ":MatchedExpression:" + pMatchedExpression ;
   concept.setOtherFeatures( UIMAUtil.string2StringArray(pJCas, otherFeatures) );
   concept.addToIndexes();
   
   return concept;

 } // end Method createConcept() ----------


  // =======================================================
 /**
  * weedOutNegativeConcepts weeds out those concepts
  * that overlap in some way with true negative (patterns
  * that found negative examples)
  * 
  * @param pJCas
  *
  */
 // ======================================================	
protected void weedOutNegativeConcepts(JCas pJCas ) {
  
  
   try {
     
     List<Annotation> positiveConcepts = UIMAUtil.getAnnotations(pJCas, Concept.typeIndexID );
     List<Annotation> negativeConcepts = UIMAUtil.getAnnotations(pJCas, TrueNegative.typeIndexID);
         
     weedOutNegativeConcepts( pJCas, positiveConcepts, negativeConcepts);
     
     
    } catch ( Exception e ) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,this.getClass(), "weedOutNegitiveConcepts", "Issue within method weedOutNegativeConcepts " + e.getMessage() );
      throw e;
    }
  } // End Method weedOutNegativeConcepts ============

//=======================================================
/**
* weedOutNegativeConcepts within the bounds of the snippet passed in
* 
* @param pJCas
* @param pSnippet 
*
*/
// ======================================================  
protected void weedOutNegativeConcepts(JCas pJCas, Annotation pSnippet) {


try {
  List<Annotation> positiveConcepts = UIMAUtil.getAnnotationsBySpan(pJCas, Concept.typeIndexID , pSnippet.getBegin(), pSnippet.getEnd());;
  List<Annotation> negativeConcepts = UIMAUtil.getAnnotationsBySpan(pJCas, TrueNegative.typeIndexID, pSnippet.getBegin(), pSnippet.getEnd());;
   
  weedOutNegativeConcepts( pJCas, positiveConcepts, negativeConcepts);
  
  

 } catch ( Exception e ) {
   e.printStackTrace();
   GLog.println(GLog.ERROR_LEVEL,"Issue within method weedOutNegativeConcepts " + e.getMessage() );
   throw e;
 }
} // End Method weedOutNegativeConcepts ============

// =======================================================
 /**
  * weedOutNegativeConcepts 
  * 
  * @param pJCas
  * @param pSnippet void
  *
  */
 // ======================================================	
protected void weedOutNegativeConcepts(JCas pJCas, List<Annotation> pPositiveConcepts, List<Annotation> pNegativeConcepts ) {
 
   try {
  
     if ( pPositiveConcepts != null && !pPositiveConcepts.isEmpty() && pNegativeConcepts != null && !pNegativeConcepts.isEmpty()) {
       
       for ( Annotation aConcept : pPositiveConcepts ) {
         List<Annotation> overlappingConcepts = UIMAUtil.fuzzyFindAnnotationsBySpan(pNegativeConcepts, aConcept.getBegin(), aConcept.getEnd() );
         
         if ( overlappingConcepts != null && !overlappingConcepts.isEmpty())
           aConcept.removeFromIndexes();
         
       }
     }
    } catch ( Exception e ) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,this.getClass(), "weedOutNegativeConcepts", "Issue within method weedOutNegativeConcepts " + e.getMessage() );
      throw e;
    }
  } // End Method weedOutNegativeConcepts ============

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
// ======================================================  
  public void initialize(UimaContext pContext)	throws ResourceInitializationException {
   
      String args[] = null;
     
      try {
        args                 = (String[]) pContext.getConfigParameterValue("args");  
        initialize( args );
        
      } catch (Exception e ) {
        GLog.println( GLog.ERROR_LEVEL,"Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
        throw new ResourceInitializationException();
      }
	  
  } // end Method initialize() ------------


// =======================================================
 /**
  * initialize
  *  On the set of Strings, strings with the following 
 *                           --conceptName=MatchedConcept
 *                           --debugGroupPatterns=true|false  (default is false)
 *            --strictPositiveRegExPatterns=/some/path/to/StrictPositiveRegexPatterns.regex
 *        --lessStrictPositiveRegExPatterns=/some/path/to/LessStrictPositiveRegexPatterns.regex
 *       --leastStrictPositiveRegExPatterns=/some/path/to/LeastStrictPositiveRegexPatterns.regex
 *            --strictNegativeRegExPatterns=/some/path/to/StrictNegativeRegexPatterns.regex
 *        --lessStrictNegativeRegExPatterns=/some/path/to/LessStrictNegativeRegexPatterns.regex
 *       --leastStrictNegativeRegExPatterns=/some/path/to/LeastStrictNegativeRegexPatterns.regex
  * 
  * @param args 
 * @throws Exception 
  *
  */
 // ======================================================	
public void initialize(String[] pArgs) throws Exception {
 
   try {
  
     this.conceptName = U.getOption(pArgs, "--conceptName=", "Concept");
     String      _strictPositiveRegExPatterns = U.getOption(pArgs, "--strictPositiveRegExPatterns=", "");
     String  _lessStrictPositiveRegExPatterns = U.getOption(pArgs, "--lessStrictPositiveRegExPatterns=", "");
     String _leastStrictPositiveRegExPatterns = U.getOption(pArgs, "--leastStrictPositiveRegExPatterns=", "");
     String      _strictNegativeRegExPatterns = U.getOption(pArgs, "--strictNegativeRegExPatterns=", "");
     String  _lessStrictNegativeRegExPatterns = U.getOption(pArgs, "--lessStrictNegativeRegExPatterns=", "");
     String _leastStrictNegativeRegExPatterns = U.getOption(pArgs, "--leastStrictNegativeRegExPatterns=", "");
     String      _manualNegativeRegExPatterns = U.getOption(pArgs, "--manualNegativeRegExPatterns" ,     "");
     String      _manualPositiveRegExPatterns = U.getOption(pArgs, "--manualPositiveRegExPatterns" ,     "");
     weedOutOverlappingConcepts = Boolean.valueOf(U.getOption(pArgs,  "--weedOutOverlappingConcepts", "false"));
     
     
     
     
     this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
     
     this.strictPositiveRegExPatterns      = loadRegularExpressions( _strictPositiveRegExPatterns     );
     this.lessStrictPositiveRegExPatterns  = loadRegularExpressions( _lessStrictPositiveRegExPatterns );
     this.leastStrictPositiveRegExPatterns = loadRegularExpressions( _leastStrictPositiveRegExPatterns);
     this.strictNegativeRegExPatterns      = loadRegularExpressions( _strictNegativeRegExPatterns     );
     this.lessStrictNegativeRegExPatterns  = loadRegularExpressions( _lessStrictNegativeRegExPatterns );
     this.leastStrictNegativeRegExPatterns = loadRegularExpressions( _leastStrictNegativeRegExPatterns);
     this.manualNegativeRegExPatterns      = loadRegularExpressions( _manualNegativeRegExPatterns);
     this.manualPositiveRegExPatterns      = loadRegularExpressions( _manualPositiveRegExPatterns);
     
     
     
     
     
    } catch ( Exception e ) {
      e.printStackTrace();
      GLog.println( GLog.ERROR_LEVEL,"Issue within method initialize " + e.getMessage() );
      throw new Exception(e);
    }
  } // End Method initialize ============
  



  // =======================================================
 /**
  * loadRegularExpressions creates an array of Pattern from
  * the filename passed in.  The fileName can be the path from a resource.
  * 
  *    The file passed in should include one regular expression per newline.
  *    
  *    The return value is a pattern, where each regular expression from the file becomes
  *    a group.
  *    
  * 
  * @param pFileName
  * @return Pattern 
  * @throws Exception
  *
  */
 // ======================================================	
  protected final Pattern[] loadRegularExpressions(String pFileName ) throws Exception {
 
    Pattern patterns[] = null;
    StringBuffer buff = new StringBuffer();
    String rows[] = null;
   try {
  	
     if (pFileName != null && !pFileName.isEmpty() && !pFileName.equals("")) {
      
       if ( pFileName.startsWith("resources")) {
         try {
           rows = U.readClassPathResourceIntoStringArray( pFileName);
         } catch (Exception e) {
           e.printStackTrace();
           String msg = "Issue reading in regular expression file " + pFileName + " " + e.toString();
           GLog.println( GLog.ERROR_LEVEL, this.getClass(), "loadRegularExpressions", msg );
         }
       } else {
         try {
           rows = U.readFileIntoStringArray(pFileName);
        } catch (Exception e9 ) {
          String msg = "Issue reading in regular expression file " + pFileName + " " + e9.toString();
          GLog.println( GLog.ERROR_LEVEL, this.getClass(), "loadRegularExpressions", msg );
        } // end try/catch 
       }
     } else {
       String msg = "No regular expresson filename passed in ";
       GLog.println( GLog.ERROR_LEVEL,msg);
       return( patterns);
     } 
     if ( rows != null ) {
         patterns = new Pattern[ rows.length];
         
         for (int i = 0; i < rows.length; i++ ) {
         
           if (  rows[i] != null && !rows[i].trim().isEmpty() && !rows[i].startsWith("#")) {  //<------ ignore commented rows 
             
             String t = rows[i].trim();
             int k = t.indexOf('~');
             if ( k > -1 )
               t = t.substring(k + 1);
             try {
               Pattern tmpPattern = Pattern.compile( t);  
               patterns[i]= tmpPattern;
             
             } catch (Exception e2) {
               e2.printStackTrace();
               String msg = "Issue with compiling the pattern " + i + ": "+ rows[i].trim() + " " + e2.toString() ;
               GLog.println( GLog.ERROR_LEVEL,msg);
             }
           } // end if it's not a comment
         } // end Loop thru the list of regular expressions
      } // end if there are rows to work from
   
    } catch ( Exception e3 ) {
      e3.printStackTrace();
      String msg = "Issue within method loadRegularExpressions " + e3.getMessage() ;
      throw new Exception( msg);
    }
    return patterns;
  } // End Method loadRegularExpressions ============
  





  // ---------------------------------------
// Global Variables
// ---------------------------------------
   protected ProfilePerformanceMeter       performanceMeter = null;
   private String                            conceptName = "Concept";
   protected boolean              weedOutOverlappingConcepts = false;
   
   protected Pattern[]           strictPositiveRegExPatterns = null;
   protected Pattern[]       lessStrictPositiveRegExPatterns = null; 
   protected Pattern[]      leastStrictPositiveRegExPatterns = null; 
   protected Pattern[]           strictNegativeRegExPatterns = null;
   protected Pattern[]       lessStrictNegativeRegExPatterns = null;
   protected Pattern[]      leastStrictNegativeRegExPatterns = null;
   protected Pattern[]           manualNegativeRegExPatterns = null;
   protected Pattern[]           manualPositiveRegExPatterns = null;
   
	
}
