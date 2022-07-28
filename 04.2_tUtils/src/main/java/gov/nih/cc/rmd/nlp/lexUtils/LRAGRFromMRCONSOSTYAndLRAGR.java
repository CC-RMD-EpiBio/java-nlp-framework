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
 * LRAGRFromMRCONSOSTYAndLRAGR
 *   builds a .lragr file from umls mrconsosty and specialist's lragr 
 *   file.  It combines both so that each term has a pos and inflection
 *   from lragr.
 *   
 *   Fruitful variants are used to generate additional variants
 *   for entries if those entries don't exist.
 * 
 *  
 *
 * @author     Guy Divita
 * @created    Apr 9, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.lexUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.lookup.LRAGRRow;
import gov.nih.nlm.nls.lvg.Api.LvgCmdApi;

/**
 * @author divitag2
 *
 */
public class LRAGRFromMRCONSOSTYAndLRAGR {

 
  static int unknownEuiCtr = 0;
  private static LvgCmdApi SyntaticUninvertCmdApi;
 
  private static String OutputDir = null;
  private static String UMLS_VERSION = null;
  private static HashMap<String, PrintWriter>  SourceOutHash = null;
  private static String lvgDir = "/some/dir/lvg/lvg2020";

  // =================================================
  /**
   * main [TBD] summary
   * 
   * @param pArgs
  */
  // =================================================
 public static void main(String[] pArgs) {
    
   
   try {
     
     int hashSize = 1438315;
     HashMap<String, List<LRAGRRow>> lragrHash    = new HashMap<String, List<LRAGRRow>>(hashSize );
     HashMap<String, List<LRAGRRow>> lragrEuiHash = new HashMap<String, List<LRAGRRow>>(hashSize );
     
     
     String logDir = U.getOption(pArgs, "--logDir=", "./logs" );
     
    
      lvgDir = U.getOption(pArgs,  "--lvgDir=", "/some/dir/lvg/lvg2020" );
     
     GLog.setLogDir( logDir );
     
     loadLRAGR( pArgs, lragrHash, lragrEuiHash);
     
     processMRCONSOSTY( pArgs, lragrHash, lragrEuiHash );
     
     
     System.err.println(" Dohn");
    
    
   } catch ( Exception e) {
     e.printStackTrace();
     System.err.println( "issue with LRAGRFromMRCONSOSTY: " + e.toString());
   }

  } // end Method main() -----------------------------

  // =================================================
  /**
   * loadLRAGR loads the lragr entries 
   * 
   * @param pArgs
   * @param lragrHash
   * @throws Exception
  */
  // =================================================
 private static final void loadLRAGR(String[] pArgs, 
                                    HashMap<String, List<LRAGRRow>> pLragrHash,
                                    HashMap<String, List<LRAGRRow>> pLragrEuiHash) throws Exception{
   
   
   BufferedReader in = null;
   try {
      
     System.err.println("Read in the lragr file ");
     
      String inputDir = U.getOption(pArgs, "--inputDir=", "./");
      String lragrFile = U.getOption(pArgs,  "--lragrFileName=", "/LEX/LRAGR");
      String fileName = inputDir + "/" + lragrFile;
      in = new BufferedReader( new java.io.FileReader( fileName ));
      
      String row = null;
      while ( ( row = in.readLine()) != null )         
        if ( row != null && row.trim().length() > 0 && !row.startsWith("#"))
          loadLRAGRRow(  pLragrHash, pLragrEuiHash, row);
      
      in.close();
      
      System.err.println("Finished reading in the lragr file ");
     
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with load LRAGR " + e.toString());
      throw e;
    }
   
    
  } // =================================================
  /**
   * loadLRAGRRow reads in an lragr row
   * 
   *   The hash key is key|pos|infl
   * @param pLragrHash
   * @param pLragrEuiHash 
   * @param pRow
  */
  // =================================================
 private static final void loadLRAGRRow(HashMap<String, List<LRAGRRow>> pLragrHash, 
                                        HashMap<String, List<LRAGRRow>> pLragrEuiHash, 
                                        String pRow) {
    
   String [] cols = U.split(pRow);
   String eui = cols[0];
   String key = cols[1] ;
   List<LRAGRRow> values = null;
   LRAGRRow anLRAGR = new LRAGRRow( pRow);
   
   if ((values = pLragrHash.get(key)) == null ) {
     values =  new ArrayList<LRAGRRow>(); 
     values.add( anLRAGR);
     pLragrHash.put(key, values );
   } else {
     values.add(  anLRAGR);
   }
   
   if ((values = pLragrEuiHash.get(eui)) == null ) {
     values =  new ArrayList<LRAGRRow>(); 
     values.add( anLRAGR);
     pLragrEuiHash.put(eui, values );
   } else {
     values.add(  anLRAGR);
   }
   
   
   
   
 } // end Method loadLRAGR() ------------------------

