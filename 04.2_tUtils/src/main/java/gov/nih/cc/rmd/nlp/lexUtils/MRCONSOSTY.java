/*******************************************************************************
 * ---------------------------------------------------------------------------
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                        Epidemiology and Biostatistics Branch 
 *                                         2019
 *    
 *  This work is licensed under the Creative Commons Attribution 4.0 International License. 
 *  
 *  This license allows you to use, share and  adapt for any purpose, provided:
 *     Provide attribution to the creators of this work within your work.
 *     Indicate if changes were made to this work.
 *     No claim to merchantability, implied warranty, or liability can be made.
 *     
 *   When attributing this code, please make reference to
 *    [citation/url here] .  
 *    
 *     In the absence of a specific paper or url listed above, reference http://clinicalcenter.nih.gov/rmd/eb/nlp
 *  
 *  To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 ******************************************************************************/
// =================================================
/**
 * MRCONSOSTYreads in and indexes the mrconsosty table
 * This resource is indexed on cui and aui.
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

/**
 * @author divitag2
 *
 */
public class MRCONSOSTY  {

  // =================================================
  /**
   * Constructor
   * @throws Exception 
   *
   * 
  **/
  // =================================================
  public MRCONSOSTY() throws Exception {
    
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
  public MRCONSOSTY(String[] pArgs) throws Exception {
    
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
    String  mrconsostyFile = null;
    
    try {
      
        System.err.println("Initializing MRCONSOSTY");
        this.meter = new PerformanceMeter(System.err);
        this.meter.begin("Starting the mrconsosty process");
           inputDir = U.getOption(pArgs,  "--inputDir=", "./2019AA/META");
        mrconsostyFile = U.getOption( pArgs, "--mrconsosty=", inputDir + "/mrconsosty.rrf");
        this.totalNumberOfMrconsostyRows = Integer.parseInt(U.getOption(pArgs,"--totalNumberOfMrconsostyRows=", "10"));
        this.rowStore = new char[this.totalNumberOfMrconsostyRows][];
        
        read( mrconsostyFile );
        
        System.err.println("Finished initializing MRCONSOSTY" );
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue trying to instantiate mrconsosty from " +  mrconsostyFile + " " + e.toString());
      throw new Exception( e );
    }
    
  } // End Method initialize () ----------------------

  // =================================================
  /**
   * getRowsFromAui 
   * @param aAui
   * @return List<char[] >
  */
  // =================================================
 public final List<String> getRowsFromAui(String aAui) {
    
    List<String> returnVal = null;
    int[] mrconsoPtrs = this.auiIndex.get( aAui);
    
    if ( mrconsoPtrs != null ) {
       int aPtr = mrconsoPtrs[0];
        String aRow = new String( this.rowStore[aPtr] );
        if ( returnVal == null ) returnVal = new ArrayList<String>(1 );
        returnVal.add( aRow);
      }
    
    return returnVal;
  } // end Method getRowsFromAui() ---------

 
 // =================================================
 /**
  * getCuisFromAui 
  * @param aAui
  * @return List<String >  of cui's
 */
 // =================================================
public final List<String> getCuisFromAui(String aAui) {
   
   List<String> returnVal = null;
   int[] mrconsoPtrs = this.auiIndex.get( aAui);
  
   
   if ( mrconsoPtrs != null ) {
      int aPtr = mrconsoPtrs[0];
       char[] aRow = this.rowStore[aPtr];
       if ( returnVal == null )  returnVal = new ArrayList<String>(1 );
       
       String cols[] = U.split( new String( aRow));
       returnVal.add( cols[0]);
     }
   
   return returnVal;
 } // end Method getCuisFromAui() ---------
 
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
  List<int[]> mrconsoPtrs = this.cuiIndex.get( aCui);
  
  if ( mrconsoPtrs != null && !mrconsoPtrs.isEmpty())
    for ( int[] ptr : mrconsoPtrs ) {
      int aPtr = ptr[0];
      String aRow = new String( this.rowStore[aPtr] );
      if ( returnVal == null ) returnVal = new ArrayList<String>(mrconsoPtrs.size() );
      returnVal.add( aRow);
    }
  
  return returnVal;
} // end Method getRowsFromCui() ---------
 
//=================================================
/**
* getMRCONSOSTYRows returns mrconsosty rows for these cuis
* @param pCCuis
* @return List<char[]> 
*/
//=================================================
public final List<String> getMRCONSOSTYRows(List<String> pCuis) {

List<String> returnVal = null;
if ( pCuis != null && !pCuis.isEmpty()) {
  
  returnVal = new ArrayList<String>( pCuis.size());
  for ( String aCui : pCuis ) {
    returnVal.addAll( getRowsFromCui(aCui));
  }
}
return returnVal;
} // end Method getMRCONSOSTYRows() -------------------

