// =================================================
/**
 * SentenceAnnotator segments a document into
 * sentence annotations.  This method takes tokens and
 * whitespace tokens as input, and returns sentence annotations.
 * 
 * This class looks for sentence delimiters that were identified
 * during tokenization to make the decision.
 *
 *
 * @author  Guy Divita 
 * @created Feb 16, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.Delimiter;
import gov.va.chir.model.Line;
import gov.va.chir.model.ListDelimiter;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.Token;
import gov.va.chir.model.Utterance;
import gov.va.chir.model.WhitespaceToken;
import gov.va.chir.model.WordToken;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ListElement;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.StructuredList;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class SentenceAnnotator extends JCasAnnotator_ImplBase {
  
  
@SuppressWarnings("unchecked")
  @Override
  // -----------------------------------------
  /**
   * process takes tokens and whitespace tokens as input and returns
   * sentences
   * 
   * 
   * 
   * @param pJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
    
    try {
    this.performanceMeter.startCounter();
    this.sentenceNumber = 0;
    
    
    
    
    // ---------------------------------------------------------------------
    // Mark the tokens that have already been included in other non-sentence
    // utterances like slotValue or list elements
    // ---------------------------------------------------------------------
    Token[] tokens = getTokens(pJCas);
    StructuredList structuredList = null;
    ListElement    listElement = null;
    String   fullText = pJCas.getDocumentText();
    HashSet<StructuredList> structuredListSet = new HashSet<StructuredList>();
    int lastNewLine = -1;
    int firstWord = -1;
    boolean newlineSeen = false;
    int indentation = 0;
    boolean listJustStarted = false;
    int newlineCtr = 0;
    if (tokens != null) {
      // int takenUtterances[][] = markUtteranceTokens(pJCas);
      markUtteranceTokens( pJCas );
      markDelimeterTokens( pJCas );
    //   markListDelimeterTokens( tokens );  <---- bad idea here
      
      ArrayList<Token> sentenceTokens = new ArrayList<Token>();
      for (int i = 0; i < tokens.length; i++) {
        // GLog.println(GLog.ERROR_LEVEL,"               Looking at token |" + tokens[i].getCoveredText() + "|");
        if ( tokens[i].getCoveredText().indexOf('\n') > -1 ) {
          lastNewLine = i;
          // GLog.println(GLog.ERROR_LEVEL,"Newline hit i =" + i);
          newlineSeen = true;
          newlineCtr++;
          listJustStarted = false;
        } else if (tokens[i].getTypeIndexID() != WhitespaceToken.typeIndexID) {
          newlineCtr=0;
          if ( newlineSeen ) {
            firstWord = i;
            newlineSeen = false;
            indentation = tokens[firstWord].getBegin() - tokens[lastNewLine].getBegin()  ;
             // GLog.println(GLog.ERROR_LEVEL,"First word = " + tokens[firstWord].getCoveredText() + "|indentation = " + indentation + "|" +  firstWord + "|" + lastNewLine);
          }
         
        }
        
        
        if ( !tokens[i].getMarked() ) {
          if (tokens[i].getTypeIndexID() == WhitespaceToken.typeIndexID) {
            // sentenceTokens.add(tokens[i]);
            if (((WhitespaceToken) tokens[i]).getEmptyLine()) {
              // GLog.println(GLog.ERROR_LEVEL,"Making sentence because of empty lines hit");
              createSentence(pJCas, sentenceTokens);
              structuredList = null;
              sentenceTokens = new ArrayList<Token>();
            } // end if empty line found
          } else {
            // ----------------
            // Not whitespace
            // ----------------
            if (tokens[i].getTypeIndexID() != WhitespaceToken.typeIndexID  ) // && ( !tokens[i].getListDelimiter() && !tokens[i].getSentenceBreak() ))
              sentenceTokens.add(tokens[i]);
            
            if  (tokens[i].getSentenceBreak() ) {
              if ( ! this.keepEndPunctuation )
                sentenceTokens.remove( tokens[i] );
              Sentence sentence = createSentence(pJCas, sentenceTokens);
              String delimiter = null;
              if ( (delimiter = containsListDelimiter( sentenceTokens )) != null ) 
                 sentence.setProcessMe(false);
                
              if ( tokens[i].getListDelimiter()) 
                // -----------------------------------
                // trim away listDelimiters that are not.  This will help downstream 
                // annotators.
                //   If there is only one ListDelimiter and it starts with f.  it's not
                //   a list delimiter. 
                // --------------------------------
                if ( isAValidListDelimeter( tokens[i], structuredList)) {
                // --------------------------------
                 // Figure out if you need to start a new
                 // list here - or use an existing list
                 //  ----> what about lists of lists?
               
               
                   
                 structuredList = StructuredList.existingListOrNewList( structuredList, delimiter );
                 structuredList.setInitialIndentation( getIndentationFromToken( fullText, delimiter, tokens[i]));
                 structuredListSet.add(structuredList);
                 listJustStarted = true;
                
                 listElement = new ListElement( );
                 listElement.setListDelimiter( delimiter);
                 
                 structuredList.addListElement( listElement);
                 
                 
                 
                 // < the next sentence is the listElement content.
                 
                 // ---------------------------------------
                 // If accumulating the tokens of a list Element
                 //   if you come across a new line, followed by white space
                 //   followed by a word that starts with a capital letter,
                 //      you've gotten into the answer of a  question/answer or at the least
                 //      a new sentence that was newline delimited
              }  // end if this is a list Delimiter
              
                
              
              
              sentenceTokens = new ArrayList<Token>();
            } // end if a new sentence should be made.
            
            
            
          } // end not whitespace
        
          // -------------------------------
          // pay attention to indentation
          //   - if we are looking at tokens after a new line
          //       -if the indentation is less than the delimiter, 
          //        it's no longer a list element 
          
          
          if ( structuredList != null && structuredList.getListElements().size() > 0 ) {
            if (listElement != null && tokens[i].getTypeIndexID() != WhitespaceToken.typeIndexID) {
              listElement.addToken( tokens[i] );
              listElement.setListContent( getListContent( pJCas,(List<Token> )listElement.getTokens()) ); 
            }
            // GLog.println(GLog.ERROR_LEVEL,"StructuredListIndentation = " + structuredList.getInitialIndentation() + "| current indentation|" + indentation + "|"  + "Token|" + tokens[i].getCoveredText() );
            if ( listElement != null  && listElement.getTokens() != null && listElement.getTokens().size() > 1 && !listJustStarted &&
                  indentation < structuredList.getInitialIndentation() && 
                  (tokens[i].getInitialCapitalization() || tokens[i].getAllCapitalization() || tokens[i].getPunctuationOnly() || newlineCtr>0) &&
                  sentenceTokens.size() > 0 ) {
         
             
                //  GLog.println(GLog.ERROR_LEVEL,"Making sentence because a line with undented Initial capitalized word was hit");
                sentenceTokens.remove( sentenceTokens.size() -1);
                createSentence( pJCas, sentenceTokens );
                sentenceTokens = new ArrayList<Token>();  
                sentenceTokens.add( tokens[i]);
                
                listElement.getTokens().remove(listElement.getTokens().size() -1);
                listElement.setListContent( getListContent( pJCas,(List<Token> )listElement.getTokens()) ); 
                structuredList = null;
                
                // -----------------------------------------------
                // Catch new sentences within lists 
                
            } else if ( listElement != null                && 
                        listElement.getTokens() != null    && 
                        listElement.getTokens().size() > 1 && 
                        !listJustStarted                   &&
                        !tokens[i].getCoveredText().contains("\n") && 
                        ((  tokens[i].getInitialCapitalization()||
                            tokens[i].getAllCapitalization()    || 
                            tokens[i].getPunctuationOnly()  )    &&
                            newlineCtr > 0 )               &&
                        sentenceTokens.size() > 1 ) {
              
              // GLog.println(GLog.ERROR_LEVEL,"Making sentence because a line with indented Initial capitalized word was hit");
              // GLog.println(GLog.ERROR_LEVEL,"|" + tokens[i].getCoveredText() + "|" + tokens[i].getInitialCapitalization() + "|" + tokens[i].getAllCapitalization() + "|" + tokens[i].getPunctuationOnly());
              if ( sentenceTokens.size() == 1) {
                 GLog.println(GLog.ERROR_LEVEL,"What the ? ====> |" + sentenceTokens.get(0).getCoveredText() + "|");
                
              }
              sentenceTokens.remove( sentenceTokens.size() -1);
              createSentence( pJCas, sentenceTokens );
              sentenceTokens = new ArrayList<Token>();  
              sentenceTokens.add( tokens[i]);  
    
            }  // end if there is a list open and the indentation is right
          } // end if there is an open list
        
        } else { // end if this token is not already part of another utterance.
          structuredList = null;
          if ((sentenceTokens != null) && (sentenceTokens.size() > 0)) {
            // GLog.println(GLog.ERROR_LEVEL,"Making sentence because too many tokens have been put into one sentence");
            createSentence(pJCas, sentenceTokens);
            sentenceTokens = new ArrayList<Token>();
          }

        }
       
      } // end Loop through the annotations

      if ((sentenceTokens != null) && (sentenceTokens.size() > 0))
        createSentence(pJCas, sentenceTokens);

    } // end if there are any tokens
    
    // -----------------------------------
    // Make annotations from the structured lists
    // -----------------------------------
    createListAnnotations( pJCas, structuredListSet );
    
    // -----------------------------------
    // Remove sentences that are also list delimiters
    // -----------------------------------
    removeListDelimeterSentences(pJCas);
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Issue with " + this.getClass().getName() + " " + e.toString());
      // throw new AnalysisEngineProcessException();
    }
  } // end Method process() -------------------
  
 

  // =================================================
/**
 * isFirstListDelimeter - since this should be the first
 * of list delimiters, they should be 1.  or a. 
 * @param structuredList 
 * 
 * @param pToken
 * @param pStructuredList
 * @return boolean
*/
// =================================================
private final boolean isAValidListDelimeter(Token pToken, StructuredList pStructuredList) {
  boolean returnVal = false;
  
  if ( pToken != null ) {
    String buff = pToken.getCoveredText() ;
    if ( buff != null && !buff.isEmpty()) {
      char firstChar = buff.trim().charAt(0);
        if ( pStructuredList == null || pStructuredList.getListElements().isEmpty() ) 
            if (  firstChar == '0' || firstChar == '1' || firstChar == 'a' || firstChar == 'A')
              returnVal = true;
            else 
              returnVal = false;
        else
          returnVal = true;
    }
  }
  return returnVal;
} // end Method isFirstListDelimiter() ------------



  // =======================================================
