// =================================================
/**
 * QuestionTemplateAnnotation labels lines with questions
 * as question.
 *   These include labels as [X] Yes  [] No
 *                           [YES]   
 *                   See examples questions
 *
 * This annotator assumes that lines have been already annotated.
 * @author  Guy Divita 
 * @created Jun 22, 2011
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

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Delimiter;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Line;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.Token;
import gov.va.chir.model.VAnnotation;
import gov.va.chir.model.WhitespaceToken;
import gov.va.chir.model.WordToken;
import gov.va.vinci.model.AssertionEvidence;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class QuestionAnswerAnnotator extends JCasAnnotator_ImplBase {


  private int conentHeadingCtr;
  // -----------------------------------------
  /**
   * process retrieves lines of the document, labels those that are questions
   * as QuestionAndAnswer elements.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
  
    try {
    this.performanceMeter.startCounter();
    unMarkLines( pJCas);
  
    multiLineQuestionAnswer( pJCas);
    
    singleLineQuestionAnswer( pJCas);
    
    QuestionWithNoAnswer_LookForAnswerInNextLine(pJCas);
  
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
     // throw new AnalysisEngineProcessException();
    }
    
    this.performanceMeter.stopCounter();

    
    
  } // end Method process --------------------
  
  

  // =======================================================
  /**
   * QuestionWithNoAnswer_LookForAnswerInNextLine 
   * 
   * @param pJCas
   */
  // =======================================================
  private void QuestionWithNoAnswer_LookForAnswerInNextLine(JCas pJCas) {
    
    List<Annotation> questionsAndAnswers = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID);
    
    if ( questionsAndAnswers != null && !questionsAndAnswers.isEmpty()) {
      // ------------------------
      for ( Annotation questionAndAnswer : questionsAndAnswers ){
        ContentHeading question = ((SlotValue)questionAndAnswer).getHeading();
        DependentContent answer = ((SlotValue)questionAndAnswer).getDependentContent();
        if ( answer == null ) {
          Annotation nextLine = getLineAfterQuestion(pJCas, question);
          if ( nextLine != null ) {
            List<Annotation> evidences = hasAsssertionEvidence(pJCas, nextLine) ;
            createAnswer( pJCas, questionAndAnswer, nextLine, evidences);
          }
        }
        
      }
    }
    
  } // End Method QuestionWithNoAnswer_LookForAnswerInNextLine() ======================
  





  // =======================================================
  /**
   * hasAsssertionEvidence 
   * 
   * @param pJCas
   * @param nextLine
   * @return List<Annotation>
   */
  // =======================================================
  private List<Annotation> hasAsssertionEvidence(JCas pJCas, Annotation nextLine) {
  
    List<Annotation> evidences = null;
    if ( nextLine != null )
       evidences = UIMAUtil.getAnnotationsBySpan(pJCas, AssertionEvidence.typeIndexID, nextLine.getBegin(), nextLine.getEnd());
    
     return evidences;
  } // End Method hasAsssertionEvidence() ======================
  



  // =======================================================
  /**
   * getLineAfterQuestion 
   * 
   * @param pJCas
   * @param question
   * @return
   */
  // =======================================================
  private Annotation getLineAfterQuestion(JCas pJCas, ContentHeading question) {
   
    Annotation returnVal = null;
    String docTexts = pJCas.getDocumentText();
    
    if ( !docTexts.isEmpty() ) {
      int lastCharOfQuestion = question.getEnd();
      char docText[] = docTexts.toCharArray();
      boolean seen = false;
      int nextLineEnding = -1;
      for ( int i = lastCharOfQuestion; i < docText.length; i++ ) {
        if ( docText[i] == '\n') {
          if ( seen == false )
            seen = true;
          else {
            nextLineEnding = i;
            break;
          }
        }
      }
      if ( nextLineEnding != -1 ) {
        List<Annotation>someLines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Line.typeIndexID, nextLineEnding -1, nextLineEnding);
        if ( someLines != null && !someLines.isEmpty() )
          returnVal = someLines.get(0);
      
      }
      
    }
    
    return returnVal;
  } // End Method getLineAfterQuestion() ======================
  


  // =======================================================
  /**
   * createAnswer creates an answer (dependentContent) from the line
   * and assigns an assertion based on the assertionEvidence
   * 
   * @param pJCas
   * @param questionAndAnswer
   * @param pLine
   * @param evidences
   */
  // =======================================================
  private void createAnswer(JCas pJCas, Annotation questionAndAnswer, Annotation pLine, List<Annotation> evidences) {
  
    String assertion = "Asserted";
    boolean conditional = false;
    String aboutThePatient = "Patient";
    if ( pLine != null ) {
      if ( evidences != null && !evidences.isEmpty() ) {
        for ( Annotation evidence : evidences  ) {
          if ( evidence.getClass().getSimpleName().contains("Negation")) {
            assertion = "Negated";
          }
          if ( evidence.getClass().getSimpleName().contains("Conditional")) {
            conditional = true;
          }
          if ( evidence.getClass().getSimpleName().contains( "SubjectIsOtherEvidence")) {
            aboutThePatient = "Other";
          }
        } // end loop thru evidences
        DependentContent answer = new DependentContent(pJCas );
        answer.setBegin(pLine.getBegin());
        answer.setEnd(pLine.getEnd());
        answer.setNegation_Status(assertion);
        answer.setConditional(conditional);
        answer.setSubject(aboutThePatient);
        answer.setParent((VAnnotation)questionAndAnswer);
        answer.addToIndexes();
        ((SlotValue)questionAndAnswer).setDependentContent(answer);
        
      }
    }
    
  } // End Method createAnswer() ======================
  


  // ------------------------------------------
  /**
   * MultiLineQuestionAnswer looks for the pattern
   *  Wh question in sentence 
   *     next sentence has the answer
   *
   *  The pattern could include a list marker to begin with
   *
   * @param pJCas
   */
  // ------------------------------------------
  private void multiLineQuestionAnswer(JCas pJCas) {
   
    List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID);
    ContentHeading    question = null;
    DependentContent    answer = null;
    Delimiter        delemeter = null;
    int[]          lineOffsets = null;
    
    lineOffsets = UIMAUtil.getLineOffsets(pJCas);
    
    if ( sentences != null ) {
    
      markExistingSlotFillers(pJCas );
      markExistingContentHeading( pJCas );
     
     
      for ( int i = 0; i < sentences.size(); i++ ) { 
      
        Annotation        sentence = sentences.get(i);
        Annotation    nextSentence = null;
        if ( i + 1 < sentences.size())
          nextSentence = sentences.get(i+1); 
        else
          nextSentence = null;
       
        if ( !((VAnnotation)sentence).getMarked() &&  isQuestion( pJCas, (Sentence) sentence  )) {
          question = createContentHeading( pJCas, sentence.getBegin(), sentence.getEnd(), false, "multiLineQuetionAnser_");
          delemeter = findDelimeter(pJCas, sentence );
          
          int delimiterLine    = 0;
          int nextSentenceLine = lineOffsets.length -1;
          if ( delemeter != null )
            delimiterLine= UIMAUtil.getLineNumber( delemeter,    lineOffsets);
          else {
            delimiterLine= UIMAUtil.getLineNumber( sentence,     lineOffsets);
          }
          if ( nextSentence != null )
            nextSentenceLine = UIMAUtil.getLineNumber( nextSentence, lineOffsets); 
          else 
            nextSentenceLine = 9999999;
          // ----------------------------
          // find the answer in the next sentence/line  (if the next sentence is less than 2 newlines away from the delimiter
         // answer = getAnswer( nextSentence, line, nextLine);
          
          if ( nextSentenceLine < delimiterLine + 2  && nextSentence!= null ) {
          
            // -----------------------------------------
            // In need of some repair here - the sentence annotator is not good enough to recognize
            // the pattern xxxxx? No \n XXXX.
            int locationOfNewline = -1;
            if ( (locationOfNewline = nextSentenceNeedsRepair( pJCas, nextSentence, delemeter)) == -1 ) {
            
              answer = createDependentContent(pJCas, nextSentence.getBegin(), nextSentence.getEnd(), false);
              ((VAnnotation)nextSentence).setMarked(true);
              Annotation zone = new Annotation(pJCas);
              zone.setBegin( sentence.getBegin());
              zone.setEnd(  nextSentence.getEnd());
          
              createQuestionAndAnswer( pJCas, zone, question, answer, delemeter);
              
            } else {
              // ----------------------------------------------------
              // make the dependent content from the rest of the line
              nextSentence.setBegin(locationOfNewline +1);
              Annotation zone = new Annotation(pJCas);
              if ( delemeter != null ) {
                zone.setBegin( delemeter.getEnd() + 1 );
                zone.setEnd( nextSentence.getBegin() -1);
              }
              answer = createDependentContent(pJCas, zone.getBegin(), zone.getEnd(), true);
              ((VAnnotation)nextSentence).setMarked(true);
              
              createQuestionAndAnswer( pJCas, zone, question, answer, delemeter);
             
              
            }
          }  else {  // create a question without an answer
            answer = null;
            Annotation zone = new Annotation(pJCas);
            zone.setBegin( sentence.getBegin());
            zone.setEnd( sentence.getEnd() );
            createQuestionAndAnswer( pJCas, zone, question, answer, delemeter);
            
            
          } // end if
                    
        
        } // end if
          
      
        
      } // end loop through sentences
      
      
    } // end if there are annotations
    
    
  }  // End Method MultiLineQuestionAnswer() -----------------------
  


  // =======================================================
  /**
   * nextSentenceNeedsRepair returns true if the sentence has a question
   * followed by a short answer, and the next line starts with a capitalized token
   * 
   * 
   * @param pJCas
   * @param pSentence
   * @param pDelimiter
   * @return int
   */
  // =======================================================
  private int nextSentenceNeedsRepair(JCas pJCas, Annotation pSentence, Annotation pDelimeter) {
  
    int returnVal = -1;
    
    String sentence = pSentence.getCoveredText();
    String docText = pJCas.getDocumentText();
    String buff = sentence;
    int locationOfNewline = 0;
    
    if ( pDelimeter != null ) {

      try {
        if ( pDelimeter.getEnd() < pSentence.getEnd() )
          buff = docText.substring( pDelimeter.getEnd() + 1, pSentence.getEnd());
        
        
        locationOfNewline = buff.indexOf('\n');
      } catch (Exception e ) {
        e.printStackTrace();
        int i = pDelimeter.getEnd();
        int k = pSentence.getEnd();
        System.err.println("Issue with repare "  + i + "|" + k + "|" + e.getMessage());
        System.exit(-1);
      }
      
      
      if ( locationOfNewline > 0 && locationOfNewline < buff.length() ) {
        String nextLine = buff.substring( locationOfNewline + 1);
        if ( nextLine != null && nextLine.trim() != null && nextLine.trim().length() > 0) {
          if (nextLine.trim().charAt(0) >= 'A' &&  nextLine.trim().charAt(0) <= 'a') {
            returnVal = locationOfNewline + pDelimeter.getEnd() + 1;
         //   System.err.println("Needs repair |" + sentence + "|");
           
          }
        }
      }
    }
    return returnVal;
  } // End Method nextSentenceNeedsRepair() ======================
  

  // ------------------------------------------
  /**
   * isQuestion returns true if the sentence text has a wh word in it, or ends with a ? at the end of it
   *
   *
   * @param pSentence
   * @return boolean if true
   */
  // ------------------------------------------
  private boolean isQuestion(JCas pJCas,  Sentence pSentence) {
   
    boolean returnVal = false;
       String pSentenceText = pSentence.getCoveredText().toLowerCase();
    
    List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas, WordToken.typeIndexID, pSentence.getBegin(), pSentence.getEnd());
    WordToken secondToken = null;
    if ( tokens != null && tokens.size() > 1 ) secondToken = (WordToken) tokens.get(1);
  //  System.err.println(" isQuestion|" + pSentenceText + "|");
    if ( pSentenceText.startsWith("have ") ||
         pSentenceText.startsWith("had ") ||
         (pSentenceText.startsWith("has ") &&  !isVerb(secondToken)) ||//<--- word after not a mod|aux|verb|
         
         pSentenceText.startsWith("when ") ||
         pSentenceText.startsWith("how ")  ||
         pSentenceText.startsWith("where ")  ||
         pSentenceText.startsWith("are ")  ||
         pSentenceText.startsWith("what ") ||
         pSentenceText.startsWith("which ") ||
         pSentenceText.startsWith("whichever ") ||
         pSentenceText.startsWith("whoever ") ||
         pSentenceText.startsWith("whom ") ||
         pSentenceText.startsWith("who ") ||
         pSentenceText.startsWith("whomever ") ||
         pSentenceText.startsWith("whose ") ||
         pSentenceText.startsWith("can ") ||
         pSentenceText.startsWith("will ") ||
         pSentenceText.startsWith("would ") ||
         pSentenceText.startsWith("whatever ") ||
         pSentenceText.startsWith("why ") ||
         pSentenceText.startsWith("can ") ||
         pSentenceText.startsWith("could ") ||
         pSentenceText.startsWith("is ") ||
         (pSentenceText.startsWith("was ") && (pSentenceText.toLowerCase().contains("was th")|| // the|there|that|
                                              pSentenceText.toLowerCase().contains("was any"))) ||
         pSentenceText.startsWith("were ") ||
         pSentenceText.startsWith("do ") ||
         pSentenceText.startsWith("does ") ||
         pSentenceText.startsWith("did ") ||
         pSentenceText.startsWith("may ") ||
         pSentenceText.startsWith("should ") ||
         pSentenceText.indexOf('?') > 0 ) {
      
        if ( !pSentenceText.endsWith("."))
          returnVal = true;
    }
    
    return returnVal;
  }  // End Method isQuestion() -----------------------
    

  // =======================================================
  /**
   * isVerb returns true if the token is a verb|aux|modal
   * 
   * @param pToken
   * @return boolean
   */
  // =======================================================
  private boolean isVerb(WordToken pToken) {
    
    boolean returnVal = false;
    String pos = pToken.getPos();
    if ( pos != null ) {
      pos = pos.toLowerCase();
    
      if ( pos.contains("verb") || pos.contains("vb") ||
           pos.contains("modal")|| pos.contains("md") ||
           pos.contains("aux") )  
      returnVal = true;
    } else {
      String word = "|" + pToken.getCoveredText().toLowerCase() + "|";
      if ( "|be|is|isn't|are|am|was|wasn't|weren't|been|being|do|don't|doesn't|did|didn't|have|has|had|having|hadn't|hasn't|haven't|can|dare|may|must|ought|shall|will|".contains(word))
        returnVal = true;
      
    }
      
    
    return returnVal;
    // End Method isVerb() ======================
  }

  // -----------------------------------------
  /**
   * process retrieves lines of the document, labels those that are questions
   * as QuestionAndAnswer elements.
   * 
   * Lines that have other slot values in them have already been marked.  This method
   * will ignore those lines.
   * 
   * 
   */
  // -----------------------------------------
  public void singleLineQuestionAnswer(JCas pJCas) throws AnalysisEngineProcessException {

    String delimiter = null;
    DependentContent answer = null;
    List<Annotation> lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID);

   // markExistingSlotFillers( pJCas );
    markExistingContentHeading( pJCas );
    
    
    markMultiLineListElements( pJCas, lines);
    
    if (lines != null) {
      for (Annotation line : lines) {
        if (line != null && 
            line.getCoveredText() != null && 
            line.getCoveredText().trim().length() > 0  &&
            !isThereASlotValueInLine(pJCas, line )) { 
          String aLine = line.getCoveredText();
       //   System.err.println("XXXXXX Looking at line " + aLine + " and it is not marked right? " + ((VAnnotation)line).getMarked());
          if (aLine != null && aLine.trim().length() > 2) {
            String realLine = aLine;
            aLine = aLine.toLowerCase();
          

            Delimiter delimiterAnnotation = findDelimeter(pJCas, line);
            if (delimiterAnnotation != null) { 
              delimiter = delimiterAnnotation.getCoveredText();
             // System.err.println("XXXXXXXX Line|" + realLine + "|" + delimiter + "|" + delimiter );
            }

            if (leftAnswer(aLine, delimiter)) {
             // System.err.println("XXXXXXX is leftAnswer");
              if ( delimiterAnnotation == null ) { 
                delimiterAnnotation = findRightBracketDelimeter(pJCas, line );
                if ( delimiterAnnotation != null ) delimiter = delimiterAnnotation.getCoveredText();
              }
              ContentHeading question = findBetterQuestion(pJCas, line, delimiter);
              if (question == null) { 
                //System.err.println("XXXXXXX question is empty, try again with quetion on the right of answer");
                question = findQuestionOnRightOfAnswer(pJCas, (Line) line, delimiterAnnotation, false);
              }
              answer = findBetterAnswer(pJCas, (Line) line, question);
              
              
              if (answer != null) {
                createQuestionAndAnswer(pJCas, line, question, answer, delimiterAnnotation);
                // System.err.println("XXXXXXX is leftAnswer|question|" + question.getCoveredText() + "|Answer|" + answer.getCoveredText() ); 
              }
            } else {
             
              if (delimiterAnnotation != null) {
               
                  // ---------------------------------------------------
                  // don't try too hard to think this line is a question
                 // System.err.println("XXXXXX not a multi-line, is it a question");
                  if (looksLikeAQuestion(realLine, delimiter)) {
                    // System.err.println("XXXXXX looks like a single line question");
                     answer = findAnswer(pJCas, (Line) line, delimiterAnnotation, false);
                    ContentHeading question = findQuestion(pJCas, line, delimiterAnnotation);

                    if (question != null) { 
                      createQuestionAndAnswer(pJCas, line, question, answer, delimiterAnnotation);
                     
                  
                    }
                  } // end of if this looks like a question
                
              } else { // end if there is a delimiter 
                // --------------------------------
                //  No obvious delimiter found - try one last time
               // System.err.println("XXXXXXX no delimiter found and not a multi-line");
                ContentHeading question = findBetterQuestion(pJCas, line, aLine);
                answer = findBetterAnswer(pJCas, line, question);
                
                if (question != null) { 
                  createQuestionAndAnswer(pJCas, line, question, answer, delimiterAnnotation);
                 
                 
                } // end second if the question is not empty
              } // end if
            } // end if this is a right answer
          } // end if there is content in the line

        } // end if the line is not empty
      } // end loop thru line
    } // end if there are any lines

  } // end Method process() ----------------
   
  

  // =======================================================
  /**
   * isThereASlotValueInLine returns true if there is a slot value or content heading already on this line.
   * 
   * @param pJCas
   * @param line
   * @return
   */
  // =======================================================
  private boolean isThereASlotValueInLine(JCas pJCas, Annotation line) {
    
    boolean returnVal = false;
    List<Annotation> slotValues = UIMAUtil.getEnclosingAnnotation(pJCas, line, SlotValue.typeIndexID);
    
    if (slotValues != null && slotValues.size() > 0)
      returnVal = true;
    else {
      List<Annotation> contentHeading = UIMAUtil.getEnclosingAnnotation(pJCas, line, ContentHeading.typeIndexID);
      
      if (contentHeading != null && contentHeading.size() > 0)
        returnVal = true;
      
    }
    
    
    return returnVal;
  } // End Method isThereASlotValueInLine() ======================
  

  // ------------------------------------------
  /**
   * markMultiLineListElementss marks lines that are part of multi-line list elements
   * that are better picked up through multi line question methods
   *
   *
   * @param pJCas
   * @param pLines
   */
  // ------------------------------------------
  private void markMultiLineListElements(JCas pJCas, List<Annotation> pLines) {
    
    if (pLines != null && pLines.size() > 0) {

      List<Annotation> lists = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.ListElement.typeIndexID);

      if (lists != null && lists.size() > 0) {
        
        // -----------------------------
        // remove lists that are not multi-line
        lists = removeListElementsThatAreNotMultiLine( lists);
        
        for (Annotation line : pLines) {

          List<Annotation> someSlotValues = UIMAUtil.fuzzyFindAnnotationsBySpan(lists, line.getBegin(), line.getEnd());

          if (someSlotValues != null && someSlotValues.size() > 0) ((VAnnotation) line).setMarked(true);
          else ((VAnnotation) line).setMarked(false);

        } // end loop through lines
      } // end if there are slotValues
    } // end if there are lines
    
    
  }  // End Method markMultiLineLists() -----------------------
  

  // ------------------------------------------
  /**
   * removeListElementsThatAreNotMultiLine
   *
   *
   * @param pLists
   * @return List of multi-line lists
   */
  // ------------------------------------------
  private List<Annotation> removeListElementsThatAreNotMultiLine(List<Annotation> pListElements) {
  
    ArrayList<Annotation> newListElements = new ArrayList<Annotation>( pListElements.size());
    
    if ( pListElements != null ) {
      
      for ( Annotation anElement : pListElements ) {
        
        if ( ((gov.va.chir.model.ListElement)anElement).getSpansLines() )
          newListElements.add( anElement);
      }
    }
    
    
    return newListElements;
  }  // End Method removeListsThatAreNotMultiLine() -----------------------
  

  // ------------------------------------------
  /**
   * markExistingSlotValues marks lines that already are 
   * covered by slotValue annotations
   *
   */
  // ------------------------------------------
  private void markExistingSlotFillers(JCas pJCas) {
    
    
      List<Annotation> slotValues = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID);

      if (slotValues != null && slotValues.size() > 0) {
        for (Annotation slotValue : slotValues) {

          List<Annotation> lines = UIMAUtil.getEnclosingAnnotation(pJCas, slotValue, Line.typeIndexID);
         

          if (lines != null && lines.size() > 0)
            for ( Annotation line : lines ) {
              ((VAnnotation) line).setMarked(true);
              
            }
      
      } // end if there are slotValues
    } // are lines
   // intln();
  }  // End Method markExistingSlotFillers() -----------------------
  
  // ------------------------------------------
  /**
   * markExistingContentHeading marks lines that already are 
   * covered by contentHeading annotations
   *
   */
  // ------------------------------------------
  private void markExistingContentHeading(JCas pJCas ) {
    
   

      List<Annotation> sectionHeadings = UIMAUtil.getAnnotations(pJCas, ContentHeading.typeIndexID);

      if (sectionHeadings != null && sectionHeadings.size() > 0) {
        for (Annotation sectionHeading : sectionHeadings) {

          List<Annotation> lines = UIMAUtil.getEnclosingAnnotation(pJCas, sectionHeading, Line.typeIndexID);
          

          if (lines != null && lines.size() > 0)
            for ( Annotation line : lines ) {
              ((VAnnotation) line).setMarked(true);
           
            }
         
      } // end if there are slotValues
       
    } // end if there are lines
    
    
  }  // End Method markExistingSlotFillers() -----------------------
  
  
  // -----------------------------------------
  /**
   * areMultipleQuestions detects if there are multiple delimeters, and
   * therefore, multiple questions
   * 
   * @param aLine
   * @param delimeter
   * @return
   */
  // -----------------------------------------
  private String[] areMultipleQuestions(String aLine, String delimeter) {
    String[] returnVal = null;
    
    int next = 0;
    int previous = -1;
    int ctr = 0;
    
    if ( !delimeter.matches("\\s+")) {
      next = aLine.indexOf(delimeter, next);
      while ( next != -1 &&   next != previous ) {
        previous = next;
        next = aLine.indexOf(delimeter, next+1);
        ctr++;
      }
    }

    int numBrackets= 0;
    char [] aLineArray = aLine.trim().toCharArray();
    for ( int i = 0; i < aLineArray.length; i++ ) {
      if      (   aLineArray[i] == '(' ) numBrackets++;
      else if (   aLineArray[i] == '[' ) numBrackets++;
      else if (   aLineArray[i] == '{' ) numBrackets++;
      else if (   aLineArray[i] == '_' ) numBrackets++;
    }
    
    
    if (ctr > 1  || (numBrackets > 1 && !(
         aLine.toLowerCase().contains("no" ) || 
         aLine.toLowerCase().contains("yes") ))) 
     {
      delimeter = findLongestRunOfSpacesInLine( aLine);
      if ( delimeter != null  && delimeter.length() > 3) {
      // split using spaces as the delimeter between questions
        Pattern p1 = Pattern.compile("\\b\\s{" + delimeter.length() + ",}");
        returnVal = p1.split(aLine);
      }
    } /* else { // check to see you have multiple label[ ] kind of questions
      String bLine = replaceBrackets(aLine);
      String pattern2 = "\\w+\\s*" + BChar + "\\w*\\s*" + EChar;
      String pattern1 = "(\\s*" + BChar + "\\w*\\s*" + EChar + ")\\s*\\w+";
      Pattern p1 = Pattern.compile(pattern1);
      Matcher m1 = p1.matcher(bLine);
     
      ArrayList<String>questions = new ArrayList<String>();
      while ( m1.find()) {
        String r1 = aLine.substring(m1.start(0), m1.end(0) );
        String r2 = aLine.substring(m1.start(1), m1.end(1));
       
        questions.add(r1 + delimeter + r2);
      }
      if ( questions.size() > 1 )
        returnVal = questions.toArray(new String[questions.size()]);
      
    }
     */
    
    return returnVal;
  } // end Method areMultipleQuestions() ----------

  

  // -----------------------------------------
  /**
   * looksLikeAQuestion returns true if the criteria is met
   *   - the delimiter is not a space,
   *   - if the delimiter is a space, it was not prefaced by a period, 
   *     and followed by a Capital letter.
   * 
   * @param aLine
   * @param delimeter
   * @return
   */
  // -----------------------------------------
  private boolean looksLikeAQuestion(String aLine, String delimeter) {
     boolean returnValue = false;
     
     if ( delimeter.matches("\\s+")) {
       int n = delimeter.length();
       Pattern p = Pattern.compile("\\w\\.\\s{" + n + ",}[A-Z|0-9]");
       Matcher m = p.matcher( aLine);
       if ( m.find() ) {
         returnValue = false;
       } else {
         returnValue = true;
       }
     } else {
       returnValue = true;
     }
     
    return returnValue;
  } // end Method looksLikeAQuestion() -------------------



  
  // -----------------------------------------
  /**
   * leftAnswer returns true if the answer is to
   * the left of the question
   * 
   * @param aLine
   * @return
   */
  // -----------------------------------------
  private boolean leftAnswer(String aLine, String delimeter) {
    boolean returnVal = false;
   
    if ( delimeter != null) {
      if ( areMultipleQuestions( aLine, delimeter) != null ) { 
        returnVal = false;
        return returnVal;
      } 
    } 
    
    String bLine = replaceBrackets( aLine);
   
    String beginPattern = "^\\s*" + BChar+ "\\s*\\S*\\s*" + EChar + "\\s*\\w+";
    Pattern p1 = Pattern.compile( beginPattern );
    String nextPattern = beginPattern + "\\s*" + BChar + "\\s*\\S*\\s*" + EChar + "\\s*\\w+"; 
    Pattern p2 = Pattern.compile( nextPattern); 
    Matcher matcher = p1.matcher(bLine);
    ArrayList<String> pieces = new ArrayList<String>();
    while ( matcher.find()) {
      String piece = matcher.group();
      pieces.add( piece);
      // System.err.println(" --left answer --> " + piece);
    }
   
    if ( pieces != null && pieces.size() > 0 )
      returnVal = true;
    
    matcher = p2.matcher( bLine );  // <---- multiple questions maybe
    if ( matcher.find() == true ) {
      if (!( matcher.group().toLowerCase().contains("yes") && 
             matcher.group().toLowerCase().contains("no") ))
        returnVal = false;
    }
    

   
    
    return returnVal;
  } // end leftAnswer



  // -----------------------------------------
  /**
   * findQuestionOnRightOfAnswer 
   * 
   * @param pJCas
   * @parm pLine
   * @param pDelimiter
   * @param pKeep
   * @return ContentHeading
   */
  // -----------------------------------------
  private ContentHeading findQuestionOnRightOfAnswer(JCas pJCas, Line pLine, Delimiter pDelimiter, boolean pKeep) {
   
     ContentHeading question = null;
    
    if ( pDelimiter != null) {
      question = createContentHeading( pJCas, pDelimiter.getEnd(), pLine.getEnd(), pKeep, "findQuestionOnRighttttttttOfAnswer_");
     
    }
    
    return question;
  } // end Method findQuestionONRightOfAnswer() ------



  // -----------------------------------------
    /**
     * findQuestion retrieves the span to the left of the delimiter from the line passed in.
     *  
     * @param pJCas 
      * @param pLine
     * @param  pDelimiter
     * @return contentHeading 
     */
    // -----------------------------------------
    private ContentHeading findQuestion(JCas pJCas, Annotation pLine, Delimiter pDelimiter) {
      ContentHeading contentHeading = null;
     
   /* old code
      if ( delimiter != null ) {
        int leadingSpaces = 0;
        for ( ; leadingSpaces < aLine.length(); leadingSpaces++) if ( aLine.charAt(leadingSpaces) != ' ') break; 
        int i = aLine.indexOf( delimiter, leadingSpaces + 1) ;
        if ( i > 1 && i < aLine.length()) 
          question = aLine.substring( 0, i).trim();
        
        */
      // get tokens to the left of the delimiter
      int dEnd = pLine.getEnd();
      if ( pDelimiter != null)
        dEnd = pDelimiter.getBegin() + 1;
      List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas,Token.typeIndexID, pLine.getBegin(), dEnd);
      if ( tokens != null && tokens.size() > 0) {
        // -------------------
        // Strip off list delimiter tokens
     
        ArrayList<Token> questionTokens = new ArrayList<Token>();
        for ( Annotation token : tokens ) {
      //    System.err.println("--- looking at question token |" + token.getCoveredText() + "|" );
          if ( !((Token)token).getListDelimiter()  && ( ((Token)token).getTypeIndexID() != WhitespaceToken.typeIndexID )) {
            questionTokens.add( (Token)token);
        //    System.err.println(" -------- added token |" + token.getCoveredText() + "|" );
          }
        } // end loop through tokens
      
        if ( questionTokens != null && questionTokens.size() > 0 ) {
        
          contentHeading = new ContentHeading( pJCas);
          Token beginToken = questionTokens.get(0);
          int begin = beginToken.getBegin();
          Token endToken = questionTokens.get( questionTokens.size() -1);
          int theEnd = endToken.getEnd();
          if ( begin >= theEnd ) {
            System.err.println("Issue creating a question content findQuestionMethod " + begin + "|" + theEnd);
            return null;
          }
          contentHeading.setBegin( begin);
          contentHeading.setEnd(  theEnd);
          contentHeading.setId("findQuestionR_Method_" + this.conentHeadingCtr++);
         
        }
      }
         
      return contentHeading;
      
    } // end Method findQuestion() -------------

  

  // ------------------------------------------
  /**
   * findAnswer returns the part on the right side of the
   * delimiter passed in
   *
   *
   * @param pJCas
   * @param pLine
   * @param pDelimiter
   * @param pKeep
   * @return
   */
  // ------------------------------------------
  private DependentContent findAnswer(JCas pJCas, Annotation pLine, Delimiter pDelimiter, boolean pKeep) {
   
    DependentContent answer = null;
    
    if ( pDelimiter != null && pLine != null && pDelimiter.getEnd() +1 < pLine.getEnd() ) {
      answer = createDependentContent(pJCas, pDelimiter.getEnd() , pLine.getEnd(), pKeep);
    }
    
    return answer;
    
  }  // End Method findAnswer() -----------------------
  

  // -----------------------------------------
  /**
   * findDelimeter returns the string that looks like a 
   * question delimiter
   * 
   * @param aLine
   * @return String
   */
  // -----------------------------------------
  private Delimiter findDelimeter( JCas pJCas, Annotation pLine) {
    
    Delimiter delimiterAnnotation = null;
    String aLine = pLine.getCoveredText();
    String delimiter = null;
    StringBuffer delimeterBuffer = null;
    String spaceDelimeter = findLongestRunOfSpacesInLine(aLine);
    
    int patternBegin = -1;
    char[] alineArray = aLine.toCharArray();
    
    int ctrColon = 0;
    int ctrQuestion = 0;
    for ( int i = 0; i < alineArray.length; i++ ) {
      if (( alineArray[i] == ':') &&  i > 0 && !U.isNumber(alineArray[i-1]) ) ctrColon++;
      if ( alineArray[i] == '?')  ctrQuestion++;
    }
    boolean endsWithPeriod = false;
    if ( aLine.trim().length() > 1  && aLine.trim().charAt(aLine.trim().length() -1) == '.' ) endsWithPeriod = true;
    
    
    if       ( ctrQuestion > 0)  delimiter = "?";
    else if  ( ctrColon    > 0) delimiter = ":";
    else if (( spaceDelimeter != null) && (spaceDelimeter.length() > 3 ) && (!endsWithPeriod))    delimiter = spaceDelimeter;
    else if ((patternBegin = aLine.indexOf("....")) > 1) { 
       delimeterBuffer = new StringBuffer();
      for ( int i = patternBegin; i < aLine.length(); i++ )
       if ( aLine.charAt(i) == '.') 
         delimeterBuffer.append( ".");
       else
         break;
    }
    else if ((patternBegin = aLine.indexOf("__")) > 1) { 
       delimeterBuffer = new StringBuffer();
      for ( int i = patternBegin; i < aLine.length(); i++ )
       if ( aLine.charAt(i) == '_') 
         delimeterBuffer.append( "_");
       else
         break;
    }
    if (delimeterBuffer != null && delimeterBuffer.length() > 0)  {
      delimiter = delimeterBuffer.toString();
    }
      
    if ( delimiter != null ) {
    
      // --------------------------
      // If there are other sentence breaking delimiters in the line, don't make this a delimiter
      if ( aLine.contains(";") || aLine.contains("-") ) {
      } else {
      
      
      int delimiterBegin = pLine.getBegin() + aLine.indexOf( delimiter);
      delimiterAnnotation = new Delimiter( pJCas);
      delimiterAnnotation.setBegin( delimiterBegin);
      delimiterAnnotation.setEnd( delimiterBegin + delimiter.length());
      delimiterAnnotation.setId("questionAnswerFindDelimeter");
      }
    } else {
      delimiterAnnotation = findRightBracketDelimeter( pJCas, pLine);
    }
    
    
    
    return delimiterAnnotation;
  } // end Methof findDelimeter() -------------

