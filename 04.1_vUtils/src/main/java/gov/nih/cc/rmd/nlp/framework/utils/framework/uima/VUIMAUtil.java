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
/**
 * VUIMAUtil contains uima utilities that rely on
 * vinci and chir specific modules
 *
 *
 * @author  Guy Divita 
 * @created Jan 20, 2011
 *
 * *  
 * 

 */

package gov.nih.cc.rmd.nlp.framework.utils.framework.uima;

import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;


import gov.nih.cc.rmd.framework.NestedSection;
import gov.nih.cc.rmd.framework.SectionZone;

// import gov.va.chir.model.VAnnotation;
import gov.va.chir.model.ClinicalStatement;
import gov.va.chir.model.CodedEntry;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Line;
import gov.va.chir.model.Section;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.VAnnotation;
import gov.va.chir.model.WordToken;
import gov.va.vinci.model.CEM;
import gov.va.vinci.model.CEMHeader;
import gov.va.vinci.model.Concept;
import gov.nih.cc.rmd.nlp.framework.utils.umls.UMLSUtils;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.FeatureValuePair;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


/**
 * The Class VUIMAUtil.
 */
public class VUIMAUtil {

  /** The Record id. */
  private static int RecordId;

  /** The Constant ASSERTION_STATUS. */
  public static final int ASSERTION_STATUS = 0; // assertion

  /** The Constant CONDITIONAL_STATUS. */
  public static final int CONDITIONAL_STATUS = 1; // conditional

  /** The Constant HISTORICAL_STATUS. */
  public static final int HISTORICAL_STATUS = 2; // historical

  /** The Constant HYPOTHETICAL_STATUS. */
  public static final int HYPOTHETICAL_STATUS = 3; // hypothetical

  /** The Constant SUBJECT_STATUS. */
  public static final int SUBJECT_STATUS = 4;

  // -----------------------------------------
  /**
   * getAnnotationsFromDocument retrieves a List<Annotation> from the cas of the
   * given type.
   *
   * @param pJCas the j cas
   * @param pType the type
   * @return List<Annotation>
   */
  // -----------------------------------------
  public final static List<Annotation> getAnnotationsFromDocument(JCas pJCas, int pType) {

    List<Annotation> returnValue = null;

    if (pJCas != null) {
      AnnotationIndex<Annotation> index = pJCas.getAnnotationIndex(pType);
      returnValue = UIMAUtil.annotationIndex2List(index);
    }
    return returnValue;
  } // end Method getAnnotationsFromDocument() ----

  // ----------------------------------------------
  /**
   * mapLabelToUIMAClass
   * 
   * N.B. This has the descriptor gov.va.chir.model and gov.nih.cc.rmd.model
   * hardwired in. If need be, this can be parameterized.
   *
   * @param plabel the plabel
   * @return the class
   */
  // ----------------------------------------------
  public final static Class<?> mapLabelToUIMAClass(String plabel) {
    String label = plabel;
    Class<?> returnValue = null;
    char[] label2 = label.toCharArray();
    if (label != null && !Character.isUpperCase(label.charAt(0)))
      label2[0] = Character.toUpperCase(label.charAt(0));
    label = new String(label2);

     
      // ----------------------
      // if there was an easier way, let me know - this is really not efficient - too many try reflection failures 
      // ----------------------
    if ((returnValue = mapToUIMAClassAux("gov.nih.cc.rmd.framework." + label)) == null) 
      if ((returnValue = mapToUIMAClassAux("gov.nih.cc.rmd.framework.model." + label)) == null)
        if ((returnValue = mapToUIMAClassAux("gov.nih.cc.rmd.model." + label)) == null) 
          if ((returnValue = mapToUIMAClassAux("gov.nih.cc.rmd.gate." + label)) == null)
            if ((returnValue = mapToUIMAClassAux("gov.va.chir.model." + label)) == null)
              if ((returnValue = mapToUIMAClassAux("gov.va.vinci." + label)) == null)
                if ((returnValue = mapToUIMAClassAux("gov.va.vinci.model." + label)) == null)
                  if ((returnValue = mapToUIMAClassAux("gov.va.vinci.model.temporal." + label)) == null)
                    if ((returnValue = mapToUIMAClassAux("gov.va.vinci.vitals." + label)) == null)
                      if ((returnValue = mapToUIMAClassAux("org.apache.ctakes.typesystem.type.refsem." + label)) == null)
                        if ((returnValue = mapToUIMAClassAux("org.apache.ctakes.typesystem.type.relation." + label)) == null)
                          if ((returnValue = mapToUIMAClassAux("org.apache.ctakes.typesystem.type.structured." + label)) == null)
                            if ((returnValue = mapToUIMAClassAux("org.apache.ctakes.typesystem.type.syntax." + label)) == null)
                              if ((returnValue = mapToUIMAClassAux("org.apache.ctakes.typesystem.type.textsem." + label)) == null)
                                if ((returnValue = mapToUIMAClassAux("org.apache.ctakes.typesystem.type.textspan." + label)) == null)
                                  if ((returnValue = mapToUIMAClassAux("org.apache.ctakes.typesystem.type.util." + label)) == null)
                                    returnValue = mapToUIMAClassAux("gov.nih.cc.rmd.model.Concept");
      
     return returnValue;

  } // end Method mapLabelToUIMAClass() -----------

  // =================================================
  /**
   * mapToUIMAClassAux returns a class if it can be found
   *  This method won't throw an exception if it cannot be 
   *  found, but it will return null
   * 
   * @param string
   * @return Class<?>      null if not found
  */
  // =================================================
  private final static Class<?> mapToUIMAClassAux(String pClassName) {
  
    Class<?> returnValue = null;
      try {
        returnValue = Class.forName(pClassName);

      } catch (Exception e) {
        System.err.println(pClassName + " not found ");
      }

    return returnValue;
  } // end Method mapLabelToUIMAClassAux() -----------

  // ----------------------------------------------
  /**
   * mapFeatureToUIMAFeature.
   *
   * @param pClass the class
   * @param pFeature the feature
   * @return the method
   */
  // ----------------------------------------------
  public final static Method mapFeatureToUIMAFeature(Class<?> pClass, String pFeature) {

    Method setBla = null;

    Method[] methodNames = pClass.getMethods();

    if (methodNames != null) {
      for (int i = 0; i < methodNames.length; i++) {
        if (methodNames[i].getName().toLowerCase().contains("add" + pFeature.toLowerCase())) {
          setBla = methodNames[i];
          break;
        }

        if (methodNames[i].getName().toLowerCase().contains("set" + pFeature.toLowerCase())) {
          setBla = methodNames[i];
          break;
        }

      }
    }

    return setBla;

  } // end Method mapFeatureToUIMAClass

  // ----------------------------------------------
  /**
   * setProvenance sets what made this annotation. The system, and the module
   * 
   * this used to be more complicated - writing to a string array. Provenance is
   * now just a vanilla string.
   *
   * @param pJCas the j cas
   * @param pAnnotation the annotation
   * @param pCreatingSystem the creating system
   * @param pCreatingModule the creating module
   */
  // ----------------------------------------------
  public final static void setProvenance(JCas pJCas, VAnnotation pAnnotation,
    String pCreatingSystem, String pCreatingModule) {

    pAnnotation.setProvenance(pCreatingSystem);

  } // end Method setProvenance()-----------------------
  // ----------------------------------------------

  /**
   * setProvenance sets what made this annotation. The system, and the module.
   * This method sets the system to "vinciNLPFramework"
   *
   * @param pCAS the cas
   * @param pAnnotation the annotation
   * @param pCreatingModule the creating module
   */
  // ----------------------------------------------
  public final static void setProvenance(JCas pCAS, VAnnotation pAnnotation,
    String pCreatingModule) {

    setProvenance(pCAS, pAnnotation, "vinciNLPFramework", pCreatingModule);

  } // end Method setProvenance()-----------------------

  // -----------------------------------------
  /**
   * getDocumentHeader retrieves the gov.va.chir.model.DocumentHeader
   *
   * @param pJCas the j cas
   * @return VAnnotation
   */
  // -----------------------------------------
  public final static DocumentHeader getDocumentHeader(JCas pJCas) {

    DocumentHeader docHeader = null;
    String documentId = null;
    try {
      List<Annotation> docHeaders = UIMAUtil.getAnnotations(pJCas, DocumentHeader.typeIndexID);

      if (docHeaders != null && !docHeaders.isEmpty()) {
        for (Annotation docH : docHeaders) {
          if (docH != null) {
            docHeader = (DocumentHeader) docH;
            return (docHeader);
          }
        }

      } else {

        String documentType = "unknown";
        String documentTitle = "unknown";
        String documentMetaData = "";
        String patientId = "unknown";
        String referenceDate = "01-01-00";
        documentId = String.valueOf(RecordId++);
        docHeader = VUIMAUtil.setDocumentHeader(pJCas, documentId, documentId, documentType,
            documentTitle, documentMetaData, patientId, referenceDate, RecordId);

      }

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Something is wrong here :" + e.toString());

    }

    return docHeader;
  } // end Method getDocumentHeader() ---------

  // -----------------------------------------
  /**
   * getDocumentId retrieves the documentID if it exits.
   *
   * @param pJCas the j cas
   * @return String "unknown" if it's not known.
   */
  // -----------------------------------------
  public final static String getDocumentId(JCas pJCas) {

    String documentId = "unknown";

    DocumentHeader header = getDocumentHeader(pJCas);
    if (header != null)
      documentId = header.getDocumentId();

    return documentId;
  } // end Method getDocumentId() ---------

