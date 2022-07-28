package gov.nih.nlm.nls.vtt.model;
import javax.swing.text.*;

import java.io.*;
/*****************************************************************************
* This class is the Markup Java object. Markup is used to label a selected 
* text by different tags with different colors, fonts, sizes, styles, etc.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class Markup 
{
	// constructor
	/**
    * Create a Markup Java object with default values.
    */
    public Markup()
    {
    }
	/**
    * Create a Markup Java object by the specified markup.
	*
	* @param markup  the specified markup
    */
    public Markup(Markup markup)
    {
		offset_ = markup.getOffset();
		length_ = markup.getLength();
		tagName_ = markup.getTagName();
		tagCategory_ = markup.getTagCategory();
		annotation_ = markup.getAnnotation();
    }
	/**
    * Create a Markup Java object by specifying offset, length, tag name,
	* tag category, and annotation.
	*
	* @param offset  the offset position
	* @param length  the length 
	* @param tagName  the name of tag
	* @param tagCategory  the category of tag 
	* @param annotation  the annotation 
    */
	public Markup(int offset, int length, String tagName, String tagCategory,
		String annotation)
	{
		offset_ = offset;
		length_ = length;
		tagName_ = tagName;
		tagCategory_ = tagCategory;
		annotation_ = annotation;
	}
	// getters and setters
	/**
    * Set the offset.
    *
    * @param offset  the beginning position
    */
	public void setOffset(int offset)
	{
		offset_ = offset;
	}
	/**
    * Set the length.
    *
    * @param length  the length
    */
	public void setLength(int length)
	{
		length_ = length;
	}
	/**
    * Set the name of applied tag.
    *
    * @param tagName  the name of applied tag
    */
	public void setTagName(String tagName)
	{
		tagName_ = tagName;
	}
	/**
    * Set the category of applied tag.
    *
    * @param tagCategory  the category of applied tag
    */
	public void setTagCategory(String tagCategory)
	{
		tagCategory_ = tagCategory;
	}
	/**
    * Set the annotation.
    *
    * @param annotation  the annotation
    */
	public void setAnnotation(String annotation)
	{
		annotation_ = annotation;
	}
	/**
    * Get the offset.
    *
    * @return the beginning position (offset)
    */
	public int getOffset()
	{
		return offset_;
	}
	/**
    * Get the length.
    *
    * @return the length
    */
	public int getLength()
	{
		return length_;
	}
	/**
    * Get the name of applied tag.
    *
    * @return the name of applied tag
    */
	public String getTagName()
	{
		return tagName_;
	}
	/**
    * Get the category of applied tag.
    *
    * @return the category of applied tag
    */
	public String getTagCategory()
	{
		return tagCategory_;
	}
	/**
    * Get the name and category string of applied tag.
    *
    * @return the name and category string of applied tag
    */
	public String getTagNameCategory()
	{
		return Tag.getNameCategory(tagName_, tagCategory_);
	}
	/**
    * Get the annotation of applied tag.
    *
    * @return the annotation
    */
	public String getAnnotation()
	{
		return annotation_;
	}
	// public methods
	/**
	* Apply tag, offset, length of markup to text.
    *
    * @param tag the tag of the applied markup
    * @param doc the styled document that markup is applied to
    * @param offset the offset of the applied markup
    * @param length the length of the applied markup
    * @param baseFontSize the base font size of the document
    * @param sas the simple attribute set of the applied markup
    * @param replace a boolean flag of replacing the previous attribute
    */
	public static void doMarkUp(Tag tag, DefaultStyledDocument doc,
		int offset, int length, int baseFontSize, SimpleAttributeSet sas, 
		boolean replace) 
	{
		if(tag == null)
		{
			return;
		}
		
		// show the style of tag
		if(tag.isDisplay() == true)
		{
			StyleConstants.setBold(sas, tag.isBold());
			StyleConstants.setItalic(sas, tag.isItalic());
			StyleConstants.setUnderline(sas, tag.isUnderline());
			StyleConstants.setForeground(sas, tag.getForeground());
			StyleConstants.setBackground(sas, tag.getBackground());
			StyleConstants.setFontFamily(sas, tag.getFontFamily());
			StyleConstants.setFontSize(sas, tag.getFontSize(baseFontSize));
			doc.setCharacterAttributes(offset, length, sas, replace);
		}
	}
	/**
	* Join two markups together.
    *
    * @param markup1  the first markup to join
    * @param markup2  the second markup to join
    *
    * @return the joined markup
    */
	public static Markup join(Markup markup1, Markup markup2)
	{
		int offset = markup1.getOffset();
		int length = markup2.getOffset() + markup2.getLength() - offset;
		String tagName = markup1.getTagName();
		String tagCategory = markup1.getTagCategory();
		String annotation = markup1.getAnnotation() + GlobalVars.SPACE_STR
			+ markup2.getAnnotation();
		Markup newMarkup = new Markup(offset, length, tagName, tagCategory,
			annotation);
		return newMarkup;
	}
	/**
	* Get the markup text of this markup, start at offset, end at offset +
	* length.
	*
	* @param text the untagged text
	*
	* @return the tagged text
    */
	public String getTaggedText(String text)
	{
		String taggedText = null;
		try {
			taggedText = text.substring(offset_, offset_+length_).replace
				(GlobalVars.LS_STR, GlobalVars.SPACE_STR);
		} catch (StringIndexOutOfBoundsException e) {
			String errMsg = 
					"** Error: illegal tag text range.  Text length is " + text.length() + ", requested tag offset is " + offset_ + ", requested tag length is " + length_;
			System.err.println(errMsg);
			throw e;
		}
		return taggedText;
	}
	/**
	* Get markup name. It is defined as offset-length and is unique in Markups.
	*
	* @return the name (offset-length) of the markup
    */
	public String getMarkupName()
	{
		String markupName = offset_ + "-" + length_;
		return markupName;
	}
	/**
	* Convert markup data to string in the format of: 
	* <br>offset|length|tagName|tagCategory|Annotation
	*
	* @return a String represent the makrup
    */
	public String toString()
	{
		return toString(true);
	}
	/**
	* Convert markup data to string with option of showing annotation 
	* in the format of: 
	* <br>offset|length|tagName|tagCategory|Annotation 
	*
	* @return a String represent the makrup
    */
	public String toString(boolean showAnnotation)
	{
		// convert Markup object to format of
		// offset|length|tagName|tagCategory|Annotation
		StringBuffer out = new StringBuffer();
		out.append(offset_);
		out.append(GlobalVars.FS_STR);
		out.append(length_);
		out.append(GlobalVars.FS_STR);
		out.append(tagName_);
		out.append(GlobalVars.FS_STR);
		out.append(tagCategory_);
		out.append(GlobalVars.FS_STR);	// must have for annotation field
		if(showAnnotation == true)
		{
			out.append(annotation_);
		}
		return out.toString();
	}
	
 
  
