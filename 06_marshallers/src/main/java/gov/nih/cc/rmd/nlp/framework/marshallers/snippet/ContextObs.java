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
// =======================================================
/**
 * Context is a container to hold a complete context
 *
 * @author  guy
 * @created Aug 30, 2013
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.snippet;

import gov.nih.cc.rmd.nlp.framework.utils.U;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guy
 *
 */
public class ContextObs {



  // =======================================================
  /**
   * Constructor Context 
   *
   * @param pDocumentId
   * @param pAnnotationId
   * @param pBeginOffset
   * @param pEndOffset
   * @param pFocus
   * @param pfocusBeginOffset
   * @param pfocusEndOffset
   * @param pSnippet
   * @param pRelevance
   * @param pPatientId
   * @param pContextNumber
   */
  // =======================================================
  public ContextObs(String  pDocumentId, 
                 String  pAnnotationId, 
                 int     pBeginOffset, 
                 int     pEndOffset, 
                 String  pFocus, 
                 int     pfocusBeginOffset,
                 int     pfocusEndOffset, 
                 String  pSnippet, 
                 String  pRelevance,
                 String  pPatientId,
                 int     pContextNumber) {
    
    this.documentId         = pDocumentId;
    this.annotationId     = pAnnotationId;
    this.beginOffset      = pBeginOffset;
    this.endOffset        = pEndOffset;
    this.focus            = pFocus;
    this.focusBeginOffset = pfocusBeginOffset;
    this.focusEndOffset   = pfocusEndOffset;
    this.snippet          = pSnippet;
    this.relevance        = pRelevance;
    this.patientId        = pPatientId;
    this.contextNumber    = pContextNumber;
    
    
  } // end Constructor() --------------------------------
  
  // =======================================================
  /**
   * fromContext breaks the context file into contexts
   * 
   * The context file includes a series of 
   *   #BEGIN================
   *   ...
   *   #END================= 
   * 
   * @param pBuff
   * @return Context[]
   */
  // =======================================================
  public static List<ContextObs> fromContexts(String pBuff) {
    
    
    ArrayList<ContextObs> contextList = new ArrayList<ContextObs>();
    
    // ---------------
    // scan for each context in the file
    String rows[] = U.split(pBuff, "\n");
    
    ArrayList<String> buff = new ArrayList<String>();
    for (int i = 0; i < rows.length; i++ ) {
      
        if ( rows[i].equals( ContextObs.CONTEXT_BEGIN_DELIMITER)) { 
      
          // close out the previous context
          if ( buff != null && buff.size() > 0 ) {
            ContextObs aContext = fromContext( buff );
            contextList.add(aContext);
          }
          buff = new ArrayList<String>(); 
          buff.add(rows[i]);
       
            
        } else {
          if ( buff != null) // keep skipping until you get a valid record
            buff.add(rows[i]); 
          else
            System.err.println("corrupt file - no " + ContextObs.CONTEXT_BEGIN_DELIMITER + " seen before this line.  Skipping." );
        
          
      } // end switch
    }  // end loop through the rows of the file
    
    // ------------------
    // clean up (shouldn't be needed unless the file is corrupt)
    if ( buff != null  && buff.size() > 0) {
      // close out the previous context
      ContextObs aContext = fromContext( buff );
      contextList.add(aContext);
      buff = null;
      
      //System.err.println("Fragment ending - no " + Context.CONTEXT_END_DELIMITER + " seen for this record");
    }
      
    
    
    if ( contextList == null || contextList.size() == 0 )
      contextList = null;
    return contextList;
    
    // End Method fromContext() ======================
  }


