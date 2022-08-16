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