  // -----------------------------------------
  /**
   * geRecordId retrieves the recordID assigned by the reader .
   *
   * @param pJCas the j cas
   * @return int
   */
  // -----------------------------------------
  public final static long getRecordId(JCas pJCas) {

    long recordId = -1;

    DocumentHeader header = getDocumentHeader(pJCas);
    if (header != null)
      recordId = header.getRecordId();

    return recordId;
  } // end Method getDocumentId() ---------

  // -----------------------------------------
  /**
   * getDocumentType retrieves the documentType if it exits.
   *
   * @param pJCas the j cas
   * @return String "unknown" if it's not known.
   */
  // -----------------------------------------
  public final static String getDocumentType(JCas pJCas) {

    String documentType = "unknown";

    DocumentHeader header = getDocumentHeader(pJCas);
    if (header != null)
      documentType = header.getDocumentType();

    return documentType;
  } // end Method getDocumentType() ---------

  // -----------------------------------------
  /**
   * getDocumentTitle retrieves the document title if it exits.
   *
   * @param pJCas the j cas
   * @return String "unknown" if it's not known.
   */
  // -----------------------------------------
  public final static String getDocumentTitle(JCas pJCas) {

    String documentTitle = "unknown";

    DocumentHeader header = getDocumentHeader(pJCas);
    if (header != null)
      documentTitle = header.getDocumentTitle();

    return documentTitle;
  } // end Method getDocumentTitle() ---------

  // -----------------------------------------
  /**
   * getReferenceDate retrieves the reference date if it exits.
   *
   * @param pJCas the j cas
   * @return String "unknown" if it's not known.
   */
  // -----------------------------------------
  public final static String getReferenceDate(JCas pJCas) {

    String referenceDate = new Date(0).toString();

    DocumentHeader header = getDocumentHeader(pJCas);
    if (header != null)
      referenceDate = header.getReferenceDate();

    return referenceDate;
  } // end Method getReferenceDate() ---------

  
//-----------------------------------------
 /**
  * getPageNumber retrieves the gov.va.chir.model.DocumentHeader's pageNumber
  *
  * @param pJCas the jcas
  * @return int
  */
 // -----------------------------------------
 public final static int getPageNumber( JCas pJCas) {
   
   int returnVal = -1;
   
   DocumentHeader documentHeader = getDocumentHeader( pJCas);
   if ( documentHeader != null )
     returnVal = documentHeader.getPageNumber();
   
   return returnVal;
   
 } // end Method getPageNumber() -----------
  // -----------------------------------------
  /**
   * getMetaData retrieves the other metadata that goes along with this record.
   *
   * @param pJCas the j cas
   * @return String "unknown" if it's not known.
   */
  // -----------------------------------------
  public final static String getMetaData(JCas pJCas) {

    String otherMetaData = "unknown";

    DocumentHeader header = getDocumentHeader(pJCas);
    if (header != null)
      otherMetaData = header.getOtherMetaData();

    return otherMetaData;
  } // end Method getMetaData() ---------

  // =======================================================
  /**
   * setDocumentHeader.
   *
   * @param pJCas the j cas
   * @param pDocumentId the document id
   * @param pDocumentName the document name
   * @param pDocumentType the document type
   * @param pDocumentTitle the document title
   * @param pDocumentMetaData the document meta data
   * @param pPatientId the patient id
   * @param pReferenceDate the reference date
   * @param pRecordId the record id
   * @return the document header
   */
  // =======================================================
  public final static DocumentHeader setDocumentHeader(JCas pJCas, String pDocumentId,
    String pDocumentName, String pDocumentType, String pDocumentTitle, String pDocumentMetaData,
    String pPatientId, String pReferenceDate, long pRecordId) {

    DocumentHeader documentHeader = new DocumentHeader(pJCas);

    documentHeader.setDocumentId(pDocumentId);
    documentHeader.setDocumentName(pDocumentName);
    documentHeader.setBegin(0);
    documentHeader.setEnd(1);
    documentHeader.setDocumentType( ciitizenNormalizeDocType( pDocumentType) );
    documentHeader.setDocumentTitle(pDocumentTitle);
    documentHeader.setOtherMetaData(pDocumentMetaData);
    documentHeader.setPatientID(pPatientId);
    documentHeader.setReferenceDate(pReferenceDate);
    documentHeader.setRecordId(pRecordId);

    documentHeader.addToIndexes(pJCas);

    return documentHeader;
  } // End Method setDocumentHeader() ======================

  // ------------------------------------------
  /**
   * createDocumentHeaderAnnotation.
   *
   * @param pJCas the j cas
   * @param pTiuDocumentSID the tiu document SID
   * @param pDocumentType the document type
   * @param pDocumentSpan the document span
   * @param pPatientSID the patient SID
   * @param pSSN the ssn
   * @param pReferenceDate the reference date
   * @param pDocumentMetaData the document meta data
   * @param pMetaDataDefinitions the meta data definitions
   * @param pRecordId the record id
   */
  // ------------------------------------------
  public final static void setDocumentHeader(JCas pJCas, long pTiuDocumentSID, String pDocumentType,
    int pDocumentSpan, long pPatientSID, String pSSN, Date pReferenceDate, String pDocumentMetaData,
    String pMetaDataDefinitions, long pRecordId) {

    DocumentHeader documentHeader = new DocumentHeader(pJCas);

    String tiuDocumentSID = String.valueOf(pTiuDocumentSID);
    String patientSID = String.valueOf(pPatientSID);

    documentHeader.setDocumentId(tiuDocumentSID);
    documentHeader.setDocumentName(tiuDocumentSID);
    documentHeader.setBegin(0);
    documentHeader.setEnd(2);
    documentHeader.setDocumentType( ciitizenNormalizeDocType( pDocumentType) );
    documentHeader.setDocumentTitle(pDocumentType);
    documentHeader.setOtherMetaData(pDocumentMetaData);
    documentHeader.setPatientID(patientSID);
    documentHeader.setSsn(pSSN);
    documentHeader.setReferenceDate(pReferenceDate.toString());
    documentHeader.setOtherMetaData(pDocumentMetaData);
    documentHeader.setRecordId((int) pRecordId);
    documentHeader.setOtherMetaDataFieldNames(pMetaDataDefinitions);

    documentHeader.addToIndexes(pJCas);

  } // End Method createDocumentHeaderAnnotation() -----------------------

  // ------------------------------------------
  /**
   * createDocumentHeaderAnnotation.
   *
   * @param pJCas the j cas
   * @param pTiuDocumentSID the tiu document SID
   * @param pDocumentType the document type
   * @param pDocumentSpan the document span
   * @param pPatientSID the patient SID
   * @param pSSN the ssn
   * @param pReferenceDate the reference date
   * @param pDocumentMetaData the document meta data
   * @param pMetaDataColumnNames the meta data column names
   * @param pMetaDataColumnTypes the meta data column types
   * @param pRecordId the record id
   */
  // ------------------------------------------
  public final static void setDocumentHeader(JCas pJCas, long pTiuDocumentSID, String pDocumentType,
    int pDocumentSpan, long pPatientSID, String pSSN, Date pReferenceDate, String pDocumentMetaData,
    String pMetaDataColumnNames, String pMetaDataColumnTypes, long pRecordId) {

    DocumentHeader documentHeader = new DocumentHeader(pJCas);

    String tiuDocumentSID = String.valueOf(pTiuDocumentSID);
    String patientSID = String.valueOf(pPatientSID);

    documentHeader.setDocumentId(tiuDocumentSID);
    documentHeader.setDocumentName(tiuDocumentSID);
    documentHeader.setBegin(0);
    documentHeader.setEnd(2);
    documentHeader.setDocumentType( ciitizenNormalizeDocType( pDocumentType) );
    documentHeader.setDocumentTitle(pDocumentType);
    documentHeader.setOtherMetaData(pDocumentMetaData);
    documentHeader.setPatientID(patientSID);
    documentHeader.setSsn(pSSN);
    documentHeader.setReferenceDate(pReferenceDate.toString());
    documentHeader.setOtherMetaData(pDocumentMetaData);
    documentHeader.setRecordId((int) pRecordId);
    documentHeader.setOtherMetaDataFieldNames(pMetaDataColumnNames);
    documentHeader.setOtherMetaDataFieldTypes(pMetaDataColumnTypes);

    documentHeader.addToIndexes(pJCas);

  } // End Method createDocumentHeaderAnnotation() -----------------------

//=================================================
 /**
  *  getCorpusName retrieves the corpus name from a GATE serial data store source that
  *  has been read in, which have separate corpus names.
  *  
  *  The corpus name, if present, has been put onto the documentHeader, in the otherMetaData
  *  field.
  * 
  * @param pDocumentType
  * @return String
 */
 // =================================================
  public static final String getCorpusName( JCas pJCas ) {
    int corpusNameIdx = -1;
    String corpusName = null;
    DocumentHeader documentHeader = VUIMAUtil.getDocumentHeader(pJCas);
    String metaDataFieldNames = documentHeader.getOtherMetaDataFieldNames();
    if ( metaDataFieldNames != null ) {
      String [] values = U.split( documentHeader.getOtherMetaData());
      String[] fieldCols = U.split( metaDataFieldNames);
      for ( int i = 0; i < fieldCols.length; i++) {
        if ( fieldCols[i].equals("corpusName")) {
          corpusNameIdx = i;
          break;
        }
      }
      if ( corpusNameIdx > -1 )
        corpusName = values[ corpusNameIdx];
    }
  
    corpusName = documentHeader.getOtherMetaData();
    return corpusName ;
  } // end Method getCorpusName() --------------------
  
  // =================================================
  /**
   * ciitizenNormalizeDocType  will return a doc type  
   *  given a file name.  This method should call the 
   *  document metadata method that does this - in the mean time
   *  I just need to know if it's a gene report or not
   *  these files contain "ngs" in them.
   * 
   * @param pDocumentType
   * @return
  */
  // =================================================
  public static String ciitizenNormalizeDocType(String pDocumentType) {
   String returnVal = pDocumentType;
   
  
   return returnVal;
  }

