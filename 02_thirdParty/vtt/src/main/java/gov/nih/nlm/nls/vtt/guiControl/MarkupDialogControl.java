package gov.nih.nlm.nls.vtt.guiControl;
import java.awt.event.*;

import javax.swing.event.*;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This is the Controller Java class for MarkupDialog. 
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class MarkupDialogControl implements ActionListener
{
	/**
	* Create a MarkupDialogControl object for markup dialog controller by 
	* specifying vttObj.
	*
	* @param vttObj  the vttObj Java object
	*/
	public MarkupDialogControl(VttObj vttObj)
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
		if(command.equals(MarkupDialog.B_OK) == true)
		{
			vttObj_.getMarkupDialog().hitOk(vttObj_);
		}
		else if(command.equals(MarkupDialog.B_CANCEL) == true)
		{
			vttObj_.getMarkupDialog().hitCancel(vttObj_);
		}
		else if(command.equals(MarkupDialog.B_APPLY) == true)
		{
			vttObj_.getMarkupDialog().hitApply(vttObj_);
		}
		else if(command.equals(MarkupDialog.B_RESET) == true)
		{
			vttObj_.getMarkupDialog().hitReset(vttObj_);
		}
		else if(command.equals(MarkupDialog.B_DELETE) == true)
		{
			vttObj_.getMarkupDialog().hitDelete(vttObj_);
		}
	}
	// data mebers
	private VttObj vttObj_ = null;
}
