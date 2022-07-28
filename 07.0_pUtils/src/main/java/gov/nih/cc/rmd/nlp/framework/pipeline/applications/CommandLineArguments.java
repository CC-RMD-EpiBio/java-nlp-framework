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
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import gov.nih.cc.rmd.nlp.framework.utils.U;

// ------------------------------------------------------------
/**
 * CommandLineArguments is a container that holds all the command line arguments
 * to pass to an application.
 *
 * @author divita July 10, 2015
 * 
 *         ------------------------------------------------------------
 *         Services Research & Development Service
 * 
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at
 * 
 * 
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License.
 *         -------------------------------------------------------------
 */
// -------------------------------------------------------------

public class CommandLineArguments {

  /** The default input file. */
  File defaultInputFile = new File("/data/input"); // should be picked up from a
                                                   // config file during init()

  /**
   * Returns the default input file.
   *
   * @return the defaultInputFile
   */
  public final File getDefaultInputFile() {
    return defaultInputFile;
  }

  /**
   * Sets the default input file.
   *
   * @param defaultInputFile the defaultInputFile to set
   */
  public final void setDefaultInputFile(File defaultInputFile) {
    this.defaultInputFile = defaultInputFile;
  }

  /**
   * Returns the default output dir.
   *
   * @return the defaultOutputDir
   */
  public final File getDefaultOutputDir() {
    return defaultOutputDir;
  }

  /**
   * Sets the default output dir.
   *
   * @param defaultOutputDir the defaultOutputDir to set
   */
  public final void setDefaultOutputDir(File defaultOutputDir) {
    this.defaultOutputDir = defaultOutputDir;
  }

  /**
   * Returns the default input dir.
   *
   * @return the defaultInputDir
   */
  public final File getDefaultInputDir() {
    return defaultInputDir;
  }

  /**
   * Sets the default input dir.
   *
   * @param defaultInputDir the defaultInputDir to set
   */
  public final void setDefaultInputDir(File defaultInputDir) {
    this.defaultInputDir = defaultInputDir;
  }

  /**
   * Returns the input file.
   *
   * @return the inputFile
   */
  public final File getInputFile() {
    return inputFile;
  }

  /**
   * Sets the input file.
   *
   * @param inputFile the inputFile to set
   */
  public final void setInputFile(File inputFile) {
    this.inputFile = inputFile;
  }

  /**
   * Returns the input dir.
   *
   * @return the inputDir
   */
  public final File getInputDir() {
    return inputDir;
  }

  /**
   * Sets the input dir.
   *
   * @param inputDir the inputDir to set
   */
  public final void setInputDir(File inputDir) {
    this.inputDir = inputDir;
  }

  /**
   * Returns the output dir.
   *
   * @return the outputDir
   */
  public final File getOutputDir() {
    return outputDir;
  }

  /**
   * Sets the output dir.
   *
   * @param outputDir the outputDir to set
   */
  public final void setOutputDir(File outputDir) {
    this.outputDir = outputDir;
  }

  /**
   * Returns the tool.
   *
   * @return the tool
   */
  public final String getTool() {
    return tool;
  }

  /**
   * Sets the tool.
   *
   * @param tool the tool to set
   */
  public final void setTool(String tool) {
    System.err.println("setting the tool to " + tool);
    this.tool = tool;
  }

  /**
   * Returns the output format.
   *
   * @return the outputFormat
   */
  public final String getOutputFormat() {
    return outputFormat;
  }

  /**
   * Returns the default annotation editor.
   *
   * @return the annotationEditors
   */
  public final int getDefaultAnnotationEditor() {
    return this.defaultAnnotationEditor;
  }

  // =======================================================
  /**
   * setInputFormat values can be from one of these FrameworkBaselinePipeline
   * constants. TEXT_READER_ | XMI_READER_ | KNOWTATOR_READER_ | VTT_READER_ |
   * MULTI_RECORD_FILE_READER_ | MULTI_RECORD_FILE_BY_PATIENT_READER_ |
   * JCAS_READER_ | COMMON_MODEL_READER_ | VINCI_MRSA_DATABASE_READER_ |
   * VINCI_DATABASE_READER_ | JDBC_DATABASE_READER_ | STRING_READER_ |
   * SIMPLE_FORMAT_READER_ | BIOC_READER_ |
   * 
   *
   * @param pInputFormat the input format
   */
  // =======================================================
  public void setInputFormat(String pInputFormat) {
    this.inputFormat = pInputFormat;

  } // End Method setInputFormat() ======================

