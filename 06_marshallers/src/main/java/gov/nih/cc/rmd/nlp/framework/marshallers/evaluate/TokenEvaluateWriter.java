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
package gov.nih.cc.rmd.nlp.framework.marshallers.evaluate;


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
import gov.va.chir.model.TruePositive;
import gov.va.chir.model.WordToken;
import gov.va.vinci.model.Copper;
import gov.va.vinci.model.Gold;




public class TokenEvaluateWriter extends JCasAnnotator_ImplBase implements Writer {
 
 
  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   * 
  **/
  // =================================================
  public TokenEvaluateWriter(String[] pArgs) throws ResourceInitializationException {
    
    initialize ( pArgs);
  }

  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   * 
  **/
  // =================================================
  public TokenEvaluateWriter( ) throws ResourceInitializationException {
    
   
  }


  

  /**
   * process
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    this.performanceMeter.startCounter();

    initializeTypes(pJCas);
  
    // ------------------------------------
    // Gather System A Labels
    // ------------------------------------
    String inputFileName = VUIMAUtil.getDocumentId(pJCas);

    List<Annotation> goldAnnotations = UIMAUtil.getAnnotations(pJCas, Gold.typeIndexID);
    List<Annotation> copperAnnotations = UIMAUtil.getAnnotations(pJCas, Copper.typeIndexID);
    List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, SectionZone.type);

    UIMAUtil.sortByOffset(goldAnnotations);
    UIMAUtil.sortByOffset(copperAnnotations);
    UIMAUtil.sortByOffset(sections);

    this.total_TN = 0;

    if (goldAnnotations != null) GLog.println(GLog.DEBUG_LEVEL, "The number of gold annotations = " + goldAnnotations.size());
    else {
      GLog.println(GLog.ERROR_LEVEL, "No gold annotations for file " + inputFileName);

    }

    if (copperAnnotations != null)
      GLog.println(GLog.DEBUG_LEVEL, "The number of copperAnnotations = " + copperAnnotations.size());

    // -----------------------------------
    // iterate thru gold
    // -----------------------------------
    if (goldAnnotations != null) { // some records won't have gold annotations in them
      for (Annotation trueGold : goldAnnotations) {

        List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas, WordToken.typeIndexID, trueGold.getBegin(),
            trueGold.getEnd());
        if (tokens != null && !tokens.isEmpty()) for (Annotation token : tokens)
          ((WordToken) token).setInGold(true);

      }
    }
    // -----------------------------------
    // iterate thru copper
    // -----------------------------------
    if (copperAnnotations != null) { // some records won't have copper annotations in them
      for (Annotation trueCopper : copperAnnotations) {

        List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas, WordToken.typeIndexID, trueCopper.getBegin(),
            trueCopper.getEnd());
        if (tokens != null && !tokens.isEmpty()) for (Annotation token : tokens)
          ((WordToken) token).setInCopper(true);

      }
    }
    
	   
    // ---------------------------------
    // Loop through the tokens to tally the tp,fp,fn,tn
    List<Annotation> tokens = UIMAUtil.getAnnotations(pJCas, WordToken.typeIndexID );

    if (tokens != null && !tokens.isEmpty()) 
      for (Annotation token : tokens) {

        if         (((WordToken) token).getInGold() && ((WordToken) token).getInCopper()) {
          this.total_TP++;
        } else if (!((WordToken) token).getInGold() && ((WordToken) token).getInCopper()) {
          this.total_FP++;
        } else if (((WordToken) token).getInGold() && !((WordToken) token).getInCopper()) {
          this.total_FN++;
        } else if (!((WordToken) token).getInGold() && !((WordToken) token).getInCopper()) {
          this.total_TN++;
        }

    } // end if there are tokens 

    
    // ---------------------------------
    // Loop through the tokens to create 'ps and concordance rows 
    tokens = UIMAUtil.getAnnotations(pJCas, WordToken.typeIndexID );
 
    List<Annotation> tps = createTPs( pJCas, tokens);
    List<Annotation> fps = createFPs( pJCas, tokens);
    List<Annotation> fns = createFNs( pJCas, tokens);
         
    destroyAux();
    
    this.performanceMeter.stopCounter();
    
   
      
} // end Method process() ----------------------------


    
  // =================================================
  /**
   * createTPs 
   * 
   * @param pJCas
   * @param tokens 
   * @return List<Annotation>
  */
  // =================================================
   private final List<Annotation> createTPs(JCas pJCas, List<Annotation> tokens) {
  
     List<Annotation> tps = new ArrayList<Annotation>();
     List<Annotation> aTP = new ArrayList<Annotation>();
     
     if (tokens != null && !tokens.isEmpty()) 
       for (Annotation token : tokens) {
     
         if (((WordToken) token).getInGold() && ((WordToken) token).getInCopper()) 
           aTP.add( token);
         else {
           if ( aTP != null && aTP.size() > 0 ) {
             Annotation aP = createAnnotation(pJCas, aTP.get(0).getBegin(), aTP.get(aTP.size() -1 ).getEnd(), "TP") ;
             tps.add (aP );
             String line = concordanceLine( pJCas, aP, windowSize, "TP" );
             this.tpList.add( line);
           }
           aTP = new ArrayList<Annotation>();
         }
      }
     
     if ( aTP != null && aTP.size() > 0 ) {
       Annotation aP = createAnnotation(pJCas, aTP.get(0).getBegin(), aTP.get(aTP.size() -1 ).getEnd(), "TP") ;
       tps.add (aP );
       String line = concordanceLine( pJCas, aP, windowSize, "TP" );
       this.tpList.add( line);
     }
      return tps;
   } // end Method createTPs() ---------------------


