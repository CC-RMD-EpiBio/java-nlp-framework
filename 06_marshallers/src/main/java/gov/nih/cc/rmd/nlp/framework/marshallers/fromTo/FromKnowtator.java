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
// =================================================
/**
 * FromKnowtatorSimple assumes that the the source file has already
 * been read in via a collection reader. This class will read in the
 * knowtator annotations and create uima annotations.
 * 
 * This has been broken down into a collection reader which reads in
 * the source text and an annotator which reads in the knowtator annotation
 * because when these two operations were combined, the annotations would
 * not show up.
 * 
 * 
 * @author Guy Divita
 * @created Mar 1, 2011
 * 
 *          *
 *          *
 *          *
 *          *
 *          *
 *          *
 *          *
 *          *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.fromTo;

import gov.nih.cc.rmd.nlp.framework.utils.U;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.uima.resource.ResourceInitializationException;

public class FromKnowtator  {




  // =======================================================
  /**
   * Constructor FromKnowtator 
   *
   */
  // =======================================================
  public FromKnowtator()  {
    
  }
   
  

  // =======================================================
  /**
   * Constructor FromKnowtator 
   *
   * @param pInputDir
   * @param pRecurse
   * @param pFixOffsets
   * @param pKnowtatorLabels
  
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public FromKnowtator( String pInputDir, boolean pRecurse, boolean pFixOffsets, String pKnowtatorLabels ) throws Exception {
      
    initialize(  pInputDir,  pRecurse,  pFixOffsets, pKnowtatorLabels );
    
  } // end Constructor() --------------------
  
	


  

//-----------------------------------------
/** 
* initialize reads the directory containing the input files.  By default, this
* recurses through sub directories. By default, this initialize sets fixOFfsets to false.
* 
* @param pInputDir
* @throws ResourceInitializationException

*/
//-----------------------------------------
public void initialize(String pInputDir ) throws ResourceInitializationException  {

initialize( pInputDir, true, false, (String) null);

} // End Method initialize() ---------------------------


//-----------------------------------------
/** 
 * initialize opens the directory containing .knowtator.xml files
 * 
 *  The inputDir should point to the directory ABOVE the ./saved and ./corpus directories.
 *
 * @param pInputDir
 * @param recurseIntoSubDirs
 * @param pFixOffsets
 * @param pKnowtatorLabelsString
 * 
 * @exception ResourceInitializationException
 
 * 
 */
// -----------------------------------------
public void initialize(String pInputDir, boolean recurseIntoSubDirs, boolean pFixOffsets, String pKnowtatorLabelsString ) throws ResourceInitializationException  {
  
  
  String[] knowtatorLabelMap = parseKnowtatorLabelsString( pKnowtatorLabelsString);
  
  this.knowtatorToUIMAMapping = new HashMap<String, String>();
  if (knowtatorLabelMap != null && knowtatorLabelMap.length > 0  ) {
    for (String aMap : knowtatorLabelMap) {
      if ( aMap != null ) {
        String[] buff = U.split(aMap);
        this.knowtatorToUIMAMapping.put(buff[0], buff[1]);
      }
    }
  }
  
  
  
  File inputDir = null;
  this.listOfFilesToProcess = new ArrayList<File>();
  this.fixOffsets = pFixOffsets;
  
  if ( pInputDir != null && pInputDir.length() > 0 ) {
    
    String savedDir = pInputDir + "/saved";
    
    inputDir = new File ( savedDir );
    
    if ( inputDir.isDirectory() && inputDir.canRead() ) {
      
      File[] filesAndSubDirs = inputDir.listFiles();
        
      if ( recurseIntoSubDirs ) {
         getFiles(this.listOfFilesToProcess, filesAndSubDirs );
      } else {
        this.listOfFilesToProcess = filterFiles( filesAndSubDirs) ;
      }
    } // end if the inputDir can be read                
  } // end if the inputDir is not null
  
  
  this.numberOfFiles = this.listOfFilesToProcess.size();
  System.err.println("The number ofiles to process = " + this.numberOfFiles);
  

       
} // end Method initialize() --------------
  


// =======================================================
/**
 * parseKnowtatorLabelsString takes xxx:xxx|yyy:yyy|zzz:zzz into an array [xxx:xxx, yyy:yyy, zzz:zzz ]
 * 
 * @param knowtatorLabelsString
 * @return
 */
// =======================================================
private String[] parseKnowtatorLabelsString(String knowtatorLabelsString) {

  String returnValue[] = null;
  if ( knowtatorLabelsString != null && knowtatorLabelsString.length() > 0 )
    returnValue = U.split(knowtatorLabelsString);
  
  return returnValue;
  // End Method parseKnowtatorLabelsString() ======================
}


  

//=======================================================
/**
* filterFiles returns a list of text files
* 
* @param pFiles
* @return List<File>
*/
// =======================================================
private List<File> filterFiles(File[] pFiles) {

 ArrayList<File> filteredFiles = new ArrayList<File>(pFiles.length);
 for ( File file: pFiles )
   if ( filterInKnowtatorFiles( file))
     filteredFiles.add( file);
   
 return filteredFiles;
}  // End Method filterFiles() ======================


// =======================================================
/**
 * filterInKnowtatorFiles returns true if the file is a knowtator file
 * 
 * @param pFile
 * @return boolean
 */
// =======================================================
private boolean filterInKnowtatorFiles(File pFile) {
 
  boolean returnValue = false;
  if ( pFile.getName().endsWith(".knowtator.xml") ) 
      returnValue = true;
    
  return returnValue;
  
}  // End Method filterInKnowtatorFiles() ======================


// =======================================================
/**
* getFiles retrieves the list of files from the directory
* that match the filtering criteria
* 
* @param pListOfFiles
* @param pFilesAndSubDirs
* 
*/
// =======================================================
private void  getFiles(List<File> pListOfFiles, File[] pFilesAndSubDirs) {
 
 for ( File file: pFilesAndSubDirs )
   if ( file.isDirectory()) {
     getFiles( pListOfFiles, file.listFiles());
   } else if ( filterInKnowtatorFiles ( file )){
    // System.err.println("adding file " + file.getAbsolutePath());
     pListOfFiles.add( file);
   }
 
}   // End Method getFiles() ======================




  // -------------------------------------------
  // Class Variables
  // -------------------------------------------
  private HashMap<String, String> knowtatorToUIMAMapping = null;
  private boolean                 fixOffsets             = true;
  public static final String PARAM_INPUTFILE = "inputFile";
  private List<File>        listOfFilesToProcess = null;
  private int                        fileCounter = 0;
  private int                      numberOfFiles = 0;
  





} // end Class FromKnowtatorSimple() -------------
