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
 * MRREL
 * reads in and indexes the mrREL table
 * This resource indexes on aui2 -> aui1|inverse-relationship|sab
 *                           cui -> hrREL  <---- do I need this?
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
public class MRREL {

  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws Exception 
   * 
  **/
  // =================================================
  public MRREL() throws Exception {
  
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
public MRREL(String[] pArgs) throws Exception {
  
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
  String  MRRELFile = null;
  
  System.err.println("here ");
  
  try {
     PerformanceMeter ameter = new PerformanceMeter();
      ameter.begin("Starting the MRREL process");
         inputDir = U.getOption(pArgs,  "--inputDir=", "./2019AA/META");
      MRRELFile = U.getOption( pArgs, "--MRREL=", inputDir + "/MRREL.RRF");
      this.totalNumberOfMRRELRows = Integer.parseInt(U.getOption(pArgs,"--totalNumberOfMRRELRows=", "10"));
      this.rowStore = new char[this.totalNumberOfMRRELRows][];
      
      this.relationsUtils = new RelationUtilities( pArgs);
      
      
      read( MRRELFile, ameter );
      
   
      
      MRCONSO mrconso = new MRCONSO ( pArgs );
      this.setMRCONSO( mrconso);
  
     
      ameter.stop();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize","Finished initializing MRREL ");
      
  } catch (Exception e) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", "Issue trying to instantiate MRREL from " +  MRRELFile + " " + e.toString());
    throw new Exception( e );
  }
  
} // End Method initialize () ----------------------
  

// =================================================
/**
 * setMRCONSO set's the mrconso for cui retrieval
 * 
 * @param pMrconso
*/
// =================================================
public void setMRCONSO(MRCONSO pMrconso) {
 
  this.mrconso = pMrconso;
  
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
public final List<char[]> getDescendantAuis( String pCui) {
   List<char[] > returnVal = new ArrayList<char[]>(); 
   
   getDescendantCuis( returnVal, pCui);
   
   return returnVal;
} // end Method getDescendantAuis() ----------------

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
 * getDescendents retrieves all the descendant cuis of this cui
 * 
 * This method should return a unique, ordered set, but guard against
 * cycles. 
 * 
 * @param pCuiList
 * @param pCui
 * @return boolean ( if a cycle or no more auis' can be found)
*/
// =================================================
public final boolean getDescendantCuis(List<char[]> pCuiList, String pCui) {
 
  
  boolean done = false;
  
  while ( !done ) {
    
    List<char[]> rows = this.cuiIndex.get( pCui );
    
    if ( rows != null && rows.size() > 0 ) {
     
   
      for ( char[] row : rows ) {
        // for each aui2, look the descendents up for these
       
        String row2 = new String( row );
        String cols[] = U.split( row2 );
        
        char[] cui2 = cols[0].toCharArray();
        
        addToCuiList( pCuiList, cui2);
        
     
      }
    }
    done = true;
    
  }
  
  return done;
} // end Method getDescendants() -------------------

// =================================================
/**
 * addToCuiList adds this cui to the list of cui's if
 * it's not already on the list.  If it's already on the
 * list, this method will return true;
 * 
 * @param pCuiList
 * @param pCui
 * @return boolean
*/
// =================================================
private final synchronized boolean addToCuiList(List<char[]> pCuiList, char[] pCui) {
  boolean returnVal = false;
  
    for ( char[] aAui : pCuiList ) {
      if ( java.util.Arrays.equals( aAui, pCui )) {
        returnVal = true;
        break;
      }
    }
    if ( !returnVal )
      pCuiList.add( pCui);
    
  return returnVal;
} // end Method addToAuiList() ---------------------



// =================================================
/**
 * getDescendents will return mrconso rows for the
 * decendents of this cui
 * 
 * 
 * @param aCui
 * @return List<char[] > of mrconso rows
*/
// =================================================
public final List<String> getDescendents(String aCui) {
  
  List<String> decendentMRCONSORows = null;
  
  try {
    
    List<char[]> cuiCharList = this.cuiIndex.get( aCui);
    if ( cuiCharList == null || cuiCharList.isEmpty() ) {
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "getDescendents","Something missing from mrrel for cui " + aCui );
      return( null);
    }
    List<String> cuiList = new ArrayList<String>( cuiCharList.size());
    
    for ( char[] aCuiChar : cuiCharList) {
      String buff =  new String( aCuiChar );
      String[] cols = U.split(buff);
      cuiList.add( new String( cols[0] ));
    }
    
    
    decendentMRCONSORows = mrconso.getMRCONSORows(cuiList );
 
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
   
   
     returnVal = new ArrayList<String>();
     rows  = this.getDescendents( aCui );
     
     if ( rows != null && !rows.isEmpty() )
       returnVal.addAll( rows);
           

   
  return returnVal;
  
} // end Method getDecendentRowsForCui() -----------

// =================================================
/**
 * read 
 * 
 * @param MRRELFile
*/
// =================================================
private final void read(String MRRELFile, PerformanceMeter pMeter) {
  
  
  BufferedReader in = null;
  try {
    
    in = new BufferedReader(  new InputStreamReader( new FileInputStream(MRRELFile), "UTF8"));
             
     String row = null;
     int ctr = 1;     
     while ((row = in.readLine()) != null) {
         
       if ( row != null && row.trim().length() > 0 && !row.startsWith("#")) {
         index( row);
         if ( ctr++ % 1000000 == 0 ) 
           pMeter.mark( " Processed " + ctr + " mrREL records" );
         
       }
     } // end Loop through the input file
             
     in.close();
    
    
    
  } catch ( Exception e) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "read", "Issue reading the MRREL file " + MRRELFile + " " + e.toString() );
  }
  
} // end Method read() -----------------------------


// =================================================
/**
 * index
 *  creates indexes for aui2 -> aui1|inverse-relationship|sab
 *  
 *  MRREL.RRF|Related Concepts|CUI1,AUI1,STYPE1,REL,CUI2,AUI2,STYPE2,RELA,RUI,SRUI,SAB,SL,RG,DIR,SUPPRESS,CVF|16|33454294|3189137935|
 *  
 *  example: 
 *  C0000921|A3090907|SCUI|CHD|C0417035|A3129885|SCUI|isa|R19913309|336786024|SNOMEDCT_US|SNOMEDCT_US||Y|N||
 *     |                           |                    |
 *  accidental fall            Fall from viaduct           
 * 
 * @param pRow
*/
// =================================================
private final void index(String pRow) {

 

 
 String[] cols = U.split( pRow);

 String cui1 = cols[ FIELD_CUI2 ];       // <-------- this is the second field (see above) ----
 String cui2 = cols[ FIELD_CUI1 ];      
 String  sab = cols[ FIELD_SAB ];
 String generalRelation = cols[ FIELD_REL1];
 String specificRelation = cols[FIELD_RELA];
 String relationshipID = cols[FIELD_RUI];
 String relation = null;
 
 if ( specificRelation!= null && specificRelation.trim().length()> 0)
   relation = specificRelation;
 else if (generalRelation != null && generalRelation.trim().length() >0 )
   relation = generalRelation;
 
  String inverseRelation = this.relationsUtils.getInverseRelation( relation ); 
 
  if ( this.relationsUtils.isRelationAllowed( relation )) 
   index ( cui1, cui2, relation, sab, relationshipID );   

  if ( this.relationsUtils.isRelationAllowed( inverseRelation ))
     index ( cui2, cui1, inverseRelation, sab, relationshipID ); 
 

} // end Method index() ----------------------------

// =================================================
/**
 * isRelationAllowed returns true if the relationship is on the allowed relationship lis
 * 
 * @param pRelation
 * @return boolean
 * @throws Exception 
*/
// =================================================
public final boolean isRelationAllowed(String pRelation ) throws Exception {
 
  boolean returnVal = false;
  
  if ( this.allowedRelations == null  )
    initializeAllowedRelations();
   
    
  returnVal = this.allowedRelations.contains( pRelation ) ; 
   
  return returnVal;
} // end Method isRelationAllowed() ------------

// =================================================
/**
 * initializeAllowedRelations reads in a list of allowed relationships
 * 
 * @param pArgs
 * @throws Exception 
 * 
*/
// =================================================
private void initializeAllowedRelations() throws Exception {

  this.allowedRelations = new HashSet<String>();
  
  try {
    String[] allowedRelations = U.readFileIntoStringArray(_allowedRelationFileName);
    
    if ( allowedRelations != null && allowedRelations.length > 0 )
      for ( String allowedRelation : allowedRelations )
        if ( !allowedRelation.startsWith("#"))
          this.allowedRelations.add( allowedRelation.trim());
    
  } catch (Exception e) {
    e.printStackTrace();
    GLog.println("Issue reading in the allowed Relations " + e.toString());
    throw e;
  }
  
 } // end Method initializeAllowedRelations() -------

// =================================================
/**
 * index 
 * 
 * @param pCui1
 * @param pCui2
 * @param pRelationship
 * @param pSab
 * @param pRelationshipID
*/
// =================================================
  private final void index(String pCui1, String pCui2, String pRelationship, String pSab, String pRelationshipID) {
  
    if ( cuiIndex == null )
      cuiIndex = new HashMap<String, List<char[]>>(this.totalNumberOfMRRELRows *3 );
   

    char[] aRow = new String( pCui2 + "|" +  pRelationship + "|" + pSab + "|" + pRelationshipID ).toCharArray() ;
    List<char[]> rows = this.cuiIndex.get( pCui1 );
    
    
    if ( rows == null) {
      rows = new ArrayList<char[]>(1);
      rows.add( aRow );
     
    } else {
       rows.add( aRow);
    }
   
    
    this.cuiIndex.put( pCui1,  rows);
   
  
} // end Method index() ---------------------------
  
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
  String cui = null;
  
