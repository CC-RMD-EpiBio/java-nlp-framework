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
/*
 *
 */
/**
 * BratAnnotationContainer holds the parts of a Brat
 * set of annotations for a given file
 *
 * @author     Guy Divita
 * @created    May 3, 2018
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.brat;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.jcas.tcas.Annotation;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.FeatureValuePair;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

/**
 * The Class BratAnnotationsContainer.
 *
 * @author guy
 */
public class BratAnnotationsContainer {

  /** The file name. */
  private String fileName;

  /** The document text. */
  private String documentText;

  /** The labels. */
  private Set<String> labels = null;

  /** The attributes. */
  private Set<String> attributes = null;

  /** The entities. */
  private List<BratAnnotation> entities = null;

  // =================================================
  /**
   * Constructor.
   *
   * @param pFileName (sans the extension)
   */
  // =================================================
  public BratAnnotationsContainer(String pFileName) {
    this.fileName = pFileName;
    this.labels = new HashSet<String>();
    this.attributes = new HashSet<String>();
    this.entities = new ArrayList<BratAnnotation>();

  } // end Constructor() ----------------------------

  // =================================================
  /**
   * setText.
   *
   * @param pDocumentText the text
   */
  // =================================================
  public void setText(String pDocumentText) {
    this.documentText = pDocumentText;

  } // end Method setText() ---------------------------

  // =================================================
  /**
   * addEntity .
   *
   * @param pAnnotation the annotation
   */
  // =================================================
  public void addEntity(Annotation pAnnotation) {

    String name = U.getNameWithoutNameSpace(pAnnotation.getClass().getName());
    String snippet = U.normalize(pAnnotation.getCoveredText());
    List<FeatureValuePair> featuresAndValues = UIMAUtil.getFeatureValuePairs(pAnnotation);

    BratAnnotation entity = new BratAnnotation(name, pAnnotation.getBegin(), pAnnotation.getEnd(),
        snippet, featuresAndValues);

    this.entities.add(entity);

  }

  // =================================================
  /**
   * write .
   *
   * @param pOutputDir the output dir
   */
  // =================================================
  public void write(String pOutputDir) {

    writeText(pOutputDir);
    String fileName = pOutputDir + "/" + this.fileName + ".ann";
    try (final PrintWriter out = new PrintWriter(fileName);) {

      for (BratAnnotation entity : this.entities) {
        out.print(entity.toString()); // <---- includes the \n
        out.print(entity.formatNote()); // <----- includes the \n

      }

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue writing the brat annotations out " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, msg);
    }

  }

  // =================================================
  /**
   * writeText -writes the text - it does not throw an error if it fails.
   *
   * @param pOutputDir the output dir
   */
  // =================================================
  private final void writeText(String pOutputDir) {

    try (final PrintWriter out = new PrintWriter(fileName);) {
      out.print(documentText);
      out.close();

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue trying to write out the text file : " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, msg);
    }

  } // end Method writeText() -----------------------

  // =================================================
  /**
   * getLabels .
   *
   * @return Set<String>
   */
  // =================================================

  public final Set<String> getLabels() {
    return this.labels;
  } // end Method getLabels() ------------------------

  // =================================================
  /**
   * addAttributes .
   *
   * @param pAttributes the attributes
   */
  // =================================================
  public void addAttributes(List<String> pAttributes) {

    if (pAttributes != null && !pAttributes.isEmpty())
      for (String attribute : pAttributes)
        this.attributes.add(attribute);

  } // end Method addAttributes() --------------------

} // end Class BratAnnotationsContainer() ----------
