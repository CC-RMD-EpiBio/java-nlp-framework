package gov.nih.nlm.nls.vtt.libs;
import java.io.*;
import java.util.*;
import java.text.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
/*****************************************************************************
* This code is to calculate the stats for the difference.
*
* <p><b>History:</b>
* <ul>
* </ul>
*
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class DiffStats
{
	// public constructors
	/**
	* The constructor of DiffStats by using default values.
	*/
    public DiffStats()
    {
        addNum_ = 0;
        changeNum_ = 0;
        deleteNum_ = 0;
		diffNum_ = 0;
		lineNum_ = 0;
		srcChangeNum_ = 0;
    }
	/**
	* The constructor of DiffStats by specifying addition number, 
	* changing number, deletion number, difference number, source change number,
	* source change lines, source deletion lines, target change lines,
	* target addition lines.
	*/
    public DiffStats(int addNum, int changeNum, int deleteNum, int diffNum,
		int srcChangeNum, Vector<Integer> srcChangeLines, 
		Vector<Integer> srcDeleteLines, Vector<Integer> tarChangeLines, 
		Vector<Integer> tarAddLines)
    {
		this(addNum, changeNum, deleteNum, diffNum, srcChangeNum, 0,
			srcChangeLines, srcDeleteLines, tarChangeLines, tarAddLines);
    }
	/**
	* The constructor of DiffStats by specifying addition number, 
	* changing number, deletion number, difference number, source change number,
	* line number, source change lines, source deletion lines, 
	* target change lines, target addition lines.
	*/
    public DiffStats(int addNum, int changeNum, int deleteNum, int diffNum,
		int srcChangeNum, int lineNum, Vector<Integer> srcChangeLines,
		Vector<Integer> srcDeleteLines, Vector<Integer> tarChangeLines,
		Vector<Integer> tarAddLines)
    {
        addNum_ = addNum;
        changeNum_ = changeNum;
        deleteNum_ = deleteNum;
		diffNum_ = diffNum;
		lineNum_ = lineNum;
		srcChangeNum_ = srcChangeNum;
		srcChangeLines_ = new Vector<Integer>(srcChangeLines);
		srcDeleteLines_ = new Vector<Integer>(srcDeleteLines);
		tarChangeLines_ = new Vector<Integer>(tarChangeLines);
		tarAddLines_ = new Vector<Integer>(tarAddLines);
    }
	// public methods
	/**
	* Get the addition number
	*
	* @return addition number
	*/
	public int getAddNum()
	{
		return addNum_;
	}
	/**
	* Get the addition number
	*
	* @return addition number
	*/
	public int getChangeNum()
	{
		return changeNum_;
	}
	/**
	* Get the deletion number
	*
	* @return deletion number
	*/
	public int getDeleteNum()
	{
		return deleteNum_;
	}
	/**
	* Get the difference number
	*
	* @return difference number
	*/
	public int getDiffNum()
	{
		return diffNum_;
	}
	/**
	* Get the line number
	*
	* @return line number
	*/
	public int getLineNum()
	{
		return lineNum_;
	}
	/**
	* Set the line number
	*
	* @param lineNum line number
	*/
	public void setLineNum(int lineNum)
	{
		lineNum_ = lineNum;
	}
	/**
	* Get the source change number
	*
	* @return source change number
	*/
	public int getSrcChangeNum()
	{
		return srcChangeNum_;
	}
	/**
	* Get the source change lines
	*
	* @return a vector of source change lines
	*/
	public Vector<Integer> getSrcChangeLines()
	{
		return srcChangeLines_;
	}
	/**
	* Get the source delete lines
	*
	* @return a vector of source delete lines
	*/
	public Vector<Integer> getSrcDeleteLines()
	{
		return srcDeleteLines_;
	}
	/**
	* Get the target change lines
	*
	* @return a vector of target change lines
	*/
	public Vector<Integer> getTarChangeLines()
	{
		return tarChangeLines_;
	}
	/**
	* Get the target addition lines
	*
	* @return a vector of target addition lines
	*/
	public Vector<Integer> getTarAddLines()
	{
		return tarAddLines_;
	}
	/**
	* get the detail of source difference from diffStr.
	*
	* @param diffStr  the difference in string presentation
	*
	* @return a DiffStats object of the difference string 
	*/
    public static DiffStats getDiffStats(String diffStr)
    {
        int lineNo = 0;
		DiffStats diffStats = new DiffStats();
        int changeNum = 0;
        int addNum = 0;
        int deleteNum = 0;
		int diffNum = 0;
		int srcChangeNum = 0;
		Vector<Integer> srcChangeLines = new Vector<Integer>();	// change lines
		Vector<Integer> srcDeleteLines = new Vector<Integer>();	// delete lines 
		Vector<Integer> tarChangeLines = new Vector<Integer>();	// change lines
		Vector<Integer> tarAddLines = new Vector<Integer>();	// tar add lines
        String line = null;
        // calculate the total line number of change, add, delete
        try
        {
            BufferedReader br = new BufferedReader(new StringReader(diffStr));
            while((line = br.readLine()) != null)
            {
                lineNo++;
                // only look for the line for the number stats
                if((line.startsWith(DiffUtil.SOURCE_IND_STR) == false)
                && (line.startsWith(DiffUtil.TARGET_IND_STR) == false)
                && (line.startsWith(DiffUtil.SEP_STR) == false))
                {
                    // calculate the number of change, add, delete
                    if(line.indexOf(DiffUtil.ADD_STR) > -1)
                    {
                        // count the target
                        addNum += getTarLineNum(line, DiffUtil.ADD_STR,
							tarAddLines);
                    }
                    else if(line.indexOf(DiffUtil.CHANGE_STR) > -1)
                    {
						// use the bigger number of the change
						int srcNum = getSrcLineNum(line, DiffUtil.CHANGE_STR,
							srcChangeLines);
						int tarNum = getTarLineNum(line, DiffUtil.CHANGE_STR,
							tarChangeLines);
                        changeNum += StrictMath.max(srcNum, tarNum);
						srcChangeNum += srcNum; 
                    }
                    else if(line.indexOf(DiffUtil.DELETE_STR) > -1)
                    {
                        deleteNum += getSrcLineNum(line, DiffUtil.DELETE_STR,
							srcDeleteLines);
                    }
                    else
                    {
                        System.err.println("** Err@DiffStats.getDiffStats():"
                            + line + ", at line " + lineNo);
                    }
                }
            }
			diffNum = changeNum + addNum + deleteNum;
			diffStats = new DiffStats(addNum, changeNum, deleteNum, diffNum,
				srcChangeNum, srcChangeLines, srcDeleteLines, tarChangeLines,
				tarAddLines);
        }
        catch (Exception e)
        {
            System.err.println("** Err@DiffStats.getDiffStats( ): "
                + line + " (" + lineNo + "), " + e);
            final String fMsg = e.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
				}
			});
        }
		return diffStats;
    }
	/**
	* The string representation of difference stats with default name, "lines".
	*
	* @return the string representation of difference stats
	*/
	public String toString()
	{
		return toString("lines");
	}
	/**
	* The string representation of difference stats with specified name.
	*
	* @param name the name show on the difference string
	* 
	* @return the string representation of difference stats
	*/
	public String toString(String name)
	{
		// print out the string
        StringBuffer sb = new StringBuffer();
        sb.append("- Number of added " + name + ": " + addNum_);
        sb.append(DiffUtil.LS_STR);
        sb.append("- Number of changed " + name + ": " + changeNum_);
        sb.append(DiffUtil.LS_STR);
        sb.append("- Number of deleted " + name + ": " + deleteNum_);
        sb.append(DiffUtil.LS_STR);
        sb.append("- Number of total different " + name + ": " + diffNum_);
        sb.append(DiffUtil.LS_STR);
		// identical number = src line num - src change num - delete num
		int identicalNum = lineNum_ - srcChangeNum_ - deleteNum_;
        sb.append("- Number of total identical " + name + ": " + identicalNum
			+ " (= " + lineNum_ + " - " + srcChangeNum_ + " - " + deleteNum_
			+ ")");
        sb.append(DiffUtil.LS_STR);
		if(lineNum_ != 0)
		{
			// identical percent = identical / (src line + addNum)
			double perc = (100.0*(identicalNum)/(lineNum_ + addNum_));
			DecimalFormat df = new DecimalFormat("0.00");
			sb.append("- Identical percentage: " + df.format(perc) + "%");
			sb.append(DiffUtil.LS_STR);
		}
        return sb.toString();
	}
	// private methods
    private static int getSrcLineNum(String line, String typeStr, 
		Vector<Integer> lines)
    {
        int index = line.indexOf(typeStr);
        String sourceStr = line.substring(0, index);
        int lineNum = getLineNum(sourceStr, lines);
        return lineNum;
    }
    private static int getTarLineNum(String line, String typeStr, 
		Vector<Integer> lines)
    {
        int index = line.indexOf(typeStr);
        String targetStr = line.substring(index+1);
        int lineNum = getLineNum(targetStr, lines);
        return lineNum;
    }
    // get the line number from source or taget string
    private static int getLineNum(String inStr, Vector<Integer> lines)
    {
        int lineNum = 1;   // default is 1 for no comma str
        int index = inStr.indexOf(DiffUtil.COMMA_STR);
        if(index > -1)
        {
            int start = Integer.parseInt(inStr.substring(0, index));
            int end = Integer.parseInt(inStr.substring(index+1));
            lineNum = end - start + 1;
			for(int i = start; i <= end; i++)
			{
				lines.addElement(new Integer(i));
			}
        }
		else	// TBD
		{
			lines.addElement(new Integer(inStr));	// when there is only 1 num
		}
        return lineNum;
    }
	// data member
	private int addNum_ = 0;
	private int changeNum_ = 0;
	private int deleteNum_ = 0;
	private int diffNum_ = 0;
	private int lineNum_ = 0;
	private int srcChangeNum_ = 0;	// only count change from source
	private Vector<Integer> srcChangeLines_ = new Vector<Integer>();
	private Vector<Integer> srcDeleteLines_ = new Vector<Integer>();
	private Vector<Integer> tarChangeLines_ = new Vector<Integer>();
	private Vector<Integer> tarAddLines_ = new Vector<Integer>();
}
