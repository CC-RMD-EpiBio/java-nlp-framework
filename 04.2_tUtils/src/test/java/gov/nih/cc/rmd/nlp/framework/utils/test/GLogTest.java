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
 * GLogTest.java  Summary [TBD]
 *
 * @author     Guy Divita
 * @created    Jun 8, 2018
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils.test;

import org.junit.Test;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;



/**
 * GLog test.
 */
public class GLogTest   {

  /**
   * Test.
   */
  @Test
  public void test() {

    GLog.println("an information messge ");
    GLog.println(GLog.ERROR_LEVEL, "an error message");
    GLog.println(GLog.ERROR_LEVEL, GLog.class, "main", "anoother way to give an error msgs ");
    GLog.println(GLog.DEBUG_LEVEL, "a debug message");
    GLog.println(GLog.STD___LEVEL, GLog.class, "main", "anoother way to give an std msgs ");
    GLog.println(GLog.INFO_LEVEL, GLog.class, "main", "anoother way to give an info msgs ");
    GLog.println(GLog.WARNING_LEVEL, GLog.class, "main", "anoother way to give an warning msgs ");

  }
}
