package gov.nih.nlm.nls.vtt.model;
import java.util.*;
/*****************************************************************************
* This is the Vtt Format Java class. There are four formats:
* <ul>
* <li>SIMPLE_FORMAT: simple format without alignment on fields
* <li>READABLE_FORMAT: align field with automatical cauculated length
* <li>FIX_LENGTH_FORMAT: align field with fixed length
* <li>SIMPLEST_FORMAT: simple format without header and annotation
* </ul>
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class VttFormat 
{
	// public constructor
	/**
	* preivate constructor, create a VttFormat Java object with default values.
	*/
    private VttFormat()
    {
    }
	/**
	* Get Vtt format from vtt format String.
	*
	* @param vttFormatStr vtt format string
	*
	* @return integer value of Vtt format
	*/
	public static int getVttFormat(String vttFormatStr)
	{
		int vttFormat = SIMPLE_FORMAT;
		for(int i = 0; i < vttFormatList_.size(); i++)
		{
			if(vttFormatStr.equals(vttFormatList_.elementAt(i)))
			{
				vttFormat = i;
				break;
			}
		}
		return vttFormat;
	}
	/**
	* Get Vtt Format String from Vtt format
	*
	* @param vttFormat integer value of vtt format
	*
	* @return Vtt format string
	*/
	public static String getVttFormatStr(int vttFormat)
	{
		String vttFormatStr = SIMPLE_FORMAT_STR;
		if((vttFormat >= SIMPLE_FORMAT) && (vttFormat < vttFormatList_.size()))
		{
			vttFormatStr = vttFormatList_.elementAt(vttFormat);
		}
		return vttFormatStr;
	}
	// data members
	/** simple format: no alignment on fields */
	public static final int SIMPLE_FORMAT = 0;
	/** readable format: align with auto length */
	public static final int READABLE_FORMAT = 1;
	/** fix length format: align with fixed length */
	public static final int FIX_LENGTH_FORMAT = 2;
	/** simplest format: no header, no annotation */
	public static final int SIMPLEST_FORMAT = 3;
	/** definition for simple format string */
	public static final String SIMPLE_FORMAT_STR = "SIMPLE_FORMAT";
	/** definition for readable format string */
	public static final String READABLE_FORMAT_STR = "READABLE_FORMAT";
	/** definition for fix length format string */
	public static final String FIX_LENGTH_FORMAT_STR = "FIX_LENGTH_FORMAT";
	/** definition for simplest format string */
	public static final String SIMPLEST_FORMAT_STR = "SIMPLEST_FORMAT";
	private static Vector<String> vttFormatList_ = new Vector<String>();
	static
	{
		vttFormatList_.add(SIMPLE_FORMAT_STR);
		vttFormatList_.add(READABLE_FORMAT_STR);
		vttFormatList_.add(FIX_LENGTH_FORMAT_STR);
		vttFormatList_.add(SIMPLEST_FORMAT_STR);
	}
}
