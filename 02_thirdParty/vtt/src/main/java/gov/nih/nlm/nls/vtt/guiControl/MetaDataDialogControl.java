package gov.nih.nlm.nls.vtt.guiControl;
import java.awt.event.*;

import javax.swing.event.*;

import java.util.*;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This is the Java class of Meta data controller for MetaDataDialog.
* 
* <p><b>History:</b>
* <ul>
* <li>SCR-94, chlu, 02-03-10, new VTT file format to include Meta-Data
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class MetaDataDialogControl implements ActionListener, ChangeListener
{
	/**
	* Create a MetadataDialogControl object for tags dialog controller by 
	* specifying vttObj.
	*
	* @param vttObj  the vttObj Java object
	*/
	public MetaDataDialogControl(VttObj vttObj)
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
			vttObj_.getMetaDataDialog().updateTabs(vttObj_);
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
		// file history panel
		if(command.equals(MetaDataDialog.B_CLOSE) == true)
		{
			vttObj_.getMetaDataDialog().hitClose();
		}
		// tags file panel
		else if(command.equals(MetaDataDialog.B_SELECT) == true)
		{
			vttObj_.getMetaDataDialog().hitSelect(vttObj_);
		}
		else if(command.equals(MetaDataDialog.B_OK) == true)
		{
			vttObj_.getMetaDataDialog().hitOk(vttObj_);
		}
		else if(command.equals(MetaDataDialog.B_CANCEL) == true)
		{
			vttObj_.getMetaDataDialog().hitCancel(vttObj_);
		}
		else if(command.equals(MetaDataDialog.B_RESET) == true)
		{
			vttObj_.getMetaDataDialog().hitReset(vttObj_);
		}
	}
	private VttObj vttObj_ = null;
	private boolean updateStateChange_ = false;
}
