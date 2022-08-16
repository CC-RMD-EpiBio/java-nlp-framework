// =================================================
/**
 * FromRedSnippets takes vtt files that are snippets of annotations in the Red format 
 * and creates v3nlpframework snippet annotations from them.
 * 
 * 
 * @author  Guy Divita 
 * @created Oct 5, 2017
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.snippet;


import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.TrueNegative;
import gov.va.chir.model.TruePositive;
import gov.va.vinci.model.Concept;
import gov.va.vinci.model.False;
import gov.va.vinci.model.Snippet;
import gov.va.vinci.model.True;
import gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;


public class FromRedSnippets extends Reader {

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
  private List<File> mFiles = null;
   private int mCurrentIndex = 0;
  
  
  
  // =======================================================
   /**
    * Constructor
    * 
    * @param pArgs
   * @throws ResourceInitializationException 
    */
   // ======================================================
  public FromRedSnippets(String[] pArgs) throws ResourceInitializationException {
    
    this.initialize( pArgs);
    
  }  // End Constructor FromRedSnippets =============
    
  

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
     
      // -------------------------------------------------
      // Break the document into the constituent vtt parts
      
      // --------------------------
      // read into the vtt sections
      VTTContainer vttContainer = VTTContainer.parseVTTFile( buff);
      
      
      String documentId    = currentFile.getName();
      String documentURI   = documentId;
      String documentText = vttContainer.getText();
      String documentTitle = "";
      String documentType  = "concordance";
      String documentName  = documentId; 
      int documentSpan     = documentText.length();
      String patientID     = "";
      String referenceDate = "";
      String metaData      = vttContainer.getMetaData();
      jcas.setDocumentText(documentText);
     
      // ----------------------------------
      // Create the documement Header
      createDocumentHeaderAnnotation( jcas, documentId, documentTitle, documentType, documentName,
          documentSpan, patientID, referenceDate, metaData );
   
  
      // ---------------------------------
      // Create the snippets from the text part
      List<Annotation> snippets = createSnippets( jcas, documentText);
      
      
      // ---------------------------------
      // Add the markups and markup decisions to the snippets
      createMarkupsAsTP_TN_Annotatations(jcas, snippets, vttContainer.getMarkups());
   
    } catch (Exception e ) {
      e.printStackTrace();
     System.err.println("Something went wrong with FromRedSnippets " + e.toString());
    }
     
		 
    // --------------------------
    // increment the fileCounter
    this.fileCounter++;
    
	} // end Method getNext() -----------------------

