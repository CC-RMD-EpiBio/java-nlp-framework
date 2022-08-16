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
