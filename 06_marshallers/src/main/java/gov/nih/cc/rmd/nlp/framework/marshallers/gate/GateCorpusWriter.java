//=================================================
/**
 * GateCorpusWriter creates a serialCropus 
 *
 * @author  Guy Divita 
 * @created June 26, 2012
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.gate;



import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;


import gate.AnnotationSet;
import gate.Document;
import gate.*;
import gate.Gate;
import gate.annotation.AnnotationSetImpl;
import gate.Factory;
import gate.corpora.DocumentStaxUtils;
import gate.persist.SerialDataStore;
import gate.util.GateException;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.FeatureValuePair;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class GateCorpusWriter extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter {

 
  private static DocumentStaxUtils docWriter;
  private String gateHome;
  // -----------------------------------------
  /** 
   * Constructor ToString
   * @throws ResourceInitializationException 
  */
    // -----------------------------------------
    public  GateCorpusWriter() throws AnalysisEngineProcessException, ResourceInitializationException {
      String args[] = null;
      initialize(  args);
    } // end Constructor 


    // -----------------------------------------
    /** 
     * Constructor ToString
     * 
     * @param pArgs
     * @throws ResourceInitializationException 
    */
      // -----------------------------------------
      public  GateCorpusWriter(String[] pArgs) throws AnalysisEngineProcessException, ResourceInitializationException {
        initialize( pArgs);
      } // end Constructor 

    
    // -----------------------------------------
    /** 
     * process iterates through all annotations, filters out
     * those that should be filtered out, then pushes them
     * into a database store.
     * 
     * Each document should include a DocumentAnnotation annotation and a documentHeader annotation
     * for re-animation purposes.
     *
     * @param pAJCas
     * @throws AnalysisEngineProcessException
     */
    // -----------------------------------------
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
      
     processAux( pJCas );

    } // end Method process


    // -----------------------------------------
    /** 
     * process iterates through all annotations, filters out
     * those that should be filtered out, then pushes them
     * into a database store.
     * 
     * Each document should include a DocumentAnnotation annotation and a documentHeader annotation
     * for re-animation purposes.
     *
     * @param pAJCas
     * @return File  (the output file  )
     * @throws AnalysisEngineProcessException
     */
    // -----------------------------------------
    public File processAux(JCas pJCas) throws AnalysisEngineProcessException {
      
      File outputFile= null;
      
      this.performanceMeter.startCounter();
   
      // -----------------------------------
      // Set the text
      String docText = pJCas.getDocumentText();
 
      
      try {
        Document gateDocument = gate.Factory.newDocument(docText);
        
       
        List<Annotation> uimaAnnotations = UIMAUtil.getAnnotations(pJCas);
        
         
        
        
        if ( uimaAnnotations != null && !uimaAnnotations.isEmpty() )
          addGateAnnotations( uimaAnnotations, gateDocument, this.setName )  ;
          
        
     
      String documentId = VUIMAUtil.getDocumentId(pJCas);
      if ( documentId.contains("/"))
        documentId = U.getFileNameOnly(documentId);
      documentId = documentId.replace(" ",  "_");
      gateDocument.setName(documentId);
      
      
      
      try {
        
        int dataStoreIndex = whichDataStore();
        Document persistDoc =  (Document)this.sds[dataStoreIndex].adopt(gateDocument);
        
        // -------------------------------
        // if you are making batches, 
        // create a new batch if the number of files in the batch exceeds the the number per batch
        // - this necetates that this method be synchronized 
        // -------------------------------
        if ( this.numberOfFilesPerGATECorpusBatch > 0 )
          if ( this.numberOfFilesInCurrentBatch[dataStoreIndex] > this.numberOfFilesPerGATECorpusBatch  ) {
              createDocumentSerialCorpus(dataStoreIndex,this.corpusName, this.batchNumber++ );
              this.numberOfFilesInCurrentBatch[dataStoreIndex] = 0;
          }
        
        persistDoc.setName( documentId );
        this.temporaryCorpus[dataStoreIndex].add( gateDocument);
        this.persistCorp[dataStoreIndex].add( gateDocument );
        this.persistCorp[dataStoreIndex].sync();
        this.sds[dataStoreIndex].sync(persistDoc);
        this.numberOfFilesInCurrentBatch[dataStoreIndex]++;
      
      } catch (Exception e) {
        throw new RuntimeException(e);
        
      }
      
      
     
      this.performanceMeter.stopCounter();
      
        
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", "Issue seralizing the gate doc " + e.toString());
        
      }

      return outputFile;
    } // end Method processAux() ---------------------------


    
    
 // -------------------------------------------------------
    /**
     * addGateAnnotations
     *
     * @param pUIMAAnnotations
     * @param doc
     * @param pLabelOptions  USE_NAMESPACE if you want to have gov.va.chir.model. kind of namespace be part of the gate label)
     *                       HITEX_CHIR_MAPPINGS  maps the CHIR Labels to hitex mappings
     *                       DONT_USE_NAMESPACE uses only the label without a name space
     *                       The default is to not use the name space.
     * @param pSetName       Name the annotation set in the gate document
     */
    // -------------------------------------------------------
    public void addGateAnnotations(List<Annotation> pUIMAAnnotations, gate.Document doc, String pSetName )  throws Exception{
      
     
      AnnotationSet aset = doc.getAnnotations(pSetName);
  
      // ---------------------------------------
      // extract  all UIMA annotations
      // ---------------------------------------
      
    for (Annotation annotation : pUIMAAnnotations) {

      long begin = annotation.getBegin();
      long end = annotation.getEnd();
      String uimaAnnotationType = annotation.getType().getName();
     
      String gateAnnotationType = mapUIMAAnnotationTypeToGateAnnotationType(uimaAnnotationType);

      if (gateAnnotationType != null) {
        List<FeatureValuePair> featureValuePairs = UIMAUtil.getFeatureValuePairs(annotation);

        gate.FeatureMap featureMap = gate.Factory.newFeatureMap();

        if (featureValuePairs != null) for (FeatureValuePair fvp : featureValuePairs) {

          // ----------------------------------------------------
          // Need to map feature names from one side to the other
          String gateFeatureName = mapUIMAAnnotatonTypeFeatureToGateFeature(uimaAnnotationType, fvp.getFeatureName());

          // ------------------------------
          // If there is no gate equivalant, no sense making it a feature
          if (gateFeatureName != null) 
            featureMap.put(gateFeatureName, fvp.getFeatureValue());
        }

        if ( end > begin )
          aset.add(begin, end, gateAnnotationType, featureMap);
      }
    } // end loop through the annotations
      
    } // end Method addGateAnnotations() ----------    
    
    
  // =================================================
  /**
   * mapUIMAAnnotationTypeToGateAnnotationType maps uima annotation types
   * to the equivalent gate version.
   * 
   * For the time being, this is done from a table read into a hash
   * that makes the mappings.
   * 
   * @param uimaAnnotationType
   * @return String   (null if none exists)
   */
  // =================================================
  private String mapUIMAAnnotationTypeToGateAnnotationType(String uimaAnnotationType) {
   
    String returnVal = null;
    
    if ( this.uimaToGateTypeHash != null )
      returnVal = this.uimaToGateTypeHash.get( uimaAnnotationType);
    
    
    return returnVal;
  } // end Method mapUIMAAnnotationTypeToGateAnnotationType()-----

    // =================================================
