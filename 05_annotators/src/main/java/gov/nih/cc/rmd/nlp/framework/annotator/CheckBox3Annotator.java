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
 * CheckBox3 turns lines that have both positive and negative asserted values into slotValues with checkbox3 as the id
 *   
 * @author  Guy Divita 
 * @created August 25, 2014
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.Line;
import gov.va.chir.model.SlotValue;
import gov.va.vinci.model.AssertedEvidence;
import gov.va.vinci.model.NegationEvidence;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class CheckBox3Annotator extends JCasAnnotator_ImplBase {
  
  @Override
  // -----------------------------------------
  /**
   * process finds check boxes from lines of documents
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
    this.performanceMeter.startCounter();
    
    // Loop thru lines
    List<Annotation> lines = UIMAUtil.getAnnotations(pJCas, Line.typeIndexID);
    
    if ( lines != null ) {
    
      for ( Annotation line : lines ) {
        
        if ( line != null  ) {
         
      //    List<Annotation> positiveAnnotations = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, AssertedEvidence.typeIndexID, line.getBegin(), line.getEnd(), true );
         //  List<Annotation> negativeAnnotations = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, NegationEvidence.typeIndexID, line.getBegin(), line.getEnd(), true );
         
          String lineTxt = line.getCoveredText();
          if ( lineTxt.contains("[") && lineTxt.contains("]")   )
             createCheckBox( pJCas, line );
                
        } // end if the line isn't null
        
        } //end loop thru lines
      
    } //end if there are  lines
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
     // throw new AnalysisEngineProcessException();
    }
  } // end Method process
      
      
  // =======================================================
  /**
   * createSlotValue a checkbox line 
   * 
   * @param pJCas
   * @param pLine
   * @param pQuestion
   * @param pAnswer
   * @return SlotValue
   */
  // =======================================================
  private SlotValue createCheckBox(JCas pJCas, Annotation pLine ) {
 
    // -------------------
    // create the slotValue
     SlotValue slotValue = new SlotValue(pJCas);
     slotValue.setBegin(pLine.getBegin());
     slotValue.setEnd( pLine.getEnd());
     slotValue.setId("CheckBox3");
    
     slotValue.addToIndexes();
    
     
     return slotValue;
    
  } // End Method createSlotValue() ======================
  



  
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
     
      this.performanceMeter = new ProfilePerformanceMeter( args, this.getClass().getSimpleName() );
      
      this.blankAnswerpattern =   Pattern.compile("_{2,}");
      this.answerPattern =  Pattern.compile("_+\\w+_+");

      
    } catch (Exception e ) {
      e.printStackTrace();
      
      System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
    

    
    
    initialize();
  } // end Method initialize() ---------

  //----------------------------------
    /**
     * initialize loads in the resources needed for slotValues. Currently, this involves
     * a list of known slot names that might show up.
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize() throws ResourceInitializationException {
      
    this.slotValueAnnotator = new SlotValueAnnotator();
    
  
  } // end Method initialize() --------------
  

 
  // -----------------------------------------
  // class Variables
  // -----------------------------------------
    SlotValueAnnotator slotValueAnnotator = null;
  
    private ProfilePerformanceMeter performanceMeter = null;
    private Pattern blankAnswerpattern =  null; 
    private Pattern answerPattern =  null; 


  
} // end Class SlotValue ---------------------
