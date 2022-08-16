// =================================================
/**
 * IndexingWriter writes out instances of key annotations
 * into the tables needed for full text indexing.
 *  
 *  These are going into an h2 database called fullTextIndexes
 *  
 *  The tables include 
 *     keyDocId = key|docId
 *     KEYTERMFREQs = key|termFrequency|numberOfDocs
 *     
 *        
 * @author  Guy Divita 
 * @created Doc 1, 2015
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.ngram;


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.Section;
import gov.va.chir.model.Token;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class NGramWriter extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter  {
 
  // =======================================================
  /**
   * Constructor IndexingWriter 
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public NGramWriter(String[] pArgs) throws ResourceInitializationException {
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
    
    HashMap<String, int[]> localDocGrams = new HashMap<String, int[]>(10000);
    
    try {

    	String documentType = VUIMAUtil.getDocumentType(pJCas);
    	if ( documentType != null && documentType.length() > 200 )  documentType = documentType.substring(0,200);
    	String sta3n        = VUIMAUtil.getSta3n(pJCas);
    	if (sta3n != null && sta3n.length() > 3) sta3n = sta3n.substring(0,3);
    	
    	if ( sta3n == null || sta3n.equals("null")) sta3n = "999";
    	String referenceDate = VUIMAUtil.getReferenceDate(pJCas);
    	String year = "1900";
    	String month = "1";
    	String month_s = "01";
    	String year_month = year + "|" + month_s;
    	if ( referenceDate != null && !referenceDate.equals("unknown")) {
    		// Assuming the date format is yyyy-mm-dd 
    		year = referenceDate.substring(0,4);
    		month = referenceDate.substring(5,7);
    		year_month = year + "|" + month;
    	}
    	
    	// ------------
    	// this is a 2 gram set of keys of keys
    	// this is |1
    	// is a    |1
    	// a 2     |1
    	// 2 gram  |1
    	// gram set|1
    	// set of  |1
    	// of keys |2
    	// keys of |1
    	// keys _  |1  <  drop trailing incomplete grams?
    	
    	List<Annotation> tokens = UIMAUtil.getAnnotations(pJCas, Token.typeIndexID);
    	
    	if ( tokens != null && !tokens.isEmpty() ) {
    	
    		
    		tokens = removeSpaceTokens( tokens);
    		
    		
    		switch ( this.gramType ) {
    		case 1: unigrams( localDocGrams, tokens, sta3n, documentType, year_month);  break;
    		case 2: biGrams ( localDocGrams, tokens, sta3n, documentType, year_month);  break;
    		}
  		
    	}// end if there are tokens
    	    	
    	recordDocGrams( localDocGrams);
    	
    } catch (Exception e) {
 //     e.printStackTrace();
 //     System.err.println("Issue in process x IndexingWriter - some of these can be ignored. Not throwing an exception here " + e.toString());
    }
    this.performanceMeter.stopCounter();
   
    
  } // end Method process() ----------------

  
//-----------------------------------------
 /**
  * bigrams 
  *   computes the bigrams for a sequential set of tokens from a document
  *   @param localDocGrams
  *   @param tokens
  *   @param sta3n
  *   @param documentType
  *   @pram year_month
  */
 // -----------------------------------------
private void biGrams(HashMap<String, int[]> localDocGrams, List<Annotation>tokens, String sta3n, String documentType, String year_month ) {
	
	
	int tokensSize = tokens.size();
	for ( int i = 0; i < tokensSize -1; i++ ) {
		Annotation token1 = tokens.get(i);
		Annotation token2 = tokens.get(i+1);
		String section =  " ";// getSection( pJCas, tokens.get(i));  if (section != null && section.length() > 200) section = section.substring(0,200);
		String gram1 = token1.getCoveredText().toLowerCase().trim().replace('|', '!');
		String gram2 = token2.getCoveredText().toLowerCase().trim().replace('|', '!');
		StringBuffer gram = new StringBuffer();
		gram.append(gram1);gram.append(" "); gram.append(gram2);
		if ( gram.length() > 80) break; // <---- on the grounds this is likely to be junk
		gram.append("|");
		gram.append(sta3n);
		gram.append("|");
		gram.append(documentType);
		gram.append("|");
		gram.append(year_month);
		gram.append("|");
		gram.append(section);
		
		addGramInstance( localDocGrams, gram.toString());
		
	}  // end loop thru tokens;
	
	
} // End Method biGrams


