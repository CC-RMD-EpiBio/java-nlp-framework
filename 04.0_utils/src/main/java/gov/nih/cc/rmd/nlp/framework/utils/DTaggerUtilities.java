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
 * DTagger is a placeholder for where this conversion method
 * came from.  The dTagger natively used the SPECIALIST tag set
 * and when needed could convert SPECIALIST tags to the PennTreebank
 * style tags. This class includes the logic to do the conversion.
 *
 *
 * @author  Guy Divita 
 * @created Feb 21, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils;

import java.util.Hashtable;


public class DTaggerUtilities {

  // -----------------------------------------
  /**
   * toPenn returns the Penn Treebank mapping given a category, inflection
   * and agreement information.
   * 
   * @param pWord
   * @param pCategory
   * @param pInflection
   * @return
   */
  // -----------------------------------------
  public static String toPenn(String pWord, String pCategory, String pInflection) {
    
    String pennTag = "NONE";
    

    if       ( pCategory.matches(  AUX )) {
      if      (pInflection.contains("infinitive" ))         pennTag = "VB";
      else if (pInflection.contains("past_part"))           pennTag = "VBN";
      else if (pInflection.contains("past"))                pennTag = "VBD";
      else if (pInflection.contains("pres_part"))           pennTag = "VBG";
      else if (pInflection.contains("pres_fst_sing"))       pennTag = "VBP";
      else if (pInflection.contains("pres_thr_sing"))       pennTag = "VBZ";
      else if (pInflection.contains("pres_fst_plur_second_thr_plur")) pennTag = "VBP";
      else if (pInflection.contains("pres_fst_plur_second_thr_plur_negative")) pennTag = "VBP";
      else                                       pennTag = "VB";
    } else if ( pCategory.matches(  MODAL       )) {       pennTag = "MD";
    } else if ( pCategory.matches(  VERB        )) {
      if      (pInflection.contains("infinitive"))          pennTag = "VB";
      else if (pInflection.contains("past_part"))           pennTag = "VBN";
      else if (pInflection.contains("past"))                pennTag = "VBD";
      else if (pInflection.contains("pres_part"))           pennTag = "VBG";
      else if (pInflection.contains("pres_fst_sing"))       pennTag = "VBP";
      else if (pInflection.contains("pres_thr_sing"))       pennTag = "VBZ";
    }else if ( pCategory.matches(  PREP         )){
      if  (!pWord.equals("to"))                   pennTag = "IN";
      else                                       pennTag = "TO"; 
    }else if ( pCategory.matches(  NOUN        )) {
      //  TBD - hook in isProper
      //if ( isProper(eui)) {
      //if      (pInflection.contains(PLUR))              pennTag = "NNPS";
      //else if (pInflection.contains(SING))              pennTag = "NNP";
      //else
      if      (pInflection.contains("plur"))                pennTag = "NNS";
      else if (pInflection.contains("sing"))                pennTag = "NN";
      else { System.err.println("inflSet on noun failed " + pInflection + "\n"); }

    } else if ( pCategory.matches(  COMPL        )){         pennTag = "IN";
    } else if ( pCategory.matches(  ADJ          )){
      if      (pInflection.contains("comparative"))         pennTag = "JJR";
      else if (pInflection.contains("positive"   ))         pennTag = "JJ";
      else if (pInflection.contains("superlative"))         pennTag = "JJS";
    } else if ( pCategory.matches(  ADV          )){
      if    (pInflection.contains("comparative") )          pennTag = "RBR";
      else if (pInflection.contains("positive"))  { 
         if      ( pWord.equals("to") ) pennTag = "to";
         // else if ( pWord.equals("where")) &&
         // (!pWord.equals("when")) )     
                                           pennTag = "RB";

      } else if (pInflection.contains("superlative"))         pennTag = "RBS";
      
    } else if ( pCategory.matches(  PRON          )) {
      if ( pWord.matches("mine|yours|his|hers|its|ours|theirs"))
                                           pennTag = "PRP$";
      else if ( pWord.matches ("what|whom|who"))
                                     pennTag = "WP"; 
      else if ( pWord.matches ("whose"))
                                           pennTag = "WP$";
      else 
        pennTag = "PP";
    } else if ( pCategory.matches(  DET          )){
      if (pWord.matches("all|such|half|both|quite|many|nary")) 
                                                 pennTag = "PDT";
      else                                         
                                           pennTag = "DT";
    } else if ( pCategory.matches(  CONJ         )) {
      if ( pWord.matches("although|though|whereas|while|because|as|since"))
                                           pennTag = "IN";
      else         
                                           pennTag = "CC";
    } else if ( pCategory.matches(  TO           )){        pennTag = "TO";
    } else if ( pCategory.matches(  PERIOD       )){        pennTag = "PD"; 

    } else if ( pCategory.matches(  PUNCT ))
         { pennTag = "Symbol"; 

    } else if (( pCategory.matches(  NUM ))|| ( pCategory.matches(  FRACTION ))) { pennTag = "CD";

    } else if (( pCategory.matches(  PREFIX ))
         || ( pCategory.matches(  WHITESPACE   ))
         || ( pCategory.matches(  NONE         )) 
         || ( pCategory.matches(  EOL          )) 
         || ( pCategory.matches(  REAL         )) 
         || ( pCategory.matches(  UNKNOWN      ))) { pennTag = "NONE";
    } else {                                    pennTag = "NONE";
    } // end switch int Tag
    
    return ( pennTag );
    
  } // end Method () -------------------------------
  
