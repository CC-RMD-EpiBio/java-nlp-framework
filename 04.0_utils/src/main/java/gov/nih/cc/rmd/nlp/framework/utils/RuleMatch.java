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
/*
 *
 *
 *
 */
package gov.nih.cc.rmd.nlp.framework.utils;

import java.util.List;
import java.util.Map;

import hitex.gate.regex.Rule;
/**
 * Value object to store matches to <code>Rule</code>s
 * @author vhaislreddd
 */
public class RuleMatch {

	private final long start;

	private final long end;

	private final String text;

	private final Rule rule;

	private final Map<String, Object> featureMap;

	private final List<String> captureGroupValues;

	/**
	 * @param start Offset of the start of the match.
	 * @param end Offset of the end of the match.
	 * @param text Text that matched the rule.
	 * @param rule Rule used in the match.
	 * @param featureMap Map of additional features defined in the rule.
	 * @param captureGroupValues List of values of additional captured group values.
	 */
	public RuleMatch(final int start, final int end, final String text,
			final Rule rule,
			final Map<String, Object> featureMap,
			final List<String> captureGroupValues) {
		this.start = start;
		this.end = end;
		this.text = text;
		this.rule = rule;
		this.featureMap = featureMap;
		this.captureGroupValues = captureGroupValues;
	}

	@Override
	public String toString() {
		return "start="	+ start
				+ "; end=" + end
				+ "; text="	+ text
				+ "; name="	+ rule.getName()
				+ "; type="	+ rule.getType()
				+ "; featureMap=" + (featureMap == null ? "null" : featureMap.toString())
				+ "; captureGroupValues=" + (captureGroupValues == null ? "null" : captureGroupValues
						.toString());
	}

	/**
	 * @return Offset of the start of the match.
	 */
	public long getStart() {
		return start;
	}
	/**
	 *  @return Offset of the end of the match.
	 */
	public long getEnd() {
		return end;
	}
	/**
	 *  @return Text that matched the rule.
	 */
	public String getText() {
		return text;
	}
	/**
	 *  @return Rule used in the match.
	 */
	public Rule getRule() {
		return rule;
	}
	/**
	 *  @return Map of additional features defined in the rule.
	 */
	public Map<String, Object> getFeatureMap() {
		return featureMap;
	}
	/**
	 *  @return List of values of additional captured group values.
	 */
	public List<String> getCaptureGroupValues() {
		return captureGroupValues;
	}

}