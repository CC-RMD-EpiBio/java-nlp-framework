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
  


  
