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
 * WordFreqByDocTypeWriter computes the table of 
 * 
 *           total   |total    | doc    | doc    | doc    |     |
 *    words |doc freq|term freq| type 1 | type 2 | type 3 | ... | type N
 *    ------+--------+---------+--------+--------+--------+-----+--------
 *    word1 |
 *    word2 |  
 *  
 *  
 *   the resulting vectors are written out to $outputDir/csv/docTypeWordFreqs.csv
 *   
 *   
 *        
 * @author  Guy Divita 
 * @created June 12, 2017
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.ngram;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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

import gov.va.chir.model.Section;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.WordToken;
import gov.va.vinci.model.Concept;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class SemanticTypeByDocTypeWriter extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter  {
 
  private int MaxDocTypes;
// =======================================================
  /**
   * Constructor VectorWriter 
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public SemanticTypeByDocTypeWriter(String[] pArgs) throws ResourceInitializationException {
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

   
    
    String docType = VUIMAUtil.getDocumentTitle(pJCas);
   
    if (docType == null ) return; 
    
   
    
    this.performanceMeter.startCounter();
    HashSet<String> wordSet = new HashSet<String>(5000);
    
     try {
    	 List<Annotation> concepts = UIMAUtil.getAnnotations(pJCas, Concept.typeIndexID);
    	
    	if ( concepts != null && !concepts.isEmpty() ) {
    		System.err.println( docType + "--------------------------------");
    		   
    		int[] docTypeFreqs = this.docTypeNamesHash.get(docType);
    		if ( docTypeFreqs == null ) {
    			docTypeFreqs = new int[1];
    			docTypeFreqs[0] = 0;
    			this.docTypeNamesHash.put( docType, docTypeFreqs);
    		}
    		MaxDocTypes = this.docTypeNamesHash.size();
    		docTypeFreqs[0]++;
    		
    		for ( Annotation aConcept: concepts ) {
    			String semanticTypes = ((Concept)aConcept).getCategories();
    			semanticTypes = normalizeWord( semanticTypes);
    			wordDocTypeSet( semanticTypes, docType, this.wordDocTypeHash, TERM_FREQ_CELL);  // termFreq offset
    	
    			wordSet.add( semanticTypes);
    		} // loop thru words
    	
    
    	
    		// Loop through the word list to count the doc counts
    		for (String semanticTypes : wordSet) {
    			wordDocTypeSet(semanticTypes, docType, this.wordDocTypeHash, DOC_FREQ_CELL );  //docFreq Offset
		
    		} // end loop thru wordSection list
    	} // end if there are words 
    
    	
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue in process x IndexingWriter - some of these can be ignored. Not throwing an exception here " + e.toString());
    }
    this.performanceMeter.stopCounter();
   
    
  } // end Method process() ----------------

  

//-----------------------------------------
/**
* wordDocTypeSet adds an instance count to the docType for this word
*   
*   @param pWord
*   @param pDocType
*   @param pWordVector
*   @param pTermOrDocFreqCell   0 = term freq  1 = doc Freq
*  
*   
*/
// -----------------------------------------
private void wordDocTypeSet( String pWord, String pDocType, HashMap<String, WordDocTypeContainer> pWordDocTypes, int pTermOrDocFreqCell) {
	
	// retrieve all the words in the sections
	
	if ( pWord != null ) {
		 WordDocTypeContainer wdt = pWordDocTypes.get( pWord );
				
		 if ( wdt == null ) {
			 wdt = new WordDocTypeContainer( );
			 pWordDocTypes.put(pWord,  wdt);
		 }
		 int[] docTypeFreqs = wdt.getWordDocTypeFreqs(pDocType);
		 int totalFreqs[] =  wdt.getDocTypesFreqs();
		 totalFreqs[pTermOrDocFreqCell]++;
		 docTypeFreqs[pTermOrDocFreqCell]++;
				 
			
		} // end if there are words
	
	
} // end Method sectionWords() ------------

// -----------------------------------------
/**
 * normalizeWord 
 *
 * @param pWord
 */
// ----------------------------------------
private String normalizeWord(String pWord  ) {
	
		String returnVal = null;
		if ( pWord  == null || pWord.trim().length() == 0 ) return returnVal; ;
		
		returnVal = pWord.trim().toLowerCase();
		returnVal = U.extremeNormalize2(returnVal);
	
	
return returnVal;
} // end Method  normalizeWord() --------------------



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
	  
		  
		  HashMap<String, int[]> docTypeCells = new HashMap<String,int[]>( MaxDocTypes);
		  
		  
		  String[] docTypes = sortByFreqDocTypeNames( this.docTypeNamesHash);
		  
		  // Print the csv header
		  //  word|wordDocFreq|section1|section2| .... |sectionN|
		  printCSVHeader( docTypes);
		  int i = 0;
		  for ( String sectionName : docTypes ) {  // a list of section names ideally sorted by decreasing freq
			  int[] ii = new int[1];
			  ii[0] = i;
			  docTypeCells.put(sectionName, ii );
			  i++;
		  }
		  // Loop thru the wordVectorHash
		  String words[] = sortDecendingWordFreqs( this.wordDocTypeHash);
		 
		  for ( String word: words ) {
			   WordDocTypeContainer wv = this.wordDocTypeHash.get( word );
			   
			   int wordTermFreq = wv.getDocTypesFreqs()[0];
			   int  wordDocFreq = wv.getDocTypesFreqs()[1];
			   
			   int[]  docTypeWordDocFreq = getDocTypeWordDocRow( wv, docTypeCells);
			    
			   // make a row of the section doc freqs for this word -
			   // 
			   // |    |doc  |term|    word freqs
			   // |word|freq |freq| DocType1| DocType2| docType3| docType4 .... |docTypeN |
			   // +----+-----+----+---------+---------+---------+---------      +---------+
			   // |word|     |    |         |         |         |               |         |
			   // +----+-----+----+---------+---------+---------+---------      +---------+
			   this.out.print(word);
			   this.out.print(DELIMITER);
			   this.out.print(wordDocFreq);
			   this.out.print(DELIMITER);
			   this.out.print(wordTermFreq);
			   this.out.print(DELIMITER);
			   for ( int k = 0; k < docTypeWordDocFreq.length; k++ ) {
				   this.out.print(docTypeWordDocFreq[k]);
				   if ( k < docTypeWordDocFreq.length -1) 
					   this.out.print(DELIMITER);
			   } // end loop  through section freqs
			   this.out.print("\n");
		  } // end loop thru words
		  
		  
		  
		 this.out.close();
		
	  } catch (Exception e) {
		  e.printStackTrace();
		  System.err.println("Issue in destroy " + e.toString());
		  GLog.println("Issue closing the gram database " + e.toString());
	  }
   
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
    
     
    
    
  
  } // end Method Destroy() -----------------

  

