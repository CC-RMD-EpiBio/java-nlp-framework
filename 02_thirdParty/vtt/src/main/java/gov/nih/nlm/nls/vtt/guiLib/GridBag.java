package gov.nih.nlm.nls.vtt.guiLib; 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
/*****************************************************************************
* This class provides methods for using GridBag layout manager.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class GridBag extends JPanel
{
	// private constructor
	private GridBag()
	{
	}
	/**
    * Set weight to GridBagConstraints for resizing.
	*
	* @param  gbc  grid bag constraints
	* @param  weightx  x weight
	* @param  weighty  y weight
    */
    public static void setWeight(GridBagConstraints gbc, int weightx, 
        int weighty) 
    {
        gbc.weightx = weightx;
        gbc.weighty = weighty;
    }
	/**
    * Set position and size to GridBagConstraints.
	*
	* @param  gbc  grid bag constraints
	* @param  x  x position
	* @param  y  y position
	* @param  width  width
	* @param  height  height
    */
    public static void setPosSize(GridBagConstraints gbc, int x, int y, 
        int width, int height)
    {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
    }
    private final static long serialVersionUID = 5L;
}
