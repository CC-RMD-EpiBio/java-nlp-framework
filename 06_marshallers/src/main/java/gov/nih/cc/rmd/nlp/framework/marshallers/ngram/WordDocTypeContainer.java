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
/**
 * 
 */
package gov.nih.cc.rmd.nlp.framework.marshallers.ngram;

import java.util.HashMap;

/**
 * @author VHASLCDivitG
 *
 */
public final class WordDocTypeContainer {

	private int[] totalDocTypeFreqs = null;
    private HashMap<String, int[]> docTypes = null;         // [0] = termfreq    [1] = docFreq

	/**
	 * Constructor
	 */
	public WordDocTypeContainer() {
		this.docTypes = new HashMap<String,int[]>();
		this.totalDocTypeFreqs = new int[2];
		
		this.totalDocTypeFreqs[0] = 0;
		this.totalDocTypeFreqs[1] = 0;
	}
	
	
	/**
	 * @return the docTypes
	 */
	public final HashMap<String, int[]> getDocTypes() {
		return docTypes;
	}


	/**
	 * getWordDocTypeFreqs
	 * @param docType
	 */
	public final int[] getWordDocTypeFreqs(String pDocType) {
		int [] docTypesFreq = this.docTypes.get( pDocType);
		if ( docTypesFreq == null) {
			docTypesFreq = new int[2];
			docTypesFreq[0] = 0;
			docTypesFreq[1] = 0;
			
			this.docTypes.put(pDocType, docTypesFreq);
		}
		return docTypesFreq;
	}


	
	public final void setDocTypeFreq( String pDocType, int[] pDocTypeFreqs) {
		this.docTypes.put( pDocType, pDocTypeFreqs );
		
	}

	public int[] getDocTypesFreqs() {
		
		return totalDocTypeFreqs;
	}


	
} // End Class WordDocTypeContainer