  // =======================================================
  /**
   * getInputFormat .
   *
   * @return String
   */
  // =======================================================
  public String getInputFormat() {
    return this.inputFormat;

  } // End Method setInputFormat() ======================

  /**
   * Sets the output format.
   *
   * @param outputFormat the outputFormat to set
   */
  public final void setOutputFormat(String outputFormat) {
    this.outputFormat = outputFormat;
  }

  /**
   * Returns the command line options.
   *
   * @return the commandLineOptions
   */
  public final String getCommandLineOptions() {
    return commandLineOptions;
  }

  /**
   * Sets the command line options.
   *
   * @param commandLineOptions the commandLineOptions to set
   */
  public final void setCommandLineOptions(String commandLineOptions) {
    this.commandLineOptions = commandLineOptions;
  }

  /**
   * Returns the number to process.
   *
   * @return the numberToProcess
   */
  public final String getNumberToProcess() {
    return numberToProcess;
  }

  /**
   * Sets the number to process.
   *
   * @param numberToProcess the numberToProcess to set
   */
  public final void setNumberToProcess(String numberToProcess) {
    this.numberToProcess = numberToProcess;
  }

  // End Method getArgs() ======================

  // =======================================================
  /**
   * getCommand.
   *
   * @return String
   */
  // =======================================================
  public String getCommand() {

    StringBuffer buff = new StringBuffer();

    // --------------------------
    // Java VM options
    buff.append("java -Xms" + this.initialMemory);
    buff.append(" ");
    System.err.println(buff);

    // -------------------------------------
    // get the jar file this is coming from
    buff.append("-jar ");
    buff.append(this.getJarFileName());
    buff.append(" ");

    // ------------------------------------
    // get the class (and main) to invoke
    buff.append(this.getMainClass());

    // --------------------------------
    // get the args to invoke
    String[] __args = getArgs();
    for (String a_arg : __args) {
      buff.append("           " + a_arg + "\n");

    }

    return buff.toString();

  } // End Method getCommand() ======================

  // =======================================================
  /**
   * getMainClass.
   *
   * @return String
   */
  // =======================================================
  public String getMainClass() {
    return this.mainClass;
  } // End Method getMaainClass() ======================

  // =======================================================
  /**
   * setMainClass.
   *
   * @param pClassName the main class
   */
  // =======================================================
  public void setMainClass(String pClassName) {
    this.mainClass = pClassName;
  } // End Method setMainClass() ======================

  // =======================================================
  /**
   * getJarFileName.
   *
   * @return String
   */
  // =======================================================
  public String getJarFileName() {
    return this.jarFileName;
  } // End Method getJarFile() ======================

  /**
   * Returns the jack version.
   *
   * @return the jack version
   */
  public String getJackVersion() {
    return this.jackVersion;
  }

  // =======================================================
  /**
   * getJarFile.
   *
   * @param pJarFileName the jar file
   */
  // =======================================================
  public void setJarFile(String pJarFileName) {
    this.jarFileName = pJarFileName;
  } // End Method setJarFile() ======================

  /* see superclass */
  @Override
  public String toString() {

    StringBuffer buff = new StringBuffer();
    String[] args = getArgs();
    for (String arg : args)
      buff.append(arg + "|");
    return buff.toString();
  }

  /**
   * To string array.
   *
   * @return the string[]
   */
  public String[] toStringArray() {
    return getArgs();

  } // End Method StringArray() ======================

