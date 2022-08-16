/*******************************************************************************
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                       Epidemiology and Biostatistics Branch 
 *                                            2019 - 2022
 *   ---------------------------------------------------------------------------
 *   Copyright Notice:
 *   This software was developed and funded by the National Institutes of Health
 *   Clinical Center (NIHCC), part of the National Institutes of Health (NIH),
 *   and agency of the United States Department of Health and Human Services,
 *   which is making the software available to the public for any commercial
 *   or non-commercial purpose under the following open-source BSD license.
 *  
 *   Government Usage Rights Notice:
 *   The U.S. Government retains unlimited, royalty-free usage rights to this 
 *   software, but not ownership, as provided by Federal law. Redistribution 
 *   and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *      1. Redistributions of source code must retain the above copyright
 *         and government usage rights notice, this list of conditions and the 
 *         following disclaimer.
 *  
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *        
 *      3. Neither the names of the National Institutes of Health Clinical
 *         Center, the National Institutes of Health, the U.S. Department of
 *         Health and Human Services, nor the names of any of the software
 *         developers may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
 *   
 *      4. The U.S. Government retains an unlimited, royalty-free right to
 *         use, distribute or modify the software.
 *   
 *      5. Please acknowledge NIH CC as the source of this software by including
 *         the phrase: "Courtesy of the U.S. National Institutes of Health Clinical Center"
 *          or 
 *                     "Source: U.S. National Institutes of Health Clinical Center."
 *  
 *     THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS
 *     IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *     TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *     PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 *     OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *     EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *     PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *     NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *     When attributing this code, please make reference to:
 *        Divita G, Carter ME, Tran LT, Redd D, Zeng QT, Duvall S, Samore MH, Gundlapalli AV. 
 *        v3NLP Framework: tools to build applications for extracting concepts from clinical text. 
 *        eGEMs. 2016;4(3). 
 *      
 *     In the absence of a specific paper or url listed above, reference https://github.com/CC-RMD-EpiBio/java-nlp-framework
 *   
 *     To view a copy of this license, visit https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD
 *******************************************************************************/
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
