// =======================================================
/**
 * SVMContexts is a container to hold the enumerated values for
 * a set of SVMContext elements
 *
 * @author  Divita
 * @created Dec 16, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.nih.cc.rmd.nlp.framework.utils.U;


public class SVMContextsOld {
  


// =======================================================
  /**
   * Constructor SVMContexts 
   *
   * @param pSVMContexts;
   * @param pTheshhold
   * @param pSVMOutputDir
   * @throws FileNotFoundException 
   * 
   */
  // =======================================================
  public SVMContextsOld(List<SVMContext> pSVMContexts, int pThreshold ) throws FileNotFoundException {
   this.contexts = pSVMContexts;
   
   
   // -----------------------
   // Set the attribute names
   this.attributeNames = setAttributeNames( this.contexts );
  
  
   // -------------------------------------------------
   // Set the attribute enumerations for each attribute
   this.attributeEnumerations = setAttributeEnumerations(this.attributeNames, this.contexts, pThreshold);
   
   
   
  } // End constructor() ---------------------------------
 


//=======================================================
 /**
  * Constructor for production run, not training. As such, needs to read back in the header file to get the
  * already decided upon enumerations
  *
  *
  * @param pHeaderFile
  * @param pOutput
 * @throws Exception 
  * 
  */
 // =======================================================
 public SVMContextsOld( String pHeaderFile ) throws Exception {
  this.contexts = null;

  String headerFileContents = U.readFile(pHeaderFile);

  ArrayList<String> _attributeNames = new ArrayList<String>();
  this.attributeEnumerations        = new HashMap<String, HashSet<String>>();
  // -----------------------
  // Set the attribute names
  getAttributeNamesAndValues( headerFileContents, _attributeNames, this.attributeEnumerations );
  this.attributeNames = _attributeNames.toArray(new String[ _attributeNames.size()]);
  
  
 } // End constructor() ---------------------------------


  
 



/**
 * @return the attributeNames
 */
public final String[] getAttributeNames() {
  return attributeNames;
}



/**
 * @return the attributeEnumerations
 */
public final HashMap<String, HashSet<String>> getAttributeEnumerations() {
  return attributeEnumerations;
}



// =======================================================
  /**
   * getAttributeEnumerations 
   * 
   * @param pAttributeName
   * @return List<String>  null if this attribute is not an enumeration
   */
  // =======================================================
  public final List<String> getAttributeEnumerations(String pAttributeName) {
   
    List<String> enumerationList = null;
    
    HashMap<String, HashSet<String>> enumerations = this.getAttributeEnumerations();
    if ( enumerations != null && enumerations.size() > 0 ) { 
      HashSet<String> enumerationHash = enumerations.get(pAttributeName);
      if (enumerationHash != null && enumerationHash.size() > 0) {
        enumerationList = hashSetToList( enumerationHash);
      } 
    } // end if there is enumeration for this attribute name
    return enumerationList;
    
  } // End Method getAttributeEnumerations() ======================
  



//=======================================================
/**
* toArffDataRow 
* 
* @param pContext
* @param pForTraining
* @return String 
*/
// =======================================================
public final String toArffDataRow( SVMContext pContext, boolean pForTraining ) {
 
 StringBuffer buff = new StringBuffer();
 
 for ( String attributeName : this.attributeNames ) {
   String attributeValue = pContext.getAttributeValue(attributeName);
   
   HashSet<String> attributeEnumerationValues = this.attributeEnumerations.get( attributeName);
 
   if ( attributeEnumerationValues != null  ) {
     // --------------------------------------------------
     // Add only if it's in the filtered enumerated values
     if ( attributeEnumerationValues.contains( attributeValue) )
       buff.append( attributeValue + ", ");
     else
       buff.append("unknown" + ", ");
   } else {
     System.err.println(" what no enumerated values for " + attributeName );
   }
 }
 
 // ------------------------------------------
 // Add the answer to the end of the data line
  if ( pForTraining )
    buff.append( pContext.getAnswerValue() );
  else
    buff.append( " ");
 
 
 buff.append("\n");
 
     
 return buff.toString();
} // End Method toArffDataRow() ======================

