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
 * BaseFrameworkApplication.java [Summary here]
 *
 * @author  guy
 * @created Oct 10, 2013
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.jcas.JCas;

import gov.nih.cc.rmd.nlp.framework.marshallers.bio.ToBIO;
import gov.nih.cc.rmd.nlp.framework.marshallers.brat.BratWriter;
//import gov.nih.cc.rmd.nlp.framework.marshallers.brat.ToBratAux;
import gov.nih.cc.rmd.nlp.framework.marshallers.corpusStats.CorpusStatsWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.corpusStats.CorpusStatsWriterDb;
import gov.nih.cc.rmd.nlp.framework.marshallers.csv.CSVWriter;
//import gov.va.vinci.nlp.framework.marshallers.commonModel.ToCommonModel;
//import gov.va.vinci.nlp.framework.marshallers.commonModel.ToCommonModelString;
import gov.nih.cc.rmd.nlp.framework.marshallers.evaluate.EvaluateWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.evaluate.TokenEvaluateWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.formVariability.FormatVariabilityWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.formVariability.SectionNameCatalogWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.frameworkObject.FrameworkObjectWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.gate.GateCorpusWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.gate.GateWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.html.HTMLWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.indexing.CuiIndexWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.indexing.IndexingWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.knowtator.ToKnowtator;
import gov.nih.cc.rmd.nlp.framework.marshallers.ngram.CoreCharacteristicsWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.ngram.FreqWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.ngram.NGramWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.ngram.PhraseVectorWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.ngram.SectionCSVWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.ngram.SemanticTypeByDocTypeWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.ngram.SophiaCSVWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.ngram.VectorWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.ngram.WordFreqByDocTypeWriter;
import gov.nih.cc.rmd.nlp.framework.marshallers.string.ToString;
import gov.nih.cc.rmd.nlp.framework.marshallers.text.ToOneColumnText;
import gov.nih.cc.rmd.nlp.framework.marshallers.text.ToPipedText;
import gov.nih.cc.rmd.nlp.framework.marshallers.text.ToText;
import gov.nih.cc.rmd.nlp.framework.marshallers.vtt.ToVTT;
import gov.nih.cc.rmd.nlp.framework.marshallers.vtt.ToVTTRefinery;
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;
import gov.nih.cc.rmd.nlp.framework.marshallers.xmi.ToXMI;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.marshallers.snippet.SnippetsWriter;

/**
 * The Class FrameworkBaselineWriters.
 */
public class FrameworkBaselineWriters extends FrameworkReaders {

  /** The Constant NONE_WRITER. */
  public static final int NONE_WRITER = 0;

  /** The Constant XMI_WRITER. */
  public static final int XMI_WRITER = 1;

  /** The Constant KNOWTATOR_WRITER. */
  public static final int KNOWTATOR_WRITER = 2;
  public static final int EHOST_WRITER = 2;

  
  /** The Constant VTT_WRITER. */
  public static final int VTT_WRITER = 3;

  /** The Constant STATS_WRITER. */
  public static final int STATS_WRITER = 4;

  /** The Constant STATS_WRITER_DB. */
  public static final int STATS_WRITER_DB = 5;

  /** The Constant CONCORDANCE_WRITER. */
  public static final int CONCORDANCE_WRITER = 6;

  /** The Constant COMMON_MODEL_WRITER. */
  public static final int COMMON_MODEL_WRITER = 7;

  /** The Constant VINCI_DATABASE_WRITER. */
  public static final int VINCI_DATABASE_WRITER = 8;

  /** The Constant JDBC_DATABASE_WRITER. */
  public static final int JDBC_DATABASE_WRITER = 9;

  /** The Constant MULTI_RECORD_FILE_WRITER. */
  public static final int MULTI_RECORD_FILE_WRITER = 10;

  /** The Constant COMMON_MODEL_STRING. */
  public static final int COMMON_MODEL_STRING = 11;

  /** The Constant STRING. */
  public static final int STRING = 12;

  /** The Constant BIOC_WRITER. */
  public static final int BIOC_WRITER = 13;

  /** The Constant SIMPLE_FORMAT_WRITER. */
  public static final int SIMPLE_FORMAT_WRITER = 14;

  /** The Constant STATS_ACCESS_WRITER. */
  public static final int STATS_ACCESS_WRITER = 15;

  /** The Constant SNIPPET_WRITER. */
  public static final int SNIPPET_WRITER = 16;

  /** The Constant RED_SNIPPET_WRITER. */
  public static final int RED_SNIPPET_WRITER = 17;

  /** The Constant FILE_WRITER. */
  public static final int TEXT_WRITER = 18;

  /** The Constant KNOWTATOR_SAMPLE_WRITER. */
  public static final int KNOWTATOR_SAMPLE_WRITER = 19;

  /** The Constant INDEXING_WRITER. */
  public static final int INDEXING_WRITER = 20;

  /** The Constant TIU_NOTE_WRITER. */
  public static final int TIU_NOTE_WRITER = 21;

  /** The Constant EVALUATE_WRITER. */
  public static final int EVALUATE_WRITER = 22;
  
  /** The Constant EVALUATE_WRITER. */
  public static final int TOKEN_EVALUATE_WRITER = 42;

  /** The Constant HTML_WRITER. */
  public static final int HTML_WRITER = 23;

  /** The Constant NGRAM_WRITER. */
  public static final int NGRAM_WRITER = 24;

  /** The Constant CSV_WRITER. */
  public static final int CSV_WRITER = 25;

  /** The Constant FREQ_WRITER. */
  public static final int FREQ_WRITER = 26;

  /** The Constant SOPHIA_WRITER_CSV. */
  public static final int SOPHIA_WRITER_CSV = 27;

  /** The Constant CUI_INDEX_WRITER. */
  public static final int CUI_INDEX_WRITER = 28;

  /** The Constant PHRASE_VECTOR_WRITER. */
  public static final int PHRASE_VECTOR_WRITER = 29;

  /** The Constant SECTION_WRITER. */
  public static final int SECTION_WRITER = 30;

  /** The Constant VECTOR_WRITER. */
  public static final int VECTOR_WRITER = 31;

  /** The Constant WORD_FREQ_BY_DOCTYPE_WRITER. */
  public static final int WORD_FREQ_BY_DOCTYPE_WRITER = 32;

  /** The Constant STY_FREQ_BY_DOCTYPE_WRITER. */
  public static final int STY_FREQ_BY_DOCTYPE_WRITER = 33;

  /** The Constant JSON_WRITER. */
  public static final int JSON_WRITER = 34;

  /** The Constant BRAT_WRITER. */
  public static final int BRAT_WRITER = 35;

  /** The Constant REFINERY_VTT_WRITER. */
  public static final int REFINERY_VTT_WRITER = 36;
  
