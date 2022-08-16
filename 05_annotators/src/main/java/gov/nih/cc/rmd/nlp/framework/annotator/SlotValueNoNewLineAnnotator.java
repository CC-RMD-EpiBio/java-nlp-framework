// =================================================
/**
 * SlotValue is an annotator that identifies simple 
 * slot:value constructs in a document that has no newlines.
 * 
 * 
 * 
 *
 * @author  Guy Divita 
 * @created Aug 3, 2020
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Delimiter;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.Line;
import gov.va.chir.model.Section;
import gov.va.chir.model.SectionHeadingDelimiter;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.SlotValueDelimiter;
import gov.va.chir.model.Token;
import gov.va.chir.model.VAnnotation;
import gov.va.chir.model.WhitespaceToken;
import gov.va.chir.model.WordToken;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.annotator.AssertionAnnotator;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class SlotValueNoNewLineAnnotator extends JCasAnnotator_ImplBase {
  
  private boolean termProcessing = false;
  
  
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
    
    String docText = pJCas.getDocumentText();
    
    if ( docText != null && docText.length() > 0 ) {
   
      int numberOfLines = U.getNewlineOffsets(docText).length;
   
      if ( numberOfLines == 0 ) 
        process( pJCas, docText);
     
    }
    else {
      String id = VUIMAUtil.getDocumentId(pJCas);
      GLog.println(GLog.ERROR_LEVEL, "Issue with finding slot values in docs with no new lines " + id);
      // throw new AnalysisEngineProcessException();
    }
    
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue with " + this.getClass().getName() + " " + e.toString());
     // throw new AnalysisEngineProcessException();
    }
    
  } // end Method process
      
      
      
  // =================================================
  /**
   * process finds slot values in text with no newlines
   * 
   * Look for colons - traverse to the right until a piece
   * of punctuation is hit - that's the start of the header
   * the colon is the delimiter between the header and
   * the slot or section.
   * Look to the right of the colon until a period
   * is hit.  That's the end of the dependent content
   * or the end of the first sentence.  
   * 
   * @param pJCas
   * @param pDocText
   * @throws Exception
  */
  // =================================================
   private final void process(JCas pJCas, String pDocText) throws Exception { 
   
     int lastPtr = 0;
     int colonPtr = -1;
     int rightBoundry = -1;
     int leftBoundry = -1;
     char[] docTextChars = pDocText.toCharArray();
   
     identifySpaceDelimeters(pJCas, pDocText );
     
     String name = VUIMAUtil.getDocumentId(pJCas);
     if ( name.contains("30724"))
       System.err.println(name + " Name here ");
     
     boolean done = false;
     while ( !done ) {
      
      colonPtr = pDocText.indexOf(":", lastPtr);
      
      if ( colonPtr == -1 )
        done = true;
      else {
        leftBoundry = getLeftSlotValueBoundary( pJCas, colonPtr-1);
        
        if ( leftBoundry > -1 ) {
          rightBoundry = getRightSlotValueBoundary( pJCas, docTextChars, colonPtr+1);
          
          if ( rightBoundry > -1 ) 
            createSlotValue( pJCas, leftBoundry, colonPtr, rightBoundry); //  this could be a sentence of a section
        } // end if there is a right boundry
      } // end if there is a colon
     
      lastPtr = colonPtr + 1;
      
     } // end loop through colons      
     
   
} // end Method process() ------------------
     
  // =================================================
  /**
   * identifySpaceDelimeters finds all instances of
   * two spaces, followed by a Capital Letter 
   * 
   * @param pJCas
   * @param docText
  */
  // =================================================
   private void identifySpaceDelimeters(JCas pJCas, String docText) {
    
    List<Annotation> whiteSpaceTokens = UIMAUtil.getAnnotations(pJCas, WhitespaceToken.typeIndexID);
    
    if ( whiteSpaceTokens != null && !whiteSpaceTokens.isEmpty()) 
      for ( Annotation token : whiteSpaceTokens ) {
        if ( token.getCoveredText().equals("   "))
            createDelimiter( pJCas, token.getBegin(), token.getEnd());
      }
    
  
     
   } // end identifySpaceDelimiters() -----------------
 



  // =================================================
  /**
   * getLeftSlotValueBoundary  finds the last period before
   * this colon, within a window of 50 chars. 
   * 
   * @param pJCas
   * @param pColonPtr
   * @return int -1 if not found within the window
  */
  // =================================================
  private int getLeftSlotValueBoundary(JCas pJCas, int pColonPtr) {
    
    int returnVal = -1;
    
    int start = pColonPtr -50;
    if ( start < 0 )
      start = 0;
    List<Annotation> delimiters = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Delimiter.typeIndexID,  start, pColonPtr );
    
   if ( delimiters != null && !delimiters.isEmpty())
     returnVal = delimiters.get( delimiters.size() -1 ).getEnd();
          
      
    
    return returnVal;
  } // end Method getLeftSlotValueBoundary() ----------



  // =================================================
  /**
   * getRightSlotValueBoundary finds the next period after
   * the colon, within a window of 100 chars
   * 
   * @param pJCas
   * @param pDocText
   * @param colonPtr
   * @return int  (-1 if not found)
  */
  // =================================================
  private final int getRightSlotValueBoundary(JCas pJCas, char[] pDocText, int pColonPtr) {
    int returnVal = -1;
    int option1 = -1;
    int option2 = -1;
    int window = 0;
    
    int WINDOW = 99;
    
    List<Annotation> delimiters = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Delimiter.typeIndexID,  pColonPtr, pColonPtr+WINDOW);
    
    if ( delimiters != null && !delimiters.isEmpty())
      option1 = delimiters.get(0).getBegin() ;
    
    
    
    for ( int i = pColonPtr+1; i < pDocText.length; i++ )
      if ( pDocText[i] == '.') {
        option2 = i;  // this would be part of the value.
        break;
      }  else {
        window++;
        if ( window >= 99 )
          break;
      }
    
    
    if ( option1 > -1 )
      if ( option2 > -1)
       returnVal = Math.min(option1, option2);
      else
        returnVal = option1;
    else if (option2 > -1)
      returnVal = option2;
    
    return returnVal;
  } // end Method getRightSlotValueBoundary() ---------



  // =================================================
  /**
   * createSlotValue
   * 
   * @param pJCas
   * @param pLeftBoundry
   * @param pColonPtr
   * @param pRightBoundry
  */
  // =================================================
  private SlotValue createSlotValue(JCas pJCas, int pLeftBoundry, int pColonPtr, int pRightBoundry) {
    
    
    String coveredText = pJCas.getDocumentText().substring(pLeftBoundry, pRightBoundry );
    String contentHeaderString = pJCas.getDocumentText().substring(pLeftBoundry, pColonPtr );
    
    ContentHeading slot  = createSlot( pJCas, pLeftBoundry, pColonPtr);
    Delimiter delimiter = createDelimiter( pJCas, pColonPtr);
    DependentContent value = createValue( pJCas, pColonPtr, pRightBoundry);
    
    SlotValue statement = new SlotValue(pJCas);
    statement.setBegin( pLeftBoundry);
    statement.setEnd( pRightBoundry);
    statement.setId( this.getClass().getName() + "_" + this.annotationCounter);
    
    statement.setDisplayString( coveredText );
   
    statement.setContentHeaderString(contentHeaderString);
    statement.setHeading(slot);
    statement.addToIndexes();
    
    
    slot.setParent(statement);
    delimiter.setParent(statement);
    value.setParent(statement);
    
    
   return statement;
    
  } // end Method createSlotValue() ----------------



  // =================================================
  /**
   * createSlot 
   * 
   * @param pJCas
   * @param pLeftBoundry
   * @param pColonPtr
   * @return ContentHeading
  */
  // =================================================
   private ContentHeading createSlot(JCas pJCas, int pLeftBoundry, int pColonPtr) {
   
     
     ContentHeading contentHeader = new ContentHeading(pJCas);
     contentHeader.setBegin( pLeftBoundry) ;
     contentHeader.setEnd( pColonPtr );
   
     contentHeader.setId( this.getClass().getName() + ":NoNewLine:ContentHeader_" + this.annotationCounter++);
     contentHeader.setProcessMe(true);
     String sectionName = U.extremeNormalize(contentHeader.getCoveredText());
     contentHeader.setSectionName(sectionName);
     contentHeader.addToIndexes();
     
     return contentHeader;
     
     
  } // end createSlot() ------------------------------



  // =================================================
  /**
   * createDelimiter 
   * 
   * @param pJCas
   * @param pColonPtr
   * @return
  */
  // =================================================
   private final Delimiter createDelimiter(JCas pJCas, int pColonPtr) {
    SlotValueDelimiter delimiter = new SlotValueDelimiter( pJCas);
    delimiter.setBegin( pColonPtr);
    delimiter.setEnd( pColonPtr + 1);
    
    delimiter.setId( this.getClass().getName() + ":Delimiter_" + this.annotationCounter++);
    delimiter.addToIndexes();
    
    return delimiter;
    
  }


