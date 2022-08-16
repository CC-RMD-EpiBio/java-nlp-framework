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
