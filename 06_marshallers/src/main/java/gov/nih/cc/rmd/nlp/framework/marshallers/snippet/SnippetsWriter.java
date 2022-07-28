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
 * SnippetsWriter creates snippets around mentions.
 *
 *
 * @author  Guy Divita 
 * @created Sept 25, 2015
 *
 **   *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.snippet;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.Snippet;
import gov.nih.cc.rmd.nlp.framework.marshallers.knowtator.ToKnowtator;
import gov.nih.cc.rmd.nlp.framework.marshallers.vtt.ToVTT;
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;
import gov.nih.cc.rmd.nlp.framework.marshallers.xmi.ToXMI;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
// import gov.nih.cc.rmd.nlp.framework.marshallers.xmi.ToXMI;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class SnippetsWriter extends JCasAnnotator_ImplBase implements gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer  {
 

  // ==========================================
  /**
   * SnippetsWriter [Summary]
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   */
  // ==========================================
  public SnippetsWriter(String[] pArgs) throws ResourceInitializationException {
  
     initialize( pArgs );

  } // end Constructor ==========================================
  



  // -----------------------------------------
  /** 
   * process iterates through all annotations, filters out
   * those that should be filtered out, then pushes them
   * into a database store.
   * 
   * Each document should include a DocumentAnnotation annotation and a documentHeader annotation
   * for re-animation purposes.
   *
   * @param pAJCas
   * @throws AnalysisEngineProcessException
   */
  // -----------------------------------------
  @Override
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
    
     
    
      
   try {
     
    this.performanceMeter.startCounter();;
    if ( this.spareCasToGetTypeSystem == null ) //  Needed for the destroy to create a new cas
      this.spareCasToGetTypeSystem = pJCas;     //  from an existing cas - the existing cas has
                                                //  the type system in it.
  
    String[] lineAndOffsets = UIMAUtil.getLinesFromDocument( pJCas);
    ArrayList<Context> positive_assertedProseContexts = new ArrayList<Context>();
    ArrayList<Context>  positive_negatedProseContexts = new ArrayList<Context>();
    ArrayList<Context> positive_assertedNotProseContexts = new ArrayList<Context>();
    ArrayList<Context>  positive_negatedNotProseContexts = new ArrayList<Context>();
    ArrayList<Context>  positive_trueNegativeContexts = new ArrayList<Context>();
    
    ArrayList<Context> control_assertedProseContexts = new ArrayList<Context>();
    ArrayList<Context>  control_negatedProseContexts = new ArrayList<Context>();
    ArrayList<Context> control_assertedNotProseContexts = new ArrayList<Context>();
    ArrayList<Context>  control_negatedNotProseContexts = new ArrayList<Context>();
    ArrayList<Context>  control_trueNegativeContexts = new ArrayList<Context>();
    
    
    HashMap<String,Integer> contextHash = null;
    
    DocumentHeader documentHeader = VUIMAUtil.getDocumentHeader(pJCas);
    
    if ( documentHeader == null )
      return;
    
    
    String documentId = documentHeader.getDocumentId();
    String  patientId = documentHeader.getPatientID();
    
    // ------------------------
    // for the type in question
    // ------------------------
    String fileName = documentId;
    if ( fileName.indexOf("/") > -1 )
      fileName = fileName.substring(fileName.lastIndexOf('/') + 1 );
    List<Annotation> allAnnotations = null;
    try {
      
      allAnnotations = UIMAUtil.getAnnotations(pJCas);
    } catch (Exception e) {
      String msg = "Issue with getting the annotations for the focus \n" + e.getMessage() + "\n" + U.getStackTrace(e);
      System.err.println(msg);
      return;
    }
    
    if ( allAnnotations != null && allAnnotations.size() > 0 ) {
    

      contextHash = new HashMap<String,Integer>(allAnnotations.size());
      
      for ( Annotation focus: allAnnotations ) {
        String shortName = getShortName( focus.getType().getName());
        
     
        if ( this.focusLabel.contains(shortName) ) {
          if ( isNew( contextHash, focus)) {
            // -----------------------------------------
            // Sample and print out only those that are at the 
            // prescribed sample rate
            if (  ++this.sampleCounter % this.sampleRate == 0  ) {
            
              Context context = createContext(pJCas, documentId, patientId, lineAndOffsets, focus, this.lineWindowSize );
              if ( context != null ) {
                String assertionStatus = context.getAssertionStatus();
                boolean  inProse = VUIMAUtil.isInProseLazy(pJCas, focus);
                boolean      positive_or_control = VUIMAUtil.isPositiveOrControl(pJCas);
                
                if ( positive_or_control ) {
                
                  if      ( assertionStatus.equals("Asserted")   &&  inProse )         positive_assertedProseContexts.add( context);
                  else if ( assertionStatus.equals("Asserted")   && !inProse )         positive_assertedNotProseContexts.add( context);
                  else if ( assertionStatus.equals("TrueNegative") )                   positive_trueNegativeContexts.add( context);
                  else if ( !assertionStatus.equals("Asserted ") &&  inProse )         positive_negatedProseContexts.add( context);
                  else if ( !assertionStatus.equals("Asserted ") && !inProse )         positive_negatedNotProseContexts.add( context);
                
                } else {
                  if      ( assertionStatus.equals("Asserted")   &&  inProse )         control_assertedProseContexts.add( context);
                  else if ( assertionStatus.equals("Asserted")   && !inProse )         control_assertedNotProseContexts.add( context);
                  else if ( assertionStatus.equals("TrueNegative") )                   control_trueNegativeContexts.add( context);
                  else if ( !assertionStatus.equals("Asserted ") &&  inProse )         control_negatedProseContexts.add( context);
                  else if ( !assertionStatus.equals("Asserted ") && !inProse )         control_negatedNotProseContexts.add( context);
                
                }
              }
               
            }
          }
                      
        } // end if this is a focus annotation
      } // end loop through the focus annotations
      
    } // end if any annotations exist 
    
    
    // --------------------------------------
    // Render these contexts into something
    
   try {
   
   
    display( pJCas, positive_assertedProseContexts,    this.positive_assertedProseContextCache, this.assertedProseSnippetDocPositive, "positive", "asserted", true, StaticPositiveAssertedProseSnippetFileCounter);
    display( pJCas, positive_negatedProseContexts,     this.positive_negatedProseContextCache,  this.negatedProseSnippetDocPositive, "positive", "negated",  true, StaticPositiveNegatedProseSnippetFileCounter);
    
    display( pJCas, positive_assertedNotProseContexts, this.positive_assertedNotProseContextCache, this.assertedNotProseSnippetDocPositive, "positive", "asserted", false, StaticPositiveAssertedNotProseSnippetFileCounter);
    display( pJCas, positive_negatedNotProseContexts,  this.positive_negatedNotProseContextCache,  this.negatedNotProseSnippetDocPositive, "positive", "negated",  false, StaticPositiveNegatedNotProseSnippetFileCounter);
   
    display( pJCas, control_assertedProseContexts,    this.control_assertedProseContextCache,     this.assertedProseSnippetDocControl, "control", "asserted", true, StaticControlAssertedProseSnippetFileCounter);
    display( pJCas, control_negatedProseContexts,     this.control_negatedProseContextCache,      this.negatedProseSnippetDocControl,  "control", "negated",  true, StaticControlNegatedProseSnippetFileCounter);
    
    display( pJCas, control_assertedNotProseContexts, this.control_assertedNotProseContextCache,  this.assertedNotProseSnippetDocControl, "control", "asserted", false, StaticControlAssertedNotProseSnippetFileCounter);
    display( pJCas, control_negatedNotProseContexts,  this.control_negatedNotProseContextCache,   this.negatedNotProseSnippetDocControl, "control", "negated",  false, StaticControlNegatedNotProseSnippetFileCounter);
   
    display( pJCas, positive_trueNegativeContexts, this.positive_trueNegativeContextCache,  this.trueNegativeSnippetDocPositive, "positive", "trueNegative", false, StaticPositiveTrueNegativeSnippetFileCounter);
    display( pJCas, control_trueNegativeContexts,  this.control_trueNegativeContextCache,   this.trueNegativeSnippetDocControl, "control", "trueNegative",  false, StaticControlTrueNegativeSnippetFileCounter);
   
    
  } catch (Exception e) {
    e.printStackTrace();
    String msg = "Issue with snippetWriter " + e.getMessage();
    System.err.println( msg );
   
  }
   } catch (Exception e ){
     e.printStackTrace();
     String msg = "Issue with snippetWriter " + e.getMessage();
     System.err.println( msg);
    
   }
   this.performanceMeter.stopCounter();  
   
  } // end Method process() =================

  

  //-----------------------------------------
  /**
   * createContext creates a context instance for this annotation. 
   * 
   * @param pJCas
   * @param pDocumentId
   * @param pLines
   * @param pAnnotation
   * @param pLineWindow 
   * @return
   */
  // -----------------------------------------
  private Context createContext(JCas pJCas, String pDocumentId, String pPatientId, String[] pLines, Annotation pAnnotation, int pLineWindow  ) {
    
    
    String focus = "";
    try {
      focus = pAnnotation.getCoveredText();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with getting the covered text " + e.getMessage());
      System.err.println( pAnnotation.getBegin() + "|" + pAnnotation.getEnd());
    }
      focus = toASCII8( focus);
    
   

    String assertionStatus = null;
    boolean    conditional = false;
    String         subject = null;
    boolean     historical = false;
    String        category = pAnnotation.getClass().getSimpleName();
           assertionStatus =  VUIMAUtil.getAssertionStatus(pJCas, pAnnotation);
               conditional =  VUIMAUtil.getConditional_Status( pAnnotation);
                   subject =  VUIMAUtil.getSubject( pAnnotation);
  //  try {      historical = ((Concept)pAnnotation).getHistorical();      } catch (Exception e) {      historical = ((VAnnotation)pAnnotation).getHistorical();};
    String    otherFeatures =  VUIMAUtil.getMetaData(pJCas);
    
  
    String focusLines[] = getFocusLines( pJCas, pAnnotation); 
    boolean hasNewlines = false;
    for (int i = 0; i < focusLines.length; i++ )
      if ( focusLines[i].contains("\n")) {
        hasNewlines = true;
        break;
      }
    String snippetLines = null;
    if ( hasNewlines )
       snippetLines = focusLines[0]  +  focusLines[1] +    focusLines[2];  // the newline is not needed
    else 
     snippetLines = focusLines[0] + '\n' +  focusLines[1] + '\n' +   focusLines[2];  // the newline is needed
   
    snippetLines = toASCII8( snippetLines);
 
    int tt = focusLines[1].indexOf(focus);   // <--- flawed.  If the focus spans 2 lines won't catch
    if ( tt == -1) {
      try {
        String focus2 = focus.substring(0, focus.indexOf('\n'));
        tt = focusLines[1].indexOf(focus2);
      } catch (Exception e) {};
    }
    int fix = 0;
    if ( !hasNewlines ) fix = 1;
    int focB = focusLines[0].length();
    int focusOffsetBegin = focusLines[0].length() + tt + fix;  // <--- flawed - if two or more patterns in the same line will fail
    int focusOffsetEnd =   focusOffsetBegin + (pAnnotation.getEnd() - pAnnotation.getBegin()); // snippetLines.length();
   
  
    String relevance = "not set";
    String id = "";
    try {
     id = ((VAnnotation) pAnnotation).getId();
    } catch (Exception e) {
      id = pDocumentId + ":" + U.pad(pAnnotation.getBegin()) + ":" + U.pad( pAnnotation.getEnd());
    }
    
   
    if ( conditional ) assertionStatus = "Conditional";
    if ( subject != null && subject.contains("SubjectIsOtherEvidence")) assertionStatus = subject;
    
    if ( assertionStatus == null || assertionStatus.equals("Asserted") )  {  assertionStatus = "Asserted"; }
    Context aContext = null;
            aContext = new Context(pDocumentId, 
                                   id, 
                                   pAnnotation.getBegin(), 
                                   pAnnotation.getEnd(), 
                                   focus, 
                                   focusOffsetBegin, // <----------------| these are relative to the vtt file, not completely known here 
                                   focusOffsetEnd,   // <----------------| 
                                   snippetLines,
                                   relevance,
                                   pPatientId,
                                   assertionStatus,
                                   conditional,
                                   historical,
                                   subject,
                                   category,
                                   otherFeatures,
                                   GlobalCounter);

        synchronized(this){ GlobalCounter++ ; };
            
        
    return ( aContext);
  } // end Method createContext() ==========================
   

   
  // =======================================================
  /**
   * getFocusLines retrieves the 
   *  line before
   *  the line
   *  the line after
   * 
   * @param pJCas
   * @param pAnnotation
   * @return String[3]   
   */
  // =======================================================
  private String[] getFocusLines(JCas pJCas, Annotation pAnnotation) {
  
    String returnVal[] = new String[3];
    String doc = pJCas.getDocumentText();
    char[] docText = doc.toCharArray();

    returnVal[0] = getLinesBeforeFocusLine(pJCas, pAnnotation, docText, doc, 3);
    returnVal[1] = getFocusLine(pJCas, pAnnotation, docText, doc);
    returnVal[2] = getLinesAfterFocusLine(pJCas, pAnnotation,docText, doc, 3);
    
   
    
   
 
    
    return returnVal;
  } // End Method getFocusLines() ======================
  
  // =======================================================
  /**
   * getLinesBeforeFocusLine retrieves the 
   *  lines before focus line
   *  
   * 
   * @param pJCas
   * @param pAnnotation
   * @param pNoOfLines
   * @return String   
   */
  // =======================================================
  private String getLinesBeforeFocusLine(JCas pJCas, Annotation pAnnotation, char[] docText, String pDoc, int pNoOfLines) {
    String returnVal = null;
    
    int beginLine = pAnnotation.getBegin();
    int ctr = 0;
    
    
    
    
    String t = pDoc;
    // read from the begin line back to the next newline
    int lineBeforeBeginOffset = 0;
    int xx = 0;
    for (int i = beginLine -1; i > 0 ; i--) {
      if ( docText[i] == '\n' ) {
        if ( ctr == 0 ) 
          xx = i;
        else if ( ctr == pNoOfLines) {
          lineBeforeBeginOffset = i;
          break;
        }
        ctr++;
      }
    }
    StringBuffer lineBefore = new StringBuffer();
    for (int i = lineBeforeBeginOffset; i < xx ; i++) lineBefore.append(docText[i]);
    lineBefore.append('\n');
    returnVal = lineBefore.toString();
    
    return returnVal;
  } // End Method getLinesBeforeFocusLine() ================
  
