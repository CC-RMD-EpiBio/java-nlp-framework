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
 * SnippetMarkup is a container for the markup info 
 * specific to the v3NLPFramework Snippets
 *
 *
 * @author  Guy Divita 
 * @created Sep 18, 2017
 *
 **  *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.snippet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.StringTokenizer;

import gov.nih.cc.rmd.nlp.framework.utils.U;

// ===========================================
/**
 * Public Types 
 */
// ===========================================
public class SnippetMarkup {

  
  // ======================================================
  /**
   * Constructor generic constructor
   *
   */
  // ======================================================
 public SnippetMarkup() {
   
 }  // End Constructor ====================================
  
 
 // =======================================================
   /**
    * Constructor 
    * 
    * @param aSnippet
    */
   // ======================================================
  public SnippetMarkup(SnippetContainer aSnippet) {
    
    
    
    this.setBeginOffset(      String.valueOf(aSnippet.getBeginOffset()));
    this.setEndOffset(        String.valueOf(aSnippet.getEndOffset() ));
    this.setLabel(                          aSnippet.getDecision() );
    this.setLabelCategory(                  aSnippet.getLabelCategory() );// should be empty normally
    this.setFocusText(                      aSnippet.getFocus() );
    this.setAssertionStatus(                aSnippet.getAssertionStatus() );
    this.setConditional(   Boolean.toString(aSnippet.getConditional()) );
    this.setSubject(                        aSnippet.getSubject());
    this.setHistorical(    Boolean.toString(aSnippet.getHistorical()));
    this.setOtherFeatures(                  aSnippet.getOtherFeatures());
    this.setLabelCategory(                aSnippet.getCategory() );
    this.setDocumentId(                     aSnippet.getDocumentId() );
    this.setPatientId(                      aSnippet.getPatientId() );
    this.setSnippetId(       String.valueOf(aSnippet.getSnippetId()));
    this.setAnnotationId(                   aSnippet.getAnnotationId() );
    this.setFocus(                          aSnippet.getFocus() );
    this.setFocusBeginOffset(String.valueOf(aSnippet.getFocusBeginOffset() ) );
    this.setFocusEndOffset(  String.valueOf(aSnippet.getFocusEndOffset()) );
    this.setFileSnippetId(   String.valueOf(aSnippet.getFileSnippetId()) );
             
    
  }  // End Constructor SnippetMarkup =============
    
  

  // =======================================================
  /**
   * parse parses the annotation into a v3NLPFramework snippet
   * annotation
   * 
   * @param pMarkupRow
   * @return SnippetMarkup
   * @throws Exception 
   */
  // ======================================================
  public final static SnippetMarkup parse(String pMarkupRow) throws Exception {

    SnippetMarkup aMarkup = null;
   try {
      aMarkup = new SnippetMarkup();
      String[] markupFields = U.split(pMarkupRow, "|");
  
      aMarkup.setBeginOffset(markupFields[0]);
      aMarkup.setEndOffset(markupFields[1]);
      aMarkup.setLabel(markupFields[2]);
      aMarkup.setCategory(markupFields[3]); // should be empty normally
   
      // ---------------------------
      // Note for Guy - in 2072 - the OtherFeatures has Pipe delimiters! making parsing with piped fields difficult here.
     
      
      StringBuffer  v3NLPFrameworkSnippetFields = new StringBuffer();
      for ( String field: markupFields ) {
    	  if ( field.contains("="))
    		  v3NLPFrameworkSnippetFields.append( field + "<::>");
      }
     
      if ( markupFields.length > 7)
    	  aMarkup.setFocusText(markupFields[7]); 
      
      if (v3NLPFrameworkSnippetFields != null) {
        String[] snippetFields = U.split_StringDelimited(v3NLPFrameworkSnippetFields.toString(), "<::>");
        if (snippetFields != null) {
          for (String snippetField : snippetFields) {
             String[] slotValue = U.split(snippetField, "=");
             if ( slotValue != null && slotValue.length > 1 && slotValue[0] != null && slotValue[1] != null )
             
             switch( slotValue[0].trim()) {
               case "assertionStatus" : aMarkup.setAssertionStatus(slotValue[1]);  break;
               case "conditional"     : aMarkup.setConditional(slotValue[1]);      break;
               case "conditionalStatus" : aMarkup.setConditional(slotValue[1]);      break;
               case "subject"         : aMarkup.setSubject(slotValue[1]);          break;
               case "subjectStatus"   : aMarkup.setSubject(slotValue[1]);          break;
               case "historical"      : aMarkup.setHistorical(slotValue[1]);       break;
               case "historicalStatus": aMarkup.setHistorical(slotValue[1]);       break;
               case "otherFeatures"   : aMarkup.setOtherFeatures(slotValue[1]);    break;
               case "category"        : aMarkup.setLabelCategory(slotValue[1]);    break;
               case "categories"      : aMarkup.setLabelCategory(slotValue[1]);    break;
               case "documentId"      : aMarkup.setDocumentId(slotValue[1]);       break;
               case "patientId"       : aMarkup.setPatientId(slotValue[1]);        break;
               case "snippetId"       : 
                 aMarkup.setSnippetId(slotValue[1]);        break;
               case "annotationId"    : aMarkup.setAnnotationId(slotValue[1]);     break;
               case "focus"           : aMarkup.setFocus(slotValue[1]);            break;
               case "focusBeginOffset": aMarkup.setFocusBeginOffset(slotValue[1]); break;
               case "focusEndOffset"  : aMarkup.setFocusEndOffset(slotValue[1]);   break;
               case "fileSnippetId"   : aMarkup.setFileSnippetId(slotValue[1]);    break;
               default:  
            	  aMarkup.setOtherFeatures( aMarkup.getOtherFeatures() + ":" + snippetField);
               //  System.err.println("Missed this field :|" + snippetField + "|");  ;
             } // end Switch 
          } // end loop thru markup fields
        } // end if there are snippet fields
      } // end if there is snippet field text
   } catch ( Exception e ) {
     e.printStackTrace();
     System.err.println("Issue within method parse " + e.getMessage() );
     throw new Exception (e);
   }
   return aMarkup;
  } // End Method parse ====================================

  
    
 


