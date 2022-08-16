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
