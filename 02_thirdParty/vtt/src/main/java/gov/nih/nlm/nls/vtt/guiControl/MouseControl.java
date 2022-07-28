package gov.nih.nlm.nls.vtt.guiControl;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This is the Java class of Mouse controller for MouseAdapter.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class MouseControl extends MouseAdapter
{
	/**
	* Create an MouseControl object for mouse controller by specifying vttObj.
	*
	* @param vttObj  the vttObj Java object
	*/
	public MouseControl(VttObj vttObj)
	{
		vttObj_ = vttObj;
	}
	// public methods
	/**
	* Override methods for mouse press.
    * isPopupTrigger need to be checked for both pressed and released
    * for different platform
	*
	* @param evt an event indicates a mouse press action in a component. 
	*/
    public void mousePressed(MouseEvent evt)
    {
        // check if popup triggle for the platform
        if(evt.isPopupTrigger())
        {
            vttObj_.getVttPopupMenu().show(evt, vttObj_);
        }
    }
	/**
	* Override methods for mouse press.
    * isPopupTrigger need to be checked for both pressed and released
    * for different platform
	*
	* @param evt an event indicates a mouse release action in a component. 
	*/
    public void mouseReleased(MouseEvent evt)
    {
        // check if popup triggle for the platform
        if(evt.isPopupTrigger())
        {
            vttObj_.getVttPopupMenu().show(evt, vttObj_);
        }
    }
	// private methods
	// data member
	private VttObj vttObj_ = null;
}
