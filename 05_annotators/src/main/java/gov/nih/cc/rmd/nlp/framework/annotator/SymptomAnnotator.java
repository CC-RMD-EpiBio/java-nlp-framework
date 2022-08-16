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
 * SymptomAnnotator creates arff files for svm and runs it thru
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

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.Section;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.TrueNegative;
import gov.va.vinci.model.CEM;
import gov.va.vinci.model.Copper;
import gov.va.vinci.model.Symptom;
import gov.va.vinci.model.Symptom_Potential;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class SymptomAnnotator extends SymptomSVMTrainingWithMore {

 
  
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
      
      
      try {
      this.performanceMeter.startCounter();
      
      List<Annotation> potentialSymptoms = UIMAUtil.getAnnotations(pJCas, Symptom_Potential.typeIndexID);
      List<Annotation> sentences         = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID);
      List<Annotation> slotValues        = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID);
      List<Annotation>  sections         = UIMAUtil.getAnnotations(pJCas, Section.typeIndexID);
      List<Annotation> cemSections       = UIMAUtil.getAnnotations(pJCas, CEM.typeIndexID);
     
      
      
      String documentId = VUIMAUtil.getDocumentId(pJCas);
      documentId = makeIntStringOfIdentifer( documentId);
      
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
     
     String text = pJCas.getDocumentText();
      if ( potentialSymptoms != null && potentialSymptoms.size() > 0 ) {
        
    	  markPotentialSymptomsAsUnmarked(potentialSymptoms);
    	  
        UIMAUtil.sortByOffset(potentialSymptoms);
        for ( Annotation potentialSymptom : potentialSymptoms ) {
        	if ( !((Symptom_Potential)potentialSymptom).getMarked()) {
          String conditional     = String.valueOf(((Symptom_Potential) potentialSymptom).getConditionalStatus());
           String symptomName = U.display(potentialSymptom.getCoveredText());
       
         
              
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
                 
                //  answer = this.model.classify( svmContext, documentId, symptomName, potentialSymptom.getBegin(), potentialSymptom.getEnd());
                  // svmContext.setAnswer(answer);
               
                  // this.contexts.add( svmContext);
                  // System.err.println("The answer = " + answer );
                } catch (Exception e) {
                  e.printStackTrace();
                  System.err.println("issue with classifying " + potentialSymptom.getCoveredText()  + " " + e.toString());
           
                }
          
               
               if ( answer != null && answer.toLowerCase().contains("true")) {
                 //                activity|modifier|anatomicalPart|
                int ctr = 41;
                String involves = svmContext.getAttributeValue(U.zeroPad(ctr++,2) + "_hasActivity" ) + "|" + 
                                  svmContext.getAttributeValue(U.zeroPad(ctr++,2) + "_hasModifier" ) + "|" + 
                                  svmContext.getAttributeValue(U.zeroPad(ctr++,2) + "_hasAnatomicalPart") ;
                 
                  createFinalSymptom(pJCas, potentialSymptom, involves );
                } else {  // create a trueNegative NotSymptom annotation
                  int ctr = 41;
                  String involves = svmContext.getAttributeValue(U.zeroPad(ctr++,2) + "_hasActivity" ) + "|" + 
                                    svmContext.getAttributeValue(U.zeroPad(ctr++,2) + "_hasModifier" ) + "|" + 
                                    svmContext.getAttributeValue(U.zeroPad(ctr++,2) + "_hasAnatomicalPart") ;
                  createTrueNegativeSymptom( pJCas, potentialSymptom, involves);
                }
                
               ((Symptom_Potential)potentialSymptom).setMarked(true);
        	} // end if the potential symptom has been seen before 
         
        } // end loop through list of annotations
      } // end if there are any annotations
      
      List<Annotation> symptoms = UIMAUtil.getAnnotations(pJCas, Symptom.typeIndexID);
      UIMAUtil.uniqueAnnotations( symptoms);
      
      List<Annotation> tns = UIMAUtil.getAnnotations(pJCas, TrueNegative.typeIndexID);
      UIMAUtil.uniqueAnnotations( tns);
     
      } catch ( Exception e ) {
        e.printStackTrace();
        System.err.println("Issue in Symptom Annotator " + e.toString());
      }
      this.performanceMeter.stopCounter();
      

    } // end Method process
    




    private void markPotentialSymptomsAsUnmarked(List<Annotation> pTerms) {
		if ( pTerms != null && !pTerms.isEmpty() )
    		for ( Annotation term: pTerms)
    			((Symptom_Potential)term).setMarked(false);
	} // end Method markPotentialSymptomsAsUnmarked() ----





	// ----------------------------------------------
    /**
     * createFinalSymptom
     * 
     * @param pJCas
     * @param annotation
     */
    // ----------------------------------------------
    private Symptom createFinalSymptom(JCas pJCas, Annotation annotation, String involves) {
    
      Symptom statement = new Symptom(pJCas);
      statement.setSectionName(  ((Symptom_Potential)annotation).getSectionName() );
      statement.setBegin(        annotation.getBegin());
      statement.setEnd(          annotation.getEnd());
      statement.setCuis(         ((Symptom_Potential)annotation).getCuis());
      statement.setAssertionStatus(  ((Symptom_Potential)annotation).getAssertionStatus() );
        
      statement.setCategories(((Symptom_Potential)annotation).getCategories());
      statement.setInProse( VUIMAUtil.isInProseLazy(pJCas, annotation) );
       
      statement.setConceptNames( ((Symptom_Potential)annotation).getConceptNames()); 
      
      statement.setOtherInfo( involves );
      statement.setId(U.getUniqId());
      statement.addToIndexes();
      
      
      Copper copper = new Copper(pJCas);
      copper.setBegin(annotation.getBegin());
      copper.setEnd( annotation.getEnd());
      copper.setAssertionStatus(((Symptom_Potential)annotation).getAssertionStatus());
      copper.setCategories( statement.getCategories());
      copper.addToIndexes();
     
        
      return statement;     
      
    }  // end Method classifyAnnotation() ---------------------


    // ----------------------------------------------
    /**
     * createTrueNegativeSymptom
     * 
     * @param pJCas
     * @param annotation
     */
    // ----------------------------------------------
    private TrueNegative createTrueNegativeSymptom(JCas pJCas, Annotation annotation, String involves) {
    
      TrueNegative statement = new TrueNegative(pJCas);
    
      statement.setSectionName(  ((Symptom_Potential)annotation).getSectionName() );
      statement.setBegin(        annotation.getBegin());
      statement.setEnd(          annotation.getEnd());
      statement.setNegation_Status( "TrueNegative");
      
     
      statement.setInProse( VUIMAUtil.isInProseLazy(pJCas, annotation) );
      //statement.setCuis(         ((Symptom_Potential)annotation).getCuis());
        
     // statement.setCategories(((Symptom_Potential)annotation).getCategories());
       
      //statement.setConceptNames( ((Symptom_Potential)annotation).getConceptNames()); 
      
      //statement.setOtherInfo( involves );
      
      statement.setId(U.getUniqId());
      statement.addToIndexes();
        
      return statement;     
      
    }  // end Method classifyAnnotation() ---------------------
 

  //----------------------------------
   /**
    * destroy
   * 
    **/
   // ----------------------------------
   public void destroy() {
     this.performanceMeter.writeProfile( this.getClass().getSimpleName());
   }
   
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
      
      
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args"); 
        
        initialize( args);
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue initializing the symptom Annotator " + e.toString());
        throw e;
      } 
    
     
    } // end Method initialize() --------
    
    
    //----------------------------------
    /**
     * initialize loads the machine learned component
     * 
     *
     * @param pArg
     * 
     **/
    // ----------------------------------
    public void initialize(String[] pArgs) throws ResourceInitializationException {
   
      try {
       
         
        this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
   
        String           model = U.getOption(pArgs, "--model=",      "resources/vinciNLPFramework/symptoms/sgd_All_2016_12_12.model");
        String      arffHeader = U.getOption(pArgs, "--arffHeader=", "resources/vinciNLPFramework/symptoms/arffHeader.arff"); 
        String       outputDir = U.getOption(pArgs, "--outputDir=", "/somedir"); 
        
        
        initializeModel( arffHeader, model, outputDir);
      
     
        this.contexts = new ArrayList<SVMContext>();
      
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Issue intializing the symptomsvm run with more " + e.toString());
        throw new ResourceInitializationException();
      }
    } // End Method initialize() ---------
   
    
    //----------------------------------
      /**
       * initializeModel loads the machine learned component
       * 
       * @param pArffHeader
       * @param pModel
       * @throws Exception 
       * 
       **/
      // ----------------------------------
      public void initializeModel(  String pArffHeader, String pModel, String pOutputDir ) throws Exception {
     
        
     //   this.model = new V3NLP_WekaClassifier( pArffHeader, pModel , pOutputDir);
      
     
        
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
    // private V3NLP_WekaClassifier model = null;
   
     ProfilePerformanceMeter              performanceMeter = null;
    

} // end Class ToGate
