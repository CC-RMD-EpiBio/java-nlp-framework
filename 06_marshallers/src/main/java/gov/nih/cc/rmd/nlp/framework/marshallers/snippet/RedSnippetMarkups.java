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
 * RedSnippetMarkups is a container to hold the markup pieces for a red cat/ex snippet
 *
 *
 * @author  Guy Divita 
 * @created Sep 25, 2017
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
public final class RedSnippetMarkups {

  // =======================================================
   /**
    * Constructor creates a set of markup lines for this 
    * red snippet
    * 
    * @param pRedSnippetContainer
    */
   // ======================================================
  public RedSnippetMarkups(RedSnippetContainer pRedSnippetContainer, int pVttOffset) {
    
    
    int currentOffset[] = new int[1];
    currentOffset[0] = pVttOffset;
 
    this.patientICNMarkup = createMarkupField( currentOffset,   // <---- this field will be updated with currentOffset
                                               1, 
                                               pRedSnippetContainer.getSnippetNum(), 
                                               RedSnippetContainer.patientICN_SLOT,
                                               pRedSnippetContainer.getPatientICN() );
    
    this.documentMarkup   = createMarkupField( currentOffset,   // <---- this field will be updated with currentOffset
                                               2, 
                                               pRedSnippetContainer.getSnippetNum(), 
                                               RedSnippetContainer.document_SLOT,
                                               pRedSnippetContainer.getDocument() );
    
    
    this.snippetNumMarkup = createMarkupField( currentOffset,   // <---- this field will be updated with currentOffset
                                               3, 
                                               pRedSnippetContainer.getSnippetNum(), 
                                               RedSnippetContainer.snippetNum_SLOT,
                                               String.valueOf(pRedSnippetContainer.getSnippetNum()) );
   
    int beginTextOffset = currentOffset[0] + RedSnippetContainer.snippetText_SLOT.length() + 2;
    
    this.snippetTextMarkup = createMarkupField( currentOffset,   // <---- this field will be updated with currentOffset
                                               4, 
                                               pRedSnippetContainer.getSnippetNum(), 
                                               RedSnippetContainer.snippetText_SLOT,
                                               String.valueOf(pRedSnippetContainer.getSnippetText()) );
    
   
    this.focusMarkup       = createFocusField(  beginTextOffset, 
                                                pRedSnippetContainer.getFocusBeginOffset(), 
                                                pRedSnippetContainer.getLabel(), 
                                                pRedSnippetContainer.getFocus());
   
    
  }  // End Constructor RedSnippetMarkup ===================

  // =======================================================
   /**
    * Constructor 
    * 
    * @param pPatientICNMarkup
    * @param pDocumentMarkup
    * @param pSnippetNumMarkup
    * @param pSnippetTextMarkup
    * @param pSnipetDecisionMarkup
    */
   // ======================================================
  public RedSnippetMarkups(String pPatientICNMarkup, 
                           String pDocumentMarkup,
                           String pSnippetNumMarkup, 
                           String pSnippetTextMarkup, 
                           String pSnippetDecisionMarkup) {

    this.patientICNMarkup  = new RedSnippetMarkup( pPatientICNMarkup );
    this.documentMarkup    = new RedSnippetMarkup(pDocumentMarkup);
    this.snippetNumMarkup  = new RedSnippetMarkup(pSnippetNumMarkup);
    this.snippetTextMarkup = new RedSnippetMarkup(pSnippetTextMarkup);
    this.focusMarkup       = new RedSnippetMarkup(pSnippetDecisionMarkup);
    
  }   // End Constructor RedSnippetMarkups =============
    
