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
// =================================================
/**
 * Reader is an interface for all marshaller readers.
 *
 *
 * @author  Guy Divita 
 * @created Sept 20, 2013
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.reader;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;


public  abstract class Reader extends CollectionReader_ImplBase {

	
  public static final String NO_TEXT = "NO TEXT";
	
    // -----------------------------------------
	  /** 
	   * initialize opens the file that has the multi-AnnotationRecord files.
	   * 
	   * 
	   * This method relies on the config variable "inputFile"
	   *
	   * @param pInputFile  
	   * @exception ResourceInitializationException [description here]
	   
	   */
	  // -----------------------------------------
	  public abstract void initialize(String pInputFile ) throws ResourceInitializationException ;

	  // -----------------------------------------
    /** 
     * initialize passes thru arguments to the reader
     * 
     * 
     * This method assumes the convention that one of the arguments is --inputDir=
     *
     * @param pArgs  
     * @exception ResourceInitializationException
     
     */
    // -----------------------------------------
    public abstract void initialize(String[] pArgs ) throws ResourceInitializationException ;
      
    
    // -----------------------------------------
    /** 
     * initialize passes thru arguments to the reader
     * 
     *  
     * This method assumes that there is a context variable that is a string array called args
     * the convention that one of the arguments is --inputDir=
     *
     * @param aContext
     * @exception ResourceInitializationException
     
     */
    // -----------------------------------------
    public void initialize(UimaContext aContext ) throws ResourceInitializationException {
      
      String args[] = null;
      try {
        args = (String[]) aContext.getConfigParameterValue("args");
        
        if ( args != null && args.length > 0 )
          initialize( args );
      } catch (Exception e ) {
        e.printStackTrace();
        System.err.println("Issue with pulling the uima context to pass to the reader " + e.toString());
        throw new ResourceInitializationException();
      }
      
    } // End Method initialize() --------------------------
    

  // =======================================================
  /**
   * attachPerformanceMeter attaches a log to the reader
   * 
   * @param pPerformanceMeter [description here]
   */
  // =======================================================
  public void attachPerformanceMeter(ProfilePerformanceMeter pPerformanceMeter) {
    this.performanceMeter = pPerformanceMeter;
  } // End Method attachPerformanceMeter() ======================
  

  // =======================================================
  /**
   * close
   * 
   * 
   */
  // =======================================================
  public void close() throws IOException  {
    
  } // End Method close() ======================
  


  // =======================================================
  /**
   * setCatalog  sets an an array corresponding to the filesToProcess array 
   * that marks if this file has been processed or not.
   * 
   * @param pRecordsToProcess
   */
  // =======================================================
  public void setCatalog( String[] pRecordsToProcess ) {
    this.filesProcessedCatalog = new boolean[ pRecordsToProcess.length];
    
    for ( int i = 0; i < (int) this.fileCounter; i++ ) 
      this.filesProcessedCatalog[i] = true;
    for ( int i = (int) this.fileCounter; i < this.filesProcessedCatalog.length; i++ )
      this.filesProcessedCatalog[i] = false;
  } // End Method setCatalog() ======================
  
  // =======================================================
  /**
   * setCatalog  sets an an array corresponding to the filesToProcess array 
   * that marks if this file has been processed or not.
   * 
   * @param pRecordsToProcess
   */
  // =======================================================
  public void setCatalog( File[] pRecordsToProcess ) {
    this.filesProcessedCatalog = new boolean[ pRecordsToProcess.length];
    for ( int i = 0; i < (int) this.fileCounter; i++ ) 
      this.filesProcessedCatalog[i] = true;
    for ( int i = (int) this.fileCounter; i < this.filesProcessedCatalog.length; i++ ) this.filesProcessedCatalog[i] = false;
  } // End Method setCatalog() ======================
  
  // =======================================================
  /**
   * setCatalog  sets an an array corresponding to the filesToProcess array 
   * that marks if this file has been processed or not.
   * 
   * @param pRecordsToProcess
   */
  // =======================================================
  public synchronized void setCatalog( Collection<?> pRecordsToProcess ) {
    this.filesProcessedCatalog = new boolean[ pRecordsToProcess.size()];
    for ( int i = 0; i < (int) this.fileCounter; i++ )  
      this.filesProcessedCatalog[i] = true;
    for ( int i = (int) this.fileCounter; i < this.filesProcessedCatalog.length; i++ ) this.filesProcessedCatalog[i] = false;
  
  } // End Method setCatalog() ======================
  
