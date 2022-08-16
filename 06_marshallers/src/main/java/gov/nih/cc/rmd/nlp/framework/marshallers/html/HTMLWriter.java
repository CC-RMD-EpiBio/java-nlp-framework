/*
 *
 */
/**
 * ToDatabase is a CAS Consumer that transforms
 * the vinciNLPFramework encoded CAS into a database through
 * jdbc calls to pre-existing database tables that conform to
 * the database schema common to framework and chartReader.
 * 
 * Make sure you have the path to your jdbc driver in the classpath.
 * 
 *   The following parameters are found in the toDatabase.xml configuration file:
 *   
 *      jdbcDriver        |
 *      databaseUserName  |  
 *      databaseName      |
 *      databasePassword  |
 *      jdbcConnectString | Note: this is devoid of the databaseName, userName,databasePassword, which are taken from
 *                                the other parameters
 *
 *
 *   Can filter to/out annotation labels via
 *      includeLabels      parameter set in the toDatabase.xml 
 *                         a list of full namespace labels (one per line)
 *      excludedLabels     a list of full namespace labels (one per line)
 *    
 *    This should be an either/or not both
 *
 * @author  Guy Divita 
 * @created June 26, 2012
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

// import edu.emory.mathcs.backport.java.util.Arrays;  
import gov.va.chir.model.DocumentHeader;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

/**
 * HTML Writer, used to set the "html" attribute of the JsonAnnotationsObject.
 */
public class HTMLWriter extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter {

  /** The output types hash. */
  private HashSet<String> outputTypesHash;

  /** The output types. */
  private String[] outputTypes;

  /** The output types L. */
  private ArrayList<String> outputTypesL;

  // =======================================================
  /**
   * Constructor ToVTT creates a vtt writer.
   *
   * @param pArgs the args
   * @throws ResourceInitializationException the resource initialization
   *           exception
   */
  // =======================================================
  public HTMLWriter(String[] pArgs) throws ResourceInitializationException {

    this.initialize(pArgs);

  } // end Constructor() ---------------------

  // -----------------------------------------
  /**
   * process iterates through all annotations, filters out those that should be
   * filtered out, then pushes them into a database store.
   * 
   * Each document should include a DocumentAnnotation annotation and a
   * documentHeader annotation for re-animation purposes.
   *
   * @param pJCas the j cas
   * @throws AnalysisEngineProcessException the analysis engine process
   *           exception
   */
  // -----------------------------------------
  @Override
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    // ------------------------
    // get document meta data
    DocumentHeader documentHeader = VUIMAUtil.getDocumentHeader(pJCas);
    String documentId = documentHeader.getDocumentId();

    // ------------------------
    // Create a html doc
    // set the css definitions
    // render the cas into it
    // write the doc out

    HTMLDocSimple htmlDoc = new HTMLDocSimple();

    // ---------------------
    // Set the metadata for the htmlDoc
    htmlDoc.setOutputDir(this.outputDir);
    htmlDoc.setFileName(documentId);
    htmlDoc.setMetaData(documentHeader);