  // =======================================================
   /**
    * toString formats the markup for v3NLPFramework formatted snippets
    * 
    *   all in one line: 
    *   
    *  beginOffset|length|decision|category|
    buff.append( "assertionStatus=Asserted<::>      <-- Asserted|Negated
    *                                    conditional=false<::>             <-- true|false
    *                                    subject=subject<::>               <-- subject|other|generic|null
    *                                    historical=false<::>              <-- true|false
    *                                    otherFeatures=unknown<::>         <-- a catch all for other data to pass thru
    *                                    category=Concept<::>              <-- whatever the annotation type was in the original file
    *                                    documentId=001<::>
    *                                    patientId=001<::>
    *                                    snippetId=0<::>
    *                                    annotationId=001.txt:01:06<::>
    *                                    focus=smith<::>          <-- should be the same as the last column
    *                                    focusBeginOffset=01<::>
    *                                    focusEndOffset=09<::>
    *                                    fileSnippetId=0<::>|
    *                                    smith                    <-- newlines and unprintables taken out
    *                                    
    * @return String
    *
    */
   // ======================================================	
  @Override
  public String toString() {
   
    StringBuffer buff = new StringBuffer();
   
    String length = String.valueOf(Integer.parseInt(endOffset) - Integer.parseInt(beginOffset));
    
    buff.append( this.beginOffset);     buff.append( "|" );
    buff.append( length);               buff.append( "|" );
    buff.append( this.label );          buff.append( "|" );
    buff.append( this.category);        buff.append( "|" );
    buff.append( this.assertionStatus); buff.append( "|" );
    
    buff.append("conditional="      + this.conditional );       buff.append( "<::>");
    buff.append("subject="          + this.subject );           buff.append( "<::>");
    buff.append("historical="       + this.historical );        buff.append( "<::>");
    buff.append("otherFeatures="    + this.otherFeatures );     buff.append( "<::>");
    buff.append("category="         + this.category );          buff.append( "<::>");
    buff.append("documentId="       + this.documentId );        buff.append( "<::>");
    buff.append("patientId="        + this.patientId );         buff.append( "<::>");
    buff.append("snippetId="        + this.snippetId );         buff.append( "<::>");
    buff.append("annotationId="     + this.annotationId );      buff.append( "<::>");
    buff.append("focus="            + this.focus );             buff.append( "<::>");
    buff.append("focusBeginOffset=" + this.focusBeginOffset );  buff.append( "<::>");
    buff.append("focusEndOffset="   + this.focusEndOffset );    buff.append( "<::>");
    buff.append("fileSnippetId="    + this.fileSnippetId );     buff.append( "|");
        
    buff.append(this.focus);
    buff.append("\n");
    
   
    return buff.toString();
    } // End Method toString() =============================


  // =======================================================
  /**
   * getBeginOffset 
   * 
   * @return the beginOffset
   *
   */
  // ======================================================
  
  public final String getBeginOffset() {
    return beginOffset;
  } // End Method getBeginOffset} ============}

  // =======================================================
  /**
   * setBeginOffset 
   * 
   * @param beginOffset the beginOffset to set
   */
  // ======================================================
  public final void setBeginOffset(String beginOffset) {
    this.beginOffset = beginOffset;
  } // End Method setBeginOffset} ============}

