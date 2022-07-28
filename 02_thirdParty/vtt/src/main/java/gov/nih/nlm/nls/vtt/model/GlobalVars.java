package gov.nih.nlm.nls.vtt.model;
/*****************************************************************************
* This class defines global variables used in Visual Tagging Tool.
* 
* <p><b>History:</b>
* <ul>
* <li> SCR-92, clu, 01-27-10, add YEAR_STR for VTT year release
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class GlobalVars 
{
	// make it private so that no one can instantiate it
    private GlobalVars()
    {
    }
	// public methods
	// private methods
	// public define varaiable
	/** line separator string for PC and Linux, no carriage return is used */
	final public static String LS_STR = "\n";
		//= System.getProperty("line.separator").toString();    
	/** field separator string */	
	final public static String FS_STR = "|";	
	/** field separator character */	
	final public static char FS_char = '|';	
	/** space string */	
	final public static String SPACE_STR = " ";	
	/** VTT year string */	
	public final static String YEAR_STR = "2013";	// updated annually
	/** VTT version string */	
	public final static String VERSION_STR = "Visual Tagging Tool, " + YEAR_STR;
	/** VTT jar string */	
	public final static String VTT_JAR_FILE = "vtt-" + YEAR_STR + ".jar";
	/** VTT file format version string */	
	public final static String VTT_FILE_VERSION = "VTT.2010.0";
	/** VTT verbose type: to a GUi display and standard io */
	final public static int VERBOSE_ALL = 0;
	/** VTT verbose type: to the standard io */
	final public static int VERBOSE_STD_IO = 1;
	/** VTT verbose type: to a GUi display */
	final public static int VERBOSE_GUI = 2;
}
