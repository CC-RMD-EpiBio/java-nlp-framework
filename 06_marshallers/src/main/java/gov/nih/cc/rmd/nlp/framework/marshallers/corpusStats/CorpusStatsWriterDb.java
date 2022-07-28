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
 * CorpusStatsWriter writes out instances of annotations and some context to 
 * a H2 database file.  A post process program will convert
 * these to database reports.
 * 
 * 
 * 
 *        
 * @author  Guy Divita 
 * @created Nov 1, 2015
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.corpusStats;


import gov.va.chir.model.DocumentHeader;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;


public class CorpusStatsWriterDb extends JCasAnnotator_ImplBase  implements gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer  {
 
  

  // =======================================================
  /**
   * Constructor CorpusStatsWriter 
   *
   * @param pStatsOutputDir
   * @param pOutputTypez  (either | or : delimited )
   * @param pAnnotationAttribute
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public CorpusStatsWriterDb( String pStatsOutputDir, String pOutputTypez, String pAnnotationAttribute) throws ResourceInitializationException {
    
    initialize( pStatsOutputDir, pOutputTypez, pAnnotationAttribute );
    
  } // end Constructor() ---------------------

  

  // =======================================================
  /**
   * Constructor CorpusStatsWriterDb 
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public CorpusStatsWriterDb(String[] pArgs) throws ResourceInitializationException {
    initialize( pArgs);
  }



  // -----------------------------------------
  /**
   * process 
   *   writes out the instances to a file, and accumulates
   *   stats for each file until the proces is done.
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
    	
    	if ( FirstTime ||  (this.numberOfDocuments % MAX_RECORDS_PER_FILE == 0 ) ) {
    		
    	
    		if ( this.connection != null ) {
    			this.connection.close();
    			this.connection = null;
    		}
    		
    		 if ( this.connection == null ) {
    			 DocumentHeader documentHeader = VUIMAUtil.getDocumentHeader(pJCas);
    			 if ( documentHeader != null) {
    				 String metaDataFieldNamez = documentHeader.getOtherMetaDataFieldNames();
    				 String metaDataFieldTypez = documentHeader.getOtherMetaDataFieldTypes();
    				 if ( metaDataFieldNamez != null ) { 
    					 this.metaDataFieldNames = U.split(metaDataFieldNamez);
    					 if ( metaDataFieldTypez != null )
    						 this.metaDataFieldTypes = U.split(metaDataFieldTypez);
    			 }
    			 
    		      initializeDatabase(this.outputStatsDir, metaDataFieldNames, metaDataFieldTypes);
    		    }
    		 }
    		 FirstTime = false;
    	}
      
    this.performanceMeter.startCounter();
    
    List<Annotation> annotations = null;
    List<Annotation> documentHeaders = UIMAUtil.getAnnotations(pJCas,gov.va.chir.model.DocumentHeader.type);
    DocumentHeader documentHeader = null;
    if ( documentHeaders != null && documentHeaders.size() > 0)
      documentHeader = (DocumentHeader) documentHeaders.get(0);
   
    String documentId = "unknown";
    String documentTitle = "unknown";
    String referenceDate = "unknown";
    String patientSID = "unknown";
    String ssn = "unknown";
    String metaDataz = null;
    String[] metaData = {};
   
    
   // String otherMetaDataHeadings = null;   <--- deciding this shouldn't be a column in every row - too inefficient
    if ( documentHeader != null ){
      documentId = ((DocumentHeader) documentHeader).getDocumentId();
      documentTitle = ((DocumentHeader)documentHeader).getDocumentType();
      referenceDate = ((DocumentHeader)documentHeader).getReferenceDate();
      patientSID = ((DocumentHeader)documentHeader).getPatientID();
      ssn        = ((DocumentHeader)documentHeader).getSsn();
      metaDataz = ((DocumentHeader) documentHeader).getOtherMetaData();
      if ( metaDataz != null ) 
  		metaData = U.split(metaDataz);
     
     // otherMetaDataHeadings = ((DocumentHeader)documentHeader).getOtherMetaDataFieldNames();
     
    }
    if ( documentTitle == null || documentTitle.trim().length() == 0 )  documentTitle = "unknown";
    if ( documentId    == null ) {
      
      documentId = VUIMAUtil.getDocumentId(pJCas);
      if ( documentId == null)
        documentId = "unknown_" + U.pad(this.numberOfDocuments);
    }
     
    
     annotations = UIMAUtil.getAnnotations(pJCas);
     List<Annotation> filteredAnnotations = filterAnnotations( annotations);
   
 
    if ( filteredAnnotations != null && filteredAnnotations.size() > 0 ) { 
    	 for ( Annotation annotation: filteredAnnotations) {
     
    		 instance (pJCas, documentId, patientSID, ssn, documentTitle,  referenceDate, metaData, metaDataFieldNames, metaDataFieldTypes, annotation);
       
    	 }  // end loop through annotations 
     } else { // file with no target annotations 
    	 emptyInstance( documentId, patientSID, ssn, documentTitle, referenceDate, metaData, metaDataFieldNames, metaDataFieldTypes );
    	
     }
   
    } catch ( Exception e) {
      e.printStackTrace();
      String msg = "Issue with writing instance out " + e.toString();
      System.err.println(msg);
    }
    this.performanceMeter.stopCounter();
    this.numberOfDocuments++;
  
  } // end Method process() ----------------


// =======================================================
  /**
   * filterAnnotations if all annotations are given, filter to 
   *   only a good set of annotations
   * 
   * @param annotations
   * @return
   */
  // =======================================================
  private List<Annotation> filterAnnotations(List<Annotation> pAnnotations) {
   
    ArrayList<Annotation> filteredAnnotations = new ArrayList<Annotation>();
    String name = null;
    if ( pAnnotations != null ) {
      
      for ( Annotation annotation : pAnnotations ) {
        name = annotation.getClass().getName();
        if ( filterAnnotations( name  ) ) 
        
     
         filteredAnnotations.add( annotation);
           
     }
      
    }
    if ( filteredAnnotations.size() == 0 )
      filteredAnnotations = null;
    
    return filteredAnnotations;
    
  }  // End Method filterAnnotations() ======================
  


