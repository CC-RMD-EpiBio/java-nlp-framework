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
 * Expanded Forms From LVG  is a method to generate all kinds of
 * variants from an MRCONSO term.  
 * 
 * LVG is used to to normalize then expand to variants
 * 
 *  normalize flow
 *  
 *  copy the input to the output 
 * 
 * @author  Guy Divita 
 * @created July 13, 2022

 */
// ================================================
package gov.nih.cc.rmd.nlp.lexUtils;


import static org.junit.Assert.assertArrayEquals;

import java.awt.desktop.AboutEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.sql.rowset.BaseRowSet;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;
import gov.nih.nlm.nls.lvg.Api.LvgCmdApi;
import gov.nih.nlm.nls.lvg.Lib.Category;
import gov.nih.nlm.nls.lvg.Lib.Inflection;
import gov.nih.nlm.nls.lvg.Lib.LexItem;


public class ExpandedFormsFromLVG {
  
  // -----------------------------------------
  /**
   * Constructor
   * @param pLvgInstalledDir  this is the root dir for where lvg has been installed.  
   * @throws Exception 
   *
   */
  // -----------------------------------------
  public ExpandedFormsFromLVG( String pLvgInstalledDir ) throws Exception {
    

    
    String lvgPropertiesFile =  pLvgInstalledDir + "/data/config/lvg.properties";
    this.mLvgCmdApi_pipe_OriginalTerms = new LvgCmdApi("-f:Ln -m -n", lvgPropertiesFile);
    this.mLvgCmdApi_pipe_SyntaticUninvert = new LvgCmdApi("-f:S -t:1 -cf:2 -if:3 -m -n", lvgPropertiesFile);
    this.mLvgCmdApi_pipe_Euis = new LvgCmdApi("-f:Ln -t:1 -m -n", lvgPropertiesFile);
    this.mLvgCmdApi_pipe_Inflections = new LvgCmdApi("-f:B -f:An -f:b -f:i -t:1 -cf:2 -if:3 -m -n -SC -SI", lvgPropertiesFile);
    this.mLvgCmdApi_pipe_Derivations_Synonyms_AcronymExpansions_Antinyms = new LvgCmdApi("-f:s -f:d -f:a -f:y -f:z -t:1 -cf:2 -if:3 -m -n -SC -SI", lvgPropertiesFile);
   
  } // end Constructor() ---------------------

