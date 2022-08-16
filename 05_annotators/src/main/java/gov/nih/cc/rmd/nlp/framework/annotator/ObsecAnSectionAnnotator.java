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
 * SectionAnnotator identifies sections within a medical document.
 * 
 * This is a wrapper around Le-Thuy's Sectionizer.  There is
 * a correcting mechanism around it to only mark geographic Sections
 * as Sections and ContentHeadings.  Items that don't fit the
 * geographic notion of a section are created with CEMHeaders, and CEMS.
 * (Clinical Element Models)
 *
 *
 * @author  Guy Divita 
 * @created Mar 1, 2015
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Section;
// import gov.va.ltran.iOBSECANN.mainroutine.SectionizerNew;
import gov.va.ltran.medrecords.Annotator.OBSecAn;
import gov.va.vinci.model.CEM;
import gov.va.vinci.model.CEMHeader;
// import gov.va.ltran.medrecords.Annotator.OBSecAn;
// import gov.nih.cc.rmd.model.CEM;
// import gov.nih.cc.rmd.model.CEMHeader;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

public class ObsecAnSectionAnnotator extends JCasAnnotator_ImplBase {

  @Override
  // -----------------------------------------
  /**
   * process takes tokens and whitespace tokens as input and returns
   * sentences
   *
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
    
    try {
    this.performanceMeter.startCounter();
    String text = pJCas.getDocumentText();
    String documentType = VUIMAUtil.getDocumentType(pJCas);
    String documentTitle = VUIMAUtil.getDocumentTitle( pJCas);
    
    String vttMarkups = null;
    if ( text != null )
      try {
    
     // vttMarkups = this.sectionizer.annotateSections(text, documentTitle);
      vttMarkups = this.sectionizer.annotateSections(text );
    	     
    	  
      if (vttMarkups != null) {

        String[] rows = U.split(vttMarkups, "\n");
        if ( rows != null ) {
          for (String markUpRow : rows) {
            if (markUpRow != null && markUpRow.trim().length() > 0 && markUpRow.contains("|SECTION|")) {
              try {
                convertVttRowIntoSection( pJCas, markUpRow);
              } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Issue with parsing row \n" + markUpRow + "\n" + e.toString() );
              }
            } // end if there is a valid row
          } // end loop through the vtt markup rows
        } // end if there are rows
      } // end if there are any vtt Markups
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with sectionizing " + e.getMessage() ;
      System.err.println( msg );
      // throw new AnalysisEngineProcessException ();
    }
    
    this.performanceMeter.stopCounter();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
     //  throw new AnalysisEngineProcessException();
    }
  } // end Method process() ------------------

 



  // =======================================================
  /**
   * convertVttRowIntoSection 
   *   converts vtt markup rows in the format that comes back from
   *   the ObSecAn.anannotateSections() method
   *   
   *   sectionNameOffsetBegin|sectionNameLength|Label|nestingLevel|EMPTY|sectionName:sectionId:sectionOffsetBegin-sectionOFfsetEnd
   *   
   *   where Label is SECTION|PROP
   *   and nestingLevel is how embedded this section is within another encompassing section.
   *   
   *   There are two side effects of this method - UIMA Section and ContentHeading annotations are made.
   * 
   * @param pJCas
   * @param markUpRow
   */
  // =======================================================
  private void convertVttRowIntoSection(JCas pJCas, String markUpRow) {
   
    
    String[] cols = U.split(markUpRow);

    if (cols[2].contains("SECTION")) {
      
      int sectionNameOffsetBegin = Integer.parseInt(cols[0].trim());
      int sectionNameOffsetEnd = sectionNameOffsetBegin + Integer.parseInt(cols[1].trim()) ;
      String sectionNesting = cols[3];
      // cols[4] is empty all the time
      String sectionNamePlus = cols[5];
      String buff[] = U.split(sectionNamePlus, ":");
      
      if (buff == null) {
       // System.err.println("bad row |" + markUpRow + "|" + sectionNamePlus + "|");
        return;
      }
      String sectionName = buff[0];
      if ( sectionName != null && sectionName.length() >= 100 ) sectionName = sectionName.substring(0,99);
     
      String sectionIndexId = "ObsecAn_" + this.sectionIdCounter++ + ":"+ buff[1];
      String sectionOffsets = buff[2];
      String sectionOffsetsBuff[] = U.split(sectionOffsets, "-");
      if (sectionOffsetsBuff == null || sectionOffsetsBuff.length < 2) {
      //  System.err.println("bad row |" + markUpRow + "|" + sectionOffsets + "|");
        return;
      }
      int sectionOffsetBegin = 0;
      int sectionOffsetEnd = 0;
      try {
        sectionOffsetBegin = Integer.parseInt(sectionOffsetsBuff[0]);
        sectionOffsetEnd = Integer.parseInt(sectionOffsetsBuff[1]) + 2; // <---- not sure why off by two here.
      } catch (Exception e ) {
        //  System.err.println("bad row |" + markUpRow + "|" + sectionOffsets + "|");
        return;
      }

      try {
      String sectionContent = pJCas.getDocumentText().substring(sectionOffsetBegin, sectionOffsetEnd );
      if ( sectionContent != null && sectionContent.startsWith(":")) {
        sectionOffsetBegin++;
      }
      } catch (Exception e){};
      
      // -----------------------------
      // Must constrain the sections to geographic notion of a section
      
      if ( geogrpahicSectionDefinition( pJCas, 
                                        sectionNameOffsetBegin, 
                                        sectionNameOffsetEnd,
                                        sectionOffsetBegin,
                                        sectionOffsetEnd)) {
        // -----------------------------
        // Create ContentHeading
        ContentHeading contentHeading = createContentHeading(pJCas, sectionNameOffsetBegin, sectionNameOffsetEnd, sectionName);

        // ---------------------------
        // Create Section zone
        createSection(pJCas, sectionOffsetBegin, sectionOffsetEnd, sectionIndexId, sectionName, contentHeading, sectionNesting);
        // System.err.println("created " + sectionName + "|" + sectionOffsetBegin + "|" + sectionOffsetEnd);
    
      } else {
        // -----------------------------
        // Create ContentHeading
        CEMHeader cemHeading = createCEMHeading(pJCas, sectionNameOffsetBegin, sectionNameOffsetEnd, sectionName);

        // ---------------------------
        // Create Section zone
        createCEM(pJCas, sectionOffsetBegin, sectionOffsetEnd, sectionIndexId, sectionName, cemHeading, sectionNesting);
       
      }
      
      } else {
     // System.err.println("What's this? " + row);
    }
    // End Method convertVttRowIntoSection() ======================
  }





