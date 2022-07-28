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
package gov.nih.cc.rmd.nlp.framework.marshallers.text;



import gov.va.chir.model.DocumentHeader;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import gate.Gate;
import gov.nih.cc.rmd.nlp.framework.marshallers.reader.AddNewLinesTool;
import gov.nih.cc.rmd.nlp.framework.utils.FileMetaData;
import gov.nih.cc.rmd.nlp.framework.utils.GleanMetaDataFromFileName;



public class FromText extends gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader {

  
//=======================================================
 /**
  * Constructor FromText is called from SuperReader
  *  assumes that the initialize() method will be called later on.
  *
  */
 // =======================================================
 public FromText()  {
   
   
 } // end Constructor() ---------------------
 
  
  
	// =======================================================
  /**
   * Constructor FromText 
   *
   * @param pInputDir
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public FromText(String pInputDir) throws ResourceInitializationException {
    
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
 public FromText(String pInputDir, boolean pRecurseIntoSubDirs) throws ResourceInitializationException {
   
   initialize( pInputDir, pRecurseIntoSubDirs);
   
 } // end Constructor() ---------------------
  
  // =================================================
/**
 * Constructor
 *
 * @param args
 * @throws ResourceInitializationException 
 * 
**/
// =================================================
public FromText(String[] args) throws ResourceInitializationException {
  
   initialize( args);
}



  // -----------------------------------------
	/** 
	 * getNext retrieves the next document id from the list of document ids, queries the database fro 
	 * annotations that share this document id, creates annotations for this id.
	 * @param pCAS
	 * @throws IOException
	 * @throws CollectionException
	 */
	// -----------------------------------------
	public synchronized void getNext(CAS pCAS) throws IOException, CollectionException {
	 
	    try {
        if ( this.fileCounter < this.numberOfFiles )
          get(pCAS, (long) this.fileCounter);  // <---------------------------- this is the meat of the method
        else {
          GLog.println(GLog.DEBUG_LEVEL,"In getNext 1 Issue with records in thread " +  Thread.currentThread().getName() + " jumped over the last in the array" );
        return;
      }
      } catch ( Exception e) {
        // ---------------------
        // In multi-treaded environments - a process could sneak in an take the
        //                                 last record between the time this thread
        //                                 calls hasNext and getNext.
        //                                 This catch will be the catch to figure
        //                                 out that there are no more records to process
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL,this.getClass(), "getNext","In getNext 1 Issue with records in thread " +  Thread.currentThread().getName() + " " + e.toString());
        return;
      }
 		 
    // --------------------------
    // increment the fileCounter
	    
