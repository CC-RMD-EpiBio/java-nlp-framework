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
/*
 *
 */
/**
 * FromDatabase reads in data from a database
 * and converts them to Cas's.
 *
 *
 * @author  Guy Divita 
 * @created Aug 17, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.csv;

import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * The Class CSVReader.
 */
public class CSVReader extends Reader {

  // -----------------------------------------
  /**
   * 
   * Constructor .
   *
   * @param pArgs the args
   * @throws ResourceInitializationException the resource initialization
   *           exception
   */
  // -----------------------------------------
  public CSVReader(String[] pArgs) throws ResourceInitializationException {
    // n/a
  } // end Constructor() --------------------

  // -----------------------------------------
  /**
   * 
   * getNext retrieves the next document id from the list of document ids,
   * queries the database fro annotations that share this document id, creates
   * annotations for this id.
   *
   * @param pCAS the cas
   * @return the next
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws CollectionException the collection exception
   */
  // -----------------------------------------
  @Override
  public void getNext(CAS pCAS) throws IOException, CollectionException {

    this.performanceMeter.startCounter();
    try {
      pCAS.getJCas();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Something went wrong with fromDatabase " + e.toString());
    }

    // --------------------------
    // increment the fileCounter
    this.fileCounter++;
    this.performanceMeter.stopCounter();

  } // end Method getNext() -----------------------

  // -----------------------------------------
  /**
   * 
   * destroy.
   */
  // -----------------------------------------
  @Override
  public void destroy() {
    this.performanceMeter.writeProfile(this.getClass().getSimpleName());

  } // end Method destroy() --------------------

  // -----------------------------------------
  /**
   * getProgress is method required for the interface that is populated with the
   * fraction of files processed by the number of files to process.
   *
   * @return Progress[]
   */
  // -----------------------------------------
  @Override
  public Progress[] getProgress() {
    Progress[] p = new Progress[] {
        new ProgressImpl(this.fileCounter, this.numberOfFiles, Progress.ENTITIES)
    };
    return p;

  } // end Method getProgress() --------------

  // -----------------------------------------
  /**
   * 
   * hasNext.
   *
   * @return true, if successful
   * @throws CollectionException the collection exception
   */
  // -----------------------------------------
  @Override
  public boolean hasNext() throws CollectionException {
    boolean returnValue = false;

    if (this.fileCounter < this.numberOfFiles)
      returnValue = true;

    return returnValue;
  } // end Method hasNext() -----------------

  // -----------------------------------------
  /**
   * 
   * initialize opens the file that has the multi-AnnotationRecord files.
   * 
   * 
   * This method relies on the config variable "inputFile"
   *
   * @throws ResourceInitializationException the resource initialization
   *           exception
   */
  // -----------------------------------------
  @Override
  public void initialize() throws ResourceInitializationException {

    UimaContext aContext = getUimaContext();

    initialize(aContext);

  } // End Method initialize() ---------------------------

  // -----------------------------------------
  /**
   * 
   * initialize .
   *
   * @param pArgs the args
   * @throws ResourceInitializationException the resource initialization
   *           exception
   */
  // -----------------------------------------
  @Override
  public void initialize(String[] pArgs) throws ResourceInitializationException {

    String[] args = null;

    try {

      U.getOption(args, "--inputDir=", "/data/snippets");

    } catch (Exception e) {
      System.err.println(
          "Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }

  }

  /* see superclass */
  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.va.vinci.nlp.framework.marshallers.reader.Reader#initialize(java.lang.
   * String)
   */
  @Override
  public void initialize(String pInputFile) throws ResourceInitializationException {
    // TODO Auto-generated method stub

  }

  // ----------------------------------------
  // Class Variables
  // ----------------------------------------

  /** The file counter. */
  private int fileCounter = 0;

  /** The number of files. */
  private int numberOfFiles = 0;

} // end Class CSVReader() -------------------