  /**
   * Update output formats.
   *
   * @param outputFormatCheckBoxNames the output format check box names
   * @param pOutputFormatCheckBoxValues the output format check box values
   */
  public void updateOutputFormats(String[] outputFormatCheckBoxNames,
    boolean[] pOutputFormatCheckBoxValues) {

    this.outputFormatCheckboxes = pOutputFormatCheckBoxValues;
    StringBuffer buff = new StringBuffer();
    for (int i = 0; i < pOutputFormatCheckBoxValues.length; i++) {
      if (pOutputFormatCheckBoxValues[i])
        buff.append(outputFormatCheckBoxNames[i] + ":");
    }

    this.outputFormat = buff.toString();
  }// End Method updateOutputFormats() ======================

  // =======================================================
  /**
   * getOutputFormatCheckboxes [Summary here].
   *
   * @return boolean
   */
  // =======================================================
  public boolean[] getOutputFormatCheckboxes() {

    return this.outputFormatCheckboxes;
  } // End Method getOutputFormatCheckboxes() ======================

  // =======================================================
  /**
   * setOutputFormatCheckboxes.
   *
   * @param pOutputFormatCheckBoxes the output format check boxes
   */
  // =======================================================
  public void OutputFormatCheckboxes(boolean pOutputFormatCheckBoxes[]) {

    this.outputFormatCheckboxes = pOutputFormatCheckBoxes;

  } // End Method setOutputFormatCheckboxes() ======================

  // =======================================================
  /**
   * updateAnnotationEditors.
   *
   * @param pAnnotationEditorCheckBoxId the annotation editor
   */
  // =======================================================
  public void setAnnotationEditor(int pAnnotationEditorCheckBoxId) {

    this.annotationEditor = pAnnotationEditorCheckBoxId;

  }// End Method updateAnnotationEditors() ======================

  // =======================================================
  /**
   * getAnnotationEditor [Summary here].
   *
   * @return the annotation editor
   */
  // =======================================================
  public int getAnnotationEditor() {
    return this.annotationEditor;
  } // End Method getAnnotationEditor() ======================

  // =======================================================
  /**
   * getAnnotationEditorCheckboxes [Summary here].
   *
   * @return boolean[]
   */
  // =======================================================
  public boolean[] getAnnotationEditorCheckboxes() {
    return this.annotationEditorCheckboxes;
  } // End Method getAnnotationEditorCheckboxes() ======================

  // ==========================================
  /**
   * setFocusType .
   *
   * @param pString the focus type
   */
  // ==========================================
  public void setFocusType(String pString) {
    this.focusLabel = pString;
    this.args = U.addArg(this.args, "--focusLabel=", this.focusLabel);
  } // end Method setFocusType() ==============

  // ==========================================
  /**
   * setOutputTypes .
   *
   * @param pOutputTypes the output types
   */
  // ==========================================
  public void setOutputTypes(String pOutputTypes) {

    this.args = U.addArg(this.args, "--outputTypes=", pOutputTypes);

  } // end Method setOutputTypes() ========================================

  // =======================================================
  /**
   * getConfigFile retrieves the configFileName
   * 
   * By default, this should be /opt/jack/config/jack.config if it exits,
   * otherwise $user/jack/config/jack.config
   * 
   * 
   * @return String
   */
  // =======================================================
  public String getConfigFile() {

    String userHome = System.getProperty("user.home");
    String jackVersion = this.getJackVersion();
    File homeConfigFile = new File(userHome + "/jack/" + jackVersion + "/config/jack.config");
    File installedConfigFile = new File("/opt" + "/jack/" + jackVersion + "/config/jack.config");
    File currentConfigFile = new File(this.configFileName);

    String returnVal = homeConfigFile.getAbsolutePath();

    if (currentConfigFile.exists() && currentConfigFile.canRead()) {
      returnVal = currentConfigFile.getAbsolutePath();

    } else if (installedConfigFile.exists() && installedConfigFile.canRead()) {
      returnVal = installedConfigFile.getAbsolutePath();

    } else if (homeConfigFile.exists() && homeConfigFile.canRead()) {
      returnVal = homeConfigFile.getAbsolutePath();
    }

    return returnVal;

  } // End Method getConfigFile() ======================

