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
