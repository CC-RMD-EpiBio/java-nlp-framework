package gov.nih.nlm.nls.vtt.model;
import java.util.*;
/*****************************************************************************
* This class provides methods to compare Markup object.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class MarkupComparator<T> implements Comparator<T>
{
	// public methods
	/**
    * This method compares two Markup objects by the order of
    * offset, and then length (smaller last).
    *
    * @param o1  the first Markup object to be compared
    * @param o2  the second Markup object to be compared
    *
    * @return  a negative integer, zero, or a positive integer as the
    * first argument is less than, equal to, or greater than the second.
    */
	public int compare(T o1, T o2)
	{
		int value = 0;
		int offset1 = ((Markup) o1).getOffset();
		int offset2 = ((Markup) o2).getOffset();
		// compare offset: smaller go first
		if(offset1 != offset2)
		{
			
			value = offset1 - offset2;
		}
		else	// compare length if offset is the same, bigger go first
		{
			int length1 = ((Markup) o1).getLength();
			int length2 = ((Markup) o2).getLength();
			// 0 go first, then bigger one
			if(length2 == 0)
			{
				value = 1;	// 0 go first: for NLP purpose
			}
			else if(length1 == 0)
			{
				value = -1;	// 0 go first: for NLP purpose
			}
			else
			{
				value = length2 - length1;
			}
		}
		return value;
	}
}