  // -----------------------------------------
  /**
   * isAuxOrModal returns true if the word is an aux or modal
   * 
   * @param word
   * @return boolean
   */
  // -----------------------------------------
  public static boolean isAuxOrModal(String word) {

    boolean returnValue = false;
    boolean dummy[] = new boolean[1];
    dummy[0] = true;
    
    // -------------------------------------------
    // If this the first time the method is called
    // load up the hash with aux's and modals
    // -------------------------------------------
    if (auxAndModalHash == null) {
      auxAndModalHash = new Hashtable<String,boolean[]>(88);
      for ( int i = 0; i < auxAndModels.length; i++ ) auxAndModalHash.put( auxAndModels[i], dummy);
    }
    if ( auxAndModalHash.get( word)!= null)
       returnValue = true;
    
    return returnValue;
  } // end Method isAuxOrModal() -------------

  //-------------------------------------------------------
  // Class Variables
  // ------------------------------------------------------
  private static final String NOUN  =  "noun";
  private static final String ADJ   =  "adj";
  private static final String ADV   =  "adv";
  private static final String VERB  =  "verb";
  private static final String AUX   =  "aux";
  private static final String MODAL =  "modal";
  private static final String DET   =  "det";
  private static final String CONJ  =  "conj";
  private static final String PRON  =  "pron";
  private static final String PREP  =  "prep";
  private static final String TO    =  "to";
  private static final String COMPL =  "compl";
  private static final String NUM   =  "num";
  private static final String FRACTION = "fraction";
  private static final String PREFIX = "prefix";
  private static final String WHITESPACE = "whitespace";
  private static final String NONE = "none";
  private static final String EOL = "eol";
  private static final String REAL = "real";
  private static final String UNKNOWN = "unknown";
  private static final String PERIOD = "pd";
  private static final String PUNCT   = "punct";
  
  
  private static Hashtable<String,boolean[]> auxAndModalHash = null;
  private static final String[] auxAndModels = { "be", "is", "isn't", "are", "am", "was", "wasn't", "were", "weren't", "been", "being", 
    "can", "could", "couldn't", "cannot", "can't", 
  "dare", "daren't", 
  "do", "don't", "does", "did", "didn't",
  "have", "has", "had", "hadn't", "hasn't", "haven't",
  "may", "might", "mayn't", "mightn't",
  "must", "mustn't",
  "ought", "oughtn't",
  "shall", "should", "shan't", "shouldn't",
  "will", "would", "wouldn't" }; 
  
  
  
} // end Class DTagger() --------------------------