  // =================================================
  /**
   * processMRCONSOSTY 
   * 
   * @param pArgs
   * @param lragrHash
   * @throws Exception
  */
  // =================================================
 private static final void processMRCONSOSTY(String[] pArgs,
                                             HashMap<String, List<LRAGRRow>> pLragrHash,
                                             HashMap<String, List<LRAGRRow>> pLragrEuiHash
                                             ) throws Exception {
   
   BufferedReader in = null;
   PrintWriter    out = null;
   try {
      String inputDir = U.getOption(pArgs, "--inputDir=", "./");
      String mrconsosty = U.getOption(pArgs,  "--mrconsostyFileName=", "META/MRCONSOSTY.RRF");
      UMLS_VERSION = U.getOption(pArgs,  "--UMLS_VERSION=", "2020AA");
      String inputFileName = inputDir + "/" + mrconsosty;
      OutputDir =     inputDir + "/sophia/" + UMLS_VERSION;
      String outputFileName = inputDir + "/mrconsosty" + UMLS_VERSION + ".lragr";
      
      System.err.println("Output going to  " + outputFileName );
      
      in = new BufferedReader( new java.io.FileReader( inputFileName ));
      U.mkDir( OutputDir);
      
      HashMap<String, List<LRAGRRow>> mrconsostyHash = new HashMap<String, List<LRAGRRow>> ( 30);
      String row = null;
      String lastCui = "noKui";
      ArrayList<String> rows = new ArrayList<String>();
      String cui = null;
      int ctr = 0;
      
      System.err.println( "Starting processing the mrconsosty rows " );
      while ( ( row = in.readLine()) != null )         
        if ( row != null && row.trim().length() > 0 && !row.startsWith("#")) {
          
          String rowCols[] = U.split( row);
      
          if ( !filterOutBadMRCONSOSTYStrings( rowCols[14], rowCols[11]  ) ) {
          
          
          cui = rowCols[0];
          if ( cui.contentEquals( lastCui )) {
            rows.add( row );
          } else {
            if ( rows != null && rows.size() > 0 ) 
              processMRCONSOSTYRowsOfAConcept( out, mrconsostyHash, pLragrHash, pLragrEuiHash, rows);
            rows = new ArrayList<String>();
            mrconsostyHash = new HashMap<String, List<LRAGRRow>> ( 30);
            rows.add( row);
          }
          lastCui = cui;
          } // end if this string has not been filtered out
          
          
          // logging 
          if ( ctr++ % 10000 == 0 ) {
            String msg = "Processed " + cui  + " " + ctr + "  mrconsosty rows " ;
            System.err.println( msg );
          }
        } // end loop thru rows of mrconsosty
       
       if ( rows != null && rows.size() > 0 ) 
         processMRCONSOSTYRowsOfAConcept( out, mrconsostyHash, pLragrHash, pLragrEuiHash, rows);
      
      in.close();
      
      // convert the hash into a sorted list (sorted on cuis )
      // print out the sorted list
      System.err.println( "print out the mrconsostylragr hash ");
      printMRCONSOSTYLRAGR( mrconsostyHash );
     
      closeOutputFiles();
      
      System.err.println("Look for the output in " + outputFileName);
    
     
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue loading MRCONSOSTY with lragr " + e.toString());
      throw e;
    }
   } // end Method processMRCONSOSTY() ----------------
 
 

// =================================================
  /**
   * filterOutBadMRCONSOSTYStrings filters out terms like
   *    Extension Namespace {XXXXX}
   *    
   *    History and physical note - recommended C-CDA R1.1 & R2.0 & R2.1 sections:-:Point in time:{Setting}:-
   *    
   *     if { is within a loinc string, don't bother, this term is pre-composed and not string retrievalbe from within text
 *      
 *       Filter out any terms with a [D], [M], [V],  [X] in it - these are deprecated. - Retired procedure [xxx]
 *       (& [ xxxx]) 
 *       
 *       -RETIRED-
   * 
   * @param pKey
   * @param pSAB 
   * @return boolean
  */
  // =================================================
  protected static boolean filterOutBadMRCONSOSTYStrings(String pKey, String pSAB ) {
    
    boolean returnCode = false;
    
    if ( pKey.contains("[D]") ||
         pKey.contains("[M]" ) ||
         pKey.contains("[V]" ) ||
         pKey.contains("[X]" ) ||
         pKey.contains("[EDTA]" ) ||
         pKey.contains("[SO]" ) ||
         pKey.contains("&[x]" ) ||
         pKey.contains("Retired procedure" ) ||
         pKey.toLowerCase().equals("other" ) ||
         pKey.trim().length() == 1 ||
         ( pSAB.contentEquals( "LNC") && isLOINCPreCoordinatedTerm( pKey ) )
         )
           returnCode = true;
         
    
    return returnCode;
  } // end Method filterOutBadMCONOSTYStrings() ----

// =================================================
/**
 * isLoincPreCoordinatedTerm returns true if the term
 * comes from loinc, and has multiple parts to it
 * 
 * Parts are delimited by ":" - if there are more
 * than 1 colons in the term, it's a pre-coordinated
 * term, not meant to be put in prose.
 * 
 * @param pKey
 * @return boolean 
*/
// =================================================
private static boolean isLOINCPreCoordinatedTerm(String pKey ) {
  
  boolean returnVal = false;
  
  String[] cols = U.split(pKey, ":");
  
  if ( cols != null && cols.length > 1 )
    returnVal = true;
  
  return returnVal;
} // end Method isLoincPreCoordinatedTerm() -------

// =================================================
  /**
   * processMRCONSOSTYRowsOfAConcept 
   * 
   * @param pOut
   * @param pMrconsostyHash
   * @param pLragrHash
   * @param rows
   * @throws Exception 
  */
  // =================================================
  private static void processMRCONSOSTYRowsOfAConcept(PrintWriter pOut, 
                                                     HashMap<String, List<LRAGRRow>> pMrconsostyHash,
                                                     HashMap<String, List<LRAGRRow>> pLragrHash, 
                                                     HashMap<String, List<LRAGRRow>> pLragrEuiHash, 
                                                     ArrayList<String> rows) throws Exception {
 
    String snomedSemanticTypes = getSnomedSemanticTypes( rows );
    
    for ( String row : rows )
      processMRCONSOSTYRow(  pMrconsostyHash, pLragrHash, pLragrEuiHash, snomedSemanticTypes,  row) ;
    
    printMRCONSOSTYLRAGR( pMrconsostyHash );
      
  } // end method processMRCONSOSTYRowsOfAConcept() -------------

// =================================================
  /**
   * getSnomedSemanticTypes . Given the set of mrconso rows for a concept, extract all parenthetical 
   * expressions that hold semantic types in the string.
   * 
   * For instance Ticlopidine hydrochloride (substance)
   * 
   * @param pRrconsoRows
   * @return String
  */
  // =================================================
  protected static String getSnomedSemanticTypes( List<String> pRrconsoRows) {
    
    String semanticTypes = null;
    HashSet<String> semanticTypez = new HashSet<String>();
    
    if ( pRrconsoRows != null && !pRrconsoRows.isEmpty() )
      for ( String row: pRrconsoRows ) {
        String aSemanticType = getSnomedSemanticType( row );
        if ( aSemanticType != null )
          semanticTypez.add( aSemanticType);
      }
    String[] semanticArray;
   
    if ( semanticTypez != null && !semanticTypez.isEmpty() ) {
      semanticArray = semanticTypez.toArray( new String[semanticTypez.size()] );
      Arrays.sort( semanticArray);
      StringBuffer stz = new StringBuffer();
     
      for ( String st : semanticArray) {
        if (st != null && st.trim().length() > 0 )
          stz.append(st + ":");
      }
      try {
        if ( stz != null && stz.toString().trim().length() > 1 )
          semanticTypes = stz.substring(0, stz.length()-1);  
      
      }
      catch ( Exception e) {
        e.printStackTrace();
        System.err.println("Issue trying to get the semantic type from semanticType " + semanticTypes + " " + e.toString());
      }
    }
    return semanticTypes;
  } // end Method getSnomedSemanticTypes() -----------

