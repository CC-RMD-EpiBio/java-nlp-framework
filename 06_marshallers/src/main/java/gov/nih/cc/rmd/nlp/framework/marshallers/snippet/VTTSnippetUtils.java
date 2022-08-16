// =================================================
/**
 * VTTSnippetUtils includes a reader to read in a vtt snippet file
 * into a snippet object with text and snippets with annotations
 * 
 * 
 * 
 * @author  Guy Divita 
 * @created Sept 14, 2017
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.snippet;


import java.io.File;

import gov.nih.cc.rmd.nlp.framework.utils.U;


public class VTTSnippetUtils  {


	// -----------------------------------------
	/**
	 * readVTTSnippetFile reads in a vtt snippets file (v3NLP Framework
	 * 
	 * @param pFileHandle
	 * @return SnippetsContainer
	 * @throws Exception
	 * 
	 */
	// -----------------------------------------
	public static SnippetsContainer readVTTSnippetFile(File pFileHandle) throws Exception {

		SnippetsContainer returnVal = null;
		try {

			returnVal = readVTTSnippetFile(pFileHandle.getAbsolutePath());

		} catch (Exception e) {
			throw e;
		}
		
		return returnVal;

	} // end Method readVTTSnippetFile() -----------------------

  
	// -----------------------------------------
		/**
		 * readVTTSnippetFile reads in a vtt snippets file (v3NLP Framework
		 * 
		 * @param pFileName
		 * @return SnippetsContainer
		 * @throws Exception
		 *
		 */
		// -----------------------------------------
public static SnippetsContainer readVTTSnippetFile(String pFileName) throws Exception {
	
  SnippetsContainer snippets = null;
	try {
		
		String buff[] = U.readFileIntoStringArray(pFileName);
		
		// --------------------------
		// read into the vtt sections
		VTTContainer vttContainer = VTTContainer.parseVTTFile( buff);
		vttContainer.setFileName( pFileName);
		
		// --------------------------
		// Parse the text and markups into snippets
	  snippets = SnippetsContainer.parse( vttContainer );
	 
		
	} catch (Exception e) {
		e.printStackTrace();
		System.err.println("Issue with reading in the vtt snippet file " + pFileName + " " + e.getMessage());
		throw e;
	}
	
	return snippets;
} // end Method readVTTSnippetFile() -----------------------


  // --------------------------
  // Global and Class variables
  // --------------------------
	
	
	
} // end Class VTTSnippetUtils() ----
