package gov.nih.nlm.nls.vtt.model;
import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
/*****************************************************************************
* This class provides a way of storing and retrieving configurations through a
* Configuration object. 
*
* <p> Currently, it contains variables of:
* VTT_DIR, VTT_FILE_DIR, TAGS_FILE, etc..
*
* <p><b>History:</b>
* <ul>
* <li>SCR-96, chlu, 02/01/10, add Username to VTT config file
* </ul>
*
* @author NLM NLS Development Team, clu
*
* @version    V-2010
****************************************************************************/
public class Configuration
{
    // public constructor
    /**
    * Create a Configuration object.  There are two ways of reading 
    * configuration files.  First, finding xxx.properties from Java classpath.
    * Second, finding file by the specified path. 
    *  
    * @param  fName  the path of the configuration file or base name when
    * using class path.
    * @param  useClassPath  a flag of finding configuration file from class path
    */
    public Configuration(String fName, boolean useClassPath)
    {
        setConfiguration(fName, useClassPath);
    }
    // public methods
    /**
    * Get a value from configuration by specifying the key.
    *
    * @param  key  key (name) of the configuration value to get
    *
    * @return  the value of the configuration item in a string format
    */
    public String getConfiguration(String key)
    {
        String out = config_.get(key);
        return out;
    }
    /**
    * Overwrite the values of configuration variables when they are specified 
	* in the properties.
    *
    * @param  properties  properties to be overwrite in the configuration
    */
    public void overwriteProperties(Hashtable<String, String> properties)
    {
        for(Enumeration<String> e = properties.keys(); e.hasMoreElements();)
        {
            String key = e.nextElement();
            String value = properties.get(key);
            config_.put(key, value);
        }
    }
    /**
    * Get system level information from configuration.  This includes
    * VTT_DIR, VTT_FILE_DIR, TAGS_FILE
    *
    * @return  the value of the configuration item in a string format
    */
    public String getInformation()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("VTT_DIR: [" + getConfiguration(VTT_DIR) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("VTT_FILE_DIR: [" + getConfiguration(VTT_FILE_DIR) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("TAGS_FILE: [" + getConfiguration(TAGS_FILE) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("USER_NAME: [" + getConfiguration(USER_NAME) + "]");
        return buffer.toString();
    }
    // private methods
    private void setConfiguration(String fName, boolean useClassPath)
    {
      Properties properties = null;
        try
        {
            // get config data from fName.properties in class path
            if(useClassPath == true)
            {
              
              // -------------------------------
              // Modified by Guy Divita to load the vtt.properties file from a jar file
              
              ClassLoader cl = Thread.currentThread().getContextClassLoader();
              InputStream stream =  cl.getResourceAsStream("resources/vinciNLPFramework/vtt/vtt.properties");
              if ( stream != null ) {
               
              
                configSrc_ = null;
                properties = new Properties();
                properties.load( stream);
                stream.close();
                }
             // configSrc_ = (PropertyResourceBundle) ResourceBundle.getBundle(fName);
            
            }
            else // get config data from fName (path) file
            {
                FileInputStream file = new FileInputStream(fName);
                configSrc_ = new PropertyResourceBundle(file);
                file.close();
            }
        }
        catch (Exception e)
        {
            System.err.println("** Configuration Error: " + e.getMessage());
            System.err.println(
                "** Error: problem of opening/reading config file: '" +
                fName + "'.");
            final String fMsg = e.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
				}
			});
        }
        // put properties from configSrc_ into config_
        