/**
 * mapUIMAAnnotatonTypeFeatureToGateFeature maps uima annotation type features
   * to the equivalent gate version.
   * 
   * For the time being, this is done from a table read into a hash
   * that makes the mappings.
   * 
 * 
 * @param uimaAnnotationType
 * @param uimaFeatureName
 * @return
*/
// =================================================
private  String mapUIMAAnnotatonTypeFeatureToGateFeature(String uimaAnnotationType, String uimaFeatureName) {
 
  
  String returnValue = null;
  if ( this.uimaToGateTypeHash != null ) {
    String featureName = uimaAnnotationType + ":" + uimaFeatureName;
    returnValue = this.uimaToGateTypeHash.get( featureName );
  }
  
  return returnValue;
} // end Method mapUIMAAnnotatonTypeFeatureToGateFeature() ---


    // -----------------------------------------
    /** 
     * destroy closes the database
     *
     */
    // -----------------------------------------
    public void destroy() {
      
      
      try {
     
        for ( int serialDataStoreIndex = 0; serialDataStoreIndex < this.numberOfGATESerialDataStores; serialDataStoreIndex++) {
          this.persistCorp[serialDataStoreIndex].sync();
          this.sds[serialDataStoreIndex].sync( this.persistCorp[serialDataStoreIndex]);
          this.persistCorp[serialDataStoreIndex].cleanup();
     
          this.sds[serialDataStoreIndex].close();
        }
          
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println( GLog.INFO_LEVEL, this.getClass(), "destroy", "Issue with closing the serial data store " + e.toString() );
      
      }
        
     
      this.performanceMeter.writeProfile( this.getClass().getSimpleName());
     
    } // end Method destroy() 



    // =================================================
      /**
       * setTmpOutputDir sets the outputDir 
       * 
       * This overides the command line argument --outputDir=
       * and it does not append an addition path like what
       * is assumed with the --outputDir= argument
       * 
       * @param pDir
       * @throws Exception 
      */
      // =================================================
     public final void setTmpOutputDir(String pDir) throws Exception {
       
       this.outputDir = pDir;
        
       try {
         U.mkDir( this.outputDir);
       } catch (Exception e) {
         e.printStackTrace();
         System.err.println("Issue trying to set the GATE tmp outputDir " + e.toString() );
         throw e;
       }
       
      } // end Method setTmpOutputDir() -------------------


    /* (non-Javadoc)
     * @see gov.va.vinci.nlp.framework.marshallers.writer.Writer#initialize(org.apache.uima.UimaContext)
     */
    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  

        initialize(args);
        
      } catch (Exception e ) {
        String msg = "Issue in initializing class " + this.getClass().getName() + " " + e.toString() ;
        GLog.println(GLog.ERROR_LEVEL, msg);     // <------ use your own logging here
        throw new ResourceInitializationException();
      }
    } // End Method initialize() ======================



    /* (non-Javadoc)
     * @see gov.va.vinci.nlp.framework.marshallers.writer.Writer#initialize(java.lang.String[])
     */
    @Override
    public void initialize(String[] pArgs) throws ResourceInitializationException {
      
     
      this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
      
      String parentOutputDir = U.getOption(pArgs, "--outputDir=", "./" ); 
      String inputMappingFile = U.getOption(pArgs,  "--uima2gateMappingFile=", "resources/vinciNLPFramework/ccRMDGate/uimaToGateMappings.csv");
      this.corpusName = U.getOption(pArgs,  "--corpusName=", "batch");
      this.setName = U.getOption(pArgs,  "--setName=", "framework");
      this.numberOfFilesPerGATECorpusBatch = Integer.parseInt(U.getOption(pArgs, "--numberOfFilesPerGATECorpusBatch=", "-1"));
      this.numberOfGATESerialDataStores = Integer.parseInt( U.getOption(pArgs, "--numberOfGATESerialDataStores=", "2"));
      
      this.batchNumber = 0;
      
      this.temporaryCorpus = new Corpus[this.numberOfGATESerialDataStores ];
      this.persistCorp = new Corpus[this.numberOfGATESerialDataStores];
      this.sds = new SerialDataStore[this.numberOfGATESerialDataStores];
      this.numberOfFilesInCurrentBatch = new int[this.numberOfGATESerialDataStores];
      for ( int i = 0; i < this.numberOfGATESerialDataStores; i++ )
          this.numberOfFilesInCurrentBatch[i] = 0;
       
      
      this.outputDir = parentOutputDir + "/GATE";
      
      try {
        U.mkDir( this.outputDir);
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", "Issue creating the output dir " + this.outputDir + " "+ e.toString());
        
      }
      
      
      //init GATE
      //    this is the first thing to be done
      
      // Tell Gate where the GATE HOME and PLUGIN directory is
      this.gateHome = U.getOption(pArgs, "--gateHome=",  "C:/Program Files (x86)/GATE_Developer_8.5.1");
      
      if(Gate.getGateHome() == null)
        Gate.setGateHome(new File(this.gateHome));
    if(Gate.getPluginsHome() == null)
        Gate.setPluginsHome(new File( this.gateHome + "/plugins"));

   
        try {
         Gate.init();
         for ( int sericalDataStoreIndex = 0; sericalDataStoreIndex < this.numberOfGATESerialDataStores; sericalDataStoreIndex++)
           createDocumentSerialCorpus(sericalDataStoreIndex, this.corpusName, this.batchNumber++ );
      
        }
         catch (Exception e) {
           e.printStackTrace();
           GLog.println(GLog.ERROR_LEVEL, this.getClass(), "initialize", "cannot initialise GATE..." + e.toString());
           throw new ResourceInitializationException(); 
         }
      
   
      
      uimaToGateTypeHash        = loadUimaToGateTypeHash(inputMappingFile);
    
      
      
    }// end Method initialize() ========================================



    // =================================================
    /**
     * divineBatchNumberName returns a semi-formatted string
     * of the int  - in 3 digit form  - 001  assuming there will be
     * no more than 999 batches in a corpus
     * 
     * @param pBatchNumber
     * @return String
    */
    // =================================================
    private String divineBatchNumberName(int pBatchNumber) {
      String  returnVal =  U.zeroPad(pBatchNumber, 3);
    
      return returnVal;
    } // end Method divineBatchNumberName() ------------


    // =================================================
    /**
     * whichDataStore cycles through 0 thru n-1 numberOfGATESerialDataStores
     * to allow new documents to be uniformly put into each datastore
     * 
     * currentDataStore will initially be set to -1;
     * 
     * @return int
    */
    // =================================================
     private final int  whichDataStore() {
     
       
      this.currentDataStore++;
      
      if ( this.currentDataStore >= this.numberOfGATESerialDataStores )
        this.currentDataStore = 0;
      
      return this.currentDataStore;
    } // end Method whichDataStore() ---------------------


    // =================================================
    /**
     * createDocumentSerialCorpus 
     * @throws Exception 
     * 
     * @param pBatchName    if there are no batches, call it corpus;  otherwise, batch
     * @param pBatchNumber  will be used if the numberOfFilesPerBatch > 0             _01
     * 
    */
    // =================================================
    private void createDocumentSerialCorpus(int pSerialDataStoreIndex, String pBatchName, int pBatchNumber) throws Exception {
    
      
      try {
      //create&open a new Serial Data Store
      //    pass the datastore class and path as parameteres
     
      String sdDir = this.outputDir + "/SerialDataStore_" + pSerialDataStoreIndex ;
      
      File sdDirF = new File( sdDir);
      
      String gateStoreURL = sdDirF.toURL().toString();
      
      if ( this.sds[pSerialDataStoreIndex] == null ) {     
        String serialStoreClassName = SerialDataStore.class.getCanonicalName();
        this.sds[pSerialDataStoreIndex]  = (SerialDataStore)Factory.createDataStore(serialStoreClassName, gateStoreURL );
        sds[pSerialDataStoreIndex].open();
        GLog.println( GLog.INFO_LEVEL, this.getClass(), "createDocumentSerialCorpus", "serial datastore created...");
      }
      String name = pBatchName ;
      if ( this.numberOfFilesPerGATECorpusBatch > 0 )
        name = pBatchName + "_" + U.zeroPad(pBatchNumber,  3);
     
      this.temporaryCorpus[pSerialDataStoreIndex] = Factory.newCorpus( name );
      
    //save corpus in datastore
      //    SecurityInfo is ingored for SerialDataStore - just pass null
      //    a new persisent corpus is returned
      
      // ----------------------------
      // if the the persistCorp is not null, clean it up before renaming it
      if ( this.persistCorp[pSerialDataStoreIndex] != null ) {
        this.persistCorp[pSerialDataStoreIndex].sync();
        this.sds[pSerialDataStoreIndex].sync( this.persistCorp[pSerialDataStoreIndex]);
        this.persistCorp[pSerialDataStoreIndex].cleanup();
      }
      
      
      this.persistCorp[pSerialDataStoreIndex] = (Corpus) sds[pSerialDataStoreIndex].adopt(this.temporaryCorpus[pSerialDataStoreIndex] );
      sds[pSerialDataStoreIndex].sync(persistCorp[pSerialDataStoreIndex]);
      GLog.println( GLog.INFO_LEVEL, this.getClass(), "createDocumentSerialCorpus", "corpus saved in datastore...");
      
    
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println( GLog.INFO_LEVEL, this.getClass(), "createDocumentSerialCorpus", "Issue with creating the serial data store " + e.toString() );
        throw e;
      }
      
    } // end Method createDocumentSerialCorpus() --------


    // =================================================
    /**
     * loadUimaToGateTypeHash reads in type  map
     * into a hash
     * 
     * This relies on a table in resources/vinciNLPFramework/ccRMDGate/uimaToGateMappings.csv 
     * which is a a table with the following (pipe delimited) fields
     * 
     *    ---------------+---------------+------------------+-------------------
     *    UIMA Type Name |Gate Type Name |Uima feature name |gate feature name
     *    ---------------+---------------+------------------+-------------------
     *    name.space.Type| GateType      |                  |
     *    name.space.Type| GateType      | attribute1       |gateAttribute1
     *    name.space.Type| GateType      | attribute1       |gateAttribute2
     * 
     * @return HashMap<String,Sring>
    */
    // =================================================
    private HashMap<String, String> loadUimaToGateTypeHash( String pInputFile) {
   
    HashMap<String, String> returnVal = new HashMap<String,String>();

     try {
      String[] rows = U.readClassPathResourceIntoStringArray(pInputFile);

      if (rows != null) {
        for (String row : rows) {
          String uimaNameSpaceType = null;
          String gateType = null;
          String uimaFeatureName = null;
          String gateFeatureName = null;
          if (row != null && row.trim().length() > 0 && !row.startsWith("#")) {
            String cols[] = U.split(row,"|");
            uimaNameSpaceType = cols[0].trim();
            gateType = cols[1].trim();

            if (cols.length > 2) {
              try {
                uimaFeatureName = cols[2].trim();
                gateFeatureName = cols[3].trim();
              } catch (Exception e) {
              }
            }
            try {
            if (uimaFeatureName == null || uimaFeatureName.trim().length() == 0)
              returnVal.put(uimaNameSpaceType, gateType);
            else {
             
              returnVal.put(uimaNameSpaceType + ":" + uimaFeatureName, gateFeatureName);
            }
            } catch (Exception e3) {
              e3.printStackTrace();
              GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadUimaToGateTypeHash",
                  "Issue reading in line "  + e3.toString() + "\n" + row + "\n");
              throw e3;
              
            }
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadUimaToGateTypeHash",
          "Issue reading in " + pInputFile + " " + e.toString() );
    }

    return returnVal;
      
    } // end Method loadUimaToGateTypeHash() -----------



    // ----------------------------------------
    // Global variables
    // ----------------------------------------
    private  String outputDir = null;
    private  ProfilePerformanceMeter performanceMeter = null;
    private  HashMap<String,String> uimaToGateTypeHash = null;
    private SerialDataStore serialDataStore = null;
    private String corpusName = null;
    private Corpus temporaryCorpus[] = null;
    private Corpus persistCorp[] = null;
    private SerialDataStore sds[] = null;
    private String setName = null;
    private int numberOfFilesPerGATECorpusBatch = 0;
    private int batchNumber = 0;
    private int numberOfFilesInCurrentBatch[] = null;
    private int numberOfGATESerialDataStores = 1;
    private int currentDataStore = -1;
   


} // end Class toCommonModel
