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

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Doug Redd 
 */
public class UnitOfMeasurePattern {

	private static final String SEP0 = "\\s{0,3}";
	private static final String SEP1 = "\\s{1,3}";
	
  
	private static final String NUMBER = "/\\d+\\.\\d+|\\b\\.\\d+|\\d+/";
	private static final String RANGE_OPERATOR = "/-|/|to|through|thru|or|and/";
	
	private static final String RANGE = "/(?:" + NUMBER  + ")" + SEP0 + "}(?:" + RANGE_OPERATOR + ")" + SEP0 + "(?:" + NUMBER + ")/";
	
	private static final String QUANTITY = "/(?:(?:" + RANGE + ")|(?:" + NUMBER + "))/";
	  
	private static final String UNITS_NO_PREFIX = "/tablets?|tabs?|capsules?|epidurals?|IU|UI|SI|inches|inch|in\\.|in|foot|feet|gallons?|gal|fl\\.?" + SEP0 + "oz\\.?|oz\\.?" + SEP0 + "fl\\.?|oz\\.?|fluid" + SEP1 + "ounces?|ounces?|cups?|quarts?|pounds?|lbs?\\.?|minutes?|min\\.?|hours?|hr\\.?|days?|months?|mos?\\.?|weeks?|wks?\\.?|years?|yrs?\\.?|y/o|p\\.?h\\.?|vials?|ppm|ppb|lpf|hpf|field|droplets?|log" + SEP0 + "copies|copies|repeats|eia|ratio|mom|cells?|od|bands?|ypll|isr|inr|s\\.?d\\.?|pan" + SEP0 + "bio|psi|degrees?|deg\\.?|fahrenheit|f|celcius|c|kelvin|k|volumes?|vol\\.?|diopters?|diop\\.?|me|points?|oe|n|bp|cpm|cfu|teaspoons?|tsp\\.?|tablespoons?|tbs\\.?/";
	    
	private static final String PREFIXES = "/femto|f|pico|p|nano|n|micro|mc|\u00B5|\u03BC|u|milli|m|centi|cubi|cm|c\\.|c|deci|d|kilo|k|mega|M|giga|G|tera|T/";
	private static final String SUFFIXES = "/\\^?[123]|" + SEP0  + "(?:cubed|squared)/";
	private static final String UNITS_FOR_PREFIXES = "/(?:meters?|m\\.|m|liters?|l|volts?|v|grams?|gm|g|c\\.|c|seconds?|secs?\\.?|s\\.?|osmoles?|osmols?\\.?|osm\\.?|moles?|mols?\\.?|titers?|hg|h2o|units?|u|eq|bsa|joules?|calories?|gy|angstroms?|ang\\.?|au|btu|base|b(?!\\.?\\w)|daltons?|da\\.?|-\\d|hertz|hz|kg)/";
			
	private static final String POWERS = "/cubic|cubed|cu|square|squared|s\\.|s|micro|m/";
	private static final String UNITS_WITH_PREFIXES = "/(?:" + POWERS  + ")" + SEP0  + "(?:" + PREFIXES  + ")" + UNITS_FOR_PREFIXES  + "|(?:" + PREFIXES  + ")" + UNITS_FOR_PREFIXES  + "|" + UNITS_FOR_PREFIXES  + "/";
	private static final String UNITS = "/(?:" + POWERS  + "" + SEP0  + ")?(?:(?:" + UNITS_NO_PREFIX + ")|(?:" + UNITS_WITH_PREFIXES  + "))(?:" + SUFFIXES  + ")?/";
	private static final String QUOTES = "/\"|'/";
	  
	private static final String HOUR = "/(?:2[0-4])|(?:[01]?\\d)/";

	private static final String COMPLETE_PATTERN = "/\\b((?:" + QUANTITY +  SEP0 + "))(?:" + UNITS  + "(?:\\.|\\b)|" + QUOTES + "(?!\\w))/";

	

	private static final String unitOfMeasurePatternString = COMPLETE_PATTERN;
	

	private static Pattern unitOfMeasurePattern = null;

	public static Collection<PatternMatch> getMatches(final String str) {
		Collection<PatternMatch> matches = new ArrayList<PatternMatch>();
		if (unitOfMeasurePattern == null) {
			unitOfMeasurePattern = Pattern.compile(unitOfMeasurePatternString);
		}
		Matcher m = unitOfMeasurePattern.matcher(str);
		while (m.find()) {
			matches.add(new PatternMatch(m.start(), m.end(), m.group()));
		}
		return matches;
	}
}