  /** The Constant FRAMEWORK_OBJECT_WRITER. */
  public static final int FRAMEWORK_OBJECT_WRITER = 37;
  
  /** The Constant GATE_WRITER. */
  public static final int GATE_WRITER = 38;
  
  /** The Constant GATE_CORPUS_WRITER. */
  public static final int GATE_CORPUS_WRITER = 45;
  
  /** The Constant FORMAT_VARIABILITY_WRITER. */
  public static final int FORMAT_VARIABILITY_WRITER = 39;

  /** The Constant SECTION_NAME_FREQ_WRITER. */
  public static final int SECTION_NAME_FREQ_WRITER = 40;
  
  /** The Constant BIO_WRITER. */
  public static final int BIO_WRITER = 41;
  
  /** The Constant PIPED_TEXT_WRITER. */
  public static final int PIPED_TEXT_WRITER = 43;
  
  
  /** The Constant ONE_COLUMN_TEXT_WRITER. */
  public static final int ONE_COLUMN_TEXT_WRITER = 44;
  
  /** The Constant XMI_WRITER_. */
  public static final String XMI_WRITER_ = "XMI_WRITER";

  /** The Constant KNOWTATOR_WRITER_. */
  public static final String KNOWTATOR_WRITER_ = "KNOWTATOR_WRITER";
  public static final String EHOST_WRITER_ = "EHOST_WRITER";


  /** The Constant KNOWTATOR_SAMPLE_WRITER_. */
  public static final String KNOWTATOR_SAMPLE_WRITER_ = "KNOWTATOR_SAMPLE_WRITER";

  /** The Constant VTT_WRITER_. */
  public static final String VTT_WRITER_ = "VTT_WRITER";

  /** The Constant STATS_WRITER_. */
  public static final String STATS_WRITER_ = "STATS_WRITER";

  /** The Constant STATS_WRITER_DB_. */
  public static final String STATS_WRITER_DB_ = "STATS_DB_WRITER";

  /** The Constant CONCORDANCE_WRITER_. */
  public static final String CONCORDANCE_WRITER_ = "CONCORDANCE_WRITER";

  /** The Constant COMMON_MODEL_WRITER_. */
  public static final String COMMON_MODEL_WRITER_ = "COMMON_MODEL_WRITER";

  /** The Constant VINCI_DATABASE_WRITER_. */
  public static final String VINCI_DATABASE_WRITER_ = "VINCI_DATABASE_WRITER";

  /** The Constant JDBC_DATABASE_WRITER_. */
  public static final String JDBC_DATABASE_WRITER_ = "JDBC_DATABASE_WRITER";

  /** The Constant MULTI_RECORD_FILE_WRITER_. */
  public static final String MULTI_RECORD_FILE_WRITER_ = "MULTI_RECORD_FILE_WRITER";

  /** The Constant COMMON_MODEL_STRING_. */
  public static final String COMMON_MODEL_STRING_ = "COMMON_MODEL_WRITER";

  /** The Constant STRING_. */
  public static final String STRING_ = "STRING_WRITER";

  /** The Constant BIOC_WRITER_. */
  public static final String BIOC_WRITER_ = "BIOC_WRITER";

  /** The Constant SIMPLE_FORMAT_WRITER_. */
  public static final String SIMPLE_FORMAT_WRITER_ = "SIMPLE_FORMAT_WRITER";

  /** The Constant STATS_ACCESS_WRITER_. */
  public static final String STATS_ACCESS_WRITER_ = "STATS_ACESS_WRITER";

  /** The Constant NONE_WRITER_. */
  public static final String NONE_WRITER_ = "NONE_WRITER";

  /** The Constant SNIPPET_WRITER_. */
  public static final String SNIPPET_WRITER_ = "SNIPPET_WRITER";

  /** The Constant RED_SNIPPET_WRITER_. */
  public static final String RED_SNIPPET_WRITER_ = "RED_SNIPPET_WRITER";

  /** The Constant INDEXING_WRITER_. */
  public static final String INDEXING_WRITER_ = "INDEXING_WRITER";

  /** The Constant TEXT_WRITER_. */
  public static final String TEXT_WRITER_ = "TEXT_WRITER";
  
  /** The Constant PIPED_TEXT_WRITER_. */
  public static final String PIPED_TEXT_WRITER_ = "PIPED_TEXT_WRITER";
  
  /** The Constant ONE_TEXT_WRITER_. */
  public static final String ONE_COLUMN_TEXT_WRITER_ = "ONE_COLUMN_TEXT_WRITER";
  
  /** The Constant TIU_NOTE_WRITER_. */
  public static final String TIU_NOTE_WRITER_ = "TIU_NOTE_WRITER";

  /** The Constant EVALUATE_WRITER_. */
  public static final String EVALUATE_WRITER_ = "EVALUATE_WRITER";
  
  /** The Constant TOKEN_EVALUATE_WRITER_. */
  public static final String TOKEN_EVALUATE_WRITER_ = "TOKEN_EVALUATE_WRITER";

  /** The Constant HTML_WRITER_. */
  public static final String HTML_WRITER_ = "HTML_WRITER";

  /** The Constant NGRAM_WRITER_. */
  public static final String NGRAM_WRITER_ = "NGRAM_WRITER";

  /** The Constant CSV_WRITER_. */
  public static final String CSV_WRITER_ = "CSV_WRITER";

  /** The Constant FREQ_WRITER_. */
  public static final String FREQ_WRITER_ = "FREQ_WRITER";

  /** The Constant SOPHIA_WRITER_CSV_. */
  public static final String SOPHIA_WRITER_CSV_ = "SOPHIA_WRITER_CSV";

  /** The Constant CUI_INDEX_WRITER_. */
  public static final String CUI_INDEX_WRITER_ = "CUI_INDEX_WRITER";

  /** The Constant PHRASE_VECTOR_WRITER_. */
  public static final String PHRASE_VECTOR_WRITER_ = "PHRASE_VECTOR_WRITER";

  /** The Constant SECTION_WRITER_. */
  public static final String SECTION_WRITER_ = "SECTION_WRITER";

  /** The Constant VECTOR_WRITER_. */
  public static final String VECTOR_WRITER_ = "VECTOR_WRITER";

  /** The Constant WORD_FREQ_BY_DOCTYPE_WRITER_. */
  public static final String WORD_FREQ_BY_DOCTYPE_WRITER_ = "WORD_FREQ_BY_DOCTYPE_WRITER";

  /** The Constant STY_FREQ_BY_DOCTYPE_WRITER_. */
  public static final String STY_FREQ_BY_DOCTYPE_WRITER_ = "STY_FREQ_BY_DOCTYPE_WRITER";

  /** The Constant JSON_WRITER_. */
  public static final String JSON_WRITER_ = "JSON_WRITER";

  /** The Constant BRAT_WRITER_. */
  public static final String BRAT_WRITER_ = "BRAT_WRITER";

