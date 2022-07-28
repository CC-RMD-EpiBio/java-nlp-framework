package gov.nih.nlm.nls.vtt.model;
import java.util.*;
/*****************************************************************************
* This is the Tag filter Java object class. 
* It is used for filtering name and category.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class TagFilter 
{
	// public constructor
	/**
	* Create a Tag Filter Java object with default values.
	*/
    public TagFilter()
    {
    }
	/**
	* Create a Tag Filter Java object by specifying name.
	*
	* @param name  the tag name is in interest
	*/
    public TagFilter(String name)
    {
		name_ = name;
    }
	/**
	* Create a Tag Filter Java object by specifying name and filter status.
	*
	* @param name  the tag name is in interest
	* @param status  the filter status
	*/
    public TagFilter(String name, boolean status)
    {
		name_ = name;
		status_ = status;
    }
	/**
	* Set the tag name for Tag Filter
	*
	* @param name  the tag name is in interest
	*/
	public void setName(String name)
	{
		name_ = name;
	}
	/**
	* Set the filter status
	*
	* @param status  the filter status
	*/
	public void setStatus(boolean status)
	{
		status_ = status;
	}
	/**
	* Get the tag name of Tag Filter
	*
	* @return tag name
	*/
	public String getName()
	{
		return name_;
	}
	/**
	* Get the filter status
	*
	* @return   boolean flag of the filter status
	*/
	public boolean getStatus()
	{
		return status_;
	}
	// static method
	/**
	* Add tag filter to list by specifying name
	*
	* @param tagFilters tag filter list
	* @param name  the tag name of tag filter to be added
	*/
	public static void addToList(Vector<TagFilter> tagFilters, String name)
	{
		if(contains(tagFilters, name) == false)
		{
			TagFilter tagFilter = new TagFilter(name);
			tagFilters.addElement(tagFilter);
		}
	}
	/**
	* Add tag filter to list by specifying tagFilter
	*
	* @param tagFilters tag filter list
	* @param tagFilter  tag filter
	*/
	public static void addToList(Vector<TagFilter> tagFilters, 
		TagFilter tagFilter)
	{
		String name = tagFilter.getName();
		if(contains(tagFilters, name) == false)
		{
			tagFilters.addElement(tagFilter);
		}
	}
	/**
	* Delete tag filter from list by specifying name
	*
	* @param tagFilters tag filter list
	* @param name  the tag name of tag filter to be added
	*/
	public static void deleteFromList(Vector<TagFilter> tagFilters, String name)
	{
		for(int i = 0; i < tagFilters.size(); i++)
		{
			TagFilter tagFilter = tagFilters.elementAt(i);
			if(tagFilter.getName().equals(name) == true)
			{
				tagFilters.removeElementAt(i);
				break;
			}
		}
	}
	/**
	* Check if tag filter list contains a tag filter by specifying name
	*
	* @param tagFilters tag filter list
	* @param name  the tag name of tag filter to be checked
	*/
	public static boolean contains(Vector<TagFilter> tagFilters, String name)
	{
		boolean flag = false;
		if((tagFilters != null) && (name != null ) && (tagFilters.size() > 0))
		{
			for(int i = 0; i < tagFilters.size(); i++)
			{
				TagFilter tagFilter = tagFilters.elementAt(i);
				if(tagFilter.getName().equals(name) == true)
				{
					flag = true;
					break;
				}
			}
		}
		return flag;
	}
	/**
	* Get a tag filter from tag filter list by specifying name
	*
	* @param tagFilters tag filter list
	* @param name  the tag name of tag filter in interest
	*/
	public static TagFilter getTagFilterByName(Vector<TagFilter> tagFilters, 
		String name)
	{
		TagFilter tagFilter = null;
		if((tagFilters != null) && (tagFilters.size() > 0))
		{
			for(int i = 0; i < tagFilters.size(); i++)
			{
				TagFilter curTagFilter = tagFilters.elementAt(i);
				if(curTagFilter.getName().equals(name) == true)
				{
					tagFilter = new TagFilter(curTagFilter.getName(),
						curTagFilter.getStatus());
					break;
				}
			}
		}
		return tagFilter;
	}
	// data members
	private String name_ = new String();	// name of the filter
	private boolean status_ = true;			// status of the fitler
}