  char[] cui_ = this.mrconso.getCuiForAui( pAui1.toCharArray());
  
  if ( cui_ != null)
    cui = new String( cui_);
  
  returnVal = getRelRowsForCui( cui);
 
  
  return returnVal;
  
} // end Method getRelRows() -----------------------


//=================================================
/**
* getRelRowsForCui retrieves the rows that are indexed from cui
* 
*   Cui1|relation|cui2|sab|id
* 
* @param pCui1
* @return String
*/
//=================================================
public final String[] getRelRowsForCui(String pCui1) {

String[] returnVal = null;



List<char[]> rows = this.cuiIndex.get( pCui1 );

String conceptName1 = mrconso.getPreferredConceptName(pCui1);

if ( rows != null && !rows.isEmpty()) {
  returnVal = new String[ rows.size()];
  for ( int i = 0; i < rows.size(); i++ ) {
    String[] cols = U. split( new String(rows.get(i)) );
    String cui2 = cols[0];
    String conceptName2 = mrconso.getPreferredConceptName(cui2);
    String relation = cols[1];
    String sab = cols[2];
    String id = cols[3];
    returnVal[i]  = id + "|" + pCui1 + "|" + conceptName1 + "|" + relation + "|" + cui2 + "|" + conceptName2 + "|" + sab;
  }
}

return returnVal;

} // end Method getRelRowsForCui() -----------------

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