//=======================================================
 /**
  * getFocusLine retrieves the 
  *   focus line
  *  
  * 
  * @param pJCas
  * @param pAnnotation
 * @param doc 
  *
  * @return String   
  */
 // =======================================================
 private String getFocusLine(JCas pJCas, Annotation pAnnotation, char[] docText, String pDoc) {
   String returnVal = null;

   String c = pDoc;
   int ptr = pAnnotation.getBegin() -1;
   int endOfAnnotation = pAnnotation.getEnd();
   if ( ptr < 0) ptr = 0;
   int beginLine = -1;
   for ( ; ptr > 0; ptr-- ) {
     if ( docText[ptr] == '\n' ) {  
        beginLine = ptr;
         break;
     }
   }
  
   // read from the begin line to the end of line <--- gets the focus line
   StringBuffer focusLine = new StringBuffer();
   int focusLinePtr = 0;
   
   for (  focusLinePtr = beginLine + 1; focusLinePtr < docText.length; focusLinePtr++ ) {
     focusLine.append(docText[focusLinePtr]);
     if ( focusLinePtr > endOfAnnotation && docText[focusLinePtr] == '\n'  )
       break;
   }
   
   // ----------------------------
   // check to see if the focus spans across lines i.e., if the focusLinePtr is < the annotation 
   // end.  If so
   
   returnVal = focusLine.toString();
   return returnVal;
   
 } // end Method getFocusLine() ==========================
 
