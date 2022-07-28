package gov.nih.nlm.nls.vtt.guiControl;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This is the Java class of controller for Config Options Dialog.
* 
* <p><b>History:</b>
* <ul>
* <li>SCR-96, chlu, 02/01/10, add Username to VTT config file
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class ConfigOptionsDialogControl implements ActionListener, ItemListener
{
	// public constractor
    /**
    * Create a ConfigOptionsDialogControl object for config options dialog 
	* controller by specifying vttObj.
    *
    * @param vttObj  the vttObj Java object
    */
	public ConfigOptionsDialogControl(VttObj vttObj)
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
			vttObj_.getConfigOptionsDialog().updateGui(vttObj_);
		}
		else if (source instanceof JTextField)  // Text Field
		{
			vttObj_.getConfigOptionsDialog().updateGui(vttObj_);
		}
		else if (source instanceof JButton)	// buttons
		{
			String command = evt.getActionCommand();
			vttObj_.getConfigOptionsDialog().updateGui(vttObj_);
			// top panel
			if(command.equals(ConfigOptionsDialog.B_CANCEL) == true)
			{
				vttObj_.getConfigOptionsDialog().hitCancel();
			}
			else if(command.equals(ConfigOptionsDialog.B_OK) == true)
			{
				vttObj_.getConfigOptionsDialog().hitOk(vttObj_);
			}
			// color
			else if(command.equals(ConfigOptionsDialog.B_TEXT_COLOR) == true)
			{
				vttObj_.getConfigOptionsDialog().hitEditTextColor(vttObj_);
			}
			else if(command.equals(ConfigOptionsDialog.B_BG_COLOR) == true)
			{
				vttObj_.getConfigOptionsDialog().hitEditBgColor(vttObj_);
			}
			// save & reset
			else if(command.equals(ConfigOptionsDialog.B_SAVE) == true)
			{
				vttObj_.getConfigOptionsDialog().hitSave(vttObj_);
			}
			else if(command.equals(ConfigOptionsDialog.B_RESET) == true)
			{
				vttObj_.getConfigOptionsDialog().hitReset(vttObj_);
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
		vttObj_.getConfigOptionsDialog().updateGui(vttObj_);
	}
	// data member
	private VttObj vttObj_ = null;
}
