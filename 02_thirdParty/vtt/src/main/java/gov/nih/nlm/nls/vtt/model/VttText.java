package gov.nih.nlm.nls.vtt.model;
import java.util.*;
import java.io.*;
/*****************************************************************************
* This class provides methods to format the original text to vtt format.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class VttText 
{
	// make it private so that no one can instantiate it
    private VttText()
    {
    }
	/**
	* Write Vtt text to an output file.
	*
	* @param out buffered writer to be written to
	* @param text Vtt text
	*/
	public static void writeTextToFile(BufferedWriter out, String text)
		throws IOException
	{
		String textStr = toString(text);
		out.write(textStr);
	}
	/**
	* Convert text to string representation by adding text banner to text.
	*
	* @param text Vtt text
	*
	* @return String representation of Vtt text
	*/
	public static String toString(String text)
	{
		StringBuffer out = new StringBuffer();
		out.append(VttDocument.SEPARATOR_STR);
		out.append(GlobalVars.LS_STR);
		out.append(VttDocument.TEXT_STR);
		out.append(GlobalVars.LS_STR);
		out.append(VttDocument.SEPARATOR_STR);
		out.append(GlobalVars.LS_STR);
		out.append(text);
		return out.toString();
	}
}
