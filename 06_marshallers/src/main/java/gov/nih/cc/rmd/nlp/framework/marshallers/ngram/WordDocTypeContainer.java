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
