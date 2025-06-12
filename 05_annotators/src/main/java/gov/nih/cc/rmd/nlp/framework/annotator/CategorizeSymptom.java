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
 * CategorizeSymptom.java Summary
 *  Class Detail
 *
 *
 * @author  Guy Divita 
 * @created Sep 30, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;


public class CategorizeSymptom {

  // ----------------------------------------------
  /**
   * Constructor: CategorizeSymptom
   *
   * @param symptomTableURL
   * @throws Exception 
   */
  // ----------------------------------------------
  public CategorizeSymptom(String symptomTableURL, String symptomGeneralizationsURL) throws Exception {
  
   loadMappingTable();
   
 
  } // end Constructor()---------------------------

  // ----------------------------------------------
  /**
   * loadGeneralizations
   * 
   * @param symptomGeneralizationsURL
   * @throws Exception 
   */
  // ----------------------------------------------
  private void loadGeneralizations(String symptomGeneralizationsURL) throws Exception {
    
	if ( mGeneralization == null ) {
    mGeneralization = new Hashtable<String,String>( 40 );
    String resourcePrefix = U.getClassPathToResources();
    String fullFileName = resourcePrefix + symptomGeneralizationsURL;
    
    BufferedReader in = new BufferedReader( new FileReader( fullFileName));
    
    String line = null;
    while ( (line = in.readLine() )!= null ) {
      String fields[] = U.split( line);
     
      // -------+-----------------
      // Number | Generalization
      // -------+-----------------
      String       code = fields[0];
      String       name = fields[1]; 
      mGeneralization.put (code, name);
    }
    in.close();
	}
  } // end Method loadGeneraliations() ------------

