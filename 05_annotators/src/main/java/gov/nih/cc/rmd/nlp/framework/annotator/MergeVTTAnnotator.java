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
 * Merge merges annotations from two corpus sources.
 * This annotator will create a new corpus, with
 * SystemA annotations and SystemB annotations.
 * 
 * It presumes that the names of the files are the same
 * and that it's looking for SystemA and SystemB annotations.
 *       
 * @author  Guy Divita 
 * @created March 22, 2012
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Section;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;


public class MergeVTTAnnotator extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  /**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    int lastLineSeen = 0;
    
    try {
    // ------------------------------------
    // Gather System B Labels
    // ------------------------------------
    String fileName =  VUIMAUtil.getDocumentId(pJCas);
    
    if ( fileName == null )
      return;
    
   if ( fileName.contains(".txt")) {
     int chopOff = fileName.indexOf(".txt");
     fileName = fileName.substring(0,chopOff );
   }
    
    String systemBFileName = this.vttInputDir + "/" + fileName + ".vtt";
    System.err.println(systemBFileName);
    
    File aFile = new File( systemBFileName);
    
    if ( !aFile.exists() ) {
      System.err.println(systemBFileName + " not found returning."); 
      return;
    }
    
    
    // ------------------------
    // Read in the vtt file
    String[] vttContents = U.readFileIntoStringArray(aFile.getAbsolutePath());
    
    // ------------------------
    // retrieve the markups
    List<String>markups = getVTTMarkups(vttContents);
    String vttText = getVTTText( vttContents);
    String[] vttTextRows = U.split( vttText, "\n");
    
   
    // ------------------------
    // Convert the markups to annotations
    if ( markups != null ) {
      
      
      // ------------------------
      // create a String[] rows 
      String[] rows = U.split(pJCas.getDocumentText(), "\n");
      
      
      for ( String markup  : markups) {
       
        if ( markup.contains("|SECTION|")) 
          lastLineSeen = createSectionAnnotation( pJCas, markup, lastLineSeen, rows, vttText, vttTextRows);
      }
    }
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with converting vtt markup to cas annotaiton " + e.toString();
      System.err.println( msg );
         
    }
          
  } // end Method Process() -------------------------------- 
  
  
//=======================================================
 /**
  * getVTTText retrieves the markups section of a vtt document
  * 
  * @param vttContents
  * @return String of vtt text rows
  */
 // =======================================================
 private String getVTTText(String[] pVttContents) {
   
   // ----------------------------------------------
   // read down to the <Markups Information> section
   int i = 0;
   int startTextLine = 0;
   int endTextLine = 0;
   for (; i < pVttContents.length; i++ ) 
     if ( pVttContents[i].startsWith("#<Text Content>")) {
       startTextLine = i;
       break;
     }
   
   for (; i < pVttContents.length; i++ )
     if ( pVttContents[i].startsWith("#<TagsConfiguration>") ) {
       endTextLine = i-2;
       break;
     }
   
   
   // ---------------------
   // 
   int numberOfLines = endTextLine - startTextLine;
   StringBuffer buff = new StringBuffer();
   for (int x = 0; x < numberOfLines; x++) {
     buff.append( pVttContents[ startTextLine + x]);
     if ( x < numberOfLines -1)
       buff.append("\n");
   
   }
   
   return buff.toString();
 } // End Method getVTTText() ======================
  
  // =======================================================
  /**
   * getVTTMarkups retrieves the markups section of a vtt document
   * 
   * @param vttContents
   * @return List<String>  of markups
   */
  // =======================================================
  private List<String> getVTTMarkups(String[] pVttContents) {
    ArrayList<String> markups = new ArrayList<String>();
    
    // ----------------------------------------------
    // read down to the <Markups Information> section
    int i = 0;
    for (; i < pVttContents.length; i++ ) 
      if ( pVttContents[i].startsWith("#<MarkUps Information>")) 
        break;
    
    // ---------------------
    // Assuming we are two lines away from the markups, start there
    
    for (i = i+2; i < pVttContents.length; i++) {
      if ( pVttContents[i].trim().length() > 1 )   // <------------------ there are blank lines in the output
        markups.add( pVttContents[i]);
    }
    
    return markups;
  } // End Method getVTTMarkups() ======================
  



  // end Method process() ----------------
  
 

  // -----------------------------------------
  /**
   * createAnnotation adds this annotation to the other cas
   * 
   * @param pJCas
   * @param pMarkup
   * @param pLastLineSeen
   * @param pRows
   * @param pVTTText
   * @param pVttTextRows
   * @return int          lastLineSeen
   * @throws Exception 
   */
  // -----------------------------------------
  private int createSectionAnnotation(JCas pJCas, String pMarkup , int pLastLineSeen, String[] pRows, String pVttText, String[] pVTTTextRows) throws Exception {
    
    // extract information from the markup
    String cols[] = U.split(pMarkup);
    int sectionNameBeginOffset = Integer.parseInt(cols[0].trim()) ;
    int   sectionNameEndOffset = sectionNameBeginOffset +  Integer.parseInt(cols[1].trim()) ;
    String   label = cols[2].trim();
    String  sectionLevel = cols[3].trim();
    String metaData = cols[5];
    
    
    // ----------------
    // The rest of the markup is metadata - not relevant here
    //    If the section has a section header, find it
    String metaDataCols[] = U.split(metaData, ":");
    String sectionName = metaDataCols[0].trim();
    
    
    // ----------------
    // Figure out what line number this heading is on to fix the offset
    int lastLineSeen = pLastLineSeen;
    int[] lastLineSeenAndLineNumber = getLineNumber(pJCas, sectionName, pLastLineSeen, pRows);
    int lineNumber = lastLineSeenAndLineNumber[1];
    lastLineSeen   = lastLineSeenAndLineNumber[0]; 
    if ( lineNumber == -1) {
      System.err.println("-----------------------------------------  not found ???? " + sectionName + "--------------");
      lineNumber = 0; // <---- zero out if the sectionName was not found 
    }
    
    
  
    
    String sectionOntologyId = metaDataCols[1].trim();
    String sectionZoneOffsets = metaDataCols[2].trim();
    String sectionOffsets[] = U.split( sectionZoneOffsets, "-");
    int sectionOffsetBegin =  Integer.parseInt(sectionOffsets[0].trim());  // <---- this is where the content begins, not including the section name 
    int sectionOffsetEnd   =  Integer.parseInt(sectionOffsets[1].trim()) ;    
    
    // --------------------------------------
    // From the vtt text, figure out how many lines the section contains
    //  This involves taking the vtt offsets for the lines
    int numberOfLinesInSection = getNumberOfLinesFrom(sectionName, sectionOffsetBegin, sectionOffsetEnd, pVttText );
    
    
    
    // -------------------
    // Create a content heading for the section name
    ContentHeading contentHeading = new ContentHeading(pJCas);
    contentHeading.setBegin(sectionNameBeginOffset + lineNumber);
    contentHeading.setEnd(sectionNameEndOffset + lineNumber);
    contentHeading.addToIndexes();
    
    
    // ---------------
    // Map the label to a pre-existing label from the type descriptor i.e. Section -> gov.va.chir.model.Section
    Section statement = new Section(pJCas);
    statement.setBegin(sectionNameBeginOffset + lineNumber);
    statement.setEnd(  sectionOffsetEnd + lineNumber + (numberOfLinesInSection));
    
    statement.setSectionHeading((ContentHeading) contentHeading);
    statement.setContentHeaderString( sectionName);
    StringArray otherFeatures = new StringArray(pJCas, 1);
    otherFeatures.set(0, sectionOntologyId);
    otherFeatures.addToIndexes();
    
    statement.setOtherFeatures(otherFeatures);
   
    statement.setSectionName( sectionName);
    statement.setId("Section_" + this.annotationCounter++);
    statement.addToIndexes();
    
    
    
   
     return lastLineSeen;
  
  } // end createAnnotation() ----------------

  // =======================================================
  /**
   * getNumberOfLinesFrom retrieves the number of newlines in 
   * the section based on the offsets.  
   * 
   * Note this method takes the offsets from the vtt text and 
   * uses the vtt text, not the cas text to compute.
   * 
   * @param sectionOffsetBegin
   * @param sectionOffsetEnd
   * @param pVttText
   * @return int
   */
  // =======================================================
  private int getNumberOfLinesFrom(String pSectionName, int sectionOffsetBegin, int sectionOffsetEnd, String pVttText) {
   
    int numberOfLines = 0;
    try {
    String sectionText = pVttText.substring( sectionOffsetBegin, sectionOffsetEnd -1);
    
    String[] rows = U.split(sectionText, "\n");
    numberOfLines = rows.length;
    } catch (Exception e) {
      System.err.println("What wrong offsets? for " + pSectionName +  " [" + sectionOffsetBegin + "|" + sectionOffsetEnd + "]");
    }
    
    return numberOfLines;
    
  } // End Method getNumberOfLinesFrom() ======================
  


  // =======================================================
  /**
   * getLineNumber returns the lineNumber for the given string.
   * 
   * @param pJCas
   * @param sectionName
   * @return int[]     lastLineSeen|lineNumber     if the line number = -1, the line was not seen
   */
  // =======================================================
  private int[] getLineNumber(JCas pJCas, String pPattern, int pLastLineSeen, String[] pRows) {
  
  
    int returnVal[] = new int[2];
    returnVal[0] = pLastLineSeen;
    returnVal[1] = -1;
    
    
    for (int lineNumber = pLastLineSeen; lineNumber < pRows.length; lineNumber++  ) {
      if ( pRows[lineNumber].indexOf(pPattern) > -1) {
        returnVal[0] = lineNumber;
        returnVal[1] = lineNumber;
        break;
      }
        
    }
   
    return returnVal;
  } // End Method getLineNumber() ======================
  


  //----------------------------------
  /**
   * initialize loads in the resources needed for phrase chunking. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
  

    String[] args = null;
    try {
      args                 = (String[]) aContext.getConfigParameterValue("args");  

    } catch (Exception e ) {
      System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
 
    this.vttInputDir     = U.getOption(args, "--vttInputDir=", "./");

  
  } // end Method initialize() -------



  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  private int  annotationCounter = 0;
  private String vttInputDir = null;
 
  
} // end Class MetaMapClient() ---------------