  // -----------------------------------------
  /**
   * main 
   * Usage: LexUtils
   *    <input term on stdin>
   * 
   *   The program takes a term, creates variants
   *   for each token of the term, and permutes them
   *   to come up with new term candidates.
   *   
   * @param args
   */
  // -----------------------------------------
  public static void main(String[] pArgs) {
    
    BufferedReader        in = null;
    String         inputTerm = null;
    
   
    String[] args = setArgs( pArgs );
    String lvgDir = U.getOption(args,  "--lvgDir=", "./lvg2022" );
    
    // ------------------------
    // Instantiate the lexUtils
    try {
      ExpandedFormsFromLVG lvgFlowInstance = new ExpandedFormsFromLVG( lvgDir);
    
   
    // -----------------------
    // grab the standard input
    try {
      in = new BufferedReader( new InputStreamReader(System.in));
    } catch ( Exception e) { System.err.println("Not able to grab standard input!"); return; } // System.exit(-1); }

     String cui = "000000";
     String attributes = "Attribute1|Attribute2";
      System.err.print("Input term: ");
      while ( (inputTerm = in.readLine()) != null ) {
        
        String[] cleanedUpVariants = lvgFlowInstance.processTerm( cui, inputTerm, attributes);  // <--------------HERE -----------
      
          if ( cleanedUpVariants != null ){
            int ctr = 0;
            for ( String i: cleanedUpVariants ) {
              System.out.print( ctr + "|" + i + "\n" );
              ctr++;
            } // end loop through output terms
            System.out.flush();
          }
       
         System.err.print("\nInput term: ");
      } // end loop through input terms
      
      // Clean up
      if ( lvgFlowInstance != null )
        lvgFlowInstance.cleanup();
      try {
        in.close();
      }
      catch (IOException e) {e.printStackTrace(); }
    
      
      
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Not able to read from standard input");
      System.exit(-1);
    } // end loop through the standard input;
    
  
    
  } // end Method Main() ---------------------

  
  // =================================================
  /**
   * run [TBD] summary
   * 
   * @param pLvgDir
   * @param pLragrFile
   * @return String  (the full path and fileName)
   * @throws Exception 
  */
  // =================================================
  public final static String run(String pLvgDir, String pLragrFilename) throws Exception {
     
    if ( _expandedFormsFromLVG == null ) {
      _expandedFormsFromLVG = new ExpandedFormsFromLVG( pLvgDir );
    }
    
    String outputFileName = null;
    try {
    
      PrintWriter out = null;
      String aDir = U.getDirFromFileName(pLragrFilename);
      String fileNameOnly = U.getFileNameOnly( pLragrFilename);
    
      outputFileName = aDir + "/" + fileNameOnly + "_variants.lragr";
      
      String[] rows = U.readFileIntoStringArray(pLragrFilename);
      
      out = new PrintWriter ( outputFileName );
      
      String cui = null;
      String form = null;
      String cats = null;
      String infl = null;
      String uninfl = null;
      String citation = null;
      String semanticTypes = null;
      String otherAttributes = null;
      
      if ( rows != null && rows.length > 0 ) 
        for ( String row : rows ) {
          String cols[] = U.split( row );
          cui = cols[0];
          form = cols[1];
          cats = cols[2];
          infl = cols[3];
          uninfl = cols[4];
          citation = cols[5];
          // semanticTypes = cols[6];
          otherAttributes = getOtherColsToRight( cols,6 );
          // this puts a leading pipe on the other attributes - take the leading pipe off
          otherAttributes = otherAttributes.substring(1);
          
         String[] expandedRows =  _expandedFormsFromLVG.processTerm( cui, form, otherAttributes);
         
         if ( expandedRows != null && expandedRows.length > 0 )
           for ( String expandedRow : expandedRows)
             out.print( expandedRow + "\n");
          
          
        }
      
        out.close();
    } catch (Exception e) {
      e.printStackTrace() ;
      GLog.error_println( "ExpandedFormsFromLVG run issue: " + e.toString());
      throw e;
    }
    
    return outputFileName;

  } // end Method run() ------------------------------
    
    
    
   
    
  // =================================================
  /**
   * getOtherColsToRight 
   * 
   * @param pColToStartWith
   * @param i
   * @return String
  */
  // =================================================
  private static final String getOtherColsToRight(String[] cols, int pColToStartWith) {
    
    String returnVal = "";
    if ( cols != null && cols.length > 0 )
      for ( int i = pColToStartWith; i < cols.length; i++)
        returnVal = returnVal + "|" + cols[i];
    
    return returnVal;
  } // end Method getOtherColsToRight() -------------