//=======================================================
 /**
  * toString [Summary here]
  * 
  * @param text
  * @param vttFormat
  * @param configObj
  * @param maxColumns
  * @return
  */
 // =======================================================
 public String toString(String    text, 
                        int       vttFormat, 
                        ConfigObj configObj, 
                        int[]     maxColumns 
                        ) {
  
   
   String returnVal = null;
   switch ( vttFormat ) {
     case VttFormat.READABLE_FORMAT:   returnVal = toString_READABLE_FORMAT(maxColumns, text);  break;
     case VttFormat.FIX_LENGTH_FORMAT: returnVal = toString_FIX_LENGTH_FORMAT(configObj,text);  break;
     case VttFormat.SIMPLE_FORMAT:     returnVal = toString_SIMPLE_FORMAT(text);                break;
     case VttFormat.SIMPLEST_FORMAT:   returnVal = toString_SIMPLEST_FORMAT(text);              break;
       
   }
  
   return returnVal;
   
 } // End Method toString() ======================
  
  /**
  * toString_READABLE_FORMAT Convert markup data to 
  * string
  * in the format of: 
  * <br>offset|length|tagName|tagCategory|Annotation 
  *
  * @param maxColumns 
  * @param text
  * @return a String represent the makrup
    */
  public String toString_READABLE_FORMAT(int[] maxColumns ,   String text)
  
  {
   
    int maxOffsetSize = maxColumns[0];
    int maxLengthSize = maxColumns[1];
    int maxTagNameSize = maxColumns[2];
    int maxTagCategorySize = maxColumns[3];
    StringBuffer out = new StringBuffer();
        out.append(Str.align(Integer.toString(this.getOffset()),   maxOffsetSize, Str.ALIGN_RIGHT));
        out.append(GlobalVars.FS_STR);
        out.append(Str.align(Integer.toString(this.getLength()),   maxLengthSize, Str.ALIGN_RIGHT));
        out.append(GlobalVars.FS_STR);
        out.append(Str.align(this.getTagName(), maxTagNameSize,     Str.ALIGN_LEFT));
        out.append(Str.align(this.getTagCategory(),  maxTagCategorySize, Str.ALIGN_LEFT));
        out.append(GlobalVars.FS_STR);
        out.append(this.getAnnotation());
        out.append(GlobalVars.FS_STR);
        out.append(this.getTaggedText(text));
        out.append(GlobalVars.LS_STR);
        return out.toString();
        
  } // end Method toString_READABLE_FORMAT() ------------------

   /**
    * toString_FIX_LENGTH_FORMAT Convert markup data to 
    * string
    * in the format of: 
    * <br>offset|length|tagName|tagCategory|Annotation 
    *
    * @return a String represent the makrup
      */
      public String toString_FIX_LENGTH_FORMAT(ConfigObj configObj,  String text )
      {
       
        StringBuffer out = new StringBuffer();
       
        out.append(Str.align(Integer.toString(this.getOffset()), 
          configObj.getOffsetSize(), Str.ALIGN_RIGHT));
        out.append(GlobalVars.FS_STR);
        out.append(Str.align(Integer.toString(this.getLength()), 
          configObj.getLengthSize(), Str.ALIGN_RIGHT));
        out.append(GlobalVars.FS_STR);
        out.append(Str.align(this.getTagName(), 
          configObj.getTagNameSize(), Str.ALIGN_LEFT));
        out.append(GlobalVars.FS_STR);
        out.append(Str.align(this.getTagCategory(), 
          configObj.getTagCategorySize(), Str.ALIGN_LEFT));
        out.append(GlobalVars.FS_STR);
        out.append(this.getAnnotation());
        out.append(GlobalVars.FS_STR);
        out.append(this.getTaggedText(text));
        out.append(GlobalVars.LS_STR);
        
        return out.toString();
      } // end Method toString_FIX_LENGTH_FORMAT() ------------------
      
      
      /**
       * toString_SIMPLE_FORMAT Convert markup data to 
       * string
       *
       * @return a String represent the makrup
         */
      public String toString_SIMPLE_FORMAT(String text )
      {
        StringBuffer out = new StringBuffer();

       
        out.append(this.toString());
        out.append(GlobalVars.FS_STR);
        out.append(this.getTaggedText(text));
        out.append(GlobalVars.LS_STR);
        
        return out.toString();
      } // end Method toString_SIMPLE_FORMAT() --------------
     
      
      /**
       * toString_SIMPLEST_FORMAT Convert markup data to 
       * string
       *
       * @return a String represent the makrup
         */
      public String toString_SIMPLEST_FORMAT(String text )
      {
        StringBuffer out = new StringBuffer();
        out.append(this.toString(false));
        out.append(this.getTaggedText(text));
        out.append(GlobalVars.LS_STR);
     
        return out.toString();
      } // end Method toString_SIMPLEST_FORMAT
  
	
	
	
	/**
	* Check if this markup equals to the specified markup.
	* Two markups are equal when they have same offset, length, name, 
	* category, and annotation.
	*
	* @return true if this markup equals to the specified markup
    */
	public boolean equals(Markup markup)
	{
		boolean equalFlag = true;
		if((offset_ != markup.getOffset())
		|| (length_ != markup.getLength())
		|| (tagName_.equals(markup.getTagName()) == false)
		|| (tagCategory_.equals(markup.getTagCategory()) == false)
		|| (annotation_.equals(markup.getAnnotation()) == false))
		{
			equalFlag = false;
		}
		return equalFlag;
	}
	/**
	* Check is a smear (by specifying the start and end position) overlap 
	* this markup. 
	*
	* @param start  the start position of the smear
	* @param end  the end position of the smear
	*
	* @return true if the specified smear range overlap this markup
    */
	public boolean isOverlap(int start, int end)
	{
		boolean flag = false;
		int markupEnd = offset_ + length_ - 1;
		// check first
		if((start > markupEnd)
		|| (end <= offset_))
		{
			return flag;
		}
		
		// go through each point og the high light
		for(int i = start; i <= end; i++)
		{
			if((i >= offset_) && (i <= markupEnd))
			{
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	
	
	// data members
	private int offset_ = 0;	// starting position
	private int length_ = 0;	// length of the markup
	private String tagName_ = null;	// name of tag
	private String tagCategory_ = new String();	// category of tag
	private String annotation_ = new String();	// annotation for this markup
}
