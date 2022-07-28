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
 * GramThing container
 *
 * @author  Divita
 * @created Sep 16, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils.nGrams;

import java.util.ArrayList;
import java.util.List;


public class GramThing {

  public List<GraphTuple> tuples = null;
  public String             key = null;
  
  // =======================================================
  /**
   * Constructor GramThing 
   *
   * @param pKey
   */
  // =======================================================
  public GramThing(String pKey) {
   this.key = pKey;
  }

  // =======================================================
  /**
   * addUniq will add only unique (normalized) keys, and combine
   * the frequency counts
   * 
   * @param pTuple
   */
  // =======================================================
  public void addUniq(GraphTuple pTuple) {
    
    boolean found = false;
    if ( this.tuples == null )
      this.tuples = new ArrayList<GraphTuple>();
    for ( GraphTuple tuple : this.tuples ) {
      
      if ( pTuple.getKey().toLowerCase().equals( tuple.getKey().toLowerCase())) {
        tuple.setFrequency(    tuple.getFrequency() + pTuple.getFrequency());
        found = true;
        break;
      }
      
    }
    if ( !found ) 
      this.tuples.add( pTuple);
    
  } // End Method addUniq() ======================

  /**
   * @return the tuples
   */
  public List<GraphTuple> getTuples() {
    return tuples;
  }

  /**
   * @param tuples the tuples to set
   */
  public void setTuples(List<GraphTuple> tuples) {
    this.tuples = tuples;
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
  

  
} // end Class() ----------
