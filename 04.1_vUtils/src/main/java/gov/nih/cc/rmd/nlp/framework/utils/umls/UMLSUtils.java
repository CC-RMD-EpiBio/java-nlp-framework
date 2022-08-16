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
/*
 *
 */
/**
 * UMLSUtils includes those utilities that pertain to
 * umls functionality.
 *
 * Note: The semantic types used in this class are hard coded.  
 * If any need to be added, they will need to be added to this
 * class.
 * 
 * 
 * @author  Guy Divita 
 * @created Oct 8, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils.umls;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * The Class UMLSUtils.
 */
public class UMLSUtils {

  // -----------------------------------------
  /**
   * semanticTypeAbbrs returns abbreviations for semantic type names passed in.
   * Assumes that the String passed in is pipe delimited.
   *
   * @param semanticTypez the semantic typez
   * @return List<String>
   */
  // -----------------------------------------
  public static List<String> semanticTypeAbbrs(String semanticTypez) {
    ArrayList<String> returnValue = null;

    if (semanticTypez != null) {
      returnValue = new ArrayList<String>();
      String[] semanticTypes = U.split(semanticTypez);
      for (String semanticType : semanticTypes) {

        returnValue.add(getSemanticTypeAbbr(semanticType));
      }
    }
    return returnValue;
  } // end Method semanticTypeAbbrs() -------------

  // -----------------------------------------
  /**
   * getSemanticTypeAbbr returns abbreviations for semantic type names passed
   * in.
   *
   * @param semanticType the semantic type
   * @return String
   */
  // -----------------------------------------
  public static String getSemanticTypeAbbr(String semanticType) {

    if (semanticTypeHash == null)
      loadSemanticTypeHash();

    String abbr = semanticTypeHash.get(semanticType.toLowerCase());

    return abbr;
  } // end Method getSemanticTypeAbbr() --

  // -----------------------------------------
  /**
   * getSemanticType returns a semanticType given a semantic type abbreviation.
   *
   * @param semanticTypeAbbr the semantic type abbr
   * @return String
   */
  // -----------------------------------------
  public static String getSemanticType(String semanticTypeAbbr) {

    if (semanticTypeAbbrHash == null)
      loadSemanticTypeHash();

    String semanticType = semanticTypeAbbrHash.get(semanticTypeAbbr);

    return semanticType;
  } // end Method semanticType

  // -----------------------------------------
  /**
   * getSemanticTypeAbbrFromTui returns a semanticType given a Tui passed in.
   *
   * @param pTui the tui
   * @return String
   */
  // -----------------------------------------
  public static String getSemanticTypeAbbrFromTui(String pTui) {

    if (semanticTypeTuiHash == null)
      loadSemanticTypeHash();

    String semanticTypeAbbr = semanticTypeTuiHash.get(pTui);

    return semanticTypeAbbr;
  } // end Method semanticType

  // -----------------------------------------
  /**
   * getTuiFromSemanticType .
   *
   * @param pSemanticType the semantic type
   * @return the tui from semantic type
   */
  // -----------------------------------------
  public static String getTuiFromSemanticType(String pSemanticType) {

    String tui = null;
    if (tuiSemanticTypeHash == null)
      loadSemanticTypeHash();
    try {

      tui = tuiSemanticTypeHash.get(pSemanticType);
    } catch (Exception e) {
      String msg = "Issue with getting the tui for semantic type |" + pSemanticType + "|"
          + e.getMessage() + "\n" + U.getStackTrace(e);

      System.err.println(msg);
      throw new RuntimeException(e);
    }
    return tui;
  }

