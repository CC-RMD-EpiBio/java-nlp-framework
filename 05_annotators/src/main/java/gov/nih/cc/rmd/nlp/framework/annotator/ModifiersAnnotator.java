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
// =================================================
/**
 * ModifiersAnnotator labels modifiers in text
 *
 *
 * @author  Guy Divita 
 * @created Dec 6, 2014
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
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class ModifiersAnnotator extends JCasAnnotator_ImplBase {
 
  
  public static final String annotatorName = ModifiersAnnotator.class.getSimpleName();
  
  
  
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
    
    this.performanceMeter.startCounter();
    List<Annotation> allTerms = UIMAUtil.getAnnotations(pJCas, gov.va.chir.model.LexicalElement.type);
    

      if (allTerms != null) 
        processTerms( pJCas, allTerms );
       
      this.performanceMeter.stopCounter();      
    
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
           
           // ---------------------------
           // Retrieve section and contentHeading annotations
           // ---------------------------
           List<Annotation> sectionAndContentHeadingAnnotations = VUIMAUtil.getSectionAndContentHeadingAnnotations(pJCas);
          
           
           
           if (terms != null) {
             // -----------------------------------------------------
             // Loop through each term
             // -----------------------------------------------------
             for (Annotation term : terms ) {



               if ((semanticTypes = ((LexicalElement) term).getSemanticTypes()) != null) {
                 if (semanticTypes.contains("modifier")) {
                   // ------------------------------------------------------
                   // if the lexical element is not part of a contentHeading of a section
                   //    create a modifier for it
                   // ------------------------------------------------------
                   if ( !isPartOfSectionHeading( pJCas, sectionAndContentHeadingAnnotations, term ) ) { 
                     try {
                       createAnnotation(pJCas, Class.forName("gov.va.vinci.model.Modifier"), (LexicalElement) term);
                     } catch (ClassNotFoundException e) {
                       e.printStackTrace();
                     }
                   }

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
             } catch (Exception e) {}
             if ( whereAmI != null && whereAmI.length() > 20) whereAmI = whereAmI.substring(0, 20);
            
             
             if ( typeId == ContentHeading.typeIndexID )
               contentHeadingSeen = true;
             if ( typeId == Section.typeIndexID)
               sectionSeen = true;
            
            // System.err.println("where am i = " + whereAmI + "|" + typeId + "|" + ContentHeading.typeIndexID + "|" + 
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
             
             
         
             String provenence = "modifiersAnnotator" ;
             //StringArray provenence = UIMAUtil.string2StringArray(pJCas, provenences);
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
             statement.setCodeSystemName("modifiersLexicon");
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
  * initialize loads in the resources needed for phrase chunking. 
  * 
  * @param aContext
  * 
  **/
 // ----------------------------------
 public void initialize(UimaContext aContext) throws ResourceInitializationException {
   
  String[] args = null;
   try {
     args                 = (String[]) aContext.getConfigParameterValue("args");  
   
    
     this.performanceMeter = new ProfilePerformanceMeter( args, this.getClass().getSimpleName() );
     
    
   } catch (Exception e ) {
     
   }
   
 
   
 } // end initialize()

 // ---------------------------
 // Global Variables
 // ---------------------------
  private int annotationCounter = 0;  
  ProfilePerformanceMeter              performanceMeter = null;
 


  
} // end Class ModifiersAnnotator() ---------------