// =================================================
  /**
   * createDelimiter 
   * 
   * @param pJCas
   * @param pBegin
   * @param pEnd
   * @return
  */
  // =================================================
   private final Delimiter createDelimiter(JCas pJCas, int pBegin, int pEnd) {
   Delimiter delimiter = new Delimiter( pJCas);
    delimiter.setBegin( pBegin);
    delimiter.setEnd( pEnd);
    
    delimiter.setId( this.getClass().getName() + ":Delimiter_" + this.annotationCounter++);
    delimiter.addToIndexes();
    
    return delimiter;
    
  } // end Method createDelmiter() ------------------

   

  // =================================================
  /**
   * createValue 
   * 
   * @param pJCas
   * @param pColonPtr
   * @param pRightBoundry
   * @return DependentContent
  */
  // =================================================
 private DependentContent createValue(JCas pJCas, int pColonPtr, int pRightBoundry) {
    DependentContent  content = new DependentContent(pJCas);
   
    content.setBegin( pColonPtr +1);
    content.setEnd  (  pRightBoundry);
    content.setId(this.getClass().getName() + "_createValue_" + this.annotationCounter++);
    content.addToIndexes();
    
    return content;
  }






 
  // --------------------------------------------------------
  /**
   * loadKnownSlots loads the known slots from the resource url.
   * 
   * The format for knownSlots is  slotName|[true|false|sectionName]  false if this is a name to block from being recognized as a name
   *                                                                  SectionName if this is a known section name, even though
   *                                                                  it looks like a slot name.
   * @param pKnownSlotsFile  
   * @throws ResourceInitializationException if loading of patterns fails.
   */
  // --------------------------------------------------------
  public void loadKnownSlots(String pKnownSlotsFile) throws ResourceInitializationException{
      
    if (pKnownSlotsFile == null){
      String message = "Known slots file's location is null.";
      getContext().getLogger().log(Level.SEVERE,message);
      throw new ResourceInitializationException();
    }  
    
    int dummy[] = new int[1];
    dummy[0] = 1;    
    
    try {
       BufferedReader in = U.getClassPathResource( pKnownSlotsFile);
      String line = null;
      if ( this.knownSlots == null )
        this.knownSlots = new Hashtable<String, int[]>();
      
      while ((line = in.readLine()) != null) {
        if (line.trim().length() == 0) {
          continue;
        }
        
        if (!line.startsWith("#")) {
          try {
            String cols[] = line.split("\\|");
            if ( cols.length > 2 ) {
              int vals[] = new int[2];
              if ( cols[1] != null  ) {
                if      ( cols[1].contains("SLOT"))    vals[0] = SLOT;
                else if ( cols[1].contains("SECTION")) vals[0] = HEADING;
                else if ( cols[1].contains("HEADING")) vals[0] = HEADING;
                else if ( cols[1].contains("FALSE"))   vals[0] = FALSE;
              }
              if ( cols[2] != null ) {
                if      ( cols[2].contains("DONT_PROCESS")) vals[1] = FALSE;
                else if ( cols[2].contains("PROCESS"))      vals[1] = TRUE;
              }
              String keyz = cols[0].trim().toLowerCase();
              keyz = keyz.replaceAll("  ", " ");  // <-------------- important normalize to one space between tokens
          
              this.knownSlots.put(keyz,vals);
         
            } else {
              GLog.println(GLog.ERROR_LEVEL,"issue with this line: " + line);
            }
          } catch (Exception e) {
            e.printStackTrace();
            GLog.println(GLog.ERROR_LEVEL,"Issue with reading in a slot value line : " + line + " " + e.toString());
            
          }
        }
      } // end loop through the line of the resource
      in.close();
      
    } catch(Exception e ) {
      e.printStackTrace();
      String message = "File not found: '" + pKnownSlotsFile + " " + e.toString();
      GLog.println(GLog.ERROR_LEVEL,message);
      
      throw new ResourceInitializationException();
    }
  
  } // end Method loadKnownSlots() -----
  
  
  
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
      GLog.println(GLog.ERROR_LEVEL,"Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
    
    
   
    
    initialize( args);
  } // end Method initialize() ---------

  
  
  
   //----------------------------------
    /**
     * initialize loads in the resources needed for slotValues. Currently, this involves
     * a list of known slot names that might show up.
     *
     * @param pArgs
     * 
     **/
    // ----------------------------------
    public void initialize(String[] pArgs) throws ResourceInitializationException {
      
    
     this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
      
    String knownSlotsFile = null;
    
    
    try {
      knownSlotsFile =  "resources/vinciNLPFramework/slotValue/knownSlots.txt";
      loadKnownSlots( knownSlotsFile);
      
      
      // --------------------------
      // Check for the existence of a local slots file
      try {
        knownSlotsFile = "resources/vinciNLPFramework/local/slotValue/knownSlots.txt";
        BufferedReader in = null;
        if ( (in = U.getClassPathResource( knownSlotsFile) ) != null ) {
          in.close();
          loadKnownSlots( knownSlotsFile);
        }
      } catch ( Exception e2) {
        // GLog.println(GLog.ERROR_LEVEL,"Not using local slot value definitions");
        
      }
        
    } catch (Exception e) {
     
    
      throw new ResourceInitializationException();
    }
  
  } // end Method initialize() --------------
  

 
  // -----------------------------------------
  // class Variables
  // -----------------------------------------

//---------------------------------------
//Global Variables
//---------------------------------------
   private long                  annotationCounter = 0;
  private Hashtable<String, int[]>     knownSlots = null;
  private static final int                   SLOT = 1;
  private static final int                HEADING = 2;
  private static final int                  FALSE = -1;
  private static final int                   TRUE = 1;
  private ProfilePerformanceMeter  performanceMeter = null;
 
  
} // end Class SlotValue ---------------------
