// =================================================
/**
 * CoreCharacteristics writer writes out stats for documents
 * into the tables needed for full text indexing.
 *  
 *  These are going into csv files (one per process)
 *  
 *  documentId|sta3n|enterpriseDocumentType|date|numberOfChars|NumberOfSections|NumberOfSentences|NumberOfWords
 *     
 *        
 * @author  Guy Divita 
 * @created feb 1, 2017
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.ngram;


import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.Section;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.WordToken;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class CoreCharacteristicsWriter extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter  {
 
  public static int CORE_CHARACTERISTICS = 0;


//=======================================================
 /**
  * Constructor IndexingWriter 
  *
  * @param pArgs
  * @throws ResourceInitializationException 
  */
 // =======================================================
 public CoreCharacteristicsWriter() throws ResourceInitializationException {
   super();
 }

  
// =======================================================
  /**
   * Constructor IndexingWriter 
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public CoreCharacteristicsWriter(String[] pArgs) throws ResourceInitializationException {
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

    this.performanceMeter.startCounter();
    
    int numberOfSentences = 0;
    int numberOfSections = 0;
    int numberOfWords = 0;
    int numberOfChars = 0;
    
   
    
    DocumentHeader docHeader = VUIMAUtil.getDocumentHeader(pJCas);
    
    String docId = docHeader.getDocumentId();
    String docType = docHeader.getDocumentType();
    String reportDate = docHeader.getReferenceDate();
    
    String year = getYear( reportDate );
    String sta3n = getSta3n( pJCas);
    
    StringBuffer coreCharacteristicsBuff = new StringBuffer();
    numberOfSentences = docHeader.getNumberOfSentences( );
	numberOfSections = docHeader.getNumberOfSections( );
	numberOfWords = docHeader.getNumberOfWords( );
	numberOfChars = docHeader.getNumberOfChars();
    
    coreCharacteristicsBuff.append(docId); 
    coreCharacteristicsBuff.append(',');
    
    coreCharacteristicsBuff.append(docType); 
    coreCharacteristicsBuff.append(',');
    
    coreCharacteristicsBuff.append(sta3n); 
    coreCharacteristicsBuff.append(',');
    
    coreCharacteristicsBuff.append(year); 
    coreCharacteristicsBuff.append(',');
    
    coreCharacteristicsBuff.append(numberOfSentences); 
    coreCharacteristicsBuff.append(',');
    coreCharacteristicsBuff.append(numberOfSections);
    coreCharacteristicsBuff.append(',');
    coreCharacteristicsBuff.append(numberOfWords);
    coreCharacteristicsBuff.append(',');
    coreCharacteristicsBuff.append(numberOfChars);
		 
    this.out.print( coreCharacteristicsBuff.toString());
    this.out.print("\n");
    
    this.performanceMeter.stopCounter();
   
    
  } // end Method process() ----------------

  
private String getYear(String referenceDate ) {
	
	String year = "1900";
	
	if ( referenceDate != null && !referenceDate.equals("unknown")) {
		// Assuming the date format is yyyy-mm-dd 
		year = referenceDate.substring(0,4);
		
	}
	return year;
	}

private String getSta3n(JCas pJCas) {
	String sta3n        = VUIMAUtil.getSta3n(pJCas);
	if (sta3n != null && sta3n.length() > 3) sta3n = sta3n.substring(0,3);
	
	if ( sta3n == null || sta3n.equals("null")) sta3n = "999";
	
	return sta3n ;
}





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
     
	  try {
	  
		  if ( this.out != null ) 
			  this.out.close();
		 
	  } catch (Exception e) {
		  e.printStackTrace();
		  GLog.println("Issue closing the coreCharacteriscs table " + e.toString());
	  }
	  GLog.println("Closed writer " );
	  
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
    
     
    
    
  
  } // end Method Destroy() -----------------

//-----------------------------------------
 /** 
  * destroy closes the open instance freq table
  *         and writes out a summary table
 * @throws Throwable 
  * 
  *
  */
 // -----------------------------------------
 @Override
 public  void finalize() throws Throwable {
    
	 destroy();
	 super.finalize();
 }

 
//----------------------------------
/**
 * openOutputFile 
 * 
 * @param pOUtputFile
 * @return PrintWriter
 * @throws Exception 
 * 
 * 
 **/
// ----------------------------------
private PrintWriter openOutputFile(String pOutputFile) throws Exception {
	
	try {
	
		String fileName = this.outputDir + "/csv/" + pOutputFile + ".csv";
		this.out = new PrintWriter( fileName);
	} catch (Exception e) {
		e.printStackTrace();
		GLog.println("Issue trying to open the output file " + e.toString());
		throw e;
	}
	return out;
} // end Method openOutputFile

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
    
    initialize(args);
    
   
  } // end Method initialize() -------

//----------------------------------
  /**
   * initialize 
   * 
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
  
    
        this.outputDir = U.getOption(pArgs, "--outputDir=", "./");
    
    
   
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
   
   
  
    try {
    	File aDir = new File( this.outputDir);
    	if ( !aDir.exists() )
    		U.mkDir( this.outputDir);
    	File cDir = new File(this.outputDir + "/csv" );
    	if ( !cDir.exists() )
    		U.mkDir( this.outputDir + "/csv");
    			
    	
    } catch (Exception e1) {
      e1.getStackTrace();
      String msg = "Issue with making the stats dir \n" + e1.getMessage() ;
      System.err.println( msg );
      throw new ResourceInitializationException ();
    }
    
    String fileName = "coreCharacteristics_" + CORE_CHARACTERISTICS++;
   
    try {
    this.out = openOutputFile( fileName );
    this.out.print("DocumentID,DocType,Location,Year,Sentences,Sections,Words,Chars\n");
    
    
  } catch (Exception e1) {
      e1.getStackTrace();
      String msg = "Issue with making the stats dir \n" + e1.getMessage() ;
      System.err.println( msg );
      throw new ResourceInitializationException ();
    }
    
    
    
  }  // End Method initialize() -----------------------
  
  






  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   
   private String outputDir = null;
   private ProfilePerformanceMeter              performanceMeter = null;
   

   private PrintWriter out = null;
	
 
} // end Class MetaMapClient() ---------------
