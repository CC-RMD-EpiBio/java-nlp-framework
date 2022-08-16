// =================================================
/**
 * FromDatabase reads in data from a database
 * and converts them to Cas's.
 *
 *
 * @author  Guy Divita 
 * @created Aug 17, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.string;



import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

// extends CollectionReader_ImplBase,

public class FromStringReaderObs extends gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader {

  
private HashSet<File> seenFiles;


private String inputDir;


//=======================================================
 /**
  * Constructor FromText is called from SuperReader
  *  assumes that the initialize() method will be called later on.
  *
  */
 // =======================================================
 public FromStringReaderObs()  {
   
   
 } // end Constructor() ---------------------
 
  
  
  // =======================================================
  /**
   * Constructor FromText 
   *
   * @param pInputDir
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public FromStringReaderObs(String pInputDir) throws ResourceInitializationException {
    
    initialize( pInputDir, true);
    
  } // end Constructor() ---------------------
  

  

//=======================================================
 /**
  * Constructor FromText 
  *
  * @param pInputDir
  * @param pRecurseIntoSubDirs
  * 
  * @throws ResourceInitializationException 
  */
 // =======================================================
 public FromStringReaderObs(String pInputDir, boolean pRecurseIntoSubDirs) throws ResourceInitializationException {
   
   initialize( pInputDir, pRecurseIntoSubDirs);
   
 } // end Constructor() ---------------------
  
  // -----------------------------------------
  /** 
   * getNext retrieves the next document id from the list of document ids, queries the database fro 
   * annotations that share this document id, creates annotations for this id.
   * @param pCAS
   * @throws IOException
   * @throws CollectionException
   */
  // -----------------------------------------
  public synchronized void  getNext(CAS pCAS) throws IOException, CollectionException {
   
    File aFile = null;
    JCas jcas = null;  
    try {
      jcas = pCAS.getJCas();
      
      synchronized(this) {
      aFile = this.listOfFilesToProcess.get(0);
      
      String documentId    = aFile.getName();
      String documentURI   = documentId;
      String documentText  = U.readFile(aFile);
      String documentTitle = "unknown";
      String documentType  = "unknown";
      String documentName  = documentId; 
      String patientID     = "unknown";
      String referenceDate = "unknown";
      String metaData      = "unknown";
      
      try {
      jcas.setDocumentText( documentText );
      } catch (Exception e) {
        e.printStackTrace();
        this.listOfFilesToProcess.remove(aFile);
        System.err.println("Doc name = " + documentId);
        
        return;
      }
      
     
      VUIMAUtil.setDocumentHeader( jcas, documentId, documentName, documentType, documentTitle, metaData, patientID, referenceDate, this.fileCounter);
   
                  
      aFile.delete();
      } // end synchronized
    } catch (Exception e ) {
      e.printStackTrace();
     System.err.println("Something went wrong with fromDatabase " + e.toString());
    }
     
     
    // --------------------------
    // increment the fileCounter
    this.fileCounter++;
    
    this.listOfFilesToProcess.remove(aFile);
    
  } // end Method getNext() -----------------------


 


  // -----------------------------------------
  /** 
   * getProgress is method required for the 
   * interface that is populated with the 
   * fraction of files processed by the number of files to process.
   *
   * @return Progress[]
   */
  // -----------------------------------------
  public Progress[] getProgress() {
    Progress[] p = new Progress[] { new ProgressImpl(this.fileCounter, this.numberOfFiles, Progress.ENTITIES)};
    return p;

  } // end Method getProgress() --------------

  // -----------------------------------------
  /** 
   * hasNext Looks in the directory and sees if
   * there are any new files to process
   *
   * @return
   * @throws IOException
   * @throws CollectionException  <---- why not anymore?
   */
  // -----------------------------------------
  public boolean hasNext()  {
    boolean returnValue = false;
    
    List<File>listOfFiles = getFilesFromInputDir();
    
    for ( File aFile : listOfFiles ) {
      if ( !this.seenFiles.contains(aFile)   ) {
        this.seenFiles.add( aFile);
        this.listOfFilesToProcess.add( aFile);
      }
    }
   
    if ( this.listOfFilesToProcess.size() > 0 )
      returnValue = true;
    
     return returnValue;
  } // end Method hasNext() -----------------
  

//-----------------------------------------
 /** 
  * initialize reads the directory containing the input files.  
  * 
  * @param pInputDir
  * @throws ResourceInitializationException

  */
 // -----------------------------------------
 public void initialize(String pInputDir ) throws ResourceInitializationException  {
   
   initialize( pInputDir, true);
   
 } // End Method initialize() ---------------------------
 