  // =================================================
  /**
   * processTerm 
   * 
   * @param inputTerm
   * @return
   * @throws Exception 
  */
  // =================================================
  public final String[] processTerm(String pCui, String inputTerm, String pAttributes ) throws Exception {
   
   
      List<String>     orignialTerms = null;
      List<String>     uninvertedTerms = null;
      List<String>     termsWithEuis = null;
      List<String>     inflectedTerms = null;
      List<String>     other_variants = null;
      List<String>    allVariants = null;
      
      allVariants = new ArrayList<String>();
      orignialTerms = this.flowPipe( this.mLvgCmdApi_pipe_OriginalTerms, inputTerm );
      
      // feed these originalTerms as the inputTerms to the next Flow
      uninvertedTerms = this.flowPipe( this.mLvgCmdApi_pipe_SyntaticUninvert, orignialTerms);
      
      // Get the eui's for these terms
      termsWithEuis = this.flowPipe( this.mLvgCmdApi_pipe_Euis, uninvertedTerms );
      
      // Get the inflections for the terms with Euis
      inflectedTerms = this.flowPipe( this.mLvgCmdApi_pipe_Inflections, termsWithEuis );
      
      // get the other variants from terms with euis
      other_variants = this.flowPipe( this.mLvgCmdApi_pipe_Derivations_Synonyms_AcronymExpansions_Antinyms, termsWithEuis );
      
      // -------------------------
      // Put these toghether
      // -------------------------
      allVariants.addAll( orignialTerms);
      allVariants.addAll( termsWithEuis );
      if ( inflectedTerms != null )  allVariants.addAll( inflectedTerms );
      if ( other_variants != null )  allVariants.addAll( other_variants );
      
      // ------------------------
      // transform cats and inflections that are numbers into words
      // ------------------------
      allVariants = bitNumbersToCategories( allVariants );
      
      // ------------------------
      // Unique this to the key|
      // ------------------------
      HashMap<String, HashSet<String>> uniqueVariants = uniqueVariantsToKeyCatInfl( allVariants );
      
      // -------------------------------------------------------------------------
      // Prune keys that are <all> if there are specific category forms available
      // -------------------------------------------------------------------------
      removeAllCategoryVariants ( uniqueVariants );
     
      // -------------------------------------------------------------------------
      //  iterate through the uniqueVariants to create entries that can be turned into an lragr row
      // -------------------------------------------------------------------------
      String[] cleanedUpVariants = iterateThruAllVariants( uniqueVariants );
      
      // -------------------------------------------------------------------------
      //  Add Attributes to each variant (This will be the semantic type and source of the term kind of information
      // -------------------------------------------------------------------------
      String[] variantsWithCuisAndAttributes = addCuisAndAttributes( pCui, pAttributes, cleanedUpVariants);
     
    return variantsWithCuisAndAttributes;

  } // end Method processTerm() ----------------------

  // =================================================
  /**
   * addCuisAndAttributes inserts/appends the attributes after the citation field for each row
   * 
   * @param pCui
   * @param pAttributes
   * @param pVariants
   * @return String[] 
  */
  // =================================================
  private String[] addCuisAndAttributes(String pCui, String pAttributes, String[] pVariants) {
   
    String returnVal[] = new String[ pVariants.length];
    for (int i = 0; i < pVariants.length; i++ ) {
     String cols[] = U.split( pVariants[i]);
     String form = cols[0];
     String cats = cols[1];
     String infl = cols[2];
     String uninfl = cols[3];
     String citation = cols[4];
     String otherAttributes = getOtherColsToRight( cols, 5);
      
      returnVal[i] = pCui + "|" + form + "|" + cats + "|" + infl + "|" + uninfl + "|" + citation + "|" + pAttributes + "|" + otherAttributes;
      
    }
    
    return returnVal;
    
  } // end Method addAttributes() --------------------

  // =================================================
  /**
   * iterateThruAllVariants 
   * 
   * @param allVariants
   * @return String[] 
  */
  // =================================================
  private static final String[] iterateThruAllVariants(HashMap<String, HashSet<String>> allVariants) {
    
    String returnVal[] = null;
    
    Set<String> keys = allVariants.keySet();
    returnVal = new String[ keys.size() ];
    
    int i = 0;
    for ( String key : keys ) {
      HashSet<String> historyCandidates = allVariants.get(key );
      String history = (String) historyCandidates.toArray()[0];
      returnVal[i++] = key  + history;
    }
    
    Arrays.sort( returnVal);
    
    
    return returnVal;
  } // end Method iterateThruAllVariants() -----------

  // =================================================
  /**
   * uniqueVariantsToKeyCatInfl
   * 
   * @param allVariants
   * @return HashMap<String, HashSet<String>>
  */
  // =================================================
  private final static HashMap<String, HashSet<String>> uniqueVariantsToKeyCatInfl(List<String> allVariants) {
    HashMap<String, HashSet<String>> uniqueVariants = new HashMap<String, HashSet<String>>( allVariants.size());
    for ( String variant : allVariants ) {
      String cols[] = U.split(variant);
      String form = cols[0];
      String category = cols[1];
      String inflection = cols[2];
      String attributes = getOtherColsToRight(cols,3);
      String key = form + "|" + category + "|" + inflection ;
      HashSet<String >attributez = uniqueVariants.get( key );
      if ( attributez  == null )
        attributez = new HashSet<String>();
      attributez.add( attributes);
      uniqueVariants.put( key, attributez);
    }
    return uniqueVariants;
  } // end Method uniqueVariantsToKeyCatInfl() -------

