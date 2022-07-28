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
 * ProgressList is a container class that holds the progress
 * statuses of a list of things (files) to be processed.  
 * 
 * This container keeps a persistent copy so that if the process
 * that needs this list gets interrupted, the progress of what
 * was processed and what was not is kept, and the process
 * can be re-started from where it left off.
 * 
 * This class contains a flush method that should be called
 * every X calls to update the persistent copy.  
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;


public class ProgressList {
  
  private static final String PROGRESSLIST_EXTENSION = ".pLog";
  private static final String STANDARD_TMP_DIRECTORY = "/temp";
  // -----------------------------------------
  /**
   * Constructor 
   * 
   * @param pFileName
   * @throws Exception 
   */
  // -----------------------------------------
  public ProgressList(String pFileName) throws Exception {
    File progressFile = new File ( pFileName );
    this.fileName = pFileName;
    
    if ( progressFile.exists() && progressFile.canRead()) {
      readProgressList( pFileName );
      this.newList = false;
    } else {
      this.newList = true;
      
      
    }
    
    
  }  // End Constructor() -----------------------------


  // -----------------------------------------
  /**
   * Constructor 
   * 
   * @param pFileName
   * @param pStartDir
   * @param pFileTypes
   * @param pRecurse
   * @throws Exception 
   */
  // -----------------------------------------
  public ProgressList( String pFileName, String pStartDir, String pFileTypes, boolean pRecurse) throws Exception {
    File progressFile = new File ( pFileName );
    
    if ( progressFile.exists() && progressFile.canRead()) {
      readProgressList( pFileName );
      
    } else {
      init(pStartDir, pFileTypes, pRecurse );
    }
    // End Constructor() -----------------------------
  }


  // -----------------------------------------
  /**
   * Constructor  This constructor looks into a
   * standard tmp directory to see if there are
   * any progress files around.  If it finds
   * any, it will pick the first to set as the
   * progress list.  It will load in this progress file.
   * 
   * If no progress files are found, a new one is created
   * the extension STANDARD_TMP_DIR/progressFileXXXXXX.pLog 
   * Virgin lists are created.
   *  
   * @throws Exception 
   * 
   */
  // -----------------------------------------
  public ProgressList() throws Exception {
   
    this.fileName = findExistingProgressList( );
    UUID uuid = java.util.UUID.randomUUID();
    
    if ( this.fileName == null ) {
      this.fileName = STANDARD_TMP_DIRECTORY + "/" + "progressLog" +
      uuid.toString() + PROGRESSLIST_EXTENSION ;
      
    }
    
    File progressFile = new File( this.fileName);
    if ( progressFile.exists() && progressFile.canRead()) {
      readProgressList( this.fileName );
      this.newList = false;
    } else {
      this.newList = true;
    }
   
    // End Constructor() -----------------------------
  }


  // ------------------------------------------
  /**
   * findExistingProgressList grabs the first
   * of any existing progress logs within the
   * standard tmp directory.  This method
   * will return null if none are found.
   *
   *
   */
  // ------------------------------------------
  private String findExistingProgressList() {

    String returnValue = null;
    File tmpDir = new File( STANDARD_TMP_DIRECTORY );
    
    if ( tmpDir != null && tmpDir.isDirectory() && tmpDir.canRead()) {
      
      String[] files = tmpDir.list();
      
      if ( files != null && files.length > 0 )
        for ( String file : files )
          if ( file.endsWith(PROGRESSLIST_EXTENSION )) {
            returnValue = file;
            break;
          }
      
    } // else found the tmpDir
    return returnValue;
     
    
    // End Method findExistingProgressList() -----------------------
  }


  // ------------------------------------------
  /**
   * setFlushFrequency sets the number of updates to handle
   * before flushing out to disk.
   *
   *
   * @param pUpdateFreq
   */
  // ------------------------------------------
  public void setFlushFrequency(int pUpdateFreq) {
    this.updateFrequency = pUpdateFreq;
    
    
    // End Method setFlushFrequency() -----------------------
  }


