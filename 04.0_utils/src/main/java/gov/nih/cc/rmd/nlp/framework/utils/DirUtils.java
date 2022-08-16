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
/*  
 *
 *
 *
 *
 *
 * 
 *
 */
/**
 * DirUtils includes methods to iterate recursively through directories
 * looking for files with a particular pattern, such as .txt, or .java
 * Then kick off a method to work on each file.  The methods should keep
 * a status of progress through the iteration, and success or failure
 * of actions on the file.  
 * 
 * There should be some way to be able to interupt the iteration, then
 * start it back up where it left off. 
 * 
 * @author Guy Divita
 * @date   09/27/2011
 * 
 * 
 * 
 */
package gov.nih.cc.rmd.nlp.framework.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DirUtils {


  // =================================================
  /**
   * copyDir copies the sourceDir folder to destinationFolder including the contents.
   * It recurses thru sub folders.  It will overwrite if files or folders already exist
   * 
   * @param sourceDir
   * @param destinationDir
  */
  // =================================================
   public static final void copyDir(File sourceFolder, File destinationFolder) throws IOException
  {
      //Check if sourceFolder is a directory or file
      //If sourceFolder is file; then copy the file directly to new location
      if (sourceFolder.isDirectory()) 
      {
          //Verify if destinationFolder is already present; If not then create it
          if (!destinationFolder.exists()) 
          {
              destinationFolder.mkdir();
              System.out.println("Directory created :: " + destinationFolder);
          }
           
          //Get all files from source directory
          String files[] = sourceFolder.list();
           
          //Iterate over all files and copy them to destinationFolder one by one
          for (String file : files) 
          {
              File srcFile = new File(sourceFolder, file);
              File destFile = new File(destinationFolder, file);
               
              //Recursive function call
              copyDir(srcFile, destFile);
          }
      }
      else
      {
          //Copy the file content from one place to another 
          Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
          System.out.println("File copied :: " + destinationFolder);
      }
  }
  
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }


  // =================================================
  /**
   * copyDir copies the sourceDir folder to destinationFolder including the contents.
   * It recurses thru sub folders.  It will overwrite if files or folders already exist
   * 
   * @param sourceDir
   * @param destinationDir
   * @throws IOException 
  */
  // =================================================
  public static final void copyDir(String sourceDir, String destinationDir) throws IOException {
   
    File srcDir = new File (sourceDir);
    File destDir = new File ( destinationDir );
    
    copyDir( srcDir, destDir);
    
    
  } // end Method copyDir() --------------------------
} // end Class DirUtils() ----------------------------
