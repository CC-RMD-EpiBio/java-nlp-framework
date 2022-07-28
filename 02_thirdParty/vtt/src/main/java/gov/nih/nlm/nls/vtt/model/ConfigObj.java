package gov.nih.nlm.nls.vtt.model;
import java.io.*;

import java.awt.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
/*****************************************************************************
* This class provides the Java object for configured variables.
*
* <p><b>History:</b>
* <ul>
* <li>SCR-96, chlu, 02/01/10, add Username to VTT config file
* </ul>
*
* @author NLM NLS Development Team, clu
*
* @version    V-2010
****************************************************************************/
public class ConfigObj
{
    // public constructor
	/**
    * Create a ConfigObj Java object with default values.
    */
    public ConfigObj()
	{
	}
	/**
    * Create a ConfigObj Java object by specifying a configuration object.
	*
	* @param conf  the configuration object
    */
    public ConfigObj(Configuration conf)
    {
		setVarsFromConfig(conf);
    }
	/**
    * Create a ConfigObj Java object by specifying a ConfigObj object.
	*
	* @param configObj  the ConfigObj object
    */
	public ConfigObj(ConfigObj configObj)
	{
		setVarsFromConfigObj(configObj);
	}
	// public methods
	/**
    * Set configuration variables by specifying a ConfigObj object.
	*
	* @param configObj  the ConfigObj object
    */
	public void setVarsFromConfigObj(ConfigObj configObj)
	{
		if(configObj != null)
		{
			vttDir_ = configObj.getVttDir(); 
			docDir_ = configObj.getDocDir();
			tagFile_ = configObj.getTagFile();
			userName_ = configObj.getUserName();
			vttFormat_ = configObj.getVttFormat();
			filePathDisplayLength_ = configObj.getFilePathDisplayLength();
			baseFontSize_ = configObj.getBaseFontSize();
			zoomFactor_ = configObj.getZoomFactor();
			offsetSize_ = configObj.getOffsetSize();
			lengthSize_ = configObj.getLengthSize();
			tagNameSize_ = configObj.getTagNameSize();
			tagCategorySize_ = configObj.getTagCategorySize();
					
			// select markup style attribute
			useTextTagForNotDisplaySelectMarkup_ 
				= configObj.isUseTextTagForNotDisplaySelectMarkup();
			overwriteSelectBold_ = configObj.isOverwriteSelectBold();
			overwriteSelectItalic_ = configObj.isOverwriteSelectItalic();
			overwriteSelectUnderline_ = configObj.isOverwriteSelectUnderline();
			overwriteSelectFont_ = configObj.isOverwriteSelectFont();
			overwriteSelectSize_ = configObj.isOverwriteSelectSize();
			overwriteSelectTextColor_ = configObj.isOverwriteSelectTextColor();
			overwriteSelectBackgroundColor_ 
				= configObj.isOverwriteSelectBackgroundColor();
			selectMarkupTag_ = new Tag(configObj.getSelectMarkupTag());
		}
	}
	/**
    * Set configuration variables by specifying a Configuration object.
	*
	* @param conf  the Configuration object
    */
	public void setVarsFromConfig(Configuration conf)
	{
		vttDir_ = conf.getConfiguration(Configuration.VTT_DIR);
		String vttFileDir = conf.getConfiguration(Configuration.VTT_FILE_DIR);
		String tagsFileStr = conf.getConfiguration(Configuration.TAGS_FILE);
		userName_ = conf.getConfiguration(Configuration.USER_NAME);
		vttFormat_ = VttFormat.getVttFormat(
			conf.getConfiguration(Configuration.VTT_FORMAT));
		filePathDisplayLength_ = Integer.parseInt(
			conf.getConfiguration(Configuration.FILEPATH_DISPLAY_LENGTH));
		baseFontSize_ = Integer.parseInt(
			conf.getConfiguration(Configuration.BASE_FONT_SIZE));
		zoomFactor_ = Integer.parseInt(
			conf.getConfiguration(Configuration.ZOOM_FACTOR));
		
		offsetSize_ = Integer.parseInt(
			conf.getConfiguration(Configuration.MARKUP_OFFSET_SIZE));
		lengthSize_ = Integer.parseInt(
			conf.getConfiguration(Configuration.MARKUP_LENGTH_SIZE));
		tagNameSize_ = Integer.parseInt(
			conf.getConfiguration(Configuration.MARKUP_TAG_NAME_SIZE));
		tagCategorySize_ = Integer.parseInt(
			conf.getConfiguration(Configuration.MARKUP_TAG_CATEGORY_SIZE));
		docDir_ = new File(vttFileDir);
		if(docDir_.isAbsolute() == false)
		{
			docDir_ = new File(vttDir_ + "/" + vttFileDir);
		}
		tagFile_ = new File(tagsFileStr);
		if(tagFile_.isAbsolute() == false)
		{
			tagFile_ = new File(vttDir_ + "/" + tagsFileStr);
		}
		// select markup style attribute
		overwriteSelectBold_ = Boolean.parseBoolean(
            conf.getConfiguration(Configuration.OVERWRITE_SELECT_BOLD));
        overwriteSelectItalic_ = Boolean.parseBoolean(
            conf.getConfiguration(Configuration.OVERWRITE_SELECT_ITALIC));
        overwriteSelectUnderline_ = Boolean.parseBoolean(
            conf.getConfiguration(Configuration.OVERWRITE_SELECT_UNDERLINE));
        overwriteSelectFont_ = Boolean.parseBoolean(
            conf.getConfiguration(Configuration.OVERWRITE_SELECT_FONT));
        overwriteSelectSize_ = Boolean.parseBoolean(
            conf.getConfiguration(Configuration.OVERWRITE_SELECT_SIZE));
        overwriteSelectTextColor_ = Boolean.parseBoolean(
            conf.getConfiguration(Configuration.OVERWRITE_SELECT_TEXT_COLOR));
        overwriteSelectBackgroundColor_ = Boolean.parseBoolean(
            conf.getConfiguration(Configuration.OVERWRITE_SELECT_BACKGROUND_COLOR));
		useTextTagForNotDisplaySelectMarkup_ = Boolean.parseBoolean(
            conf.getConfiguration(
				Configuration.USE_TEXT_TAG_FOR_NOT_DISPLAY_SELECT_MARKUP));
		// select attribute
        boolean selectBold = Boolean.parseBoolean(
            conf.getConfiguration(Configuration.SELECT_BOLD));
        boolean selectItalic = Boolean.parseBoolean(
            conf.getConfiguration(Configuration.SELECT_ITALIC));
        boolean selectUnderline = Boolean.parseBoolean(
            conf.getConfiguration(Configuration.SELECT_UNDERLINE));
        String selectFont = conf.getConfiguration(Configuration.SELECT_FONT);
        String selectSize = conf.getConfiguration(Configuration.SELECT_SIZE);
		// text color
        int selectTextColorR = Integer.parseInt(
            conf.getConfiguration(Configuration.SELECT_TEXT_COLOR_R));
        int selectTextColorG = Integer.parseInt(
            conf.getConfiguration(Configuration.SELECT_TEXT_COLOR_G));
        int selectTextColorB = Integer.parseInt(
            conf.getConfiguration(Configuration.SELECT_TEXT_COLOR_B));
        Color textColor = new Color(selectTextColorR,
            selectTextColorG, selectTextColorB);
		// background color
        int selectBackgroundColorR = Integer.parseInt(
            conf.getConfiguration(Configuration.SELECT_BACKGROUND_COLOR_R));
        int selectBackgroundColorG = Integer.parseInt(
            conf.getConfiguration(Configuration.SELECT_BACKGROUND_COLOR_G));
        int selectBackgroundColorB = Integer.parseInt(
            conf.getConfiguration(Configuration.SELECT_BACKGROUND_COLOR_B));
        Color backgroundColor = new Color(selectBackgroundColorR,
            selectBackgroundColorG, selectBackgroundColorB);
		// update select markup tag
		String category = new String();
        selectMarkupTag_ = new Tag("Select Markup", category, true, selectBold,
            selectItalic, selectUnderline, textColor, backgroundColor,
            selectFont, selectSize);
	}
	/**
    * Set configuration variables: VTT top directory.
	*
	* @param vttDir  the absolute path of VTT top directory
    */
	public void setVttDir(String vttDir)
	{
		vttDir_ = vttDir;
	}
	/**
    * Set configuration variables: VTT documents directory.
	*
	* @param docDir  the absolute path of VTT documents directory
    */
	public void setDocDir(File docDir)
	{
		docDir_ = docDir;
	}
	/**
    * Set configuration variables: VTT Tag file.
	*
	* @param tagFile  the absolute path of VTT Tag file
    */
	public void setTagFile(File tagFile)
	{
		tagFile_ = tagFile;
	}
	/**
    * Set configuration variables: VTT user name.
	*
	* @param userName  the VTT user name
    */
	public void setUserName(String userName)
	{
		userName_ = userName;
	}
	/**
    * Set configuration variables: VTT format. There are four VTT format:
	* <ul>
	* <li>SIMPLE_FORMAT: not align
	* <li>READABLE_FORMAT: align with auto length
	* <li>FIX_LENGTH_FORMAT: align with fixed length
	* <li>SIMPLEST_FORMAT: no header, no annotation
	* </ul>
	*
	* @param vttFormat  the VTT format
    */
	public void setVttFormat(int vttFormat)
	{
		vttFormat_ = vttFormat;
	}
	/**
    * Set configuration variables: the display length of file path.
	*
	* @param filePathDisplayLength  the display length of file path
    */
	public void setFilePathDisplayLength(int filePathDisplayLength)
	{
		filePathDisplayLength_ = filePathDisplayLength;
	}
	/**
    * Set configuration variables: the base font size.
	*
	* @param baseFontSize  the base font size
    */
	public void setBaseFontSize(int baseFontSize)
	{
		baseFontSize_ = baseFontSize;
	}
	/**
    * Set configuration variables: the zoom in/out factor.
	*
	* @param zoomFactor  the zoom factor
    */
	public void setZoomFactor(int zoomFactor)
	{
		zoomFactor_ = zoomFactor;
	}
	/**
    * Set configuration variables: the size of field for offset.
	*
	* @param offsetSize  the size of field for offset
    */
	public void setOffsetSize(int offsetSize)
	{
		offsetSize_ = offsetSize;
	}
	/**
    * Set configuration variables: the size of field for length.
	*
	* @param lengthSize  the size of field for length
    */
	public void setLengthSize(int lengthSize)
	{
		lengthSize_ = lengthSize;
	}
	/**
    * Set configuration variables: the size of field for tag name.
	*
	* @param tagNameSize  the size of field for tag name
    */
	public void setTagNameSize(int tagNameSize)
	{
		tagNameSize_ = tagNameSize;
	}
	/**
    * Set configuration variables: the size of field for tag category.
	*
	* @param tagCategorySize  the size of field for tag category
    */
	public void setTagCategorySize(int tagCategorySize)
	{
		tagCategorySize_ = tagCategorySize;
	}
	/**
    * Set configuration variables: the boolean flag of use text tag for not 
	* display select markup.
	*
	* @param useTextTagForNotDisplaySelectMarkup  the boolean flag of use text 
	* 		tag for not display select markup
    */
	public void setUseTextTagForNotDisplaySelectMarkup(
		boolean useTextTagForNotDisplaySelectMarkup)
	{
		useTextTagForNotDisplaySelectMarkup_ 
			= useTextTagForNotDisplaySelectMarkup;
	}
	/**
    * Set configuration variables: the boolean flag of overwriting bold on 
	* select markup.
	*
	* @param overwriteSelectBold  the boolean flag of overwriting bold on
	* 		select markup
    */
	public void setOverwriteSelectBold(boolean overwriteSelectBold)
	{
		overwriteSelectBold_ = overwriteSelectBold;
	}
	/**
    * Set configuration variables: the boolean flag of overwriting italic on 
	* select markup.
	*
	* @param overwriteSelectItalic  the boolean flag of overwriting italic on
	* 		select markup
    */
	public void setOverwriteSelectItalic(boolean overwriteSelectItalic)
	{
		overwriteSelectItalic_ = overwriteSelectItalic;
	}
	/**
    * Set configuration variables: the boolean flag of overwriting underline on 
	* select markup.
	*
	* @param overwriteSelectUnderline  the boolean flag of overwriting underline
	* 		on select markup
    */
	public void setOverwriteSelectUnderline(boolean overwriteSelectUnderline)
	{
		overwriteSelectUnderline_ = overwriteSelectUnderline;
	}
	/**
    * Set configuration variables: the boolean flag of overwriting font on 
	* select markup.
	*
	* @param overwriteSelectFont  the boolean flag of overwriting font on
	* 		select markup
    */
	public void setOverwriteSelectFont(boolean overwriteSelectFont)
	{
		overwriteSelectFont_ = overwriteSelectFont;
	}
	/**
    * Set configuration variables: the boolean flag of overwriting size on 
	* select markup.
	*
	* @param overwriteSelectSize  the boolean flag of overwriting size on
	* 		select markup
    */
	public void setOverwriteSelectSize(boolean overwriteSelectSize)
	{
		overwriteSelectSize_ = overwriteSelectSize;
	}
	/**
    * Set configuration variables: the boolean flag of overwriting text color 
	* on select markup.
	*
	* @param overwriteSelectTextColor  the boolean flag of overwriting text 
	* 		color on select markup
    */
	public void setOverwriteSelectTextColor(boolean overwriteSelectTextColor)
	{
		overwriteSelectTextColor_ = overwriteSelectTextColor;
	}
	/**
    * Set configuration variables: the boolean flag of overwriting background
	* color on select markup.
	*
	* @param overwriteSelectBackgroundColor  the boolean flag of overwriting 
	* 		background color on select markup
    */
	public void setOverwriteSelectBackgroundColor(
		boolean overwriteSelectBackgroundColor)
	{
		overwriteSelectBackgroundColor_ = overwriteSelectBackgroundColor;
	}
	/**
    * Set configuration variables: the tag for the selected markup 
	*
	* @param selectMarkupTag  the tag for the selected markup
    */
	public void setSelectMarkupTag(Tag selectMarkupTag)
	{
		selectMarkupTag_ = selectMarkupTag;
	}
	/**
    * Get configuration variables: VTT top directory.
	*
	* @return the absolute path of VTT top directory
    */
	public String getVttDir()
	{
		return vttDir_;
	}
	
