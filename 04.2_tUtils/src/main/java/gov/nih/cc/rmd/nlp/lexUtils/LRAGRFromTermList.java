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
 * LRAGRFromTermList  
 * 
 *   builds .lragr files from term files f
 *
 * @author     Guy Divita
 * @created    Apr 9, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.lexUtils;

import java.io.File;
import java.io.PrintWriter;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * @author divitag2
 *
 */
public class LRAGRFromTermList {

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
     String semanticType = U.getOption(args,  "--semanticType=", "Imparative");
     
     File aDir = new File( inputDir);
     
     if ( aDir.exists() ) {
       File[] someFiles = aDir.listFiles();
       if ( someFiles != null && someFiles.length > 0 ) 
         
         for ( File aFile : someFiles ) 
           
           if ( aFile != null && aFile.getName().endsWith(".txt" )) 
             makeLRAGRFile( aFile, semanticType);
           
     }    
   } catch ( Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, "issue with LRAGRFromMRCONSOSTY: " + e.toString());
   }

  }

  // =================================================
  /**
   * makeLRAGRFile
   * 
   * @param aFile
   * @param pSemanticType
  */
  // =================================================
 private static void makeLRAGRFile(File aFile, String pSemanticType) {
   
   try {
     String lragrFileName = U.getFileNamePrefix(aFile.getAbsolutePath()) + ".lragr";
     PrintWriter out = new PrintWriter( lragrFileName );
     
     String[] rows = U.readFileIntoStringArray( aFile.getAbsolutePath() ) ;
     out.print( lragrHeader ( lragrFileName ));
     for ( String row: rows ) { 
       if ( row != null )
         out.print( makeLragrRow( row, pSemanticType) );
     }
     out.close();
     
   } catch (Exception e ) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, "issue making an lragr file " + e.toString());
   }
    
 }

  // =================================================
  /**
   * makeLragrRow 
   * 
   * @param pRow
   * @param pSemanticType
   * @return String
  */
  // =================================================
    private static String makeLragrRow(String pRow, String pSemanticType) {

      String returnValString = null;
    StringBuffer returnVal = new StringBuffer();
    if (pRow != null && !pRow.isEmpty() && !pRow.startsWith("#")) {
      

    
      String[] lragrCols = new String[17];
      lragrCols[0] = "F" + U.zeroPad(ctr++, 5);
      lragrCols[1] = pRow.trim().toLowerCase();
      lragrCols[2] = "<noun>"; // <----- look this up in a future itteration
      lragrCols[3] = "<base>";
      lragrCols[4] = pRow.trim();
      lragrCols[5] = pRow.trim();
      lragrCols[6] = pSemanticType;
      lragrCols[7] = "RMD";  //sab
      lragrCols[8] = "";   // source Id
      lragrCols[9] = "n";
      lragrCols[10] = "0";
      lragrCols[11] = "";
      lragrCols[12] = "";
      lragrCols[13] = "";
      lragrCols[14] = ""; // source category
      lragrCols[15] = ""; // tui
      lragrCols[16] = "Finding";  // semantic type

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

  private static int ctr  = 0;
}
