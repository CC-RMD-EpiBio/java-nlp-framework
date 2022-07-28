package gov.nih.nlm.nls.vtt.guiControl;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This is the Java class of Window controller for WindowAdapter. Its main
* feature is close the window.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class WindowControl extends WindowAdapter
{
	// public constractor
	/**
	* Create a WindowControl object for window controller by specifying vttObj.
	*
	* @param vttObj  the vttObj Java object
	*/
	public WindowControl(JFrame owner, VttObj vttObj)
	{
		owner_ = owner;
		vttObj_ = vttObj;
	}
	// public methods
	/**
	* Set the owner.
	*
	* @param owner the parents GUI component of this class
	*/
	public static void setOwner(JFrame owner)
	{
		owner_ = owner;
	}
	/**
	* Override methods for closing window.
	*
	* @param evt a low-level event that indicates that a window has changed 
	* its status.
	*/
    public void windowClosing(WindowEvent evt)
    {
		// check changes, save and exit
		FileOperations.exitOperation(owner_, vttObj_);
    }
	// private methods
	// data member
	private static JFrame owner_ = null;
	private VttObj vttObj_ = null;
}
