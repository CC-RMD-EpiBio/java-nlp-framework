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
 * OffsetTest - creates a file with a \r line followed by lines terminated
 * with \r\n
 * 
 * @author  Guy Divita 
 * @created Jun 3, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.knowtator.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class OffsetTest {

  public static void main(String[] args ) {
    
    
    try {
      PrintWriter out = new PrintWriter("/data/vhaslcdivitg/data/offsetTest/sample1.txt");
      
      out.print("Title\r");
      out.print("       line 1 with some foley cath\r\n");
      out.print("       line 2 with some side pain\r\n");
      out.print("       line 3 with some fever\r\n");
      out.close();
    
    
    
    
    
    
    
    
    
    
    
    
    
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      String msg = "Issue with TODO \n" + e.getMessage() ;
       System.err.println( msg );
      
    }
    
    
    
    
  }
 

  // -----------------------------------------
  // Global Variables
  // -----------------------------------------
  
   
  
} // end Class OffsetTest
