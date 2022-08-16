/*
 *
 */
/**
 * U includes a bunch of lexical methods that are needed
 * within the vinci nlp framework.
 *
 *
 * @author  Guy Divita 
 * @created Feb 21, 2011
 * @modified Dec 28, Dec 29
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * The Class U.
 */
public class U {

  // public final static String
  // PUNCTUATON_LIST="~`!@#$%^&\\*\\(\\)_\\-\\+={}\\[\\]\\|\\\\:;'<>\\?\\/,\\.\"";

  // ---------------------------------------------
  // Class variables
  /** The Constant NL. */
  // ---------------------------------------------
  public static final String NL = System.getProperty("line.separator").toString();

  /** The Constant Newline. */
  public static final String Newline = System.getProperty("line.separator").toString();

  /** The Constant FS. */
  public static final String FS = System.getProperty("file.separator").toString();

  /** The Constant PS. */
  public static final String PS = System.getProperty("path.separator").toString();

  /** The Constant JV. */
  public static final String JV = System.getProperty("java.version").toString();

  /** The Constant HR. */
  public static final String HR =
      "====================================================================";

  /** The Constant EOF_CHAR. */
  private static final char EOF_CHAR = (char) 26;

  /** The standard input. */
  private static BufferedReader standardInput = null;

  /** The runtime. */
  private static Runtime runtime = null;

  /** The df 2. */
  private static DecimalFormat df2 = null;
  
  /** Run in eclipse or not inEclipse */
  private static boolean inEclipse = false;
  
  /** Run in eclipse flag inEclipseStr - this works in conjunction with putting on the command line -DrunInEclipse=true */
  private static String inEclipseStr = null;  

  // private static Logger gLog =
  // LogManager.getLogger("gov.nih.cc.rmd.framework.utils");

  // ----------------------------------------------
  /**
   * allUpperCase returns true if all the characters in the string are
   * uppercase.
   *
   * @param content the content
   * @return String
   */
  // ----------------------------------------------
  public final static boolean allUpperCase(String content) {
    if (content == null) {
      throw new IllegalArgumentException("String cannot be null.");
    }
    return content.equals(content.toUpperCase());
  } // end Method allUpperCase() ------------------

  // -----------------------------------------
  /**
   * charCount returns the number of char in the string.
   *
   * @param pKey the key
   * @param pPattern the pattern
   * @return int
   */
  // -----------------------------------------
  public final static int charCount(String pKey, char pPattern) {
    int returnVal = 0;

    if ((pKey != null) && (pKey.length() > 0)) {
      char[] buff = pKey.toCharArray();
      for (char c : buff)
        if (c == pPattern)
          returnVal++;
    }

    return returnVal;
  } // end charCount()-----------------------------

  // ----------------------------------------------
  /**
   * containsLetters.
   *
   * @param rBuff the r buff
   * @return boolean
   */
  // ----------------------------------------------
  public final static boolean containsLetters(String rBuff) {
    boolean returnVal = false;

    for (int i = 0; i < rBuff.length(); i++) {
      if (Character.isLetter(rBuff.charAt(i))) {
        returnVal = true;
        break;
      }
    }
    return returnVal;
  } // end Method containsLetters() ---------------

  // =======================================================
  /**
   * containsNumber returns true if any of the characters in this string are a
   * number.
   *
   * @param rBuff the r buff
   * @return boolean
   */
  // =======================================================
  public final static boolean containsNumber(String rBuff) {
    boolean returnVal = false;
    
    if ( rBuff != null)
      for (int i = 0; i < rBuff.length(); i++) {
        if (Character.isDigit(rBuff.charAt(i))) {
          returnVal = true;
          break;
        }
    }
    return returnVal;
  } // End Method containsNumber() ======================

  // -----------------------------------------
  /**
   * containsPunctuation returns true if one or more characters of this token
   * are punctuation.
   *
   * @param pValue the value
   * @return boolean
   */
  // -----------------------------------------
  public final static boolean containsPunctuation(String pValue) {
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
   * @param pValue the value
   * @return boolean
   */
  // -----------------------------------------
  public final static boolean containsSymbol(String pValue) {
    boolean returnValue = false;

    return returnValue;
  } // end Method containsSymbol() ---------

  // ------------------------------------------
  /**
   * containsTokenSplittingChar returns true if the chars are one of / \ + - : ;
   * ( ) [ ] { } ?.
   *
   * @param pTokenString the token string
   * @return boolean
   */
  // ------------------------------------------
  public final static boolean containsTokenSplittingChar(String pTokenString) {
    boolean returnVal = false;

    if (pTokenString != null && pTokenString.length() > 0) {
      char[] chars = pTokenString.toCharArray();
      for (char c : chars) {
        if (U.containsTokenSplittingChar(c)) {
          returnVal = true;
          break;
        }
      } // end loop throuch chars
    } // end if there were any chars to work with

    return returnVal;

  } // End Method containsTokenSplittingChar() -----------------------

  // ------------------------------------------
  /**
   * containsTokenSplittingChar returns true if the chars are one of / \ + - : ;
   * ( ) [ ] { } ? " ~ | , .
   *
   * N.B.  taking out / from the mix  - GD 2022-04-28
   * @param c the c
   * @return boolean
   */
  // ------------------------------------------
  public final static boolean containsTokenSplittingChar(char c) {
    boolean returnVal = false;
    
    // c == '/' ||
    if ( c == '\\' || c == '+' || c == '-' || c == ':' || c == ';' || c == '('
        || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' || c == '.' || c == '"'
        || c == '|' || c == '~' || c == ',' || c == '?')
      returnVal = true;

    return returnVal;

  } // End Method containsTokenSplittingChar() -----------------------

  // ----------------------------------------------
  /**
   * display strips unprintable chars.
   *
   * @param pVal the val
   * @return String
   */
  // ----------------------------------------------
  public final static String display(String pVal) {
    String val = null;

    if (pVal != null) {
      val = pVal.replace('\n', ' ');
      val = val.replaceAll(U.NL, " ");
      val = val.replaceAll("  ", " ");
      val = val.replaceAll("\r", " ");

    }

    return val;
  } // end Method quote() -------------------------

  // ------------------------------------------
  /**
   * display2 replaces \n with <NEWLINE> and \r with <CARRAGERETURN> | with
   * <PIPE>.
   *
   * @param pVal the val
   * @return String
   */
  // ------------------------------------------
  public final static String display2(String pVal) {

    String val = null;
    if (pVal != null) {
      val = pVal.replace("\r\n", " <NEWLINE> ");
      val = val.replaceAll("\n", " <NEWLINE> ");
      val = val.replaceAll("  ", " ");
      val = val.replaceAll("\r", " <NEWLINE> ");
      val = val.replaceAll("\\|", " <PIPE> ");
    }
    return val;

    // End Method display2() -----------------------
  }

  // =======================================================
  /**
   * displayForCSV formats for CSV files. The delimeter is passed in to replace
   * any instances of this character with one that is not a csv delimiter. It
   * also
   *
   * @param pCoveredText the covered text
   * @param pCSVDelimeter the CSV delimeter
   * @param pNotCSVDelimeter the not CSV delimeter
   * @return String
   */
  // =======================================================
  public final static String displayForCSV(String pCoveredText, String pCSVDelimeter,
    String pNotCSVDelimeter) {
    String val = null;

    if (pCoveredText != null) {
      val = display2(pCoveredText);
      val = val.replaceAll(pCSVDelimeter, pNotCSVDelimeter);
    }
    return val;
  } // End Method displayForCSV() ======================

  // ------------------------------------------
  /**
   * prettyPringXML puts indents and newlines in xml encoded strings for human
   * consumption.
   *
   * @param input the input
   * @return String
   */
  // ------------------------------------------
  public final static String prettyPrintXML(String input) {
    return prettyPrintXML(input, 2);
  } // end Method prettyPrintXML() -------------

  // ------------------------------------------
  /**
   * prettyPringXML puts indents and newlines in xml encoded strings for human
   * consumption.
   *
   * @param input the input
   * @param indent the indent
   * @return String
   */
  // ------------------------------------------
  private static String prettyPrintXML(String input, int indent) {
    try {
      Source xmlInput = new StreamSource(new StringReader(input));
      StringWriter stringWriter = new StringWriter();
      StreamResult xmlOutput = new StreamResult(stringWriter);
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      // This statement works with JDK 6
      transformerFactory.setAttribute("indent-number", indent);

      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.transform(xmlInput, xmlOutput);
      return xmlOutput.getWriter().toString();
    } catch (Throwable e) {
      // You'll come here if you are using JDK 1.5
      // you are getting an the following exeption
      // java.lang.IllegalArgumentException: Not supported: indent-number
      // Use this code (Set the output property in transformer.
      try {
        Source xmlInput = new StreamSource(new StringReader(input));
        StringWriter stringWriter = new StringWriter();
        StreamResult xmlOutput = new StreamResult(stringWriter);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
            String.valueOf(indent));
        transformer.transform(xmlInput, xmlOutput);
        return xmlOutput.getWriter().toString();
      } catch (Throwable t) {
        return input;
      }
    }
  }

  // -----------------------------------------
  /**
   * getClassPathResource searches the system clasloader's classpath to pick up
   * the the file mentioned.
   *
   * @param pRelativeFilePath the relative file path
   * @return BufferedReader (null if not found)
   * @throws Exception the exception
   */
  // -----------------------------------------
  public final static BufferedReader getClassPathResource(String pRelativeFilePath)
    throws Exception {

    BufferedReader returnValue = null;
    // ClassLoader cl = ClassLoader.getSystemClassLoader();
    ClassLoader cl = U.class.getClassLoader();
    // ClassLoader cl = Thread.currentThread().getContextClassLoader();
    InputStream stream = cl.getResourceAsStream(pRelativeFilePath);
    if (stream != null) {
       
     
      URL resource = cl.getResource(pRelativeFilePath);
      if (resource == null) {
          throw new IllegalArgumentException("file not found! " + pRelativeFilePath);
      } else {
         returnValue = new BufferedReader(new InputStreamReader(stream));
          
      }
    
    } else {
      // String classPath = System.getProperty("java.class.path");
      final String msg = "Not able to read " + pRelativeFilePath + " from the classpath";
      // + "\n"
      // + "Classpath = \n" + classPath;
      throw new Exception(msg);
    }
    return returnValue;
  } // end Method getClassPathResource() ---------

  // -----------------------------------------
  /**
   * getClassPathToResource.
   *
   * @param resource the resource
   * @return URL
   * @throws Exception the exception
   */
  // -----------------------------------------
  public final static URL getClassPathToResource(String resource) throws Exception {

    URL r = null;
    // -----------------------------
    // If the resource is an absolute path, pass back the absolute path as a URL
    if (resource.startsWith("/")
        || (resource.length() > 2 && (resource.charAt(1) == ':') || resource.charAt(0) == '\\')) {
      File aFile = new File(resource);
      r = aFile.toURI().toURL();
    } else {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      r = cl.getResource(resource);
    }
    return r;
  }

  // -----------------------------------------
  /**
   * getClassPathToResources searches the system classloader's classpath to
   * return the parent path to where ./classes/.. ... ... /resources is
   *
   * @return String (null if not found)
   * @throws Exception the exception
   */
  // -----------------------------------------
  public final static String getClassPathToResources() throws Exception {
    String returnValue = null;

    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Enumeration<URL> r = cl.getResources("resources/framework/DO_NOT_PUT_IN_JAR.txt");

    if (r.hasMoreElements()) {
      while (r.hasMoreElements()) {
        String buff = r.nextElement().getPath();
        returnValue = buff.substring(0,
            buff.indexOf("resources/framework/DO_NOT_PUT_IN_JAR.txt"));

      }
    } else {

      returnValue = getClassPathToResourcesObs();
      if (returnValue == null)
        GLog.println(GLog.ERROR_LEVEL,
            "Unable to find the resource folder. Failed to find the file resources/framework/DO_NOT_PUT_IN_JAR.txt possibly because it did not get deployed there?");
      return null;
    }
   // GLog.println(GLog.STD___LEVEL, "the xxxxx classpath to Resources = |" + returnValue + "|");
    return returnValue;

  } // end Method getClassPathToResources() ------

  // -----------------------------------------
  /**
   * getClassPathToResources searches the system classloader's classpath to
   * return the parent path to where ./classes/.. ... ... /resources is
   *
   * @return String (null if not found)
   * @throws Exception the exception
   * @deprecated
   */
  // -----------------------------------------
  public final static String getClassPathToResourcesObs() throws Exception {

    String returnValue = null;

    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Enumeration<URL> r = cl.getResources("resources/vinciNLPFramework/DO_NOT_PUT_IN_JAR.txt");

    if (r.hasMoreElements()) {
      while (r.hasMoreElements()) {
        String buff = r.nextElement().getPath();
        returnValue =
            buff.substring(0, buff.indexOf("resources/vinciNLPFramework/DO_NOT_PUT_IN_JAR.txt"));

      }
    } else {
      // Todo: change this to a logger
      GLog.println(GLog.ERROR_LEVEL,
          "Unable to find the resource folder. Failed to find the file resources/com/ciitizen/framework/DO_NOT_PUT_IN_JAR.txt possibly because it did not get deployed there?");
      return null;
    }
    // GLog.println(GLog.STD___LEVEL, "the xxxxx classpath to Resources = |" + returnValue + "|");
    return returnValue;

  } // end Method getClassPathToResources() ------

  // -----------------------------------------
  /**
   * getClassPathToResources searches the system classloader's classpath to
   * return the parent path to where ./classes/.. ... ... /resources is
   *
   * @return String (null if not found)
   * @throws Exception the exception
   */
  // -----------------------------------------
  public final static String getClassPathToDataResources() throws Exception {

    String returnValue = null;

    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Enumeration<URL> r = cl.getResources("resources/data/DO_NOT_REMOVE.txt");

    if (r.hasMoreElements()) {
      while (r.hasMoreElements()) {
        String buff = r.nextElement().getPath();
        returnValue = buff.substring(0, buff.indexOf("resources/data/DO_NOT_REMOVE.txt"));

      }
    } else {
      // Todo: change this to a logger
      GLog.println(GLog.ERROR_LEVEL, U.class, "getClassPathToResources",
          "Unable to find the resource folder. Failed to find the file resources/data possibly because it did not get deployed there?");
      return null;
    }
    return returnValue;

  } // end Method getClassPathToResources() ------