	    this.fileCounter++;
    
	} // end Method getNext() -----------------------


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
  
   
   JCas jcas = null;  
   try {
     jcas = pCAS.getJCas();
     File aFile = null;
     
    
     
     try {
     
       aFile = this.listOfFilesToProcess.get( (int) pRecordId);
     
     } catch ( Exception e) {
       // ---------------------
       // In multi-treaded environments - a process could sneak in an take the
       //                                 last record between the time this thread
       //                                 calls hasNext and getNext.
       //                                 This catch will be the catch to figure
       //                                 out that there are no more records to process
       e.printStackTrace();
       GLog.println(GLog.ERROR_LEVEL,this.getClass(), "get","Issue with records in thread " +  Thread.currentThread().getName() + " " + e.toString());
       return;
     }
     
     
     
     
     
     String documentText  = null;
     String documentId    = aFile.getAbsolutePath(); //   used to be aFile.getName();  needs to carry along the full path
     if ( documentId.contains( "\\"))
       documentId = documentId.replace('\\', '/');
     GLog.println(GLog.STD___LEVEL,this.getClass(), "get"," --------->  Reading in " + aFile.getName() );
    
    
       documentText = U.readFile(aFile);
       
       if ( this.addNewLines )
         documentText = this.addNewLineTool.addBTISNewLines( documentText );
       
       // -----------------------------
       // Deal with BTRIS De-identification markers
       //  [LAST_NAME i=459] 
       // ----------------------------
       if ( this.deIdentified ) {
         documentText = this.addNewLineTool.stripBTRISDeidentifyMarkers( documentText );
       }
     
     String documentTitle = "unknown";
     FileMetaData fileMetaData = GleanMetaDataFromFileName.getFileMetaData( documentId);
     String documentType  = fileMetaData.getDocumentType();
     String documentName  = fileMetaData.getDocumentName(); 
     String patientID     = fileMetaData.getPatientId();
     String referenceDate = fileMetaData.getDocumentDate();
     int           pageNo = fileMetaData.getPageNumber(); 
     String metaData      = "unknown";
 
    
     DocumentHeader documentHeader = VUIMAUtil.setDocumentHeader( jcas, documentId, documentName, documentType, documentTitle, metaData, patientID, referenceDate, pRecordId);
  
     documentHeader.setPageNumber( pageNo);
     
     VUIMAUtil.setDocumentText(jcas, documentName,  documentType, referenceDate, documentText, this.setMetaDataHeader );
  
   } catch (Exception e ) {
     e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL,this.getClass(), "get","Something went wrong with fromDatabase " + e.toString());
   }
    
   
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
		Progress[] p = new Progress[] { new ProgressImpl((int) this.fileCounter, this.numberOfFiles, Progress.ENTITIES)};
		return p;

	} // end Method getProgress() --------------

	// -----------------------------------------
	/** 
	 * hasNext
	 *
	 * @return
	 * @throws IOException
	 * @throws CollectionException  <---- why not anymore?
	 */
	// -----------------------------------------
	public synchronized boolean hasNext()  {
		boolean returnValue = false;
		

	  if (this.fileCounter < this.numberOfFiles ) 
		  returnValue = true;
	  
		 return returnValue;
	} // end Method hasNext() -----------------
	
	


//=======================================================
/**
 * initialize 
 * 
 * @param pArgs    assumes there is the arg --inputDir= and optionally --recurseIntoSubDirs=[true|false]
 * @throws ResourceInitializationException
 *
 */
// ======================================================
public void initialize(String[] pArgs) throws ResourceInitializationException {
 
 
  try {
 
    String inputDir = U.getOption(pArgs, "--inputDir=", "./");
    boolean recurseIntoSubDirs = Boolean.parseBoolean(U.getOption(pArgs,  "--recurseIntoSubDirs=", "true"));
     this.setMetaDataHeader = Boolean.parseBoolean(U.getOption(pArgs,  "--setMetaDataHeader=", "false"));
     this.addNewLines = Boolean.parseBoolean(U.getOption(pArgs,  "--addNewLines=", "false")); 
     this.deIdentified = Boolean.parseBoolean(U.getOption(pArgs,  "--deIdentified=", "false" )); 
     
     String fileCtr = U.getOption(pArgs, "--fileCounter=", "0" );
     this.fileCounter = Integer.valueOf(fileCtr);
    
    
    
    initialize( inputDir, recurseIntoSubDirs);
    
    this.addNewLineTool = new AddNewLinesTool( pArgs );
   
    
    
   } catch ( Exception e ) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL,this.getClass(), "initialize", "Issue within method initialize " + e.getMessage() );
     throw new ResourceInitializationException();
   }
 } // End Method initialize ============

	