  // ----------------------------------------------
  /**
   * loadMappingTablePrep1 This method takes the concatinated mapping table that Olivier Bodenreider 
   * gave to me, extracts the relevant fields, generalizes it to the categories I'm looking for
   * creates a reduced table for the runtime use.
   * 
   * See the table symptomGeneralizations.txt for the categories I kept. I kept all the icd codes
   * that were between 780 and 799.5.  In addition, I kept E001-E030.9, which got marked as ACTIVITY
   * but contained what the annotators were marking as symptoms,  I kept 710-739.99 which are
   * DISEASES of the MUSCULOSKELETAL SYSTEM and CONNECTIVE Tissue.  There were some cui's that had symptom as
   * a semantic type but only mapped to this ICD code.  I kept 338-338.9 which is the generalied category
   * for pain.  And I kept 680-709.99 which was Unspecified disorder of skin and subcutaneous tissue.  That 
   * category had symptoms like rash in it.
   * 
   * I threw out mappings that Olivier included that involved icd codes 
   *       00-99.99|Procedures 
   *       01-05.99|OPERATIONS ON THE NERVOUS SYSTE
   *       06-07.99|OPERATIONS ON THE ENDOCRINE SYSTEM
   *       18-20.99||OPERATIONS ON THE EAR
   *       21-29.99|OPERATIONS ON THE NOSE, MOUTH, AND PHARYNX
   *       30-34.99|OPERATIONS ON THE RESPIRATORY SYSTEM
   *       35-49.99|OPERATIONS ON THE CARDIOVASCULAR SYSTEM
   *       42-54.99|OPERATIONS ON THE DIGESTIVE SYSTEM
   *       55-59.99|OPERATIONS ON THE URINARY SYSTEM
   *       65-71.99||OPERATIONS ON THE FEMALE GENITAL ORGANS
   *       72-75.99|OBSTETRICAL PROCEDURES
   *       76-84.99|OPERATIONS ON THE MUSCULOSKELETAL SYSTEM
   *       85-86.99|OPERATIONS ON THE INTEGUMENTARY SYSTEM
   *       87-99.99||MISCELLANEOUS DIAGNOSTIC AND THERAPEUTIC PROCEDURES
   *       001-139.99|INFECTIOUS AND PARASITIC DISEASES
   *       010-018.99|TUBERCULOSIS
   *       030-041.99|
   *       042-042.99|HUMAN IMMUNODEFICIENCY VIRUS [HIV] INFECTION
   *       140-239.99|NEOPLASMS
   *       190-199.99|MALIGNANT NEOPLASM OF OTHER AND UNSPECIFIED SITES
   *       200-208.99|MALIGNANT NEOPLASM OF LYMPHATIC AND HEMATOPOIETIC TISSUE
   *       209-209.99|NEUROENDOCRINE TUMORS
   *       210-229.99|BENIGN NEOPLASMS
   *       230-234.99|CARCINOMA IN SITU
   *       239-239.99|NEOPLASMS OF UNSPECIFIED NATURE
   *       240-246.99|DISORDERS OF THYROID GLAND      
   *       260-269.99|NUTRITIONAL DEFICIENCIES
   *       270-279.99|OTHER METABOLIC AND IMMUNITY DISORDERS
   *       280-289.99|DISEASES OF THE BLOOD AND BLOOD-FORMING ORGANS
   *       290-299.99|PSYCHOSES
   *       290-294.99|ORGANIC PSYCHOTIC CONDITIONS
   *       290-319.99|MENTAL DISORDERS <---------------------
   *       295-299.99|OTHER PSYCHOSES    <------------------------ 
   *       300-316.99|NEUROTIC DISORDERS, PERSONALITY DISORDERS, AND OTHER NONPSYCHOTIC MENTAL DISORDERS
   *       317-319.99||MENTAL RETARDATION
   *       320-389.99|DISEASES OF THE NERVOUS SYSTEM AND SENSE ORGANS

   *       340-349.99|OTHER DISORDERS OF THE CENTRAL NERVOUS SYSTEM
   *       350-359.99|DISORDERS OF THE PERIPHERAL NERVOUS SYSTEM
   *       390-392.99|ACUTE RHEUMATIC FEVER
   *       390-459.99|DISEASES OF THE CIRCULATORY SYSTEM
   *       401-405.99|HYPERTENSIVE DISEASE
   *       410-414.99|ISCHEMIC HEART DISEASE
   *       415-417.99|DISEASES OF PULMONARY CIRCULATION
   *       420-429.99|OTHER FORMS OF HEART DISEASE
   *       430-438.99|CEREBROVASCULAR DISEASE
   *       460-519.99|
   *       470-478.99|OTHER DISEASES OF THE UPPER RESPIRATORY TRACT
   *       510-519.99|OTHER DISEASES OF RESPIRATORY SYSTEM
   *       520-579.99|DISEASES OF THE DIGESTIVE SYSTEM  <-----------------------
   *       550-553.99|HERNIA OF ABDOMINAL CAVITY  <---------------------
   *       570-579.99|OTHER DISEASES OF DIGESTIVE SYSTEM <--------------
   *       580-629.99|DISEASES OF THE GENITOURINARY SYSTEM
   *       590-599.99|OTHER DISEASES OF URINARY SYSTEM
   *       600-608.99|DISEASES OF MALE GENITAL ORGANS
   *       610-612.99|DISORDERS OF BREAST <---------------------
   *       614-616.99|INFLAMMATORY DISEASE OF FEMALE PELVIC ORGANS <------------
   *       630-679.99|COMPLICATIONS OF PREGNANCY, CHILDBIRTH, AND THE PUERPERIUM
   *       670-677.99|COMPLICATIONS OF THE PUERPERIUM
   *       700-709.99|OTHER DISEASES OF SKIN AND SUBCUTANEOUS TISSUE
   *       720-724.99|DORSOPATHIES
   *       740-759.99|CONGENITAL ANOMALIES
   *       760-779.99|CERTAIN CONDITIONS ORIGINATING IN THE PERINATAL PERIOD
   *       790-796.99|NONSPECIFIC ABNORMAL FINDINGS
   *       797-799.99|ILL-DEFINED AND UNKNOWN CAUSES OF MORBIDITY AND MORTALITY
   *       800-804.99|FRACTURE OF SKULL
   *       800-829.99|FRACTURES
   *       800-999.99|INJURY AND POISONING
   *       810-819.99|FRACTURE OF UPPER LIMB
   *       830-839.99|DISLOCATION
   *       840-848.99|C0409317|SPRAINS AND STRAINS OF JOINTS AND ADJACENT MUSCLES
   *       870-897.99|OPEN WOUNDS
   *       880-887.99|OPEN WOUND OF UPPER LIMB
   *       890-897.99|DISEASES OF THE MUSCULOSKELETAL SYSTEM AND CONNECTIVE TISSUE
   *       900-904.99|INJURY TO BLOOD VESSELS
   *       910-919.99|SUPERFICIAL INJURY
   *       940-949.99|BURNS
   *       990-995.99|OTHER AND UNSPECIFIED EFFECTS OF EXTERNAL CAUSES
   *       E800-E807.9|RAILWAY ACCIDENTS
   *       E830-E838.9|WATER TRANSPORT ACCIDENTS
   *       E880-E888.9|ACCIDENTAL FALLS
   *       E916-E928.9|OTHER ACCIDENTS
   *       V01-V06.99|PERSONS WITH POTENTIAL HEALTH HAZARDS RELATED TO COMMUNICABLE DISEASES
   *       V12.60|
   *       V60-V69.99|PERSONS ENCOUNTERING HEALTH SERVICES IN OTHER CIRCUMSTANCES
   *       V85-V85.99|BODY MASS INDEX
   *       V86-V86.99|ESTROGEN RECEPTOR STATUS
   *       V87-V87.99|OTHER SPECIFIED PERSONAL EXPOSURES AND HISTORY PRESENTING HAZARDS TO HEALTH
   *       V88-V88.99|ACQUIRED ABSENCE OF OTHER ORGANS AND TISSUE
   *       V90-V90.99|RETAINED FOREIGN BODY
   *       V91-V91.99|MULTIPLE GESTATION PLACENTA STATUS
   *       NOCODE
   * 
   * @param symptomTableURL
   * @throws Exception 
   */
  // ----------------------------------------------
  private void loadMappingTablePrep1(String symptomTableURL) throws Exception {
    
	if ( mMapping == null ) {
    mMapping = new HashMap<String,String>(120000);
    String resourcePrefix = U.getClassPathToResources();
    String fullFileName = resourcePrefix + symptomTableURL;
    
    BufferedReader in = new BufferedReader( new FileReader( fullFileName));
   
    PrintWriter out = new PrintWriter ( fullFileName + ".out");
    
    String line = null;
    while ( (line = in.readLine() )!= null ) {
      
      
      if (( line != null ) && ( line.trim().length() > 0 ) && ( line.indexOf("|") > 0 ) ) {
      String fields[] = U.split( line);
      // ----+-------------+---+-----------+---------------+----------+------------------------+   
      // mark|mapping Vocab|cui|conceptName|mappedSourceCui|mappedCode|MappedSourceConceptName |
      //     |  ICD9CM     |   |           |               |   780    |                     
      // ----+-------------+---+-----------+---------------+----------+------------------------+
      String        cui = fields[0];
      String mappedCodez = null;
      try {
    	  mappedCodez = fields[2]; // there might be multiple, delimited by ; 
      
    	  String mappedCodes[] = null;
      
      
      if ( mappedCodez.indexOf(";") > 0  ) 
        mappedCodes = U.split(mappedCodez, ";");
      else {
        mappedCodes = new String[1];
        mappedCodes[0] = mappedCodez;
      }
      for ( int i = 0; i < mappedCodes.length; i++ ) {
        String categoryCode = generalizeCode( mappedCodes[i]);
        String generalizedCode = generalize( categoryCode);
        
        if ( generalizedCode != null ) {
          out.println( cui + "|" + generalizedCode);
          mMapping.put( cui, generalizedCode);
        }
      }
      } catch ( Exception e ) {
    	  // System.err.println("Trouble digesting line --> " + line);
      }
        // else 
          // System.err.println("Throwing out code " + mappedCodes[i]);
      } // end loop through each mappedCode
      
    } // end loop through table 
    out.close();
    in.close();
	}
  } // end Method loadMappingTable() --------------

