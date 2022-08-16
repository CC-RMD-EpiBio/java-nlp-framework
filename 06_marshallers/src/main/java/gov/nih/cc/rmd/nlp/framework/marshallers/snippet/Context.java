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
public class Context {



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
   * @param pAssertionStatus
   * @param pConditional
   * @param pHistorical
   * @param pSubject 
   * @param pCategory  
   * @param pOtherFeatures
   * @param pContextNumber
   */
  // =======================================================
  public Context(String  pDocumentId, 
                 String  pAnnotationId, 
                 int     pBeginOffset, 
                 int     pEndOffset, 
                 String  pFocus, 
                 int     pfocusBeginOffset,
                 int     pfocusEndOffset, 
                 String  pSnippet, 
                 String  pRelevance,
                 String  pPatientId,
                 String  pAassertionStatus, 
                 boolean pConditional,
                 boolean pHistorical, 
                 String  pSubject, 
                 String  pCategory,
                 String  pOtherFeatures,
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
    if (pPatientId != null )this.patientId        = pPatientId;
    this.assertionStatus  = pAassertionStatus;
    this.conditional      = pConditional;
    this.historical       = pHistorical;
    this.subject          = pSubject;
    this.category         = pCategory;
    this.otherFeatures    = pOtherFeatures;
    this.contextNumber    = pContextNumber;
    
    
  } // end Constructor() --------------------------------
  