  // =================================================
  /**
   * createFPs 
   * 
   * @param pJCas
   * @param tokens 
   * @return List<Annotation>
  */
  // =================================================
   private final List<Annotation> createFPs(JCas pJCas,  List<Annotation> tokens) {
  
     List<Annotation> fps = new ArrayList<Annotation>();
     List<Annotation> aFP = new ArrayList<Annotation>();
     
     if (tokens != null && !tokens.isEmpty()) 
       for (Annotation token : tokens) {
     
         if (!((WordToken) token).getInGold() && ((WordToken) token).getInCopper()) 
           aFP.add( token);
         else {
           if ( aFP != null && aFP.size() > 0 ) {
             Annotation aP = createAnnotation(pJCas, aFP.get(0).getBegin(), aFP.get(aFP.size() -1 ).getEnd(), "FP");
             fps.add (aP );
             String line = concordanceLine( pJCas, aP, windowSize, "FP" );
             this.fpList.add( line);
           }
           aFP = new ArrayList<Annotation>();
         }
         
      }
     
     if ( aFP != null && aFP.size() > 0 ) {
       Annotation aP = createAnnotation(pJCas, aFP.get(0).getBegin(), aFP.get(aFP.size() -1 ).getEnd(), "FP");
       fps.add (aP );
       String line = concordanceLine( pJCas, aP, windowSize, "FP" );
      this.fpList.add( line);
     }
     return fps;
   } // end Method createTPs() ---------------------


