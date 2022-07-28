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
 * Evaluation Consumer calculates TP, TN, FP, FN for
 * a corpus, and three kinds of annotations.
 *       
 * @author  Guy Divita 
 * @created March 15, 2015
 *
 * [Utah/Salt Lake City Preamble Here]
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.TrueNegative;
import gov.va.chir.model.TruePositive;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.Copper;
import gov.va.vinci.model.Gold;




public class EvaluateWithAttributeAnnotator extends JCasAnnotator_ImplBase implements Writer {
 
  
  // -----------------------------------------
  private HashMap<String, int[]> goldStatus;

// =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   * 
  **/
  // =================================================
  public EvaluateWithAttributeAnnotator(String[] pArgs) throws ResourceInitializationException {
    initialize( pArgs);
   
  }

//=================================================
 /**
  * Constructor
  *
  * @param pArgs
  * @throws ResourceInitializationException 
  * 
 **/
 // =================================================
 public EvaluateWithAttributeAnnotator() throws ResourceInitializationException {
  
  
 }





/**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    this.performanceMeter.startCounter();

    initializeTypes( pJCas);
	this.lastSectionSeen = 0; 
    String docType = getDocumentTitle( pJCas);
    
    // ------------------------------------
    // Create SystemA   Annotations  (Gold)
    // Create SystemB   Annotations  (copper)
    // 
    // ------------------------------------
    
    // ------------------------------------
    // Gather System A Labels
    // ------------------------------------
    String inputFileName = VUIMAUtil.getDocumentId(pJCas);
    
    List<Annotation>    goldAnnotations = UIMAUtil.getAnnotations(pJCas, Gold.typeIndexID, false); 
    List<Annotation>  copperAnnotations = UIMAUtil.getAnnotations(pJCas, Copper.typeIndexID, false);
    List<Annotation>           sections = UIMAUtil.getAnnotations(pJCas, SectionZone.type);
    List<Annotation>      trueNegatives = UIMAUtil.getAnnotations(pJCas,  TrueNegative.typeIndexID, false);
   //List<Annotation> clinicalStatements = UIMAUtil.getAnnotations(pJCas, Problem.typeIndexID);
    
    UIMAUtil.sortByOffset(goldAnnotations);
    UIMAUtil.sortByOffset(copperAnnotations);
    UIMAUtil.sortByOffset(sections);
    UIMAUtil.sortByOffset(trueNegatives);
    
    this.total_TN = 0;
    if ( trueNegatives != null )
      this.total_TN = trueNegatives.size();
    
    if ( goldAnnotations != null )
    	GLog.println(GLog.DEBUG_LEVEL,"The number of gold annotations = " + goldAnnotations.size());
    else { 
    	GLog.println(GLog.ERROR_LEVEL,"No gold annotations for file " + inputFileName);
    	
    }
    
    if ( copperAnnotations != null )
    	GLog.println(GLog.DEBUG_LEVEL,"The number of copperAnnotations = " + copperAnnotations.size());


    
    
	  // -----------------------------------
      // iterate thru gold
      // -----------------------------------
	  if ( goldAnnotations != null  ) {     // some records won't have gold annotations in them
	    for ( Annotation trueGold: goldAnnotations ) {
	      /*
	      this.log.trace("Looking at " + trueGold.getCoveredText() + "|" 
	          + trueGold.getBegin() + "|" + trueGold.getEnd() + "|" + trueGold.getType().getName() + "|" + 
	          trueGold.getAddress() );
	      */
	      // ------------------------------------------------------
	      // Look for any copper annotation that overlaps true gold
	      
