package gov.nih.nlm.nls.vtt.model;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.util.jar.*;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.guiLib.*;
/*****************************************************************************
* This class defines global variables used in Visual Tagging Tool.
* 
* <p><b>History:</b>
* <ul>
* <li>SCR-94, chlu, 02-03-10, new VTT file format to include Meta-Data
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class VttGui 
{
	// public constructor
	/**
	* Create a VttGui Java object with default values.
	*/
    public VttGui()
    {
    }
	// public methods
	/**
	* Get Vtt main frame.
	*
	* @return Vtt main frame
	*/
	public MainFrame getMainFrame()
	{
		return mainFrame_;
	}
	/**
	* Get Vtt popup menu.
	*
	* @return Vtt popup menu
	*/
	public VttPopupMenu getVttPopupMenu()
	{
		return vttPopupMenu_;
	}
	/**
	* Get Vtt help document.
	*
	* @return Vtt help document
	*/
	public DocHtmlBrowser getHelpDoc()
	{
		return helpDoc_;
	}
	/**
	* Get markup dialog.
	*
	* @return markup dialog
	*/
	public MarkupDialog getMarkupDialog()
	{
		return markupDialog_;
	}
	/**
	* Get markups dialog.
	*
	* @return markups dialog
	*/
	public MarkupsDialog getMarkupsDialog()
	{
		return markupsDialog_;
	}
	/**
	* Get tags dialog.
	*
	* @return tags dialog
	*/
	public TagsDialog getTagsDialog()
	{
		return tagsDialog_;
	}
	/**
	* Get tag eidt dialog.
	*
	* @return tag eidt dialog
	*/
	public EditTagDialog getEditTagDialog()
	{
		return editTagDialog_;
	}
	/**
	* Get tag add dialog.
	*
	* @return tag add dialog
	*/
	public AddTagDialog getAddTagDialog()
	{
		return addTagDialog_;
	}
	/**
	* Get find dialog.
	*
	* @return find dialog
	*/
	public FindDialog getFindDialog()
	{
		return findDialog_;
	}
	/**
	* Get search dialog.
	*
	* @return search dialog
	*/
	public SearchDialog getSearchDialog()
	{
		return searchDialog_;
	}
	
	/**
	* Get zoom dialog.
	*
	* @return zoom dialog
	*/
	public ZoomDialog getZoomDialog()
	{
		return zoomDialog_;
	}
	/**
	* Get configuration option dialog.
	*
	* @return configuration option dialog
	*/
	public ConfigOptionsDialog getConfigOptionsDialog()
	{
		return configOptionsDialog_;
	}
	/**
	* Get Meta data option dialog.
	*
	* @return Meta data option dialog
	*/
	public MetaDataDialog getMetaDataDialog()
	{
		return metaDataDialog_;
	}
	/**
	* Set Vtt main frame.
	*
	* @param mainFrame Vtt main frame
	*/
	public void setMainFrame(MainFrame mainFrame)
	{
		mainFrame_ = mainFrame;
	}
	/**
	* Set Vtt help document.
	*
	* @param helpDoc Vtt help document
	*/
	public void setHelpDoc(DocHtmlBrowser helpDoc)
	{
		helpDoc_ = helpDoc;
	}
	/**
	* Get start position of highlight.
	*
	* @return start position of highlight
	*/
    public int getHighlightStart()
    {
        return highlightStart_;
    }
	/**
	* Set end position of highlight.
	*
	* @return end position of highlight
	*/
    public int getHighlightEnd()
    {
        return highlightEnd_;
    }
	/**
	* Get the mode of overlap
	*
	* @return a boolean flag of overlap mode
	*/
    public boolean getOverlap()
    {
        return overlap_;
    }
	/**
	* Get Vtt undo manager.
	*
	* @return Vtt undo manager
	*/
    public UndoManager getUndoManager()
    {
        return undoManager_;
    }
	/**
	* Set the start position of highlight.
	*
	* @param highlightStart the start position of highlight
	*/
    public void setHighlightStart(int highlightStart)
    {
        highlightStart_ = highlightStart;
    }
	/**
	* Set the end position of highlight.
	*
	* @param highlightEnd the end position of highlight
	*/
    public void setHighlightEnd(int highlightEnd)
    {
        highlightEnd_ = highlightEnd;
    }
	/**
	* Set the Vtt overlap mode.
	*
	* @param overlap the Vtt overlap mode
	*/
    public void setOverlap(boolean overlap)
    {
        overlap_ = overlap;
    }
	// protected methods
	/**
	* Init GUI components
	*
	* @param vttObj the Vtt object
	* @param mainFrame Vtt main frame
	*/
	protected void initGui(VttObj vttObj, MainFrame mainFrame)
	{
		// init main frame
		mainFrame_ = mainFrame;
		mainFrame_.setVisible(true);
		vttPopupMenu_ = new VttPopupMenu(vttObj);
		// init dialog
		markupDialog_ = new MarkupDialog(mainFrame_, vttObj);
		markupsDialog_ = new MarkupsDialog(mainFrame_, vttObj);
		tagsDialog_ = new TagsDialog(mainFrame_, vttObj);
		editTagDialog_ = new EditTagDialog(tagsDialog_, vttObj);
		addTagDialog_ = new AddTagDialog(tagsDialog_, vttObj);
		findDialog_ = new FindDialog(mainFrame_, vttObj);
		searchDialog_ = new SearchDialog(mainFrame_, vttObj);
		zoomDialog_ = new ZoomDialog(mainFrame_, vttObj);
		configOptionsDialog_ = new ConfigOptionsDialog(mainFrame_, vttObj);
		metaDataDialog_ = new MetaDataDialog(mainFrame_, vttObj);
	}
	// private methods
	// data member
	// GUI: View
	private MainFrame mainFrame_ = null; // includes VttMenuBar, MainPanel, StatusPanel
	
	private VttPopupMenu vttPopupMenu_ = null;
	private static DocHtmlBrowser helpDoc_ = null;	// only one help document
	// dialogs
	private MarkupDialog markupDialog_ = null;
	private MarkupsDialog markupsDialog_ = null;
	private TagsDialog tagsDialog_ = null;
	private EditTagDialog editTagDialog_ = null;
	private AddTagDialog addTagDialog_ = null;
	private FindDialog findDialog_ = null;
	private SearchDialog searchDialog_ = null;
	private ZoomDialog zoomDialog_ = null;
	private ConfigOptionsDialog configOptionsDialog_ = null;
	private MetaDataDialog metaDataDialog_ = null;
	// GUI: control vars
	private int highlightStart_ = 0;    // highlight start position
    private int highlightEnd_ = 0;      // highlight end position
    private boolean overlap_ = false;   // flag of a markup can be overlap tag
    private UndoManager undoManager_ = new UndoManager();   // undo/redo manager
}
