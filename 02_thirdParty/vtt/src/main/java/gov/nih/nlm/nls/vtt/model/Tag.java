package gov.nih.nlm.nls.vtt.model;
import java.awt.*;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
/*****************************************************************************
* This is the Tag Java object class. A tag is used to markup a selected text.
* A tag includes color, font, size, style, and other properties. 
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class Tag 
{
	// public constructor
	/**
	* Create a Tag Java object with default values.
	*/
    public Tag()
    {
    }
	/**
	* Create a Tag Java object by specifying a tag.
	*
	* @param tag a tag to be instantiated
	*/
    public Tag(Tag tag)
    {
		setTag(tag);
    }
	/**
	* Create a Tag Java object by specifying name.
	*
	* @param name the name of tag
	*/
    public Tag(String name)
    {
		name_ = name;
	}
	/**
	* Create a Tag Java object by specifying name, category, bold, italic, 
	* underline, foreground color, and background color.
	*
	* @param name the name of tag
	* @param category the category of tag
	* @param bold bold in font style
	* @param italic italic in font style
	* @param underline underline in font style
	* @param foreground foreground color in font style
	* @param background background color in font style
	*/
    public Tag(String name, String category, boolean bold, boolean italic, 
		boolean underline, Color foreground, Color background)
    {
		name_ = name;
		category_ = category;
		bold_ = bold;
		italic_ = italic;
		underline_ = underline;
		foreground_ = foreground;
		background_ = background;
	}
	/**
	* Create a Tag Java object by specifying name, category, display, bold, 
	* italic, underline, foreground color, background color,
	* font family name, and font size.
	*
	* @param name the name of tag
	* @param category the category of tag
	* @param display the boolean flag of display the tag
	* @param bold bold in font style
	* @param italic italic in font style
	* @param underline underline in font style
	* @param foreground foreground color in font style
	* @param background background color in font style
	* @param fontFamily name of font family
	* @param fontSize size of font
	*/
    public Tag(String name, String category, boolean display, boolean bold, 
		boolean italic, boolean underline, Color foreground, Color background, 
		String fontFamily, String fontSize)
    {
		name_ = name;
		category_ = category;
		display_ = display;
		bold_ = bold;
		italic_ = italic;
		underline_ = underline;
		foreground_ = foreground;
		background_ = background;
		fontFamily_ = fontFamily;
		fontSize_ = fontSize;
	}
	// public methods
	/**
	* Set the name
	*
	* @param name of the tag
	*/
	public void setName(String name)
	{
		name_ = name;
	}
	/**
	* Set the category
	*
	* @param category the category of tag
	*/
	public void setCategory(String category)
	{
		category_ = category;
	}
	/**
	* Set the display
	*
	* @param display the boolean flag of display the tag
	*/
	public void setDisplay(boolean display)
	{
		display_ = display;
	}
	/**
	* Set the bold in the font style
	*
	* @param bold bold in font style
	*/
	public void setBold(boolean bold)
	{
		bold_ = bold;
	}
	/**
	* Set the italic in the font style
	*
	* @param italic italic in the font style
	*/
	public void setItalic(boolean italic)
	{
		italic_ = italic;
	}
	/**
	* Set the underline in the font style
	*
	* @param underline underline in the font style
	*/
	public void setUnderline(boolean underline)
	{
		underline_ = underline;
	}
	/**
	* Set the foreground color in font style
	*
	* @param foreground foreground color in font style
	*/
	public void setForeground(Color foreground)
	{
		foreground_ = foreground;
	}
	/**
	* Set the background color in the font style
	*
	* @param background background color in the font style
	*/
	public void setBackground(Color background)
	{
		background_ = background;
	}
	/**
	* Set the name of font family
	*
	* @param fontFamily name of font family
	*/
	public void setFontFamily(String fontFamily)
	{
		fontFamily_ = fontFamily;
	}
	/**
	* Set the size of font
	*
	* @param fontSize size of font
	*/
	public void setFontSize(String fontSize)
	{
		fontSize_ = fontSize;
	}
	/**
	* Get the name
	*
	* @return name of the tag
	*/
	public String getName()
	{
		return name_;
	}
	/**
	* Get the category
	*
	* @return category of the tag
	*/
	public String getCategory()
	{
		return category_;
	}
	/**
	* Get the name category
	*
	* @return name and category of the tag
	*/
	public String getNameCategory()
	{
		return getNameCategory(name_, category_);
	}
	/**
	* Convert the name and category to nameCategory string
	*
	* @return name and category string of the tag
	*/
	public static String getNameCategory(String name, String category)
	{
		String nameCategoryStr = name;
		if(category.length() > 0)
		{
			nameCategoryStr += GlobalVars.FS_STR + category;
		}
		return nameCategoryStr;
	}
	/**
	* Get the name by specifying nameCategory
	*
	* @return name of the nameCategory
	*/
	public static String getName(String nameCategory)
    {
        StringTokenizer buf = new StringTokenizer(nameCategory, 
			GlobalVars.FS_STR);
        String name = buf.nextToken();
        return name;
    }
	/**
	* Get the category by specifying nameCategory
	*
	* @return category of the nameCategory
	*/
    public static String getCategory(String nameCategory)
    {
        StringTokenizer buf = new StringTokenizer(nameCategory, 
			GlobalVars.FS_STR);
        buf.nextToken();
        String category = new String();
        if(buf.hasMoreTokens() == true)
        {
            category = buf.nextToken();
        }
        return category;
    }
	/**
	* Get the boolean flag of display
	*
	* @return the boolean flag of display
	*/
	public boolean isDisplay()
	{
		return display_;
	}
	/**
	* Get the boolean flag of bold
	*
	* @return the boolean flag of bold
	*/
	public boolean isBold()
	{
		return bold_;
	}
	/**
	* Get the boolean flag of italic
	*
	* @return the boolean flag of italic
	*/
	public boolean isItalic()
	{
		return italic_;
	}
	/**
	* Get the boolean flag of underline
	*
	* @return the boolean flag of underline
	*/
	public boolean isUnderline()
	{
		return underline_;
	}
	/**
	* Get the foreground color of text
	*
	* @return the foreground color of text
	*/
	public Color getForeground()
	{
		return foreground_;
	}
	/**
	* Get the background color of text
	*
	* @return the background color of text
	*/
	public Color getBackground()
	{
		return background_;
	}
	/**
	* Get the name of font family
	*
	* @return the name of font family
	*/
	public String getFontFamily()
	{
		return fontFamily_;
	}
	/**
	* Get the size of font
	*
	* @return the size of font
	*/
	public String getFontSize()
	{
		return fontSize_;
	}
	/**
	* Get the absolute size of font by specifying the base size
	*
	* @return the absolute size of font
	*/
	public int getFontSize(int baseFontSize)
	{
		// set the default to base font size if anything is not illegal
		int fontSize = baseFontSize;
		try
		{
			if(fontSize_.startsWith("+"))
			{
				fontSize = baseFontSize
					+ Integer.parseInt(fontSize_.substring(1));
			}
			else if(fontSize_.startsWith("-"))
			{
				fontSize = baseFontSize + Integer.parseInt(fontSize_);
			}
			else
			{
				fontSize = Integer.parseInt(fontSize_);
			}
		}
		catch (Exception e)
		{
			System.err.println("** Err@Tag.GetFontSize( ): illegal font size: " 
				+ e.toString());
			final String fMsg = e.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Vtt Input File Error (Markup)", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		return fontSize;
	}
	/**
	* Set tag to a specified tag
	*
	* @param tag  the specified tag
	*/
	public void setTag(Tag tag)
	{
		name_ = tag.getName();
		category_ = tag.getCategory();
		bold_ = tag.isBold();
		italic_ = tag.isItalic();
		underline_ = tag.isUnderline();
		display_ = tag.isDisplay();
		foreground_ = tag.getForeground();
		background_ = tag.getBackground();
		fontFamily_ = tag.getFontFamily();
		fontSize_ = tag.getFontSize();
	}
	/**
	* Set bold, italic, underline, display, font, size to default
	*/
	public void setToTextDefault()
	{
		bold_ = false;
		italic_ = false;
		underline_ = false;
		display_ = true;
		fontFamily_ = DEFAULT_FONT_FAMILY;
		fontSize_ = DEFAULT_FONT_SIZE;
	}
	/**
	* Convert tag to a string representation:
	* name|category|bold|italic|underline|display|fr|fg|fb|br|bg|bb|font|size
	*
	* @return string representation of a tag
	*/
	public String toString()
	{
		String outStr = name_ + GlobalVars.FS_STR 
			+ category_ + GlobalVars.FS_STR  
			+ String.valueOf(bold_) + GlobalVars.FS_STR  
			+ String.valueOf(italic_) + GlobalVars.FS_STR  
			+ String.valueOf(underline_) + GlobalVars.FS_STR  
			+ String.valueOf(display_) + GlobalVars.FS_STR  
			+ String.valueOf(foreground_.getRed()) + GlobalVars.FS_STR  
			+ String.valueOf(foreground_.getGreen()) + GlobalVars.FS_STR  
			+ String.valueOf(foreground_.getBlue()) + GlobalVars.FS_STR  
			+ String.valueOf(background_.getRed()) + GlobalVars.FS_STR  
			+ String.valueOf(background_.getGreen()) + GlobalVars.FS_STR  
			+ String.valueOf(background_.getBlue()) + GlobalVars.FS_STR  
			+ fontFamily_ + GlobalVars.FS_STR  
			+ fontSize_; 
		
		return outStr;
	}
	/**
	* Convert a specified tag to a string representation:
	* name|category|bold|italic|underline|display|fr|fg|fb|br|bg|bb|font|size
	*
	* @return string representation of a specified tag
	*/
	public static String toString(Tag tag)
	{
		StringBuffer out = new StringBuffer();
		out.append(tag.getName());
		out.append(GlobalVars.FS_STR);
		out.append(tag.getCategory());
		out.append(GlobalVars.FS_STR);
		out.append(tag.isBold());
		out.append(GlobalVars.FS_STR);
		out.append(tag.isItalic());
		out.append(GlobalVars.FS_STR);
		out.append(tag.isUnderline());
		out.append(GlobalVars.FS_STR);
		out.append(tag.isDisplay());
		out.append(GlobalVars.FS_STR);
		out.append(tag.getForeground().getRed());
		out.append(GlobalVars.FS_STR);
		out.append(tag.getForeground().getGreen());
		out.append(GlobalVars.FS_STR);
		out.append(tag.getForeground().getBlue());
		out.append(GlobalVars.FS_STR);
		out.append(tag.getBackground().getRed());
		out.append(GlobalVars.FS_STR);
		out.append(tag.getBackground().getGreen());
		out.append(GlobalVars.FS_STR);
		out.append(tag.getBackground().getBlue());
		out.append(GlobalVars.FS_STR);
		out.append(tag.getFontFamily());
		out.append(GlobalVars.FS_STR);
		out.append(tag.getFontSize());
		out.append(GlobalVars.LS_STR);
		
		return out.toString();
	}
	// data members
	public final static String DEFAULT_FONT_FAMILY = "Monospaced";
	public final static String DEFAULT_FONT_SIZE = "+0";
	private final static String DEFAULT_CATEGORY = new String();
	private String name_ = new String();	// name of the tag
	private String category_ = DEFAULT_CATEGORY; // category of the tag
	private boolean bold_ = false;	// bold
	private boolean italic_ = false;	// italic
	private boolean underline_ = false;	// underline
	private boolean display_ = true;	// display
	private Color foreground_ = Color.BLACK;	// foreground color, word color
	private Color background_ = Color.WHITE;	// background color
	private String fontFamily_ = DEFAULT_FONT_FAMILY;
	private String fontSize_ = DEFAULT_FONT_SIZE;
}
