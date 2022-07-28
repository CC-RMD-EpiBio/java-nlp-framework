package gov.nih.nlm.nls.vtt.guiControl;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import gov.nih.nlm.nls.vtt.gui.*;
/*****************************************************************************
* This is the Java class of controller for Difference Dialog
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class DiffDialogControl implements ActionListener
{
	// public constractor
    /**
    * Create a DiffDialogControl object for diff Dialog controller by 
    * specifying vttObj.
    *
    * @param diffDialog  the diff dialog
    */
	public DiffDialogControl(DiffDialog diffDialog)
	{
		diffDialog_ = diffDialog;
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
		if (source instanceof JTextField)  // Text Field
		{
			// make enter in textfield to find
		}
		else if (source instanceof JButton)	// buttons
		{
			String command = evt.getActionCommand();
			// top panel
			if(command.equals(DiffDialog.B_OK) == true)
			{
				diffDialog_.hitOk();
			}
			else if(command.equals(DiffDialog.B_OPEN) == true)
			{
				diffDialog_.hitOpen();
			}
		}
	}
	// data member
	private DiffDialog diffDialog_ = null;
}
