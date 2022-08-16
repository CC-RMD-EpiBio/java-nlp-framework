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
 * SlotValuesInSections hunts down content headings that are followed by an additional colon on the same line
 * SectionName: slot:value
 * and transforms the part to the left of the contentHeading as it's own slot value
 *   
 * @author  Guy Divita 
 * @created May 3, 2019
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Delimiter;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.Line;
import gov.va.chir.model.Section;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.SlotValueDelimiter;
import gov.va.chir.model.Token;
import gov.va.chir.model.VAnnotation;


public class SlotValuesInSectionsAnnotator extends JCasAnnotator_ImplBase {
  


  private int annotationCounter = 0;



  @Override
  // -----------------------------------------
  /**
   * process finds slotValues in the whole document
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
    this.performanceMeter.startCounter();
   
    List<Annotation> contentHeadings = UIMAUtil.getAnnotations(pJCas, ContentHeading.typeIndexID);
    
    if ( contentHeadings != null && !contentHeadings.isEmpty())
      for ( Annotation contentHeading: contentHeadings ) {
        List<Annotation> contentHeadingLinez = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, Line.typeIndexID, contentHeading.getBegin(), contentHeading.getEnd() );
        if ( contentHeadingLinez != null && !contentHeadingLinez.isEmpty() ) {
          Annotation contentHeadingLine = contentHeadingLinez.get(0);
         
          processContentHeadingLine( pJCas, contentHeadingLine , contentHeading);
        }
      }
      
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
     // throw new AnalysisEngineProcessException();
    }
  } // end Method process
      
  // =================================================
  /**
   * processContentHeadingLine looks for two colons in the the line
   * if found, creates a slot:value around the second colon
   * 
   * @param pJCas
   * @param contentHeadingLine
  */
  // =================================================
  private final void processContentHeadingLine(JCas pJCas, Annotation pContentHeadingLine, Annotation pContentHeading) {
    
    char [] cbuff = pContentHeadingLine.getCoveredText().toCharArray();
    int offset = pContentHeadingLine.getBegin();
    int colonCtr = 0;
    int firstColon = -1;
    int lastColon = -1;
    for ( int i = 0; i < cbuff.length; i++ ) {
      if ( cbuff[i]==':') {
        colonCtr++;
        if ( colonCtr == 1)
          firstColon = i;
        if ( colonCtr == 2)
          lastColon = i;
      }
    }
    
    
    if ( colonCtr == 2 && !(offset +lastColon +1 >= pContentHeadingLine.getEnd()) ) {
      
      createSlotValueFromColonContent( pJCas, offset +firstColon, offset +lastColon, pContentHeadingLine.getEnd(), pContentHeading);
    }
    
  } // end Method processContentHeadingLine() --------------

  // =================================================
  /**
   * createSlotValueFromColonContent 
   * 
   * @param pJCas
   * @param pFirstColon
   * @param pLastColon
   * @param pEndOfLine
  */
  // =================================================
  private final void createSlotValueFromColonContent(JCas pJCas, int pFirstColon, int pLastColon, int pEndOfLine, Annotation pContentHeading) {
   
    
 
      SlotValue     slotValue = new SlotValue(pJCas);
     
      slotValue.setBegin(pFirstColon + 1);
      slotValue.setEnd(pEndOfLine);
      String buff = slotValue.getCoveredText();
      slotValue.setDisplayString( buff );
      
      slotValue.setId( this.getClass().getCanonicalName() + ":createSlotValueFromColonContent_" +  + this.annotationCounter++);
      

      ContentHeading contentHeader = createContentHeading( pJCas, pFirstColon +1, pLastColon, slotValue, pContentHeading);
      
      
      contentHeader.setParent(slotValue);
      String contentHeaderString = contentHeader.getCoveredText();
      
      if ( contentHeaderString == null || contentHeaderString.trim().length() == 0 ) {
        GLog.println(GLog.ERROR_LEVEL,"Something is wrong here empty content heading of a slotValue " );
       // throw new RuntimeException();
      }
      slotValue.setContentHeaderString(contentHeaderString);
      slotValue.setHeading(contentHeader);
      slotValue.setProcessContent( true);
      
      // ------------------------
      // Delimiter
      
        Delimiter delimiter = createDelimiter( pJCas, pLastColon, slotValue );
        slotValue.setDelimiter(delimiter.getCoveredText());
     
        
        if ( pLastColon + 1 >= pEndOfLine ) 
          System.err.println("Somthing is wrong ehre");
        
        DependentContent contentValue = createDependentContent(pJCas, pLastColon + 1, pEndOfLine, slotValue );
        contentValue.setParent( slotValue);
        
        slotValue.setContentString( contentValue.getCoveredText());
        slotValue.setDependentContent( contentValue);
     
   
       slotValue.addToIndexes();
      
  } // end Method createSlotValueFromColonContent() ----------
     
