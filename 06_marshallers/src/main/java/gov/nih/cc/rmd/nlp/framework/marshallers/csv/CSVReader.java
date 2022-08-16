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
