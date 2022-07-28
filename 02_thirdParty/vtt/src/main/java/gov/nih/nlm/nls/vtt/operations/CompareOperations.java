package gov.nih.nlm.nls.vtt.operations;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This class provides public methods for VTT comparison related operations.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class CompareOperations
{
	// private constructor
	private CompareOperations()
	{
	}
	// public methods
	/**
    * Compare source VTT file to target VTT file.
    *
    * @param owner  the owner of this AddTagDialog
    * @param vttObj  the vttObj Java object
    */
	public static void compareToFileOperation(JFrame owner, VttObj vttObj)
	{
		// step 1. check if any file open
		if(checkSourceFile(owner, vttObj) == true)
		{
			// step 2. choose target file
			File targetFile = chooseTargetFile(owner, vttObj);
			// step 3. compare source and target file
			if(targetFile != null)
			{
				compareToTargetFile(owner, vttObj, targetFile);
			}
		}
	}
	// private methods
	private static void compareToTargetFile(JFrame owner, VttObj vttObj,
		File targetFile)
	{
		// current source vttDocument
		VttDocument sourceVttDocument = vttObj.getVttDocument();
		// target vttDocument
		VttDocument targetVttDocument = new VttDocument();
		targetVttDocument.readFromFile(null, targetFile);
		// step 1: compare Text, 
		String sourceTextStr = sourceVttDocument.getText();
		String targetTextStr = targetVttDocument.getText();
		if(sourceTextStr.equals(targetTextStr) == false)
		{
			File sourceFile = vttObj.getDocFile();
			String title = 
				"VTT Compare: Different Text from Source and Target Files";
			String typeStr = "Text";
			DiffDialog textDiffDialog = new DiffDialog(owner, title, typeStr,
				sourceTextStr, targetTextStr, sourceFile, targetFile, vttObj);
			textDiffDialog.setVisible(true);
			return;	
		}
		// step 2: compare tags
		Vector<Tag> sourceTags = sourceVttDocument.getTags().getTags();
		Vector<Tag> targetTags = targetVttDocument.getTags().getTags();
		// use no header for tags string
		String sourceTagsStr = Tags.toString(sourceTags, false);
		String targetTagsStr = Tags.toString(targetTags, false);
		if(sourceTagsStr.equals(targetTagsStr) == false)
		{
			File sourceFile = vttObj.getDocFile();
			String title = 
				"VTT Compare: Different Tags from Source and Target Files";
			String typeStr = "Tags";
			TagsDiffDialog tagsDiffDialog = new TagsDiffDialog(owner, title, 
				typeStr, sourceTagsStr, targetTagsStr, sourceFile, targetFile, 
				vttObj);
			tagsDiffDialog.setVisible(true);
			return;	
		}
		// step 3: compare markups
		Vector<Markup> sourceMarkups 
			= sourceVttDocument.getMarkups().getMarkups();
		Vector<Markup> targetMarkups 
			= targetVttDocument.getMarkups().getMarkups();
		// use no header, no annotation for markups string
		String sourceMarkupsStr = Markups.toString(sourceMarkups, sourceTextStr,
			VttFormat.SIMPLEST_FORMAT, vttObj.getConfigObj(), false);
		String targetMarkupsStr = Markups.toString(targetMarkups, sourceTextStr,
			VttFormat.SIMPLEST_FORMAT, vttObj.getConfigObj(), false);
		if(sourceMarkupsStr.equals(targetMarkupsStr) == false)
		{
			File sourceFile = vttObj.getDocFile();
			String title = 
				"VTT Compare: Different Markups from Source and Target Files";
			String typeStr = "Markups";
			MarkupsDiffDialog markupsDiffDialog = new MarkupsDiffDialog(owner, 
				title, typeStr, sourceMarkupsStr, targetMarkupsStr, sourceFile,
				targetFile, vttObj);
			markupsDiffDialog.setVisible(true);
		}
		else	//
		{
			JOptionPane.showMessageDialog(owner,
				"Text, Tags, and Markups in Source file is identical to the target file!\n",
				"Compare to a file", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	private static File chooseTargetFile(JFrame owner, VttObj vttObj)
	{
		// display input file selection dialog
		File sourceFile = vttObj.getDocFile();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(vttObj.getConfigObj().getDocDir());
		fileChooser.setDialogTitle("Choose target file to compare");
		fileChooser.setMultiSelectionEnabled(false);
		// open a selected file
		File targetFile = null;
		if(fileChooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION)
		{
			targetFile = fileChooser.getSelectedFile();
			
			try
			{
				if(sourceFile.getCanonicalPath().equals(
					targetFile.getCanonicalPath()) == true)
				{
					int compareOption = JOptionPane.showConfirmDialog(owner,
						"Target file is the source file:\n"
						+ sourceFile,
						"Compare to a file", JOptionPane.OK_CANCEL_OPTION);
					
					// cancel option if the target is the same as source
					if(compareOption == JOptionPane.CANCEL_OPTION)
					{
						targetFile = null;
					}
				}
				else	// new file, open to a new frame
				{
					int compareOption = JOptionPane.showConfirmDialog(owner,
						"Compare source file: [" + sourceFile + "]\n"
						+ "   to target file: [" + targetFile + "].",
						"Compare to a file", JOptionPane.OK_CANCEL_OPTION);
					if(compareOption == JOptionPane.CANCEL_OPTION)
					{
						targetFile = null;
					}
				}
			}
			catch (IOException e)
			{
				System.err.println(
					"** Err@CompareOperations.ChooseTargetFile( ), " 
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
		return targetFile;
	}
	private static boolean checkSourceFile(JFrame owner, VttObj vttObj)
	{
		// flag
		boolean flag = true;
		// get current doc
		DefaultStyledDocument doc = (DefaultStyledDocument)
			vttObj.getMainFrame().getMainPanel().getStyledDocument();
		// no docFile is specified or empty doc, send warning message
		if((vttObj.getDocFile() == null)
		|| (doc.getLength() <= 0))
		{
			JOptionPane.showMessageDialog(owner,
				"No source file is opened! Can't compare!\n"
				+"Please open a file first!",
				"Compare to a file", JOptionPane.WARNING_MESSAGE);
			
			flag = false;
		}
		return flag;
	}
	// data member
}
