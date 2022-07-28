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
 * RecordIndex.java
 *
 * @author  guy
 * @created Apr 8, 2015
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.vtt;


/**
 * @author guy
 *
 */
public class RecordIndex {

 
  private int  fileId = 0;
  private long offset = 0;
  private int length = 0;

  // =======================================================
  /**
   * Constructor RecordIndex 
   *
   * @param pInputFile
   * @param pIn
   * @param pOffset
   * @param pLength
   */
  // =======================================================
  public RecordIndex( int pFileId, long pOffset, int pLength) {
  
    this.fileId = pFileId;
    this.offset = pOffset;
    this.length = pLength;
  }

  // =======================================================
  /**
   * getFileId
   * 
   * @return int
   */
  // =======================================================
  public int getFileId() {
   return this.fileId;
  } // End Method getIn() ======================

  // =======================================================
  /**
   * getBeginOffset 
   * 
   * @return long
   */
  // =======================================================
  public long getBeginOffset() {
   return this.offset;
  } // End Method getBeginOffset() ======================

  // =======================================================
  /**
   * getLength 
   * 
   * @return int
   */
  // =======================================================
  public int getLength() {
  return this.length;
  } // End Method getLength() ======================
  
  
  

} // end class RecordIndex() ------------