  // ------------------------------------------
  /**
   * updateConceptInfo creates or updates the conceptInfo with cui,tui,
   * conceptName, semantic type in a human readable form.
   *
   * @param pClinicalStatement the clinical statement
   * @param pCodedEntry the coded entry
   */
  // ------------------------------------------
  public final static void updateConceptInfo(ClinicalStatement pClinicalStatement,
    CodedEntry pCodedEntry) {

    // ---------------------------------------------------------
    // Retrieve the prior conceptInfo from the clinicalStatement
    // ---------------------------------------------------------
    String priorConceptInfo = pClinicalStatement.getConceptInfo();

    // ---------------------------------------------------------
    // Retrieve the current concept information
    // ---------------------------------------------------------
    String conceptName = pCodedEntry.getDisplayName();
    String cui = pCodedEntry.getCodeCode();
    if ((conceptName == null) || (conceptName.length() > 2000))
      conceptName = pCodedEntry.getCoveredText();
    if ((cui == null) || (cui.length() > 20)) {
      cui = "N/A";
      System.err.println("Issue with a funky coded entry ->" + conceptName);
    }

    // ---------------------------------
    // Get the semantic Types (tui|name|abbr 's)
    // ---------------------------------

    StringArray semanticTypez = pCodedEntry.getSemanticType();
    String semanticTypes = null;
    if (semanticTypez != null)
      semanticTypes = UIMAUtil.stringArrayToString(semanticTypez);
    String tuis = null;
    try {

      tuis = getTuis(semanticTypez);

    } catch (Exception e) {
      System.err.println("issue with getting the tuis for " + conceptName + "|" + cui + "|"
          + pCodedEntry.getCoveredText());
      throw new RuntimeException(e);
    }
    String[] semanticTypeAbbrz = UIMAUtil.stringArrayToArrayOfString(semanticTypez);
    semanticTypes = getSemanticTypeNames(semanticTypeAbbrz);
    String semanticTypeAbbrs = convertStringArrayToDelimitedString(semanticTypeAbbrz);

    // -------------------------------------------------------
    // human readable concept Info
    // cui|tui|ConceptName|semantiType|semanticTypeAbbr
    // -------------------------------------------------------
    String conceptInfo =
        cui + "|" + tuis + "|" + conceptName + "|" + semanticTypes + "|" + semanticTypeAbbrs;

    String buff = null;
    if (priorConceptInfo != null)
      buff = priorConceptInfo + U.NL + conceptInfo;
    else
      buff = conceptInfo;

    pClinicalStatement.setConceptInfo(buff);

  } // end Method updateConceptInfo() ----------

  // ------------------------------------------
  /**
   * getCuis returns a colon separated list of cuis if they exist.
   *
   * @param pAnnotation the annotation
   * @return String (colon separated)
   */
  // ------------------------------------------
  public final static String getCuis(Annotation pAnnotation) {
    String returnValue = null;
    String cuis = null;
    try {
      cuis = ((Concept) pAnnotation).getCuis();

    } catch (Exception e1) {
      try {
        cuis = ((gov.va.chir.model.CodedEntry) pAnnotation).getCodeCode();
      } catch (Exception e3) {
        // n/a
      }

    }
    if (cuis != null)
      returnValue = cuis.replace('|', ':');

    return returnValue;

  } // End Method getCuis() -----------------------

  // ------------------------------------------
  /**
   * getConceptNames returns a colon separated list of concept names if they
   * exist.
   *
   * @param pAnnotation the annotation
   * @return String (colon separated)
   */
  // ------------------------------------------
  public final static String getConceptNames(Annotation pAnnotation) {
    String returnValue = null;
    String conceptNames = null;
    try {
      conceptNames = ((Concept) pAnnotation).getConceptNames();

    } catch (Exception e1) {
      try {
        conceptNames = ((gov.va.chir.model.CodedEntry) pAnnotation).getDisplayName();
      } catch (Exception e3) {
        // n/a
      }

    }
    if (conceptNames != null)
      returnValue = conceptNames.replace('|', ':');

    return returnValue;

  } // End Method getConceptNames() -----------------------

  // ------------------------------------------
  /**
   * getTuis - given a StringArray of semantic type abbrs, return a String of
   * tui's, colon delimited.
   *
   * @param semanticTypez the semantic typez
   * @return String
   */
  // ------------------------------------------
  public final static String getTuis(StringArray semanticTypez) {

    String returnValue = null;

    StringBuffer buff = new StringBuffer();
    String tui = null;
    if (semanticTypez != null) {

      String[] semanticTypes = UIMAUtil.stringArrayToArrayOfString(semanticTypez);
      for (int i = 0; i < semanticTypes.length; i++) {
        if (semanticTypes[i] != null) {
          tui = UMLSUtils.getTuiFromSemanticType(semanticTypes[i]);
          buff.append(tui);
          if (i < semanticTypes.length - 1)
            buff.append(":");
        }
      } // end loop through semantic types
      returnValue = buff.toString();

    } // end of if semanticTypez isn't empty

    return returnValue;

    // End Method tuisAndSemanticTypes() -----------------------
  }

  // ------------------------------------------
  /**
   * getSemanticTypeNames returns a set of semantic type names, colon separated.
   *
   * @param semanticTypeAbbrs the semantic type abbrs
   * @return String
   */
  // ------------------------------------------
  public final static String getSemanticTypeNames(String[] semanticTypeAbbrs) {

    String returnVal = null;

    if (semanticTypeAbbrs != null) {
      StringBuffer buff = new StringBuffer();
      for (int i = 0; i < semanticTypeAbbrs.length; i++) {
        if (semanticTypeAbbrs[i] != null && semanticTypeAbbrs[i].length() > 0) {
          String name = UMLSUtils.getSemanticType(semanticTypeAbbrs[i]);
          buff.append(name);
          if (i < semanticTypeAbbrs.length - 1)
            buff.append(":");
        } // end loop through semantic type abbrs
      }
      returnVal = buff.toString();
    }

    return returnVal;

  } // End Method getSemanticTypeNames() -----------------------

  // ------------------------------------------
  /**
   * getCategory retrieves the first level category semantic Type if one exists
   * for this annotation.
   *
   * @param pAnnotation the annotation
   * @return String (null if not)
   */
  // ------------------------------------------
  public final static String getCategory(Annotation pAnnotation) {

    String category = null;

    try {

      category = ((gov.va.vinci.model.Concept) pAnnotation).getCategories();

    } catch (Exception e1) {
      try {

        StringArray semanticTypez =
            ((gov.va.chir.model.ClinicalStatement) pAnnotation).getSemanticTypez();
        category = UIMAUtil.stringArrayToString(semanticTypez);

      } catch (Exception e2) {
        try {

          StringArray semanticTypez =
              ((gov.va.chir.model.CodedEntry) pAnnotation).getSemanticType();
          category = UIMAUtil.stringArrayToString(semanticTypez);

        } catch (Exception e3) {
          // n/a
        }

      }
    }
    // -------------------------------------
    // Use annotation label as the category
    if (category == null) {
      String longAnnotationName = pAnnotation.getClass().getName();
      category = longAnnotationName.substring(longAnnotationName.lastIndexOf('.') + 1);
    }

    return category;

  } // End Method getCategory() -----------------------

  // ------------------------------------------
  /**
   * convertStringArrayToDelimitedString returns a colon delimited list for an
   * array of elements.
   *
   * @param anArray the an array
   * @return String
   */
  // ------------------------------------------
  public final static String convertStringArrayToDelimitedString(String[] anArray) {
    String returnValue = null;

    if (anArray != null && anArray.length > 0) {
      StringBuffer buff = new StringBuffer();
      for (int i = 0; i < anArray.length; i++) {
        buff.append(anArray[i]);
        if (i < anArray.length - 1)
          buff.append(":");
      } // end of loop through the array
      returnValue = buff.toString();
    } // if there are any elements

    return returnValue;

    // End Method convertStringArrayToDelimitedString() -----------------------
  }

  // =======================================================
  /**
   * getAssertionStatus will return a unified assertion status from the
   * annotation By default, it's asserted, and everything else is false.
   *
   * @param pJCas the j cas
   * @param pAnnotation the annotation
   * @return String +---------+-----------+----------+------------+-------+
   */
  // =======================================================
  public final static String getAssertionStatus(JCas pJCas, Annotation pAnnotation) {
    boolean props[] = new boolean[5];

    props = getAssertionStatus(pAnnotation);
    StringBuffer buff = new StringBuffer();
    if (props[ASSERTION_STATUS])
      buff.append("Asserted");
    else
      buff.append("Negated");
    if (props[CONDITIONAL_STATUS])
      buff.append("|Conditional");
    if (props[HISTORICAL_STATUS])
      buff.append("|Historical");
    if (props[HYPOTHETICAL_STATUS])
      buff.append("|Hypothetical");
    if (props[SUBJECT_STATUS])
      buff.append("|Patient");
    else
      buff.append("|Other");

    return buff.toString();
  } // End Method getAssertionStatus() ======================

  // =======================================================
  /**
   * getAssertionStatus will return a unified assertion status from the
   * annotation By default, it's asserted, and everything else is false.
   *
   * @param pJCas the j cas
   * @param pAnnotation the annotation
   * @return boolean[5] +---------+-----------+----------+------------+-------+
   */
  // =======================================================
  public final static boolean[] getAssertionProperties(JCas pJCas, Annotation pAnnotation) {
    boolean props[] = new boolean[5];

    props = getAssertionStatus(pAnnotation);

    return props;
  } // End Method getAssertionStatus() ======================