  // =================================================
/**
 * getSnomedSemanticType returns the parenthetical expression from within the mrconso string
 * (field 14)
 * 
 *  also deal with categories that look like 
 *      Abnormalities, Drug-Induced [Disease/Finding]  -> multiple categories, use : as delimiter
 * @param pRow
 * @return String   
*/
// =================================================
private static String getSnomedSemanticType(String pRow) {
  
  String semanticType = null;
  
  
  String cols[] = U.split(pRow);
  String umlsString = cols[14];
  
  int begin_p = umlsString.lastIndexOf("(");
  int end_p = umlsString.lastIndexOf(")");
  
  if ( begin_p > 0 && end_p > 0 && end_p > begin_p)
    semanticType = umlsString.substring(begin_p+1, end_p);
  
  return semanticType;
  
} // end Method getSnomedSemanticType() ---------------

  // =================================================
  /**
   * printMRCONSOSTYLRAGR
   * 
   * @param mrconsostyHash
   * @throws Exception 
  */
  // =================================================
  private static void printMRCONSOSTYLRAGR( HashMap<String, List<LRAGRRow>> mrconsostyHash) throws Exception {
    
    Set<String> cuis = mrconsostyHash.keySet();
    
    String[] kuis = cuis.toArray( new String[cuis.size()] );
    Arrays.parallelSort(kuis);
    
    for ( String cui : kuis) {
      List<LRAGRRow> rows = mrconsostyHash.get( cui);
      printMRCONSOSTYLRAGR( rows );
    }
    
    
  } // end Method printMRCONSOSTYLRAGR() -----------

// =================================================
/**
 * printMRCONSOSTYLRAGR 
 *
 * @param rows
 * @throws Exception 
*/
// =================================================
 private static void printMRCONSOSTYLRAGR( List<LRAGRRow> rows) throws Exception {
 
   PrintWriter out = null;
   for ( LRAGRRow row: rows ) {
     
     String source = row.getTerminology();
     out = getOpenOutputFile( source );
     out.print( row.toLRAGRString() + "\n");
     
     
   }
  
} // end method printMRCONSOSTYLRAGR

// =================================================
/**
 * getOpenOutputFile 
 * 
 * @param pSource
 * @return PrintWriter
 * @throws Exception 
*/
// =================================================
private static PrintWriter getOpenOutputFile(String pSource) throws Exception {
 
  PrintWriter out = null;
  
  if ( SourceOutHash == null ) 
    SourceOutHash = new HashMap<String, PrintWriter>( 90);
  
  String sourceOutputFileName = OutputDir + "/" + "mrconsosty_" + UMLS_VERSION + "_" + pSource + ".lragr";
  out = SourceOutHash.get( sourceOutputFileName);
  
  if ( out == null ) {
    try {
    out = new PrintWriter( sourceOutputFileName );
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue creating output file for " + sourceOutputFileName + " " + e.toString());
      throw e;
    }
    SourceOutHash.put( sourceOutputFileName, out);
  }
  
  return out;
  
} // end Method getOpenOutputFile() ---------------

// =================================================
/**
 * closeOutputFiles closes the output files in the outputfile hash 
 * 
*/
// =================================================
private static void closeOutputFiles() {
  
  PrintWriter out = null;
  
  String[] fileNames = SourceOutHash.keySet().toArray( new String[ SourceOutHash.size()]);
  for ( String fileName : fileNames ) {
    
    out = SourceOutHash.get( fileName );
    if ( out != null ) {
      System.err.println("Closing " + fileName);
      out.close();
    }
  }
  
} // end Method closeOutputFiles() -----------------

// =================================================
 /**
 * processMRCONSOSTYRow
 * @param mrconsostyHash 
 *  
 *   
 * @param pLragrHash
 * @param pLragrEuiHash
 * @param pSNOWMEDSemanticTypes
 * @param pRow
*/
// =================================================
 private static void processMRCONSOSTYRow(HashMap<String, List<LRAGRRow>> mrconsostyHash, 
                                          HashMap<String, List<LRAGRRow>> pLragrHash, 
                                          HashMap<String, List<LRAGRRow>> pLragrEuiHash,
                                          String pSNOWMEDSemanticTypes,
                                          String pRow) {
 
   
   if ( pRow != null && pRow.trim().length() > 0 && !pRow.startsWith("#")) {
     String[] mrconsostyCols = U.split(pRow);
    
     // CUI    ,LAT,TS,LUI,     STT,SUI,     ISPREF,AUI,      SAUI,SCUI,     SDUI,SAB,TTY,CODE     ,STR       ,SRL,SUPPRESS,CVF,  CUI,TUI,STN,STY,ATUI,CVF
    // C0011581|ENG|S |L0011570|PF| S0031644|N     |A28306371|    |LA10576-9|    |LNC|LA |LA10576-9|Depression|0  |N       |256||C0011581|T048|B2.2.1.2.1.1|Mental or Behavioral Dysfunction|AT17565419|2304|
     
     
     try {
       String cui                  = mrconsostyCols[ 0];
       String lang                 = mrconsostyCols[ 1];
       String termStatus           = mrconsostyCols[ 2];
       String lui                  = mrconsostyCols[ 3];
       String stringType           = mrconsostyCols[ 4];
       String sui                  = mrconsostyCols[ 5];
       String isPref               = mrconsostyCols[ 6];
       String aui                  = mrconsostyCols[ 7];
       String saui                 = mrconsostyCols[ 8];
       String scui                 = mrconsostyCols[ 9];
       String sdui                 = mrconsostyCols[10];
       String sab                  = mrconsostyCols[11];
       String termTypeInSource     = mrconsostyCols[12]; //< ----- not what I was looking for - [TBD]
       
      
       String sourceId             = mrconsostyCols[13];
       String key                  = mrconsostyCols[14].toLowerCase();
       String sourceRestrictionLevel = mrconsostyCols[15];         
       String suppress             = mrconsostyCols[16];
       String contentViewFlag      = mrconsostyCols[17];
       String cui2                 = mrconsostyCols[19]; // <------ there was an extra field inserted by the mrconso +|| + mrsty 
       String tui                  = mrconsostyCols[20];
       String semanticTreeNumber   = mrconsostyCols[21];
       String semanticType         = mrconsostyCols[22];
       String atui                 = mrconsostyCols[23];
       String contentViewFlag2     = mrconsostyCols[24];
     
       
       // check to see if the key has any lragr rows - if so, borrow the pos/inflections from each of those rows
       
       // take the exact key and create variants for it
       // strip off parenthetical expressions and create variants for it
       // Syntacticly un-invert and create variants for it
       
       List<LRAGRRow>  lragrRows = pLragrHash.get(key);
       
       processMRCONSOSTYVariants( lragrRows, mrconsostyHash, pLragrHash, pLragrEuiHash, cui, key, semanticType,sab, sourceId, isPref, "n", "0", pSNOWMEDSemanticTypes);
       
       String plainKey = removeParantheticalExpressionsAndNecAndNosAndOther( key,sab, tui);
       
       // ------------------------------
       // Terms that have "brand" in them, should be treated differently - 
       // They should have an extra variant with "brand of " taken out
       
       if ( plainKey.toLowerCase().contains(" brand of ")) { 
         processMRCONSOSTYBrandOfVariants( lragrRows, mrconsostyHash, pLragrHash, pLragrEuiHash, cui, plainKey, semanticType,sab, sourceId, isPref, "p", "1", pSNOWMEDSemanticTypes);
         
       
       
       } else if ( !plainKey.contentEquals(key )) 
         processMRCONSOSTYVariants( lragrRows, mrconsostyHash, pLragrHash, pLragrEuiHash, cui, plainKey, semanticType,sab, sourceId, isPref, "p", "1", pSNOWMEDSemanticTypes);
       
       if ( !isSubstance( plainKey, tui) ) {
         List<String> uninvertedKeys = syntaticUninvert( plainKey);
       
         if ( uninvertedKeys != null && !uninvertedKeys.isEmpty() )
           for ( String uninvertedKey : uninvertedKeys ) 
             if ( !uninvertedKey.contentEquals(key )) 
               processMRCONSOSTYVariants( lragrRows, mrconsostyHash, pLragrHash, pLragrEuiHash, cui, uninvertedKey, semanticType,sab, sourceId, isPref, "S", "1", pSNOWMEDSemanticTypes);
         }
         
       
     } catch (Exception e) {
       e.printStackTrace();
       System.err.println("Cannot parse this Mrconsosty row |" + pRow );
     }
   
     
     
   }
   
} // end Method processMRCONSOSTYRow() ---------------
  

 