  // =======================================================
  /**
   * getConfigFile will make the directory if the directory does not yet exist
   * and the pMakeDirIfNotExist is true.
   *
   * @param pMakeDirIfNotExist the make dir if not exist
   * @return File
   */
  // =======================================================
  public File getConfigFile(boolean pMakeDirIfNotExist) {

    String userDir = System.getProperty("user.home");
    String jackVersion = this.getJackVersion();
    String jackDir = userDir + "/jack/" + jackVersion + "/config";
    String initialConfigFileName = this.getConfigFile();

    File initialConfigFile = new File(initialConfigFileName);

    if (pMakeDirIfNotExist && !(initialConfigFile.exists() && initialConfigFile.canWrite())) {
      // ------------------------------------------------------
      // Create the user directory to store the initial version

      try {
        U.mkDir(jackDir);
        initialConfigFile = new File(jackDir + "/jack.config");
      } catch (Exception e3) {
        e3.printStackTrace();
        String msg = "Issue creating the jack directory " + jackDir + " " + e3.toString();
        System.err.println(msg);

      }
    }

    return initialConfigFile;
  } // end Method getInitialConfigFile() ===================

  // =======================================================
  /**
   * getArgs retrieves the args and has the side effect of populating this.args
   * Coordinate this with the parse, load and save methods.
   * 
   * @return String[]
   */
  // =======================================================
  public String[] getArgs() {

    this.args = new String[22];

    this.args[0] = "--inputDir=" + this.inputDir.getAbsolutePath();
    this.args[1] = "--inputFile=" + this.inputFile.getAbsolutePath();
    this.args[2] = "--outputDir=" + this.outputDir.getAbsolutePath();

    this.args[3] = "--tool=" + this.tool;
    this.args[4] = "--inputFormat=" + this.inputFormat;
    this.args[5] = "--outputFormat=" + this.outputFormat;
    this.args[6] = "--numberToProcess=" + this.numberToProcess;
    this.args[7] = "--initialMemory=" + this.initialMemory;
    this.args[8] = "--jarFileName=" + this.jarFileName;
    this.args[9] = "--mainClass=" + this.mainClass;

    // ---------------------
    // default options
    this.args[10] = "--defaultInputDir=" + this.defaultInputDir.getAbsolutePath();
    this.args[11] = "--defaultInputFile=" + this.defaultInputFile.getAbsolutePath();
    this.args[12] = "--defaultOutputDir=" + this.defaultOutputDir.getAbsolutePath();
    this.args[13] = "--defaultTool=" + this.defaultTool;

    this.args[14] = "--defaultAnnotationEditor=" + this.defaultAnnotationEditor;

    // ---------------------
    // Specific to Jack options
    this.args[15] = "--annotationEditor=" + this.annotationEditor;
    this.args[16] = "--configFile=" + this.configFileName;

    // ----------------------
    // used for regex pipelines

    this.args[17] = "--rulesXML=" + this.rulesXML;
    this.args[18] = "--rulesURL=" + this.rulesURL;
    this.args[19] = "--jackVersion=" + this.jackVersion;
    this.args[20] = "--focusLabel=" + this.focusLabel;
    this.args[21] = "--outputTypes=" + this.outputTypes;

    return args;
  }

  // =======================================================
  /**
   * load parses the contents of a configuration file that is filled with
   * command line arguments
   * 
   * These can be one key=value pair per line, or can be what would be put on
   * the command line
   * 
   * I.e., newlines are ignored.
   *
   * @param pInputConfigFileContents the input config file contents
   */
  // =======================================================
  public void load(String pInputConfigFileContents) {

    String inputContents = pInputConfigFileContents.replaceAll("\n", " ");
    parse(inputContents);

  } // End Method load() ======================