  // ------------------------------------------
  /**
   * getSemanticTypeNames returns a set of semantic type names, colon separated.
   *
   * @param semanticTypeAbbrs the semantic type abbrs
   * @return String
   */
  // ------------------------------------------
  public String getSemanticTypeNames(String[] semanticTypeAbbrs) {

    String returnVal = null;

    if (semanticTypeAbbrs != null) {
      StringBuffer buff = new StringBuffer();
      for (int i = 0; i < semanticTypeAbbrs.length; i++) {
        String name = UMLSUtils.getSemanticType(semanticTypeAbbrs[i]);
        buff.append(name);
        if (i < semanticTypeAbbrs.length - 1)
          buff.append(":");
      } // end loop through semantic type abbrs
      returnVal = buff.toString();
    }

    return returnVal;

  } // End Method getSemanticTypeNames() -----------------------

  // // -----------------------------------------
  // /**
  // * consolidateByCui returns UMLSConcepts for a bunch of rows
  // * in the form #Grams|Str|maxLookAhead|cui|sui|tui|sab| .....
  // *
  // * @param filteredCuiSuiTuiSabRows
  // * @return UMLSConcept[]
  // */
  // // -----------------------------------------
  // public static UMLSConcept[] consolidateByCui(String[]
  // filteredCuiSuiTuiSabRows ) {
  // UMLSConcept[] concepts = null;
  // HashMap<String, List<String>> cuiHash = null;
  //
  // if ( filteredCuiSuiTuiSabRows != null && filteredCuiSuiTuiSabRows.length >
  // 0 ) {
  //
  // cuiHash = new HashMap<String,
  // List<String>>(filteredCuiSuiTuiSabRows.length*2);
  //
  // // -----------------------------------------------
  // // Consolidate by cui thru a Hashmap
  // // -----------------------------------------------
  // for ( String row : filteredCuiSuiTuiSabRows) {
  // String fields[] = U.split( row);
  // String cui = fields[3];
  // List<String> cuiRows = cuiHash.get(cui);
  // if ( cuiRows == null )
  // cuiRows = new ArrayList<String>();
  // cuiRows.add( row);
  // cuiHash.put( cui, cuiRows);
  // }
  // } // end if there is any rows
  //
  // // ----------------------------------------------
  // // Loop thru the consolidated cuis
  // // ----------------------------------------------
  // if ( cuiHash != null && cuiHash.size() > 0 ) {
  // Collection<String> cuiKeys = cuiHash.keySet();
  // concepts = new UMLSConcept[cuiHash.size()];
  // int i = 0;
  // for(String cui: cuiKeys) {
  // List<String>rows = cuiHash.get(cui);
  // concepts[i++] = new UMLSConcept( cui, rows);
  //
  // } // end loop through cuis of the hash
  //
  // } // end if there are any cuis
  // else {
  // System.err.println( "no concepts? ");
  //
  // }
  //
  // return concepts;
  // } // end Method consolidateByCui() --------------

  // =================================================
  /**
   * getSemanticGroupsFromSemanticTypes 
   * 
   * @param semanticTypeAbbrs
   * @return String[] 
  */
  // =================================================
   public static String[] getSemanticGroupsFromSemanticTypeAbbrs(String[] pSemanticTypeAbbrs) {
   
     String[] returnVal = null;
     
     HashSet<String> groups = new HashSet<String>();
     if ( pSemanticTypeAbbrs != null && pSemanticTypeAbbrs.length > 0 ) 
       for ( String abbr : pSemanticTypeAbbrs )
          groups.add( getSemanticGroupForSemanticType( abbr ) );
         
    if ( !groups.isEmpty())
      returnVal = groups.toArray( new String[ groups.size()]);
     
     
     
     return returnVal;
  } // end Method getSemanticGroupsFromSemanticTypes() ------

  // =================================================
  /**
   * getSemanticGroupNameForSemanticType
   * 
   * @param pAbbr
   * @return String
  */
  // =================================================
  public static String getSemanticGroupForSemanticType(String pAbbr) {
    String returnVal = null;
    
    if ( pAbbr != null && !pAbbr.isEmpty())
      returnVal = semanticGroupNameHash.get( pAbbr );
      
    return returnVal;
  } // end Method getSemanticGroupForSemanticType() --

  
  

