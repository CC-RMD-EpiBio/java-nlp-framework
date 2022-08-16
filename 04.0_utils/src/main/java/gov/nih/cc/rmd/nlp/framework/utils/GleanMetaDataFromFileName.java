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
 * GleanMetaDataFromFileName is a simple way
 * to get meta data from a file - using
 * what's stuffed into the name of the file
 *
 * @author     Guy Divita
 * @created    Mar 15, 2018
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GleanMetaDataFromFileName {

  public static final char DateDelimiterChar = '.';

  // =================================================
  /**
   * getMetaData returns a FileMetaData instance
   * with the date part and name part from the file
   * And if there is reference to a page number, the page number
   * 
   * @param pFileName
   * @return FileMetaData 
  */
  // =================================================
  public static final FileMetaData getFileMetaData(String pFileName ) {
   
    FileMetaData metaData = null;
    
    // Find the date part of the string first
    // xxxxx2014-12-21
    // xxxxx01-24-1994
    // xxxxx02-24
    // xxxxx02_24
    // xxxxx02/05/21 <----- not allowed in a filename
    // xxxxx02.04.21
    // xxxxx02.04.2001  
    // replace all characters with X.  
    // remove leading and trailing x's
    // replace X with a date delimiter
    char[] dateChars = new String(pFileName).toCharArray();
    char[] docTypeChars = new String( pFileName). toCharArray();
    for ( int i = 0; i < dateChars.length; i++ ) { 
      char c = dateChars[i];
      
      if ( isDateDelimiterChar( c ) ) {
        dateChars[i] = '\001';
        docTypeChars[i] = ' ';
      } else {
        if ( Character.isDigit(c) ) 
          docTypeChars[i] = ' ';
        else 
          dateChars[i] = ' ';
        
      }
    }
      
    
    
    String dateString = new String (dateChars ).trim();
    dateString = dateString.replace('.', ' ').trim();
    dateString = dateString.replace('\001', DateDelimiterChar);
    
   String     docType = new String( docTypeChars).trim();
    
    metaData = new FileMetaData();
    metaData.setDocumentDate( dateString);
    docType = docType.replace(" txt", "");
    metaData.setDocumentType( docType);
    
    metaData.setDocumentName( pFileName);
    
    metaData.setPageNumber( getPageNumber( pFileName));
   
    
    return metaData;
  }  // End Method 
  
  // =================================================
  /**
   * getPageNumber returns -1 if nothing is found or the page
   * number found in the filename
   * 
   * 
   * @param pFileName
   * @return int
  */
  // =================================================
  private final static int getPageNumber(String pFileName) {
    int returnVal = -1;
    
   
    Matcher matcher = pageNoPattern.matcher(pFileName);
    while (matcher.find()) {
     try {
      String pageNo = matcher.group(1);
      returnVal = Integer.parseInt(pageNo );
      break;
     } catch (Exception e) {};
    }
    
    return returnVal;
  }

  // =================================================
  /**
   * isDateDelimiterChar is /   01/01/1992  
   *                        .   01.01.1992
   *                        -   01-01-1992
   *                        _   01_01_1992
   *                        :   01:01:1992
   *                        
   * 
   * @param pChar
   * @return boolean
  */
  // =================================================
  public final static boolean isDateDelimiterChar(char pChar) {
   
    boolean returnVal = false;
    switch ( pChar ) {
      case '/' : returnVal = true; break;
      case '.' : returnVal = true; break;
      case '-' : returnVal = true; break;
      case '_' : returnVal = true; break;
      case ':' : returnVal = true; break;
    }
    return returnVal;
  } // end method isDateDelimiterChar() -------------



  // end Method getDocumentType() ------------------
  
  static final Pattern pageNoPattern = Pattern.compile("[p|P]age.(\\d)");

} // end Class GleanMetaDataFromFilename() -----
