package gov.nih.nlm.nls.vtt.guiControl;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This is the Controller Java class for MarkupDialog to synchronize JScrollBar. 
* Use ChangeListener for JScorllBar.getModel().
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class MarkupsDiffDialogControl implements ChangeListener
{
	/**
	* Create a MarkupsDiffDialogControl object for markups difference dialog 
	* controller by specifying model.
	*
	* @param model  bounded range model
	*/
	public MarkupsDiffDialogControl(BoundedRangeModel model)
	{
		model_ = model;
	}
	// public methods
	/**
	* Override methods
	*
	* @param evt is used to notify interested parties that state has changed 
	* in the event source
	*/
	public void stateChanged(ChangeEvent evt) 
	{
		BoundedRangeModel model = (BoundedRangeModel) evt.getSource();
		model_.setValue(model.getValue());
	}
	// data mebers
	private BoundedRangeModel model_ = null;
}
