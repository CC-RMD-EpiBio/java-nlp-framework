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
package gov.nih.cc.rmd.nlp.framework.marshallers.ngram;

import java.util.HashMap;
import java.util.List;

/**
 * WordMetaDataContainer is a container to hold
 * 
 *   docFreq
 *   termFreq
 *   Sections MetaData   section|docFreq|termFreq   
 *   
 * @author VHASLCDivitG
 *
 */
public class WordMetaDataContainer {

	private String word = null;
	private int docFreq = 0;
	private int termFreq = 0;
	private HashMap<String, SectionMetaDataContainer> sectionFreqs = null;
	
	
	
	/** 
	 * Constructor 

	 */
	public WordMetaDataContainer( String pWord ) {
		this.setWord(pWord);
		this.docFreq = 0;
		this.termFreq = 0;
		this.sectionFreqs = new HashMap<String, SectionMetaDataContainer>();
	} // end Constructor () --------------------
	
	
	/**
	 * @return the docFreq
	 */
	public final int getDocFreq() {
		return docFreq;
	}
	/**
	 * @param docFreq the docFreq to set
	 */
	public final void setDocFreq(int docFreq) {
		this.docFreq = docFreq;
	}
	/**
	 * @return the termFreq
	 */
	public final int getTermFreq() {
		return termFreq;
	}
	/**
	 * @param termFreq the termFreq to set
	 */
	public final void setTermFreq(int termFreq) {
		this.termFreq = termFreq;
	}
	/**
	 * @return the sectionFreqs
	 */
	public final HashMap<String, SectionMetaDataContainer> getSectionFreqs() {
		return sectionFreqs;
	}
	/**
	 * @param sectionFreqs the sectionFreqs to set
	 */
	public final void setSectionFreqs(HashMap<String,SectionMetaDataContainer> sectionFreqs) {
		this.sectionFreqs = sectionFreqs;
	}


	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}


	/**
	 * @param word the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}
	
	
} // End Class WordMetaData 
