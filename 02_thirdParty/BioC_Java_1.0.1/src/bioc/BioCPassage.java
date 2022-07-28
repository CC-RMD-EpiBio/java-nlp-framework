package bioc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * One passage in a {@link BioCDocument}.
 * 
 * This might be the {@code text} in the passage and possibly
 * {@link BioCAnnotation}s over that text. It could be the
 * {@link BioCSentence}s in the passage. In either case it might include
 * {@link BioCRelation}s over annotations on the passage.
 */
public class BioCPassage implements Iterable<BioCSentence> {

  /**
   * The offset of the passage in the parent document. The significance of the
   * exact value may depend on the source corpus. They should be sequential and
   * identify the passage's position in the document. Since pubmed is extracted
   * from an XML file, the title has an offset of zero, while the abstract is
   * assumed to begin after the title and one space.
   */
  protected int                  offset;

  /**
   * The original text of the passage.
   */
  protected String               text;

  /**
   * Information of text in the passage.
   * 
   * For PubMed references, it might be "title" or "abstract". For full text
   * papers, it might be Introduction, Methods, Results, or Conclusions. Or
   * they might be paragraphs.
   */
  protected Map<String, String>  infons;

  /**
   * The sentences of the passage.
   */
  protected List<BioCSentence>   sentences;

  /**
   * Annotations on the text of the passage.
   */
  protected List<BioCAnnotation> annotations;

  /**
   * Relations between the annotations and possibly other relations on the text
   * of the passage.
   */
  protected List<BioCRelation>   relations;

  public BioCPassage() {
    offset = -1;
    text = "";
    infons = new HashMap<String, String>();
    sentences = new ArrayList<BioCSentence>();
    annotations = new ArrayList<BioCAnnotation>();
    relations = new ArrayList<BioCRelation>();
  }

  public BioCPassage(BioCPassage passage) {
    offset = passage.offset;
    text = passage.text;
    infons = new HashMap<String, String>(passage.infons);

    sentences = new ArrayList<BioCSentence>();
    for (BioCSentence sen : passage.sentences) {
      sentences.add(new BioCSentence(sen));
    }

    annotations = new ArrayList<BioCAnnotation>();
    for (BioCAnnotation ann : passage.annotations) {
      annotations.add(new BioCAnnotation(ann));
    }

    relations = new ArrayList<BioCRelation>();
    for (BioCRelation rel : passage.relations) {
      relations.add(new BioCRelation(rel));
    }
  }

  
  /**
   * @return the offset
   */
  public int getOffset() {
    return offset;
  } 
  
  /**
   * @param offset the offset to set
   */
  public void setOffset(int offset) {
    this.offset = offset;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * @param text the text to set
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * @return the infons
   */
  public Map<String, String> getInfons() {
    return infons;
  }

  /**
   * @param infons the infons to set
   */
  public void setInfons(Map<String,String> infons) {
    this.infons = infons;
  }

  public void clearInfons(){
	  infons.clear();
  }
  
  public String getInfon(String key) {
    return infons.get(key);
  }

  public void putInfon(String key, String value) {
    infons.put(key, value);
  }

  public void removeInfon(String key){
	  infons.remove(key);
  }

  
  
  public List<BioCAnnotation> getAnnotations() {
	    return annotations;
  }
  
  public void setAnnotations(List <BioCAnnotation> annotations) {
	    this.annotations = annotations;
  }

  public void clearAnnotations(){
	  annotations.clear();
  }

  public BioCAnnotation getAnnotation(int index){
	  return annotations.get(index);
  }

  public void addAnnotation(BioCAnnotation annotation) {
	  annotations.add(annotation);
  }

  public void removeAnnotation(BioCAnnotation annotation){
	 annotations.remove(annotation);  
  }

  public void removeAnnotation(int index){
	  annotations.remove(index);
  }
  
  /**
   * @return iterator over annotations
   */
  public Iterator<BioCAnnotation> annotationIterator() {
    return annotations.iterator();
  }

/**
 * @return the relations
 */
  public List<BioCRelation> getRelations() {
	    return relations;
  }
  
  public void setRelations(List <BioCRelation> relations) {
    this.relations = relations;
  }
 
  public void clearRelations(){
	  relations.clear();
  }

  public BioCRelation getRelation(int index){
	  return relations.get(index);
  }

  public void addRelation(BioCRelation relation) {
	    relations.add(relation);
  }

  public void removeRelation(BioCRelation relation){
	  relations.remove(relation);  
  }

  public void removeRelation(int index){
	  relations.remove(index);
  }
  
  /**
   * @return iterator over relations
   */
  public Iterator<BioCRelation> relationIterator() {
    return relations.iterator();
  }
 
  /**
   * @return the sentences
   */
  public List<BioCSentence> getSentences() {
	    return sentences;
	  }
  
  public BioCSentence getSentence(int index){
	  return sentences.get(index);
  }

  public void removeSentence(BioCSentence sentence){
	sentences.remove(sentence);  
  }

  public void removeSentence(int index){
	sentences.remove(index);
  }

  public void clearSentences(){
	sentences.clear();
  }

/**
 * @param sentence the sentence to add
 */
public void addSentence(BioCSentence sentence) {
  sentences.add(sentence);
}

/**
 * @param sentences the collection of sentences to set
 */
public void setSentences(List <BioCSentence> sentences) {
	    this.sentences = sentences;
	  }

/**
 * @return iterator over {@link BioCSentence}s
 */
public Iterator<BioCSentence> iterator() {
  return sentences.iterator();
}
      

  @Override
  public String toString() {
    String s = "infons: " + infons;
    s += "\n";
    s += "offset: " + offset;
    s += "\n";
    s += text;
    s += "\n";
    s += sentences;
    s += "\n";
    s += annotations;
    s += "\n";
    s += relations;
    s += "\n";
    return s;
  }
}
