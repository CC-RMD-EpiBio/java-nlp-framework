package gov.nih.nlm.nls.vtt.model;
import javax.swing.text.*;
import java.io.*;
/*****************************************************************************
* This is the Undo Java object class. Undo is the element in the undos in the
* UndoManager. It includes action and markup.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class UndoBase
{
	// public constructors
	/**
	* Create a Undo base Java object with default values.
	*/
    public UndoBase()
    {
    }
	/**
	* Create a Undo base Java object by specifying action, current markup,
	* and previous markup.
	*
	* @param action markup action
	* @param cur current markup
	* @param prev previous markup
	*/
	public UndoBase(int action, Markup cur, Markup prev)
	{
		action_ = action;
		cur_ = cur;
		prev_ = prev;
	}
	// public methods
	/**
	* Set action.
	* 
	* @param action markup action
	*/
	public void setAction(int action)
	{
		action_ = action;
	}
	/**
	* Set current markup.
	* 
	* @param cur current markup
	*/
	public void setCur(Markup cur)
	{
		cur_ = cur;
	}
	/**
	* Set previous markup.
	* 
	* @param prev previous markup
	*/
	public void setPrev(Markup prev)
	{
		prev_ = prev;
	}
	/**
	* Get action.
	* 
	* @return markup action
	*/
	public int getAction()
	{
		return action_;
	}
	/**
	* Get current/new markup.
	*
	* @return current markup
	*/
	public Markup getCur()
	{
		return cur_;
	}
	/**
	* Get previous/old markup
	*
	* @return previous markup
	*/
	public Markup getPrev()
	{
		return prev_;
	}
	/**
	* Convert undo object to String representation.
	*
	* @return the string representation of undo object
	*/
	public String toString()
	{
		// convert Undo object to format of
		// action|cur|prev: offset|length|tagName|TagText|Annotation
		StringBuffer out = new StringBuffer();
		out.append(Markups.getActionStr(action_));
		out.append(": prev|");
		out.append(prev_.toString());
		out.append("; cur|");
		out.append(cur_.toString());
		return out.toString();
	}
	// data members
	private int action_ = Markups.ADD;	// basic actions: ADD, DELETE, CHANGE
	private Markup cur_ = new Markup();	// crurent markup for the undo
	private Markup prev_ = new Markup();	// prev markup for the undo
}
