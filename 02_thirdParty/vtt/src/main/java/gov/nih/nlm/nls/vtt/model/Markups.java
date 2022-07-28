package gov.nih.nlm.nls.vtt.model;
import java.io.*;
import java.util.*;
import java.awt.*;

import javax.swing.*;
/*****************************************************************************
* This class is the collection of Java object Markup. This class is used 
* directly in VTT.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class Markups 
{
	// public constructor
	/**
    * Create a Markups Java object with default values.
    */
    public Markups()
	{
	}
	/**
    * Create a Markups Java object by specifying input file and tags.
    */
    public Markups(String inFile, Tags tags)
    {
		loadMarkupsFromFile(inFile, tags);
	}
	// public methods
	/**
    * Get the markups.
	*
	* @return a vector of Markup objects
    */
	public Vector<Markup> getMarkups()
	{
		return markups_;
	}
	/**
    * Get the size of markups.
	*
	* @return the size of Markup objects
    */
	public int getSize()
	{
		return markups_.size();
	}
	/**
    * Get markup by specifying the index.
	*
	* @param index the index of interested markup
	*
	* @return the markup of the specified index
    */
	public Markup getMarkup(int index)
	{
		Markup markup = markups_.elementAt(index);
		return markup;
	}
	/**
	* Add a markup to markups list.
	* 
	* @param markup the markup to be added
	*/
	public void addMarkup(Markup markup)
	{	int index = getIndex(markup);
		// add markup if it does not exist (same offset and length)
		if(getIndex(markup) == -1)
		{
			markups_.addElement(markup);
			
		}
		else	// update the markup by latest tag
		{
			Markup tempMarkup = getMarkup(index);
			tempMarkup.setTagName(markup.getTagName());
			setMarkupAt(tempMarkup, index);
		}
		sortMarkups();
	}
	/**
	* Set a markup to the selected markup on markups list.
	* 
	* @param markup the markup to be set
	*/
	public void setSelectMarkup(Markup markup)
	{
		markups_.setElementAt(markup, selectIndex_);
	}
	/**
	* Set markup at specified position.
	* 
	* @param markup the markup to be set
	* @param index the index of markup to be set on the markups list
	*/
	public void setMarkupAt(Markup markup, int index)
	{
		markups_.setElementAt(markup, index);
	}
	/**
	* Remove markup from specified position.
	*
	* @param index the position of markup to be removed on the markups list
	*/
	public void removeMarkupAt(int index)
	{ 
		if((markups_.size() > 0)
		&& (index > -1)
		&& (index < markups_.size()))
		{
			markups_.removeElementAt(index);
		}
	}
	/**
	* get the index of a specified Markup by name (offset and length), 
	* This method returns -1 if the specified markup does not exist.
	*
	* @param markup  the markup is in interested
	*
	* @return the index of interested markup on the markups list
	*/
	public int getIndex(Markup markup)
	{
		int index = -1;
		for(int i = 0; i < markups_.size(); i++)
		{
			Markup cur = markups_.elementAt(i);
			if((cur.getOffset() == markup.getOffset())
			&& (cur.getLength() == markup.getLength()))
			{
				index = i;
				break;
			}
		}
		return index;
	}
	/**
	* save markups to file.
	*
	* @param outFile the file of markups list to be saved to
	*/
	public void saveMarkupsToFile(String outFile)
	{
		try
		{
			BufferedWriter out =  new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outFile), "UTF-8"));
			writeMarkupsToFile(out, markups_);
			out.close();
		}
		catch(Exception e)
		{
			System.err.println("** Err: problem of opening/writing file: "
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
	* Load markups from a file.
	* This method checks if all markups belongs to existing Tags.
	*
	* @param inFile the file of markups list to be loaded from
	* @param tags the tags used in the VttDocument
	*/
	public void loadMarkupsFromFile(String inFile, Tags tags)
	{
		String line = null;
		int lineNo = 0;
		int markupNo = 0;
		// init markups_ vector
		markups_ = new Vector<Markup>();
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(inFile), "UTF-8"));
			// read in line by line from a file
			while((line = in.readLine()) != null)
			{
				lineNo++;
				// skip the line if it is empty or comments (#)
				if((line.length() > 0) && (line.charAt(0) != '#'))
				{
					markupNo++;
					Markup markup = readMarkupFromLine(line, tags);
					if(markup != null)
					{
						markups_.addElement(markup);	
					}
				}
			}
			in.close();
		}
		catch(Exception e)
		{
			System.err.println("** Err: problem of opening/reading file: "
				+ inFile + " at line: " + lineNo);
			System.err.println(line);
			System.err.println("Exception: " + e.toString());	
			final String fMsg = e.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		// sort the markups_
		sortMarkups();
	}
	/**
	* update tag name category for all markups with specified the original
	* and new name category.
	*
	* @param orgNameCategory original nameCategory
	* @param newNameCategory new nameCategory
	*/
	public void updateTagNameCategoryInMarkups(String orgNameCategory, 
		String newNameCategory)
	{
		Vector<Markup> markups = new Vector<Markup>();
		String orgName = Tag.getName(orgNameCategory);
		String orgCategory = Tag.getCategory(orgNameCategory);
		String newName = Tag.getName(newNameCategory);
		String newCategory = Tag.getCategory(newNameCategory);
		for(int i = 0; i < markups_.size(); i++)
		{
			Markup markup = markups_.elementAt(i);
			// update the tag name 
			if((markup.getTagName().equals(orgName) == true)
			&& (markup.getTagCategory().equals(orgCategory) == true))
			{
				markup.setTagName(newName);
				markup.setTagCategory(newCategory);
			}
			markups.addElement(markup);
		}
		markups_ = markups;
	}
	/**
	* Delete all markups with specified tag nameCategory.
	*
	* @param tagNameCategory tag namecategory of markup to be deleted
	*/
	public void deleteMarkupByTag(String tagNameCategory)
	{
		Vector<Markup> markups = new Vector<Markup>();
		for(int i = 0; i < markups_.size(); i++)
		{
			Markup markup = markups_.elementAt(i);
			if(markup.getTagNameCategory().equals(tagNameCategory) == false)
			{
				markups.addElement(markup);
			}
		}
		markups_ = markups;
	}
	// public static method
	/**
	* Write specified markups to a file.
	*
	* @param out the buffered writer of out file destination
	* @param markups markups to be sent to a file
	*/
	public static void writeMarkupsToFile(BufferedWriter out,
		Vector<Markup> markups) throws IOException
	{
		String markupsStr = toString(markups);
		out.write(markupsStr);
	}
	/**
	* Convert specified markups to String.
	*
	* @param markups the markups to be converted to string
	*
	* @return the string representation of the specified markups
	*/
	public static String toString(Vector<Markup> markups)
	{
		return toString(markups, true, true);
	}
	/**
	* Convert specified markups to String with options of showing header and 
	* annotation.
	*
	* @param markups the markups to be converted to string
	* @param showHeader a boolean flag of showing header
	* @param showAnnotation a boolean flag of showing annotation
	*
	* @return the string representation of the specified markups
	*/
	public static String toString(Vector<Markup> markups, boolean showHeader,
		boolean showAnnotation)
	{
		StringBuffer out = new StringBuffer();
		// Markups header	
		if(showHeader == true)
		{
			out.append(VttDocument.SEPARATOR_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.MARKUP_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.MARKUP_FIELD_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.SEPARATOR_STR);
			out.append(GlobalVars.LS_STR);
		}
		// go through all Markups	
		for(int i = 0; i < markups.size(); i++)
		{
			Markup markup = markups.elementAt(i);
			out.append(markup.toString(showAnnotation));
			out.append(GlobalVars.LS_STR);
		}		
		return out.toString();
	}
	
	/**
	 * showHeader
	  * @return the string representation of the specified markups
	  */
	  public String showHeader()
	  {
	    StringBuffer out = new StringBuffer();
	   
	      out.append(VttDocument.SEPARATOR_STR);
	      out.append(GlobalVars.LS_STR);
	      out.append(VttDocument.MARKUP_STR);
	      out.append(GlobalVars.LS_STR);
	      out.append(VttDocument.MARKUP_FIELD_STR);
	      out.append(GlobalVars.LS_STR);
	      out.append(VttDocument.SEPARATOR_STR);
	      out.append(GlobalVars.LS_STR);
	   
		return out.toString();
	}
	/**
	* Convert specified markups to string with specifying text, vtt format, and
	* configuration.
	*
	* @param markups the markups to be converted to string
	* @param text the text section of vtt document
	* @param vttFormat vtt formats: SIMPLE_FORMAT, READABLE_FORMAT, 
	* 	FIX_LENGTH_FORMAT, and SIMPLEST_FORMAT
	* @param configObj vtt configuration object
	*
	* @return the string representation of the specified markups
	*/
	public static String toString(Vector<Markup> markups, String text, 
		int vttFormat, ConfigObj configObj)
	{
		return toString(markups, text, vttFormat, configObj, true);
	}
	/**
   * calculateMaxColumWidths
    * @return int[4]  [maxOffsetSize,maxLengthSize, maxTagNameSize, maxTagCategorySize]
    */
    public int[] calculateMaxColumnWidths( )
    {

      Vector<Markup> markups = this.markups_; 
      int maxOffsetSize = 0;
      int maxLengthSize = 0;
      int maxTagNameSize = 0;
      int maxTagCategorySize = 0;
      for(int i = 0; i < markups.size(); i++)
      {
        Markup markup = markups.elementAt(i);
        int offsetSize 
          = Integer.toString(markup.getOffset()).length();
        int lengthSize 
          = Integer.toString(markup.getLength()).length();
        int tagNameSize = markup.getTagName().length();
        int tagCategorySize = markup.getTagCategory().length();
        maxOffsetSize 
          = ((maxOffsetSize >= offsetSize)?maxOffsetSize:offsetSize); 
        maxLengthSize 
          = ((maxLengthSize >= lengthSize)?maxLengthSize:lengthSize); 
        maxTagNameSize 
        = ((maxTagNameSize >= tagNameSize)?maxTagNameSize:tagNameSize); 
        maxTagCategorySize 
        = ((maxTagCategorySize >= tagCategorySize)?maxTagCategorySize:tagCategorySize); 
      }
      maxOffsetSize = ((maxOffsetSize < MAX_OFFSET_SIZE)
        ?maxOffsetSize:MAX_OFFSET_SIZE);
      maxLengthSize = ((maxLengthSize < MAX_LENGTH_SIZE)
        ?maxLengthSize:MAX_LENGTH_SIZE);
      maxTagNameSize = ((maxTagNameSize < MAX_TAG_NAME_SIZE)
        ?maxTagNameSize:MAX_TAG_NAME_SIZE);
      maxTagCategorySize = ((maxTagCategorySize < MAX_TAG_CATEGORY_SIZE)
        ?maxTagCategorySize:MAX_TAG_CATEGORY_SIZE);
      
      int[]  returnValues = new int[4];
      returnValues[0] = maxOffsetSize;
      returnValues[1] = maxLengthSize;
      returnValues[2] = maxTagNameSize;
      returnValues[3] = maxTagCategorySize;
      
      return returnValues;
    } // end Method calculateMaxColumnWidths() --------------
    /**
	* Convert specified markups to string with specifying text, vtt format, 
	* configuration, and flag of showing header.
	*
	* @param markups the markups to be converted to string
	* @param text the text section of vtt document
	* @param vttFormat vtt formats: SIMPLE_FORMAT, READABLE_FORMAT, 
	* 	FIX_LENGTH_FORMAT, and SIMPLEST_FORMAT
	* @param configObj vtt configuration object
	* @param showHeader a boolean flag of showing header
	*
	* @return the string representation of the specified markups
	*/
     public  String _toString(Vector<Markup>  markups,
                             String          text, 
                             int             vttFormat, 
                             ConfigObj       configObj, 
                             boolean         showHeader)
     {
       StringBuffer out = new StringBuffer();
       // Markups header/banner
       if(showHeader)
       {
        out.append(showHeader());
       }
       // go through all Markups 
       int maxWidths[] = calculateMaxColumnWidths( );
       
       for(int i = 0; i < markups.size(); i++)
       {
         Markup markup = markups.elementAt(i);
         switch(vttFormat)
         {
           case VttFormat.READABLE_FORMAT:   out.append(markup.toString_READABLE_FORMAT(maxWidths, text));       break;
           case VttFormat.FIX_LENGTH_FORMAT: out.append(markup.toString_FIX_LENGTH_FORMAT(configObj, text));     break;
           case VttFormat.SIMPLE_FORMAT:     out.append(markup.toString_SIMPLE_FORMAT(text));                    break; 
           case VttFormat.SIMPLEST_FORMAT:   out.append(markup.toString_SIMPLEST_FORMAT(text));                  break;
           
         }
       } // end loop thru markups
       return out.toString();
     }
    
    
	/**
	* Convert specified markups to string with specifying text, vtt format, 
	* configuration, and flag of showing header.
	*
	* @param markups the markups to be converted to string
	* @param text the text section of vtt document
	* @param vttFormat vtt formats: SIMPLE_FORMAT, READABLE_FORMAT, 
	* 	FIX_LENGTH_FORMAT, and SIMPLEST_FORMAT
	* @param configObj vtt configuration object
	* @param showHeader a boolean flag of showing header
	*
	* @return the string representation of the specified markups
	*/
	public static String toString(Vector<Markup> markups, String text, 
		int vttFormat, ConfigObj configObj, boolean showHeader)
	{
		StringBuffer out = new StringBuffer();
		// Markups header/banner
		if(showHeader)
		{
			out.append(VttDocument.SEPARATOR_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.MARKUP_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.MARKUP_FIELD_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.SEPARATOR_STR);
			out.append(GlobalVars.LS_STR);
		}
		// go through all Markups	
		switch(vttFormat)
		{
			case VttFormat.READABLE_FORMAT:
				// find max size of fields 1, 2, 3
				int maxOffsetSize = 0;
				int maxLengthSize = 0;
				int maxTagNameSize = 0;
				int maxTagCategorySize = 0;
				for(int i = 0; i < markups.size(); i++)
				{
					Markup markup = markups.elementAt(i);
					int offsetSize 
						= Integer.toString(markup.getOffset()).length();
					int lengthSize 
						= Integer.toString(markup.getLength()).length();
					int tagNameSize = markup.getTagName().length();
					int tagCategorySize = markup.getTagCategory().length();
					maxOffsetSize 
						= ((maxOffsetSize >= offsetSize)?maxOffsetSize:offsetSize);	
					maxLengthSize 
						= ((maxLengthSize >= lengthSize)?maxLengthSize:lengthSize);	
					maxTagNameSize 
					= ((maxTagNameSize >= tagNameSize)?maxTagNameSize:tagNameSize);	
					maxTagCategorySize 
					= ((maxTagCategorySize >= tagCategorySize)?maxTagCategorySize:tagCategorySize);	
				}
				maxOffsetSize = ((maxOffsetSize < MAX_OFFSET_SIZE)
					?maxOffsetSize:MAX_OFFSET_SIZE);
				maxLengthSize = ((maxLengthSize < MAX_LENGTH_SIZE)
					?maxLengthSize:MAX_LENGTH_SIZE);
				maxTagNameSize = ((maxTagNameSize < MAX_TAG_NAME_SIZE)
					?maxTagNameSize:MAX_TAG_NAME_SIZE);
				maxTagCategorySize = ((maxTagCategorySize < MAX_TAG_CATEGORY_SIZE)
					?maxTagCategorySize:MAX_TAG_CATEGORY_SIZE);
				// print out the readable format by lineup fields 1, 2, 3
				for(int i = 0; i < markups.size(); i++)
				{
					Markup markup = markups.elementAt(i);
					out.append(Str.align(Integer.toString(markup.getOffset()), 
						maxOffsetSize, Str.ALIGN_RIGHT));
					out.append(GlobalVars.FS_STR);
					out.append(Str.align(Integer.toString(markup.getLength()), 
						maxLengthSize, Str.ALIGN_RIGHT));
					out.append(GlobalVars.FS_STR);
					out.append(Str.align(markup.getTagName(), maxTagNameSize,
						Str.ALIGN_LEFT));
					out.append(Str.align(markup.getTagCategory(), 
						maxTagCategorySize, Str.ALIGN_LEFT));
					out.append(GlobalVars.FS_STR);
					out.append(markup.getAnnotation());
					out.append(GlobalVars.FS_STR);
					out.append(markup.getTaggedText(text));
					out.append(GlobalVars.LS_STR);
				}
				break;
			case VttFormat.FIX_LENGTH_FORMAT:	
				// print out the format with fixed length of fields 1, 2, 3
				for(int i = 0; i < markups.size(); i++)
				{
					try {
						Markup markup = markups.elementAt(i);
						out.append(Str.align(Integer.toString(markup.getOffset()), 
							configObj.getOffsetSize(), Str.ALIGN_RIGHT));
						out.append(GlobalVars.FS_STR);
						out.append(Str.align(Integer.toString(markup.getLength()), 
							configObj.getLengthSize(), Str.ALIGN_RIGHT));
						out.append(GlobalVars.FS_STR);
						out.append(Str.align(markup.getTagName(), 
							configObj.getTagNameSize(), Str.ALIGN_LEFT));
						out.append(GlobalVars.FS_STR);
						out.append(Str.align(markup.getTagCategory(), 
							configObj.getTagCategorySize(), Str.ALIGN_LEFT));
						out.append(GlobalVars.FS_STR);
						out.append(markup.getAnnotation());
						out.append(GlobalVars.FS_STR);
						out.append(markup.getTaggedText(text));
						out.append(GlobalVars.LS_STR);
					} catch (Exception e) {
						String errMsg = 
								"** Error: invalid markup: " + markups.elementAt(i);
						System.err.println(errMsg);
						final String fMsg = e.getMessage();
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
							}
						});
					}
				}
				break;
			case VttFormat.SIMPLE_FORMAT:	
				// Save the markups
				for(int i = 0; i < markups.size(); i++)
				{
					Markup markup = markups.elementAt(i);
					out.append(markup.toString());
					out.append(GlobalVars.FS_STR);
					out.append(markup.getTaggedText(text));
					out.append(GlobalVars.LS_STR);
				}
				break;
			case VttFormat.SIMPLEST_FORMAT:	
				for(int i = 0; i < markups.size(); i++)
				{
					Markup markup = markups.elementAt(i);
					out.append(markup.toString(false));
					out.append(markup.getTaggedText(text));
					out.append(GlobalVars.LS_STR);
				}
				break;
		}
		return out.toString();
	}
	/**
	* Get the markup report in a format of line Vector of String.
	*
	* @param nameCategoryList the vector of nameCategory to shown on the report
	* @param markups the markups to be reports
	*
	* @return a report composed of a vector of line (String) 
	*/
	public static Vector<String> getReportStrs(Vector<String> nameCategoryList, 
		Vector<Markup> markups)
	{
		Vector<String> reportStrs = new Vector<String>();
		// go through markups to calculate markup number (sub-total)
		int[] markupNums = new int[nameCategoryList.size()];	// sub total
		for(int i = 0; i < markups.size(); i++)
		{
			String tagNameCategory = markups.elementAt(i).getTagNameCategory();
			// update sub total
			for(int j = 0; j < nameCategoryList.size(); j++)
			{
				if(tagNameCategory.equals(nameCategoryList.elementAt(j)) == true)
				{
					markupNums[j]++;
					break;
				}
			}
		}
		// print out sub-total for each name category
		for(int i = 0; i < nameCategoryList.size(); i++)
		{
			String reportStr = nameCategoryList.elementAt(i);
			reportStrs.add(reportStr + ": " + markupNums[i] 
				+ GlobalVars.LS_STR);
		}
		reportStrs.add("--------------------------" + GlobalVars.LS_STR);
		reportStrs.add("Total Markup Number: " + markups.size() 
			+ GlobalVars.LS_STR);
		return reportStrs;
	}
	/**
	* Read a markup from a string (line), without tags checking.
	*
	* @param line the input line for the markup
	*
	* @return the markup object read from the input line
	*/
	public static Markup readMarkupFromLine(String line) 
	{
		return readMarkupFromLine(line, null, VttDocument.NOT_CHECK);
	}
	/**
	* Read a markup from a string (line), with tags checking.
	*
	* @param line the input line for the markup
	* @param tags the tags in vtt Document used for valifying
	* @param maxTextPos the max. tetx position
	*
	* @return the markup object read from the input line
	*/
	public static Markup readMarkupFromLine(String line, Tags tags,
		int maxTextPos) 
	{
		return readMarkupFromLine(line, tags, maxTextPos, 
			GlobalVars.VERBOSE_ALL);
	}
	/**
	* Read a markup from a string (line), with tags checking and verbose type 
	* options.
	*
	* @param line the input line for the markup
	* @param tags the tags in vtt Document used for valifying
	* @param maxTextPos the max. tetx position
	* @param verboseType verbose types: VERVOSE_GUI and VERBOSE_STD_IO
	*
	* @return the markup object read from the input line
	*/
	public static Markup readMarkupFromLine(String line, Tags tags,
		int maxTextPos, int verboseType) 
	{
		Markup markup = null;
		// use '|' as delimiter to parse token
		int index1 = line.indexOf(GlobalVars.FS_STR);	// offset
		int index2 = line.indexOf(GlobalVars.FS_STR, index1+1);	// length
		int index3 = line.indexOf(GlobalVars.FS_STR, index2+1);	// tag name
		int index4 = line.indexOf(GlobalVars.FS_STR, index3+1);	// tag category
		int index5 = line.indexOf(GlobalVars.FS_STR, index4+1);	// annotation
		try
		{
			// read first 5 fields in from inFile data
			int offset = Integer.parseInt(line.substring(0, index1).trim());
			int length = Integer.parseInt(line.substring(index1+1, index2).trim());
			String tagName = line.substring(index2+1, index3).trim();
			String tagCategory = line.substring(index3+1, index4).trim();
			// if there is no annotation, index5 = -1
			String annotation = ((index5 == -1)?line.substring(index4+1).trim():line.substring(index4+1, index5).trim());
			if((annotation == null)
			|| (annotation.length() == 0))
			{
				annotation = new String();
			}
			// ignore fields 5+: tag text and others
			// set markup, check tag
			String tagNameCategory = Tag.getNameCategory(tagName, tagCategory);
			if((tags != null)
			&& !tagName.contains("col-") && (tags.containsNameCategory(tagNameCategory) == false))
			{
				String errMsg = 
					"** Error: illegal tag name-category in markup data from input file!\n"
					+ "Tag name-category, (" + tagNameCategory 
					+ "), does not exist!\n"
					+ VttDocument.SEPARATOR_STR + "\n"
					+ VttDocument.MARKUP_FIELD_STR + "\n"
					+ VttDocument.SEPARATOR_STR + "\n" + line;
				
				switch(verboseType)
				{
					case GlobalVars.VERBOSE_STD_IO:
						System.err.println(errMsg);
						break;
					case GlobalVars.VERBOSE_GUI:
						// send out error dialog
						JOptionPane.showMessageDialog(null, errMsg,
							"Vtt Input File Error (Markup)", 
							JOptionPane.ERROR_MESSAGE);
						break;
					case GlobalVars.VERBOSE_ALL:
					default:
						System.err.println(
						"** Err@Markups.ReadMarkupFromLine( ): Illegal Tag Name-Category.");
						System.err.println("Tag name-category, (" 
							+ tagNameCategory + "), does not exist!");
						System.err.println(line);
						// send out error dialog
						JOptionPane.showMessageDialog(null, errMsg,
							"Vtt Input File Error (Markup)", 
							JOptionPane.ERROR_MESSAGE);
						break;
				}
			}
			else if(VttDocument.isLegalMarkupPos(offset, length, maxTextPos)
				== false)
			{
				String errMsg =
					"** Error: illegal tag position in markup data from input file!\n"
					+ "Tag position: [" + offset + ", " + (offset + length) 
					+ "]; Max. text pos: [" + maxTextPos + "]\n"
					+ VttDocument.SEPARATOR_STR + "\n"
					+ VttDocument.MARKUP_FIELD_STR + "\n"
					+ VttDocument.SEPARATOR_STR + "\n" + line;
				switch(verboseType)
				{
					case GlobalVars.VERBOSE_STD_IO:
						System.err.println(errMsg);
						break;
					case GlobalVars.VERBOSE_GUI:
						// send out error dialog
						JOptionPane.showMessageDialog(null, errMsg,
							"Vtt Input File Error (Markup)", 
							JOptionPane.ERROR_MESSAGE);
						break;
					case GlobalVars.VERBOSE_ALL:
					default:
						System.err.println(
					"** Err@Markups.ReadMarkupFromLine( ): Illegal Tag Position.");
						System.err.println("Tag position: [" + offset + ", "  
							+ (offset+ length) + "]; Max. text pos: [" 
							+ maxTextPos + "]");
						System.err.println(line);
						// send out error dialog
						JOptionPane.showMessageDialog(null, errMsg,
							"Vtt Input File Error (Markup)", 
							JOptionPane.ERROR_MESSAGE);
						break;
				}
			}
			/**
			else if(((tags == null)
			 || (tags.ContainsNameCategory(tagNameCategory) == true))
			&& ((maxTextPos == -1)
			 || ((offset <= maxTextPos) && (offset + length) <= maxTextPos)))
			 **/
			else
			{
				markup = new Markup(offset, length, tagName, tagCategory, 
					annotation);
			}
		}
		catch(NumberFormatException nfe)
		{
			String errMsg = 
				"** Error: wrong markup data/format in the input file!\n"
				+ VttDocument.SEPARATOR_STR + "\n"
				+ VttDocument.MARKUP_FIELD_STR + "\n"
				+ VttDocument.SEPARATOR_STR + "\n" + line;
			switch(verboseType)
			{
				case GlobalVars.VERBOSE_STD_IO:
					System.err.println(errMsg);
					final String fMsg = nfe.getMessage();
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
						}
					});
					break;
				case GlobalVars.VERBOSE_GUI:
					// send out error dialog
					final String f2Msg = errMsg;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JOptionPane.showMessageDialog(null, f2Msg,
									"Vtt Input File Error (Markup)", 
									JOptionPane.ERROR_MESSAGE);
						}
					});
					break;
				case GlobalVars.VERBOSE_ALL:
				default:
					System.err.println(
					"** Err@Tags.ReadMarkupFromLine(): Illegal data/format for markup.");
					System.err.println(line);
					System.err.println(nfe.toString());
					// send out error dialog
					final String f3Msg = errMsg;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JOptionPane.showMessageDialog(null, f3Msg,
									"Vtt Input File Error (Markup)", 
									JOptionPane.ERROR_MESSAGE);
						}
					});
					break;
			}
		}
		return markup;
	}
	/** 
	* Convert action to the action string.
	*
	* @param action the integer value of action
	*
	* @return the string representation of action
	*/
	public static String getActionStr(int action)
	{
		String actionStr = "Add";
		switch(action)
		{
			case ADD:
				actionStr = "Add";
				break;
			case DELETE:
				actionStr = "Delete";
				break;
			case CHANGE:
				actionStr = "Change";
				break;
			case JOIN:
				actionStr = "Join";
				break;
			case OVERRIDE:
				actionStr = "Override";
				break;
		}
		return actionStr;
	}
	/** 
	* Get the selected index.
	*
	* @return the selected index
	*/
	public int getSelectIndex()
	{
		return selectIndex_;
	}
	/** 
	* Get the selected markup.
	*
	* @return the selected markup
	*/
	public Markup getSelectMarkup()
	{
		return markups_.elementAt(selectIndex_);
	}
	/** 
	* Set the selected index.
	*
	* @param selectIndex the selected index
	*/
	public void setSelectIndex(int selectIndex)
	{
		selectIndex_ = selectIndex;
	}
	/** 
	* Sort the markups by:
	* <ul>
	* <li>offset (smaller go first)
	* <li>length (bigger go first if offset is the same)
	* </ul>
	*/
	public void sortMarkups()
	{
		Collections.sort(markups_, mc_);
	}
	/**
	* Find the selected index by specifying the current position.
	* This method should be called whenever Caret position is changed.
	* It returns position of cur caret of end of selected markup.
	*
	* @param curPos the current position of caret
	*
	* @return the index of selected markup, returns -1 if no markup is selected
	*/
	public int findSelectIndex(int curPos)
	{
		int newPos = curPos;
		// find selectIndex_ by caret position click
		selectIndex_ = -1;
		for(int i = 0; i < markups_.size(); i++)
		{
			Markup markup = markups_.elementAt(i);
			int start = markup.getOffset();
			int end = start + markup.getLength();
			
			// find the selected index by curPos
			if((curPos != 0)
			&& (curPos >= start)
			&& (curPos <= end)
			&& (!"SnippetColumn".equals(markup.getTagName())))
			{
				selectIndex_ = i;
				newPos = end;
				break;
			}
		}
		return newPos;
	}
	/**
	* Find the select index by specifying the markup
	*
	* @param markup the markup to be found for its index
	*/
	public void findSelectIndex(Markup markup)
	{
		if(markup == null)
		{
			selectIndex_ = -1;
		}
		else
		{
			findSelectIndex(markup.getOffset(), markup.getLength());
		}
	}
	/**
	* Find the select index by start and end (smear)
	*
	* @param start  the start position of smear
	* @param length  the length of smear
	*/
	public void findSelectIndex(int start, int length)
	{
		// find selectIndex_ by start and end positions
		selectIndex_ = -1;
		for(int i = 0; i < markups_.size(); i++)
		{
			Markup markup = markups_.elementAt(i);
			if((markup.getOffset() == start)
			&& (markup.getLength() == length))
			{
				selectIndex_ = i;
				break;
			}
			else if(markup.getOffset() > start)
			{
				break;
			}
		}
	}
	/**
	* Move the selected markup to the first from the beginning and then
	* return the beginning position of the markup.
	*
	* @param showAll a boolean flag of showing all markups
	* @param vttObj the vtt object
	*
	* @return the beginning position of the first select markup from the 
	* beginning
	*/
	public int getFirstSelectIndex(boolean showAll, VttObj vttObj)
	{
		int startIndex = 0;
		return getNextSelectIndexByIndex(showAll, startIndex, vttObj);
	}
	/**
	* Move the selected markup to the last from the end and then
	* return the end position of markup.
	*
	* @param showAll a boolean flag of showing all markups
	* @param vttObj the vtt object
	*
	* @return the end position of the last select markup from the end
	*/
	public int getLastSelectIndex(boolean showAll, VttObj vttObj)
	{
		int startIndex = markups_.size() - 1;
		return getPrevSelectIndexByIndex(showAll, startIndex, vttObj);
	}
	/**
	* Increase the selected index to the next display markup, 
	* and then return the end position of markup
	*
	* @param showAll a boolean flag of showing all markups
	* @param vttObj the vtt object
	*
	* @return the end position of selected markup
	*/
	public int increaseSelectIndex(boolean showAll, VttObj vttObj)
	{
		return getNextSelectIndex(showAll, vttObj);
	}
	/**
	* Decrease the selected index to the previous display markup, 
	* and then return the end position of markup
	*
	* @param showAll a boolean flag of showing all markups
	* @param vttObj the vtt object
	*
	* @return the end position of selected markup
	*/
	public int decreaseSelectIndex(boolean showAll, VttObj vttObj)
	{
		return getPrevSelectIndex(showAll, vttObj);
	}
	// private methods
	private static Markup readMarkupFromLine(String line, Tags tags)
	{
		return readMarkupFromLine(line, tags, VttDocument.NOT_CHECK);
	}
	// get next selected index
	// TBD: not working on displayed forward
	private int getNextSelectIndex(boolean showAll, VttObj vttObj)
	{
		int newPos = -1;
		// 1. if no markup is selected, get the next markup index from cur pos
		if(selectIndex_ == -1)
		{
			int caretPos = vttObj.getMainFrame().getMainPanel().getTextPane().getCaretPosition();
			newPos = getNextSelectIndexByPos(showAll, caretPos, vttObj);
		}
		// 2. if selectIndex_ != -1, return the next markup index from cur index
		else
		{
			// update start index
			int startIndex = selectIndex_ + 1;
			if(startIndex == markups_.size())	// go back to the first 1
			{
				startIndex = 0;
			}
			newPos = getNextSelectIndexByIndex(showAll, startIndex, vttObj);
		}
		return newPos;
	}
	private int getNextSelectIndexByPos(boolean showAll, int caretPos,
		VttObj vttObj)
	{
		int newPos = -1;
		Tags tags = vttObj.getVttDocument().getTags();
		
		// go through the markups by currnet pos
		for(int i = 0; i < markups_.size(); i++)
		{
			Markup markup = markups_.elementAt(i);
			String tagNameCategory = markup.getTagNameCategory();
			Tag tag = tags.getTagByNameCategory(tagNameCategory);
			// check if show all or displayable
			if(((showAll == true) || (tag.isDisplay() == true))
			&& (!"SnippetColumn".equals(markup.getTagName())) 
			&& (markup.getOffset() >= caretPos))
			{
				// set selectIndex_
				selectIndex_ = i;
				// set end position for return
				newPos = markup.getOffset() + markup.getLength();
				break;
			}
		}
		// if no next selected index found, set index to -1
		if(newPos == -1)
		{
			selectIndex_ = -1;
			newPos = caretPos;
		}
		return newPos;
	}
	private int getNextSelectIndexByIndex(boolean showAll, int startIndex,
		VttObj vttObj)
	{
		int newPos = -1;
		Tags tags = vttObj.getVttDocument().getTags();
		// go through the markups from next pos
		int size = markups_.size();
		for(int i = 0; i < size; i++)
		{
			int ii = (i + startIndex)%size;		// to recycle
			Markup markup = markups_.elementAt(ii);
			String tagNameCategory = markup.getTagNameCategory();
			Tag tag = tags.getTagByNameCategory(tagNameCategory);
			// check if show all or displayable
			if(((showAll == true) || (tag.isDisplay() == true)) 
					&& (!"SnippetColumn".equals(markup.getTagName())))
			{
				// this is need to avoid infinit loop in overlap markup
				// index is updated when caret position is changed
				selectIndex_ = ii;
				// set end position for return
				newPos = markup.getOffset() + markup.getLength();
				break;
			}
		}
		// if no next selected index found, set index to -1
		if(newPos == -1)
		{
			selectIndex_ = -1;
			newPos = vttObj.getMainFrame().getMainPanel().getTextPane().getCaretPosition();
		}
		return newPos;
	}
	// get the select Index of prev markup, 
	private int getPrevSelectIndex(boolean showAll, VttObj vttObj)
	{
		int newPos = -1;
		// 1. if no markup is selected, get the prev markup index from cur pos
		if(selectIndex_ == NO_SELECT)
		{
			int caretPos = vttObj.getMainFrame().getMainPanel().getTextPane().getCaretPosition();
			newPos = getPrevSelectIndexByPos(showAll, caretPos, vttObj);
		}
		// 2. if selectIndex_ != -1, return the next markup index from cur index
		else
		{
			// update start index
			int startIndex = selectIndex_ - 1;
			if(startIndex == NO_SELECT)
			{
				startIndex = markups_.size()-1;
			}
			newPos = getPrevSelectIndexByIndex(showAll, startIndex, vttObj);
		}
		return newPos;
	}
	private int getPrevSelectIndexByPos(boolean showAll, int caretPos,
		VttObj vttObj)
	{
		int newPos = -1;
		Tags tags = vttObj.getVttDocument().getTags();
		// go through the markups by currnet pos
		for(int i = markups_.size()-1; i >= 0; i--)
		{
			Markup markup = markups_.elementAt(i);
			String tagNameCategory = markup.getTagNameCategory();
			Tag tag = tags.getTagByNameCategory(tagNameCategory);
			// check if show all or displayable
			if(((showAll == true) || (tag.isDisplay() == true))
			&& (!"SnippetColumn".equals(markup.getTagName())) 
			&& (markup.getOffset() < caretPos))
			{
				// set selectIndex_
				selectIndex_ = i;
				// set end position for return
				newPos = markup.getOffset() + markup.getLength();
				break;
			}
		}
		// if no prev selected index found, set index to -1
		if(newPos == -1)
		{
			selectIndex_ = -1;
			newPos = caretPos;
		}
		return newPos;
	}
	private int getPrevSelectIndexByIndex(boolean showAll, int startIndex,
		VttObj vttObj)
	{
		int newPos = -1;
		Tags tags = vttObj.getVttDocument().getTags();
		// go through the markups from prev pos
		int size = markups_.size();
		for(int i = 0; i < size; i++)
		{
			int ii = (startIndex - i + size)%size;	// for recycle
			Markup markup = markups_.elementAt(ii);
			String tagNameCategory = markup.getTagNameCategory();
			Tag tag = tags.getTagByNameCategory(tagNameCategory);
			if (((showAll == true) || (tag.isDisplay() == true))
					&& (!"SnippetColumn".equals(markup.getTagName())))
			{
				selectIndex_ = ii;
				// set end position for return
				newPos = markup.getOffset() + markup.getLength();
				break;
			}
		}
		// if no prev selected index found, set index to -1
		if(newPos == -1)
		{
			selectIndex_ = -1;	// no selection
			newPos = vttObj.getMainFrame().getMainPanel().getTextPane().getCaretPosition();
		}
		return newPos;
	}
	// final variables
	/** selection variables: no select */
	public final static int NO_SELECT = -1;
	/** markup action: add a markup */
	public final static int ADD = 0;	
	/** markup action: change a markup, position or tag */
	public final static int CHANGE = 1;	
	/** markup action: delete a markup */
	public final static int DELETE = 2;	
	/** markup action: join a selected markup and the next */
	public final static int JOIN = 3;	
	/** markup action: override selected markups/next */
	public final static int OVERRIDE = 4;	
	/** Vtt fixed format definiton: max field size for offset */
	public final static int MAX_OFFSET_SIZE = 7;
	/** Vtt fixed format definiton: max field size for length */
	public final static int MAX_LENGTH_SIZE = 2;
	/** Vtt fixed format definiton: max field size for tag name */
	public final static int MAX_TAG_NAME_SIZE = 15;
	/** Vtt fixed format definiton: max field size for tag category */
	public final static int MAX_TAG_CATEGORY_SIZE = 15;
	// data members
	private int selectIndex_ = NO_SELECT;		// -1: no Markup is selected
	private static MarkupComparator<Markup> mc_ 
		= new MarkupComparator<Markup>();
	private Vector<Markup> markups_ = new Vector<Markup>();	// list of markups
}