  // =======================================================
  /**
   * filterAnnotations filters out labels we don't want to see
   * 
   * @param name
   * @return boolean (True if not filtered out)
   */
  // =======================================================
  private boolean filterAnnotations(String pName) {
   
    boolean returnVal = false;
    if ( pName.contains("model")  &&
        !pName.contains("Token") &&
        !pName.contains("CSI")  &&
        !pName.contains("List")  &&
        !pName.contains("Relationship")  &&
        !pName.contains("ListElement")  &&
        !pName.contains("DocumentClassification") &&
        !pName.contains("DocumentHeader") &&
        !pName.contains("Delimiter") &&
        !pName.contains("VAnnotation"))
        returnVal = true;
    
    return returnVal;
    // End Method filterAnnotations() ======================
  }




//------------------------------------------
/**
 * instance
 *
 *
 * @param pDocumentId
 * @param pDocumentTitle
 * @param pMetaData
 * @param pAnnotation
 * @throws Exception 
 */
// ------------------------------------------
private void instance(JCas pJCas, String pDocumentId, String pPatientSID, String pSSN, String pDocumentTitle, String pReferenceDate, String[] pMetaData, String[] pMetaDataFieldNames, String[] pMetaDataFieldTypes, Annotation pAnnotation) throws Exception {
  

  int              spanBegin = pAnnotation.getBegin();
  int                spanEnd = pAnnotation.getEnd();
  String  longAnnotationName = pAnnotation.getType().getName();
  String shortAnnotationName = longAnnotationName.substring(longAnnotationName.lastIndexOf('.') + 1);
  String             snippet = null;
  try {
    String buff = null;
    snippet = "";
    try {
      buff = pAnnotation.getCoveredText();
      snippet = U.displayForCSV(buff, CSV_DELIMETER, NOT_CSV_DELIMETER );
      if ( snippet != null && snippet.length() > this.snippetSize ) snippet = snippet.substring(0, this.snippetSize);
    } catch (Exception e) {}
    
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println("Issue with getting an annotation's text " + e.getMessage());
    System.err.println("For label " + pAnnotation.getClass().getCanonicalName());
    throw new RuntimeException( e);
  }
  
  String       category = VUIMAUtil.getCategory( pAnnotation);
  if ( category != null && category.length() >= 1000) category = category.substring(0,999);
  String           cuis = VUIMAUtil.getCuis( pAnnotation);
  if ( cuis != null &&  cuis.length() >= 1000) cuis = cuis.substring(0, 999);
  String   conceptNames = VUIMAUtil.getConceptNames( pAnnotation);
  if ( conceptNames != null && conceptNames.length() >= 1000) conceptNames = conceptNames.substring(0,999);
  String    sectionName = VUIMAUtil.getSectionName( pAnnotation);
  String negationStatus = VUIMAUtil.getAssertionStatus(pJCas, pAnnotation);
  String        inProse = String.valueOf(VUIMAUtil.isInProseLazy(pJCas, pAnnotation));
 
 
  if ( sectionName != null && sectionName.length() > 99) sectionName = sectionName.substring(0,99);
  
  
  
  instance( 
         pDocumentId, 
         pPatientSID,
         pSSN,
         pDocumentTitle, 
         pReferenceDate, 
         shortAnnotationName,
         snippet,
         negationStatus,
         category,
         cuis,
         conceptNames,
         sectionName,
         inProse,
         spanBegin,
         spanEnd,
         pMetaData,
         pMetaDataFieldNames,
         pMetaDataFieldTypes);
    
} // end Method instance() -------------------


