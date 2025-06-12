
// =================================================
/**
 * SlotValue is an annotator that identifies simple 
 * slot:value constructs in a document. These get 
 * labeled as utterances of the slotValue subclass.
 * 
 * This class relies on a small file of known slot names that don't 
 * fit the regular pattern (resources/slotValue/knownSlots.txt)
 * 
 * This resource includes if the name should be a slot, or a heading,
 * or neither.  The resource also includes if the slot should be
 * further processed or not.  Some slots are known to have content
 * that will be quantitative, or PHI information.
 *
 * The typical slot pattern includes looking for a colon delimiter,
 * looking to the left of the delimiter (to the beginning of the line) 
 * and to the right of the delimiter (to the end of line)
 * and looking at the line below the delimiter.
 * If the string to the left of the delimiter is all in upper case
 * or in initial caps, it's a header of some sort.
 * If there is a header, and there is content to the left of the
 * delimiter, if there are only a few tokens (<14) it's a slot content.
 * If there is content on the line under the header, and if it's <14 tokens
 * it's a slot content.
 *
 * @author  Guy Divita 
 * @created Feb 17, 2011
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
import gov.va.chir.model.WordToken;
import gov.va.vinci.model.temporal.AbsoluteDate;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.framework.model.Date;
import gov.nih.cc.rmd.nlp.framework.annotator.AssertionAnnotator;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class SlotValueAnnotator extends JCasAnnotator_ImplBase {
  
  private boolean termProcessing = false;
  
  
  @Override
  // -------------------k----------------------
  /**
   * process finds slotValues in the whole document
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    
    if ( !this.termProcessing ) {
    try {
    this.performanceMeter.startCounter();
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " Start " + this.getClass().getSimpleName());
    
   
    List<Annotation> lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID);
    
    if (lines != null && !lines.isEmpty()) {
      // ------------------------------------------------------
      // transform the list of tokens into something I can move
      // back and forth with
      // ------------------------------------------------------
      List<Token>[] rows = getRows(pJCas, lines );
     
     
      process( pJCas, rows ) ;
      
      
    }
    else {
      String id = VUIMAUtil.getDocumentId(pJCas);
      GLog.println(GLog.ERROR_LEVEL, "wtf - no lines? " + id);
      // throw new AnalysisEngineProcessException();
    }
    
   
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " End " + this.getClass().getSimpleName());
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue with " + this.getClass().getName() + " " + e.toString());
     // throw new AnalysisEngineProcessException();
    }
    
   
    }
  } // end Method process
      
      
      
  // -----------------------------------------
  /**
   * process finds slotValues in lists of rows of tokens that make up lines

   * 
   * @param aJCas
   */
  // -----------------------------------------
  protected void process(JCas pJCas, List<Token>[] rows ) throws AnalysisEngineProcessException {

    ArrayList<Token> content = null;
    
      if ( rows == null ) 
        ;
       // GLog.println(GLog.ERROR_LEVEL,"Rows are empty? ");
      else if (rows != null) {
         
      
        for (int i = 0; i < rows.length ; i++) {
          
          try {
            
          // ----------------------------------------------------------------
          // If the row is not empty, AND it doesn't look like a List element
          //  String key = getContentFromTokens(pJCas, rows[i]);
           
          if (rows[i] != null && rows[i].size() > 0 &&  !containsListDelimiter(rows[i] ) ) { 
            int slotOrHeading[] = containsSlot(pJCas, rows[i]);
            int delimiterIndex = slotOrHeading[2];
            List<Token> delimiter = getDelimiter(delimiterIndex, rows[i]);
            List<Annotation> delimiterz = convertTokensToAnnotations( delimiter);
            boolean process = false;
            if (slotOrHeading[1] == 1)
              process = true;
            if (slotOrHeading[0] == SLOT || slotOrHeading[0] == HEADING) {
              List<Annotation> contentHeader = getContentHeader(rows[i], delimiterIndex);
              List<Token> contentHeaderz = convertAnnotationsToTokens( contentHeader);
           

              if (slotOrHeading[0] == SLOT) {
                
                 if (contentHeader != null  ) {
                   
                   /* ---------------------------
                    * 1|Allergies:
                    * 2|
                    * 3|Medication:
                    * If there is a blank line after the content heading, 
                    * this could be a slot value without the value or
                    * it could be a section heading.  The only way to tell
                    * is via a section/slot name lookup.
                    */
                   
                  if ( lineIsBlankAfterDelimiter( pJCas, contentHeader, delimiterz)) {
                    if ( nextLlineIsBlankAfterContentHeading(pJCas, contentHeader))
                      createSlotValue(pJCas, contentHeader, content, delimiter,  process);
                    else
                      if ( nextLineLooksLikeASlot( pJCas, contentHeader))
                        createSlotValue(pJCas, contentHeader, content, delimiter,  process);
                      else {
                       createSectionHeading(pJCas, contentHeaderz, delimiter);
                      }
                  } else {
                     /* -------------------------
                      *  At this point, there is a xxxx: yyyyy , where xxx was determined to be a slot heading
                      *  So, at this point, we can assume it's a slot value, or, poke further and see if the stuff
                      *  on the right of the delimiter is a short value.  If it is, then make a slotvalue, or
                      *  make a section
                      *
                     */
                   
                      List<Annotation> tokensToTheRightOfDelimiter = getTokensToTheRightOfTheDelimiter( pJCas, delimiter );
                      if ( containsValue(pJCas, tokensToTheRightOfDelimiter ) ) {
                        List<Token> tokenzToTheRightOfDelimiter = convertAnnotationsToTokens( tokensToTheRightOfDelimiter);
                        createSlotValue(pJCas, contentHeader, tokenzToTheRightOfDelimiter, delimiter,  process);
                      }
                      else {
                      
                        createSectionHeading(pJCas, contentHeaderz, delimiter);
                      }
                  }
                  
                  } else if (containsValue(pJCas, rows[i], true)) {           
                    content = getContent(rows[i], delimiterIndex);
                  if ( !overlapsSlotValue(pJCas, contentHeaderz, content) )
                    createSlotValue(pJCas, contentHeader, content, delimiter,  process);
                 
                  }
                
              } else if (slotOrHeading[0] == HEADING) {
               
                
                if (contentHeader != null && contentHeader.size() < 10 )
                  createSectionHeading(pJCas, contentHeaderz, delimiter);
              }
            } // if row isn't null
          } // end if row contains slot
          } catch (Exception e ) {
            e.printStackTrace();
            String msg = "Issue with this row " + e.toString();
            GLog.println(GLog.ERROR_LEVEL,msg);
          }
        } // end loop through rows
      } // end if there are any rows
   
   
  } // end Method process() ------------------
  
  // =================================================
  /**
   * getTokensToTheRightOfTheDelimiter returns the tokens to the right of the delimiter
   * 
   * @param pJCas
   * @param pDelimiters
   * @return List<Annotation>
  */
  // =================================================
  private List<Annotation> getTokensToTheRightOfTheDelimiter(JCas pJCas, List<Token> pDelimiters) {
    
    List<Annotation> returnVal = null;
    if ( pDelimiters != null && !pDelimiters.isEmpty()) {
      Annotation delimiter = pDelimiters.get((pDelimiters.size() -1));
      Annotation currentLine = UIMAUtil.getLine(pJCas, delimiter);
      returnVal = UIMAUtil.getAnnotationsBySpan(pJCas, WordToken.typeIndexID, delimiter.getEnd() + 1, currentLine.getEnd() );
    
    }
    
    return returnVal;
  } // end Method getTokensToTheRightOfTheDelimiter() ------



  // =======================================================
  /**
   * overlapsSlotValue returns true if the tokens overlap an existing
   * slot value structure
   * 
   * @param pJCas
   * @param contentHeader   <-- assumes this is in offset order ascending
   * @param content         <-- assumes this is in offset order ascending
   * @return boolean
   */
  // =======================================================
  private boolean overlapsSlotValue(JCas pJCas, List<Token> contentHeader, List<Token> content) {
    
    boolean returnVal = false;
    int begin = 0;
    int  _end = 0;
    int minBegin = 9999999;
    int maxEnd = 0;
    if ( contentHeader != null ) {
      for ( Annotation aToken : contentHeader ) {
        begin = aToken.getBegin();
        _end = aToken.getEnd();
        if ( begin < minBegin )  
          minBegin = begin;
        if ( _end > maxEnd )  
          maxEnd = _end;
      } // end loop thru slot tokens  
           
    } // end if contentHeader is not null
    
    if ( content != null ) {
      for ( Annotation aToken : content ) {
        begin = aToken.getBegin();
        _end = aToken.getEnd();
        if ( begin < minBegin )  
          minBegin = begin;
        if ( _end > maxEnd )  
          maxEnd = _end;
      } // end loop thru value tokens  
           
    } // end if content is not null
      
    List<Annotation> slotValues = UIMAUtil.getAnnotationsBySpan(pJCas, SlotValue.typeIndexID, minBegin, maxEnd);
  
    if ( slotValues != null && slotValues.size() > 0 )
       returnVal = true;
    
    return returnVal;
  }    // End Method overlapsSlotValue() ======================
  