  // -----------------------------------------
  /**
   * getClassPathToResources searches the system classloader's classpath to
   * return the parent path to where ./classes/.. ... ... /resources is
   *
   * @return String (null if not found)
   * @throws Exception the exception
   */
  // -----------------------------------------
  public final static String getClassPathToLocalResources() throws Exception {

    String returnValue = null;

    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Enumeration<URL> r = cl.getResources("resources/com/ciitizen/framework/local");

    if (r.hasMoreElements()) {
      while (r.hasMoreElements()) {
        String buff = r.nextElement().getPath();
        returnValue = buff.substring(0, buff.indexOf("resources/com/ciitizen/framework/local"));

      }
    } else {
      // Todo: change this to a logger
      // System.err.println("Unable to find the resource folder. Failed to find
      // the file resources/com/ciitizen/framework/local possibly because it did
      // not get deployed there?");
      return null;
    }
    return returnValue;

  } // end Method getClassPathToResources() ------

  // -----------------------------------------
  /**
   * getURLStringFromResource searches the system classloader's classpath to
   * return string of the URL after the path to it has been found.
   * 
   * I.e. this should return something like file:resources/xxx.css when
   * 
   * the xxx.css has been put to the path: src/main/resources/xxx.css (maven way
   * of doing this) Note that in the pom file you might have to have a resource
   * section filled with an include pattern <build> <resources> <resource>
   * <filtering>false</filtering> <directory>src/main/resources</directory>
   * <includes> <include> xxxx </include> </includes> </resource> </resources>
   *
   * @param pResourceName the resource name
   * @return String (null if not found)
   * @throws Exception the exception
   */
  // -----------------------------------------
  public final static String getURLStringFromResource(String pResourceName) throws Exception {

    String returnValue = null;

    try {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();

      URL t = cl.getResource(pResourceName);
      returnValue = t.toExternalForm();

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue getting the resource for " + pResourceName + " " + e.toString();
      throw new Exception(msg);
    }

    return returnValue;

  } // end Method getURLStringFromResource() ------

  // -----------------------------------------
  /**
   * getFileNamePrefix returns the name of the file devoid of the suffix If
   * pName is null, a "_" is returned.
   *
   * @param pName the name
   * @return String
   */
  // -----------------------------------------
  public final static String getFileNamePrefix(String pName) {
    String name = pName;

    if (name != null) {
      int lastSlash = pName.lastIndexOf("/");

      if (lastSlash > 0) {
        name = pName.substring(lastSlash + 1);
        int firstDot = name.lastIndexOf(".");
        if (firstDot > 0)
          name = name.substring(0, firstDot);
      } else {
        int firstDot = name.lastIndexOf(".");
        if (firstDot > 0)
          name = name.substring(0, firstDot);
      }
    } else {
      name = "_";
    }
    return name;
  } // end Method getFileNamePrefix() --------

  // -----------------------------------------
  /**
   * getHomeDirectory searches the classpath to pick up the last instance that
   * includes vinciNLPFramework and uses the path to it as the homeDirectory.
   *
   * @return String (null if not found)
   */
  // -----------------------------------------
  public final static String getHomeDirectory() {
    String homeDirectory = "";
    String buff = System.getProperty("java.class.path");
    String[] paths = buff.split(File.pathSeparator);

    for (int i = 0; i < paths.length; i++) {
      if (paths[i].contains("vinciNLPFramework")) {
        if ((homeDirectory.length() == 0) || paths[i].length() < homeDirectory.length())
          homeDirectory = paths[i];

      }

    }
    homeDirectory = homeDirectory.replaceAll("\\\\", "/") + "/";

    return homeDirectory;
  } // end Method getHomeDirectory() -----------

  // -----------------------------------------
  /**
   * getLineEndSize returns the number of characters that a line end holds.
   * 
   * The current algorithm is a dumb and stupid way of doing it - by peeking in
   * the document for the first occurrence of one of these patterns:
   * 
   * \r\n \n \r <CRLF>
   *
   * @param documentContent the document content
   * @return int
   */
  // -----------------------------------------
  public final static int getLineEndSize(String documentContent) {
    int lineEndSize = 2;

    if (documentContent.indexOf("\r\n") > -1)
      lineEndSize = 2;
    else if (documentContent.indexOf("<CRLF>") > -1)
      lineEndSize = 6;
    else if (documentContent.indexOf("\r") > -1)
      lineEndSize = 1;
    else if (documentContent.indexOf("\n") > -1)
      lineEndSize = 1;

    return lineEndSize;
  } // end Method getLineEndSize() -----------

  // -----------------------------------------
  /**
   * getNewlineOffsets returns an array of offsets for newlines found.
   *
   * @param textSource the text source
   * @return int[]
   */
  // -----------------------------------------
  public final static int[] getNewlineOffsets(String textSource) {
    ArrayList<Integer> intBuf = new ArrayList<Integer>();

    char buff[] = textSource.toCharArray();

    for (int i = 0; i < buff.length - 1; i++) {
      // char b = buff[i];
      // System.err.println("[" + i + "]" + "|" + b + "|" ) ;
      if ((buff[i] == '\r') && (buff[i + 1] == '\n'))
        intBuf.add(i++);
      else if (buff[i] == '\n')
        intBuf.add(i);
      else if (buff[i] == '\r')
        intBuf.add(i);
    } // end loop through chars of the text

    int intArray[] = new int[intBuf.size()];
    for (int i = 0; i < intArray.length; i++)
      intArray[i] = intBuf.get(i).intValue();

    return intArray;
  } // end Method getNewlineOffsets() -------------

  // -----------------------------------------
  /**
   * getNewlineType peeks in the first 1000 chars of this text to see what kind
   * of newlines are in it.
   *
   * @param docText the doc text
   * @return String
   */
  // -----------------------------------------
  public final static String getNewlineType(String docText) {
    String newLine = null;

    String firstChars = null;
    if (docText.length() > 1000)
      firstChars = docText.substring(0, 1000);
    else
      firstChars = docText;

    if (firstChars.indexOf("\r\n") > -1)
      newLine = "\r\n";
    else if (firstChars.indexOf('\r') > -1)
      newLine = "\r";
    else if (firstChars.indexOf('\n') > -1)
      newLine = "\n";
    else
      newLine = "\n";

    return newLine;

  } // end Method getNewlineType() ----------

  // -----------------------------------------
  /**
   * getOption is a simple command line option parser that retrieves command
   * line argument from the args[] variable. T
   * 
   * @param pArgs the main's args[] variable
   * @param pOption the argument looked for. Note, pass in the "--" or "-" as
   *          part of the option Arguments that have value must have "=" as the
   *          option=value delimiter. Not a space
   * 
   *          Binary options like "--upperCase return "true" when seen on the
   *          command line.
   * 
   * @param pDefault A default value, if no option is found.
   * @return String
   */
  // -----------------------------------------
  public final static String getOption(String[] pArgs, String pOption, String pDefault) {

    String value = pDefault;

    if (pArgs != null) {

      for (String key : pArgs) {
        if (key.startsWith(pOption)) {
          if (key.contains("=")) {
            String values[] = U.split(key, "=");
            value = values[1];
            value = value.replace('\\', '/' ); 
          } else
            value = "true";
        }
      } // end Loop through args
    } // end if there are any args
    return value;
  } // end method getOpt() ------------------------

  // =================================================
  /**
   * getOption
   * 
   * Hierarchy order of configuration values
   * 
   * default value unless there are values from config.properties unless there
   * are values from command line arguments
   * 
   * There is a translation between values from the config.properties to the
   * command line arguments. That is, the key passed around INCLUDES the "--"
   * and the "="
   * 
   * config.properties key = value \|/ \|/ command line args --key= value
   * 
   * In my code, I'm looking for "--key=" as the key in the hashes There is
   * historical reason for that. --key is a binary [true|false] vs --key=value.
   * Also it was just easier to pass the full string from the command line
   * arguments and parse it later than to remove the syntactic sugar everywhere
   * I was looking to store a command line argument that included a value.
   *
   * @param pArgs the args
   * @param pProperties the properties
   * @param pKey the key
   * @param pDefaultValue the default value
   * @return String
   */
  // =================================================
  public static final String getOption(String[] pArgs, Properties pProperties, String pKey,
    String pDefaultValue) {

    String value = pDefaultValue;

    String configKey = pKey.substring(2);
    if (configKey.endsWith("="))
      configKey = configKey.substring(0, configKey.length() - 1);
    value = pProperties.getProperty(configKey);

    if (pArgs != null) {

      for (String key : pArgs) {

        if (key.startsWith(pKey)) {
          if (key.contains("=")) {
            String values[] = U.split(key, "=");
            value = values[1];
          } else
            value = "true";
        }
      } // end Loop through args
    } // end if there are any args

    return value;
  } // End Method getOption() ------------------

  // -----------------------------------------
  /**
   * getOptionsFromHashMap converts hashMap<k,v> to [--k=v,--k=v, ...] This
   * method converts the hashMap to commandline posix style arguments that start
   * with --key=value pairs.
   * 
   * Any keys coming from the hashMap will overwrite the defaultArgs
   *
   * @param pHashMap the hash map
   * @param pDefaultArgs of the form --k=dV, --k=dV, ...]
   * @return String[] of the form [--k=v,--k=v,...]
   */
  // -----------------------------------------
  public final static String[] getOptionsFromHashMap(HashMap<String, String> pHashMap,
    String[] pDefaultArgs) {
    String[] returnVal = null;
    ArrayList<String> options = null;
    ArrayList<String> keptDefaults = null;

    if (pHashMap != null && !pHashMap.isEmpty()) {
      options = new ArrayList<String>(pHashMap.size());
      if (pDefaultArgs != null && pDefaultArgs.length > 0) {
        keptDefaults = new ArrayList<String>(pDefaultArgs.length);
        for (String anArg : pDefaultArgs) {
          String key_value[] = U.split(anArg, "=");
          String key = key_value[0];
          if (key.startsWith("--"))
            key = key.substring(2);
          // ------------------------------
          // look up the key in the hashMap
          String mapValue = pHashMap.get(key);
          if (mapValue == null)
            keptDefaults.add(anArg);
        } // end loop thru default args
      } // end if there are any default args
      Set<String> keys = pHashMap.keySet();
      returnVal = new String[pHashMap.size()];

      for (String key : keys)
        options.add("--" + key + "=" + pHashMap.get(key));

      if (keptDefaults != null && !keptDefaults.isEmpty())
        for (String arg : keptDefaults)
          options.add(arg);

      if (options != null)
        returnVal = options.toArray(new String[options.size()]);

    } else {
      returnVal = pDefaultArgs;
    }

    return returnVal;
  } // end Method getOptionsFromHashMap() ----

  // -----------------------------------------
  /**
   * getOption is a simple command line option parser that retrieves command
   * line argument from the args[] variable. T
   *
   * @param pArgs the args
   * @return String
   */
  // -----------------------------------------
  public final static boolean printArgs(String[] pArgs) {

    boolean returnVal = Boolean.getBoolean(U.getOption(pArgs, "--help", "false"));

    if (returnVal) {

      for (String arg : pArgs) {
        if (!arg.contains("--help"))
          System.err.print(arg + "\n");
      }
    }

    return returnVal;
  } // end method getOpt() ------------------------

  // ----------------------------------------------
  /**
   * getOpts returns nameValue pairs for command line args.
   * 
   * This is a sloppy way of parsing through posix like command line arguments
   * where the args start with --xxxx, and could have a value. For those args
   * that have values there will be an = delimiter. For instance --xxxx =
   * /path/to/something --zzzz=/path/to/something
   *
   * @param args the args
   * @return NameValue[]
   */
  // ----------------------------------------------
  public final static NameValue[] getOpts(String[] args) {

    NameValue nameValues[] = null;
    ArrayList<NameValue> nameValueList = new ArrayList<NameValue>();

    StringBuffer buff = null;
    if (args != null) {

      for (int i = 0; i < args.length; i++) {

        if (args[i].startsWith("--")) {
          // ---------------------------------------
          // store off the previous name value pair
          // ---------------------------------------
          if (buff != null) {
            nameValueList.add(getOptsAux(buff.toString()));
          }

          // ---------------------------------------
          // Start a new name value pair
          // ---------------------------------------
          buff = new StringBuffer();
          buff.append(args[i]);
        } else if (args[i].startsWith("=")) {
          if (buff == null)
            buff = new StringBuffer();
          buff.append(args[i]);
        } else {
          // ---------------------------------------
          // add to the buffer
          // --------------------------------------
          if (buff == null)
            buff = new StringBuffer();
          buff.append(args[i]);
        }

      } // end loop through args
      if (buff != null) {
        nameValueList.add(getOptsAux(buff.toString()));
      }

      if (nameValueList != null)
        nameValues = nameValueList.toArray(new NameValue[nameValueList.size()]);

    } // end if there are any args

    return nameValues;
  } // end Method getOpts() -----------------------

  // ----------------------------------------------
  /**
   * getOptsAux.
   *
   * @param pBuff the buff
   * @return NameValue
   */
  // ----------------------------------------------
  private static NameValue getOptsAux(String pBuff) {
    NameValue nameValue = null;
    String name = null;
    String value = null;
    int delimiterIndex = pBuff.indexOf('=');
    name = pBuff.substring(0, delimiterIndex).trim();
    if (delimiterIndex > 0 && delimiterIndex + 1 < pBuff.length())
      value = pBuff.substring(delimiterIndex + 1).trim();

    nameValue = new NameValue(name, value);

    return nameValue;
  } // end Method getOptsAux() --------------------

  // =======================================================
  /**
   * addArgs adds items to a command line args string array and returns a new
   * string array with the new item.
   *
   * @param pArgs the args
   * @param pKey the key
   * @param pValue the value
   * @return String[]
   */
  // =======================================================
  public final static String[] addArgs(String[] pArgs, String pKey, String pValue) {

    int newListSize = 1;
    String newList[] = null;

    if (pArgs == null) {
      newList = new String[1];

    } else {
      newListSize = pArgs.length + 1;
      newList = new String[newListSize];
      for (int i = 0; i < pArgs.length; i++)
        newList[i] = pArgs[i];
    }
    newList[newListSize - 1] = pKey + pValue;

    return newList;
  } // End Method addArgs() ======================

  // =======================================================
  /**
   * addArg adds items to a command line args string array and returns a new
   * string array with the new item. The argument is only added if one does not
   * already exist on the list. If the argument exists, the passed in value is
   * used
   * 
   * Note that the pKey has the "-- and =" delimiters on it for those key=value
   * parameters. An example of a pKey would be "--outputType=" It should not
   * include the "=" delimiter for those arguments that are binary like
   * "--debug" implying "--debug=true" kind of arguments.
   *
   * @param pArgs the args
   * @param pKey the key
   * @param pValue the value
   * @return String[]
   */
  // =======================================================
  public final static String[] addArg(String[] pArgs, String pKey, String pValue) {

    int newListSize = 1;
    String newList[] = null;

    if (pArgs == null) {
      newList = new String[1];
      newList[0] = pKey + pValue;

    } else {

      for (int i = 0; i < pArgs.length; i++ ) {
        String arg = pArgs[i];
        if (arg.startsWith(pKey)) {
          arg = pKey + pValue;
          pArgs[i] = arg;
          newList = pArgs;
          break;
        } else {
          newListSize = pArgs.length + 1;
          newList = new String[newListSize];
          for (int j = 0; j < pArgs.length; j++)
            newList[j] = pArgs[j];
          newList[newListSize - 1] = pKey + pValue;
        }
      }
    }

    return newList;
  } // End Method addArgs() ======================

