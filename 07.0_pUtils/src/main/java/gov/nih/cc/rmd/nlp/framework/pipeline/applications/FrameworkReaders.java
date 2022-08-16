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
 * BaseFrameworkApplication.java [Summary here]
 *
 * @author  guy
 * @created Oct 10, 2013
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import java.util.Map;

import org.apache.uima.collection.CollectionReader;

import gov.nih.cc.rmd.nlp.framework.marshallers.file.FromFile;
import gov.nih.cc.rmd.nlp.framework.marshallers.gate.GateCorpusReader;
import gov.nih.cc.rmd.nlp.framework.marshallers.gate.GateReader;
import gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader;
import gov.nih.cc.rmd.nlp.framework.marshallers.snippet.CombineSnippetsWithXMI;
import gov.nih.cc.rmd.nlp.framework.marshallers.string.FromStringReader;
import gov.nih.cc.rmd.nlp.framework.marshallers.text.FromPipedText;
import gov.nih.cc.rmd.nlp.framework.marshallers.text.FromText;
import gov.nih.cc.rmd.nlp.framework.marshallers.vtt.FromVTT;
import gov.nih.cc.rmd.nlp.framework.marshallers.xmi.FromXMI;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;

/**
 * Framework readers.
 */
public class FrameworkReaders extends FrameworkBase {

  // ----------------------------------------
  // Public enumerations of readers
  // (The ones marked private here need to be implemented)
 
  // ----------------------------------------
  /** The Constant TEXT_READER. */
  public static final int TEXT_READER = 0;

  /** The Constant PIPED_TEXT_READER. */
  public static final int PIPED_TEXT_READER = 22;

  
  /** The Constant XMI_READER. */
  public static final int XMI_READER = 2;

  /** The Constant KNOWTATOR_READER. */
  public static final int KNOWTATOR_READER = 3;

  /** The Constant VTT_READER. */
  public static final int VTT_READER = 4;

  /** The Constant MULTI_RECORD_FILE_READER. */
  public static final int MULTI_RECORD_FILE_READER = 5;

  /** The Constant JCAS_READER. */
  private static final int JCAS_READER = 6;

  /** The Constant COMMON_MODEL_READER. */
  private static final int COMMON_MODEL_READER = 7; // TBD

  /** The Constant VINCI_MRSA_DATABASE_READER. */
  private static final int VINCI_MRSA_DATABASE_READER = 8; // TBD

  /** The Constant VINCI_Q_DATABASE_READER. */
  public static final int VINCI_Q_DATABASE_READER = 9;

  /** The Constant VINCI_DATABASE_READER. */
  public static final int VINCI_DATABASE_READER = 10;

  /** The Constant H2_DATABASE_READER. */
  public static final int H2_DATABASE_READER = 11;

  /** The Constant JDBC_DATABASE_READER. */
  private static final int JDBC_DATABASE_READER = 12; // TBD

  /** The Constant STRING_READER. */
  public static final int STRING_READER = 13;

  /** The Constant SIMPLE_FORMAT_READER. */
  public static final int SIMPLE_FORMAT_READER = 14; // TBD

  /** The Constant BIOC_READER. */
  public static final int BIOC_READER = 15;

  /** The Constant MULTI_RECORD_FILE_BY_PATIENT_READER. */
  public static final int MULTI_RECORD_FILE_BY_PATIENT_READER = 16;

  /** The Constant FILE_READER. */
  public static final int FILE_READER = 17;

  /** The Constant SNIPPETS_READER. */
  public static final int SNIPPETS_READER = 18;

  /** The Constant RED_SNIPPETS_READER. */
  public static final int RED_SNIPPETS_READER = 19;
  
  /** The Constant GATE_READER. */
  public static final int GATE_READER = 20;
  
  /** The Constant GATE_CORPUS_READER. */
  public static final int GATE_CORPUS_READER = 23;

  /** The Constant SNIPPETS2XMI_READER. */
  public static final int SNIPPETS2XMI_READER = 21;
  
  /** The Constant TEXT_READER_. */
  public static final String TEXT_READER_ = "TEXT_READER";
  
  /** The Constant PIPED_TEXT_READER_. */
  public static final String PIPED_TEXT_READER_ = "PIPED_TEXT_READER";

