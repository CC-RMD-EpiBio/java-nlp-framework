package gov.nih.nlm.nls.vtt.model;
import java.awt.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.*;
import javax.swing.text.Highlighter.Highlight;

import java.util.*;
import java.io.*;

import gov.nih.nlm.nls.vtt.libs.*;
import gov.nih.nlm.nls.vtt.operations.*;
import gov.nih.nlm.nls.vtt.util.UnderlineHighlighter;
/*****************************************************************************
* This class is the VttDocument Java object. It includes three components:
* <ul>
* <li>Text
* <li>Tags
* <li>Markups
* </ul>
* 
* <p><b>History:</b>
* <ul>
* <li>SCR-100, chlu, 09-15-10, fix bug to open a pure text file
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class VttDocument 
{
	// public constructors
	/**
	* Create a Vtt document Java object with default values.
	*/
    public VttDocument()
    {
		init();
    }
	/**
	* Create a Vtt document Java object by specifying a tag file.
	*/
    public VttDocument(File tagFile)
    {
		init();
		// init tags_
		tags_ = new Tags(tagFile.getAbsolutePath()); 	// default tags
    }
	// public methods
	/**
	* Get the Meta Data of Vtt documents.
	*
	* @return the Meta Data of Vtt documents
	*/
	public MetaData getMetaData()
	{
		return metaData_;
	}
	/**
	* Get the text of Vtt documents.
	*
	* @return the text of Vtt documents
	*/
	public String getText()
	{
		return text_;
	}
	/**
	* Get the tags list of Vtt documents.
	*
	* @return the tags lists of Vtt documents
	*/
	public Tags getTags()
	{	
		return tags_;
	}
	/**
	* Get the markups list of Vtt documents.
	*
	* @return the markups list of Vtt documents
	*/
	public Markups getMarkups()
	{	
		return markups_;
	}
	/**
	* Set the markups list to Vtt document.
	*
	* @param markups the markups list to be set
	*/
	public void setMarkups(Markups markups)
	{	
		markups_ = markups;
	}
	/**
	* Set the text to Vtt document.
	*
	* @param text the text to be set
	*/
	public void setText(String text)
	{
		text_ = text;
	}
	/**
	* Set the tags list to Vtt document.
	*
	* @param tags the tags list to be set
	*/
	public void setTags(Tags tags)
	{	
		
		tags_ = tags;
	}
	/**
	* Read text, tags, markups of Vtt Document from a file
	*
	* @param inFile the input file
	*
	* @return true if the input file contains legal format of Vtt document data
	*/
	public boolean readFromFile(String fileContents, File inFile)
	{
		// init tags, markups, etc...
		boolean readFlag = true;
		init();
		String line = null;
		int mode = MODE_TEXT;	// set the default mode to text
		// readin data for text, tags, markups,
		try
		{
			BufferedReader in;
			if (fileContents!=null) {
				in = new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(fileContents.getBytes()), "UTF-8"));
			} else {
				in = new BufferedReader(new InputStreamReader(
				new FileInputStream(inFile), "UTF-8"));
			}
			// read in line by line from a file
			while((line = in.readLine()) != null)
			{
				// update mode: continue if it is header
				if(isHeader(line) == true)
				{
					mode = getMode(line, mode);
					continue;
				}

				// update text, tags, markup
				switch(mode)
				{
					case MODE_META:
						updateMetaData(line);
						break;
					case MODE_TEXT:
						updateText(line);
						break;
					case MODE_TAG:
						readFlag = updateTags(line);
						break;
					case MODE_MARKUP:
						int maxTextPos = text_.length();
						readFlag = updateMarkup(line, tags_, maxTextPos);
						break;
				}

				// stop reading more lines if erros in format of vtt file
				if(readFlag == false)
				{
					init();	// reset the vttDocument
					break;	// stop
				}
			}
			in.close();
			// sort markups by offset & length
			markups_.sortMarkups();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		// Use the default tags if no tags information
		if(tags_.getTags().size() == 0)
		{
			tags_ = lastTags_;
		}
		return readFlag;
	}
	/**
	* Check if the vtt has been changed by comparing to the orignal inFile.
	*
	* @param inFile the input file to be compared
	* @param vttObj the Vtt object to be compared
	*
	* @return true if Vtt document in vtt object has been changed (different)
	* from the input file and vice versa
	*/
	public boolean hasChanged(File inFile, VttObj vttObj)
	{
		boolean flag = false;
		// vttDocument from inFile
		VttDocument vttDocument = new VttDocument();
		vttDocument.readFromFile(null, inFile);
		String fileStr = VttDocument.toString(vttDocument, 
			VttFormat.SIMPLE_FORMAT, vttObj);
		// current vttDocument
		String curStr = VttDocument.toString(this, VttFormat.SIMPLE_FORMAT,
			vttObj);
		flag = !curStr.equals(fileStr);
		// compare
		return flag;
	}
	/** 
	* Check if the position of the specified markup is legal.
	*
	* @param markup the markup to be check on it's position
	* @param maxTextLength max. text length
	*
	* @return ture if the position of specified markup is legal and vice versa
	*/
	public static boolean isLegalMarkupPos(Markup markup, int maxTextLength)
	{
		boolean flag = false;
		if(markup != null)
		{
			flag = isLegalMarkupPos(markup.getOffset(), markup.getLength(),
				maxTextLength);
		}
		return flag;
	}
	/** 
	* Check if a position of the specified start position and length is legal.
	*
	* @param startPos the start position
	* @param length the length
	* @param maxTextLength max. text length
	*
	* @return ture if the postion of specified markup is legal and vice versa
	*/
	public static boolean isLegalMarkupPos(int startPos, int length,
		int maxTextLength)
	{
		boolean flag = false;
		if(maxTextLength > 0)
		{
			int endPos = startPos + length;
			if((startPos > -1)
			&& (endPos > -1)
			&& (startPos <= maxTextLength)
			&& (endPos <= maxTextLength))
			{
				flag = true;
			}
		}
		else if(maxTextLength == NOT_CHECK)
		{
			flag = true;
		}
		return flag;
	}
	/**
	* Get the reserved tag.
	* The reserved Tag is the 1st element of tags with name of "Text/Clear".
	* This tag is used for:
	* <ul>
	* <li>text tag: bold, font, size, italic, underline
	* <li>highlight color: foreground & background colors
	* </ul>
	*
	* @return the reserved tag
	*/
	public Tag getReservedTag()
	{
		// get tag from 1st element of tags
		return tags_.getTags().elementAt(RESERVED_TAG_INDEX);
	}
	/**
	* set reserve tag to a specified tag
	*
	* @param tag the tag to be set as reserved tag
	*/
	public void setReservedTag(Tag tag)
	{
		tags_.getTags().setElementAt(tag, RESERVED_TAG_INDEX);
	}
	/**
	* Get text tag. The text tag is composed of reserved tag, foreground color,
	* and background color.
	*
	* @return the text tag
	*/
	public Tag getTextTag()
	{
		// get tag from 1st element of tags and reset color
		Tag textTag = new Tag(getReservedTag());
		// set default text color
		textTag.setForeground(TEXT_COLOR); 
		textTag.setBackground(TEXT_BG_COLOR); 
		return textTag;
	}
	// public static method
	/**
	* Convert vttDocuemt to a string representation in the format of
	* text|Tags|Markup.
	*
	* @param vttDocument the vtt document to be converted to string 
	* @param vttFormat the vtt format desired in the string representation
	* @param vttObj the vtt object with configuration to be used
	*
	* @return string representation of a Vtt document
	*/
	public static String toString(VttDocument vttDocument, 
		int vttFormat, VttObj vttObj)
	{
		String text = vttDocument.getText();
		Vector<Tag> tags = vttDocument.getTags().getTags();
		Vector<Markup> markups = vttDocument.getMarkups().getMarkups();
		StringBuffer out = new StringBuffer();
		// 1. print out Text
		out.append(VttText.toString(text));
		// 2. print out textTag & Tags
		out.append(Tags.toString(tags));
		// 3. print out Markup
		ConfigObj configObj = vttObj.getConfigObj();
		out.append(Markups.toString(markups, text, vttFormat, configObj));
		return out.toString();
	}
	