  // =======================================================
  /**
   * addArgs adds two arrays of arguments together. A new string array is
   * returned. This does not check for duplicate entries.
   *
   * @param pArgs1 the args 1
   * @param pArgs2 the args 2
   * @return String[]
   */
  // =======================================================
  public final static String[] addArgs(String[] pArgs1, String[] pArgs2) {

    int n = 0;
    if (pArgs1 != null && pArgs1.length > 0)
      n = pArgs1.length;
    if (pArgs2 != null && pArgs2.length > 0)
      n = n + pArgs2.length;

    String[] returnVal = new String[n];
    if (pArgs1 != null && pArgs1.length > 0)
      for (int i = 0; i < pArgs1.length; i++)
        returnVal[i] = pArgs1[i];

    int k = pArgs1.length;
    if (pArgs2 != null && pArgs2.length > 0)
      for (int i = 0; i < pArgs2.length; i++)
        returnVal[k++] = pArgs2[i];

    return returnVal;
  } // End Method addArgs() ======================

  // -----------------------------------------
  /**
   * getPrintWriter opens a printWriter for this file or throws a runtime
   * exception trying.
   *
   * @param outputFileName the output file name
   * @return PrintWriter
   */
  // -----------------------------------------
  public final static PrintWriter getPrintWriter(String outputFileName) {
    PrintWriter out = null;
    try {
      out = new PrintWriter(outputFileName);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return out;
  } // end Method getPrintWriter() ------------

  // -----------------------------------------
  /**
   * getStackTrace returns the contents of the stack trace in a string.
   *
   * @param e the e
   * @return String
   */
  // -----------------------------------------
  public final static String getStackTrace(Exception e) {
    String returnTrace = null;
    StringBuffer buff = new StringBuffer();
    StackTraceElement[] pes = e.getStackTrace();
    for (StackTraceElement p : pes)
      buff.append(p.toString() + "\n");
    returnTrace = buff.toString();
    return returnTrace;
  } // End Method getStackTrace() -----------

  // ----------------------------------------------
  /**
   * hashKeysToList returns a List<String> of the keys.
   *
   * @param catHash the cat hash
   * @return int
   */
  // ----------------------------------------------
  public final static List<String> hashKeysToList(Map<String, int[]> catHash) {
    if (catHash == null) {
      return null;
    }
    return new ArrayList<String>(catHash.keySet());
  } // end Method hashKeysToList() ------------------

  // -------------------------------------------------------
  /**
   * HashToList returns a list of the items in the hash.
   *
   * @param subTerms the sub terms
   * @return List<>
   */
  // -------------------------------------------------------
  public final static List<? extends Object> HashToList(Hashtable<String, ?> subTerms) {
    if (subTerms == null) {
      return null;
    }

    return new ArrayList<Object>(subTerms.values());
  } // end Method HashToList() --------------------

  // -----------------------------------------
  /**
   * hasMultiplePeriods returns true if the number of periods in the string are
   * more than one.
   *
   * @param pValue the value
   * @return boolean
   */
  // -----------------------------------------
  public final static boolean hasMultiplePeriods(String pValue) {
    boolean returnValue = false;

    if (pValue == null) {
      return false;
    }

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

  // ----------------------------------
  /**
   * isAllCaps returns true if all the chars in the string are uppercase.
   * 
   * Note: Spaces, punctuation and numbers are now ignored.
   *
   * @param pValue the value
   * @return boolean true if all the chars are uppercase.
   */
  // ----------------------------------
  public final static boolean isAllCaps(String pValue) {
    boolean returnValue = true;
    
    if ( pValue == null )
      return false;
      
    
    char buff[] = pValue.toCharArray();
    for (int i = 0; i < buff.length; i++) {
      if ( buff[i] == ' ' || buff[i] == '\t')
        continue;
      if ( U.isPunctuation( buff[i] ))
        continue;
      if ( U.isNumber( buff[i] ))
        continue;
      
      if (buff[i] >= 'a' && buff[i] <= 'z') {
        returnValue = false;
        break;
      } // end if this char is not uppercase
    } // end loop through the chars of the buffer
    return returnValue;
  } // end method isAllCaps() ----------

  // =======================================================
  /**
   * isAllLowerCase returns true if the string is in all lower case (or
   * non-letter characters).  If the string is null, false is returned
   *
   * @param pValue the value
   * @return boolean
   */
  // =======================================================
  public final static boolean isAllLowerCase(String pValue) {
    boolean returnValue = true;
    
    if( pValue == null )
      return false;
    char buff[] = pValue.toCharArray();
    for (int i = 0; i < buff.length; i++) {
      if ( buff[i] == ' ' || buff[i] == '\t')
        continue;
      if ( U.isPunctuation( buff[i] ))
        continue;
      if ( U.isNumber( buff[i] ))
        continue;
      if (buff[i] >= 'A' && buff[i] <= 'Z') {
        returnValue = false;
        break;
      } // end if this char is not lowercase
    } // end loop through the chars of the buffer
    return returnValue;
  } // End Method isAllLowerCase() ======================

  // ----------------------------------
  /**
   * isInitialCap returns true if the first letter is uppercase and the rest are
   * lowercase.
   *
   * @param pValue the value
   * @return boolean true if the first letter is uppercase and the rest are
   *         lowercase.
   */
  // ----------------------------------
  public final static boolean isInitialCap(String pValue) {
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
   * isNumber returns true if this string has no letters in it.
   *
   * @param pString the string
   * @return boolean (true if this is a number )
   */
  // -----------------------------------------
  public final static boolean isNumber(String pString) {
    boolean returnValue = false;

    if (pString.trim().matches("-?\\d+(\\.\\d+)?"))
      returnValue = true;
    return returnValue;
  } // end Method isPunctuation() -----

  
  // -----------------------------------------
  /**
   * isRealNumber returns true if this string is a real number (it has one period in it)
   * assumes that this is a token with no spaces in it.
   *
   * @param pString the string
   * @return boolean (true if this is a number )
   */
  // -----------------------------------------
  public final static boolean isRealNumber(String pString) {
    boolean returnValue = false;

    int periodCtr = 0;
    if ( isNumber( pString )) {
      char[] buff = pString.toCharArray();
      for ( int i = 0; i < buff.length; i++) 
        if ( buff[i]== '.') {
          periodCtr++;
        if ( periodCtr >= 2 )
          break;
        }
      
    }
    if ( periodCtr == 1)
      returnValue = true;
   
    return returnValue;
  } // end Method isPunctuation() -----

  // =======================================================
  /**
   * isNumber returns true if this is a number.
   *
   * @param c the c
   * @return boolean
   */
  // =======================================================
  public final static boolean isNumber(char c) {

    boolean returnVal = false;
    if (c >= '0' && c <= '9')
      returnVal = true;
    return returnVal;
  } // End Method isNumber() ======================

  // -----------------------------------------
  /**
   * isOnlyPunctuation returns true if all the characters in this string are
   * punctuation or white space.
   *
   * @param pValue the value
   * @return boolean
   */
  // -----------------------------------------
  public final static boolean isOnlyPunctuation(String pValue) {
    boolean returnValue = true;

    if (pValue != null && pValue.length() > 0) {
      char[] chars = pValue.toCharArray();

      for (int i = 0; i < chars.length; i++) {
        
        if ( chars[i] == ' ' || chars[i] == '\t' )
          continue;
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
   * isPunctuation returns true if this character is a piece of punctuation.
   *
   * @param pChar the char
   * @return boolean (true if is punctuation)
   */
  // -----------------------------------------
  public final static boolean isPunctuation(char pChar) {
    boolean returnValue = false;

    if (pChar == '~' || pChar == '`' || pChar == '!' || pChar == '@' || pChar == '#' || pChar == '$'
        || pChar == '%' || pChar == '^' || pChar == '&' || pChar == '*' || pChar == '('
        || pChar == ')' || pChar == '-' || pChar == '_' || pChar == '=' || pChar == '+'
        || pChar == '[' || pChar == '{' || pChar == ']' || pChar == '}' || pChar == '\\'
        || pChar == '|' || pChar == ';' || pChar == ':' || pChar == '"' || pChar == '\''
        || pChar == ',' || pChar == '<' || pChar == '.' || pChar == '>' || pChar == '/'
        || pChar == '?')
      returnValue = true;

    // ---> does not work
    // `!@#$%^&\\*\\(\\)_\\-\\+={}\\[\\]\\|\\\\:;'<>\\?\\/,\\.\"";

    return returnValue;
  } // end Method isPunctuation() -----

  // -----------------------------------------
  /**
   * isWhiteSpace returns true if this character is whitespace.
   *
   * @param pChar the char
   * @return boolean (true if is whitespace
   */
  // -----------------------------------------
  public final static boolean isWhiteSpace(char pChar) {
    boolean returnValue = false;

    if (pChar <= ' ')
      returnValue = true;

    return returnValue;
  } // end Method isPunctuation() -----

  // -----------------------------------------
  /**
   * isSentenceBreak returns true if this character is a sentence breaking piece
   * of punctuation. Periods, question marks, exclamation marks are, semi-colons
   * and colons should be configurable and by default should be. [TBD]
   * 
   * added tab as a sentence break char
   *
   * @param pChar the char
   * @return boolean
   */
  // -----------------------------------------
  public final static boolean isSentenceBreakChar(char pChar) {
    boolean returnValue = false;

    if (pChar == ':' || 
        pChar == ';' || 
        pChar == '.' || 
        pChar == '!' || 
        pChar == '?' || 
        pChar == '\t')
      returnValue = true;

    return returnValue;
  } // end Method isSentnceBreakChar() ------
  
  // -----------------------------------------
  /**
   * isSentenceBreak returns true if this character is a sentence breaking piece
   * of punctuation. Periods, question marks, exclamation marks are, semi-colons
   * and colons should be configurable and by default should be. [TBD]
   * 
   * added tab as a sentence break char
   *
   * @param pToken
   * @return boolean
   */
  // -----------------------------------------
  public final static boolean isSentenceBreakChar(String pToken) {
    boolean returnValue = false;

    if ( pToken != null && !pToken.isEmpty() && pToken.trim().length() == 1) {
     
      char pChar = pToken.trim().charAt(0);
      returnValue = isSentenceBreakChar( pChar);
    }
    
  
    return returnValue;
  } // end Method isSentnceBreakChar() ------
  
    


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
  public final static boolean isWindows() {
    boolean returnCode = false;
    String osName = System.getProperty("os.name");

    if (osName.toLowerCase().indexOf("windows") > -1)
      returnCode = true;

    return (returnCode);
  } // end Method isWindows() ----------------

  // -----------------------------------------
  /**
   * normalize returns a string that can be represented on one line, without
   * special characters.
   *
   * @param coveredText the covered text
   * @return String
   */
  // -----------------------------------------
  public final static String normalize(String coveredText) {
    String returnValue = coveredText;
    returnValue = returnValue.replace('\n', ' ');
    returnValue = returnValue.replace('\r', ' ');

    return returnValue;
  } // end Method normalize() ----------------

  // =======================================================
  /**
   * extremeNormalize replaces any characters that would cause a problem with
   * making this a variable name spaces -> _ newlines, tabs removed slashs,
   * dashes, backslashes, brackets, parens -> _.
   *
   * @param word the word
   * @return String
   */
  // =======================================================
  public final static String extremeNormalize(String word) {

    String returnValue = word;
    returnValue = returnValue.replace('\n', '_');
    returnValue = returnValue.replace('\r', '_');
    returnValue = returnValue.replace(' ', '_');
    returnValue = returnValue.replace('=', '_');
    returnValue = returnValue.replace('-', '_');
    returnValue = returnValue.replace('@', '_');
    returnValue = returnValue.replace('%', '_');
    returnValue = returnValue.replace('!', '_');
    returnValue = returnValue.replace('#', '_');
    returnValue = returnValue.replace('^', '_');
    returnValue = returnValue.replace('&', '_');
    returnValue = returnValue.replace('*', '_');
    returnValue = returnValue.replace('(', '_');
    returnValue = returnValue.replace(')', '_');
    returnValue = returnValue.replace('+', '_');
    returnValue = returnValue.replace('{', '_');
    returnValue = returnValue.replace('}', '_');
    returnValue = returnValue.replace('[', '_');
    returnValue = returnValue.replace(']', '_');
    returnValue = returnValue.replace('|', '_');
    returnValue = returnValue.replace('\\', '_');
    returnValue = returnValue.replace(':', '_');
    returnValue = returnValue.replace(';', '_');
    returnValue = returnValue.replace('"', '_');
    returnValue = returnValue.replace('\'', '_');
    returnValue = returnValue.replace('`', '_');
    returnValue = returnValue.replace('<', '_');
    returnValue = returnValue.replace(',', '_');
    returnValue = returnValue.replace('>', '_');
    returnValue = returnValue.replace('.', '_');
    returnValue = returnValue.replace('/', '_');
    returnValue = returnValue.replace('0', 'X');
    returnValue = returnValue.replace('1', 'X');
    returnValue = returnValue.replace('2', 'X');
    returnValue = returnValue.replace('3', 'X');
    returnValue = returnValue.replace('4', 'X');
    returnValue = returnValue.replace('5', 'X');
    returnValue = returnValue.replace('6', 'X');
    returnValue = returnValue.replace('7', 'X');
    returnValue = returnValue.replace('8', 'X');
    returnValue = returnValue.replace('9', 'X');

    return returnValue;

  }// End Method extremeNormalize() ======================

  // =======================================================
  /**
   * extremeNormalize2 1. lowercases 2. replaces any characters that would cause
   * a problem with making this a variable name spaces -> _ newlines, tabs
   * removed slashs, dashes, backslashes, brackets, parens -> _ 3. replaces
   * number(s) with one X 4. replaces ',' with '_' so that csv delimiters work
   * 5. replaces '|' with '_' so pipe delimiters will work
   *
   * @param pWord the word
   * @return String
   */
  // =======================================================
  public final static String extremeNormalize2(String pWord) {

    String returnValue = null;
    if (pWord != null) {

      returnValue = extremeNormalize(pWord.toLowerCase());

      returnValue = returnValue.replace('0', 'X');
      returnValue = returnValue.replace('1', 'X');
      returnValue = returnValue.replace('2', 'X');
      returnValue = returnValue.replace('3', 'X');
      returnValue = returnValue.replace('4', 'X');
      returnValue = returnValue.replace('5', 'X');
      returnValue = returnValue.replace('6', 'X');
      returnValue = returnValue.replace('7', 'X');
      returnValue = returnValue.replace('8', 'X');
      returnValue = returnValue.replace('9', 'X');
      returnValue = returnValue.replace(',', '_');
      returnValue = returnValue.replace('|', '_');

      returnValue = returnValue.replace("XX", "X");

    }
    return returnValue;
  } // End method extremeNormalize2 () --------------

  // ----------------------------------------------
  /**
   * numberOf returns the number of time this pattern was seen in the given
   * pBuff.
   *
   * @param pBuff the buff
   * @param pPattern the pattern
   * @return int
   */
  // ----------------------------------------------
  public final static int numberOf(String pBuff, String pPattern) {
    int from = 0;
    int newFrom = 0;
    int returnVal = 0;
    while ((newFrom = pBuff.indexOf(pPattern, from)) > -1) {
      returnVal++;
      from = newFrom + 1;
    }

    return returnVal;
  } // end Method numberOf() --------------

  // -----------------------------------------
  /**
   * openStandardInput returns back the handle for stdin.
   *
   * @return BufferedReader
   */
  // -----------------------------------------
  public final static BufferedReader openStandardInput() {
    if (standardInput == null) {

      // ---------------------------------------
      // Open the standard input to get messages
      // ---------------------------------------
      try {
        standardInput = new BufferedReader(new InputStreamReader(System.in));
      } catch (RuntimeException e3) {
        GLog.println(GLog.ERROR_LEVEL, U.class, "openStandardInput",
            "Not able to open the standard input for reading");
      }
    }

    return (standardInput);

  } // end Method openStandardInput() -----------

  // -----------------------------------------
  /**
   * pad pads out the string to 10 places with 0's.
   *
   * @param pNumber the number
   * @return String
   */
  // -----------------------------------------
  public final static String pad(long pNumber) {
    String returnVal = String.format("%010d", pNumber);
    return returnVal;
  } // end Method pad() ---------------------------

  // -----------------------------------------
  /**
   * pad pads out the string to pNumberOfPlaces places with 0's.
   *
   * @param pNumber the number
   * @param pNumberOfPlaces the number of places
   * @return String
   */
  // -----------------------------------------
  public final static String zeroPad(long pNumber, int pNumberOfPlaces) {
    String format = "%0" + pNumberOfPlaces + "d";
    String returnVal = String.format(format, pNumber);
    return returnVal;
  } // end Method zeroPad() ---------------------------

  // ------------------------------------------
  /**
   * hr (horizontal rule) returns a string of X "-".
   *
   * @param x the x
   * @return String
   */
  // ------------------------------------------
  public final static String hr(int x) {

    StringBuffer buff = new StringBuffer();

    for (int i = 0; i < x; i++)
      buff.append("-");

    return buff.toString();

  } // End Method hr() -----------------------

  // -----------------------------------------
  /**
   * spacePadRight creates a string that is padded on the right by X spaces. For
   * instance a call to spacePadRight on the string xx would create a string
   * ->|xxx |<-
   * 
   * If the string is larger than the totalFieldLength, the whole string is
   * returned.
   *
   * @param pTotalFieldLength the total field length
   * @param pString the string
   * @return String
   */
  // -----------------------------------------
  public final static String spacePadRight(int pTotalFieldLength, String pString) {
    String returnVal = pString;

    StringBuffer b = new StringBuffer();
    if (pString != null)
      if (pString.length() < pTotalFieldLength) {
        for (int i = 0; i < pTotalFieldLength - pString.length(); i++)
          b.append(" ");

        returnVal = b.toString() + pString;
      } else {
        for (int i = 0; i < pTotalFieldLength; i++)
          b.append(pString.charAt(i));
        returnVal = b.toString();
      }

    return returnVal;
  } // end Method spacePadRight() -----------------

  // -----------------------------------------
  /**
   * spacePadLeft creates a string that is padded on the left by X spaces. For
   * instance a call to spacePadRight on the string xx would create a string ->|
   * xx|<-
   * 
   * If the string is larger than the totalFieldLength, the whole string is
   * returned.
   *
   * @param pTotalFieldLength the total field length
   * @param pString the string
   * @return String
   */
  // -----------------------------------------
  public final static String spacePadLeft(int pTotalFieldLength, String pString) {
    String returnVal = pString;

    StringBuffer b = new StringBuffer();
    if (pString != null)
      if (pString.length() < pTotalFieldLength) {
        for (int i = 0; i < pTotalFieldLength - pString.length(); i++)
          b.append(" ");

        returnVal = b.toString() + pString;
      } else {
        int i = pString.length() - 1;
        for (int j = 0; j < pTotalFieldLength; j++)
          b.append(pString.charAt(i--));
        returnVal = b.reverse().toString();
      }

    return returnVal;
  } // end Method spacePadRight() -----------------
  
  
//=================================================
/**
* dashLeftPad 
* 
* @param i
* @param string
* @return String
*/
//=================================================
public static final String dashLeftPad( int pTotalFieldLength, String pString) {
  String returnVal = pString;

  StringBuffer b = new StringBuffer();
  if (pString != null)
    if (pString.length() < pTotalFieldLength) {
      for (int i = 0; i < pTotalFieldLength - pString.length(); i++)
        b.append("-");

      returnVal = b.toString() + pString;
    } else {
      int i = pString.length() - 1;
      for (int j = 0; j < pTotalFieldLength; j++)
        b.append(pString.charAt(i--));
      returnVal = b.reverse().toString();
    }

  return returnVal;
} // end Method dashLeftPad() ---------------------

  // ----------------------------------------------
  /**
   * quote returns a string with quotes around it.
   *
   * @param pVal the val
   * @return String
   */
  // ----------------------------------------------
  public final static String quote(int pVal) {
    StringBuffer buff = new StringBuffer();
    buff.append('"');
    buff.append(pVal);
    buff.append('"');
    return buff.toString();
  } // end Method quote() -------------------------

  // ----------------------------------------------
  /**
   * quote returns a string with quotes around it. [Replaces doublequotes in the
   * text with single quotes]
   *
   * @param pVal the val
   * @return String
   */
  // ----------------------------------------------
  public final static String quote(String pVal) {
    StringBuffer buff = new StringBuffer();
    buff.append('"');
    if (pVal != null) {
      String val = pVal.replace('"', '\'');
      buff.append(val);
    }
    buff.append('"');
    return buff.toString();
  } // end Method quote() -------------------------

  // =================================================
  /**
   * getFileFromClassPathResource.
   *
   * @param pFileName the file name
   * @return File
   * @throws Exception the exception
   */
  // =================================================
  public final static File getFileFromClassPathResource(String pFileName) throws Exception {

    File returnVal = null;

    try {
      ClassLoader c = U.class.getClassLoader();
      URL aURL = c.getResource(pFileName);
      if (aURL != null)
        returnVal = new File(aURL.toURI());
    } catch (Exception e) {
      e.printStackTrace();
      String msg =
          "Issue trying to get the file " + pFileName + " from the classpath " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, msg);
      throw new Exception(msg);
    }
    return returnVal;
  } // end Method getFileFromClassPathResource() --

  // -----------------------------------------
  /**
   * readFile reads the content of a text file in the classpath into a string
   * and returns it. This method preserves the same kind of line delimiters as
   * the original file.
   *
   * @param pFileName the full path to the resource. ie = gov/va/myFile.text
   * @return String the contents of the file.
   * @throws Exception the exception
   */
  // -----------------------------------------
  public final static String readClassPathResource(String pFileName) throws Exception {

    String returnValue = null;

    try (final InputStream stream = U.class.getClassLoader().getResourceAsStream(pFileName);) {

      if (stream == null) {
        throw new Exception("file : " + pFileName + " was not found as a resource");
      }

      StringBuilder buff = new StringBuilder();
      try (final BufferedReader in = new BufferedReader(new InputStreamReader(stream));) {

        int buffSize = 8192;
        char[] buffer = new char[buffSize];
        int read = -1;

        while ((read = in.read(buffer, 0, buffer.length)) > 0)
          buff.append(buffer, 0, read);
        returnValue = buff.toString();

        return returnValue;
      }
    }
  } // end Method readClassPathResource() ---------------

  // ------------------------------------------
  /**
   * readClassPathResourceIntoStringArray reads the contents of a file into a
   * string array, one per line.
   *
   * @param pResourceFileName the resource file name
   * @return String[]
   * @throws Exception the exception
   */
  // ------------------------------------------
  public final static String[] readClassPathResourceIntoStringArray(String pResourceFileName)
    throws Exception {

    try (final BufferedReader in = getClassPathResource(pResourceFileName);) {

      String row = null;

      ArrayList<String> buff = new ArrayList<String>();
      if (in != null) {
        while ((row = in.readLine()) != null) {
          buff.add(row);
        }
      }
      return buff.toArray(new String[buff.size()]);
    }

  } // End Method readClassPathResourceIntoStringArray() -----------------------

  // ------------------------------------------
  /**
   * readClassPathResourceIntoString reads the contents of a file into a string.
   *
   * @param pResourceFileName the resource file name
   * @return String
   * @throws Exception the exception
   */
  // ------------------------------------------
  public final static String readClassPathResourceIntoString(String pResourceFileName)
    throws Exception {

    StringBuffer buff = new StringBuffer();
    try (BufferedReader in = getClassPathResource(pResourceFileName);) {

      String row = null;

      while ((row = in.readLine()) != null) {
        buff.append(row + '\n');
      }

      return buff.toString();
    }

  } // End Method readClassPathResourceIntoString() -----------------------

  // -----------------------------------------
  /**
   * readFile reads the content of a text file into a string and returns it.
   * This method opens the file as utf8 files. This method preserves the same
   * kind of line delimiters as the original file.
   *
   * @param pFileName the file name
   * @return String
   * @throws Exception the exception
   */
  // -----------------------------------------
  public final static String readFile(String pFileName) throws Exception {

    return readFile(pFileName, "UTF8");

  } // end Method readFile() -----------------

  // ------------------------------------------
  /**
   * readFile.
   *
   * @param pFile the file
   * @param pCharSet the char set
   * @return String
   * @throws IOException Signals that an I/O exception has occurred.
   */
  // ------------------------------------------
  public final static String readFile(File pFile, String pCharSet) throws IOException {
    StringBuilder buff = new StringBuilder();
    try (final BufferedReader in =
        new BufferedReader(new InputStreamReader(new FileInputStream(pFile), pCharSet));) {

      int buffSize = 8192;
      char[] buffer = new char[buffSize];
      int read = -1;

      while ((read = in.read(buffer, 0, buffer.length)) > 0) {
        replaceEOF(buffer);
        buff.append(buffer, 0, read);
      }

      return buff.toString();
    }
    // End Method readFile() -----------------------
  }

  // =======================================================
  /**
   * replaceEOF replaces any EOF characters (char)26 or ^Z with a space.
   *
   * @param buffer the buffer
   * @return char[]
   */
  // =======================================================
  public final static char[] replaceEOF(char[] buffer) {

    if (buffer != null && buffer.length > 0)
      for (int i = 0; i < buffer.length; i++)
        if (buffer[i] == EOF_CHAR)
          buffer[i] = ' ';

    return buffer;
  } // End Method stripEOF() ======================

  // ------------------------------------------
  /**
   * readFile.
   *
   * @param pFile the file
   * @return String
   * @throws IOException Signals that an I/O exception has occurred.
   */
  // ------------------------------------------
  public final static String readFile(File pFile) throws IOException {

    return readFile(pFile, "UTF8");

    // End Method readFile() -----------------------
  }

  // =================================================
  /**
   * readFromStream consumes the whole stream, puts the content into the
   * returned string, and closes the inputStream.
   *
   * @param inputStream @return String @exception
   * @return the string
   * @throws Exception the exception
   */
  // =================================================
  public final static String readFromStream(InputStream inputStream) throws Exception {
    StringBuffer buff = new StringBuffer();
    String returnVal = null;
    try (final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));) {
      String line = null;
      while ((line = in.readLine()) != null) {
        buff.append(line);
        buff.append("\n");
      }
      returnVal = buff.toString();

      return returnVal;
    }

  } // End method readFromStream() ------------------

  // -----------------------------------------
  /**
   * readFileIntoStringArray reads a file into a string array, making traversal
   * thru lines clean.
   *
   * @param pFileName the file name
   * @return String[] one for each line
   * @throws Exception the exception
   */
  // -----------------------------------------
  public final static String[] readFileIntoStringArray(String pFileName) throws Exception {
    String[] returnVal = null;

    String buff = readFile(pFileName);

    buff = buff.replace('\r',  ' ');
    returnVal = U.split(buff, "\n");
    
    

    return returnVal;
  } // end Method readFileIntoStringArray() -------

  // -----------------------------------------
  /**
   * readFile reads the content of a text file into a string and returns it.
   * This method preserves the same kind of line delimiters as the original
   * file.
   *
   * @param pFileName the file name
   * @param pCharSet the char set
   * @return String
   * @throws Exception the exception
   */
  // -----------------------------------------
  public final static String readFile(String pFileName, String pCharSet) throws Exception {

    File aFile = new File(pFileName);
    String returnValue = readFile(aFile, pCharSet);

    return returnValue;
  } // end Method readFile() -----------------

  // -----------------------------------------
  /**
   * deleteFile deletes a physical file (if it exists).
   *
   * @param pFileName the file name
   * @throws Exception the exception
   */
  // -----------------------------------------
  public final static void deleteFile(String pFileName) throws Exception {

    File aFile = new File(pFileName);
    if (aFile.exists()) {
      aFile.delete();
    }

  } // end Method deleteFile() --------------------

  // ------------------------------------------
  /**
   * deleteDirectory deletes the contents of a directory and the directory
   * itself.
   *
   * @param folder the folder
   * @throws Exception the exception
   */
  // ------------------------------------------
  public final static void deleteDirectory(File folder) throws Exception {
    File[] files = folder.listFiles();
    if (files != null) { // some JVMs return null for empty dirs
      for (File f : files) {
        if (f.isDirectory()) {
          deleteDirectory(f);
        } else {
          f.delete();
        }
      }
    }
    folder.delete();

  } // End Method deleteDirectory() -----------------------

  // ------------------------------------------
  /**
   * mkDir creates a directory and any paths to that dir.
   *
   * @param pDir the dir
   * @throws Exception the exception
   */
  // ------------------------------------------
  public final static void mkDir(String pDir) throws Exception {

    File adir = new File(pDir);
    if (!adir.exists())
      adir.mkdirs();

  } // End Method mkDir() -----------------------

  // ------------------------------------------
  /**
   * returns the path to the temp dir.
   *
   * @return the tmp dir
   */
  // ------------------------------------------
  public final static String getTmpDir() {
    return "/temp";

  } // End Method getTmpDir() -----------------------

  // ------------------------------------------
  /**
   * returns the path to the temp dir.
   *
   * @return the string
   * @throws Exception the exception
   */
  // ------------------------------------------
  public final static String createTmpDir() throws Exception {

    String tmpDir = "/temp";
    Path tempDir = Files.createTempDirectory(tmpDir);
    tmpDir = tempDir.toFile().getAbsolutePath();

    return tmpDir;
  } // End Method getTmpDir() -----------------------

  // -----------------------------------------
  /**
   * removeHyphen returns a string without the hyphen. For example, pin-hole
   * becomes pinhole.
   *
   * @param pString the string
   * @return String
   */
  // -----------------------------------------
  public final static String removeHyphen(String pString) {

    if (pString == null) {
      return null;
    }

    return pString.replaceAll("-", "");

  } // end Method removeHyphen

  // -----------------------------------------
  /**
   * run executes (almost) everything that can be run from a commandline.
   *
   * @param pCommand the command
   * @return String (Anything that comes back from the sent command)
   */
  // -----------------------------------------
  public final static String run(String pCommand) {
    return run(pCommand, null);

  } // End Method run() ----------------------

  // -----------------------------------------
  /**
   * run executes (almost) everything that can be run from a commandline.
   *
   * @param pCommand the command
   * @param pEnvironmentVariables the environment variables
   * @return String (Anything that comes back from the sent command)
   */
  // -----------------------------------------
  public final static String run(String pCommand, String[] pEnvironmentVariables) {
    Process p = null;
    StringBuffer buff = new StringBuffer();
    StringBuffer errorBuff = new StringBuffer();
    String[] environmentVariables = pEnvironmentVariables;

    try {
      if (runtime == null)
        runtime = Runtime.getRuntime();

      if (runtime != null) {
        if (environmentVariables != null && environmentVariables.length > 0)
          p = runtime.exec(pCommand.trim(), environmentVariables);
        else
          p = runtime.exec(pCommand.trim());

        try (final BufferedReader outputStream =
            new BufferedReader(new InputStreamReader(p.getInputStream()));) {

          String line = null;
          while ((line = outputStream.readLine()) != null) {
            buff.append(line);
            buff.append(U.NL);
          }

          try (final BufferedReader errorStream =
              new BufferedReader(new InputStreamReader(p.getErrorStream()));) {

            line = null;
            while ((line = errorStream.readLine()) != null) {
              errorBuff.append(line);
              errorBuff.append(U.NL);
              buff.append(line);
              buff.append(U.NL);
            }
          }
          // ------------------------------------------------------
          // I descided to have the error put on the string instead
          // rather than spew out an error
          // ------------------------------------------------------
          // if ( errorBuff.length() > 0 ) {
          // System.err.println("? Error: " + errorBuff );
          // }
        }
      } else {
        System.err.print("runtime is null, aborting...");
        System.exit(0);
      }
    } catch (IOException e2) {
      GLog.println(GLog.ERROR_LEVEL, U.class, "run",
          "Problem with getting output from the command " + U.NL + pCommand + U.NL + e2.toString());
      System.err.flush();
    } catch (RuntimeException e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, U.class, "run",
          "Problem running the command |" + pCommand + "| " + e.getMessage());

      System.err.flush();

    }
    if (p != null)
      p.destroy();
    p = null;

    return (buff.toString());

  } // end Method Run() ---------------------

  // -----------------------------------------
  /**
   * runP executes (almost) everything that can be run from a commandline.
   *
   * @param pCommand the command
   * @param pEnvironmentVariables the environment variables
   * @return Process
   */
  // -----------------------------------------
  public final static Process runP(String pCommand, String[] pEnvironmentVariables) {
    Process p = null;

    String[] environmentVariables = pEnvironmentVariables;

    try {
      if (runtime == null)
        runtime = Runtime.getRuntime();

      if (runtime != null) {
        if (environmentVariables != null && environmentVariables.length > 0)
          p = runtime.exec(pCommand.trim(), environmentVariables);
        else
          p = runtime.exec(pCommand.trim());

      }
    } catch (IOException e2) {
      e2.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, U.class, "runP",
          "Problem with getting output from the command " + e2.toString());

    } catch (RuntimeException e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, U.class, "runP",
          "Problem running the command |" + pCommand + "| " + e.toString());

    }

    return p;
  } // end Method runP() ------------------------