//-----------------------------------------
 /** 
  * sortByFreqSectionNames creates an array of section names, sorted (descending) by frequency
  * 
  * @param pWordVectorHash
  * @return String[] 
  *
  */
 // -----------------------------------------
  private String[] sortDecendingWordFreqs( HashMap<String, WordDocTypeContainer> pWordVectorHash) {
	 
	  String[] buff = new String[ pWordVectorHash.size()];
	  String[] returnVal = new String[pWordVectorHash.size()];
	  
	  Set<String> keys = pWordVectorHash.keySet();
	  int i = 0;
	  for ( String key: keys ) {
		WordDocTypeContainer wcvx = pWordVectorHash.get(key);
		int[] docTypeFreqs = wcvx.getDocTypesFreqs();
		buff[i] = U.zeroPad( docTypeFreqs[0],10) + "|" + key;
		i++;
 	  }
	  
	  // reverse and strip off the freq
	  // reverse and remove freq
      int j = 0;
	  for ( int k = buff.length -1; k >= 0 ; k-- ) {
		  String[] cols = U.split( buff[k]);
		  returnVal[ j] = cols[1];
		  j++;
	  }
	  
	  
	  
	  return returnVal;
  } // end Method sortDecendingWordFreqs()--

//-----------------------------------------
 /** 
  * sortByFreqDocTypeNames creates an array of doc type names, sorted (descending) by frequency
  * 
  * @param pDocTypes
  * @return String[] 
  *
  */
 // -----------------------------------------
  private String[] sortByFreqDocTypeNames( HashMap<String, int[]> pDocTypes) {
	  String[] buff = new String[ pDocTypes.size()];
	  
	  Set<String> keys = pDocTypes.keySet();
	  int i = 0;
	  for ( String key: keys ) {
		  int[] freqs = pDocTypes.get(key);
		  buff[i] = U.zeroPad(freqs[0], 5) + "|" + key;
		  i++;
	  }
	  Arrays.sort( buff);
	  
	  // reverse and remove freq
	  String[] returnVal = new String[ buff.length ];
	  int j = 0;
	  for ( int k = buff.length -1; k >= 0 ; k-- ) {
		  String[] cols = U.split( buff[k]);
		  returnVal[ j] = cols[1];
		  j++;
	  }
	  	  
	  return returnVal;
  } // end Method sortByFreqSectionNames() -----
  
  // -----------------------------------------
  /** 
   * printCSVHeader   Print the csv header
		               word|wordDocFreq|section1|section2| .... |sectionN|
   * @param pSectionNames
   * 
   */
  // -----------------------------------------
   private void printCSVHeader( String[] pDocTypeNames) {
	   
	   this.out.print("semanticType");
	   this.out.print(DELIMITER);
	   this.out.print("TermFreq");
	   this.out.print(DELIMITER);
	   this.out.print("DocFreq");
	   this.out.print(DELIMITER);
	   
	   for ( int i = 0; i < pDocTypeNames.length; i++ ) {
		   this.out.print(pDocTypeNames[i]);
		   if ( i < pDocTypeNames.length -1) 
				this.out.print(DELIMITER);
	   }
	   this.out.print("\n");
	   
   } // end Method printCSVHeader() ---------
  
