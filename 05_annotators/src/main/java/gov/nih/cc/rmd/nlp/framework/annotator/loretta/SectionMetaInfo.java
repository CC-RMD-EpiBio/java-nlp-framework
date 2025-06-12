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
 * SectionMetaInfo is a resource class that keeps 
 * track of what sections hold what clinical information
 * in terms of what labels to look for and create.
 * 
 * Medications in medication sections,  problems, symptoms, findings
 * in chief complaint sections ...
 * 
 * It is driven by a the resource resources/com/ciitizen/framework/sections/sectionInfo.txt
 * which has normalizedSectionName|sectionName|AnnotationLabel|  other info
 *
 * @author     Guy Divita
 * @created    Jul 10, 2018
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.annotator.loretta;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * @author guy
 *
 */
public class SectionMetaInfo {

  // =================================================
  /**
   * Constructor
   *
   * @param pResourceFile
   * @throws IOException
  **/
  // =================================================
  public SectionMetaInfo( ) throws IOException {
    
    String resourceFile = "resources/com/ciitizen/framework/sections/ccdaSectionHeaders.lragr";
    
    try {
      
      loadSectionInfo( resourceFile);
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue reading in " + resourceFile + " " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "constructor", msg );
      throw new IOException( msg);
      
    }
  } // end Constructor() ----------------------------

  
  
  // =================================================
  /**
   * Constructor
   *
   * @param pResourceFile
   * @throws IOException
  **/
  // =================================================
  public SectionMetaInfo(String pResourceFile) throws IOException {
    
    try {
      
      loadSectionInfo( pResourceFile);
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue reading in " + pResourceFile + " " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "constructor", msg );
      throw new IOException( msg);
      
    }
  } // end Constructor() ----------------------------

  // =================================================
  /**
   * isValidSectionFor a crude way to limit what annotations come out of what sections.
   * 
   *   the section passed in is matched against general section patterns (does it have "medication" in
   *   the name of the section, does it have "finding" in it.
   *   
   *   Each of these patterns has a set of allowable Labels.  This means that the section resource list needs to
   *   be updated when there is a new label or annotator. 
   * 
   * @param pSectionName
   * @param pAnnotationType
   * @return boolean
  */
  // =================================================
  public boolean isValidSectionFor(String pSectionName, String pAnnotationType) {
   
    boolean returnVal = false;
    
    if ( pSectionName != null && !pSectionName.isEmpty() && !pSectionName.contains("unknown") && !pSectionName.contains("Unlabeled Section")) {
    
     String key = pSectionName.toLowerCase();
     String possibleAnnotationTypes = this.possibleAnnotationTypes.get(  key );
     if ( possibleAnnotationTypes != null )
       if ( possibleAnnotationTypes.contains( pAnnotationType)  || possibleAnnotationTypes.contains("all"))
         returnVal = true; 
   
    }
    return returnVal;
  } // end Method isValidSectionFor() 
  


  // =================================================
  /**
   * loadSectionInfo
   * 
   * @param pResourceFile
   * @throws Exception
  */
  // =================================================
  private void loadSectionInfo(String pResourceFile)  throws Exception {
    
  
    String[] rows = U.readClassPathResourceIntoStringArray(pResourceFile );
  
   
    if ( rows != null ) {
   
      this.possibleAnnotationTypes = new HashMap<String,String>( rows.length);
      
      for ( String row : rows ) {
        if ( row != null && !row.startsWith("#") && row.trim().length() > 0  ) {
          
          String cols[] = U.split(row);
          String key = cols[1].toLowerCase();
          // String sectionType = cols[6];
          String annotationTypes = cols[7];
    
          this.possibleAnnotationTypes.put(key, annotationTypes);
        }
      }
    }
  }  // end Method loadSectionInfo() 
  
  

  // ------------------------------
  // Class variables
  // ------------------------------
 
  HashMap<String, String> possibleAnnotationTypes = null;
  static String[] sectionPatternList = null;
  
  
} // end Class loadSectionInfo() ---------------------
