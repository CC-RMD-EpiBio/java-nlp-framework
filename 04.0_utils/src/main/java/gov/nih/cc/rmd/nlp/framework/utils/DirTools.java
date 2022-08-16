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
// =======================================================
/**
 * DirTools is a synchronized static class that reads in the files from an 
 * input directory in a safe way, doles out these files in a safe way, 
 * moves the files from the input directory to a finished directory in
 * a safe way when done.
 *
 * @author  guy
 * @created Nov 21, 2013
 *
   
 */



import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;



/**
 * @author guy
 *
 */
public class DirTools {

  private List<String> mFiles = null;
  private Iterator<String> mFilesIterator;
  private String inputDir = null;
  private String finishedDir = null;

//=======================================================
 /**
  * Constructor DirTools creates a list of files to process
  *
  * @param pInputDir
  * 
  * @throws Exception 
  */
 // =======================================================
 public DirTools(String pInputDir ) throws Exception {
   
   this.inputDir = pInputDir;
   
   if ( finishedDir != null ) 
     U.mkDir(finishedDir);
   
   try {
     ListFilesRecursively fw = new ListFilesRecursively( ); 
   
     String startDir = pInputDir;
 
     String[] files = fw.listAllFiles( startDir );
    
     mFiles = Arrays.asList(files);
     if ( mFiles != null )
       mFilesIterator = mFiles.iterator();
     
   } catch (Exception e) {
     e.printStackTrace();
     String msg = "Issue creating the DirTools with the inputDir " + pInputDir + " " + e.toString();
     System.err.println(msg);
     throw new Exception( msg);
   }
 } // end Constructor() -----------------------------------

 

//=======================================================
/**
 * Constructor DirTools creates a list of files to process
 *
 * @param pInputDir
 * @param pFinishedDir
 * @throws Exception 
 */
// =======================================================
public DirTools(String pInputDir, String pFinishedDir ) throws Exception {
  
  this.inputDir = pInputDir;
  this.finishedDir = pFinishedDir;
  
  U.mkDir(finishedDir);
  try {
    ListFilesRecursively fw = new ListFilesRecursively( ); 
  
    String startDir = pInputDir;

    String[] files = fw.listAllFiles( startDir );
   
    mFiles = Arrays.asList(files);
    if ( mFiles != null )
      mFilesIterator = mFiles.iterator();
    
  } catch (Exception e) {
    e.printStackTrace();
    String msg = "Issue creating the DirTools with the inputDir " + pInputDir + " " + e.toString();
    System.err.println(msg);
    throw new Exception( msg);
  }
} // end Constructor() -----------------------------------


  // =======================================================
/**
 * getinputDir 
 * 
 * @return the inputDir
 */
// =======================================================
public final String getInputDir() {
  return inputDir;
}



// =======================================================
/**
 * setinputDir 
 * 
 * @param inputDir the inputDir to set
 */
// =======================================================
public final void setInputDir(String inputDir) {
  this.inputDir = inputDir;
}



  /**
   * @return the files
   */
  public synchronized List<String> getfiles() {
    return mFiles;
  } // end Method getFiles



  // =======================================================
  /**
   * getNext retrieves the name of the next file to be processed
   * 
   * @return String
   */
  // =======================================================
  public synchronized String getNext() {
    
    String returnVal = null;
    
    if ( mFilesIterator != null ) 
      try {
        returnVal = mFilesIterator.next();
      } catch (Exception e) {}
    return returnVal;
  }  // End Method getNext() ======================
  



  // =======================================================
  /**
   * moveToFinishedDir moves a successfully processed file to
   * the finished directory so that it is out of the way
   * 
   * @param pFileName
   * @throws Exception 
   */
  // =======================================================
  public synchronized void moveToFinishedDir(String pFileName) throws Exception {
   
    if ( this.finishedDir == null ) {
      String msg = "No finished dir defined, not moving it";
      System.err.println(msg);
      throw new Exception(msg);
    }
    
    String     justFileName = U.getNameWithoutNameSpace(pFileName);
    String finishedLocation = this.finishedDir + "/" + justFileName;
    try {
    
      File afile =new File( pFileName);

      if(afile.renameTo(new File(this.finishedDir + "/" + afile.getName()))){
        System.out.println("File is moved successful!");
       
        
      }else{
       
        String msg = "Issue trying to move " + pFileName + " to " +  finishedLocation ;
        System.err.println(msg);
        throw new Exception (msg);
      }

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue trying to move " + pFileName + " to " +  finishedLocation + " " + e.toString();
      System.err.println(msg);
      throw new Exception (msg);
    }
  }  // End Method moveToFinishedDir() ======================



  // =======================================================
  /**
   * hasNext checks the iterator to see if there are any additional
   * files to be processed
   * 
   * @return boolean
   */
  // =======================================================
  public synchronized boolean hasNext() {
    return mFilesIterator.hasNext();
    
  }  // End Method hasNext() ======================





  
  
  

} // end Class DirTools