  // =================================================
/**
 * processMRCONSOSTYBrandOfVariants creates a variant with "brand of" taken out
 * and a variant that starts after "brand of" - capturing
 * the generic form of the substance.  This might introduce
 * some ambiguity when the branded form and the generic are
 * different cuis.
 * 
 * @param lragrRows
 * @param mrconsostyHash
 * @param pLragrHash
 * @param pLragrEuiHash
 * @param cui
 * @param plainKey
 * @param semanticType
 * @param sab
 * @param sourceId
 * @param isPref
 * @param string
 * @param string2
 * @param pSNOWMEDSemanticTypes
*/
// =================================================

private static void processMRCONSOSTYBrandOfVariants(List<LRAGRRow> lragrRows, HashMap<String, List<LRAGRRow>> mrconsostyHash,
    HashMap<String, List<LRAGRRow>> pLragrHash, HashMap<String, List<LRAGRRow>> pLragrEuiHash, String cui, String plainKey,
    String semanticType, String sab, String sourceId, String isPref, String string, String string2,
    String pSNOWMEDSemanticTypes) {
  
  
  int genericIndex = plainKey.toLowerCase().indexOf(" Brand of ");
  String genericKey = plainKey.substring( genericIndex + " Brand of ".length());
  plainKey = plainKey.replace(" Brand of ", " ");
  plainKey = plainKey.replace(" brand of ", " ");
  processMRCONSOSTYVariants( lragrRows, mrconsostyHash, pLragrHash, pLragrEuiHash, cui, plainKey,  semanticType,sab, sourceId, isPref, "p", "1", pSNOWMEDSemanticTypes);
  processMRCONSOSTYVariants( lragrRows, mrconsostyHash, pLragrHash, pLragrEuiHash, cui, genericKey, semanticType,sab, sourceId, isPref, "p", "1", pSNOWMEDSemanticTypes);
  
} // end Method processMRCONSOSTYBrandOfVariants() ---

