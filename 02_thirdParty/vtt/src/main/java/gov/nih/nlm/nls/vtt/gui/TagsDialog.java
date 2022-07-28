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
* This class is the GUI dialog for all operations on tags. It includes tabs of:
* Properties, Display, Save and Import.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class TagsDialog extends JDialog
{
	// public constructor 
	/**
    * Create a TagsDialog object for all tags operations by specifying owner 
	* and vttObj.
    *
    * @param owner  the owner of this TagsDialog
    * @param vttObj  the vttObj Java object
    */
	public TagsDialog(Window owner, VttObj vttObj)
	{
		// constructor
		super(owner);
		// init controller & GUI compoment
		initGuiControllers(vttObj);
		initGuiComponents(owner, vttObj);
	}
	// update GUI components
	/**
    * Set TagsDialog to be visible (show).
	* This does not work on the checkbox status of displayP_.
    *
    * @param owner  the owner of this TagsDialog
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
    * Set TagsDialog to be visible (show).
    *
    * @param owner  the owner of this TagsDialog
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
    * Set TagsDialog to be not visible (not show).
    */
	public void notShow()
	{
		setVisible(false);
	}
	/**
    * Update all tabs in the dialog. This method must be called when the 
	* state changes for dynamic checkbox in displayP.
	* displayP has Vector of Java object JCheckBox that needs updates and 
	* redraw.
	*
    * @param vttObj  the vttObj Java object
    */
	public void updateTabs(VttObj vttObj)
	{
		// update the checkbox in display panel
		int tabIndex = tabbedPane_.getSelectedIndex();
		switch(tabIndex)
		{
			case 0: // property tab
				propertyP_ = createPropertyPanel(vttObj);
				tabbedPane_.setComponentAt(0, propertyP_);
				break;
			case 1:	// display filter tab
				displayP_ = createDisplayFilterPanel(vttObj);
				tabbedPane_.setComponentAt(1, displayP_);
				break;
			case 2:	// quick key tab
				keyMapP_ = createQuickKeyMappingPanel(vttObj);
				tabbedPane_.setComponentAt(2, keyMapP_);
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
	// Set up Tab:
	/**
    * The operation when hit Add button.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitAdd(VttObj vttObj)
	{
		vttObj.getAddTagDialog().show(this, vttObj);
	}
	/**
    * Set the index of interested tag.
    *
    * @param index  the index of interested tag
    */
	public void setTagIndex(int index)
	{
		tagIndex_ = index;
	}
	/**
	* The operation when hit button of mapping the current tags to quick keys. 
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitMapToQuickKeys(VttObj vttObj)
	{
		// provide confirm dialog
		String msg = "Map the order of tags to quick keys (1~9):" 
			+ GlobalVars.LS_STR;
		Vector<Tag> tags = vttObj.getVttDocument().getTags().getTags();
		for(int i = 1; i < 10; i++)
		{
			if(i >= tags.size())
			{
				msg += "Key[" + i + "]: " + GlobalVars.LS_STR;
			}
			else
			{
				msg += "Key[" + i + "]: " + tags.elementAt(i).getNameCategory() 
					+ GlobalVars.LS_STR;
			}
		}
		int saveOption = JOptionPane.showConfirmDialog(this, msg,
			"Map Tags order to quick keys",
			JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.INFORMATION_MESSAGE);
		if(saveOption == JOptionPane.OK_OPTION)
		{
			// save current tag order to quick key
			vttObj.getVttDocument().getTags().setQuickKeyMappingsToDefault();
		}
	}
	/**
	* The operation when hit button of applying the current tags to quick keys. 
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitApplyQuickKeys(VttObj vttObj)
	{
		// Save the slection of JComboBox to local vars
		Vector<String> quickKeyMappings = new Vector<String>();
		for(int i = 0; i < QUICK_KEY_SIZE; i++)
		{
			String tagNameCategory = 
				(String) keyMapList_.elementAt(i).getSelectedItem();
			if(tagNameCategory == null)
			{
				tagNameCategory = new String();
			}
			quickKeyMappings.addElement(tagNameCategory);	
		}
		// provide confirm dialog
		String msg = "Save quick keys (1~9):" + GlobalVars.LS_STR;
		for(int i = 0; i < QUICK_KEY_SIZE; i++)
		{
			msg += "Key[" + (i+1) + "]: " + quickKeyMappings.elementAt(i) 
				+ GlobalVars.LS_STR;
		}
		int saveOption = JOptionPane.showConfirmDialog(this, msg,
			"Save quick keys", JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.INFORMATION_MESSAGE);
		if(saveOption == JOptionPane.OK_OPTION)
		{
			// apply the slection of JComboBox from loval vars to global
			vttObj.getVttDocument().getTags().setQuickKeyMappings(
				quickKeyMappings);
		}
	}
	/**
	* The operation to reset quick keys. 
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitResetQuickKeys(VttObj vttObj)
	{
		// reset the selection of JComboBox (from global)
		for(int i = 0; i < QUICK_KEY_SIZE; i++)
		{
			keyMapList_.elementAt(i).setSelectedIndex(
				vttObj.getVttDocument().getTags().getQuickKeyMappingIndex(i));
		}
	}
	/**
	* The operation when hit sort name or category button.
	* This operation sort tags list by name and category.
	* Set up Tab: work on tagList_ and tagNameCategory_
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitSortNameCategory(VttObj vttObj)
	{
		// save selected index
		int index = tagList_.getSelectedIndex();
		String tagNameCategory = (String) tagList_.getSelectedValue();
		// sort tags
		vttObj.getVttDocument().getTags().sortTags();
		updateTagListModel(vttObj);
		// set the select index
		tagIndex_ = vttObj.getVttDocument().getTags().getIndexByTag(
			tagNameCategory) - VttDocument.TAG_START_INDEX;
		tagList_.setSelectedIndex(tagIndex_);
	}
	/**
	* The operation when hit move up button.
	* This operation move the select tag up in the tags list.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitMoveUp(VttObj vttObj)
	{
		// save selected index
		int index = tagList_.getSelectedIndex();
		if(index > 0)
		{
			vttObj.getVttDocument().getTags().moveUp(
				index + VttDocument.TAG_START_INDEX);
			updateTagListModel(vttObj);
			tagIndex_ = index - VttDocument.TAG_START_INDEX;
			tagList_.setSelectedIndex(tagIndex_);
		}
		else if(index != 0)
		{
			JOptionPane.showMessageDialog(this, 
				"Please select a tag to move up.",
				"Error: Tag Properties", JOptionPane.ERROR_MESSAGE);
		}
	}
	/**
	* The operation when hit move down button.
	* This operation move the select tag down in the tags list.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitMoveDown(VttObj vttObj)
	{
		int index = tagList_.getSelectedIndex();
		int size = tagNameCategory_.size();
		if((index >= 0) && (index < size-1)) 
		{
			vttObj.getVttDocument().getTags().moveDown(
				index + VttDocument.TAG_START_INDEX);
			updateTagListModel(vttObj);
			tagIndex_ = index+1;
			tagList_.setSelectedIndex(tagIndex_);
		}
		else if(index != size-1)
		{
			JOptionPane.showMessageDialog(this, 
				"Please select a tag to move down.",
				"Error: Tag Properties", JOptionPane.ERROR_MESSAGE);
		}
	}
	/**
    * The operation when hit Edit button.
	*
	* @param vttObj  the vttObj Java object
    */
	public void hitEdit(VttObj vttObj)
	{
		int index = tagList_.getSelectedIndex();
		if(index >= 0)
		{
			tagIndex_ = index;
			vttObj.getEditTagDialog().show(this,
				tagIndex_ + VttDocument.TAG_START_INDEX, vttObj);
		}
		else
		{
			JOptionPane.showMessageDialog(this, 
				"Please select a tag to edit.",
				"Error: Tag Properties", JOptionPane.ERROR_MESSAGE);
		}
	}
	/**
    * The operation when hit Delete button.
	*
	* @param vttObj  the vttObj Java object
    */
	public void hitDelete(VttObj vttObj)
	{
		int index = tagList_.getSelectedIndex();
		String tagNameCategory = (String) tagList_.getSelectedValue();
		if(index >= 0)
		{
			int selectOption = JOptionPane.showConfirmDialog(this,
				"Tag [" + tagNameCategory 
				+ "] and all associated tagged data will be deleted.",
				"Warning: Delete a Tag", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE);
			tagIndex_ = index;	
			// delete the selected tag & associated markup
			if(selectOption == JOptionPane.OK_OPTION)
			{
				// delete tag and update tag list model
				vttObj.getVttDocument().getTags().deleteTag(
					tagIndex_ + VttDocument.TAG_START_INDEX);
				// Update GUi	
				updateTagListModel(vttObj);
				// Delete all markups associated with this tag 
				vttObj.getVttDocument().getMarkups().deleteMarkupByTag(
					tagNameCategory);
				// update style of text & markup
				TextDisplayOperations.updateStyle(
					TextDisplayOperations.TEXT_MARKUPS, vttObj);
			}
		}
		else
		{
			JOptionPane.showMessageDialog(this, 
				"Please select a tag to delete.",
				"Error: Tag Properties", JOptionPane.ERROR_MESSAGE);
		}
	}
	/**
    * The operation when hit tab of display all name.
	*
	* @param vttObj  the vttObj Java object
    */
	public void hitDisplayAllName(VttObj vttObj)
	{
		for(int i = 0; i < nameCbs_.size(); i++)
		{
			nameCbs_.elementAt(i).setSelected(true);
		}
		updateGlobalFromGui(vttObj);
		TextDisplayOperations.updateStyle(TextDisplayOperations.TEXT_MARKUPS,
			vttObj);
	}
	/**
    * The operation when hit tab of display all category.
	*
	* @param vttObj  the vttObj Java object
    */
	public void hitDisplayAllCategory(VttObj vttObj)
	{
		for(int i = 0; i < categoryCbs_.size(); i++)
		{
			categoryCbs_.elementAt(i).setSelected(true);
		}
		updateGlobalFromGui(vttObj);
		TextDisplayOperations.updateStyle(TextDisplayOperations.TEXT_MARKUPS,
			vttObj);
	}
	/**
    * The operation when hit tab of display no name.
	*
	* @param vttObj  the vttObj Java object
    */
	public void hitDisplayNoName(VttObj vttObj)	// clear display
	{
		for(int i = VttDocument.TAG_START_INDEX; i < nameCbs_.size(); i++)
		{
			nameCbs_.elementAt(i).setSelected(false);
		}
		updateGlobalFromGui(vttObj);
		TextDisplayOperations.updateStyle(TextDisplayOperations.TEXT_MARKUPS,
			vttObj);
	}
	/**
    * The operation when hit tab of display no category.
	*
	* @param vttObj  the vttObj Java object
    */
	public void hitDisplayNoCategory(VttObj vttObj)	// clear display
	{
		for(int i = 0; i < categoryCbs_.size(); i++)
		{
			categoryCbs_.elementAt(i).setSelected(false);
		}
		updateGlobalFromGui(vttObj);
		TextDisplayOperations.updateStyle(TextDisplayOperations.TEXT_MARKUPS,
			vttObj);
	}
	/**
    * The operation when hit save & import buttons.
	*
	* @param vttObj  the vttObj Java object
    */
	public void hitSave(VttObj vttObj)
	{
		vttObj.getConfigObj().setTagFile(new File(tagFileText_.getText()));
		int saveOption = JOptionPane.showConfirmDialog(this,
			"Save tag configuration to file " 
			+ vttObj.getConfigObj().getTagFile().getAbsolutePath() 
			+ "?", 
			"Save Tags Configuration",
			JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.INFORMATION_MESSAGE);
		if(saveOption == JOptionPane.OK_OPTION)
		{
			// save global tags_ to file
			Tags.saveTagsToFile(
				vttObj.getConfigObj().getTagFile().getAbsolutePath(),
				vttObj.getVttDocument().getTags().getTags());
		}
	}
	/**
    * The operation when hit save as buttons.
	*
	* @param vttObj  the vttObj Java object
    */
	public void hitSaveAs(VttObj vttObj)
	{
		vttObj.getConfigObj().setTagFile(new File(tagFileText_.getText()));
		JFileChooser fileChooser = new JFileChooser();
		//fileChooser.setCurrentDirectory(GlobalVars.tagFile_);
		fileChooser.setSelectedFile(
			vttObj.getConfigObj().getTagFile()); 
		fileChooser.setDialogTitle("Save Tag File As");
		fileChooser.setMultiSelectionEnabled(false);
		if(fileChooser.showSaveDialog(this)
			== JFileChooser.APPROVE_OPTION)
		{
			// save global tags_ to file
			vttObj.getConfigObj().setTagFile(
				fileChooser.getSelectedFile());
			Tags.saveTagsToFile(
				vttObj.getConfigObj().getTagFile().getAbsolutePath(),
				vttObj.getVttDocument().getTags().getTags());
			
			// update JTextField
			tagFileText_.setText(
				vttObj.getConfigObj().getTagFile().getAbsolutePath());
		}
	}
	/**
    * The operation when hit import buttons.
	*
	* @param vttObj  the vttObj Java object
    */
	public void hitImport(VttObj vttObj)
	{
		vttObj.getConfigObj().setTagFile(
			new File(tagFileText_.getText()));
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(
			vttObj.getConfigObj().getTagFile());
		fileChooser.setSelectedFile(
			vttObj.getConfigObj().getTagFile());
		fileChooser.setDialogTitle("Import Tag File");
		fileChooser.setMultiSelectionEnabled(false);
		// import tag from a file
		if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			// reload global tags_
			Tags tempTags 
				= new Tags(vttObj.getVttDocument().getTags());
			vttObj.getConfigObj().setTagFile(
				fileChooser.getSelectedFile());
			vttObj.getVttDocument().getTags().loadTagsFromFile(
				vttObj.getConfigObj().getTagFile().getAbsolutePath()); 
			
			// update JTextField
			tagFileText_.setText(
				vttObj.getConfigObj().getTagFile().getAbsolutePath());
			
			// Get a set of all currently used tags and merge them with the imported list
			Vector<Markup> markups = vttObj.getVttDocument().getMarkups().getMarkups();
			for (Markup markup : markups) {
				String tagNameCategory = markup.getTagNameCategory();
				if (!vttObj.getVttDocument().getTags().containsNameCategory(tagNameCategory)) {
					Tag newTag = tempTags.getTagByNameCategory(tagNameCategory);
					vttObj.getVttDocument().getTags().addTag(newTag);
				}
			}

			// update display to the new imported tags
			if(TextDisplayOperations.updateStyle(TextDisplayOperations.MARKUPS,
				vttObj))
			{
				// close tagsDialog
				hitClose();
			}
			else
			{
				// put the original tags back
				vttObj.getVttDocument().setTags(tempTags);
				// Warning:
				JOptionPane.showMessageDialog(this,
					"Some tags are not defined in the new imported tags file, please re-import a correct tags file!",
					"Import a Tags file ", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	/**
	* This method updates from Gui to Global for diaplay filter.
	*
	* @param vttObj  the vttObj Java object
    */
	public void updateGlobalFromGui(VttObj vttObj)
	{
		// update name display filter
		Vector<TagFilter> nameList = new Vector<TagFilter>();
		for(int i = 0; i < nameCbs_.size(); i++)
		{
			TagFilter tagFilter = new TagFilter(nameCbs_.elementAt(i).getText(),
				nameCbs_.elementAt(i).isSelected());
			nameList.addElement(tagFilter);
		}
		TagOperations.updateNameList(vttObj, nameList);
		
		// update category display filter
		Vector<TagFilter> categoryList = new Vector<TagFilter>();
		for(int i = 0; i < categoryCbs_.size(); i++)
		{
			String category = categoryCbs_.elementAt(i).getText();
			if(category.equals(NOT_SPECIFY_STR) == true)
			{
				category = new String();
			}
			TagFilter tagFilter = new TagFilter(category,
				categoryCbs_.elementAt(i).isSelected());
			categoryList.addElement(tagFilter);
		}
		TagOperations.updateCategoryList(vttObj, categoryList);
	}
	// private methods
	private void updateGuiFromGlobal(VttObj vttObj)
	{
		// property panel
		updateTagListModel(vttObj);
		updateTagListSelectIndex();
		// display filter panel
		updateDisplayFiltersPanel(vttObj);
		// quick key mapping panel
		updateQuickKeyMappingPanel(vttObj);
		// import & save panel
	}
	// Update tag list in properties panel from global vars
	private void updateTagListModel(VttObj vttObj)
	{
		tagNameCategory_.removeAllElements();
		Vector<Tag> tags = vttObj.getVttDocument().getTags().getTags();
		for(int i = VttDocument.TAG_START_INDEX; i < tags.size(); i++)
		{
			Tag tag = tags.elementAt(i);
			tagNameCategory_.addElement(tag.getNameCategory());
		}
	}
	// update select index of JList of tab list
	private void updateTagListSelectIndex()
	{
		if((tagIndex_ < 0)
		|| (tagIndex_ >= tagNameCategory_.size()))
		{
			tagIndex_ = 0;
		}
		tagList_.setSelectedIndex(tagIndex_);
	}
	// update display panel: GUI status from global vars
	// TBD the order is not quite right ...
	private void updateDisplayFiltersPanel(VttObj vttObj)
	{
		// update checkBoxes from global vars (tags)
		Vector<TagFilter> nameList 
			= vttObj.getVttDocument().getTags().getNameList();
		// name display filter
		nameCbs_.removeAllElements();
		for(int i = 0; i < nameList.size(); i++)
		{
			TagFilter tagFilter = nameList.elementAt(i);
			JCheckBox cb = new JCheckBox(tagFilter.getName());
			cb.addItemListener(tagsDialogControl_);
			cb.setSelected(tagFilter.getStatus());
			nameCbs_.addElement(cb);
		}
		// category display filter
		Vector<TagFilter> categoryList 
			= vttObj.getVttDocument().getTags().getCategoryList();
		// update checkBoxes from global vars (tags)
		categoryCbs_.removeAllElements();
		for(int i = 0; i < categoryList.size(); i++)
		{
			TagFilter tagFilter = categoryList.elementAt(i);
			String category = tagFilter.getName();
			if(category.length() == 0)
			{
				category = NOT_SPECIFY_STR;
			}
			JCheckBox cb = new JCheckBox(category);
			cb.addItemListener(tagsDialogControl_);
			cb.setSelected(tagFilter.getStatus());
			categoryCbs_.addElement(cb);
		}
	}
	// update display panel: GUI status from global vars
	private void updateQuickKeyMappingPanel(VttObj vttObj)
	{
		// update name Category list
		Tags tagsObj = vttObj.getVttDocument().getTags();
		Vector<Tag> tags = tagsObj.getTags();
		Vector<String> tagNameCategory = new Vector<String>();
		for(int i = VttDocument.TAG_START_INDEX; i < tags.size(); i++)
		{
			Tag tag = tags.elementAt(i);
			tagNameCategory.addElement(tag.getNameCategory());
		}
		// update JComboBox to the latest name category
		keyMapList_.removeAllElements();
		for(int i = 0; i < QUICK_KEY_SIZE; i++)
		{
			JComboBox tagCb = new JComboBox();
			tagCb.addItem(new String());
			for(int j = 0; j < tagNameCategory.size(); j++) 
			{
				String nameCategory = tagNameCategory.elementAt(j);
				tagCb.addItem(nameCategory);
			}
			tagCb.addActionListener(tagsDialogControl_);
			// set selected index
			tagCb.setSelectedIndex(tagsObj.getQuickKeyMappingIndex(i));
			keyMapList_.addElement(tagCb);
		}
	}
	// create GUI Compoment
	private JPanel createQuickKeyMappingPanel(VttObj vttObj)
	{
		JPanel keyMapP = new JPanel();
		// top panel: Title
		JPanel topP = new JPanel();
		topP.add(new JLabel("Tags: Quick Keys Mapping:"));
		// button panel
		JPanel buttonP = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		buttonP.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);	// external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		buttonP.add(applyB_, gbc);
		GridBag.setPosSize(gbc, 1, 0, 1, 1);	
		buttonP.add(resetB_, gbc);
		// center panel
		JPanel centerP = new JPanel();
		centerP.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Quick Key Mapping"));
		centerP.setPreferredSize(new Dimension(300, 350));
		centerP.setLayout(gbl);
		// update combo list
		updateQuickKeyMappingPanel(vttObj);
		for(int i = 0; i < QUICK_KEY_SIZE; i++)
		{
			GridBag.setPosSize(gbc, 0, i, 1, 1);
			String label = "Key [" + (i+1) + "]:"; 
			centerP.add(new JLabel(label), gbc);
			GridBag.setPosSize(gbc, 1, i, 1, 1);
			centerP.add(keyMapList_.elementAt(i), gbc);
		}
		// add Panel
		keyMapP.add(topP, "North");
		keyMapP.add(centerP, "Center");
		keyMapP.add(buttonP, "South");
		return keyMapP;
	}
	private JPanel createSaveImportPanel()
	{
		JPanel displayP = new JPanel();
		// top panel: Title
		JPanel topP = new JPanel();
		topP.add(new JLabel("Save & Import Options:"));
		// button panel
		JPanel buttonP = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		buttonP.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);	// external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		buttonP.add(saveB_, gbc);
		GridBag.setPosSize(gbc, 1, 0, 1, 1);	
		buttonP.add(saveAsB_, gbc);
		GridBag.setPosSize(gbc, 2, 0, 1, 1);	
		buttonP.add(importB_, gbc);
		// center panel
		JPanel centerP = new JPanel();
		centerP.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Save & Import"));
		centerP.setPreferredSize(new Dimension(420, 200));
		centerP.setLayout(gbl);
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		centerP.add(new JLabel("Tag Configuration File: "), gbc);
		GridBag.setPosSize(gbc, 0, 1, 1, 1);
		centerP.add(tagFileText_, gbc);
		GridBag.setPosSize(gbc, 0, 2, 1, 1);
		centerP.add(buttonP, gbc);
		// add Panel
		displayP.add(topP, "North");
		displayP.add(centerP, "Center");
		return displayP;
	}
	private JPanel createPropertyPanel(VttObj vttObj)
	{
		JPanel propertyP = new JPanel();
		// top panel: Title
		JPanel topP = new JPanel();
		topP.add(new JLabel("Tags Options:"));
		// scroll pane for tag list	
		tagList_ = new JList(tagNameCategory_);
		updateTagListModel(vttObj);
		updateTagListSelectIndex();
		JScrollPane selectedSp = new JScrollPane(tagList_);
		selectedSp.setPreferredSize(new Dimension(230, 350));
		// button panel
		JPanel buttonP = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		buttonP.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);    // external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		GridBag.setPosSize(gbc, 0, 0, 1, 1);	
		buttonP.add(addB_, gbc);
		GridBag.setPosSize(gbc, 0, 1, 1, 1);	
		buttonP.add(deleteB_, gbc);
		GridBag.setPosSize(gbc, 0, 2, 1, 1);	
		buttonP.add(editB_, gbc);
		GridBag.setPosSize(gbc, 0, 3, 1, 1);	
		buttonP.add(sortB_, gbc);
		GridBag.setPosSize(gbc, 0, 4, 1, 1);	
		buttonP.add(upB_, gbc);
		GridBag.setPosSize(gbc, 0, 5, 1, 1);	
		buttonP.add(downB_, gbc);
		GridBag.setPosSize(gbc, 0, 6, 1, 1);	
		buttonP.add(mapKeyB_, gbc);
		// center panel
		JPanel centerP = new JPanel();
		centerP.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Tag: Name|Category"));
		centerP.add(selectedSp);
		centerP.add(buttonP);
		// add panel
		propertyP.setLayout(new BorderLayout());
		propertyP.add(topP, "North");
		propertyP.add(centerP, "Center");
		return propertyP;
	}
	// The list of name and category filter is stored in a vector.
	// This vector is updated everytime the tags is changed.
	// Thus, this method is needed to called whenever there is a change.
	private JPanel createDisplayFilterPanel(VttObj vttObj)
	{
		JPanel displayP = new JPanel();
		// top panel: Title
		JPanel topP = new JPanel();
		topP.add(new JLabel("Tags Display Filter:"));
		// center panel
		updateDisplayFiltersPanel(vttObj);
		JPanel nameP = createNameSubPanel(vttObj);
		JPanel categoryP = createCategorySubPanel(vttObj);
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		JPanel centerP = new JPanel();
		centerP.setLayout(gbl);
		gbc.insets = new Insets(5, 5, 5, 5);	// external padding
		gbc.fill = GridBagConstraints.VERTICAL;
		GridBag.setWeight(gbc, 100, 0);
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		centerP.add(nameP, gbc);
		GridBag.setPosSize(gbc, 1, 0, 1, 1);
		centerP.add(categoryP, gbc);
		// add Panel
		displayP.setLayout(new BorderLayout());
		displayP.add(topP, "North");
		displayP.add(centerP, "Center");
		return displayP;
	}
	private JPanel createNameSubPanel(VttObj vttObj)
	{
		// name check Boxes	
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		JPanel nameCheckBoxP = new JPanel();
		nameCheckBoxP.setLayout(gbl);
		gbc.insets = new Insets(5, 5, 5, 5);	// external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		// update nameCheckBox then add to panel
		for(int i = VttDocument.TAG_START_INDEX; i < nameCbs_.size(); i++)
		{
			int ii = i - VttDocument.TAG_START_INDEX;
			GridBag.setPosSize(gbc, 0, ii, 1, 1);
			nameCheckBoxP.add(nameCbs_.elementAt(i), gbc);
		}
		// scroll pane for tag name list
		JScrollPane nameSelectSp = new JScrollPane(nameCheckBoxP);
		nameSelectSp.setPreferredSize(new Dimension(150, 250));
		// name layout panel
		JPanel nameLayoutP = new JPanel();
		nameLayoutP.setLayout(gbl);
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		nameLayoutP.add(nameSelectSp, gbc);
		GridBag.setPosSize(gbc, 0, 1, 1, 1);
		nameLayoutP.add(allNameB_, gbc);
		GridBag.setPosSize(gbc, 0, 2, 1, 1);	
		nameLayoutP.add(noNameB_, gbc);
		// name panel
		JPanel nameP = new JPanel();
		nameP.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Name Filter"));
		nameP.add(nameLayoutP);
		return nameP;
	}
	private JPanel createCategorySubPanel(VttObj vttObj)
	{
		// category check Boxes	
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		JPanel categoryCheckBoxP = new JPanel();
		categoryCheckBoxP.setLayout(gbl);
		gbc.insets = new Insets(5, 5, 5, 5);	// external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		// update nameCheckBox then add to panel
		for(int i = 0; i < categoryCbs_.size(); i++)
		{
			GridBag.setPosSize(gbc, 0, i, 1, 1);	
			categoryCheckBoxP.add(categoryCbs_.elementAt(i), gbc);
		}
		// scroll pane for tag category list
		JScrollPane categorySelectSp = new JScrollPane(categoryCheckBoxP);
		categorySelectSp.setPreferredSize(new Dimension(150, 250));
		// category layout panel
		JPanel categoryLayoutP = new JPanel();
		categoryLayoutP.setLayout(gbl);
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		categoryLayoutP.add(categorySelectSp, gbc);
		GridBag.setPosSize(gbc, 0, 1, 1, 1);
		categoryLayoutP.add(allCategoryB_, gbc);
		GridBag.setPosSize(gbc, 0, 2, 1, 1);	
		categoryLayoutP.add(noCategoryB_, gbc);
		// category panel
		JPanel categoryP = new JPanel();
		categoryP.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Category Filter"));
		categoryP.add(categoryLayoutP);
		return categoryP;
	}
	private void initGuiControllers(VttObj vttObj)
	{
		// control
		tagsDialogControl_ = new TagsDialogControl(vttObj);
		// top panel
		tabbedPane_.addChangeListener(tagsDialogControl_);
		closeB_.setActionCommand(B_CLOSE);
		closeB_.addActionListener(tagsDialogControl_);
		// property panel
		propertyP_ = createPropertyPanel(vttObj);
		tagList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addB_.setActionCommand(B_ADD);
		deleteB_.setActionCommand(B_DELETE);
		editB_.setActionCommand(B_EDIT);
		sortB_.setActionCommand(B_SORT);
		upB_.setActionCommand(B_UP);
		downB_.setActionCommand(B_DOWN);
		mapKeyB_.setActionCommand(B_MAP_KEYS);
		addB_.addActionListener(tagsDialogControl_);
		deleteB_.addActionListener(tagsDialogControl_);
		editB_.addActionListener(tagsDialogControl_);
		sortB_.addActionListener(tagsDialogControl_);
		upB_.addActionListener(tagsDialogControl_);
		downB_.addActionListener(tagsDialogControl_);
		mapKeyB_.addActionListener(tagsDialogControl_);
		// display filter panel
		displayP_ = createDisplayFilterPanel(vttObj);
		allNameB_.setActionCommand(B_ALL_NAME);
		noNameB_.setActionCommand(B_NO_NAME);
		allCategoryB_.setActionCommand(B_ALL_CATEGORY);
		noCategoryB_.setActionCommand(B_NO_CATEGORY);
		
		allNameB_.addActionListener(tagsDialogControl_);
		noNameB_.addActionListener(tagsDialogControl_);
		allCategoryB_.addActionListener(tagsDialogControl_);
		noCategoryB_.addActionListener(tagsDialogControl_);
		// quick key mapping panel
		keyMapP_ = createQuickKeyMappingPanel(vttObj);
		applyB_.setActionCommand(B_APPLY);
		resetB_.setActionCommand(B_RESET);
		applyB_.addActionListener(tagsDialogControl_);
		resetB_.addActionListener(tagsDialogControl_);
		// save & import panel
		tagFileText_ = new JTextField(
			vttObj.getConfigObj().getTagFile().getAbsolutePath());
		tagFileText_.setColumns(80);
		tagFileText_.setHorizontalAlignment(JTextField.RIGHT);
		importP_ = createSaveImportPanel();
		saveB_.setActionCommand(B_SAVE);
		saveAsB_.setActionCommand(B_SAVE_AS);
		importB_.setActionCommand(B_IMPORT);
		saveB_.addActionListener(tagsDialogControl_);
		saveAsB_.addActionListener(tagsDialogControl_);
		importB_.addActionListener(tagsDialogControl_);
	}
	private void initGuiComponents(Window owner, VttObj vttObj)
	{
		// basic setup: title, modal
		setTitle("Tags");
		setModal(true);
		// Geometry Setting
		setLocationRelativeTo(owner);
		setMinimumSize(new Dimension(450, 520));
		// tabbed pane
		tabbedPane_.addTab("Properties", propertyP_); 
		tabbedPane_.addTab("Display Filter", displayP_); 
		tabbedPane_.addTab("Quick Keys", keyMapP_); 
		tabbedPane_.addTab("Save & Import", importP_); 
		// bottom panel: Close Button
		JPanel bottomP = new JPanel();
		bottomP.add(closeB_);
		// top level
		getContentPane().add(tabbedPane_, "Center");
		getContentPane().add(bottomP, "South");
		// release state change to true after init view objects for update
		tagsDialogControl_.setUpdateStateChange(true);
	}
	// final vairables
	private final static long serialVersionUID = 5L;
	private final static int QUICK_KEY_SIZE= 9;
	private final static String NOT_SPECIFY_STR= "(Not Specified)";
	// top level
	/** Close button */
	public final static String B_CLOSE = "CLOSE";
	// properties set up tab
	/** Close button */
	public final static String B_ADD = "ADD";
	/** Delete button */
	public final static String B_DELETE = "DELETE";
	/** Edit button */
	public final static String B_EDIT = "EDIT";
	/** Sort button */
	public final static String B_SORT = "SORT";
	/** up button */
	public final static String B_UP = "UP";
	/** Down button */
	public final static String B_DOWN = "DOWN";
	/** Keys Map button */
	public final static String B_MAP_KEYS = "MAP_KEYS";
	// display tab
	/** All name button */
	public final static String B_ALL_NAME = "ALL_NAME";
	/** No name button */
	public final static String B_NO_NAME = "NO_NAME";
	/** All category button */
	public final static String B_ALL_CATEGORY = "ALL_CATEGORY";
	/** No category button */
	public final static String B_NO_CATEGORY = "NO_CATEGORY";
	// key mapping tab
	/** Apply button */
	public final static String B_APPLY = "APPLY";
	/** Reset button */
	public final static String B_RESET = "RESET";
	// import tab
	/** Save button */
	public final static String B_SAVE = "SAVE";
	/** Save as button */
	public final static String B_SAVE_AS = "SAVE_AS";
	/** Import button */
	public final static String B_IMPORT = "IMPORT";
	// data members
	private DefaultListModel tagNameCategory_ = new DefaultListModel();
	private boolean firstTime_ = true;
	// GUI: control
	private TagsDialogControl tagsDialogControl_ = null; 
	// top panel
	private JTabbedPane tabbedPane_ = new JTabbedPane();
	private JButton closeB_ = new JButton("Close");
	// property tab
	private JPanel propertyP_ = null;	// for dynamic updates
	private JList tagList_ = new JList(); // tag list
	private int tagIndex_ = 0;	// selected Index for tag list
	private JButton addB_ = new JButton("Add");
	private JButton deleteB_ = new JButton("Delete");
	private JButton editB_ = new JButton("Edit");
	private JButton sortB_ = new JButton("Sort");
	private JButton upB_ = new JButton("Move Up");
	private JButton downB_ = new JButton("Move Down");
	private JButton mapKeyB_ = new JButton("Map To Quick Keys");
	// display tab
	private JPanel displayP_ = null;	// for dynamic updates
	private Vector<JComboBox> keyMapList_ = new Vector<JComboBox>();
	private JButton applyB_ = new JButton("Apply");
	private JButton resetB_ = new JButton("Reset");
	private Vector<JCheckBox> nameCbs_ = new Vector<JCheckBox>();
	private Vector<JCheckBox> categoryCbs_ = new Vector<JCheckBox>();
	private JButton allNameB_ = new JButton("Display All");
	private JButton noNameB_ = new JButton("Display None");
	private JButton allCategoryB_ = new JButton("Display All");
	private JButton noCategoryB_ = new JButton("Display None");
	// quick key mapping tab
	private JPanel keyMapP_ = null;	// for dynamic updates
	// save & import tab
	private JPanel importP_ = null;	// for dynamic updates
	private JTextField tagFileText_ = null;
	private JButton saveB_ = new JButton("Save");
	private JButton saveAsB_ = new JButton("Save As");
	private JButton importB_ = new JButton("Import");
}