  // -----------------------------------------
  /**
   * listen returns the output from the process running.
   *
   * @param p the p
   * @return Process
   * @throws IOException Signals that an I/O exception has occurred.
   */
  // -----------------------------------------
  public final static String listen(Process p) throws IOException {

    StringBuffer buff = new StringBuffer();
    StringBuffer errorBuff = new StringBuffer();
    try (final BufferedReader outputStream =
        new BufferedReader(new InputStreamReader(p.getInputStream()));) {

      String line = null;
      while ((line = outputStream.readLine()) != null) {
        buff.append(line);
        buff.append(U.NL);
      }
    }

    try (final BufferedReader errorStream =
        new BufferedReader(new InputStreamReader(p.getErrorStream()));) {

      String line = null;
      while ((line = errorStream.readLine()) != null) {
        errorBuff.append(line);
        errorBuff.append(U.NL);
        buff.append(line);
        buff.append(U.NL);
      }
    }
    // ------------------------------------------------------
    // I descided to have the error put on the string instead
    // rather than spew out an error
    // ------------------------------------------------------
    // if ( errorBuff.length() > 0 ) {
    // System.err.println("? Error: " + errorBuff );
    // }

    p.destroy();
    // p = null;

    return (buff.toString());

  } // end Method Run() ---------------------

