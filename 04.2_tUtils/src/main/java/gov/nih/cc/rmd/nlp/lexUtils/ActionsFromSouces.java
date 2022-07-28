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
 * I accumulated actions and activities from different sources
 * Turn all of these into lragr files
 * 
 *  Where there are duplicates, favor ones that come from the following sources:
 *  snomed 
 *  chv
 *  msh
 *  nci
 *  rmd 
 *
 * @author     Guy Divita
 * @created    Apr 9, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.lexUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * @author divitag2
 *
 */
public class ActionsFromSouces {

  private static int actionCtr = 1;

  // =================================================
  /**
   * main [TBD] summary
   * 
   * @param args
  */
  // =================================================
 public static void main(String[] args) {
    
   
   try {
     
     String inputDir = U.getOption(args, "--inputDir=", "./");
     
     String umlsActivities = inputDir + "/activities.mrconsosty";
     String actionsFromMentions = inputDir + "/actionsFromMentions.txt";      
     String actionsNotInUMLS = inputDir + "/actionsNotInUMLS.txt";    
     
     String rows[] =  makeLRAGRFile( umlsActivities, "Action");
     
     HashMap<String,String>rowHash = makeLRAGRHash( rows);
     addActionsFromMentions(rowHash, actionsFromMentions );
     addActionsFromIntraspection(rowHash, actionsNotInUMLS );
     
     printHash( inputDir, rowHash);
     
     
     System.err.println("Dohn");
           
        
   } catch ( Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, "issue with LRAGRFromMRCONSOSTY: " + e.toString());
   }

  }

  // =================================================
  /**
   * printHash
   * 
   * @param inputDir
   * @param rowHash
   * @throws Exception 
  */
  // =================================================
   private static void printHash(String inputDir, HashMap<String, String> rowHash) throws Exception {
   
    PrintWriter out = null;
    try {
      out = new PrintWriter( inputDir + "/Actions.lragr");
      out.print( lragrHeader( "Actions.lragr" ));
      
      Set<String> keys = rowHash.keySet();
      String[] sortedKeys = keys.toArray(new String[keys.size()]);
      Arrays.sort( sortedKeys);
      for ( String key: sortedKeys) 
        out.print(  rowHash.get( key ) );
      
      out.close();
      
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception();
    }
     
  } // end Method printHash() ------------------

  // =================================================
  /**
   * addActionsFromMentions 
   *   has the format term | ICF ID
   *   some of these are quoted - strip the quotes
   *   some of these have " as inches in them as "" - make a variant of these ""
   * 
   * @param rowHash
   * @param actionsFromMentions
   * @throws Exception 
  */
  // =================================================
   private static void addActionsFromMentions(HashMap<String, String> rowHash, String actionsFromMentions) throws Exception {
    
     
     try {
     
     String rows[] = U.readFileIntoStringArray(actionsFromMentions);
     
     
     if ( rows != null && rows.length > 0 ) {
       for ( String row: rows ) {
         if ( !row.isEmpty() && !row.startsWith("#")) {
           String cols[] = U.split( row);
           String key = cols[1];
           if ( rowHash.get(key.toLowerCase()) == null  ) {
             rowHash.put( key.toLowerCase(), makeLragrRowFromKey ( key, "RMD",  "Action" ));
           }
         }
       }
     }
    
    
     
     } catch (Exception e) {
       e.printStackTrace();
       throw new Exception();
     }
  } // end Method addActionsFromMentions() -----------
   

   // =================================================
   /**
    * addActionsFromIntraspection
    *   has the format term | ICF ID
    *   some of these are quoted - strip the quotes
    *   some of these have " as inches in them as "" - make a variant of these ""
    * 
    * @param rowHash
    * @param pFileName
    * @throws Exception 
   */
   // =================================================
    private static void addActionsFromIntraspection(HashMap<String, String> rowHash, String pFileName) throws Exception {
     
      
      try {
      
      String rows[] = U.readFileIntoStringArray(pFileName);
      
      
      if ( rows != null && rows.length > 0 ) {
        for ( String row: rows ) {
          if ( !row.isEmpty() && !row.startsWith("#")) {
            String key = row.trim().toLowerCase();
            if ( rowHash.get(key)  == null  ) 
              rowHash.put( key , makeLragrRowFromKey ( key, "RMD",  "Action" ));
            
          }
        }
      }
     
     
      
      } catch (Exception e) {
        e.printStackTrace();
        throw new Exception();
      }
   } // end Method addActionsFromMentions() -----------

  // =================================================
  /**
   * makeLragrRowFromKey 
   * 
   * @param key
   * @param sab
   * @param semanticType
   * @return String
  */
  // =================================================
  private static String makeLragrRowFromKey(String key, String sab, String semanticType) {
    
    String row = null;
    StringBuffer returnVal = new StringBuffer();
    String[] lragrCols = new String[17];
    lragrCols[0] = "A" + U.zeroPad(actionCtr++, 7);
    lragrCols[1] = key.toLowerCase();
    lragrCols[2] = "<noun>"; // <----- look this up in a future itteration
    lragrCols[3] = "<base>";
    lragrCols[4] = key.toLowerCase();
    lragrCols[5] = key.toLowerCase();
    lragrCols[6] = semanticType;
    lragrCols[7] = sab;
    lragrCols[8] = lragrCols[0];
    lragrCols[9] = "n";
    lragrCols[10] = "0";
    lragrCols[11] = "";
    lragrCols[12] = "";
    lragrCols[13] = "";
    lragrCols[14] = "";
    lragrCols[15] = "";
    lragrCols[16] = "";
    
    // # cui|key|pos|infl|uninflect|citation|semanticType|sab|
    // # sourceId|hist|dist|preferredTerm|tts|||||

    for (int i = 0; i < lragrCols.length; i++) {
      returnVal.append(lragrCols[i]);
      if (i < lragrCols.length - 1) 
        returnVal.append('|');
      else
        returnVal.append("\n");
      
    }

    row = returnVal.toString();
   
    return row;
    
  } // end Method makeLragrRowFromKey() --------------

  // =================================================
  /**
   * makeLRAGRHash
   * 
   * @param rows
   * @return HashMap<String,String>
  */
  // =================================================
   private static HashMap<String, String> makeLRAGRHash(String[] rows) {
   
     HashMap<String, String> map = new HashMap<String,String>(rows.length);
     
     for ( String row: rows ) {
       String cols[] = U.split( row );
       if ( cols != null && cols.length > 1) {
         String key = cols[1].toLowerCase();
         addTohash( map, key, row);
         
       }
     }
    return map;
  } // end Method makeLRAGRHash() ------------------

  // =================================================
  /**
   * addTohash 
   * @param map
   * @param key
   * @param row
  */
  // =================================================
  private static void addTohash(HashMap<String, String> map, String key, String row) {
   
    if ( map.get(key) == null ) 
      map.put( key, row);
    
  } // end Method addToHash() ---------------------

  // =================================================
  /**
   * makeLRAGRFile
   * 
   * @param aFile
   * @param semanticType
  */
  // =================================================
 private static String[] makeLRAGRFile(String pFileName, String semanticType) {
   
   String[] returnVal = null;
   try {
     
     
     String[] rows = U.readFileIntoStringArray( pFileName ) ;
     ArrayList<String> returnRows = new ArrayList<String>(rows.length);
     for ( String row: rows ) { 
       if ( row != null && !row.startsWith("#"))
          returnRows.add( makeLragrRow( row, semanticType) ); 
     }
    returnVal = returnRows.toArray(new String[returnRows.size()] );
     
   } catch (Exception e ) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, "issue making an lragr file " + e.toString());
   }
    
   return returnVal;
 }

  // =================================================
  /**
   * makeLragrRow 
   * 
   * @param pRow
   * @return String
  */
  // =================================================
    private static String makeLragrRow(String pRow, String semanticType) {

      String returnValString = null;
    StringBuffer returnVal = new StringBuffer();
    if (pRow != null && !pRow.isEmpty() && !pRow.startsWith("#")) {
      // # cui |sui|saui|scui|sdui|sab|code |str |srl|tui|sty
      // 0 1 2 3 4 5 6 7 8 9 10
      String[] mrconsostyCols = U.split(pRow);
      if ( mrconsostyCols.length < 8) {
              System.err.println("issue here: " + pRow);
              return returnValString;
      }

      // # cui|key|pos|infl|uninflect|citation|semanticType|sab|
      // # sourceId|hist|dist|preferredTerm|tts||||sty
      String[] lragrCols = new String[18];
      lragrCols[0] = mrconsostyCols[0];
      lragrCols[1] = mrconsostyCols[7].toLowerCase();
      lragrCols[2] = "<noun>"; // <----- look this up in a future itteration
      lragrCols[3] = "<base>";
      lragrCols[4] = mrconsostyCols[7].toLowerCase();
      lragrCols[5] = mrconsostyCols[7].toLowerCase();
      lragrCols[6] = semanticType;
      lragrCols[7] = mrconsostyCols[5];
      lragrCols[8] = mrconsostyCols[3];
      lragrCols[9] = "n";
      lragrCols[10] = "0";
      lragrCols[11] = "";
      lragrCols[12] = "";
      lragrCols[13] = "";
      lragrCols[14] = "";
      lragrCols[15] = mrconsostyCols[9];
      lragrCols[16] = mrconsostyCols[10];
      lragrCols[17] = mrconsostyCols[6];

      for (int i = 0; i < lragrCols.length; i++) {
        returnVal.append(lragrCols[i]);
        if (i < lragrCols.length - 1) returnVal.append('|');
        else
          returnVal.append('\n');
      }

    } else if (!pRow.isEmpty() && pRow.startsWith("#")) {
      returnVal.append(pRow);
    } else {
      returnVal = null;
    }
    
    if ( returnVal != null )
      returnValString = returnVal.toString();
    
    return  returnValString;
  } // end Method makeLragrRow() ---------------------

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
