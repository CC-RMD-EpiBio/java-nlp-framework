// =================================================
/**
 * LvgOutputStructure is a container for LVG's output
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
public class LvgOutputStructure {

  // =================================================
  /**
   * Constructor
   *
   * 
  **/
  // =================================================
  public LvgOutputStructure() {
   
    
  } // end Constructor() -----------------------------
  

  // =================================================
  /**
   * Constructor
   *
   * @param pLvgOutput
  **/
  // =================================================
  public LvgOutputStructure(String pLvgOutput) {
   
    
  } // end Constructor() -----------------------------
  
  // =================================================
  /**
   * Constructor
   *
   * @param inputTerm
   * @param variant
   * @param pos
   * @param infl
   * @param history
   * @param cost
   * 
  **/
  // =================================================
  public LvgOutputStructure(String pInputTerm, String pVariant, String pPos, String pInfl, String pHistory, String pCost) {
   
    this.inputTerm = pInputTerm;
    this.variant = pVariant;
    this.pos = pPos;
    this.infl = pInfl;
    this.history = pHistory;
    this.cost = pCost;
  } // end Constructor() ----------------------------
  
  
  /**
   * @return the inputTerm
   */
  public final String getInputTerm() {
    return inputTerm;
  }
  /**
   * @param inputTerm the inputTerm to set
   */
  public final void setInputTerm(String inputTerm) {
    this.inputTerm = inputTerm;
  }
  /**
   * @return the variant
   */
  public final String getVariant() {
    return variant;
  }
  /**
   * @param variant the variant to set
   */
  public final void setVariant(String pVariant) {
    this.variant = pVariant;
  }
  /**
   * @return the pos
   */
  public final String getPos() {
    return pos;
  }
  /**
   * @param pos the pos to set
   */
  public final void setPos(String pos) {
    this.pos = pos;
  }
  /**
   * @return the infl
   */
  public final String getInfl() {
    return infl;
  }
  /**
   * @param infl the infl to set
   */
  public final void setInfl(String infl) {
    this.infl = infl;
  }
  /**
   * @return the history
   */
  public final String getHistory() {
    return history;
  }
  /**
   * @param history the history to set
   */
  public final void setHistory(String history) {
    this.history = history;
  }
  /**
   * @return the cost
   */
  public final String getCost() {
    return cost;
  }
  /**
   * @param cost the cost to set
   */
  public final void setCost(String cost) {
    this.cost = cost;
  }
  // Container Variables
  private String inputTerm;
  private String variant;
  private String pos;
  private String infl;
  private String history;
  private String cost;
  

}
