package gov.nih.nlm.nls.vtt.operations;
import java.awt.*;

import javax.swing.text.*;

import java.util.*;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This class provides methods for Undo/redo related operations.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class UndoOperations
{
	// private constructor
	private UndoOperations()
	{
	}
	/**
	* Undo operations for adding, changing, deleting, and joining markups.
    *
    * @param vttObj  the vttObj Java object
    */
	public static void undo(VttObj vttObj)
	{
		if(vttObj.getUndoManager().isUndoable() == true)
		{
			// get undo from undos_
			int undoIndex = vttObj.getUndoManager().getIndex();
			UndoNode undoNode = vttObj.getUndoManager().getUndoNode(undoIndex);
			vttObj.getUndoManager().reduceIndex();	// reduce index
			// update markups on the undoNode
			updateMarkupsFromUndoNode(undoNode, vttObj);
			// for debug purpose, update MarkupsDialog & MarkupDialog
			MarkupOperations.updateModelessDialog(vttObj);
		}
		else	// beep when is not undoable
		{
			Toolkit.getDefaultToolkit().beep();
		}
		// update undos list in the MarkupsDialog & MarkupDialog
		vttObj.getMarkupsDialog().updateGui(vttObj);
		vttObj.getMarkupDialog().updateGui(vttObj);
	}
	/**
	* Redo operations for adding, changing, deleting, and joining markups.
    *
    * @param vttObj  the vttObj Java object
    */
	public static void redo(VttObj vttObj)
	{
		if(vttObj.getUndoManager().isRedoable() == true)
		{
			// get redo
			int redoIndex = vttObj.getUndoManager().getIndex() + 1;
			UndoNode redoNode = vttObj.getUndoManager().getUndoNode(redoIndex);
			vttObj.getUndoManager().increaseIndex();	// increase index
			// update markups on the redoNode
			updateMarkupsFromRedoNode(redoNode, vttObj);
			// synchronize caret to cur
			/**
			Markup cur = redo.GetLastUndoBase().GetCur();
			SelectOperations.SynchronizCaret(cur, vttObj);
			**/
			// for debug purpose, update MarkupsDialog & MarkupDialog
			MarkupOperations.updateModelessDialog(vttObj);
		}
		else	// beep when is not redoable
		{
			Toolkit.getDefaultToolkit().beep();
		}
		// update undos list in the MarkupsDialog
		vttObj.getMarkupsDialog().updateGui(vttObj);
	}
	// private methods
	private static void updateMarkupsFromUndoNode(UndoNode undoNode, 
		VttObj vttObj)
	{
		Vector<UndoBase> undoBases = undoNode.getUndoBases();
		for(int i = undoBases.size()-1; i >= 0; i--)
		{
			UndoBase undoBase = undoBases.elementAt(i);
			updateMarkupsFromUndoBase(undoBase, vttObj);
		}
	}
	private static void updateMarkupsFromUndoBase(UndoBase undoBase, 
		VttObj vttObj)
	{
		int action = undoBase.getAction();
		Markup cur = new Markup(undoBase.getCur());	// cur markup
		Markup prev = new Markup(undoBase.getPrev());	// prev markup
		// update markups by the undo
		int markupIndex = vttObj.getVttDocument().getMarkups().getIndex(cur);
		// undo the action
		switch(action)
		{
			case Markups.ADD:	// delete the markup from markups
				if(markupIndex > -1)
				{
					// remove the added markup
					vttObj.getVttDocument().getMarkups().removeMarkupAt(
						markupIndex);
					// set the select index	
					vttObj.getVttDocument().getMarkups().setSelectIndex(-1);
					// update the text/doc with style
					TextDisplayOperations.updateStyle(
						TextDisplayOperations.TEXT_MARKUPS, vttObj);
					// set the highlight text to the deleted markup
					// must call after above update the text/style
					SelectOperations.setHighlight(vttObj, cur);
				}
				else
				{
					System.out.println("** Err@UndoOperations.ADD");
				}
				break;
			case Markups.CHANGE:	// change the markup
				if(markupIndex > -1)
				{
					// change markup properties
					vttObj.getVttDocument().getMarkups().setMarkupAt(
						prev, markupIndex);
					// set the select index	
					vttObj.getVttDocument().getMarkups().findSelectIndex(prev);
					// update the text/doc with style
					TextDisplayOperations.updateStyle(
						TextDisplayOperations.TEXT_MARKUPS, vttObj);
				}
				else
				{
					System.out.println("** Err@UndoOperation.CHANGE");
				}
				break;
			case Markups.DELETE:	// add the markup
				if(markupIndex == -1)
				{
					// add the deleted markup to Markups
					vttObj.getVttDocument().getMarkups().addMarkup(prev);
					// set the select index	
					vttObj.getVttDocument().getMarkups().findSelectIndex(prev);
					// update the text/doc with style
					TextDisplayOperations.updateStyle(
						TextDisplayOperations.TEXT_MARKUPS, vttObj);
				}
				else
				{
					System.out.println("** Err@UndoOperation.DELETE");
				}
				break;
		}
	}
	private static void updateMarkupsFromRedoNode(UndoNode redoNode, 
		VttObj vttObj)
	{
		Vector<UndoBase> redoBases = redoNode.getUndoBases();
		for(int i = 0; i < redoBases.size(); i++)
		{
			UndoBase redoBase = redoBases.elementAt(i);
			updateMarkupsFromRedoBase(redoBase, vttObj);
		}
	}
	private static void updateMarkupsFromRedoBase(UndoBase redoBase, 
		VttObj vttObj)
	{
		int action = redoBase.getAction();
		Markup cur = new Markup(redoBase.getCur());
		Markup prev = new Markup(redoBase.getPrev());
		// update markups by the undo
		int markupIndex = vttObj.getVttDocument().getMarkups().getIndex(prev);
		// undo the action
		switch(action)
		{
			case Markups.DELETE:	// delete the markup from markups
				if(markupIndex > -1)
				{
					// set the index
					vttObj.getVttDocument().getMarkups().findSelectIndex(cur);
					// deelte the markup
					vttObj.getVttDocument().getMarkups().removeMarkupAt(
						markupIndex);
					// update the text/doc with style
					TextDisplayOperations.updateStyle(
						TextDisplayOperations.TEXT_MARKUPS, vttObj);
				}
				else
				{
					System.out.println("** Err@RedoOperation.DELETE");
				}
				break;
			case Markups.CHANGE:	// change the markup
				if(markupIndex > -1)	// exist
				{
					// change markup
					vttObj.getVttDocument().getMarkups().setMarkupAt(
						cur, markupIndex);
					// set the index
					vttObj.getVttDocument().getMarkups().findSelectIndex(cur);
					// update the text/doc with style
					TextDisplayOperations.updateStyle(
						TextDisplayOperations.TEXT_MARKUPS, vttObj);
				}
				else
				{
					System.out.println("** Err@RedoOperation.CHANGE");
				}
				break;
			case Markups.ADD:	// add the markup
				if(markupIndex == -1)	// not exist
				{
					// add the markup
					vttObj.getVttDocument().getMarkups().addMarkup(cur);
					// set the index
					vttObj.getVttDocument().getMarkups().findSelectIndex(cur);
					// update the text/doc with style
					TextDisplayOperations.updateStyle(
						TextDisplayOperations.TEXT_MARKUPS, vttObj);
				}
				else
				{
					System.out.println("** Err@RedoOperation.ADD");
				}
				break;
		}
	}
	// data member
}
