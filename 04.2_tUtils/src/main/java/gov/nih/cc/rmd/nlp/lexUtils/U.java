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
 * U includes a bunch of lexical methods that are needed
 * within the vinci nlp framework.
 *
 *
 * @author  Guy Divita 
 * @created Feb 21, 2011
 *
 * [Utah/Salt Lake City Preamble Here]
 */
// ================================================
package gov.nih.cc.rmd.nlp.lexUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class U {

  // -----------------------------------------
  /**
   * containsPunctuation returns true if one or more characters of this token
   * are punctuation
   * 
   * @param pValue
   * @return boolean
   */
  // -----------------------------------------
  public static boolean containsPunctuation(String pValue) {
    boolean returnValue = false;

    if (pValue != null && pValue.length() > 0) {
      char[] chars = pValue.toCharArray();

      for (int i = 0; i < chars.length; i++) {
        if (isPunctuation(chars[i])) {
          returnValue = true;
          break;
        }
      }
    }
    return returnValue;
  } // end Method containsPunction() ---------

  // -----------------------------------------
  /**
   * containsSymbol returns true if one or more characters of this token are a
   * symbol or ligature. For instance (TM), (R), degree, ae, Alpha, Beta, gamma,
   * mu. [TBD]
   * 
   * @param pValue
   * @return boolean
   */
  // -----------------------------------------
  public boolean containsSymbol(String pValue) {
    boolean returnValue = false;

    return returnValue;
  } // end Method containsSymbol() ---------

  // -----------------------------------------
  /**
   * isSentenceBreak returns true if this character is a sentence breaking piece
   * of punctuation. Periods, question marks, exclamation marks are, semi-colons
   * and colons should be configurable and by default should be. [TBD]
   * 
   * @param pChar
   * @return
   */
  // -----------------------------------------
  public boolean isSentenceBreakChar(char pChar) {
    boolean returnValue = false;
    if (":;.!?".indexOf(pChar) > -1)
      returnValue = true;
    return returnValue;
  } // end Method isSentnceBreakChar() ------

  // ----------------------------------
  /**
   * isAllCaps returns true if all the chars in the string are uppercase.
   * 
   * Note: Right now a term like BRA13 would return false because the numbers
   * would fail the uppercase test.
   * 
   * @param pValue
   * @return boolean true if all the chars are uppercase.
   */
  // ----------------------------------
  public boolean isAllCaps(String pValue) {
    boolean returnValue = true;
    char buff[] = pValue.toCharArray();
    for (int i = 0; i < buff.length; i++) {
      if (!Character.isUpperCase(buff[i])) {
        returnValue = false;
        break;
      } // end if this char is not uppercase
    } // end loop through the chars of the buffer
    return returnValue;
  } // end method isAllCaps() ----------

  // ----------------------------------
  /**
   * isInitialCap returns true if the first letter is uppercase and the rest are
   * lowercase.
   * 
   * @param pValue
   * @return boolean true if the first letter is uppercase and the rest are
   *         lowercase.
   */
  // ----------------------------------
  public boolean isInitialCap(String pValue) {
    boolean returnValue = false;
    char buff[] = pValue.toCharArray();
    if ((buff != null) && (buff.length > 0))
      if (Character.isUpperCase(buff[0])) {
        returnValue = true;
        for (int i = 1; i < buff.length; i++) {
          if (Character.isUpperCase(buff[i])) {
            returnValue = false;
            break;
          } // end if statement
        } // end loop through each char of the buff
      } // end if the first char is upper case
    return returnValue;
  } // end method isInitialCap() ------

  // -----------------------------------------
  /**
   * isPunctuation returns true if this character is a piece of punctuation.
   * 
   * @param pChar
   * @return boolean (true if is punctuation)
   */
  // -----------------------------------------
  public static boolean isPunctuation(char pChar) {
    boolean returnValue = false;
    
    if ( !Character.isWhitespace( pChar) &&
         !Character.isLetterOrDigit(pChar) )
    
      returnValue = true;

    return returnValue;
  } // end Method isPunctuation() -----

  // -----------------------------------------
  /**
   * isNumber returns true if this string has no letters in it
   * 
   * @param pString
   * @return boolean (true if this is a number )
   */
  // -----------------------------------------
  public boolean isNumber(String pString) {
    boolean returnValue = false;

    if ((pString.matches("\\d+$")) && (!pString.matches("\\w")))
      returnValue = true;

    return returnValue;
  } // end Method isPunctuation() -----

  // -----------------------------------------
  /**
   * isOnlyPunctuation returns true if all the characters in this string are
   * punctuation.
   * 
   * 
   * @param pValue
   * @return boolean
   */
  // -----------------------------------------
  public static boolean isOnlyPunctuation(String pValue) {
    boolean returnValue = true;

    if (pValue != null && pValue.length() > 0) {
      char[] chars = pValue.toCharArray();

      for (int i = 0; i < chars.length; i++) {
        if (!isPunctuation(chars[i])) {
          returnValue = false;
          break;
        }
      }
    } else {
      returnValue = false;
    }
    return returnValue;
  } // end Method isOnlyPunctuation() -

  // -----------------------------------------
  /**
   * hasMultiplePeriods returns true if the number of periods in the string are
   * more than one.
   * 
   * @param pValue
   * @return boolean
   */
  // -----------------------------------------
  public boolean hasMultiplePeriods(String pValue) {
    boolean returnValue = false;

    char buff[] = pValue.toCharArray();
    int count = 0;
    for (int i = 0; i < buff.length; i++) {
      if (buff[i] == '.') {
        count++;
        if (count > 1) {
          returnValue = true;
          break;
        }
      } // end if period was found
    } // end loop through chars of the string

    return returnValue;
  } // end Method hasMultiplePeriods() -------

  // -----------------------------------------
  /**
   * getHomeDirectory searches the classpath to pick up the last instance that
   * includes vinciNLPFramework and uses the path to it as the homeDirectory
   * 
   * @return String (null if not found)
   */
  // -----------------------------------------
  public static String getHomeDirectory() {
    String homeDirectory = "                                                                                                                                              ";

    String buff = System.getProperty("java.class.path");
    String[] paths = buff.split(File.pathSeparator);

    for (int i = 0; i < paths.length; i++) {
      if (paths[i].contains("vinciNLPFramework")) {
        if (paths[i].length() < homeDirectory.length())
          homeDirectory = paths[i];

      }

    }
    homeDirectory = homeDirectory.replaceAll("\\\\", "/") + "/";

    return homeDirectory;
  } // end Method getHomeDirectory() -----------

  // -----------------------------------------
  /**
   * getClassPathResource searches the system clasloader's classpath to pick up
   * the the file mentioned
   * 
   * @param pRelativeFilePath
   * @return BufferedReader (null if not found)
   * @exception Excepetion
   */
  // -----------------------------------------
  public static BufferedReader getClassPathResource(String pRelativeFilePath)
      throws Exception {

    BufferedReader returnValue = null;
    ClassLoader cl = ClassLoader.getSystemClassLoader();
    InputStream stream = cl.getResourceAsStream(pRelativeFilePath);
    returnValue = new BufferedReader(new InputStreamReader(stream));
    return returnValue;
  } // end Method getClassPathResource() ---------

  // -----------------------------------------
  /**
   * getClassPathToResources searches the system clasloader's classpath to
   * return the parent path to where ./classes/.. ... ... /resources is
   * 
   * @return String (null if not found)
   * @exception Excepetion
   */
  // -----------------------------------------
  public static String getClassPathToResources() throws Exception {
    String returnValue = "                                                                                                                                              ";

    String buff = System.getProperty("java.class.path");

    String[] paths = buff.split(File.pathSeparator);

    for (int i = 0; i < paths.length; i++) {
      if ((paths[i].contains("classes"))
          && (paths[i].contains("lexUtils"))) {
        if (paths[i].length() < returnValue.length())
          returnValue = paths[i];

      }
    }
    returnValue = returnValue.replaceAll("\\\\", "/") + "/";

    return returnValue;
  } // end Method getClassPathToResources() ------

  // -----------------------------------------
  /**
   * normalize returns a string that can be represented on one line, without
   * special characters
   * 
   * @param coveredText
   * @return String
   */
  // -----------------------------------------
  public static String normalize(String coveredText) {
    String returnValue = coveredText;
    returnValue = returnValue.replace('\n', ' ');
    returnValue = returnValue.replace('\r', ' ');

    return returnValue;
  } // end Method normalize() ----------------

  // -------------------------------------------------------
  /**
   * HashToList returns a list of the items in the hash.
   * 
   * @param subTerms
   * @return List<>
   */
  // -------------------------------------------------------
  public static List<? extends Object> HashToList(Hashtable<String, ?> subTerms) {
    ArrayList<Object> list = null;

    if (subTerms != null) {
      list = new ArrayList<Object>(subTerms.size());
      Enumeration<?> elements = subTerms.elements();

      while (elements.hasMoreElements()) {
        list.add(elements.nextElement());
      } // end loop through the list of elements;
    } // end if there is a hash or not

    return list;
  } // end Method HashToList() --------------------

  // -----------------------------------------
  /**
   * openStandardInput returns back the handle for stdin
   * 
   * @return
   */
  // -----------------------------------------
  public static BufferedReader openStandardInput() {
    if (standardInput == null) {

      // ---------------------------------------
      // Open the standard input to get messages
      // ---------------------------------------
      try {
        standardInput = new BufferedReader(new InputStreamReader(System.in));
      } catch (RuntimeException e3) {
        System.err.println("Not able to open the standard input for reading");
      }
    }

    return (standardInput);

  } // end Method openStandardInput() -----------

  // -----------------------------------------
  /**
   * Method toString returns this environment variable stated in an OS/shell
   * specific way. For windows, it will be the set var=val format, for UNIX it
   * will be the Bourne shell var = val; \n export $var; format
   * 
   * 
   * @return boolean
   */
  // -----------------------------------------
  public static boolean isWindows() {
    boolean returnCode = false;
    String osName = System.getProperty("os.name");

    if (osName.toLowerCase().indexOf("windows") > -1)
      returnCode = true;

    return (returnCode);
  } // end Method isWindows() ----------------

  // ---------------------------------------------
  // Class variables
  // ---------------------------------------------

  // -----------------------------------------
  /**
   * run executes (almost) everything that can be run from a commandline
   * 
   * @param pCommand
   * @return String (Anything that comes back from the sent command)
   */
  // -----------------------------------------
  public static String run(String pCommand) {
    Process p = null;
    StringBuffer buff = new StringBuffer();
    StringBuffer errorBuff = new StringBuffer();

    try {
      if (runtime == null)
        runtime = Runtime.getRuntime();

      if (runtime != null) {
        p = runtime.exec(pCommand.trim());

        BufferedReader outputStream = new BufferedReader(new InputStreamReader(
            p.getInputStream()));

        String line = null;
        while ((line = outputStream.readLine()) != null) {
          buff.append(line);
          buff.append(U.NL);
        }
        outputStream.close();

        outputStream = new BufferedReader(new InputStreamReader(
            p.getErrorStream()));

        line = null;
        while ((line = outputStream.readLine()) != null) {
          errorBuff.append(line);
          errorBuff.append(U.NL);
          buff.append(line);
          buff.append(U.NL);
        }
        outputStream.close();
        // ------------------------------------------------------
        // I descided to have the error put on the string instead
        // rather than spew out an error
        // ------------------------------------------------------
        // if ( errorBuff.length() > 0 ) {
        // System.err.println("? Error: " + errorBuff );
        // }
      } else {
        System.err.print("runtime is null, aborting...");
        System.exit(0);
      }
    } catch (IOException e2) {
      System.err.println("Problem with getting output from the command " + U.NL
          + pCommand + U.NL + e2.toString());
      System.err.flush();
    } catch (RuntimeException e) {
      System.err.println("Problem running the command |" + pCommand + "| "
          + e.getMessage() + "|" + e.toString());
      e.printStackTrace(System.err);
      System.err.flush();

    }
    if ( p != null )
      p.destroy();
    p = null;

    return (buff.toString());

  } // end Method Run() ---------------------

  

  // ----------------------------------------------
  /**
   * quote returns a string with quotes around it
   * 
   * @param pVal
   * @return String
   */
  // ----------------------------------------------
  public static String quote(int pVal) {
    StringBuffer buff = new StringBuffer();
    buff.append('"');
    buff.append( pVal);
    buff.append('"');
    return buff.toString();
  } // end Method quote() -------------------------
  

// ----------------------------------------------
  /**
   * unquote remove the sorrounding quotes from a string
   * 
   * @param string
   * @return
   */
  // ----------------------------------------------
  public static String unquote(String pString) {
    String buff = pString;
    if ( pString.charAt(0)== '"' ) {
      if ( pString.charAt(pString.length() -1) == '"' )
        buff = pString.substring(1,pString.length()-1);
    }

    return buff;
  } // end Method unquote() -----------------------

  //----------------------------------------------
  /**
   * toString
   * 
   * @param pList
   * @return
   */
  // ----------------------------------------------

  public static String toString(List<?> pList) {
    String returnValue = null;
    
    if ( pList != null ) {
      StringBuffer buff = new StringBuffer();
    
      Iterator<?>  i = pList.iterator();
      while ( i.hasNext() ) {
        buff.append("     ");
        buff.append( i.next().toString());
        buff.append(U.NL);
      } // end loop 
      returnValue = buff.toString();
    }
    return returnValue;
  } // end Method toString( List) 
  
  // ----------------------------------------------
  /**
   * split Given a pipe delimited string, return the contents
   * of the fields, one per string. This method handles null fields.
   * 
   * @param pRow
   * @param pDelimiter
   * @return String[]
   */
  // ----------------------------------------------
  public static String[] split(String pRow, String pDelimiter) {

    String[] returnValue = null;
    String buff = null;
    String delimiter = pDelimiter;
    if ( pDelimiter.equals("|")) delimiter = "\\|"; // <--- needed to be backslashed because pipe is a special char in re.
    if ( pDelimiter.equals("(")) delimiter = "\\("; // <--- needed to be backslashed because ( is a special char in re.
    if ( pDelimiter.equals(")")) delimiter = "\\)"; // <--- needed to be backslashed because ) is a special char in re.
    if ( pDelimiter.equals("-")) delimiter = "\\-"; // <--- needed to be backslashed because - is a special char in re.
    // if ( pDelimiter.equals(",")) delimiter = "\\,"; // <--- needed to be backslashed because - is a special char in re.
    int i = 0;

    
    int maxFields = 0;
    if ((pRow != null) && (pRow.trim().length() > 0)) {

      StringTokenizer st = new StringTokenizer( pRow, delimiter, true);
      ArrayList<String> val = new ArrayList<String>(st.countTokens());
      while (st.hasMoreElements()) {
        
        buff = st.nextToken();
        if (buff.compareTo(pDelimiter ) == 0) {
          maxFields++;
          i++;
          if (i == 2) {
            val.add(new String(""));
            --i;
          }

        } else {
          if ( maxFields < i ) maxFields = i;
          val.add(new String(buff));
          i = 0;
        }
      }
          //   2        <      2  +1             =  val should  = 3
          //   3
      //if ( val.size() < maxFields +1) 
       // maxFields = val.size() + 1 ;
      st = null;
      buff = null;
      val.trimToSize();

      
      returnValue = new String[maxFields+1];
      for (int k = 0; k < maxFields+1; k++)
        if ( k < val.size())
          returnValue[k] = (String) val.get(k);
        else
          returnValue[k]="";
    }
    return returnValue;
  } // end Method split() -------------------------

//----------------------------------------------
  /**
   * split Given a pipe delimited string, return the contents
   * of the fields, one per string. This method handles null fields.
   * 
   * @param string
   * @return String[] 
   */
  // ----------------------------------------------
  public static String[] split(String pRow) {
    
    String [] returnValue = U.split( pRow, "|");
    
    return( returnValue );
    
  } // end Method split() -------------------------
  
  // ----------------------------------------------
  /**
   * quote returns a string with quotes around it.
   * [Replaces doublequotes in the text with single quotes]
   * 
   * @param pVal
   * @return String
   */
  // ----------------------------------------------
  public static String quote(String pVal) {
    StringBuffer buff = new StringBuffer();
    buff.append('"');
    if ( pVal != null ) {
      String val = pVal.replace('"', '\'' );
      buff.append( val);
    }
    buff.append('"');
    return buff.toString();
  } // end Method quote() -------------------------

  // ----------------------------------------------
  /**
   * containsLetters
   * 
   * @param rBuff
   * @return boolean
   */
  // ----------------------------------------------
  public static boolean containsLetters(String rBuff) {
    boolean returnVal = false;
    
    for ( int i = 0; i < rBuff.length(); i++ ) {
      if ( Character.isLetter(rBuff.charAt(i)) ) {
        returnVal = true;
        break;
      }
    }
    return returnVal;
  } // end Method containsLetters() ---------------

  // ----------------------------------------------
  /**
   * allUpperCase returns true if all the characters in the
   * string are uppercase.
   * 
   * @param content
   * @return
   */
  // ----------------------------------------------
  public static boolean allUpperCase(String content) {
    boolean returnVal = true;
    
    for ( int i = 0; i < content.length(); i++ ) {
      if ( Character.isLowerCase(content.charAt(i) )) {
        returnVal = false;
        break;
      }
          
    }
    return returnVal;
    
  } // end Method allUpperCase() ------------------

  // =================================================
  /**
   * isCamelCase returns true if each word of the text begins with caps and the rest
   * of the letters are lowercase
   * 
   * @param  pText
   * @return boolean 
  */
  // =================================================
  public static boolean isCapsCamelCase(String pText) {
    boolean returnVal = true;
    
    String words[] = U.split(pText, " ");
    if ( words != null && words.length > 0 ) {
      for ( String word: words )
        if ( !isCapsCamelCaseWord( word )) {
          returnVal = false;
          break;
        }
    }
    
    return returnVal;
  }  // end Method isCamelCase() ------------------
  
  
//=================================================
 /**
  * isCamelCaseWord returns true if the word of the text begins with caps and the rest
  * of the letters are lowercase
  * 
  * @param  pText
  * @return boolean  false if a null word is passed in as well
 */
 // =================================================
 public static boolean isCapsCamelCaseWord(String pText) {
   boolean returnVal = true;
   
   if ( pText != null && pText.trim().length() > 0 ) {
   char wordChars[] = pText.toCharArray();
   int lowerCaseSeen = 0;
   
   int k = 0;
   for ( k = 0; k < wordChars.length; k++ )
     if ( Character.isLetter(wordChars[k]))
       break;
   
   if ( k < wordChars.length && Character.isLetter( wordChars[k]) && Character.isUpperCase( wordChars[k] )) {
     
     for ( int i = k+1; i < wordChars.length; i++ ) 
       if ( Character.isLowerCase(wordChars[i] )) {
         lowerCaseSeen++;
         break;
       }
     if ( lowerCaseSeen > 0 )
       returnVal = true;
     
   } else 
     returnVal = false;
     
   }
   
   return returnVal;
 }  // end Method isCamelCase() ------------------
  

  // ----------------------------------------------
  /**
   * numberOf returns the number of time this pattern was seen
   * in the given pBuff
   * 
   * @param pBuff
   * @param pPattern
   * @return int
   */
  // ----------------------------------------------
  public static int numberOf(String pBuff, String pPattern) {
    int from = 0;
    int newFrom = 0;
    int returnVal = 0;
    while (  (newFrom = pBuff.indexOf(pPattern, from )) > 0){ 
      returnVal++;
      from = newFrom + 1;
    }
    
    return returnVal;
  } // end Method numberOf() --------------

  // =================================================
  /**
   * stripLeadingAndTrailingPunctuation
   * 
   * @param pName
   * @return String
  */
  // =================================================
  public final static String  stripLeadingAndTrailingPunctuation(String pName) {

    String returnVal = pName.trim();
    
    StringBuffer buff = new StringBuffer();
    char chars[] = pName.toCharArray();
    
    
    for (int i = 0; i < chars.length ; i ++ ) 
      if (  Character.isWhitespace( chars[i] )  )
    	  buff.append( chars[i]);
      else if ( Character.isLetterOrDigit(chars[i] ))
          buff.append(chars[i]);
      else if ( chars[i] == '_' || chars[i] == '-' )
    	  buff.append(" ");
      else if ( i == 0)
        ;
      else if ( i == chars.length -1)
        ;
       
    
    returnVal = buff.toString();
    
    return returnVal;
  } // end Method stripLeadingAndTrailingPunctuation()  ---------------
  

  public static final String NL = System.getProperty("line.separator").toString();
  public static final String Newline = System.getProperty("line.separator").toString();
  public static final String FS = System.getProperty("file.separator").toString();
  public static final String PS = System.getProperty("path.separator").toString();
  public static final String JV = System.getProperty("java.version").toString();
  public static final String HR = "====================================================================";

  private static BufferedReader standardInput = null;
  private static Runtime runtime = null;
  

} // end Class U() --------------------------------
