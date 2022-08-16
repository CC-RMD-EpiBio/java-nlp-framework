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
//=================================================
/**
 * ActivityAnatomyModifierPolarity creates a table of 
 *    activity|anatomy
 *    activity|modifier
 *    
 *     modifier|polarity (gold/not gold)
 *     
 *     The last will be a lookup to determine what the coverage 
 *     of pathologic activities are via - if I find an activity
 *     that has been modified, and the modifier is not negative,
 *     it's not a symptom.
 *
 *  
 * @author  Guy Divita 
 * @created May 21, 2015
 *
 * 

 */
// ================================================

package gov.nih.cc.rmd.nlp.framework.annotator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.Utterance;
import gov.va.vinci.model.Activity;
import gov.va.vinci.model.Gold;
import gov.va.vinci.model.Modifier;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class ActivityAnatomyModiferPolarity extends JCasAnnotator_ImplBase {

 
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
  
      List<Annotation>    utterances     = UIMAUtil.getAnnotations(pJCas, Utterance.typeIndexID);
 
      if ( utterances != null ) {
       for ( Annotation sentence : utterances ) {
         

         List<Annotation>    golds          = UIMAUtil.getAnnotationsBySpan(pJCas, Gold.typeIndexID,      sentence.getBegin(), sentence.getEnd());
         List<Annotation>    activities     = UIMAUtil.getAnnotationsBySpan(pJCas, Activity.typeIndexID,  sentence.getBegin(), sentence.getEnd());
         List<Annotation>    modifiers      = UIMAUtil.getAnnotationsBySpan(pJCas, Modifier.typeIndexID,  sentence.getBegin(), sentence.getEnd());
          
         if ( activities != null && activities.size() > 0 ) {
           String activity = listAnnotations( activities );
           if ( modifiers != null && modifiers.size() > 0 )    {
             String modifier = listAnnotations( modifiers );
             String polarity = "negative";
             if ( golds != null && golds.size() > 0 )            {
               polarity = "positive";
               
             } // end of if golds
             this.table1.add( activity + "|" + modifier + "|" + polarity);
             this.table2.add( modifier + "|" + polarity);
           } // end of if modifiers
         } // end of if activities
         
       } // end loop thru Utterances
      } // end if there are utterances
         
         

    } // =======================================================
    /**
     * listAnnotations returns a concatinated set of surface forms for the annotations 
     * 
     * @param pAnnotations
     * @return String (colon delimited)
     */
    // =======================================================
    private String listAnnotations(List<Annotation> pAnnotations) {
      
      StringBuffer buff = new StringBuffer();
      String returnVal = null;
      if ( pAnnotations != null && pAnnotations.size() > 0 ) { 
        for ( Annotation annotation : pAnnotations ) {
          buff.append(U.display(annotation.getCoveredText()) + ":" );
        }
        returnVal = buff.toString().substring(0, buff.length() -1 );
      }
      return returnVal;
      // End Method listAnnotations() ======================
    }



    // end Method process
    
    
    
    // =======================================================
    /**
     * destroy will write out the contexts - 
     *     Before that happens, 
     *        it will create vectors of words before, words, words after
     * 
     */
    // =======================================================
    public void destroy() {
    
      // -------------------------
      // Create the output file to put the contexts into
      PrintWriter outTable1 = null;
      PrintWriter outTable2 = null;
     
      String outputFile1 = this.outputDir + "/stats/" + "table1.txt";
      String outputFile2 = this.outputDir + "/stats/" + "table2.txt";
      
     
      try {
        outTable1 = new PrintWriter( outputFile1);
        outTable2 = new PrintWriter( outputFile2);
       
        for ( String row : this.table1) outTable1.print(row + "\n");
        for ( String row : this.table2) outTable2.print(row + "\n");
        
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        String msg = "Issue with trying  to open the output file " + e.getMessage() ;
        System.err.println( msg );
        throw new RuntimeException (msg);
      }
      
     
       
      outTable1.close();
      outTable2.close();
      
      
    
      
    } // End Method destroy() ======================




    //----------------------------------
    /**
     * initialize loads the machine learned component
     * 
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
      
     
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  
        
        
  
      } catch (Exception e ) {
        System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
        throw new ResourceInitializationException();
      }
      
  
      this.outputDir      = U.getOption(args, "--outputDir=", "./"); 
       try {
    	   U.mkDir(this.outputDir + "/stats");
       } catch (Exception e) {
    	   e.printStackTrace();
    	   System.err.println("Issue trying to make the stats dir " + e.toString());
       }
      this.table1 = new ArrayList<String>();
      this.table2 = new ArrayList<String>();
      
    
    }
   
   
  
    // ----------------------------------------
    // Global variables
    // ----------------------------------------

    private String outputDir = null;
    private ArrayList<String> table1 = null;
    private ArrayList<String> table2 = null;
    
    

    
  

} // end Class ToGate