  // =======================================================
  /**
   * parse parses the string to populate - this method must know about all
   * options now. Coordinate this with the getArgs method.
   *
   * @param pCommandLineText the command line text
   * @return the string[]
   */
  // =======================================================
  public String[] parse(String pCommandLineText) {

    String args[] = U.split(pCommandLineText, " ");

    // -----------------------
    // Common options

    this.inputDir =
        new File(U.getOption(args, "--inputDir=", this.defaultInputDir.getAbsolutePath()));
    this.inputFile =
        new File(U.getOption(args, "--inputFile=", this.defaultInputFile.getAbsolutePath()));
    this.outputDir =
        new File(U.getOption(args, "--outputDir=", this.defaultOutputDir.getAbsolutePath()));

    this.tool = U.getOption(args, "--tool=", this.defaultTool);
    this.inputFormat = U.getOption(args, "--inputFormat=", this.defaultInputFormat);
    this.outputFormat = U.getOption(args, "--outputFormat=", defaultOutputFormat);
    this.numberToProcess = U.getOption(args, "--numberToProcess=", this.defaultNumberToProcess);
    this.initialMemory = U.getOption(args, "--initialMemory=", "2g");
    this.jarFileName = U.getOption(args, "--jarFileName=", "");
    this.mainClass = U.getOption(args, "--mainClass=", "");

    this.rulesXML = U.getOption(args, "--rulesXML=", "");// RegExAnnotator.ExampleRegExRule);
    this.rulesURL = U.getOption(args, "--rulesURL=", ""); // "/data/regexFiles/example1.xml";

    // ---------------------
    // default options
    this.defaultInputDir =
        new File(U.getOption(args, "--defaultInputDir=", this.defaultInputDir.getAbsolutePath()));
    this.defaultInputFile =
        new File(U.getOption(args, "--defaultInputFile=", this.defaultInputFile.getAbsolutePath()));
    this.defaultOutputDir =
        new File(U.getOption(args, "--defaultOutputDir=", this.defaultInputDir.getAbsolutePath()));
    this.defaultTool = U.getOption(args, "--defaultTool=", this.defaultTool);
    this.defaultInputFormat = U.getOption(args, "--outputFormat=", defaultOutputFormat);
    this.defaultNumberToProcess =
        U.getOption(args, "--numberToProcess=", this.defaultNumberToProcess);

    this.defaultAnnotationEditor = Integer.parseInt(U.getOption(args, "--defaultAnnotationEditor=",
        String.valueOf(this.defaultAnnotationEditor)));

    // ---------------------
    // Specific to Jack options
    this.annotationEditor = Integer.parseInt(
        U.getOption(args, "--annotationEditor=", String.valueOf(this.defaultAnnotationEditor)));
    this.configFileName = U.getOption(args, "--configFile=", this.configFileName);
    this.jackVersion = U.getOption(args, "--jackVersion=", this.jackVersion);
    this.focusLabel = U.getOption(args, "--focusLabel=", "Concept");
    this.outputTypes = U.getOption(args, "--outputTypes=", "Concept");

    // -------------------
    // Coordinate with the getArgs method
    this.args = getArgs();

    return this.args;

  } // End Method parse() ======================

  // =======================================================
  /**
   * setToolInstance.
   *
   * @param pInstance the tool instance
   */
  // =======================================================
  public void setToolInstance(Object pInstance) {
    this.instance = pInstance;
  } // End Method setToolInstance() ======================

  // =======================================================
  /**
   * save saves this.args String[] to the pConfigFileName.
   * 
   * Coordinate with the parse, and load methods.
   *
   * @param pConfigFileName the config file name
   * @throws FileNotFoundException the file not found exception
   */
  // =======================================================
  public void save(String pConfigFileName) throws FileNotFoundException {

    try (PrintWriter out = new PrintWriter(pConfigFileName);) {

      out.print("--inputDir=" + this.inputDir.getAbsolutePath() + "\n");
      out.print("--inputFile=" + this.inputFile.getAbsolutePath() + "\n");
      out.print("--outputDir=" + this.outputDir.getAbsolutePath() + "\n");

      out.print("--tool=" + this.tool + "\n");
      out.print("--inputFormat=" + this.inputFormat + "\n");
      out.print("--outputFormat=" + this.outputFormat + "\n");
      out.print("--numberToProcess=" + this.numberToProcess + "\n");
      out.print("--initialMemory=" + this.initialMemory + "\n");
      out.print("--jarFileName=" + this.jarFileName + "\n");
      out.print("--mainClass=" + this.mainClass + "\n");

      // ---------------------
      // default options
      out.print("--defaultInputDir=" + this.defaultInputDir.getAbsolutePath() + "\n");
      out.print("--defaultInputFile=" + this.defaultInputFile.getAbsolutePath() + "\n");
      out.print("--defaultOutputDir=" + this.defaultOutputDir.getAbsolutePath() + "\n");
      out.print("--defaultTool=" + this.defaultTool + "\n");
      out.print("--defaultAnnotationEditor=" + this.defaultAnnotationEditor + "\n");

      // ---------------------
      // Specific to Jack options
      out.print("--annotationEditor=" + this.annotationEditor + "\n");
      out.print("--configFile=" + this.configFileName + "\n");

      // ----------------------
      // used for regex pipelines

      out.print("--rulesXML=" + this.rulesXML + "\n");
      out.print("--rulesURL=" + this.rulesURL + "\n");
      out.print("--jackVersion=" + this.jackVersion + "\n");
      out.print("--focusLabel=" + this.focusLabel + "\n");
      out.print("--outputTypes=" + this.outputTypes + "\n");

    }

  } // End Method save() ======================

