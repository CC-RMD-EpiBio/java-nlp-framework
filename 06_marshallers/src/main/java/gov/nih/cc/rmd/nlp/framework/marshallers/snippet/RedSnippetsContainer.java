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
 * RedSnippetsContainer.java  TODO file description
 *
 *
 * @author  Guy Divita 
 * @created Sep 22, 2017
 *
 **  *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.snippet;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.U;

// ===========================================
/**
 * Public Types 
 */
// ===========================================
public class RedSnippetsContainer {

  private List<RedSnippetContainer> snippets = null;
  private String             metaDataSection = null;
  private String               labelsSection = null;
  private String                 textSection = null;
  private String              markupsSection = null;
  private String                    fileName = null;



  // =======================================================
   /**
    * Constructor 
    * 
    *    transform the following fields
    *    
    *    v3NLPFramework                  Red
    *    -------------                  -------
    *    CONTEXT_BEGIN_DELIMITER        -------------
    *    CONTEXT_PATIENT_ID_SLOT        PatientICN:
    *    CONTEXT_DOCUMENT_ID_SLOT       Document
    *    CONTEXT_SNIPPET_NUMBER_SLOT    Snippet Num:
    *    CONTEXT_SNIPPET_SLOT           Snippet Text:\n
    *                                    
    *                                    
    *    For each snippet create the following markups:                                
    *    12| 1|SnippetColumn  |               |snippetNumber="1"<::>columnNumber="1"<::>columnName="PatientICN"|  [ pv3NLPSnippetsContainer.getPatientId() ]
    *    24|12|SnippetColumn  |               |snippetNumber="1"<::>columnNumber="2"<::>columnName="Document"|    [ pv3NLPSnippetsContainer.getDocumentId() ]
    *    50| 1|SnippetColumn  |               |snippetNumber="1"<::>columnNumber="3"<::>columnName="Snippet Num"| [ pv3NLPSnippetsContainer.getSnippitId() ]
    *   68|720|SnippetColumn  |               |snippetNumber="1"<::>columnNumber="4"<::>columnName="Snippet Text"|[ pv3NLPSnippetsContainer.getNormalizedSnippet() ]
    *  
    *  Transform the focus decision to 
    *  
    *  268|145|pv3NLPSnippetsContainer.getDecision()|               ||pv3NLPSnippetsContainer.getNormalizedFocus()                    
    * 
    * @param vttSnippetsContainer
    */
   // ======================================================
  public RedSnippetsContainer(SnippetsContainer pv3NLPSnippetsContainer) {
   
    List<SnippetContainer> v3NLPSnippets = pv3NLPSnippetsContainer.getSnippets() ;
    
    this.snippets = new ArrayList<RedSnippetContainer>();
    for ( SnippetContainer v3NLPSnippet : v3NLPSnippets) {
      RedSnippetContainer aRedSnippet = new RedSnippetContainer( v3NLPSnippet);
      this.snippets.add( aRedSnippet);
    } // end loop thru v3NLP snippets
 
    this.metaDataSection = pv3NLPSnippetsContainer.getMetaData();
    this.labelsSection   = pv3NLPSnippetsContainer.getLabels();
    
    this.fileName = pv3NLPSnippetsContainer.getVttContainer().getFileName();
    
    transformV3NLPLabelsToRedLabels();
    
    
  }  // End Constructor RedSnippetsContainer =============
    
  