  // =======================================================
  /**
   * Constructor 
   * 
   * @param pMarkupRows ( the rows are 
   *                     patientICN markup row
   *                     document   markup row
   *                     snippetNum markup row
   *                     snippetText markup row
   *                     snippetDecision markup row )
   */
  // ======================================================
 public RedSnippetMarkups(String[]  pMarkupRows) {

   this.patientICNMarkup  = new RedSnippetMarkup( pMarkupRows[0] );
   this.documentMarkup    = new RedSnippetMarkup(pMarkupRows[1]);
   this.snippetNumMarkup  = new RedSnippetMarkup(pMarkupRows[2]);
   this.snippetTextMarkup = new RedSnippetMarkup(pMarkupRows[3]);
   
   if ( pMarkupRows[4] != null && !pMarkupRows[4].isEmpty())
   
     this.focusMarkup       = new RedSnippetMarkup(pMarkupRows[4]);
   else 
     System.err.println("No judgement for this markup yet?" + pMarkupRows[0]);
   
 }   // End Constructor RedSnippetMarkups =============
  
  

  // =======================================================
   /**
    * Constructor 
    * 
    * @param pPatientICNMarkup
    * @param pDocumentMarkup
    * @param pSnippetNumMarkup
    * @param pSnippetTextMarkup
    * @param pSnipetDecisionMarkup
    */
   // ======================================================
  public RedSnippetMarkups(RedSnippetMarkup pPatientICNMarkup, 
                           RedSnippetMarkup pDocumentMarkup,
                           RedSnippetMarkup pSnippetNumMarkup, 
                           RedSnippetMarkup pSnippetTextMarkup, 
                           RedSnippetMarkup pSnippetDecisionMarkup) {
        
    this.patientICNMarkup  = pPatientICNMarkup;
    this.documentMarkup    = pDocumentMarkup;
    this.snippetNumMarkup  = pSnippetNumMarkup;
    this.snippetTextMarkup = pSnippetTextMarkup;
    this.focusMarkup       = pSnippetDecisionMarkup;
  } // End Constructor RedSnippetMarkups =============
    
  

  // =======================================================
   /**
    * toString
    * 
    * @return String
    *
    */
   // ======================================================	
  public String toString() {
  
    StringBuffer buff = new StringBuffer();
     try {
       buff.append(this.patientICNMarkup  ); 
       buff.append(this.documentMarkup    );
       buff.append(this.snippetNumMarkup  );
       buff.append(this.snippetTextMarkup );
       buff.append(this.focusMarkup       );
  
     
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method toString2 " + e.getMessage() );
        throw e;
      }
     return buff.toString();
    } // End Method toString ===============================

  // =======================================================
   /**
    * createFocusField creates a markup for the focus of 
    * the red snippet
    * 
    * @param pVttBeginTextOffset
    * @param pFocus
    * @return RedSnippetMarkup
    *
    */
   // ======================================================	
  private RedSnippetMarkup createFocusField(int pVttBeginTextOffset, int pFocusBeginOffset, String pLabel, String pFocus) {
   
     try {
    	
       int currentOffset = pVttBeginTextOffset;
       int    beginOffset = currentOffset + pFocusBeginOffset  ;
       int         length = pFocus.length();
            currentOffset = beginOffset + length + 1;
          
       RedSnippetMarkup aRedMarkup = new RedSnippetMarkup( beginOffset, length, pLabel, pFocus ); 
     
         return aRedMarkup;
    
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method createFocusField " + e.getMessage() );
        throw e;
      }
    } // End Method createFocusField ============
    
  

  // =======================================================
   /**
    * createPatientICNMarkup 
    * 
    * @param pRedSnippetContainer
    * @param pVttOffset
    * @return int
    *
    */
   // ======================================================	
  private RedSnippetMarkup createMarkupField( int[] pVttOffset, 
                                              int pSnippetNumber, 
                                              int pColumnNumber, 
                                              String pMarkupFieldName, 
                                              String pMarkupFieldValue) {
   
    int currentOffset = pVttOffset[0];
    int    beginOffset = currentOffset + pMarkupFieldName.length() + ": ".length() ;
    int         length = pMarkupFieldValue.length();
         currentOffset = beginOffset + length + 1;
         pVttOffset[0] = currentOffset;
         
    
    
     RedSnippetMarkup aRedMarkup = new RedSnippetMarkup( beginOffset, length, pColumnNumber, pSnippetNumber,  pMarkupFieldName, pMarkupFieldValue ); 
  
      return aRedMarkup;
    
    } // End Method createPatientICNMarkup ============
  
