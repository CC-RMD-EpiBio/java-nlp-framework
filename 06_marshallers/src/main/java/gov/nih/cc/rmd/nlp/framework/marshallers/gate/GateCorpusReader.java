// =================================================
/**
 * GateReader reads Gate Documents and converts them to Cas's
 *
 * @author  Guy Divita 
 * @created March 15, 2019
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.gate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import gate.Corpus;
import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.corpora.DocumentContentImpl;
import gate.creole.ResourceInstantiationException;
import gate.persist.SerialDataStore;
import gov.nih.cc.rmd.nlp.framework.utils.FileMetaData;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.GleanMetaDataFromFileName;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.va.chir.model.DocumentHeader;

// extends CollectionReader_ImplBase,

public class GateCorpusReader extends gov.nih.cc.rmd.nlp.framework.marshallers.text.FromText {
 
  private String gateHome;



  // =======================================================
  /**
   * Constructor  
   *
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public GateCorpusReader() throws ResourceInitializationException {
    
    String[] args = null;
    initialize( args);
    
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
  public GateCorpusReader(String[] pArgs) throws ResourceInitializationException {
    initialize( pArgs);
    
    // initialize Gate components
    try {
      
      // Tell Gate where the GATE HOME and PLUGIN directory is
      this.gateHome = U.getOption(pArgs, "--gateHome=",  "C:/Program Files (x86)/GATE_Developer_8.5.1");
      
      if(Gate.getGateHome() == null)
        Gate.setGateHome(new File(this.gateHome));
    if(Gate.getPluginsHome() == null)
        Gate.setPluginsHome(new File( this.gateHome + "/plugins"));

      // Since this class will do the mapping to uima classes, it
      // will need to know what the UIMA classes are - that is usually done
      // when creating a pipeline, but it's got to be done sooner 
     // String typeDescriptorClassPath = U.getOption(pArgs,"--typeDescriptor=", "gov.nih.cc.rmd.Model");
     // UIMAUtil.setTypeDescriptor(typeDescriptorClassPath);
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,this.getClass(), "initialize", "Issue within method initialize  initializing the gate functionality " + e.getMessage() );
      throw new ResourceInitializationException();
    }
    
    
    
  } // end Constructor() ------------------


//-----------------------------------------
 /** 
  * getNext retrieves the next document id from the list of document ids, queries the database fro 
  * annotations that share this document id, creates annotations for this id.
  * @param pCAS
  * @throws IOException
  * @throws CollectionException
  */
 // -----------------------------------------
 @Override
 public synchronized void getNext(CAS pCAS) throws IOException, CollectionException {
  
     try {
       if ( this.fileCounter < this.numberOfFiles )
         get(pCAS, (long) this.fileCounter);  // <---------------------------- this is the meat of the method
       else {
         GLog.println(GLog.DEBUG_LEVEL,"In getNext 1 Issue with records in thread " +  Thread.currentThread().getName() + " jumped over the last in the array" );
       return;
     }
     } catch ( Exception e) {
       // ---------------------
       // In multi-treaded environments - a process could sneak in an take the
       //                                 last record between the time this thread
       //                                 calls hasNext and getNext.
       //                                 This catch will be the catch to figure
       //                                 out that there are no more records to process
       e.printStackTrace();
       GLog.println(GLog.ERROR_LEVEL,this.getClass(), "getNext","In getNext 1 Issue with records in thread " +  Thread.currentThread().getName() + " " + e.toString());
       return;
     }
    
   // --------------------------
   // increment the fileCounter
     
     this.fileCounter++;
   
 } // end Method getNext() -----------------------



//=======================================================
/**
* initialize 
* 
* @param pArgs    assumes there is the arg --inputDir= and optionally --recurseIntoSubDirs=[true|false]
* @throws ResourceInitializationException
*
*/
//======================================================
 @Override
 public void initialize(String[] pArgs) throws ResourceInitializationException  {
  
  
   String inputMappingFile = U.getOption(pArgs,  "--uima2gateMappingFile=", "resources/vinciNLPFramework/ccRMDGate/uimaToGateMappings.csv");
   this.setMetaDataHeader = Boolean.parseBoolean(U.getOption(pArgs,  "--setMetaDataHeader=", "true"));
   String fileCtr = U.getOption(pArgs, "--fileCounter=", "0" );
   this.fileCounter = Integer.valueOf(fileCtr);
  
   
   
   uimaToGateTypeHash        = loadUimaToGateTypeHash(inputMappingFile);
   gate2UimaTypeHash         = loadGate2UimaTypeHash( inputMappingFile);
   
   String inputDir = U.getOption(pArgs,  "--inputDir=", "00_data/exampleDataStoreOfCorpus1");
   
   try {
   Gate.init();

   } catch (Exception e) {
     e.printStackTrace();
     GLog.println( "Issue with initializing gate: " + e.toString());
     throw new ResourceInitializationException();
   }
   if ( GateUtils.isGATESerialDataStoreDir( inputDir )) {
     
  
     // Open up the data store up and get the names of the files
     try {
       loadDataStore( inputDir );
    
       this.setCatalog(this.dataStoreRecordList.size());
       this.numberOfFiles = this.dataStoreRecordList.size() ;
       GLog.println(GLog.STD___LEVEL,this.getClass(), "initialize", "The number ofiles to process = " + this.numberOfFiles);
       
     
     } catch (Exception e) {
       e.printStackTrace();
       GLog.println( "Issue with loading the data store " + inputDir + " : " + e.toString());
       throw new ResourceInitializationException();
     }
     
   } else {
     GLog.println( inputDir + " is not a gateSerialStore ");
     throw new ResourceInitializationException();
   }
     
   

  
  

}// end Method initialize() ========================================



