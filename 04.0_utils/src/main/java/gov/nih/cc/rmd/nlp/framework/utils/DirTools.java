package gov.nih.cc.rmd.nlp.framework.utils;
// =======================================================
/**
 * DirTools is a synchronized static class that reads in the files from an 
 * input directory in a safe way, doles out these files in a safe way, 
 * moves the files from the input directory to a finished directory in
 * a safe way when done.
 *
 * @author  guy
 * @created Nov 21, 2013
 *
   
 */



import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;



/**
 * @author guy
 *
 */
public class DirTools {

  private List<String> mFiles = null;
  private Iterator<String> mFilesIterator;
  private String inputDir = null;
  private String finishedDir = null;

//=======================================================
 /**
  * Constructor DirTools creates a list of files to process
  *
  * @param pInputDir
  * 
  * @throws Exception 
  */
 // =======================================================
 public DirTools(String pInputDir ) throws Exception {
   
   this.inputDir = pInputDir;
   
   if ( finishedDir != null ) 
     U.mkDir(finishedDir);
   
   try {
     ListFilesRecursively fw = new ListFilesRecursively( ); 
   
     String startDir = pInputDir;
 
     String[] files = fw.listAllFiles( startDir );
    
     mFiles = Arrays.asList(files);
     if ( mFiles != null )
       mFilesIterator = mFiles.iterator();
     
   } catch (Exception e) {
     e.printStackTrace();
     String msg = "Issue creating the DirTools with the inputDir " + pInputDir + " " + e.toString();
     System.err.println(msg);
     throw new Exception( msg);
   }
 } // end Constructor() -----------------------------------

 

//=======================================================
/**
 * Constructor DirTools creates a list of files to process
 *
 * @param pInputDir
 * @param pFinishedDir
 * @throws Exception 
 */
// =======================================================
public DirTools(String pInputDir, String pFinishedDir ) throws Exception {
  
  this.inputDir = pInputDir;
  this.finishedDir = pFinishedDir;
  
  U.mkDir(finishedDir);
  try {
    ListFilesRecursively fw = new ListFilesRecursively( ); 
  
    String startDir = pInputDir;

    String[] files = fw.listAllFiles( startDir );
   
    mFiles = Arrays.asList(files);
    if ( mFiles != null )
      mFilesIterator = mFiles.iterator();
    
  } catch (Exception e) {
    e.printStackTrace();
    String msg = "Issue creating the DirTools with the inputDir " + pInputDir + " " + e.toString();
    System.err.println(msg);
    throw new Exception( msg);
  }
} // end Constructor() -----------------------------------


  // =======================================================
/**
 * getinputDir 
 * 
 * @return the inputDir
 */
// =======================================================
public final String getInputDir() {
  return inputDir;
}



// =======================================================
/**
 * setinputDir 
 * 
 * @param inputDir the inputDir to set
 */
// =======================================================
public final void setInputDir(String inputDir) {
  this.inputDir = inputDir;
}



  /**
   * @return the files
   */
  public synchronized List<String> getfiles() {
    return mFiles;
  } // end Method getFiles



  // =======================================================
  /**
   * getNext retrieves the name of the next file to be processed
   * 
   * @return String
   */
  // =======================================================
  public synchronized String getNext() {
    
    String returnVal = null;
    
    if ( mFilesIterator != null ) 
      try {
        returnVal = mFilesIterator.next();
      } catch (Exception e) {}
    return returnVal;
  }  // End Method getNext() ======================
  



  // =======================================================
  /**
   * moveToFinishedDir moves a successfully processed file to
   * the finished directory so that it is out of the way
   * 
   * @param pFileName
   * @throws Exception 
   */
  // =======================================================
  public synchronized void moveToFinishedDir(String pFileName) throws Exception {
   
    if ( this.finishedDir == null ) {
      String msg = "No finished dir defined, not moving it";
      System.err.println(msg);
      throw new Exception(msg);
    }
    
    String     justFileName = U.getNameWithoutNameSpace(pFileName);
    String finishedLocation = this.finishedDir + "/" + justFileName;
    try {
    
      File afile =new File( pFileName);

      if(afile.renameTo(new File(this.finishedDir + "/" + afile.getName()))){
        System.out.println("File is moved successful!");
       
        
      }else{
       
        String msg = "Issue trying to move " + pFileName + " to " +  finishedLocation ;
        System.err.println(msg);
        throw new Exception (msg);
      }

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue trying to move " + pFileName + " to " +  finishedLocation + " " + e.toString();
      System.err.println(msg);
      throw new Exception (msg);
    }
  }  // End Method moveToFinishedDir() ======================



  // =======================================================
  /**
   * hasNext checks the iterator to see if there are any additional
   * files to be processed
   * 
   * @return boolean
   */
  // =======================================================
  public synchronized boolean hasNext() {
    return mFilesIterator.hasNext();
    
  }  // End Method hasNext() ======================





  
  
  

} // end Class DirTools
null
