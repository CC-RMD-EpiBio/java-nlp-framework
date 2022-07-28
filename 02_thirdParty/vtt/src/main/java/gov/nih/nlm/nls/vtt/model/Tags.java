package gov.nih.nlm.nls.vtt.model;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.awt.*;

import javax.swing.*;
/*****************************************************************************
* This is the collection of Tag Java objects class. It is used directly in VTT.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class Tags
{
	// public constructor
	/**
	* Create a tags list Java object with default values.
	*/
    public Tags()
    {
		clearQuickKeyMappings();
    }
	/**
	* Create a tags list Java object by specifying tags.
	*
	* @param tags  the tags to be instantiated for this tags list
	*/
	public Tags(Tags tags)
	{
		tags_ = new Vector<Tag>(tags.getTags());
		nameList_ = new Vector<TagFilter>(tags.getNameList());
		categoryList_ = new Vector<TagFilter>(tags.getCategoryList());
		quickKeyMappings_ = new Vector<String>(tags.getQuickKeyMappings());
	}
	/**
	* Create a tags list Java object from a input file.
	*
	* @param inFile  the input file with data of tags
	*/
    public Tags(String inFile)
    {
		clearQuickKeyMappings();
		loadTagsFromFile(inFile);
	}
	// public methods
	/**
	* Get the tags list
	*
	* @return  the tags list
	*/
	public Vector<Tag> getTags()
	{
		return tags_;
	}
	/**
	* Get the tag from tags list by specifying the index
	*
	* @param index the position of interest
	*
	* @return  the tag at the specified index
	*/
	public Tag getTagAt(int index)
	{
		Tag tag = null;
		if((index >= 0) && (index < tags_.size()))
		{
			tag = tags_.elementAt(index);
		}
		return tag;
	}
	/**
	* Set the tag at the specified position with specifying tag and update flag.
	*
	* @param index the position of interest
	* @param tag  tag to be set
	* @param updateFlag a boolean flag of update
	*/
	public void setTagAt(int index, Tag tag, boolean updateFlag)
	{
		String newName = tag.getName();
		String newCategory = tag.getCategory();
		// update indexed tag to new tag
		tags_.elementAt(index).setTag(tag);
		// update nameList and categoryList
		if(updateFlag == true)
		{
			// keep the same status for those same name and category
			Vector<String> nameCategoryList = getNameCategoryList(false);
			Vector<TagFilter> nameList = new Vector<TagFilter>();
			Vector<TagFilter> categoryList = new Vector<TagFilter>();
			for(int i = 0; i < nameCategoryList.size(); i++)
			{
				String nameCategory = nameCategoryList.elementAt(i);
				// add existing name filter to List
				String name = Tag.getName(nameCategory);
				TagFilter nameFilter 
					= TagFilter.getTagFilterByName(nameList_, name);
				if(nameFilter != null)
				{
					TagFilter.addToList(nameList, nameFilter);
				}
				// update existing category filter to List
				String category = Tag.getCategory(nameCategory);
				TagFilter categoryFilter 
					= TagFilter.getTagFilterByName(categoryList_, category);
				if(categoryFilter != null)
				{
					TagFilter.addToList(categoryList, categoryFilter);
				}
			}
			// Add filter if tag name and category is new
			TagFilter.addToList(nameList, newName);
			TagFilter.addToList(categoryList, newCategory);
			//update the list
			nameList_ = new Vector<TagFilter>(nameList);
			categoryList_ = new Vector<TagFilter>(categoryList);
		}
	}
	/**
	* Get names of tags in a format of Vector of String
	*
	* @return names of tags list
	*/
	public Vector<String> getTagNames()
	{
		Vector<String> names = new Vector<String>();
		for(int i = 0; i < tags_.size(); i++)
		{
			names.addElement(tags_.elementAt(i).getName());
		}
		return names;
	}
	/**
	* Get tag by specifying name and category
	*
	* @param nameCategory the name and category string of interest
	*
	* @return tag of the specified name and category from tags list
	*/
	public Tag getTagByNameCategory(String nameCategory)
	{
		for(int i = 0 ; i < tags_.size();i ++)
		{
			Tag tag = tags_.elementAt(i);
			if(tag.getNameCategory().equals(nameCategory) == true)
			{
				return tag;
			}
		}
		return null;
	}
	/**
	* Get tag by the specified index
	*
	* @param index the specified index of interest
	*
	* @return tag of the specified index from tags list
	*/
	public Tag getTagByIndex(int index)
	{
		Tag tag = null;
		// check tags_ and index
		if((tags_ != null)
		&& (tags_.size() > 0)
		&& (index >= 0)
		&& (index < tags_.size()))
		{
			tag = tags_.elementAt(index);
		}
		return tag;
	}
	/**
	* Get name list from tags list.
	*
	* @return name list from tags list.
	*/
	public Vector<TagFilter> getNameList()
	{
		return nameList_;
	}
	/**
	* Get category list from tags list.
	*
	* @return category list from tags list.
	*/
	public Vector<TagFilter> getCategoryList()
	{
		//Collections.sort(categoryList_);
		return categoryList_;
	}
	/**
	* Get name category list from tags list.
	*
	* @return name category list from tags list.
	*/
	public Vector<String> getNameCategoryList(boolean checkDisplay)
	{
		Vector<String> nameCategoryList = new Vector<String>();
		for(int i = 0; i < tags_.size(); i++)
		{
			Tag tag = tags_.elementAt(i);
			String nameCategory = tag.getNameCategory();
			if(!checkDisplay || tag.isDisplay()){
				nameCategoryList.addElement(nameCategory);
			}
		}
		return nameCategoryList;
	}
	/**
	* Get quick key mapping of tags.
	*
	* @return quick key mapping of tags
	*/
	public Vector<String> getQuickKeyMappings()
	{
		return quickKeyMappings_;
	}
	/**
	* Set quick key mapping of tags.
	*
	* @param quickKeyMappings quick key mapping of tags
	*/
	public void setQuickKeyMappings(Vector<String> quickKeyMappings)
	{
		quickKeyMappings_ = new Vector<String>(quickKeyMappings);
	}
	/**
	* Set quick key mapping of tags with specified name category at the 
	* specified index.
	*
	* @param tagNameCategory name category of tag to be set on quick key
	* @param index the specified index of quick key mapping
	*/
	public void setQuickKeyMappingAt(String tagNameCategory, int index)
	{
		if((quickKeyMappings_ != null)
		&& (index > -1)
		&& (index < quickKeyMappings_.size()))
		{
			quickKeyMappings_.setElementAt(tagNameCategory, index);
		}
	}
	/**
	* Get the index of tags at the specified quick key index (0 ~ 9)
	*
	* @param index the quick key index
	*
	* @return index of tags list for the specified quick key index
	*/
	public int getQuickKeyMappingIndex(int index)
	{
		int mapIndex = -1;
		if((index >= 0) && (index < 9))
		{
			for(int i = 0; i < tags_.size(); i++)
			{
				String tagNameCategory = tags_.elementAt(i).getNameCategory();
				if(quickKeyMappings_.elementAt(index).equals(tagNameCategory))
				{
					mapIndex = i;
					break;
				}
			}
		}
		return mapIndex;
	}
	/**
	* Add a tag to tags list.
	*
	* @param tag the tag to be added
	*/
	public void addTag(Tag tag)
	{
		// add tag object to tags list
		tags_.addElement(tag);	
		// add tag name, category, nameCategory to List
		TagFilter.addToList(nameList_, tag.getName());
		TagFilter.addToList(categoryList_, tag.getCategory());
	}
	/**
	* Delete a tag from tags list.
	*
	* @param index the index of tags list to be deleted
	*/
	public void deleteTag(int index)
	{
		Tag deletedTag = tags_.elementAt(index);
		// Update tags: remove tag object from tags list
		tags_.remove(index);
		// remove tag name from list if it does not exist
		if(containsNameInTags(deletedTag.getName()) == false)
		{
			TagFilter.deleteFromList(nameList_, deletedTag.getName());
		}
		if(containsCategoryInTags(deletedTag.getCategory()) == false)
		{
			TagFilter.deleteFromList(categoryList_, deletedTag.getCategory());
		}
	}
	/**
	* Sort Tags by name|category.
	*/
	public void sortTags()
	{
		// sort tags by name|category alphabetically
		Collections.sort(tags_, tc_);
	}
	/**
	* Get the index of tags list by the specifying name category
	*
	* @param nameCategory name and category of interest
	*
	* @return the index of tags list
	*/
	public int getIndexByTag(String nameCategory)
	{
		int index = -1;
		for(int i = 0; i < tags_.size(); i++)
		{
			Tag tag = tags_.elementAt(i);
			if(nameCategory.equals(tag.getNameCategory()) == true)
			{
				index = i;
				break;
			}
		}
		return index;
	}
	/**
	* Move up a speficied indexed tag on the tags list.
	*
	* @param index the index of tag to be moved up
	*/
	public void moveUp(int index)
	{
		int size = tags_.size();
		if((index > VttDocument.TAG_START_INDEX) && (index < size))
		{
			Tag tag = tags_.elementAt(index);
			tags_.removeElementAt(index);
			tags_.insertElementAt(tag, index-1);
		}
	}
	/**
	* Move down a specified indexed tag on the tags list.
	*
	* @param index the index of tag to be moved down
	*/
	public void moveDown(int index)
	{
		int size = tags_.size();
		if((index >= VttDocument.TAG_START_INDEX) && (index < size-1))
		{
			Tag tag = tags_.elementAt(index);
			tags_.removeElementAt(index);
			tags_.insertElementAt(tag, index+1);
		}
	}
	/**
	* Get the boolean flag of display after filter.
	*
	* @param tag the tag of interest
	*
	* @return a boolean flag of display after filter
	*/
	public boolean isDisplayAfterFilter(Tag tag)
	{
		int nameIndex = getNameIndex(tag.getName());
		int categoryIndex = getCategoryIndex(tag.getCategory());
		boolean display 
			= nameList_.elementAt(nameIndex).getStatus() 
			&& categoryList_.elementAt(categoryIndex).getStatus();
		return display;	
	}
	/**
	* Check if category list contains the specified name category.
	*
	* @param nameCategory the name and category of interest
	*
	* @return true if contains the specified name and category and vise versa
	*/
	public boolean containsNameCategory(String nameCategory)
	{
		boolean flag = false;
		Vector<String> nameCategoryList = getNameCategoryList(false);
		for(int i = 0; i < nameCategoryList.size(); i++)
		{
			if(nameCategoryList.elementAt(i).equals(nameCategory) == true)
			{
				flag = true;
				break;
			}
		}
		return flag;
	}
	/**
	* Set quick key mapping to default (same order as the order of tags list).
	*/
	public void setQuickKeyMappingsToDefault()
	{
		// set -1 to all 0 ~ 9 keys
		clearQuickKeyMappings();
		// update quick key map to the same order as in tags
		for(int i = 0; i < tags_.size(); i++)
		{
			// add default quick key mapping: 1 ~ 9
			if((i > 0) && (i < 10))
			{
				String nameCategory = tags_.elementAt(i).getNameCategory();
				quickKeyMappings_.setElementAt(nameCategory, (i-1));
			}
		}
	}
	// public static methods
	/**
	* Save tags to a specified file.
	*
	* @param outFile the output file to be saved
	* @param tags the tags list to be saved
	*/
	public static void saveTagsToFile(String outFile, Vector<Tag> tags)
	{
		try
		{
			BufferedWriter out =  new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outFile), "UTF-8"));
			writeTagsToFile(out, tags);
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
					JOptionPane.showMessageDialog(null, fMsg, "Vtt Input File Error (Markup)", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
	/**
	* Write tags to a buffered writer.
	*
	* @param out buffered writer to be write to
	* @param tags the tags list to be saved
	*/
	public static void writeTagsToFile(BufferedWriter out, Vector<Tag> tags) 
		throws IOException
	{
		String tagsStr = toString(tags);
		out.write(tagsStr);
	}
	/**
	* Convert tags to string representation of VTT format: header + tags
	*
	* @param tags the tags list of interest
	*
	* @return the string representation
	*/
	public static String toString(Vector<Tag> tags)
	{
		return toString(tags, true);
	}
	/**
	* Convert tags to string representation of VTT format: header + tags
	*
	* @param tags the tags list of interest
	* @param headerFlag the boolean flag of showing header
	*
	* @return the string representation
	*/
	public static String toString(Vector<Tag> tags, boolean headerFlag)
	{
		StringBuffer out = new StringBuffer();
		// header
		if(headerFlag == true)
		{
			out.append(VttDocument.SEPARATOR_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.TAG_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.TAG_FIELD_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.SEPARATOR_STR);
			out.append(GlobalVars.LS_STR);
		}
		// print the first element, clear Tag
		if((tags != null) && (tags.size() > 0))
		{
			Tag clearTag = tags.elementAt(VttDocument.RESERVED_TAG_INDEX);
			out.append(Tag.toString(clearTag));
			// Add a line separator after "Text/Clear" Tag
			if(headerFlag == true)
			{
				out.append(VttDocument.SEPARATOR_STR);
				out.append(GlobalVars.LS_STR);
			}
			// go through all Tags
			for(int i = VttDocument.TAG_START_INDEX; i < tags.size(); i++)
			{
				Tag tag = tags.elementAt(i);
				out.append(Tag.toString(tag));
			}
		}
		return out.toString();
	}
	/**
	* Load tags from a file
	*
	* @param inFile the input file of tags
	*/
	public void loadTagsFromFile(String inFile)
	{
		String line = null;
		int lineNo = 0;
		int tagNo = 0;
		// init tags_, nameList_, and categoryList_ vector
		tags_ = new Vector<Tag>();
		nameList_ = new Vector<TagFilter>(); 
		categoryList_ = new Vector<TagFilter>(); 
		try
		{
			//System.out.println("----- Read Tags data from: " + inFile);
		  BufferedReader in = null;
		  if ( inFile.startsWith("resources")) {
		   in = readerFromClassPathResource( inFile );
		    
		  } else {
		     in = new BufferedReader(new InputStreamReader( new FileInputStream(inFile), "UTF-8"));
		  }
			
			// read in line by line from a file
			while((line = in.readLine()) != null)
			{
				lineNo++;
				// skip the line if it is empty or comments (#)
				if((line.length() > 0) && (line.charAt(0) != '#'))
				{
					tagNo++;
					Tag tag = readTagFromLine(line);
					addTag(tag);	// also update nameList_ & categoryList_	
				}
			}
			// set quick key mapping to default
			setQuickKeyMappingsToDefault();
			in.close();
		}
		catch(Exception e)
		{
			System.err.println("** Err@Tags.LoadTagsFromFile(" + inFile 
				+ ") at line: " + lineNo);
			System.err.println(line);
			System.err.println("Exception: " + e.toString());
			final String fMsg = e.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Vtt Input File Error (Markup)", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
	/**
	* Read a tag information from a line with verbose options of both gui 
	* and stnadard system io.
	*
	* @param line a line represents a tag
	*
	* @return the tag of the line
	*/
	public static Tag readTagFromLine(String line)
	{
		return readTagFromLine(line, GlobalVars.VERBOSE_ALL);
	}
	/**
	* Read a tag information from a line with specifying verbose option.
	*
	* @param line a line represents a tag
	* @param verboseType verbose types: VERVOSE_GUI and VERBOSE_STD_IO
	*
	* @return the tag of the line
	*/
	public static Tag readTagFromLine(String line, int verboseType)
	{
		Tag tag = null;
		// use '|' as delimiter to parse token
		int index1 = line.indexOf(GlobalVars.FS_STR); // name
		int index2 = line.indexOf(GlobalVars.FS_STR, index1+1); // category
		// read first 2 fields on from inFile data
		String name = line.substring(0, index1);
		String category = line.substring(index1+1, index2);
		String line2 = line.substring(index2);
		StringTokenizer buf = new StringTokenizer(line2, GlobalVars.FS_STR);
		// read in data
		boolean bold = Boolean.valueOf(buf.nextToken());
		boolean italic = Boolean.valueOf(buf.nextToken());
		boolean underline = Boolean.valueOf(buf.nextToken());
		boolean display = Boolean.valueOf(buf.nextToken());
		try
		{
			int foregroundRed = Integer.parseInt(buf.nextToken());
			int foregroundGreen = Integer.parseInt(buf.nextToken());
			int foregroundBlue = Integer.parseInt(buf.nextToken());
			Color foregroundColor = new Color(foregroundRed,
				foregroundGreen, foregroundBlue);
			int backgroundRed = Integer.parseInt(buf.nextToken());
			int backgroundGreen = Integer.parseInt(buf.nextToken());
			int backgroundBlue = Integer.parseInt(buf.nextToken());
			Color backgroundColor = new Color(backgroundRed,
				backgroundGreen, backgroundBlue);
			String fontFamily = buf.nextToken();
			String fontSize = buf.nextToken();
			tag = new Tag(name, category, display, bold, italic, underline,
				foregroundColor, backgroundColor, fontFamily,
				fontSize);
		}
		catch(NumberFormatException nfe)
		{
			String errMsg = 
				"** Error: wrong tag data/format in the input file!\n" 
				+ VttDocument.SEPARATOR_STR + "\n"
				+ VttDocument.TAG_FIELD_STR + "\n"
				+ VttDocument.SEPARATOR_STR + "\n" + line;
			// send out error dialog
			switch(verboseType)
			{
				case GlobalVars.VERBOSE_STD_IO:
					System.err.println(errMsg);
					JOptionPane.showMessageDialog(null, nfe.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
					break;
				case GlobalVars.VERBOSE_GUI:
					JOptionPane.showMessageDialog(null, errMsg,
						"Vtt Input File Error (Tag)", 
						JOptionPane.ERROR_MESSAGE);
					break;
				case GlobalVars.VERBOSE_ALL:
				default:
					System.err.println(
						"** Err@Tags.ReadTagFromLine(): Illegal data/format for tag.");
					System.err.println(line); 
					System.err.println(nfe.toString()); 
					JOptionPane.showMessageDialog(null, errMsg,
						"Vtt Input File Error (Tag)", 
						JOptionPane.ERROR_MESSAGE);
					break;
			}
		}
		return tag;	
	}
	/**
	* Check if specified tags contain text tag, "Text/Clear".
	*
	* @param tags the tags list to be checked
	*
	* @return true if the specified tags list contains text tag
	*/
	public static boolean containsTextTag(Vector<Tag> tags)
	{
		boolean flag = false;
		// Check if tags contain "Text/Clear" tag
		if((tags != null)
		&& (tags.size() > 0)
		&& (tags.elementAt(VttDocument.RESERVED_TAG_INDEX).getName().equals(
			VttDocument.RESERVED_TAG_STR)))
		{
			flag = true;
		}
		return flag;
	}
	/**
	* Set name list.
	*
	* @param nameList the name list to be set
	*/
	public void setNameList(Vector<TagFilter> nameList)
	{
		nameList_ = new Vector<TagFilter>(nameList);
	}
	/**
	* Set category list.
	*
	* @param categoryList the category list to be set
	*/
	public void setCategoryList(Vector<TagFilter> categoryList)
	{
		categoryList_ = new Vector<TagFilter>(categoryList);
	}
	// private methods
	private boolean containsNameInTags(String name)
	{
		boolean flag = false;
		for(int i = 0; i < tags_.size(); i++)
		{
			Tag tag = tags_.elementAt(i);
			if(tag.getName().equals(name) == true)
			{
				flag = true;
				break;
			}
		}
		
		return flag;
	}
	private boolean containsCategoryInTags(String category)
	{
		boolean flag = false;
		for(int i = 0; i < tags_.size(); i++)
		{
			Tag tag = tags_.elementAt(i);
			if(tag.getCategory().equals(category) == true)
			{
				flag = true;
				break;
			}
		}
		
		return flag;
	}
	private int getNameIndex(String name)
	{
		int index = -1;
		for(int i = 0; i < nameList_.size(); i++)
		{
			if(nameList_.elementAt(i).getName().equals(name) == true)
			{
				index = i;
				break;
			}
		}
		return index;
	}
	private int getCategoryIndex(String category)
	{
		int index = -1;
		for(int i = 0; i < categoryList_.size(); i++)
		{
			if(categoryList_.elementAt(i).getName().equals(category) == true)
			{
				index = i;
				break;
			}
		}
		return index;
	}
	private void clearQuickKeyMappings()
	{
		quickKeyMappings_.removeAllElements();
		// set the default value: 0 ~ 9 to -1
		for(int i = 0; i < 9; i++)
		{
			quickKeyMappings_.addElement(new String());
		}
	}
	
	
//-----------------------------------------
/**
* readFile reads the content of a text file in the classpath into
* a string and returns it.  This method preserves
* the same kind of line delimiters as the original file.
*
*
* @param resourceName the full path to the resource. ie = gov/va/myFile.text
* @return String  the contents of the file.
* @exception
*/
//-----------------------------------------
 private static BufferedReader readerFromClassPathResource(String pFileName) throws Exception {


   InputStream stream = null;
   String fname = pFileName;
   try {
      fname = pFileName.replace('\\', '/');
     
     stream = Tags.class.getClassLoader().getResourceAsStream(fname);
   } catch (Exception e ) {
     throw new Exception ( "file : " + fname + " was not found as a resource " + e.toString());
   }
     
   if ( stream == null ) {
     throw new Exception ( "file : " + fname  + " was not found as a resource");
   }
     
   StringBuilder buff = new StringBuilder();
   BufferedReader  in = null;
    
   try {
     in = new BufferedReader ( new InputStreamReader(stream));
   } catch (Exception e) {
     e.printStackTrace();
     throw new Exception( "Something went wrong in readClassPathResource trying to open \n|" + pFileName + "|\n" + e.toString());
   }
   
    return in;
 
 } // end Method readClassPathResource() ---------------

	
	// data members
	private static TagComparator<Tag> tc_ = new TagComparator<Tag>();
	// list of unique tag: by name and category
	private Vector<Tag> tags_ = new Vector<Tag>();	
	// list of unique tag names
	private Vector<TagFilter> nameList_ = new Vector<TagFilter>(); 
	// list of unique categories
	private Vector<TagFilter> categoryList_ = new Vector<TagFilter>(); 
	// Display Filters
	// quick key mapping
	private Vector<String> quickKeyMappings_ = new Vector<String>();
}
