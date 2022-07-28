package gov.nih.nlm.nls.vtt.api;
import java.awt.BorderLayout;
import java.io.*;
import java.util.*;

import javax.swing.*;

import org.apache.commons.io.FileUtils;

//import gov.hih.nlm.nls.vtt.Util.IndexWrapper;
//import gov.hih.nlm.nls.vtt.Util.SnippetFinder;
//import gov.hih.nlm.nls.vtt.Util.pathUtil;

// xxx





import gov.nih.nlm.nls.vtt.gui.FindDialog;
import gov.nih.nlm.nls.vtt.gui.MainPanel;
import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This class is the VTT Java API class. 
* Its data members include a list of VttObj for opening multiple Vtt files.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class VttApi
{
	
	 
	// private constructors
	private VttApi()
	{
	}
	// public methods
	/**
	* Initiate VTT without opening any VTT file.
	*/
	public static void init()
	{
		init(new String(), true);
	}
	/**
	* Initiate VTT with specifying VTT file and verbose flag.
	*
	* @param inFileStr  the absolute path of VTT file
	* @param verbose  flag of verbose on reading VTT file
	*/
	public static void init(String inFileStr, boolean verbose)
	{
		init(new File(inFileStr), verbose);
	}
	/**
	* Initiate VTT with specifying VTT file, configuration file, and
	* verbose flag.
	*
	* @param inFileStr  the absolute path of VTT file
	* @param configFile  the absolute path of VTT configuration file
	* @param verbose  flag of verbose on reading VTT file
	*/
	public static void init(String inFileStr, String configFile,
		boolean verbose)
	{
		init(new File(inFileStr), configFile, verbose);
	}
	/**
	* Initiate VTT with specifying VTT File object and verbose flag.
	*
	* @param inFile  the Java File object of VTT file
	* @param verbose  flag of verbose on reading VTT file
	*/
	public static void init(File inFile, boolean verbose)
	{
		init(inFile, null, X_POS, Y_POS, verbose);
	}
	/**
	* Initiate VTT with specifying VTT File object, configuration file, and
	* verbose flag.
	*
	* @param inFile  the Java File object of VTT file
	* @param configFile  the absolute path of VTT configuration file
	* @param verbose  flag of verbose on reading VTT file
	*/
	public static void init(File inFile, String configFile, boolean verbose)
	{
		init(inFile, configFile, X_POS, Y_POS, verbose);
	}
	/**
	* Initiate VTT with specifying VttObj Java object.
	*
	* @param vttObj  Java VttObj object
	*/
	public static void init(VttObj vttObj)
	{
		// add new VttObj
		if(vttObj != null)
		{
			vttObjs_.addElement(vttObj);
		}
	}
	/**
	* Initiate VTT with specifying VTT File object, configuration file, 
	* initial x position and y position of VTT window, and verbose flag.
	*
	* @param inFile  the Java File object of VTT file
	* @param configFile  the absolute path of VTT configuration file
	* @param xPos  initial x position of VTT window
	* @param yPos  initial y position of VTT window
	* @param verbose  flag of verbose on reading VTT file
	*/
	public static void init(File inFile, String configFile, int xPos, int yPos,
		boolean verbose)
	{
		VttObj vttObj = createVttObj(inFile, configFile, xPos, yPos, verbose);
		vttObjs_.addElement(vttObj);
	}
	/**
	* Clean up VttApi Java up. This method is used to close all opened VTT 
	* files and then close window.
	*/
	public static void cleanUp()
	{
		for(int i = 0; i < vttObjs_.size(); i++)
		{
			VttObj vttObj = vttObjs_.elementAt(i);
			FileOperations.exitOperation(vttObj.getMainFrame(), vttObj);
		}
	}
	/**
	* Add a VTT window without opening a VTT file.
	*/
	public static void add()
	{
		add(new String());
	}
	/**
	* Add a VTT window with opening a VTT file.
	*
	* @param inFileStr  the absolute path of VTT file
	*/
	public static void add(String inFileStr)
	{
		vttObjs_.addElement(createVttObj(new File(inFileStr)));
	}
	/**
	* Add a VTT window with opening a VTT file.
	*
	* @param inFile  the Java File object of VTT file
	*/
	public static void add(File inFile)
	{
		vttObjs_.addElement(createVttObj(inFile));
	}
	/**
	* Remove a VTT window with specifying an index.
	*
	* @param index  the index of vttObj list to be removed
	*/
	public static void removeAt(int index)
	{
		if((index >= 0) && (index < vttObjs_.size()))
		{
			vttObjs_.elementAt(index).getMainFrame().setVisible(false);
			vttObjs_.removeElementAt(index);
		}
	}
	/**
	* Get the size of all vttObj list (opened vtt file/window).
	*
	* @return  the size of all vttObj list
	*/
	public static int getSize()
	{
		return vttObjs_.size();
	}
	/**
	* Get the index by specifying vttObj Java object.
	*
	* @param vttObj  vttObj Java object of interested
	*
	* @return  the index of specified vttObj Java object
	*/
	public static int getIndexByVttObj(VttObj vttObj)
	{
		int index = -1;
		for(int i = 0; i < vttObjs_.size(); i++)
		{
			if(vttObjs_.elementAt(i) == vttObj)
			{
				index = i;
				break;
			}
		}
		return index;
	}
	/**
	* Get the index by specifying VTT File Java object.
	*
	* @param inFile  the Java File object of interested
	*
	* @return  the index of specified VTT File Java object
	*/
	public static int getIndexByFile(File inFile)
	{
		int index = -1;
		for(int i = 0; i < vttObjs_.size(); i++)
		{
			VttObj vttObj = vttObjs_.elementAt(i);
			try
			{
				if((vttObj.getDocFile() != null)	
				&& (vttObj.getDocFile().getCanonicalFile().equals(
					inFile.getCanonicalFile()) == true))
				{
					index = i;
					break;
				}
			}
			catch (Exception e)
			{
				System.err.println("** ERR@VttApi.GetIndexByFile() "
					+ e.toString()); 
				final String fMsg = e.getMessage();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
					}
				});
			}
		}
		return index;
	}
	/**
	* Get a boolean flag if the specified VTT Java File is opened.
	*
	* @param inFile  the Java File object of interested
	*
	* @return  a boolean flag if the specified VTT Java File is opened
	*/
	public static boolean contains(File inFile)
	{
		boolean flag = false;
		for(int i = 0; i < vttObjs_.size(); i++)
		{
			VttObj vttObj = vttObjs_.elementAt(i);
			try
			{
				if(vttObj.getDocFile().getCanonicalFile().equals(
					inFile.getCanonicalFile()) == true)
				{
					flag = true;
					break;
				}
			}
			catch (Exception e)
			{
				System.err.println("** ERR@VttApi.Contains() " + e.toString()); 
				final String fMsg = e.getMessage();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
					}
				});
			}
		}
		return flag;
	}
	/**
	* Get a VttObj Java object from VTT by specifying index.
	*
	* @param index  the index of intertested
	*
	* @return  a VttObj Java object from VTT by specifying index
	*/
	public static VttObj getVttObjAt(int index)
	{
		VttObj vttObj = null;
		if((index >= 0) && (index < vttObjs_.size()))
		{
			vttObj = vttObjs_.elementAt(index);
		}
		return vttObj;
	}
	
	/**
	* Get the last VttObj Java object from VTT. Please note that the
	* last vttObj is the latest one which is just added into the VTT.
	*
	* @return  the last VttObj Java object from VTT
	*/
	public static VttObj getLastVttObj()
	{
		int index = getSize() - 1;
		return getVttObjAt(index);
	}
	/**
	* Set focus at the specified VTT window by index. The focus VTT window 
	* will be bring up to the top.
	*
	* @param index  the index of intertested
	*/
	public static void setFocusAt(int index)
	{
		if((index >= 0) && (index < vttObjs_.size()))
		{
			// bring the right frame to front
			vttObjs_.elementAt(index).getMainFrame().toFront();
			// set textPane to focus so that keysControl will work
			vttObjs_.elementAt(index).getMainFrame().getMainPanel().getTextPane().requestFocus();
		}
	}
	/**
	* Convert information of all opened VTT files of VttApi to a String.
	*
	* @return the String format of a VttApi. This includes all opened VTT file.
	*/
	public static String _toString()
	{
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < vttObjs_.size(); i++)
		{
			VttObj vttObj = vttObjs_.elementAt(i);
			buf.append("vttObj[" + i + "]: " + vttObj + ", DocFile: "
				+ vttObj.getDocFile() + "\n");
		}
		return buf.toString();
	}
	// private methods
	private static VttObj createVttObj(String inFileStr)
	{
		return createVttObj(new File(inFileStr));
	}
	private static VttObj createVttObj(File inFile)
	{
		int xPos = X_POS;
		int yPos = Y_POS;
		// auto decide the xPos and yPos
		if(getSize() > 0)
		{
			int index = getSize() - 1;
			xPos = getVttObjAt(index).getMainFrame().getX() + OFFSET;
			yPos = getVttObjAt(index).getMainFrame().getY() + OFFSET;
		}
		
		return createVttObj(inFile, xPos, yPos, verbose_);
	}
	private static VttObj createVttObj(String inFileStr, int xPos, int yPos, 
		boolean verbose) 
	{
		File inFile = new File(inFileStr);
		return createVttObj(inFile, xPos, yPos, verbose);
	}
	private static VttObj createVttObj(File inFile, int xPos, int yPos, 
		boolean verbose) 
	{
		return createVttObj(inFile, null, xPos, yPos, verbose);
	}
	public static VttObj createVttObj(File inFile, String configFile, 
		int xPos, int yPos, boolean verbose) 
	{
		// check inFile
		String inFileName = inFile.toString();
		int inFileNameLength = inFileName.length();
		boolean inFileExist = inFile.exists();
		File finalInFile = null;	 // default: no input file specified
		if(inFileNameLength > 0)
		{
			// print out verbose information
			if(verbose == true)
			{
				// the specified file exist
				if(inFileExist == true)
				{
					System.out.println("-- Read in a file: " + inFile);
				}
				// the specified file does not exist
				else if(inFileExist == false)
				{
					System.out.println("-- In file: " + inFile 
							+ " does not exist.");
				}
			}
			// get the absolute path for the inFile
			try
			{
				finalInFile = inFile.getCanonicalFile();
			}
			catch(Exception e){}
		}
		// init Vtt Api
		VttObj vttObj = new VttObj(finalInFile, configFile, xPos, yPos);
		
		return vttObj;
	}
	// data members
	// This class is the very top level, so it can be static arrocss application
	private static Vector<VttObj> vttObjs_ = new Vector<VttObj>();
	
	private static boolean verbose_ = true;
	// location
	public final static int X_POS = 0;
	public final static int Y_POS = 50;
	private final static int OFFSET = 50;
	
}
