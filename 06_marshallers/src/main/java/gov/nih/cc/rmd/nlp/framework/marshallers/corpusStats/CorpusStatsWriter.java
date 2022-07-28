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
 * CorpusStatsWriter writes out annotations and some context to 
 * a csv file.  If only one annotation, say SimpleConcept
 * or Symptom is given, the frequency of the type and semantic Type
 * is kept and written out.
 * 
 * 
 * 
 *        
 * @author  Guy Divita 
 * @created April 16, 2013
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.corpusStats;


import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.Concept;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;


public class CorpusStatsWriter extends JCasAnnotator_ImplBase  implements gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer  {
 
  

 
  // =======================================================
  /**
   * Constructor CorpusStatsWriter 
   *
   * @param pStatsOutputDir
   * @param pOutputTypes
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public CorpusStatsWriter( String pStatsOutputDir, String[] pOutputTypes) throws ResourceInitializationException {
    
    initialize( pStatsOutputDir, pOutputTypes, 0);
    
  } // end Constructor() ---------------------

  // =======================================================
  /**
   * Constructor CorpusStatsWriter 
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public CorpusStatsWriter(String pArgs[]) throws ResourceInitializationException {
   
    initialize( pArgs);
    
   } // end Constructor() ----------------------------------

  
  // =======================================================
  /**
   * Constructor CorpusStatsWriter 
   *
   * @param pStatsOutputDir
   * @param pOutputTypes
   * @param pServerNumber
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public CorpusStatsWriter(String pStatsOutputDir, String[] pOutputTypes, int pServerNumber) throws ResourceInitializationException {
     
    initialize( pStatsOutputDir, pOutputTypes, pServerNumber);
  }


//=======================================================
 /**
  * Constructor CorpusStatsWriter 
  *
  * @param pArgs
  * @param pServerNumber
  * @throws ResourceInitializationException 
  */
 // =======================================================
  public CorpusStatsWriter(String[] pArgs, int pServerNumber) throws ResourceInitializationException {
	  
	 initialize( pArgs, pServerNumber);
	   
  } // end Constructor() ---------------------

// -----------------------------------------
  /**
   * process 
   *   writes out the instances to a file, and accumulates
   *   stats for each file until the proces is done.
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

  
    List<Annotation> annotations = null;
    List<Annotation> documentHeaders = UIMAUtil.getAnnotations(pJCas,gov.va.chir.model.DocumentHeader.type);
    DocumentHeader documentHeader = null;
    if ( documentHeaders != null && documentHeaders.size() > 0)
      documentHeader = (DocumentHeader) documentHeaders.get(0);
   
    String documentId = "unknown";
    String documentType = "unknown";
    String referenceDate = "unknown";
    String patientSID = "unknown";
    String metaData = null;
    String otherMetaDataHeadings = null; 
    if ( documentHeader != null ){
      documentId = ((DocumentHeader) documentHeader).getDocumentId();
      documentType = ((DocumentHeader)documentHeader).getDocumentType();
      referenceDate = ((DocumentHeader)documentHeader).getReferenceDate();
      patientSID = ((DocumentHeader)documentHeader).getPatientID();
      metaData = ((DocumentHeader) documentHeader).getOtherMetaData();
      otherMetaDataHeadings = ((DocumentHeader)documentHeader).getOtherMetaDataFieldNames();
      if (otherMetaDataHeadings != null && otherMetaDataHeadings.trim().length() > 0 )
        otherMetaDataHeadings = otherMetaDataHeadings.replace('|', CSV_CHAR_DELIMITER);
    }
    if ( documentType == null || documentType.trim().length() == 0 )  documentType = "unknown";
    if ( documentId    == null ) {
      
      documentId = VUIMAUtil.getDocumentId(pJCas);
      if ( documentId == null)
        documentId = "unknown_" + U.pad(this.numberOfDocuments);
    }
     
    if ( this.firstTime ) {
      this.types = initializeTypes( pJCas);
      if ( this.types == null  ||  types.length < 1) {
        System.err.println("Error: No type specified, pass in a type via the labels command line or parameter argument" );
      } else {
        //System.err.println("The types = " + this.types[0]);
      }
    
      // ----------------------------------------------------
      // Write out the column headings of the the instance table
      try {   writeColumnHeadings(otherMetaDataHeadings); } catch (Exception e) {System.err.println(" issue writing columns");}
    
       
      this.firstTime = false;
    }
     
    if ( this.types == null ) {
      annotations = UIMAUtil.getAnnotations(pJCas);
      annotations = filterAnnotations( annotations);
    } else {
      annotations = UIMAUtil.getAnnotations(pJCas, this.types);
    }
    
 
   
    
    
    HashMap<String,Integer> totalAnnotations = new HashMap<String, Integer>(20);
//  HashMap<String, StringBuffer[]> keyHash = new HashMap<String,StringBuffer[]>(4000);
    HashSet<String>documentCategoryVector  = new HashSet<String>();
     if ( annotations.size() > 0 ) { 
    	 for ( Annotation annotation: annotations) {
     
    		 instance ( documentId, patientSID, documentType,  referenceDate, metaData, annotation);
       
    		 summarize( documentId, documentType, metaData, annotation );
    		 totalAnnotationsPerDocument( totalAnnotations, annotation);
    		 documentCategoryVector.add(getCategory(annotation));
    		// summarizeAtDocLevel( documentId, documentType, annotation);
       
    	 }  // end loop through annotations 
     } else { // file with no target annotations 
    	 emptyInstance( documentId, patientSID, documentType, referenceDate, metaData );
    	 documentCategoryVector.add("None");
       categorySet.add("None");
    	 summarize( documentId, documentType, metaData, null );
    	 
     }
     
     updateLabelHeadings( annotations);  // <--- needed for table5
     totalAnnotationsPerDocumentSummary(documentId, patientSID, documentType, referenceDate, metaData, totalAnnotations);
     updateDocumentCounts(documentCategoryVector, documentType);
     this.numberOfDocuments++;
  
  } // end Method process() ----------------


// =======================================================
  /**
   * updateDocumentCounts adds document counts of documentType|category to a documentTypeByCategoryDocFreqTable table across a corpus
   * 
   * @param pDocumentCategoryVector
   * @param pDocumentType
   */
  // =======================================================
  private void updateDocumentCounts( HashSet<String> pDocumentCategoryVector, String pDocumentType) {
   
    if ( this.documentTypeByCategoryDocFreqTable == null )  this.documentTypeByCategoryDocFreqTable = new HashMap<String,int[]>();
    for ( String documentCategory : pDocumentCategoryVector ) {
      String key = pDocumentType + "|" + documentCategory;
     
      int[] value = this.documentTypeByCategoryDocFreqTable.get( key );
     
      if ( value == null ) {
        value = new int[1]; 
        value[0] = 0;
      }
      value[0]++;
      this.documentTypeByCategoryDocFreqTable.put(key,value);
    }     
    
     
  } // End Method updateDocumentCounts() ======================
  


  


// =======================================================
  /**
   * filterAnnotations if all annotations are given, filter to 
   *   only a good set of annotations
   * 
   * @param annotations
   * @return
   */
  // =======================================================
  private List<Annotation> filterAnnotations(List<Annotation> pAnnotations) {
   
    ArrayList<Annotation> filteredAnnotations = new ArrayList<Annotation>(pAnnotations);
    String name = null;
    if ( pAnnotations != null ) {
      
      for ( Annotation annotation : pAnnotations ) {
        name = annotation.getClass().getName();
        if ( filterAnnotations( name  ) ) 
        
     
         filteredAnnotations.add( annotation);
           
     }
      
    }
    
    
    return filteredAnnotations;
    
  }  // End Method filterAnnotations() ======================
  


