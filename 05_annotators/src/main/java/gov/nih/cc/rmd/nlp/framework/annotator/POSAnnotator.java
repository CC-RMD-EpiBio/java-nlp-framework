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
 * POSAnnotator wraps up the openNLP PoS tagger using
 * the cTakes model (specified via parameters in the descriptor).
 * This annotator does the pipefitting to correctly label
 * the terms that the tokens cover. 
 * 
 * In a future release, no pipefitting should be necessary, where
 * the pos tagger was trained on terms not words.
 *
 * The cTAKES version 1.0.5 tool uses the openNLP version 1.4.0.
 * There are conflicts if other versions of openNLP are used, such
 * as version 1.1.0, and 0.9.
 * 
 * @author  Guy Divita 
 * @created May 19, 2011
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
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.PartOfSpeech;
import gov.va.chir.model.Token;
import gov.va.chir.model.Utterance;
import gov.va.chir.model.WhitespaceToken;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import opennlpL.tools.lang.english.PosTagger;
import opennlpL.tools.postag.POSDictionary;
import opennlpL.tools.postag.POSTagger;
import opennlpL.tools.postag.TagDictionary;

public class POSAnnotator extends JCasAnnotator_ImplBase {

  // -----------------------------------------
  /** 
   * process iterates though the utterances of a document, and assigns
   * part of speech tags (in PennTreebank format) to the lexical elements
   * and tokens of each.
   * 
   * This method expects to have Utterances, LexicalElements, and Tokens
   * as input. Part of Speech tags are added to the lexicalElements and tokens.
   *
   * @param pAJCas
   * @throws AnalysisEngineProcessException
   */
  // -----------------------------------------
  @Override
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
    
    try {
    this.performanceMeter.startCounter();
	 // System.err.println("Entering pos");
    // this.logger.trace(this.className +  "|process|Begin|");
    // Extract the utterances of the document
    // Extract the terms of each utterance
    // Extract the tokens of each term (add a pointer to each token to indicate what term it refers to)
    // push through the tagger
    // assign tags to each token
    // Negotiate the term tag from the tokens that are part of multi-word tokens
    // Update the CAS
    
    List<Annotation> utterances = UIMAUtil.getAnnotationsFromDocument(pJCas,
        Utterance.type);

    if (utterances != null) {

      for (Annotation utterance : utterances)
        posTag(pJCas, (Utterance) utterance);

    } // end if there are any utterances
    
    // this.logger.trace(this.className +  "|process|Return|");
    // System.err.println("Exiting POS");
    
    this.performanceMeter.stopCounter();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("issue in POS " + e.toString());
    }
    
  } // end Method process() ------------------

  // -----------------------------------------
  /**
   * posTag takes an utterance in, and adds pos tags to each lexicalElement and token
   * associated with this posTag.
   * @param pJCas 
   * 
   * @param pUtterance
   * @exception AnalysisEngineProcessException
   */
  // -----------------------------------------
  private void posTag(JCas pJCas, Utterance pUtterance) throws AnalysisEngineProcessException {
 
    ArrayList<String>tokenStringList = new ArrayList<String>();
    ArrayList<Token>tokenList = new ArrayList<Token>();
    String documentId = VUIMAUtil.getDocumentId(pJCas);
    // Extract the terms of each utterance
    FSArray termz = pUtterance.getUtteranceLexicalElements();
    if ( termz != null ) {
      @SuppressWarnings("unchecked")
      List<LexicalElement> terms = UIMAUtil.fSArray2List(pJCas, termz);
    
      if ( terms != null && terms.size() > 0 )
        for ( LexicalElement term : terms) {
      
          FSArray tokenz = term.getTokens();
          @SuppressWarnings("unchecked")
          List<Token> tokens = UIMAUtil.fSArray2List(pJCas, tokenz);
          if ( tokens != null && tokens.size() > 0  ) 
            for ( Token aToken : tokens) {
              try {
              aToken.setParent( term);
              if ( aToken.getTypeIndexID() != WhitespaceToken.type ) {
                tokenStringList.add( aToken.getDisplayString());
                tokenList.add( aToken);
              }
              } catch (Exception e) {
                e.printStackTrace();
                System.err.println(documentId + "|" + "Issue with making POS on this token : " +  aToken.getCoveredText() + "|" + aToken.getBegin() + "|" + aToken.getEnd() + e.toString());
              }
            }
        } // end loop through terms of the utterance
      String tokenArray[] = new String[ tokenStringList.size()];
      tokenArray = tokenStringList.toArray( tokenArray);
      
      // --------------------------------
      // the work is done here
      // --------------------------------
      String[] tags = this.tagger.tag( tokenArray);
      
      // ---------------------------------------
      // Mark the terms with pos from the tokens 
      // ---------------------------------------
      if ( tags != null ) {
        for (int i = 0; i < tags.length; i++ ) {
          Token aToken = tokenList.get(i);
          PartOfSpeech aPOS = new PartOfSpeech(pJCas);
          //VUIMAUtil.setProvenance( pJCas, aPOS, this.className );
          aPOS.setBegin(aToken.getBegin()  );
          aPOS.setEnd( aToken.getEnd()  );
          aPOS.setPos( tags[i]);
          aPOS.setTypeSystem("PennTreebank"); // this needs to be generalized
          aPOS.addToIndexes(pJCas);
          aToken.setPartOfSpeech(aPOS);
          
          
        }
        // -------------------------------------
        // Set the term's pos from the token's pos and info from the lexical elements
        // -------------------------------------
        setTermPOS( pJCas, terms);
        
      }
      
      
    } // end if there were any terms of the utterance
    
    
    
  } // end Method posTag() -------------------

  // -----------------------------------------
  /**
   * setTermPOS Given a set of terms, for each term
   * set the term's pos from the tokens's pos that make up
   * the term and info from the term's lexical Items
   * 
   * @param pJCas
   * @param pTerms
   */
  // -----------------------------------------
  private void setTermPOS(JCas pJCas, List<LexicalElement> pTerms) {
   
    if ( pTerms != null )
      for ( LexicalElement term : pTerms )
        setTermPOS( pJCas, term);
    
  } // end Method setTermPOS() --------

  // -----------------------------------------
  /**
   * setTermPOS sets the term's part of speech.  
   *    If the term is a single token, use the token's pos.
   *    If the term is multi word 
   *      if the 
   * @param pTerm
   */
  // -----------------------------------------
	private void setTermPOS(JCas pJCas, LexicalElement pTerm) {

		if (pTerm != null) {

			FSArray tokenz = pTerm.getTokens();
			@SuppressWarnings("unchecked")
			List<Token> tokens = UIMAUtil.fSArray2List(pJCas, tokenz);
			if (tokens != null) {
				if (tokens.size() == 1)
					pTerm.setPartOfSpeech(tokens.get(0).getPartOfSpeech());
				else {

					// ------------------------------------------------
					// This is where things get interesting:
					// At this point, we have a multi-word term, where
					// the pos tagger has tagged the parts, but does not
					// necessarily know it's a term. And this multi-word
					// term is ambiguous. There are only 822 phrase boundary
					// changing ambiguous multiword terms. For the rest,
					// the POS from the term should populate the tokens.
					// For these 822, the right most token's tag should
					// populate the term's pos.
					// ------------------------------------------------
					FSArray posFeatureArray = pTerm.getPotentialPartsOfSpeech();
					if (posFeatureArray.size() == 1) { // < if (!pTerm.getAmbiguous()
													

						// --------------------------------------------
						// non-ambiguous term
						// Set the term's pos
						// Set the tokens of the term pos
						// --------------------------------------------
						PartOfSpeech pos = (PartOfSpeech) posFeatureArray.get(0);
						pTerm.setPartOfSpeech(pos);
						for (Token aToken : tokens) {
							aToken.setPartOfSpeech(pos);
						} // loop through the tokens of the term

					} else {
						// ----------------------------
						// look at the last token's pos
						// ----------------------------
						Token lastToken = tokens.get(tokens.size() - 1);
						PartOfSpeech lastTokenPartOfSpeech = lastToken.getPartOfSpeech();
						if ( lastTokenPartOfSpeech != null )
						for (int i = 0; i < posFeatureArray.size(); i++) {
							PartOfSpeech possiblePOS = (PartOfSpeech) posFeatureArray.get(i);
							String aPos = possiblePOS.getPos();
							if (aPos != null && aPos.compareToIgnoreCase(lastTokenPartOfSpeech.getPos()) == 0) {
							  //VUIMAUtil.setProvenance( pJCas, possiblePOS, this.className );
							  possiblePOS.setBegin( pTerm.getBegin());
							  possiblePOS.setEnd( pTerm.getEnd());
								pTerm.setPartOfSpeech(possiblePOS);
								break;
							} // the right term pos found
						} // end loop through the possible parts of speech of the term
						else {
							//pTerm.setPartOfSpeech(lastTokenPartOfSpeech);
;						}
					} // end if this is an ambiguous term
				} // end if this a multi-word term
			} // end of there are tokens of a term
		} // end of if there is a term
	}  // end Method setTermPOS() -------
	