    // =======================================================
  /**
   * fromContext transforms a string in the context format into a Context
   * 
   * @param pRows
   * @return Context
   */
  // =======================================================
  private static ContextObs fromContext(List<String> pRows) {
   
    ContextObs aContext = null;
    
    String fileName = null;
    String annotationId = null;
    int beginOffset = 0;
    int endOffset = 0;
    String focus = null;
    int snippetBeginOffset = 0;
    int snippetEndOffset = 0;
    String relevance = "not set";
    String patientId = "0000000000";
    int contextNumber = 0;
    StringBuffer snippet = new StringBuffer();
    
    if ( pRows != null && pRows.size() > 0) {
     for ( String row : pRows) {
      
        if      ( row.contains( ContextObs.CONTEXT_DOCUMENT_ID_SLOT)         )           fileName = getValueFrom   ( ContextObs.CONTEXT_DOCUMENT_ID_SLOT, row);
        else if ( row.contains( ContextObs.CONTEXT_ANNOTATION_ID_SLOT)       )       annotationId = getValueFrom   ( ContextObs.CONTEXT_ANNOTATION_ID_SLOT, row);
        else if ( row.contains( ContextObs.CONTEXT_BEGIN_OFFSET_SLOT)        )        beginOffset = getIntValueFrom( ContextObs.CONTEXT_BEGIN_OFFSET_SLOT, row);
        else if ( row.contains( ContextObs.CONTEXT_END_OFFSET_SLOT)          )          endOffset = getIntValueFrom( ContextObs.CONTEXT_END_OFFSET_SLOT, row);
        else if ( row.contains( ContextObs.CONTEXT_FOCUS_SLOT)               )              focus = getValueFrom(    ContextObs.CONTEXT_FOCUS_SLOT, row);
        else if ( row.contains( ContextObs.CONTEXT_FOCUS_BEGIN_OFFSET_SLOT)  ) snippetBeginOffset = getIntValueFrom( ContextObs.CONTEXT_FOCUS_BEGIN_OFFSET_SLOT, row);
        else if ( row.contains( ContextObs.CONTEXT_FOCUS_END_OFFSET_SLOT)    )   snippetEndOffset = getIntValueFrom( ContextObs.CONTEXT_FOCUS_END_OFFSET_SLOT, row);
        else if ( row.contains( ContextObs.CONTEXT_SNIPPET_SLOT)             )  ;
        else if ( row.contains( ContextObs.CONTEXT_BEGIN_DELIMITER)          )  ;
        else if ( row.contains( ContextObs.CONTEXT_END_DELIMITER)            )  ;   
        else  snippet.append( row + '\n');
        
        
        
    }
      
     aContext = new ContextObs( fileName, annotationId, beginOffset, endOffset, focus, snippetBeginOffset, snippetEndOffset,snippet.toString(), relevance, patientId, contextNumber);
      
      
    }
    
    return aContext;
  } // End Method fromContext() ======================



    // =======================================================
    /**
     * getValueFrom returns the value from the right hand side of the slot
     * 
     * @param pSlot
     * @param pRow
     * @return
     */
    // =======================================================
    private static String getValueFrom(String pSlot, String pRow) {
     
      String retValue = null;
      if ( pRow != null && pSlot != null  && pRow.length() > pSlot.length())  {
        retValue = pRow.substring(pSlot.length() + 1);
      }
      return retValue;
    }  // End Method getValueFrom() ======================
    

    // =======================================================
    /**
     * getIntValueFrom returns the value from the right hand side of the slot
     * 
     * @param pSlot
     * @param pRow
     * @return
     */
    // =======================================================
    private static int getIntValueFrom(String pSlot, String pRow) {
     
      int retValue = -1;
      if ( pRow != null && pSlot != null  && pRow.length() > pSlot.length())  {
        String v = pRow.substring(pSlot.length() + 1);
        retValue = Integer.valueOf(v);
      }
      return retValue;
    }  // End Method getValueFrom() ======================
    
    

 
  /**
   * @return the snippet
   */
  public final String getSnippet() {
    return snippet;
  }
  /**
   * @param snippet the snippet to set
   */
  public final void setSnippet(String snippet) {
    this.snippet = snippet;
  }
  /**
   * @return the focusEndOffset
   */
  public final int getFocusEndOffset() {
   
      
    return this.focusEndOffset;
  }
  /**
   * @param focusEndOffset the focusEndOffset to set
   */
  public final void setfocusEndOffset(int focusEndOffset) {
    this.focusEndOffset = focusEndOffset;
  }
  /**
   * @return the focusBeginOffset
   */
  public final int getFocusBeginOffset() {
    
   
    return this.focusBeginOffset;
  }
  /**
   * @param pFocusBeginOffset the focusBeginOffset to set
   */
  public final void setfocusBeginOffset(int pFocusBeginOffset) {
    
   this.focusBeginOffset = pFocusBeginOffset;
      
  }
  /**
   * @return the focus
   */
  public final String getFocus() {
    return focus;
  }
  /**
   * @param focus the focus to set
   */
  public final void setFocus(String focus) {
    this.focus = focus;
  }
  /**
   * @return the endOffset
   */
  public final int getEndOffset() {
    return endOffset;
  }
  /**
   * @param endOffset the endOffset to set
   */
  public final void setEndOffset(int endOffset) {
    this.endOffset = endOffset;
  }
  /**
   * @return the beginOffset
   */
  public final int getBeginOffset() {
    return beginOffset;
  }
  /**
   * @param beginOffset the beginOffset to set
   */
  public final void setBeginOffset(int beginOffset) {
    this.beginOffset = beginOffset;
  }
  /**
   * @return the annotationId
   */
  public final String getAnnotationId() {
    return annotationId;
  }
  /**
   * @param annotationId the annotationId to set
   */
  public final void setAnnotationId(String annotationId) {
    this.annotationId = annotationId;
  }
  // =======================================================
  /**
   * getPatientId
   * 
   * @return
   */
  // =======================================================
  public String getPatientId() {
  
    return this.patientId; 
  }  // End Method getPatientId() ======================