//-----------------------------------------
/**
* unigrams 
*   computes the unigrams for a sequential set of tokens from a document
*   @param localDocGrams
*   @param tokens
*   @param sta3n
*   @param documentType
*   @param year_month
*/
// -----------------------------------------
private void unigrams(HashMap<String, int[]> localDocGrams, List<Annotation>tokens, String sta3n, String documentType, String year_month ) {
	
	
	int tokensSize = tokens.size();
	for ( int i = 0; i < tokensSize ; i++ ) {
		Annotation token1 = tokens.get(i);
	
		String section =  " ";// getSection( pJCas, tokens.get(i));  if (section != null && section.length() > 200) section = section.substring(0,200);
		String gram1 = token1.getCoveredText().toLowerCase().trim().replace('|', '!');
		
		// ---------------------------------
		// filter out some useless grams  - one char grams - stopWords - just a number 
		if( gram1.trim().length() == 1 )     continue;
	  if( gram1.trim().length() > 30 )     continue;
		if ( onStopWordList( gram1 ))        continue;
		if ( U.isNumber( gram1 ))            continue;
		if ( U.isOnlyPunctuation(gram1))     continue;
		
		StringBuffer gram = new StringBuffer();
		gram.append(gram1);
	
		gram.append("|");
		gram.append(sta3n);
		gram.append("|");
		gram.append(documentType);
		gram.append("|");
		gram.append(year_month);
		gram.append("|");
		gram.append(section);
		
		addGramInstance( localDocGrams, gram.toString());
		
	}  // end loop thru tokens;
	
} // end Method unigrams



//==========================================
 /**
  * getSection gets section the token is in
  *
  * @param pJCas
  * @param pToken
  * @return String
  * 
  */
 // ==========================================
private String getSection(JCas pJCas, Annotation pToken) {
	String sectionName = "unknown";
	
	List<Annotation> sections = UIMAUtil.getEnclosingAnnotation(pJCas, pToken, Section.typeIndexID );
	
	
	if (sections != null && !sections.isEmpty() ) {
		Section section = (Section) sections.get(0);
		sectionName = section.getSectionName();
	}
	
	return sectionName;
} // end Method getSection() ===============

//==========================================
 /**
  * removeSpaceTokens keeps word and punctuation tokens
  *
  * @param pTokens
  * @return List<Annotation>
  * 
  */
 // ==========================================
private List<Annotation> removeSpaceTokens(List<Annotation> pTokens) {
	
	ArrayList<Annotation> filteredTokens = new ArrayList<Annotation>(5000);
	for ( Annotation token : pTokens ) {
	
		if ( token.getCoveredText().trim().length() > 0 )
			filteredTokens.add( token);
	}
	
	return filteredTokens;
	
}

//==========================================
 /**
  * addGramInstancee adds this to the 
  * hash keeping count
  *
  * @param pKey   gram|sta3n|documentType
  * 
  */
 // ==========================================
  private synchronized void addGramInstance(HashMap<String, int[]> pDocGrams, String pKey ) {
	
	  
	  if ( !onStopWordList(pKey) ) {
		  int freqs[] = pDocGrams.get(pKey )	;    
		  if ( freqs == null ) {
			  freqs = new int[1];
			  freqs[0] = 1;
		  } else {
			freqs[0]++;  
			  
		  }
		  pDocGrams.put(pKey, freqs);
	  }
	 
	  
  } // end Method addGramInstance()