	/**
    * Get configuration variables: VTT documents directory.
	*
	* @return the File object of VTT documents directory
    */
	public File getDocDir()
	{
		return docDir_;
	}
	
	/**
    * Get configuration variables: VTT documents directory.
	*
	* @return the Fiel object of VTT Tag file
    */
	public File getTagFile()
	{
		return tagFile_;
	}
	
	/**
    * Get configuration variables: VTT user name.
	*
	* @return the absolute path of VTT user name
    */
	public String getUserName()
	{
		return userName_;
	}
	/**
    * Get configuration variables: VTT format.
	*
	* @return the VTT format
    */
	public int getVttFormat()
	{
		return vttFormat_;
	}
	/**
    * Get configuration variables: the display length of file path.
	*
	* @return the display length of file path
    */
	public int getFilePathDisplayLength()
	{
		return filePathDisplayLength_;
	}
	/**
    * Get configuration variables: the base font size.
	*
	* @return the base font size
    */
	public int getBaseFontSize()
	{
		return baseFontSize_;
	}
	/**
    * Get configuration variables: the zoom in/out factor.
	*
	* @return the zoom in/out factor
    */
	public int getZoomFactor()
	{
		return zoomFactor_;
	}
	/**
    * Get configuration variables: the size of field for offset.
	*
	* @return the size of field for offset
    */
	public int getOffsetSize()
	{
		return offsetSize_;
	}
	/**
    * Get configuration variables: the size of field for length.
	*
	* @return the size of field for length
    */
	public int getLengthSize()
	{
		return lengthSize_;
	}
	/**
    * Get configuration variables: the size of field for tag name.
	*
	* @return the size of field for tag name
    */
	public int getTagNameSize()
	{
		return tagNameSize_;
	}
	/**
    * Get configuration variables: the size of field for tag category.
	*
	* @return the size of field for tag category
    */
	public int getTagCategorySize()
	{
		return tagCategorySize_;
	}
	/**
    * Get configuration variables: the boolean flag of use text tag for not
	* display select markup.
	*
	* @return the boolean flag of use text tag for not display select markup
    */
	public boolean isUseTextTagForNotDisplaySelectMarkup()
	{
		return useTextTagForNotDisplaySelectMarkup_;
	}
	
