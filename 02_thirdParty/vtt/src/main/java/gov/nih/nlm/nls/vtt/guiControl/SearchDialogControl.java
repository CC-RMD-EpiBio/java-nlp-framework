package gov.nih.nlm.nls.vtt.guiControl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextField;

import gov.nih.nlm.nls.vtt.gui.SearchDialog;
import gov.nih.nlm.nls.vtt.model.VttObj;

public class SearchDialogControl implements ActionListener {

	/**
	* Create a FindDialogControl object for finding text dialog controller by 
	* specifying vttObj.
	*
	* @param vttObj  the vttObj Java object
	*/
	public SearchDialogControl(VttObj vttObj)
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
			vttObj_.getSearchDialog().hitSearch(vttObj_);
		}
		else if (source instanceof JButton)	// buttons
		{
			String command = evt.getActionCommand();
			// top panel
			if(command.equals(SearchDialog.B_CLOSE) == true)
			{
				vttObj_.getSearchDialog().hitClose();
			}
			else if(command.equals(SearchDialog.B_SEARCH) == true)
			{
				vttObj_.getSearchDialog().hitSearch(vttObj_);
			}
		}
	}
	// data member
	private VttObj vttObj_ = null;


}
