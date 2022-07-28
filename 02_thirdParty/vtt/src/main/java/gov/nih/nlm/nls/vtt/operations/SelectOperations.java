package gov.nih.nlm.nls.vtt.operations;
import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This class provides methods for GUI selected related operations.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class SelectOperations
{
	// private constructor
	private SelectOperations()
	{
	}
	// public static methods
	/**
	* Synchronize caret position to the specified markup.
    *
    * @param markup  the markup used for the updated caret position
    * @param vttObj  the vttObj Java object
    */
	public static void synchronizCaret(Markup markup, VttObj vttObj)
	{
		int endPos = markup.getOffset() + markup.getLength();
		// set caret to the end of selected markup
		vttObj.getMainFrame().getMainPanel().setCaretPosition(endPos);
		// reset caret by mouse
		vttObj.getMainFrame().getMainPanel().setUseCaretUpdateByMouse(true);
	}
	/**
	* Reset high light.
    *
    * @param vttObj  the vttObj Java object
    */
	public static void resetHighlight(VttObj vttObj)
	{
		// reset selection to current inserting position (end of selection)
		// so the style change will take effect right away
		int caret = vttObj.getMainFrame().getMainPanel().getCaretPosition();
		vttObj.getMainFrame().getMainPanel().getTextPane().setSelectionStart(
			caret);
		vttObj.getMainFrame().getMainPanel().getTextPane().setSelectionEnd(
			caret);
	}
	/**
    * Set high light by the specified markup.
    *
    * @param vttObj  the vttObj Java object
    * @param markup  the markup used for the updated caret position
    */
	public static void setHighlight(VttObj vttObj, Markup markup)
	{
		if(markup != null)
		{
			int start = markup.getOffset();
			int end = start + markup.getLength();
			setHighlight(vttObj, start, end);
		}
	}
	/**
    * Set high light by the specified start and end position.
    *
    * @param vttObj  the vttObj Java object
    * @param start  the starting position of highlight
    * @param end  the ending position of highlight
    */
	public static void setHighlight(VttObj vttObj, int start, int end)
	{
		vttObj.getMainFrame().getMainPanel().getTextPane().select(start, end);
		// update the doc with style
		TextDisplayOperations.updateStyle(TextDisplayOperations.MARKUPS, 
			vttObj);
	}
	// data member
}
