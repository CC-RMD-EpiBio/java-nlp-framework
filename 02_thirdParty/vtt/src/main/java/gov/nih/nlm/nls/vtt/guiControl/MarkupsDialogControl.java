package gov.nih.nlm.nls.vtt.guiControl;
import java.awt.event.*;

import javax.swing.event.*;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This is the Java class of Controller for MarkupsDialog 
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class MarkupsDialogControl implements ActionListener
{
	/**
	* Create a MarkupsDialogControl object for markups dialog controller by 
	* specifying vttObj.
	*
	* @param vttObj  the vttObj Java object
	*/
	public MarkupsDialogControl(VttObj vttObj)
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
		String command = evt.getActionCommand();
		// top panel
		if(command.equals(MarkupsDialog.B_CLOSE) == true)
		{
			vttObj_.getMarkupsDialog().hitClose();
		}
		// log panel
		// report panel
	}
	// data member
	private VttObj vttObj_ = null;
}