	/**
    * Get configuration variables: the boolean flag of overwriting bold on
	* select markup.
	*
	* @return the boolean flag of overwriting bold on select markup
    */
	public boolean isOverwriteSelectBold()
	{
		return overwriteSelectBold_;
	}
	
	/**
    * Get configuration variables: the boolean flag of overwriting italic on
	* select markup.
	*
	* @return the boolean flag of overwriting italic on select markup
    */
	public boolean isOverwriteSelectItalic()
	{
		return overwriteSelectItalic_;
	}
	
	/**
    * Get configuration variables: the boolean flag of overwriting underline on
	* select markup.
	*
	* @return the boolean flag of overwriting underline on select markup
    */
	public boolean isOverwriteSelectUnderline()
	{
		return overwriteSelectUnderline_;
	}
	
	/**
    * Get configuration variables: the boolean flag of overwriting font on
	* select markup.
	*
	* @return the boolean flag of overwriting font on select markup
    */
	public boolean isOverwriteSelectFont()
	{
		return overwriteSelectFont_;
	}
	
	/**
    * Get configuration variables: the boolean flag of overwriting size on
	* select markup.
	*
	* @return the boolean flag of overwriting size on select markup
    */
	public boolean isOverwriteSelectSize()
	{
		return overwriteSelectSize_;
	}
	
