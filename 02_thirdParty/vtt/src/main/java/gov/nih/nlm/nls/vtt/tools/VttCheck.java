package gov.nih.nlm.nls.vtt.tools;
import java.io.*;
import java.util.*;

import gov.nih.nlm.nls.vtt.api.*;
/*****************************************************************************
* This class is the top level class for VTT file format check application. 
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class VttCheck 
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
        else
        {
            System.err.println("Usage: java vttCheck <inFile>");
			exitFlag = true;
        }
		// init Vtt Api 
		if(exitFlag == false)
		{
			/**
			boolean verbose = true;
			VttApi.Init(inFileStr, verbose);
			**/
			VttCheckApi.readFromFile(new File(inFileStr));
		}
    }
}
