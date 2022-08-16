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
 * MRHIER
 * reads in and indexes the mrhier table
 * This resource indexes on aui2 -> aui1|inverse-relationship|sab
 *                           cui -> hrhier  <---- do I need this?
 * 
 * @author     Guy Divita
 * @created    Jul 23, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.lexUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
public class MRHIER {

  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws Exception 
   * 
  **/
  // =================================================
  public MRHIER() throws Exception {
  
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
public MRHIER(String[] pArgs) throws Exception {
  
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
  String  MRHIERFile = null;
  
  try {
      this.meter = new PerformanceMeter(System.err);
      this.meter.begin("Starting the MRHIER process");
         inputDir = U.getOption(pArgs,  "--inputDir=", "./2020AA/META");
      MRHIERFile = U.getOption( pArgs, "--MRHIER=", inputDir + "/MRHIER.RRF");
      this.totalNumberOfMRHIERRows = Integer.parseInt(U.getOption(pArgs,"--totalNumberOfMRHIERRows=", "10"));
      this.rowStore = new char[this.totalNumberOfMRHIERRows][];
   
      this.relationUtils = new RelationUtilities( pArgs);  
      
      read( MRHIERFile );
      
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", "Finished initializing MRHIER ");
      
   
      
      MRCONSO mrconso = new MRCONSO ( pArgs );
      this.setMRCONSO( mrconso);
     
      this.meter.stop();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", "MHIER: initialize : done " );
  
  
  } catch (Exception e) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(),"initialize", "Issue trying to instantiate MRHIER from " +  MRHIERFile + " " + e.toString());
    throw new Exception( e );
  }
  
} // End Method initialize () ----------------------
  

// =================================================
/**
 * setMRCONSO sets the mrconso for cui retrieval
 * 
 * @param pMrconso
*/
// =================================================
public void setMRCONSO(MRCONSO pMrconso) {
 
  this.mrconso = pMrconso;
  
} // end Method setMRCONSO() ------------------------

//=================================================
/**
* getMRCONSO gets the mrconso for cui retrieval
*
* 
* @return MRCONSO
*/
//=================================================
public MRCONSO getMRCONSO() {

 return this.mrconso ;

} // end Method setMRCONSO() ------------------------



//=================================================
/**
* getDescendents retrieves all the descendant auis of this aui
* 
* This method should return a unique, ordered set, but guard against
* cycles. 
* 
* @param pAui
* @return List<char[]>  of aui's
*/
//=================================================
public final List<char[]> getDescendantAuis( String pAui) {
   List<char[] > returnVal = new ArrayList<char[]>(); 
   
   getDescendantAuis( returnVal, pAui);
   
   return returnVal;
} // end Method getDescendantAuis() ----------------

// =================================================
/**
 * getDescendents retrieves all the descendant auis of this aui
 * 
 * This method should return a unique, ordered set, but guard against
 * cycles. 
 * 
 * @param pAuiList
 * @param pAui
 * @return boolean ( if a cycle or no more auis' can be found)
*/
// =================================================
public final boolean getDescendantAuis(List<char[]> pAuiList, String pAui) {
 
  
  boolean done = false;
  
  while ( !done ) {
    
    List<char[]> rows = this.auiIndex.get( pAui );
    
    if ( rows != null && rows.size() > 0 ) {
     
   
      for ( char[] row : rows ) {
        // for each aui2, look the descendents up for these
       
        String row2 = new String( row );
        String cols[] = U.split( row2 );
        
        char[] aui2 = cols[0].toCharArray();
        
        addToAuiList( pAuiList, aui2);
        
     
      }
    }
    done = true;
    
  }
  
  return done;
} // end Method getDescendants() -------------------

// =================================================
/**
 * addToAuiList adds this aui to the list of aui's if
 * it's not already on the list.  If it's already on the
 * list, this method will return true;
 * 
 * @param pAuiList
 * @param pAui
 * @return boolean
*/
// =================================================
private final synchronized boolean addToAuiList(List<char[]> pAuiList, char[] pAui) {
  boolean returnVal = false;
  
    for ( char[] aAui : pAuiList ) {
      if ( java.util.Arrays.equals( aAui, pAui )) {
        returnVal = true;
        break;
      }
    }
    if ( !returnVal )
      pAuiList.add( pAui);
    
  return returnVal;
} // end Method addToAuiList() ---------------------

//=================================================
/**
* getAuisForCui retrieves auis from a cui
* 
* @param pCui
* @param pAui
* @return List<String> 
 * @throws Exception 
*/
//=================================================
public final List<String> getAuisForCui(String aCui) throws Exception {
	List<String> returnVal = null;
	
	
	returnVal = mrconso.getAuisFromCui( aCui );
	
	
	return returnVal;
} // end Method getAuisForCui() --------------------