  // ------------------------------------------
  /**
   * findFilesToProcess iterates through directories
   * to find files, then filters those files to
   * the fileTypes to process and creates
   * a hash table, and two convenience lists of 
   * files to process, and files processed.
   *
   *
   * @param startDir
   * @param fileTypes
   * @param recurse (through directories)
   * @throws Excpetion
   * 
   */
  // ------------------------------------------
  public List<String> findFilesToProcess(String startDir, String fileTypes, boolean recurse ) 
  throws Exception 
  {
    
    initializeLists();
    HashSet<String> fileExtensionHash = createFileExtensionHash(fileTypes);
    
    File aDir = new File( startDir);
  
    if ( !aDir.exists() || !aDir.isDirectory() || !aDir.canRead() ) {
      throw new Exception ("Either the directory |" + startDir + "| does not exist, or it cannot be read");
    }
  
    String[] candidateFilesToProcess = null;
  
    if ( !recurse )
      candidateFilesToProcess = aDir.list();
    else {
      ListFilesRecursively aRDir = new ListFilesRecursively();
      candidateFilesToProcess = aRDir.listAllFiles( startDir);
    }
  
   // ------------------------------
   // Filter in files to process
   // ------------------------------
   if ( candidateFilesToProcess != null && candidateFilesToProcess.length > 0) {
     
     for ( String candidateFile : candidateFilesToProcess ) {
       
       if ( fileExtensionHash != null && fileExtensionHash.size() > 0 ) {
         String fileExtension = U.getFileExtension( candidateFile);
         if ( fileExtension != null  && fileExtensionHash.contains( fileExtension)) {
           this.filesToProcess.add( candidateFile);
           this.fileHash.put( candidateFile, "to be processed");
         } //end if this is a valid candidate
       } else {  // process all candidates
         this.filesToProcess.add( candidateFile);
         this.fileHash.put( candidateFile, "to be processed");
       } // end if there are no file extensions to process
     } // end loop through candidate files
       
   } // end if there are any candidate files
  
  return this.filesToProcess;
  
  }   // End Method findFilesToProcess() -----------------------


  // ------------------------------------------
  /**
   * update updates the hash's status for the file
   * and updates the lists of processed files and
   * files to process
   *
   *
   * @param pFileName
   * @param status
   * @throws FileNotFoundException 
   */
  // ------------------------------------------
  public void update(String pFileName, String status) throws FileNotFoundException {
  
    this.fileHash.put( pFileName, status);
    
    // ---------------------
    // Find the fileName on the filesToProcess list
    // and take it off
  
    
    for ( String foundFile : this.filesToProcess )
      if ( foundFile.equals(pFileName)) {
        this.filesToProcess.remove( foundFile);
        break;
      }
    
    // ---------------------
    // Add this file to the files processed list
    this.filesProcessed.add(pFileName);
    
    this.numberProcessed++;
    // ---------------------
    // flush the lists out to a persistent store
    if ( this.numberProcessed % this.updateFrequency == 0)
      flush();
    
    // End Method update() -----------------------
  }


  // ------------------------------------------
  /**
   * flush flushes the processed and unproccessed files out to
   * a store that can be read back in 
   * @throws FileNotFoundException 
   *
   *
   */
  // ------------------------------------------
  public void flush() throws FileNotFoundException {
    
    PrintWriter out = new PrintWriter( this.fileName );
    String status = null;
    // ---------------------
    // Iterate through all files in the two lists
    // ---------------------
    for ( String processedFile : this.filesProcessed ) {
      status = this.fileHash.get( processedFile);
      out.println( processedFile + "|" + status );
    }
    for ( String  fileToProcess : this.filesToProcess )
      out.println(fileToProcess + "|" + NOT_YET_PROCESSED);
    
    out.close();
    
    // End Method flush() -----------------------
  }


  // ------------------------------------------
  /**
   * getFilesToProess returns the list of files
   * that need to be processed.
   *
   *
   * @return List<String> of full file names
   */
  // ------------------------------------------
  public List<String> getFilesToProcess() {
    return this.filesToProcess;
    
  } // End Method getFilesToProess() -----------------------


  // ------------------------------------------
  /**
   * getProgress  returns the number of files processed.
   *
   *
   * @return int
   */
  // ------------------------------------------
  public int getNumberProcessed() {
    
    return this.numberProcessed;
    
    // End Method getProgress() -----------------------
  }


  // ------------------------------------------
  /**
   * isNew returns if this progressList is a new one
   * or one that was started before.
   *
   *
   * @return boolean
   */
  // ------------------------------------------
  public boolean isNew() {
    
      return this.newList;
      
    // End Method isNew() -----------------------
  }


