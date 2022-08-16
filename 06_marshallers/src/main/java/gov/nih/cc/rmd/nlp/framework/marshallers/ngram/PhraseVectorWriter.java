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
