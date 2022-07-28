package gov.nih.nlm.nls.vtt.operations;
import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter.Highlight;

import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.util.UnderlineHighlighter;
/*****************************************************************************
* This class provides methods for GUI search related operations.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class SearchOperations
{
	
	// private constructor
	private SearchOperations()
	{
	}
	// public static methods

	/**
	* Reset search high light.
    *
    * @param vttObj  the vttObj Java object
    */
	public static void resetSearchHighlight(VttObj vttObj)
	{
		// reset selection to current inserting position (end of selection)
		// so the style change will take effect right away
		//vttObj.GetMainFrame().GetMainPanel().SetCaretPosition(0);
		int caret = vttObj.getMainFrame().getMainPanel().getCaretPosition();
		vttObj.getMainFrame().getMainPanel().getTextPane().setSelectionStart(
			caret);
		vttObj.getMainFrame().getMainPanel().getTextPane().setSelectionEnd(
			caret);
	}
	/**
	* Clear search high light.
    *
    * @param vttObj  the vttObj Java object
    */
	public static void clearSearchHighlight(VttObj vttObj)
	{
		// reset selection to current inserting position (end of selection)
		// so the style change will take effect right away
		vttObj.getMainFrame().getMainPanel().getTextPane().getHighlighter().removeAllHighlights();
	}
	/**
    * Set search high light
    *
    * @param vttObj  the vttObj Java object
    * @param wordToHighlight	the word to highlight
    */
	public static void setSearchHighlight(VttObj vttObj, String wordToHighlight, boolean matchCase)
	{
		String content = null;
		
		 try {
			 Document d = vttObj.getMainFrame().getMainPanel().getTextPane().getDocument();
		     content = d.getText(0, d.getLength());
		   
		 } catch (BadLocationException e) {
		   // Cannot happen
		 }
		 
		 if(wordToHighlight.contains("|")){
			String[] wordsToHighlight = wordToHighlight.split("\\|");
			for(int i=0; i<wordsToHighlight.length; i++){
			   if(matchCase == false)
				{
				   wordsToHighlight[i] = wordsToHighlight[i].toLowerCase();
				   if ( content != null )
				     content = content.toLowerCase();
				}
			   String pipedregex = wordsToHighlight[i];
			   Pattern pipedpatten = Pattern.compile(pipedregex);
			   Matcher pipedmatcher = pipedpatten.matcher(content);
			   while (pipedmatcher.find()) {
			     try {
						vttObj.getMainFrame().getMainPanel().getTextPane().getHighlighter().addHighlight(pipedmatcher.start(), pipedmatcher.end(), new UnderlineHighlighter.UnderlineHighlightPainter(Color.red));
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			   }
			}	
		 }else{
			 if(matchCase == false)
			 	{
				 	wordToHighlight = wordToHighlight.toLowerCase();
				 	if ( content != null )
				 	  content = content.toLowerCase();
			 	}
			 String regex =  wordToHighlight;
			 Pattern p = Pattern.compile(regex);
			 Matcher matcher = p.matcher(content);
			 while (matcher.find()) {
				 try {
					vttObj.getMainFrame().getMainPanel().getTextPane().getHighlighter().addHighlight(matcher.start(),
							matcher.end(), new UnderlineHighlighter.UnderlineHighlightPainter(Color.red));
				 } catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		   } 
		}
	
		
}
