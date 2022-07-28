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