  // =======================================================
  /**
   * getAssertionStatus will return a unified assertion status from the
   * annotation .
   *
   * @param pAnnotation the annotation
   * @return boolean "Assertion|Conditional|Historical|Hypothetical|Subject
   */
  // =======================================================
  public final static boolean[] getAssertionStatus(Annotation pAnnotation) {

    boolean props[] = new boolean[5];

    props[ASSERTION_STATUS] = true; // assertion
    props[CONDITIONAL_STATUS] = false; // conditional
    props[HISTORICAL_STATUS] = false; // historical
    props[HYPOTHETICAL_STATUS] = false; // hypothetical
    props[SUBJECT_STATUS] = true; // subject

    props[ASSERTION_STATUS] = getAssertion_Status(pAnnotation);
    props[CONDITIONAL_STATUS] = getConditional_Status(pAnnotation);
    props[HISTORICAL_STATUS] = getHistorical_Status(pAnnotation);
    props[HYPOTHETICAL_STATUS] = getHypothetical_Status(pAnnotation);
    props[SUBJECT_STATUS] = getSubject_Status(pAnnotation);

    return props;
  } // End Method getAssertionStatus() ======================

  // =================================================
  /**
   * getAssertion_Status.
   *
   * @param pAnnotation the annotation
   * @return boolean true if asserted
   */
  // =================================================
  public final static boolean getAssertion_Status(Annotation pAnnotation) {

    boolean returnVal = true;
    String assertionStatus = getAssertion_StatusString(pAnnotation);

    if (assertionStatus == null)
      returnVal = true;
    else if (assertionStatus.contains("TrueNegative"))
      returnVal = false;
    else if (assertionStatus.contains("Asserted"))
      returnVal = true;
    else if (assertionStatus.contains("Negated"))
      returnVal = false;

    return returnVal;
  }

  // =================================================
  /**
   * getAssertion_Status.
   *
   * @param pAnnotation the annotation
   * @return boolean true if asserted
   */
  // =================================================
  public final static String getAssertion_StatusString(Annotation pAnnotation) {

    String assertionStatus = null;

    try {
      assertionStatus = ((VAnnotation) pAnnotation).getNegation_Status();
    } catch (Exception e1) {
      try {
        assertionStatus = ((Concept) pAnnotation).getAssertionStatus();
      } catch (Exception e2) {
        // n/a
      }
    }

    return assertionStatus;
  }

  // =======================================================
  /**
   * getConditional_Status returns the conditional status.
   *
   * @param pAnnotation the annotation
   * @return boolean
   */
  // =======================================================
  public final static boolean getConditional_Status(Annotation pAnnotation) {
    boolean returnVal = false;
    try {
      returnVal = ((VAnnotation) pAnnotation).getConditional();

    } catch (Exception e) {
      try {
        returnVal = ((Concept) pAnnotation).getConditionalStatus();

      } catch (Exception e2) {
        // n/a
      }
    }

    return returnVal;
  } // End Method getConditionalStatus() ======================
  // =======================================================

  /**
   * getConditional_StatusString returns the conditional status.
   *
   * @param pAnnotation the annotation
   * @return String True|False
   */
  // =======================================================
  public final static String getConditional_StatusString(Annotation pAnnotation) {

    String returnVal = String.valueOf(getConditional_Status(pAnnotation));

    return returnVal;
  }

  // =================================================
  /**
   * getHistorical_Status .
   *
   * @param pAnnotation the annotation
   * @return boolean
   */
  // =================================================
  public final static boolean getHistorical_Status(Annotation pAnnotation) {
    boolean historicalStatus = false;
    try {
      historicalStatus = ((VAnnotation) pAnnotation).getHistorical();
    } catch (Exception e5) {
      try {
        historicalStatus = ((Concept) pAnnotation).getHistoricalStatus();
      } catch (Exception e6) {
        // n/a
      }
    }

    return historicalStatus;

  } // End Method getHistoricalStatus() --------------

  // =================================================
  /**
   * getHistorical_StatusString.
   *
   * @param pAnnotation the annotation
   * @return boolean
   */
  // =================================================
  public final static String getHistorical_StatusString(Annotation pAnnotation) {

    String returnVal = String.valueOf(getHistorical_Status(pAnnotation));

    return returnVal;

  } // End Method getHistorical_StatusString() ------

  // =================================================
  /**
   * getHypothetical_Status .
   *
   * @param pAnnotation the annotation
   * @return boolean
   */
  // =================================================
  public final static boolean getHypothetical_Status(Annotation pAnnotation) {
    boolean HypotheticalStatus = false;
    // try {HypotheticalStatus = ((VAnnotation) pAnnotation).getHypothetical();
    // } catch (Exception e5) {
    // try {HypotheticalStatus = ((Concept)
    // pAnnotation).getHypotheticalStatus();} catch (Exception e6) {} }

    // TBD

    return HypotheticalStatus;

  } // End Method getHypotheticalStatus() --------------

  // =================================================
  /**
   * getHypothetical_StatusString .
   *
   * @param pAnnotation the annotation
   * @return String
   */
  // =================================================
  public final static String getHypothetical_StatusString(Annotation pAnnotation) {

    String returnVal = String.valueOf(getHypothetical_Status(pAnnotation));

    return returnVal;

  } // End Method getHypothetical_StatusString() ----

  // =======================================================
  /**
   * getSubject_Status .
   *
   * @param pAnnotation the annotation
   * @return boolean (true if it's about the patient)
   */
  // =======================================================
  public final static boolean getSubject_Status(Annotation pAnnotation) {

    boolean returnVal = true;
    String buff = getSubject(pAnnotation);
    if (!buff.equals("Patient"))
      returnVal = false;

    return returnVal;
  } // End Method getSubject() ======================

  // =======================================================
  /**
   * getSubject returns the subject.
   *
   * @param pAnnotation the annotation
   * @return String (Patient|Other)
   */
  // =======================================================
  public final static String getSubject(Annotation pAnnotation) {
    String returnVal = "Patient";
    try {
      returnVal = ((VAnnotation) pAnnotation).getSubject();

    } catch (Exception e) {
      try {
        returnVal = ((Concept) pAnnotation).getSubjectStatus();

      } catch (Exception e2) {
        // n/a
      }
    }
    if (returnVal == null)
      returnVal = "Patient";
    return returnVal;
  } // End Method getSubject() ======================

  // =======================================================
  /**
   * getSectionName retrieves the section name from either a VAnnotation or a
   * Concept.
   *
   * @param pAnnotation the annotation
   * @return String
   */
  // =======================================================
  public final static String getSectionName(Annotation pAnnotation) {
    String returnVal = "unknown";
    try {
      returnVal = ((VAnnotation) pAnnotation).getSectionName();

    } catch (Exception e) {
      try {
        returnVal = ((Concept) pAnnotation).getSectionName();
        if (returnVal.equals("unknown"))
          returnVal = deriveSectionName(pAnnotation);

      } catch (Exception e2) {
        // n/a
      }
    }
    if (returnVal == null) {
      returnVal = deriveSectionName(pAnnotation);
    }

    return returnVal;
  } // End Method getSectionName() ======================

  // =================================================
  /**
   * deriveSectionName finds the sectionZone, and looks up the section name from
   * the section zone from this annotation.
   *
   * @param pAnnotation the annotation
   * @return String "unknown" if not found
   */
  // =================================================
  public static String deriveSectionName(Annotation pAnnotation) {
    String returnVal = "unknown";

    try {
      JCas jcas = pAnnotation.getCAS().getJCas();

      List<Annotation> sectionZones = UIMAUtil.fuzzyFindAnnotationsBySpan(jcas, SectionZone.typeIndexID, pAnnotation.getBegin(), pAnnotation.getEnd());

      if (sectionZones != null && !sectionZones.isEmpty()) {
        for (Annotation section : sectionZones) {
          returnVal = ((SectionZone) section).getSectionName();
          if (returnVal != null)
            break;
        }
      }

    } catch (Exception e) {
      // n/a
    }
    return returnVal;
    // end Method deriveSectionName()
  }

  // =======================================================
  /**
   * deleteAnnotations removes all annotations from the jcas This is supposed to
   * be equivalent to the cas.reset() which does not seem to be working.
   * 
   * This will not delete the documentHeader though.
   *
   * @param pJCas the j cas
   */
  // =======================================================
  public final static void deleteAnnotations(JCas pJCas) {

    List<Annotation> allAnnotations = UIMAUtil.getAnnotations(pJCas);

    if (allAnnotations != null && allAnnotations.size() > 0) {

      for (Annotation annotation : allAnnotations)
        if (!annotation.getClass().getSimpleName().equals("DocumentHeader"))
          annotation.removeFromIndexes();
    }

  } // End Method deleteAnnotations() ======================

  // =======================================================
  /**
   * getSectionAndContentHeadingAnnotations returns a list of section and
   * contentHeading annotations.
   *
   * @param pJCas the j cas
   * @return List<Annotation>
   */
  // =======================================================
  public final static List<Annotation> getSectionAndContentHeadingAnnotations(JCas pJCas) {
    List<Annotation> sectionAnnotations = UIMAUtil.getAnnotations(pJCas, Section.typeIndexID);
    List<Annotation> contentHeadingAnnotations =
        UIMAUtil.getAnnotations(pJCas, ContentHeading.typeIndexID);
    ArrayList<Annotation> sectionAndContentHeadingAnnotations = new ArrayList<Annotation>();
    if (sectionAnnotations != null)
      sectionAndContentHeadingAnnotations.addAll(sectionAnnotations);
    if (contentHeadingAnnotations != null)
      sectionAndContentHeadingAnnotations.addAll(contentHeadingAnnotations);
    if (sectionAndContentHeadingAnnotations != null
        && sectionAndContentHeadingAnnotations.size() > 0)
      UIMAUtil.sortByOffset(sectionAndContentHeadingAnnotations);

    return sectionAndContentHeadingAnnotations;
    // End Method getSectionAndContentHeadingAnnotations()
    // ======================
  }

