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
