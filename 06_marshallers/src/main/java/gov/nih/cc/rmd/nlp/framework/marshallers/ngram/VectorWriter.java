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
 * VectorWriter reads in the definition of a word vector (column cells associated with a word) 
 *  then writes out each document/section with a word vector row with each cell filled with
 *  the word freq of that word appearing in that section. 
 *  
 *   --wordFreqTable=/some/path/to/wordFreq.csv   
 *   --wordFreqThreshold=2 
 *      or 
 *   --numberOfCells=1000  computes the words to include in the vector based on freq and alphabetical order.  
 *   
 *   A cell would be defined in the order of the words found in the wordFreq table that
 *   meet the wordFreqThreshold criteria.  
 *   
 *   the resulting vectors are written out to $outputDir/vectors/vector.csv
 *   The resulting vector definition is written out to $outputDir/vectors/wordVectorDef.txt
 *  
 *   
 *        
 * @author  Guy Divita 
 * @created June 1, 2017
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
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class VectorWriter extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter  {
 
  private int MaxSectionTypes;

// =======================================================
  /**
   * Constructor VectorWriter 
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public VectorWriter(String[] pArgs) throws ResourceInitializationException {
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
    
    HashSet<String> wordDocSet = new HashSet<String>(100);  //  word|SectionName list
    try {
      List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, Section.typeIndexID);
    	
    	if ( sections != null && !sections.isEmpty() ) 
    	  for ( Annotation section: sections ) {
    		   sectionNames(  section );
    		   sectionWords( pJCas, section, this.wordVectorHash, wordDocSet);
    		   sectionAverages( pJCas,  this.sectionAvgHash, section );
    	
    	  } // loop thru sections
    	
    	// Loop through the word|section list to count the doc counts
    	
    	for (String wordSection : wordDocSet) {
    		String cols[] = U.split(wordSection);
    		String word = cols[0];
    		String sectionName = cols[1];
    		
    		WordMetaDataContainer wmdc = this.wordVectorHash.get( word );
		
			wmdc.setDocFreq( wmdc.getDocFreq() +1);
			HashMap<String, SectionMetaDataContainer> sectionFreqHash = wmdc.getSectionFreqs();
			SectionMetaDataContainer sectionFreqsContainer = sectionFreqHash.get(sectionName);
			if ( sectionFreqsContainer != null )
				sectionFreqsContainer.setDocFreq( sectionFreqsContainer.getDocFreq() + 1);
		
    	} // end loop thru wordSection list
    	
    	if ( this.sectionNamesHash.size() > MaxSectionTypes )
    		MaxSectionTypes = this.sectionNamesHash.size();
    
    	
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue in process x IndexingWriter - some of these can be ignored. Not throwing an exception here " + e.toString());
    }
    this.performanceMeter.stopCounter();
   
    
  } // end Method process() ----------------

  

//-----------------------------------------
/**
* sectionWords creates a vector of (relevant) words seen in this section
*   
*   @param pJCas
*   @param pSection
*   @param pWordVector
*   @param pSectionAvgHash
*   
*/
// -----------------------------------------
private void sectionWords( JCas pJCas, 
		Annotation pSection ,HashMap<String,WordMetaDataContainer> pWordVector, HashSet<String> pDocWordsSections) {
	
	// retrieve all the words in the sections
	
	if ( pSection != null ) {
	
		String sectionName = ((Section)pSection).getSectionName();
		
	
		if ( sectionName == null || sectionName.trim().length() == 0 )  return;
		sectionName = normalizeSectionName( sectionName);
		
		List<Annotation> words = UIMAUtil.getAnnotationsBySpan( pJCas, WordToken.typeIndexID, pSection.getBegin(), pSection.getEnd() );
		
		
		if ( words != null && !words.isEmpty() ) {
			for ( Annotation wordAnnotation:  words ) {
				String word = U.extremeNormalize2( wordAnnotation.getCoveredText() );
			
				pDocWordsSections.add( word + "|" + sectionName );
				
				WordMetaDataContainer wmdc = pWordVector.get( word );
				
				if ( wmdc == null ) {
					wmdc = new WordMetaDataContainer( word);
				}
				wmdc.setTermFreq( wmdc.getTermFreq() +1);
				HashMap<String, SectionMetaDataContainer> sectionFreqHash = wmdc.getSectionFreqs();
				SectionMetaDataContainer sectionFreqsContainer = sectionFreqHash.get(sectionName );
				if ( sectionFreqsContainer == null ) {
					sectionFreqsContainer = new SectionMetaDataContainer( sectionName );
					sectionFreqHash.put(sectionName, sectionFreqsContainer);
				}
				sectionFreqsContainer.setTermFreq( sectionFreqsContainer.getTermFreq() + 1);
				pWordVector.put(word,  wmdc);
				
			}
			
			
			
		} // end if there are words
	} // end of if this is a section
	
	
} // end Method sectionWords() ------------

// -----------------------------------------
/**
 * sectionAverages 
 * @param pJCas
 * @param pSectionAvgHash
 * @param pSection
 */
// ----------------------------------------
private void sectionAverages(JCas pJCas, HashMap<String, SectionAvgContainer> pSectionAvgHash, Annotation pSection  ) {
	
	// retrieve all the words in the sections
	if ( pSection != null ) {
	
		String sectionName = ((Section)pSection).getSectionName();
		
		if ( sectionName  == null || sectionName.trim().length() == 0 ) return ;
		
		sectionName = normalizeSectionName( sectionName);
		int numberOfSectionWords = 0;
		int numberOfSectionSentences = 0;
		
	
	
		List<Annotation> words = UIMAUtil.getAnnotationsBySpan( pJCas, WordToken.typeIndexID, pSection.getBegin(), pSection.getEnd() );
		List<Annotation> sentences = UIMAUtil.getAnnotationsBySpan( pJCas, Sentence.typeIndexID, pSection.getBegin(), pSection.getEnd() );
		if ( sentences != null && !sentences.isEmpty()) 
			numberOfSectionSentences = sentences.size();
		if ( words != null && !words.isEmpty())
			numberOfSectionWords = words.size();
		
	SectionAvgContainer smdh =  pSectionAvgHash.get( sectionName);
	
	if ( smdh == null) {
		smdh = new SectionAvgContainer( sectionName);
		pSectionAvgHash.put( sectionName, smdh);
	}
	
	smdh.setTotalNumberOfSectionInstances( smdh.getTotalNumberOfSectionInstances()  + 1 );
	smdh.setTotalNumberOfSectionSentences( smdh.getTotalNumberOfSectionSentences() + numberOfSectionSentences);
	smdh.setTotalNumberOfSectionWords( smdh.getTotalNumberOfSectionWords() + numberOfSectionWords);
	smdh.setAvgNumberOfSectionSentences(  smdh.getTotalNumberOfSectionSentences()/ smdh.getTotalNumberOfSectionInstances());

	
	}
	
} // end Method  xxxx() --------------------


//-----------------------------------------
/**
* normalizeSectionName  normalizes sectionName 
*  
*  
* @param pName
* @return String
*/
//----------------------------------------
private String normalizeSectionName(  String pName   ) {
	
	String returnVal = null;
	if ( pName != null ) {
		
		returnVal = pName.trim().toLowerCase();
		returnVal = U.extremeNormalize2(returnVal);
	}
	
	return returnVal;
}

//-----------------------------------------
/**
* sectionNames  keeps a hash of sectionNames by instance counts
*  
* @param pSection
*/
//----------------------------------------
private void sectionNames(  Annotation pSection  ) {
	
	String sectionName = ((Section ) pSection).getSectionName();

	if ( sectionName  == null || sectionName.trim().length() == 0 ) return ;
	
	sectionName = normalizeSectionName( sectionName );
	
	int[] counts = this.sectionNamesHash.get( sectionName);
	if ( counts == null ) {
		counts = new int[1];
		counts[0] = 0;
		this.sectionNamesHash.put( sectionName, counts);
	}
	counts[0]++;
	
} // end Method sectionNames() -------------
	


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
	  
		  
		  HashMap<String, int[]> sectionCells = new HashMap<String,int[]>( MaxSectionTypes);
		  
		  
		  String[] sectionNames = sortByFreqSectionNames( this.sectionNamesHash);
		  
		  // Print the csv header
		  //  word|wordDocFreq|section1|section2| .... |sectionN|
		  printCSVHeader( sectionNames);
		  int i = 0;
		  for ( String sectionName : sectionNames ) {  // a list of section names ideally sorted by decreasing freq
			  int[] ii = new int[1];
			  ii[0] = i;
			  sectionCells.put(sectionName, ii );
			  i++;
		  }
		  // Loop thru the wordVectorHash
		  String words[] = sortDecendingWordFreqs( this.wordVectorHash);
		 
		  for ( String word: words ) {
			   WordMetaDataContainer wv = this.wordVectorHash.get( word );
			   
			   int wordTermFreq = wv.getTermFreq();
			   int wordDocFreq = wv.getDocFreq();
			   int[]  sectionWordDocFreq = getSectionWordDocRow( wv.getSectionFreqs(), sectionCells);
			    
			   // make a row of the section doc freqs for this word -
			   // 
			   // |    |doc  |term|    section word freqs
			   // |word|freq |freq| section1| section2| section3| section4 .... |sectionN |
			   // +----+-----+----+---------+---------+---------+---------      +---------+
			   // |word|     |    |         |         |         |               |         |
			   // +----+-----+----+---------+---------+---------+---------      +---------+
			   this.out.print(word);
			   this.out.print(DELIMITER);
			   this.out.print(wordDocFreq);
			   this.out.print(DELIMITER);
			   this.out.print(wordTermFreq);
			   this.out.print(DELIMITER);
			   for ( int k = 0; k < sectionWordDocFreq.length; k++ ) {
				   this.out.print(sectionWordDocFreq[k]);
				   if ( k < sectionWordDocFreq.length -1) 
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
  private String[] sortDecendingWordFreqs( HashMap<String, WordMetaDataContainer> pWordVectorHash) {
	 
	  String[] buff = new String[ pWordVectorHash.size()];
	  String[] returnVal = new String[pWordVectorHash.size()];
	  
	  Set<String> keys = pWordVectorHash.keySet();
	  int i = 0;
	  for ( String key: keys ) {
		WordMetaDataContainer wcvx = pWordVectorHash.get(key);
		buff[i] = U.zeroPad( wcvx.getDocFreq(),10) + "|" + key;
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
  * sortByFreqSectionNames creates an array of section names, sorted (descending) by frequency
  * 
  * @param pSectionNames
  * @return String[] 
  *
  */
 // -----------------------------------------
  private String[] sortByFreqSectionNames( HashMap<String, int[]> pSectionNames) {
	  String[] buff = new String[ pSectionNames.size()];
	  
	  Set<String> keys = pSectionNames.keySet();
	  int i = 0;
	  for ( String key: keys ) {
		  int[] freqs = pSectionNames.get(key);
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
   private void printCSVHeader( String[] pSectionNames) {
	   
	   this.out.print("word");
	   this.out.print(DELIMITER);
	   this.out.print("DocFreq");
	   this.out.print(DELIMITER);
	   this.out.print("TermFreq");
	   this.out.print(DELIMITER);
	   
	   for ( int i = 0; i < pSectionNames.length; i++ ) {
		   this.out.print(pSectionNames[i]);
		   if ( i < pSectionNames.length -1) 
				this.out.print(DELIMITER);
	   }
	   this.out.print("\n");
	   
   } // end Method printCSVHeader() ---------
  
// -----------------------------------
   /**
    * getSectionWordDocRow
    * @param sectionFreqs
    * @return int[] 
    */
   // -------------------------------
 private int[] getSectionWordDocRow(HashMap<String, SectionMetaDataContainer> sectionFreqs, HashMap<String,int[]>sectionCells) {
	 
	 int[] sectionWordDocFreq = new int[MaxSectionTypes];
	 Set<String> sectionNames = sectionFreqs.keySet();
	
	   for ( String sectionName : sectionNames ) {
		   try {
			  
			   SectionMetaDataContainer sv = sectionFreqs.get(sectionName);
			   int sectionDocFreq = sv.getDocFreq();
			   int sectionTermFreq = sv.getTermFreq();
			   int[] sectionCellz = sectionCells.get( sectionName.trim() );
			   if ( sectionCellz == null ) {
				   throw new Exception("This cannot be for section |" + sectionName + "|");
			   }
			   int sectionCell = sectionCellz[0];
			   if ( this.TERM_OR_DOC_FREQ == TERM_FREQ ) 
				   sectionWordDocFreq[ sectionCell] = sectionTermFreq;
			   else
				   sectionWordDocFreq[ sectionCell] = sectionDocFreq;
		   } catch (Exception e) {
			   e.printStackTrace();
			   System.err.println("Issue with getting the section position freq table for sectionName " + sectionName + " " + e.toString());
		   }
		    
	   }
	return sectionWordDocFreq;
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
   String      stopWordFile = U.getOption(pArgs, "--stopWordFile=", "resources/com/ciitizen/framework/nGram/stopWordList.txt");
   String       numbersFile = U.getOption(pArgs, "--numbersTable=", "resources/com/ciitizen/framework/nGram/numbers.txt");
   String     wordFreqTable = U.getOption(pArgs, "--wordFreqTable=", "resources/com/ciitizen/framework/nGram/termDocFreq.csv");
   int    wordFreqThreshold = Integer.parseInt(U.getOption(pArgs, "--wordFreqThreshold=", "4"));
   int        numberOfCells = Integer.parseInt(U.getOption(pArgs, "--numberOfCells=", "2000"));
             this.DELIMITER = U.getOption(pArgs,  "--delimiter=", "|"); 
    
  
    this.TERM_OR_DOC_FREQ = TERM_FREQ ;
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    
    this.wordVectorHash = new HashMap<String, WordMetaDataContainer>(1000000);
    this.sectionAvgHash = new HashMap<String, SectionAvgContainer> (100000);
    this.sectionNamesHash = new HashMap<String, int[]>( 100000);
    
   
    
    U.mkDir(outputDir + "/csv");
    this.out = new PrintWriter( outputDir + "/csv/wordSectionFreqs_" + STATIC_COUNTER++ + ".csv");
  
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
   private HashMap<String, WordMetaDataContainer > wordVectorHash = null;
   private HashMap<String, SectionAvgContainer> sectionAvgHash = null; 
   private HashMap<String, int[]> sectionNamesHash = null;
   private final static int TERM_FREQ = 1;
   private final static int DOC_FREQ = 0;
   private int TERM_OR_DOC_FREQ = DOC_FREQ;
   
   private String DELIMITER = "|";
   private static int STATIC_COUNTER = 0;
  
   private ProfilePerformanceMeter              performanceMeter = null;
   

 
} // end Class MetaMapClient() ---------------