//=======================================================
/**
* toArffDataRow 
* 
* @param pContext
* @param pForTraining
* @return String 
*/
//=======================================================
public final String toArffDataRowDebugging( SVMContext pContext ) {

StringBuffer buff = new StringBuffer();

buff.append( pContext.getDocumentId()  + "|" );
buff.append( pContext.getOffsets()     + "|" );
buff.append( pContext.getSymptomName() + "|" );
buff.append( pContext.getAnswerValue() + "|" );

/*
for ( String attributeName : this.attributeNames ) {
 String attributeValue = pContext.getAttributeValue(attributeName);
 
 HashSet<String> attributeEnumerationValues = this.attributeEnumerations.get( attributeName);

 if ( attributeEnumerationValues != null  ) {
   // --------------------------------------------------
   // Add only if it's in the filtered enumerated values
   if ( attributeEnumerationValues.contains( attributeValue) )
     buff.append( attributeValue + ", ");
   else
     buff.append("unknown" + ", ");
 } else {
   System.err.println(" what no enumerated values for " + attributeName );
 }
}
*/




buff.append("\n");

   
return buff.toString();
} // End Method toArffDataRow() ======================





// =======================================================
/**
 * setHeaderAndComments
 * 
 * @param pComments
 */
// =======================================================
public final void setHeaderAndComments(String pComments) {
  this.headerComments = pComments;
} // End Method setHeaderAndComments() ======================



// -------------------------------------
  // Global Variables
  // 

  // =======================================================
/**
 * setRelation sets the arff relation
 * 
 * @param pRelation
 */
// =======================================================
public final void setRelation(String pRelation) {
  this.relation = pRelation;
} // End Method setRelation() ======================



// =======================================================
/**
 * getHeaderAndComments returns the arff header with
 * the relation 
 * 
 * @return
 */
// =======================================================
public final String getHeaderCommentsAndRelation() {
  
  StringBuffer buff = new StringBuffer();
  buff.append("% ====================================================== %\n");
  buff.append("% == ");
  buff.append( this.headerComments + " == %\n");
  buff.append("% == Created on " + U.getDateStampSimple() + "          == %\n");
  buff.append("% ====================================================== %\n");
  buff.append("\n");
  buff.append("@relation " + this.relation + "\n\n");
  
  return buff.toString();
} // End Method getHeaderAndComments() ======================



// =======================================================
/**
 * writeHeader writes out the attribute rows of an arff 
 * 
 * @param pSVMOutputDir
 * @throws FileNotFoundException 
 *
 */
// =======================================================
public final void writeHeader(String pSVMOutputDir )  {
  
  try {
  this.arffHeaderFile = pSVMOutputDir + "/" + "arffHeader.arff";
  PrintWriter out = new PrintWriter (  this.arffHeaderFile  );
  
  // ---------------
  // This has to be a valid arrf file with relation and data tags in it
  String header = getHeaderCommentsAndRelation(); 
  String attributeHeader = arffAttributes();
  
  out.print( header );
  out.print( attributeHeader );
  out.print( "@data\n");
  
  out.close();
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println("Issue writing the arff header file " + e.toString());
    throw new RuntimeException();
  }
  
} // End Method writeHeader() ======================



//=======================================================
 /**
  * arffAttributes   
  * 

  * @return String  the attributes in the arff format
  */
 // =======================================================
 public  String arffAttributes() {
   
  
   StringBuffer buff = new StringBuffer();
 
   // ----------------------------------------
   // Create a hash to hold each attribute 
   for ( String attributeName : this.attributeNames ) {
  
     HashSet<String> attributeEnumerationSet = this.attributeEnumerations.get( attributeName );
     String            attributeEnumerations = hashSetToCommaDelimitedString( attributeEnumerationSet );
     
     buff.append( "@attribute " + attributeName + " { " + attributeEnumerations + " }\n" );
     
   
   } // end loop through the attribute names
   
   // Add the answer enumeration as the last attribute
   String[]           answerValuesArray = getValues( "answer", this.contexts);
   HashSet<String> answerEnumerationSet = arrayToHashSet( answerValuesArray, 0);
   String            answerEnumerations = hashSetToCommaDelimitedString( answerEnumerationSet );
   buff.append("@attribute 99_answer { " + answerEnumerations + " }\n\n");
       
   return buff.toString();
 } // End Method arffAttributes() ======================



