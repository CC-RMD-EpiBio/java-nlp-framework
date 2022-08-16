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
 * LRAGRFromMRCONSOSTY
 * 
 *   builds .lragr files from mrconsosty files for 
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
public class LRAGRFromMRCONSOSTY {

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
     String semanticType = U.getOption(args,  "--semanticType=", "LevelOfEffort");
     String tui = U.getOption(args, "--tui=", "T029");
     String umlsSemanticType = U.getOption(args,  "--umlsSemanticType=", "Body Location or Region");
    
    
     File aDir = new File( inputDir);
     
     if ( aDir.exists() ) {
       File[] someFiles = aDir.listFiles();
       if ( someFiles != null && someFiles.length > 0 ) 
         
         for ( File aFile : someFiles ) 
           
           if ( aFile != null && aFile.getName().endsWith(".mrconsosty" )) 
             makeLRAGRFile( aFile, semanticType);
           else if( aFile != null && aFile.getName().endsWith(".rrf" ))  
             makeLRAGRFile( aFile, semanticType, tui, umlsSemanticType);
             
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
       if ( row != null ) {
         out.print( makeLragrRow( row, pSemanticType) );
       } else {
         
       }
     }
     out.close();
     
   } catch (Exception e ) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, "issue making an lragr file " + e.toString());
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
private static void makeLRAGRFile(File aFile, String pSemanticType, String pTui, String pUMLSSemanticType) {
  
  try {
    String lragrFileName = U.getFileNamePrefix(aFile.getAbsolutePath()) + ".lragr";
    PrintWriter out = new PrintWriter( lragrFileName );
    
    String[] rows = U.readFileIntoStringArray( aFile.getAbsolutePath() ) ;
    out.print( lragrHeader ( lragrFileName ));
    for ( String row: rows ) { 
      if ( row != null ) {
        out.print( makeLragrRow( row, pSemanticType, pTui, pUMLSSemanticType) );
      } else {
        
      }
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
      // # cui |sui|saui|scui|sdui|sab|code |str |srl|tui|sty
      // 0 1 2 3 4 5 6 7 8 9 10
      String[] mrconsostyCols = U.split(pRow);
      if ( mrconsostyCols.length < 8) {
              System.err.println("issue here: " + pRow);
              return returnValString;
      }

      // # cui|key|pos|infl|uninflect|citation|semanticType|sab|
      // # sourceId|hist|dist|preferredTerm|tts|||||
      String[] lragrCols = new String[17];
      lragrCols[0] = mrconsostyCols[0];
      lragrCols[1] = mrconsostyCols[14].toLowerCase();
      lragrCols[2] = "<noun>"; // <----- look this up in a future itteration
      lragrCols[3] = "<base>";
      lragrCols[4] = mrconsostyCols[14];
      lragrCols[5] = mrconsostyCols[14];
      lragrCols[6] = pSemanticType;
      lragrCols[7] = mrconsostyCols[11];  //sab
      lragrCols[8] = mrconsostyCols[13];   // source Id
      lragrCols[9] = "n";
      lragrCols[10] = "0";
      lragrCols[11] = "";
      lragrCols[12] = "";
      lragrCols[13] = "";
      lragrCols[14] = mrconsostyCols[12];   // source category
      lragrCols[15] = mrconsostyCols[19];  // tui
      lragrCols[16] = mrconsostyCols[21];  // semantic type

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
     * makeLragrRow 
     * 
     * @param pRow
     * @param pSemanticType
     * @return String
    */
    // =================================================
      private static String makeLragrRow(String pRow, String pSemanticType, String pTui, String pUMLSSemanticType) {

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
        // # sourceId|hist|dist|preferredTerm|tts|||||
        String[] lragrCols = new String[17];
        lragrCols[0] = mrconsostyCols[0];
        lragrCols[1] = mrconsostyCols[14].toLowerCase();
        lragrCols[2] = "<noun>"; // <----- look this up in a future itteration
        lragrCols[3] = "<base>";
        lragrCols[4] = mrconsostyCols[14];
        lragrCols[5] = mrconsostyCols[14];
        lragrCols[6] = pSemanticType;
        lragrCols[7] = mrconsostyCols[11];  //sab
        lragrCols[8] = mrconsostyCols[13];   // source Id
        lragrCols[9] = "n";
        lragrCols[10] = "0";
        lragrCols[11] = "";
        lragrCols[12] = "";
        lragrCols[13] = "";
        lragrCols[14] = mrconsostyCols[12];   // source category
        lragrCols[15] = pTui; // mrconsostyCols[19];  // tui
        lragrCols[16] = pUMLSSemanticType; // mrconsostyCols[21];  // semantic type

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