  // =======================================================
  /**
   * filterAnnotations filters out labels we don't want to see
   * 
   * @param name
   * @return
   */
  // =======================================================
  private boolean filterAnnotations(String pName) {
   
    boolean returnVal = false;
    if ( pName.contains("model")  &&
        !pName.contains("Token") &&
        !pName.contains("CSI")  &&
        !pName.contains("List")  &&
        !pName.contains("Relationship")  &&
        !pName.contains("ListElement")  &&
        !pName.contains("DocumentClassification") &&
        !pName.contains("DocumentHeader") &&
        !pName.contains("Delimiter") &&
        !pName.contains("VAnnotation"))
        returnVal = true;
    
    return returnVal;
    // End Method filterAnnotations() ======================
  }


  // ------------------------------------------
  /**
   * writeColumnHeadings
   *   writes out the column headings into the instance table
   * @throws Exception 
   *
   */
  // ------------------------------------------
  private void writeColumnHeadings(String pOtherMetaDataHeadings) throws Exception {
    
    
    String msg2 = 
    String.format("%1$-20s",              "DocumentId") + CSV_DELIMETER +
    String.format("%1$-10s",              "PatientSID") + CSV_DELIMETER + 
    String.format("%1$-20s",              "DocumentType") + CSV_DELIMETER +
   
    String.format("%1$-10s",              "Date") + CSV_DELIMETER +
    String.format("%1$-15s",              "Label") + CSV_DELIMETER +
    String.format("%1$-40s",              "Snippit") + CSV_DELIMETER + 
    String.format("%1$-7s",             "Assertion") + CSV_DELIMETER + 
    String.format("%1$-15s",              "category") + CSV_DELIMETER + 
    String.format("%1$-16s",              "cuis") + CSV_DELIMETER +
    String.format("%1$-20s",              "conceptNames") + CSV_DELIMETER +
    String.format("%1$-5s",               "Begin") + CSV_DELIMETER +
    String.format("%1$-5s",               "End")   + CSV_DELIMETER +
    String.format("%1$-20s",              "Section") + CSV_DELIMETER +
    String.format("%1$-8s",               "inProse") + CSV_DELIMETER +
                                          pOtherMetaDataHeadings ;
    
    /*
    String msg1 = 
    String.format("%1$-20s",                "#" + U.hr(19) + "+") + 
    String.format("%1$-20s",                      U.hr(20) + "+") + 
    String.format("%1$-15s",                      U.hr(15) + "+") +  
    String.format("%1$-40s",                      U.hr(40) + "+") +  
    String.format("%1$-7s",                       U.hr(7)  + "+") +  
    String.format("%1$-15s",                      U.hr(15) + "+") +  
    String.format("%1$-16s",                      U.hr(16) + "+") +  
    String.format("%1$-20s",                      U.hr(20) + "+") +
    String.format("%1$-5s",                       U.hr(5)  + "+") +
    
    String.format("%1$-20s",                      U.hr(20)  + "+") +
                                                  U.hr(10)  ;
    */
    if ( this.out == null )
      openFiles();
    this.out.print("Table 1: Instance Table\n");
   
   // this.out.println( msg1);
    this.out.print( msg2 + "\n");
  //  this.out.println( msg1);
    // End Method writeColumnHeadings() -----------------------
  }


// =======================================================
  /**
   * openFiles opens the instance file and the table5
   * 
   */
  // =======================================================
  private void openFiles() throws Exception {
 
    if ( this.out == null ) {
     
    
      
       try {
         this.out              = new PrintWriter( this.getCorpusInstancesFile());
         this.summaryTable5    = new PrintWriter( this.getSummaryTable5Name());
         
         System.err.println("+---------------------------------------------------------------------------------------------+");
         System.err.println("| CorpusStats now writing to " + this.getCorpusInstancesFile()  + "|" );
         System.err.println("+---------------------------------------------------------------------------------------------+");
         
         
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        String msg = "Issue with trying to open the output corpus stats files " + e.getMessage() ;
        System.err.println( msg );
        throw new Exception (msg);
      }
    }
  } // End Method openFiles() ======================
  


//------------------------------------------
/**
 * instance
 *
 *
 * @param pDocumentId
 * @param pDocumentTitle
 * @param pMetaData
 * @param pAnnotation
 */
// ------------------------------------------
private void instance(String pDocumentId, String pPatientSID, String pDocumentType, String pReferenceDate, String pMetaData, Annotation pAnnotation) {
  

  int              spanBegin = pAnnotation.getBegin();
  int                spanEnd = pAnnotation.getEnd();
  String  longAnnotationName = pAnnotation.getType().getName();
  String shortAnnotationName = longAnnotationName.substring(longAnnotationName.lastIndexOf('.') + 1);
  String             snippet = null;
  try {
    String buff = null;
    snippet = "";
    try {
      buff = pAnnotation.getCoveredText();
      snippet = U.displayForCSV(buff, CSV_DELIMETER, NOT_CSV_DELIMETER );
    } catch (Exception e) {}
    
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println("Issue with getting an annotation's text " + e.getMessage());
    System.err.println("For label " + pAnnotation.getClass().getCanonicalName());
    throw new RuntimeException( e);
  }
  
  String     category = getCategory( pAnnotation);
  String         cuis = getCuis( pAnnotation);
  String conceptNames = getConceptNames( pAnnotation);
  String  sectionName = getSectionName( pAnnotation);
  String negationStatus = null;
 
  
  try {
    negationStatus = ((VAnnotation)pAnnotation).getNegation_Status();
  } catch (Exception e ){
    try {
      negationStatus = ((Concept)pAnnotation).getAssertionStatus();
    }catch (Exception e2 ) { }
    
  }
    if ( negationStatus == null )
    negationStatus = "asserted";
  
    String metaData = "";
    if ( pMetaData != null )
       metaData = pMetaData.replace('|',  CSV_DELIMETER.charAt(0));
  
    String inProse = "true";
    try {
      inProse = String.valueOf(((Concept)pAnnotation).getInProse());
    } catch (Exception e) {
      try {
        inProse = String.valueOf(((VAnnotation)pAnnotation).getInProse());
      } catch (Exception e2) {}
    };
  
  String msg = null;
  msg = String.format("%1$-20s",  pDocumentId) + CSV_DELIMETER +
        String.format("%1$-10s",  pPatientSID) + CSV_DELIMETER + 
        String.format("%1$-20s",  pDocumentType) + CSV_DELIMETER +
        String.format("%1$-10s",  pReferenceDate) + CSV_DELIMETER +
        String.format("%1$-15s",  shortAnnotationName) + CSV_DELIMETER +
        String.format("%1$-40s",  snippet) + CSV_DELIMETER + 
        String.format("%1$-7s",   negationStatus) + CSV_DELIMETER + 
        String.format("%1$-15s",  category) + CSV_DELIMETER + 
        String.format("%1$-16s",  cuis) + CSV_DELIMETER + 
        String.format("%1$-20s",  conceptNames) + CSV_DELIMETER +
        String.format("%1$5d",    spanBegin) + CSV_DELIMETER +
        String.format("%1$5d",    spanEnd)   + CSV_DELIMETER +
        String.format("%1$-20s",  sectionName) + CSV_DELIMETER +
        String.format("%1$-8s",     inProse ) + CSV_DELIMETER + 
                                  metaData ;
   this.out.print( msg + "\n" );
   
   
  
  // End Method instance() -----------------------
}

//------------------------------------------
/**
 * emptyIninstance
 *
 *
 * @param pDocumentId
 * @param pDocumentTitle
 * @param pMetaData
 * 
 */
// ------------------------------------------
private void emptyInstance(String pDocumentId, String pPatientSID, String pDocumentType, String pReferenceDate, String pMetaData) {
  

  int              spanBegin = 0;
  int                spanEnd = 0;
  
  String shortAnnotationName = "No Annotations";
  String             snippet = "";
  
  String     category = "";
  String         cuis = "";
  String conceptNames = "";
  String  sectionName = "";
  
  String metaData = " ";
  if (pMetaData != null )
    metaData = pMetaData.replace('|',  CSV_DELIMETER.charAt(0));


  
  String msg = null;
  msg = String.format("%1$-20s",  pDocumentId) + CSV_DELIMETER +
        String.format("%1$-10s",  pPatientSID) + CSV_DELIMETER + 
        String.format("%1$-20s",  pDocumentType) + CSV_DELIMETER +
        String.format("%1$-10s",  pReferenceDate) + CSV_DELIMETER +
        String.format("%1$-15s",  shortAnnotationName) + CSV_DELIMETER +
        String.format("%1$-40s",  snippet) + CSV_DELIMETER + 
        String.format("%1$-7s",   " ") + CSV_DELIMETER + 
        String.format("%1$-15s",  category) + CSV_DELIMETER + 
        String.format("%1$-16s",  cuis) + CSV_DELIMETER + 
        String.format("%1$-20s",  conceptNames) + CSV_DELIMETER +
        String.format("%1$5d",    spanBegin) + CSV_DELIMETER +
        String.format("%1$5d",    spanEnd)   + CSV_DELIMETER +
        String.format("%1$-20s",  sectionName) + CSV_DELIMETER +
                                  metaData ;
   this.out.print( msg + "\n" );
   
  
  
  // End Method instance() -----------------------
}
//------------------------------------------
/**
* summarize
*  keeps a tally of documentTitles x categories - both by class and instances
*
*  N.B. We'll see if this causes too much overhead - it shouldn't, in theory.
*  
* @param pDocumentId
* @param pDocumentTitle
* @param metaData
* @param pAnnotation
*/
//------------------------------------------
private void summarize(String pDocumentId, String pDocumentTitle, String metaData, Annotation pAnnotation) {


  String aCategory = null;
  if ( pAnnotation == null ) {
    aCategory = "None";
  } else {
     aCategory = getCategory( pAnnotation);  
  }
  

@SuppressWarnings("unchecked")
HashMap<String,Integer> documentTypes =  this.documentTitleHash.get( pDocumentTitle);

if ( documentTypes == null) documentTypes = new HashMap<String,Integer>();

Integer freq = documentTypes.get( aCategory );
if ( freq == null)  freq = 0;
freq++;
documentTypes.put( aCategory, freq);

this.documentTitleHash.put( pDocumentTitle, documentTypes);



}  // End Method summarize() -----------------------

//------------------------------------------
/**
* totalAnnotationsPerDocument
*  keeps a tally of documentTitles x categories - both by class and instances
*
*  N.B. We'll see if this causes too much overhead - it shouldn't, in theory.
*  
* @param pDocumentId
* @param pDocumentTitle
* @param metaData
* @param pAnnotation
*/
//------------------------------------------
private void totalAnnotationsPerDocument(  HashMap<String,Integer>annotationFreq, Annotation pAnnotation ) {


String aCategory = getCategory( pAnnotation);

Integer freq = annotationFreq.get( aCategory);

if ( freq == null)
  freq = new Integer(0);
freq++;
annotationFreq.put( aCategory, freq);


}  // End Method summarize() -----------------------


