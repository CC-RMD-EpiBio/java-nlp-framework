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
