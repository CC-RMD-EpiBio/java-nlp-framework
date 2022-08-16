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
 * RedSnippetContainer holds snippets and annotations for
 * Red Cat/Ex style snippets.
 *
 * @author  Guy Divita 
 * @created Sep 22, 2017
 *
 **  *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.snippet;

// ===========================================
/**
 * Public Types 
 */
// ===========================================
public class RedSnippetContainer {

 

  // =======================================================
   /**
    * Constructor from a V3NLPFramework Snippet Container
    * 
    * @param pV3nlpSnippet
    */
   // ======================================================
  public RedSnippetContainer(SnippetContainer pV3nlpSnippet) {
    
    this.patientICN       = pV3nlpSnippet.getPatientId();
    this.document         = pV3nlpSnippet.getDocumentId();
    this.snippetNum       = pV3nlpSnippet.getSnippetId();
    this.snippetText      = pV3nlpSnippet.getSnippet();
    this.label            = pV3nlpSnippet.getDecision();
    this.focus            = pV3nlpSnippet.getFocus();
    this.focusBeginOffset = pV3nlpSnippet.getFocusBeginOffset();
    
    
  }  // End Constructor RedSnippetContainer =============
    
  
  // =======================================================
   /**
    * toString returns a string of a red cat/ex formatted
    * snippet of the form:
    * 
    * PatientICN: 1
    * Document: freport4.txt
    * Snippet Num: 1
    * Snippet Text: 
    * xxxxxx
    * xxxxxx
    * xxxxxx
    * ----------------------------------------------------------------------------------
    * @return String
    *
    */
   // ======================================================	
  public String toString() {
   
    StringBuffer buff = new StringBuffer();
    
     try {
       buff.append( patientICN_SLOT);  buff.append(": ");  buff.append( this.patientICN);  buff.append("\n");
       buff.append( document_SLOT);    buff.append(": ");  buff.append( this.document);    buff.append("\n");
       buff.append( snippetNum_SLOT);  buff.append(": ");  buff.append( this.snippetNum);  buff.append("\n");
       buff.append( snippetText_SLOT); buff.append(":\n"); buff.append( this.snippetText);  // buff.append("\n");
       buff.append(snippetDelimiter);                                                      buff.append("\n");
        
    
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method toString2 " + e.getMessage() );
        throw e;
      }
     
     return buff.toString();
    } // End Method toString2 ============
    
  
   
  // =======================================================
   /**
    * getMarkups returns a RedSnippetsMarkup (which includes
    * really 4 markup lines for a red snippet)
    * 
    * @param pBeginOffset
    * @return RedSnippetMarkup
    *
    */
   // ======================================================	
  public final RedSnippetMarkups getMarkups(int pBeginOffset) {
   
    if ( this.markups == null ) 
      this.markups = new RedSnippetMarkups( this, pBeginOffset);
    return this.markups;
  } // End Method getMarkups() ==============================


  

  // =======================================================
  /**
   * getPatientICN 
   * 
   * @return the patientICN
   *
   */
  // ======================================================
  
  public String getPatientICN() {
    return patientICN;
  } // End Method getPatientICN} ============}


  // =======================================================
  /**
   * setPatientICN 
   * 
   * @param patientICN the patientICN to set
   */
  // ======================================================
  public void setPatientICN(String patientICN) {
    this.patientICN = patientICN;
  } // End Method setPatientICN} ============}


  // =======================================================
  /**
   * getDocument 
   * 
   * @return the document
   *
   */
  // ======================================================
  
  public String getDocument() {
    return document;
  } // End Method getDocument} ============}


  // =======================================================
  /**
   * setDocument 
   * 
   * @param document the document to set
   */
  // ======================================================
  public void setDocument(String document) {
    this.document = document;
  } // End Method setDocument} ============}


  // =======================================================
  /**
   * getSnippetNum 
   * 
   * @return the snippetNum
   *
   */
  // ======================================================
  
  public int getSnippetNum() {
    return snippetNum;
  } // End Method getSnippetNum} ============}


  // =======================================================
  /**
   * setSnippetNum 
   * 
   * @param snippetNum the snippetNum to set
   */
  // ======================================================
  public void setSnippetNum(int snippetNum) {
    this.snippetNum = snippetNum;
  } // End Method setSnippetNum} ============}


  // =======================================================
  /**
   * getSnippetText 
   * 
   * @return the snippetText
   *
   */
  // ======================================================
  
  public String getSnippetText() {
    return snippetText;
  } // End Method getSnippetText} ============}


  // =======================================================
  /**
   * setSnippetText 
   * 
   * @param snippetText the snippetText to set
   */
  // ======================================================
  public void setSnippetText(String snippetText) {
    this.snippetText = snippetText;
  } // End Method setSnippetText} ============}


  // =======================================================
  /**
   * getLabel 
   * 
   * @return the label
   *
   */
  // ======================================================
  
  public String getLabel() {
    return label;
  } // End Method getLabel} ============}


  // =======================================================
  /**
   * setLabel 
   * 
   * @param label the label to set
   */
  // ======================================================
  public void setLabel(String label) {
    this.label = label;
  } // End Method setLabel} ============}




 
   // =======================================================
   /**
    * getFocus returns the normalized focus string
    * 
    * @return String
    *
    */
   // ======================================================	
  public String getFocus() {
    return this.focus;
    } // End Method getFocus() ============================

  // ======================================================  
  /**
    * getFocusBeginOffset returns the number of chars from 
    * the beginning of the snippet that the focus starts from.
    * 
    * @return int
    *
    */
   // ======================================================	
  public int getFocusBeginOffset() {
    return this.focusBeginOffset;
    } // End Method getFocusBeginOffset() ==================

//======================================================  
 /**
   * setFocusBeginOffset sets the number of chars from 
   * the beginning of the snippet that the focus starts from.
   * 
   * @return int
   *
   */
  // ======================================================	
 public void setFocusBeginOffset(int pFocusBeginOffset) {
    this.focusBeginOffset = pFocusBeginOffset;
   } // End Method setFocusBeginOffset() ==================
  
  // -----------------------------
  // Global and Class Variables
  
  /**
 * @return the focusEndOffset
 */
public int getFocusEndOffset() {
	return focusEndOffset;
}


/**
 * @param focusEndOffset the focusEndOffset to set
 */
public void setFocusEndOffset(int focusEndOffset) {
	this.focusEndOffset = focusEndOffset;
}

private String    patientICN = null;
  private String      document = null;
  private int       snippetNum = -1;
  private String   snippetText = null;
  private String         label = null;
  private String         focus = null;
  private int focusBeginOffset = -1;
  private int focusEndOffset = -1;
  private RedSnippetMarkups markups = null;
  public static final String  patientICN_SLOT = "PatientICN";
  public static final String    document_SLOT = "Document";
  public static final String  snippetNum_SLOT = "Snippet Num";
  public static final String snippetText_SLOT = "Snippet Text";
  public static final String snippetDelimiter = "----------------------------------------------------------------------------------"; // End Method getFocus ============
    
 
  
} // End Class RedSnippetContainer ============
