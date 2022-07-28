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
 * 
 */
package gov.nih.cc.rmd.nlp.lexUtils;

/**
 * @author Guy
 *
 */
public class LexUtils {

  // ==========================================
  /**
   * LexUtils [Summary]
   *
   */
  // ==========================================
  public LexUtils() {
    // TODO Auto-generated constructor stub
    // end Constructor ==========================================
  }

  // ==========================================
  /**
   * main [Summary]
   *
   * @param args
   */
  // ==========================================
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    // end Method main() ========================================
  }

  // ==========================================
  /**
   * createLexicon 
   *
   * @param pSourceResourcePath
   * @return Lexicon
   * @throws Exception 
   */
  // ==========================================
  public static Lexicon createLexicon(String pSourceResourcePath) throws Exception {
    
    Lexicon lexicon = new Lexicon( pSourceResourcePath );
  
    return lexicon ;
    
  } // end Method createLexicon() ========================================
  

} // end Class LexUtils