  //------------------------------------------
  /**
   * instance  is a STATIC SYNCHRONIZED method that does the inserts into one table
   * across multiple threads.  There is a 
 * @throws Exception 
   *
   *
   
   */
  // ------------------------------------------
  private  synchronized void instance(
                        String pDocumentId, 
                        String pPatientSID, 
                        String pSSN,
                        String pDocumentTitle, 
                        String pReferenceDate, 
                        String pShortAnnotationName,
                        String pSnippet,
                        String pNegationStatus,
                        String pCategory,
                        String pCuis,
                        String pConceptNames,
                        String pSectionName,
                        String pInProse,
                        int    pSpanBegin,
                        int    pSpanEnd,
                        String[] pMetaData,
                        String[] pMetaDataFieldNames,
                        String[] pMetaDataFieldTypes
      
      ) throws Exception {
    
   // -----------------------------
   // populate a prepared statement
   
   // ------------------------------
   // Execute the insert statement
    if ( pReferenceDate != null && pReferenceDate.toLowerCase().contains("unknown"))  pReferenceDate = "1975-01-01";
   
   try {
     insertInstanceTablePreparedStatement.setLong  (1, corpuseInstanceRowID++             );
     insertInstanceTablePreparedStatement.setString(2, pDocumentId         );
     insertInstanceTablePreparedStatement.setString(3, pPatientSID         );
     insertInstanceTablePreparedStatement.setString(4, pSSN                );
     insertInstanceTablePreparedStatement.setString(5, pDocumentTitle       );
     insertInstanceTablePreparedStatement.setString(6, pReferenceDate      );
     insertInstanceTablePreparedStatement.setString(7, pShortAnnotationName );
     insertInstanceTablePreparedStatement.setString(8, pSnippet             );
     insertInstanceTablePreparedStatement.setString(9, pNegationStatus      );
     insertInstanceTablePreparedStatement.setString(10, pCategory            );
     insertInstanceTablePreparedStatement.setString(11, pCuis                );
     insertInstanceTablePreparedStatement.setString(12,pConceptNames        );
     insertInstanceTablePreparedStatement.setString(13,pSectionName         );
     insertInstanceTablePreparedStatement.setString(14,pInProse             );
     insertInstanceTablePreparedStatement.setInt   (15, pSpanBegin          );
     insertInstanceTablePreparedStatement.setInt   (16, pSpanEnd            );
   } catch (Exception e) {
	   e.printStackTrace();
	   GLog.println("Issue with insert statement " + e.toString());
	   throw e;
   }
     if ( pMetaData != null ) {
     int minusI = 0;	 
     for ( int i = 0; i < pMetaData.length; i++ ) {
    
    	 
    	 String value = pMetaData[i];
    	 if ( value == null )  
    		 value = "-1";
    	 if ( value.toLowerCase().contains("nul") || value.toLowerCase().contains("unknown")) {
    		 if ((pMetaDataFieldTypes != null && i < pMetaDataFieldTypes.length   && !pMetaDataFieldTypes[i].isEmpty() ) &&   
    		     ( pMetaDataFieldTypes[i].equals("TIMESTAMP")|| pMetaDataFieldTypes[i].equals("DATE")) )
    			 value = "1975-01-01";
    	 }
    	 
    	 if ( pMetaDataFieldNames != null && 
    	      pMetaDataFieldNames.length > 0 &&
    	      i < pMetaDataFieldNames.length &&
    	      pMetaDataFieldNames[i] != null &&
    	     !pMetaDataFieldNames[i].isEmpty() &&
    	   
    	     !pMetaDataFieldNames[i].toLowerCase().contains("reportText") &&
    	     ! pMetaDataFieldNames[i].toLowerCase().contains("rownum") && 
    	     !pMetaDataFieldNames[i].toLowerCase().contains("tiustandardtitle")
    	     
    			 ) {
    		 try {
    			 insertInstanceTablePreparedStatement.setObject(i + 17 - minusI, value         );
    		 } catch (Exception e) {
    			 e.printStackTrace();
    			 GLog.println("Something wrong with setting " + i + " " + pMetaDataFieldNames[i] + ": " + value );
    		 }
       } else {
    	   minusI++;
       }
     }
     }
     
     try {
     insertInstanceTablePreparedStatement.execute();
   
   } catch (Exception e) {
     e.printStackTrace();
     GLog.println("Issue with the prepared statement insertion "    + e.toString());
   }
  
} // End Method instance() -----------------------


//------------------------------------------
/**
 * emptyIninstance
 *
 *
 * @param pDocumentId
 * @param pDocumentTitle
 * @param pMetaData
 * @throws Exception 
 * 
 */
// ------------------------------------------
private void emptyInstance( String pDocumentId, String pPatientSID, String pSSN, String pDocumentTitle, String pReferenceDate, String[] pMetaData, String[] pMetaDataFieldNames, String[] pMetaDataFieldTypes) throws Exception {
  

  int              spanBegin = 0;
  int                spanEnd = 0;
  
  String shortAnnotationName = "No Annotations";
  String             snippet = "";
  
  
  
  String     category = "";
  String         cuis = "";
  String conceptNames = "";
  String  sectionName = "";
  String negationStatus = "";
  String       inProse = "";
  

  instance( 
      pDocumentId, 
      pPatientSID, 
      pSSN,
      pDocumentTitle, 
      pReferenceDate, 
      shortAnnotationName,
      snippet,
      negationStatus,
      category,
      cuis,
      conceptNames,
      sectionName,
      inProse,
      spanBegin,
      spanEnd,
      pMetaData, 
      pMetaDataFieldNames,
      pMetaDataFieldTypes);
  
   
  
  
} // End Method instance() -----------------------


