package gov.nih.nlm.nls.vtt.gui;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.io.*;

import javax.swing.text.*;

import gov.nih.nlm.nls.vtt.guiControl.*;
import gov.nih.nlm.nls.vtt.guiLib.*;
import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This class is the GUI main frame Java object used in VTT. It includes:
* <ul>
* <li>VttMenuBar
* <li>MainPanel
* <li>StatusPanel
* </ul>
* 
* <p><b>History:</b>
* <ul>
* <li> SCR-89, clu, 01-25-10, add VTT icon image
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class MainFrame extends JFrame
{
	// public constructor
	/**
    * Create a MainFrame object used in VTT with specifying vttObj.
    *
    * @param vttObj  the vttObj Java object
    */
	public MainFrame(VttObj vttObj)
	{
		this(vttObj, 50, 50);
	}
	// constructor with inFile
	/**
    * Create a MainFrame object used in VTT with specifying vttObj and 
	* x and y position of VTT window.
    *
    * @param vttObj  the vttObj Java object
    * @param x  x position of VTT window
    * @param y  y position of VTT window
    */
	public MainFrame(VttObj vttObj, int x, int y)
	{
		// init gui controller
		initGuiController(vttObj);
		// get screen size
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		int screenHeight = d.height;
		int screenWidth = d.width;
		// init frame 
		//setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocation(x, y);
		// set minized icon image
		Image img = tk.getImage(vttObj.getConfigObj().getVttDir()
			+ "/data/images/vttLogo.jpg");
		setIconImage(img);
		// menu bar: all dialog is relative to main Frame
		vttMenuBar_ = new VttMenuBar(this, vttObj);
		setJMenuBar(vttMenuBar_);
		// content & Status
		mainPanel_ = new MainPanel(vttObj);
		getContentPane().add(mainPanel_, BorderLayout.CENTER);
		getContentPane().add(statusPanel_, BorderLayout.PAGE_END);
		// set title
		updateTitle(vttObj);		
		// add listener
		WindowControl.setOwner(this);
		addWindowListener(windowControl_);
		// Display the window
		pack();
	}
	/**
    * Get VTT menu bar.
    *
    * @return VTT menu bar
    */
	public VttMenuBar getVttMenuBar()
	{
		return vttMenuBar_;
	}
	
	/**
    * Get VTT main panel.
    *
    * @return VTT main panel
    */
	public MainPanel getMainPanel()
	{
		return mainPanel_;
	}
	
	/**
    * Get VTT status panel.
    *
    * @return VTT status panel
    */
	public StatusPanel getStatusPanel()
	{
		return statusPanel_;
	}
	/**
    * Update VTT title with the name of opened file.
    *
	* @param vttObj  the vttObj Java object
    */
	public void updateTitle(VttObj vttObj)
	{
		// set default file name
		String titleStr = GlobalVars.VERSION_STR;
		File docFile = vttObj.getDocFile();
		// set teh tile as file name + VTT version
		if((docFile != null)
		&& (docFile.exists() == true))
		{
			try
			{
				String fileName = docFile.getName();
				String canonicalPath = docFile.getCanonicalPath();
				int fileLength = fileName.length();
				int canonicalPathLength = canonicalPath.length();
				int filePathDisplayLength 
					= vttObj.getConfigObj().getFilePathDisplayLength();
				// display the file name
				if(fileLength >= filePathDisplayLength)
				{
					titleStr = fileName + " - " + VTT_VERSION;
				}
				// display the whole path
				else if (canonicalPathLength <= filePathDisplayLength)
				{
					titleStr = canonicalPath + " - " + VTT_VERSION;
				}
				else	// display size of dispFilePathLength_ of canonical path
				{
					int startIndex 
						= canonicalPathLength - filePathDisplayLength;
					titleStr = "..." + canonicalPath.substring(startIndex) 
						+ " - " + VTT_VERSION;
				}
			}
			catch (IOException e)
			{
			}
		}
		this.setTitle(titleStr);
	}
	private void initGuiController(VttObj vttObj)
	{
		windowControl_ = new WindowControl(this, vttObj);
	}
	
	// private methods
	// static variables
	private static final long serialVersionUID = 5L;
	private static final String VTT_VERSION = "VTT, " + GlobalVars.YEAR_STR;
	// data member
	private WindowControl windowControl_ = null;
	private VttMenuBar vttMenuBar_ = null;
	private MainPanel mainPanel_ = null;
	private StatusPanel statusPanel_ = new StatusPanel();
}
