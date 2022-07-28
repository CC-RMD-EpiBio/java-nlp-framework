package gov.nih.nlm.nls.vtt.operations;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.util.*;
import java.io.*;

import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This class provides text Tag related operations.
* 
* <p><b>History:</b>
* <ul>
* <li>SCR-95, chlu, 02-04-10, one click to reload tags files
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class TagOperations
{
	// private constructor
	private TagOperations()
	{
	}
	// public method
	/**
	* Load tags from the default specified tags file.
    *
    * @param vttObj  the vttObj Java object
    */
	public static void quickTagsLoading(VttObj vttObj)
	{
		String tagsFileStr 
			= vttObj.getVttDocument().getMetaData().getTagsFile();

		// check if tags file
		if((tagsFileStr == null) || (tagsFileStr.length() == 0))
		{
			JOptionPane.showMessageDialog(null,
				"No default tags file is specified?",
				"Load a Tags file", JOptionPane.WARNING_MESSAGE);
		}
		else	// a legal tags file
		{
			// confirmation message
			if(vttObj.getVttDocument().getMetaData().getConfirmation() == true)
			{
				if(JOptionPane.showConfirmDialog(null,
					"Are you sure to load a new tags file?\n" + tagsFileStr,
					"Load a Tags file", JOptionPane.OK_CANCEL_OPTION)
					== JOptionPane.OK_OPTION)
				{
					loadTagsFromFile(vttObj);
				}
			}
			else
			{
				loadTagsFromFile(vttObj);
			}
		}
	}

	/**
	* Set the properties of indexed tag to a new tag 
    *
    * @param vttObj  the vttObj Java object
    * @param tag  the new tag
    * @param index  the index of selected tag
    *
    * @return a boolean flag if the setting successful
    */
	public static boolean set(VttObj vttObj, Tag tag, int index)
	{
		boolean flag = true;
		// check if tag name changed
		String newNameCategory = tag.getNameCategory();
		String orgNameCategory 
		= vttObj.getVttDocument().getTags().getTagAt(index).getNameCategory();
		// not the same tag name category
		if(orgNameCategory.equals(newNameCategory) == false)
		{
			Vector<String> nameCategoryList = 
				 vttObj.getVttDocument().getTags().getNameCategoryList(false);
			// the new name category exist	 
			if(nameCategoryList.contains(newNameCategory) == true)
			{
				JOptionPane.showMessageDialog(null,
					"Tag name|category can't be changed from (" 
					+ orgNameCategory + ") to an existing name|category (" 
					+ newNameCategory + ").\n",
					"Error: Change a tag name|category", 
					JOptionPane.ERROR_MESSAGE);
				flag = false;
			}
			else	// the new name category does not exist
			{
				int selectOption = JOptionPane.showConfirmDialog(null,
					"Tag name|category has been changed from (" 
					+ orgNameCategory + ") to a new name|category (" 
					+ newNameCategory
					+ ").\n All associated tagged data will be changed.",
					"Warning: Change a tag name|category", 
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				// if user hit OK to change the name|category of tag
				if(selectOption == JOptionPane.OK_OPTION)
				{
					// Update the new edited tag in tags
					vttObj.getVttDocument().getTags().setTagAt(index, tag, 
						true);
					// update the quick key mapping
					vttObj.getVttDocument().getTags().setQuickKeyMappingAt(
						tag.getNameCategory(), 
						index-VttDocument.TAG_START_INDEX);
					
					// update markups associated with this tag
					vttObj.getVttDocument().getMarkups().updateTagNameCategoryInMarkups(orgNameCategory, newNameCategory);
					// update Makrups and Markup Dialog
					MarkupOperations.updateModelessDialog(vttObj);
					// update TagsDialog
					vttObj.getTagsDialog().updateGui(vttObj);
					// update textPane_
					TextDisplayOperations.updateStyle(TextDisplayOperations.MARKUPS,
						vttObj);
				}
				else
				{
					flag = false;
				}
			}
		}
		else // same tag name category, set the properties
		{
			// Update the new edited tag in tags
			vttObj.getVttDocument().getTags().setTagAt(index, tag, false);
			
			// update TagsDialog
			vttObj.getTagsDialog().updateGui(vttObj);
			// update textPane_
			TextDisplayOperations.updateStyle(TextDisplayOperations.MARKUPS,
				vttObj);
		}
		return flag;
	}
	/**
	* Add a new tag.
    *
    * @param vttObj  the vttObj Java object
    * @param tag  the new tag
    *
    * @return a boolean flag if the adding operation successful
    */
	public static boolean add(VttObj vttObj, Tag tag)
	{
		String nameCategory = tag.getNameCategory();
		boolean flag = true;
        // check syntax of new nameCategory
        if((nameCategory == null)
        || (nameCategory.length() == 0))
        {
			flag = false;
            JOptionPane.showMessageDialog(null,
                "Name|Category can't be an empty String!\nPlease select name and category for the new tag",
                "Error: Tag Properties", JOptionPane.ERROR_MESSAGE);
        }
        // check duplicated name
        else if(vttObj.getVttDocument().getTags().containsNameCategory(
			nameCategory) == true)
        {
			flag = false;
            JOptionPane.showMessageDialog(null,
                "NameCategory [" + nameCategory + "] already existed. Please select another Name|Category.",
                "Error: Tag Properties", JOptionPane.ERROR_MESSAGE);
        }
        else    // legal name
        {
			// add the tag to global
            vttObj.getVttDocument().getTags().addTag(new Tag(tag));
            // update TagsDialog
			vttObj.getTagsDialog().setTagIndex(
				vttObj.getVttDocument().getTags().getTags().size() - 1
					- VttDocument.TAG_START_INDEX);
            vttObj.getTagsDialog().updateGui(vttObj);
        }
		return flag;
	}
	/**
	* Update the name list of all tags.
    *
    * @param vttObj  the vttObj Java object
    * @param nameList  the list of all tag names
    */
	public static void updateNameList(VttObj vttObj, Vector<TagFilter> nameList)
	{
		if(vttObj.getVttDocument().getTags().getNameList().size()
			== nameList.size())
		{
			vttObj.getVttDocument().getTags().setNameList(nameList);
		}
	}
	/**
	* Update the category list of all tags.
    *
    * @param vttObj  the vttObj Java object
    * @param categoryList  the list of all tag categories
    */
	public static void updateCategoryList(VttObj vttObj,
		Vector<TagFilter> categoryList)
	{
		if(vttObj.getVttDocument().getTags().getCategoryList().size()
			== categoryList.size())
		{
			vttObj.getVttDocument().getTags().setCategoryList(categoryList);
		}
	}
	private static void loadTagsFromFile(VttObj vttObj)
	{
		
		// reload global tags_
		Tags tempTags
			= new Tags(vttObj.getVttDocument().getTags());
		vttObj.getConfigObj().setTagFile(
			new File(vttObj.getVttDocument().getMetaData().getTagsFile()));
		vttObj.getVttDocument().getTags().loadTagsFromFile(
			vttObj.getConfigObj().getTagFile().getAbsolutePath());

		// update display to the new imported tags
		if(TextDisplayOperations.updateStyle(TextDisplayOperations.MARKUPS,
			vttObj) == false)
		{
			// put the original tags back
			vttObj.getVttDocument().setTags(tempTags);
			// Warning:
			JOptionPane.showMessageDialog(null,
				"Some tags are not defined in the tags file, please select a correct tags file!",
				"Load a Tags file", JOptionPane.ERROR_MESSAGE);
		}
	}
	// data member
}
