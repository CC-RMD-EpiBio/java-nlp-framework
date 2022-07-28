// =================================================
/**
 * TermLookupGAux indexes an lragr file
 * @author     Guy Divita
 * @created    May 16, 2021
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies.lookup;

import java.io.BufferedReader;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * @author Guy
 *
 */
public class TermLookupGAux implements Runnable {

  private TermLookupG parentClass = null;
  private String fileName = null;

  // =================================================
  /**
   * Constructor
   * @param pParentClass 
   * @param pFileName 
   *
   * 
  **/
  // =================================================
  public TermLookupGAux(String pFileName, TermLookupG pParentClass ) {
     this.parentClass = pParentClass;
     this.fileName = pFileName ;
  }

  // =================================================
  /**
   * main 
   * 
   * @param args
  */
  // =================================================

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

  // =================================================
  /**
   * index 
   * 
   * @param fileName
  */
  // =================================================
  public void index(String fileName) throws Exception   {
   
    
    
    try (final BufferedReader in = U.getInputStreamFromFileOrResource(fileName);) {
      GLog.println(GLog.INFO_LEVEL, this.getClass(), "indexx", "Reading in local terminology file |" + fileName + "|");

      String line = null;
      while ((line = in.readLine()) != null) {
        if (line != null && !line.startsWith("#") && line.length() > 4) {
          this.parentClass.index(line);
        }
      } // end loop through the lines
      
      
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception (GLog.println( GLog.ERROR_LEVEL, this.getClass(), "run" , "Issue in the thread running termLookupG " + e.toString()));
    }

    
  } // end Method index() --------------------------

 //=================================================
 /**
  * run 
  * 
 */
 // =================================================
  @Override
  public void run() {
    
    try {
      this.index( this.fileName);
      
      Runtime.getRuntime().gc();
      Thread.currentThread().interrupt();
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println( GLog.ERROR_LEVEL, this.getClass(), "run" , "Issue in the thread running termLookupG " + e.toString());
    }
    
    
  } // end Method run() ----------------------------

} // end Class TermIndexG() ------------------------
