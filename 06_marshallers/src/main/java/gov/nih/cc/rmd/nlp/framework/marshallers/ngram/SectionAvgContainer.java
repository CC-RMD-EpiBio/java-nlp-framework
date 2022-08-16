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
