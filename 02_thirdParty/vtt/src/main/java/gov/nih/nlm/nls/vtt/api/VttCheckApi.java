package gov.nih.nlm.nls.vtt.api;
import java.io.*;
import java.util.*;

import javax.swing.*;

import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This class is the VTT file format checker Java API class. 
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class VttCheckApi
{
	// private constructors
	private VttCheckApi()
	{
	}
	// public methods
	/**
	* Read text, tags, markups of Vtt Document from a file.
	* This method is similar to VttDocument.ReadFromFile() except it does not
	* create GUI VTT.
	*
	* @return true if the input file contains legal format of Vtt document data
	*/
	public static boolean readFromFile(File inFile)
	{
		System.out.println("================================================"); 
		System.out.println("-- Check VTT format for the follwing file:"); 
		System.out.println("[" + inFile + "]");
		System.out.println("================================================"); 
		// init tags, markups, etc...
		boolean readFlag = true;
		String line = null;
		int lineNo = 0;	// line number
		int mode = VttDocument.MODE_TEXT;	// set the default mode to text
		String text = new String();    // text
		Tags tags = new Tags();        // tags
		// read in data for text, tags, markups,
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(inFile), "UTF-8"));
			// read in line by line from a file
			while((line = in.readLine()) != null)
			{
				// update line no
				lineNo++;
				// update mode: continue if it is header
				if(VttDocument.isHeader(line) == true)
				{
					mode = VttDocument.getMode(line, mode);
					continue;
				}
				// update text, tags, markup
				switch(mode)
				{
					case VttDocument.MODE_TEXT:
						text = updateText(line, text);
						break;
					case VttDocument.MODE_TAG:
						readFlag = updateTags(line, tags);
						break;
					case VttDocument.MODE_MARKUP:
						int maxTextPos = text.length();
						readFlag = updateMarkup(line, tags, maxTextPos);
						break;
				}
				// stop reading more lines if erros in format of vtt file
				if(readFlag == false)
				{
					System.err.println("** VttCheck stops at line: " + lineNo);
					break;	// stop
				}
			}
			in.close();
		}
		catch(Exception e)
		{
			readFlag = false;
			System.out.println("RFF2 - Exception: " + e.toString());
		}
		// send OK messge
		if(readFlag == true)
		{
			System.out.println("-- VttCheck completed: No error found in this VTT file.");
		}
		return readFlag;
	}
	// private methods
	private static String updateText(String line, String text)
	{
		text += line + GlobalVars.LS_STR;
		return text;
	}
	private static boolean updateTags(String line, Tags tags)
	{
		boolean flag = true;
		// skip the line if it is empty or comments (#)
		if((line.length() > 0) && (line.charAt(0) != '#'))
		{
			Tag tag = Tags.readTagFromLine(line, GlobalVars.VERBOSE_STD_IO);
			if(tag != null)
			{
				tags.addTag(tag);
			}
			else
			{
				flag = false;
			}
		}
		return flag;
	}
	private static boolean updateMarkup(String line, Tags tags, int maxTextPos)
	{
		boolean flag = true;
		// skip the line if it is empty or comments (#)
		if((line.length() > 0) && (line.charAt(0) != '#'))
		{
			if(Markups.readMarkupFromLine(line, tags, maxTextPos,
				GlobalVars.VERBOSE_STD_IO) == null)
			{
				flag = false;
			}
		}
		return flag;
	}
	// data members
}