  // =======================================================
   /**
    * Constructor Context - an empty context container
    * 
    */
   // ======================================================
  public Context() {
    
  } // End Constructor Context =============================
    
//=======================================================
 /**
  * Constructor transforms a string in the context format into a Context
  * 
  * @param pRows
  * @return Context
 * @throws Exception 
  */
 // =======================================================
 public Context (List<String> pRows) throws Exception {
  
  
   StringBuffer _snippet = new StringBuffer();
   
   if ( pRows != null && pRows.size() > 0) {
    for ( String row : pRows) {
     
       if      ( row.contains( Context.CONTEXT_DOCUMENT_ID_SLOT)         )       this.documentId = getValueFrom   ( Context.CONTEXT_DOCUMENT_ID_SLOT, row);
       else if ( row.contains( Context.CONTEXT_PATIENT_ID_SLOT)          )        this.patientId = getValueFrom   ( Context.CONTEXT_PATIENT_ID_SLOT, row);
       else if ( row.contains( Context.CONTEXT_ANNOTATION_ID_SLOT)       )     this.annotationId = getValueFrom   ( Context.CONTEXT_ANNOTATION_ID_SLOT, row);
       else if ( row.contains( Context.CONTEXT_SNIPPET_NUMBER_SLOT)      )    this.contextNumber = getIntValueFrom( Context.CONTEXT_SNIPPET_NUMBER_SLOT, row);
       else if ( row.contains( Context.CONTEXT_FILE_SNIPPET_ID)           )    this.fileSnippetId = getIntValueFrom(  Context.CONTEXT_FILE_SNIPPET_ID, row);
       else if ( row.contains( Context.CONTEXT_FOCUS_BEGIN_OFFSET_SLOT)  ) this.focusBeginOffset = getIntValueFrom( Context.CONTEXT_FOCUS_BEGIN_OFFSET_SLOT, row);
       else if ( row.contains( Context.CONTEXT_FOCUS_END_OFFSET_SLOT)    )   this.focusEndOffset = getIntValueFrom( Context.CONTEXT_FOCUS_END_OFFSET_SLOT, row);
       else if ( row.contains( Context.CONTEXT_BEGIN_OFFSET_SLOT)        )      
         this.beginOffset = getIntValueFrom( Context.CONTEXT_BEGIN_OFFSET_SLOT, row);
       
       else if ( row.contains( Context.CONTEXT_END_OFFSET_SLOT)          )        this.endOffset = getIntValueFrom( Context.CONTEXT_END_OFFSET_SLOT, row);
       else if ( row.contains( Context.CONTEXT_FOCUS_SLOT)               )            this.focus = getValueFrom(    Context.CONTEXT_FOCUS_SLOT, row);
       else if ( row.contains( Context.CONTEXT_ASSERTION_STATUS_SLOT)    )  this.assertionStatus = getValueFrom( Context.CONTEXT_ASSERTION_STATUS_SLOT, row);
       else if ( row.contains( Context.CONTEXT_CONDITIONAL_SLOT)         )      this.conditional = getBooleanValueFrom( Context.CONTEXT_CONDITIONAL_SLOT, row);
       else if ( row.contains( Context.CONTEXT_HISTORICAL_SLOT)          )       this.historical = getBooleanValueFrom( Context.CONTEXT_HISTORICAL_SLOT, row);
       else if ( row.contains( Context.CONTEXT_SUBJECT_SLOT)             )          this.subject = getValueFrom(  Context.CONTEXT_SUBJECT_SLOT, row);
       else if ( row.contains( Context.CONTEXT_CATEGORY_SLOT)            )         this.category = getValueFrom( Context.CONTEXT_CATEGORY_SLOT, row);
       else if ( row.contains( Context.CONTEXT_OTHERFEATURES_SLOT)       )    this.otherFeatures = getValueFrom(  Context.CONTEXT_OTHERFEATURES_SLOT, row);
   
       
       
       
     else if ( row.contains( Context.CONTEXT_SNIPPET_SLOT)               )  ;
       else if ( row.contains( Context.CONTEXT_BEGIN_DELIMITER)          )  ;
       else if ( row.contains( Context.CONTEXT_END_DELIMITER)            )  ;   
       else  _snippet.append( row + '\n');
       
    } // end loop thru context text
    this.snippet = _snippet.toString();
   } else {
     throw new Exception("no rows passed in to this constructor"); 
   }
 
 } // End Method fromContext() ======================

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
   * @throws Exception 
   */
  // =======================================================
  public static List<Context> fromContexts(String pBuff) throws Exception {
    
    
    ArrayList<Context> contextList = new ArrayList<Context>();
    
    // ---------------
    // scan for each context in the file
    String rows[] = U.split(pBuff, "\n");
    
    ArrayList<String> buff = new ArrayList<String>();
    for (int i = 0; i < rows.length; i++ ) {
      
        if ( rows[i].equals( Context.CONTEXT_BEGIN_DELIMITER)) { 
      
          // close out the previous context
          if ( buff != null && buff.size() > 0 ) {
            Context aContext = fromContext( buff );
            contextList.add(aContext);
          }
          buff = new ArrayList<String>(); 
          buff.add(rows[i]);
       
            
        } else {
          if ( buff != null) // keep skipping until you get a valid record
            buff.add(rows[i]); 
          else
            System.err.println("corrupt file - no " + Context.CONTEXT_BEGIN_DELIMITER + " seen before this line.  Skipping." );
        
          
      } // end switch
    }  // end loop through the rows of the file
    
    // ------------------
    // clean up (shouldn't be needed unless the file is corrupt)
    if ( buff != null  && buff.size() > 0) {
      // close out the previous context
      Context aContext = fromContext( buff );
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
 * @throws Exception 
  */
 // =======================================================
 public static Context fromContext(List<String> pRows) throws Exception {
   
   Context returnVal  = new Context( pRows);
   
   return returnVal;
 } // End Method fromContext() ============================
  
  
    



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
        String v = pRow.substring(pSlot.length() + 1).trim();
        retValue = Integer.valueOf(v);
      }
      return retValue;
    }  // End Method getValueFrom() ======================
    
    
 // =======================================================
    /**
     * getBooleanValueFrom returns the value from the right hand side of the slot
     * 
     * @param pSlot
     * @param pRow
     * @return boolean
     */
    // =======================================================
    private static boolean getBooleanValueFrom(String pSlot, String pRow) {
     
      boolean retValue = false;
      if ( pRow != null && pSlot != null  && pRow.length() > pSlot.length())  {
        String v = pRow.substring(pSlot.length() + 1);
        retValue = Boolean.valueOf(v);
      }
      return retValue;
    }  // End Method getBooleanValueFrom() ======================
    

 
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
    return this.annotationId;
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
   * getContextNumberBeginOffset returns the offset of where the value
   * of the contextNumber is in the context 
   * 
   * @return int
   */
  // =======================================================
  public int getContextNumberBeginOffset() {
  
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
      
      retVal.append(CONTEXT_BEGIN_DELIMITER                                + "\n");
      retVal.append(CONTEXT_PATIENT_ID_SLOT     + " "  + this.patientId    + "\n"); this.patientIdBeginOffset     = retVal.length() - this.patientId.length()  + 1;
      retVal.append(CONTEXT_DOCUMENT_ID_SLOT    + " "  + this.documentId   + "\n"); this.documentIdBeginOffset    = retVal.length() - this.documentId.length() + 1;
      retVal.append(CONTEXT_ANNOTATION_ID_SLOT  + " "  + this.annotationId + "\n"); 
     
      
      retVal.append(CONTEXT_SNIPPET_NUMBER_SLOT + " "  + contextNo         + "\n"); this.contextNumberBeginOffset = retVal.length() - contextNo.length()       + 1;
      retVal.append(CONTEXT_FILE_SNIPPET_ID     + " "  + this.fileSnippetId + "\n"); this.contextNumberBeginOffset = retVal.length() - contextNo.length()       + 1;
      
      retVal.append(CONTEXT_FOCUS_BEGIN_OFFSET_SLOT  + " "  + this.focusBeginOffset + "\n"); this.setFocusBeginOffsetOffset(retVal.length() - String.valueOf(this.focusBeginOffset).length() + 1);
      retVal.append(CONTEXT_FOCUS_END_OFFSET_SLOT    + " "  + this.focusEndOffset   + "\n"); this.setFocusEndOffsetOffset(retVal.length() - String.valueOf( this.focusEndOffset).length() + 1);
      retVal.append(CONTEXT_FOCUS_SLOT          + " "  + this.focus        + "\n");          this.focusKeyBeginOffset = retVal.length() - this.focus.length() + 1; 
     
      retVal.append(CONTEXT_ASSERTION_STATUS_SLOT   + " "  + this.assertionStatus        + "\n");          
      retVal.append(CONTEXT_CONDITIONAL_SLOT        + " "  + this.conditional            + "\n");          
      retVal.append(CONTEXT_HISTORICAL_SLOT         + " "  + this.historical             + "\n");         
      retVal.append(CONTEXT_SUBJECT_SLOT            + " "  + this.subject                + "\n");
      retVal.append(CONTEXT_CATEGORY_SLOT           + " "  + this.category               + "\n");
      retVal.append(CONTEXT_OTHERFEATURES_SLOT      + " "  + this.otherFeatures          + "\n");
       
      
      this.snippetBeginOffset       = retVal.length() + CONTEXT_SNIPPET_SLOT.length()  + "\n".length();
      retVal.append(CONTEXT_SNIPPET_SLOT                                   + "\n"); 
      retVal.append(this.snippet                                           + "\n"); 
      retVal.append(CONTEXT_END_DELIMITER                                  + "\n");
      
     
      
      return retVal.toString();
  }  // End Method toContextFormatString1() ======================