//-----------------------------------------
  /**
   * findDelimeter returns the string that looks like a 
   * question delimiter
   * 
   * @param aLine
   * @return String
   */
  // -----------------------------------------
  private Delimiter findRightBracketDelimeter( JCas pJCas, Annotation pLine) {
    
    Delimiter delimiterAnnotation = null;
    String aLine = pLine.getCoveredText();
    String delimiter = null;
   
    char[] alineArray = aLine.toCharArray();
    
    int lastBracket = -1;
    for ( int i = 0; i < alineArray.length; i++ ) {
      if ( alineArray[i] == ']') { 
        lastBracket = i;
        
      }
      
    }
   
    if ( lastBracket > -1) {
      delimiter = "]";
      int delimiterBegin = pLine.getBegin() + lastBracket;
      delimiterAnnotation = new Delimiter( pJCas);
      delimiterAnnotation.setBegin( delimiterBegin);
      delimiterAnnotation.setEnd( delimiterBegin + delimiter.length());
      delimiterAnnotation.setId("FindRightBracketDelimete");
      
    }
    
    return delimiterAnnotation;
  } // end Method findRightBracketDelimeter() -------------
  

// -----------------------------------------
  /**
   * findLongestRunOfSpacesInLine looks for the longest run of 
   * inner spaces within a line and returns them
   * 
   * @param aLine
   * @return String
   */
  // -----------------------------------------
  private String findLongestRunOfSpacesInLine(String aLine) {
    String returnValue = null;
   
    if ( aLine != null ) {
      
      String bLine = aLine.trim();
      char [] line = bLine.toCharArray();
      int ctr = 0;
      int maxCtr = 0;
      for ( int i = 0; i < line.length; i++ ) {
        if( line[i] == ' ' ) {
          ctr++;
          if ( ctr > maxCtr) maxCtr = ctr;
        } else
          ctr=0;
      
      } // end loop thru the chars of the line
      if ( maxCtr > 1)
        returnValue = U.spacePadRight(maxCtr, " ");
      
    }
    return returnValue;
  } // end Method findLongestRunOfSpacesInLine() ---



