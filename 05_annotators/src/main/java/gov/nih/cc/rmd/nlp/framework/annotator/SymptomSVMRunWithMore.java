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
//=================================================
/**
 * SymptomSVM Run With More creates arff files for svm and runs it thru
 * the trained model(s).
 *  Features include 
 *  bag of words around the symptom words used (words chosen outside this annotator)
 *  symptom words
 *  symptom asertion status
 *  indentation
 *  
 *  answer - if this term is a reference symptom or not
 *  
 * @author  Guy Divita 
 * @created May 21, 2014
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
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.FalseNegative;
import gov.va.chir.model.FalsePositive;
import gov.va.chir.model.Section;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.TrueNegative;
import gov.va.chir.model.TruePositive;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.CEM;
import gov.va.vinci.model.Concept;
import gov.va.vinci.model.Gold;
import gov.va.vinci.model.Symptom;
import gov.va.vinci.model.Symptom_Potential;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class SymptomSVMRunWithMore extends SymptomSVMTrainingWithMore {

 
  
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
      
      // ----------------------------
      // gather instances to classify
      // ----------------------------
      // if ( this.numberProcessed % 1 == 0 ) {
      //   String documentId = VUIMAUtil.getDocumentId(pJCas);
      //    System.err.println( this.processMeter.mark(this.numberProcessed + "| processing |" + documentId));
      //    
      //  }
      this.numberProcessed++;
      
      List<Annotation> potentialSymptoms = UIMAUtil.getAnnotations(pJCas, Symptom_Potential.typeIndexID);
      List<Annotation> sentences         = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID);
      List<Annotation> slotValues        = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID);
      List<Annotation>  sections         = UIMAUtil.getAnnotations(pJCas, Section.typeIndexID);
      List<Annotation> cemSections       = UIMAUtil.getAnnotations(pJCas, CEM.typeIndexID);
      List<Annotation>     golds         = UIMAUtil.getAnnotations(pJCas, Gold.typeIndexID);
      
      if ( golds != null && golds.size() > 0 ) {
        UIMAUtil.sortByOffset(golds);
        for ( Annotation g : golds)  ((Concept)g).setMarked(false);
      }  else
      return;
      
      String documentId = VUIMAUtil.getDocumentId(pJCas);
      
      if ( slotValues != null && slotValues.size() > 0) {
        if ( sentences != null )
          sentences.addAll( slotValues);
        else 
          sentences = slotValues;
      }
      if ( sentences != null && sentences.size() > 0)
        UIMAUtil.sortByOffset(sentences);
      
      int[]             endOfLineOffsets = UIMAUtil.getLineOffsets(pJCas);
      int[]                beginsAndEnds = new int[2];
      int                     windowSize = 5;
      String                      answer = "unknown";
     
     
      if ( potentialSymptoms != null && potentialSymptoms.size() > 0 ) {
        
        UIMAUtil.sortByOffset(potentialSymptoms);
        for ( Annotation potentialSymptom : potentialSymptoms ) {
          String negationStatus = ((Symptom_Potential) potentialSymptom).getAssertionStatus();
          String conditional     = String.valueOf(((Symptom_Potential) potentialSymptom).getConditionalStatus());
          String experiencer      = ((Symptom_Potential) potentialSymptom).getSubjectStatus();
          String symptomName = U.display(potentialSymptom.getCoveredText());
          String kindOfSlotValue = getSurroundingUtterance(pJCas, potentialSymptom);
          if (negationStatus == null || negationStatus.equals("Asserted")   ) {
            if ( !((Symptom_Potential) potentialSymptom).getConditionalStatus() ) {     
              if ( experiencer == null || experiencer.equalsIgnoreCase("patient")  ) {
                
               
                SVMContext svmContext = createContext( pJCas, 
                                                       potentialSymptom, 
                                                       answer, 
                                                       sentences, 
                                                       sections, 
                                                       cemSections, 
                                                       endOfLineOffsets, 
                                                       beginsAndEnds, 
                                                       windowSize,
                                                       documentId,
                                                       conditional);
         
                try {
                 
     //             answer = this.model.classify( svmContext, documentId, symptomName, potentialSymptom.getBegin(), potentialSymptom.getEnd());
                  svmContext.setAnswer(answer);
               
                  this.contexts.add( svmContext);
                  System.err.println("The answer = " + answer );
                } catch (Exception e) {
                  e.printStackTrace();
                  System.err.println("issue with classifying " + potentialSymptom.getCoveredText()  + " " + e.toString());
           
                }
          
                // -----------------------------
                // create tp,fp, fn, tn to be tallied at the end
                String sectionName = getSectionName( pJCas,  potentialSymptom, sections ) ;
                evalutate(pJCas, potentialSymptom, answer,  golds, documentId, sectionName, kindOfSlotValue );
          
                if ( answer != null && answer.toLowerCase().contains("true")) {
                  createFinalSymptom(pJCas, potentialSymptom );
                }
              } // end if this is the patient
            } // end if this is conditional
          } // end if the potential symptom is not negated
         
        } // end loop through list of annotations
      } // end if there are any annotations
      
      
      // -------------------------
      // Go thru the golds to see if there are any that are not marked
      // Those that are not marked are false negatives
      if ( golds != null && golds.size() > 0 ) 
        for ( Annotation g : golds ) 
          if ( !((Concept)g).getMarked() ) {
           
            try {
              String symptomName = U.display(g.getCoveredText());
              SVMContext svmContext = createContext( pJCas, g, answer, sentences, sections, cemSections, endOfLineOffsets, beginsAndEnds, windowSize,documentId, "false");
        //      answer = this.model.classify( svmContext, documentId, symptomName, g.getBegin(), g.getEnd());
              svmContext.setAnswer(answer);
              this.contexts.add(svmContext);
              System.err.println("The answer = " + answer );
            } catch (Exception e) {
              e.printStackTrace();
              System.err.println("issue with classifying the fn " + g.getCoveredText()  + " " + e.toString());
             
            }
            String sectionName = getSectionName( pJCas,  g, sections ) ;
            String kindOfSlotValue = getSurroundingUtterance(pJCas, g);
            createAnnotation(pJCas, "FN", g , documentId, sectionName ,"unknown", kindOfSlotValue);
          }
      

    } // end Method process
    



    // =======================================================
    /**
     * getSurroundingUtterance returns the kind of slot value|check box|section heading|question this potential
     * symptom is in
     * 
     * @param pJCas
     * @param potentialSymptom
     * @return
     */
    // =======================================================
    private String getSurroundingUtterance(JCas pJCas, Annotation potentialSymptom) {
      String returnVal = "none";
      
      List<Annotation> slotValues = UIMAUtil.getEnclosingAnnotation(pJCas, potentialSymptom, SlotValue.typeIndexID);
      if ( slotValues != null ) {
        for (Annotation slot: slotValues) {
          returnVal =  ((SlotValue) slot).getId();
        }
      } else {
        List<Annotation> sectionHeadings = UIMAUtil.getEnclosingAnnotation(pJCas, potentialSymptom, ContentHeading.typeIndexID);
        if ( sectionHeadings != null && sectionHeadings.size() > 0 ) 
          returnVal = "Section Heading";
      }
      
      
      return returnVal;
      // End Method getSurroundingUtterance() ======================
    }




    // =======================================================
    /**
     * evalutate creates tp, fp, tn, fn annotations to be tallied at the end
     * 
     * @param pJCas
     * @param potentialSymptom 
     * @param answer
     * @param golds
     * @param pDocumentId
     * @param pSectionName
     * @param kindOfSlotValue 
     
     */
    // =======================================================
    private void evalutate(JCas pJCas, Annotation potentialSymptom, String answer, List<Annotation> golds, String pDocumentId , String pSectionName, String kindOfSlotValue) {
     
      boolean in = inGold( pJCas, golds, potentialSymptom);
      String organSystem = ((Symptom_Potential) potentialSymptom ).getCategories();
      if ( answer.contains("true")) {
        if ( in )
          createAnnotation(pJCas ,"TP", potentialSymptom , pDocumentId, pSectionName, organSystem, kindOfSlotValue);
        else 
          createAnnotation(pJCas, "FP", potentialSymptom , pDocumentId, pSectionName, organSystem, kindOfSlotValue);
      
      } else {
        if ( in )
          createAnnotation(pJCas, "FN", potentialSymptom , pDocumentId, pSectionName, organSystem, kindOfSlotValue);
        else 
          createAnnotation(pJCas, "TN", potentialSymptom , pDocumentId, pSectionName, organSystem, kindOfSlotValue);
      }
      
    
      
      
    } // End Method evalutate() ======================
    


    // =======================================================
    /**
     * createAnnotation [Summary here]
     * 
     * @param pJCas
     * @param pAnnotationType
     * @param pAnnotation
     * @param svmContext
     * @param pSectionName
     * @param pOrganSystem 
     * @param kindOfSlotValue 
     */
    // =======================================================
    private void createAnnotation(JCas pJCas, String pAnnotationType, Annotation pAnnotation , String pDocumentId, String pSectionName, String pOrganSystem, String kindOfSlotValue) {
      
      Annotation statement = null;
      if      ( pAnnotationType.contains("TP"))  statement = new TruePositive(pJCas);
      else if ( pAnnotationType.contains("TN"))  statement = new TrueNegative(pJCas);
      else if ( pAnnotationType.contains("FP"))  statement = new FalsePositive(pJCas);
      else if ( pAnnotationType.contains("FN"))  statement = new FalseNegative(pJCas);
      else statement = new TruePositive(pJCas);
      
      statement.setBegin(        pAnnotation.getBegin());
      statement.setEnd(          pAnnotation.getEnd());
      statement.addToIndexes();
      
      
      String  leftContext = UIMAUtil.getLeftContext(pJCas, pAnnotation, this.contextSize);
      String rightContext = UIMAUtil.getRightContext(pJCas, pAnnotation, this.contextSize);
      String        begin = U.spacePadRight(5, String.valueOf(pAnnotation.getBegin()));
      String          end = U.spacePadRight(5, String.valueOf(pAnnotation.getEnd())); 
      String assertionStatus = " ";
      try {
        assertionStatus = ((Symptom_Potential)pAnnotation).getAssertionStatus();
      } catch (Exception e) {
        try {
          assertionStatus = ((VAnnotation)pAnnotation).getNegation_Status();
        } catch (Exception e2) {
          assertionStatus = " ";
        }
      };
      String marked = " ";  try {  marked = String.valueOf( ((Concept) pAnnotation).getMarked()); } catch (Exception e) {};
      String       focus = U.spacePadRight(30,  U.display(pAnnotation.getCoveredText()));
      String concordance = pAnnotationType + "|" + pDocumentId  + "|" + begin + "|" + end + "|" + leftContext + "|" + focus + "|" + rightContext + "|" + assertionStatus + "|" + marked + "|" + pSectionName + "|" + pOrganSystem + "|" + kindOfSlotValue ;
      
      if      ( pAnnotationType.contains("TP"))  this.tps.add( concordance); 
      else if ( pAnnotationType.contains("TN"))  this.tns.add( concordance);
      else if ( pAnnotationType.contains("FP"))  this.fps.add( concordance);
      else if ( pAnnotationType.contains("FN"))  this.fns.add( concordance);
      
      
      
    } // End Method createAnnotation() ======================
    




    // =======================================================
    /**
     * inGold returns true if the potentialSymptom is in the list of golds
     * 
     * This method has a side effect that it marks the golds it sees
     * 
     * @param pJCas
     * @param golds
     * @param potentialSymptom
     * @return boolean
     */
    // =======================================================
    private boolean inGold(JCas pJCas, List<Annotation> golds, Annotation potentialSymptom) {
      boolean returnVal = false;
      
       List<Annotation> overlappingMatches = UIMAUtil.getOverlapping(golds, potentialSymptom.getBegin(), potentialSymptom.getEnd());
     
       if ( overlappingMatches != null && overlappingMatches.size() > 0 ) {
       
         for ( Annotation g : overlappingMatches )
          ((Concept)g).setMarked(true);
         
         returnVal = true;
       }
         
       
       return returnVal;
    }  // End Method inGold() ======================
    




    // ----------------------------------------------
    /**
     * createFinalSymptom
     * 
     * @param pJCas
     * @param annotation
     */
    // ----------------------------------------------
    private Symptom createFinalSymptom(JCas pJCas, Annotation annotation) {
    
      Symptom statement = new Symptom(pJCas);
      statement.setSectionName(  ((Symptom_Potential)annotation).getSectionName() );
      statement.setBegin(        annotation.getBegin());
      statement.setEnd(          annotation.getEnd());
      statement.setCuis(         ((Symptom_Potential)annotation).getCuis());
        
      statement.setCategories(((Symptom_Potential)annotation).getCategories());
       
      statement.setConceptNames( ((Symptom_Potential)annotation).getConceptNames()); 
      statement.setId(U.getUniqId());
      statement.addToIndexes();
        
      return statement;     
      
    }  // end Method classifyAnnotation() ---------------------

 
    // =======================================================
    /**
     * destroy will write out the contexts - 
     *     Before that happens, 
     *        it will create vectors of words before, words, words after
     * 
     */
    // =======================================================
    @Override
    public void destroy() {
    
      try {
        this.processMeter.mark("in destroy");
      
       writeOutDebuggingARFF();
       this.processMeter.mark("Finished writing out the arff and debug file " );
       
      } catch (Exception e) {
       e.printStackTrace();
       System.err.println("Issue writing out the arff file for debugging purposes " + e.toString());
      }
       
      // --------------------------
      // Print out the evaluation 
      this.processMeter.mark("starting evaluation");
      evaluationSummary();
      this.processMeter.mark("Finished evaluation");
      
      this.processMeter.stop("finishied");
      
    } // End Method destroy() ======================
    
 // =======================================================
    /**
     * writeOutDebuggingARFF 
     * @throws Exception 
     * 
     */
    // =======================================================
    private void writeOutDebuggingARFF() throws Exception {
      
     // this.model.printARFFFile( this.svmOutputDir );
      
      String fileName = this.svmOutputDir + "/debugging_test.txt";
      PrintWriter out = new PrintWriter(fileName);
      
      SVMContexts svmModelSummary = new SVMContexts( this.contexts , this.minThreshold   );
      out.println(svmModelSummary.printDebuggingData());
      out.close();
      
      
    }  // End Method writeOutDebuggingARFF() ======================
    




    // -----------------------------------------
    /** 
     * destroy prints out the summary numbers and the
     * recall, precision, and f-metric
     *
     */
    // -----------------------------------------
   public void evaluationSummary() {

      // --------------------------------
      // write out the statistics
      // --------------------------------
      PrintWriter out = null;
      try {
         out = new PrintWriter( this.summaryFileName);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        System.err.println("Not able to open for writing the summaryFile " + this.summaryFileName);
      }
      System.err.println(" -----------------------------------------");
      System.err.println("  writing out the summary file " + this.summaryFileName);
      System.err.println(" ------------------------------------------");
      
     
      int total_TP = this.tps.size();
      int total_TN = this.tns.size();
      int total_FP = this.fps.size();
      int total_FN = this.fns.size();
      int totalInstances = total_TP + total_FP + total_TN + total_FN;
     
      double recall    = total_TP / (( total_TP + total_FN) + .00000001) ;
      double precision = total_TP / (( total_TP + total_FP)  + .00000001); 
      double accuracy  = (total_TP + total_TN)/ ((total_TP + total_TN + total_FP + total_FN) + .0000001); 
      double specificity = total_TN / ((total_TN + total_FP) + .0000001);
      double           f = 2 * ((precision*recall)/(precision+recall)); 
      
      if ( out == null ) return;
      
      out.print(" ---------------------------------------\n");
      out.print(" ---- Run on " + U.getDateStampSimple() + " ----\n");
      out.print(" ----    in " + this.summaryFileName + " ----\n");
      out.print(" ---------------------------------------\n");
      out.print(" ---------------------------------------\n");
      
      out.print(" The number of instances                              = " + U.spacePadRight(6, String.valueOf(totalInstances)) + "\n"); 
      out.print( "True  Positive (TP)                                  = " + U.spacePadRight(6, String.valueOf(total_TP))       + "\n");
      out.print( "True  Negative (TN)                                  = " + U.spacePadRight(6, String.valueOf(total_TN ))      + "\n");
      out.print( "False Positive (FP)                                  = " + U.spacePadRight(6, String.valueOf(total_FP))       + "\n");
      out.print( "False Negative (FN)                                  = " + U.spacePadRight(6, String.valueOf(total_FN))       + "\n");
      out.print( "Recall                                  (tp/(tp+fn)) = " + U.spacePadRight(6, String.valueOf(recall))        + "\n");
      out.print( "Sensitivity                             (tp/(tp+fn)) = " + U.spacePadRight(6, String.valueOf(recall))        + "\n");
      out.print( "Precision                               (tp/(tp+fp)) = " + U.spacePadRight(6, String.valueOf(precision ))     + "\n");
      out.print( "Positive Predictive value (PPV)         (tp/(tp+fp)) = " + U.spacePadRight(6, String.valueOf(precision ))     + "\n");
      out.print( "Accuracy                      ((tp+tn)/(tp+tn+fp+fn) = " + U.spacePadRight(6, String.valueOf(accuracy ))      + "\n");
      out.print( "Specificity                           (tn/(tn + fp)  = " + U.spacePadRight(6, String.valueOf(specificity))    + "\n");
      out.print( "F-Measure (2* (precision*recall)/(precision+recall)) = " + U.spacePadRight(6, String.valueOf(f))              + "\n");
      
      
     
      out.print("  +-----------------------+\n");
      out.print("  |Confusion Matrix       |\n");
      out.print("  +-----------+-----------+\n");
      out.print("  |  TP       |  FN       |\n");
      out.print("  |           |           |\n");
      out.print("  | " + String.format("%8d", total_TP) + "  | " +
                          String.format("%8d", total_FN) + "  |\n");
      out.print("  +-----------+-----------+\n");
      out.print("  |  FP       |  TN       |\n");
      out.print("  | " + String.format("%8d", total_FP) + "  | " + 
                           String.format("%8d", total_TN) + "  |\n");
      out.print("  +-----------+-----------+\n");
      
      out.print("\n\n\n\n"); 
    
      out.print(" ----------------------------------\n");
      out.print(" False Negatives                   \n");
      out.print(" ----------------------------------\n");
      for ( String line : this.fns)
        out.print( line + "\n");
      out.print(" ----------------------------------\n");
      
      out.print("\n\n\n\n"); 
      
      out.print(" ----------------------------------\n");
      out.print(" False Positives                   \n");
      out.print(" ----------------------------------\n");
      for ( String line : this.fps)
        out.print( line + "\n");
      out.print(" ----------------------------------\n");
      
   out.print("\n\n\n\n"); 
      
      out.print(" ----------------------------------\n");
      out.print(" True Positives                    \n");
      out.print(" ----------------------------------\n");
      for ( String line : this.tps)
        out.print( line + "\n");
      out.print(" ----------------------------------\n");
      
      out.close();

    
    } // end Method evaluationSummary() -----------------

    //----------------------------------
    /**
     * initialize loads the machine learned component
     * 
     *
     * @param aContext
     * 
     **/
    // ----------------------------------
    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
      
     
      String[] args = null;
      this.numberProcessed = 0;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args"); 
       
  
      } catch (Exception e ) {
        System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
        throw new ResourceInitializationException();
      }
      
      try {
        String  localResources = U.getOption(args,  "--localResources=", "./bla");
        String  modelDirectory = U.getClassPathToLocalResources() + "/symptoms";
       
        if ( !localResources.equals("./bla"))
          modelDirectory = localResources + "/symptoms";
    
      String           model = U.getOption(args, "--model=", modelDirectory + "/" + "svmModelC10_4folds_linearModel.model"); 
      String      arffHeader = U.getOption(args, "--arffHeader=", modelDirectory + "/" + "arffHeader.arff"); 
     
      this.outputDir       = U.getOption(args, "--outputDir=" ,"./eval");
      this.svmOutputDir    = this.outputDir + "/svm";
        
          
      
      initializeModel( arffHeader, model, this.outputDir);
      
      this.tps = new ArrayList<String>();
      this.tns = new ArrayList<String>();
      this.fps = new ArrayList<String>();
      this.fns = new ArrayList<String>();
      

     
      U.mkDir(this.outputDir + "/eval");
      U.mkDir(this.svmOutputDir);
     
      this.summaryFileName = this.outputDir + "/eval/eval.txt"; 
      
      this.contextSize     = Integer.parseInt(U.getOption(args, "--contextSize=", "50"));
      
      String symptomAnnotatorLog = this.outputDir + "/logs/symptomAnnotator.log";
      this.processMeter = new PerformanceMeter(symptomAnnotatorLog);
      
      this.contexts = new ArrayList<SVMContext>();
      
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue intializing the symptomsvm run with more " + e.toString());
        throw new ResourceInitializationException();
      }
    } // End Method initialize() ---------
   
    
    //----------------------------------
      /**
       * initialize loads the machine learned component
       * 
       * @param pArffHeader
       * @param pModel
       * @throws Exception 
       * 
       **/
      // ----------------------------------
      public void initializeModel(  String pArffHeader, String pModel, String pOutputDir ) throws Exception {
     
        
     //   this.model = new V3NLP_WekaClassifier( pArffHeader, pModel, pOutputDir );
      
     
        
    } // end Method initialize() --------------
  
   
  
    /**
       * @return the svmOutputDir
       */
      public String getSvmOutputDir() {
        return svmOutputDir;
      }


     


      // ----------------------------------------
      // Global variables
      // ----------------------------------------
     // private SVMContexts contexts = null;
     private String svmOutputDir = null;
 //    private V3NLP_WekaClassifier model = null;
     private List<String>  tps = null;
     private List<String>  tns = null;
     private List<String>  fps = null;
     private List<String>  fns = null;
     private String      outputDir = null;
     private String     summaryFileName = null;
     private int          contextSize = 30;
     PerformanceMeter processMeter = null;
     private int numberProcessed = 0;
     

} // end Class ToGate
