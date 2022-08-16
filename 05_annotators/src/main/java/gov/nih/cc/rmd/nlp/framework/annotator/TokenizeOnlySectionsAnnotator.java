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
 * TokenizeOnlySections tokenizes only within section boundaries
 * 
 *  
 *
 * 
 * @author Guy Divita
 * @created Mar 23, 2017
 * 
 *  
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.Section;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class TokenizeOnlySectionsAnnotator extends TokenAnnotator {

 
  // -----------------------------------------
  /**
   * process takes a cas of un-annotated text and breaks it into
   * gov.va.chir.tokens.  This uses a finite state automata through
   * each character of the text, accumulating tokens as token barriers
   * are hit.  
   * 
   * Token barriers include space tokens.  
   * 
   * There is a small amount of repair done to mark punctuation that
   * is surrounded by whitespace, and punctuation that is at the end
   * of a non white-space token.
   * 
   * 
   * 
   * @param pJCas
   */
  // -----------------------------------------
  @Override
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
    super.performanceMeter.startCounter();
    boolean breakOnBreakChars = false;

  
    List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, Section.typeIndexID, true );
    
    ArrayList<Annotation> worthySections = null;
    if ( sections != null && !sections.isEmpty() ) {
      worthySections = new ArrayList<Annotation>();
      
      for ( Annotation aSection: sections ) {
        String sectionName = ((Section)aSection).getSectionName();
        if ( sectionName != null && worthySection( sectionName) )
          worthySections.add(aSection);
      }
      
    }
    
    
    if ( worthySections != null && !worthySections.isEmpty()) {
    
      for ( Annotation aSection : worthySections ) {
        String docText = aSection.getCoveredText();
        
        super.tokenize(pJCas, docText, aSection.getBegin(), breakOnBreakChars);
      }
    }
      
    super.repairNumbersThatEndInPeriods( pJCas);

    
    
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
    //  throw new AnalysisEngineProcessException();
    
    }
    super.performanceMeter.stopCounter();

  } // end Method process() --------------------------

  
  // =======================
  /**
   * worthySection 
   *
   * @param sectionName
   * @return returns true if the sectionname is in the list of sections
   */
  // =======================
  private boolean worthySection(String pSectionName) {
    
    boolean returnVal = false;
    String lowerCaseSectionName = pSectionName.toLowerCase().trim();
    for (String worthySectionName : this.worthySectionNames ) 
      if ( lowerCaseSectionName.contains(worthySectionName)) {
        returnVal = true;
        break;
      }
    return returnVal;
    } // End Method worthySection =======
  


  //----------------------------------
  /**
   * initialize 
   *
   * @param pArgs
   * 
   **/
  // ----------------------------------
  @Override
  public void initialize(String pArgs[]) throws ResourceInitializationException {

    super.initialize( pArgs);
   
    try {
      
     // this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
      
      String sectionNames = U.getOption(pArgs,  "--sectionNames=", "impression:plan");
      
      if ( sectionNames != null ) {
       sectionNames = sectionNames.replace('|',  ':').toLowerCase();
       this.worthySectionNames = U.split(sectionNames, ":");
      } else {
        System.err.println("no sectionNames passed in. Use --sectionNames=name1:name2 to specify");
        throw new ResourceInitializationException();
      }
      } catch ( Exception e) {
      e.printStackTrace();
      String msg = "Issue with tokenizerA:  " + e.toString();
      
      System.err.println(msg);
      throw new ResourceInitializationException();
    }

  
  } // end Method initialize() --------------


  


  // End Method tokenizeOnlySectionsAnnotator() -----------------------
  
 


  // ---------------------------------------------
  // Class Variables
  // ---------------------------------------------

 private String[]                   worthySectionNames = null;
  


} // end Class Tokenizer --------------------------
