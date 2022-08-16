// =================================================
/**
 * RedCatAnnotator uses the regular expressions created by the RedCat tool
 * to find and extract patterns from documents.  Both positive and negative
 * patterns are found and there is a back-off mechanism built in to find patterns
 * if no strict patterns are found.
 * 
 * The patterns will be labeled with the --ConceptName=XXXX passed in.  They will be 
 * made into Concept and Concept with Negative assertions.
 * 
 * TBD - hook in Leo's dynamic annotation scheme to convert Concept into conceptName
 *
 * @author  Guy Divita 
 * @created Sept 29, 2017
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import gov.va.vinci.model.Snippet;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

/**
 * Evaluates a set of regular expression rules against documents and adds
 * annotations defined in those rules.
 * 
 * @author divita
 */
public class RedCatSnippetExtractorAnnotator extends  RedCatAnnotator {



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
	
		try {
		    List<Annotation> snippets  = UIMAUtil.getAnnotations(pJCas, gov.va.vinci.model.Snippet.typeIndexID);
		    
		    if ( snippets != null && !snippets.isEmpty()) {
		      
		      for ( Annotation snippet: snippets ) {
		        
		        // create a snippet from just the text part of the snippet 
		        Annotation subSnippet = createSubSnippet( pJCas, snippet );
		        if ( subSnippet != null ) {
		          process( pJCas, subSnippet );
		          snippet.removeFromIndexes();
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
   
       Snippet snippet = new Snippet(pJCas);
      
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
    
  



  // ---------------------------------------
// Global Variables
// ---------------------------------------
   private ProfilePerformanceMeter       performanceMeter = null;
 
	
}
