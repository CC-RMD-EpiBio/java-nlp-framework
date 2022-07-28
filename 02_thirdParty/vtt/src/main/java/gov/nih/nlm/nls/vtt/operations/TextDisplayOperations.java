package gov.nih.nlm.nls.vtt.operations;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.Highlighter.Highlight;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This class provides text display related operations.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class TextDisplayOperations
{
	// private constructor
	private TextDisplayOperations()
	{
	}
	// public method
	/**
	* Update style of text, highlight, & markups (display or not).
    *
    * @param vttObj  the vttObj Java object
    * @param mode  MARKUPS for update markups only; TEXT_MARKUPS for update both
	* 		text and markups
    *
    * @return a boolean flag if the update style operation successful
    */
	public static boolean updateStyle(int mode, VttObj vttObj)
	{
		return updateStyle(mode, vttObj, vttObj.getMainFrame());
	}
	/**
	* Update style of text, highlight, & markups (display or not).
    *
    * @param mode  MARKUPS for update markups only; TEXT_MARKUPS for update both
	* 		text and markups
    * @param vttObj  the vttObj Java object
    * @param mainFrame  the main frame of VTT application
    *
    * @return a boolean flag if the update style operation successful
    */
	public static boolean updateStyle(int mode, VttObj vttObj, 
		MainFrame mainFrame)	 
	{
		boolean flag = true;
		DefaultStyledDocument doc = (DefaultStyledDocument) 
			mainFrame.getMainPanel().getStyledDocument();
		// better way for coding, easier for debug
		switch(mode)
		{
			// update markups: display or not
			case MARKUPS:
				flag = VttDocument.updateStyleForMarkups(doc, 
					vttObj.getVttDocument(), vttObj.getConfigObj(), vttObj);
				break;
			// update text: used when the style of text needs to be updated
			// please notes that the cursor position is changed to the end 
			case TEXT_MARKUPS:
				// update style for text & markup, keep the same caret position
				int curCaretPos 
					= mainFrame.getMainPanel().getCaretPosition();
				VttDocument.updateStyleForText(vttObj, doc, vttObj.getVttDocument(),
					vttObj.getConfigObj(), 
					vttObj.getConfigObj().getZoomFactor());
				flag = VttDocument.updateStyleForMarkups(doc, 
					vttObj.getVttDocument(), vttObj.getConfigObj(), vttObj);
				mainFrame.getMainPanel().setCaretPosition(curCaretPos);				
				break;
		}
		
		return flag;
	}
	/**
	* Update style of text, highlight, & markups (display or not), within specified offset range.
    *
    * @param vttObj  the vttObj Java object
    * @param mode  MARKUPS for update markups only; TEXT_MARKUPS for update both
	* 		text and markups
    *
    * @return a boolean flag if the update style operation successful
    */
	public static boolean updateStyle(VttObj vttObj, int rangeStart, int rangeEnd, boolean highlightRange)
	{
		MainFrame mainFrame = vttObj.getMainFrame();
		DefaultStyledDocument doc = (DefaultStyledDocument) 
				mainFrame.getMainPanel().getStyledDocument();
		return VttDocument.updateStyleForMarkupsInRange(doc, vttObj.getVttDocument(), vttObj.getConfigObj(), vttObj, vttObj.getConfigObj().getZoomFactor(), rangeStart, rangeEnd, highlightRange);
	}
	/**
	* Toggle bold.
    *
    * @param vttObj  the vttObj Java object
    */
	public static void toggleBold(VttObj vttObj)
	{
		boolean bold = !vttObj.getVttDocument().getReservedTag().isBold();
		vttObj.getVttDocument().getReservedTag().setBold(bold);
		updateStyle(TEXT_MARKUPS, vttObj);
	}
	/**
	* Toggle italic.
    *
    * @param vttObj  the vttObj Java object
    */
	public static void toggleItalic(VttObj vttObj)
	{
		boolean italic = !vttObj.getVttDocument().getReservedTag().isItalic();
		vttObj.getVttDocument().getReservedTag().setItalic(italic);
		updateStyle(TEXT_MARKUPS, vttObj);
	}
	/**
	* Toggle underline.
    *
    * @param vttObj  the vttObj Java object
    */
	public static void toggleUnderline(VttObj vttObj)
	{
		boolean underline = !vttObj.getVttDocument().getReservedTag().isUnderline();
		vttObj.getVttDocument().getReservedTag().setUnderline(underline);
		updateStyle(TEXT_MARKUPS, vttObj);
	}
	/**
	* Update font family.
    *
    * @param fontFamily  the font family in String
    * @param vttObj  the vttObj Java object
    */
	public static void updateFontFamily(String fontFamily, VttObj vttObj)
	{
		vttObj.getVttDocument().getReservedTag().setFontFamily(fontFamily);
		updateStyle(TEXT_MARKUPS, vttObj);
	}
	/**
	* Set font size to default.
    *
	* @param owner  the owner of this dialog
    * @param vttObj  the vttObj Java object
    */
	public static void setupFontSize(JFrame owner, VttObj vttObj)
	{
		String size = JOptionPane.showInputDialog(owner, "Text font size:", 
			vttObj.getVttDocument().getReservedTag().getFontSize());
		// update size if the input is not null
		if(size != null)
		{
			vttObj.getVttDocument().getReservedTag().setFontSize(size);
			updateStyle(TEXT_MARKUPS, vttObj);
		}
	}
	/**
	* Set text style to default (reserved tag).
    *
    * @param vttObj  the vttObj Java object
    */
	public static void setTextToDefault(VttObj vttObj)
	{
		// set reserved tag to a default tag
		Tag tag = new Tag(vttObj.getVttDocument().getReservedTag());
		tag.setToTextDefault();
		vttObj.getVttDocument().setReservedTag(tag);
		// update checkbox
		vttObj.getMainFrame().getVttMenuBar().setTextMenuToDefault();
		updateStyle(TEXT_MARKUPS, vttObj);
	}
	/**
	* Set the color of highlight.
    *
    * @param vttObj  the vttObj Java object
    */
	public static void setHighlightColors(VttObj vttObj)
	{
		setHighlightColors(vttObj, vttObj.getMainFrame());
	}
	/**
	* Set the color of highlight.
    *
    * @param vttObj  the vttObj Java object
	* @param mainFrame  the VTT main frame
    */
	public static void setHighlightColors(VttObj vttObj, MainFrame mainFrame)
	{
		// use the first (0) tag as the text/selection colors
		Tag textTag = vttObj.getVttDocument().getReservedTag();
		mainFrame.getMainPanel().setHighlightColors(
			textTag.getForeground(), textTag.getBackground());
	}
	/**
	* Set the color of highlight text.
    *
    * @param vttObj  the vttObj Java object
    */
	public static void setHighlightTextColor(VttObj vttObj)
	{
		Color color = JColorChooser.showDialog(
			vttObj.getMainFrame().getMainPanel().getTextPane(),
			"Selected Text Color Editor", 
			vttObj.getVttDocument().getReservedTag().getForeground());
		if(color != null)
		{
			// update from input to vtt text tag color
			vttObj.getVttDocument().getReservedTag().setForeground(color);
			// update the main panel
			setHighlightColors(vttObj);
			updateStyle(MARKUPS, vttObj);
		}
	}
	/**
	* Set the color of highlight background.
    *
    * @param vttObj  the vttObj Java object
    */
	public static void setHighlightBackgroundColor(VttObj vttObj)
	{
		Color color = JColorChooser.showDialog(
			vttObj.getMainFrame().getMainPanel().getTextPane(),
			"Selection Color Editor", 
			vttObj.getVttDocument().getReservedTag().getBackground());
		if(color != null)
		{
			// update from input to vtt text tag color
			vttObj.getVttDocument().getReservedTag().setBackground(color);
			// update the main panel
			setHighlightColors(vttObj);
			updateStyle(MARKUPS, vttObj);
		}
	}
	// data member
	public final static int MARKUPS = 0;
	public final static int TEXT_MARKUPS = 1;
}
