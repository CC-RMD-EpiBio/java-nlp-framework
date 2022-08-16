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
package gov.nih.cc.rmd.nlp.framework.marshallers.formVariability;
//=================================================
/**
 * SectionFreqVectorAnnotator creates a SectionFreqVector
 * to tack onto the FormatVariability vector
 * 
 *
 * @author  Guy Divita 
 * @created Oct 1, 2019
 *
 * 
 */



import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.SectionZone;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.DocumentHeader;



public class SectionFreqVectorAnnotator extends JCasAnnotator_ImplBase {





    // -----------------------------------------
    /** 
     * process counts up the features that we can tract for
     * formatting variability
     *
     * @param pAJCas
     * @throws AnalysisEngineProcessException
     */
    // -----------------------------------------
    @Override
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
     
    
	   try {
     
	     DocumentHeader header = VUIMAUtil.getDocumentHeader(pJCas);
	     
	     int vector[] = new int[ this.numberOfSections ];
	     
	     List<Annotation> sectionZones = UIMAUtil.getAnnotations(pJCas, SectionZone.typeIndexID, false );
       if ( sectionZones != null && !sectionZones.isEmpty()) {
         
         for ( Annotation section : sectionZones) {
           String sectionName = (( SectionZone) section ).getSectionName() ;
           
            processSectionName( sectionName, vector  );
           
          
             
         }
       }
       
       String vectorString = convertVector( vector );
       header.setOtherMetaData( vectorString);
     
      } catch (Exception e) {
        e.getStackTrace();
        String msg = "Issue " + e.getMessage() ;
        System.err.println(msg);
      }
        
        
      System.err.println("Finished processing file" );
    } // end Method process -------------




    // =================================================
    /**
     * convertVector converts the int vector into a pipe delimited string
     * 
     * @param pVector
     * @return String
    */
    // =================================================
     private String convertVector(int[] pVector) {
     
       StringBuffer buff = new StringBuffer();
       
       for ( int i = 0 ; i < pVector.length ; i++) {
         buff.append( pVector[i]);
         
         if ( i < pVector.length -1 )
           buff.append("|");
       }
       
       return buff.toString();
       
    } // end Method convertVector() -----------------




    // =================================================
    /**
     * processSectionName returns a vector of the most popular section names
     * 
     * @param pSectionName
    */
    // =================================================
    private void  processSectionName(String pSectionName, int[] vector ) {
     
      int[] sectionFreq = this.sectionNames.get( pSectionName );
    
      if ( sectionFreq != null ) {
        int sectionId = sectionFreq[0];
        vector[ sectionId]++;
      }
     
    } // end Method processSectionName() ---------------




    //----------------------------------
     /**
      * destroy
     * 
      **/
     // ----------------------------------
     public void destroy() {
       try {
         
         this.performanceMeter.writeProfile( this.getClass().getSimpleName());
           
        } catch ( Exception e) {
         e.printStackTrace();
       }
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
      
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  
        
        initialize( args );   
        
      } catch (Exception e ) {
        System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
        throw new ResourceInitializationException();
      }
      
 
    } // end Method initialize() ---------

    //----------------------------------
      /**
       * initialize 
       *
       * @param pArgs
       * 
       **/
      // ----------------------------------
      public void initialize(String pArgs[]) throws ResourceInitializationException {
        
        this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
        
        String outputDir = U.getOption(pArgs,  "--outputDir=", "/some/output/dir" ); 
        
        String sectionNamesFile = U.getOption(pArgs,  "--sectionNames=", "/some/dir/sectionNames.csv" ); 
        
      
        
        initializeSectionNames( outputDir, sectionNamesFile  );
    
    } // end Method initialize() --------------
    

   
    

    //----------------------------------
    /**
     * initializeSectionsNames
     *
     * @param pSectionNamesFile
     * 
     **/
    // ----------------------------------
    public void initializeSectionNames (String pOutputDir, String pSectionNamesFile ) throws ResourceInitializationException {
      
     
      try {
        U.mkDir(pOutputDir + "/stats");
        
        this.out = new PrintWriter( pOutputDir + "/stats/sectionVector.txt");
        this.out2 = new PrintWriter( pOutputDir + "/stats/sectionVector.csv");
        
        
        this.sectionNames = new HashMap<String, int[]> ();
        
        String[] rows = U.readFileIntoStringArray( pSectionNamesFile );
        
        for ( int i = 0; i < rows.length; i++) {
          
          String cols[] = U.split( rows[i]);
          String sectionName = cols[1].trim();
          
          int iArray[] = new int[1];
          iArray[0] = i;
          this.sectionNames.put( cols[1].trim(), iArray);
          
          this.out.print(U.zeroPad(i, 8) + "|" + sectionName + "\n");
          
          this.out2.print( sectionName + "|");
          
          
        }
        
        this.numberOfSections = rows.length;
        
        
        this.out.close();
        this.out2.print("\n");
        this.out2.close();
        
       
        
        
      } catch (Exception e) {
        e.printStackTrace();
        throw new ResourceInitializationException();
      }
    
    
 
    } // end Method initialize() --------------


    

    
  // ----------------------------------------
    // Global variables
    // ----------------------------------------
    private  PrintWriter out = null;
    private  PrintWriter out2 = null;
    private ProfilePerformanceMeter performanceMeter = null;
    private HashMap<String, int[]> sectionNames = null;
    private int numberOfSections = 0;
    
    
  
  
  

} // end Class toCommonModel
