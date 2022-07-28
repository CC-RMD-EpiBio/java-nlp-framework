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
 * SymptomSVM Training creates arff files for svm
 * training.  Features include 
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

import java.io.File;
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

import gov.va.chir.model.DependentContent;
import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.PartOfSpeech;
import gov.va.chir.model.Section;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.Token;
import gov.va.chir.model.Utterance;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.Activity;
import gov.va.vinci.model.CEM;
import gov.va.vinci.model.Concept;
import gov.va.vinci.model.Gold;
import gov.va.vinci.model.Modifier;
import gov.va.vinci.model.Symptom_Potential;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class SymptomSVMTrainingWithMore extends JCasAnnotator_ImplBase {

 
 
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
      
      try {
      // ----------------------------
      // gather instances to classify
      // ----------------------------
      List<Annotation> potentialSymptoms = UIMAUtil.getAnnotations(pJCas, Symptom_Potential.typeIndexID);
      List<Annotation> sentences         = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID);
      List<Annotation> slotValues        = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID);
      List<Annotation>  sections         = UIMAUtil.getAnnotations(pJCas, Section.typeIndexID);
      List<Annotation>    golds          = UIMAUtil.getAnnotations(pJCas, Gold.typeIndexID);
      List<Annotation>  cemSections      = UIMAUtil.getAnnotations(pJCas, CEM.typeIndexID);
      int[] lineOffsets = UIMAUtil.getLineOffsets(pJCas);
      String documentId = VUIMAUtil.getDocumentId(pJCas);
      documentId = makeIntStringOfIdentifer( documentId);
      
      System.err.println( "Processing [" + this.ctr++ + "]" + documentId );
      
      
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
      String                      answer = "";
      
      if ( golds != null && golds.size() > 0 ) {
        UIMAUtil.sortByOffset(golds);
        for ( Annotation g : golds ) ((Concept) g).setMarked(false);
       
      } else {
        System.err.println(" dropping this for training purposes, it has no gold annotations " + documentId);
        return;        // skip those files that have no annotations in them for trainin purposes
      }
        
       
      if ( potentialSymptoms != null ) {
        UIMAUtil.sortByOffset(potentialSymptoms);
        for ( Annotation potentialSymptom : potentialSymptoms ) {
          
          String negationStatus   = ((Symptom_Potential) potentialSymptom).getAssertionStatus() ;
          boolean conditional     = ((Symptom_Potential) potentialSymptom).getConditionalStatus();
          String experiencer      = ((Symptom_Potential) potentialSymptom).getSubjectStatus();
 
          if (negationStatus == null || negationStatus.equals("Asserted")   ) {
          //  if ( !conditional ) {     
              if ( experiencer == null || experiencer.equalsIgnoreCase("patient")  ) {
            
                answer = String.valueOf(isReferenceSymtpom( pJCas, golds, potentialSymptom));  // assumes that reference symptoms are in cas
               
               
                
                categorizeSymptom( (Symptom_Potential) potentialSymptom, Boolean.valueOf(answer));
                categorizeSymptomLocation(pJCas, potentialSymptom, Boolean.valueOf(answer) );
                String       isHeading = isHeading(pJCas, potentialSymptom );                         if (       isHeading == null) isHeading = "false";
                String overlappingTerm = getLexicalElementAndNegationStatus(pJCas, potentialSymptom); if ( overlappingTerm == null) overlappingTerm = "false";
                String      activities = getActivities(pJCas,potentialSymptom);                       if (      activities == null) activities = "false";
                String       modifiers = getModifiers(pJCas,potentialSymptom);                        if (       modifiers == null) modifiers = "false";
                this.actvityModifiterTable.add(activities + "," + modifiers + "," + answer);
                
                String clues = isHeading + "|" + overlappingTerm  + "|" + activities + "|" + modifiers  + "|" + conditional + "|" + answer  ;
                System.err.println("Would get this one |" + documentId + "|" + termize(potentialSymptom.getCoveredText()  + "|" + potentialSymptom.getBegin() + "|" + potentialSymptom.getEnd() + "|why|" + clues ));
                
                
                SVMContext svmContext = createContext( pJCas, potentialSymptom, answer, sentences, sections, cemSections, endOfLineOffsets, beginsAndEnds, windowSize ,documentId, String.valueOf(conditional) );
     	          
                // ---------------------------------------
                // Sample the negative examples - don't use all of them for training - keep the
                // ratio of positive to negative balanced - is what called for
                // The effect of the sampling below is semi-random - the first negative example after
                // the positive example is kept.
                // 
                //  THIS did not work - f = .2 when the arff file f was at .97
              
               
              
                this.contexts.add( svmContext );
              
                } else {
                potentialSymptom.removeFromIndexes();
              }
           // }
          }
        } // end loop through list of annotations
      

      } // end if there are any annotations 
      
      // -------------------------
      // Add the false negatives that would otherwise be missed
      if ( golds != null )
        for ( Annotation g : golds ) {
          if ( !((Concept)g).getMarked()) {         
            String       isHeading = isHeading(pJCas, g );                         if (       isHeading == null) isHeading = "false";
            String overlappingTerm = getLexicalElementAndNegationStatus(pJCas, g); if ( overlappingTerm == null) overlappingTerm = "false";
            String      activities = getActivities(pJCas,g);                       if (      activities == null) activities = "false";
            String       modifiers = getModifiers(pJCas,g);                        if (       modifiers == null) modifiers = "false";
            String     conditional = getConditional( pJCas,g);
            String  potentialSymptomFound = findPotential_Sysmptom( pJCas,g);
            Symptom_Potential foundSymptom = findPotential_Sysmptom_aux( pJCas, g);
           
            String clues = isHeading + "|" + overlappingTerm  + "|" + activities + "|" + modifiers + "|" +  "|" + conditional + "|" + "true"   ;
            
            System.err.println("Would miss this one |" + documentId + "|" + termize(g.getCoveredText()  + "|" + g.getBegin() + "|" + g.getEnd() + "|why|" + clues ));
            
            this.actvityModifiterTable.add(activities + "|" + modifiers + "|" + "true");
            
            categorizeSymptom( foundSymptom, true);
            categorizeSymptomLocation(pJCas, foundSymptom, true);
          
            
            SVMContext svmContext = createContext( pJCas, g, "true", sentences, sections, cemSections, endOfLineOffsets, beginsAndEnds, windowSize , documentId, conditional + "|" + potentialSymptomFound );
        
            this.contexts .add( svmContext );
           
            
            
          }
        }
      
      } catch (Exception e) {
        e.printStackTrace();
        String msg = "Issue with SymptomSVMTrainingWithMore " + e.toString() + "\n";
        System.err.print(msg);
      }

    } // end Method process
    

    protected String makeIntStringOfIdentifer(String pId) {
		StringBuffer buff = new StringBuffer();
		
		char[] c_s = pId.toCharArray();
    	for ( char c : c_s )
    		if ( U.isNumber(c))
    			buff.append(c);
		
		
    	return buff.toString().trim();
	}


	// =======================================================
    /**
     * categorizeSymptomLocation 
     * 
     * @param pJCas
     * @param potentialSymptom
     * @param pAnswer
     */
    // =======================================================
    private void categorizeSymptomLocation(JCas pJCas, Annotation potentialSymptom, boolean pAnswer) {
    
      
      if      ( isInQuestion(pJCas, potentialSymptom) ) { if ( pAnswer )  this.location[question]++;   else this.pLocation[question]++; }
      else if ( isInAnswer(pJCas, potentialSymptom))    { if ( pAnswer )  this.location[answer]++;     else this.pLocation[answer]++;   }
      else if ( isInSlot(pJCas, potentialSymptom))      { if ( pAnswer )  this.location [slot]++;      else this.pLocation[slot]++;     }
      else if ( isInValue(pJCas, potentialSymptom))     { if ( pAnswer )  this.location[value]++;      else this.pLocation[value]++;    }
      else if ( isHeadingAux(pJCas, potentialSymptom))  { if ( pAnswer )  this.location[heading]++;    else this.pLocation[heading]++;  }
      else if ( isInList(pJCas, potentialSymptom))      { if ( pAnswer )  this.location[listElement]++;else this.pLocation[listElement]++;}
      else if ( isInSentence(pJCas, potentialSymptom))  { if ( pAnswer )  this.location[sentence]++;   else this.pLocation[sentence]++; }
      else                                              { if ( pAnswer )  this.location[other]++;      else this.pLocation[other]++;    }
      
      
    } // End Method categorizeSymptomLocation() ======================
    


    // =======================================================
    /**
     * categorizeSymptom takes the first symtpom, not all of them.
     * 
     * @param foundSymptom
     * @param pAnswer
     */
    // =======================================================
    private void categorizeSymptom(Symptom_Potential foundSymptom, boolean pAnswer) {
     
      if ( foundSymptom != null  && pAnswer ) {
        //retrieve the symptom categories from the categories field
        String categories = foundSymptom.getCategories();
        if ( categories != null ) {
          
         String buff[] =  U.split(categories, ":");
        
        
         if ( buff != null && buff.length > 0 ) {
           String cat = buff[0];
             if ( cat != null && cat.trim().length() > 0 ) {
               
               if      ( cat.equals("SignOrSymptom") )   this.symptomCategories[0]++;
               else if ( cat.equals("Cardiovascular")  ) this.symptomCategories[1]++;
               else if ( cat.equals("Digestive")  )      this.symptomCategories[2]++;
               else if ( cat.equals("Endocrine")  )      this.symptomCategories[3]++;
               else if ( cat.equals("Genitourinary")  )  this.symptomCategories[4]++;
               else if ( cat.equals("Immune")  )         this.symptomCategories[5]++;
               else if ( cat.equals("Integumentary")  )  this.symptomCategories[6]++;
               else if ( cat.equals("Lymphatic")  )      this.symptomCategories[7]++;
               else if ( cat.equals("MentalHealth")  )   this.symptomCategories[8]++;
               else if ( cat.equals("Musculoskeletal")  )this.symptomCategories[9]++;
               else if ( cat.equals("Nervous")  )        this.symptomCategories[10]++;
               else if ( cat.equals("Reproductive")  )   this.symptomCategories[11]++;
               else if ( cat.equals("Respiratory")  )    this.symptomCategories[12]++;
               else if ( cat.equals("Urinary")  )        this.symptomCategories[13]++;
               else if ( cat.equals("GeneralSymptom")  ) this.symptomCategories[14]++;
               else {                                     this.symptomCategories[14]++;
               System.err.println("Something else " + cat);
               }
             } else {
               this.symptomCategories[14]++;
               System.err.println("Something else " + cat);
            }
             
           } else {
             this.symptomCategories[14]++;
         }
          
        } else {
          this.symptomCategories[14]++;
        }
          
        
        
        
      } // end if there is a found symptom
      else {
        if ( pAnswer ) {
          this.symptomCategories[14]++;
         
        }
      }
      
      
      
    } // End Method categorizeSymptom() ======================
    




    // =======================================================
    /**
     * getConditional returns a string true/false of the lexical element that overlaps this annotation
     * 
     * @param pJCas
     * @param pAnnotatoin
     * @return String
     */
    // =======================================================
    private String getConditional(JCas pJCas, Annotation pAnnotation ) {
    
      String returnValue = "false";
      List<Annotation> terms = UIMAUtil.getEnclosingAnnotation(pJCas, pAnnotation, LexicalElement.typeIndexID);
      
      if ( terms != null && terms.size() > 0)
        for ( Annotation term: terms) {
          returnValue = String.valueOf( ((LexicalElement)term).getConditional());
        }
      
      return returnValue;
    } // End Method getConditional() ======================
    


    // =======================================================
    /**
     * findPotential_Sysmptom returns a string (true/false) if an overlapping potential symptom was found
     * 
     * @param pJCas
     * @param pAnnotaton
     * @return String (true/false)
     */
    // =======================================================
    private String findPotential_Sysmptom(JCas pJCas, Annotation pAnnotation) {
     
      String returnValue = "false";
      List<Annotation> symptom = UIMAUtil.getEnclosingAnnotation(pJCas, pAnnotation, Symptom_Potential.typeIndexID);
      
      if ( symptom != null && symptom.size() > 0)
        returnValue = "true";
      
      return returnValue;
      // End Method findPotential_Sysmptom() ======================
    }

    // =======================================================
    /**
     * findPotential_Sysmptom returns a string (true/false) if an overlapping potential symptom was found
     * 
     * @param pJCas
     * @param pAnnotaton
     * @return String (true/false)
     */
    // =======================================================
    private Symptom_Potential findPotential_Sysmptom_aux(JCas pJCas, Annotation pAnnotation) {
     
      Symptom_Potential returnValue = null;
      List<Annotation> symptoms = UIMAUtil.getEnclosingAnnotation(pJCas, pAnnotation, Symptom_Potential.typeIndexID);
      
      if ( symptoms != null && symptoms.size() > 0)
        returnValue = (Symptom_Potential) symptoms.get(0);
      
      return returnValue;
      // End Method findPotential_Sysmptom() ======================
    }

    // =======================================================
    /**
     * getActivities returns a list of activities for the utterance that the annotation is within
     * 
     * @param pJCas
     * @param pAnnotation
     * @return String
     */
    // =======================================================
    private String getActivities(JCas pJCas, Annotation pAnnotation) {
     
      String returnVal = "false";
      StringBuffer buff = new StringBuffer();
      
      List<Annotation> utterances = UIMAUtil.getEnclosingAnnotation(pJCas, pAnnotation, Utterance.typeIndexID);
      
      if ( utterances != null ) 
        for (Annotation utterance: utterances ) {
          List<Annotation> activities = UIMAUtil.getEnclosingAnnotation(pJCas, utterance, Activity.typeIndexID );
          
          if ( activities != null && activities.size() > 0 ) {
            for ( Annotation activity : activities )
              buff.append(termize(activity.getCoveredText())  + ":");
          }
        }
      if ( buff.length() > 0 )
        returnVal = buff.toString().substring(0, buff.length() -1); // <--- take off the last ":"
      
      return returnVal;
    } // End Method getActivities() ======================
    

    // =======================================================
    /**
     * getModifiers returns a list of modifiers for the utterance that the annotation is within
     * 
     * @param pJCas
     * @param pAnnotation
     * @return String
     */
    // =======================================================
    private String getModifiers(JCas pJCas, Annotation pAnnotation) {
     
      String returnVal = "false";
      StringBuffer buff = new StringBuffer();
      
      List<Annotation> utterances = UIMAUtil.getEnclosingAnnotation(pJCas, pAnnotation, Utterance.typeIndexID);
      
      if ( utterances != null ) 
        for (Annotation utterance: utterances ) {
          List<Annotation> modifiers = UIMAUtil.getEnclosingAnnotation(pJCas, utterance, Modifier.typeIndexID );
          
          if ( modifiers != null && modifiers.size() > 0 ) {
            for ( Annotation modifier : modifiers )
              buff.append(termize(modifier.getCoveredText())  + ":");
          }
        }
      if ( buff.length() > 0 )
        returnVal = buff.toString().substring(0, buff.length() -1); // <--- take off the last ":"
      
      return returnVal;
    } // End Method getModifiers() ======================
    




    // =======================================================
    /**
     * getLexicalElementAndNegationStatus returns term|negationStatus
     * 
     * @param pJCas
     * @param g
     * @return String
     */
    // =======================================================
    private String getLexicalElementAndNegationStatus(JCas pJCas, Annotation pAnnotation) {
      
      String returnVal = "false";
      List<Annotation> terms = UIMAUtil.getAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pAnnotation.getBegin(), pAnnotation.getEnd());
      
      if ( terms != null && terms.size() > 0)
        for ( Annotation term : terms ) {
          returnVal = termize( term.getCoveredText() + "|" + ((LexicalElement) term).getNegation_Status());
        }
        
      
      return returnVal;
      // End Method getLexicalElementAndNegationStatus() ======================
    }





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
      PrintWriter out = null;
      PrintWriter outDebugging = null;
      String dateStamp = U.getDateStampSimple();
      String outputFile = this.svmOutputDir + "/" + "symptomTrainingModel.arff";
      SVMContexts svmModelSummary = null;
      try {
        svmModelSummary = new SVMContexts( this.contexts , this.minThreshold   );
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue trying to create the summary contexts " + e.toString());
        throw new RuntimeException ();
      }
      try {
        out = new PrintWriter( outputFile);
        outDebugging = new PrintWriter( outputFile + ".txt");
       
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        String msg = "Issue with trying  to open the output file " + e.getMessage() ;
        System.err.println( msg );
        throw new RuntimeException (msg);
      }
      
      svmModelSummary.setHeaderAndComments("Symptom training feature set using a minimum threshold = " + minThreshold );
       
      svmModelSummary.setRelation("symptom");
      
      svmModelSummary.writeHeader( this.svmOutputDir );
      

      out.print( svmModelSummary.getHeaderCommentsAndRelation());
        
      // write out the arff header
      out.print( svmModelSummary.arffAttributes( ));
        
      out.print( svmModelSummary.printData(true));
      outDebugging.println(svmModelSummary.printDebuggingData());
       
      out.close();
        
      outDebugging.close();
      
      writeActivitiesModifierTable( );
      
      writeSymptomCategories();
      
      writeSymptomLocatoinCategories();
      
     // Now we can train directly from here 
      
      // --------------------------------
      System.err.println("Starting the training from the created arrf files ");
      
      String args[] = { 
          "--classifier="     + classifier,
          "--dateStamp="      + dateStamp,
          "--outputPreamble=" + this.outputDir,
          "--outputDir="      + this.outputDir,
          "--localResources=" + this.localResources,
          "--model="          + this.svmOutputDir + "/" + classifier + "." + "model",
          "--arffHeader="     + svmModelSummary.getArffHeader(),
          "--arffFile="       + outputFile,
          "--classifier="     + this.classifier  };
   //   V3NLP_WekaClassifier.main(args);
      
      
      
    } // End Method destroy() ======================



    // =======================================================
    /**
     * writeSymptomCategories 
     * 
     */
    // =======================================================
    private void writeSymptomCategories() {
      PrintWriter out;
      try {
        out = new PrintWriter( this.svmOutputDir + "/symptomClassificationTable.csv");
     
       
        out.print("Sign or Symptom ,"+ this.symptomCategories[0] + '\n' );
        out.print("Cardiovascular ," + this.symptomCategories[1]+ '\n' );
        out.print("Digestive ,"      + this.symptomCategories[2]+ '\n' );
        out.print("Endocrine ,"      + this.symptomCategories[3]+ '\n' );
        out.print("Genitourinary ,"  + this.symptomCategories[4]+ '\n' );
        out.print("Immune ,"         + this.symptomCategories[5]+ '\n' );
        out.print("Integumentary ,"  + this.symptomCategories[6]+ '\n' );
        out.print("Lymphatic ,"      + this.symptomCategories[7]+ '\n' );
        out.print("MentalHealth ,"   + this.symptomCategories[8]+ '\n' );
        out.print("Musculoskeletal ,"+ this.symptomCategories[9]+ '\n' );
        out.print("Nervous ,"        + this.symptomCategories[10]+ '\n' );
        out.print("Reproductive ,"   + this.symptomCategories[11]+ '\n' );
        out.print("Respiratory ,"    + this.symptomCategories[12]+ '\n' );
        out.print("Urinary ,"        + this.symptomCategories[13]+ '\n' );
        out.print("GeneralSymptom ," + this.symptomCategories[14]+ '\n' );
         
      
        out.close();
      
      } catch (FileNotFoundException e) {
        e.printStackTrace();
       System.err.println( "Issue with writing the symptom categories " + e.getMessage());
      }
      // End Method writeSymptomCategories() ======================
    }

    // =======================================================
    /**
     * writeSymptomLocatoinCategories 
     * 
     */
    // =======================================================
    private void writeSymptomLocatoinCategories() {
      PrintWriter out;
      try {
        out = new PrintWriter( this.svmOutputDir + "/symptomLocationClassificationTable.csv");
     
        
        out.print("True Symptoms locations \n");
        out.print("Question, " +  this.location[question]  + '\n' );
        out.print("Answer, "   + this.location[answer]     + '\n' );
        out.print("Slot, "     + this.location [slot]      + '\n' );
        out.print("Value, "    + this.location[value]      + '\n' );      
        out.print("Heading, "  + this.location[heading]    + '\n' );    
        out.print("List, "     + this.location[listElement]+ '\n' );
        out.print("Sentence, " + this.location[sentence]   + '\n' );  
        out.print("Other, "    + this.location[other]      + '\n' );      
        
        out.print("Potential Symptoms that are not True symptoms locations \n");
        out.print("Question, " + this.pLocation[question]  + '\n' );
        out.print("Answer, "   + this.pLocation[answer]     + '\n' );
        out.print("Slot, "     + this.pLocation [slot]      + '\n' );
        out.print("Value, "    + this.pLocation[value]      + '\n' );      
        out.print("Heading, "  + this.pLocation[heading]    + '\n' );    
        out.print("List, "     + this.pLocation[listElement]+ '\n' );
        out.print("Sentence, " + this.pLocation[sentence]   + '\n' );  
        out.print("Other, "    + this.pLocation[other]      + '\n' ); 
        
        out.close();
      
      } catch (FileNotFoundException e) {
        e.printStackTrace();
       System.err.println( "Issue with writing the symptom categories " + e.getMessage());
      }
      // End Method writeSymptomCategories() ======================
    }

    
    
    // =======================================================
    /**
     * writeActivitiesModifierTable 
     * @throws FileNotFoundException 
     * 
     */
    // =======================================================
    private void writeActivitiesModifierTable()  {
      
      PrintWriter out;
      try {
        out = new PrintWriter( this.svmOutputDir + "/activitiesModifierTable.csv");
     
        for ( String row : this.actvityModifiterTable )
          out.print(row + "\n");
      
        out.close();
      
      } catch (FileNotFoundException e) {
        e.printStackTrace();
       System.err.println( "Issue with writing the activities modifier " + e.getMessage());
      }
      
    } // End Method writeActivitiesModifierTable() ======================
    


    // =======================================================
    /**
     * createContext 
     * 
     * @param pJCas
     * @param potentialSymptom
     * @param sentences
     * @param sections 
     * @param cemSections
     * @param endOfLineOffsets
     * @param beginsAndEnds 
     * @param windowSize 
     * @param training 
     * @param pDocumentId
     * @param conditional 
     * @return SVMContext
     */
    // =======================================================
    public SVMContext createContext(JCas pJCas, 
                                     Annotation potentialSymptom, 
                                     String answer,
                                     List<Annotation> sentences, 
                                     List<Annotation> sections, 
                                     List<Annotation> cemSections,
                                     int[] endOfLineOffsets, 
                                     int[] beginsAndEnds, 
                                     int windowSize,
                                     String pDocumentId, 
                                     String conditional
                                     ) {
    
      SVMContext svmContext = null;
      
      String            symptomWords = arffNormalize(potentialSymptom.getCoveredText()); 
      
      String         assertionStatus = getAssertionStatus(pJCas, potentialSymptom);
      
      Annotation[] surroundingTokens =  getTokensAroundSymptomLines( pJCas,  potentialSymptom, endOfLineOffsets,  beginsAndEnds ) ;
      String[]             leftWords =  getLeftSideWords( pJCas, potentialSymptom, surroundingTokens,  windowSize, beginsAndEnds) ;
      String[]            rightWords =  getRightSideWords( pJCas, potentialSymptom, surroundingTokens,  windowSize, beginsAndEnds) ;
        
      String[]             leftPOSs =  getLeftSidePOSs( pJCas, potentialSymptom, surroundingTokens,  windowSize, beginsAndEnds) ;
      String[]            rightPOSs =  getRightSidePOSs( pJCas, potentialSymptom, surroundingTokens,  windowSize, beginsAndEnds) ;

      String               symptomPOS = arffNormalize( getSymptomPOS( pJCas, surroundingTokens, beginsAndEnds));
    
      String              activity = getFeature(pJCas, potentialSymptom, sentences, gov.va.vinci.model.Activity.typeIndexID);
      String              modifier = getFeature(pJCas, potentialSymptom, sentences, gov.va.vinci.model.Modifier.typeIndexID);
      String        anatomicalPart = getFeature(pJCas, potentialSymptom, sentences, gov.va.vinci.model.AnatomicalPart.typeIndexID);
      String              severity = getFeature(pJCas, potentialSymptom,sentences, gov.va.vinci.model.Severity.typeIndexID);
      String              duration = getFeature(pJCas, potentialSymptom, sentences,gov.va.vinci.model.Duration.typeIndexID);
      String           sectionName = getSectionName(pJCas, potentialSymptom, sections );
      String        cemSectionName = getCEMSectionName( pJCas, potentialSymptom, cemSections);
      String          documentType = getDocumentType( pJCas);
      
      
      int                      ctr = 0;
      boolean          indentation = isLineIndented(pJCas,endOfLineOffsets, potentialSymptom);
      boolean               inSlot = isInSlot(pJCas, potentialSymptom);
      boolean              inValue = isInValue(pJCas, potentialSymptom);
      String             inHeading = isHeading(pJCas, potentialSymptom);
      boolean               inList = isInList(pJCas, potentialSymptom);
      boolean             hasActivity = has(activity);
      boolean             hasModifier = has(modifier);
      boolean             hasAnatomicalPart = has( anatomicalPart);
      boolean             hasSeverity = has( severity);
      boolean             hasDuration = has( duration);
      boolean             hasSectionName = has( sectionName);
      boolean             hasCEMSectionName = has( cemSectionName);
      
      
      
      boolean isJustAfterSlotValue = isJustAfterSlotValue(pJCas,endOfLineOffsets, potentialSymptom);
      boolean           inSentence = isInSentence( pJCas,potentialSymptom);
      boolean       isAllUpperCase = U.allUpperCase(potentialSymptom.getCoveredText());
      boolean         isInitialCap = U.isInitialCap(potentialSymptom.getCoveredText());
      boolean       isAllLowerCase = U.isAllLowerCase(potentialSymptom.getCoveredText());
      
      String               categories = ((Concept)potentialSymptom).getCategories();
      if ( categories == null ) categories = "unknown";
      boolean categorySignOrSymptom   =  categories.contains("SignOrSymptom")     ? true: false;
      boolean categoryCardiovascular  =  categories.contains("Cardiovascular")    ? true: false;
      boolean categoryDigestive       =  categories.contains("Digestive")         ? true: false;
      boolean categoryEndocrine       =  categories.contains("Endocrine")         ? true: false;
      boolean categoryGeneralSymptom  =  categories.contains("GeneralSymptom")    ? true: false;
      boolean categoryGenitourinary   =  categories.contains("Genitourinary")     ? true: false;
      boolean categoryImmune          =  categories.contains("Immune")            ? true: false;
      boolean categoryIntegumentary   =  categories.contains("Integumentary")     ? true: false;
      boolean categoryLymphatic       =  categories.contains("Lymphatic")         ? true: false;
      boolean categoryMentalHealth    =  categories.contains("MentalHealth")      ? true: false;
      boolean categoryMusculoskeletal =  categories.contains("Musculoskeletal")   ? true: false;
      boolean categoryNervous         =  categories.contains("Nervous")           ? true: false;
      boolean categoryReproductive    =  categories.contains("Reproductive")      ? true: false;
      boolean categoryRespiratory     =  categories.contains("Respiratory")       ? true: false;
      boolean categoryUnirary         =  categories.contains("Urinary")           ? true: false;
      boolean categoryUnknown         =  categories.contains("unknown")           ? true: false;
  
      
      ArrayList<String> attributes = new ArrayList<String>();
      attributes.add(U.zeroPad(ctr++,2) + "_symptomWords"    + "|" + symptomWords);
      attributes.add(U.zeroPad(ctr++,2) + "_documentID"  + "|" + pDocumentId);
      attributes.add(U.zeroPad(ctr++,2) + "_contextID"  + "|" + CONTEXT_ID++);
      // attributes.add( "01_trueSymptomWord" + "|" + trueSymptomWord);
      attributes.add(U.zeroPad(ctr++,2) + "_assertionStatus"  + "|" + assertionStatus);
                                            
      
      attributes.add(U.zeroPad(ctr++,2) + "_wordToLeft_5"     + "|" + leftWords[4]);
      attributes.add(U.zeroPad(ctr++,2) + "_wordToLeft_4"     + "|" + leftWords[3]);
      attributes.add(U.zeroPad(ctr++,2) + "_wordToLeft_3"     + "|" + leftWords[2]);
      attributes.add(U.zeroPad(ctr++,2) + "_wordToLeft_2"     + "|" + leftWords[1]);
      attributes.add(U.zeroPad(ctr++,2) + "_wordToLeft_1"     + "|" + leftWords[0]);
    
      attributes.add(U.zeroPad(ctr++,2) + "_wordtoRight_1"     + "|" + rightWords[0]);
      attributes.add(U.zeroPad(ctr++,2) + "_wordtoRight_2"     + "|" + rightWords[1]);
      attributes.add(U.zeroPad(ctr++,2) + "_wordtoRight_3"     + "|" + rightWords[2]);
      attributes.add(U.zeroPad(ctr++,2) + "_wordtoRight_4"     + "|" + rightWords[3]);
      attributes.add(U.zeroPad(ctr++,2) + "_wordtoRight_5"     + "|" + rightWords[4]);
      
      attributes.add(U.zeroPad(ctr++,2) + "_posToLeft_5"     + "|" + leftPOSs[4]);
      attributes.add(U.zeroPad(ctr++,2) + "_posToLeft_4"     + "|" + leftPOSs[3]);
      attributes.add(U.zeroPad(ctr++,2) + "_posToLeft_3"     + "|" + leftPOSs[2]);
      attributes.add(U.zeroPad(ctr++,2) + "_posToLeft_2"     + "|" + leftPOSs[1]);
      attributes.add(U.zeroPad(ctr++,2) + "_posToLeft_1"     + "|" + leftPOSs[0]);
    
      attributes.add(U.zeroPad(ctr++,2) + "_posToRight_1"     + "|" + rightPOSs[0]);
      attributes.add(U.zeroPad(ctr++,2) + "_posToRight_2"     + "|" + rightPOSs[1]);
      attributes.add(U.zeroPad(ctr++,2) + "_posToRight_3"     + "|" + rightPOSs[2]);
      attributes.add(U.zeroPad(ctr++,2) + "_posToRight_4"     + "|" + rightPOSs[3]);
      attributes.add(U.zeroPad(ctr++,2) + "_posToRight_5"     + "|" + rightPOSs[4]);
      
      attributes.add(U.zeroPad(ctr++,2) + "_symptomPOS"     + "|" +   symptomPOS);
      
      
                                            
      attributes.add(U.zeroPad(ctr++,2) + "_indentation" + "|" + String.valueOf(indentation));
      attributes.add(U.zeroPad(ctr++,2) + "_inSlot" + "|" +  String.valueOf(inSlot));
      attributes.add(U.zeroPad(ctr++,2) + "_inValue" + "|" +  String.valueOf(inValue));
      attributes.add(U.zeroPad(ctr++,2) + "_inHeading" + "|" +  inHeading);
      attributes.add(U.zeroPad(ctr++,2) + "_inList" + "|" +  String.valueOf(inList));
      attributes.add(U.zeroPad(ctr++,2) + "_isJustAfterSlotValue" + "|" +  String.valueOf( isJustAfterSlotValue));
      attributes.add(U.zeroPad(ctr++,2) + "_inSentence" + "|" +  String.valueOf(inSentence));
      attributes.add(U.zeroPad(ctr++,2) + "_isAllUpperCase" + "|" +  String.valueOf(isAllUpperCase));
      attributes.add(U.zeroPad(ctr++,2) + "_isInitialCap" + "|" +  String.valueOf(isInitialCap));
      attributes.add(U.zeroPad(ctr++,2) + "_isAllLowerCase" + "|" +  String.valueOf(isAllLowerCase));
      attributes.add(U.zeroPad(ctr++,2) + "_conditional" + "|" +  conditional);
          
      attributes.add(U.zeroPad(ctr++,2) + "_activity" + "|" +  activity );
      attributes.add(U.zeroPad(ctr++,2) + "_modifier" + "|" + modifier );
      attributes.add(U.zeroPad(ctr++,2) + "_anatomicalPart" + "|" + anatomicalPart );
      attributes.add(U.zeroPad(ctr++,2) + "_severity" + "|" +  severity );
      attributes.add(U.zeroPad(ctr++,2) + "_duration" + "|" +  duration );
      attributes.add(U.zeroPad(ctr++,2) + "_sectionName" + "|"  + arffNormalize(sectionName) ); 
      attributes.add(U.zeroPad(ctr++,2) + "_cemSectionName" + "|"  + arffNormalize(cemSectionName) );
      
      attributes.add(U.zeroPad(ctr++,2) + "_hasActivity" + "|" +  hasActivity );
      attributes.add(U.zeroPad(ctr++,2) + "_hasModifier" + "|" + hasModifier );
      attributes.add(U.zeroPad(ctr++,2) + "_hasAnatomicalPart" + "|" + hasAnatomicalPart );
      attributes.add(U.zeroPad(ctr++,2) + "_hasSeverity" + "|" +  hasSeverity );
      attributes.add(U.zeroPad(ctr++,2) + "_hasDuration" + "|" +  hasDuration );
      attributes.add(U.zeroPad(ctr++,2) + "_hasSectionName" + "|" + hasSectionName ); 
      attributes.add(U.zeroPad(ctr++,2) + "_hasCEMSectionName" + "|" + hasCEMSectionName ); 
      
      attributes.add(U.zeroPad(ctr++,2) + "_documentType" + "|" + documentType );
      
   
      attributes.add(U.zeroPad(ctr++, 2) + "_category_SignOrSymptom"   +  "|" + categorySignOrSymptom);
      attributes.add(U.zeroPad(ctr++, 2) + "_category_Cardiovascular"  +  "|" + categoryCardiovascular);
      attributes.add(U.zeroPad(ctr++, 2) + "_category_Digestive"       +  "|" + categoryDigestive);
      attributes.add(U.zeroPad(ctr++, 2) + "_category_Endocrine"       +  "|" + categoryEndocrine );
      attributes.add(U.zeroPad(ctr++, 2) + "_category_GeneralSymptom"  +  "|" + categoryGeneralSymptom);
      attributes.add(U.zeroPad(ctr++, 2) + "_category_Genitourinary"   +  "|" + categoryGenitourinary);
      attributes.add(U.zeroPad(ctr++, 2) + "_category_Immune"          +  "|" + categoryImmune);
      attributes.add(U.zeroPad(ctr++, 2) + "_category_Integumentary"   +  "|" + categoryIntegumentary);
      attributes.add(U.zeroPad(ctr++, 2) + "_category_Lymphatic"       +  "|" + categoryLymphatic );
      attributes.add(U.zeroPad(ctr++, 2) + "_category_MentalHealth"    +  "|" + categoryMentalHealth);
      attributes.add(U.zeroPad(ctr++, 2) + "_category_Musculoskeletal" +  "|" + categoryMusculoskeletal);
      attributes.add(U.zeroPad(ctr++, 2) + "_category_Nervous"         +  "|" + categoryNervous);
      attributes.add(U.zeroPad(ctr++, 2) + "_category_Reproductive"    +  "|" +  categoryReproductive);
      attributes.add(U.zeroPad(ctr++, 2) + "_category_Respiratory"     +  "|" + categoryRespiratory);
      attributes.add(U.zeroPad(ctr++, 2) + "_category_Urinary"         +  "|" + categoryUnirary);
      attributes.add(U.zeroPad(ctr++, 2) + "_category_Unknown"         +  "|" + categoryUnknown);
        
        
      
     
      // for debugging info
      String offsets = potentialSymptom.getBegin() + "|" + potentialSymptom.getEnd();
      
      
      
       svmContext = new SVMContext( attributes, answer , pDocumentId, offsets, symptomWords);
     
      
      
      return svmContext;
    } // End Method createContext() ======================
    



    // =======================================================
    /**
     * termize transforms, normalizes a string to one unquoted token
     * useful for an enumeration value.
     * 
     * This lowercases the terms as well.
     * 
     * @param sectionName
     * @return
     */
    // =======================================================
    private static String termize(String pName) {
      
      String buff = pName;
      if ( buff != null ) {
             buff = buff.trim().toLowerCase();
             buff = buff.replace('[', ' ');
             buff = buff.replace(']', ' ');
             buff = buff.replace('(', ' ');
             buff = buff.replace(')', ' ');
             buff = buff.replace('{', ' ');
             buff = buff.replace('}', ' ');
            
             buff = buff.replace('\r', ' ');
             buff = buff.replace('\n', ' ');
             buff = buff.replace('.', ' ');
            
             
             buff = buff.replace('#', ' ');
             buff = buff.replace('%', ' ');
             buff = buff.replace('}', ' ');
             buff = buff.replace('{', ' ');
             buff = buff.replace('&', ' ');
             buff = buff.replace('\'', ' ');
             buff = buff.replace('\'', ' ');
             buff = buff.replace('!', ' ');
             buff = buff.replace('`', ' ');
             buff = buff.replace('@', ' ');
             buff = buff.replace('$', ' ');
             buff = buff.replace('&', ' ');
             buff = buff.replace('*', ' ');
             buff = buff.replace('|', ' ');
             buff = buff.replace(',', ' ');
             buff = buff.replace('\\', ' ');
             buff = buff.replace('/', ' ');
             
         
             buff = buff.replace(',',' ');
             buff = buff.replace('.', ' ');
             buff = buff.replace('\'', ' ');
             buff = buff.replace('`', ' ');
             buff = buff.replace('%', ' ');
            
             buff = buff.trim();
             buff = buff.replace(' ', '_');
      }
      if (buff == null || buff.length() == 0 ) 
             buff = "unknown";
     
      
      
      return buff;
    } // End Method termize() ======================
    



    // =======================================================
    /**
     * arffNormalize strips the string of non-printable characters and commas
     * 
     * @param pCoveredText
     * @return String
     */
    // =======================================================
    private static String arffNormalize(String pCoveredText) {
    
      String returnVal = null;
      
      if (pCoveredText != null && pCoveredText.toString().trim().length() > 0) {
       
        returnVal = pCoveredText.toLowerCase().trim();
        if ( U.containsNumber( returnVal) )
          returnVal = "number";
        
        else if ( U.isOnlyPunctuation(returnVal) )
            returnVal = "sym";
        else 
          returnVal = termize( returnVal);
      } else 
        returnVal = "unknown";
      
      return returnVal;
    } // End Method arffarffNormalize() ======================



    // =======================================================
    /**
     * has returns true if this string is not null and not "unknown"
     * 
     * @param pActivity
     * @return
     */
    // =======================================================
    private boolean has(String pActivity) {
      boolean returnVal = false;
      
      if ( pActivity != null  && !pActivity.equals("unknown"))
        returnVal = true;
      
      return returnVal;
    }  // End Method has() ======================
    



    // =======================================================
    /**
     * isReferenceSymtpom looks within the CAS for an overlapping human defined
     * reference symptom.  Any reference Symptom that overlaps in some way
     * this potential symptom will return a true.
     * 
     * @param pJCas
     * @param golds 
     * @param potentialSymptom
     * @return boolean
     */
    // =======================================================
    private boolean isReferenceSymtpom(JCas pJCas, List<Annotation> golds, Annotation potentialSymptom) {
      
      boolean returnVal = false;
      
      List<Annotation>overlappingGoldAnnotatations  = UIMAUtil.getOverlapping(golds, potentialSymptom.getBegin(), potentialSymptom.getEnd());
          
      if ( overlappingGoldAnnotatations != null && overlappingGoldAnnotatations.size() > 0 ) {
        returnVal = true;
      
        for ( Annotation g : overlappingGoldAnnotatations) ((Concept)g).setMarked(true);
      }
      return returnVal;
    }  // End Method isReferenceSymtpom() ======================
    



    // =======================================================
    /**
     * isInSentence looks within the cas to see if this potential symptom
     * fits within the bounds of a sentence.  (Some symptoms will be within
     * a slot value or content heading that are not within sentence boundaries)
     * 
     * @param pJCas
     * @param potentialSymptom
     * @return boolean
     */
    // =======================================================
    private boolean isInSentence(JCas pJCas, Annotation potentialSymptom) {
      
      boolean returnVal = false;
      
      if ( potentialSymptom !=  null ) {
        List<Annotation>  sentences = UIMAUtil.getEnclosingAnnotation(pJCas, potentialSymptom,  Sentence.typeIndexID );
      if ( sentences != null && sentences.size() > 0 ) 
        returnVal = true;
      }
    
      return returnVal;
    } // End Method isInSentence() ======================
    

    // =======================================================
    /**
     * isJustAfterSlotValue returns true if this symptom appears in
     * a line after a known slot value
     * 
     *     Get the line that the symptom is in
     *     Get the line before the symptom
     *     See if this line contains a slotValue
     * @param pJCas
     * @param potentialSymptom
     * @return boolean
     */
    // =======================================================
    private boolean isJustAfterSlotValue(JCas pJCas, int[] endOfLineOffsets, Annotation potentialSymptom) {
      
      boolean returnVal = false;
      

      
      int symptomLineNumber = UIMAUtil.getLineNumber(potentialSymptom, endOfLineOffsets);
      if ( symptomLineNumber > 1 && symptomLineNumber < endOfLineOffsets.length ) {
        
         List<Annotation >slotValues = UIMAUtil.getAnnotationsBySpan(pJCas, SlotValue.typeIndexID, endOfLineOffsets[symptomLineNumber -2], endOfLineOffsets[symptomLineNumber -1]);
         if ( slotValues != null && slotValues.size() > 0 )
            returnVal= true;
      }
      
      
      return returnVal;
    }  // End Method isJustAfterSlotValue() ======================
    



    // =======================================================
    /**
     * isInList returns true if this symptom is within a list structure
     * 
     * @param pJCas
     * @param potentialSymptom
     * @return boolean
     */
    // =======================================================
    private boolean isInList(JCas pJCas, Annotation potentialSymptom) {
   boolean returnVal = false;
      
   if ( potentialSymptom != null ) {
      List<Annotation>  listStructures = UIMAUtil.getEnclosingAnnotation(pJCas, potentialSymptom,  gov.va.chir.model.List.typeIndexID );
          
      if ( listStructures != null && listStructures.size() > 0 ) 
        returnVal = true;
   }
      
      return returnVal;
    } // End Method isInList() ======================
    



    // =======================================================
    /**
     * isHeading returns true if the symptom is within a heading
     * 
     * @param pJCas
     * @param potentialSymptom
     * @return String
     */
    // =======================================================
    private String isHeading(JCas pJCas, Annotation potentialSymptom) {
      
      String returnVal = "false";
      List<Annotation>  headings = UIMAUtil.getEnclosingAnnotation(pJCas, potentialSymptom,  gov.va.chir.model.ContentHeading.typeIndexID );
      
      if ( headings != null && headings.size() > 0 ) {
        if ( ! isInSlot( pJCas, potentialSymptom)) 
          returnVal = "true";
      }
       
      
      return returnVal;
    } // End Method isHeading() ======================
    
    

    // =======================================================
    /**
     * isHeading returns true if the symptom is within a heading
     * 
     * @param pJCas
     * @param potentialSymptom
     * @return String
     */
    // =======================================================
    private boolean isHeadingAux(JCas pJCas, Annotation potentialSymptom) {
      
      boolean returnVal = false;
      List<Annotation>  headings = UIMAUtil.getEnclosingAnnotation(pJCas, potentialSymptom,  gov.va.chir.model.ContentHeading.typeIndexID );
      
      if ( headings != null && headings.size() > 0 ) {
        if ( ! isInSlot( pJCas, potentialSymptom)) 
          returnVal = true;
      }
       
      
      return returnVal;
    } // End Method isHeading() ======================
    

    // =======================================================
    /**
     * isInQuestion returns true if the symptom is within a question
     * 
     * @param pJCas
     * @param potentialSymptom
     * @return String
     */
    // =======================================================
    private boolean isInQuestion(JCas pJCas, Annotation potentialSymptom) {
      
      boolean returnVal = false;
      List<Annotation>  headings = UIMAUtil.getEnclosingAnnotation(pJCas, potentialSymptom,  gov.va.chir.model.ContentHeading.typeIndexID );
      
      if ( headings != null && headings.size() > 0 ) {
        if ( isInSlot( pJCas, potentialSymptom)) { 
        
          Annotation aHeading = headings.get(0);
          if ( aHeading != null ) {
            String text = aHeading.getCoveredText();
            if (text != null && text.indexOf('?') > -1 )
              returnVal = true;
          }
        }
      }
    
      return returnVal;
    } // End Method isHeading() ======================
    

    // =======================================================
    /**
     * isInAnswer returns true if the symptom is within the answer to a question
     * 
     * @param pJCas
     * @param potentialSymptom
     * @return String
     */
    // =======================================================
    private boolean isInAnswer(JCas pJCas, Annotation potentialSymptom) {
      
      boolean returnVal = false;
      List<Annotation>  slotValues = UIMAUtil.getEnclosingAnnotation(pJCas, potentialSymptom,  SlotValue.typeIndexID );
      
     if ( slotValues != null && slotValues.size() > 0) {
       Annotation slotValue = slotValues.get(0);
       if ( slotValue != null ) {
         Annotation _slot = ((SlotValue)slotValue).getContentHeader() ;     
         if ( _slot != null ) {
           String _question = _slot.getCoveredText();
           if ( _question != null && _question.indexOf('?') > -1 ) {
             Annotation _value = ((SlotValue)slotValue).getDependentContent();
             if ( _value != null ) {
               if ( _value.getBegin() <= potentialSymptom.getBegin() && _value.getEnd() >= potentialSymptom.getEnd())
                 returnVal = true;
             }
           }
         }
             
       }
       
       
     }
  
      return returnVal;
    } // End Method isHeading() ======================
    

    


    // =======================================================
    /**
     * isInValue returns true if this symptom appears in the value part of
     * a slot value (the dependent content)
     * 
     * @param pJCas
     * @param potentialSymptom
     * @return boolean
     */
    // =======================================================
    private boolean isInValue(JCas pJCas, Annotation potentialSymptom) {
      boolean returnVal = false;
      
      List<Annotation>  theSlotValue = UIMAUtil.getEnclosingAnnotation(pJCas, potentialSymptom, DependentContent.typeIndexID);
      
      if ( theSlotValue != null && theSlotValue.size() > 0 ) 
        returnVal = true;
          
      
      return returnVal;
    } // End Method isInValue() ======================
    



    // =======================================================
    /**
     * isInSlot returns true if this symptom appears in the slot part of a slot value structure
     * 
     * @param pJCas
     * @param potentialSymptom
     * @return boolean
     */
    // =======================================================
    private boolean isInSlot(JCas pJCas, Annotation potentialSymptom) {
      
      boolean returnVal = false;
      List<Annotation>  slotValues = UIMAUtil.getEnclosingAnnotation(pJCas,potentialSymptom, gov.va.chir.model.SlotValue.typeIndexID );
      
      if ( slotValues != null && slotValues.size() > 0 ) {
        Annotation firstSlotValue = slotValues.get(0);
        Annotation contentHeading = ((SlotValue)firstSlotValue).getContentHeader();
        if ( contentHeading != null ) 
          if ( contentHeading.getBegin() <= potentialSymptom.getBegin() )
           if ( contentHeading.getEnd() >= potentialSymptom.getEnd() ) 
             returnVal = true;
           
        
          returnVal = true;
      }
        
          
      
      return returnVal;
    } // End Method isInSlot() ======================
    



    // =======================================================
    /**
     * isLineIndented returns true if the line that contains the potential symptom
     * contains leading whitespace.
     * 
     * @param pJCas
     * @param potentialSymptom
     * @return boolean
     */
    // =======================================================
    private boolean isLineIndented(JCas pJCas, int[] endOfLineOffsets,  Annotation potentialSymptom) {
      
      boolean returnVal = false;
      
      // ------ get the row that contains this symptom
      String pLine = UIMAUtil.getLine(pJCas, potentialSymptom, endOfLineOffsets);
      
      // ---------------------------
      // does the line start with \s
      if ( pLine != null && pLine.length() > 0 ) {
        if ( Character.isWhitespace(pLine.charAt(0))) {
          returnVal = true;
        }
      }
        
      return returnVal;
    }  // End Method isLineIndented() ======================
    
    
 // =======================================================
   /**
    * badContext looks at the tokens before and after 
    * to look for patterns that are not symptoms.  For example
    *  screen for <symptom>, or PRN <symptom>, or 
    *    
    *    <symptom> prevention
    *    <symptom> preventive care
    *    <symptom> management 
    * 
    * @param pJCas
    * @param term
    * @return
    */
   // =======================================================
     boolean badContext(JCas pJCas, Annotation term) {
     
     boolean returnVal = false;
     
     String[] badSymptomXXX = {"prevention", "preventive care", "management", "scale", "test", "assessment"};
     String[] xxxBadSymptom = {"screen for", 
                               "screened for", 
                               "negative screen for",
                               "range for",
                               "risk for", 
                               "labs significant for",
                               "history for", 
                               "no data available for",
                               "left message for",
                               "no plans for",
                               "evaluated for", 
                               "contact provider for", 
                               "required for", 
                               "will be reffered for",
                               "perscribed",
                               "prn", 
                               "bedtime for",
                               "day for",
                               "as needed for",
                               "bid for",
                               "morning for",
                               "daily for",
                               "qhs for",
                               "tongue for",
                               "wks for",
                               "months for",
                               "continues to",
                               "referral to",
                               "referred to",
                               "referral to stop",
                               "have you every had any",
                               "have you every had any experience that was so frightening, horrible, or upsetting",
                               "monitor for signs or symptoms of"
     
                              };
     String concordanceLine = concordanceLine(pJCas, term, 20);
     String buff[] = U.split(concordanceLine);
     
     for ( String pattern : badSymptomXXX) {
       try {
         if ( buff.length > 1 &&  buff[1] != null && buff[1].length() > 0 &&  buff[1].toLowerCase().startsWith(pattern)) {
           returnVal = true;
           break;
         }
       } catch (Exception e) {
         e.printStackTrace();
         System.err.println("issue with looking at the pattern " + e.toString());
         throw new RuntimeException();
       }
     }
     
     if ( !returnVal ) {
       for ( String pattern : xxxBadSymptom ) {
         if (buff[0] != null && buff[0].length() > 0 &&  buff[0].toLowerCase().endsWith(pattern)) {
           returnVal = true;
           break;
         }
       }
     }
     return returnVal;
   } // End Method badContext() ======================
   


   //-----------------------------------------
     /**
      * concordanceLine formats the context around an annotation
      * into the following format   msg|token3 token2 token1 |annotation| token1 token2 token3
      * 
      * @param pJCas
      * @param pAnnotation
      * @param pCharWindow (20 chars)
      * @return String
      */
     // -----------------------------------------
     private String concordanceLine(JCas pJCas,  Annotation pAnnotation , int pCharWindow) {
                                     
       String retValue = null;
       
       // ---------------------------------------
       // get the line that this annotation is on
       String fileName = VUIMAUtil.getDocumentId(pJCas);
       fileName = fileName.substring(fileName.lastIndexOf('/') + 1 );
       int begin = pAnnotation.getBegin();
       int end = pAnnotation.getEnd();
       int beginL = begin - pCharWindow;
       if (beginL < 0 ) beginL = 0;
       int endR = end + pCharWindow;
       String docText = pJCas.getDocumentText();
       int docTextSize = docText.length();
       if ( endR > docTextSize ) endR = docTextSize;
       List<Annotation> lTokens = UIMAUtil.getAnnotationsBySpan(pJCas, gov.va.chir.model.WordToken.typeIndexID, beginL, begin);
       List<Annotation> rTokens = UIMAUtil.getAnnotationsBySpan(pJCas, gov.va.chir.model.WordToken.typeIndexID, end, endR);
       String lfs = getTextFrom( lTokens);
       String rfs = getTextFrom( rTokens);
       if ( lTokens == null && rTokens == null ) {
         lfs = U.display(docText.substring(beginL, begin));
         rfs = U.display(docText.substring(end, endR));
       } else {
          lfs = getTextFrom( lTokens);
          rfs = getTextFrom( rTokens);
      
       }
       
       retValue = lfs + "|" + rfs ;
       return retValue;
       
     } // end Method concordanceLine;


   //-----------------------------------------
   /**
   * getTextFrom retrieves the text from the spans of the annotations passed in
   *  (it is presumed that this list of annotations is in span order and not overlapping.)
   * 
   * Newlines are taken out.
   * 
   * @param tokens
   * @return String
   */
   //-----------------------------------------
   private String getTextFrom(List<Annotation> tokens) {

   String returnVal = null;

   if ( tokens != null ) {
     StringBuffer buff = new StringBuffer();
     for ( Annotation annotation : tokens ) {
         buff.append( U.display(annotation.getCoveredText()));
         buff.append(" ");
     } // end loop thru tokens
     returnVal = buff.toString().trim();
   } // end if the annotations are filled out

   return returnVal;
   } // end method getTextFrom() --------------



    // =======================================================
    /**
     * getTokensAroundSymptomLines returns tokens within a 3 line window of the symptom
     * 
     * @param pJCas
     * @param potentialSymptom
     * @param lineEndOffsets
     * @param pBeginAndEnds
     * @return Annotation[]
     */
    // =======================================================
    private Annotation[] getTokensAroundSymptomLines(JCas pJCas, Annotation potentialSymptom, int[] lineEndOffsets, int[] pBeginAndEnds) {
      
      ArrayList<Annotation>someTokens = new ArrayList<Annotation>();
      Annotation[] returnVal = null;
      String docText = pJCas.getDocumentText();
      
      // ---------------
      // Get the offset of the beginning of the line that has this symptom
      int lineNumber = UIMAUtil.getLineNumber(potentialSymptom, lineEndOffsets);
      int beginLineOffset = 0;
      if ( lineNumber > 1 )
        beginLineOffset = lineEndOffsets[lineNumber-2] + 1;
      
      int endLineOffset = docText.length() -1;
      if (lineEndOffsets != null && lineEndOffsets.length > 0 )
        endLineOffset = lineEndOffsets[lineEndOffsets.length -1];
     
      if ( lineEndOffsets != null &&  lineNumber + 1 < lineEndOffsets.length)
        endLineOffset = lineEndOffsets[lineNumber+1];
      
      if ( beginLineOffset == endLineOffset ) {
        beginLineOffset = potentialSymptom.getBegin() - 40;
        endLineOffset = potentialSymptom.getEnd() + 40;
      }
      if ( beginLineOffset < 0) beginLineOffset = 0;
      if (endLineOffset > docText.length() -1 ) endLineOffset = docText.length() -1;
 
      
      // -------------
      // Get the tokens from the beginLine offsets to where the potential Symptom starts
      List<Annotation> tokens = UIMAUtil.getAnnotationsBySpan(pJCas, gov.va.chir.model.Token.typeIndexID, beginLineOffset, endLineOffset);
      tokens = (ArrayList<Annotation>) UIMAUtil.uniqueAnnotations(tokens);
      // ------------
      // Weed out the whitespace tokens
      int beginCursur = 0;
      int endCursur = 0;
      int currentCounter = 0;
      if ( tokens != null && tokens.size() > 0 ) {
        for ( Annotation aToken : tokens ) {
          if ( aToken.getCoveredText() != null &&
               aToken.getCoveredText().trim() != null &&
               aToken.getCoveredText().trim().length() > 0 ) {
           someTokens.add( aToken );
           if ( aToken.getBegin() == potentialSymptom.getBegin())
             beginCursur = currentCounter;
           if ( aToken.getEnd() == potentialSymptom.getEnd() ) 
             endCursur = currentCounter;
           currentCounter++;
          }
        }
      } else {
        System.err.println("Something went wrong here - should be at least one token");
        System.err.println("The begin offset = " + beginLineOffset );
        System.err.println("The end line offset = " + endLineOffset);
        
      }
      if ( someTokens!= null && someTokens.size() > 0) {
    	 
        returnVal = someTokens.toArray(new Annotation[someTokens.size()]);
        pBeginAndEnds[0] = beginCursur;
        pBeginAndEnds[1] = endCursur;
      }
   
      return returnVal;
    } // End Method getWordsAroundSymptomLines() ======================
    

    // =======================================================
    /**
     * getLeftSideWords returns the punctuation and word tokens to the left
     * of line that contains this symptom  
     * 
     * @param pJCas
     * @param pPotentialSymptom
     * @param pThreeLinesOfTokens
     * @param pWindowSize
     * @param pPotentialSymptomTokenBeginAndEnd
     * @return String[]
     */
    // =======================================================
    private String[] getLeftSideWords(JCas pJCas, Annotation potentialSymptom, Annotation[] pThreeLinesOfTokens, int pWindowSize, int[] pPotentialSymptomTokenBeginAndEnd) {
      
      String[] words = new String[ pWindowSize];
     int currentCurser = pPotentialSymptomTokenBeginAndEnd[0] -1;
    
      for ( int i = pWindowSize -1; i >= 0 ; i--  )  {
        if ( currentCurser > -1  ) {
          if ( pThreeLinesOfTokens!= null && 
        		  pThreeLinesOfTokens[currentCurser] != null && 
        		  pThreeLinesOfTokens[currentCurser].getCoveredText() != null  )
            words[i] = arffNormalize(pThreeLinesOfTokens[currentCurser].getCoveredText());
          else
            words[i] = "unknown";
        } else {
          words[i] = "unknown";
        }
        currentCurser--;
      
      }
   
      return words;
    } // End Method getLeftSideWords() ======================
    
    // =======================================================
    /**
     * getWordsAr returns the punctuation and word tokens to the left
     * of line that contains this symptom  (not uniqued)
     * 
     * @param pJCas
     * @param pPotentialSymptom
     * @param pThreeLinesOfTokens
     * @param pWindowSize
     * @param pPotentialSymptomTokenBeginAndEnd
     * @return String[]
     */
    // =======================================================
    private String[] getRightSideWords(JCas pJCas, Annotation potentialSymptom, Annotation[] pThreeLinesOfTokens, int pWindowSize, int[] pPotentialSymptomTokenBeginAndEnd) {
      
      String[] words = new String[ pWindowSize];
     int currentCurser = pPotentialSymptomTokenBeginAndEnd[1] +1;
    
      for ( int i = 0; i < pWindowSize; i++  )  {
        if ( pThreeLinesOfTokens != null && currentCurser < pThreeLinesOfTokens.length  ) {
          words[i] = arffNormalize(pThreeLinesOfTokens[currentCurser].getCoveredText());
        } else {
          words[i] = "unknown";
        }
        currentCurser++;
       
      }
   
      return words;
    } // End Method getRightSideWords() ======================
    

 // =======================================================
    /**
     * getLeftSidePOSs returns pos's to the left
     * of the symptom  
     * 
     * @param pJCas
     * @param pPotentialSymptom
     * @param pThreeLinesOfTokens
     * @param pWindowSize
     * @param pPotentialSymptomTokenBeginAndEnd
     * @return String[]
     */
    // =======================================================
    private String[] getLeftSidePOSs(JCas pJCas, Annotation potentialSymptom, Annotation[] pThreeLinesOfTokens, int pWindowSize, int[] pPotentialSymptomTokenBeginAndEnd) {
      
      String[] words = new String[ pWindowSize];
     int currentCurser = pPotentialSymptomTokenBeginAndEnd[0] -1;
    
      for ( int i = pWindowSize-1; i >= 0 ; i--  )  {
        if (  pThreeLinesOfTokens != null && currentCurser > -1  ) {
          Token aToken = (Token)pThreeLinesOfTokens[currentCurser];
          PartOfSpeech aPOS = aToken.getPartOfSpeech();
          if ( aPOS != null ) 
            words[i] = arffNormalize(aPOS.getPos());
          else 
            words[i] = "unknown";
        } else {
          words[i] = "unknown";
        }
        currentCurser--;
      
      }
   
      return words;
    } // End Method getLeftSideWords() ======================
    
    // =======================================================
    /**
     * getRightSidePOS returns the pos's of the words to the right of the symptom
     * 
     * @param pJCas
     * @param pPotentialSymptom
     * @param pThreeLinesOfTokens
     * @param pWindowSize
     * @param pPotentialSymptomTokenBeginAndEnd
     * @return String[]
     */
    // =======================================================
    private String[] getRightSidePOSs(JCas pJCas, Annotation potentialSymptom, Annotation[] pThreeLinesOfTokens, int pWindowSize, int[] pPotentialSymptomTokenBeginAndEnd) {
      
      String[] words = new String[ pWindowSize];
     int currentCurser = pPotentialSymptomTokenBeginAndEnd[1] +1;
    
      for ( int i = 0; i < pWindowSize; i++  )  {
        if ( pThreeLinesOfTokens != null &&  currentCurser < pThreeLinesOfTokens.length  ) {
          Token aToken = (Token)pThreeLinesOfTokens[currentCurser];
          PartOfSpeech aPOS = aToken.getPartOfSpeech();
          if ( aPOS != null )
            words[i] = arffNormalize( aPOS.getPos());
          else {
            words[i] = "unkown";
          
          }
        } else {
          words[i] = "unknown";
        }
        currentCurser++;
       
      }
   
      return words;
    } // End Method getRightSideWords() ======================
    


    // =======================================================
    /**
     * getSymptomPOS 
     * 
     * @param pJCas
     * @param surroundingTokens
     * @param beginsAndEnds
     * @return
     */
    // =======================================================
    private String getSymptomPOS(JCas pJCas, Annotation[] pSurroundingTokens, int[] beginsAndEnds) {
      
      String returnVal = "unknown";
      if (  pSurroundingTokens != null ) {
        Annotation aToken = pSurroundingTokens[ beginsAndEnds[1]];
 
        PartOfSpeech aPOS = ((Token) aToken).getPartOfSpeech();
        if ( aPOS != null )
          returnVal = aPOS.getPos();
      }
     
     return returnVal;
      // End Method getSymptomPOS() ======================
    }



    // =======================================================
    /**
     * getFeature given an annotation, get the closest thing to
     * this annotation within the utterance it is in
     * 
     * @param pJCas
     * @param potentialSymptom
     * @param pSentences
     * @param pTypeId
     * @return
     */
    // =======================================================
    private String getFeature(JCas pJCas, Annotation potentialSymptom, List<Annotation> pSentences, int pTypeId) {
    
      String returnVal = null;
      List<String> returnVals = null;
      // --------------------
      // Retrieve the sentence/slotValue this annotation is in
      
      List<Annotation> sentences = UIMAUtil.fuzzyFindAnnotationsBySpan(pSentences, potentialSymptom.getBegin(), potentialSymptom.getEnd());
      
      // ------------------------
      // For each sentence,
      //   Look for annotations of the type 
      if ( sentences != null ) {
        
        for ( Annotation sentence : sentences ) {
          List<Annotation> targets = UIMAUtil.getAnnotationsBySpan(pJCas, pTypeId, sentence.getBegin(), sentence.getEnd());
          
          if ( targets != null  && targets.size() > 0 ) {
            returnVals = new ArrayList<String>(targets.size());
         
            for ( Annotation target : targets) 
              returnVals.add(arffNormalize(target.getCoveredText()));
          }
    
        }
      }
      
      // ---------------------
      // if there are more than one - choose the last one
      if ( returnVals != null )
        returnVal = returnVals.get(returnVals.size() -1 );
      else
        returnVal = "unknown";
      
      return returnVal;
    } // End Method getFeature() ======================
    

    // =======================================================
    /**
     * getSectionName given an annotation, get the closest thing to
     * this annotation within the utterance it is in
     * 
     * @param pJCas
     * @param potentialSymptom
     * @param pSentences
     * @param pTypeId
     * @return
     */
    // =======================================================
    public String getSectionName(JCas pJCas, Annotation potentialSymptom, List<Annotation> pSections ) {
    
      String returnVal = null;
    
      // --------------------
      // Retrieve the sentence/slotValue this annotation is in
      
      List<Annotation> sections = UIMAUtil.fuzzyFindAnnotationsBySpan(pSections, potentialSymptom.getBegin(), potentialSymptom.getEnd());
      
      // ------------------------
      // For each sentence,
      //   Look for annotations of the type 
      // ---------------------
      // if there are more than one - choose the last one
      //                            - choose the one with the deepest nesting that has a section name
      if ( sections != null && sections.size() > 0  ) {
        int max = -1;
        for ( Annotation section : sections ) {
         
          int sectionNesting =  0;
          String sectionId = null;
          try { 
            sectionNesting = Integer.parseInt(UIMAUtil.stringArrayToString(((Section)section).getOtherFeatures()).trim());
            sectionId = ((VAnnotation) section).getId();
          } catch (Exception e) {
            sectionNesting = 0;
          }
          if ( ((Section) section).getSectionName() != null  && sectionNesting >= max && sectionId != null && sectionId.contains("ObsecAn")) {
            returnVal = ((Section) section).getSectionName().toLowerCase().trim();
            max = sectionNesting;
       
          } // end if this is the deepest section
        } // end loop thru sections
      } // end if there are sections
      
      if ( returnVal == null || returnVal.trim().length() == 0 )
        returnVal = "unknown";
   
      return returnVal;
    } // End Method getSectionName() ======================
    

 // =======================================================
    /**
     * getSectionName given an annotation, get the closest thing to
     * this annotation within the utterance it is in
     * 
     * @param pJCas
     * @param potentialSymptom
     * @param pCEMSections
     * 
     * @return String
     */
    // =======================================================
    public String getCEMSectionName(JCas pJCas, Annotation potentialSymptom, List<Annotation> pCEMSections ) {
    
      String returnVal = null;
    
      // --------------------
      // Retrieve the sentence/slotValue this annotation is in
      
      List<Annotation> sections = UIMAUtil.fuzzyFindAnnotationsBySpan(pCEMSections, potentialSymptom.getBegin(), potentialSymptom.getEnd());
      
      // ------------------------
      // For each sentence,
      //   Look for annotations of the type 
      // ---------------------
      // if there are more than one - choose the last one
      //                            - choose the one with the deepest nesting that has a section name
      if ( sections != null && sections.size() > 0  ) {
        int max = -1;
        for ( Annotation section : sections ) {
         
          int sectionNesting =  0;
         
          try { 
            String nesting = ((CEM)section).getNesting();
            if ( nesting != null && nesting.trim().length() > 0 )
              sectionNesting = Integer.parseInt(nesting);
          } catch (Exception e) {
            sectionNesting = 0;
          }
          if ( ((CEM) section).getName() != null  && sectionNesting >= max ) {
            returnVal = ((CEM) section).getName().toLowerCase().trim();
            max = sectionNesting;
       
          } // end if this is the deepest section
        } // end loop thru sections
      } // end if there are sections
      
      if ( returnVal == null || returnVal.trim().length() == 0 )
        returnVal = "unknown";
   
      return returnVal;
    } // End Method getCEMSectionName() ======================
    




    // =======================================================
    /**
     * getDocumentType retrieves a normalized form of the documentType 
     * 
     * @param pJCas
     * @return String
     */
    // =======================================================
    private String getDocumentType(JCas pJCas) {
     
      String returnVal = null;
      List<String> returnVals = null;
      List<Annotation>documentHeaders = UIMAUtil.getAnnotations(pJCas, DocumentHeader.typeIndexID);
     
     
     if ( documentHeaders != null && documentHeaders.size() > 0) {
       returnVals = new ArrayList<String>(1);
       for ( Annotation documentHeader : documentHeaders) {
         returnVals.add(((DocumentHeader)documentHeader).getDocumentType());
       }
       
     }
     // ---------------------
     // if there are more than one - choose the last one
     if ( returnVals != null )
       returnVal = returnVals.get(returnVals.size() -1 );
     if ( returnVal == null ) returnVal = "unknown";
     else
       returnVal = "unknown";
     
     returnVal = arffNormalize(returnVal.toLowerCase());
     
     
     
     return returnVal;
    } // End Method getDocumentType() ======================
    



    // =======================================================
    /**
     * getAssertionStatus returns a string that is either Negated|Asserted|hypothetical|subject ...
     * 
     * @param potentialSymptom
     * @return
     */
    // =======================================================
    private String getAssertionStatus(JCas pJCas, Annotation potentialSymptom) {
     
      String returnVal = VUIMAUtil.getAssertionStatus(pJCas, potentialSymptom);
     
      return returnVal;
    }  // End Method getAssertionStatus() ======================
    


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
      this.localResources = U.getOption(args, "--localResources=", "./bla");
      String    threshold = U.getOption(args, "--wordFreqThreshold=", "10"); 
      this.classifier     = U.getOption(args, "--classifier=", "NaiveBayes");
      
     initialize( outputDir, threshold);
     
     this.ctr = 0;
    }
   
    
    //----------------------------------
      /**
       * initialize loads the machine learned component
       * 
       * @param pOutputDir
       * @param pThreshold 
       * 
       **/
      // ----------------------------------
      public void initialize( String pOutputDir, String pThreshhold ) throws ResourceInitializationException {
     
        
        this.minThreshold = Integer.parseInt( pThreshhold);
        File aDir = new File( pOutputDir + "/svm");
        aDir.mkdirs();
        System.err.println("The dir = " + aDir.getAbsolutePath());
        this.svmOutputDir = pOutputDir  + "/svm";  // needs to be passed in and put into outputDir!
       
      
      
        
    } // end Method initialize() --------------
  
   
  
    // ----------------------------------------
    // Global variables
    // ----------------------------------------
    protected ArrayList<SVMContext> contexts = new ArrayList<SVMContext>();
    protected int minThreshold = 1;
    private String svmOutputDir = null;
    private String outputDir = null;
    private String localResources = null;
    private String classifier = null;
    private int    ctr = 0;

    
    private ArrayList<String> actvityModifiterTable = new ArrayList<String>(35000);
    private int[] symptomCategories = new int[15];
    private int negativeExamples = 0;
    private int positiveExamples = 0;
  
    private static final int question = 0;
    private static final int answer = 1;
    private static final int slot = 2;
    private static final int value = 3;
    private static final int heading = 4;
    private static final int listElement = 5;
    private static final int sentence = 6;
    private static final int other = 7;
    private int[] pLocation = new int[8];
    private int[] location = new int[8];
    private static int CONTEXT_ID = 1;

} // end Class ToGate