  /** The Constant XMI_READER_. */
  public static final String XMI_READER_ = "XMI_READER";

  /** The Constant KNOWTATOR_READER_. */
  public static final String KNOWTATOR_READER_ = "KNOWTATOR_READER";

  /** The Constant VTT_READER_. */
  public static final String VTT_READER_ = "VTT_READER";

  /** The Constant MULTI_RECORD_FILE_READER_. */
  public static final String MULTI_RECORD_FILE_READER_ = "MULTI_RECORD_FILE_READER";

  /** The Constant MULTI_RECORD_FILE_BY_PATIENT_READER_. */
  public static final String MULTI_RECORD_FILE_BY_PATIENT_READER_ =
      "MULTI_RECORD_FILE_BY_PATIENT_READER";

  /** The Constant JCAS_READER_. */
  public static final String JCAS_READER_ = "JCAS_READER";

  /** The Constant COMMON_MODEL_READER_. */
  public static final String COMMON_MODEL_READER_ = "COMMON_MODEL_READER"; // TBD

  /** The Constant VINCI_MRSA_DATABASE_READER_. */
  public static final String VINCI_MRSA_DATABASE_READER_ = "VINCI_MRSA_DATABASE_READER"; // TBD

  /** The Constant VINCI_Q_DATABASE_READER_. */
  public static final String VINCI_Q_DATABASE_READER_ = "VINCI_Q_DATABASE_READER"; // TBD

  /** The Constant VINCI_DATABASE_READER_. */
  public static final String VINCI_DATABASE_READER_ = "VINCI_DATABASE_READER";

  /** The Constant H2_DATABASE_READER_. */
  public static final String H2_DATABASE_READER_ = "H2_DATABASE_READER";

  /** The Constant JDBC_DATABASE_READER_. */
  public static final String JDBC_DATABASE_READER_ = "JDBC_READER"; // TBD

  /** The Constant STRING_READER_. */
  public static final String STRING_READER_ = "STRING_READER";

  /** The Constant SIMPLE_FORMAT_READER_. */
  public static final String SIMPLE_FORMAT_READER_ = "SIMPLE_FORMAT_READER"; // TBD

  /** The Constant BIOC_READER_. */
  public static final String BIOC_READER_ = "BIOC_READER";

  /** The Constant FILE_READER_. */
  public static final String FILE_READER_ = "FILE_READER";

  /** The Constant SNIPPETS_READER_. */
  public static final String SNIPPETS_READER_ = "SNIPPETS_READER";

  /** The Constant RED_SNIPPETS_READER_. */
  public static final String RED_SNIPPETS_READER_ = "RED_SNIPPETS_READER";

  /** The Constant GATE_READER_. */
  public static final String GATE_READER_ = "GATE_READER";
  
  /** The Constant GATE_CORPUS_READER_. */
  public static final String GATE_CORPUS_READER_ = "GATE_CORPUS_READER_";
  
  /** The Constant SNIPPETS2XMI_READER_. */
  public static final String SNIPPETS2XMI_READER_ = "SNIPPETS2XMI_READER";
  
  // =======================================================
  /**
   * createReader creates and sets up a singleton reader. This reader is
   * synchronized across the n servers.
   * 
   * The default reader is the text reader.
   *
   * @param pArgs relies upon the argument --inputFormat= with the values
   *          coming from FrameworkBaselineApplicaiton.TEXT_READER_ ....
   * 
   * @return CollectionReader
   */
  // =======================================================
  public CollectionReader createReader(String[] pArgs) {

    String inputFormatType = U.getOption(pArgs, "--inputFormat=", FrameworkReaders.TEXT_READER_);

    int readerType = getReaderType(inputFormatType);

    Reader SingleReader = (Reader) this.createReader(readerType, pArgs);

    return SingleReader;
  } // End Method createReader() ======================

