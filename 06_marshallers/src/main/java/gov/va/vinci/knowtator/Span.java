// =================================================
/**
 * Span.java Summary
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

public class Span {

  // -----------------------------------------
  /**
   * Constructor: Span.java Summary
   * [Detail Here]
   *
   * @param pStart
   * @param pEnd
   */
  // -----------------------------------------
  public Span(int pStart, int pEnd) {
    this.start = pStart;
    this.end   = pEnd;
  }
  
  // ----------------------------------------------
  /**
   * Constructor: Span
   *
   * @param pBegin
   * @param pEnd
   */
  // ----------------------------------------------
  public Span(String pBegin, String pEnd) {
    this.start = Integer.parseInt( pBegin);
    this.end = Integer.parseInt( pEnd);
  } // end Constructor() ---------------------

  // ----------------------------------------------
  /** 
   * toString
   *
   * @return
   */
  // ----------------------------------------------
  @Override
  public String toString() {
    return "<Span start=" + U.quote(start) + ", end=" + U.quote(end) + "/>";
  }

  // -----------------------------------------
  /** 
   * getstart retrieves start
   *  
   * @return the start
   */
  // -----------------------------------------
  public int getStart() {
    return start;
  }
  // -----------------------------------------
  /** 
   * setstart sets the value of start
   *  
   * @param start the start to set
   */
  // -----------------------------------------
  public void setStart(int start) {
    this.start = start;
  }
  // -----------------------------------------
  /** 
   * getend retrieves end
   *  
   * @return the end
   */
  // -----------------------------------------
  public int getEnd() {
    return end;
  }
  // -----------------------------------------
  /** 
   * setend sets the value of end
   *  
   * @param end the end to set
   */
  // -----------------------------------------
  public void setEnd(int end) {
    this.end = end;
  }

  // -----------------------------------------
  // Global Variables
  // -----------------------------------------
  private int start = -1;
  private int end = -1;

}