  // =================================================
/**
 * syntaticUninvert transforms terms that are xxxx, yyyy into yyyy xxxx,  ie.  pain, acute  -> acute pain
 * 
 * @param plainKey
 * @return List<String>
*/
// =================================================
private static List<String> syntaticUninvert(String pTerm) {
  
  ArrayList<String> returnVal = null;
  try {
    if ( SyntaticUninvertCmdApi == null ) {
      String lvgPropertiesFile = lvgDir + "/data/config/lvg.properties";
      SyntaticUninvertCmdApi = new LvgCmdApi("-f:S -m -n -SC -SI -CR:o", lvgPropertiesFile, null);
    }
   
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println( "Issue with starting lvg's Syntatic uninvert " + e.toString() );
    
  }
  
  String[] variants = null;
  if ( pTerm != null && pTerm.contains(",")) {
  
  try {
 
    if ( pTerm.contains("lenticular,  , per lens, trifocal")) {
      System.err.println("-----------------------------------------------------------------------> |" + pTerm + "|");
      System.exit(1);
    }
    // ------------------------------------
    // Retrieve inflections of the synonyms
    // ------------------------------------
    String variantz = SyntaticUninvertCmdApi.MutateToString(pTerm );
  
    returnVal = new ArrayList<String>(1);
    
    variants = variantz.split("\n");
    for (int z = 0; z < variants.length; z++) {
      String cols[]  = U.split(variants[z]);
      returnVal.add( cols[1]);
    }
    
  } catch (Exception e) {
   // e.printStackTrace() ;
    System.err.println("Syntatic uninvert didn't work well for " + pTerm + " " + e.toString());
  }
  }
  return returnVal;
} // end Method syntaticUninvert() -----------------

// =================================================
/**
 * removeParantheticalExpressionsAndNecAndNosAndOther strips off the string
 * everything from the first ( or [ or {
 * 
 *  x Strip ( but use the category) except if the concept is from the following semantic types:
 *    T109 |T116 | T121 | T126 |T125 |T131 |T123| T127 |T130 | T195 |T114 |T129  - these are chemicals, drugs, proteins ... - there may be a (substance) at the end - strip these off
 * 
 *  { 
 *  {  if it doesn't have : or number
 *  strip off the { } but not the innards if there is nothing on either side i.e. {Graft} -> graft
 *  if { is within a loinc string, don't bother, this term is pre-composed and not string retrievable from within text (should be handled in filter)
 * 
 *  terms that [D], [X],  (& [ xxxx]) should be stopped out - these are deprecated terms - 
 *  terms that just have [someCategory] could be a category, or an attribute of the term (anatomy, or treats, )
 *       ie. Benzoyl peroxide [acne]
 *       or they are acronyms ie. Electro-oculogram [EOG]
 *       
 *        
 *  
 *  terms that have [someAttribute](someCategory) - remove both, but take the category as the category. - except if the Category is [Ambiguous], then take the () as the category
 *  
 *  if there are multiple () and [] - strip them, but leave the text
 *  
 *  [Dsa] -< acronyms
 *  
 *  NDFRT [] at the end of the term like [TC] are categories - See   https://bioportal.bioontology.org/ontologies/NDFRT/?p=classes&conceptid=root
 *     MoA - Cellular or Molecular Interactions
 *     Chemical/Ingredient - duh
 *     PK - Clinical Kinetics 
 *     PE - Physiological Effects
 *     [xxddd] at the beginning of a term are tree or classes - these can be stripped
 *     
 *   <sub> xxxx </sub>   
 *   <sup> xxxx </sup>
 *       >x<
 *       
 *       nec$
 *       nos$
 *       not elsewhere classified$
 *       not otherwise classified$
 *       , other$
 * 
 * @param pKey
 * @return String
*/
// =================================================
protected static String removeParantheticalExpressionsAndNecAndNosAndOther(String pKey, String sab, String tui) {
 
  String returnVal = pKey;
  
  pKey = removeNecAndNos( pKey);
  
  if ( !isSubstance ( pKey, tui )) {
    if      ( pKey.contains( "(") && pKey.contains("[")) returnVal = removeEnclosingPunctuation( pKey );
    else if ( pKey.contains("(") )                       returnVal = removeEnclosingExpression( "(", ")", pKey);
    else if ( pKey.contains("[") )                       returnVal = removeEnclosingExpression( "[", "]", pKey);
    else if ( pKey.contains("{") )                       returnVal = removeEnclosingExpression( "{", "}", pKey);
    else if ( pKey.contains("<") )                       returnVal = removeEnclosingExpression( "<", ">", pKey);
  }
  
  
  
  return returnVal;
} // end Method removeParantheticalExpressions() -----