  // =======================================================
  /**
   * geogrpahicSectionDefinition returns true if the following heuristics are true
   * 
   *    does the sectionName start on a line
   *       AND
   *    does the section have more than one line  OR
   *    does the content after the sectionName have sentence ending punctuation at the end of the line 
   * 
   * @param pJCas
   * @param sectionNameOffsetBegin
   * @param sectionNameOffsetEnd
   * @param sectionOffsetBegin
   * @param sectionOffsetEnd
   * @param sectionName 
   * @return boolean
   */
  // =======================================================
  private boolean geogrpahicSectionDefinition(JCas pJCas, 
                                              int sectionNameOffsetBegin, 
                                              int sectionNameOffsetEnd,
                                              int sectionOffsetBegin, 
                                              int sectionOffsetEnd 
                                              ) {
    boolean returnVal = false;
  
    if ( sectionNameBeginsLine( pJCas, sectionNameOffsetBegin) )
       if ( getNumberOfNewLinesInSection( pJCas, sectionOffsetBegin, sectionOffsetEnd) > 0  ||
            sectionEndWithPunctuation( pJCas, sectionOffsetBegin, sectionOffsetEnd) ) 
           if ( sectionNameOffsetEnd != sectionNameOffsetBegin)
       
             returnVal = true;
         
    
    
    return returnVal;
  } // End Method geogrpahicSectionDefinition() ======================
  


  // =======================================================
  /**
   * sectionNameBeginsLine returns true if the sectionName
   * is the first non-whitespace token since the last newline.
   * 
   * @param pJCas
   * @param sectionNameOffsetBegin
   * @return boolean
   */
  // =======================================================
  private boolean sectionNameBeginsLine(JCas pJCas, int sectionNameOffsetBegin) {
     
    boolean returnValue = false;
    String documentText = pJCas.getDocumentText();
    
    if ( documentText != null && sectionNameOffsetBegin < documentText.length() ) {
      String buff = documentText.substring( 0, sectionNameOffsetBegin );
      if ( buff != null ) {
        int nl = buff.lastIndexOf('\n');
        if ( nl > 0 ) {
          String beginingOfLine = buff.substring(nl);
          if ( beginingOfLine != null)
            if ( beginingOfLine.trim().length() == 0 ) 
              returnValue = true;
        }
      }
    }
    return returnValue;
    // End Method sectionNameBeginsLine() ======================
  }





