// =================================================
/**
 * ThreadedCaculateLookAheadAux 
 *
 * @author     Guy Divita
 * @created    May 16, 2021
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies.lookup;

import java.util.Enumeration;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;

/**
 * @author Guy
 *
 */
public class ThreadedCaculateLookAheadAux  implements Runnable {



  private Enumeration<String> keys;
  private TermLookupG parentClass;

  // =================================================
  /**
   * Constructor
   *
   * @param pKeys
   * @param termLookupG
   * 
  **/
  // =================================================
  public ThreadedCaculateLookAheadAux(Enumeration<String> pKeys, TermLookupG pParentClass ) {
    this.keys = pKeys;
    this.parentClass = pParentClass;
    
  } // end Constructor() -----------------------------

  // =================================================
  /**
   * run
   * 
  */
  // =================================================
  @Override
  public void run() {
    
    try {
      this.parentClass.calculateLookAhead(  this.keys );
      Thread.currentThread().interrupt();
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "run", " issue calculating the look ahead :" + e.toString());
    }
    
  }

}
