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
// =======================================================
/**
 * SVMContext.java [Summary here]
 *
 * @author  guy
 * @created Jun 2, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * @author guy
 *
 */
public class SVMContextOld {

  

  // =======================================================
  /**
   * Constructor SVMContext 
   *
   * @param pAttributeNameAndValues a list of attributeName|attribute value strings - all of these will be converted to nominal values after
   *                                                                                  seeing all the values 
   * @param pAnswerValue - the value of the answer in String form
   *                                                                           
   *                                                                              
   
   */
  // =======================================================
  public SVMContextOld( List<String> pAttributeNamesAndValues, String pAnswerValue ) {
    
    initialize( pAttributeNamesAndValues);
   
    this.answerValue           = pAnswerValue;
    
  } // end Constructor ---------------------------------

 

  


  // =======================================================
  /**
   * Constructor SVMContext this is the constructor to use
   * for runs, rather than training
   *
   * @param pAttributesNamesAndValues
   */
  // =======================================================
  public SVMContextOld(ArrayList<String> pAttributesNamesAndValues) {
   
    initialize( pAttributesNamesAndValues );
    
  } // end Constructor ---------------------------------

// =======================================================
  /**
   * Constructor SVMContext 
   *
   * @param pAttributeNamesAndValues
   * @param pAnswerValue
   * @param pDocumentId
   * @param pOffsets
   * @param pSymptomWords
   */
  // =======================================================
  public SVMContextOld(ArrayList<String> pAttributeNamesAndValues, String pAnswerValue, String pDocumentId, String pOffsets, String pSymptomWords) {
   
    initialize( pAttributeNamesAndValues);
    
    this.answerValue           = pAnswerValue;
    this.documentId            = pDocumentId;
    this.offsets               = pOffsets;
    this.symptomName           = pSymptomWords;
    
  }






//=======================================================
 /**
  * initialize 
  * 
  * @param pAttributeNamesAndValues
  */
 // =======================================================
 private void initialize(List<String> pAttributeNamesAndValues) {
   
   this.attributes = new HashMap<String,String>();
   String[] cols = null;
   
   
   for ( String attributeNameAndValue : pAttributeNamesAndValues ) {
    cols= U.split(attributeNameAndValue );
    String  attributeName = cols[0];
    String attributeValue = cols[1];
   
    this.attributes.put( attributeName, attributeValue);
   }
 } // End Method initialize() ======================
 


  // =======================================================
/**
 * setAnswer 
 * 
 * @param pAnswer
 */
// =======================================================
public void setAnswer(String pAnswer) {
  this.answerValue = pAnswer;
  
}// End Method setAnswer() ======================







  // =======================================================
  /**
   * getAttributeNames retrieves the attribute names
   * 
   * @return Set<String>
   */
  // =======================================================
  public Set<String> getAttributeNames() {
   
    return this.attributes.keySet();
        
  } // End Method getAttributeNames() ======================
  
//=======================================================
 /**
  * getAnswerValue 
  * 
  * @return String  
  */
 // =======================================================
 public String getAnswerValue() {
  
   return this.answerValue;
       
 } // End Method getAttributeNames() ======================

//=======================================================
/**
 * getAttributeValue retrieves the attribute value for a given attribute
 * 
 * @param pAttributeName
 * @return String   "unknown" if null
 */
// =======================================================
public String getAttributeValue(String pAttributeName) {
 
  String returnVal = null;
  
  returnVal = this.attributes.get(pAttributeName);
  
  if ( returnVal == null )
    returnVal = "unknown";
  
  return returnVal;
      
} // End Method getAttributeNames() ======================


  // =======================================================
  /**
   * getSortedAttributeNames returns an array of attribute
   * names 
   * @param 
   * @return String[] 
   */
  // =======================================================
  public String[] getSortedAttributeNames( ) {
    
    if ( this.sortedAttributeNames == null ) {
      Set<String> attributeNames = this.attributes.keySet();
      this.sortedAttributeNames = attributeNames.toArray(new String[attributeNames.size()]);
      Arrays.sort(this.sortedAttributeNames);
    }
    return this.sortedAttributeNames;
   
  } // End Method toString() ======================
  
  // =======================================================
  /**
   * getDocumentId 
   * 
   * @return
   */
  // =======================================================
  public String getDocumentId() {
    return this.documentId;
  } // End Method getDocumentId() ======================
  

  // =======================================================
  /**
   * getOffsets [Summary here]
   * 
   * @return
   */
  // =======================================================
  public String getOffsets() {
   return offsets;
  } // End Method getOffsets() ======================
  

  // =======================================================
  /**
   * getSymptomName [Summary here]
   * 
   * @return
   */
  // =======================================================
  public String getSymptomName() {
  return this.symptomName;
  } // End Method getSymptomName() ======================
  

  // ----------------------------
  // Fields
  // ----------------------------
  private HashMap<String,String>attributes = null;
  private String[] sortedAttributeNames = null;
  private String answerValue = null;
  private String documentId  = null;
  private String offsets     = null;
  private String symptomName = null;


  
  
  
}
