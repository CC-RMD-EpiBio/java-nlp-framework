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
 * RedSnippetsWriter creates snippets around mentions using the RedEx/RedCat
 * format - to feed into RedEx/RedCat
 *
 * @author  Guy Divita 
 * @created Oct 31, 2017
 *
 **   *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.snippet;

import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.nlm.nls.vtt.model.Markups;
import gov.nih.nlm.nls.vtt.model.Tags;
import gov.nih.nlm.nls.vtt.model.VttDocument;
import gov.nih.cc.rmd.nlp.framework.marshallers.vtt.ToVTT;
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class RedSnippetsWriter extends SnippetsWriter  {
 




   public RedSnippetsWriter(String[] pArgs) throws ResourceInitializationException {
		super(pArgs);
		
	}

   

   // =======================================================
   /**
    * display 
    * 
    * @param pJCas
    * @param pContextsCache
    * @param pCasTextBuff
    * @param outputDir2
    * @param assertionStatus
    * @param inProse
    * @param pSnippetFileCounter
    * @throws Exception 
    */
   // =======================================================
   @Override
   void display(JCas pJCas, List<Context> pContextsCache, String pCasText, String outputDir2, String cohort, String assertionStatus, boolean inProse, int[] pSnippetFileCounter ) throws Exception {
    
     
     JCas jcas = null;
     
     if (pCasText == null || pContextsCache == null  ) return;
     
     try {
       jcas = UIMAUtil.createNewJCAS(pJCas); 
     } catch (Exception e) {
       e.printStackTrace(); 
       System.err.println("Issue creating a new cas " + e.toString() + " continuing ....");
       return;
     }
        
     if ( pCasText == null || pCasText.trim().length() == 0 )  { 
       System.err.println("Empty text! why? " + pContextsCache.size() + " continuing ...."); 
       // throw new RuntimeException();
       return;
     }
     jcas.setDocumentText(pCasText);
     createDocumentHeader( jcas, super.outputDir, cohort, assertionStatus,  inProse, pSnippetFileCounter[0]++);
   
     // -----------------------------------------------
     // Create the annotations for each of the contexts
     for ( int i = 0; i < pContextsCache.size(); i++) {
       
    	 SnippetContainer sc = new SnippetContainer(  pContextsCache.get(i));
    	 createSnippetAnnotation(jcas, sc );
     }

     try { 
       System.err.println("Writing out a snippet file " + (pSnippetFileCounter[0]-1));
       
       createRedSnippetFile( jcas);
       
       
     } catch (Exception e) {
       e.printStackTrace(); 
       System.err.println("Issue writing " + e.toString() + " continuing ..."); 
     }
    
     
    
   } // End Method display() ======================
   
private void createRedSnippetFile(JCas pJCas) {

	
	try {
	 String documentId = VUIMAUtil.getDocumentId(pJCas);
	
	 String outputFileName = super.outputDir + "/" + documentId + ".red.snippets.vtt";
	
    VttDocument vttDoc = ((ToVTT) super.outputWriter).processToVttDoc( pJCas);
    
    
    VTTContainer vttContainer = new VTTContainer(vttDoc.getMetaData().toString(true), 
    		                                     vttDoc.getText(), 
    		                                     Tags.toString(vttDoc.getTags().getTags()),
    		                                     Markups.toString(vttDoc.getMarkups().getMarkups()) );

    vttContainer.setFileName( outputFileName);
	
	// --------------------------
	// Parse the text and markups into snippets
    SnippetsContainer snippets = SnippetsContainer.parse( vttContainer );
 
    
    RedSnippetsContainer redSnippetsContainer = new RedSnippetsContainer( snippets );
    
    
   
    System.err.println("Writing out file " + outputFileName );
     // vttSnippetsContainer.write( outputFileName );
   
  //  redSnippetsContainer.convertSnippetsToYesNo();
    redSnippetsContainer.write( outputFileName );
    
	} catch ( Exception e) {
		e.printStackTrace();
		System.err.println("Issue converting from vtt to redSnioppet vtt " + e.toString());
	}
    
   
}



  
   // ----------------------------------------
   // Global variables
   // ----------------------------------------
   
   
  
} // end Class ExampleAnnotator() ---------------
