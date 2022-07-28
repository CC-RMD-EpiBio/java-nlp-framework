package gov.nih.nlm.nls.vtt.gui;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import gov.nih.nlm.nls.vtt.guiControl.*;
import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This class defines GUI popup menu executed by pressing right mouse button.
* The actions are performed in MainPanel.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class VttPopupMenu
{
	// public constructor
	/**
    * Create a VttPopupMenu object used in VTT with specifying vttObj.
    *
    * @param vttObj  the vttObj Java object
    */
	public VttPopupMenu(VttObj vttObj)
	{
		// init Gui Control
		vttPopupMenuControl_ = new VttPopupMenuControl(vttObj);
		undoItem_.addActionListener(vttPopupMenuControl_);
		redoItem_.addActionListener(vttPopupMenuControl_);
		// init Gui Compoment
		popupMenu_ = createVttPopupMenu(vttObj);
	}
	/**
    * Set VttPopupMenu to be visible (show).
    *
    * @param evt  Java mouse event object 
    * @param vttObj  the vttObj Java object
    */
	public void show(MouseEvent evt, VttObj vttObj) 
	{
		popupMenu_ = createVttPopupMenu(vttObj);
		popupMenu_.show(evt.getComponent(), evt.getX(), evt.getY());
	}
	// private methods
	private JPopupMenu createVttPopupMenu(VttObj vttObj)
	{
		JPopupMenu popupMenu = new JPopupMenu();
		// Update
		updateGuiFromGlobal(vttObj);
		// popup menu: tag menu items
		Vector<Object> popupItems = getTagMenuItems(vttObj);
		// delete & delete all
		popupItems.addElement("Separator"); 
		if(Tags.containsTextTag(vttObj.getVttDocument().getTags().getTags()) 
			== true)
		{
			popupItems.addElement("Delete");
		}
		popupItems.addElement("Delete All");
		popupItems.addElement("Separator"); 
		// undo & redo
		popupItems.addElement("Undo");
		popupItems.addElement("Redo");
		popupItems.addElement("Separator"); 
		// fiel related
		popupItems.addElement("Save");
		popupItems.addElement("Print");
		popupItems.addElement("Close");
		popupItems.addElement("Exit");
		// add all above items to popup menu
		addItemToMenu(popupMenu, popupItems, vttObj);
		return popupMenu;
	}
	private Vector<Object> getTagMenuItems(VttObj vttObj)
	{
		Vector<Object> tagMenuItems = new Vector<Object>(); 
		// Add tags name into popup menu item
		Vector<String> tagNameCategoryList 
			= vttObj.getVttDocument().getTags().getNameCategoryList(true);
		// skip the first, text tag
		Vector<TagNameCategoryList> nameCategoryList 
			= new Vector<TagNameCategoryList>();
		for(int i = VttDocument.TAG_START_INDEX; i < tagNameCategoryList.size();
			i++)
		{
			String tagNameCategory = tagNameCategoryList.elementAt(i);
			String name = Tag.getName(tagNameCategory);
			String category = Tag.getCategory(tagNameCategory);
			// check if the name exist in nameCategoryList
			boolean nameExistFlag = false;
			for(int j = 0; j < nameCategoryList.size(); j++)
			{
				// existing name, new category
				if(nameCategoryList.elementAt(j).getName().equals(name) == true)
				{
					nameExistFlag = true;
					nameCategoryList.elementAt(j).addCategory(category);
					break;
				}
			}
			// new name
			if(nameExistFlag == false)
			{
				Vector<String> categoryList = new Vector<String>();
				categoryList.addElement(category);
				nameCategoryList.addElement(
					new TagNameCategoryList(name, categoryList));
			}
		}
		// add tag menu (name) & sub Menu (category)
		for(int i = 0; i < nameCategoryList.size(); i++)
		{
			TagNameCategoryList temp = nameCategoryList.elementAt(i);
			String name = temp.getName();
			Vector<String> categoryList = temp.getCategoryList();
			
			// if tag name does not have any sub menu
			if((categoryList.size() == 1)
			&& (categoryList.elementAt(0).length() == 0))
			{
				tagMenuItems.addElement(name);
			}
			else
			{
				tagMenuItems.addElement(makeSubMenu(name, categoryList));
			}
		}
		return tagMenuItems;
	}
	// for sub menu, use name to store tag name, text to store tag category
	private JMenu makeSubMenu(String name, Vector<String> menuItemList)
	{
		JMenu subMenu = new JMenu(name);
		for(int i = 0; i < menuItemList.size(); i++)
		{
			// stoire tag category
			JMenuItem subMenuItem = new JMenuItem(menuItemList.elementAt(i));
			subMenuItem.addActionListener(vttPopupMenuControl_);
			subMenuItem.setName(name);	// store tag name
			subMenu.add(subMenuItem);
		}
		return subMenu;
	}
	private void updateGuiFromGlobal(VttObj vttObj)
	{
		// update undo
		undoItem_.setEnabled(vttObj.getUndoManager().isUndoable());
		// update redo
		redoItem_.setEnabled(vttObj.getUndoManager().isRedoable());
	}
	private void addItemToMenu(JPopupMenu menu, Vector<Object> items,
		VttObj vttObj)
	{
		// add menu items to menu
		for(int i = 0; i < items.size(); i++)
		{
			Object item = items.elementAt(i);
			if(item instanceof String)
			{
				String itemStr = (String) item;
				// add a separator if input is null
				if(itemStr.equals("Separator"))
				{
					menu.addSeparator();
				}
				else if(itemStr.equals("Undo"))
				{
					menu.add(undoItem_);
				}
				else if(itemStr.equals("Redo"))
				{
					menu.add(redoItem_);
				}
				else
				{
					JMenuItem menuItem = new JMenuItem(itemStr);
					menuItem.addActionListener(vttPopupMenuControl_);
					menu.add(menuItem);
				}
			}
			else if(item instanceof JMenu) // sub menu
			{
				menu.add((JMenu) item);
			}
		}
	}
	
	// data members
	private JPopupMenu popupMenu_ = null;
	private JMenuItem undoItem_ = new JMenuItem("Undo");
	private JMenuItem redoItem_ = new JMenuItem("Redo");
	private VttPopupMenuControl vttPopupMenuControl_ = null; 
}
