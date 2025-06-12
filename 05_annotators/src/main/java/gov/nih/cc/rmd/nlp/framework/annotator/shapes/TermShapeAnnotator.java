/*******************************************************************************
 * ---------------------------------------------------------------------------
 * NIH Clinical Center
 * Department of Rehabilitation
 * Epidemiology and Biostatistics Branch
 * 2019
 * 
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * 
 * This license allows you to use, share and adapt for any purpose, provided:
 * Provide attribution to the creators of this work within your work.
 * Indicate if changes were made to this work.
 * No claim to merchantability, implied warranty, or liability can be made.
 * 
 * When attributing this code, please make reference to
 * [citation/url here] .
 * 
 * In the absence of a specific paper or url listed above, reference http://clinicalcenter.nih.gov/rmd/eb/nlp
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 ******************************************************************************/
// =================================================
/**
 * TermShapeAnnotator discovers Shape's from a combination
 * of other shapes and other looked up terms
 * 
 * numberRange
 * units of measure combined with numbers
 * 
 * This is a fourth iteration of this functionality
 * 
 * I'm limiting the scope to finding patterns within
 * a line.
 * 
 * 
 *
 *
 * @author Guy Divita
 * @created April 19, 2019
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.shapes;

import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.model.NumberRange;
import gov.nih.cc.rmd.framework.model.Shape;
import gov.nih.cc.rmd.framework.model.UnitOfMeasure;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Line;

public class TermShapeAnnotator extends JCasAnnotator_ImplBase {

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
      GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " Start " + this.getClass().getSimpleName());
      

      List<Annotation> lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID);

      if (lines != null && !lines.isEmpty()) {

        UIMAUtil.uniqueAnnotations(lines);
        for (Annotation line : lines) {
          int offset = line.getBegin();
          // process by tabs
          String lineBuff = line.getCoveredText();
          if (lineBuff != null && lineBuff.trim().length() > 0) {
            String tabbedColumns[] = U.split(lineBuff, "\t");

            if (tabbedColumns != null && tabbedColumns.length > 0) for (int i = 0; i < tabbedColumns.length; i++) {
              findShapesInColumn(pJCas, tabbedColumns[i], offset, offset + tabbedColumns[i].length());
              offset += tabbedColumns[i].length() + 1; // +1 for each tab
            }
          } // if line isn't empty
        } // end loop thru line annotations
      } // end if there are lines

    } catch (

    Exception e) {
      e.printStackTrace();
      String msg = "Issue with one of the shapes " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
    }
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " End " + this.getClass().getSimpleName());
    this.performanceMeter.stopCounter();

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

    List<Annotation> allThingsNumber = UIMAUtil.getAnnotationsBySpan(pJCas, gov.nih.cc.rmd.framework.model.Number.typeIndexID,
        pBeginOffset, pEndOffset);
    if (allThingsNumber != null && !allThingsNumber.isEmpty()) {

      findNumberRangeShape(pJCas, pColumn, pBeginOffset, pEndOffset);
      findUnitsOfMeasureShape(pJCas, pColumn, pBeginOffset, pEndOffset);
    }

  } // end Method processShapesInColumn() -----------

//=================================================
 /**
  * findUnitsOfMeasureShape
  * 
  * @param pJCas
  * @param pColumn
  * @param pBeginOffset
  */
 // =================================================
 private final void findUnitsOfMeasureShape(JCas pJCas, String pColumn, int pBeginOffset, int pEndOffset) {

 
   List<Annotation> numbers = UIMAUtil.getAnnotationsBySpan(pJCas, gov.nih.cc.rmd.framework.model.Number.typeIndexID, pBeginOffset, pEndOffset);
  
  if (numbers != null && !numbers.isEmpty()) {
    for (Annotation number : numbers ) {
     
      if (! ( (gov.nih.cc.rmd.framework.model.Number) number).getMarked() ) {
        findUnitsOfMeasureShape( pJCas, number);
        ( (gov.nih.cc.rmd.framework.model.Number) number).setMarked(true);
      }
    }
  }
 } // end Method() findUnitsOfMeasureShape() ---------------------
  
