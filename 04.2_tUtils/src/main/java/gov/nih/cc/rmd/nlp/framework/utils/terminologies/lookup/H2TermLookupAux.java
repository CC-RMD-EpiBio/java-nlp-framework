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
 * H2TermLookupAux.java  Summary [TBD]
 *
 * @author     Guy Divita
 * @created    Jun 25, 2021
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies.lookup;

import java.io.BufferedReader;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * @author Guy
 *
 */
public class H2TermLookupAux  implements Runnable {


  private H2TermLookupImpl parentClass = null;
  private String fileName = null;

  // =================================================
  /**
   * Constructor
   * @param pParentClass 
   * @param pFileName 
   *
   * 
  **/
  // =================================================
  public H2TermLookupAux(String pFileName, H2TermLookupImpl pParentClass ) {
     this.parentClass = pParentClass;
     this.fileName = pFileName ;
  }

  // =================================================
  /**
   * main 
   * 
   * @param args
  */
  // =================================================

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

  // =================================================
  /**
   * index 
   * 
   * @param fileName
  */
  // =================================================
  public void index(String fileName) throws Exception   {
   
    
    
    try (final BufferedReader in = U.getInputStreamFromFileOrResource(fileName);) {
      GLog.println(GLog.INFO_LEVEL, this.getClass(), "indexx", "Reading in local terminology file |" + fileName + "|");

      String line = null;
      while ((line = in.readLine()) != null) {
        if (line != null && !line.startsWith("#") && line.length() > 4) {
          this.parentClass.index(line);
        }
      } // end loop through the lines
      
      
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception (GLog.println( GLog.ERROR_LEVEL, this.getClass(), "run" , "Issue in the thread running termLookupG " + e.toString()));
    }

    
  } // end Method index() --------------------------

 //=================================================
 /**
  * run 
  * 
 */
 // =================================================
  @Override
  public void run() {
    
    try {
      this.index( this.fileName);
      
      Runtime.getRuntime().gc();
      Thread.currentThread().interrupt();
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println( GLog.ERROR_LEVEL, this.getClass(), "run" , "Issue in the thread running termLookupG " + e.toString());
    }
    
    
  } // end Method run() ----------------------------

}
