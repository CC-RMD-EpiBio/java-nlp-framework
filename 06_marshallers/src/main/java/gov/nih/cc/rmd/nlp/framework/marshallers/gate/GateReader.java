/*******************************************************************************
 * ---------------------------------------------------------------------------
 * NIH Clinical Center
 * Department of Rehabilitation
 * Epidemiology and Biostatistics Branch
 * 2019
 * 
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * 
 * This license allows you to use, share and adapt for any purpose, provided:
 * Provide attribution to the creators of this work within your work.
 * Indicate if changes were made to this work.
 * No claim to merchantability, implied warranty, or liability can be made.
 * 
 * When attributing this code, please make reference to
 * [citation/url here] .
 * 
 * In the absence of a specific paper or url listed above, reference http://clinicalcenter.nih.gov/rmd/eb/nlp
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 ******************************************************************************/
// =================================================
/**
 * GateReader reads Gate Documents and converts them to Cas's
 *
 * @author Guy Divita
 * @created March 15, 2019
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.gate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import gate.Document;
import gate.Gate;
import gate.corpora.DocumentContentImpl;
import gov.nih.cc.rmd.nlp.framework.marshallers.reader.AddNewLinesTool;
import gov.nih.cc.rmd.nlp.framework.utils.FileMetaData;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.GleanMetaDataFromFileName;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.va.chir.model.DocumentHeader;

// extends CollectionReader_ImplBase,

public class GateReader extends gov.nih.cc.rmd.nlp.framework.marshallers.text.FromText {

  private String gateHome;

  // =======================================================
  /**
   * Constructor
   *
   * @throws ResourceInitializationException
   */
  // =======================================================
  public GateReader() throws ResourceInitializationException {

    String[] args = null;
    initialize(args);

  } // end Constructor() ---------------------

  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws ResourceInitializationException
   * 
   **/
  // =================================================
  public GateReader(String[] pArgs) throws ResourceInitializationException {
    initialize(pArgs);

    // initialize Gate components
    try {

      // Tell Gate where the GATE HOME and PLUGIN directory is
      this.gateHome = U.getOption(pArgs, "--gateHome=", "C:/Program Files (x86)/GATE_Developer_8.5.1");

      if (Gate.getGateHome() == null) Gate.setGateHome(new File(this.gateHome));
      if (Gate.getPluginsHome() == null) Gate.setPluginsHome(new File(this.gateHome + "/plugins"));

      // register the needed plugins
      // Gate.getCreoleRegister().registerDirectories(new File(Gate.getPluginsHome(), "yourPlugin").toURI().toURL());

      Gate.init();

      // Since this class will do the mapping to uima classes, it
      // will need to know what the UIMA classes are - that is usually done
      // when creating a pipeline, but it's got to be done sooner
      // String typeDescriptorClassPath = U.getOption(pArgs,"--typeDescriptor=", "gov.nih.cc.rmd.Model");
      // UIMAUtil.setTypeDescriptor(typeDescriptorClassPath);

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize",
          "Issue within method initialize  initializing the gate functionality " + e.getMessage());
      throw new ResourceInitializationException();
    }

  }

  // -----------------------------------------
  /**
   * get retrieves the next document id from the list of document ids, queries the database fro
   * annotations that share this document id, creates annotations for this id.
   * 
   * @param pCAS
   * @param pRecordId
   * @throws IOException
   * @throws CollectionException
   */
  // -----------------------------------------
  @Override
  public synchronized void get(CAS pCAS, long pRecordId) throws IOException, CollectionException {

    JCas jcas = null;
    try {
      jcas = pCAS.getJCas();

      File aFile = null;

      try {

        aFile = this.listOfFilesToProcess.get((int) pRecordId);

      } catch (Exception e) {
        // ---------------------
        // In multi-treaded environments - a process could sneak in an take the
        // last record between the time this thread
        // calls hasNext and getNext.
        // This catch will be the catch to figure
        // out that there are no more records to process
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "get",
            "Issue with records in thread " + Thread.currentThread().getName() + " " + e.toString());
        return;
      }

      String documentText = null;
      String documentId = aFile.getName();
      GLog.println(GLog.STD___LEVEL, this.getClass(), "get", " --------->  Reading in " + aFile.getName());

      // -----------------------------
      // Read the Gate file here
      // -----------------------------
      documentText = readGateDocument(aFile, jcas);

      String documentTitle = "unknown";
      FileMetaData fileMetaData = GleanMetaDataFromFileName.getFileMetaData(documentId);
      String documentType = fileMetaData.getDocumentType();
      String documentName = fileMetaData.getDocumentName();
      String patientID = fileMetaData.getPatientId();
      String referenceDate = fileMetaData.getDocumentDate();
      int pageNo = fileMetaData.getPageNumber();
      String metaData = "unknown";

      try {
        DocumentHeader documentHeader = VUIMAUtil.setDocumentHeader(jcas, documentId, documentName, documentType, documentTitle,
            metaData, patientID, referenceDate, pRecordId);
        documentHeader.setPageNumber(pageNo);
      } catch (Exception e) {
        e.printStackTrace();
      }

      VUIMAUtil.setDocumentText(jcas, documentName, documentType, referenceDate, documentText, this.setMetaDataHeader);

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "get", "Something went wrong with fromDatabase " + e.toString());
    }

  } // end Method getNext() -----------------------

  // =================================================
  /**
   * readGateDocument populates the cas that's passed in
   * 
   * @param pFile
   * @param pJCas
   * @return String
   * @throws Exception
   */
  // =================================================
  private final String readGateDocument(File pFile, JCas pJCas) throws Exception {

    String documentContent = null;
    Document gateDocument = null;
    try {
      documentContent = U.readFile(pFile);
    } catch (IOException e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "ReadGateDocument",
          "Issue with reading the gate document " + pFile.getAbsolutePath() + " :" + e.toString());
      throw new Exception();
    }

    try {

      // ----------------------
      // peak into the file to see if the encoding is UTF-8 (which case, everything is fine)
      // or if it's something else, and we have to re-read the file with the correct encoding
      // -----------------------
      String encoding = U.getEncodingFromXMLFile( documentContent);
      
      // If there is no xml pre-amble - some btris gate files were corrupted with this issue) 
      if ( encoding == null && !documentContent.startsWith("<?xml"))
        documentContent = "<?xml version='1.0' encoding='UTF-8'?>\n" + documentContent;
      
      else if ( encoding.equals("UTF-8"))
        ;
  
      // else it's a different charset, have to re-read using the correct encoding
      else {
       documentContent = U.readFile(pFile, encoding);
       // once read in,  the encoding is now UTF-8, so change the charset to UTF-8
       // change the preamble to utf-8 before sending it to gate to be parsed
       documentContent = documentContent.replaceFirst(encoding, "UTF-8");
       
      }
        
      /* -------------------------------------------------
       *  Old Code - delete after verifying it's working
       
      
      if (!documentContent.startsWith("<?xml")) 
        documentContent = "<?xml version='1.0' encoding='UTF-8'?>\n" + documentContent;
      //   documentContent = "<?xml version='1.0' encoding='windows-1252'?>\n" + documentContent;
  

      else if (!documentContent.startsWith("<?xml version='1.0' encoding='UTF-8'?>") ) {
        documentContent = documentContent.replace("windows-1252", "UTF-8");
      }

      ------------------------------------------------ */
      
      //-------------------------------------------------
      // Remove carriage returns and replace with a space to preserve the offsets
      if (documentContent.indexOf('\13') > 0) 
        documentContent = documentContent.replace('\13', ' ');

    
      
      gateDocument = gate.Factory.newDocument(documentContent);
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "ReadGateDocument",
          "Issue with reading the gate document " + pFile.getAbsolutePath() + " :" + e.toString());
      throw new Exception();
    }

    // Extract the text
    gateDocument.setPreserveOriginalContent(true);
    DocumentContentImpl content = (DocumentContentImpl) gateDocument.getContent();
    String originalContent = content.getOriginalContent();

    // Extract the annotations

    // -----------------------------
    // if --addNewLines=true repair the butchered text, and add newlines back in
    //
    //
    if (this.addNewLines) {
      originalContent = this.addNewLineTool.addBTISNewLines(originalContent);
    }

    // -----------------------------
    // Deal with BTRIS De-identification markers
    // [LAST_NAME i=459]
    // ----------------------------
    if (this.deIdentified) {
      originalContent = this.addNewLineTool.stripBTRISDeidentifyMarkers(originalContent);
    }

    GateUtils.convertGateAnnotationsToCAS(gateDocument, pJCas, this.gateTouimaTypeHash);

    return originalContent;

  } // end Method ReadGateDocument() -----------------------

  // =================================================
  /**
   * readGateDocumentAux populates the cas from the
   * gate file that's passed in
   * 
   * It is assumed that the gate file used the same
   * text that is already in the existing cas.
   * 
   * This method is useful for when the cas was written
   * out as a gate file, processed, where there are new
   * gate annotations in it - to read the gate file
   * back in to extract the new annotations in it.
   * 
   * 
   * @param pFile
   * @param pJCas
   * @return String
   * @throws Exception
   */
  // =================================================
  public final void readGateDocumentAux(File pFile, JCas pJCas) throws Exception {

    String documentContent = null;
    Document gateDocument = null;
    try {
      documentContent = U.readFile(pFile);
    } catch (IOException e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "ReadGateDocument",
          "Issue with reading the gate document " + pFile.getAbsolutePath() + " :" + e.toString());
      throw new Exception();
    }

    try {

      if (!documentContent.startsWith("<?xml"))
        documentContent = "<?xml version='1.0' encoding='windows-1252'?>\n" + documentContent;

      if (documentContent.indexOf('\13') > 0) documentContent = documentContent.replace('\13', ' ');

      gateDocument = gate.Factory.newDocument(documentContent);
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "ReadGateDocument",
          "Issue with reading the gate document " + pFile.getAbsolutePath() + " :" + e.toString());
      throw new Exception();
    }

    // Extract the text
    gateDocument.setPreserveOriginalContent(true);

    // Extract the annotations

    GateUtils.convertGateAnnotationsToCAS(gateDocument, pJCas, this.gateTouimaTypeHash);

  } // end Method ReadGateDocument() -----------------------

  // =======================================================
  /**
   * filterInTextFiles returns true if the file is a text file
   * 
   * @param pFile
   * @return boolean
   */
  // =======================================================
  @Override
  protected boolean filterInTextFiles(File pFile) {

    boolean returnValue = false;
    String extension = U.getFileExtension(pFile.getName());

    if (extension == null || extension.indexOf(".xml") >= 0) returnValue = true;

    return returnValue;

  } // End Method filterInTextFiles() ======================

  // =======================================================
  /**
   * initialize
   * 
   * @param pArgs
   *          assumes there is the arg --inputDir= and optionally --recurseIntoSubDirs=[true|false]
   * @throws ResourceInitializationException
   *
   */
  // ======================================================
  @Override
  public void initialize(String[] pArgs) throws ResourceInitializationException {

    super.initialize(pArgs);

    String inputMappingFile = U.getOption(pArgs, "--uima2gateMappingFile=",
        "resources/vinciNLPFramework/ccRMDGate/uimaToGateMappings.csv");

    this.addNewLines = Boolean.parseBoolean(U.getOption(pArgs, "--addNewLines=", "false"));
    this.deIdentified = Boolean.parseBoolean(U.getOption(pArgs, "--deIdentified=", "false"));
    try {
      this.addNewLinesTool = new AddNewLinesTool(pArgs);
    } catch (Exception e) {
      e.printStackTrace();
      throw new ResourceInitializationException();
    }

  
    HashMap<String, String>[] hashes = loadUimaToGateTypeHash(inputMappingFile);
    this.uimaToGateTypeHash = hashes[0];
    this.gateTouimaTypeHash = hashes[1];
    
   

  }// end Method initialize() ========================================

  // =================================================
  /**
   * loadUimaToGateTypeHash reads in type map
   * into a hash
   * 
   * This relies on a table in resources/vinciNLPFramework/ccRMDGate/uimaToGateMappings.csv
   * which is a a table with the following (pipe delimited) fields
   * 
   * ---------------+---------------+------------------+-------------------
   * UIMA Type Name |Gate Type Name |Uima feature name |gate feature name
   * ---------------+---------------+------------------+-------------------
   * name.space.Type| GateType | |
   * name.space.Type| GateType | attribute1 |gateAttribute1
   * name.space.Type| GateType | attribute1 |gateAttribute2
   * 
   * @return HashMap<String,Sring>[]    [0] = uima2gateHash
   *                                    [1] = gate2uimaHash
   */                         
  // =================================================
  private HashMap<String, String>[]  loadUimaToGateTypeHash(String pInputFile) {

    HashMap<String, String> uima2gateHash = new HashMap<String, String>();
    HashMap<String, String> gate2UmiaHash = new HashMap<String, String>();

    try {
      String[] rows = U.readClassPathResourceIntoStringArray(pInputFile);

      if (rows != null) {
        for (String row : rows) {
          String uimaNameSpaceType = null;
          String gateType = null;
          String uimaFeatureName = null;
          String gateFeatureName = null;
          if (row != null && row.trim().length() > 0 && !row.startsWith("#")) {
            String cols[] = U.split(row, "|");
            uimaNameSpaceType = cols[0].trim();
            gateType = cols[1].trim();
           
            if (cols.length > 3) {
              try {
                uimaFeatureName = cols[2].trim();
                gateFeatureName = cols[3].trim();
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
            try {
              if (uimaFeatureName == null || uimaFeatureName.trim().length() == 0) {
                uima2gateHash.put(uimaNameSpaceType, gateType);
                gate2UmiaHash.put( gateType, uimaNameSpaceType );
                
              } else {

                uima2gateHash.put(uimaNameSpaceType + ":" + uimaFeatureName, gateFeatureName);
                gate2UmiaHash.put(gateType + ":" + gateFeatureName, uimaFeatureName );
              }
            } catch (Exception e3) {
              e3.printStackTrace();
              GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadUimaToGateTypeHash",
                  "Issue reading in line " + e3.toString() + "\n" + row + "\n");
              throw e3;

            }
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadUimaToGateTypeHash",
          "Issue reading in " + pInputFile + " " + e.toString());
    }

    @SuppressWarnings("unchecked")
    HashMap<String,String> returnVal[] = new HashMap[2];
    returnVal[0] = uima2gateHash;
    returnVal[1] = gate2UmiaHash;
    
    return returnVal;

  } // end Method loadUimaToGateTypeHash() -----------
  


  // ----------------------------------------
  // Global variables
  // ----------------------------------------

  private HashMap<String, String> uimaToGateTypeHash = null;
  private HashMap<String, String> gateTouimaTypeHash = null;
  private AddNewLinesTool         addNewLinesTool    = null;
  private boolean                 deIdentified       = false;

} // end Class MultiAnnotationRecordCollectionReader() ----