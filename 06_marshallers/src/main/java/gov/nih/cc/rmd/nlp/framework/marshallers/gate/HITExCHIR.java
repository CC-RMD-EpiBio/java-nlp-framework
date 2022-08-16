// =================================================
/**
 * HITExCHIR is a class that makes the mappings between HITEX and CHIR labels.  
 * This should read the mappings from a file so that the mappings are not
 * hard coded.
 * 
 * This is really the first rev, and shouldn't be taken as the final way to
 * inter-operate between the HITEX and CHIR labels. 
 * 
 * Alternative ways are to use the mapping xml file that can be passed between
 * gate and UIMA.  I didn't like that way too much.  
 * 
 * The CHIR labels come from the gov.va.chir.model.xml uima type descriptor.
 * The HITEX labels come from the gov.va.research.v3nlp.common.AnnotationConstants.java
 * 
 * The mapping table is in resources/VinciNLPFramework/chir_mappings/chir_mappings.mappings
 * 
 *
 *
 * @author  Guy Divita 
 * @created Nov 15, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.gate;

import gov.nih.cc.rmd.nlp.framework.utils.U;

import java.io.BufferedReader;
import java.util.Hashtable;


public class HITExCHIR {

  // -----------------------------------------
  /**
   * hitex2ChirLabel returns the chir label given a hitex label 
   * if the mapping exits.
   * 
   * @param hitexLabel
   * @return String  (null if the mapping does not exist)
   */
  // -----------------------------------------
  public static String hitex2ChirLabel(String hitexLabel) {
    String returnValue = null;
    
    if (hitexChirMappingTable == null )
      loadChirHitexMappingTable();
    returnValue = hitexChirMappingTable.get(hitexLabel);
    
    return returnValue;
  } // end Method hitex2ChirLabel

  // -----------------------------------------
  /**
   * chir2HitexLabel returns the hitex label given a chir label 
   * if the mapping exits.
   * 
   * @param chirLabel
   * @return String (null if the mapping does not exist)
   */
  // -----------------------------------------
  public static String chir2HitexLabel(String chirLabel) {
    
    String returnValue = null;
    
    if (chirHitexMappingTable == null )
      loadChirHitexMappingTable();
    returnValue = chirHitexMappingTable.get(chirLabel);
    
    return returnValue;
  } //end Method chir2HitexLabel
  

  // -----------------------------------------
  /**
   * chir2HitexFeatureName Given a chirLabel and a featureName, this method will return
   * the hitex feature name
   * 
   * @param pChirLabel
   * @param featureName
   * @return String
   */
  // -----------------------------------------
  public static String chir2HitexFeatureName(String pChirLabel, String pFeatureName) {
  
    String returnedLabelAndFeature = null;
    String returnedFeatureName = null;
    
    if (chirHitexMappingTable == null )
      loadChirHitexMappingTable();
    String labelAndFeature = pChirLabel + "|" + pFeatureName;
    returnedLabelAndFeature = chirHitexMappingTable.get(labelAndFeature);
    
    if ( returnedLabelAndFeature != null ) {
      String cols[] = U.split( returnedLabelAndFeature);
      returnedFeatureName = cols[1];
    }
    
    return returnedFeatureName;
  } // end Method chir2HitexFeatureName() ---------
  
  // -----------------------------------------
  /**
   * Hitex2ChirFeatureName Given a hitexLabel and a featureName, this method will return
   * the chir feature name
   * 
   * @param pHitexLabel
   * @param featureName
   * @return String
   */
  // -----------------------------------------
  public static String hitex2ChirFeatureName(String pHitexLabel, String pFeatureName) {
  
    String returnedLabelAndFeature = null;
    String returnedFeatureName = null;
    
    if (hitexChirMappingTable == null )
      loadChirHitexMappingTable();
    String labelAndFeature = pHitexLabel + "|" + pFeatureName;
    returnedLabelAndFeature = hitexChirMappingTable.get(labelAndFeature);
    
    if ( returnedLabelAndFeature != null ) {
      String cols[] = U.split( returnedLabelAndFeature);
      returnedFeatureName = cols[1];
    }
    
    return returnedFeatureName;
  } // end Method hitex2chirFeatureName() ---------
  
  
  
  // -----------------------------------------
  /**
   * loadChirHitexMappingTable loads the mapping table
   * 
   */
  // -----------------------------------------
  private static void loadChirHitexMappingTable() {
    
    chirHitexMappingTable = new Hashtable<String,String>(40);
    hitexChirMappingTable = new Hashtable<String,String>(40);
    // location of the mapping table
    String relativeFilePath = "resources/vinciNLPFramework/chir_mappings/chir_hitex.mappings";
    
    try {
      BufferedReader in = U.getClassPathResource(relativeFilePath);
      String line = null;
      
      while ( (line=in.readLine() )!= null ) {
        if ( !line.startsWith("#")) { // ignore comments
          String [] cols = U.split(line);
          if ( cols.length == 2) { // labels only
            String chirLabel = cols[0].trim();
            String hitexLabel = cols[1].trim();
            
            chirHitexMappingTable.put(chirLabel, hitexLabel);
            hitexChirMappingTable.put(hitexLabel, chirLabel);
          } // end labels only
          
          else if ( cols.length == 4) {
            String        chirLabel = cols[0].trim();
            String  chirFeatureName = cols[1];
            if ( chirFeatureName != null  && chirFeatureName.length() > 0 ) 
              chirFeatureName = chirFeatureName.trim();
            
            String       hitexLabel = cols[2].trim();
            String hitexFeatureName = cols[3];
            if ( hitexFeatureName != null  && hitexFeatureName.length() > 0 )
              hitexFeatureName = hitexFeatureName.trim();
            
            String chirLabelAndFeature = chirLabel + "|" + chirFeatureName;
            String hitexLabelAndFeature = hitexLabel + "|" + hitexFeatureName;
            chirHitexMappingTable.put(chirLabelAndFeature, hitexLabelAndFeature);
            hitexChirMappingTable.put(hitexLabelAndFeature, chirLabelAndFeature);
          } // end labels and features 
          
        } // end non comments
      } // end loop through lines of mapping
      
      
    } catch (Exception e) {
      
      e.printStackTrace();
      System.err.println("Could not load the chir_hitex label mappings expected at "  + relativeFilePath + " " + e.toString());
    }
    
  } // end Method loadChirHitexMapppingTable() --

  // -----------------------------
  // Class Variables
  // -----------------------------
  private static Hashtable<String,String> chirHitexMappingTable = null;
  private static Hashtable<String,String> hitexChirMappingTable = null;
  
  
  
} // end Class HITExCHIR class