	      List<Annotation> matches = null;
	      if ( this.fuzzyMatching )
	       matches = UIMAUtil.fuzzyFindAnnotationsBySpan( copperAnnotations, trueGold.getBegin(), trueGold.getEnd() );
	      else
	        matches = UIMAUtil.getAnnotationsBySpan( pJCas, Copper.typeIndexID, trueGold.getBegin(), trueGold.getEnd() );
	        
	        
	      if ( matches != null  && matches.size() > 0 ) {
	        boolean oneFound = false;
	   //     String goldAssertion = "unknown";
	   //     String copperAssertion = "unknown";
	        for ( Annotation match: matches ) { 
	          
	        	if ( attributeMatches( trueGold, match )) {
	        		setMarked( match, true );
	        		oneFound = true;
	        	}
	         
	        } // end loop  thru the matches
	        // -------------------------------------
	        // Mark the TP
	        if ( oneFound ) {
	          setMarked(trueGold, true);  
	          
	         // this.log.trace(trueGold.getCoveredText() + " is true positive");
	          this.total_TP++;
	          createAnnotation( pJCas, trueGold, "TP");
	          int windowSize = 55;
            String sectionName = getSection(sections,trueGold);  
            String  category = " ";// getSemanticTypes( pJCas, clinicalStatements, trueGold);
	          String line = concordanceLine( pJCas, trueGold, windowSize, "TP", docType, sectionName, category, "Asserted");
	        
	          this.tpList.add( line);
	        } else {
	            // ------------------------------
	            // the spans match but the negation status does not
	            // this is now considered a false negative
	            // ------------------------------
	        	 this.total_FN++;
	                createAnnotation( pJCas, trueGold, "FN");
	                 setMarked(trueGold, true);
	                
	                
	                int windowSize = 55;
	                String sectionName = getSection(sections, trueGold);
	                String  category = " ";// getSemanticTypes( pJCas, clinicalStatements, trueGold);
	                String line = concordanceLine( pJCas, trueGold, windowSize, "FN", docType, sectionName, category, "Asserted" );
	                this.fnList.add( line);
	               
	             
	          }
	        
	      } // end if there is a copper match
	      
	     
	    } // end loop through gold ---------------------------------
	  } // end if there are any gold
	      
	    // -----------------------------------
	    // iterate thru copper
	    // -----------------------------------
	    if ( copperAnnotations != null  ) {     // some records won't have copper annotations in them
	      for ( Annotation copper: copperAnnotations ) {
	        
	         try { if ( copper.getCoveredText() == null || copper.getCoveredText().trim().length() < 1) continue; } catch (Exception e2) { continue; }
	        // ---------------------------------
	        // look for any annotation not marked
	        if ( getMarked(copper) != true ) {
	        
	        	this.total_FP++;
	          createAnnotation(pJCas, copper, "FP");
	          setMarked(copper, true);
	          int windowSize = 55;
	          String sectionName = getSection(sections,copper);
	          String  category = " "; //getSemanticTypes( pJCas, clinicalStatements, copper);
	          String copperAssertionStatus = VUIMAUtil.getAssertionStatus (pJCas,copper ); //((VAnnotation)copper).getNegation_Status();
            String line = concordanceLine( pJCas, copper, windowSize, "FP", docType, sectionName, category, copperAssertionStatus);
       
            
          
            this.fpList.add( line);
            
	        }
	      } // end loop thru copperAnnotations 
	    } // end if there are any copper annotations
	    
	    
	    // -----------------------------------
	    // iterate thru gold again
	    // -----------------------------------
	    if ( goldAnnotations != null  ) {     // some records won't have gold annotations in them
	      this.lastSectionSeen = 0;
	      String goldAssertion = null;
		for ( Annotation gold: goldAnnotations ) {
	        // ---------------------------------
	    	// Figure out how many gold were negated
	    	
			goldAssertion =  VUIMAUtil.getAssertionStatus(pJCas, gold ); // ((VAnnotation)gold).getNegation_Status();
			if ( goldAssertion == null)   goldAssertion = "Asserted";
			int[] freq = this.goldStatus.get( goldAssertion);
			if (freq == null) {	freq = new int[1]; freq[0] = 0;}
			freq[0]++;
			this.goldStatus.put( goldAssertion, freq);
		
			
	        // look for any annotation not marked
	        if (  getMarked(gold) != true ) {
	        	
                this.total_FN++;
                createAnnotation( pJCas, gold, "FN");
                int windowSize = 55;
                String sectionName = getSection(sections, gold);
                String  category = " ";// getSemanticTypes( pJCas, clinicalStatements, gold);
       //         String goldAssertionStatus = ((VAnnotation)gold).getNegation_Status();
                String line = concordanceLine( pJCas, gold, windowSize, "FN", docType, sectionName, category, "Asserted");
                this.fnList.add( line);
           
                
	        }
	      } // end loop thru goldAnnotations 
	    } // end if there is any gold again -----
	    
	    
	    // ---------------------
	    // Loop thru the tn's - 
    
	  this.destroyAux();

    this.performanceMeter.stopCounter();
	  
  } // end Method process() ----------------
  




  private boolean attributeMatches(Annotation trueGold, Annotation copper) {
	
	  	
	  boolean returnVal = false;
	  String   goldFeatureValue = null;
	  String copperFeatureValue = null;
	  
	  try {
		  
		  if ( this.evaluationGoldFeatureName == null )
			  returnVal = true;
		  else  {
		  goldFeatureValue   = UIMAUtil.getFeatureValueByName(trueGold,   this.evaluationGoldFeatureName);
		  copperFeatureValue = UIMAUtil.getFeatureValueByName(copper,     this.evaluationCopperFeatureName);
	  
		  if ( goldFeatureValue != null && copperFeatureValue != null  && goldFeatureValue.compareTo(copperFeatureValue) == 0 )
			  returnVal = true;
		  else if ( goldFeatureValue == null && copperFeatureValue == null)
			  returnVal = true;
		  }
	  
	  } catch ( Exception e ) {
		  
	  }
  
	return returnVal;
} // end Method attributeMatches() --------



