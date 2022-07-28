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
 * FromKnowtatorSimple assumes that the the source file has already
 * been read in via a collection reader. This class will read in the
 * knowtator annotations and create uima annotations.
 * 
 * This has been broken down into a collection reader which reads in
 * the source text and an annotator which reads in the knowtator annotation
 * because when these two operations were combined, the annotations would
 * not show up.
 * 
 * 
 * @author Guy Divita
 * @created Mar 1, 2011
 * 
 *          *
 *          *
 *          *
 *          *
 *          *
 *          *
 *          *
 *          *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.knowtator;

//=================================================
/**
* FromDatabase reads in data from a database
* and converts them to Cas's.
*
*
* @author  Guy Divita 
* @created Aug 17, 2011
*
* *  
*   *
*   *
*   *
*   *
*   *
*   * 
*   *
* 

*/
//================================================
import gov.va.chir.model.DocumentHeader;
import gov.va.vinci.model.Gold;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;


public class KnowtatorReader extends gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader {


// =======================================================
/**
* Constructor FromText 
*
* @param pInputDir
* @throws ResourceInitializationException 
*/
// =======================================================
public KnowtatorReader(String pInputDir) throws ResourceInitializationException {
 
 initialize( pInputDir, true, false);
 
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
public KnowtatorReader(String pInputDir, boolean pRecurseIntoSubDirs) throws ResourceInitializationException {

initialize( pInputDir, pRecurseIntoSubDirs, false);

} // end Constructor() ---------------------

//=======================================================
/**
* Constructor FromText 
*
* @param pInputDir
* @param pRecurseIntoSubDirs
* @param pFixOFfsets
* 
* @throws ResourceInitializationException 
*/
//=======================================================
public KnowtatorReader(String pInputDir, boolean pRecurseIntoSubDirs, boolean pFixOffsets) throws ResourceInitializationException {

initialize( pInputDir, pRecurseIntoSubDirs, pFixOffsets);

} // end Constructor() ---------------------

// -----------------------------------------
/** 
* getNext retrieves the next knowtator file, finds the text file it came from
* reads both, converts the annotations in the knowtator file to uima annotations.
* 
* @param pCAS
* @throws IOException
* @throws CollectionException
*

*/
// -----------------------------------------
@Override
public void getNext(CAS pCAS) throws IOException, CollectionException {

 File corpusFile = null;
 JCas     jcas = null;  
 try {
   jcas = pCAS.getJCas();
   File  savedFile = this.listOfFilesToProcess.get(this.fileCounter);


   
   try {
     corpusFile = getTextFile( savedFile);
   } catch (Exception e) {
     e.printStackTrace();
     throw new IOException(e.toString());
   }
   
   
   String documentId    = corpusFile.getName();
   String documentURI   = documentId;
   String documentText  = U.readFile(corpusFile);
   String documentTitle = "unknown";
   String documentType  = "unknown";
   String documentName  = documentId; 
   int documentSpan     = documentText.length();
   String patientID     = "unknown";
   String referenceDate = "unknown";
   String metaData      = "unknown";
  
   documentText = fixIssueWithOrphanCarriageReturn(documentText);
   VUIMAUtil.setDocumentHeader(jcas, documentId, documentName, documentType, documentTitle, metaData, patientID, referenceDate, this.fileCounter);
   
   pCAS.setDocumentText( documentText );
  
   // --------------------------------------
   // Add to the cas annotations from the knowtator file
 
   try {
     this.fromKnowtator.fromKnowtator( jcas, corpusFile, savedFile);
     
      
     
   } catch (Exception e2) {
     e2.getStackTrace();
     String msg = "Issue converting the knowtator file " + savedFile + " " + e2.toString();
     System.err.println(msg);
     throw new IOException(msg);
   }

 } catch (Exception e ) {
   e.printStackTrace();
   String msg = "Issue creating a cas  " + " " + e.toString();
   System.err.println(msg);
   throw new CollectionException();
 
 }
  
  
 // --------------------------
 // increment the fileCounter
 this.fileCounter++;
 
} // end Method getNext() -----------------------


// =======================================================
/**
 * fixIssueWithOrphanCarrageReturn looks for the first orphan carriage return 
 * (\r)   without at newline (\n)
 * 
 * It adds a newline before the \r
 * 
 * 
 * @param documentText
 * @return
 */
// =======================================================
private String fixIssueWithOrphanCarriageReturn(String pBuff) {
  
  StringBuffer buff = new StringBuffer();
  
  int a = pBuff.indexOf("\r\n");
  int b = pBuff.indexOf("\r");
  int c = pBuff.indexOf("\n");
  
  if ( a == b && b + 1 == c )
    buff = buff.append(pBuff);
  else if ( a == b ) {
    String x = pBuff.substring(0, b +1);
    String p = pBuff.substring(b+1, pBuff.length());
    buff.append(x + "\n" + p);
  } else {
    buff = buff.append(pBuff);
  }
  
  return buff.toString();
  
  
}  // End Method fixIssueWithOrphanCarrageReturn() ======================


// =======================================================
/**
 * getTextFile retrieves the text file associated with
 * the knowtator file.  This is done by replacing the
 * "saved" part of the path with "corpus"
 * 
 * @param aFile
 * @return
 * @throws Exception 
 */
// =======================================================
private File getTextFile(File aFile) throws Exception {

  File returnVal = null;
  
  String textPath = aFile.getAbsolutePath().replace("/saved/", "/corpus/");
  textPath = textPath.replace("\\saved\\", "\\corpus\\");
  textPath = textPath.replace(".knowtator.xml", "");
  
  
  returnVal = new File( textPath);
  
  if ( !returnVal.canRead())
    throw new Exception ("Cannot read the file that should be at " + textPath);
  
  return returnVal;
}  // End Method getTextFile() ======================


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
* 
* @throws IOException
* @throws CollectionException
*/
// -----------------------------------------
public void initialize() throws ResourceInitializationException  {

 UimaContext aContext = getUimaContext();
  
 try {
   
   this.inputFile = (String) aContext.getConfigParameterValue(PARAM_INPUTFILE);
   System.err.println(" The input file ===== " + this.inputFile);

    initialize ( this.inputFile, true, false);
   
 } catch (Exception e) {
   String msg = "CollectionReader: inputFile was not found ." + e.toString() + U.getStackTrace(e);
   System.err.println(msg);
   throw new ResourceInitializationException();
 }

 } // End Method initialize() ---------------------------
   

//-----------------------------------------
/** 
* initialize reads the directory containing the input files.  By default, this
* recurses through sub directories. By default, this initialize sets fixOFfsets to false.
* 
* @param pInputDir
* @throws ResourceInitializationException

*/
// -----------------------------------------
public void initialize(String pInputDir ) throws ResourceInitializationException  {

initialize( pInputDir, true, false);

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
    
    initialize( inputDir , true,false );
    
   } catch ( Exception e ) {
     e.printStackTrace();
     System.err.println("Issue within method initialize " + e.getMessage() );
     throw e;
   }
 } // End Method initialize ============


// -----------------------------------------
 /** 
  * initialize opens the directory containing .knowtator.xml files
  * 
  *  The inputDir should point to the directory ABOVE the ./saved and ./corpus directories.
  *
  * @param pInputFile
  * @param recurseIntoSubDirs
  * @throws IOException
  * @throws CollectionException
  * 
  * [TBD] read in metadata here and attach the info to each jcas 
  */
 // -----------------------------------------
 public void initialize(String pInputDir, boolean recurseIntoSubDirs, boolean pFixOffsets ) throws ResourceInitializationException  {
   
   
   File inputDir = null;
   this.listOfFilesToProcess = new ArrayList<File>();
   this.fixOffsets = pFixOffsets;
   
   if ( pInputDir != null && pInputDir.length() > 0 ) {
     
     String savedDir = pInputDir + "/saved";
     
     inputDir = new File ( savedDir );
     
     if ( inputDir.isDirectory() && inputDir.canRead() ) {
       
       File[] filesAndSubDirs = inputDir.listFiles();
         
       if ( recurseIntoSubDirs ) {
          getFiles(this.listOfFilesToProcess, filesAndSubDirs );
       } else {
         this.listOfFilesToProcess = filterFiles( filesAndSubDirs) ;
       }
     } // end if the inputDir can be read                
   } // end if the inputDir is not null
   

    this.fromKnowtator = new FromKnowtator( this.labelMap, this.fixOffsets);
   
   
   this.numberOfFiles = this.listOfFilesToProcess.size();
   System.err.println("The number ofiles to process = " + this.numberOfFiles);
   
 
        
} // end Method initialize() --------------
 
 
 /* (non-Javadoc)
  * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#close()
  */
 @Override
 public void close()  {
  
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
     if ( filterInKnowtatorFiles( file))
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
     } else if ( filterInKnowtatorFiles ( file )){
      // System.err.println("adding file " + file.getAbsolutePath());
       pListOfFiles.add( file);
     }
   
 }   // End Method getFiles() ======================
 


 // =======================================================
 /**
  * filterInKnowtatorFiles returns true if the file is a knowtator file
  * 
  * @param pFile
  * @return boolean
  */
 // =======================================================
 private boolean filterInKnowtatorFiles(File pFile) {
  
   boolean returnValue = false;
   if ( pFile.getName().endsWith(".knowtator.xml") ) 
       returnValue = true;
     
   return returnValue;
   
 }  // End Method filterInKnowtatorFiles() ======================
 


public static final String PARAM_INPUTFILE = "inputFile";

private String inputFile;
// ----------------------------------------
// Class Variables
// ----------------------------------------

private int                        fileCounter = 0;
private int                      numberOfFiles = 0;
private List<File>        listOfFilesToProcess = null;
private boolean                     fixOffsets = false;
private String[]                      labelMap = null;
private FromKnowtator            fromKnowtator = null;





} // end Class MultiAnnotationRecordCollectionReader() ----
