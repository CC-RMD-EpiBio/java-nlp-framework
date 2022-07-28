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
* This class is the GUI dialog for VTT find feature.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class FindDialog extends JDialog
{
	// public constructor 
	/**
    * Create an FindDialog object to find a specified text in VTT.
    *
    * @param owner  the owner of this FindDialog
    * @param vttObj  the vttObj Java object
    */
	public FindDialog(Window owner, VttObj vttObj)
	{
		// constructor
		super(owner);
		// init GUI controller & GUI compoment
		initGuiControllers(vttObj);
		initGuiComponents(owner);
	}
		
	// GUI Control: Buttons control
	/**
    * Previous button: find the previous text.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitPrevious(VttObj vttObj)
	{
		findAction_ = PREVIOUS;
		// update local vars & effect on main panel
		updateGui(vttObj);
	}
	/**
    * Next button: find the next text.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitNext(VttObj vttObj)
	{
		findAction_ = NEXT;
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
		findStringInText(text, startPos, targetStr_, findAction_, 
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
		int newPos = 0;
		int size = targetStr.length();	// size of target string
		// check if use matchCase option
		if(matchCase == false)
		{
			text = text.toLowerCase();
			targetStr = targetStr.toLowerCase();
		}
		// find the target String
		if((text != null) && (text.length() > 0))
        {
            switch(findAction)
            {
                case PREVIOUS:
					int selectMarkupIndex 
						= vttObj.getVttDocument().getMarkups().getSelectIndex();
					int start = startPos-size-1;
                    newPos = text.lastIndexOf(targetStr, start);
					updateStyleOnFoundWord(newPos, size, newPos, targetStr, 
						findAction, vttObj);
                    break;
                case NEXT:
                    newPos = text.indexOf(targetStr, startPos);
					updateStyleOnFoundWord(newPos, size, newPos+size, targetStr,
						findAction, vttObj);
                    break;
            }
        }
	}
	private void updateStyleOnFoundWord(int start, int size, int newPos,
		String targetStr, int findAction, VttObj vttObj)
	{
		// reach the end of file, beep and continue to find?
		if(start <= -1)
		{
			// ContinueSearch(findAction, )
			Toolkit.getDefaultToolkit().beep();	
			switch(findAction)
			{
				case PREVIOUS:
					String msg = "Reach the begining of file, no more \"" 
						+ targetStr 
						+ "\" found.\n Continue to find from the end?";
					int selectOption = JOptionPane.showConfirmDialog(this, 
						msg, "VTT Find: Previous", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
					if(selectOption == JOptionPane.YES_OPTION)
					{
						vttObj.getMainFrame().getMainPanel().setCaretPosition(
							vttObj.getVttDocument().getText().length());
						updateGui(vttObj);	
					}
					break;
				case NEXT:
					String msg2 = "Reach the end of file, no more \"" 
						+ targetStr 
						+ "\" found.\n Continue to find from the begining?";
					int selectOption2 = JOptionPane.showConfirmDialog(this, 
						msg2, "VTT Find: Next", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
					if(selectOption2 == JOptionPane.YES_OPTION)
					{
						vttObj.getMainFrame().getMainPanel().setCaretPosition(0);
						updateGui(vttObj);	
					}
					break;
			}
		}
		else
		{
			// update the selected markup index
			vttObj.getVttDocument().getMarkups().findSelectIndex(start, size);
			int selectMarkupIndex 
				= vttObj.getVttDocument().getMarkups().getSelectIndex();
			// use text Panel select if no a markup
			if(selectMarkupIndex == Markups.NO_SELECT)
			{
				// Can't reset the caret so selection can be seen
				vttObj.setHighlightStart(start);
				vttObj.setHighlightEnd(start + size);
				vttObj.getMainFrame().getMainPanel().setUseCaretUpdateByMouse(
					false);
				vttObj.getMainFrame().getMainPanel().getTextPane().select(
					vttObj.getHighlightStart(), vttObj.getHighlightEnd());
			}
			else	// use selected markup if match the target string
			{
				// reset highlight
				vttObj.setHighlightStart(newPos);
				vttObj.setHighlightEnd(newPos);
				// remark the style
				TextDisplayOperations.updateStyle(
					TextDisplayOperations.MARKUPS, vttObj);
				// synchronize caret
				vttObj.getMainFrame().getMainPanel().setCaretPosition(newPos);	
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
			BorderFactory.createEtchedBorder(), "Find a Term"));
		// find term
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		centerP.add(new JLabel("Find:"), gbc);
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
		buttonP.add(previousB_);
		buttonP.add(nextB_);
		buttonP.add(closeB_);
		return buttonP;
	}
	private void initGuiControllers(VttObj vttObj)
	{
		// controller
		findDialogControl_ = new FindDialogControl(vttObj);
		// buttom panel
		previousB_.setActionCommand(B_PREVIOUS);
		nextB_.setActionCommand(B_NEXT);
		closeB_.setActionCommand(B_CLOSE);
		previousB_.addActionListener(findDialogControl_);
		nextB_.addActionListener(findDialogControl_);
		closeB_.addActionListener(findDialogControl_);
		// target String
		targetStrTf_.setEditable(true); 
		targetStrTf_.addActionListener(findDialogControl_);
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
	private final static int NEXT = 0;
	private final static int PREVIOUS = 1;
	/** Next button */
	public final static String B_NEXT = "NEXT";
	/** Previous button */
	public final static String B_PREVIOUS = "PREVIOUS";
	/** Close button */
	public final static String B_CLOSE = "CLOSE";
	// data members
	// controller
	private FindDialogControl findDialogControl_ = null;
	// local control vars
	private String targetStr_ = new String();
	private boolean matchCase_ = false;
	private int findAction_ = NEXT; 
	// buttom panel
	private JButton previousB_ = new JButton("Previous");
	private JButton nextB_ = new JButton("Next");
	private JButton closeB_ = new JButton("Close");
	// center panel: find
	private JTextField targetStrTf_ = new JTextField(25);
	
	// options
	private JCheckBox matchCaseCb_ = new JCheckBox("Match case");
}
