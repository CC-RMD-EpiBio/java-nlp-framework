// =================================================
/**
 * FromBioC creates uima cas's from files that are in the
 * bioC format
 *
 *
 * @author  Guy Divita 
 * @created March 12, 2014
 *
 * *  
 *   *
 *   * --- Copyright Notice: --------------------------------------------------
 *   *
 *   * Copyright 2014 United States Department of Veterans Affairs, 
 *   *                Health Services Research & Development Service
 *   *
 *   *  Licensed under the Apache License, Version 2.0 (the "License");
 *   *  you may not use this file except in compliance with the License.
 *   *  You may obtain a copy of the License at
 *   *
 *   *      http://www.apache.org/licenses/LICENSE-2.0
 *   *
 *   *  Unless required by applicable law or agreed to in writing, software
 *   *  distributed under the License is distributed on an "AS IS" BASIS,
 *   *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   *  See the License for the specific language governing permissions and
 *   *  limitations under the License. 
 *   * 
 *   * --- End Copyright Notice: ----------------------------------------------
 *   *
 * 

 */
// ================================================
package gov.nih.nlm.nls.vtt.bioC;


//import gov.va.vinci.nlp.framework.utils.U;

import gov.nih.nlm.nls.vtt.model.Markup;
import gov.nih.nlm.nls.vtt.model.Markups;
import gov.nih.nlm.nls.vtt.model.VttDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bioc.BioCAnnotation;
import bioc.BioCDocument;
import bioc.BioCLocation;
import bioc.BioCPassage;

public class FromBioC  {

  
  // =======================================================
  /**
   * getBioCTextFromDocumentPassages returns the aglomerated text
   * from all the passages
   * 
   * @param pBioCDocument
   * @return pVttDocument
   */
  // =======================================================
  public VttDocument toVTTDocument(BioCDocument pBioCDocument) {
    
    VttDocument vttDocument = new VttDocument();
  
    List<BioCPassage> passages = pBioCDocument.getPassages();
    
    if ( passages != null ) {
      createMarkups(vttDocument, passages);
      
    }
   return vttDocument;
    
  }  // End Method toVTTDocument() ======================
  
  
  

    // end Method getNext () ----------------------------
        
  // =======================================================
  /**
   * convertAnnotations transforms the annotations from passages 
   * to vtt markups in a vtt document 
   * 
   * @param pVttDoc
   * @param passages
   * @return
   */
  // =======================================================
  public void createMarkups(VttDocument pVttDoc, List<BioCPassage> passages) {
  
    Markups markups =  pVttDoc.getMarkups();
    
    for ( BioCPassage passage : passages ) {
      List<BioCAnnotation> boiCAnnotations = passage.getAnnotations();
      if ( boiCAnnotations != null && boiCAnnotations.size() > 0) {
        for ( BioCAnnotation annotation: boiCAnnotations ) {
          List<BioCLocation> locations = annotation.getLocations();
          BioCLocation location = locations.get(0);
          ArrayList<String >attributesPairs  = null;
          String annotationId = annotation.getID();
          
          int       beginOffset = location.getOffset();
          int              endOffset = beginOffset + location.getLength();
          Map<String, String> infons = annotation.getInfons();
          String  annotationType = "Annotation";
          
          if ( infons != null && infons.size() > 0 ) {
            Set<String> attributeKeys = infons.keySet();
            attributesPairs = new ArrayList<String>();
            for ( String attributeKey : attributeKeys) {
              String attributeValue = infons.get(attributeKey);
              
              if (attributeKey.equals("Type")) 
                annotationType = attributeValue;
              else  
                attributesPairs.add( attributeKey + "|" + attributeValue );
            } // end loop through the infons for this annotation
          } // end if there are any infons
          Markup markup = createMarkup (  annotationId, annotationType, beginOffset, endOffset, attributesPairs);
          markups.addMarkup(markup);
        } // end loop thru annotations
      }// end if there are any annotations
    } // end loop through passages
    
    pVttDoc.setMarkups(markups);
  
  }  // End Method convertAnnotations() ======================
  

