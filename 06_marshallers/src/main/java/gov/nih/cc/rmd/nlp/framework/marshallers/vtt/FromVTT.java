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
package gov.nih.cc.rmd.nlp.framework.marshallers.vtt;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import gov.nih.nlm.nls.vtt.api.VttApi;
import gov.nih.nlm.nls.vtt.model.Markup;
import gov.nih.nlm.nls.vtt.model.Markups;
import gov.nih.nlm.nls.vtt.model.VttDocument;
import gov.nih.nlm.nls.vtt.model.VttObj;
import gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class FromVTT extends Reader {

  
//-----------------------------------------
 /** 
  * Constructor 
  * 
  * @param pInputDir
 * @throws ResourceInitializationException 
  * @throws Exception
  * 
  */
 // -----------------------------------------
  public FromVTT(String pInputDir) throws ResourceInitializationException {
    initialize(pInputDir);
  } // end Constructor() --------------------
  
//-----------------------------------------
 /** 
  * Constructor 
  * 
  * @param pInputDir
 * @throws ResourceInitializationException 
  * @throws Exception
  * 
  */
 // -----------------------------------------
  public FromVTT(String pInputDir, String[] pArgs) throws ResourceInitializationException {
    File aDir = new File( pInputDir);
    try {
      initialize(aDir, pArgs);
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with from VTT " + e.getMessage();
      GLog.println(GLog.ERROR_LEVEL,this.getClass(), "Constructor", msg );
      throw  new ResourceInitializationException(e);
    }
  } // end Constructor() --------------------




  // =================================================
/**
 * Constructor
 *
 * @param args
 * @throws ResourceInitializationException 
 * 
**/
// =================================================
public FromVTT(String[] pArgs) throws ResourceInitializationException {
  
  initialize( pArgs);
} // end Constructor() -----------------------------

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
	 
	
	  this.performanceMeter.startCounter();
    JCas jcas = null;  
    try {
      jcas = pCAS.getJCas();
      AnnotationRecord record = this.records.get(this.fileCounter);
      String msg = "Record " + this.fileCounter + " = " + record.tiuDocumentSID ;
      GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "getNext", msg );
      
      String documentId    = record.tiuDocumentSID;
      String documentText  = record.reportText; 
      String documentTitle = record.tiuDocumentDefinitionType;
      String documentType  = record.tiuDocumentDefinitionType;
      String documentName  = documentId; 
      documentText.length();
      String patientID     = record.patientSID;
      String referenceDate = record.referenceDate;
      String metaData      = record.referenceDate;
     
      
      VUIMAUtil.setDocumentHeader( jcas, documentId, documentName, documentType, documentTitle, metaData, patientID, referenceDate, this.fileCounter);

      VUIMAUtil.setDocumentText(jcas, documentName, documentType, referenceDate, documentText, this.setMetaDataHeader);
      
                   
      // --------------------------------------------------
      // Create annotations from the vtt annotations if any
      
      List<SimpleAnnotation> annotations = record.getAnnotations();
      if ( annotations != null && annotations.size() > 0 ) {
        for ( SimpleAnnotation annotation : annotations)
          createAnnotation(jcas, annotation);
      }
      
      
   
    } catch (Exception e ) {
      e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getNext", "Something went wrong with fromDatabase " + e.toString());
    }
     
		 
    // --------------------------
    // increment the fileCounter
    this.fileCounter++;
    this.performanceMeter.stopCounter();
    
	} // end Method getNext() -----------------------



  // ------------------------------------------
  /**
   * createAnnotation  creates the annotation that is the answer
   *
   *
   * @param pJCas
   * @param pAnnotation 
   *
   */
  // ------------------------------------------
  private void createAnnotation(JCas pJCas, SimpleAnnotation pAnnotation) {
   
    pAnnotation.getLabelName();
    
    Class<?> uimaLabelClass = null;
    try {
      uimaLabelClass = UIMAUtil.mapLabelToUIMAClass(pJCas, pAnnotation.getLabelName());
    } catch (ClassNotFoundException e1) {
      e1.printStackTrace();
      String msg = "Issue with trying to map a label " + e1.getMessage(); 
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createAnnotation", msg);
      
    }

    if ( uimaLabelClass != null ) {

    // ----------------------------------------------
    // create a UIMA annotation from this label
    // ----------------------------------------------
    try {

      Constructor<?> c = uimaLabelClass.getConstructor(new Class[] { JCas.class });
      Object uimaAnnotation = c.newInstance(pJCas);
      ((org.apache.uima.jcas.tcas.Annotation)uimaAnnotation).setBegin( pAnnotation.getBeginOffset());
      ((org.apache.uima.jcas.tcas.Annotation)uimaAnnotation).setEnd( pAnnotation.getEndOffset());
      ((org.apache.uima.jcas.tcas.Annotation)uimaAnnotation).addToIndexes(pJCas);
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue creating an annotation from vtt for "  + pAnnotation.toString() + e.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createAnnotation", msg);
      
    }
    }
  } // End Method createAnnotation() ======================
  

  // -----------------------------------------
	/** 
	 * destroy
	 *

	 */
	// -----------------------------------------
	@Override
	public void destroy()  {
		this.performanceMeter.writeProfile( this.getClass().getSimpleName());
		
	} // end Method destroy() --------------------




  // -----------------------------------------
	/** 
	 * close
	 *
	 * @throws IOException
	 */
	// -----------------------------------------
	@Override
	public void close()  {
		
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
    
    initialize( aContext);

	 
	} // End Method initialize() ---------------------------
	    
	
