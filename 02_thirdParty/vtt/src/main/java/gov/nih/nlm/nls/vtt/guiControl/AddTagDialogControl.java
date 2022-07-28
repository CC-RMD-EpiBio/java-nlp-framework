package gov.nih.nlm.nls.vtt.guiControl;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This is the Java class of Controller for AddTagDialog. 
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class AddTagDialogControl implements ActionListener, ItemListener
{
	// public constractor
	/**
	* Create an AddTagDialogControl object for adding tags dialog controller by 
	* specifying vttObj.
	*
	* @param vttObj  the vttObj Java object
	*/
	public AddTagDialogControl(VttObj vttObj)
	{
		vttObj_ = vttObj;
	}
	// public methods
	/**
	* Override methods for button & combo box
	*
	* @param evt a semantic event which indicates that a component-defined 
	* action occurred.
	*/
	public void actionPerformed(ActionEvent evt) 
	{
		Object source = evt.getSource();
		if(source instanceof JComboBox) // combo box
		{
			vttObj_.getAddTagDialog().updateGui(vttObj_);
		}
		else if (source instanceof JTextField)	// Text Field
		{
			vttObj_.getAddTagDialog().updateGui(vttObj_);
		}
		else if (source instanceof JButton)	// button
		{
			String command = evt.getActionCommand();
			// top panel
			if(command.equals(AddTagDialog.B_CANCEL) == true)
			{
				vttObj_.getAddTagDialog().hitCancel();
			}
			else if(command.equals(AddTagDialog.B_OK) == true)
			{
				vttObj_.getAddTagDialog().hitOk(vttObj_);
			}
			// color
			else if(command.equals(AddTagDialog.B_TEXT_COLOR) == true)
			{
				vttObj_.getAddTagDialog().hitEditTextColor(vttObj_);
			}
			else if(command.equals(AddTagDialog.B_BG_COLOR) == true)
			{
				vttObj_.getAddTagDialog().hitEditBgColor(vttObj_);
			}
		}
	}
	/**
	* Override methods for check box
	*
	* @param e a semantic event which indicates that an item was selected or
	* deselected.
	*/
	public void itemStateChanged(ItemEvent e)
	{
		vttObj_.getAddTagDialog().updateGui(vttObj_);
	}
	// data members
	private VttObj vttObj_ = null;
}
