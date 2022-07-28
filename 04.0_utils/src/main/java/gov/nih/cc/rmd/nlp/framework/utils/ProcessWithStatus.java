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
// ------------------------------------------------------------
/**
 * ProcessWithStatus is a class that runs a method on
 * a bunch of files, and keeps track of the status
 * of what has run and what has yet to be run.
 * 
 * If the process fails, it can be re-started and pick
 * up where it left off.
 *
 * @author Guy Divita
 * Sep 28, 2012
 * 
 * ------------------------------------------------------------
 *
 *
 *
 * -------------------------------------------------------------
 */
// -------------------------------------------------------------
package gov.nih.cc.rmd.nlp.framework.utils;

import java.util.List;


public class ProcessWithStatus {

  private ProgressList progressList;
  private int numberProcessed;

  // -----------------------------------------
  /**
   * Constructor 
   * 
   * @param startingDir
   * @param docTypes
   * @param updateFreq
   * @param recurse
   * @throws Exception 
   */
  // -----------------------------------------
  public ProcessWithStatus(String startingDir, String docTypes, int updateFreq, boolean recurse) throws Exception {
    // --------------------------
    // If there is a progress log, open it up, read in the
    // contents to find where in the list to start from
    // --------------------------
     
    this.progressList = new ProgressList( );
    
    progressList.setFlushFrequency( updateFreq);
    this.numberProcessed = progressList.getNumberProcessed(); // gets absolute number of records processed 
    //STOPPED_HERE
    // ---------------------------
    // If this is a new progress list, create an index of new
    // files to process
    // --------------------------
    if (progressList.isNew() ) {
      progressList.findFilesToProcess( startingDir, docTypes, recurse);
    }
    
    // --------------------------
    // Iterate through the files to be processed
    // --------------------------
    List<String> fileNamesToProcess = progressList.getFilesToProcess();
    
    
    // End Constructor() -----------------------------
  }

  // ------------------------------------------
  /**
   * run
   * @throws Exception 
   *
   *
   */
  // ------------------------------------------
  public void run() throws Exception {
    String status = "failed:";
    
    List<String> fileNamesToProcess = this.progressList.getFilesToProcess();
    for ( String fileName: fileNamesToProcess ) {
      
      // ------------------
      // Process file
      // ------------------
      status = processFileName( fileName);
      
      // ------------------
      // Update the processList with status
      // ------------------
      this.progressList.update( fileName, status);
      
    
        
    } // end Loop through files to process
    
    // -------------------------------
    // If the process completed, remove the progressList
    // -------------------------------
    this.progressList.destroy();

    // End Method run() -----------------------
  }

 

  // ------------------------------------------
  /**
   * processFileName
   *
   *
   * @param fileName
   * @return String
   */
  // ------------------------------------------
  public static String processFileName(String fileName) {
    // TODO Auto-generated method stub
    return null;
    
    
    // End Method processFileName() -----------------------
  }

 
  

}
