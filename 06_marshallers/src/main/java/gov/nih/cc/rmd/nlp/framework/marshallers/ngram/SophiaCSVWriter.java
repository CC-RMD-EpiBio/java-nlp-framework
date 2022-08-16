// =================================================
/**
 * SophiaWriter writer writes out stats for documents
 * into the tables needed for full text indexing.
 *  
 *  These are going into csv files (one per process)
 *  
 *  documentId|sta3n|enterpriseDocumentType|date|numberOfChars|NumberOfSections|NumberOfSentences|NumberOfWords
 *     
 *        
 * @author  Guy Divita 
 * @created feb 1, 2017            |Freq|Freq ...|Freq  |Freq|Freq|
 * @updated Aug 1, 2017  --  added |sty1|sty2 ...|sty156|grp1|grp2|
 *                                 +----+----+ ..+------+----+----+
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.ngram;


import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.vinci.model.Concept;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class SophiaCSVWriter extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter  {
 
  private static final int TERM_FREQ = 0;
public static int CORE_CHARACTERISTICS = 0;
// =======================================================
  /**
   * Constructor IndexingWriter 
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public SophiaCSVWriter(String[] pArgs) throws ResourceInitializationException {
    initialize( pArgs);
  }

  // -----------------------------------------
  /**
   * process writes out the instances to a file, and accumulates
   *         stats for each file until the proces is done.
   * @param pJCas
   *   
   */
  // -----------------------------------------
  public synchronized void process(JCas pJCas) throws AnalysisEngineProcessException {

    this.performanceMeter.startCounter();

    try {
    
    String    documentId = VUIMAUtil.getDocumentId(pJCas);
	String         sta3n = getSta3n(pJCas );
	String referenceDate = VUIMAUtil.getReferenceDate(pJCas);
	String          year = getYear( referenceDate );
	String   documentType = VUIMAUtil.getDocumentType(pJCas);
	
	String rowPrefix = documentId   + DELIMETER +
			           documentType + DELIMETER +
			           sta3n        + DELIMETER +
			           year         ; 
	
	
	int[] styFreqArray = new int[NUMBER_OF_SEMANTIC_TYPES +1 ]; 
	int[]  groupVector = new int[NUMBER_OF_GROUPS];
	List<Annotation>  concepts = UIMAUtil.getAnnotations(pJCas, Concept.typeIndexID, true );
	
	if ( concepts != null && !concepts.isEmpty() ) {
		for ( Annotation concept: concepts ) {
			String semanticTypez  = ((Concept) concept).getCategories();
			String semanticTypes[] = U.split(semanticTypez, ":");
			HashSet<String>seenSemanticTypeHash = new HashSet<String>(5);
			HashSet<Integer>seenSemanticGroupHash = new HashSet<Integer>(2);
			if ( semanticTypes != null )
				for ( String typez: semanticTypes ) {
					String types[] = U.split(typez);
					for ( String aType : types ) {
						int[] tui = tuiLookup.get( aType);
						if ( tui != null ) { 
							if ( !seenSemanticTypeHash.contains(aType) ) { 
								seenSemanticTypeHash.add( aType );
								styFreqArray[tui[0]]++;
					
								int[] groupIdx = groupLookup.get(aType);
								if ( groupIdx != null && !seenSemanticGroupHash.contains(groupIdx[0])) {
									groupVector[groupIdx[0]]++;
									seenSemanticGroupHash.add( groupIdx[0]);
								}
							}
						}
					} // end loop thru the multiple semantic types for a concept
					
					
				} // end Loop thru semantic types
		} // end loop thru concepts
	} // end if there are concepts	
	String semantcTypeVector = formatFreqArray( styFreqArray);
	String semanticGroupVector = formatFreqArray ( groupVector);
		
	this.out.print( rowPrefix + DELIMETER + semantcTypeVector  + semanticGroupVector + "\n");
	
	
  } catch (Exception e) {
	  e.printStackTrace();
	  System.err.println("Issue with processing " + e.toString());
  }
    
    this.performanceMeter.stopCounter();
   
 



} // end Method process() ----------------

//-----------------------------------------
 /**
  * foramtFreqArray prints out the array as xx,xx,xx, ... xx
  * where each position is the tui id and the cell is the term frequency
  * in 
  *   @param pFreqArray
  */
 // -----------------------------------------
private String formatFreqArray(int[] pFreqArray) {
	
	StringBuffer buff = new StringBuffer();
	for ( int i = 1; i < pFreqArray.length; i++ ) {
		buff.append( pFreqArray[i] );
		if ( i < pFreqArray.length ) 
			buff.append(DELIMETER);
	}
	
	return buff.toString();
} // End Method formatFreqArray() -----

