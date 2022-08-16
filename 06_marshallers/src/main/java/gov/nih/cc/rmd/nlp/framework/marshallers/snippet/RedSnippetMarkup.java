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
 * RedSnippetMarkup is a container for a red cat/ex style markup line
 * 
 *   includes 
 *
 * 12| 1|SnippetColumn  |               |snippetNumber="1"<::>columnNumber="1"<::>columnName="PatientICN"|1
 * @author  Guy Divita 
 * @created Sep 25, 2017
 *
 **  *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.snippet;

import gov.nih.cc.rmd.nlp.framework.utils.U;

// ===========================================
/**
 * Public Types 
 */
// ===========================================
public class RedSnippetMarkup {

  // =======================================================
   /**
    * Constructor 
    * 
    * @param pBeginOffset
    * @param pLength
    * @param pSnippetNumber
    * @param pColumnNumber
    * @param pColumnName
    * @param pColumnValue
    */
   // ======================================================
  public RedSnippetMarkup(int    pBeginOffset, 
                          int    pLength, 
                          int    pSnippetNumber, 
                          int    pColumnNumber, 
                          String pColumnName,
                          String pColumnValue) {
    
    this.beginOffset = pBeginOffset;
    this.length = pLength;
    this.label = "SnippetColumn  ";
    this.snippetNumber = pSnippetNumber;
    this.columnNumber = pColumnNumber;
    this.columnName = pColumnName;
    this.columnValue = U.normalize(pColumnValue);
    
    
  
  } // End Constructor RedSnippetMarkup =============
    
  

  


  // =======================================================
   /**
    * Constructor for the decision or focus label
    * 
    * @param pBeginOffset
    * @param pLength
    * @param pLabel
    * @param pFocus
    */
   // ======================================================
  public RedSnippetMarkup(int pBeginOffset, int pLength, String pLabel, String pFocus) {
    
    this.beginOffset = pBeginOffset;
    this.length = pLength;
    this.label = pLabel;
    
    this.columnValue = U.normalize(pFocus);
    
  } // End Constructor RedSnippetMarkup =============
    
  
  
// =======================================================
   /**
    * Constructor creates a Markup from a red markup row
    *    this will mess up if there are Pipes in the value field
    * 
    * @param pRow
    */
   // ======================================================
  public RedSnippetMarkup(String pRow) {
    
    
    String[] cols = U.split(pRow);  
    
    if ( cols != null && cols.length > 5) {
      this.beginOffset = Integer.parseInt(cols[0].trim() );
      this.length      = Integer.parseInt(cols[1].trim() );
      this.category    = cols[2].trim();
      this.subCategory = cols[3];
      this.columnValue = cols[5]; 
      
      
      if ( cols[4] != null && !cols[4].isEmpty()) {
      // parse thru cols[4] via <::> delimiters
      
      
      String[] subCols = U.split_StringDelimited( cols[4], "<::>");
      
      if ( subCols != null && subCols.length > 2 ) {
        
        for ( String subColumn : subCols ) {
          try {
          if      ( subColumn.startsWith("snippetNumber"))  this.snippetNumber = U.getIntValueFromSlotValue( subColumn) ;
          else if ( subColumn.startsWith("columnNumber"))   this.columnNumber  = U.getIntValueFromSlotValue( subColumn) ;
          else if ( subColumn.startsWith("columnName"))     this.columnName    = U.getValueFromSlotValue( subColumn) ;
          } catch (Exception e) {
            e.printStackTrace();
            String msg = "Issue with parsing the red markup subfields : " + e.toString();
            System.err.println(msg);
          }
        } // end loop thru sub columns
      } // end if there are additional sub fields
    } // end if there are enough fields
    } // end if there is a column 4 
  
  }  // End Constructor RedSnippetMarkup =============
    
  










  //=======================================================
  /**
   * toString
   * 
   *      12| 1|SnippetColumn  |               |snippetNumber="1"<::>columnNumber="1"<::>columnName="PatientICN"|1
   *      
   *      or 
   *      
   *      268|145|Yes            |              ||PAST MEDICAL HISTORY:   
   *    
   * @return String
   *
   */
  // ====================================================  
 public String toString() {
  
   StringBuffer buff = new StringBuffer();
   
   buff.append(this.beginOffset);  buff.append("|");
   buff.append(this.length);       buff.append("|");
   buff.append(this.label);        buff.append("|");
   buff.append(this.subCategory);  buff.append("|");
   
   // ----------------------------
   // the red specifc things
   
   if ( this.snippetNumber > -1) {
     
	   buff.append("snippetNumber=\""); buff.append(this.snippetNumber); buff.append("\""); buff.append("<::>" );
	   buff.append("columnNumber=\"" ); buff.append(this.columnNumber ); buff.append("\""); buff.append("<::>" );
	   buff.append("columnName=\"");    buff.append(this.columnName   ); buff.append("\""); 
   }
   buff.append("|");
   buff.append(this.columnValue);
   buff.append("\n");
   
   return buff.toString();
   } // End Method toString() ===========================
   


  
  // ----------------------------
  // Global and Class fields
  
  
  // =======================================================
  /**
   * getBeginOffset 
   * 
   * @return the beginOffset
   *
   */
  // ======================================================
  