//=======================================================
 /**
  * initialize 
  * 
  * @param pArgs    assumes there is the arg --inputDir=    and optionally --recurseIntoSubDirs= true|false
  * @throws ResourceInitializationException
  *
  */
 // ======================================================
public void initialize(String[] pArgs) throws ResourceInitializationException {
  
  
   try {
  
     String inputDir = U.getOption(pArgs, "--inputDir=", "./");
     boolean recurseIntoSubDirs = Boolean.parseBoolean(U.getOption(pArgs, "--recurseIntoSubDirs=", "true"));
     
     initialize( inputDir, recurseIntoSubDirs  );
    } catch ( Exception e ) {
      e.printStackTrace();
      System.err.println("Issue within method initialize " + e.getMessage() );
      throw e;
    }
  } // End Method initialize ============
 
  // -----------------------------------------
    /** 
     * initialize opens the directory containing .string files and files 
     * that do not have an extension 
     * 
     * If an inputDir is not specified, the default tmp dir is used.
     * 
     *
     * @param pInputFile
     * @param recurseIntoSubDirs  <--  not used
     * @throws IOException
     * @throws CollectionException
     */
    // -----------------------------------------
    public void initialize(String pInputDir, boolean recurseIntoSubDirs ) throws ResourceInitializationException  {
      
      
      if ( pInputDir == null ) try {
        this.inputDir = File.createTempFile("xxx", ".tmp").getParent();
      } catch (IOException e) {
        e.printStackTrace();
        String msg = "Issue with creating a temp dir" + e.getMessage();
        System.err.println( msg );
        throw new ResourceInitializationException ();
      } else {
        this.inputDir = pInputDir;
      }
      
      this.listOfFilesToProcess = new Vector<File>();
      this.seenFiles = new HashSet<File>();
    
      try {
      List<File>listOfFiles = getFilesFromInputDir();
      
      for ( File aFile : listOfFiles ) {
        if ( !this.seenFiles.contains(aFile)  ) {
          this.seenFiles.add( aFile);
          this.listOfFilesToProcess.add( aFile);
        }
      }
      
      } catch( Exception e) {
        e.printStackTrace();
        String msg = "issue with reader " + e.toString();
        System.err.println(msg);
        throw new ResourceInitializationException();
        
      }
     
  
      
      this.numberOfFiles = this.listOfFilesToProcess.size();
      System.err.println("The number ofiles to process = " + this.numberOfFiles);
      
    
           
  } // end Method initialize() --------------
    
    
    // =======================================================
    /**
     * getFilesFromInputDir 
     * 
     * @return
     */
    // =======================================================
    private List<File> getFilesFromInputDir() {
      
      List<File> filesToProcess = null;
      File inputDir_ = null;
      if ( this.inputDir != null && this.inputDir.length() > 0 ) {
        
        inputDir_ = new File ( this.inputDir);
        
        if ( inputDir_.isDirectory() && inputDir_.canRead() ) {
          
          File[] filesAndSubDirs = inputDir_.listFiles();
          filesToProcess = filterFiles( filesAndSubDirs) ;
          
        } // end if the inputDir can be read                
      } // end if the inputDir is not null
    
      return filesToProcess; 
    } // End Method getFilesFromInputDir() ======================
    



    /* (non-Javadoc)
     * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#close()
     */
    @Override
    public void close() throws IOException {
     
    }  // End Method close() ======================
    
    
    
  // =======================================================
    /**
     * filterFiles returns a list of text files
     * 
     * @param pFiles
     * @return List<File>
     */
    // =======================================================
    private List<File> filterFiles(File[] pFiles) {
    
      ArrayList<File> filteredFiles = new ArrayList<File>(pFiles.length);
      for ( File file: pFiles )
        if ( filterInTextFiles( file))
          filteredFiles.add( file);
        
      return filteredFiles;
    }  // End Method filterFiles() ======================
    




    // =======================================================
    /**
     * filterInTextFiles returns true if the file is a .string file
     * 
     * @param pFile
     * @return boolean
     */
    // =======================================================
    private boolean filterInTextFiles(File pFile) {
     
      boolean returnValue = false;
      String extension = U.getFileExtension(pFile.getName());
      
      if ( extension != null &&  extension.indexOf("string") > 0  )
            { 
        returnValue = true;
      }
     
      return returnValue;
      
    }  // End Method filterInTextFiles() ======================
    
    

  public static final String PARAM_INPUTFILE = "inputFile";


  // ----------------------------------------
  // Class Variables
  // ----------------------------------------
  
  private int                        fileCounter = 0;
  private int                      numberOfFiles = 0;
  private Vector<File>      listOfFilesToProcess = null;
  
  

  
} // end Class MultiAnnotationRecordCollectionReader() ----
