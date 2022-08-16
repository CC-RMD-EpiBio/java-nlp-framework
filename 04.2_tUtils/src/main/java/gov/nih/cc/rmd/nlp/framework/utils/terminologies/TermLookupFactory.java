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
 * TermLookupFactory.java  Summary [TBD]
 *
 * @author     Guy Divita
 * @created    May 18, 2018
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies;

import java.io.File;

import gov.nih.cc.rmd.nlp.framework.utils.terminologies.lookup.TermLookupG;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.lookup.TermLookupLocalTermsImpl;
import gov.nih.cc.rmd.nlp.framework.utils.terminologies.lookup.TermLookupSimpleImpl;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * A factory for creating TermLookup objects.
 *
 * @author guy
 */
public class TermLookupFactory {

  // =================================================
  /**
   * getTermLookup.
   *
   * @param pTerminologyFilez the terminology filez
   * @return TermLookupInterface
   * @throws Exception the exception
   */
  // =================================================
  public static TermLookupInterface getTermLookup(String pTerminologyFilez) throws Exception {

    String[] terminologyFiles = U.split(pTerminologyFilez, ":");
    TermLookupInterface returnVal = getTermLookup(terminologyFiles, 1000);

    return returnVal;

  }

  // =================================================
  /**
   * getTermLookup.
   *
   * @param pArgs the args
   * @return TermLookupLocalCoreInterface
   * @throws Exception the exception
   */
  // =================================================
  public static final TermLookupInterface getTermLookup(String[] pArgs) throws Exception {

    TermLookupInterface returnVal = null;

    String terminologyFilez = U.getOption(pArgs, "--localTerminologyFiles=", "");
    int numberOfRecords = Integer.parseInt(U.getOption(pArgs, "--numberOfSophiaKeys=", "1000"));
    String[] terminologyFiles = U.split(terminologyFilez, ":");

    returnVal = getTermLookup(terminologyFiles, numberOfRecords);

    return returnVal;

  } // End Method getTermLookup() ---------------------

  // =================================================
  /**
   * getTermLookup.
   *
   * @param pImplementation the implementation
   * @return TermLookupInterface
   * @throws Exception the exception
   */
  // =================================================
  public static TermLookupInterface getTermLookupSimple(String pImplementation) throws Exception {

    TermLookupInterface returnVal = null;

    switch (pImplementation) {

     
      case "gov.nih.cc.rmd.framework.simple":
        returnVal = new TermLookupSimpleImpl();
        break;
      case "gov.nih.cc.rmd.framework":
      default:
        returnVal = new TermLookupLocalTermsImpl();
        break;

    }

    return returnVal;
  } // end getTermLookup() ---------------------------

  // =================================================
  /**
   * getTermLookup.
   *
   * @param pTerminologyFiles the terminology files
   * @param pNoRecords the no records
   * @return TermLookupLocalCoreInterface
   * @throws Exception the exception
   */
  // =================================================
  public static final TermLookupInterface getTermLookup(String[] pTerminologyFiles, int pNoRecords)
    throws Exception {

    TermLookupInterface returnVal = null;

    int numberOfLocalTerminologies = getNumberOfLocalTerminologies(pTerminologyFiles);

    // if (numberOfLocalTerminologies == 0) {
    //
    // Class<?> clazz = Class
    // .forName("gov.nih.cc.rmd.framework.utils.terminologies.ciitizen.TermLookupCiitizenImpl");
    // returnVal = (TermLookupInterface) clazz.newInstance();
    // returnVal.init(pTerminologyFiles);
    // } else
    
    
    
    if (numberOfLocalTerminologies > 0) {
      boolean useMe = true;
      for ( String anLRAGRFile : pTerminologyFiles )
        if ( anLRAGRFile.toLowerCase().contains("mrconso")) {
          returnVal = new TermLookupG();   // this version uses a different configuration of the lexRecords than what's needed for text lookup
          useMe = false;
          break;
        }
      
      if ( useMe ) 
        returnVal = new TermLookupLocalTermsImpl();
     
      returnVal.init(pTerminologyFiles, pNoRecords);
      
      
      
      // Class<?> clazz = Class
      //    .forName("gov.nih.cc.rmd.framework.utils.terminologies.lookup.TermLookupLocalTermsImpl");
      // returnVal = (TermLookupInterface) clazz.newInstance();
    

    }

    return returnVal;
  }

  // =================================================
  /**
   * getNumberOfLocalTerminologies returns the number of terminologies
   * referenced that are not urls.
   * 
   * They will either be absolute paths or relative paths that start with
   * /resource
   *
   * @param pTerminologyFiles the terminology files
   * @return int
   */
  // =================================================
  private static int getNumberOfLocalTerminologies(String[] pTerminologyFiles) {

    int returnVal = 0;
    if (pTerminologyFiles != null && pTerminologyFiles.length > 0)
      for (int i = 0; i < pTerminologyFiles.length; i++) {
        if (pTerminologyFiles[i].startsWith("resource"))
          returnVal++;
        else if (pTerminologyFiles[i].startsWith("/")) {
          File aFile = new File(pTerminologyFiles[i]);
          if (aFile.exists())
            returnVal++;
        }
      }
    return returnVal;
  } // end Method getTermLookup() -------------------

} // end Class TermLookupFactory() ------------------