  // ----------------------------------------------
  /**
   * split Given a pipe delimited string, return the contents of the fields, one
   * per string. This method handles null fields. If the last field is empty,
   * ie. x|x| the last field will be null.
   *
   * @param pRow the row
   * @return String[]
   */
  // ----------------------------------------------
  public final static String[] split(String pRow) {

    return splitBetter(pRow);

  } // end Method split() -------------------------

  // =======================================================
  /**
   * split returns a string[] with fields where the fields are delimited by the
   * delimiter. The delimiter can be multiple characters long.
   *
   * @param pBuff the buff
   * @param pDelimiter the delimiter
   * @return String []
   */
  // ======================================================
  public final static String[] split_StringDelimited(String pBuff, String pDelimiter) {

    String returnVal[] = null;

    try {
      int next = 0;
      int from = 0;
      ArrayList<String> buff = new ArrayList<String>();
      while (from < pBuff.length() && next > -1 && (next = pBuff.indexOf(pDelimiter, from)) > -1) {
        String field = pBuff.substring(from, next);
        buff.add(field);
        from = next + pDelimiter.length();
      }

      // catch the last field
      String field = pBuff.substring(from);
      buff.add(field);

      if (buff != null && !buff.isEmpty())
        returnVal = buff.toArray(new String[buff.size()]);

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, U.class, "split",
          "Issue within method split " + e.getMessage());
      throw e;
    }

    return returnVal;
  } // End Method split ============
  // =======================================================

  /**
   * getNumberOfEmptyFields returns how many instances of || there are.
   *
   * @param pBuff the buff
   * @return int
   */
  // =======================================================
  @SuppressWarnings("unused")
  private static int getNumberOfEmptyFields(String pBuff) {

    int numberOfInstances = 0;
    int p = 0;
    int k = 0;
    while ((k = pBuff.indexOf("||", p)) > 0) {
      if (k == p)
        break;
      numberOfInstances++;
      p = k;
    }
    if (pBuff.lastIndexOf('|') == pBuff.length() - 1)
      numberOfInstances++;

    return numberOfInstances;
    // End Method getNumberOfEmptyFields() ======================
  }

