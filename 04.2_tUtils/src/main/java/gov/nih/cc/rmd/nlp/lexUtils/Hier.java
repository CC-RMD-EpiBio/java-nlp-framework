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
