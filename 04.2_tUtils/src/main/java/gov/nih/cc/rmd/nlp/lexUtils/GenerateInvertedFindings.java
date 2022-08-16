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
 * GenerateInvertedFindings generates variants
 * that are x is y  from terms that are y x
 * where y is a qualitative value and x is an observable
 * entity and y x is a finding.
 * 
 * or where y x = a disorder
 * also where y = morphologic abnormality 
 * and
 *      where x = body structure
 *      
 *  match y or adj variant form y-> al or y -> ic
 * 
 * 
 * 
 *  and 
 * 
 *
 * @author     Guy Divita
 * @created    Apr 9, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.lexUtils;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.lookup.LRAGRRow;
import gov.nih.nlm.nls.lvg.Api.LvgCmdApi;

/**
 * @author divitag2
 *
 */
public class GenerateInvertedFindings extends LRAGRFromMRCONSOSTYAndLRAGR {


  
  private static LvgCmdApi derivationsCmdApi;




  // =================================================
  /**
   * main [TBD] summary
   * 
   * @param pArgs
  */
  // =================================================
 public static void main(String[] pArgs) {
    
   
   PrintWriter out = null;
   try {
  
     
     String[] args = setArgs( pArgs);
     GLog.set( args );
     String    outputDir = U.getOption(pArgs, "--outputDir=", "./");
     
     
     out = new PrintWriter( outputDir + "/invertedFindings.lragr" );
     
     
     
     List<String >observableEntities =  new ArrayList<String>();
     List<String> qualitativeValues = new ArrayList<String>();
     HashMap<String, List<String>> findings = new HashMap<String, List<String>>();
     
     loadHashes( args, findings, observableEntities, qualitativeValues );
     
     // Loop through the findings looking to decompose them into observable entities and a qualifier
     
     for ( String qualifierRow : qualitativeValues  ) {
       String[] cols = U.split( qualifierRow);
       String qualifier = cols[14];
       for ( String observableEntityRow : observableEntities ) {
         String[] cols2 = U.split(observableEntityRow);
         String observableEntity = cols2[14];
         String variant1 = (qualifier + " " + observableEntity).toLowerCase();
         String variant2 =  (observableEntity + " " + qualifier).toLowerCase() ;
         System.err.println( variant1 );
         System.err.println( variant2 );
         
         if ( variant1.contains("difficulty walking") )
           System.err.println(" -> " + variant1);
         
         if ( variant2.contains("difficulty walking") )
           System.err.println(" -> " + variant1);
         List<String> row = null;
         if ( ( row = findings.get( variant1 ))  != null  || (row = findings.get( variant2 ))!= null ) {
           // row is the finding mrconso row that is the combination of qualifier value + observable entity 
           // generate an observable entity is qualifier value
           // generate an observable entity was qualifier value
           addVariants( out, observableEntity, qualifier, row );
          
           
         } else {
           List<String >derivations = getDerivationsFor( qualifier );
           if ( derivations != null && !derivations.isEmpty())
             for (String derivation : derivations ) {
               String variant3 = derivation + " " + observableEntity ; 
               String variant4 =  observableEntity + derivation;
           
               if ( ( row = findings.get( variant3 ))  != null  || (row = findings.get( variant4 ))!= null ) {
                 // row is the finding mrconso row that is the combination of qualifier value + observable entity 
                 // generate an observable entity is qualifier value
                 // generate an observable entity was qualifier value
                 addVariants( out, derivation, qualifier, row );
               }
           
             }
         
         }
         
       }
     }
     
     out.close();
     GLog.println(" Dohn");
    
    
   } catch ( Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, "issue with LRAGRFromMRCONSOSTY: " + e.toString());
   }

  } // =================================================
 
  /**
   * addVariants creates new lragr rows for the variants
   * 
   * @param out
   * @param observableEntity
   * @param qualifier
   * @param pRows   these are mrconsosty rows
  */
  // =================================================
 private static void addVariants(PrintWriter pOut, String pObservableEntity, String pQualifier, List<String> pRows) {
   
    String generated1 = (pObservableEntity + " is " +  pQualifier).toLowerCase();
    String generated2 = (pObservableEntity + " was " + pQualifier).toLowerCase();
    
    for ( String row : pRows ) {
      addGeneratedVariant(pOut, generated1, row , "<present>");
      addGeneratedVariant(pOut, generated2, row , "<past>");
    }
    
  } // end Method addVariants() ----------------------
 
 // =================================================
  /**
   * addGeneratedVariant 
   * 
   * @param pOut   
   * @param pVariant
   * @param pRow         mrconsosty row
   * @param pInflection
   * 
   *  cui|key|
  */
  // =================================================
 private static void addGeneratedVariant(PrintWriter pOut, String pVariant, String pRow, String pInflection) {
  
   String[] mrconsostyCols = U.split(pRow);
  
   
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
   String uninflection         = mrconsostyCols[14].toLowerCase();
   String sourceRestrictionLevel = mrconsostyCols[15];         
   String suppress             = mrconsostyCols[16];
   String contentViewFlag      = mrconsostyCols[17];
   String cui2                 = mrconsostyCols[18];
   String tui                  = mrconsostyCols[19];
   String semanticTreeNumber   = mrconsostyCols[20];
   String semanticType         = mrconsostyCols[21];
   String atui                 = mrconsostyCols[22];
   String contentViewFlag2     = mrconsostyCols[23];
   
   String snomedSemanticType = "finding"; 
   String flowHistory = "S";  
   String flowDistance = "1";
   String eui = "U" + U.zeroPad(unknownEuiCtr++, 6 );
   
   LRAGRRow aLragrRow = new LRAGRRow( cui, pVariant, "<verb>", pInflection,  uninflection,   uninflection,      semanticType, sab, sourceId, flowHistory,  flowDistance,        isPref, snomedSemanticType , eui); 
   
   pOut.print( aLragrRow.toLRAGRString() + "\n");
   
   
  } // end Method addGeneratedVariant () -------------
 
 
 
  // =================================================
  /**
   * loadHashes loads the hashes from the mrconso file
   * 
   * @param pArgs
   * @param findings
   * @param observableEntities
   * @param qualitativeValues
   * @throws Exception 
  */
  // =================================================
 private static void loadHashes(String[] pArgs, HashMap<String, List<String>> findings, List<String> observableEntities, List<String> qualitativeValues) throws Exception {
   
   BufferedReader in = null;
 
   try {
      String inputDir = U.getOption(pArgs, "--inputDir=", "./");
      String mrconsosty = U.getOption(pArgs,  "--mrconsostyFileName=", "META/mrconsosty.txt");
      String inputFileName = inputDir + "/" + mrconsosty;
      String outputFileName = inputDir + "/mrconsostylragr.txt";
      
      GLog.println("Output going to  " + outputFileName );
      
      in = new BufferedReader( new java.io.FileReader( inputFileName ));
     
      String row = null;
      String lastCui = "noKui";
      ArrayList<String> rows = new ArrayList<String>();
      String cui = null;
      int ctr = 0;
      
      GLog.println( "Starting processing the mrconsosty rows " );
      while ( ( row = in.readLine()) != null )         
        if ( row != null && row.trim().length() > 0 && !row.startsWith("#")) {
          
          String rowCols[] = U.split( row);
      
          if ( !filterOutBadMRCONSOSTYStrings( rowCols[14], rowCols[11]  ) ) {
          
          
          cui = rowCols[0];
          if ( cui.contentEquals( lastCui )) {
            rows.add( row );
          } else {
            if ( rows != null && rows.size() > 0 ) 
              processMRCONSOSTYRowsOfAConcept(  findings, observableEntities, qualitativeValues, rows);
            rows = new ArrayList<String>();
            rows.add( row);
          }
          lastCui = cui;
          } // end if this string has not been filtered out
          
          
          // logging 
          if ( ctr++ % 10 == 0 ) {
            String msg = "Processed " + cui  + " " + ctr + "  mrconsosty rows " ;
            GLog.println( msg );
          }
        } // end loop thru rows of mrconsosty
       
       if ( rows != null && rows.size() > 0 ) 
         processMRCONSOSTYRowsOfAConcept(  findings, observableEntities, qualitativeValues, rows);
      
      in.close();
      
   
     
    } catch (Exception e) {
      e.printStackTrace();
      GLog.error_println("Issue loading MRCONSOSTY with lragr " + e.toString());
      throw e;
    }
  
    
  } // end Method loadHashes() ---------------------

  
  

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
   * @param pFindings
   * @param pObservableEntities
   * @param pQualitativeValues
   * @param pRows
  
  */
  // =================================================
  private static void processMRCONSOSTYRowsOfAConcept(HashMap<String, List<String>> pFindings,
                                                      List<String>                  pObservableEntities,
                                                      List<String>                  pQualitativeValues,
                                                      List<String>                  pRows) {
 
    String snomedSemanticTypes = getSnomedSemanticTypes( pRows );
    
    String[] semanticTypez = U.split( snomedSemanticTypes, ":" );
    
    for ( String row : pRows ) {
      
      
      if ( semanticTypez != null && semanticTypez.length > 0 )
        for ( String semanticType : semanticTypez ) {
        
        switch ( semanticType ) {
          case "finding" :        
            addFinding( pFindings, row );  break;
          case "observable entity" : 
            pObservableEntities.add( row); break;
          case "qualifier value" : 
            pQualitativeValues.add( row ); break;
          default : ;;
          
        }
      }
    } 
    
      
  } // end Method processMRCONSOSTYRowsOfAConcept() ----
  
  // =================================================
