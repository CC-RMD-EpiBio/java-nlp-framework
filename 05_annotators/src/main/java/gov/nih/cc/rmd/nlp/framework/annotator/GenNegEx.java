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
/**
 *
 * All rights reserved.
 */
package gov.nih.cc.rmd.nlp.framework.annotator;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * ************************************************************************************
 * Author: Imre Solti
 * Date: 09/15/2008
 * Modified: 04/15/2009
 * Changed to specifications of test kit and discussions with WC and PH.
 * Modified: 04/26/2009
 * Fixed the deletion of last character in scope fo PREN, PREP negation scopes.
 * <p/>
 * Wendy Chapman's NegEx algorithm in Java.
 * <p/>
 * Sentence boundaries serve as WINDOW for negation (suggested by Wendy Chapman)
 * <p/>
 * **************************************************************************************
 */

/*


you may not use this file except in compliance with the License. You may obtain a copy of the License at 


Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
*/

public class GenNegEx {

   // private static Log logger = LogFactory.getLog(GenNegEx.class);
    private List<String>sortedRules = null;

    String[] TO_ESCAPE = {"+", "*", ".", "(", ")", "}", "{", "?", "$", "^"};

    public String negCheck(String sentenceString, String phraseString, List<String> ruleStrings,
                           boolean negatePossible) throws Exception {

        Sorter s = new Sorter();
        String sToReturn = "";
        String sScope = "";
        

        String filler = "_";
        boolean negPoss = negatePossible;

        // Sort the rules by length in descending order.
        // Rules need to be sorted so the longest rule is always tried to match
        // first.
        // Some of the rules overlap so without sorting first shorter rules (some of them POSSIBLE or PSEUDO)
        // would match before longer legitimate negation rules.
        //

        // There is efficiency issue here. It is better if rules are sorted by the
        // calling program once and used without sorting in GennegEx.
        if ( this.sortedRules == null) {
          this.sortedRules = new ArrayList<String>();
          this.sortedRules = s.sortRules(ruleStrings);
        } 
        
        
        // Process the sentence and tag each matched negation
        // rule with correct negation rule tag.
        //
        // At the same time check for the phrase that we want to decide
        // the negation status for and
        // tag the phrase with [PHRASE] ... [PHRASE]
        // In both the negation rules and in the  phrase replace white space
        // with "filler" string. (This could cause problems if the sentences
        // we study has "filler" on their own.)

        // Sentence needs one character in the beginning and end to match.
        // We remove the extra characters after processing.
        String sentence = "." + sentenceString + ".";

        // Tag the phrases we want to detect for negation.
        // Should happen before rule detection.
        String phrase = phraseString;  // <-- brackets across lines causes a problem
        Pattern pph = null;
        try {
            pph = Pattern.compile(phrase.trim(), Pattern.CASE_INSENSITIVE);
        } catch (Exception e) {
            // IF There was an exception, escape the phrase for special regex characters. It is more
            // efficient to only escape if an error, as most phrases will work fine.
            // logger.info("In Special processing... (" + phrase.trim() + ")");
         
          try {
            pph = Pattern.compile(escapeRegexCharacters(phrase.trim()), Pattern.CASE_INSENSITIVE);
          } catch (Exception e2 ) {
            // System.err.println(" another exception with " + phrase);
            throw new Exception( "Trouble looking for negation pattern in phrase |" + phrase + "| " + e2);
          }
            
        }
         Matcher mph = pph.matcher(sentence);

        while (mph.find() == true) {
            sentence = mph.replaceAll(" [PHRASE]" + mph.group().trim().replaceAll(" ", filler)
                    + "[PHRASE]");
        }

        Iterator<String> iRule = this.sortedRules.iterator();
        while (iRule.hasNext()) {
            String rule = iRule.next();
            Pattern p = Pattern.compile("[\\t]+");     // Working.
            String[] ruleTokens = p.split(rule.trim());
            // Add the regular expression characters to tokens and asemble the rule again.
            String[] ruleMembers = ruleTokens[0].trim().split(" ");
            String rule2 = "";
            for (int i = 0; i <= ruleMembers.length - 1; i++) {
                if (!ruleMembers[i].equals("")) {
                    if (ruleMembers.length == 1) {
                        rule2 = ruleMembers[i];
                    } else {
                        rule2 = rule2 + ruleMembers[i].trim() + "\\s+";
                    }
                }
            }
            // Remove the last s+
            if (rule2.endsWith("\\s+")) {
                rule2 = rule2.substring(0, rule2.lastIndexOf("\\s+"));
            }

            rule2 = "(?m)(?i)[[\\p{Punct}&&[^\\]\\[]]|\\s+](" + rule2 + ")[[\\p{Punct}&&[^_]]|\\s+]";

            Pattern p2 = Pattern.compile(ruleTokens[0].trim());
            Matcher m = p2.matcher(sentence);

            while (m.find()) {
                String rpWith = ruleTokens[2].substring(2).trim();
                sentence = m.replaceAll(" " + rpWith
                        + m.group().trim().replaceAll(" ", filler)
                        + rpWith + " ");
            }
        }

        // Exchange the [PHRASE] ... [PHRASE] tags for [NEGATED] ... [NEGATED]
        // based of PREN, POST rules and if flag is set to true
        // then based on PREP and POSP, as well.

        // Because PRENEGATION [PREN} is checked first it takes precedent over
        // POSTNEGATION [POST].
        // Similarly POSTNEGATION [POST] takes precedent over POSSIBLE PRENEGATION [PREP]
        // and [PREP] takes precedent over POSSIBLE POSTNEGATION [POSP].

        Pattern pSpace = Pattern.compile("[\\s+]");
        String[] sentenceTokens = pSpace.split(sentence);
        StringBuilder sb = new StringBuilder();


        // Check for [PREN]
        for (int i = 0; i < sentenceTokens.length; i++) {
            sb.append(" " + sentenceTokens[i].trim());
            if (sentenceTokens[i].trim().startsWith("[PREN]") || sentenceTokens[i].trim().startsWith("[PRE_NEG]")) {

                for (int j = i + 1; j < sentenceTokens.length; j++) {
                    if (sentenceTokens[j].trim().startsWith("[CONJ]") ||
                            sentenceTokens[j].trim().startsWith("[PSEU]") ||
                            sentenceTokens[j].trim().startsWith("[POST]") ||
                            sentenceTokens[j].trim().startsWith("[PREP]") ||
                            sentenceTokens[j].trim().startsWith("[POSP]")) {
                        break;
                    }

                    if (sentenceTokens[j].trim().startsWith("[PHRASE]")) {
                        sentenceTokens[j] = sentenceTokens[j].trim().replaceAll("\\[PHRASE\\]", "[NEGATED]");
                    }
                }
            }
        }

        sentence = sb.toString();
        pSpace = Pattern.compile("[\\s+]");
        sentenceTokens = pSpace.split(sentence);
        StringBuilder sb2 = new StringBuilder();

        // Check for [POST]
        for (int i = sentenceTokens.length - 1; i > 0; i--) {
            sb2.insert(0, sentenceTokens[i] + " ");
            if (sentenceTokens[i].trim().startsWith("[POST]")) {
                for (int j = i - 1; j > 0; j--) {
                    if (sentenceTokens[j].trim().startsWith("[CONJ]") ||
                            sentenceTokens[j].trim().startsWith("[PSEU]") ||
                            sentenceTokens[j].trim().startsWith("[PRE_NEG]") ||
                            sentenceTokens[j].trim().startsWith("[PREN]") ||
                            sentenceTokens[j].trim().startsWith("[PREP]") ||
                            sentenceTokens[j].trim().startsWith("[POSP]")) {
                        break;
                    }

                    if (sentenceTokens[j].trim().startsWith("[PHRASE]")) {
                        sentenceTokens[j] = sentenceTokens[j].trim().replaceAll("\\[PHRASE\\]", "[NEGATED]");
                    }
                }
            }
        }

        sentence = sb2.toString();

        // If POSSIBLE negation is detected as negation.
        // negatePossible being set to "true" then check for [PREP] and [POSP].
        if (negPoss == true) {
            pSpace = Pattern.compile("[\\s+]");
            sentenceTokens = pSpace.split(sentence);

            StringBuilder sb3 = new StringBuilder();

            // Check for [PREP]
            for (int i = 0; i < sentenceTokens.length; i++) {
                sb3.append(" " + sentenceTokens[i].trim());
                if (sentenceTokens[i].trim().startsWith("[PREP]")) {

                    for (int j = i + 1; j < sentenceTokens.length; j++) {
                        if (sentenceTokens[j].trim().startsWith("[CONJ]") ||
                                sentenceTokens[j].trim().startsWith("[PSEU]") ||
                                sentenceTokens[j].trim().startsWith("[POST]") ||
                                sentenceTokens[j].trim().startsWith("[PRE_NEG]") ||
                                sentenceTokens[j].trim().startsWith("[PREN]") ||
                                sentenceTokens[j].trim().startsWith("[POSP]")) {
                            break;
                        }

                        if (sentenceTokens[j].trim().startsWith("[PHRASE]")) {
                            sentenceTokens[j] = sentenceTokens[j].trim().replaceAll("\\[PHRASE\\]", "[POSSIBLE]");
                        }
                    }
                }
            }

            sentence = sb3.toString();
            pSpace = Pattern.compile("[\\s+]");
            sentenceTokens = pSpace.split(sentence);
            StringBuilder sb4 = new StringBuilder();

            // Check for [POSP]
            for (int i = sentenceTokens.length - 1; i > 0; i--) {
                sb4.insert(0, sentenceTokens[i] + " ");
                if (sentenceTokens[i].trim().startsWith("[POSP]")) {
                    for (int j = i - 1; j > 0; j--) {
                        if (sentenceTokens[j].trim().startsWith("[CONJ]") ||
                                sentenceTokens[j].trim().startsWith("[PSEU]") ||
                                sentenceTokens[j].trim().startsWith("[PREN]") ||
                                sentenceTokens[j].trim().startsWith("[PRE_NEG]") ||
                                sentenceTokens[j].trim().startsWith("[PREP]") ||
                                sentenceTokens[j].trim().startsWith("[POST]")) {
                            break;
                        }

                        if (sentenceTokens[j].trim().startsWith("[PHRASE]")) {
                            sentenceTokens[j] = sentenceTokens[j].trim().replaceAll("\\[PHRASE\\]", "[POSSIBLE]");
                        }
                    }
                }
            }

            sentence = sb4.toString();
        }

        // Remove the filler character we used.
        sentence = sentence.replaceAll(filler, " ");

        // Remove the extra periods at the beginning
        // and end of the sentence.
        sentence = sentence.substring(0, sentence.trim().lastIndexOf('.'));
        sentence = sentence.replaceFirst(".", "");

        // Get the scope of the negation for PREN and PREP
        if (sentence.contains("[PRE_NEG]") || sentence.contains("[PREN]") || sentence.contains("[PREP]")) {
            int startOffset = sentence.indexOf("[PREN]");
            if (startOffset == -1) {
                startOffset = sentence.indexOf("[PRE_NEG]");
            }
            if (startOffset == -1) {
                startOffset = sentence.indexOf("[PREP]");
            }

            int endOffset = sentence.indexOf("[CONJ]");
            if (endOffset == -1) {
                endOffset = sentence.indexOf("[PSEU]");
            }
            if (endOffset == -1) {
                endOffset = sentence.indexOf("[POST]");
            }
            if (endOffset == -1) {
                endOffset = sentence.indexOf("[POSP]");
            }
            if (endOffset == -1 || endOffset < startOffset) {
                endOffset = sentence.length() - 1;
            }
            sScope = sentence.substring(startOffset, endOffset + 1);
        }

        // Get the scope of the negation for POST and POSP
        if (sentence.contains("[POST]") || sentence.contains("[POSP]")) {
            int endOffset = sentence.lastIndexOf("[POST]");
            if (endOffset == -1) {
                endOffset = sentence.lastIndexOf("[POSP]");
            }

            int startOffset = sentence.lastIndexOf("[CONJ]");
            if (startOffset == -1) {
                startOffset = sentence.lastIndexOf("[PSEU]");
            }
            if (startOffset == -1) {
                startOffset = sentence.lastIndexOf("[PREN]");
            }
            if (startOffset == -1) {
                startOffset = sentence.lastIndexOf("[PRE_NEG]");
            }
            if (startOffset == -1) {
                startOffset = sentence.lastIndexOf("[PREP]");
            }
            if (startOffset == -1) {
                startOffset = 0;
            }
            sScope = sentence.substring(startOffset, endOffset);
        }

        // Classify to: negated/possible/affirmed
        if (sentence.contains("[NEGATED]")) {
            sentence = sentence + "\t" + "negated" + "\t" + sScope;
        } else if (sentence.contains("[POSSIBLE]")) {
            sentence = sentence + "\t" + "possible" + "\t" + sScope;
        } else {
            sentence = sentence + "\t" + "affirmed" + "\t" + sScope;
        }

        sToReturn = sentence;

        return sToReturn;
    }

    protected String escapeRegexCharacters(String s) {
        String returnVal = new String(s);
        for (int i = 0; i < TO_ESCAPE.length; i++) {
            returnVal = returnVal.replaceAll("\\" + TO_ESCAPE[i], "\\\\" + TO_ESCAPE[i]);

        }
        return returnVal;
    }
}