  // =================================================
/**
 * removeEnclosingPunctuation returns the string without the ( ) or [ ] or {  }
 * 
 * @param pKey
 * @return
*/
// =================================================
private static String removeEnclosingPunctuation(String pKey) {
  String returnVal = pKey;
  
  returnVal = returnVal.replace("(", " ");
  returnVal = returnVal.replace("()", " ");
  returnVal = returnVal.replace("[", " ");
  returnVal = returnVal.replace("]", " ");
  returnVal = returnVal.replace("{", " ");
  returnVal = returnVal.replace("}", " ");
  
  
  return returnVal;
} // end Method removeEnclosingPunction() ------------

  // =================================================
/**
 * isSubstance return true if this term is from one of the substance semantic types
 * 
 *  T109 |T116 | T121 | T126 |T125 |T131 |T123| T127 |T130 | T195 |T114 |T129  - these are chemicals, drugs, proteins ... - there may be a (substance) at the end - strip these off
 * 
 * @param pKey
 * @param pTui
 * @return boolean
*/
// =================================================
private static boolean isSubstance(String pKey, String pTui) {
  boolean returnVal = false;
  
  if (( pTui != null ) &&
     pTui.contentEquals("T109")  ||
     pTui.contentEquals("T114")  || 
     pTui.contentEquals("T116")  || 
     pTui.contentEquals("T121")  || 
     pTui.contentEquals("T123")  || 
     pTui.contentEquals("T125")  ||   
     pTui.contentEquals("T126")  || 
     pTui.contentEquals("T127")  ||
     pTui.contentEquals("T129")  ||
     pTui.contentEquals("T130") )
    returnVal = true;
  
  return returnVal;
} // end Method isSubstance() ----------------------

