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
package gov.nih.cc.rmd.nlp.framework.marshallers.xmi;


import gov.va.chir.model.DocumentHeader;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;


public class FromXMI extends gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader {

  

  // =======================================================
  /**
   * Constructor FromXMI
   *
   */
  // =======================================================
  public FromXMI()  {
    
   
    
  } // end Constructor() ---------------------

//=======================================================
 /**
  * Constructor FromXMI
  * 
  * @param pArgs
  * @throws ResourceInitializationException
  *
  */
 // =======================================================
 public FromXMI( String[] pArgs ) throws ResourceInitializationException {
   
   String inputDir = U.getOption(pArgs, "--inputDir=",  "/some/inputDirectory");
  
   initialize( inputDir, true);
   
   
 } // end Constructor() ---------------------
  
  
	// =======================================================
  /**
   * Constructor FromText 
   *
   * @param pInputDir
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public FromXMI(String pInputDir) throws ResourceInitializationException {
    
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
 public FromXMI(String pInputDir, boolean pRecurseIntoSubDirs) throws ResourceInitializationException {
   
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
	@Override
	public void getNext(CAS pCAS) {
	 

    InputStream inputStream = null;
    File              aFile = null;

    try {
    
      aFile = this.listOfFilesToProcess.get(this.fileCounter);
      
      inputStream = ReadInAndfixLoneCarrageReturn( aFile );
     
      // inputStream = new FileInputStream(aFile);
      
      
      System.err.println(" about to deserialize file " + aFile.toString());
      XmiCasDeserializer.deserialize(inputStream, pCAS, ! mFailOnUnknownType);
    
      
      
      // --------------------------------------------------
      // A file descriptor and an CSI descriptor to the cas (if one doesn't already exist)
      // --------------------------------------------------
      
      addDocumentHeader( pCAS, aFile);
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with converting the xmi to into a cas " +  e.getMessage();
      System.err.println(msg);
    }
    
    try {
      if ( inputStream != null )
      inputStream.close();
    } catch (Exception e) {}
 
		 
    // --------------------------
    // increment the fileCounter
    this.fileCounter++;
    
	} // end Method getNext() -----------------------




	// ==========================================
  /**
   * fixLoneCarrageReturns transforms lone \r's to \n's
   *  from the input xmi stream.  This is done
   *  here because once the cas's text is set in
   *  the de-searialize, it cannot be altered.
   *
   * @param pFile
   * @return InutStream
   * @throws IOException 
   */
  // ==========================================
  private InputStream ReadInAndfixLoneCarrageReturn(File pFile) throws IOException {
    
    InputStream stream = null;
 
    String buff = U.readFile( pFile );
    
    char[] cbuff = buff.toCharArray();
    
    for ( int i = 0; i < cbuff.length - 9; i++ ) {
    
      // Looking for the pattern
      // &#13;#10;
      // &#13;__  <---- change to a #10
    
      if ( cbuff[i+0] == '&' && 
           cbuff[i+1] == '#' &&
           cbuff[i+2] == '1' &&
           cbuff[i+3] == '3' &&
           cbuff[i+4] == ';' ) {
             
             if ( cbuff[i+5] == '&' &&
                  cbuff[i+6] == '#' &&
                  cbuff[i+7] == '1' &&
                  cbuff[i+8] == '0' &&
                  cbuff[i+9] == ';' ) {
               // do nothing
             } else {
               cbuff[i+3] = '0';
             }
      }
    }
    

    
    // convert cbuff back to a string
    buff = new String( cbuff);
    
    // deal with non-ascii chars
    buff = stripNonAsciiChars( buff );
    
    // convert string to inputSream
    stream = new ByteArrayInputStream( buff.getBytes(StandardCharsets.UTF_8));
    
    
    return stream;
  } // end Method fixLoneCarrageReturns() ========================================
  


  // =================================================
  /**
   * stripNonAsciiChars will convert any characters out of print range
   * into spaces. 
   * 
   * @param pBuff
   * @return String
  */
  // =================================================
  private String stripNonAsciiChars( String pBuff) {
    
    String returnVal = pBuff;
    if ( returnVal != null)
      
      // strips off all non-ASCII characters
      returnVal = returnVal.replaceAll("[^\\x00-\\x7F]", " ");
   
      // erases all the ASCII control characters
      returnVal = returnVal.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", " ");
       
      // removes non-printable characters from Unicode
      returnVal = returnVal.replaceAll("\\p{C}", "");
      
      return returnVal;
    
  } // end Method stripNonAsciiChars() --------------


  // =======================================================
  /**
   * addDocumentHeader sets the documentHeader if one has not
   * already been set.
   * 
   * @param pCAS
   * @param aFile
   * @throws CASException 
   */
  // =======================================================
  private void addDocumentHeader(CAS pCAS, File aFile) throws CASException {

    
    JCas jCas = pCAS.getJCas();
    
    DocumentHeader documentHeader = VUIMAUtil.getDocumentHeader(jCas);
    
    if ( documentHeader == null ) {
      String       documentId = aFile.getAbsolutePath();
      String     documentName = aFile.getName();
      String     documentType = "unknown";
      String    documentTitle = "unknown";
      String documentMetaData = "";
      String        patientId = "unknown";
      String    referenceDate = "01/01/1900";
      VUIMAUtil.setDocumentHeader( jCas, documentId, documentName, documentType, documentTitle, documentMetaData, patientId, referenceDate, this.fileCounter);
    }
    
   
   
  } // End Method addDocumentHeader() ======================
  
