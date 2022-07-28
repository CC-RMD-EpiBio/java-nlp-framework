package gov.nih.nlm.nls.vtt.operations;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.util.*;

import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This class provides text markup related operations.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class MarkupOperations
{
	// private constructor
	private MarkupOperations()
	{
	}
	// public method
	public static void saveToFileOperation(JFrame owner,VttObj vttObj)
	{
		Markups markups = vttObj.getVttDocument().getMarkups();
		markups.saveMarkupsToFile("C:\\BioInformatics_WIN\\temp_tags.txt");
		
	}
	
	
	/**
	* Delete the selected markup from Markups
    *
    * @param vttObj  the vttObj Java object
    */
	public static void deleteOperation(VttObj vttObj)
	{
		// Init undo node
		UndoNode undoNode = new UndoNode(Markups.DELETE);
		// find the selected markup to be delete
		int index = vttObj.getVttDocument().getMarkups().getSelectIndex();
		// update if delete select markup successful
		if(deleteMarkup(index, vttObj, undoNode) == true)
		{
			// update undo manager
			vttObj.getUndoManager().addUndoNode(undoNode);
			// remark the style
			TextDisplayOperations.updateStyle(
				TextDisplayOperations.TEXT_MARKUPS, vttObj);
			// for debug purpose, update MarkupsDialog
			updateModelessDialog(vttObj);
		}
	}
	/**
	* Join selected markup with the next markup to a new markup
    *
    * @param vttObj  the vttObj Java object
    */
	public static void joinOperation(VttObj vttObj)
	{
		// init undo node
		UndoNode undoNode = new UndoNode(Markups.JOIN);
		if(joinMarkups(vttObj, undoNode) == true) 
		{
			// update undo manager
			vttObj.getUndoManager().addUndoNode(undoNode);
			// remark the style
			TextDisplayOperations.updateStyle(
				TextDisplayOperations.TEXT_MARKUPS, vttObj);
			// for debug purpose, update MarkupsDialog
			updateModelessDialog(vttObj);
		}
	}
	/**
	* Delete all markups operation
    *
	* @param owner  the owner of this dialog
    * @param vttObj  the vttObj Java object
    */
	public static void deleteAllOperation(JFrame owner, VttObj vttObj)
	{
		int deleteAllOption = JOptionPane.showConfirmDialog(owner,
			"Are you sure to delete all markups?",
			"Delete all markups?",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE);
		// delete the markup list, and then update the style
		if(deleteAllOption == JOptionPane.YES_OPTION)
		{
			// update undos
			Markups markups = vttObj.getVttDocument().getMarkups();
			for(int i = markups.getSize()-1; i >= 0; i--)
			{
				Markup markup = new Markup(markups.getMarkup(i));
				Markup prev = new Markup(markup);
				UndoNode undoNode = new UndoNode(Markups.DELETE, markup, prev);
				vttObj.getUndoManager().addUndoNode(undoNode);
			}
			// reset (delete all) markups in the markups
			vttObj.getVttDocument().setMarkups(new Markups());
			
			// update style
			TextDisplayOperations.updateStyle(
				TextDisplayOperations.TEXT_MARKUPS, vttObj);
			// set caret postion to top	
			vttObj.getMainFrame().getMainPanel().setCaretPosition(0);
			// for debug purpose, update MarkupsDialog
			updateModelessDialog(vttObj);
		}
	}
	/**
	* Update a selected markup operation by specifying a tag (name and category)
	* includes.
	* <ul>
	* <li>ADD: add a new markup 
	* <li>CHANGE: change the tag of a selected markup
	* <li>OVERRIDE: change the tag to the smear text
	* </ul>
    *
	* @param tagName  the name of tag
	* @param tagCategory  the category of tag
    * @param vttObj  the vttObj Java object
    */
	public static void updateOperation(String tagName, String tagCategory,
		VttObj vttObj)
	{
		// update selected Markup
		int selectIndex = vttObj.getVttDocument().getMarkups().getSelectIndex();
		// overlap mode: true
		if(vttObj.getOverlap() == true)
		{
			// add new markup by tag on the highlight/smear text
			if(selectIndex == -1)
			{
				addMarkup(tagName, tagCategory, vttObj);
				
			}
			else	// Change the tag on the selected markup
			{
				changeMarkup(tagName, tagCategory, selectIndex, vttObj);
				
			}
		}
		else	// overlap mode: off (default)
		{
			// add new markup by tag on the highlight/smear text
			if(selectIndex == -1)
			{
				overrideMarkup(tagName, tagCategory, vttObj);// add & override
				
			}
			else	// Change the tag on the selected markup
			{
				changeMarkup(tagName, tagCategory, selectIndex, vttObj);
							}
		}
		// remark the style
		TextDisplayOperations.updateStyle(vttObj, vttObj.getHighlightStart(), vttObj.getHighlightEnd(), false);
		// for debug purpose, update MarkupsDialog
		updateModelessDialog(vttObj);
	}
	/**
	* Change the selected (indexed) markup with a specified markup.
    *
    * @param cur  the current selected markup
    * @param vttObj  the vttObj Java object
    *
    * @return a boolean flag if change successfully
    */
	public static boolean changeSelectMarkupOperation(Markup cur, VttObj vttObj)
	{
		boolean flag = true;
		// get previous markup
		Markup prev = new Markup(
			vttObj.getVttDocument().getMarkups().getSelectMarkup());
		int maxTextPos = vttObj.getVttDocument().getText().length(); 	
		// if the new markup does not change
		if(prev.equals(cur) == true)
		{
			// beep
			Toolkit.getDefaultToolkit().beep();
			flag = false;
		}
		else if(VttDocument.isLegalMarkupPos(cur, maxTextPos) == false)
		{
			JOptionPane.showMessageDialog(null,
				"Error: illegal tag position from input!\n"
				+ "Max. position: " + maxTextPos + "\n",
				"Vtt Markup Error", JOptionPane.ERROR_MESSAGE);
			flag = false;
		}
		else	 // new markup
		{
			// update undos
			UndoNode undoNode = new UndoNode(Markups.CHANGE, cur, prev);	
			vttObj.getUndoManager().addUndoNode(undoNode);
			// update the selected markup
			vttObj.getVttDocument().getMarkups().setSelectMarkup(cur);
			// remark the style and text
			TextDisplayOperations.updateStyle(
				TextDisplayOperations.TEXT_MARKUPS, vttObj);
			// for debug purpose, update MarkupsDialog
			updateModelessDialog(vttObj);
		}
		return flag;
	}
	/**
	* This method clears the markup by using "Text/Clear" tag.
	* "Text/Clear" tag is used here for the clear function.
	* This method is not used, Delete is used instead. 
	* However, this method is kept for future usage.
    *
    * @param vttObj  the vttObj Java object
    */
	public static void clearOperation(VttObj vttObj)
	{
		// clear tag by using attribute of textTag
		String name = VttDocument.RESERVED_TAG_STR;
		String category = new String();
		int start = vttObj.getHighlightStart();
		int end = vttObj.getHighlightEnd();
		int length = end - start;
		if(length > 0)
		{
			DefaultStyledDocument doc = (DefaultStyledDocument) 
				vttObj.getMainFrame().getMainPanel().getStyledDocument();
			Markup markup = new Markup(start, length, name, category,
				new String());
			vttObj.getVttDocument().getMarkups().addMarkup(markup);
			// get the clear Text Tag
			Tag tag = vttObj.getVttDocument().getTextTag();
			int baseFontSize = vttObj.getConfigObj().getBaseFontSize();
			VttDocument.setTextStyleByMarkupTag(doc, markup, tag, baseFontSize,
				vttObj.getConfigObj().getZoomFactor());
			// reset highlight selection so we can see the change right away
			SelectOperations.resetHighlight(vttObj);
			// for debug purpose, update MarkupsDialog
			updateModelessDialog(vttObj);
		}
	}
	/**
	* Update Modeless dialog, Markup and Markups dialogs
    *
    * @param vttObj  the vttObj Java object
    */
	public static void updateModelessDialog(VttObj vttObj)
	{
		if(vttObj.getMarkupsDialog().isVisible() == true)
		{
			vttObj.getMarkupsDialog().updateGui(vttObj);
		}
		if(vttObj.getMarkupDialog().isVisible() == true)
		{
			vttObj.getMarkupDialog().updateGui(vttObj);
		}
	}
	/**
	* Set selected Markup by left mouse click (current position of caret)
    *
    * @param curPos  the current (caret) position
    * @param vttObj  the vttObj Java object
    */
	public static void setSelectMarkup(int curPos, VttObj vttObj)
	{
		// update the selected markup index
		int newPos = vttObj.getVttDocument().getMarkups().findSelectIndex(
			curPos);
		// remark the style
		TextDisplayOperations.updateStyle(vttObj, newPos, newPos, true);
		// synchronize caret
		vttObj.getMainFrame().getMainPanel().setCaretPosition(newPos);
		// update non-modal Markup dialog
		vttObj.getMarkupDialog().updateGui(vttObj);
	}
	/**
    * Reset the selected markup.
    *
    * @param vttObj  the vttObj Java object
    */
	public static void resetSelectMarkup(VttObj vttObj)
	{
		// get the current markup selection
		int oldSelIdx = vttObj.getVttDocument().getMarkups().getSelectIndex();
		// reset select Index
		vttObj.getVttDocument().getMarkups().setSelectIndex(-1);
		// remark the style
		TextDisplayOperations.updateStyle(vttObj, oldSelIdx, oldSelIdx, true);
	}
	/**
    * Move the select markup in forward direction.
    *
    * @param showAll  the boolean flag if show all markup
    * @param vttObj  the vttObj Java object
    */
	public static void forwardSelectMarkup(boolean showAll, VttObj vttObj)
	{
		// increase selected index of display markup
		int newPos = vttObj.getVttDocument().getMarkups().increaseSelectIndex(
			showAll, vttObj);
		// reset highlight
		vttObj.setHighlightStart(newPos);
		vttObj.setHighlightEnd(newPos);
		// remark the style
		TextDisplayOperations.updateStyle(TextDisplayOperations.MARKUPS,
			vttObj);
		// synchronize caret 
		vttObj.getMainFrame().getMainPanel().setCaretPosition(newPos);
		// update non-modal Markup dialog
		vttObj.getMarkupDialog().updateGui(vttObj);
	}
	/**
    * Move the select markup in backward direction.
    *
    * @param showAll  the boolean flag if show all markup
    * @param vttObj  the vttObj Java object
    */
	public static void backwardSelectMarkup(boolean showAll, VttObj vttObj)
	{
		// increase selected index of display markup
		int newPos = vttObj.getVttDocument().getMarkups().decreaseSelectIndex(showAll, vttObj);
		// reset highlight
		vttObj.setHighlightStart(newPos);
		vttObj.setHighlightEnd(newPos);
		// remark the style
		TextDisplayOperations.updateStyle(TextDisplayOperations.MARKUPS,
			vttObj);
		// synchronize caret
		vttObj.getMainFrame().getMainPanel().setCaretPosition(newPos);
		// update non-modal dialog
		vttObj.getMarkupDialog().updateGui(vttObj);
	}
	/**
    * Set the select markup to the beginning of displayable markups (home).
    *
    * @param showAll  the boolean flag if show all markup
    * @param vttObj  the vttObj Java object
    */
	public static void setHomeSelectMarkup(boolean showAll, VttObj vttObj)
	{
		// set selected index to home of display markup
		int newPos = vttObj.getVttDocument().getMarkups().getFirstSelectIndex(showAll, vttObj);
		// reset highlight
		vttObj.setHighlightStart(newPos);
		vttObj.setHighlightEnd(newPos);
		// remark the style
		TextDisplayOperations.updateStyle(TextDisplayOperations.MARKUPS,
			vttObj);
		// synchronize caret
		vttObj.getMainFrame().getMainPanel().setCaretPosition(newPos);
		// update non-modal dialog
		vttObj.getMarkupDialog().updateGui(vttObj);
	}
	/**
	* Set the select index to the end of displayable markups (end).
    *
    * @param showAll  the boolean flag if show all markup
    * @param vttObj  the vttObj Java object
    */
	public static void setEndSelectMarkup(boolean showAll, VttObj vttObj)
	{
		// set selected index to end of display markup
		int newPos = vttObj.getVttDocument().getMarkups().getLastSelectIndex(showAll, vttObj);
		// reset highlight
		vttObj.setHighlightStart(newPos);
		vttObj.setHighlightEnd(newPos);
		// remark the style
		TextDisplayOperations.updateStyle(TextDisplayOperations.MARKUPS,
			vttObj);
		// synchronize caret
		vttObj.getMainFrame().getMainPanel().setCaretPosition(newPos);
		// update non-modal dialog
		vttObj.getMarkupDialog().updateGui(vttObj);
	}
	// private methods
	// delete a markup for join or override
	private static boolean deleteMarkup(int index, VttObj vttObj, 
		UndoNode undoNode)
	{
		boolean flag = true;
		VttDocument vttDocument = vttObj.getVttDocument();
		// make sure if index and markups are legal
		if((vttDocument.getMarkups() != null) 
		&& (vttDocument.getMarkups().getSize() > 0)
		&& (index < vttDocument.getMarkups().getSize())
		&& (index >= 0))
		{
			// update undos
			Markup markup 
				= new Markup(vttDocument.getMarkups().getMarkup(index));
			Markup prev = new Markup(markup);	// prev markup for undo
			UndoBase undoBase = new UndoBase(Markups.DELETE, markup, prev);
			undoNode.addUndoBase(undoBase);
			// delete markup from list
			vttDocument.getMarkups().removeMarkupAt(index);
		}
		else
		{
			 Toolkit.getDefaultToolkit().beep();
			 flag = false;
		}
		return flag;
	}
	// change an existed (indexed) markup by tag
	private static void changeMarkup(String tagName, String tagCategory,
		int index, VttObj vttObj)
	{
		// get prev and cur markup
		Markup prev = new Markup(
			vttObj.getVttDocument().getMarkups().getMarkup(index));
		Markup cur = new Markup(prev);	
		// if the tag (name & category) does not changed
		if((cur.getTagName().equals(tagName) == true)
		&& (cur.getTagCategory().equals(tagCategory) == true))
		{
			// beep
			Toolkit.getDefaultToolkit().beep();
		}
		else	// change to the new tag
		{
			// update markup at index
			cur.setTagName(tagName);
			cur.setTagCategory(tagCategory);
			vttObj.getVttDocument().getMarkups().setMarkupAt(cur, index);
			// update undos
			UndoNode undoNode = new UndoNode(Markups.CHANGE, cur, prev);	
			vttObj.getUndoManager().addUndoNode(undoNode);
		}
	}
	// add markup to a selected (highlight) text
	private static void addMarkup(String tagName, String tagCategory,
		VttObj vttObj) 
	{
		// update markup
		int start = vttObj.getHighlightStart();
		int end = vttObj.getHighlightEnd();
		int length = end - start;
		// if lighlight exist, update tag
		if(length > 0)
		{
			// init undo node: ADD
			UndoNode undoNode = new UndoNode(Markups.ADD);
			// update the markups and doc
			Markup markup = new Markup(start, length, tagName, tagCategory,
				new String());
			addMarkup(markup, vttObj, undoNode);
			// update undo manager
			vttObj.getUndoManager().addUndoNode(undoNode);
		}
		else
		{
			// beep
			Toolkit.getDefaultToolkit().beep();
		}
	}
	private static void addMarkup(Markup markup, VttObj vttObj, 
		UndoNode undoNode)
	{
		vttObj.getVttDocument().getMarkups().addMarkup(markup);
		// update undos
		Markup prev = new Markup(markup);
		UndoBase undoBase = new UndoBase(Markups.ADD, markup, prev);
		undoNode.addUndoBase(undoBase);
		// update the selected index so the new added is selected
		vttObj.getVttDocument().getMarkups().findSelectIndex(markup);
	}
	private static boolean joinMarkups(VttObj vttObj, UndoNode undoNode)
	{
		boolean flag = true;
		VttDocument vttDocument = vttObj.getVttDocument();
		// save markup info
		int index = vttObj.getVttDocument().getMarkups().getSelectIndex();
		int nextIndex = index + 1;	// next markup
		// check if index & next index are legal
		if((vttDocument.getMarkups() != null) 
		&& (vttDocument.getMarkups().getSize() > 0)
		&& (index < vttDocument.getMarkups().getSize())
		&& (index >= 0)
		&& (nextIndex < vttDocument.getMarkups().getSize()))
		{
			// update new markup
			Markup curMarkup = vttDocument.getMarkups().getMarkup(index); 
			Markup nextMarkup = vttDocument.getMarkups().getMarkup(index+1); 
			Markup newMarkup = Markup.join(curMarkup, nextMarkup);	
			// delete select markup
			deleteMarkup(index, vttObj, undoNode);
			// delete next markup, please notes index is the next index here
			if(index < vttDocument.getMarkups().getSize())
			{
				deleteMarkup(index, vttObj, undoNode);
			}
			// add a new joined markup
			addMarkup(newMarkup, vttObj, undoNode);
		}
		else
		{
			flag = false;
			Toolkit.getDefaultToolkit().beep();
		}
		return flag;
	}
	// override a smear text when overlap mode is off
	private static void overrideMarkup(String tagName, String tagCategory,
		VttObj vttObj)
	{
		int start = vttObj.getHighlightStart();
		int end = vttObj.getHighlightEnd();
		int length = end - start;
		
		// if lighlight exist, update tag
		// delete all markups touch smear, then add a new Markup
		if(length > 0)
		{
			// find all overlap markups: touch the smear range
			Vector<Markup> markups 
				= vttObj.getVttDocument().getMarkups().getMarkups();
			Vector<Integer> overlapList = new Vector<Integer>();
			for(int i = 0; i < markups.size(); i++)
			{
				Markup markup = markups.elementAt(i);
				if(!markup.getTagName().equals("SnippetColumn") && markup.isOverlap(start, end) == true)
				{
					overlapList.addElement(new Integer(i));
				}
			}
			// init undo 
			UndoNode undoNode = new UndoNode(Markups.ADD);
			if(overlapList.size() > 0)
			{
				undoNode.setAction(Markups.OVERRIDE);
			}
			// delete all overlap markups
			Collections.sort(overlapList);
			for(int i = overlapList.size()-1; i >= 0; i--)
			{
				int index = overlapList.elementAt(i).intValue();
				deleteMarkup(index, vttObj, undoNode);
			}
			// override with a new markup
			Markup markup = new Markup(start, length, tagName, tagCategory,
				new String());
			addMarkup(markup, vttObj, undoNode);
			// update undo manager
			vttObj.getUndoManager().addUndoNode(undoNode);
		}
		else
		{
			// beep
			Toolkit.getDefaultToolkit().beep();
		}
	}
	// data member
}