    // =======================================================
    /**
     * toString 
     * @return String
     */
    // =======================================================
    @Override 
    public String toString() {
     return toContextFormatString1();
     
  }  // End Method toString() ======================

    

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



   // =======================================================
    /**
     * getAssertionStatus
     * 
     * @return String  
     */
    // =======================================================
    public String getAssertionStatus() {
      return assertionStatus;
    } // End Method getAssertionStatus() ======================

    // =======================================================
    /**
     * getConditional 
     * 
     * @return boolean
     */
    // =======================================================
    public boolean getConditional() {
      return conditional;
    }  // End Method getConditional() ======================

    // =======================================================
    /**
     * getHistorical 
     * 
     * @return boolean
     */
    // =======================================================
    public boolean getHistorical() {
     return this.historical;
    
    }  // End Method getHistorical() ======================
    
    // =======================================================
    /**
     * getFileSnippetId
     * 
     * @return int
     */
    // =======================================================
    public int getFileSnippetId( ) {
      return this.fileSnippetId;
    }  // End Method getFileSnippetId() ======================

    // =======================================================
    /**
     * getOtherFeatures 
     * 
     * @return String
     */
    // =======================================================
    public String getOtherFeatures() {
     return otherFeatures;
    }  // End Method getOtherFeatures() ======================

    // =======================================================
    /**
     * getCategory
     * 
     * @return String
     */
    // =======================================================
    public String getCategory() {
     return category;
    }  // End Method getCategory() ======================

    // =======================================================
    /**
     * getSubject
     * 
     * @return String
     */
    // =======================================================
    public String getSubject() {
     return subject;
    }  // End Method getSubject() ======================

     // ==========================================
    /**
     * getSnippetId (really the contextNumber)
     *
     * @return int
     */
    // ==========================================
    public int getSnippetId() {
     
      return this.contextNumber;
    } // end Method getSnippetId() ==============
    
    
    
    
    // =======================================================
     /**
      * setAssertionStatus
      * 
      * @param pAssertionStatus   
      */
     // =======================================================
     public void setAssertionStatus(String pAssertionStatus) {
        assertionStatus = pAssertionStatus;
     } // End Method setAssertionStatus() ======================

     // =======================================================
     /**
      * setConditional 
      * 
      * @param pConditional
      */
     // =======================================================
     public void setConditional( boolean pConditional) {
       this.conditional = pConditional;
     }  // End Method setConditional() ======================

     // =======================================================
     /**
      * setHistorical 
      * 
      * @param pHistorical
      */
     // =======================================================
     public void setHistorical(boolean pHistorical) {
       this.historical = pHistorical;
     
     }  // End Method setHistorical() ======================

