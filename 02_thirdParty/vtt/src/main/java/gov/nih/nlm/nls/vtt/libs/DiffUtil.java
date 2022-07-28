package gov.nih.nlm.nls.vtt.libs;
import java.io.*;
import java.util.*;
import java.lang.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
/*****************************************************************************
* This class is modified from FileDiff.java from incava.org.
* This is the utility class for using Diff class and print results 
* in the format similar to the Unix "diff" program.
* 
* <ul>
* <li>diff Format:
* <ul>
* <li> source lines + type + target lines
* <li> lines details
* <li> ---
* </ul>
*
* <li>type:
* <ul>
* <li> c: change
* <li> a: add
* <li> d: delete
* </ul>
* 
* <li> source indent: <
* <li> target indent: >
* </ul>
*
* <p><b>History:</b>
* <ul>
* </ul>
*
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class DiffUtil
{
	// private constructor
    private DiffUtil()
    {
    }
	// public methods
	/**
	* Get the difference String.
	*
	* @param aLines the first vector of lines to compare
	* @param bLines the second vector of lines to compare
	*
	* @return the difference string
	*/
	public static String getDiffStr(Vector<String> aLines, 
		Vector<String> bLines)
	{
		StringBuffer sb = new StringBuffer();
		// go through the difference list
        List<Difference> diffs  = (new Diff<String>(aLines, bLines)).getDiff();
        for(Difference diff : diffs) 
		{
            int delStart = diff.getDeletedStart();
            int delEnd = diff.getDeletedEnd();
            int addStart = diff.getAddedStart();
            int addEnd = diff.getAddedEnd();
			// print out line number difference with type
			String lineNumberTypeStr = getLineNumberTypeStr(
				delStart, delEnd, addStart, addEnd);
			sb.append(lineNumberTypeStr);
			sb.append(LS_STR);
			
			// print out details lines contents
			String detailLinesStr = getDetailLinesStr(aLines, bLines, 
				delStart, delEnd, addStart, addEnd);
			sb.append(detailLinesStr);
        }
		return sb.toString();
	}
	/**
	* Get the line from a file.
	*
	* @param inFile the input file
	*
	* @return a vector of lines form the input file
	*/
	public static Vector<String> getLinesFromFile(String inFile)
	{
        Vector<String> lines = new Vector<String>();
		try
        {
            BufferedReader br = new BufferedReader(new FileReader(inFile));
            String in = null;
            while((in = br.readLine()) != null)
            {
                lines.add(in);
            }
        }
        catch (Exception e)
        {
            System.err.println("** Err@DiffUtil.GetLinesFromFile( ): " 
				+ inFile + ", " + e);
            final String fMsg = e.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
				}
			});
        }
        return lines;
	}
	/**
	* Get the line from a string.
	*
	* @param inStr the input string
	*
	* @return a vector of lines form the input string
	*/
	public static Vector<String> getLinesFromStr(String inStr)
	{
        Vector<String> lines = new Vector<String>();
		try
        {
            BufferedReader br = new BufferedReader(new StringReader(inStr));
            String line = null;
            while((line = br.readLine()) != null)
            {
                lines.add(line);
            }
        }
        catch (Exception e)
        {
            System.err.println("** Err@DiffUtil.GetLinesFromFile( ): " 
                + inStr + ", " + e);
            final String fMsg = e.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
				}
			});
        }
		return lines;
	}
	// private methods
	private static String getDetailLinesStr(Vector<String> aLines, 
		Vector<String> bLines, int delStart, int delEnd,
		int addStart, int addEnd) 
	{
		StringBuffer sb = new StringBuffer();
		// not a deletion, print out the source data
		if(delEnd != Difference.NONE) 
		{
			sb.append(getLinesStr(delStart, delEnd, SOURCE_IND_STR, aLines));
			if(addEnd != Difference.NONE) 
			{
				sb.append(SEP_STR);
				sb.append(LS_STR);
			}
		}
		// not a addition, print out the target data
		if (addEnd != Difference.NONE) 
		{
			sb.append(getLinesStr(addStart, addEnd, TARGET_IND_STR, bLines));
		}
		return sb.toString();
	}
		
	private static String getLineNumberTypeStr(int delStart, int delEnd,
		int addStart, int addEnd)
	{
		// from and end line number
		String fromStr = toStartEndString(delStart, delEnd);
		String toStr = toStartEndString(addStart, addEnd);
		// a: add, d: delete, c: change
		String typeStr = toTypeStr(delEnd, addEnd); 
		StringBuffer sb = new StringBuffer();
		sb.append(fromStr);
		sb.append(typeStr);
		sb.append(toStr);
		return sb.toString();
	}
	private static String toTypeStr(int delEnd, int addEnd)
	{
    	String typeStr = new String();
		// not delete or add, then it is a change
		if((delEnd != Difference.NONE)
		&& (addEnd != Difference.NONE))
		{
			typeStr = CHANGE_STR;
		}
		else
		{
			 if(delEnd == Difference.NONE)
			 {
			 	typeStr = ADD_STR;
			 }
			 else
			 {
				 typeStr = DELETE_STR;
			 }
		}
		return typeStr;	 
	}
	
	// start & end line number
    private static String toStartEndString(int start, int end)
    {
        // adjusted, because file lines are one-indexed, not zero.
        StringBuffer sb = new StringBuffer();
        // start position: match the line numbering from diff(1):
        sb.append(end == Difference.NONE ? start : (OFFSET + start));
        
		// end position: 
        if(end != Difference.NONE && start != end) 
		{
            sb.append(COMMA_STR);
			sb.append(OFFSET + end);
        }
        return sb.toString();
    }
	// print out the diffrence from orginal data
    private static String getLinesStr(int start, int end, String ind, 
		Vector<String> lines)
    {
        StringBuffer sb = new StringBuffer();
		
        for(int i = start; i <= end; ++i) 
		{
			sb.append(ind);
			sb.append(SPACE_STR);
			sb.append(lines.elementAt(i));
			sb.append(LS_STR);
        }
		return sb.toString();
    }
	// data member
	/** line separator string */
	protected static final String LS_STR
		= System.getProperty("line.separator").toString();
	/** space string */
	protected static final String SPACE_STR = " ";
	/** comma string */
	protected static final String COMMA_STR = ",";
	/** addition string */
	protected static final String ADD_STR = "a";
	/** change string */
	protected static final String CHANGE_STR = "c";
	/** deletion string */
	protected static final String DELETE_STR = "d";
	/** separate string */
	protected static final String SEP_STR = "---";
	/** source indent string */
	protected static final String SOURCE_IND_STR = "<";
	/** target indent string */
	protected static final String TARGET_IND_STR = ">";
	private static final int OFFSET = 1;	// line number offset
}
