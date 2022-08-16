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