//=======================================================
/**
 * nextLlineIsBlankAfterContentHeading returns true if the line after a xxx: 
 * is just a new line 
 * 
 * @param pJCas
 * @param contentHeader
 * @param delimiterz
 * @return boolean
 */
// =======================================================
private boolean nextLlineIsBlankAfterContentHeading(JCas pJCas, List<Annotation> pContentHeader) {

 boolean returnVal = false;
 
 Annotation contentHeader = pContentHeader.get( pContentHeader.size() -1 );
 List<Annotation> lines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Line.typeIndexID,  contentHeader.getBegin(), contentHeader.getEnd() +2);
 
 if ( lines != null && !lines.isEmpty() ) {
   
   Annotation aLine = lines.get(0);
  
   List<Annotation>nextLines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Line.typeIndexID,  aLine.getEnd() + 1, aLine.getEnd() + 2);
   
   if ( nextLines != null && !nextLines.isEmpty() ) {
     Annotation nextLine = nextLines.get(0);
     String nextLineText = nextLine.getCoveredText();
     if ( nextLineText == null || nextLineText.trim().length() == 0 )
       returnVal = true;
     
   }
   else {
     // we are at the bottom of the file
     returnVal = true;
   }
 }
   
  return returnVal;
}  // End Method contentIsOnNextLine() ======================
  
// =======================================================
/**
 * nextLineLooksLikeASlot returns true if the line after a xxx:
 * looks also like a slot
 * it starts with a capital letter, and has a colon in it
 * 
 * @param pJCas
 * @param contentHeader
 * @param delimiterz
 * @return boolean
 */
// =======================================================
private boolean nextLineLooksLikeASlot(JCas pJCas, List<Annotation> contentHeader2) {

  boolean returnVal = false;

  Annotation contentHeader = contentHeader2.get(contentHeader2.size() - 1);
  List<Annotation> lines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Line.typeIndexID, contentHeader.getBegin(),
      contentHeader.getEnd() + 2);

  if (lines != null && !lines.isEmpty()) {

    Annotation aLine = lines.get(0);

    List<Annotation> nextLines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Line.typeIndexID, aLine.getEnd() + 1,
        aLine.getEnd() + 2);

    if (nextLines != null) {
      Annotation nextLine = nextLines.get(0);
      String nextLineText = nextLine.getCoveredText();
      if (nextLineText != null && nextLineText.trim().length() > 0 ) {
        String buff = nextLineText.trim();
        if ( buff.contains(":") || Character.isUpperCase(buff.charAt(0)))
          returnVal = true;

      }
    } 
  }

  return returnVal;
} // End Method contentIsOnNextLine() ======================

//=======================================================
/**
* lineIsBlankAfterDelimiter returns true if the line after a xxx: ______
* is empty
*  
* 
* @param pJCas
* @param contentHeader
* @param delimiter
* @return boolean
*/
//=======================================================
private boolean lineIsBlankAfterDelimiter(JCas pJCas, List<Annotation> pContentHeader, List<Annotation> delimiter) {

boolean returnVal = false;
int delimiterEnd = -1;

Annotation contentHeader = pContentHeader.get( pContentHeader.size() -1 );
List<Annotation> lines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Line.typeIndexID,  contentHeader.getBegin(), contentHeader.getEnd() +2);

