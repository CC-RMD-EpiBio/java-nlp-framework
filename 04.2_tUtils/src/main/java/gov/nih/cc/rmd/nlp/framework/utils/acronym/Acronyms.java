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
 * Acronyms includes methods that recognize by lookup
 * and by pattern acronyms. 
 *
 *
 * @author  Guy Divita 
 * @created Feb 21, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils.acronym;



import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import gov.nih.cc.rmd.nlp.framework.utils.U;




public class Acronyms {
//-----------------------------------------
  /**
   * Constructor 
   *   This constructor requires the path of
   *   a resource that points to a list of
   *   known acronyms.  This resource should
   *   be point to resources/tokenizer/knownAcronyms.txt
   *   and be referenced by "knownAcronymsFile" in
   *   the parameters configuration. 
   * 
   * @param pFileName
   * @throws ResourceInitializationException 
   */
  // -----------------------------------------
  public Acronyms ( String pFileName ) throws Exception {
   
    loadKnownAcronyms( pFileName);
  } // end Constructor() ---------------------
  
//-----------------------------------------
  /**
   * Constructor 
   *   This constructor assumes the existance
   *   of the file  resources/tokenizer/knownAcronyms.txt

   * @throws Exception 
   */
  // -----------------------------------------
  public Acronyms ( ) throws Exception {
   
    String fileName = "resources/com/ciitizen/framework/tokenizer/knownAcronyms.txt";
    
    loadKnownAcronyms( fileName);
  } // end Constructor() ---------------------
  
  
  
  // -----------------------------------------
  /**
   * isAcronym returns true if the string looks like an acronym.
   * This method relies on a lookup table, and if that fails,
   * the following patterns:
   *     multiple periods
   *     Initial case, less than 4 letters, ending with a period
   *     All caps, ending with a period.
   * 
   * 
   * @param pValue
   * @param charsToRight 
   * @return
   */
  // -----------------------------------------
  public boolean isAcronym(String pValue, String charsToRight) {
    boolean returnValue = false;
    char lastChar, firstChar;
    int len = 0;
     
    
    if ( pValue != null ) {
     len =  pValue.length();
     if ( len > 1) {
       firstChar = pValue.charAt(0);
       lastChar = pValue.charAt(pValue.length() - 1 );
  
       
       if      (isKnownAcronym ( pValue ) )                                                                               returnValue = true;
       if      (U.hasMultiplePeriods (pValue))                                                                            returnValue = true;
       else if ((lastChar == '.' ) && (len < 4 ) && Character.isUpperCase(firstChar ) && !isNewSentence( charsToRight) )   returnValue = true;
       else if ( U.isAllCaps(pValue ) && len < 5 && !U.containsPunctuation(pValue))                                        returnValue = true;
     } // end if the string is not long enough
    } // end if pValue is not empty
    return returnValue;
  } // end Method isAcronym() ----------------

  // ------------------------------------------
  /**
   * isNewSentence
   *    looks for the next chars to see if the next
   *    chars start with a new line or an upper case.
   *
   * @param charsToRight
   * @return
   */
  // ------------------------------------------
  private boolean isNewSentence(String charsToRight) {
  
    boolean returnVal = false;
    
    if (charsToRight == null || charsToRight.length() == 0 ) 
      returnVal = true;
    else { 
      char buff[] = charsToRight.toCharArray();
      
      for ( int i = 0; i < buff.length; i++ ) {
        if ( buff[i] == ' ' ||
             buff[i] == '\t' ) {
          ;;
        } else if ( buff[i] == '\r' || buff[i] == '\n') {
            returnVal = true;
            break;
          } else if ( buff[i] >= 'A' && buff[i] <= 'Z') {
            returnVal = true;
            break;
          }
          else {
            returnVal = false;
            break;
          }
      } // end loop through charsToRight
        
    } // end 
    
    
    return returnVal;
    
  }  // End Method isNewSentence() -----------------------
  

  // -----------------------------------------
  /**
   * isKnownAcronym returns true if the string passed in is on the list of known acronyms.
   * The list of known acronyms is taken from the resource xxxxxx;
   * 
   * @param pValue
   * @return
   */
  // -----------------------------------------
  public boolean isKnownAcronym(String pValue) {
    boolean returnValue = false;
    
    if ( this.knownAcronyms.get( pValue) != null )
        returnValue = true;
    return returnValue;
  } // end Method isKnownAcronym() -----------

  // --------------------------------------------------------
  /**
   * loadKnownAcronyms loads the known acronyms from the resource url.
   * 
   * @throws ResourceInitialization if loading of patterns fails.  
   */
  // --------------------------------------------------------
  public void loadKnownAcronyms(String pLocation) throws Exception{
      
    if (pLocation == null){
      String message = "Known acronyms file's location is null.";
     
      throw new Exception( message);
    }  
    
    int dummy[] = new int[1];
    dummy[0] = 1;
    
    try {
     
      
      BufferedReader in = U.getClassPathResource( pLocation);
      String line = null;
      this.knownAcronyms = new Hashtable<String, int[]>();
      
      while ((line = in.readLine()) != null) {
        if (line.trim().length() == 0) {
          continue;
        }
        
        if (!line.startsWith("#")) {
          this.knownAcronyms.put(line.trim(), dummy);
                     
        }
      } // end loop through the line of the resource
      in.close();
      
    } catch(FileNotFoundException e){
      e.printStackTrace();
      String msg =   "File not found: '" + pLocation +  "  " + e.toString() ;
      System.err.println(msg);
      throw new FileNotFoundException( msg);
    } catch(IOException e){
        e.printStackTrace();
        String msg =   "I/O Error occured " +  "while reading from file: '" +  pLocation + " " + e.toString();
        System.err.println(msg);
      throw new IOException( msg );
    }
  
  } // end Method loadKnownAcronyms() -------------
  
  // ----------------------------------------------
  // Class Variables
  // ----------------------------------------------
  private Hashtable<String, int[]>  knownAcronyms = null;

  // =======================================================
  /**
   * getAcronymExpansions [Summary here]
   * 
   * @param eui
   * @return
   */
  // =======================================================
  public List<String> getAcronymExpansions(String eui) {
    // TODO Auto-generated method stub
    return null;
    // End Method getAcronymExpansions() ======================
  }

  // =======================================================
  /**
   * getAcronymExpansionEuis [Summary here]
   * 
   * @param eui
   * @return
   */
  // =======================================================
  public List<String> getAcronymExpansionEuis(String eui) {
    // TODO Auto-generated method stub
    return null;
    // End Method getAcronymExpansionEuis() ======================
  }

  // =======================================================
  /**
   * getAcronymOf [Summary here]
   * 
   * @param eui
   * @return
   */
  // =======================================================
  public List<String> getAcronymOf(String eui) {
    // TODO Auto-generated method stub
    return null;
    // End Method getAcronymOf() ======================
  }
 

} // end Class Acronyms() -------------------------
