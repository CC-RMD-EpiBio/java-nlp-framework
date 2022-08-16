// =================================================
/**
 * LineAnnotator labels lines from text.
 *
 *
 * @author  Guy Divita 
 * @created Jun 22, 2011
 *
 * [Utah/Salt Lake City Preamble Here]
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.workFreq;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.TermUtils;

import gov.nih.cc.rmd.nlp.framework.utils.U;


public class WordFrequencyWriter extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  /**
   * process retrieves lines of the document, labels those that are questions
   * as QuestionAndAnswer elements.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    List<String> words = null;
    String docText = pJCas.getDocumentText();
    HashSet<String> totalDocWords = new HashSet<String>();
    if ( docText != null) {
      String lines[] = docText.split("\n");
        if ( lines != null && lines.length > 0 )
          for ( String line : lines ) {
            try {
              words = termUtils.tokenize(line);
              addToDocWords( totalDocWords, words);
            } catch (Exception e) {
             
              e.printStackTrace();
            }
            if ( words != null && words.size() > 0 ) {
              
              addToWordFreqHash( words );
            } // end if where are words
          } // end loop through lines
      } // end if there is doc Text for this doc 
    
    addToDocFreqHash(totalDocWords );
    
  
  } // end Method process() ----------------
   
//-----------------------------------------
  /**
   * destroy
   * 
   * @param pTotalDocWords
   * 
   */
  // -----------------------------------------
@Override
  public void destroy() {
   
  // ---------------------------------
  //  sort the freqHash by wordFreq
  // ---------------------------------
  Set<String> keys = this.freqHash.keySet();
  String[] buff = new String[keys.size()];
  int freqs[] = null;
  int i = 0;
  for ( String key : keys ) {
    freqs = this.freqHash.get( key);
    buff[i] = U.pad(freqs[0]) + "|" + U.pad(freqs[1]) + "|" + key;
    i++;
  }
  
  
    
    
    
  } // end Method destroy() -----------------
  
  
  //-----------------------------------------
  /**
   * addToDocFreqHash increments the doc freqs for this word
   * 
   * @param pTotalDocWords
   * 
   */
  // -----------------------------------------
  private void addToDocFreqHash(HashSet<String> totalDocWords) {
   
    for ( String word : totalDocWords ) {
      int freqs[] = this.freqHash.get(word);
      freqs[1]++;
    }
    
  } // end Method addToDocFreqHash() --------


  //-----------------------------------------
  /**addToDocWords
   * 
   * @param pHashsSet
   * @param pWords
   * 
   */
  // -----------------------------------------
  private void addToDocWords( HashSet<String> pHashSet, List<String> pWords )  {
   
    for ( String word : pWords )
      pHashSet.add(word);
 
  } // end Method addToDocWords () --------


//-----------------------------------------
 /**addToWordFreqHash Adds these words to a hash
  * that keeps track of how many times each word
  * has been seen
  * 
  * @param pWordList
  * 
  * @return Evidence
  */
 // -----------------------------------------
 private void addToWordFreqHash( List<String> pWords )  {
  
   int[] freqs = null;
   for ( String word: pWords ){
     if ( (freqs = this.freqHash.get(word )) == null ) {
       freqs = new int[2];
       freqs[0] = 0;
       freqs[1] = 0;  
     } // end if freqs were found
     freqs[0]++;
     this.freqHash.put( word, freqs); 
     
   } // end loop through words

 } // end Method addToFreqHash() -------------
  
  
//-----------------------------------------
 /**addToWordFreqHash Adds these words to a hash
  * that keeps track of how many times each word
  * has been seen
  * 
  * @param pWordList
  * 
  * @return Evidence
  */
 // -----------------------------------------
 private void addToDocFreqHash( List<String> pWords )  {
  
   int[] freqs = null;
   for ( String word: pWords ) {
     if ( (freqs = this.freqHash.get(word )) == null ) {
       freqs = new int[2];
       freqs[0] = 0;
       freqs[1] = 0;  
     } // end if freqs were found
     freqs[1]++;
     this.freqHash.put( word, freqs); 
     
   } // end loop through words

 } // end Method addToFreqHash() -------------
 

  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
    if (aContext != null)
      super.initialize(aContext);
   
    
    try {
    
      String knownAcronymsFile = "resources/com/ciitizen/framework/tokenizer/knownAcronyms.txt";
      this.termUtils = new TermUtils( knownAcronymsFile );
      this.freqHash =  new HashMap<String, int[]>();
     

    

   
      
      } catch ( Exception e) {
        e.printStackTrace();
        String msg = "Issue with tokenizer: Could not open the known acronyms file. " + e.toString();
       
        System.err.println(msg);
        throw new ResourceInitializationException();
      }
    
    
      
  } // end Method initialize() -------
  
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  private TermUtils termUtils = null;
  private HashMap<String, int[] > freqHash = null;
  
  
  
} // end Class LineAnnotator() ---------------