  // =================================================
  /**
   * removeAllCategoryVariants removes those variants that
   * are "<all>"   if there are specific categories 
   * for these forms
   * 
   * @param uniqueVariants
   * @return uniqueVariants sans the bad entries 
  */
  // =================================================
   private final static HashMap<String, HashSet<String>> removeAllCategoryVariants(HashMap<String, HashSet<String>> uniqueVariants) {
    
     HashMap<String, HashSet<String>> form_CatInflHash = new HashMap<String, HashSet<String>>( uniqueVariants.size() );
     Set<String> keys = uniqueVariants.keySet();
     
     for ( String key : keys ) {
      String cols[] =  U.split ( key );
      String form = cols[0];
      String cat = cols[1];
      String infl = cols[2];
      
      HashSet<String> catInfls = form_CatInflHash.get( form );
      if ( catInfls == null )
        catInfls = new HashSet<String>();
      catInfls.add( cat + "|" + infl);
      form_CatInflHash.put ( form, catInfls);
      
     }
     
     // now all are sorted by key
     // iterate thru form_CatInflHash 
     Set<String> formKeys = form_CatInflHash.keySet();
     for ( String key : formKeys ) {
      HashSet<String> catInfls = form_CatInflHash.get( key );
      
      // ---------------------
      // ah ha! here is where we can figure out if there is a more specific 
      // entry than <all>
      // ---------------------
      if ( catInfls.size() > 1) {
        Iterator<String> itr = catInfls.iterator();
        while ( itr.hasNext() ) {
          String catInfl = itr.next();
          if ( catInfl.contains("all")) {
            // higgs boson found! remove these from the uniqueVariants because a more specific version exists for this form
            String anAllKey = key + "|" + catInfl; 
            
            // remove this entry from the unique variants hash
            // HashSet<String> badRecord = uniqueVariants.get( anAllKey);
            uniqueVariants.remove ( anAllKey );  // <----------------------------- magic 
          }
        }
      }
     }
 
     return uniqueVariants;
  } // end Method 

  // =================================================
  /**
   * bitNumbersToCategories 
   * 
   * @param allVariants
   * @return List<String>
  */
  // =================================================
  private static List<String> bitNumbersToCategories(List<String> allVariants) {
   
    List<String> returnVal = new ArrayList<String>( allVariants.size());
    for ( String variant : allVariants ) 
      returnVal.add(  bitNumbersToCategories ( variant ) );
   
    return returnVal;
  } // end Method bitNumbersToCategories() -----------
  
  
//=================================================
 /**
  * bitNumbersToCategories 
  * 
  * @param pVaraint
  * @return String
 */
 // =================================================
 private static String bitNumbersToCategories(String pVariant) {
  
   String returnVal = null;
   String cols[] = U.split( pVariant);
   String key = cols[0];
   String cat = cols[1];
   String infl = cols[2];
   String uninfl = cols[3];
   String citation = cols[4];
   String hist = cols[5];
   
   String wordCat = cat ;
   if ( !wordCat.startsWith("<")) {
     long cat_number = Long.parseLong( wordCat);
     wordCat = "<" + Category.ToName( cat_number ) + ">";
   }
   String wordInfl = infl;
   if ( !wordInfl.startsWith("<")) {
    long infl_number = Long.parseLong( infl );
     wordInfl = "<" +  Inflection.ToName(infl_number) + ">";
   }
   returnVal = key + "|" + wordCat + "|" + wordInfl + "|" + uninfl + "|" + citation + "|" + hist;
   
   return returnVal;
 } // end Method bitNumbersToCategories() -----------

  // -----------------------------------------
  /**
   * cleanup closes the open files, sockets, database connections that lvg would have opened.
   * 
   */
  // -----------------------------------------
  public void cleanup() {
    
    
  } // end Method cleanup() ------------------

