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
 * AnnotatorStatsTableRow is a container for performance stats
 *
 * @author  guy
 * @created Sep 10, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline;

/**
 * The Class AnnotatorStatsTableRow.
 */
public class AnnotatorStatsTableRow implements Comparable<AnnotatorStatsTableRow> {

  /** The order. */
  public int order;

  /** The annotator name. */
  public String annotatorName;

  /** The number of threads. */
  public int numberOfThreads;

  /** The initialization time. */
  public int initializationTime;

  /** The total time in minutes. */
  public long totalTimeInMinutes;

  /** The total time in milli seconds. */
  public long totalTimeInMilliSeconds;

  /** The percent time. */
  public double percentTime;

  /** The percent initialization time. */
  public double percentInitializationTime;

  /** The max Q. */
  public int maxQ;

  /* see superclass */
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(AnnotatorStatsTableRow pRow) {
    int returnVal = 0;

    returnVal = this.order - pRow.order;

    return returnVal;
  } // End Method compareTo() ======================

}
