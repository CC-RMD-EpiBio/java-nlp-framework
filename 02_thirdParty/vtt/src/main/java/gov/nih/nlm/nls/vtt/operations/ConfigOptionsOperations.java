package gov.nih.nlm.nls.vtt.operations;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.util.*;

import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This class provides configuration related operations.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class ConfigOptionsOperations
{
	// private constructor
	private ConfigOptionsOperations()
	{
	}
	// public method
	/**
    * Set the configuration by specifying configObj Java object.
    *
    * @param configObj  the configuration Java object
    * @param vttObj  the vttObj Java object
    */
	public static void setConfig(ConfigObj configObj, VttObj vttObj)
	{
		// update to global vars
		vttObj.getConfigObj().setVarsFromConfigObj(configObj);
		// update title in mainFrame
		vttObj.getMainFrame().updateTitle(vttObj);
		// update textPane_
		TextDisplayOperations.updateStyle(TextDisplayOperations.TEXT_MARKUPS,
			vttObj);
	}
	// data member
}