//=======================================================
/**
 * getLinesAfterFocusLine retrieves the 
 *   lines after the focus line
 *  
 * 
 * @param pJCas
 * @param pAnnotation
 * @param pNumberOfLines
 *
 * @return String   
 */
// =======================================================
private String getLinesAfterFocusLine(JCas pJCas, Annotation pAnnotation, char[] docText, String pDoc, int pNumberOfLines) {
  
  String returnVal = null;
  String dx = pDoc;
  int beginLine = pAnnotation.getEnd();
  
  for ( int i = beginLine; i< docText.length; i++) {
    if ( docText[i] == '\n' ) {
      beginLine = i + 1;
      break;
    }
  }
  
  // read from the end of the focus line to the next newline
  StringBuffer linesAfter = new StringBuffer();
  int ctr = 0;
  for (int i = beginLine; i < docText.length; i++ ) {
    linesAfter.append(docText[i]);
    if ( docText[i] == '\n' ) {
      if  (ctr == pNumberOfLines ) 
        break;
      ctr++;
    }
  }
  returnVal = linesAfter.toString();
  
  return returnVal;
  
} // end Method getLinesAfterFocusLine() ================
  // =======================================================
  /**
   * display writes out a file of these contexts 1000 at a time
   * more or less.   Will keep all annotations for a given record
   * in the same file.
   * 
   * @param pJCas
   * @param  pContexts
   * @param pCountextCounter
   * @param pNewJCas
   * @param pCasTextBuff
   * @param pSnippetFileCounter
   * @throws FileNotFoundException 
   *
   */
  // =======================================================
  void display( JCas pJCas, 
                        List<Context> pContexts, 
                        List<Context>pContextsCache, 
                    //   int[] pContextCounter, 
                        StringBuffer pCasTextBuff, 
                        String cohort,
                        String assertionStatus, 
                        boolean pInProse, 
                        int[] pSnippetFileCounter) throws Exception {
    
    
    for ( int i = 0; i < pContexts.size(); i++) {
             
      
      // ----------------------------------------------------------
      // If the counter is over the limit, create a new file
      if ( pContextsCache!= null &&
            pContextsCache.size() > 0 &&
            pContextsCache.size() >= this.contextsPerFile ) {
        
        display(pJCas, pContextsCache, pCasTextBuff.toString(), this.outputDir, cohort, assertionStatus, pInProse, pSnippetFileCounter );
        pContextsCache.clear();
        pCasTextBuff.setLength(0);
        String msg = "Writing out snippet file with " + pContextsCache.size() + " becasue the number of snippets in this file is >= " + this.contextsPerFile ;
        System.err.print(msg);
        
        
      } // end if you need to go to a new file
     
      if ( pContextsCache != null )
        pContextsCache.add( pContexts.get(i));
      addContextToCAS(  pContexts.get(i), pCasTextBuff );  // <----- builds up the docText, sets the offsets of the context
     // pContextCounter[0]++;
    
        
    } // end loop through rows
   
    pContexts.clear();
         
    
    
  }  // End Method display() ======================
  


  // =======================================================
  /**
   * display 
   * 
   * @param pJCas
   * @param pContextsCache
   * @param pCasTextBuff
   * @param outputDir2
   * @param assertionStatus
   * @param inProse
   * @param pSnippetFileCounter
   * @throws Exception 
   */
  // =======================================================
   void display(JCas pJCas, List<Context> pContextsCache, String pCasText, String outputDir2, String cohort, String assertionStatus, boolean inProse, int[] pSnippetFileCounter ) throws Exception {
   
    
    JCas jcas = null;
    
    if (pCasText == null || pContextsCache == null  ) return;
    
    try {
      jcas = UIMAUtil.createNewJCAS(pJCas); 
    } catch (Exception e) {
      e.printStackTrace(); 
      System.err.println("Issue creating a new cas " + e.toString() + " continuing ....");
      return;
    }
       
    if ( pCasText == null || pCasText.trim().length() == 0 )  { 
      System.err.println("Empty text! why? " + pContextsCache.size() + " continuing ...."); 
      // throw new RuntimeException();
      return;
    }
    jcas.setDocumentText(pCasText);
    createDocumentHeader( jcas, this.outputDir, cohort, assertionStatus,  inProse, pSnippetFileCounter[0]++);
  
    // -----------------------------------------------
    // Create the annotations for each of the contexts
    for ( int i = 0; i < pContextsCache.size(); i++) {
      pContextsCache.get(i).setFileSnippetId(i);
      SnippetContainer sc = new SnippetContainer( pContextsCache.get(i));
      createSnippetAnnotation(jcas, (SnippetContainer) sc );
    }

    try { 
      System.err.println("Writing out a snippet file " + (pSnippetFileCounter[0]-1));
      this.outputWriter.process( jcas);
    } catch (Exception e) {
      e.printStackTrace(); 
      System.err.println("Issue writing " + e.toString() + " continuing ..."); 
    }
   
   
  } // End Method display() ======================
  



  // =======================================================
  /**
   * createDocumentHeader 
   * 
   * @param newJCas
   * @param pInProse
   * @param snippetFileCounter
   */
  // =======================================================
  protected void createDocumentHeader(JCas pJCas, String pOutputDir, String cohort, String assertionStatus, boolean pInProse,  int snippetFileCounter) {
   
    DocumentHeader documentHeader = new DocumentHeader(pJCas);
    
    String inProse = "Prose";
    if ( !pInProse ) inProse = "NotProse";
    
    documentHeader.setBegin(0);
    documentHeader.setEnd(1);
    
    
    String snippetFileName = "Snippets_" + cohort + "_" + assertionStatus + inProse +  "_" +  U.zeroPad(snippetFileCounter, 5);
    documentHeader.setDocumentId(snippetFileName);
    documentHeader.setDocumentPath(pOutputDir);
    documentHeader.addToIndexes();
    
  } // End Method createDocumentHeader() ======================
  



  // =======================================================
  /**
   * addContextToCAS adds the context to the jcas
   * 
   * @param pJCas
   * @param context
   * @param pDocText
 * @return 
   * 
   */
  // =======================================================
  protected void addContextToCAS( Context pContext, StringBuffer pDocText) {
     
    
    String contextBlock = pContext.toContextFormatString1();
    
    int blockBeginOffset = pDocText.length();
    
    pDocText.append( "\n" + contextBlock);
    
    String docText = pDocText.toString();
    int snippetBlockTextBegin = docText.indexOf(Context.CONTEXT_SNIPPET_SLOT, blockBeginOffset) + Context.CONTEXT_SNIPPET_SLOT.length() + 1;
   
 
    int focusMentionBegin = snippetBlockTextBegin + pContext.getFocusBeginOffset() ;
    int focusMentionEnd   = focusMentionBegin + pContext.getFocus().length();
  
    pContext.setfocusBeginOffset(focusMentionBegin);
    pContext.setfocusEndOffset(focusMentionEnd);
    

  }  // End Method addContextToCAS() ======================
  


  // =======================================================
  /**
   * addContextToCAS adds the context to the jcas
   * 
   * @param pJCas
   * @param context
   * 
   */
  // =======================================================
  protected void createSnippetAnnotation(JCas pJCas, SnippetContainer pContext  ) {
     
    
    // -----------------------------
    // Create a Snippet annotation
    Snippet focusAnnotation = new Snippet(pJCas);
  
    focusAnnotation.setBegin(pContext.getFocusBeginOffset()  );
    focusAnnotation.setEnd(  pContext.getFocusEndOffset()  );
    focusAnnotation.setAssertionStatus(pContext.getAssertionStatus());
    focusAnnotation.setConditional( pContext.getConditional());
    focusAnnotation.setHistorical(  pContext.getHistorical());
    focusAnnotation.setOtherFeatures( pContext.getOtherFeatures());
    focusAnnotation.setCategory( pContext.getCategory());
    focusAnnotation.setDocumentId(pContext.getDocumentId());
    focusAnnotation.setPatientId(pContext.getPatientId());
    focusAnnotation.setSnippetId( pContext.getSnippetId());
    focusAnnotation.setAnnotationId( pContext.getAnnotationId());
    focusAnnotation.setFileSnippetId( pContext.getFileSnippetId());
    focusAnnotation.setFocusBeginOffset( pContext.getFocusBeginOffset());
    focusAnnotation.setFocusEndOffset(pContext.getFocusEndOffset());
    
    focusAnnotation.addToIndexes();

 
    
  }  // End Method addContextToCAS() ======================
  


  
  // =======================================================
  /**
   * getShortName returns the name of the class devoid of its namespace
   * 
   * @param name
   * @return
   */
  // =======================================================
  private String getShortName(String pName) {
  
    String shortName = pName;
     int period = pName.lastIndexOf(".");  
     if ( period > 0 && period < pName.length() ) {
       shortName = pName.substring( period + 1);
     }
     return shortName;
  }  // End Method getShortName() ======================


  // =======================================================
    /**
     * isNew returns true if the span for this size is a new span
     * 
     * @param pContextHash
     * @param pFocus
     * @return boolean
     */
    // =======================================================
    private boolean isNew(HashMap<String,Integer> pContextHash, Annotation pFocus) {
      boolean returnVal = false;
      
      Integer dummy = new Integer(0);
      String key = pFocus.getBegin() + "|" ; //  + pFocus.getEnd();
     
      if ( pContextHash.get(key)== null ) {
        pContextHash.put(key, dummy);
        returnVal = true;
      }
      
      return returnVal;
    }  // End Method isNew() ======================
    

    // =======================================================
    /**
     * toASCII8 converts any characters outside the ascii 8
     * range to ascii 8 characters.  If a translation from
     * a strange character is not possible, the character
     * will be replaced with a space to keep the offsets
     * the same.
     * 
     * @param pJCas
     * @param markups
     */
    // =======================================================
    private String toASCII8(String docText) {
    String buff = null;
      
    char    chars[]  = docText.toCharArray();
      char newChars[] = new char[chars.length]; 
    
      for (int i = 0 ; i < chars.length; i++ ) {
        
        
        if ( chars[i] >= SPACE && chars[i] <= TILDA )
          newChars[i] = chars[i];
        else if ( chars[i] == NEWLINE )
          newChars[i] = NEWLINE;
        else if ( chars[i] == CR )
          newChars[i] = SPACE;
        else
          newChars[i] = SPACE;
      }
      buff = new String( newChars);
      
    
    return buff;
  } // end Method toASCII8 -------------------------------

    
    // =======================================================
    /**
     * destroy calls the writer one last time
     * 
    */
    // =======================================================
    @Override
    public void destroy() {
      
      try {
      
      if ( this.positive_assertedProseContextCache != null  && !this.positive_assertedProseContextCache.isEmpty() )
        display(this.spareCasToGetTypeSystem, this.positive_assertedProseContextCache, this.assertedProseSnippetDocPositive.toString(), this.outputDir,  "positive", "asserted", true, StaticPositiveAssertedProseSnippetFileCounter );
      
      
      if ( this.positive_assertedNotProseContextCache != null && !this.positive_assertedNotProseContextCache.isEmpty())
        display(this.spareCasToGetTypeSystem, this.positive_assertedNotProseContextCache, this.assertedNotProseSnippetDocPositive.toString(), this.outputDir, "positive", "asserted", false, StaticPositiveAssertedNotProseSnippetFileCounter );
      
      
      if (this.positive_negatedProseContextCache != null  && !this.positive_negatedProseContextCache.isEmpty())
        display(this.spareCasToGetTypeSystem, this.positive_negatedProseContextCache, this.negatedProseSnippetDocPositive.toString(), this.outputDir, "positive", "negated", true, StaticPositiveNegatedProseSnippetFileCounter );
      
      if (this.positive_negatedNotProseContextCache != null && !this.positive_negatedNotProseContextCache.isEmpty())
        display(this.spareCasToGetTypeSystem, this.positive_negatedNotProseContextCache, this.negatedNotProseSnippetDocPositive.toString(), this.outputDir, "positive", "negated", false, StaticPositiveNegatedNotProseSnippetFileCounter );
      
      if (this.positive_trueNegativeContextCache != null && !this.positive_trueNegativeContextCache.isEmpty())
        display(this.spareCasToGetTypeSystem, this.positive_trueNegativeContextCache, this.trueNegativeSnippetDocPositive.toString(), this.outputDir, "positive", "trueNegative", false, StaticPositiveTrueNegativeSnippetFileCounter );
     
      
      // -------------- 
      if ( this.control_assertedProseContextCache != null  && !this.control_assertedProseContextCache.isEmpty() )
        display(this.spareCasToGetTypeSystem, this.control_assertedProseContextCache, this.assertedProseSnippetDocControl.toString(), this.outputDir, "control",  "asserted", true, StaticControlAssertedProseSnippetFileCounter );
      
      
      if ( this.control_assertedNotProseContextCache != null && !this.control_assertedNotProseContextCache.isEmpty())
        display(this.spareCasToGetTypeSystem, this.control_assertedNotProseContextCache, this.assertedNotProseSnippetDocControl.toString(), this.outputDir, "control", "asserted", false, StaticControlAssertedNotProseSnippetFileCounter );
      
      
      if (this.control_negatedProseContextCache != null  && !this.control_negatedProseContextCache.isEmpty())
        display(this.spareCasToGetTypeSystem, this.control_negatedProseContextCache, this.negatedProseSnippetDocControl.toString(), this.outputDir, "control", "negated", true, StaticControlNegatedProseSnippetFileCounter );
      
      if (this.control_negatedNotProseContextCache != null && !this.control_negatedNotProseContextCache.isEmpty())
        display(this.spareCasToGetTypeSystem, this.control_negatedNotProseContextCache, this.negatedNotProseSnippetDocControl.toString(), this.outputDir, "control", "negated", false, StaticControlNegatedNotProseSnippetFileCounter );
      
      if (this.control_trueNegativeContextCache != null && !this.control_trueNegativeContextCache.isEmpty())
        display(this.spareCasToGetTypeSystem, this.control_trueNegativeContextCache, this.trueNegativeSnippetDocControl.toString(), this.outputDir, "control", "trueNegative", false, StaticControlTrueNegativeSnippetFileCounter );
      
      
      
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue in destroy  " + e.toString());
        
      }
     
      
      this.outputWriter.destroy();
      this.performanceMeter.writeProfile( this.getClass().getSimpleName());
      
    }

   //-----------------------------------------
   /**
    * initialize loads in the resources.
    *   Put pipeline parameters in the args parameter
    *   to be retrieved here.
    * 
    *      Pass in on your args, an arg with the value: "--outputDir=/some/path/to/put/outputFiles"
    *
    *   This class is tooled up to have performance statisitcs get put into $outputDir/logs/Class.log  
    *
    * @param aContext
    *
    */
   // -----------------------------------------
    public void initialize(UimaContext aContext)  throws ResourceInitializationException {
     
      String args[] = null;
      args                  = (String[]) aContext.getConfigParameterValue("args");
      initialize ( args);
      
     
    } // End Method initialize() ================================

  //-----------------------------------------
   /**
    * initialize loads in the resources.
    *   Put pipeline parameters in the args parameter
    *   to be retrieved here.
    * 
    *      Pass in on your args, an arg with the value: "--outputDir=/some/path/to/put/outputFiles"
    *
    *   This class is tooled up to have performance statisitcs get put into $outputDir/logs/Class.log  
    *
    * @param pArgs
    *
    */
   // -----------------------------------------
    public void initialize(String pArgs[])  throws ResourceInitializationException {

      String  outputDir_ = U.getOption(pArgs,  "--outputDir=", "./") + "/snippets";
      String   focusLabel = U.getOption(pArgs, "--focusLabel=", "Concept");
      String  sampleRate = U.getOption(pArgs, "--sampleRate=",  "1");
      String  snippetsPerFile = U.getOption(pArgs, "--snippetsPerFile=", "200");
      String snippetWriterType = U.getOption(pArgs, "--snippetWriterType=", "VTT").toLowerCase();
      String  outputLabels = U.getOption(pArgs,  "--evaluationLabels=", "Snippet:A:B");
      
      
      this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
       
      try { U.mkDir( outputDir_ ); } catch ( Exception e) { e.printStackTrace(); System.err.println("Issue creating the snippets dir " + e.toString()); };
     
      initialize( outputDir_, focusLabel, sampleRate, snippetsPerFile, snippetWriterType, outputLabels );
      
    } // End Method initialize() ------------
    
    
  //-----------------------------------------
    /**
     * initialize loads in the resources.
     *   Put pipeline parameters in the args parameter
     *   to be retrieved here.
     *
     * @param pOutputDir  (assumes this to be ./outputDir/snippets - and already created)
     * @param pFocusType  (what annotation will be the focus annotation)
     * @param pSampleRate 
     *
     */
    // -----------------------------------------
     public void initialize( String pOutputDir, String pFocusLabel, String pSampleRate, String pSnippetsPerFile, String pSnippetWriterType , String evaluationLabels )  throws ResourceInitializationException {
      
       
       System.err.println("SNIPPET Writer invoked.  Output going to directory " + pOutputDir );
       System.err.println("Using Focus label " + pFocusLabel );
       System.err.println("SampleRate = " + pSampleRate);
       System.err.println("Snippets per file = " + pSnippetsPerFile);
       
       this.contextsPerFile = Integer.parseInt(pSnippetsPerFile);
    
    try {
  
      this.outputDir = pOutputDir;
      this.focusLabel = pFocusLabel;
      this.sampleRate = Integer.parseInt(pSampleRate);
      
      String outputTypes[] =  U.split(evaluationLabels, ":" );
    
    
 
    
     if ( pSnippetWriterType.contains("vtt"))            this.outputWriter = new ToVTT(this.outputDir, outputTypes);
     else if ( pSnippetWriterType.contains("xmi"))       this.outputWriter = new ToXMI(this.outputDir);
     else if ( pSnippetWriterType.contains("knowtator")) this.outputWriter = new ToKnowtator(this.outputDir + "/..", this.outputDir, outputTypes);
    
      synchronized(this) {
        if ( StaticCountersFirstTime ) {
          StaticPositiveAssertedProseSnippetFileCounter[0] = 0;
          StaticPositiveAssertedNotProseSnippetFileCounter[0] = 0;
       
          StaticPositiveNegatedProseSnippetFileCounter[0] = 0;
          StaticPositiveNegatedNotProseSnippetFileCounter[0] = 0;
          
          StaticControlAssertedProseSnippetFileCounter[0] = 0;
          StaticControlAssertedNotProseSnippetFileCounter[0] = 0;
       
          StaticControlNegatedProseSnippetFileCounter[0] = 0;
          StaticControlNegatedNotProseSnippetFileCounter[0] = 0;
          
          StaticPositiveTrueNegativeSnippetFileCounter[0] = 0;
          StaticCountersFirstTime = false;
        }
      }
        
     
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with getting the passed in arguments " + e.toString());
      throw new ResourceInitializationException();
    
    }
 
  } // end Method initialize() -----------
  
   // ----------------------------------------
   // Global variables
   // ----------------------------------------
   
  
   public static final int CONTEXT_BEFORE   = 0;
   public static final int FOCUS            = 1;
   public static final int CONTEXT_AFTER    = 2;
   public static final int ANSWER           = 3;
   public static final int ID               = 4;
   public static final int BEGIN_OFFSET     = 5;
   public static final int END_OFFSET       = 6;
   public static final int FILENAME         = 7;
  
   private static final char NEWLINE = '\n';
   private static final char CR = '\r';
   private static final char TILDA = '~';
   private static final char SPACE = ' ';
   
 
   
   private JCas spareCasToGetTypeSystem = null;
   
 
   private StringBuffer assertedProseSnippetDocPositive = new StringBuffer();
   private StringBuffer negatedProseSnippetDocPositive =  new StringBuffer();
   private StringBuffer trueNegativeSnippetDocPositive =  new StringBuffer();

   
   private StringBuffer assertedProseSnippetDocControl = new StringBuffer();
   private StringBuffer negatedProseSnippetDocControl =  new StringBuffer();
   
   private StringBuffer assertedNotProseSnippetDocPositive = new StringBuffer();
   private StringBuffer negatedNotProseSnippetDocPositive =  new StringBuffer();
   
   
   private StringBuffer assertedNotProseSnippetDocControl = new StringBuffer();
   private StringBuffer negatedNotProseSnippetDocControl =  new StringBuffer();
   private StringBuffer trueNegativeSnippetDocControl =  new StringBuffer();
   
  
   
   private ArrayList<Context> positive_assertedProseContextCache = new ArrayList<Context>();
   private ArrayList<Context> positive_negatedProseContextCache = new ArrayList<Context>();
   private ArrayList<Context> positive_assertedNotProseContextCache = new ArrayList<Context>();
   private ArrayList<Context> positive_negatedNotProseContextCache = new ArrayList<Context>();
   private ArrayList<Context> positive_trueNegativeContextCache = new ArrayList<Context>();


   

   private ArrayList<Context> control_assertedProseContextCache = new ArrayList<Context>();
   private ArrayList<Context> control_negatedProseContextCache = new ArrayList<Context>();
   private ArrayList<Context> control_assertedNotProseContextCache = new ArrayList<Context>();
   private ArrayList<Context> control_negatedNotProseContextCache = new ArrayList<Context>();
   private ArrayList<Context> control_trueNegativeContextCache = new ArrayList<Context>();
   
    
   
   
   private String focusLabel = null;
   private int lineWindowSize = 1;
  
   /*
   private int positive_assertedProseContextCounter[]    = new int[1];
   private int positive_negativeProseContextCounter[]    = new int[1];
   private int positive_assertedNotProseContextCounter[] = new int[1];
   private int positive_negativeNotProseContextCounter[] = new int[1];
   private int positive_trueNegativeContextCounter[] = new int[1];
   
   private int control_assertedProseContextCounter[]    = new int[1];
   private int control_negativeProseContextCounter[]    = new int[1];
   private int control_assertedNotProseContextCounter[] = new int[1];
   private int control_negativeNotProseContextCounter[] = new int[1];
   
   private int control_trueNegativeContextCounter[] = new int[1];
   */
   
   
   
   private int contextsPerFile = 200;

  
   protected String outputDir = null;

   protected Writer outputWriter = null;

   private int sampleRate = 1;
   private int sampleCounter = 0;
   private ProfilePerformanceMeter  performanceMeter = null;
   
   
   // ----------------------------------------
   // Synchronized Global variables
   // ----------------------------------------
   private static int GlobalCounter = 0;
   private static boolean StaticCountersFirstTime = false;
   private static int[] StaticPositiveAssertedProseSnippetFileCounter = new int[1]; 
   private static int[] StaticPositiveNegatedProseSnippetFileCounter = new int[1]; 
   private static int[] StaticPositiveAssertedNotProseSnippetFileCounter = new int[1]; 
   private static int[] StaticPositiveNegatedNotProseSnippetFileCounter = new int[1]; 
   private static int[] StaticPositiveTrueNegativeSnippetFileCounter = new int[1];
   
   
   private static int[] StaticControlAssertedProseSnippetFileCounter = new int[1]; 
   private static int[] StaticControlNegatedProseSnippetFileCounter = new int[1]; 
   private static int[] StaticControlAssertedNotProseSnippetFileCounter = new int[1]; 
   private static int[] StaticControlNegatedNotProseSnippetFileCounter = new int[1]; 
   private static int[] StaticControlTrueNegativeSnippetFileCounter = new int[1]; 
   
   
  
} // end Class ExampleAnnotator() ---------------
