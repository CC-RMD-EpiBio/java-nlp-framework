package bioc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Collection of documents.
 * 
 * Collection of documents for a project. They may be an entire corpus or some
 * portion of a corpus. Fields are provided to describe the collection.
 * 
 * Documents may appear empty if doing document at a time IO.
 */
public class BioCCollection implements Iterable<BioCDocument> {

  /**
   * Describe the original source of the documents.
   */
  protected String              source;

  /**
   * Date the documents obtained from the source.
   */
  protected String              date;

  /**
   * Name of a file describing the contents and conventions used in this XML
   * file.
   */
  protected String              key;
  protected Map<String, String> infons;

  /**
   * All the documents in the collection. This will be empty if document at a
   * time IO is used to read the XML file. Any contents will be ignored if
   * written with document at a time IO.
   */
  protected List<BioCDocument>  documents;

  public BioCCollection() {
    source = "";
    date = "";
    key = "";
    infons = new HashMap<String, String>();
    documents = new ArrayList<BioCDocument>();
  }

  public BioCCollection(BioCCollection collection) {
    date = collection.date;
    source = collection.source;
    key = collection.key;
    infons = new HashMap<String, String>(collection.infons);
    documents = new ArrayList<BioCDocument>(collection.documents);
  }

  /**
   * @return the source
   */
  public String getSource() {
    return source;
  }

  /**
   * @param source the source to set
   */
  public void setSource(String source) {
    this.source = source;
  }

  /**
   * @return the date
   */
  public String getDate() {
    return date;
  }

  /**
   * @param date the date to set
   */
  public void setDate(String date) {
    this.date = date;
  }

  /**
   * @return the key
   */
  public String getKey() {
    return key;
  }

  /**
   * @param key the key to set
   */
  public void setKey(String key) {
    this.key = key;
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
  
  /**
   * @return the documents
   */
  public List<BioCDocument> getDocuments() {
    return documents;
  }

  /**
   * @param documents the documents to set
   */
  public void setDocuments (List <BioCDocument> documents){
	  this.documents = documents;	  
  }
  
  public void clearDocuments(){
	  documents.clear();
  }

  public int getSize(){
	return documents.size();  
  }
  
  public BioCDocument getDocument(int index) {
	    return documents.get(index);
	  }
  
  /**
   * @param document the document to add
   */
  public void addDocument(BioCDocument document) {
    documents.add(document);
  }

  public void removeDocument(BioCDocument document){
	  documents.remove(document);
  }
  
 
  
  @Override
  public String toString() {
    String s = "source: " + source;
    s += "\n";
    s += "date: " + date;
    s += "\n";
    s += "key: " + key;
    s += "\n";
    s += infons;
    s += "\n";
    s += documents;
    s += "\n";
    return s;
  }

  @Override
  public Iterator<BioCDocument> iterator() {
    return documents.iterator();
  }
}