//=================================================
/**
* getCuisForAUI retrieves cuis from a aui
* 
* @param pAui
* @return List<String> 
* @throws Exception 
*/
//=================================================
public final List<String> getCuisForAui(String pAui) throws Exception {
List<String> returnVal = null;


returnVal = mrconso.getCuisFromAui( pAui );


return returnVal;
} // end Method getCuisForAui() --------------------

// =================================================
/**
 * getDescendents will return mrconso rows for the
 * decendents of this aui
 * 
 * 
 * @param aAui
 * @return List<char[] > of mrconso rows
*/
// =================================================
public final List<String> getDescendents(String aAui) {
  
  List<String> decendentMRCONSORows = null;
  
  try {
    List<char[]>decendentAuis = getDescendantAuis( aAui );
    List<String>decendentCuis = getDecendentCuis(decendentAuis );
         decendentMRCONSORows = mrconso.getMRCONSORows(decendentCuis );
 
  } catch ( Exception e ) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getDescendents", "Issue getting decendents " + e.toString());
    throw e;
  }
  return decendentMRCONSORows;
} // end Method getDescendents() ------------------

// =================================================
/**
 * getDecendentCuis 
 * 
 * @param pDecendentAuis
 * @return List<String>  of cuis
*/
// =================================================
private List<String> getDecendentCuis(List<char[]> pDecendentAuis) {
  
  List<String>  returnVal = new ArrayList<String>( pDecendentAuis.size() );
  HashSet<String> cuiHash = new HashSet<String>();
  
  for ( char[] aui : pDecendentAuis   ) {
    List<String> cuis =  this.mrconso.getCuisFromAui(new String(aui)) ;
    
    if ( cuis != null && !cuis.isEmpty())
      for ( String cui: cuis )
        if ( !cuiHash.contains( cui) ) { 
          cuiHash.add( cui );
          returnVal.add( cui );
        }
  }
  
  return returnVal;
} // end Method getDecendentCuis() -----------------



// =================================================
/**
 * getDecendentRowsForCui returns mrconso rows for the dependents of this cui
 * 
 * @param aCui
 * @return List<char[] >
 * @throws Exception 
*/
// =================================================
public final List<String> getDecendentRowsForCui(String aCui) throws Exception {
 
   
   List<String> returnVal = null;
   List<String> rows = null;
   
   List<String> auis = this.getAuisForCui( aCui );
   
   if ( auis != null && !auis.isEmpty())
     for ( String aui : auis ) {
    
       returnVal = new ArrayList<String>();
       rows  = this.getDescendents( aui );
     
       if ( rows != null && !rows.isEmpty() )
         returnVal.addAll( rows);
           
     }
   
   else {
     String msg = "Ouch - cui with no aui? " + aCui ;
    // throw new Exception ( msg);
   }
   
  return returnVal;
  
} // end Method getDecendentRowsForCui() -----------

// =================================================
/**
 * getDescendentRowsForCui will deposit an output file for this seed cui
 * with all the descendant concepts from it.
 * 
 * @param pOutputDir
 * @param pCui
 * @return List<String>  
 * @throws Exception 
*/
// =================================================
public final List<String> getDescendentRowsForCui(String outputDir, String pCui) throws Exception {
 
  PrintWriter out = null;
  List<String> descendents = null;
  try {
    descendents = getDecendentRowsForCui(pCui);
    
    if ( descendents != null && !descendents.isEmpty())
    try {
      out = new PrintWriter( outputDir + "/MHIER_" + pCui + ".txt");
      
      for ( String row : descendents ) 
        out.print( row + "\n");
      
      out.close();
      
    } catch (Exception e2) {
      e2.printStackTrace();
      String msg = GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getDescendentRowsForCui",  "Issue writting the descendents out for for " + pCui + " :" + e2.toString());
      throw new Exception (msg);
    }
    
    
    
  } catch (Exception e) {
    e.printStackTrace();
    String msg = GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getDecendentRowsForCui",  "Issue getting the decendents for " + pCui + " :" + e.toString());
    throw new Exception (msg);
  }
  
  
  return descendents;
  
} // end Method getDecendentRowsForCui() ----------- 

