package gov.nih.nlm.nls.vtt.guiControl;
import javax.swing.event.*;
import javax.swing.text.*;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This is the Java class controls selected text and selected markup by
* mimiking left mouse click (from listen to caret update of CaretListener).
*
* <ul>
* <li>select text (highlight): by left mouse drag
* <li>select markup: by key or left mouse click
* </ul>
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class SelectControl implements CaretListener
{
	/**
	* Create a SelectControl object for VTT selection controller by 
	* specifying vttObj.
	*
	* @param vttObj  the vttObj Java object
	*/
	public SelectControl(VttObj vttObj)
	{
		vttObj_ = vttObj;
	}
	// public methods
	/**
	* Override methods for update caret.
	* In TextPane, this method is called automatically  when caret position 
	* is updated. Caret position is updated by left mouse button or arrow keys.
	* Update selection start and end.
	*
	* @param e  is used to notify interested parties that the text caret 
	* has changed in the event source.
	*/
	public void caretUpdate(CaretEvent e) 
	{
		// check if use caret update from Java dot/mark (mouse click, not keys)
		if(vttObj_.getMainFrame().getMainPanel().getUseCaretUpdateByMouse() 
			== true)
		{
			int dot = e.getDot();	// start: caret position
			int mark = e.getMark();	// end: position of end selection
			// mimik right mouse click, if mark = dot
			int caretPos = dot;
			DefaultStyledDocument doc = (DefaultStyledDocument) 
				vttObj_.getMainFrame().getMainPanel().getStyledDocument();
			// avoid use caretPos = 0 or doc.getLength() when reload the text
			if(mark == dot)	// select a markup by mouse right button
			{
				// not the begin or end of the doc
				if((caretPos != 0)	// begin of the doc
				&& (caretPos < doc.getLength()))	// end of the doc
				{
					// update select index of markup
					MarkupOperations.setSelectMarkup(caretPos, vttObj_);
				}
				else // pos at begin or end, no operation on select index markup
					// lock exception happens because at the end of loading 
					// attibute, the caret change and can't not change 
					// the attribute again.
					// This cause no select markup by mouse on begin or end 
					//of the document
				{
					// update highlight text position
					vttObj_.setHighlightStart(caretPos);
					vttObj_.setHighlightEnd(caretPos);
				}
			}
			else if(mark != dot)	// update highlight range
			{
				// reset select Index of markup
				MarkupOperations.resetSelectMarkup(vttObj_);
				// update highlight text position
				vttObj_.setHighlightStart(dot);
				vttObj_.setHighlightEnd(mark);
				if(mark < dot)
				{
					vttObj_.setHighlightStart(mark);
					vttObj_.setHighlightEnd(dot);
				}
			}
		}
		// reset control to mouse
		vttObj_.getMainFrame().getMainPanel().setUseCaretUpdateByMouse(true);
		// update status
		if(vttObj_.getHighlightStart() == vttObj_.getHighlightEnd())
		{
			vttObj_.getMainFrame().getStatusPanel().setText(
				"Markup overlap: [" 
				+ vttObj_.getOverlap()
				+ "], No Highlight: [" 
				+ vttObj_.getHighlightStart() + " - "
				+ vttObj_.getHighlightEnd() 
				+ "], Select Markup Index: ["
				+ vttObj_.getVttDocument().getMarkups().getSelectIndex() 
				+ "], Caret: "
				+ vttObj_.getMainFrame().getMainPanel().getTextPane().getCaretPosition());
		}
		else
		{
			vttObj_.getMainFrame().getStatusPanel().setText(
				"Markup overlap: [" 
				+ vttObj_.getOverlap()
				+ "], Current Highlight: [" 
				+ vttObj_.getHighlightStart() + " - "
				+ vttObj_.getHighlightEnd() 
				+ "], Select Markup Index: ["
				+ vttObj_.getVttDocument().getMarkups().getSelectIndex() 
				+ "], Caret: "
				+ vttObj_.getMainFrame().getMainPanel().getTextPane().getCaretPosition());
		}
	}
	// data member
	private VttObj vttObj_ = null;
}