//-----------------------------------------
/** 
 * initialize reads the directory containing the input files.  By default, this
 * recurses through sub directories.
 * 
 * @param pInputDir
 * @throws ResourceInitializationException

 */
// -----------------------------------------
@Override 
public void initialize(String pInputDir ) throws ResourceInitializationException  {
  

  String inputMappingFile  = "--uima2gateMappingFile=resources/vinciNLPFramework/ccRMDGate/uimaToGateMappings.csv";
  String setMetaDataHeader = "--setMetaDataHeader=false";
  
  String args[] = { inputMappingFile, setMetaDataHeader };
  initialize( args);
  
  
} // End Method initialize() ---------------------------



/** 
  * get retrieves the next document id from the list of document ids, queries the database fro 
  * annotations that share this document id, creates annotations for this id.
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
   } catch (Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL,this.getClass(), "get","Something went wrong trying to get a new jcas " + e.toString());
     throw new CollectionException() ;
   }
   
     
   try {
     
      
       
       // -----------------------------
       // Read the Gate file here
       // -----------------------------
       String[] documentIds = new String[1];
       String documentText = readGateDocument( jcas, pRecordId, documentIds );
       String documentId = documentIds[0];
      
     
     String documentTitle = "unknown";
     FileMetaData fileMetaData = GleanMetaDataFromFileName.getFileMetaData( documentId);
     String documentType  = fileMetaData.getDocumentType();
     String documentName  = fileMetaData.getDocumentName(); 
     String patientID     = fileMetaData.getPatientId();
     String referenceDate = fileMetaData.getDocumentDate();
     int           pageNo = fileMetaData.getPageNumber(); 
     String metaData      = this.corpusName.get((int) pRecordId);
     String metaDataFields[] = null;
 
     try {
       DocumentHeader documentHeader = VUIMAUtil.setDocumentHeader( jcas, documentId, documentName, documentType, documentTitle, metaData, patientID, referenceDate, pRecordId);
       documentHeader.setOtherMetaDataFieldNames( "corpusName");
       
       documentHeader.setPageNumber( pageNo);
     } catch ( Exception e) {
       e.printStackTrace();
     }
    
     
     VUIMAUtil.setDocumentText(jcas, documentName,  documentType, referenceDate, documentText, this.setMetaDataHeader );
  
   } catch (Exception e ) {
     e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL,this.getClass(), "get","Something went wrong with fromDatabase " + e.toString());
   }
    
   
   
 } // end Method getNext() ----------------------- 


 

// =================================================
/**
 * readGateDocument populates the cas that's passed in
 *
 * @param pJCas
 * @param pRecordId 
 * @param pDocumentId   (this will be filled out )
 * @return String
 * @throws Exception 
*/
// =================================================
private final String readGateDocument(JCas pJCas, long pRecordId, String[] pDocumentId) throws Exception {
  
  String documentContent = null;
  Document gateDocument = null;
  try {
    
     gateDocument =  getGateDocument( pRecordId );
    
  
  } catch (Exception e) {
    e.printStackTrace();
    GLog.println(GLog.ERROR_LEVEL, this.getClass(), "ReadGateDocument", "Issue with reading the gate document " +  " :" + e.toString() );
    throw new Exception ();
  }
  
  
  
  // Extract the text
  gateDocument.setPreserveOriginalContent(true);
  DocumentContentImpl content = (DocumentContentImpl) gateDocument.getContent();
  documentContent = content.getOriginalContent();
  
  
  //Extract the annotations

  pDocumentId[0] = gateDocument.getName();
  GateUtils.convertGateAnnotationsToCAS(gateDocument, pJCas, gate2UimaTypeHash);
  
  return documentContent;
  
  
} // end Method ReadGateDocument() -----------------------



