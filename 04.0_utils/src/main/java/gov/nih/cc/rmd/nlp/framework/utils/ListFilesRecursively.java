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
// ------------------------------------------------------------
/**
 * ListFilesRecursively is a class to recursively list files
 * given a starting directory.  The class is not a static class
 * so the listAllFiles can be used over and over again within
 * a threaded environment.  
 * 
 * This method duplicates functionality within the apache common-io 
 * class. It is here because I didn't want this package to rely
 * upon any external projects, in an effort to keep dependencies 
 * to a minimum for this package.
 *
 * @author Guy Divita
 * Sep 28, 2012
 * 
 * ------------------------------------------------------------
 *
 *
 *
 * -------------------------------------------------------------
 */
// -------------------------------------------------------------
package gov.nih.cc.rmd.nlp.framework.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ListFilesRecursively {

  //------------------------------------------
  /**
   * walk recursively walks through files of a directory
   * until there are no more directories to walk through
   * This method accumulates files along the way 
   * in the class variable this.filesFound.
   *
   * @param path
   */
  // ------------------------------------------
  private void walk( String path ) { 
    
    File root = new File( path ); 
    File[] list = root.listFiles(); 

    for ( File f : list ) { 
      if ( f.isDirectory() ) { 
        walk( f.getAbsolutePath() ); 
       // System.out.println( "Dir:" + f.getAbsoluteFile() ); 
      } 
      else { 
        if ( this.filesFound == null )
          this.filesFound = new ArrayList<String>();
        this.filesFound.add( f.getAbsolutePath());
        // System.out.println( "File:" + f.getAbsoluteFile() ); 
      } 
    }   
  } // end Method walk() ---------------------

  
  // ------------------------------------------
  /**
   * listAllFiles recursively lists files
   *   if the startDir exists but is empty, the returned string will be empty but not null
   *
   * @param startDir
   * @return String[]
   * @exception Exception
   */
  // ------------------------------------------
  public String[] listAllFiles(String startDir) throws Exception  {
    
    String returnVal[] = null;
    File mStartDir = new File ( startDir);
    
    if ( !mStartDir.exists() || !mStartDir.isDirectory() || !mStartDir.canRead() ) {
      throw new Exception ("\n\n Either the directory \n|" + startDir + "|\n does not exist, or it cannot be read\n");
    }
   
    
    try {
      walk( startDir);
    } catch  ( Exception e ) {
      e.printStackTrace();
      throw new Exception ( "Something went wrong recursively listing files :" + e.toString());
    }
    if ( this.filesFound != null )
      returnVal = this.filesFound.toArray(new String[ this.filesFound.size()]);
    else {
       // ------------------------
       // the file was found but is empty
      returnVal = new String[0];
    }
    // ------------------------
    // Clear out the files found for the next call
    if (this.filesFound != null)
      this.filesFound.clear();
      
    return returnVal;
        
  }  // End Method listAllFiles() -----------------------

  // ------------------------------------------
  /**
   * main
   *   This main takes an unlabeled starting directory
   *   as an argument.  If no arguments, the current directory
   *   is used.
   *
   * @param args
   */
  // ------------------------------------------
  public static void main(String[] args) {
  
      ListFilesRecursively fw = new ListFilesRecursively( ); 
      
      String startDir = "./";
      if ( args != null )
         startDir = args[0];
      
      try {
        String[] files = fw.listAllFiles( startDir );
        
        if ( files != null ) {
          for ( int i = 0; i < files.length; i++ ) {
            System.out.println( i + "|" + files[i]);
          }
        }
        
      } catch (Exception e) {
       
        e.printStackTrace();
        System.err.println( "Something went wrong in main : " +  e.toString() );
      } 
      
  
  
    // End Method main() -----------------------
  }




  private List<String> filesFound = null;
  

  // End ListFilesRecursively Class -------------------------------
}
