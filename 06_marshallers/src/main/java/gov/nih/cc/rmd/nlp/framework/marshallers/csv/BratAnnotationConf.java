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
 * BratAnnotationConf is a container to house those
 * things that go into a brat annotation conf file.
 * 
 * @author     Guy Divita
 * @created    May 3, 2018
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.csv;

import java.io.PrintWriter;
import java.util.HashSet;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * The Class BratAnnotationConf.
 *
 * @author guy
 */
public final class BratAnnotationConf {

  /** The entities. */
  private HashSet<String> entities = null;

  /** The relationships. */
  private HashSet<String> relationships = null;

  /** The events. */
  private HashSet<String> events = null;

  /** The attributes. */
  private HashSet<String> attributes = null;

  /** The output file. */
  private String outputFile = "annotation.conf";

  // =================================================
  /**
   * Constructor.
   *
   * @param pOutputDir the output dir
   */
  // =================================================
  public BratAnnotationConf(String pOutputDir) {

    initialize(pOutputDir);
  }

  // =================================================
  /**
   * addLabel adds the label to the entities list.
   *
   * @param pLabel the label
   */
  // =================================================
  public final void addLabel(String pLabel) {
    entities.add(pLabel);

  }

  // =================================================
  /**
   * write writes out the annotations.conf file
   *
   * @throws Exception the exception
   */
  // =================================================
  public final void write() throws Exception {

    try (final PrintWriter out = new PrintWriter(this.outputFile);) {
      out.print("[entities]\n\n");
      for (String entity : entities)
        out.print(entity + '\n');

      out.print("\n[relations]\n");
      for (String relation : this.relationships)
        out.print(relation + '\n');

      out.print("\n[events]\n");
      for (String event : this.events)
        out.print(event + "\n");

      out.print("\n[attributes]\n");
      for (String attribute : this.attributes)
        out.print(attribute + "\n");

      String date = U.getDateStampSimple();
      out.print("\n\n");
      out.print("# Created by Ciitizen marshallers.brat.BratWriter on " + date + "\n");
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue trying to write out the annotations conf file " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, msg);
      throw new Exception(msg);
    }

  } // end Method write() ----------------------------

  // =================================================
  /**
   * initialize.
   *
   * @param pOutputDir the output dir
   */
  // =================================================
  private final void initialize(String pOutputDir) {

    entities = new HashSet<String>();
    relationships = new HashSet<String>();
    events = new HashSet<String>();
    attributes = new HashSet<String>();

    outputFile = pOutputDir + "/annotation.conf";

  } // end Method initialize() ---------------------

} // end Class BratAnnotationConf -----------------
