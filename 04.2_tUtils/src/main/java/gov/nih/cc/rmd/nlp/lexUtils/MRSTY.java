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
 * MRSTY reads in and indexes the MRSTY table
 * This resource is indexed on cui and tui.
 *
 * @author     Guy Divita
 * @created    Jul 24, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.lexUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;
// import gov.nih.cc.rmd.nlp.framework.utils.umls.UMLSUtils;

/**
 * @author divitag2
 *
 */
public class MRSTY {

  // =================================================
  /**
   * Constructor
   * @throws Exception 
   *
   * 
  **/
  // =================================================
  public MRSTY() throws Exception {
    
    String args[] = setArgs( (String[] ) null );
    
    initialize( args );
   
  } // End Constructor () -----------------------------

  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws Exception 
   * 
  **/
  // =================================================
  public MRSTY(String[] pArgs) throws Exception {
    
    initialize( pArgs );
    
  } // End Constructor () -----------------------------
  
//=================================================
 /**
  * initialize
  *
  * @param pArgs
  * @throws Exception 
  * 
 **/
 // =================================================
 public final void initialize(  String[] pArgs ) throws Exception {
   
    String     inputDir = null;
    String    MRSTYFile = null;
    
    try {
      
        System.err.println("Initializing MRSTY");
        this.meter = new PerformanceMeter(System.err);
        this.meter.begin("Starting the MRSTY process");
           inputDir = U.getOption(pArgs,  "--inputDir=", "./2019AA/META");
        MRSTYFile = U.getOption( pArgs, "--MRSTY=", inputDir + "/MRSTY.rrf");
        this.totalNumberOfMRSTYRows = Integer.parseInt(U.getOption(pArgs,"--totalNumberOfMRSTYRows=", "10"));
        this.rowStore = new char[this.totalNumberOfMRSTYRows][];
        
        read( MRSTYFile );
        
        System.err.println("Finished initializing MRSTY " );
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue trying to instantiate MRSTY from " +  MRSTYFile + " " + e.toString());
      throw new Exception( e );
    }
    
  } // End Method initialize () ----------------------

  // =================================================
  /**
   * getRowsFromTui 
   * @param atui
   * @return List<char[] >  rows of mrsty that have this tui
  */
  // =================================================
 public final List<String> getRowsFromTui(String atui) {
    
    List<String> returnVal = null;
    List<int[]> MRSTYPtrs = this.tuiIndex.get( atui);
    
    if ( MRSTYPtrs != null ) {
        for ( int[] aPtr_ : MRSTYPtrs  ) {
          int aPtr = aPtr_[0];
          String aRow = new String( this.rowStore[aPtr] );
          if ( returnVal == null ) returnVal = new ArrayList<String>(1 );
          returnVal.add( aRow);
        }
    }
    
    return returnVal;
  } // end Method getRowsFromTui() ---------

 
 // =================================================
 /**
  * getCuisFromTui 
  * @param atui
  * @return List<String >  of cui's
 */
 // =================================================
public final List<String> getCuisFromTui(String atui) {
   
   List<String> returnVal = null;
   List<int[]> MRSTYPtrs = this.tuiIndex.get( atui);
  
   
   if ( MRSTYPtrs != null ) {
     for ( int[] aPtr_ : MRSTYPtrs ) {
      int aPtr = aPtr_[0];
       char[] aRow = this.rowStore[aPtr];
       if ( returnVal == null )  returnVal = new ArrayList<String>(1 );
       
       String cols[] = U.split( new String( aRow));
       returnVal.add( cols[0]);
     }
   }
   
   return returnVal;
 } // end Method getCuisFromTui() ---------
 
// =================================================
/**
 * getRowsFromCui retrieves the rows that are match
 * the passed in cui
 * 
 * @param aCui
 * @return List<char[]>
*/
// =================================================
 public final List<String> getRowsFromCui(String aCui) {
  
  List<String> returnVal = null;
  List<int[]> MRSTYPtrs = this.cuiIndex.get( aCui);
  
  if ( MRSTYPtrs != null && !MRSTYPtrs.isEmpty())
    for ( int[] ptr : MRSTYPtrs ) {
      int aPtr = ptr[0];
      String aRow = new String( this.rowStore[aPtr] );
      if ( returnVal == null ) 
        returnVal = new ArrayList<String>(MRSTYPtrs.size() );
      returnVal.add( aRow);
    }
  
  return returnVal;
} // end Method getRowsFromCui() ---------
 
//=================================================
/**
* getMRSTYRows returns MRSTY rows for these cuis
* @param pCCuis
* @return List<char[]> 
*/
//=================================================
public final List<String> getMRSTYRows(List<String> pCuis) {

List<String> returnVal = null;
if ( pCuis != null && !pCuis.isEmpty()) {
  
  returnVal = new ArrayList<String>( pCuis.size());
  for ( String aCui : pCuis ) {
    returnVal.addAll( getRowsFromCui(aCui));
  }
}
return returnVal;
} // end Method getMRSTYRows() -------------------

