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
* This class is the GUI dialog for the selected Markup.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class MarkupDialog extends JDialog
{
	// public constructor 
	/**
    * Create a MarkupDialog object for the selected Markup.
    *
    * @param owner  the owner of this FindDialog
    * @param vttObj  the vttObj Java object
    */
	public MarkupDialog(Window owner, VttObj vttObj)
	{
		// constructor
		super(owner);
		// init GUI controller & GUI compoment
		initGuiControllers(vttObj);
		initGuiComponents(owner);
	}
	// update GUI components
	/**
    * Toggle show or not-show of this dialog.
    *
    * @param vttObj  the vttObj Java object
    */
	public void toggleShow(VttObj vttObj)
	{
		if(isVisible() == false)
		{
			doShow(vttObj);
		}
		else
		{
			doNotShow();
		}
	}
	/**
    * Set this dialog to be visible (show).
    *
    * @param vttObj  the vttObj Java object
    */
	public void doShow(VttObj vttObj)
	{
		updateGui(vttObj);
		setVisible(true);
	}
	/**
    * Set this dialog to be not visible (not show).
    */
	public void doNotShow()
	{
		setVisible(false);
	}
	/**
    * Update GUI component: local variables and then update GUI.
    *
    * @param vttObj  the vttObj Java object
    */
	public boolean updateGui(VttObj vttObj)
	{
		boolean flag = true;
		// update Gui Vars
		if(updateLocalVarsFromGlobal(vttObj) == false)
		{
			flag = false;
			Toolkit.getDefaultToolkit().beep();
		}
		else	// successful update local vars
		{
			updateGuiFromLocalVars(vttObj);
		}
		return flag;
	}
	// GUI Control: buttons control
	/**
    * Ok button: save Markup data and close this dialog.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitOk(VttObj vttObj)
	{
		if(updateLocalVarsFromGui() == true)
		{
			// update global markup
			MarkupOperations.changeSelectMarkupOperation(markup_, vttObj);
			// close 
			setVisible(false);
		}
	}
	/**
    * Apply button: apply Markup data.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitApply(VttObj vttObj)
	{
		if(updateLocalVarsFromGui() == true)
		{
			// update global markup
			if(MarkupOperations.changeSelectMarkupOperation(markup_, vttObj)
				== true)
			{
				// update gui
				updateGuiFromLocalVars(vttObj);
			}
		}
	}
	/**
    * Cancel button: close this dialog and ignore Markup data.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitCancel(VttObj vttObj)
	{
		// close 
		setVisible(false);
		// update caret to the end of doc
		SelectOperations.resetHighlight(vttObj);
	}
	// update GUI from local vars
	/**
    * Reset button: reset Markup data.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitReset(VttObj vttObj)
	{
		updateGuiFromLocalVars(vttObj);
	}
	/**
    * Delete button: delete the selected markup
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitDelete(VttObj vttObj)
	{
		// delete markup from list
		MarkupOperations.deleteOperation(vttObj);
	}
	// private methods
	// Update local vars from GUI input
	private boolean updateLocalVarsFromGui()
	{
		boolean flag = true;
		// update data from gui to markup
		int offset = Integer.parseInt(startTf_.getText());
		int end = Integer.parseInt(endTf_.getText());
		int length = end - offset;
		// check if lengh is > 0
		if(length <= 0)
		{
			flag = false;
			// popup error message at the center
			JOptionPane.showMessageDialog(null,
				"Error: The end position must be > starting position!",
				"Illegal start/end position", JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			String tagName = (String) tagNameCb_.getSelectedItem();
			String tagCategory = (String) tagCategoryCb_.getSelectedItem();
			String annotation = annotationTf_.getText();
			markup_ = new Markup(offset, length, tagName, tagCategory,
				annotation);
		}
		return flag;
	}
	// Update local vars from global object
	private boolean updateLocalVarsFromGlobal(VttObj vttObj)
	{
		boolean flag = true;
		int markupIndex = vttObj.getVttDocument().getMarkups().getSelectIndex();
		if(markupIndex < 0)
		{
			markup_ = null;
			flag = false;
		}
		else if (markupIndex >= vttObj.getVttDocument().getMarkups().getSize())
		{
			markup_ = new Markup();
		}
		else
		{
			markup_ = new Markup(
				vttObj.getVttDocument().getMarkups().getSelectMarkup());
		}
		return flag;
	}
	// update GUI from local vars
	private void updateGuiFromLocalVars(VttObj vttObj)
	{
		// get local markup
		int start = markup_.getOffset(); 
		int length = markup_.getLength();
		int end = start + length;
		String annotation = markup_.getAnnotation();
		String text = "[" 
			+ vttObj.getVttDocument().getText().substring(start, end) + "]";
		// update center panel
		// update text, start, end, annotation
		textTf_.setText(text);
		startTf_.setText(String.valueOf(start));
		endTf_.setText(String.valueOf(end));
		annotationTf_.setText(annotation);
		// Update tag
		Vector<Tag> tags = vttObj.getVttDocument().getTags().getTags();
		String markupTagName = markup_.getTagName();
		String markupTagCategory = markup_.getTagCategory();
		// combo box for selected tag name
		// Don't show 1 reserved Tag, Text/Clear, in the pull down menu
		Vector<TagFilter> nameList 
			= vttObj.getVttDocument().getTags().getNameList();
		tagNameCb_.removeAllItems();
		for(int i = VttDocument.TAG_START_INDEX; i < nameList.size(); i++)
		{
			int ii = i - VttDocument.TAG_START_INDEX;
			String name = nameList.elementAt(i).getName();
			tagNameCb_.addItem(name);
			if(name.equals(markupTagName) == true)
			{
				tagNameCb_.setSelectedIndex(ii);
			}
		}
		// combo box for selected tag properties
		Vector<TagFilter> categoryList 
			= vttObj.getVttDocument().getTags().getCategoryList();
		tagCategoryCb_.removeAllItems();
		for(int i = 0; i < categoryList.size(); i++)
		{
			String category = categoryList.elementAt(i).getName();
			tagCategoryCb_.addItem(category);
			if(category.equals(markupTagCategory) == true)
			{
				tagCategoryCb_.setSelectedIndex(i);
			}
		}
	}
	// create GUI compoment
	private JPanel createMarkupPanel()
	{
		JPanel markupP = new JPanel();
		// top panel: Title
		JPanel topP = new JPanel();
		topP.add(new JLabel("Modify Markup:"));
		// center panel
		JPanel centerP = new JPanel();
		centerP.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Selected Markup"));
		GridBagLayout gbl = new GridBagLayout();
		centerP.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);    // external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		// tagged text
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		centerP.add(new JLabel("Tagged Text :"), gbc);
		GridBag.setPosSize(gbc, 1, 0, 1, 1);
		centerP.add(textTf_, gbc);
		// Tags
		GridBag.setPosSize(gbc, 0, 1, 1, 1);
		centerP.add(new JLabel("Tag Name :"), gbc);
		GridBag.setPosSize(gbc, 1, 1, 1, 1);
		centerP.add(tagNameCb_, gbc);
		GridBag.setPosSize(gbc, 0, 2, 1, 1);
		centerP.add(new JLabel("Tag Category :"), gbc);
		GridBag.setPosSize(gbc, 1, 2, 1, 1);
		centerP.add(tagCategoryCb_, gbc);
		// start pos
		GridBag.setPosSize(gbc, 0, 3, 1, 1);
		centerP.add(new JLabel("Start Pos :"), gbc);
		GridBag.setPosSize(gbc, 1, 3, 1, 1);
		centerP.add(startTf_, gbc);
		// End Pos
		GridBag.setPosSize(gbc, 0, 4, 1, 1);
		centerP.add(new JLabel("End Pos :"), gbc);
		GridBag.setPosSize(gbc, 1, 4, 1, 1);
		centerP.add(endTf_, gbc);
		GridBag.setPosSize(gbc, 0, 5, 1, 1);
		centerP.add(new JLabel("Annotation :"), gbc);
		GridBag.setPosSize(gbc, 1, 5, 1, 1);
		centerP.add(annotationTf_, gbc);
		// add Panel
		markupP.setLayout(new BorderLayout());
		markupP.add(topP, "North");
		markupP.add(centerP, "Center");
		return markupP;
	}
	private JPanel createButtonPanel()
	{
		// markup panel: text Field
		textTf_.setEditable(false);
		// bottom panel: Ok, Cancel, Delete Buttons
		JPanel buttonP = new JPanel();
		buttonP.add(okB_);
		buttonP.add(cancelB_);
		buttonP.add(applyB_);
		buttonP.add(resetB_);
		buttonP.add(deleteB_);
		return buttonP;
	}
	private void initGuiComponents(Window owner)
	{
		// basic setup: title, modal
		setTitle("Markup");
		setModal(false);	// set Modal to false so users can see change
		// Geometry Setting
		setLocationRelativeTo(owner);
		//setLocation(650, 100);
		setMinimumSize(new Dimension(400, 320));
		// top level
		getContentPane().add(createMarkupPanel(), "Center");
		getContentPane().add(createButtonPanel(), "South");
	}
	private void initGuiControllers(VttObj vttObj)
	{
		// init Controller
		markupDialogControl_ = new MarkupDialogControl(vttObj);
		// bottom panel, buttons
		okB_.setActionCommand(B_OK);
		cancelB_.setActionCommand(B_CANCEL);
		applyB_.setActionCommand(B_APPLY);
		resetB_.setActionCommand(B_RESET);
		deleteB_.setActionCommand(B_DELETE);
		okB_.addActionListener(markupDialogControl_);
		cancelB_.addActionListener(markupDialogControl_);
		applyB_.addActionListener(markupDialogControl_);
		resetB_.addActionListener(markupDialogControl_);
		deleteB_.addActionListener(markupDialogControl_);
	}
	// final variables
	private final static long serialVersionUID = 5L;
	/** Ok button */
	public final static String B_OK = "OK";
	/** Cancel button */
	public final static String B_CANCEL = "CANCEL";
	/** Apply button */
	public final static String B_APPLY = "APPLY";
	/** Reset button */
	public final static String B_RESET = "RESET";
	/** Delete button */
	public final static String B_DELETE = "DELETE";
	// data members
	// local vars
	private Markup markup_ = null;	// local var, for reset button
	// GUI: control
	private MarkupDialogControl markupDialogControl_  = null;
	// center panel: for dynamic updates
	private JTextField textTf_ = new JTextField(20);	// text
	private JComboBox tagNameCb_ = new JComboBox();	// tags name
	private JComboBox tagCategoryCb_ = new JComboBox();	// tags category
	private JTextField startTf_ = new JTextField(20);	// start
	private JTextField endTf_ = new JTextField(20);	// end
	private JTextField annotationTf_ = new JTextField(20);	// annotations
	// bottom panel
	private JTabbedPane tabbedPane_ = new JTabbedPane();
	private JButton okB_ = new JButton("Ok");
	private JButton cancelB_ = new JButton("Cancel");
	private JButton applyB_ = new JButton("Apply");
	private JButton resetB_ = new JButton("Reset");
	private JButton deleteB_ = new JButton("Delete");
}
