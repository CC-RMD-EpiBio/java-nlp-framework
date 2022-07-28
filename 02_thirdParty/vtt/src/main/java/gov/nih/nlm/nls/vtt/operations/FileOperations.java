package gov.nih.nlm.nls.vtt.operations;
import java.io.*;

import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;

import org.apache.commons.io.FileUtils;

import gov.nih.nlm.nls.vtt.api.*;
import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.util.ReadExcel;
import gov.nih.nlm.nls.vtt.util.UnderlineHighlighter;
import gov.nih.nlm.nls.vtt.util.WriteExcel;
/*****************************************************************************
* This class provides public methods for file related operations.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class FileOperations 
{
	public static String word;
	public static Highlighter highlighter = new UnderlineHighlighter(null);
	
	// private constructor
	private FileOperations()
	{
	}
	// public methods
	/**
    * Open a VTT file operations with a file choose dialog.
    *
    * @param owner  the owner of this AddTagDialog
    * @param vttObj  the vttObj Java object
    */
	public static void openOperation (final JFrame owner, VttObj vttObj)
	{
		// display input file selection dialog
		JFileChooser fileChooser = new JFileChooser();
		if(vttObj.getDocFile() == null)
		{
			fileChooser.setCurrentDirectory(vttObj.getConfigObj().getDocDir());
		}
		else
		{
			fileChooser.setSelectedFile(vttObj.getDocFile());
		}
		fileChooser.setDialogTitle("Open a file");
		fileChooser.setMultiSelectionEnabled(false);
		// open a selected file
		if (fileChooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {

			File newFile = fileChooser.getSelectedFile();

			openFile(owner, newFile, vttObj);	
		}
	}

	/**
	    * Open a VTT file operation.
	    * If it is an excel file, create snippet markups.
	    *
	    * @param newFile  the file to be open
	    * @param vttObj  the vttObj Java object
	    */
	public static void openFile(final JFrame owner, File newFile, VttObj vttObj) {
		if (newFile.getName().endsWith("xlsx")
				|| newFile.getName().endsWith("xls")) {
			String file = newFile.getAbsolutePath();
			ReadExcel rdexl = new ReadExcel();
			rdexl.setInputFile(file);
			try {
				ReadExcel.FileContentsAndMarkups fileContentsAndMarkups = rdexl.read();
				if (fileContentsAndMarkups.getFileContents() != null) {
					openFile(owner, fileContentsAndMarkups.getFileContents(), newFile, vttObj,
							fileContentsAndMarkups.getMarkups());
				}
			} catch (final Exception e) {
				e.printStackTrace();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(
								owner,
								"Failed to open excel file:\n"
										+ e.toString(), "File Exception",
								JOptionPane.ERROR_MESSAGE);
					}
				});
			}
		} else {
			openFile(owner, null, newFile, vttObj, null);
		}
	}	
	
	/**
    * Open a VTT file operation. This method includes:
	* <ul> 
	* <li>if file exists: move the frame to the front and set focus
	* <li>if no file opened in current panel: open it
	* <li>if some files opened and this is a new file, open to a new frame
	* </ul> 
    *
    * @param newFile  the file to be open
    * @param vttObj  the vttObj Java object
    * @param snippetColumnMarkups additional markups for snippets, may be null.
    */
	private static void openFile(JFrame owner, String fileContents, File newFile, VttObj vttObj, Markups snippetColumnMarkups)
	{
		// if file exists, move the frame to the front and set focus
		int index = VttApi.getIndexByFile(newFile);
		if(index > -1)	// contain this file already
		{
			VttApi.setFocusAt(index);
		}
		// no file opened in current panel, open it
		else if(vttObj.getDocFile() == null)
		{
			int curIndex = VttApi.getIndexByVttObj(vttObj);
			loadFileToVtt(owner, fileContents, newFile, VttApi.getVttObjAt(curIndex), snippetColumnMarkups);
			// update modeless Markups dialog
			MarkupOperations.updateModelessDialog(vttObj);
		}
		else	// new file, open to a new frame
		{
			VttApi.add();
			loadFileToVtt(owner, fileContents, newFile, VttApi.getLastVttObj(), snippetColumnMarkups);
		}
	}

	/**
    * Close VTT file operations. This method includes:
	* <ul>
	* <li>if no VTT file open, send out warning message
	* <li>if the VTT file changed, save then close it
	* <li>if the VTT file not changed, close it
	* </ul>
    *
    * @param owner  the owner of this AddTagDialog
    * @param vttObj  the vttObj Java object
    */
	public static void closeOperation(JFrame owner, VttObj vttObj)
	{
		// get current doc
		DefaultStyledDocument doc = (DefaultStyledDocument) 
			vttObj.getMainFrame().getMainPanel().getStyledDocument();
		// no docFile is specified or empty doc, send warning message
		if((vttObj.getDocFile() == null)
		|| (doc.getLength() <= 0))
		{
			JOptionPane.showMessageDialog(owner, 
				"No text (file) is opened! Can't close!",
				"Close a file", JOptionPane.WARNING_MESSAGE);
		}
		else	// check changes, then close
		{
			// if the data has been changed, show dialog for save
			if(vttObj.getVttDocument().hasChanged(
				vttObj.getDocFile(), vttObj) == true)
			{
				saveCloseDialog(owner, vttObj);
			}
			else	// close file
			{
				closeFileOperation(vttObj);
			}
		}
	}
	/**
    * Save a VTT file operations. This method includes:
	* <ul>
	* <li>if no VTT file open, send out warning message
	* <li>if the file has VTT extension, save it
	* <li>if the file does not has extension, save it with VTT extension
	* </ul>
    *
    * @param owner  the owner of this AddTagDialog
    * @param vttObj  the vttObj Java object
    */
	public static void saveOperation(JFrame owner, VttObj vttObj)
	{
		// get current doc
		DefaultStyledDocument doc = (DefaultStyledDocument) 
			vttObj.getMainFrame().getMainPanel().getStyledDocument();
		// no docFile is specified/opened, send warning message
		if((vttObj.getDocFile() == null)
		|| (doc.getLength() <= 0))
		{
			JOptionPane.showMessageDialog(owner, 
				"Empty text! Please open a text (file) first!",
				"Save a file", JOptionPane.WARNING_MESSAGE);
		}
		else // docFile is opened
		{
			// prompt dialog for saving file
			if(vttObj.getDocFile().getAbsolutePath().endsWith(
				VttDocument.FILE_EXT) == true)
			{
				saveVttFileDialog(owner, vttObj);
			}
			else	// if the extension is not .vtt
			{
				saveNonVttFileDialog(owner, vttObj);
			}
		}
	}
	/**
    * Save a VTT file to a specified file name operations. This method includes:
	* <ul>
	* <li>if no VTT file open, send out warning message
	* <li>if the file exists, save it to a specified file name
	* </ul>
    *
    * @param owner  the owner of this AddTagDialog
    * @param vttObj  the vttObj Java object
    */
	public static void saveAsOperation(JFrame owner, VttObj vttObj)
	{
		// get current doc
		DefaultStyledDocument doc = (DefaultStyledDocument) 
			vttObj.getMainFrame().getMainPanel().getStyledDocument();
		// no docFile is specified or empty doc, send warning message
		if((vttObj.getDocFile() == null)
		|| (doc.getLength() <= 0))
		{
			JOptionPane.showMessageDialog(owner, 
				"Empty text! Please open a text (file) first!",
				"Save a file as", JOptionPane.WARNING_MESSAGE);
		}
		else	// provide Save As dialog
		{
			saveAsDialog(owner, vttObj);
		}
	}
	
	/**
	    * Save a VTT file to a excel file. This method includes:
		* <ul>
		* <li>if no VTT file open, send out warning message
		* <li>if the file exists, save it to a specified file name
		* </ul>
	    *
	    * @param owner  the owner of this AddTagDialog
	    * @param vttObj  the vttObj Java object
	    */
		public static void saveAsExcelOperation(JFrame owner, VttObj vttObj)
		{
			// get current doc
			DefaultStyledDocument doc = (DefaultStyledDocument) 
				vttObj.getMainFrame().getMainPanel().getStyledDocument();
			// no docFile is specified or empty doc, send warning message
			if((vttObj.getDocFile() == null)
			|| (doc.getLength() <= 0))
			{
				JOptionPane.showMessageDialog(owner, 
					"Empty text! Please open a text (file) first!",
					"Save a file as", JOptionPane.WARNING_MESSAGE);
			}
			else	// provide Save As dialog
			{
				saveAsExcelDialog(owner, vttObj);
			}
		}
		
	
	/**
    * Print the textPane operations. This method includes:
	* <ul>
	* <li>if empty doc, send out warning message
	* <li>if the file exists, print the textPane
	* </ul>
    *
    * @param vttObj  the vttObj Java object
    */
	public static void printOperation(VttObj vttObj)
	{
		if(vttObj.getMainFrame().getMainPanel().getTextPane() == null)
		{
			System.err.println("** ERR@FileOperations.FilePrint(): MainPanel.GetTextPane() is null."); 
		}
		// check if it is empty doc
		if(vttObj.getMainFrame().getMainPanel().getStyledDocument().getLength() == 0)
		{
			JOptionPane.showMessageDialog(null, "Can't print empty file.",
				"Warning: Printing job", JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			// print out
			try
			{
				// Change the font size to 10 to fit into one line
				// create a new doc
				// find how much the font size adjusting factor 
				Vector<Tag> tags = vttObj.getVttDocument().getTags().getTags();
				int maxFontSize = 0;
				int baseFontSize = vttObj.getConfigObj().getBaseFontSize();
				for(int i = 0; i < tags.size(); i++)
				{
					int fontSize = tags.elementAt(i).getFontSize(baseFontSize);
					if(fontSize > maxFontSize)
					{
						maxFontSize = fontSize;
					}
				}
				int fondSizeFactor = 10 - maxFontSize;
				// font size too big, not fit into one page
				int selectOption = JOptionPane.YES_OPTION;
				if(fondSizeFactor < 0)
				{
					selectOption = JOptionPane.showConfirmDialog(null, 
						"Font sizes are too big. Some lines may be wrapped.\nAutomatic adjust on font size for this print job?",
						"Warning: Printing job", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
						
				}
				// font size too small, may not see well
				else if(fondSizeFactor > 0)
				{
					selectOption = JOptionPane.showConfirmDialog(null, 
						"Font sizes are too small. Some lines may be not read clearly.\nAutomatic adjust on font size for this print job?",
						"Warning: Printing job", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
				}
				// set teh fondSizeFactor to 0 if user does not auto adjust
				if(selectOption == JOptionPane.NO_OPTION)
				{
					fondSizeFactor = 0;
				}
					
				// Set the style of text to doc 
				DefaultStyledDocument doc = new DefaultStyledDocument();
				VttDocument.updateStyleForText(vttObj, doc, vttObj.getVttDocument(),
					vttObj.getConfigObj(), fondSizeFactor);
				// Set the style of markup to doc 
				VttDocument.updateStyleForMarkups(doc, vttObj.getVttDocument(), 
					vttObj.getConfigObj(), vttObj, fondSizeFactor);
					
				// Use textPane to print
				JTextPane textPane = new JTextPane(doc);
				textPane.print();	// display the print dialog and then print
			}
			catch(SecurityException se)
			{
				System.err.println("** ERR@FileOperations.FilePrint(): Security error: " + se.toString());
				final String fMsg = se.getMessage();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(null, fMsg, "Security Exception", JOptionPane.ERROR_MESSAGE);
					}
				});
			}
			catch(PrinterException pe)
			{
				System.err.println("** ERR@FileOperations.FilePrint(): Printing error: " + pe.toString());
				final String fMsg = pe.getMessage();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(null, fMsg, "Printer Exception", JOptionPane.ERROR_MESSAGE);
					}
				});
			}
			catch(Exception e)
			{
				System.err.println("** ERR@FileOperations.FilePrint(): other error: " + e.toString());
				final String fMsg = e.getMessage();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
					}
				});
			}
		}
	}
	/**
    * Exit VTT application operations. This method includes:
	* <ul>
	* <li>Close all VTT files (frames)
	* <li>Close help documents frame
	* </ul>
    *
    * @param owner  the owner of this AddTagDialog
    * @param vttObj  the vttObj Java object
    */
	public static void exitOperation(JFrame owner, VttObj vttObj)
	{
		// close exit current frame
		int exitOption = exitFileOperation(owner, vttObj);
		
		// dispose, close exit all frames, if not cancelled
		if(exitOption != JOptionPane.CANCEL_OPTION)
		{
			for(int i = VttApi.getSize()-1; i >= 0; i--)
			{
				VttApi.setFocusAt(i);	// set focus
				VttObj curVttObj = VttApi.getVttObjAt(i);
				if(exitFileOperation(curVttObj.getMainFrame(), curVttObj)
					== JOptionPane.CANCEL_OPTION)
				{
					break;	// if cancelled, stop exiting
				}
			}
		}
		// dispose the help document
		if(vttObj.getHelpDoc() != null)
		{
			vttObj.getHelpDoc().dispose();
		}
	}
	/**
	* Load a file to VTT application.
    *
    * @param newFile  the file to open
    * @param vttObj  the vttObj Java object
    */
	public static void loadFileToVtt(final JFrame owner, final String fileContents, final File newFile, final VttObj vttObj, final Markups snippetColumnMarkups)
	{
		if (fileContents != null) {
			// Load from string instead of file
			vttObj.setDocFile(newFile);
			vttObj.getConfigObj().setDocDir(
					vttObj.getDocFile().getParentFile());

				// init document
				boolean readFlag = 
						vttObj.getVttDocument().readFromFile(fileContents, vttObj.getDocFile());
				if(readFlag == true)
				{
					// update selection color in MainPanel.textPane
					TextDisplayOperations.setHighlightColors(vttObj);
					// update the text/doc with style
					TextDisplayOperations.updateStyle(
						TextDisplayOperations.TEXT_MARKUPS, vttObj);
					// update caret to the begining
					vttObj.getMainFrame().getMainPanel().setCaretPosition(0);
					// update mainFrame title	
					vttObj.getMainFrame().updateTitle(vttObj);
					// reset caret by mouse
					vttObj.getMainFrame().getMainPanel().setUseCaretUpdateByMouse(
						true);
					if (snippetColumnMarkups!=null) {
						for (int i = 0; (i< snippetColumnMarkups.getSize()); i++) {
							Markup markup = snippetColumnMarkups.getMarkup(i);
							if (markup.getOffset() + markup.getLength() > vttObj.getVttDocument().getText().length()) {
								String warnMsg = 
										"** Warning: excluding snippet column markup with illegal text range.  Doc text length = " + vttObj.getVttDocument().getText().length() + ".  Markup = " + markup.toString();
								System.err.println(warnMsg);
							} else {
								vttObj.getVttDocument().getMarkups().addMarkup(markup);
							}
						}
					}
				}else{
					vttObj.setDocFile(null);	// reset the docFile
				}

		} else if((newFile != null)
		&& (newFile.exists() == true))
		{
			vttObj.setDocFile(newFile);
			vttObj.getConfigObj().setDocDir(
					vttObj.getDocFile().getParentFile());

			// init document
			SwingWorker<Object, Object> sw = new SwingWorker<Object, Object>() {
				@Override
				protected Object doInBackground() throws Exception {
					try {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								if (owner != null) {
									owner.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
									owner.setEnabled(false);
								}
							}
						});
						boolean readFlag = false;
						if (vttObj.getDocFile().getName().endsWith(".xls") || vttObj.getDocFile().getName().endsWith(".xlsx")) {
							ReadExcel re = new ReadExcel();
							re.setInputFile(vttObj.getDocFile().getAbsolutePath());
							ReadExcel.FileContentsAndMarkups fcam = re.read();
							vttObj.getVttDocument().setText(fcam.getFileContents());
							vttObj.getVttDocument().setMarkups(fcam.getMarkups());
							readFlag = true;
						} else {
							readFlag = 
								vttObj.getVttDocument().readFromFile(fileContents, vttObj.getDocFile());
						}
						if(readFlag == true)
						{
							// update selection color in MainPanel.textPane
							TextDisplayOperations.setHighlightColors(vttObj);
							// update the text/doc with style
							TextDisplayOperations.updateStyle(
								TextDisplayOperations.TEXT_MARKUPS, vttObj);
							// update caret to the begining
							vttObj.getMainFrame().getMainPanel().setCaretPosition(0);
							// update mainFrame title	
							vttObj.getMainFrame().updateTitle(vttObj);
							// reset caret by mouse
							vttObj.getMainFrame().getMainPanel().setUseCaretUpdateByMouse(
								true);
							if (snippetColumnMarkups!=null) {
								for (int i = 0; (i< snippetColumnMarkups.getSize()); i++) {
									Markup markup = snippetColumnMarkups.getMarkup(i);
									vttObj.getVttDocument().getMarkups().addMarkup(markup);							
								}
							}
						}else{
							vttObj.setDocFile(null);	// reset the docFile
						}
					} finally {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								if (owner != null) {
									owner.setEnabled(true);
									owner.setCursor(Cursor.getDefaultCursor());
								}
							}
						});
					}
					return null;
				}
			};
			sw.execute();
			try {
				sw.get();
			} catch (InterruptedException | ExecutionException e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(null,
								"Error when loading file: " + newFile.toString() + "\n" + e.getMessage(),
								"Vtt Input File Error", JOptionPane.ERROR_MESSAGE);
					}
				});
			}
		}
		else if(newFile != null)	// in file not exist
		{
			System.err.println("** ERR@FileOperations.LoadFileToVtt( )");
			System.err.println("** In file [" + newFile + "] can't be found!");
			// send out error dialog
			JOptionPane.showMessageDialog(null,
				"Error: Input files does not exist!\n" + newFile.toString(),
				"Vtt Input File Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	// private methods
	private static void saveNonVttFileDialog(JFrame owner, VttObj vttObj)
	{
		String fileName = vttObj.getDocFile().getName();
		// save dialog for chosing file to be saved
		int saveOption = JOptionPane.showConfirmDialog(owner, 
			"File: " + vttObj.getDocFile().getAbsolutePath()
			+ " does not have .vtt extension.\n"
			+ "To save as:\n"
			+ "* " + fileName + ": click Yes\n"
			+ "* " + fileName + ".vtt: click No\n",
			"Save to a file", JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.INFORMATION_MESSAGE);
			
		// save file in not .vtt format	
		if(saveOption == JOptionPane.YES_OPTION)
		{
			// Save operations
			VttDocument.saveFile(vttObj.getDocFile(), vttObj.getVttDocument(),
				vttObj.getConfigObj().getVttFormat(), vttObj);
		}
		// provide save as dialog with file in .vtt format
		else if(saveOption == JOptionPane.NO_OPTION)
		{
			saveAsDialog(owner, vttObj);
		}
	}
	private static void saveAsDialog(JFrame owner, VttObj vttObj)
	{
		// add .vtt extension if not .vtt file
		vttObj.setDocFile( 
			VttDocument.updateDocFileNameToVtt(vttObj.getDocFile()));
		// save as dialog
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setSelectedFile(vttObj.getDocFile());
		fileChooser.setDialogTitle("Save File As");
		fileChooser.setMultiSelectionEnabled(false);
		if(fileChooser.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION)
		{
			// save text, tags, markup to file.vtt
			vttObj.setDocFile(fileChooser.getSelectedFile());
			VttDocument.saveFile(vttObj.getDocFile(), vttObj.getVttDocument(),
				vttObj.getConfigObj().getVttFormat(), vttObj);
			// update mainFrame title	
			vttObj.getMainFrame().updateTitle(vttObj);
		}
	}
	
	private static void saveAsExcelDialog(JFrame owner, VttObj vttObj) 
	{
		// add .vtt extension if not .vtt file
		//vttObj.SetDocFile( 
			//VttDocument.UpdateDocFileNameToVtt(vttObj.GetDocFile()));
		// save as dialog
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(vttObj.getDocFile().getParentFile());
		fileChooser.setFileFilter(new FileNameExtensionFilter("Microsoft Excel", "xls", "xlsx"));
		fileChooser.setSelectedFile(new File(vttObj.getDocFile().getName().replace(".vtt",  "")+".xlsx"));
		fileChooser.setDialogTitle("Export To Excel");
		fileChooser.setMultiSelectionEnabled(false);
		if(fileChooser.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION)
		{
			// save text, tags, markup to file.xlsx
			//vttObj.SetDocFile(fileChooser.getSelectedFile());
			//VttDocument.SaveFile(vttObj.GetDocFile(), vttObj.GetVttDocument(),
				//vttObj.GetConfigObj().GetVttFormat(), vttObj);
			WriteExcel wrtexcel = new WriteExcel();
			try {
				Document d = vttObj.getMainFrame().getMainPanel().getTextPane().getDocument();
			    String content = d.getText(0, d.getLength());
				wrtexcel.setInputFile(content, vttObj.getVttDocument().getMarkups());
				wrtexcel.setOutputFile(fileChooser.getSelectedFile());
				try {
					File excel = wrtexcel.write();
					//Markups mrkups = new Markups();
					
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(owner,  "Unable to export to Excel.  Please make sure the file is not open by another application.", "Error exporting to Excel", JOptionPane.ERROR_MESSAGE);
				}
				} catch (BadLocationException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(owner,  e.getMessage(), "Error exporting to Excel", JOptionPane.ERROR_MESSAGE);
				}
			// update mainFrame title	
			vttObj.getMainFrame().updateTitle(vttObj);
		}
	}
	
	private static void saveVttFileDialog(JFrame owner, VttObj vttObj)
	{
		// save dialog for chosing file to be saved
		int saveOption = JOptionPane.showConfirmDialog(owner, 
			"Save to file: " 
			+ vttObj.getDocFile().getAbsolutePath() + "?",
			"Save to a file", JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE);
			
		if(saveOption == JOptionPane.OK_OPTION)
		{
			// Save operations
			VttDocument.saveFile(vttObj.getDocFile(), vttObj.getVttDocument(),
				vttObj.getConfigObj().getVttFormat(), vttObj);
		}
	}
	private static void saveCloseDialog(JFrame owner, VttObj vttObj)
	{
		// add .vtt extension
		vttObj.setDocFile( 
			VttDocument.updateDocFileNameToVtt(vttObj.getDocFile()));
		int closeOption = JOptionPane.showConfirmDialog(owner, 
			"Save changes to file " 
			+ vttObj.getDocFile().getAbsolutePath() + "?",
			"Save/Close current file", JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE);
			
		if(closeOption == JOptionPane.YES_OPTION)
		{
			// save then close
			VttDocument.saveFile(vttObj.getDocFile(), vttObj.getVttDocument(),
				vttObj.getConfigObj().getVttFormat(), vttObj);
			closeFileOperation(vttObj);
		}
		else if(closeOption == JOptionPane.NO_OPTION)
		{
			// close
			closeFileOperation(vttObj);
		}
	}
	private static int saveExitDialog(JFrame owner, VttObj vttObj)
	{
		// add .vtt extension
		vttObj.setDocFile( 
			VttDocument.updateDocFileNameToVtt(vttObj.getDocFile()));
		int exitOption = JOptionPane.showConfirmDialog(owner, 
			"Save changes to file " 
			+ vttObj.getDocFile().getAbsolutePath() + "?",
			"Save/Exit current file", JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE);
			
		if(exitOption == JOptionPane.YES_OPTION)
		{
			// save, then close
			VttDocument.saveFile(vttObj.getDocFile(), vttObj.getVttDocument(),
				vttObj.getConfigObj().getVttFormat(), vttObj);
			closeFrame(vttObj);
		}
		else if(exitOption == JOptionPane.NO_OPTION)
		{
			// close
			closeFrame(vttObj);
		}
		return exitOption;
	}
	// close frame if more than 1, otherwise, just close the Doc
	private static void closeFileOperation(VttObj vttObj)
	{
		// remove the current frame when there are more than 1 frames
		if(VttApi.getSize() > 1)
		{
			closeFrame(vttObj);
		}
		else	 // just close the doc
		{
			// close
			DefaultStyledDocument doc = (DefaultStyledDocument) 
				vttObj.getMainFrame().getMainPanel().getStyledDocument();
			VttDocument.closeFile(doc, vttObj.getVttDocument(), vttObj);
			// reset undos
			vttObj.getUndoManager().reset();
			// for debug purpose, update MarkupsDialog
			vttObj.getMarkupsDialog().updateGui(vttObj);
			// update mainFrame title	
			vttObj.getMainFrame().updateTitle(vttObj);
		}
	}
	private static int exitFileOperation(JFrame owner, VttObj vttObj)
	{
		int exitOption = JOptionPane.YES_OPTION;
		// get current doc
		DefaultStyledDocument doc = (DefaultStyledDocument) 
			vttObj.getMainFrame().getMainPanel().getStyledDocument();
		// no docFile is specified or empty doc, Close the frame
		if((vttObj.getDocFile() == null)
		|| (doc.getLength() <= 0))
		{
			closeFrame(vttObj);
		}
		else	// docFile is opened, check change, then close
		{
			// some changes since last save, save/close
			if(vttObj.getVttDocument().hasChanged(
				vttObj.getDocFile(), vttObj) == true)
			{
				exitOption = saveExitDialog(owner, vttObj);
			}
			else	// no changes since last saved, close it
			{
				closeFrame(vttObj);
			}
		}
		return exitOption;
	}
	private static void closeFrame(VttObj vttObj)
	{
		// dispose the frame
		int index = VttApi.getIndexByVttObj(vttObj);
		VttApi.removeAt(index);
		vttObj.getMainFrame().dispose();	// dispose the frame to exit
	}
	// data member
}