// =======================================================
   /**
    * createMarkupsAsTP_TN_Annotatations  For each markup, it finds the snippet annotation, 
    * and fills in the details - the patient id, the snippet number, and most importantly,
    * the snippet decision.  Additionally, it creates a true positive/true negative
    * annotation from each decision. 
    * 
    * @param pSnippets
    * @param pMarkups 
    *
    */
   // ======================================================	
  private void createMarkupsAsTP_TN_Annotatations(JCas pJCas, List<Annotation> pSnippets, String pMarkups) {
   
    
     try {
    
       List<RedSnippetMarkups> redMarkups = createMarkups( pMarkups );
       
       // --------------------------------------
       // Match up the markups with the snippets
       
       if ( redMarkups != null && !redMarkups.isEmpty()) {
         
         for ( RedSnippetMarkups markup : redMarkups ) {
           
           int beginSnippetOffset = markup.getPatientICNMarkup().getBeginOffset() - "PatientICN:".length() -1 ;
           int   endSnippetOffset = markup.getSnippetTextMarkup().getBeginOffset() + markup.getSnippetTextMarkup().getLength();
           
           List<Annotation> snippets = UIMAUtil.getContained(pSnippets, beginSnippetOffset, endSnippetOffset);
           
           if ( snippets != null && snippets.size() == 1 ) {
             
             // ----------------------------
             // add the markup decision to the snippet
             // create a true positive or a true negative annotation for this markup decision 
             // ----------------------------
              Snippet aSnippet =  ( Snippet)snippets.get(0);
              if ( markup.getFocusMarkup() != null )
                aSnippet.setFocusCategory( markup.getFocusMarkup().getCategory());
              else 
                aSnippet.setFocusCategory("Snippet");
              
              aSnippet.setPatientId( markup.getPatientICNMarkup().getColumnValue());
              aSnippet.setDocumentId( markup.getDocumentMarkup().getColumnValue());
              aSnippet.setSnippetId(  Integer.parseInt(markup.getSnippetNumMarkup().getColumnValue()));
              createEfficacyAnnotation( pJCas, markup.getFocusMarkup() );
             
           }
           
           
         } // end loop thru redMarkups 
         
       } // end if there any redMarkups
       
       
       
     } catch ( Exception e ) {
       e.printStackTrace();
       System.err.println("Issue within method createMarkupsAsTP_TN_Annotatations " + e.getMessage() );
       throw e;
     }
    System.err.println(" got to this point ");
    } // End Method createMarkupsAsTP_TN_Annotatations ============
    
 

  // =======================================================
   /**
    * createEfficacyAnnotation creates either a truePositive or
    * trueNegative annotation for this focusMarkup
    * 
    * @param pJCas
    * @param pFocusMarkup 
    *
    */
   // ======================================================	
  private void createEfficacyAnnotation(JCas pJCas, RedSnippetMarkup pFocusMarkup) {
    
    Annotation statement = null;
    if ( pFocusMarkup != null ) {
      if ( pFocusMarkup.getCategory().equals("Yes") )
        statement = new True( pJCas);
      else 
        statement = new False( pJCas);
    
      statement.setBegin( pFocusMarkup.getBeginOffset());
      statement.setEnd(   pFocusMarkup.getBeginOffset() + pFocusMarkup.getLength());
      statement.addToIndexes();
    }
    } // End Method createEfficacyAnnotation ===============
    
 

  // =======================================================
   /**
    * createMarkups
    * 
    * @param pMarkups
    * @return List<RedSnippetMarkups>
    *
    */
   // ======================================================	
  private List<RedSnippetMarkups> createMarkups(String pMarkups) {
   
    List<RedSnippetMarkups> redMarkups = null;
     try {
       if ( pMarkups != null && !pMarkups.isEmpty() ) {
         String [] rows = U.split(pMarkups, "\n");
         String markup[] = null;
         redMarkups = new ArrayList<RedSnippetMarkups>();
         
         
         
         for ( int i = 0; i < rows.length; i++ ) {
           
           if      (rows[i].contains("<::>columnName=\"PatientICN\"|" ) ) { 
             if ( markup != null ) 
               redMarkups.add( new RedSnippetMarkups( markup ));
             markup = new String[5];
             markup[0] =rows[i] ;
           }
           else if ( rows[i].contains("<::>columnName=\"Document\"|")    ) {             markup[1] = rows[i]; } 
           else if ( rows[i].contains("<::>columnName=\"Snippet Num\"|") ) {             markup[2] = rows[i]; }
           else if ( rows[i].contains("<::>columnName=\"Snippet Text\"|")) {             markup[3] = rows[i]; }
           else if (!rows[i].contains("|SnippetColumn") && rows[i].contains("||") && (
               rows[i].toLowerCase().contains("|yes") || 
               rows[i].toLowerCase().contains("|no")) || 
               rows[i].toLowerCase().contains("maybe")||
               rows[i].toLowerCase().contains("uncertain")                                         ) { 
        	   markup[4] = rows[i];
           }
           
         } // end loop thru markups
     
         if ( markup != null )
           redMarkups.add(new RedSnippetMarkups( markup ));
         
       } // end if there are any markups 
 
     } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method createMarkupsAsTP_TN_Annotatations " + e.getMessage() );
        throw e;
      }
     
       return redMarkups; 
       
    } // End Method createMarkups()  ======================
    
  

  //=======================================================
  /**
   * createSnippets
   * 
   * @param pJCas
   * @param pDocumentText
   * throws Exception
   *
   */
  // ======================================================  
