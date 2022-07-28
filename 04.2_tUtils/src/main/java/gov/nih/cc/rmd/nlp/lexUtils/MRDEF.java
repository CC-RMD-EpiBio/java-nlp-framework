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
 * MRDEF reads in and indexes the MRDEF table
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

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

/**
 * @author divitag2
 *
 */
public class MRDEF {

  // =================================================
  /**
   * Constructor
   * @throws Exception 
   *
   * 
  **/
  // =================================================
  public MRDEF() throws Exception {
    
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
  public MRDEF(String[] pArgs) throws Exception {
    
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
    String  MRDEFFile = null;
    
    try {
      
        System.err.println("Initializing MRDEF");
       
        inputDir = U.getOption(pArgs,  "--inputDir=", "./2020AA/META");
        MRDEFFile = U.getOption( pArgs, "--MRDEF=", inputDir + "/MRDEF.rrf");
        String  filteredOutSabs = U.getOption(pArgs,  "--filteredOutSabs=", "ICF-CY:GO" );
        this.totalNumberOfMRDEFRows = Integer.parseInt(U.getOption(pArgs,"--totalNumberOfMRDEFRows=", "246428"));
        this.rowStore = new char[this.totalNumberOfMRDEFRows][];
        
        
        initializeFilteredOutSabs( filteredOutSabs );
        
        read( MRDEFFile);
         
        System.err.println("Finished initializing MRDEF " );
        System.err.flush();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue trying to instantiate MRDEF from " +  MRDEFFile + " " + e.toString());
      throw new Exception( e );
    }
    
  } // =================================================
  /**
   * initializeFilteredOutSabs creates a hash of those
   * sources to filter out
   * 
   * @param pFilteredOutSabs   (a colon delimited set of sabs)
  */
  // =================================================
  private final void initializeFilteredOutSabs(String pFilteredOutSabs) {
    this.filteredOutSabs = new HashSet<String>();

    if ( pFilteredOutSabs != null && pFilteredOutSabs.trim().length() > 0 ) {
      String cols[]  = U.split(pFilteredOutSabs, ":" );
      if ( cols != null && cols.length > 0)
        for ( String col : cols )
            if ( col != null && col.trim().length() > 0 )
              this.filteredOutSabs.add(col.trim());
      
    }
    
  } // end Method initializeFilteredOutSabs() -------

  // End Method initialize () ----------------------

  

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
 
  try {
  
  List<int[]> MRDEFPtrs = this.cuiIndex.get( aCui);
  
  if ( MRDEFPtrs != null && !MRDEFPtrs.isEmpty())
    for ( int[] ptr : MRDEFPtrs ) {
      int aPtr = ptr[0];
      String aRow = new String( this.rowStore[aPtr] );
      if ( returnVal == null ) returnVal = new ArrayList<String>(MRDEFPtrs.size() );
      returnVal.add( aRow);
    }
  
  } catch (Exception e) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getRowsFromCui", "Something wrong here " + e.toString());
   
  }
  return returnVal;
} // end Method getRowsFromCui() ---------
 