  // =======================================================
  /**
   * hasAnnotations returns true if this cas has v3NLP Framework annotations
   * (other than the documentHeader, CSI ).
   *
   * @param pJCas the j cas
   * @return boolean
   */
  // =======================================================
  public final static boolean hasAnnotations(JCas pJCas) {

    boolean returnVal = false;

    List<Annotation> annotations = UIMAUtil.getAnnotations(pJCas);
    if (annotations != null)
      for (Annotation annotation : annotations) {
        String annotationClassName = annotation.getClass().getName();
        if (annotationClassName.contains("gov")) {
          if (annotationClassName.contains("DocumentHeader")
              || annotationClassName.contains("CSI")) {
            returnVal = false;
          } else {
            returnVal = true;
            break;
          }
        }
      }

    return returnVal;
  } // end Method hasAnnotations() ------------------

  // =======================================================
  /**
   * getSectionName retrieves the section that this annotation sits within.
   * There is a hierarchy of what kind of section labels returned. If the
   * annotation already has the section labeled, pass this along. If there is a
   * geographical section* this is embedded within, the geographical section
   * name is returned. If there is no geographical section, the CEM section name
   * is returned.
   * 
   * A geographical section is a section where the section name returned from
   * the Sectionizer corresponds to the contentHeading found in the text.
   *
   * @param pJCas the j cas
   * @param pAnnotation the annotation
   * @return String
   */
  // =======================================================
  public final static String getSectionName(JCas pJCas, Annotation pAnnotation) {
    String sectionName = "unknown";

    try {
      sectionName = ((VAnnotation) pAnnotation).getSectionName();
      if (sectionName != null && !sectionName.isEmpty())
        return sectionName;
    } catch (Exception e) {
      // n/a
    }

    List<Annotation> sectionZones = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,
        SectionZone.typeIndexID, pAnnotation.getBegin(), pAnnotation.getEnd());

    if (sectionZones != null && !sectionZones.isEmpty()) {
      SectionZone aSection = (SectionZone) sectionZones.get(0);
      sectionName = aSection.getSectionName();
    } else {

      List<Annotation> sections = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Section.typeIndexID,
          pAnnotation.getBegin(), pAnnotation.getEnd());

      if (sections != null && !sections.isEmpty()) {
        Section aSection = (Section) sections.get(0);
        sectionName = aSection.getSectionName();
      } else { // <----------------- no section exists, find the cem section

        List<Annotation> CEMSections = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, CEM.typeIndexID,
            pAnnotation.getBegin(), pAnnotation.getEnd());

        // ----------------------
        // there will be multiple, nested CEMS. Choose the one that is the most
        // nested
        if (CEMSections != null && !CEMSections.isEmpty()) {
          int cemNesting = 0;
          int maxNesting = -1;
          for (Annotation cem : CEMSections) {

            CEM aCEM = (CEM) cem;
            cemNesting = getCEMNesting(aCEM);
            if (cemNesting > maxNesting) {
              CEMHeader cemHeader = aCEM.getHeader();
              if (cemHeader != null) {
                sectionName = cemHeader.getName();
              }
            } // end if this is the most nested element
          } // end loop through cems
        } // end if there are cems
      } // end if sections were empty
    } // end if there were no sectionZones

    return sectionName;
  } // End Method getSectionName() ======================

  // =======================================================
  /**
   * getSection retrieves the section that this annotation sits within.
   *
   * @param pJCas the j cas
   * @param pAnnotation the annotation
   * @return String
   */
  // =======================================================
  public final static Annotation getSection(JCas pJCas, Annotation pAnnotation) {
    VAnnotation section = null;

    try {
      section = ((VAnnotation) pAnnotation).getSection();
    } catch (Exception e) {
      // not one of ours
    }

    return section;
  } // End Method getSection() ===========================
  
//=================================================
/**
* getSectionZone
*  
* @param pContentHeading
* @return Annotation
*/
// =================================================
public static Annotation getSectionZone(JCas pJCas, Annotation pContentHeading) {
    
  return getSectionZoneFor( pJCas, pContentHeading);


} // end Method getSectionZoneFor() -----------------

  // =======================================================
  /**
   * getCEMNesting returns the nesting
   * 
   * by default, if there is no nesting, 0 is returned.
   *
   * @param aCEM the a CEM
   * @return int
   */
  // =======================================================
  private static int getCEMNesting(CEM aCEM) {

    int returnVal = 0;
    String nesting_ = aCEM.getNesting();
    if (nesting_ != null)
      try {
        returnVal = Integer.parseInt(nesting_.trim());
      } catch (Exception e) {
        // n/a
      }

    return returnVal;
  } // End Method getCEMNesting() ======================

  // =======================================================
  /**
   * isInProseLazy returns true if this concept is in prose or not. The
   * heruistics are as follows:
   * 
   * If this term is within a contentHeading - not in prose if this term is
   * within a sentence - and the sentence is not a question, in prose if this
   * term is in dependent content, if the dependent content has a trailing
   * period, yes, otherwise no.
   *
   * @param pJCas the j cas
   * @param pTerm the term
   * @return boolean
   */
  // =======================================================
  public final static boolean isInProseLazy(JCas pJCas, Annotation pTerm) {
    boolean returnVal = true;

    try {
      returnVal = ((Concept) pTerm).getInProse();
    } catch (Exception e) {

      try {
        returnVal = ((VAnnotation) pTerm).getInProse();
      } catch (Exception e2) {

        returnVal = isInProse(pJCas, pTerm);
      }
    }
    return returnVal;
  } // End Method isInProseLazy() ------------------------

  // =======================================================
  /**
   * isInProse returns true if this concept is in prose or not. The heruistics
   * are as follows: If this term is within a contentHeading - not in prose If
   * this term is within a List - not in prose if this term is within a sentence
   * - and the sentence is not a question, in prose if this term is in dependent
   * content, if the dependent content has a trailing period, yes, otherwise no.
   *
   * @param pJCas the j cas
   * @param pTerm the term
   * @return boolean
   */
  // =======================================================
  public final static boolean isInProse(JCas pJCas, Annotation pTerm) {
    boolean returnVal = true;
    List<Annotation> contentHeadings = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,
        ContentHeading.typeIndexID, pTerm.getBegin(), pTerm.getEnd());

    List<Annotation> lists = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,
        gov.va.chir.model.List.typeIndexID, pTerm.getBegin(), pTerm.getEnd());

    if ((contentHeadings != null && !contentHeadings.isEmpty())
        || (lists != null && !lists.isEmpty())) {
      returnVal = false;
    } else {
      List<Annotation> sentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Sentence.typeIndexID,
          pTerm.getBegin(), pTerm.getEnd());

      if (sentences != null && !sentences.isEmpty()) {
        returnVal = true;
      } else {
        List<Annotation> dependentContents = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,
            DependentContent.typeIndexID, pTerm.getBegin(), pTerm.getEnd());

        if (dependentContents != null && !dependentContents.isEmpty()) {
          for (Annotation dependentContent : dependentContents) {
            returnVal = false;
            String buff = dependentContent.getCoveredText();
            if (buff != null && buff.trim().endsWith(".")) {
              returnVal = true;
              break;
            }
          }
        }
      }
    }
    return returnVal;
  } // End Method isInProse() ======================

  // =======================================================
  /**
   * isPositive or Control returns true if the annotation comes from the control
   * group or the positive group. That's now marked in the documentHeader
   *
   * @param pJCas the j cas
   * @return boolean
   */
  // =======================================================
  public final static boolean isPositiveOrControl(JCas pJCas) {
    boolean returnVal = true;

    try {
      DocumentHeader docHeader = getDocumentHeader(pJCas);
      returnVal = docHeader.getPositiveOrControl();
    } catch (Exception e) {
      // n/a
    }

    return returnVal;
  } // End Method isInProse() ======================

  // ==========================================
  /**
   * getOtherAttribute retrieves an attribute from the annotation if it exists,
   * otherwise it returns a null.
   *
   * @param pAnnotation the annotation
   * @param pAnnotationAttribute the annotation attribute
   * @return String
   */
  // ==========================================
  public final static String getOtherAttribute(Annotation pAnnotation,
    String pAnnotationAttribute) {
    String returnVal = null;

    try {
      List<FeatureValuePair> featureValuePairs = UIMAUtil.getFeatureValuePairs(pAnnotation);

      if (featureValuePairs != null)
        for (FeatureValuePair fvp : featureValuePairs) {
          if (fvp.getFeatureName().equals(pAnnotationAttribute)) {
            returnVal = fvp.getFeatureValue();
            break;
          }
        }
    } catch (Exception e) {
      // n/a
    }

    return returnVal;
  }// end Method getOtherAttribute() ==========

  // ==========================================
  /**
   * setDocumentText adds in the document title on the first 3 lines, and sets
   * the text.
   *
   * @param pJCas the j cas
   * @param pDocumentType the document type
   * @param pDocumentTitle the document title
   * @param pReferenceDate the reference date
   * @param pReportText the report text
   */
  // ==========================================
  public final static void setDocumentText(JCas pJCas, String pDocumentType, String pDocumentTitle,
    String pReferenceDate, String pReportText) {

    boolean createMetaDataHeader = true;

    if (pReportText.contains("MetaData=Not=Part=of=Record="))
      createMetaDataHeader = false;

    setDocumentText(pJCas, pDocumentType, pDocumentTitle, pReferenceDate, pReportText,
        createMetaDataHeader);

  } // end Method setDocumentText() ===========

  // ==========================================
  /**
   * setDocumentText adds in the document title on the first 3 lines, and sets
   * the text.
   *
   * @param pJCas the j cas
   * @param pDocumentType the document type
   * @param pDocumentTitle the document title
   * @param pReferenceDate the reference date
   * @param pReportText the report text
   * @param setHeader - true will set a metadata header
   */
  // ==========================================
  public final static void setDocumentText(JCas pJCas, String pDocumentType, String pDocumentTitle,
    String pReferenceDate, String pReportText, boolean setHeader) {

    StringBuilder buff = new StringBuilder();

    if (setHeader) {
      buff.append("===============================================MetaData=Not=Part=of=Record=\n");
      buff.append("Title: ");
      buff.append(pDocumentTitle);
      buff.append('\n');
      buff.append("DocumentType: ");
      buff.append(pDocumentTitle);
      buff.append('\n');
      buff.append("Reference Date: ");
      buff.append(pReferenceDate);
      buff.append('\n');
      buff.append("===============================================MetaData=Not=Part=of=Record=\n");
    }
    
    // --------------------------------
    // Convert any non utf 8 characters to utf 8 characters
    // --------------------------------
    
  //  String cleanText = ToUtf8.processToXMLChars( pReportText );
    
    buff.append( pReportText );

    pJCas.setDocumentText(buff.toString());

  } // end Method setDocumentText() ===========
  
