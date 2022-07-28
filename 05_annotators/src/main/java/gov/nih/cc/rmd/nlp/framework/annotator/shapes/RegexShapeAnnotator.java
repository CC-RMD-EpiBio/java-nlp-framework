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
 * Shapes Annotator finds terms from regular expression like
 * pattern discovery. 
 * 
 * This is a third iteration of this functionality
 * 
 * I'm limiting the scope to finding patterns within
 * a line. 
 * 
 *  
 *
 *
 * @author  Guy Divita 
 * @created April 19, 2019
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.shapes;


import java.lang.reflect.Constructor;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.PageNumber;
import gov.nih.cc.rmd.framework.model.Email;
import gov.nih.cc.rmd.framework.model.Identifier;
import gov.nih.cc.rmd.framework.model.Number;
import gov.nih.cc.rmd.framework.model.Person;
import gov.nih.cc.rmd.framework.model.PhoneNumber;
import gov.nih.cc.rmd.framework.model.PotentialNumber;
import gov.nih.cc.rmd.framework.model.SSN;
import gov.nih.cc.rmd.framework.model.Shape;
import gov.nih.cc.rmd.framework.model.Zipcode;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Line;
import gov.va.chir.model.VAnnotation;



public class RegexShapeAnnotator extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  /**
   * process retrieves lines of the document 
   * looks for patterns with the line
   * 
   * Sometimes a line will include columns in tables
   * Keep the window to look in within a column.
   * Columns are delimited by tabs.
   * 
   * The tension here is that the more I make separate
   * patterns, the slower this annotator becomes.
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
      this.performanceMeter.startCounter();

      List<Annotation> lines = UIMAUtil.getAnnotations( pJCas, Line.typeIndexID); 
   
      if ( lines != null && !lines.isEmpty()) {
      
        UIMAUtil.uniqueAnnotations(lines);
         for ( Annotation line : lines ) {
           int offset = line.getBegin();
           // process by tabs 
           String lineBuff = line.getCoveredText();
           if ( lineBuff != null && lineBuff.trim().length() > 0 ) { 
             String tabbedColumns[] = U.split( lineBuff, "\t");
           
             if ( tabbedColumns != null && tabbedColumns.length > 0 ) 
               for ( int i = 0; i < tabbedColumns.length; i++ ) {
                 findShapesInColumn ( pJCas, tabbedColumns[i], offset, offset + tabbedColumns[i].length() ) ;
                 offset+= tabbedColumns[i].length() + 1; // +1 for each tab
               }
           } // if line isn't empty
         } // end loop thru line annotations 
      } // end if there are lines
       
      List<Annotation> annotations = UIMAUtil.getAnnotations(pJCas, Shape.typeIndexID, true);
     UIMAUtil.uniqueAnnotations(annotations) ;
  } catch (
    
      Exception e) {
        e.printStackTrace();
        String msg = "Issue with one of the shapes " + e.toString();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
      }
        
    
  
  } // end Method process() ----------------
   
  
  
  // =================================================
  /**
   * findShapesInColumn
   * 
   * @param pJCas
   * @param pColumn
   * @param pBeginOffset
   * @throws Exception 
   */
  // =================================================
  private void findShapesInColumn(JCas pJCas, String pColumn, int pBeginOffset, int pEndOffset) throws Exception {

  
   

    findShape( pJCas, this.personRegEx,  Person.class, pColumn, pBeginOffset);
    findShape( pJCas, this.emailRegEx, Email.class, pColumn, pBeginOffset);
   // findShape(pJCas,  this.urlRegEx, URL.class, pColumn, pBeginOffset);
    findShape(pJCas,  this.phoneRegex, PhoneNumber.class, pColumn, pBeginOffset);
    findShape(pJCas,  this.zipCodeRegex, Zipcode.class, pColumn, pBeginOffset);
    findShape(pJCas,  this.numberRegEx,  PotentialNumber.class,  pColumn, pBeginOffset);
    findShape(pJCas,  this.ssnRegEx,  SSN.class,  pColumn, pBeginOffset);
    findShape(pJCas,  this.pageNoRegEx,  PageNumber.class,  pColumn, pBeginOffset);
    findShape(pJCas,  this.identifierRegEx,  Identifier.class,  pColumn, pBeginOffset);
    
  
    /* now done in TermShapeAnnotator() 
    
    if (foundNumber) {
      findNumberRangeShape(pJCas, pColumn, pBeginOffset, pEndOffset);
      findUnitsOfMeasureShape(pJCas, pColumn, pBeginOffset, pEndOffset);
    }
    */

  } // end Method processShapesInColumn() -----------

 
  
 //=================================================
 /**
  * findShape
  * 
  * @param pJCas
  * @param pPatterns
  * @param pColumn
  * @param pBeginOffset
  * @return boolean    true if a shape or more is found
  * @throws Exception
  */
 // =================================================
 private final boolean findShape(JCas pJCas, Pattern pPatterns, Class<?> pClass, String pColumn, int pBeginOffset) throws Exception {

   boolean found = false;
   try {
     
    
   
     Matcher matcher = pPatterns.matcher( pColumn ); 
     
     while (matcher.find()) {
       String matchedText = pColumn.substring(matcher.start(), matcher.end()) ;
       
       if ( matchedText == null || matchedText.trim().length() == 0 ) {
         return false;
       }
        
       // capture the focus term in the pattern 
       
       int numberOfGroups = matcher.groupCount();
       int focusOffsetBegin = -1;
       for (int i = 0; i < numberOfGroups; i++) {
           
         String focusTerm = null;
         if ( numberOfGroups == 1 )
           focusTerm = matcher.group();
         
         else if ( i < numberOfGroups -1  )
           focusTerm = matcher.group(i + 1);
         else
           focusTerm = matcher.group();
        
         if ( focusTerm == null ) 
           focusTerm = matcher.group();
         
         
         focusOffsetBegin = matcher.group().indexOf( focusTerm) + matcher.start() + pBeginOffset ;
         int focusOffsetEnd   = focusOffsetBegin + focusTerm.length();
       
         if ( !overlapsWithExistingTerm( pJCas,focusOffsetBegin, focusOffsetEnd  ) ) {
           createAnnotation(pJCas, pClass, focusOffsetBegin, focusOffsetEnd );
           found = true;
           break;
         }
       } // end capture the focusTerm 
        
     } // end loop through matches to this string
      
   } catch (Exception e) {
     e.printStackTrace();
     String msg = "Issue with regex shape detection " + e.toString();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
     throw new Exception ( msg );
   }
   
   return found;
 } // end Method findEmailShape() -------------------


  // =================================================
