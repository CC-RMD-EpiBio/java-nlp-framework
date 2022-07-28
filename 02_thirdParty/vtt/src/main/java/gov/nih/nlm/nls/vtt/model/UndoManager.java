package gov.nih.nlm.nls.vtt.model;
import java.io.*;
import java.util.*;
/*****************************************************************************
* This is the undo manager java class which manages the list of Undo Java 
* objects. This class is used directly in VTT for undo and redo operations.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class UndoManager 
{
	// public constructor
	/**
	* Create an undo manager Java object with default values.
	*/
    public UndoManager()
    {
    }
	// public methods
	/**
	* Add Undo node to undoNodes list.
	*
	* @param undo the undo node to be added
	*/
	public void addUndoNode(UndoNode undo)
	{
		// at the end of the undo list, including no undo in the list 
		if((index_ + 1) == undoNodes_.size()) 
		{
			// add current undo
			undoNodes_.addElement(undo);
			index_++;
		}
		else	// in the middle of undo list: delete the rest elements
		{
			// remove all elements after index
			int startIndex = index_ + 1;
			int endIndex = undoNodes_.size() - 1;
			removeUndoNodesFrom(startIndex);
			// add current undo
			undoNodes_.addElement(undo);
			index_++;
		}
	}
	// public methods
	/**
	* Get the index. The index is the current pointer on the undos list.
	*
	* @return The current index on the undos list
	*/
	public int getIndex()
	{
		return index_;
	}
	/**
	* Get the undo node of the specified index.
	*
	* @param index the index of interest
	*
	* @return undo node of the specified index from the undo nodes list
	*/
	public UndoNode getUndoNode(int index)
	{
		UndoNode undoNode = undoNodes_.elementAt(index);
		return undoNode;
	}
	/**
	* Get the size of the undo nodes list.
	*
	* @return the size of the undo nodes list
	*/
	public int getSize()
	{
		return undoNodes_.size();
	}
	/**
	* Get the undo nodes list.
	*
	* @return a vector of the undo nodes
	*/
	public Vector<UndoNode> getUndoNodes()
	{
		return undoNodes_;
	}
	/**
	* Check if undoable.
	*
	* @return a boolean flag of undoable for the indexed undo node
	*/
	public boolean isUndoable()
	{
		boolean undoable = true;
		// undoNodes_ is empty or index_ is -1: set to false
		if((undoNodes_.size() == 0)
		|| (index_ == -1))
		{
			undoable = false;
		}
		return undoable;
	}
	/**
	* Check if redoable.
	*
	* @return a boolean flag of redoable for the indexed undo node
	*/
	public boolean isRedoable()
	{
		boolean redoable = true;
		// undoNodes_ is empty or index_ = undos.size(): set to false
		if((undoNodes_.size() == 0)
		|| (index_ + 1 == undoNodes_.size()))
		{
			redoable = false;
		}
		return redoable;
	}
	/**
	* Increase index of current undo nodes list.
	*/
	public void increaseIndex()
	{
		index_++;
	}
	/**
	* Decrease index of current undo nodes list.
	*/
	public void reduceIndex()
	{
		index_--;
	}
	/**
	* Converts undos to String representation.
	*
	* @return the string representation of undo nodes list
	*/
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("----- Undo List, Size: " + undoNodes_.size() +
			", Index: " + index_ + " -----");
		buf.append(GlobalVars.LS_STR);
		for(int i = 0; i < undoNodes_.size(); i++)	
		{
			UndoNode undoNode = undoNodes_.elementAt(i);
			buf.append("[" + i + "]: " + undoNode.toString());
		}
		return buf.toString();
	}
	/**
	* Reset the undo manager.
	*/
	public void reset()
	{
		// reset undos and index
		undoNodes_.removeAllElements();
		index_ = -1;
	}
	// private methods
	// remove undoNode from the index to the end of the undoNodes_
	private void removeUndoNodesFrom(int index)
	{
		int length = undoNodes_.size();
		for(int i = length-1; i >= index; i--)
		{
			undoNodes_.removeElementAt(i);
		}
	}
	// data members
	private int index_ = -1;	// the index of the current/last undo object
	private Vector<UndoNode> undoNodes_ = new Vector<UndoNode>();	// list of undos
}