//=================================================
/**
* setDocumentText 
* 
* @param pJCas
* @param pDocumentType
* @param pDocumentText
* @param pDocumentText
*/
//=================================================
public static void setDocumentText(JCas pJCas, String pDocumentType, String pDocumentText, boolean pSetHeader) {
 
  setDocumentText( pJCas,  pDocumentType, "", "", pDocumentText, pSetHeader);
    
 
} // end Method setDocumentText() -------------


  // ==========================================
  /**
   * getAnnotationTypes returns a list of the annotation labels for this cas
   * 
   * (without namespaces).
   *
   * @param pJCas the j cas
   * @return List<String>
   */
  // ==========================================
  public final static List<String> getAnnotationTypes(JCas pJCas) {

    HashSet<String> labelHash = new HashSet<String>();
    ArrayList<String> labels = null;

    List<Annotation> allAnnotations = UIMAUtil.getAnnotations(pJCas);
    if (allAnnotations != null && !allAnnotations.isEmpty())
      for (Annotation annotation : allAnnotations) {
        String name = annotation.getClass().getSimpleName();
        labelHash.add(name);
      }

    if (!labelHash.isEmpty()) {
      labels = new ArrayList<String>(labelHash.size());
      for (String label : labelHash)
        labels.add(label);

    }

    return labels;
    // end Method getAnnotationTypes() ========
  }

  // ==========================================
  /**
   * getSta3n returns the sta3n if it exists.
   *
   * @param pJCas the j cas
   * @return List<String>
   */
  // ==========================================
  public final static String getSta3n(JCas pJCas) {
    String returnVal = null;

    DocumentHeader documentHeader = VUIMAUtil.getDocumentHeader(pJCas);
    if (documentHeader != null) {
      String metaDataFieldNames = documentHeader.getOtherMetaDataFieldNames();
      if (metaDataFieldNames != null) {
        String[] cols = U.split(metaDataFieldNames);
        if (cols != null && cols.length > 0) {

          String buff = documentHeader.getOtherMetaData();
          String metaData[] = U.split(buff);
          for (int i = 0; i < cols.length; i++) {
            if (cols[i] != null && cols[i].toLowerCase().equals("sta3n")) {
              returnVal = metaData[i];
              break;
            }
          }
        }
      }
    }

    return returnVal;
  } // end Method getSta3n() ================

  // =================================================
  /**
   * getWordBefore returns the word Before this annotation.
   *
   * @param pJCas the j cas
   * @param pAnnotation the annotation
   * @return String (the normalized form)
   */
  // =================================================
  public static final String getWordBefore(JCas pJCas, Annotation pAnnotation) {

    String returnVal = null;

    if (pAnnotation != null) {

      String docText = pJCas.getDocumentText();

      if (docText != null) {
        // int docLength = docText.length();
        int leftOffset = pAnnotation.getBegin() - 2;
        int window = 20;

        if (leftOffset - window < 2)
          window = leftOffset - 1;

        {

          List<Annotation> wordsBefore = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,
              WordToken.typeIndexID, leftOffset - window, leftOffset);

          if (wordsBefore != null && !wordsBefore.isEmpty()) {

            Annotation lastWord = null;
            for (int i = wordsBefore.size() - 1; i >= 0; i--) {

              lastWord = wordsBefore.get(i);
              if (!((WordToken) lastWord).getPunctuationOnly())
                break;

            }

            if (lastWord != null) {
              returnVal = lastWord.getCoveredText();
              if (returnVal != null)
                returnVal = U.normalize(returnVal);
            }
          }
        }
      }
    }
    return returnVal;
  } // end Method getWordAfter() ---------------------

  // =================================================
  /**
   * getWordAfter returns the word after this annotation.
   *
   * @param pJCas the j cas
   * @param pAnnotation the annotation
   * @return String (the normalized form)
   */
  // =================================================
  public static final String getWordAfter(JCas pJCas, Annotation pAnnotation) {

    String returnVal = null;

    if (pAnnotation != null) {

      String docText = pJCas.getDocumentText();

      if (docText != null) {
        int docLength = docText.length();
        int rightOffset = pAnnotation.getEnd() + 1;
        int window = 20;

        if (rightOffset + window > docLength)
          window = docLength - rightOffset;

        List<Annotation> wordsAfter = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,
            WordToken.typeIndexID, rightOffset, rightOffset + window);

        if (wordsAfter != null && !wordsAfter.isEmpty()) {

          Annotation firstWord = wordsAfter.get(0);

          if (firstWord != null) {
            returnVal = firstWord.getCoveredText();
            if (returnVal != null)
              returnVal = U.normalize(returnVal);
          }
        }
      }

    }
    return returnVal;
  } // end Method getWordAfter() ---------------------

  // =================================================
  /**
   * isRightJustified returns true if the annotation starts >= 50% of the line.
   *
   * @param pJCas the j cas
   * @param pAnnotation the annotation
   * @return boolean
   */
  // =================================================
  public final static boolean isRightJustified(JCas pJCas, Annotation pAnnotation) {

    boolean returnVal = false;

    List<Annotation> lines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Line.typeIndexID,
        pAnnotation.getBegin(), pAnnotation.getEnd());

    if (lines != null && !lines.isEmpty()) {
      Annotation aLine = lines.get(0);

      int lineLength = aLine.getEnd() - aLine.getBegin();
      int startingPositionInLine = pAnnotation.getBegin() - aLine.getBegin();

      double percentPosition = (startingPositionInLine / lineLength) * 100;

      if (percentPosition >= 50)
        returnVal = true;

    }

    return returnVal;
  } // end isRightJustified() ------------------------

  // =================================================
  /**
   * isASlotValue returns true if this annotation is within a slot value
   * structure.
   *
   * @param pJCas the j cas
   * @param pAnnotation the annotation
   * @return boolean
   */
  // =================================================
  public static final boolean isASlotValue(JCas pJCas, Annotation pAnnotation) {
    boolean returnVal = false;

    List<Annotation> slotValues = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, SlotValue.typeIndexID,
        pAnnotation.getBegin(), pAnnotation.getEnd());

    if (slotValues != null && !slotValues.isEmpty())
      returnVal = true;

    return returnVal;
  } // end Method isSlotValue() ---------------------

  // =================================================
  /**
   * getEventDate returns an event date if this annotation has an event date
   * attribute.
   * 
   * if this annotation has an event date attribute, but it's null, the
   * reference date is returned, unless reference date is also null.
   *
   * @param pUimaAnnotation the uima annotation
   * @param pReferenceDate the reference date
   * @return String (null if empty, or not applicable)
   */
  // =================================================
  public final static String getEventDate(Annotation pUimaAnnotation, String pReferenceDate) {

    String returnDate = pReferenceDate;

    try {
      returnDate = ((Concept) pUimaAnnotation).getEventDate();
    } catch (Exception e) {
      try {
        returnDate = ((VAnnotation) pUimaAnnotation).getEventDate();
      } catch (Exception e2) {
         try {
           returnDate = ((DocumentHeader) pUimaAnnotation).getEventDate();
         } catch (Exception e3 ) {}
        // not an annotation that should have an event date
      }
    }
    return returnDate;
  } // end Method getEventDate()

  // =================================================
  /**
   * setEventDate sets an event date
   * 
   * Event date is an attribute of two kinds of annotations - VAnnoation,
   * Concept, This is a helper function to abstract away from needing to know
   * what kind of annotation this annotation is.
   *
   * @param pUimaAnnotation the uima annotation
   * @param pReferenceDate the reference date
   */
  // =================================================
  public final static void setEventDate(Annotation pUimaAnnotation, String pReferenceDate) {

    try {
      ((Concept) pUimaAnnotation).setEventDate(pReferenceDate);
    } catch (Exception e) {
      try {
        ((VAnnotation) pUimaAnnotation).setEventDate(pReferenceDate);
      } catch (Exception e2) {

        // not an annotation that should have an event date
      }
    }

  } // end Method setEventDate()

  // =================================================
  /**
   * setStatementDate sets a statement date
   * 
   * statement date is an attribute of two kinds of annotations - VAnnoation,
   * Concept, This is a helper function to abstract away from needing to know
   * what kind of annotation this annotation is.
   *
   * @param pUimaAnnotation the uima annotation
   * @param pStatementDate the statement date
   */
  // =================================================
  public final static void setStatementDate(Annotation pUimaAnnotation, String pStatementDate) {

    try {
      ((Concept) pUimaAnnotation).setStatementDate(pStatementDate);
    } catch (Exception e) {
      try {
        ((VAnnotation) pUimaAnnotation).setStatementDate(pStatementDate);
      } catch (Exception e2) {

        // not an annotation that should have an event date
      }
    }

  } // end Method setEventDate()

  
