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
 * Document.java Summary
 *  Class Detail
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

import java.util.ArrayList;
import java.util.List;


public class Document {

 
  
  // -----------------------------------------
  /**
   * Constructor
   *
   * @param pDocumentName
   */
  // -----------------------------------------
  public Document(String pDocumentName) {
    this.textSource = pDocumentName;
  }
  // -----------------------------------------
  /** 
   * getannotations retrieves annotations
   *  
   * @return the annotations
   */
  // -----------------------------------------
  public List<gov.va.vinci.knowtator.Annotation> getAnnotations() {
    return annotations;
  }
  // -----------------------------------------
  /** 
   * setannotations sets the value of annotations
   *  
   * @param annotations the annotations to set
   */
  // -----------------------------------------
  public void setAnnotations(List<gov.va.vinci.knowtator.Annotation> annotations) {
    this.annotations = annotations;
  }
  // -----------------------------------------
  /** 
   * getclassMentions retrieves classMentions
   *  
   * @return the classMentions
   */
  // -----------------------------------------
  public List<ClassMention> getClassMentions() {
    return classMentions;
  }
  // -----------------------------------------
  /** 
   * setclassMentions sets the value of classMentions
   *  
   * @param classMentions2 the classMentions to set
   */
  // -----------------------------------------
  public void setClassMentions(List<ClassMention> classMentions2) {
    this.classMentions = classMentions2;
  }
  // -----------------------------------------
  /** 
   * getstringSlotMentions retrieves stringSlotMentions
   *  
   * @return the stringSlotMentions
   */
  // -----------------------------------------
  public List<StringSlotMention> getStringSlotMentions() {
    return stringSlotMentions;
  }
  // -----------------------------------------
  /** 
   * setstringSlotMentions sets the value of stringSlotMentions
   *  
   * @param stringSlotMentions the stringSlotMentions to set
   */
  // -----------------------------------------
  public void setStringSlotMentions(
      ArrayList<StringSlotMention> stringSlotMentions) {
    this.stringSlotMentions = stringSlotMentions;
  }
  
  // -----------------------------------------
  /** 
   * gettextSource retrieves textSource
   *  
   * @return the textSource
   */
  // -----------------------------------------
  public String getTextSource() {
    return textSource;
  }
  // -----------------------------------------
  /** 
   * settextSource sets the value of textSource
   *  
   * @param textSource the textSource to set
   */
  // -----------------------------------------
  public void setTextSource(String textSource) {
    this.textSource = textSource;
  }


  // ----------------------------------------------
  /** 
   * getComplexSlotMentions retrieves complexSlotMentions
   *  
   * @return the complexSlotMentions
   */
  // ----------------------------------------------
  public List<ComplexSlotMention> getComplexSlotMentions() {
    return complexSlotMentions;
  }
  // ----------------------------------------------
  /** 
   * setStringSlotMentions sets the value of stringSlotMentions
   *  
   * @param stringSlotMentions the stringSlotMentions to set
   *
   */
  // ----------------------------------------------
  public void setStringSlotMentions(List<StringSlotMention> stringSlotMentions) {
    this.stringSlotMentions = stringSlotMentions;
  }

  private String textSource = null;
  // ----------------------------------------------
  /**
   * setComplexSlotMentions
   * 
   * @param pComplexSlotMentions
   */
  // ----------------------------------------------
  public void setComplexSlotMentions(List<ComplexSlotMention> pComplexSlotMentions) {
this.complexSlotMentions = pComplexSlotMentions;    
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
    String buff =  "Document  ================= " + U.NL + 
                      textSource + U.NL +
                      "================ " + U.NL + 
                      "<annotations> " + U.NL  + 
                      U.toString( annotations ) + U.NL +
                      "</annotations>" + U.NL + 
                      "<classMentions> "  + U.NL + 
                       U.toString(classMentions) + U.NL + 
                       "</classMentions>" + U.NL + 
                       "<stringSlotMentions>" + U.NL + 
                       U.toString(stringSlotMentions) +  U.NL + 
                       "</stringSlotMentions>"  + U.NL + 
                       "<complexSlotMentions>" + U.NL + 
                       U.toString(complexSlotMentions)  + U.NL + 
                       "</complexSlotMentions>";
    
    return buff;
  } // end Method toString() ---------------

  

  //----------------------------------------
  // GlobalVariables
  // ----------------------------------------
  private List<gov.va.vinci.knowtator.Annotation> annotations = null;
  private List<ClassMention> classMentions = null;
  private List<StringSlotMention> stringSlotMentions = null;
  private List<ComplexSlotMention> complexSlotMentions = null;
}
