package gov.nih.nlm.nls.vtt.gui;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import gov.nih.nlm.nls.vtt.guiControl.*;
/*****************************************************************************
* This class is the status panel to show information, including caret position,
* highlight range, etc.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class StatusPanel extends JPanel
{
    // public constructor
	/**
    * Create an StatusPanel object to show information.
    */
    public StatusPanel()
    {
		setLayout(new GridLayout(1, 1));
		
		// add text area to panel
		add(statusLabel_);
	}
	/**
    * Get the status label.
    *
    * @return the status label
    */
	public JLabel getStatusLabel()
	{
		return statusLabel_;
	}
	/**
    * Set the text of information.
    *
    * @param text  the String format of shown information
    */
	public void setText(String text)
	{
		statusLabel_.setText(text);
	}
	/**
    * Toggle show or not-show of this panel.
    */
	public void toggleShow()
	{
		boolean aFlag = statusLabel_.isVisible();
		statusLabel_.setVisible(!aFlag);
	}
	// final variables
    private final static long serialVersionUID = 5L;
	// data member
	private JLabel statusLabel_ = new JLabel("Markup Information");
}