  // =======================================================
/**
 * totalAnnotationsPerDocumentSummary [Summary here]
 * 
 * @param documentId
 * @param patientSID
 * @param documentType
 * @param referenceDate
 * @param metaData
 * @param totalAnnotations
 */
// =======================================================
private void totalAnnotationsPerDocumentSummary(String pDocumentId, String pPatientSID, String pDocumentType,
    String pReferenceDate, String pMetaData, HashMap<String, Integer> pTotalAnnotations) {

  String    metaData = pMetaData;

 
  String annotationFrequencies = formatAnnotationFrequencies( pTotalAnnotations);
  
  String msg = null;
  msg = String.format("%1$-21s",  pDocumentId) + CSV_DELIMETER +
        String.format("%1$-10s",  pPatientSID) + CSV_DELIMETER + 
        String.format("%1$-20s",  pDocumentType) + CSV_DELIMETER +
        String.format("%1$-13s",  pReferenceDate) + CSV_DELIMETER +
        annotationFrequencies + CSV_DELIMETER +
        metaData ;
   this.summaryTable5.print( msg + "\n" );
   this.summaryTable5.flush();
     // End Method totalAnnotationsPerDocumentSummary() ======================
}


// =======================================================
/**
 * formatAnnotationFrequencies 
 * 
 * @param pTotalAnnotations
 * @return
 */
// =======================================================
private String formatAnnotationFrequencies(HashMap<String, Integer> pTotalAnnotations) {
  
  StringBuffer buff = new StringBuffer();
  
  
  int totalFreq = getTotalFreq( pTotalAnnotations);
  
  buff.append( String.format("%1$20d", totalFreq) );
  buff.append(CSV_DELIMETER);
  for (int i = 0; i < this.labelHeadings.size(); i++) {
    
    Integer value = pTotalAnnotations.get(this.labelHeadings.get(i));
    if (value == null) value = 0;
    buff.append ( String.format("%1$20d", value));
    if ( i < this.labelHeadings.size() )
      buff.append(CSV_DELIMETER);
    
    
  } // end loop thru labels
  
  
  return buff.toString();
} // End Method formatAnnotationFrequencies() ======================



// =======================================================
/**
 * getTotalFreq returns the total frequency for this document
 * 
 * @param pTotalAnnotations
 * @return int
 */
// =======================================================
private int getTotalFreq(HashMap<String, Integer> pTotalAnnotations) {
  int freq = 0;
  for ( int i = 0; i < this.labelHeadings.size(); i++ ) {
      Integer freqz = pTotalAnnotations.get( this.labelHeadings.get(i));
      if ( freqz != null )
        freq+= freqz;
    }
  return freq;
}  // End Method getTotalFreq() ======================



// =======================================================
/**
 * updateLabelHeadings adds to the label headings if a new
 * label is seen from the list of annotation instances
 * 
 * This method updates the class variable this.labelHeadings
 * 
 * @param pAnnotations
 */
// =======================================================
private void updateLabelHeadings(List<Annotation> pAnnotations) {

 
  if ( pAnnotations != null )
    for ( Annotation annotation : pAnnotations ) {
      String label = annotation.getClass().getSimpleName();
    
      if ( !this.seenLabels.contains( label) ) {
          this.seenLabels.add( label);
          this.labelHeadings.add( label);
      }
    } //end loop through instances
}  // End Method updateLabelHeadings() ======================



// ------------------------------------------
/**
 * getCuis returns a colon separated list of cuis if they exist
 *
 * @param pAnnotation
 * @return String (colon separated)
 */
// ------------------------------------------
private String getCuis(Annotation pAnnotation) {
  String returnValue = null;
  String cuis = null;
  try {
    cuis = ((gov.va.vinci.model.Concept) pAnnotation).getCuis();
    
  } catch ( Exception e1) {
    try {
      cuis = ((gov.va.chir.model.CodedEntry) pAnnotation).getCodeCode();
    } catch (Exception e3 ) {
      ;;
    };
  }
  if ( cuis!= null )
    returnValue = cuis.replace('|', ':');
  
  return returnValue;
  
  // End Method getCuis() -----------------------
}

// ------------------------------------------
/**
 * getConceptNames returns a colon separated list of concept names if they exist
 *
 * @param pAnnotation
 * @return String (colon separated)
 */
// ------------------------------------------
private String getConceptNames(Annotation pAnnotation) {
  String returnValue = null;
  String conceptNames = null;
  try {
    conceptNames = ((gov.va.vinci.model.Concept) pAnnotation).getConceptNames();
    
  } catch ( Exception e1) {
    try {
      conceptNames = ((gov.va.chir.model.CodedEntry) pAnnotation).getDisplayName();
    } catch (Exception e3 ) {
      ;;
    };
  }
  if ( conceptNames!= null )
    returnValue = conceptNames.replace('|', ':');
  
  return returnValue;
  
  // End Method getCuis() -----------------------
}


//------------------------------------------
/**
* getSectionName returns the section the annotation was found in
*
* @param pAnnotation
* @return String 
*/
//------------------------------------------
private String getSectionName( Annotation pAnnotation) {

String sectionName = null;
try {
 sectionName = VUIMAUtil.getSectionName( pAnnotation);
 
} catch ( Exception e1) {

}

return sectionName;

// End Method getCuis() -----------------------
}