  // ------------------------------------------
  /**
   * destroy removes the persistent file store
   * @throws Exception 
   *  
   *
   */
  // ------------------------------------------
  public void destroy() throws Exception {
    try {
      U.deleteFile(this.fileName);
    } catch ( Exception e) {
      e.printStackTrace();
      throw new Exception ( "Issue removing the files processed status file \n|" + 
          this.fileName + "|\n " + e.toString());
    }
      
  } // End Method destroy() -----------------------


  // ------------------------------------------
  /**
   * readProgressList opens and reads in the contents
   * of the progress file.  It populates those files that
   * have been processed into one list, ones that need
   * to be processed into a second list.
   *
   *
   * @param pFileName
   * @throws Exception 
   */
  // ------------------------------------------
  private void readProgressList(String pFileName) throws Exception {
    
    String[] rows = null;
    try {
      rows = U.readFileIntoStringArray(pFileName);
    } catch( Exception e) {
      e.printStackTrace();
      throw new Exception( "Issue reading in the progress file \n|" + pFileName + "|\n" + e.toString()); 
    }
    
    initializeLists();
   
    // ----------------------------
    // read through the rows of the file
    
    for ( String row : rows ) {
      String[] cols = U.split(row);
      String mFileName = cols[0];
      String status = cols[1];
      if ( status.contains(NOT_YET_PROCESSED) )
        this.filesToProcess.add( mFileName);
      else 
        this.filesProcessed.add( mFileName);
    
      this.fileHash.put( fileName, status);
    } // end loop through rows of the file
      
    
    // End Method readProgressList() -----------------------
  }


  // ------------------------------------------
  /**
   * initializeLists clears or creates the 
   * filesProcessed, filesToProcess and fileHash lists
   *
   *
   */
  // ------------------------------------------
  private void initializeLists() {
   
    // ----------------------------------
    // Clear or initialize the stores
    if ( this.filesProcessed != null )
      this.filesProcessed.clear();
    else
      this.filesProcessed = new ArrayList<String>();
    
    if ( this.filesToProcess != null )
      this.filesToProcess.clear();
    else
      this.filesToProcess = new ArrayList<String>();
   
    if ( this.fileHash != null)
      this.fileHash.clear();
    else
      this.fileHash = new HashMap<String,String>();
      
    
  } // End Method initializeLists() -----------------------
  


  private List<String> filesProcessed = null;   // convenience list of processFile|status
  // ------------------------------------------
  /**
   * init finds files to process given a starting directory,
   * file types to process and whether or not to recurse through
   * sub directories
   * 
   * @throws Exception 
   *
   *
   */
  // ------------------------------------------
  private void init(String startDir, String fileTypes, boolean recurse) throws Exception {
   
   
   findFilesToProcess( startDir, fileTypes, recurse);
     
  }  // End Method init() -----------------------
  

  // ------------------------------------------
  /**
   * createFileExtensionHash
   *   
   * @param fileTypes
   * @return HashSet<String> of file extensions (with the period)
   */
  // ------------------------------------------
  private HashSet<String> createFileExtensionHash(String fileTypes) {
 // Prepare fileTypes into an easy hash
  
    HashSet<String> fileExtensionHash = null;
    if ( fileTypes != null &&  fileTypes.length() > 0 ) {
      fileExtensionHash = new HashSet<String>(fileTypes.length());
      String fileExtensions[] = U.split(fileTypes);
      if ( fileExtensions != null && fileExtensions.length > 0)
        for ( String fileExtension : fileExtensions )
          fileExtensionHash.add ( fileExtension);
    }
    return fileExtensionHash; 
      
  }  // End Method createFileExtensionHash() -----------------------

  private String fileName;
  // -----------------------------------
  /**
   * Class Fields
   *
   */
  // ------------------------------------
  private List<String> filesToProcess = null;   // convenience list of files to process
  private HashMap<String, String>fileHash = null;  // hash of <fileName> by status
  private int updateFrequency = 5;
  private int numberProcessed = 0; // 
  
  
  private boolean newList;
  private static final String NOT_YET_PROCESSED = "NOT_YET_PROCESSED";
   
  // End ProgressList Class -------------------------------
}