//=================================================
 /**
  * setReferenceDate sets a Reference date
  * 
  * Reference date is an attribute of three kinds of annotations - VAnnoation,
  * Concept, and SectionZone This is a helper function to abstract away from needing to know
  * what kind of annotation this annotation is.
  *
  * @param pUimaAnnotation the uima annotation
  * @param pReferenceDate the statement date
  */
 // =================================================
 public final static void setReferenceDate(Annotation pUimaAnnotation, String pReferenceDate) {

   try {
     ((Concept) pUimaAnnotation).setReferenceDate(pReferenceDate);
   } catch (Exception e) {
     try {
       ((VAnnotation) pUimaAnnotation).setReferenceDate(pReferenceDate);
     } catch (Exception e2) {
    	 try {
    	       ((SectionZone) pUimaAnnotation).setReferenceDate(pReferenceDate);
    	     } catch (Exception e3) {
    	    	 // not an annotation that should have an event date
    	     }
     }
   }

 } // end Method setReferenceDate()

 



//=================================================
 /**
  * repairUnknownSectionZones
  *   
  *
  * @param pJCas the cas
  * @param pContentHeading  the content heading
  * @return SectionZone 
  */
 // =================================================
public static SectionZone repairUnknownSectionZones(JCas pJCas, Annotation pContentHeading) {
	
	SectionZone newSectionZone = null;
	
	if ( isInSlotValue( pJCas, pContentHeading)  )
	    return null;
	
	Annotation lastSectionZone = getSectionZoneFor( pJCas, pContentHeading);
	
	// Iterate up, until you get a section zone that has a non empty section name and
	// a section name that does not start with a date 
	
	
	SectionZone previousSectionZone = getPreviousSectionZone( pJCas, lastSectionZone);
	
	ArrayList<SectionZone> nestedSections = new ArrayList<SectionZone>();
	nestedSections.add( (SectionZone) lastSectionZone);
	while ( previousSectionZone != null && 
			(previousSectionZone.getSectionName() == null ||
			previousSectionZone.getSectionName().toLowerCase().contains("unknown") ||
			previousSectionZone.getSectionName().toLowerCase().contains("unlabeled"))) {
		    	nestedSections.add( previousSectionZone);
		    	previousSectionZone = getPreviousSectionZone( pJCas, previousSectionZone);
	}
	  
	if ( previousSectionZone != null && previousSectionZone.getSectionName() != null ) {
		SectionZone firstSectionZone = previousSectionZone ;
		// create a new section that envelopes the firstSection thru the last section
		// set the referenceDate and statement date to the first section's version
	    newSectionZone = createSectionZone( pJCas, firstSectionZone.getBegin(), lastSectionZone.getEnd(), firstSectionZone.getIndentation(), firstSectionZone.getSectionName(), firstSectionZone.getSectionTypes());
		newSectionZone.setReferenceDate( firstSectionZone.getReferenceDate());
		newSectionZone.setStatementDate( firstSectionZone.getStatementDate());
       // changeSectionZonesToNestedSectionZones( pJCas, newSectionZone, nestedSections);
		previousSectionZone.removeFromIndexes();
		lastSectionZone.removeFromIndexes();
		pContentHeading.removeFromIndexes();
		
	}
	
	
	
	return newSectionZone;
} // end Method repairUnknownSectionZones() ---------


// =================================================
/**
 * getLine getLine returns the line the annotation is within
 *         if there are two lines that cover this annotation
 *         this method returns the first line.
 * 
 * @param pJCas
 * @param pAnnotation 
 * @return Line
*/
// =================================================
  public final static Line getLine(JCas pJCas, Annotation pAnnotation) {
  
    Line returnVal = null;
    List<Annotation> lines = getLines( pJCas, pAnnotation );
    
    if (lines != null  && !lines.isEmpty() )
      returnVal = (Line) lines.get(0);
  return returnVal;
} // end getLine() ----------------------------------

//=================================================
/**
* getLine getLine returns the lines the annotation is within
*        
* @param pJCas
* @param pAnnotation 
* @return List<Annotation> of Line
*/
//=================================================
 public final static List<Annotation> getLines(JCas pJCas, Annotation pAnnotation) {
 
   List<Annotation> returnVal = null;
   List<Annotation> lines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Line.typeIndexID,  pAnnotation.getBegin(), pAnnotation.getEnd() );
   
   if (lines != null  && !lines.isEmpty() )
     returnVal = lines;

   return  returnVal;
} // end getLine() ----------------------------------

// =================================================
/**
 * isInSlotValue 
 * 
 * @param pContentHeading
 * @return boolean
*/
// =================================================
private final static boolean isInSlotValue(JCas pJCas, Annotation pContentHeading) {
  boolean returnVal = false;
  
  if ( pContentHeading != null ) {
    List<Annotation> slotValue = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas,  SlotValue.typeIndexID,  pContentHeading.getBegin(), pContentHeading.getEnd());
    
    if ( slotValue != null && !slotValue.isEmpty())
      returnVal = true;
  }
  return returnVal;

} // end Method isInSlotValue() -------------------

//=================================================
/**
* changeSectionZonesToNestedSectionZones 
*  
* @param pJCas
* @param pNewSectionZone
* @param pNestedSections
* @return SectionZone
*/
//=================================================
private static void changeSectionZonesToNestedSectionZones(JCas pJCas, SectionZone pNewSectionZone, List<SectionZone> pNestedSections) {
	
	// the order of this nested section has to be reversed
	if ( pNestedSections != null ) {
		FSArray nestedSectionz = new FSArray( pJCas, pNestedSections.size());
		for ( int i = pNestedSections.size() -1 ; i >= 0 ; i-- ) {
			NestedSection nestedSection = createNestedSection( pJCas, pNestedSections.get(i), pNewSectionZone );
			nestedSectionz.set(i, nestedSection);
		}
	    nestedSectionz.addToIndexes();
	    pNewSectionZone.setNestedSection( nestedSectionz );
	}
	
	
} // end Method changeSectionZonesToNestedSectionZones() --


//=================================================
/**
* createNestedSection 
*  
* @param pJCas
* @param pSectionZone
* @param pNewSectionZone
* @return SectionZone
*/
//=================================================
private static NestedSection createNestedSection(JCas pJCas, SectionZone pSectionZone, SectionZone pNewSectionZone) {
	
	NestedSection statement = new NestedSection(pJCas );
	
	statement.setBegin( pSectionZone.getBegin());
	statement.setEnd( pSectionZone.getEnd());
	statement.setFromSection(pSectionZone.getSectionTypes());
	statement.setId( pSectionZone.getId());
	statement.setIndentation( pSectionZone.getIndentation());
	statement.setNestedSection( pSectionZone.getNestedSection());
	statement.setParentSection( pNewSectionZone);
	statement.setReferenceDate(pSectionZone.getReferenceDate());
	statement.setSectionName( pSectionZone.getSectionName());
	statement.setSectionTypes( pSectionZone.getSectionTypes());
	statement.setStatementDate( pSectionZone.getStatementDate());
	statement.addToIndexes();
	
	// remove the original section from the index
	pSectionZone.removeFromIndexes();
   return statement;
	
} // end Method createNestedSection() --------------

//=================================================
/**
* createSectionZone 
*  
* @param pJCas
* @param pBegin
* @param pEnd
* @param pIndentation
* @param pSectionName
* @param pSectionType
* @return SectionZone
*/
//=================================================
private final static SectionZone createSectionZone(JCas pJCas, int pBegin, int pEnd, int pIndentation, String pSectionName, String pSectionType ) {
	
	SectionZone statement = new SectionZone( pJCas);
	statement.setBegin( pBegin);
	statement.setEnd( pEnd);
	statement.setSectionName( pSectionName);
	statement.setId( "VUIMAUtil.repairContentHeading_");
	statement.addToIndexes();
	statement.setIndentation( pIndentation) ;
	statement.setSectionTypes( pSectionType );
// 	statement.setNestedSection(v);  to be done outside of this method
	
	return statement;
	
	
} // end Method createSectionZone() ---------------

//=================================================
/**
* getPreviousSectionZone 
*  
* @param pJCas
* @param pSectionZone
* @return Annotation
*/
// =================================================
private static SectionZone getPreviousSectionZone(JCas pJCas, Annotation pSectionZone) {
	  
	Annotation returnVal = null;
	if ( pSectionZone.getBegin() - 3 > 0 ) { 
	  
      List<Annotation> sectionZones = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, SectionZone.typeIndexID, pSectionZone.getBegin()-4, pSectionZone.getBegin() - 1  );

		if ( sectionZones != null && !sectionZones.isEmpty() ) {
		  UIMAUtil.uniqueAnnotations(sectionZones);
		  UIMAUtil.sortByOffset(sectionZones);
		  
		  for ( Annotation section : sectionZones) {
		    if ( section.getClass().getName().endsWith("SectionZone")) {
		      returnVal = section;
		      break;
		    }
		  }
		  
		}
	}
 return (SectionZone) returnVal;


} // end Method getPreviousSectionZone() ----------

