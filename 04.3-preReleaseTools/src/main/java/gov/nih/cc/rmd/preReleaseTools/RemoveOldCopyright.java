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
 * RemoveOldCopyright  iterates through a project's 
 *  *.java files,  find the old copyright blurbs
 *  and removes them.
 *  
 *    Requires --inputDir=/some/project/name   (will recurse through subdirs looking for .java files
 *             
 *             
 *    Relies upon a file of patterns to find and remove the line for
 *    in src/main/resources/oldCopyrightPatterns.txt 
 *
 * @author     Guy Divita
 * @created    Sep 10, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.preReleaseTools;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

/**
 * @author divitag2
 *
 */
public class RemoveOldCopyright {

  // =================================================
  /**
   * main 
   * 
   *      Looking for --inputDir=
   * @param pArgs    
  */
  // =================================================
public static void main(String[] pArgs) {
   
  
  try {
    
    String[] args = setArgs( pArgs);
    
    initialize( pArgs );
    
    List<File> sourceCode = getSourceCodeFiles( args );
  
    for ( File souceFile : sourceCode )
      removeCopyright( souceFile);
  
    
    
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println("Issue trying to remove copyright from file " + e.toString());
  }

  } // end Method main() -----------------------------

// =================================================
  /**
   * removeCopyright creates a new version of the file without copyright lines in it.
   * 
   * @param pSourceFile
   * @throws Exception 
  */
  // =================================================
  private final static void removeCopyright(File pSourceFile) throws Exception {
   
    
    String[] rows = null;
    
    System.err.println("Looking at " + pSourceFile);
    // Read in the source file
    try {
      rows = U.readFileIntoStringArray(pSourceFile.getAbsolutePath());
    } catch (Exception e) {
     e.printStackTrace();
     System.err.println(" Issue with reading or writing the source file " + pSourceFile.getAbsolutePath() + " " + e.toString());
     throw e;
    
      
    }
    
    // make changes
    List<String> betterRows = new ArrayList<String>();
    boolean changesMade = makeChanges( rows, betterRows );
    
  
    
    if ( changesMade )
    // write a new copy of the file in the same location
      overwrite( pSourceFile, betterRows );
    
    
  } // end Method removeCopyright() ----------------

// =================================================
/**
 * makeChanges removes rows that have lines from the old copyright
 * blurbs in them
 * 
 * @param pRows
 * @param betterRows
 * @return boolean   (if changes were made) 
*/
// =================================================
private final static boolean makeChanges(String[] pRows, List<String> betterRows) {
 
  boolean returnVal = false;
  
  for (String row : pRows ) 
    if ( !copyrightLineFound (row )) {
      betterRows.add( row );
      
    } else {
      returnVal = true;
    }
  
    
  return returnVal;
} // end Method makeChanges() ----------------------

// =================================================
/**
 * copyrightLineFound
 * 
 * @param pRow
 * @return boolean
*/
// =================================================
private static boolean copyrightLineFound(String pRow) {
   boolean returnVal = false;
   
   
   String row = pRow.replace('*',  ' ');
   row = row.trim();
   
   if ( row.contains("Health Services Research & Development Service"))
     System.err.println( "|" + row + "|");
   
   
   if ( copyRightRows.contains( row ))
     returnVal = true;
   else if ( pRow.toLowerCase().contains(" *  *") && !pRow.contains("|"))   // *  * is a remnant of a prior attempt to put copyright in.
     returnVal = true;
   else if ( pRow.toLowerCase().contains(" *   *") && !pRow.contains("|"))   // *  * is a remnant of a prior attempt to put copyright in.
     returnVal = true;
  
     
   else if ( pRow.toLowerCase().contains("copyright") && !pRow.contains("|")) {
    
     System.err.println("Check on this -> line with |" + pRow);
     returnVal = true;
   } if (  pRow.toLowerCase().contains("ciitizen")) 
      System.err.println("Check on this -> line with |" + pRow);
     
   
   return returnVal;
} // end Method copyrightLineFound

// =================================================
/**
 * overwrite creates a new version of the pSourceFile
 * with the better rows in it.
 * 
 * @param pSourceFile
 * @param betterRows
 * @throws Exception 
*/
// =================================================
private final static void overwrite(File pSourceFile, List<String> betterRows) throws Exception {
  
  
  FileWriter out = null;
  
  String newFileName = pSourceFile.getAbsolutePath();
  
  
  
  if ( pSourceFile.canWrite() ) {
    
    try {
  
      System.err.println("Overwriting " + pSourceFile.getAbsolutePath());
      // delete the old file
      pSourceFile.delete();
      
      // create a new file
      File newFile = new File( newFileName);
      
      out = new FileWriter( newFile, false);
      
      for ( String row: betterRows )
        out.write( row + "\n");
      
      
      out.close();
    } catch ( Exception e) {
      e.printStackTrace();
      System.err.println("Issue writing the file " + pSourceFile + " " + e.toString());
      throw e;
    }
    
  }
  
  
} // end Method overwrite() ------------------------

// =================================================
  /**
   * getSourceCodeFiles retrieves source code files from the dir and it's children
   * 
   * @param pArgs
   * @return List<File> 
   * @throws Exception
  */
  // =================================================
 private final static List<File> getSourceCodeFiles(String[] pArgs) throws Exception {
 
   
   List<File>sourceFiles  = null;
   List<File>returnVal = new ArrayList<File>();
   
   try {
     String inputDir = U.getOption( pArgs, "--inputDir=", "/some/src/dir");
    
  
  
    // Iterate through inputDir, printing out the .java files
    
  
    
    List<File> allDirs = getAllDirs( inputDir );
    
    if ( allDirs != null && !allDirs.isEmpty())
      for ( File aDir : allDirs ) {
        
        // Look in each of these dirs to find source code
        sourceFiles = getSourceFiles( aDir );
        returnVal.addAll( sourceFiles);
       
      }
    
   } catch (Exception e) {
     e.printStackTrace();
     System.err.println("Something went wrong " + e.toString());
     throw e;
     
   }
   
   return returnVal;
  }

// =================================================
  /**
   * getSourceFiles returns a list of files that are 
   * source code files
   * 
   * @param pDir
   * @return List<File> 
  */
  // =================================================
  private final static List<File> getSourceFiles(File pDir) {
  
    List<File> returnVal = new ArrayList<File>();
    
    File[] someFiles = pDir.listFiles();
    
    if ( someFiles != null && someFiles.length > 0 ) 
      for ( File aFile : someFiles ) 
        if ( isSourceCode( aFile ))
          returnVal.add( aFile);
      
    return returnVal;
  } // end Method getSourceFiles() -----------------

// =================================================
/**
 * isSourceCode returns true if this is source code
 *   *.java  or copyright.txt  or *.lragr 
 * 
 * @param pFile
 * @return boolean
*/
// =================================================
 private static boolean isSourceCode(File pFile) {
  
   boolean returnVal = false;
   
   String fileName = pFile.getName().toLowerCase();
   
   if ( fileName != null  )
     if ( fileName.endsWith(".java") ||
    //      fileName.endsWith(".lragr") ||
          fileName.toLowerCase().contains("copyright") )
       returnVal = true;
     
   return returnVal;
   
   
} // end Method isSourceCode() --------------------

// =================================================
  /**
   * getAllDirs returns a list of all dirs from the inputDir down
   * 
   * @param pInputDir
   * @return List<File>
  */
  // =================================================
 private final static List<File> getAllDirs(String pInputDir) {
  
   List<File> returnVal = new ArrayList<File>();
    
    File rootDir = new File(pInputDir);
    
    if ( rootDir != null && rootDir.isDirectory() && rootDir.canWrite()) {
      
      returnVal.add( rootDir);
      
      File[] children = rootDir.listFiles();
      
      if ( children != null  )
        for ( File child: children ) {
          if ( child.isDirectory() && child.canWrite() ) {
            returnVal.add( child );
            List<File>childDirs = getChildDirs( child );
            if ( childDirs != null && !childDirs.isEmpty())
              returnVal.addAll( childDirs);
          }
        }
      
    }
    
    return returnVal;
  } // end Method getAllDirs() -------------

// =================================================
/**
 * getChildDirs returns a list of recursed child directories sans the pDir itself
 * 
 * @param pDir
 * @return List<File>
*/
// =================================================
 private final static List<File> getChildDirs(File pDir) {
  List<File> returnVal = new ArrayList<File>();
  
  File[] children = pDir.listFiles();
  
  if ( children != null  )
    for ( File child: children ) {
      if ( child.isDirectory() && child.canWrite() ) {
        returnVal.add( child );
        List<File>childDirs = getChildDirs( child );
        if ( childDirs != null && !childDirs.isEmpty())
          returnVal.addAll( childDirs);
      }
    } 
   
  return returnVal; 
} // end Method getChildDirs() -------------

//------------------------------------------
/**
* setArgs
* 
* 
* @return
*/
// ------------------------------------------
public static String[] setArgs(String pArgs[]) {


 String    inputDir  = U.getOption(pArgs, "--inputDir=", "/data/input/");

 String args[] = {
     
     "--inputDir=" + inputDir
     
 
 };

 
    
 if ( Use.usageAndExitIfHelp("RemoveOldCopyright",   pArgs, args ))
   System.exit(0);
  

 return args;

}  // End Method setArgs() -----------------------



// =================================================
/**
 * initialize reads in the old copyright rows
 * 
 * @param pArgs   (--copyrightLinesFile=/somepath/ )
 * @throws Exception 
*/
// =================================================
private final static void initialize(String[] pArgs) throws Exception {
  
  String copyrightFileName = U.getOption(pArgs, "--copyrightLinesFile=", "copyrightLines.txt" );
  copyRightRows = new HashSet<String>();
  
  try {
    String buff = U.readClassPathResource(copyrightFileName);
    
    System.err.println("Initializing with " + copyrightFileName);
    String[] rows;
    if ( buff != null ) { 
      rows = U.split(buff, "\n");
      
      for ( String row : rows ) {
        
        if ( row != null && row.trim().length() > 0 ) {
          row = row.replace('*', ' ');
          copyRightRows.add( row.trim());
        }
        
      }
    
    
    }
    
    System.err.println("Done initializing ");
  } catch (Exception e ) {
    e.printStackTrace();
    System.err.println("Issue trying to read in the resources file " + e.toString());
    throw e;
  }
  
} // end initialize()-------------------------------



// ------------------------------------------------
// Global Variables
// ------------------------------------------------
private static HashSet<String> copyRightRows;



} // end Class RemoveOldCopyRight
