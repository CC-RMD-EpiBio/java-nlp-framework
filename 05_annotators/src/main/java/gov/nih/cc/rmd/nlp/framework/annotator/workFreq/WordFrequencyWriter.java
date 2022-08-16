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
