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