// =======================================================
/**
 * printData [Summary here]
 * 
 * @return String
 */
// =======================================================
public String printData( boolean pForTraining) {
  
  StringBuffer buff = new StringBuffer();
  buff.append("\n@data\n");
  
  // write out each context
  if ( contexts != null)
    for ( SVMContext context: this.contexts ) {
      
         buff.append( toArffDataRow( context , pForTraining) );
      
    } // end loop through contexts
  
  
  return buff.toString();
  // End Method printData() ======================
}



// =======================================================
/**
 * printDebuggingData 
 * 
 * @return
 */
// =======================================================
public String printDebuggingData() {

  StringBuffer buff = new StringBuffer();
  // write out each context
  if ( contexts != null)
    for ( SVMContext context: this.contexts ) {
      
         buff.append( toArffDataRowDebugging( context ) );
      
    } // end loop through contexts
  
  
 
  return buff.toString();
} // End Method printDebuggingData() ======================




// =======================================================
/**
 * setAttributeNames 
 * 
 * @param pArffHeaderFileContents
 * @return
 */
// =======================================================
private void getAttributeNamesAndValues(String                           pArffHeaderFileContents, 
                                        ArrayList<String>                pAttributeNames, 
                                        HashMap<String, HashSet<String>> pAttributeEnumerations ) {
 
  String[] rows = U.split(pArffHeaderFileContents, "\n");
  
  
  for ( String row : rows) {
    
    if ( row.startsWith("@attribute")) {
      String attributeName = getAttributeNameFromRow( row);
      pAttributeNames.add(attributeName);
      HashSet<String> enumerations = getAttributeEnumerationsFromRow( row);
      if ( enumerations != null )
        pAttributeEnumerations.put(attributeName, enumerations );
    }
      
  } // end loop thru rows
  
} // End Method setAttributeNames() ======================




// =======================================================
/**
 * getAttributeEnumerationsFromRow 
 * 
 * @param row
 * @return HashSet<String>  null if there are no enumerations
 */
// =======================================================
private HashSet<String> getAttributeEnumerationsFromRow(String pRow) {
  
  HashSet<String> _set = null;
  
  int bracket = pRow.indexOf('{');
  
  if ( bracket > -1 ) {
    _set = new HashSet<String>();
    int endBracket = pRow.indexOf('}');
    
    String innerRow = pRow.substring(bracket + 1, endBracket);
    String[] enumerations = U.split(innerRow, ",");
    
    for ( String enumeration : enumerations ) 
      _set.add( enumeration.trim());
    
    
  }
  
  return _set;
} // End Method getAttributeEnumerationsFromRow() ======================




// =======================================================
/**
 * getAttributeNameFromRow retrieves the attribute name from this row, which is bounded by
 * the first to sets of spaces
 * 
 * @param row
 * @return String
 */
// =======================================================
private String getAttributeNameFromRow(String pRow) {
 
  StringBuffer buff = new StringBuffer();
  
  char[] chars = pRow.toCharArray();
  
  int firstSeen = -1;
  int firstSpace = pRow.indexOf(' ');
 
  for ( int  i = firstSpace+1 ; i < chars.length; i++ ) {
    
    if ( chars[i] == ' ') {
      if ( firstSeen == -1 )
        firstSeen = i;
      if ( buff.length() > 0 ) 
       break;
     
    } else {
      buff.append(chars[i]);
    }
    
  }
  
  return buff.toString().trim();
  // End Method getAttributeNameFromRow() ======================
}