// =================================================
/**
 * read 
 * 
 * @param MRHIERFile
*/
// =================================================
private final void read(String MRHIERFile) {
  
  
  BufferedReader in = null;
  try {
    
    in = new BufferedReader(  new InputStreamReader( new FileInputStream(MRHIERFile), "UTF8"));
             
     String row = null;
     long ctr = 1;     
     while ((row = in.readLine()) != null) {
         
       if ( row != null && row.trim().length() > 0 && !row.startsWith("#")) {
         index( row, ctr);
         if ( ctr++ % 1000000 == 0 ) 
           this.meter.mark( " Processed " + ctr + " mrhier records" );
         
       }
     } // end Loop through the input file
             
     in.close();
    
    
    
  } catch ( Exception e) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "read", "Issue reading the MRHIER file " + MRHIERFile + " " + e.toString() );
  }
  
} // end Method read() -----------------------------

//=================================================
/**
* getRelRows retrieves the rows that are indexed from aui1
* 
*    aui1|relation|aui2|sab
* 
* @param pAui1
* @return String
*/
//=================================================
public final String[] getRelRows(String pAui1) {
  
  String[] returnVal = null;
  
  List<char[]> rows = this.auiIndex.get( pAui1 );
  
  if ( rows != null && !rows.isEmpty()) {
    returnVal = new String[ rows.size()];
    for ( int i = 0; i < rows.size(); i++ ) {
      String[] cols = U. split( new String(rows.get(i)) );
      String aui2 = cols[0];
      String relation = cols[1];
      String sab = cols[2];
      String id = cols[3];
      returnVal[i] = pAui1 + "|" + relation + "|" + aui2 + "|" + sab + "|" + id;
    }
  }
  
  return returnVal;
  
} // end Method getRelRows() -----------------------

// =================================================
/**
 * getRelCuiRows retrieves the rows that are indexed from aui1
 * but mapped back to cuis
 * 
 * cui1|relation|cui2|sab
 * 
 * @param pAui1
 * @return String
 * @throws Exception
 */
// =================================================
public final List<String> getRelCuiRows(String pAui1) throws Exception {

  List<String> returnVal = new ArrayList<String>();

  String[] rows = getRelRows(pAui1);

  for (String row : rows) {
    String[] cols = U.split(row);
   //  String id = cols[0];
    String aui1 = cols[0];
    String relation = cols[1];
    String aui2 = cols[2];
    String source = cols[3];
    String id = cols[4];
    
    List<String> cuis1 = this.getCuisForAui(aui1);
    if (cuis1 != null && !cuis1.isEmpty()) 
      for (String cui1 : cuis1) {
        String conceptName1 = mrconso.getPreferredConceptName(cui1);
      List<String> cuis2 = this.getCuisForAui(aui2);
      if (cuis2 != null && !cuis2.isEmpty()) 
        for (String cui2 : cuis2) {
          String conceptName2 = mrconso.getPreferredConceptName(cui2);
          String buff = id + "|" + cui1 + "|" + conceptName1 + "|" + relation + "|" + cui2 + "|" + conceptName2 + "|" + source;
        
          returnVal.add(buff);
      }
    }
  }
  return returnVal;
} // end Method getRelCuiRows() --------------------

// =================================================
/**
 * index
 *  creates indexes for aui2 -> aui1|inverse-relationship|sab
 * 
 * @param pRow
 * @param pRowNumb
*/
// =================================================
private final void index(String pRow, long pRowNum) {

 

 
 String[] cols = U.split( pRow);

 String aui1 = cols[ FIELD_AUI ];
 String aui2 = cols[ FIELD_PAUI ];
 String  sab = cols[ FIELD_SAB ];
 String relationship = cols[ FIELD_RELA];
 
 if ( relationship == null || relationship.trim().length() == 0 )
   relationship = "inverse_isa";
 
 index ( aui1, aui2, relationship, sab, pRowNum ); 
 


 String inverseRelationship = relationUtils.getInverseRelation( relationship );
 
 if ( inverseRelationship != null && inverseRelationship.trim().length() > 0  )
   index ( aui2, aui1, inverseRelationship, sab , pRowNum);   
 else 
   index( aui2, aui1, "is-a", sab, pRowNum);
 
 this.mrhierI++;
 


  
} // end Method index() ----------------------------

