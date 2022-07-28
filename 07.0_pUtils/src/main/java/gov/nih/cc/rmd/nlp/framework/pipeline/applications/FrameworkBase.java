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
 * BaseFrameworkBase
 *
 * @author  guy
 * @created Sept 3, 2016
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;

import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;

/**
 * The Class FrameworkBase.
 */
public class FrameworkBase {

  /** The args. */
  protected String[] args = null;

  /** The output dir. */
  // protected static String staticInputDir = null;
  protected String outputDir = null;

  /** The log dir. */
  protected String logDir = null;

  /** The server name. */
  protected String serverName = "defaultName";

  /** The writers. */
  protected Set<Writer> writers = null;

  /** The analysis engine. */
  protected AnalysisEngine analysisEngine = null;

  /** The number to process. */
  protected long numberToProcess = Integer.MAX_VALUE;

} // End Class FrameworkBase