//=================================================
 /**
  * findUnitsOfMeasureShape
  * 
  * @param pJCas
  * @param pNumber
  * 
  */
 // =================================================
 private final void findUnitsOfMeasureShape(JCas pJCas, Annotation pNumber) {
   
   int nextTokenBegin = pNumber.getEnd();
   int nextTokenEnd = nextTokenBegin + 10;
   int documentLength = pJCas.getDocumentText().length();
   if ( nextTokenEnd > documentLength) nextTokenEnd = documentLength -1;
   List<Annotation> unitsOfMeasure = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  UnitOfMeasure.typeIndexID,  nextTokenBegin, nextTokenEnd);
   
   if (unitsOfMeasure != null && !unitsOfMeasure.isEmpty()) {
     createAnnotation(pJCas, UnitOfMeasure.class, pNumber.getBegin(), unitsOfMeasure.get(0).getEnd() );
     unitsOfMeasure.get(0).removeFromIndexes();
   }
   
 } // end Method findUnitsOfMeasureShape() -----------

 
  
  
  // =================================================
  /**
   * findUnitsOfMeasureShapeAux,
   * 
   * Looks for annotations in the span that are units of measure
   * Then creates an annotation of the beginOffset to the end
   * of the last unit of measure found in this span
   * 
   * @param pJCas
   * @param pColumn
   * @param pBeginOffset
   */
  // =================================================
  private final void findUnitsOfMeasureShapeAux1(JCas pJCas, String pColumn, int pBeginOffset, int pEndOffset) {

    try {
      List<Annotation> units = UIMAUtil.getAnnotationsBySpan(pJCas, UnitOfMeasure.typeIndexID, pBeginOffset, pEndOffset);

      if (units != null && !units.isEmpty()) {
        UIMAUtil.sortByOffset(units);
        int endOffset = units.get(units.size() - 1).getEnd();
        createAnnotation(pJCas, UnitOfMeasure.class, pBeginOffset, endOffset);
      }
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with units of measure shape detection " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
    }

  } // end Method findUnitsOfMeasureShapeAux1() ---------

  // =================================================
  /**
   * findUnitsOfMeasureShapeAux2,
   * 
   * Looks for annotations in the span that are units of measure
   * Then creates an annotation of the first unit of measure found to the end
   * offset
   * 
   * @param pJCas
   * @param pColumn
   * @param pBeginOffset
   */
  // =================================================
  private final void findUnitsOfMeasureShapeAux2(JCas pJCas, String pColumn, int pBeginOffset, int pEndOffset) {

    try {
      List<Annotation> units = UIMAUtil.getAnnotationsBySpan(pJCas, UnitOfMeasure.typeIndexID, pBeginOffset, pEndOffset);

      if (units != null && !units.isEmpty()) {
        UIMAUtil.sortByOffset(units);
        int beginOffset = units.get(0).getBegin();
        createAnnotation(pJCas, UnitOfMeasure.class, beginOffset, pEndOffset);
      }
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with units of measure shape detection " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
    }

  } // end Method findUnitsOfMeasureShapeAux1() ---------

  // =================================================
  /**
   * findNumberRangeShape finds two numbers that either have a - or spaces (not a tab) in them
   * 
   * @param pJCas
   * @param pColumn
   * @param pBeginOffset
   * @return boolean true if a range was found
   */
  // =================================================
  private final boolean findNumberRangeShape(JCas pJCas, String pColumn, int pBeginOffset, int pEndOffset) {

    boolean returnVal = false;
    List<Annotation> numbers = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, gov.nih.cc.rmd.framework.model.Number.typeIndexID,
        pBeginOffset, pEndOffset);

    if (numbers != null && numbers.size() > 1) {
      if (numbers.size() == 2) {
        String docText = pJCas.getDocumentText();
        // look for a dash between these numbers
        UIMAUtil.sortByOffset(numbers);
        String stuffInbetween = null;
        try {
         if ( numbers.get(1).getBegin() > numbers.get(0).getEnd() )
           stuffInbetween = docText.substring(numbers.get(0).getEnd(), numbers.get(1).getBegin());
        } catch (Exception e) {
          e.printStackTrace();
          System.err.println(" here");
        }
        
        if ((stuffInbetween != null) && (stuffInbetween.trim().equals("-") || (!stuffInbetween.contains("\t") && stuffInbetween.trim().length() == 0))) {
          makeRange(pJCas, numbers.get(0), numbers.get(1));
          returnVal = true;
        }

      } else {
        // ----
      }
    }
    return returnVal;
  } // end Method findNumberRangeShape() -----------

  // =================================================
  /**
   * makeRange
   * 
   * @param pJCas
   * @param pNumber1
   * @param pNumber2
   */
  // =================================================
  private NumberRange makeRange(JCas pJCas, Annotation pNumber1, Annotation pNumber2) {

    NumberRange statement = new NumberRange(pJCas);
    statement.setBegin(pNumber1.getBegin());
    statement.setEnd(pNumber2.getEnd());
    statement.setId("ShapesAnnotator_Range_" + annotationCounter++);
    statement.addToIndexes();

    return statement;

  }

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

      ((Shape) statement).setBegin(pBeginOffset);
      ((Shape) statement).setEnd(pEndOffset);
      ((Shape) statement).setId("TermShapesAnnotator_" + annotationCounter++);

      ((Shape) statement).addToIndexes(pJCas);

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

  // ----------------------------------
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

      args = (String[]) pContext.getConfigParameterValue("args");

    } catch (Exception e) {

    }
    initialize(args);

  } // end Method initialize() -------

  // ----------------------------------
  /**
   * initialize loads in the resources.
   * 
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {

    this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getSimpleName());

  } // end Method initialize() -------

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------

  protected int                   annotationCounter = 0;   // new Term Counter.
  private ProfilePerformanceMeter performanceMeter  = null;

} // end Class TermShapeAnnotator() ---------------
