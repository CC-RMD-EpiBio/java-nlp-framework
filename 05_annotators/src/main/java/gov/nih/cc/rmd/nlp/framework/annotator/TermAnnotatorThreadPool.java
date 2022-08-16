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
