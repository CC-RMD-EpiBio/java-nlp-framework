package gov.nih.nlm.nls.vtt.gui;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.util.*;
import java.lang.*;
import java.io.*;

import gov.nih.nlm.nls.vtt.guiControl.*;
import gov.nih.nlm.nls.vtt.guiLib.*;
import gov.nih.nlm.nls.vtt.libs.*;
import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This class is the GUI dialog for comparing VTT Tags difference feature.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class TagsDiffDialog extends DiffDialog
{
	// public constructor 
	/**
    * Create a TagsDiffDialog object to compare tags from source and target by
    * specifying owner, title, typeStr, sourceDataStr, targetDataStr,
    * sourceFile, targetFile, vttObj.
    *
    * @param owner  the owner of this DiffDialog
    * @param title  the title of this DiffDialog
    * @param typeStr  the type of comparison, such as "Tag"
    * @param sourceDataStr  the source data in String format
    * @param targetDataStr  the target data in String format
    * @param sourceFile  the absolute path of source file
    * @param targetFile  the absolute path of target file
    * @param vttObj  the vttObj Java object
    */
	public TagsDiffDialog(Window owner, String title, String typeStr,
		String sourceDataStr, String targetDataStr, File sourceFile, 
		File targetFile, VttObj vttObj)
	{
		super(owner, title, typeStr, sourceDataStr, targetDataStr, 
			sourceFile, targetFile, vttObj);
	}
	// protected methods
	// Override methods for customerized panel
	/**
    * Create a view difference panel. This method is override for 
	* customerized panel.  
    *
    * @param sourceDataStr  the source data in String format
    * @param targetDataStr  the target data in String format
    * @param diffStats  the DiffStats object of statistics on difference
    * @param vttObj  the vttObj Java object
    */
	protected JPanel createViewDiffPanel(String sourceDataStr,
		String targetDataStr, DiffStats diffStats, VttObj vttObj)
	{
		JPanel viewP = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		viewP.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 3, 3, 3);    // external padding
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBag.setWeight(gbc, 100, 0);
		JPanel sourceViewP_ = createViewTagsPanel("Tags in Source File",
			sourceDataStr); 
		JPanel targetViewP_ = createViewTagsPanel("Tags in Target File",
			targetDataStr); 
		// Source & Target Data
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		viewP.add(sourceViewP_, gbc);
		GridBag.setPosSize(gbc, 1, 0, 1, 1);
		viewP.add(targetViewP_, gbc);
		return viewP;
	}
	// private methods
	private JPanel createViewTagsPanel(String title, String tagsStr)
	{
		JPanel textP = new JPanel();
		textP.setMinimumSize(new Dimension(450, 450));
		// top panel: Title
		JPanel titleP = new JPanel();
		titleP.add(new JLabel(title));
		// text content
		JTextPane tp = new JTextPane();
		DefaultStyledDocument doc =
			(DefaultStyledDocument) tp.getStyledDocument();
		// get tags and thr display str
		Vector<Tag> tags = getTagsFromStr(tagsStr);
		String demoStr = new String();
		Vector<String> demoStrs = new Vector<String>();
		for(int i = 0; i < tags.size(); i++)
		{
			Tag tag = tags.elementAt(i);
			String curStr = "Tag[" + i + "]: " + tag.getNameCategory()
				+ GlobalVars.LS_STR;
			demoStr += curStr;	
			demoStrs.addElement(curStr);
		}
		try
		{
			// init doc
			SimpleAttributeSet sas = new SimpleAttributeSet();
			doc.remove(0, doc.getLength());
			doc.insertString(doc.getLength(), demoStr, sas);
			// markup the doc
			int lineOffset = 0;
			for(int i = 0; i < tags.size(); i++)
			{
				Tag tag = tags.elementAt(i);
				int offset = demoStrs.elementAt(i).indexOf("]: ") + 3
					+ lineOffset;
				Markup.doMarkUp(tag, doc, offset, 
					tag.getNameCategory().length(), 12, sas, true);
				lineOffset += demoStrs.elementAt(i).length();	
			}
		}
		catch (BadLocationException e)
		{
			System.err.println(
				"** Err@TagsDiffDialog.CreateViewTagsPanel(): " + e.toString());
			final String fMsg = e.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		tp.setMargin(new Insets(5,5,5,5));
        tp.setCaretPosition(0);
        tp.setEditable(false);
        JScrollPane contentP = new JScrollPane(tp);
        contentP.setPreferredSize(new Dimension(430, 430));
        // add Panel
        textP.setLayout(new BorderLayout());
        textP.add(titleP, "North");
        textP.add(contentP, "Center");
        return textP;
	}
	private Vector<Tag> getTagsFromStr(String tagsStr)
	{
		Vector<Tag> tags = new Vector<Tag>();
		String line = null;
		int lineNo = 0;
		try
		{
			BufferedReader in = new BufferedReader(new StringReader(tagsStr));
			while((line = in.readLine()) != null)
			{
				// skip the line if it is empty or comments (#)
				if((line.length() > 0) && (line.charAt(0) != '#'))
				{
					Tag tag = Tags.readTagFromLine(line);
					tags.addElement(tag);
				}
				lineNo++;
			}
		}
		catch(Exception e)
		{
			System.err.println("** Err@TagsDiffDialog.GetTagsFromStr("
				+ lineNo + "): " + line);
			System.err.println("Exception: " + e.toString());
			final String fMsg = e.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, fMsg, "Exception", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		return tags;
	}
	// data members
	private final static long serialVersionUID = 5L;
	// local control vars:
}
