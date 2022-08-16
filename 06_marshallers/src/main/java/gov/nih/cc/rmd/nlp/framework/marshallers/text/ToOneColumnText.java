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
//=================================================
/**
 * This writer put's out text that is tabular into
 * one column format - where each cell is a separate
 * line.
 *
 * @author  Guy Divita 
 * @created June 26, 2012
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.text;


import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.TableCell;
import gov.va.chir.model.TablesMetaData;



public class ToOneColumnText extends AbstractWriter {





  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   * 
  **/
  // =================================================
  public ToOneColumnText(String[] pArgs) throws ResourceInitializationException {
    initialize( pArgs);
  } // end Constructor() ----------------------





    // -----------------------------------------
    /** 
     * process iterates through all annotations, filters out
     * those that should be filtered out, then pushes them
     * into a database store.
     * 
     * Each document should include a DocumentAnnotation annotation and a documentHeader annotation
     * for re-animation purposes.
     *
     * @param pAJCas
     * @throws AnalysisEngineProcessException
     */
    // -----------------------------------------
    @Override
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
      
	 
    File outputFile = null;
      
     try {
      
      String returnVal = null;
      String inputFileName = VUIMAUtil.getDocumentId(pJCas);
      inputFileName = U.getFileNamePrefix(inputFileName);
      String outputFileName = inputFileName + ".txt";
      outputFile = new File ( this.outputDir + "/" + outputFileName);
      PrintWriter out = null;
     
      
      String docText = pJCas.getDocumentText();
      returnVal = docText;
     
      // -----------------------------------
      // Re-pretty print the tables in a single column format
      
       returnVal = prettyPrintTables( pJCas);
      
    
      // ----------------------------------
      // print out the text followed by pipe delimited annotations, index style
      // ----------------------------------
     
     
     
      out = new PrintWriter (outputFile.getAbsolutePath() );
      out.print( returnVal );
      out.close();
      
    
    } catch (Exception e) {
        e.getStackTrace();
        String msg = "Issue writing out file " + outputFile.getAbsolutePath() + "\n" + e.getMessage() + "\n" ;
        System.err.println(msg);
      }
      
        
      System.err.println("Finished processing file" );
    }  // end Method process


    // =================================================
    /**
     * prettyPrintTables retrieves the table cells, puts
     * them into a table format, then creates text representation
     * of them.
     * 
     * @param pJCas
     * @return String
     * @throws Exception 
    */
    // =================================================
     private String prettyPrintTables(JCas pJCas) throws Exception {
      
       String returnVal = null;
       StringBuffer refactoredDocText = new StringBuffer();
      // -----------------------------------
      // get the table meta data (for all tables in the file)
      String[] tablesMetaData = getTablesMetaData( pJCas);
      
      
      if ( tablesMetaData != null && tablesMetaData.length > 0 ) {
      // -----------------------------------
      // create a data structure to house the cells of each table
      //  table Number|  table row | table column
      TableStructure tablez = new TableStructure (tablesMetaData);
      
      
      // ------------------------------------
      // populate the table structure with the table cell annotations
      getTableCellContent( pJCas, tablez );
     
      // ------------------------------------
      // Loop through the tables and convert the tables into strings
          
          
      try {
          for (int tableNum = 0; tableNum < tablez.size(); tableNum++ )
            for ( int tableRowNum = 0; tableRowNum < tablez.getNumberOfRowsForTable(tableNum); tableRowNum++ ) 
              for ( int tableColumnNum = 0; tableColumnNum < tablez.getNumberOfColumnsForTableRow( tableNum, tableRowNum ); tableColumnNum++) {
                
                String buff = tablez.getCell( tableNum, tableRowNum, tableColumnNum ) ;
                
                if ( buff != null )
                  refactoredDocText.append( buff + "\n" );
              }
                
      } catch (Exception e2) {
        e2.printStackTrace();
        throw e2;
      }
          
        }
        
        if ( refactoredDocText != null && refactoredDocText.toString().trim().length() > 0  )
          returnVal = refactoredDocText.toString();
        
        
      return returnVal;
    }





    // =================================================
    /**
     * getTableCellContent retrieves and populates the 
     * tablez structure with the contents of each table cell
     * 
     * @param pJCas
     * @param pTablez
     * @return String[][][]   tableNum|tableRow|tableCol < content
     * @throws Exception 
    */
    // =================================================
   private final  void getTableCellContent(JCas pJCas, TableStructure pTablez) throws Exception {
   
     try {
        List<Annotation> tableCells = UIMAUtil.getAnnotations( pJCas, TableCell.typeIndexID );
        
        if ( tableCells != null && !tableCells.isEmpty()) {
       
          for ( Annotation tableCell : tableCells ) {
           
           int tableNumber  = ((TableCell) tableCell).getTableNumber();
           int rowNumber    = ((TableCell) tableCell).getRowNumber();
           int columnNumber = ((TableCell) tableCell).getColumnNumber();
           String cellContents = tableCell.getCoveredText().trim();  // <--------------------------   trimming this!
           pTablez.addToCell(tableNumber, rowNumber, columnNumber, cellContents ) ;
       
          } // end loop through the table cell annotations
         
        } // end if there are any table Cells
        
        } catch ( Exception e3 ) {
          e3.printStackTrace();
          GLog.error_println("Issue with retrieving the table cells from the annotations " + e3.toString());
          throw e3;
        
        }
     
    } // end Method getTableCellContent() --------------






	// =================================================
    /**
     * getTablesMetaData returns info about each table
     * in the file.  The number of rows returned equate
     * to the number of tables in the file.
     * Each row will include the table number, the the char offset
     * the table starts at,  the number of rows, the number of columns
     * 
     *   
     *   The annotation will have this info in it
     *
     *       tableNumber | tableRows | tableColumns |  tableOffset | tableName\n
   *  
     * 
     * @param pJCas
     * @return String[]   tableNumber|charOffsetStart|numberOfRows|NumberOfColumns
    */
    // =================================================
     private String[] getTablesMetaData(JCas pJCas) {
     
       
       String returnVal[] = null;
       List< Annotation> tablesMetadataAnnotationz = UIMAUtil.getAnnotations( pJCas,  TablesMetaData.typeIndexID);
       
       if ( tablesMetadataAnnotationz != null && !tablesMetadataAnnotationz.isEmpty()) {
         Annotation tablesMetadataAnnotation = tablesMetadataAnnotationz.get(0);
         
         String buff = ((TablesMetaData)tablesMetadataAnnotation).getTablesMetaData();
         
          returnVal = U.split(buff, "\n");
         
       }
         
      return returnVal;
    } // end Method getTablesMetaData() ----------------





  //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
      
      String pOutputDir = null;
    
      
      initialize( pOutputDir  );
    
    
      
    } // end Method initialize() --------------


  //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param pArgs
     * 
     **/
    // ----------------------------------
    public void initialize( String[] pArgs ) throws ResourceInitializationException {
      
      String      outputDir = U.getOption(pArgs, "--outputDir=", "./someDir/");
      
      this.segmentType = U.getOption( pArgs,  "--segmentType=", "Segment");
      
      
      initialize( outputDir  );
    
    
      
    } // end Method initialize() --------------


    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(String pOutputDir ) throws ResourceInitializationException {
      
     
      this.outputDir = pOutputDir + "/pipedText";
 
      try {
        U.mkDir( this.outputDir );
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue creating the output dir " + this.outputDir + " " + e.toString());
        throw new ResourceInitializationException();
      }
      
      
    } // end Method initialize() --------------


    


    // -----------------------------------------
    /** 
     * destroy closes the database
     *
     */
    // -----------------------------------------
    @Override
    public void destroy() {
    
    } // end Method destroy() 


    // ----------------------------------------
    // Global variables
    // ----------------------------------------
  
    private String segmentType = "Sentence";
    private String outputDir;
    private char SEGMENT_DELIMITER = '|';
    
   // Table Metadata  tableNumber|charOffsetStart|numberOfRows|NumberOfColumns
    private final int TABLE_METADATA_TABLE_NUMBER = 0;
    private final int TABLE_METADATA_TABLE_CHAR_OFFSET_START = 1;
    private final int TABLE_METADATA_TABLE_NUMBER_OF_ROWS = 2;
    private final int TABLE_METADATA_TABLE_NUMBER_OF_COLUMNS = 3;

  
  

} // end Class toCommonModel