//=================================================
/**
* getMRDEFRows returns MRDEF rows for these cuis
* @param pCCuis
* @return List<char[]> 
*/
//=================================================
public final List<String> getMRDEFRows(List<String> pCuis) {

List<String> returnVal = null;
try {
if ( pCuis != null && !pCuis.isEmpty()) {
  
  returnVal = new ArrayList<String>( pCuis.size());
  for ( String aCui : pCuis ) {
    List<String> c = getRowsFromCui(aCui);
    if ( c != null )
      returnVal.addAll(c );
  }
}
} catch ( Exception e) {
  e.printStackTrace();
  GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getMRDEFRows"," something Went wrong here " + e.toString());
}

return returnVal;
} // end Method getMRDEFRows() -------------------

  // =================================================
  /**
   * read 
   * 
   * @param MRDEFFile
   * @throws Exception
  */
  // =================================================
  private final void read(String MRDEFFile) throws Exception {
    
    
    String[] rows = null;
    try {
      
       rows =  U.readClassPathResourceIntoStringArray(MRDEFFile);
    } catch ( Exception e) {
      e.toString();
     try {
      rows = U.readFileIntoStringArray(MRDEFFile);
     } catch ( Exception e2) {
       e2.printStackTrace();
       System.err.println( "Issue reading the MRDEF file " + MRDEFFile + " " + e.toString() );
       throw e2;
     }
    }
      
      int ctr = 1;
      for ( String row : rows ) {
        
        if ( row != null && row.trim().length() > 0 && !row.startsWith("#")) {
          index( row);
          if ( ctr++ % 1000000 == 0 ) 
            this.meter.mark( " Processed " + ctr + " MRDEF records" );
          
        }
      } // end Loop through the input file
      
      
      /*
      BufferedReader in = null;
      in = new BufferedReader(  new InputStreamReader( new FileInputStream(MRDEFFile), "UTF8"));
               
       String row = null;
       int ctr = 1;     
       while ((row = in.readLine()) != null) {
           
         if ( row != null && row.trim().length() > 0 && !row.startsWith("#")) {
           index( row);
           if ( ctr++ % 1000000 == 0 ) 
             this.meter.mark( " Processed " + ctr + " MRDEF records" );
           
         }
       } // end Loop through the input file
               
       in.close();
      */
      
  } // end Method read() -----------------------------

  // =================================================
  /**
   * index
   *  creates indexes by cui.   
   * 
   * @param pRow
   * @throws Exception
  */
  // =================================================
 private final void index(String pRow) throws Exception  {
  
   
 
   try {
   String[] cols = U.split( pRow);
   
   String cui = cols[ FIELD_CUI ];
   String sab = cols[ FIELD_SAB ];
   
   if ( !filteredOutSabs(sab )) {
   
     this.rowStore[this.MRDEFI] = pRow.toCharArray();  // reads MRDEF in 14 sec - 2218 mem
   
  
     indexCui ( cui, this.MRDEFI );                    // indexes cuis in 24 - 14 sec  - 2702 mem
  
  
   
   
      this.MRDEFI++;
   } // end if not filtered out by sab  
   } catch (Exception e) {
     e.printStackTrace();
     System.err.println("Issue indexing row : " + pRow + " " + e.toString());
     throw e;
   }
   
  

    
  } // end Method index() ----------------------------

  // =================================================
  /**
   * filteredOutSabs returns true if this sab is on
   * the list of things to filter out
   * 
   * @param sab
   * @return boolean
  */
  // =================================================
 public  boolean filteredOutSabs(String sab) {
  
   boolean returnVal = false;
     if ( this.filteredOutSabs.contains( sab ) )
       returnVal = true;
     
   return returnVal;
   
  } // end Method filteredOutSabs() ------------------

  // =================================================
  /**
   * indexCui stores the row index for this cui
   * 
   * @param cui
   * @param MRDEFI2
  */
  // =================================================
 private void indexCui(String cui, int pRowId) {
    
   if ( cuiIndex == null )
     cuiIndex = new HashMap<String, List<int[]>>(this.totalNumberOfMRDEFRows *3 );
  
   
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
   * getDefinitions returns a row for each definition sab|def
   * 
   * @param pCui
   * @return String[]
  */
  // =================================================
    public final String[] getDefinitions(String pCui) {
   
    String[] returnVal = null;
   
    List<String> rows = getRowsFromCui( pCui);
    int i = 0;
    if ( rows != null && !rows.isEmpty() ) {
      returnVal = new String[ rows.size()];
      for ( String row: rows ) {
          String cols[] = U.split(row );
          returnVal[i++] = cols[FIELD_SAB] + "|" + cols[FIELD_DEF];
      }
    }
       
    
    return returnVal;
  }

  // =================================================
  /**
   * getRow retrieves the i'th row from the MRDEF table
   * 
   * @param pI
   * @return char[]
  */
  // =================================================
  public final char[] getRow(int pI) {
   
    char[] returnRow = null;
    
    if ( pI < this.totalNumberOfMRDEFRows )
      returnRow = this.rowStore[ pI ];
    
    return returnRow;
    
  } // end Method getRow() ------

  // =================================================
  /**
   * getCui returns the cui from the MRDEF row
   * 
   * @param pMRDEFRow
   * @return String
  */
  // =================================================
   public static String getCui(String pMRDEFRow) {
    
     String returnVal = null;
     if ( pMRDEFRow != null && pMRDEFRow.trim().length() > 0 ) {
       String[] cols = U.split(pMRDEFRow );
       if ( cols.length > 0 )
         returnVal = cols[FIELD_CUI];
     }
     
     return returnVal;
     
  } // end Method getCui() ---------------------------

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
  } // end Method getCuiBins() -----------------------

  
  

  

  // =================================================
  /**
   * getSize returns the number of rows in this MRDEF file
   * 
   *   Note: this is fed in as a parameter during initialization. 
   *   It's the number of array cells that was created
   *   
   * 
   * @return int
  */
  // =================================================
  public int getSize() {
    return this.rowStore.length;
  }

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
      
      String     inputDir = U.getOption(pArgs,  "--inputDir=", "./2020AA/META");
      String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_terminology_" );
      String  MRDEFFile = U.getOption( pArgs, "--MRDEF=", inputDir + "/MRDEF.rrf");
      String totalNumberOfMRDEFRows = U.getOption(pArgs,"--totalNumberOfMRDEFRows=", "10");
      
      PerformanceMeter meter = new PerformanceMeter();
    
      
      // read in MRDEF
      meter.begin( " Reading in MRDEF " );
      MRDEF MRDEF = new MRDEF( args );
      
    
      
      String aCui = "C0011570";
    
      List<String> rows = null;
      int ctr = 0;
      
     
       rows = MRDEF.getRowsFromCui( aCui );
      
   
      if ( rows != null && !rows.isEmpty() )
        for ( String row : rows ) 
          System.err.println( ctr++ + "|" + new String( row) );
      else 
        System.err.println( ctr++ + "| no records found" );
      
    
      
      meter.stop();
      
      System.err.println("Dohn");
      
    } catch ( Exception e ) {
      e.printStackTrace();
      System.err.println("Issue with creating MRDEFsty from mrhier " + e.toString());
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

   String     inputDir = U.getOption(pArgs,  "--inputDir=", "resources/vinciNLPFramework/sophia/2020AA");
   String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_terminology_" + dateStamp);
   String      MRDEF = U.getOption( pArgs, "--MRDEF=", inputDir + "/" + "MRDEF.RRF");
   String totalNumberOfMRDEFRows = U.getOption(pArgs,"--totalNumberOfMRDEFRows=", "246428");
   String  filteredOutSabs = U.getOption(pArgs,  "--filteredOutSabs=", "" );
   
  
   String args[] = {
       
       "--inputDir=" + inputDir,
       "--outputDir=" + outputDir,
       "--MRDEF="    + MRDEF,
       "--totalNumberOfMRDEFRows=" + totalNumberOfMRDEFRows,
       "--filteredOutSabs=" + filteredOutSabs
   };

   
   try { 
   if ( Use.usageAndExitIfHelp("MRDEF",   pArgs, args  ))
     System.exit(0);
   } catch (Exception e) {}

   return args;

 }  // End Method setArgs() -----------------------

  // ------------------------------------------------
  // Class Variables
  // ------------------------------------------------
  char[][] rowStore = new char[100][];
  int totalNumberOfMRDEFRows = 246428;
  int MRDEFI = 0;
  PerformanceMeter meter = null;
  private StringBuffer buff = new StringBuffer();
  
  HashMap<String, List<int[]>> cuiIndex = null;
  HashMap<String, int[]>       auiIndex = null;
  HashMap<String, List<int[]>> strIndex = null;
  private int maxAuiList  = 0;
  private int maxCuiList = 0;
  int cuiBins[] = new int[400];
  private HashSet<String> filteredOutSabs = null;
  
  // MRDEF.RRF|Definitions|CUI,AUI,ATUI,SATUI,SAB,DEF,SUPPRESS,CVF
  public static final int FIELD_CUI    =  0;
  public static final int FIELD_AUI    =  1;
  public static final int FIELD_ATUI   =  2;
  public static final int FIELD_SATUI  =  3;
  public static final int FIELD_SAB    =  4;
  public static final int FIELD_DEF    =  5;
  public static final int FIELD_ISPREF =  6;
  public static final int FIELD_SUPRESS = 7;
  public static final int FIELD_CVF    =  8;
 
} // end Class MRDEF --------------------------
