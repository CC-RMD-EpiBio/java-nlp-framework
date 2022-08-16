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
 * FromVTTSnippets takes vtt files that are snippets of annotations in lots of files
 * and puts them back together into the original documents.
 * 
 * 
 * @author  Guy Divita 
 * @created Sept 3, 2013
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.snippet;


import gov.va.chir.model.DocumentHeader;
import gov.va.vinci.model.Concept;
import gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader;
import gov.nih.cc.rmd.nlp.framework.utils.U;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;


public class FromVTTSnippets extends Reader {

	public static final String PARAM_INPUTFILE = "inputFile";

  public static final String PARAM_INPUTDIR = "InputDirectory";
  public static final String PARAM_FAILUNKNOWN = "FailOnUnknownType";

  private static final String VTT_TEXT_BEGIN_DELIMIER =
      "#<---------------------------------------------------------------------->\n" + 
      "#<Text Content>\n" + 
      "#<---------------------------------------------------------------------->\n";


  private static final String VTT_TEXT_END_DELIMITER = 
      "#<---------------------------------------------------------------------->\n" +
      "#<Tags Configuration>";
  
  private static final String VTT_MARKUPS_BEGIN_DELIMITER = 
      "#<---------------------------------------------------------------------->\n" + 
      "#<MarkUps Information>\n" + 
      "#<Offset|Length|TagName|TagCategory|Annotation|TagText>\n" +
      "#<---------------------------------------------------------------------->\n";

  //private String inputFile;
  protected ArrayList<File> mFiles;
  protected int mCurrentIndex;
  
  
  // =======================================================
   /**
    * Constructor
    * 
    * @param pArgs
   * @throws ResourceInitializationException 
    */
   // ======================================================
  public FromVTTSnippets(String[] pArgs) throws ResourceInitializationException {
  
    initialize( pArgs);
  } // End Constructor FromVTTSnippets =============
    
  
//=======================================================
  /**
   * Constructor  (called from the CombineSnippetWithXMI class)
   * 
   * When accessed from CombineSnippetWithXMI, this reader isn't 
   * used, but the methods that parse through the snippet files
   * are used. Thus, the init isn't called, and the getNext isn't
   * called from here either.
   * 
  * @throws ResourceInitializationException 
   */
  // ======================================================
 public FromVTTSnippets( ) throws ResourceInitializationException {
 
  
 } // End Constructor FromVTTSnippets =============
   
 
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
	public void getNext(CAS pCAS) throws IOException, CollectionException {
	 
	  System.err.println("In getNext");
    JCas jcas = null;
    String buff = null;
    try {
      jcas = pCAS.getJCas();
      
      File currentFile = (File) mFiles.get(mCurrentIndex++);
      
     
      try {
        System.err.println(" about to absorb file " + currentFile.toString());
        buff = U.readFile(currentFile);
      } catch (Exception e) {
        return;
      }
     
    
      String documentId    = currentFile.getName();
      String documentURI   = documentId;
      String documentText  = buff;
      String documentTitle = "";
      String documentType  = "concordance";
      String documentName  = documentId; 
      int documentSpan     = documentText.length();
      String patientID     = "";
      String referenceDate = "";
      String metaData      = "";
      jcas.setDocumentText(documentText);
     
        
      
   
      createDocumentHeaderAnnotation( jcas, documentId, documentTitle, documentType, documentName,
          documentSpan, patientID, referenceDate, metaData );
   
     // createConcordanceFocusAnnotations(jcas,buff);
      createFocusAnnotations( jcas, buff);
   
    } catch (Exception e ) {
      e.printStackTrace();
     System.err.println("Something went wrong with fromConcordance " + e.toString());
    }
     
		 
    // --------------------------
    // increment the fileCounter
    this.fileCounter++;
    
	} // end Method getNext() -----------------------


  
//=======================================================
 /**
  * createFocusAnnotations 
  * 
  * @param pJCas
  * @param pBuff
 * @throws Exception 
  */
 // =======================================================
 private void createFocusAnnotations(JCas pJCas, String pBuff) throws Exception {
  
   // ---------------------------
   // parse the annotation into contexts
   
   List<Context> contexts = Context.fromContexts( pBuff);
   
   if ( contexts != null && contexts.size() > 0 )
     for ( Context Context1: contexts )
       createFocusAnnotation( pJCas, Context1);
  
 
 } // End Method createFocusAnnotations() ======================
 


 