	/**
    * Get configuration variables: the boolean flag of overwriting text color on
	* select markup.
	*
	* @return the boolean flag of overwriting italic on text color markup
    */
	public boolean isOverwriteSelectTextColor()
	{
		return overwriteSelectTextColor_;
	}
	
	/**
    * Get configuration variables: the boolean flag of overwriting background
	* color on select markup.
	*
	* @return the boolean flag of overwriting background color on select markup
    */
	public boolean isOverwriteSelectBackgroundColor()
	{
		return overwriteSelectBackgroundColor_;
	}
	
	/**
    * Get configuration variables: the tag for the selected markup.
	*
	* @return the tag for the selected markup
    */
	public Tag getSelectMarkupTag()
	{
		return selectMarkupTag_;
	}
	/**
	* Save configObj to file.
	*
	* @param configFile  the config file to save to
	* @param configObj  the configObj object to be saved
    */
	public static void saveFile(File configFile, ConfigObj configObj)
	{
		String outFile = configFile.getAbsolutePath();
		try
		{
			BufferedWriter out =  new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outFile), "UTF-8"));
			String outStr = configObj.toString();
			out.write(outStr);
			out.close();
		}
		catch(Exception e)
		{
			System.err.println(
				"** Err@SaveConfigFile(): problem of opening/writing file: "
				+ outFile);
			System.err.println("Exception: " + e.toString());
			final String fMsg = e.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
	/**
	* Convert the configObj to String
	*
	* @return the string format of this configObj
    */
	public String toString()
	{
		StringBuffer out = new StringBuffer();
		// directories and files
		out.append(FILE_BANNER);
		out.append(Configuration.VTT_DIR + "=" + vttDir_ + GlobalVars.LS_STR);
		out.append(Configuration.VTT_FILE_DIR + "=" + docDir_ 
			+ GlobalVars.LS_STR);
		out.append(Configuration.TAGS_FILE + "=" + tagFile_ 
			+ GlobalVars.LS_STR);
		// other options
		out.append(Configuration.USER_NAME + "=" + userName_ 
			+ GlobalVars.LS_STR);
		out.append(SHORT_SEPERATOR + GlobalVars.LS_STR);
		out.append(Configuration.FILEPATH_DISPLAY_LENGTH + "=" 
			+ filePathDisplayLength_ + GlobalVars.LS_STR);
		out.append(Configuration.BASE_FONT_SIZE + "=" 
			+ baseFontSize_ + GlobalVars.LS_STR);
		out.append(Configuration.ZOOM_FACTOR + "=" 
			+ zoomFactor_ + GlobalVars.LS_STR);
		out.append(Configuration.VTT_FORMAT + "=" 
			+ VttFormat.getVttFormatStr(vttFormat_) + GlobalVars.LS_STR);
		// fixed length	
		out.append(Configuration.MARKUP_OFFSET_SIZE + "=" 
			+ offsetSize_ + GlobalVars.LS_STR);
		out.append(Configuration.MARKUP_LENGTH_SIZE + "=" 
			+ lengthSize_ + GlobalVars.LS_STR);
		out.append(Configuration.MARKUP_TAG_NAME_SIZE + "=" 
			+ tagNameSize_ + GlobalVars.LS_STR);
		out.append(Configuration.MARKUP_TAG_CATEGORY_SIZE + "=" 
			+ tagCategorySize_ + GlobalVars.LS_STR);
		// select markup properties
		out.append(SELECT_MARKUP_BANNER);
		out.append(Configuration.OVERWRITE_SELECT_BOLD + "=" 
			+ overwriteSelectBold_ + GlobalVars.LS_STR);
		out.append(Configuration.OVERWRITE_SELECT_ITALIC + "=" 
			+ overwriteSelectItalic_ + GlobalVars.LS_STR);
		out.append(Configuration.OVERWRITE_SELECT_UNDERLINE + "=" 
			+ overwriteSelectUnderline_ + GlobalVars.LS_STR);
		out.append(Configuration.OVERWRITE_SELECT_FONT + "=" 
			+ overwriteSelectFont_ + GlobalVars.LS_STR);
		out.append(Configuration.OVERWRITE_SELECT_SIZE + "=" 
			+ overwriteSelectSize_ + GlobalVars.LS_STR);
		out.append(Configuration.OVERWRITE_SELECT_TEXT_COLOR + "=" 
			+ overwriteSelectTextColor_ + GlobalVars.LS_STR);
		out.append(Configuration.OVERWRITE_SELECT_BACKGROUND_COLOR + "=" 
			+ overwriteSelectBackgroundColor_ + GlobalVars.LS_STR);
		out.append(Configuration.USE_TEXT_TAG_FOR_NOT_DISPLAY_SELECT_MARKUP 
			+ "=" + useTextTagForNotDisplaySelectMarkup_ + GlobalVars.LS_STR);
		out.append(SHORT_SEPERATOR + GlobalVars.LS_STR);
		out.append(Configuration.SELECT_BOLD + "=" 
			+ selectMarkupTag_.isBold() + GlobalVars.LS_STR);
		out.append(Configuration.SELECT_ITALIC + "=" 
			+ selectMarkupTag_.isItalic() + GlobalVars.LS_STR);
		out.append(Configuration.SELECT_UNDERLINE + "=" 
			+ selectMarkupTag_.isUnderline() + GlobalVars.LS_STR);
		out.append(Configuration.SELECT_FONT + "=" 
			+ selectMarkupTag_.getFontFamily() + GlobalVars.LS_STR);
		out.append(Configuration.SELECT_SIZE + "=" 
			+ selectMarkupTag_.getFontSize() + GlobalVars.LS_STR);
		out.append(Configuration.SELECT_TEXT_COLOR_R + "=" 
			+ selectMarkupTag_.getForeground().getRed() + GlobalVars.LS_STR);
		out.append(Configuration.SELECT_TEXT_COLOR_G + "=" 
			+ selectMarkupTag_.getForeground().getGreen() + GlobalVars.LS_STR);
		out.append(Configuration.SELECT_TEXT_COLOR_B + "=" 
			+ selectMarkupTag_.getForeground().getBlue() + GlobalVars.LS_STR);
		out.append(Configuration.SELECT_BACKGROUND_COLOR_R + "=" 
			+ selectMarkupTag_.getBackground().getRed() + GlobalVars.LS_STR);
		out.append(Configuration.SELECT_BACKGROUND_COLOR_G + "=" 
			+ selectMarkupTag_.getBackground().getGreen() + GlobalVars.LS_STR);
		out.append(Configuration.SELECT_BACKGROUND_COLOR_B + "=" 
			+ selectMarkupTag_.getBackground().getBlue() + GlobalVars.LS_STR);
		return out.toString();
	}
	// data member from config file
	private String vttDir_ = null; // top dir of vtt
    private File docDir_ = null;
    private File tagFile_ = null; // default tag file
	private String userName_ = new String(); // vtt user name
    private int vttFormat_ = VttFormat.SIMPLE_FORMAT;
    private int filePathDisplayLength_ = 0;
    private int baseFontSize_ = 12;	// use as base font size for text and tags
    private int zoomFactor_ = 0;	// global relative factor for zoom in/out
			
    private int offsetSize_ = 4;	 // the size for offset
    private int lengthSize_ = 2;	 // the size for length
    private int tagNameSize_ = 12;
    private int tagCategorySize_ = 12;
	// select markup style attribute
	private boolean overwriteSelectBold_ = false;
    private boolean overwriteSelectItalic_ = false;
    private boolean overwriteSelectUnderline_ = false;
    private boolean overwriteSelectFont_ = false;
    private boolean overwriteSelectSize_ = false;
    private boolean overwriteSelectTextColor_ = false;
    private boolean overwriteSelectBackgroundColor_ = false;
	// if true: use text tag as the base for not display selected markup
	// if false: use the same tag as the base for not display selected markup
	private boolean useTextTagForNotDisplaySelectMarkup_ = false;	
	private Tag selectMarkupTag_ = null;
	// final variable, banner
	private final static String FULL_SEPERATOR = 
		"#-------------------------------------------------------------------";
	private final static String SHORT_SEPERATOR = "#-----------";
	private final static String COMMENT = "#";
	private final static String FILE_BANNER = 
		FULL_SEPERATOR + GlobalVars.LS_STR
		+ "# VTT System Variables:"
		+ GlobalVars.LS_STR
		+ COMMENT
		+ GlobalVars.LS_STR
		+ "# VTT_DIR: the absolute path of the Visual Tagging Tool directory"
		+ GlobalVars.LS_STR
		+ "# VTT_FILE_DIR: the path of Vtt file directory"
		+ GlobalVars.LS_STR
		+ "# TAGS_FILE: the path of default tags file"
		+ GlobalVars.LS_STR
		+ COMMENT
		+ GlobalVars.LS_STR
		+ "# FILE_DISPLAY_LENGTH: the display length of opened file path shown in the title"
		+ GlobalVars.LS_STR
		+ "# BASE_FONT_SIZE: the base font size for text and tag"
		+ GlobalVars.LS_STR
		+ "# ZOOM_FACTOR: the factor of zoom in/out"
		+ GlobalVars.LS_STR
		+ "# VTT_FORMAT: SIMPLE_FORMAT, READABLE_FORMAT, FIX_LENGTH_FORMAT"
		+ GlobalVars.LS_STR
		+ COMMENT
		+ GlobalVars.LS_STR
		+ "# MARKUP_OFFSET_SIZE: size of markup offset field in FIX_LENGTH format"
		+ GlobalVars.LS_STR
		+ "# MARKUP_LENGTH_SIZE: size of markup length field in FIX_LENGTH format"
		+ GlobalVars.LS_STR
		+ "# MARKUP_TAG_NAME_SIZE: size of markup tagName field in FIX_LENGTH format"
		+ GlobalVars.LS_STR
		+ "# MARKUP_TAG_CATEGORY_SIZE: size of markup tagCategory field in FIX_LENGTH format"
		+ GlobalVars.LS_STR + FULL_SEPERATOR + GlobalVars.LS_STR;
	private final static String SELECT_MARKUP_BANNER = 
		FULL_SEPERATOR + GlobalVars.LS_STR
		+ "# Select Markup properties:"
		+ GlobalVars.LS_STR
		+ COMMENT
		+ GlobalVars.LS_STR
		+ "# OVERWRITE_SELECT_BOLD: overwrite the bold attribute on select markup"
		+ GlobalVars.LS_STR
		+ "# OVERWRITE_SELECT_ITALIC: overwrite the italic attribute on select markup"
		+ GlobalVars.LS_STR
		+ "# OVERWRITE_SELECT_UNDERLINE: overwrite the underline attribute on select markup"
		+ GlobalVars.LS_STR
		+ "# OVERWRITE_SELECT_FONT: overwrite the font attribute on select markup"
		+ GlobalVars.LS_STR
		+ "# OVERWRITE_SELECT_SIZE: overwrite the size attribute on select markup"
		+ GlobalVars.LS_STR
		+ "# OVERWRITE_SELECT_TEXT_COLOR: overwrite the text color on select markup"
		+ GlobalVars.LS_STR
		+ "# OVERWRITE_SELECT_BACKGROUND_COLOR: overwrite the bg color on select markup"
		+ GlobalVars.LS_STR
		+ "# USE_TEXT_TAG_FOR_NOT_DISPLAY_SELECT_MARKUP: use textTag for not display select"
		+ GlobalVars.LS_STR
		+ COMMENT
		+ GlobalVars.LS_STR
		+ "# SELECT_BOLD: select markup bold attribute"
		+ GlobalVars.LS_STR
		+ "# SELECT_ITALIC: select markup italic attribute"
		+ GlobalVars.LS_STR
		+ "# SELECT_UNDERLINE: select markup underline attribute"
		+ GlobalVars.LS_STR
		+ "# SELECT_FONT: select markup font attribute"
		+ GlobalVars.LS_STR
		+ "# SELECT_SIZE: select markup size attribute (+2|14|-2)"
		+ GlobalVars.LS_STR
		+ "# SELECT_TEXT_COLOR_R: select markup text color-red (0 ~ 255)"
		+ GlobalVars.LS_STR
		+ "# SELECT_TEXT_COLOR_G: select markup text color-green (0 ~ 255)"
		+ GlobalVars.LS_STR
		+ "# SELECT_TEXT_COLOR_B: select markup text color-blue (0 ~ 255)"
		+ GlobalVars.LS_STR
		+ "# SELECT_BACKGROUND_COLOR_R: select markup text color-red (0 ~ 255)"
		+ GlobalVars.LS_STR
		+ "# SELECT_BACKGROUND_COLOR_G: select markup text color-green (0 ~ 255)"
		+ GlobalVars.LS_STR
		+ "# SELECT_BACKGROUND_COLOR_B: select markup text color-blue (0 ~ 255)"
		+ GlobalVars.LS_STR + FULL_SEPERATOR + GlobalVars.LS_STR;
}