  // =================================================
  /**
   * read 
   * 
   * @param MRSTYFile
   * @throws Exception
  */
  // =================================================
  private final void read(String MRSTYFile) throws Exception {
    
    
    BufferedReader in = null;
    try {
      
      in = new BufferedReader(  new InputStreamReader( new FileInputStream(MRSTYFile), "UTF8"));
               
       String row = null;
       int ctr = 1;     
       while ((row = in.readLine()) != null) {
           
         if ( row != null && row.trim().length() > 0 && !row.startsWith("#")) {
           index( row);
           if ( ctr++ % 1000000 == 0 ) 
             this.meter.mark( " Processed " + ctr + " MRSTY records" );
           
         }
       } // end Loop through the input file
               
       in.close();
      
      
      
    } catch ( Exception e) {
      e.printStackTrace();
      System.err.println( "Issue reading the MRSTY file " + MRSTYFile + " " + e.toString() );
      throw e;
    }
    
  } // end Method read() -----------------------------

  // =================================================
  /**
   * index
   *  creates indexes for cui, string and tui
   * 
   * @param pRow
   * @throws Exception
  */
  // =================================================
 private final void index(String pRow) throws Exception  {
  
   
 
   try {
   String[] cols = U.split( pRow);
   
   String cui = cols[ FIELD_CUI ];
   String tui = cols[ FIELD_TUI ];
   String sty = cols[ FIELD_STY ];
 
  
   this.rowStore[this.MRSTYI] = pRow.toCharArray();  // reads MRSTY in 14 sec - 2218 mem
   
   indexCui ( cui, this.MRSTYI );                    // indexes cuis in 24 - 14 sec  - 2702 mem
   indexTui ( tui, this.MRSTYI );                    // indexes tuis in 55 - 24 sec  - 3355 mem
 
  
  
   //  0   1   2  3   4   5    6     7   8     9   10   11  12  13  14   15   16      17 
   // CUI,LAT,TS,LUI,STT,SUI,ISPREF,tui,Stui,SCUI,SDUI,SAB,TTY,CODE,STR,SRL,SUPPRESS,CVF
   
   
   
   this.MRSTYI++;
   
   } catch (Exception e) {
     e.printStackTrace();
     System.err.println("Issue indexing row : " + pRow + " " + e.toString());
     throw e;
   }
   
  

    
  } // end Method index() ----------------------------

  // =================================================
  /**
   * indexCui stores the row index for this cui
   * 
   * @param cui
   * @param MRSTYI2
  */
  // =================================================
 private void indexCui(String cui, int pRowId) {
    
   if ( cuiIndex == null )
     cuiIndex = new HashMap<String, List<int[]>>(this.totalNumberOfMRSTYRows *3 );
  
   
   List<int[]> rows = this.cuiIndex.get( cui );
   
   if ( rows == null) {
     rows = new ArrayList<int[]>(28);
     int aRow[] = new int[1];
     aRow[0] = pRowId;
     rows.add( aRow);
    
   } else {
  
   int aRow[] = new int[1];
   aRow[0] = pRowId;
   rows.add( aRow);
   }
  
   
   this.cuiIndex.put( cui,  rows);
   
  } // end Method indexCui() ------------------------

