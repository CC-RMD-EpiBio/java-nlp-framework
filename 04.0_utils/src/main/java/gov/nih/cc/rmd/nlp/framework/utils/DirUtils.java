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

} 
null
