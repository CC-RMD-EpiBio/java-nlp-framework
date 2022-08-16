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
package gov.nih.cc.rmd.nlp.framework.marshallers.file;



import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;



public class FromFile extends gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader {

  
//=======================================================
 /**
  * Constructor FromFile is called from SuperReader
  *  assumes that the initialize() method will be called later on.
  *
  */
 // =======================================================
 public FromFile()  {
   
   
 } // end Constructor() ---------------------
 
  
  
	// =======================================================
  /**
   * Constructor FromFile
   *
   * @param pInputFile
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public FromFile(String pInputFile) throws ResourceInitializationException {
    
    initialize( pInputFile );
    
  } // end Constructor() ---------------------
  

  

//=======================================================
 /**
  * Constructor FromText 
  *
  * @param pInputFile
  * @param pRecurseIntoSubDirs
  * 
  * @throws ResourceInitializationException 
  */
 // =======================================================
 public FromFile(String pInputFile, boolean pRecurseIntoSubDirs) throws ResourceInitializationException {
   
   initialize( pInputFile );
   
 } // end Constructor() ---------------------
  
  // -----------------------------------------
	/** 
	 * getNext retrieves the next document passed in either by the init method, or
	 * the setFile() method.
	 * @param pCAS
	 * @throws IOException
	 * @throws CollectionException
	 */
	// -----------------------------------------
	public synchronized void getNext(CAS pCAS) throws IOException, CollectionException {
	 
	    try {
      
          get(pCAS, this.fileCounter);  // <---------------------------- this is the meat of the method
      
      } catch ( Exception e) {
       
        e.printStackTrace();
        System.err.println("In getNext 1 Issue with records in thread " +  Thread.currentThread().getName() + " " + e.toString());
        return;
      }
 		 
    // --------------------------
    // increment the fileCounter
	    
	    
	    this.inputFileNameHash.put(this.inputFile + this.fileCounter, "PROCESSED");
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
 public synchronized void get(CAS pCAS, int pRecordId) throws IOException, CollectionException {
  
   
   JCas jcas = null;  
   try {
     jcas = pCAS.getJCas();
     File aFile = null;
     
    
     
     try {
     
       aFile = new File( this.inputFile );
     
     } catch ( Exception e) {
       // ---------------------
       // In multi-treaded environments - a process could sneak in an take the
       //                                 last record between the time this thread
       //                                 calls hasNext and getNext.
       //                                 This catch will be the catch to figure
       //                                 out that there are no more records to process
       e.printStackTrace();
       System.err.println("In getNext of FromFile 1 Issue with records in thread " +  Thread.currentThread().getName() + " " + e.toString());
       return;
     }
     
      
     String documentId    = aFile.getName();
     
     String documentText  = U.readFile(aFile);
     String documentTitle = "unknown";
     String documentType  = "unknown";
     String documentName  = documentId; 
     String patientID     = "unknown";
     String referenceDate = "unknown";
     String metaData      = "unknown";
 
     pCAS.setDocumentText( documentText );
     VUIMAUtil.setDocumentHeader( jcas, documentId, documentName, documentType, documentTitle, metaData, patientID, referenceDate, pRecordId);
  
                 
  
   } catch (Exception e ) {
     e.printStackTrace();
    System.err.println("Something went wrong with fromDatabase " + e.toString());
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
		Progress[] p = new Progress[] { new ProgressImpl(this.fileCounter, this.numberOfFiles, Progress.ENTITIES)};
		return p;

	} // end Method getProgress() --------------

	// -----------------------------------------
	/** 
	 * hasNext  returns true only if fileNameHash.processed( pInputFile) is false;
	 *
	 * @return boolean
  */
	// -----------------------------------------
	public synchronized boolean hasNext()  {
		boolean returnValue = false;
		
		String processed =  this.inputFileNameHash.get( this.inputFile + this.fileCounter);
		
		if ( processed != null ) 
		  if   ( processed.equals("PROCESSED"))  returnValue = false;
		  else                                   returnValue = true;
		  
		
		 return returnValue;
	} // end Method hasNext() -----------------
	
	



 
	// -----------------------------------------
	  /** 
	   * initialize sets the inputFileName.  The inputFileName can also
	   * be set by the setInputFileName() method.
	   *
	   * @param pInputFileName
	   *
	   * @throws ResourceInitializationException
	   */
	  // -----------------------------------------
	  public void initialize(String pInputFileName ) throws ResourceInitializationException  {
	    
	      this.inputFile = pInputFileName;
	      
	      this.inputFileNameHash = new HashMap<String,String>();
	      this.inputFileNameHash.put(this.inputFile + this.fileCounter, "NOT PROCESSED");
	  
	      
	      this.listOfFilesToProcess = new ArrayList<>(1);
	      this.listOfFilesToProcess.add( new File (this.inputFile));
	
	   
	    this.setCatalog(this.listOfFilesToProcess);
      this.numberOfFiles = this.listOfFilesToProcess.size();
      System.err.println("The number ofiles to process = " + this.numberOfFiles);
      
    
	  	 	   
	} // end Method initialize() --------------
	  
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
	   
	      String inputFile = U.getOption(pArgs, "--inputFile=", "someFile.txt");
	      
	      initialize( inputFile  );
	     } catch ( Exception e ) {
	       e.printStackTrace();
	       System.err.println("Issue within method initialize " + e.getMessage() );
	       throw e;
	     }
	   } // End Method initialize ============
		
	// -----------------------------------------
    /** 
     * setIputFileName sets the inputFileName.  
     * 
     * @param pInputFileName
     *
     * @throws ResourceInitializationException
     */
    // -----------------------------------------
	  public void setInputFileName(String pInputFileName ) throws ResourceInitializationException  {
      
      this.inputFile = pInputFileName;
      
     this.inputFileNameHash.put(this.inputFile + this.fileCounter, "NOT PROCESSED");
     this.listOfFilesToProcess.add( new File (this.inputFile));

     this.numberOfFiles = this.listOfFilesToProcess.size();
   
         
} // end Method setInputFileName() --------------  
	  
	  
		
	  /* (non-Javadoc)
	   * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#close()
	   */
	  @Override
	  public void close() {
	   
	  }  // End Method close() ======================
	  
	 

  public static final String PARAM_INPUTFILE = "inputFile";


  // ----------------------------------------
	// Class Variables
	// ----------------------------------------
	private String                       inputFile = null;
	private HashMap<String, String> inputFileNameHash;


  private int                        fileCounter = 0;
	private int                      numberOfFiles = 0;
  private ArrayList<File>        listOfFilesToProcess = null;
  
  
  

	
} // end Class MultiAnnotationRecordCollectionReader() ----