    // =======================================================
  /**
   * createAnnotation 
   * 
   * @param pJcas
   * @param pAnnotationId
   * @param pAnnotationType
   * @param pBeginOffset
   * @param pEndOffset
   * @param pAttributeValuePairs
   */
  // =======================================================
  private Markup createMarkup(String pAnnotationId, String pAnnotationType, int pBeginOffset, int pEndOffset, ArrayList<String> pAttributeValuePairs) {
    
      Markup returnValue = new Markup();
      // ----------------------------------------------
      // Map this label to a uima class
      // ----------------------------------------------
      StringBuffer metaData = new StringBuffer(); 
   
      returnValue.setOffset(pBeginOffset);
      returnValue.setLength(pEndOffset - pBeginOffset);
      metaData.append("id="); metaData.append(pAnnotationId); metaData.append("|");
  
      // ----------------------------------------------------
      // find the features (featureElements) associated with this annotation
      // (feature)
      // -----------------------------------------------------
     for ( String aFeature: pAttributeValuePairs ) {
        
        String [] cols = split( aFeature);
        String featureName = cols[0];
        String featureValue = cols[1];
          
        if (featureValue != null) {
          metaData.append(featureName); metaData.append("="); metaData.append(featureValue); metaData.append("|");
        } else {
          metaData.append(featureName);metaData.append("|");
        }
     } // end loop through feature values
        
     returnValue.setAnnotation(metaData.toString());

    return returnValue;
   
	}   // End Method createMarkup() ======================

   
  // ------------------------------------------
    /**
     * createDocumentHeaderAnnotation
     *
     *
     * @param pJCas
     * @param pDocumentId
     * @param pDocumentTitle
     * @param pDocumentType
     * @param pDocumentName
     * @param pDocumentSpan
     * @param pPatientId
     * @param pReferenceDate,
     * @param pDocumentMetaData
     */
    // ------------------------------------------
    public Markup createDocumentHeaderAnnotation(VttDocument pVttDoc, 
                                                String pDocumentId, 
                                                String pDocumentTitle, 
                                                String pDocumentType,
                                                String pDocumentName,
                                                int    pDocumentSpan,
                                                String pPatientId,
                                                String pReferenceDate,
                                                String pDocumentMetaData) {
      
      Markup documentHeader = new Markup();
      documentHeader.setOffset(0);
      documentHeader.setLength(pDocumentSpan);
      
      StringBuffer metaData = new StringBuffer();
      metaData.append("doucmentId="    + pDocumentId + "|");
      metaData.append("patientID="     + pPatientId + "|");
      metaData.append("referenceDate=" + pReferenceDate + "|");
      metaData.append("documentName="  + pDocumentName + "|");
      metaData.append("documentType="  + pDocumentType + "|");
      metaData.append("documentTitle=" + pDocumentTitle + "|");
      metaData.append("otherMetaData=" + pDocumentMetaData.replace('|', ':'));
      
      documentHeader.setAnnotation(metaData.toString());
      
      Markups markups = pVttDoc.getMarkups();
      markups.addMarkup(documentHeader);
      
      return documentHeader;
    
      
      
    }  // End Method createDocumentHeaderAnnotation() -----------------------


    // =======================================================
    /**
     * split splits a row into a String[] of number of rows
     *    This method is more efficient.
     *    
     *    (borrowed from gov.va.vinci.nlp.framework.utils.U from the utils.general package. 
     *     I didn't want introduce the dependency for just one call)
     * 
     * @param pRow
     * @return String[] 
     */
    // =======================================================
    private static String[] split(String pRow) {
    
      char splitChar = '|';
      char[] cs = pRow.toCharArray();
      int numberOfCols = getNumberOfCols(cs, splitChar);
      
      String[] cols = new String[ numberOfCols];
      int currentCol = 0;
      int buffSize = 0;
      char[] buff = new char[cs.length];
      for (int i = 0; i < cs.length; i++ ) {
       
       
        if ( cs[i] != splitChar) {
          buff[buffSize] = cs[i];
          buffSize++;
        } else {
         char[] trimmedBuff = new char[buffSize];
         for (int j = 0; j< buffSize; j++ ) trimmedBuff[j] = buff[j];
         cols[ currentCol ] = new String( trimmedBuff);
         buffSize = 0;
         currentCol++;
        }
        
        
      
      } // end of loop
      
      // add the last buff to the next column
    
        
      char[] trimmedBuff = new char[buffSize];
      for (int j = 0; j< buffSize; j++ ) trimmedBuff[j] = buff[j];
      cols[ currentCol ] = new String( trimmedBuff);
      
      
      return cols;
    } // End Method split() ======================
    
 // =======================================================
    /**
     * getNumberOfCols returns the number of columns in this row
     *  (That's the number of splitChars seen + 1
     * @param pRow
     * @param splitChar
     * @return int
     */
    // =======================================================
    private static int getNumberOfCols(char[] pRow, char splitChar) {
      
      int cols = 0;
      for (int i = 0; i < pRow.length; i++ )
        if ( pRow[i] == splitChar ) cols++;
      return cols + 1;
    } // End Method getNumberOfCols() ======================
    
	
  // ----------------------------------------
	// Class Variables
	// ----------------------------------------

  

	
} // end Class MultiAnnotationRecordCollectionReader() ----