//=======================================================
 /**
  * setCatalog  sets an an array corresponding to the filesToProcess array 
  * that marks if this file has been processed or not.
  * 
  * @param pRecordsToProcess
  */
 // =======================================================
 public synchronized void setCatalog( int pRecordsToProcess ) {
   this.filesProcessedCatalog = new boolean[ pRecordsToProcess ];
   for ( int i = 0; i < (int) this.fileCounter; i++ )   this.filesProcessedCatalog[i] = true;
   for ( int i = (int) this.fileCounter; i < this.filesProcessedCatalog.length; i++ ) this.filesProcessedCatalog[i] = false;
 
 } // End Method setCatalog() ======================
  
  
  // =======================================================
  /**
   * setProcessed sets the cell to true when processed.
   * 
   * 
   * @param pRecordId
   */
  // =======================================================
  public synchronized void setProcessed( long pRecordId ) {
    if ( pRecordId >= 0 && pRecordId < this.filesProcessedCatalog.length )
      this.filesProcessedCatalog[(int) pRecordId] = true;
   
  } // End Method setProcessed() ======================
  
  
  // =======================================================
  /**
   * getCatalog  gets the array corresponding to the filesToProcess array 
   * that marks if this file has been processed or not.
   * 
   * @param pRecordId
   */
  // =======================================================
  public synchronized boolean[] getCatalog(  ) {
    return this.filesProcessedCatalog;
   
  } // End Method getCatalog() ======================
  

//-----------------------------------------
 /** 
  * get retrieves the next document id from the list of document ids, queries the database fro 
  * annotations that share this document id, creates annotations for this id.
  * @param pCAS
  * @param pRecordId
  * @throws IOException
  * @throws CollectionException
  */
 // -----------------------------------------
 public synchronized void get(CAS pCAS, long pRecordId) throws IOException, CollectionException {
   // TBD
 }
  
  
  // =======================================================
  /**
   * isProcessed retrieves the processed status for the record id
   * 
   *  Note that if the pRecordId is bigger, this method will return
   *  a true value on the grounds that a non existent file will not 
   *  need to be processed.
   *  
   * @param pRecordId
   */
  // =======================================================
  public synchronized boolean isProcessed(int pRecordId  ) {
   
    if ( pRecordId < this.filesProcessedCatalog.length )
      return this.filesProcessedCatalog[pRecordId];
    else 
      return true;
  } // End Method isProcessed() ======================
  

  // =======================================================
  /**
   * allProcessed returns true if all the records have been
   * processed.  This is an expensive process, so use it
   * sparingly
   *  
   * @return boolean
   */
  // =======================================================
  public synchronized boolean allProcessed(  ) {
   
    boolean returnVal = true;
    for (int i = this.filesProcessedCatalog.length -1; i >=0 ; i--)
      if ( !this.filesProcessedCatalog[i] )
        return false;
    
    
    return returnVal;
    
  } // End Method allProcessed() ======================


  // =======================================================
  /**
   * destroy
   *  
   */
  // =======================================================
  @Override
  public void destroy() {
     if ( this.performanceMeter != null )
       this.performanceMeter.writeProfile( this.getClass().getSimpleName());
  }
  
  
 // =======================================================
  /**
   * getNumberOfFiles
   * 
   * @return int
   */
  // =======================================================
  protected long getNumberOfFiles() {
   return this.numberOfFiles;
  } // End Method getNumberOfFiles() ======================
  
//=======================================================
 /**
  * setNumberOfFiles
  * 
  * @param  pNumberOfFiles
  */
 // =======================================================
 protected void setNumberOfFiles(int pNumberOfFiles) {
   System.err.println(" SEEETing the number of files " + pNumberOfFiles);
   this.numberOfFiles = pNumberOfFiles;
 } // End Method setNumberOfFiles() ======================
  
//=======================================================
/**
 * getFileCounter
 * 
 * @return int
 */
// =======================================================
protected long getFileCounter() {
 return this.fileCounter;
} // End Method getFileCounter() ======================

//=======================================================
/**
* setFileCounter
* 
* @param  pNumberOfFiles
*/
// =======================================================
protected void setFileCounter(long l) {
 this.fileCounter = l;
} // End Method setFileCounter() ====================== 
 
 
//=======================================================
 /**
  * whatIsNotProcessed returns a int[] of
  * the records that have not yet been processed
  * 
  * This method is expensive, so use it sparingly.
  * 
  *  
  * @return int[]
  */
 // =======================================================
 public synchronized int[] whatIsNotProcessed(  ) {
  
   ArrayList<Integer> unprocessed = new ArrayList<Integer>();
   for ( int recordId = 0; recordId <  this.filesProcessedCatalog.length; recordId++ ) {
     if ( !this.filesProcessedCatalog[recordId])  
       unprocessed.add(recordId);
   }
   int[] returnVal = new int[unprocessed.size()];
   for ( int i = 0; i < unprocessed.size(); i++ ) returnVal[i] = unprocessed.get(i);
   
   return returnVal;
   
 } // End Method allProcessed() ======================
  
  
  protected ProfilePerformanceMeter performanceMeter = null;
  protected long                      fileCounter = 0;
  protected long                    numberOfFiles = 0;
  protected boolean[]     filesProcessedCatalog = null;
  protected String[]                       args = null;

  
  
	
} // end Class MultiAnnotationRecordCollectionReader() ----