//-----------------------------------------
 /** 
  * initialize reads the directory containing the input files.  By default, this
  * recurses through sub directories.
  * 
  * @param pInputDir
  * @throws ResourceInitializationException

  */
 // -----------------------------------------
 public void initialize(String pInputDir ) throws ResourceInitializationException  {
   
   initialize( pInputDir, true);
   
 } // End Method initialize() ---------------------------
 
 
	// -----------------------------------------
	  /** 
	   * initialize opens the directory containing .txt files and files 
	   * that do not have an extension 
	   * 
	   *
	   * @param pInputFile
	   * @param recurseIntoSubDirs
	   * @throws IOException
	   * @throws CollectionException
	   */
	  // -----------------------------------------
	  public void initialize(String pInputDir, boolean recurseIntoSubDirs ) throws ResourceInitializationException  {
	    
	    
	    File inputDir = null;
	    this.listOfFilesToProcess = new ArrayList<File>();
	    
	    if ( pInputDir != null && pInputDir.length() > 0 ) {
	      
	      inputDir = new File ( pInputDir);
	      
	      if ( inputDir.isDirectory() && inputDir.canRead() ) {
	        
	        File[] filesAndSubDirs = inputDir.listFiles();
	          
	        if ( recurseIntoSubDirs ) {
	           getFiles(this.listOfFilesToProcess, filesAndSubDirs );
	        } else {
	          this.listOfFilesToProcess = filterFiles( filesAndSubDirs) ;
	        }
	      } // end if the inputDir can be read	      	      
	    } // end if the inputDir is not null
	    
	
	   
	    this.setCatalog(this.listOfFilesToProcess);
      this.numberOfFiles = this.listOfFilesToProcess.size();
      GLog.println(GLog.STD___LEVEL,this.getClass(), "initialize", "The number ofiles to process = " + this.numberOfFiles);
      
    
	  	 	   
	} // end Method initialize() --------------
		
		
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
    private ArrayList<File> filterFiles(File[] pFiles) {
    
      ArrayList<File> filteredFiles = new ArrayList<File>(pFiles.length);
      for ( File file: pFiles )
        if ( filterInTextFiles( file))
          filteredFiles.add( file);
        
      return filteredFiles;
    }  // End Method filterFiles() ======================
    


    // =======================================================
    /**
     * getFiles retrieves the list of files from the directory
     * that match the filtering criteria
     * 
     * @param pListOfFiles
     * @param pFilesAndSubDirs
     * 
     */
    // =======================================================
    private void  getFiles(List<File> pListOfFiles, File[] pFilesAndSubDirs) {
      
      for ( File file: pFilesAndSubDirs )
        if ( file.isDirectory()) {
          getFiles( pListOfFiles, file.listFiles());
        } else if ( filterInTextFiles ( file )){
         // System.err.println("adding file " + file.getAbsolutePath());
          pListOfFiles.add( file);
        }
      
    }   // End Method getFiles() ======================
    


    // =======================================================
    /**
     * filterInTextFiles returns true if the file is a text file
     * 
     * @param pFile
     * @return boolean
     */
    // =======================================================
    protected boolean filterInTextFiles(File pFile) {
     
      boolean returnValue = false;
      String extension = U.getFileExtension(pFile.getName());
      
      if ( extension == null ||
           extension.indexOf("txt") > 0  ||
           // extension.indexOf("json") > 0  ||  <---- this should have its own reader
           extension.indexOf("text") > 0  ||
           extension.indexOf("note") > 0  ||
           extension.indexOf("rpt") > 0 ) { 
        returnValue = true;
      }
     
      return returnValue;
      
    }  // End Method filterInTextFiles() ======================
    
    

  public static final String PARAM_INPUTFILE = "inputFile";


  // ----------------------------------------
	// Class Variables
	// ----------------------------------------
	
	// protected int                       fileCounter = 0;
	protected int                      numberOfFiles = 0;
  protected ArrayList<File>        listOfFilesToProcess = null;
  protected boolean                  setMetaDataHeader = true;
  protected boolean                  addNewLines = false;
  protected boolean                  deIdentified = false;
  protected AddNewLinesTool           addNewLineTool = null;
  
  
  /**
   * Collection reader params. Currently it just uses the same params as BaseFileSubReader.
   */
 // public static class Param extends BaseFileSubReader.Param {

 // }



	
} // end Class MultiAnnotationRecordCollectionReader() ----
