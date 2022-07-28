package gov.nih.nlm.nls.vtt.guiLib;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
/*****************************************************************************
* This class provides a JTextPane with linewrap option.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class LineWrapableTextPane extends JTextPane
{
	/**
    * Get the boolean flag of line wrap option
    *
    * @return the boolean flag of line wrap option
    */
	public boolean getScrollableTracksViewportWidth()
	{
		return lineWrap_;
	}
	/**
    * Set line wrap option to the specify value.
    *
    * @param lineWrap  the boolean flag of line wrap option
    */
	public void setLineWrap(boolean lineWrap)
	{
		lineWrap_ = lineWrap;
	}
	// final variables
	private final static long serialVersionUID = 5L;
	// data members
	private boolean lineWrap_ = true;
}