  // =================================================
  /**
   * read 
   * 
   * @param mrconsostyFile
   * @throws Exception
  */
  // =================================================
  private final void read(String mrconsostyFile) throws Exception {
    
    
    BufferedReader in = null;
    try {
      
      in = new BufferedReader(  new InputStreamReader( new FileInputStream(mrconsostyFile), "UTF8"));
               
       String row = null;
       int ctr = 1;     
       while ((row = in.readLine()) != null) {
           
         if ( row != null && row.trim().length() > 0 && !row.startsWith("#")) {
           index( row);
           if ( ctr++ % 1000000 == 0 ) 
             this.meter.mark( " Processed " + ctr + " mrconsostysty records" );
           
         }
       } // end Loop through the input file
               
       in.close();
      
      
      
    } catch ( Exception e) {
      e.printStackTrace();
      System.err.println( "Issue reading the mrconsosty file " + mrconsostyFile + " " + e.toString() );
      throw e;
    }
    
  } // end Method read() -----------------------------

  // =================================================
  /**
   * index
   *  creates indexes for cui, string and aui
   * 
   * @param pRow
   * @throws Exception
  */
  // =================================================
 private final void index(String pRow) throws Exception  {
  
   
 
   try {
   String[] cols = U.split( pRow);
   
   String cui = cols[ FIELD_CUI ];
   String aui = cols[ FIELD_AUI ];
   String str = cols[ FIELD_STR ];
 
  
   this.rowStore[this.mrconsostyI] = pRow.toCharArray();  // reads mrconsostysty in 14 sec - 2218 mem
   
   indexCui ( cui, this.mrconsostyI );                    // indexes cuis in 24 - 14 sec  - 2702 mem
   indexAui ( aui, this.mrconsostyI );                    // indexes auis in 55 - 24 sec  - 3355 mem
 // indexStr ( str, this.mrconsostyI );
  
  
   //  0   1   2  3   4   5    6     7   8     9   10   11  12  13  14   15   16      17 
   // CUI,LAT,TS,LUI,STT,SUI,ISPREF,AUI,SAUI,SCUI,SDUI,SAB,TTY,CODE,STR,SRL,SUPPRESS,CVF
   
   
   
   this.mrconsostyI++;
   
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
   * @param mrconsostyI2
  */
  // =================================================
 private void indexCui(String cui, int pRowId) {
    
   if ( cuiIndex == null )
     cuiIndex = new HashMap<String, List<int[]>>(this.totalNumberOfMrconsostyRows *3 );
  
   
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
  
   if ( rows.size() < 399 )
     this.cuiBins[ rows.size()]++;
   else
    this.cuiBins[ 399]++;
   
   this.cuiIndex.put( cui,  rows);
   
  } // end Method indexCui() ------------------------

  // =================================================
  /**
   * indexAui indexes the rows for this Aui
   * 
   * @param aui
   * @param mrconsostyI2
  */
  // =================================================
  private void indexAui(String aui, int pRowId) {
  
    if ( auiIndex == null )
      auiIndex = new HashMap<String,int[]>(this.totalNumberOfMrconsostyRows * 4);
   
    
    int[] rows = this.auiIndex.get( aui );
    
    if ( rows == null) {
     rows = new int[1];
    }
    rows[0]= pRowId;
    this.auiIndex.put( aui,  rows );
   
    
   } // end Method indexAui() ------------------------

  // =================================================
  /**
   * indexStr indexes the rows for this String 
   * 
   * @param str
   * @param mrconsostyI2
  */
  // =================================================
  private void indexStr(String str, int pRowId) {

    if ( strIndex == null )
      strIndex = new HashMap<String, List<int[]>>(this.totalNumberOfMrconsostyRows * 4);
   
    
    List<int[]> rows = this.strIndex.get( str );
    
    if ( rows == null) {
      rows = new ArrayList<int[]>(1);
     
      this.strIndex.put( str,  rows);
    } 
   
    int aRow[] = new int[1];
    aRow[0] = pRowId;
    rows.add( aRow);
    
   } // end Method indexAui() ------------------------


  // =================================================
  /**
   * getRow retrieves the i'th row from the mrconsosty table
   * 
   * @param pI
   * @return char[]
  */
  // =================================================
  public final char[] getRow(int pI) {
   
    char[] returnRow = null;
    
    if ( pI < this.totalNumberOfMrconsostyRows )
      returnRow = this.rowStore[ pI ];
    
    return returnRow;
    
  } // end Method getRow() ------

  // =================================================
  /**
   * getCuiBins the frequency of how many 
   * 
   * @return int[]
  */
  // =================================================
    public final int[] getCuiBins() {
      
      for ( int i = 0;  i < this.cuiBins.length; i++ ) {
        if ( this.cuiBins[i] > 0 )
          System.err.print( " " + i + "|" + this.cuiBins[i] + "\n" );
      }
      
     return this.cuiBins;
  }

  // =================================================
  /**
   * getCuiForAui retrieves the cui for an aui
   * 
   * @param pAui
   * @return char[]
  */
  // =================================================
   public char[]  getCuiForAui(char[] pAui) {
     
     char[] returnVal = null;
     HashSet<String> cuiHash = new HashSet<String>();
     String aui = new String( pAui);
     List<String> rows = getRowsFromAui(aui) ;
     
     if ( rows != null && !rows.isEmpty() ) {
       for ( String row: rows ) {
         String aRow = new String ( row );
         
         String[] cols = U.split(aRow);
         String cui = cols[0];
         cuiHash.add( cui);
         returnVal = cui.toCharArray();
         
         
       }
       
       if ( !cuiHash.isEmpty() ) {
         System.err.println("There are more than one unique cui for this aui " + pAui);
       }
     }
     
     return returnVal;
   
  } // end Method getCuiForAui() ----------------------

  // =================================================
  /**
   * getAuisFromCui
   * 
   * @param aCui
   * @return List<String()
   * @throws Exception 
  */
  // =================================================
   public final List<String> getAuisFromCui(String aCui) throws Exception {
     
     List<String> returnVal = null;
   
     try {
     List<String> rows = this.getRowsFromCui(aCui);
     
     if ( rows != null && !rows.isEmpty()) {
       returnVal = new ArrayList<String>();
       for ( String row : rows ) {
         String cols[] = U.split( row);
         returnVal.add(cols[MRCONSOSTY.FIELD_AUI].trim() );
         
       }
     }
     } catch (Exception e) {
       e.printStackTrace();
       System.err.println( "Issue trying to get the aui for this cui " + aCui + " " + e.toString());
       throw e;
     }
    return returnVal;
  } // end Method getAuisForCui() --------------------

  // =================================================
  /**
   * getPreferredConceptName returns the preferred concept name for this cui
   * 
   * @param pCui
   * @return String
  */
  // =================================================
   public String getPreferredConceptName(String pCui) {
    String returnVal = "no perferred term?";
 
    List<String> rows = getRowsFromCui(  pCui); 
    
    if ( rows != null )
      
      // Look for |P| .... |PF|    termStatus (TS)  .... sourse term type (STT)
      for ( String row: rows ) {
        String cols[ ] = U.split(row);
        String ts = cols[ FIELD_TS ];
        String stt = cols[ FIELD_STT ];
        
        if ( ts.contentEquals("P") && stt.contentEquals("PF") ) {
          returnVal = cols[ FIELD_STR ];
          break;
        
        }
      }
   
    
    return returnVal;
   } // end Method getPreferredConceptName() ----------

  // =================================================
  /**
   * merge merges a row of mrconso and a row of mrsty
   *  The mrsty row is just concatenated to the end of the mrconso row.
   * 
   * @param mrconsoRow
   * @param mrstyRow
   * @return String mrconsoRow + | + mrstyRow
  */
  // =================================================
  public static String merge(String mrconsoRow, String mrstyRow) {
   
    StringBuffer buff = new StringBuffer();
    buff.append( mrconsoRow);
    buff.append("|");
    buff.append(mrstyRow);
    
    return buff.toString();
    
  } // end Method merge() ----------------------------

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
      
      String     inputDir = U.getOption(args,  "--inputDir=", "./2019AA/META");
      String    outputDir = U.getOption(args, "--outputDir=", inputDir + "_terminology_" );
      String  mrconsostyFile = U.getOption( args, "--mrconsosty=", inputDir + "/mrconsosty.rrf");
      String totalNumberOfMrconsostyRows = U.getOption(args,"--totalNumberOfMrconsostyRows=", "10");
      
      PerformanceMeter meter = new PerformanceMeter();
    
      
      // read in mrconsosty
      meter.begin( " Reading in mrconsostysty " );
      MRCONSOSTY mrconsosty = new MRCONSOSTY( args );
      
     // int[] cuiBins = mrconsosty.getCuiBins();
    
      // read in mrsty 
      
      // read in mrhier
      
      //     Repeat until done
      // +---------------------
      // | find a seed term
      // |
      // | walk through data store to find aui/cui decendents in mrhier
      // |   
      // | walk through list of cuis to get mrconsosty
      // |
      // | talk through list of cuis to get mrsty
      // |
      // |merge mrconsosty and mrsty rows for list of cuis
      // +-------------------
      String aCui = "C0011351";
      String aAui = "A11981768";
      List<String> rows = null;
      int ctr = 0;
      
     
       rows = mrconsosty.getRowsFromCui( aCui );
      
     // int ctr =0;
      if ( rows != null && !rows.isEmpty() )
        for ( String row : rows ) 
          System.err.println( ctr++ + "|" + new String( row) );
      else 
        System.err.println( ctr++ + "| no records found" );
      
      
      rows = mrconsosty.getRowsFromAui( aAui );
      
      ctr =0;
      if ( rows != null && !rows.isEmpty() )
        for ( String row : rows ) 
          System.err.println( ctr++ + "|" + new String( row) );
      else 
        System.err.println( ctr++ + "| no records found" );
      
      
      
      meter.stop();
      
      System.err.println("Dohn");
      
    } catch ( Exception e ) {
      e.printStackTrace();
      System.err.println("Issue with creating mrconsosty from mrhier " + e.toString());
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
   String   mrconsosty = U.getOption( pArgs, "--mrconsosty=", inputDir + "/MRCONSOSTY.RRF");
   String totalNumberOfMrconsostyRows = U.getOption(pArgs,"--totalNumberOfMrconsostyRows=", "5545407");
   
  
   String args[] = {
       
       "--inputDir=" + inputDir,
       "--outputDir=" + outputDir,
       "--mrconsosty="    + mrconsosty,
       "--totalNumberOfMrconsoRows=" + totalNumberOfMrconsostyRows
   };

   
      
   if ( Use.usageAndExitIfHelp("MRCONSOSTY",   pArgs, args  ))
     System.exit(0);
    

   return args;

 }  // End Method setArgs() -----------------------

  // ------------------------------------------------
  // Class Variables
  // ------------------------------------------------
  char[][] rowStore = new char[100][];
  int totalNumberOfMrconsostyRows = 5545407;
  int mrconsostyI = 0;
  PerformanceMeter meter = null;
  private StringBuffer buff = new StringBuffer();
  
  HashMap<String, List<int[]>> cuiIndex = null;
  HashMap<String, int[]>       auiIndex = null;
  HashMap<String, List<int[]>> strIndex = null;
  private int maxAuiList  = 0;
  private int maxCuiList = 0;
  int cuiBins[] = new int[400];
  
  public static final int FIELD_CUI    =  0;
  public static final int FIELD_LAT    =  1;
  public static final int FIELD_TS     =  2;
  public static final int FIELD_LUI    =  3;
  public static final int FIELD_STT    =  4;
  public static final int FIELD_SUI    =  5;
  public static final int FIELD_ISPREF =  6;
  public static final int FIELD_AUI    =  7;
  public static final int FIELD_SAUI   =  8;
  public static final int FIELD_SCUI   =  9;
  public static final int FIELD_SDUI   = 10;
  public static final int FIELD_SAB    = 11;
  public static final int FIELD_TTY    = 12;
  public static final int FIELD_CODE   = 13;
  public static final int FIELD_STR    = 14;
  public static final int FIELD_SRL    = 15;
  public static final int FIELD_SUPRESS= 16;
  public static final int FIELD_CVF    = 17;
  
  public static final int FIELD_ONTOLOGY_CATEGORY = 18;   // a value added field
  public static final int FIELD_ONTOLOGY_ID       = 19;   // a value added field
  
  
  // public static final int FIELD_18     = 18;  
  public static final int FIELD_CUI2   =  18;
  public static final int FIELD_TUI    =  19;
  public static final int FIELD_STN    =  20;  // Semantic Tree number
  public static final int FIELD_STY    =  21;  // Semantic type  (is redundant with TUI)
  public static final int FIELD_ATUI   =  22;  // unique identifier for attribute?
  public static final int FIELD_CSV    =  23;
 
  // these fields are alternative mrconsosty locations - the ontology tools inject an ontology id, and ontology category 
  public static final int FIELD_X_CUI2   =  20;
  public static final int FIELD_X_TUI    =  21;
  public static final int FIELD_X_STN    =  22;  // Semantic Tree number
  public static final int FIELD_X_STY    =  23;  // Semantic type  (is redundant with TUI)
  public static final int FIELD_X_ATUI   =  24;  // unique identifier for attribute?
  public static final int FIELD_X_CSV    =  25;
 
  
  
  
}
