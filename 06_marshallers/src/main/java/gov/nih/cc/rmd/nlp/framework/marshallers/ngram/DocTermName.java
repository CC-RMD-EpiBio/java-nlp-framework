package gov.nih.cc.rmd.nlp.framework.marshallers.ngram;

public class DocTermName {

	long docFreq = 0;
	long termFreq = 0;
	String term = "";
	
	public long getDocFreq() {
		return docFreq;
	}
	public void setDocFreq(long docFreq) {
		this.docFreq = docFreq;
	}
	public long getTermFreq() {
		return termFreq;
	}
	public void setTermFreq(long termFreq) {
		this.termFreq = termFreq;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
}
