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
 * BTRIS_RedactionAnnotator find the redaction mentions
 * and turns them into Redacted labels
 * 
 * Example [ First Name i=2018 ]     <--  all of that would be within one WordToken
 *
 *
 * @author  Guy Divita 
 * @created November 7 2019
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.shapes;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.model.Person;
import gov.nih.cc.rmd.framework.model.Redacted;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Token;
import gov.va.chir.model.WordToken;
import gov.va.chir.model.VAnnotation;



public class BTRIS_RedactionAnnotator extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  /**
   * process creates Redaction annotations around 
   * BTRIS redacted mentions.
   * 
   * This is to make a de-identified name look like a single token for machine learning work.
   * 
   * Example [ First Name = 2018 ]     <--  all of that would be within one WordToken
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
    this.performanceMeter.startCounter();

    
    String buff = pJCas.getDocumentText();
    
   
    process(pJCas, buff, BTRIS_LAST_NAME  );
    process(pJCas, buff,  BTRIS_FIRST_NAME );
    process(pJCas, buff,  BTRIS_FIRST_NAME2 );
    process(pJCas, buff, BTRIS_ORGANIZATION );
    process(pJCas, buff, BTRIS_organization );
    process(pJCas, buff, BTRIS_ID );
    process(pJCas, buff, BTRIS_id );
    
    
    } catch ( Exception e ) {
      
    }
    
    this.performanceMeter.stopCounter();
    } // end Method process() ---------------
    
    
  // =================================================
  /**
   * process 
   * 
   * @param pJCas
   * @param pDocText
   * @param pRedactionPattern
  */
  // =================================================
    private void process(JCas pJCas, String pDocText, String pRedactionPattern) {
    
      int lastPtr = -1;
      int deidPtr = -1;
    
      while ( (deidPtr = pDocText.indexOf( pRedactionPattern, lastPtr) ) >= 0 ) {
        int endbracket = pDocText.indexOf("]", deidPtr +1 );
       createRedactedMention( pJCas, deidPtr, endbracket);
       lastPtr = endbracket+1;
    }
    
  } // end Method addBTISNewLines() ------------------


  // =================================================
/**
 * createRedactedMention creates a word token
 * 
 * @param pJCas
 * @param pBegin
 * @param pEnd
*/
// =================================================
private final void createRedactedMention(JCas pJCas, int pBegin, int pEnd ) {
 
  
  
  Redacted statement = new Redacted( pJCas);
  statement.setBegin(pBegin);
  statement.setEnd( pEnd);
  statement.setReplaceWithClass( true);
  statement.setId( "BTRIS_RedactonAnnotator_" + annotationCtr++);
  statement.addToIndexes();
  
  
  
} // end Method createWordToken() -------------


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
   * initialize loads in the resources. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
     
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  

        initialize(args);
        
      } catch (Exception e ) {
        String msg = "Issue in initializing class " + this.getClass().getName() + " " + e.toString() ;
        GLog.println(GLog.ERROR_LEVEL, msg);     // <------ use your own logging here
        throw new ResourceInitializationException();
      }
      
   
      
  } // end Method initialize() -------
  
  //----------------------------------
  /**
   * initialize initializes the class.  Parameters are passed in via a String
   *                array  with each row containing a --key=value format.
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    
      
  } // end Method initialize() -------
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  private final static String BTRIS_LAST_NAME = "[LAST_NAME i=";
  private final static String BTRIS_FIRST_NAME = "[FIRST_NAME i=";
  private final static String BTRIS_FIRST_NAME2 = "[ FIRST_NAME";
  private final static String BTRIS_ORGANIZATION = "[ORGANIZATION i=";
  private final static String BTRIS_organization = "[organization i=";
  private final static String BTRIS_ID = "[ID i=";
  private final static String BTRIS_id = "[id i=";
  
  
} // end Class LineAnnotator() ---------------

