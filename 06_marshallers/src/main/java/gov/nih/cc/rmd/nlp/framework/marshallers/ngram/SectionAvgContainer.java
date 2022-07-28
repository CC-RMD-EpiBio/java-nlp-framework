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

/**
 * @author VHASLCDivitG
 *
 */
public class SectionAvgContainer {

	private String name = "";
	private int totalNumberOfSectionWords = 0;
	private int totalNumberOfSectionSentences = 0;
	private int totalNumberOfSectionInstances = 0;
	private double avgNumberOfSectionWords = 0.0;
	private double avgNumberOfSectionSentences = 0.0;

	/**
	 * 
	 */
	public SectionAvgContainer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor
	 * @param pSectionName
	 */
	public SectionAvgContainer( String pSectionName ) {
		this.name = pSectionName;
		
	} // End Constructor() --------

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
	 * @return the totalNumberOfSectionWords
	 */
	public final int getTotalNumberOfSectionWords() {
		return totalNumberOfSectionWords;
	}

	/**
	 * @param totalNumberOfSectionWords the totalNumberOfSectionWords to set
	 */
	public final void setTotalNumberOfSectionWords(int totalNumberOfSectionWords) {
		this.totalNumberOfSectionWords = totalNumberOfSectionWords;
	}

	/**
	 * @return the totalNumberOfSectionSentences
	 */
	public final int getTotalNumberOfSectionSentences() {
		return totalNumberOfSectionSentences;
	}

	/**
	 * @param totalNumberOfSectionSentences the totalNumberOfSectionSentences to set
	 */
	public final void setTotalNumberOfSectionSentences(int totalNumberOfSectionSentences) {
		this.totalNumberOfSectionSentences = totalNumberOfSectionSentences;
	}

	/**
	 * @return the avgNumberOfSectionWords
	 */
	public final double getAvgNumberOfSectionWords() {
		return avgNumberOfSectionWords;
	}

	/**
	 * @param avgNumberOfSectionWords the avgNumberOfSectionWords to set
	 */
	public final void setAvgNumberOfSectionWords(double avgNumberOfSectionWords) {
		this.avgNumberOfSectionWords = avgNumberOfSectionWords;
	}

	/**
	 * @return the avgNumberOfSectinSentences
	 */
	public final double getAvgNumberOfSectinSentences() {
		return avgNumberOfSectionSentences;
	}

	/**
	 * @param avgNumberOfSectinSentences the avgNumberOfSectinSentences to set
	 */
	public final void setAvgNumberOfSectionSentences(double avgNumberOfSectinSentences) {
		this.avgNumberOfSectionSentences = avgNumberOfSectinSentences;
	}

	/**
	 * @return the totalNumberOfSectionInstances
	 */
	public int getTotalNumberOfSectionInstances() {
		return totalNumberOfSectionInstances;
	}

	/**
	 * @param totalNumberOfSectionInstances the totalNumberOfSectionInstances to set
	 */
	public void setTotalNumberOfSectionInstances(int totalNumberOfSectionInstances) {
		this.totalNumberOfSectionInstances = totalNumberOfSectionInstances;
	}

}
