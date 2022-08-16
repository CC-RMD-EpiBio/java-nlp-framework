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
 * TableStructure.java  Summary [TBD]
 *
 * @author     Guy Divita
 * @created    Dec 10, 2020
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.text;

import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * @author divitag2
 *
 */
public class TableStructure {

  // =================================================
  /**
   * Constructor
   *
   * @param tablesMetaData
   * 
  **/
  // =================================================
  public TableStructure(String[] pTablesMetaData) throws Exception {
   
    try {
    if ( pTablesMetaData != null ) {
      
      this.tablez = new String[ pTablesMetaData.length  ][][];
     
     for ( int i = 0; i < pTablesMetaData.length; i++ ) {
      String[] cols = U.split(pTablesMetaData[i]);
      int numberOfTableRows = Integer.parseInt(cols[1]);
      int numberOfTableColumns = Integer.parseInt( cols[2]);
     
      tablez[i] = new String[ numberOfTableRows + 1] [numberOfTableColumns + 1 ];
     
     
      for ( int k = 0; k <= numberOfTableRows; k++) {
        String[] tableCol = tablez[i][k];
        for ( int l = 0; l < numberOfTableColumns; l++) {
          tableCol[l] = new String();
        }
      }
      
     }
     }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  } // end Constructor() -----------------------------

  
  // =================================================
  /**
   * getCell
   *
   * @param pTableNumber
   * @param pRowNumber
   * @param pColumnNumber
   * @return String
   * 
  **/
  // =================================================
  public final String getCell( int pTableNumber, int pRowNumber, int pColumnNumber) throws Exception  {
    
    String returnVal = null;
    
    try {
    returnVal = this.tablez[pTableNumber] [pRowNumber] [pColumnNumber];
    
    } catch (Exception e ) {
      e.printStackTrace();
      System.err.println(" Something wrong there " + e.toString());
      System.err.println(" ");
      throw e;
    }
    return returnVal;
  } // end Method getCell() --------------------------
  
/**
   * @return the tablez
   */
  public final String[][][] getTablez() {
    return tablez;
  }



  /**
   * @param tablez the tablez to set
   */
  public final void setTablez(String[][][] tablez) {
    this.tablez = tablez;
  }





  // =================================================
  /**
   * setCell 
   * 
   * @param pTableNumber
   * @param pRowNumber
   * @param pColumnNumber
   * @param pContent
   * @throws Exception
  */
  // =================================================
   public final void setCell(int pTableNumber, int pRowNumber, int pColumnNumber, String pContent) throws Exception {
   
     try {
       this.tablez[ pTableNumber] [ pRowNumber] [pColumnNumber ] = pContent;
     } catch (Exception e) {
       e.printStackTrace();
       throw e;
     }
   
  } // End method setCell () ------------------------
   


  // =================================================
  /**
   * addToCell
   * 
   * @param pTableNumber
   * @param pRowNumber
   * @param pColumnNumber
   * @param pContent
  */
  // =================================================
  public final void addToCell(int pTableNumber, int pRowNumber, int pColumnNumber, String pContent) throws Exception {
   
    try {
    
    String cellContents = this.getCell(pTableNumber, pRowNumber, pColumnNumber);
    
    if ( cellContents == null ) 
      this.tablez[ pTableNumber] [ pRowNumber] [pColumnNumber ] = pContent;
    else 
      this.tablez [pTableNumber] [pRowNumber] [pColumnNumber]  = cellContents + CELL_NEWLINE + pContent;
    
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    
  } // end Method addToCell() ----------------------


  // -----------------------------
// Global Variables
// -----------------------------
  private String[][][] tablez = null;
  
  private String CELL_NEWLINE = " ";

  // =================================================
  /**
   * size 
   * 
   * @return int
  */
  // =================================================
  public final int size() {
    
    int returnVal = 0;
    if ( this.tablez != null)
       returnVal = this.tablez.length;
     
    return returnVal;
  } // End Method size() -----------------------------


  // =================================================
  /**
   * getNumberOfRowsForTable
   * 
   * @param pTableNum
   * @return int
  */
  // =================================================
  public final int getNumberOfRowsForTable(int pTableNum) throws Exception {
    int returnVal = 0;
    
    try {
    String[][] aTable = this.tablez[ pTableNum];
    
    returnVal = aTable.length;
    
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    return returnVal;
  } // end Method getNumberOfRowsForTable() --------


  // =================================================
  /**
   * getNumberOfColumnsForTableRow 
   * 
   * @param pTableNum
   * @param pTableRowNum
   * @return int
   * @throws Exception
  */
  // =================================================
  public final int getNumberOfColumnsForTableRow(int pTableNum, int pTableRowNum) throws Exception {
    
    int returnVal = 0;
    try {
    String[][] aTable = this.tablez[ pTableNum];
    String[] aTableRow = aTable[pTableRowNum];
    returnVal = aTableRow.length;
    
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    
    
    return returnVal;
  } // end Method getNumberOfColumnsForTableRow() -----

  
}