  // -----------------------------------------
  /** 
   * destroy closes the open instance freq table
   *         and writes out a summary table
   * 
   *
   */
  // -----------------------------------------
  @Override
  public void destroy() {
  
    close();
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
    super.destroy();
  } // end Method Destroy() -----------------

 


  //----------------------------------
  /**
   * initialize 
   * 
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
  
    
	  
	    this.snippetSize = Integer.parseInt(U.getOption(pArgs,  "--snippetSize=", "20" ));
	    String      outputDir = U.getOption(pArgs, "--outputDir=", "./");
	    String  labelsToIndex = U.getOption(pArgs, "--outputTypes=", "Concept");
	    String   otherAttribute = U.getOption(pArgs,  "--otherAttribute=", "");
	    
	    MAX_RECORDS_PER_FILE = Integer.parseInt(U.getOption(pArgs,  "--maxRecordsPerFile=", "150000"));
 
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
  
    initialize(  outputDir, labelsToIndex, otherAttribute   );
   
  } // end Method initialize() -------
  
  
  
  //----------------------------------
  /**
   * initialize 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
  
    String[]    args = (String[])aContext.getConfigParameterValue("args");
    
    initialize( args);
   
  } // end Method initialize() -------
  
  
  // ------------------------------------------
  /**
   * initialize called from another mechanism besides a classic writer, like within the knowtator listener.
   * 
   * @param pOutputDir
   * @param pOutputTypez
   * @param pAnnotationAttribute 
   */
  // ------------------------------------------
  public void initialize( String pOutputDir, String pOutputTypez, String pAnnotationAttribute )  throws ResourceInitializationException {
   
    
    this.outputStatsDir       = pOutputDir + "/stats";
    System.err.println(" OutputStats Dir = " + this.outputStatsDir);
   
    try {
      File dir = new File(this.outputStatsDir);
      if ( !dir.exists() )
         U.mkDir(this.outputStatsDir);
    } catch (Exception e1) {
      e1.getStackTrace();
      String msg = "Issue with making the stats dir \n" + e1.getMessage() ;
      System.err.println( msg );
      throw new ResourceInitializationException ();
    }
   
   
  
    this.numberOfDocuments = 0;
    if      ( pOutputTypez != null && pOutputTypez.contains(":"))         this.labelsToIndex = U.split(pOutputTypez, ":");
    else if ( pOutputTypez != null && pOutputTypez.contains("|"))         this.labelsToIndex = U.split(pOutputTypez, "|");
    else if ( pOutputTypez != null && pOutputTypez.trim().length() > 0 )  this.labelsToIndex = U.split(pOutputTypez);
    else                                                                  this.labelsToIndex = null;
    
    if ( this.labelsToIndex != null )
      if ( this.labelsToIndex.length == 0)
        this.labelsToIndex = null;
    
    this.annotationAttribute = pAnnotationAttribute;
    
  }  // End Method initialize() -----------------------
  
  


// =======================================================
  /**
   * initializeDatabase 
 * @param pJCas 
   * 
   * 
   */
  // =======================================================
  private void initializeDatabase(String pOutStatsDir, String[] metaDataFieldNames, String[] metaDataFieldTypes ) {
    try {

      String    databaseName = null;
      synchronized(this) {
        databaseName = pOutStatsDir + "/corpusInstances_" + DATABASE_FILECOUNTER;
        DATABASE_FILECOUNTER++;
      }
      
      this.connection = connect2H2_Database(databaseName );
    
      createInstanceTable( this.connection, metaDataFieldNames, metaDataFieldTypes );
     
        
      insertInstanceTablePreparedStatement = createInsertInstancePreparedStatement(this.connection, metaDataFieldNames );
    
    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue with the database stuff " + e.toString());
      }
  
    
  } // End Method initializeDatabase() ======================
  

//=======================================================
 /**
  * close closes the H2 database
  * 
  */
 // =======================================================
 public void close( ) {
   try {

   if ( this.connection != null && this.connection.isClosed() ) 
     this.connection.close();
   
    this.connection = null;
   
   } catch (Exception e) {
       e.printStackTrace();
       System.err.println("Issue with closing the database " + e.toString());
     }
 
   
 } // End Method close() ================================


//=======================================================
/**
 * finalize
 * 
 */
// =======================================================
 @Override
public void finalize( ) {
  close();

  
} // End Method finalize() ==============================
 
//=======================================================
 /**
  * createH2_DatabaseTable 
  * 
  * @param pOutputDatabaseName
  * @param pMetaDataFieldNames,
  * @param pMetaDataFieldTypes
  * @return Connection
  * 
  */
 // =======================================================
 private void createInstanceTable( Connection pConnection, String[] pMetaDataFieldNames, String[] pMetaDataFieldTypes ) {
 
   Statement stat = null;
   try {
  
    stat = pConnection.createStatement();
    StringBuffer buff = new StringBuffer();
		   
		  buff.append( "create table CORPUS_INSTANCES  (\n" );
		  buff.append("corpuseInstanceRowID  BIGINT,      \n");
		  buff.append("documentSID           varchar(40),\n");
		  buff.append("patientSID            varchar(20)   NULL,\n"); 
		  buff.append("ScrSSN                varchar(9)    NULL, \n");
		  buff.append("TIUStandardTitle      varchar(100)  NULL, \n");
		  buff.append("ReferenceDateTime     DATE          NULL,\n"); 
		  buff.append("label                 varchar(50)   null,\n"); 
		  buff.append("snippet               varchar(" + this.snippetSize + ")   null,\n"); 
		  buff.append("assertionStatus       varchar(20)   null,\n"); 
		  buff.append("category              varchar(1000) null,\n"); 
		  buff.append("cuis                  varchar(1000) null, \n"); 	 	 
		  buff.append("conceptNames          varchar(1000) null, \n"); 
		  buff.append("section               varchar(100)  null, \n");
		  buff.append("inProse               varchar(10)   null, \n");
		  buff.append("beginOffset           int, \n");
	      buff.append("endOffset             int");
    
	      if ( pMetaDataFieldNames != null && pMetaDataFieldNames.length > 0 ) {
	        buff.append(",\n");
	        for ( int i = 0; i < pMetaDataFieldNames.length; i++ ) {
	          if (! pMetaDataFieldNames[i].toLowerCase().contains("reportText") && 
	              ! pMetaDataFieldNames[i].toLowerCase().contains("rownum") && 
	              ! pMetaDataFieldNames[i].toLowerCase().contains("tiustandardtitle") 
	        	  
	              ) {
	            buff.append( pMetaDataFieldNames[i] + "        " + pMetaDataFieldTypes[i]  );
	            if ( i < pMetaDataFieldNames.length -1 ) {
	              buff.append(",\n");
	            }
	           }
	        }
	      }
	      if ( buff.toString().endsWith(",\n")) { 
	      	StringBuffer newBuff = new StringBuffer(); 
	      	newBuff.append( buff.substring( 0, buff.length() -2));
	      	buff = newBuff;
	      }
	   
	      
	      buff.append(")\n");
    
    try {
    	stat.execute( buff.toString());
    } catch (Exception e) {
    	e.printStackTrace();
    	System.err.println("Couldn't make the table : " + e.toString());
    	throw e;
    }
    try {
    stat.execute("create INDEX CORPUS_INSTANCES_RowId ON CORPUS_INSTANCES (corpuseInstanceRowID )"); // <---- do this either manually, or on 
    } catch (Exception e) {
    	e.printStackTrace();
    	System.err.println("Couldn't make the indexes " + e.toString());
    	throw e;
    }
   stat.close();
       
   
   } catch (Exception e) {
     e.printStackTrace();
     System.err.println( "Issue creating the h2 datatabase " + e.toString());
     if ( stat != null ) try {
        stat.close();
     } catch (Exception e2) {};
     
   }
  
 }  // End Method createH2_DatabaseTable() ======================
 




