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

import java.util.List;

// Utility class to sort the negation rules by length in descending order.
// Rules need to be matched by longest first because there is overlap between the
// RegEx of the rules.
// 

// Author: Imre Solti
// solti@u.washington.edu
// Date: 10/20/2008

public class Sorter {

  //  private static Log logger = LogFactory.getLog(GenNegEx.class);

    public List<String> sortRules(List<String> unsortedRules) {

        try {
            // Sort the negation rules by length to make sure
            // that longest rules match first.

            for (int i = 0; i < unsortedRules.size() - 1; i++) {
                for (int j = i + 1; j < unsortedRules.size(); j++) {
                    String a = (String) unsortedRules.get(i);
                    String b = (String) unsortedRules.get(j);
                    if (a.trim().length() < b.trim().length()) {
                        // Sorting into descending order by lebgth of string.
                        unsortedRules.set(i, b);
                        unsortedRules.set(j, a);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("issue in negex sorter: " + e.toString());
        }
        return unsortedRules;
    }
}