private boolean getMarked(Annotation pConcept) {
   boolean returnVal = false;
    try {
      returnVal =   ((VAnnotation)pConcept).getMarked();
    } catch (Exception e) {
      try {
        returnVal = ((gov.va.vinci.model.Concept)pConcept).getMarked();
      } catch (Exception e2) {
        e2.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL,"Issue trying to get the marked status " + e2 );
        throw new RuntimeException(e2);
      }
    }
  return returnVal;
  // End Method getMarked() ======================
}



  private void setMarked(Annotation match, boolean b) {
   
    try {
      ((VAnnotation)match).setMarked(b);
    } catch (Exception e) {
        ((gov.va.vinci.model.Concept)match).setMarked(b);
    }
  }  // End Method setMarked() ======================



  //----------------------------------------------
  /**
   * getSection retrieves the section that encompasses the span of the
   * annotation passed in.
   * 
   * There are two assumptions made here: the section list is ordered by offset
   * and calls to this routine will be looking for annotations that are also ordered
   * by offset.
   * 
   * @param pSections
   * @param pAnnotation
   * @return
   */
  // ----------------------------------------------
  private String getSection(List<Annotation> pSections, Annotation pAnnotation ) {
    
    Annotation section = null;
    String      returnVal = "";
    
    if  ( pSections != null ) {
      long annotationBegin = pAnnotation.getBegin();
      long   annotationEnd = pAnnotation.getEnd();
    
      Annotation  aSection = null;
      long    sectionBegin = -1;
      long      sectionEnd = -1;
      int    sectionLength = pSections.size();
    
    
      for ( int i = this.lastSectionSeen; i < sectionLength; i++ ) {
        aSection = pSections.get(i);
        sectionBegin = aSection.getBegin();
        sectionEnd   = aSection.getEnd();
        // GLog.println(GLog.DEBUG_LEVEL,"key|" + sectionBegin + "|" + sectionEnd + "gold/copper|" + annotationBegin + "|" + annotationEnd );
        if (( sectionBegin <= annotationBegin ) &&
            ( sectionEnd   >= annotationEnd )) {
          section = aSection;
          returnVal = U.display( ((SectionZone) section).getSectionName());
          this.lastSectionSeen = i;
         //  GLog.println(GLog.DEBUG_LEVEL," ----------------- found section " + returnVal);
          break;
        } // end if this annotation is sorounded by this section 
        else if ( sectionEnd > annotationEnd) {
        	// GLog.println(GLog.DEBUG_LEVEL,"Went beyond");
        	break;
        }
      } // end loop through the sections of a document
      if ( this.lastSectionSeen == sectionLength ) this.lastSectionSeen = 0;
    
    }
    
    return returnVal;
  } // end Method getSection() ---------------------


  private String getDocumentTitle(JCas pJCas) {

	String docTitle = "unknown";
	
	Annotation documentHeader = UIMAUtil.getAnnotation(pJCas, DocumentHeader.typeIndexID);
	if ( documentHeader != null ) {
		docTitle = ((DocumentHeader) documentHeader).getDocumentTitle();
		if ( docTitle == null )
				docTitle = "unknown";
	}
	
  	return docTitle;
  } // end Method getDocumentTitle


