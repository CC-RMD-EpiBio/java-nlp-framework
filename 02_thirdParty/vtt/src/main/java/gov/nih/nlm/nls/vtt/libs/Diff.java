package gov.nih.nlm.nls.vtt.libs;
import java.util.*;
/*****************************************************************************
* This class implements the Longest Common Subsequences Algorithm to find 
* difference between two lists of objects.  This code is modified 
* from Diff.java from incava.org
*
* Compares two lists, returning a list of the additions, changes, and deletions
* between them. A Comparator may be passed as an argument to the constructor, 
* and will thus be used. If not provided, the initial value in the aList 
* ("from") list will be looked at to see if it supports the comparable 
* interface. If so, its equals and compareTo methods will be invoked on the 
* instances in the "from" and "to" lists; otherwise, for speed, hash codes 
* from the objects will be used instead for comparison.
*
* <p><b>History:</b>
* <ul>
* </ul>
*
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class Diff<E>
{
	// constructor
	/**
	* Constructs the Diff object by specifying two arrays and comparator.
	*
	* @param aArray the first input array for Diff
	* @param bArray the second input array for Diff
	* @param comparator the Diff comparator
	*/
    public Diff(E[] aArray, E[] bArray, Comparator<E> comparator)
    {
        this(Arrays.asList(aArray), Arrays.asList(bArray), comparator);
    }
    /**
    * Constructs the Diff object by specifying two arrays with the default
    * comparison mechanism between the objects, such as <code>equals</code> and
    * <code>compareTo</code>.
	*
	* @param aArray the first input array for Diff
	* @param bArray the second input array for Diff
    */
    public Diff(E[] aArray, E[] bArray)
    {
        this(aArray, bArray, null);
    }
    /**
    * Constructs the Diff object by specifying two lists and comparator.
	*
	* @param aList the first input list for Diff
	* @param bList the second input list for Diff
	* @param comparator the Diff comparator
    */
    public Diff(List<E> aList, List<E> bList, Comparator<E> comparator)
    {
        aList_ = aList;
        bList_ = bList;
        comparator_ = comparator;
        thresh_ = null;
    }
    /**
    * Constructs the Diff object by specifying two lists with the default
    * comparison mechanism between the objects, such as <code>equals</code> and
    * <code>compareTo</code>.
	*
	* @param aList the first input list for Diff
	* @param bList the second input list for Diff
    */
    public Diff(List<E> aList, List<E> bList)
    {
        this(aList, bList, null);
    }
	// public methods
	/**
    * Runs diff and get the results.
	*
	* @return a list of the result, Difference objects
	*/
    public List<Difference> getDiff()
    {
        traverseSequences();
        // add the last difference, if pending:
        if(pending_ != null) 
		{
            diffs_.add(pending_);
        }
        return diffs_;
    }
	// private methods
    /**
    * Traverses the sequences, seeking the longest common subsequences,
    * invoking the methods <code>finishedA</code>, <code>finishedB</code>,
    * <code>onANotB</code>, and <code>onBNotA</code>.
    */
    private void traverseSequences()
    {
        Integer[] matches = getLongestCommonSubsequences();
        int lastA = aList_.size() - 1;
        int lastB = bList_.size() - 1;
        int bi = 0;
        int ai;
        int lastMatch = matches.length - 1;
        
        for (ai = 0; ai <= lastMatch; ++ai) 
		{
            Integer bLine = matches[ai];
            if (bLine == null) 
			{
                onANotB(ai, bi);
            }
            else 
			{
                while (bi < bLine) 
				{
                    onBNotA(ai, bi++);
                }
                onMatch(ai, bi++);
            }
        }
        boolean calledFinishA = false;
        boolean calledFinishB = false;
        while (ai <= lastA || bi <= lastB) 
		{
            // Is last A?
            if (ai == lastA + 1 && bi <= lastB) {
                if (!calledFinishA && callFinishedA()) 
				{
                    finishedA(lastA);
                    calledFinishA = true;
                }
                else 
				{
                    while (bi <= lastB) 
					{
                        onBNotA(ai, bi++);
                    }
                }
            }
            // is last B?
            if (bi == lastB + 1 && ai <= lastA) 
			{
                if (!calledFinishB && callFinishedB()) 
				{
                    finishedB(lastB);
                    calledFinishB = true;
                }
                else 
				{
                    while (ai <= lastA) 
					{
                        onANotB(ai++, bi);
                    }
                }
            }
            if (ai <= lastA) 
			{
                onANotB(ai++, bi);
            }
            if (bi <= lastB) 
			{
                onBNotA(ai, bi++);
            }
        }
    }
    /**
    * Override and return true in order to have <code>finishedA</code> invoked
    * at the last element in the <code>a</code> array.
    */
    private boolean callFinishedA()
    {
        return false;
    }
    /**
    * Override and return true in order to have <code>finishedB</code> invoked
    * at the last element in the <code>b</code> array.
    */
    private boolean callFinishedB()
    {
        return false;
    }
    /**
    * Invoked at the last element in <code>a</code>, if
    * <code>callFinishedA</code> returns true.
    */
    private void finishedA(int lastA)
    {
    }
    /**
    * Invoked at the last element in <code>b</code>, if
    * <code>callFinishedB</code> returns true.
    */
    private void finishedB(int lastB)
    {
    }
    /**
    * Invoked for elements in <code>a</code> and not in <code>b</code>.
    */
    private void onANotB(int ai, int bi)
    {
        if (pending_ == null) 
		{
            pending_ = new Difference(ai, ai, bi, -1);
        }
        else 
		{
            pending_.setDeleted(ai);
        }
    }
    /**
    * Invoked for elements in <code>b</code> and not in <code>a</code>.
    */
    private void onBNotA(int ai, int bi)
    {
        if (pending_ == null) 
		{
            pending_ = new Difference(ai, -1, bi, bi);
        }
        else 
		{
            pending_.setAdded(bi);
        }
    }
    /**
    * Invoked for elements matching in <code>a</code> and <code>b</code>.
    */
    private void onMatch(int ai, int bi)
    {
        if (pending_ == null) 
		{
            // no current pending
        }
        else 
		{
            diffs_.add(pending_);
            pending_ = null;
        }
    }
    /**
    * Compares the two objects, using the comparator provided with the
    * constructor, if any.
    */
    private boolean equals(E x, E y)
    {
		boolean flag = 
		(comparator_ == null ? x.equals(y) : comparator_.compare(x, y) == 0);
		return flag;
    }
    
    /**
    * Returns an array of the longest common subsequences.
    */
    private Integer[] getLongestCommonSubsequences()
    {
        int aStart = 0;
        int aEnd = aList_.size() - 1;
        int bStart = 0;
        int bEnd = bList_.size() - 1;
        TreeMap<Integer, Integer> matches = new TreeMap<Integer, Integer>();
        while((aStart <= aEnd) 
		&& (bStart <= bEnd)
		&& (equals(aList_.get(aStart), bList_.get(bStart)))) 
		{
            matches.put(aStart++, bStart++);
        }
        while((aStart <= aEnd) 
		&& (bStart <= bEnd) 
		&& (equals(aList_.get(aEnd), bList_.get(bEnd)))) 
		{
            matches.put(aEnd--, bEnd--);
        }
        Map<E, List<Integer>> bMatches = null;
        if(comparator_ == null) 
		{
            if (aList_.size() > 0 && aList_.get(0) instanceof Comparable) 
			{
                // this uses the Comparable interface
                bMatches = new TreeMap<E, List<Integer>>();
            }
            else 
			{
                // this just uses hashCode()
                bMatches = new HashMap<E, List<Integer>>();
            }
        }
        else 
		{
            // we don't really want them sorted, but this is the only Map
            // implementation (as of JDK 1.4) that takes a comparator.
            bMatches = new TreeMap<E, List<Integer>>(comparator_);
        }
        for (int bi = bStart; bi <= bEnd; ++bi) 
		{
            E element = bList_.get(bi);
            E key = element;
            List<Integer> positions = bMatches.get(key);
            
            if (positions == null) 
			{
                positions = new ArrayList<Integer>();
                bMatches.put(key, positions);
            }
            
            positions.add(bi);
        }
        thresh_ = new TreeMap<Integer, Integer>();
        Map<Integer, Object[]> links = new HashMap<Integer, Object[]>();
        for (int i = aStart; i <= aEnd; ++i) 
		{
            E aElement  = aList_.get(i);
            List<Integer> positions = bMatches.get(aElement);
            if (positions != null) 
			{
                Integer k = 0;
                ListIterator<Integer> pit 
					= positions.listIterator(positions.size());
                while(pit.hasPrevious()) 
				{
                    Integer j = pit.previous();
                    k = insert(j, k);
                    if (k == null) 
					{
                        // nothing
                    }
                    else 
					{
                        Object value = k > 0 ? links.get(k - 1) : null;
                        links.put(k, new Object[] { value, i, j });
                    }   
                }
            }
        }
        if(thresh_.size() > 0) 
		{
            Integer ti = thresh_.lastKey();
            Object[] link = links.get(ti);
            while (link != null) 
			{
                Integer x = (Integer)link[1];
                Integer y = (Integer)link[2];
                matches.put(x, y);
                link = (Object[])link[0];
            }
        }
        int size = (matches.size() == 0 ? 0 : 1 + matches.lastKey());
        Integer[] ary  = new Integer[size];
        for (Integer idx : matches.keySet()) 
		{
            Integer val = matches.get(idx);
            ary[idx] = val;
        }
        return ary;
    }
    /**
    * Returns whether the integer is not zero (including if it is not null).
    */
    private static boolean isNonZero(Integer i)
    {
        return i != null && i != 0;
    }
    /**
    * Returns whether the value in the map for the given index is greater than
    * the given value.
    */
    private boolean isGreaterThan(Integer index, Integer val)
    {
        Integer lhs = thresh_.get(index);
        return lhs != null && val != null && lhs.compareTo(val) > 0;
    }
    /**
    * Returns whether the value in the map for the given index is less than
    * the given value.
    */
    private boolean isLessThan(Integer index, Integer val)
    {
        Integer lhs = thresh_.get(index);
        return lhs != null && (val == null || lhs.compareTo(val) < 0);
    }
    /**
    * Returns the value for the greatest key in the map.
    */
    private Integer getLastValue()
    {
        return thresh_.get(thresh_.lastKey());
    }
    /**
    * Adds the given value to the "end" of the threshold map, that is, with the
    * greatest index/key.
    */
    private void append(Integer value)
    {
        Integer addIdx = null;
        if(thresh_.size() == 0) 
		{
            addIdx = 0;
        }
        else
		{
            Integer lastKey = thresh_.lastKey();
            addIdx = lastKey + 1;
        }
        thresh_.put(addIdx, value);
    }
    /**
    * Inserts the given values into the threshold map.
    */
    private Integer insert(Integer j, Integer k)
    {
        if (isNonZero(k) && isGreaterThan(k, j) && isLessThan(k - 1, j)) 
		{
            thresh_.put(k, j);
        }
        else 
		{
            int high = -1;
            if (isNonZero(k)) 
			{
                high = k;
            }
            else if (thresh_.size() > 0) 
			{
                high = thresh_.lastKey();
            }
            // off the end?
            if (high == -1 || j.compareTo(getLastValue()) > 0) 
			{
                append(j);
                k = high + 1;
            }
            else 
			{
                // binary search for insertion point:
                int low = 0;
        
                while (low <= high) 
				{
                    int index = (high + low) / 2;
                    Integer val   = thresh_.get(index);
                    int cmp = j.compareTo(val);
                    if (cmp == 0) 
					{
                        return null;
                    }
                    else if (cmp > 0) 
					{
                        low = index + 1;
                    }
                    else 
					{
                        high = index - 1;
                    }
                }
        
                thresh_.put(low, j);
                k = low;
            }
        }
        return k;
    }
	// data members
	/** The source list, AKA the "from" values */
    protected List<E> aList_; 
	/** The target list, AKA the "to" values */
    protected List<E> bList_; 
	/** The list of differences */
    protected List<Difference> diffs_ = new ArrayList<Difference>();
    private Difference pending_; // The pending, uncommitted difference.
    private Comparator<E> comparator_; // The comparator used, if any.
    private TreeMap<Integer, Integer> thresh_; // The thresholds.
}
