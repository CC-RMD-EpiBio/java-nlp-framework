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