/**
 * addFinding adds rows to 
 * 
 * @param pFindings
 * @param pRow
*/
// =================================================
 private static void addFinding(HashMap<String, List<String>> pFindings, String pRow) {
  
   if ( pRow != null ) {
    String cols[] =  U.split(pRow);
    
    String key = cols[14].toLowerCase();
    String sab = cols[11];
    String tui = cols[19];
    key = removeParantheticalExpressionsAndNecAndNosAndOther(key,  sab,  tui);
  
    
    List<String> rows = pFindings.get( key );
    if ( rows == null ) {
      rows = new ArrayList<String> ();
      pFindings.put( key, rows);
    }
    rows.add( pRow);
    
   }
  

 } // end method processMRCONSOSTYRowsOfAConcept() -------------

 
  // =================================================
/**
 * getDerivationsFor creates derivations for an incoming term
 * 
 * 
 * @param plainKey
 * @return List<String>
*/
// =================================================
private static List<String> getDerivationsFor(String pTerm) {
  
  ArrayList<String> returnVal = null;
  try {
    if ( derivationsCmdApi == null )
      derivationsCmdApi = new LvgCmdApi("-f:d -kdt:S -m -n -SC -SI -CR:o");
   
  } catch (Exception e) {
    e.printStackTrace();
    GLog.error_println( "Issue with starting lvg's Syntatic uninvert " + e.toString() );
    
  }
  
  String[] variants = null;
  if ( pTerm != null && pTerm.contains(",")) {
  
  try {
 
    // ------------------------------------
    // Retrieve derivations of the term 
    // ------------------------------------
    String variantz = derivationsCmdApi.MutateToString(pTerm);
  
    returnVal = new ArrayList<String>(1);
    
    variants = variantz.split("\n");
    for (int z = 0; z < variants.length; z++) {
      String cols[]  = U.split(variants[z]);
      returnVal.add( cols[1]);
    }
    
  } catch (Exception e) {
   // e.printStackTrace() ;
    GLog.error_println("Syntatic uninvert didn't work well for " + pTerm + " " + e.toString());
  }
  }
  return returnVal;
} // end Method syntaticUninvert() -----------------


// ------------------------------------------
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

  String    inputDir  = U.getOption(pArgs, "--inputDir=", "/data/input/");
  String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_variants_" + dateStamp);
  String       logDir = U.getOption(pArgs, "--logDir=",   outputDir + "/logs" ); 
  String   printToLog = U.getOption(pArgs, "--printToLog=", "true");
  String mrconsosty = U.getOption(pArgs,  "--mrconsostyFileName=", "META/mrconsosty.txt");
  

  String args[] = {
      
      "--inputDir=" + inputDir,
      "--outputDir=" + outputDir,
      "--logDir=" + logDir,
      "--printToLog=" + printToLog,
      "--mrconsostyFileName=" + mrconsosty
      
  };


  return args;

}  // End Method setArgs() -----------------------


} // end Class GenerateInvertedFinding() ----------