  // =======================================================
  /**
   * getNumberOfNewLinesInSection returns the number of newlines
   * in the zone
   * 
   * @param pJCas
   * @param pSectionOffsetBegin
   * @param pSectionOffsetEnd
   * @return int
   */
  // =======================================================
  private int getNumberOfNewLinesInSection(JCas pJCas, int pSectionOffsetBegin, int pSectionOffsetEnd) {
   
    int numberOfNewLines = 0;
    int sectionOffsetEnd = 0;
    
    String documentText = pJCas.getDocumentText();
    
    if ( pSectionOffsetBegin < documentText.length() ) {
      if ( pSectionOffsetEnd >= documentText.length()) {
        sectionOffsetEnd = documentText.length() -1;
      } else {
        sectionOffsetEnd = pSectionOffsetEnd;
      }
    
      String sectionZone = null;
      
      try {
        sectionZone = documentText.substring(pSectionOffsetBegin, sectionOffsetEnd);
   
        char[] ca = sectionZone.toCharArray();
    
        for (int i = 0; i < ca.length; i++)
          if ( ca[i] == '\n' )  
            numberOfNewLines++;
      } catch (Exception e) { e.printStackTrace(); System.err.println("Issue figuring out the number of lines in the section " + e.toString());}
    } //end if the offsets are fine
    
    return numberOfNewLines;
  } // End Method getNumberOfNewLinesInSection() ======================
  





  // =======================================================
  /**
   * sectionEndWithPunctuation (.!?;)
   * 
   * @param pJCas
   * @param pSectionOffsetBegin
   * @param pSectionOffsetEnd
   * @return boolean
   */
  // =======================================================
  private boolean sectionEndWithPunctuation(JCas pJCas, int pSectionOffsetBegin, int pSectionOffsetEnd) {
    boolean returnVal = false;
    
    try {
      String documentText = pJCas.getDocumentText();
      String sectionZone = null;
      int sectionOffsetBegin = pSectionOffsetBegin;
      int sectionOffsetEnd = pSectionOffsetEnd;
      if (documentText != null &&  sectionOffsetBegin < documentText.length() ) {
     
        if ( sectionOffsetEnd >= documentText.length() ) 
          sectionOffsetEnd = documentText.length() ;
        sectionZone = documentText.substring(sectionOffsetBegin, sectionOffsetEnd);
    
      
        String buff = sectionZone.trim();
        if ( buff != null && buff.length() > 2 ) {
          char lastChar = buff.charAt(buff.length() -1);
          if (lastChar == '.' ||
              lastChar == '!' ||
              lastChar == '?' ||
              lastChar == ';' ) 
            returnVal = true;
        }
      } 
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("In the sectionEndWithPunctuation issue :" + e.toString());
    }
    
    
    return returnVal;
  } // End Method sectionEndWithPunctuation() ======================
  





  // =======================================================
  /**
   * createSection
   * 
   * @param pJCas
   * @param sectionOffsetBegin
   * @param sectionOffsetEnd
   * @param sectionIndexId
   * @param sectionName
   * @param contentHeading
   * @param sectionNesting
   * @return Section
   */
  // =======================================================
  private Section createSection(JCas pJCas, 
                                int sectionOffsetBegin, 
                                int sectionOffsetEnd, 
                                String sectionIndexId,
                                String sectionName, 
                                ContentHeading contentHeading, 
                                String sectionNesting) {
    
    Section statement = new Section(pJCas);
    
    String docText = pJCas.getDocumentText();
    if ( sectionOffsetEnd >= docText.length())
      sectionOffsetEnd = docText.length() -1;
    
    
    statement.setBegin(sectionOffsetBegin);
    statement.setEnd(sectionOffsetEnd);
    String _sectionIndexId = "ObsecAn_Section" + this.sectionIdCounter++ + ":"+ sectionIndexId;
    statement.setId(_sectionIndexId);
    statement.setProcessMe(true);
    String[] otherInfo = new String[1];
    otherInfo[0] = sectionNesting;
    StringArray vv = UIMAUtil.string2StringArray(pJCas, otherInfo);
    statement.setOtherFeatures(vv);
    statement.setContentHeaderString(sectionName);
    statement.setSectionHeading(contentHeading);
    statement.setSectionName(sectionName);
    statement.addToIndexes();
    
    return statement;
  } // End Method createSection() ======================
  


//=======================================================
 /**
  * createCEM
  * 
  * @param pJCas
  * @param sectionOffsetBegin
  * @param sectionOffsetEnd
  * @param sectionIndexId
  * @param sectionName
  * @param contentHeading
  * @param sectionNesting
  * @return Section
  */
 // =======================================================
 private CEM createCEM(JCas pJCas, 
                               int sectionOffsetBegin, 
                               int sectionOffsetEnd, 
                               String sectionIndexId,
                               String sectionName, 
                               CEMHeader contentHeading, 
                               String sectionNesting) {
   
   String docText = pJCas.getDocumentText();
   if ( sectionOffsetEnd >= docText.length() )
     sectionOffsetEnd = docText.length() -1;
   
   CEM statement = new CEM(pJCas);
   
   statement.setBegin(sectionOffsetBegin);
   statement.setEnd(sectionOffsetEnd);
   String _cemIndexId = "ObsecAn_CEM" + this.cemIdCounter++;
   statement.setId(_cemIndexId);
   
   statement.setNesting( sectionNesting);
   statement.setHeader(contentHeading);
   statement.setName(sectionName);
   statement.addToIndexes();
   
   return statement;
 } // End Method createSection() ======================
 



