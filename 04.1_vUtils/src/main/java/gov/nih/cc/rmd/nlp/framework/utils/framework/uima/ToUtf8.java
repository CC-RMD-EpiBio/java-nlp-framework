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
