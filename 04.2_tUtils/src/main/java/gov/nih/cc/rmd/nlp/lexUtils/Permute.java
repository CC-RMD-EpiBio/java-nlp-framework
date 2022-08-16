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