// =======================================================
/**
 * setAttributeEnumerations sets the enumerated values for each attribute
 * 
 * @param attributeNames
 * @param pContexts
 * @param pThreshold
 */
// =======================================================
private HashMap<String, HashSet<String>> setAttributeEnumerations(String[] pAttributeNames, List<SVMContext> pContexts, int pThreshold) {
  
  this.attributeEnumerations = new HashMap<String,HashSet<String>>();
  HashMap<String, HashSet<String>> _attributeEnumerations = new HashMap<String, HashSet<String>>();
  for ( String attributeName : pAttributeNames){
    HashSet<String> attributeEnumeration = setAttributeEnumeration( attributeName, pContexts, pThreshold );
    _attributeEnumerations.put( attributeName, attributeEnumeration );
  }
    
  return _attributeEnumerations;
  
} // End Method setAttributeEnumerations() ======================



// =======================================================
/**
 * setAttributeEnumeration sets the enumeration for this attribute
 * 
 * @param attributeName
 * @param pContexts
 * @param pThreshold
 */
// =======================================================
private HashSet<String> setAttributeEnumeration(String attributeName, List<SVMContext> pContexts, int pThreshold) {
  
  String[] attributeValuesArray = getValues( attributeName, this.contexts);
  HashSet<String> attributeEnumerationSet = arrayToHashSet( attributeValuesArray, pThreshold);
  
  return ( attributeEnumerationSet);
  
} // End Method setAttributeEnumeration() ======================



  // =======================================================
  /**
   * setAttributeNames sets the names (and order) of the attributes
   * 
   * @param List<SVMContext>
   * @return String[]
   */
  // =======================================================
  private String[] setAttributeNames( List<SVMContext> pContexts ) {
  
    // figure out the attributes from a context
    
    if ( this.attributeNames == null ) {
      String[] _attributeNames = pContexts.get(0).getAttributeNames();
    
      this.attributeNames = _attributeNames;
      //Arrays.sort(this.attributeNames);
    }
    
    return this.attributeNames;
  } // End Method setAttributeNames() ======================
  



 // =======================================================
 /**
  * hashSetToCommaDelimitedString 
  * 
  * @param attributeEnumerationSet
  * @return String
  */
 // =======================================================
 private  String hashSetToCommaDelimitedString(HashSet<String> attributeEnumerationSet) {
   
   String returnVal = null;
   StringBuffer buff = new StringBuffer();
   boolean containsUnknown = false;
   if ( attributeEnumerationSet != null && attributeEnumerationSet.contains("unknown") ) 
     containsUnknown = true;
   if ( attributeEnumerationSet != null && attributeEnumerationSet.size() > 0 ) {
     String ae[] = attributeEnumerationSet.toArray(new String[ attributeEnumerationSet.size()]);
     Arrays.sort( ae);
    
     for ( String attributeEnumeration : ae )
       buff.append(attributeEnumeration + ", ");
   }
   // ---------------------------------------------------
   // Add and "unknown" enumeration to the set at the end
   
   if ( !containsUnknown ) {
     buff.append("unknown ");
     returnVal = buff.toString();
   }    else
     // take off the last comma
     returnVal  = buff.toString().substring(0, buff.length() -2);
     
   return returnVal;
 }   // End Method hashSetToCommaDelimitedString() ======================
 
