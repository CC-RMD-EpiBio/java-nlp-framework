package gov.nih.nlm.nls.vtt.gui;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
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
public class ZoomDialog extends JDialog
{
	// public constructor 
	/**
    * Create an ZoomDialog object to zoom in/out text in VTT.
    *
    * @param owner  the owner of this FindDialog
    * @param vttObj  the vttObj Java object
    */
	public ZoomDialog(Window owner, VttObj vttObj)
	{
		// constructor
		super(owner);
		// init GUI controller & GUI compoment
		initGuiControllers(vttObj);
		initGuiComponents(owner);
	}
		
	// GUI Control: Buttons control
	/**
    * Default button: set the zoom factor to default.
    *
    * @param vttObj  the vttObj Java object
    */
	public void hitDefault(VttObj vttObj)
	{
		zoomFactor_ = ZOOM_INIT;
		updateGuiFromLocalVars();
		updateGlobalFromLocalVars(vttObj);
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
		// update zoom factor slider
		updateLocalVarsFromGrobal(vttObj);
		updateGuiFromLocalVars();
	}
	/**
    * Update local variables from the update of GUI.
    */
	public void updateLocalVarsFromGui()
	{
		zoomFactor_ = zoomSl_.getValue();
	}
	/**
    * Update global variables from the update of local variables.
    *
    * @param vttObj  the vttObj Java object
    */
	public void updateGlobalFromLocalVars(VttObj vttObj)
	{
		vttObj.getConfigObj().setZoomFactor(zoomFactor_);
	}
	// private methods
	// update GUI from Control vars
	private void updateLocalVarsFromGrobal(VttObj vttObj)
	{
		zoomFactor_ = vttObj.getConfigObj().getZoomFactor();
	}
	private void updateGuiFromLocalVars()
	{
		// zoom factor slider
		zoomSl_.setValue(zoomFactor_);
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
			BorderFactory.createEtchedBorder(), "Zoom Factor"));
		// Zoom slider term
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		//centerP.add(new JLabel("Zoom Factor: ("  + zoomFactor_ + ")"), gbc);
		//GridBag.SetPosSize(gbc, 1, 0, 1, 1);
		centerP.add(zoomSl_, gbc);
		return centerP;
	}
	private JPanel createButtonPanel()
	{
		// button panel: Ok, Cancel Button
		JPanel buttonP = new JPanel();
		buttonP.add(defaultB_);
		buttonP.add(closeB_);
		return buttonP;
	}
	private void initGuiControllers(VttObj vttObj)
	{
		// controller
		zoomDialogControl_ = new ZoomDialogControl(vttObj);
		// buttom panel
		closeB_.setActionCommand(B_CLOSE);
		defaultB_.setActionCommand(B_DEFAULT);
		closeB_.addActionListener(zoomDialogControl_);
		defaultB_.addActionListener(zoomDialogControl_);
		// Zoom Slider
		zoomSl_.addChangeListener(zoomDialogControl_);
	}
	private	void initGuiComponents(Window owner)
	{
		// basic setup: title, modal
		setTitle("VTT Zoom +/-");
		setModal(true);
		// Geometry Setting
		setLocationRelativeTo(owner);
		//setLocation(700, 100);
		setMinimumSize(new Dimension(380, 150));
		// slider: Tick and labels
		zoomSl_.setMajorTickSpacing(5);
		zoomSl_.setMinorTickSpacing(1);
		zoomSl_.setPaintTrack(true);	// paint track, default
		zoomSl_.setPaintTicks(true);	// paint ticks
		zoomSl_.setPaintLabels(true);	// paint label
		zoomSl_.setSnapToTicks(true);	// use the closest tick
		// top level
		getContentPane().add(createCenterPanel(), "Center");
		getContentPane().add(createButtonPanel(), "South");
	}
	// final variables
	private final static long serialVersionUID = 5L;
	private final static int ZOOM_MIN = -10;
	private final static int ZOOM_MAX = 30;
	private final static int ZOOM_INIT = 0;
	/** Default button */
	public final static String B_DEFAULT = "DEFAULT";
	/** Close button */
	public final static String B_CLOSE = "CLOSE";
	// data members
	// controller
	private ZoomDialogControl zoomDialogControl_ = null;
	// local control vars
	private int zoomFactor_ = ZOOM_INIT;
	// buttom panel
	private JButton defaultB_ = new JButton("Default");
	private JButton closeB_ = new JButton("Close");
	// center panel: find
	private JSlider zoomSl_ = new JSlider(JSlider.HORIZONTAL, ZOOM_MIN,
		ZOOM_MAX, ZOOM_INIT);
}
