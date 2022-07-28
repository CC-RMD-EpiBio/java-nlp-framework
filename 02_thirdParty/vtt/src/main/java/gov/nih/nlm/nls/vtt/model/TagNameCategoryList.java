package gov.nih.nlm.nls.vtt.model;
import java.util.*;
/*****************************************************************************
* This is the Tag Name Category List Java object class. 
* It is used in the popup menu for multiple layer sub menu.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class TagNameCategoryList 
{
	// public constructor
	/**
	* Create a Tag name category list Java object with default values.
	*/
    public TagNameCategoryList()
    {
    }
	/**
	* Create a Tag name category list Java object by specifying name.
	*
	* @param name  the name of tag name category list
	*/
    public TagNameCategoryList(String name)
    {
		name_ = name;
    }
	/**
	* Create a Tag name category list Java object by specifying name and
	* category list.
	*
	* @param name  the name of tag name category list
	* @param categoryList  the category list of tag name category list
	*/
    public TagNameCategoryList(String name, Vector<String> categoryList)
    {
		name_ = name;
		categoryList_ = new Vector<String>(categoryList);
	}
	/**
	* Set name of tag name category list
	*
	* @param name  the name of tag name category list
	*/
	public void setName(String name)
	{
		name_ = name;
	}
	/**
	* Set category list
	*
	* @param categoryList  the category list
	*/
	public void setCategoryList(Vector<String> categoryList)
	{
		categoryList_ = new Vector<String>(categoryList);
	}
	/**
	* Get name of tag name category list
	*
	* @return the name of tag name category list
	*/
	public String getName()
	{
		return name_;
	}
	/**
	* Get category list
	*
	* @return the category list
	*/
	public Vector<String> getCategoryList()
	{
		return categoryList_;
	}
	/**
	* Add a category to category list
	*
	* @param category the category  to be added
	*/
	public void addCategory(String category)
	{
		categoryList_.addElement(category);
	}
	// data members
	private String name_ = new String();	// name of the tag
	// category list
	private Vector<String> categoryList_ = new Vector<String>(); 
}