  /** The Constant REFINERY_VTT_WRITER_. */
  public static final String REFINERY_VTT_WRITER_ = "REFINERY_VTT_WRITER";
  
  /** The Constant FRAMEWORK_OBJECT_WRITER_. */
  public static final String FRAMEWORK_OBJECT_WRITER_ = "FRAMEWORK_OBJECT_WRITER";
  
  /** The Constant GATE_WRITER_. */
  public static final String GATE_WRITER_ = "GATE_WRITER";
  
  /** The Constant GATE_CORPUS_WRITER_. */
  public static final String GATE_CORPUS_WRITER_ = "GATE_CORPUS_WRITER_";

  /** The Constant FORMAT_VARIABILITY_WRITER_. */
  public static final String FORMAT_VARIABILITY_WRITER_ = "FORMAT_VARIABILITY_WRITER";

  /** The Constant SECTION_NAME_FREQ_WRITER_. */
  public static final String SECTION_NAME_FREQ_WRITER_ = "SECTION_NAME_FREQ_WRITER";

  /** The Constant SECTION_NAME_FREQ_WRITER_. */
  public static final String BIO_WRITER_ = "BIO_WRITER";



  /** The Constant BIOC_XML_STRING. */
  public static final String BIOC_XML_STRING = "bioC";

  /** The Constant COMMON_MODEL_XML_STRING. */
  public static final String COMMON_MODEL_XML_STRING = "commonModel";

  /** The Constant XMI_XML_STRING. */
  public static final String XMI_XML_STRING = "xmi";

  /** The Constant VTT_PIPE_STRING. */
  public static final String VTT_PIPE_STRING = "vtt";

  /** The Constant JSON_STRING. */
  public static final String JSON_STRING = "json";

  /** The Constant BIOC_OBJECT. */
  public static final String BIOC_OBJECT = "bioCObject";

  /** The Constant COMMON_MODEL_OBJECT. */
  public static final String COMMON_MODEL_OBJECT = "commonModelObject";

  /** The Constant VTT_OBJECT. */
  public static final String VTT_OBJECT = "vttObject";

  /** The Constant VTT_STRING_OBJECT. */
  public static final String VTT_STRING_OBJECT = "vttStringObject";

  /** The Constant KNOWTATOR_OBJECT. */
  public static final String KNOWTATOR_OBJECT = "knowtatorObject";

  /** The Constant JSON_OBJECT. */
  public static final String JSON_OBJECT = "JsonObject";

  /** The Constant STRING_OBJECT. */
  public static final String STRING_OBJECT = "StringObject";

  /** The Constant HTML_OBJECT. */
  public static final String HTML_OBJECT = "HTMLObject";

  /** The Constant EVALUATE_OBJECT. */
  public static final String EVALUATE_OBJECT = "EvaluateObject";
  
  /** The Constant UIMA_OBJECT. */
  public static final String UIMA_OBJECT = "UIMAObject";
  
  /** The Constant FRAMEWORK_OBJECT. */
  public static final String FRAMEWORK_OBJECT = "FRAMEWORKObject";
  
  /** The Constant GATE_OBJECT. */
  public static final String GATE_OBJECT = "GATEObject";
  
  /** The Constant GATE_OBJECT. */
  public static final String BIO_OBJECT = "BIOObject";


  /** The output strings. */
  private ArrayList<String> outputStrings;

  // =======================================================
  /**
   * Constructor FrameworkBaselineApplication .
   */
  // =======================================================
  public FrameworkBaselineWriters() {

  }

  // =======================================================
  /**
   * addWriters.
   * 
   * @param pArgs assumes that there is an --outputFormat= option that contains
   *          one or more of the values
   */
  // =======================================================
  public void addWriters(String[] pArgs) {

    String outputFormat = U.getOption(pArgs, "--outputFormat=", "");

    String writerTypes[] = U.split(outputFormat, ":");

    if (writerTypes != null && writerTypes.length > 0) {
      for (String writerType : writerTypes) {

        int intWriterType = getTypeOfWriterCode(writerType);
        this.addWriter(intWriterType, pArgs);
      }
    }

  } // End Method addWriters() ======================

