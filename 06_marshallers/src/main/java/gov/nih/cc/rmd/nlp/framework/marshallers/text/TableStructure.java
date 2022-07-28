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
