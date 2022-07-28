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
 * GraphArc is a container to hold arc information including the 
 *   node 1 name
 *   node 2 name
 *   direction of relationship
 *   named relationship (optional
 *   strength of the relationship in absolute terms
 *   strength of the relationship in scaled terms from 1 - 10  
 *   
 *
 * @author  guy
 * @created Sep 18, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils.nGrams;


public class GraphArc {

  public String node1 = null;
  public String node2 = null;
  public String direction = "->";
  public String relationshipName = null;
  public int    strength = 1;
  public int    scaledStrength = 1;
  // =======================================================
  /**
   * Constructor GraphArc 
   *
   * @param pNode1
   * @param pNode2
   * @param pDirection
   * @param pFrequency
   */
  // =======================================================
  public GraphArc(String pNode1, String pNode2, String pDirection, int pFrequency) {
    
    this.node1 = pNode1;
    this.node2 = pNode2;
    this.direction = pDirection;
    this.strength = pFrequency;
    
  } // end Constructor
  /**
   * @return the node1
   */
  public String getNode1() {
    return node1;
  }
  /**
   * @param node1 the node1 to set
   */
  public void setNode1(String node1) {
    this.node1 = node1;
  }
  /**
   * @return the node2
   */
  public String getNode2() {
    return node2;
  }
  /**
   * @param node2 the node2 to set
   */
  public void setNode2(String node2) {
    this.node2 = node2;
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
   * @return the relationshipName
   */
  public String getRelationshipName() {
    return relationshipName;
  }
  /**
   * @param relationshipName the relationshipName to set
   */
  public void setRelationshipName(String relationshipName) {
    this.relationshipName = relationshipName;
  }
  /**
   * @return the strength
   */
  public int getStrength() {
    return strength;
  }
  /**
   * @param strength the strength to set
   */
  public void setStrength(int strength) {
    this.strength = strength;
  }
  /**
   * @return the scaledStrength
   */
  public int getScaledStrength() {
    return scaledStrength;
  }
  /**
   * @param scaledStrength the scaledStrength to set
   */
  public void setScaledStrength(int scaledStrength) {
    this.scaledStrength = scaledStrength;
  }
} // end Calss GraphArc
