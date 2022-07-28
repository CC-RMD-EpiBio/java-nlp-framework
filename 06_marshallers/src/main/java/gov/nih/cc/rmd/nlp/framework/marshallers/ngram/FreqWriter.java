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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.WordToken;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class FreqWriter extends  gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter  {
 
  // =======================================================
  /**
   * Constructor IndexingWriter 
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public FreqWriter(String[] pArgs) throws ResourceInitializationException {
    initialize( pArgs);
  }

  // -----------------------------------------
  /**
   * process 
   *   writes out the instances to a file, and accumulates
   *   stats for each file until the proces is done.
   */
  // -----------------------------------------
  public synchronized void process(JCas pJCas) throws AnalysisEngineProcessException {

    this.performanceMeter.startCounter();
    
    HashMap<String, int[]> localDocGrams = new HashMap<String, int[]>(10000);
    
    try {
      List<Annotation> tokens = UIMAUtil.getAnnotations(pJCas, WordToken.typeIndexID);
    	
    	if ( tokens != null && !tokens.isEmpty() ) {
    	  unigrams( localDocGrams, tokens );  
 
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
* unigrams 
*   computes the unigrams for a sequential set of tokens from a document
*   @param localDocGrams
*   @param tokens
*   @param sta3n
*   @param documentType
*   @param year_month
*/
// -----------------------------------------
private void unigrams(HashMap<String, int[]> localDocGrams, List<Annotation>tokens  ) {
	
	
	int tokensSize = tokens.size();
	for ( int i = 0; i < tokensSize ; i++ ) {
		Annotation token1 = tokens.get(i);
	
		String gram1 = token1.getCoveredText().toLowerCase().trim().replace('|', '!');
		if ( gram1 != null ) {
		  gram1 = U.extremeNormalize(gram1);
		
		// ---------------------------------
		// filter out some useless grams  - one char grams - stopWords - just a number 
		if( gram1.trim().length() == 1 )     continue;
		if ( gram1.trim().length() > 30 )    continue;
		if ( onStopWordList( gram1 ))        continue;
		if ( U.isNumber( gram1 ))            continue;
		if ( U.isOnlyPunctuation(gram1))     continue;
		
		
		addGramInstance( localDocGrams, gram1 );
		}
		
	}  // end loop thru tokens;
	
} // end Method unigrams



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
				freqs[0] = 1;
				freqs[1] = 0;	
			}
			freqs[0] += termFreq;        // termFreq 
			freqs[1]+=1;                // doc freq

			System.out.println("->" + gram + "|" + freqs[0] + "|" + freqs[1]);
			this.docGrams.put(gram,  freqs);
				
			
		} // end loop thru grams
	}
	
} // end Method recordDocGrams() ===========

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
	  
		  
		  writeToFile(  this.docGrams);
		 
		
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
private void writeToFile( HashMap<String, long[]> pDocGrams) throws Exception {
	
	PrintWriter out = null;
	StringBuilder buff = new StringBuilder();
	
	
	out = openOutputFile( );
	// Iterate thru the docGram keys
	if (pDocGrams != null && !pDocGrams.isEmpty()) {
		Set<String> grams = pDocGrams.keySet();
		long ctr = 0;
		
		
		out.print("Word, TermFreq, DocFreq\n");
		for ( String gram : grams ) {
	
			try {
					
				buff.setLength(0);
			
				long[]    freq = pDocGrams.get(gram);
				long  termFreq = freq[0];
				long   docFreq = freq[1];
			
				if ( termFreq <= this.cutOffFreq ) continue;
			
				buff.append( gram);     buff.append(",");
				buff.append( termFreq); buff.append(",");
				buff.append( docFreq); 
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
private PrintWriter openOutputFile() throws Exception {
	PrintWriter out = null;
	
	try {
		U.mkDir(this.outputDir + "/csv");
	} catch ( Exception e) {};
	try {
		String fileName = this.outputDir + "/csv/termDocFreq.csv";
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
   String  stopWordFile = U.getOption(pArgs, "--stopWordFile=", "resources/com/ciitizen/framework/nGram/stopWordList.txt");
   String   numbersFile = U.getOption(pArgs, "--numbersFile=", "resources/com/ciitizen/framework/nGram/numbers.txt");
   String      hashSize = U.getOption(pArgs,  "--nGramHashSize=", "1000000000");
   this.cutOffFreq = Integer.parseInt(U.getOption(pArgs,  "--cutOffFreq=",                  "0"));
    
          
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
   initialize( outputDir, stopWordFile, numbersFile, hashSize);
  
    
   
  } // end Method initialize() -------
  
  
  
  // ------------------------------------------
  /**
   * initialize called from another mechanism besides a classic writer, like within the knowtator listener.
   * 
   * @param pOutputDir
   */
  // ------------------------------------------
  public void initialize( String pOutputDir,  String stopWordFile , String numbersFile, String pHashSize)  throws ResourceInitializationException {
 
    this.outputDir = pOutputDir;
    
    try {
      U.mkDir(pOutputDir);
    } catch (Exception e1) {
      e1.getStackTrace();
      String msg = "Issue with making the stats dir \n" + e1.getMessage() ;
      System.err.println( msg );
      throw new ResourceInitializationException ();
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



  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private String gramTableName = null;
   private String outputDir = null;
   private  HashMap<String, long[]> docGrams = null;
   private HashSet<String> stopWordList = null;
   private int cutOffFreq = 0;
   private ProfilePerformanceMeter              performanceMeter = null;
   

 
} // end Class MetaMapClient() ---------------