if ( lines != null && !lines.isEmpty() ) {
 
 Annotation aLine = lines.get(0);
 String aLineText = aLine.getCoveredText();
 
 if ( delimiter == null || delimiter.isEmpty()) {
   delimiterEnd = contentHeader.getEnd();
   
 }
 
 // if there is any text after the content heading and delimiter on the same line, return false
 
 if ( delimiterEnd == -1)
   delimiterEnd =  delimiter.get( delimiter.size() -1).getEnd() - aLine.getBegin();
 
 if ( delimiterEnd >= aLineText.length() ) 
   returnVal = true;
 else
 {
   String restOfLine = aLineText.substring(delimiterEnd +1);
  
   if ( restOfLine == null || restOfLine.trim().length() == 0 )
     returnVal = true;
 }
 
 
 
}
 
return returnVal;
}  // End Method contentIsOnNextLine() ======================


  // =======================================================
  /**
   * contentIsOnNextLine returns the content of the next line
   * assuming that this is a slot value, where the value is
   * on the next line
   * 
   * If the content is > 40 chars or 10 tokens, this is going 
   * to assume we got it wrong, and it's not a slot/value at all
   * 
   * or if the next line also looks like a slot value return null
   * 
   * @param pContentRowOfTokens
   * @param 
   * @return String
   */
  // =======================================================
  private List<Token> contentIsOnNextLine(List<Token> pContentRowOfTokens ) {
    
    List<Token> returnVal = null;
    if ( pContentRowOfTokens != null ) {
      if ( pContentRowOfTokens.size() < 10 )  { // <---- limits the dependent content by 10 tokens
        StringBuffer contentText = new StringBuffer();
        for ( Token token : pContentRowOfTokens ) {
          contentText.append( token.getCoveredText() + " ");
          if ( token.getCoveredText().contains(":"))
            return null;
        }
        if ( contentText.length() >= 40 ) 
          returnVal = null;
        else 
          returnVal = pContentRowOfTokens;
      } // <---- end if it's not too long
      
      
    }  // end if there is content tokens
    return returnVal;
  } // End Method contentIsOnNextLine() ======================
  



  // -----------------------------------------
  /**
   * getDelimiter returns an arrayList containing the
   * token or tokens that make up the delimiter
   * 
   * @param pDelimiterIndex
   * @param pTokens
   * @return ArrayList<Token>
   */
  // -----------------------------------------
  protected static ArrayList<Token> getDelimiter(int pDelimiterIndex, List<Token> pTokens) {
    
    ArrayList<Token> delimiter = new ArrayList<Token>(1);
    if ( (pTokens == null ) || ( pDelimiterIndex < 0) || (pDelimiterIndex > pTokens.size()))
      delimiter = null;
    else
      delimiter.add( pTokens.get( pDelimiterIndex));
    
    return delimiter;
  } // end Method getDelimiter() ---------------

  // -----------------------------------------
  /**
   * getContentHeader returns the left part of the slotValue pair.
   * 
   * @param pTokens
   * @param pDelimiterIndex
   * @return ArrayList<Token>
   */
  // -----------------------------------------
  protected static ArrayList<Annotation> getContentHeader(List<Token> pTokens, int pDelimiterIndex) {
   // GLog.println(GLog.ERROR_LEVEL," delimiter index = " + pDelimiterIndex);
    int del = pDelimiterIndex;
    if ( del < 0 )  del = pTokens.size();
    ArrayList<Annotation> contentHeader = new ArrayList<Annotation>();
    for ( int i = 0; i < pTokens.size() && i < del; i++ ) {
      Token aToken = pTokens.get(i);
      contentHeader.add( aToken);
    } // end loop through tokens
    if ( contentHeader.size() == 0 )
       contentHeader = null;
    
    return contentHeader;
  } // end Method getContentHeader() ---------

  // -----------------------------------------
  /**
   * getContent returns the right side of a slot/value pair
   * 
   * @param pTokens
   * @param pDelimiterIndex 
   * @return ArrayList<Token>
   */
  // -----------------------------------------
  protected static ArrayList<Token> getContent(List<Token> pTokens, int pDelimiterIndex) {
    ArrayList<Token> content = new ArrayList<Token>();
    
    for ( int i = pDelimiterIndex + 1; i < pTokens.size(); i++ ) {
      Token aToken = pTokens.get(i);
      content.add( aToken);
    }
    if ( content.size() == 0)
      content = null;
    
    return content;
  } // end Method getContent() --------------

  // -----------------------------------------
  /**
   * getRows returns an array of ArrayList<Token>
   * given the index to the tokens of the document.
   * 
   * @param plines
   * @return ArrayList[] of ArrayList<Token>
   */
  // -----------------------------------------
   @SuppressWarnings("unchecked")
  protected ArrayList<Token>[]  getRows(JCas pJCas, List<Annotation> pLines ) {
    
    ArrayList<List<Token>> rows = new ArrayList<List<Token>>();
    ArrayList<Token> rowArray[] = null;
    List<?> tokens = new ArrayList<Token>();
    
    
    if ( pLines != null && !pLines.isEmpty())
    
      for ( Annotation aLine : pLines ) {
      
        tokens = UIMAUtil.getAnnotationsBySpan(pJCas, Token.typeIndexID, aLine.getBegin(), aLine.getEnd(), true );
     
        if (( tokens != null ) && (tokens.size() > 0) ) 
          rows.add((List<Token>) tokens);
        
    } // end loop through tokens of the document
    
    
    
    if (( rows!= null  && !rows.isEmpty() )) {
      
      rowArray = (ArrayList[]) rows.toArray( new ArrayList[rows.size()]);
    }
  
    
    return rowArray;
  } // end Method getRows() ------------------
   
   // -----------------------------------------
   /**
    * getRows returns an array of ArrayList<Token>
    * given the index to the tokens of the document.
    * This is used in the slotValueFromList annotator where
    * the first delimiter of the token needs to be pulled off before
    * looking for a slot value
    * 
    * @param pTokens
    * @return ArrayList[] of ArrayList<Token>
    */
   // -----------------------------------------
   @SuppressWarnings("unchecked")
  public ArrayList<Token>[]  getRowsAux(JCas pJCas, List<Token> pTokens ) {
     
     ArrayList<List<Token>> rows = new ArrayList<List<Token>>();
     ArrayList<Token> rowArray[] = null;
     List<?> tokens = new ArrayList<Token>();
   
         if (( tokens != null ) && (tokens.size() > 0) ) 
           rows.add((List<Token>) tokens);
     
     if (( rows!= null  && !rows.isEmpty() )) {
       
       rowArray = (ArrayList[]) rows.toArray( new ArrayList[rows.size()]);
     }
   
     
     return rowArray;
   } // end Method getRows() ------------------

  // -----------------------------------------
  /**
   * containsSlot returns true if this row looks like
   * a slot from a slotValue pair.  This is typically
   * a row with a colon delimiter, where the slot is
   * either all in upper case, or initial caps.
   * There is also a small dictionary of lookups for
   * those that miss the above criteria, such as cc:
   * 
   * @param pTokens
   * @return int[2]    [0] SLOT|HEADING|FALSE
   *                   [1] TRUE|FALSE
   *                   [3] the index of the delimiter 
   */
  // -----------------------------------------
  private int[]  containsSlot(JCas pJCas, List<Token> pTokens) {
   
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "containsSlot", "entering containsSlot");
    int[] returnValue = new int[3];
    returnValue[0] = FALSE;
    
    returnValue[1] = TRUE;
    returnValue[2] = -1;

    int delimiter = findDelimiter(pJCas, pTokens);
    returnValue[2] = delimiter;
    
    if (delimiter > -1) {
     
      returnValue[0] = SLOT;
      ArrayList<Token> tokensBeforeDelimiter = new ArrayList<Token>(delimiter);
      ArrayList<Token> tokensAfterDelimiter = new ArrayList<Token>(pTokens.size());
      
      for (int i = 0; i < delimiter; i++)
        tokensBeforeDelimiter.add(pTokens.get(i));
      
      for (int i = delimiter + 1; i< pTokens.size(); i++) 
        tokensAfterDelimiter.add( pTokens.get(i));

      String  fullBuff = getContentFromTokens(pJCas,pTokens);
      String  buff = getContentFromTokens(pJCas,tokensBeforeDelimiter);
      String rBuff = getContentFromTokens(pJCas, tokensAfterDelimiter);
      
      
      if (buff != null) {
        GLog.println(GLog.DEBUG_LEVEL,this.getClass(), "containsSlot", "Delimiter found " + delimiter + " in the line |" + fullBuff );
        String notTouchedBuff = buff;
        buff = buff.toLowerCase();
        
        buff = replaceMultipleWhiteSpace( buff );
       
        // -------------------------------
        // if this isn't a slot or content heading - hint all lowercase words to the left
        if ( U.isAllLowerCase( notTouchedBuff ) || tokensBeforeDelimiter.size() > 20) {
          returnValue[0] = FALSE;
          returnValue[1] = TRUE;
          returnValue[2] = -1;
         return returnValue;
        }
          
     
        int rows[] = this.knownSlots.get(buff);
       
        if (rows == null) {

          // ----------------------------------
          // Looking at the left side of the delimiter
          /*   ===============================================
           *   This code used to see if the left side was all in caps
           *   if so, it's a section heading - that's not a good rule 
           *   
          for (int i = 0; i < delimiter; i++) {
            Token aToken = pTokens.get(i);

            // if the tokens are in all caps, or initial caps, -- not slot/value but a contentHeading/content  <--- There should be a better criteria
            //                                                                                                      like if the content contains a period, and/or has multiple tokens ...
            if ((aToken.getTypeIndexID() != WhitespaceToken.typeIndexID)
                && (( (aToken.getAllCapitalization()  )) )  // || aToken.getInitialCapitalization())))
                || (aToken.getCoveredText().contains("?") ) ) {
              returnValue[0] = FALSE;
              // GLog.println(GLog.ERROR_LEVEL," should not be heeeere|" + aToken.getAllCapitalization() + "|" + aToken.getInitialCapitalization() + "|" + aToken.getCoveredText());
              break;
            }
        
          } // end loop from begin to delimiter
          */
          
          // -----------------------------------------
          // if the right tokens contain more than one period, or comma, or include a semi colon
          // mark this as the beginnings of a section
          // -----------------------------------------
          if ( returnValue[0] != FALSE ) {
            if (rBuff != null && ((( U.numberOf(rBuff, ".") > 0) && ( U.containsLetters( rBuff)) ||
                (( U.numberOf(rBuff, ",") > 0) && ( U.containsLetters( rBuff )))) ||
                ( U.numberOf(rBuff, ";") > 0) )) {
              // GLog.println(GLog.ERROR_LEVEL,"something on the right hand side of the delimiter is saying heading |" + rBuff + "|");
              returnValue[0] = HEADING;
              returnValue[1] = TRUE;
            
            } else if ( rBuff == null ) {
              returnValue[0] = HEADING;
              returnValue[1] = TRUE;
              
            }
          }
          
          // -----------------------------------
          // A known slot was found
          // -----------------------------------
        } else {
          GLog.println(GLog.DEBUG_LEVEL,this.getClass(), "containsSlot", "something on the right hand side of the delimiter is saying heading from the known slots |" + buff + "|");
          returnValue[0] = rows[0];
          returnValue[1] = rows[1];

        }
      } // end if buff != null
    } // end delimiter found
    else {
     String key = getContentFromTokens(pJCas, pTokens);
     
     if ( key != null ) {
       key = key.toLowerCase();
       // GLog.println(GLog.ERROR_LEVEL," ----------------------- looking for |" + key + "|");
       int[] rows = this.knownSlots.get(key); 
       if ( rows != null  ) {
        //  GLog.println(GLog.ERROR_LEVEL,"The known slots is saying that this is a heading " + key.toLowerCase());
         returnValue[0] = rows[0];
         returnValue[1] = rows[1];
       }
    }
    }   
    
   GLog.println(GLog.DEBUG_LEVEL,this.getClass(), "containsSlot", "Returning with something");
    
    
    return returnValue;
  } // end Method containsSlot() -------------

  // =================================================
  /**
   * replaceMultipleWhiteSpace returns the string
   * with single whitespace rather than  multiple whitespaces
   * 
   * @param buff
   * @return String
  */
  // =================================================
  private final String replaceMultipleWhiteSpace(String pBuff) {
    
   
    String previousBuff = pBuff;
    String buff = pBuff; 
    String returnVal = null;
    buff = buff.replaceAll("  ", " ");
    while ( !previousBuff.contentEquals( buff )) {
      previousBuff = buff;
      buff = buff.replaceAll("  ", " ");
      
    }
    returnVal = buff;
    return returnVal;
  } // end Method replaceMultipleWhiteSpace() ------



  // -----------------------------------------
  /**
   * findDelimiter returns the token index of
   * a slot value delimiter (:) .
   * 
   * @param pTokens
   * @return int (-1 if not found)
   */
  // -----------------------------------------
  public int findDelimiter(JCas pJCas, List<Token> pTokens) {
 
    int delimiter = -1;
    for ( int i = 0; i < pTokens.size(); i++ ) {
      String buff = pTokens.get(i).getCoveredText();
      if ( buff != null && ( (
          buff.indexOf(":") == 0  ) || buff.indexOf("=") == 0 )) {
          delimiter = i;
          break;
      }
    } // end loop tokens of the row
    
      return delimiter;
  } // end Method findDelimiter() ------------

  // -----------------------------------------
  /**
   * containsValue returns true if 
   *    There are a few tokens
   *    or 
   *    there is a slot value delimiter followed by a few tokens,
   *    and the last token of this line is not an end of sentence.
   * 
   * @param pTokens
   * @param pSameLine  (true if the same line)
   * @return
   */
  // -----------------------------------------
  private boolean containsValue(JCas pJCas, List<Token> pTokens, boolean pSameLine ) {
    boolean returnValue = false;
    
    String content = getContentFromTokens( pJCas, pTokens);
    // GLog.println(GLog.ERROR_LEVEL,"The content = |" + content + "|");
    // -------------------------------------------
    // Make this a section content if
    //   one or more than 1 period
    //   one or more semicolon
    //   one or more comma
    // -------------------------------------------
    
    if ( !pSameLine ) {   
      if (( content == null) ||  (content.trim().length() < 1)   )
        returnValue = false;
    
      else if (( U.numberOf(content, ". ") > 0) ||
               ( U.numberOf(content, ",") > 0) ||
               ( U.numberOf(content, ";") > 0) ||
               ( U.numberOf(content, "=") > 1) ||
               ( U.numberOf(content, ":") > 0) ||
               ( U.allUpperCase( content)) )
        returnValue = false;

      else if ( pTokens.size() < 8) {
        returnValue = true;
        
      }
    } else {
      // -----------------------------
      // the same line 
      // -----------------------------
      int i = findDelimiter(pJCas, pTokens);
      if ( i > 0) {
        Token lastToken = pTokens.get(pTokens.size() -1);
        String lastTokenString = lastToken.getCoveredText();
        // GLog.println(GLog.ERROR_LEVEL,"Last token = " + lastToken.getCoveredText());
        char lastCharOfLine = lastTokenString.charAt(lastTokenString.length() -1 );
        int numberOfTokensAfterDelimiter = pTokens.size() - i;
        if (( numberOfTokensAfterDelimiter < 20*2) && !U.isSentenceBreakChar(lastCharOfLine ) )
        returnValue = true;
        else {
          // GLog.println(GLog.ERROR_LEVEL, (pTokens.size()  - i )+ "|" + !lastToken.getSentenceBreak() );
        }
      }
    } // end if the same line
    
    // GLog.println(GLog.ERROR_LEVEL,"Theeee returnValue = " + returnValue );
    return returnValue;
  } // end Method contiansValue() ------------
  
