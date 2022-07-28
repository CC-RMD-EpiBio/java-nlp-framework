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
public class MetaData 
{
	// public constructor
	/**
	* Create a MetaData Java object with default values.
	*/
    public MetaData()
    {
    }

	// public methods
	/**
	* Get default tags file.
	*
	* @return the path of default tags file
	*/
	public String getTagsFile()
	{
		return tagsFile_;
	}

	/**
	* Get the flag of confirmation.
	*
	* @return the boolean value of confirmation flag
	*/
	public boolean getConfirmation()
	{
		return confirmation_;
	}

	/**
	* Set default tags file.
	*
	* @param tagsFile path of default tags file
	*/
	public void setTagsFile(String tagsFile)
	{
		tagsFile_ = tagsFile;
	}

	/**
	* Set the flag of confirmation.
	*
	* @param confirmation the boolean value of confirmation flag
	*/
	public void setConfirmation(boolean confirmation)
	{
		confirmation_ = confirmation;
	}

	/**
	* Get SaveInfos
	*
	* @return the save infos
	*/
	public Vector<SaveInfo> getSaveInfos()
	{
		return saveInfos_;
	}

	/**
    * Read Meta data from a line with verbose options
    *
    * @param line a line represents meta data
    */
    public void readMetaDataFromLine(String line)
    {
		readMetaDataFromLine(line, GlobalVars.VERBOSE_ALL);
	}

	/**
    * Read Meta data from a line with verbose options
    *
    * @param line a line represents meta data
	* @param verboseType verbose types: VERVOSE_GUI and VERBOSE_STD_IO
    */
    public void readMetaDataFromLine(String line, int verboseType)
    {
		StringTokenizer buf = new StringTokenizer(line, GlobalVars.FS_STR);

		String metaDataType = buf.nextToken();
		if(metaDataType.equals(TAGS_FILE_STR) == true)
		{
			confirmation_ = new Boolean(buf.nextToken()).booleanValue();
			tagsFile_ = buf.nextToken();
		}
		else if(metaDataType.equals(FILE_SAVE_STR) == true)
		{
			String vttFileVersion = buf.nextToken();
			String userName = buf.nextToken();
			String dateTime = buf.nextToken();

			SaveInfo saveInfo = new SaveInfo(vttFileVersion, userName, 
				dateTime);
			saveInfos_.addElement(saveInfo);	
		}
		else
		{
			System.err.println("** Warning@MetaData.ReadMetaDataFromLine( ): "
				+ "Illegal meta data type: [" + metaDataType + "]");
		}
	}

	/**
	* Add SaveInfo object to saveInfos_.
	*
	* @param saveInfo saveInfo to be added to meta data
	*/
	public void addSaveInfo(SaveInfo saveInfo)
	{
		saveInfos_.addElement(saveInfo); 
	}

	/**
	* Reset data member to init values.
	*/
	public void reset()
	{
		tagsFile_ = new String();
		confirmation_ = true;
		saveInfos_ = new Vector<SaveInfo>();
	}

	/**
	* Convert file history to string representation: header + file history
	*
	* @param headerFlag the boolean flag of showing header
	*
	* @return the string representation
	*/
	public String getFileHistoryStr(boolean headerFlag)
	{
		StringBuffer out = new StringBuffer();
		// header
		if(headerFlag == true)
		{
			out.append(VttDocument.SEPARATOR_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.META_DATA_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.META_FILE_HISTORY_FIELD_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.SEPARATOR_STR);
			out.append(GlobalVars.LS_STR);
		}

		// save info history
		for(int i = 0; i < saveInfos_.size(); i++)
		{
			SaveInfo saveInfo = saveInfos_.elementAt(i);
			out.append(saveInfo.toString());
			out.append(GlobalVars.LS_STR);
		}
		return out.toString();
	}

	/**
	* Convert meta data to string representation: header + Meta-Data
	*
	* @param headerFlag the boolean flag of showing header
	*
	* @return the string representation
	*/
	public String toString(boolean headerFlag)
	{
		StringBuffer out = new StringBuffer();
		// header
		if(headerFlag == true)
		{
			out.append(VttDocument.SEPARATOR_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.META_DATA_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.META_TAGS_FILE_FIELD_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.META_FILE_HISTORY_FIELD_STR);
			out.append(GlobalVars.LS_STR);
			out.append(VttDocument.SEPARATOR_STR);
			out.append(GlobalVars.LS_STR);
		}

		// tags file
		if((tagsFile_ != null) && (tagsFile_.length() > 0))
		{
			out.append(TAGS_FILE_STR + GlobalVars.FS_STR + confirmation_
				+ GlobalVars.FS_STR + tagsFile_);
			out.append(GlobalVars.LS_STR);
		}

		// save info history
		for(int i = 0; i < saveInfos_.size(); i++)
		{
			SaveInfo saveInfo = saveInfos_.elementAt(i);
			out.append(saveInfo.toString());
			out.append(GlobalVars.LS_STR);
		}
		return out.toString();
	}

	// data members
	public static final String TAGS_FILE_STR = "TAGS_FILE";
	public static final String FILE_SAVE_STR = "FILE_SAVE";

	private String tagsFile_ = new String();	// Vtt tag file path
	private boolean confirmation_ = true;	// loading tags file confirmation
	private Vector<SaveInfo> saveInfos_ = new Vector<SaveInfo>();// save infos
}
