/*******************************************************************************
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                       Epidemiology and Biostatistics Branch 
 *                                            2019 - 2022
 *   ---------------------------------------------------------------------------
 *   Copyright Notice:
 *   This software was developed and funded by the National Institutes of Health
 *   Clinical Center (NIHCC), part of the National Institutes of Health (NIH),
 *   and agency of the United States Department of Health and Human Services,
 *   which is making the software available to the public for any commercial
 *   or non-commercial purpose under the following open-source BSD license.
 *  
 *   Government Usage Rights Notice:
 *   The U.S. Government retains unlimited, royalty-free usage rights to this 
 *   software, but not ownership, as provided by Federal law. Redistribution 
 *   and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *      1. Redistributions of source code must retain the above copyright
 *         and government usage rights notice, this list of conditions and the 
 *         following disclaimer.
 *  
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *        
 *      3. Neither the names of the National Institutes of Health Clinical
 *         Center, the National Institutes of Health, the U.S. Department of
 *         Health and Human Services, nor the names of any of the software
 *         developers may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
 *   
 *      4. The U.S. Government retains an unlimited, royalty-free right to
 *         use, distribute or modify the software.
 *   
 *      5. Please acknowledge NIH CC as the source of this software by including
 *         the phrase: "Courtesy of the U.S. National Institutes of Health Clinical Center"
 *          or 
 *                     "Source: U.S. National Institutes of Health Clinical Center."
 *  
 *     THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS
 *     IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *     TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *     PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 *     OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *     EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *     PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *     NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *     When attributing this code, please make reference to:
 *        Divita G, Carter ME, Tran LT, Redd D, Zeng QT, Duvall S, Samore MH, Gundlapalli AV. 
 *        v3NLP Framework: tools to build applications for extracting concepts from clinical text. 
 *        eGEMs. 2016;4(3). 
 *      
 *     In the absence of a specific paper or url listed above, reference https://github.com/CC-RMD-EpiBio/java-nlp-framework
 *   
 *     To view a copy of this license, visit https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD
 *******************************************************************************/
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