  // =======================================================
  /**
   * getDocumentId 
   * 
   * @return String
   */
  // =======================================================
  public String getDocumentId() {
    return this.documentId;
  }  // End Method getDocumentId() ======================
  

  
  // =======================================================
  /**
   * setRelevance sets the relevance
   * 
   * @param pRelevance
   */
  // =======================================================
  public void setRelevance(String pRelevance) {
   this.relevance = pRelevance;
  }  // End Method setRelevancy() ======================

  
  // =======================================================
  /**
   * getContextNumber returns the sequential number given when the context was created.
   * 
   * @return int
   */
  // =======================================================
  public int getContextNumber() {
    return this.contextNumber;
  }   // End Method getContextNumber() ======================
  

  // =======================================================
  /**
   * getContextNumberString
   * 
   * @return String
   */
  // =======================================================
  public String getContextNumberString() {
    return U.pad(this.contextNumber);
   
  }  // End Method getContextNumberString() ======================
  

  // =======================================================
  /**
   * getRelevance gets the relevance
   * 
   * @returns String
   */
  // =======================================================
  public String getRelevance() {
   return this.relevance;
  }  // End Method getRelevancy() ======================



  // =======================================================
  /**
   * getPatientIdBeginOffset returns the offset where the
   * value of the patient id is in the context.
   * 
   * @return int
   */
  // =======================================================
  public int getPatientIdBeginOffset() {
    return this.patientIdBeginOffset;
  }  // End Method getPatientIdBeginOffset() ======================

  // =======================================================
  /**
   * getDocumentIdBeginOffset returns the offset of where the value
   * of the documentId is in the context 
   * 
   * @return int
   */
  // =======================================================
  public int getDocumentIdBeginOffset() {
   return this.documentIdBeginOffset;
  }  // End Method getDocumentIdBeginOffset() ======================

  // =======================================================
  /**
   * getAnnotationIdBeginOffset [Summary here]
   * 
   * @return
   */
  // =======================================================
  public int getAnnotationIdBeginOffset() {
    return this.annotationIdBeginOffset;
  }  // End Method getAnnotationIdBeginOffset() ======================
  

  // =======================================================
  /**
   * getContextNumberOffset returns the offset of where the value
   * of the contextNumber is in the context 
   * 
   * @return int
   */
  // =======================================================
  public int getContextNumberOffset() {
  
    return this.contextNumberBeginOffset;
  }  // End Method getContextNumberOffset() ======================

  // =======================================================
  /**
   * getSnippetBeginOffset returns the offset where the
   * snippet text starts (not where the delimiter starts)
   * 
   * @return int
   */
  // =======================================================
  public int getSnippetBeginOffset() {
    return this.snippetBeginOffset;
  
  } // End Method getSnippetBeginOffset() ======================

  // =======================================================
    /**
     * toContextFormatString1 returns a string of the form:
     * Patient ID: 1
     * Document ID: 1
     * Snippet Number: 1  
     * Snippet Text: 
     *  
     * Text starts here line 1
     *  line 2
     *  ..
     *  last line
     * SNIPPET END ---------------------------------------------------------------------------------
     *  
     *  As a side affect, the patientIdBeginOffset, 
     *  documentIdBeginOffset, contextNumberBeginOffset and 
     *  snippetBeginOffset get set
     * 
     * @return String
     */
    // =======================================================
    public String toContextFormatString1() {
      
      StringBuffer retVal = new StringBuffer();
      String contextNo = U.pad( this.contextNumber);
      
      retVal.append(CONTEXT_PATIENT_ID_SLOT     + " "  + this.patientId    + "\n"); this.patientIdBeginOffset     = retVal.length() - this.patientId.length()  + 1;
      retVal.append(CONTEXT_DOCUMENT_ID_SLOT    + " "  + this.documentId   + "\n"); this.documentIdBeginOffset    = retVal.length() - this.documentId.length() + 1;
      retVal.append(CONTEXT_ANNOTATION_ID_SLOT  + " "  + this.annotationId + "\n"); 
      this.annotationIdBeginOffset    = retVal.length() - this.annotationId.length() + 1;
      
      retVal.append(CONTEXT_SNIPPET_NUMBER_SLOT + " "  + contextNo         + "\n"); this.contextNumberBeginOffset = retVal.length() - contextNo.length()       + 1;
      retVal.append(CONTEXT_FOCUS_BEGIN_OFFSET_SLOT  + " "  + this.focusBeginOffset + "\n"); this.setFocusBeginOffsetOffset(retVal.length() - String.valueOf(this.focusBeginOffset).length() + 1);
      retVal.append(CONTEXT_FOCUS_END_OFFSET_SLOT    + " "  + this.focusEndOffset   + "\n"); this.setFocusEndOffsetOffset(retVal.length() - String.valueOf( this.focusEndOffset).length() + 1);
      retVal.append(CONTEXT_FOCUS_SLOT          + " "  + this.focus        + "\n");          this.focusKeyBeginOffset = retVal.length() - this.focus.length() + 1; 
          this.snippetBeginOffset       = retVal.length() + CONTEXT_SNIPPET_SLOT.length()  + 2;
      retVal.append(CONTEXT_SNIPPET_SLOT                                   + "\n"); 
      retVal.append(snippet                                                + "\n"); 
      retVal.append(CONTEXT_END_DELIMITER                                  + "\n");
      
     
      
      return retVal.toString();
  }  // End Method toContextFormatString1() ======================