  // =======================================================
  /**
   * addWriters.
   * 
   * @param pOutputFormat the output format
   * @param pArgs assumes that there is an --outputFormat= option that contains
   *          one or more of the values
   */
  // =======================================================
  public void addWritersObs(String pOutputFormat, String[] pArgs) {

    String[] args = pArgs;

    if (pOutputFormat.contains(FrameworkBaselineWriters.NONE_WRITER_)) {
      // n/a
    }

    if (pOutputFormat.contains(FrameworkBaselineWriters.STATS_WRITER_))
      this.addWriter(FrameworkBaselineWriters.STATS_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.STATS_WRITER_DB_))
      this.addWriter(FrameworkBaselineWriters.STATS_WRITER_DB, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.VTT_WRITER_))
      this.addWriter(FrameworkBaselineWriters.VTT_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.XMI_WRITER_))
      this.addWriter(FrameworkBaselineWriters.XMI_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.BIOC_WRITER_))
      this.addWriter(FrameworkBaselineWriters.BIOC_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.BIO_WRITER_))
      this.addWriter(FrameworkBaselineWriters.BIO_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.SIMPLE_FORMAT_WRITER_))
      this.addWriter(FrameworkBaselineWriters.SIMPLE_FORMAT_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.KNOWTATOR_WRITER_))
      this.addWriter(FrameworkBaselineWriters.KNOWTATOR_WRITER, args);
    
    if (pOutputFormat.contains(FrameworkBaselineWriters.EHOST_WRITER_))
      this.addWriter(FrameworkBaselineWriters.KNOWTATOR_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.KNOWTATOR_SAMPLE_WRITER_))
      this.addWriter(FrameworkBaselineWriters.KNOWTATOR_SAMPLE_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.CONCORDANCE_WRITER_))
      this.addWriter(FrameworkBaselineWriters.CONCORDANCE_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.COMMON_MODEL_WRITER_))
      this.addWriter(FrameworkBaselineWriters.COMMON_MODEL_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.VINCI_DATABASE_WRITER_))
      this.addWriter(FrameworkBaselineWriters.VINCI_DATABASE_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.JDBC_DATABASE_WRITER_))
      this.addWriter(FrameworkBaselineWriters.JDBC_DATABASE_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.MULTI_RECORD_FILE_WRITER_))
      this.addWriter(FrameworkBaselineWriters.MULTI_RECORD_FILE_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.COMMON_MODEL_STRING_))
      this.addWriter(FrameworkBaselineWriters.COMMON_MODEL_STRING, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.STRING_))
      this.addWriter(FrameworkBaselineWriters.STRING, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.STATS_ACCESS_WRITER_))
      this.addWriter(FrameworkBaselineWriters.STATS_ACCESS_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.SNIPPET_WRITER_))
      this.addWriter(FrameworkBaselineWriters.SNIPPET_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.RED_SNIPPET_WRITER_))
      this.addWriter(FrameworkBaselineWriters.RED_SNIPPET_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.INDEXING_WRITER_))
      this.addWriter(FrameworkBaselineWriters.INDEXING_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.TEXT_WRITER_))
      this.addWriter(FrameworkBaselineWriters.TEXT_WRITER, args);
    
    if (pOutputFormat.contains(FrameworkBaselineWriters.PIPED_TEXT_WRITER_))
      this.addWriter(FrameworkBaselineWriters.PIPED_TEXT_WRITER, args);
    
    if (pOutputFormat.contains(FrameworkBaselineWriters.ONE_COLUMN_TEXT_WRITER_))
      this.addWriter(FrameworkBaselineWriters.ONE_COLUMN_TEXT_WRITER, args);
    
    if (pOutputFormat.contains(FrameworkBaselineWriters.TIU_NOTE_WRITER_))
      this.addWriter(FrameworkBaselineWriters.TIU_NOTE_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.EVALUATE_WRITER_))
      this.addWriter(FrameworkBaselineWriters.EVALUATE_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.HTML_WRITER_))
      this.addWriter(FrameworkBaselineWriters.HTML_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.NGRAM_WRITER_))
      this.addWriter(FrameworkBaselineWriters.NGRAM_WRITER, args);
    if (pOutputFormat.equals(FrameworkBaselineWriters.CSV_WRITER_))
      this.addWriter(FrameworkBaselineWriters.CSV_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.SECTION_WRITER_))
      this.addWriter(FrameworkBaselineWriters.SECTION_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.FREQ_WRITER_))
      this.addWriter(FrameworkBaselineWriters.FREQ_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.SOPHIA_WRITER_CSV_))
      this.addWriter(FrameworkBaselineWriters.SOPHIA_WRITER_CSV, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.CUI_INDEX_WRITER_))
      this.addWriter(FrameworkBaselineWriters.CUI_INDEX_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.PHRASE_VECTOR_WRITER_))
      this.addWriter(FrameworkBaselineWriters.PHRASE_VECTOR_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.WORD_FREQ_BY_DOCTYPE_WRITER_))
      this.addWriter(FrameworkBaselineWriters.WORD_FREQ_BY_DOCTYPE_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.STY_FREQ_BY_DOCTYPE_WRITER_))
      this.addWriter(FrameworkBaselineWriters.STY_FREQ_BY_DOCTYPE_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.JSON_WRITER_))
      this.addWriter(FrameworkBaselineWriters.JSON_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.BRAT_WRITER_))
      this.addWriter(FrameworkBaselineWriters.BRAT_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.REFINERY_VTT_WRITER_))
      this.addWriter(FrameworkBaselineWriters.REFINERY_VTT_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.FRAMEWORK_OBJECT_WRITER_))
      this.addWriter(FrameworkBaselineWriters.FRAMEWORK_OBJECT_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.GATE_WRITER_))
      this.addWriter(FrameworkBaselineWriters.GATE_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.GATE_CORPUS_WRITER_))
    this.addWriter(FrameworkBaselineWriters.GATE_CORPUS_WRITER, args);
    if (pOutputFormat.contains(FrameworkBaselineWriters.FORMAT_VARIABILITY_WRITER_))
      this.addWriter(FrameworkBaselineWriters.FORMAT_VARIABILITY_WRITER, args);

    if (pOutputFormat.contains(FrameworkBaselineWriters.SECTION_NAME_FREQ_WRITER_))
      this.addWriter(FrameworkBaselineWriters.SECTION_NAME_FREQ_WRITER, args);

    

  } // End Method addWriters() ======================

  // =======================================================
  /**
   * createWriters creates the writers that will transform the processed cas's
   * to output files.
   * 
   * @param pWriterType the writer type
   * @param pArgs the args
   */
  // =======================================================
  public void addWriter(int pWriterType, String[] pArgs) {

    int serverNumber = 0;
    if (this.writers != null)
      serverNumber = this.writers.size() + 1;

    addWriter(pWriterType, pArgs, serverNumber);

  } // end Method addWriter() --------------------------------

  // =======================================================
  /**
   * createWriters creates the writers that will transform the processed cas's
   * to output files.
   * 
   * @param pTypeOfWriter (see the enumeration of XXX_WRITERs
   * @param pArgs the args
   * @param pServerNumber -- ties the thread id to the writer name
   * @return Writer
   */
  // =======================================================
  public synchronized Writer addWriter(int pTypeOfWriter, String[] pArgs, int pServerNumber) {

    if (this.writers == null)
      this.writers = new HashSet<Writer>();

    Writer newWriter = createWriter(pTypeOfWriter, pArgs, pServerNumber);
    this.writers.add(newWriter);

    return newWriter;
  } // end Method addWriter() --------------------------------

  // =======================================================
  /**
   * createWriters creates the writers that will transform the processed cas's
   * to output files.
   * 
   * @param pTypeOfWriter (see the enumeration of XXX_WRITERs
   * @param pArgs the args
   * @param serverNumber the server number
   * @return the writer
   */
  // =======================================================
  public Writer createWriter(int pTypeOfWriter, String[] pArgs, int serverNumber) {

    String[] args = U.addArg(pArgs, "--servername=", this.serverName);
    Writer returnVal = null;

    try {
      switch (pTypeOfWriter) {

        case XMI_WRITER:
          returnVal = (new ToXMI(args));
          break;
         case KNOWTATOR_WRITER:
         returnVal =  new ToKnowtator(args);
         break;
        
        case VTT_WRITER:
          returnVal = createVTTWriter(args);
          break;
        case STATS_WRITER:
          returnVal = new CorpusStatsWriter(args, serverNumber);
          break;
        case STATS_WRITER_DB:
          returnVal = (new CorpusStatsWriterDb(args));
          break;
        // case CONCORDANCE_WRITER:
        // returnVal = createConcordanceWriter(args);
        // break;
        // case MULTI_RECORD_FILE_WRITER:
        // returnVal = createMultiRecordFileWriter(args);
        // break;
        case COMMON_MODEL_WRITER:
         // returnVal = createCommonModelWriter(args);
          break;
        case COMMON_MODEL_STRING:
       //   returnVal = createCommonModelString(args);
          break;
        // case SIMPLE_FORMAT_WRITER:
        // returnVal = createSimpleFormatWriter(args);
        // break;
        case STRING:
          returnVal = createString(args);
          break;
        case SNIPPET_WRITER:
          returnVal = (new SnippetsWriter(args));
          break;
        // case RED_SNIPPET_WRITER:
        // returnVal = (new RedSnippetsWriter(args));
        // break;
        case INDEXING_WRITER:
          returnVal = createIndexingWriter(args);
          break;
        // case TIU_NOTE_WRITER:
        // returnVal = createTIU_NoteWriter(args);
        // break;
        case TEXT_WRITER:
          returnVal = new ToText(args);
           break;
           
        case PIPED_TEXT_WRITER:
          returnVal = new ToPipedText(args);
           break;
           
           
        case ONE_COLUMN_TEXT_WRITER:
          returnVal = new ToOneColumnText(args);
           break;
           
        case EVALUATE_WRITER:
          returnVal = createEvaluateWriter(args);
          break;
          
        case TOKEN_EVALUATE_WRITER:
          returnVal = new TokenEvaluateWriter(args);
          break;
        case HTML_WRITER:
          returnVal = createHTMLWriter(args);
          break;
        case NGRAM_WRITER:
          returnVal = (new NGramWriter(args));
          break;
        case CSV_WRITER:
       //  returnVal = (new CoreCharacteristicsWriter(args));
          returnVal = new CSVWriter( args);
          break;
        case SECTION_WRITER:
          returnVal = (new SectionCSVWriter(args));
          break;
        case FREQ_WRITER:
          returnVal = (new FreqWriter(args));
          break;
        case SOPHIA_WRITER_CSV:
          returnVal = (new SophiaCSVWriter(args));
          break;
        case CUI_INDEX_WRITER:
          returnVal = (new CuiIndexWriter(args));
          break;
        case PHRASE_VECTOR_WRITER:
          returnVal = (new PhraseVectorWriter(args));
          break;
        case VECTOR_WRITER:
          returnVal = (new VectorWriter(args));
          break;
        case WORD_FREQ_BY_DOCTYPE_WRITER:
          returnVal = (new WordFreqByDocTypeWriter(args));
          break;
        case STY_FREQ_BY_DOCTYPE_WRITER:
          returnVal = (new SemanticTypeByDocTypeWriter(args));
          break;
       
        case BRAT_WRITER:
          returnVal = (new BratWriter(args));
          break;
        case REFINERY_VTT_WRITER:
          returnVal = (new ToVTTRefinery(args));
          break;
          
        case FRAMEWORK_OBJECT_WRITER:
          returnVal = (new FrameworkObjectWriter(args));
          break;

        case GATE_WRITER:
          returnVal = (new GateWriter(args));
          break;
          
        case GATE_CORPUS_WRITER:
          returnVal = (new GateCorpusWriter(args));
          break;
          
        case FORMAT_VARIABILITY_WRITER:
          returnVal = (new FormatVariabilityWriter(args));
          break;
          
        case SECTION_NAME_FREQ_WRITER:
          returnVal = (new SectionNameCatalogWriter(args));
          break;
          
        case BIO_WRITER:
          returnVal = (new ToBIO(args));
          break;
      
        default:
          returnVal = null;

         
      }
      String writerType = getTypeOfWriter(pTypeOfWriter);
      GLog.println(GLog.STD___LEVEL, this.getClass(), "createWriter", "Added writer " + writerType);
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createWriter",
          "Issue with adding this writer " + e.toString());
    }

    return returnVal;

  } // end Method createWriter() --------------------------

  // =======================================================
  /**
   * getTyptOfWriter.
   * 
   * @param pTypeOfWriter (see the enumeration of XXX_WRITERs
   * @return String
   */
  // =======================================================
  public static String getTypeOfWriter(int pTypeOfWriter) {

    String returnVal = null;
    switch (pTypeOfWriter) {

      case XMI_WRITER:
        returnVal = XMI_WRITER_;
        break;
      case KNOWTATOR_WRITER:
        returnVal = KNOWTATOR_WRITER_;
        break;
      case KNOWTATOR_SAMPLE_WRITER:
        returnVal = KNOWTATOR_SAMPLE_WRITER_;
        break;
      case VTT_WRITER:
        returnVal = VTT_WRITER_;
        break;
      case STATS_WRITER:
        returnVal = STATS_WRITER_;
        break;
      case STATS_WRITER_DB:
        returnVal = STATS_WRITER_DB_;
        break;
      case STATS_ACCESS_WRITER:
        returnVal = STATS_ACCESS_WRITER_;
        break;
      case CONCORDANCE_WRITER:
        returnVal = CONCORDANCE_WRITER_;
        break;
      case MULTI_RECORD_FILE_WRITER:
        returnVal = MULTI_RECORD_FILE_WRITER_;
        break;
      case COMMON_MODEL_WRITER:
        returnVal = COMMON_MODEL_WRITER_;
        break;
      case COMMON_MODEL_STRING:
        returnVal = COMMON_MODEL_STRING_;
        break;
      case BIOC_WRITER:
        returnVal = BIOC_WRITER_;
        break;
      case BIO_WRITER:
        returnVal = BIO_WRITER_;
        break;
      case SIMPLE_FORMAT_WRITER:
        returnVal = SIMPLE_FORMAT_WRITER_;
        break;
      case STRING:
        returnVal = STRING_;
        break;
      case SNIPPET_WRITER:
        returnVal = SNIPPET_WRITER_;
        break;
      case RED_SNIPPET_WRITER:
        returnVal = RED_SNIPPET_WRITER_;
        break;
      case INDEXING_WRITER:
        returnVal = INDEXING_WRITER_;
        break;
      case TIU_NOTE_WRITER:
        returnVal = TIU_NOTE_WRITER_;
        break;
      case EVALUATE_WRITER:
        returnVal = EVALUATE_WRITER_;
        break;
      case TOKEN_EVALUATE_WRITER:
        returnVal = TOKEN_EVALUATE_WRITER_;
        break;
      case HTML_WRITER:
        returnVal = HTML_WRITER_;
        break;
      case NGRAM_WRITER:
        returnVal = NGRAM_WRITER_;
        break;
      case CSV_WRITER:
        returnVal = CSV_WRITER_;
        break;
      case SECTION_WRITER:
        returnVal = SECTION_WRITER_;
        break;
      case FREQ_WRITER:
        returnVal = FREQ_WRITER_;
        break;
      case SOPHIA_WRITER_CSV:
        returnVal = SOPHIA_WRITER_CSV_;
        break;
      case CUI_INDEX_WRITER:
        returnVal = CUI_INDEX_WRITER_;
        break;
      case PHRASE_VECTOR_WRITER:
        returnVal = PHRASE_VECTOR_WRITER_;
        break;
      case VECTOR_WRITER:
        returnVal = VECTOR_WRITER_;
        break;
      case WORD_FREQ_BY_DOCTYPE_WRITER:
        returnVal = WORD_FREQ_BY_DOCTYPE_WRITER_;
        break;
      case STY_FREQ_BY_DOCTYPE_WRITER:
        returnVal = STY_FREQ_BY_DOCTYPE_WRITER_;
        break;
      case JSON_WRITER:
        returnVal = JSON_WRITER_;
        break;
      case BRAT_WRITER:
        returnVal = BRAT_WRITER_;
        break;
      case REFINERY_VTT_WRITER:
        returnVal = REFINERY_VTT_WRITER_;
        break;
        
      case FRAMEWORK_OBJECT_WRITER:
        returnVal = FRAMEWORK_OBJECT_WRITER_;
        break;
        
      case GATE_WRITER:
        returnVal = GATE_WRITER_;
        break;
        
      case GATE_CORPUS_WRITER:
        returnVal = GATE_CORPUS_WRITER_;
        break;
        
      case FORMAT_VARIABILITY_WRITER:
        returnVal = FORMAT_VARIABILITY_WRITER_;
        break;
        
      case SECTION_NAME_FREQ_WRITER:
        returnVal = SECTION_NAME_FREQ_WRITER_;
        break;

    
      case NONE_WRITER:
        returnVal = NONE_WRITER_;
        break;
      default:
        returnVal = NONE_WRITER_;
    }
    return returnVal;

  } // end Method getTypeOfWriter() --------------------------

  // ==========================================
  /**
   * getTypeOfWriterCode .
   *
   * @param pTypeOfWriter the type of writer
   * @return int
   */
  // ==========================================
  public static int getTypeOfWriterCode(String pTypeOfWriter) {

    int returnVal = NONE_WRITER;

    switch (pTypeOfWriter) {

      case XMI_WRITER_:
        returnVal = XMI_WRITER;
        break;
      case KNOWTATOR_WRITER_:
      case EHOST_WRITER_: 
        returnVal = KNOWTATOR_WRITER;
        break;
      case KNOWTATOR_SAMPLE_WRITER_:
        returnVal = KNOWTATOR_SAMPLE_WRITER;
        break;
      case VTT_WRITER_:
        returnVal = VTT_WRITER;
        break;
      case STATS_WRITER_:
        returnVal = STATS_WRITER;
        break;
      case STATS_WRITER_DB_:
        returnVal = STATS_WRITER_DB;
        break;
      case STATS_ACCESS_WRITER_:
        returnVal = STATS_ACCESS_WRITER;
        break;
      case CONCORDANCE_WRITER_:
        returnVal = CONCORDANCE_WRITER;
        break;
      case MULTI_RECORD_FILE_WRITER_:
        returnVal = MULTI_RECORD_FILE_WRITER;
        break;
      case COMMON_MODEL_WRITER_:
        returnVal = COMMON_MODEL_WRITER;
        break;
      case BIOC_WRITER_:
        returnVal = BIOC_WRITER;
        break;
      case BIO_WRITER_:
        returnVal = BIO_WRITER;
        break;
      case SIMPLE_FORMAT_WRITER_:
        returnVal = SIMPLE_FORMAT_WRITER;
        break;
      case STRING_:
        returnVal = STRING;
        break;
      case SNIPPET_WRITER_:
        returnVal = SNIPPET_WRITER;
        break;
      case RED_SNIPPET_WRITER_:
        returnVal = RED_SNIPPET_WRITER;
        break;
      case INDEXING_WRITER_:
        returnVal = INDEXING_WRITER;
        break;
      case TIU_NOTE_WRITER_:
        returnVal = TIU_NOTE_WRITER;
        break;
      case EVALUATE_WRITER_:
        returnVal = EVALUATE_WRITER;
        break;
      case TOKEN_EVALUATE_WRITER_:
        returnVal = TOKEN_EVALUATE_WRITER;
        break;
      case HTML_WRITER_:
        returnVal = HTML_WRITER;
        break;
      case NGRAM_WRITER_:
        returnVal = NGRAM_WRITER;
        break;
      case CSV_WRITER_:
        returnVal = CSV_WRITER;
        break;
      case SECTION_WRITER_:
        returnVal = SECTION_WRITER;
        break;
      case FREQ_WRITER_:
        returnVal = FREQ_WRITER;
        break;
      case SOPHIA_WRITER_CSV_:
        returnVal = SOPHIA_WRITER_CSV;
        break;
      case CUI_INDEX_WRITER_:
        returnVal = CUI_INDEX_WRITER;
        break;
      case PHRASE_VECTOR_WRITER_:
        returnVal = PHRASE_VECTOR_WRITER;
        break;
      case VECTOR_WRITER_:
        returnVal = VECTOR_WRITER;
        break;
      case WORD_FREQ_BY_DOCTYPE_WRITER_:
        returnVal = WORD_FREQ_BY_DOCTYPE_WRITER;
        break;
      case STY_FREQ_BY_DOCTYPE_WRITER_:
        returnVal = STY_FREQ_BY_DOCTYPE_WRITER;
        break;
      case JSON_WRITER_:
        returnVal = JSON_WRITER;
        break;
      case BRAT_WRITER_:
        returnVal = BRAT_WRITER;
        break;
      case REFINERY_VTT_WRITER_:
        returnVal = REFINERY_VTT_WRITER;
        break;
      case FRAMEWORK_OBJECT_WRITER_:
        returnVal = FRAMEWORK_OBJECT_WRITER;
        break;
        
      case GATE_WRITER_:
        returnVal = GATE_WRITER;
        break;
        
      case GATE_CORPUS_WRITER_:
        returnVal = GATE_CORPUS_WRITER;
        break;
        
      case FORMAT_VARIABILITY_WRITER_:
        returnVal = FORMAT_VARIABILITY_WRITER;
        break;
        
      case SECTION_NAME_FREQ_WRITER_:
        returnVal = SECTION_NAME_FREQ_WRITER;
        break;
        
      case NONE_WRITER_:
        returnVal = NONE_WRITER;
        break;
      default:
        returnVal = NONE_WRITER;
    }
    return returnVal;

  } // end Method getTypeOfWriterCode() =======

  // =======================================================
  /**
   * addWriter adds a generic uima writer.
   * 
   * @param pWriter the writer
   */
  // =======================================================
  public void addWriter(Writer pWriter) {
    if (this.writers == null)
      this.writers = new HashSet<Writer>();
    this.writers.add(pWriter);
  } // End Method addWriter() ======================

  // =======================================================
  /**
   * createMultiRecordFileWriter creates a multiRecordFileWriter writer.
   * 
   * @param pArgs the args
   * @return Writer
   */
  // =======================================================
  // public Writer createMultiRecordFileWriter(String[] pArgs) {
  //
  // ToMultiRecordFile multiRecordFileWriter = null;
  //
  // String outputDir = U.getOption(pArgs, "--outputDir=", "/data/output/");
  // String multiRecordFileOutputDir = outputDir + "/multiRecordFileOutput";
  // String sRecordsPerFile = U.getOption(pArgs, "--recordsPerFile=", "1000");
  // int recordsPerFile = Integer.parseInt(sRecordsPerFile);
  //
  // try {
  //
  // multiRecordFileWriter = new ToMultiRecordFile(multiRecordFileOutputDir,
  // recordsPerFile);
  //
  // } catch (Exception e5) {
  // e5.getStackTrace();
  // String msg = "Issue with creating the multiRecordsPerFile writer \n" +
  // e5.getMessage() + "\n";
  // GLog.println(GLog.ERROR_LEVEL, this.getClass(),
  // "createMultiRecordFileWriter", msg);
  // throw new RuntimeException(msg);
  //
  // }
  // return multiRecordFileWriter;
  // } // End Method createMultiRecordFileWriter() ======================

  // // =======================================================
  // /**
  // * createConcordanceWriter creates a concordance writer.
  // *
  // * @param pArgs the args
  // * @return Writer
  // */
  // // =======================================================
  // public Writer createConcordanceWriter(String[] pArgs) {
  //
  // ToVTTConcordance concordanceWriter = null;
  //
  // String outputDir = U.getOption(pArgs, "--outputDir=", "/data/output");
  // String concordanceOutputDir = outputDir + "/concordance";
  // String focusLabel = U.getOption(pArgs, "--focusLabel=", "Concept");
  //
  // try {
  //
  // // concordanceWriter = new ToVTTConcordance( concordanceOutputDir,
  // // focusLabel, serviceName );
  // concordanceWriter = new ToVTTConcordance();
  // concordanceWriter.initialize(concordanceOutputDir, focusLabel);
  //
  // } catch (Exception e5) {
  // e5.getStackTrace();
  // String msg = "Issue with creating the concordance writer \n" +
  // e5.getMessage() + "\n";
  // GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createConcordanceWriter",
  // msg);
  // throw new RuntimeException(msg);
  //
  // }
  // return concordanceWriter;
  // } // End Method createConcordanceWriter() ======================

  // =======================================================
  /**
   * createIndexingWriter creates an indexing writer.
   * 
   * @param pArgs the args
   * @return Writer
   */
  // =======================================================
  public Writer createIndexingWriter(String[] pArgs) {

    IndexingWriter indexingWriter = null;

    try {

      indexingWriter = new IndexingWriter(pArgs);

    } catch (Exception e) {
      e.getStackTrace();
      String msg = "Issue with creating the indexing writer " + e.getMessage() + "\n";
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createIndexingWriter", msg);
      throw new RuntimeException(msg);
    }
    return indexingWriter;
  } // End Method createIndexingWriter() ======================

  //// =======================================================
  /// **
  // * createTIU_NoteWriter creates a tiu note writer.
  // *
  // * @param pArgs the args
  // * @return Writer
  // */
  //// =======================================================
  // public Writer createTIU_NoteWriter(String[] pArgs) {
  //
  // ToH2TIU_Notes tiuNoteWriter = null;
  //
  // try {
  //
  // tiuNoteWriter = new ToH2TIU_Notes( pArgs );
  //
  // } catch (Exception e) {
  // e.getStackTrace();
  // String msg = "Issue with creating the tiuNote writer " + e.getMessage() +
  //// "\n" ;
  // GLog.println( GLog.ERROR_LEVEL, this.getClass(), "createTIU_NoteWriter",
  //// msg );
  // throw new RuntimeException(msg);
  // }
  // return tiuNoteWriter;
  // } // End Method createTIU_NoteWriter() ======================

  // =======================================================
  /**
   * createEvaluateWriter creates an evaluation writer.
   * 
   * @param pArgs the args
   * @return Writer
   */
  // =======================================================
  public Writer createEvaluateWriter(String[] pArgs) {

    EvaluateWriter evaluateWriter = null;

    try {

      evaluateWriter = new EvaluateWriter(pArgs);

    } catch (Exception e) {
      e.getStackTrace();
      String msg = "Issue with creating the evaluate writer " + e.getMessage();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createEvaluationWriter", msg);
      throw new RuntimeException(msg);
    }
    return evaluateWriter;
  } // End Method createEvaluateWriter() ======================
  
  


  // =======================================================
  /**
   * createHTMLWriter creates an html writer.
   * 
   * @param pArgs the args
   * @return Writer
   */
  // =======================================================
  public Writer createHTMLWriter(String[] pArgs) {

    HTMLWriter htmlWriter = null;

    try {

      htmlWriter = new HTMLWriter(pArgs);

    } catch (Exception e) {
      e.getStackTrace();
      String msg = "Issue with creating the html writer " + e.getMessage();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createHTMLWriter", msg);
      throw new RuntimeException(msg);
    }
    return htmlWriter;
  } // End Method createHTMLWriter() ======================

  // =======================================================
  /**
   * createStatsWriter creates a stats writer.
   * 
   * @param pArgs the args
   * @param serverNumber the server number
   * @return Writer
   */
  // // =======================================================
  // public Writer createStatsWriter(String[] pArgs, int serverNumber) {
  //
  // CorpusStatsWriter statsWriter = null;
  //
  // String outputDir = U.getOption(pArgs, "--outputDir=", "/data/output/");
  // String statsOutputDir = outputDir + "/stats";
  // String outputTypez = U.getOption(pArgs, "--outputTypes=", "");
  //
  // String outputTypes[] = null;
  // if (outputTypez.contains(":"))
  // outputTypes = U.split(outputTypez, ":");
  // else
  // outputTypes = U.split(outputTypez);
  //
  // try {
  //
  // statsWriter = new CorpusStatsWriter(statsOutputDir, outputTypes,
  // serverNumber);
  //
  // } catch (Exception e5) {
  // e5.getStackTrace();
  // String msg = "Issue with creating the stats writer \n" + e5.getMessage() +
  // "\n";
  // GLog.println(GLog.ERROR_LEVEL, this.getClass(), "statsWriter", msg);
  // throw new RuntimeException(msg);
  //
  // }
  // return statsWriter;
  // } // End Method createStatsWriter() ======================

  // =======================================================
  /**
   * createCommonModelWriter creates a multiRecordFileWriter writer.
   * 
   * @param pArgs the args
   * @return Writer
   */
  // =======================================================
  /*
  public Writer createCommonModelWriter(String[] pArgs) {

    ToCommonModel commonModelWriter = null;

    try {
      String outputDir = U.getOption(pArgs, "--outputDir=", "/data/output/");
      String commonModelOutputDir = outputDir + "/commonModel";
      String outputTypez = U.getOption(pArgs, "--outputTypes=", "");

      commonModelWriter = new ToCommonModel(commonModelOutputDir, outputTypez);

    } catch (Exception e5) {
      e5.getStackTrace();
      String msg = "Issue with creating the multiRecordsPerFile writer \n" + e5.getMessage() + "\n";
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createCommonModelWriter", msg);
      throw new RuntimeException(msg);

    }
    return commonModelWriter;
  } // End Method createConcordanceWriter() ======================

  // end Method createWriters() ---------------------
*/
  // =======================================================
  /**
   * createCommonModelString .
   * 
   * @param pArgs the args
   * @return the writer
   */
  // =======================================================
 /*
  protected Writer createCommonModelString(String[] pArgs) {

    ToCommonModelString commonModelWriter = null;

    try {

      String outputTypez = U.getOption(pArgs, "--outputTypes=", "");

      this.outputStrings = new ArrayList<String>();

      commonModelWriter = new ToCommonModelString(this.outputStrings, outputTypez);

    } catch (Exception e5) {
      e5.getStackTrace();
      String msg = "Issue with creating the multiRecordsPerFile writer \n" + e5.getMessage() + "\n";
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createCommonModelStringWriter", msg);
      throw new RuntimeException(msg);

    }
    return commonModelWriter;
  } // End Method createCommonModelString() ======================
*/
  // =======================================================
  /**
   * createString [Summary here].
   * 
   * @param pArgs the args
   * @return the writer
   */
  // =======================================================
  protected Writer createString(String[] pArgs) {

    ToString commonModelWriter = null;

    try {

      this.outputStrings = new ArrayList<String>();

      commonModelWriter = new ToString();
      commonModelWriter.initialize(this.outputStrings);

    } catch (Exception e5) {
      e5.getStackTrace();
      String msg = "Issue with creating the multiRecordsPerFile writer \n" + e5.getMessage() + "\n";
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createString", msg);
      throw new RuntimeException(msg);

    }
    return commonModelWriter;
  } // End Method createCommonModelString() ======================

  // =======================================================
  /**
   * createVTTWriter creates a VTT writer.
   * 
   * @param pArgs the args
   * @return Writer
   */
  // =======================================================
  public Writer createVTTWriter(String[] pArgs) {

    ToVTT vttWriter = null;

    String outputDir = U.getOption(pArgs, "--outputDir=", "/data/output/");
    String vttOutputDir = outputDir + "/vtt";
    String outputTypez = U.getOption(pArgs, "--outputTypes=", "Concept");

    String outputTypes[] = U.split(outputTypez, ":");

    try {

      vttWriter = new ToVTT(vttOutputDir, outputTypes);

    } catch (Exception e5) {
      e5.getStackTrace();
      String msg = "Issue with creating the vtt writer \n" + e5.getMessage() + "\n";
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createVTTWriter", msg);
      throw new RuntimeException(msg);

    }

    return vttWriter;
  } // End Method createVTTWriter() ======================

  // =======================================================
  /**
   * createKnowtatorWriter creates a knowtator writer.
   * 
   * @param pArgs the args
   * @return Writer
   */
   // =======================================================
    public Writer createKnowtatorWriter(String[] pArgs) {
  
   ToKnowtator knowtatorWriter = null;
  
   try {
  
   knowtatorWriter = new ToKnowtator(pArgs); // eHostWorkSpace, eHostProject,
  // // outputTypes);
  //
   } catch (Exception e5) {
   e5.getStackTrace();
   String msg = "Issue with creating the knowtator writer \n" +
   e5.getMessage() + "\n";
   GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createKnowtatorWriter",
   msg);
   throw new RuntimeException(msg);
  
  }
   return knowtatorWriter;
  
   } // End Method createKnowtatorWriter() ======================
  //
  // // =======================================================
  // /**
  // * createKnowtatorSampleWriter creates a knowtator writer.
  // *
  // * @param pArgs the args
  // * @return Writer
  // */
  // // =======================================================
  // public Writer createKnowtatorSampleWriter(String[] pArgs) {
  //
  // ToKnowtator knowtatorWriter = null;
  // String serviceName = U.getOption(pArgs, "--serviceName=",
  // "v3NLPFramework"); // aka
  // // eHostWorkspace
  // String outputDir = U.getOption(pArgs, "--outputDir=", "/data/output/");
  // String eHostWorkSpace = outputDir;
  // String eHostProject = serviceName;
  // String outputTypez = U.getOption(pArgs, "--outputTypes=", "");
  //
  // String outputTypes[] = null;
  // if (outputTypez.indexOf("|") > -1)
  // outputTypes = U.split(outputTypez);
  // else
  // outputTypes = U.split(outputTypez, ":");
  // int sampleRate = Integer.parseInt(U.getOption(pArgs, "--sampleRate=",
  // "10"));
  //
  // try {
  //
  // knowtatorWriter =
  // new ToKnowtatorSample(eHostWorkSpace, eHostProject, outputTypes,
  // sampleRate);
  //
  // } catch (Exception e5) {
  // e5.getStackTrace();
  // String msg = "Issue with creating the knowtator writer \n" +
  // e5.getMessage() + "\n";
  // GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createKnowtatorWriter",
  // msg);
  // throw new RuntimeException(msg);
  //
  // }
  // return knowtatorWriter;
  //
  // } // End Method createKnowtatorWriter() ======================

  // =======================================================
  /**
   * createSimpleFormatWriter creates a bioC writer.
   * 
   * @param pArgs the args
   * @return Writer
   */
  // =======================================================
  // public Writer createSimpleFormatWriter(String[] pArgs) {
  //
  // ToSimpleFormat sfWriter = null;
  //
  // String outputDir = U.getOption(pArgs, "--outputDir=", "/data/output/");
  // String _outputDir = outputDir + "/sf";
  // String outputTypez = U.getOption(pArgs, "--outputTypes=", "");
  // String annotator = U.getOption(pArgs, "--annotator=", "v3NLPFramework");
  //
  // String outputTypes[] = U.split(outputTypez, ":");
  //
  // try {
  //
  // sfWriter = new ToSimpleFormat(_outputDir, outputTypes, annotator);
  //
  // } catch (Exception e5) {
  // e5.getStackTrace();
  // String msg = "Issue with creating the bioC writer " + e5.getMessage();
  // GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createBioCWriter", msg);
  // throw new RuntimeException(msg);
  //
  // }
  //
  // return sfWriter;
  // } // End Method createBioCWriter() ======================

  // =======================================================
  /**
   * write writes out the processed cas using the created writers.
   * 
   * @param jCas the j cas
   */
  // =======================================================
  public void write(JCas jCas) {

    if (this.writers != null) {

      for (Writer writer : this.writers) {

        if (writer != null) {
          try {
            writer.process(jCas);

          } catch (Exception e1) {
            e1.printStackTrace();
            String msg = "Issue with writing the file out using  " + writer.getClass().getName()
                + e1.toString();
            GLog.println(GLog.ERROR_LEVEL, this.getClass(), "write", msg);
            continue;
          }
        }

      } // end loop through the writers
    } // end if there are any writers

  } // End Method write() ======================

  // =======================================================
  /**
   * getWriters.
   * 
   * @return List<Writer>
   */
  // =======================================================
  public Set<Writer> getWriters() {
    return this.writers;

  } // End Method getWriters() ===========================

} // End Class FrameworkBaselineApplication
