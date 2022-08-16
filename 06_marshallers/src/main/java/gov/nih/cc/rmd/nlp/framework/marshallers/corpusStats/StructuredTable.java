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
 * StructuredTable is a container that holds the contents
 * of a n x m  table that could include a title, row, column 
 * headings, and values for each cell.   
 * 
 * 
 * @author  guy
 * @created Oct 2, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.corpusStats;

import gov.nih.cc.rmd.nlp.framework.utils.U;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author guy
 *
 */
public class StructuredTable {

  // end Class StructuredTable() -------------------------
  
  // =======================================================
  /**
   * Constructor StructuredTable 
   *
   */
  // =======================================================
  public StructuredTable(String pTableName, String pOutputDir, String pDelimiter) {
   initialize(pTableName, pOutputDir, pDelimiter);
  
  
  }


  // =======================================================
  /**
   * process absorbs the contents of this file
   * 
   * @param table
   * @param pFile
   * @throws Exception 
   */
  // =======================================================
  public void process(File pFile) throws Exception {
  
    try {
    // Read the contents of the file into an array
    
      String buff = U.readFile( pFile);
      
    
      String rows[] = buff.split("\n");
    
      int[] firstDataCell = new int[5];
      // -------------------------
      // Find the Title if any
    
      if ( this.title == null )
        this.title = getTitle( rows, this.delimiter, firstDataCell);
      
      // -------------------------
      // Find the column headings if any. 
      // Put the row number with the column headings in firstDataCell[
      //                                                              FIRST_ROW_OF_COLUMN_HEADINGS,
      //                                                              NUMBER_OF_COLUMNS, 
      //                                                              NUMBER_OF_ROWS,
     //                                                               COLUMN_WITH_ROW_HEADINGS,
     //                                                              ] 
      // Put the number of columns                      firstDataCell[ _, here, ]
    
      String[] columnHeadings = getColumnHeadings( rows , this.delimiter, firstDataCell);
      combineColumnHeadings( columnHeadings);
      
      
      // -------------------------
      // Find the row headings if any 
      // Put column number with the row headings in   firstDataCell[_, _, Here,] 
      // put the number of row headings               firstDataCell[_,_,_,Here] 
      String[] rowHeadings = getRowHeadings( rows, this.delimiter,  firstDataCell );
      combineRowHeadings( rowHeadings);
     
    
      int firstRow = firstDataCell[FIRST_ROW_OF_COLUMN_HEADINGS] +1;
      int lastRow =  firstRow + firstDataCell[NUMBER_OF_ROWS];
      int firstCol = firstDataCell[COLUMN_WITH_ROW_HEADINGS] + 1;
      int lastCol =  firstDataCell[NUMBER_OF_COLUMNS];
      
      
      for ( int currentRow = firstRow; currentRow < rows.length; currentRow++ ) {
        
        String currentRowValue = rows[currentRow];
        String currentColValues[] = currentRowValue.split(this.delimiter);
      
        for ( int currentCol = firstCol; currentCol < currentColValues.length; currentCol++ ) {
          
          String currentCellValue = currentColValues[currentCol].trim();
          String key = rowHeadings[currentRow] + "|" + columnHeadings[currentCol ];
          
          double[] cellValue = this.rowColMatrix.get(key);
          
          if ( cellValue == null ) {
            cellValue = new double[1];
            cellValue[0] = 0.0;
            
           
          } 
          if ( currentCellValue != null && currentCellValue.trim().length() > 0 && U.isNumber(currentCellValue))
            cellValue[0] = cellValue[0] + Integer.parseInt(currentCellValue);
         
          this.rowColMatrix.put(key, cellValue);
         
          
        } // end loop through columns of a row
      } // end loop thru the rows of a table
      
 
      pFile.delete();
    
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue combining a n x m matrix " + e.toString();
      System.err.println(msg);
      throw new Exception();

    }
  }  // end Method process() ---------------------------------

  
   