/**
 * overlapsWithExistingTerm returns true if
 * this span overlaps with another lexical element
 * 
 * @param pJCas
 * @param pBegin
 * @param pBen
 * @return boolean
*/
// =================================================
private final  boolean overlapsWithExistingTerm(JCas pJCas, int pBegin, int pEnd) {
  
  boolean returnVal = false;
  
  List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  LexicalElement.typeIndexID, pBegin,  pEnd);
  
  if ( terms != null && !terms.isEmpty())
    returnVal = true;
  return returnVal;
} // end Method notOverlappingWithExistingTerm() ---------



  // -----------------------------------------
  /**
   * createAnnotation will create an annotation of the class pClassType.
   *
   * @param pJCas
   *          the j cas
   * @param pClassName
   *          the class name
   * @param pBeginOffset
   *          the begin offset
   * @param pEndOffset
   *          the end offset
   */
  // -----------------------------------------
  private void createAnnotation(JCas pJCas, Class<?> pClassName, int pBeginOffset, int pEndOffset) {

    try {
      Constructor<?> c = pClassName.getConstructor(new Class[] { JCas.class });
      Object statement = c.newInstance(pJCas);

      ((Annotation) statement).setBegin(pBeginOffset);
      ((Annotation) statement).setEnd(pEndOffset);
      ((VAnnotation) statement).setId(this.getClass().getCanonicalName() + ":createAnnotation_" + annotationCounter++);

      ((Annotation) statement).addToIndexes(pJCas);

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createAnnotation", "Something went wrong here " + e.toString());
    }

  } // end Method createAnnotation() ---