  // =======================================================
  /**
   * createFocusAnnotation 
   * 
   * @param pJCas
   * @param pRow
   * @param pRowOffset
   */
  // =======================================================
  private void createFocusAnnotation(JCas pJCas, Context pContext ) {
    
    
    Concept concept = new Concept( pJCas);
    concept.setBegin( pContext.getFocusBeginOffset() );
    concept.setEnd(   pContext.getFocusEndOffset() );
    concept.addToIndexes();

    
    
  } // End Method createFocusAnnotation() ======================

  
  // ------------------------------------------
  /**
   * createDocumentHeaderAnnotation
   *
   *
   * @param pJCas
   * @param pDocumentId
   * @param pDocumentTitle
   * @param pDocumentType
   * @param pDocumentName
   * @param pDocumentSpan
   * @param pPatientId
   * @param pReferenceDate,
   * @param pDocumentMetaData
   */
  // ------------------------------------------
  private void createDocumentHeaderAnnotation(JCas   pJCas, 
                                              String pDocumentId, 
                                              String pDocumentTitle, 
                                              String pDocumentType,
                                              String pDocumentName,
                                              int    pDocumentSpan,
                                              String pPatientId,
                                              String pReferenceDate,
                                              String pDocumentMetaData) {
    
    DocumentHeader documentHeader = new DocumentHeader(pJCas);
    
    documentHeader.setDocumentId( pDocumentId);
    documentHeader.setDocumentName( pDocumentName);
    documentHeader.setBegin( 0 );
    documentHeader.setEnd( pDocumentSpan);
    documentHeader.setDocumentType( pDocumentType);
    documentHeader.setDocumentTitle( pDocumentTitle);
    documentHeader.setOtherMetaData( pDocumentMetaData);
    documentHeader.setPatientID( pPatientId);
    documentHeader.setReferenceDate(pReferenceDate);    
    documentHeader.addToIndexes(pJCas);
    
    
  }  // End Method createDocumentHeaderAnnotation() -----------------------



  // -----------------------------------------
	/** 
	 * destroy
	 *
	 */
	// -----------------------------------------
	@Override
  public void destroy()  {
		 if ( this.performanceMeter != null )
		       this.performanceMeter.writeProfile( this.getClass().getSimpleName());
	} // end Method close() --------------------

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
	public boolean hasNext() throws CollectionException {
		boolean returnValue = false;
		

	  if (this.fileCounter < this.numberOfFiles ) 
		  returnValue = true;
	  
		 return returnValue;
	} // end Method hasNext() -----------------
	
//-----------------------------------------
 /** 
  * initialize retrieves the config parameters
  * and opens the dir that includes the multi-record files.
  * 
  * This method relies on the config variable "corpusDir".
  * The context variables are explicitly set in the FlapDatabaseReaderClient
  * from command line variables passed in through the script that kicks
  * the client off. 
  *
  * @return
  * @throws IOException
  * @throws CollectionException
  */
 // -----------------------------------------
 public void initialize() throws ResourceInitializationException  {
  super.initialize();
   
 }
	
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
     
     initialize2( inputDir  );
    } catch ( Exception e ) {
      e.printStackTrace();
      System.err.println("Issue within method initialize " + e.getMessage() );
      throw e;
    }
  } // End Method initialize ============
	
	// =======================================================
 /**
  * initialize @see gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader#initialize(java.lang.String)
  * 
  * @param pInputFile
  * @throws ResourceInitializationException
  *
  */
 // ======================================================
