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


import gov.va.chir.model.VAnnotation;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Logger;


public class EvaluateWithGoldAnnotator extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  /**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    this.performanceMeter.startCounter();

    initializeTypes( pJCas);
	  
    // ------------------------------------
    // Create SystemA   Annotations  (mud)
    // Create SystemB   Annotations  (copper)
    // Create Reference Annotations  (Gold)
    // ------------------------------------
    
    // ------------------------------------
    // Gather System A Labels
    // ------------------------------------
    List<Annotation>    goldAnnotations = UIMAUtil.getAnnotations(pJCas, this.goldAnnotationType);
    List<Annotation>    goldAnnotations2 = UIMAUtil.getAnnotations(pJCas, gov.va.vinci.model.Vague_mentions.type);
    if ( goldAnnotations2 != null)
      goldAnnotations.addAll( goldAnnotations2);
    
    List<Annotation>  copperAnnotations = UIMAUtil.getAnnotations(pJCas, this.copperAnnotationType, UIMAUtil.WITHOUT_SUBCLASSES); // <---- here's the problem too broad
    List<Annotation>     mudAnnotations = UIMAUtil.getAnnotations(pJCas, this.mudAnnotationType);
    
	  // -----------------------------------
    // iterate thru gold
    // -----------------------------------
	  if ( goldAnnotations != null  ) {     // some records won't have gold annotations in them
	    for ( Annotation trueGold: goldAnnotations ) {
	      // ------------------------------------------------------
	      // Look for any copper annotation that overlaps true gold
	      List<Annotation> matches = UIMAUtil.fuzzyFindAnnotationsBySpan( copperAnnotations, trueGold.getBegin(), trueGold.getEnd() );
   
	      if ( matches != null && matches.size() > 0 ) {
	        for ( Annotation match: matches ) 
	          ((VAnnotation)match).setMarked(true);
	        
	          // -------------------------------------
	          // Mark the TP -- one per match - that will keep the number of matches in line with vectors
	          ((VAnnotation)trueGold).setMarked(true);  
	          this.total_TP++;
	          createAnnotation( pJCas, trueGold, "TP");
	        
	      } // end if there is a copper match
	      
	      // ------------------------------------------------------
	      // Look for any mud annotation that overlaps true gold
	      List<Annotation> mudMatches = UIMAUtil.fuzzyFindAnnotationsBySpan( mudAnnotations, trueGold.getBegin(), trueGold.getEnd() );
	      if ( matches != null ) {
	        for ( Annotation match: mudMatches )
            ((VAnnotation)match).setMarked(true);
	      } // end if there is a mud match
	    } // end loop through gold ---------------------------------
	      
	    // -----------------------------------
	    // iterate thru copper
	    // -----------------------------------
	    if ( copperAnnotations != null  ) {     // some records won't have copper annotations in them
	      for ( Annotation copper: copperAnnotations ) {
	        // ---------------------------------
	        // look for any annotation not marked
	        if ( ((VAnnotation)copper).getMarked() != true ) {
	          this.total_FP++;
	          createAnnotation(pJCas, copper, "FP");
	        }
	      } // end loop thru copperAnnotations 
	    } // end if there are any copper annotations
	    
	    
	    // -----------------------------------
	    // iterate thru gold again
	    // -----------------------------------
	    if ( goldAnnotations != null  ) {     // some records won't have gold annotations in them
	      for ( Annotation gold: goldAnnotations ) 
	        // ---------------------------------
	        // look for any annotation not marked
	        if ( ((VAnnotation)gold).getMarked() != true ) {
                this.total_FN++;
                createAnnotation( pJCas, gold, "FN");
                int windowSize = 35;
                String line = concordanceLine( pJCas, gold, windowSize, "FN");
                this.fnList.add( line);
	          }
	        } // end loop thru goldAnnotations 
	  } // end if there is any gold again -----

	  // -----------------------------------
    // iterate thru mud
    // -----------------------------------
    if ( mudAnnotations != null  ) {     // some records won't have gold annotations in them
      for ( Annotation mud: mudAnnotations ) {
        if ( ((VAnnotation)mud).getMarked() != true ) {
          this.total_TN++;
          createAnnotation( pJCas, mud, "TN");
        }
      } // end loop thru mud annotations
    } // end if there are any mud annotations
	  
	  
    this.performanceMeter.stopCounter();
	  
    
    
  } // end Method process() ----------------
  
  

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
    else
      statement = new gov.va.chir.model.TruePositive(pJCas);
   
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
  * @return
  */
 // -----------------------------------------
 private String concordanceLine(JCas pJCas, Annotation pAnnotation, int pCharWindow, String pMsg ) {
   String retValue = null;
   
   // ---------------------------------------
   // get the line that this annotation is on
   int begin = pAnnotation.getBegin();
   int end = pAnnotation.getEnd();
   int beginL = begin - pCharWindow;
   if (beginL < 0 ) beginL = 0;
   int endR = end + pCharWindow;
   String docText = pJCas.getDocumentText();
   int docTextSize = docText.length();
   if ( endR > docTextSize ) endR = docTextSize;
   List<Annotation> lTokens = UIMAUtil.getAnnotationsBySpan(pJCas, gov.va.chir.model.Token.type, beginL, begin);
   List<Annotation> rTokens = UIMAUtil.getAnnotationsBySpan(pJCas, gov.va.chir.model.Token.type, end, endR);
   String lfs = getTextFrom( lTokens);
   String rfs = getTextFrom( rTokens);
   String focus = U.display(pAnnotation.getCoveredText());
   lfs = U.spacePadLeft(pCharWindow, lfs);
   rfs = U.spacePadRight(pCharWindow,rfs);
   focus = U.spacePadRight(30,focus) ;
   
   retValue = U.spacePadRight(4, pMsg + ":") + "|" + lfs + "|" + focus + "|" + rfs ;
   
   
   
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
        copperAnnotationType = UIMAUtil.getAnnotationType(pJCas, this.copperType);
        mudAnnotationType    = UIMAUtil.getAnnotationType(pJCas,    this.mudType);
      
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
    double recall    = this.total_TP / ( this.total_TP + this.total_FN + .000001) ;
    double precision = this.total_TP / ( this.total_TP + this.total_FP + .000001 ); 
    double accuracy  = (this.total_TP + this.total_TN)/ (this.total_TP + this.total_TN + this.total_FP + this.total_FN + .0000001); 
    double specificity = this.total_TN / (this.total_TN + this.total_FP + .000001 );
    double           f = 2 * ((precision*recall)/(precision+recall)); 
     
    if ( out == null ) return;
    out.println( "True  Positive (TP)                                  = " + this.total_TP);
    out.println( "True  Negative (TN)                                  = " + this.total_TN);
    out.println( "False Positive (FP)                                  = " + this.total_FP);
    out.println( "False Negative (FN)                                  = " + this.total_FN);
    out.println( "Recall                                  (tp/(tp+fn)) = "  + recall);
    out.println( "Sensitivity                             (tp/(tp+fn)) = "  + recall);
    out.println( "Precision                               (tp/(tp+fp)) = " + precision );
    out.println( "Positive Predictive value (PPV)         (tp/(tp+fp)) = " + precision );
    
    out.println( "Accuracy                      ((tp+tn)/(tp+tn+fp+fn) = " + accuracy);
    out.println( "Specificity                           (tn/(tn + fp)  = " + specificity);
    out.println(" F-Measure (2* (precision*recall)/(precision+recall)) = " + f);
    
    
    out.println("  +----------------+ ");
    out.println("   |Confusion Matrix|");
    out.println("  +-------+--------+");
    out.println("  |  TP   |  FN    |");
    out.println("  |       |        |");
    out.println("  | " + this.total_TP + "  | " + this.total_FN + "  ");
    out.println("  +-------+--------+");
    out.println("  |  FP   |  TN    |");
    out.println("  | " + this.total_FP + "  | " + this.total_TN + "  ");
    out.println("  +-------+--------+");
    
    
    out.println("\n\n\n\n"); 
    out.println(" ----------------------------------");
    out.println(" False Negatives                   ");
    out.println(" ----------------------------------");
    for ( String line : this.fnList)
      out.println( line);
    out.println(" ----------------------------------");
    
    
    // --------------------------------------
    // False positives
    // --------------------------------------
    
    
    
    out.close();
    
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
    
    super.destroy();
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
    
  
    String[] args = null;
     try {
       args                 = (String[]) aContext.getConfigParameterValue("args");  

       this.outputDir      = U.getOption(args, "--outputDir=", "./");
       this.performanceMeter = new PerformanceMeter( this.outputDir + "/logs/profile_" + this.getClass().getSimpleName() + ".log"  );
      
       goldType        = U.getOption(args,"--goldType=", "Symptom");
       copperType      = U.getOption(args,"--copperType=", "PotentialSymptom");
       mudType         = U.getOption(args,"--mudType=", "Symptom");
       summaryFileName = U.getOption(args,"--SummaryStatisticsFile=", summaryFileName);
    
    
      
     } catch (Exception e ) {
       
     }
     
    

  } // end Method initialize() -------
  
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  private Logger logger = null;
  private int total_TP = 0; 
  private int total_TN = 0;
  private int total_FP = 0;
  private int total_FN = 0;
  private String          goldType = null;
  private String        copperType = null;
  private String           mudType = null;
  private org.apache.uima.cas.Type   goldAnnotationType = null;
  private org.apache.uima.cas.Type   copperAnnotationType = null;
  private org.apache.uima.cas.Type   mudAnnotationType = null;
  private String summaryFileName = "./summaryStatistics.txt";
  private ArrayList<String> fnList = new ArrayList<String>();
  PerformanceMeter              performanceMeter = null;
  private String                       outputDir = null;
 
  
} // end Class MetaMapClient() ---------------