//=================================================
/**
* inverseRelationship 
*   returns the inverse relationship if there is a logical inverse.
*   Otherwise, null is returned.
*    
*   isa <->inverse-isa
*   PAR <->CHD                           parent/child
*   part_of <-> has_part
*   member_of <-> has_member
*   tributary_of <-> has_tributary
*   RB <-> RN                             broader than/narrower than
* 
* @param pRelationship  
* @return String
*/
// =================================================
public final static String inverseRelationship(String pRelationship) {
 
    String returnVal = null;

    if (pRelationship == null || pRelationship.trim().length() == 0) return returnVal;
    String relationship = new String(pRelationship);
    switch (relationship) {
      case "isa":
        returnVal = "inverse_isa";
        break;
      case "PAR":
        returnVal = "CHD";
        break;
      case "inverse-isa":
      case "inverse_isa":
        returnVal = "isa";
        break;
      case "CHD":
        returnVal = "PAR";
        break;
      case "part_of":
        returnVal = "has_part";
        break;
      case "member_of":
        returnVal = "has_member";
        break;
      case "tributary_of":
        returnVal = "has_tributary";
        break;

      case "AQ":
      case "DEL":
      case "QB":
      case "RL":
      case "RO":
      case "RQ":
      case "RU":
      case "SIB":
      case "SY":
      case "XR":
        returnVal = null;
        break;
      case "RB":
        returnVal = "RN";
        break;
      case "RN":
        returnVal = "RB";
        break;
    }

    return returnVal;
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
    
    String     inputDir = U.getOption(args,  "--inputDir=", "./2019AA/META");
    String    outputDir = U.getOption(args, "--outputDir=", inputDir + "_terminology_" );
    String  mrRELFile = U.getOption( args, "--mrREL=", inputDir + "/mrREL.rrf");
    String totalNumberOfmrRELRows = U.getOption(args,"--totalNumberOfmrRELRows=", "10");
    
    PerformanceMeter meter = new PerformanceMeter();
  
    
    // read in mrREL
    meter.begin( " Reading in MRREL  " );
    MRREL mrREL = new MRREL( args );
 
  
 
 
    String aCui = "C1285340";
    List<String> rows = mrREL.getDescendents( aCui );
     
    
    int ctr =0;
    if ( rows != null && !rows.isEmpty() )
      for ( String row : rows ) 
        GLog.println( ctr++ + "|" +  row );
    else 
      GLog.println( ctr++ + "| no records found" );
     
    
    // -------------------------------- */
    // use unique lui's - normalize to remove the nos, () expressions
    
    meter.stop();
    
    GLog.println("MRREL: Dohn");
    
    mrREL.close();
  } catch ( Exception e ) {
    e.printStackTrace();
    System.err.println("Issue with creating mrRELsty from mrREL " + e.toString());
  }


} // end Method main() ----------------------------