  // ------------------------------------------
/**
 * getCategory retrieves the first level category
 *  semantic Type if one exists for this annotation
 *
 *
 * @param pAnnotation
 * @return String (null if not)
 */
// ------------------------------------------
private String getCategory(Annotation pAnnotation) {
 
  String category = null;
 
  try {
    category = ((gov.va.vinci.model.Concept) pAnnotation).getCategories();
    
  } catch ( Exception e1) {
      try {
      
        StringArray semanticTypez = ((gov.va.chir.model.ClinicalStatement) pAnnotation).getSemanticTypez();
        category = UIMAUtil.stringArrayToString(semanticTypez);
 
      } catch (Exception e2) { 
          try {
        
            StringArray semanticTypez = ((gov.va.chir.model.CodedEntry) pAnnotation).getSemanticType();
            category = UIMAUtil.stringArrayToString(semanticTypez);
     
          } catch (Exception e3 ) { };
      }
  }
  // -------------------------------------
  // Use annotation label as the category
  if ( category == null) {
    String longAnnotationName = pAnnotation.getClass().getName();
    category = longAnnotationName.substring(longAnnotationName.lastIndexOf('.') + 1);
  } 
  
  if ( category != null )
    
   if ( ! categorySet.contains( category)  ) categorySet.add( category); 
  
  return category;
      
 
 } // End Method getCategory() -----------------------



  

  //-----------------------------------------
  /**
   * initializeTypes transforms the label strings
   * to org.uima.cas.types 
   * 
   * @param pJCas
   */
  // -----------------------------------------
  private Type[] initializeTypes(JCas pJCas) {
   
    Type[] clabelTypes = null;
    List<Type> labelTypes = new ArrayList<Type>();
    if ( this.firstTime ) {
   
      if ( this.labelsToIndex != null && this.labelsToIndex.length > 0) {
     
        for ( String label: this.labelsToIndex ) {
          
          if ( label != null && label.length() > 0 && !label.contains("TOP")  && !label.contains("MetaData")) {
            Type aType = null;
            try {
              aType = UIMAUtil.getAnnotationType(pJCas, label );
              /*
              if ( aType == null )
                aType = UIMAUtil.getLabelType( pJCas, "gov.va.vinci.model." + label);
            
              if ( aType == null )
                aType = UIMAUtil.getLabelType( pJCas, "gov.va.chir.model." + label);
            */
              if ( aType != null) {
                labelTypes.add( aType);
                this.labelHeadings.add( label );
                if ( label.equals(this.focusColumnName))
                  this.focusColumnNumber = this.labelHeadings.size();
              }
            } catch (Exception e) {
              e.printStackTrace();
              String msg1 = "Error: Issue with initializeTypes " + e.getMessage();
              System.err.println(msg1);
           
            }
          } else {
           //  System.err.println("Empty label? ");
          }
          
    
        } // end Loop through labels
        clabelTypes = labelTypes.toArray(new Type[ labelTypes.size()]);
       
      }  // end if there are any labels to index
      
      if ( clabelTypes == null || clabelTypes.length == 0) {
        // figure out all labels
       // System.err.println("Cataloging all types");
        Iterator<Type> ti = pJCas.getTypeSystem().getTypeIterator();
        
        ArrayList<String> mLabelsToIndex = new ArrayList<String>();
        while ( ti.hasNext() ) {
          Type aType = ti.next();
          if ( aType != null && aType.getName() != null &&filterAnnotations ( aType.getName() ) ) {
           labelTypes.add( aType);
           mLabelsToIndex.add( aType.getShortName());
           
        }
        if ( mLabelsToIndex != null)
          this.labelsToIndex = mLabelsToIndex.toArray(new String[mLabelsToIndex.size()]);
       
          
    
        } // end Loop through labels
        clabelTypes = labelTypes.toArray(new Type[ labelTypes.size()]);
        
       
        
      }  // end if there are any labels to index
      
      this.firstTime = false;
  
    } // end if firstTime
    return clabelTypes;
  } // end Method initializeTypes() ----------
  

 
  // -----------------------------------------
  /** 
   * destroy closes the open instance freq table
   *         and writes out a summary table
   * 
   *
   */
  // -----------------------------------------
  @Override
  public void destroy() {
 
    
    System.err.println("In destroy " + this.instanceCounter);
    if ( this.out != null ) {
      this.out.close();
    
      //writeSummeryTable5();
        
     // writeSummaryTable6( );
    
    
    
      // ----------------------
      // sort and write out the summary tables
    
      try {
          writeSummaryTable2();
          writeSummaryTable3();
          writeSummaryTable4();
          writeSummaryTable7();
      } catch ( Exception e)  {
        String msg1 = "Error: writing out the summarization tables: " + e.getMessage() + "\n" ;
        String msg2 = U.getStackTrace(e) + "\n";
        System.err.println( msg1 + msg2 );
     
      }
    }
    
    super.destroy();
  } // end Method Destroy() -----------------