// =================================================
/**
 * loadDataStore 
 * 
 * @param pDataStoreDirPath
 * @throws Exception
 * 
*/
// =================================================
private final void loadDataStore( String pDataStoreDirPath ) throws Exception {
  
  File dataStoreDirFile = new File(pDataStoreDirPath);
  this.currentDataStore = null;
 
  if (!dataStoreDirFile.exists() || !dataStoreDirFile.canRead() ) {
     
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadDataStore", "can't read the data store "  +  pDataStoreDirPath );
     throw new IOException();
     
  
  }
   String serialStoreClassName = SerialDataStore.class.getCanonicalName();

   String dsURL = dataStoreDirFile.toURI().toString();
   this.currentDataStore = (SerialDataStore) Factory.openDataStore( serialStoreClassName, dsURL );
   
 
        List<String> corpusIds = currentDataStore.getLrIds("gate.corpora.SerialCorpusImpl");
        
        FeatureMap fm = Factory.newFeatureMap();
        fm.put(DataStore.DATASTORE_FEATURE_NAME,this.currentDataStore);
        for ( String corpusId : corpusIds ) {
          fm.put(DataStore.LR_ID_FEATURE_NAME,  corpusId );
          Corpus c = (Corpus) Factory.createResource("gate.corpora.SerialCorpusImpl", fm);
          String corpusName = c.getName();
          
          // -----------------------------------
          // This is a skrewy way to carry along the corpus name for the 
          // documents - but it works - 
          //    for each corpus
          //      Retrieve the corpus name
          //      for each document in the corpus
          //         create a list that corresponds to the document index, but keep the corpus name 
          //         in the corpusName list (a one to one mapping to the document index)
          // -----------------------------------
          List<String> corpusNameForDocumentIndex = c.getDocumentNames();
          if ( corpusNameForDocumentIndex != null)
            for ( int j = 0; j < corpusNameForDocumentIndex.size(); j++  )
              corpusNameForDocumentIndex.set(j,  corpusName );
          System.err.println("erase me");
          if ( this.corpusName == null ) this.corpusName = new ArrayList<String>( corpusNameForDocumentIndex.size() * 2 );
          this.corpusName.addAll(  corpusNameForDocumentIndex );
          
          
        
        }
          
           
      
     this.dataStoreRecordList = this.currentDataStore.getLrIds( "gate.corpora.DocumentImpl" );
   System.err.println("here - looking for batch names");
      
  // int pRecordId =  0;
 //  Document gateDoc =  getGateDocument( pRecordId );
    
  
} // end Method loadDataStore() --------------------


// =================================================
/**
 * getGateDocument retrieves the record from the
 * current datastore that's been loaded
 * 
 * @param  pRecordId
 * @return Document   (it's a GATE document)
 * @throws ResourceInstantiationException 
*/
// =================================================
private final Document getGateDocument(long  pRecordId) throws ResourceInstantiationException {
  
  
  FeatureMap xserialDocListFeatures = Factory.newFeatureMap(); 
  xserialDocListFeatures.put(DataStore.LR_ID_FEATURE_NAME,  dataStoreRecordList.get( (int)  pRecordId )); 
 
  xserialDocListFeatures.put(DataStore.DATASTORE_FEATURE_NAME, this.currentDataStore); 

  // retrieve a doc by the following:
   Document doc = (Document)  Factory.createResource("gate.corpora.DocumentImpl", xserialDocListFeatures);
  
  
  

  return doc;
  
} // end Method getGateDocument() ------------------





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
        if (uimaFeatureName == null || uimaFeatureName.trim().length() == 0) {
          returnVal.put(uimaNameSpaceType, gateType);
         
        } else {
         
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



//=================================================
/**
* loadGate2UimaTypeHash reads in type  map
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
//=================================================
private HashMap<String, String> loadGate2UimaTypeHash( String pInputFile) {

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
     if (uimaFeatureName == null || uimaFeatureName.trim().length() == 0) {
       returnVal.put( gateType, uimaNameSpaceType);
      
     } else {
      
       returnVal.put(gateType + ":" + gateFeatureName, uimaFeatureName );
     }
     } catch (Exception e3) {
       e3.printStackTrace();
       GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadGate2UimaTypeHash",
           "Issue reading in line "  + e3.toString() + "\n" + row + "\n");
       throw e3;
       
     }
   }
 }
}

} catch (Exception e) {
e.printStackTrace();
GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadGate2UimaTypeHash",
   "Issue reading in " + pInputFile + " " + e.toString() );
}

return returnVal;

} // end Method loadGate2UimaTypeHash() -----------



// ----------------------------------------
// Global variables
// ----------------------------------------

private  HashMap<String,String> uimaToGateTypeHash = null;
private  HashMap<String,String> gate2UimaTypeHash = null;
private SerialDataStore currentDataStore = null;
private List<String> dataStoreRecordList = null;
private List<String> corpusName  = null;
private boolean setMetaDataHeader = false;

  

  
} // end Class MultiAnnotationRecordCollectionReader() ----
