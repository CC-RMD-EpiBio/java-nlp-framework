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
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This class is the GUI dialog for all operations on MetaData. 
* It includes tags file and fiel history.
* 
* <p><b>History:</b>
* <ul>
* <li>SCR-94, chlu, 02-03-10, new VTT file format to include Meta-Data
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class MetaDataDialog extends JDialog
{
	// public constructor 
	/**
    * Create a MetaDataDialog object for all Meta Data by specifying owner 
	* and vttObj.
    *
    * @param owner  the owner of this MetaDataDialog
    * @param vttObj  the vttObj Java object
    */
	public MetaDataDialog(Window owner, VttObj vttObj)
	{
		// constructor
		super(owner);
		// init controller & GUI compoment
		initGuiControllers(vttObj);
		initGuiComponents(owner, vttObj);
	}
	// update GUI components
	/**
    * Set MetaDataDialog to be visible (show).
    *
    * @param owner  the owner of this MetaDataDialog
    * @param index  the index of specified tag
    * @param vttObj  the vttObj Java object
    */
	public void show(Window owner, int index, VttObj vttObj)
	{
		// update Gui Vars
		tabbedPane_.setSelectedIndex(index); // assign selected index for tab
		updateGui(vttObj);
		updateTabs(vttObj);	// must have, to make displayP works
		setVisible(true);
	}
	/**
    * Set MetaDataDialog to be visible (show).
    *
    * @param owner  the owner of this MetaDataDialog
    * @param vttObj  the vttObj Java object
    */
	public void show(Window owner, VttObj vttObj)
	{
		// update Gui Vars
		updateGui(vttObj);
		updateTabs(vttObj);	// must have, to make displayP works
		setVisible(true);
	}
	/**
    * Set MetaDataDialog to be not visible (not show).
    */
	public void notShow()
	{
		setVisible(false);
	}
	/**
    * Update all tabs in the dialog.
	*
    * @param vttObj  the vttObj Java object
    */
	public void updateTabs(VttObj vttObj)
	{
		// update the checkbox in display panel
		int tabIndex = tabbedPane_.getSelectedIndex();
		switch(tabIndex)
		{
			case 0: // tags file tab
				tagsFileP_ = createTagsFilePanel();
				tabbedPane_.setComponentAt(0, tagsFileP_);
				break;
			case 1:	// save history tab
				fileHistoryP_ = createFileHistoryPanel(vttObj);
				tabbedPane_.setComponentAt(1, fileHistoryP_);
				break;
		}
	}
	/**
    * Update GUI component: local variables.
    *
    * @param vttObj  the vttObj Java object
    */
	public void updateGui(VttObj vttObj)
	{
		// no reset button, so no need to update to local vars
		updateGuiFromGlobal(vttObj);
	}
	// GUI Control: buttons control
	/**
    * The operation when hit Close button.
    */
	public void hitClose()
	{
		setVisible(false);
	}
	/**
    * The operation when hit select buttons.
	*
	* @param vttObj  the vttObj Java object
    */
	public void hitSelect(VttObj vttObj)
	{
		File tagsFile = new File(tagsFileText_.getText());
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(tagsFile);
		fileChooser.setSelectedFile(tagsFile);
		fileChooser.setDialogTitle("Select Tags File");
		fileChooser.setMultiSelectionEnabled(false);
		// import tag from a file
		if(fileChooser.showDialog(this, "Set") 
			== JFileChooser.APPROVE_OPTION)
		{
			// update JTextField
			tagsFileText_.setText(
				fileChooser.getSelectedFile().getAbsolutePath());
		}
	}
	/**
    * The operation when hit reset buttons.
	*
	* @param vttObj  the vttObj Java object
    */
	public void hitReset(VttObj vttObj)
	{
		// no reset button, so no need to update to local vars
		updateGuiFromGlobal(vttObj);
	}
	/**
    * The operation when hit cancel buttons.
	*
	* @param vttObj  the vttObj Java object
    */
	public void hitCancel(VttObj vttObj)
	{
		hitClose();
	}
	/**
    * The operation when hit ok buttons.
	*
	* @param vttObj  the vttObj Java object
    */
	public void hitOk(VttObj vttObj)
	{
		updateGlobalFromGui(vttObj);
		hitClose();
	}
	/**
	* This method updates from Gui to Global for diaplay filter.
	*
	* @param vttObj  the vttObj Java object
    */
	public void updateGlobalFromGui(VttObj vttObj)
	{
		vttObj.getVttDocument().getMetaData().setTagsFile(
			tagsFileText_.getText());
		vttObj.getVttDocument().getMetaData().setConfirmation(
			confirmationCb_.isSelected());
	}
	// private methods
	private void updateGuiFromGlobal(VttObj vttObj)
	{
		// tags file panel
		updateTagsFilePanel(vttObj);
		// File history panel
		updateFileHistoryPanel(vttObj);
	}
	// update Tags File panel
	private void updateTagsFilePanel(VttObj vttObj)
	{
		tagsFileText_.setText(
			vttObj.getVttDocument().getMetaData().getTagsFile());
		confirmationCb_.setSelected(
			vttObj.getVttDocument().getMetaData().getConfirmation());
	}
	private void updateFileHistoryPanel(VttObj vttObj)
	{
		String fileHistoryStr 
			= vttObj.getVttDocument().getMetaData().getFileHistoryStr(true);
		fileHistoryText_.setText(fileHistoryStr);
		fileHistoryText_.getCaret().setDot(0);
	}
	// create GUI Compoment
	private JPanel createFileHistoryPanel(VttObj vttObj)
	{
		JPanel fileHistoryP = new JPanel();

		// top panel: Title
		JPanel topP = new JPanel();
		topP.add(new JLabel("File History:"));

		// scroll pane for file history
		updateFileHistoryPanel(vttObj);
		JScrollPane centerP = new JScrollPane(fileHistoryText_);

		// button
		JPanel buttonP = new JPanel();
		buttonP.add(closeB_);

		// add Panel
		fileHistoryP.setLayout(new BorderLayout());
		fileHistoryP.add(topP, "North");
		fileHistoryP.add(centerP, "Center");
		fileHistoryP.add(buttonP, "South");
		return fileHistoryP;
	}
	private JPanel createTagsFilePanel()
	{
		JPanel tagsFileP = new JPanel();
		// top panel: Title
		JPanel topP = new JPanel();
		topP.add(new JLabel("Tags Definition File Options"));

		// center panel
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);	// external padding
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;

		JPanel centerP = new JPanel();
		centerP.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Loading Tags"));
		centerP.setPreferredSize(new Dimension(570, 120));
		centerP.setLayout(new GridBagLayout());
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		//centerP.add(new JLabel("Tags File: "), gbc);
		centerP.add(selectB_, gbc);
		GridBag.setPosSize(gbc, 1, 0, 1, 1);
		centerP.add(tagsFileText_, gbc);
		GridBag.setPosSize(gbc, 0, 1, 2, 1);
		//centerP.add(selectB_, gbc);
		//GridBag.SetPosSize(gbc, 1, 1, 1, 1);
		centerP.add(confirmationCb_, gbc);

		// button panel
		JPanel buttonP = new JPanel();
		buttonP.setLayout(new GridBagLayout());

		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		buttonP.add(okB_, gbc);
		GridBag.setPosSize(gbc, 1, 0, 1, 1);	
		buttonP.add(cancelB_, gbc);
		GridBag.setPosSize(gbc, 2, 0, 1, 1);	
		buttonP.add(resetB_, gbc);

		// add Panel
		tagsFileP.add(topP, "North");
		tagsFileP.add(centerP, "Center");
		tagsFileP.add(buttonP, "South");
		return tagsFileP;
	}
	private void initGuiControllers(VttObj vttObj)
	{
		// control
		metaDataDialogControl_ = new MetaDataDialogControl(vttObj);
		// top panel
		tabbedPane_.addChangeListener(metaDataDialogControl_);
		// tags file panel
		tagsFileText_ = new JTextField(
			vttObj.getVttDocument().getMetaData().getTagsFile());
		tagsFileText_.setColumns(40);
		tagsFileText_.setHorizontalAlignment(JTextField.LEFT);
		confirmationCb_.setSelected(
			vttObj.getVttDocument().getMetaData().getConfirmation());
		selectB_.setActionCommand(B_SELECT);
		okB_.setActionCommand(B_OK);
		cancelB_.setActionCommand(B_CANCEL);
		resetB_.setActionCommand(B_RESET);
		selectB_.addActionListener(metaDataDialogControl_);
		okB_.addActionListener(metaDataDialogControl_);
		cancelB_.addActionListener(metaDataDialogControl_);
		resetB_.addActionListener(metaDataDialogControl_);
		tagsFileP_ = createTagsFilePanel();
		// file history
		closeB_.setActionCommand(B_CLOSE);
		closeB_.addActionListener(metaDataDialogControl_);
		fileHistoryP_ = createFileHistoryPanel(vttObj);
	}
	private void initGuiComponents(Window owner, VttObj vttObj)
	{
		// basic setup: title, modal
		setTitle("Meta Data");
		setModal(true);
		// Geometry Setting
		setLocationRelativeTo(owner);
		setMinimumSize(new Dimension(600, 350));
		// tabbed pane
		tabbedPane_.addTab("Tags File", tagsFileP_); 
		tabbedPane_.addTab("File History", fileHistoryP_); 
		// top level
		getContentPane().add(tabbedPane_, "Center");
		// release state change to true after init view objects for update
		metaDataDialogControl_.setUpdateStateChange(true);
	}
	// final vairables
	private final static long serialVersionUID = 5L;
	// top level
	// Save history tab
	/** Close button */
	public final static String B_CLOSE = "CLOSE";
	// Tags file tab
	/** SET button */
	public final static String B_SELECT = "SELECT";
	/** OK button */
	public final static String B_OK = "OK";
	/** Cancel button */
	public final static String B_CANCEL = "CANCEL";
	/** Reset button */
	public final static String B_RESET = "RESET";
	// data members
	private boolean firstTime_ = true;
	// GUI: control
	private MetaDataDialogControl metaDataDialogControl_ = null; 
	// top panel
	private JTabbedPane tabbedPane_ = new JTabbedPane();
	// save history tab
	private JPanel fileHistoryP_ = null;	// for dynamic updates
	private JTextArea fileHistoryText_ = new JTextArea();
	private JButton closeB_ = new JButton("Close");
	// tab file tab
	private JPanel tagsFileP_ = null;	// for dynamic updates
	private JTextField tagsFileText_ = null;
	private JButton selectB_ = new JButton("Tags File");
	private JCheckBox confirmationCb_ = new JCheckBox("Confirmation");
	private JButton okB_ = new JButton("Ok");
	private JButton cancelB_ = new JButton("Cancel");
	private JButton resetB_ = new JButton("Reset");
}
