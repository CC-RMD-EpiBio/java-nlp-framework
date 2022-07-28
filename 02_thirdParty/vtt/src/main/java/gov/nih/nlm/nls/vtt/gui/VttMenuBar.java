package gov.nih.nlm.nls.vtt.gui;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import gov.nih.nlm.nls.vtt.guiControl.*;
import gov.nih.nlm.nls.vtt.guiLib.*;
import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This class defines all GUI menu bar setup. 
* The action is performed in MainFrame.
* 
* <p><b>History:</b>
* <ul>
* <li>SCR-94, chlu, 02-03-10, new VTT file format to include Meta-Data
* <li>SCR-95, chlu, 02-04-10, one click to reload tags files
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class VttMenuBar extends JMenuBar
{
	// public constructor
	/**
    * Create a VttMenuBar object for VTT menu bar.
    *
    * @param owner  the owner of this VttMenuBar
    * @param vttObj  the vttObj Java object
    */
	public VttMenuBar(JFrame owner, VttObj vttObj)
	{
		vttMenuBarControl_ = new VttMenuBarControl(owner, vttObj);
		// Add sub menu top menu bar
		add(createVttMenu());
		add(createTextMenu());
		add(createTagMenu());
		add(createMarkupMenu(vttObj));
		add(createOptionMenu());
		add(createHelpMenu());
	}
	// public method
	/**
    * Update GUI from control variables.
    *
    * @param vttObj  the vttObj Java object
    */
	public void updateGuiFromControl(VttObj vttObj)
	{
		// update tags quick load
		boolean quickLoadFlag = true;
		String tagsFile = vttObj.getVttDocument().getMetaData().getTagsFile();
		if((tagsFile == null) || (tagsFile.length() == 0))
		{
			quickLoadFlag = false;
		}
		quickLoadItem_.setEnabled(quickLoadFlag);

		// update undo
		undoItem_.setEnabled(vttObj.getUndoManager().isUndoable());
		// update redo
		redoItem_.setEnabled(vttObj.getUndoManager().isRedoable());
	}
	/**
    * Set text menu to the default setting.
    */
	public void setTextMenuToDefault()
	{
		boldItem_.setState(false);
		italicItem_.setState(false);
		underlineItem_.setState(false);
		// font family & size
		monospacedItem_.setSelected(true);
	}
	// private methods
	private JMenu createVttMenu()
    {
		// sub menu
        JMenu vttMenu = new JMenu("Vtt");
        vttMenu.setMnemonic(KeyEvent.VK_V);
        // menu item for sub menu: accelerators
        JMenuItem openItem = new JMenuItem(VTT_OPEN);
        //openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
            //InputEvent.ALT_DOWN_MASK));
        JMenuItem saveItem = new JMenuItem(VTT_SAVE);
        JMenuItem saveAsItem = new JMenuItem(VTT_SAVE_AS);
        JMenuItem saveAsExcelItem = new JMenuItem(VTT_SAVE_AS_EXCEL);
        JMenuItem closeItem = new JMenuItem(VTT_CLOSE);
        JMenuItem printItem = new JMenuItem(VTT_PRINT);
        JMenuItem exitItem = new JMenuItem(VTT_EXIT);
		
		// put together
        JMenuItem[] vttItems = {
			openItem, saveItem, saveAsItem, saveAsExcelItem, null,
			printItem, closeItem, exitItem
			};
		// add item to menu
		addItemToMenu(vttMenu, vttItems);
		return vttMenu;
    }
	private JMenu createTextMenu()
    {
		// sub menu
		JMenu textMenu = new JMenu("Text");
        textMenu.setMnemonic(KeyEvent.VK_T);
		// font
		monospacedItem_.setSelected(true);
        ButtonGroup fontGroup = new ButtonGroup();
        fontGroup.add(dialogItem_);
        fontGroup.add(dialogInputItem_);
        fontGroup.add(monospacedItem_);
        fontGroup.add(sansSerifItem_);
        fontGroup.add(serifItem_);
		Object[] fontItems = {
			dialogItem_, dialogInputItem_, monospacedItem_, 
			sansSerifItem_, serifItem_
			};
		// font size
		JMenuItem sizeItem = new JMenuItem(TEXT_FONT_SIZE);
		// default
        JMenuItem defaultItem = new JMenuItem(TEXT_DEFAULT);
		// selction colors
        JMenuItem highlightTextColorItem = new JMenuItem(TEXT_HIGHLIGHT_COLOR);
        JMenuItem highlightBackgroundColorItem 
			= new JMenuItem(TEXT_HIGHLIGHT_BG_COLOR);
		// about
        JMenuItem whatItem = new JMenuItem(TEXT_HELP);
		// put together
		JMenuItem[] textItems = {
			boldItem_, italicItem_, underlineItem_,
			makeMenu("Font", fontItems), sizeItem,
			defaultItem, null,
			highlightTextColorItem, highlightBackgroundColorItem, null,
			whatItem
			};
		// add item to menu
		addItemToMenu(textMenu, textItems);
		return textMenu;
	}
	private JMenu createTagMenu()
    {
		// sub menu
        JMenu tagMenu = new JMenu("Tags");
        tagMenu.setMnemonic(KeyEvent.VK_G);
        // menu item for sub menu: accelerators
        JMenuItem propertyItem = new JMenuItem(TAG_SETUP);
        //propertyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
            //InputEvent.ALT_DOWN_MASK));
        JMenuItem displayFilterItem = new JMenuItem(TAG_DISPLAY_FILTER);
        JMenuItem quickKeyMapItem = new JMenuItem(TAG_QUICK_KEY_MAP);
        JMenuItem saveImportItem = new JMenuItem(TAG_SAVE_IMPORT);
        JMenuItem whatItem = new JMenuItem(TAG_HELP);
        JMenuItem[] tagItems = {
			propertyItem, displayFilterItem, quickKeyMapItem,
			saveImportItem, null, quickLoadItem_, null, whatItem
			};
		// add item to menu
		addItemToMenu(tagMenu, tagItems);
		// Update: add action listener to update tagItems: quickLoad
		tagMenu.addMenuListener(vttMenuBarControl_);
		return tagMenu;
    }
	private JMenu createMarkupMenu(VttObj vttObj)
    {
		// sub menu
        JMenu markupMenu = new JMenu("Markups");
        markupMenu.setMnemonic(KeyEvent.VK_M);
        // menu item for sub menu: accelerators
        JMenuItem logItem = new JMenuItem(MARKUP_DETAILS);
        JMenuItem reportItem = new JMenuItem(MARKUP_REPORTS);
        JMenuItem undoListItem = new JMenuItem(MARKUP_UNDO_LIST);
        JMenuItem deleteItem = new JMenuItem(MARKUP_DELETE);
        JMenuItem deleteAllItem = new JMenuItem(MARKUP_DELETE_ALL);
        JMenuItem saveToFile = new JMenuItem(MARKUP_SAVE_TO_FILE);
		JCheckBoxMenuItem overlapItem = new JCheckBoxMenuItem(MARKUP_OVERLAP,
			vttObj.getOverlap());
        JMenuItem whatItem = new JMenuItem(MARKUP_HELP);
        JMenuItem[] markupItems = {
			logItem, reportItem, undoListItem, null, 
			deleteItem, deleteAllItem, saveToFile, null, 
			undoItem_, redoItem_, null, 
			overlapItem, null,
			whatItem
			};
		// add item to menu
		addItemToMenu(markupMenu, markupItems);
		// Update: add action listener to update menuItems: undo, redo
		markupMenu.addMenuListener(vttMenuBarControl_);
		return markupMenu;
    }
	private JMenu createOptionMenu()
    {
		// sub menu
        JMenu optionMenu = new JMenu("Options");
        optionMenu.setMnemonic(KeyEvent.VK_O);
		// look and feel
		JRadioButtonMenuItem systemItem 
			= new JRadioButtonMenuItem(OPTION_LAF_SYSTEM);
		JRadioButtonMenuItem metalItem 
			= new JRadioButtonMenuItem(OPTION_LAF_METAL);
		JRadioButtonMenuItem motifItem 
			= new JRadioButtonMenuItem(OPTION_LAF_MOTIF);
		JRadioButtonMenuItem windowItem 
			= new JRadioButtonMenuItem(OPTION_LAF_WINDOW);
		JRadioButtonMenuItem gtkItem = new JRadioButtonMenuItem(OPTION_LAF_GTK);
		metalItem.setSelected(true);
		ButtonGroup lafGroup = new ButtonGroup();
		lafGroup.add(systemItem);
		lafGroup.add(metalItem);
		lafGroup.add(motifItem);
		lafGroup.add(windowItem);
		lafGroup.add(gtkItem);
		Object[] lafItems = {
			systemItem, metalItem, motifItem, windowItem, gtkItem
			};
		// others: configuration, find, show status
        JMenuItem zoomItem = new JMenuItem(OPTION_ZOOM);
        JMenuItem findItem = new JMenuItem(OPTION_FIND);
        JMenuItem searchItem = new JMenuItem(OPTION_SEARCH);
		JCheckBoxMenuItem lineWrapItem = new JCheckBoxMenuItem(OPTION_LINE_WRAP,
			true);
		JCheckBoxMenuItem showStatusItem = new JCheckBoxMenuItem(
			OPTION_SHOW_STATUS, true);
        JMenuItem compareItem = new JMenuItem(OPTION_COMPARE);
        JMenuItem configSetupItem = new JMenuItem(OPTION_CONFIG_SETUP);
		
		// meta data:
		JMenuItem tagsFileItem = new JMenuItem(META_TAGS_FILE);
		JMenuItem saveHistoryItem = new JMenuItem(META_SAVE_HISTORY);
		Object[] metaItems = {tagsFileItem, saveHistoryItem};
		// put together
        JMenuItem[] optionItems = {
			makeMenu("Look and Feel", lafItems), 
			zoomItem, findItem, searchItem, lineWrapItem, showStatusItem, null,
			compareItem, null, configSetupItem, 
			makeMenu("Meta-Data", metaItems)
			};
		// add item to menu
		addItemToMenu(optionMenu, optionItems);
		return optionMenu;
	}
	private JMenu createHelpMenu()
	{
		// sub Help Menu: mnemonics
		JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
		// menu item
		JMenuItem[] helpItems = {
			new JMenuItem(HELP_ABOUT),
			new JMenuItem(HELP_KEYS_MAP),
			new JMenuItem(HELP_DOCUMENTS)
			};
		// add item to menu
		addItemToMenu(helpMenu, helpItems);
		return helpMenu;
	}
	private void addItemToMenu(JMenu menu, JMenuItem[] items)
	{
		// add menu items to menu
		for(int i = 0; i < items.length; i++)
		{
			// add a separator if input is null
			if(items[i] == null)
			{
				menu.addSeparator();
			}
			else
			{
				items[i].addActionListener(vttMenuBarControl_);
				menu.add(items[i]);
			}
		}
	}
	private JMenu makeMenu(Object parent, Object[] items)
	{
		JMenu menu = null;
        if(parent instanceof JMenu)
        {
            menu = (JMenu) parent;
        }
        else if(parent instanceof String)
        {
            menu = new JMenu((String) parent);
        }
        else
        {
            return null;
        }
		for(int i = 0; i < items.length; i++)
        {
            if(items[i] == null)        // add a separator if input is null
            {
                menu.addSeparator();
            }
            else
            {
                menu.add(makeMenuItem(items[i]));
            }
        }
        return menu;
	}
	private JMenuItem makeMenuItem(Object item)
	{
        JMenuItem mi = null;
        if(item instanceof String)
        {
            mi = new JMenuItem((String)item);
        }
        else if(item instanceof JMenuItem)
        {
            mi = (JMenuItem)item;
        }
        else
        {
            return null;
        }
		mi.addActionListener(vttMenuBarControl_);
        return mi;
    }
	// final variables
	private static final long serialVersionUID = 5L;
	/** menu item: Vtt open */
	public static final String VTT_OPEN = "Open (o)";
	/** menu item: Vtt save */
	public static final String VTT_SAVE = "Save (s)";
	/** menu item: Vtt save as */
	public static final String VTT_SAVE_AS = "Save As (a)";
	/** menu item: Vtt save as excel */
	public static final String VTT_SAVE_AS_EXCEL = "Export To Excel (e)";
	/** menu item: Vtt print */
	public static final String VTT_PRINT = "Print (p)";
	/** menu item: Vtt close */
	public static final String VTT_CLOSE = "Close (c)";
	/** menu item: Vtt exit */
	public static final String VTT_EXIT = "Exit (x)";
	/** menu item: text bold */
	public static final String TEXT_BOLD = "Bold";
	/** menu item: text italic */
	public static final String TEXT_ITALIC = "Italic";
	/** menu item: text underline */
	public static final String TEXT_UNDERLINE = "Underline";
	/** menu item: text font size */
	public static final String TEXT_FONT_SIZE = "Font Size";
	/** menu item: text default */
	public static final String TEXT_DEFAULT = "Default Text Style";
	/** menu item: text font serif */
	public static final String TEXT_FONT_SERIF = "Serif";
	/** menu item: text font SansSerif */
	public static final String TEXT_FONT_SANSSERIF = "SansSerif";
	/** menu item: text font Monospaced */
	public static final String TEXT_FONT_MONOSPACED = "Monospaced";
	/** menu item: text font Dialog */
	public static final String TEXT_FONT_DIALOG = "Dialog";
	/** menu item: text font DialogInput */
	public static final String TEXT_FONT_DIALOGINPUT = "DialogInput";
	/** menu item: text highlight color */
	public static final String TEXT_HIGHLIGHT_COLOR = "Highlight Text Color";
	/** menu item: text background color */
	public static final String TEXT_HIGHLIGHT_BG_COLOR = "Highlight Background Color";
	/** menu item: text help */
	public static final String TEXT_HELP = "Text ?";
	/** menu item: tag setup */
	public static final String TAG_SETUP = "Setup";
	/** menu item: tag display filter */
	public static final String TAG_DISPLAY_FILTER = "Display Filter";
	/** menu item: tag quick key mapping */
	public static final String TAG_QUICK_KEY_MAP = "Quick Key Mapping";
	/** menu item: tag save/import */
	public static final String TAG_SAVE_IMPORT = "Save & Import";
	/** menu item: tag quick load */
	public static final String TAG_QUICK_LOAD = "Quick load Tags (q)";
	/** menu item: tag help */
	public static final String TAG_HELP = "Tags ?";
	/** menu item: markup details */
	public static final String MARKUP_DETAILS = "Log Details";
	/** menu item: markup reports */
	public static final String MARKUP_REPORTS = "Reports";
	/** menu item: markup undlo list */
	public static final String MARKUP_UNDO_LIST = "Undo Manager Log";
	/** menu item: markup delete */
	public static final String MARKUP_DELETE = "Delete";
	/** menu item: markup delete all */
	public static final String MARKUP_DELETE_ALL = "Delete All";
	/** menu item: markup save to file */
	public static final String MARKUP_SAVE_TO_FILE = "Save To File";
	/** menu item: markup undo */
	public static final String MARKUP_UNDO = "Undo (u)";
	/** menu item: markup redo */
	public static final String MARKUP_REDO = "Redo (r)";
	/** menu item: markup overlap */
	public static final String MARKUP_OVERLAP = "Overlap";
	/** menu item: markup help */
	public static final String MARKUP_HELP = "Markups ?";
	/** menu item: option look and feel system */
	public static final String OPTION_LAF_SYSTEM = "System";
	/** menu item: option look and feel metal */
	public static final String OPTION_LAF_METAL = "Metal";
	/** menu item: option look and feel motif */
	public static final String OPTION_LAF_MOTIF = "Motif";
	/** menu item: option look and feel window */
	public static final String OPTION_LAF_WINDOW = "Window";
	/** menu item: option look and feel GTK */
	public static final String OPTION_LAF_GTK = "GTK";
	/** menu item: option zoom */
	public static final String OPTION_ZOOM = "Zoom +/- (z)";
	/** menu item: option find */
	public static final String OPTION_FIND = "Find ... (f)";
	/** menu item: option search */
	public static final String OPTION_SEARCH = "Search ... (s)";
	/** menu item: option line wrap */
	public static final String OPTION_LINE_WRAP = "Line Wrapping";
	/** menu item: option show status */
	public static final String OPTION_SHOW_STATUS = "Show Status";
	/** menu item: option compare */
	public static final String OPTION_COMPARE = "Compare to ... (t)";
	/** menu item: option config setup */
	public static final String OPTION_CONFIG_SETUP = "Config Setup";
	/** menu item: option Meta tags file */
	public static final String META_TAGS_FILE = "Default Tags File";
	/** menu item: option Meta save history */
	public static final String META_SAVE_HISTORY = "Save History";
	/** menu item: help about */
	public static final String HELP_ABOUT = "About";
	/** menu item: help keps map */
	public static final String HELP_KEYS_MAP = "Keys Mapping Table";
	/** menu item: help documents */
	public static final String HELP_DOCUMENTS = "Documents (h)";
	// data member
	// control
	private VttMenuBarControl vttMenuBarControl_ = null;
	// tags quick load
    private JMenuItem quickLoadItem_ = new JMenuItem(TAG_QUICK_LOAD);
	// undo/redo
    private JMenuItem undoItem_ = new JMenuItem(MARKUP_UNDO);
    private JMenuItem redoItem_ = new JMenuItem(MARKUP_REDO);
	// bold, italic, underline
	private JCheckBoxMenuItem boldItem_ 
		= new JCheckBoxMenuItem(TEXT_BOLD, false);
	private JCheckBoxMenuItem italicItem_ 
		= new JCheckBoxMenuItem(TEXT_ITALIC, false);
	private JCheckBoxMenuItem underlineItem_ 
		= new JCheckBoxMenuItem(TEXT_UNDERLINE, false);
	// font family
    private JRadioButtonMenuItem dialogItem_ 
		= new JRadioButtonMenuItem(TEXT_FONT_DIALOG);
	private JRadioButtonMenuItem dialogInputItem_
		= new JRadioButtonMenuItem(TEXT_FONT_DIALOGINPUT);
	private JRadioButtonMenuItem monospacedItem_
		= new JRadioButtonMenuItem(TEXT_FONT_MONOSPACED);
	private JRadioButtonMenuItem sansSerifItem_
		= new JRadioButtonMenuItem(TEXT_FONT_SANSSERIF);
	private JRadioButtonMenuItem serifItem_ 
		= new JRadioButtonMenuItem(TEXT_FONT_SERIF);
}
