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
 * SymptomNoRedAnnotator is an annotator that 
 * determines transforms asserted potential symptoms
 * into symptoms.
 *
 * @author  Guy Divita 
 * @created June 21, 2013
 *
 * *  
 * 

 */
// ================================================

package gov.nih.cc.rmd.nlp.framework.annotator;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.vinci.model.Copper;
import gov.va.vinci.model.OrganSystem;
import gov.va.vinci.model.Symptom;
import gov.va.vinci.model.Symptom_Potential;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.MachineLearningContext;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

// import regxgen.classifier.SVMClassifier;

//import com.sun.org.apache.xml.internal.serializer.utils.Utils;
public class SymptomNoREDAnnotator extends JCasAnnotator_ImplBase {

 
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
      
      // ----------------------------
      // gather instances to classify
      // ----------------------------
    
      List<Annotation> potentialSymptoms = UIMAUtil.getAnnotations(pJCas, Symptom_Potential.typeIndexID);
       
      if ( potentialSymptoms != null ) {
        for ( Annotation potentialSymptom : potentialSymptoms ) {
        	
          // -----------------------------------------
          // Only create symptoms of asserted symptoms
          String negationStatus = ((Symptom_Potential) potentialSymptom).getAssertionStatus();
          boolean conditional     = ((Symptom_Potential) potentialSymptom).getConditionalStatus();
          String experiencer      = ((Symptom_Potential) potentialSymptom).getSubjectStatus();
          if ((negationStatus == null) || ( ( !(negationStatus.equals("Negated")) && !(negationStatus.equals("Potental")) ) &&  
              (!conditional) && 
              experiencer.equalsIgnoreCase("patient")  )) {
          
             Symptom finalSymptom =  createFinalSymptom( pJCas, potentialSymptom); //<------ only when not using the machine learned part!
             createCopper( pJCas, finalSymptom);
      
     
         }
        
          
          } // end if the symptom was asserted 
        
        } // end loop through list of annotations
      

     
  
      
    } // end Method process
    
    
    
    // =======================================================
    /**
     * filterOutNonAssertedOrganSystemAnnotations removes organ system annotations
     * that did not make it as final symptoms
     * 
     * @param pJCas
     * @param finalSymptoms
     */
    // =======================================================
    private void filterOutNonAssertedOrganSystemAnnotations(JCas pJCas, ArrayList<Symptom> finalSymptoms) {


      // ----------------------
      // unmark all organ systems
      // -----------------------
      Type organSystemType = pJCas.getTypeSystem().getType( OrganSystem.class.getName());
      List<Annotation> organSystemAnnotations = UIMAUtil.getAnnotations(pJCas, organSystemType, true);
      unmarkAnnotations( organSystemAnnotations );
      
     // -----------------------
      // Loop thru each final symptom 
      if ( finalSymptoms != null ) {
      
        for ( Symptom symptom : finalSymptoms ) {
          
          // ----------------------------
          // Find all organ system annotations that match this symptom
          // mark each organ system
          markOrganSystemsForSymptom( pJCas, symptom);
          
        } // end loop through each symptom
        
  
      } // end if there are any final symptoms
  
      // --------------------------
      // loop through the organ systems again, 
      // those that are not marked, delete
      if ( organSystemAnnotations != null ) {
        for ( Annotation organSystem: organSystemAnnotations ) {
          if ( ((OrganSystem) organSystem).getOtherInfo() == null ) {   
            organSystem.removeFromIndexes();
          }
        }
      }
        
      
      
    }  // End Method filterOutNonAssertedOrganSystemAnnotations() ======================
    



    // =======================================================
    /**
     * markOrganSystemsForSymptom Finds all organ systems that match
     * the span of the symptom, and marks them.
     * 
     * @param pJCas
     * @param symptom
     */
    // =======================================================
    private void markOrganSystemsForSymptom(JCas pJCas, Symptom pSymptom) {
     
      if (pSymptom != null ) {
        
        List<Annotation> allConcepts = UIMAUtil.getAnnotationsBySpan(pJCas, pSymptom.getBegin(), pSymptom.getEnd());
        
        if ( allConcepts != null ) {
          for ( Annotation concept : allConcepts ) {
            
            if ( this.organSymptomCategoryz.contains( concept.getClass().getSimpleName() ) ) {
             ((OrganSystem)concept).setOtherInfo("Marked");
            } // end if this is of the right type
            
          } // end loop though allConcepts
        } // end if there are any concpets for this span
        
        
      } // end if there is a symptom
      
      // End Method markOrganSystemsForSymptom() ======================
    }



    // =======================================================
    /**
     * unmarkAnnotations clears out the "otherInfo" field for 
     * the annotations. 
     * 
     * @param pOrganSystemAnnotations
     */
    // =======================================================
    private void unmarkAnnotations(List<Annotation> pOrganSystemAnnotations) {
    
        if ( pOrganSystemAnnotations != null ) {
          
          for ( Annotation organSystem : pOrganSystemAnnotations ) {
            ((OrganSystem) organSystem).setOtherInfo((String)null); 
            
          } // end loop through organ Systems
        }
   
    } // End Method unmarkAnnotations() ======================
    



    // =======================================================
    /**
     * getSmallContext returns the sentence/phrase that this annotation
     * is within
     * 
     * @param pJCas
     * @param pAnnotation
     * @return String
     */
    // =======================================================
    private String getSmallContext(JCas pJCas, Annotation pAnnotation, List<Annotation>pTokens ) {
     
      String returnVal = null;
      
      List<Annotation> sentences = UIMAUtil.getEnclosingAnnotation(pJCas, pAnnotation, Sentence.typeIndexID);
      
      if ( sentences != null ) {
        
        returnVal = display(pJCas, sentences);
      } else {
        
        // -------------------------------------
        // if this is not within a sentence, it could be within a slot/value structure or a content heading of a section
        List<Annotation> slotValues = UIMAUtil.getEnclosingAnnotation( pJCas, pAnnotation, SlotValue.typeIndexID);
        
        if ( slotValues != null ) {
          returnVal = display(pJCas,slotValues);
        }
        else {
          List<Annotation> headings = UIMAUtil.getEnclosingAnnotation( pJCas, pAnnotation, ContentHeading.typeIndexID);
          if ( headings != null ) {
            returnVal = display(pJCas, headings);
          } else {
            
            // --------------------------------------------------
            // None of the above stratigies worked, take the span context 
            returnVal = this.ml.getContextFormat(pJCas, pAnnotation, pTokens );
            
          } // end if have to get the full context
          
        } // end if have to get teh content heading context
          
          
        } // end if a slotValue context
    
      
      
      return returnVal;
    } // End Method getSmallContext() ======================
    


    // =======================================================
    /**
     * display displays the contents of the spans of a list of annotations
     * replacing non-printing chars, and putting it all on one line
     *
     * @param pJCas 
     * @param pAnnotations
     * 
     * @return String
     */
    // =======================================================
    private String display(JCas pJCas, List<Annotation> pAnnotations) {
    
      String returnVal = null;
      int minBegin = 999999999;
      int maxEnd  = 0;
      int begin = 0;
      int eend = 0;
      
      if ( pAnnotations != null ) {
        for ( Annotation  annotation: pAnnotations ) {
          begin = annotation.getBegin();
          if ( begin < minBegin )
            minBegin = begin;
          eend = annotation.getEnd();
          if ( eend > maxEnd )
            maxEnd = eend;
          
        } // end loop through anotations
        
       // -------------------------
        // get the content of the span minBegin to maxEnd
        returnVal = pJCas.getDocumentText().substring( minBegin, maxEnd);
        returnVal = U.display2(returnVal);
              
      } // end if there are any annotations to display
      
 
      return returnVal;
    }  // End Method display() ======================
    



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



    // ----------------------------------------------
    /**
     * createFinalSymptom
     * 
     * @param pJCas
     * @param annotation
     */
    // ----------------------------------------------
    private Copper createCopper(JCas pJCas, Annotation annotation) {
    
      Copper statement = new Copper(pJCas);
      
      statement.setBegin(        annotation.getBegin());
      statement.setEnd(          annotation.getEnd());
    statement.addToIndexes();
        
      return statement;     
      
    }  // end Method classifyAnnotation() ---------------------



    


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
      
     
     
      if (aContext != null )
        super.initialize(aContext);
     
      initialize();
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
      public void initialize() throws ResourceInitializationException {
        
        
        
     
      organSymptomCategoryz = new HashSet<String> ();
      for ( String category : PotentialSymptomAnnotator.symptomCategories )  organSymptomCategoryz.add( category); 
      
     
    
    } // end Method initialize() --------------
  
    

   
    // ----------------------------------------
    // Global variables
    // ----------------------------------------
  
  
    private HashSet<String> organSymptomCategoryz = null;
    // -----------------------------------------
    /** 
     * process iterates through all annotations and converts them
     * to gate annotations then puts them out to a gate formatted
     * persistent store.
     *
     * @param pAJCas
     * @throws AnalysisEngineProcessException
     */
    // -----------------------------------------

    private MachineLearningContext ml;

} // end Class ToGate