// -----------------------------------
   /**
    * getDocTypeWordDocRow
    * @param sectionFreqs
    * @return int[] 
    */
   // -------------------------------
 private int[] getDocTypeWordDocRow( WordDocTypeContainer pWordDocTypeContainer, HashMap<String,int[]>sectionCells) {
	 
	 int[] docTypeWordDocFreq = new int[MaxDocTypes];
	 HashMap<String, int[]> docTypeHash = pWordDocTypeContainer.getDocTypes();
	 
	 
	 Set<String> docTypes = docTypeHash.keySet();
	
	   for ( String docType : docTypes ) {
		   try {
			       
			    int[] docTypeFreqs  = docTypeHash.get(docType);
			   /* if ( docTypeFreqs == null ) {
			    	docTypeFreqs = new int[1];
			    	docTypeFreqs[0] = 0;
			    	docTypeHash.put(sectionName,docTypeFreqs);
			    }
			    */
			  
			   int[] sectionCellz = sectionCells.get( docType );
			   if ( sectionCellz == null ) {
				   throw new Exception("This cannot be for docType |" + docType + "|");
			   }
			   int sectionCell = sectionCellz[0];
			   docTypeWordDocFreq[ sectionCell] = docTypeFreqs[this.termOrDocFreq];
		   } catch (Exception e) {
			   e.printStackTrace();
			   System.err.println("Issue with getting the section position freq table for sectionName " + docType + " " + e.toString());
		   }
		    
	   } // end loop thru docTypes 
	return docTypeWordDocFreq;
} // end Method getSectionWordDocRow() ------------

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
  public synchronized void initialize(String[] pArgs) throws ResourceInitializationException {
  
    try {
    String        outputDir = U.getOption(pArgs, "--outputDir=", "./");
 
             this.DELIMITER = U.getOption(pArgs,  "--delimiter=", "|"); 
    
  
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    
    
    this.docTypeNamesHash = new HashMap<String, int[]>( 5000);
    this.wordDocTypeHash = new HashMap<String, WordDocTypeContainer> (1000000);
    
    this.termOrDocFreq = TERM_FREQ_CELL;
    
   
    U.mkDir(outputDir + "/csv");
    this.out = new PrintWriter( outputDir + "/csv/wordDocTypeFreqs_" + STATIC_COUNTER++ + ".csv");
  
    } catch ( Exception e) {
	   e.printStackTrace();
	   System.err.println("Issue with VectorWriter " + e.toString());
	   throw new ResourceInitializationException();
   }
   
  } // end Method initialize() -------
  
  
  
  
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private PrintWriter out = null;
   private String outputDir = null;
   private HashSet<String> stopWordList = null;
   private HashMap<String, WordDocTypeContainer > wordDocTypeHash = null;
   private HashMap<String, int[]> docTypeNamesHash = null;
   private String DELIMITER = "|";
   private static int STATIC_COUNTER = 0;
   public static final int DOC_FREQ_CELL = 1;
   public static final int TERM_FREQ_CELL = 0;
   private int termOrDocFreq = DOC_FREQ_CELL;
private ProfilePerformanceMeter              performanceMeter = null;
   

 
} // end Class MetaMapClient() ---------------
