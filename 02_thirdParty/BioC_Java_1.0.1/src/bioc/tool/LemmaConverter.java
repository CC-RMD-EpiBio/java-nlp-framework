package bioc.tool;

import java.util.HashMap;
import java.util.Map;

import bioc.BioCAnnotation;
import bioc.util.CopyConverter;

import edu.ucdenver.ccp.nlp.biolemmatizer.BioLemmatizer;

/**
 * BioC wrapper
 */
public class LemmaConverter extends CopyConverter{
  /** lemma cache */
  private Map<String, String> lemmaMap;
  
  /** load BioLemmatizer */
  private static BioLemmatizer bioLemmatizer = new BioLemmatizer();
  
  
  public LemmaConverter () {
    lemmaMap = new HashMap<String, String>();
  }
  
/**
    Modify an {@code BioCAnnotation}.
 */
  public BioCAnnotation getAnnotation(BioCAnnotation in) {
    BioCAnnotation out = new BioCAnnotation();
    out.setID(in.getID());
    out.setInfons(in.getInfons());
    out.setText(in.getText());
    out.setLocations(in.getLocations());
    String pos = out.getInfon("POS");
    String token = out.getText();
    String lemma = generateLemma(token, pos); 
    out.putInfon("lemma", lemma);
    return out;
  }
 
 /**
  * Generate lemma for the input String with POS
  */
 private String generateLemma(String token, String pos) {
   String lemma;
   if(lemmaMap.containsKey(token + " " + pos)) {
      return lemmaMap.get(token + " " + pos);
   }
   else {
     lemma = bioLemmatizer.lemmatizeByLexiconAndRules(token, pos).lemmasToString();
     lemmaMap.put((token + " " + pos), lemma );
   } 
   return lemma;
 }
 
 
}