  /**
     * @return the focusEndOffsetOffset
     */
    public int getFocusEndOffsetOffset() {
      return focusEndOffsetOffset;
    }

    /**
     * @param focusEndOffsetOffset the focusEndOffsetOffset to set
     */
    public void setFocusEndOffsetOffset(int focusEndOffsetOffset) {
      this.focusEndOffsetOffset = focusEndOffsetOffset;
    }



  /**
     * @return the focusBeginOffsetOffset
     */
    public int getFocusBeginOffsetOffset() {
      return focusBeginOffsetOffset;
    }

    /**
     * @param focusBeginOffsetOffset the focusBeginOffsetOffset to set
     */
    public void setFocusBeginOffsetOffset(int focusBeginOffsetOffset) {
      this.focusBeginOffsetOffset = focusBeginOffsetOffset;
    }



  /**
     * @return the focusKeyBeginOffset
     */
    public int getFocusKeyBeginOffset() {
      return focusKeyBeginOffset;
    }

    /**
     * @param pFocusKeyBeginOffset the  pFocusKeyBeginOffset to set
     */
    public void setFocusKeyBeginOffset(int  pFocusKeyBeginOffset) {
      this.focusKeyBeginOffset = pFocusKeyBeginOffset;
    }



  // ---------------------------------
  // Global Variables
  public static final String   CONTEXT_BEGIN_DELIMITER           = "-Snippet-Begin---------------------------------------------------------";
  public static final String   CONTEXT_END_DELIMITER             = "-Snippet-End----------------------------------------------------------------";
  public static final String   CONTEXT_PATIENT_ID_SLOT           = "Patient ID:";
  public static final String   CONTEXT_DOCUMENT_ID_SLOT          = "Document ID:";
  public static final String   CONTEXT_ANNOTATION_ID_SLOT        = "AnnotationId:";
  public static final String   CONTEXT_SNIPPET_NUMBER_SLOT       = "Snippet Number:";
  public static final String   CONTEXT_BEGIN_OFFSET_SLOT         = "BeginOffset:";
  public static final String   CONTEXT_END_OFFSET_SLOT           = "EndOffset:";
  
  public static final String   CONTEXT_FOCUS_SLOT                = "Focus:";
  public static final String   CONTEXT_FOCUS_BEGIN_OFFSET_SLOT   = "FocusBeginOffset:";
  public static final String   CONTEXT_FOCUS_END_OFFSET_SLOT     = "FocusEndOffset:";
  public static final String   CONTEXT_SNIPPET_SLOT              = "Snippet Text:";
  public static final String   ATTRIBUTE_FIELD_DELIMITER         = "<::>";
  public static final String   CONTEXT_FOCUS_LABEL               = "Concept";
  public static final String   SNIPPET_COLUMN_LABEL              = "SnippetColumn";  
  private String annotationId = null;
  private int contextNumber = -1;
  private int beginOffset = 0;
  private int endOffset = 0;
  private String focus = null;
  private int focusBeginOffset = -1;
  private int focusEndOffset = 0;

  private int focusBeginOffsetOffset = 0;
  private int focusEndOffsetOffset = 0;
  private int focusKeyBeginOffset = 0;
  private String snippet = null;
  private String relevance = null;
  private String patientId = null;
  private String documentId = null;
  private int patientIdBeginOffset;
  private int documentIdBeginOffset;
  private int annotationIdBeginOffset;
  private int contextNumberBeginOffset;
  private int snippetBeginOffset;
  

  
  
 

 
  
} // end Class Context