  // ------------------------------------------
  /**
   * loadMappingTable
   * @throws Exception 
   *
   *
   */
  // ------------------------------------------
  private void loadMappingTable() throws Exception {
    

    String resourcePrefix = null;
    String   fullFileName = null;
    BufferedReader     in = null;
    
    if ( mMapping == null ) {
    
    resourcePrefix = U.getClassPathToResources();
    
    mMapping = new HashMap<String, String>(1200);
    mMapping2 = new HashMap<String,String>(1200);
    fullFileName = resourcePrefix + "/resources/com/ciitizen/framework/symptoms/justSymptoms.sorted";
    
    try {
      in = new BufferedReader( new FileReader( fullFileName));
    } catch (Exception e) {
      String msg = "Issue trying to read " + fullFileName + " \n" + e.getMessage() + "\n" + U.getStackTrace(e);
      GLog.println(GLog.ERROR_LEVEL, CategorizeSymptom.class, "loadMappingTable", msg );
      in.close();
      throw e;
    }
    
    String line = null;
    while ( (line = in.readLine() )!= null ) {
      if ( line != null  && line.length( )> 2) {
      try {
      //  System.err.println( "|" + line + "|");
      String[] cols = U.split(line);
      String cui = cols[0];
      String generalizedCode = cols[1];
      String icdCode = cols[2];
      mMapping.put( cui, generalizedCode);
      mMapping2.put( cui, icdCode);
      } catch ( Exception e) {}
      }
      
    } // end loop through lines
    
    in.close();
    }
    
    // End Method loadMappingTable() -----------------------
  }