//-----------------------------------------
/**
* getYear gets the year from the referenceDateTime
* 
* @param pDate
*/
// -----------------------------------------
private String getYear(String pDate ) {
	
	String year = "1900";
	
	if ( pDate != null && !pDate.equals("unknown")) {
		// Assuming the date format is yyyy-mm-dd 
		year = pDate.substring(0,4);
		
	}
	return year;
	} // End Method getYear() ----------------

//-----------------------------------------
/**
* getSta3n gets the station from the header
* 
* @param pJCas
*/
//-----------------------------------------
private String getSta3n(JCas pJCas) {
	String sta3n        = VUIMAUtil.getSta3n(pJCas);
	if (sta3n != null && sta3n.length() > 3) sta3n = sta3n.substring(0,3);
	
	if ( sta3n == null || sta3n.equals("null")) sta3n = "999";
	
	return sta3n ;
} // end Method getSta3n() ----------------


// -----------------------------------------
  /** 
   * destroy closes the open instance freq table
   *         and writes out a summary table
   * 
   *
   */
  // -----------------------------------------
  @Override
  public void destroy() {
     
	  if (this.out != null ) {
		  this.out.close();
		  GLog.println("Closed writer " );
	  }	  
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
  
  } // end Method Destroy() -----------------
  
  
//----------------------------------
  /**
   * header 
   * 
   * @return String
   **/
  // ----------------------------------
  private String header()  {
	  
	  
  
	  StringBuffer semanticTypeHeader = new StringBuffer();
	  StringBuffer semanticGroupHeader = new StringBuffer();
	  
	  for ( int i = 1; i <= NUMBER_OF_SEMANTIC_TYPES; i++) {
		  semanticTypeHeader.append(   "T" + U.zeroPad(i,3) );
		  semanticTypeHeader.append(DELIMETER);
	  }
      semanticGroupHeader.append("ACTI"); semanticGroupHeader.append(DELIMETER);   
      semanticGroupHeader.append("ANAT"); semanticGroupHeader.append(DELIMETER);  
      semanticGroupHeader.append("CHEM"); semanticGroupHeader.append(DELIMETER);  
      semanticGroupHeader.append("CONC"); semanticGroupHeader.append(DELIMETER);  
      semanticGroupHeader.append("DEVI"); semanticGroupHeader.append(DELIMETER);  
      semanticGroupHeader.append("DISO"); semanticGroupHeader.append(DELIMETER);  
      semanticGroupHeader.append("GENE"); semanticGroupHeader.append(DELIMETER);  
      semanticGroupHeader.append("GEOG"); semanticGroupHeader.append(DELIMETER);  
      semanticGroupHeader.append("LIVB"); semanticGroupHeader.append(DELIMETER);  
      semanticGroupHeader.append("OBJC"); semanticGroupHeader.append(DELIMETER);  
      semanticGroupHeader.append("OCCU"); semanticGroupHeader.append(DELIMETER);  
      semanticGroupHeader.append("ORGA"); semanticGroupHeader.append(DELIMETER);  
      semanticGroupHeader.append("PHEN"); semanticGroupHeader.append(DELIMETER);  
      semanticGroupHeader.append("PHYS"); semanticGroupHeader.append(DELIMETER);  
      semanticGroupHeader.append("PROC");
  
      String theHeader = ""
      		+ "DocumentID"   + DELIMETER +
              "DocumentType" + DELIMETER +
              "Station"      + DELIMETER +
              "Year"         + DELIMETER + 
              semanticTypeHeader + 
              semanticGroupHeader ;
      
      return theHeader;
  } // end Method header() ----------------
	
	
//-----------------------------------------
 /** 
  * destroy closes the open instance freq table
  *         and writes out a summary table
 * @throws Throwable 
  * 
  *
  */
 // -----------------------------------------
 @Override
 public  void finalize() throws Throwable {
    
	 destroy();
	 super.finalize();
 }

 

