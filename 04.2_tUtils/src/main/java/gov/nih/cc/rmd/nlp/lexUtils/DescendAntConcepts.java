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
 * DescendAntConcepts
 * reads in and indexes the relationship table
 * This resource indexes on aui2 -> aui1|inverse-relationship|sab
 *                           cui -> hrhier  <---- do I need this?
 * 
 * MRREL.RRF|Related Concepts|CUI1,AUI1,STYPE1,REL,CUI2,AUI2,STYPE2,RELA,RUI,SRUI,SAB,SL,RG,DIR,SUPPRESS,CVF|16|30797014|2811737031|

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

import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

/**
 * @author divitag2
 *
 */
public class DescendAntConcepts {

  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws Exception 
   * 
  **/
  // =================================================
  public DescendAntConcepts() throws Exception {
  
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
public DescendAntConcepts(String[] pArgs) throws Exception {
  
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
  
  try {
      this.meter = new PerformanceMeter(System.err);
      this.meter.begin("Starting the MRREL process");
         inputDir = U.getOption(pArgs,  "--inputDir=", "./2019AA/META");
      MRRELFile = U.getOption( pArgs, "--MRREL=", inputDir + "/MRREL.RRF");
      this.totalNumberOfMRRELRows = Integer.parseInt(U.getOption(pArgs,"--totalNumberOfMRRELRows=", "10"));
      this.rowStore = new char[this.totalNumberOfMRRELRows][];
      
      read( MRRELFile );
      
      System.err.println("Finished initializing MRREL ");
      
    
      
      MRCONSO mrconso = new MRCONSO ( pArgs );
      this.setMRCONSO( mrconso);
  
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println("Issue trying to instantiate MRREL from " +  MRRELFile + " " + e.toString());
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


// =================================================
/**
 * getDescendents retrieves all the descendant auis of this aui
 * 
 * This method should return a unique, ordered set, but guard against
 * cycles. 
 * 
 *   2445510 CHD|
 *     19436 CHD|part_of
 *     18512 CHD|has_parent
 *      5327 CHD|branch_of
 *      3631 CHD|member_of
 *      1659 CHD|tributary_of
 * 
 * @param pAuiList
 * @param pAui
 * @param pCui1
 * @return boolean ( if a cycle or no more auis' can be found)
*/
// =================================================
public final boolean getDescendantAuis(List<char[]> pAuiList, List<String> logicList, String pAui, String pCui1) {
 
  
  boolean done = false;
  String decendentAui = pAui;
  String decendentCui = pCui1;
  
  while ( !done ) {
    
   
    List<char[]> rows = this.auiIndex.get( decendentAui ); 
    // rows coming back are in the format:
    // aui|relationship|sab|cui
    
    if ( rows != null && rows.size() > 0 ) {
     
   
      for ( char[] row : rows ) {
        // for each aui2, look the descendents up for these
        
       
        String row2 = new String( row );
        String cols[] = U.split( row2 );
        
        char[] aui2 = cols[0].toCharArray();
        String cui2 = cols[3].trim();
        String relationship = cols[1];
    //    String relationshipAttribute = cols[7];
        
        String sab = cols[2];
        
        if ( relationship.contains("CHD") || relationship.contains("RN")) {
         String decendentConcept = mrconso.getPreferredConceptName(  decendentCui) ;
         String concept2 = mrconso.getPreferredConceptName(  cui2 );
        
         // -----------------------------------
         // looking for a chain of logic here
         //  aui1|cui1ConceptName|relationship|aui2|cui2|conceptName|sab
         String newRow = concept2 + "|" + cols[0] + "|" + cui2 + "|" +  relationship + "|" +   decendentAui+ "|" + decendentCui + "|" + decendentConcept + "|" + sab ;
        
        
        
          addToAuiList( pAuiList, aui2);
          if (! cui2.contentEquals(  decendentCui))
            logicList.add( newRow);
         decendentAui = new String( aui2);
         decendentCui = cui2;
         done = getDescendantAuis( pAuiList,  logicList, decendentAui,  decendentCui); 
        } // end if this is a child 
      }  // end loop through rows
    } // end if there are any rows
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

// =================================================
/**
 * getDescendents will return mrconso rows for the
 * decendents of this aui
 * 
 * 
 * @param aAui
 * @param pCui
 * @param decendentLogic 
 * @return List<char[] > of mrconso rows
*/
// =================================================
public final List<String> getDescendents(String aAui, String pCui, List<String> decendentLogic) {
  
  List<String> decendentMRCONSORows = null;
  List<char[]>  decendentAuis = new ArrayList<char[]>();
 
  
  try {
    getDescendantAuis(decendentAuis, decendentLogic, aAui, pCui ) ;
    List<String>decendentCuis = getDecendentCuis(decendentAuis );
    decendentMRCONSORows = mrconso.getMRCONSORows(decendentCuis );

   
  } catch ( Exception e ) {
    e.printStackTrace();
    System.err.println("Issue getting decendents " + e.toString());
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
 * read 
 * 
 * @param MRRELFile
*/
// =================================================
private final void read(String MRRELFile) {
  
  
  BufferedReader in = null;
  try {
    
    in = new BufferedReader(  new InputStreamReader( new FileInputStream(MRRELFile), "UTF8"));
             
     String row = null;
     int ctr = 1;     
     while ((row = in.readLine()) != null) {
         
       if ( row != null && row.trim().length() > 0 && !row.startsWith("#")) {
         index( row);
         if ( ctr++ % 1000000 == 0 ) 
           this.meter.mark( " Processed " + ctr + " mrrel records" );
         
       }
     } // end Loop through the input file
             
     in.close();
    
    
    
  } catch ( Exception e) {
    e.printStackTrace();
    System.err.println( "Issue reading the MRREL file " + MRRELFile + " " + e.toString() );
  }
  
} // end Method read() -----------------------------


// =================================================
/**
 * index
 *  creates indexes for aui2 -> aui1|inverse-relationship|sab
 * 
 * MRREL.RRF|Related Concepts|CUI1,AUI1,STYPE1,REL,CUI2,AUI2,STYPE2,RELA,RUI,SRUI,SAB,SL,RG,DIR,SUPPRESS,CVF|16|30797014|2811737031|
 * @param pRow
*/
// =================================================
private final void index(String pRow) {

 

 
 String[] cols = U.split( pRow);

 String aui1 = cols[ FIELD_AUI1 ];
 String aui2 = cols[ FIELD_AUI2 ];
 String  sab = cols[ FIELD_SAB ];
 String  cui = cols[ FIELD_CUI2];
 String relationship = cols[ FIELD_REL];
 String relationshipAttribute = cols[FIELD_REL_Attribute ];
 String betterRelationship = relationship;
 
 if ( relationshipAttribute != null && relationshipAttribute.trim().length() > 0 && !relationship.contentEquals("SY") )
   betterRelationship = relationshipAttribute;
 
 index ( aui1, aui2, betterRelationship, sab, cui);
 /*
 if ( hierarchicalRelationship( betterRelationship , relationshipAttribute)) {
  
    this.mrrelI++;
 } else {
   if (!betterRelationship.contentEquals("PAR") && 
       !betterRelationship.contentEquals("RB")  &&     
       !betterRelationship.endsWith("_of")  &&      
       !betterRelationship.contentEquals("RO")  &&      
       !relationship.contentEquals("RQ")  && 
       !betterRelationship.contentEquals("isa")  && 
       !betterRelationship.contentEquals("contains")  &&       //
       !betterRelationship.contentEquals("measures")  &&       //
       !betterRelationship.contentEquals("exhibited_by")  &&   //
       !betterRelationship.contentEquals("used_for")  &&   //
       !betterRelationship.contentEquals("AQ")        )
    ;// System.err.println("rel " + betterRelationship);
     
   String inverseRelationship = inverseRelationship( betterRelationship );

   if ( inverseRelationship != null ) {
      index ( aui2, aui1, inverseRelationship, sab, cui );   
      this.mrrelI++;
   } 
 }
 */


  
} // end Method index() ----------------------------

// =================================================
/**
 * hierarchicalRelationship returns true if the relationship is an inverse-isa, child, narrower than (RN), or sibling (SIB) 
 * 
 * @param pRelationship
 * @return boolean 
*/
// =================================================
private boolean hierarchicalRelationship(String pRelationship, String pRelationshipAttribute ) {
  boolean returnVal = false;

  
  
  
  if (pRelationship == null || pRelationship.trim().length() == 0) return returnVal;
  
  if      ( pRelationship.contentEquals("CHD"))   return true;
  else if (  pRelationship.contentEquals("PAR"))   return true;
  else if (  pRelationship.contentEquals("parent_of"))   return true;
  else if (  pRelationship.contentEquals("mapped_to"))   return true;
  else if (  pRelationship.contentEquals("RN"))   return true;
  else if (  pRelationship.contentEquals("RB"))   return false;
 
  else if (  pRelationship.contentEquals("RQ"))   return false;
  else if (  pRelationship.contentEquals("AQ"))   return false;
  else if (  pRelationship.contentEquals("SY"))   return true;
  else if (  pRelationship.contentEquals("SIB"))   return true;
 
  else if ( pRelationship.contentEquals("RO") && (pRelationshipAttribute == null || pRelationshipAttribute.trim().length() == 0 ))  return false;
  else if ( pRelationship.contentEquals("RO") ) {
     if ( pRelationshipAttribute.startsWith("has_") )   return true;
     else if ( pRelationshipAttribute.contains("part_"))   return true;
     else if ( pRelationshipAttribute.contains("consists"))   return true;
     else if ( pRelationshipAttribute.endsWith("subset_includes_concept"))  return true; 
   
  } 
  
  /*
    else if ( pRelationshipAttribute.endsWith("_of"))     return false;
     else if ( pRelationshipAttribute.endsWith("_by"))     return false; 
     else if ( pRelationshipAttribute.endsWith("_for"))    return false; 
    else if ( pRelationshipAttribute.endsWith("_from"))   return true;
    else if ( pRelationshipAttribute.startsWith("sib"))     return true; 
    else if ( pRelationshipAttribute.startsWith("common"))  return true; 
    else if ( pRelationshipAttribute.startsWith("alias"))   return true; 
    else if ( pRelationshipAttribute.endsWith("tradename")) return true; 
    
    else if ( pRelationshipAttribute.contains("part"))      return true; 
    else if ( pRelationshipAttribute.endsWith("consists"))  return true; 
   
  
   // else if ( pRelationshipAttribute.endsWith("_to"))     return false; 
    else if ( pRelationshipAttribute.equals("isa"))       return true; 
    else if ( pRelationshipAttribute.startsWith("member_")) return true; 
    else if ( pRelationshipAttribute.startsWith("manifestation_")) return false; 
  */  
  
  
 
  return returnVal;
} // end Method hierarchicalRelationship() -----------------


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
    
    if ( pRelationship.endsWith("_of")  )
        returnVal = "has_" + relationship.substring(0, relationship.length() -3) ;
    else if ( pRelationship.endsWith("_for") )
      returnVal = "has_" + relationship.substring(0, relationship.length() -4) ;
    else
    switch (relationship) {
      case "isa": returnVal = "inverse_isa";  break;
      case "PAR": returnVal = "CHD";          break;
      case "RB":  returnVal = "RN";           break;
      default :   returnVal = null;
    }

    return returnVal;
}

// =================================================
/**
 * index 
 * 
 * @param pAui1
 * @param pAui2
 * @param pRelationship
 * @param pSab
 * @param pCui
*/
// =================================================
  private final void index(String pAui1, String pAui2, String pRelationship, String pSab, String pCui) {
  
    if ( auiIndex == null )
      auiIndex = new HashMap<String, List<char[]>>(this.totalNumberOfMRRELRows *3 );
   
    char[] aRow = new String( pAui2 + "|" +  pRelationship + "|" + pSab + "|" + pCui).toCharArray() ;
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
    
    String    timeStamp = U.getDate();
    String     inputDir = U.getOption(args,  "--inputDir=", "./2019AA/META");
    String     inputFile = U.getOption(args,  "--inputFile=", "someFile");
    String    outputDir = U.getOption(args, "--outputDir=", inputDir + "_mental_health_terminology_" + timeStamp );
    String  mrrelFile = U.getOption( args, "--mrrel=", inputDir + "/mrrel.rrf");
    String totalNumberOfmrrelRows = U.getOption(args,"--totalNumberOfmrrelRows=", "10");
    String  filteredOutSabs = U.getOption(args,  "--filteredOutSabs=", "ICF-CY:GO" );
    String   outputName= U.getOption(pArgs,  "--outputName=", "someTopLevel" );
    String propertiesFile = U.getOption( pArgs, "--relPropertiesFile=", "c:/work/softwareRepos/framework-legacy/00_legacy/04.2_tUtils/src/resources/relationshipIds.properties");

    
    
    HashSet<String> filteredOutSabsHash =  setFilteredOutSabHash( filteredOutSabs);
    
    
    
    PerformanceMeter meter = new PerformanceMeter();
    
    U.mkDir(outputDir);
    outputDir = outputDir + "/" + outputName;
    U.mkDir(outputDir);
   
    
    // read in mrrel
    meter.begin( " Reading in MRREL  " );
    DescendAntConcepts mrrel = new DescendAntConcepts( args );
    
    // read in mrhier
    MRHIER mrhier = new MRHIER(args );
 
   
    
    String aCui = "C1269683" ;  //<--- Major Depressive Disorder   
    aCui = "C0004268";  // <---- attention  ~= attention functions, other specified
    
    System.err.print("Enter a cui to be decended " + " --> ");
    BufferedReader in =  new BufferedReader(new InputStreamReader(System.in)); 
    String inputCui = null;
    
    if ( inputFile != null && !inputFile.contains("someFile")) {
        
      try {
        String[] buff = U.readFileIntoStringArray(inputFile);
        if ( buff != null && buff.length > 0 ) {
          for (  String cui : buff) {
            if ( cui != null && !cui.startsWith("#")) {
              mrrel.processCui(  outputDir, cui, filteredOutSabsHash );
             mrhier.getDescendentRowsForCui(outputDir, cui) ;
            }
          }
        }
        
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Something went wrong with the batch mode " + e.toString());
      }
      
      
    } else {
    
      while ( (inputCui = in.readLine() ) != null ) {
        if ( inputCui != null && !inputCui.startsWith("#")) {
          System.err.print(inputCui + "\n");
          mrrel.processCui(  outputDir, inputCui, filteredOutSabsHash );
          mrhier.getDescendentRowsForCui(outputDir, inputCui) ;
       
        }
        System.err.print("Enter a cui to be decended (or control^z to quit) " + " --> ");
      
      }
    }
   
 
    
   
    // ----------------------------------
    // From the rel-logic, - re-read, index, create relationship ids, uniq on cui1|rel|cui2|sab 
    // looks for a properties file to keep track of the relationship id's assigned
    RelationshipUtils relUtils = new RelationshipUtils( args  );
    relUtils.readRelFiles( outputDir);
    relUtils.assignRelIds();
    relUtils.writeRelFiles();
    
    
    // -------------------------------- */
    // use unique lui's - normalize to remove the nos, () expressions
    
    meter.stop();
    
    System.err.println("Dohn");
    
  } catch ( Exception e ) {
    e.printStackTrace();
    System.err.println("Issue with creating mrrelsty from mrrel " + e.toString());
  }


} // end Method main() ----------------------------


// =================================================
/**
 * setFilteredOutSabHash 
 * 
 * @param filteredOutSabs
 * @return  HashSet<String>
*/
// =================================================
private final static HashSet<String> setFilteredOutSabHash(String filteredOutSabs) {
  HashSet<String> returnVal = null; 
  if ( filteredOutSabs != null ) {
    String[] cols = U.split(filteredOutSabs, ":");
    if ( cols != null && cols.length > 0 ) {
      returnVal = new HashSet<String>( cols.length);
      for ( String col : cols )
        returnVal.add( col.trim());
    }
  }
  
  return returnVal;
} // end Method setFilteredOutSabHash() -----------

//=================================================
/**
* processCui will create a $outputDir/[concept]_mrconso.rrf 
*  with all the decendents of the cui passed in.
*  
* @param pOutputDir
* @param pCui
*/
//=================================================
public void processCui(String pOutputDir, String pCui)  {

  processCui( pOutputDir, pCui, null);
  
} // end Method processCui() -----------------------

// =================================================
/**
 * processCui will create a $outputDir/[concept]_mrconso.rrf 
 *  with all the descendants of the cui passed in.
 *  
 * @param outputDir
 * @param pCui
 * @param pFilteredOutSabs
*/
// =================================================
public void processCui(String outputDir, String pCui, HashSet<String> pFilteredOutSabs) {
  try {  
    int ctr = 0;
    String conceptName = this.mrconso.getPreferredConceptName( pCui );
    
    if ( conceptName == null || conceptName.trim().length() == 0 )  return;
    
    conceptName = conceptName.replace(' ', '_');
    String fileName = outputDir + "/" + conceptName + "_mrconso.RRF";
    PrintWriter out = new PrintWriter ( fileName); //<----------------------------------- something wrong here >
    PrintWriter outLogic = new PrintWriter ( outputDir + "/" + conceptName + "_rel_logic.txt");
    
    
    // ----------------------
    // Add the original pCui's mrconso row to the output file
    // -GD 2021-06-02
    List<String> originalRows = this.mrconso.getRowsFromCui(pCui);
    if ( originalRows != null && !originalRows.isEmpty() )
      for ( String originalRow : originalRows ) {
        if ( !filteredOutSabs( pFilteredOutSabs, originalRow, MRCONSO.FIELD_SAB )) {
          out.print( originalRow + "\n");
          ctr++;
      }
    
    List<String> auis = this.getAuisForCui( pCui );
   
    List<String> rows = null;
    List<String> logicList = new ArrayList<String>();
   
 
     for ( String aui : auis ) {
       rows  = this.getDescendents( aui , pCui, logicList);
     
   
    if ( rows != null && !rows.isEmpty() ) {
      for ( String row : rows ) {
        if ( !filteredOutSabs( pFilteredOutSabs, row, MRCONSO.FIELD_SAB )) {
          out.print(   row + "\n" );
          ctr++;
        }
      }
    }
    // else 
    //  System.err.println( ctr++ + "| no records found" );
    
    System.err.println(ctr + " rows printed to " + fileName );
    
   
    
    
    if ( !logicList.isEmpty() ) {
      for ( String row: logicList ) {
        if ( !filteredOutSabs( pFilteredOutSabs, row, REL_LOGIC_FIELD_SAB)) {
          // if ( row.contains("ICF-CY"))
          outLogic.print( row  + "\n" );
        }
      }
    }
     }
    
     }
     
     out.close();
     outLogic.close();
  } catch (Exception e) {
    e.printStackTrace();
    System.err.println("Issue processing the cui " + pCui + " " + e.toString());
  }
  
} // end Method processCui() ---------------

// =================================================
/**
 * filteredOutSabs finds the the sab and sees if it
 * matches one of those that has to be filtered out
 * If so, it returns true.
 * 
 * @param pFilteredOutSabs
 * @param originalRow
 * @return boolean
*/
// =================================================
public final boolean filteredOutSabs(HashSet<String> pFilteredOutSabs, String pRow, int pSabCol ) {
  
  boolean returnVal = false;
  
  String cols[] = U.split(pRow );
  
  if (cols.length >= pSabCol ) {
    String sab = cols[pSabCol];
  
    if ( pFilteredOutSabs.contains( sab ) )
      returnVal = true;
    
  }
  
  return returnVal;
} // end Method filteredOutSabs() ----------

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
 String     inputFile = U.getOption(pArgs,  "--inputFile=", "someFile");
 String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_terminology_" + dateStamp);
 String        mrrel = U.getOption( pArgs, "--mrrel=", inputDir + "/MRREL.RRF");
 String totalNumberOfMRRELRows = U.getOption(pArgs,"--totalNumberOfMRRELRows=", "35728208");
 String totalNumberOfMrconsoRows = U.getOption(pArgs,"--totalNumberOfMrconsoRows=", "8946369");
 String totalNumberOfMRHIERRows = U.getOption(pArgs,"--totalNumberOfMRHIERRows=", "17406196");
 String      mrhier = U.getOption( pArgs, "--mrhier=", inputDir + "/mrhier.RRF");
 String  filteredOutSabs = U.getOption(pArgs,  "--filteredOutSabs=", "ICF-CY:GO" );
 String   outputName= U.getOption(pArgs,  "--outputName=", "someTopLevel" );
 String propertiesFile = U.getOption( pArgs, "--relPropertiesFile=", "c:/work/softwareRepos/framework-legacy/00_legacy/04.2_tUtils/src/resources/relationshipIds.properties");

 
 

 String args[] = {
     
     "--inputDir=" + inputDir,
     "--inputFile=" + inputFile,
     "--outputDir=" + outputDir,
     "--mrrel="    + mrrel,
     "--mrhier="   + mrhier,
     "--totalNumberOfMrconsoRows=" + totalNumberOfMrconsoRows,
     "--totalNumberOfMRRELRows=" + totalNumberOfMRRELRows,
     "--totalNumberOfMRHIERRows=" + totalNumberOfMRHIERRows,
     "--filteredOutSabs=" + filteredOutSabs,
     "--outputName=" + outputName,
     "--relPropertiesFile=" + propertiesFile
     
 };

 
    
 if ( Use.usageAndExitIfHelp("DescendAntConcepts",   pArgs, args  ))
   System.exit(0);
  

 return args;

}  // End Method setArgs() -----------------------

// ------------------------------------------------
// Class Variables
// ------------------------------------------------
char[][] rowStore = new char[100][];
int totalNumberOfMRRELRows = 5545407;
int mrrelI = 0;
PerformanceMeter meter = null;
HashMap<String, List<char[]>>       auiIndex = null;
MRCONSO mrconso = null;

final static  String ExampleAui = "A3513188"; // C1285340|ENG|P|L3017748|PF|S3369470|Y|A3513188|482320016|363180009||SNOMEDCT_US|PT|363180009|Inflammatory disorder of the respiratory tract|9|N|256|

// MRREL.RRF|Related Concepts|CUI1,AUI1,STYPE1,REL,CUI2,AUI2,STYPE2,RELA,RUI,SRUI,SAB,SL,RG,DIR,SUPPRESS,CVF|16|30797014|2811737031|

public static final int FIELD_CUI1   =  0;
public static final int FIELD_AUI1   =  1;
public static final int FIELD_STYPE1 =  2;  // The name of the column in MRCONSO.RRF that contains the identifier used for the first element in the relationship, i.e. AUI, CODE, CUI, SCUI, SDUI.
public static final int FIELD_REL   =  3;
public static final int FIELD_CUI2   =  4;
public static final int FIELD_AUI2   =  5;
public static final int FIELD_STYPE2 =  6; // The name of the column in MRCONSO.RRF that contains the identifier used for the second element in the relationship, i.e. AUI, CODE, CUI, SCUI, SDUI.
public static final int FIELD_REL_Attribute   = 7;
public static final int FIELD_RUI    =  8;  // relationship identifier
public static final int FIELD_SRUI   =  9;  // source asserted relationship
public static final int FIELD_SAB    =  10;
public static final int FIELD_SL     =  11; // Source of relationship labels
public static final int FIELD_RG     =  12; // Relationship group. Used to indicate that a set of relationships should be looked at in conjunction.
public static final int FIELD_DIR    =  13; // Source asserted directionality flag. Y indicates that this is the direction of the relationship in its source; N indicates that it is not; a blank indicates that it is not important or has not yet been determined.
public static final int FIELD_SUPRESS = 14;
public static final int FIELD_CVF     = 15;

private static final int REL_LOGIC_FIELD_CONCEPT_NAME1 = 0;
private static final int REL_LOGIC_FIELD_AUI1 = 1;
private static final int REL_LOGIC_FIELD_CUI1 = 2;
private static final int REL_LOGIC_FIELD_REL = 3;
private static final int REL_LOGIC_FIELD_AUI2 = 4;
private static final int REL_LOGIC_FIELD_CUI2 = 5;
private static final int REL_LOGIC_FIELD_CONCEPT_NAME2 = 6;
private static final int REL_LOGIC_FIELD_SAB = 7;

  
} // end Class MRREL() ----------------
