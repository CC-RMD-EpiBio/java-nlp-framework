package gov.nih.nlm.nls.vtt.model;
import java.awt.*;
import java.util.*;
/*****************************************************************************
* This is the Save Information Java object class. 
* It includes VTT file format version, user name, and time stamp. 
* 
* <p><b>History:</b>
* <ul>
* <li>SCR-98, chlu, 02-02-10, add who/when/file format to VTT file.
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class SaveInfo 
{
	// public constructor
	/**
	* Create a SaveInfo Java object with default values.
	*/
    public SaveInfo()
    {
    }
	/**
	* Create a SaveInfo Java object by specifying a tag.
	*
	* @param saveInfo a saveInfo to be instantiated
	*/
    public SaveInfo(SaveInfo saveInfo)
    {
		setSaveInfo(saveInfo);
    }
	/**
	* Create a SaveInfo Java object by specifying vtt file version, user name, 
	* and data time stamp.
	*
	* @param vttFileVersion VTT file format version
	* @param userName user name 
	* @param dateTime date time stamp
	*/
    public SaveInfo(String vttFileVersion, String userName, String dateTime)
    {
		vttFileVersion_ = vttFileVersion;
		userName_ = userName;
		dateTime_ = dateTime;
	}
	// public methods
	/**
	* Set the VTT file format version
	*
	* @param vttFileVersion VTT file format version
	*/
	public void setVttFileVersion(String vttFileVersion)
	{
		vttFileVersion_ = vttFileVersion;
	}
	/**
	* Set the user name
	*
	* @param userName user name 
	*/
	public void setUserName(String userName)
	{
		userName_ = userName;
	}
	/**
	* Set the date time stamp
	*
	* @param dateTime date time stamp
	*/
	public void setDateTime(String dateTime)
	{
		dateTime_ = dateTime;
	}
	/**
	* Get the vtt file version
	*
	* @return the background color of text
	*/
	public String getVttFileVersion()
	{
		return vttFileVersion_;
	}
	/**
	* Get the user name
	*
	* @return the user name
	*/
	public String getUserName()
	{
		return userName_;
	}
	/**
	* Get the Date time stamp
	*
	* @return the date time stamp
	*/
	public String getDateTime()
	{
		return dateTime_;
	}
	/**
	* Set saveInfo to a specified saveInfo
	*
	* @param saveInfo  the specified saveInfo
	*/
	public void setSaveInfo(SaveInfo saveInfo)
	{
		vttFileVersion_ = saveInfo.getVttFileVersion();
		userName_ = saveInfo.getUserName();
		dateTime_ = saveInfo.getDateTime();
	}
	/**
	* Convert SaveInfo to a string representation:
	* SAVE_HISTORY|vtt file version|user name|time stamp
	*
	* @return string representation of a SaveInfo
	*/
	public String toString()
	{
		String outStr = MetaData.FILE_SAVE_STR + GlobalVars.FS_STR 
			+ vttFileVersion_ + GlobalVars.FS_STR  
			+ userName_ + GlobalVars.FS_STR  
			+ dateTime_; 
		
		return outStr;
	}
	/**
	* Convert a specified SaveInfo to a string representation:
	* SAVE_HISTORY|vtt file version|user name|time stamp
	*
	* @return string representation of a specified Save Info
	*/
	public static String toString(SaveInfo saveInfo)
	{
		StringBuffer out = new StringBuffer();
		out.append(MetaData.FILE_SAVE_STR);
		out.append(GlobalVars.FS_STR);
		out.append(saveInfo.getVttFileVersion());
		out.append(GlobalVars.FS_STR);
		out.append(saveInfo.getUserName());
		out.append(GlobalVars.FS_STR);
		out.append(saveInfo.getDateTime());
		
		return out.toString();
	}
	// data members
	private String vttFileVersion_ = new String();	// Vtt file version
	private String userName_ = new String();	// user name
	private String dateTime_ = new String();	// date, time stamp
}
