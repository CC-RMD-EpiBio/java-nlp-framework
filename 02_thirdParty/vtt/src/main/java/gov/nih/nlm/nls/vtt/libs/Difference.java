package gov.nih.nlm.nls.vtt.libs;
/*****************************************************************************
* This class is modified from Difference.java from incava.org. 
* It represents a difference, as used in Diff.java. A difference consists
* of two pairs of starting and ending points, each pair representing either the
* "from" or the "to" collection passed to Diff class. If an ending point
* is -1, then the difference was either a deletion or an addition. For example,
* if GetDeletedEnd() returns -1, then the difference represents an addition;
* if GetAddEnd() returns -1, then the difference represents an deletion.
*
* <p><b>History:</b>
* <ul>
* </ul>
*
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class Difference
{
	/**
    * public constructor of Difference by specifying deletion start and end,
	* addition start and end.
    */
    public Difference(int delStart, int delEnd, int addStart, int addEnd)
    {
        delStart_ = delStart;
        delEnd_ = delEnd;
        addStart_ = addStart;
        addEnd_ = addEnd;
    }
	// public methods
	/**
	* Get the deletion start
	*
	* @return the deletion start line number
	*/
    public int getDeletedStart()
    {
        return delStart_;
    }
	/**
	* Get the deletion end
	*
	* @return the deletion end line number
	*/
    public int getDeletedEnd()
    {
        return delEnd_;
    }
	/**
	* Get the addition start
	*
	* @return the addition start line number
	*/
    public int getAddedStart()
    {
        return addStart_;
    }
	/**
	* Get the addition end
	*
	* @return the addition end line number
	*/
    public int getAddedEnd()
    {
        return addEnd_;
    }
	/**
	* Set the deletion line.
	* Use this line number as deletion start if it is less than start,
	* use this line number as deletion end if it is greater than end.
	*
	* @param line the deletion line number
	*/
    public void setDeleted(int line)
    {
        delStart_ = Math.min(line, delStart_);
        delEnd_ = Math.max(line, delEnd_);
    }
	/**
	* Set the Added line.
	* Use this line number as addition start if it is less than start,
	* use this line number as addition end if it is greater than end.
	*
	* @param line the deletion line number
	*/
    public void setAdded(int line)
    {
        addStart_ = Math.min(line, addStart_);
        addEnd_ = Math.max(line, addEnd_);
    }
    /**
    * Compares this object to the other for equality. Both objects must be of
    * type Difference, with the same starting and ending points.
	*
	* @param obj the Difference object to compare to
	*
	* @return a boolean flag, true if it is equal, false if not
    */
    public boolean equals(Object obj)
    {
        if(obj instanceof Difference) 
		{
            Difference other = (Difference) obj;
			
            boolean flag = ((delStart_ == other.delStart_) 
				&& (delEnd_   == other.delEnd_) 
				&& (addStart_ == other.addStart_) 
				&& (addEnd_   == other.addEnd_));
			return flag;	
        }
        else 
		{
            return false;
        }
    }
    /**
	* Returns a string representation of this Difference.
	*
	* @return a string representation of this Difference object
	*/
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("del: [" + delStart_ + ", " + delEnd_ + "]");
        buf.append("; ");
        buf.append("add: [" + addStart_ + ", " + addEnd_ + "]");
        return buf.toString();
    }
	// data members
	/** definition for none */
    public static final int NONE = -1;
    
    private int delStart_ = NONE;	 // deletion starts.
    private int delEnd_ = NONE;		 // deletion ends.
    private int addStart_ = NONE;	 // addition starts.
    private int addEnd_ = NONE;		 // addition ends.
}
