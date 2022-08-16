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
 * BratAnnotationConf is a container to house those
 * things that go into a brat annotation conf file.
 * 
 * @author     Guy Divita
 * @created    May 3, 2018
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.brat;

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
