package gov.nih.cc.rmd.nlp.framework.marshallers.ngram;


/**
 * SectionMetaDataContainer is a container to hold
 * 
 *   SectionName
 *   SectionDocFreq
 *   SectionTermFreq
 *  
 * @author VHASLCDivitG
 *
 */
public class SectionMetaDataContainer {

	/**
	 * Constructor
	 * @param pSectionName
	 */
	public SectionMetaDataContainer(String pSectionName) {
		this.name = pSectionName;
		this.docFreq = 0;
		this.termFreq = 0;
	}
	
	
	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}
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
	String name = null;
	int docFreq = 0;
	int termFreq = 0;
} // end Class SectionMetaDataContainer
