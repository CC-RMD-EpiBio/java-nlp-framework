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
// =================================================
/**
 * Phrase Vector Writer writer writes out phrase stats 
 *   
 *  These are going into csv files (one per process)
 *  
 *   enterpriseDocumentTitle|sectionName|sta3n|year|phrase|docFreq|termFreq
 *   
 *   or 
 *   
 *   enterpriseDocumentTitle|sectionName|sta3n|year|docFreq1|docFreq2|docFreq3| .....
 *   
 *   phraseIndex|Phrase Name
 *        
 * @author  Guy Divita 
 * @created feb 1, 2017
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.ngram;


import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.Section;
import gov.va.vinci.model.Concept;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class PhraseVectorWriter extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter  {
 
  public static int CORE_CHARACTERISTICS = 0;


// =======================================================
  /**
   * Constructor IndexingWriter 
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public PhraseVectorWriter(String[] pArgs) throws ResourceInitializationException {
    initialize( pArgs);
  }

  // -----------------------------------------
  /**
   * process 
   *   writes out the instances to a file, and accumulates
   *   stats for each file until the proces is done.
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    this.performanceMeter.startCounter();
    
    
   
    
    DocumentHeader docHeader = VUIMAUtil.getDocumentHeader(pJCas);
    
    
    String docType = docHeader.getDocumentType();
    String reportDate = docHeader.getReferenceDate();
    
    String year = getYear( reportDate );
    String sta3n = getSta3n( pJCas);
    
    // Iterate thru the valid sections
    List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, Section.typeIndexID, true );
    
    if ( sections != null && !sections.isEmpty()) {
     
      for ( Annotation section: sections )
      
      processSection( pJCas, section, docType, sta3n, year);
        
    }
    
    
	
    this.performanceMeter.stopCounter();
   
    
  } // end Method process() ----------------

  
// =======================
  /**
   * processSection 
   *
   * @param pJCas
   * @param section
   * @param phraseVectorHash
   */
  // =======================
  private void processSection(JCas pJCas, 
                              Annotation pSection, 
                              String pDocumentType,
                              String pStation,
                              String pYear) {
    
    // retrieve section name
    // retrieve the concepts within this section
    
    String sectionName = ((Section) pSection).getSectionName();
    
    List<Annotation> concepts = UIMAUtil.getAnnotationsBySpan(pJCas, Concept.typeIndexID, pSection.getBegin(), pSection.getEnd(), true);
    
    if ( concepts != null && !concepts.isEmpty() ) {
      for ( Annotation concept: concepts ) {
        String phraseName = ((Concept) concept ).getConceptNames();
        
        String key = phraseName + "|" + pDocumentType + "|" + sectionName + "|" + pStation + "|" + pYear + "|"; 
        
        int[] freqs = this.phraseVectorHash.get(key);
        if ( freqs == null ) {
          freqs = new int[1];
          freqs[0] =0;
        }
        freqs[0]++;
        this.phraseVectorHash.put( key, freqs);
        
        
        
      } // end for loop
    }
    
  } // End Method processSection =======
  

private String getYear(String referenceDate ) {
	
	String year = "1900";
	
	if ( referenceDate != null && !referenceDate.equals("unknown")) {
		// Assuming the date format is yyyy-mm-dd 
		year = referenceDate.substring(0,4);
		
	}
	return year;
	}

private String getSta3n(JCas pJCas) {
	String sta3n        = VUIMAUtil.getSta3n(pJCas);
	if (sta3n != null && sta3n.length() > 3) sta3n = sta3n.substring(0,3);
	
	if ( sta3n == null || sta3n.equals("null")) sta3n = "999";
	
	return sta3n ;
}





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
     
	  try {
	  
		  PrintWriter out = new PrintWriter( this.outputDir + "csv/phraseVectorsForSection.csv");
		  out.print("phrase|DocType|Section|Location\n");
		  
		  Set<String> keys = this.phraseVectorHash.keySet();
		  for ( String key: keys) {
		    int[] freqs = this.phraseVectorHash.get(key);
		    out.print(key + "|" + freqs[0] + "\n");
		  }
		 
		  out.close();
		  
	  } catch (Exception e) {
		  e.printStackTrace();
		  GLog.println("Issue closing the coreCharacteriscs table " + e.toString());
	  }
	  GLog.println("Closed writer " );
	  
    this.performanceMeter.writeProfile( this.getClass().getSimpleName());
    
     
    
    
  
  } // end Method Destroy() -----------------

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
    
   
   
  
    try {
    	File aDir = new File( this.outputDir);
    	if ( !aDir.exists() )
    		U.mkDir( this.outputDir);
    	File cDir = new File(this.outputDir + "/csv" );
    	if ( !cDir.exists() )
    		U.mkDir( this.outputDir + "/csv");
    			
    	
    } catch (Exception e1) {
      e1.getStackTrace();
      String msg = "Issue with making the stats dir \n" + e1.getMessage() ;
      System.err.println( msg );
      throw new ResourceInitializationException ();
    }
    
    String fileName = "coreCharacteristics_" + CORE_CHARACTERISTICS++;
   
    try {
   
   
    this.phraseVectorHash = new HashMap<String,int[]> ();
    
    
  } catch (Exception e1) {
      e1.getStackTrace();
      String msg = "Issue with making the stats dir \n" + e1.getMessage() ;
      System.err.println( msg );
      throw new ResourceInitializationException ();
    }
    
    
    
  }  // End Method initialize() -----------------------
  
  






  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   
   private String outputDir = null;
   private ProfilePerformanceMeter              performanceMeter = null;
   private HashMap<String,int[]>                phraseVectorHash = null;                
   

	
 
} // end Class MetaMapClient() ---------------