  // =================================================
  /**
   * createDependentContent
   * 
   * @param pJCas
   * @param pLastColon
   * @param pEndOfLine
   * @param slotValue
   * @return
  */
  // =================================================
  private final DependentContent createDependentContent(JCas pJCas, int pLastColon, int pEndOfLine, SlotValue slotValue) {
    
    DependentContent  content = new DependentContent(pJCas);
    
    content.setBegin( pLastColon + 1);
    content.setEnd  ( pEndOfLine );
  
    content.setId( this.getClass().getCanonicalName() + ":createDependentContent_" + this.annotationCounter++);
    
    
    // VUIMAUtil.setProvenance( pJCas, content, this.getClass().getName() );
  
     content.addToIndexes();
    
    return content;
  } // end Method createDependentContent() ------------

     
  // =================================================
  /**
   * createDelimiter
   * 
   * @param pJCas
   * @param pLastColon
   * @param pSlotValue
   * @return Delimiter
  */
  // =================================================
  private final Delimiter createDelimiter(JCas pJCas, int pLastColon, SlotValue pSlotValue) {
   
    SlotValueDelimiter delimiter = new SlotValueDelimiter( pJCas);
    delimiter.setBegin( pLastColon);
    delimiter.setEnd(pLastColon + 1);
    
    delimiter.setId( this.getClass().getCanonicalName() + ":createDependentContent_"  + this.annotationCounter++);
    // VUIMAUtil.setProvenance( pJCas, delimiter, this.getClass().getName() );
    delimiter.setParent(pSlotValue );
    delimiter.setProcessMe(false);
    delimiter.addToIndexes();
    
    return delimiter;
  } // end Method createDelimiter() -----------------

     
    // =================================================
  /**
   * createContentHeading 
   * 
   * @param pJCas
   * @param pFirstColon
   * @param pLastColon
   * @param pSlotValue
   * @param pSectionName
   * @return ContentHeading
  */
  // =================================================
  private final ContentHeading createContentHeading(JCas pJCas, int pFirstColon, int pLastColon, Annotation pSlotValue, Annotation pSection) {
    
    ContentHeading contentHeader = new ContentHeading(pJCas);
    contentHeader.setBegin( pFirstColon + 1);
    contentHeader.setEnd( pLastColon  );
    contentHeader.setId( this.getClass().getCanonicalName() + ":createContentHeading"  + this.annotationCounter++);
    contentHeader.setProcessMe(true);
    contentHeader.setParent( pSlotValue);
    contentHeader.setSection((VAnnotation ) pSection);
    contentHeader.setSectionName( ((VAnnotation )pSection).getSectionName() );
    contentHeader.addToIndexes();
    
    return contentHeader;
    

  } // end Method createContentHeading() -----

  
  



//----------------------------------
 /**
  * destroy
 * 
  **/
 // ----------------------------------
 public void destroy() {
   this.performanceMeter.writeProfile( this.getClass().getSimpleName());
 }
 
 
 //----------------------------------
 /**
  * initialize loads in the resources needed for slotValues. Currently, this involves
  * a list of known slot names that might show up.
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
    
   }
   initialize( args);
  
 } // end Method initialize() ---------

 //----------------------------------
 /**
  * initialize 
  *
  * @param pArgs
  * 
  **/
 // ----------------------------------
 public void initialize(String pArgs[]) throws ResourceInitializationException {
   
   this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
   

} // end Method initialize() --------------
    
 
  // -----------------------------------------
  // class Variables
  // -----------------------------------------

//---------------------------------------
//Global Variables
//---------------------------------------
  private ProfilePerformanceMeter  performanceMeter = null;
  
  
} // end Class SlotValueRepairAnnotator ---------------------