//-----------------------------------------
  /**
   * getSemanticTypes retrieves semantic types from the list of clinicical statements that 
   * overlap this annotation
   * 
   * @param pJCas
   * @param pSymptom
   * @return String
   */
  // -----------------------------------------
  private String getSemanticTypes(JCas pJCas, List<Annotation>pFullSet, Annotation pFocus ) {
  	String returnVal = "";
  	
  	List<Annotation> concepts = UIMAUtil.fuzzyFindAnnotationsBySpan(pFullSet, pFocus.getBegin(), pFocus.getEnd());

  	StringBuffer buff = new StringBuffer();
  	if ( concepts != null && concepts.size() > 0 ) {
  		for ( Annotation aConcept: concepts ) {
  			String semanticTypes = null;
  			semanticTypes = ((gov.va.vinci.model.Concept)aConcept).getCategories();
  			
  			buff.append( semanticTypes + ":");
  		}
  	}
  	if ( buff != null )
  	 returnVal = buff.toString();
  	
  	return returnVal;
  } // end Method getSemanticTypes() --------

// -----------------------------------------
  /**
   * createAnnotation creates evaluation annotations
   * 
   * @param pJCas
   * @param pAnnotation
   * @param string
   */
  // -----------------------------------------
  private void createAnnotation(JCas pJCas, Annotation annotation, String pType) {
    
    Annotation statement = null;
    if ( pType.equals("TP"))
        statement = new gov.va.chir.model.TruePositive(pJCas);
    else if ( pType.equals("TN"))
      statement = new gov.va.chir.model.TrueNegative(pJCas);
    else if ( pType.equals("FP"))
      statement = new gov.va.chir.model.FalsePositive(pJCas);
    else if ( pType.equals("FN"))
      statement = new gov.va.chir.model.FalseNegative(pJCas);
    else statement = new TruePositive(pJCas );
   
    
    
    statement.setBegin(        annotation.getBegin());
    statement.setEnd(          annotation.getEnd());  
    statement.addToIndexes();

  } // end createAnnotation() ----------------

//-----------------------------------------
  /**
   * concordanceLine formats the context around an annotation
   * into the following format   msg|token3 token2 token1 |annotation| token1 token2 token3
   * 
   * @param pJCas
   * @param pAnnotation
   * @param pMsg
   * @param sectionName 
   * @return
   */
  // -----------------------------------------
  private String concordanceLine(JCas       pJCas, 
                                 Annotation pAnnotation, 
                                 int        pCharWindow, 
                                 String     pMsg , 
                                 String     docType, 
                                 String     sectionName, 
                                 String     category,
                                 String     assertion) {
    String retValue = null;
    
    // ---------------------------------------
    // get the line that this annotation is on
    String fileName = VUIMAUtil.getDocumentId(pJCas);
    fileName = fileName.substring(fileName.lastIndexOf('/') + 1 );
    fileName = fileName.substring(fileName.lastIndexOf('\\') + 1 );
    int begin = pAnnotation.getBegin();
    int end = pAnnotation.getEnd();
    int beginL = begin - pCharWindow;
    if (beginL < 0 ) beginL = 0;
    int endR = end + pCharWindow;
    String docText = pJCas.getDocumentText();
    int docTextSize = docText.length();
    if ( endR > docTextSize ) endR = docTextSize;
    List<Annotation> lTokens = UIMAUtil.getAnnotationsBySpan(pJCas, gov.va.chir.model.WordToken.typeIndexID, beginL, begin);
    List<Annotation> rTokens = UIMAUtil.getAnnotationsBySpan(pJCas, gov.va.chir.model.WordToken.typeIndexID, end, endR);
    String lfs = getTextFrom( pJCas, lTokens);
    String rfs = getTextFrom( pJCas, rTokens);
    if ( lTokens == null && rTokens == null ) {
      lfs = U.display(docText.substring(beginL, begin));
      rfs = U.display(docText.substring(end, endR));
    } else {
       lfs = getTextFrom(pJCas, lTokens);
       rfs = getTextFrom(pJCas, rTokens);
   
    }
    String focus = "";
    try { 
    
      if ( this.dontLowerCase )
        focus = U.display( pAnnotation.getCoveredText() );
      else
        focus = U.display(pAnnotation.getCoveredText().toLowerCase()); 
    } catch (Exception e) {};
    lfs = U.spacePadLeft(pCharWindow, lfs);
    rfs = U.spacePadRight(pCharWindow,rfs);
    focus = U.spacePadRight(30,focus) ;
    String offsets = pAnnotation.getBegin()  + "|" + pAnnotation.getEnd() ;
     
    
    retValue = U.spacePadRight(4, pMsg + ":|") + U.spacePadRight(25,fileName) + "|" + lfs + "|" + focus + "|" + rfs + "|" + category + "|" + sectionName + "|"  + assertion + "|" + offsets ;
    
    
    
    return retValue;
  } // end Method concordanceLine() ---------