//----------------------------------
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
      args                 = (String[]) aContext.getConfigParameterValue("args");  

    } catch (Exception e ) {
      System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
    
    this.performanceMeter = new ProfilePerformanceMeter( args, this.getClass().getSimpleName() );
    
    
    if (aContext != null)
      super.initialize(aContext);
    
    
    this.initialize();
  } // end Method initialize() ----------------------------------
    
    
  
  // =======================================================
  /**
   * initialize 
   * 
   * @throws ResourceInitializationException 
   * 
   */
  // =======================================================
  public void initialize() throws ResourceInitializationException {
    
   
    TagDictionary tagDictionary = null;
    boolean       caseSensitive = true;
   try {

     

     String openNLPModelFile =     "resources/vinciNLPFramework/PartOfSpeech/postagger.model.bin";
     String tagDictionaryFile =    "resources/vinciNLPFramework/PartOfSpeech/tag.dictionary.txt";
   
     
     
     tagDictionary = (TagDictionary) new POSDictionary(tagDictionaryFile, caseSensitive, true);
     this.tagger = new PosTagger(openNLPModelFile,  tagDictionary);
    
   } catch( Exception e) {
     e.printStackTrace();
     System.err.println("Could not create the openNLP POS tagger " + e.getMessage());
     throw new ResourceInitializationException(e);
   }
 
  }  // End Method initialize() ======================
  





  // -------------------------------------
  // Class variables
  // -------------------------------------
//---------------------------------------
//Global Variables
//---------------------------------------
  private POSTagger               tagger = null;
  public static final String     TAG_DICTIONARY_PARAM = "TagDictionary";
  public static final String OPENNLP_MODEL_FILE_PARAM = "openNLPModelFile";
  ProfilePerformanceMeter              performanceMeter = null;
  
} // end Class POSTaggerSimple() --------------------
