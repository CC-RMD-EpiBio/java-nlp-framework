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
// =======================================================
/**
 * RunAndReport runs a scale-out pipeline application 
 * with a specific configuration, and report back the 
 * average time per record it took
 *
 * @author  Divita
 * @created Mar 27, 2015
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils;

/**
 * @author Divita
 *
 */
public class RunAndReport {

  // =======================================================
  /**
   * run kicks off an external process with 
   * 
   *   This method assumes that the main from the pJarFile will include data on standard out
   *   and that data will end with a pipe surrounded string with |avgMSPerRecord|   
   *   
   *   
   * @param pMemory
   * @param pNumberOfPipelines
   * @param pJarFile
   * @param pInputOptions
   * @return double   The average milliseconds (ms) it takes to process a record 
   */
  // =======================================================
  public double run(double pMemory, 
                       int pNumberOfPipelines, 
                       String pJarFile, 
                       String pInputOptions, 
                       int pContextNumberOfThreads, 
                       int pNumberToProcess) {
    
    double averageMsPerRecord = 0.0;
    
    // convert gb to mb and only keep the integer portion
    int mbMemory = (int) pMemory * 1000 ;
    System.err.println(U.run("which java"));
    String command = "java -Xms" + mbMemory + "m " + 
                          "-Xmx16g -jar " + 
                           pJarFile + " " + 
                           pInputOptions + " " + 
                           "--numberOfApplications="   + pNumberOfPipelines + " " + 
                           "--contextNumberOfThreads=" + pContextNumberOfThreads + " " +
                           "--numberToProcess="        + pNumberToProcess;
    
    System.err.println("About to kick off the command \n" + command + '\n');
    String returnOutput =  U.run(command);
    System.err.println("Got back " + returnOutput);
    
    String cols[] = U.split(returnOutput);
    if ( cols != null && cols.length > 0 )
      averageMsPerRecord = Double.parseDouble(cols[ cols.length - 2]);
    
  
    return averageMsPerRecord;
  } // End Method run() ======================
  

  
}
