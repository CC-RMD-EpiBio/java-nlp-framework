/*******************************************************************************
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                       Epidemiology and Biostatistics Branch 
 *                                            2019 - 2022
 *   ---------------------------------------------------------------------------
 *   Copyright Notice:
 *   This software was developed and funded by the National Institutes of Health
 *   Clinical Center (NIHCC), part of the National Institutes of Health (NIH),
 *   and agency of the United States Department of Health and Human Services,
 *   which is making the software available to the public for any commercial
 *   or non-commercial purpose under the following open-source BSD license.
 *  
 *   Government Usage Rights Notice:
 *   The U.S. Government retains unlimited, royalty-free usage rights to this 
 *   software, but not ownership, as provided by Federal law. Redistribution 
 *   and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *      1. Redistributions of source code must retain the above copyright
 *         and government usage rights notice, this list of conditions and the 
 *         following disclaimer.
 *  
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *        
 *      3. Neither the names of the National Institutes of Health Clinical
 *         Center, the National Institutes of Health, the U.S. Department of
 *         Health and Human Services, nor the names of any of the software
 *         developers may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
 *   
 *      4. The U.S. Government retains an unlimited, royalty-free right to
 *         use, distribute or modify the software.
 *   
 *      5. Please acknowledge NIH CC as the source of this software by including
 *         the phrase: "Courtesy of the U.S. National Institutes of Health Clinical Center"
 *          or 
 *                     "Source: U.S. National Institutes of Health Clinical Center."
 *  
 *     THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS
 *     IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *     TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *     PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 *     OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *     EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *     PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *     NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *     When attributing this code, please make reference to:
 *        Divita G, Carter ME, Tran LT, Redd D, Zeng QT, Duvall S, Samore MH, Gundlapalli AV. 
 *        v3NLP Framework: tools to build applications for extracting concepts from clinical text. 
 *        eGEMs. 2016;4(3). 
 *      
 *     In the absence of a specific paper or url listed above, reference https://github.com/CC-RMD-EpiBio/java-nlp-framework
 *   
 *     To view a copy of this license, visit https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD
 *******************************************************************************/
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
