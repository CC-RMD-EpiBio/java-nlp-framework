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
// =======================================================
/**
 * GraphTuple is a container that holds a key, frequency and direction
 *
 * @author  guy
 * @created Sep 17, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils.nGrams;

/**
 * @author guy
 *
 */
public class GraphTuple {

  public String key = null;
  public int frequency = 0;
  public String direction = null;
  // =======================================================
  /**
   * Constructor GraphTuple 
   *
   * @param pKey
   * @param pFrequency
   * @param pDirection
   */
  // =======================================================
  public GraphTuple(String pKey, int pFrequency, String pDirection) {
   this.key = pKey;
   this.frequency = pFrequency;
   this.direction = pDirection;
  }

  // =======================================================
  /**
   * Constructor GraphTuple 
   *
   * @param pKey
   * @param pFrequency
   * @param pDirection
   */
  // =======================================================
  public GraphTuple(String pKey, String pFrequency, String pDirection) {
    
    this.key = pKey;
    this.frequency = Integer.parseInt(pFrequency.trim());
    this.direction = pDirection;
  }

  /**
   * @return the direction
   */
  public String getDirection() {
    return direction;
  }

  /**
   * @param direction the direction to set
   */
  public void setDirection(String direction) {
    this.direction = direction;
  }

  /**
   * @return the key
   */
  public String getKey() {
    return key;
  }

  /**
   * @param key the key to set
   */
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * @return the frequency
   */
  public int getFrequency() {
    return frequency;
  }

  /**
   * @param frequency the frequency to set
   */
  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "GraphTuple [key=" + key + ", frequency=" + frequency + ", direction=" + direction + "]";
  }


 
  

  

}