  // =======================================================
   /**
    * transformV3NLPLabelsToRedLabels  
    *    V3NP labels include Snippet, true, false, uncertain
    *    
    *    Red labels include yes, no, maybe, and SnippetColumn
    *    
    *    we would like to have the labels be
    *    yes, no, maybe, snippet, and SnippetColumn
    *    
    *    Also fix the colors to
    *      yes -> green
    *      no  -> red
    *      maybe -> pink
    *      SnippetColumn -> grey
    *      
    *  
    *
    */
   // ======================================================	
  private void transformV3NLPLabelsToRedLabels() {
   
    this.labelsSection = 
    "Yes||true|false|false|true|0|0|0|200|255|200|Monospaced|12\n" +
    "No||true|false|false|true|255|255|255|255|0|0|Monospaced|12\n" +
    "Uncertain||true|false|false|true|0|0|255|153|204|255|Monospaced|12\n" +
    "SnippetColumn||false|false|false|true|0|0|0|228|228|228|Monospaced|+0\n" + 
    "Snippet||false|false|false|true|0|0|0|255|255|0|Monospaced|+0\n";
    
    
    } // End Method transformV3NLPLabelsToRedLabels ========
    
  



  // =======================================================
   /**
    * write writes out a vtt file in the Red Cat/Ex style
    * 
    * @param pOutputFileName 
   * @throws Exception 
    *
    */
   // =====================================================	
  public void write(String pOutputFileName) throws Exception {
  
    
     PrintWriter out = null;
     try {
    
         String buff = write();
         
         out = new PrintWriter( pOutputFileName );
         out.print(buff);
         out.close();
         
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method write " + e.getMessage() );
        if ( out != null ) out.close();
        throw e;
      }
     
     
    } // End Method write ===============================



  // =======================================================
   /**
    * write  returns a string of vtt file in the Red Cat/Ex style
    * 
    * @return String
   * @throws Exception 
    *
    */
   // ======================================================	
  public String write() throws Exception {
    
    StringBuffer buff = new StringBuffer();
    
     try {
    	
     
       buff.append(  VTTContainer.MetadataHeader );
       buff.append( this.metaDataSection);
       
      createVttTextAndMarkupsSectionFromSnippets();
       
       buff.append(VTTContainer.TextHeader );
       buff.append( this.textSection);
       
       buff.append(VTTContainer.LabelsHeader);  
       buff.append( this.labelsSection);
       
       buff.append(VTTContainer.MarkupsHeader);
       buff.append( this.markupsSection );
       
      
    
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue within method write " + e.getMessage() );
        throw e;
      }
     
     return buff.toString();
    } // End Method write ===============================
    
  // =======================================================
  /**
   * createVttTextAndMarkupsSectionFromSnippets creates text section
   * from the snippets
  * @throws Exception 
   *
   */
  // ======================================================  
 private final void createVttTextAndMarkupsSectionFromSnippets() throws Exception {
   
   StringBuffer textPart = null;
   StringBuffer markupPart = null;
   try {
      textPart = new StringBuffer();
      markupPart = new StringBuffer();
      int currentOffset = 0;
      for ( int i = 0; i < this.snippets.size(); i++ ) {
         RedSnippetContainer aSnippet = this.snippets.get(i);
        textPart.append( aSnippet.toString());
        
        RedSnippetMarkups snippetMarkups =  aSnippet.getMarkups( currentOffset );
        markupPart.append( snippetMarkups.toString());
        
        currentOffset+=aSnippet.toString().length();
        
        
        
        
        
      } // end loop thru snippets
      
      this.textSection = textPart.toString();
      this.markupsSection = markupPart.toString();
   
     } catch ( Exception e2 ) {
       e2.printStackTrace();
       System.err.println("Issue within method updateVttTextSectionFromSnippets " + e2.getMessage() );
       throw e2;
     }
   } // End Method createVttTextAndMarkupsSectionFromSnippets ============


 // =======================================================
  /**
   * convertSnippetsToYesNo
   * 
   * 
   */
  // ======================================================	
 public final void  convertSnippetsToYesNo()  {
	 String label = "Yes";
	 if (this.fileName.toLowerCase().contains("negated"))
		 label = "No";
	 
	for ( RedSnippetContainer aSnippet : this.snippets ) {
		aSnippet.setLabel(label);
	}
   
 } // end Method convertSnippetsToYesNo() ==================
  

} // End Class RedSnippetsContainer ============