  // ------------------------------------------
  /**
   * writeSummaryTable writes out a table with
   *   Number of documents
   *   Number of Concept Instances
   *   Number of Classes
   *   Number of DocumentTypes
   * @throws RuntimeException 
   *
   *
   */
  // ------------------------------------------
  private void writeSummaryTable2() throws Exception {
    
    System.err.println("In writeSummaryTable2 " + this.instanceCounter);
    PrintWriter summaryTable1 = null;
    this.numberOfClasses = this.categorySet.size();
    try {
      this.summaryTable2Name = this.outputStatsDir + "/statsTable2_" + this.instanceCounter + ".csv";
      summaryTable1 = new PrintWriter( summaryTable2Name);
   
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      String msg1 = "Error: Issue with creating the statsTable2 " + e.getMessage(); 
      System.err.println(msg1  );
      throw new RuntimeException( msg1  );
    }
    
    
    
    int numberOfDocumentTypes = this.documentTitleHash.size();
   
    summaryTable1.print("Summary Table 2\n");
    summaryTable1.print("Categories" + CSV_DELIMETER + "Frequency\n");
  
    summaryTable1.print("Number of Documents    " + CSV_DELIMETER + String.format("%1$14d",this.numberOfDocuments) + "\n");
   // summaryTable1.print("Number of classes      " + CSV_DELIMETER + String.format("%1$14d",this.numberOfClasses) + "\n");
   // summaryTable1.print("Number of DocumentTypes" + CSV_DELIMETER + String.format("%1$14d", numberOfDocumentTypes) + "\n");
    
  
    summaryTable1.close();
    
    // End Method writeSummaryTable1() -----------------------
  }


  // ------------------------------------------
  /**
   * writeSummaryTable2 writes out the classes x total documents table
   * @throws Exception 
   *
   *
   */
  // ------------------------------------------
  private void writeSummaryTable3() throws Exception {
    PrintWriter table = null;
    
    this.summaryTable3Name = this.outputStatsDir + "/statsTable3_" + this.instanceCounter + ".csv";
    
    try {
      table = new PrintWriter( this.summaryTable3Name);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      String msg1 = "Error: Issue with creating the statsTable3 " + e.getMessage(); 
      System.err.println(msg1 );
      throw new RuntimeException( msg1 );
     
    }
    String[] pClassTypes = getClassTypes();
    
    
    // ---------------------------------
    // Create a classTypeHash filled with instance and doc freqs
    HashMap<String,Integer>classTypeHash = calculateClassTypesFromDocumentHash( );
    HashMap<String,Integer>classDocFreqHash = calculateClassDocFromDocumentHash( );
    
    
 
    table.print("Table 3:  Categories" + "\n");
    
    // String msg1 = "+" + U.hr(19)                         + "+" + U.hr(40) +                            "+" ;
    String msg2 =  String.format("%1$-20s","Classes") + CSV_DELIMETER + String.format("%1$-40s", "Total Instances" ) + CSV_DELIMETER + String.format("%1$-40s",  "Total Documents") ; 
    // table.println( msg1 );
    table.print( msg2 + "\n" );
    // table.println( msg1 );

    
    int mTotalClassTypeFreq = 0;
    int mTotalDocFreq = 0;
    for (String classType : pClassTypes ) {
      int totalClassTypeFreq = 0;
      int totalClassDocFreq = 0;
      if ( classType != null ) {
        totalClassTypeFreq = classTypeHash.get( classType ); 
        totalClassDocFreq = classDocFreqHash.get( classType);
      }
        
      
      table.print(  String.format("%1$-20s", classType) + CSV_DELIMETER + String.format("%1$12d", totalClassTypeFreq )  + CSV_DELIMETER + String.format("%1$12d",  totalClassDocFreq) + "\n"); 
      mTotalClassTypeFreq += totalClassTypeFreq;
      mTotalDocFreq+=totalClassDocFreq;
      
    } // end loop through the class types
    table.print(  String.format("%1$-20s", "Total")              + CSV_DELIMETER + 
                   String.format("%1$12d", mTotalClassTypeFreq ) + CSV_DELIMETER + 
                   String.format("%1$12d", mTotalDocFreq       )  +   "\n"); 
    
    table.close();
    
    
    
  } 


  // End Method writeSummaryTable2() -----------------------
  


  // ------------------------------------------
  /**
   * writeSummaryTable3 writes out the table classInstances x document type
   *
   *
   */
  // ------------------------------------------
  private void writeSummaryTable4() throws Exception {
 PrintWriter table = null;
    
 
 this.summaryTable4Name = this.outputStatsDir + "/statsTable4_" + this.instanceCounter + ".csv";
 
 
    try {
      table = new PrintWriter( this.summaryTable4Name);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      String msg = "Error: Issue with creating the statsTable 4 " + e.getMessage(); 
      System.err.println(msg);
      throw new RuntimeException( msg);
     
    }
  
    // --------------------------------
    // Initialization
    // --------------------------------
    String[]   docTypes = getDocTypes(); 
    String[] categories = getClassTypes();
    HashMap<String,Integer>  catDocTypeHash = new HashMap<String,Integer>();
    
    // ------------------------------------------------
    // Table Header
    // ------------------------------------------------
    table.print("Table 4:  Document types by categories\n" );
        
       
    table.print(  String.format("%1$-40s",  "Document Types") + CSV_DELIMETER );
    
    table.print(CSV_DELIMETER  );
    
    // -----------------------------------------------------------
    // Print out the class headings
    for ( String category: categories ) {
      table.print(  String.format("%1$-15s" + CSV_DELIMETER , category)) ;
    }
    table.print(     String.format("%1$-15s" + CSV_DELIMETER, "Total"));
    table.print("\n");
  

    // ----------------------------------------
    // Table row content
    Integer  docCatFreq = 0;
   
  
      for ( String docType : docTypes ) {
    
        int totalDocCatFreq = 0;
        table.print( String.format( "%1$-40s"+ CSV_DELIMETER, docType ));      

        
        for ( String category: categories ) { // ---------------------------------------------------------------
        
        
          docCatFreq = 0;
          @SuppressWarnings("unchecked")
          HashMap<String, Integer> categoryHash = this.documentTitleHash.get( docType);
          if ( categoryHash != null)
            docCatFreq = categoryHash.get(category);
          if ( docCatFreq == null)
            docCatFreq = 0;
      
      
          table.print(  String.format( "%1$15d" + CSV_DELIMETER, docCatFreq) );
         
          
          Integer totalCatDocFreq = catDocTypeHash.get( category );
        
          if ( totalCatDocFreq == null ) totalCatDocFreq = 0;
            totalCatDocFreq+=docCatFreq;
          catDocTypeHash.put(category, totalCatDocFreq);
        
          totalDocCatFreq+= docCatFreq;
        } // end loop through categories  ----------------------------------------------------------------------
        table.print( String.format( "%1$15d" + CSV_DELIMETER, totalDocCatFreq));
        table.print("\n");
        
        
      } // end loop through the categories
    
    // -------------------------------------
    // Print the last total row
    table.print(  String.format("%1$-20s" + CSV_DELIMETER , "Total")) ;
    int completeTotal = 0;
    for ( String category : categories ) {
      Integer totalCatDocFreq = catDocTypeHash.get( category );
      if ( totalCatDocFreq == null) totalCatDocFreq = 1;
      table.print( String.format( "%1$15d" + CSV_DELIMETER , totalCatDocFreq));
      completeTotal+=totalCatDocFreq;
      
    } // end loop through docType totals
    table.print( String.format( "%1$15d" + CSV_DELIMETER, completeTotal)); 
    table.print("\n");
   
    
    // -----------------------------
    // close the file
    table.close();
    
    // End Method writeSummaryTable4() -----------------------
  }