/**
 * markUtteranceTokens marks tokens that are already in
 * another utterance.  
 * 
 * @param pJCas
 * @param tokens
 * @throws Exception 
 */
// =======================================================
private void markUtteranceTokens(JCas pJCas)  {
  
 try {
  List<Annotation> utterances = UIMAUtil.getAnnotations(pJCas, Utterance.class, true );
  
  if ( utterances != null ) {
    for ( Annotation utterance: utterances ) {
      
      List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas, Token.typeIndexID, utterance.getBegin(), utterance.getEnd());
      
      if ( tokens != null ) {
        for ( Annotation token: tokens ) 
          ((Token)token).setMarked(true);
      }
    } // end loop through utterances
      
  } // if there are any utterances
 } catch ( Exception e) {
   e.printStackTrace();
   String msg = "Issue with trying to mark tokens from utterances " + e.getMessage();
   GLog.println(GLog.ERROR_LEVEL,msg);
  
 }
  
}  // End Method markUtteranceTokens() ======================

// =======================================================
/**
* markDelimeterTokens marks tokens that are already in
* another delimeter.  
* 
* @param pJCas
* @param tokens
* @throws Exception 
*/
//=======================================================
private void markDelimeterTokens(JCas pJCas)  {
 
try {
 List<Annotation> utterances = UIMAUtil.getAnnotations(pJCas, Delimiter.class, true );
 
 if ( utterances != null ) {
   for ( Annotation utterance: utterances ) {
     
     List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas, Token.typeIndexID, utterance.getBegin(), utterance.getEnd());
     
     if ( tokens != null ) {
       for ( Annotation token: tokens ) 
         ((Token)token).setMarked(true);
     }
   } // end loop through utterances
     
 } // if there are any utterances
} catch ( Exception e) {
  e.printStackTrace();
  String msg = "Issue with trying to mark tokens from delimeter " + e.getMessage();
  GLog.println(GLog.ERROR_LEVEL,msg);
 
}
 
}  // End Method markDelimeterTokens() ======================



  // ------------------------------------------
  /**
   * getIndentationFromToken
   *
   *
   * @param fullText
   * @param delimiter
   * @param pToken
   * @return int  the number of spaces 
   */
  // ------------------------------------------
  private int getIndentationFromToken(String fullText, String delimiter, Token pToken) {
    int indentation = 0;
    
 
    // GLog.println(GLog.ERROR_LEVEL,"Delimiter="  + delimiter + "| currentToken = " + pToken.getCoveredText());
    char c = fullText.charAt(pToken.getBegin());
    int currentPosition = pToken.getBegin();
    while ( c != '\n'  && currentPosition > 0) {
    
      currentPosition--;
      c = fullText.charAt(currentPosition);
    }
    if ( c == '\n' || c == '\r') {
      indentation = pToken.getBegin() - currentPosition ; //  + delimiter.length();
    }
    // GLog.println(GLog.ERROR_LEVEL,"The indentation = " + indentation);
    
    return indentation;
    
  }  // End Method getIndentationFrom() -----------------------
  

  // ------------------------------------------
  /**
   * containsListDelimiter returns true if one of the tokens in the set
   * is labeled with listDelimiter
   *
   *
   * @param sentenceTokens
   * @return String (null if not found)
   */
  // ------------------------------------------
  private String containsListDelimiter(ArrayList<Token> sentenceTokens) {
   
    String returnVal = null;
    
    if ( sentenceTokens != null && sentenceTokens.size() > 0 )
      for ( Token token :  sentenceTokens ) 
        if ( token.getListDelimiter()  ) {
          returnVal = token.getCoveredText();
          break;
        }
    
    return returnVal;
  } // End Method containsListDelimiter() -----------------------
  

  // end Method process() ------------------
  
  

  // -----------------------------------------
  /**
   * getTokens extracts all token instances that are
   * in the cas into an array for quick retrieval.
   * 
   * 
   * @param pAJCas
   * @return Token[]
   */
  // -----------------------------------------
  private Token[] getTokens(JCas pJCas) {  
  
    Token[] tokens = null;
  
    List<Annotation> tokenIndex = UIMAUtil.getAnnotations(pJCas, Token.typeIndexID);

    
    if ( tokenIndex != null && tokenIndex.size() > 0 ) {
      // ------------------------
      // weed out the null tokens
      ArrayList<Annotation> newTokens = new ArrayList<Annotation>( tokenIndex.size() );
  
      for ( Annotation token : tokenIndex ) {
        if ( token != null )
          newTokens.add( token);
      }
    
      // -----------------------
      // sort by offset
      UIMAUtil.sortByOffset(newTokens);
      
      // -----------------------
      // Convert to an array
      tokens = newTokens.toArray( new Token[newTokens.size()]);
    
    }
    
    return tokens;
  } // end Method getTokens() ----------------

  // ------------------------------------------
  /**
   * getListContent figures out the string of the listElement
   * from the tokens passed in that cover the listElement
   * 
   * This could have been done in the ListElement method
   * but I didn't want to explicitly cast the tokens in the ListElement
   * as UIMA tokens.  ListElements are in the utils.general package
   * which should not include any UIMA classes in it.  The token
   * container is therefore a list of Object rather than
   * a list of Token.  As a consequence, ListElement doesn't
   * have the internal ability to figure out the
   * string from the set of Object Tokens.  
   *
   *
   * @param pJCas
   * @param tokens
   * @return
   */
  // ------------------------------------------
  private String getListContent(JCas pJCas, List<Token> tokens) {
  
    String returnVal = null;
    
    if ( tokens != null  && tokens.size() > 0) {
      int begin = tokens.get(0).getBegin();
      int  end  = tokens.get(tokens.size() -1 ).getEnd();
      returnVal = UIMAUtil.getText( pJCas, begin, end);
      
    }
    
    return returnVal;
    
  }  // End Method getListContent() -----------------------
  
  

  // =======================================================
  /**
   * removeListDelimeterSentences removes sentences that exactly
   * overlap list delimeter markers.
   * 
   * @param pJCas
   */
  // =======================================================
  private void removeListDelimeterSentences(JCas pJCas) {
    
    List<Annotation> delimeters = UIMAUtil.getAnnotations( pJCas, ListDelimiter.typeIndexID  );
    
    if ( delimeters != null ) {
      for ( Annotation delimeter : delimeters ) {
        
        List<Annotation> sentences = UIMAUtil.getAnnotationsBySpan(pJCas, Sentence.typeIndexID, delimeter.getBegin(), delimeter.getEnd());
        
        if ( sentences != null ) {
          for ( Annotation sentence: sentences )
          sentence.removeFromIndexes();
        }
        
      } // loop through the delimeters
    }
  
  } // End Method removeListDelimeterSentences() ======================

  // -----------------------------------------
  /**
   * createSentence creates a Sentence annotation that includes
   * the (word) tokens of the sentence as a FSArray.  The whitespace
   * tokens are not needed, nor are the trailing punctuation.
   * 
   * Note 2019-12-30 - made an option to keep the trailing period to make it compatible with ctakes chunker
   
   * If there are no tokens, no sentence is created.
   * 
   * @param pSentenceTokens
   * @return Sentence
   */
  // -----------------------------------------
  private Sentence createSentence(JCas pCAS, ArrayList<Token> pSentenceTokens) {
   
    Sentence sentenceAnnotation = null;

    if (pSentenceTokens != null && pSentenceTokens.size() > 0) {
      StringBuffer buff = new StringBuffer();
      for (Token token : pSentenceTokens)
        buff.append(token.getCoveredText());

      int beg = pSentenceTokens.get(0).getBegin();
      int endd = pSentenceTokens.get(pSentenceTokens.size() - 1).getEnd();
      int zz = endd - beg;
      if (zz > 4000) {

        GLog.println(GLog.ERROR_LEVEL,"Super long sentence " +  beg + "|" + endd + "|" + zz + "|>" + buff.toString() + "<|");
        // throw new RuntimeException( "issue with new sentence");
      } else if ((pSentenceTokens != null) && (pSentenceTokens.size() > 0) && (buff.toString().trim().length() > 0)) {
        sentenceAnnotation = new Sentence(pCAS);
        sentenceAnnotation.setId("Sentnece_" + this.annotationCounter++);

       // VUIMAUtil.setProvenance(pCAS, sentenceAnnotation, this.getClass().getName());
        sentenceAnnotation.setBegin(pSentenceTokens.get(0).getBegin());
        sentenceAnnotation.setEnd(pSentenceTokens.get(pSentenceTokens.size() - 1).getEnd());
        sentenceAnnotation.setDisplayString(sentenceAnnotation.getCoveredText());
        sentenceAnnotation.setProcessMe(true);
        sentenceAnnotation.setSentenceNumber(this.sentenceNumber++);

        sentenceAnnotation.addToIndexes();

        // GLog.println(GLog.ERROR_LEVEL,"Made a sentence from |" + sentenceAnnotation.getCoveredText() + "|");

        // ---------------------------------
        // Set the (word) tokens of the sentence for later retrieval
        // ---------------------------------

        ArrayList<Token> tokens = new ArrayList<Token>(pSentenceTokens.size());
        for (Iterator<Token> i = pSentenceTokens.iterator(); i.hasNext();) {
          Token token = i.next();
          if (token.getTypeIndexID() != WhitespaceToken.typeIndexID) if (i.hasNext()) tokens.add(token);
          else if ( this.keepEndPunctuation || !this.keepEndPunctuation && !token.getSentenceBreak()) 
            tokens.add(token);

        } // end loop through all tokens

        FSArray z = UIMAUtil.list2FsArray(pCAS, tokens);

        sentenceAnnotation.setUtteranceTokens(z);
        
        String sent = sentenceAnnotation.getCoveredText();
        

      }
    }
    
    return sentenceAnnotation;
  } // end Method createSentence() ----------------------

  // ------------------------------------------
  /**
   * createListAnnotations iterates through the set of lists and makes
   * list annotations
   *
   *
   * @param pJCas
   * @param structuredListSet
   */
  // ------------------------------------------
  private void createListAnnotations(JCas pJCas, HashSet<StructuredList> structuredListSet) {
   
    if ( structuredListSet != null && structuredListSet.size() > 0 ) 
      for ( StructuredList structuredList : structuredListSet )
        createListAnnotation( pJCas, structuredList); 
    
    
  }  // End Method createListAnnotations() -----------------------
  

  // ------------------------------------------
  /**
   * createListAnnotation creates a List annotation along with 
   * listElement annotations
   *
   *
   * @param pJCas
   * @param structuredList
   */
  // ------------------------------------------
  private gov.va.chir.model.List createListAnnotation(JCas pJCas, StructuredList structuredList) {
  
    gov.va.chir.model.List listAnnotation = new gov.va.chir.model.List(pJCas);
    
    List<ListElement> listElements = structuredList.getListElements();
    List<?>tokens = listElements.get(0).getTokens();
    FSArray listElementz  = null;
    if ( tokens != null && tokens.size() > 0 ) {
      List<?>lastTokens = listElements.get(listElements.size() -1).getTokens();
      Token firstToken = (Token)tokens.get(0);
      Token  lastToken = (Token)lastTokens.get(lastTokens.size() -1);
    
    
      listAnnotation.setBegin( firstToken.getBegin() );
      listAnnotation.setEnd(   lastToken.getEnd());
      
      //VUIMAUtil.setProvenance( pJCas, listAnnotation, this.getClass().getName() );
    
      listAnnotation.addToIndexes();
    
      // ------------------------------------
      // Create Annotations for each listElement
      // 
      ArrayList<gov.va.chir.model.ListElement> listElementss = new ArrayList<gov.va.chir.model.ListElement>( listElements.size());
      for ( ListElement listElement : listElements ) {
      
        gov.va.chir.model.ListElement anElement = null;
        try { anElement = createListElement( pJCas, listElement); } catch (Exception e)  {}; 
        if ( anElement != null ) {
          anElement.setParent( listAnnotation);
          listElementss.add( anElement);
        }
      }
      if ( listElements != null && listElements.size() > 0 ) {
        listElementz = UIMAUtil.list2FsArray(pJCas, listElementss);
        listAnnotation.setListElements(listElementz);
      }
    }
    
    
    return listAnnotation;
    
  }  // End Method createListAnnotation() -----------------------
  

  // ------------------------------------------
  /**
   * createListElement
   *
   *
   * @param pJCas
   * @param listElement
   * @return
   * @throws Exception 
   */
  // ------------------------------------------
  private gov.va.chir.model.ListElement createListElement(JCas pJCas, ListElement listElement) throws Exception {
  
    
    gov.va.chir.model.ListElement statement = null;

    List<?> tokens = listElement.getTokens();
    if (tokens != null && tokens.size() > 1) {
      statement = new gov.va.chir.model.ListElement(pJCas);

      Token firstToken = (Token) tokens.get(0);

      Token secondToken = null;
      try {
        secondToken = (Token) tokens.get(1);
      } catch (Exception e) {
        e.printStackTrace();
        String msg = "Issue in trying to create a list element " + e.toString();
        throw new Exception(msg);
      }

      Token lastToken = (Token) tokens.get(tokens.size() - 1);
      statement.setBegin(firstToken.getBegin());
      statement.setEnd(lastToken.getEnd());
      //VUIMAUtil.setProvenance(pJCas, statement, this.getClass().getName());

      List<Annotation> sentences = UIMAUtil.getAnnotationsBySpan(pJCas, Sentence.typeIndexID, secondToken.getBegin(),
          lastToken.getEnd());

      ListDelimiter d = createListDelimiter(pJCas, listElement.getListDelimiter(), firstToken);
      statement.setDelimiter(d);

      tokens.remove(0);
      FSArray tokenz = UIMAUtil.list2FsArray(pJCas, tokens);
      statement.setTokens(tokenz);

      FSArray sentencez = UIMAUtil.list2FsArray(pJCas, sentences);
      statement.setContent(sentencez);

      // figure out if this element spans more than one line
      statement.setSpansLines(false);
      List<Annotation> lines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Line.typeIndexID, statement.getBegin(),
          statement.getEnd());
      if (lines != null && lines.size() > 1) statement.setSpansLines(true);

      statement.addToIndexes();
    }
    
    return statement;
    
  }   // End Method createListElement() -----------------------
  

  // ------------------------------------------
  /**
   * createListDelimiter
   *
   *
   * @param pJCas
   * @param listDelimiter
   * @param pToken
   * @return
   */
  // ------------------------------------------
  private ListDelimiter createListDelimiter(JCas pJCas, String listDelimiter, Token pToken) {
  
    ListDelimiter delimiter = new ListDelimiter( pJCas);
    delimiter.setBegin( pToken.getBegin());
    delimiter.setEnd(   pToken.getEnd());
    delimiter.setId("Sentence_list_delimeter_" );
    delimiter.addToIndexes();
    
      
    return delimiter;
    
  }  // End Method createDelimiter() -----------------------
  
  
  
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
   * initialize loads in the resources needed for tokenization. Currently, this involves
   * a list of acronyms that might show up at the end of a sentence.
   *
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
    String[] args = null;
    try {
      args                 = (String[]) aContext.getConfigParameterValue("args");  
      
      initialize ( args); 

    } catch (Exception e ) {
      GLog.println(GLog.ERROR_LEVEL,"Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
    
    
    if (aContext != null )
      super.initialize(aContext);
    // this.logger = FrameworkLogging.addLogger(this.getClass().getName());
  
  } // end Method initialize() --------------
  
  //----------------------------------
  /**
   * initialize 
   *
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String pArgs[]) throws ResourceInitializationException {
    
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    this.keepEndPunctuation = Boolean.parseBoolean(U.getOption(pArgs,  "--keepSentenceEndingPunctuation=", "false"));
    

} // end Method initialize() --------------

 
// ---------------------------------------
// Global Variables
// ---------------------------------------
  private long                annotationCounter = 0;
  private int                    sentenceNumber = 0;
  ProfilePerformanceMeter      performanceMeter = null;
  private boolean            keepEndPunctuation = false;

  
  
} // end Class SentenceTokenizerSimple() ----
