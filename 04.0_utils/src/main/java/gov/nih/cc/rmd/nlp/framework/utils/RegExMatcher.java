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
/*
 *
 *
 *
 */
package gov.nih.cc.rmd.nlp.framework.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hitex.gate.regex.Rule;
import hitex.gate.regex.RuleFactory;
import hitex.gate.regex.util.RuleLoadingException;
import hitex.gate.regex.util.RuleValidationException;

public class RegExMatcher {
	/**
	 * Evaluates each rule against the text
	 * @param text
	 * @param rules
	 * @return A list of matches.
	 */
	public static List<RuleMatch> evaluate(final String text, final List<Rule> rules) {
		List<RuleMatch> matches = new ArrayList<RuleMatch>();
		for(Rule rule : rules) {		
			Pattern pattern = rule.getCompiledPattern();
			int captGroupNum = rule.getCaptGroupNum();
			Collection<String> keys = rule.getFeatureNames();

			Matcher matcher = pattern.matcher(text);

			while(matcher.find()) {
				Map<String, Object> featureMap = new HashMap<String, Object>(keys.size());
				for(String key : keys) {
					featureMap.put(key, rule.getFeatureValue(key));
				}
				List<String> captureGroupValues = new ArrayList<String>(matcher.groupCount());
				for(int i=0; i< matcher.groupCount(); i++){
					captureGroupValues.add(matcher.group(i+1));
				}
				RuleMatch match = new RuleMatch(matcher.start(captGroupNum), matcher.end(captGroupNum), matcher.group(captGroupNum), rule,
						featureMap, captureGroupValues);
				matches.add(match);
			}
		}
		return matches;
	}

	/**
	 * Load regular expression rules from XML string or URL.  Rules from the XML string have priority.
	 * @param rulesXML regular expression rules as an XML string.
	 * @param rulesURL regular expression rules from a URL.
	 * @return A list of regular expression rules.
	 * @throws RuleValidationException 
	 * @throws RuleLoadingException 
	 */
	public static List<Rule> loadRegexRules(final String rulesXML,
			final URL rulesURL) throws RuleLoadingException, RuleValidationException {
		List<Rule> rules;

		if (rulesXML != null) {
			rules = RuleFactory.read(rulesXML);
		} else if (rulesURL != null) {
			rules = RuleFactory.read(rulesURL);
		} else {
			throw new RuleLoadingException(
					"RegEx rule definitions cannot be null.");
		}
		return rules;
	}
}