//=======================================================
/**
* hashSetToList
* 
* @param attributeEnumerationSet
* @return List
*/
// =======================================================
private  List<String> hashSetToList(HashSet<String> attributeEnumerationSet) {
 
 List<String> returnVal = null;
 
 boolean containsUnknown = false;
 if ( attributeEnumerationSet != null && attributeEnumerationSet.contains("unknown") ) 
   containsUnknown = true;
 if ( attributeEnumerationSet != null && attributeEnumerationSet.size() > 0 ) {
   String ae[] = attributeEnumerationSet.toArray(new String[ attributeEnumerationSet.size()]);
   Arrays.sort( ae);
  
   returnVal = new ArrayList<String>( ae.length );
   for ( String attributeEnumeration : ae )
    returnVal.add(attributeEnumeration);
 }
 // ---------------------------------------------------
 // Add and "unknown" enumeration to the set at the end
 
 if ( !containsUnknown ) {
   if ( returnVal == null )  returnVal = new ArrayList<String>( );
   returnVal.add("unknown ");
  
 } 
   
   
 return returnVal;
}   // End Method hashSetToCommaDelimitedString() ======================




 // =======================================================
 /**
  * getValues retrieves an array of word|freq for the feature
  *  within the contexts 
  * @param pAttributeName
  * @param pContexts
  * @return
  */
 // =======================================================
 private  String[] getValues(String pAttributeName, List<SVMContext> pContexts) {
 
 
   HashMap<String,int[]> wordHash = new HashMap<String,int[]>();
  
   for (SVMContext context: pContexts ) {
     
     String word = null;
     if ( pAttributeName.equals("answer"))
       word = context.getAnswerValue();
     else 
       word = context.getAttributeValue( pAttributeName);
     if ( word != null ) { 
       int freq[] = wordHash.get(word);
       if ( freq == null ) {
         freq = new int[1];
         freq[0] = 0;
       }
       freq[0]++;
       wordHash.put(word, freq);
     }
   } // end loop through contexts

   // -----------------------
   // Covert the word|freq  hashMap into a string array
   String[] wordFreq = wordHashToWordFreqArray( wordHash);
   
   return wordFreq;
 }  // End Method getWords() ======================
 

 // =======================================================
 /**
  * arrayToHashSet creates a hashSet from the word freq array,
  * truncated at the words that fall below the minThreshhold
  * 
  * @param wordFreqArray
  * @param minThreshold
  * @return HashSet<String>
  */
 // =======================================================
 private  HashSet<String> arrayToHashSet(String[] wordFreqArray, int minThreshold) {
   
   HashSet<String> returnHash = new HashSet<String>();
   
   if ( wordFreqArray != null )
     for ( String row: wordFreqArray ) {
       String cols[] = U.split(row);
       String word = cols[0];
       int    freq = Integer.valueOf(cols[1]);
       if ( freq > minThreshold)
          returnHash.add( word);
       
     } // end loop through the word freq array
     
   return returnHash;
 } // End Method arrayToHashSet() ======================
 


 // =======================================================
 /**
  * wordHashToWordFreqArray converts a HashMap<String,int[]> into
  * an array of word|freq, where the array is sorted by freq,
  * in decending order.
  * 
  * @param wordHash
  * @param minThreshold 
  * @return String[] of word|freq
  */
 // =======================================================
 private  String[] wordHashToWordFreqArray(HashMap<String, int[]> wordHash) {
 
   Set<String> keys = wordHash.keySet();
   String[] freqWord = new String[keys.size()];
   int ctr = 0;
 
   for ( String key: keys){
     int[] freq = wordHash.get(key);
     freqWord[ctr] = U.pad( freq[0] ) + "|" + key;
  
     ctr++;
    
   }
   
   // ------------------------
   // Sort the array 
   Arrays.sort(freqWord);
   
   // ------------------------
   // reverse the order of the array
   String[] wordFreq = new String[ freqWord.length   ];
   
   int k = 0;
   for ( int i = freqWord.length -1 ; i >=0; i-- ) {
     String[] cols = U.split(freqWord[i]);
     wordFreq[k] = cols[1] + "|" + cols[0];
   
     k++;
   }
 
   return wordFreq;
 } // End Method wordHashToWordFreqArray() ======================



private List<SVMContext> contexts;
  private String[] attributeNames = null;
  private HashMap<String, HashSet<String>> attributeEnumerations = null;
  private String  headerComments = null;
  private String  relation = null;
  private String arffHeaderFile = null;
  // =======================================================
  /**
   * getArffHeader returns the full path to the arff header file
   * 
   * @return
   */
  // =======================================================
  public String getArffHeader() {
  
    return this.arffHeaderFile;
  } // End Method getArffHeader() ======================
  
   
  
} // end Class SVMContents
