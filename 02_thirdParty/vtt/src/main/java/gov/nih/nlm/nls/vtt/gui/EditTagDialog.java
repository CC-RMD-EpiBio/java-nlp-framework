package gov.nih.nlm.nls.vtt.gui;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.util.*;
import java.lang.*;

import gov.nih.nlm.nls.vtt.guiControl.*;
import gov.nih.nlm.nls.vtt.guiLib.*;
import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This class is the GUI dialog for editing a selected tag.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class EditTagDialog extends JDialog
{
	// public constructor 
	/**
    * Create an EditTagDialog object to edit tags by specifying owner 
	* and vttObj.
    *
    * @param owner  the owner of this AddTagDialog
    * @param vttObj  the vttObj Java object
    */
	public EditTagDialog(Window owner, VttObj vttObj)
	{
		// constructor
		super(owner);
		// Init control
		initGuiControllers(vttObj);
		initGuiComponents(owner);
	}
		
	// public methods
	// Update GUI Components
	/**
    * Set EditTagDialog to be visible (show).
    *
    * @param owner  the owner of this AddTagDialog
    * @param srcTagIndex  the index of the tag to be edited
    * @param vttObj  the vttObj Java object
    */
	public void show(Window owner, int srcTagIndex, VttObj vttObj)
	{
		// update local vars from global vars
		updateLocalVarsFromGlobal(srcTagIndex, vttObj);
		// update Gui vars
		updateGuiFromLocalVars(vttObj);
		setVisible(true);
	}
	/**
    * Set AddTagDialog to be not visible (not show).
    */
	public void notShow()
	{
		setVisible(false);
	}
	/**
    * Update GUI component: local variables and preview.
    *
    * @param vttObj  the vttObj Java object
    */
	public void updateGui(VttObj vttObj)
	{
		// update local vars from GUI
		if(updateGuiFlag_ == true)
		{
			if(updateLocalVarsFromGui(false) == true)
			{
				// update the preview panel
				updatePreviewText(vttObj);
			}
		}
	}
	// GUI GuiControl: Buttons control
	/**
    * The operation when hit OK button.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitOk(VttObj vttObj)
	{
		// update local vars
		if(updateLocalVarsFromGui(true) == true)
		{
			// update global tag operations, and close this dialog
			if(TagOperations.set(vttObj, tag_, srcTagIndex_) == true)
			{
				setVisible(false);
			}
		}
	}
	/**
    * The operation when hit Cancel button.
    */
	public void hitCancel()
	{
		setVisible(false);
	}
	/**
    * The operation when hit reset button.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitReset(VttObj vttObj)
	{
		// update local vars from global vars
		updateLocalVarsFromGlobal(srcTagIndex_, vttObj);
		//update Gui frimlocal vars
		updateGuiFromLocalVars(vttObj);
	}
	/**
    * The operation when hit EditTextColor button.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitEditTextColor(VttObj vttObj)
	{
		Color color = JColorChooser.showDialog(this, 
			"Text Color Editor", tag_.getForeground());
		if(color != null)
		{
			tag_.setForeground(color);
			updatePreviewText(vttObj);
		}
	}
	/**
    * The operation when hit EditBgColor button.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitEditBgColor(VttObj vttObj)
	{
		Color color = JColorChooser.showDialog(this, 
			"Background Color Editor", tag_.getBackground());
		if(color != null)
		{
			tag_.setBackground(color);
			updatePreviewText(vttObj);
		}
	}
	// private methods
	private boolean updateLocalVarsFromGui(boolean checkFlag)
	{
		// name & category
		String name = ((String) nameList_.getSelectedItem()).trim();
		String category = ((String) categoryList_.getSelectedItem()).trim();
		boolean flag = AddTagDialog.isLegalTagNameCategory(name, category, 
			checkFlag);
		// update local vars if name and category are legal
		if(flag == true)
		{
			// change name & category
			tag_.setName(name);
			tag_.setCategory(category);
			// font
			tag_.setFontFamily((String) fontList_.getSelectedItem());
			tag_.setFontSize(sizeTf_.getText());
			// effect
			tag_.setBold(boldCb_.isSelected());
			tag_.setItalic(italicCb_.isSelected());
			tag_.setUnderline(underlineCb_.isSelected());
			tag_.setDisplay(displayCb_.isSelected());
			// Color of foreground_ and background_ are updated in edit button
		}
		return flag;
	}
	private int findSelectedIndex(JComboBox comboBox, String itemName)
	{
		int selectedIndex = -1;
		ComboBoxModel comboBoxModel = comboBox.getModel();
		for(int i = 0; i < comboBoxModel.getSize(); i++)
		{
			String curName = (String) comboBoxModel.getElementAt(i);
			if(curName.equals(itemName) == true)
			{
				selectedIndex = i;
				break;
			}
		}
		return selectedIndex;
	}
	private void updatePreviewText(VttObj vttObj)
	{
		boolean viewAllTags = viewAllTagsCb_.isSelected();
		int baseFontSize = vttObj.getConfigObj().getBaseFontSize();
		// only view the editing tag
		if(viewAllTags == false)
		{
			DefaultStyledDocument doc =
				(DefaultStyledDocument) previewText_.getStyledDocument();
			String demoStr = "This is the preview of tag: " 
				+ tag_.getNameCategory() + " in text.";	
			SimpleAttributeSet sas = new SimpleAttributeSet();
			int offset = 28;
			try
			{
				doc.remove(0, doc.getLength());
				doc.insertString(doc.getLength(), demoStr, sas);
				Markup.doMarkUp(tag_, doc, offset, 
					tag_.getNameCategory().length(), baseFontSize, sas, false);
			}
			catch (BadLocationException ble)
			{
				System.err.println(
				"** Err: Couldn't insert initial text in tag preview panel");
				final String fMsg = ble.getMessage();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
					}
				});
			}
		}
		else	// view all tags
		{
			DefaultStyledDocument doc =
				(DefaultStyledDocument) previewText_.getStyledDocument();
			String demoStr = new String();
			Vector<Tag> tags = vttObj.getVttDocument().getTags().getTags();
			Vector<String> demoStrs = new Vector<String>();
			for(int i = 0; i < tags.size(); i++)
			{
				Tag tag = tags.elementAt(i);
				demoStrs.addElement("This is the preview of tag: " 
					+ tag.getNameCategory()
					+ " in text." + GlobalVars.LS_STR);
				demoStr += demoStrs.elementAt(i);	
			}
			try
			{
				// init doc
				SimpleAttributeSet sas = new SimpleAttributeSet();
				doc.remove(0, doc.getLength());
				doc.insertString(doc.getLength(), demoStr, sas);
				// markup the doc
				int offset = 28;
				for(int i = 0; i < tags.size(); i++)
				{
					Tag tag = tags.elementAt(i);
					
					// current edit tag
					if(tag.getNameCategory().equals(tag_.getNameCategory()) 
						== true)
					{
						Markup.doMarkUp(tag_, doc, offset, 
							tag_.getNameCategory().length(), baseFontSize,
							sas, true);
					}
					else	// not current editing tag
					{
						Markup.doMarkUp(tag, doc, offset, 
							tag.getNameCategory().length(), baseFontSize,
							sas, true);
					}
					offset += demoStrs.elementAt(i).length();
				}
			}
			catch (BadLocationException ble)
			{
				System.err.println(
				"** Err: Couldn't insert initial text in tag preview panel");
				final String fMsg = ble.getMessage();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
					}
				});
			}
		}
	}
	private void updateLocalVarsFromGlobal(int srcTagIndex, VttObj vttObj)
	{
		// init local vars
		srcTagIndex_ = srcTagIndex;
		tag_ 
			= new Tag(vttObj.getVttDocument().getTags().getTagAt(srcTagIndex_));
	}
	// update GUI from Control vars
	private void updateGuiFromLocalVars(VttObj vttObj)
	{
		// set to flase so no listener update Gui
		updateGuiFlag_ = false; 
		// name 
		Tags tags = vttObj.getVttDocument().getTags();
		Vector<TagFilter> nameList = tags.getNameList();
		nameList_.removeAllItems();
		for(int i = VttDocument.TAG_START_INDEX; i < nameList.size(); i++)
		{
			String name = nameList.elementAt(i).getName();
			nameList_.addItem(name);
		}
		int nameIndex = findSelectedIndex(nameList_, tag_.getName());
		if(nameIndex == -1)
		{
			nameIndex = 0;	// default: no name
		}
		nameList_.setSelectedIndex(nameIndex);
		// category
		Vector<TagFilter> categoryList = tags.getCategoryList(); 
		categoryList_.removeAllItems();
		for(int i = 0; i < categoryList.size(); i++)
		{
			String category = categoryList.elementAt(i).getName();
			categoryList_.addItem(category);
		}
		int categoryIndex = findSelectedIndex(categoryList_, 
			tag_.getCategory());
		if(categoryIndex == -1)
		{
			categoryIndex = 0;	// default: no category
		}
		categoryList_.setSelectedIndex(categoryIndex);
		// font
		int fontIndex = findSelectedIndex(fontList_, tag_.getFontFamily());
		if(fontIndex == -1)
		{
			fontIndex = 3;	// default: index for Monospaced
		}
		fontList_.setSelectedIndex(fontIndex);
		// size
		sizeTf_.setText(tag_.getFontSize());
		// effect
		boldCb_.setSelected(tag_.isBold());
		italicCb_.setSelected(tag_.isItalic());
		underlineCb_.setSelected(tag_.isUnderline());
		displayCb_.setSelected(tag_.isDisplay());
		// privew
		updatePreviewText(vttObj);
		// reset updateGuiFlag_
		updateGuiFlag_ = true;
	}
	private JPanel createCenterPanel()
	{
		JPanel centerP = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		centerP.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);    // external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		// center panel
		JPanel propertyP = new JPanel();
		propertyP.setLayout(gbl);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		propertyP.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Effect"));
		// tag name
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		propertyP.add(new JLabel("Name: "), gbc);
		GridBag.setPosSize(gbc, 1, 0, 1, 1);
		propertyP.add(nameList_, gbc);
		// tag category
		GridBag.setPosSize(gbc, 2, 0, 1, 1);
		propertyP.add(new JLabel("Category: "), gbc);
		GridBag.setPosSize(gbc, 3, 0, 1, 1);
		propertyP.add(categoryList_, gbc);
		// font	
		GridBag.setPosSize(gbc, 0, 1, 1, 1);
		propertyP.add(new JLabel("Font: "), gbc);
		GridBag.setPosSize(gbc, 1, 1, 1, 1);
		propertyP.add(fontList_, gbc);
		// size
		GridBag.setPosSize(gbc, 0, 2, 1, 1);
		propertyP.add(new JLabel("Size: "), gbc);
		GridBag.setPosSize(gbc, 1, 2, 1, 1);
		propertyP.add(sizeTf_, gbc);
		// color 
		GridBag.setPosSize(gbc, 2, 1, 1, 1);
		propertyP.add(new JLabel("Text Color: "), gbc);
		GridBag.setPosSize(gbc, 3, 1, 1, 1);
		propertyP.add(textColorB_, gbc);
		GridBag.setPosSize(gbc, 2, 2, 1, 1);
		propertyP.add(new JLabel("Background: "), gbc);
		GridBag.setPosSize(gbc, 3, 2, 1, 1);
		propertyP.add(bgColorB_, gbc);
		// bold, italic, underline
		GridBag.setPosSize(gbc, 0, 3, 1, 1);
		propertyP.add(boldCb_, gbc);
		GridBag.setPosSize(gbc, 1, 3, 1, 1);
		propertyP.add(italicCb_, gbc);
		GridBag.setPosSize(gbc, 2, 3, 1, 1);
		propertyP.add(underlineCb_, gbc);
		GridBag.setPosSize(gbc, 3, 3, 1, 1);
		propertyP.add(displayCb_, gbc);
		// preview Panel
		JPanel previewP = new JPanel();
		previewP.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Preview"));
		previewP.setLayout(new BorderLayout());
		previewP.add(viewAllTagsCb_, "North");
		JScrollPane tagSp = new JScrollPane(previewText_);
		tagSp.setPreferredSize(new Dimension(250, 100));
		previewP.add(tagSp, "Center");
		// add name, font, color to center panel
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		centerP.add(propertyP, gbc);
		GridBag.setPosSize(gbc, 0, 1, 1, 1);
		centerP.add(previewP, gbc);
		// add Panel
		return centerP;
	}
	private JPanel createButtonPanel()
	{
		// button panel: Ok, Cancel Button
		JPanel buttonP = new JPanel();
		buttonP.add(okB_);
		buttonP.add(cancelB_);
		buttonP.add(resetB_);
		return buttonP;
	}
	private void initGuiControllers(VttObj vttObj)
	{
		// init Controller
		editTagDialogControl_ = new EditTagDialogControl(vttObj);
		// top panel
		okB_.setActionCommand(B_OK);
		cancelB_.setActionCommand(B_CANCEL);
		resetB_.setActionCommand(B_RESET);
		okB_.addActionListener(editTagDialogControl_);
		cancelB_.addActionListener(editTagDialogControl_);
		resetB_.addActionListener(editTagDialogControl_);
		// property panel
		nameList_.addActionListener(editTagDialogControl_);
		nameList_.setEditable(true);
		categoryList_.addActionListener(editTagDialogControl_);
		categoryList_.setEditable(true);
		fontList_.addItem("Dialog");
		fontList_.addItem("DialogInput");
		fontList_.addItem("Helvetica");
		fontList_.addItem("Monospaced");
		fontList_.addItem("SansSerif");
		fontList_.addItem("Serif");
		fontList_.setSelectedIndex(0);
		fontList_.addActionListener(editTagDialogControl_);
		sizeTf_.addActionListener(editTagDialogControl_);
		boldCb_.addItemListener(editTagDialogControl_);
		italicCb_.addItemListener(editTagDialogControl_);
		underlineCb_.addItemListener(editTagDialogControl_);
		displayCb_.addItemListener(editTagDialogControl_);
		// color
		textColorB_.setActionCommand(B_TEXT_COLOR);
		bgColorB_.setActionCommand(B_BG_COLOR);
		textColorB_.addActionListener(editTagDialogControl_);
		bgColorB_.addActionListener(editTagDialogControl_);
		// preview panel
		previewText_.setEditable(false);
		viewAllTagsCb_.addItemListener(editTagDialogControl_);
	}
	private void initGuiComponents(Window owner)
	{
		// basic setup: title, modal
		setTitle("Tag Property Editing");
		setModal(true);
		// Geometry Setting
		setLocationRelativeTo(owner);
		setMinimumSize(new Dimension(500, 400));	// width, height
		// top level
		getContentPane().add(createCenterPanel(), "Center");
		getContentPane().add(createButtonPanel(), "South");
	}
	// final variables
	private final static long serialVersionUID = 5L;
	/** Ok button */
	public final static String B_OK = "OK";
	/** Cancel button */
	public final static String B_CANCEL = "CANCEL";
	/** Reset button */
	public final static String B_RESET = "RESET";
	/** Text color button */
	public final static String B_TEXT_COLOR = "TEXT_COLOR";
	/** Background color button */
	public final static String B_BG_COLOR = "BG_COLOR";
	// data members
	private Tag tag_ = null;	// local control vars
	private int srcTagIndex_ = -1;	// local vars
	private boolean updateGuiFlag_ = true;  // flag to update Gui
	// GUI: control
	private EditTagDialogControl editTagDialogControl_ = null;
	// top panel
	private JButton okB_ = new JButton("Ok");
	private JButton cancelB_ = new JButton("Cancel");
	private JButton resetB_ = new JButton("Reset");
	// center panel
	private JComboBox nameList_ = new JComboBox();
	private JComboBox categoryList_ = new JComboBox();
	private JComboBox fontList_ = new JComboBox();
	private JTextField sizeTf_ = new JTextField(2);
	private JButton textColorB_ = new JButton("Edit");
	private JButton bgColorB_ = new JButton("Edit");
	private JCheckBox boldCb_ = new JCheckBox("Bold");
	private JCheckBox italicCb_ = new JCheckBox("Italic");
	private JCheckBox underlineCb_ = new JCheckBox("Underline");
	private JCheckBox displayCb_ = new JCheckBox("Display");
	// preview Panel
	private JTextPane previewText_ = new JTextPane();
	private JCheckBox viewAllTagsCb_ = new JCheckBox("View all tags");
}