//-----------------------------------------
  /**
  * getTextFrom retrieves the text from the spans of the annotations passed in
  *  (it is presumed that this list of annotations is in span order and not overlapping.)
  * 
  * Newlines are taken out.
  * 
  * @param tokens
  * @return String
  */
  //-----------------------------------------
  private String getTextFrom(JCas pJCas, List<Annotation> tokens) {

  String returnVal = null;

  if ( tokens != null ) {
    StringBuffer buff = new StringBuffer();
    
    if ( this.showNewLines ) {
      returnVal = getTextFromCoveredText( pJCas, tokens );
      returnVal = evalNormalizeShowNewlines( returnVal);
      
    } else { 
    
      for ( Annotation annotation : tokens ) {
        buff.append( U.display(annotation.getCoveredText()));
        buff.append(" ");
      } // end loop thru tokens
      returnVal = buff.toString().trim();
    }
  } // end if the annotations are filled out

  return returnVal;
  } // end method getTextFrom() --------------


  
  // =================================================
/**
 * evalNormalizeShowNewlines transforms newlines in the text into ~
 * conflate multiple spaces into one.
 * @param pBuff
 * @return String
*/
// =================================================
private final String evalNormalizeShowNewlines(String pBuff) {
  
  String returnVal = pBuff;
  returnVal= returnVal.replace('\n', '~');
  returnVal= returnVal.replaceAll ("  ", " ");
  
  return returnVal;
} // end Method evalNormalizeShowNewlines() ----------





  // =================================================
/**
 * getTextFromCoveredText returns the covered text covered by 
 * a set of annotations that are given
 * 
 *  Assume the tokens are in offset order
 *  @param pJCas
 * @param pTokens
 * @return String
*/
// =================================================
private final String getTextFromCoveredText( JCas pJCas, List<Annotation> pTokens) {
  
  String returnVal = null;
  
  int begin = pTokens.get(0).getBegin();
  int _end  = pTokens.get( pTokens.size() -1).getEnd() ;
  returnVal = pJCas.getDocumentText();
  returnVal = returnVal.substring( begin, _end);
  
  
  return returnVal;
}





  // -----------------------------------------
  /**
   * initializeTypes initializes the 
   * gold, copper, and mud types from the
   * input parameters.  This only needs to be
   * done once per corpus.  This would have
   * been done in the initialize method, but
   * the JCas isn't available at that point. 
   * 
   * There should be a way to look into the tpye
   * descriptor before getting a jcas handle. I just
   * don't know how at this point.
   * 
   * @param pJCas 
   * 
   * 
   */
  // -----------------------------------------
  private void initializeTypes(JCas pJCas) {
    
    if ( this.goldAnnotationType == null ) {
      try {
        goldAnnotationType   = UIMAUtil.getAnnotationType(pJCas,   this.goldType);
      //  copperAnnotationType = UIMAUtil.getAnnotationType(pJCas, this.copperType);
           
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("One of the types is not valid");     
      }
    }
 
  } // end Method initializeTypes() ----------

  // -----------------------------------------
  /** 
   * destroy prints out the summary numbers and the
   * recall, precision, and f-metric
   *
   */
  // -----------------------------------------
