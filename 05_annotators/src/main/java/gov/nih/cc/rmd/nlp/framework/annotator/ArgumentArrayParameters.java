package gov.nih.cc.rmd.nlp.framework.annotator;
// =======================================================
/**
 * ArgumentArrayParameters is a container to hold argument parameters
 *
 * @author  guy
 * @created Jan 19, 2014
 *
   
 */
// =======================================================

/**
 * @author guy
 *
 */
public class ArgumentArrayParameters {

  private Object argument1 = null;
  private Object argument2 = null;
  private Object argument3 = null;
  private Object argument4 = null;
  

  // =======================================================
  /**
   * Constructor ArgumentArrayParameters 
   *
   * @param conceptText
   * @param sentenceText
   * @param aConcept
   */
  // =======================================================
  public ArgumentArrayParameters(Object pArg1, Object pArg2, Object pArg3) {
    this.argument1 = pArg1;
    this.argument2 = pArg2;
    this.argument3 = pArg3;
  } // end Constructor

  /**
   * @return the argument1
   */
  public Object getArgument1() {
    return argument1;
  }

  /**
   * @param argument1 the argument1 to set
   */
  public void setArgument1(Object argument1) {
    this.argument1 = argument1;
  }

  /**
   * @return the argument2
   */
  public Object getArgument2() {
    return argument2;
  }

  /**
   * @param argument2 the argument2 to set
   */
  public void setArgument2(Object argument2) {
    this.argument2 = argument2;
  }

  /**
   * @return the argument3
   */
  public Object getArgument3() {
    return argument3;
  }

  /**
   * @param argument3 the argument3 to set
   */
  public void setArgument3(Object argument3) {
    this.argument3 = argument3;
  }

  /**
   * @return the argument4
   */
  public Object getArgument4() {
    return this.argument4;
    
    // End Method getArgument4() ======================
  }
  
  /**
   * @param argument4 the argument4 to set
   */
  public void setArgument4(Object argument4) {
    this.argument4 = argument4;
  }
  
} // end Class ArgumentArrayParameters
