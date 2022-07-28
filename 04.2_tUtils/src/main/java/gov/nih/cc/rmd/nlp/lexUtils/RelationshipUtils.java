// =================================================
/**
 * RelationshipUtils reads in any files in the dir that end with  
 * *_rel_logic.txt
 * 
 * It creates new relationship ids for uniqued rows that
 * are of the form cui1|rel|cui2
 * 
 * That means that there can be non-unique rows that
 * differ by sab. 
 * 
 * It writes out the files with the relationship ids in them
 * 
 * It gets it's seed for the relationship ids from
 * 
 *  String propertiesFile = U.getOption( pArgs, "--relPropertiesFile=", "./softwareRepos/framework-legacy/00_legacy/04.2_tUtils/src/resources/relationshipIds.properties");
     
 * 
 * 
 *
 * @author     Guy Divita
 * @created    Jun 9, 2021
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.lexUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

/**
 * @author Guy
 *
 */
public final class RelationshipUtils {

  

  public static final int FIELD_CONCEPT_NAME1 = 0;
  public static final int FIELD_AUI1 = 1;
  public static final int FIELD_CUI1 = 2;
  public static final int FIELD_REL = 3;
  public static final int FIELD_CONCEPT_NAME2 = 4;
  public static final int FIELD_AUI2 = 5;
  public static final int FIELD_CUI2 = 6;
  public static final int FIELD_SAB = 7;
  
  
  // =================================================
  /**
   * Constructor
   *
   * @param pArgs
   * @throws Exception 
   * 
  **/
  // =================================================
  public RelationshipUtils(String[] pArgs) throws Exception {
   
    initialize( pArgs);
    
  } // end Constructor() --------------------------

  // =================================================
  /**
   * initialize 
   * 
   * @param pArgs
   * @throws Exception 
  */
  // =================================================
  private final void initialize(String[] pArgs) throws Exception {
    
    String propertiesFile = U.getOption( pArgs, "--relPropertiesFile=", "./softwareRepos/framework-legacy/00_legacy/04.2_tUtils/src/resources/relationshipIds.properties");
    this.outputDir = U.getOption(pArgs, "--outputDir=", "/some/outputDir");
    System.err.println("Setting the output Dir to = " + this.outputDir);
    
    readPropertiesFile( propertiesFile);
    
  } // end Method initialize() -----------------------
  
  
 //=================================================
 /**
  * readPropertiesFile reads in parameters from the property file
  * 
  * @param pPropertiesFile
  * @throws Exception 
 */
 // =================================================
 public final static void readPropertiesFile(String pPropertiesFile) throws Exception {
   
   _properties = new Properties();
   
   Reader reader = null;
   try {
     reader = new FileReader( pPropertiesFile );
   } catch (FileNotFoundException e) {
     e.printStackTrace();
     String msg = GLog.error_println("Issue reading in the mf concept id properties file " + pPropertiesFile + " :" + e.toString());
     throw new Exception( msg);
   }
   
   try {
     _properties.load( reader );
     _fileName = pPropertiesFile;
   } catch (IOException e2) {
     e2.printStackTrace();
     String msg = GLog.error_println("Issue parsing  the mf concept id properties file " + pPropertiesFile + " :" + e2.toString());
     throw new Exception( msg);
   
   }
   
   // -------------------------
   // read in the last number
   // -------------------------
   String lastId = RelationshipUtils.getLastRelationshipIdString();
   System.err.println( " The last id = " + lastId);
   
 } // end method readPropertiesFile() ----------------

// =================================================
/**
 * getLastRelationshipId
 * 
 * @return int
*/
// =================================================
 public final static int getLastRelationshipId() {
  
    if ( _relationshipCtr == 0 )
      _relationshipCtr =  Integer.parseInt(_properties.getProperty("relationshipCounter"));
      
    return _relationshipCtr; 

} // end Method getLastRelationshipId() ---------------
 
 
//=================================================
/**
* getLastRelationshipId
* 
* @return String
*/
//=================================================
public final static String getLastRelationshipIdString() {

  StringBuffer buff = new StringBuffer();
  int ctr = getLastRelationshipId();
  
  buff.append( getRelationshipPrefix() );
  buff.append(   U.zeroPad( ctr, 7));
    
  return buff.toString();

} // end Method getLastRelationshipId() ---------------

