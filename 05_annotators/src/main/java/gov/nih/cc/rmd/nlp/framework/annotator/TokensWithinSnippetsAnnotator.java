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
