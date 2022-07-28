package gov.nih.nlm.nls.vtt.guiControl;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This is the Java class of controller for Find Options Dialog
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class FindDialogControl implements ActionListener
{
	/**
	* Create a FindDialogControl object for finding text dialog controller by 
	* specifying vttObj.
	*
	* @param vttObj  the vttObj Java object
	*/
	public FindDialogControl(VttObj vttObj)
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
		if (source instanceof JTextField)  // Text Field
		{
			// make enter in textfield to find
			vttObj_.getFindDialog().hitNext(vttObj_);
		}
		else if (source instanceof JButton)	// buttons
		{
			String command = evt.getActionCommand();
			// top panel
			if(command.equals(FindDialog.B_CLOSE) == true)
			{
				vttObj_.getFindDialog().hitClose();
			}
			else if(command.equals(FindDialog.B_PREVIOUS) == true)
			{
				vttObj_.getFindDialog().hitPrevious(vttObj_);
			}
			else if(command.equals(FindDialog.B_NEXT) == true)
			{
				vttObj_.getFindDialog().hitNext(vttObj_);
			}
		}
	}
	// data member
	private VttObj vttObj_ = null;
}