 // =======================================================
 /**
  * createH2InsertPreparedStatement
 * @param metaDataFieldTypes 
 * @param metaDataFieldNames 
  * 
  * @param h2Connection
  * @param pTableName
  * @return PreparedStatement
  * @throws SQLException 
  */
 // =======================================================
 private PreparedStatement createInsertInstancePreparedStatement(Connection pConnection, String[] pMetaDataFieldNames  ) throws SQLException {
   
   String insertQuery = "INSERT INTO CORPUS_INSTANCES ("
       + "corpuseInstanceRowID,\n"     // 1
       + "documentSID, \n"             // 2
       + "patientSID, \n"              // 3
       + "ScrSSN,\n"                   // 4
       + "TIUStandardTitle, \n"        // 5
       + "ReferenceDateTime, \n"       // 6
       + "label, \n"                   // 7
       + "snippet, \n"                 // 8
       + "assertionStatus, \n"         // 9
       + "category, \n"                // 10
       + "cuis, \n"                    // 11
       + "conceptNames, \n"            // 12
       + "section, \n"                 // 13
       + "inProse, \n"                 // 14             
       + "beginOffset, \n"             // 15
       + "endOffset \n" ;             // 16
   
   	StringBuffer buff = new StringBuffer();
   	buff.append(insertQuery);
   
    int noQuestions = 16;
   	if ( pMetaDataFieldNames != null && pMetaDataFieldNames.length > 0 ) { 
   		buff.append(",\n" );
   		for ( int i = 0; i < pMetaDataFieldNames.length; i++ ) { 
   		
   		
   			
   		 if (! pMetaDataFieldNames[i].toLowerCase().contains("reportText") &&
   		     ! pMetaDataFieldNames[i].toLowerCase().contains("rownum") && 
   		     ! pMetaDataFieldNames[i].toLowerCase().contains("tiustandardtitle" ) &&
            pMetaDataFieldNames[i].trim().length() > 0   ) {
   		         noQuestions++;
   		         buff.append( pMetaDataFieldNames[i]   );	
   		         if ( i < pMetaDataFieldNames.length -1) 
   		           buff.append(",\n");
   		       }
   		}
   	}
    if ( buff.toString().endsWith(",\n")) { 
    	StringBuffer newBuff = new StringBuffer(); 
    	newBuff.append( buff.substring(0, buff.length() -2));
    	buff = newBuff;
    }
 
    
   	buff.append(")\n values" + "( " );
   	for ( int i = 0; i < noQuestions; i++ ) { 
   		buff.append("?");
   		if ( i < noQuestions -1 ) 
   			buff.append(",");
   	}
   	buff.append(" )\n");
   
   	GLog.println( buff.toString());
   
   PreparedStatement insertPreparedStatement = pConnection.prepareStatement(buff.toString());
   
   return insertPreparedStatement;
 
   // End Method createH2InsertPreparedStatement() ======================
 }