//-----------------------------------------
 /**
  * containsValue returns true if 
  *    There are a few tokens
  *    or 
  *    there is a slot value delimiter followed by a few tokens,
  *    and the last token of this line is not an end of sentence.
  * 
  * @param tokensToTheRightOfDelimiter
  *
  * @return boolean 
  */
 // -----------------------------------------
 private boolean containsValue( JCas pJCas, List<Annotation> tokensToTheRightOfDelimiter  ) {
   boolean returnValue = false;
   
   
   
   
   String content = getContentFromTokens( pJCas, tokensToTheRightOfDelimiter);
   
   // -------------------------------------------
   // Make this a section content if
   //   one or more than 1 period
   //   one or more semicolon
   //   one or more comma
   // -------------------------------------------
   
    
     if (( content == null) ||  (content.trim().length() < 1)   )
       returnValue = false;
   
     else if (containsDateTime( pJCas, tokensToTheRightOfDelimiter) )
       returnValue = true;
     
     else if (  // ( U.numberOf(content, ". ") > 0) ||
                //  ( U.numberOf(content, ",") > 0) ||
              ( U.numberOf(content, ";") > 0) ||
              ( U.numberOf(content, "=") > 1) )
              // ( U.numberOf(content, ":") > 0) ) 
       
       // how do you tell if this is a name or not?
       // Doe, John M. 
       // vs
       // Patient had expressed an interest, but was not receptive.
       
             // ( U.allUpperCase( content)) )
       returnValue = false;

     else {
       List<Annotation>justWordTokens = stripPunctuationTokens( tokensToTheRightOfDelimiter );
       if ( justWordTokens == null || justWordTokens.size() < 8) {
     
         returnValue = true;
       }  
     }
 
   return returnValue;
 } // end Method contiansValue() ------------

  // =================================================
