package gov.nih.nlm.nls.vtt.gui;
import java.awt.*;

import javax.swing.*;
import javax.swing.text.*;

import gov.nih.nlm.nls.vtt.guiControl.*;
import gov.nih.nlm.nls.vtt.guiLib.*;
import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.util.UnderlineHighlighter;
/*****************************************************************************
* This class shows the main panel of VTT. It includes:
* <ul>
* <li>scrollPane - textPane_
* <li>VttPopupmenu
* <p>
* <li>MouseControl
* <li>SelectControl
* </ul>
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class MainPanel extends JPanel implements Cloneable
{
    // public constructor
	/**
    * Create a MainPanel object used in VTT with specifying vttObj.
    *
    * @param vttObj  the vttObj Java object
    */
    public MainPanel(VttObj vttObj)
    {
		// init main panel
		setLayout(new BorderLayout());
		setOpaque(true);
		
        // Create the text pane and configure it.
        textPane_ = new LineWrapableTextPane();
        textPane_.setMargin(new Insets(5,5,5,5));
		textPane_.setEditable(false);	// caret disappear
		setCaretPosition(0);
		// create scrolled text area
        JScrollPane scrollPane = new JScrollPane(textPane_);
        scrollPane.setPreferredSize(new Dimension(700, 800));
		// add text area to panel
		add(scrollPane, BorderLayout.CENTER);
		// add popup menu
		VttPopupMenu vttPopupMenu = new VttPopupMenu(vttObj);
		// Add controller (listenrer) for popupmenu and text caret
		textPane_.addMouseListener(new MouseControl(vttObj));
		textPane_.addKeyListener(new KeysControl(vttObj));
		textPane_.addCaretListener(new SelectControl(vttObj));
		textPane_.setHighlighter(new UnderlineHighlighter(Color.red));
		
		
	}
	/**
    * Get VTT styled document.
    *
    * @return VTT styled document
    */
	public StyledDocument getStyledDocument()
	{
		return textPane_.getStyledDocument();
	}
	/**
    * Get VTT text pane.
    *
    * @return VTT text pane
    */
	public LineWrapableTextPane getTextPane()
	{
		return textPane_;
	}
	/**
    * Get VTT caret position.
    *
    * @return VTT caret position
    */
	public int getCaretPosition()
	{
		return textPane_.getCaretPosition();
	}
	/**
    * Set highlight color of text panel.
    *
    * @param textColor  the text color of high light text
    * @param selectionColor  the selection (background) color of high light text
    */
	public void setHighlightColors(Color textColor, Color selectionColor)
	{
		textPane_.setSelectedTextColor(textColor);
		textPane_.setSelectionColor(selectionColor);
	}
	/**
    * Set VTT caret position.
    *
    * @param pos caret position
    */
	public void setCaretPosition(int pos)
	{
		// set caretUpdateByMouse to false because this method
		// is used to update caret position other than mouse
		useCaretUpdateByMouse_ = false;
		textPane_.setCaretPosition(pos);
	}
	/**
    * Set the boolean flag of using mouse to update VTT caret position.
    *
    * @param useCaretUpdateByMouse a boolean flag to use mouse update caret
    */
	public void setUseCaretUpdateByMouse(boolean useCaretUpdateByMouse)
	{
		useCaretUpdateByMouse_ = useCaretUpdateByMouse;
	}
	/**
    * Get the boolean flag of using mouse to update VTT caret position.
    *
    * @return the boolean flag to use mouse update caret
    */
	public boolean getUseCaretUpdateByMouse()
	{
		return useCaretUpdateByMouse_;
	}
	// TBD: real time update & get size
	/**
    * Toggle line wrap of text.
    */
	public void toggleLineWrap()
	{
		lineWrap_ = !lineWrap_;
		textPane_.setLineWrap(lineWrap_);
	}
	// final variables
    private final static long serialVersionUID = 5L;
	// data members
	private LineWrapableTextPane textPane_ = null;
	private boolean lineWrap_ = true;
	// flag for use caretUpdate by mouse in listener control
	private boolean useCaretUpdateByMouse_ = true;	
}