  // -----------------------------------------
  /**
   * flowPipe runs the pipe on the inputTerm 
 * corpus.
   * @param pInputTermAndAttributes
   * @return List<String>
   * @throws Exception 
   */
  // -----------------------------------------
    private List<String>  flowPipe(LvgCmdApi pPipe, String pInputTerm) throws Exception {
     
    List<String> outputBuffer = new ArrayList<String>( );
       
      try {
        
        if ( pInputTerm != null && pInputTerm.trim().length() > 0 ) {
          
          String history = "";
          String buff = pPipe.MutateToString(pInputTerm);
          String[] variants = buff.split("\n");
          for ( String variant : variants )  {
            
            if ( variant != null && variant.trim().length() > 0 ) { 
            
              // what happens when there is no output? 
            // extract the output variant
             
              String variantColumn = null;
              String category = null;
              String inflection = null;
              String uninfl = null;
              String citation = null;
              String cols[] = U.split( variant );
              
              if ( cols[1] != null && cols[1].contains("-No Output-") ) {
                variantColumn = cols[0];
                inflection = "2097152";
                category = "2047";
                history = "n";
                uninfl = variantColumn;
                citation = uninfl;
                
              } else {
                  
                 variantColumn = cols[1];
                 category = cols[2];
                 inflection = cols[3];
                 uninfl = cols[10];
                 citation = cols[11];
                 history = variant.replace("|",  ":");
              }
             
              outputBuffer.add( variantColumn + "|" + category + "|" + inflection + "|" + uninfl + "|" + citation + "|" + history );
            
            }
          }
        } 
          
        } catch (Exception e) {
        e.printStackTrace();
        throw new Exception( "LvgAPI issue: " + e.toString() );
      }
   
    return outputBuffer;
  } // end Method flowPipe() ------------
    
 // -----------------------------------------
    /**
     * flowPipe runs the pipe on the inputTerm 
   * corpus.
     * @param pInputTermAndAttributes
     * @return List<String>
     * @throws Exception 
     */
    // -----------------------------------------
      private List<String>  flowPipe2(LvgCmdApi pPipe, String pInputTerm) throws Exception {
       
      List<String> outputBuffer = new ArrayList<String>( );
         
      String history = null;
        try {
          
          if ( pInputTerm != null && pInputTerm.trim().length() > 0 ) {
            
           
            String buff = pPipe.MutateToString(pInputTerm);
            String[] variants = buff.split("\n");
            for ( String variant : variants )  {
              
              if ( variant != null && variant.trim().length() > 0 ) { 
              
                // what happens when there is no output? 
              // extract the output variant
               if ( variant.contains("d"))
                 System.err.println( "d");
                
                
                String variantColumn = null;
                String category = null;
                String inflection = null;
                String uninfl = "";
                String citation = "";
                String eui = "";
                String cols[] = U.split( variant );
                
                if ( cols == null || cols[0].contains("null") || cols[1].contains("null") ) 
                  continue ;
                
                if ( cols[0] != null && cols[6].contains("-No Output-") ) {
                  variantColumn = cols[0];
                  inflection = "2097152";
                  category = "2047";
                  history = "n";
                  uninfl = variantColumn;
                  citation = variantColumn;
                  
                } else if ( cols.length == 13 ) {  // uninflection (B) and inflection (i) 
                    
                  
                   variantColumn = cols[6];
                   category = cols[7];
                   inflection = cols[8];
                   history = cols[5] + ":" + cols[9];
                   uninfl = variantColumn;
                   citation = variantColumn;
                   
                   
                } else if ( cols.length == 16 ) {  // eui lookup
                  
                  variantColumn = cols[6];
                  category = cols[7];
                  inflection = cols[8];
                  eui = cols[11];
                  history = cols[9];
                  uninfl = variantColumn;
                  citation = variantColumn;
                 //  history = history + ":" + cols[9];
                  
                } else if ( cols.length == 12 ) {   // inflection and An
                    variantColumn = cols[6];
                    category = cols[7];
                    inflection = cols[8];
                    eui = cols[11];
                    history = cols[9] ;
                    uninfl = variantColumn;
                    citation = variantColumn;
                    
                    
                 } else if ( cols.length >= 17  ) {  // inflection and An
                   variantColumn = cols[6];
                   category = cols[7];
                   inflection = cols[8];
                  //  eui = cols[11];
                   eui = cols[14];
                   history =  cols[9]  + ":" + eui;
                   
                   uninfl = variantColumn;
                   citation = variantColumn;
                   
                } else {
                  System.err.println("here " + cols.length);
                }
              
                if ( variantColumn == null || variantColumn.contains("null"))
                  continue;
                
                if ( U.isNumber( variantColumn ))
                  System.err.println(" shouldn't be here ");
                
                String buff2 =  variantColumn + "|" + category + "|" + inflection + "|" + uninfl + "|" + citation + "|" +  history ;
                
                outputBuffer.add( buff2 );
                System.err.println( "->" + buff2);
              
              }
            }
          } 
            
          } catch (Exception e) {
          e.printStackTrace();
          throw new Exception( "LvgAPI issue: " + e.toString() );
        }
     
      return outputBuffer;
    } // end Method flowPipe2() ------------
  