/**
 * stripPunctuationTokens is an attempt to ignore punctuation like (),; 
 * to not count the punc when counting the number of tokens for the purposes
 * to see if it is a slot value, rather than a sentence.
 * 
 * @param tokensToTheRightOfDelimiter
 * @return List<Annotation>
*/
// =================================================
private List<Annotation> stripPunctuationTokens(List<Annotation> tokensToTheRightOfDelimiter) {
  
  List<Annotation> returnVal = null;
  
  if (tokensToTheRightOfDelimiter != null && !tokensToTheRightOfDelimiter.isEmpty()  ) {
    returnVal = new ArrayList<Annotation>(tokensToTheRightOfDelimiter.size());
  
    for ( Annotation token :  tokensToTheRightOfDelimiter )
      if ( !((Token) token).getContainsPunctuation() ) 
        returnVal.add( token );
  }
  
  
  return returnVal;
} // end Method stripPunctuationTokens() -----------



  // =================================================
/**
 * containsDateTime returns true if this span includes any date/time references
 *   (assumption - pTokens are offset ordered;
 * @param pJCas
 * @param pTokens
 * @return boolean
*/
// =================================================
private final boolean containsDateTime(JCas pJCas, List<Annotation> pTokens) {
 
  boolean returnVal = false;
  
    int begin_ = pTokens.get(0).getBegin();
    int end_   = pTokens.get( pTokens.size() -1 ).getEnd();
  
    List<Annotation> dates = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  Date.typeIndexID, begin_, end_, true );
    
    if ( dates != null && !dates.isEmpty() )
      returnVal = true;
    
    
    
  return returnVal ;
} // end Method containsDateTime() ----------------



  // -----------------------------------------
  /**
   * createSlotValue creates a slotValue annotation
   * from the slot and the value.  This method
   * also sets the phrases of the slotvalue, and
   * the tokens that are contained within the
   * slot value.
   * 
   * This version is used in the SlotValueWithinLists class
   * 
   * @param pJCas
   * @param pSlotTokens
   * @param pValueTokens
   * @param pDelimiter
   * 
   * 
   */
  // -----------------------------------------
  public void createSlotValue(JCas pJCas, 
                               List<Annotation> pSlotTokens, 
                               List<Annotation> pValueTokens,
                               List<Annotation> pDelimiter
                               ) {
    if ( pSlotTokens == null || pSlotTokens.isEmpty())  return;
    int      begin = ((Token) pSlotTokens.get(0                     )).getBegin();
    int    slotEnd = ((Token) pSlotTokens.get(pSlotTokens.size() -1 )).getEnd();
    int valueBegin = -1;
    int valueEnd = slotEnd;
    if ( pValueTokens != null && pValueTokens.size() > 0) {
      valueBegin = ((Token) pValueTokens.get(0)).getBegin();
      valueEnd = ((Token) pValueTokens.get(pValueTokens.size()-1)).getEnd();
    }
    int z = valueEnd - valueBegin;
    
    ContentHeading contentHeader;
    if ( z > 0 && z < 3000 ) {
    SlotValue     slotValue = new SlotValue(pJCas);
   
   // VUIMAUtil.setProvenance( pJCas, slotValue, this.getClass().getName() );
    slotValue.setBegin(begin);
    slotValue.setEnd( valueEnd );
    String buff = slotValue.getCoveredText();
    slotValue.setDisplayString( buff );
    
    slotValue.setId( "SlotValue:" + this.annotationCounter++);
    

    contentHeader = createContentHeading( pJCas, pSlotTokens, this.annotationCounter++);
    
    
    contentHeader.setParent(slotValue);
    String contentHeaderString = contentHeader.getCoveredText();
    
    if ( contentHeaderString == null || contentHeaderString.trim().length() == 0 ) {
      GLog.println(GLog.ERROR_LEVEL,"Something is wrong here empty content heading of a slotValue " );
     // throw new RuntimeException();
    }
    slotValue.setContentHeaderString(contentHeaderString);
    slotValue.setHeading(contentHeader);
    slotValue.setProcessContent( true);
    
    
   
    // ------------------------
    // Delimiter
    
    if ( pDelimiter != null ) {
      Delimiter delimiter = createDelimiter( pJCas, pDelimiter, slotValue );
      slotValue.setDelimiter(delimiter.getCoveredText());
    }
      
    // ------------------------
    // Dependent content
    if ( pValueTokens != null && !pValueTokens.isEmpty()) {
      
      DependentContent contentValue = createDependentContent(pJCas, pValueTokens, slotValue );
      contentValue.setParent( slotValue);
      
      slotValue.setContentString( contentValue.getCoveredText());
      slotValue.setDependentContent( contentValue);
     
    }
   
   // if ( !this.assertInAssertionAnnotator )  ---- now done in assertion annotator properly
    //  assertQuestionAnswer( pJCas, slotValue);
    
    slotValue.addToIndexes();
    
    // GLog.println(GLog.ERROR_LEVEL,"Made a slot value");
    }    
  } // end Method createSlotValue() ----------

  // -----------------------------------------
  /**
   * createSlotValue creates a slotValue annotation
   * from the slot and the value.  This method
   * also sets the phrases of the slotvalue, and
   * the tokens that are contained within the
   * slot value.
   * 
   * 
   * @param pJCas
   * @param contentHeader2
   * @param content
   * @param delimiter2
   * @param pProcess
   * 
   */
  // -----------------------------------------
  protected void createSlotValue(JCas pJCas, 
                               List<Annotation> contentHeader2, 
                               List<Token> content,
                               List<Token> delimiter2,
                               boolean pProcess ) {
    
    int      begin = ((Token) contentHeader2.get(0                     )).getBegin();
    int    slotEnd = ((Token) contentHeader2.get(contentHeader2.size() -1 )).getEnd();
    int valueBegin = -1;
    int valueEnd = slotEnd;
    if ( content != null && content.size() > 0) {
      valueBegin = ((Token) content.get(0)).getBegin();
      valueEnd = ((Token) content.get(content.size()-1)).getEnd();
    }
    int z = valueEnd - valueBegin;
    
    if ( z > 0 && z < 3000 ) {
    SlotValue     slotValue = new SlotValue(pJCas);
   
   // VUIMAUtil.setProvenance( pJCas, slotValue, this.getClass().getName() );
    slotValue.setBegin(begin);
    slotValue.setEnd( valueEnd );
    slotValue.setDisplayString( slotValue.getCoveredText() );
    slotValue.setId( "SlotValue:" + this.annotationCounter++);
    
   
    // --------------------------
    // Content Heading  (here the content heading should not make phrases to process)
    
    // --------------------------
    // If a content heading already exists, attach this to the slot value
    //      also, delete a section that is the slot + value 
    ContentHeading contentHeader = getExistingContentHeader( pJCas, contentHeader2);
    if ( contentHeader == null )
       contentHeader = createContentHeading( pJCas, contentHeader2, this.annotationCounter++);
    else {
      // delete any section that covers just the slot + value
      deleteSection( pJCas, slotValue);
    }
    
    contentHeader.setParent(slotValue);
    String contentHeaderString = contentHeader.getCoveredText();
    
    if ( contentHeaderString == null || contentHeaderString.trim().length() == 0 ) {
      GLog.println(GLog.ERROR_LEVEL,"Something is wrong here empty content heading of a slotValue " );
     // throw new RuntimeException();
    }
    slotValue.setContentHeaderString(contentHeaderString);
    slotValue.setHeading(contentHeader);
    slotValue.setProcessContent( pProcess);
    
    
   
    // ------------------------
    // Delimiter
    
    if ( delimiter2 != null ) {
      List<Annotation> delimiter3 = convertTokensToAnnotations( delimiter2);
      Delimiter delimiter = createDelimiter( pJCas, delimiter3, slotValue );
      slotValue.setDelimiter(delimiter.getCoveredText());
    }
      
    // ------------------------
    // Dependent content
    if ( content != null && !content.isEmpty()) {
      List<Annotation> contents = convertTokensToAnnotations( content);
      DependentContent contentValue = createDependentContent(pJCas, contents, slotValue );
      contentValue.setParent( slotValue);
      
      slotValue.setContentString( contentValue.getCoveredText());
      slotValue.setDependentContent( contentValue);
     
    }
   
   // if ( !this.assertInAssertionAnnotator )  ---- now done in assertion annotator properly
    //  assertQuestionAnswer( pJCas, slotValue);
    
    slotValue.addToIndexes();
    
    // GLog.println(GLog.ERROR_LEVEL,"Made a slot value");
    }    
  } // end Method createSlotValue() ----------

  