private List<Annotation> createSnippets(JCas pJCas, String pDocumentText) throws Exception {

   ArrayList<Annotation> returnVal = null;
   try {
    
     if (pDocumentText != null && !pDocumentText.isEmpty()) {
     
       StringBuffer snippetBuff = null;
       int currentOffset = 0;
       
       String[] rows = U.split(pDocumentText, "\n");
       returnVal = new ArrayList<Annotation>();  
       
       for ( int i = 0; i < rows.length; i++ ) {
         
         String row = rows[i];
         
         if ( row.startsWith( "PatientICN:" )) {
           
           if ( snippetBuff != null && snippetBuff.toString().trim().length() > 0 ) {
             try {
             Annotation aSnippet = createSnippet( pJCas, pDocumentText, snippetBuff.toString(), currentOffset );
             returnVal.add( aSnippet);
             currentOffset = aSnippet.getEnd() ;
             } catch (Exception e) {
               e.printStackTrace();
               System.err.println( "Issue here " + e.toString());
             }
           }
           snippetBuff = new StringBuffer();
         }
         
         if ( snippetBuff != null ) {  
           snippetBuff.append(row);
           snippetBuff.append("\n");
         }
         
       } // end loop thru the rows
      
      if ( snippetBuff != null && snippetBuff.toString().trim().length() > 0 ) {
        Annotation aSnippet = createSnippet( pJCas, pDocumentText, snippetBuff.toString(), currentOffset );
        returnVal.add( aSnippet);
        currentOffset = aSnippet.getEnd() + 1;
      }
       
    
     } // end if there is any text
   
   } catch (Exception e) {
     e.printStackTrace();
     String msg = "Issue with finding patterns from regular expressions : " + e.toString() ;
     System.err.println(msg);
   }
   
   return returnVal;
 } // End Method createSnippet() ==========================

 // =======================================================
  /**
   * createSnippet
   * 
   * @param pJCas
   * @param pDocText
   * @param pSnippet 
   * @param pCurrentOffset
   * @return Annotation
  * @throws Exception 
   *
   */
  // ======================================================  
 private Annotation createSnippet(JCas pJCas, String pDocText, String pSnippet, int pCurrentOffset) throws Exception {
  
  // figure out the offsets to the snippet within the docText
   int currentOffset = pCurrentOffset ;
   Annotation snippet = null;
   
   try {
   if ( pCurrentOffset < pDocText.length() ) {
     int beginOffset = pDocText.indexOf( pSnippet.trim(), currentOffset);
     if ( beginOffset != -1) {
     
     
       // ---------------------------------------------------
       // guard against going beyond the end of the document
       int endOffset = beginOffset + pSnippet.length();
       if ( endOffset > pDocText.length() -1)  
         endOffset = pDocText.length() -1; 
       
       String snippetText = getSnippetTextFromSnippet( pSnippet );
       
       snippet = createSnippet( pJCas, snippetText, beginOffset, endOffset);
       currentOffset = snippet.getEnd() + 1;
       
     }
   }
   }
    catch ( Exception e) {
      e.printStackTrace();
      System.err.println("issue here " + e.toString());
      throw e;
    }
    return snippet;
 } // end Method createSnippet() =============================
 
 

 // =======================================================
  /**
   * getSnippetTextFromSnippet returns just the text section
   * of the snippet
   * 
   * @param pDocText
   * @return String
   *
   */
  // ======================================================  
   private String getSnippetTextFromSnippet(String pDocText) {
   
     StringBuffer returnVal = new StringBuffer();
     
     String rows[] = U.split( pDocText, "\n");
     
     for (int i = 0; i < rows.length; i++ ) {
       if      ( rows[i].startsWith("PatientICN:"))   continue;
       else if ( rows[i].startsWith("Document:"))     continue;
       else if ( rows[i].startsWith("Snippet Num:"))  continue;
       else if ( rows[i].startsWith("Snippet Text:")) continue;
       else  returnVal.append( rows[i] + "\n");
     }
     
    return returnVal.toString();
   } // End Method getSnippetTextFromSnippet ============
   
 

 // =======================================================
  /**
   * createSnippet
   * 
   * @param pJCas
   * @param pSnippet 
   * @param pCurrentOffset
  
  * @throws Exception 
   *
   */
  // ======================================================  
 private Annotation createSnippet(JCas pJCas, String pSnippetString, int pBeginOffset, int pEndOffset) throws Exception {
  
 
   Snippet statement = new Snippet(pJCas);
   statement.setAnnotationId("BreakIntoSnippets_" + snippetCounter++);
   statement.setOtherFeatures(pSnippetString );
   statement.setBegin(pBeginOffset);
   statement.setEnd( pEndOffset);
   statement.addToIndexes();
   return statement;
   
 } // end Method createSnippet() =============================
       
      
  
  
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
	 * close
	 *
	 * @throws IOException
	 */
	// -----------------------------------------
	@Override
	public void close() throws IOException {
		
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
	    mFiles = new ArrayList<File>();
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
	      if ( files[i].getName().endsWith(".vtt") ) {
	        System.err.println("Adding file " + files[i].getAbsolutePath());
	        mFiles.add(  files[i]);
	      }
	    }
	    
	    // ------------------------------------
	    // Each vtt file contains contexts from a bunch of text files
	    // Create a list of text files from reading thru the vtt contexts
	    // There is a gentlemans agreement that the vtt file won't have contexts
	    // from a partial text file.  
	    
	    
	    this.numberOfFiles = mFiles.size();
	    this.setCatalog( this.numberOfFiles);
	 

	  } catch (Exception e) {
	    e.printStackTrace();
      String msg = "CollectionReader: inputFile was not found ." + e.toString() + U.getStackTrace(e);
      System.err.println(msg);
      throw new ResourceInitializationException();
	  }
	 
	  } // End Method initialize() ---------------------------
		
  // =======================================================
   /**
    * initialize @see gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader#initialize(java.lang.String)
    * 
    * @param pInputFile  (multi file file - doen't make sense ehere
    * @throws ResourceInitializationException
    *
    */
   // ======================================================
  @Override
  public void initialize(String pInputFile) throws ResourceInitializationException {
    
        System.err.println("This initialize method passes in the name of a file with multple files in it - it is not used for snippet files " );
        throw new ResourceInitializationException();
      
    } // End Method initialize ============================

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
  
  // ----------------------------------------
	// Class Variables
	// ----------------------------------------
	

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
    
    ArrayList<Context> context = null;
    for ( Context Context1 : snippets) {
      String key = Context1.getDocumentId();
      if ( fileBySnippetsHash.get(key ) == null ) {
       context = new ArrayList<Context>();
      }
      if ( context != null )
        context.add( Context1);
      fileBySnippetsHash.put(key, context);
      
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
      int beginOffset = Context.getFocusBeginOffset();
      int endOffset   = Context.getFocusEndOffset();
      
      String key = U.pad(beginOffset ) + "|" + U.pad(endOffset);
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
      if ( cols[0] == null || cols[0].isEmpty())
        System.err.println("Issue with this row \n" + row );
      int beginOffset = Integer.valueOf(cols[0].trim());
      int    len = Integer.valueOf( cols[1].trim());
      int endOffset = beginOffset + len;
      String value = cols[3];
      String key = U.pad(beginOffset) + "|" + U.pad(  endOffset);
      aHash.put(key, value);
      
      
    } // end loop through the markups
    return aHash;
  } // End Method createMarkupOffsets() ======================
  


  // =======================================================
  /**
   * getMarkups [Summary here]
   * 
   * @param buff
   * @return
   */
  // =======================================================
  private String[] getMarkups(String pBuff) {
  
    String[] returnVal = null;
    int beginMarkupOffset = pBuff.indexOf(VTT_MARKUPS_BEGIN_DELIMITER);
    String markupSection = pBuff.substring(beginMarkupOffset);
    String markups[] = U.split(markupSection,"\n");
   
    List<String> markupsArray = new ArrayList<String>( markups.length -3);
    for ( int i = 4; i < markups.length; i++ ){
      if ( markups[i] != null && !markups[i].isEmpty() && !markups[i].startsWith("#"))
      markupsArray.add(markups[i]);
    }
    returnVal = markupsArray.toArray(new String[markupsArray.size()] );
    
    return returnVal;
    
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
    List<Context> context = Context.fromContexts( origText);
    
    return context;
  }  // End Method getSnippets() ======================
  


  private int                        fileCounter = 0;
  private static int                snippetCounter = 0;
	private int                      numberOfFiles = 0;
	private HashMap<String,List<Context>> fileBySnippetHash  = null;
    
 
	

	
} // end Class MultiAnnotationRecordCollectionReader() ----
