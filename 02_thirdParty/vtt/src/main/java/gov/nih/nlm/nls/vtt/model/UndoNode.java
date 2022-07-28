package gov.nih.nlm.nls.vtt.model;
import java.io.*;
import java.util.*;
/*****************************************************************************
* This is the undo node class. It includes a list of UndoBase of ADD, DELETE, 
* CHANGE.  This class is used directly in VTT for undo and redo managers.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class UndoNode
{
	// public constructor
	/**
	* Create an undo node Java object with default values.
	*/
    public UndoNode()
    {
    }
	/**
	* Create an undo node Java object by specifying action.
	* This is used for multiple undoNode base operations: join, override
	*
	* @param action  action on markup
	*/
    public UndoNode(int action)
    {
		action_ = action;
    }
	/**
	* Create an undo node Java object with default values.
	* This is used for one undoNode base operation: add, delete, change
	*
	* @param action  action on markup
	* @param cur  current markup
	* @param prev  previous markup
	*/
	public UndoNode(int action, Markup cur, Markup prev)
	{
		action_ = action;
		UndoBase undoBase = new UndoBase(action, cur, prev);
		addUndoBase(undoBase);
	}
	// public methods
	/**
	* Add undoBase to undo bases.
	*
	* @param undoBase undoBase to be added
	*/
	public void addUndoBase(UndoBase undoBase)
	{
		// add UndoBase to the end of the list
		undoBases_.addElement(undoBase);
	}
	/**
	* Get undoBase from undo bases list.
	*
	* @param index the index of interest
	*
	* @return undoBase of interest
	*/
	public UndoBase getUndoBase(int index)
	{
		UndoBase undoBase = undoBases_.elementAt(index);
		return undoBase;
	}
	/**
	* Get size of bases list.
	*
	* @return size of undoBases list
	*/
	public int getSize()
	{
		return undoBases_.size();
	}
	/**
	* Get the first undo base from bases list.
	*
	* @return the first undo base from undoBases list
	*/
	public UndoBase getFirstUndoBase()
	{
		return undoBases_.elementAt(0);
	}
	/**
	* Get the last undo base from bases list.
	*
	* @return the last undo base from undoBases list
	*/
	public UndoBase getLastUndoBase()
	{
		int index = getSize() - 1;
		return undoBases_.elementAt(index);
	}
	/**
	* Get the undo bases list.
	*
	* @return the undo base list
	*/
	public Vector<UndoBase> getUndoBases()
	{
		return undoBases_;
	}
	/**
	* Set the action.
	*
	* @param action the action to be set
	*/
	public void setAction(int action)
	{
		action_ = action;
	}
	/**
	* Converts undoNode to String representation.
	*
	* @return string representation of undo node
	*/
	public String toString()
	{
		StringBuffer out = new StringBuffer();
		// print out undo node
		out.append("Action: ");
		out.append(action_);
		out.append(" (");
		out.append(Markups.getActionStr(action_));
		out.append(")");
		out.append(GlobalVars.LS_STR);
		// print out undo bases
		for(int i = 0; i < undoBases_.size(); i++)	
		{
			UndoBase undoBase = undoBases_.elementAt(i);
			int ii = i + 1;
			out.append("   " + ii + ". " + undoBase.toString());
			out.append(GlobalVars.LS_STR);
		}
		return out.toString();
	}
	// data members
	private int action_ = Markups.ADD;	// ADD, DELETE, CHANGE, JOIN, OVERRIDE
	private Vector<UndoBase> undoBases_ = new Vector<UndoBase>();	// list of undos
}
