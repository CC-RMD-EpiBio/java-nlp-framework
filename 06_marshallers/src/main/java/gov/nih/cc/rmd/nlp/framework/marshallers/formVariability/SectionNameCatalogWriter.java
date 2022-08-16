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
 * SectionNameCatalog writes the names of the sections seen.
 * 
 * This will be transformed into a vector for formatVariability Writer
 * 
 *
 * @author  Guy Divita 
 * @created June 26, 2012
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
import gov.nih.cc.rmd.nlp.framework.marshallers.writer.Writer;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.ContentHeading;



public class SectionNameCatalogWriter extends JCasAnnotator_ImplBase implements Writer {




  // -----------------------------------------
  /** 
   * Constructor 
   * @throws ResourceInitializationException 
  */
    // -----------------------------------------
    public  SectionNameCatalogWriter() throws AnalysisEngineProcessException, ResourceInitializationException {
      String args[] = null;
      initialize(  args);
    } // end Constructor 


    // -----------------------------------------
    /** 
     * Constructor 
     * 
     * @param pArgs
     * @throws ResourceInitializationException 
    */
      // -----------------------------------------
      public  SectionNameCatalogWriter(String[] pArgs) throws AnalysisEngineProcessException, ResourceInitializationException {
        initialize( pArgs);
      } // end Constructor 



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
     
	     List<Annotation> sectionZones = UIMAUtil.getAnnotations(pJCas, SectionZone.typeIndexID, false );
       if ( sectionZones != null && !sectionZones.isEmpty()) {
         
         for ( Annotation section : sectionZones) {
           String sectionName = (( SectionZone) section ).getSectionName() ;
           processSectionName( sectionName  );
             
         }
       }
     
      } catch (Exception e) {
        e.getStackTrace();
        String msg = "Issue " + e.getMessage() ;
        System.err.println(msg);
      }
        
        
      System.err.println("Finished processing file" );
    } // end Method process -------------




    // =================================================
    /**
     * processSectionName keeps and counts sectionName frequencies
     * 
     * @param pSectionName
    */
    // =================================================
    private static synchronized void processSectionName(String pSectionName) {
     
      int[] sectionFreq = sectionNames.get( pSectionName );
      
      if ( sectionFreq == null ) {
        sectionFreq = new int[1];
        sectionFreq[0] = 0;
      }
      sectionFreq[0]++;
      sectionNames.put( pSectionName, sectionFreq );
      
      
      
      
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
         
         writeSectionNames();
         
         
        } catch ( Exception e) {
         e.printStackTrace();
       }
     }
    // =================================================
    /**
     * writeSectionNames 
     * 
    */
    // =================================================
   private static synchronized void writeSectionNames() {

      {
     if ( out != null ) {
       
       Set<String> keys = sectionNames.keySet();
       
       String[] rows = new String[ keys.size()];
       
       
       int i = 0; 
       for ( String key : keys )  {
         if ( key != null && key.trim().length() > 0 )
           rows[i++] =  U.zeroPad( sectionNames.get(key)[0], 8 ) + "|" + key ;
         else 
           rows[i++] = "";
       }
       
       Arrays.parallelSort( rows);
       
      
       
       for ( i = rows.length -1; i >= 0; i-- ) {
         String row = rows[i];
         if ( row != null && row.trim().length() > 0 && row.length() < 50 )
           out.print(row + "\n");
       }
       
       out.close();
     }
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
        
        this.sectionNames = new HashMap<String, int[]> ();
        
        initialize( outputDir );
    
    } // end Method initialize() --------------
    

   
    

    //----------------------------------
    /**
     * initialize picks up the parameters needed to write a cas out to the database
    
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(String pOutputDir ) throws ResourceInitializationException {
      
     
      try {
        
        String outputDir = pOutputDir + "/stats";
        U.mkDir(outputDir );
        String outputFile = outputDir + "/" + "sectionNames.csv";
        
        if ( out == null )
          out = new PrintWriter( outputFile);
        
        
        
      } catch (Exception e) {
        e.printStackTrace();
        throw new ResourceInitializationException();
      }
    
    
 
    } // end Method initialize() --------------


    
  // ----------------------------------------
    // Global variables
    // ----------------------------------------
    private  static PrintWriter out = null;
    private ProfilePerformanceMeter performanceMeter = null;
    private static HashMap<String, int[]> sectionNames = null;
    
    
  
  
  

} // end Class toCommonModel
