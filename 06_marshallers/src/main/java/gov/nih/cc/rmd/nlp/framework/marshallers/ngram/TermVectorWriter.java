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
 * TermVector1 writer writes out a file of term frequencies to create 
 * 
 * 
 * 
 * row for each document/section seen in a document
 * along with it's document type and station and year if it's available.
 * 
 *   prodcu
 *  
 *  These are going into csv files (one per process)
 *  
 *  documentId|sta3n|enterpriseDocumentType|date|SectionName|SectionSize|word1_freq|word2_freq|... wordN_freq|
 *     
 *        
 * @author  Guy Divita 
 * @created May 22, 2017
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.ngram;


import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.Section;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.WordToken;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class TermVectorWriter extends gov.nih.cc.rmd.nlp.framework.marshallers.writer.AbstractWriter  {
 
 

// =======================================================
  /**
   * Constructor IndexingWriter 
   *
   * @param pArgs
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public TermVectorWriter(String[] pArgs) throws ResourceInitializationException {
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
    
    try {
    int numberOfChars = 0;
    
   
    DocumentHeader docHeader = VUIMAUtil.getDocumentHeader(pJCas);
    
    String docId = docHeader.getDocumentId();
    String docType = docHeader.getDocumentType();
    String reportDate = docHeader.getReferenceDate();
    
    String year = getYear( reportDate );
    String sta3n = getSta3n( pJCas);
    
    StringBuffer coreCharacteristicsBuff0 = new StringBuffer();
   
    StringBuffer coreCharacteristicsBuff1 = new StringBuffer();
    
    
    coreCharacteristicsBuff1.append(docId); 
    coreCharacteristicsBuff1.append(',');
    
    coreCharacteristicsBuff1.append(docType); 
    coreCharacteristicsBuff1.append(',');
    
    coreCharacteristicsBuff1.append(sta3n); 
    coreCharacteristicsBuff1.append(',');
    
    coreCharacteristicsBuff1.append(year); 
    coreCharacteristicsBuff1.append(',');
    String firstPart = coreCharacteristicsBuff1.toString();
    
    // ------------------------------------
    // Loop through the sections 
    
    List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, Section.typeIndexID, true );
    
    if ( sections != null && !sections.isEmpty()) {
      for ( Annotation section : sections ) {
        String sectionName = ((Section)section).getSectionName();
        if ( sectionName != null && sectionName.trim().length() > 0 )
        	sectionName = U.extremeNormalize(sectionName);
        if ( sectionName != null && sectionName.length() > 40) sectionName = sectionName.substring(0,40);
        String txt = section.getCoveredText();
        if ( txt != null ) {
          StringBuffer coreCharacteristicsBuff2 = new StringBuffer();
          int contentHeadingSize = getContentHeadingSize( (Section) section);
          
          int sectionSize = txt.length() + contentHeadingSize;
          int sectionBegin = getSectionBegin( (Section) section);
          int sectionEnd = section.getEnd();
          
          coreCharacteristicsBuff2.append(sectionName); 
          coreCharacteristicsBuff2.append(',');
          coreCharacteristicsBuff2.append(sectionSize);
          coreCharacteristicsBuff2.append(',');
          coreCharacteristicsBuff2.append(sectionBegin);
          coreCharacteristicsBuff2.append(',');
          coreCharacteristicsBuff2.append(sectionEnd);
          coreCharacteristicsBuff2.append("\n");
        
          coreCharacteristicsBuff0.append( firstPart + coreCharacteristicsBuff2.toString());
         
          
        } // this is a good section
     } // end loop thru sections
    }  // end if there are any sections
	 
    this.out.print( coreCharacteristicsBuff0.toString());
    }    
    catch (Exception e) {
    	e.printStackTrace();
    	System.err.println("Issue with section csv writer " + e.toString());
    }
    
    this.performanceMeter.stopCounter();
   
    
  } // end Method process() ----------------

  
// =======================
  /**
   * getSectionBegin retrieves the contentHeading (if there is one)
   * and uses the begining offset of that
   *
   * @param section
   * @return int
   */
  // =======================
  private int getSectionBegin(Section section) {
   
    int returnVal = section.getBegin();
    ContentHeading heading = section.getSectionHeading();
    if ( heading != null)
      returnVal = heading.getBegin();
    
    return returnVal;
    } // End Method getSectionBegin =======
  

//=======================
 /**
  * getSectionBegin retrieves the contentHeading (if there is one)
  * and uses the begining offset of that
  *
  * @param section
  * @return int
  */
 // =======================
 private int getContentHeadingSize(Section section) {
  
   int returnVal = 0;
   ContentHeading heading = section.getSectionHeading();
   if ( heading != null) {
     String headingText = heading.getCoveredText();
     if ( headingText != null)
       returnVal = headingText.length();
   }
   
   return returnVal;
   } // End Method getSectionBegin =======
 

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
	  
		  if ( this.out != null ) 
			  this.out.close();
		 
	  } catch (Exception e) {
		  e.printStackTrace();
		  GLog.println("Issue closing the section  table " + e.toString());
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
 * openOutputFile 
 * 
 * @param pOUtputFile
 * @return PrintWriter
 * @throws Exception 
 * 
 * 
 **/
// ----------------------------------
private PrintWriter openOutputFile(String pOutputFile) throws Exception {
	
	try {
	
		String fileName = this.outputDir + "/csv/" + pOutputFile + ".csv";
		this.out = new PrintWriter( fileName);
	} catch (Exception e) {
		e.printStackTrace();
		GLog.println("Issue trying to open the output file " + e.toString());
		throw e;
	}
	return out;
} // end Method openOutputFile

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
    
    String fileName = "section_" + sectionCSVCtr++;
   
    try {
    this.out = openOutputFile( fileName );
    this.out.print("DocumentID,DocType,Location,Year,SectionName,SectionSize,Begin,End\n");
    
    
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
   

   private static int sectionCSVCtr = 0;
  private PrintWriter out = null;
	
 
} // end Class MetaMapClient() ---------------