  // -----------------------------------------
  /**
   * loadSemanticTypeHash loads semantic types and their abbreviations into a
   * hashtable.
   */
  // -----------------------------------------
  private static void loadSemanticTypeHash() {

    semanticTypeHash = new Hashtable<String, String>(210);
    semanticTypeAbbrHash = new Hashtable<String, String>(210);
    semanticTypeTuiHash = new Hashtable<String, String>(210);
    tuiSemanticTypeHash = new Hashtable<String, String>(210);
    semanticGroupNameHash = new Hashtable<String, String>(210);

    for (int i = 0; i < semanticTypeTable.length; i++) {

      String field[] = U.split(semanticTypeTable[i]);
      semanticTypeHash.put(field[SEMANTIC_TYPE_NAME].toLowerCase(), field[SEMANTIC_TYPE_ABBR]);
      semanticTypeAbbrHash.put(field[SEMANTIC_TYPE_ABBR], field[SEMANTIC_TYPE_NAME]);
      semanticTypeTuiHash.put(field[SEMANTIC_TYPE_TUI], field[SEMANTIC_TYPE_ABBR]);
      tuiSemanticTypeHash.put(field[SEMANTIC_TYPE_ABBR], field[SEMANTIC_TYPE_TUI]);
      semanticGroupNameHash.put( field[SEMANTIC_TYPE_ABBR], field[SEMANTIC_GROUP_NAME]);

    }

  } // end Method loadSemanticTypeHash() ------

  // ------------------------------------------
  // Global Variables
  /** The semantic type hash. */
  // ------------------------------------------
  private static Hashtable<String, String> semanticTypeHash = null;

  /** The semantic type abbr hash. */
  private static Hashtable<String, String> semanticTypeAbbrHash = null;

  /** The semantic type tui hash. */
  private static Hashtable<String, String> semanticTypeTuiHash = null;

  /** The tui semantic type hash. */
  private static Hashtable<String, String> tuiSemanticTypeHash = null;
  
  /** The semantic group hash. */
  private static Hashtable<String, String> semanticGroupNameHash = null;


  /** The Constant SEMANTIC_TYPE_NAME. */
  private static final int SEMANTIC_TYPE_NAME = 2;

  /** The Constant SEMANTIC_TYPE_ABBR. */
  private static final int SEMANTIC_TYPE_ABBR = 1;

  /** The Constant SEMANTIC_TYPE_TUI. */
  private static final int SEMANTIC_TYPE_TUI = 0;
  
  /** The Constant SEMANTIC_GROUP ABBR */
  private static final int SEMANTIC_GROUP_ABBR= 3;
  
  /** The Constant SEMANTIC_GROUP Name */
  private static final int SEMANTIC_GROUP_NAME = 4;