  // =======================================================
  /**
   * printTable
   * @throws Exception 
   * 
   */
  // =======================================================
  public void printTable() throws Exception {
  
    
    PrintWriter out = null;
    try {
      
      out = new PrintWriter( this.outputDir + "/stats/" + this.tableName + "_Combined.csv");

      out.print( title );
     
      // ----------------------------
      // Write out the column headings
     
      for ( int i = 0; i < this._columnHeadings.size(); i++ ) {
        out.print( this._columnHeadings.get(i));
        out.print( this.delimiter);
      }
      out.print("\n");
      
      // ---------------------------
      // Write out the row heading and rows
      
      String rowHeading = null;
      String totalRowHeading = null;
      for (int j = 2; j < this._rowHeadings.size(); j++) {

        rowHeading = this._rowHeadings.get(j);
        if (rowHeading.toLowerCase().contains("total")) {
          totalRowHeading = rowHeading;
        
        } else {
          out.print(rowHeading);
          out.print(this.delimiter);

          // ------------------------------
          // write out the cells
          printTableAux( out, rowHeading);
         
        }
      } // end loop thru rows
      if ( totalRowHeading != null ) {
        out.print(totalRowHeading);
        out.print(this.delimiter);
        printTableAux( out, totalRowHeading);
      }
      // ---------------------------------
      // Print out the summary row

      out.close();
      
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue printing the final table for " + this.tableName + " " + e.toString();
      System.err.println(msg);
      throw new Exception ( msg);
    }
    
    
  } // End Method printTable() ======================
  


  // =======================================================
  /**
   * printTableAux [Summary here]
   * 
   * @param out
   * @param rowHeading
   */
  // =======================================================
  private void printTableAux(PrintWriter out, String rowHeading) {
 
   for (int i = 1; i < this._columnHeadings.size(); i++) {

       printTableAux2(out, rowHeading, this._columnHeadings.get(i) );
     
   }
     out.print("\n");
  }  // End Method printTableAux() ======================
  


  // =======================================================
  /**
   * printTableAux [Summary here]
   * 
   * @param rowHeading
   * @param string
   */
  // =======================================================
  private void printTableAux2(PrintWriter out, String rowHeading, String columnHeading) {
  
   String key = rowHeading + "|" + columnHeading;
   double value[] = this.rowColMatrix.get(key);
   if (value == null) {
     value = new double[1];
     value[0] = 0.0;
   }
 
   out.print(value[0] + this.delimiter);

  }   // End Method printTableAux() ======================
  


  // =======================================================
  /**
   * combineColumnHeadings adds any new column headings to
   * a set of column headings
   * 
   * @param pColumnHeadings
   */
  // =======================================================
  private void combineColumnHeadings(String[] pColumnHeadings) {
   
    for ( String columnHeading : pColumnHeadings ) {
      if ( ! this.columnHeadingSet.contains( columnHeading ) ) {
        this.columnHeadingSet.add( columnHeading);
        if ( this._columnHeadings == null ) this._columnHeadings = new ArrayList<String>();
        this._columnHeadings.add( columnHeading);
      }
    }
  } // End Method combineColumnHeadings() ======================
  

  // =======================================================
 /**
  * combineRowHeadings adds any new row headings to
  * a set of row headings
  * 
  * @param pColumnHeadings
  */
 // =======================================================
 private void combineRowHeadings(String[] pRowHeadings) {
   for ( String rowHeading : pRowHeadings ) { 
     if ( !this.rowHeadingSet.contains(rowHeading)) {
       if ( this._rowHeadings == null ) this._rowHeadings = new ArrayList<String>();
       this._rowHeadings.add(rowHeading);
       this.rowHeadingSet.add( rowHeading);
     }
   }
 } // End Method combineColumnHeadings() ======================
 
  
  // =======================================================
  /**
   * getTitle returns all the rows that are title rows;
   * 
   * @param rows
   * @return
   */
  // =======================================================
  private String getTitle(String[] pRows, String pDelimiter, int[] firstDataCell) {
  
    StringBuffer aTitle = new StringBuffer();
    
    for (int i = 0; i < pRows.length; i++) {
     
      if ( !pRows[i].contains( pDelimiter) ) {
        aTitle.append(pRows[i] + "\n");
        firstDataCell[LAST_ROW_OF_TITLE] = i;
      } else {
        break;
      }
    }
    
    return aTitle.toString();
  } // End Method getTitle() ======================
  