//----------------------------------------------
 /**
  * split2 Given a  delimited string, return the contents of the fields, one
  * per string.  The delimiter can be more than one character.
  *
  * @param pRow the row
  * @param pDelimiter the delimiter
  * @return String[]
  */
 // ----------------------------------------------
  public final static String[] split2(String pRow, String pDelimiter) {

    String[] returnValue = null;
    List<String>buff = new ArrayList<String>();
    
    int fromIndex = 0;
    int next = -1 ;
    String val = null;
    
    while ( (next = pRow.indexOf( pDelimiter, fromIndex )) > 0 ) {
      val = pRow.substring(fromIndex, next );
      buff.add ( val );
      
      fromIndex = next + pDelimiter.length();
    }
    if ( buff != null && !buff.isEmpty())
      returnValue = buff.toArray( new String[ buff.size() ]);
    
    
    return returnValue;
  
  } // end Method split2() ------------------------------------------
  
  // ----------------------------------------------
  /**
   * split Given a pipe delimited string, return the contents of the fields, one
   * per string. This method handles null fields.
   *
   * @param pRow the row
   * @param pDelimiter the delimiter
   * @return String[]
   */
  // ----------------------------------------------
  public final static String[] split(String pRow, String pDelimiter) {

    String[] returnValue = null;
    String buff = null;
    String delimiter = pDelimiter;
    if (pDelimiter.equals("|"))
      delimiter = "\\|"; // <--- needed to be backslashed because pipe is a
                         // special char in re.
    if (pDelimiter.equals("("))
      delimiter = "\\("; // <--- needed to be backslashed because ( is a special
                         // char in re.
    if (pDelimiter.equals(")"))
      delimiter = "\\)"; // <--- needed to be backslashed because ) is a special
                         // char in re.
    if (pDelimiter.equals("-"))
      delimiter = "\\-"; // <--- needed to be backslashed because - is a special
                         // char in re.
    if (pDelimiter.equals(U.NL)) 
      returnValue = pRow.split("\\r?\\n");
   
    if (pDelimiter.equals("\n")) {
        returnValue = pRow.split("\n");

    } else {
      // if ( pDelimiter.equals(",")) delimiter = "\\,"; // <--- needed to be
      // backslashed because - is a special char in re.
      int i = 0;

      int maxFields = 0;
      if ((pRow != null) && (pRow.trim().length() > 0)) {

        StringTokenizer st = new StringTokenizer(pRow, delimiter, true);
        ArrayList<String> val = new ArrayList<String>(st.countTokens());
        while (st.hasMoreElements()) {

          buff = st.nextToken();
          if (buff.compareTo(pDelimiter) == 0) {
            maxFields++;
            i++;
            if (i == 2) {
              val.add(new String(""));
              --i;
            }

          } else {
            if (maxFields < i)
              maxFields = i;
            val.add(new String(buff));
            i = 0;
          }
        }
        // 2 < 2 +1 = val should = 3
        // 3
        // if ( val.size() < maxFields +1)
        // maxFields = val.size() + 1 ;
        st = null;
        buff = null;
        val.trimToSize();

        returnValue = new String[maxFields + 1];
        for (int k = 0; k < maxFields + 1; k++)
          if (k < val.size())
            returnValue[k] = val.get(k);
          else
            returnValue[k] = "";
      }
    }
    return returnValue;
  } // end Method split() -------------------------

  // =======================================================
  /**
   * split splits a row into a String[] of number of rows This method is more
   * efficient because it knows how many col's to create, and has a cut - to
   * stop reading after the pCut number of fields.
   *
   * @param pRow the row
   * @param pCols the cols
   * @param pCut the cut
   * @return String[]
   */
  // =======================================================
  public final static String[] split(String pRow, int pCols, int pCut) {

    char[] cs = pRow.toCharArray();

    String[] cols = new String[pCols];
    int currentCol = 0;
    int buffSize = 0;
    char[] buff = new char[cs.length];
    for (int i = 0; i < cs.length; i++) {

      if (cs[i] != '|') {
        buff[buffSize] = cs[i];
        buffSize++;
      } else {
        char[] trimmedBuff = new char[buffSize];
        for (int j = 0; j < buffSize; j++)
          trimmedBuff[j] = buff[j];
        cols[currentCol] = new String(trimmedBuff);
        buffSize = 0;
        currentCol++;
      }

      if (currentCol >= pCut)
        break;

    } // end of loop

    // if you have not run past the cut point, add the buff to the next column
    if (currentCol < pCut) {

      char[] trimmedBuff = new char[buffSize];
      for (int j = 0; j < buffSize; j++)
        trimmedBuff[j] = buff[j];
      cols[currentCol] = new String(trimmedBuff);
    }

    return cols;
  } // End Method split() ======================

  // =======================================================
  /**
   * split splits a row into a String[] of number of rows This method is more
   * efficient. This method uses a pipe as the field delimiter
   *
   * @param pRow the row
   * @return String[]
   */
  // =======================================================
  public final static String[] splitBetter(String pRow) {
    return splitBetter(pRow, '|');

  }

  // =======================================================
  /**
   * split splits a row into a String[] of number of rows This method is more
   * efficient.
   *
   * @param pRow the row
   * @param pSplitChar the split char
   * @return String[]
   */
  // =======================================================
  public final static String[] splitBetter(String pRow, char pSplitChar) {

    char[] cs = pRow.toCharArray();
    int numberOfCols = getNumberOfCols(cs, pSplitChar);

    String[] cols = new String[numberOfCols];
    int currentCol = 0;
    int buffSize = 0;
    char[] buff = new char[cs.length];
    for (int i = 0; i < cs.length; i++) {

      if (cs[i] != pSplitChar) {
        buff[buffSize] = cs[i];
        buffSize++;
      } else {
        char[] trimmedBuff = new char[buffSize];
        for (int j = 0; j < buffSize; j++)
          trimmedBuff[j] = buff[j];
        cols[currentCol] = new String(trimmedBuff);
        buffSize = 0;
        currentCol++;
      }

    } // end of loop

    // add the last buff to the next column

    char[] trimmedBuff = new char[buffSize];
    for (int j = 0; j < buffSize; j++)
      trimmedBuff[j] = buff[j];
    cols[currentCol] = new String(trimmedBuff);

    return cols;
  } // End Method split() ======================

  // =======================================================
  /**
   * getNumberOfCols returns the number of columns in this row (That's the
   * number of splitChars seen + 1.
   *
   * @param pRow the row
   * @param splitChar the split char
   * @return int
   */
  // =======================================================
  public final static int getNumberOfCols(char[] pRow, char splitChar) {

    int cols = 0;
    for (int i = 0; i < pRow.length; i++)
      if (pRow[i] == splitChar)
        cols++;
    return cols + 1;
  } // End Method getNumberOfCols() ======================

  // -----------------------------------------
  /**
   * split2List returns a List<String> from a string that is pipe delimited
   * 
   * if string is null, this returns an empty list.
   *
   * @param pRow the row
   * @return List<String>
   */
  // -----------------------------------------
  public final static List<String> split2List(String pRow) {

    if (pRow == null) {
      return null;
    }

    String[] buff = U.split(pRow, "|");
    return new ArrayList<String>(Arrays.asList(buff));
  } // end Method split2List() --------------------
    // =======================================================

  /**
   * splitOnLines splits on lines regardless if they are \r\n or \n.
   *
   * @param pBuff the buff
   * @return String[]
   */
  // =======================================================
  public final static String[] splitOnLines(String pBuff) {

    String returnVal[] = null;
    String splitChar = U.NL;
    if (pBuff != null && pBuff.length() > 0) {
      if (pBuff.indexOf('\r') > -1)
        splitChar = "\r";
      returnVal = U.split(splitChar);
    }
    if (returnVal == null && pBuff != null) {
      returnVal = new String[1];
      returnVal[0] = pBuff;
    }

    return returnVal;
  } // End Method splitOnLines() ======================

  // =======================================================
  /**
   * getValueFromRow retrieves the value from a specific field.
   *
   * @param pRow the row
   * @param pField the field
   * @return String
   */
  // =======================================================
  public final static String getValueFromRow(String pRow, int pField) {
    String returnVal = "";

    String[] cols = U.splitBetter(pRow);
    if (cols != null && cols.length > pField)
      returnVal = cols[pField];

    return returnVal;
  }// End Method getValueFromRow() ======================

  // =======================================================
  /**
   * getValueFromSlotValue returns the right side of the equation xxx=yyyyy.
   *
   * @param pBuff the buff
   * @return String
   * @throws Exception the exception
   */
  // ======================================================
  public final static String getValueFromSlotValue(String pBuff) throws Exception {

    String returnVal = null;
    try {
      int i = pBuff.indexOf("=");
      if (i > -1) {
        returnVal = pBuff.substring(i + 1).trim();
      }

    } catch (Exception e1) {
      e1.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, U.class, "getValueFromSlotValue",
          "Issue within method getValueFromSlotValue " + e1.getMessage());
      throw e1;
    }
    return returnVal;
  } // End Method getValueFromSlotValue ============

  // =======================================================
  /**
   * getIntValueFromSlotValue returns the right side of the equation xxx=yyyyy.
   *
   * @param pBuff the buff
   * @return int -1 if it messed up
   * @throws Exception the exception
   */
  // ======================================================
  public final static int getIntValueFromSlotValue(String pBuff) throws Exception {

    int returnVal = -1;
    try {
      String buff = pBuff;
      if (buff != null) {
        buff = buff.replace("\"", "");
        buff = buff.replace("'", "");
        buff = getValueFromSlotValue(buff);
        if (buff != null) {

          returnVal = Integer.parseInt(buff.trim());
        }
      }
    } catch (Exception e1) {
      e1.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, U.class, "getIntValueFromSlotValue",
          "Issue within method getValueFromSlotValue " + e1.getMessage());
      throw e1;
    }
    return returnVal;
  } // End Method getIntValueFromSlotValue ============

  // -----------------------------------------
  /**
   * array2String returns a string that is delimeter delimited of the elements
   * from the stringArray passed in. If the elements are empty or null, a ""
   * string is passed back.
   *
   * @param elements the elements
   * @param delimeter the delimeter
   * @return String
   */
  // -----------------------------------------
  public final static String stringArray2String(String[] elements, String delimeter) {
    StringBuffer buff = new StringBuffer();
    String returnVal = "";
    if (elements != null)
      for (int i = 0; i < elements.length; i++) {
        buff.append(elements[i]);
        if (i < elements.length - 1)
          buff.append(delimeter);
      }
    returnVal = buff.toString();
    return returnVal;
  } // end stringArray2String

  /**
   * List 2 string.
   *
   * @param elements the elements
   * @param delimeter the delimeter
   * @return the string
   */
  public final static String list2String(List<String> elements, char delimeter) {
    StringBuffer buff = new StringBuffer();
    String returnVal = "";
    if (elements != null)
      for (int i = 0; i < elements.size(); i++) {
        buff.append(elements.get(i));
        if (i < elements.size() - 1)
          buff.append(delimeter);
      }
    returnVal = buff.toString();
    return returnVal;

  }

  // ------------------------------------------
  /**
   * stringToBoolean converts
   * "true|True|false|False|Yes|yes|No|no|T|t|F|f|Y|y|n|N|0|1" to a boolean
   * value N.B. 1 = true here. N.B.B. actually, any value other than
   * true|Yes|Y|1 will return false.
   *
   * @param pBuff the buff
   * @return boolean
   */
  // ------------------------------------------
  public final static boolean stringToBoolean(String pBuff) {

    boolean returnValue = false;
    if (pBuff != null) {
      String buff = pBuff.toLowerCase();

      if (buff.equals("true") || buff.equals("t") || buff.equals("yes") || buff.equals("y")
          || buff.equals("1"))
        returnValue = true;
    }

    return returnValue;

  } // End Method stringToBoolean() -----------------------

  // ----------------------------------------------
  /**
   * toString returns a String of the elements of a List delimeted by newlines
   * and indented.
   *
   * @param pList the list
   * @return String
   */
  // ----------------------------------------------

  public final static String toString(List<?> pList) {
    String returnValue = null;

    if (pList != null) {
      StringBuffer buff = new StringBuffer();

      Iterator<?> i = pList.iterator();
      while (i.hasNext()) {
        buff.append("     ");
        buff.append(i.next().toString());
        buff.append(U.NL);
      } // end loop
      returnValue = buff.toString();
    }
    return returnValue;
  } // end Method toString( List)
  // ----------------------------------------------

  /**
   * unquote remove the sorrounding quotes from a string (both single and double
   * quotes).
   *
   * @param pString the string
   * @return String
   */
  // ----------------------------------------------
  public final static String unquote(String pString) {
    String buff = pString;
    if ((pString != null) && (pString.length() > 1)
        && (pString.charAt(0) == '"' || pString.charAt(0) == '\'')) {
      if (pString.charAt(pString.length() - 1) == '"'
          || pString.charAt(pString.length() - 1) == '\'')
        buff = pString.substring(1, pString.length() - 1);
    }

    return buff;
  } // end Method unquote() -----------------------

  // =================================================
  /**
   * enumerizeNorm removes punctuation, camelCases multi-word terms.
   *
   * @param pTerm the term
   * @return String
   */
  // =================================================
  public final static String enumerizeNorm(String pTerm) {

    String returnVal = pTerm;

    if (pTerm != null && !pTerm.isEmpty()) {

      // replace punctuation with spaces
      returnVal = returnVal.replaceAll("[^a-zA-Z0-9]", " ");

      if (returnVal != null && returnVal.trim().length() > 0) {

        String[] words = U.split(returnVal, " ");
        StringBuffer buff = new StringBuffer();
        if (words.length > 1) {
          buff.append(words[0].toLowerCase());

          for (int i = 1; i < words.length; i++)
            buff.append(upCaseWord(words[i]));

          returnVal = buff.toString();
        } // end if there is more than one word
      } // end if there is anything left after replacing puncutation
    } // end if the term is empty

    return returnVal;
  } // end enumerizeNorm() --------------------------

  // =================================================
  /**
   * UpCaseWord lowercases the word, then uppercases the first letter of the
   * word.
   *
   * @param pWord the word
   * @return String
   */
  // =================================================
  public final static String upCaseWord(String pWord) {
    String returnVal = pWord;

    if (returnVal != null && !returnVal.isEmpty() && returnVal.trim().length() > 0) {
      returnVal = returnVal.toLowerCase();
      char[] chars = returnVal.toCharArray();
      chars[0] = Character.toUpperCase(chars[0]);
      returnVal = new String(chars);
    }
    return returnVal;
  } // end Method upCaseword() -----------------------

  // -----------------------------------------
  /**
   * replaceLast replaces the last pattern with newPattern within the pString.
   *
   * @param pattern the pattern
   * @param newPattern the new pattern
   * @param pString the string
   * @return String
   */
  // -----------------------------------------
  public final static String replaceLast(String pattern, String newPattern, String pString) {

    String returnValue = pString;
    if (pString != null) {
      Pattern pat = Pattern.compile(pattern);
      if (pat != null) {
        Matcher z = pat.matcher(pString);
        if (z != null) {
          int start = -1;
          int eend = -1;
          while (z.find()) {
            start = z.start();
            eend = z.end();
          }
          if (start > -1) {
            String a = returnValue.substring(0, start);
            String b = newPattern;
            String c = returnValue.substring(eend);
            returnValue = a + b + c;
          }
        }
      }
    }

    return returnValue;
  } // end Method replaceLast

  // ------------------------------------------
  /**
   * getFileExtension retrieves file extensions, the last string after the the
   * last period of the filename.
   * 
   * What gets returned is the file extension with the leading period.
   * 
   * If there are no file extensions, nothing is returned.
   *
   * @param candidateFile the candidate file
   * @return String
   */
  // ------------------------------------------
  public final static String getFileExtension(String candidateFile) {

    String extension = null;
    int p = candidateFile.lastIndexOf('.');
    if (p > -1)
      extension = candidateFile.substring(p);
    return extension;

  } // End Method getFileExtension() -----------------------

  // ------------------------------------------
  /**
   * write writes out the contents to a file.
   *
   * @param fileName the file name
   * @param pText the text
   * @param backup the backup
   * @throws Exception the exception
   */
  // ------------------------------------------
  public final static void write(String fileName, String pText, boolean backup) throws Exception {

    if (backup) {
      String origText = U.readFile(fileName);
      try (final PrintWriter bOut = new PrintWriter(fileName + ".bak");) {
        bOut.println(origText);
      }
    }

    try (final PrintWriter out = new PrintWriter(fileName);) {
      out.println(pText);
    }

    // End Method write() -----------------------
  }

  // ------------------------------------------
  /**
   * errorMsg writes out a formatted error message and stack trace to a log and
   * to standard error.
   *
   * @param pLog the log
   * @param eTrace the e trace
   * @param pE the p E
   * @param pMsg the msg
   */
  // ------------------------------------------
  public final static void errorMsg(PrintWriter pLog, String eTrace, Exception pE, String pMsg) {

    String msg = U.HR + U.NL + pMsg + U.NL + eTrace + U.NL + pE.toString() + U.HR;
    pLog.println(msg);
    pLog.flush();
    System.err.println(msg);

    // End Method errorMsg() -----------------------
  }

  // ------------------------------------------
  /**
   * errorMsg writes out a formatted error message and stack trace to standard
   * error.
   *
   * @param pE the p E
   * @param pMsg the msg
   */
  // ------------------------------------------
  public final static void errorMsg(Exception pE, String pMsg) {
    String eTrace = U.getStackTrace(pE);
    String msg = U.HR + U.NL + pMsg + U.NL + eTrace + U.NL + pE.toString() + U.HR;
    System.err.println(msg);

  } // End Method errorMsg() -----------------------

  // ------------------------------------------
  /**
   * getPID returns the pid of the cmd pattern passed in.
   *
   * @param pCmd the cmd
   * @param proc the proc
   * @return String "none" if not found
   */
  // ------------------------------------------
  public final static String getPID(String pCmd, Process proc) {

    String returnVal = "none";
    if (U.isWindows())
      returnVal = getWindowsPID(pCmd);
    else
      returnVal = getUnixPID(proc);
    return returnVal;
  } // End Method getPID() -----------------------

  // ------------------------------------------
  /**
   * getUnixPID.
   *
   * @param pProc the proc
   * @return String "none" if not found"
   */
  // ------------------------------------------
  public final static String getUnixPID(Process pProc) {
    String returnVal = "none";
    try {
      if (pProc.getClass().getName().equals("java.lang.UNIXProcess")) {
        @SuppressWarnings("rawtypes")
        Class proc = pProc.getClass();
        java.lang.reflect.Field field = proc.getDeclaredField("pid");
        field.setAccessible(true);
        Object pid = field.get(pProc);
        returnVal = String.valueOf(pid);
      } else {
        throw new IllegalArgumentException("Not a UNIXProcess");
      }
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, U.class, "getUnixPID",
          "Issue getting the process id from the forked process " + e.getMessage());

    }

    return (returnVal);
  } // End Method getUnixPID() -----------------------

  // ------------------------------------------
  /**
   * getPID.
   *
   * @param pCmd the cmd
   * @return String "none" if not found
   */
  // ------------------------------------------
  public final static String getWindowsPID(String pCmd) {

    String returnValue = "none";
    Process p = null;

    try {
      p = Runtime.getRuntime().exec("cmd /c tasklist /V /FO CSV");

      try (final BufferedReader in =
          new BufferedReader(new InputStreamReader(p.getInputStream()));) {
        String line;
        while ((line = in.readLine()) != null) {

          String[] cols = U.split(line, ",");
          if (cols[cols.length - 1].endsWith(pCmd)) {
            returnValue = U.unquote(cols[1]);
            break;
          }
        } // end reading through the input
      }
      p.destroy();

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, U.class, "getPID",
          "Something went wrong getting the pid " + e.getMessage());

    }

    return returnValue;

  } // End Method getPID() -----------------------

  // ------------------------------------------
  /**
   * kill given a pid, kill the method - detect if you are running on windows or
   * not.
   *
   * @param pid the pid
   * @throws IOException Signals that an I/O exception has occurred.
   */
  // ------------------------------------------
  public final static void kill(String pid) throws IOException {

    if (U.isWindows())
      Runtime.getRuntime().exec("taskkill /F /T /PID " + pid);
    else
      Runtime.getRuntime().exec("kill -9 " + pid);

  } // End Method kill() -----------------------

  // ------------------------------------------
  /**
   * readProperties reads the properties in from a properties file and doesn't
   * complain when it cannot read in the file.
   * 
   * Or if the fileName is on the classpath, will read it from that
   *
   * @param pFileName the file name
   * @return Properties
   */
  // ------------------------------------------
  public final static Properties readProperties(String pFileName) {

    Properties properties = new Properties();

    try {
      File aFile = new File(pFileName);

      if (aFile.canRead()) {
        try (final FileReader reader = new FileReader(aFile);) {
          properties.load(reader);
        }
      } else {
        // look at the class path to see if it's there

        try (final InputStream stream = U.class.getClassLoader().getResourceAsStream(pFileName);) {
          properties.load(stream);
        }
      }

    } catch (Exception e) {
      U.errorMsg(e, "Issue reading the properties file " + pFileName);
    }
    return (properties);

  } // End Method readProperties() -----------------------

  // ------------------------------------------
  /**
   * getDateStamp returns with a simple yyyy-mm-dd hh:mm:ss date string.
   *
   * @return String
   */
  // ------------------------------------------
  public final static String getDateStamp() {
    String dateStamp = "0000-01-01_00:00:00";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    dateStamp = sdf.format(new Date());

    return dateStamp;
  } // End Method getDateStamp() -----------------------

  // ------------------------------------------
  /**
   * getDateStampSimple returns a simple date stamp with no colons in it. Colons
   * mess up file names. Use this method for appending a date stamp to a file
   * name. The format is yyyy-mm-dd_hh_mm_ss
   *
   * @return String
   */
  // ------------------------------------------
  public final static String getDateStampSimple() {
    String dateStamp = "0000-01-01_00:00:00";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
    dateStamp = sdf.format(new Date());

    return dateStamp;

    // End Method getDateStampSimple() -----------------------
  }

  // ------------------------------------------
  /**
   * getDate returns with a simple yyyy.mm.dd string
   *
   *
   * @return String
   */
  // ------------------------------------------
  public final static String getDate() {
    String dateStamp = "0000-01-01";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    Calendar cal = Calendar.getInstance();
    dateStamp = sdf.format(cal.getTime());

    return dateStamp;

  } // End Method getDate() -----------------------

  // ------------------------------------------
  /**
   * log will write out a message to the gLog file.
   *
   * @param pOut the out
   * @param pMsg the msg
   * @deprecated
   */
  // ------------------------------------------
  public final static void logP(PrintWriter pOut, String pMsg) {

    GLog.println(GLog.STD___LEVEL, U.class, "logP", pMsg);

  } // End Method log() -----------------------

  // ------------------------------------------
  /**
   * isAbsolutePath returns true if the path is an abosolute or relative path.
   *
   * @param pFileName the file name
   * @return boolean
   */
  // ------------------------------------------
  public final static boolean isAbsolutePath(String pFileName) {

    File aFile = new File(pFileName);
    boolean returnValue = aFile.isAbsolute();
    return returnValue;

  } // End Method isAbsolutePath() -----------------------

  // ------------------------------------------
  /**
   * cleanPath replaces backslashes with forward slashes in a path.
   *
   * @param pPath the path
   * @return String
   */
  // ------------------------------------------
  public final static String cleanPath(String pPath) {

    String path = pPath;
    if (pPath.indexOf('\\') > -1) {
      while (path.indexOf('\\') > -1)
        path = path.replace('\\', '/');
    }
    return path;

  } // End Method cleanPath() -----------------------

  // ------------------------------------------
  /**
   * charArrayToInt a more efficient way to go from a char[] to int.
   *
   * @param cs the cs
   * @return int
   */
  // ------------------------------------------
  public final static int charArrayToInt(char[] cs) {
    int result = 0;
    for (int i = 0; i < cs.length; i++) {
      int digit = cs[i] - '0';
      if ((digit < 0) || (digit > 9)) {

        // System.err.println("cs = |" + new String(cs) + "|");
        // throw new NumberFormatException();
      } else {
        result *= 10;
        result += digit;
      }
    }
    // System.err.println(new String(cs) + "|" + result);
    return result;

  } // End Method charArrayToInt() -----------------------

  // ------------------------------------------
  /**
   * log logs the message at the Level status.
   *
   * @param pClass the class
   * @return the package name
   */
  // ------------------------------------------
  // public final static void log(java.util.logging.Logger pLogger, Level
  // pLevel, String pMsg) {

  // pLogger.log(pLevel, pMsg);

  // } // End Method log() -----------------------

  // ------------------------------------------
  /**
   * getPackageName retrieves the package name from the class passed in.
   *
   *
   * @param pClass
   * @return String
   */
  // ------------------------------------------
  public final static String getPackageName(Class<?> pClass) {
    String packageName = null;

    String className = pClass.getCanonicalName();
    int lastPeriod = className.lastIndexOf('.');
    packageName = className.substring(0, lastPeriod);

    GLog.println(GLog.STD___LEVEL, U.class, "getPackageName",
        "---------------- package name =====>|" + packageName + "|");

    return packageName;

  } // End Method getPackageName() -----------------------

  // ------------------------------------------
  /**
   * stripPunctuation returns a string that has no punctuation in it. This is
   * useful to go from 3.) -> 3
   * 
   * If the trimmed return value amounts to 0 chars, null is returned.
   *
   * @param pString the string
   * @return String
   */
  // ------------------------------------------
  public final static String stripPunctuation(String pString) {

    String returnVal = pString;
    StringBuffer buff = new StringBuffer();

    if (pString != null && pString.trim().length() > 0) {

      char cArray[] = pString.toCharArray();

      for (char c : cArray) {
        if (!U.isPunctuation(c))
          buff.append(c);
      } // end loop thru the chars of the string
      returnVal = buff.toString().trim();
      if (returnVal.length() == 0)
        returnVal = null;

    } // end if not empty

    return returnVal;

  } // End Method stripPunctuation() -----------------------

  // ------------------------------------------
  /**
   * getNameWithoutNameSpace returns the name of a class or a path that is the
   * left most element - for example gov.nih.cc.rmd.model.Sentence ==> Sentence
   *
   * @param pFullName the full name
   * @return String
   */
  // ------------------------------------------
  public final static String getNameWithoutNameSpace(String pFullName) {

    String name = pFullName;

    int index = pFullName.lastIndexOf('.');
    if (index > 0 && index < pFullName.length() - 2)
      name = pFullName.substring(index + 1);

    return name;
  } // End Method getNameWithoutNameSpace() -----

  // ------------------------------------------
  /**
   * getFileNameOnly returns the name of a file without the path or extension.
   *
   * @param pFullName the full name
   * @return String
   */
  // ------------------------------------------
  public final static String getFileNameOnly(String pFullName) {

    String name = pFullName;
    
   
    if ( pFullName != null ) {
      File aFile = new File ( pFullName);
      name = aFile.getName();
      
      if ( name != null )
        name = name.substring(0, name.lastIndexOf("."));
    }
   

    return name;
  } // End Method getNameWithoutNameSpace() -----
  
  // =======================================================
  /**
   * stripEOF.
   *
   * @param pBuff the buff
   * @return String
   */
  // =======================================================
  public final static String stripEOF(String pBuff) {

    String buff = null;
    char eof = (char) 26;
    if (pBuff != null)
      buff = pBuff.replace(eof, ' ');
    return buff;
    // End Method stripEOF() ======================
  }

  // =======================================================
  /**
   * Removes all invalid Unicode characters that are not suitable to be used
   * either in markup or text inside XML Documents.
   * 
   * Based on these recommendations
   * http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char
   * http://cse-mjmcl.cse.bris.ac.uk/blog/2007/02/14/1171465494443.html
   *
   * @param s The resultant String stripped of the offending characters!
   * @return String
   */
  // =======================================================
  public final static String removeInvalidXMLCharacters(String s) {
    StringBuilder out = new StringBuilder();

    int codePoint;
    int i = 0;

    while (i < s.length()) {
      // This is the unicode code of the character.
      codePoint = s.codePointAt(i);
      if ((codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
          || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
          || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
          || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF))) {
        out.append(Character.toChars(codePoint));
      }
      i += Character.charCount(codePoint);
    }
    return out.toString();
  }

  // =======================================================
  /**
   * Remove all characters that are valid XML markups.
   * http://www.w3.org/TR/2000/REC-xml-20001006#syntax
   *
   * @param s the s
   * @return String
   */
  // =======================================================
  public final static String removeXMLMarkups(String s) {
    StringBuffer out = new StringBuffer();
    char[] allCharacters = s.toCharArray();
    for (char c : allCharacters) {
      if ((c == '\'') || (c == '<') || (c == '>') || (c == '&') || (c == '\"')) {
        continue;
      } else {
        out.append(c);
      }
    }
    return out.toString();
  }

  // =======================================================
  /**
   * scrubInvalidXML remotes non ascii chars, invalid xml chars and illegal xml
   * chars.
   *
   * @param pBuff the buff
   * @return the string
   */
  // =======================================================
  public final static String scrubInvalidXML(String pBuff) {

    String x = removeInvalidXMLCharacters(pBuff);
    String y = removeXMLMarkups(x);
    String z = y.replaceAll("[^\\p{ASCII}]", "");

    return z;
  } // end Method scrubInvalidXML() =======================

  // =======================================================
  /**
   * getOnlyFileName retrieves only the filename from the full input path.
   *
   * @param pFullFileName the full file name
   * @return the only file name
   */
  // =======================================================
  public final static String getOnlyFileName(String pFullFileName) {

    String name = pFullFileName;

    int index = pFullFileName.lastIndexOf('/');
    if (index < 0)
      index = pFullFileName.lastIndexOf('\\');

    if (index < 0)
      index = 0;

    if (index > 0 && index < pFullFileName.length() - 2)
      name = pFullFileName.substring(index + 1);

    return name;
  } // End Method getOnlyFileName() ======================

  // =======================================================
  /**
   * getUniqId returns a unique id string.
   *
   * @return String
   */
  // =======================================================
  public final static String getUniqId() {
    String retVal = UUID.randomUUID().toString();
    return retVal;
  } // End Method getUniqId() ======================

  // =======================================================
  /**
   * getInputStreamFromFileOrResource tries to read from a file first, and if
   * not from the resource that would be found in a jar.
   *
   * @param pFileName a relative path starting from resources/....
   * @return BufferedReader
   * @exception Exception the exception
   */
  // =======================================================
  public final static BufferedReader getInputStreamFromFileOrResource(String pFileName)
    throws Exception {

    final String resourceDir = U.getClassPathToResources();
    if (resourceDir != null) {
      String fullFileName = resourceDir + "/" + pFileName;
      return new BufferedReader(new FileReader(fullFileName));
    }

    final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
    final InputStream inStream = contextLoader.getResourceAsStream(pFileName);
    if (inStream != null) {
      return new BufferedReader(new InputStreamReader(inStream));
    }

    final File pFile = new File(pFileName);
    if (pFile.exists() && !pFile.isDirectory()) {
      return new BufferedReader(new FileReader(pFileName));
    }

    throw new Exception("Unable to find resource as file or in classpath = " + pFileName);

  } // End Method getInputStreamFromFileOrResource() ======================

  // =======================================================
  /**
   * getDirFromFileName returns the path that leads up to the filename.
   *
   * @param pFileName the file name
   * @return String
   */
  // =======================================================
  public final static String getDirFromFileName(String pFileName) {

    int lastSlash = -1;
    String dirName = "./";
    if (pFileName != null) {
      lastSlash = pFileName.lastIndexOf('/');
      if (lastSlash == -1) {
        lastSlash = pFileName.lastIndexOf('\\');
      }
      if (lastSlash > -1 && lastSlash < pFileName.length())
        dirName = pFileName.substring(0, lastSlash);

    }

    return dirName;

  } // End Method getDirFromFileName() ======================

  // =======================================================
  /**
   * getVersionStamp returns a string that includes the time that this was
   * comiled, from a resource file that has such information in it
   * 
   * The file has a specific place, and an external process should be updating
   * it from time to time
   * 
   * /utah/branches/versions/currentVersion.txt
   * @return String
   */
  // =======================================================
  public final static String getVersionStamp() {

    String returnVal = null;
    try {
      returnVal = U.readFile("/utah/branches/versions/currentVersion.txt");
    } catch (Exception e) {

      returnVal = " no version on file ";
    }

    return returnVal;

  } // End Method getVersionStamp() ======================

  // =======================================================
  /**
   * getNumericValue returns a double from the numeric part of the buffer. This
   * method returns the first value of a string that might contain multiple
   * numeric values.
   * 
   * i.e. 10.0 < returns 10 10.0.10 < returns 10
   *
   * @param buff the buff
   * @return double returns -1 if there is no value
   */
  // =======================================================
  public final static double getNumericValue(String buff) {

    double returnVal = -1;
    StringBuffer value = new StringBuffer();

    if (buff != null) {
      char tt[] = buff.trim().toCharArray();
      boolean firstCharSeen = false;
      boolean secondCharSeen = false;
      boolean firstPeriodSeen = false;
      if (tt != null && tt.length > 0) {
        for (int i = 0; i < tt.length; i++) {
          char v = tt[i];
          if (v >= '0' && v <= '9' && !secondCharSeen)
            value.append(tt[i]);
          else if (tt[i] == '.' && !firstPeriodSeen) {
            firstPeriodSeen = true;
            value.append(tt[i]);
          } else if (!firstCharSeen) {
            firstCharSeen = true;

          } else {
            break;
          }
        }
      }
      if (value != null && value.length() > 0)
        returnVal = Double.valueOf(value.toString());

    }

    return returnVal;
    // End Method getNumericValue() ======================
  }

  // =======================================================
  /**
   * runJavaProcess is similar to runProcess, but figures out how to kick off a
   * java process.
   *
   * @param pBinDir the bin dir
   * @param pJarFile the jar file
   * @param pJavaArgs the java args
   * @param pCommandLineArgs the command line args
   * @return Process
   * @throws Exception the exception
   */
  // =======================================================
  public final static ProcessBuilder javaProcess(String pBinDir, String pJarFile, String pJavaArgs,
    String[] pCommandLineArgs) throws Exception {

    String FS = System.getProperty("file.separator");
    String javaCommand = System.getProperty("java.home") + FS + "bin" + FS + "java";

    StringBuffer commandLineArgBuff = new StringBuffer();
    ProcessBuilder pb = new ProcessBuilder();

    File binDir = new File(pBinDir);
    if (!binDir.exists()) {
      GLog.println(GLog.ERROR_LEVEL, U.class, "javaProcess", "The dir does not exist ");
      throw new Exception();
    }

    File jarFile = new File(pBinDir + "/" + pJarFile);
    if (!jarFile.exists()) {
      GLog.println(GLog.ERROR_LEVEL, U.class, "javaProcess", "The jarfile  does not exist ");
      throw new Exception();
    }
    javaCommand = javaCommand.replace('\\', '/');
    GLog.println(GLog.STD___LEVEL, U.class, "javaProcess", " Java command = " + javaCommand);

    String jarArgs = "-jar \"" + jarFile.getAbsolutePath() + "\"";
    GLog.println(GLog.STD___LEVEL, U.class, "javaProcess", " JavaArgs = " + pJavaArgs + jarArgs);
    String javaArgs = pJavaArgs; // + jarArgs;
    javaArgs = javaArgs.replace('\\', '/');

    if (pCommandLineArgs != null)
      for (String arg : pCommandLineArgs)
        commandLineArgBuff.append(arg + " ");

    String fullCommand = javaCommand + " " + javaArgs;

    pb.command(fullCommand);
    GLog.println(GLog.STD___LEVEL, U.class, "runJavaProcess", fullCommand);
    pb.redirectError();
    pb.redirectInput();
    pb.redirectOutput();

    U.run(fullCommand);

    return pb;

  } // End Method runJavaProcess() ======================

  // =======================================================
  /**
   * getClassPath returns a string that is the classpath, delimited with the
   * correct delimiter for the system being run.
   *
   * @return String
   */
  // =======================================================
  public final static String getClassPath() {

    String classpath = null;
    URL[] urls = ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs();

    StringBuffer buff = new StringBuffer();
    if (urls != null) {
      for (URL url : urls) {
        buff.append(new File(url.getPath()));
        buff.append(System.getProperty("path.separator"));
      }
      classpath = buff.toString();
      int toIndex = classpath.lastIndexOf(System.getProperty("path.separator"));
      classpath = classpath.substring(0, toIndex);
      classpath = classpath.replace("\\", "/");
      classpath = classpath.replace("C:", "");
    }
    return classpath;

  } // End Method getClassPath() ======================

  // ==========================================
  /**
   * singleQuote returns a string with 'xxxx' if there are embedded single
   * quotes, they are duplicated as in John's boat -> 'John''s boat'
   * 
   * This is to conform with H2's quoting mechanism.
   *
   * @param pTerm the term
   * @return String
   */
  // ==========================================
  public final static String singleQuote(String pTerm) {
    String returnVal = pTerm;

    returnVal = pTerm.replace("'", "''");
    returnVal = "'" + returnVal + "'";

    return returnVal;
  } // end Method singleQuote() ===============

  // ==========================================
  /**
   * reverse reverses the order of a List.
   *
   * @param pElements the elements
   * @return the list
   */
  // ==========================================
  public final static List<?> reverse(List<?> pElements) {

    ArrayList<Object> newList = new ArrayList<Object>(pElements.size());
    for (int i = pElements.size() - 1; i >= 0; i--)
      newList.add(pElements.get(i));

    return newList;

  } // end Method reverse() ===================

  
  // =================================================
  /**
   * reverse reverses the order of this table
   * 
   * @param pList
   */
  // =================================================
  public static String[] reverse(String[] pList) {

    String [] returnVal = null;
    if ( pList != null && pList.length > 0 ) {
      returnVal = new String[ pList.length ];
      int k = 0;
      for ( int i = pList.length -1 ; i >=  0; i-- ) 
        returnVal[k++] = pList[i];

    }

    return returnVal;

  } // end Method reverse() ----------------------

  // ==========================================
  /**
   * changeOption changes the value of an option.
   *
   * @param pArgs the args
   * @param pOptionName the option name
   * @param pNewValue the new value
   */
  // ==========================================
  public final static void changeOption(String[] pArgs, String pOptionName, String pNewValue) {

    for (int i = 0; i < pArgs.length; i++) {
      String arg = pArgs[i];
      if (arg.startsWith(pOptionName)) {
        pArgs[i] = pOptionName + pNewValue;
        break;
      }
    }
  } // End Method changeOption() =============

  // ==========================================
  /**
   * formatDouble formats a double to 2 decimal places.
   *
   * @param pVal the val
   * @return String
   */
  // ==========================================
  public final static String formatDouble(double pVal) {
    String returnVal = "";

    if (df2 == null)
      df2 = new DecimalFormat(".##");
    returnVal = df2.format(pVal);

    return returnVal;

  } // End Method formatDobule() -------------

  // =================================================
  /**
   * getClassName returns the name of the class from a static call.
   *
   * @param pMethodHandle the method handle
   * @return String
   */
  // =================================================

  public final static String getClassName(String pMethodHandle) {

    String returnVal = pMethodHandle;

    // returnVal = returnVal.substring(6);
    int lastPeriod = returnVal.lastIndexOf('.');
    returnVal = returnVal.substring(lastPeriod + 1);
    return returnVal;
  } // End Method getClassName() --------------------

  // =================================================
  /**
   * getClassNameFull returns the full name of the class including the
   * namespace.
   *
   * @param pMethodHandle the method handle
   * @return String
   */
  // =================================================
  public final static String getClassNameFull(String pMethodHandle) {
    String returnVal = pMethodHandle;
    returnVal = returnVal.substring(6);

    return returnVal;

  } // End Method getClassNameFull() -----------------

  // =================================================
  /**
   * inEclipse returns true if one has put -DrunInEclipse=true
   * on the command line args
   * 
   * @return boolean 
  */
  // =================================================
  public final static boolean inEclipse() {
    
    if ( inEclipseStr == null ) {
     inEclipseStr = System.getProperty("runInEclipse");  
     inEclipse = "true".equalsIgnoreCase(inEclipseStr);
   }
    
    return inEclipse;
  }
  

  // =================================================
