package gov.nih.nlm.nls.vtt.model;
import java.util.*;
/*****************************************************************************
* This class provides methods to compare Tag objects.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class TagComparator<T> implements Comparator<T>
{
	// public methods
	/**
    * This method compares two Tag objects by the name|category.
    *
    * @param o1  the first Tag object to be compared
    * @param o2  the second Tag object to be compared
    *
    * @return  a negative integer, zero, or a positive integer as the
    * first argument is less than, equal to, or greater than the second.
    */
	public int compare(T o1, T o2)
	{
		String nameCategory1 = ((Tag) o1).getNameCategory();
		String nameCategory2 = ((Tag) o2).getNameCategory();
		int value = nameCategory1.compareTo(nameCategory2);
		// check "Text/Clear" Tag
		if(nameCategory1.equals(VttDocument.RESERVED_TAG_STR) == true)
		{
			value = -1;
		}
		else if(nameCategory2.equals(VttDocument.RESERVED_TAG_STR) == true)
		{
			value = 1;
		}
		return value;
	}
}
