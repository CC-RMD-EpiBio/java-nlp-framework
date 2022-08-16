/*******************************************************************************
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                       Epidemiology and Biostatistics Branch 
 *                                            2019 - 2022
 *   ---------------------------------------------------------------------------
 *   Copyright Notice:
 *   This software was developed and funded by the National Institutes of Health
 *   Clinical Center (NIHCC), part of the National Institutes of Health (NIH),
 *   and agency of the United States Department of Health and Human Services,
 *   which is making the software available to the public for any commercial
 *   or non-commercial purpose under the following open-source BSD license.
 *  
 *   Government Usage Rights Notice:
 *   The U.S. Government retains unlimited, royalty-free usage rights to this 
 *   software, but not ownership, as provided by Federal law. Redistribution 
 *   and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *      1. Redistributions of source code must retain the above copyright
 *         and government usage rights notice, this list of conditions and the 
 *         following disclaimer.
 *  
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *        
 *      3. Neither the names of the National Institutes of Health Clinical
 *         Center, the National Institutes of Health, the U.S. Department of
 *         Health and Human Services, nor the names of any of the software
 *         developers may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
 *   
 *      4. The U.S. Government retains an unlimited, royalty-free right to
 *         use, distribute or modify the software.
 *   
 *      5. Please acknowledge NIH CC as the source of this software by including
 *         the phrase: "Courtesy of the U.S. National Institutes of Health Clinical Center"
 *          or 
 *                     "Source: U.S. National Institutes of Health Clinical Center."
 *  
 *     THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS
 *     IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *     TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *     PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 *     OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *     EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *     PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *     NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *     When attributing this code, please make reference to:
 *        Divita G, Carter ME, Tran LT, Redd D, Zeng QT, Duvall S, Samore MH, Gundlapalli AV. 
 *        v3NLP Framework: tools to build applications for extracting concepts from clinical text. 
 *        eGEMs. 2016;4(3). 
 *      
 *     In the absence of a specific paper or url listed above, reference https://github.com/CC-RMD-EpiBio/java-nlp-framework
 *   
 *     To view a copy of this license, visit https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD
 *******************************************************************************/
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