  // =======================================================
  /**
   * createContentHeading 
   * 
   * @param pJCas
   * @param sectionNameOffsetBegin
   * @param sectionNameOffsetEnd
   * @param sectionName
   * @return ContentHeading
   */
  // =======================================================
  private ContentHeading createContentHeading(JCas pJCas, int sectionNameOffsetBegin, int sectionNameOffsetEnd, String sectionName) {
   
   ContentHeading  statement = new ContentHeading(pJCas);
   
   statement.setBegin( sectionNameOffsetBegin);
   statement.setEnd(   sectionNameOffsetEnd);
   statement.setProcessMe(false);
   statement.setSectionName(sectionName);
   String _contentHeadingIndexId = "ObsecAn_ContentHeading" + this.contentHeadingIdCounter++ ;
   statement.setId(_contentHeadingIndexId);
   statement.setProcessMe(true);
   statement.addToIndexes();
   
   return statement;
   
  } // End Method createContentHeading() ======================
  


  // =======================================================
  /**
   * createCEMHeading 
   * 
   * @param pJCas
   * @param sectionNameOffsetBegin
   * @param sectionNameOffsetEnd
   * @param sectionName
   * @return ContentHeading
   */
  // =======================================================
  private CEMHeader createCEMHeading(JCas pJCas, int sectionNameOffsetBegin, int sectionNameOffsetEnd, String sectionName) {
   
   CEMHeader  statement = new CEMHeader(pJCas);
   
   statement.setBegin( sectionNameOffsetBegin);
   statement.setEnd(   sectionNameOffsetEnd);
  
   statement.setName(sectionName);
   String _cemHeadingIndexId = "ObsecAn_CEMHeading" + this.cemHeaderIdCounter++ ;
   statement.setId(_cemHeadingIndexId);
   statement.addToIndexes();
   
   return statement;
   
  } // End Method createContentHeading() ======================
  

  // ----------------------------------
  /**
   * destroy
  * 
   **/
  // ----------------------------------
  public void destroy() {
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
  }


  // ----------------------------------
  /**
   * initialize loads in the resources needed for sectionTokenization.
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext)  throws ResourceInitializationException {

   
    String[] args = null;
    try {
      args                 = (String[]) aContext.getConfigParameterValue("args");  

      initialize( args );
      
    } catch (Exception e ) {
      System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
  } // end Method initialize() -------
    
    
 // ----------------------------------
    /**
     * initialize loads in the resources needed for sectionTokenization.
     * 
     * @param pArgs
     * 
     **/
    // ----------------------------------
    public void initialize(String[] pArgs)  throws ResourceInitializationException {

    try {
    
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    
    
    this.sectionizer = new OBSecAn();
    // this.sectionizer = new SectionizerNew( pArgs);
  } catch (Exception e) {
    e.printStackTrace();
    String msg = "Issue with kicking off the sectionizer  " + e.getMessage();
    System.err.println( msg );
    throw new ResourceInitializationException();
  } 
  
  
  } // end Method initialize() --------------


 
// ---------------------------------------
// Global Variables
// ---------------------------------------
 private int annotationCounter = 0; // new Section Counter.
  private int sectionIdCounter = 0;
 private int contentHeadingIdCounter = 0;
 private int cemIdCounter = 0;
 private int cemHeaderIdCounter = 0;
//  private SectionizerNew sectionizer = null;
 private OBSecAn sectionizer = null;
 ProfilePerformanceMeter performanceMeter = null;
 private String  outputDir = null;

 
} // end Class SectionTokenizerSimple() -----------
