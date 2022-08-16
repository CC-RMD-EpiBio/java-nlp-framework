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
