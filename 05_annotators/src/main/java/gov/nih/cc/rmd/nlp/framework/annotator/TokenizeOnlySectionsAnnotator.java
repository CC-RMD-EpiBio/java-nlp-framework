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
