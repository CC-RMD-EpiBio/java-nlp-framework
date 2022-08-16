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
// =======================================================
/**
 * CombineTables combines table counts from split tables
 *
 * @author Divita
 * @created Oct 2, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.corpusStats;

import gov.nih.cc.rmd.nlp.framework.utils.U;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class CombineTables {

  // =======================================================
  /**
   * concatinateTable 
   * 
   * @param outputDir
   * @param string
   * @throws Exception 
   */
  // =======================================================
  public static void concatinateTable(String pOutputDir, String pTableName ) throws Exception {
   
    List<File> t = getTables( pOutputDir + "/stats", pTableName );
    boolean headerWritten = false;
    BufferedReader in = null;
    try {
    
      PrintWriter out = new PrintWriter( pOutputDir + "/stats/" + pTableName + "_Combined.csv");
      // -------------------------
      // Read in the initial file to get title, column, row headings
      for ( int i = 0; i < t.size(); i++ ) {
        in = new BufferedReader( new FileReader(t.get(i)));
        String line = null;
        while ( (line = in.readLine() )!= null ) {
          if ( !headerWritten ) {
            if ( !line.contains(",")) 
              out.print(line + "\n");
            else
              out.print(line + "\n");
              headerWritten = true;
          }
          if ( line.contains(",") && !line.contains("DocumentId"))
            out.print( line + "\n");
          
         
          
        } // end loop thru the rows of a file
        
        in.close();
      //  t.get(i).delete();
        
      } // end loop thru files
      out.close();
    } catch (Exception e ) {
      e.printStackTrace();
      String msg = "Issue concatinating file " + e.toString();
      System.err.println(msg);
      throw new Exception (msg);
    }
    
  } // End Method concatinateTable() ======================
  

  // =======================================================
  /**
   * combineARFFFiles combines arff files from the pOutputDir directory into one combined arff file in that directory. 
   *    This assumes that all the arff files have the same attributes and enumerated values within the arff files.
   *    
   *    This will create a "combined.arff" file 
   *    
   * @param outputDir
   * @param string
   * @throws Exception 
   */
  // =======================================================
  public static void combineARFFFiles(String pOutputDir ) throws Exception {
   
    List<File> t = getArffFiles( pOutputDir );
   
    BufferedReader in = null;
    try {
      PrintWriter out = new PrintWriter( pOutputDir + "/svm/Combined.arff");
     
      // -------------------------
      // Read in the first arff file to pick up the arff file attributes
      String arffFileHeader = getArffFileAttributes( t.get(0));
      
      // ---------------------------
      // Write the header out
      out.print(arffFileHeader);
      out.print('\n');
      out.print("@data\n");
      
      // ------------------------------------------------
      // Loop through each of the arff files, skip down to the line after the "@data" line write out all lines after that
     
      for ( int i = 0; i < t.size(); i++ ) {
        in = new BufferedReader( new FileReader(t.get(i)));
        String line = null;
        boolean dataSeen = false;
        while ( (line = in.readLine() )!= null ) {
          
          if ( !dataSeen ) {
            if ( line.startsWith("@data") ) 
              dataSeen = true;
          } else {
            out.print(line);
            out.print('\n');
          }
        } // end loop thru the rows of a file
        
        in.close();
       
        
      } // end loop thru files
      out.close();
    } catch (Exception e ) {
      e.printStackTrace();
      String msg = "Issue concatinating file " + e.toString();
      System.err.println(msg);
      throw new Exception (msg);
    }
    
  } // End Method combineARFFFiles() ======================
  

 //=======================================================
 /**
  * getArffFileAttributes reads in the arff file, picks up and
  * returns the attributes lines. This reads everything down
  * to the @data line.
  * 
  * @param pFile
  * @return String
  * @throws Exception 
  */
 // =======================================================
  private static String getArffFileAttributes(File pFile) throws Exception  {
    
    StringBuffer buff = new StringBuffer();
    
    BufferedReader in = null;
    try {
     in = new BufferedReader( new FileReader(pFile ));
     String line = null;
    boolean dataSeen = false;
    while ( (line = in.readLine() )!= null ) {
      
      if ( !dataSeen ) {
        if ( line.startsWith("@data") ) 
          dataSeen = true;
        else {
          buff.append( line);
          buff.append('\n');
        }
      } else {
        in.close();
        return buff.toString();
      }
    } // end loop thru the file.
   
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue reading the arff file " + e.toString());
      throw e;
    }
    if ( in != null)
      in.close();
    
    return buff.toString();
  } // End Method getArffFileAttributes() ======================
  
  //=======================================================
  /**
   * getArffFileAttributes reads in the arff file, picks up and
   * returns the attributes lines. This reads everything down
   * to the @data line.
   * 
   *  Note that this does not iterate through sub directories.
   * @param pFile
   * @return String
   * @throws Exception 
   */
  // =======================================================
   private static List<File> getArffFiles(String pOutputDir) throws Exception  {
   
      ArrayList<File> returnVal = null;
      
      File aDir = new File( pOutputDir);
      
      if ( aDir != null && aDir.isDirectory() && aDir.canRead()) {
        File[] someFiles = aDir.listFiles();
        
        if ( someFiles != null && someFiles.length > 0 ) {
          returnVal = new ArrayList<File>();
          for ( File aFile : someFiles ) {
            if (aFile.getName().endsWith(".arff") && !aFile.getName().toLowerCase().contains("combined") ) {
              returnVal.add(aFile);
            }
          }
        }
      }
     
      return returnVal;
   } // end Method getArffFiles() --------------------------

  // =======================================================
  /**
   * combineTable
   * 
   * @param pOutputDir
   * @param pTableName 
   * @throws Exception 
   */
  // =======================================================
  public static void combineTable(String pOutputDir, String pTableName) throws Exception {
   
    
    List<File> t = getTables( pOutputDir + "/stats", pTableName );
    
    
    // -------------------------
    // Read in the initial file to get title, column, row headings
    StructuredTable table = new StructuredTable(pTableName, pOutputDir, ",");
  
    for ( int i = 0; i < t.size(); i++ ) {
    
      table.process(t.get(i));
      
    } // end loop thru files to process;
    
    table.printTable();
    
  } // End Method combineTable() ======================
  

  // =======================================================
  /**
   * getTables retrieves all the tables that start with this name
   * 
   * @param pTableNamePrefix
   * @return
   */
  // =======================================================
  private static List<File> getTables(String pDir, String pTableNamePrefix) {
    
    File aDir = new File( pDir);
    File[] allFiles = aDir.listFiles();
    ArrayList<File> returnVal = new ArrayList<File>();
    
    for ( File aFile : allFiles) {
      String fileName = aFile.getName();
      if ( fileName.startsWith(pTableNamePrefix ) && !fileName.contains("_Combined" ) )
          returnVal.add( aFile);
    }
    
    return returnVal;
    // End Method getTables() ======================
  }

  // =======================================================
  /**
   * combine
   * 
   * @param pOutputDir
   */
  // =======================================================
  public static void combine (String pOutputDir ) {
  
    try {
     
      
      concatinateTable( pOutputDir, "corpusInstances");
      combineTable( pOutputDir, "statsTable2");    
      combineTable( pOutputDir, "statsTable3");
      combineTable( pOutputDir, "statsTable4");
    
      combineTable( pOutputDir, "statsTable7");
  
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue writing out the combined tables");
    }
    
  
  
  
  } // end Method combine() -----------------------------
  
      // =======================================================
      /**
       * main
       * 
       * @param pArgs
       */
      // =======================================================
      public static void main (String[] pArgs ) {
      
        try {
         
          String outputDir = U.getOption(pArgs, "--outputDir=", "./");
          
          concatinateTable( outputDir, "corpusInstances");
          
        } catch ( Exception e) {
          
        }
      } // end Method main() -----------------------------


      // =======================================================
      /**
       * combineEasy 
       * 
       * @param outputDir
       */
      // =======================================================
      public static void combineEasy(String pOutputDir) {
       
       List<File> t = getTables( pOutputDir + "/stats", "corpusInstances_" );
        boolean headerWritten = false;
        BufferedReader in = null;
        try {
        
          PrintWriter out = new PrintWriter( pOutputDir + "/stats/" + "Instances" + "_Combined.csv");
          // -------------------------
          // Read in the initial file to get title, column, row headings
          for ( int i = 0; i < t.size(); i++ ) {
            in = new BufferedReader( new FileReader(t.get(i)));
            String line = null;
            while ( (line = in.readLine() )!= null ) {
              if ( !headerWritten ) {
                if ( !line.contains(",")) 
                  out.print(line + "\n");
                else
                  out.print(line + "\n");
                  headerWritten = true;
              }
              if ( line.contains(",") && !line.contains("DocumentId"))
                out.print( line + "\n");
              
             
              
            } // end loop thru the rows of a file
            
            in.close();
          //  t.get(i).delete();
            
          } // end loop thru files
          out.close();
        } catch (Exception e ) {
          e.printStackTrace();
          String msg = "Issue concatinating file " + e.toString();
          System.err.println(msg);
         
        }
        
      } // End Method combineEasy() ======================
      
  
} // end Class CombineTables -----------------------------
