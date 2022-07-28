package gov.nih.nlm.nls.vtt.operations;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import gov.nih.nlm.nls.vtt.guiLib.*;
import gov.nih.nlm.nls.vtt.model.*;
/*****************************************************************************
* This class provides methods for help Documents related operations.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class HelpDocOperations
{
	// private construct
	private HelpDocOperations()
	{
	}
	/**
    * Show help documents.
    *
    * @param startpage  the initial starting page
    * @param vttObj  the vttObj Java object
    */
	public static void showHelpDoc(int startpage, VttObj vttObj)
	{
		String vttDir = new File(vttObj.getConfigObj().getVttDir()).getPath();
		// init starting url
		String docUrl = "file:" + vttDir + "/docs/userDoc/tutorial/index.html";
		switch(startpage)
		{
			case HOME_PAGE:
				break;
			case TEXT_PAGE:
				docUrl = "file:" + vttDir + "/docs/userDoc/tutorial/text.html";
				break;
			case TAGS_PAGE:
				docUrl = "file:" + vttDir + "/docs/userDoc/tutorial/tag.html";
				break;
			case MARKUPS_PAGE:
				docUrl = "file:" + vttDir + "/docs/userDoc/tutorial/markup.html";
				break;
			case KEYS_PAGE:
				docUrl = "file:" + vttDir + "/docs/userDoc/tutorial/keys.html";
				break;
		}
		
		// init helpDoc
		if(vttObj.getHelpDoc() == null)
		{
			String title = "Visual Tagging Tool Documents";
			String homeUrl = "file:" + vttDir + "/docs/userDoc/index.html";
			vttObj.setHelpDoc(new DocHtmlBrowser(null, title, 650, 100, 600, 
				800, homeUrl, docUrl));
			vttObj.getHelpDoc().setModal(false);
		}
		else
		{
			vttObj.getHelpDoc().setPage(docUrl);
		}
		vttObj.getHelpDoc().setVisible(true);
	}
	// final variables
	public final static int HOME_PAGE = 0;
	public final static int TEXT_PAGE = 1;
	public final static int TAGS_PAGE = 2;
	public final static int MARKUPS_PAGE = 3;
	public final static int KEYS_PAGE = 4;
}