// =================================================
/**
 * close clears up memory
 * 
*/
// =================================================
public final void close() {
 
  this.rowStore = null;
  this.cuiIndex = null;
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

 String     inputDir = U.getOption(pArgs,  "--inputDir=", "./2019AA/META");
 String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_terminology_" + dateStamp);
 String      MRREL = U.getOption( pArgs, "--MRREL=", inputDir + "/MRREL.RRF");
 String totalNumberOfMRRELRows = U.getOption(pArgs,"--totalNumberOfMRRELRows=", "33454294");
 String totalNumberOfMrconsoRows = U.getOption(pArgs,"--totalNumberOfMrconsoRows=", "7825061");
 String     relationsWithInverses = U.getOption(pArgs,  "--relationsWithInverses=", "./resources/relationsWithInverses.csv");
 String     inverseRelations      = U.getOption(pArgs,  "--inverseRelations=", "./resources/inverseRelations.csv");
 String     allowedRelations      = U.getOption(pArgs, "--allowedRelations=", "./resources/allowedRelations.txt");
 String                   version = "2022.02.17";

 
 

 String args[] = {
     
     "--inputDir=" + inputDir,
     "--outputDir=" + outputDir,
     "--MRREL="    + MRREL,
     "--totalNumberOfMrconsoRows=" + totalNumberOfMrconsoRows,
     "--totalNumberOfMRRELRows=" + totalNumberOfMRRELRows,
     "--relationsWithInverses=" + relationsWithInverses, 
     "--inverseRelations=" + inverseRelations,
     "--allowedRelations=" + allowedRelations,
     "--version="                  + version
    
 };

 if ( Use.version(pArgs, args ) || Use.usageAndExitIfHelp( "MRREL", pArgs, args ) )
   Runtime.getRuntime().exit(0);


 return args;

}  // End Method setArgs() -----------------------

// ------------------------------------------------
// Class Variables
// ------------------------------------------------
char[][] rowStore = new char[100][];
int totalNumberOfMRRELRows = 33454294;
// int mrRELI = 0;
// PerformanceMeter meter = null;
HashMap<String, List<char[]>>       cuiIndex = null;
MRCONSO mrconso = null;
 String _allowedRelationFileName = null;
 HashSet<String>  allowedRelations = null;
 RelationUtilities   relationsUtils = null;

// CUI1,AUI1,STYPE1,REL,CUI2,AUI2,STYPE2,RELA,RUI,SRUI,SAB,SL,RG,DIR,SUPPRESS,CVF|16|33454294|3189137935|

public static final int FIELD_CUI1    =  0;
public static final int FIELD_AUI1    =  1;
public static final int FIELD_STYPE1  =  2;
public static final int FIELD_REL1    =  3;
public static final int FIELD_CUI2     = 4;
public static final int FIELD_AUI2    =  5;
public static final int FIELD_STYPE2  =  6;
public static final int FIELD_RELA    =  7;
public static final int FIELD_RUI     =  8;
public static final int FIELD_SRUI    =  9;
public static final int FIELD_SAB     =  10;




  
} // end Class MRREL() ----------------
