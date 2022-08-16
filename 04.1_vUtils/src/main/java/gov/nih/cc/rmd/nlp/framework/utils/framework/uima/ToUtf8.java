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
 * ToUtf8 converts text to UTF 8 text if it is not
 *
 * @author     Guy Divita
 * @created    Jul 17, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils.framework.uima;

import java.io.UnsupportedEncodingException;

/**
 * @author divita
 *
 */
public class ToUtf8 {

  // =================================================
  /**
   * main [TBD] summary
   * 
   * @param args
  */
  // =================================================
 public static void main(String[] args) {
   

  } // end Method Main() -----------------------------

  // =================================================
  /**
   * process returns utf 8 encoded text
   * 
   * @param pReportText
   * @return String
  */
  // =================================================
   public static String process(String pReportText) {
    
    String returnVal = pReportText;
    try {
      returnVal = fixEncoding ( pReportText);
    } catch (Exception e ) {
      e.printStackTrace();
      System.err.println("Issue with non utf-8 encoding " + e.toString());
    }
    return returnVal; 
  } // end Method process() -----------------------
   
  // =================================================
  /**
   * fixEncoding returns utf 8 encoded text
   * 
   * @param pReportText
   * @return String
  */
  // =================================================
   public static String fixEncoding(String latin1) {
     try {
      // byte[] bytes = latin1.getBytes("US-ASCII");
       byte[] bytes = latin1.getBytes("UTF-8");
       if (!validUTF8(bytes))
         return latin1;   
       return new String(bytes, "UTF-8");  
     } catch (UnsupportedEncodingException e) {
      // Impossible, throw unchecked
      throw new IllegalStateException("No UTF-8: " + e.getMessage());
     }

    } // end Method fixEncoding() ------------------

   // =================================================
   /**
    * validUTF8 returns true if the encoding is utf 8
    * 
    * @param pReportText
    * @return boolean
   */
   // =================================================
    public static boolean validUTF8(byte[] input) {
     int i = 0;
     // Check for BOM
     if (input.length >= 3 && (input[0] & 0xFF) == 0xEF
       && (input[1] & 0xFF) == 0xBB & (input[2] & 0xFF) == 0xBF) {
      i = 3;
     }

     int end;
     for (int j = input.length; i < j; ++i) {
      int octet = input[i];
      if ((octet & 0x80) == 0) {
       continue; // ASCII
      }

      // Check for UTF-8 leading byte
      if ((octet & 0xE0) == 0xC0) {
       end = i + 1;
      } else if ((octet & 0xF0) == 0xE0) {
       end = i + 2;
      } else if ((octet & 0xF8) == 0xF0) {
       end = i + 3;
      } else {
       // Java only supports BMP so 3 is max
       return false;
      }

      while (i < end) {
       i++;
       octet = input[i];
       if ((octet & 0xC0) != 0x80) {
        // Not a valid trailing byte
        return false;
       }
      }
     }
     return true;
    } // end Method validUTF8() ----------

  // =================================================
  /**
   * processToXMLChars replaces characters that cannot be 
   * represented via XML into XML 1.0 compliant text.
   * 
   * (essentially, anything outside of ASCII 7 )
   * 
   * Characters outside that range are replaced with spaces
   * 
   * @param pReportText
   * @return String
  */
  // =================================================
  public static String processToXMLChars(String pReportText) {
  
    String returnVal = null;
    
    char[] chars = pReportText.toCharArray();
    StringBuffer buff = new StringBuffer();
    
    for ( int i = 0; i < chars.length; i++) {
      if (chars[i] == '\t' || chars[i] == '\n' || chars[i] == 13 || ( chars[i] >= ' ' && chars[i] <= '~' )) 
       
        buff.append(chars[i]);
      else
        buff.append(' ');
        
    }
    returnVal = buff.toString();
    
    return returnVal;
   
  } // end Method processToXMLChars() ----------------

} // end Class ToUtf8 
