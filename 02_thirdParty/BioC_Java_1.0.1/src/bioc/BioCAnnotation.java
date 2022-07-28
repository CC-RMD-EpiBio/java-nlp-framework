package bioc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.relation.Relation;

/**
 * Stand off annotation. The connection to the original text can be made
 * through the {@code location} and the {@code text} fields.
 */
public class BioCAnnotation {

  /**
   * Id used to identify this annotation in a {@link Relation}.
   */
  protected String              id;
  protected Map<String, String> infons;
  protected List<BioCLocation>  locations;

  /**
   * The annotated text.
   */
  protected String              text;

  public BioCAnnotation() {
    id = "";
    infons = new HashMap<String, String>();
    locations = new ArrayList<BioCLocation>();
    text = "";
  }

  public BioCAnnotation(BioCAnnotation annotation) {
    id = annotation.id;
    infons = new HashMap<String, String>(annotation.infons);
    locations = new ArrayList<BioCLocation>(annotation.locations);
    text = annotation.text;
  }

  public String getID() {
	    return id;
  }
 
  public void setID(String id) {
	    this.id = id;
  }

  public Map<String, String> getInfons() {
	    return infons;
	  }

  public void setInfons(Map<String, String> infons) {
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
  
  public List<BioCLocation> getLocations() {
	    return locations;
  }
 
  public void setLocations(List<BioCLocation> locations) {
	    this.locations = locations;  
  }

  public void clearLocations(){
	  locations.clear();
  }
  
  public void addLocation(BioCLocation location) {
    locations.add(location);
  }

  public void setLocation(BioCLocation location){
	  ArrayList<BioCLocation> locationList = new ArrayList<BioCLocation>();
      locationList.add(location);
	  setLocations(locationList);
  }
  
  public void setLocation (int offset, int length){
	setLocation(new BioCLocation(offset,length));  
  }

  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    String s = "id: " + id;
    s += "\n";
    s += infons;
    s += "locations: " + locations;
    s += "\n";
    s += "text: " + text;
    s += "\n";
    return s;
  }
}