  // ----------------------
  // Global and Class Variables

  // =======================================================
  /**
   * getPatientICNMarkup 
   * 
   * @return the patientICNMarkup
   *
   */
  // ======================================================
  
  public final RedSnippetMarkup getPatientICNMarkup() {
    return patientICNMarkup;
  } // End Method getPatientICNMarkup} ============}

  // =======================================================
  /**
   * setPatientICNMarkup 
   * 
   * @param patientICNMarkup the patientICNMarkup to set
   */
  // ======================================================
  public final void setPatientICNMarkup(RedSnippetMarkup patientICNMarkup) {
    this.patientICNMarkup = patientICNMarkup;
  } // End Method setPatientICNMarkup} ============}

  // =======================================================
  /**
   * getDocumentMarkup 
   * 
   * @return the documentMarkup
   *
   */
  // ======================================================
  
  public final RedSnippetMarkup getDocumentMarkup() {
    return documentMarkup;
  } // End Method getDocumentMarkup} ============}

  // =======================================================
  /**
   * setDocumentMarkup 
   * 
   * @param documentMarkup the documentMarkup to set
   */
  // ======================================================
  public final void setDocumentMarkup(RedSnippetMarkup documentMarkup) {
    this.documentMarkup = documentMarkup;
  } // End Method setDocumentMarkup} ============}

  // =======================================================
  /**
   * getSnippetNumMarkup 
   * 
   * @return the snippetNumMarkup
   *
   */
  // ======================================================
  
  public final RedSnippetMarkup getSnippetNumMarkup() {
    return snippetNumMarkup;
  } // End Method getSnippetNumMarkup} ============}

  // =======================================================
  /**
   * setSnippetNumMarkup 
   * 
   * @param snippetNumMarkup the snippetNumMarkup to set
   */
  // ======================================================
  public final void setSnippetNumMarkup(RedSnippetMarkup snippetNumMarkup) {
    this.snippetNumMarkup = snippetNumMarkup;
  } // End Method setSnippetNumMarkup} ============}

  // =======================================================
  /**
   * getSnippetTextMarkup 
   * 
   * @return the snippetTextMarkup
   *
   */
  // ======================================================
  
  public final RedSnippetMarkup getSnippetTextMarkup() {
    return snippetTextMarkup;
  } // End Method getSnippetTextMarkup} ============}

  // =======================================================
  /**
   * setSnippetTextMarkup 
   * 
   * @param snippetTextMarkup the snippetTextMarkup to set
   */
  // ======================================================
  public final void setSnippetTextMarkup(RedSnippetMarkup snippetTextMarkup) {
    this.snippetTextMarkup = snippetTextMarkup;
  } // End Method setSnippetTextMarkup} ============}

  // =======================================================
  /**
   * getFocusMarkup 
   * 
   * @return the focusMarkup
   *
   */
  // ======================================================
  
  public final RedSnippetMarkup getFocusMarkup() {
    return focusMarkup;
  } // End Method getFocusMarkup} ============}

  // =======================================================
  /**
   * setFocusMarkup 
   * 
   * @param focusMarkup the focusMarkup to set
   */
  // ======================================================
  public final void setFocusMarkup(RedSnippetMarkup focusMarkup) {
    this.focusMarkup = focusMarkup;
  } // End Method setFocusMarkup} ============}

  private RedSnippetMarkup  patientICNMarkup = null;
  private RedSnippetMarkup    documentMarkup = null;
  private RedSnippetMarkup  snippetNumMarkup = null;
  private RedSnippetMarkup snippetTextMarkup = null;
  private RedSnippetMarkup       focusMarkup = null;

    
  } // End Class RedSnippetMarkups() =======================
    
  
  
