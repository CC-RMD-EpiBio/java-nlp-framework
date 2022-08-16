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
null