// ------------------------------------------
  /**
   * findBetterAnswer Given a better question, return the answer
   * from the original string
   *
   *
   * @param pJCas
   * @param pLine
   * @param pQuestion
   * @return
   */
  // ------------------------------------------
  private DependentContent findBetterAnswer(JCas pJCas, Annotation pLine, ContentHeading pQuestion) {
  
    DependentContent betterAnswer = null;
    
  
    if ( pQuestion != null ) {
   
    
    // --------------------------------------------
    // If the answer is on the left of the question
    // return the part of the line before the question
    if ( pQuestion.getEnd() >= pLine.getEnd()) {
      
      betterAnswer = createDependentContent( pJCas, pLine.getBegin(), pQuestion.getBegin() -1, false);
    } else {
    // ----------------------------------------
    // If the answer is on the right of the question
    // return the part after the question
      betterAnswer = createDependentContent( pJCas, pQuestion.getEnd() + 1, pLine.getEnd() , false);
    }
    
    
    }
    
    return betterAnswer;
    
  }  // End Method findBetterAnswer() -----------------------

  // -----------------------------------------
  /**
   * findBetterQuestion takes answers that are of the type
   * [] widowed and finds the labels for them
   * 
   * @param pLine
   * @param pJCas
   * 
   * @param answer
   * @return
   */
  // -----------------------------------------
  private ContentHeading findBetterQuestion(JCas pJCas, Annotation pLine, String answer) {
    String question = null;
    ContentHeading contentHeading = null;

    if (answer != null) {
      String bAnswer = replaceBrackets(answer);
      // Look for answer
      String side = "neither";
      int first = bAnswer.trim().lastIndexOf(BChar);
      int last = bAnswer.trim().lastIndexOf(EChar);
      if (last >= bAnswer.trim().length() - 1) side = "right";
      else if ((last > -1 && first <= 2)) side = "left";
      /*
       * if ( side.equals("neither")) {
       * first = bAnswer.trim().indexOf(no);
       * last = bAnswer.trim().indexOf(EChar);
       * if ( last >= bAnswer.trim().length() -1 )
       * side = "right";
       * else if ( (last <= 2 ))
       * side = "left";
       * }
       */
      int beginIndex = bAnswer.lastIndexOf(EChar);
      int endIndex = bAnswer.lastIndexOf(BChar);
    //  System.err.println(answer + "|" + side + "|" + beginIndex + "|" + endIndex);
      if ( endIndex > beginIndex ) {
      if (side.equals("left")) {

        question = answer.substring(beginIndex + 1);
      } else if (side.equals("right")) {
        if (endIndex - 1 > 0) 
        	question = answer.substring(0, endIndex - 1);
      }
      if ( question != null ) {
    	  int questionBegin = pLine.getBegin() + pLine.getCoveredText().indexOf(question);
          int   questionEnd = questionBegin + question.length();
          contentHeading = createContentHeading(pJCas, questionBegin, questionEnd, false, "findBetterQuestion_"  );
         
      }
      
    }
    }
    return contentHeading;
  } // end Method findBetterQuestion() --------