//----------------------------------
  /**
   * initialize 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
  
    String[]    args = (String[])aContext.getConfigParameterValue("args");
    
    initialize(args);
    
   
  } // end Method initialize() -------

//----------------------------------
  /**
   * initialize 
   * 
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
  
    
        this.outputDir = U.getOption(pArgs, "--outputDir=", "./");
    
    
   
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
     initializeSemanticTypesLookup();
     initializeSemanticGroupLookup();
   
  
    try {
    	File aDir = new File( this.outputDir);
    	if ( !aDir.exists() )
    		U.mkDir( this.outputDir);
    	File cDir = new File(this.outputDir + "/csv" );
    	if ( !cDir.exists() )
    		U.mkDir( this.outputDir + "/csv");
    			
    	this.out = new PrintWriter( this.outputDir + "/csv/semanticTypeVectors_" + FileCtr++  + ".csv" );
    	
    	this.out.print( header() + "\n" );
    	
    } catch (Exception e1) {
      e1.getStackTrace();
      String msg = "Issue with making the stats dir \n" + e1.getMessage() ;
      System.err.println( msg );
      throw new ResourceInitializationException ();
    }
    
 
	
  }  // End Method initialize() -----------------------
  
  
//----------------------------------
  /**
   * initializeSemanticTypesLookup 
 * @throws ResourceInitializationException 
   * 
   * 
   **/
  // ----------------------------------
  private void initializeSemanticTypesLookup() throws ResourceInitializationException {
		
	  if ( tuiLookup == null ) 
		  tuiLookup = new HashMap<String, int[]> (200);
		  
	  String semanticTypeLookupFile = "resources/com/ciitizen/framework/semanticTypes/SemanticTypes_2013AA.txt";
	  try {
		
		 String[] rows = U.readClassPathResourceIntoStringArray(semanticTypeLookupFile);
		  
		 if ( rows != null )
			 for (String row: rows  ) {
				 if ( row.startsWith("#"))  continue;
				 
				 String cols[]= U.split(row);
				 String semanticTypeAbbr = cols[0];
				 int    tui = Integer.parseInt(cols[1].substring(1).trim());
				 int tuis[] = tuiLookup.get( semanticTypeAbbr);
				 if ( tuis == null ) {
					 tuis = new int[1];
					 tuis[0] = tui;
					 tuiLookup.put(semanticTypeAbbr, tuis);
				 }
			 }
		 
	  } catch (Exception e) {
		  e.printStackTrace();
		  System.err.println("Issue reading in the semanticType file " + semanticTypeLookupFile + " " + e.toString());
		  throw new ResourceInitializationException();
	  }
		
	} // End Method initializeSemanticTypesLookup() -



  
//----------------------------------
  /**
   * initializeSemanticGroupLookup 
 * @throws ResourceInitializationException 
   * 
   * 
   **/
  // ----------------------------------
  private void initializeSemanticGroupLookup() throws ResourceInitializationException {
		
	  if ( groupLookup == null ) 
		  groupLookup = new HashMap<String, int[]> (NUMBER_OF_SEMANTIC_TYPES);
		  
	  String semanticGroupLookupFile = "resources/com/ciitizen/framework/semanticTypes/SemGroups_2013.txt";
	  try {
		
		 String[] rows = U.readClassPathResourceIntoStringArray(semanticGroupLookupFile);
		  
		 if ( rows != null )
			 for (String row: rows  ) {
				 if ( row.startsWith("#"))  continue;
				 
				 String cols[]= U.split(row);
				 String groupAbbr = cols[0];
				 String groupName = cols[1];
				 String tui = cols[2];
				 String semType = cols[3];
				 String semTypeAbbr = cols[4];
				 
				 
				 
				 int groupIdx[] = groupLookup.get( semTypeAbbr);
				 if ( groupIdx  == null ) {
					 groupIdx = new int[1];
					 switch ( groupAbbr ) {
					 case "ACTI": groupIdx[0] = 1; break;
					 case "ANAT": groupIdx[0] = 2; break;
					 case "CHEM": groupIdx[0] = 3; break;
					 case "CONC": groupIdx[0] = 4; break;
					 case "DEVI": groupIdx[0] = 5; break;
					 case "DISO": groupIdx[0] = 6; break;
					 case "GENE": groupIdx[0] = 7; break;
					 case "GEOG": groupIdx[0] = 8; break;
					 case "LIVB": groupIdx[0] = 9; break;
					 case "OBJC": groupIdx[0] = 10; break;
					 case "OCCU": groupIdx[0] = 11; break;
					 case "ORGA": groupIdx[0] = 12; break;
					 case "PHEN": groupIdx[0] = 13; break;
					 case "PHYS": groupIdx[0] = 14; break;
					 case "PROC": groupIdx[0] = 15; break;
					 default:     groupIdx[0] = 0;  //<---- should never happen
					 
					 }
					 groupLookup.put(semTypeAbbr, groupIdx);
				 }
			 }
		 
	  } catch (Exception e) {
		  e.printStackTrace();
		  System.err.println("Issue reading in the semanticType file " + semanticGroupLookupFile + " " + e.toString());
		  throw new ResourceInitializationException();
	  }
		
	} // End Method initializeSemanticTypesLookup() -



  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private String outputDir = null;
   private ProfilePerformanceMeter              performanceMeter = null;
   public static final int NUMBER_OF_SEMANTIC_TYPES = 205 ;
	private static HashMap<String,int[]> tuiLookup = null;
	private static HashMap<String,int[]> groupLookup = null;
	private static char DELIMETER = ',';
	private PrintWriter out = null;
	private static int FileCtr = 0;
	private static int NUMBER_OF_GROUPS = 15 + 1;
	
	

  
} // end Class MetaMapClient() ---------------