  // =================================================
  /**
   * flowPipe
   * 
   * @param mLvgCmdApi_pipe22
   * @param outputTerms
   * @return
   * @throws Exception 
  */
  // =================================================
  private List<String> flowPipe(LvgCmdApi pPipe, List<String> pInputTermsAndAttributes) throws Exception {
    List<String> variants = new ArrayList<String>( );
    
    try {
      
      if ( pInputTermsAndAttributes != null && !pInputTermsAndAttributes.isEmpty() ) {
        
        for ( String inputTermAndAttributes : pInputTermsAndAttributes ) {
         
          List<String> someVariants =  flowPipe2( pPipe, inputTermAndAttributes );
          variants.addAll( someVariants);
          
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception( "LvgAPI issue: " + e.toString() );
    }
    return variants;
        
  } // end Method flowPipe() -----------------

     
        
  // -----------------------------------------
  /**
   * uniq
   * 
   * @param pOutputTerms
   * @return
   */
  // -----------------------------------------
  private String[] uniq(List<String> pTerms) {
    
    String[] returnVal = null;
    if ( pTerms != null ) {
    HashMap<String, int[]> map = new HashMap<String,int[]>(pTerms.size()*2);
    int [] dummy = new int[1];
    for ( String entry : pTerms )
      map.put(entry, dummy);
    
    returnVal = new String[ map.size()];
    int i = 0;
    for ( String entry: map.keySet() )
      returnVal[i++] = entry;
        
    Arrays.sort(returnVal);
    }
    return returnVal;
    
  } // end Method uniq () --------------------
  
  
//------------------------------------------
 /**
  * setArgs
  * 
  * 
  * @return
  */
 // ------------------------------------------
 public static String[] setArgs(String pArgs[]) {

   
   String dateStamp = U.getDateStampSimple();
 
   String lvgDir = U.getOption(pArgs, "--lvgDir=", "./umls/lvg2022");
   
   
   
   String version = "2022_07_13_2";
   
   String args[] = {
       "--lvgDir="  + lvgDir,
       "--version=" + version
        };

   if ( Use.version(pArgs, args ) || Use.usageAndExitIfHelp( "ExpandedFormsFromLVG", pArgs, args ) )
     Runtime.getRuntime().exit(0);
 

   return args;

 } // End Method setArgs() -----------------------

  // -----------------------------------------
  // Class Variables
  // -----------------------------------------
   static ExpandedFormsFromLVG _expandedFormsFromLVG = null;
 
  
  LvgCmdApi mLvgCmdApi_pipe_OriginalTerms = null;
  LvgCmdApi mLvgCmdApi_pipe_SyntaticUninvert = null;
  LvgCmdApi mLvgCmdApi_pipe_Euis = null;
  LvgCmdApi mLvgCmdApi_pipe_Inflections = null;
  LvgCmdApi mLvgCmdApi_pipe_Derivations_Synonyms_AcronymExpansions_Antinyms = null;
  
  
} // end Class lexUtils() --------------------