     // =======================================================
     /**
      * setOtherFeatures 
      * 
      * @param pOtherFeatures
      */
     // =======================================================
     public void setOtherFeatures(String pOtherFeatures) {
       otherFeatures = pOtherFeatures;
     }  // End Method getOtherFeatures() ======================

     // =======================================================
     /**
      * setCategory
      * 
      * @param pCategory
      */
     // =======================================================
     public void setCategory(String pCategory) {
       category = pCategory;
     }  // End Method getCategory() ===========================

     // =======================================================
     /**
      * setSubject
      * 
      * @param pSubject
      */
     // =======================================================
     public void setSubject(String pSubject) {
       category = pSubject;
     }  // End Method setSubject

     // =======================================================
     /**
      * setSnippetId  (this is the unique snippet id across the whole 
      * set of pipelines)
      * 
      * @param pId
      */
     // =======================================================
     public void setSnippetId(int pId ) {
       this.contextNumber = pId;
     }  // End Method setSnippetId() ======================

     
     
     // =======================================================
     /**
      * setFileSnippetId this is the snippet id within the snippet file
      * 
      * @param pId
      */
     // =======================================================
     public void setFileSnippetId(int pId ) {
       this.fileSnippetId = pId;
     }  // End Method setFileSnippetId() ======================


  // ---------------------------------
  // Global Variables
  public static final String   CONTEXT_BEGIN_DELIMITER           = "=Snippet-Begin=====================================================================";
  public static final String   CONTEXT_END_DELIMITER             = "========================================================================Snippet-End=";
  public static final String   CONTEXT_SNIPPET_SLOT              = "============================================================================Snippet=";

  public static final String   CONTEXT_PATIENT_ID_SLOT           = "Patient ID:";
  public static final String   CONTEXT_DOCUMENT_ID_SLOT          = "Document ID:";
  public static final String   CONTEXT_ANNOTATION_ID_SLOT        = "AnnotationId:";
  public static final String   CONTEXT_SNIPPET_NUMBER_SLOT       = "Snippet ID:    ";
  public static final String   CONTEXT_BEGIN_OFFSET_SLOT         = "BeginOffset:";
  public static final String   CONTEXT_END_OFFSET_SLOT           = "EndOffset:";
  
  public static final String   CONTEXT_FOCUS_SLOT                = "Focus:";
  public static final String   CONTEXT_FOCUS_BEGIN_OFFSET_SLOT   = "FocusBeginOffset:";
  public static final String   CONTEXT_FOCUS_END_OFFSET_SLOT     = "FocusEndOffset:";
  
  public static final String   CONTEXT_ASSERTION_STATUS_SLOT     = "AssertionStatus:";
  public static final String   CONTEXT_CONDITIONAL_SLOT          = "Conditional:";
  public static final String   CONTEXT_HISTORICAL_SLOT           = "Historical:";
  public static final String   CONTEXT_SUBJECT_SLOT              = "Subject:";
  public static final String   CONTEXT_CATEGORY_SLOT             = "Category:";
  public static final String   CONTEXT_OTHERFEATURES_SLOT        = "OtherFeatures:";
  public static final String   CONTEXT_FILE_SNIPPET_ID           = "File Snippet Id: ";
  
  
  
  
  
 
  public static final String   ATTRIBUTE_FIELD_DELIMITER         = "<::>";
  public static final String   CONTEXT_FOCUS_LABEL               = "Concept";
  public static final String   SNIPPET_COLUMN_LABEL              = "SnippetColumn";  
  public static final String   SNIPPET_BLOCK_BEGIN_DELIMETER     = "Snippet Text:";
  
  
  protected String annotationId = null;
  protected int contextNumber = -1;
  protected int beginOffset = 0;
  protected int endOffset = 0;
  protected String focus = null;
  protected int focusBeginOffset = -1;
  protected int focusEndOffset = 0;

  protected int focusBeginOffsetOffset = 0;
  protected int focusEndOffsetOffset = 0;
  protected int focusKeyBeginOffset = 0;
  protected String snippet = null;
  protected String relevance = null;
  protected String patientId = "";
  protected String documentId = null;
  protected int fileSnippetId = 1;
  protected int patientIdBeginOffset;
  protected int documentIdBeginOffset;
  protected int annotationIdBeginOffset;
  protected int contextNumberBeginOffset;
  protected int snippetBeginOffset;
  protected boolean historical;
  protected String assertionStatus;
  protected boolean conditional;
  protected String otherFeatures;
  protected String subject;
  protected String category;
  

  
  
 

 
  
} // end Class Context
