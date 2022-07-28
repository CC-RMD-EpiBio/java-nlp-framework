package gov.nih.nlm.nls.vtt.model;
/*****************************************************************************
* This is the Str Java object class to provide operations on String. 
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class Str 
{
	// private constructor so no one can instantiate it
	private Str()
    {
    }
	// public methods
	/**
	* Align the input string. Align the string to a fixed size with option to
	* align to left or right.
	*
	* @param alignSize the fix size to be align
	* @param alignFlag align left (ALIGN_LEFT) or right (ALIGN_RIGHT)
	*/
	public static String align(String inStr, int alignSize, int alignFlag)
	{
		StringBuffer alignStr = new StringBuffer(inStr);
		int diffSize = alignSize - inStr.length();
		// align the inStr if the size is smaller
		if(diffSize > 0)
		{
			switch(alignFlag)
			{
				case ALIGN_LEFT:
					for(int i = 0; i < diffSize; i++)
					{
						alignStr.append(" ");
					}
					break;
				case ALIGN_RIGHT:
					for(int i = 0; i < diffSize; i++)
					{
						alignStr.insert(0, " ");
					}
					break;
			}
		}
		return alignStr.toString();
	}
	// data members
	/** align left */
	public final static int ALIGN_LEFT = 0;
	/** align right */
	public final static int ALIGN_RIGHT = 1;
}