  // =======================================================
  /**
   * createReader returns a reader (the default is fromText)
   * 
   * Relies on args being filled with --inputDir= for most of them --inputFile=
   * for MULTI_RECORD_FILE_READER (that should be changed to implement
   * --inputDir at some point)
   * 
   *
   * @param pReaderType the reader type
   * @param args the args
   * @return the collection reader
   */
  // =======================================================
  public CollectionReader createReader(int pReaderType, String[] args) {

    try {

      String _inputDir = U.getOption(args, "--inputDir=", "");
      String inputFormatType = getReaderType( pReaderType);
      

      GLog.println(GLog.STD___LEVEL, this.getClass(), "createReader", "About to attach a reader ");
      singletonReader = createReaderAux(pReaderType, args);
      GLog.println(GLog.STD___LEVEL, this.getClass(), "createReader",  "Attached reader " + inputFormatType);
      GLog.println(GLog.STD___LEVEL, this.getClass(), "createReader",  "================================>Reading from " + _inputDir);

    } catch (Exception e6) {
      e6.printStackTrace();
      String msg = "Issue with creating the reader " + e6.getMessage();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createReader", msg);
      throw new RuntimeException(msg);

    }

    return singletonReader;
    // End Method createReader() ======================
  }

  // =======================================================
  /**
   * createSingletonReader .
   *
   * @param pReaderType the reader type
   * @param pArgs the args
   * @return the collection reader
   */
  // =======================================================
  public static CollectionReader createSingletonReader(int pReaderType, String[] pArgs) {

    Reader reader = FrameworkReaders.createReaderAux(pReaderType, pArgs);

    return reader;
  } // End Method createSingletonReader() ======================

  // =======================================================
  /**
   * createSingletonReader .
   *
   * @param pReaderType the reader type
   * @param pArgs the args
   * @return the collection reader
   */
  // =======================================================
  public static CollectionReader createSingletonReader(String pReaderType, String[] pArgs) {

    int readerType = getReaderType(pReaderType);

    Reader reader = FrameworkReaders.createReaderAux(readerType, pArgs);

    return reader;
  } // End Method createSingletonReader() ======================

