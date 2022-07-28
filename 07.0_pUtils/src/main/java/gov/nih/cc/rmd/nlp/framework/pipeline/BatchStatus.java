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
/*
 *
 */
/**
 * BatchStatus is a container that keeps track of the batch status
 * 
 *
 * @author  divita
 * @created Jan 19, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline;

/**
 * The Class BatchStatus.
 *
 * @author guy
 */
public class BatchStatus {

  /** The filled. */
  private boolean filled = true;

  /** The finished. */
  private boolean finished = false;

  /** The alive. */
  private boolean alive = true;

  /** The available. */
  private boolean available = true;

  // =======================================================
  /**
   * Constructor BatchStatus .
   */
  // =======================================================
  public BatchStatus() {

  } // end Constructor() ----------------

  /**
   * Indicates whether or not available is the case.
   *
   * @return the available
   */
  public boolean isAvailable() {
    return available;
  }

  // =======================================================
  /**
   * finished .
   *
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  // =======================================================
  public boolean isFinished() {

    return this.finished;

    // End Method finished() ======================
  }

  // =======================================================
  /**
   * setFilled .
   *
   * @param pVal the filled
   */
  // =======================================================
  public void setFilled(boolean pVal) {
    this.filled = pVal;
  } // End Method setFilled() ======================

  // =======================================================
  /**
   * getFilled .
   *
   * @return boolean
   */
  // =======================================================
  public boolean getFilled() {
    return this.filled;
  } // End Method getFilled() ======================

  // =======================================================
  /**
   * setFinished .
   *
   * @param pVal the finished
   */
  // =======================================================
  public void setFinished(boolean pVal) {
    this.finished = pVal;
  }// End Method setFinished() ======================

  // =======================================================
  /**
   * alive is true until the pipeline dies.
   *
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  // =======================================================
  public boolean isAlive() {
    return this.alive;
    // End Method alive() ======================
  }

  // =======================================================
  /**
   * setIsAlive.
   *
   * @param pVal the is alive
   */
  // =======================================================
  public void setIsAlive(boolean pVal) {
    this.alive = pVal;
    // End Method setIsAlive() ======================
  }

  // ========================================================
  /**
   * setAvailable.
   *
   * @param available the available to set
   */
  // =======================================================
  public void setAvailable(boolean available) {
    this.available = available;
    this.finished = !available;
  } // End Method setAvailable() ============================

}
