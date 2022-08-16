/*
 *
 *
 *
 */
package gov.nih.cc.rmd.nlp.framework.utils;

/**
 * Holds pattern match information (Start, end, string).
 * 
 * TODO: Should constructor and setters allow for null string? Currently does.  
 *
 */
public class PatternMatch {
	private int start;
	private int end;
	private String str;

	public PatternMatch(final int start, final int end, final String str) {
		this.start = start;
		this.end = end;
		this.str = str;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}
}