        if ( configSrc_ == null && properties!= null ) {
          Set<?> keys = properties.keySet();
          for( Object key: keys) 
          {
           
            String value = (String) properties.get(key);
            config_.put((String) key, value);
          }
          
        } else {
          for(Enumeration<String> e = configSrc_.getKeys(); e.hasMoreElements();)
          {
            String key = e.nextElement();
            String value = configSrc_.getString(key);
            config_.put(key, value);
          }
        }
        // reset TOP_DIR
        if(getConfiguration(VTT_DIR).equals(AUTO_MODE) == true)
        {
            File file = new File(System.getProperty("user.dir"));
            String curDir = file.getAbsolutePath()
                + System.getProperty("file.separator");
            config_.put(VTT_DIR, curDir);    
        }
    }
    // data member
	/** VTT Mode: for automatic find the configuration file from classpath */
    public final static String AUTO_MODE = "AUTO_MODE";
    /** key for the path of VTT directory defined in configuration file */
    public final static String VTT_DIR = "VTT_DIR";
    /** key for the path of VTT files locate in conf file */
    public final static String VTT_FILE_DIR = "VTT_FILE_DIR";
    /** key for the path of default tags file defined in conf file */
    public final static String TAGS_FILE = "TAGS_FILE";
    /** key for the username defined in conf file */
    public final static String USER_NAME = "USER_NAME";
    /** key for the path of display length of file path in title */
    public final static String FILEPATH_DISPLAY_LENGTH = "FILEPATH_DISPLAY_LENGTH";
    /** key for the font size */
    public final static String BASE_FONT_SIZE = "BASE_FONT_SIZE";
    /** key for the zoom factor */
    public final static String ZOOM_FACTOR = "ZOOM_FACTOR";
    /** key for the readable format */
    public final static String VTT_FORMAT = "VTT_FORMAT";
    /** key for the markup offset fix length */
    public final static String MARKUP_OFFSET_SIZE = "MARKUP_OFFSET_SIZE";
    /** key for the markup length fix length */
    public final static String MARKUP_LENGTH_SIZE = "MARKUP_LENGTH_SIZE";
    /** key for the markup tag name fix length */
    public final static String MARKUP_TAG_NAME_SIZE = "MARKUP_TAG_NAME_SIZE";
    /** key for the markup tag category fix length */
    public final static String MARKUP_TAG_CATEGORY_SIZE = "MARKUP_TAG_CATEGORY_SIZE";
    /** key for the overwrite select markup style: bold */
    public final static String OVERWRITE_SELECT_BOLD 
		= "OVERWRITE_SELECT_BOLD";
    /** key for the overwrite select markup style: italic */
    public final static String OVERWRITE_SELECT_ITALIC 
		= "OVERWRITE_SELECT_ITALIC";
    /** key for the overwrite select markup style: underline */
    public final static String OVERWRITE_SELECT_UNDERLINE 
		= "OVERWRITE_SELECT_UNDERLINE";
    /** key for the overwrite select markup style: font */
    public final static String OVERWRITE_SELECT_FONT 
		= "OVERWRITE_SELECT_FONT";
    /** key for the overwrite select markup style: SIZE */
    public final static String OVERWRITE_SELECT_SIZE 
		= "OVERWRITE_SELECT_SIZE";
    /** key for the overwrite select markup style: text color */
    public final static String OVERWRITE_SELECT_TEXT_COLOR 
		= "OVERWRITE_SELECT_TEXT_COLOR";
    /** key for the overwrite select markup style: background color */
    public final static String OVERWRITE_SELECT_BACKGROUND_COLOR 
		= "OVERWRITE_SELECT_BACKGROUND_COLOR";
    /** key for use non-display tag instead of text tag in select markup */
    public final static String USE_TEXT_TAG_FOR_NOT_DISPLAY_SELECT_MARKUP 
		= "USE_TEXT_TAG_FOR_NOT_DISPLAY_SELECT_MARKUP";
    /** key for the select markup style: bold */
    public final static String SELECT_BOLD = "SELECT_BOLD";
    /** key for the select markup style: italic */
    public final static String SELECT_ITALIC = "SELECT_ITALIC";
    /** key for the select markup style: underline */
    public final static String SELECT_UNDERLINE = "SELECT_UNDERLINE";
    /** key for the select markup style: font */
    public final static String SELECT_FONT = "SELECT_FONT";
    /** key for the select markup style: size */
    public final static String SELECT_SIZE = "SELECT_SIZE";
    /** key for the select markup style: text color: red */
    public final static String SELECT_TEXT_COLOR_R = "SELECT_TEXT_COLOR_R";
    /** key for the select markup style: text color: green */
    public final static String SELECT_TEXT_COLOR_G = "SELECT_TEXT_COLOR_G";
    /** key for the select markup style: text color: blue */
    public final static String SELECT_TEXT_COLOR_B = "SELECT_TEXT_COLOR_B";
    /** key for the select markup style: background color: red */
    public final static String SELECT_BACKGROUND_COLOR_R 
		= "SELECT_BACKGROUND_COLOR_R";
    /** key for the select markup style: background color: green */
    public final static String SELECT_BACKGROUND_COLOR_G 
		= "SELECT_BACKGROUND_COLOR_G";
    /** key for the select markup style: background color: blue */
    public final static String SELECT_BACKGROUND_COLOR_B 
		= "SELECT_BACKGROUND_COLOR_B";
    // private data member
    private PropertyResourceBundle configSrc_ = null;
    private Hashtable<String, String> config_ =
        new Hashtable<String, String>();    // the real config vars
}