  // ----------------------------------------------
  /**
   * generalize
   * 
   * @param categoryCode
   */
  // ----------------------------------------------
  public  String generalize(String mappingCode) {
   /* 
    780 General Symptoms
    781 Symptoms involving nervous and musculoskeletal systems
    782 Symptoms involving skin and other integumentary tissue
    783 Symptoms concerning nutrition, metabolism, and development
    784 Symptoms involving head and neck
    785 Symptoms involving cardiovascular system
    786 Symptoms involving respiratory system and other chest symptoms
    787 Symptoms involving digestive system
    788 Symptoms involving urinary system
    789 Other Symptoms involving abdomen and pelvis
    790 Nonspecific findings on examination of blood
    791 Nonspecific findings on examination of urine
    792 Nonspecific abnormal findings in other body substances
    793 Nonspecific (abnormal) findings on radiological and other examination of body structure
    794 Nonspecific abnormal results of function studies
    795 Other and nonspecific abnormal cytological, histological, immunological and DNA test findings
    796 Other nonspecific abnormal findings
    797 Senility without mention of psychosis
    798 Sudden death, cause unknown
    799.0 Asphyxia and hypoxemia
    799.1 Respiratory arrest
    799.2 Signs and symptoms involving emotional state
    799.3 Debility, unspecified
    799.4 Cachexia
    799.5 Signs and symptoms involving cognition
    */
    String returnValue = null;
    String generalizedCode = generalizeCode( mappingCode);
    
    if ( generalizedCode != null )
    
      returnValue = mGeneralization.get( generalizedCode );
    
    return returnValue ;
    
  } // end Method generalize() --------------------

  // ----------------------------------------------
  /**
   * generalize returns the trunkated* icd code given 
   * a full icd code.
   * 
   * @param mappedCode
   * @return
   */
  // ----------------------------------------------
  private String generalizeCode(String mappedCode) {
    
    String code = mappedCode;
   
    if ( code != null ) {
      if ( code.indexOf("-") < 1 ) {  //  codes with - are already generalized
        int point = code.indexOf(".");
          if ( point > 2 ) {
            if ( code.startsWith("799")) {
              code = code.substring(0, point + 2);
             }
            else
              code = code.substring( 0, point);
          } 
        }
    }
      
    
    return code;
  } // end Method generalize() --------------------

  // ----------------------------------------------
  /**
   * categorize
   * 
   * @param cuiz
   * @return
   */
  // ----------------------------------------------
  public List<String> categorize(String cuiz) {
    
    ArrayList<String> returnCode = new ArrayList<String>();
    String cuis[] = U.split(cuiz, ":");
    if ( cuis != null )
    for ( String cui: cuis) {
      String mappingCode = mMapping.get(cui) ;
      if ( mappingCode != null )
        returnCode.add( mappingCode );
    }
    
    return returnCode;
  } // end Method categorize() --------------------

//----------------------------------------------
  /**
   * getMappingCode
   * 
   * @param cuiz
   * @return
   */
  // ----------------------------------------------
  public List<String> getMappingCode(String cuiz) {
    
    ArrayList<String> returnCode = new ArrayList<String>();
    String cuis[] = U.split(cuiz, ":");
    if ( cuis != null )
    for ( String cui: cuis) {
      String mappingCode = mMapping.get(cui) ;
      if ( mappingCode != null )
        returnCode.add( mappingCode );
    }
    
    return returnCode;
  } // end Method categorize() --------------------
//----------------------------------------------
  /**
   * getMappingCode
   * 
   * @param cuiz
   * @return
   */
  // ----------------------------------------------
  public List<String> getMappingCode2(String cuiz) {
    
    ArrayList<String> returnCode = new ArrayList<String>();
    String cuis[] = U.split(cuiz, ":");
    if ( cuis != null )
    for ( String cui: cuis) {
      String mappingCode = mMapping2.get(cui) ;
      if ( mappingCode != null )
        returnCode.add( mappingCode );
    }
    
    return returnCode;
  } // end Method categorize() --------------------
  
  // ----------------------------------------------
  /**
   * main
   * 
   * @param args
   */
  // ----------------------------------------------
  public static void main(String[] args) {
    
 
    try {
 
      GLog.set(args);
      
      CategorizeSymptom cs = new CategorizeSymptom( "resources/com/ciitizen/framework/symptoms/justSymptoms.sorted",
          "resources/com/ciitizen/framework/symptoms/symptomGeneralizations.txt");
      
     
      
      // ---------------------------------------------
      // 
      // ---------------------------------------------
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      String cui = null;
      List<String> categories = null;
      while (( cui = in.readLine()) != null ) {
        categories = cs.categorize( cui );
  
        if ( categories != null)  
         for ( String category : categories)  
          System.err.println( cui + "|" + category );
       else
         System.err.println( cui + "|NOT FOUND");
        
        
      } // end loop through input
      
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    
    
    
  } // end Method main() --------------------------
  

  // ----------------------------------------------
  // Class Variables
  // ----------------------------------------------
  private static HashMap<String, String>        mMapping = null;
  private static HashMap<String, String>        mMapping2 = null;
  private static Hashtable<String, String> mGeneralization = null;

} // end Class CategorizeSymptom() --------------