  // =================================================
  /**
   * main 
   * 
   * @param pArgs
  */
  // =================================================
 public static void main(String[] pArgs) {
    // ----------------------------------
    // From the rel-logic, - re-read, index, create relationship ids, uniq on cui1|rel|cui2|sab 
   
   try {
     String[] args = setArgs( pArgs);
     String      inputDir = U.getOption(args, "--inputDir=", "/some/dir");
 
     RelationshipUtils relUtils = new RelationshipUtils( args );
     relUtils.readRelFiles( inputDir );
     relUtils.assignRelIds();
     relUtils.writeRelFiles();
     
     GLog.println("Dohn");

   } catch ( Exception e) {
     e.printStackTrace();
     GLog.error_println("Issue with RelationshipUtils " + e.toString());
   }
  }

  // =================================================
  /**
   * readRelFiles finds all the files with 
   * *_rel_logic.txt 
   * into a list
   * @param pInputDir
   * @throws Exception 
  */
  // =================================================
  public final void readRelFiles(String pInputDir) throws Exception {
    
    this.rowHash = new HashMap<String, List<String>>();
    try {
      File aDir = new File( pInputDir);
      if ( aDir != null && aDir.canRead() && aDir.isDirectory() ) {
        File[] listOfFiles = aDir.listFiles();
        if ( listOfFiles != null && listOfFiles.length > 0 ) 
          for ( File aFile : listOfFiles ) 
            if ( aFile.getName().endsWith(REL_FILE_TYPE)) 
              indexFile( aFile);
      }
      
    } catch ( Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "readRelFiles", "Issue reading in the output Files " + e.toString());
      throw e;
    }
    
  }  // end readRelFiles() ---------------------------

  // =================================================
  /**
   * indexFile 
   * 
   * @param aFile
   * @throws Exception 
  */
  // =================================================
  private final void indexFile(File aFile) throws Exception {
   
    try {
     String fileName = aFile.getName();
     fileName = fileName.replace(".txt", "");
     String[] rows = U.readFileIntoStringArray(aFile.getAbsolutePath());
     
     if ( rows != null && rows.length > 0 ) 
       for ( String aRow : rows ) 
        if (  !( aRow == null || aRow.startsWith("#") || aRow.trim().length() == 0 ))
           indexRow (fileName, aRow);
      
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "indexFile", "Issue reading and parsing file " + aFile.getAbsolutePath() + " :" + e.toString());
      throw e;
    }
    
    
  } // end Method indexFile() ------------------------

  // =================================================
  /**
   * indexRow 
   *    conceptName1|aui1|cui1|rel|aui2|cui2|conceptName2|sab
   *    Rehearsing|A16037760|C2371287|CHD|A16036626|C2371286|Basic learning (d130-d159)|ICF
   * 
   * @param pFileName
   * @param pRow
  */
  // =================================================
  @SuppressWarnings("unused")
  private final void indexRow(String pFileName, String pRow) {
   
    String aRow = pFileName + "|" + pRow;
    try {
      String[] cols = U.split(pRow);
      
      String conceptName1 = cols[FIELD_CONCEPT_NAME1];
      String         aui1 = cols[FIELD_AUI1];
      String         cui1 = cols[FIELD_CUI1];
      String          rel = cols[FIELD_REL];
      String conceptName2 = cols[FIELD_CONCEPT_NAME2];
      String         aui2 = cols[FIELD_AUI2];
      String         cui2 = cols[FIELD_CUI2];
      String          sab = cols[FIELD_SAB];
      
     
     
      String key = cui1 + "|" + rel + "|" + cui2;
      List<String >rows = this.rowHash.get( key);
      if ( rows == null ) {
        rows = new ArrayList<String>();
        this.rowHash.put( key, rows);
      }
      rows.add(aRow);
      
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "indexRow", "Issue reading and parsing row |" + aRow + "| :" + e.toString());
      throw e;
    }
    
  } // end Method indexRow() -------------------------

  // =================================================
  /**
   * assignRelIds 
   * @throws Exception 
   * 
  */
  // =================================================
 public final void assignRelIds() throws Exception {
  
   try {
     
     this.rowsWithRelationshipIds = new ArrayList<String>();
     
     Set<String> keys = this.rowHash.keySet();
     String[] keyArray = keys.toArray( new String[keys.size()]);
     Arrays.sort(keyArray);
     for ( String aKey : keyArray) {
       String nextRelationshipId = getNextRelationshipId();
       
       List<String> rows = this.rowHash.get( aKey);
       HashSet<String> uniqRows = new HashSet<String>( rows.size());
       for ( String row : rows ) {
         String updatedRow = nextRelationshipId + "|" + row ;
         uniqRows.add( updatedRow);
       }
       for ( String row : uniqRows ) {
         this.rowsWithRelationshipIds.add( row);
       }
       
     }
     
     
   } catch (Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "assignrelIds", "Issue assigning rel ids |" + e.toString());
     throw e;
   }
    
  } // end Method assignRelIds() ---------------------

  
   
   // =================================================
   /**
    * getNextRelationshipId  computes the next id, and saves it to
    * the datastore.
    * 
    * @return String
    * @exception
   */
   // =================================================
   public final synchronized static String getNextRelationshipId() throws Exception {
    
     String returnVal = null;
     StringBuffer buff = new StringBuffer();
    
     int ctr = getLastRelationshipId();
     ctr++;
     _relationshipCtr = ctr;
     buff.append( getRelationshipPrefix() );
     buff.append(   U.zeroPad( ctr, 7));
     
     returnVal = buff.toString();
     
     _properties.setProperty("relationshipCounter", String.valueOf( ctr) );
     storeRelationshipID();
     
     return returnVal;
     
   } // end Method getNextRelationshipId() -----------

  // =================================================
  /**
   * writeRelFiles writes out the rows into the respective 
   * files
   * @throws Exception 
   * 
  */
  // =================================================
 public void writeRelFiles() throws Exception {
    
    HashMap<String, PrintWriter> outHash = new HashMap<String, PrintWriter>();
   
   try {
     
     U.mkDir(this.outputDir);
     
     for ( String row :  rowsWithRelationshipIds ) {
      
       String[] cols = U.split( row );
       String relationshipId = cols[0];
       String fileName = cols[1];
       fileName=fileName.replace('/', '_');
       fileName = fileName.replace('\\', '_');
       fileName = fileName.replace('.', '_');
       String conceptName1 = cols[FIELD_CONCEPT_NAME1 + 2];
       String         aui1 = cols[FIELD_AUI1 + 2];
       String         cui1 = cols[FIELD_CUI1 + 2];
       String          rel = cols[FIELD_REL + 2];
       String conceptName2 = cols[FIELD_CONCEPT_NAME2 + 2];
       String         aui2 = cols[FIELD_AUI2 + 2];
       String         cui2 = cols[FIELD_CUI2 + 2];
       String          sab = cols[FIELD_SAB + 2];
       
       StringBuffer updatedRow = new StringBuffer();
       updatedRow.append( relationshipId);             updatedRow.append("|");
       updatedRow.append( conceptName1);               updatedRow.append("|");
       updatedRow.append( aui1 );                      updatedRow.append("|");
       updatedRow.append( cui1);                       updatedRow.append("|");          
       updatedRow.append(rel);                         updatedRow.append("|");
       updatedRow.append( conceptName2);               updatedRow.append("|");
       updatedRow.append( aui2 );                      updatedRow.append("|");
       updatedRow.append( cui2);                       updatedRow.append("|");
       updatedRow.append( sab);            
       updatedRow.append( "\n");
       
       PrintWriter out  = outHash.get(fileName);
       if ( out == null ) {
         System.err.println("--------> writing to this directory " + this.outputDir );
         out = new PrintWriter( this.outputDir + "/" + fileName + ".rel");
         outHash.put(fileName, out);
       }
       out.print(  updatedRow.toString());
       
       
     } // end Loop through the sorted rows with relationship ids in them
     
     // -----------------
     // Close the output files
     // -----------------
     
     Set<String> keys = outHash.keySet();
     for ( String key : keys ) {
      PrintWriter out = outHash.get( key);
      out.close();
     }
     
   
   } catch (Exception e) {
     e.printStackTrace();
     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "writeRelFiles", "Issue writing the files out" + e.toString());
     throw e;
   }
    
  } // end Method writeRelFiles() --------------------
  

  // =================================================
  /**
   * getRelationshipPrefix 
   * 
   * @return String
  */
  // =================================================
  public final static String getRelationshipPrefix() {
    String prefix = _properties.getProperty("relationshipIDPrefix");
    
    return prefix;
  } // end Method getRelationshipPrefix() ------------------------
  

  
  
  // =================================================
  /**
   * storeRelationshipID  writes the latest to the properties file
   * @throws Exception 
   * 
  */
  // =================================================
   private static synchronized void storeRelationshipID() throws Exception {
    
     PrintWriter out = null;
     
     
     String dateStamp = U.getDateStampSimple();
     try {
       File aFile = new File( _fileName);
       if ( aFile.canWrite() ) {
       
         out = new PrintWriter( _fileName);
         _properties.store( out, "Last Updated " + dateStamp );
       } else {
         String msg = GLog.error_println("Issue writing to the properties file " + _fileName + " : cannot write to file ");
         throw new Exception ( msg );
       }
     } catch ( Exception e ) {
       e.printStackTrace();
       String msg = GLog.error_println("Issue writing to the properties file " + _fileName + " :" + e.toString());
       throw new Exception ( msg );
     }
    
  } // end Method storeID() -------------------------------

  
  //------------------------------------------
  /**
  * setArgs
  * 
  * 
  * @return
  */
  // ------------------------------------------
  public static String[] setArgs(String pArgs[]) {

   // -------------------------------------
   // dateStamp
   String dateStamp = U.getDate();

   // -------------------------------------
   // Input and Output

   String propertiesFile = U.getOption( pArgs, "--relPropertiesFile=", "./softwareRepos/framework-legacy/00_legacy/04.2_tUtils/src/resources/relationshipIds.properties");
  
   String     inputDir = U.getOption(pArgs,  "--inputDir=", "./2019AA/META");
   String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_relationships_" + dateStamp);
 
   
   String args[] = {
       "--relPropertiesFile=" + propertiesFile,
       "--inputDir=" + inputDir,
       "--outputDir=" + outputDir,
      
       
      
       
   };

   
      
   if ( Use.usageAndExitIfHelp("RelationshipUtils",   pArgs, args  ))
     System.exit(0);
    

   return args;

  }  // End Method setArgs() -----------------------

   // --------------------------
   // Global Variables
   // --------------------------
  private static Properties _properties = null;
  private static String _fileName = null;
  private static int _relationshipCtr = 0;
  private HashMap<String, List<String>> rowHash = null;
  private List<String>  rowsWithRelationshipIds = null;
  private String outputDir = null;
  private static final String REL_FILE_TYPE = "_rel_logic.txt";
 


} // end Class RelationshipUtils() ----------------