//-----------------------------------------
 /**
  * createQuestionAndAnswer creates slotValue structure. As a temporary side effect, lines that have this
  * slotValue in it get marked.  
  * 
  * @param pJCas
  * @param pLine
  * @param aLine
  * @param question
  * @param answer
  * @param delimeter
  * 
  * @return Evidence
  */
 // -----------------------------------------
 private SlotValue createQuestionAndAnswer( JCas pJCas, Annotation pLine, ContentHeading pQuestion, DependentContent answer, Delimiter pDelimiter )  {
  
   SlotValue questionAndAnswer = new SlotValue( pJCas);
 
 
   questionAndAnswer.setBegin(                    pLine.getBegin());
   questionAndAnswer.setEnd(                      pLine.getEnd());
   questionAndAnswer.setId("createQuestionAndAnswer");
   
   ContentHeading question = pQuestion;
   
   // ---------------------------
   // Find any Phrases that are within this line (this might be overly general
   List<Annotation> phrases = UIMAUtil.getAnnotationsBySpan(pJCas, gov.va.chir.model.Phrase.type, pLine.getBegin(), pLine.getEnd());
   if ( phrases != null )
     for ( Annotation phrase : phrases) {
       ((VAnnotation) phrase).setParent( questionAndAnswer);
     }
 
   int answerLength = 0;
   if ( answer != null ) {
     answerLength = answer.getEnd()  - answer.getBegin();
     if ( question == null && answerLength > 2 ) {
       question = findBetterQuestion( pJCas, pLine, answer.getCoveredText() );
       answer = findBetterAnswer( pJCas, pLine, question );
     }
   }
   
   // -------------------------
   // Question 
   
   if ( question != null ) {
     
     question.addToIndexes();
     question.setParent( questionAndAnswer);
    
     questionAndAnswer.setHeading(question);
     questionAndAnswer.setContentHeaderString(question.getCoveredText());
     gov.va.chir.model.Phrase phrase = createPhrase(pJCas, question);
     questionAndAnswer.setContentHeader(phrase );
   }
    
   
   // -------------------------
   // Answer
   if ( answer != null ) {
     
     DependentContent contentValue = answer;
     contentValue.setParent( questionAndAnswer);
     contentValue.addToIndexes(pJCas);
     phrases = createPhrases(pJCas, contentValue);
     questionAndAnswer.setContentString( answer.getCoveredText() );
     questionAndAnswer.setDependentContent(contentValue);
   
   //   questionAndAnswer.setContent(UIMAUtil.list2FsArray(pJCas,phrases )); <--- not sure I need this
     
     
   }
   // -------------------------
   // Mark the delimiter
   if ( pDelimiter != null ) {
     pDelimiter.addToIndexes();
     pDelimiter.setParent( questionAndAnswer);
     questionAndAnswer.setDelimiter( pDelimiter.getCoveredText() );
   }  
   
   questionAndAnswer.addToIndexes(pJCas);
   
   assertQuestionAnswer( pJCas, questionAndAnswer);
   
   markLinesWithAnnotationsInIt( pJCas, questionAndAnswer);
   
   return questionAndAnswer;

 } // end Method createEvidence() ---
  
  

 

  // =======================================================
