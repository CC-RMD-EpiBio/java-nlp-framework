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