  public final int getBeginOffset() {
    return beginOffset;
  } // End Method getBeginOffset} ============}






  // =======================================================
  /**
   * setBeginOffset 
   * 
   * @param beginOffset the beginOffset to set
   */
  // ======================================================
  public final void setBeginOffset(int beginOffset) {
    this.beginOffset = beginOffset;
  } // End Method setBeginOffset} ============}






  // =======================================================
  /**
   * getLength 
   * 
   * @return the length
   *
   */
  // ======================================================
  
  public final int getLength() {
    return length;
  } // End Method getLength} ============}






  // =======================================================
  /**
   * setLength 
   * 
   * @param length the length to set
   */
  // ======================================================
  public final void setLength(int length) {
    this.length = length;
  } // End Method setLength} ============}






  // =======================================================
  /**
   * getLabel 
   * 
   * @return the label
   *
   */
  // ======================================================
  
  public final String getLabel() {
    return label;
  } // End Method getLabel} ============}






  // =======================================================
  /**
   * setLabel 
   * 
   * @param label the label to set
   */
  // ======================================================
  public final void setLabel(String label) {
    this.label = label;
  } // End Method setLabel} ============}






  // =======================================================
  /**
   * getCategory 
   * 
   * @return the category
   *
   */
  // ======================================================
  
  public final String getCategory() {
    return category;
  } // End Method getCategory} ============}






  // =======================================================
  /**
   * setCategory 
   * 
   * @param category the category to set
   */
  // ======================================================
  public final void setCategory(String category) {
    this.category = category;
  } // End Method setCategory} ============}






  // =======================================================
  /**
   * getSubCategory 
   * 
   * @return the subCategory
   *
   */
  // ======================================================
  
  public final String getSubCategory() {
    return subCategory;
  } // End Method getSubCategory} ============}






  // =======================================================
  /**
   * setSubCategory 
   * 
   * @param subCategory the subCategory to set
   */
  // ======================================================
  public final void setSubCategory(String subCategory) {
    this.subCategory = subCategory;
  } // End Method setSubCategory} ============}






  // =======================================================
  /**
   * getSnippetNumber 
   * 
   * @return the snippetNumber
   *
   */
  // ======================================================
  
  public final int getSnippetNumber() {
    return snippetNumber;
  } // End Method getSnippetNumber} ============}






  // =======================================================
  /**
   * setSnippetNumber 
   * 
   * @param snippetNumber the snippetNumber to set
   */
  // ======================================================
  public final void setSnippetNumber(int snippetNumber) {
    this.snippetNumber = snippetNumber;
  } // End Method setSnippetNumber} ============}






  // =======================================================
  /**
   * getColumnNumber 
   * 
   * @return the columnNumber
   *
   */
  // ======================================================
  
  public final int getColumnNumber() {
    return columnNumber;
  } // End Method getColumnNumber} ============}






  // =======================================================
  /**
   * setColumnNumber 
   * 
   * @param columnNumber the columnNumber to set
   */
  // ======================================================
  public final void setColumnNumber(int columnNumber) {
    this.columnNumber = columnNumber;
  } // End Method setColumnNumber} ============}






  // =======================================================
  /**
   * getColumnName 
   * 
   * @return the columnName
   *
   */
  // ======================================================
  
  public final String getColumnName() {
    return columnName;
  } // End Method getColumnName} ============}






  // =======================================================
  /**
   * setColumnName 
   * 
   * @param columnName the columnName to set
   */
  // ======================================================
  public final void setColumnName(String columnName) {
    this.columnName = columnName;
  } // End Method setColumnName} ============}






  // =======================================================
  /**
   * getColumnValue 
   * 
   * @return the columnValue
   *
   */
  // ======================================================
  
  public final String getColumnValue() {
    return columnValue;
  } // End Method getColumnValue} ============}






  // =======================================================
  /**
   * setColumnValue 
   * 
   * @param columnValue the columnValue to set
   */
  // ======================================================
  public final void setColumnValue(String columnValue) {
    this.columnValue = columnValue;
  } // End Method setColumnValue} ============}




  private int beginOffset = -1;
  private int length = 0;
  private String label = null;
  private String category = null;
  private String subCategory = "            ";
  private int snippetNumber = -1;
  private int columnNumber = -1;
  private  String columnName = null;
  private String  columnValue = null;
  
  

} // End Class RedSnippetMarkup ============
