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
 * Class: SectionInfo.java [Summary]
 * By: Guy
 * Created: 11:55:56 AM
 * Modified: 
 */
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.tcas.Annotation;

// ==============================
/**
 * Types/Global Variables/Fields
 * 
 */
// =============================
public class SectionInfo {

  // ========================
  /**
   * Constructor 
   * @param section
   */
  // =======================
  public SectionInfo(Annotation section) {
    this.sections = new ArrayList<Annotation>();
    this.sections.add( section);
    } // End Method Constructor =======

  // =======================
  /**
   * add [Summary]
   *
   * @param section
   */
  // =======================
  public void add(Annotation section) {
    this.sections.add(section);
  } // End Method add =======

  // =======================
  /**
   * getSections [Summary]
   *
   * @return
   */
  // =======================
  public List<Annotation> getSections() {
    return this.sections;
  } // End Method getSections =======
  
  // ----------------------
  // Global Variables
  List<Annotation> sections = null;
  
} // end Class SectionInfo
  


  