//public static method
 /**
 * Convert vttDocuemt to a string representation in the format of
 * text|Tags|Markup.
 *
 * @param vttDocument the vtt document to be converted to string 
 * @param vttFormat the vtt format desired in the string representation
 * @param vttObj the vtt object with configuration to be used
 *
 * @return string representation of a Vtt document
 */
 public  String toString(  int vttFormat, VttObj vttObj)
 {
   String text = this.getText();
   Vector<Tag> tags = this.getTags().getTags();
   Markups markups = this.getMarkups();
   StringBuffer out = new StringBuffer();
   // 1. print out Text
   out.append(VttText.toString(text));
   // 2. print out textTag & Tags
   out.append(Tags.toString(tags));
   // 3. print out Markup
   ConfigObj configObj = vttObj.getConfigObj();
   
   out.append(markups.showHeader());
   int[] maxColumns = markups.calculateMaxColumnWidths();
 
   for ( Markup markup : markups.getMarkups() ) 
     out.append(markup.toString( text, vttFormat, configObj, maxColumns));
  
   return out.toString();
 }
 
	
	
	
	/**
	* Save vttDocument to a file.
	*
	* @param docFile the file the Vtt document to be saved to
	* @param vttDocument the Vtt document to be saved
	* @param vttFormat the Vtt format desired
	* @param vttObj the vtt object with configuration to be used
	*/
	public static void saveFile(File docFile, VttDocument vttDocument,
		int vttFormat, VttObj vttObj)
	{
		String outFile = docFile.getAbsolutePath();
		try
		{
			BufferedWriter out =  new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outFile), "UTF-8"));
			SaveInfo saveInfo = new SaveInfo(GlobalVars.VTT_FILE_VERSION,
				vttObj.getConfigObj().getUserName(), DateObj.getTime());
			vttDocument.getMetaData().addSaveInfo(saveInfo);	
			String metaStr = vttDocument.getMetaData().toString(true);
			out.write(metaStr);
			String outStr = toString(vttDocument, vttFormat, vttObj);
			out.write(outStr);
			out.close();
		}
		catch(Exception e)
		{
			System.err.println(
				"** Err@SaveFile(): problem of opening/writing file: "
				+ outFile);
			PrintWriter pw = new PrintWriter(System.err);
			e.printStackTrace(pw);
			final String errMsg = "** Err@SaveFile(): problem of opening/writing file: "
					+ outFile + "\n" + e.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, errMsg, "Exception", JOptionPane.ERROR_MESSAGE);	
				}
			});
		}
	}
	/**
	* Close the current opened vttDocument. 
	*
	* @param doc the open styled document of Vtt document
	* @param vttDocument the Vtt document to be saved
	* @param vttObj the vtt object with configuration to be used
	*/
	public static void closeFile(DefaultStyledDocument doc, 
		VttDocument vttDocument, VttObj vttObj)
	{
		// remove document
		try
		{
			doc.remove(0, doc.getLength());
		}
		catch (BadLocationException ble)
		{
			System.err.println(
			"** Err@VttDocument.CloseFile(): Couldn't remove text.");
			final String fMsg = ble.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Vtt Input File Error (Markup)", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		// clean up text, markup, leave tags as is
		vttObj.setDocFile(null);
		vttDocument.setText(new String());
		vttDocument.setMarkups(new Markups());

		// clean up meta data
		vttDocument.getMetaData().reset();
	}
	/**
	* Reload and update text style on Vtt document. 
	*
	* @param doc the open styled document of Vtt document
	* @param vttDocument the Vtt document to be updated
	* @param configObj the Vtt configuration object
	* @param zoomFactor the zoom in/out factor of Vtt document
	*/
	public static void updateStyleForText(VttObj vttObj, DefaultStyledDocument doc,
		VttDocument vttDocument, ConfigObj configObj, int zoomFactor)
	{
		// init & attribute
		try
		{
			// Save the highlights
			Highlight[] highlightsArray = vttObj.getMainFrame().getMainPanel().getTextPane().getHighlighter().getHighlights();
			ArrayList<Integer[]> savedHighlights = new ArrayList<Integer[]>();
			for (Highlight highlight : highlightsArray) {
				if (highlight.getPainter() instanceof UnderlineHighlighter.UnderlineHighlightPainter) {
					Integer[] highlightOffsets = new Integer[2];
					highlightOffsets[0] = highlight.getStartOffset();
					highlightOffsets[1] = highlight.getEndOffset();
					savedHighlights.add(highlightOffsets);
				}
			}
			
			doc.remove(0, doc.getLength());	// remove text
			
			// set text style
			int baseFontSize = configObj.getBaseFontSize();
			SimpleAttributeSet sas = setStyleConstants(vttDocument.getTextTag(),
				baseFontSize, zoomFactor);
			// insert text with style ???
			doc.insertString(doc.getLength(), vttDocument.getText(), sas);
			// Restore the highlights
			vttObj.getMainFrame().getMainPanel().getTextPane().getHighlighter().removeAllHighlights();			
			for (int highlighterIndex = 0; (highlighterIndex < savedHighlights.size()); highlighterIndex++) {
				Integer[] highlight = savedHighlights.get(highlighterIndex);
				vttObj.getMainFrame().getMainPanel().getTextPane().getHighlighter().addHighlight(
						highlight[0].intValue(), highlight[1].intValue(), new UnderlineHighlighter.UnderlineHighlightPainter(Color.red));
			}
		}
		catch (BadLocationException ble)
		{
			System.err.println(
				"** Err@VttDocument.UpdateTextStyleOnTextDoc( ).");
			final String fMsg = ble.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Vtt Input File Error (Markup)", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
	/**
	* Update style for all markups by going through markups and change style 
	* on the text/doc.
	*
	* @param doc the open styled document of Vtt document
	* @param vttDocument the Vtt document to be updated
	* @param configObj the Vtt configuration object
	* @param vttObj the vtt object with configuration to be used
	*
	* @return false if there is an error while updating style on markups 
	*/
	public static boolean updateStyleForMarkups(DefaultStyledDocument doc, 
		VttDocument vttDocument, ConfigObj configObj, VttObj vttObj)
	{
		return updateStyleForMarkups(doc, vttDocument, configObj, vttObj,
			vttObj.getConfigObj().getZoomFactor());
	}
	/**
	* Update style for all markups by going through markups and change style 
	* on the text/doc.
	*
	* @param doc the open styled document of Vtt document
	* @param vttDocument the Vtt document to be updated
	* @param configObj the Vtt configuration object
	* @param vttObj the vtt object with configuration to be used
	* @param zoomFactor the zoom in/out factor of Vtt document
	*
	* @return false if there is an error while updating style on markups 
	*/
	public static boolean updateStyleForMarkups(DefaultStyledDocument doc, 
		VttDocument vttDocument, ConfigObj configObj, VttObj vttObj,
		int zoomFactor)
	{
		Vector<Markup> markups = vttDocument.getMarkups().getMarkups();
		int selectIndex = vttDocument.getMarkups().getSelectIndex();
		Tags tags = vttDocument.getTags();
		// check if the tags include all tag specified from markup
		for(int i = 0; i < markups.size(); i++)
		{
			String tagNameCategory = markups.elementAt(i).getTagNameCategory();
			if(tags.getTagByNameCategory(tagNameCategory) == null)
			{
				System.out.println("** Err@UpdateStyleForMarkups: Tag [" 
					+ tagNameCategory + "] does not exist!");
				return false;	
			}
		}
		// go through all markups
		// a tag is uniquely identify by nameCategory.
		// two tags are considered the same in the following cases
		// Test|123, Base and Test, 123|Base
		// This issue does not cause any problem since it is checked at starts
		//
		//// To improve performance, make all of the changes while the document is not being displayed
		// Temporarily set the textpane to an empty document
		vttObj.getMainFrame().getMainPanel().getTextPane().setDocument(new DefaultStyledDocument());
		// Make the changes to the real document
		for(int i = 0; i < markups.size(); i++)
		{
			Markup markup = markups.get(i);
			String tagNameCategory = markup.getTagNameCategory();
			Tag tag = tags.getTagByNameCategory(tagNameCategory);
			// change select style if it is selected
			if(i == selectIndex)
			{
				// selected: display or not
				Tag selectTag = getSelectTag(tag, configObj, vttObj);
				int baseFontSize = tag.getFontSize(configObj.getBaseFontSize());
				setTextStyleByMarkupTag(doc, markup, selectTag, baseFontSize,
					zoomFactor);
			}
			else	// not selected markups
			{
				int baseFontSize = configObj.getBaseFontSize();
				// Tag is display, but display filter is not => don't display
				if((tag.isDisplay() == true)
				&& (tags.isDisplayAfterFilter(tag) == false))
				{
					tag = new Tag(vttDocument.getTextTag());
				}
				// tag is not display ..
				else if(tag.isDisplay() == false)
				{
					tag = new Tag(vttDocument.getTextTag());
				}
				// set the markup style
				setTextStyleByMarkupTag(doc, markup, tag, baseFontSize,
					zoomFactor);
			}
		}
		// restore the real document to the textpane
		vttObj.getMainFrame().getMainPanel().getTextPane().setDocument(doc);
		return true;	
	}
	
	public static boolean updateStyleForMarkupsInRange(DefaultStyledDocument doc, 
			VttDocument vttDocument, ConfigObj configObj, VttObj vttObj,
			int zoomFactor, int rangeStart, int rangeEnd, boolean highlightRange)
	{
		Vector<Markup> markups = vttDocument.getMarkups().getMarkups();
		Tags tags = vttDocument.getTags();

		// Clear old highlight
		int currHighlightStart = vttObj.getHighlightStart();
		int currHighlightEnd = vttObj.getHighlightEnd();
		// find markups in the current highlighted range
		for (Markup markup : markups) {
			int offset = markup.getOffset();
			int length = markup.getLength();
			int end = offset + length;
			if ((offset >= currHighlightStart && offset <= currHighlightEnd) || (end >= currHighlightStart && end <= currHighlightEnd))
			{
				String tagNameCategory = markup.getTagNameCategory();
				Tag tag = tags.getTagByNameCategory(tagNameCategory);
				if(((tag.isDisplay() == true) && (tags.isDisplayAfterFilter(tag) == false)) || tag.isDisplay() == false)
				{
					tag = new Tag(vttDocument.getTextTag());
				}
				// apply markup
				// set attributes for specified characters, not replace prev attribute
				int baseFontSize = tag.getFontSize(configObj.getBaseFontSize());
				SimpleAttributeSet sas 
					= setStyleConstants(tag, baseFontSize, zoomFactor);
				doc.setCharacterAttributes(offset, length, sas, false);
			}
		}
		
		//// Apply new highlight
		if (highlightRange) {
			vttObj.setHighlightStart(rangeStart);
			vttObj.setHighlightEnd(rangeEnd);
		}
		// find markups in the new highlighted range
		for (Markup markup : markups) {
			int offset = markup.getOffset();
			int length = markup.getLength();
			int end = offset + length;
			if ((offset >= rangeStart && offset <= rangeEnd) || (end >= rangeStart && end <= rangeEnd))
			{
				String tagNameCategory = markup.getTagNameCategory();
				Tag tag = tags.getTagByNameCategory(tagNameCategory);
				tag = getSelectTag(tag, configObj, vttObj);
				if(((tag.isDisplay() == true) && (tags.isDisplayAfterFilter(tag) == false)) || tag.isDisplay() == false)
				{
					tag = new Tag(vttDocument.getTextTag());
				}
				// apply markup
				// set attributes for specified characters, not replace prev attribute
				int baseFontSize = tag.getFontSize(configObj.getBaseFontSize());
				SimpleAttributeSet sas 
					= setStyleConstants(tag, baseFontSize, zoomFactor);
				doc.setCharacterAttributes(offset, length, sas, false);
			}
		}
		return true;
	}

	/**
	* Set text style base on current markup-tag for the text in the markup 
	* range change.
	*
	* @param doc the open styled document of Vtt document
	* @param markup the markup (range) to apply text style
	* @param tag the tag to apply text style
	* @param baseFontSize the base font size of text style
	* @param zoomFactor the zoom in/out factor of Vtt document
	*/
	public static void setTextStyleByMarkupTag(DefaultStyledDocument doc, 
		Markup markup, Tag tag, int baseFontSize, int zoomFactor)
	{
		// apply markup
		int offset = markup.getOffset();
		int length = markup.getLength();
		// set attributes for specified characters, not replace prev attribute
		SimpleAttributeSet sas 
			= setStyleConstants(tag, baseFontSize, zoomFactor);
		doc.setCharacterAttributes(offset, length, sas, false);
	}
	/**
	* Set style constant by specifying tag, base font size, and zoom factor.
	*
	* @param tag the tag to apply text style
	* @param baseFontSize the base font size of text style
	* @param zoomFactor the zoom in/out factor of Vtt document
	*
	* @return the simple attribute set for Vtt document
	*/
	public static SimpleAttributeSet setStyleConstants(Tag tag, 
		int baseFontSize, int zoomFactor)
	{
		int fontSize = tag.getFontSize(baseFontSize) + zoomFactor;
		return setStyleConstants(tag, fontSize);
	}
	/**
	* Set style constants by specifying tag and font size.
	*
	* @param tag the tag to apply text style
	* @param fontSize the font size of text style
	*
	* @return the simple attribute set for Vtt document
	*/
	public static SimpleAttributeSet setStyleConstants(Tag tag, 
		int fontSize)
	{
		SimpleAttributeSet sas = new SimpleAttributeSet();
		// set attribute
		StyleConstants.setBold(sas, tag.isBold());
		StyleConstants.setItalic(sas, tag.isItalic());
		StyleConstants.setUnderline(sas, tag.isUnderline());
		// change color of tag when the tag is text/clear
		if(tag.getName().equals(RESERVED_TAG_STR) == true)
		{
			StyleConstants.setForeground(sas, TEXT_COLOR);
			StyleConstants.setBackground(sas, TEXT_BG_COLOR);
		}
		else	// other tags
		{
			StyleConstants.setForeground(sas, tag.getForeground());
			StyleConstants.setBackground(sas, tag.getBackground());
		}
		StyleConstants.setFontFamily(sas, tag.getFontFamily());
		StyleConstants.setFontSize(sas, fontSize);
		return sas;
	}
	/**
	* Update Document file name to vtt extension.
	*
	* @param inFile the input file
	*
	* @return the file with vtt extension
	*/
	public static File updateDocFileNameToVtt(File inFile)
	{
		String docFile = inFile.getAbsolutePath();
		if(docFile.endsWith(FILE_EXT) == false)
		{
			docFile += FILE_EXT;
		}
		File file = new File(docFile);
		return file;
	}
	/**
	* Get the select tag from vtt Document.
	*
	* @param markupTag the tag of selected markup
	* @param configObj the Vtt configuration object
	* @param vttObj the vtt object with configuration to be used
	*
	* @return the selected tag
	*/
	public static Tag getSelectTag(Tag markupTag, ConfigObj configObj,
		VttObj vttObj)
	{
		// for select markups with display tag
		// and not display with use not display tag
		Tag selectTag = updateSelectTag(markupTag, configObj);
		
		// for those not display tag & use text tag as base
		if((markupTag.isDisplay() == false)
		&& (configObj.isUseTextTagForNotDisplaySelectMarkup() == true))
		{
			selectTag = updateSelectTag(
				vttObj.getVttDocument().getTextTag(), configObj);
		}
		return selectTag;
	}
	/**
	* Return reading mode: text, tags, markups.
	*
	* @param line the lien to be read in
	* @param curMode the current reading mode
	*
	* @return the updated reading mode
	*/
	public static int getMode(String line, int curMode)
	{
		int mode = curMode;
		if(line.equals(META_DATA_STR) == true)
		{
			mode = MODE_META;
		}
		else if(line.equals(TEXT_STR) == true)
		{
			mode = MODE_TEXT;
		}
		else if(line.equals(TAG_STR) == true)
		{
			mode = MODE_TAG;
		}
		else if(line.equals(MARKUP_STR) == true)
		{
			mode = MODE_MARKUP;
		}
		return mode;
	}
	/**
	* Check if the input string is a header line.
	*
	* @param str the input string
	*
	* @return true if it is part of header
	*/
	public static boolean isHeader(String str)
	{
		return header_.contains(str);
	}
	// private methods
	private static Tag updateSelectTag(Tag markupTag, ConfigObj configObj)
	{
		Tag tag = new Tag(markupTag);
		Tag selectTag = new Tag(configObj.getSelectMarkupTag());
		
		if(configObj.isOverwriteSelectBold() == true)
		{
			tag.setBold(selectTag.isBold());
		}
		if(configObj.isOverwriteSelectItalic() == true)
		{
			tag.setItalic(selectTag.isItalic());
		}
		if(configObj.isOverwriteSelectUnderline() == true)
		{
			tag.setUnderline(selectTag.isUnderline());
		}
		if(configObj.isOverwriteSelectFont() == true)
		{
			tag.setFontFamily(selectTag.getFontFamily());
		}
		if(configObj.isOverwriteSelectSize() == true)
		{
			tag.setFontSize(selectTag.getFontSize());
		}
		if(configObj.isOverwriteSelectTextColor() == true)
		{
			tag.setForeground(selectTag.getForeground());
		}
		if(configObj.isOverwriteSelectBackgroundColor() == true)
		{
			tag.setBackground(selectTag.getBackground());
		}
		// force display to be true so it always get markup in preview
		tag.setDisplay(true);
		return tag;
	}
	private void updateMetaData(String line)
	{
		metaData_.readMetaDataFromLine(line);
	}
	private void updateText(String line)
	{
		text_ += line + GlobalVars.LS_STR;
	}
	private boolean updateTags(String line)
	{
		boolean flag = true;
		// skip the line if it is empty or comments (#)
		if((line.length() > 0) && (line.charAt(0) != '#'))
		{
			Tag tag = Tags.readTagFromLine(line);
			if(tag != null)
			{
				tags_.addTag(tag);
			}
			else
			{
				flag = false;
			}
		}
		// set the default qukc key mapping to default
		tags_.setQuickKeyMappingsToDefault();
		return flag;
	}
	private boolean updateMarkup(String line, Tags tags, int maxTextPos)
	{
		boolean flag = true;
		// skip the line if it is empty or comments (#)
		if((line.length() > 0) && (line.charAt(0) != '#'))
		{
			Markup markup = Markups.readMarkupFromLine(line, tags, maxTextPos);
			// make sure markup has legal tag name
			if (markup !=null )
			{	
				markups_.addMarkup(markup);
				
			}
			else
			{
				flag = false;
			}
		}
		return flag;
	}
	private void init()
	{
		// save default tags
		lastTags_ = new Tags(tags_);
		// init text, tags, markup
		metaData_ = new MetaData();
		text_ = new String();
		tags_ = new Tags();
		markups_ = new Markups();
	
	}
	// final variables
	// header
	/** separate string in the header */
	public static final String SEPARATOR_STR = 
	"#<---------------------------------------------------------------------->";
	/** Meta data in the header */
	public static final String META_DATA_STR = "#<Meta Data>";
	/** text string in the header */
	public static final String TEXT_STR = "#<Text Content>";
	/** tag string in the header */
	public static final String TAG_STR = "#<Tags Configuration>";
	/** markup string in the header */
	public static final String MARKUP_STR = "#<MarkUps Information>";
	/** Meta data: default tags file field header string */
	public static final String META_TAGS_FILE_FIELD_STR = 
		"#<Tags file|confirmation|path of default tags file>";
	/** Meta data: save field header string */
	public static final String META_FILE_HISTORY_FIELD_STR = 
		"#<File History|VTT file format version|User name|Time stamp>";
	/** tag field header string */
	public static final String TAG_FIELD_STR = 
		"#<Name|Category|Bold|Italic|Underline|Display|FR|FG|FB|BR|BG|BB|FontFamily|FontSize>";
	/** markup field header string */
	public static final String MARKUP_FIELD_STR = 
		"#<Offset|Length|TagName|TagCategory|Annotation|TagText>";
	private static HashSet<String> header_ = new HashSet<String>();
	// file Extension
	/** VTT file extension */
	public static final String FILE_EXT = ".vtt";
	// mode: for file read in 
	/** read in mode of text */
	public static final int MODE_META = 0;
	/** read in mode of text */
	public static final int MODE_TEXT = 1;
	/** read in mode of tag */
	public static final int MODE_TAG = 2;
	/** read in mode of markup */
	public static final int MODE_MARKUP = 3;
	/** not check value definition */
	public static final int NOT_CHECK = -1;	// not check max text length
	/** reserved tag string definition:
	the default text reserves 1st tag for clearing tag, and highlight colors
	*/
	public static final String RESERVED_TAG_STR = "Text/Clear";
	/** reserved tag index, reserved 1st tag as text tag */
	public static final int RESERVED_TAG_INDEX = 0;	
	/** The start tag index, use the 2nd tag as start */
	public static final int TAG_START_INDEX = 1;
	private static final Color TEXT_COLOR = Color.BLACK;
	private static final Color TEXT_BG_COLOR = Color.WHITE;
	// data members
	private MetaData metaData_ = new MetaData(); // meta data
	private String text_ = new String();	// text
	private Tags tags_ = new Tags();		// tags
	private Tags lastTags_ = new Tags();	// defautl tags: before load vttFile
	private Markups markups_ = null;		// markups
	static
	{
		// init header
		header_.add(SEPARATOR_STR);	// seperator line
		header_.add(META_DATA_STR);	// meta data
		header_.add(TEXT_STR);	// text content
		header_.add(TAG_STR);	// tags configuration
		header_.add(MARKUP_STR);	// markups information

		header_.add(META_TAGS_FILE_FIELD_STR);	// Tags file fields description
		header_.add(META_FILE_HISTORY_FIELD_STR);	//File history fields desc
		header_.add(TAG_FIELD_STR);	// Tag fields description
		header_.add(MARKUP_FIELD_STR);	// Markup fields description
	}
}
