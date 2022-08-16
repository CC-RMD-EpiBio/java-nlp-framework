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
 * ActivitiesAnnotator labels activities derived from the UMLS activities
 * semantic types.
 *
 *
 * @author  Guy Divita 
 * @created Jun 22, 2014
 *
 **   *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.CodedEntry;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Section;
import gov.va.chir.model.VAnnotation;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class ActivitiesAnnotator extends JCasAnnotator_ImplBase {
 
  public static final String annotatorName = ActivitiesAnnotator.class.getSimpleName();
  
 
    
    // -----------------------------------------
    /**
     * process overrides the JCasAnnotator_ImplBase.  
     * This version labels lines of the document.
     * 
     * 
     */
    // -----------------------------------------
    public void process(JCas pJCas) throws AnalysisEngineProcessException {
      
      try {
      this.performanceMeter.startCounter();
      
      List<Annotation> allTerms = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.LexicalElement.type);
      

      if (allTerms != null) 
	processTerms( pJCas, allTerms );
         
      this.performanceMeter.stopCounter();
      
      } catch (Exception e ) {
        e.printStackTrace();
        System.err.println("Issue with Activities annotator " + e.toString());
      }
      
    } // end Method process() ----------------
     
    

         // =======================================================
           /**
            * processTerms.  If the terms are cauti terms, creates cauti Concepts
            * from them.
            * 
            * @param pJCas
            * @param terms
            */
           // =======================================================
           private void processTerms(JCas pJCas, List<Annotation> terms) {
             
             String semanticTypes = null;
             if (terms != null) {
               
               // ---------------------------
               // Retrieve section and contentHeading annotations
               // ---------------------------
               List<Annotation> sectionAndContentHeadingAnnotations = VUIMAUtil.getSectionAndContentHeadingAnnotations(pJCas);
              
               
               // -----------------------------------------------------
               // Loop through each term
               // -----------------------------------------------------
               for (Annotation term : terms ) {



                 if ((semanticTypes = ((LexicalElement) term).getSemanticTypes()) != null) {
                   if (semanticTypes.contains("activity") ) {
                     // ------------------------------------------------------
                     // if the lexical element is not part of a contentHeading of a section
                     //    create an activity for it
                     // ------------------------------------------------------
                     if ( !isPartOfSectionHeading( pJCas, sectionAndContentHeadingAnnotations, term ) ) { 
                       try {
                         createAnnotation(pJCas, Class.forName("gov.va.vinci.model.Activity"), (LexicalElement) term);
                       } catch (ClassNotFoundException e) {
                         e.printStackTrace();
                       }
                     } // end if this is not part of a section heading
                   } // end if this term even has a valid semantic type
                 } //end if there are semantic types
               } // end loop through terms
             } // end if there are any terms
             
           }  // End Method processTerms() ======================
           

           
           // =======================================================
           /**
            * isPartOfSectionHeading looks for content Heading and
            * section to see if this term is within the content heading
            * of a known section.
            * 
            * @param pJCas
            * @param pTerm
            * @return boolean
            */
           // =======================================================
           private boolean isPartOfSectionHeading(JCas pJCas, List<Annotation> pSectionAnnotations, Annotation pTerm) {
            boolean returnVal = false;
            
            boolean contentHeadingSeen = false;
            boolean sectionSeen = false;
            List<Annotation> overlappingAnnotations = UIMAUtil.fuzzyFindAnnotationsBySpan(pSectionAnnotations, pTerm.getBegin(), pTerm.getEnd());
            
            if ( overlappingAnnotations != null && overlappingAnnotations.size() > 1) {
              
              for ( Annotation anAnnotation : overlappingAnnotations ) {
                int typeId = anAnnotation.getTypeIndexID();
                String whereAmI = null;
                try {
                 whereAmI = anAnnotation.getCoveredText();
                } catch (Exception e) {
                  
                }
                if ( whereAmI != null && whereAmI.length() > 20) whereAmI = whereAmI.substring(0, 20);
                
                if ( typeId == ContentHeading.typeIndexID )
                  contentHeadingSeen = true;
                if ( typeId == Section.typeIndexID)
                  sectionSeen = true;
               
               //  System.err.println("where am i = " + whereAmI + "|" + typeId + "|" + ContentHeading.typeIndexID + "|" + 
               // Section.typeIndexID + "|" + anAnnotation.getClass().getName());
                if ( contentHeadingSeen && sectionSeen)
                 break;
                
              } // end loop thru the overlappingAnnotations
              
              if ( contentHeadingSeen && sectionSeen)
                returnVal = true;
            } //end if there are overlapping annotations
            
            return returnVal;
                       
           } // End Method isPartOfSectionHeading() ======================
           

            
         //-----------------------------------------
           /**
            * createAnnotation creates a clinicalStatement of from the phrase passed in.
            * The phrase is set as the parent of each Evidence
            * 
            * @param pJCas
            * @param uimaLabelClass
            * @param pTerm
            * 
            * @return Evidence

            */
           // -----------------------------------------
           private void createAnnotation( JCas pJCas, Class<?> uimaLabelClass, LexicalElement pTerm ) {
           
             
             
             try {
               Constructor<?> c = uimaLabelClass.getConstructor(new Class[] { JCas.class });
              
               try {
                 CodedEntry statement = (CodedEntry) c.newInstance(pJCas);
              
             
               // VUIMAUtil.setProvenance( pJCas, statement, this.getClass().getName() );
               statement.setBegin(                    pTerm.getBegin());
               statement.setEnd(                      pTerm.getEnd());
               //statement.setProvenance(               pTerm.getProvenance());
               
               
               // --------------------------------------------------
               // There are terms in the lexicon that are negated by default
               //    
               StringArray otherFeaturez = pTerm.getOtherFeatures();
               String otherFeatures = null;
               String termNegationStatus = "Asserted";
               if ( pTerm.getNegation_Status() != null ) 
                 termNegationStatus =  pTerm.getNegation_Status();
               
               if ( otherFeaturez != null ) {
                 otherFeatures = UIMAUtil.stringArrayToString(otherFeaturez);  
                 if (otherFeatures.contains("egated") && termNegationStatus.contains("Asserted"))
                     termNegationStatus = "Negated";
               }
               
               
           
               String provenence = "ActivitiesAnnotator" ;
               statement.setProvenance(provenence);
               statement.setId( "anatomicalPartsAnnotator_" + this.annotationCounter++);
               StringArray extraStuffs = pTerm.getOtherFeatures();
               String extraStuff = null;
               if ( extraStuffs != null && extraStuffs.size() > 1)
                 extraStuff = extraStuffs.get(0);
               
               
               String codeCode = pTerm.getEuis(0);
               String conceptName = pTerm.getCitationForm();
               String semanticType = pTerm.getSemanticTypes();
               if ( extraStuff != null ) {
                 String[] cols = U.split(extraStuff);
                 codeCode = cols[0];
                 conceptName = cols[1];
               } 
                   
                 
               
               statement.setCodeCode(codeCode);
               statement.setCodeSystemName("anatomicalPartsLexicon");
               StringArray semanticTypez = new StringArray(pJCas, 1);
               semanticTypez.set(0, semanticType);
               statement.setSemanticType(semanticTypez);
               statement.setDisplayName(conceptName);
               statement.setNegation_Status( termNegationStatus );
              
               
               statement.setParent( pTerm) ;
               statement.addToIndexes(pJCas);

             } catch (Exception e) {
               
               // ----------------------
               // Try just making a VAnnotation
               try {
                 
                 String negationStatus = pTerm.getNegation_Status();
                 if ( negationStatus != null && !negationStatus.contains("asserted") && c.getName().contains("Evidence")) {
                   // don't make a concept - its negated
                 } else {
                 
                
                 VAnnotation statement = (VAnnotation) c.newInstance(pJCas);
                 
                 
                 // VUIMAUtil.setProvenance( pJCas, statement, this.getClass().getName() );
                 statement.setBegin(                    pTerm.getBegin());
                 statement.setEnd(                      pTerm.getEnd());
                 //statement.setProvenance(               pTerm.getProvenance());
                 
               
            
                 statement.setParent( pTerm) ;
                 statement.addToIndexes(pJCas);  
                 }
               } catch (Exception e2) {
                 e2.printStackTrace();
                 System.err.println("Something went wrong here " + e.toString() );
               
               }
             }
             } catch (Exception e3) {
               e3.printStackTrace();
               System.err.println("Issue trying to make a new annotation " + e3.toString());
               
             }
               
           
             
           } // end Method createAnnotation() ---
    

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
      e.printStackTrace();
      String msg = "Issue initializing " + annotatorName + " " + e.toString() ;
      GLog.println(GLog.ERROR_LEVEL, msg);
      throw new ResourceInitializationException();
    }
    
  } // end Method initialize() --------
  
  //----------------------------------
   /**
    * initialize
    * 
    * @param pArgs
    * 
    **/
   // ----------------------------------
   public void initialize(String[] pArgs) throws ResourceInitializationException {
     
    try {
      
       this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
       
          
     } catch (Exception e ) {
       e.printStackTrace();
       String msg = "Issue initializing " + annotatorName + " " + e.toString() ;
       GLog.println(GLog.ERROR_LEVEL, msg);
       throw new ResourceInitializationException();
     }
          
   } // end initialize() --------

   // ---------------------------
   // Global Variables
   // ---------------------------
   
 
   private int annotationCounter = 0;  
   private ProfilePerformanceMeter              performanceMeter = null;
 


  
  
} // end Class ExampleAnnotator() ---------------