//=================================================
/**
* getSectionZoneFor 
*  
* @param pContentHeading
* @return Annotation
*/
// =================================================
public static Annotation getSectionZoneFor(JCas pJCas, Annotation pContentHeading) {
	  
	Annotation returnVal = null;
	List<Annotation> sectionZones = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, SectionZone.typeIndexID, pContentHeading.getBegin(), pContentHeading.getEnd(), false);

	if ( sectionZones != null && !sectionZones.isEmpty() )
		returnVal = sectionZones.get(0);

 return returnVal;


} // end Method getSectionZoneFor() -----------------

// =================================================
/**
 * getContentHeadingForSectionZone returns the content heading
 * annotation if there is one
 * 
 * @param pJCas
 * @param pSectionZone
 * @return Annotation
*/
// =================================================
public final static Annotation getContentHeadingForSectionZone(JCas pJCas, Annotation pSectionZone) {
  Annotation returnVal = null;
  
   if ( pSectionZone != null ) {
     List<Annotation> contentHeadings = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, ContentHeading.typeIndexID, pSectionZone.getBegin(), pSectionZone.getEnd());

     if ( contentHeadings != null && !contentHeadings.isEmpty() )
      returnVal = contentHeadings.get(0);

   }
  
  return returnVal;
} // end Method getContentHeadingForSectionZone() ---

// =================================================
/**
 * getLines retrieves the line this annotation is in and the next
 * N number of lines
 * 
 * @param pJCas
 * @param pAnnotation
 * @param pN
 * @return List<Annotation>  of Line
*/
// =================================================
public final static List<Annotation> getLines(JCas pJCas, Annotation pAnnotation, int pN) {
  
  List<Annotation> returnVal = new ArrayList<Annotation>( pN);
  Annotation beginningLine =  getLine( pJCas, pAnnotation);
  
  if ( beginningLine != null ) {
    Annotation aLine = beginningLine;
    returnVal.add( beginningLine);
    for ( int i = 0; i < pN; i++ ) {
     
      List<Annotation> nextLines = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Line.typeIndexID, aLine.getEnd() + 1, aLine.getEnd() + 3);
      if ( nextLines != null && !nextLines.isEmpty() ) {
        returnVal.add( nextLines.get(0));
        aLine = nextLines.get(0);
      } else 
        break;  
    }
  }
  
  return returnVal;
  
} // end Method getLines() --------------------------

// =================================================
/**
 * getSemanticTypesFromTerm returns semantic type abbrs from lragr rows
 * where the semantic type has been filled out.  This is 
 * in the xx field. 
 * 
 * The content of this field might be multiple semantic types.  
 * In that case, the semantic types are colon separated in the
 * field.
 * 
 * These are changed to one per cell in the string array
 * 
 * This will automatically convert from semantic types to
 * the abbrs if needed.
 * 
 * @param pTerm
 * @return String[]
*/
// =================================================
public static String[] getSemanticTypesFromTerm(Annotation pTerm) {
  
 
  String[] semanticTypeAbbrs = null;
  
  if ( annotationIsALexicalElement ( pTerm )) {
 
    String semanticTypes = ((LexicalElement) pTerm).getSemanticTypes() ;
    
    if (semanticTypes != null && semanticTypes.length() > 0 )  {
      String semanticTypez[] = U.split( semanticTypes, ":");
    
      // -----------------------
      // Convert these to semantic type abbreviations if they are not
      // already
    
        semanticTypeAbbrs = convertToSemanticTypeAbbrs( semanticTypez );
      }
   }
  
        
  return semanticTypeAbbrs;
}  // end Method getSemanticTypes() ----------------

// =================================================
/**
 * getSecondaryCategoriesFromTerm 
 *  returns secondary categories from lragr rows
 * where the secondary categories has been filled out.  This is 
 * in the xx field. 
 * 
 * The content of this field might be multiple  types.  
 * In that case, the  types are colon separated in the
 * field.
 * 
 * These are changed to one per cell in the string array
 *
 * @param pTerm
 * @return String[] 
*/
// =================================================
  public static String[] getSecondaryCategoriesFromTerm(Annotation pTerm) {
    String[] secondaryCategoriez = null;
    HashSet<String> secondaryCategoriesHash = new HashSet<String>();

    if (annotationIsALexicalElement(pTerm)) {

      StringArray otherInfoz = ((LexicalElement) pTerm).getOtherFeatures();
      if (otherInfoz != null) {
        for (int i = 0; i < otherInfoz.size(); i++) {
          String otherInfo = otherInfoz.get(i);
          String[] cols = U.split(otherInfo);
          if (cols != null && cols.length > 5) {
            String secondaryCategories = cols[5];
            if (secondaryCategories != null && !secondaryCategories.isEmpty()) {
              secondaryCategoriez = U.split(secondaryCategories, ":");
              if (secondaryCategoriez != null) for (String cat : secondaryCategoriez)
                secondaryCategoriesHash.add(cat);

            }
          }
        }
      }
      if (secondaryCategoriesHash != null && secondaryCategoriesHash.size() > 0)
        secondaryCategoriez = secondaryCategoriesHash.toArray(new String[secondaryCategoriesHash.size()]);
    }

    return secondaryCategoriez;
  } // end Method getSecondaryCategoriesFromTerm() ---

// =================================================
/**
 * convertToSemanticTypeAbbrs 
 * 
 * @param pSemanticTypes
 * @return String[]
*/
// =================================================
 public static String[] convertToSemanticTypeAbbrs(String[] pSemanticTypes) {
 
   String[] abbrs = null;
   
   if ( pSemanticTypes != null && pSemanticTypes.length > 0 ) {
   
     abbrs = new String[ pSemanticTypes.length ];
     for ( int i = 0; i < pSemanticTypes.length; i++ ) 
       abbrs[i] = UMLSUtils.getSemanticTypeAbbr(  pSemanticTypes[i]);
   }
   
   
   return abbrs;
} // end Method convertToSemanticTypeAbbrs()--------

// =================================================
/**
 * annotationIsALexicalElement returns true if this
 * annotation is a LexicalElement
 * 
 * @param pTerm
 * @return boolean 
*/
// =================================================
 private static boolean annotationIsALexicalElement(Annotation pTerm) {
   boolean returnVal = false;
   String annotationType = pTerm.getType().getName();
  if ( pTerm != null && annotationType.contentEquals(  "gov.va.chir.model.LexicalElement")) 
    returnVal = true;
  
  return returnVal;
} // end Method getSemanticTypes() -------------------

// =================================================
/**
 * getSabsFromTerm returns the sources  from lragr rows
 * where the sources have been filled out.  This is 
 * in the xx field. 
 * 
 * The content of this field might be multiple sources.  
 * In that case, the sources are colon separated in the
 * field.
 * 
 * These are changed to one per cell in the string array.
 * 
 * @param pTerm  (assumes this is a lexicalElement) 
 * @return String
*/
// =================================================
 public static String getSabsFromTerm(Annotation pTerm) {
 
   String returnVal = null;
  StringBuffer sabs = new StringBuffer();
  
  if ( annotationIsALexicalElement ( pTerm )) {
 
    StringArray otherInfoz = ((LexicalElement) pTerm).getOtherFeatures() ;
    if ( otherInfoz != null ) {
      for (int i = 0 ; i < otherInfoz.size(); i++  ) {
        String row = otherInfoz.get(i);
        if ( row != null  && row.trim().length() > 0 ) {
          String[] cols = U.split(row);
          if ( cols != null && cols.length > 0 ) {
             sabs.append( cols[0]);
             sabs.append(":");
          }
        }      
      }
      if ( sabs != null && sabs.toString().trim().length() > 0 ) {
        returnVal = sabs.toString().trim();
        returnVal = returnVal.substring(0, returnVal.length() -1 );
      }
    }
        
  }
  return  returnVal;
} // end Method getSabs() ---------------------------

// =================================================
/**
 * getSabsFromTerm returns the source(s) for this cui
 * 
 * @param pTerm
 * @param pCui
 * @return String
*/
// =================================================
  public final static String getSabsFromTerm(Annotation pTerm, String pCui) {
 
    String returnVal = null;
    StringBuffer sabs = new StringBuffer();

    if (annotationIsALexicalElement(pTerm)) {

      StringArray otherInfoz = ((LexicalElement) pTerm).getOtherFeatures();
      if (otherInfoz != null) {
        for (int i = 0; i < otherInfoz.size(); i++) {
          String row = otherInfoz.get(i);

          if (row.contains(pCui)) {

            if (row != null && row.trim().length() > 0) {
              String[] cols = U.split(row);
              if (cols != null && cols.length > 0) {
                sabs.append(cols[0]);
                sabs.append(":");
              }
            }
          }
          if (sabs != null && sabs.toString().trim().length() > 0) {
            returnVal = sabs.toString().trim();
            returnVal = returnVal.substring(0, returnVal.length() - 1);
          }
        }
      }
   }
  return returnVal;
} // end Method getSabsFromTerm() --------------------

// =================================================
/**
 * getOutputFileName returns the path to the output file
 * assembled from the pieces passed in - the outputDir, the documentId from
 * the jcas, and the extension passed in
 * 
 * @param pJCas
 * @param pOutputDir
 * @param pExtension  (no period please)
 * @return String 
*/
// =================================================
  public final static String getOutputFileName(JCas pJCas, String pOutputDir, String pExtension) {

    String returnVal = null;
    try {
      String documentId = VUIMAUtil.getDocumentId(pJCas);
      if ( documentId.contains("/"))
        documentId = U.getFileNameOnly(documentId);
      documentId = documentId.replace(" ",  "_");
       returnVal = pOutputDir + "/" + documentId + "." + pExtension;
    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, VUIMAUtil.class, "getOutputFileName", "Issue with getting the output fileName " + e.toString());
      throw e;
    }
    return returnVal ;
} // end Method getOutputFileName() ---------------





} // end Class VUIMAUtil ----------------------------
