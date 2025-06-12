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
 * TermAnnotator identifies single and multi-word terms within text. 
 * This is a dictionary based lookup that relies upon the SPECIALIST Lexicon
 * for the source of the lexemes.  This lookup mechanism is an evolution
 * of the lookup mechanism within NLM's TextTools (and subsequently the
 * MMTx projects).  
 * 
 * The lookup algorithm matches terms within a sentence from left to
 * right, on grounds that more terms have their head word on the right
 * of the string than the left (in English).
 *
 *   Parameters:
 *       localTerminologyFiles   String[] a list of resource relative or fullpath to LRAGR files containing local lexica. 
 *    
 *
 * @author  Guy Divita 
 * @created Mar 1, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Utterance;
import gov.va.chir.model.VAnnotation;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class ThreadedTermAnnotator extends JCasAnnotator_ImplBase {
    
    @SuppressWarnings("unchecked")
    @Override
    // -----------------------------------------
    /**
     * process takes sentences and returns terms attached to that sentence.
     * 
     * @param aJCas
     */
    // -----------------------------------------
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
    
      try {
      this.performanceMeter.startCounter();
      
    // Utterances include sentence slotValue, content headings, dependent content ....
    // if you use utterance, the same span could get termized multiple times because it
    // is within the bounds of a slotValue, contentHeading and sentence all at the same time.
   
    List<Annotation>       sentences = UIMAUtil.getAnnotations(pJCas, Utterance.typeIndexID, true);
    List<Annotation> contentHeadings = UIMAUtil.getAnnotations(pJCas, ContentHeading.typeIndexID);
    List<Annotation> dependentContents = UIMAUtil.getAnnotations(pJCas, DependentContent.typeIndexID);
    

    if (sentences != null) {

      // ------------------------------------------------------
      // Walk through the utterances, looking for those that
      // are contentHeaders, and not slotValue's.
      // ------------------------------------------------------
      for (Annotation aSentence : sentences ) {
       
          if ( ((Utterance)aSentence).getProcessMe() ) {
            try {
              this.termAnnotatorThreadPool.termTokenizeSentence(pJCas, (Utterance) aSentence);
          
            } catch (Exception e) {
              e.printStackTrace();
              System.err.println("Issue with finding terms in sentences " + e.toString());
              throw new AnalysisEngineProcessException();
           
            } // throw a uima exception here.
          } //end if this is a sentence to be processed 

      } // end loop through the sentences of the document
    } // end if there are any sentences
  
    // ---------------------------------------------
    // Process the content headings of those slot:values and questions that are asserted
    // ---------------------------------------------
    
   
    if ( contentHeadings != null ) {
    for ( Annotation contentHeading : contentHeadings) {
      try {
        
        
        List<?> contentHeadingTerms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, contentHeading.getBegin(), contentHeading.getEnd());
        
        if ( contentHeadingTerms == null || contentHeadingTerms.size() == 0 )
           this.termAnnotatorThreadPool.termTokenizeSentence(pJCas, (ContentHeading) contentHeading );
    
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue with finding terms in contentHeadings " + e.toString());
        throw new AnalysisEngineProcessException();
        // throw a uima exception here.
      }
    }
    }
    
    // ---------------------------------------------
    // Process the dependent content of  slot:values 
    // ---------------------------------------------
    
    if ( dependentContents != null ) {
    for ( Annotation dependentContent : dependentContents) {
      try {
        List<Annotation> depententContentTerms  = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, dependentContent.getBegin(), dependentContent.getEnd());
        
        if ( depententContentTerms == null || depententContentTerms.size() == 0 ) {
           this.termAnnotatorThreadPool.termTokenizeSentence(pJCas,   dependentContent );
        }
        
      
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue with finding terms in dependentContent " + e.toString());
        throw new AnalysisEngineProcessException();
      }
    }
    }
 
    // ---------------------------------
    // Wait until all the sentences have been termized before proceeding
    waitUntilAllUtterancesAreProcessed(pJCas, sentences,contentHeadings, dependentContents);
    
    /*
     
    
    // -----------------------------
    // Pass the assertion status of the content headings and 
    // dependent content on to the terms within them
    propogateNegationStatusFromSlotValuesToEmbeddedTerms(pJCas);
    
    
    
    
    // ----------------------------------
    // Final repair - look for terms that contain hyphens, that are not from any lexicon
    //   break them into their parts and add terms for each constituent.
    repairHyphenatedTerms(pJCas);
   
    */
    
    this.performanceMeter.stopCounter();
    
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
        throw new AnalysisEngineProcessException();
      }
    } // end Method process() ------------------

   
    // ==========================================
    /**
     * propogateNegationStatusFromSlotValuesToEmbeddedTerms 
     * The newly created terms should inherit the assertion
     * status of slot and value pieces that encase them.
     *
     * @param pJCas
     */
    // ==========================================
    private void propogateNegationStatusFromSlotValuesToEmbeddedTerms(JCas pJCas) {
      
      List<Annotation> contentHeadings = UIMAUtil.getAnnotations(pJCas, ContentHeading.typeIndexID);
      List<Annotation> dependentContents = UIMAUtil.getAnnotations(pJCas, DependentContent.typeIndexID);
      
      
      propogateNegationStatusFromSlotValuesToEmbeddedTerms(pJCas, contentHeadings);
      propogateNegationStatusFromSlotValuesToEmbeddedTerms(pJCas, dependentContents);
      
      
    } // End Method propogateNegationStatusFromSlotValuesToEmbeddedTerms() ===
      

    // ==========================================
    /**
     * propogateNegationStatusFromSlotValuesToEmbeddedTerms 
     * The newly created terms should inherit the assertion
     * status of slot and value pieces that encase them.
     *
     * @param pJCas
     * @param List<Annotation>
     */
    // ==========================================
    private void propogateNegationStatusFromSlotValuesToEmbeddedTerms(JCas pJCas, List<Annotation> contentHeadings ) {
      
    
      if ( contentHeadings != null ) {
      for ( Annotation contentHeading : contentHeadings) {
        try {
          
          
          List<Annotation> contentHeadingTerms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, contentHeading.getBegin(), contentHeading.getEnd());
          
             //System.err.println("Content heading = " + contentHeading.getCoveredText());
              if ( ((VAnnotation)contentHeading).getNegation_Status() == null  ||         
                   ((VAnnotation)contentHeading).getNegation_Status().contains("sserted") ) {
              
               ;; 
              } else {
              
                // ----------------------------------------
                // We have a negated content heading - negate any terms within it
                if ( contentHeadingTerms != null && contentHeadingTerms.size() > 0 )
                  negateTerms( (List<Annotation>) contentHeadingTerms, ((ContentHeading) contentHeading).getNegation_Status());
                
              }
       
        } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException(e.toString());
          // throw a uima exception here.
        }
      }
      }
    
    }  // end Method propogateNegationStatusFromSlotValuesToEmbeddedTerms() ========================================
    



    // =======================================================
    /**
     * negateTerms negates terms found in content headings
     * 
     * @param pTermsToNegate
     * @param pNegationStatus
     */
    // =======================================================
    private void negateTerms(List<Annotation> pTermsToNegate, String pNegationStatus) {
      
      if ( pTermsToNegate != null ) 
        for ( Annotation term : pTermsToNegate ) {
          
          ((LexicalElement) term).setNegation_Status( pNegationStatus);
          ((LexicalElement) term).setSubject("Patient");
          ((LexicalElement) term).setConditional(false);
        }
          
      
    } // End Method negateTerms() ======================
    

    // =======================================================
    /**
     * repairHyphenatedTerms  look for terms that contain hyphens, that are not from any lexicon
     *                        break them into their parts and add terms for each constituent.
     * 
     * @param pJCas
     *
     */
    // =======================================================
     private void repairHyphenatedTerms(JCas pJCas)  {
    
      List<Annotation> terms = UIMAUtil.getAnnotations(pJCas, LexicalElement.typeIndexID);
      
      if ( terms != null )
        for ( Annotation term : terms ) {
          if (term.getCoveredText().contains("-") ) {
            if ( ((LexicalElement)term).getEuis() == null ||  ((LexicalElement)term).getEuis().get(0).contains("unknown") ||  ((LexicalElement)term).getSemanticTypes() == null ) {
              try {
                termAnnotatorThreadPool.repairHyphenatedTerm( pJCas, term);  //<------------------------------------  threaded
              } catch (Exception e) {
                e.printStackTrace();
                System.err.println( "Issue with retokenizing around hyphens " + e.getMessage());
                
              }
            }
            
          } // end if there is a hyphen in this term
        } // end loop thru terms
      
    } // End Method repairHyphenatedTerms() ======================
    

   

    // ==========================================
    /**
     * waitUntilAllUtterancesAreProcessed will iterate thru
     * each list, and wait until each utterance has been
     * marked before proceeding.
     * 
     * This is a cheap way of doing re-synchronization of the
     * threads 
     *
     * @param pJCas
     * @param sentences
     * @param contentHeadings
     * @param dependentContents
     */
    // ==========================================
    private void waitUntilAllUtterancesAreProcessed(JCas             pJCas, 
                                                   List<Annotation> sentences,
                                                   List<Annotation> contentHeadings, 
                                                   List<Annotation> dependentContents) {
     
      
      waitUntilAllUtterancesAreProcessed( pJCas, sentences);
      waitUntilAllUtterancesAreProcessed( pJCas, contentHeadings);
      waitUntilAllUtterancesAreProcessed( pJCas, dependentContents);
      
    }  // end Method waitUntilAllSentencesAreProcessed() =

    // ==========================================
    /**
     * waitUntilAllUtterancesAreProcessed will iterate thru
     * the list, and wait until each utterance has been
     * marked before proceeding.
     * 
     * This is a cheap way of doing re-synchronization of the
     * threads 
     *
     *
     * @param pJCas
     * @param sentences
     */
    // ==========================================
    private void waitUntilAllUtterancesAreProcessed(JCas pJCas, List<Annotation> sentences) {
     
      if ( sentences != null && sentences.size() > 0) {
        
        for ( Annotation sentence : sentences ) {
          if ( ((VAnnotation)sentence).getProcessMe() ) 
              while (!((VAnnotation)sentence).getMarked() ) {
                try {  Thread.sleep(10);  } catch (InterruptedException e) {  }
              } // sleep until the sentence gets marked
        } // end loop thru each sentence
      } // end if there is at least one sentence
      
    } // end Method waitUntilAllUtterancesAreProcessed() ========================================
    






    //----------------------------------
    /**
     * initialize loads in the resources needed for term tokenization. Currently, 
     * this involves NLM's LRAGR table.   
     * 
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
     
    
      String[] args = null;
    
      try {
       args                  = (String[]) aContext.getConfigParameterValue("args"); 
       
       initialize( args );
       
       
       
      } catch (Exception e) {
        e.printStackTrace();
       //  System.err.println("Issue with getting the local terminology files " + e.toString());
        throw new ResourceInitializationException();
 
      }
    
     
      
    } // end Method initialize() ---------

    
 
    
    //----------------------------------
    /**
     * initialize loads in the resources needed for term tokenization. Currently, 
     * this involves NLM's LRAGR table.   
     * 
     * @param pArgs
     * 
     **/
    // ----------------------------------
    public void initialize(String[] pArgs) throws ResourceInitializationException {
        
      String localTerminologyFilez = null;
      String localResources = null;
      int numberOfTermThreads = 1;
      
      if ( pArgs != null && pArgs.length > 0 ) {
        localTerminologyFilez = U.getOption(pArgs, "--localTerminologyFiles=", "");
               localResources = U.getOption(pArgs, "--localResources",         "./");
          numberOfTermThreads = Integer.parseInt(U.getOption(pArgs,  "--numberOfTermThreads=", "1"));
      }
     
         
      this.termAnnotatorThreadPool = new TermAnnotatorThreadPool();
      this.termAnnotatorThreadPool.initialize( localTerminologyFilez, localResources, numberOfTermThreads , pArgs);
      
      this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
      
    } // end Method Initialize();
    
   
 
    
  // ----------------------------------
  /**
   * destroy cleans up after the last invocation.
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void destroy() {
    
    try {
       this.termAnnotatorThreadPool.destroy();
    }
    catch (Throwable e) {
      e.printStackTrace();
      getContext().getLogger().log(Level.SEVERE, "Tokenization: known acronyms are not specified.");
    } // end catch()
    
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
    
  } // end Method destroy() ------------
 


//---------------------------------------
//Global Variables
//---------------------------------------

 
    // -------------------------------------------
    // Class Variables
    // -------------------------------------------
    
    ProfilePerformanceMeter       performanceMeter = null;
    private TermAnnotatorThreadPool      termAnnotatorThreadPool = null;
    
    
} // end Class TermAnnotator() -----------
