// =======================================================
/**
 * BatchStatus is a container that keeps track of the batch status
 * 
 *
 * @author  divita
 * @created Jan 19, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

/**
 * @author guy
 *
 */
public class BatchStatus {

  private int numberToProcess = 0;
  private int numberProcessed = 0;
  private boolean queueEmpty = true;
  private boolean finished = false;
  private boolean alive = true;
  
  // =======================================================
  /**
   * Constructor BatchStatus 
   *
   * @param size
   */
  // =======================================================
  public BatchStatus(int pNumberToProcess) {
    this.numberToProcess = pNumberToProcess;
  } // end Constructor() ----------------

  // =======================================================
  /**
   * finished 
   * 
   * @return
   */
  // =======================================================
  public boolean isFinished() {
    
   return this.finished;
    
    // End Method finished() ======================
  }

  // =======================================================
  /**
   * add adds one to the numberProcessed
   * 
   */
  // =======================================================
  public void add() {
    this.numberProcessed++;
  }  // End Method add() ======================

  // =======================================================
  /**
   * setEmpty sets the queueEmpty value to true
   * 
   */
  // =======================================================
  public void setEmpty() {
   this.queueEmpty = true;
    // End Method setEmpty() ======================
  }

  // =======================================================
  /**
   * queueFilled sets the queueEmpty value to false
   * @param pNumberInQueue
   * 
   */
  // =======================================================
  public void queueFilled(int pNumberInQueue) {
    this.queueEmpty = false;
    this.numberToProcess = pNumberInQueue;
    this.numberToProcess = 0;
  }  // End Method queueFilled() ======================
  
//=======================================================
 /**
  * isQueueEmpty returns with the queueEmpty value
  * 
  */
 // =======================================================
 public boolean isQueueEmpty() {
  return this.queueEmpty ;
 }  // End Method isQueueEmpty() ======================

// =======================================================
/**
 * setFinished 
 * 
 */
// =======================================================
public  void setFinished(boolean pVal) {
   this.finished = pVal;
}// End Method setFinished() ======================

// =======================================================
/**
 * alive is true until the pipeline dies
 * 
 * @return
 */
// =======================================================
public  boolean isAlive() {
  return this.alive;
  // End Method alive() ======================
}

// =======================================================
/**
 * setIsAlive
 * 
 * @param pVal
 */
// =======================================================
public void setIsAlive(boolean pVal) {
  this.alive = pVal;
  // End Method setIsAlive() ======================
}

// =======================================================
/**
 * setNumberProcessed [Summary here]
 * 
 * @param pNum
 */
// =======================================================
public void setNumberProcessed(int pNum) {
 this.numberProcessed = pNum;
} // End Method setNumberProcessed() ======================

// =======================================================
/**
 * setNumberToProcess [Summary here]
 * 
 * @param pNum
 */
// =======================================================
public void setNumberToProcess(int pNum) {
  this.numberToProcess = pNum;
}  // End Method setNumberToProcess() ======================




}
