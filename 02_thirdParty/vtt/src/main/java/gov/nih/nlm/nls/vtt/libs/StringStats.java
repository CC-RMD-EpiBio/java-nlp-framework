package gov.nih.nlm.nls.vtt.libs;
import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
/*****************************************************************************
* This code is to calculate the stats for a String. It includes character count,
* word count, and line count.
*
* <p><b>History:</b>
* <ul>
* </ul>
*
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class StringStats
{
	// public constructors
	/**
	* The constructor of StringStats object.
	*/
    public StringStats()
    {
        charNum_ = 0;
        wordNum_ = 0;
        lineNum_ = 0;
    }
	/**
	* The constructor of StringStats object by specifying total number of 
	* characters, total number of characters with line feed,
	* total number of words, total number of lines, 
	* and a vector of start position of each line
	*/
    public StringStats(int charNum, int charNum2, int wordNum, int lineNum,
		Vector<Integer> linePos)
    {
        charNum_ = charNum;
        charNum2_ = charNum2;
        wordNum_ = wordNum;
		lineNum_ = lineNum;
		linePos_ = new Vector<Integer>(linePos);
    }
	// public methods
	/**
	* Get the total number of characters without line feed
	*
	* @return total number of characters without line feed
	*/
	public int getCharNum()
	{
		return charNum_;
	}
	/**
	* Get the total number of characters with line feed
	*
	* @return total number of characters with line feed
	*/
	public int getCharNum2()
	{
		return charNum2_;
	}
	/**
	* Get the total number of words
	*
	* @return total number of words
	*/
	public int getWordNum()
	{
		return wordNum_;
	}
	/**
	* Get the total number of lines
	*
	* @return total number of lines
	*/
	public int getLineNum()
	{
		return lineNum_;
	}
	/**
	* Get the start position of each line
	*
	* @return a vector of start position of each line
	*/
	public Vector<Integer> getLinePos()
	{
		return linePos_;
	}
	/**
	* Get the difference stats of an input string in string presentation
	*
	* @param inStr input string of difference
	*
	* @return the stats of string difference
	*/
    public static StringStats getStringStats(String inStr)
    {
		StringStats stringStats = new StringStats();
		int charNum = 0;
		int charNum2 = 0;
        int wordNum = 0;
        int lineNum = 0;
		Vector<Integer> linePos = new Vector<Integer>();	
		int lineStartPos = 0;
        try
        {
            BufferedReader br = new BufferedReader(new StringReader(inStr));
            String line = null;
            while((line = br.readLine()) != null)
            {
				linePos.addElement(new Integer(lineStartPos));
                charNum += line.length();
                wordNum += getWordCount(line);
                lineNum++;
				lineStartPos = charNum + lineNum;
            }
			charNum2 = inStr.length();
			linePos.addElement(new Integer(lineStartPos));// add last char
			stringStats = new StringStats(charNum, charNum2, wordNum, lineNum,
				linePos);
        }
        catch (Exception e)
        {
            System.err.println("** Err@StringStats.GetStringStats( ): "
                + inStr + " (" + lineNum + "), " + e);
            final String fMsg = e.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
				}
			});
        }
		return stringStats;
    }
	/**
	* Get the line start positions.
	* 
	* @param linePos the position of all lines
	* @param lineNo the line no in interested
	*
	* @return the starting position of the specified line
	*/
	public static int getLineStartPos(Vector<Integer> linePos, int lineNo)
	{
		int lineStart = -1;
		int totalLineNo = linePos.size();
		// lineNo starts with 1
		// LinePos has one extra position for the last character+1
		if((lineNo > 0) && (lineNo < totalLineNo))
		{
			lineStart = linePos.elementAt(lineNo-1);
		}
		return lineStart;
	}
	/**
	* Get the line end positions.
	* 
	* @param linePos the position of all lines
	* @param lineNo the line no in interested
	*
	* @return the end position of the specified line
	*/
	public static int getLineEndPos(Vector<Integer> linePos, int lineNo)
	{
		int lineEnd = -1;
		int totalLineNo = linePos.size();
		// lineNo starts with 1
		if((lineNo > 0) && (lineNo < totalLineNo))
		{
			lineEnd = linePos.elementAt(lineNo) - 1;	// use the next line
		}
		return lineEnd;
	}
	/**
	* The string representation of the string difference.
	*
	* @return string representation of the string difference
	*/
	public String toString()
	{
		// print out the string
        StringBuffer sb = new StringBuffer();
		sb.append("- Number of characters: " + charNum_ + " (" + charNum2_ 
			+ ")");
        sb.append(DiffUtil.LS_STR);
        sb.append("- Number of words: " + wordNum_);
        sb.append(DiffUtil.LS_STR);
        sb.append("- Number of lines: " + lineNum_);
        sb.append(DiffUtil.LS_STR);
        return sb.toString();
	}
	// private methods
    private static int getWordCount(String line)
    {
        int numWord = 0;
        int index = 0;
        boolean prevWhiteSpace = true;
        while(index < line.length())
        {
            char c = line.charAt(index++);
            boolean curWhiteSpace = Character.isWhitespace(c);
            if(prevWhiteSpace && !curWhiteSpace)
            {
                numWord++;
            }
            prevWhiteSpace = curWhiteSpace;
        }
        return numWord;
    }
	// data member
	private int charNum_ = 0;		// total number of chars
	private int charNum2_ = 0;		// includes line return character
	private int wordNum_ = 0;		// total number of words
	private int lineNum_ = 0;		// total number of lines
	// the start pos of each line
	private Vector<Integer> linePos_ = new Vector<Integer>();	
}