  // =================================================
  /**
   * indextui indexes the rows for this tui
   * 
   * @param tui
   * @param MRSTYI2
  */
  // =================================================
  private void indexTui(String tui, int pRowId) {
  
    if ( this.tuiIndex == null )
      this.tuiIndex = new HashMap<String,List<int[]>>(this.totalNumberOfMRSTYRows * 4);
   
   
    List<int[]> rows = this.tuiIndex.get( tui );
    
    if ( rows == null) {
      rows = new ArrayList<int[]>(10000);
      int aRow[] = new int[1];
      aRow[0] = pRowId;
      rows.add( aRow);
     
    } else {
   
    int aRow[] = new int[1];
    aRow[0] = pRowId;
    rows.add( aRow);
    }
    this.tuiIndex.put( tui,  rows );
   
    
   } // end Method indextui() ------------------------

  
  // =================================================
  /**
   * getRow retrieves the i'th row from the MRSTY table
   * 
   * @param pI
   * @return char[]
  */
  // =================================================
  public final char[] getRow(int pI) {
   
    char[] returnRow = null;
    
    if ( pI < this.totalNumberOfMRSTYRows )
      returnRow = this.rowStore[ pI ];
    
    return returnRow;
    
  } // end Method getRow() ------


  // =================================================
  /**
   * getCuisForTui retrieves the cui for an tui
   * 
   * @param pTui
   * @return char[]
  */
  // =================================================
   public String[]  getCuisForTui(String pTui) {
     
     String[] returnVals= null;
     HashSet<String> cuiHash = new HashSet<String>();
   
     List<String> rows = getRowsFromTui(pTui) ;
     
     if ( rows != null && !rows.isEmpty() ) {
       for ( String row: rows ) {
         String[] cols = U.split(row);
         String cui = cols[0];
         cuiHash.add( cui);
       }
       
       if ( !cuiHash.isEmpty() ) {
        returnVals =  (String[]) cuiHash.toArray( );
       }
     }
     
     return returnVals;
   
  } // end Method getCuiForTui() ----------------------

  // =================================================
  /**
   * getTuisFromCui
   * 
   * @param aCui
   * @return List<String>   the list of tuis
   * @throws Exception 
  */
  // =================================================
   public final List<String> getTuisFromCui(String aCui) throws Exception {
     
     List<String> returnVal = null;
   
     try {
     List<String> rows = this.getRowsFromCui(aCui);
     
     if ( rows != null && !rows.isEmpty()) {
       returnVal = new ArrayList<String>();
       for ( String row : rows ) {
         String cols[] = U.split( row);
         returnVal.add(cols[MRSTY.FIELD_TUI].trim() );
         
       }
     }
     } catch (Exception e) {
       e.printStackTrace();
       System.err.println( "Issue trying to get the tui for this cui " + aCui + " " + e.toString());
       throw e;
     }
    return returnVal;
  } // end Method gettuisForCui() --------------------

  

  // =================================================
  /**
   * getTemplateRow returns a template row 
   * 
   * CUI,TUI,STN,STY,ATUI,CVF|6|4183223|231188411|
   * C0000005|T116|A1.4.1.2.1.7|Amino Acid, Peptide, or Protein|AT17648347|256|
   *
   * @return String
  */
  // =================================================
  public static String getTemplateRowForCui( String pCui)  {
    
    StringBuffer buff = new StringBuffer();
    
    buff.append( pCui);             buff.append("|");
    buff.append("T00");             buff.append("|");
    buff.append("A0.0.0.0.0");      buff.append("|");
    buff.append("Top");             buff.append("|");
    buff.append("A00000");          buff.append("|");
    buff.append("256");      
    
    String returnVal = buff.toString();
    
    
    
    
    return returnVal;
  } // end Method getTemplateRowForCui() -------------

