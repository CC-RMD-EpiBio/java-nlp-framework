package gov.nih.nlm.nls.vtt.gui;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.util.*;
import java.lang.*;
import java.io.*;

import gov.nih.nlm.nls.vtt.guiControl.*;
import gov.nih.nlm.nls.vtt.guiLib.*;
import gov.nih.nlm.nls.vtt.libs.*;
import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This class is the GUI dialog for comparing VTT Text difference feature.  
* It is designed to be used for general comparison purpose with min. 
* modifications.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class DiffDialog extends JDialog
{
	// public constructor 
	/**
    * Create a DiffDialog object to compare text from source and target by 
	* specifying owner, title, typeStr, sourceDataStr, targetDataStr, 
	* sourceFile, targetFile, vttObj.
    *
    * @param owner  the owner of this DiffDialog
    * @param title  the title of this DiffDialog
    * @param typeStr  the type of comparison, such as "Text".
    * @param sourceDataStr  the source data in String format
    * @param targetDataStr  the target data in String format
    * @param sourceFile  the absolute path of source file
    * @param targetFile  the absolute path of target file
    * @param vttObj  the vttObj Java object
    */
	public DiffDialog(Window owner, String title, String typeStr,
		String sourceDataStr, String targetDataStr, File sourceFile, 
		File targetFile, VttObj vttObj)
	{
		super(owner);
		// init GUI controller & GUI compoment
		initGuiControl();
		// basic setup: title, modal
		setTitle(title);
		setModal(true);
		setMinimumSize(new Dimension(980, 550));
		// Geometry Setting
		setLocationRelativeTo(owner);
		// Init Gui set up
		targetFile_ = targetFile;
		vttObj_ = vttObj;
		
		// local data
		String diffStr = getDiffStr(sourceDataStr, targetDataStr);
		StringStats srcStrStats = StringStats.getStringStats(sourceDataStr);
		StringStats tarStrStats = StringStats.getStringStats(targetDataStr);
		DiffStats diffStats = DiffStats.getDiffStats(diffStr);
		diffStats.setLineNum(srcStrStats.getLineNum());
		Vector<Integer> srcLinePos = srcStrStats.getLinePos();
		Vector<Integer> srcChangeLines = diffStats.getSrcChangeLines();
		Vector<Integer> srcDeleteLines = diffStats.getSrcDeleteLines();
		Vector<Integer> tarLinePos = tarStrStats.getLinePos();
		Vector<Integer> tarChangeLines = diffStats.getTarChangeLines();
		Vector<Integer> tarAddLines = diffStats.getTarAddLines();
		// visual data
		sourceViewDataP_ = createViewTextPanel("Source " + typeStr + " Data", 
			sourceDataStr, srcLinePos, srcChangeLines, srcDeleteLines);
		targetViewDataP_ = createViewTextPanel("Target " + typeStr + " Data", 
			targetDataStr, tarLinePos, tarChangeLines, tarAddLines);
		// raw data
		sourceDataP_ = createDataTextPanel("Source " + typeStr + " Data", 
			sourceDataStr);
		targetDataP_ = createDataTextPanel("Target " + typeStr + " Data", 
			targetDataStr);
		// diff
		diffP_ = createDataTextPanel(typeStr + " Difference", diffStr);
		// report
		String reportStr = getReportStr(srcStrStats, tarStrStats, diffStats,
			sourceFile, targetFile, typeStr);
		reportP_ = createDataTextPanel(typeStr + " Comparison Report", 
			reportStr);
		// top level
		getContentPane().add(
			createCenterPanel(typeStr, sourceDataStr, targetDataStr, diffStats,
			vttObj), "Center");
		getContentPane().add(createButtonPanel(), "South");
	}
		
	// GUI Control: Buttons control
	/**
    * The operation when hit OK button.
	*/
	public void hitOk()
	{
		setVisible(false);
	}
	/**
    * The operation when hit Open button. This method is used to open
	* the compared source or target files.
	*/
	public void hitOpen()
	{
		FileOperations.openFile(null, targetFile_, vttObj_);
	}
	// private methods
	// GUI component
	private JTabbedPane createCenterPanel(String typeStr, String sourceDataStr,
		String targetDataStr, DiffStats diffStats, VttObj vttObj)
	{
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel viewDiffP = createViewDiffPanel(sourceDataStr, targetDataStr,
			diffStats, vttObj);
		String title = "View " + typeStr + " Diff";
		String title2 = "View Data Diff";
		if(viewDiffP != null)
		{
			tabbedPane.addTab(title, viewDiffP);
		}
		else
		{
			title2 = new String(title);
		}
		
		tabbedPane.addTab(title2, createViewDataDiffPanel());
		tabbedPane.addTab("Stats Reports", createReportPanel());
		tabbedPane.addTab("Difference", createDiffPanel());
		tabbedPane.addTab("Raw Data", createDataPanel());
		return tabbedPane;
	}
	// thid method shold be overrided when a customerized display is desired
	protected JPanel createViewDiffPanel(String sourceDataStr, 
		String targetDataStr, DiffStats diffStats, VttObj vttObj)
	{
		JPanel viewP = null;
		return viewP;
	}
	private JPanel createViewDataDiffPanel()
	{
		// Option panel
		JPanel viewP = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		viewP.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 3, 3, 3);    // external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		// Source & Target Data
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		viewP.add(sourceViewDataP_, gbc);
		GridBag.setPosSize(gbc, 1, 0, 1, 1);
		viewP.add(targetViewDataP_, gbc);
		return viewP;
	}
	private JPanel createReportPanel()
	{
		// Option panel
		JPanel reportP = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		reportP.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 3, 3, 3);    // external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		// Report
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		reportP.add(reportP_, gbc);
		return reportP;
	}
	private JPanel createDiffPanel()
	{
		// Option panel
		JPanel diffP = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		diffP.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 3, 3, 3);    // external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		// Diff
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		diffP.add(diffP_, gbc);
		return diffP;
	}
	private JPanel createDataPanel()
	{
		// Option panel
		JPanel dataP = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		dataP.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 3, 3, 3);    // external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		// Source & Target Data
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		dataP.add(sourceDataP_, gbc);
		GridBag.setPosSize(gbc, 1, 0, 1, 1);
		dataP.add(targetDataP_, gbc);
		return dataP;
	}
	// used to create visual effect text panel
	private JPanel createViewTextPanel(String title, String contentStr,
		Vector<Integer> linePos, Vector<Integer> changeLines, 
		Vector<Integer> addDeleteLines)
	{
		JPanel textP = new JPanel();
		textP.setMinimumSize(new Dimension(450, 450));
		// top panel: Title
		JPanel titleP = new JPanel();
		titleP.add(new JLabel(title));
		// text content
		JTextPane tp = new JTextPane();
		DefaultStyledDocument doc =
			(DefaultStyledDocument) tp.getStyledDocument();
		try
		{
			// insert text
			SimpleAttributeSet sas = new SimpleAttributeSet();
			doc.remove(0, doc.getLength());
			doc.insertString(doc.getLength(), contentStr, sas);
			// visual effects for change
			for(int i = 0; i < changeLines.size(); i++)
			{
				int lineNo = changeLines.elementAt(i);	// start from 1
				int startPos = StringStats.getLineStartPos(linePos, lineNo);
				int endPos = StringStats.getLineEndPos(linePos, lineNo);
				int length = endPos - startPos + 1;
				Markup.doMarkUp(changeTag_, doc, startPos, length, 12, sas, 
					true);
			}
			// visual effects for add/delete
			for(int i = 0; i < addDeleteLines.size(); i++)
			{
				int lineNo = addDeleteLines.elementAt(i);	// start from 1
				int startPos = StringStats.getLineStartPos(linePos, lineNo);
				int endPos = StringStats.getLineEndPos(linePos, lineNo);
				int length = endPos - startPos + 1;
				Markup.doMarkUp(addDeleteTag_, doc, startPos, length, 12, sas, 
					true);
			}
		}
		catch (BadLocationException e)
		{
			System.err.println(
				"** Err@DiffDialog.CreateViewTextPanel(): " + e.toString());
			final String fMsg = e.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		tp.setMargin(new Insets(5,5,5,5));
		tp.setCaretPosition(0);
		tp.setEditable(false);
		JScrollPane contentP = new JScrollPane(tp);
		contentP.setPreferredSize(new Dimension(430, 430));
		// add Panel
		textP.setLayout(new BorderLayout());
		textP.add(titleP, "North");
		textP.add(contentP, "Center");
		return textP;
	}
	// used to create pure text panel in the data panel
	private JPanel createDataTextPanel(String title, String contentStr)
	{
		JPanel textP = new JPanel();
		textP.setMinimumSize(new Dimension(450, 450));
		// top panel: Title
		JPanel titleP = new JPanel();
		titleP.add(new JLabel(title));
		// text content
		JTextArea ta = new JTextArea();
		ta.setText(contentStr);
		ta.setColumns(80);
		ta.setRows(30);
		ta.setLineWrap(true);
		ta.setEditable(false);
		ta.moveCaretPosition(0);	// set caret to the begining
		ta.setSelectionStart(0);	// set selection 
		ta.setSelectionEnd(0);		// set selection
		JScrollPane contentP = new JScrollPane(ta);
		// add Panel
		textP.setLayout(new BorderLayout());
		textP.add(titleP, "North");
		textP.add(contentP, "Center");
		return textP;
	}
	private JPanel createButtonPanel()
	{
		// button panel: Ok, Cancel Button
		JPanel buttonP = new JPanel();
		buttonP.add(okB_);
		buttonP.add(openB_);
		return buttonP;
	}
	private String getDiffStr(String sourceStr, String targetStr)
	{
		Vector<String> aLines = DiffUtil.getLinesFromStr(sourceStr);
		Vector<String> bLines = DiffUtil.getLinesFromStr(targetStr);
		String diffStr = DiffUtil.getDiffStr(aLines, bLines);
		return diffStr;
	}
	private String getReportStr(StringStats srcStrStats, 
		StringStats tarStrStats, DiffStats diffStats, File sourceFile, 
		File targetFile, String typeStr) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("-----------------------------------------");
		sb.append(GlobalVars.LS_STR);
		sb.append("- Source file: " + sourceFile);
		sb.append(GlobalVars.LS_STR);
		sb.append(srcStrStats.toString());
		sb.append(GlobalVars.LS_STR);
		sb.append("-----------------------------------------");
		sb.append(GlobalVars.LS_STR);
		sb.append("- Target file: " + targetFile);
		sb.append(GlobalVars.LS_STR);
		sb.append(tarStrStats.toString());
		sb.append(GlobalVars.LS_STR);
		sb.append("-----------------------------------------");
		sb.append(GlobalVars.LS_STR);
		sb.append("- Difference Stats: ");
		sb.append(GlobalVars.LS_STR);
		sb.append(diffStats.toString(typeStr + " (lines)"));
		sb.append(GlobalVars.LS_STR);
		return sb.toString();
	}
	private void initGuiControl()
	{
		diffDialogControl_ = new DiffDialogControl(this);
		// buttom panel
		okB_.setActionCommand(B_OK);
		openB_.setActionCommand(B_OPEN);
		okB_.addActionListener(diffDialogControl_);
		openB_.addActionListener(diffDialogControl_);
	}
	// final variables
	private final static long serialVersionUID = 5L;
	/** Ok button */
	public final static String B_OK = "OK";
	/** Open button */
	public final static String B_OPEN = "OPEN";
	/** tag for changed tags */
	protected final static Tag changeTag_ = new Tag("Change", new String(), 
		true, false, false, false, Color.BLACK, Color.YELLOW, "Dialog", "+0");
	/** tag for added/deleted tag */
	protected final static Tag addDeleteTag_ = new Tag("Add/Delete", 
		new String(), true, false, false, false, Color.BLACK, Color.PINK, 
		"Dialog", "+0");
	// data members
	private DiffDialogControl diffDialogControl_ = null;
	private File targetFile_ = null;
	private VttObj vttObj_ = null;
	// local control vars: dynamic updates
	private JPanel sourceViewDataP_ = null;
	private JPanel targetViewDataP_ = null;
	private JPanel sourceDataP_ = null;
	private JPanel targetDataP_ = null;
	private JPanel diffP_ = null;	// diff output str
	private JPanel reportP_ = null;	// stats on the diff comparison
	// buttom panel
	private JButton okB_ = new JButton("Ok");
	private JButton openB_ = new JButton("Open Target File");
}
