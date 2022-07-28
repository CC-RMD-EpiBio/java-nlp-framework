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