  // =================================================
/**
 * removeNecAndNos removes 
 *   ,not elsewhere classified, 
 *   ,not otherwise specified,
 *   NEC, NOS, 
 *   NEC and NOS  
 *   NEC/NOS
 *   NEC NOS
 *   NEC/NOS-unspec
 *   NOS/NEC
 *   NEC & NOS
 *   NEC, NOS
 *   in SNOMEDCT
 *   in ICD9CM
 *   , other  (case insensitive)
 * @param pKey
 * @return String 
*/
// =================================================
private static String removeNecAndNos(String pKey) {
  String returnVal = pKey;
  
  returnVal = returnVal.replace(", not elsewhere classified,", " ");
  returnVal = returnVal.replace(" not elsewhere classified", " ");
  returnVal = returnVal.replace(", not otherwise specified,", " ");
  returnVal = returnVal.replace(" not otherwise specified", " ");
  
  returnVal = returnVal.replace("NEC, NOS",  " ");
  returnVal = returnVal.replace("NEC and NOS", " ");
  returnVal = returnVal.replace("NEC/NOS", " ");
  returnVal = returnVal.replace("NEC NOS", " ");
  returnVal = returnVal.replace("NEC/NOS-unspec", " ");
  returnVal = returnVal.replace("NOS/NEC", " ");
  returnVal = returnVal.replace("NEC & NOS", " ");
  returnVal = returnVal.replace("NEC, NOS", " ");
  returnVal = returnVal.replace("in SNOMEDCT", " ");
  returnVal = returnVal.replace("in ICD9CM", " ");

  
  if ( returnVal.toLowerCase().endsWith(", other")) {
    returnVal = returnVal.substring(0, returnVal.length() -7 ); 
    
  }
  return returnVal;
} // end Method removeNecAndNos() ------------------

  // =================================================
  /**
   * removeEnclosingExpression 
   * 
   * @param pOpen
   * @param pClse
   * @param pKey
   * @return String
  */
  // =================================================
  private static String removeEnclosingExpression(String pOpen, String pClose, String pKey) {
    return  removeEnclosingExpression( pOpen, pClose, pKey, 0);
  }

  // =================================================
/**
 * removeEnclosingExpression
 * 
 * @param pOpen
 * @param pClose
 * @param key
 * @return String
*/
// =================================================
private static String removeEnclosingExpression(String pOpen, String pClose, String pKey, int level) {

   String returnVal = pKey;
  
   int _begin = pKey.indexOf( pOpen);
   int _end = pKey.indexOf( pClose, _begin);
   String sectionA = null;
   String sectionB = null;
   
   if ( _begin > 0) {
     sectionA = pKey.substring(0, _begin);
     sectionB = pKey.substring(_end +1);
   }
   returnVal = sectionA + " " + sectionB ;
   if ( level < 4 &&  returnVal != null && returnVal.trim().length() > 1 &&  returnVal.contains(pOpen) && returnVal.contains( pClose))
     returnVal = removeEnclosingExpression( pOpen, pClose, returnVal, level + 1);
   
   // ----------------------------
   // some of these are leaving ,  , after the expression is taken out
   // hunt for it, and replace with only one ,
   returnVal = fixDoubleCommas( returnVal);
   
   
  return returnVal; 
}

  // =================================================
  /**
   * fixDoubleCommas replaces sequences of ,  , with ,
   * 
   * @param returnVal
   * @return String 
  */
  // =================================================
  private static String fixDoubleCommas(String pTerm ) {
  
    String returnVal = pTerm;
     
    returnVal = returnVal.replaceAll(",  ,", ", ");
    returnVal = returnVal.replaceAll(", ,", ", ");
    returnVal = returnVal.replaceAll(",   ,", ", ");
    returnVal = returnVal.replaceAll(",,", ", ");
  
   
  return returnVal;
  } // end Method fixDoubleCommas() ------------------

  // =================================================
/**
 * processXXX
 * 
*/
// =================================================
private static void processMRCONSOSTYVariants( 
                                List<LRAGRRow> lragrRows, 
                                HashMap<String, List<LRAGRRow>> mrconsostyHash,
                                HashMap<String, List<LRAGRRow>> pLragrHash, 
                                HashMap<String, List<LRAGRRow>> pLragrEuiHash,
                                String cui,
                                String key,
                                String semanticType,
                                String sab,
                                String sourceId,
                                String isPref,
                                String pFlowHistory,
                                String pFlowDistance,
                                String pSNOWMEDSemanticTypes
                               ) {
  
  if ( lragrRows != null  && !lragrRows.isEmpty() ) {
    
    String[] euis = getEuis( lragrRows);
    if ( euis != null && euis.length > 0 ) 
      for (String eui: euis )
        // create known variants for this cui from all the lragr variants
        createVariantsFromLRAGR( mrconsostyHash, pLragrEuiHash, eui, cui, key, semanticType, sab,sourceId, isPref, pSNOWMEDSemanticTypes);
    
  } else {
  
   // This is not covered by a direct lragr row - make one up
    String eui = "U" + U.zeroPad(unknownEuiCtr++, 6 );
    LRAGRRow aLragrRow = new LRAGRRow( cui, key, "noun","uncount(thr_sing)",  key,   key,      semanticType, sab, sourceId, pFlowHistory,  pFlowDistance,        isPref, pSNOWMEDSemanticTypes , eui); 
   
    addToMrconsostyHash( mrconsostyHash, aLragrRow, cui, key, semanticType, sab, sourceId, isPref, pSNOWMEDSemanticTypes ); 
    
    
  }
  
}

