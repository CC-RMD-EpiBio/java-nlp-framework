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
 * Annotation is a container to hold knowtator annotations that have been converted from uima cas annotations.
 *
 *
 * @author  Guy Divita 
 * @created Jun 13, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.va.vinci.knowtator;

import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.va.vinci.knowtator.Mention;


public class Annotation {

  
  // ----------------------------------------------
  /**
   * Constructor: Annotation
   *
   * @param pMmentionId
   * @param pSpan
   * @param pSpannedText
   * @param pCreationDate
   */
  // ----------------------------------------------
  public Annotation(Mention  pMention, Span pSpan, String pSpannedText, String pCreationDate, String pAnnotator) {
   this.mention = pMention;
   this.span = pSpan;
   this.spannedText = pSpannedText;
   this.creationDate = pCreationDate;
   this.annotator = pAnnotator;
   
  } // end Constructor() ---------------------

  // ----------------------------------------------
  /**
   * Constructor: Annotation
   *
   */
  // ----------------------------------------------
  public Annotation() {
   
  }

  // -----------------------------------------
  /**
   * setMention
   * 
   * @param pCveredText
   */
  // -----------------------------------------
  public void setMention(String pMentionId) {
   this.mention = new Mention( pMentionId);
    
  }
  
  // -----------------------------------------
  /**
   * getMention
   * 
   * @return Mention
   */
  // -----------------------------------------
  public Mention getMention() {
   return ( this.mention );
  }
  
  // -----------------------------------------
  /**
   * setContent
   * 
   * @param pCveredText
   */
  // -----------------------------------------
  public void setContent(String pCoveredText) {
   this.spannedText = pCoveredText;
    
  }

  // -----------------------------------------
  /**
   * setAnnotationId sets the annotaton id
   * 
   * @param pAnnotationIdString
   */
  // -----------------------------------------
  public void setAnnotationId(String pAnnotationIdString) {
    setMention( pAnnotationIdString);    
  } 

 
  // -----------------------------------------
  /** 
   * getSpan retrieves the span
   *  
   * @return the start
   */
  // -----------------------------------------
  public Span getSpan() {
    return this.span;
  }

  // -----------------------------------------
  /** 
   * setSpan sets the span
   *  
   * @param start the start to set
   */
  // -----------------------------------------
  public void setSpan(Span pSpan) {
    this.span = pSpan;
  }


  // -----------------------------------------
  /** 
   * getid retrieves id
   *  
   * @return the id
   */
  // -----------------------------------------
  public String getId() {
    
    return (this.mention.getMentionId());
  }

  // -----------------------------------------
  /** 
   * setid sets the value of id
   *  
   * @param id the id to set
   */
  // -----------------------------------------
  public void setId(String id) {
   this.setMention( id);
  }

  // -----------------------------------------
  /** 
   * getcreationDate retrieves creationDate
   *  
   * @return the creationDate
   */
  // -----------------------------------------
  public String getCreationDate() {
    return this.creationDate;
  }

  // ----------------------------------------------
  /** 
   * toString
   *
   * @return
   */
  // ----------------------------------------------
  @Override
  public String toString() {
    return "<Annotation> " + U.NL + 
           mention + U.NL + 
    		   span + U.NL +  
    		   "<spannedText>"  + spannedText + "</spannedText>"   + U.NL + 
    		   "<creationDate>" + creationDate + "</creationDate>" + U.NL +
    		   "<annotator>"    + annotator    + "</annotator>"    + U.NL + 
           "</Annotation>" ;
  }

  // -----------------------------------------
  /** 
   * setcreationDate sets the value of creationDate
   *  
   * @param creationDate the creationDate to set
   */
  // -----------------------------------------
  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  // -----------------------------------------
  /** 
   * getSpannedText retrieves content
   *  
   * @return the content
   */
  // -----------------------------------------
  public String getSpannedText() {
    return this.spannedText;
  }

  
public String getAnnotator() {
  return annotator;

} // End Method getAnnotator() ---------------------------
  

  public void setAnnotator(String annotator) {
  this.annotator = annotator;
  
  
  }  // End Method setAnnotator() ---------------------------
  

  //------------------------------------------
  // Global Variables
  // ------------------------------------------
   private Mention mention = null;
   private Span span = null;
   private String spannedText = null;
   private String creationDate = null;
   private String annotator = null;

}