  // =======================================================
  /**
   * getEndOffset 
   * 
   * @return the endOffset
   *
   */
  // ======================================================
  
  public final String getEndOffset() {
    return endOffset;
  } // End Method getEndOffset} ============}

  // =======================================================
  /**
   * setEndOffset 
   * 
   * @param endOffset the endOffset to set
   */
  // ======================================================
  public final void setEndOffset(String endOffset) {
    this.endOffset = endOffset;
  } // End Method setEndOffset} ============}

  // =======================================================
  /**
   * getCategory 
   * 
   * @return the category
   *
   */
  // ======================================================
  
  public final String getCategory() {
    return category;
  } // End Method getCategory} ============}

  // =======================================================
  /**
   * setCategory 
   * 
   * @param category the category to set
   */
  // ======================================================
  public final void setCategory(String category) {
    this.category = category;
  } // End Method setCategory} ============}

  // =======================================================
  /**
   * getFocusText 
   * 
   * @return the focusText
   *
   */
  // ======================================================
  
  public final String getFocusText() {
    return focusText;
  } // End Method getFocusText} ============}

  // =======================================================
  /**
   * setFocusText 
   * 
   * @param focusText the focusText to set
   */
  // ======================================================
  public final void setFocusText(String focusText) {
    this.focusText = focusText;
  } // End Method setFocusText} ============}

  // =======================================================
  /**
   * getAssertionStatus 
   * 
   * @return the assertionStatus
   *
   */
  // ======================================================
  
  public final String getAssertionStatus() {
    return assertionStatus;
  } // End Method getAssertionStatus} ============}

  // =======================================================
  /**
   * setAssertionStatus 
   * 
   * @param assertionStatus the assertionStatus to set
   */
  // ======================================================
  public final void setAssertionStatus(String assertionStatus) {
    this.assertionStatus = assertionStatus;
  } // End Method setAssertionStatus} ============}

  // =======================================================
  /**
   * getConditional 
   * 
   * @return the conditional
   *
   */
  // ======================================================
  
  public final String getConditional() {
    return conditional;
  } // End Method getConditional} ============}

  // =======================================================
  /**
   * setConditional 
   * 
   * @param conditional the conditional to set
   */
  // ======================================================
  public final void setConditional(String conditional) {
    this.conditional = conditional;
  } // End Method setConditional} ============}

  // =======================================================
  /**
   * getSubject 
   * 
   * @return the subject
   *
   */
  // ======================================================
  
  public final String getSubject() {
    return subject;
  } // End Method getSubject} ============}

  // =======================================================
  /**
   * setSubject 
   * 
   * @param subject the subject to set
   */
  // ======================================================
  public final void setSubject(String subject) {
    this.subject = subject;
  } // End Method setSubject} ============}

  // =======================================================
  /**
   * getHistorical 
   * 
   * @return the historical
   *
   */
  // ======================================================
  
  public final String getHistorical() {
    return historical;
  } // End Method getHistorical} ============}

  // =======================================================
  /**
   * setHistorical 
   * 
   * @param historical the historical to set
   */
  // ======================================================
  public final void setHistorical(String historical) {
    this.historical = historical;
  } // End Method setHistorical} ============}

  // =======================================================
  /**
   * getOtherFeatures 
   * 
   * @return the otherFeatures
   *
   */
  // ======================================================
  
  public final String getOtherFeatures() {
    return otherFeatures;
  } // End Method getOtherFeatures} ============}

  // =======================================================
  /**
   * setOtherFeatures 
   * 
   * @param otherFeatures the otherFeatures to set
   */
  // ======================================================
  public final void setOtherFeatures(String otherFeatures) {
    this.otherFeatures = otherFeatures;
  } // End Method setOtherFeatures} ============}

  // =======================================================
  /**
   * getLabelCategory 
   * 
   * @return the LabelCategory
   *
   */
  // ======================================================
  
  public final String getLabelCategory() {
    return this.labelCategory;
  } // End Method getLabelCategory} ============}

  // =======================================================
  /**
   * setLabelCategory 
   * 
   * @param pLabelCategory the labelCategory to set
   */
  // ======================================================
  public final void setLabelCategory(String pLabelCategory) {
    this.labelCategory = pLabelCategory;
  } // End Method setLabelCategory} ============}

  // =======================================================
  /**
   * getDocumentId 
   * 
   * @return the documentId
   *
   */
  // ======================================================
  
  public final String getDocumentId() {
    return documentId;
  } // End Method getDocumentId} ============}

  // =======================================================
  /**
   * setDocumentId 
   * 
   * @param documentId the documentId to set
   */
  // ======================================================
  public final void setDocumentId(String documentId) {
    this.documentId = documentId;
  } // End Method setDocumentId} ============}

