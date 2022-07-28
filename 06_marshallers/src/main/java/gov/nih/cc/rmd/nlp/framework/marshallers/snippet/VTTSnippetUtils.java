/*******************************************************************************
 * ---------------------------------------------------------------------------
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                        Epidemiology and Biostatistics Branch 
 *                                         2019
 *    
 *  This work is licensed under the Creative Commons Attribution 4.0 International License. 
 *  
 *  This license allows you to use, share and  adapt for any purpose, provided:
 *     Provide attribution to the creators of this work within your work.
 *     Indicate if changes were made to this work.
 *     No claim to merchantability, implied warranty, or liability can be made.
 *     
 *   When attributing this code, please make reference to
 *    [citation/url here] .  
 *    
 *     In the absence of a specific paper or url listed above, reference http://clinicalcenter.nih.gov/rmd/eb/nlp
 *  
 *  To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 ******************************************************************************/
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
