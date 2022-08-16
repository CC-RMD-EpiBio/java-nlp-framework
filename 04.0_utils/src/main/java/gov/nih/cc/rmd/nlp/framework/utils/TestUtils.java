package gov.nih.cc.rmd.nlp.framework.utils;
// =================================================
/**
 * TestUtils is a part of the efficacy test utilities
 * 
 * The efficacy test pattern includes the following:
 *   
 *  1.  Where the test data is (one or more of the following)
 *         ${projectHome}/00_data/vtt/someTests.vtt   Directory with annotated data in it in .vtt format
 *         ${projectHome/00_data/snippet/someTests.vtt  Directory  with annotated snippets for training/testing
 *         ${projectHome}/00_data/xmi/someTests.xmi   Directory with annotated data in it in uima's xmi format
 *         ${projecthome}/00_ data/ascii/someTests.txt    Directory with un-annotated data in ascii format
 *   
 *  2. Where the result data spores goes:
 *        ${projectHome}/00_data/efficacyResults/applicationNameTest/eval/eval_main.txt   Full efficacy report
 *        ${projectHome/00_data/efficacyResults/applicationNameTest/efficacyTest.csv      Record of every run's f-Score
 *
 *  3. Running the test from a junit test hook -
 *       call's TestUtils.getBaseDir()  to get the ${projectHome}  directory - 
 *          This is inferred by looking for the ${user.dir}, (the current dir) 
 *          then looking for the /target/bin on that path
 *          and stripping /target/bin off
 *          
 *       from eclipse:  the current directory that mains are run from are set 
 *                      manually in the "run configuration"->arguments->working directory.  
 *                      The convention is to set this working directory to target/bin
 *       
 *       from command line via mvn test
 *                      The current directory is usually where the pom.xml is
 *                      which should be the $project home.  (I need to test if this
 *                      is the case when the reactor kicks off in a parent directory
 *                      
 *       
 *   
 * @author     Guy Divita
 * @created    Jan 19, 2018
 * 
*/


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * @author guy
 *
 */
public class TestUtils {

  // =================================================
  /**
   * getBaseDir is assuming this is being run via command
   * line maven wrapper -i.e. mvn test  from a base dir
   * that has the pom.xml in it 
   * 
   *   or from within eclipse where the workingDir = ..../target/bin
   *   
   * It will try to infer/devine what the base project dir is
   * 
   * If this is being called from a war file within a tomcat
   * server, lord knows what should come back.
   * 
   * @return String
  */
  // =================================================
 public static String getBaseDir() {
    
    String basePath = null;
    try {
      String base = System.getProperty( "user.dir" );
      
      if ( base != null && base.trim().length() > 0) {
        basePath = base.replace('\\', '/'); 
        basePath = basePath.replace("/target", "");
        basePath = basePath.replace("/bin", "");
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, "Issue trying to get the project's base dir " + e.toString());
      throw e;
    }
    
    return basePath;
 } // end method getBaseDir() -------------

 // =================================================
 /**
  * getGoldFile returns a file from the resources
  * that is the goldfile
  * 
  * @param pArgs
  * @return File (null if not found)
  * @throws Exception 
 */
 // =================================================
 public static File getGoldFile(String[] pArgs) throws Exception {
  
   File goldFile = null;
   try {
     String goldStandard = U.getOption(pArgs,  "--goldStandardFile=", "resources/goldStandards/goldStandard.json" ); 
     
     goldFile = U.getFileFromClassPathResource(goldStandard);
     
     GLog.println(GLog.STD___LEVEL, "Looking for the file " + goldFile );
   } catch (Exception e) {
     e.printStackTrace();
     String msg = "Issue getting the gold standard  for " + e.toString();
     GLog.println(GLog.ERROR_LEVEL, msg);
     throw new Exception( msg);
   }
   return goldFile; 
 } // end Method getGoldFile() ------------




 //=================================================
 /**
 * getTmpFileName returns a fileName from a temp dir
 * that tomcat or jenkins can write to.  Uses System.getProperty("java.io.tmpdir");
 * 
 * @return String
  * @throws IOException 
 */
 //=================================================
 public final static File getTmpFile(String pPrefix) throws IOException {
  
   String filename  = null;
   File    aFile = null;
   try {
     String tmpDir = System.getProperty("java.io.tmpdir");
     tmpDir = tmpDir.replace('\\', '/');
    
     String tmpFileName = "Copper_" + pPrefix + "_" + U.getDateStampSimple() + ".json";
     filename = tmpDir + "/" + tmpFileName;
     filename = filename.replaceAll("//", "/" );
      aFile = new File( filename );
     if  ( !aFile.createNewFile() ) {
       String msg = "Issue trying to get a writable file wtf " + filename + pPrefix ;
       GLog.println(GLog.ERROR_LEVEL, msg );
       // throw new IOException ( msg);
     }
   } catch (Exception e) {
     e.printStackTrace();
       String msg = "Issue trying to get a writable file " + filename  + e.toString();
       GLog.println(GLog.ERROR_LEVEL, msg );
       throw e  ;
     }
     
     return aFile;
       
   } // end Method getTmpFile() ----------------
   

 //=================================================
 /**
 * getTmpOut returns an open PrintWriter file handle.
 * 
 * @param pFile
 * 
 * @return PrintWriter
 * @exception 
 */
 //=================================================
  public static final PrintWriter getTmpOut( File pFile ) throws Exception {
    PrintWriter out = null; 
    try {
      out = new PrintWriter( pFile );
    } catch (Exception e) {
      GLog.println(GLog.ERROR_LEVEL, "Issue trying to open the tmp  file for comparison " + pFile.getAbsolutePath() + " " + e.toString());
      throw e;
    }
    return out;
  }

// =================================================
/**
 * replaceIdentifiers replaces identifiers with "XXXXX"
 * 
 * @param pBuff
 * @return String
*/
// =================================================
  public final static String replaceIdentifiers(String pBuff) {
  
    StringBuffer buff = new StringBuffer();
    
    if ( pBuff != null ) {
      String rows[] = U.split(pBuff, "\n");
    
      for ( int i = 0; i < rows.length; i++ ) {
        if ( rows[i] != null && rows[i].length() > 0 ) {
          if ( rows[i].trim().startsWith("\"id\"") )
              rows[i] = "      \"id\" : \"XXXX\",";
          else if ( rows[i].trim().startsWith("\"parentAnnotationId\""))
            rows[i] = "      \"parentAnnotationId\" : \"PPPP\",";
          buff.append(rows[i] + "\n");
        }
      }
      
    }
      
  return buff.toString();
} // end Method replaceIdentifiers() ------------
 
} // end Class testUtils() -----------------