// =================================================
/**
 * index 
 * 
 * @param pAui1
 * @param pAui2
 * @param pRelationship
 * @param pSab
*/
// =================================================
  private final void index(String pAui1, String pAui2, String pRelationship, String pSab, long pRowNum) {
  
    if ( auiIndex == null )
      auiIndex = new HashMap<String, List<char[]>>(this.totalNumberOfMRHIERRows *3 );
   
    
  
    
    
    char[] aRow = new String( pAui2 + "|" +  pRelationship + "|" + pSab + "|" + "MHR"+ U.zeroPad( pRowNum, 10 ) ).toCharArray() ;

    List<char[]> rows = this.auiIndex.get( pAui1 );
    
    
    
    if ( rows == null) {
      rows = new ArrayList<char[]>(1);
      rows.add( aRow );
     
    } else {
       rows.add( aRow);
    }
   
    
    this.auiIndex.put( pAui1,  rows);
    
   
    
    
    
  
} // end Method index() ---------------------------



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
    String  mrhierFile = U.getOption( args, "--mrhier=", inputDir + "/mrhier.rrf");
    String totalNumberOfmrhierRows = U.getOption(args,"--totalNumberOfmrhierRows=", "10");
    
    PerformanceMeter meter = new PerformanceMeter();
  
    
    // read in mrhier
    meter.begin( " Reading in MRHIER  " );
    MRHIER mrhier = new MRHIER( args );
 
  
 
 
    String aCui = "C1285340";
    
    List<String> auis = mrhier.getAuisForCui( aCui );
   
    List<String> rows = null;
    int ctr = 0;
 
     for ( String aui : auis ) {
       rows  = mrhier.getDescendents( aui );
     
    
    ctr =0;
    if ( rows != null && !rows.isEmpty() )
      for ( String row : rows ) 
        GLog.println( ctr++ + "|" +  row );
    else 
      GLog.println( ctr++ + "| no records found" );
     }
    
    // -------------------------------- */
    // use unique lui's - normalize to remove the nos, () expressions
    
    mrhier.close();
    meter.stop();
    
    GLog.println("MRHIER: Dohn");
    
  } catch ( Exception e ) {
    e.printStackTrace();
    GLog.println("Issue with creating mrhiersty from mrhier " + e.toString());
  }


} // end Method main() ----------------------------


//=================================================
/**
* close clears up memory
* 
*/
//=================================================
public final void close() {

this.rowStore = null;
this.auiIndex = null;
this.mrconso = null;

Runtime.getRuntime().gc();

}

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

 String     inputDir = U.getOption(pArgs,  "--inputDir=", "./2020AA/META");
 String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_terminology_" + dateStamp);
 String      mrhier = U.getOption( pArgs, "--mrhier=", inputDir + "/mrhier.RRF");
 String totalNumberOfMRHIERRows = U.getOption(pArgs,"--totalNumberOfMRHIERRows=", "17406195");
 String totalNumberOfMrconsoRows = U.getOption(pArgs,"--totalNumberOfMrconsoRows=", "8946369");
 String     relationsWithInverses = U.getOption(pArgs,  "--relationsWithInverses=", "./resources/relationsWithInverses.csv");
 String     inverseRelations      = U.getOption(pArgs,  "--inverseRelations=", "./resources/inverseRelations.csv");
 String     allowedRelations      = U.getOption(pArgs, "--allowedRelations=", "./resources/allowedRelations.txt");
 String                   version = "2022.02.17";
 

 
 

 String args[] = {
     
     "--inputDir=" + inputDir,
     "--outputDir=" + outputDir,
     "--mrhier="    + mrhier,
     "--totalNumberOfMrconsoRows=" + totalNumberOfMrconsoRows,
     "--totalNumberOfMRHIERRows=" + totalNumberOfMRHIERRows,
     "--relationsWithInverses=" + relationsWithInverses, 
     "--inverseRelations=" + inverseRelations,
     "--allowedRelations=" + allowedRelations,
     "--version="                  + version
 };

 
 if ( Use.version(pArgs, args ) || Use.usageAndExitIfHelp( "MRHIER", pArgs, args ) )
   Runtime.getRuntime().exit(0);
 

 return args;

}  // End Method setArgs() -----------------------

// ------------------------------------------------
// Class Variables
// ------------------------------------------------
char[][] rowStore = new char[100][];
int totalNumberOfMRHIERRows = 5545407;
int mrhierI = 0;
PerformanceMeter meter = null;
HashMap<String, List<char[]>>       auiIndex = null;
MRCONSO mrconso = null;
RelationUtilities relationUtils = null;


final static  String ExampleAui = "A3513188"; // C1285340|ENG|P|L3017748|PF|S3369470|Y|A3513188|482320016|363180009||SNOMEDCT_US|PT|363180009|Inflammatory disorder of the respiratory tract|9|N|256|

// |CUI,AUI,CXN,PAUI,SAB,RELA,PTR,HCD,CV

public static final int FIELD_CUI    =  0;
public static final int FIELD_AUI    =  1;
public static final int FIELD_CXN    =  2;
public static final int FIELD_PAUI   =  3;
public static final int FIELD_SAB    =  4;
public static final int FIELD_RELA   =  5;
public static final int FIELD_PTR    =  6;
public static final int FIELD_HCD    =  7;
public static final int FIELD_CV     =  8;


  
} // end Class MRHIER() ----------------
