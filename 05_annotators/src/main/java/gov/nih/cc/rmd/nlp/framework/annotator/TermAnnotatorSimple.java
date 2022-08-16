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

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;



import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Utterance;
import gov.va.chir.model.WordToken;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermLookupFactory;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.TermLookupInterface;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities.TermUtils;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class TermAnnotatorSimple extends JCasAnnotator_ImplBase {
    
   
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
      this.startTimeInIndexCalls = System.currentTimeMillis();
      
      if ( this.processMe ) {
      
    // Utterances include sentence slotValue, content headings, dependent content ....
    // if you use utterance, the same span could get termized multiple times because it
    // is within the bounds of a slotValue, contentHeading and sentence all at the same time.
        
       
   
    List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Utterance.typeIndexID, true);

    if (sentences != null && !sentences.isEmpty()) {

      // ------------------------------------------------------
      // Walk through the utterances, looking for those that
      // are contentHeaders, and not slotValue's.
      // ------------------------------------------------------
      for (Annotation aSentence : sentences ) {
        System.err.println(aSentence.getCoveredText());
        termTokenizeSentence(pJCas, (Utterance) aSentence);
      }
         
    
    } // end if there are any sentences
  
    // ---------------------------------------------
    // Process the content headings of those slot:values and questions that are asserted
    // ---------------------------------------------
    
    List<Annotation> contentHeadings = UIMAUtil.getAnnotations(pJCas, ContentHeading.typeIndexID);
    if ( contentHeadings != null && contentHeadings.isEmpty() ) {
      for ( Annotation aHeading : contentHeadings )
         termTokenizeSentence(pJCas, (ContentHeading) aHeading );
       
    }
    
    // ---------------------------------------------
    // Process the dependent content of  slot:values 
    // ---------------------------------------------
    List<Annotation> dependentContents = UIMAUtil.getAnnotations(pJCas, DependentContent.typeIndexID);
   
    if ( dependentContents != null && !dependentContents.isEmpty() ) 
      for ( Annotation dependentContent : dependentContents) 
        termTokenizeSentence(pJCas,  (Utterance) dependentContent );
       
      
    
    
    // ----------------------------------
    // Final repair - look for terms that contain hyphens, that are not from any lexicon
    //   break them into their parts and add terms for each constituent.
    // repairHyphenatedTerms(pJCas);
    
      }
    
      String msg = computeCallsToIndex();
      
     
      this.performanceMeter.stopCounter( msg);
    
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL,"Issue with term tokenizing " + e.toString());
        this.performanceMeter.stopCounter();
        return;
      }
    } // end Method process() ------------------

    
   

    // =================================================
    /**
     * computeCallsToIndex accumulates stats about 
     * how many calls were made to the data-store
     * by record, total, max, avg
     * 
     * @return String  a message with the current stats
     * 
    */
    // =================================================
   private final String computeCallsToIndex() {
     
     this.numberProcessed++;
     this.totalCallsToIndex+= this.callsToIndex;
     this.avgCallsToIndex = this.totalCallsToIndex / this.numberProcessed;
     
     if ( this.callsToIndex > this.maxCallsToIndex)
       this.maxCallsToIndex = this.callsToIndex;
     
     this.callsToIndex = 0;
      
     this.currentTime = System.currentTimeMillis(); 
     this.timeInIndexCalls =  this.currentTime  - this.startTimeInIndexCalls ;
     
     this.totalTimeInIndexCalls+= this.timeInIndexCalls;
     this.avgTimeInIndexCalls = this.totalTimeInIndexCalls / this.numberProcessed;
     
     if ( this.maxTimeInIndexCalls > this.timeInIndexCalls )
       this.maxTimeInIndexCalls = this.timeInIndexCalls;
     
     
     
     String msg = 
         "+----------------------------------------------+---------------------------\n"
     +   "| The termAnnotator Lookup implementation used   | " + this.terminologyImplementation + "\n" 
     +   "| The number of documents processed              | " + this.numberProcessed           + "\n"
     +   "| The total calls to the index                   | " + this.totalCallsToIndex         + "\n"
     +   "| The avg number of calls to index by document   | " + this.avgCallsToIndex           + "\n"
     +   "| The Max number of calls for a given document   | " + this.maxCallsToIndex           + "\n"
     +   "+------------------------------------------------+--------------------------------------\n"
     +   "| The total time taken with calls to the index   | " + this.totalTimeInIndexCalls    + " (milliseconds) \n"
     +   "| The avg time taken with calls to the index     | " + this.avgTimeInIndexCalls      + " (milliseconds) \n"
     +   "| The Max time taken with calls to the index     | " + this.maxTimeInIndexCalls      + " (milliseconds) \n"
     +   "+------------------------------------------------+---------------------------\n";
     
   
    return msg ;
      
    } // end Method computeCallsToIndex() -------------




    // -----------------------------------------
    /**
     * termTokenizeSentence creates terms from the
     * tokens in this sentence.
     * 
     * @param pJCas
     * @param pSentence
     * @return 
     * @throws Exception 
     */
    // -----------------------------------------
    public List<?> termTokenizeSentence(JCas pJCas,  Utterance pSentence) throws Exception {
      
      List<LexicalElement> terms = null;
      List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas, WordToken.typeIndexID, pSentence.getBegin(), pSentence.getEnd());
  
      String[] terminologies = getTerminologiesFromSection( pJCas, pSentence );
      
      if ( tokens != null ) {
        terms =  termTokenizeSentence( pJCas, terminologies, tokens);
      } // end if there are any tokens in this utterance
      
      return terms;
    } // end Method termTokenizeSentence() -----

   

    // =================================================
    /**
     * getTerminologiesFromSection returns a terminology
     * based on what section this annotation is in
     * 
     * i.e., look in rxnorm if you are in a medications section
     *       look in loinc if you are in vitals, labs ... section
     *       otherwise look in snomedct_us
     * 
     * @param pJCas
     * @param pSentence
     * @return String[]
    */
    // =================================================
    private final String[] getTerminologiesFromSection(JCas pJCas, Utterance pSentence) {
      // TODO 
      String[] terminologies = {"SNOMEDCT_US","SNOMEDCT"};
      
      return terminologies;
    } // end Method getTerminologyFromSection()----------




    // ------------------------------------------
    /**
     * removeWhiteSpaceTokens
     *
     *
     * @param pTokens
     * @return
     */
    // ------------------------------------------
    public List<Annotation> removeWhiteSpaceTokens(List<Annotation> pTokens) {
    
      ArrayList<Annotation> buff = null;
      if ( pTokens != null && pTokens.size() > 0) {
        buff = new ArrayList<Annotation>( pTokens.size());
        for ( Annotation token : pTokens ) {
        	if ( token != null ) {
        	if ( token.getCoveredText().trim().length() == 0) {
            token.removeFromIndexes();
          } else if ( U.isOnlyPunctuation(token.getCoveredText())){
            token.removeFromIndexes();
          } else {
            buff.add( token);
          }            
        	}
        }
        
      }
      return buff;
    
    }  // End Method removeWhiteSpaceTokens() -----------------------
    

    // -----------------------------------------
    /**
     * lookup retrieves the terms from a sentence
     * 
     * @param pJCas
     * @param pTerminologies 
     * @param pSentenceTokens
     * @return List<LexicalElement>
     * 
     * @throws Exception 
     */
    // -----------------------------------------
    public List<LexicalElement> termTokenizeSentence(JCas pJCas, String[] pTerminologies,  List<Annotation> pSentenceTokens ) throws Exception {
     
     List<LexicalElement> terms = null;
      if ( pSentenceTokens != null  && !pSentenceTokens.isEmpty() ) 
        terms = lookup(pJCas, pTerminologies, pSentenceTokens );
      return terms ;
            
    } // end Method termTokenizeSentence() ---------------------------

    // =================================================
    /**
     * lookup [TBD] summary
     * 
     * @param pSentenceTokens
     * @return
     * @throws Exception 
    */
    // =================================================
   private final List<LexicalElement> lookup( JCas pJCas, String[] pTerminologies, List<Annotation> pTokens) throws Exception {
      
     List<LexicalElement> terms = null;
     
     
     List<Annotation> origTokens = makeTokens( pTokens, 0, pTokens.size()); // <---- strips sentence ending punctuation
     List<Annotation>     tokens = makeTokens( origTokens, 0, origTokens.size());
     
    
     int i = 0;
     while ( tokens != null &&   !tokens.isEmpty() ) {
      
        String key = makeKeyFrom( pJCas, tokens );
       // System.err.println("--->|" + key + "<---");
        String[] termResults = getSimple( pTerminologies, key);
        if ( termResults != null ) {
          createTerm( pJCas, tokens, termResults );
          origTokens = makeTokens( origTokens, 0, ( (origTokens.size() - tokens.size())  ));
          tokens = makeTokens( origTokens, 0, origTokens.size());
          i = 0;
        } else if ( tokens.size() == 1 && termResults == null ) {
          i = 0;
          origTokens = makeTokens(origTokens, 0, origTokens.size() -1) ;
          tokens     = makeTokens(origTokens, 0, origTokens.size());
        } else {
          i++;
          tokens = makeTokens( origTokens , i, origTokens.size() );
        }
      
     }
     
      return terms ;
    } // End lookup() ---------------------------------

    // =================================================
    /**
     * makeKeyFrom returns the string that was covered by
     * the set of tokens.  The key will have whitespace normalized.
     * Case is preserved.
     * 
     * @param pTokens
     * @return String
     * @throws Exception 
    */
    // =================================================
    private String makeKeyFrom(JCas pJCas, List<Annotation> pTokens) throws Exception {
      String returnVal = null;
      
      String buff = pJCas.getDocumentText();
      int beginOffset = pTokens.get(0).getBegin();
      int endOffset   = pTokens.get( pTokens.size() -1 ).getEnd();
      returnVal = buff.substring(beginOffset, endOffset);
      
      returnVal = this.tokenize.normalizeNotReverse(returnVal);
     
      return returnVal;
    } // end Method makeKeyFrom() ----------------------

    
    
    
    // =================================================
    /**
     * get 
     * 
     * @param pKey
     * @return String[]  rows from the table
    */
    // =================================================
      private String[] getSimple(String[] pTerminologies, String pKey) {
      
        
        String key = null;
        String[] returnVal = null;
        
        try {
        
          key = this.tokenize.normalizeNotReverse( pKey );
        
          returnVal = this.terminologyService.getSimple( pTerminologies, key );
          this.callsToIndex++;
          
        } catch (Exception e) {
          e.printStackTrace();
          String msg = "Issue with the term service with term " + key + " " + e.toString();
          GLog.println(GLog.ERROR_LEVEL, msg);
        }
          
      return returnVal;
    } // end Method get() ------------------------------

    // =================================================
    /**
     * createTerm creates the clinicalStatement, the lexicalElement,
     * and the underlying codedEntries for this span and set
     * of results
     * Each result is a codedEntry - there could be multiple
     * (ambiguous) coded entries for a given span 
     * 
     * @param pJCas
     * @param pTokens
     * @param pTermResults
    */
    // =================================================
    private  final void createTerm(JCas pJCas, List<Annotation> pTokens, String[] pTermResults) {
     
      createLexicalElement( pJCas, pTokens, pTermResults );
      
      
      
    } // end Method createTerm() -----------------------

    // =================================================
    /**
     * createLexicalElement 
     * 
     * @param pJCas
     * @param pTokens
    */
    // =================================================
     private final Annotation createLexicalElement(JCas pJCas, List<Annotation> pTokens, String[] resultRows) {
      
       LexicalElement statement = new LexicalElement(pJCas);
       int beginOffset = pTokens.get(0).getBegin();
       int endOffset   = pTokens.get( pTokens.size() -1 ).getEnd();
       statement.setBegin( beginOffset);
       statement.setEnd(    endOffset );
       statement.setId("TermAnnotatorSimple_" + annotationCounter );
       
       StringArray lexMatches = UIMAUtil.string2StringArray(pJCas, resultRows);
       statement.setLexMatches( lexMatches );
       
       statement.addToIndexes();
       
       return statement;
      
    }  // end Method createLexicalElement() ------------
 
    // =================================================
    /**
     * makeTokens makes a subset of tokens from
     *  the list of tokens 0 thru the pEnd
     * 
     * @param pTokens
     * @param pEnd
     * @return List<Annotation>
    */
    // =================================================
   private final List<Annotation> makeTokens(List<Annotation> pTokens, int pEnd) {
      ArrayList< Annotation> someTokens = null;
      
      if ( pTokens != null && pTokens.size() > 0 ) {
        someTokens = new ArrayList<Annotation>( pTokens.size() );
        for ( int i = pEnd; i < pTokens.size(); i++)
            someTokens.add( pTokens.get(i));
        
      }
      return someTokens;
   } // end Method makeTokens() --------------------

    
   // =================================================
   /**
    * makeTokens makes a subset of tokens from
    *  the list of tokens 0 thru the pEnd
    * 
    * @param pTokens
    * @param pEnd
    * @return List<Annotation>
   */
   // =================================================
  private final List<Annotation> makeTokens(List<Annotation> pTokens, int pBegin, int pEnd) {
     ArrayList< Annotation> someTokens = null;
     
     if ( pTokens != null && pTokens.size() > 0 ) {
       someTokens = new ArrayList<Annotation>( pTokens.size() );
       for ( int i = pBegin; i < pEnd; i++) {
         if ( i ==  pBegin && ((WordToken)pTokens.get(i)).getPunctuationOnly() )
           continue;
         else if ( i ==  pEnd -1 && ((WordToken)pTokens.get(i)).getPunctuationOnly() )
           continue;
         else if ( !((WordToken)pTokens.get(i)).getSentenceBreak() )
           someTokens.add( pTokens.get(i));
       
       }
     }
     return someTokens;
  } // end Method makeTokens() --------------------

   
    // ----------------------------------
    /**
     * destroy cleans up after the last invocation.
     * 
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void destroy() {
      
      
      

      this.performanceMeter.writeProfile( this.getClass().getSimpleName());
      
    } // end Method destroy() ------------




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
     
      String args[] = null;
      try {
        args                  = (String[]) aContext.getConfigParameterValue("args"); 
        
        initialize( args );
        
      } catch ( Exception e) {
        e.printStackTrace();
        String msg = "Issue with initializing TermAnnotator " + e.toString();
        GLog.println(GLog.ERROR_LEVEL, msg);
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
        
      String terminologyFilez = null;
      
      try {
        this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
        this.terminologyImplementation = U.getOption(pArgs,  "--sophiaImplementation=", "gov.cc.rmd.terminologyService");
        terminologyFilez = U.getOption(pArgs, "--terminologyFiles=", "");
        
        String[] terminologyFiles = U.split(terminologyFilez, ":");
        
        
        if ( terminologyFilez == null ) 
          this.processMe = false;
        else {
          
          
          String knownAcronymsFile = "resources/vinciNLPFramework/tokenizer/knownAcronyms.txt";
          
          try {
            this.tokenize = new TermUtils(knownAcronymsFile);
            
            
          }
          catch (Exception e2) {
            e2.printStackTrace();
            String msg = "Issue: TermTokenizer : couldnt create the termUtils |" + knownAcronymsFile + "|" + e2.toString() ;
            GLog.println(GLog.ERROR_LEVEL,msg);
            throw new ResourceInitializationException();
          }
          
          this.terminologyService = TermLookupFactory.getTermLookupSimple( terminologyImplementation );
          this.terminologyService.init(pArgs, terminologyFiles);
        
        }
         
     
       
       
      } catch (Exception e) {
        e.printStackTrace();
       //  GLog.println(GLog.ERROR_LEVEL,"Issue with getting the local terminology files " + e.toString());
        throw new ResourceInitializationException();
 
      }
    
    
      
    } // end Method Initialize();
    
    
  

  //---------------------------------------
//Global Variables
//---------------------------------------

 
    // -------------------------------------------
    // Class Variables
    // -------------------------------------------
    private int                           annotationCounter  = 0;    // new Term Counter.
    private TermLookupInterface           terminologyService = null;
    private String                        terminologyImplementation     = null;
    private ProfilePerformanceMeter       performanceMeter   = null;
    private boolean                       processMe          = true;
    private TermUtils                     tokenize           = null;
    private int                           callsToIndex       = 0;
    private int                           maxCallsToIndex    = 0;
    private int                           avgCallsToIndex    = 0;
    private int                           totalCallsToIndex  = 0;
    private int                           numberProcessed    = 0;
    private double                        timeInIndexCalls = 0.0;
    private double                     maxTimeInIndexCalls = 0.0;
    private double                     avgTimeInIndexCalls = 0.0;
    private double                   totalTimeInIndexCalls = 0.0;
    private long                     startTimeInIndexCalls = 0;
    private long                               currentTime = 0;

    
} // end Class TermAnnotator() -----------