  // =======================================================
  /**
   * getPatientId 
   * 
   * @return the patientId
   *
   */
  // ======================================================
  
  public final String getPatientId() {
    return patientId;
  } // End Method getPatientId} ============}

  // =======================================================
  /**
   * setPatientId 
   * 
   * @param patientId the patientId to set
   */
  // ======================================================
  public final void setPatientId(String patientId) {
    this.patientId = patientId;
  } // End Method setPatientId} ============}

  // =======================================================
  /**
   * getSnippetId 
   * 
   * @return the snippetId
   *
   */
  // ======================================================
  
  public final String getSnippetId() {
    return snippetId;
  } // End Method getSnippetId} ============}

  // =======================================================
  /**
   * setSnippetId 
   * 
   * @param snippetId the snippetId to set
   */
  // ======================================================
  public final void setSnippetId(String snippetId) {
    this.snippetId = snippetId;
  } // End Method setSnippetId} ============}

  // =======================================================
  /**
   * getAnnotationId 
   * 
   * @return the annotationId
   *
   */
  // ======================================================
  
  public final String getAnnotationId() {
    return annotationId;
  } // End Method getAnnotationId} ============}

  // =======================================================
  /**
   * setAnnotationId 
   * 
   * @param annotationId the annotationId to set
   */
  // ======================================================
  public final void setAnnotationId(String annotationId) {
    this.annotationId = annotationId;
  } // End Method setAnnotationId} ============}

  // =======================================================
  /**
   * getFocus 
   * 
   * @return the focus
   *
   */
  // ======================================================
  
  public final String getFocus() {
    return focus;
  } // End Method getFocus} ============}

  // =======================================================
  /**
   * setFocus 
   * 
   * @param focus the focus to set
   */
  // ======================================================
  public final void setFocus(String focus) {
    this.focus = focus;
  } // End Method setFocus} ============}

  // =======================================================
  /**
   * getFocusBeginOffset 
   * 
   * @return the focusBeginOffset
   *
   */
  // ======================================================
  
  public final String getFocusBeginOffset() {
    return focusBeginOffset;
  } // End Method getFocusBeginOffset} ============}

  // =======================================================
  /**
   * setFocusBeginOffset 
   * 
   * @param focusBeginOffset the focusBeginOffset to set
   */
  // ======================================================
  public final void setFocusBeginOffset(String focusBeginOffset) {
    this.focusBeginOffset = focusBeginOffset;
  } // End Method setFocusBeginOffset} ============}

  // =======================================================
  /**
   * getFocusEndOffset 
   * 
   * @return the focusEndOffset
   *
   */
  // ======================================================
  
  public final String getFocusEndOffset() {
    return focusEndOffset;
  } // End Method getFocusEndOffset} ============}

  // =======================================================
  /**
   * setFocusEndOffset 
   * 
   * @param focusEndOffset the focusEndOffset to set
   */
  // ======================================================
  public final void setFocusEndOffset(String focusEndOffset) {
    this.focusEndOffset = focusEndOffset;
  } // End Method setFocusEndOffset} ============}

  // =======================================================
  /**
   * getFileSnippetId 
   * 
   * @return the fileSnippetId
   *
   */
  // ======================================================
  public final String getFileSnippetId() {
    return fileSnippetId;
  } // End Method getFileSnippetId} ============}

  // =======================================================
  /**
   * setFileSnippetId 
   * 
   * @param fileSnippetId the fileSnippetId to set
   */
  // ======================================================
  public final void setFileSnippetId(String fileSnippetId) {
    this.fileSnippetId = fileSnippetId;
  } // End Method setFileSnippetId} ============}
  

  // =======================================================
  /**
   * setLabel 
   * 
   * @param label the label to set
   */
  // ======================================================
  public final void setLabel(String label) {
    this.label = label;
  } // End Method setLabel} ============}

  // =======================================================
   /**
    * getLabel 
    * 
    * @return String
    *
    */
   // ======================================================	
  public final String getLabel() {
      return this.label;
    
    } // End Method getLabel ============
    
  
  // ==================================
  // Class and Global Variables
  // ==================================
     public static final String SNIPPET_LABEL = "Snippet";

     private String      beginOffset = null;
     private String        endOffset = null;
     private String            label = null;
     private String    labelCategory = null;
     private String        focusText = null;
 
     private String  assertionStatus = null;
     private String      conditional = null;
     private String          subject = null;
     private String       historical = null;
     private String    otherFeatures = null;
     private String         category = null;
     private String       documentId = null;
     private String        patientId = null;
     private String        snippetId = null;
     private String     annotationId = null;
     private String            focus = null;
     private String focusBeginOffset = null;
     private String   focusEndOffset = null;
     private String    fileSnippetId = null;
      
  
} // End Class SnippetMarkup ============
