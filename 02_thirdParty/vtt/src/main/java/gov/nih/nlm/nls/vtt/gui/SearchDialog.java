package gov.nih.nlm.nls.vtt.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gov.nih.nlm.nls.vtt.guiControl.FindDialogControl;
import gov.nih.nlm.nls.vtt.guiControl.SearchDialogControl;
import gov.nih.nlm.nls.vtt.guiLib.GridBag;
import gov.nih.nlm.nls.vtt.model.Markups;
import gov.nih.nlm.nls.vtt.model.VttObj;
import gov.nih.nlm.nls.vtt.operations.SearchOperations;
import gov.nih.nlm.nls.vtt.operations.TextDisplayOperations;

public class SearchDialog extends JDialog {

	public SearchDialog(Window owner, VttObj vttObj) {
		// constructor
				super(owner);
				// init GUI controller & GUI compoment
				initGuiControllers(vttObj);
				initGuiComponents(owner);
	}
	
	
	/**
	    * Search button: find the search text.
	    *
	    * @param vttObj  the vttObj Java object
	    */
		public void hitSearch(VttObj vttObj)
		{
			searchAction_ = SEARCH;
			// update local vars & effect on main panel
			updateGui(vttObj);
		}
		/**
	    * Close button: close this dialog.
	    */
		public void hitClose()
		{
			setVisible(false);
		}
		
		
		
		
		// Update GUI Components
		/**
	    * Set this dialog to be visible (show).
	    *
	    * @param owner  the owner of this dialog
	    * @param vttObj  the vttObj Java object
	    */
		public void show(Window owner, VttObj vttObj) 
		{
			// update Gui vars
			updateGuiFromControl();
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
	    * Update GUI component: local variables and then update Gui.
	    *
	    * @param vttObj  the vttObj Java object
	    */
		public void updateGui(VttObj vttObj)
		{
			// don't update if it is the first time, tag_ is used in preview
			// update target String
			targetStr_ = targetStrTf_.getText();
			// update options
			matchCase_ = matchCaseCb_.isSelected();
			// TBD: find the target & update textPane_
			String text = vttObj.getVttDocument().getText();
			int startPos 
				= vttObj.getMainFrame().getMainPanel().getCaretPosition();
			findStringInText(text, startPos, targetStr_, searchAction_, 
				matchCase_, vttObj);
			
		}
		// private methods
		// update GUI from Control vars
		private void updateGuiFromControl()
		{
			// target & result String
			targetStrTf_.setText(targetStr_);
			// options: match case
			matchCaseCb_.setSelected(matchCase_);
		}
		private void findStringInText(String text, int startPos, 
			String targetStr, int findAction, boolean matchCase, VttObj vttObj)
		{
			
			// find the target String
			if((text != null) && (text.length() > 0))
	        {
	            switch(findAction)
	            {
	                case SEARCH:
	                	SearchOperations.clearSearchHighlight(vttObj);
	                	SearchOperations.setSearchHighlight(vttObj, targetStr, matchCase);
	                    break;
	            }
	        }
		}
		
		
	
	
	// GUI component
		private JPanel createCenterPanel()
		{
			// Option panel
			JPanel centerP = new JPanel();
			GridBagLayout gbl = new GridBagLayout();
			centerP.setLayout(gbl);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(3, 3, 3, 3);    // external padding
			gbc.fill = GridBagConstraints.HORIZONTAL;
			GridBag.setWeight(gbc, 100, 0);
			centerP.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Search a Keyword(s)"));
			// search term
			GridBag.setPosSize(gbc, 0, 0, 1, 1);
			centerP.add(new JLabel("Search:"), gbc);
			GridBag.setPosSize(gbc, 1, 0, 1, 1);
			centerP.add(targetStrTf_, gbc);
			// match case option
			GridBag.setPosSize(gbc, 0, 1, 1, 1);
			centerP.add(new JLabel("Options:"), gbc);
			GridBag.setPosSize(gbc, 1, 1, 1, 1);
			centerP.add(matchCaseCb_, gbc);
			return centerP;
		}
		private JPanel createButtonPanel()
		{
			// button panel: Ok, Cancel Button
			JPanel buttonP = new JPanel();
			buttonP.add(searchB_);
			buttonP.add(closeB_);
			return buttonP;
		}
	
	private void initGuiControllers(VttObj vttObj)
	{
		// controller
		searchDialogControl_ = new SearchDialogControl(vttObj);
		// buttom panel
		searchB_.setActionCommand(B_SEARCH);
		closeB_.setActionCommand(B_CLOSE);
		searchB_.addActionListener(searchDialogControl_);
		closeB_.addActionListener(searchDialogControl_);
		// target String
		targetStrTf_.setEditable(true); 
		targetStrTf_.addActionListener(searchDialogControl_);
	}
	private	void initGuiComponents(Window owner)
	{
		// basic setup: title, modal
		setTitle("VTT Find");
		setModal(false);
		// Geometry Setting
		setLocationRelativeTo(owner);
		//setLocation(700, 100);
		setMinimumSize(new Dimension(380, 150));
		// top level
		getContentPane().add(createCenterPanel(), "Center");
		getContentPane().add(createButtonPanel(), "South");
	}
	
	// final variables
		private final static long serialVersionUID = 5L;
		private final static int SEARCH = 0;
		private final static int CLOSE = 1;
		/** SEARCH button */
		public final static String B_SEARCH = "SEARCH";
		/** Close button */
		public final static String B_CLOSE = "CLOSE";
		// data members
		// controller
		private SearchDialogControl searchDialogControl_ = null;
		// local control vars
		private String targetStr_ = new String();
		private boolean matchCase_ = false;
		private int searchAction_ = SEARCH; 
		// buttom panel
		private JButton searchB_ = new JButton("Search");
		private JButton closeB_ = new JButton("Close");
		// center panel: find
		private JTextField targetStrTf_ = new JTextField(25);
		
		// options
		private JCheckBox matchCaseCb_ = new JCheckBox("Match case");

}