/**
 * markLinesWithAnnotationInIt finds the lines that cover this entity
 * and mark them.
 * 
 * @param pJCas
 * @param pAnnotation
 */
// =======================================================
private void markLinesWithAnnotationsInIt(JCas pJCas, Annotation pAnnotation ) {
  
  List<Annotation> lines = UIMAUtil.getAnnotationsBySpan(pJCas, Line.typeIndexID, pAnnotation.getBegin(), pAnnotation.getEnd());
  
  if ( lines != null && lines.size() > 0 ) {
    for (Annotation line : lines )
      ((Line)line).setMarked(true);
  }
  
 
  
}  // End Method markLinesWithSlotValueInIt() ======================


  // =======================================================
  /**
   * unMarkLines unmarks lines
   * 
   * @param pJCas
   */
  // =======================================================
  private void unMarkLines(JCas pJCas) {
    List<Annotation> lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID);
    
    if ( lines != null && lines.size() > 0 ) {
      for (Annotation line : lines )
        ((Line)line).setMarked(false);
    }
    
  } // End Method unMarkLines() ======================
  

  // ------------------------------------------
/**
 * createContentHeading
 *
 *
 * @param pJCas
 * @param questionStart
 * @param questionEnd
 * @param pId 
 */
// ------------------------------------------
private ContentHeading createContentHeading(JCas pJCas, int questionStart, int questionEnd, boolean pKeep, String pId) {
  
  ContentHeading contentHeading = new ContentHeading(pJCas);
  contentHeading.setBegin( questionStart);
  contentHeading.setEnd(questionEnd);
  contentHeading.setId(pId + "_" + this.conentHeadingCtr++);
  if ( pKeep) 
    contentHeading.addToIndexes(pJCas);

   // VUIMAUtil.setProvenance(pJCas, (VAnnotation)contentHeading,this.getClass().getName() );
  
  return contentHeading;
  // End Method createContentHeading() -----------------------
}

  // ------------------------------------------
  /**
   * createDependentContent
   *
   *
   * @param pJCas
   * @param pBeginOffset
   * @param pEndOffset
   * @param pKeep   (if this should be a perminant annotation or not
   * @return DependentContent
   */
  // ------------------------------------------
  private DependentContent createDependentContent(JCas pJCas, int pBeginOffset, int pEndOffset, boolean pKeep) {
    
    DependentContent dependentContent = new DependentContent( pJCas);
    dependentContent.setBegin( pBeginOffset);
    dependentContent.setEnd( pEndOffset);
    dependentContent.setId("QuestionAnswerCreateDependentContent");
    // VUIMAUtil.setProvenance(pJCas, (VAnnotation)dependentContent,this.getClass().getName() );
    if ( pKeep )
      dependentContent.addToIndexes(pJCas);
    
    return dependentContent;
    
    
  }  // End Method createDependentContent() -----------------------

  // -----------------------------------------
  /**
   * createPhrases creates a set of phrases around
   * the span of the annotation passed in. 
   * 
   * I had originally returned the phrases that were created, but it is likely that
   * those phrases, if they were created before this annotator are just wrong. 
   * 
   * @param pJCas
   * @param pAnnotation
   * @return
   */
  // -----------------------------------------
  private gov.va.chir.model.Phrase createPhrase(JCas pJCas, Annotation pAnnotation) {
     
      gov.va.chir.model.Phrase phrase = new gov.va.chir.model.Phrase(pJCas);
      phrase.setBegin( pAnnotation.getBegin());
      phrase.setEnd( pAnnotation.getEnd());
      // VUIMAUtil.setProvenance(pJCas, (VAnnotation)phrase,this.getClass().getName() );
      
      /* ----------------------------
       * Obsolete code
       
      // --------------------------------------
      // find terms if they exist
      // --------------------------------------
      FSArray termz = UIMAUtil.getAnnotationFSArrayBySpan(pJCas, gov.va.chir.model.LexicalElement.type, pAnnotation.getBegin(), pAnnotation.getEnd());
      if ( termz != null ) 
        phrase.setLexicalElements( termz );
      
      
      // --------------------------------------
      // find tokens if they exist
      // --------------------------------------
      FSArray tokenz = UIMAUtil.getAnnotationFSArrayBySpan(pJCas, gov.va.chir.model.Token.type, pAnnotation.getBegin(), pAnnotation.getEnd());
      if ( tokenz != null ) 
        phrase.setPhraseTokens( tokenz);
      */  
      
      phrase.setPhraseKind( "final");
      // phrase.setPhraseType("SBARQ");
      
      phrase.addToIndexes( pJCas );
      
      
    return phrase;
  } // end Method getPhrases() -- ------

  // -----------------------------------------
  /**
   * createPhrases creates a set of phrases around
   * the span of the annotation passed in. 
   * 
   * I had originally returned the phrases that were created, but it is likely that
   * those phrases, if they were created before this annotator are just wrong. 
   * 
   * @param pJCas
   * @param pAnnotation
   * @return
   */
  // -----------------------------------------
  private List<Annotation> createPhrases(JCas pJCas, Annotation pAnnotation) {
     
      List<Annotation> phrases = null;
      gov.va.chir.model.Phrase phrase = new gov.va.chir.model.Phrase(pJCas);
      phrase.setBegin( pAnnotation.getBegin());
      phrase.setEnd( pAnnotation.getEnd());
      // VUIMAUtil.setProvenance(pJCas, (VAnnotation)phrase,this.getClass().getName() );
      
      /*
      // -------------------------------------
      // Obsolete code
      // --------------------------------------
      // find terms if they exist
      // --------------------------------------
      FSArray termz = UIMAUtil.getAnnotationFSArrayBySpan(pJCas, gov.va.chir.model.LexicalElement.type, pAnnotation.getBegin(), pAnnotation.getEnd());
      if ( termz != null ) 
        phrase.setLexicalElements( termz );
      
      
      // --------------------------------------
      // find tokens if they exist
      // --------------------------------------
      FSArray tokenz = UIMAUtil.getAnnotationFSArrayBySpan(pJCas, gov.va.chir.model.Token.type, pAnnotation.getBegin(), pAnnotation.getEnd());
      if ( tokenz != null ) 
        phrase.setPhraseTokens( tokenz);
      */
      
      phrase.setPhraseKind( "final");
      // phrase.setPhraseType("SBARQ");
      
      phrase.addToIndexes( pJCas );
      phrases = new ArrayList<Annotation>(1);
      phrases.add( phrase);
      
      
    return phrases;
  } // end Method getPhrases() -- ------

 
  // -----------------------------------------
  /**
   * replaceBrackets replaces brackets, parren's, and curly brackets 
   * with _OPEN_ and _CLOSE_ 
   * 
   * @param aLine
   * @return String 
   */
  // -----------------------------------------
  private String replaceBrackets(String aLine) {
    String bLine = aLine;
  
    if ( bLine != null ) {
    bLine = bLine.replaceAll("\\[", BChar);
    bLine = bLine.replaceAll("\\(", BChar);
    bLine = bLine.replaceAll("\\{", BChar);
    
    bLine = bLine.replaceAll("\\]", EChar);
    bLine = bLine.replaceAll("\\)", EChar);
    bLine = bLine.replaceAll("\\}", EChar);
    }
    return bLine;
  } // end Method replaceBrackets

