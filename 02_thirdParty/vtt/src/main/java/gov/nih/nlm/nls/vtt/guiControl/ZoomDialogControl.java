package gov.nih.nlm.nls.vtt.guiControl;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This is the Java class of controller for Zoom Options Dialog.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class ZoomDialogControl implements ActionListener, ChangeListener
{
	// public constractor
	/**
	* Create a ZoomDialogControl object for zoom dialog controller by 
	* specifying vttObj.
	*
	* @param vttObj  the vttObj Java object
	*/
	public ZoomDialogControl(VttObj vttObj)
	{
		vttObj_ = vttObj;
	}
	// public methods
	/**
	* Override methods for button & combo box
	*
	* @param evt is used to notify interested parties that state has changed 
	* in the event source
	*/
	public void stateChanged(ChangeEvent evt)
	{
		JSlider source = (JSlider) evt.getSource();
		if(source.getValueIsAdjusting() == false)
		{
			// zoom opeations
			vttObj_.getZoomDialog().updateLocalVarsFromGui();
			vttObj_.getZoomDialog().updateGlobalFromLocalVars(vttObj_);
			// update text font display
			TextDisplayOperations.updateStyle(
				TextDisplayOperations.TEXT_MARKUPS, 
				vttObj_, vttObj_.getMainFrame());
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
		Object source = evt.getSource();
		if (source instanceof JButton)	// buttons
		{
			String command = evt.getActionCommand();
			// top panel
			if(command.equals(ZoomDialog.B_CLOSE) == true)
			{
				vttObj_.getZoomDialog().hitClose();
			}
			else if(command.equals(ZoomDialog.B_DEFAULT) == true)
			{
				vttObj_.getZoomDialog().hitDefault(vttObj_);
			}
		}
	}
	// data member
	private VttObj vttObj_ = null;
}
