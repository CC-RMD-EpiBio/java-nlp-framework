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
 * TokenWithinSnippetsAnnotator creates subSnippets from snippets (as a side effect) and tokenizes
 * only the things with these sub-snippets.
 * 
 * @author Guy Divita
 * @created Dec 2, 2017
 * 
 *  
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import gov.va.vinci.model.Snippet;
import gov.va.vinci.model.SubSnippet;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class TokensWithinSnippetsAnnotator extends TokenAnnotator {

  

  // =======================================================
   /**
    * process
    * 
    * @param pJCas
    *
    */
   // ======================================================  
	@Override
	public void process(JCas pJCas) throws AnalysisEngineProcessException {

	  super.performanceMeter.startCounter();
	
	  int offset = 0;
	  boolean breakOnBreakChars = false;
		try {
		    List<Annotation> snippets  = UIMAUtil.getAnnotations(pJCas, gov.va.vinci.model.Snippet.typeIndexID, false);
		    
		    if ( snippets != null && !snippets.isEmpty()) {
		      
		      for ( Annotation snippet: snippets ) {
		        
		    	  if (snippet.getClass().getName().contains("Snippet") ) {
		        // create a snippet from just the text part of the snippet 
		    	  Annotation subSnippet = createSubSnippet( pJCas, snippet );
		    	  if ( subSnippet != null ) {
		         
		          
		    		  String docText = subSnippet.getCoveredText();
		    		  offset = subSnippet.getBegin();
		    		  super.tokenize(pJCas, docText, offset, breakOnBreakChars);

		          
		          
		    		  snippet.removeFromIndexes();
		    	  }
		        }
		      }
		      
		   
		 
		  } // end if there is a valid snippet
		    
		    
		
		} catch (Exception e) {
		  e.printStackTrace();
		  String msg = "Issue with finding patterns from regular expressions : " + e.toString() ;
		  System.err.println(msg);
		}
		
		super.performanceMeter.stopCounter();
	}



  // =======================================================
   /**
    * createSubSnippet creates an annotation around just the
    * text part of the snippet
    * 
    * @param pJCas
    * @param snippet
    * @return Annotation
    *
    */
   // ======================================================	
  private Annotation createSubSnippet(JCas pJCas, Annotation pSnippet) {
   
       SubSnippet snippet = new SubSnippet(pJCas);
      
       // figure out the text part - it's been suffed into the otherFeatures of the incoming snippet
       String textPart = ((Snippet) pSnippet).getOtherFeatures();
       if ( textPart == null || textPart.isEmpty() )  return null;
       String allText = pSnippet.getCoveredText();
       String beginTextPart = textPart;
       if (textPart.length() > 10 ) beginTextPart = textPart.substring(0,10);
       int textOffsetPart = allText.indexOf( beginTextPart );
       
       
       int textBegin =  pSnippet.getBegin() + textOffsetPart;
       int textEnd   =  textBegin + textPart.length() - (101 -15);
       
       if ( textBegin > textEnd ) {
    	   int b = textBegin;
    	   textBegin = textEnd;
    	   textEnd = b;
       }
       if ( textBegin < 0 || textEnd < 0 || textBegin>= textEnd ) {
    	   System.err.println("Issue here ");
    	   
    	   
       }
    	   
       snippet.setBegin( textBegin);
       snippet.setEnd( textEnd);
       snippet.setAnnotationId(    ((Snippet)pSnippet).getAnnotationId() );
       snippet.setAssertionStatus( ((Snippet)pSnippet).getAssertionStatus() );
       snippet.setCategory(        ((Snippet)pSnippet).getCategory() );
       snippet.setConditional(     ((Snippet)pSnippet).getConditional() );
       snippet.setDocumentId(      ((Snippet)pSnippet).getDocumentId() );
       snippet.setFileSnippetId(   ((Snippet)pSnippet).getFileSnippetId() );
       snippet.setFocus(           ((Snippet)pSnippet).getFocus() );
       snippet.setFocusBeginOffset(((Snippet)pSnippet).getFocusBeginOffset() );
       snippet.setFocusEndOffset(  ((Snippet)pSnippet).getFocusEndOffset() );
       snippet.setFocusCategory(   ((Snippet)pSnippet).getFocusCategory() );
       snippet.setHistorical(      ((Snippet)pSnippet).getHistorical() );
       snippet.setOtherFeatures(   ((Snippet)pSnippet).getOtherFeatures() );
       snippet.setPatientId(       ((Snippet)pSnippet).getPatientId() );
       snippet.setSnippetId(       ((Snippet)pSnippet).getSnippetId() );
       snippet.setSubject(         ((Snippet)pSnippet).getSubject() );
       
       snippet.addToIndexes();
       
    return snippet;
    } // End Method createSubSnippet ============
    
 
  
  
} // end Class Tokenizer --------------------------