// -----------------------------------------
/**
 * assertQuestionAnswer will mark the processMe attribute and the negation
 * status attribute depending on the question answer
 * 
 * @param questionAndAnswer
 */
// -----------------------------------------
public static void assertQuestionAnswer(JCas pJCas, SlotValue questionAndAnswer) {
  
  String content = null;
  if ( questionAndAnswer.getDependentContent() != null ) {
   content = questionAndAnswer.getDependentContent().getCoveredText();
  }
   ContentHeading heading = questionAndAnswer.getHeading();
   if ( heading != null ) {
     heading.setProcessMe(false);
     heading.setNegation_Status("Negated");
   
   }
   DependentContent dependentContent = questionAndAnswer.getDependentContent();
   if ( dependentContent != null ) {
     dependentContent.setProcessMe(false);
     dependentContent.setNegation_Status("Negated");
   }
   if ( content != null ) {
     char contentArray[] = content.toLowerCase().toCharArray();
   
     int   xd = content.toLowerCase().indexOf("x");  // <---- too simplistic catching anxiety - it's looking for [x] or (x) or _x_
     if ( xd > 0  && contentArray[xd-1] >= 'a' && contentArray[xd-1] <= 'z' ) xd = -1;   
     if ( xd >= 0 && xd < content.length() -1   && contentArray[xd+1] >= 'a' && contentArray[xd+1] <= 'z' ) xd = -1;   
     int   pd = content.toLowerCase().indexOf("+");
  
   
     
     if ( xd < 0 && pd > -1 ) xd = pd;
   
     // ---------------------------------------
     // is x'd yes question or yes/no question
     // 
     if ( content.toLowerCase().contains("yes") ||  content.toLowerCase().contains("no") || 
         content.toLowerCase().matches("\\by\\b")   ||  content.toLowerCase().matches("\\bn\\b") )  {  
       // -------------------------
       // if Box'ed or X'd answer
     
       int yesd = content.toLowerCase().indexOf("yes");
       int  nod = content.toLowerCase().indexOf("no");
       int   yd = content.toLowerCase().indexOf("y");
       int   nd = content.toLowerCase().indexOf("n");
     
       if ( xd > -1   && content.toLowerCase().indexOf("explain") < 0){
         if ( (Math.abs(yesd - xd) < Math.abs(nod - xd)) ||
             (Math.abs(yd   - xd) < Math.abs(nd  - xd ))) {
           if ( heading != null ) {
             heading.setProcessMe(true);
             heading.setNegation_Status("Asserted");
           }
           if ( dependentContent != null ) {
             dependentContent.setProcessMe(true);
             dependentContent.setNegation_Status("Asserted");
           }
         } 
       } else if ((content.toLowerCase().contains("no") && !(content.toLowerCase().contains("yes"))) ||  // to prevent yes[] no[] from being picked up here
                  content.toLowerCase().contains("none") ||
                  (content.toLowerCase().contains(" n ") && !(content.toLowerCase().contains("y "))) ||
                  content.toLowerCase().contains("denies") ||
                  content.toLowerCase().contains("denied") 
                  
                 ) {
         
         if ( heading != null ) {
           heading.setProcessMe(true);
           heading.setNegation_Status("Negated");
         }
         if ( dependentContent != null ) {
           dependentContent.setProcessMe(true);
           dependentContent.setNegation_Status("Negated");
         }
       } else if ( ( (content.toLowerCase().contains("yes")  || 
                      content.toLowerCase().contains("good") ||
                      content.toLowerCase().contains("fair") ||
                      content.toLowerCase().contains("excellent") ||
                      content.toLowerCase().contains("outstanding") ||
                      content.toLowerCase().contains("better") ||
                      content.toLowerCase().contains("best") ||
                      content.toLowerCase().contains("well") ||
                      (content.toLowerCase().contains("adequate") ||
                      (content.toLowerCase().contains("optimal" ) && content.toLowerCase().contains("suboptimal")) ||
                      (content.toLowerCase().contains("clean") && !content.toLowerCase().contains("unclean"))  ||
                      content.toLowerCase().contains("mild") ||
                      content.toLowerCase().contains("moderate") ||
                      content.toLowerCase().contains("mst") ||               // <------- likely to cause issue
                      content.toLowerCase().contains("severe") ||
                      U.containsNumber(content.toLowerCase() ) ||   // <--- if there's a number is not a negative connotatnion
                      content.toLowerCase().contains("frequently") 
                          
                         
                          
                          ) 
                             
           
             ) && !content.toLowerCase().contains("no")) || 
                   !(content.toLowerCase().contains("none")) ||
                   !(content.toLowerCase().contains("denies")) ||
                   !(content.toLowerCase().contains("denied")) ||
                   (content.toLowerCase().contains("y")    && !content.toLowerCase().contains("n") )) {
                  // (!content.toLowerCase().contains("no") &&  !content.toLowerCase().contains("n"))) {   
                  // are terms like poor|bad|inadequate|unclean negated terms?
         if ( heading != null ) {
           heading.setProcessMe(true);
           heading.setNegation_Status("Asserted");
         }
         if ( dependentContent != null ) {
           dependentContent.setProcessMe(true);
           dependentContent.setNegation_Status("Asserted");
         }
       }
     } else if ( xd > 0 ){ // ---------------------------------------
                         // checked content
   
       if ( heading != null ) 
         heading.setProcessMe(true);
     } else if ( xd < 0  && content != null && content.length() > 20 ) {
       if ( heading != null ) {
         heading.setProcessMe(true);
         heading.setNegation_Status("Asserted");
       }
       if ( dependentContent != null ) { 
         dependentContent.setProcessMe(true);
         dependentContent.setNegation_Status("Asserted");
       }
     } else if ( content != null && U.containsNumber(content) && dependentContent != null && heading != null ) {
       dependentContent.setProcessMe(true);
       dependentContent.setNegation_Status("Asserted");
       heading.setProcessMe(true);
       heading.setNegation_Status("Asserted");
     }
   } else { // there is no content, the content heading needs to be negated
     if (heading != null )
       heading.setNegation_Status("Negated");
   }
   
   // --------------------------------------
   // Propagate the processMe down to overlapping phrases and tokens
   if ( heading != null ) {
     boolean processMe = ((VAnnotation )heading).getProcessMe();
   
     List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan( pJCas,  gov.va.chir.model.Token.typeIndexID, heading.getBegin(), heading.getEnd());
     if ( tokens != null )
       for ( Annotation token : tokens ) 
         ((VAnnotation) token).setProcessMe( processMe);
       
     List<Annotation> phrases = UIMAUtil.getAnnotationsBySpan( pJCas, gov.va.chir.model.Phrase.typeIndexID, heading.getBegin(), heading.getEnd() );
     if ( phrases != null )
       for ( Annotation phrase : phrases ) 
         ((VAnnotation)phrase).setProcessMe( processMe);
     
   }
   
   
   // --------------------------------
   // Propagate assertion/negation status to the lexical elements within the questions and answers
   if ( heading != null )  assertOrNegate(pJCas, heading);
   if ( dependentContent != null ) assertOrNegate( pJCas, dependentContent );
  
} // end Method assertQuestionAnswer() --------