    processHTMLDoc(pJCas, htmlDoc);
    String outputFile = htmlDoc.write();
    documentHeader.setDocumentPath(outputFile);

  } // end Method process

  // ==========================================
  /**
   * processHTMLDoc.
   *
   * @param pJCas the j cas
   * @param htmlDoc the html doc
   */
  // ==========================================
  public void processHTMLDoc(JCas pJCas, HTMLDocSimple htmlDoc) {
    String documentText = pJCas.getDocumentText();
    List<Annotation> allAnnotations = getAnnotations(pJCas);
    processHTMLDoc(documentText, allAnnotations, htmlDoc);
  }

  /**
   * Process api.
   *
   * @param jcas the jcas
   * @return the string
   */
  public String processApi(final JCas jcas) {
    final HTMLDocSimple htmlDoc = new HTMLDocSimple();
    processHTMLDoc(jcas.getDocumentText(), getAnnotations(jcas), htmlDoc);
    return htmlDoc.renderToHTML();
  }

  // ==========================================
  /**
   * processHTMLDoc.
   *
   * @param text the text
   * @param annotations the annotations
   * @param htmlDoc the html doc
   */
  // ==========================================
  @SuppressWarnings("unchecked")
  public void processHTMLDoc(final String text, final List<Annotation> annotations,
    final HTMLDocSimple htmlDoc) {
    final Set<String> annotationCategories = new HashSet<>();
    List<Span> plotSpans = new ArrayList<>();
    if (annotations != null) {
      for (final Annotation annotation : annotations) {

        // Skip document header, otherwise everything is always overlapping
        if (annotation instanceof DocumentHeader) {
          continue;
        }

        try {
          annotation.getCoveredText();
        } catch (Exception e) {
          // bad annotation, skip
          // GLog.error_println(HTMLWriter.class, "processHTMLDoc", "Bad
          // annotation");
          continue;
        }

        // -----------------------------------------------
        // Figure out the annotation labels here
        // The assumption is that the non-name-space names
        // of the classes map to the output labels
        String annotationCategory = annotation.getClass().getSimpleName();

        if (this.outputTypesHash.contains(annotationCategory)) {
         
          
          plotSpans.add(new Span(annotation.getBegin(), annotation.getEnd(), 
                  
                  Arrays.asList(new String[] { annotation.getClass().getSimpleName() }
                  
                      )));
          annotationCategories.add(annotationCategory);
        }
      } // end loop thru annotations
    }
    List<Span> newSpans = null;
    do {
      newSpans = new ArrayList<>();
      for (int i = 0; i < plotSpans.size(); i++) {
        final Span span = plotSpans.get(i);
        for (int j = i + 1; j < plotSpans.size(); j++) {
          final Span otherSpan = plotSpans.get(j);
          if (span.getBeginOffset() >= otherSpan.getBeginOffset()) {
            if (span.getBeginOffset() <= otherSpan.getEndOffset()) {
              // beginning of span is within otherSpan
              if (otherSpan.getEndOffset() < span.getEndOffset()) {
                // end of span is after end of otherSpan, classic overlap
                addSpan(newSpans, otherSpan.getBeginOffset(), span.getBeginOffset(),
                    otherSpan.getClasses());
                addSpan(newSpans, span.getBeginOffset(), otherSpan.getEndOffset(),
                    otherSpan.getClasses(), span.getClasses());
                addSpan(newSpans, otherSpan.getEndOffset(), span.getEndOffset(), span.getClasses());
              } else {
                // span is entirely within otherSpan
                addSpan(newSpans, otherSpan.getBeginOffset(), span.getBeginOffset(),
                    otherSpan.getClasses());
                addSpan(newSpans, span.getBeginOffset(), span.getEndOffset(),
                    otherSpan.getClasses(), span.getClasses());
                addSpan(newSpans, span.getEndOffset(), otherSpan.getEndOffset(),
                    otherSpan.getClasses());
              }
            }
          } else if (otherSpan.getBeginOffset() < span.getEndOffset()) {
            // beginning of otherSpan is within span
            if (span.getEndOffset() < otherSpan.getEndOffset()) {
              // end of otherSpan is after end of span, classic overlap
              addSpan(newSpans, span.getBeginOffset(), otherSpan.getBeginOffset(),
                  span.getClasses());
              addSpan(newSpans, otherSpan.getBeginOffset(), span.getEndOffset(), span.getClasses(),
                  otherSpan.getClasses());
              addSpan(newSpans, span.getEndOffset(), otherSpan.getEndOffset(),
                  otherSpan.getClasses());
            } else {
              // otherSpan is entirely within span
              addSpan(newSpans, span.getBeginOffset(), otherSpan.getBeginOffset(),
                  span.getClasses());
              addSpan(newSpans, otherSpan.getBeginOffset(), otherSpan.getEndOffset(),
                  span.getClasses(), otherSpan.getClasses());
              addSpan(newSpans, otherSpan.getEndOffset(), span.getEndOffset(), span.getClasses());
            }
          }
          if (!newSpans.isEmpty()) {
            for (final Span s : plotSpans) {
              if (!s.equals(span) && !(s.equals(otherSpan))) {
                newSpans.add(s);
              }
            }
            break;
          }
        }
        if (!newSpans.isEmpty()) {
          break;
        }
      }
      if (!newSpans.isEmpty()) {
        plotSpans = newSpans;
      }
    } while (!newSpans.isEmpty());
    // Arrange in order
    Collections.sort(plotSpans, new Comparator<Span>() {
      @Override
      public int compare(Span o1, Span o2) {
        if (o1 != null) {
          if (o2 != null) {
            return o1.getBeginOffset() - o2.getEndOffset();
          } else {
            return 1;
          }
        } else if (o2 != null) {
          return -1;
        }
        return 0;
      }
    });
    // create the head
    final Set<String> classes = plotSpans.stream().map(s -> s.getClasses())
        .flatMap(Collection::stream).collect(Collectors.toSet());
    final List<float[]> hslColors = getContrastingColors(classes.size());
    final Map<String, float[]> classColors = new HashMap<>();
    int i = 0;
    for (final String clazz : classes) {
      classColors.put(clazz, hslColors.get(i++));
    }
    for (final Span span : plotSpans) {
      if (span.getClasses().size() > 1) {
        String combo = String.join("-", span.getClasses());
        if (!classColors.containsKey(combo)) {
          classColors.put(combo, new float[] {
              0f, 0f, 60f
          });
        }
      }
    }
    final StringBuilder cssHead = new StringBuilder();
    for (final Map.Entry<String, float[]> cc : classColors.entrySet()) {
      final String textColor = cc.getValue()[0] < 180 ? "black" : "white";
      cssHead.append("." + cc.getKey() + " { background-color: hsl(" + cc.getValue()[0] + ", "
          + cc.getValue()[1] + "%, " + cc.getValue()[2] + "% ); color: " + textColor + "; }\n");
      i++;
    }
    htmlDoc.addHead("<style>\n" + cssHead.toString() + "</style>");

    // create the body
    StringBuilder body = new StringBuilder();
    int prevEnd = 0;
    for (final Span span : plotSpans) {
      if (span.getBeginOffset() > prevEnd) {
        body.append(text.substring(prevEnd, span.getBeginOffset()));
      }
      final String classesStr = String.join("-", span.getClasses());
      body.append("<span class=\"" + classesStr + "\" title=\"" + classesStr + "\">");
      body.append(text.subSequence(span.getBeginOffset(), span.getEndOffset()));
      body.append("</span>");
      prevEnd = span.getEndOffset();
    }
    if (prevEnd < text.length()) {
      body.append(text.substring(prevEnd));
    }
    htmlDoc.setBody(body.toString());

  } // end Method processHTMLDoc() ===========

  /**
   * Adds the span.
   *
   * @param spans the spans
   * @param beginOffset1 the begin offset 1
   * @param beginOffset2 the begin offset 2
   * @param classes the classes
   */
  private void addSpan(final List<Span> spans, final int beginOffset1, final int beginOffset2,
    final List<String> classes) {
    if (beginOffset1 != beginOffset2) {
      final List<String> copyClasses = new ArrayList<>(classes);
      spans.add(new Span(beginOffset1, beginOffset2, copyClasses));
    }
  }

  /**
   * Adds the span.
   *
   * @param spans the spans
   * @param beginOffset1 the begin offset 1
   * @param beginOffset2 the begin offset 2
   * @param classes1 the classes 1
   * @param classes2 the classes 2
   */
  private void addSpan(final List<Span> spans, final int beginOffset1, final int beginOffset2,
    final List<String> classes1, final List<String> classes2) {
    if (beginOffset1 != beginOffset2) {
      final List<String> combined = new ArrayList<>(classes1);
      combined.addAll(classes2);
      spans.add(new Span(beginOffset1, beginOffset2, combined));
    }
  }

  /**
   * Generates contrasting HSL colors.
   * 
   * @param numColors the number of colors needed
   * @return an array of floats, representing hue in degrees (0 - 360),
   *         saturation % (0 - 100), and luminance % (0 - 100)
   */
  private static List<float[]> getContrastingColors(final int numColors) {
    final float saturation = 100;
    final int luminance = 50;
    final Random random = new Random(0);
    float currentHue = random.nextFloat();
    List<float[]> colors = new ArrayList<>(numColors);

    float invGoldenRatio = 0.6180340f;

    for (int i = 0; i < numColors; i++) {

      colors.add(new float[] {
          currentHue * 360, saturation, luminance
      });

      currentHue += invGoldenRatio;
      currentHue %= 1.0f;
    }
    return colors;
  }

  // ==========================================
  /**
   * getAnnotations returns annotations that are on the --outputTypes= uniqued,
   * in descending order.
   *
   * @param pJCas the j cas
   * @return List<Annotation>
   */
  // ==========================================
  private List<Annotation> getAnnotations(JCas pJCas) {

    List<Annotation> returnVal = null;
    List<Annotation> allAnnotations = UIMAUtil.getAnnotations(pJCas);

    if (allAnnotations != null && !allAnnotations.isEmpty()) {

      returnVal = new ArrayList<Annotation>(allAnnotations.size());
      for (Annotation annotation : allAnnotations) {
        String annotationCategory = annotation.getClass().getSimpleName();
        if (this.outputTypesHash.contains(annotationCategory))
          returnVal.add(annotation);
      }
      returnVal = UIMAUtil.uniqueAnnotations(returnVal);
      UIMAUtil.sortByOffsetDescending(returnVal);
    }

    return returnVal;

  } // end Method getAnnotations() ------------

  /**
   * The Class Span.
   */
  private static class Span {

    /** The begin offset. */
    private int beginOffset;

    /** The end offset. */
    private int endOffset;

    /** The classes. */
    private List<String> classes;

    /**
     * Instantiates a {@link Span} from the specified parameters.
     *
     * @param beginOffset the begin offset
     * @param endOffset the end offset
     * @param classes the classes
     */
    public Span(final int beginOffset, final int endOffset, final List<String> classes) {
      super();
      this.beginOffset = beginOffset;
      this.endOffset = endOffset;
      this.classes = classes;
    }

    /**
     * Returns the begin offset.
     *
     * @return the beginOffset
     */
    public int getBeginOffset() {
      return beginOffset;
    }

    /**
     * Returns the end offset.
     *
     * @return the endOffset
     */
    public int getEndOffset() {
      return endOffset;
    }

    /**
     * Returns the classes.
     *
     * @return the classes
     */
    public List<String> getClasses() {
      if (classes == null) {
        classes = new ArrayList<>();
      }
      return classes;
    }

    /* see superclass */
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "Span [beginOffset=" + beginOffset + ", endOffset=" + endOffset + ", classes="
          + classes + "]";
    }

    /* see superclass */
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + beginOffset;
      result = prime * result + ((classes == null) ? 0 : classes.hashCode());
      result = prime * result + endOffset;
      return result;
    }

    /* see superclass */
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Span other = (Span) obj;
      if (beginOffset != other.beginOffset)
        return false;
      if (classes == null) {
        if (other.classes != null)
          return false;
      } else if (!classes.equals(other.classes))
        return false;
      if (endOffset != other.endOffset)
        return false;
      return true;
    }

  }

  // ----------------------------------
  /**
   * initialize picks up the parameters needed to write a cas out to the
   * database.
   *
   * @param aContext the a context
   * @throws ResourceInitializationException the resource initialization
   *           exception
   */
  // ----------------------------------
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {

    String args[] = null;
    args = (String[]) aContext.getConfigParameterValue("args");

    initialize(args);

  } // end Method initialize() --------------

  // ----------------------------------
  /**
   * initialize picks up the parameters needed to write a cas out to the
   * database.
   *
   * @param pArgs the args
   * @throws ResourceInitializationException the resource initialization
   *           exception
   */
  // ----------------------------------
  @Override
  public void initialize(String[] pArgs) throws ResourceInitializationException {

    this.outputDir = U.getOption(pArgs, "--outputDir=", "./") + "/html";

    String outputTypez = U.getOption(pArgs, "--outputTypes=", "SectionZone:AbsoluteDate");

    this.outputTypes = U.split(outputTypez, ":");
    this.outputTypesHash = new HashSet<String>();
    this.outputTypesL = new ArrayList<String>(this.outputTypes.length);

    for (String outputType : outputTypes) {
      this.outputTypesHash.add(outputType);
      this.outputTypesL.add(outputType);
    }

    // -----------------------
    // Make this directory if it doesn't exit
    try {
      U.mkDir(this.outputDir);
    } catch (Exception e1) {
      e1.getStackTrace();
      String msg = "Issue with trying to make the html output dir " + e1.getMessage();
      System.err.println(msg);
      throw new ResourceInitializationException();
    }

  } // end Method initialize() --------------

  // -----------------------------------------
  /**
   * destroy closes the database.
   */
  // -----------------------------------------
  @Override
  public void destroy() {
    // n/a
  } // end Method destroy()

  // ----------------------------------------
  // Global variables
  /** The output dir. */
  // ----------------------------------------
  private String outputDir;

} // end Class toHTML