  // =======================================================
  /**
   * getToolInstance.
   *
   * @return the tool instance
   */
  // =======================================================
  public Object getToolInstance() {
    return this.instance;
  } // End Method getToolInstance() ======================

  // --------------------------
  // Global Variables

  /** The args. */
  String args[] = null;

  /** The default output dir. */
  File defaultOutputDir = new File("/data/output/jack"); // The config file
                                                         // should keep the last
                                                         // saved

  /** The default input dir. */
  // values
  File defaultInputDir = new File("/data/input/");

  /** The default tool. */
  String defaultTool = "Sophia";

  /** The default input format. */
  String defaultInputFormat = FrameworkReaders.TEXT_READER_;

  /** The default output format. */
  String defaultOutputFormat = FrameworkBaselineWriters.XMI_WRITER_;

  /** The default number to process. */
  String defaultNumberToProcess = "1";

  /** The input file. */
  File inputFile = defaultInputFile;

  /** The input dir. */
  File inputDir = defaultInputDir;

  /** The output dir. */
  File outputDir = defaultOutputDir;

  /** The tool. */
  String tool = defaultTool; // should be picked up from the config file

  /** The input format. */
  String inputFormat = FrameworkReaders.TEXT_READER_; //
                                                      // TEXT_READER|
                                                      // XMI_READER|KNOWTATOR_READER|
                                                      // VTT_READER|MULTI_RECORD_FILE_READER|
                                                      // MULTI_RECORD_FILE_BY_PATIENT_READER|
                                                      // JCAS_READER|COMMON_MODEL_READER|
                                                      // VINCI_DATABASE_READER|JDBC_READER|
                                                      // STRING_READER|
                                                      // SIMPLE_FORMAT_READER|
                                                      // BIOC_READER|

  /** The output format. */
  String outputFormat = FrameworkBaselineWriters.VTT_WRITER_; // These can
                                                              // be
                                                              // concatenated
                                                              // with ":"

  /** The output format checkboxes. */
  // delimiters
  boolean outputFormatCheckboxes[] = {
      false, true, false, false
  }; // vtt,xmi,ehost,excel

  /** The annotation editor checkboxes. */
  boolean annotationEditorCheckboxes[] = {
      true, false, false, false
  }; // vtt/uima/ehost/excel

  /** The default annotation editor. */
  int defaultAnnotationEditor = 0; // vtt;

  /** The annotation editor. */
  int annotationEditor = defaultAnnotationEditor;

  /** The command line options. */
  String commandLineOptions = ""; // " "

  /** The number to process. */
  String numberToProcess = defaultNumberToProcess;

  /** The instance. */
  Object instance = null; // this might not be the place for this, but
                          // it holds the tool/panel/application
                          // instance

  /** The initial memory. */
  String initialMemory = "2g";

  /** The jar file name. */
  String jarFileName = "unknown";

  /** The main class. */
  String mainClass = "unknown";

  /** The config file name. */
  String configFileName = "./jack.config";

  /** The rules XML. */
  String rulesXML = "";

  /** The rules URL. */
  String rulesURL = "";

  /** The jack version. */
  String jackVersion = "2016.04";

  /** The focus label. */
  String focusLabel = "Concept";

  /** The output types. */
  String outputTypes = "Concept";

} // end Class CommandLineArguments
