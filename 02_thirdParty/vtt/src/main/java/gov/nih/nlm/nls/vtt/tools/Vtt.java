package gov.nih.nlm.nls.vtt.tools;
import java.io.*;
import java.util.*;

import gov.nih.nlm.nls.vtt.api.*;
/*****************************************************************************
* This class is the top level class for VTT application. 
* It calls VttApi to load VTT.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/			
public class Vtt 
{
	/**
    * This class is the top level class for VTT application.
    *
    * @param args  arguments (input VTT file)
    */
    public static void main(String[] args) 
    {
		boolean exitFlag = false;
		String inFileStr = new String();
        // read in configuration
        if(args.length == 1)
		{
			// update File
			inFileStr = args[0];
		}
        else if(args.length > 0)
        {
            System.err.println("Usage: java vtt <inFile>");
			exitFlag = true;
        }
		// init Vtt Api 
		if(exitFlag == false)
		{
			VttApi.init(inFileStr, true);
		}
    }
}
