// =================================================
/**
 * GLvgUtils.java  Summary [TBD]
 *
 * @author     Guy Divita
 * @created    Sep 23, 2020
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.lexUtils;

/**
 * @author divitag2
 *
 */
public class GLvgUtils {

  // =================================================
  /**
   * parseLvgOutput 
   * 
   * @param pLvgAPIOutput
   * @return LvgOutputStructure
  */
  // =================================================
  public final static LvgOutputStructure parseLvgOutput(String pLvgAPIOutput) {
    
//    Economic|economic|<adj>|<base>|v|1|1|1|n|0|3|
    
//   Factors|Factors|<all>|n|2|none|
//    inputTerm, Variant, pos, infl, --------, history, cost   
    LvgOutputStructure returnVal = new LvgOutputStructure() ;
    
    try {
    
    String cols[] = U.split(  pLvgAPIOutput );
    
    returnVal.setInputTerm( cols[0]);
    
    if ( cols[1].contentEquals("-No Output-"))
      return null ;
    
    returnVal.setVariant(  cols[1]);
    returnVal.setPos( cols[2]);
    returnVal.setInfl(cols[3]);
    if ( cols.length > 9) {
      returnVal.setHistory(cols[8]);
      returnVal.setCost( cols[9]);
    } else {
      returnVal.setHistory("n");
      returnVal.setCost("0");
    }
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue parsing the lvg output " + e.toString());
    }
    
    return returnVal; 
  } // end Method LvgOutputStructure() --------------

} // end Class GLvgUtils() ---------------------------