  // ------------------------------------------
  /**
   * writeSummaryTable7 writes out the table document type by category for document counts
   *
   *
   */
  // ------------------------------------------
  private void writeSummaryTable7() throws Exception {
 PrintWriter table = null;
    
   this.summaryTable7Name = this.outputStatsDir + "/statsTable7_" + this.instanceCounter + ".csv";
 
 
    try {
      table = new PrintWriter( this.summaryTable7Name);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      String msg = "Error: Issue with creating the statsTable 7 " + e.getMessage(); 
      System.err.println(msg);
      throw new RuntimeException( msg);
     
    }
  
    // --------------------------------
    // Initialization
    // --------------------------------
    String[]   docTypes = getDocTypes(); 
    String[] categories = getClassTypes();
    HashMap<String,Integer>  catDocTypeHash = new HashMap<String,Integer>();
    
    // ------------------------------------------------
    // Table Header
    // ------------------------------------------------
    table.print("Table 7:  Document types by categories\n" );
        
       
    table.print(  String.format("%1$-40s",  "Document Types") + CSV_DELIMETER );
    
    table.print(CSV_DELIMETER  );
    
    // -----------------------------------------------------------
    // Print out the class headings
    for ( String category: categories ) {
      table.print(  String.format("%1$-15s" + CSV_DELIMETER , category)) ;
    }
    table.print(     String.format("%1$-15s" + CSV_DELIMETER, "Total"));
    table.print("\n");
  

    // ----------------------------------------
    // Table row content
    Integer  docCatFreq = 0;
   
  
      for ( String docType : docTypes ) {
    
        int totalDocCatFreq = 0;
        table.print( String.format( "%1$-40s"+ CSV_DELIMETER, docType ));      

        
        for ( String category: categories ) { // ---------------------------------------------------------------
        
        
          docCatFreq = 0;
          String key = docType + "|" + category;
          int[] freq = this.documentTypeByCategoryDocFreqTable.get(key);
          if ( freq != null )
            docCatFreq = freq[0];
         
      
          table.print(  String.format( "%1$15d" + CSV_DELIMETER, docCatFreq) );
         
          
          Integer totalCatDocFreq = catDocTypeHash.get( category );
        
          if ( totalCatDocFreq == null ) totalCatDocFreq = 0;
            totalCatDocFreq+=docCatFreq;
          catDocTypeHash.put(category, totalCatDocFreq);
        
          totalDocCatFreq+= docCatFreq;
        } // end loop through categories  ----------------------------------------------------------------------
        table.print( String.format( "%1$15d" + CSV_DELIMETER, totalDocCatFreq));
        table.print("\n");
        
        
      } // end loop through the categories
    
    // -------------------------------------
    // Print the last total row
    table.print(  String.format("%1$-20s" + CSV_DELIMETER , "Total")) ;
    int completeTotal = 0;
    for ( String category : categories ) {
      Integer totalCatDocFreq = catDocTypeHash.get( category );
      if ( totalCatDocFreq == null) totalCatDocFreq = 1;
      table.print( String.format( "%1$15d" + CSV_DELIMETER , totalCatDocFreq));
      completeTotal+=totalCatDocFreq;
      
    } // end loop through docType totals
    table.print( String.format( "%1$15d" + CSV_DELIMETER, completeTotal)); 
    table.print("\n");
   
    
    // -----------------------------
    // close the file
    table.close();
    
    // End Method writeSummaryTable7() -----------------------
  }


  // =======================================================
  /**
   * writeSummeryTable5 
   *    close table 5.
   *    create a header
   *    open a new file up
   *    write the header
   *    read the contents of the closed table 5
   *    write the contents of the closed table 5
   * 
   */
  // =======================================================
  private void writeSummeryTable5() {
    
    this.summaryTable5.close();
    String summaryTable5NameTemp  = this.outputStatsDir + "/statsTable5_" + this.instanceCounter + ".csv.tmp";
    String summaryTable5Name      = this.outputStatsDir + "/statsTable5_" + this.instanceCounter + ".csv";
    
    File aFile = new File( summaryTable5NameTemp );
    
    if ( aFile.exists() ) {
    
      try {
        PrintWriter mOut = new PrintWriter(summaryTable5Name );
        writeAnnotationFrequencyHeadings( mOut );
    
        String table5Contents = U.readFile( summaryTable5NameTemp);
        mOut.print( table5Contents);
        mOut.close();
      
        //  U.deleteFile(summaryTable5NameTemp);
      
      } catch (Exception e) {
        e.printStackTrace();
        String msg = "Issue with updating table 5 " + e.getMessage();
        System.err.println(msg);
      }
 
    }  // end if there is a file to process 
  }  // End Method writeSummeryTable5() ======================
  


  // =======================================================
  /**
   * writeSummaryTable6  re-sorts table 5 by most freq subtype,
   * then creates a file that is a script to call vtt on the top
   * 20 of those files.
   * @throws Exception 
   * 
   */
  // =======================================================
  @SuppressWarnings("unchecked")
  private void writeSummaryTable6( )  {
   
    try {
    
     String inputDir = this.outputStatsDir + "/../corpus/";
     File aDir = new File(inputDir);
     if (aDir.exists()) {
     
       int fieldOfInterest =  this.focusColumnNumber;
       String summaryTable5NameTemp  = this.outputStatsDir + "/statsTable5_" + this.instanceCounter + ".csv.tmp";
       String table5Contents = U.readFile( summaryTable5NameTemp);
       
       String rows[] = U.split(table5Contents, "\n");
    
    
       FieldComparitor fieldComparitor = new FieldComparitor(fieldOfInterest);
       if (rows != null) {
         Arrays.sort(rows, fieldComparitor);
    
         // -----------------------------
         // Print out table 6
         String summaryTable6Name      = this.outputStatsDir + "/highYieldDocuments_" + this.instanceCounter + ".csv";
         String reviewScript           = this.outputStatsDir + "/highYieldDocumentsToReview_" + this.instanceCounter + ".sh";
   
   
         PrintWriter mOut = new PrintWriter(summaryTable6Name );
         PrintWriter mReviewOut = new PrintWriter( reviewScript);
         writeAnnotationFrequencyHeadings( mOut );
    
         for ( int i = 0; i < rows.length; i++)  
           mOut.print( rows[i] + '\n');
         mOut.close();
      
         //  U.deleteFile(summaryTable5NameTemp);
      
         // -------------------------------
         // print out a script that kicks off vtt on the top 10 files
    
      
         int sample = 20;
         if ( sample > rows.length ) sample = rows.length;
         for ( int i = 0; i < sample; i++ ) {
      
          if ( rows[i] != null) {
            String[] cols = U.split(rows[i], CSV_DELIMETER );
            
            if ( cols != null &&  cols[0] != null && cols[0].length() > 0 ) {
              
              
            // -> not working!  copyFileToReviewDir( this.outputStatsDir + "/../corpus/" + cols[0], this.outputStatsDir + "/forReview" );   
              String fileName = cols[0].trim() + ".vtt";
              mReviewOut.print("vtt " + fileName.trim()  + '\n');
            }
          }
         }
         mReviewOut.close();
       } // end if there are any rows  
     }
    } catch (Exception e) {
        e.printStackTrace();
        String msg = "Issue with making high yield table " + e.getMessage();
        System.err.println(msg);
    }
    
    
    
    
  } // End Method writeTable6() ======================
  