  // =======================================================
  /**
   * getRowHeadings reads thru the rows of the file, picking 
   * up the first column and using this as the row headings
   * 
   * This method assumes that the firstDataCell[ LAST_ROW_OF_TITLE ] has been set
   * @param rows
   * @param firstDataCell  also sets the column number that the row headings are in (assumed to be 1)
   * @return String[]
   */
  // =======================================================
  private String[] getRowHeadings(String[] pRows, String pDelimiter, int[] firstDataCell) {
  
    ArrayList<String> rowHeadings = new ArrayList<String>();
    String returnVal[] = null;
    for ( int i =  firstDataCell[LAST_ROW_OF_TITLE ]; i < pRows.length; i++  ) {
      
      String[] cols = pRows[i].split( pDelimiter );
      
      if ( cols[0] != null  )
        rowHeadings.add( cols[0].trim());
    }
    firstDataCell[NUMBER_OF_ROWS] = rowHeadings.size();
    returnVal = rowHeadings.toArray( new String[rowHeadings.size()]);
    
    return returnVal;
  } // End Method getRowHeadings() ======================
  

  // =======================================================
  /**
   * getColumnHeadings [Summary here]
   * 
   * @param pRows
   * @param pFirstDataCell
   * @return
   */
  // =======================================================
  private String[] getColumnHeadings(String[] pRows, String pDelimiter,  int[] pFirstDataCell) {
   
    
    ArrayList<String> colHeadings = new ArrayList<String>();
    String returnVal[] = null;
    int firstSeenColumn = -1;
    int firstSeenColumnHeadingRow = -1;
    for ( int i =  pFirstDataCell[LAST_ROW_OF_TITLE ]; i < pRows.length; i++  ) {
      
      if ( pRows[i].contains(pDelimiter ) ) {
        String[] cols = pRows[i].split( pDelimiter );
       
        if ( firstSeenColumnHeadingRow == -1)
          firstSeenColumnHeadingRow = i;
        
        // Take out any blank column names
        for ( int k = 0; k < cols.length; k++ )
          if ( cols[k] != null && cols[k].trim().length() > 0 ) {
            if (firstSeenColumn == -1)
              firstSeenColumn = k ;
            colHeadings.add(cols[k].trim());
          }
        
        
        break;
      }
    
     
    }
 
    pFirstDataCell[FIRST_ROW_OF_COLUMN_HEADINGS] = firstSeenColumnHeadingRow;
    pFirstDataCell[NUMBER_OF_COLUMNS] = colHeadings.size();
    pFirstDataCell[COLUMN_WITH_ROW_HEADINGS] = firstSeenColumn;
    returnVal = colHeadings.toArray( new String[colHeadings.size()]);
    
    return returnVal;
    // End Method getColumnHeadings() ======================
  }

  // =======================================================
  /**
   * initialize 
   * 
   */
  // =======================================================
  private void initialize(String pTableName, String pOutputDir, String pDelimiter) {
    this.tableName = pTableName;
    this.outputDir = pOutputDir;
    this.delimiter = pDelimiter;
    this.rowColMatrix = new HashMap<String, double[]>();
    this.columnHeadingSet = new HashSet<String>();
    this.rowHeadingSet = new HashSet<String>();
    // End Method initialize() ======================
  }

    
  
private HashMap<String, double[]> rowColMatrix;
private String  title = null;
private HashSet<String> columnHeadingSet = null;
private List<String> _columnHeadings = null;
private List<String> _rowHeadings = null;
private HashSet<String> rowHeadingSet = null ;
private String tableName = null;
private String outputDir = null;
private String delimiter = ",";
private static final int FIRST_ROW_OF_COLUMN_HEADINGS = 0;
private static final int NUMBER_OF_COLUMNS = 1;
private static final int COLUMN_WITH_ROW_HEADINGS = 2;
private static final int NUMBER_OF_ROWS = 3;
private static final int LAST_ROW_OF_TITLE = 4;




}
