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


import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.Section;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.Copper;
import gov.va.vinci.model.Gold;
import gov.va.vinci.model.Problem;
import gov.va.chir.model.TruePositive;
import gov.va.chir.model.TrueNegative;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

import java.io.File;
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



public class EvaluateAnnotator extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  private HashMap<String, int[]> goldStatus;

/**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    this.performanceMeter.startCounter();

    try {
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
    
    List<Annotation>    goldAnnotations = UIMAUtil.getAnnotations(pJCas, Gold.typeIndexID); 
    List<Annotation>  copperAnnotations = UIMAUtil.getAnnotations(pJCas, Copper.typeIndexID);
    List<Annotation>           sections = UIMAUtil.getAnnotations(pJCas, Section.type);
    List<Annotation>      trueNegatives = UIMAUtil.getAnnotations(pJCas,  TrueNegative.typeIndexID);
   //List<Annotation> clinicalStatements = UIMAUtil.getAnnotations(pJCas, Problem.typeIndexID);
    
    UIMAUtil.sortByOffset(goldAnnotations);
    UIMAUtil.sortByOffset(copperAnnotations);
    UIMAUtil.sortByOffset(sections);
    UIMAUtil.sortByOffset(trueNegatives);
    
    //this.total_TN = 0;
    if ( trueNegatives != null )
      this.total_TN += trueNegatives.size();
    
    if ( goldAnnotations != null )
    	System.err.println("The number of gold annotations = " + goldAnnotations.size());
    else { 
    	System.err.println("No gold annotations for file " + inputFileName);
    	
    }
    
    if ( copperAnnotations != null )
    	System.err.println("The number of copperAnnotations = " + copperAnnotations.size());


    
    
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
	      List<Annotation> matches = UIMAUtil.fuzzyFindAnnotationsBySpan( copperAnnotations, trueGold.getBegin(), trueGold.getEnd() );
   
	      if ( matches != null  && matches.size() > 0 ) {
	        boolean oneFound = false;
	   //     String goldAssertion = "unknown";
	   //     String copperAssertion = "unknown";
	        for ( Annotation match: matches ) { 
	          
	          setMarked( match, true );
	         
	          
	            oneFound = true;
	         
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

    } catch (Exception e ) {
      e.printStackTrace();
      String msg = "Issue with evaluation annotator :" + e.toString();
      GLog.println(msg);
    }
	  
    this.performanceMeter.stopCounter();
	  
  } // end Method process() ----------------
  


// 

  private boolean getMarked(Annotation pConcept) {
   boolean returnVal = false;
    try {
      returnVal =   ((VAnnotation)pConcept).getMarked();
    } catch (Exception e) {
      try {
        returnVal = ((gov.va.vinci.model.Concept)pConcept).getMarked();
      } catch (Exception e2) {
        e2.printStackTrace();
        System.err.println("Issue trying to get the marked status " + e2 );
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
        // System.err.println("key|" + sectionBegin + "|" + sectionEnd + "gold/copper|" + annotationBegin + "|" + annotationEnd );
        if (( sectionBegin <= annotationBegin ) &&
            ( sectionEnd   >= annotationEnd )) {
          section = aSection;
          returnVal = U.display( ((Section) section).getContentHeaderString());
          this.lastSectionSeen = i;
         //  System.err.println(" ----------------- found section " + returnVal);
          break;
        } // end if this annotation is sorounded by this section 
        else if ( sectionEnd > annotationEnd) {
        	// System.err.println("Went beyond");
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
    String lfs = getTextFrom( lTokens);
    String rfs = getTextFrom( rTokens);
    if ( lTokens == null && rTokens == null ) {
      lfs = U.display(docText.substring(beginL, begin));
      rfs = U.display(docText.substring(end, endR));
    } else {
       lfs = getTextFrom( lTokens);
       rfs = getTextFrom( rTokens);
   
    }
    String focus = U.display(pAnnotation.getCoveredText().toLowerCase());
    lfs = U.spacePadLeft(pCharWindow, lfs);
    rfs = U.spacePadRight(pCharWindow,rfs);
    focus = U.spacePadRight(30,focus) ;
    String offsets = pAnnotation.getBegin()  + "|" + pAnnotation.getEnd() ;
     
    
    retValue = U.spacePadRight(4, pMsg + ":|") + fileName + "|" + lfs + "|" + focus + "|" + rfs + "|" + category + "|" + sectionName + "|"  + assertion + "|" + offsets ;
    
    
    
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
  private String getTextFrom(List<Annotation> tokens) {

  String returnVal = null;

  if ( tokens != null ) {
    StringBuffer buff = new StringBuffer();
    for ( Annotation annotation : tokens ) {
        buff.append( U.display(annotation.getCoveredText()));
        buff.append(" ");
    } // end loop thru tokens
    returnVal = buff.toString().trim();
  } // end if the annotations are filled out

  return returnVal;
  } // end method getTextFrom() --------------


  
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
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
    super.destroy();
    
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
      System.err.println("Not able to open for writing the summaryFile " + this.summaryFileName);
    }
    System.err.println(" -----------------------------------------");
    System.err.println("  writing out the summary file " + this.summaryFileName);
    System.err.println(" ------------------------------------------");
    
    if ( out == null ) return;
    out.println(" --------------------------------------");
    out.println(" -- The number of assertions in the gold");
    Set<String> keys  = this.goldStatus.keySet();
    int total = 0;
    for ( String key : keys ) {
    	int[] freq = this.goldStatus.get(key);
    	out.println("   " + key + "|" + freq[0]);
    	total+=freq[0];
    }
    out.println( "      Total | " + total);
    
    double recall    = this.total_TP / (( this.total_TP + this.total_FN) + .00000001) ;
    double precision = this.total_TP / (( this.total_TP + this.total_FP)  + .00000001); 
    double accuracy  = (this.total_TP + this.total_TN)/ ((this.total_TP + this.total_TN + this.total_FP + this.total_FN) + .0000001); 
    double specificity = this.total_TN / ((this.total_TN + this.total_FP) + .0000001);
    double           f = 2 * ((precision*recall)/(precision+recall)); 
     
    out.println( "True  Positive (TP)                                  = " + this.total_TP);
    out.println( "True  Negative (TN)                                  = N/A" );
    out.println( "False Positive (FP)                                  = " + this.total_FP);
    out.println( "False Negative (FN)                                  = " + this.total_FN);
    out.println( "Recall                                  (tp/(tp+fn)) = "  + recall);
    out.println( "Sensitivity                             (tp/(tp+fn)) = "  + recall);
    out.println( "Precision                               (tp/(tp+fp)) = " + precision );
    out.println( "Positive Predictive value (PPV)         (tp/(tp+fp)) = " + precision );
    
    out.println( "Accuracy                      ((tp+tn)/(tp+tn+fp+fn) = " + accuracy );
    out.println( "Specificity                           (tn/(tn + fp)  = " + specificity);
    out.println(" F-Measure (2* (precision*recall)/(precision+recall)) = " + f);
    
    
   
    out.println("  +-----------------------+ ");
    out.println("  |Confusion Matrix       |");
    out.println("  +-----------+-----------+");
    out.println("  |  TP       |  FN       |");
    out.println("  |           |           |");
    out.println("  | " + String.format("%8d",this.total_TP) + "  | " +
                         String.format("%8d",this.total_FN) + "  |");
    out.println("  +-----------+-----------+");
    out.println("  |  FP       |  TN       |");
    out.println("  | " + String.format("%8d",this.total_FP) + "  | " + 
                         String.format("%8d",this.total_TN) + "  |");
    out.println("  +-----------+-----------+");
    
    out.println("\n\n\n\n"); 
  
    out.println(" ----------------------------------");
    out.println(" False Negatives                   ");
    out.println(" ----------------------------------");
    for ( String line : this.fnList)
      out.println( line);
    out.println(" ----------------------------------");
    
    out.println("\n\n\n\n"); 
    
    out.println(" ----------------------------------");
    out.println(" False Positives                   ");
    out.println(" ----------------------------------");
    for ( String line : this.fpList)
      out.println( line);
    out.println(" ----------------------------------");
    
 out.println("\n\n\n\n"); 
    
    out.println(" ----------------------------------");
    out.println(" True Positives                   ");
    out.println(" ----------------------------------");
    for ( String line : this.tpList)
      out.println( line);
    out.println(" ----------------------------------");
    
    out.close();
    

    
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
    
  

    
    goldType        = "Gold";
   // copperType      = "Copper";
    String args[] = null;
    try {
      args                 = (String[]) aContext.getConfigParameterValue("args");  

    } catch (Exception e ) {
      System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
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
    
      String threadId = Thread.currentThread().getName();
      String outputDir_      = U.getOption(pArgs, "--outputDir=" ,"./") ;
      this.outputDir         = outputDir_ + "/eval";
      U.mkDir(this.outputDir );
      U.mkDir(outputDir_ + "/logs");
    
      this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );

      
      this.summaryFileName = this.outputDir + "/eval_" + threadId + ".txt"; 

      this.goldStatus = new HashMap<String, int[]>(10);
  
       
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with setting up the evaluation " + e.getMessage();
  
      System.err.println( msg );
      throw new ResourceInitializationException ();
    }
  
  
  } // end Method initialize() -------
  
 
  
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  public static final String annotatorName = EvaluateAnnotator.class.getSimpleName();


  private int total_TP = 0; 
  private int total_TN = 0;
  private int total_FP = 0;
  private int total_FN = 0;
  private String          goldType = null;
 // private String        copperType = null;
 // private String           mudType = null;
  private org.apache.uima.cas.Type   goldAnnotationType = null;
 // private org.apache.uima.cas.Type   copperAnnotationType = null;
  private ArrayList<String> fnList = new ArrayList<String>();
  private ArrayList<String> fpList = new ArrayList<String>();
  private ArrayList<String> tpList = new ArrayList<String>();
  private String outputDir = null;
  private String summaryFileName = "./summaryStatistics.txt";
  ProfilePerformanceMeter              performanceMeter = null;

  private int lastSectionSeen = 0;
 
  
} // end Class MetaMapClient() ---------------