// =================================================
  /**
   * convertTokensToAnnotations
   * 
   * @param pTokens
   * @return list<Annotation> 
  */
  // =================================================
  private List<Annotation> convertTokensToAnnotations(List<Token>  pTokens) {
    List<Annotation> returnVal = null;
    if ( pTokens != null && !pTokens.isEmpty()) {
      returnVal = new ArrayList<Annotation>( pTokens.size());
      for ( Token token : pTokens )
        returnVal.add(   (Annotation ) token );
    }
    return returnVal;
  } // end Method convertTokensToAnnotations



//=================================================
 /**
  * convertAnnotationsToTokens
  * 
  * @param pTokens
  * @return list<Annotation> 
 */
 // =================================================
 public static final List<Token> convertAnnotationsToTokens(List<Annotation>  pAnnotations) {
   List<Token> returnVal = null;
   if ( pAnnotations != null && !pAnnotations.isEmpty()) {
     returnVal = new ArrayList<Token>( pAnnotations.size());
     for ( Annotation annotation : pAnnotations )
       returnVal.add(   (Token ) annotation );
   }
   return returnVal;
 } // end Method convertTokensToAnnotations

  

// =======================================================
  /**
   * deleteSection 
   * 
   * @param pJCas
   * @param pSlotValue
   */
  // =======================================================
  protected static void deleteSection(JCas pJCas, SlotValue pSlotValue) {
    
    List<Annotation> sections = UIMAUtil.getAnnotationsBySpan(pJCas, Section.typeIndexID, pSlotValue.getBegin(), pSlotValue.getEnd());
    
    if ( sections != null && sections.size() > 0 )
      for ( Annotation section: sections )
        section.removeFromIndexes(pJCas);
    
    
  } // End Method deleteSection() ======================
  



  // =======================================================
  /**
   * getExistingContentHeader retrieves a content Heading that 
   * covers this set of pSlots
   * 
   * @param pJCas
   * @param pSlotTokens
   * @return ContentHeading
   */
  // =======================================================
  public static ContentHeading getExistingContentHeader(JCas pJCas, List<Annotation> pSlotTokens) {
   
    ContentHeading contentHeading = null;
    int start = pSlotTokens.get(0).getBegin();
    int   end = pSlotTokens.get( pSlotTokens.size() -1).getEnd() ;
    List<Annotation> contentHeadings = UIMAUtil.getAnnotationsBySpan(pJCas, ContentHeading.typeIndexID, start, end);
    
    if ( contentHeadings != null && contentHeadings.size() > 0 ) {
      contentHeading = (ContentHeading) contentHeadings.get(0);
    }
    return contentHeading;
  } // End Method getExistingContentHeader() ======================
  