@Override
  public void destroy() {

    destroyAux();
    
    if ( this.performanceMeter != null )
      this.performanceMeter.writeProfile( this.getClass().getSimpleName());
  
  }

  // -----------------------------------------
  /** 
   * destroyAux prints out the summary numbers and the
   * recall, precision, and f-metric
   *
   */
  // -----------------------------------------
  private void destroyAux() {

    // --------------------------------
    // write out the statistics
    // --------------------------------
    PrintWriter out = null;
    try {
       out = new PrintWriter( this.summaryFileName);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Not able to open for writing the summaryFile " + this.summaryFileName);
    }
    GLog.println(GLog.STD___LEVEL," -----------------------------------------");
    GLog.println(GLog.STD___LEVEL,"  writing out the summary file " + this.summaryFileName);
    GLog.println(GLog.STD___LEVEL," ------------------------------------------");
    
    if ( out == null ) return;
    
    String report = report();
    out.print( report );
    out.close();
    

    
  } // end Method Destroy() -----------------


//-----------------------------------------
 /** 
  * destroyAux prints out the summary numbers and the
  * recall, precision, and f-metric
  *
  */
 // -----------------------------------------
 public final String report() {

   StringBuffer buff = new StringBuffer();
  
  
   buff.append(" --------------------------------------\n");
   buff.append( "-- " + this.reportTitle +          "--\n");
   buff.append(" --------------------------------------\n\n");
   buff.append(" -- The number of assertions in the gold\n");
   Set<String> keys  = this.goldStatus.keySet();
   int total = 0;
   for ( String key : keys ) {
       int[] freq = this.goldStatus.get(key);
       buff.append("   " + key + "|" + freq[0]);
       total+=freq[0];
   }
   buff.append( "      Total | " + total + "\n");
   
   double recall    = this.total_TP / (( this.total_TP + this.total_FN) + .00000001) ;
   double precision = this.total_TP / (( this.total_TP + this.total_FP)  + .00000001); 
   double accuracy  = (this.total_TP + this.total_TN)/ ((this.total_TP + this.total_TN + this.total_FP + this.total_FN) + .0000001); 
   double specificity = this.total_TN / ((this.total_TN + this.total_FP) + .0000001);
   double           f = 2 * ((precision*recall)/(precision+recall)); 
    
   buff.append( "True  Positive (TP)                                  = " + this.total_TP + "\n");
   buff.append( "True  Negative (TN)                                  = N/A \n" );
   buff.append( "False Positive (FP)                                  = " + this.total_FP + "\n");
   buff.append( "False Negative (FN)                                  = " + this.total_FN + "\n");
   buff.append( "Recall                                  (tp/(tp+fn)) = "  + recall + "\n");
   buff.append( "Sensitivity                             (tp/(tp+fn)) = "  + recall + "\n");
   buff.append( "Precision                               (tp/(tp+fp)) = " + precision + "\n");
   buff.append( "Positive Predictive value (PPV)         (tp/(tp+fp)) = " + precision + "\n");
   
   buff.append( "Accuracy                      ((tp+tn)/(tp+tn+fp+fn) = " + accuracy + "\n");
   buff.append( "Specificity                           (tn/(tn + fp)  = " + specificity + "\n");
   buff.append(" F-Measure (2* (precision*recall)/(precision+recall)) = " + f + "\n");
   
   
  
   buff.append("  +-----------------------+\n ");
   buff.append("  |Confusion Matrix       |\n");
   buff.append("  +-----------+-----------+\n");
   buff.append("  |  TP       |  FN       |\n");
   buff.append("  |           |           |\n");
   buff.append("  | " + String.format("%8d",this.total_TP) + "  | " +
                        String.format("%8d",this.total_FN) + "  |\n");
   buff.append("  +-----------+-----------+\n");
   buff.append("  |  FP       |  TN       |\n");
   buff.append("  | " + String.format("%8d",this.total_FP) + "  | " + 
                        String.format("%8d",this.total_TN) + "  |\n");
   buff.append("  +-----------+-----------+\n");
   
   buff.append("\n\n\n\n"); 
 
   buff.append(" ----------------------------------\n");
   buff.append(" False Negatives                   \n");
   buff.append(" ----------------------------------\n");
   for ( String line : this.fnList )
     buff.append( line + "\n");
   buff.append(" ----------------------------------\n");
   
   buff.append("\n\n\n\n"); 
   
   buff.append(" ----------------------------------\n");
   buff.append(" False Positives                   \n");
   buff.append(" ----------------------------------\n");
   for ( String line : this.fpList )
     buff.append( line + "\n");
   buff.append(" ----------------------------------\n");
   
buff.append("\n\n\n\n"); 
   
   buff.append(" ----------------------------------\n");
   buff.append(" True Positives                    \n");
   buff.append(" ----------------------------------\n");
   for ( String line : this.tpList)
     buff.append( line + "\n");
   buff.append(" ----------------------------------\n");
   
 return buff.toString();
   
 } // end Method Destroy() -----------------


  
  
  //----------------------------------
  /**
   * initialize loads in the resources needed for phrase chunking. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
    String args[] = null;
    try {
      args                 = (String[]) aContext.getConfigParameterValue("args");  

    } catch (Exception e ) {
      GLog.println(GLog.ERROR_LEVEL,"Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
    

    initialize( args );
 
  } // end initialize() ---------------

  //----------------------------------
  /**
   * initialize sets up the output dir - will add files to the $outputDir/eval directory.
   *   This annotator expects Gold and Copper annotations to be pre-populated.
   * 
   * @param args   looking for
   *    --outputDir=   (will create an eval directory off it.)
   * 
   **/
  // ----------------------------------
  public void initialize(String pArgs[]) throws ResourceInitializationException {
    
    try {
    
      String dateStamp = U.getDateStampSimple();
      String threadId = Thread.currentThread().getName();
      String outputDir_      = U.getOption(pArgs, "--outputDir=" ,"./") ;
      this.reportTitle = U.getOption( pArgs,  "--reportTitle=", "---") ;
      this.reportTitle = this.reportTitle + " Report run on " + dateStamp;
      this.dontLowerCase = Boolean.parseBoolean(U.getOption(pArgs, "--evaluationFocusDontLowerCase=",  "false"));
      this.showNewLines = Boolean.parseBoolean(U.getOption(pArgs,  "--evaluationShowNewlines=", "false"));      
      
      this.outputDir         = outputDir_ + "/eval";
      U.mkDir(this.outputDir );
      U.mkDir(outputDir_ + "/logs");
    
       this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
      

      this.summaryFileName = this.outputDir + "/eval_" + threadId + ".txt"; 

      this.goldStatus = new HashMap<String, int[]>(10);
  
       this.evaluationGoldFeatureName = U.getOption(pArgs, "--evaluationGoldFeatureName=", "Bowel_Prep_Quality");
       this.evaluationCopperFeatureName = U.getOption(pArgs, "--evaluationCopperFeatureName=", "bowelPreparationQuality");
       this.fuzzyMatching = Boolean.parseBoolean( U.getOption(pArgs,  "--fuzzyMatching=", "true"));
      
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with setting up the evaluation " + e.getMessage();
  
      GLog.println(GLog.ERROR_LEVEL, msg );
      throw new ResourceInitializationException ();
    }
  
  
  } // end Method initialize() -------
  
 
  
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  public static final String annotatorName = EvaluateWithAttributeAnnotator.class.getSimpleName();


  private int total_TP = 0; 
  private int total_TN = 0;
  private int total_FP = 0;
  private int total_FN = 0;
  private String          goldType   = "Gold";
 // private String        copperType = null;
 // private String           mudType = null;
  private org.apache.uima.cas.Type   goldAnnotationType = null;
 // private org.apache.uima.cas.Type   copperAnnotationType = null;
  private ArrayList<String> fnList = new ArrayList<String>();
  private ArrayList<String> fpList = new ArrayList<String>();
  private ArrayList<String> tpList = new ArrayList<String>();
  private String evaluationGoldFeatureName = null;
  private String evaluationCopperFeatureName = null;
  private String outputDir = null;
  private String summaryFileName = "./summaryStatistics.txt";
  protected ProfilePerformanceMeter       performanceMeter = null;
  private  String reportTitle = "";
  private boolean fuzzyMatching = true;

  private int lastSectionSeen = 0;
  private boolean dontLowerCase = false;
  private boolean showNewLines =  false;
 
  
} // end Class MetaMapClient() ---------------