// ----------------------------------
/**
* destroy.
*/
// ----------------------------------
@Override
public void destroy() {
  this.performanceMeter.writeProfile(this.getClass().getSimpleName());
}

  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param pContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext pContext) throws ResourceInitializationException {
       
    String args[] = null;
    
    try {
    
      args                 = (String[]) pContext.getConfigParameterValue("args");  
      initialize( args );
      
    } catch (Exception e ) {
      GLog.println( GLog.ERROR_LEVEL," Issue with " + e.toString());
      throw new ResourceInitializationException();
    }
    
    
  } // end Method initialize() -------
  
  
  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    this.emailRegEx = Pattern.compile( _emailRegEx, Pattern.CASE_INSENSITIVE);
    this.numberRegEx = Pattern.compile(_numberRegex, Pattern.CASE_INSENSITIVE );
    this.zipCodeRegex = Pattern.compile(_zipCodeRegex, Pattern.CASE_INSENSITIVE);
    this.phoneRegex = Pattern.compile(_phoneRegEx, Pattern.CASE_INSENSITIVE);
    this.urlRegEx = Pattern.compile( _urlRegEx, Pattern.CASE_INSENSITIVE);
    this.personRegEx = Pattern.compile( _personRegEx, Pattern.CASE_INSENSITIVE);
    this.ssnRegEx = Pattern.compile( _ssnRegEx, Pattern.CASE_INSENSITIVE);
    this.pageNoRegEx = Pattern.compile( _pageNoRegEx, Pattern.CASE_INSENSITIVE);
    this.identifierRegEx = Pattern.compile( _identifierRegEx, Pattern.CASE_INSENSITIVE);
   
  } // end Method initialize() -------
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
 
  protected int annotationCounter = 0; // new Term Counter.
  private ProfilePerformanceMeter performanceMeter = null;
  Pattern personRegEx = null;
  Pattern emailRegEx = null;
  Pattern numberRegEx = null;
  Pattern urlRegEx = null;
  Pattern phoneRegex = null;
  Pattern zipCodeRegex = null;
  Pattern ssnRegEx = null;
  Pattern pageNoRegEx = null;
  Pattern identifierRegEx = null;
   
  
  
  
  private static final String _zipCodeRegex = "^[0-9]{5}(?:-[0-9]{4})?$";
  private static final String _numberRegex = 
      
                               "\\b(\\d+\\.\\d+)[A-z]{1,4}\\b|" +   // 1.30MG 
                               "\\b(\\d+)[A-z]{1,4}\\b|" +   // 30MG 
                               "\\b(\\d+\\.\\d+)[A-z]{1,4}\\/[A-z]{1,4}\b|" + //  1.03mmol/L
      
                               "\\b\\${0,1}(\\d+\\.\\d+)\\b|" +           // real $ 1.2
                               "\\b(\\d{1,2}\\.\\d+)%{0,1}\\b|" +           // real 1.2 %
                               "\\b(\\d+\\s{0,2}\\.\\d+)\\b|" +         // real 1 .2  
                               "\\b(\\d+\\s{0,1}\\.\\s{0,1}\\d+)\\b|" +  // real 1 . 2 
                               "\\b\\${0,1}(\\d{1,3}\\,\\d{3}\\,\\d{3})\\b|\\b\\${0,1}(\\d{1,3}\\,\\d{3})\\b|" +  // integer thousands 1,000,000 
                               "\\b(\\d{1,2}/\\d)\\b|" +  //  fractions 1/2
                               "\\s(\\.\\d+)\\b|" +   // real numbers that start with .xxxx
                               "\\b\\$(\\d+)\\b|" +
                               "\\b(\\d+)\\b|" + 
                               "\\b(\\d+\\%)\\b|" +
                               "\\b(\\d+\\.\\d+)\\b" ;//  3.5-
                               // --------------------------------- might break these into a separate regex  ----------------
                              
  
                              
                               // 12/12/08   <---- picking this up
                               // missing 126/74  because that's blood pressure    
                               // missing 0.03  in the 03.mg/0.3MG)
                               // getting too much .3MG  
  
  
  private static final String _phoneRegEx = "(.?\\d{3}.\\s?\\d{3}.\\d{4}|\\b\\d{3}[\\s-]\\d{4}\\b)";
      // ->   didn't work so good  "(\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4})";
  
  
  
  private static final String _ssnRegEx =  "^(?!000|666)[0-8][0-9]{2}-(?!00)[0-9]{2}-(?!0000)[0-9]{4}$";
  
  private static final String _emailRegEx = "([A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6})";
  
  private static final String _urlRegEx = "((http://|https://)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?)";
  
  private static final String _personRegEx1  = "(\\[\\s*First Name = \\(\\d+ - Subject \\d+ \\) \\])";
  private static final String _personRegEx2  = "(\\[\\s*Last Name = \\(\\d+ - Subject \\d+ \\) \\])";
  private static final String _personRegEx3  = "(\\[\\s*Nickname = \\(\\d+ - Subject \\d+ \\) \\])";
  
  private static final String _pageNoRegEx1a = "(\\bpage\\s+\\d+\\s+\\bof\\s+\\d+)";
  private static final String _pageNoRegEx1b = "(\\bpg\\s+\\d+\\s+\\bof\\s+\\d+)";
  private static final String _pageNoRegEx1c = "(\\bexhibit\\s+\\d+\\s+\\bof\\s+\\d+)";  
  private static final String _pageNoRegEx1d = "(\\bp\\.\\s+\\d+\\s+\\bof\\s+\\d+)"; 
  private static final String _pageNoRegEx1e = "(\\bp\\.\\s+\\d+\\s+\\b/\\s+\\d+)"; 
  
  private static final String _pageNoRegEx1  = _pageNoRegEx1a + "|" + _pageNoRegEx1b + "|" + _pageNoRegEx1c + "|" + _pageNoRegEx1d + "|" + _pageNoRegEx1e ;
  
  private static final String _pageNoRegEx2a = "(\\bpage\\s+\\d+)";
  private static final String _pageNoRegEx2b = "(\\bpg\\s+\\d+)";
  private static final String _pageNoRegEx2c = "(\\bexhibit\\s+\\d+)"; 
  private static final String _pageNoRegEx2d = "(\\bexhibit no\\.\\s+\\d+)"; 
  private static final String _pageNoRegEx2  = _pageNoRegEx2a + "|" + _pageNoRegEx2b + "|" + _pageNoRegEx2c + "|" + _pageNoRegEx2d;
  
  private static final String _pageNoRegEx3 = "pageno \\d+";
  
  
  private static final String _personRegEx = "(" + _personRegEx1  + "|" + _personRegEx2 + "|" + _personRegEx3 + ")";
  private static final String _pageNoRegEx = "(" + _pageNoRegEx1  + "|" + _pageNoRegEx2 + "|" + _pageNoRegEx3 + ")";
  private static final String _identifierRegEx = "(\\d{5,20})";
  
  
} // end Class RegExShapeAnnotator() ---------------