/**
 * clearConsole will clear the console, and if the
 * console is an eclipse console, will print a bunch of \n's
 * 
 * There is no way to figure out if we are running in
 * eclipse, so for the time being, add to eclipse's run configuration
 * -DruninEclipse=true
 * 
 * @throws IOException 
 * @throws InterruptedException 
 * 
*/
// =================================================
public final static void clearConsole()  {
  
  try {
    
    if ( U.inEclipse()  ) 
      for ( int i = 0; i < 20; i++ )
        System.out.print("\n");
    
    else if ( U.isWindows())
      new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    else 
      new ProcessBuilder("sh", "-c", "clear").inheritIO().start().waitFor();
    
  } catch (Exception e) {
    e.printStackTrace();
  }
  
} // end Method clearConsole() -------------------

// =================================================
/**
 * clearConsole prints x number of lines to simulate 
 * clear console when in the inEclipse mode.
 * 
 * @param pNumberOfLines
*/
// =================================================
public static void clearConsole(int pNumberOfLines) {
  
  if ( U.inEclipse()  ) 
    for ( int i = 0; i < pNumberOfLines; i++ )
      System.out.print(i + "\n");
  else
    clearConsole();
  
  
}

// =================================================
/**
 * initialCapitalize returns the string with the first
 * character capitialized
 * 
 * @param pOldName
 * @return String
*/
// =================================================
public static String initialCapitalize(String pOldName) {
  
  String returnVal = pOldName;
  
  if ( returnVal != null && returnVal.length() > 0 ) {
    char firstChar = pOldName.charAt(0);
    char FirstChar = Character.toUpperCase(firstChar);
    char[] returnValChars = returnVal.toCharArray();
    returnValChars[0] = FirstChar;
    returnVal = new String( returnValChars);
    
  }
  return returnVal;
}

