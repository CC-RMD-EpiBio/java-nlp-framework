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
// =================================================
/**
 * TermAnnotatorThreadPool is a wrapper around
 * the TermAnnotatorAux that doles out available
 * instances of TermAnnotatorAux to create terms
 *
 * @author  Guy Divita 
 * @created Feb 11, 2016
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.Stack;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Utterance;



public class TermAnnotatorThreadPool  {
    
   
  

    
    // end Class TermAnnotator() -----------
  
  // ==========================================
  /**
   * termTokenizeSentence 
   *
   * @param pJCas
   * @param pUtterance
   * @return 
   * @throws Exception 
   */
  // ==========================================
  public void termTokenizeSentence(JCas pJCas, Annotation pUtterance) throws Exception {
   
    
    TermAnnotatorAux availableThread = getAnAvailableThread();   // <--- will block until one is available
    
    
    availableThread.utteranceStack.add( new AnnotationContainer(pJCas, (Utterance) pUtterance));   // <--- the availableThread.run() will 
                                                                                                   //      detect new items
                                                                                                   //      on the stack and
                                                                                                   //      and process them
    
     //synchronized(availableThread.utteranceStack) {availableThread.utteranceStack.notify();};  // <-- this tells the run to wake up if it's waiting
     System.err.println("Just put a sentence on the stack ");
    
    
   
  } // end Method termTokenizeSentence() ========
  

  // ==========================================
  /**
   * getAnAvailableThread retrieves an available thread
   * If non is on the stack, it will sleep until one has been put on
   *
   * @return Thread
   * @throws InterruptedException 
   */
  // ==========================================
  private TermAnnotatorAux getAnAvailableThread() throws InterruptedException {
   
    TermAnnotatorAux availableThread  = null;
    if (  !this.availableThreads.empty() ) {
      availableThread  =  this.availableThreads.pop();
      System.err.println("found one ");
    } else {
      int ctr = 0;
      while ( availableThread == null) {
        System.err.println("Waiting for an available instance of termAnnotator " + ctr++);
       // synchronized(this.availableThreads) { this.availableThreads.wait(); }
        Thread.sleep(500);
        if ( !this.availableThreads.empty()) {
          availableThread = this.availableThreads.pop();
          System.err.println("found one ");
        }
      }
    } 
    return availableThread;
    
  } // end Method getAnAvailableThread() ======
  


  // ==========================================
  /**
   * repairHyphenatedTerm 
   *
   * @param pJCas
   * @param pTerm
   * @throws InterruptedException 
   */
  // ==========================================
  public void repairHyphenatedTerm(JCas pJCas, Annotation pTerm) throws InterruptedException {
   
    TermAnnotatorAux availableThread = getAnAvailableThread();   // <--- will block until one is available
    
    
    availableThread.termStack.add( new AnnotationContainer(pJCas, (LexicalElement) pTerm));   // <--- the availableThread.run() will 
                                                                                                   //      detect new items
                                                                                                   //      on the stack and
                                                                                                   //      and process them
   
    //synchronized(availableThread.utteranceStack) { availableThread.utteranceStack.notifyAll();}  // <-- this tells the run to wake up if it's waiting
    
    
  } // end Method repairHyphenatedTerm() ======
  


    //----------------------------------
    /**
     * initialize creates X number of threads
     * 
     * 
     * @param localTerminologyFilez
     * @param pLocalResources
     * @param pNumberOfThreads
     * 
     **/
    // ----------------------------------
    public void initialize(String localTerminologyFilez, String pLocalResources, int pNumberOfThreads, String pArgs[] ) throws ResourceInitializationException {
        
      this.termAnnotatorAuxes = new TermAnnotatorAux[ pNumberOfThreads ];
              this.threadPool = new Thread[ pNumberOfThreads];
        this.availableThreads = new Stack<TermAnnotatorAux>();
            this.inUseThreads = new Stack<TermAnnotatorAux>();
     
     System.err.println("The number of term threads = " + pNumberOfThreads);
     
     for ( int i = 0; i < pNumberOfThreads; i++ ) {
        this.termAnnotatorAuxes[i] = new TermAnnotatorAux();
        this.termAnnotatorAuxes[i].initialize(localTerminologyFilez, pLocalResources, availableThreads, pArgs );
        this.threadPool[i] = new Thread( termAnnotatorAuxes[i]);
        this.threadPool[i].start();
        this.availableThreads.push( this.termAnnotatorAuxes[i]);
     }
     
   
     
     
    } // end Method initialize() -------
      
  

    // ==========================================
    /**
     * destroy invokes destroys in each of the thread instances destroy
     *
     */
    // ==========================================
    public void destroy() {
    
      for (int i = 0; i < this.termAnnotatorAuxes.length; i++)
        this.termAnnotatorAuxes[i].destroy();
      
    } // end Method destroy() ========================================
    



    // -------------------------------------------
    // Class Variables
    // -------------------------------------------
    private TermAnnotatorAux    termAnnotatorAuxes[] = null;
    private Thread                      threadPool[] = null;
    private Stack<TermAnnotatorAux> availableThreads = null;
    private Stack<TermAnnotatorAux>     inUseThreads = null;
    
    
}
