// =================================================
/**
 * FromString Reader reads in data from a strings passed in
 * via a Map<key, inputText> mechanism, and converts them to Cas's.
 *
 * @author  Guy Divita 
 * @created Feb 22, 2018
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.string;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;

// extends CollectionReader_ImplBase,

public class FromStringReader extends gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader {
 
  // =======================================================
  /**
   * Constructor  
   *
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public FromStringReader() throws ResourceInitializationException {
    
    String[] args = null;
    initialize( args);
    
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
  public final void  getNext(CAS pCAS) throws IOException, CollectionException {
   
    String stringID = null;
    String inputText = null;
    JCas jcas = null;  
    this.performanceMeter.startCounter();
    
    try {
      jcas = pCAS.getJCas();
      
      synchronized(this) {
        Set<String> keys = this.inputQueue.keySet();
        for ( String key: keys ) {
          stringID = key;
          inputText = this.inputQueue.get(stringID);
          this.inputQueue.remove(key);
          break;
        }
        
      String documentId    = stringID;
      String documentText  = inputText;
      String documentTitle = "FromStringReader";
      String documentType  = "String";
      String documentName  = documentId; 
      String patientID     = "n/a";
      String referenceDate = "n/a";
      String metaData      = "";
      
      try {
      jcas.setDocumentText( documentText );
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL, "Issue reading from the string reader " + e.toString());
        this.performanceMeter.stopCounter();
        return;
      }
      
     VUIMAUtil.setDocumentHeader( jcas, documentId, documentName, documentType, documentTitle, metaData, patientID, referenceDate, this.fileCounter);
   
      } // end synchronized
    } catch (Exception e ) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL,"Something went wrong with from String Reader " + e.toString());
      this.performanceMeter.stopCounter();
      return;
    }
     
    // --------------------------
    // increment the fileCounter
    this.fileCounter++;
    this.performanceMeter.stopCounter();
    
  } // end Method getNext() -----------------------


 


  // -----------------------------------------
  /** 
   * getProgress is method required for the 
   * interface that is populated with the 
   * fraction of files processed by the number of files to process.
   *
   * @return Progress[]
   */
  // -----------------------------------------
  public final Progress[] getProgress() {
    Progress[] p = new Progress[] { new ProgressImpl(this.fileCounter, this.numberOfFiles, Progress.ENTITIES)};
    return p;

  } // end Method getProgress() --------------

  // -----------------------------------------
  /** 
   * hasNext Looks in the directory and sees if
   * there are any new files to process
   *
   * @return
   * @throws IOException
   * @throws CollectionException  <---- why not anymore?
   */
  // -----------------------------------------
  public final synchronized boolean hasNext()  {
    boolean returnValue = true;
    
    this.numberOfFiles = this.inputQueue.size();
    
   
    while ( this.numberOfFiles == 0)  {
     // wait until a kill order or it fills up
      try {
        this.inputQueue.wait();
        this.numberOfFiles = this.inputQueue.size();
        if ( this.finished ) {
          returnValue = false;
          break;
        }
      } catch (Exception e) {
        e.printStackTrace();
        GLog.println(GLog.ERROR_LEVEL,"Issue waiting for the input queue to fill up " + e.toString());
        throw new RuntimeException();
      }
    }
    
   
    
     return returnValue;
  } // end Method hasNext() -----------------
  

  // =================================================
  /** 
   * getInputQueue returns the Map<String ID, inputText>
   * 
   * if getNext() gets called, pulls the next from this map.
   * When processed, the key  gets removed.
   *
   * @return Map<String, String>   
   *
   */
  //=================================================
  public final Map<String, String> getInputQueue()   {
    return this.inputQueue; 
  } // end Method getInputQueue() --------------------
    
  // =================================================
  /**
   * setFinished tells the instance to stop processing 
   */
  // =================================================
  public final void setFinished() {
    this.finished = true;
    this.inputQueue.notifyAll();
    
  } // end Method setFinished() ----------------------

  //=================================================
  /**
   * addToQueue
   * 
   * @param pInputText
   * @return String (the StringID )
   */
  // =================================================
 public final synchronized String addtoQueue( String pInputText) {
   
   UUID anId = UUID.randomUUID();
   String stringID = anId.toString();
   this.inputQueue.put(stringID, pInputText);
   this.numberOfFiles = this.inputQueue.size();
   this.inputQueue.notifyAll();
   
   return stringID;
   
 } // end Method addToQueue() ----------------------
    
 
 
 //=======================================================
 /**
  * destroy 
  * 
  */
 // ======================================================
 @Override
 public void destroy() {
   try {
     this.setFinished();
     this.performanceMeter.writeProfile( this.getClass().getSimpleName());
   } catch (Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, "Issue destroying String reader " + e.toString());
     
   }
 } // end Method destroy()    
     
  
  
 //=======================================================
 /**
  * initialize 
  * 
  * @param aContext   
  * @throws ResourceInitializationException
  *
  */
 // ======================================================
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
     
     
      try {
     
        args  = (String[]) aContext.getConfigParameterValue("args");
    
        initialize( args );
      
      
       } catch ( Exception e ) {
         e.printStackTrace();
         e.getStackTrace();
         String msg = "Issue initializing the FromStringReader " + e.getMessage();
         System.err.println(msg);
         throw new ResourceInitializationException();
         
       }
     } // End Method initialize ============

   //=======================================================
   /**
    * initialize 
    * 
    * @param pArgs    
    * @throws ResourceInitializationException
    */
   // ======================================================
   public final void initialize(String[] pArgs) throws ResourceInitializationException {
      
     try {
       this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
       this.inputQueue = new LinkedHashMap<String,String>();
        
     } catch ( Exception e ) {
       e.printStackTrace();
       System.err.println("Issue within method initialize " + e.getMessage() );
       throw e;
     }
   } // End Method initialize ============

  /* (non-Javadoc)
   * @see gov.va.vinci.nlp.framework.marshallers.reader.Reader#initialize(java.lang.String)
   */
  @Override
  public void initialize(String pInputFile) throws ResourceInitializationException {
    // makes no sense here   
  }

  
  //----------------------------------------
  // Class Variables
  // ----------------------------------------
 
  private int                        fileCounter = 0;
  private int                      numberOfFiles = 0;
  private Map<String,String>          inputQueue = null;
  private boolean                       finished = false;
  
  

  
} // end Class MultiAnnotationRecordCollectionReader() ----
