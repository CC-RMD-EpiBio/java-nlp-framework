package gov.nih.nlm.nls.vtt.operations;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
/*****************************************************************************
* This class provides methods for global GUI related operations.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class GuiOperations
{
	// private constructor
	private GuiOperations()
	{
	}
	/**
    * Update Java GUI look and feel. It includes six options:
	* <ul>
	* <li>System
	* <li>Metal
	* <li>Motif
	* <li>Window
	* <li>GTK
	* <li>Metal
	* </ul>
    *
    * @param option  look and feel option
    * @param owner  the owner of this AddTagDialog
    */
	public static void updateLookAndFeel(String option, JFrame owner)
	{
		// update laf
		String laf = new String();
		if(option.equals("System") == true)
		{
			laf = UIManager.getSystemLookAndFeelClassName();
		}
		else if(option.equals("Metal") == true)
		{
			laf = METAL;
		}
		else if(option.equals("Motif") == true)
		{
			laf = MOTIF;
		}
		else if(option.equals("Window") == true)
		{
			laf = WINDOW;
		}
		else if(option.equals("GTK") == true)
		{
			laf = GTK;
		}
		else if(option.equals("Metal") == true)
		{
			laf = METAL;
		}
		else
		{
			laf = UIManager.getCrossPlatformLookAndFeelClassName();
		}
		// change look and feel
		try
		{
			UIManager.setLookAndFeel(laf);
			SwingUtilities.updateComponentTreeUI(owner);
		}
		catch(Exception e)
		{
			System.err.println("** ERR: This platform does not support: " 
				+ laf);
			final String fMsg = e.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
	// data member
	private final static String METAL 
		= "javax.swing.plaf.metal.MetalLookAndFeel";
	private final static String MOTIF 
		= "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	private final static String WINDOW 
		= "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	private final static String GTK 
		= "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
}
