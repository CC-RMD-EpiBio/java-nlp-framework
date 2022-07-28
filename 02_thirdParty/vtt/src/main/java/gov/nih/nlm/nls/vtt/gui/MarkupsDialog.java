package gov.nih.nlm.nls.vtt.gui;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.util.*;
import java.io.*;

import gov.nih.nlm.nls.vtt.guiControl.*;
import gov.nih.nlm.nls.vtt.guiLib.*;
import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This class is the GUI dialog for all operations on markups. It includes 
* tabs of Logs, Reports and Undos.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class MarkupsDialog extends JDialog
{
	// public constructor 
	/**
    * Create a MarkupsDialog object for all operations on Markups.
    *
    * @param owner  the owner of this FindDialog
    * @param vttObj  the vttObj Java object
    */
	public MarkupsDialog(Window owner, VttObj vttObj)
	{
		super(owner);
		// init GUI controller & GUI compoment
		initGuiControllers(vttObj);
		initGuiComponents(owner);
	}
	
	
	// update GUI components
	/**
    * Set this dialog to be visible (show).
    *
    * @param owner  the owner of this FindDialog
    * @param index  the index of shown tab
    * @param vttObj  the vttObj Java object
    */
	public void show(Window owner, int index, VttObj vttObj)
	{
		// update Gui Vars
		tabbedPane_.setSelectedIndex(index); // assign selected index for tab
		updateGui(vttObj);
		setVisible(true);
	}
	/**
    * Set this dialog to be visible (show).
    *
    * @param owner  the owner of this FindDialog
    * @param vttObj  the vttObj Java object
    */
	public void show(Window owner, VttObj vttObj)
	{
		// update Gui Vars
		updateGui(vttObj);
		setVisible(true);
	}
	/**
    * Set this dialog to be not visible (not show).
    */
	public void notShow()
	{
		setVisible(false);
	}
	/**
    * Update GUI component: local variables and then update GUI.
    *
    * @param vttObj  the vttObj Java object
    */
	public void updateGui(VttObj vttObj)
	{
		updateGuiFromGlobal(vttObj);
	}
	// GUI Control: buttons control
	/**
    * Close button: close this dialog.
    */
	public void hitClose()
	{
		setVisible(false);
	}
	// private methods
	private void updateGuiFromGlobal(VttObj vttObj)
	{
		// update log panel
		updateMarkupsLog(vttObj);
		// report panel
		updateMarkupsReport(vttObj);
		// undo list panel
		updateUndoList(vttObj);
	}
	private void updateMarkupsLog(VttObj vttObj)
	{
		String text = vttObj.getVttDocument().getText();
		String log = Markups.toString(
			vttObj.getVttDocument().getMarkups().getMarkups(), text,
			vttObj.getConfigObj().getVttFormat(), 
			vttObj.getConfigObj());
		logText_.setText(log);
		// set caret postion to the beginning
		logText_.getCaret().setDot(0);
	}
	private void updateMarkupsReport(VttObj vttObj)
	{
		Vector<Tag> tags 
			= vttObj.getVttDocument().getTags().getTags();
		Vector<String> nameCategloryList
			= vttObj.getVttDocument().getTags().getNameCategoryList(false);
		Vector<String> reportStrs = Markups.getReportStrs(nameCategloryList,
			vttObj.getVttDocument().getMarkups().getMarkups());
		
		String reportStr = new String();
		for(int i = 0; i < reportStrs.size(); i++)
		{
			reportStr += reportStrs.elementAt(i);
		}
		DefaultStyledDocument doc =
			(DefaultStyledDocument) reportText_.getStyledDocument();
		// init the report str
		SimpleAttributeSet sas = new SimpleAttributeSet();
		try
		{
			doc.remove(0, doc.getLength());
			doc.insertString(doc.getLength(), reportStr, sas);
		}
		catch (BadLocationException ble)
		{
			System.err.println(
				"** Err@MarkupsDialog.UpdateMarkupReport()");
			final String fMsg = ble.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		// apply style to tags name-category
		// TBD
		int offset = 0;
		int length = 0;
		for(int i = 0; i < tags.size(); i++)
		{
			// set attribute
			Tag tag = tags.elementAt(i);
			int baseFontSize = vttObj.getConfigObj().getBaseFontSize();
			sas = VttDocument.setStyleConstants(tag, baseFontSize, 
				vttObj.getConfigObj().getZoomFactor());
			length = tag.getNameCategory().length();
			doc.setCharacterAttributes(offset, length, sas, false);
			offset += reportStrs.elementAt(i).length();
		}
		// set caret postion to the begining
		reportText_.setCaretPosition(0);
	}
	private void updateUndoList(VttObj vttObj)
	{
		String undoListStr = vttObj.getUndoManager().toString();
		undoText_.setText(undoListStr);
		// set caret postion to the beginning
		undoText_.getCaret().setDot(0);
	}
	private JPanel createLogPanel(VttObj vttObj)
	{
		JPanel logP = new JPanel();
		// top panel: Title
		JPanel topP = new JPanel();
		topP.add(new JLabel("Markups Log Details:"));
		// scroll pane for markup log
		updateMarkupsLog(vttObj);
		JScrollPane selectedSp = new JScrollPane(logText_);
		// add panel
		logP.setLayout(new BorderLayout());
		logP.add(topP, "North");
		logP.add(selectedSp, "Center");
		return logP;
	}
	private JPanel createReportPanel(VttObj vttObj)
	{
		JPanel reportP = new JPanel();
		// top panel: Title
		JPanel topP = new JPanel();
		topP.add(new JLabel("Markups Report:"));
		// update markup report
		updateMarkupsReport(vttObj);
		// add Panel
		reportP.setLayout(new BorderLayout());
		reportP.add(topP, "North");
		reportP.add(reportText_, "Center");
		return reportP;
	}
	private JPanel createUndoPanel(VttObj vttObj)
	{
		JPanel undoP = new JPanel();
		// top panel: Title
		JPanel topP = new JPanel();
		topP.add(new JLabel("Undo Manager Logs:"));
		// scroll pane for markup log
		updateUndoList(vttObj);
		JScrollPane selectedSp = new JScrollPane(undoText_);
		// add panel
		undoP.setLayout(new BorderLayout());
		undoP.add(topP, "North");
		undoP.add(selectedSp, "Center");
		return undoP;
	}
	private void initGuiControllers(VttObj vttObj)
	{
		// init controller
		markupsDialogControl_ = new MarkupsDialogControl(vttObj);
		// top panel
		closeB_.setActionCommand(B_CLOSE);
		closeB_.addActionListener(markupsDialogControl_);
		// log panel
		logP_ = createLogPanel(vttObj);
		logText_.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		logText_.setEditable(false);
		// report panel
		reportP_ = createReportPanel(vttObj);
		reportText_.setEditable(false);
		// undo panel
		undoP_ = createUndoPanel(vttObj);
		undoText_.setEditable(false);
	}
	private void initGuiComponents(Window owner)
	{
		// basic setup: title, modal
		setTitle("Markups");
		setModal(false); // set Modal to false so users can see change real time
		// Geometry Setting
		setLocationRelativeTo(owner);
		setMinimumSize(new Dimension(600, 500));
		// tabbed pane
		tabbedPane_.addTab("Logs", logP_); 
		tabbedPane_.addTab("Reports", reportP_); 
		tabbedPane_.addTab("Undos", undoP_); 
		// bottom panel: Close Button
		JPanel bottomP = new JPanel();
		bottomP.add(closeB_);
		// top level
		getContentPane().add(tabbedPane_, "Center");
		getContentPane().add(bottomP, "South");
	}
	// final variables
	private final static long serialVersionUID = 5L;
	/** Close button */
	public final static String B_CLOSE = "CLOSE";
	// data members
	// local vars
	// GUI: control
	private MarkupsDialogControl markupsDialogControl_ = null;
	// top panel
	private JTabbedPane tabbedPane_ = new JTabbedPane();
	private JButton closeB_ = new JButton("Close");
	// logs tab
	private JPanel logP_ = null;	// for dynamic updates
	private JTextArea logText_ = new JTextArea();
	// report tab
	private JPanel reportP_ = null;	// for dynamic updates
	private JTextPane reportText_ = new JTextPane();
	// logs tab
	private JPanel undoP_ = null;	// for dynamic updates
	private JTextArea undoText_ = new JTextArea();
}
