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
* This class is the GUI dialog for comparing VTT Markups difference feature.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class MarkupsDiffDialog extends DiffDialog
{
	// public constructor
	/**
    * Create a MarkupsDiffDialog object to compare markups from source and 
	* target by specifying owner, title, typeStr, sourceDataStr, targetDataStr,
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
	public MarkupsDiffDialog(Window owner, String title, String typeStr,
		String sourceDataStr, String targetDataStr, File sourceFile, 
		File targetFile, VttObj vttObj)
	{
		super(owner, title, typeStr, sourceDataStr, targetDataStr, 
			sourceFile, targetFile, vttObj);
	}
	// protected methods
	/**
    * Create a view difference panel. This method is override for
    * customerized panel.
    *
    * @param sourceDataStr  the source data in String format
    * @param targetDataStr  the target data in String format
    * @param diffStats  the DiffStats object of statistics on difference
    * @param vttObj  the vttObj Java object
	*
	* @return view the difference panel
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
		// get the differences in markups 
		Vector<Integer> srcChangeLines = diffStats.getSrcChangeLines();
		Vector<Integer> srcDeleteLines = diffStats.getSrcDeleteLines();
		Vector<Integer> tarChangeLines = diffStats.getTarChangeLines();
		Vector<Integer> tarAddLines = diffStats.getTarAddLines();
		Vector<Markup> srcMarkups = getMarkupsFromStr(sourceDataStr);
		Vector<Markup> tarMarkups = getMarkupsFromStr(targetDataStr);
		Vector<Markup> srcChangeMarkups 
			= getMarkupsByLines(srcMarkups, srcChangeLines); 
		Vector<Markup> tarChangeMarkups 
			= getMarkupsByLines(tarMarkups, tarChangeLines); 
		// update change line to add/delete lines in markups
		updateChangeDeleteAddLines(srcChangeLines, srcDeleteLines, 
			srcMarkups, tarChangeMarkups);
		updateChangeDeleteAddLines(tarChangeLines, tarAddLines, 
			tarMarkups, srcChangeMarkups);
		// get source and target panels
		String text = vttObj.getVttDocument().getText();
		sourceScrollPane_ = new JScrollPane();
		targetScrollPane_ = new JScrollPane();
		JPanel sourceViewP = createViewMarkupsPanel("Markups in Source File",
			text, sourceDataStr, srcChangeLines, srcDeleteLines,
			sourceScrollPane_); 
		JPanel targetViewP = createViewMarkupsPanel("Markups in Target File",
			text, targetDataStr, tarChangeLines, tarAddLines,
			targetScrollPane_); 
		JScrollBar sourceScrollBar = sourceScrollPane_.getVerticalScrollBar();	
		JScrollBar targetScrollBar = targetScrollPane_.getVerticalScrollBar();	
		// synchronize the scroller bar
		MarkupsDiffDialogControl sourceListener
			= new MarkupsDiffDialogControl(sourceScrollBar.getModel());
		MarkupsDiffDialogControl targetListener
			= new MarkupsDiffDialogControl(targetScrollBar.getModel());
		sourceScrollBar.getModel().addChangeListener(targetListener);	
		targetScrollBar.getModel().addChangeListener(sourceListener);	
		// Source & Target Data
		GridBag.setPosSize(gbc, 0, 0, 1, 1);
		viewP.add(sourceViewP, gbc);
		GridBag.setPosSize(gbc, 1, 0, 1, 1);
		viewP.add(targetViewP, gbc);
		return viewP;
	}
	// private methods
	private static Vector<Markup> getMarkupsByLines(Vector<Markup> inMarkups,
		Vector<Integer> lines)
	{
		Vector<Markup> outMarkups = new Vector<Markup>();
		for(int i = 0; i < lines.size(); i++)
		{
			int lineIndex = lines.elementAt(i) - 1;  // index from 1 to 0
			Markup markup = inMarkups.elementAt(lineIndex);
			outMarkups.addElement(markup);
		}
		return outMarkups;
	}
	private static void updateChangeDeleteAddLines(Vector<Integer> changeLines,
		Vector<Integer> addDeleteLines, Vector<Markup> fromMarkups, 
		Vector<Markup> toChangeMarkups)
	{
		// move the lines No. from changeLines to addDeleteLines 
		// if the start pos and length are not the same
		// check if the start/length is contains in the toLines ChangeLines
		for(int i = changeLines.size()-1; i >= 0; i--)
		{
			int lineNo = changeLines.elementAt(i); 
			int lineIndex = lineNo - 1; 
			Markup fromMarkup = fromMarkups.elementAt(lineIndex);
			if(isInMarkups(toChangeMarkups, fromMarkup) == false)
			{
				addDeleteLines.addElement(new Integer(lineNo));
				changeLines.removeElementAt(i);
			}
		}
	}
	// expensive, can be improved by providing the start index
	private static boolean isInMarkups(Vector<Markup> markups, Markup markup)
	{
		boolean inFlag = false;
		for(int i = 0; i < markups.size(); i++)
		{
			Markup curMarkup = markups.elementAt(i);
			if((curMarkup.getOffset() == markup.getOffset())
			&& (curMarkup.getLength() == markup.getLength()))
			{
				inFlag = true;
				break;
			}
		}
		return inFlag;
	}
	private JPanel createViewMarkupsPanel(String title, String contentStr,
		String makrupsStr, Vector<Integer> changeLines, 
		Vector<Integer> addDeleteLines, JScrollPane jScrollPane)
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
		// get markups
		Vector<Markup> markups = getMarkupsFromStr(makrupsStr);
		try
		{
			// insert text
			SimpleAttributeSet sas = new SimpleAttributeSet();
			doc.remove(0, doc.getLength());
			doc.insertString(doc.getLength(), contentStr, sas);
			// visual effects for change
			for(int i = 0; i < changeLines.size(); i++)
			{
				// change index from 1 to 0
				int lineIndex = changeLines.elementAt(i) - 1;	
				Markup markup = markups.elementAt(lineIndex);
				int startPos = markup.getOffset();
				int length = markup.getLength();
				Markup.doMarkUp(changeTag_, doc, startPos, length, 12, sas,
					true);
			}
			// visual effects for add/delete
			for(int i = 0; i < addDeleteLines.size(); i++)
			{
				// index from 1 to 0
				int lineIndex = addDeleteLines.elementAt(i) - 1;
				Markup markup = markups.elementAt(lineIndex);
				int startPos = markup.getOffset();
				int length = markup.getLength();
				Markup.doMarkUp(addDeleteTag_, doc, startPos, length, 12, sas,
					true);
			}
		}
		catch (BadLocationException e)
		{
			System.err.println(
				"** Err@MarkupsDiffDialog.CreateViewMarkupsPanel(): " 
				+ e.toString());
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
		jScrollPane.setViewportView(tp);
        jScrollPane.setPreferredSize(new Dimension(430, 430));
        // add Panel
        textP.setLayout(new BorderLayout());
        textP.add(titleP, "North");
        textP.add(jScrollPane, "Center");
        return textP;
	}
	private Vector<Markup> getMarkupsFromStr(String markupsStr)
	{
		Vector<Markup> markups = new Vector<Markup>();
		String line = null;
		int lineNo = 0;
		try
		{
			BufferedReader in 
				= new BufferedReader(new StringReader(markupsStr));
			while((line = in.readLine()) != null)
			{
				// skip the line if it is empty or comments (#)
				if((line.length() > 0) && (line.charAt(0) != '#'))
				{
					Markup markup = Markups.readMarkupFromLine(line);
					markups.addElement(markup);
				}
				lineNo++;
			}
		}
		catch(Exception e)
		{
			System.err.println("** Err@MarkupsDiffDialog.GetMarkupsFromStr("
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
		return markups;
	}
	// data members
	private final static long serialVersionUID = 5L;
	// local control vars:
    JScrollPane sourceScrollPane_ = null;
    JScrollPane targetScrollPane_ = null;
}