  /** The Constant semanticTypeTable. */
  /*  This is a copy of the
     01_sophia_resources\src\main\resources\resources\vinciNLPFramework
        \sophia\2017AA\semanticTypes\semanticTypesAndGroups.txt
        
        */
  private static final String[] semanticTypeTable = {
   //   #
   //  #  This file is built from https://metamap.nlm.nih.gov/Docs/SemGroups_2018.txt
   //   #  and 2017AA's NET 
   //   #
   //   #---+----+----------------+---------+---------+----------------
   //  #TUI|Abbr|SemanticTypeName|GroupAbbr|GroupName|SemanticTypeName
   //   #---+----+----------------+---------+---------+----------------
      "T001|orgm|Organism|LIVB|Living Beings|Organism",
      "T002|plnt|Plant|LIVB|Living Beings|Plant",
      "T004|fngs|Fungus|LIVB|Living Beings|Fungus",
      "T005|virs|Virus|LIVB|Living Beings|Virus",
      "T006|rich|Rickettsia or Chlamydia|???|???|Rickettsia or Chlamydia",
      "T007|bact|Bacterium|LIVB|Living Beings|Bacterium",
      "T008|anim|Animal|LIVB|Living Beings|Animal",
      "T009|invt|Invertebrate|???|???|Invertebrate",
      "T010|vtbt|Vertebrate|LIVB|Living Beings|Vertebrate",
      "T011|amph|Amphibian|LIVB|Living Beings|Amphibian",
      "T012|bird|Bird|LIVB|Living Beings|Bird",
      "T013|fish|Fish|LIVB|Living Beings|Fish",
      "T014|rept|Reptile|LIVB|Living Beings|Reptile",
      "T015|mamm|Mammal|LIVB|Living Beings|Mammal",
      "T016|humn|Human|LIVB|Living Beings|Human",
      "T017|anst|Anatomical Structure|ANAT|Anatomy|Anatomical Structure",
      "T018|emst|Embryonic Structure|ANAT|Anatomy|Embryonic Structure",
      "T019|cgab|Congenital Abnormality|DISO|Disorders|Congenital Abnormality",
      "T020|acab|Acquired Abnormality|DISO|Disorders|Acquired Abnormality",
      "T021|ffas|Fully Formed Anatomical Structure|ANAT|Anatomy|Fully Formed Anatomical Structure",
      "T022|bdsy|Body System|ANAT|Anatomy|Body System",
      "T023|bpoc|Body Part, Organ, or Organ Component|ANAT|Anatomy|Body Part, Organ, or Organ Component",
      "T024|tisu|Tissue|ANAT|Anatomy|Tissue",
      "T025|cell|Cell|ANAT|Anatomy|Cell",
      "T026|celc|Cell Component|ANAT|Anatomy|Cell Component",
      "T028|gngm|Gene or Genome|GENE|Genes & Molecular Sequences|Gene or Genome",
      "T029|blor|Body Location or Region|ANAT|Anatomy|Body Location or Region",
      "T030|bsoj|Body Space or Junction|ANAT|Anatomy|Body Space or Junction",
      "T031|bdsu|Body Substance|ANAT|Anatomy|Body Substance",
      "T032|orga|Organism Attribute|PHYS|Physiology|Organism Attribute",
      "T033|fndg|Finding|DISO|Disorders|Finding",
      "T034|lbtr|Laboratory or Test Result|PHEN|Phenomena|Laboratory or Test Result",
      "T035|???|???|???|???|???",
      "T036|???|???|???|???|???",
      "T037|inpo|Injury or Poisoning|DISO|Disorders|Injury or Poisoning",
      "T038|biof|Biologic Function|PHEN|Phenomena|Biologic Function",
      "T039|phsf|Physiologic Function|PHYS|Physiology|Physiologic Function",
      "T040|orgf|Organism Function|PHYS|Physiology|Organism Function",
      "T041|menp|Mental Process|PHYS|Physiology|Mental Process",
      "T042|ortf|Organ or Tissue Function|PHYS|Physiology|Organ or Tissue Function",
      "T043|celf|Cell Function|PHYS|Physiology|Cell Function",
      "T044|moft|Molecular Function|PHYS|Physiology|Molecular Function",
      "T045|genf|Genetic Function|PHYS|Physiology|Genetic Function",
      "T046|patf|Pathologic Function|DISO|Disorders|Pathologic Function",
      "T047|dsyn|Disease or Syndrome|DISO|Disorders|Disease or Syndrome",
      "T048|mobd|Mental or Behavioral Dysfunction|DISO|Disorders|Mental or Behavioral Dysfunction",
      "T049|comd|Cell or Molecular Dysfunction|DISO|Disorders|Cell or Molecular Dysfunction",
      "T050|emod|Experimental Model of Disease|DISO|Disorders|Experimental Model of Disease",
      "T051|evnt|Event|ACTI|Activities & Behaviors|Event",
      "T052|acty|Activity|ACTI|Activities & Behaviors|Activity",
      "T053|bhvr|Behavior|ACTI|Activities & Behaviors|Behavior",
      "T054|socb|Social Behavior|ACTI|Activities & Behaviors|Social Behavior",
      "T055|inbe|Individual Behavior|ACTI|Activities & Behaviors|Individual Behavior",
      "T056|dora|Daily or Recreational Activity|ACTI|Activities & Behaviors|Daily or Recreational Activity",
      "T057|ocac|Occupational Activity|ACTI|Activities & Behaviors|Occupational Activity",
      "T058|hlca|Health Care Activity|PROC|Procedures|Health Care Activity",
      "T059|lbpr|Laboratory Procedure|PROC|Procedures|Laboratory Procedure",
      "T060|diap|Diagnostic Procedure|PROC|Procedures|Diagnostic Procedure",
      "T061|topp|Therapeutic or Preventive Procedure|PROC|Procedures|Therapeutic or Preventive Procedure",
      "T062|resa|Research Activity|PROC|Procedures|Research Activity",
      "T063|mbrt|Molecular Biology Research Technique|PROC|Procedures|Molecular Biology Research Technique",
      "T064|gora|Governmental or Regulatory Activity|ACTI|Activities & Behaviors|Governmental or Regulatory Activity",
      "T065|edac|Educational Activity|PROC|Procedures|Educational Activity",
      "T066|mcha|Machine Activity|ACTI|Activities & Behaviors|Machine Activity",
      "T067|phpr|Phenomenon or Process|PHEN|Phenomena|Phenomenon or Process",
      "T068|hcpp|Human-caused Phenomenon or Process|PHEN|Phenomena|Human-caused Phenomenon or Process",
      "T069|eehu|Environmental Effect of Humans|PHEN|Phenomena|Environmental Effect of Humans",
      "T070|npop|Natural Phenomenon or Process|PHEN|Phenomena|Natural Phenomenon or Process",
      "T071|enty|Entity|OBJC|Objects|Entity",
      "T072|phob|Physical Object|OBJC|Objects|Physical Object",
      "T073|mnob|Manufactured Object|OBJC|Objects|Manufactured Object",
      "T074|medd|Medical Device|DEVI|Devices|Medical Device",
      "T075|resd|Research Device|DEVI|Devices|Research Device",
      "T076|???|???|???|???|???",
      "T077|cnce|Conceptual Entity|CONC|Concepts & Ideas|Conceptual Entity",
      "T078|idcn|Idea or Concept|CONC|Concepts & Ideas|Idea or Concept",
      "T079|tmco|Temporal Concept|CONC|Concepts & Ideas|Temporal Concept",
      "T080|qlco|Qualitative Concept|CONC|Concepts & Ideas|Qualitative Concept",
      "T081|qnco|Quantitative Concept|CONC|Concepts & Ideas|Quantitative Concept",
      "T082|spco|Spatial Concept|CONC|Concepts & Ideas|Spatial Concept",
      "T083|geoa|Geographic Area|GEOG|Geographic Areas|Geographic Area",
      "T084|???|???|???|???|???",
      "T085|mosq|Molecular Sequence|GENE|Genes & Molecular Sequences|Molecular Sequence",
      "T086|nusq|Nucleotide Sequence|GENE|Genes & Molecular Sequences|Nucleotide Sequence",
      "T087|amas|Amino Acid Sequence|GENE|Genes & Molecular Sequences|Amino Acid Sequence",
      "T088|crbs|Carbohydrate Sequence|GENE|Genes & Molecular Sequences|Carbohydrate Sequence",
      "T089|rnlw|Regulation or Law|CONC|Concepts & Ideas|Regulation or Law",
      "T090|ocdi|Occupation or Discipline|OCCU|Occupations|Occupation or Discipline",
      "T091|bmod|Biomedical Occupation or Discipline|OCCU|Occupations|Biomedical Occupation or Discipline",
      "T092|orgt|Organization|ORGA|Organizations|Organization",
      "T093|hcro|Health Care Related Organization|ORGA|Organizations|Health Care Related Organization",
      "T094|pros|Professional Society|ORGA|Organizations|Professional Society",
      "T095|shro|Self-help or Relief Organization|ORGA|Organizations|Self-help or Relief Organization",
      "T096|grup|Group|LIVB|Living Beings|Group",
      "T097|prog|Professional or Occupational Group|LIVB|Living Beings|Professional or Occupational Group",
      "T098|popg|Population Group|LIVB|Living Beings|Population Group",
      "T099|famg|Family Group|LIVB|Living Beings|Family Group",
      "T100|aggp|Age Group|LIVB|Living Beings|Age Group",
      "T101|podg|Patient or Disabled Group|LIVB|Living Beings|Patient or Disabled Group",
      "T102|grpa|Group Attribute|CONC|Concepts & Ideas|Group Attribute",
      "T103|chem|Chemical|CHEM|Chemicals & Drugs|Chemical",
      "T104|chvs|Chemical Viewed Structurally|CHEM|Chemicals & Drugs|Chemical Viewed Structurally",
      "T105|???|???|???|???|???",
      "T106|???|???|???|???|???",
      "T107|???|???|???|???|???",
      "T108|???|???|???|???|???",
      "T109|orch|Organic Chemical|CHEM|Chemicals & Drugs|Organic Chemical",
      "T110|strd|Steroid|???|???|Steroid",
      "T111|eico|Eicosanoid|???|???|Eicosanoid",
      "T112|???|???|???|???|???",
      "T113|???|???|???|???|???",
      "T114|nnon|Nucleic Acid, Nucleoside, or Nucleotide|CHEM|Chemicals & Drugs|Nucleic Acid, Nucleoside, or Nucleotide",
      "T115|opco|Organophosphorus Compound|???|???|Organophosphorus Compound",
      "T116|aapp|Amino Acid, Peptide, or Protein|CHEM|Chemicals & Drugs|Amino Acid, Peptide, or Protein",
      "T117|???|???|???|???|???",
      "T118|carb|Carbohydrate|???|???|Carbohydrate",
      "T119|lipd|Lipid|???|???|Lipid",
      "T120|chvf|Chemical Viewed Functionally|CHEM|Chemicals & Drugs|Chemical Viewed Functionally",
      "T121|phsu|Pharmacologic Substance|CHEM|Chemicals & Drugs|Pharmacologic Substance",
      "T122|bodm|Biomedical or Dental Material|CHEM|Chemicals & Drugs|Biomedical or Dental Material",
      "T123|bacs|Biologically Active Substance|CHEM|Chemicals & Drugs|Biologically Active Substance",
      "T124|nsba|Neuroreactive Substance or Biogenic Amine|???|???|Neuroreactive Substance or Biogenic Amine",
      "T125|horm|Hormone|CHEM|Chemicals & Drugs|Hormone",
      "T126|enzy|Enzyme|CHEM|Chemicals & Drugs|Enzyme",
      "T127|vita|Vitamin|CHEM|Chemicals & Drugs|Vitamin",
      "T128|???|???|???|???|???",
      "T129|imft|Immunologic Factor|CHEM|Chemicals & Drugs|Immunologic Factor",
      "T130|irda|Indicator, Reagent, or Diagnostic Aid|CHEM|Chemicals & Drugs|Indicator, Reagent, or Diagnostic Aid",
      "T131|hops|Hazardous or Poisonous Substance|CHEM|Chemicals & Drugs|Hazardous or Poisonous Substance",
      "T167|sbst|Substance|OBJC|Objects|Substance",
      "T168|food|Food|OBJC|Objects|Food",
      "T169|ftcn|Functional Concept|CONC|Concepts & Ideas|Functional Concept",
      "T170|inpr|Intellectual Product|CONC|Concepts & Ideas|Intellectual Product",
      "T171|lang|Language|CONC|Concepts & Ideas|Language",
      "T184|sosy|Sign or Symptom|DISO|Disorders|Sign or Symptom",
      "T185|clas|Classification|CONC|Concepts & Ideas|Classification",
      "T190|anab|Anatomical Abnormality|DISO|Disorders|Anatomical Abnormality",
      "T191|neop|Neoplastic Process|DISO|Disorders|Neoplastic Process",
      "T192|rcpt|Receptor|CHEM|Chemicals & Drugs|Receptor",
      "T194|arch|Archaeon|LIVB|Living Beings|Archaeon",
      "T195|antb|Antibiotic|CHEM|Chemicals & Drugs|Antibiotic",
      "T196|elii|Element, Ion, or Isotope|CHEM|Chemicals & Drugs|Element, Ion, or Isotope",
      "T197|inch|Inorganic Chemical|CHEM|Chemicals & Drugs|Inorganic Chemical",
      "T200|clnd|Clinical Drug|CHEM|Chemicals & Drugs|Clinical Drug",
      "T201|clna|Clinical Attribute|PHYS|Physiology|Clinical Attribute",
      "T203|drdd|Drug Delivery Device|DEVI|Devices|Drug Delivery Device",
      "T204|euka|Eukaryote|LIVB|Living Beings|Eukaryote"};
  /*

      
      "aapp|T116|Amino Acid, Peptide, or Protein", 
      "acab|T020|Acquired Abnormality",
      "acty|T052|Activity", "aggp|T100|Age Group", 
      "alga|T003|Alga",
      "amas|T087|Amino Acid Sequence", 
      "amph|T011|Amphibian",
      "anab|T190|Anatomical Abnormality",
      "anim|T008|Animal", 
      "anst|T017|Anatomical Structure", 
      "antb|T195|Antibiotic",
      "arch|T194|Archaeon", 
      "bacs|T123|Biologically Active Substance", 
      "bact|T007|Bacterium",
      "bdsu|T031|Body Substance",
      "bdsy|T022|Body System", 
      "bhvr|T053|Behavior",
      "biof|T038|Biologic Function", 
      "bird|T012|Bird", 
      "blor|T029|Body Location or Region",
      "bmod|T091|Biomedical Occupation or Discipline", 
      "bodm|T122|Biomedical or Dental Material",
      "bpoc|T023|Body Part, Organ, or Organ Component", 
      "bsoj|T030|Body Space or Junction",
      "carb|T118|Carbohydrate", 
      "celc|T026|Cell Component", 
      "celf|T043|Cell Function",
      "cell|T025|Cell", 
      "cgab|T019|Congenital Abnormality", 
      "chem|T103|Chemical",
      "chvf|T120|Chemical Viewed Functionally", 
      "chvs|T104|Chemical Viewed Structurally",
      "clas|T185|Classification",
      "clna|T201|Clinical Attribute", 
      "clnd|T200|Clinical Drug",
      "cnce|T077|Conceptual Entity", 
      "comd|T049|Cell or Molecular Dysfunction",
      "crbs|T088|Carbohydrate Sequence", 
      "diap|T060|Diagnostic Procedure",
      "dora|T056|Daily or Recreational Activity",
      "dsyn|T047|Disease or Syndrome",
      "edac|T065|Educational Activity", 
      "eehu|T069|Environmental Effect of Humans",
      "eico|T111|Eicosanoid", 
      "elii|T196|Element, Ion, or Isotope",
      "emod|T050|Experimental Model of Disease", 
      "emst|T018|Embryonic Structure",
      "enty|T071|Entity", 
      "enzy|T126|Enzyme",
      "evnt|T051|Event", 
      "famg|T099|Family Group",
      "ffas|T021|Fully Formed Anatomical Structure", 
      "fish|T013|Fish", 
      "fndg|T033|Finding",
      "fngs|T004|Fungus", 
      "food|T168|Food",
      "ftcn|T169|Functional Concept",
      "genf|T045|Genetic Function", 
      "geoa|T083|Geographic Area",
      "gngm|T028|Gene or Genome",
      "gora|T064|Governmental or Regulatory Activity",
      "grpa|T102|Group Attribute",
      "grup|T096|Group", 
      "hcpp|T068|Human-caused Phenomenon or Process",
      "hcro|T093|Health Care Related Organization", 
      "hlca|T058|Health Care Activity",
      "hops|T131|Hazardous or Poisonous Substance",
      "horm|T125|Hormone", 
      "humn|T016|Human",
      "idcn|T078|Idea or Concept", 
      "imft|T129|Immunologic Factor",
      "inbe|T055|Individual Behavior",
      "inch|T197|Inorganic Chemical",
      "inpo|T037|Injury or Poisoning",
      "inpr|T170|Intellectual Product", 
      "invt|T009|Invertebrate",
      "irda|T130|Indicator, Reagent, or Diagnostic Aid",
      "lang|T171|Language",
      "lbpr|T059|Laboratory Procedure", 
      "lbtr|T034|Laboratory or Test Result",
      "lipd|T119|Lipid",
      "mamm|T015|Mammal",
      "mbrt|T063|Molecular Biology Research Technique",
      "mcha|T066|Machine Activity",
      "medd|T074|Medical Device",
      "menp|T041|Mental Process",
      "mnob|T073|Manufactured Object",
      "mobd|T048|Mental or Behavioral Dysfunction",
      "moft|T044|Molecular Function", 
      "mosq|T085|Molecular Sequence",
      "neop|T191|Neoplastic Process",
      "nnon|T114|Nucleic Acid, Nucleoside, or Nucleotide",
      "npop|T070|Natural Phenomenon or Process",
      "nsba|T124|Neuroreactive Substance or Biogenic Amine", 
      "nusq|T086|Nucleotide Sequence",
      "ocac|T057|Occupational Activity",
      "ocdi|T090|Occupation or Discipline",
      "opco|T115|Organophosphorus Compound",
      "orch|T109|Organic Chemical",
      "orga|T032|Organism Attribute", 
      "orgf|T040|Organism Function", 
      "orgm|T001|Organism",
      "orgt|T092|Organization", 
      "ortf|T042|Organ or Tissue Function",
      "patf|T046|Pathologic Function",
      "phob|T072|Physical Object",
      "phpr|T067|Phenomenon or Process",
      "phsf|T039|Physiologic Function",
      "phsu|T121|Pharmacologic Substance",
      "plnt|T002|Plant", 
      "podg|T101|Patient or Disabled Group",
      "popg|T098|Population Group", 
      "prog|T097|Professional or Occupational Group",
      "pros|T094|Professional Society",
      "qlco|T080|Qualitative Concept",
      "qnco|T081|Quantitative Concept",
      "rcpt|T192|Receptor", "rept|T014|Reptile",
      "resa|T062|Research Activity",
      "resd|T075|Research Device",
      "rich|T006|Rickettsia or Chlamydia",
      "rnlw|T089|Regulation or Law",
      "sbst|T167|Substance",
      "shro|T095|Self-help or Relief Organization", 
      "socb|T054|Social Behavior",
      "sosy|T184|Sign or Symptom", 
      "spco|T082|Spatial Concept", 
      "strd|T110|Steroid",
      "tisu|T024|Tissue", 
      "tmco|T079|Temporal Concept",
      "topp|T061|Therapeutic or Preventive Procedure", 
      "virs|T005|Virus", "vita|T127|Vitamin",
      "vtbt|T010|Vertebrate",
      "drdd|T203|Drug Delivery Device", 
      "euka|T204|Eukaryote"
  };
*/
} // end Class UMLSUtils() ------------------------
