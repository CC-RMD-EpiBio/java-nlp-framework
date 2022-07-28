package gov.nih.nlm.nls.vtt.model;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.awt.*;
import java.util.jar.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.guiControl.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This class defines Visual Tagging Tool API.
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
public class VttObj extends VttGui
{
	// public constructor
	/**
	* Create a VttObj Java object with default values.
	*/
    public VttObj()
    {
		this(null, 50, 50);
    }
	/**
	* Create a VttObj Java object by specifying input file.
	*
	* @param inFile input file
	*/
    public VttObj(File inFile)
    {
		this(inFile, 50, 50);
    }
	/**
	* Create a VttObj Java object by specifying initial position of Vtt.
	*
	* @param x x position
	* @param y y position
	*/
    public VttObj(int x, int y)
    {
		this(null, x, y);
	}
	/**
	* Create a VttObj Java object by specifying input file and initial 
	* position of Vtt.
	*
	* @param inFile input file
	* @param x x position
	* @param y y position
	*/
    public VttObj(File inFile, int x, int y)
	{
		this(inFile, null, x, y);
	}
	/**
	* Create a VttObj Java object by specifying input file, configuration file,
	* and initial position of Vtt.
	*
	* @param inFile input file
	* @param configFile configuration file
	* @param x x position
	* @param y y position
	*/
    public VttObj(File inFile, String configFile, int x, int y) {
      this( inFile, configFile, x, y, true );
    }
    
    /**
     * Create a VttObj Java object by specifying input file, configuration file,
     * and initial position of Vtt.
     *
     * @param inFile input file
     * @param configFile configuration file
     * @param x x position
     * @param y y position
     * @param initializeGui (true by default)
     */
       public VttObj(File inFile, String configFile, int x, int y, boolean initializeGui)
    
    {
		init(this, configFile);
		// must be after model_ and control_ are init 
		if ( initializeGui ) {
		MainFrame mainFrame = new MainFrame(this, x, y);
		initGui(this, mainFrame);
	  }
		// load inFile to mainFrame.mainPanel
		FileOperations.loadFileToVtt(null, null, inFile, this, new Markups());
    }
	// public methods
	/**
	* Get Vtt version information.
	*
	* @return Vtt version information
	*/
	public String getVersionInfo()
	{
		return versionInfo_;
	}
	/**
	* Get Vtt configuration object.
	*
	* @return Vtt configuration object
	*/
	public ConfigObj getConfigObj()
	{
		return configObj_;
	}
	/**
	* Get Vtt document file.
	*
	* @return Vtt document file
	*/
	public File getDocFile()
	{
		return docFile_;
	}
	/**
	* Get Vtt document.
	*
	* @return Vtt document
	*/
	public VttDocument getVttDocument()
	{
		return vttDocument_;
	}
	/**
	* Set Vtt document file.
	*
	* @param docFile Vtt document file
	*/
	public void setDocFile(File docFile)
	{
		docFile_ = docFile;
	}
	// private method
	private void init(VttObj vttObj)
	{
		init(vttObj, null);
	}
	private void init(VttObj vttObj, String configFile)
	{
		// get config file from environment variable
		boolean useClassPath = false;
		if(configFile == null)
		{
			useClassPath = true;
			configFile = "data.config.vtt";
			configFile = "vtt.properties";
		}
		// readm data from configuration file
		Configuration conf = new Configuration(configFile, useClassPath);
		configObj_ = new ConfigObj(conf);
		// init
		File tagFile = configObj_.getTagFile();
		if (!tagFile.exists()) {
			try {
				// System.err.println("Tag File " + configObj_.getTagFile() + " does not exist, loading default tag file.");
				tagFile = File.createTempFile("tag", ".dat");
				tagFile.deleteOnExit();
				InputStream is = getClass().getClassLoader().getResourceAsStream("tags/tags.data");
				Scanner scan = new Scanner(is);
				scan.useDelimiter("\\Z");
				PrintWriter tagfileWriter = new PrintWriter(tagFile);
				tagfileWriter.write(scan.next());
				scan.close();
				is.close();
				tagfileWriter.close();
			} catch (Exception e) {
				System.err.println("Failed to open default tag file.");
			}
		}
		vttDocument_ = new VttDocument(tagFile);
		// version build information
		try
		{
/* - this is how guy did it ...
            JarFile jarFile = new JarFile(findJarPath(GlobalVars.VTT_JAR_FILE));
			Manifest manifest = jarFile.getManifest();
			String buildDate = manifest.getMainAttributes().getValue("Implementation-TIME");
*/
			Enumeration<java.net.URL> manifestURLEnum = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
			if (manifestURLEnum.hasMoreElements()) {
				java.net.URL url = manifestURLEnum.nextElement();
				InputStream is = null;
				try {
					is = url.openStream();
					Manifest manifest = new Manifest(is);
					String buildDate = manifest.getMainAttributes().getValue("Implementation-TIME");
					versionInfo_ = GlobalVars.VERSION_STR 
							+ GlobalVars.LS_STR + "Build: " + buildDate;
				} finally {
					if (is != null) {
						is.close();
					}
				}
				
			}
		}
		catch (Exception e)
		{
			System.err.println("** Err@Model.Init( ) " + e.toString());
			final String fMsg = e.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Vtt Input File Error (Markup)", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
	private static String findJarPath(String jarFileName)
	{
		String jarFilePath = jarFileName;
		
		
		// ------------------------------------------------------------
		// The class loader is not necessarily the system class loader
		// get the thread's class loader first
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL jarFileURL = cl.getResource(jarFileName);
		if ( jarFileURL != null )
		  jarFilePath = jarFileURL.getPath();
		
		/*
		String classpath = System.getProperty("java.class.path");
		// tokenize all path
		Vector<String> classpathList = new Vector<String>();
		StringTokenizer buf = new StringTokenizer(classpath, ":;");
		while(buf.hasMoreTokens() == true)
		{
			String temp = buf.nextToken();
			classpathList.add(temp);
		}
		// find the first classpath
		for(int i = 0; i < classpathList.size(); i++)
		{
			String cur = classpathList.elementAt(i);
			if((cur.endsWith(jarFileName) == true)
			&& ((new File(cur).exists()) == true))
			{
				jarFilePath = cur;
				break;
			}
		}
		*/
		return jarFilePath;
	}
	// Model
	private String versionInfo_ = GlobalVars.VERSION_STR;
	private ConfigObj configObj_ = null;
	private File docFile_ = null;	// document target file 
	private VttDocument vttDocument_ = null;
}