//-----------------------------------------
 /** 
  * onStopWordList
  * 
  *
  */
 // -----------------------------------------
  private boolean onStopWordList(String pKey) {
	
	 return this.stopWordList.contains(pKey) ;
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
  public synchronized void destroy() {
     
	  try {
	  
		  
		  writeToFile( this.gramTableName , this.docGrams);
		 
		  /* 
		  writeToH2File( this.static_connection, this.docGrams);
		  
		  if ( this.static_connection != null && !this.static_connection.isClosed() ) {
			  this.static_connection.close();
			  this.static_connection = null;
		  }
		  
		  */
	  } catch (Exception e) {
		  e.printStackTrace();
		  GLog.println("Issue closing the gram database " + e.toString());
	  }
   
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
    
     
    
    
  
  } // end Method Destroy() -----------------

 

//-----------------------------------------
 /** 
  * destroy closes the open instance freq table
  *         and writes out a summary table
 * @throws Exception 
  * 
  *
  */
 // -----------------------------------------
 private void writeToH2File(Connection static_connection2, HashMap<String, long[]> pDocGrams) throws Exception {
	
	// Iterate thru the docGram keys
		if (pDocGrams != null && !pDocGrams.isEmpty()) {
			Set<String> grams = pDocGrams.keySet();
			long ctr = 0;
			for ( String gramAnd : grams ) {
				try {
		    	String cols[] = U.split(gramAnd);
				String    gram = cols[0];
			
				String   sta3n = cols[1];
			
				String docType = cols[2];
				String    year = cols[3];
				String   month = cols[4];
			//	String section = cols[5];
				long[]    freq = pDocGrams.get(gramAnd);
				long  termFreq = freq[0];
				long   docFreq = freq[1];
			
				try {
					this.insertGramTableTermFreqPreparedStatement.setString(1, gram);
					this.insertGramTableTermFreqPreparedStatement.setLong(2, termFreq);
					this.insertGramTableTermFreqPreparedStatement.setLong(3, docFreq);
					this.insertGramTableTermFreqPreparedStatement.setString(4, sta3n);
					this.insertGramTableTermFreqPreparedStatement.setString(5, docType );
					this.insertGramTableTermFreqPreparedStatement.setString(6, year );
					this.insertGramTableTermFreqPreparedStatement.setString(7, month );
				//	this.insertGramTableTermFreqPreparedStatement.setString(8, section);
					
					this.insertGramTableTermFreqPreparedStatement.addBatch();
				} catch (Exception e) {
					e.printStackTrace();
					GLog.println("Issue add to batch execute command " + e.toString());
					throw e;
				}
				} catch (Exception e) {
					e.printStackTrace();
					GLog.println("Issue with pushing a gram to the db " + e.toString());
				}
				
				if ( ctr % 10000 == 0 ) {
					try { 
						this.insertGramTableTermFreqPreparedStatement.executeBatch();
					} catch (Exception e) {
						e.printStackTrace();
						GLog.println("Issue executing large batch " + e.toString());
						// throw e;
					}
				}
				ctr++;
			}
			try {
				this.insertGramTableTermFreqPreparedStatement.executeBatch();
			} catch (Exception e) {
				e.printStackTrace();
				GLog.println("Issue with last execute batch " + e.toString());
			}
			}
	 
} // end writeToH2File

//-----------------------------------------
/** 
 * destroy closes the open instance freq table
 *         and writes out a summary table
* @throws Exception 
 * 
 *
 */
// -----------------------------------------
private void writeToFile(String pOutputFile, HashMap<String, long[]> pDocGrams) throws Exception {
	
	PrintWriter out = null;
	StringBuilder buff = new StringBuilder();
	
	
	out = openOutputFile( pOutputFile);
	// Iterate thru the docGram keys
	if (pDocGrams != null && !pDocGrams.isEmpty()) {
		Set<String> grams = pDocGrams.keySet();
		long ctr = 0;
		
		for ( String gramAnd : grams ) {
	
			try {
					
				buff.setLength(0);
				
		    	String cols[] = U.split(gramAnd);
				String    gram = cols[0];
			
				String   sta3n = cols[1];
			
				String docType = cols[2];
				String    year = cols[3];
				String   month = cols[4];
			//	String section = cols[5];
				long[]    freq = pDocGrams.get(gramAnd);
				long  termFreq = freq[0];
				long   docFreq = freq[1];
			
			
				buff.append( gram);     buff.append("|");
				buff.append( termFreq); buff.append("|");
				buff.append( docFreq);  buff.append("|");
				buff.append( sta3n);    buff.append("|");
				buff.append( docType ); buff.append("|");
				buff.append( year );  //  buff.append("|");
				// buff.append( month );   buff.append("|");
				//	buff.append( section);  buff.append("|");
				buff.append("\n");
					
				out.print(buff.toString());
				
				
			
			} catch (Exception e) {
				e.printStackTrace();
				GLog.println("Issue with last execute batch " + e.toString());
			}
			
			if ( ctr % 10000 == 0 ) {
				GLog.println("nGram : wrote out " + ctr + " grams");
			}
			ctr++;
			
		} // end loop thru grams
		if ( out != null )
			out.close();
		
	} // end if there are grams
	
				
	 
} // end writeToH2File

 
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
	PrintWriter out = null;
	
	try {
		U.mkDir(this.outputDir + "/csv");
		String fileName = this.outputDir + "/csv/" + pOutputFile + ".csv";
		out = new PrintWriter( fileName);
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
  
    
    String    outputDir = U.getOption(pArgs, "--outputDir=", "./");
    String databaseName = U.getOption(pArgs, "--outputDatabasename=", "sophiaBiGrams");
    
    
      this.gramTableName =  U.getOption(pArgs,  "--gramTableName=", "bigrams");
    String  stopWordFile = U.getOption(pArgs, "--stopWordFile=", "resources/com/ciitizen/framework/nGram/stopWordList.txt");
    String   numbersFile = U.getOption(pArgs, "--numbersFile=", "resources/com/ciitizen/framework/nGram/numbers.txt");
    String      hashSize = U.getOption(pArgs,  "--nGramHashSize=", "1000000000");
           this.gramType = Integer.parseInt(U.getOption(pArgs,  "--gramType=", "1" )); 
    
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
   initialize( outputDir, databaseName, gramTableName, stopWordFile, numbersFile, hashSize);
  
    
   
  } // end Method initialize() -------
  
  
  
  // ------------------------------------------
  /**
   * initialize called from another mechanism besides a classic writer, like within the knowtator listener.
   * 
   * @param pOutputDir
   */
  // ------------------------------------------
  public void initialize( String pOutputDir, String pDatabaseName, String pGramTableName, String stopWordFile , String numbersFile, String pHashSize)  throws ResourceInitializationException {
 
    this.outputDir = pOutputDir;
    
    try {
      U.mkDir(pOutputDir);
    } catch (Exception e1) {
      e1.getStackTrace();
      String msg = "Issue with making the stats dir \n" + e1.getMessage() ;
      System.err.println( msg );
      throw new ResourceInitializationException ();
    }
   
    if ( static_connection == null ) {
     // static_connection = initializeDatabase(this.outputDir, pDatabaseName, pGramTableName);
    }
    int hashSize = Integer.parseInt(pHashSize);
    this.docGrams = new HashMap<String, long[]>( hashSize);
 
    try {
    	initializeStopWordList( stopWordFile, numbersFile );
    } catch (Exception e) {
    	e.printStackTrace();
    	GLog.println("Issue reading the stop word file " + e.toString() );
    	 throw new ResourceInitializationException ();
    }
    	
    
    
    
    
  }  // End Method initialize() -----------------------
  
  

//=======================================================
 /**
  * initializeStopWordsList 
  * 
  * @param stopWordsFile
  * @param numbersFile
 * @throws Exception 
  * 
  * 
  */
 // =======================================================
private void initializeStopWordList(String stopWordFile, String numbersFile) throws Exception {
	
	this.stopWordList = new HashSet<String>(10000);
	String rows[] = U.readClassPathResourceIntoStringArray(stopWordFile);
	for (String row: rows ) 
		if (!row.startsWith("#"))
			this.stopWordList.add(row);
	
	
	rows = U.readClassPathResourceIntoStringArray(numbersFile);
	for (String row: rows ) 
		if (!row.startsWith("#"))
			this.stopWordList.add(row);
}

// =======================================================
  /**
   * initializeDatabase 
   * 
   * @param pOutputDir
   * @param pDatabaseName
   * @param pGramTableName
   * 
   * @return
   */
  // =======================================================
  private Connection initializeDatabase(String pOutputDir, String pDatabaseName, String pGramTableName ) throws ResourceInitializationException{
    try {

      String    databaseName = pOutputDir + "/" + pDatabaseName;
     
      
      
      static_connection = connect2H2_Database(databaseName );

      createGramTable(static_connection, pGramTableName);
      
      
     // updateGramTableTermFreqPreparedStatement = createUpdateGramTableTermFreqPreparedStatement( static_connection, pGramTableName );
      updateGramTableTermFreqPreparedStatement = createUpdateGramTableDocFreqPreparedStatement(  static_connection, pGramTableName );
      insertGramTableTermFreqPreparedStatement = createInsertGramTablePreparedStatement( static_connection, pGramTableName );
        
      
      
    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue with the database stuff " + e.toString());
      }
  
    return static_connection;
  } // End Method initializeDatabase() ======================
  

//==========================================
 /**
  * recordDocGrams pushes these doc gram freqs
  * to an h2 table of grams
  *
  * @param pDocGrams
 * @throws Exception 
  * 
  */
 // ==========================================
private synchronized void recordDocGrams(HashMap<String, int[]> pDocGrams) throws Exception {
	
	
	// Iterate thru the docGram keys
	if (pDocGrams != null && !pDocGrams.isEmpty()) {
		Set<String> grams = pDocGrams.keySet();
		for ( String gram : grams ) {
			int[] freq = pDocGrams.get(gram);
			int termFreq = freq[0];
			
			long freqs[] = this.docGrams.get(gram);
			
			if ( freqs == null ) {
				freqs = new long[2];
				freqs[0] = 0;
				freqs[1] = 0;	
			}
			freqs[0] = termFreq;        // termFreq 
			freqs[1]+=1;                // doc freq

			
			this.docGrams.put(gram,  freqs);
				
			
		} // end loop thru grams
	}
	
} // end Method recordDocGrams() ===========


//=======================================================
 /**
  * close closes the H2 database
  * 
  */
 // =======================================================
 public  void close( ) {
   try {

   if ( static_connection != null && static_connection.isClosed() ) 
     static_connection.close();
   
    static_connection = null;
   
   } catch (Exception e) {
       e.printStackTrace();
       System.err.println("Issue with closing the database " + e.toString());
     }
 
 } // End Method close() ================================

//=======================================================
/**
 * createGramTable
 * 
 * @param pConnection
 * @param pGramName
 * 
 */
// =======================================================
private void createGramTable( Connection pConnection, String pGramName) throws ResourceInitializationException{

  try {
 
  Statement stat = pConnection.createStatement();
  
  stat.execute("create table " + pGramName + " (" +             
      "gram                        varchar(80)," + 
      "termFreq                    long,  " +
      "docFreq                     long,  " +
      "sta3n                       varchar(3), " +
      "tiuDocumentStandardTitle    varchar(200), " +
      "year                        varchar(4), " +
      "month                       varchar(2), " +
      "section                     varchar(200) " +
      ") ");  
 
  

 // stat.execute("create INDEX " + pGramName + "_key ON " + pGramName + " (gram)");
    stat.execute("create INDEX " + pGramName + "_All ON " + pGramName + "(gram,sta3n,tiuDocumentStandardTitle,year,month,section)");
    stat.close();
         
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println( "Issue creating the h2 datatabase table " + pGramName +  " " + e.toString());
    throw new ResourceInitializationException();
  }
 
}  // End Method createKEYTERMFREQsTable() ======================



 // =======================================================
 /**
  * createUpdateGramTableTermFreqPreparedStatement
  * 
  * @param  pConnection
  * @param  pGramTable
  * @return PreparedStatement
  * @throws SQLException 
  */
 // =======================================================
 private  PreparedStatement createUpdateGramTableDocFreqPreparedStatement(Connection pConnection, String pGramTable ) throws SQLException {
   
	 
	 String updateTableSQL = "UPDATE " + pGramTable + " SET TermFreq = TermFreq + ?, docFreq = docFreq + 1 WHERE gram = ?";
	 // SET DocFreq = docFreq + 1
   
    PreparedStatement updatePreparedStatement = pConnection.prepareStatement(updateTableSQL);
   
   return updatePreparedStatement;
 
 } // End Method createUpdateGramTableTermFreqPreparedStatement() ======================
 
 

 

 // =======================================================
 /**
  * createInsertGramTableTermFreqPreparedStatement
  * 
  * @param  pConnection
  * @param  pGramTable
  * @return PreparedStatement
  * @throws SQLException 
  */
 // =======================================================
 private  PreparedStatement createInsertGramTablePreparedStatement(Connection pConnection, String pGramTable ) throws SQLException {
   
	 
	 String insertTableSQL = "INSERT INTO " + pGramTable + " ( GRAM, TermFreq, DocFreq, sta3n, tiuDocumentStandardTitle, year, month, section) values (?, ?, ?, ?, ?, ?, ?, ?)";
  
   
    PreparedStatement updatePreparedStatement = pConnection.prepareStatement(insertTableSQL);
   
   return updatePreparedStatement;
 
 } // End Method createInsertGramTableTermFreqPreparedStatement() ======================
 
 


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
  private static Connection connect2H2_Database(String pH2DatabaseName) {
  
    Connection conn = null;
    try {
      @SuppressWarnings("unused")
      org.h2.Driver t = null;  // <----- here to make sure this is a complile error if not found.
    Class.forName("org.h2.Driver");
    conn = DriverManager.getConnection("jdbc:h2:" + pH2DatabaseName   );
    Logger tmp_logger = java.util.logging.Logger.getLogger("h2database");
    tmp_logger.setLevel(Level.OFF);
    
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println( "Issue creating the h2 datatabase");
    }
    
    return conn;
  }  // End Method createH2_DatabaseTable() ======================
  


  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private String gramTableName = null;
   private String outputDir = null;
   private  Connection static_connection = null;
   private  HashMap<String, long[]> docGrams = null;
   private int gramType = 1;
   
   private  PreparedStatement updateGramTableTermFreqPreparedStatement;
   private  PreparedStatement insertGramTableTermFreqPreparedStatement;
  
   private HashSet<String> stopWordList = null;
    
   private ProfilePerformanceMeter              performanceMeter = null;
   

 
} // end Class MetaMapClient() ---------------
