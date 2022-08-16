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
 * FromPipedText reads in data from text files that have pipes in them that indicate segment boundaries
 * The pipes are converted to spaces to keep the offsets correct.  Gotta figure out how to
 * preserve that info though to create segments downstream.
 *
 * and converts them to Cas's.
 *
 *
 * @author  Guy Divita 
 * @created Aug 17, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.text;



import gov.va.chir.model.DocumentHeader;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import gate.Gate;
import gov.nih.cc.rmd.nlp.framework.marshallers.reader.AddNewLinesTool;
import gov.nih.cc.rmd.nlp.framework.utils.FileMetaData;
import gov.nih.cc.rmd.nlp.framework.utils.GleanMetaDataFromFileName;



public class FromPipedText extends FromText {

  
//=======================================================
 /**
  * Constructor FromText is called from SuperReader
  *  assumes that the initialize() method will be called later on.
  *
  */
 // =======================================================
 public FromPipedText()  {
   
   
 } // end Constructor() ---------------------
 
  
  
	// =======================================================
  /**
   * Constructor FromText 
   *
   * @param pInputDir
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public FromPipedText(String pInputDir) throws ResourceInitializationException {
    
    initialize( pInputDir, true);
    
  } // end Constructor() ---------------------
  

  

//=======================================================
 /**
  * Constructor FromText 
  *
  * @param pInputDir
  * @param pRecurseIntoSubDirs
  * 
  * @throws ResourceInitializationException 
  */
 // =======================================================
 public FromPipedText(String pInputDir, boolean pRecurseIntoSubDirs) throws ResourceInitializationException {
   
   initialize( pInputDir, pRecurseIntoSubDirs);
   
 } // end Constructor() ---------------------
  
  // =================================================
/**
 * Constructor
 *
 * @param args
 * @throws ResourceInitializationException 
 * 
**/
// =================================================
public FromPipedText(String[] args) throws ResourceInitializationException {
  
   initialize( args);
}




//-----------------------------------------
 /** 
  * get retrieves the next document id from the list of document ids, queries the database fro 
  * annotations that share this document id, creates annotations for this id.
  * @param pCAS
  * @param pRecordId
  * @throws IOException
  * @throws CollectionException
  */
 // -----------------------------------------
@Override
 public synchronized void get(CAS pCAS, long pRecordId) throws IOException, CollectionException {
  
   
   JCas jcas = null;  
   try {
     jcas = pCAS.getJCas();
     File aFile = null;
     
    
     
     try {
     
       aFile = this.listOfFilesToProcess.get( (int) pRecordId);
     
     } catch ( Exception e) {
       // ---------------------
       // In multi-treaded environments - a process could sneak in an take the
       //                                 last record between the time this thread
       //                                 calls hasNext and getNext.
       //                                 This catch will be the catch to figure
       //                                 out that there are no more records to process
       e.printStackTrace();
       GLog.println(GLog.ERROR_LEVEL,this.getClass(), "get","Issue with records in thread " +  Thread.currentThread().getName() + " " + e.toString());
       return;
     }
     
     
     
     
     
     String documentText  = null;
     String documentId    = aFile.getAbsolutePath(); //   used to be aFile.getName();  needs to carry along the full path
     if ( documentId.contains( "\\"))
       documentId = documentId.replace('\\', '/');
     GLog.println(GLog.STD___LEVEL,this.getClass(), "get"," --------->  Reading in " + aFile.getName() );
    
    
       documentText = U.readFile(aFile);
       
       if ( this.addNewLines )
         documentText = this.addNewLineTool.addBTISNewLines( documentText );
       
       // -----------------------------
       // Deal with BTRIS De-identification markers
       //  [LAST_NAME i=459] 
       // ----------------------------
       if ( this.deIdentified ) {
         documentText = this.addNewLineTool.stripBTRISDeidentifyMarkers( documentText );
       }
       
       // ----------------------------------------- <  NEW ----
       // find the pipe offsets, store in a colon delimeterd string, and attach that list to the documentHeader for future retrieval.
       //
       String pipeOffsetList = findPipesInText(documentText);
       
       // ----------------------------------------- < NEW -----
       // Replace the pipes in the text with a space
       // 
       documentText = replacePipesInText( documentText );
     
     String documentTitle = "unknown";
     FileMetaData fileMetaData = GleanMetaDataFromFileName.getFileMetaData( documentId);
     String documentType  = fileMetaData.getDocumentType();
     String documentName  = fileMetaData.getDocumentName(); 
     String patientID     = fileMetaData.getPatientId();
     String referenceDate = fileMetaData.getDocumentDate();
     int           pageNo = fileMetaData.getPageNumber(); 
     String metaData      = "unknown";
 
    
     DocumentHeader documentHeader = VUIMAUtil.setDocumentHeader( jcas, documentId, documentName, documentType, documentTitle, metaData, patientID, referenceDate, pRecordId);
  
     
     // -------------------------------------------- < NEW -----
     // Stuff the offsets into the otherMetaData fields
     documentHeader.setOtherMetaData(pipeOffsetList);
     documentHeader.setOtherMetaDataFieldNames("segmentBoundaries");
     documentHeader.setOtherMetaDataFieldTypes("String");
   
     documentHeader.setPageNumber( pageNo);
     
     VUIMAUtil.setDocumentText(jcas, documentName,  documentType, referenceDate, documentText, this.setMetaDataHeader );
  
   } catch (Exception e ) {
     e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL,this.getClass(), "get","Something went wrong with fromDatabase " + e.toString());
   }
    
   
 } // end Method getNext() ----------------------- 


  // =================================================
/**
 * findPipesInText returns a string of where the offsets to pipes are
 * in the text
 * 
 * @param documentText
 * @return String  offset1:offset2 ...
*/
// =================================================
 private String findPipesInText(String pDocumentText) {
   
   StringBuffer buff = new StringBuffer(); 
   String returnVal = null;
   
   
   char[] docTextArray = pDocumentText.toCharArray();
   
   for ( int i = 0; i < docTextArray.length; i++ )
     if ( docTextArray[i] == '|')
       buff.append(  i + ":");
   
   if ( buff != null && buff.toString().trim().length() > 0 )
     returnVal = buff.toString().substring(0, buff.toString().length() -1);
   
  return returnVal;
 } // end Method findPipesInText() -----------------


//=================================================
/**
* replacePipesInText replaces pipes with spaces 
* in the text.  If you are using this method, the pipes
* in the text are segment markers. Not part of the text
*   example: 
*      End of sentence.| The next sentence. 
*          BECOMES
*      End of sentence.  The next sentence.
* 
* @param documentText
* @return String  
*/
//=================================================
private String replacePipesInText(String pDocumentText) {
 
   String returnVal = pDocumentText;
 
   returnVal = returnVal.replace('|',  ' ');
 
return returnVal;
} // end Method findPipesInText() -----------------

 

  // ----------------------------------------
	// Class Variables
	// ----------------------------------------
	
	
} // end Class FromPipedText() -------------
