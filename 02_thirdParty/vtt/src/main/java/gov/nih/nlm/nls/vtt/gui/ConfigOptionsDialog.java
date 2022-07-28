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
import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This class is the GUI dialog for setting VTT configuration options.
* 
* <p><b>History:</b>
* <ul>
* <li>SCR-96, chlu, 02/01/10, add Username to VTT config file
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class ConfigOptionsDialog extends JDialog
{
	// public constructor 
	/**
	* Create a ConfigOptionsDialog object to setup configuration option
	* by specifying owner and vttObj.
	*
	* @param owner  the owner of this ConfigOptionsDialog
	* @param vttObj  the vttObj Java object
	*/
	public ConfigOptionsDialog(Window owner, VttObj vttObj)
	{
		// constructor
		super(owner);
		// init GUI controller & GUI compoment
		initGuiControllers(vttObj);
		initGuiComponents(owner);
	}
	// Update GUI Components
	/**
    * Set ConfigOptionsDialog to be visible (show).
    *
    * @param owner  the owner of this AddTagDialog
    * @param vttObj  the vttObj Java object
    */
	public void show(Window owner, VttObj vttObj) 
	{
		// update local variables from global
		tabbedPane_.setSelectedIndex(0);
		updateLocalVarsFromGlobal(vttObj);
		
		// update Gui from lcoal variables
		updateGuiFromLocalVars(vttObj);
		setVisible(true);
	}
	/**
    * Set ConfigOptionsDialog to be not visible (not show).
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
			updateLocalVarsFromGui(vttObj);
			// update Gui from lcoal variables
			updatePreviewText(vttObj);
		}
	}
	// GUI Control: Buttons control
	/**
    * The operation when hit OK button.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitOk(VttObj vttObj)
	{
		// update local var from Gui
		updateLocalVarsFromGui(vttObj);
		// set global operation
		ConfigOptionsOperations.setConfig(configObj_, vttObj);
		setVisible(false);
	}
	/**
    * The operation when hit Cancel button.
    */
	public void hitCancel()
	{
		setVisible(false);
	}
	/**
    * The operation when hit Save button. This method saves 
	* the VTT configuration file to ${VTT_DIR}/data/config/vtt.properties
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitSave(VttObj vttObj)
	{
		String configFile = vttObj.getConfigObj().getVttDir() 
			+ "data/config/vtt.properties";
		
		// save dialog
		int saveOption = JOptionPane.showConfirmDialog(this,
			"Save to configuration file: "
			+ configFile + "?",
			"Save to configuration file", JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.INFORMATION_MESSAGE);
		if(saveOption == JOptionPane.OK_OPTION)
		{
			// update to global vars
			vttObj.getConfigObj().setVarsFromConfigObj(configObj_);
			// save to file
			ConfigObj.saveFile(new File(configFile), vttObj.getConfigObj());
		}
	}
	/**
    * The operation when hit Reset button.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitReset(VttObj vttObj)
	{
		// update local variables from global
		updateLocalVarsFromGlobal(vttObj);
		
		// update Gui vars
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
			"Text Color Editor",
			configObj_.getSelectMarkupTag().getForeground());
		if(color != null)
		{
			configObj_.getSelectMarkupTag().setForeground(color);
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
			"Background Color Editor", 
			configObj_.getSelectMarkupTag().getBackground());
		if(color != null)
		{
			configObj_.getSelectMarkupTag().setBackground(color);
			updatePreviewText(vttObj);
		}
	}
	// private methods
	private void updateLocalVarsFromGlobal(VttObj vttObj)
	{
		configObj_ = new ConfigObj(vttObj.getConfigObj());
	}
	// update GUI from Local vars
	private void updateGuiFromLocalVars(VttObj vttObj)
	{
		// set to flase so no listener update Gui
		updateGuiFlag_ = false;
		// dir & files
		vttDirTf_.setText(configObj_.getVttDir());
		vttFileDirTf_.setText(configObj_.getDocDir().getPath());
		tagsFileTf_.setText(configObj_.getTagFile().getPath());
		userNameTf_.setText(configObj_.getUserName());
		// display options
		fileDisplayLengthTf_.setText(
			String.valueOf(configObj_.getFilePathDisplayLength()));
		baseFontSizeTf_.setText(
			String.valueOf(configObj_.getBaseFontSize()));
		zoomFactorTf_.setText(
			String.valueOf(configObj_.getZoomFactor()));
		vttFormatList_.setSelectedIndex(configObj_.getVttFormat());
		offsetSizeTf_.setText(String.valueOf(configObj_.getOffsetSize()));
		lengthSizeTf_.setText(String.valueOf(configObj_.getLengthSize()));
		tagNameSizeTf_.setText(String.valueOf(configObj_.getTagNameSize()));
		tagCategorySizeTf_.setText(String.valueOf(
			configObj_.getTagCategorySize()));
		// overwrite
		boldCb_.setSelected(configObj_.isOverwriteSelectBold());
		italicCb_.setSelected(configObj_.isOverwriteSelectItalic());
		underlineCb_.setSelected(
			configObj_.isOverwriteSelectUnderline());
		fontCb_.setSelected(configObj_.isOverwriteSelectFont());
		sizeCb_.setSelected(configObj_.isOverwriteSelectSize());
		textColorCb_.setSelected(
			configObj_.isOverwriteSelectTextColor());
		backgroundColorCb_.setSelected(
			configObj_.isOverwriteSelectBackgroundColor());
		// use text tag for not display select markup	
		useTextTagForNotDisplaySelectMarkupCb_.setSelected(
			configObj_.isUseTextTagForNotDisplaySelectMarkup());
		// attribute: bold
		Tag tag = configObj_.getSelectMarkupTag();
		String bold = Boolean.toString(tag.isBold());
		int boldIndex = findSelectedIndex(boldList_, bold);
		if(boldIndex == -1)	// no found
		{
			boldIndex = 0;	// default: false
		}
		boldList_.setSelectedIndex(boldIndex);
		// attribute: italic 
		String italic = Boolean.toString(tag.isItalic());
		int italicIndex = findSelectedIndex(italicList_, italic);
		if(italicIndex == -1)	// no found
		{
			italicIndex = 0;	// default: false
		}
		italicList_.setSelectedIndex(italicIndex);
		// attribute: underline 
		String underline = Boolean.toString(tag.isUnderline());
		int underlineIndex = findSelectedIndex(underlineList_, underline);
		if(underlineIndex == -1)	// no found
		{
			underlineIndex = 0;	// default: index for Monospaced
		}
		underlineList_.setSelectedIndex(underlineIndex);
		// attribute: font 
		String font = tag.getFontFamily();
		int fontIndex = findSelectedIndex(fontList_, font);
		if(fontIndex == -1)	// no found
		{
			fontIndex = 3;	// default: index for Monospaced
		}
		fontList_.setSelectedIndex(fontIndex);
		// attribute: size
		Tag markupTag = new Tag(configObj_.getSelectMarkupTag()); //local var
		sizeTf_.setText(markupTag.getFontSize());
		// no needs for updating colors
		// privew
		updatePreviewText(vttObj);
		// reset updateGuiFlag_
		updateGuiFlag_ = true;
	}
	// update value of local vars from Gui and then update Gui
	private void updateLocalVarsFromGui(VttObj vttObj)
	{
		// update dir & files
		configObj_.setDocDir(new File(vttFileDirTf_.getText()));
		configObj_.setUserName(userNameTf_.getText());
		// update options
		configObj_.setFilePathDisplayLength(Integer.parseInt(
			fileDisplayLengthTf_.getText()));
		configObj_.setBaseFontSize(Integer.parseInt(baseFontSizeTf_.getText()));
		configObj_.setZoomFactor(Integer.parseInt(zoomFactorTf_.getText()));
		configObj_.setVttFormat(vttFormatList_.getSelectedIndex());
		configObj_.setOffsetSize(Integer.parseInt(offsetSizeTf_.getText()));
		configObj_.setLengthSize(Integer.parseInt(lengthSizeTf_.getText()));
		configObj_.setTagNameSize(Integer.parseInt(tagNameSizeTf_.getText()));
		configObj_.setTagCategorySize(Integer.parseInt(
			tagCategorySizeTf_.getText()));
		// update overwrite
		configObj_.setOverwriteSelectBold(boldCb_.isSelected());
		configObj_.setOverwriteSelectItalic(italicCb_.isSelected());
		configObj_.setOverwriteSelectUnderline(underlineCb_.isSelected());
		configObj_.setOverwriteSelectFont(fontCb_.isSelected());
		configObj_.setOverwriteSelectSize(sizeCb_.isSelected());
		configObj_.setOverwriteSelectTextColor(textColorCb_.isSelected());
		configObj_.setOverwriteSelectBackgroundColor(
			backgroundColorCb_.isSelected());
		// update non display tag
		configObj_.setUseTextTagForNotDisplaySelectMarkup(
			useTextTagForNotDisplaySelectMarkupCb_.isSelected());
		// update attribute
		configObj_.getSelectMarkupTag().setBold(Boolean.parseBoolean(
			(String) boldList_.getSelectedItem()));
		configObj_.getSelectMarkupTag().setItalic(Boolean.parseBoolean(
			(String) italicList_.getSelectedItem()));
		configObj_.getSelectMarkupTag().setUnderline(Boolean.parseBoolean(
			(String) underlineList_.getSelectedItem()));
		configObj_.getSelectMarkupTag().setFontFamily(
			(String) fontList_.getSelectedItem());
		configObj_.getSelectMarkupTag().setFontSize(sizeTf_.getText());
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
		// view all tags when they are select
		DefaultStyledDocument doc =
			(DefaultStyledDocument) previewText_.getStyledDocument();
		String demoStr = new String();
		Vector<Tag> tags 
			= vttObj.getVttDocument().getTags().getTags();
		Vector<String> demoStrs = new Vector<String>();
		for(int i = 1; i < tags.size(); i++)
		{
			Tag tag = tags.elementAt(i);
			demoStrs.addElement("Markup: " + tag.getNameCategory() 
				+ " vs. selected: " + tag.getNameCategory() 
				+ GlobalVars.LS_STR);
			demoStr += demoStrs.elementAt(i-1);
		}
		try
		{
			// init doc
			SimpleAttributeSet sas = new SimpleAttributeSet();
			doc.remove(0, doc.getLength());
			doc.insertString(doc.getLength(), demoStr, sas);
			// markup the doc
			int lineOffset = 0;
			for(int i = 1; i < tags.size(); i++)
			{
				Tag tag = tags.elementAt(i);
				int size = tag.getNameCategory().length();
				int markupOffset = lineOffset + 8;
				int selectOffset = lineOffset + 23 + size;
				// regular markup
				int baseFontSize = configObj_.getBaseFontSize();
				Markup.doMarkUp(tag, doc, markupOffset, size, baseFontSize, 
					sas, true);
				// select & display markup
				int selectBaseFontSize = tag.getFontSize(baseFontSize);
				Tag selectTag = new Tag(
					VttDocument.getSelectTag(tag, configObj_, vttObj));
				Markup.doMarkUp(selectTag, doc, selectOffset, size, 
					selectBaseFontSize, sas, true);
				lineOffset += demoStrs.elementAt(i-1).length();
			}
		}
		catch (BadLocationException ble)
		{
			System.err.println(
			"** Err: Can't insert init text in markup options preview panel");
			final String fMsg = ble.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		previewText_.setCaretPosition(0);
	}
	// GUI component
	private JPanel createCenterPanel()
	{
		JPanel centerP = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		centerP.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);    // external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		// add select markup and preview panel to center panel
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		centerP.add(createVttSystemPanel(), gbc);
		GridBag.setPosSize(gbc, 0, 1, 1, 1);
		centerP.add(createOptionPanel(), gbc);
		GridBag.setPosSize(gbc, 0, 2, 1, 1);
		centerP.add(createSelectMarkupPanel(), gbc);
		GridBag.setPosSize(gbc, 0, 3, 1, 1);
		centerP.add(createPreviewPanel(), gbc);
		// add Panel
		return centerP;
	}
	private JPanel createVttSystemPanel()
	{
		// directory & File panel
		JPanel vttSystemP = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		vttSystemP.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);    // external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		vttSystemP.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "VTT System Options"));
		// vtt dir
		// Overwrites & attribute: bold
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		vttSystemP.add(new JLabel("Vtt directory: "), gbc);
		GridBag.setPosSize(gbc, 1, 0, 1, 1);
		vttSystemP.add(vttDirTf_, gbc);
		// tag file
		GridBag.setPosSize(gbc, 0, 1, 1, 1);
		vttSystemP.add(new JLabel("Tags file: "), gbc);
		GridBag.setPosSize(gbc, 1, 1, 1, 1);
		vttSystemP.add(tagsFileTf_, gbc);
		// file dir
		GridBag.setPosSize(gbc, 0, 2, 1, 1);
		vttSystemP.add(new JLabel("Data directory: "), gbc);
		GridBag.setPosSize(gbc, 1, 2, 1, 1);
		vttSystemP.add(vttFileDirTf_, gbc);
		// user name
		GridBag.setPosSize(gbc, 0, 3, 1, 1);
		vttSystemP.add(new JLabel("User Name: "), gbc);
		GridBag.setPosSize(gbc, 1, 3, 1, 1);
		vttSystemP.add(userNameTf_, gbc);
		return vttSystemP;
	}
	private JPanel createOptionPanel()
	{
		// Option panel
		JPanel optionP = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		optionP.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);    // external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		optionP.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Display & Other Options"));
		// filepath display length
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		optionP.add(new JLabel("File path display length in the title: "), gbc);
		GridBag.setPosSize(gbc, 1, 0, 1, 1);
		optionP.add(fileDisplayLengthTf_, gbc);
		GridBag.setPosSize(gbc, 0, 1, 1, 1);
		optionP.add(new JLabel("Base font size for text and tags: "), gbc);
		GridBag.setPosSize(gbc, 1, 1, 1, 1);
		optionP.add(baseFontSizeTf_, gbc);
		GridBag.setPosSize(gbc, 0, 2, 1, 1);
		optionP.add(new JLabel("Factor for zoom in/out: "), gbc);
		GridBag.setPosSize(gbc, 1, 2, 1, 1);
		optionP.add(zoomFactorTf_, gbc);
		GridBag.setPosSize(gbc, 0, 3, 1, 1);
		optionP.add(new JLabel("Vtt Format, lineup Markup fields 1-3 by: "), 
			gbc);
		GridBag.setPosSize(gbc, 1, 3, 1, 1);
		optionP.add(vttFormatList_, gbc);
		GridBag.setPosSize(gbc, 0, 4, 1, 1);
		optionP.add(new JLabel("Markup offset size in fixed format: "), gbc);
		GridBag.setPosSize(gbc, 1, 4, 1, 1);
		optionP.add(offsetSizeTf_, gbc);
		GridBag.setPosSize(gbc, 0, 5, 1, 1);
		optionP.add(new JLabel("Markup length size in fixed format: "), gbc);
		GridBag.setPosSize(gbc, 1, 5, 1, 1);
		optionP.add(lengthSizeTf_, gbc);
		GridBag.setPosSize(gbc, 0, 6, 1, 1);
		optionP.add(new JLabel("Markup tag name size in fixed format: "), gbc);
		GridBag.setPosSize(gbc, 1, 6, 1, 1);
		optionP.add(tagNameSizeTf_, gbc);
		GridBag.setPosSize(gbc, 0, 7, 1, 1);
		optionP.add(new JLabel("Markup tag category size in fixed format: "), 
			gbc);
		GridBag.setPosSize(gbc, 1, 7, 1, 1);
		optionP.add(tagCategorySizeTf_, gbc);
		return optionP;
	}
	private JPanel createSelectMarkupPanel()
	{
		// Select Markup panel
		JPanel selectMarkupP = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		selectMarkupP.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);    // external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		selectMarkupP.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Setup Select Markup Effect"));
		// banner
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		selectMarkupP.add(new JLabel("Overwrite"), gbc);
		GridBag.setPosSize(gbc, 1, 0, 1, 1);
		selectMarkupP.add(new JLabel("Attributes"), gbc);
		// Overwrites & attribute: bold
		GridBag.setPosSize(gbc, 0, 1, 1, 1);
		selectMarkupP.add(boldCb_, gbc);
		GridBag.setPosSize(gbc, 1, 1, 1, 1);
		selectMarkupP.add(boldList_, gbc);
		// Overwrites & attribute: italic
		GridBag.setPosSize(gbc, 0, 2, 1, 1);
		selectMarkupP.add(italicCb_, gbc);
		GridBag.setPosSize(gbc, 1, 2, 1, 1);
		selectMarkupP.add(italicList_, gbc);
		
		// Overwrites & attribute: underline
		GridBag.setPosSize(gbc, 0, 3, 1, 1);
		selectMarkupP.add(underlineCb_, gbc);
		GridBag.setPosSize(gbc, 1, 3, 1, 1);
		selectMarkupP.add(underlineList_, gbc);
		// Overwrites & attribute: font
		GridBag.setPosSize(gbc, 0, 4, 1, 1);
		selectMarkupP.add(fontCb_, gbc);
		GridBag.setPosSize(gbc, 1, 4, 1, 1);
		selectMarkupP.add(fontList_, gbc);
		// Overwrites & attribute: size
		GridBag.setPosSize(gbc, 0, 5, 1, 1);
		selectMarkupP.add(sizeCb_, gbc);
		GridBag.setPosSize(gbc, 1, 5, 1, 1);
		selectMarkupP.add(sizeTf_, gbc);
		// Overwrites & attribute: text color
		GridBag.setPosSize(gbc, 0, 6, 1, 1);
		selectMarkupP.add(textColorCb_, gbc);
		GridBag.setPosSize(gbc, 1, 6, 1, 1);
		selectMarkupP.add(textColorB_, gbc);
		// Overwrites & attribute: background color
		GridBag.setPosSize(gbc, 0, 7, 1, 1);
		selectMarkupP.add(backgroundColorCb_, gbc);
		GridBag.setPosSize(gbc, 1, 7, 1, 1);
		selectMarkupP.add(backgroundColorB_, gbc);
		// set use markup for non display tag
		GridBag.setPosSize(gbc, 0, 8, 2, 1);
		selectMarkupP.add(useTextTagForNotDisplaySelectMarkupCb_, gbc);
		return selectMarkupP;
	}
	private JPanel createPreviewPanel()
	{
		// preview Panel
		JPanel previewP = new JPanel();
		previewP.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Select Markup Preview"));
		JScrollPane tagSp = new JScrollPane(previewText_);
		tagSp.setPreferredSize(new Dimension(450, 150));
		previewP.add(tagSp, "Center");
		return previewP;
	}
	private JPanel createButtonPanel()
	{
		// button panel: Ok, Cancel Button
		JPanel buttonP = new JPanel();
		buttonP.add(okB_);
		buttonP.add(cancelB_);
		buttonP.add(saveB_);
		buttonP.add(resetB_);
		return buttonP;
	}
	private void initGuiControllers(VttObj vttObj)
	{
		// update local vars from global vars
		configOptionsDialogControl_ = new ConfigOptionsDialogControl(vttObj);
		// buttom panel
		okB_.setActionCommand(B_OK);
		cancelB_.setActionCommand(B_CANCEL);
		saveB_.setActionCommand(B_SAVE);
		resetB_.setActionCommand(B_RESET);
		okB_.addActionListener(configOptionsDialogControl_);
		cancelB_.addActionListener(configOptionsDialogControl_);
		saveB_.addActionListener(configOptionsDialogControl_);
		resetB_.addActionListener(configOptionsDialogControl_);
		// dir & file
		vttDirTf_.setEditable(false); 
		tagsFileTf_.setEditable(false); 
		// vtt format
		vttFormatList_.addItem("Simple: no lineup");
		vttFormatList_.addItem("Readable: max length");
		vttFormatList_.addItem("Fixed: length from in file");
		vttFormatList_.setSelectedIndex(0);
		// select markup panel
		boldList_.addItem("false");
		boldList_.addItem("true");
		boldList_.setSelectedIndex(0);
		italicList_.addItem("false");
		italicList_.addItem("true");
		italicList_.setSelectedIndex(0);
		underlineList_.addItem("false");
		underlineList_.addItem("true");
		underlineList_.setSelectedIndex(0);
		fontList_.addItem("Dialog");
		fontList_.addItem("DialogInput");
		fontList_.addItem("Helvetica");
		fontList_.addItem("Monospaced");
		fontList_.addItem("SansSerif");
		fontList_.addItem("Serif");
		fontList_.setSelectedIndex(0);
		// add listener
		vttFileDirTf_.addActionListener(configOptionsDialogControl_);
		tagsFileTf_.addActionListener(configOptionsDialogControl_);
		fileDisplayLengthTf_.addActionListener(configOptionsDialogControl_);
		baseFontSizeTf_.addActionListener(configOptionsDialogControl_);
		zoomFactorTf_.addActionListener(configOptionsDialogControl_);
		vttFormatList_.addActionListener(configOptionsDialogControl_);
		offsetSizeTf_.addActionListener(configOptionsDialogControl_);
		lengthSizeTf_.addActionListener(configOptionsDialogControl_);
		tagNameSizeTf_.addActionListener(configOptionsDialogControl_);
		tagCategorySizeTf_.addActionListener(configOptionsDialogControl_);
		boldCb_.addItemListener(configOptionsDialogControl_);
		italicCb_.addItemListener(configOptionsDialogControl_);
		underlineCb_.addItemListener(configOptionsDialogControl_);
		fontCb_.addItemListener(configOptionsDialogControl_);
		sizeCb_.addItemListener(configOptionsDialogControl_);
		textColorCb_.addItemListener(configOptionsDialogControl_);
		backgroundColorCb_.addItemListener(configOptionsDialogControl_);
		useTextTagForNotDisplaySelectMarkupCb_.addItemListener(
			configOptionsDialogControl_);
		boldList_.addActionListener(configOptionsDialogControl_);
		italicList_.addActionListener(configOptionsDialogControl_);
		underlineList_.addActionListener(configOptionsDialogControl_);
		fontList_.addActionListener(configOptionsDialogControl_);
		sizeTf_.addActionListener(configOptionsDialogControl_);
		// color
		textColorB_.setActionCommand(B_TEXT_COLOR);
		backgroundColorB_.setActionCommand(B_BG_COLOR);
		textColorB_.addActionListener(configOptionsDialogControl_);
		backgroundColorB_.addActionListener(configOptionsDialogControl_);
		// preview panel
		previewText_.setEditable(false);
	}
	private void initGuiComponents(Window owner)
	{
		// basic setup: title, modal
		setTitle("Configuration Options");
		setModal(true);
		// Geometry Setting
		setLocationRelativeTo(owner);
		setLocation(500, 120);
		setMinimumSize(new Dimension(500, 650));
		// GUI compoment
		if(systemOptionsP_ == null)
		{
			systemOptionsP_ = new JPanel();
			systemOptionsP_.add(createVttSystemPanel(), "Center");
			systemOptionsP_.add(createOptionPanel(), "South");
		}
		if(markupsP_ == null)
		{
			markupsP_ = new JPanel();
			markupsP_.add(createSelectMarkupPanel(), "Center");
			markupsP_.add(createPreviewPanel(), "South");
		}
		// tabbed pane
		tabbedPane_.addTab("System Options", systemOptionsP_);
		tabbedPane_.addTab("Selected Markups", markupsP_);
		// top level
		getContentPane().add(tabbedPane_, "Center");
		getContentPane().add(createButtonPanel(), "South");
	}
	// final variables
	/** Ok Button */
	public final static String B_OK = "OK";
	/** Cancel Button */
	public final static String B_CANCEL = "CANCEL";
	/** Save Button */
	public final static String B_SAVE = "SAVE";
	/** Reset Button */
	public final static String B_RESET = "RESET";
	/** Text Color Button */
	public final static String B_TEXT_COLOR = "TEXT_COLOR";
	/** Background Color Button */
	public final static String B_BG_COLOR = "BG_COLOR";
	private final static long serialVersionUID = 5L;
	// data members
	// local vars
	private ConfigObj configObj_ = null;	// local var for resets
	private boolean updateGuiFlag_ = true;	// flag to update Gui
	// controller
	private ConfigOptionsDialogControl configOptionsDialogControl_ = null;
	// top panel
	private JTabbedPane tabbedPane_ = new JTabbedPane();
	private JPanel systemOptionsP_ = null;    // for dynamic updates
	private JPanel markupsP_ = null;    // for dynamic updates
	// buttom panel
	private JButton okB_ = new JButton("Ok");
	private JButton cancelB_ = new JButton("Cancel");
	private JButton saveB_ = new JButton("Save");
	private JButton resetB_ = new JButton("Reset");
	// dir & file panel
	private JTextField vttDirTf_ = new JTextField(30);
	private JTextField vttFileDirTf_ = new JTextField(20);
	private JTextField tagsFileTf_ = new JTextField(20);
	private JTextField userNameTf_ = new JTextField(20);
	
	// display & other options
	private JTextField fileDisplayLengthTf_ = new JTextField(3);
	private JTextField baseFontSizeTf_ = new JTextField(3);
	private JTextField zoomFactorTf_ = new JTextField(3);
	private JComboBox vttFormatList_ = new JComboBox();
	private JTextField offsetSizeTf_ = new JTextField(3);
	private JTextField lengthSizeTf_ = new JTextField(3);
	private JTextField tagNameSizeTf_ = new JTextField(3);
	private JTextField tagCategorySizeTf_ = new JTextField(3);
	// select markup panel
	private JCheckBox boldCb_ = new JCheckBox("Bold: ");
	private JCheckBox italicCb_ = new JCheckBox("Italic: ");
	private JCheckBox underlineCb_ = new JCheckBox("Underline: ");
	private JCheckBox fontCb_ = new JCheckBox("Font family: ");
	private JCheckBox sizeCb_ = new JCheckBox("Font size: ");
	private JCheckBox textColorCb_ = new JCheckBox("Text color: ");
	private JCheckBox backgroundColorCb_ 
		= new JCheckBox("Background color: ");
	private JCheckBox useTextTagForNotDisplaySelectMarkupCb_ 
		= new JCheckBox("Apply select markup effect on non-display selected markup");
	private JComboBox boldList_ = new JComboBox();
	private JComboBox italicList_ = new JComboBox();
	private JComboBox underlineList_ = new JComboBox();
	private JComboBox fontList_ = new JComboBox();
	private JTextField sizeTf_ = new JTextField();
	private JButton textColorB_ = new JButton("Edit");
	private JButton backgroundColorB_ = new JButton("Edit");
	// preview Panel
	private JTextPane previewText_ = new JTextPane();
}
