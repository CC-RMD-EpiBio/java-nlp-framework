// =======================================================
/**
 * SVMContext is a container to hold features around a mention
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * @author guy
 *
 */
public class SVMContext {

  
  

  public static final String ANSWER_FIELD_NAME = "999_Answer";
  
  // =======================================================
  /**
   * Constructor SVMContext 
   *           
   */
  // =======================================================
  public SVMContext( ) {
    
  
  } // end Constructor ---------------------------------

  

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
  public SVMContext( List<String> pAttributeNamesAndValues, String pAnswerValue ) {
    
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
  public SVMContext(List<String> pAttributesNamesAndValues) {
   
    initialize( pAttributesNamesAndValues );
    
  } // end Constructor ---------------------------------

// =======================================================
  /**
   * Constructor SVMContext 
   *
   * @param pAttributeNamesAndValues
   * @param pAnswerValue
   * @param pDocumentId
   * @param pOffsets  [begin|end]
   * @param pSymptomWords
   */
  // =======================================================
  public SVMContext(List<String> pAttributeNamesAndValues, String pAnswerValue, String pDocumentId, String pOffsets, String pSymptomWords) {
   
    initialize( pAttributeNamesAndValues);
    
    this.answerValue           = pAnswerValue;
    this.documentId            = pDocumentId;
    this.offsets               = pOffsets;
    this.symptomName           = pSymptomWords;
    
  }

//=======================================================
 /**
  * Constructor SVMContext 
  *
  * @param pAttributeNamesAndValues
  * @param pAnswerValue
  * @param pDocumentId
  * @param pOffsets  [begin|end]

  */
 // =======================================================
 public SVMContext(List<String> pAttributeNamesAndValues, String pAnswerValue, String pDocumentId, String pOffsets) {
  
   initialize( pAttributeNamesAndValues);
   
   this.answerValue           = pAnswerValue;
   this.documentId            = pDocumentId;
   this.offsets               = pOffsets;
  
   
 } // end Constructor() ----------------------------------



// =================================================
/**
 * Constructor use this contstructor when there will be more than one answer from the set of attributes and values.  
 *
 * @param pAttributes
 * @param pDocumentId
 * @param pOffsets
 * 
**/
// =================================================
public SVMContext(List<String> pAttributeNamesAndValues, String pDocumentId, String pOffsets) {
  
   initialize( pAttributeNamesAndValues);
   
   this.documentId            = pDocumentId;
   this.offsets               = pOffsets;
  
   
 } // end Constructor() ----------------------------------



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
  
}// End Method setAnswer() -------------------------------