//-----------------------------------------
/**
* assertQuestionAnswer will mark the processMe attribute and the negation
* status attribute depending on the question answer
* 
* @deprecated
* @param questionAndAnswer
*/
//-----------------------------------------
public void assertQuestionAnswer(JCas pJCas, SlotValue questionAndAnswer) {
 
 
  ContentHeading heading = questionAndAnswer.getHeading();
  if ( heading != null ) {
    heading.setProcessMe(false);
    heading.setNegation_Status("Negated");
  
  }
 
  DependentContent dependentContent = questionAndAnswer.getDependentContent();
  String dependentContentAssertionStatus = AssertionAnnotator.getDependentContentAssertionStatus( dependentContent );
  
 
 
  
  // --------------------------------------
  // Propagate the processMe down to overlapping phrases and tokens
  if ( heading != null ) {
    heading.setNegation_Status( dependentContentAssertionStatus);
    boolean processMe = ((VAnnotation )heading).getProcessMe();
  
    List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan( pJCas,  gov.va.chir.model.Token.typeIndexID, heading.getBegin(), heading.getEnd());
    if ( tokens != null )
      for ( Annotation token : tokens ) {
        ((VAnnotation) token).setProcessMe( processMe);
        ((VAnnotation) token).setNegation_Status(dependentContentAssertionStatus);
      }
    List<Annotation> phrases = UIMAUtil.getAnnotationsBySpan( pJCas, gov.va.chir.model.Phrase.typeIndexID, heading.getBegin(), heading.getEnd() );
    if ( phrases != null )
      for ( Annotation phrase : phrases ) {
        ((VAnnotation)phrase).setProcessMe( processMe);
        ((VAnnotation)phrase).setNegation_Status(dependentContentAssertionStatus);
      }
    
  }
 
} // end Method assertQuestionAnswer() --------


  // ------------------------------------------
  /**
   * createDependantContent
   *
   *
   * @param pJCas
   * @param pValueTokens
   * @param slotValue.getEnd() 
   * @return DependentContent
   */
  // ------------------------------------------
  private  DependentContent createDependentContent(JCas pJCas, List<Annotation> pValueTokens, SlotValue slotValue ) {
    
    DependentContent  content = new DependentContent(pJCas);
    int beginEnd[] = getBeginAndEndValueOffsets( pValueTokens);
    if ( beginEnd[0] > slotValue.getEnd() ) {
      GLog.println(GLog.ERROR_LEVEL,"Shouldnt be - ");
    }
    content.setBegin( beginEnd[0]);
    content.setEnd  ( slotValue.getEnd() );
  
    content.setId("SlotValue:Fixed2Content_" + this.annotationCounter++);
    
    
    // VUIMAUtil.setProvenance( pJCas, content, this.getClass().getName() );
  
     content.addToIndexes();
    
    return content;
  }  // End Method createDependantContent() -----------------------
  



  private int[] getBeginAndEndValueOffsets(List<Annotation> pValueTokens) {
  
    int[] beginEnd = new int[2];
    beginEnd[0] = 99999;
    beginEnd[1] = 0;
    
    int first = 9999;
    int last = 0;
    
    if ( pValueTokens != null && !pValueTokens.isEmpty() ) {
      for ( Annotation annotation: pValueTokens ) {
        
        if ( ( annotation.getType().getName().contentEquals("WhitespaceToken") )) continue;
        first = annotation.getBegin();
        last  = annotation.getEnd();
        if (first < beginEnd[0]   ) beginEnd[0] = first;
        if (last  > beginEnd[1]   ) beginEnd[1] = last;
      }
    } else {
      beginEnd[0] = beginEnd[1] = 0;
    }
    if ( beginEnd[0] > beginEnd[1]) beginEnd[0] = beginEnd[1];
    
    return beginEnd;
  }  // End Method getBeginAndEndValueOffsets() ======================
  



  // ------------------------------------------
  /**
   * createContentHeading
   *
   *
   * @param pJCas
   * @param pSlotTokens
   */
  // ------------------------------------------
  private  ContentHeading createContentHeading(JCas pJCas, List<Annotation> pSlotTokens, long pCounter) {
    
    ContentHeading contentHeader = new ContentHeading(pJCas);
    // VUIMAUtil.setProvenance( pJCas, contentHeader, this.getClass().getName() );
    contentHeader.setBegin( pSlotTokens.get(0).getBegin());
    contentHeader.setEnd( pSlotTokens.get( pSlotTokens.size() -1).getEnd() );
  
    contentHeader.setId( "SlotValue:XXX:ContentHeader_" + pCounter);
   
    contentHeader.setProcessMe(true);
    String sectionName = U.extremeNormalize(contentHeader.getCoveredText());
    contentHeader.setSectionName(sectionName);
    contentHeader.addToIndexes();
    String buff = contentHeader.getCoveredText();
    
    return contentHeader;
    
    // End Method createContentHeading() -----------------------
  }



  // ------------------------------------------
  /**
   * createDelimiter
   *
   *
   * @param pJCas
   * @param pDelimiter
   */
  // ------------------------------------------
  private  SlotValueDelimiter createDelimiter(JCas pJCas, List<Annotation> pDelimiter, VAnnotation pParent  ) {
   
    SlotValueDelimiter delimiter = new SlotValueDelimiter( pJCas);
    delimiter.setBegin(pDelimiter.get(0).getBegin());
    delimiter.setEnd(pDelimiter.get(pDelimiter.size() -1).getEnd());
    
    delimiter.setId( "SlotValue:delimiter_" + this.annotationCounter++);
    // VUIMAUtil.setProvenance( pJCas, delimiter, this.getClass().getName() );
    delimiter.setParent(pParent );
    delimiter.setProcessMe(false);
    delimiter.addToIndexes();
    
    return delimiter;
    
  }  // End Method createDelimiter() -----------------------
  




  // -----------------------------------------
  /**
   * getContentFromTokens returns the string from the span of these
   * tokens. 
   * 
   * @param pTokens
   * @return String
   */
  // -----------------------------------------
  public String getContentFromTokens(JCas pJCas, List<?> pTokens) {
    String returnValue = null;
  
    
    if ( pTokens != null && pTokens.size() > 0 ) {
      
      int _begin = ((Token) pTokens.get(0)).getBegin();
      int   _end = ((Token)pTokens.get(pTokens.size() -1)).getEnd();
      returnValue = pJCas.getDocumentText().substring(_begin, _end);
      returnValue = returnValue.replaceAll("  ", " ");  // <-------------- important normalize to one space between tokens (done for the index)
      //GLog.println(GLog.ERROR_LEVEL,"LeftOrRightSide:" + returnValue);
    }
  
 
    return returnValue;
  } // end Method getContentFromTokens() -----

  // -----------------------------------------
  /**
   * createSectionHeading creates a section heading (contentUtterance)
   * for this set of tokens.
   * 
   * @param pJCas
   * @param pTokens
   * @param pDelimiter 
   * 
   */
  // -----------------------------------------
  private void createSectionHeading(JCas pJCas, List<Token> pTokens, List<Token> pDelimiter) {
    
    ContentHeading contentHeading = new ContentHeading(pJCas);
    
    int begin = pTokens.get(0).getBegin();
    
    
    int  zEnd = pTokens.get(pTokens.size() -1).getEnd();
    //if ( pDelimiter != null )
    //  zEnd = pDelimiter.get( pDelimiter.size() -1).getEnd();
    // VUIMAUtil.setProvenance( pJCas, contentHeading, this.getClass().getName() );
    contentHeading.setBegin(begin);
    contentHeading.setEnd( zEnd );
    
    contentHeading.setId( "SlotValue:XX:ContentHeading_" + this.annotationCounter++);
   
    contentHeading.setProcessMe(true);
    String sectionName = U.extremeNormalize(contentHeading.getCoveredText());
    contentHeading.setSectionName(sectionName);
    contentHeading.addToIndexes();
    

    if ( pDelimiter  != null ) {
     SectionHeadingDelimiter  delemeter = new SectionHeadingDelimiter( pJCas);
     delemeter.setBegin( pDelimiter.get(0).getBegin());
     delemeter.setEnd(   pDelimiter.get(pDelimiter.size() -1 ).getEnd());
     delemeter.setId("SlotValue:createSectionHeading");
     delemeter.setParent( (Annotation) contentHeading);
     delemeter.setProcessMe(false);
     delemeter.addToIndexes();
    
     
    }
    
    
  } // end Method createSectionHeading() -----
  
//------------------------------------------
  /**
   * containsListDelimiter returns true if one of the tokens in the set
   * is labeled with listDelimiter
   *
   *
   * @param sentenceTokens
   * @return String (null if not found)
   */
  // ------------------------------------------
  private boolean containsListDelimiter(List<Token> sentenceTokens) {
   
    boolean returnVal = false;
    
    if ( sentenceTokens != null && sentenceTokens.size() > 0 )
      for ( Token token :  sentenceTokens ) {
         if ( token.getListDelimiter()  ) {
           
          String t = token.getCoveredText();
          if ( !Character.isLetter(t.trim().charAt(0))) {
            returnVal = true;
            break;
          }
        }
      }
    return returnVal;
  } // End Method containsListDelimiter() -----------------------
  
  
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
   * initialize 
   *
   * 
   * 
   **/
  // ----------------------------------
  public void initialize() throws ResourceInitializationException {
    
    String args[] = null;
    initialize( args );
    
  } // end Method initialize() -------
    
  
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
    
     this.termProcessing = Boolean.parseBoolean( U.getOption(pArgs, "--termProcessing=" , "false"));
    
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