  // =======================================================
  /**
   * createH2_DatabaseTable 
   * 
   * @param pOutputDatabaseName
   * @param pTableName
   * @param maxRows 
   * @return Connection
   * 
   */
  // =======================================================
  public Connection connect2H2_Database(String pH2DatabaseName) {
  
    Connection conn = null;
    try {
      @SuppressWarnings("unused")
      org.h2.Driver t = null;  // <----- here to make sure this is a complile error if not found.
    Class.forName("org.h2.Driver");
    conn = DriverManager.getConnection("jdbc:h2:" + pH2DatabaseName );
   
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println( "Issue creating the h2 datatabase");
    }
    
    return conn;
  }  // End Method createH2_DatabaseTable() ======================
  


  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  public static final char CSV_CHAR_DELIMITER = ',';
  public static final String CSV_DELIMETER = String.valueOf(CSV_CHAR_DELIMITER);  // could be a pipe
  private static final String NOT_CSV_DELIMETER = "<COMMA>";   
  
  private int numberOfDocuments = 0;  // <--- needed to assign document ids if they dont exist
  private String outputStatsDir = null;
  private String[] labelsToIndex = null;
  private String annotationAttribute = null;
  

  
   
   private Connection connection = null;
   private PreparedStatement insertInstanceTablePreparedStatement = null;
   private static int DATABASE_FILECOUNTER = 0;
   private ProfilePerformanceMeter  performanceMeter = null;
   private static long corpuseInstanceRowID = 0;
   private static boolean FirstTime = true;
   private String[] metaDataFieldNames = {};
   private String[] metaDataFieldTypes = {};
   private int snippetSize = 20;
   private static int MAX_RECORDS_PER_FILE = 10000000;
  
 
} // end Class MetaMapClient() ---------------