//-----------------------------------------
 /** 
  * initialize 
  * @param pArgs 
  * @throws IOException
  * @throws CollectionException
  */
 // -----------------------------------------
 public void initialize(String[] pArgs ) throws ResourceInitializationException  {


   
   try {

     this.inputDir        = U.getOption(pArgs, "--inputDir=", "/data/snippets"); 
   
     
     // ---------------------------------
     // Loop thru the input dir to parse through the vtt files 
     
     // ---------------------------
     File _inputDir = new File( this.inputDir );
     
     initialize( _inputDir, args);
   
    GLog.println(GLog.STD___LEVEL, this.getClass(), "initialize", "The number of files to process = " + this.numberOfFiles);
   } catch (Exception e ) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize","Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
     throw new ResourceInitializationException();
   }
   
 }
  
//-----------------------------------------
 /** 
  * initialize traverses the directory and sub directories to find .vtt files
 * @throws Exception 
  * 
  * @throws IOException
  * @throws CollectionException
  */
 // -----------------------------------------
	public void initialize(File pInputDir, String[] pArgs) throws Exception {
	  
		 
	  this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
	   
	  if ( pInputDir.isDirectory() && pInputDir.canRead() ) {
	     
      File[] subElements = pInputDir.listFiles();
      
      for ( File subElement : subElements ) {
      
        if ( subElement.isDirectory() ) {
          
          initialize( subElement, pArgs );
        
        } else {
        
          if ( subElement.isFile() && subElement.canRead() && subElement.getName().endsWith(".vtt")) {
            initialize( subElement.getCanonicalPath());
        
          } // end if is a file and .vtt
        } // end if this is not a directory 
      } // end loop thru directory elements
	  } else {
	    
	    String msg = "is not a dir or cannot read the directory " + pInputDir.getAbsolutePath() ;
	    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", msg);
	    throw new Exception(msg);
	  }
      
	  this.setMetaDataHeader = Boolean.parseBoolean(U.getOption(pArgs,  "--setMetaDataHeader=", "false"));
	  this.setCatalog(this.records);
	} // End Method initialize() ======================
  


    // -----------------------------------------
	  /** 
	   * initialize opens the file that has the multi-AnnotationRecord files.
	   * 
	   * 
	   * This method relies on the config variable "inputFile"
	   *
	   * @param pInputFile
	   * @throws IOException
	   * @throws CollectionException
	   */
	  // -----------------------------------------
	  public final void initialize(String pInputFile ) throws ResourceInitializationException  {
	    
	    
	    // -----------------------
	    // Peek in to see if this file is a multi-record file
	    // -----------------------
	    if ( isMultiRecordFile( pInputFile ))
	        
	    // -----------------------
	    // Open the input file, parse it into AnnotationRecords
	    // 
	    // -----------------------
	    try {
	      
	      List<AnnotationRecord>someRecords = parseFile( pInputFile);
	      
	      // ------------------------
	      // add some records to this.records
	      if ( someRecords != null && someRecords.size() > 0) {
	        if ( this.records == null )  this.records = new ArrayList<AnnotationRecord>();
	        this.records.addAll(someRecords);
	      }
	    } catch (Exception e ) {
	      String msg = "Issue parsing the file |" + pInputFile + "|" + e.toString() + "\n" + U.getStackTrace(e);
	      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize",msg);
	      throw new ResourceInitializationException();
	    }
	    
	    else 
   
	    // ---------------------------------
	    // What if this isn't a snippets file?
	    // ----------------------------------
	    try {
	      AnnotationRecord aRecord = parseVTTFile( pInputFile);
	    
	      if ( this.records == null ) {
	        if ( this.records == null )  this.records = new ArrayList<AnnotationRecord>();
	      }
	      this.records.add( aRecord);  
	       
	    } catch (Exception e) {
	      e.printStackTrace();
	      String msg = "Issue parsing vtt file " + pInputFile + " " + e.toString();
	      GLog.println(msg);
	    }
	      
	    
      this.numberOfFiles = this.records.size();
     
      
    
	  	 	   
	} // end Method initialize() --------------
		
		
	// =================================================
    /**
     * isMultiRecordFile looks for multi record delimiters
     * 
     * 
     * @param pInputFile
     * @return boolean
    */
    // =================================================
   private final boolean isMultiRecordFile(String pInputFile) {
      
     
     boolean returnVal = false;
     
     try {
       String buff = U.readFile(pInputFile);
     
       if ( ( buff.contains( RED_patientICN_SLOT ) &&
              buff.contains((RED_snippetNum_SLOT ) )
              ||
              buff.contains(SNIPPET_CONTEXT_ANNOTATION_ID_SLOT) &&
              buff.contains(SNIPPET_CONTEXT_FOCUS_BEGIN_OFFSET_SLOT) )
           )
         
         returnVal = true;
       
     } catch (Exception e) {
       e.printStackTrace();
       GLog.println(GLog.ERROR_LEVEL, "Issue reading from file :" + e.toString());
     }
     
     return returnVal;
    } // end Method isMultiRecordFile() -------------------

  // =======================================================
    /**
     * parseFile opens the file, creates AnnotationRecords
     * 
     * An annotationRecord equates with a snippet from a vtt file.
     * 
     * There are components to the vtt snippets file
     * 
     *   The SnippetColumn  columnName patientId
     *                      columnName Document ID
     *                      columnName Annotation ID
     *                      columnName Focus Begin
     *                      columnName Focus End
     *                      columnName Focus
     *                      columnName Snippet Text *  (take the actual text from the offsets for this
     *            Label TRUE|FALSE|CONCEPT  
     * 
     * @param pInputFile
     * @return AnnotationRecord[]
     * @throws Exception 
     */
    // =======================================================
    private List<AnnotationRecord> parseFile(String pInputFile) throws Exception {
      
       ArrayList<AnnotationRecord> annotationRecordList = new ArrayList<AnnotationRecord>(1000);
    
       GLog.println(GLog.STD___LEVEL, this.getClass(), "parseFile", "Reading in file :" + pInputFile);
     
        VttApi.init( pInputFile, false);
        File inFile = new File( pInputFile);
      
        VttObj vttObj = VttApi.createVttObj(inFile, null, VttApi.X_POS, VttApi.Y_POS, false);
        VttDocument vttDoc = vttObj.getVttDocument();
        
        String buff = vttDoc.getText();
        
        Markups markups = vttDoc.getMarkups();
        
       
        String patientID = null;
        String documentID = null;
        String originalDocument = null;
        String originalAnnotationId = null;
        String snippetNumber = null;
        String focusBeginOffset = null;
        String focusEndOffset = null;
        String focus = null;
        String reportText = null;
        String answerLabelName = null;
        int answerOffset = -1;
        int answerLength = -1;
        String metaData = null;
        int recordOffset = 0;
        for (int i = 0; i < markups.getSize(); i++) {
          
          Markup markup = markups.getMarkup(i);
          
          int        offset = markup.getOffset();
          int        length = markup.getLength();
          String    tagName = markup.getTagName();
    
          String    payload = markup.getAnnotation();
          String markupText = markup.getTaggedText(buff);
         
          
          
        
          if ( tagName.contains("SnippetColumn")) {
            // ---------------------------------
            // parse snippetColumn Annotation 
           payload = payload.replace("<::>", "~");
           String[] cols     = U.split(payload,"~");
           snippetNumber     = U.split(cols[0], "=" )[1];
           String columnName = U.split(cols[2], "=")[1];
           
           if ( columnName.contains("Patient ID") || columnName.contains("PatientICN") ) {
             if ( patientID != null )  {
           
          
               // --------------------------------
               // Write the record out if it exists
               ArrayList<SimpleAnnotation> annotations = new ArrayList<SimpleAnnotation>();
               SimpleAnnotation annotation = new SimpleAnnotation(answerLabelName, answerOffset, answerOffset + answerLength);
               annotations.add( annotation);
               metaData = originalDocument + "|" + originalAnnotationId + "|" + snippetNumber  + "|" + focusBeginOffset + "|" + focusEndOffset + "|" + focus;            
               AnnotationRecord record = new AnnotationRecord(patientID,documentID,reportText,metaData,annotations);
               annotationRecordList.add(record);
               System.err.println("adding record " + record.tiuDocumentSID);
             }
             
             patientID = null;
             documentID = null;
             originalDocument = null;
             originalAnnotationId = null;
             focusBeginOffset = null;
             focusEndOffset = null;
             focus = null;
             reportText = null;
             answerLabelName = null;
             answerOffset = -1;
             answerLength = -1;
             metaData = null;
             patientID = markupText.trim();
           }
           if ( columnName.contains("Document ID"))         originalDocument = markupText.trim();
           if ( columnName.contains("Document")) {            
        	   originalDocument = markupText.trim();           documentID = originalDocument;
           };
           if ( columnName.contains("Annotation ID"))   originalAnnotationId = markupText.trim();
           if ( columnName.contains("Snippet Num"))     originalAnnotationId = markupText.trim();
           
           if ( columnName.contains("Snippet Number"))            documentID = originalDocument + "_" + markupText.trim();
           if ( columnName.contains("FocusBeginOffset"))    focusBeginOffset = markupText.trim();
           if ( columnName.contains("FocusEndOffset"))        focusEndOffset = markupText.trim();
           if ( columnName.contains("Focus"))                          focus = markupText.trim();
           if ( columnName.contains("Snippet Text"))  {     reportText = buff.substring( offset, offset + length );  recordOffset = offset;  }
           
        
          } else {
            answerLabelName = tagName;
            answerOffset = offset - recordOffset;
            answerLength = length;
          
            
          } // end if it's a snippet column or answer label
          
        } // end loop through markups
        
        // --------------------------------
        // Write the record out if it exists
        if (patientID != null  ) {
          ArrayList<SimpleAnnotation> annotations = new ArrayList<SimpleAnnotation>();
          SimpleAnnotation annotation = new SimpleAnnotation(answerLabelName, answerOffset, answerOffset + answerLength);
          annotations.add( annotation);
          metaData = originalDocument + "|" + originalAnnotationId + "|" + snippetNumber  + "|" + focusBeginOffset + "|" + focusEndOffset + "|" + focus;            
          AnnotationRecord record = new AnnotationRecord(patientID,documentID,reportText,metaData,annotations);
          annotationRecordList.add(record);
          System.err.println("adding record " + record.tiuDocumentSID);
        }
        // ----------------------------------------------------------------------
        // I hope there is no need to close a vtt doc. There is no handle for it.
        
       
      return annotationRecordList;
    } // End Method parseFile() ======================
    

 // =======================================================
    /**
     * parseVTTFile opens the file, creates an AnnotationRecord
     * 
     * If there is metadata, great, it will be put into the metadata fields
     * for patientId, doc id, otherFeatures ....
     * 
     * Each annotation will be absorbed into the set of SimpleAnnotations
     * 
     * @param pInputFile
     * @return AnnotationRecord
     * @throws Exception 
     */
    // =======================================================
    private AnnotationRecord parseVTTFile(String pInputFile) throws Exception {
   
      AnnotationRecord record = null;
    
      GLog.println(GLog.STD___LEVEL, this.getClass(), "parseVTTFile","Reading in file :" + pInputFile);
     
       // VttApi.init( pInputFile, false);
        File inFile = new File( pInputFile);
    
        VttObj vttObj = new VttObj(inFile, null, VttApi.X_POS, VttApi.Y_POS, false);  
        VttDocument vttDoc = vttObj.getVttDocument();
        
        vttObj.getVttDocument().readFromFile(null, inFile);
        String buff = vttDoc.getText();
        
        Markups markups = vttDoc.getMarkups();
        
        // --------------------------------
        // right now there is no way to pick this up.
        // TBD - read this from the meta data header from
        // files that pass through framework writers
        // ----------------------------------
        String      patientID =  "unknown";
        String     documentID = pInputFile;
        String reportDateTime = "01/01/1970";
        String reportText = buff;
        String otherMetaData = "";
        String metaData = documentID + "|" + patientID + "|" + reportDateTime  + "|" + otherMetaData;    
      
        
        // Loop thru the markups/annotations making simple annotations
        ArrayList<SimpleAnnotation> annotations = new ArrayList<SimpleAnnotation>();
        for (int i = 0; i < markups.getSize(); i++) {
          
          Markup markup = markups.getMarkup(i);
          int            markupOffset = markup.getOffset();
          int                  length = markup.getLength();
          String              tagName = markup.getTagName();
          String             category = markup.getTagCategory(); // ignore here
          String annotationAttributes = category + "|" + markup.getAnnotation();
         // String           markupText = markup.getTaggedText(buff);
       
           
           SimpleAnnotation annotation = new SimpleAnnotation(tagName, markupOffset, markupOffset + length, annotationAttributes);
           annotations.add( annotation);         
        } // end loop through markups/annotations
         
        record = new AnnotationRecord(patientID,documentID,reportText,metaData,annotations);
    
        // ----------------------------------------------------------------------
        // I hope there is no need to close a vtt doc. There is no handle for it.
      
      return record;
    } // End Method parseVTTFile() ======================
    
    
    
  // ----------------------------------------
	// Class Variables
	// ----------------------------------------
	
	private int                        fileCounter = 0;
	private int                      numberOfFiles = 0;
	private List<AnnotationRecord>         records = null;
	private String                      inputDir = null;
	
	// --------------------------------------------------------
	// These were borrowed from the marshallers.snippet project 
	// If these point to that project, a circluarity happens
	// 
	 private static final String  RED_patientICN_SLOT = "PatientICN";
	 private static final String  RED_snippetNum_SLOT = "Snippet Num";
	 private static final String   SNIPPET_CONTEXT_ANNOTATION_ID_SLOT        = "AnnotationId:";
	 private static final String   SNIPPET_CONTEXT_FOCUS_BEGIN_OFFSET_SLOT   = "FocusBeginOffset:";
     private boolean              setMetaDataHeader = true;
	 
	  

	
	

	
} // end Class MultiAnnotationRecordCollectionReader() ----
