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
 * SnippetContainer.java  TODO file description
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

import java.util.List;

// ===========================================
/**
 * Public Types 
 */
// ===========================================
public class SnippetContainer extends Context {



  // =======================================================
  /**
   * Constructor SnippetContainer
   * 
   * @param pDocumentId
   * @param pAnnotationId
   * @param pBeginOffset
   * @param pEndOffset
   * @param pFocus
   * @param pfocusBeginOffset
   * @param pfocusEndOffset
   * @param pSnippet
   * @param pRelevance
   * @param pPatientId
   * @param pAassertionStatus
   * @param pConditional
   * @param pHistorical
   * @param pSubject
   * @param pCategory
   * @param pOtherFeatures
   * @param pContextNumber
   * @param pLabelCategory
   */
  // ======================================================
  public SnippetContainer(String pDocumentId, String pAnnotationId, int pBeginOffset, int pEndOffset, String pFocus,
      int pfocusBeginOffset, int pfocusEndOffset, String pSnippet, String pRelevance, String pPatientId, String pAassertionStatus,
      boolean pConditional, boolean pHistorical, String pSubject, String pCategory, String pOtherFeatures, int pContextNumber, String pLabelCategory) {
   
    super(pDocumentId, pAnnotationId, pBeginOffset, pEndOffset, pFocus, pfocusBeginOffset, pfocusEndOffset, pSnippet, pRelevance,
        pPatientId, pAassertionStatus, pConditional, pHistorical, pSubject, pCategory, pOtherFeatures, pContextNumber);
    
    this.labelCategory = pLabelCategory;
   

  }  // End Constructor SnippetContainer =============

  // =======================================================
   /**
    * Constructor 
    * 
    * @param pSnippetInfo
   * @throws Exception 
    */
   // ======================================================
  public SnippetContainer(List<String> pSnippetInfo) throws Exception {
  
    super( pSnippetInfo );
  } // End Constructor SnippetContainer =============
    
  // =======================================================
  /**
   * Constructor 
   * 
   * @param pContext
  * @throws Exception 
   */
  // ======================================================

  public SnippetContainer(Context pContext) {
	 

	 
	  this.contextNumber            = pContext.getContextNumber();
	  this.beginOffset              = pContext.getBeginOffset(); 
	  this.endOffset                = pContext.getEndOffset();
	  this.focus                    = pContext.getFocus();
	  this.focusBeginOffset         = pContext.getFocusBeginOffset();
	  this.focusEndOffset           = pContext.getFocusEndOffset();

	  this.focusBeginOffsetOffset   = pContext.getFocusBeginOffsetOffset();
	  this.focusEndOffsetOffset     = pContext.getFocusEndOffsetOffset();
	  this.focusKeyBeginOffset      = pContext.getFocusKeyBeginOffset();
	  this.snippet                  = pContext.getSnippet();
	  this.relevance                = pContext.getRelevance();
	  this.patientId                = pContext.getPatientId();
	  this.documentId               = pContext.getDocumentId();
	  this.fileSnippetId            = pContext.getFileSnippetId();
	  this.patientIdBeginOffset     = pContext.getPatientIdBeginOffset();
	  this.documentIdBeginOffset    = pContext.getDocumentIdBeginOffset();
	  this.annotationIdBeginOffset  = pContext.getAnnotationIdBeginOffset();
	  this.contextNumberBeginOffset = pContext.getContextNumberBeginOffset();
	  this.snippetBeginOffset       = pContext.getSnippetBeginOffset();
	  this.historical               = pContext.getHistorical();
	  this.assertionStatus          = pContext.getAssertionStatus();
	  this.conditional              = pContext.getConditional();
	  this.otherFeatures            = pContext.getOtherFeatures();
	  this.subject                  = pContext.getSubject();
	  this.category                 = pContext.getCategory();
	  
	   this.decision                = null;
	   this.labelCategory           = pContext.getCategory();
	   
	   
} // end Constructor () ----------------------------------

// =======================================================
   /**
    * Constructor SnippetContainer
    * 
    * @param pSnippetInfo
   * @throws Exception 
    */
   // ======================================================
  public static SnippetContainer parse(List<String> pSnippetInfo ) throws Exception {
    
    SnippetContainer aSnippetContainer = null;
    aSnippetContainer =  new SnippetContainer( pSnippetInfo );
   
    return aSnippetContainer;
    
  } // End Constructor SnippetContainer ====================

  // =======================================================
   /**
    * setDecision sets the decision (the human markup )
    * 
    * @param pLabel
    *
    */
   // ======================================================	
  public void setDecision( String pLabel) {
    
    this.decision = pLabel;
    
    } // End Method setDecision() ==========================
    
//==========================================================
  /**
   * getDecision gets the decision (the human markup )
   * 
   * @return String
   */
  // ========================================================  
 public String getDecision() {
   
   return this.decision;
   
   } // End Method getDecision() ============================
   

 // =======================================================
  /**
   * setLabelCategory is the vtt field after the label field
   * which is the sub category of the label. It's usually empty
   * 
   * @param pCategory
   *
   */
  // ======================================================  
 public void setLabelCategory( String pCategory) {
   
   this.labelCategory = pCategory;
   
   } // End Method setLabelCategory() ====================
   
//==========================================================
 /**
  * getLabelCategory returns the vtt field after the label field. 
  * This is the label sub category.  it's usually empty.
  * 
  * @return String
  */
 // ========================================================  
public  String getLabelCategory() {
  
  return this.labelCategory;
  
  } // End Method getLabelCategory() =====================
  
  
  
// =======================================================
 /**
  * getMarkup returns a SnippetMarkup 
  * 
  * @return SnippetMarkup
  *
  */
 // ======================================================	
public SnippetMarkup getMarkup() {
 
  SnippetMarkup markup = null;
  
   try {
     
     markup = new SnippetMarkup(this);
     
    } catch ( Exception e ) {
      e.printStackTrace();
      System.err.println("Issue within method getMarkup " + e.getMessage() );
      throw e;
    }
    return markup;
} // End Method getMarkup() ================================




    // ======================================================
    // Additional Class variables (in addition to Context global variables 
    // ======================================================
    private String decision = null;
    private String labelCategory = null;
    
    

} // End Class SnippetContainer ============
