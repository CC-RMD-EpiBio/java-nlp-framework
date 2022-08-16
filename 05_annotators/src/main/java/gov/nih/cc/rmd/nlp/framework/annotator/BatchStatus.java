/*******************************************************************************
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                       Epidemiology and Biostatistics Branch 
 *                                            2019 - 2022
 *   ---------------------------------------------------------------------------
 *   Copyright Notice:
 *   This software was developed and funded by the National Institutes of Health
 *   Clinical Center (NIHCC), part of the National Institutes of Health (NIH),
 *   and agency of the United States Department of Health and Human Services,
 *   which is making the software available to the public for any commercial
 *   or non-commercial purpose under the following open-source BSD license.
 *  
 *   Government Usage Rights Notice:
 *   The U.S. Government retains unlimited, royalty-free usage rights to this 
 *   software, but not ownership, as provided by Federal law. Redistribution 
 *   and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *      1. Redistributions of source code must retain the above copyright
 *         and government usage rights notice, this list of conditions and the 
 *         following disclaimer.
 *  
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *        
 *      3. Neither the names of the National Institutes of Health Clinical
 *         Center, the National Institutes of Health, the U.S. Department of
 *         Health and Human Services, nor the names of any of the software
 *         developers may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
 *   
 *      4. The U.S. Government retains an unlimited, royalty-free right to
 *         use, distribute or modify the software.
 *   
 *      5. Please acknowledge NIH CC as the source of this software by including
 *         the phrase: "Courtesy of the U.S. National Institutes of Health Clinical Center"
 *          or 
 *                     "Source: U.S. National Institutes of Health Clinical Center."
 *  
 *     THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS
 *     IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *     TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *     PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 *     OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *     EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *     PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *     NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *     When attributing this code, please make reference to:
 *        Divita G, Carter ME, Tran LT, Redd D, Zeng QT, Duvall S, Samore MH, Gundlapalli AV. 
 *        v3NLP Framework: tools to build applications for extracting concepts from clinical text. 
 *        eGEMs. 2016;4(3). 
 *      
 *     In the absence of a specific paper or url listed above, reference https://github.com/CC-RMD-EpiBio/java-nlp-framework
 *   
 *     To view a copy of this license, visit https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD
 *******************************************************************************/
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