  // -----------------------------------------
	/** 
	 * getProgress is method required for the 
	 * interface that is populated with the 
	 * fraction of files processed by the number of files to process.
	 *
	 * @return Progress[]
	 */
	// -----------------------------------------
	@Override
	public Progress[] getProgress() {
		Progress[] p = new Progress[] { new ProgressImpl(this.fileCounter, this.numberOfFiles, Progress.ENTITIES)};
		return p;

	} // end Method getProgress() --------------

	// -----------------------------------------
	/** 
	 * hasNext
	 *
	 * @return
	 * @throws IOException
	 * @throws CollectionException
	 */
	// -----------------------------------------
	@Override
	public boolean hasNext()  {
		boolean returnValue = false;
		

	  if (this.fileCounter < this.numberOfFiles ) 
		  returnValue = true;
	  
		 return returnValue;
	} // end Method hasNext() -----------------
	
	

  // -----------------------------------------
	/** 
	 * initialize opens the file that has the multi-AnnotationRecord files.
	 * 
	 * 
	 * This method relies on the config variable "inputFile"
	 *
	 * @return
	 * @throws IOException
	 * @throws CollectionException
	 */
	// -----------------------------------------
	public void initialize() throws ResourceInitializationException  {

	  UimaContext aContext = getUimaContext();
     
	  try {
	    
	    this.inputFile = (String) aContext.getConfigParameterValue(PARAM_INPUTFILE);
	    System.err.println(" The input file ===== " + this.inputFile);
	 
	     initialize ( this.inputFile, true);
	    
	  } catch (Exception e) {
      String msg = "CollectionReader: inputFile was not found ." + e.toString() + U.getStackTrace(e);
      System.err.println(msg);
      throw new ResourceInitializationException();
	  }
	 
	  } // End Method initialize() ---------------------------

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
 
 
//=======================================================
 /**
  * initialize 
  * 
  * @param pArgs    assumes there is the arg --inputDir= 
  * @throws ResourceInitializationException
  *
  */
 // ======================================================
public void initialize(String[] pArgs) throws ResourceInitializationException {
  
  
   try {
  
     String inputDir = U.getOption(pArgs, "--inputDir=", "./");
     
     initialize( inputDir  );
    } catch ( Exception e ) {
      e.printStackTrace();
      System.err.println("Issue within method initialize " + e.getMessage() );
      throw e;
    }
  } // End Method initialize ============
 
	// -----------------------------------------
	  /** 
	   * initialize opens the directory containing .xmi files 
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
	    System.err.println("The input dir = " + pInputDir) ;
	    if ( pInputDir != null && pInputDir.length() > 0 ) {
	      
	      inputDir = new File ( pInputDir);
	      
	      if ( inputDir.isDirectory() && inputDir.canRead() ) {
	        
	        File[] filesAndSubDirs = inputDir.listFiles();
	          
	        if ( recurseIntoSubDirs ) {
	           getFiles(this.listOfFilesToProcess, filesAndSubDirs );
	        } else {
	          this.listOfFilesToProcess = filterFiles( filesAndSubDirs) ;
	        }
	      } else { // end if the inputDir can be read	      	      
	        System.err.println("COULD NOT READ " + pInputDir);
	      }
	    } // end if the inputDir is not null
	    
	
	    
      this.numberOfFiles = this.listOfFilesToProcess.size();
      super.setCatalog(this.numberOfFiles);
      
      
      System.err.println("The number ofiles to process = " + this.numberOfFiles);
      
    
	  	 	   
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
    private List<File> filterFiles(File[] pFiles) {
    
      ArrayList<File> filteredFiles = new ArrayList<File>(pFiles.length);
      for ( File file: pFiles )
        if ( filterInXMIFiles( file))
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
        } else if ( filterInXMIFiles ( file )){
          pListOfFiles.add( file);
        }
      
    }   // End Method getFiles() ======================
    


    // =======================================================
    /**
     * filterInXMIFiles returns true if the file is an XMI file
     * 
     * @param pFile
     * @return boolean
     */
    // =======================================================
    private boolean filterInXMIFiles(File pFile) {
     
      boolean returnValue = false;
      String extension = U.getFileExtension(pFile.getName());
      if ( extension == null ||
           extension.indexOf("xmi") > 0 )
          
        returnValue = true;
      
      return returnValue;
      
    }  // End Method filterInTextFiles() ======================
    


  public static final String PARAM_INPUTFILE = "inputFile";

  private String inputFile;
  // ----------------------------------------
	// Class Variables
	// ----------------------------------------
	
	private int                        fileCounter = 0;
	private int                      numberOfFiles = 0;
  private List<File>        listOfFilesToProcess = null;
  
  
  private Boolean mFailOnUnknownType = false;

  
  
  
  
} // end Class MultiAnnotationRecordCollectionReader() ----
