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
// =================================================
/**
 * BratAnnotation is a container for Brat formatted Entities 
 *
 *    T4  Concept 34 39 cold
 *    #4  attributes  cui=xxxxx|assertion=true|conditional=false| ......
 *
 * @author     Guy Divita
 * @created    May 4, 2018
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.brat;

import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.uima.FeatureValuePair;


/**
 * @author guy
 *
 */

public class BratAnnotation {

 
  // =================================================
  /**
   * constructor
   * 
   * @param pName
   * @param pBegin
   * @param pEnd
   * @param pSnippet
   * @param pFeatures
  */
  // =================================================
   public BratAnnotation (String pName, int pBegin, int pEnd, String pSnippet, List<FeatureValuePair> pFeatures) {
   
     this.name = pName;
     this.begin = pBegin;
     this.end = pEnd;
     this._ID = counter++;
     this.id = "T" + _ID;
     this.snippet = pSnippet;
     this.features = pFeatures;
  
     
  } // end Constructor() ---------------
   
   
  // =================================================
    /**
     * formatNote prints out #[_ID] \t attributes \t  [xxx=yyy|pp=ttt| ....] \n 
     *                   
     * @return String
    */
    // =================================================
   public final String formatNote() {
     
     StringBuffer buff = new StringBuffer();
     
     buff.append("#");
     buff.append(_ID);
     buff.append('\t');
     buff.append("attributes");
     buff.append(" ");
     buff.append(this.id);
     buff.append('\t');
     buff.append( getFeaturesAndValues());
     
     buff.append('\n');
     
     return buff.toString();
     
    }

  // =================================================
  /**
   * toString
   * 
   * @return String
   * 
  */
  // =================================================
 @Override  
  public String toString() {
   StringBuffer buff = new StringBuffer();
   

   buff.append(this.id );
   buff.append("\t");
   buff.append( this.name );
   buff.append(" ");
   buff.append(this.begin);
   buff.append(" ");
   buff.append(this.end);
   buff.append("\t");
   buff.append(this.snippet);
   buff.append('\n');
   
   return buff.toString();
    
  }


  // =================================================
  /**
   * getFeaturesAndValues returns a string of concatinated
   *   feature=value|feature=value|..... |
   * 
   * @return
  */
  // =================================================
   private String getFeaturesAndValues() {
   
     StringBuffer buff = new StringBuffer();
     if ( this.features !=  null && !this.features.isEmpty())
     for ( FeatureValuePair feature: this.features ) {
       buff.append(feature.getFeatureName() );
       buff.append("=");
       buff.append(feature.getFeatureValue());
       buff.append("|");
     }
     return buff.toString();
  } // end Method getFeaturesAndValues() --------------


  /**
   * @return the id
   */
  public String getId() {
    return id;
    }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }
  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  /**
   * @return the begin
   */
  public int getBegin() {
    return begin;
  }
  /**
   * @param begin the begin to set
   */
  public void setBegin(int begin) {
    this.begin = begin;
  }
  /**
   * @return the end
   */
  public int getEnd() {
    return end;
  }
  /**
   * @param end the end to set
   */
  public void setEnd(int end) {
    this.end = end;
  }
  /**
   * @return the snippet
   */
  public String getSnippet() {
    return snippet;
  }
  /**
   * @param snippet the snippet to set
   */
  public void setSnippet(String snippet) {
    this.snippet = snippet;
  }
  /**
   * @return the note
   */
  public String getNote() {
    return note;
  }
  /**
   * @param note the note to set
   */
  public void setNote(String note) {
    this.note = note;
  }
  
  /**
   * resetCounter
   */
  public static void resetCounter() {
    counter = 1;
  }
  
  // -------------------
 // Container variables
  private String id = null;
 private int _ID = 0;
 private String name = null;
 private int    begin = 0;
 private int    end = 0;
 private String  snippet = null;
 private String  note = null;
 private List<FeatureValuePair> features = null;
 private static int counter = 1;
  
}