  // =======================================================
  /**
   * getAttributeNames retrieves the attribute names
   * 
   * @return String[] 
   */
  // =======================================================
  public String[] getAttributeNames() {
    
    String[] sortedAttributeNames  = getSortedAttributeNames();
   
    return sortedAttributeNames;
    
        
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

//=======================================================
/**
* getAttributeValue retrieves the attribute value for a given part of an attribute name
* 
* @param pAttributeName
* @return String   "unknown" if null
*/
//=======================================================
public String getAttributeValueAux(String pAttributeNamePattern) {

String returnVal = null;

  for (String key : this.getAttributeNames()) {
    if ( key.contains( pAttributeNamePattern )) {
      returnVal = this.attributes.get(key);
      break;
    }
  }

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
   * getSymptomName 
   * 
   * @return
   */
  // =======================================================
  public String getSymptomName() {
  return this.symptomName;
  } // End Method getSymptomName() ======================
  

  // =================================================
  /**
   * getCSVHeader returns a pipe delimited line with the column headings in it
   * The ordering comes from the natural ordering that comes back from the hash.keySet() 
   * 
   * @return String
  */
  // =================================================
  public String getCSVHeader() {
    
    StringBuffer buff = new StringBuffer();
    
    
     String[] namez = getAttributeNames();
    
   
    if ( namez != null )
    
    buff.append("DocID" + CSVDelimeter);
    buff.append("Offsets" + CSVDelimeter );
    for ( int i = 0; i < namez.length; i++ ) 
      buff.append( namez[i] + CSVDelimeter );
    
    // buff.append(SVMContext.ANSWER_FIELD_NAME);
    
    
    return buff.toString();
  } // end Method getCSVHeader() ---------------------


  // =================================================
  /**
   * toCSVRow returns a pipe delimited row of values,
   * the ordering determined by the sorted/alphebetical ordering
   * of the attribute names (which should be prefaced by zero delimited numbers)
   *
   * The documentId is always the first column
   * The offsets    are the next set of columns
   * The answer is always the last column
   * 
   * @return String
  */
  // =================================================
  public final String toCSVRow() {
    
    StringBuffer buff = new StringBuffer();
    String[] namez = getAttributeNames();
    
    
    if ( namez != null )
    
    
    buff.append( this.documentId + CSVDelimeter);
    buff.append( this.getOffsets() + CSVDelimeter);
    for ( int i = 0; i < namez.length; i++ ) 
     buff.append(   this.getAttributeValue( namez[i]) + CSVDelimeter);
    
    // buff.append( this.getAnswerValue());
     
    
    return buff.toString();
  } // end Method toCSVRow() ------------------------

  
// =================================================
  /**
   * toFeatureVectorString creates a String that is the set
   * of line feature vectors
   * 
   * If there is a pipe in any of the values, they get transformed to ~
   * and newlines into spaces.
   * 
   * @return String values that are ordered by the name (which are prefixed by numbers)
  */
  // =================================================
  public String toFeatureVectorString() {
    
    String returnVal = null;
    StringBuffer buff = new StringBuffer();
    String[] namez = getAttributeNames();
    String value = "";
    
   
    if ( namez != null )
   
    buff.append( this.documentId + CSVDelimeter);
    buff.append( this.getOffsets() + CSVDelimeter);
    for ( int i = 0; i < namez.length; i++ ) {
      value = this.getAttributeValue( namez[i]);
      if ( value != null && !value.isEmpty())
        value = U.normalizePipesAndNewLines( value).trim(); 

      buff.append( value );
      if ( i < namez.length -1)
        buff.append( CSVDelimeter  );
    }
    
    // buff.append( this.getAnswerValue());
    
    returnVal = buff.toString();
     
    
    
    return returnVal;
  } // end Method toFeatureVectorString() -------------






  // =================================================
  /**
   * toArffRow 
   * @deprecated    Use the version from the contexts instead
   *                so that the enumerations can be on-the-fly
   *                calculated and filtered to "unknown" if
   *                there are not enough frequency for the
   *                the words in postion attributes
  */
  // =================================================
   public final String toArffRow() {
  
     StringBuffer buff = new StringBuffer();
     
     String[] attributeNames = this.getSortedAttributeNames();
     for ( int i = 0; i < attributeNames.length;  i++) {
       String attributeName = attributeNames[i];
       String attributeValue = this.getAttributeValue(attributeName);
       buff.append( attributeValue );
       if ( i < attributeNames.length -1 )
         buff.append(",");
      
     }
     
     // ------------------------------------------
     // Add the answer to the end of the data line
    // buff.append( this.answerValue );
     
     
     buff.append("\n");
     
         
     return buff.toString();
  } // end Method toArffRow() -----------------------






//=================================================
 /**
  * setCSVDelimiter sets the csv delimiter. By default, it's a pipe.
  * 
  * @param pDelimeter
 */
 // =================================================
 public final void setCSVDelimiter( String pDelimiter) {

   this.CSVDelimeter = pDelimiter;
   
 } // end Method setCSVDelimiter() ------------------
 
//=================================================
/**
 * getCSVDelimiter sets the csv delimiter. 
 * 
 * @return String 
*/
// =================================================
public final String getCSVDelimiter( ) {

  return this.CSVDelimeter;
  
} // end Method getCSVDelimiter() ------------------

// =================================================
/**
 * parse turns a featureVector into an SVMContext
 * 
 * 
 * The documentId is always the first column
 * The offsets    are the next set of columns
 * The answer is always the last column - prefixed by 999_
 * 
 * The ordering of the values comes from the ordering of the 
 * attributenames passed in otherwise.
 * 
 * The offsets are one field with begin and end offset delimited by a ~
 * 
 * @param pAttributeNames
 * @param pFeatureVector
 * @return SVMContext
 * 
*/
// =================================================
public final static SVMContext parse(String[] pAttributeNames, String pFeatureVector, String pDelimiter) {
  
  String[] columns = U.split( pFeatureVector, pDelimiter );
  
  SVMContext context = new SVMContext();
  
  context.attributes = new HashMap<String,String>( columns.length  );
  for ( int i = 0; i < columns.length ;  i++ ) {
   
    if      ( pAttributeNames[i].equals("documentId"))
      context.documentId = columns[i];
    else if ( pAttributeNames[i].equals("offsets"))
      context.offsets = columns[i];
    else
      context.attributes.put( pAttributeNames[i],  columns[i]);
   
    if ( pAttributeNames[i].equals(SVMContext.ANSWER_FIELD_NAME)) 
      context.answerValue = columns[i];
  }
  
  
  
  return context;
  
} // end Constructor parse() ------------------------------

  // ----------------------------
  // Fields
  // ----------------------------
  private HashMap<String,String>attributes = null;
  private String[] sortedAttributeNames = null;
  private String answerValue = null;
  private String documentId  = null;
  private String offsets     = null;
  private String symptomName = null;
  private String CSVDelimeter = "|";

  // =================================================
  /**
   * createFeatureVectorHeader returns a String of the featureNames delimited by the delimiter.
   * 
   * @param pFeatureVectorNames
   * @param pFeatureVectorDelimiter
   * @return String
  */
  // =================================================
  public final static String createFeatureVectorHeader(String[] pFeatureVectorNames, String pFeatureVectorDelimiter) {
 
    String returnVal = null;
    StringBuffer buff = new StringBuffer();
    
    buff.append("#");
    if ( pFeatureVectorNames != null ) {
      for ( int i = 0; i < pFeatureVectorNames.length ; i++)
        buff.append( pFeatureVectorNames[i] + pFeatureVectorDelimiter );
      returnVal = buff.substring(0, buff.length() -1 );
      
      System.err.println(returnVal);
    }
    return returnVal;
    
  } // end Method createFeatureVectorHeader() -------
 


  
  
  
}
