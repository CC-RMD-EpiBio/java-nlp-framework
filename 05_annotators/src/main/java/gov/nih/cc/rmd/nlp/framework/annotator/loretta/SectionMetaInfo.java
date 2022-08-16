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
