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
 * ScaleOutAnnotatorPerformanceStats accumulates performance statistics for each multi-threaded annotator
 *   Upon the destroy() method for each scaled out annotator, this write method will create
 *   a file (in the logs/annotators dir) that has the number of threads and cumulative time for this annotator in it.
 *   
 *   Upon the termination of the application, the analyze method is called, reads in each of the
 *   files in the annotators dir, computes a table that has the annotator|numThreads|% time in it 
 *   (maybe sorted by % time?) 
 *
 * @author  divita
 * @created Sep 10, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * The Class ScaleOutAnnotatorPerformanceStats.
 */
public class ScaleOutAnnotatorPerformanceStats {

  /** The annoatator performance stats. */
  private static String annoatatorPerformanceStats = "annoatatorPerformanceStats";

  /** The stats suffix. */
  private static String statsSuffix = ".stats";

  /** The order field. */
  private static int orderField = 0;

  /** The annotator name field. */
  private static int annotatorNameField = 1;

  /** The number of threads field. */
  private static int numberOfThreadsField = 2;

  /** The initialization field. */
  private static int initializationField = 3;

  /** The cumulative time field. */
  private static int cumulativeTimeField = 4;

  /** The max Q field. */
  private static int maxQField = 5;

  // =======================================================
  /**
   * write creates a file with the performance stats in it.
   *
   * @param logDir the log dir
   * @param order the order
   * @param pAnnotatorClassName the annotator class name
   * @param numberOfThreads the number of threads
   * @param initializationTime the initialization time
   * @param cumulativeTime the cumulative time
   * @param maxQ the max Q
   */
  // =======================================================
  public static void write(String logDir, int order, String pAnnotatorClassName,
    int numberOfThreads, long initializationTime, long cumulativeTime, int maxQ) {

    int t = 0;
    if (pAnnotatorClassName.contains("gov.va.vinci.nlp.framework.annotator."))
      t = "gov.va.vinci.nlp.framework.annotator".length() + 1;
    String annotatorClassName = pAnnotatorClassName.substring(t);

    String performanceStatsFileName =
        logDir + "/" + annoatatorPerformanceStats + "/" + annotatorClassName + statsSuffix;
    File performanceStatsDir = new File(logDir + "/" + annoatatorPerformanceStats);

    try {
      if (!performanceStatsDir.exists())
        U.mkDir(performanceStatsDir.getAbsolutePath());

      try (final PrintWriter out = new PrintWriter(performanceStatsFileName);) {
        out.print(order + "|" + annotatorClassName + "|" + numberOfThreads + "|"
            + initializationTime + "|" + cumulativeTime + "|" + maxQ + "|" + "\n");
      }

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue writing the performance stats file out for " + annotatorClassName + " :"
          + e.toString();
      System.err.println(msg);
    }

  } // End Method write() ======================

  // =======================================================
  /**
   * analyze gathers all the .stats files, and creates a summary table
   *
   * @param logDir the log dir
   */
  // =======================================================
  public static void analyze(String logDir) {

    long totalTime = 0;
    long totalInitializationTime = 0;
    try {
      String performanceStatsDir = logDir + "/" + annoatatorPerformanceStats;

      File aDir = new File(performanceStatsDir);

      if (aDir != null && aDir.exists() && aDir.canRead()) {

        File[] files = aDir.listFiles(new FilenameFilter() {
          @Override
          public boolean accept(File dir, String name) {
            return name.endsWith(statsSuffix);
          }
        });

        AnnotatorStatsTableRow rows[] = new AnnotatorStatsTableRow[files.length];
        int i = 0;
        for (File aFile : files) {

          String contents = U.readFile(aFile.getAbsoluteFile());
          String cols[] = U.split(contents);
          rows[i] = new AnnotatorStatsTableRow();
          rows[i].order = Integer.parseInt(cols[orderField]);
          rows[i].annotatorName = cols[annotatorNameField];
          rows[i].numberOfThreads = Integer.parseInt(cols[numberOfThreadsField]);
          rows[i].initializationTime = Integer.parseInt(cols[initializationField]);
          rows[i].totalTimeInMilliSeconds = Long.parseLong(cols[cumulativeTimeField]);
          rows[i].maxQ = Integer.parseInt(cols[maxQField]);
          totalTime = totalTime + rows[i].totalTimeInMilliSeconds;
          totalInitializationTime = totalInitializationTime + rows[i].initializationTime;
          i++;
        } // end loop through files

        // -----------------------
        // Create a table of the % of the total time
        for (int j = 0; j < rows.length; j++) {
          rows[j].totalTimeInMinutes =
              TimeUnit.MILLISECONDS.toMinutes(rows[j].totalTimeInMilliSeconds);
          rows[j].percentTime =
              ((rows[j].totalTimeInMilliSeconds + 0.0001) / totalTime + 0.0001) * 100;
          rows[j].percentInitializationTime =
              ((rows[j].initializationTime + 0.0001) / totalInitializationTime + 0.0001) * 100;
        }

        // -----------------------
        // sort by order
        Arrays.sort(rows);

        // -----------------------
        // Write out the completed table
        String performanceStatsFileName =
            logDir + "/" + annoatatorPerformanceStats + "/" + "totalPerformanceStats" + ".csv";

        try (final PrintWriter out = new PrintWriter(performanceStatsFileName);) {
          DecimalFormat df = new DecimalFormat("#.00");

          out.print("Order" + "," + "Annotator" + "," + "# Threads" + "," + "# Minutes" + ","
              + "% Time" + "," + "% Initialization" + "," + "max Q" + "\n");

          for (int k = 0; k < rows.length; k++) {
            out.print(rows[k].order + "," + rows[k].annotatorName + "," + rows[k].numberOfThreads
                + "," + rows[k].totalTimeInMilliSeconds + "," + df.format(rows[k].percentTime) + ","
                + df.format(rows[k].percentInitializationTime) + "," + rows[k].maxQ + "\n");
          } // end loop through the rows
        }
      } // end if the performance stats dir exists

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue writing the stats out" + e.toString();
      System.err.println(msg);
    }

  } // End Method analize() ======================

} // end Class ScaleOutAnnotatorPerformanceStats