  // =======================================================
  /**
   * copyFileToReviewDir copies the file to the directory.
   * 
   * @param pFileName (it's not an absolute path)
   * @param pDir      (it's an absolute path, but may not exist yet)
   * @throws Exception 
   */
  // =======================================================
  private void copyFileToReviewDir(String pFileName, String pDir) throws Exception {
    
    try {
      File aDir = new File( pDir);
      if ( !aDir.exists())
        U.mkDir(pDir);
      
      String fileName = null;
      try {
        fileName = pFileName.substring( pFileName.lastIndexOf("/"));
      } catch (Exception e) {
       fileName = pFileName.substring(pFileName.lastIndexOf('\\'));
      }
      if ( fileName == null ) fileName = pFileName;
      
      Path source = Paths.get(this.outputStatsDir + "/../corpus/" + fileName.trim());
      Path target = Paths.get( pDir + "/" + fileName.trim() ) ;
      
      Files.copy(source, target, REPLACE_EXISTING);
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue copying the file " + e.toString();
      System.err.println(msg);
      // throw new Exception (msg);
    }
      
      
    
    // End Method copyFileToReviewDir() ======================
  }


  // =======================================================
  /**
   * writeAnnotationFrequencyHeadings returns the headings for the categories
   * @param annotations 
   * 
   * @return String
   */
  // =======================================================
  private void writeAnnotationFrequencyHeadings( PrintWriter pOut ) {
   
    StringBuffer buff = new StringBuffer();
    
    buff.append("Table of document x label frequency\n");
    
    buff.append(       String.format("%1$-20s",  "DocumentId" )           + CSV_DELIMETER +
                       String.format("%1$-10s",  "PatientSID" )           + CSV_DELIMETER + 
                       String.format("%1$-20s",  "DocumentType")          + CSV_DELIMETER +
                       String.format("%1$-10s",  "Date")                  + CSV_DELIMETER +
                       String.format("%1$-20s",  "Total freq")            + CSV_DELIMETER);
     
    
    
  
    for ( int i = 0; i < this.labelHeadings.size(); i++) {
      
      String label = this.labelHeadings.get(i);
      if ( label.length() > 19)
        label = label.substring(0, 19);
      buff.append ( String.format("%1$-20s", label));
      buff.append(CSV_DELIMETER);
      
      
    } // end loop thru labels
    
   
    pOut.print( buff.toString() + "\n");
   
    this.summaryTable5.flush();
    // System.err.println(buff);
  }  // End Method formatAnnotationFrequencyHeadings() ======================


  // ------------------------------------------
  /**
   * getDocTypes returns a set of document types
   * (sorted alphabetically).
   *
   * @return
   */
  // ------------------------------------------
  private String[] getDocTypes() {
    
    if ( this.docTypes == null ) {
      Set<String> keys = this.documentTitleHash.keySet();
      this.docTypes = keys.toArray( new String[ keys.size()]);
      Arrays.sort(this.docTypes);
    
    } 
    return this.docTypes;
    
  }  // End Method getDocTypes() -----------------------
  


  // ------------------------------------------
    /**
     * getClassTypes returns an array of class types
     *
     *
     * @return
     */
    // ------------------------------------------
    private String[] getClassTypes() {
   // ---------------------------------
      // Sort alphabetically - should be by frequency - but that's complicated to do
      if ( this.classTypes == null ) {
        this.classTypes = (String[] ) this.categorySet.toArray(new String[this.categorySet.size()]);
        Arrays.sort(this.classTypes);
      }
      return this.classTypes;
      
      // End Method getClassTypes() -----------------------
    }


  // ------------------------------------------
  /**
   * calculateClassTypesFromDocumentHash iterates through the document hash
   * and accumulates the categories 
   *
   *
   * @return HashMap<String, Integer>
   */
  // ------------------------------------------
  private HashMap<String, Integer> calculateClassTypesFromDocumentHash() {
   
    HashMap<String, Integer> classTypeHash = new HashMap<String, Integer>();
    
    Set<String> docTypeKeys = this.documentTitleHash.keySet();
    
    for ( String docTypeKey: docTypeKeys) {
      @SuppressWarnings("unchecked")
      HashMap<String, Integer> categoriesForDocumentType = this.documentTitleHash.get( docTypeKey);
      
      Set<String> categoryKeys = categoriesForDocumentType.keySet();
      
      for ( String categoryKey : categoryKeys ) {
        
        Integer totalFreq = classTypeHash.get( categoryKey);
        if (totalFreq == null )
          totalFreq = 0;
        
        Integer freq = categoriesForDocumentType.get(categoryKey);
        totalFreq+= freq;
        
        classTypeHash.put( categoryKey, totalFreq);
      } // end loop through categories
      
    } // end loop through document types
    
    
    return classTypeHash;
  }  // End Method calculateClassTypesFromDocumentHash() -----------------------
  