//=======================================================
/**
* assertOrNegate propagates the assertion status from the entity
* to the terms and tokens that the entity encapsulates
* 
* @param pJCas
* @param pEntity
*/
//=======================================================
private static void assertOrNegate(JCas pJCas, VAnnotation pEntity) {

  assertOrNegateTerms(pJCas, pEntity);
  assertOrNegateTokens(pJCas, pEntity);

}  // End Method assertOrNegate() ======================



// =======================================================
/**
 * assertOrNegateTerms propagates the assertion status from the entity
 * to the terms and tokens that the entity encapsulates
 * 
 * @param pJCas
 * @param pEntity
 */
// =======================================================
private static void assertOrNegateTerms(JCas pJCas, VAnnotation pEntity) {
  
  List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pEntity.getBegin(), pEntity.getEnd());
  
  if ( terms != null && !terms.isEmpty()) {
    
    String status = pEntity.getNegation_Status();
    
   for (Annotation term : terms ) {
      ((LexicalElement)term).setNegation_Status(status);
    }
  }
  
}  // End Method assertOrNegateTerms() ======================



//=======================================================
/**
* assertOrNegateTerms propagates the assertion status from the entity
* to the terms and tokens that the entity encapsulates
* 
* @param pJCas
* @param pEntity
*/
//=======================================================
private static void assertOrNegateTokens(JCas pJCas, VAnnotation pEntity) {

List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas, WordToken.typeIndexID, pEntity.getBegin(), pEntity.getEnd());

if ( tokens != null && !tokens.isEmpty()) {
 
 String status = pEntity.getNegation_Status();
 
for (Annotation token : tokens ) {
   ((WordToken)token).setNegation_Status(status);
 }
}

}  // End Method assertOrNegateTokens()---------


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
   * initialize loads in the resources. 
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
      System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
    
    this.performanceMeter = new ProfilePerformanceMeter( args, this.getClass().getSimpleName() );
    
  } // end Method initialize() -------
  



  // ---------------------------------------
  // Global Variables
  // ---------------------------------------

   private String BChar = "A";
   private String EChar = "B";
   ProfilePerformanceMeter              performanceMeter = null;
  
  
} // end Class MetaMapClient() ---------------
