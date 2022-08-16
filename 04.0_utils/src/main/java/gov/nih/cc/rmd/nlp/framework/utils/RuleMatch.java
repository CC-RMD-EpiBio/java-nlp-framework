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
