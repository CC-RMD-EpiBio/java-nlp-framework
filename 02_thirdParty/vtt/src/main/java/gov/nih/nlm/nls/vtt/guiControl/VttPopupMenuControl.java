package gov.nih.nlm.nls.vtt.guiControl;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This is the Java class for Popup Menu controller
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class VttPopupMenuControl implements ActionListener 
{
	// public constractor
	/**
	* Create a VttPopupMenuControl object for Popup menu controller by 
	* specifying vttObj.
	*
	* @param vttObj  the vttObj Java object
	*/
	public VttPopupMenuControl(VttObj vttObj)
	{
		vttObj_ = vttObj;
	}
	// public methods
	/**
	* Override methods for Menu Selection.
	*
	* @param evt a semantic event which indicates that a component-defined 
	* action occurred.
	*/
	public void actionPerformed(ActionEvent evt)
	{
		String arg = evt.getActionCommand();    // get action command
		// doc
		DefaultStyledDocument doc = (DefaultStyledDocument) 
			vttObj_.getMainFrame().getMainPanel().getStyledDocument();
		MainFrame mainFrame = vttObj_.getMainFrame();	
		if(arg.equals("Exit"))   // Exit
		{
			FileOperations.exitOperation(vttObj_.getMainFrame(), vttObj_);
		}
		else if(arg.equals("Close"))
		{
			FileOperations.closeOperation(mainFrame, vttObj_);
		}
		else if(arg.equals("Print"))
		{
			FileOperations.printOperation(vttObj_);
		}
		else if(arg.equals("Save"))
		{
			FileOperations.saveOperation(mainFrame, vttObj_);
		}
		else if(arg.equals("Delete"))
		{
			MarkupOperations.deleteOperation(vttObj_);
		}
		else if(arg.equals("Delete All"))
		{
			MarkupOperations.deleteAllOperation(mainFrame, vttObj_);
		}
		else if(arg.equals("Undo"))
		{
			UndoOperations.undo(vttObj_);
		}
		else if(arg.equals("Redo"))
		{
			UndoOperations.redo(vttObj_);
		}
		else	// Add new Markup tag action
		{
			JMenuItem source = (JMenuItem) evt.getSource();
			String name = source.getName();
			String category = new String();
			if(name == null)	// no category
			{
				name = arg;
			}
			else	// with category
			{
				category = arg;
			}
			MarkupOperations.updateOperation(name, category, vttObj_);
		}
	}
	// private methods
	// data member
	private VttObj vttObj_ = null;
}
