package gov.nih.nlm.nls.vtt.guiControl;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.io.*;

import gov.nih.nlm.nls.vtt.api.*;
import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This is the Java class of MenuBar controller for ActionListener.
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
public class VttMenuBarControl implements ActionListener, MenuListener
{
	/**
	* Create a VttMenuBarControl object for Vtt Menu Bar controller by 
	* specifying owner and vttObj.
	*
	* @param vttObj  the vttObj Java object
	*/
	public VttMenuBarControl(JFrame owner, VttObj vttObj)
	{
		owner_ = owner;
		vttObj_ = vttObj;
	}
	// public methods
	/**
	* Override methods for action listener for Menu Selection.
	*
	* @param evt a semantic event which indicates that a component-defined 
	* action occurred.
	*/
	public void actionPerformed(ActionEvent evt)
	{
		String arg = evt.getActionCommand();    // get action command
		// Vtt System Sub Menu
		if(arg.equals(VttMenuBar.VTT_OPEN))
        {
			FileOperations.openOperation(owner_, vttObj_);
        }
		else if(arg.equals(VttMenuBar.VTT_SAVE))
        {
			FileOperations.saveOperation(owner_, vttObj_);
        }
		else if(arg.equals(VttMenuBar.VTT_SAVE_AS))
        {
			FileOperations.saveAsOperation(owner_, vttObj_);
        }
		else if(arg.equals(VttMenuBar.VTT_SAVE_AS_EXCEL))
        {
			FileOperations.saveAsExcelOperation(owner_, vttObj_);
        }
		else if(arg.equals(VttMenuBar.VTT_PRINT))
        {
			FileOperations.printOperation(vttObj_);
		}
		else if(arg.equals(VttMenuBar.VTT_CLOSE))
        {
			FileOperations.closeOperation(owner_, vttObj_);
		}
		else if(arg.equals(VttMenuBar.VTT_EXIT))	 // Exit
		{
			FileOperations.exitOperation(owner_, vttObj_);
		}
		// Text Sub menu
		else if((arg.equals(VttMenuBar.TEXT_FONT_SERIF))
        || (arg.equals(VttMenuBar.TEXT_FONT_SANSSERIF))
        || (arg.equals(VttMenuBar.TEXT_FONT_MONOSPACED))
        || (arg.equals(VttMenuBar.TEXT_FONT_DIALOG))
        || (arg.equals(VttMenuBar.TEXT_FONT_DIALOGINPUT)))
        {
			TextDisplayOperations.updateFontFamily(arg, vttObj_);
        }
		else if(arg.equals(VttMenuBar.TEXT_BOLD))
		{
			TextDisplayOperations.toggleBold(vttObj_);
		}
		else if(arg.equals(VttMenuBar.TEXT_ITALIC))
		{
			TextDisplayOperations.toggleItalic(vttObj_);
		}
		else if(arg.equals(VttMenuBar.TEXT_UNDERLINE))
		{
			TextDisplayOperations.toggleUnderline(vttObj_);
		}
		else if(arg.equals(VttMenuBar.TEXT_FONT_SIZE))
        {
			TextDisplayOperations.setupFontSize(owner_, vttObj_);
        }
		else if(arg.equals(VttMenuBar.TEXT_DEFAULT))
		{
			TextDisplayOperations.setTextToDefault(vttObj_);
		}
		else if(arg.equals(VttMenuBar.TEXT_HIGHLIGHT_COLOR))
		{
			TextDisplayOperations.setHighlightTextColor(vttObj_);
		}
		else if(arg.equals(VttMenuBar.TEXT_HIGHLIGHT_BG_COLOR))
		{
			TextDisplayOperations.setHighlightBackgroundColor(vttObj_);
		}
		else if(arg.equals(VttMenuBar.TEXT_HELP))
		{
			HelpDocOperations.showHelpDoc(HelpDocOperations.TEXT_PAGE, vttObj_);
		}
		// Tag Sub menu
		else if(arg.equals(VttMenuBar.TAG_SETUP))
		{
			vttObj_.getTagsDialog().show(owner_, 0, vttObj_);
		}
		else if(arg.equals(VttMenuBar.TAG_DISPLAY_FILTER))
		{
			vttObj_.getTagsDialog().show(owner_, 1, vttObj_);
		}
		else if(arg.equals(VttMenuBar.TAG_QUICK_KEY_MAP))
		{
			vttObj_.getTagsDialog().show(owner_, 2, vttObj_);
		}
		else if(arg.equals(VttMenuBar.TAG_SAVE_IMPORT))
		{
			vttObj_.getTagsDialog().show(owner_, 3, vttObj_);
		}
		else if(arg.equals(VttMenuBar.TAG_QUICK_LOAD))
		{
			TagOperations.quickTagsLoading(vttObj_);
		}
		else if(arg.equals(VttMenuBar.TAG_HELP))
		{
			HelpDocOperations.showHelpDoc(HelpDocOperations.TAGS_PAGE, vttObj_);
		}
		// Markup Sub menu
		else if(arg.equals(VttMenuBar.MARKUP_DETAILS))
		{
			vttObj_.getMarkupsDialog().show(owner_, 0, vttObj_);
		}
		else if(arg.equals(VttMenuBar.MARKUP_REPORTS))
		{
			vttObj_.getMarkupsDialog().show(owner_, 1, vttObj_);
		}
		else if(arg.equals(VttMenuBar.MARKUP_UNDO_LIST))
		{
			vttObj_.getMarkupsDialog().show(owner_, 2, vttObj_);
		}
		else if(arg.equals(VttMenuBar.MARKUP_DELETE))
		{
			// delete selected markup from list
			MarkupOperations.deleteOperation(vttObj_);
		}
		else if(arg.equals(VttMenuBar.MARKUP_DELETE_ALL))
		{
			MarkupOperations.deleteAllOperation(owner_, vttObj_);
		}
		else if(arg.equals(VttMenuBar.MARKUP_SAVE_TO_FILE))
		{
			MarkupOperations.saveToFileOperation(owner_, vttObj_);
		}
		else if(arg.equals(VttMenuBar.MARKUP_UNDO))
		{
			UndoOperations.undo(vttObj_);
		}
		else if(arg.equals(VttMenuBar.MARKUP_REDO))
		{
			UndoOperations.redo(vttObj_);
		}
		else if(arg.equals(VttMenuBar.MARKUP_OVERLAP))
		{
			vttObj_.setOverlap(!vttObj_.getOverlap());
			vttObj_.getMainFrame().getStatusPanel().setText(
				"Markup overlap: [" + vttObj_.getOverlap() + "]");
		}
		else if(arg.equals(VttMenuBar.MARKUP_HELP))
		{
			HelpDocOperations.showHelpDoc(HelpDocOperations.MARKUPS_PAGE, 
				vttObj_);
		}
		// Options menu
		else if((arg.equals(VttMenuBar.OPTION_LAF_SYSTEM) == true)
		|| (arg.equals(VttMenuBar.OPTION_LAF_METAL) == true)
		|| (arg.equals(VttMenuBar.OPTION_LAF_MOTIF) == true)
		|| (arg.equals(VttMenuBar.OPTION_LAF_WINDOW) == true)
		|| (arg.equals(VttMenuBar.OPTION_LAF_GTK) == true))
		{
			GuiOperations.updateLookAndFeel(arg, owner_);
		}
		else if(arg.equals(VttMenuBar.OPTION_ZOOM))
		{
			vttObj_.getZoomDialog().show(owner_, vttObj_);
		}
		else if(arg.equals(VttMenuBar.OPTION_FIND))
		{
			vttObj_.getFindDialog().show(owner_, vttObj_);
		}
		else if(arg.equals(VttMenuBar.OPTION_SEARCH))
		{
			vttObj_.getSearchDialog().show(owner_, vttObj_);
		}
		else if(arg.equals(VttMenuBar.OPTION_LINE_WRAP))
		{
			vttObj_.getMainFrame().getMainPanel().toggleLineWrap();
			TextDisplayOperations.updateStyle(TextDisplayOperations.MARKUPS,
				vttObj_);
		}
		else if(arg.equals(VttMenuBar.OPTION_SHOW_STATUS))
		{
			vttObj_.getMainFrame().getStatusPanel().toggleShow();
			vttObj_.getMainFrame().getStatusPanel().setText(
				"Markup overlap: [" + vttObj_.getOverlap() + "]");
		}
		else if(arg.equals(VttMenuBar.OPTION_COMPARE))	// compare two vtt files
		{
			CompareOperations.compareToFileOperation(owner_, vttObj_);
		}
		else if(arg.equals(VttMenuBar.OPTION_CONFIG_SETUP))
		{
			vttObj_.getConfigOptionsDialog().show(owner_, vttObj_);
		}
		else if(arg.equals(VttMenuBar.META_TAGS_FILE))
		{
			vttObj_.getMetaDataDialog().show(owner_, 0, vttObj_);
		}
		else if(arg.equals(VttMenuBar.META_SAVE_HISTORY))
		{
			vttObj_.getMetaDataDialog().show(owner_, 1, vttObj_);
		}
		// Help Sub menu
		else if(arg.equals(VttMenuBar.HELP_ABOUT))
		{
			JOptionPane.showMessageDialog(null, vttObj_.getVersionInfo(),
				"About Visual Tagging Tool", JOptionPane.INFORMATION_MESSAGE);
		}
		else if(arg.equals(VttMenuBar.HELP_KEYS_MAP))
		{
			HelpDocOperations.showHelpDoc(HelpDocOperations.KEYS_PAGE, vttObj_);
		}
		else if(arg.equals(VttMenuBar.HELP_DOCUMENTS))
		{
			HelpDocOperations.showHelpDoc(HelpDocOperations.HOME_PAGE, vttObj_);
		}
	}
	/**
	* Override methods for menu cancel
	*
	* @param e is used to notify interested parties that menu which is the 
	* event source has been posted, selected, or canceled.
	*/
	public void menuCanceled(MenuEvent e)
	{
	}
	/**
	* Override methods for menu deselected
	*
	* @param e is used to notify interested parties that menu which is the 
	* event source has been posted, selected, or canceled.
	*/
	public void menuDeselected(MenuEvent e)
	{
	}
	/**
	* Override methods for menu selected
	*
	* @param e is used to notify interested parties that menu which is the 
	* event source has been posted, selected, or canceled.
	*/
	public void menuSelected(MenuEvent e)
	{
		// update the dynamic menuItem
		vttObj_.getMainFrame().getVttMenuBar().updateGuiFromControl(
			vttObj_);
	}
	// private methods
	// data member
	private JFrame owner_ = null;
	private VttObj vttObj_ = null;
}
