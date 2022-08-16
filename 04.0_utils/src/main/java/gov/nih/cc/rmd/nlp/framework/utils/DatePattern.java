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
 * @author Doug Redd Based on the DateAndTime class written by Guy Divita
 */
public class DatePattern {

	private static final String SEP0 = "\\s{0,3}";
	private static final String SEP1 = "\\s{1,3}";
	private static final String TH = "(?:'?(?:st|nd|rd|th))";
	private static final String SINGLE_DAY_OF_MONTH = "(?:01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|1|2|3|4|5|6|7|8|9)";
	private static final String MULTI_DAYS_OF_MONTH = SINGLE_DAY_OF_MONTH
			+ "(?:" + SEP0 + TH + SEP0 + ")?" + "(?:" + SEP0 + "(?:," + SEP0
			+ "|and" + SEP1 + ")" + SINGLE_DAY_OF_MONTH + "(?:" + SEP0 + TH
			+ ")?){0,}";
	private static final String DAY_OF_WEEK = "(?:Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday|Mon\\.?|Tues?\\.?|Wed\\.?|Thurs?\\.?|Fri\\.?|Sat\\.?|Sun\\.?|Thrusday|Wensday)";
	private static final String MONTH_ALPHA = "(?:January|February|March|April|May|June|July|August|September|October|November|December|Jan\\.?|Feb\\.?|Mar\\.?|Apr\\.?|Jun\\.?|Jul\\.?|Aug\\.?|Sept?\\.?|Oct\\.?|Nov\\.?|Dec\\.?)";
	private static final String MONTH_NUMERIC = "(?:01|02|03|04|05|06|07|08|09|10|11|12|1|2|3|4|5|6|7|9)";
	private static final String YEAR = "(?:18|19|20)?\\d\\d";
	private static final String YEAR4 = "(?:18|19|20)\\d\\d";

	private static final String datePatternString =
	/* ORDER IS IMPORTANT */
	/* May 23, 24 and 25 */
	"\\b(?:" + DAY_OF_WEEK + SEP0 + "(?:," + SEP0 + ")?)?" + MONTH_ALPHA + SEP0
			+ MULTI_DAYS_OF_MONTH + "(?!(?:(?:(?:" + SEP0 + "," + SEP0
			+ ")|(?:" + SEP1 + "))" + YEAR + "))\\b" + "|" +
			/*
			 * June 24th, 2008 | June 24 2008 | Friday, January 13, 2006 |
			 * Friday January 13, 2006 | Fri January 13, 2006
			 */
			"\\b(?:" + DAY_OF_WEEK + SEP0 + "(?:," + SEP0 + ")?)?"
			+ MONTH_ALPHA + SEP0 + MULTI_DAYS_OF_MONTH + "(?:(?:" + SEP0 + ","
			+ SEP0 + ")|(?:" + SEP1 + "))" + YEAR + "\\b" + "|" +
			/* 8 June 2008 | 4'th of June, 2008 | 4-January | 18Dec07 */
			"\\b" + MULTI_DAYS_OF_MONTH + SEP0 + "(?:" + TH + SEP1 + ")?"
			+ "(?:(?:" + SEP1 + "of" + SEP1 + ")|(?:" + "-" + SEP0 + "))?"
			+ MONTH_ALPHA + "(?:(?:" + SEP0 + ",)?" + SEP0 + YEAR + ")?\\b"
			+ "|" +
			/* September 2001 */
			"\\b" + MONTH_ALPHA + SEP0 + YEAR + "\\b"
			/* Dec7 | Dec 7 | March 27 */
			+ "|" + "\\b" + MONTH_ALPHA + SEP0 + MULTI_DAYS_OF_MONTH + "(?:"
			+ SEP0 + TH + ")?\\b"
			/* Friday, 14'th | Fri 14'th | Friday the 14'th */
			+ "|" + "\\b" + DAY_OF_WEEK + "(?:" + SEP1 + "(?:the" + SEP1
			+ ")?|" + SEP0 + "," + SEP0 + ")?" + MULTI_DAYS_OF_MONTH + "(?:"
			+ SEP0 + TH + ")?\\b" + "|" +
			/* 2010/12/13 | 2010-12-13 */
			"\\b" + YEAR + "(/|-)" + MONTH_NUMERIC + "\\1"
			+ SINGLE_DAY_OF_MONTH + "\\b" + "|" +
			/* 13/12/2010 | 13-12-2010 */
			"\\b" + SINGLE_DAY_OF_MONTH + "(/|-)" + MONTH_NUMERIC + "\\2"
			+ YEAR + "\\b" + "|" +
			/*
			 * 12/13/2010 | 12-13-2010 | 12/13/10 | 12-13-10 | 9/30/10 | 9-30-10
			 * | 13/12/10 | 13-12-10 | 9/3/10 | 9-3-10
			 */
			"\\b" + MONTH_NUMERIC + "(/|-)" + SINGLE_DAY_OF_MONTH + "\\3"
			+ YEAR + "\\b" + "|" +
			/* 1998/1999 | 1998-1999 | 1968-2010 */
			"\\b" + YEAR4 + SEP0 + "(?:/|-)" + SEP0 + YEAR4 + "\\b" + "|" +
			/* 12/2010 | 12-2010 | 9/3/10 | 9-3-10 */
			"\\b" + MONTH_NUMERIC + SEP0 + "(?:/|-)" + SEP0 + YEAR4 + "\\b"
			+ "|" +
			/* 12/13 | 12-13 */
			"\\b" + MONTH_NUMERIC + SEP0 + "(/|-)" + SEP0 + SINGLE_DAY_OF_MONTH
			+ "(?:" + SEP0 + "(?:/|-)" + SEP0 + MONTH_NUMERIC + SEP0 + "\\4"
			+ SEP0 + SINGLE_DAY_OF_MONTH + ")?\\b" + "|" +
			/* 13/12 | 13-12 | 13/10-14/12 */
			"\\b" + MULTI_DAYS_OF_MONTH + SEP0 + "(/|-)" + SEP0 + MONTH_NUMERIC
			+ "(?:" + SEP0 + "(?:/|-)" + SEP0 + MULTI_DAYS_OF_MONTH + SEP0
			+ "\\5" + SEP0 + MONTH_NUMERIC + ")?\\b" + "|" +
			/* 2006/April | '08-June */
			"(?:\\b|')" + YEAR + SEP0 + "(?:/|-)" + SEP0 + MONTH_ALPHA + "\\b"
			+ "|" +
			/* 2010 | Early-2008 | 1980's | '80s */
			"(?:\\b|')(?:(?:Early|Mid|Late)(?:(?:" + SEP0 + "-" + SEP0
			+ ")|(?:" + SEP1 + ")))?"
			+ "(?:(?:'\\d\\d|(?:18|19|20)))?\\d\\d(?:(?:')?s)?\\b";

	private static Pattern datePattern = null;

	public static Collection<PatternMatch> getMatches(final String str) {
		Collection<PatternMatch> matches = new ArrayList<PatternMatch>();
		if (datePattern == null) {
			datePattern = Pattern.compile(datePatternString);
		}
		Matcher m = datePattern.matcher(str);
		while (m.find()) {
			matches.add(new PatternMatch(m.start(), m.end(), m.group()));
		}
		return matches;
	}
}
null
