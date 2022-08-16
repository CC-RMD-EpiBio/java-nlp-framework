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
