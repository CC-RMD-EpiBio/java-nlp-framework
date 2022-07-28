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
 * Hier is a container to hold a row of the mrhier table
 *
 * @author     Guy Divita
 * @created    Jul 23, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.lexUtils;

/**
 * @author divitag2
 *
 */
public class Hier {


  // =================================================
  /**
   * Constructor
   *
   * @param pCui
   * @param pAui
   * @param pCxn
   * @param pPaui
   * @param pSab
   * @param pRela
   * @param pPathToRoot
   * 
  **/
  // =================================================
  public Hier(char[] pCui, 
              char[] pAui, 
              char[] pCxn, 
              char[] pPaui, 
              char[] pSab, 
              char[] pRela, 
              char[] pPathToRoot) {
  
    this.cui = pCui;
    this.aui = pAui;
    this.cxn = pCxn;
    this.paui = pPaui;
    this.sab = pSab;
    this.rela = pRela;
    this.pathToRoot = pPathToRoot;
    
  } // end Constructor() ------------------------
  
  /**
   * @return the cui
   */
  public final char[] getCui() {
    return cui;
  }
  /**
   * @param cui the cui to set
   */
  public final void setCui(char[] cui) {
    this.cui = cui;
  }
  /**
   * @return the aui
   */
  public final char[] getAui() {
    return aui;
  }
  /**
   * @param aui the aui to set
   */
  public final void setAui(char[] aui) {
    this.aui = aui;
  }
  /**
   * @return the cxn
   */
  public final char[] getCxn() {
    return cxn;
  }
  /**
   * @param cxn the cxn to set
   */
  public final void setCxn(char[] cxn) {
    this.cxn = cxn;
  }
  /**
   * @return the paui
   */
  public final char[] getPaui() {
    return paui;
  }
  /**
   * @param paui the paui to set
   */
  public final void setPaui(char[] paui) {
    this.paui = paui;
  }
  /**
   * @return the sab
   */
  public final char[] getSab() {
    return sab;
  }
  /**
   * @param sab the sab to set
   */
  public final void setSab(char[] sab) {
    this.sab = sab;
  }
  /**
   * @return the rela
   */
  public final char[] getRela() {
    return rela;
  }
  /**
   * @param rela the rela to set
   */
  public final void setRela(char[] rela) {
    this.rela = rela;
  }
  /**
   * @return the pathToRoot
   */
  public final char[] getPathToRoot() {
    return pathToRoot;
  }
  /**
   * @param pathToRoot the pathToRoot to set
   */
  public final void setPathToRoot(char[] pathToRoot) {
    this.pathToRoot = pathToRoot;
  }
  // =================================================
  /**
   * inverse creates a new Hier which switches the paui and aui, and reverses the rela
   * 
   * @return Hier
  */
  // =================================================
  public Hier inverse() {
    
    Hier returnVal = null;
    char[] inverse = inverseRelationship( rela);
    if ( inverse != null ) {
      char[] pCui = null;
      
     
      pCui = this.mrconso.getCuiForAui( paui);  
     
      returnVal = new Hier(   pCui, paui, cxn, aui, sab, inverse, (char[]) null);
    }
    return returnVal;
  } // end Method inverse()

  
  // =================================================
  /**
   * setMRCONSO set's the mrconso for cui retrieval
   * 
   * @param pMrconso
  */
  // =================================================
  public void setMRCONSO(MRCONSO pMrconso) {
   
    this.mrconso = pMrconso;
    
  } // end Method setMRCONSO() ------------------------

  // =================================================
  /**
   * inverseRelationship 
   *   returns the inverse relationship if there is a logical inverse.
   *   Otherwise, null is returned.
   *    
   *   isa <->inverse-isa
   *   PAR <->CHD                           parent/child
   *   part_of <-> has_part
   *   member_of <-> has_member
   *   tributary_of <-> has_tributary
   *   RB <-> RN                             broader than/narrower than
   * 
   * @param pRelationship  
   * @return char[]
  */
  // =================================================
  public static char[] inverseRelationship(char[] pRelationship) {
    
    char[] returnValue = null;
    String returnVal = null;
    
    if ( pRelationship == null || pRelationship.length == 0 )
      return returnValue;
    String relationship = new String ( pRelationship);
    switch ( relationship ) {
      case "isa":          returnVal = "inverse_isa";   break;
      case "PAR":          returnVal = "CHD";           break;
      case "inverse-isa":  returnVal = "isa";           break;
      case "CHD":          returnVal = "PAR";           break;
      case "part_of":      returnVal = "has_part";      break;
      case "member_of":    returnVal = "has_member";    break;
      case "tributary_of": returnVal = "has_tributary"; break;
      
      case "AQ": 
      case "DEL":
      case "QB" : 
      case "RL" : 
      case "RO" :
      case "RQ" :
      case "RU" :
      case "SIB" :
      case "SY" :
      case "XR" :
                             returnVal = null;          break;
      case "RB" :            returnVal = "RN";          break;
    }
    
    if ( returnVal != null )
      returnValue = returnVal.toCharArray();
    
    return returnValue;
  }
  // ----------------
  // class variables
  char[] cui;
  char[] aui;
  char[] cxn;
  char[] paui;
  char[] sab;
  char[] rela;
  char[] pathToRoot;
  MRCONSO mrconso = null;
  
} // end Class Hier() --------------