  // =================================================
  /**
   * main 
   * 
   * @param pArgs
  */
  // =================================================
  public static void main(String[] pArgs) {
  
    try {
      
      String[] args = setArgs(pArgs );
      // Read in arguments
      
      String     inputDir = U.getOption(pArgs,  "--inputDir=", "./2019AA/META");
      String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_terminology_" );
      String  MRSTYFile = U.getOption( pArgs, "--MRSTY=", inputDir + "/MRSTY.rrf");
      String totalNumberOfMRSTYRows = U.getOption(pArgs,"--totalNumberOfMRSTYRows=", "10");
      
      PerformanceMeter meter = new PerformanceMeter();
    
      
      // read in MRSTY
      meter.begin( " Reading in MRSTY " );
      MRSTY MRSTY = new MRSTY( args );
      
     // int[] cuiBins = MRSTY.getCuiBins();
    
      
      
      // +-------------------
      String aCui = "C0011351";
      String aTui = "T001";
     
      List<String> rows = null;
      int ctr = 0;
      
     
       rows = MRSTY.getRowsFromCui( aCui );
      
     // int ctr =0;
      if ( rows != null && !rows.isEmpty() )
        for ( String row : rows ) 
          System.err.println( ctr++ + "|" + new String( row) );
      else 
        System.err.println( ctr++ + "| no records found" );
      
      
      rows = MRSTY.getRowsFromTui( aTui );
      
      ctr =0;
      if ( rows != null && !rows.isEmpty() )
        for ( String row : rows ) 
          System.err.println( ctr++ + "|" + new String( row) );
      else 
        System.err.println( ctr++ + "| no records found" );
      
      
      List<String> tuis = MRSTY.getTuisFromCui(aCui);
      
      if ( tuis != null )
        for ( String tui : tuis )
          System.err.println("Cui:" + aCui + ":" + tui);
      
     // String semanticType = UMLSUtils.getSemanticTypeAbbrFromTui(aTui);
     // String semanticAbbr = UMLSUtils.getSemanticTypeAbbrFromTui(aTui);
     // System.err.println(aTui + ":" + semanticAbbr + ":" + semanticType );
      
      
      meter.stop();
      
      System.err.println("Dohn");
      
    } catch ( Exception e ) {
      e.printStackTrace();
      System.err.println("Issue with creating MRSTYsty from mrhier " + e.toString());
    }


  } // end Method main() ----------------------------

  
//------------------------------------------
 /**
  * setArgs
  * 
  * 
  * @return
  */
 // ------------------------------------------
 public static String[] setArgs(String pArgs[]) {

   // -------------------------------------
   // dateStamp
   String dateStamp = U.getDateStampSimple();

   // -------------------------------------
   // Input and Output

   String     inputDir = U.getOption(pArgs,  "--inputDir=", "./2019AA/META");
   String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_terminology_" + dateStamp);
   String      MRSTY = U.getOption( pArgs, "--MRSTY=", inputDir + "/MRSTY.RRF");
   String totalNumberOfMRSTYRows = U.getOption(pArgs,"--totalNumberOfMRSTYRows=", "5545407");
   
  
   String args[] = {
       
       "--inputDir=" + inputDir,
       "--outputDir=" + outputDir,
       "--MRSTY="    + MRSTY,
       "--totalNumberOfMRSTYRows=" + totalNumberOfMRSTYRows
   };

   
      
   if ( Use.usageAndExitIfHelp("MRSTY",   pArgs, args  ))
     System.exit(0);
    

   return args;

 }  // End Method setArgs() -----------------------

  // ------------------------------------------------
  // Class Variables
  // ------------------------------------------------
  char[][] rowStore = new char[100][];
  int totalNumberOfMRSTYRows = 5545407;
  int MRSTYI = 0;
  PerformanceMeter meter = null;
  private StringBuffer buff = new StringBuffer();
  
  HashMap<String, List<int[]>> cuiIndex = null;
  HashMap<String, List<int[]>> tuiIndex = null;
  HashMap<String, List<int[]>> strIndex = null;
  private int maxtuiList  = 0;
  private int maxCuiList = 0;
  int cuiBins[] = new int[400];
  
  public static final int FIELD_CUI    =  0;
  public static final int FIELD_TUI    =  1;
  public static final int FIELD_STN    =  2;  // Semantic Tree number
  public static final int FIELD_STY    =  3;  // Semantic type  (is redundant with TUI)
  public static final int FIELD_ATUI   =  4;  // unique identifier for attribute?
  public static final int FIELD_CSV    =  5;
  
}
