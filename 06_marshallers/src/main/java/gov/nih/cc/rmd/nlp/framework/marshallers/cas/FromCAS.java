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
// =================================================
/**
 * This is a reader to be employed by the scale-out single annotator application.
 * It's purpose is to take in cas's, then dole them out when the getNext() method
 * is called by the engine.
 * 
 * It keeps a hash of document ids|CAS to be processed.  getNext() retrieves
 * from this list.
 * 
 * A writer of documentIds | CAS is where the completed cas's go.
 *
 *
 * @author  Guy Divita 
 * @created Sept 16, 2014
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.cas;



import java.io.IOException;
import java.util.Queue;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCopier;
import org.apache.uima.util.Progress;

// import gov.va.vinci.leo.cr.BaseFileCollectionReader;
import gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader;
import gov.nih.cc.rmd.nlp.framework.utils.U;



public class FromCAS extends Reader {

  

private JCas nextCas;



//=======================================================
 /**
  * Constructor FromText is called from SuperReader
  *  assumes that the initialize() method will be called later on.
  *
  */
 // =======================================================
 public FromCAS()  {
   
   
 } // end Constructor() ---------------------
 
  
  
	// =======================================================
  /**
   * Constructor FromText 
   *
   * @param pInputList
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public FromCAS(Queue<JCas> pInputList) throws ResourceInitializationException {
    
    initialize( pInputList );
    
  } // end Constructor() ---------------------
  

  
  // -----------------------------------------
	/** 
	 * getNext retrieves the next document id from the list of document ids, queries the database fro 
	 * annotations that share this document id, creates annotations for this id.
	 * @param pCAS
	 * @throws IOException
	 * @throws CollectionException
	 */
	// -----------------------------------------
	public void getNext(CAS pCAS) throws IOException, CollectionException {
	 
	  
    JCas jCas = null;
    CAS  aCas = null;
    int ctr = 0;
    
    jCas = this.nextCas;
    
    try {
        aCas = jCas.getCas();
        pCAS.setDocumentText(jCas.getDocumentText());
        CasCopier.copyCas(aCas, pCAS,false);
       // System.err.println("Marshelled it to this view " );
      
    } catch (Exception e ) {
      e.printStackTrace();
     System.err.println("Something went wrong with fromCas " + e.toString());
    }

   
    
	} // end Method getNext() -----------------------


 



	// -----------------------------------------
	/** 
	 * hasNext
	 *
	 * @return
	
	 */
	// -----------------------------------------
	public synchronized boolean hasNext()  {
		boolean returnValue = false;
		

	 if ( this.inputList!= null ) { 
	  
	   while ( this.inputList.isEmpty() ) {
	     
	   
	       try {
	         // System.err.println(" In HasNext() The input list is empty - sleep for a while");
	         synchronized ( this.inputList) {
	           this.inputList.wait();
	         }
        } catch (Exception e) {
          
         
        }
	 
	   }
	   try {
	     if ( !this.inputList.isEmpty()  ) {
	       this.nextCas = this.inputList.remove();
	      // System.err.println("Someone added a record into the list "  + Thread.currentThread().getName());
	       returnValue = true;
	     } 	  
	   } catch (Exception e ) {
	     e.printStackTrace();
	     System.err.println("Issue with has next " + e.toString());
	   }
	  
	 }
		 return returnValue;
	} // end Method hasNext() -----------------
	
	


 
	// -----------------------------------------
	  /** 
	   * initialize sets the inputHash
	   * @param pInputList
	   
	   * @throws CollectionException
	   */
	  // -----------------------------------------
	  public void initialize(Queue<JCas> pInputList) throws ResourceInitializationException  {
	    
	    this.inputList = pInputList;
	
	  	 	   
	} // end Method initialize() --------------
	  
	  
		
	  //=======================================================
	  /**
	   * initialize 
	   * 
	   * @param pArgs    assumes there is the arg --inputDir= 
	   * @throws ResourceInitializationException
	   *
	   */
	  // ======================================================
	 public void initialize(String[] pArgs) throws ResourceInitializationException {
	   
	   
	   System.err.println("This reader should only be calling the initialize( Queue<JCas> ) initialize method");
	   throw new ResourceInitializationException();
	     
	   } // End Method initialize ============
	  
	  
	  
	
        // ----------------------------------------
	// Class Variables
	// ----------------------------------------
	  private Queue<JCas> inputList = null;



    /* (non-Javadoc)
     * @see gov.va.vinci.nlp.framework.marshallers.reader.Reader#initialize(java.lang.String)
     */
    
    public void initialize(String pInputFile) throws ResourceInitializationException {
     
      // End Method initialize() ======================
    }



    /* (non-Javadoc)
     * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#getProgress()
     */
    @Override
    public Progress[] getProgress() {
      // TODO Auto-generated method stub
      return null;
      // End Method getProgress() ======================
    }

} // end Class FromCAS() ----
