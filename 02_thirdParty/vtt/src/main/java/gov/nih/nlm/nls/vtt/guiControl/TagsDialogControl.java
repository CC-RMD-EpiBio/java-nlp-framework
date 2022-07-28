package gov.nih.nlm.nls.vtt.guiControl;
import java.awt.event.*;

import javax.swing.event.*;

import java.util.*;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This is the Java class of Tags controller for TagsDialog.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class TagsDialogControl implements ActionListener, ChangeListener, ItemListener
{
	/**
	* Create a TagsDialogControl object for tags dialog controller by 
	* specifying vttObj.
	*
	* @param vttObj  the vttObj Java object
	*/
	public TagsDialogControl(VttObj vttObj)
	{
		vttObj_ = vttObj;
	}
	// public methods
	/**
	* Set the flag of updating changed state.
	*
	* @param updateStateChange  a boolean flag of updating changed state
	*/
	public void setUpdateStateChange(boolean updateStateChange)
	{
		updateStateChange_ = updateStateChange;
	}
	/**
	* Override methods for tab panel change
	*
	* @param evt is used to notify interested parties that state has changed 
	* in the event source
	*/
	public void stateChanged(ChangeEvent evt)
	{
		// must have to update display filter checkbox list
		if(updateStateChange_ == true)
		{
			vttObj_.getTagsDialog().updateTabs(vttObj_);
		}
	}
	/**
	* Override methods for check box, when status change
	*
	* @param e a semantic event which indicates that an item was selected or
	* deselected.
	*/
	public void itemStateChanged(ItemEvent e)
	{
		if(updateStateChange_ == true)
		{
			vttObj_.getTagsDialog().updateGlobalFromGui(vttObj_);
			TextDisplayOperations.updateStyle(
				TextDisplayOperations.TEXT_MARKUPS, vttObj_);
		}
	}
	/**
	* Override methods for button & combo box
	*
	* @param evt a semantic event which indicates that a component-defined 
	* action occurred.
	*/
	public void actionPerformed(ActionEvent evt) 
	{
		String command = evt.getActionCommand();
		// top panel
		if(command.equals(TagsDialog.B_CLOSE) == true)
		{
			vttObj_.getTagsDialog().hitClose();
		}
		// property panel
		else if(command.equals(TagsDialog.B_ADD) == true)
		{
			vttObj_.getTagsDialog().hitAdd(vttObj_);
		}
		else if(command.equals(TagsDialog.B_DELETE) == true)
		{
			vttObj_.getTagsDialog().hitDelete(vttObj_);
		}
		else if(command.equals(TagsDialog.B_EDIT) == true)
		{
			vttObj_.getTagsDialog().hitEdit(vttObj_);
		}
		else if(command.equals(TagsDialog.B_SORT) == true)
		{
			vttObj_.getTagsDialog().hitSortNameCategory(vttObj_);
		}
		else if(command.equals(TagsDialog.B_UP) == true)
		{
			vttObj_.getTagsDialog().hitMoveUp(vttObj_);
		}
		else if(command.equals(TagsDialog.B_DOWN) == true)
		{
			vttObj_.getTagsDialog().hitMoveDown(vttObj_);
		}
		else if(command.equals(TagsDialog.B_MAP_KEYS) == true)
		{
			vttObj_.getTagsDialog().hitMapToQuickKeys(vttObj_);
		}
		// display panel
		else if(command.equals(TagsDialog.B_ALL_NAME) == true)
		{
			vttObj_.getTagsDialog().hitDisplayAllName(vttObj_);
		}
		else if(command.equals(TagsDialog.B_NO_NAME) == true)
		{
			vttObj_.getTagsDialog().hitDisplayNoName(vttObj_);
		}
		else if(command.equals(TagsDialog.B_ALL_CATEGORY) == true)
		{
			vttObj_.getTagsDialog().hitDisplayAllCategory(vttObj_);
		}
		else if(command.equals(TagsDialog.B_NO_CATEGORY) == true)
		{
			vttObj_.getTagsDialog().hitDisplayNoCategory(vttObj_);
		}
		// Quick keys Mapping
		else if(command.equals(TagsDialog.B_APPLY) == true)
		{
			vttObj_.getTagsDialog().hitApplyQuickKeys(vttObj_);
		}
		else if(command.equals(TagsDialog.B_RESET) == true)
		{
			vttObj_.getTagsDialog().hitResetQuickKeys(vttObj_);
		}
		// Save & Import
		else if(command.equals(TagsDialog.B_SAVE) == true)
		{
			vttObj_.getTagsDialog().hitSave(vttObj_);
		}
		else if(command.equals(TagsDialog.B_SAVE_AS) == true)
		{
			vttObj_.getTagsDialog().hitSaveAs(vttObj_);
		}
		else if(command.equals(TagsDialog.B_IMPORT) == true)
		{
			vttObj_.getTagsDialog().hitImport(vttObj_);
		}
	}
	private VttObj vttObj_ = null;
	private boolean updateStateChange_ = false;
}
