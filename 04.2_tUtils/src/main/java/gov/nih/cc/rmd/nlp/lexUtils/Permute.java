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
 * Permute takes two, three and 4 row table and
 * permutes the values from the columns in each
 * 
 * This is useful when generating the variants
 * of a multi-word term, where you have variant
 * tokens for each of the words
 *
 *
 * @author  Guy Divita 
 * @created Apr 21, 2011
 *
 * [Utah/Salt Lake City Preamble Here]
 */
// ================================================
package gov.nih.cc.rmd.nlp.lexUtils;

import java.util.ArrayList;
import java.util.List;

 public class Permute {
  
   // ---------------------------------------
   // Public Class Variables
   // ---------------------------------------  
   public static final int MAX_SIZE = 10000;   // The number of variants to truncate to to

  // -----------------------------------------
  /**
   * permute permutes the values from the list a with the values of list b
   * truncated to the Permute.MAX_SIZE variants.
   * 
   * @param pY
   * @param pCols
   * @return String[] 
   */
  // -----------------------------------------
  public static List<String> permute(List<String> pA, List<String> pB)  {
    
    ArrayList<String> c = null;
    if ( pA != null ) { 
      if ( pB != null ) {
        int aSize = pA.size();
        int bSize = pB.size();
        int proposedSize = (aSize * bSize) + 1;
        
        System.err.println( proposedSize);
        
        if (proposedSize <=  MAX_SIZE) {
        
          c = new ArrayList<String>(proposedSize);
      
          for (int i = 0; i < aSize; i++)
            for (int j = 0; j < bSize; j++) {
              String buff = pA.get(i) + " " + pB.get(j);
              c.add(buff); 
            } 
          }
      } else {
        c = (ArrayList<String>)pA;
      }
    } else {
      if ( pB != null)
        c = (ArrayList<String>)pB;
    }
    return c;
 } // end Method permute(a,b) ---------------


  // -----------------------------------------
  /**
   * permute permutes the values from the list a with the values of list b, and c
   * 
   * @param pA
   * @param pB
   * @param pC
   * @return String[]
   */
  // -----------------------------------------
  public static List<String>  permute(List<String> pA, 
                              List<String> pB, 
                              List<String> pC)  {
    
    List<String> y = permute( pA, pB);
    List<String> d = permute( y, pC);
       
    return d ;
  } // end Method permute(a,b,c) ---------------

  // -----------------------------------------
  /**
   * permute permutes the values from the an array of ArrayList<String>
   * 
   * @param pCols
   * @return List<String>
   */
  // -----------------------------------------
  public static List<String> permute(ArrayList<String>[] pCols)  {

    List<String> returnValue = null;
    if ( pCols != null )
      if ( pCols.length == 1) {
        returnValue = pCols[0];
      }else {
        List<String> interiumArray = permute( pCols[0], pCols[1]);
        for ( int i = 2; i < pCols.length; i++) 
          interiumArray = permute( interiumArray, pCols[i]);
        returnValue = interiumArray;
      }
    return returnValue;
  } // end Method permute(a,b,c,d) ---------------

  // -----------------------------------------
  /**
   * permute permutes the values from the list a with the values of list b, c, d, e
   * 
   * @param pA
   * @param pB
   * @param pC
   * @param pD
   * @param pE
   * @return List<String> 
   */
  // -----------------------------------------
  public static List<String> permute(String[] pA, 
                              String[] pB, 
                              String[] pC, 
                              String[] pD,
                              String[] pE)  {
    
    int aSize = pA.length;
    int bSize = pB.length;
    int cSize = pC.length;
    int dSize = pD.length;
    int eSize = pE.length;
    ArrayList<String> c= new ArrayList<String>( (aSize * bSize * cSize * dSize * eSize) + 1);
    
    for (int i = 0; i < aSize; i++)
      for (int j = 0; j < bSize; j++ )
        for (int k = 0; k < cSize; k++ )
          for (int m = 0; m < dSize; m++ )
            for (int n = 0; n < eSize; n++ )
        c.add( pA[i] + " " + pB[j] + " "+ pC[k] + " " + pD[m] + " " + pE[n]);    
    return c ;
  } // end Method permute(a,b,c,d,e) ---------------

//-----------------------------------------
  /**
   * permute permutes the values from the list a with the values of list b, c, d, e
   * 
   * @param pA
   * @param pB
   * @param pC
   * @param pD
   * @param pE
   * @param pF
   * @return List<String> 
   */
  // -----------------------------------------
  public static List<String> permute(String[] pA, 
                              String[] pB, 
                              String[] pC, 
                              String[] pD,
                              String[] pE,
                              String[] pF)  {
    int aSize = pA.length;
    int bSize = pB.length;
    int cSize = pC.length;
    int dSize = pD.length;
    int eSize = pE.length;
    ArrayList<String> c= new ArrayList<String>( (aSize * bSize * cSize * dSize * eSize) + 1);
    
    for (int i = 0; i < aSize; i++)
      for (int j = 0; j < bSize; j++ )
        for (int k = 0; k < cSize; k++ )
          for (int m = 0; m < dSize; m++ )
            for (int n = 0; n < eSize; n++ )
        c.add( pA[i] + " " + pB[j] + " "+ pC[k] + " " + pD[m] + " " + pE[n]);    
    return c ;
  } // end Method permute(a,b,c,d,e) ---------------


  // ----------------------------------------------
  // Class Variables
  // ----------------------------------------------

} // end Class Permute() --------------------------