// =================================================
/**
 * isWord returns true if this is a sequence of Character.isAlphebetic() 
 * @param pText
 * @return boolean
*/
// =================================================
public final static boolean isWord(String pText ) {
  boolean returnVal = false;
  
  char[] chrs = pText.toCharArray();
  
  
  for ( int i = 0; i < chrs.length; i++ ) 
   if( Character.isAlphabetic(chrs[i]) )
     returnVal = true;
   else 
     return false;
   
   
  return returnVal;
} // end Method isWord() -----------------------

// =================================================
/**
 * normalizePipesAndNewLines replaces pipes with "~" and newlines with spaces.
 * 
 * 
 * @param annotation
 * @return String
*/
// =================================================
 public final static String normalizePipesAndNewLines(String pText) {
  
   String buff = pText.replace('|', '~');
   buff = buff.replace('\n', ' ');
   buff = buff.replace( '\r', ' ');
  
   
  return buff ;
} // end Method normalizePipesAndNewLines() ------

 private final static String EMPTY_STRING = "";
// =================================================
/**
 * rightTrim trims the end of a string
 * 
 * @param pLine
 * @return String
*/
// =================================================
 public final static String rightTrim(String pLine) {
  
   if ( pLine != null )
     return pLine.replaceAll("\\s+$", EMPTY_STRING);
   else
     return null;
   
} // end Method rightTrim() ------------------------
 
//=================================================
/**
* rTrim trims the end of a string
* 
* @param pLine
* @return String
*/
//=================================================
public final static String rTrim(String pLine) {
  return rightTrim( pLine);
} // end Method rTrim() --------------------------

 
//=================================================
/**
* leftTrim trims the beginning of a string
* 
* @param pLine
* @return String
*/
//=================================================
public final static String leftTrim(String pLine) {

 if ( pLine != null )
   return pLine.replaceAll("^\\s+", EMPTY_STRING);
 else
   return null;
 
} // end Method leftTrim() ------------------------

//=================================================
/**
* lTrim trims the beginning of a string
* 
* @param pLine
* @return String
*/
//=================================================
public final static String lTrim(String pLine) {
  return leftTrim( pLine );
} // end Method lTrim() --------------------------

// =================================================
/**
 * stripExtensionFromInputFile 
 * 
 * @param pInputFile
 * @return String
*/
// =================================================
public final static String stripExtensionFromInputFile(String pInputFile) {
  
  String returnVal = pInputFile;
  String extension = U.getFileExtension(pInputFile);
  
  if ( extension != null && extension.trim().length() > 0  )
      returnVal = returnVal.substring(0, returnVal.lastIndexOf(extension));
  
  return returnVal;
 
 
  
} // end Method stripExtensionFromInputFile() ------

 

// =================================================
/**
 * getNumLeadingSpaces returns the number of leading spaces/tabs
 * 
 *   question - should I weight tabs more?
 * @param pLine
 * @return int
*/
// =================================================
public static final int getNumLeadingSpaces(String pLine) {
  int returnVal = 0;
  
  char[] c = pLine.toCharArray();
  
  for (int i = 0; i < c.length; i++ )
    if ( c[i] == ' ' || c[i] == '\t')
      returnVal++;
    else
      break;
  return returnVal;
} // end Method getNumLeadingSpaces() --------------

// =================================================
/**
 * getFirstToken returns the first token of a line
 *    
 *    
 * @param pLine
 * @return String
*/
// =================================================
  public final static String getFirstToken(String pLine) {
  
    String returnVal = null;
    StringBuffer resultBuff = new StringBuffer();
    String aBuff = rTrim(pLine);
    if ( aBuff != null && !aBuff.isEmpty()) {
      char[] cBuff = aBuff.toCharArray();
      for ( int i = 0; i < cBuff.length; i++ )  
        if ( Character.isSpaceChar(cBuff[i]))
          break;
        else
          resultBuff.append(cBuff[i]);
    }
    
    return returnVal;
  } // end Method getFirstToken() -----------------

// =================================================
/**
 * getLastChar returns the right most non space char
 * if it exists.  If the line is just spaces, the
 * ' ' is returned.
 * 
 * @param pLine
 * @return char
*/
// =================================================
public final static char getLastChar(String pLine) {
  
  char returnChar = ' ';
  if ( pLine != null ) {
    String buff = pLine.trim();
    returnChar = buff.charAt( buff.length()-1);
  }
  return returnChar;
} // end Method getLastChar() --------------------

// =================================================
/**
 * getEncodingFromXMLFile  retrieves the explicit encoding/character set from 
 *                         the xml preamble. Without the double quotes
 *                         
 *                         <?xml version="1.0"encoding="UTF-8"?>
 *                                                      -----
 * @param pDocumentContent
 * @return String         null if there is not one
*/
// =================================================
public static String getEncodingFromXMLFile(String pDocumentContent) {
  
  String returnVal = null;
  
 //  "encoding="UTF-8"?>
 int encodingStart = pDocumentContent.indexOf("encoding=" );
 if (encodingStart > 0 ) {
   encodingStart = encodingStart + "encoding=".length() + 1 ;
   int encodingEnd = pDocumentContent.indexOf("?>");
   if ( encodingEnd > 0)
     returnVal = pDocumentContent.substring(encodingStart, encodingEnd-1 );
   
 }
   
  return returnVal;
} // end Method getEncodingFromXMLFile() -------


//=================================================
/**
* UTF8_PrintWriter creates a PrintWriter which opens
* with UTF-8 encoding explicitly. 
* 
* @param pFile
* @return PrintWriter
* @throws FileNotFoundException
* @throws UnsupportedEncodingException 
* 
**/
// =================================================
public static final PrintWriter UTF8_PrintWriter(File pFile) throws FileNotFoundException, UnsupportedEncodingException {
 
 PrintWriter out = null;
 OutputStream       outputStream       = new FileOutputStream( pFile);
 OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
 out = new PrintWriter( outputStreamWriter);
 
 return out;
} // end Method UTF8_PrintWriter() ---------------


//=================================================
/**
* UTF8_PrintWriter creates a PrintWriter which opens
* with UTF-8 encoding explicitly. 
* 
* @param pFileName
* @return PrintWriter
* @throws FileNotFoundException
* @throws UnsupportedEncodingException 
* 
**/
//=================================================
public static final PrintWriter UTF8_PrintWriter(String pFileName) throws FileNotFoundException, UnsupportedEncodingException {

  File aFile = new File( pFileName);
  PrintWriter out = UTF8_PrintWriter( aFile);
  
return out;
} // end Method UTF8_PrintWriter() ---------------

// =================================================
/**
 * sortAndUniqFile 
 * 
 * @param pFile
 * @throws Exception
*/
// =================================================
public final static void sortAndUniqFile(String pFile) throws Exception  {

  try {
  String[] rows = U.readFileIntoStringArray( pFile);
  
  HashSet<String> hashRows = new HashSet<String>( rows.length*2);
  
  for ( String row: rows)
    hashRows.add( row );
  
  // convert the hash to an array
  
  String[] uniquedRows = hashRows.toArray( new String[hashRows.size()]);
  Arrays.sort( uniquedRows  );
  
  // remove the old file
  U.deleteFile(pFile);
  
  // create a new file, put the contents of the uniqued and sorted rows in it
  PrintWriter out = new PrintWriter( pFile);
  for ( String row : uniquedRows ) 
    out.print(row + "\n");
  
  out.close();
  
  
  } catch (Exception e) {
    e.printStackTrace();
    GLog.println("Issue sorting And Uniquing the file " + pFile + " :" + e.toString());
    throw e;
  }
  
  
} // end Method sortAndUniqFile() ------------------


// =================================================
/**
 * copyFile 
 * 
 * @param pFrom
 * @param pOutputDir
 * @param pOverwrite
 * @throws Exception
*/
// =================================================
public static void copyFile(String pFrom, String pOutputDir, boolean pOverwrite) throws Exception  {

  try {
    
    Path src = Paths.get(pFrom);
    String sourceName = U.getOnlyFileName(src.toFile().getName());
    String outputFileName = pOutputDir + "/" + sourceName;
    File outputFile = new File( outputFileName );
    Path dest = Paths.get(outputFile.getAbsolutePath() );
    
    
    if ( !outputFile.exists() || pOverwrite ) 
      Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
    
     
  } catch ( Exception e) {
    e.printStackTrace();
    GLog.println("Issue copying file from " + pFrom + " to dir " + pOutputDir + " :" + e.toString());
    throw e;
  }
  
  
  
  
} // end Method copyFile() ------------------------




} // end Class U() --------------------------------
null