  // =======================================================
  /**
   * getReaderType.
   *
   * @param pReaderType the reader type
   * @return int
   */
  // =======================================================
  public static int getReaderType(String pReaderType) {

    int returnVal = TEXT_READER;
    if      (pReaderType.contains(TEXT_READER_))                        returnVal = TEXT_READER;
    else if (pReaderType.contains(PIPED_TEXT_READER_))                        returnVal = PIPED_TEXT_READER;
    else if (pReaderType.contains(XMI_READER_))                         returnVal = XMI_READER;
    else if (pReaderType.contains(KNOWTATOR_READER_))                   returnVal = KNOWTATOR_READER;
    else if (pReaderType.contains(VTT_READER_))                         returnVal = VTT_READER;
    else if (pReaderType.contains(GATE_READER_))                        returnVal = GATE_READER;
    else if (pReaderType.contains(GATE_CORPUS_READER_))                 returnVal = GATE_CORPUS_READER;
    
    else if (pReaderType.contains(SNIPPETS2XMI_READER_))                returnVal = SNIPPETS2XMI_READER;
    else if (pReaderType.contains(MULTI_RECORD_FILE_READER_))           returnVal = MULTI_RECORD_FILE_READER;
    else if (pReaderType.contains(MULTI_RECORD_FILE_BY_PATIENT_READER_))returnVal = MULTI_RECORD_FILE_BY_PATIENT_READER;
    else if (pReaderType.contains(JCAS_READER_))                        returnVal = JCAS_READER;
    else if (pReaderType.contains(COMMON_MODEL_READER_))                returnVal = COMMON_MODEL_READER; // TBD
    else if (pReaderType.contains(VINCI_MRSA_DATABASE_READER_))         returnVal = VINCI_MRSA_DATABASE_READER; // TBD
    else if (pReaderType.contains(VINCI_Q_DATABASE_READER_))            returnVal = VINCI_Q_DATABASE_READER; // TBD
    else if (pReaderType.contains(VINCI_DATABASE_READER_))              returnVal = VINCI_DATABASE_READER;
    else if (pReaderType.contains(H2_DATABASE_READER_))                 returnVal = H2_DATABASE_READER;
    else if (pReaderType.contains(JDBC_DATABASE_READER_))               returnVal = JDBC_DATABASE_READER; // TBD
    else if (pReaderType.contains(STRING_READER_))                      returnVal = STRING_READER;
    else if (pReaderType.contains(SIMPLE_FORMAT_READER_))               returnVal = SIMPLE_FORMAT_READER; // TBD
    else if (pReaderType.contains(BIOC_READER_))                        returnVal = BIOC_READER;
    else if (pReaderType.equals(FILE_READER_))                          returnVal = FILE_READER;
    else if (pReaderType.equals(SNIPPETS_READER_))                      returnVal = SNIPPETS_READER;
    else if (pReaderType.equals(RED_SNIPPETS_READER_))                  returnVal = RED_SNIPPETS_READER;

    return returnVal;
  } // End Method getReaderType() ======================

  
//=======================================================
 /**
  * getReaderType.
  *
  * @param pReaderType the reader type
  * @return int
  */
 // =======================================================
 public static String getReaderType(int pReaderType) {

   String returnVal = TEXT_READER_;
   if      (pReaderType == TEXT_READER )                        returnVal = TEXT_READER_;
   else if (pReaderType == PIPED_TEXT_READER )                  returnVal = PIPED_TEXT_READER_;
   else if (pReaderType == XMI_READER )                         returnVal = XMI_READER_;
   else if (pReaderType == KNOWTATOR_READER )                   returnVal = KNOWTATOR_READER_ ;
   else if (pReaderType == VTT_READER )                         returnVal = VTT_READER_ ;
   else if (pReaderType == GATE_READER )                        returnVal = GATE_READER_ ;
   else if (pReaderType == GATE_CORPUS_READER )                 returnVal = GATE_CORPUS_READER_ ;
   else if (pReaderType == SNIPPETS2XMI_READER )                returnVal = SNIPPETS2XMI_READER_ ;
   else if (pReaderType == MULTI_RECORD_FILE_READER )           returnVal = MULTI_RECORD_FILE_READER_ ;
   else if (pReaderType == MULTI_RECORD_FILE_BY_PATIENT_READER )returnVal = MULTI_RECORD_FILE_BY_PATIENT_READER_ ;
   else if (pReaderType == JCAS_READER )                        returnVal = JCAS_READER_ ;
   else if (pReaderType == COMMON_MODEL_READER )                returnVal = COMMON_MODEL_READER_ ; // TBD
   else if (pReaderType == VINCI_MRSA_DATABASE_READER )         returnVal = VINCI_MRSA_DATABASE_READER_ ; // TBD
   else if (pReaderType == VINCI_Q_DATABASE_READER )            returnVal = VINCI_Q_DATABASE_READER_ ; // TBD
   else if (pReaderType == VINCI_DATABASE_READER )              returnVal = VINCI_DATABASE_READER_ ;
   else if (pReaderType == H2_DATABASE_READER )                 returnVal = H2_DATABASE_READER_ ;
   else if (pReaderType == JDBC_DATABASE_READER )               returnVal = JDBC_DATABASE_READER_ ; // TBD
   else if (pReaderType == STRING_READER )                      returnVal = STRING_READER_ ;
   else if (pReaderType == SIMPLE_FORMAT_READER )               returnVal = SIMPLE_FORMAT_READER_ ; // TBD
   else if (pReaderType == BIOC_READER )                        returnVal = BIOC_READER_ ;
   else if (pReaderType ==  FILE_READER )                       returnVal = FILE_READER_ ;
   else if (pReaderType ==  SNIPPETS_READER )                   returnVal = SNIPPETS_READER_ ;
   else if (pReaderType ==  RED_SNIPPETS_READER )               returnVal = RED_SNIPPETS_READER_ ;

   return returnVal;
 } // End Method getReaderType() ======================
  // =======================================================
  /**
   * createReaderAux returns a reader (the default is fromText)
   * 
   * Relies on args being filled with --inputDir= for most of them --inputFile=
   * for MULTI_RECORD_FILE_READER (that should be changed to implement
   * --inputDir at some point)
   * 
   *
   * @param pReaderType the reader type
   * @param args the args
   * @return the reader
   */
  // =======================================================
public static Reader createReaderAux(int pReaderType, String[] args) {

    Reader StaticReader = null;

    try {

      String _inputDir = U.getOption(args, "--inputDir=", "");
      String _inputFile = U.getOption(args, "--inputFile=", "");
      String fixOffsetz = U.getOption(args, "--fixOffsets=", "true");
      int beginRecordRow = Integer.parseInt(U.getOption(args, "--beginRecordRow=", "0"));
      String reSampleFile = U.getOption(args, "--reSampleFile=", "");
      String recurseValue = U.getOption(args, "--recurseIntoDirs=", "true");
      String labelMappingValues = U.getOption(args, "--labelMappingValues=", ""); // <---
                                                                                  // this
                                                                                  // should
                                                                                  // be
                                                                                  // oldVal:newVal|oldVal:newVal
      boolean recurse = Boolean.valueOf(recurseValue);
      boolean fixOffsets = Boolean.valueOf(fixOffsetz);

      switch (pReaderType) {
        case TEXT_READER:
          StaticReader = new FromText(args);
          break;
          
        case PIPED_TEXT_READER:
          StaticReader = new FromPipedText(args);
          break; 
          
        case FILE_READER:
          StaticReader = new FromFile(_inputDir);
          break;
        case XMI_READER:
          StaticReader = new FromXMI(_inputDir, recurse);
          break;
        // case KNOWTATOR_READER:
        // StaticReader = new FromKnowtator(_inputDir, recurse, fixOffsets,
        // labelMappingValues);
        // break;// this is the project level which should have corpus/saved
        // case MULTI_RECORD_FILE_READER:
        // StaticReader = new FromMultiRecordFile(_inputFile, beginRecordRow,
        // reSampleFile);
        // break; // formatType now passed as param

        case STRING_READER:
          FromStringReader aReader = new FromStringReader();
          StaticInputQueue = aReader.getInputQueue();
          StaticReader = aReader;
          break;

        // case SNIPPETS_READER:
        // StaticReader = new FromVTTSnippets(args);
        // break;
        // case RED_SNIPPETS_READER:
        // StaticReader = new FromRedSnippets(args);
        // break;

        case GATE_READER:
          StaticReader = new GateReader(args);
          break;
          
        case GATE_CORPUS_READER:
          StaticReader = new GateCorpusReader(args);
          break;
          
         
          
        case SNIPPETS2XMI_READER:
          StaticReader = new CombineSnippetsWithXMI(args);
          break;
          
          
        
        case VTT_READER:
          StaticReader = new FromVTT(args);
          break;
        // case JCAS_READER: aReader = new Marshallers.createJCASReader();
        // break;
        // case COMMON_MODEL_READER: aReader = new FromCommonModelReader();
        // break;
        // case VINCI_MRSA_DATABASE: aReader = new FromVINCIMRSADatabase();
        // break;

        // case H2_DATABASE_READER:
        // StaticReader = new FromH2Database(args);
        // break;
        // case JDBC_DATABASE: aReader = new FromJDBC_Database(); break;
        // default: singletonReader = new FromText( inputDir); break;
        default:
          StaticReader = null;
          break;
      }

      if (StaticReader == null) {
        String msg = "Something went wrong.  The static reader is null";
        GLog.println(GLog.ERROR_LEVEL, FrameworkReaders.class, "createReaderAux", msg);
        throw new RuntimeException(msg);
      }

    } catch (Exception e6) {
      e6.printStackTrace();
      String msg = "Issue with creating the reader " + e6.getMessage();
      GLog.println(GLog.ERROR_LEVEL, FrameworkReaders.class, "createReaderAux", msg);
      throw new RuntimeException(msg);

    }

    return StaticReader;
  } // End Method createReaderAux() ======================

  // =======================================================
  /**
   * createReader creates a reader using the corpus as an input type.
   *
   * @param corpus the corpus
   * @return CollectionReader
   */
  // =======================================================
  // public CollectionReader createReader(Corpus corpus) {
  //
  // singletonReader = new FromCorpus(corpus);
  // return singletonReader;
  // } // End Method createReader() ======================

  // =======================================================
  /**
   * attachReader .
   *
   * @param singleReader the single reader
   */
  // =======================================================
  public void attachReader(Reader singleReader) {
    GLog.println(GLog.STD___LEVEL, this.getClass(), "attachReader", "Attached the reader ");
    this.singletonReader = singleReader;
    if (this.singletonReader == null)
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "attachReader", "The reader is null here!");

  } // End Method attachReader() ======================

  // -----------------------------------------
  // Private Global variables
  /** The singleton reader. */
  // ----------------------------------------
  protected Reader singletonReader = null;

  /** The Static input dir. */
  protected String StaticInputDir = null;

  /** The Static input queue. */
  protected static Map<String, String> StaticInputQueue = null;

} // End Class FrameworkBaselineApplication