  // ------------------------------------------
  /**
   * calculateClassDocFromDocumentHash iterates through the document hash
   * and accumulates the categories by doc freq
   *
   *
   * @return HashMap<String, Integer>
   */
  // ------------------------------------------
  private HashMap<String, Integer> calculateClassDocFromDocumentHash() {
   
    HashMap<String, Integer> classTypeHash = new HashMap<String, Integer>();
    
  
    String[]   docTypes = getDocTypes(); 
    String[] categories = getClassTypes();
    
    
       
     for ( String categoryKey : categories ) {
      
       int totalCatFreq = 0;
       for ( String docTypeKey: docTypes) {
         
         String docCatKey = docTypeKey + "|" + categoryKey ;
      
         int docCatFreq = 0;
         int[] freq = this.documentTypeByCategoryDocFreqTable.get(docCatKey);
         if ( freq != null )
           docCatFreq = freq[0];
         totalCatFreq+= docCatFreq;
       } // end loop through document types
       classTypeHash.put( categoryKey, totalCatFreq);
      } // end loop through categories
 
    return classTypeHash;
  }  // End Method calculateClassTypesFromDocumentHash() -----------------------
  

//----------------------------------
  /**
   * initialize looking for  
   *      --outputDir= to add a /stats dir to
   *      --outputTypes=  colon delmited list of labels (no namespaces), if null, all labels will print
   *     Note that prior versions of this class used "label" rather than --outputTypes
   * @param pArgs
   * @param pServerNumber
   *
   * 
   **/
  // ----------------------------------
  public void initialize( String pArgs[] ) throws ResourceInitializationException {
	  initialize( pArgs, 0);
  
  }
  
  
  //----------------------------------
  /**
   * initialize looking for  
   *      --outputDir= to add a /stats dir to
   *      --outputTypes=  colon delmited list of labels (no namespaces), if null, all labels will print
   *     Note that prior versions of this class used "label" rather than --outputTypes
   * @param pArgs
   * @param pServerNumber
   *
   * 
   **/
  // ----------------------------------
  public void initialize( String pArgs[], int pServerNumber) throws ResourceInitializationException {
    
    
    String        outputDir = U.getOption(pArgs,  "--outputDir=",   "/someDir" + "/stats" ); 
    String      outputTypez = U.getOption(pArgs,  "--outputTypes=", "");  // if null, all, delmit by colons
    String focusColumnName_ = U.getOption(pArgs, "--focusColumn=" , "Concept" ); 
    String[] outputTypes = U.split( outputTypez, ":");
   
    initialize(  outputDir, outputTypes, pServerNumber, focusColumnName_) ; 
   
    
  } // end Method initialize() -------
  
//----------------------------------
  /**
   * initialize loads in the resources needed for phrase chunking. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
    
 String[] args = null;
    
    if ( aContext != null ) {
     args = (String[])aContext.getConfigParameterValue("args");
    }
    initialize(  args ); 
    
    
   
   
    
  } // end Method initialize() -------
  
  
  // ------------------------------------------
  /**
   * initialize called from another mechanism besides a classic writer, like within the knowtator listener.
   * 
   *
   *
   * @param ppInputDir
   * @param pOutStatsDir
   * @param pOutputTypes
   * @param pServerNumber 
   *
   */
  // ------------------------------------------
  @SuppressWarnings("rawtypes")
  public void initialize( String pOutStatsDir, String[] pOutputTypes, int pServerNumber )  throws ResourceInitializationException {
   
	  initialize( pOutStatsDir, pOutputTypes, pServerNumber, "Concept");
  }
  
  // ------------------------------------------
  /**
   * initialize called from another mechanism besides a classic writer, like within the knowtator listener.
   * 
   *
   *
   * @param ppInputDir
   * @param pOutputDir
   * @param pOutputTypes
   * @param pServerNumber 
   * @param pFocusColumnName
   */
  // ------------------------------------------
  @SuppressWarnings("rawtypes")
  public void initialize( String pOutputDir, String[] pOutputTypes, int pServerNumber, String pFocusColumnName)  throws ResourceInitializationException {
   
    this.outputStatsDir       = pOutputDir + "/stats";
    this.instanceCounter      = pServerNumber;
    this.corpusInstancesFile  = this.outputStatsDir + "/corpusInstances_" + this.instanceCounter +  ".csv";
    this.focusColumnName      = pFocusColumnName;  
    
    
    synchronized( this ) {
      while ( new File(this.corpusInstancesFile).exists() ) { 
        this.instanceCounter++;
        this.corpusInstancesFile = this.outputStatsDir + "/corpusInstances_" + this.instanceCounter +  ".csv";
       // System.err.println(" the next name = " + this.corpusInstancesFile );
      }
    }
    this.summaryTable5Name= this.outputStatsDir + "/statsTable5_" + this.instanceCounter + ".csv.tmp";
    
    System.err.println("         - the this.instanceCounter = " + this.instanceCounter + " --");
   
    try {
      U.mkDir(this.outputStatsDir);
    } catch (Exception e1) {
      e1.getStackTrace();
      String msg = "Issue with making the stats dir \n" + e1.getMessage() ;
      System.err.println( msg );
      throw new ResourceInitializationException ();
    }
    //String uid                = UUID.randomUUID().toString();
  
    
    
    try {
      openFiles();
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with opening up the instance tables " + e.getMessage();
      System.err.println( msg );
      throw new ResourceInitializationException ();
    }
    
      
   
    
    
    this.firstTime         = true;
    this.documentTitleHash = new HashMap<String, HashMap>();
    this.categorySet       = new HashSet<String>();
    this.numberOfDocuments = 0;
    this.labelsToIndex     = pOutputTypes;
    
    if ( this.labelsToIndex != null )
      if ( this.labelsToIndex.length == 0)
        this.labelsToIndex = null;
    this.seenLabels    = new HashSet<String>();
    this.labelHeadings = new ArrayList<String>();
    
    
  }  // End Method initialize() -----------------------
  
  



  /**
   * @return the corpusInstancesFile
   */
  public String getCorpusInstancesFile() {
    return corpusInstancesFile;
  }


  /**
   * @param corpusInstancesFile the corpusInstancesFile to set
   */
  public void setCorpusInstancesFile(String corpusInstancesFile) {
    this.corpusInstancesFile = corpusInstancesFile;
  }





  // =======================================================
  /**
   * getSummaryTable5Name 
   * 
   * @return String
   */
  // =======================================================
  public String getSummaryTable5Name() {
    return summaryTable5Name;
  }


  // =======================================================
  /**
   * getSummaryTable3Name 
   * 
   * @return String
   */
  // =======================================================
  public String getSummaryTable3Name() {
    return summaryTable3Name;
  } // End Method getSummaryTable3Name() ======================
  


  // =======================================================
  /**
   * getSummaryTable4Name 
   * 
   * @return String
   */
  // =======================================================
  public String getSummaryTable4Name() {
    return summaryTable4Name;
  } // End Method getSummaryTable4Name() ======================
  


  // =======================================================
  /**
   * getSummaryTable7Name
   * 
   * @return String
   */
  // =======================================================
  public String getSummaryTable7Name() {
  return this.summaryTable7Name;
  } // End Method getSummaryTable7Name() ======================
  


  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  public static final char CSV_CHAR_DELIMITER = ',';
  public static final String CSV_DELIMETER = String.valueOf(CSV_CHAR_DELIMITER);  // could be a pipe
  private static final String NOT_CSV_DELIMETER = "<COMMA>";   
  
  private int numberOfDocuments;
  private int numberOfClasses = 0;
  private String corpusInstancesFile = null;
 
  private String summaryTable2Name = null;
  private String summaryTable3Name = null;
  private String summaryTable4Name = null;
  private String summaryTable5Name = null;
  private String summaryTable7Name = null;
  
  
  private String outputStatsDir = null;
  private boolean firstTime = true;
 
  private String[] labelsToIndex = null;
  private ArrayList<String>labelHeadings = null;
  private HashSet<String>seenLabels = null;
  
  private Type[]                               types = null;
  @SuppressWarnings("rawtypes")
  private HashMap<String, HashMap> documentTitleHash = null;
  private HashSet<String>                categorySet = null;
  private String[]                        classTypes = null;
  private String[]                          docTypes = null;
  private PrintWriter                            out = null;
  private PrintWriter                  summaryTable5 = null;
 // private String                         instanceCtr = "none";
  private String focusColumnName = "total";
  private int    focusColumnNumber = 4;
  private HashMap<String,int[]> documentTypeByCategoryDocFreqTable = null;
  
   private int instanceCounter = 0;
  
  
} // end Class MetaMapClient() ---------------