   // =================================================
   /**
    * createFNs 
    * 
    * @param pJCas
    * @param tokens 
    * @return List<Annotation>
   */
   // =================================================
    private final List<Annotation> createFNs(JCas pJCas, List<Annotation> tokens) {
   
      List<Annotation> fns = new ArrayList<Annotation>();
      List<Annotation> aFN = new ArrayList<Annotation>();
      
      if (tokens != null && !tokens.isEmpty()) 
        for (Annotation token : tokens) {
      
          if (((WordToken) token).getInGold() && !((WordToken) token).getInCopper()) 
            aFN.add( token);
          else {
            if ( aFN != null && aFN.size() > 0 ) {
              Annotation aP = createAnnotation(pJCas, aFN.get(0).getBegin(), aFN.get(aFN.size() -1 ).getEnd(), "FN") ;
              fns.add ( aP );
              String line = concordanceLine( pJCas, aP, windowSize, "FN" );
              this.fnList.add( line);
            }
            aFN = new ArrayList<Annotation>();
          }
       }
      
      if ( aFN != null && aFN.size() > 0 ) {
        Annotation aP = createAnnotation(pJCas, aFN.get(0).getBegin(), aFN.get(aFN.size() -1 ).getEnd(), "FN") ;
        String line = concordanceLine( pJCas, aP, windowSize, "FN" );
        this.fnList.add( line);
        fns.add ( aP );
      }
     
      
      return fns;
    } // end Method createTPs() ---------------------



// -----------------------------------------
  /**
   * createAnnotation creates evaluation annotations
   * 
   * @param pJCas
   * @param pBegin
   * @param pEnd
   * @param pType
   * @return Annotation
   */
  // -----------------------------------------
  private Annotation createAnnotation(JCas pJCas, int pBegin, int pEnd , String pType) {
    
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
   
    statement.setBegin(        pBegin ) ;
    statement.setEnd(          pEnd );
    statement.addToIndexes();
    
    return statement;

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
  private String concordanceLine(JCas       pJCas, 
                                 Annotation pAnnotation, 
                                 int        pCharWindow, 
                                 String     pMsg  
                                ) {
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
    String lfs = getTextFrom( lTokens);
    String rfs = getTextFrom( rTokens);
    if ( lTokens == null && rTokens == null ) {
      lfs = U.display(docText.substring(beginL, begin));
      rfs = U.display(docText.substring(end, endR));
    } else {
       lfs = getTextFrom( lTokens);
       rfs = getTextFrom( rTokens);
   
    }
    String focus = "";
    try { focus = U.display(pAnnotation.getCoveredText().toLowerCase()); } catch (Exception e) {};
    lfs = U.spacePadLeft(pCharWindow, lfs);
    rfs = U.spacePadRight(pCharWindow,rfs);
    focus = U.spacePadRight(30,focus) ;
    String offsets = pAnnotation.getBegin()  + "|" + pAnnotation.getEnd() ;
     
    
    retValue = U.spacePadRight(4, pMsg + ":|") + U.spacePadRight(25,fileName) + "|" + lfs + "|" + focus + "|" + rfs + "|"  + offsets ;
    
    
    
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
   buff.append("         Token Level Evaluation        \n");
   buff.append( "-- " + this.reportTitle +          "--\n");
   buff.append(" --------------------------------------\n\n");
  
   
   double recall    = this.total_TP / (( this.total_TP + this.total_FN) + .00000001) ;
   double precision = this.total_TP / (( this.total_TP + this.total_FP)  + .00000001); 
   double accuracy  = (this.total_TP + this.total_TN)/ ((this.total_TP + this.total_TN + this.total_FP + this.total_FN) + .0000001); 
   double specificity = this.total_TN / ((this.total_TN + this.total_FP) + .0000001);
   double           f = 2 * ((precision*recall)/(precision+recall)); 
    
   buff.append( "True  Positive (TP)                                  = " + this.total_TP + "\n");
   buff.append( "True  Negative (TN)                                  = " + this.total_TN + "\n" );
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
      this.reportTitle = U.getOption( pArgs,  "--reportTitle=", "") ;
      this.reportTitle = this.reportTitle + "Report run on " + dateStamp;
                                             
      this.outputDir         = outputDir_ + "/eval";
      U.mkDir(this.outputDir );
      U.mkDir(outputDir_ + "/logs");
    
       this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
     this.summaryFileName = this.outputDir + "/token_eval_" + threadId + ".txt"; 

    
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
  public static final String annotatorName = TokenEvaluateWriter.class.getSimpleName();
  //private HashMap<String, int[]> goldStatus = null;
  private int total_TP = 0; 
  private int total_TN = 0;
  private int total_FP = 0;
  private int total_FN = 0;
  private String          goldType   = "Gold";
  private org.apache.uima.cas.Type   goldAnnotationType = null;
  private ArrayList<String> fnList = new ArrayList<String>();
  private ArrayList<String> fpList = new ArrayList<String>();
  private ArrayList<String> tpList = new ArrayList<String>();
 private String outputDir = null;
  private String summaryFileName = "./summaryStatistics.txt";
  protected ProfilePerformanceMeter       performanceMeter = null;
  private  String reportTitle = "";
   private static int windowSize = 55;

 
  
} // end Class MetaMapClient() ---------------
