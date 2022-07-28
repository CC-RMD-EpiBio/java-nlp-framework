package gov.nih.nlm.nls.vtt.guiControl;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import gov.nih.nlm.nls.vtt.api.*;
import gov.nih.nlm.nls.vtt.gui.*;
import gov.nih.nlm.nls.vtt.model.*;
import gov.nih.nlm.nls.vtt.operations.*;
/*****************************************************************************
* This is the Java class of Keys controller for KeyAdapter.
* 
* <p><b>History:</b>
* <ul>
* <li>SCR-90, clu, 01-26-10, fix bugs of Mnemonic and regular key press
* <li>SCR-95, chlu, 02-04-10, one click to reload tags files
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class KeysControl extends KeyAdapter
{
	/**
	* Create a KeysControl object for key controller by 
	* specifying vttObj.
	*
	* @param vttObj  the vttObj Java object
	*/
	public KeysControl(VttObj vttObj)
	{
		vttObj_ = vttObj;
	}
	// public methods
	/**
	* Override methods for key press.
	*
	* @param evt an event indicateds a keystroke press occurred in a component. 
	*/
    public void keyPressed(KeyEvent evt)
    {
    }
	/**
	* Override methods for key release.
	*
	* @param evt an event indicateds a keystroke release occurred in a component
	*/
    public void keyReleased(KeyEvent evt)
    {
    }
	/**
	* Override methods for key types.
	*
	* @param evt an event indicateds a keystroke occurred in a component. 
	*/
    public void keyTyped(KeyEvent evt)
    {
		char keyChar = evt.getKeyChar();
		int keyInt = (int) keyChar;
		int keyCode = evt.getKeyCode();
		JFrame mainFrame = vttObj_.getMainFrame();
		//System.out.println("[" + keyChar + "]-[" + keyInt + "]"); 
		// upper case
		if(Character.isUpperCase(keyChar))
		{
		}
		// key
		if(keyChar == 'h')
		{
			if(evt.isAltDown() == false)
			{
				HelpDocOperations.showHelpDoc(HelpDocOperations.HOME_PAGE, 
					vttObj_);
			}
		}
		// tags
		else if(keyChar == 'q')	// quick loading tags file
		{
			TagOperations.quickTagsLoading(vttObj_);
		}
		// Markup
		else if(keyChar == ',')	// backward all Markup selection
		{
			MarkupOperations.backwardSelectMarkup(true, vttObj_);
		}
		else if(keyChar == '.')	// forward all Markup selection
		{
			MarkupOperations.forwardSelectMarkup(true, vttObj_);
		}
		else if(keyChar == '[')	// set selection to all markups home
		{
			MarkupOperations.setHomeSelectMarkup(true, vttObj_);
		}
		else if(keyChar == ']')	// set selection to all markups end
		{
			MarkupOperations.setEndSelectMarkup(true, vttObj_);
		}
		else if(keyChar == '<')	// backward dsplayable Markup selection
		{
			MarkupOperations.backwardSelectMarkup(false, vttObj_);
		}
		else if(keyChar == '>')	// forward dsplayable Markup selection
		{
			MarkupOperations.forwardSelectMarkup(false, vttObj_);
		}
		else if(keyChar == '{')	// set dsplayable selection to home
		{
			MarkupOperations.setHomeSelectMarkup(false, vttObj_);
		}
		else if(keyChar == '}')	// set dsplayable selection to end
		{
			MarkupOperations.setEndSelectMarkup(false, vttObj_);
		}
		else if(keyChar == 'j')	// join the selected markup to next markup
		{
			MarkupOperations.joinOperation(vttObj_);
		}
		else if(keyChar == 'm')	// markup dialog
		{
			if(evt.isAltDown() == false)
			{
				vttObj_.getMarkupDialog().toggleShow(vttObj_);
			}
		}
		else if(keyChar == 'u')	// undo markup
		{
			UndoOperations.undo(vttObj_);
		}
		else if(keyChar == 'r') // redo markup
		{
			UndoOperations.redo(vttObj_);
		}
		else if((keyInt == 8) 	// <- backspace: delete the selected Markup
		|| (keyInt == 127))		// Delete
		{
			// it is comment out because these two keys with default beep( )
			//MarkupOperations.Delete();
		}
		else if(keyChar == '0')	// delete markup
		{
			MarkupOperations.deleteOperation(vttObj_);
		}
		else if((keyChar == '1')
		|| (keyChar == '2')
		|| (keyChar == '3')
		|| (keyChar == '4')
		|| (keyChar == '5')
		|| (keyChar == '6')
		|| (keyChar == '7')
		|| (keyChar == '8')
		|| (keyChar == '9'))
		{
			// change from charInt to int, 49 is the base offset
			int keyIndex = keyInt - 49;		// start from 0
			int tagIndex = 
				vttObj_.getVttDocument().getTags().getQuickKeyMappingIndex(
				keyIndex); 
			// legal tag
			if(tagIndex != -1)
			{
				Tag tag 
				= vttObj_.getVttDocument().getTags().getTagByIndex(tagIndex);
				String tagName = tag.getName();
				String tagCategory = tag.getCategory();
				MarkupOperations.updateOperation(tagName, tagCategory, vttObj_);
				// update Modeless dialog
				MarkupOperations.updateModelessDialog(vttObj_);
			}
			else
			{
				Toolkit.getDefaultToolkit().beep();
			}
		}
		// system
		else if(keyChar == 'o') //open a file
		{
			if(evt.isAltDown() == false)
			{
				FileOperations.openOperation(mainFrame, vttObj_);
			}
		}
		else if(keyChar == 's') //save a file
		{
			FileOperations.saveOperation(mainFrame, vttObj_);
		}
		else if(keyChar == 'a') //save as a file
		{
			FileOperations.saveAsOperation(mainFrame, vttObj_);
		}
		else if(keyChar == 'p') //print a file
		{
			FileOperations.printOperation(vttObj_);
		}
		else if(keyChar == 'c') //close a file
		{
			FileOperations.closeOperation(mainFrame, vttObj_);
		}
		else if(keyChar == 'x') //exit a file
		{
			FileOperations.exitOperation(mainFrame, vttObj_);
		}
		else if(keyChar == 'f') // find option
		{
			vttObj_.getFindDialog().show(mainFrame, vttObj_);
		}
		else if(keyChar == 'z') // zoom option
		{
			vttObj_.getZoomDialog().show(mainFrame, vttObj_);
		}
		else if(keyChar == 't') // compare
		{
			if(evt.isAltDown() == false)	// to avoid nmenoic
			{
				System.out.println("--- alt --");
				CompareOperations.compareToFileOperation(mainFrame, vttObj_);
			}
		}
		else if(keyChar == 'v') // reserved
		{
			if(evt.isAltDown() == false)
			{
			}
		}
		else if(keyChar == 'g') // reserved
		{
			if(evt.isAltDown() == false)
			{
			}
		}
		// shift key
		if(evt.isShiftDown() == true)
		{
			//System.out.println("--- shift --");
		}
		// arrow keys
		if(keyCode == KeyEvent.VK_LEFT)
		{
			System.out.println("-- Get 'Left' Key");
		}
		else if(keyCode == KeyEvent.VK_RIGHT)
		{
			System.out.println("-- Get 'Right' Key");
		}
		else if(keyCode == KeyEvent.VK_UP)
		{
			System.out.println("-- Get 'Up' Key");
		}
		else if(keyCode == KeyEvent.VK_DOWN)
		{
			System.out.println("-- Get 'Down' Key");
		}
    }
	// private methods
	// data member
	private VttObj vttObj_ = null;
}
