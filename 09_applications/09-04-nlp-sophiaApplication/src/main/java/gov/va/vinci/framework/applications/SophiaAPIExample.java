// ------------------------------------------------------------
/**
 * The Sophia API Example demonstrates how to pass text into
 * the sophia pipeline, and retrieve a processed bioC|vtt|commonModel
 * document instance out.
 * 
 * @author Divita
 *         Jan 28, 2015
 * 
 *         ------------------------------------------------------------
 *
 *
 *
 *         -------------------------------------------------------------
 */
// -------------------------------------------------------------
package gov.va.vinci.framework.applications;

import gov.va.vinci.nlp.framework.pipeline.applications.FrameworkBaselineApplication;
import gov.va.vinci.nlp.framework.utils.DirTools;
import gov.va.vinci.nlp.framework.utils.U;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bioc.BioCAnnotation;
import bioc.BioCDocument;
import bioc.BioCLocation;
import bioc.BioCPassage;

// -----------------------------------
/**
 * Class Types
 *
 */
// ------------------------------------
public class SophiaAPIExample {

  // ------------------------------------------
  /**
   * main
   * See the setArgs method to see what specific command line
   * arguments should be passed in here.
   * 
   * This example expects a command line argument --inputDir=/some/dir/with/textFiles/in/it
   *
   * @param pArgs
   */
  // ------------------------------------------
  public static void main(String[] pArgs) {

    try {

      String[] args = setArgs(pArgs);
      Sophia sophia = new Sophia(args);

      String inputDir = U.getOption(args, "--inputDir=", "/data/input");

      // ------------------
      // Process a set of text documents
      // -----------------
      DirTools dirTools = new DirTools(inputDir);

      String aFileName = null;
      while ((aFileName = dirTools.getNext()) != null) {

        String inputText = U.readFile(aFileName);

        // Magic happens here -------------------------+
        // \|/
        BioCDocument doc = (BioCDocument) sophia.processAPI(FrameworkBaselineApplication.BIOC_OBJECT, inputText);

        // /|\
        // --------------------------------------------+

        // --------------------------------
        // Traverse and print out the bioC document
        prettyPrintBioCDocument(doc);

      } // End loop thru the files in the directory

      System.err.println("Finished");

    } catch (Exception e) {
      e.getStackTrace();
      String msg = "Issue processing the files " + e.toString();
      System.err.println(msg);
    }

  } // end Method main() --------------------------------------

  // =======================================================
  /**
   * prettyPrintBioCDocument [Summary here]
   * 
   * @param doc
   */
  // =======================================================
  private static void prettyPrintBioCDocument(BioCDocument doc) {

    // ------------------------------------
    // Loop thru the generic annotations found in the document rendered as bioC annotations
    List<BioCPassage> passages = doc.getPassages();

    for (BioCPassage section : passages) {
      prettyPrintAnnotationsFromSection(section);
    }

  } // End Method prettyPrintBioCDocument() ======================

  // =======================================================
  /**
   * prettyPrintAnnotationsFromSEction pretty prints the annotations for each passage (a.k.a. section/paragraph)
   *
   * @param pSection
   * 
   */
  // =======================================================
  private static void prettyPrintAnnotationsFromSection(BioCPassage pSection) {

    List<BioCAnnotation> boiCAnnotations = pSection.getAnnotations();
    if (boiCAnnotations != null && boiCAnnotations.size() > 0) {
      for (BioCAnnotation annotation : boiCAnnotations) {
        List<BioCLocation> locations = annotation.getLocations();
        BioCLocation location = locations.get(0);
        ArrayList<String> attributesPairs = null;
        String annotationId = annotation.getID();
        String annotationText = annotation.getText();

        int beginOffset = location.getOffset();
        int endOffset = beginOffset + location.getLength();
        Map<String, String> infons = annotation.getInfons();
        String annotationType = "Annotation";

        if (infons != null && infons.size() > 0) {
          Set<String> attributeKeys = infons.keySet();
          attributesPairs = new ArrayList<String>();
          for (String attributeKey : attributeKeys) {
            String attributeValue = infons.get(attributeKey);

            if (attributeKey.equals("Type")) annotationType = attributeValue;
            else attributesPairs.add(attributeKey + "|" + attributeValue);
          } // end loop through the infons for this annotation
        } // end if there are any infons

        prettyPrintAnnotation(annotationId, annotationType, annotationText, beginOffset, endOffset, attributesPairs); // <------
                                                                                                                      // distilled
                                                                                                                      // annotation

      } // end loop thru annotations
    }// end if there are any annotations

  } // End Method convertAnnotations() ======================

  // =======================================================
  /**
   * prettyPrintAnnotation
   * 
   * @param pAnnotationId
   * @param pAnnotationType
   * @param pBeginOffset
   * @param pEndOffset
   * @param pAttributeValuePairs
   */
  // =======================================================
  private static void prettyPrintAnnotation(String pAnnotationId, String pAnnotationType, String pAnnotationText,
      int pBeginOffset, int pEndOffset, ArrayList<String> pAttributeValuePairs) {

    // ----------------------------------------------
    // Write out the annotation as annotationId|AnnotationType|content|beginOffset|EndOffset|attribute1=value1|attribute2=value2
    // ....|
    // ----------------------------------------------

    StringBuffer buff = new StringBuffer();

    buff.append(pAnnotationId);
    buff.append(FIELD_DELIMITER);
    buff.append(pAnnotationType);
    buff.append(FIELD_DELIMITER);
    buff.append(pAnnotationText);
    buff.append(FIELD_DELIMITER);

    buff.append(prettyPrintAttributeValuePairs(pAttributeValuePairs)); // <--- no need to add a delimiter here

    System.out.println(buff.toString());

  } // End Method createAnnotation() ======================

  // =======================================================
  /**
   * prettyPrintAttributeValuePairs
   * 
   * @param pAttributeValuePairs
   * @return String (field delimited name=value pairs)
   */
  // =======================================================
  private static String prettyPrintAttributeValuePairs(ArrayList<String> pAttributeValuePairs) {

    StringBuffer attributeValues = new StringBuffer();
    for (String aFeature : pAttributeValuePairs) {

      String[] cols = U.split(aFeature);
      String featureName = cols[0];
      String featureValue = cols[1];
      attributeValues.append(featureName);
      attributeValues.append("=");
      attributeValues.append(featureValue);
      attributeValues.append(FIELD_DELIMITER);
    }

    return attributeValues.toString();
    // End Method prettyPrintAttributeValuePairs() ======================
  }

  // ------------------------------------------
  /**
   * setArgs These are arguments that can be set on the command line
   * to pass in input, output directories, what resulting output labels
   * will be in the output files. What parameters are needed for
   * specific annotators.
   *
   * @return String[]
   */
  // ------------------------------------------
  private static String[] setArgs(String pArgs[]) {

    String inputDir = U.getOption(pArgs, "--inputDir=", "/data/input/i2b2");

    String args[] = { "--inputDir=" + inputDir

    };

    return args;

  } // End Method setArgs() -----------------------

  public static String FIELD_DELIMITER = "|";

} // End SophiaApplication Class -------------------------------