  // =================================================
/**
 * getEuis retrieves the euis from the list of lragr rows
 * 
 * @param lragrRows
 * @return String[] 
*/
// =================================================
private static String[] getEuis(List<LRAGRRow> lragrRows) {
  
  String[] returnVal = null;
  HashSet<String> euiHash = null;
  
  if ( lragrRows != null && !lragrRows.isEmpty()) {
    
    euiHash = new HashSet<String>( lragrRows.size());
    for ( LRAGRRow row : lragrRows ) {
      String eui = row.getCuis();
      if ( eui != null )
        euiHash.add( eui);
    }
  
    String[] euiArray = euiHash.toArray(new String[euiHash.size()]);
    Arrays.sort( euiArray);
    returnVal = euiArray;
  
  }
  
  return returnVal;
} // end Method getEuis() ----------------------------

  // =================================================
/**
 * createVariantsFromLRAGR will look into the lrgar for 
 * 
 * @param mrconsostyHash
 * @param pLragrHash 
 * @param eui 
 * @param cui
 * @param key
 * @param semanticType
 * @param sab
 * @param sourceId
 * @param isPref
 * @param pSNOWMEDSemanticTypes
*/
// =================================================
private static void createVariantsFromLRAGR(HashMap<String, List<LRAGRRow>> mrconsostyHash, 
                                           HashMap<String, List<LRAGRRow>> pLragrEuiHash, 
                                           String eui, 
                                           String cui, 
                                           String key,
                                           String semanticType,  
                                           String sab, 
                                           String sourceId, 
                                           String isPref,
                                           String pSNOWMEDSemanticTypes) {
  
  
   // get all the rows from lragr with that eui
  
  List<LRAGRRow> rowsForEUI = pLragrEuiHash.get( eui);
  
  if ( rowsForEUI != null && !rowsForEUI.isEmpty() ) 
    // for each lragr row, create a mronsostylragr entry with this form 
    
    for ( LRAGRRow aLragrRow: rowsForEUI ) {
      
   
        String inflKey  = aLragrRow.getName();
        String pos  = aLragrRow.getPOS();
        String infl = aLragrRow.getInflection();
        String uninfl = aLragrRow.getUninflectedForm();
        String citation = aLragrRow.getCitationForm();
        String variantHistory = aLragrRow.getVariationHistory();
        String distance = String.valueOf( aLragrRow.getFlowDistance());
        
        LRAGRRow newMRCONSOSTYLRAGRRow = new LRAGRRow(cui, inflKey, pos,infl, uninfl, citation, semanticType, sab, sourceId, variantHistory, distance, isPref, pSNOWMEDSemanticTypes , eui); 
           
        // add this to to the mrconsosty hash
        List<LRAGRRow> mrconsoRows = mrconsostyHash.get( cui );
        if ( mrconsoRows == null ) {
          mrconsoRows = new ArrayList<LRAGRRow>();
          mrconsostyHash.put(cui, mrconsoRows);
        }
        mrconsoRows.add( newMRCONSOSTYLRAGRRow);
        
        
    } // end loop thru euis
  
  
    
  
  
} // end Method createVariantsFromLRAGR() ------------

  // =================================================
/**
 * addToMrconsostyHash [TBD] summary
 * 
 * @param mrconsostyHash
 * @param aLragrRow
 * @param cui
 * @param key
 * @param semanticType
 * @param sab
 * @param sourceId
 * @param isPref
 * @param pSNOWMEDSemanticTypes
*/
// =================================================
private static void addToMrconsostyHash(HashMap<String, List<LRAGRRow>> mrconsostyHash, 
                                        LRAGRRow aLragrRow, 
                                        String cui, 
                                        String key, 
                                        String semanticType,
                                        String sab, 
                                        String sourceId, 
                                        String isPref,
                                        String pSNOWMEDSemanticTypes) {
  String eui = aLragrRow.getEui();
  String pos  = aLragrRow.getPOS();
  String infl = aLragrRow.getInflection();
  String uninfl = aLragrRow.getUninflectedForm();
  String citation = aLragrRow.getCitationForm();
  String variantHistory = aLragrRow.getVariationHistory();
  String distance = String.valueOf( aLragrRow.getFlowDistance());
  
  LRAGRRow newMRCONSOSTYLRAGRRow = new LRAGRRow(cui, key, pos,infl, uninfl, citation, semanticType, sab, sourceId, variantHistory, distance, isPref, pSNOWMEDSemanticTypes , eui); 
      
  // add this to to the mrconsosty hash
  List<LRAGRRow> mrconsoRows = mrconsostyHash.get( cui );
  if ( mrconsoRows == null ) {
    mrconsoRows = new ArrayList<LRAGRRow>();
    mrconsostyHash.put(cui, mrconsoRows);
  }
  mrconsoRows.add( newMRCONSOSTYLRAGRRow);
  
}

 


  // =================================================
  /**
   * lragrHeader
   * 
   * @param pOut
   * @param pLragrFileName
  */
  // =================================================
 private static String lragrHeader( String pLragrFileName) {
   
   String header = "# -----------------------\n" +
                   "# " + pLragrFileName + " \n" + 
                   "#   cui|key|pos|infl|uninflect|citation|semanticType|sab|" + 
                        "sourceId|hist|dist|preferredTerm|tts|||||\n" +
                   "# ------------------------\n" ;
                   
    return header;                
        
    
  }

}