@Override
public void initialize(String pInputFile) throws ResourceInitializationException {

  initialize2( pInputFile);
  
  } // End Method initialize ============



  // -----------------------------------------
	/** 
	 * initialize2
	 * 
	 *
	 * @return
	 * @throws IOException
	 * @throws CollectionException
	 */
	// -----------------------------------------
	public void initialize2(String pInputDir) throws ResourceInitializationException  {

	 
    
	  try {
  
	    File directory = new File( pInputDir.trim());
	    mCurrentIndex = 0;
	    // if input directory does not exist or is not a directory, throw exception
	    if (!directory.exists() || !directory.isDirectory()) {
	      System.err.println("Issue reading in the directory of concordance files " );
	      
	      throw new ResourceInitializationException(ResourceConfigurationException.DIRECTORY_NOT_FOUND,
	    
	          new Object[] { PARAM_INPUTDIR, this.getMetaData().getName(), directory.getPath() });
	    } 
	     

	    // get list of .xmi files in the specified directory
	    // ArrayList<File> vttFiles = new ArrayList<File>();
	    File[] files = directory.listFiles();
	    for (int i = 0; i < files.length; i++) {
	      System.err.println("Looking at file " + files[i].getAbsolutePath());
	      if (
	           files[i].getName().endsWith(".vtt") &&
	          !files[i].isDirectory() && 
	          !files[i].getName().endsWith(".xmi") &&
	          !files[i].getName().endsWith(".xml")) {
	        System.err.println("Adding file " + files[i].getAbsolutePath());
	        
	         List<File>filesFromVtt = getOriginalFiles( files[i]);
	         mFiles.addAll( filesFromVtt);
	      }
	    }
	    
	    // ------------------------------------
	    // Each vtt file contains contexts from a bunch of text files
	    // Create a list of text files from reading thru the vtt contexts
	    // There is a gentlemans agreement that the vtt file won't have contexts
	    // from a partial text file.  
	    
	    
	    this.numberOfFiles = mFiles.size();
	 

	  } catch (Exception e) {
      String msg = "CollectionReader: inputFile was not found ." + e.toString() + U.getStackTrace(e);
      System.err.println(msg);
      throw new ResourceInitializationException();
	  }
	 
	  } // End Method initialize() ---------------------------
		
  // ----------------------------------------
	// Class Variables
	// ----------------------------------------
	
 //=======================================================
 /**
  * getSnippets read through the pFile and adds to the class variable
  * this.fileBySnippetHash  the list of snippets from the file
  * 
  * This.fileBySnippetHash is key'd by documentId
  * 
  * @param pFile
  * @throws Exception 
  */
 // =======================================================
 public void getSnippets(File pFile ) throws Exception {
   
   String buff = U.readFile(pFile);
   List<Context>snippets = getSnippets(buff);
   String[]      markups = getMarkups( buff);
   marryMarkupsWithContexts(snippets, markups);
   
   this.fileBySnippetHash = createFileSnippetHash( snippets);
   
   
 }  // End Method getOriginalFiles() ======================
	
	// =======================================================
  /**
   * getOriginalFiles read through the pFile and add to the mFiles
   * the filenames of each of the original files that the contexts
   * are from.
   * 
   * @param pFile
   * @throws Exception 
   */
  // =======================================================
  private List<File> getOriginalFiles(File pFile) throws Exception {
    
   getSnippets( pFile);
    // iterate through the fileBySnippetHash
    // create a list of files
    List<File> files = createListOfFilesFromFileBySnippetHash( this.fileBySnippetHash);
    return ( files );
 
  }  // End Method getOriginalFiles() ======================
  

  // =======================================================
  /**
   * createListOfFilesFromFileBySnippetHash 
   * 
   * @param fileBySnippetHash
   * @return
   */
  // =======================================================
  private List<File> createListOfFilesFromFileBySnippetHash(HashMap<String, List<Context>> pFileBySnippetHash) {
    
  ArrayList<File> files = new ArrayList<File>();  
   Set<String> keys = pFileBySnippetHash.keySet();
   
   for ( String key: keys) {
    // files.add( new File ( this.corpusDir + "/" + key));
   }
   
   return files;
    
  } // End Method createListOfFilesFromFileBySnippetHash() ======================
  


  // =======================================================
  /**
   * createFileSnippetHash returns a hash of all the snippets for a given documentID
   * 
   * @param snippets
   * @return HashMap<String,List<Context>>
   */
  // =======================================================
  private HashMap<String, List<Context>> createFileSnippetHash(List<Context> snippets) {
    
    HashMap<String, List<Context>>  fileBySnippetsHash = new HashMap<String, List<Context>>();
    
    ArrayList<Context> Context = null;
    for ( Context Context1 : snippets) {
      String key = Context1.getDocumentId();
      if ( fileBySnippetsHash.get(key ) == null ) {
       Context = new ArrayList<Context>();
      }
      if ( Context != null )
        Context.add( Context1);
      fileBySnippetsHash.put(key, Context);
      
    } // end loop through the snippets
    
    
    return fileBySnippetsHash;
    
  }  // End Method createFileSnippetHash() ======================
  


  // =======================================================
  /**
   * marryMarkupsWithContexts prunes the false contexts 
   * from the list based on the markups 
   * 
   * @param snippets
   * @param markups
   */
  // =======================================================
  private void marryMarkupsWithContexts(List<Context> snippets, String[] markups) {
   
    // -------------
    // create a hash of the markup offsets
    HashMap<String,String>markupOffsets = createMarkupOffsets( markups);
  
    for ( Context Context: snippets) {
      
      String key = String.valueOf(Context.getSnippetId() ) ;
    
      String relevancy = markupOffsets.get(key);
      Context.setRelevance( relevancy);
    
      
    } // end loop through contexts
    
  }  // End Method marryMarkupsWithContexts() ======================
  


  // =======================================================
  /**
   * createMarkupOffsets creates a hash of the offsets by the markup label
   * 
   * @param markups
   * @return HashMap<String,String>
   */
  // =======================================================
  private HashMap<String, String> createMarkupOffsets(String[] markups) {
   
    HashMap<String,String> aHash = new HashMap<String,String>(markups.length);
    
    for ( String row : markups) {
      String cols[] = U.split(row);
      String snippetId = getSnippetId( cols[4]);
      
      // -------- obsolute code ----
      // int beginOffset = Integer.valueOf(cols[0].trim());
      // int    len = Integer.valueOf( cols[1].trim());
      // int endOffset = beginOffset + len;
      
      String value = cols[2];
      String key = snippetId;
      aHash.put(key, value);
      
      
    } // end loop through the markups
    return aHash;
  } // End Method createMarkupOffsets() ======================
  


  // =================================================
  /**
   * getSnippetId retrieves the snippet id from the markup row
   * 
   * @param pMetaData
   * @return
  */
  // =================================================
 private String getSnippetId(String pMetaData ) {
    
   // pMetaData is a key=value<::>key=value<::> .... 
   // key value pairs are delimited by <::> 
   String[] pairs = U.split2( pMetaData, "<::>");
   String returnVal = null;
   
   if ( pairs != null && pairs.length > 0 )
     for ( String pair: pairs ) {
       if ( pair.startsWith ("snippetId=")) {
         String[] cols = U.split(pair , "=");
         returnVal = cols[1];
         break;
       }
     }
   
    return returnVal;
  } // end Method getSnippetId() -----------------


  // =======================================================
  /**
   * getMarkups 
   * 
   * @param buff
   * @return
   */
  // =======================================================
  private String[] getMarkups(String pBuff) {
  
    int beginMarkupOffset = pBuff.indexOf(VTT_MARKUPS_BEGIN_DELIMITER);
    String markupSection = pBuff.substring(beginMarkupOffset + VTT_MARKUPS_BEGIN_DELIMITER.length() + 1 );
    String markups[] = U.split(markupSection,"\n");
    
    return markups;
    
  }  // End Method getMarkups() ============================
  


  // =======================================================
  /**
   * getSnippets retrieves a list of snippets from the file
   * 
   * @param buff
   * @return List<String>
   * @throws Exception 
   */
  // =======================================================
  private List<Context> getSnippets(String buff) throws Exception {
    
    
    int beginTextPartOfVTT = buff.indexOf(VTT_TEXT_BEGIN_DELIMIER) + VTT_MARKUPS_BEGIN_DELIMITER.length();
    int endTextPartOfVTT   = buff.indexOf(VTT_TEXT_END_DELIMITER);
    String origText = buff.substring( beginTextPartOfVTT, endTextPartOfVTT);
    // ---------------------------
    // parse the annotation into contexts
    List<Context> contexts = Context.fromContexts( origText);
    
    return contexts;
  }  // End Method getSnippets() ======================
  


  private int                        fileCounter = 0;
	private int                      numberOfFiles = 0;
	protected HashMap<String,List<Context>> fileBySnippetHash  = null;
    
 
	

	
} // end Class MultiAnnotationRecordCollectionReader() ----
