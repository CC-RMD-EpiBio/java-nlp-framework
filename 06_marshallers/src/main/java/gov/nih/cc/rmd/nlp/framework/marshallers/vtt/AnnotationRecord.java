// =======================================================
/**
 * AnnotationRecord.java [Summary here]
 *
 * @author  guy
 * @created Aug 7, 2013
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.vtt;

import java.util.ArrayList;
import java.util.List;

import gov.va.chir.model.DocumentHeader;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;

import org.apache.uima.jcas.JCas;

/**
 * @author guy
 *
 */
public class AnnotationRecord {

  // =======================================================
  /**
   * Constructor AnnotationRecord 
   *
   * @param pJCas
   */
  // =======================================================
  public AnnotationRecord(JCas pJCas) {

    DocumentHeader  documentHeader = VUIMAUtil.getDocumentHeader( pJCas);
    this.tiuDocumentSID            = documentHeader.getDocumentId();
    this.patientSID                = documentHeader.getPatientID();
    this.referenceDate             = documentHeader.getReferenceDate();
    this.tiuDocumentDefinitionType = documentHeader.getDocumentTitle();
    this.otherMetaData             = documentHeader.getOtherMetaData();
    this.reportText                = pJCas.getDocumentText();
    
  } 
  // =======================================================
  /**
   * Constructor AnnotationRecord 
   * @param annotations 
   * @param metaData 
   * @param reportText 
   * @param documentID 
   * @param patientID 
   *
   */
  // =======================================================
  public AnnotationRecord(String patientID, String documentID, String reportText, String metaData, ArrayList<SimpleAnnotation> annotations) {
   
    this.patientSID = patientID;
    this.tiuDocumentSID = documentID;
    this.reportText = reportText;
    this.otherMetaData = metaData;
    this.annotations = annotations;
    
  }
  // =======================================================
  /**
   * getMetaData returns the metadata in a pipe delimited String
   *
   * @return String  docID|patientID|referenceDate|docType
   * 
   */
  // =======================================================
  public String getMetaData() {
    
    StringBuffer buff = new StringBuffer();
    buff.append(this.tiuDocumentSID );            buff.append("|");
    buff.append(this.patientSID     );            buff.append("|");
    buff.append(this.referenceDate  );            buff.append("|");
    buff.append(this.tiuDocumentDefinitionType ); buff.append("|");
    buff.append(this.otherMetaData);              buff.append("|");
    
   
    return ( buff.toString());
  
    
  }  // End Method getMetaData() ======================
  
  // =======================================================
  /**
   * getRecordText 
   * 
   * @return String
   */
  // =======================================================
  public String getReportText() {
     return this.reportText;
  }  // End Method getRecordText() ======================

  

  // =======================================================
  /**
   * getAnnotations
   * 
   * @return List<SimpleAnnotation>
   */
  // =======================================================
  public List<SimpleAnnotation> getAnnotations() {
     return this.annotations;
  }  // End Method getAnnotations() ======================

  
  
  // -------------------------------
  // Global variables
  // -------------------------------
   public String tiuDocumentSID;
  public String patientSID;
  public String referenceDate;
  public String tiuDocumentDefinitionType;
  public String otherMetaData;
  public String reportText;
  public List<SimpleAnnotation> annotations;
  public static final String       RECORD_DELIMITER = "'END OF NOTE'";
  
  
  
}
